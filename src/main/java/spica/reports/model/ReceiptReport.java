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
public class ReceiptReport implements Serializable {

  private Integer id;
  private Integer ledgerId;
  private Integer bankStatus;
  private Integer statusId;
  private Integer customerId;
  private Integer tranId;
  private Double amount;
  private Double cumulativeSum;
  private String customer;
  private String documentNumber;
  private String narration;
  private String chequeNo;
  private String finDocNo;
  private String address;
  private String bankName;
  private Date entryDate;
  private Date chequeDate;
  private Date processedAt;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Date getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(Date entryDate) {
    this.entryDate = entryDate;
  }

  public String getDocumentNumber() {
    return documentNumber;
  }

  public void setDocumentNumber(String documentNumber) {
    this.documentNumber = documentNumber;
  }

  public Integer getLedgerId() {
    return ledgerId;
  }

  public void setLedgerId(Integer ledgerId) {
    this.ledgerId = ledgerId;
  }

  public Double getCumulativeSum() {
    return cumulativeSum;
  }

  public void setCumulativeSum(Double cumulativeSum) {
    this.cumulativeSum = cumulativeSum;
  }

  public Integer getBankStatus() {
    return bankStatus;
  }

  public void setBankStatus(Integer bankStatus) {
    this.bankStatus = bankStatus;
  }

  public String getNarration() {
    return narration;
  }

  public void setNarration(String narration) {
    this.narration = narration;
  }

  public String getChequeNo() {
    return chequeNo;
  }

  public void setChequeNo(String chequeNo) {
    this.chequeNo = chequeNo;
  }

  public Date getChequeDate() {
    return chequeDate;
  }

  public void setChequeDate(Date chequeDate) {
    this.chequeDate = chequeDate;
  }

  public Integer getStatusId() {
    return statusId;
  }

  public void setStatusId(Integer statusId) {
    this.statusId = statusId;
  }

  public String getFinDocNo() {
    return finDocNo;
  }

  public void setFinDocNo(String finDocNo) {
    this.finDocNo = finDocNo;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public Integer getTranId() {
    return tranId;
  }

  public void setTranId(Integer tranId) {
    this.tranId = tranId;
  }

  public Date getProcessedAt() {
    return processedAt;
  }

  public void setProcessedAt(Date processedAt) {
    this.processedAt = processedAt;
  }

}
