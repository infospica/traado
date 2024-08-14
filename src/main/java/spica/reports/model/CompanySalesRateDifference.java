package spica.reports.model;
import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;
import spica.sys.UserRuntimeView;


public class CompanySalesRateDifference implements Serializable { 
  
  private String supplierName;
  private String customerName;
  private String gstNo;
  private String invoiceNo;
  private java.util.Date invoiceDate;	    
  private String mfrCode;
  private String productName;
  private String hsnCode;
  private String packType;
  private String batchNo;
  private java.util.Date expiryDate;           
  private Long qty;
  private Long qtyFree;
  private Double valueMrp;
  private Double valuePts;
  private Double valueRate;
  private Double valuePtr;
  private Double goodsValue;
  private Double schemeDiscount;
  private Double productDiscount;
  private Double invoiceDiscount;  
  private Double rateDifference;  
  
 
  public String getSupplierName() {
    return supplierName;
  }
  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }
  public String getCustomerName() { 
    return this.customerName;
  }
  public void setCustomerName(String customerName) { 
    this.customerName=customerName;
  }
  public String getGstNo() { 
    return this.gstNo;
  }
  public void setGstNo(String gstNo) { 
    this.gstNo=gstNo;
  }    
  public String getInvoiceNo() { 
    return this.invoiceNo;
  }
  public void setInvoiceNo(String invoiceNo) { 
    this.invoiceNo=invoiceNo;
  }
  public java.util.Date getInvoiceDate() { 
    return this.invoiceDate;
  }
  public void setInvoiceDate(java.util.Date invoiceDate) { 
    this.invoiceDate=invoiceDate;
  }  
  public String getMfrCode() {
    return this.mfrCode;
  }
  public void setMfrCode(String mfrCode) { 
    this.mfrCode=mfrCode;
  }   
  public String getProductName() { 
    return this.productName;
  }
  public void setProductName(String productName) { 
    this.productName=productName;
  }   
  public String getHsnCode() { 
    return this.hsnCode;
  }
  public void setHsnCode(String hsnCode) { 
    this.hsnCode=hsnCode;
  }   
  public String getPackType() { 
    return this.packType;
  }
  public void setPackType(String packType) { 
    this.packType=packType;
  }   
  public String getBatchNo() { 
    return this.batchNo;
  }
  public void setBatchNo(String batchNo) { 
    this.batchNo=batchNo;
  }   
  public Date getExpiryDate() { 
    return this.expiryDate;
  }
  public void setExpiryDate(Date expiryDate) { 
    this.expiryDate=expiryDate;
  }   
  public Long getQty() { 
    return this.qty;
  }
  public void setQty(Long qty) { 
    this.qty=qty;
  }
  public Long getQtyFree() { 
    return this.qtyFree;
  }
  public void setQtyFree(Long qtyFree) { 
    this.qtyFree=qtyFree;
  }
  public Double getValueMrp() { 
    return this.valueMrp;
  }
  public void setValueMrp(Double valueMrp) { 
    this.valueMrp=valueMrp;
  }
  public Double getValuePts() { 
    return this.valuePts;
  }
  public void setValuePts(Double valuePts) { 
    this.valuePts=valuePts;
  }
  public Double getValuePtr() { 
    return this.valuePtr;
  }
  public void setValuePtr(Double valuePtr) { 
    this.valuePtr=valuePtr;
  }
  public Double getGoodsValue() { 
    return this.goodsValue;
  }
  public void setGoodsValue(Double goodsValue) { 
    this.goodsValue=goodsValue;
  } 
  public Double getSchemeDiscount() { 
    return this.schemeDiscount;
  }
  public void setSchemeDiscount(Double schemeDiscount) { 
    this.schemeDiscount=schemeDiscount;
  }
  public Double getProductDiscount() { 
    return this.productDiscount;
  }
  public void setProductDiscount(Double productDiscount) { 
    this.productDiscount=productDiscount;
  }
  public Double getInvoiceDiscount() { 
    return this.invoiceDiscount;
  }
  public void setInvoiceDiscount(Double invoiceDiscount) { 
    this.invoiceDiscount=invoiceDiscount;
  } 

  public Double getValueRate() {
    return valueRate;
  }

  public void setValueRate(Double valueRate) {
    this.valueRate = valueRate;
  }

  public Double getRateDifference() {
    return rateDifference;
  }

  public void setRateDifference(Double rateDifference) {
    this.rateDifference = rateDifference;
  }
     
}