/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print.Impl;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerLicense;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorLicense;
import spica.scm.print.PageHeader;
import spica.scm.print.PdfUtil;
import spica.scm.print.RecipientAddress;
import spica.sys.SystemConstants;
import wawo.app.faces.AppView;
import wawo.entity.util.DateUtil;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sujesh
 */
public class PageHeaderImpl extends PdfUtil implements PageHeader {

  public SalesInvoice salesInvoice;
  public float[] widths;

  @Override
  public PdfPTable setCompanyAddress(CompanyAddress companyAddress, Company company, String logo) {

    if (StringUtils.isEmpty(logo)) {
      return createCompanyAddress(companyAddress, company);
    } else {
      return createCompanyAddressWithlogo(companyAddress, company, logo);
    }
  }

  @Override
  public PdfPTable setCompanyGST(Company company, List companyLicenseList) {
    PdfPTable table = new PdfPTable(new float[]{25, 75});
    table.setWidthPercentage(30);
    table.setHorizontalAlignment(Element.ALIGN_CENTER);
    PdfPCell gst = new PdfPCell();
    PdfPCell gstVal = new PdfPCell();
    PdfPCell pan = new PdfPCell();
    PdfPCell panVal = new PdfPCell();
    PdfPCell fssai = new PdfPCell();
    PdfPCell fssaiVal = new PdfPCell();
    Paragraph para = new Paragraph();
    PdfPCell dl = new PdfPCell();
    List<CompanyLicense> cList = companyLicenseList;

    if (company.getId() != null) {
      gst = new PdfPCell(new Phrase("GST No.", fontSubHeader()));
      gst.setLeading(5, 0);
      String gstNo = company.getGstNo() == null ? "" : company.getGstNo();
      gstVal = new PdfPCell(new Phrase(":   " + gstNo.toUpperCase(), fontSubHeader()));
      gstVal.setLeading(5, 0);
      String panNo = company.getPanNo() == null ? "" : company.getPanNo();
      pan = new PdfPCell(new Phrase("PAN No", fontSubHeader()));
      pan.setLeading(5, 0);
      panVal = new PdfPCell(new Phrase(":   " + panNo.toUpperCase(), fontSubHeader()));
      panVal.setLeading(5, 0);
      // For FSSAI
      fssai = new PdfPCell(new Phrase("FSSAI No.", fontSubHeader()));
      fssai.setLeading(5, 0);
      para.add(new Phrase(":   ", fontSubHeader()));
      for (CompanyLicense license : cList) {
        if (license.getLicenseTypeId() != null) {
          if (Objects.equals(license.getLicenseTypeId().getId(), SystemConstants.FSSAI_LIC)) {
            para.add(new Phrase(license.getLicenseKey() == null ? "" : license.getLicenseKey(), fontSubHeader()));
            para.add(Chunk.NEWLINE);
            para.add(new Phrase("    ", fontSubHeader()));
          }
        }
      }
      fssaiVal = new PdfPCell(para);
      fssaiVal.setLeading(5, 0);
      // For D.L.No
      dl = new PdfPCell(new Phrase("D.L NO", fontSubHeader()));
      dl.setLeading(5, 0);
      para = new Paragraph();
      para.add(new Phrase(": ", fontSubHeader()));
      for (CompanyLicense license : cList) {
        if (license.getLicenseTypeId() != null) {
          if (Objects.equals(license.getLicenseTypeId().getId(), SystemConstants.DRUG_LIC)) {
            para.add(new Phrase(license.getLicenseKey(), fontSubHeader()));
            para.add(Chunk.NEWLINE);
            para.add(Chunk.NEWLINE);
            para.add(new Phrase("  ", fontSubHeader()));
          }
        }
      }
    }
    PdfPCell dlVal = new PdfPCell(para);
    dlVal.setPaddingBottom(-5);
    dlVal.setLeading(5, 0);
    table.addCell(gst).setBorder(0);
    table.addCell(gstVal).setBorder(0);
    table.addCell(pan).setBorder(0);
    table.addCell(panVal).setBorder(0);
    table.addCell(fssai).setBorder(0);
    table.addCell(fssaiVal).setBorder(0);
    table.addCell(dl).setBorder(0);
    table.addCell(dlVal).setBorder(0);
    return table;
  }

  @Override
  public PdfPTable setBillInvoice(String billNumber, Date billDate, int creditDays, float[] column, boolean receipt) throws DocumentException {
    PdfPTable table = new PdfPTable(2);
    String billNoLabel = "";
    if (billNumber.startsWith("CNT")) {
      billNoLabel = "CN No.";
    } else if (billNumber.startsWith("DNT")) {
      billNoLabel = "DN No.";
    } else {
      billNoLabel = receipt ? "Voucher No." : "Bill No.";
    }

    PdfPCell bill = new PdfPCell(new Phrase(billNoLabel, fontBill()));
    bill.setPaddingLeft(15);
    Chunk c = new Chunk(": " + billNumber, fontInvoiceNo());
    PdfPCell billVal = new PdfPCell(new Phrase(c));
    PdfPCell date = new PdfPCell(new Phrase("Date.", fontBill()));
    date.setPaddingLeft(15);
    AppView appview = new AppView();
    PdfPCell dateVal = new PdfPCell(new Phrase(": " + appview.date(billDate), fontInvoiceNo()));
    dateVal.setPaddingRight(0);
    PdfPCell dueDateLabel = new PdfPCell();
    PdfPCell dueDateVal = new PdfPCell();
    if (creditDays != 0) {
      dueDateLabel = new PdfPCell(new Phrase("Due Date.", fontBill()));
      dueDateLabel.setPaddingLeft(15);
      dueDateVal = new PdfPCell(new Phrase(": " + appview.date(getDueDate(billDate, creditDays)), fontBill()));
      dueDateVal.setPaddingRight(0);
    }
    table.addCell(bill).setBorder(0);
    table.addCell(billVal).setBorder(0);
    table.addCell(date).setBorder(0);
    table.addCell(dateVal).setBorder(0);
    table.addCell(dueDateLabel).setBorder(0);
    table.addCell(dueDateVal).setBorder(0);
    widths = new float[]{75, c.getWidthPoint() + 15};
    if (billNumber.length() < 7) {
      widths = new float[]{75, 70};
    }

    table.setTotalWidth(widths);
    table.setLockedWidth(true);

    return table;
  }

