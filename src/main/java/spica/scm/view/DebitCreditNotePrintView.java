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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.service.AccountingLedgerService;
import spica.fin.service.DebitCreditNoteItemService;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerLicense;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorLicense;
import spica.scm.print.PageFooterEvent;
import spica.scm.print.PrintDebitCreditNote;
import spica.scm.service.CompanyAddressService;
import spica.scm.service.CompanyLicenseService;
import spica.scm.service.CustomerAddressService;
import spica.scm.service.CustomerService;
import spica.scm.util.CurencyToText;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
@Named(value = "debitCreditNotePrintView")
@ViewScoped
public class DebitCreditNotePrintView implements Serializable {

  private DebitCreditNote debitCreditNote;
  private List<DebitCreditNoteItem> debitCreditNoteItemList;
  private CompanySettings companySettings;
  private CompanyAddress companyAddress;
  private CustomerAddress customerAddress;
  private AccountingLedger accountingLedger;
  private List<CustomerLicense> customerLicenseList;
  private List<CompanyLicense> companyLicenseList;
  private Customer customer;
  private Vendor vendor;
  private VendorAddress vendorAddress;
  private List<VendorLicense> vendorLicenseList;
  private String printType;
  private transient String address;
  private transient String state;
  private transient String gst;
  private transient String fssai;

  private int incrementCount = 0;
  private transient List<String> dummyList;
  private int breakCounter = 0;
  private int lastIncrementCount = 0;
  private boolean intrastate;
  private transient String headerLabel;

  @PostConstruct
  public void init() {
    debitCreditNote = (DebitCreditNote) Jsf.popupParentValue(DebitCreditNote.class);
    setPrintType(getDebitCreditNote().getPrintType());

  }

  public void debitCreditPrint(MainView main) {
    setDebitCreditNoteItemList(DebitCreditNoteItemService.selectByDebitCreditNote(main, getDebitCreditNote()));
    companySettings = UserRuntimeView.instance().getCompany().getCompanySettings();
    companyAddress = CompanyAddressService.selectCompanyRegisteredAddress(main, getDebitCreditNote().getCompanyId());
    accountingLedger = AccountingLedgerService.selectByPk(main, getDebitCreditNote().getAccountingLedgerId());
    companyLicenseList = CompanyLicenseService.licenseListByCompany(main, getDebitCreditNote().getCompanyId());
    if (accountingLedger != null && accountingLedger.getStateId() != null) {
      if (accountingLedger.getStateId().getId().equals(getDebitCreditNote().getCompanyId().getStateId().getId())) {
        setIntrastate(true);
      }
    }
    if (accountingLedger.isCustomer() && accountingLedger.getEntityId() != null) {
      customer = CustomerService.selectByPk(main, new Customer(accountingLedger.getEntityId()));
    }
    if (customer != null && customer.getId() != null) {
      CustomerAddress customerAddress = CustomerAddressService.selectCustomerRegisteredAddress(main, customer);
      address = customerAddress.getAddress();
      state = customer.getStateId() != null ? customer.getStateId().getStateName() : "";
      gst = customer.getGstNo();
      fssai = customer.getFssaiNo();
    } else {
      address = accountingLedger.getAddress();
      state = accountingLedger.getStateId() != null ? accountingLedger.getStateId().getStateName() : "";
      gst = accountingLedger.getGstTaxNo();

    }
    setHeaderLabel(" DEBIT CREDIT NOTE ");
  }

  public void incrementCount() {
    incrementCount++;
  }

  public int getIncrementCount() {
    return incrementCount;
  }
  private int firstBreakRow = 27;
  private int lastBreakRow = 15;

