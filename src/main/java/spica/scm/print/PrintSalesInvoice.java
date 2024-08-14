/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.print;

import com.itextpdf.text.BaseColor;
import spica.scm.print.Impl.PageHeaderImpl;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import spica.scm.common.InvoiceGroup;
import spica.scm.common.InvoiceItem;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyBank;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerLicense;
import spica.scm.domain.Manufacture;
import spica.scm.domain.SalesInvoice;
import spica.scm.print.Impl.PageFooterImpl;
import spica.scm.print.Impl.PageProductImpl;
import spica.scm.util.MathUtil;
import spica.sys.SystemConstants;
import wawo.app.faces.AppView;

/**
 *
 * @author sujesh
 */
public class PrintSalesInvoice extends PdfUtil {

  private SalesInvoice salesInvoice;

  private transient List<InvoiceItem> invoiceItemList;
  private transient List<Manufacture> manufactureList;
  private transient List<CustomerLicense> customerLicenseList;
  private transient List<CompanyLicense> companyLicenseList;
  private transient List<InvoiceGroup> invoiceGroup;
  private transient CompanySettings companySettings;
  private transient boolean intraStateSales;
  private transient CompanyAddress companyAddress;
  private transient CustomerAddress customerAddress;
  private transient ConsignmentDetail consignmentDetail;
  private transient Customer customer;
  private transient Company company;
  private transient List<String> dummyList;
  private transient boolean firstRow;
  private transient boolean iterator;
  private transient boolean billTop;
  private transient PdfPTable dataTable;
  private transient PdfPTable productTable;
  private transient float headerHeight;
  private transient float pHeight;
  private transient float tableHeight;
  private transient float fHeight;
  private transient float sumTableHeight;
  private transient CompanyBank companyBank;
  private CompanyLicense companyLicense;
  PdfWriter pdfWriter;
  Document doc;

  public PrintSalesInvoice() {
  }

  public PrintSalesInvoice(SalesInvoice salesInvoice) throws DocumentException, IOException {
    this.salesInvoice = salesInvoice;
  }

  public void initiatePrint() throws DocumentException, IOException {
    invoiceGroup = getSalesInvoice().getInvoiceGroup();
  }

  public PdfPTable dataTable(String printType, Document document, PdfWriter writer, ItemTable itemTable) throws Exception {
    initiatePrint();
    PageProduct pageProduct = new PageProductImpl();
    dataTable = new PdfPTable(1);
    if (printType.equals(SystemConstants.PRINT_PORTRAIT)) {
      dataTable.setTotalWidth(pageWidth);
      dataTable.setLockedWidth(true);
    } else {
      pageWidth = landScapeWidth;
      dataTable.setTotalWidth(pageWidth);
      dataTable.setLockedWidth(true);
    }
    PdfPTable header = new PdfPTable(getHeaderTable());
    PdfPCell headerCell = new PdfPCell(header);
    productTable = pageProduct.setProductItems(itemTable, writer, pageWidth);
    PdfPCell productCell = new PdfPCell(productTable);
    headerCell.setBorder(0);
    headerCell.setPadding(0);
    productCell.setPadding(0);
    productCell.setBorder(0);
    dataTable.addCell(headerCell);
    if (companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
      dataTable.setHeaderRows(1);
    }
    dataTable.addCell(productCell);
    dataTable.setSplitLate(false);
    dataTable.getTotalWidth();
    document.add(dataTable);
    createFooterTable();
    document.add(pageProduct.pageFillRows(writer, document, itemTable.getColumnWidth(), fHeight, printType));
    if (companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
      header.writeSelectedRows(0, -1, document.leftMargin(), document.top(), writer.getDirectContent());
    }
    return dataTable;
  }

  public float[] getCellWidth(Float keys[]) {
    float[] array = new float[keys.length];
    int index = 0;
    for (Float element : keys) {
      array[index++] = element.floatValue();
    }
    return array;
  }

