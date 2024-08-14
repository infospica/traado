/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print;

import java.io.Serializable;
import java.util.List;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorLicense;

/**
 *
 * @author sujesh
 */
public class RecipientAddress implements Serializable {

  private Integer id;
  private String name;
  private String address;
  private String district;
  private String state;
  private String pinCode;
  private String phone;
  private String phone1;
  private String email;
  private String gstNo;
  private String panNo;
  private String fssai[];
  private String tinNo;
  private String cstNo;
  private String dllNo[];
  private RecipientAddress shippingAddress;
  private List<VendorLicense> vendorLicenseList;

  public RecipientAddress() {
  }

  public RecipientAddress(Vendor vendor, VendorAddress vendorAddress) {
    this.id = vendorAddress.getId();
    this.name = vendor.getVendorName();
    this.address = vendorAddress.getAddress();
    this.state = vendorAddress.getStateId() != null ? vendorAddress.getStateId().getStateName() : "";
    this.district = vendorAddress.getDistrictId() != null ? vendorAddress.getDistrictId().getDistrictName() : "";
    this.phone = vendorAddress.getPhone1();
    this.phone1 = vendorAddress.getPhone2();
    this.email = vendorAddress.getEmail();
    this.panNo = vendor.getPanNo();
    this.tinNo = vendor.getTinNo();
    this.gstNo = vendor.getGstNo();
    this.cstNo = vendor.getCstNo();
    this.pinCode = vendorAddress.getPin();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getPinCode() {
    return pinCode;
  }

  public void setPinCode(String pinCode) {
    this.pinCode = pinCode;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getGstNo() {
    return gstNo;
  }

  public void setGstNo(String gstNo) {
    this.gstNo = gstNo;
  }

  public String getPanNo() {
    return panNo;
  }

  public void setPanNo(String panNo) {
    this.panNo = panNo;
  }

  public String[] getFssai() {
    return fssai;
  }

  public void setFssai(String[] fssai) {
    this.fssai = fssai;
  }

  public String[] getDllNo() {
    return dllNo;
  }

  public void setDllNo(String[] dllNo) {
    this.dllNo = dllNo;
  }

  public RecipientAddress getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(RecipientAddress shippingAddress) {
    this.shippingAddress = shippingAddress;
  }

  public String getPhone1() {
    return phone1;
  }

  public void setPhone1(String phone1) {
    this.phone1 = phone1;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTinNo() {
    return tinNo;
  }

  public void setTinNo(String tinNo) {
    this.tinNo = tinNo;
  }

  public String getCstNo() {
    return cstNo;
  }

  public void setCstNo(String cstNo) {
    this.cstNo = cstNo;
  }

  public void switchVendorToRecipient(Vendor vendor, VendorAddress vendorAddress) {

  }

  public List<VendorLicense> getVendorLicenseList() {
    return vendorLicenseList;
  }

  public void setVendorLicenseList(List<VendorLicense> vendorLicenseList) {
    this.vendorLicenseList = vendorLicenseList;
  }

}
