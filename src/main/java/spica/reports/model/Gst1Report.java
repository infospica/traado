package spica.reports.model;

import java.io.Serializable;
import java.util.Date;

public class Gst1Report implements Serializable {

  private Integer id;
  private String gstNo;
  private String customerName;
  private String invoiceNo;
  private String invoiceDate;
  private Date entryDate;
  private Double invoiceAmount;
  private Long customerStateId;
  private Double ratePercentage;
  private Double taxAmount;
  private Double taxableValue;
  private String stateName;
  private String stateCode;
  private Double kfCessValue;
  private Double billAmount;
  private String type;
  private Double serviceTaxableValue;
  private Double serviceTaxAmount;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getGstNo() {
    return this.gstNo;
  }

  public void setGstNo(String gstNo) {
    this.gstNo = gstNo;
  }

  public String getCustomerName() {
    return this.customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getInvoiceNo() {
    return this.invoiceNo;
  }

  public void setInvoiceNo(String invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public String getInvoiceDate() {
    return this.invoiceDate;
  }

  public void setInvoiceDate(String invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public Double getInvoiceAmount() {
    return this.invoiceAmount;
  }

  public void setInvoiceAmount(Double invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
  }

  public Long getCustomerStateId() {
    return this.customerStateId;
  }

  public void setCustomerStateId(Long customerStateId) {
    this.customerStateId = customerStateId;
  }

  public Double getRatePercentage() {
    return this.ratePercentage;
  }

  public void setRatePercentage(Double ratePercentage) {
    this.ratePercentage = ratePercentage;
  }

  public Double getTaxableValue() {
    return taxableValue;
  }

  public void setTaxableValue(Double taxableValue) {
    this.taxableValue = taxableValue;
  }

  public String getStateName() {
    return stateName;
  }

  public void setStateName(String stateName) {
    this.stateName = stateName;
  }

  public Double getTaxAmount() {
    return taxAmount;
  }

  public void setTaxAmount(Double taxAmount) {
    this.taxAmount = taxAmount;
  }

  public Date getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(Date entryDate) {
    this.entryDate = entryDate;
  }

  public String getStateCode() {
    return stateCode;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  public Double getKfCessValue() {
    return kfCessValue;
  }

  public void setKfCessValue(Double kfCessValue) {
    this.kfCessValue = kfCessValue;
  }

  public Double getBillAmount() {
    return billAmount;
  }

  public void setBillAmount(Double billAmount) {
    this.billAmount = billAmount;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Double getServiceTaxableValue() {
    return serviceTaxableValue;
  }

  public void setServiceTaxableValue(Double serviceTaxableValue) {
    this.serviceTaxableValue = serviceTaxableValue;
  }

  public Double getServiceTaxAmount() {
    return serviceTaxAmount;
  }

  public void setServiceTaxAmount(Double serviceTaxAmount) {
    this.serviceTaxAmount = serviceTaxAmount;
  }

}
