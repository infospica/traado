/*
 * @(#)AccountView.java	1.0 Thu Apr 07 11:31:23 IST 2016
 *
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import spica.constant.AccountingConstant;
import spica.reports.model.CustomerOutstanding;
import spica.reports.model.FilterParameters;
import spica.reports.service.CustomerOutstandingService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.District;
import spica.scm.domain.Product;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.Territory;
import spica.scm.export.ExcelSheet;
import spica.scm.service.SalesAgentService;
import spica.scm.view.ScmLookupExtView;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;
import wawo.entity.util.UniqueCheck;

/**
 * AccountView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:23 IST 2016
 */
@Named(value = "customerOutstandingView")
@ViewScoped
public class CustomerOutstandingView implements Serializable {

  private transient List<CustomerOutstanding> customerOutstandingList;
  private AccountGroup accountGroup;
  private Account account;
  private District district;
  private SalesAgent salesAgent;
  private Double receivableAmount = 0.0;
  private Double receivedAmount = 0.0;
  private Double balance = 0.0;
  private Double billAmount = 0.0;
  //private Double cumulative = 0.0;
  private FilterParameters filterParameters;
  private Territory territory;
  private List<Territory> territoryList;
  private List<Product> selectedProductList;
//  private transient List<ChequeEntry> relatedChequeEntryList;
//  private String diffText;
//  private Double totalChequeAmount = 0.0;

