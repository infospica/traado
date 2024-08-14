/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import spica.scm.print.Impl.PageFooterImpl;
import spica.scm.print.Impl.PageHeaderImpl;
import spica.scm.print.Impl.PageProductImpl;
import spica.sys.SystemConstants;
import wawo.app.faces.AppView;

/**
 *
 * @author godson
 */
public class PrintSalesServicesInvoice extends PdfUtil {

  private PdfPTable dataTable;
  private Document document;
  private PageProduct pageProduct;
  private PdfPTable productTable;
  private SalesServicesInvoice salesServicesInvoice;
  private List<SalesServicesInvoiceItem> salesServicesInvoicesItemList;
  private CompanyAddress companyAddress;
  private CustomerAddress customerAddress;
  private List<CompanyLicense> companyLicenseList;
  private List<CustomerLicense> customerLicenseList;
  private ConsignmentDetail consignmentDetail;
  private CompanySettings companySettings;
  private Customer customer;
  private float pHeight, fHeight, sumTableHeight, headerHeight;
  private Vendor vendor;
  private VendorAddress vendorAddress;
  private List<VendorLicense> vendorLicenseList;
  private UserProfile userProfile;

  public PdfPTable initiatePrint(SalesServicesInvoice salesServicesInvoice, Document document) throws Exception {
    this.salesServicesInvoice = salesServicesInvoice;
    this.document = document;

    dataTable = new PdfPTable(1);
    dataTable.setTotalWidth(pageWidth);
    dataTable.setLockedWidth(true);

    createPageHeaderBar();
    createProductTable();

    return dataTable;
  }

  private void createPageHeaderBar() throws Exception {
    PageHeader header = new PageHeaderImpl();
    PdfPTable table = new PdfPTable(1);
    table.setTotalWidth(pageWidth);
    table.setLockedWidth(true);
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    tableMap = new LinkedHashMap<>();
    tableMap = createHeaderTables(table);
    PdfPTable headerTable = new PdfPTable(getCellWidth(tableMap.keySet().toArray(new Float[0])));
    headerTable.getDefaultCell().setBorder(0);
    for (Map.Entry<Float, PdfPTable> entry : tableMap.entrySet()) {
      headerTable.addCell(entry.getValue());
    }
    table.addCell(headerTable);
    if (companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_MIDDLE)) {
      PdfPCell cell = new PdfPCell(header.setGStInvoiceLabel(serviceInvoiceLabel));
      cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell.setPaddingBottom(5);
      cell.setBorder(0);
      table.addCell(cell);
    }

    PdfPCell headerCell = new PdfPCell(table);
    headerCell.setBorder(0);
    headerCell.setPadding(0);
    headerHeight = table.getTotalHeight();

