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
import spica.reports.model.CompanyCustomerSales;
import spica.reports.model.FilterParameters;
import spica.reports.model.Gst1Report;
import spica.reports.service.Gst1ReportService;
import spica.reports.service.TcsReportService;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.export.ExcelSheet;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Godson Joseph
 */
@Named(value = "tcsReportView")
@ViewScoped
public class TcsReportView implements Serializable {

  private transient Company company;
  private transient FilterParameters filterParameters;
  private transient List<CompanyCustomerSales> tcsReportList;
  private transient Double totalBillAmount;
  private transient Double totalBillAmountWithoutTcs;
  private transient Double totaltcsAmount;

  public List<CompanyCustomerSales> getTcsReportList(MainView main) {
    if (StringUtil.isEmpty(tcsReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        tcsReportList = TcsReportService.getTcsReportList(main, filterParameters, getCompany());
        totalBillAmount = 0.00;
        totalBillAmountWithoutTcs = 0.00;
        totaltcsAmount = 0.00;
        if (!tcsReportList.isEmpty() && tcsReportList.size() > 0) {
          for (CompanyCustomerSales list : tcsReportList) {
            totalBillAmount += list.getInvoiceAmount() == null ? 0.0 : list.getInvoiceAmount();
            totalBillAmountWithoutTcs += list.getInvoiceAmountSubtotal() == null ? 0.0 : list.getInvoiceAmountSubtotal();
            totaltcsAmount += list.getTcsNetAmount() == null ? 0.0 : list.getTcsNetAmount();
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return tcsReportList;
  }

  public void setTcsReportList(List<CompanyCustomerSales> tcsReportList) {
    this.tcsReportList = tcsReportList;
  }

  public void backOrNext(int pos) {
    setTcsReportList(null);
    if (wawo.entity.util.DateUtil.moveMonths(getFilterParameters().getFromDate(), pos).compareTo(new Date()) <= 0) {
      getFilterParameters().setFromDate(wawo.entity.util.DateUtil.moveMonths(getFilterParameters().getFromDate(), pos));
    }
  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return UserRuntimeService.accountGroupFilterAutoAll(main, filter, UserRuntimeView.instance().getCompany().getId(), UserRuntimeView.instance().getAppUser());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void reset() {
    setTcsReportList(null);
    setFilterParameters(null);
  }

  public Company getCompany() {
    if (company == null) {
      company = UserRuntimeView.instance().getCompany();
    }
    return company;
  }
  public void export(MainView main) {
    try {
      ExcelSheet.createCustomerWiseTcsReport(getTcsReportList(main), getCompany(), getFilterParameters());
    } catch (Throwable ex) {
      main.rollback(ex, "error.select");
    } finally {
      main.close();
    }

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

  public void setFilterParameters(FilterParameters filterParameters) {
    this.filterParameters = filterParameters;
  }

  public Double getTotalBillAmount() {
    return totalBillAmount;
  }

  public void setTotalBillAmount(Double totalBillAmount) {
    this.totalBillAmount = totalBillAmount;
  }

  public Double getTotalBillAmountWithoutTcs() {
    return totalBillAmountWithoutTcs;
  }

  public void setTotalBillAmountWithoutTcs(Double totalBillAmountWithoutTcs) {
    this.totalBillAmountWithoutTcs = totalBillAmountWithoutTcs;
  }

  public Double getTotaltcsAmount() {
    return totaltcsAmount;
  }

  public void setTotaltcsAmount(Double totaltcsAmount) {
    this.totaltcsAmount = totaltcsAmount;
  }

}
