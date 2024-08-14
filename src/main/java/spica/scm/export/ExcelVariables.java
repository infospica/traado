/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.export;

/**
 *
 * @author godson
 */
public class ExcelVariables {

  private Double valueTot;
  private Double taxableTot;
  private Double igstTot;
  private Double cgstTot;
  private Double sgstTot;
  private Double cessTot;
  private Double amountUpto30;
  private Double amountUpto60;
  private Double amountUpto90;
  private Double amountUpto120;
  private Double amountAbove120;
  private Double total;
  private Double districtAmountUpto30;
  private Double districtAmountUpto60;
  private Double districtAmountUpto90;
  private Double districtAmountUpto120;
  private Double districtAmountAbove120;
  private Double districtTotal;
  private Double reportAmountUpto30;
  private Double reportAmountUpto60;
  private Double reportAmountUpto90;
  private Double reportAmountUpto120;
  private Double reportAmountAbove120;
  private Double reportTotal;

  private int groupLastRow;
  private int groupFirstRow;
  private int secGroupLastRow;
  private int secGroupFirstRow;

  public void resetTotal() {
    this.valueTot = 0.00;
    this.taxableTot = 0.00;
    this.igstTot = 0.00;
    this.cgstTot = 0.00;
    this.sgstTot = 0.00;
    this.cessTot = 0.00;
  }

  public void resetCustomerTotal() {
    this.amountUpto30 = 0.00;
    this.amountUpto60 = 0.00;
    this.amountUpto90 = 0.00;
    this.amountUpto120 = 0.00;
    this.amountAbove120 = 0.00;
    this.total = 0.00;
  }

  public void resetDistrictTotal() {
    this.districtAmountUpto30 = 0.00;
    this.districtAmountUpto60 = 0.00;
    this.districtAmountUpto90 = 0.00;
    this.districtAmountUpto120 = 0.00;
    this.districtAmountAbove120 = 0.00;
    this.districtTotal = 0.00;
  }

  public void resetReportTotal() {
    this.reportAmountUpto30 = 0.00;
    this.reportAmountUpto60 = 0.00;
    this.reportAmountUpto90 = 0.00;
    this.reportAmountUpto120 = 0.00;
    this.reportAmountAbove120 = 0.00;
    this.reportTotal = 0.00;
  }

  public void resetAll() {
    this.valueTot = 0.00;
    this.taxableTot = 0.00;
    this.igstTot = 0.00;
    this.cgstTot = 0.00;
    this.sgstTot = 0.00;
    this.cessTot = 0.00;
    this.amountUpto30 = 0.00;
    this.amountUpto60 = 0.00;
    this.amountUpto90 = 0.00;
    this.amountUpto120 = 0.00;
    this.amountAbove120 = 0.00;
    this.total = 0.00;
    this.districtAmountUpto30 = 0.00;
    this.districtAmountUpto60 = 0.00;
    this.districtAmountUpto90 = 0.00;
    this.districtAmountUpto120 = 0.00;
    this.districtAmountAbove120 = 0.00;
    this.districtTotal = 0.00;
    this.reportAmountUpto30 = 0.00;
    this.reportAmountUpto60 = 0.00;
    this.reportAmountUpto90 = 0.00;
    this.reportAmountUpto120 = 0.00;
    this.reportAmountAbove120 = 0.00;
    this.reportTotal = 0.00;
    this.groupFirstRow = 0;
    this.groupLastRow = 0;
    this.secGroupFirstRow = 0;
    this.secGroupLastRow = 0;
  }

  public void addDistrictTotal() {
    this.districtAmountUpto30 += this.amountUpto30;
    this.districtAmountUpto60 += this.amountUpto60;
    this.districtAmountUpto90 += this.amountUpto90;
    this.districtAmountUpto120 += this.amountUpto120;
    this.districtAmountAbove120 += this.amountAbove120;
    this.districtTotal += this.total;
  }

  public void addReportTotal() {
    this.reportAmountUpto30 += this.districtAmountUpto30;
    this.reportAmountUpto60 += this.districtAmountUpto60;
    this.reportAmountUpto90 += this.districtAmountUpto90;
    this.reportAmountUpto90 += this.districtAmountUpto120;
    this.reportAmountAbove120 += this.districtAmountAbove120;
    this.reportTotal += this.districtTotal;
  }

  public Double getValueTot() {
    return valueTot;
  }

  public void setValueTot(Double valueTot) {
    this.valueTot = valueTot;
  }

  public Double getTaxableTot() {
    return taxableTot;
  }

  public void setTaxableTot(Double taxableTot) {
    this.taxableTot = taxableTot;
  }

  public Double getIgstTot() {
    return igstTot;
  }

  public void setIgstTot(Double igstTot) {
    this.igstTot = igstTot;
  }