  @Override
  public PdfPTable setCustomerAddress(Customer customer, CustomerAddress customerAddress, boolean billToPrintable) {
    PdfPTable table = new PdfPTable(1);
    if (billToPrintable) {
      PdfPCell billTo = new PdfPCell(new Phrase("Bill To:", fontHeader()));
      billTo.setPaddingBottom(0);
      table.addCell(billTo).setBorder(0);
    }
    String companyName = customer.getCompanyId() != null ? customer.getCompanyId().getCompanyName() : "";
    PdfPCell customerName = new PdfPCell(new Phrase(customer.getId() == null ? "" : customer.getCustomerName(), (companyName.length() > 31 ? fontTitleForLong() : fontTitle())));
    customerName.setLeading(10, 0);
    customerName.setPaddingTop(0);
    customerName.setPaddingBottom(0);
    Paragraph addressPara = new Paragraph();
    if (customerAddress.getId() != null) {
      if (!StringUtil.isEmpty(customerAddress.getAddress())) {
        addressPara.add(new Phrase(customerAddress.getAddress(), fontHeader()));
      }
      if (!StringUtil.isEmpty(customerAddress.getDistrictId().getDistrictName())) {
        addressPara.add(new Phrase(", ", fontProduct()));
        addressPara.add(new Phrase(customerAddress.getDistrictId().getDistrictName(), fontHeader()));
      }
      if (!StringUtil.isEmpty(customerAddress.getStateId().getStateName())) {
        addressPara.add(new Phrase(", ", fontProduct()));
        addressPara.add(new Phrase(customerAddress.getStateId().getStateName(), fontHeader()));
      }
      if (!StringUtil.isEmpty(customerAddress.getPin())) {
        addressPara.add(new Phrase(", PIN: " + customerAddress.getPin(), fontHeader()));
      }
      if (!StringUtil.isEmpty(customerAddress.getPhone1())) {
        addressPara.add(Chunk.NEWLINE);
        addressPara.add(new Phrase("Phone: " + customerAddress.getPhone1(), fontHeader()));
      }
      if (!StringUtil.isEmpty(customerAddress.getPhone2())) {
        addressPara.add(new Phrase(", " + customerAddress.getPhone2(), fontHeader()));
      }
      if (!StringUtil.isEmpty(customerAddress.getEmail())) {
        addressPara.add(Chunk.NEWLINE);
        addressPara.add(new Phrase("Email: " + customerAddress.getEmail(), fontHeader()));
      }
    }
    PdfPCell addressCell = new PdfPCell(addressPara);
    addressCell.setLeading(10, 0);
    table.addCell(customerName).setBorder(0);
    table.addCell(addressCell).setBorder(0);
    return table;
  }

  @Override
  public PdfPTable setShipingAddress(CustomerAddress shippingAddress) {
    PdfPTable table = new PdfPTable(1);
    PdfPCell billTo = new PdfPCell(new Phrase("Shipping Address:", fontHeader()));
    billTo.setPaddingBottom(0);
    table.addCell(billTo).setBorder(0);
    PdfPCell customerName = new PdfPCell();
    Paragraph addressPara = new Paragraph();
    if (shippingAddress != null) {
      customerName = new PdfPCell(new Phrase(shippingAddress.getCustomerId() == null ? "" : shippingAddress.getCustomerId().getCustomerName(), fontBill()));
      customerName.setLeading(10, 0);
      customerName.setPaddingTop(0);
      customerName.setPaddingBottom(0);
      if (shippingAddress.getId() != null) {
        if (!StringUtil.isEmpty(shippingAddress.getAddress())) {
          addressPara.add(new Phrase(shippingAddress.getAddress(), fontHeader()));
        }
        if (!StringUtil.isEmpty(shippingAddress.getDistrictId().getDistrictName())) {
          addressPara.add(new Phrase(", ", fontProduct()));
          addressPara.add(new Phrase(shippingAddress.getDistrictId().getDistrictName(), fontHeader()));
        }
        if (!StringUtil.isEmpty(shippingAddress.getStateId().getStateName())) {
          addressPara.add(new Phrase(", ", fontProduct()));
          addressPara.add(new Phrase(shippingAddress.getStateId().getStateName(), fontHeader()));
        }
        if (!StringUtil.isEmpty(shippingAddress.getPin())) {
          addressPara.add(new Phrase(", PIN: " + shippingAddress.getPin(), fontHeader()));
        }
        if (!StringUtil.isEmpty(shippingAddress.getPhone1())) {
          addressPara.add(Chunk.NEWLINE);
          addressPara.add(new Phrase("Phone: " + shippingAddress.getPhone1(), fontHeader()));
        }
        if (!StringUtil.isEmpty(shippingAddress.getPhone2())) {
          addressPara.add(new Phrase(", " + shippingAddress.getPhone2(), fontHeader()));
        }
        if (!StringUtil.isEmpty(shippingAddress.getEmail())) {
          addressPara.add(Chunk.NEWLINE);
          addressPara.add(new Phrase("Email: " + shippingAddress.getEmail(), fontHeader()));
        }
      }
    }
    PdfPCell addressCell = new PdfPCell(addressPara);
    addressCell.setLeading(10, 0);
    table.addCell(customerName).setBorder(0);
    table.addCell(addressCell).setBorder(0);

    return table;
  }

