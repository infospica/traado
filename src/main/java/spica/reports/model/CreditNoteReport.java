/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.model;

import java.util.Date;

/**
 *
 * @author Godson Joseph
 */
public class CreditNoteReport {
  private Integer id;
  private Double ratePercentage;
  private String customerName;
  private String invoiceNo;
  private String productName;
  private Double qty;
  private Double qtyFree;
  private Double rate;
  private Double grossValue;
  private Double discAmount;
  private Double netValue;
  private Double marginPercentage;
  private Double billingSd;
  private Double diff;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Double getRatePercentage() {
    return ratePercentage;
  }

  public void setRatePercentage(Double ratePercentage) {
    this.ratePercentage = ratePercentage;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getInvoiceNo() {
    return invoiceNo;
  }

  public void setInvoiceNo(String invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Double getQty() {
    return qty;
  }

  public void setQty(Double qty) {
    this.qty = qty;
  }

  public Double getQtyFree() {
    return qtyFree;
  }

  public void setQtyFree(Double qtyFree) {
    this.qtyFree = qtyFree;
  }

  public Double getRate() {
    return rate;
  }

  public void setRate(Double rate) {
    this.rate = rate;
  }

  public Double getGrossValue() {
    return grossValue;
  }

  public void setGrossValue(Double grossValue) {
    this.grossValue = grossValue;
  }

  public Double getDiscAmount() {
    return discAmount;
  }

  public void setDiscAmount(Double discAmount) {
    this.discAmount = discAmount;
  }

  public Double getNetValue() {
    return netValue;
  }

  public void setNetValue(Double netValue) {
    this.netValue = netValue;
  }

  public Double getMarginPercentage() {
    return marginPercentage;
  }

  public void setMarginPercentage(Double marginPercentage) {
    this.marginPercentage = marginPercentage;
  }

  public Double getBillingSd() {
    return billingSd;
  }

  public void setBillingSd(Double billingSd) {
    this.billingSd = billingSd;
  }

  public Double getDiff() {
    return diff;
  }

  public void setDiff(Double diff) {
    this.diff = diff;
  }

}
