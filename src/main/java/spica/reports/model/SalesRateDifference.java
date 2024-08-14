package spica.reports.model;

import java.io.Serializable;

public class SalesRateDifference implements Serializable {

  private Integer id;
  private String supplierName;
  private String invoiceNo;
  private java.util.Date invoiceDate;
  private String customerName;
  private String gstNo;
  private String productName;
  private String hsnCode;
  private String packType;
  private String batchNo;
  private java.util.Date expiryDate;
  private Long qty;
  private Long qtyFree;
  private Double landingRate;
  private Double valuePts;
  private Double rateDeviation;
  private Double valueMrp;
  private Double schemeDiscount;
  private Double schemeDiscDeviation;
  private Double productDiscount;
  private Double productDiscDeviation;
  private Double invoiceDiscount;
  private Double invoiceDiscDeviation;
  private Double goodsValue;
  private Double igst;
  private Double cgst;
  private Double sgst;
  private Double netAmount;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getSupplierName() {
    return this.supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
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

  public java.util.Date getExpiryDate() {
    return this.expiryDate;
  }

  public void setExpiryDate(java.util.Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public Long getQty() {
    return this.qty;
  }

  public void setQty(Long qty) {
    this.qty = qty;
  }

  public Long getQtyFree() {
    return this.qtyFree;
  }

  public void setQtyFree(Long qtyFree) {
    this.qtyFree = qtyFree;
  }

  public Double getValuePts() {
    return this.valuePts;
  }

  public void setValuePts(Double valuePts) {
    this.valuePts = valuePts;
  }

  public Double getRateDeviation() {
    return this.rateDeviation;
  }

  public void setRateDeviation(Double rateDeviation) {
    this.rateDeviation = rateDeviation;
  }

  public Double getValueMrp() {
    return this.valueMrp;
  }

  public void setValueMrp(Double valueMrp) {
    this.valueMrp = valueMrp;
  }

  public Double getSchemeDiscount() {
    return this.schemeDiscount;
  }

  public void setSchemeDiscount(Double schemeDiscount) {
    this.schemeDiscount = schemeDiscount;
  }

  public Double getSchemeDiscDeviation() {
    return this.schemeDiscDeviation;
  }

  public void setSchemeDiscDeviation(Double schemeDiscDeviation) {
    this.schemeDiscDeviation = schemeDiscDeviation;
  }

  public Double getProductDiscount() {
    return this.productDiscount;
  }

  public void setProductDiscount(Double productDiscount) {
    this.productDiscount = productDiscount;
  }

  public Double getProductDiscDeviation() {
    return this.productDiscDeviation;
  }

  public void setProductDiscDeviation(Double productDiscDeviation) {
    this.productDiscDeviation = productDiscDeviation;
  }

  public Double getInvoiceDiscount() {
    return this.invoiceDiscount;
  }

  public void setInvoiceDiscount(Double invoiceDiscount) {
    this.invoiceDiscount = invoiceDiscount;
  }

  public Double getInvoiceDiscDeviation() {
    return this.invoiceDiscDeviation;
  }

  public void setInvoiceDiscDeviation(Double invoiceDiscDeviation) {
    this.invoiceDiscDeviation = invoiceDiscDeviation;
  }

  public Double getGoodsValue() {
    return this.goodsValue;
  }

  public void setGoodsValue(Double goodsValue) {
    this.goodsValue = goodsValue;
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

  public Double getLandingRate() {
    return landingRate;
  }

  public void setLandingRate(Double landingRate) {
    this.landingRate = landingRate;
  }

}
