/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author sujesh
 */
public class SalesAreaWiseReport implements Serializable {

  private Integer id;
  private String invoiceType;
  private Integer customerId;
  private Integer accountGroupId;
  private Integer districtId;
  private Integer salesReturnType;
  private Integer quantity;

  private Double netAmount;
  private Double freeQuantity;
  private Double valueGoods;
  private Double rate;
  private Double mrp;
  private Double deductedValue;
  private Double creditSales;
  private Double packSize;

  private String invoiceNumber;
  private String customerName;
  private String districtName;
  private String productName;
  private String packing;
  private String gstNo;
  private String hsnCode;
  private String accountGroupName;
  private String unit;
  private String pincode;

  private Date invoiceDate;

  public SalesAreaWiseReport() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getInvoiceType() {
    return invoiceType;
  }

  public void setInvoiceType(String invoiceType) {
    this.invoiceType = invoiceType;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public Integer getAccountGroupId() {
    return accountGroupId;
  }

  public void setAccountGroupId(Integer accountGroupId) {
    this.accountGroupId = accountGroupId;
  }

  public Integer getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Integer districtId) {
    this.districtId = districtId;
  }

  public Integer getSalesReturnType() {
    return salesReturnType;
  }

  public void setSalesReturnType(Integer salesReturnType) {
    this.salesReturnType = salesReturnType;
  }

  public Double getNetAmount() {
    return netAmount;
  }

  public void setNetAmount(Double netAmount) {
    this.netAmount = netAmount;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getDistrictName() {
    return districtName;
  }

  public void setDistrictName(String districtName) {
    this.districtName = districtName;
  }

  public Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public String getPincode() {
    return pincode;
  }

  public void setPincode(String pincode) {
    this.pincode = pincode;
  }

  public Double getFreeQuantity() {
    return freeQuantity;
  }

  public void setFreeQuantity(Double freeQuantity) {
    this.freeQuantity = freeQuantity;
  }

  public Double getValueGoods() {
    return valueGoods;
  }

  public void setValueGoods(Double valueGoods) {
    this.valueGoods = valueGoods;
  }

  public Double getRate() {
    return rate;
  }

  public void setRate(Double rate) {
    this.rate = rate;
  }

  public Double getMrp() {
    return mrp;
  }

  public void setMrp(Double mrp) {
    this.mrp = mrp;
  }

  public Double getDeductedValue() {
    return deductedValue;
  }

  public void setDeductedValue(Double deductedValue) {
    this.deductedValue = deductedValue;
  }

  public Double getCreditSales() {
    return creditSales;
  }

  public void setCreditSales(Double creditSales) {
    this.creditSales = creditSales;
  }

  public Double getPackSize() {
    return packSize;
  }

  public void setPackSize(Double packSize) {
    this.packSize = packSize;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getPacking() {
    return packing;
  }

  public void setPacking(String packing) {
    this.packing = packing;
  }

  public String getGstNo() {
    return gstNo;
  }

  public void setGstNo(String gstNo) {
    this.gstNo = gstNo;
  }

  public String getHsnCode() {
    return hsnCode;
  }

  public void setHsnCode(String hsnCode) {
    this.hsnCode = hsnCode;
  }

  public String getAccountGroupName() {
    return accountGroupName;
  }

  public void setAccountGroupName(String accountGroupName) {
    this.accountGroupName = accountGroupName;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

}
