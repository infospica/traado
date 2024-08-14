package spica.reports.model;

import java.io.Serializable;

public class CompanyCustomerProductwiseSales implements Serializable {

  private String customerName;
  private String gstNo;
  private String productName;
  private String hsnCode;
  private String packing;
  private String mfrCode;
  private Long qty;
  private Integer qtyFree;
  private Double goodsValue;
  private Double netAmount;
  private Double tax;

  public String getCustomerName() {
    return this.customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getGstNo() {
    return this.gstNo;
  }

  public void setGstNo(String gstNo) {
    this.gstNo = gstNo;
  }

  public String getProductName() {
    return this.productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getHsnCode() {
    return this.hsnCode;
  }

  public void setHsnCode(String hsnCode) {
    this.hsnCode = hsnCode;
  }

  public String getPacking() {
    return this.packing;
  }

  public void setPacking(String packing) {
    this.packing = packing;
  }

  public String getMfrCode() {
    return this.mfrCode;
  }

  public void setMfrCode(String mfrCode) {
    this.mfrCode = mfrCode;
  }

  public Long getQty() {
    return this.qty;
  }

  public void setQty(Long qty) {
    this.qty = qty;
  }

  public Integer getQtyFree() {
    return this.qtyFree;
  }

  public void setQtyFree(Integer qtyFree) {
    this.qtyFree = qtyFree;
  }

  public Double getGoodsValue() {
    return this.goodsValue;
  }

  public void setGoodsValue(Double goodsValue) {
    this.goodsValue = goodsValue;
  }

  public Double getNetAmount() {
    return this.netAmount;
  }

  public void setNetAmount(Double netAmount) {
    this.netAmount = netAmount;
  }

  public Double getTax() {
    return tax;
  }

  public void setTax(Double tax) {
    this.tax = tax;
  }

}
