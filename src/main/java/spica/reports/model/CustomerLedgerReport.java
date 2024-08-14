/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.model;

/**
 *
 * @author godson
 */
import java.io.Serializable;
import java.util.Date;

public class CustomerLedgerReport implements Serializable {

  private String customerName;
  private String accountGroup;
  private java.util.Date accDate;
  private String transTitle;
  private String documentNumber;
  private String refDocumentNo;
  private String narration;
  private Double debitAmount;
  private Double creditAmount;
  private Double balanceAmount;
  private String voucherType;
  private int isOpening;

  public String getCustomerName() {
    return this.customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getAccountGroup() {
    return this.accountGroup;
  }

  public void setAccountGroup(String accountGroup) {
    this.accountGroup = accountGroup;
  }

  public java.util.Date getAccDate() {
    return this.accDate;
  }

  public void setAccDate(java.util.Date accDate) {
    this.accDate = accDate;
  }

  public String getTransTitle() {
    return this.transTitle;
  }

  public void setTransTitle(String transTitle) {
    this.transTitle = transTitle;
  }

  public String getDocumentNumber() {
    return this.documentNumber;
  }

  public void setDocumentNumber(String documentNumber) {
    this.documentNumber = documentNumber;
  }

  public String getRefDocumentNo() {
    return this.refDocumentNo;
  }

  public void setRefDocumentNo(String refDocumentNo) {
    this.refDocumentNo = refDocumentNo;
  }

  public String getNarration() {
    return this.narration;
  }

  public void setNarration(String narration) {
    this.narration = narration;
  }

  public Double getDebitAmount() {
    return debitAmount;
  }

  public void setDebitAmount(Double debitAmount) {
    this.debitAmount = debitAmount;
  }

  public Double getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(Double creditAmount) {
    this.creditAmount = creditAmount;
  }

  public Double getBalanceAmount() {
    return balanceAmount;
  }

  public void setBalanceAmount(Double balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public String getVoucherType() {
    return voucherType;
  }

  public void setVoucherType(String voucherType) {
    this.voucherType = voucherType;
  }

  public int getIsOpening() {
    return isOpening;
  }

  public void setIsOpening(int isOpening) {
    this.isOpening = isOpening;
  }

}