  public Double getCgstTot() {
    return cgstTot;
  }

  public void setCgstTot(Double cgstTot) {
    this.cgstTot = cgstTot;
  }

  public Double getSgstTot() {
    return sgstTot;
  }

  public void setSgstTot(Double sgstTot) {
    this.sgstTot = sgstTot;
  }

  public Double getCessTot() {
    return cessTot;
  }

  public void setCessTot(Double cessTot) {
    this.cessTot = cessTot;
  }

  public Double getAmountUpto30() {
    return amountUpto30;
  }

  public void setAmountUpto30(Double amountUpto30) {
    this.amountUpto30 = amountUpto30;
  }

  public Double getAmountUpto60() {
    return amountUpto60;
  }

  public void setAmountUpto60(Double amountUpto60) {
    this.amountUpto60 = amountUpto60;
  }

  public Double getAmountUpto90() {
    return amountUpto90;
  }

  public void setAmountUpto90(Double amountUpto90) {
    this.amountUpto90 = amountUpto90;
  }

  public Double getAmountUpto120() {
    return amountUpto120;
  }

  public void setAmountUpto120(Double amountUpto120) {
    this.amountUpto120 = amountUpto120;
  }

  public Double getAmountAbove120() {
    return amountAbove120;
  }

  public void setAmountAbove120(Double amountAbove120) {
    this.amountAbove120 = amountAbove120;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getDistrictAmountUpto30() {
    return districtAmountUpto30;
  }

  public void setDistrictAmountUpto30(Double districtAmountUpto30) {
    this.districtAmountUpto30 = districtAmountUpto30;
  }

  public Double getDistrictAmountUpto60() {
    return districtAmountUpto60;
  }

  public void setDistrictAmountUpto60(Double districtAmountUpto60) {
    this.districtAmountUpto60 = districtAmountUpto60;
  }

  public Double getDistrictAmountUpto90() {
    return districtAmountUpto90;
  }

  public void setDistrictAmountUpto90(Double districtAmountUpto90) {
    this.districtAmountUpto90 = districtAmountUpto90;
  }

  public Double getDistrictAmountUpto120() {
    return districtAmountUpto120;
  }

  public void setDistrictAmountUpto120(Double districtAmountUpto120) {
    this.districtAmountUpto120 = districtAmountUpto120;
  }

  public Double getDistrictAmountAbove120() {
    return districtAmountAbove120;
  }

  public void setDistrictAmountAbove120(Double districtAmountAbove120) {
    this.districtAmountAbove120 = districtAmountAbove120;
  }

  public Double getDistrictTotal() {
    return districtTotal;
  }

  public void setDistrictTotal(Double districtTotal) {
    this.districtTotal = districtTotal;
  }

  public Double getReportAmountUpto30() {
    return reportAmountUpto30;
  }

  public void setReportAmountUpto30(Double reportAmountUpto30) {
    this.reportAmountUpto30 = reportAmountUpto30;
  }

  public Double getReportAmountUpto60() {
    return reportAmountUpto60;
  }

  public void setReportAmountUpto60(Double reportAmountUpto60) {
    this.reportAmountUpto60 = reportAmountUpto60;
  }

  public Double getReportAmountUpto90() {
    return reportAmountUpto90;
  }

  public void setReportAmountUpto90(Double reportAmountUpto90) {
    this.reportAmountUpto90 = reportAmountUpto90;
  }

  public Double getReportAmountUpto120() {
    return reportAmountUpto120;
  }

  public void setReportAmountUpto120(Double reportAmountUpto120) {
    this.reportAmountUpto120 = reportAmountUpto120;
  }

  public Double getReportAmountAbove120() {
    return reportAmountAbove120;
  }

  public void setReportAmountAbove120(Double reportAmountAbove120) {
    this.reportAmountAbove120 = reportAmountAbove120;
  }

  public Double getReportTotal() {
    return reportTotal;
  }

  public void setReportTotal(Double reportTotal) {
    this.reportTotal = reportTotal;
  }

  public int getGroupLastRow() {
    return groupLastRow;
  }

  public void setGroupLastRow(int groupLastRow) {
    this.groupLastRow = groupLastRow;
  }

  public int getGroupFirstRow() {
    return groupFirstRow;
  }

  public void setGroupFirstRow(int groupFirstRow) {
    this.groupFirstRow = groupFirstRow;
  }

  public int getSecGroupLastRow() {
    return secGroupLastRow;
  }

  public void setSecGroupLastRow(int secGroupLastRow) {
    this.secGroupLastRow = secGroupLastRow;
  }

  public int getSecGroupFirstRow() {
    return secGroupFirstRow;
  }

  public void setSecGroupFirstRow(int secGroupFirstRow) {
    this.secGroupFirstRow = secGroupFirstRow;
  }

}