  @Override
  public PdfPTable setCarrierDetails(ConsignmentDetail consignmentdetail, String printType, boolean salesReturn, String debitNo, String debitNoteDate, String regNo) throws DocumentException {
    PdfPTable table = new PdfPTable(new float[]{35, 65});
    PdfPCell emptyCell = new PdfPCell(new Phrase(" ", fontHeader()));

    if (salesReturn) {
      PdfPCell debitNoLabelCell = new PdfPCell(new Phrase(" Db. No.", fontBill()));
      Chunk c = new Chunk(": " + debitNo, fontInvoiceNo());
      PdfPCell debitNoCell = new PdfPCell(new Phrase(c));
      PdfPCell date = new PdfPCell(new Phrase(" Db. Date.", fontBill()));
      Chunk d = new Chunk(": " + debitNoteDate, fontInvoiceNo());
      PdfPCell dateVal = new PdfPCell(new Phrase(d));
      PdfPCell RegNoLabelCell = new PdfPCell();
      PdfPCell RegNoCell = new PdfPCell();
      RegNoLabelCell = new PdfPCell(new Phrase(" Reg. No.", fontBill()));
      RegNoCell = new PdfPCell(new Phrase(": " + regNo, fontBill()));
      table.addCell(debitNoLabelCell).setBorder(0);
      table.addCell(debitNoCell).setBorder(0);
      table.addCell(date).setBorder(0);
      table.addCell(dateVal).setBorder(0);
      table.addCell(RegNoLabelCell).setBorder(0);
      table.addCell(RegNoCell).setBorder(0);
      table.getDefaultCell().setPaddingLeft(0);
      widths = new float[]{75, (c.getWidthPoint() > d.getWidthPoint() ? c.getWidthPoint() : d.getWidthPoint()) + 15};
      table.setTotalWidth(widths);
      table.setLockedWidth(true);
    } else {
      PdfPCell carrierVal;
      String lrno = "", lrdt = "";

      if (consignmentdetail != null) {
        lrno = consignmentdetail.getExitDocumentNo() == null ? "" : consignmentdetail.getExitDocumentNo();
        lrdt = consignmentdetail.getLrDate() == null ? "" : consignmentdetail.getLrDate().toString();
      }
      PdfPCell lrNoLabel = new PdfPCell(new Phrase("  L.R. NO", fontHeader()));
      PdfPCell lrNoVal = new PdfPCell(new Phrase(": " + lrno, fontHeader()));
      PdfPCell lrDt = new PdfPCell(new Phrase("  L.R. DT", fontHeader()));
      PdfPCell lrDtVal = new PdfPCell(new Phrase(": " + lrdt, fontHeader()));
      PdfPCell carrier = new PdfPCell(new Phrase("  CARRIER", fontHeader()));
      if (consignmentdetail == null || consignmentdetail.getTransporterId() == null) {
        carrierVal = new PdfPCell(new Phrase(": ", fontHeader()));
      } else {
        carrierVal = new PdfPCell(new Phrase(": " + consignmentdetail.getTransporterId().getTransporterName(), fontHeader()));
      }

      PdfPCell eWay = new PdfPCell(new Phrase("  E.Way Bill", fontHeader()));
      PdfPCell eWayVal = new PdfPCell(new Phrase(": ", fontHeader()));
      PdfPCell noDate = new PdfPCell(new Phrase("  No.Date", fontHeader()));
      PdfPCell noDateVal = new PdfPCell(new Phrase(": ", fontHeader()));
      if (printType.equals(SystemConstants.PRINT_SINGLE_LINE)) {
        table.setTotalWidth(widths);
        lrNoLabel.setPaddingLeft(15);
        lrDt.setPaddingLeft(15);
        carrier.setPaddingLeft(15);
        eWay.setPaddingLeft(15);
        noDate.setPaddingLeft(15);
      } else {
        table.setTotalWidth(125);
      }
      table.setLockedWidth(true);

      if (!printType.equals(SystemConstants.PRINT_SINGLE_LINE)) {
        table.addCell(emptyCell).setBorder(0);
        table.addCell(emptyCell).setBorder(0);
      }
      table.addCell(lrNoLabel).setBorder(0);
      table.addCell(lrNoVal).setBorder(0);
      table.addCell(lrDt).setBorder(0);
      table.addCell(lrDtVal).setBorder(0);
      table.addCell(carrier).setBorder(0);
      table.addCell(carrierVal).setBorder(0);
      table.addCell(eWay).setBorder(0);
      table.addCell(eWayVal).setBorder(0);
      table.addCell(noDate).setBorder(0);
      table.addCell(noDateVal).setBorder(0);
    }
    return table;
  }

