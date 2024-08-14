/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import spica.constant.AccountingConstant;
import spica.reports.model.FilterParameters;
import spica.reports.model.GstReport;
import spica.reports.model.GstReportSummary;
import spica.reports.service.CompanyGstInAndOutService;
import spica.scm.domain.Company;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sujesh
 */
@Named(value = "gstReportDetailView")
@ViewScoped
public class GstReportDetailView implements Serializable {

  private GstReportSummary reportSummary;
  private FilterParameters filterParameters;
  private Company company;
  private transient LazyDataModel<GstReportSummary> gstReportDetailLazyModel;

  private transient List<GstReport> gstReportDetailList;

  @PostConstruct
  public void init() {
    reportSummary = (GstReportSummary) Jsf.popupParentValue(GstReportSummary.class);
  }

  public List<GstReport> getGstReportDetailList(MainView main) {
    if (StringUtil.isEmpty(gstReportDetailList) && !main.hasError()) {
      try {
        if (reportSummary != null) {
          gstReportDetailList = ledgertDetailView(main, reportSummary);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return gstReportDetailList;
  }

  public LazyDataModel<GstReportSummary> getGstReportDetailLazyModel() {
    return gstReportDetailLazyModel;
  }

  public void setGstReportDetailLazyModel(LazyDataModel<GstReportSummary> gstReportDetailLazyModel) {
    this.gstReportDetailLazyModel = gstReportDetailLazyModel;
  }

  public void setGstReportDetailList(List<GstReport> gstReportDetailList) {
    this.gstReportDetailList = gstReportDetailList;
  }

  public GstReportSummary getReportView() {
    return reportSummary;
  }

  public void setReportView(GstReportSummary reportView) {
    this.reportSummary = reportView;
  }

  public FilterParameters getFilterParameters() {
    return filterParameters;
  }

  public void setFilterParameters(FilterParameters filterParameters) {
    this.filterParameters = filterParameters;
  }

  public GstReportSummary getReportSummary() {
    return reportSummary;
  }

  private List ledgertDetailView(MainView main, GstReportSummary reportView) {
    Integer taxType;
    if (company == null || company.getId() == null) {
      company = UserRuntimeView.instance().getCompany();
    }
    if (reportView.getCgst() > 0) {
      taxType = AccountingConstant.ACC_HEAD_TAX_CGST.getId();
    } else if (reportView.getIgst() > 0) {
      taxType = AccountingConstant.ACC_HEAD_TAX_IGST.getId();
    } else {
      taxType = AccountingConstant.ACC_HEAD_TAX_SGST.getId();
    }
    gstReportDetailList = CompanyGstInAndOutService.getLedgerDetails(main, reportView, company, taxType);

    return gstReportDetailList;

  }

  public double actualAmount(Double credit, Double debit) {
    if (credit == null) {
      return debit;
    }
    if (debit == null) {
      return credit;
    }
    return (credit > debit) ? credit - debit : debit - credit;

  }
}
