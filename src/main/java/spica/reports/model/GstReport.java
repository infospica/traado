/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author sujesh
 */
public class GstReport implements Serializable {

  private String ledgercode;
  private String title;
  private String taxtype;
  private String document;
  private Double credit;
  private Double debit;
  private Double amount;
  private Integer entityid;
  private Date createdat;
  private Integer transactionid;
  private Integer detailid;

  public GstReport() {
  }

  public String getLedgercode() {
    return ledgercode;
  }

  public void setLedgercode(String ledgercode) {
    this.ledgercode = ledgercode;
  }

  public String getTaxtype() {
    return taxtype;
  }

  public void setTaxtype(String taxtype) {
    this.taxtype = taxtype;
  }

  public Double getCredit() {
    return credit;
  }

  public void setCredit(Double credit) {
    this.credit = credit;
  }

  public Double getDebit() {
    return debit;
  }

  public void setDebit(Double debit) {
    this.debit = debit;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getCreatedat() {
    return createdat;
  }

  public void setCreatedat(Date createdat) {
    this.createdat = createdat;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Integer getEntityid() {
    return entityid;
  }

  public void setEntityid(Integer entityid) {
    this.entityid = entityid;
  }

  public String getDocument() {
    return document;
  }

  public void setDocument(String document) {
    this.document = document;
  }

  public Integer getTransactionid() {
    return transactionid;
  }

  public void setTransactionid(Integer transactionid) {
    this.transactionid = transactionid;
  }

  public Integer getDetailid() {
    return detailid;
  }

  public void setDetailid(Integer detailid) {
    this.detailid = detailid;
  }

}