  @Override
  public PdfPTable setGStInvoiceLabel(String label) {
    PdfPTable table = new PdfPTable(1);
    table.getDefaultCell().setBorder(0);
    PdfPCell cell = new PdfPCell(new Phrase(label, fontTitle()));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorder(0);
    table.addCell(cell);
    table.setWidthPercentage(100);

    return table;
  }

  public Date getDueDate(Date invoiceDate, int creditDays) {
    return DateUtil.moveDays(invoiceDate, creditDays);
  }

  @Override
  public PdfPTable setCustomerGST(Customer customer, List<CustomerLicense> customerLicenseList, String printType) {

    PdfPTable table = new PdfPTable(new float[]{27, 73});
    table.setWidthPercentage(30);
    PdfPCell emptyCell = new PdfPCell(new Phrase(" ", fontSubHeader()));
    PdfPCell gst = new PdfPCell(new Phrase("GST NO", fontSubHeader()));
    gst.setLeading(5, 0);
    PdfPCell gstVal = new PdfPCell(new Phrase(":   " + (customer.getGstNo() != null ? customer.getGstNo().toUpperCase() : ""), fontSubHeader()));
    gstVal.setLeading(5, 0);
    PdfPCell pan = new PdfPCell(new Phrase("PAN NO", fontSubHeader()));
    pan.setLeading(5, 0);
    PdfPCell panVal = new PdfPCell(new Phrase(":   " + (customer.getPanNo() != null ? customer.getPanNo().toUpperCase() : ""), fontSubHeader()));
    panVal.setLeading(5, 0);

    //  For FSSAI
    PdfPCell fssai = new PdfPCell(new Phrase("FSSAI NO", fontProduct()));
    fssai.setLeading(5, 0);
    Paragraph para = new Paragraph();
    para.add(new Phrase(":   ", fontHeader()));
    for (CustomerLicense license : customerLicenseList) {
      if (license.getLicenseTypeId() != null) {
        if (Objects.equals(license.getLicenseTypeId().getId(), SystemConstants.FSSAI_LIC)) {
          para.add(new Phrase(license.getLicenseKey(), fontSubHeader()));
          para.add(Chunk.NEWLINE);
          para.add(new Phrase("    ", fontSubHeader()));
        }
      }
    }
    PdfPCell fssaiVal = new PdfPCell(para);
    fssaiVal.setLeading(5, 0);
    // For D.L.No
    PdfPCell dl = new PdfPCell(new Phrase("D.L NO", fontSubHeader()));
    dl.setLeading(5, 0);
    para = new Paragraph();
    para.add(new Phrase(":   ", fontSubHeader()));
    for (CustomerLicense license : customerLicenseList) {
      if (license.getLicenseTypeId() != null) {
        if (Objects.equals(license.getLicenseTypeId().getId(), SystemConstants.DRUG_LIC)) {
          para.add(new Phrase(license.getLicenseKey(), fontSubHeader()));
          para.add(Chunk.NEWLINE);
          para.add(Chunk.NEWLINE);
          para.add(new Phrase("    ", fontSubHeader()));
        }
      }
    }
    PdfPCell dlVal = new PdfPCell(para);
    dlVal.setPaddingBottom(-5);
    dlVal.setLeading(5, 0);
    if (!printType.equals(SystemConstants.PRINT_SINGLE_LINE)) {
      table.addCell(emptyCell).setBorder(0);
      table.addCell(emptyCell).setBorder(0);
    }
    table.addCell(gst).setBorder(0);
    table.addCell(gstVal).setBorder(0);
    table.addCell(pan).setBorder(0);
    table.addCell(panVal).setBorder(0);
    table.addCell(fssai).setBorder(0);
    table.addCell(fssaiVal).setBorder(0);
    table.addCell(dl).setBorder(0);
    table.addCell(dlVal).setBorder(0);

    return table;

  }

  @Override
  public PdfPTable setSalesReturnLabel(Integer salesReturnType, Integer confirm) {
    PdfPTable table = new PdfPTable(1);
    table.getDefaultCell().setBorder(0);
    PdfPCell cell = new PdfPCell(new Phrase((salesReturnType == 1 ? "CREDIT NOTE SALEABLE RETURN" : "CREDIT NOTE DAMAGE/EXPIRY RETURN") + (confirm == 1 ? " (DRAFT)" : ""),
            fontTitle()));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorder(0);
    table.addCell(cell);
    table.setWidthPercentage(100);

    return table;
  }

