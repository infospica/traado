package spica.reports.model;

import java.io.Serializable;

public class AgeWiseStockAnalysis implements Serializable {

  private String productName;
  private String invoiceNo;
  private String productEntryDate;
  private Long currentQty;
  private Double taxableAmount;
  private Integer days;
  private String hsnCode;
  private Double ratePercentage;

  public String getProductName() {
    return this.productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getInvoiceNo() {
    return this.invoiceNo;
  }

  public void setInvoiceNo(String invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public String getProductEntryDate() {
    return this.productEntryDate;
  }

  public void setProductEntryDate(String productEntryDate) {
    this.productEntryDate = productEntryDate;
  }

  public Long getCurrentQty() {
    return this.currentQty;
  }

  public void setCurrentQty(Long currentQty) {
    this.currentQty = currentQty;
  }

  public Double getTaxableAmount() {
    return taxableAmount;
  }

  public void setTaxableAmount(Double taxableAmount) {
    this.taxableAmount = taxableAmount;
  }

  public Integer getDays() {
    return this.days;
  }

  public void setDays(Integer days) {
    this.days = days;
  }

  public String getHsnCode() {
    return this.hsnCode;
  }

  public void setHsnCode(String hsnCode) {
    this.hsnCode = hsnCode;
  }

  public Double getRatePercentage() {
    return this.ratePercentage;
  }

  public void setRatePercentage(Double ratePercentage) {
    this.ratePercentage = ratePercentage;
  }
}
