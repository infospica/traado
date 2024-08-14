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
import spica.fin.domain.AccountingTransactionDetail;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.print.Impl.PageFooterImpl;
import spica.scm.print.Impl.PageHeaderImpl;
import spica.scm.print.Impl.PageProductImpl;
import spica.sys.SystemConstants;
import wawo.app.faces.AppView;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
public class PrintchequeReceipt extends PdfUtil {

  private transient float headerHeight;
  private float[] columns;
  float pHeight, fHeight, sumTableHeight;
  private float totalWidth;
  private PdfPTable dataTable;
  private PdfPTable productTable;
  private boolean firstRow;
  private boolean iterator;

  private PageProduct pageProduct;
  private AccountingTransactionDetail partyDetail;
  private Company company;
  private CompanyAddress companyAddress;
  private CompanySettings companySettings;
  private List<CompanyLicense> companyLicenseList;

  public PdfPTable initiatePrint(MainView main) throws Exception {
    dataTable = new PdfPTable(1);
    dataTable.setTotalWidth(pageWidth);
    dataTable.setLockedWidth(true);
    totalWidth = pageWidth;
    createPageHeader();
    createProductTable();
    return dataTable;
  }

  private void createPageHeader() throws Exception {
    PdfPCell headerCell = new PdfPCell(createPageHeaderBar());
    headerCell.setBorder(0);
    headerCell.setPadding(0);
    dataTable.addCell(headerCell);
    dataTable.setSplitLate(false);
  }

  private PdfPCell createPageHeaderBar() throws Exception {
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
      PdfPCell cell = new PdfPCell(header.setGStInvoiceLabel(SystemConstants.PRINT_CHEQUE_RECEIPT));
      cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell.setPaddingBottom(4);
      cell.setBorderWidthTop(0);
      table.addCell(cell);
    }

    PdfPCell headerCell = new PdfPCell(table);
    headerCell.setPadding(0);
    headerCell.setBorder(0);
    headerHeight = table.getTotalHeight();
    return headerCell;
  }

  private Map<Float, PdfPTable> createHeaderTables(PdfPTable table) throws Exception {
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    PageHeader header = new PageHeaderImpl();
    PdfPCell cell = new PdfPCell();
    if (companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_TOP)) {
      cell = new PdfPCell(header.setGStInvoiceLabel(SystemConstants.PRINT_CHEQUE_RECEIPT));
      cell.setBorder(0);
      table.addCell(cell).setBorder(0);
    }
    PdfPTable single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    single.addCell(header.setCompanyAddress(companyAddress, company, companySettings.getFileContent()));
    cell = new PdfPCell(header.setCompanyGST(company, companyLicenseList));
    cell.setPaddingTop(0);
    cell.setPaddingBottom(0);
    cell.setPaddingLeft(2);
    cell.setBorder(0);
    single.addCell(cell);
    tableMap.put(34.5f, single);
    single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    single.addCell(header.setAddress(partyDetail.getAccountingLedgerId().getTitle(), partyDetail.getAccountingLedgerId().getAddress(), null));
    cell = new PdfPCell(header.setLicense(partyDetail.getAccountingLedgerId().getGstTaxNo(), null));
    cell.setPaddingTop(0);
    cell.setPaddingBottom(0);
    cell.setPaddingLeft(2);
    cell.setBorder(0);
    single.addCell(cell);
    tableMap.put(33.5f, single);
    single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    single.addCell(header.setBillInvoice(partyDetail.getAccountingTransactionId().getDocumentNumber(), partyDetail.getAccountingTransactionId().getEntryDate(), 0, new float[]{34, 66}, true));

    tableMap.put(32f, single);

    return tableMap;
  }

  private void createProductTable() {
    pageProduct = new PageProductImpl();
    productTable = pageProduct.setChequeReceiptProduct(partyDetail, companySettings);
    PdfPCell productCell = new PdfPCell(productTable);
    productCell.setBorderWidthTop(0);
    productCell.setPadding(0);
    productCell.setBorderWidthBottom(0);
    dataTable.addCell(productCell);
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
    }
    float[] columns = new float[]{5, 20, 20, 20, 20};

    if (writer.getCurrentPageNumber() == 1) {
      pageProduct.emptyRows(columns, dataTable.getTotalWidth(), document.getPageSize().getHeight(), 750, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top() - headerHeight, writer.getDirectContent());
    } else {
      pageProduct.emptyRows(columns, dataTable.getTotalWidth(), writer.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top(), writer.getDirectContent());
    }

    footer.writeSelectedRows(0, -1, document.leftMargin(), 600, writer.getDirectContent());
  }

  private PdfPTable createFooterTable() {
    PdfPTable table = new PdfPTable(1);
    try {
      companySettings.setDisclaimer(false);
      PageFooter footer = new PageFooterImpl();
      table.setTotalWidth(pageWidth);
      table.setLockedWidth(true);
      table.getDefaultCell().setBorder(0);
      PdfPCell cell = new PdfPCell(createTaxationTable());
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell).setBorder(0);
      cell = new PdfPCell(footer.setDeclaration(companySettings, company.getCompanyName(), companyAddress.getDistrictId().getDistrictName()));
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
    PdfPCell cell = new PdfPCell(footer.setTotalItem(1));
    cell.setPadding(0);
    taxTable.addCell(cell);
    cell = new PdfPCell(new PdfPTable(1));
    cell.setPadding(0);
    taxTable.addCell(cell).setBorder(0);
    cell = new PdfPCell(footer.setAmountWords(partyDetail.getDetailItem().get(0).getNetAmount(), 0, null, null, null, null));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(35);
    cell = new PdfPCell(taxTable);
    cell.setPadding(0);
    table.addCell(cell);
    Map<String, String> billMap = new HashMap<>();
//    billMap.put("TAXV", AppView.formatDecimal(debitCreditNote.getAssessableValue(), 2));
//    if (debitCreditNote.getTaxableInvoice() == 1) {
//      billMap.put("GST", AppView.formatDecimal(debitCreditNote.getIgstValue(), 2));
//    }
//    billMap.put("TOTAL", AppView.formatDecimal(debitCreditNote.getGrandTotal(), 2));
//    billMap.put("ROUND", AppView.formatDecimal(debitCreditNote.getRoundOff(), 2));
    billMap.put("NET", AppView.formatDecimal(partyDetail.getDetailItem().get(0).getNetAmount(), 2));

    cell = new PdfPCell(footer.setBillAmount(billMap, false));
    cell.setPadding(3);
    table.addCell(cell);
    return table;
  }

  public void setPartyDetail(AccountingTransactionDetail partyDetail) {
    this.partyDetail = partyDetail;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public void setFirstRow(boolean firstRow) {
    this.firstRow = firstRow;
  }

  public void setIterator(boolean iterator) {
    this.iterator = iterator;
  }

  public boolean isIterator() {
    return iterator;
  }

  public boolean isFirstRow() {
    return firstRow;
  }

  public void setCompanyLicenseList(List<CompanyLicense> companyLicenseList) {
    this.companyLicenseList = companyLicenseList;
  }

  public void setCompanyAddress(CompanyAddress companyAddress) {
    this.companyAddress = companyAddress;
  }

  public void setCompanySettings(CompanySettings companySettings) {
    this.companySettings = companySettings;
  }

}
