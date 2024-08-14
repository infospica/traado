/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.reports.model.CompanyCustomerSales;
import spica.reports.model.CompanyCustomerSalesRender;
import spica.reports.model.FilterParameters;
import spica.reports.service.CompanyCustomerSalesService;
import spica.scm.domain.Customer;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Account;
import spica.sys.UserRuntimeView;
import spica.scm.view.ScmLookupExtView;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;
import java.text.DecimalFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.scm.domain.Brand;
import spica.scm.domain.Company;
import spica.scm.export.ExcelUtil;
import static spica.scm.export.ExcelUtil.decimalFormat;
import spica.sys.SystemRuntimeConfig;
import wawo.app.faces.Jsf;

/**
 *
 * @author krishna.vm
 */
@Named(value = "companyCustomerSalesView")
@ViewScoped
public class CompanyCustomerSalesView implements Serializable {

  private transient List<CompanyCustomerSales> companyCustomerSalesList;
  private transient List<CompanyCustomerSales> salesRegisterList;
  private transient FilterParameters filterParameters;
  private transient List reportNames;
  private String[] selectedColumns;
  private List<String> columnNames;
  private transient boolean renValue;
  private transient CompanyCustomerSalesRender companyCustomerSalesRender;
  private transient Double qty;
  private transient Double freeQty;
  private transient Double gstAmt;
  private transient Double billAmt;
  private transient Double taxableAmt;
  private transient Double goodsValue;
  private transient Double schemeDiscount;
  private transient Double productDiscount;
  private transient Double invoiceDiscount;
  private transient Double cashDiscount;
  private transient Double stateCess;
  private transient Double igst;
  private transient Double cgst;
  private transient Double sgst;
//  Services
  private transient Double serviceTaxableTotal;
  private transient Double serviceIgstTotal;
  private transient Double serviceCgstTotal;
  private transient Double serviceSgstTotal;
  private transient Double serviceCessTotal;
  private transient Double serviceGstTotal;
  private transient Double tcsApplicabletotal;
  private transient Double tcsNetTotal;
  private transient Double InvoiceAmountTotal;
  private transient Double mrp;
  private transient DecimalFormat df = new DecimalFormat("0.00");

