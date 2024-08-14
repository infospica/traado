/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.model;

import java.io.Serializable;
import java.util.Date;
import spica.scm.util.MathUtil;

/**
 *
 * @author sujesh
 */
public class SalesReturnReport implements Serializable {

  private Integer id;
  private String customerName;
  private String gstNo;
  private String regNo;
  private String invoiceNo;
  private java.util.Date invoiceDate;
  private java.util.Date returnDate;
  private Date entryDate;
  private String mfrCode;
  private String productName;
  private String hsnCode;
  private String packType;
  private String batchNo;
  private java.util.Date expiryDate;
  private Double qty;
  private Double freeQty;
  private Double valueMrp;
  private Double rate;
  private Double goodsValue;
  private Double salesValue;
  private Double cgst;
  private Double sgst;
  private Double igst;
  private Double netAmount;
  private String returnType;
  private Integer state;
  private String districtName;
  private String territoryName;
  private Double gstAmount;
  private Double schemeDiscount;
  private Double productDiscount;
  private Double invoiceDiscount;
  private Double valuePts;
  private Double valuePtr;
  private Double returnValue;
  private Double invoiceAmount;
  private Integer splitInvoice;

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getGstNo() {
    return gstNo;
  }

  public void setGstNo(String gstNo) {
    this.gstNo = gstNo;
  }

  public String getInvoiceNo() {
    return invoiceNo;
  }

  public void setInvoiceNo(String invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public String getMfrCode() {
    return mfrCode;
  }

  public void setMfrCode(String mfrCode) {
    this.mfrCode = mfrCode;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getHsnCode() {
    return hsnCode;
  }

  public void setHsnCode(String hsnCode) {
    this.hsnCode = hsnCode;
  }

  public String getPackType() {
    return packType;
  }

  public void setPackType(String packType) {
    this.packType = packType;
  }

  public String getBatchNo() {
    return batchNo;
  }

  public void setBatchNo(String batchNo) {
    this.batchNo = batchNo;
  }

  public Date getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
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
    return MathUtil.roundOff(netAmount, 0);
  }

  public void setNetAmount(Double netAmount) {
    this.netAmount = netAmount;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public Date getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(Date returnDate) {
    this.returnDate = returnDate;
  }

  public Date getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(Date entryDate) {
    this.entryDate = entryDate;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public String getDistrictName() {
    return districtName;
  }

  public void setDistrictName(String districtName) {
    this.districtName = districtName;
  }

  public String getTerritoryName() {
    return territoryName;
  }

  public void setTerritoryName(String territoryName) {
    this.territoryName = territoryName;
  }

  public Double getGstAmount() {
    return gstAmount;
  }

  public void setGstAmount(Double gstAmount) {
    this.gstAmount = gstAmount;
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

  public Double getFreeQty() {
    return freeQty;
  }

  public void setFreeQty(Double freeQty) {
    this.freeQty = freeQty;
  }

  public Double getValuePts() {
    return valuePts;
  }

  public void setValuePts(Double valuePts) {
    this.valuePts = valuePts;
  }

  public Double getValuePtr() {
    return valuePtr;
  }

  public void setValuePtr(Double valuePtr) {
    this.valuePtr = valuePtr;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Double getReturnValue() {
    return returnValue;
  }

  public void setReturnValue(Double returnValue) {
    this.returnValue = returnValue;
  }

  public Double getInvoiceAmount() {
    return invoiceAmount;
  }

  public void setInvoiceAmount(Double invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
  }

  public Integer getSplitInvoice() {
    return splitInvoice;
  }

  public void setSplitInvoice(Integer splitInvoice) {
    this.splitInvoice = splitInvoice;
  }

  public Double getSalesValue() {
    return salesValue;
  }

  public void setSalesValue(Double salesValue) {
    this.salesValue = salesValue;
  }

  public String getRegNo() {
    return regNo;
  }

  public void setRegNo(String regNo) {
    this.regNo = regNo;
  }

}
