/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import spica.reports.model.PurchaseReturnReport;
import spica.reports.model.FilterParameters;
import spica.reports.model.CompanyCustomerSalesRender;

import spica.reports.service.PurchaseReturnReportService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author krishna.vm
 */
@Named(value = "purchaseReturnReportView")
@ViewScoped
public class PurchaseReturnReportView implements Serializable {

  private transient List<PurchaseReturnReport> purchaseReturnReportList;
  private transient FilterParameters filterParameters;
  private transient List reportNames;
  private String[] selectedColumns;
  private List<String> columnNames;
  private transient boolean renValue;
  private transient CompanyCustomerSalesRender companyCustomerSalesRender;
  private Double net = 0.0;
  private Double goodsValue = 0.0;
  private Double igst = 0.0;
  private Double cgst = 0.0;
  private Double sgst = 0.0;
  private Double qty = 0.0;

  public List<PurchaseReturnReport> getPurchaseReturnReportList(MainView main) {
    if (StringUtil.isEmpty(purchaseReturnReportList)) {
      try {
        if (filterParameters.getFromDate() == null) {
          filterParameters.setFromDate(UserRuntimeView.instance().getStartFiscalDate());
        }
        companyCustomerSalesRender = new CompanyCustomerSalesRender();
        companyCustomerSalesRender.setRender(true);
        purchaseReturnReportList = PurchaseReturnReportService.getPurchaseReturnReportList(main, UserRuntimeView.instance().getCompany(), getFilterParameters());
        net = 0.0;
        qty = 0.0;
        goodsValue = 0.0;
        igst = 0.0;
        cgst = 0.0;
        sgst = 0.0;
        for (PurchaseReturnReport purchase : purchaseReturnReportList) {
          if (purchase.getNetAmount() != null && !purchase.getNetAmount().isNaN()) {
            net += purchase.getNetAmount() != null ? purchase.getNetAmount() : 0;
            qty += purchase.getQty() != null ? purchase.getQty() : 0;
            goodsValue += purchase.getGoodsValue() != null ? purchase.getGoodsValue() : 0;
            if (purchase.getCgst() == null || purchase.getCgst() < 0) {
              igst += purchase.getIgst() != null ? purchase.getIgst() : 0;
            }
            cgst += purchase.getCgst() != null ? purchase.getCgst() : 0;
            sgst += purchase.getSgst() != null ? purchase.getSgst() : 0;
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return purchaseReturnReportList;
  }

  public void setPurchaseReturnReportList(List<PurchaseReturnReport> purchaseReturnReportList) {
    this.purchaseReturnReportList = purchaseReturnReportList;
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
    purchaseReturnReportList = null;
    columnNames = null;
    selectedColumns = null;
  }

  public void submitForm() {
    applyFilter(null);
  }

  public void applyColumnFilter(AjaxBehaviorEvent event) {

    companyCustomerSalesRender.setRender(false);
    for (String selectedColumn : selectedColumns) {
      if (selectedColumn.equals("Supplier Name")) {
        companyCustomerSalesRender.setSupplierName(true);
      } else if (selectedColumn.equals("Invoice No")) {
        companyCustomerSalesRender.setInvoiceNo(true);
      } else if (selectedColumn.equals("Invoice Date")) {
        companyCustomerSalesRender.setInvoiceDate(true);
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
        columnNames.add("Supplier Name");
        columnNames.add("GST No");
        columnNames.add("Invoice No");
        columnNames.add("Invoice Date");

        columnNames.add("Goods Value");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("IGST");
        columnNames.add("Net Amount");
        columnNames.add("Return Type");
      } else if ("2".equals(filterParameters.getReportType())) {
        columnNames.add("Supplier Name");
        columnNames.add("GST No");

        columnNames.add("Invoice No");
        columnNames.add("Invoice Date");

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

  public CompanyCustomerSalesRender getCompanyCustomerSalesRender() {
    return companyCustomerSalesRender;
  }

  public void setCompanyCustomerSalesRender(CompanyCustomerSalesRender companyCustomerSalesRender) {
    this.companyCustomerSalesRender = companyCustomerSalesRender;
  }

  public Double getNet() {
    return net;
  }

  public Double getGoodsValue() {
    return goodsValue;
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

  public void openPurchaseReturnPopup(Integer id) {
    Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getPurchaseReturnView(), id, id);
  }
}
