package spica.reports.model;

import java.io.Serializable;
import java.util.Date;
import spica.scm.domain.SalesAgent;

public class CompanyCustomerSales implements Serializable {

  private Integer id;
  private String supplierName;
  private String customerName;
  private String party;
  private String gstNo;
  private String panNo;
  private String documentNo;
  private String districtName;
  private String territoryName;
  private String pinCode;
  private String invoiceNo;
  private java.util.Date entryDate;
  private java.util.Date invoiceDate;
  private String repName;
  private String refInvoiceNo;
  private java.util.Date refInvoiceDate;
  private String agentName;
  private String mfrCode;
  private String productName;
  private String hsnCode;
  private String packType;
  private String batchNo;
  private java.util.Date expiryDate;
  private Double qty;
  private Double qtyFree;
  private Double valueMrp;
  private Double rate;
  private Double valuePts;
  private Double valuePtr;
  private Double goodsValue;
  private Double schemeDiscount;
  private Double productDiscount;
  private Double invoiceDiscount;
  private Double salesValue;
  private Double igst;
  private Double cgst;
  private Double sgst;
  private Double gstAmount;
  private Double netAmount;
  private Double cashDiscount;
  private SalesAgent agent;
  private Double stateCess;
  private Integer invoiceOrReturn;
  private String groupName;
  private Double todaySales;
  private Double todayReturn;
  private Double thisMonthSales;
  private Double thisMonthReturn;
  private Double lastMonthSales;
  private Double lastMonthReturn;
  private Integer salesOrReturn;
  private String invoiceType;
  private Double taxPerc;
  private Double purchaseValue;
  private Double taxableValue;
  private Double serviceTaxableAmount;
  private Double serviceIgst;
  private Double serviceCgst;
  private Double serviceSgst;
  private Double serviceGstAmount;
  private Double invoiceAmount;
  private Double invoiceAmountSubtotal;
  private Double tcsApplicableAmount;
  private Double tcsNetAmount;
  private Double serviceCessAmount;
  private Double landingRate;
  private String productClassification;

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getCustomerName() {
    return this.customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getGstNo() {
    return this.gstNo;
  }

  public void setGstNo(String gstNo) {
    this.gstNo = gstNo;
  }

  public String getDistrictName() {
    return this.districtName;
  }

  public void setDistrictName(String districtName) {
    this.districtName = districtName;
  }

  public String getTerritoryName() {
    return this.territoryName;
  }

  public void setTerritoryName(String territoryName) {
    this.territoryName = territoryName;
  }

  public String getPinCode() {
    return this.pinCode;
  }

  public void setPinCode(String pinCode) {
    this.pinCode = pinCode;
  }

  public String getInvoiceNo() {
    return this.invoiceNo;
  }

  public void setInvoiceNo(String invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public java.util.Date getInvoiceDate() {
    return this.invoiceDate;
  }

  public void setInvoiceDate(java.util.Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public String getRepName() {
    return this.repName;
  }

  public void setRepName(String repName) {
    this.repName = repName;
  }

  public String getAgentName() {
    return this.agentName;
  }

  public void setAgentName(String agentName) {
    this.agentName = agentName;
  }

  public String getMfrCode() {
    return this.mfrCode;
  }

  public void setMfrCode(String mfrCode) {
    this.mfrCode = mfrCode;
  }

  public String getProductName() {
    return this.productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getHsnCode() {
    return this.hsnCode;
  }

  public void setHsnCode(String hsnCode) {
    this.hsnCode = hsnCode;
  }

  public String getPackType() {
    return this.packType;
  }

  public void setPackType(String packType) {
    this.packType = packType;
  }

  public String getBatchNo() {
    return this.batchNo;
  }

  public void setBatchNo(String batchNo) {
    this.batchNo = batchNo;
  }

  public Date getExpiryDate() {
    return this.expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public Double getQty() {
    return this.qty;
  }

  public void setQty(Double qty) {
    this.qty = qty;
  }

  public Double getQtyFree() {
    return this.qtyFree;
  }

  public void setQtyFree(Double qtyFree) {
    this.qtyFree = qtyFree;
  }

  public Double getValueMrp() {
    return this.valueMrp;
  }

  public void setValueMrp(Double valueMrp) {
    this.valueMrp = valueMrp;
  }

  public Double getValuePts() {
    return this.valuePts;
  }

  public void setValuePts(Double valuePts) {
    this.valuePts = valuePts;
  }

  public Double getValuePtr() {
    return this.valuePtr;
  }

  public void setValuePtr(Double valuePtr) {
    this.valuePtr = valuePtr;
  }

  public Double getGoodsValue() {
    return this.goodsValue;
  }

  public void setGoodsValue(Double goodsValue) {
    this.goodsValue = goodsValue;
  }

  public Double getSchemeDiscount() {
    return this.schemeDiscount;
  }

  public void setSchemeDiscount(Double schemeDiscount) {
    this.schemeDiscount = schemeDiscount;
  }

  public Double getProductDiscount() {
    return this.productDiscount;
  }

  public void setProductDiscount(Double productDiscount) {
    this.productDiscount = productDiscount;
  }

  public Double getInvoiceDiscount() {
    return this.invoiceDiscount;
  }

  public void setInvoiceDiscount(Double invoiceDiscount) {
    this.invoiceDiscount = invoiceDiscount;
  }

  public Double getIgst() {
    return this.igst;
  }

  public void setIgst(Double igst) {
    this.igst = igst;
  }

  public Double getCgst() {
    return this.cgst;
  }

  public void setCgst(Double cgst) {
    this.cgst = cgst;
  }

  public Double getSgst() {
    return this.sgst;
  }

  public void setSgst(Double sgst) {
    this.sgst = sgst;
  }

  public Double getNetAmount() {
    return this.netAmount;
  }

  public void setNetAmount(Double netAmount) {
    this.netAmount = netAmount;
  }

  public Double getGstAmount() {
    return gstAmount;
  }

  public void setGstAmount(Double gstAmount) {
    this.gstAmount = gstAmount;
  }

  public Double getCashDiscount() {
    return cashDiscount;
  }

  public void setCashDiscount(Double cashDiscount) {
    this.cashDiscount = cashDiscount;
  }

  public SalesAgent getAgent() {
    return agent;
  }

  public void setAgent(SalesAgent agent) {
    this.agent = agent;
  }

  public Double getSalesValue() {
    return salesValue;
  }

  public void setSalesValue(Double salesValue) {
    this.salesValue = salesValue;
  }

  public Date getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(Date entryDate) {
    this.entryDate = entryDate;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Double getStateCess() {
    return stateCess;
  }

  public void setStateCess(Double stateCess) {
    this.stateCess = stateCess;
  }

  public Integer getInvoiceOrReturn() {
    return invoiceOrReturn;
  }

  public void setInvoiceOrReturn(Integer invoiceOrReturn) {
    this.invoiceOrReturn = invoiceOrReturn;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public Double getTodaySales() {
    return todaySales;
  }

  public void setTodaySales(Double todaySales) {
    this.todaySales = todaySales;
  }

  public Double getThisMonthSales() {
    return thisMonthSales;
  }

  public void setThisMonthSales(Double thisMonthSales) {
    this.thisMonthSales = thisMonthSales;
  }

  public Double getThisMonthReturn() {
    return thisMonthReturn;
  }

  public void setThisMonthReturn(Double thisMonthReturn) {
    this.thisMonthReturn = thisMonthReturn;
  }

  public Double getTodayReturn() {
    return todayReturn;
  }

  public void setTodayReturn(Double todayReturn) {
    this.todayReturn = todayReturn;
  }

  public Double getLastMonthSales() {
    return lastMonthSales;
  }

  public void setLastMonthSales(Double lastMonthSales) {
    this.lastMonthSales = lastMonthSales;
  }

  public Double getLastMonthReturn() {
    return lastMonthReturn;
  }

  public void setLastMonthReturn(Double lastMonthReturn) {
    this.lastMonthReturn = lastMonthReturn;
  }

  public Double getRate() {
    return rate;
  }

  public void setRate(Double rate) {
    this.rate = rate;
  }

  public Integer getSalesOrReturn() {
    return salesOrReturn;
  }

  public void setSalesOrReturn(Integer salesOrReturn) {
    this.salesOrReturn = salesOrReturn;
  }

  public String getInvoiceType() {
    return invoiceType;
  }

  public void setInvoiceType(String invoiceType) {
    this.invoiceType = invoiceType;
  }

  public Double getTaxPerc() {
    return taxPerc;
  }

  public void setTaxPerc(Double taxPerc) {
    this.taxPerc = taxPerc;
  }

  public Double getPurchaseValue() {
    return purchaseValue;
  }

  public void setPurchaseValue(Double purchaseValue) {
    this.purchaseValue = purchaseValue;
  }

  public Double getTaxableValue() {
    return taxableValue;
  }

  public void setTaxableValue(Double taxableValue) {
    this.taxableValue = taxableValue;
  }

  public String getDocumentNo() {
    return documentNo;
  }

  public void setDocumentNo(String documentNo) {
    this.documentNo = documentNo;
  }

  public String getParty() {
    return party;
  }

  public void setParty(String party) {
    this.party = party;
  }

  public String getRefInvoiceNo() {
    return refInvoiceNo;
  }

  public void setRefInvoiceNo(String refInvoiceNo) {
    this.refInvoiceNo = refInvoiceNo;
  }

  public Date getRefInvoiceDate() {
    return refInvoiceDate;
  }

  public void setRefInvoiceDate(Date refInvoiceDate) {
    this.refInvoiceDate = refInvoiceDate;
  }

  public Double getServiceTaxableAmount() {
    return serviceTaxableAmount;
  }

  public void setServiceTaxableAmount(Double serviceTaxableAmount) {
    this.serviceTaxableAmount = serviceTaxableAmount;
  }

  public Double getServiceIgst() {
    return serviceIgst;
  }

  public void setServiceIgst(Double serviceIgst) {
    this.serviceIgst = serviceIgst;
  }

  public Double getServiceCgst() {
    return serviceCgst;
  }

  public void setServiceCgst(Double serviceCgst) {
    this.serviceCgst = serviceCgst;
  }

  public Double getServiceSgst() {
    return serviceSgst;
  }

  public void setServiceSgst(Double serviceSgst) {
    this.serviceSgst = serviceSgst;
  }

  public Double getServiceGstAmount() {
    return serviceGstAmount;
  }

  public void setServiceGstAmount(Double serviceGstAmount) {
    this.serviceGstAmount = serviceGstAmount;
  }

  public Double getInvoiceAmount() {
    return invoiceAmount;
  }

  public void setInvoiceAmount(Double invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
  }

  public Double getTcsApplicableAmount() {
    return tcsApplicableAmount;
  }

  public void setTcsApplicableAmount(Double tcsApplicableAmount) {
    this.tcsApplicableAmount = tcsApplicableAmount;
  }

  public Double getTcsNetAmount() {
    return tcsNetAmount;
  }

  public void setTcsNetAmount(Double tcsNetAmount) {
    this.tcsNetAmount = tcsNetAmount;
  }

  public Double getServiceCessAmount() {
    return serviceCessAmount;
  }

  public void setServiceCessAmount(Double serviceCessAmount) {
    this.serviceCessAmount = serviceCessAmount;
  }

  public Double getLandingRate() {
    return landingRate;
  }

  public void setLandingRate(Double landingRate) {
    this.landingRate = landingRate;
  }

  public String getProductClassification() {
    return productClassification;
  }

  public void setProductClassification(String productClasssification) {
    this.productClassification = productClasssification;
  }

  public String getPanNo() {
    return panNo;
  }

  public void setPanNo(String panNo) {
    this.panNo = panNo;
  }

  public Double getInvoiceAmountSubtotal() {
    return invoiceAmountSubtotal;
  }

  public void setInvoiceAmountSubtotal(Double invoiceAmountSubtotal) {
    this.invoiceAmountSubtotal = invoiceAmountSubtotal;
  }
}
