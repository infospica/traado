/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.fin.common.FilterObjects;
import spica.reports.model.AgeWiseStockAnalysis;
import spica.reports.model.FilterParameters;
import spica.reports.service.AgeWiseStockAnalysisService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
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
 * @author godson
 */
@Named(value = "ageWiseStockAnalysisView")
@ViewScoped
public class AgeWiseStockAnalysisView extends FilterObjects implements Serializable {

  private transient List<AgeWiseStockAnalysis> ageWiseStockAnalysisList;
  private transient Company company;
  private transient FilterParameters filterParameters;
  private Double taxableAmount;
  private Double qty;

  public List<AgeWiseStockAnalysis> getAgeWiseStockAnalysisList(MainView main) {
    if (StringUtil.isEmpty(ageWiseStockAnalysisList) && !main.hasError() && filterParameters.getAccountGroup() != null) {
      try {
        company = UserRuntimeView.instance().getCompany();
        ageWiseStockAnalysisList = AgeWiseStockAnalysisService.getAgeWiseStockAnalysisList(main, company, filterParameters);
        taxableAmount = 0.0;
        qty = 0.0;
        if (!ageWiseStockAnalysisList.isEmpty() && ageWiseStockAnalysisList.size() > 0) {
          for (AgeWiseStockAnalysis list : ageWiseStockAnalysisList) {
            taxableAmount += list.getTaxableAmount() != null ? list.getTaxableAmount() : 0;
            qty += list.getCurrentQty() != null ? list.getCurrentQty() : 0;
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return ageWiseStockAnalysisList;
  }

  public void setAgeWiseStockAnalysisList(List<AgeWiseStockAnalysis> ageWiseStockAnalysisList) {
    this.ageWiseStockAnalysisList = ageWiseStockAnalysisList;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
    }
    return filterParameters;
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    if (accountGroup != null) {
      if (accountGroup.getId() != null) {
        filterParameters.setAccountGroup(accountGroup);
        filterParameters.setAccount(null);
        filterParameters.setCustomer(null);
        reset();
      }
    }
  }

  public List<Account> accountAuto(String filter) {
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      MainView main = Jsf.getMain();
      try {
        return ScmLookupExtView.accountByAccountGroupProfile(main, filterParameters.getAccountGroup(), filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select", null);
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

  public void reset() {
    setAgeWiseStockAnalysisList(null);
  }

  public void onFromDateSelect(SelectEvent event) {
    filterParameters.setFromDate((Date) event.getObject());
    if (ageWiseStockAnalysisList != null) {
      ageWiseStockAnalysisList.clear();
      reset();
    }
  }

  public void export(MainView main) {
    try {
      ExcelSheet.createAgeWiseStockReport(ageWiseStockAnalysisList);
    } catch (Throwable t) {
      main.rollback(t, "error.select", null);
    } finally {
      main.close();
    }
  }

  public Double getTaxableAmount() {
    return taxableAmount;
  }

  public Double getQty() {
    return qty;
  }

}
