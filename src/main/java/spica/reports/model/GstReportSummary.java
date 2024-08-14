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
public class GstReportSummary implements Serializable {

  private String ledgerCode;
  private double sgst;
  private double cgst;
  private double igst;
  private Integer entityId;
  private String title;
  private String document;
  private Date fromDate;
  private Date toDate;
  private double amount;

  public GstReportSummary() {
  }

  public GstReportSummary(GstReportSummary report) {
    this.ledgerCode = report.getLedgerCode();
    this.sgst = report.getSgst();
    this.cgst = report.getCgst();
    this.igst = report.getIgst();
    this.entityId = report.getEntityId();
    this.title = report.getTitle();
    this.document = report.getDocument();
    this.fromDate = report.getFromDate();
    this.toDate = report.getToDate();
    this.amount = report.getAmount();
  }

  public String getLedgerCode() {
    return ledgerCode;
  }

  public void setLedgerCode(String ledgerCode) {
    this.ledgerCode = ledgerCode;
  }

  public double getSgst() {
    return sgst;
  }

  public void setSgst(double sgst) {
    this.sgst = sgst;
  }

  public double getCgst() {
    return cgst;
  }

  public void setCgst(double cgst) {
    this.cgst = cgst;
  }

  public double getIgst() {
    return igst;
  }

  public void setIgst(double igst) {
    this.igst = igst;
  }

  public Integer getEntityId() {
    return entityId;
  }

  public void setEntityId(Integer entityId) {
    this.entityId = entityId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDocument() {
    return document;
  }

  public void setDocument(String document) {
    this.document = document;
  }

  public Date getFromDate() {
    return fromDate;
  }

  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  public Date getToDate() {
    return toDate;
  }

  public void setToDate(Date toDate) {
    this.toDate = toDate;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

}
