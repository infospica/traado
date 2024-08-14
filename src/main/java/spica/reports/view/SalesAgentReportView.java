/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.constant.ReportConstant;
import spica.reports.model.CompanyCustomerSales;
import spica.reports.model.CompanyCustomerSalesRender;
import spica.reports.model.FilterParameters;
import spica.reports.service.SalesAgentReportService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.SalesAgent;
import spica.scm.export.ExcelSheet;
import spica.scm.service.RoleService;
import spica.scm.service.SalesAgentService;
import spica.scm.view.ScmLookupExtView;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import spica.sys.domain.User;
import spica.sys.domain.UserRole;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sujesh
 */
@Named(value = "salesAgentReportView")
@ViewScoped
public class SalesAgentReportView implements Serializable {

  private transient List<CompanyCustomerSales> companyCustomerSalesList;
  private transient FilterParameters filterParameters;
  private transient List reportNames;
  private String[] selectedColumns;
  private List<String> columnNames;
  private transient boolean renValue;
  private transient CompanyCustomerSalesRender companyCustomerSalesRender;
  private transient Double gstAmt;
  private transient Double billAmt;
  private transient Double goodsValue;
  private transient Double igst;
  private transient Double cgst;
  private transient Double sgst;
  private transient Double gstAmount;
  private transient Double netAmount;
  private transient Double mrp;
  private transient Double qty;
  private transient Double freeQty;
  private transient Double schemeDiscount;
  private transient Double productDiscount;
  private transient Double invoiceDiscount;
  private transient Double cashDiscount;
  // private transient DecimalFormat df = new DecimalFormat("0.00");
  private transient Company company;
  private String agentName;
  private transient Double salesValue;
  private transient Double purchaseValue;
  private transient Integer agentStatus;
  private transient List<CompanyCustomerSales> dailyReport;
  private transient boolean renderPurchaseValue;

