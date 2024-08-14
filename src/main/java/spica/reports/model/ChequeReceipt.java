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
public class ChequeReceipt implements Serializable {

  private Integer id;
  private String partyName;
  private String chequeNo;
  private Date chequeDate;
  private Double amount;
  private String drawee;
  private Date contraDate;
  private Date createdAt;

  public ChequeReceipt() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getPartyName() {
    return partyName;
  }

  public void setPartyName(String partyName) {
    this.partyName = partyName;
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

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getDrawee() {
    return drawee;
  }

  public void setDrawee(String drawee) {
    this.drawee = drawee;
  }

  public Date getContrDate() {
    return contraDate;
  }

  public void setContrDate(Date contrDate) {
    this.contraDate = contrDate;
  }

  public Date getContraDate() {
    return contraDate;
  }

  public void setContraDate(Date contraDate) {
    this.contraDate = contraDate;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

}
