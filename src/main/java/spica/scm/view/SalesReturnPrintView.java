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
import spica.scm.common.InvoiceItem;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerLicense;
import spica.scm.domain.Manufacture;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.domain.SalesReturnSplit;
import spica.scm.print.PageFooterEvent;
import spica.scm.print.PrintSalesReturn;
import spica.scm.service.CompanyAddressService;
import spica.scm.service.CompanyLicenseService;
import spica.scm.service.ConsignmentDetailService;
import spica.scm.service.CustomerAddressService;
import spica.scm.service.CustomerLicenseService;
import spica.scm.service.ManufactureService;
import spica.scm.service.SalesReturnItemService;
import spica.scm.service.SalesReturnSplitService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.CurencyToText;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
@Named(value = "salesReturnPrintView")
@ViewScoped
public class SalesReturnPrintView implements Serializable {

  private SalesReturn salesReturn;
  private SalesReturnSplit salesReturnSplit;
  private SalesReturnItem salesReturnItem;
  private List<InvoiceItem> salesReturnItemList;
  private CompanySettings companySettings;
  private CompanyAddress companyAddress;
  private List<CustomerLicense> customerLicenseList;
  private List<CompanyLicense> companyLicenseList;
  private CustomerAddress customerAddress;
  private ConsignmentDetail consignmentDetail;
  private int incrementCount = 0;
  private transient TaxCalculator taxCalculator;
  private transient List<String> dummyList;
  private int breakCounter = 0;
  private int lastIncrementCount = 0;
  private List<Manufacture> manufactureList;
  private List<SalesReturnItem> itemList;

  @PostConstruct
  public void init() {
    salesReturn = (SalesReturn) Jsf.popupParentValue(SalesReturn.class);
    salesReturnSplit = (SalesReturnSplit) Jsf.popupParentValue(SalesReturnSplit.class);
  }