  public void setIncrementCount(Integer currentIndex, boolean isLandscape) {
    breakCounter++;
    lastIncrementCount = getIncrementCount();
    if (currentIndex != null && currentIndex > 0) {
      this.incrementCount = 0;
      firstBreakRow = isLandscape ? 35 : 35;
      lastBreakRow = isLandscape ? 20 : 20;
    } else {
      firstBreakRow = isLandscape ? 27 : 27;
      lastBreakRow = isLandscape ? 18 : 20;
      if (companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE) && isLandscape) {
        firstBreakRow = 32;
        lastBreakRow = 23;
      }
    }

  }

  public void pageBreakCounter() {
    breakCounter += breakCounter;
  }

  public List<String> extraLiner(boolean isLandScape) {
    breakCounter = breakCounter - 1;
    int actualRows = 0;

    if (breakCounter > 1) {
      actualRows = 32;
      dummyList = getDummyList(actualRows, breakCounter);
    } else {
      if (getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) {
        actualRows = 20;
        dummyList = getDummyList(actualRows, breakCounter);
      }
//      if (companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
//        actualRows = 23;
//        dummyList = getDummyList(actualRows, breakCounter);
//      } else {
//        if (isLandScape) {
//          actualRows = 18;
//          dummyList = getDummyList(actualRows, breakCounter);
//        } else {
//          actualRows = 24;
//          dummyList = getDummyList(actualRows, breakCounter);
//        }
//      }
    }

    return dummyList;
  }

  private List<String> getDummyList(int actualRows, int breakCounter) {
    int totalRows = 0;
    if (breakCounter == 1) {
      totalRows = getDebitCreditNoteItemList().size() + debitCreditNote.getInvoiceGroup().size();
      int count = actualRows - totalRows;
      if (count >= 0) {
        dummyList = new ArrayList<>(Collections.nCopies(actualRows - totalRows, " "));
      } else {
        actualRows = 32;
        totalRows = (getDebitCreditNoteItemList().size() % 27) + debitCreditNote.getInvoiceGroup().size();
        dummyList = new ArrayList<>(Collections.nCopies((actualRows - totalRows) + 1, " "));
      }

    } else {
      totalRows = (getDebitCreditNoteItemList().size() % 27) + debitCreditNote.getInvoiceGroup().size();
      dummyList = new ArrayList<>(Collections.nCopies(actualRows - totalRows, " "));
    }
    return dummyList;
  }

  public String curencyWords(int amount) {
    return CurencyToText.india(amount);
  }

  public String getFooter() {
    if (companySettings != null) {
      return (companySettings.getPrintInvoiceFooter().replaceAll("#company#", companySettings.getCompanyId().getCompanyName()))
              .replaceAll("#district#", companyAddress.getDistrictId().getDistrictName());
    }
    return "";
  }

  public double sumOfDiscountValue(double scheme, double product) {
    return (scheme + product);
  }

  public int getFirstBreakRow() {
    return firstBreakRow;
  }

  public void setFirstBreakRow(int firstBreakRow) {
    this.firstBreakRow = firstBreakRow;
  }

  public int getLastBreakRow() {
    return lastBreakRow;
  }

  public void setLastBreakRow(int lastBreakRow) {
    this.lastBreakRow = lastBreakRow;
  }

  public DebitCreditNote getDebitCreditNote() {
    return debitCreditNote;
  }

  public void setDebitCreditNote(DebitCreditNote debitCreditNote) {
    this.debitCreditNote = debitCreditNote;
  }

  public List<DebitCreditNoteItem> getDebitCreditNoteItemList() {
    return debitCreditNoteItemList;
  }

  public void setDebitCreditNoteItemList(List<DebitCreditNoteItem> debitCreditNoteItemList) {
    this.debitCreditNoteItemList = debitCreditNoteItemList;
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

  public CustomerAddress getCustomerAddress() {
    return customerAddress;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Vendor getVendor() {
    return vendor;
  }

  public void setVendor(Vendor vendor) {
    this.vendor = vendor;
  }

  public VendorAddress getVendorAddress() {
    return vendorAddress;
  }

  public void setVendorAddress(VendorAddress vendorAddress) {
    this.vendorAddress = vendorAddress;
  }

  public void setCustomerAddress(CustomerAddress customerAddress) {
    this.customerAddress = customerAddress;
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

  public String getPrintType() {
    return printType;
  }

  public void setPrintType(String printType) {
    this.printType = printType;
  }

  public AccountingLedger getAccountingLedger() {
    return accountingLedger;
  }

  public void setAccountingLedger(AccountingLedger accountingLedger) {
    this.accountingLedger = accountingLedger;
  }

  public boolean isIntrastate() {
    return intrastate;
  }

  public void setIntrastate(boolean intrastate) {
    this.intrastate = intrastate;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getGst() {
    return gst;
  }

  public void setGst(String gst) {
    this.gst = gst;
  }

  public String getFssai() {
    return fssai;
  }

  public void setFssai(String fssai) {
    this.fssai = fssai;
  }

  public String getHeaderLabel() {
    return headerLabel;
  }

  public void setHeaderLabel(String headerLabel) {
    this.headerLabel = headerLabel;
  }

  public void printIText(MainView main) {
    Document document = null;
    try {
      FacesContext fc = FacesContext.getCurrentInstance();
      HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
      String fileName = getDebitCreditNote().getDocumentNo() + ".pdf";
      response.setContentType("application/pdf");
      response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

      PdfWriter pdfWriter;
      Rectangle pageSize = new Rectangle(595, 842);
      document = new Document(pageSize, 20, 20, 20, 20);
      pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
      pdfWriter.setPageEvent(new PageFooterEvent(pageSize.getWidth()));
      document.open();
      PrintDebitCreditNote printDebitCreditNote = new PrintDebitCreditNote();
      printDebitCreditNote.setCompanySettings(companySettings);
      printDebitCreditNote.setCompanyAddress(companyAddress);
      printDebitCreditNote.setCompanyLicenseList(companyLicenseList);
      printDebitCreditNote.setName(debitCreditNote.getAccountingLedgerId().getTitle());
      printDebitCreditNote.setAddress(address);
      printDebitCreditNote.setState(state);
      printDebitCreditNote.setGst(gst);
      printDebitCreditNote.setFssai(fssai);
      printDebitCreditNote.setIntrastate(intrastate);
      printDebitCreditNote.setDebitCreditNoteItemList(debitCreditNoteItemList);
      printDebitCreditNote.setParty(getDebitCreditNote().getDebitCreditParty());
      printDebitCreditNote.setInvoiceType(getDebitCreditNote().getInvoiceType());
      document.add(printDebitCreditNote.initiatePrint(debitCreditNote, document));
      printDebitCreditNote.createFooterBar(document, pdfWriter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      document.close();
    }
  }
}