  @Override
  public PdfPTable setVendorAddress(Vendor vendor, VendorAddress vendorAddress) {
    PdfPTable table = new PdfPTable(1);
    PdfPCell customerName = new PdfPCell(new Phrase(vendor.getId() == null ? "" : vendor.getVendorName(), fontTitle()));
    customerName.setLeading(10, 0);
    customerName.setPaddingTop(0);
    customerName.setPaddingBottom(0);
    Paragraph addressPara = new Paragraph();
    if (vendorAddress.getId() != null) {
      if (!StringUtil.isEmpty(vendorAddress.getAddress())) {
        addressPara.add(new Phrase(vendorAddress.getAddress(), fontHeader()));
      }
      if (!StringUtil.isEmpty(vendorAddress.getStateId().getStateName())) {
        addressPara.add(new Phrase(", ", fontProduct()));
        addressPara.add(new Phrase(vendorAddress.getStateId().getStateName(), fontHeader()));
      }
      if (!StringUtil.isEmpty(vendorAddress.getPin())) {
        addressPara.add(new Phrase(", PIN: " + vendorAddress.getPin(), fontHeader()));
      }
      if (!StringUtil.isEmpty(vendorAddress.getPhone1())) {
        addressPara.add(new Phrase(", Phone: " + vendorAddress.getPhone1(), fontHeader()));
      }
      if (!StringUtil.isEmpty(vendorAddress.getPhone2())) {
        addressPara.add(new Phrase(", Mob: " + vendorAddress.getPhone2(), fontHeader()));
      }
      if (!StringUtil.isEmpty(vendorAddress.getEmail())) {
        addressPara.add(new Phrase(", Email: " + vendorAddress.getEmail(), fontHeader()));
      }
    }
    PdfPCell addressCell = new PdfPCell(addressPara);
    addressCell.setLeading(10, 0);
    table.addCell(customerName).setBorder(0);
    table.addCell(addressCell).setBorder(0);
    return table;
  }

  @Override
  public PdfPTable setVendorGST(Vendor vendor, List<VendorLicense> vendorLicenseList, String printType) {
    PdfPTable table = new PdfPTable(new float[]{30, 70});
    PdfPCell emptyCell = new PdfPCell(new Phrase(" ", fontHeader()));
    PdfPCell gst = new PdfPCell(new Phrase("GST NO", fontHeader()));
    gst.setLeading(5, 0);
    PdfPCell gstVal = new PdfPCell(new Phrase(": " + vendor.getGstNo(), fontHeader()));
    gstVal.setLeading(5, 0);
    PdfPCell pan = new PdfPCell(new Phrase("PAN NO", fontHeader()));
    pan.setLeading(5, 0);
    PdfPCell panVal = new PdfPCell(new Phrase(": " + vendor.getPanNo(), fontHeader()));
    panVal.setLeading(5, 0);

    //  For FSSAI
    PdfPCell fssai = new PdfPCell(new Phrase("FSSAI NO", fontHeader()));
    fssai.setLeading(5, 0);
    Paragraph para = new Paragraph();
    para.add(new Phrase(": ", fontHeader()));
    for (VendorLicense license : vendorLicenseList) {
      if (license.getLicenseTypeId() != null) {
        if (Objects.equals(license.getLicenseTypeId().getId(), SystemConstants.FSSAI_LIC)) {
          para.add(new Phrase(license.getLicenseKey(), fontHeader()));
          para.add(Chunk.NEWLINE);
          para.add(new Phrase("  ", fontHeader()));
        }
      }
    }
    PdfPCell fssaiVal = new PdfPCell(para);
    fssaiVal.setLeading(5, 0);
    // For D.L.No
    PdfPCell dl = new PdfPCell(new Phrase("D.L NO", fontHeader()));
    dl.setLeading(5, 0);
    para = new Paragraph();
    para.add(new Phrase(": ", fontHeader()));
    for (VendorLicense license : vendorLicenseList) {
      if (license.getLicenseTypeId() != null) {
        if (Objects.equals(license.getLicenseTypeId().getId(), SystemConstants.DRUG_LIC)) {
          para.add(new Phrase(license.getLicenseKey(), fontHeader()));
          para.add(Chunk.NEWLINE);
          para.add(Chunk.NEWLINE);
          para.add(new Phrase("  ", fontHeader()));
        }
      }
    }
    PdfPCell dlVal = new PdfPCell(para);
    dlVal.setPaddingBottom(-5);
    dlVal.setLeading(5, 0);
    if (!printType.equals(SystemConstants.PRINT_SINGLE_LINE)) {
      table.addCell(emptyCell).setBorder(0);
      table.addCell(emptyCell).setBorder(0);
    }
    table.addCell(gst).setBorder(0);
    table.addCell(gstVal).setBorder(0);
    table.addCell(pan).setBorder(0);
    table.addCell(panVal).setBorder(0);
    table.addCell(fssai).setBorder(0);
    table.addCell(fssaiVal).setBorder(0);
    table.addCell(dl).setBorder(0);
    table.addCell(dlVal).setBorder(0);

    return table;
  }

