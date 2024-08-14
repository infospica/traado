/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.fin.common.FilterObjects;
import spica.reports.model.DocumentSummary;
import spica.reports.model.FilterParameters;
import spica.reports.model.GstB2csReport;
import spica.reports.model.GstCdnReport;
import spica.reports.model.GstHsnReport;
import spica.reports.model.Gst1Report;
import spica.reports.service.Gst1ReportService;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.export.ExcelGstSheet;
import spica.scm.view.PopUpView;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.DateUtil;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "companyGstReportView")
@ViewScoped
public class CompanyGstReportView extends FilterObjects implements Serializable {

  private transient Company company;
  private transient FilterParameters filterParameters;
  private transient List<Gst1Report> gstb2bReportList;
  private transient List<GstCdnReport> gstCdnrCReportList;
//  private transient List<GstCdnReport> gstCdnrDReportList;
  private transient List<GstHsnReport> gstHsnReportList;
  private transient List<GstB2csReport> gstB2csReportList;
  private transient List<DocumentSummary> documentSummaryList;
  private transient Double refundValue;
  private transient Double totalIgst;
  private transient Double totalCgst;
  private transient Double totalSgst;
  private transient Double taxableValue;
  private transient Double invoiceAmount;
  private transient Double taxAmount;
  private transient String type;
  private transient Double totalValue;
  private transient List<Gst1Report> draftList;
  private transient Integer exportFormat = SystemConstants.EXPORT;
  private transient int totalNo;
  private transient int totalCancelled;


