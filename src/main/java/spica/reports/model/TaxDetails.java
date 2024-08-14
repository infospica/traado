package spica.reports.model;

import java.io.Serializable;

public class TaxDetails implements Serializable {

  private Long accountingGroupId;
  private String taxName;
  private Long id;
  private String title;
  private Double tax;
  private String taxType;
  private int rowIndex;

  public String getTaxName() {
    return this.taxName;
  }

  public void setTaxName(String taxName) {
    this.taxName = taxName;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getAccountingGroupId() {
    return accountingGroupId;
  }

  public void setAccountingGroupId(Long accountingGroupId) {
    this.accountingGroupId = accountingGroupId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getTax() {
    return tax;
  }

  public void setTax(Double tax) {
    this.tax = tax;
  }

  public String getTaxType() {
    return taxType;
  }

  public void setTaxType(String taxType) {
    this.taxType = taxType;
  }

  public int getRowIndex() {
    return rowIndex;
  }

  public void setRowIndex(int rowIndex) {
    this.rowIndex = rowIndex;
  }

}
