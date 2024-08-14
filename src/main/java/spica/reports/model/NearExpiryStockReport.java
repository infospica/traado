package spica.reports.model;

import java.io.Serializable;

public class NearExpiryStockReport implements Serializable {

  private Long productId;
  private Long accountId;
  private Long productDetailId;
  private Long productEntryDetailId;
  private Long stockId;
  private String productName;
  private String hsnCode;
  private String packSize;
  private String batchNo;
  private Long mrpValue;
  private java.util.Date expiryDateActual;
  private Long expiryDays;
  private Long quantitySaleable;
  private Long quantityFreeSaleable;
  private Long quantityBlocked;
  private Long quantityFreeBlocked;
  private Long purchaseRate;
  private Long ptsRate;
  private Long salesRate;
  private String supplierName;
  private String supplierGst;

  public Long getProductId() {
    return this.productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getAccountId() {
    return this.accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public Long getProductDetailId() {
    return this.productDetailId;
  }

  public void setProductDetailId(Long productDetailId) {
    this.productDetailId = productDetailId;
  }

  public Long getProductEntryDetailId() {
    return this.productEntryDetailId;
  }

  public void setProductEntryDetailId(Long productEntryDetailId) {
    this.productEntryDetailId = productEntryDetailId;
  }

  public Long getStockId() {
    return this.stockId;
  }

  public void setStockId(Long stockId) {
    this.stockId = stockId;
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

  public String getPackSize() {
    return this.packSize;
  }

  public void setPackSize(String packSize) {
    this.packSize = packSize;
  }

  public String getBatchNo() {
    return this.batchNo;
  }

  public void setBatchNo(String batchNo) {
    this.batchNo = batchNo;
  }

  public Long getMrpValue() {
    return this.mrpValue;
  }

  public void setMrpValue(Long mrpValue) {
    this.mrpValue = mrpValue;
  }

  public java.util.Date getExpiryDateActual() {
    return this.expiryDateActual;
  }

  public void setExpiryDateActual(java.util.Date expiryDateActual) {
    this.expiryDateActual = expiryDateActual;
  }

  public Long getExpiryDays() {
    return this.expiryDays;
  }

  public void setExpiryDays(Long expiryDays) {
    this.expiryDays = expiryDays;
  }

  public Long getQuantitySaleable() {
    return this.quantitySaleable;
  }

  public void setQuantitySaleable(Long quantitySaleable) {
    this.quantitySaleable = quantitySaleable;
  }

  public Long getQuantityFreeSaleable() {
    return this.quantityFreeSaleable;
  }

  public void setQuantityFreeSaleable(Long quantityFreeSaleable) {
    this.quantityFreeSaleable = quantityFreeSaleable;
  }

  public Long getQuantityBlocked() {
    return this.quantityBlocked;
  }

  public void setQuantityBlocked(Long quantityBlocked) {
    this.quantityBlocked = quantityBlocked;
  }

  public Long getQuantityFreeBlocked() {
    return this.quantityFreeBlocked;
  }

  public void setQuantityFreeBlocked(Long quantityFreeBlocked) {
    this.quantityFreeBlocked = quantityFreeBlocked;
  }

  public Long getPurchaseRate() {
    return this.purchaseRate;
  }

  public void setPurchaseRate(Long purchaseRate) {
    this.purchaseRate = purchaseRate;
  }

  public Long getPtsRate() {
    return this.ptsRate;
  }

  public void setPtsRate(Long ptsRate) {
    this.ptsRate = ptsRate;
  }

  public Long getSalesRate() {
    return this.salesRate;
  }

  public void setSalesRate(Long salesRate) {
    this.salesRate = salesRate;
  }

  public String getSupplierName() {
    return this.supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getSupplierGst() {
    return this.supplierGst;
  }

  public void setSupplierGst(String supplierGst) {
    this.supplierGst = supplierGst;
  }
}