  @Override
  public PdfPTable setUserAddress(UserProfile userProfile) {
    PdfPTable table = new PdfPTable(1);
    PdfPCell customerName = new PdfPCell(new Phrase(userProfile.getId() == null ? "" : userProfile.getName(), fontTitle()));
    customerName.setLeading(10, 0);
    customerName.setPaddingTop(0);
    customerName.setPaddingBottom(0);
    Paragraph addressPara = new Paragraph();
    if (userProfile.getId() != null) {
      if (!StringUtil.isEmpty(userProfile.getAddress1())) {
        addressPara.add(new Phrase(userProfile.getAddress1(), fontHeader()));
      }
      if (!StringUtil.isEmpty(userProfile.getAddress2())) {
        addressPara.add(new Phrase(", ", fontProduct()));
        addressPara.add(new Phrase(userProfile.getAddress2(), fontHeader()));
        addressPara.add(new Phrase(", ", fontProduct()));
      }
      if (!StringUtil.isEmpty(userProfile.getPhone1())) {
        addressPara.add(new Phrase("Phone: " + userProfile.getPhone1(), fontHeader()));
      }
      if (!StringUtil.isEmpty(userProfile.getPhone2())) {
        addressPara.add(new Phrase(", Mob: " + userProfile.getPhone2(), fontHeader()));
      }
      if (!StringUtil.isEmpty(userProfile.getEmail())) {
        addressPara.add(Chunk.NEWLINE);
        addressPara.add(new Phrase("Email: " + userProfile.getEmail(), fontHeader()));
      }
    }
    PdfPCell addressCell = new PdfPCell(addressPara);
    addressCell.setLeading(10, 0);
    table.addCell(customerName).setBorder(0);
    table.addCell(addressCell).setBorder(0);
    return table;
  }

  @Override
  public PdfPTable setUserGST(UserProfile userProfile, String printType) {
    PdfPTable table = new PdfPTable(new float[]{30, 70});
    PdfPCell emptyCell = new PdfPCell(new Phrase(" ", fontHeader()));
    PdfPCell gst = new PdfPCell(new Phrase("GST NO", fontHeader()));
    gst.setLeading(5, 0);
    PdfPCell gstVal = new PdfPCell(new Phrase(": " + (userProfile.getGstNo() == null ? "" : userProfile.getGstNo()), fontHeader()));
    gstVal.setLeading(5, 0);
    PdfPCell pan = new PdfPCell(new Phrase("PAN NO", fontHeader()));
    pan.setLeading(5, 0);
    PdfPCell panVal = new PdfPCell(new Phrase(": " + (userProfile.getPanNo() == null ? "" : userProfile.getPanNo()), fontHeader()));
    panVal.setLeading(5, 0);

    if (!printType.equals(SystemConstants.PRINT_SINGLE_LINE)) {
      table.addCell(emptyCell).setBorder(0);
      table.addCell(emptyCell).setBorder(0);
    }
    table.addCell(gst).setBorder(0);
    table.addCell(gstVal).setBorder(0);
    table.addCell(pan).setBorder(0);
    table.addCell(panVal).setBorder(0);

    return table;
  }

  @Override
  public PdfPTable setAddress(String name, String address, String state) {
    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(30);
    PdfPCell companyName = new PdfPCell(new Phrase(name, fontTitle()));
    companyName.setLeading(10, 0);
    Paragraph addressPara = new Paragraph();
    if (address != null) {
      addressPara.add(new Phrase(address, fontHeader()));
    }
    if (state != null) {
      addressPara.add(new Phrase(", ", fontProduct()));
      addressPara.add(new Phrase(state, fontHeader()));
    }
    PdfPCell addressCell = new PdfPCell(addressPara);
    addressCell.setLeading(10, 0);
    table.addCell(companyName).setBorder(0);
    table.addCell(addressCell).setBorder(0);

    return table;
  }

  @Override
  public PdfPTable setLicense(String gst, String fssai) {
    PdfPTable table = new PdfPTable(new float[]{25, 75});
    table.setWidthPercentage(30);
    table.setHorizontalAlignment(Element.ALIGN_CENTER);
    PdfPCell gstNo = new PdfPCell();
    gstNo = new PdfPCell(new Phrase("GST No.", fontHeader()));
    gstNo.setLeading(5, 0);
    PdfPCell gstVal = new PdfPCell(new Phrase(": " + (gst == null ? "" : gst), fontHeader()));
    gstVal.setLeading(5, 0);

    PdfPCell fssaiNo = new PdfPCell();
    fssaiNo = new PdfPCell(new Phrase("FSSAI No.", fontHeader()));
    fssaiNo.setLeading(5, 0);
    PdfPCell fssaiVal = new PdfPCell(new Phrase(": " + (fssai == null ? "" : fssai), fontHeader()));
    fssaiVal.setLeading(5, 0);
    table.addCell(gstNo).setBorder(0);
    table.addCell(gstVal).setBorder(0);
    table.addCell(fssaiNo).setBorder(0);
    table.addCell(fssaiVal).setBorder(0);
    return table;
  }

  private PdfPTable createCompanyAddressWithlogo(CompanyAddress companyAddress, Company company, String logo) {
    PdfPTable table = new PdfPTable(new float[]{20, 80});
    table.setWidthPercentage(30);
    try {
      PdfPCell logoCell = new PdfPCell();
      byte[] imageBytes = Base64.getDecoder().decode(logo);
      Image image = Image.getInstance(imageBytes);
      logoCell.addElement((Element) image);
      logoCell.setBorder(0);

      if (company.getId() != null) {
        PdfPCell companyName = new PdfPCell(new Phrase(company.getCompanyName(), fontTitle()));
        companyName.setLeading(10, 0);

        PdfPCell addressCell = createAddressCell(companyAddress);
        addressCell.setLeading(10, 0);

        PdfPTable aT = new PdfPTable(1);
        aT.getDefaultCell().setBorder(0);
        aT.addCell(companyName).setBorder(0);
        aT.addCell(addressCell).setBorder(0);
        PdfPCell aTcell = new PdfPCell();
        aTcell.setBorder(0);
        aTcell.addElement(aT);

        table.addCell(logoCell);
        table.addCell(aTcell).setBorder(0);

      }

    } catch (BadElementException | IOException ex) {
      Logger.getLogger(PageHeaderImpl.class.getName()).log(Level.SEVERE, null, ex);
    }
    return table;

  }