  public List<CompanyCustomerSales> getCompanyCustomerSalesList(MainView main) {
    if (StringUtil.isEmpty(companyCustomerSalesList)) {
      try {
        if (filterParameters.getFromDate() == null) {
          filterParameters.setFromDate(UserRuntimeView.instance().getStartFiscalDate());
        }
        companyCustomerSalesRender = new CompanyCustomerSalesRender();
        companyCustomerSalesRender.setRender(true);
        companyCustomerSalesList = CompanyCustomerSalesService.getCompanyCustomerSalesList(main, UserRuntimeView.instance().getCompany(), getFilterParameters());
        gstAmt = 0.0;
        billAmt = 0.0;
        qty = 0.0;
        freeQty = 0.0;
        goodsValue = 0.0;
        schemeDiscount = 0.0;
        productDiscount = 0.0;
        invoiceDiscount = 0.0;
        cashDiscount = 0.0;
        stateCess = 0.0;
        igst = 0.0;
        cgst = 0.0;
        sgst = 0.0;
        mrp = 0.0;
        taxableAmt = 0.0;
        if (!companyCustomerSalesList.isEmpty() && companyCustomerSalesList.size() > 0) {
          for (CompanyCustomerSales list : companyCustomerSalesList) {
            if (list.getGstAmount() != null) {
              gstAmt += list.getGstAmount();
            }
            if (list.getNetAmount() != null) {
              billAmt += list.getNetAmount();
            }
            qty += list.getQty() == null ? 0 : list.getQty();
            freeQty += list.getQtyFree() == null ? 0 : list.getQtyFree();
            mrp += list.getValueMrp() == null ? 0 : list.getValueMrp();
            taxableAmt += list.getSalesValue() == null ? 0 : list.getSalesValue();
            /*taxableAmt -= list.getSchemeDiscount() == null ? 0 : list.getSchemeDiscount();
            taxableAmt -= list.getProductDiscount() == null ? 0 : list.getProductDiscount();
            taxableAmt -= list.getCashDiscount() == null ? 0 : list.getCashDiscount();
            taxableAmt -= list.getInvoiceDiscount() == null ? 0 : list.getInvoiceDiscount();*/
            goodsValue += list.getGoodsValue() != null ? list.getGoodsValue() : 0;
            schemeDiscount += list.getSchemeDiscount() == null ? 0 : list.getSchemeDiscount();
            productDiscount += list.getProductDiscount() == null ? 0 : list.getProductDiscount();
            invoiceDiscount += list.getInvoiceDiscount() == null ? 0 : list.getInvoiceDiscount();
            cashDiscount += list.getCashDiscount() == null ? 0 : list.getCashDiscount();
            stateCess += list.getStateCess() == null ? 0 : list.getStateCess();
            igst += list.getIgst() == null ? 0 : list.getIgst();
            cgst += list.getCgst() == null ? 0 : list.getCgst();
            sgst += list.getSgst() == null ? 0 : list.getSgst();

          }
        }

      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companyCustomerSalesList;
  }

  public void setCompanyCustomerSalesList(List<CompanyCustomerSales> companyCustomerSalesList) {
    if (getFilterParameters() != null && !getFilterParameters().isIncludeReturn()) {
      getFilterParameters().setIncludeSales(true);
    }
    this.companyCustomerSalesList = companyCustomerSalesList;
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
    companyCustomerSalesList = null;
    columnNames = null;
    selectedColumns = null;

  }

  public void submitForm() {
    applyFilter(null);
  }

  public void applyColumnFilter(AjaxBehaviorEvent event) {

    companyCustomerSalesRender.setRender(false);
    for (String selectedColumn : selectedColumns) {
      if (selectedColumn.equals("Invoice No")) {
        companyCustomerSalesRender.setInvoiceNo(true);
      } else if (selectedColumn.equals("Invoice Date")) {
        companyCustomerSalesRender.setInvoiceDate(true);
      } else if (selectedColumn.equals("Customer Name")) {
        companyCustomerSalesRender.setCustomerName(true);
      } else if (selectedColumn.equals("GST No")) {
        companyCustomerSalesRender.setGstNo(true);
      } else if (selectedColumn.equals("District Name")) {
        companyCustomerSalesRender.setDistrictName(true);
      } else if (selectedColumn.equals("Territory Name")) {
        companyCustomerSalesRender.setTerritoryName(true);
      } else if (selectedColumn.equals("Pin Code")) {
        companyCustomerSalesRender.setPinCode(true);
      } else if (selectedColumn.equals("Rep. Name")) {
        companyCustomerSalesRender.setRepName(true);
      } else if (selectedColumn.equals("Agent Name")) {
        companyCustomerSalesRender.setAgentName(true);
      } else if (selectedColumn.equals("MFR Code")) {
        companyCustomerSalesRender.setMfrCode(true);
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
        companyCustomerSalesRender.setRate(true);
      } else if (selectedColumn.equals("PTS")) {
        companyCustomerSalesRender.setValuePts(true);
      } else if (selectedColumn.equals("PTR")) {
        companyCustomerSalesRender.setValuePtr(true);
      } else if (selectedColumn.equals("Goods Value")) {
        companyCustomerSalesRender.setGoodsValue(true);
      } else if (selectedColumn.equals("Scheme Discount")) {
        companyCustomerSalesRender.setSchemeDiscount(true);
      } else if (selectedColumn.equals("Product Discount")) {
        companyCustomerSalesRender.setProductDiscount(true);
      } else if (selectedColumn.equals("Invoice Discount")) {
        companyCustomerSalesRender.setInvoiceDiscount(true);
      } else if (selectedColumn.equals("Cash Discount")) {
        companyCustomerSalesRender.setCashDiscount(true);
      } else if (selectedColumn.equals("Sales Value")) {
        companyCustomerSalesRender.setSalesValue(true);
      } else if (selectedColumn.equals("IGST")) {
        companyCustomerSalesRender.setIgst(true);
      } else if (selectedColumn.equals("State Cess")) {
        companyCustomerSalesRender.setStateCess(true);
      } else if (selectedColumn.equals("CGST")) {
        companyCustomerSalesRender.setCgst(true);
      } else if (selectedColumn.equals("SGST")) {
        companyCustomerSalesRender.setSgst(true);
      } else if (selectedColumn.equals("GST Amt.")) {
        companyCustomerSalesRender.setGstAmount(true);
      } else if (selectedColumn.equals("Service Taxable Value")) {
        companyCustomerSalesRender.setSalesValue(true);
      } else if (selectedColumn.equals("Service IGST")) {
        companyCustomerSalesRender.setIgst(true);
      } else if (selectedColumn.equals("Service Cess")) {
        companyCustomerSalesRender.setStateCess(true);
      } else if (selectedColumn.equals("Service CGST")) {
        companyCustomerSalesRender.setCgst(true);
      } else if (selectedColumn.equals("Service SGST")) {
        companyCustomerSalesRender.setSgst(true);
      } else if (selectedColumn.equals("Service GST Amt.")) {
        companyCustomerSalesRender.setGstAmount(true);
      } else if (selectedColumn.equals("Tcs Applicable Amount")) {
        companyCustomerSalesRender.setNetAmount(true);
      } else if (selectedColumn.equals("Tcs Net value")) {
        companyCustomerSalesRender.setGstAmount(true);
      } else if (selectedColumn.equals("Bill Amt.")) {
        companyCustomerSalesRender.setNetAmount(true);
      }
    }

  }

  public boolean getRenValue() {
    return renValue;
  }

  public List getReportNames() {
    if (reportNames == null) {
      reportNames = new ArrayList();
      reportNames.add(0, "Blank");
      reportNames.add(1, "InvoiceWise");
      reportNames.add(2, "InvoiceProductWise");
      reportNames.add(3, "CustomerWise");
      reportNames.add(4, "CustomerInvoiceWise");
      reportNames.add(5, "CustomerProductWise");
      reportNames.add(6, "TerritoryWise");
      reportNames.add(7, "TerritoryCustomerWise");
      reportNames.add(8, "TerritoryCustomerProductWise");
    }
    return reportNames;
  }

  public String[] getSelectedColumns() {
    return selectedColumns;
  }

  public void setSelectedColumns(String[] selectedColumns) {
    this.selectedColumns = selectedColumns;
  }

  public List<String> getColumnNames() {
    if (columnNames == null) {
      columnNames = new ArrayList<String>();
      if ("0".equals(filterParameters.getReportType())) {
        columnNames.add("-----");
      } else if ("1".equals(filterParameters.getReportType())) {
        columnNames.add("Invoice No");
        columnNames.add("Invoice Date");

        columnNames.add("Customer Name");
        columnNames.add("GST No");
        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("State Cess");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("2".equals(filterParameters.getReportType())) {
        columnNames.add("Invoice No");
        columnNames.add("Invoice Date");

        columnNames.add("Customer Name");
        columnNames.add("GST No");

        columnNames.add("MFR Code");
        columnNames.add("Product Name");
        columnNames.add("HSN Code");
        columnNames.add("Pack Type");
        columnNames.add("Batch No");
        columnNames.add("Expiry date");

        columnNames.add("Qty");
        columnNames.add("Qty Free");
        columnNames.add("MRP");
        columnNames.add("Rate");
        columnNames.add("PTS");
        columnNames.add("PTR");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("State Cess");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("3".equals(filterParameters.getReportType())) {
        columnNames.add("Invoice No");
        columnNames.add("Invoice Date");

        columnNames.add("Customer Name");
        columnNames.add("GST No");
        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("State Cess");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("4".equals(filterParameters.getReportType())) {

        columnNames.add("Customer Name");
        columnNames.add("GST No");
        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("Invoice No");
        columnNames.add("Invoice Date");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("State Cess");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("5".equals(filterParameters.getReportType())) {

        columnNames.add("Customer Name");
        columnNames.add("GST No");
        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("MFR Code");
        columnNames.add("Product Name");
        columnNames.add("HSN Code");
        columnNames.add("Pack Type");
        columnNames.add("Batch No");
        columnNames.add("Expiry date");

        columnNames.add("Qty");
        columnNames.add("Qty Free");
        columnNames.add("MRP");
        columnNames.add("Rate");
        columnNames.add("PTS");
        columnNames.add("PTR");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("State Cess");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("6".equals(filterParameters.getReportType())) {

        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("State Cess");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("7".equals(filterParameters.getReportType())) {

        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("Customer Name");
        columnNames.add("GST No");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("State Cess");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("8".equals(filterParameters.getReportType())) {

        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("Customer Name");
        columnNames.add("GST No");

        columnNames.add("MFR Code");
        columnNames.add("Product Name");
        columnNames.add("HSN Code");
        columnNames.add("Pack Type");
        columnNames.add("Batch No");
        columnNames.add("Expiry date");

        columnNames.add("Qty");
        columnNames.add("Qty Free");
        columnNames.add("MRP");
        columnNames.add("Rate");
        columnNames.add("PTS");
        columnNames.add("PTR");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("State Cess");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else {
        columnNames.add("Invoice No");
        columnNames.add("Invoice Date");

        columnNames.add("Customer Name");
        columnNames.add("GST No");
        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("State Cess");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Service Taxable Value");
        columnNames.add("Service Cess");
        columnNames.add("Service IGST");
        columnNames.add("Service CGST");
        columnNames.add("Service SGST");
        columnNames.add("Service GST Amt.");
        columnNames.add("Invoice Amount");
        columnNames.add("TCS Applicable Amount");
        columnNames.add("TCS Net Value");
        columnNames.add("Bill Amt.");
      }
      selectedColumns = new String[columnNames.size()];
      selectedColumns = columnNames.toArray(selectedColumns);
    }

    return columnNames;
  }

  public void setColumnNames(List<String> columnNames) {
    this.columnNames = columnNames;
  }

  public CompanyCustomerSalesRender getCompanyCustomerSalesRender() {
    return companyCustomerSalesRender;
  }

  public void setCompanyCustomerSalesRender(CompanyCustomerSalesRender companyCustomerSalesRender) {
    this.companyCustomerSalesRender = companyCustomerSalesRender;
  }

  public List<Customer> customerAuto(String filter) {
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      return ScmLookupExtView.selectCustomerByAccountGroup(filterParameters.getAccountGroup(), filter);
    }
    return null;
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

  public Double getGstAmt() {
    if (gstAmt == null) {
      gstAmt = 0.0;
    }
    return gstAmt;
  }

  public void setGstAmt(Double gstAmt) {
    this.gstAmt = gstAmt;
  }

  public Double getBillAmt() {
    if (billAmt == null) {
      billAmt = 0.0;
    }
    return billAmt;
  }

  public void setBillAmt(Double billAmt) {
    this.billAmt = billAmt;
  }

  public Double getTaxableAmt() {
    if (taxableAmt == null) {
      taxableAmt = 0.0;
    }
    return taxableAmt;
  }

  public void setTaxableAmt(Double taxableAmt) {
    this.taxableAmt = taxableAmt;
  }

  public Double getGoodsValue() {
    return goodsValue;
  }

  public Double getSchemeDiscount() {
    return schemeDiscount;
  }

  public Double getProductDiscount() {
    return productDiscount;
  }

  public Double getInvoiceDiscount() {
    return invoiceDiscount;
  }

  public Double getCashDiscount() {
    return cashDiscount;
  }

  public Double getStateCess() {
    return stateCess;
  }

  public Double getIgst() {
    return igst;
  }

  public Double getCgst() {
    return cgst;
  }

  public Double getSgst() {
    return sgst;
  }

  public Double getQty() {
    return qty;
  }

  public Double getMrp() {
    return mrp;
  }

  public Double getFreeQty() {
    return freeQty;
  }

  public void openSalesInvoicePopUp(Integer id) {
    Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesInvoiceView().replaceFirst("/scm", ""), id, id);
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

  public List<Brand> brandAuto(MainView main, String filter) {
    try {
      List<Brand> brandList = CompanyCustomerSalesService.brandList(main, UserRuntimeView.instance().getCompany(), filter);
      if (brandList != null) {
        brandList.add(0, new Brand(0, "---"));
      }
      return brandList;
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void branSelectEvent() {
    if (filterParameters.getBrand() != null) {
      filterParameters.setAccountGroup(null);
      filterParameters.setAccount(null);
      filterParameters.setCustomer(null);
    }
  }

  public void export(MainView main) {
    try {
      createSalesReport(companyCustomerSalesList, salesRegisterList, filterParameters, UserRuntimeView.instance().getCompany());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void createSalesReport(List<CompanyCustomerSales> companyCustomerSalesList, List<CompanyCustomerSales> salesRegisterList, FilterParameters filterParameters, Company company) throws IOException {
    String name = "SALES_";
    String[] arr = null;
    if (filterParameters.getReportType().equals("1")) {
      name += "INVOICE_WISE";
      arr = new String[]{"INVOICE NO", "INVOICE TYPE", "INVOICE DATE", "CUSTOMER NAME", "GST", "DISTRICT", "TERRITORY", "GOODS VALUE", "SCHEME DISCOUNT", "PRODUCT DISCOUNT",
        "INVOICE DISCOUNT", "CASH DISCOUNT", "SALES VALUE", "CESS", "IGST", "CGST", "SGST", "GST AMOUNT", "BILL AMOUNT"};
    } else if (filterParameters.getReportType().equals("2")) {
      name += "INVOICE_PRODUCT_WISE";
      arr = new String[]{"INVOICE NO", "INVOICE TYPE", "INVOICE DATE", "CUSTOMER NAME", "GST", "DISTRICT", "PRODUCT CLASSIFICATION", "MFR",
        "PRODUCT NAME", "HSN CODE", "TAX %", "PACK TYPE", "BATCH",
        "EXPIRY DATE", "QTY", "QTY FREE", "PURCHASE RATE", "LANDING RATE", "MRP", "RATE", "PTS", "PTR", "GOODS VALUE", "SCHEME DISCOUNT", "PRODUCT DISCOUNT",
        "INVOICE DISCOUNT", "CASH DISCOUNT", "SALES VALUE", "CESS", "IGST", "CGST", "SGST", "GST AMOUNT", "BILL AMOUNT"};
    } else if (filterParameters.getReportType().equals("3")) {
      name += "CUSTOMER_WISE";
      arr = new String[]{"CUSTOMER NAME", "GST", "DISTRICT", "TERRITORY", "GOODS VALUE", "SCHEME DISCOUNT", "PRODUCT DISCOUNT",
        "INVOICE DISCOUNT", "CASH DISCOUNT", "SALES VALUE", "CESS", "IGST", "CGST", "SGST", "GST AMOUNT", "BILL AMOUNT"};
    } else if (filterParameters.getReportType().equals("4")) {
      name += "CUSTOMER_INVOICE_WISE";
      arr = new String[]{"CUSTOMER NAME", "GST", "DISTRICT", "TERRITORY", "INVOICE NO", "INVOICE DATE", "GOODS VALUE", "SCHEME DISCOUNT", "PRODUCT DISCOUNT",
        "INVOICE DISCOUNT", "CASH DISCOUNT", "SALES VALUE", "CESS", "IGST", "CGST", "SGST", "GST AMOUNT", "BILL AMOUNT"};
    } else if (filterParameters.getReportType().equals("5")) {
      name += "CUSTOMER_PRODUCT_WISE";
      arr = new String[]{"CUSTOMER NAME", "GST", "DISTRICT", "TERRITORY", "MFR", "PRODUCT NAME", "HSN CODE", "TAX %", "PACK TYPE", "BATCH",
        "EXPIRY DATE", "QTY", "QTY FREE", "MRP", "RATE", "PTS", "PTR", "GOODS VALUE", "SCHEME DISCOUNT", "PRODUCT DISCOUNT",
        "INVOICE DISCOUNT", "CASH DISCOUNT", "SALES VALUE", "CESS", "IGST", "CGST", "SGST", "GST AMOUNT", "BILL AMOUNT"};
    } else if (filterParameters.getReportType().equals("6")) {
      name += "TERRITORY_WISE";
      arr = new String[]{"DISTRICT", "TERRITORY", "GOODS VALUE", "SCHEME DISCOUNT", "PRODUCT DISCOUNT",
        "INVOICE DISCOUNT", "CASH DISCOUNT", "SALES VALUE", "CESS", "IGST", "CGST", "SGST", "GST AMOUNT", "BILL AMOUNT"};
    } else if (filterParameters.getReportType().equals("7")) {
      name += "TERRITORY_CUSTOMER_WISE";
      arr = new String[]{"DISTRICT", "TERRITORY", "CUSTOMER NAME", "GST", "GOODS VALUE", "SCHEME DISCOUNT", "PRODUCT DISCOUNT",
        "INVOICE DISCOUNT", "CASH DISCOUNT", "SALES VALUE", "CESS", "IGST", "CGST", "SGST", "GST AMOUNT", "BILL AMOUNT"};
    } else if (filterParameters.getReportType().equals("8")) {
      name += "TERRITORY_CUSTOMER_PRODUCT_WISE";
      arr = new String[]{"DISTRICT", "TERRITORY", "CUSTOMER NAME", "GST", "MFR", "PRODUCT NAME", "HSN CODE", "TAX %", "PACK TYPE", "BATCH",
        "EXPIRY DATE", "QTY", "QTY FREE", "MRP", "RATE", "PTS", "PTR", "GOODS VALUE", "SCHEME DISCOUNT", "PRODUCT DISCOUNT",
        "INVOICE DISCOUNT", "CASH DISCOUNT", "SALES VALUE", "CESS", "IGST", "CGST", "SGST", "GST AMOUNT", "BILL AMOUNT"};
    } else {
      companyCustomerSalesList = new ArrayList<>();
      companyCustomerSalesList = salesRegisterList;
      name += "REGISTER";
      arr = new String[]{"INVOICE NO", "INVOICE TYPE", "INVOICE DATE", "CUSTOMER NAME", "GST", "DISTRICT", "TERRITORY", "GOODS VALUE", "SCHEME DISCOUNT", "PRODUCT DISCOUNT",
        "INVOICE DISCOUNT", "CASH DISCOUNT", "SALES VALUE", "CESS", "IGST", "CGST", "SGST", "GST AMOUNT", "SERVICE TAXABLE VALUE", "SERVICE CESS", "SERVICE IGST",
        "SERVICE CGST", "SERVICE SGST", "SERVICE GST AMOUNT", "INVOICE AMOUNT", "TCS APPLICABLE AMOUNT", "TCS NET AMOUNT",
        "BILL AMOUNT"};
    }
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      XSSFRow row = null;

//    XSSFFont font = workbook.createFont();
//    font.setFontHeightInPoints((short) 10);
//    font.setFontName("Arial");
//    font.setColor(IndexedColors.BLACK.getIndex());
//    font.setBold(true);
//    font.setItalic(false);
      //CellStyle styletotal = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
      // Create cell style 
      CellStyle style = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      CellStyle styleNormalRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.LEFT);
      CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      CellStyle styleNormalRightRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.RIGHT);
      CellStyle styleNormalRightBold = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
      CellStyle styleNormalRightRedBold = ExcelUtil.styleRedBold(workbook, HorizontalAlignment.RIGHT);
      CellStyle styleNormalCenter = workbook.createCellStyle();
      styleNormalCenter.setAlignment(HorizontalAlignment.CENTER);

      CellStyle colHead = ExcelUtil.styleBold(workbook, HorizontalAlignment.LEFT);
      colHead.setWrapText(true);
      colHead.setVerticalAlignment(VerticalAlignment.CENTER);
      Cell cell;
      int rowId = ExcelUtil.createHeader(workbook, sheet, company, filterParameters.getAccountGroup(),
              filterParameters.getAccount(), filterParameters.getCustomer());
      int cellId = 0;
//    ExcelUtil.createAccountGroupHeader(row, sheet, rowId, colHead, groupName);
//     List

      row = sheet.createRow(rowId++);
      row.setRowStyle(ExcelUtil.setFillColor(ExcelUtil.FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      int size = 0;
      cellId = 0;
      List<String> headerList = new ArrayList<>();
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        headerList.add(obj);
        cell.setCellStyle(ExcelUtil.setFillColor(ExcelUtil.FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size);
        size++;
      }
      if (companyCustomerSalesList != null && !companyCustomerSalesList.isEmpty()) {
        for (CompanyCustomerSales list : companyCustomerSalesList) {
          cellId = 0;
          row = sheet.createRow(rowId++);
          row.setRowStyle(style);
          list.setSalesOrReturn(list.getSalesOrReturn() != null ? list.getSalesOrReturn() : 1);

          if (!filterParameters.getReportType().equals("4")) {
            if (headerList.contains("INVOICE NO")) {
              cell = row.createCell(headerList.indexOf("INVOICE NO"));
              cell.setCellValue(list.getInvoiceNo());
              if (list.getSalesOrReturn().intValue() == 1) {
                cell.setCellStyle(style);
              } else {
                cell.setCellStyle(styleNormalRed);
              }
            }
            if (headerList.contains("INVOICE TYPE")) {
              cell = row.createCell(headerList.indexOf("INVOICE TYPE"));
              cell.setCellValue(list.getInvoiceType());
              if (list.getSalesOrReturn().intValue() == 1) {
                cell.setCellStyle(style);
              } else {
                cell.setCellStyle(styleNormalRed);
              }
            }
            if (headerList.contains("INVOICE DATE")) {
              cell = row.createCell(headerList.indexOf("INVOICE DATE"));
              if (list.getInvoiceDate() != null) {
                cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(list.getInvoiceDate()));
              }
              if (list.getSalesOrReturn().intValue() == 1) {
                cell.setCellStyle(style);
              } else {
                cell.setCellStyle(styleNormalRed);
              }
            }
          }

          if (!filterParameters.getReportType().equals("7") || !filterParameters.getReportType().equals("8")) {
            if (headerList.contains("CUSTOMER NAME")) {
              cell = row.createCell(headerList.indexOf("CUSTOMER NAME"));
              cell.setCellValue(list.getCustomerName());
              if (list.getSalesOrReturn().intValue() == 1) {
                cell.setCellStyle(style);
              } else {
                cell.setCellStyle(styleNormalRed);
              }
            }

            if (headerList.contains("GST")) {
              cell = row.createCell(headerList.indexOf("GST"));
              cell.setCellValue(list.getGstNo());
              if (list.getSalesOrReturn().intValue() == 1) {
                cell.setCellStyle(style);
              } else {
                cell.setCellStyle(styleNormalRed);
              }
            }
          }

          if (headerList.contains("DISTRICT")) {
            cell = row.createCell(headerList.indexOf("DISTRICT"));
            cell.setCellValue(list.getDistrictName());
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("TERRITORY")) {
            cell = row.createCell(headerList.indexOf("TERRITORY"));
            cell.setCellValue(list.getTerritoryName());
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (filterParameters.getReportType().equals("7") || filterParameters.getReportType().equals("8")) {
            if (headerList.contains("CUSTOMER NAME")) {
              cell = row.createCell(headerList.indexOf("CUSTOMER NAME"));
              cell.setCellValue(list.getCustomerName());
              if (list.getSalesOrReturn().intValue() == 1) {
                cell.setCellStyle(style);
              } else {
                cell.setCellStyle(styleNormalRed);
              }
            }

            if (headerList.contains("GST")) {
              cell = row.createCell(headerList.indexOf("GST"));
              cell.setCellValue(list.getGstNo());
              if (list.getSalesOrReturn().intValue() == 1) {
                cell.setCellStyle(style);
              } else {
                cell.setCellStyle(styleNormalRed);
              }
            }
          }

          if (filterParameters.getReportType().equals("4")) {
            if (headerList.contains("INVOICE NO")) {
              cell = row.createCell(headerList.indexOf("INVOICE NO"));
              cell.setCellValue(list.getInvoiceNo());
              if (list.getSalesOrReturn().intValue() == 1) {
                cell.setCellStyle(style);
              } else {
                cell.setCellStyle(styleNormalRed);
              }
            }
            if (headerList.contains("INVOICE TYPE")) {
              cell = row.createCell(headerList.indexOf("INVOICE TYPE"));
              cell.setCellValue(list.getInvoiceType());
              if (list.getSalesOrReturn().intValue() == 1) {
                cell.setCellStyle(style);
              } else {
                cell.setCellStyle(styleNormalRed);
              }
            }
            if (headerList.contains("INVOICE DATE")) {
              cell = row.createCell(headerList.indexOf("INVOICE DATE"));
              if (list.getInvoiceDate() != null) {
                cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(list.getInvoiceDate()));
              }
              if (list.getSalesOrReturn().intValue() == 1) {
                cell.setCellStyle(style);
              } else {
                cell.setCellStyle(styleNormalRed);
              }
            }
          }
          if (headerList.contains("PRODUCT CLASSIFICATION")) {
            cell = row.createCell(headerList.indexOf("PRODUCT CLASSIFICATION"));
            if (list.getProductClassification() != null) {
              cell.setCellValue(list.getProductClassification());
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }
          if (headerList.contains("MFR")) {
            cell = row.createCell(headerList.indexOf("MFR"));
            cell.setCellValue(list.getMfrCode());
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("PRODUCT NAME")) {
            cell = row.createCell(headerList.indexOf("PRODUCT NAME"));
            cell.setCellValue(list.getProductName());
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("HSN CODE")) {
            cell = row.createCell(headerList.indexOf("HSN CODE"));
            cell.setCellValue(list.getHsnCode());
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("TAX %")) {
            cell = row.createCell(headerList.indexOf("TAX %"));
            cell.setCellValue(list.getTaxPerc());
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("PACK TYPE")) {
            cell = row.createCell(headerList.indexOf("PACK TYPE"));
            cell.setCellValue(list.getPackType());
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("BATCH")) {
            cell = row.createCell(headerList.indexOf("BATCH"));
            cell.setCellValue(list.getBatchNo());
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("EXPIRY DATE")) {
            cell = row.createCell(headerList.indexOf("EXPIRY DATE"));
            if (list.getExpiryDate() != null) {
              cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(list.getExpiryDate()));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("QTY")) {
            cell = row.createCell(headerList.indexOf("QTY"));
            if (list.getQty() != null) {
              cell.setCellValue(list.getQty());
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("QTY FREE")) {
            cell = row.createCell(headerList.indexOf("QTY FREE"));
            if (list.getQtyFree() != null) {
              cell.setCellValue(list.getQtyFree());
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }
          if (headerList.contains("PURCHASE RATE")) {
            cell = row.createCell(headerList.indexOf("PURCHASE RATE"));
            if (list.getPurchaseValue() != null) {
              cell.setCellValue(list.getPurchaseValue());
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }
          if (headerList.contains("LANDING RATE")) {
            cell = row.createCell(headerList.indexOf("LANDING RATE"));
            if (list.getLandingRate() != null) {
              cell.setCellValue(list.getLandingRate());
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }
          if (headerList.contains("MRP")) {
            cell = row.createCell(headerList.indexOf("MRP"));
            cell.setCellValue(list.getValueMrp());
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("RATE")) {
            cell = row.createCell(headerList.indexOf("RATE"));
            if (list.getRate() != null) {
              cell.setCellValue(list.getRate());
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("PTS")) {
            cell = row.createCell(headerList.indexOf("PTS"));
            if (list.getValuePts() != null) {
              cell.setCellValue(list.getValuePts());
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("PTR")) {
            cell = row.createCell(headerList.indexOf("PTR"));
            if (list.getValuePtr() != null) {
              cell.setCellValue(list.getValuePtr());
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(style);
            } else {
              cell.setCellStyle(styleNormalRed);
            }
          }

          if (headerList.contains("GOODS VALUE")) {
            cell = row.createCell(headerList.indexOf("GOODS VALUE"));
            if (list.getGoodsValue() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getGoodsValue())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("SCHEME DISCOUNT")) {
            cell = row.createCell(headerList.indexOf("SCHEME DISCOUNT"));
            if (list.getSchemeDiscount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getSchemeDiscount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("PRODUCT DISCOUNT")) {
            cell = row.createCell(headerList.indexOf("PRODUCT DISCOUNT"));
            if (list.getProductDiscount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getProductDiscount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("INVOICE DISCOUNT")) {
            cell = row.createCell(headerList.indexOf("INVOICE DISCOUNT"));
            if (list.getInvoiceDiscount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getInvoiceDiscount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("CASH DISCOUNT")) {
            cell = row.createCell(headerList.indexOf("CASH DISCOUNT"));
            if (list.getCashDiscount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getCashDiscount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("SALES VALUE")) {
            cell = row.createCell(headerList.indexOf("SALES VALUE"));
            if (list.getSalesValue() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getSalesValue())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRightBold);
            } else {
              cell.setCellStyle(styleNormalRightRedBold);
            }
          }

          if (headerList.contains("CESS")) {
            cell = row.createCell(headerList.indexOf("CESS"));
            if (list.getStateCess() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getStateCess())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRightBold);
            } else {
              cell.setCellStyle(styleNormalRightRedBold);
            }
          }

          if (headerList.contains("IGST")) {
            cell = row.createCell(headerList.indexOf("IGST"));
            if (list.getIgst() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getIgst())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("CGST")) {
            cell = row.createCell(headerList.indexOf("CGST"));
            if (list.getCgst() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getCgst())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("SGST")) {
            cell = row.createCell(headerList.indexOf("SGST"));
            if (list.getSgst() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getSgst())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }
          if (headerList.contains("GST AMOUNT")) {
            cell = row.createCell(headerList.indexOf("GST AMOUNT"));
            if (list.getGstAmount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getGstAmount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRightBold);
            } else {
              cell.setCellStyle(styleNormalRightRedBold);
            }
          }
          if (headerList.contains("SERVICE TAXABLE VALUE")) {
            cell = row.createCell(headerList.indexOf("SERVICE TAXABLE VALUE"));
            if (list.getServiceTaxableAmount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getServiceTaxableAmount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRightBold);
            } else {
              cell.setCellStyle(styleNormalRightRedBold);
            }
          }

          if (headerList.contains("SERVICE CESS")) {
            cell = row.createCell(headerList.indexOf("SERVICE CESS"));
            if (list.getServiceCessAmount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getServiceCessAmount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRightBold);
            } else {
              cell.setCellStyle(styleNormalRightRedBold);
            }
          }

          if (headerList.contains("SERVICE IGST")) {
            cell = row.createCell(headerList.indexOf("SERVICE IGST"));
            if (list.getServiceIgst() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getServiceIgst())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("SERVICE CGST")) {
            cell = row.createCell(headerList.indexOf("SERVICE CGST"));
            if (list.getServiceCgst() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getServiceCgst())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("SERVICE SGST")) {
            cell = row.createCell(headerList.indexOf("SERVICE SGST"));
            if (list.getServiceSgst() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getServiceSgst())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }
          if (headerList.contains("SERVICE GST AMOUNT")) {
            cell = row.createCell(headerList.indexOf("SERVICE GST AMOUNT"));
            if (list.getServiceGstAmount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getServiceGstAmount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRightBold);
            } else {
              cell.setCellStyle(styleNormalRightRedBold);
            }
          }

          if (headerList.contains("INVOICE AMOUNT")) {
            cell = row.createCell(headerList.indexOf("INVOICE AMOUNT"));
            if (list.getInvoiceAmount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getInvoiceAmount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRightBold);
            } else {
              cell.setCellStyle(styleNormalRightRedBold);
            }
          }
          if (headerList.contains("TCS APPLICABLE AMOUNT")) {
            cell = row.createCell(headerList.indexOf("TCS APPLICABLE AMOUNT"));
            if (list.getTcsApplicableAmount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTcsApplicableAmount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRight);
            } else {
              cell.setCellStyle(styleNormalRightRed);
            }
          }

          if (headerList.contains("TCS NET AMOUNT")) {
            cell = row.createCell(headerList.indexOf("TCS NET AMOUNT"));
            if (list.getTcsNetAmount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTcsNetAmount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRightBold);
            } else {
              cell.setCellStyle(styleNormalRightRedBold);
            }
          }
          if (headerList.contains("BILL AMOUNT")) {
            cell = row.createCell(headerList.indexOf("BILL AMOUNT"));
            if (list.getNetAmount() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getNetAmount())));
            }
            if (list.getSalesOrReturn().intValue() == 1) {
              cell.setCellStyle(styleNormalRightBold);
            } else {
              cell.setCellStyle(styleNormalRightRedBold);
            }
          }
        }
      }
      cellId = 0;
      row = sheet.createRow(rowId);
      row.setRowStyle(ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT));
      cell = row.createCell(cellId++);
      cell.setCellValue("Total");
      sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 2));
      cellId += 2;
      if (headerList.contains("QTY")) {
        cell = row.createCell(headerList.indexOf("QTY"));
        cell.setCellValue(Double.parseDouble(decimalFormat.format(qty)));
      }
      if (headerList.contains("QTY FREE")) {
        cell = row.createCell(headerList.indexOf("QTY FREE"));
        cell.setCellValue(Double.parseDouble(decimalFormat.format(freeQty)));
      }
      cell = row.createCell(headerList.indexOf("GOODS VALUE"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(goodsValue)));

      cell = row.createCell(headerList.indexOf("SCHEME DISCOUNT"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(schemeDiscount)));

      cell = row.createCell(headerList.indexOf("PRODUCT DISCOUNT"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(productDiscount)));

      cell = row.createCell(headerList.indexOf("INVOICE DISCOUNT"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(invoiceDiscount)));

      cell = row.createCell(headerList.indexOf("CASH DISCOUNT"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(cashDiscount)));

      cell = row.createCell(headerList.indexOf("SALES VALUE"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(taxableAmt)));

      cell = row.createCell(headerList.indexOf("CESS"));
      if (stateCess != null) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(stateCess)));
      }

      cell = row.createCell(headerList.indexOf("IGST"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(igst)));

      cell = row.createCell(headerList.indexOf("CGST"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(cgst)));

      cell = row.createCell(headerList.indexOf("SGST"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(sgst)));

      cell = row.createCell(headerList.indexOf("GST AMOUNT"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(gstAmt)));

      if (headerList.contains("SERVICE TAXABLE VALUE")) {
        cell = row.createCell(headerList.indexOf("SERVICE TAXABLE VALUE"));
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceTaxableTotal)));

        cell = row.createCell(headerList.indexOf("SERVICE CESS"));
        if (serviceCessTotal != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceCessTotal)));
        }