  @PostConstruct
  public void init() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
      filterParameters.setFilterType(ReportConstant.ALL);
    }
    renderPurchaseValue = isPurchaseValueRender();
    filterParameters.setFilterOption(0);
  }

  public List<CompanyCustomerSales> getCompanyCustomerSalesList(MainView main) {
    if (StringUtil.isEmpty(companyCustomerSalesList)) {
      try {
//        if (filterParameters.getFromDate() == null) {
//          filterParameters.setFromDate(UserRuntimeView.instance().getStartFiscalDate());
//        }
        if (getFilterParameters().getFilterOption() == ReportConstant.COMPANY_OPTION) {
          getFilterParameters().setSalesAgent(null);
        }
        if (getFilterParameters().getReportType() == null) {
          getFilterParameters().setReportType(String.valueOf(ReportConstant.INVOICE_WISE));
        }
        companyCustomerSalesRender = new CompanyCustomerSalesRender();
        companyCustomerSalesRender.setRender(true);
        companyCustomerSalesList = SalesAgentReportService.getCompanyCustomerSalesList(main, UserRuntimeView.instance().getCompany(), getFilterParameters(), getAgentStatus());
        gstAmt = 0.0;
        billAmt = 0.0;
        goodsValue = 0.0;
        gstAmount = 0.0;
        netAmount = 0.0;
        salesValue = 0.0;
        schemeDiscount = 0.0;
        productDiscount = 0.0;
        invoiceDiscount = 0.0;
        cashDiscount = 0.0;
        qty = 0.0;
        mrp = 0.0;
        freeQty = 0.0;
        igst = 0.0;
        cgst = 0.0;
        sgst = 0.0;
        purchaseValue = 0.0;
        if (!companyCustomerSalesList.isEmpty() && companyCustomerSalesList.size() > 0) {
          for (CompanyCustomerSales list : companyCustomerSalesList) {
            if (list.getGstAmount() != null) {
              gstAmt += list.getGstAmount();
            }
            if (list.getNetAmount() != null) {
              billAmt += list.getNetAmount();
            }
            if (list.getQty() != null) {
              qty += list.getQty();
            }
            if (list.getQtyFree() != null) {
              freeQty += list.getQtyFree();
            }
            if (list.getGoodsValue() != null) {
              goodsValue += list.getGoodsValue();
            }
            if (list.getGstAmount() != null) {
              gstAmount += list.getGstAmount();
            }
            if (list.getNetAmount() != null) {
              netAmount += list.getNetAmount();
            }
            if (list.getSalesValue() != null) {
              salesValue += list.getSalesValue();
            }
            if (list.getIgst() != null) {
              igst += list.getIgst();
            }
            if (list.getSgst() != null) {
              sgst += list.getSgst();
            }
            if (list.getCgst() != null) {
              cgst += list.getCgst();
            }
            if (list.getSchemeDiscount() != null) {
              schemeDiscount += list.getSchemeDiscount();
            }
            if (list.getCashDiscount() != null) {
              cashDiscount += list.getCashDiscount();
            }
            if (list.getInvoiceDiscount() != null) {
              invoiceDiscount += list.getInvoiceDiscount();
            }
            if (list.getProductDiscount() != null) {
              productDiscount += list.getProductDiscount();
            }
            if (list.getValueMrp() != null) {
              mrp += list.getValueMrp();
            }
            if (list.getPurchaseValue() != null) {
              purchaseValue += list.getPurchaseValue();
            }
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
    this.companyCustomerSalesList = companyCustomerSalesList;
  }

  public FilterParameters getFilterParameters() {
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
      if (selectedColumn.equals("Supplier Name")) {
        companyCustomerSalesRender.setSupplierName(true);
      } else if (selectedColumn.equals("Invoice No")) {
        companyCustomerSalesRender.setInvoiceNo(true);
      } else if (selectedColumn.equals("Entry Date")) {
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
      } else if (selectedColumn.equals("Purchase Value")) {
        companyCustomerSalesRender.setPurchaseValue(true);
      } else if (selectedColumn.equals("IGST")) {
        companyCustomerSalesRender.setIgst(true);
      } else if (selectedColumn.equals("CGST")) {
        companyCustomerSalesRender.setCgst(true);
      } else if (selectedColumn.equals("SGST")) {
        companyCustomerSalesRender.setSgst(true);
      } else if (selectedColumn.equals("GST Amt.")) {
        companyCustomerSalesRender.setGstAmount(true);
      } else if (selectedColumn.equals("Bill Amt.")) {
        companyCustomerSalesRender.setNetAmount(true);
      } else if (selectedColumn.equals("Sales Agent")) {
        companyCustomerSalesRender.setSalesAgent(true);
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
      columnNames = new ArrayList<>();
      if ("0".equals(filterParameters.getReportType())) {
        columnNames.add("-----");
      } else if ("1".equals(filterParameters.getReportType())) {
//        columnNames.add("Supplier Name");
        columnNames.add("Invoice No");
        columnNames.add("Entry Date");

        columnNames.add("Customer Name");
        columnNames.add("Sales Agent");
        columnNames.add("GST No");
        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("2".equals(filterParameters.getReportType())) {
//        columnNames.add("Supplier Name");
        columnNames.add("Invoice No");
        columnNames.add("Entry Date");

        columnNames.add("Customer Name");
        columnNames.add("Sales Agent");
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
        columnNames.add("PTS");
        columnNames.add("PTR");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("Sales Value");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("3".equals(filterParameters.getReportType())) {
//        columnNames.add("Supplier Name");
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
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("4".equals(filterParameters.getReportType())) {
        columnNames.add("Supplier Name");

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
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("5".equals(filterParameters.getReportType())) {
        columnNames.add("Supplier Name");

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
        columnNames.add("PTS");
        columnNames.add("PTR");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("6".equals(filterParameters.getReportType())) {
        columnNames.add("Supplier Name");

        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("7".equals(filterParameters.getReportType())) {
        columnNames.add("Supplier Name");

        columnNames.add("District Name");
        columnNames.add("Territory Name");

        columnNames.add("Customer Name");
        columnNames.add("GST No");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
        columnNames.add("Bill Amt.");
      } else if ("8".equals(filterParameters.getReportType())) {
        columnNames.add("Supplier Name");

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
        columnNames.add("PTS");
        columnNames.add("PTR");

        columnNames.add("Goods Value");
        columnNames.add("Scheme Discount");
        columnNames.add("Product Discount");
        columnNames.add("Invoice Discount");
        columnNames.add("Cash Discount");
        columnNames.add("IGST");
        columnNames.add("CGST");
        columnNames.add("SGST");
        columnNames.add("GST Amt.");
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
        return ScmLookupExtView.accountByAccountGroupProfileAll(main, filterParameters.getAccountGroup(), filter);
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
    setCompanyCustomerSalesList(null);
  }

  public void accountSelectEvent(SelectEvent event) {
    Account account = (Account) event.getObject();
    if (account != null) {
      if (account.getId() != null) {
        filterParameters.setAccount(account);
        filterParameters.setCustomer(null);

      }
    }
    setCompanyCustomerSalesList(null);
  }

  public void salesAgentSelectEvent(SelectEvent event) {
    SalesAgent salesAgent = (SalesAgent) event.getObject();
    if (salesAgent != null) {
      if (salesAgent.getId() != null) {
        filterParameters.setSalesAgent(salesAgent);
        filterParameters.setAccountGroup(null);
        filterParameters.setAccount(null);
        filterParameters.setCustomer(null);
      }
    }
    setCompanyCustomerSalesList(null);
    setDailyReport(null);
  }

  public List<SalesAgent> salesAgentAuto(String filter) {
    if (getCompany() != null) {
      MainView main = Jsf.getMain();
      try {
        return SalesAgentService.salesAgentsAll(main, getCompany(), filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
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

  public Company getCompany() {
    if (company == null) {
      return UserRuntimeView.instance().getCompany();
    } else {
      return company;
    }
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    if (getCompany() != null && filterParameters != null) {
      if (filterParameters.getSalesAgent() == null) {
        SalesAgent salesAgent = new SalesAgent();
        salesAgent.setCompanyId(getCompany());
        filterParameters.setSalesAgent(salesAgent);
      }
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

  public String getAgentName() {
    if (agentName == null) {
      agentName = UserRuntimeView.instance().getCompany().getCompanyName();
    }
    return agentName;
  }

  public Double getGoodsValue() {
    return goodsValue;
  }

  public void setGoodsValue(Double goodsValue) {
    this.goodsValue = goodsValue;
  }

  public Double getGstAmount() {
    return gstAmount;
  }

  public void setGstAmount(Double gstAmount) {
    this.gstAmount = gstAmount;
  }

  public Double getNetAmount() {
    return netAmount;
  }

  public void setNetAmount(Double netAmount) {
    this.netAmount = netAmount;
  }

  public Double getSalesValue() {
    return salesValue;
  }

  public void setSalesValue(Double salesValue) {
    this.salesValue = salesValue;
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

  public Double getMrp() {
    return mrp;
  }

  public void setMrp(Double mrp) {
    this.mrp = mrp;
  }

  public Double getQty() {
    return qty;
  }

  public void setQty(Double qty) {
    this.qty = qty;
  }

  public Double getFreeQty() {
    return freeQty;
  }

  public void setFreeQty(Double freeQty) {
    this.freeQty = freeQty;
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

  public Double getCashDiscount() {
    return cashDiscount;
  }

  public void setCashDiscount(Double cashDiscount) {
    this.cashDiscount = cashDiscount;
  }

  public void openInvoicePopUp(Integer id, Integer type) {
    if (type == 1) {
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesInvoiceView().replaceFirst("/scm", ""), id, id);
    } else if (type == 2) {
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesReturnView().replaceFirst("/scm", ""), id, id);
    }
  }

  public Integer getAgentStatus() {
    return agentStatus;
  }

  public void setAgentStatus(Integer agentStatus) {
    this.agentStatus = agentStatus;
  }

  public void reset() {
    setCompanyCustomerSalesList(null);
    setDailyReport(null);
  }

  public void export(MainView main, boolean dailyReportCheck) {
    try {
      if (dailyReportCheck) {
        ExcelSheet.createDailyReport(dailyReport, filterParameters);
      } else {
        List<CompanyCustomerSales> dailywiseReport = SalesAgentReportService.dailySalesAgentReport(main, UserRuntimeView.instance().getCompany());
        ExcelSheet.createDailySalesAgentwiseReport(dailywiseReport, UserRuntimeView.instance().getCompany().getCompanyName());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<CompanyCustomerSales> getDailyReport() {
    if (filterParameters.getSalesAgent() != null && filterParameters.getToDate() != null) {
      MainView main = Jsf.getMain();
      try {
        if (StringUtil.isEmpty(dailyReport)) {
          companyCustomerSalesRender = new CompanyCustomerSalesRender();
          companyCustomerSalesRender.setRender(true);
          dailyReport = SalesAgentReportService.dailyReport(main, UserRuntimeView.instance().getCompany(), getFilterParameters());
          billAmt = 0.0;
          salesValue = 0.0;
          qty = 0.0;
          freeQty = 0.0;
          if (!dailyReport.isEmpty() && dailyReport.size() > 0) {
            for (CompanyCustomerSales list : dailyReport) {
              if (list.getNetAmount() != null) {
                billAmt += list.getNetAmount();
              }
              if (list.getQty() != null) {
                qty += list.getQty();
              }
              if (list.getQtyFree() != null) {
                freeQty += list.getQtyFree();
              }
              if (list.getSalesValue() != null) {
                salesValue += list.getSalesValue();
              }
            }
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return dailyReport;
  }

  public void setDailyReport(List<CompanyCustomerSales> dailyReport) {
    this.dailyReport = dailyReport;
  }

  public Double getPurchaseValue() {
    return purchaseValue;
  }

  public void setPurchaseValue(Double purchaseValue) {
    this.purchaseValue = purchaseValue;
  }

  public static boolean isPurchaseValueRender() {
    User u = UserRuntimeView.instance().getAppUser();
    if (u.isRoot()) {
      return true;
    } else if (u != null && u.getUserIdUserRole() != null) {
      for (UserRole userRole : u.getUserIdUserRole()) {
        if (userRole.getRoleId().equals(RoleService.ADMIN) || userRole.getRoleId().equals(RoleService.DIRECTOR)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isRenderPurchaseValue() {
    return renderPurchaseValue;
  }

  public void setRenderPurchaseValue(boolean renderPurchaseValue) {
    this.renderPurchaseValue = renderPurchaseValue;
  }

}
