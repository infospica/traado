/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.fin.common.FilterObjects;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.reports.model.FilterParameters;
import spica.reports.model.Gst3bReport;
import spica.reports.service.Gst3bReportService;
import spica.scm.domain.Company;
import spica.scm.export.ExcelGstSheet;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.DateUtil;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "gst3bReportView")
@ViewScoped
public class Gst3bReportView extends FilterObjects implements Serializable {

  private transient List<Gst3bReport> gst3bPurchaseReportList;
  private transient List<Gst3bReport> gst3bPurchaseReturnReportList;
  private transient List<Gst3bReport> gst3bCdnrCReportList;
  private transient List<Gst3bReport> gst3bCdnrDReportList;
  private transient List<Gst3bReport> gst3bSalesReportList;
  private transient List<Gst3bReport> gst3bSalesReturnReportList;
  private transient Company company;
  private transient FilterParameters filterParameters;
  private transient Double billAmount;
  private transient Double taxableValue;
  private transient Double taxData;
  private transient String type;

//  public void Gst3bReportView(MainView main) {
//    getGst3bPurchaseReportList(main);
//    getGst3bSalesReportList(main);
//  }
  public List<Gst3bReport> getGst3bPurchaseReportList(MainView main) {
    if (StringUtil.isEmpty(gst3bPurchaseReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        gst3bPurchaseReportList = Gst3bReportService.getGst3bPurchaseReportList(main, company, filterParameters);
        billAmount = 0.00;
        taxableValue = 0.00;
        taxData = 0.00;
        if (!gst3bPurchaseReportList.isEmpty() && gst3bPurchaseReportList.size() > 0) {
          for (Gst3bReport list : gst3bPurchaseReportList) {
            billAmount += (list.getBillAmount() == null ? 0 : list.getBillAmount());
            taxableValue += (list.getTaxableValue() == null ? 0 : list.getTaxableValue());
            taxData += (list.getTaxData() == null ? 0 : list.getTaxData());
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      }finally{
        main.close();
      }
    }
    return gst3bPurchaseReportList;
  }

  public void setGst3bPurchaseReportList(List<Gst3bReport> gst3bPurchaseReportList) {
    this.gst3bPurchaseReportList = gst3bPurchaseReportList;
  }

  public List<Gst3bReport> getGst3bPurchaseReturnReportList(MainView main) {
    if (StringUtil.isEmpty(gst3bPurchaseReturnReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        gst3bPurchaseReturnReportList = Gst3bReportService.getGst3bPurchaseReturnReportList(main, company, filterParameters);
        billAmount = 0.00;
        taxableValue = 0.00;
        taxData = 0.00;
        if (!gst3bPurchaseReturnReportList.isEmpty() && gst3bPurchaseReturnReportList.size() > 0) {
          for (Gst3bReport list : gst3bPurchaseReturnReportList) {
            billAmount += (list.getBillAmount() == null ? 0 : list.getBillAmount());
            taxableValue += (list.getTaxableValue() == null ? 0 : list.getTaxableValue());
            taxData += (list.getTaxData() == null ? 0 : list.getTaxData());
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      }finally{
        main.close();
      }
    }
    return gst3bPurchaseReturnReportList;
  }

  public void setGst3bPurchaseReturnReportList(List<Gst3bReport> gst3bPurchaseReturnReportList) {
    this.gst3bPurchaseReturnReportList = gst3bPurchaseReturnReportList;
  }

  public List<Gst3bReport> getGst3bCdnrCReportList(MainView main) {
    if (StringUtil.isEmpty(gst3bCdnrCReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        gst3bCdnrCReportList = Gst3bReportService.getGst3bCdnrCReportList(main, company, filterParameters);
        billAmount = 0.00;
        taxableValue = 0.00;
        taxData = 0.00;
        if (!gst3bCdnrCReportList.isEmpty() && gst3bCdnrCReportList.size() > 0) {
          for (Gst3bReport list : gst3bCdnrCReportList) {
            billAmount += (list.getBillAmount() == null ? 0 : list.getBillAmount());
            taxableValue += (list.getTaxableValue() == null ? 0 : list.getTaxableValue());
            taxData += (list.getTaxData() == null ? 0 : list.getTaxData());
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      }finally{
        main.close();
      }
    }
    return gst3bCdnrCReportList;
  }

  public void setGst3bCdnrCReportList(List<Gst3bReport> gst3bCdnrCReportList) {
    this.gst3bCdnrCReportList = gst3bCdnrCReportList;
  }

  public List<Gst3bReport> getGst3bCdnrDReportList(MainView main) {
    if (StringUtil.isEmpty(gst3bCdnrDReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        gst3bCdnrDReportList = Gst3bReportService.getGst3bCdnrDReportList(main, company, filterParameters);
        billAmount = 0.00;
        taxableValue = 0.00;
        taxData = 0.00;
        if (!gst3bCdnrDReportList.isEmpty() && gst3bCdnrDReportList.size() > 0) {
          for (Gst3bReport list : gst3bCdnrDReportList) {
            billAmount += (list.getBillAmount() == null ? 0 : list.getBillAmount());
            taxableValue += (list.getTaxableValue() == null ? 0 : list.getTaxableValue());
            taxData += (list.getTaxData() == null ? 0 : list.getTaxData());
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      }finally{
        main.close();
      }
    }
    return gst3bCdnrDReportList;
  }

  public void setGst3bCdnrDReportList(List<Gst3bReport> gst3bCdnrDReportList) {
    this.gst3bCdnrDReportList = gst3bCdnrDReportList;
  }

  public List<Gst3bReport> getGst3bSalesReportList(MainView main) {
    if (StringUtil.isEmpty(gst3bSalesReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        gst3bSalesReportList = Gst3bReportService.getGst3bSalesReportList(main, company, filterParameters);
        billAmount = 0.00;
        taxableValue = 0.00;
        taxData = 0.00;
        if (!gst3bSalesReportList.isEmpty() && gst3bSalesReportList.size() > 0) {
          for (Gst3bReport list : gst3bSalesReportList) {
            billAmount += (list.getBillAmount() == null ? 0 : list.getBillAmount());
            taxableValue += (list.getTaxableValue() == null ? 0 : list.getTaxableValue());
            taxData += (list.getTaxData() == null ? 0 : list.getTaxData());
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      }finally{
        main.close();
      }
    }
    return gst3bSalesReportList;
  }

  public void setGst3bSalesReportList(List<Gst3bReport> gst3bSalesReportList) {
    this.gst3bSalesReportList = gst3bSalesReportList;
  }

  public List<Gst3bReport> getGst3bSalesReturnReportList(MainView main) {
    if (StringUtil.isEmpty(gst3bSalesReturnReportList) && !main.hasError()) {
      try {
        company = UserRuntimeView.instance().getCompany();
        gst3bSalesReturnReportList = Gst3bReportService.getGst3bSalesReturnReportList(main, company, filterParameters);
        billAmount = 0.00;
        taxableValue = 0.00;
        taxData = 0.00;
        if (!gst3bSalesReturnReportList.isEmpty() && gst3bSalesReturnReportList.size() > 0) {
          for (Gst3bReport list : gst3bSalesReturnReportList) {
            billAmount += (list.getBillAmount() == null ? 0 : list.getBillAmount());
            taxableValue += (list.getTaxableValue() == null ? 0 : list.getTaxableValue());
            taxData += (list.getTaxData() == null ? 0 : list.getTaxData());
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      }finally{
        main.close();
      }
    }
    return gst3bSalesReturnReportList;
  }

  public void setGst3bSalesReturnReportList(List<Gst3bReport> gst3bSalesReturnReportList) {
    this.gst3bSalesReturnReportList = gst3bSalesReturnReportList;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Double getBillAmount() {
    return billAmount;
  }

  public void setBillAmount(Double billAmount) {
    this.billAmount = billAmount;
  }

  public Double getTaxableValue() {
    return taxableValue;
  }

  public void setTaxableValue(Double taxableValue) {
    this.taxableValue = taxableValue;
  }

  public Double getTaxData() {
    return taxData;
  }

  public void setTaxData(Double taxData) {
    this.taxData = taxData;
  }

  public String getType() {
    if (type == null) {
      type = SystemConstants.PURCHASE_REPORT;
    }
    return type;
  }

  public void setType(String type) {
    this.type = type;
    reset();
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.DAY_OF_MONTH, 1);
      filterParameters.setFromDate(cal.getTime());
      filterParameters.setToDate(new Date());
    }
    return filterParameters;
  }

  public void backOrNext(int pos) {
    filterParameters.setFromDate(DateUtil.moveMonths(filterParameters.getFromDate(), pos));
    reset();
  }

  public void onFromDateSelect(SelectEvent event) {
    filterParameters.setFromDate((Date) event.getObject());
    reset();
  }

  public void export(MainView main, boolean exportAll) {
    try {
      if (exportAll) {
        if (gst3bPurchaseReportList == null) {
          getGst3bPurchaseReportList(main);
        }
        if (gst3bSalesReportList == null) {
          getGst3bSalesReportList(main);
        }
        if (gst3bSalesReturnReportList == null) {
          getGst3bSalesReturnReportList(main);
        }
        if (gst3bPurchaseReturnReportList == null) {
          getGst3bPurchaseReturnReportList(main);
        }
        if (gst3bCdnrCReportList == null) {
          getGst3bCdnrCReportList(main);
        }
        if (gst3bCdnrDReportList == null) {
          getGst3bCdnrDReportList(main);
        }
      }
      Map<String, List<Gst3bReport>> gst3bMap = new LinkedHashMap<>();
      gst3bMap.put("PURCHASE", gst3bPurchaseReportList);
      gst3bMap.put("PURCHASE RETURN", gst3bPurchaseReturnReportList);
      gst3bMap.put("SALES", gst3bSalesReportList);
      gst3bMap.put("SALES RETURN", gst3bSalesReturnReportList);
      gst3bMap.put("CDNR C", gst3bCdnrCReportList);
      gst3bMap.put("CDNR D", gst3bCdnrDReportList);
      ExcelGstSheet.createGst3bReport(main, filterParameters, gst3bMap, exportAll, getType());
    } catch (Throwable ex) {
      main.rollback(ex, "error.select");
    } finally {
      main.close();
    }
  }

  public void reset() {
    setGst3bPurchaseReportList(null);
    setGst3bSalesReportList(null);
    setGst3bSalesReturnReportList(null);
    setGst3bPurchaseReturnReportList(null);
    setGst3bCdnrDReportList(null);
    setGst3bCdnrCReportList(null);
  }

  public void openPopup(Integer id, Integer type) {
    switch (type) {
      case 1:
        Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getProductEntryView().replaceFirst("/scm", ""), id, id);
        break;
      case 2:
        Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesInvoiceView(), id, id);
        break;
      case 3:
        Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesReturnView(), id, id);
        break;
      case 4:
        Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getPurchaseReturnView(), id, id);
        break;
      case 5:
        Jsf.popupForm(FileConstant.SUPPLIER_DEBIT_NOTE, new AccountingTransactionDetailItem(id), id);
        break;
    }
  }
}
