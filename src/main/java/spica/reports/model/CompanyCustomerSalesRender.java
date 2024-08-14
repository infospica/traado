package spica.reports.model;

import java.io.Serializable;
import spica.sys.UserRuntimeView;

public class CompanyCustomerSalesRender implements Serializable {

  private Boolean supplierName;
  private Boolean customerName;
  private Boolean gstNo;
  private Boolean districtName;
  private Boolean territoryName;
  private Boolean pinCode;
  private Boolean invoiceNo;
  private Boolean invoiceDate;
  private Boolean repName;
  private Boolean agentName;
  private Boolean mfrCode;
  private Boolean productName;
  private Boolean hsnCode;
  private Boolean packType;
  private Boolean batchNo;
  private Boolean expiryDate;
  private Boolean qty;
  private Boolean qtyFree;
  private Boolean valueMrp;
  private Boolean valuePts;
  private Boolean valuePtr;
  private Boolean rate;
  private Boolean rateDeviation;
  private Boolean goodsValue;
  private Boolean schemeDiscount;
  private Boolean productDiscount;
  private Boolean invoiceDiscount;
  private Boolean schemeDiscDeviation;
  private Boolean productDiscDeviation;
  private Boolean invoiceDiscDeviation;
  private Boolean igst;
  private Boolean cgst;
  private Boolean sgst;
  private Boolean netAmount;
  private Boolean gstAmount;
  private Boolean returnType;
  private Boolean valueRate;
  private Boolean rateDiff;
  private Boolean cashDiscount;
  private Boolean entryDate;
  private Boolean salesAgent;
  private Boolean salesValue;
  private Boolean purchaseValue;
  private Boolean stateCess;
  private Boolean returnValue;
  private Boolean serviceTaxableAmount;
  private Boolean serviceIgst;
  private Boolean serviceCgst;
  private Boolean serviceSgst;
  private Boolean serviceGstAmount;
  private Boolean invoiceAmount;
  private Boolean serviceCess;
  private Boolean tcsApplicableAmount;
  private Boolean tcsNetAmount;

  public void setRender(boolean renValue) {
    supplierName = renValue;
    customerName = renValue;
    gstNo = renValue;
    districtName = renValue;
    territoryName = renValue;
    pinCode = renValue;
    invoiceNo = renValue;
    invoiceDate = renValue;
    repName = renValue;
    agentName = renValue;
    mfrCode = renValue;
    productName = renValue;
    hsnCode = renValue;
    packType = renValue;
    batchNo = renValue;
    expiryDate = renValue;
    qty = renValue;
    qtyFree = renValue;
    valueMrp = renValue;
    valuePts = renValue;
    valuePtr = renValue;
    rateDeviation = renValue;
    goodsValue = renValue;
    schemeDiscount = renValue;
    productDiscount = renValue;
    invoiceDiscount = renValue;
    schemeDiscDeviation = renValue;
    productDiscDeviation = renValue;
    invoiceDiscDeviation = renValue;
    igst = renValue;
    cgst = renValue;
    sgst = renValue;
    netAmount = renValue;
    gstAmount = renValue;
    returnType = renValue;
    valueRate = renValue;
    rateDiff = renValue;
    cashDiscount = renValue;
    entryDate = renValue;
    salesAgent = renValue;
    salesValue = renValue;
    stateCess = renValue;
    returnValue = renValue;
    purchaseValue = renValue;
    serviceTaxableAmount = renValue;
    serviceIgst = renValue;
    serviceCgst = renValue;
    serviceSgst = renValue;
    serviceGstAmount = renValue;
    invoiceAmount = renValue;
    tcsApplicableAmount = renValue;
    serviceCess = renValue;
    tcsNetAmount = renValue;
  }

  public Boolean getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(Boolean supplierName) {
    this.supplierName = supplierName;
  }

  public Boolean getCustomerName() {
    return this.customerName;
  }

  public void setCustomerName(Boolean customerName) {
    this.customerName = customerName;
  }

  public Boolean getGstNo() {
    return this.gstNo;
  }

  public void setGstNo(Boolean gstNo) {
    this.gstNo = gstNo;
  }

  public Boolean getDistrictName() {
    return this.districtName;
  }

  public void setDistrictName(Boolean districtName) {
    this.districtName = districtName;
  }