  private PdfPTable createCompanyAddress(CompanyAddress companyAddress, Company company) {
    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(30);
    if (company.getId() != null) {
      PdfPCell companyName = new PdfPCell(new Phrase(company.getCompanyName(), (company.getCompanyName().length() > 31 ? fontTitleForLong() : fontTitle())));
      companyName.setLeading(10, 0);
      PdfPCell addressCell = createAddressCell(companyAddress);
      addressCell.setLeading(10, 0);
      table.addCell(companyName).setBorder(0);
      table.addCell(addressCell).setBorder(0);
    }
    return table;

  }

  private PdfPCell createAddressCell(CompanyAddress companyAddress) {

    Paragraph addressPara = new Paragraph();
    if (companyAddress.getId() != null) {
      if (companyAddress.getAddress() != null) {
        addressPara.add(new Phrase(companyAddress.getAddress(), fontHeader()));
        addressPara.add(Chunk.NEWLINE);
      }
      if (companyAddress.getDistrictId().getDistrictName() != null) {
        addressPara.add(new Phrase(companyAddress.getDistrictId().getDistrictName(), fontHeader()));
      }
      if (!StringUtil.isEmpty(companyAddress.getStateId().getStateName())) {
        addressPara.add(new Phrase(", ", fontProduct()));
        addressPara.add(new Phrase(companyAddress.getStateId().getStateName(), fontHeader()));
      }
      if (!StringUtil.isEmpty(companyAddress.getPin())) {
        addressPara.add(new Phrase(", PIN: " + companyAddress.getPin(), fontHeader()));
      }
      if (!StringUtil.isEmpty(companyAddress.getPhone1())) {
        addressPara.add(Chunk.NEWLINE);
        addressPara.add(new Phrase("Phone: " + companyAddress.getPhone1(), fontHeader()));
      }
    }
    return new PdfPCell(addressPara);

  }

  @Override
  public PdfPTable setPurchaseReturnLabel(Integer returnType, Integer confirm) {
    PdfPTable table = new PdfPTable(1);
    table.getDefaultCell().setBorder(0);
    PdfPCell cell = new PdfPCell(new Phrase((returnType == 2 ? "PURCHASE RETURN - NON-MOVING / NEAR EXPIRY" : "PURCHASE RETURN - DAMAGED & EXPIRED") + (confirm == 1 ? " (DRAFT)" : ""),
            fontTitle()));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorder(0);
    table.addCell(cell);
    table.setWidthPercentage(100);

    return table;
  }

  @Override
  public PdfPTable setRecipientAddress(RecipientAddress recipientAddress, boolean billToPrintable) {
    PdfPTable table = new PdfPTable(1);
    if (billToPrintable) {
      PdfPCell billTo = new PdfPCell(new Phrase("Bill To:", fontHeader()));
      billTo.setPaddingBottom(0);
      table.addCell(billTo).setBorder(0);
    }
    PdfPCell customerName = new PdfPCell(new Phrase(recipientAddress.getId() == null ? "" : recipientAddress.getName(), fontTitle()));
    customerName.setLeading(10, 0);
    customerName.setPaddingTop(0);
    customerName.setPaddingBottom(0);
    Paragraph addressPara = new Paragraph();
    if (recipientAddress.getId() != null) {
      if (!StringUtil.isEmpty(recipientAddress.getAddress())) {
        addressPara.add(new Phrase(recipientAddress.getAddress(), fontHeader()));
      }
      if (!StringUtil.isEmpty(recipientAddress.getState())) {
        addressPara.add(new Phrase(", ", fontProduct()));
        addressPara.add(new Phrase(recipientAddress.getState(), fontHeader()));
      }
      if (!StringUtil.isEmpty(recipientAddress.getPinCode())) {
        addressPara.add(new Phrase(", PIN: " + recipientAddress.getPinCode(), fontHeader()));
      }
      if (!StringUtil.isEmpty(recipientAddress.getPhone())) {
        addressPara.add(new Phrase(", Phone: " + recipientAddress.getPhone(), fontHeader()));
      }
      if (!StringUtil.isEmpty(recipientAddress.getPhone1())) {
        addressPara.add(new Phrase(", Mob: " + recipientAddress.getPhone1(), fontHeader()));
      }
      if (!StringUtil.isEmpty(recipientAddress.getEmail())) {
        addressPara.add(new Phrase(", Email: " + recipientAddress.getEmail(), fontHeader()));
      }
    }
    PdfPCell addressCell = new PdfPCell(addressPara);
    addressCell.setLeading(10, 0);
    table.addCell(customerName).setBorder(0);
    table.addCell(addressCell).setBorder(0);
    return table;
  }