  public List<CustomerOutstanding> getCustomerOutstandingList(MainView main) {
    if (StringUtil.isEmpty(customerOutstandingList) && !main.hasError()) {
      //Territory ter;
      try (UniqueCheck uc = new UniqueCheck()) {
        customerOutstandingList = new ArrayList<>();
        // List<CustomerOutstanding> tmpList = new ArrayList<>();
        // List<Integer> cid = new ArrayList<>();
        String product = null;
        filterParameters.setProductHash(null);
        if (selectedProductList != null) {
          product = "";
          int i = 1;
          for (Product list : selectedProductList) {
            product += list.getId().toString();
            if (i != selectedProductList.size()) {
              product += "#";
            }
            i++;
          }
          filterParameters.setProductHash(product);
        }
        customerOutstandingList = CustomerOutstandingService.getCustomerOutstandingList(main, filterParameters, AccountingConstant.ACC_ENTITY_CUSTOMER.getId(), getDistrict(), SystemConstants.HIDE_INVOICE_WISE, territory);
        // Integer customerId = 0;
        receivableAmount = 0.0;
        receivedAmount = 0.0;
        billAmount = 0.0;
        balance = 0.0;
        Double cumulative = 0.0;
        for (CustomerOutstanding outStanding : customerOutstandingList) {
          outStanding.setReceivedAmount((outStanding.getInvoiceamount() == null ? 0 : outStanding.getInvoiceamount()) - (outStanding.getOustandingamount() == null ? 0 : outStanding.getOustandingamount()));
          if (StringUtils.isEmpty(outStanding.getAgentName())) {
            outStanding.setAgentName(getCompany().getCompanyName());
          }
          if (AccountingConstant.VOUCHER_TYPE_RECEIPT.getId() == outStanding.getVoucherTypeId()) {
            outStanding.setReceivedAmount(outStanding.getInvoiceamount() != null ? Math.abs(outStanding.getInvoiceamount()) : null);
            outStanding.setInvoiceamount(0.0);
          }
          receivedAmount += (outStanding.getReceivedAmount() == null ? 0 : outStanding.getReceivedAmount());
          billAmount += (outStanding.getInvoiceamount() == null ? 0 : outStanding.getInvoiceamount());
          balance += (outStanding.getOustandingamount() == null ? 0 : outStanding.getOustandingamount());

          if (!uc.exist(outStanding.getCustomerId())) {
            cumulative = 0.0;
          }
          cumulative += (outStanding.getOustandingamount() == null ? 0 : outStanding.getOustandingamount());
          outStanding.setCumulativeAmount(cumulative);
        }

      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return customerOutstandingList;
  }

  public void reset() {
    setCustomerOutstandingList(null);
    setAccountGroup(null);
    setAccount(null);
    setDistrict(null);
    setTerritory(null);
    setSelectedProductList(null);
//    setRelatedChequeEntryList(null);
  }

  public void setCustomerOutstandingList(List<CustomerOutstanding> customerOutstandingList) {
    this.customerOutstandingList = customerOutstandingList;
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    setAccountGroup(accountGroup);
    setAccount(null);
    setDistrict(null);
    setTerritory(null);
    setSelectedProductList(null);
    customerOutstandingList = null;
//    relatedChequeEntryList = null;
    Jsf.update("product");
  }

  public void accountSelectEvent(SelectEvent event) {
    Account account = (Account) event.getObject();
    setAccount(account);
    customerOutstandingList = null;
  }

  public void tillDate() {
    customerOutstandingList = null;
//    relatedChequeEntryList = null;
  }

  public List<Account> accountAuto(String filter) {
    List<Account> accountList = new ArrayList<>();
    MainView main = Jsf.getMain();
    try {
      if (accountGroup != null) {
        if (accountGroup.getId() != null) {
          accountList = ScmLookupExtView.accountByAccountGroupProfileAll(main, accountGroup, filter);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return accountList;
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {

    this.account = account;
  }

  public void excel(MainView main, Integer type) {
    try {
      if (type.equals(SystemConstants.SHOW_AGE_WISE)) {
        List<CustomerOutstanding> outstandingAgewise = CustomerOutstandingService.getCustomerOutstandingList(main, filterParameters, AccountingConstant.ACC_ENTITY_CUSTOMER.getId(), getDistrict(), SystemConstants.SHOW_AGE_WISE, territory);
        ExcelSheet.createCustomerOutstandingAgeWise(main, outstandingAgewise, filterParameters, salesAgent, territory, district, selectedProductList);
      } else {
        ExcelSheet.createCustomerOutstandingReport(main, filterParameters.getCustomer(), customerOutstandingList, accountGroup, type);
      }
    } catch (Throwable ex) {
      main.rollback(ex, "error.select");
    } finally {
      main.close();
    }

  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
    }
    return filterParameters;
  }

  public void setFilterParameters(FilterParameters filterParameters) {
    this.filterParameters = filterParameters;
  }

  public List<Customer> customerAuto(String filter) {
    if (StringUtils.isEmpty(filter)) {
      filterParameters.setCustomer(null);
    }
    List<Customer> customerList = ScmLookupExtView.selectCustomerByCompany(getCompany(), filter);
    customerList.add(0, new Customer(0, SystemConstants.ALL_CUSTOMER));
    return customerList;
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }

  public void customerSelectEvent(SelectEvent event) {
    Customer customer = (Customer) event.getObject();
    setCustomerOutstandingList(null);
    filterParameters.setCustomer(customer);
    setDistrict(null);
//    setRelatedChequeEntryList(null);
    Jsf.update("chequeEntryDiv");
  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      List<AccountGroup> accountGroupLlist = null;
      if (filterParameters.getCustomer() == null || filterParameters.getCustomer().getId() == 0) {
        accountGroupLlist = UserRuntimeView.instance().accountGroupAuto(filter);
      } else {
        accountGroupLlist = CustomerOutstandingService.selectAccountGroupByCustomer(main, filterParameters.getCustomer(), filter);
      }
      accountGroupLlist.add(0, new AccountGroup(0, SystemConstants.ALL_ACCOUNT_GROUP));
      return accountGroupLlist;
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public Double getReceivableAmount() {
    return receivableAmount;
  }

  public void setReceivableAmount(Double receivableAmount) {
    this.receivableAmount = receivableAmount;
  }

  public Double getBalance() {
    return balance;
  }

  public Double getBillAmount() {
    return billAmount;
  }

  public Double getReceivedAmount() {
    return receivedAmount;
  }

  public void setReceivedAmount(Double receivedAmount) {
    this.receivedAmount = receivedAmount;
  }

  public SalesAgent getSalesAgent() {
    return salesAgent;
  }

  public void setSalesAgent(SalesAgent salesAgent) {
    this.salesAgent = salesAgent;
  }

  public void districtSelectEvent(SelectEvent event) {
    District district = (District) event.getObject();
    setDistrict(district);
    customerOutstandingList = null;
  }

  public List<District> districtAuto(String filter) {
    MainView main = Jsf.getMain();
    List<District> districtList = new ArrayList<>();
    try {
      if (getSalesAgent()!= null || getTerritory() != null) {
        districtList = CustomerOutstandingService.selectDistrict(main, getSalesAgent(), getTerritory());
      } else if (getAccountGroup() != null) {
        districtList = CustomerOutstandingService.selectDistrictByAccountGroup(main, getAccountGroup());
      } else {
        districtList = CustomerOutstandingService.selectDistrict(main);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    districtList.add(0, new District(0, SystemConstants.ALL_DISTRICT));
    return districtList;
  }

  public District getDistrict() {
    return district;
  }

  public void setDistrict(District district) {
    this.district = district;
  }

  public List<SalesAgent> salesAgentAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      if (getCompany() != null) {
        List<SalesAgent> salesAgents = SalesAgentService.salesAgentsAll(main, getCompany(), filter);
        return salesAgents;
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }


  public void salesAgentSelectEvent(SelectEvent event) {
    SalesAgent salesAgent = (SalesAgent) event.getObject();
    if (salesAgent != null) {
      if (salesAgent.getId() != null) {
        filterParameters.setSalesAgent(salesAgent);
        customerOutstandingList = null;
      }
    } else {
      filterParameters.setSalesAgent(null);
      customerOutstandingList = null;
    }
  }

  public Territory getTerritory() {
    return territory;
  }

  public void setTerritory(Territory territory) {
    this.territory = territory;
    customerOutstandingList = null;
  }

  public List<Territory> getTerritoryList() {
    MainView main = Jsf.getMain();
    try {
      if (getSalesAgent() != null) {
        territoryList = UserRuntimeService.selectTerritoryBySalesAgent(main, getSalesAgent().getId(), UserRuntimeView.instance().getCompany().getId());
      } else {
        territoryList = CustomerOutstandingService.slectTerritoryList(main, getFilterParameters().getAccountGroup(), UserRuntimeView.instance().getCompany().getId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return territoryList;
  }

  public List<Product> getProductList(MainView main) {
    try {
      if (accountGroup != null) {
        return CustomerOutstandingService.getProductList(main, accountGroup);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void onProductSelect(SelectEvent event) {
    Set<Product> tempSet = new HashSet<>();
    List<Product> tempList;
    if (getSelectedProductList().size() > 1) {
      for (Product prod : getSelectedProductList()) {
        tempSet.add(prod);
      }
      tempList = new ArrayList<>(tempSet);
      setSelectedProductList(tempList);
    }
    setCustomerOutstandingList(null);
  }

  /**
   *
   * @param event
   */
  public void onProductUnSelect(UnselectEvent event) {
    if (getSelectedProductList().size() == 1) {
      getSelectedProductList().clear();
    }
    setCustomerOutstandingList(null);
  }

  public List<Product> getSelectedProductList() {
    return selectedProductList;
  }

  public void setSelectedProductList(List<Product> selectedProductList) {
    this.selectedProductList = selectedProductList;
  }
}
