/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import spica.constant.AccountingConstant;
import spica.reports.model.CustomerOutstanding;
import spica.reports.model.FilterParameters;
import spica.reports.model.SupplierOutstanding;
import spica.reports.service.CustomerOutstandingService;
import spica.reports.service.SupplierOutStandingService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Vendor;
import spica.scm.view.ScmLookupExtView;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
@Named(value = "supplierOutstandingView")
@ViewScoped
public class SupplierOutstandingView implements Serializable {

  private transient SupplierOutstanding supplierOutstanding;
  private transient List<CustomerOutstanding> supplierOutstandingList;
  // private transient Company company;
  private transient AccountGroup accountGroup;
  private transient Account account;
  private Map<String, Double> amountBySupplierDebit;
  private Map<String, Double> amountBySupplierCredit;
  private Double receivableAmount = 0.0;
  private Double receivedAmount = 0.0;
  private Double balance = 0.0;
  private Double billAmount = 0.0;
  private Double cumulative = 0.0;
  private FilterParameters filterParameters;

  public SupplierOutstanding getSupplierOutstanding() {
    return supplierOutstanding;
  }

  public void setSupplierOutstanding(SupplierOutstanding supplierOutstanding) {
    this.supplierOutstanding = supplierOutstanding;
  }

  public List<CustomerOutstanding> getSupplierOutstandingList() {
    return supplierOutstandingList;
  }

  public void setSupplierOutstandingList(List<CustomerOutstanding> supplierOutstandingList) {
    this.supplierOutstandingList = supplierOutstandingList;
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

  public List<CustomerOutstanding> fetchSupplierOutstandingList(MainView main) {
    try {
      if (supplierOutstandingList == null) {
        supplierOutstandingList = CustomerOutstandingService.getCustomerOutstandingList(main, filterParameters, AccountingConstant.ACC_ENTITY_VENDOR.getId(), null, SystemConstants.HIDE_INVOICE_WISE, null);
        Integer customerId = 0;
        receivableAmount = 0.0;
        receivedAmount = 0.0;
        billAmount = 0.0;
        balance = 0.0;
        cumulative = 0.0;
        for (CustomerOutstanding outStanding : supplierOutstandingList) {
          outStanding.setInvoiceamount(outStanding.getInvoiceamount() * -1);
          outStanding.setOustandingamount(outStanding.getOustandingamount() * -1);
          billAmount += outStanding.getInvoiceamount();
          balance += outStanding.getOustandingamount();
          outStanding.setReceivedAmount(outStanding.getInvoiceamount() - outStanding.getOustandingamount());
          receivedAmount += outStanding.getReceivedAmount();
          if (StringUtils.isEmpty(outStanding.getAgentName())) {
            outStanding.setAgentName(getCompany().getCompanyName());
          }
          cumulative = customerId.intValue() == outStanding.getCustomerId() ? cumulative : 0.0;
          cumulative += outStanding.getOustandingamount();
          outStanding.setCumulativeAmount(cumulative);
          customerId = outStanding.getCustomerId();
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return supplierOutstandingList;
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    getFilterParameters().setAccountGroup(accountGroup);
    getFilterParameters().setAccount(null);
    supplierOutstandingList = null;
  }

  public void accountSelectEvent(SelectEvent event) {
    Account account = (Account) event.getObject();
    filterParameters.setAccount(account);
    supplierOutstandingList = null;
  }

  public List<Account> accountAuto(String filter) {
    //  List<Account> accountList = new ArrayList<>();
    if (getFilterParameters().getAccountGroup() != null) {
      if (getFilterParameters().getAccountGroup().getId() != null) {
        MainView main = Jsf.getMain();
        try {
          return ScmLookupExtView.accountByAccountGroupProfileAll(main, getFilterParameters().getAccountGroup(), filter);
        } catch (Throwable t) {
          main.rollback(t, "error.select");
        } finally {
          main.close();
        }
      }
    }
    return null;
  }

  public Double getAmountBySupplierDebit(String supplierName) {
    if (amountBySupplierDebit != null) {
      return amountBySupplierDebit.get(supplierName);
    } else {
      return null;
    }
  }

  public Double getAmountBySupplierCredit(String supplierName) {
    if (amountBySupplierCredit != null) {
      return amountBySupplierCredit.get(supplierName);
    } else {
      return null;
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

  public List<Vendor> vendorAuto(String filter) {
    List<Vendor> vendorList = null;
    MainView main = Jsf.getMain();
    try {
      vendorList = SupplierOutStandingService.selectVendorAuto(main, filter, getCompany());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    vendorList.add(0, new Vendor(0, "All"));
    return vendorList;
  }

  public void vendorSelectEvent(SelectEvent event) {
    Vendor vendor = (Vendor) event.getObject();
    setSupplierOutstandingList(null);
    filterParameters.setVendor(vendor);
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    List<AccountGroup> accountGroupLlist = new ArrayList<>();
    if (filterParameters.getVendor() == null || filterParameters.getVendor().getId() == 0) {
      accountGroupLlist = UserRuntimeView.instance().accountGroupAuto(filter);
    } else {
      MainView main = Jsf.getMain();
      try {
        accountGroupLlist = SupplierOutStandingService.selectAccountGroupBySupplier(main, filterParameters.getVendor(), filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    accountGroupLlist.add(0, new AccountGroup(0, "All"));
    return accountGroupLlist;
  }

  public Double getReceivableAmount() {
    return receivableAmount;
  }

  public void setReceivableAmount(Double receivableAmount) {
    this.receivableAmount = receivableAmount;
  }

  public Double getReceivedAmount() {
    return receivedAmount;
  }

  public void setReceivedAmount(Double receivedAmount) {
    this.receivedAmount = receivedAmount;
  }

  public Double getBillAmount() {
    return billAmount;
  }

  public void setBillAmount(Double billAmount) {
    this.billAmount = billAmount;
  }

  public Double getCumulative() {
    return cumulative;
  }

  public void setCumulative(Double cumulative) {
    this.cumulative = cumulative;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }
}
