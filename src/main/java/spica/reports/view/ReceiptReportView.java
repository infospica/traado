/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.constant.AccountingConstant;
import spica.reports.model.FilterParameters;
import spica.reports.model.ReceiptReport;
import spica.reports.service.ReceiptReportService;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.Customer;
import spica.scm.domain.Territory;
import spica.scm.export.ExcelReports;
import spica.scm.service.AccountGroupService;
import spica.scm.service.CompanyAddressService;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sujesh, Arun
 */
@Named(value = "receiptReportView")
@ViewScoped
public class ReceiptReportView implements Serializable {

//  private Company company;
  private FilterParameters filterParameters;
//  private AccountGroup accountGroup;
  // private Account account;
  private Double billAmount;
//  private Date minDate;
//  private static XSSFFont font;
//  private static XSSFWorkbook workbook;
//  private static CellStyle styleHead;
  private Territory territory;
  private Integer option = 1;
  private Integer adjustedOption;

  private List<ReceiptReport> receiptReportList;
  private List<Integer> customerList;

  public List<ReceiptReport> getReceiptReport(MainView main) {
    try {
      if (StringUtil.isEmpty(receiptReportList)) {

        ReceiptReport tmp = new ReceiptReport();
        Double cum = 0.0;
        billAmount = 0.0;
        int status = getOption() == AccountingConstant.STATUS_PROCESSED ? getAdjustedOption() : getOption();
        receiptReportList = ReceiptReportService.selectReceiptReportList(main, getCompany().getId(), getFilterParameters(), getTerritory(), 2, null, status);
        if (!StringUtil.isEmpty(receiptReportList)) {
          for (ReceiptReport receipt : receiptReportList) {
            if (getCutomerList().isEmpty() || !getCutomerList().contains(receipt.getCustomerId())) {
              customerList.add(receipt.getCustomerId());
            }
            if (receipt.getBankStatus() == null || receipt.getBankStatus() != 4) {
              cum = (tmp.getCustomerId() == null || (tmp.getCustomerId().intValue() != receipt.getCustomerId())) ? receipt.getAmount() : (cum + receipt.getAmount());
              receipt.setCumulativeSum(cum);
              tmp = receipt;
              billAmount += receipt.getAmount();
            }
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return receiptReportList;
  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    MainView main = Jsf.getMain();
    List<AccountGroup> accountGroupLlist = new ArrayList<>();
    try {
      if (getFilterParameters().getCustomer() == null || getFilterParameters().getCustomer().getId() == 0) {
        accountGroupLlist = UserRuntimeView.instance().accountGroupAuto(filter);
      } else {
        accountGroupLlist = AccountGroupService.selectAccountGroupByCustomer(main, filterParameters.getCustomer(), filter);
      }
      accountGroupLlist.add(0, new AccountGroup(0, SystemConstants.ALL_ACCOUNT_GROUP));

    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return accountGroupLlist;
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    getFilterParameters().setAccountGroup(accountGroup);
    // setAccountGroup(accountGroup);
    // setAccount(null);
    getFilterParameters().setAccount(null);
    customerList = null;
    reset();
  }

  public List<Customer> customerAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      List<Customer> list = ReceiptReportService.selectCustomerAuto(main, getCompany().getId(), filterParameters, filter);
      list.add(0, new Customer(0, "All Customer"));
      return list;
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void customerSelectEvent(SelectEvent event) {
    Customer customer = (Customer) event.getObject();
    getFilterParameters().setCustomer(customer);
    customerList = null;
    reset();
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }

  public void reset() {
    receiptReportList = null;
  }

  public List<ReceiptReport> getReceiptReportList() {
    return receiptReportList;
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
    }
    return filterParameters;
  }

//  public AccountGroup getAccountGroup() {
//    return accountGroup;
//  }
//  
//  public void setAccountGroup(AccountGroup accountGroup) {
//    this.accountGroup = accountGroup;
//  }
//  public Account getAccount() {
//    return account;
//  }
//  
//  public void setAccount(Account account) {
//    this.account = account;
//  }
  public Double getBillAmount() {
    return billAmount;
  }

  public void setReceiptReportList(List<ReceiptReport> receiptReportList) {
    this.receiptReportList = receiptReportList;
  }

  public void excel(MainView main) throws IOException {
    try {
      ExcelReports.collectionReceipt(main, getReceiptReportList(), getTerritory(), getFilterParameters(), UserRuntimeView.instance().getCompany());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<Territory> customerTerritory(MainView main) {
    try {
      if (!StringUtil.isEmpty(customerList)) {
        return ReceiptReportService.selectTerritoryByCustomer(main, getCutomerList(), getCompany().getId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }
//  

  private List<Integer> getCutomerList() {
    if (customerList == null) {
      customerList = new ArrayList();
    }
    return customerList;
  }
//  
//  public void setCutomerList(List<Integer> cutomerList) {
//    this.customerList = cutomerList;
//  }

  public Territory getTerritory() {
    return territory;
  }

  public void setTerritory(Territory territory) {
    this.territory = territory;
  }

  public CompanyAddress getCompanyAddress(MainView main) {
    try {
      return CompanyAddressService.selectCompanyRegisteredAddress(main, getCompany());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public Integer getOption() {
    return option;
  }

  public void setOption(Integer option) {
    this.option = option;
  }

  public Integer getAdjustedOption() {
    return adjustedOption;
  }

  public void setAdjustedOption(Integer adjustedOption) {
    this.adjustedOption = adjustedOption;
  }

  public void chageChequeReceiptStatus() {
    if (getOption().intValue() == AccountingConstant.STATUS_PROCESSED) {
      if (getAdjustedOption() == null) {
        setAdjustedOption(AccountingConstant.STATUS_PROCESSED);
      }
    }
    setReceiptReportList(null);
  }

}
