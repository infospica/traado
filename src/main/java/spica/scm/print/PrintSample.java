/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.print;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
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
import spica.scm.print.Impl.PageHeaderImpl;
import spica.scm.print.Impl.PageProductImpl;
import spica.sys.SystemConstants;

/**
 *
 * @author godson
 */
public class PrintSample extends PdfUtil {

  private SalesInvoice salesInvoice;

  private transient List<InvoiceItem> invoiceItemList;
  private transient List<Manufacture> manufactureList;
  private transient List<CustomerLicense> customerLicenseList;
  private transient List<CompanyLicense> companyLicenseList;
  private transient List<InvoiceGroup> invoiceGroup;
  private transient CompanySettings companySettings;
  private transient CompanyAddress companyAddress;
  private transient CustomerAddress customerAddress;
  private transient ConsignmentDetail consignmentDetail;
  private transient Customer customer;
  private transient Company company;
  private transient List<String> dummyList;
  private transient boolean firstRow;
  private transient boolean iterator;
  private transient boolean billTop;
  private transient boolean intraStateSales;
  private transient PdfPTable dataTable;
  private transient PdfPTable productTable;
  private transient float headerHeight;

  private CompanyLicense companyLicense;
  PdfWriter pdfWriter;
  Document doc;

  public PrintSample() {
  }

  public PrintSample(SalesInvoice salesInvoice) throws DocumentException, IOException {
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
    PdfPCell headerCell = new PdfPCell(getHeaderTable());
    productTable = pageProduct.setProductItems(itemTable, writer, pageWidth);
    PdfPCell productCell = new PdfPCell(productTable);
    headerCell.setBorder(0);
    headerCell.setPadding(0);
    productCell.setPadding(0);
    productCell.setBorder(0);
    dataTable.addCell(headerCell);
    dataTable.setHeaderRows(1);
    dataTable.addCell(productCell);
    dataTable.setSplitLate(false);
    return dataTable;
  }

  public PdfPTable getHeaderTable() {
    PdfPTable table = new PdfPTable(1);
    PageHeader pageHeader = new PageHeaderImpl();
    try {
      Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
      setFirstRow(true);
      setIterator(true);
      table.setTotalWidth(pageWidth);
      do {
        tableMap = new LinkedHashMap<>();
        tableMap = createHeaderTables(isFirstRow(), table);
        PdfPTable headerTable = new PdfPTable(getCellWidth(tableMap.keySet().toArray(new Float[0])));
        headerTable.getDefaultCell().setBorder(0);
        for (Map.Entry<Float, PdfPTable> entry : tableMap.entrySet()) {
          headerTable.addCell(entry.getValue());
        }
        table.addCell(headerTable);
        if (!isIterator()) {
          table.addCell(pageHeader.setGStInvoiceLabel(sampleNote));
        }
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
    if (isFirstRow()) {
      tableMap.put(40f, pageHeader.setCompanyAddress(companyAddress, company, companySettings.getFileContent()));
      tableMap.put(32f, pageHeader.setCompanyGST(company, companyLicenseList));
      tableMap.put(28f, pageHeader.setBillInvoice(salesInvoice.getInvoiceNo(), salesInvoice.getInvoiceDate(), salesInvoice.getSalesCreditDays(), new float[]{50, 50}, false));
      setFirstRow(false);
      setIterator(true);
    } else {
      table.addCell(pageHeader.setGStInvoiceLabel(challan));
      tableMap.put(30f, pageHeader.setCustomerAddress(customer, customerAddress, true));
      tableMap.put(24f, pageHeader.setCustomerGST(customer, customerLicenseList, SystemConstants.PRINT_MULTIPLE_LINE));
      tableMap.put(26f, pageHeader.setShipingAddress(getSalesInvoice().getShippingAddressId()));
      tableMap.put(20f, pageHeader.setCarrierDetails(consignmentDetail, SystemConstants.PRINT_MULTIPLE_LINE,false,null,null,null));
      setIterator(false);
    }
    return tableMap;
  }

  float pHeight, fHeight, sumTableHeight;

  public void createFooterBar(Document document, PdfWriter writer, float[] columnWidth) {
    try {
      PageProduct pageProduct = new PageProductImpl();
      pHeight = productTable.getTotalHeight();

      if (!document.isOpen()) {
        document.open();
      }
      PdfPTable footer = new PdfPTable(createFooterTable());
      footer.setTotalWidth(pageWidth);
      footer.setLockedWidth(true);
      sumTableHeight = pHeight + headerHeight + fHeight;
//      Empty Rows

      float[] columns = sampleInvoiceColumn();
      if (writer.getCurrentPageNumber() == 1) {
        pageProduct.emptyRows(columnWidth, dataTable.getTotalWidth(), document.getPageSize().getHeight(), headerHeight + 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
                document.top() - headerHeight, writer.getDirectContent());
      } else {
        pageProduct.emptyRows(columnWidth, dataTable.getTotalWidth(), writer.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
                document.top(), writer.getDirectContent());
      }

      if (isBreakable(salesInvoice.getPrintMode(), writer.getCurrentPageNumber())) {
        document.newPage();
        pageProduct.emptyRows(columnWidth, dataTable.getTotalWidth(), writer.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
                document.top(), writer.getDirectContent());
      }

      document.getPageSize();
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
      PageFooter pageFooter = new PageFooterImpl();
      table.setTotalWidth(pageWidth);
      table.setLockedWidth(true);
      table.getDefaultCell().setBorder(0);
      PdfPCell cell = new PdfPCell(createTaxationTable());
      cell.setBackgroundColor(BaseColor.WHITE);
      cell.setPadding(0);
      table.addCell(cell).setBorder(0);
      cell = new PdfPCell(pageFooter.setDeclaration(companySettings, salesInvoice.getCompanyId().getCompanyName(), salesInvoice.getCompanyAddressId().getDistrictId().getDistrictName()));
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell).setBorder(0);
    } catch (Exception ex) {
      Logger.getLogger(PrintSalesInvoice.class.getName()).log(Level.SEVERE, null, ex);
    }
    fHeight = table.getTotalHeight();

    return table;
  }

  public PdfPTable createTaxationTable() throws Exception {
    PdfPTable table = new PdfPTable(1);
    table.setTotalWidth(pageWidth);
    table.setLockedWidth(true);
    Paragraph para = new Paragraph();
    para.add(new Phrase("Total Qty: ", fontTax()));
    para.add(new Phrase(String.valueOf(salesInvoice.getProductNetQuantity().toString() == null ? "" : salesInvoice.getProductNetQuantity().toString()), fontBill()));
    PdfPCell totalQty = new PdfPCell(para);
    totalQty.setPadding(5);
    totalQty.setHorizontalAlignment(Element.ALIGN_RIGHT);
    table.addCell(totalQty);
    return table;
  }

  public boolean isBreakable(String printType, int pageNumber) {
    if (printType.equals(SystemConstants.PRINT_PORTRAIT)) {
      float expectedHeight = pageNumber * SystemConstants.PRINT_PORTRAIT_HEIGHT;
      if (expectedHeight < sumTableHeight) {
        return true;
      }
    } else {
      float expectHeight = pageNumber * SystemConstants.PRINT_LANDSCAPE_HEGHT;
      if (expectHeight < sumTableHeight) {
        return true;
      }
    }
    return false;
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

}