  public void salesReturnPrint(MainView main) {
    try {
      if (salesReturnSplit != null) {
        setSalesReturn(new SalesReturn(getSalesReturnSplit()));
        setSalesReturnItemList(SalesReturnSplitService.selectSalesReturnItemForPrint(main, getSalesReturnSplit()));
        setItemList(SalesReturnSplitService.getSalesReturnItemListBySplitItem(main, SalesReturnSplitService.selectSalesReturnItemSplitList(main, salesReturnSplit)));
        manufactureList = ManufactureService.selectManufactureBySalesReturnSplit(main, getSalesReturnSplit());
        setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesReturn().getTaxProcessorId().getProcessorClass()));
        getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getItemList(), false);
      } else {
        setSalesReturnItemList(SalesReturnItemService.selectSalesReturnItemForPrint(main, getSalesReturn()));
        manufactureList = ManufactureService.selectManufactureBySalesReturn(main, getSalesReturn());
      }
      companySettings = UserRuntimeView.instance().getCompany().getCompanySettings();
      companyAddress = CompanyAddressService.selectCompanyRegisteredAddress(main, getSalesReturn().getCompanyId());
      customerAddress = CustomerAddressService.selectCustomerRegisteredAddress(main, getSalesReturn().getCustomerId());
      customerLicenseList = CustomerLicenseService.listActiveLicenseBycustomer(main, getSalesReturn().getCustomerId(), SystemConstants.ACTIVE_LICENSE);
      companyLicenseList = CompanyLicenseService.listActiveLicenseByCompany(main, getSalesReturn().getCompanyId(), SystemConstants.ACTIVE_LICENSE);
      consignmentDetail = ConsignmentDetailService.selectConsignmentDetailByConsignment(main, getSalesReturn().getConsignmentId().getId());

    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void salesReturnSplitPrint(MainView main) {
    try {
      setSalesReturn(new SalesReturn(getSalesReturnSplit()));
      setSalesReturnItemList(SalesReturnSplitService.selectSalesReturnItemForPrint(main, getSalesReturnSplit()));
      companySettings = UserRuntimeView.instance().getCompany().getCompanySettings();
      companyAddress = CompanyAddressService.selectCompanyRegisteredAddress(main, getSalesReturn().getCompanyId());
      customerAddress = CustomerAddressService.selectCustomerRegisteredAddress(main, getSalesReturn().getCustomerId());
      customerLicenseList = CustomerLicenseService.listActiveLicenseBycustomer(main, getSalesReturn().getCustomerId(), SystemConstants.ACTIVE_LICENSE);
      companyLicenseList = CompanyLicenseService.listActiveLicenseByCompany(main, getSalesReturn().getCompanyId(), SystemConstants.ACTIVE_LICENSE);
      consignmentDetail = ConsignmentDetailService.selectConsignmentDetailByConsignment(main, getSalesReturn().getConsignmentId().getId());

    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
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

  public Double getDiscountValue(Double pd, Double cd) {
    if (pd != null && cd != null) {
      return pd + cd;
    } else if (pd != null) {
      return cd;
    } else {
      return pd;
    }
  }

  public double getCahDisc(Double cd) {
    if (cd != null && cd > 0) {
      return cd;
    } else {
      return 0.00;
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
      if (companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
        actualRows = 23;
        dummyList = getDummyList(actualRows, breakCounter);
      } else {
        if (isLandScape) {
          actualRows = 18;
          dummyList = getDummyList(actualRows, breakCounter);
        } else {
          actualRows = 24;
          dummyList = getDummyList(actualRows, breakCounter);
        }
      }
    }

    return dummyList;
  }

  private List<String> getDummyList(int actualRows, int breakCounter) {
    int totalRows = 0;
    if (breakCounter == 1) {
      totalRows = getSalesReturnItemList().size() + salesReturn.getInvoiceGroup().size();
      int count = actualRows - totalRows;
      if (count >= 0) {
        dummyList = new ArrayList<>(Collections.nCopies(actualRows - totalRows, " "));
      } else {
        actualRows = 32;
        totalRows = (getSalesReturnItemList().size() % 27) + salesReturn.getInvoiceGroup().size();
        dummyList = new ArrayList<>(Collections.nCopies((actualRows - totalRows) + 1, " "));
      }

    } else {
      totalRows = (getSalesReturnItemList().size() % 27) + salesReturn.getInvoiceGroup().size();
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

  public SalesReturn getSalesReturn() {
    return salesReturn;
  }

  public void setSalesReturn(SalesReturn salesReturn) {
    this.salesReturn = salesReturn;
  }

  public SalesReturnItem getSalesReturnItem() {
    return salesReturnItem;
  }

  public void setSalesReturnItem(SalesReturnItem salesReturnItem) {
    this.salesReturnItem = salesReturnItem;
  }

  public List<InvoiceItem> getSalesReturnItemList() {
    return salesReturnItemList;
  }

  public void setSalesReturnItemList(List<InvoiceItem> salesReturnItemList) {
    this.salesReturnItemList = salesReturnItemList;
  }

  public CompanySettings getCompanySettings() {
    return companySettings;
  }

  public CompanyAddress getCompanyAddress() {
    return companyAddress;
  }

  public List<CustomerLicense> getCustomerLicenseList() {
    return customerLicenseList;
  }

  public List<CompanyLicense> getCompanyLicenseList() {
    return companyLicenseList;
  }

  public CustomerAddress getCustomerAddress() {
    return customerAddress;
  }

  public ConsignmentDetail getConsignmentDetail() {
    return consignmentDetail;
  }

  public List<Manufacture> getManufactureList() {
    return manufactureList;
  }

  public void printIText(MainView main) {
    Document document = null;
    try {
      FacesContext fc = FacesContext.getCurrentInstance();
      HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
      String fileName = getSalesReturn().getInvoiceNo() + ".pdf";
      response.setContentType("application/pdf");
      response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

      PdfWriter pdfWriter;
      float width = 0;
      Rectangle pageSize = null;
      if (salesReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) {
        pageSize = new Rectangle(595, 842);
      } else {
        pageSize = new Rectangle(842, 595);
      }
      document = new Document(pageSize, 20, 20, 20, 20);

      pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
      pdfWriter.setPageEvent(new PageFooterEvent(pageSize.getWidth()));
      document.open();

      PrintSalesReturn printSalesReturn = new PrintSalesReturn();
      printSalesReturn.setCompanyAddress(companyAddress);
      printSalesReturn.setCustomerAddress(customerAddress);
      printSalesReturn.setCompanyLicenseList(companyLicenseList);
      printSalesReturn.setCustomerLicenseList(customerLicenseList);
      printSalesReturn.setCompanySettings(companySettings);
      printSalesReturn.setConsignmentDetail(consignmentDetail);
      printSalesReturn.setInvoiceItemList(salesReturnItemList);
      printSalesReturn.setManufactureList(manufactureList);

      document.add(printSalesReturn.initiatePrint(salesReturn, document));
      printSalesReturn.createFooterBar(document, pdfWriter);

    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      document.close();
    }

  }

  public SalesReturnSplit getSalesReturnSplit() {
    return salesReturnSplit;
  }

  public void setSalesReturnSplit(SalesReturnSplit salesReturnSplit) {
    this.salesReturnSplit = salesReturnSplit;
  }

  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public List<SalesReturnItem> getItemList() {
    return itemList;
  }

  public void setItemList(List<SalesReturnItem> itemList) {
    this.itemList = itemList;
  }

}
