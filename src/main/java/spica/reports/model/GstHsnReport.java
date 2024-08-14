package spica.reports.model;

import java.io.Serializable;

public class GstHsnReport implements Serializable {

  private String hsnCode;
  private String productCategoryTitle;
  private String commodityTitle;
  private String productPackingTitle;
  private Long totalQuantity;
  private Double totalValue;
  private Double totalTaxableValue;
  private Double totalIgst;
  private Double totalCgst;
  private Double totalSgst;
  private Integer isSalesInterstate;
  private Double kfCessValue;
  private Integer invoiceOrReturn;

  public String getHsnCode() {
    return this.hsnCode;
  }

  public void setHsnCode(String hsnCode) {
    this.hsnCode = hsnCode;
  }

  public String getCommodityTitle() {
    return this.commodityTitle;
  }

  public void setCommodityTitle(String commodityTitle) {
    this.commodityTitle = commodityTitle;
  }

  public String getProductPackingTitle() {
    return this.productPackingTitle;
  }

  public void setProductPackingTitle(String productPackingTitle) {
    this.productPackingTitle = productPackingTitle;
  }

  public Long getTotalQuantity() {
    return this.totalQuantity;
  }

  public void setTotalQuantity(Long totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  public Double getTotalValue() {
    return this.totalValue;
  }

  public void setTotalValue(Double totalValue) {
    this.totalValue = totalValue;
  }

  public Double getTotalTaxableValue() {
    return this.totalTaxableValue;
  }

  public void setTotalTaxableValue(Double totalTaxableValue) {
    this.totalTaxableValue = totalTaxableValue;
  }

  public Double getTotalIgst() {
    return this.totalIgst;
  }

  public void setTotalIgst(Double totalIgst) {
    this.totalIgst = totalIgst;
  }

  public Double getTotalCgst() {
    return this.totalCgst;
  }

  public void setTotalCgst(Double totalCgst) {
    this.totalCgst = totalCgst;
  }

  public Double getTotalSgst() {
    return this.totalSgst;
  }

  public void setTotalSgst(Double totalSgst) {
    this.totalSgst = totalSgst;
  }

  public Integer getIsSalesInterstate() {
    return this.isSalesInterstate;
  }

  public void setIsSalesInterstate(Integer isSalesInterstate) {
    this.isSalesInterstate = isSalesInterstate;
  }

  public Double getKfCessValue() {
    return kfCessValue;
  }

  public void setKfCessValue(Double kfCessValue) {
    this.kfCessValue = kfCessValue;
  }

  public Integer getInvoiceOrReturn() {
    return invoiceOrReturn;
  }

  public void setInvoiceOrReturn(Integer invoiceOrReturn) {
    this.invoiceOrReturn = invoiceOrReturn;
  }

  public String getProductCategoryTitle() {
    return productCategoryTitle;
  }

  public void setProductCategoryTitle(String productCategoryTitle) {
    this.productCategoryTitle = productCategoryTitle;
  }

}
