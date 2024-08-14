/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
