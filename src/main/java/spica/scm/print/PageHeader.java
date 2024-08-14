/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import java.util.Date;
import java.util.List;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerLicense;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorLicense;

/**
 *
 * @author sujesh
 */
public interface PageHeader {

  public PdfPTable setCompanyAddress(CompanyAddress companyAddress, Company company, String logo);

  public PdfPTable setCompanyGST(Company company, List companyLicenseList);

  public PdfPTable setBillInvoice(String billNumber, Date billDate, int creditDays, float[] column, boolean voucher) throws DocumentException;

  public PdfPTable setCustomerAddress(Customer customer, CustomerAddress customerAddress, boolean billTo);

  public PdfPTable setCustomerGST(Customer customer, List<CustomerLicense> customerLicenseList, String printType);

  public PdfPTable setShipingAddress(CustomerAddress shippingAddress);

  public PdfPTable setCarrierDetails(ConsignmentDetail consignmentdetail, String printType, boolean salesReturn, String debitNo, String debitNoteDate, String regNo) throws DocumentException;

  public PdfPTable setGStInvoiceLabel(String label);

  public PdfPTable setSalesReturnLabel(Integer salesReturnType, Integer confirm);

  public PdfPTable setVendorAddress(Vendor vendor, VendorAddress vendorAddress);

  public PdfPTable setVendorGST(Vendor vendor, List<VendorLicense> vendorLicenseList, String printType);

  public PdfPTable setUserAddress(UserProfile userProfile);

  public PdfPTable setUserGST(UserProfile userProfile, String printType);

  public PdfPTable setAddress(String name, String address, String state);

  public PdfPTable setLicense(String gst, String fssai);

  public PdfPTable setPurchaseReturnLabel(Integer returnType, Integer confirm);

  public PdfPTable setRecipientAddress(RecipientAddress recipientAddress, boolean billToPrintable);

  public PdfPTable setRecipientGST(RecipientAddress recipientAddress, String printType);

  public PdfPTable setRecipientShipingAddress(RecipientAddress shippingAddress);

  public PdfPTable setVendorAddress(Vendor vendor, VendorAddress vendorAddress, boolean billTo);

}
