/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.reports.model.CompanyCustomerSalesRender;
import spica.reports.model.FilterParameters;
import spica.reports.model.SalesReturnReport;
import spica.reports.service.SalesReturnReportService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.view.ScmLookupExtView;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sujesh
 */
@Named(value = "salesReturnReportView")
@ViewScoped
public class SalesReturnReportView implements Serializable {

  private SalesReturnReport salesReturnReport;
  private transient FilterParameters filterParameters;
  private List<SalesReturnReport> salesReturnReportList;
  private List<String> columnNames;
  private String[] selectedColumns;
  private transient CompanyCustomerSalesRender companyCustomerSalesRender;
  private transient List reportNames;
  private transient Company company;
  private Double net = 0.0;
  private Double qty = 0.0;
  private Double freeQty = 0.0;
  private Double goodsValue = 0.0;
  private Double returnValue = 0.0;
  private Double salesValue = 0.0;
  private Double igst = 0.0;
  private Double cgst = 0.0;
  private Double sgst = 0.0;
  private Double gstAmt = 0.0;
  private Double billAmt = 0.0;
  private transient Double schemeDiscount;
  private transient Double productDiscount;
  private transient Double invoiceDiscount;
  private int type;

  public SalesReturnReport getSalesReturnReport() {
    return salesReturnReport;
  }

  public void setSalesReturnReport(SalesReturnReport salesReturnReport) {
    this.salesReturnReport = salesReturnReport;
  }