    getDataTable().addCell(headerCell);
  }

  public Map<Float, PdfPTable> createHeaderTables(PdfPTable table) throws Exception {
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    PageHeader header = new PageHeaderImpl();
    PdfPCell cell = new PdfPCell();
    if (companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_TOP)) {
      cell = new PdfPCell(header.setGStInvoiceLabel(serviceInvoiceLabel));
      cell.setBorder(0);
      table.addCell(cell).setBorder(0);
    }
    PdfPTable single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    single.addCell(header.setCompanyAddress(companyAddress, salesServicesInvoice.getCompanyId(), companySettings.getFileContent()));
    cell = new PdfPCell(header.setCompanyGST(salesServicesInvoice.getCompanyId(), companyLicenseList));
    cell.setPaddingTop(0);
    cell.setPaddingBottom(0);
    cell.setPaddingLeft(2);
    cell.setBorder(0);
    single.addCell(cell);
    tableMap.put(34.5f, single);
    single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    if (salesServicesInvoice.getAccountingLedgerId().isCustomer()) {
      single.addCell(header.setCustomerAddress(customer, customerAddress, false));
      cell = new PdfPCell(header.setCustomerGST(customer, customerLicenseList, SystemConstants.PRINT_SINGLE_LINE));
    } else if (salesServicesInvoice.getAccountingLedgerId().getAccountingEntityTypeId().getId() == 1) {
      single.addCell(header.setVendorAddress(vendor, vendorAddress));
      cell = new PdfPCell(header.setVendorGST(vendor, vendorLicenseList, SystemConstants.PRINT_SINGLE_LINE));
    } else if (salesServicesInvoice.getAccountingLedgerId().getAccountingEntityTypeId().getId() == 3) {
      single.addCell(header.setUserAddress(userProfile));
      cell = new PdfPCell(header.setUserGST(userProfile, SystemConstants.PRINT_SINGLE_LINE));
    }
    cell.setPaddingTop(0);
    cell.setPaddingBottom(0);
    cell.setPaddingLeft(2);
    cell.setBorder(0);
    single.addCell(cell);
    tableMap.put(33.5f, single);
    single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    single.addCell(header.setBillInvoice(salesServicesInvoice.getSerialNo(), salesServicesInvoice.getEntryDate(), 0, new float[]{34, 66}, false));

    tableMap.put(32f, single);

    return tableMap;
  }

  public float[] getCellWidth(Float keys[]) {
    float[] array = new float[keys.length];
    int index = 0;
    for (Float element : keys) {
      array[index++] = element;
    }
    return array;
  }

  private void createProductTable() {
    pageProduct = new PageProductImpl();

    productTable = pageProduct.setSalesServicesInvoiceProduct(getSalesServicesInvoicesItemList(), companySettings);
    PdfPCell productCell = new PdfPCell(productTable);
    productCell.setBorderWidthTop(0);
    productCell.setPadding(0);
    productCell.setBorderWidthBottom(0);

    getDataTable().addCell(productCell);
  }

  public void createFooterBar(Document document, PdfWriter writer) {
    pHeight = productTable.getTotalHeight();
    if (!document.isOpen()) {
      document.open();
    }
    PdfPTable footer = new PdfPTable(createFooterTable());
    footer.setTotalWidth(pageWidth);
    footer.setLockedWidth(true);
    sumTableHeight = pHeight + headerHeight + fHeight;
    //      Empty Rows
    float expectHeight = 0;
    if (companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_TOP)) {
      expectHeight = writer.getCurrentPageNumber() * SystemConstants.PRINT_PORTRAIT_HEIGHT;
    } else {
      expectHeight = writer.getCurrentPageNumber() * SystemConstants.PRINT_PORTRAIT_HEIGHT;
    }
    float[] columns = salesServicesInvoiceColumn();
    if (writer.getCurrentPageNumber() == 1) {
      pageProduct.emptyRows(columns, dataTable.getTotalWidth(), document.getPageSize().getHeight(), headerHeight + 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top() - headerHeight, writer.getDirectContent());
    } else {
      pageProduct.emptyRows(columns, dataTable.getTotalWidth(), writer.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top(), writer.getDirectContent());
    }
    if (isBreakable(writer.getCurrentPageNumber())) {
      document.newPage();
      pageProduct.emptyRows(columns, dataTable.getTotalWidth(), writer.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top(), writer.getDirectContent());
    }
    footer.writeSelectedRows(0, -1, document.leftMargin(), footer.getTotalHeight() + document.bottomMargin(), writer.getDirectContent());
  }

  public PdfPTable createFooterTable() {
    PdfPTable table = new PdfPTable(1);
    try {
      PageFooter footer = new PageFooterImpl();
      table.setTotalWidth(pageWidth);
      table.setLockedWidth(true);
      table.getDefaultCell().setBorder(0);
      PdfPCell cell = new PdfPCell(createTaxationTable());
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell).setBorder(0);
      cell = new PdfPCell(footer.setDeclaration(companySettings, salesServicesInvoice.getCompanyId().getCompanyName(), companyAddress.getDistrictId().getDistrictName()));
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell).setBorder(0);
      cell = new PdfPCell(footer.setWebMail(companyAddress.getEmail(), companyAddress.getWebsite()));
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(cell).setBorder(0);

    } catch (Exception ex) {
      Logger.getLogger(PrintSalesServicesInvoice.class.getName()).log(Level.SEVERE, null, ex);
    }
    fHeight = table.getTotalHeight();

    return table;
  }

  public PdfPTable createTaxationTable() throws Exception {
    PageFooter footer = new PageFooterImpl();
    PdfPTable table = new PdfPTable(new float[]{70, 30});
    table.setTotalWidth(pageWidth);
    table.setLockedWidth(true);
    PdfPTable taxTable = new PdfPTable(1);
    PdfPCell cell = new PdfPCell(footer.setTotalItem(getSalesServicesInvoicesItemList().size()));
    cell.setPadding(0);
    taxTable.addCell(cell);
    boolean isIntrastate;
    if (salesServicesInvoice.getSgstValue() == null) {
      isIntrastate = false;
    } else {
      isIntrastate = true;
    }
    cell = new PdfPCell(footer.setSalesServicesInvoiceTaxation(salesServicesInvoice.getInvoiceGroup(), isIntrastate));
    cell.setPadding(0);
    taxTable.addCell(cell).setBorder(0);
    cell = new PdfPCell(footer.setAmountWords(salesServicesInvoice.getNetValue(), 0, null, null, null, null));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(35);
    cell = new PdfPCell(taxTable);
    cell.setPadding(0);
    table.addCell(cell);
    Map<String, String> billMap = new HashMap<>();
    billMap.put("TAXV", AppView.formatDecimal(salesServicesInvoice.getAssessableValue(), 2));
    billMap.put("GST", AppView.formatDecimal(salesServicesInvoice.getIgstValue(), 2));
    billMap.put("TDS", AppView.formatDecimal(salesServicesInvoice.getTdsValue(), 2));
    billMap.put("TOTAL", AppView.formatDecimal(salesServicesInvoice.getGrandTotal(), 2));
    billMap.put("ROUND", AppView.formatDecimal(salesServicesInvoice.getRoundOffValue(), 2));
    billMap.put("NET", AppView.formatDecimal(salesServicesInvoice.getNetValue(), 2));

    cell = new PdfPCell(footer.setBillAmount(billMap, false));
    cell.setPadding(3);
    table.addCell(cell);
    return table;
  }

  public boolean isBreakable(int pageNumber) {
    float expectHeight = pageNumber * SystemConstants.PRINT_PORTRAIT_HEIGHT;
    if (expectHeight < sumTableHeight) {
      return true;
    }
    return false;
  }

