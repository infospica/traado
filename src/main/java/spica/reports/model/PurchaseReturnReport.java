/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.model;

/**
 *
 * @author krishna.vm
 */
public class PurchaseReturnReport {

  private Integer id;
  private String supplierName;
  private String gstNo;
  private String invoiceNo;
  private java.util.Date invoiceDate;
  private String mfrCode;
  private String productName;
  private String hsnCode;
  private String packType;
  private String batchNo;
  private java.util.Date expiryDate;
  private Double qty;
  private Double valueMrp;
  private Double rate;
  private Double goodsValue;
  private Double cgst;
  private Double sgst;
  private Double igst;
  private Double schemeDiscount;
  private Double schemeDiscountPercentage;
  private Double productDiscount;
  private Double productDiscountPercentage;
  private Double netAmount;
  private String returnType;

  private java.util.Date entryDate;

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

  public String getGstNo() {
    return this.gstNo;
  }

  public void setGstNo(String gstNo) {
    this.gstNo = gstNo;
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

  public java.util.Date getExpiryDate() {
    return this.expiryDate;
  }

  public void setExpiryDate(java.util.Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public String getReturnType() {
    return this.returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public Double getQty() {
    return qty;
  }

  public void setQty(Double qty) {
    this.qty = qty;
  }

  public Double getValueMrp() {
    return valueMrp;
  }

  public void setValueMrp(Double valueMrp) {
    this.valueMrp = valueMrp;
  }

  public Double getRate() {
    return rate;
  }

  public void setRate(Double rate) {
    this.rate = rate;
  }

  public Double getGoodsValue() {
    return goodsValue;
  }

  public void setGoodsValue(Double goodsValue) {
    this.goodsValue = goodsValue;
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

  public Double getIgst() {
    return igst;
  }

  public void setIgst(Double igst) {
    this.igst = igst;
  }

  public Double getNetAmount() {
    return netAmount;
  }

  public void setNetAmount(Double netAmount) {
    this.netAmount = netAmount;
  }

  public java.util.Date getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(java.util.Date entryDate) {
    this.entryDate = entryDate;
  }

  public Double getSchemeDiscount() {
    return schemeDiscount;
  }

  public void setSchemeDiscount(Double schemeDiscount) {
    this.schemeDiscount = schemeDiscount;
  }

  public Double getSchemeDiscountPercentage() {
    return schemeDiscountPercentage;
  }

  public void setSchemeDiscountPercentage(Double schemeDiscountPercentage) {
    this.schemeDiscountPercentage = schemeDiscountPercentage;
  }

  public Double getProductDiscount() {
    return productDiscount;
  }

  public void setProductDiscount(Double productDiscount) {
    this.productDiscount = productDiscount;
  }

  public Double getProductDiscountPercentage() {
    return productDiscountPercentage;
  }

  public void setProductDiscountPercentage(Double productDiscountPercentage) {
    this.productDiscountPercentage = productDiscountPercentage;
  }

}