  public List<SalesReturnReport> getSalesReturnReportList(MainView main) {
    if (StringUtil.isEmpty(salesReturnReportList)) {
      try {

        if (filterParameters.getFromDate() == null) {
          filterParameters.setFromDate(UserRuntimeView.instance().getStartFiscalDate());
        }
        companyCustomerSalesRender = new CompanyCustomerSalesRender();
        companyCustomerSalesRender.setRender(true);
        salesReturnReportList = SalesReturnReportService.getSalesReturnReportList(main, UserRuntimeView.instance().getCompany(), getFilterParameters(), getType());

        billAmt = 0.0;
        gstAmt = 0.0;
        net = 0.0;
        igst = 0.0;
        cgst = 0.0;
        sgst = 0.0;
        qty = 0.0;
        freeQty = 0.0;
        schemeDiscount = 0.0;
        productDiscount = 0.0;
        invoiceDiscount = 0.0;
        goodsValue = 0.0;
        returnValue = 0.0;
        salesValue = 0.0;
        for (SalesReturnReport report : salesReturnReportList) {
          if (report.getGstAmount() != null) {
            gstAmt += report.getGstAmount();
          }
          if (report.getNetAmount() != null) {
            net += report.getNetAmount();
          }
          if (report.getGoodsValue() != null) {
            goodsValue += report.getGoodsValue();
          }
          if (report.getIgst() != null) {
            igst += report.getIgst();
          }
          if (report.getCgst() != null) {
            cgst += report.getCgst();
          }
          if (report.getSgst() != null) {
            sgst += report.getSgst();
          }
          if (report.getQty() != null) {
            qty += report.getQty();
          }
          if (report.getNetAmount() != null) {
            billAmt += report.getNetAmount();
          }
          if (report.getSalesValue() != null) {
            salesValue += report.getSalesValue();
          }
          freeQty += report.getFreeQty() == null ? 0 : report.getFreeQty();

          schemeDiscount += report.getSchemeDiscount() == null ? 0 : report.getSchemeDiscount();
          productDiscount += report.getProductDiscount() == null ? 0 : report.getProductDiscount();
          invoiceDiscount += report.getInvoiceDiscount() == null ? 0 : report.getInvoiceDiscount();
          returnValue += report.getReturnValue() == null ? 0 : report.getReturnValue();

        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return salesReturnReportList;
  }

  public void setSalesReturnReportList(List<SalesReturnReport> salesReturnReportList) {
    this.salesReturnReportList = salesReturnReportList;
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

  public void applyFilter(ValueChangeEvent event) {
    salesReturnReportList = null;
    columnNames = null;
    selectedColumns = null;
  }

  public void submitForm() {
    applyFilter(null);
  }

  public List<String> getColumnNames() {
    if (columnNames == null) {
      columnNames = new ArrayList<String>();
      if ("0".equals(filterParameters.getReportType())) {
        columnNames.add("-----");
      } else if ("1".equals(filterParameters.getReportType())) {
        columnNames.add("Invoice No");
        columnNames.add("Entry Date");
        columnNames.add("Customer Name");
        columnNames.add("GST No");
        columnNames.add("District");
        columnNames.add("Territory");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Return Value");

        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("IGST");
        columnNames.add("Net Amount");
        columnNames.add("Return Type");
      } else if ("2".equals(filterParameters.getReportType())) {
        columnNames.add("Customer Name");
        columnNames.add("GST No");

        columnNames.add("Invoice No");
        columnNames.add("Entry Date");

        columnNames.add("Product Name");
        columnNames.add("HSN Code");
        columnNames.add("Pack Type");
        columnNames.add("Batch No");
        columnNames.add("Expiry date");

        columnNames.add("Qty");
        columnNames.add("MRP");
        columnNames.add("Rate");

        columnNames.add("Goods Value");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("IGST");
        columnNames.add("Net Amount");
        columnNames.add("Return Type");
      }
      selectedColumns = new String[columnNames.size()];
      selectedColumns = columnNames.toArray(selectedColumns);
    }

    return columnNames;
  }

  public void setColumnNames(List<String> columnNames) {
    this.columnNames = columnNames;
  }

  public String[] getSelectedColumns() {
    return selectedColumns;
  }

  public void setSelectedColumns(String[] selectedColumns) {
    this.selectedColumns = selectedColumns;
  }

  public List getReportNames() {
    if (reportNames == null) {
      reportNames = new ArrayList();
      reportNames.add(0, "Blank");
      reportNames.add(1, "InvoiceWise");
      reportNames.add(2, "InvoiceProductWise");
    }
    return reportNames;
  }

  public void setReportNames(List reportNames) {
    this.reportNames = reportNames;
  }

  public void applyColumnFilter(AjaxBehaviorEvent event) {

    companyCustomerSalesRender.setRender(false);
    for (String selectedColumn : selectedColumns) {
      if (selectedColumn.equals("Customer Name")) {
        companyCustomerSalesRender.setSupplierName(true);
      } else if (selectedColumn.equals("Invoice No")) {
        companyCustomerSalesRender.setInvoiceNo(true);
      } else if (selectedColumn.equals("Entry Date")) {
        companyCustomerSalesRender.setEntryDate(true);
      } else if (selectedColumn.equals("Customer Name")) {
        companyCustomerSalesRender.setCustomerName(true);
      } else if (selectedColumn.equals("GST No")) {
        companyCustomerSalesRender.setGstNo(true);
      } else if (selectedColumn.equals("Product Name")) {
        companyCustomerSalesRender.setProductName(true);
      } else if (selectedColumn.equals("HSN Code")) {
        companyCustomerSalesRender.setHsnCode(true);
      } else if (selectedColumn.equals("Pack Type")) {
        companyCustomerSalesRender.setPackType(true);
      } else if (selectedColumn.equals("Batch No")) {
        companyCustomerSalesRender.setBatchNo(true);
      } else if (selectedColumn.equals("Expiry date")) {
        companyCustomerSalesRender.setExpiryDate(true);
      } else if (selectedColumn.equals("Qty")) {
        companyCustomerSalesRender.setQty(true);
      } else if (selectedColumn.equals("Qty Free")) {
        companyCustomerSalesRender.setQtyFree(true);
      } else if (selectedColumn.equals("MRP")) {
        companyCustomerSalesRender.setValueMrp(true);
      } else if (selectedColumn.equals("Rate")) {
        companyCustomerSalesRender.setValuePts(true);
      } else if (selectedColumn.equals("Rate Deviation")) {
        companyCustomerSalesRender.setRateDeviation(true);
      } else if (selectedColumn.equals("Goods Value")) {
        companyCustomerSalesRender.setGoodsValue(true);
      } else if (selectedColumn.equals("Scheme Discount")) {
        companyCustomerSalesRender.setSchemeDiscount(true);
      } else if (selectedColumn.equals("Scheme Deviation(")) {
        companyCustomerSalesRender.setSchemeDiscDeviation(true);
      } else if (selectedColumn.equals("Product Discount")) {
        companyCustomerSalesRender.setProductDiscount(true);
      } else if (selectedColumn.equals("Product Deviation")) {
        companyCustomerSalesRender.setProductDiscDeviation(true);
      } else if (selectedColumn.equals("Invoice Discount")) {
        companyCustomerSalesRender.setInvoiceDiscount(true);
      } else if (selectedColumn.equals("Invoice Deviation")) {
        companyCustomerSalesRender.setInvoiceDiscDeviation(true);
      } else if (selectedColumn.equals("IGST")) {
        companyCustomerSalesRender.setIgst(true);
      } else if (selectedColumn.equals("CGST")) {
        companyCustomerSalesRender.setCgst(true);
      } else if (selectedColumn.equals("SGST")) {
        companyCustomerSalesRender.setSgst(true);
      } else if (selectedColumn.equals("Net Amount")) {
        companyCustomerSalesRender.setNetAmount(true);
      } else if (selectedColumn.equals("Return Type")) {
        companyCustomerSalesRender.setReturnType(true);
      } else if (selectedColumn.equals("Return Value")) {
        companyCustomerSalesRender.setReturnValue(true);
      }
    }
  }

  public CompanyCustomerSalesRender getCompanyCustomerSalesRender() {
    return companyCustomerSalesRender;
  }

  public void setCompanyCustomerSalesRender(CompanyCustomerSalesRender companyCustomerSalesRender) {
    this.companyCustomerSalesRender = companyCustomerSalesRender;
  }

  /* private List genrateInvoiceWise(List<SalesReturnReport> salesReturnReportList) {
    HashMap<String, SalesReturnReport> salesMap = new HashMap<>();
    SalesReturnReport r;
    for (SalesReturnReport sr : salesReturnReportList) {
      //  if (salesMap != null) {
      r = salesMap.get(sr.getInvoiceNo());
      if (r != null) {
        if (sr.getSchemeDiscount() != null) {
          r.setSchemeDiscount(r.getSchemeDiscount() + sr.getSchemeDiscount());
        }
        if (sr.getProductDiscount() != null) {
          r.setProductDiscount(r.getProductDiscount() + sr.getProductDiscount());
        }
        if (sr.getInvoiceDiscount() != null) {
          r.setInvoiceDiscount(r.getInvoiceDiscount() + sr.getInvoiceDiscount());
        }
        if (sr.getIgst() != null) {
          if (r.getIgst() == null) {
            r.setIgst(0.00);
          }
          r.setIgst(r.getIgst() + sr.getIgst());
        }
        if (sr.getSgst() != null) {
          if (r.getSgst() == null) {
            r.setSgst(0.00);
          }
          r.setSgst(r.getSgst() + sr.getSgst());
        }
        if (sr.getCgst() != null) {
          if (r.getCgst() == null) {
            r.setCgst(0.00);
          }
          r.setCgst(r.getCgst() + sr.getCgst());
        }
        if (sr.getGstAmount() != null) {
          if (r.getGstAmount() == null) {
            r.setGstAmount(0.00);
          }
          r.setGstAmount(r.getGstAmount() + sr.getGstAmount());
        }

        r.setNetAmount(r.getNetAmount() + sr.getNetAmount());
        r.setGoodsValue(r.getGoodsValue() + sr.getGoodsValue());
        r.setReturnValue(r.getReturnValue() + sr.getReturnValue());

//          salesMap.get(sr.getInvoiceNo()).setSgst(sr.getSgst() + salesMap.get(sr.getInvoiceNo()).getSgst());
//          salesMap.get(sr.getInvoiceNo()).setIgst(sr.getIgst() + salesMap.get(sr.getInvoiceNo()).getIgst());
//          salesMap.get(sr.getInvoiceNo()).setCgst(sr.getCgst() + salesMap.get(sr.getInvoiceNo()).getCgst());
        //        salesMap.get(sr.getInvoiceNo()).setNetAmount(sr.getNetAmount() + salesMap.get(sr.getInvoiceNo()).getNetAmount());
      } else {

        salesMap.put(sr.getInvoiceNo(), sr);
      }
      // }
    }
    return new ArrayList(salesMap.values());
  } */
  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    if (accountGroup != null) {
      if (accountGroup.getId() != null) {
        filterParameters.setAccountGroup(accountGroup);
        filterParameters.setAccount(null);
        filterParameters.setCustomer(null);

      }
    }
  }

  public List<Account> accountAuto(String filter) {
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      MainView main = Jsf.getMain();
      try {
        return ScmLookupExtView.accountByAccountGroupProfile(main, filterParameters.getAccountGroup(), filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public List<Customer> customerAuto(String filter) {
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      return ScmLookupExtView.selectCustomerByAccountGroup(filterParameters.getAccountGroup(), filter);
    }
    return null;
  }

  public boolean isIntraState(Integer stateId) {
    if (company == null) {
      company = new Company();
      setCompany(UserRuntimeView.instance().getCompany());
    }

    Integer id = getCompany().getStateId().getId();
    if (stateId == id) {
      return true;
    }

    return false;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Double getNet() {
    return net;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public Double getGoodsValue() {
    return goodsValue;
  }

  public Double getIgst() {
    return igst;
  }

  public void setIgst(Double igst) {
    this.igst = igst;
  }

  public Double getCgst() {
    return cgst;
  }

  public void setCgst(Double cgst) {
    this.cgst = cgst;
  }

  public Double getSgst() {
    return sgst;
  }

  public void setSgst(Double sgst) {
    this.sgst = sgst;
  }

  public Double getQty() {
    return qty;
  }

  public void setQty(Double qty) {
    this.qty = qty;
  }

  public Double getSchemeDiscount() {
    return schemeDiscount;
  }

  public void setSchemeDiscount(Double schemeDiscount) {
    this.schemeDiscount = schemeDiscount;
  }

  public Double getProductDiscount() {
    return productDiscount;
  }

  public void setProductDiscount(Double productDiscount) {
    this.productDiscount = productDiscount;
  }

  public Double getInvoiceDiscount() {
    return invoiceDiscount;
  }

  public void setInvoiceDiscount(Double invoiceDiscount) {
    this.invoiceDiscount = invoiceDiscount;
  }

  public Double getGstAmt() {
    return gstAmt;
  }

  public void setGstAmt(Double gstAmt) {
    this.gstAmt = gstAmt;
  }

  public Double getBillAmt() {
    return billAmt;
  }

  public void setBillAmt(Double billAmt) {
    this.billAmt = billAmt;
  }

  public Double getFreeQty() {
    return freeQty;
  }

  public void setFreeQty(Double freeQty) {
    this.freeQty = freeQty;
  }

  public void openSalesReturnPopUp(Integer id) {
    Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesReturnView().replaceFirst("/scm", ""), id, id);
  }

  public AccountingTransactionDetailItem getAccountingTransactionDetailItem(Integer id) {
    AccountingTransactionDetailItem acc = new AccountingTransactionDetailItem();
    if (acc.getAccountingTransactionDetailId() == null) {
      acc.setAccountingTransactionDetailId(new AccountingTransactionDetail());
    }
    if (acc.getAccountingTransactionDetailId().getAccountingTransactionId() == null) {
      acc.getAccountingTransactionDetailId().setAccountingTransactionId(new AccountingTransaction());
    }
    acc.getAccountingTransactionDetailId().getAccountingTransactionId().setEntityId(id);
    return acc;
  }

  public Double getReturnValue() {
    return returnValue;
  }

  public void setReturnValue(Double returnValue) {
    this.returnValue = returnValue;
  }

  public void reset() {
    setCompany(null);
    setFilterParameters(null);
    setType(0);
  }

  public Double getSalesValue() {
    return salesValue;
  }

  public void setSalesValue(Double salesValue) {
    this.salesValue = salesValue;
  }

}
