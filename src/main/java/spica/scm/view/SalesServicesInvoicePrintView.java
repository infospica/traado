/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerLicense;
import spica.scm.domain.SalesServicesInvoice;
import spica.scm.domain.SalesServicesInvoiceItem;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorLicense;
import spica.scm.print.PrintSalesServicesInvoice;
import spica.scm.service.CompanyAddressService;
import spica.scm.service.CompanyLicenseService;
import spica.scm.service.CustomerAddressService;
import spica.scm.service.CustomerLicenseService;
import spica.scm.service.CustomerService;
import spica.scm.service.SalesServicesInvoiceItemService;
import spica.scm.service.UserProfileService;
import spica.scm.service.VendorAddressService;
import spica.scm.service.VendorLicenseService;
import spica.scm.service.VendorService;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author godson
 */
@Named(value = "salesServicesInvoicePrintView")
@ViewScoped
public class SalesServicesInvoicePrintView implements Serializable {

  private transient SalesServicesInvoice salesServicesInvoice;
  private CompanySettings companySettings;
  private CompanyAddress companyAddress;
  private CustomerAddress customerAddress;
  private List<CustomerLicense> customerLicenseList;
  private List<CompanyLicense> companyLicenseList;
  private List<SalesServicesInvoiceItem> salesServicesInvoicesItemList;
  private Customer customer;
  private ConsignmentDetail consignmentDetail;
  private Vendor vendor;
  private VendorAddress vendorAddress;
  private List<VendorLicense> vendorLicenseList;
  private UserProfile userProfile;

  @PostConstruct
  public void init() {
    salesServicesInvoice = (SalesServicesInvoice) Jsf.popupParentValue(SalesServicesInvoice.class);
  }

