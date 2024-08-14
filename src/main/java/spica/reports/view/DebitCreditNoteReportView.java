/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import spica.reports.model.CompanyCustomerSales;
import spica.reports.model.FilterParameters;
import spica.reports.service.DebitCreditNoteReportService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.view.ScmLookupExtView;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.DateUtil;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Godson Joseph
 */
@Named(value = "debitCreditNoteReportView")
@ViewScoped
public class DebitCreditNoteReportView implements Serializable {

  private transient FilterParameters filterParameters;
  private transient List<CompanyCustomerSales> debitCreditReportList;
  private transient Integer type;

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
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

  public List<CompanyCustomerSales> getDebitCreditReportList() {
    if (StringUtil.isEmpty(debitCreditReportList)) {
      MainView main = Jsf.getMain();
      try {
        debitCreditReportList = DebitCreditNoteReportService.getDebitCreditReportList(main, getCompany(), type, filterParameters);
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return debitCreditReportList;
  }

  public void setDebitCreditReportList(List<CompanyCustomerSales> debitCreditReportList) {
    this.debitCreditReportList = debitCreditReportList;
  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    if (getCompany() != null && filterParameters != null) {
      MainView main = Jsf.getMain();
      try {
        return UserRuntimeService.accountGroupAutoAll(main, filter, getCompany().getId(), UserRuntimeView.instance().getAppUser());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public void backOrNext(int pos) {
    filterParameters.setFromDate(DateUtil.moveMonths(filterParameters.getFromDate(), pos));
    reset();
  }

  public void partyChangeEvent() {
    reset();
    setType(null);
    filterParameters.setAccount(null);
    filterParameters.setAccountGroup(null);
    filterParameters.setCustomer(null);
  }

  public void reset() {
    setDebitCreditReportList(null);
  }

  public List<Account> accountAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return ScmLookupExtView.accountByCompanyAuto(filter, getCompany().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
}