  public PdfPTable getHeaderTable() {
    PdfPTable table = new PdfPTable(1);
    table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
    try {
      Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
      setFirstRow(true);
      setIterator(true);
      table.setTotalWidth(pageWidth);
      table.setLockedWidth(true);
      do {
        tableMap = new LinkedHashMap<>();
        tableMap = createHeaderTables(isFirstRow(), table);
        PdfPTable headerTable = new PdfPTable(getCellWidth(tableMap.keySet().toArray(new Float[0])));
        headerTable.getDefaultCell().setBorder(0);
        for (Map.Entry<Float, PdfPTable> entry : tableMap.entrySet()) {
          headerTable.addCell(entry.getValue());
        }
        table.addCell(headerTable);
      } while (isIterator());
    } catch (Exception e) {
      Logger.getLogger(PrintSalesInvoice.class.getName()).log(Level.SEVERE, null, e);
    }
    headerHeight = table.getTotalHeight();
    return table;
  }

  public Map<Float, PdfPTable> createHeaderTables(boolean firstRow, PdfPTable table) throws Exception {
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    PageHeader pageHeader = new PageHeaderImpl();
    if (salesInvoice.getPrintMode().equals(SystemConstants.PRINT_PORTRAIT) || companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
      if (isFirstRow()) {
        if (salesInvoice.getPrintMode().equals(SystemConstants.PRINT_PORTRAIT) && companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_TOP)) {
          PdfPCell cell = new PdfPCell(pageHeader.setGStInvoiceLabel((getSalesInvoice().getProformaStatus() != null && getSalesInvoice().getProformaStatus().intValue() == SystemConstants.SALES_INVOICE_TYPE_PROFORMA
                  && getSalesInvoice().getSalesInvoiceStatusId().getId().intValue() == SystemConstants.DRAFT) ? proformaLabel : invoiceLabel));
          cell.setBorderWidthBottom(0);
          cell.setBorderWidthTop(0);
          cell.setBorderColorLeft(BaseColor.WHITE);
          cell.setBorderColorRight(BaseColor.WHITE);
          cell.setBackgroundColor(BaseColor.WHITE);
          cell.setPaddingBottom(4);
          table.addCell(cell);
        }
        tableMap.put(40f, pageHeader.setCompanyAddress(companyAddress, company, companySettings.getFileContent()));
        tableMap.put(32f, pageHeader.setCompanyGST(company, companyLicenseList));
        tableMap.put(28f, pageHeader.setBillInvoice(salesInvoice.getInvoiceNo(), salesInvoice.getInvoiceDate(), salesInvoice.getSalesCreditDays(), new float[]{50, 50}, false));
        setFirstRow(false);
        setIterator(true);
      } else {
        if ((salesInvoice.getPrintMode().equals(SystemConstants.PRINT_PORTRAIT) && companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_MIDDLE))
                || salesInvoice.getPrintMode().equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
          PdfPCell cell = new PdfPCell(pageHeader.setGStInvoiceLabel((getSalesInvoice().getProformaStatus().intValue() == SystemConstants.SALES_INVOICE_TYPE_PROFORMA
                  && getSalesInvoice().getSalesInvoiceStatusId().getId().intValue() == SystemConstants.DRAFT) ? proformaLabel : invoiceLabel));
          cell.setBackgroundColor(BaseColor.WHITE);
          table.addCell(cell);
        }
        tableMap.put(30f, pageHeader.setCustomerAddress(customer, customerAddress, true));
        tableMap.put(25f, pageHeader.setCustomerGST(customer, customerLicenseList, SystemConstants.PRINT_MULTIPLE_LINE));
        tableMap.put(25.5f, pageHeader.setShipingAddress(getSalesInvoice().getShippingAddressId()));
        tableMap.put(19.5f, pageHeader.setCarrierDetails(consignmentDetail, SystemConstants.PRINT_MULTIPLE_LINE, false, null, null, null));
        setIterator(false);
      }
    } else {
      if (companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
        PdfPCell cell = new PdfPCell(pageHeader.setGStInvoiceLabel((getSalesInvoice().getProformaStatus().intValue() == SystemConstants.SALES_INVOICE_TYPE_PROFORMA
                && getSalesInvoice().getSalesInvoiceStatusId().getId().intValue() == SystemConstants.DRAFT) ? proformaLabel : invoiceLabel));
        cell.setBorder(0);
        table.addCell(cell);
        PdfPTable single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.addCell(pageHeader.setCompanyAddress(companyAddress, company, companySettings.getFileContent()));
        cell = new PdfPCell(pageHeader.setCompanyGST(company, companyLicenseList));
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setPaddingLeft(2);
        cell.setBorder(0);
        single.addCell(cell);
        tableMap.put(27.5f, single);
        single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.addCell(pageHeader.setCustomerAddress(customer, customerAddress, true));

        cell = new PdfPCell(pageHeader.setCustomerGST(customer, customerLicenseList, SystemConstants.PRINT_SINGLE_LINE));
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setPaddingLeft(2);
        cell.setBorder(0);
        single.addCell(cell);
        tableMap.put(25f, single);
        single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.addCell(pageHeader.setShipingAddress(getSalesInvoice().getShippingAddressId()));
        tableMap.put(25.5f, single);
        single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.addCell(pageHeader.setBillInvoice(salesInvoice.getInvoiceNo(), salesInvoice.getInvoiceDate(), salesInvoice.getSalesCreditDays(), new float[]{50, 50}, false));
        cell = new PdfPCell(pageHeader.setCarrierDetails(consignmentDetail, SystemConstants.PRINT_SINGLE_LINE, false, null, null, null));
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setPaddingLeft(3);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setBorder(0);
        single.addCell(cell);

        tableMap.put(20f, single);
        setIterator(false);
      }

    }
    return tableMap;
  }

  public void createFooterBar(Document document, PdfWriter writer, String freeType, float[] column, CompanySettings companySettings) {
    try {
      PageProduct pageProduct = new PageProductImpl();
      pHeight = productTable.getTotalHeight();
      if (!document.isOpen()) {
        document.open();
      }
      PdfPTable footer = new PdfPTable(createFooterTable());
//      sumTableHeight = pHeight + headerHeight + fHeight;
      sumTableHeight = tableHeight + fHeight;
//      Add Extra Lines
      if (companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
        pageProduct.emptyRows(column, dataTable.getTotalWidth(), document.getPageSize().getHeight(), headerHeight + 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
                document.top() - headerHeight, writer.getDirectContent());
      } else {
        pageProduct.emptyRows(column, dataTable.getTotalWidth(), document.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
                document.top(), writer.getDirectContent());
      }

//      Add Footer 
      if (salesInvoice.getPrintMode().equals(SystemConstants.PRINT_PORTRAIT)) {
        footer.writeSelectedRows(0, -1, document.leftMargin(), footer.getTotalHeight() + document.bottomMargin(), writer.getDirectContent());
      } else {
        footer.writeSelectedRows(0, -1, document.leftMargin(), footer.getTotalHeight() + document.bottomMargin(), writer.getDirectContent());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public PdfPTable createFooterTable() {
    PdfPTable table = new PdfPTable(1);
    try {
      Double sDisc = 0.0;
      Double pDisc = 0.0;
      for (InvoiceItem item : salesInvoice.getInvoiceItemList()) {
        sDisc += item.getSchemeDiscountValue() != null ? item.getSchemeDiscountValue() : 0;
        pDisc += item.getProductDiscountValue() != null ? item.getProductDiscountValue() : 0;
      }
      sDisc = MathUtil.roundOff(sDisc, 2);
      pDisc = MathUtil.roundOff(pDisc, 2);
      PageFooter pageFooter = new PageFooterImpl();
      table.setTotalWidth(pageWidth);
      table.setLockedWidth(true);
      table.getDefaultCell().setBorder(0);
      String totalQty = salesInvoice.getProductNetQuantity() != null ? AppView.formatDecimal(salesInvoice.getProductNetQuantity(), 2) : "0";
      PdfPCell cell = new PdfPCell(pageFooter.setItemQty(salesInvoice.getInvoiceItemList().size(), totalQty,
              salesInvoice.getNoOfBoxes(), salesInvoice.getWeight(), sDisc, pDisc, null));
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell);
      cell = new PdfPCell(createTaxationTable());
      cell.setBackgroundColor(BaseColor.WHITE);
      cell.setPadding(0);
      table.addCell(cell).setBorder(0);
      cell = new PdfPCell(pageFooter.setDeclaration(companySettings, salesInvoice.getCompanyId().getCompanyName(), salesInvoice.getCompanyAddressId().getDistrictId().getDistrictName()));
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell).setBorder(0);
      cell = new PdfPCell(pageFooter.setWebMail(companyAddress.getEmail(), companyAddress.getWebsite()));
      cell.setPadding(0);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell).setBorder(0);

    } catch (Exception ex) {
      Logger.getLogger(PrintSalesInvoice.class
              .getName()).log(Level.SEVERE, null, ex);
    }
    fHeight = table.getTotalHeight();

    return table;
  }

  public PdfPTable createTaxationTable() throws Exception {
    PageFooter pageFooter = new PageFooterImpl();
    PdfPTable table = new PdfPTable(new float[]{70, 30});
    table.setTotalWidth(pageWidth);
    table.setLockedWidth(true);
    PdfPTable taxTable = new PdfPTable(1);
    PdfPCell cell = new PdfPCell();
    if (salesInvoice.getIsSalesInterstate() == SystemConstants.INTERSTATE || salesInvoice.getSezZone().intValue() == 1) {
      if (salesInvoice.getFreightRate() != null || salesInvoice.getService2Rate() != null) {
        cell = new PdfPCell(pageFooter.setInterStateServiceTaxation(salesInvoice));
        cell.setPadding(0);
        cell.setPaddingTop(10);
        taxTable.addCell(cell);
      }
    }
    if (salesInvoice.getIsSalesInterstate() == SystemConstants.INTRASTATE && salesInvoice.getSezZone().intValue() == 0) {
      cell = new PdfPCell(pageFooter.setIntraStateTaxation(invoiceGroup));
      cell.setPadding(0);
      taxTable.addCell(cell).setBorder(0);
      if (salesInvoice.getFreightRate() != null || salesInvoice.getService2Rate() != null) {
        cell = new PdfPCell(pageFooter.setIntraStateServiceTaxation(salesInvoice));
        cell.setPadding(0);
        cell.setPaddingTop(10);
        taxTable.addCell(cell);
      }
    }
    String bankName = "", accountNo = "", ifscCode = "";
    if (companyBank != null) {
      bankName = companyBank.getBankId() != null ? companyBank.getBankId().getName() : "";
      accountNo = companyBank.getAccountNo() != null ? companyBank.getAccountNo() : "";
      ifscCode = companyBank.getIfscCode() != null ? companyBank.getIfscCode() : "";
    }
    cell = new PdfPCell(pageFooter.setAmountWords(salesInvoice.getInvoiceAmount(), companySettings.getShowManufacturer(), manufactureList,
            bankName, accountNo, ifscCode));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(50);
    cell = new PdfPCell(pageFooter.setNote(salesInvoice.getNoteForCustomerOrSupplier()));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(20);
    cell = new PdfPCell(taxTable);
    cell.setPadding(0);
    table.addCell(cell);
    Map<String, String> billMap = new HashMap<>();
    billMap.put("GVAL", AppView.formatDecimal(salesInvoice.getInvoiceAmountNet(), 2));
    billMap.put("INVD", AppView.formatDecimal(salesInvoice.getInvoiceDiscountValue(), 2));
    if (salesInvoice.getCashDiscountApplicable() != null && salesInvoice.getCashDiscountTaxable() != null && salesInvoice.getCashDiscountTaxable() == 1 && salesInvoice.getCashDiscountValue() != null) {
      billMap.put("CASHDTAX", AppView.formatDecimal(salesInvoice.getCashDiscountValue() != null ? salesInvoice.getCashDiscountValue() : 0.00, 2));
    }
    billMap.put("TAXV", AppView.formatDecimal(salesInvoice.getInvoiceAmountAssessableTotal(), 2));
    billMap.put("GST", AppView.formatDecimal(salesInvoice.getInvoiceAmountIgstTotal(), 2));
    if (salesInvoice.getCashDiscountApplicable() != null && salesInvoice.getCashDiscountTaxable() == null && salesInvoice.getCashDiscountValue() != null) {
      billMap.put("CASHD", AppView.formatDecimal(salesInvoice.getCashDiscountValue() != null ? salesInvoice.getCashDiscountValue() : 0.00, 2));
    }
    billMap.put("ROUND", AppView.formatDecimal(salesInvoice.getInvoiceRoundOff(), 2));
    billMap.put("NET", AppView.formatDecimal(salesInvoice.getInvoiceAmount(), 2));
    if (salesInvoice.getFreightRate() != null) {
      billMap.put("FREIGHT1LABEL", salesInvoice.getServiceId().getTitle());
      billMap.put("FREIGHT1", AppView.formatDecimal(salesInvoice.getFreightRate(), 2));
    }
    if (salesInvoice.getService2Rate() != null) {
      billMap.put("FREIGHT2LABEL", salesInvoice.getServices2Id().getTitle());
      billMap.put("FREIGHT2", AppView.formatDecimal(salesInvoice.getService2Rate(), 2));
    }

    if (salesInvoice.getKeralaFloodCessNetValue() != null && salesInvoice.getKeralaFloodCessNetValue() > 0) {
      billMap.put("KFCESS", AppView.formatDecimal(salesInvoice.getKfCessTotal(), 2));
    }
    if (salesInvoice.getTcsNetValue() != null && salesInvoice.getTcsNetValue() > 0) {
      billMap.put("TCS", AppView.formatDecimal(salesInvoice.getTcsNetValue(), 2));
    }
    cell = new PdfPCell(pageFooter.setBillAmount(billMap, true));
    cell.setPadding(3);
    table.addCell(cell);
    return table;
  }

  public SalesInvoice getSalesInvoice() {
    return salesInvoice;
  }

  public void setSalesInvoice(SalesInvoice salesInvoice) {
    this.salesInvoice = salesInvoice;
  }

  public List<InvoiceItem> getInvoiceItemList() {
    return invoiceItemList;
  }

  public void setInvoiceItemList(List<InvoiceItem> invoiceItemList) {
    this.invoiceItemList = invoiceItemList;
  }

  public List<Manufacture> getManufactureList() {
    return manufactureList;
  }

  public void setManufactureList(List<Manufacture> manufactureList) {
    this.manufactureList = manufactureList;
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

  public List<InvoiceGroup> getInvoiceGroup() {
    return invoiceGroup;
  }

  public void setInvoiceGroup(List<InvoiceGroup> invoiceGroup) {
    this.invoiceGroup = invoiceGroup;
  }

  public CompanySettings getCompanySettings() {
    return companySettings;
  }

  public void setCompanySettings(CompanySettings companySettings) {
    this.companySettings = companySettings;
  }

  public boolean isIntraStateSales() {
    return intraStateSales;
  }

  public void setIntraStateSales(boolean intraStateSales) {
    this.intraStateSales = intraStateSales;
  }

  public CompanyAddress getCompanyAddress() {
    return companyAddress;
  }

  public void setCompanyAddress(CompanyAddress companyAddress) {
    this.companyAddress = companyAddress;
  }

  public ConsignmentDetail getConsignmentDetail() {
    return consignmentDetail;
  }

  public void setConsignmentDetail(ConsignmentDetail consignmentDetail) {
    this.consignmentDetail = consignmentDetail;
  }

  public List<String> getDummyList() {
    return dummyList;
  }

  public void setDummyList(List<String> dummyList) {
    this.dummyList = dummyList;
  }

  public boolean isFirstRow() {
    return firstRow;
  }

  public void setFirstRow(boolean firstRow) {
    this.firstRow = firstRow;
  }

  public boolean isIterator() {
    return iterator;
  }

  public void setIterator(boolean iterator) {
    this.iterator = iterator;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public boolean isBillTop() {
    return billTop;
  }

  public void setBillTop(boolean billTop) {
    this.billTop = billTop;
  }

  public CustomerAddress getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(CustomerAddress customerAddress) {
    this.customerAddress = customerAddress;
  }

  public CompanyLicense getCompanyLicense() {
    return companyLicense;
  }

  public void setCompanyLicense(CompanyLicense companyLicense) {
    this.companyLicense = companyLicense;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public CompanyBank getCompanyBank() {
    return companyBank;
  }

  public void setCompanyBank(CompanyBank companyBank) {
    this.companyBank = companyBank;
  }

}