  @Override
  public PdfPTable setRecipientGST(RecipientAddress recipientAddress, String printType) {
    PdfPTable table = new PdfPTable(new float[]{30, 70});
    PdfPCell emptyCell = new PdfPCell(new Phrase(" ", fontHeader()));
    PdfPCell gst = new PdfPCell(new Phrase("GST NO", fontHeader()));
    gst.setLeading(5, 0);
    PdfPCell gstVal = new PdfPCell(new Phrase(": " + recipientAddress.getGstNo(), fontHeader()));
    gstVal.setLeading(5, 0);
    PdfPCell pan = new PdfPCell(new Phrase("PAN NO", fontHeader()));
    pan.setLeading(5, 0);
    PdfPCell panVal = new PdfPCell(new Phrase(": " + recipientAddress.getPanNo(), fontHeader()));
    panVal.setLeading(5, 0);

    //  For FSSAI
    PdfPCell fssai = new PdfPCell(new Phrase("FSSAI NO", fontHeader()));
    fssai.setLeading(5, 0);
    Paragraph para = new Paragraph();
    para.add(new Phrase(": ", fontHeader()));
    if (recipientAddress.getFssai() != null) {
      for (String license : recipientAddress.getFssai()) {
        if (license != null) {
          para.add(new Phrase(license, fontHeader()));
          para.add(Chunk.NEWLINE);
          para.add(new Phrase("  ", fontHeader()));
        }
      }
    }

    PdfPCell fssaiVal = new PdfPCell(para);
    fssaiVal.setLeading(5, 0);
    // For D.L.No
    PdfPCell dl = new PdfPCell(new Phrase("D.L NO", fontHeader()));
    dl.setLeading(5, 0);
    para = new Paragraph();
    para.add(new Phrase(": ", fontHeader()));
    if (recipientAddress.getVendorLicenseList() != null) {
      for (VendorLicense vendorLicense : recipientAddress.getVendorLicenseList()) {
        if (Objects.equals(vendorLicense.getLicenseTypeId().getId(), SystemConstants.DRUG_LIC)) {
          para.add(new Phrase(vendorLicense.getLicenseKey(), fontHeader()));
          para.add(Chunk.NEWLINE);
          para.add(Chunk.NEWLINE);
          para.add(new Phrase("  ", fontHeader()));
        }
      }
    }
    PdfPCell dlVal = new PdfPCell(para);
    dlVal.setPaddingBottom(-5);
    dlVal.setLeading(5, 0);
    if (!printType.equals(SystemConstants.PRINT_SINGLE_LINE)) {
      table.addCell(emptyCell).setBorder(0);
      table.addCell(emptyCell).setBorder(0);
    }
    table.addCell(gst).setBorder(0);
    table.addCell(gstVal).setBorder(0);
    table.addCell(pan).setBorder(0);
    table.addCell(panVal).setBorder(0);
    table.addCell(fssai).setBorder(0);
    table.addCell(fssaiVal).setBorder(0);
    table.addCell(dl).setBorder(0);
    table.addCell(dlVal).setBorder(0);

    return table;
  }

  @Override
  public PdfPTable setRecipientShipingAddress(RecipientAddress shippingAddress) {
    PdfPTable table = new PdfPTable(1);
    PdfPCell addressLabel = new PdfPCell(new Phrase("Shipping Address:", fontHeader()));
    PdfPCell shippingName = new PdfPCell();
    Paragraph shippingPara = new Paragraph();
//     if (salesInvoice.getShippingAddressId() != null) {
//      if (salesInvoice.getShippingAddressId().getCustomerId().getCustomerName() != null) {
//        shippingName = new PdfPCell(new Phrase(salesInvoice.getShippingAddressId().getCustomerId() == null
//                ? "" : salesInvoice.getShippingAddressId().getCustomerId().getCustomerName(), fontTitle()));
//      }
//
//      if (salesInvoice.getCustomerAddressId().getAddress() != null) {
//        shippingPara.add(new Phrase(salesInvoice.getShippingAddressId().getAddress(), fontHeader()));
//      }
//      if (salesInvoice.getCustomerAddressId().getStateId().getStateName() != null) {
//        shippingPara.add(new Phrase(", " + salesInvoice.getShippingAddressId().getStateId() == null
//                ? "" : salesInvoice.getShippingAddressId().getStateId().getStateName(), fontHeader()));
//      }
//      if (salesInvoice.getShippingAddressId().getPin() != null) {
//        shippingPara.add(new Phrase(", PIN: " + salesInvoice.getShippingAddressId().getPin(), fontHeader()));
//      }
//      if (salesInvoice.getShippingAddressId().getPhone1() != null) {
//        shippingPara.add(new Phrase(", Phone: " + salesInvoice.getShippingAddressId().getPhone1(), fontHeader()));
//      }
//      if (salesInvoice.getShippingAddressId().getPhone2() != null) {
//        shippingPara.add(new Phrase(", Mob: " + salesInvoice.getShippingAddressId().getPhone2(), fontHeader()));
//      }
//      if (salesInvoice.getShippingAddressId().getEmail() != null) {
//        shippingPara.add(new Phrase(", Email: " + salesInvoice.getShippingAddressId().getEmail(), fontHeader()));
//      }
//    }
    PdfPCell address = new PdfPCell(shippingPara);
    address.setLeading(10, 0);
    table.addCell(addressLabel).setBorder(0);
    table.addCell(shippingName).setBorder(0);
    table.addCell(address).setBorder(0);

    return table;
  }

  @Override
  public PdfPTable setVendorAddress(Vendor vendor, VendorAddress vendorAddress, boolean billTo) {
    return new PdfPTable(new float[1]);
  }

}
