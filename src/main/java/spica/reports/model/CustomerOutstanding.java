package spica.reports.model;

import java.io.Serializable;

public class CustomerOutstanding implements Serializable {

  private Integer id;
  private String name;
  private Integer customerId;
  private Integer tranId;
  private Integer entityId;
  private Integer entityTypeId;
  private String invoiceno;
  private java.util.Date invoicedate;
  private String daysoutstanding;
  private Double invoiceamount;
  private Double oustandingamount;
  private String agentName;

  private String district;
  private String voucherType;
  private String territoryName;
  private String phone1;
  private String phone2;
  private String address;
  private Integer voucherTypeId;

  private Double receivedAmount;
  private Double cumulativeAmount;
  private String daysRange;
  // private Integer territoryId;
//  private String repname;
//  private int debitorcredit;
  //  private Double difference;
  // private Double receivableAmount;

  // private String documentNumber;
  // private boolean negativeCumulative;
//  public String getRepname() {
//    return this.repname;
//  }
//
//  public void setRepname(String repname) {
//    this.repname = repname;
//  }
  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInvoiceno() {
    return this.invoiceno;
  }

  public void setInvoiceno(String invoiceno) {
    this.invoiceno = invoiceno;
  }

  public java.util.Date getInvoicedate() {
    return this.invoicedate;
  }

  public void setInvoicedate(java.util.Date invoicedate) {
    this.invoicedate = invoicedate;
  }

  public Double getInvoiceamount() {
    return this.invoiceamount;
  }

  public void setInvoiceamount(Double invoiceamount) {
    this.invoiceamount = invoiceamount;
  }

  public Double getOustandingamount() {
    return this.oustandingamount;
  }

  public void setOustandingamount(Double oustandingamount) {
    this.oustandingamount = oustandingamount;
  }

  public String getDaysoutstanding() {
    return this.daysoutstanding;
  }

  public void setDaysoutstanding(String daysoutstanding) {
    this.daysoutstanding = daysoutstanding;
    if (daysoutstanding != null) {
      int d = Integer.valueOf(daysoutstanding);
      if (d < 31) {
        daysRange = "0-30";
      } else if (d > 30 && d < 61) {
        daysRange = "31-60";
      } else if (d > 60 && d < 91) {
        daysRange = "61-90";
      } else if (d > 90 && d < 121) {
        daysRange = "90-120";
      } else if (d > 120) {
        daysRange = "Above 120";
      }
    }
  }

  public String getDaysRange() {
    return daysRange;
  }

  public void setDaysRange(String daysRange) {
    this.daysRange = daysRange;
  }

//  public int getDebitorcredit() {
//    return debitorcredit;
//  }
//
//  public void setDebitorcredit(int debitorcredit) {
//    this.debitorcredit = debitorcredit;
//  }
  public String getAgentName() {
    return agentName;
  }

  public void setAgentName(String agentName) {
    this.agentName = agentName;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

//  public Double getDifference() {
//    return difference;
//  }
//
//  public void setDifference(Double difference) {
//    this.difference = difference;
//  }
//  public Double getReceivableAmount() {
//    return receivableAmount;
//  }
//
//  public void setReceivableAmount(Double receivableAmount) {
//    this.receivableAmount = receivableAmount;
//  }
//  public String getDocumentNumber() {
//    return documentNumber;
//  }
//
//  public void setDocumentNumber(String documentNumber) {
//    this.documentNumber = documentNumber;
//  }
  public Double getCumulativeAmount() {
    return cumulativeAmount;
  }

  public void setCumulativeAmount(Double cumulativeAmount) {
    this.cumulativeAmount = cumulativeAmount;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Double getReceivedAmount() {
    return receivedAmount;
  }

  public void setReceivedAmount(Double receivedAmount) {
    this.receivedAmount = receivedAmount;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public String getVoucherType() {
    return voucherType;
  }

  public void setVoucherType(String voucherType) {
    this.voucherType = voucherType;
  }

  public String getTerritoryName() {
    return territoryName;
  }

  public void setTerritoryName(String territoryName) {
    this.territoryName = territoryName;
  }

  public String getPhone1() {
    return phone1;
  }

  public void setPhone1(String phone1) {
    this.phone1 = phone1;
  }

  public String getPhone2() {
    return phone2;
  }

  public void setPhone2(String phone2) {
    this.phone2 = phone2;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

//  public Integer getTerritoryId() {
//    return territoryId;
//  }
//
//  public void setTerritoryId(Integer territoryId) {
//    this.territoryId = territoryId;
//  }
  public Integer getEntityId() {
    return entityId;
  }

  public void setEntityId(Integer entityId) {
    this.entityId = entityId;
  }

  public Integer getEntityTypeId() {
    return entityTypeId;
  }

  public void setEntityTypeId(Integer entityTypeId) {
    this.entityTypeId = entityTypeId;
  }

  public Integer getVoucherTypeId() {
    return voucherTypeId;
  }

  public void setVoucherTypeId(Integer voucherTypeId) {
    this.voucherTypeId = voucherTypeId;
  }

  public Integer getTranId() {
    return tranId;
  }

  public void setTranId(Integer tranId) {
    this.tranId = tranId;
  }

//  public boolean isNegativeCumulative() {
//    return negativeCumulative;
//  }
//
//  public void setNegativeCumulative(boolean negativeCumulative) {
//    this.negativeCumulative = negativeCumulative;
//  }
}