  public List<Gst1Report> getGstb2bReportList(MainView main) {
    if (StringUtil.isEmpty(gstb2bReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        gstb2bReportList = Gst1ReportService.getGstB2bReportList(main, filterParameters, company);
        invoiceAmount = 0.00;
        taxAmount = 0.00;
        if (!gstb2bReportList.isEmpty() && gstb2bReportList.size() > 0) {
          for (Gst1Report list : gstb2bReportList) {
            invoiceAmount += list.getInvoiceAmount() == null ? 0.0 : list.getInvoiceAmount();
            taxAmount += list.getTaxAmount() == null ? 0.0 : list.getTaxAmount();
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return gstb2bReportList;
  }

  public void setGstb2bReportList(List<Gst1Report> gstb2bReportList) {
    this.gstb2bReportList = gstb2bReportList;
  }

  public List<GstCdnReport> getGstCdnrCReportList(MainView main) {
    if (StringUtil.isEmpty(gstCdnrCReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        gstCdnrCReportList = Gst1ReportService.getGstCdnrCReportList(main, filterParameters, company);
        refundValue = 0.00;
        taxableValue = 0.00;
        if (!gstCdnrCReportList.isEmpty() && gstCdnrCReportList.size() > 0) {
          for (GstCdnReport list : gstCdnrCReportList) {
            refundValue += list.getRefundValue() == null ? 0.0 : list.getRefundValue();
            taxableValue += list.getTaxableValue() == null ? 0.0 : list.getTaxableValue();
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return gstCdnrCReportList;
  }

  public List<GstHsnReport> getGstHsnReportList(MainView main) {
    if (StringUtil.isEmpty(gstHsnReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        gstHsnReportList = Gst1ReportService.getGstHsnReportList(main, filterParameters, company);
        totalValue = 0.00;
        taxableValue = 0.00;
        totalIgst = 0.00;
        totalCgst = 0.00;
        totalSgst = 0.00;
        if (!gstHsnReportList.isEmpty() && gstHsnReportList.size() > 0) {
          for (GstHsnReport list : gstHsnReportList) {
            totalValue += list.getTotalValue() == null ? 0.0 : list.getTotalValue();
            taxableValue += list.getTotalTaxableValue() == null ? 0.0 : list.getTotalTaxableValue();
            totalIgst += list.getTotalIgst() == null ? 0.0 : list.getTotalIgst();
            totalCgst += list.getTotalCgst() == null ? 0.0 : list.getTotalCgst();
            totalSgst += list.getTotalSgst() == null ? 0.0 : list.getTotalSgst();
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return gstHsnReportList;
  }

  public List<GstB2csReport> getGstB2csReportList(MainView main) {
    if (StringUtil.isEmpty(gstB2csReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        gstB2csReportList = Gst1ReportService.getGstB2csReportList(main, filterParameters, company);
        taxableValue = 0.00;
        if (!gstB2csReportList.isEmpty() && gstB2csReportList.size() > 0) {
          for (GstB2csReport list : gstB2csReportList) {
            taxableValue += list.getTaxableValue() == null ? 0.0 : list.getTaxableValue();
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return gstB2csReportList;
  }

  public void setGstB2csReportList(List<GstB2csReport> gstB2csReportList) {
    this.gstB2csReportList = gstB2csReportList;
  }

  public void setGstHsnReportList(List<GstHsnReport> gstHsnReportList) {
    this.gstHsnReportList = gstHsnReportList;
  }

  public void setGstCdnrCReportList(List<GstCdnReport> gstCdnrCReportList) {
    this.gstCdnrCReportList = gstCdnrCReportList;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Double getInvoiceAmount() {
    return invoiceAmount;
  }

  public void setInvoiceAmount(Double invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
  }

  public Double getTaxAmount() {
    return taxAmount;
  }

  public void setTaxAmount(Double taxAmount) {
    this.taxAmount = taxAmount;
  }

  public String getType() {
    if (type == null) {
      type = SystemConstants.B2B;
    }
    return type;
  }

  public void setType(String type) {
    this.type = type;
    reset();
    Jsf.update("ExportButtonsDiv");
  }

  public Double getRefundValue() {
    return refundValue;
  }

  public void setRefundValue(Double refundValue) {
    this.refundValue = refundValue;
  }

  public Double getTaxableValue() {
    return taxableValue;
  }

  public Double getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(Double totalValue) {
    this.totalValue = totalValue;
  }

  public void setTaxableValue(Double taxableValue) {
    this.taxableValue = taxableValue;
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
    }
    return filterParameters;
  }

  public void backOrNext(int pos) {
    filterParameters.setFromDate(DateUtil.moveMonths(filterParameters.getFromDate(), pos));
//    filterParameters.setToDate(DateUtil.moveMonths(filterParameters.getToDate(), pos));
    reset();
  }

  public void onFromDateSelect(SelectEvent event) {
    filterParameters.setFromDate((Date) event.getObject());
    reset();
  }

  public void reset() {
    setGstb2bReportList(null);
    setGstCdnrCReportList(null);
    setGstHsnReportList(null);
    setGstB2csReportList(null);
    setDocumentSummaryList(null);
    draftList = null;
    if (filterParameters != null) {
      filterParameters.setAccountGroup(null);
    }
  }

  public Double getTotalIgst() {
    return totalIgst;
  }

  public void setTotalIgst(Double totalIgst) {
    this.totalIgst = totalIgst;
  }

  public Double getTotalCgst() {
    return totalCgst;
  }

  public void setTotalCgst(Double totalCgst) {
    this.totalCgst = totalCgst;
  }

  public Double getTotalSgst() {
    return totalSgst;
  }

  public void setTotalSgst(Double totalSgst) {
    this.totalSgst = totalSgst;
  }

//  public void openSalesInvoicePopup(Integer id) {
//    Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesInvoiceView().replaceFirst("/scm", ""), id, id);
//  }
//
//  public void openSalesReturnPopup(Integer id) {
//    Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesReturnView().replaceFirst("/scm", ""), id, id);
//  }
//
//  public void openPurchaseReturnPopup(Integer id) {
//    Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getPurchaseReturnView().replaceFirst("/scm", ""), id, id);
//  }

  public void openInvoicePopup(PopUpView popUpView, Integer id, String type) {
    popUpView.showInvoices(id, type);
    Jsf.update("confirmDlg");
  }
 public void loadDraftList(MainView main){
       if (draftList == null || exportFormat == SystemConstants.EXPORT_ALL) {
      draftList = new ArrayList<>();      
      try {
        if (getType().equals(SystemConstants.B2B) || exportFormat == SystemConstants.EXPORT_ALL) {
          draftList.addAll(Gst1ReportService.getSalesInvoiceDraftList(main, filterParameters, company));
        }
        if (getType().equals(SystemConstants.CDNR) || exportFormat == SystemConstants.EXPORT_ALL) {
          draftList.addAll(Gst1ReportService.getSalesReturnDraftList(main, filterParameters, company));
          draftList.addAll(Gst1ReportService.getDebitCreditNoteDraftList(main, filterParameters, company));
        }
        if (getType().equals(SystemConstants.HSN)) {
          draftList = Gst1ReportService.getSalesInvoiceDraftList(main, filterParameters, company);
          draftList.addAll(Gst1ReportService.getSalesReturnDraftList(main, filterParameters, company));
          draftList.addAll(Gst1ReportService.getDebitCreditNoteDraftList(main, filterParameters, company));
        }
        if (getType().equals(SystemConstants.B2CS)) {
          draftList = Gst1ReportService.getSalesInvoiceDraftList(main, filterParameters, company);
        }
      } catch (Throwable t) {
        main.error(t,"error.select", null);
      } finally {
        main.close();
      }
    }
 }
  public List<Gst1Report> getDraftList() {
    return draftList;
  }

  public void exportGst1Report(MainView main) {
    try {
      if (exportFormat == SystemConstants.EXPORT) {
        ExcelGstSheet.createGst1Report(main, filterParameters, gstb2bReportList, gstCdnrCReportList, gstHsnReportList, gstB2csReportList, documentSummaryList, false, getType());

      } else if (exportFormat == SystemConstants.EXPORT_ALL) {
        if (filterParameters != null) {
          filterParameters.setAccountGroup(null);
        }
        if (gstb2bReportList == null) {
          getGstb2bReportList(main);
        }
        if (gstB2csReportList == null) {
          getGstB2csReportList(main);
        }
        if (gstHsnReportList == null) {
          getGstHsnReportList(main);
        }
        if (gstCdnrCReportList == null) {
          getGstCdnrCReportList(main);
        }
        if (documentSummaryList == null) {
          getDocumentSummaryList(main);
        }
        ExcelGstSheet.createGst1Report(main, filterParameters, gstb2bReportList, gstCdnrCReportList, gstHsnReportList, gstB2csReportList, documentSummaryList, true, getType());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void setExportFormat(Integer exportFormat) {
    this.exportFormat = exportFormat;
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

  public List<DocumentSummary> getDocumentSummaryList(MainView main) {
    try {
      if (StringUtil.isEmpty(documentSummaryList) && !main.hasError()) {
        company = UserRuntimeView.instance().getCompany();
        if (filterParameters != null) {
          documentSummaryList = Gst1ReportService.getDocumentSummaryList(main, filterParameters, company);
        }
        totalNo = 0;
        totalCancelled = 0;
        if (!documentSummaryList.isEmpty() && documentSummaryList.size() > 0) {
          for (DocumentSummary list : documentSummaryList) {
            totalNo += list.getTotalNo() == null ? 0 : list.getTotalNo();
            totalCancelled += list.getCancelledNo() == null ? 0 : list.getCancelledNo();
          }
        }
      }
      return documentSummaryList;
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void setDocumentSummaryList(List<DocumentSummary> documentSummaryList) {
    this.documentSummaryList = documentSummaryList;
  }

  public int getTotalNo() {
    return totalNo;
  }

  public void setTotalNo(int totalNo) {
    this.totalNo = totalNo;
  }

  public int getTotalCancelled() {
    return totalCancelled;
  }

  public void setTotalCancelled(int totalCancelled) {
    this.totalCancelled = totalCancelled;
  }

}