  public void salesServicesInvoicePrint(MainView main) {
    try {
      setSalesServicesInvoicesItemList(SalesServicesInvoiceItemService.selectBySalesInvoiceService(main, getSalesServicesInvoice()));
      if (salesServicesInvoice.getAccountingLedgerId().isCustomer()) {
        customer = (Customer) CustomerService.selectByPk(main, new Customer(salesServicesInvoice.getAccountingLedgerId().getEntityId()));
      } else if (salesServicesInvoice.getAccountingLedgerId().getAccountingEntityTypeId().getId() == 1) {
        vendor = (Vendor) VendorService.selectByPk(main, new Vendor(salesServicesInvoice.getAccountingLedgerId().getEntityId()));
      } else if (salesServicesInvoice.getAccountingLedgerId().getAccountingEntityTypeId().getId() == 3) {
        userProfile = (UserProfile) UserProfileService.selectByPk(main, new UserProfile(salesServicesInvoice.getAccountingLedgerId().getEntityId()));
      }
      companySettings = UserRuntimeView.instance().getCompany().getCompanySettings();
      companyAddress = CompanyAddressService.selectCompanyRegisteredAddress(main, getSalesServicesInvoice().getCompanyId());
      customerAddress = CustomerAddressService.selectCustomerRegisteredAddress(main, new Customer(salesServicesInvoice.getAccountingLedgerId().getEntityId()));
      customerLicenseList = CustomerLicenseService.listActiveLicenseBycustomer(main, new Customer(salesServicesInvoice.getAccountingLedgerId().getEntityId()), SystemConstants.ACTIVE_LICENSE);
      companyLicenseList = CompanyLicenseService.listActiveLicenseByCompany(main, getSalesServicesInvoice().getCompanyId(), SystemConstants.ACTIVE_LICENSE);
      vendorAddress = VendorAddressService.selectDefaultVendorAddress(main, new Vendor(salesServicesInvoice.getAccountingLedgerId().getEntityId()));
      vendorLicenseList = VendorLicenseService.licenseListByVendor(main, new Vendor(salesServicesInvoice.getAccountingLedgerId().getEntityId()));
//      consignmentDetail = ConsignmentDetailService.selectConsignmentDetailByConsignment(main, salesServicesInvoice.getAccountingLedgerId().get.getId());

    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void printIText(MainView main) {
    Document document = null;
    try {
      FacesContext fc = FacesContext.getCurrentInstance();
      HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
      String fileName = getSalesServicesInvoice().getSerialNo() + ".pdf";
      response.setContentType("application/pdf");
      response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

      PdfWriter pdfWriter;
      Rectangle pageSize = new Rectangle(595, 842);
      document = new Document(pageSize, 20, 20, 20, 20);
      pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
      document.open();
      PrintSalesServicesInvoice printSalesServicesInvoice = new PrintSalesServicesInvoice();
      printSalesServicesInvoice.setCompanyAddress(companyAddress);
      printSalesServicesInvoice.setCustomerAddress(customerAddress);
      if (salesServicesInvoice.getAccountingLedgerId().isCustomer()) {
        printSalesServicesInvoice.setCustomer(customer);

      } else if (salesServicesInvoice.getAccountingLedgerId().getAccountingEntityTypeId().getId() == 1) {
        printSalesServicesInvoice.setVendor(vendor);
      } else if (salesServicesInvoice.getAccountingLedgerId().getAccountingEntityTypeId().getId() == 3) {
        printSalesServicesInvoice.setUserProfile(userProfile);
      }
      printSalesServicesInvoice.setVendorAddress(vendorAddress);
      printSalesServicesInvoice.setVendorLicenseList(vendorLicenseList);
      printSalesServicesInvoice.setCompanyLicenseList(companyLicenseList);
      printSalesServicesInvoice.setCustomerLicenseList(customerLicenseList);
      printSalesServicesInvoice.setCompanySettings(companySettings);
      printSalesServicesInvoice.setSalesServicesInvoicesItemList(salesServicesInvoicesItemList);

//      printSalesServicesInvoice.setConsignmentDetail(consignmentDetail);    
      document.add(printSalesServicesInvoice.initiatePrint(salesServicesInvoice, document));
      printSalesServicesInvoice.createFooterBar(document, pdfWriter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      document.close();
    }
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public SalesServicesInvoice getSalesServicesInvoice() {
    return salesServicesInvoice;
  }

  public void setSalesServicesInvoice(SalesServicesInvoice salesServicesInvoice) {
    this.salesServicesInvoice = salesServicesInvoice;
  }

  public List<SalesServicesInvoiceItem> getSalesServicesInvoicesItemList() {
    return salesServicesInvoicesItemList;
  }

  public void setSalesServicesInvoicesItemList(List<SalesServicesInvoiceItem> salesServicesInvoicesItemList) {
    this.salesServicesInvoicesItemList = salesServicesInvoicesItemList;
  }

  public CompanySettings getCompanySettings() {
    return companySettings;
  }

  public void setCompanySettings(CompanySettings companySettings) {
    this.companySettings = companySettings;
  }

  public CompanyAddress getCompanyAddress() {
    return companyAddress;
  }

  public void setCompanyAddress(CompanyAddress companyAddress) {
    this.companyAddress = companyAddress;
  }

  public List<CustomerLicense> getCustomerLicenseList() {
    return customerLicenseList;
  }

  public void setCustomerLicenseList(List<CustomerLicense> customerLicenseList) {
    this.customerLicenseList = customerLicenseList;
  }

  public List<CompanyLicense> getCompanyLicenseList() {
    return companyLicenseList;
  }

  public void setCompanyLicenseList(List<CompanyLicense> companyLicenseList) {
    this.companyLicenseList = companyLicenseList;
  }

  public CustomerAddress getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(CustomerAddress customerAddress) {
    this.customerAddress = customerAddress;
  }

  public ConsignmentDetail getConsignmentDetail() {
    return consignmentDetail;
  }

  public void setConsignmentDetail(ConsignmentDetail consignmentDetail) {
    this.consignmentDetail = consignmentDetail;
  }

  public Vendor getVendor() {
    return vendor;
  }

  public void setVendor(Vendor vendor) {
    this.vendor = vendor;
  }

  public UserProfile getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

}
