/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Anoop Jayachandran
 */
public class CompanyGstInAndOut implements Serializable {

  private Integer id;
  private String invoiceNo;
  private Double invoiceAmountIgst;
  private Double invoiceAmountCgst;
  private Double invoiceAmountSgst;
  private Date entryDate;
  private String invoiceType;
  private Integer gstOutOrIn;
  private String trader;

  public CompanyGstInAndOut() {
  }

  public CompanyGstInAndOut(Integer id, String invoiceNo, Double invoiceAmountIgst, Double invoiceAmountCgst, Double invoiceAmountSgst, Date entryDate, String invoiceType, Integer gstOutOrIn, String trader) {
    this.id = id;
    this.invoiceNo = invoiceNo;
    this.invoiceAmountIgst = invoiceAmountIgst;
    this.invoiceAmountCgst = invoiceAmountCgst;
    this.invoiceAmountSgst = invoiceAmountSgst;
    this.entryDate = entryDate;
    this.invoiceType = invoiceType;
    this.gstOutOrIn = gstOutOrIn;
    this.trader = trader;
  }
  
  

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getInvoiceNo() {
    return invoiceNo;
  }

  public void setInvoiceNo(String invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public Double getInvoiceAmountIgst() {
    return invoiceAmountIgst;
  }

  public void setInvoiceAmountIgst(Double invoiceAmountIgst) {
    this.invoiceAmountIgst = invoiceAmountIgst;
  }

  public Double getInvoiceAmountCgst() {
    return invoiceAmountCgst;
  }

  public void setInvoiceAmountCgst(Double invoiceAmountCgst) {
    this.invoiceAmountCgst = invoiceAmountCgst;
  }

  public Double getInvoiceAmountSgst() {
    return invoiceAmountSgst;
  }

  public void setInvoiceAmountSgst(Double invoiceAmountSgst) {
    this.invoiceAmountSgst = invoiceAmountSgst;
  }

  public Date getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(Date entryDate) {
    this.entryDate = entryDate;
  }

  public String getInvoiceType() {
    return invoiceType;
  }

  public void setInvoiceType(String invoiceType) {
    this.invoiceType = invoiceType;
  }

  public Integer getGstOutOrIn() {
    return gstOutOrIn;
  }

  public void setGstOutOrIn(Integer gstOutOrIn) {
    this.gstOutOrIn = gstOutOrIn;
  }

  public String getTrader() {
    return trader;
  }

  public void setTrader(String trader) {
    this.trader = trader;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 13 * hash + Objects.hashCode(this.id);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CompanyGstInAndOut other = (CompanyGstInAndOut) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "CompanyGstInAndOut{" + "id=" + id + ", invoiceNo=" + invoiceNo + ", invoiceAmountIgst=" + invoiceAmountIgst + ", invoiceAmountCgst=" + invoiceAmountCgst + ", invoiceAmountSgst=" + invoiceAmountSgst + ", entryDate=" + entryDate + ", invoiceType=" + invoiceType + ", gstOutOrIn=" + gstOutOrIn + '}';
  }

}