        cell = row.createCell(headerList.indexOf("SERVICE IGST"));
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceIgstTotal)));

        cell = row.createCell(headerList.indexOf("SERVICE CGST"));
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceCgstTotal)));

        cell = row.createCell(headerList.indexOf("SERVICE SGST"));
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceSgstTotal)));

        cell = row.createCell(headerList.indexOf("SERVICE GST AMOUNT"));
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceGstTotal)));

        cell = row.createCell(headerList.indexOf("INVOICE AMOUNT"));
        cell.setCellValue(Double.parseDouble(decimalFormat.format(InvoiceAmountTotal)));

        cell = row.createCell(headerList.indexOf("TCS APPLICABLE AMOUNT"));
        cell.setCellValue(Double.parseDouble(decimalFormat.format(tcsApplicabletotal)));

        cell = row.createCell(headerList.indexOf("TCS NET AMOUNT"));
        cell.setCellValue(Double.parseDouble(decimalFormat.format(tcsNetTotal)));
      }
      cell = row.createCell(headerList.indexOf("BILL AMOUNT"));
      cell.setCellValue(Double.parseDouble(decimalFormat.format(billAmt)));
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  public List<CompanyCustomerSales> getSalesRegisterList(MainView main) {
    if (StringUtil.isEmpty(salesRegisterList)) {
      try {
        if (filterParameters.getFromDate() == null) {
          filterParameters.setFromDate(UserRuntimeView.instance().getStartFiscalDate());
        }
        companyCustomerSalesRender = new CompanyCustomerSalesRender();
        companyCustomerSalesRender.setRender(true);
        salesRegisterList = CompanyCustomerSalesService.getSalesRegisterList(main, UserRuntimeView.instance().getCompany(), getFilterParameters());
        gstAmt = 0.0;
        billAmt = 0.0;
        qty = 0.0;
        freeQty = 0.0;
        goodsValue = 0.0;
        schemeDiscount = 0.0;
        productDiscount = 0.0;
        invoiceDiscount = 0.0;
        cashDiscount = 0.0;
        stateCess = 0.0;
        igst = 0.0;
        cgst = 0.0;
        sgst = 0.0;
        mrp = 0.0;
        taxableAmt = 0.0;
        serviceTaxableTotal = 0.0;
        serviceIgstTotal = 0.0;
        serviceCgstTotal = 0.0;
        serviceSgstTotal = 0.0;
        serviceCessTotal = 0.0;
        serviceGstTotal = 0.0;
        tcsApplicabletotal = 0.0;
        tcsNetTotal = 0.0;
        InvoiceAmountTotal = 0.0;
        if (!salesRegisterList.isEmpty() && salesRegisterList.size() > 0) {
          for (CompanyCustomerSales list : salesRegisterList) {
            if (list.getGstAmount() != null) {
              gstAmt += list.getGstAmount();
            }
            if (list.getNetAmount() != null) {
              billAmt += list.getNetAmount();
            }
            qty += list.getQty() == null ? 0 : list.getQty();
            freeQty += list.getQtyFree() == null ? 0 : list.getQtyFree();
            mrp += list.getValueMrp() == null ? 0 : list.getValueMrp();
            taxableAmt += list.getSalesValue() == null ? 0 : list.getSalesValue();
            /*taxableAmt -= list.getSchemeDiscount() == null ? 0 : list.getSchemeDiscount();
            taxableAmt -= list.getProductDiscount() == null ? 0 : list.getProductDiscount();
            taxableAmt -= list.getCashDiscount() == null ? 0 : list.getCashDiscount();
            taxableAmt -= list.getInvoiceDiscount() == null ? 0 : list.getInvoiceDiscount();*/
            goodsValue += list.getGoodsValue() != null ? list.getGoodsValue() : 0;
            schemeDiscount += list.getSchemeDiscount() == null ? 0 : list.getSchemeDiscount();
            productDiscount += list.getProductDiscount() == null ? 0 : list.getProductDiscount();
            invoiceDiscount += list.getInvoiceDiscount() == null ? 0 : list.getInvoiceDiscount();
            cashDiscount += list.getCashDiscount() == null ? 0 : list.getCashDiscount();
            stateCess += list.getStateCess() == null ? 0 : list.getStateCess();
            igst += list.getIgst() == null ? 0 : list.getIgst();
            cgst += list.getCgst() == null ? 0 : list.getCgst();
            sgst += list.getSgst() == null ? 0 : list.getSgst();
            serviceTaxableTotal += list.getServiceTaxableAmount() == null ? 0 : list.getServiceTaxableAmount();
            serviceIgstTotal += list.getServiceIgst() == null ? 0 : list.getServiceIgst();
            serviceCgstTotal += list.getServiceCgst() == null ? 0 : list.getServiceCgst();
            serviceSgstTotal += list.getServiceSgst() == null ? 0 : list.getServiceSgst();
            serviceCessTotal += list.getServiceCessAmount() == null ? 0 : list.getServiceCessAmount();
            tcsApplicabletotal += list.getTcsApplicableAmount() == null ? 0 : list.getTcsApplicableAmount();
            tcsNetTotal += list.getTcsNetAmount() == null ? 0 : list.getTcsNetAmount();
            InvoiceAmountTotal += list.getInvoiceAmount() == null ? 0 : list.getInvoiceAmount();
            serviceGstTotal += list.getServiceGstAmount() == null ? 0 : list.getServiceGstAmount();
          }
        }

      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return salesRegisterList;
  }

  public void setSalesRegisterList(List<CompanyCustomerSales> companyCustomerSalesList) {
    this.salesRegisterList = companyCustomerSalesList;
  }

  public Double getServiceTaxableTotal() {
    return serviceTaxableTotal;
  }

  public Double getServiceIgstTotal() {
    return serviceIgstTotal;
  }

  public Double getServiceCgstTotal() {
    return serviceCgstTotal;
  }

  public Double getServiceSgstTotal() {
    return serviceSgstTotal;
  }

  public Double getServiceCessTotal() {
    return serviceCessTotal;
  }

  public Double getTcsApplicabletotal() {
    return tcsApplicabletotal;
  }

  public Double getTcsNetTotal() {
    return tcsNetTotal;
  }

  public Double getInvoiceAmountTotal() {
    return InvoiceAmountTotal;
  }

  public Double getServiceGstTotal() {
    return serviceGstTotal;
  }

  public void setServiceGstTotal(Double serviceGstTotal) {
    this.serviceGstTotal = serviceGstTotal;
  }

}
