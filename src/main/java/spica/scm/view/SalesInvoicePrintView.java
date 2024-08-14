/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import spica.scm.common.InvoiceItem;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyBank;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.CustomerLicense;
import spica.scm.domain.Manufacture;
import spica.scm.domain.SalesInvoice;
import spica.scm.export.ExcelUtil;
import spica.scm.print.CellProperty;
import spica.scm.print.ItemTable;
import spica.scm.print.PageFooterEvent;
import spica.scm.print.PdfUtil;
import spica.scm.print.PrintSalesInvoice;
import spica.scm.print.fonts.PageHeaderEvent;
import spica.scm.service.CompanyAddressService;
import spica.scm.service.CompanyBankService;
import spica.scm.service.CompanyLicenseService;
import spica.scm.service.CompanySettingsService;
import spica.scm.service.ConsignmentDetailService;
import spica.scm.service.CustomerAddressService;
import spica.scm.service.CustomerLicenseService;
import spica.scm.service.ManufactureService;
import spica.scm.service.SalesInvoiceItemService;
import spica.scm.service.SalesInvoiceService;
import spica.scm.service.TransporterService;
import spica.scm.util.CurencyToText;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.app.config.AppConfig;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.DateUtil;

/**
 *
 * @author fify
 */
@Named(value = "invoicePrintView")
@ViewScoped
public class SalesInvoicePrintView implements Serializable {

  private transient SalesInvoice salesInvoice;
  //private float columnWidth[];

  /**
   *
   */
  @PostConstruct
  public void init() {
    salesInvoice = (SalesInvoice) Jsf.popupParentValue(SalesInvoice.class);
  }

