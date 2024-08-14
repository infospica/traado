/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.addon.model;

/**
 *
 * @author sujesh
 */
public class SmsUsages {

  private Integer companyId;
  private String companyName;
  private Integer allowedSms;
  private Integer usedSms;
  private Integer balanceCount;
  private Integer smsEnabled;

  public Integer getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Integer companyId) {
    this.companyId = companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public Integer getAllowedSms() {
    return allowedSms;
  }

  public void setAllowedSms(Integer allowedSms) {
    this.allowedSms = allowedSms;
  }

  public Integer getUsedSms() {
    return usedSms;
  }

  public void setUsedSms(Integer usedSms) {
    this.usedSms = usedSms;
  }

  public Integer getBalanceCount() {
    return balanceCount;
  }

  public void setBalanceCount(Integer balanceCount) {
    this.balanceCount = balanceCount;
  }

  public Integer getSmsEnabled() {
    return smsEnabled;
  }

  public void setSmsEnabled(Integer smsEnabled) {
    this.smsEnabled = smsEnabled;
  }

}