//  Getters and Setters
  public CompanyAddress getCompanyAddress() {
    return companyAddress;
  }

  public void setCompanyAddress(CompanyAddress companyAddress) {
    this.companyAddress = companyAddress;
  }

  public CompanySettings getCompanySettings() {
    return companySettings;
  }

  public void setCompanySettings(CompanySettings companySettings) {
    this.companySettings = companySettings;
  }

  public PdfPTable getDataTable() {
    return dataTable;
  }

  public List<SalesServicesInvoiceItem> getSalesServicesInvoicesItemList() {
    return salesServicesInvoicesItemList;
  }

  public void setSalesServicesInvoicesItemList(List<SalesServicesInvoiceItem> salesServicesInvoicesItemList) {
    this.salesServicesInvoicesItemList = salesServicesInvoicesItemList;
  }

  public void setDataTable(PdfPTable dataTable) {
    this.dataTable = dataTable;
  }

  public SalesServicesInvoice getSalesServicesInvoice() {
    return salesServicesInvoice;
  }

  public void setSalesServicesInvoice(SalesServicesInvoice salesServicesInvoice) {
    this.salesServicesInvoice = salesServicesInvoice;
  }

  public List<CompanyLicense> getCompanyLicenseList() {
    return companyLicenseList;
  }

  public void setCompanyLicenseList(List<CompanyLicense> companyLicenseList) {
    this.companyLicenseList = companyLicenseList;
  }

  public ConsignmentDetail getConsignmentDetail() {
    return consignmentDetail;
  }

  public void setConsignmentDetail(ConsignmentDetail consignmentDetail) {
    this.consignmentDetail = consignmentDetail;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public List<CustomerLicense> getCustomerLicenseList() {
    return customerLicenseList;
  }

  public void setCustomerLicenseList(List<CustomerLicense> customerLicenseList) {
    this.customerLicenseList = customerLicenseList;
  }

  public CustomerAddress getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(CustomerAddress customerAddress) {
    this.customerAddress = customerAddress;
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

  public List<VendorLicense> getVendorLicenseList() {
    return vendorLicenseList;
  }

  public void setVendorLicenseList(List<VendorLicense> vendorLicenseList) {
    this.vendorLicenseList = vendorLicenseList;
  }

  public UserProfile getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

}
