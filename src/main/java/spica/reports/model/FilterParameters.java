/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Brand;
import spica.scm.domain.Customer;
import spica.scm.domain.District;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Vendor;

/**
 *
 * @author krishna.vm
 */
public class FilterParameters implements Serializable {

  private Date today;
  private Date firstDayOfWeek;
  private Date firstDayOfMonth;

  private AccountGroup accountGroup;
  private Account account;
  private Customer customer;
  private UserProfile userProfile;
  private SalesAgent salesAgent;
  private String reportType;
  private int selectedYear;
  private int selectedMonth;
  private int filterOption;
  private String filterType;
  private Vendor vendor;
  private District[] district;
  private String productHash;
  private boolean includeReturn;
  private boolean includeSales;
  private Brand brand;

  private Date fromDate;
  private Date toDate;

  public int getToYear() {
    return dmy(getToDate(), Calendar.YEAR);
  }

  public int getToMonth() {
    return dmy(getToDate(), Calendar.MONTH);
  }

  public int getFromYear() {
    return dmy(getFromDate(), Calendar.YEAR);
  }

  public int getFromMonth() {
    return dmy(getFromDate(), Calendar.MONTH);
  }

  private int dmy(Date date, int type) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(type);
  }

  public Date getFromDate() {
    if (fromDate == null) { //Set first day of month
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.DAY_OF_MONTH, 1);
      fromDate = cal.getTime();
    }
    return fromDate;
  }

  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  public Date getToDate() {
    if (toDate == null) { //Set current date
      toDate = new Date();
    }
    return toDate;
  }

  public void setToDate(Date toDate) {
    this.toDate = toDate;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public String getReportType() {
    if (reportType == null) {
      reportType = String.valueOf(1);
    }
    return reportType;
  }

  public void setReportType(String reportType) {
    this.reportType = reportType;
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public int getSelectedYear() {
    return selectedYear;
  }

  public void setSelectedYear(int selectedYear) {
    this.selectedYear = selectedYear;
  }

  public int getSelectedMonth() {
    return selectedMonth;
  }

  public void setSelectedMonth(int selectedMonth) {
    this.selectedMonth = selectedMonth;
  }

  public UserProfile getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

  public int getFilterOption() {
    return filterOption;
  }

  public void setFilterOption(int filterOption) {
    this.filterOption = filterOption;
  }

  public Date getToday() {
    if (today == null) { //Set current date
      today = new Date();
    }
    return today;
  }

  public void setToday(Date today) {
    this.today = today;
  }

  public Date getFirstDayOfWeek() {
    return firstDayOfWeek;
  }

  public void setFirstDayOfweek(Date firstDayOfweek) {
    this.firstDayOfWeek = firstDayOfweek;
  }

  public Date getFirstDayOfMonth() {
    return firstDayOfMonth;
  }

  public void setFirstDayOfMonth(Date firstDayOfMonth) {
    this.firstDayOfMonth = firstDayOfMonth;
  }

  public String getFilterType() {
    if (filterType == null) {
      filterType = String.valueOf(1);
    }
    return filterType;
  }

  public void setFilterType(String filterType) {
    this.filterType = filterType;
  }

  public Vendor getVendor() {
    return vendor;
  }

  public void setVendor(Vendor vendor) {
    this.vendor = vendor;
  }

  public District[] getDistrict() {
    return district;
  }

  public void setDistrict(District[] district) {
    this.district = district;
  }

  public String getProductHash() {
    return productHash;
  }

  public void setProductHash(String productHash) {
    this.productHash = productHash;
  }

  public boolean isIncludeReturn() {
    return includeReturn;
  }

  public void setIncludeReturn(boolean includeReturn) {
    this.includeReturn = includeReturn;
  }

  public Brand getBrand() {
    return brand;
  }

  public void setBrand(Brand brand) {
    this.brand = brand;
  }

  public SalesAgent getSalesAgent() {
    return salesAgent;
  }

  public void setSalesAgent(SalesAgent salesAgent) {
    this.salesAgent = salesAgent;
  }

  public boolean isIncludeSales() {
    return includeSales;
  }

  public void setIncludeSales(boolean includeSales) {
    this.includeSales = includeSales;
  }

}
