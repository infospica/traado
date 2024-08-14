package spica.reports.model;

import java.io.Serializable;
import java.util.Map;

public class Gst3bReport implements Serializable {

  private Integer id;
  private String invoiceNo;
  private String documentNo;
  private String invoiceDate;
  private String entryDate;
  private String supplierName;
  private String customerName;
  private String receiverName;
  private String gstNo;
  private Double billAmount;
  private Double taxPercentage;
  private Double taxableValue;
  private Double taxData;
  private Double kfCessValue;
  private Map<Double, Double[]> taxMap;
  private Map<Double, Double[]> igstTaxMap;
  private Integer isInterstate;
  private String placeOfSupply;
  private String type;
  private String returnType;
  private Integer productOrService;
  private Double serviceTaxPercentage;
  private Double serviceTaxableValue;
  private Double tcsValue;
  private Double serviceTaxData;
  private Map<Double, Double[]> serviceTaxMap;
  private Map<Double, Double[]> serviceIgstTaxMap;
  private String groupName;
  private Integer accountGroupId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getInvoiceNo() {
    return this.invoiceNo;
  }

  public void setInvoiceNo(String invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public String getInvoiceDate() {
    return this.invoiceDate;
  }

  public void setInvoiceDate(String invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public String getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(String entryDate) {
    this.entryDate = entryDate;
  }

  public String getSupplierName() {
    return this.supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getGstNo() {
    return this.gstNo;
  }

  public void setGstNo(String gstNo) {
    this.gstNo = gstNo;
  }

  public Double getBillAmount() {
    return this.billAmount;
  }

  public void setBillAmount(Double billAmount) {
    this.billAmount = billAmount;
  }

  public Double getTaxPercentage() {
    return this.taxPercentage;
  }

  public void setTaxPercentage(Double taxPercentage) {
    this.taxPercentage = taxPercentage;
  }

  public Double getTaxableValue() {
    return this.taxableValue;
  }

  public void setTaxableValue(Double taxableValue) {
    this.taxableValue = taxableValue;
  }

  public Double getTaxData() {
    return this.taxData;
  }

  public void setTaxData(Double taxData) {
    this.taxData = taxData;
  }

  public Map<Double, Double[]> getTaxMap() {
    return taxMap;
  }

  public void setTaxMap(Map<Double, Double[]> taxMap) {
    this.taxMap = taxMap;
  }

  public String getPlaceOfSupply() {
    return placeOfSupply;
  }

  public void setPlaceOfSupply(String placeOfSupply) {
    this.placeOfSupply = placeOfSupply;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getReceiverName() {
    return receiverName;
  }

  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }

  public Integer getIsInterstate() {
    return isInterstate;
  }

  public void setIsInterstate(Integer isInterstate) {
    this.isInterstate = isInterstate;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public Double getKfCessValue() {
    return kfCessValue;
  }

  public void setKfCessValue(Double kfCessValue) {
    this.kfCessValue = kfCessValue;
  }

  public Map<Double, Double[]> getIgstTaxMap() {
    return igstTaxMap;
  }

  public void setIgstTaxMap(Map<Double, Double[]> igstTaxMap) {
    this.igstTaxMap = igstTaxMap;
  }

  public Integer getProductOrService() {
    return productOrService;
  }

  public void setProductOrService(Integer productOrService) {
    this.productOrService = productOrService;
  }

  public Double getServiceTaxPercentage() {
    return serviceTaxPercentage;
  }

  public void setServiceTaxPercentage(Double serviceTaxPercentage) {
    this.serviceTaxPercentage = serviceTaxPercentage;
  }

  public Double getServiceTaxableValue() {
    return serviceTaxableValue;
  }

  public void setServiceTaxableValue(Double serviceTaxableValue) {
    this.serviceTaxableValue = serviceTaxableValue;
  }

  public Double getServiceTaxData() {
    return serviceTaxData;
  }

  public void setServiceTaxData(Double serviceTaxData) {
    this.serviceTaxData = serviceTaxData;
  }

  public Map<Double, Double[]> getServiceTaxMap() {
    return serviceTaxMap;
  }

  public void setServiceTaxMap(Map<Double, Double[]> serviceTaxMap) {
    this.serviceTaxMap = serviceTaxMap;
  }

  public Map<Double, Double[]> getServiceIgstTaxMap() {
    return serviceIgstTaxMap;
  }

  public void setServiceIgstTaxMap(Map<Double, Double[]> serviceIgstTaxMap) {
    this.serviceIgstTaxMap = serviceIgstTaxMap;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public Integer getAccountGroupId() {
    return accountGroupId;
  }

  public void setAccountGroupId(Integer accountGroupId) {
    this.accountGroupId = accountGroupId;
  }

  public String getDocumentNo() {
    return documentNo;
  }

  public void setDocumentNo(String documentNo) {
    this.documentNo = documentNo;
  }

  public Double getTcsValue() {
    return tcsValue;
  }

  public void setTcsValue(Double tcsValue) {
    this.tcsValue = tcsValue;
  }

}
