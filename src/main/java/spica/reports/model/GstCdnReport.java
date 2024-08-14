package spica.reports.model;

import java.io.Serializable;
import java.util.Date;

public class GstCdnReport implements Serializable {

  private Integer id;
  private String gstNo;
  private String name;
  private Date recieptDate;
  private String receiptNo;
  private String note;
  private String invoiceNo;
  private String refundNumber;
  private Date refundDate;
  private String stateCode;
  private String stateName;
  private Double billAmount;
  private Double refundValue;
  private Double taxRate;
  private Double taxableValue;
  private Long isInterstate;
  private String documentType;
  private String returnType;
  private Integer isSplit;

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

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNote() {
    return this.note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getRefundNumber() {
    return this.refundNumber;
  }

  public void setRefundNumber(String refundNumber) {
    this.refundNumber = refundNumber;
  }

  public String getStateCode() {
    return this.stateCode;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  public String getStateName() {
    return this.stateName;
  }

  public void setStateName(String stateName) {
    this.stateName = stateName;
  }

  public Double getRefundValue() {
    return this.refundValue;
  }

  public void setRefundValue(Double refundValue) {
    this.refundValue = refundValue;
  }

  public Double getTaxRate() {
    return this.taxRate;
  }

  public void setTaxRate(Double taxRate) {
    this.taxRate = taxRate;
  }

  public Double getTaxableValue() {
    return this.taxableValue;
  }

  public void setTaxableValue(Double taxableValue) {
    this.taxableValue = taxableValue;
  }

  public Long getIsInterstate() {
    return this.isInterstate;
  }

  public void setIsInterstate(Long isInterstate) {
    this.isInterstate = isInterstate;
  }

  public Date getRecieptDate() {
    return recieptDate;
  }

  public void setRecieptDate(Date recieptDate) {
    this.recieptDate = recieptDate;
  }

  public Date getRefundDate() {
    return refundDate;
  }

  public void setRefundDate(Date refundDate) {
    this.refundDate = refundDate;
  }

  public String getDocumentType() {
    return documentType;
  }

  public void setDocumentType(String documentType) {
    this.documentType = documentType;
  }

  public Double getBillAmount() {
    return billAmount;
  }

  public void setBillAmount(Double billAmount) {
    this.billAmount = billAmount;
  }

  public String getInvoiceNo() {
    return invoiceNo;
  }

  public void setInvoiceNo(String invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public Integer getIsSplit() {
    return isSplit;
  }

  public void setIsSplit(Integer isSplit) {
    this.isSplit = isSplit;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

}
