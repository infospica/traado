/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.util;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author godson
 */
public class ReferenceInvoice implements Serializable {

  private Integer id;
  private Integer debitCreditId;
  private Integer debitCreditNoteItemId;
  private String invoiceNo;
  private String refInvoice;
  private Date refInvoiceDate;
  private String documentNo;
  private Date entryDate;
  private String productDesc;
  private Integer accountGroupId;
  private Integer accountId;
  private Integer vendorId;
  private Integer customerId;
  private Integer invoiceType;
  private Double qty;
  private Double taxableValue;
  private Double taxValue;
  private Double netAmount;
  private String adjRemarks;
  private Double runQtyIn;
  private Double runQtyOut;

  public String getInvoiceNo() {
    return invoiceNo;
  }

  public void setInvoiceNo(String invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public Integer getAccountGroupId() {
    return accountGroupId;
  }

  public void setAccountGroupId(Integer accountGroupId) {
    this.accountGroupId = accountGroupId;
  }

  public Integer getAccountId() {
    return accountId;
  }

  public void setAccountId(Integer accountId) {
    this.accountId = accountId;
  }

  public Integer getVendorId() {
    return vendorId;
  }

  public void setVendorId(Integer vendorId) {
    this.vendorId = vendorId;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getInvoiceType() {
    return invoiceType;
  }

  public void setInvoiceType(Integer invoiceType) {
    this.invoiceType = invoiceType;
  }

  public Double getQty() {
    return qty;
  }

  public void setQty(Double qty) {
    this.qty = qty;
  }

  public Integer getDebitCreditId() {
    return debitCreditId;
  }

  public void setDebitCreditId(Integer debitCreditId) {
    this.debitCreditId = debitCreditId;
  }

  public String getDocumentNo() {
    return documentNo;
  }

  public void setDocumentNo(String documentNo) {
    this.documentNo = documentNo;
  }

  public Date getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(Date entryDate) {
    this.entryDate = entryDate;
  }

  public String getProductDesc() {
    return productDesc;
  }

  public void setProductDesc(String productDesc) {
    this.productDesc = productDesc;
  }

  public Double getTaxableValue() {
    return taxableValue;
  }

  public void setTaxableValue(Double taxableValue) {
    this.taxableValue = taxableValue;
  }

  public Double getTaxValue() {
    return taxValue;
  }

  public void setTaxValue(Double taxValue) {
    this.taxValue = taxValue;
  }

  public Double getNetAmount() {
    return netAmount;
  }

  public void setNetAmount(Double netAmount) {
    this.netAmount = netAmount;
  }

  public Integer getDebitCreditNoteItemId() {
    return debitCreditNoteItemId;
  }

  public void setDebitCreditNoteItemId(Integer debitCreditNoteItemId) {
    this.debitCreditNoteItemId = debitCreditNoteItemId;
  }

  public String getRefInvoice() {
    return refInvoice;
  }

  public void setRefInvoice(String refInvoice) {
    this.refInvoice = refInvoice;
  }

  public Date getRefInvoiceDate() {
    return refInvoiceDate;
  }

  public void setRefInvoiceDate(Date refInvoiceDate) {
    this.refInvoiceDate = refInvoiceDate;
  }

  public String getAdjRemarks() {
    return adjRemarks;
  }

  public void setAdjRemarks(String adjRemarks) {
    this.adjRemarks = adjRemarks;
  }

}