  public Boolean getTerritoryName() {
    return this.territoryName;
  }

  public void setTerritoryName(Boolean territoryName) {
    this.territoryName = territoryName;
  }

  public Boolean getPinCode() {
    return this.pinCode;
  }

  public void setPinCode(Boolean pinCode) {
    this.pinCode = pinCode;
  }

  public Boolean getInvoiceNo() {
    return this.invoiceNo;
  }

  public void setInvoiceNo(Boolean invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public Boolean getInvoiceDate() {
    return this.invoiceDate;
  }

  public void setInvoiceDate(Boolean invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public Boolean getRepName() {
    return this.repName;
  }

  public void setRepName(Boolean repName) {
    this.repName = repName;
  }

  public Boolean getAgentName() {
    return this.agentName;
  }

  public void setAgentName(Boolean agentName) {
    this.agentName = agentName;
  }

  public Boolean getMfrCode() {
    return this.mfrCode;
  }

  public void setMfrCode(Boolean mfrCode) {
    this.mfrCode = mfrCode;
  }

  public Boolean getProductName() {
    return this.productName;
  }

  public void setProductName(Boolean productName) {
    this.productName = productName;
  }

  public Boolean getHsnCode() {
    return this.hsnCode;
  }

  public void setHsnCode(Boolean hsnCode) {
    this.hsnCode = hsnCode;
  }

  public Boolean getPackType() {
    return this.packType;
  }

  public void setPackType(Boolean packType) {
    this.packType = packType;
  }

  public Boolean getBatchNo() {
    return this.batchNo;
  }

  public void setBatchNo(Boolean batchNo) {
    this.batchNo = batchNo;
  }

  public Boolean getExpiryDate() {
    return this.expiryDate;
  }

  public void setExpiryDate(Boolean expiryDate) {
    this.expiryDate = expiryDate;
  }

  public Boolean getQty() {
    return this.qty;
  }

  public void setQty(Boolean qty) {
    this.qty = qty;
  }

  public Boolean getQtyFree() {
    return this.qtyFree;
  }

  public void setQtyFree(Boolean qtyFree) {
    this.qtyFree = qtyFree;
  }

  public Boolean getValueMrp() {
    return this.valueMrp;
  }

  public void setValueMrp(Boolean valueMrp) {
    this.valueMrp = valueMrp;
  }

  public Boolean getValuePts() {
    return this.valuePts;
  }

  public void setValuePts(Boolean valuePts) {
    this.valuePts = valuePts;
  }

  public Boolean getValuePtr() {
    return this.valuePtr;
  }

  public void setValuePtr(Boolean valuePtr) {
    this.valuePtr = valuePtr;
  }

  public Boolean getGoodsValue() {
    return this.goodsValue;
  }

  public void setGoodsValue(Boolean goodsValue) {
    this.goodsValue = goodsValue;
  }

  public Boolean getSchemeDiscount() {
    return this.schemeDiscount;
  }

  public void setSchemeDiscount(Boolean schemeDiscount) {
    this.schemeDiscount = schemeDiscount;
  }

  public Boolean getProductDiscount() {
    return this.productDiscount;
  }

  public void setProductDiscount(Boolean productDiscount) {
    this.productDiscount = productDiscount;
  }

  public Boolean getInvoiceDiscount() {
    return this.invoiceDiscount;
  }

  public void setInvoiceDiscount(Boolean invoiceDiscount) {
    this.invoiceDiscount = invoiceDiscount;
  }

  public Boolean getIgst() {
    return this.igst;
  }

  public void setIgst(Boolean igst) {
    this.igst = igst;
  }

  public Boolean getCgst() {
    return this.cgst;
  }

  public void setCgst(Boolean cgst) {
    this.cgst = cgst;
  }

  public Boolean getSgst() {
    return this.sgst;
  }

  public void setSgst(Boolean sgst) {
    this.sgst = sgst;
  }

  public Boolean getNetAmount() {
    return this.netAmount;
  }

  public void setNetAmount(Boolean netAmount) {
    this.netAmount = netAmount;
  }

  public Boolean getRateDeviation() {
    return rateDeviation;
  }

  public void setRateDeviation(Boolean rateDeviation) {
    this.rateDeviation = rateDeviation;
  }

  public Boolean getSchemeDiscDeviation() {
    return schemeDiscDeviation;
  }

  public void setSchemeDiscDeviation(Boolean schemeDiscDeviation) {
    this.schemeDiscDeviation = schemeDiscDeviation;
  }

  public Boolean getProductDiscDeviation() {
    return productDiscDeviation;
  }

  public void setProductDiscDeviation(Boolean productDiscDeviation) {
    this.productDiscDeviation = productDiscDeviation;
  }

  public Boolean getInvoiceDiscDeviation() {
    return invoiceDiscDeviation;
  }

  public void setInvoiceDiscDeviation(Boolean invoiceDiscDeviation) {
    this.invoiceDiscDeviation = invoiceDiscDeviation;
  }

  public Boolean getRate() {
    return rate;
  }

  public void setRate(Boolean rate) {
    this.rate = rate;
  }

  public Boolean getReturnType() {
    return returnType;
  }

  public void setReturnType(Boolean returnType) {
    this.returnType = returnType;
  }

  public Boolean getValueRate() {
    return valueRate;
  }

  public void setValueRate(Boolean valueRate) {
    this.valueRate = valueRate;
  }

  public Boolean getRateDiff() {
    return rateDiff;
  }

  public void setRateDiff(Boolean rateDiff) {
    this.rateDiff = rateDiff;
  }

  public Boolean getGstAmount() {
    return gstAmount;
  }

  public void setGstAmount(Boolean gstAmount) {
    this.gstAmount = gstAmount;
  }

  public Boolean getCashDiscount() {
    return cashDiscount;
  }

  public void setCashDiscount(Boolean cashDiscount) {
    this.cashDiscount = cashDiscount;
  }

  public Boolean getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(Boolean entryDate) {
    this.entryDate = entryDate;
  }

  public Boolean getSalesAgent() {
    return salesAgent;
  }

  public void setSalesAgent(Boolean salesAgent) {
    this.salesAgent = salesAgent;
  }

  public Boolean getSalesValue() {
    return salesValue;
  }

  public void setSalesValue(Boolean salesValue) {
    this.salesValue = salesValue;
  }

  public Boolean getStateCess() {
    return stateCess;
  }

  public void setStateCess(Boolean stateCess) {
    this.stateCess = stateCess;
  }

  public Boolean getReturnValue() {
    return returnValue;
  }

  public void setReturnValue(Boolean returnValue) {
    this.returnValue = returnValue;
  }

  public Boolean getPurchaseValue() {
    return purchaseValue;
  }

  public void setPurchaseValue(Boolean purchaseValue) {
    this.purchaseValue = purchaseValue;
  }

  public Boolean getServiceTaxableAmount() {
    return serviceTaxableAmount;
  }

  public void setServiceTaxableAmount(Boolean serviceTaxableAmount) {
    this.serviceTaxableAmount = serviceTaxableAmount;
  }

  public Boolean getServiceIgst() {
    return serviceIgst;
  }

  public void setServiceIgst(Boolean serviceIgst) {
    this.serviceIgst = serviceIgst;
  }

  public Boolean getServiceCgst() {
    return serviceCgst;
  }

  public void setServiceCgst(Boolean serviceCgst) {
    this.serviceCgst = serviceCgst;
  }

  public Boolean getServiceSgst() {
    return serviceSgst;
  }

  public void setServiceSgst(Boolean serviceSgst) {
    this.serviceSgst = serviceSgst;
  }

  public Boolean getServiceGstAmount() {
    return serviceGstAmount;
  }

  public void setServiceGstAmount(Boolean serviceGstAmount) {
    this.serviceGstAmount = serviceGstAmount;
  }

  public Boolean getInvoiceAmount() {
    return invoiceAmount;
  }

  public void setInvoiceAmount(Boolean invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
  }

  public Boolean getTcsApplicableAmount() {
    return tcsApplicableAmount;
  }

  public void setTcsApplicableAmount(Boolean tcsApplicableAmount) {
    this.tcsApplicableAmount = tcsApplicableAmount;
  }

  public Boolean getTcsNetAmount() {
    return tcsNetAmount;
  }

  public void setTcsNetAmount(Boolean tcsNetAmount) {
    this.tcsNetAmount = tcsNetAmount;
  }

  public Boolean getServiceCess() {
    return serviceCess;
  }

  public void setServiceCess(Boolean serviceCess) {
    this.serviceCess = serviceCess;
  }

}