  public void printItext(MainView main) {
    try {
      generatePrint(main, getSalesInvoice(), null);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   * Creates a new instance of InvoicePrintView
   */
  public SalesInvoicePrintView() {
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

  public static void generatePrint(Main main, SalesInvoice salesInvoice, String filePath) throws Exception {
    PrintSalesInvoice printSalesInvoice = loadBasicSalesData(main, salesInvoice);
    CompanySettings companySettings = printSalesInvoice.getSalesInvoice().getCompanyId().getCompanySettings();
    String fileName = salesInvoice.getInvoiceNo() + ".pdf";
    Document document;
    if (printSalesInvoice.getSalesInvoice().getPrintMode().equals(SystemConstants.PRINT_PORTRAIT)) {
      document = ExcelUtil.getPortrait();
    } else {
      document = ExcelUtil.getLandscape();
    }
    String date = "";
    if (salesInvoice.getInvoiceDate() != null) {
      date = AppConfig.displayDatePattern.format(salesInvoice.getInvoiceDate());
    }
    PdfWriter pdfWriter = ExcelUtil.getPdfWriter(document, fileName, filePath); //if file path then out to file else as stream
    printSalesInvoice.initiatePrint();
    pdfWriter.setPageEvent(new PageFooterEvent(document.getPageSize().getWidth()));
    if (!companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
      pdfWriter.setPageEvent(new PageHeaderEvent(salesInvoice.getCompanyId().getCompanyName(), salesInvoice.getInvoiceNo(), salesInvoice.getCustomerId().getCustomerName(),
              date));
    }
     document.open();
    ItemTable table = generateItemTable(salesInvoice);
    printSalesInvoice.dataTable(salesInvoice.getPrintMode(), document, pdfWriter, table);
    printSalesInvoice.createFooterBar(document, pdfWriter, salesInvoice.getPrintType(), table.getColumnWidth(), salesInvoice.getCompanyId().getCompanySettings());
    ExcelUtil.responseComplete(document, filePath);

  }

//  private static void generatePrintSample(PrintSample printSample, String freeType, SalesInvoice salesInvoice) throws Exception {
//    String fileName = salesInvoice.getInvoiceNo() + ".pdf";
//    Rectangle pageSize = pageSize = new Rectangle(595, 842);
//    Document document = new Document(pageSize, 20, 20, 20, 20);
//    PdfWriter pdfWriter = PdfWriter.getInstance(document, ExcelUtil.outResponse(fileName));
//    pdfWriter.setPageEvent(new PageFooterEvent(pageSize.getWidth()));
//    document.open();
//    ItemTable table = generateSampleItemTable(salesInvoice, freeType);
//    document.add(printSample.dataTable(salesInvoice.getPrintMode(), document, pdfWriter, table));
//    printSample.createFooterBar(document, pdfWriter, table.getColumnWidth());
//    ExcelUtil.responseComplete(document);
//  }
  private static ItemTable generateItemTable(SalesInvoice salesInvoice) {
    String freeType = salesInvoice.getPrintType();
    CompanySettings companySettings = salesInvoice.getCompanyId().getCompanySettings();
    List<InvoiceItem> invoiceItemList = salesInvoice.getInvoiceItemList();
    ItemTable table = new ItemTable();
    List<CellProperty> properties = new LinkedList<>();
    List<CellProperty> dataList = new LinkedList<>();
    table.setGstFlag(true);
    table.setPortrait(salesInvoice.getPrintMode().equals(SystemConstants.PRINT_PORTRAIT));
    properties.add(new CellProperty("Sl#", 3f, false, true, true));
    if (companySettings.getShowManufacturer() == 1) {
      properties.add(new CellProperty("Mfg. Co.", 3.5f, true, true, true));
    }
    properties.add(new CellProperty("HSN Code", 7f, true, true, true));
    properties.add(new CellProperty("Product Name", 18f, false, true, true));
    properties.add(new CellProperty("Pack Desc", 6f, false, true, true));
    properties.add(new CellProperty("Batch No.", 9.5f, false, true, true));
    properties.add(new CellProperty("Exp. Date", 5.5f, true, true, true));
    properties.add(new CellProperty("MRP (Rs.)", 6f, true, true, true));
    properties.add(new CellProperty("Qty", 5.5f, false, true, true));
    if (freeType.equals(SystemConstants.TYPE_I)) {
      properties.add(new CellProperty("Free", 4f, false, true, false));
    }
    properties.add(new CellProperty("Rate (Rs.)", 6f, true, true, true));
    if (freeType.equals(SystemConstants.TYPE_II)) {
      properties.add(new CellProperty("S.Dis (Rs.)", 7f, true, true, true));
    }
    properties.add(new CellProperty("P.Dis %", 5f, true, true, false));
    properties.add(new CellProperty("P.Dis (Rs.)", 6f, true, true, false));
    properties.add(new CellProperty("Dis. %", 5f, true, false, true));
    properties.add(new CellProperty("Dis. (Rs.)", 7f, true, false, true));
    properties.add(new CellProperty("PTS (Rs.)", 5f, true, false, true));
    properties.add(new CellProperty("PTR (Rs.)", 6f, true, true, true));
    properties.add(new CellProperty("Taxable Amount", 8f, true, false, true));
    properties.add(new CellProperty("GST %", 4f, true, true, true));
    properties.add(new CellProperty("GST Amount", 7f, true, false, true));
    properties.add(new CellProperty("Total (Rs.)", 8f, true, false, true));
    properties.add(new CellProperty("Goods Value", 8f, true, true, false));
    table.setCellProperties(properties);
    int i = 0;
    PdfUtil pdfUtil = new PdfUtil();
    for (InvoiceItem item : invoiceItemList) {
      i += 1;
      dataList.add(new CellProperty(String.valueOf(i), SystemConstants.LEFT));
      if (companySettings.getShowManufacturer() == 1) {
        dataList.add(new CellProperty(pdfUtil.limitStringSize(String.valueOf(item.getMfgCode()), 3, false), SystemConstants.LEFT));
      }
      dataList.add(new CellProperty(String.valueOf(item.getHsnCode()), SystemConstants.LEFT));
      dataList.add(new CellProperty(String.valueOf(item.getProductName()), SystemConstants.LEFT));

      if (item.getPackDescription() != null) {
        dataList.add(new CellProperty(item.getPackDescription(), SystemConstants.LEFT));
      } else {
        dataList.add(new CellProperty(String.valueOf(decimalCheck(item.getPackDescription())), SystemConstants.LEFT));
      }

      dataList.add(new CellProperty(String.valueOf(item.getBatchNo()), SystemConstants.LEFT));
      dataList.add(new CellProperty(String.valueOf(item.getExpiryDate()), SystemConstants.LEFT));
      dataList.add(new CellProperty(String.valueOf(decimal(item.getValueMrp())), SystemConstants.RIGHT));
      int qty = item.getProductQty().intValue();
      if (freeType.equals(SystemConstants.TYPE_II) || freeType.isEmpty()) {
        if (item.getProductQtyFree() != null && item.getProductQtyFree() > 0) {
          qty += item.getProductQtyFree();
        }
      }
      dataList.add(new CellProperty(String.valueOf(qty), SystemConstants.RIGHT));
      if (freeType.equals(SystemConstants.TYPE_I)) {
        dataList.add(new CellProperty((item.getProductQtyFree() != null ? String.valueOf(item.getProductQtyFree().intValue()) : ""), SystemConstants.RIGHT));
      }
      dataList.add(new CellProperty(String.valueOf(item.getValueRate()), SystemConstants.RIGHT));
      if (freeType.equals(SystemConstants.TYPE_II)) {
        dataList.add(new CellProperty(String.valueOf((item.getSchemeDiscountValue() != null ? decimal(item.getSchemeDiscountValue()) : "")), SystemConstants.RIGHT));
      }
      dataList.add(new CellProperty((item.getProductDiscountPercentage() != null ? String.valueOf(item.getProductDiscountPercentage()) : ""), SystemConstants.RIGHT));
      dataList.add(new CellProperty((item.getProductDiscountValue() != null ? decimal(item.getProductDiscountValue()) : ""), SystemConstants.RIGHT));
      String discPercentage = "";
      if (item.getSchemeDiscountPercentage() != null && item.getProductDiscountPercentage() != null) {
        discPercentage = decimal(item.getSchemeDiscountPercentage()) + "\n+" + decimal(item.getProductDiscountPercentage());
      } else {
        if (item.getSchemeDiscountPercentage() != null) {
          discPercentage = decimal(item.getSchemeDiscountPercentage());
        }
        if (item.getProductDiscountPercentage() != null) {
          discPercentage = decimal(item.getProductDiscountPercentage());
        }
      }
      dataList.add(new CellProperty(discPercentage, SystemConstants.RIGHT));
      String discValue = "";
      discValue = (item.getSchemeDiscountValue() == null ? "" : decimal(item.getSchemeDiscountValue()))
              + (item.getSchemeDiscountValue() != null && item.getProductDiscountValue() != null ? "\n+" : "")
              + (item.getProductDiscountValue() == null ? "" : decimal(item.getProductDiscountValue()))
              + ((item.getProductDiscountValue() != null && item.getInvoiceDiscountValue() != null) || (item.getSchemeDiscountValue() != null && item.getInvoiceDiscountValue() != null) ? "\n+" : "")
              + (item.getInvoiceDiscountValue() == null ? "" : decimal(item.getInvoiceDiscountValue()))
              + ((item.getInvoiceDiscountValue() != null && item.getCashDiscountValue() != null) || (item.getProductDiscountValue() != null && item.getCashDiscountValue() != null) || (item.getSchemeDiscountValue() != null && item.getCashDiscountValue() != null) ? "\n+" : "")
              + (item.getCashDiscountValue() == null ? "" : decimal(item.getCashDiscountValue()));
      dataList.add(new CellProperty(discValue, SystemConstants.RIGHT));
      Double totalDisc = ((item.getInvoiceDiscountValue() == null ? 0 : item.getInvoiceDiscountValue())
              + (item.getCashDiscountValue() == null ? 0 : item.getCashDiscountValue()));
      dataList.add(new CellProperty(String.valueOf(decimal(item.getValuePts())), SystemConstants.RIGHT));
      dataList.add(new CellProperty(String.valueOf(decimal(item.getValuePtr())), SystemConstants.RIGHT));
      dataList.add(new CellProperty((item.getCashDiscountValue() == null ? decimal(item.getValueSale()) : decimal(item.getValueSale() - totalDisc)), SystemConstants.RIGHT));
      dataList.add(new CellProperty(String.valueOf(decimal(item.getCgstTaxRate() + item.getSgstTaxRate())), SystemConstants.RIGHT));
      dataList.add(new CellProperty(String.valueOf(decimal(item.getValueIgst())), SystemConstants.RIGHT));
      dataList.add(new CellProperty(String.valueOf(item.getCashDiscountValue() == null ? decimal(item.getValueTaxable() + item.getValueIgst()) : decimal(item.getValueTaxable() + item.getValueIgst() - totalDisc)), SystemConstants.RIGHT));
      dataList.add(new CellProperty(String.valueOf(decimal(item.getValueSale())), SystemConstants.RIGHT));
    }
    table.setData(dataList);
    return table;

  }

  private static String decimal(Number text) {
    if (text != null) {
      return AppConfig.decimalFormat.format(text);
    }
    return "";
  }

//  private static ItemTable generateSampleItemTable(SalesInvoice salesInvoice, String freeType) {
//    //CompanySettings companySettings = salesInvoice.getCompanyId().getCompanySettings();
//    List<InvoiceItem> invoiceItemList = salesInvoice.getInvoiceItemList();
//    ItemTable table = new ItemTable();
//    List<CellProperty> properties = new LinkedList<>();
//    List<CellProperty> dataList = new LinkedList<>();
//    table.setGstFlag(true);
//    table.setPortrait(salesInvoice.getPrintMode().equals(SystemConstants.PRINT_PORTRAIT));
//    properties.add(new CellProperty("Sl#", 3f, false, true, true));
//    properties.add(new CellProperty("Product Name/ Remark", 18f, false, true, true));
//    properties.add(new CellProperty("HSN Code/ SAC", 7.5f, false, true, true));
//    properties.add(new CellProperty("Packing", 6.5f, false, true, true));
//    properties.add(new CellProperty("Batch No.", 9f, false, true, true));
//    properties.add(new CellProperty("Mfg. Date", 5.5f, false, true, true));
//    properties.add(new CellProperty("Exp. Date", 5.5f, false, true, true));
//    properties.add(new CellProperty("Qty", 4f, false, true, true));
//    table.setCellProperties(properties);
//    int i = 0;
//    for (InvoiceItem item : invoiceItemList) {
//      i += 1;
//      dataList.add(new CellProperty(String.valueOf(i), SystemConstants.LEFT));
//      dataList.add(new CellProperty(String.valueOf(item.getProductName()), SystemConstants.LEFT));
//      dataList.add(new CellProperty(String.valueOf(item.getHsnCode()), SystemConstants.LEFT));
//      if (item.getPackDescription() == null) {
//        dataList.add(new CellProperty(String.valueOf(decimalCheck(item.getPackSize())), SystemConstants.LEFT));
//      } else {
//        dataList.add(new CellProperty(item.getPackDescription(), SystemConstants.LEFT));
//      }
//      dataList.add(new CellProperty(String.valueOf(item.getBatchNo()), SystemConstants.RIGHT));
//      dataList.add(new CellProperty(String.valueOf(""), SystemConstants.RIGHT));
//      dataList.add(new CellProperty(String.valueOf(item.getExpiryDate()), SystemConstants.LEFT));
//      dataList.add(new CellProperty(String.valueOf(item.getProductQty() + (item.getProductQtyFree() != null ? item.getProductQtyFree() : 0)), SystemConstants.RIGHT));
//    }
//    table.setData(dataList);
//    //columnWidth = table.getColumnWidth();
//    return table;
//  }
  private static String decimalCheck(String s) {
    String arr[] = s.split("\\.");
    if (arr[1].charAt(0) == '0') {
      s = arr[0] + arr[1].replaceAll("[^A-Za-z]+", "");
      return s;
    } else {
      return s;
    }
  }

  public SalesInvoice getSalesInvoice() {
    return salesInvoice;
  }

  public void setSalesInvoice(SalesInvoice salesInvoice) {
    this.salesInvoice = salesInvoice;
  }

  public String curencyWords(int amount) {
    return CurencyToText.india(amount);
  }

  public Date getDueDate() {
    return DateUtil.moveDays(salesInvoice.getInvoiceDate(), salesInvoice.getSalesCreditDays());
  }

  // for sms purpose, stored pdf file while confirm status
  private static PrintSalesInvoice loadBasicSalesData(Main main, SalesInvoice salesInvoice) throws Exception {

    //TODO godson sujesh do we need to pass like these see how its done in creditnote debit note print
    PrintSalesInvoice printSalesInvoice = null;
    if (salesInvoice.getCompanyId().getCompanySettings() == null) {
      salesInvoice.getCompanyId().setCompanySettings(CompanySettingsService.selectByCompany(main.em(), salesInvoice.getCompanyId()));
    }
    salesInvoice.setCustomerAddressId(CustomerAddressService.selectByPk(main, salesInvoice.getCustomerAddressId()));
    salesInvoice.setInvoiceItemList(SalesInvoiceItemService.selectSalesInvoiceItemForPrint(main, salesInvoice));
    List<Manufacture> manufactureList = ManufactureService.selectManufactureBySalesInvoice(main, salesInvoice);
    SalesInvoiceService.updateSalesInvoicePrinted(main, salesInvoice);
//    TaxCalculator taxCalculator = SystemRuntimeConfig.getTaxCalculator(salesInvoice.getTaxProcessorId().getProcessorClass());
//      List<SalesInvoiceItem> salesInvoiceItemList = SalesInvoiceItemService.selectSalesInvoiceItemBySalesInvoice(main, salesInvoice, taxCalculator);
    List<InvoiceItem> invoiceItemList = salesInvoice.getInvoiceItemList();
    List<CustomerLicense> customerLicenseList = CustomerLicenseService.listActiveLicenseBycustomer(main, salesInvoice.getCustomerId(), SystemConstants.ACTIVE_LICENSE);
    List<CompanyLicense> companyLicenseList = CompanyLicenseService.listActiveLicenseByCompany(main, salesInvoice.getCompanyId(), SystemConstants.ACTIVE_LICENSE);
    //  CompanySettings companySettings = salesInvoice.getCompanyId().getCompanySettings();
    CompanyAddress companyAddress = CompanyAddressService.selectCompanyRegisteredAddress(main, salesInvoice.getCompanyId());
    CompanyBank companyBank = CompanyBankService.bankByCompanyId(main, salesInvoice.getCompanyId());
    ConsignmentDetail consignmentDetail = ConsignmentDetailService.selectConsignMentDetailBySalesInvoice(main, salesInvoice.getId());
    consignmentDetail = consignmentDetail == null ? new ConsignmentDetail() : consignmentDetail;
    if (consignmentDetail.getTransporterId() == null) {
      consignmentDetail.setTransporterId(TransporterService.selectTransporterByCustomer(main, salesInvoice.getCustomerId()));
    }

    if (salesInvoice.getPrintType().equals(SystemConstants.SAMPLE)) {
//      PrintSample printSample = new PrintSample(salesInvoice);
//      printSample.setManufactureList(manufactureList);
//      printSample.setInvoiceItemList(invoiceItemList);
//      printSample.setCustomerLicenseList(customerLicenseList);
//      printSample.setCompanyLicenseList(companyLicenseList);
//      printSample.setCompanySettings(salesInvoice.getCompanyId().getCompanySettings());
//      printSample.setCompanyAddress(companyAddress);
//      printSample.setConsignmentDetail(consignmentDetail);
//      printSample.setCustomerAddress(salesInvoice.getCustomerAddressId());
//      printSample.setCustomer(salesInvoice.getCustomerId());
//      printSample.setCompany(salesInvoice.getCompanyId());
      // generatePrintSample(printSample, salesInvoice.getPrintType(), companySettings);
    } else {
      printSalesInvoice = new PrintSalesInvoice(salesInvoice);
      printSalesInvoice.setManufactureList(manufactureList);
      printSalesInvoice.setInvoiceItemList(invoiceItemList);
      printSalesInvoice.setCustomerLicenseList(customerLicenseList);
      printSalesInvoice.setCompanyLicenseList(companyLicenseList);
      printSalesInvoice.setCompanySettings(salesInvoice.getCompanyId().getCompanySettings());  //TODO can be removed and used from salesinvoice 
      printSalesInvoice.setCompanyAddress(companyAddress);
      printSalesInvoice.setConsignmentDetail(consignmentDetail);
      printSalesInvoice.setCustomerAddress(salesInvoice.getCustomerAddressId());
      printSalesInvoice.setCustomer(salesInvoice.getCustomerId()); //TODO can be removed and used from salesinvoice 
      printSalesInvoice.setCompany(salesInvoice.getCompanyId());  //TODO can be removed and used from salesinvoice 
      printSalesInvoice.setCompanyBank(companyBank);

    }
    return printSalesInvoice;
  }

}
