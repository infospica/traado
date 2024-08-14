/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.List;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.constant.ReportConstant;
import spica.reports.model.CreditNoteReport;
import spica.reports.model.FilterParameters;
import spica.reports.service.CreditNoteReportService;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.export.ExcelSheet;
import spica.scm.view.ScmLookupExtView;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.DateUtil;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Godson Joseph
 */
@Named(value = "creditNoteReportView")
@ViewScoped
public class CreditNoteReportView implements Serializable {

  private transient FilterParameters filterParameters;
  private transient List<CreditNoteReport> creditNoteSalesReportList;
  private transient List<CreditNoteReport> creditNoteReturnReportList;
  private transient String type;

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

  public List<CreditNoteReport> getCreditNoteSalesReportList() {
    if (StringUtil.isEmpty(creditNoteSalesReportList)) {
      MainView main = Jsf.getMain();
      try {
        creditNoteSalesReportList = CreditNoteReportService.getCreditNoteSalesReportList(main, getCompany(), filterParameters);
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return creditNoteSalesReportList;
  }

  public void setCreditNoteSalesReportList(List<CreditNoteReport> creditNoteSalesReportList) {
    this.creditNoteSalesReportList = creditNoteSalesReportList;
  }

  public List<CreditNoteReport> getCreditNoteReturnReportList() {
    if (StringUtil.isEmpty(creditNoteReturnReportList)) {
      MainView main = Jsf.getMain();
      try {
        creditNoteReturnReportList = CreditNoteReportService.getCreditNoteReturnReportList(main, getCompany(), filterParameters);
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return creditNoteReturnReportList;
  }

  public void setCreditNoteReturnReportList(List<CreditNoteReport> creditNoteReturnReportList) {
    this.creditNoteReturnReportList = creditNoteReturnReportList;
  }

//
//  public List<AccountGroup> accountGroupAuto(String filter) {
//    if (getCompany() != null && filterParameters != null) {
//      MainView main = Jsf.getMain();
//      try {
//        return UserRuntimeService.accountGroupAutoAll(main, filter, getCompany().getId(), UserRuntimeView.instance().getAppUser());
//      } catch (Throwable t) {
//        main.rollback(t, "error.select");
//      } finally {
//        main.close();
//      }
//    }
//    return null;
//  }
  public void backOrNext(int pos) {
    filterParameters.setFromDate(DateUtil.moveMonths(filterParameters.getFromDate(), pos));
    reset();
  }

  public void reset() {
    setCreditNoteSalesReportList(null);
    setCreditNoteReturnReportList(null);
  }

  public List<Account> accountAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      if (filterParameters.getAccountGroup() != null) {
        List<Account> accountList = ScmLookupExtView.accountByAccountGroupProfile(main, filterParameters.getAccountGroup(), filter);
        if (accountList != null) {
          accountList.add(0, new Account(0, "All"));
        }
        return accountList;
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void export(MainView main) {
    try {
      ExcelSheet.createCreditNoteReport(main, getCreditNoteSalesReportList(), getCreditNoteReturnReportList(), filterParameters);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    filterParameters.setAccount(null);
    reset();
  }

  public void accountChangeEvent(AjaxBehaviorEvent event) {
    filterParameters.setAccount(null);
    reset();
  }
  
  public void filterChangeEvent(ValueChangeEvent event) {
    reset();
  }
   public String getType() {
    if (type == null) {
      type = ReportConstant.SALES;
    }
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  
}
