/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import spica.fin.common.FilterObjects;
import spica.reports.model.CustomerLedgerReport;
import spica.reports.model.FilterParameters;
import spica.reports.service.CustomerLedgerReportService;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Customer;
import spica.scm.domain.District;
import spica.scm.domain.SalesAgent;
import spica.scm.export.LedgerReportExcel;
import spica.scm.service.SalesAgentService;
import spica.scm.view.ScmLookupExtView;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "customerLedgerReportView")
@ViewScoped
public class CustomerLedgerReportView extends FilterObjects implements Serializable {

  private transient List<CustomerLedgerReport> customerLedgerReportList;
  private transient FilterParameters filterParameters;
  private transient Integer salesAgentId;
  private Double credit;
  private Double debit;

  public List<CustomerLedgerReport> getCustomerLedgerReportList(MainView main) {
    if (StringUtil.isEmpty(customerLedgerReportList) && !main.hasError()) {
      try {
        credit = 0.0;
        debit = 0.0;
        customerLedgerReportList = CustomerLedgerReportService.createCustomerLedgerReportList(main, filterParameters);
        for (CustomerLedgerReport customer : customerLedgerReportList) {
          credit += customer.getCreditAmount() == null ? 0.0 : customer.getCreditAmount();
          debit += customer.getDebitAmount() == null ? 0.0 : customer.getDebitAmount();
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return customerLedgerReportList;
  }

  public void setCustomerLedgerReportList(List<CustomerLedgerReport> customerLedgerReportList) {
    this.customerLedgerReportList = customerLedgerReportList;
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    filterParameters.setAccountGroup(accountGroup);
    filterParameters.setCustomer(null);
    filterParameters.setSalesAgent(null);
    reset();
  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    filterParameters.setDistrict(null);
    return UserRuntimeView.instance().accountGroupAutoAll(filter);
  }

  public List<SalesAgent> salesAgentAuto(String filter) {
    if (UserRuntimeView.instance().getCompany() != null) {
      MainView main = Jsf.getMain();
      try {
        return SalesAgentService.salesAgentsAll(main, UserRuntimeView.instance().getCompany(), filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public void salesAgentSelectEvent(SelectEvent event) {
    SalesAgent salesAgent = (SalesAgent) event.getObject();
    filterParameters.setSalesAgent(salesAgent);
    filterParameters.setAccountGroup(null);
    filterParameters.setCustomer(null);
    filterParameters.setDistrict(null);
    reset();
  }

  public List<District> districtAuto(String filter) {
    List<District> districtList = null;
    if (UserRuntimeView.instance().getCompany() != null && filterParameters.getAccountGroup() == null) {
      districtList = CustomerLedgerReportService.districtListbyCompany(filter);
    }
    if (filterParameters.getAccountGroup() != null) {
      districtList = CustomerLedgerReportService.districtListbyAccountGroup(filterParameters, filter);
    }
    return districtList;
  }

  public List<Customer> customerAuto(String filter) {
    List<Customer> customerList = null;
    if (StringUtils.isEmpty(filter)) {
      filterParameters.setCustomer(null);
    }
    if (UserRuntimeView.instance().getCompany() != null && (filterParameters.getDistrict() == null || filterParameters.getDistrict().length == 0)) {
      customerList = ScmLookupExtView.selectCustomerByCompany(UserRuntimeView.instance().getCompany(), filter);
      customerList.add(0, new Customer(0, "All"));
    }
    if (filterParameters.getDistrict() != null && filterParameters.getDistrict().length > 0) {
      customerList = CustomerLedgerReportService.selectCustomerByDistrict(filterParameters, filter);
      customerList.add(0, new Customer(0, "All"));
    }
    return customerList;
  }

  public void customerSelectEvent(SelectEvent event) {
    Customer customer = (Customer) event.getObject();
    filterParameters.setCustomer(customer);
    filterParameters.setSalesAgent(null);
    reset();
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
    }
    return filterParameters;
  }

  public void backOrNext(int pos) {
    if (wawo.entity.util.DateUtil.moveMonths(getFilterParameters().getFromDate(), pos).compareTo(new Date()) <= 0) {
      getFilterParameters().setFromDate(wawo.entity.util.DateUtil.moveMonths(getFilterParameters().getFromDate(), pos));
      reset();
    }
  }

  public void onFromDateSelect(SelectEvent event) {
    filterParameters.setFromDate((Date) event.getObject());
    reset();
  }

  public void reset() {
    setCustomerLedgerReportList(null);
  }

  public void export(MainView main) {
    try {
      String salesAgent = filterParameters.getSalesAgent() != null ? filterParameters.getSalesAgent().getName() : "";
      LedgerReportExcel.createCustomerLedgerReport(getCustomerLedgerReportList(main), salesAgent);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public Integer getSalesAgentId() {
    return salesAgentId;
  }

  public void setSalesAgentId(Integer salesAgentId) {
    this.salesAgentId = salesAgentId;
  }

  public Double getCredit() {
    return credit;
  }

  public Double getDebit() {
    return debit;
  }

}
