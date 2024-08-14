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
public class SupplierOutstanding implements Serializable {

  private Integer id;
  private String supplier;
  private String document;
  private Date invoiceDate;
  private Double invoiceAmount;
  private Double oustandingAmount;
  private Integer daysOutstanding;
  private Integer debitOrCredit;

  public SupplierOutstanding() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
  }

  public String getDocument() {
    return document;
  }

  public void setDocument(String document) {
    this.document = document;
  }

  public Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public Double getInvoiceAmount() {
    return invoiceAmount;
  }

  public void setInvoiceAmount(Double invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
  }

  public Double getOustandingAmount() {
    return oustandingAmount;
  }

  public void setOustandingAmount(Double oustandingAmount) {
    this.oustandingAmount = oustandingAmount;
  }

  public Integer getDaysOutstanding() {
    return daysOutstanding;
  }

  public void setDaysOutstanding(Integer daysOutstanding) {
    this.daysOutstanding = daysOutstanding;
  }

  public Integer getDebitOrCredit() {
    return debitOrCredit;
  }

  public void setDebitOrCredit(Integer debitOrCredit) {
    this.debitOrCredit = debitOrCredit;
  }

}
