/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import spica.constant.AccountingConstant;
import spica.reports.model.FilterParameters;
import spica.reports.model.GstReport;
import spica.reports.model.GstReportSummary;

import spica.reports.service.CompanyGstInAndOutService;
import spica.scm.domain.Company;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author Anoop Jayachandran
 */
@Named(value = "companyGstInAndOutView")
@ViewScoped
public class CompanyGstInAndOutView implements Serializable {

  private transient FilterParameters filterParameters;
  private transient List<GstReport> gstReportList;
  private transient List<GstReport> gstReportDetailList;
  private transient List<GstReport> gstLedgerDetailList;
  private transient List<GstReportSummary> gstViewList;
  private transient List<GstReportSummary> gstDetailViewList;
  private transient List<GstReportSummary> gstLedgerDetailViewList;
  private transient Company company;
  private transient Date lastActive;
  // private transient boolean changed = false;
  private transient String preLedgerCode = null;

  /**
   * Creates a new instance of CompanyGstInAndOutView
   */
  public CompanyGstInAndOutView() {
  }

  public void setGstViewList(List<GstReportSummary> gstViewList) {
    this.gstViewList = gstViewList;
  }

  public Company getCompany() {
    return company;
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

  public void resetCompanyGstInAndOutView() {
    setGstViewList(null);
    //setFilterParameters(null);
  }

  public void actionCompanyGstInAndOutReport(MainView main) {
    try {
      fetchReport(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void fetchReport(MainView main) {
    if (isChanged()) {
      setPreLedgerCode(null);
      if (company == null || company.getId() == null) {
        company = UserRuntimeView.instance().getCompany();
      }
      setLastActive(filterParameters.getFromDate());
      gstReportList = CompanyGstInAndOutService.taxInputOutput(main, company, filterParameters);
      gstViewList = generateGstView(gstReportList);
    }
  }

  public List<GstReportSummary> getGstViewList(MainView main) {
    try {
      fetchReport(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return gstViewList;
  }

  public List<GstReportSummary> getGstDetailViewList(MainView main, String ledgerCode) {
    try {
      if (getPreLedgerCode() == null || !getPreLedgerCode().equals(ledgerCode)) {
        setStartEndDate();
        setPreLedgerCode(ledgerCode);
        if (company == null || company.getId() == null) {
          company = UserRuntimeView.instance().getCompany();
        }
        filterParameters.setReportType(ledgerCode);
        gstReportDetailList = CompanyGstInAndOutService.taxInputOutputDetail(main, company, filterParameters);
        gstDetailViewList = generateGstViewDetail(gstReportDetailList);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return gstDetailViewList;

  }

  private List generateGstView(List<GstReport> reportList) {
    Map<String, GstReportSummary> map = new HashMap<>();
    if (reportList != null) {
      for (GstReport t : reportList) {
        if (map != null) {
          GstReportSummary v = new GstReportSummary();
          if (map.get(t.getLedgercode()) != null) {
            v = map.get(t.getLedgercode());
          } else {
            v.setLedgerCode(t.getLedgercode());
          }

          if (t.getTaxtype().equals("SGST")) {
            v.setSgst(actualAmount(t.getDebit(), t.getCredit()));
          } else if (t.getTaxtype().equals("CGST")) {
            v.setCgst(actualAmount(t.getDebit(), t.getCredit()));
          } else if (t.getTaxtype().equals("IGST")) {
            v.setIgst(actualAmount(t.getDebit(), t.getCredit()));
          }
          map.put(v.getLedgerCode(), v);

        }
      }
    }
    return new ArrayList(map.values());
  }

  public double actualAmount(Double debit, Double credit) {
    if (debit == null) {
      debit = 0.0;
    }
    if (credit == null) {
      credit = 0.0;
    }
    return (debit > credit ? debit - credit : credit - debit);
  }

  public void setStartEndDate() {
    Calendar cal = Calendar.getInstance();
    if (getFilterParameters().getFromDate() == null) {
      cal.setTime(new Date());
      cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
      filterParameters.setFromDate(cal.getTime());
      cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
      filterParameters.setToDate(cal.getTime());
    } else {
      cal.setTime(filterParameters.getFromDate());
      cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
      filterParameters.setFromDate(cal.getTime());
      cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
      filterParameters.setToDate(cal.getTime());
    }
  }

  public List<GstReportSummary> generateGstViewDetail(List<GstReport> reportDetailList) {
    Map<String, GstReportSummary> map = new HashMap<>();
    reportDetailList.forEach((report) -> {
      GstReportSummary view = new GstReportSummary();
      switch (report.getTaxtype()) {
        case "IGST":
          view.setIgst(actualAmount(report.getDebit(), report.getCredit()));
          break;
        case "CGST":
          view.setCgst(actualAmount(report.getDebit(), report.getCredit()));
          break;
        case "SGST":
          view.setSgst(actualAmount(report.getDebit(), report.getCredit()));
          break;
        default:
          break;
      }
      view.setLedgerCode(report.getLedgercode());
      view.setTitle(report.getTitle());
      view.setEntityId(report.getEntityid());
      map.put(report.getTitle(), view);
    });
    return new ArrayList(map.values());
  }

  public boolean isChanged() {
    if (company != null && !company.getId().equals(UserRuntimeView.instance().getCompany().getId())) {
      company = UserRuntimeView.instance().getCompany();
      return true;
    }
    if (gstViewList == null) {
      return true;
    }
    if (getLastActive() == null) {
      return true;
    } else {
      setStartEndDate();
      if (getLastActive().equals(filterParameters.getFromDate())) {
        return false;
      } else {
        return true;
      }
    }
  }

  public String getPreLedgerCode() {
    return preLedgerCode;
  }

  public void setPreLedgerCode(String preLedgerCode) {
    this.preLedgerCode = preLedgerCode;
  }

//  public void setChanged(boolean changed) {
//    this.changed = changed;
//  }
  public Date getLastActive() {
    return lastActive;
  }

  public void setLastActive(Date lastActive) {
    this.lastActive = lastActive;
  }

  public List<GstReportSummary> getGstLedgerDetailViewList() {
    return gstLedgerDetailViewList;
  }

  public List<GstReport> getGstLedgerDetailList() {
    return gstLedgerDetailList;
  }

  public void showReportDetailDialog(GstReportSummary reportSummary) {
    reportSummary.setFromDate(filterParameters.getFromDate());
    reportSummary.setToDate(filterParameters.getToDate());
    Jsf.popupList(FileConstant.GST_REPORT_DETAIL, reportSummary);
  }

  public void showReportDetailByTax(GstReportSummary report, int taxType) {
    GstReportSummary reportSummary = new GstReportSummary(report);
    reportSummary.setLedgerCode(report.getLedgerCode());
    if (AccountingConstant.ACC_HEAD_TAX_IGST.getId() == taxType) {
      reportSummary.setCgst(0.0);
      reportSummary.setSgst(0.0);
    }
    if (AccountingConstant.ACC_HEAD_TAX_CGST.getId() == taxType) {
      reportSummary.setIgst(0.0);
      reportSummary.setSgst(0.0);
    }
    if (AccountingConstant.ACC_HEAD_TAX_SGST.getId() == taxType) {
      reportSummary.setIgst(0.0);
      reportSummary.setCgst(0.0);
    }
    reportSummary.setFromDate(filterParameters.getFromDate());
    reportSummary.setToDate(filterParameters.getToDate());
    Jsf.popupList(FileConstant.GST_REPORT_DETAIL, reportSummary);
  }

}
