package spica.reports.model;

import java.io.Serializable;

public class GstB2csReport implements Serializable {

  private Double ratePercentage;
  private Double taxableValue;
  private String stateName;
  private String stateCode;
  private Double cessAmount;
  private Integer type;

  public Double getRatePercentage() {
    return this.ratePercentage;
  }

  public void setRatePercentage(Double ratePercentage) {
    this.ratePercentage = ratePercentage;
  }

  public Double getTaxableValue() {
    return this.taxableValue;
  }

  public void setTaxableValue(Double taxableValue) {
    this.taxableValue = taxableValue;
  }

  public String getStateName() {
    return this.stateName;
  }

  public void setStateName(String stateName) {
    this.stateName = stateName;
  }

  public String getStateCode() {
    return stateCode;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Double getCessAmount() {
    return cessAmount;
  }

  public void setCessAmount(Double cessAmount) {
    this.cessAmount = cessAmount;
  }

}
