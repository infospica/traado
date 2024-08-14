/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.service.AccountingTransactionDetailService;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanySettings;
import spica.scm.export.ExcelUtil;
import spica.scm.print.Impl.PageFooterImpl;
import spica.scm.print.Impl.PageHeaderImpl;
import spica.scm.service.CompanyAddressService;
import spica.scm.service.CompanyLicenseService;
import spica.scm.service.CompanySettingsService;
import spica.sys.SystemConstants;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Godson Joseph
 */
public class PrintJournal extends PdfUtil {

  private transient float headerHeight;
  private PdfPTable headerTable;
  //private float[] columns;
  float pHeight, fHeight, sumTableHeight;
  // private float totalWidth;
  private PdfPTable dataTable;
//  private PdfPTable productTable;
//  private boolean firstRow;
  //private boolean iterator;
//  private Double totalDebit;
//  private Double totalCredit;
  // private List<AccountingTransactionDetail> detailList;
  // private PageProduct pageProduct;
  // private AccountingTransaction partyDetail;
  // private Company company;
  // private CompanyAddress companyAddress;
//  private CompanySettings companySettings;
  // private List<CompanyLicense> companyLicenseList;

  public void initiatePrint(MainView main, String fileName, AccountingTransaction partyDetail) throws Exception {
    //  this.partyDetail = partyDetail;

    Document document = ExcelUtil.getPortrait();
    PdfWriter writer = ExcelUtil.getPdfWriter(document, fileName, null); //if file path then out to file else as stream
    document.open();
    List<AccountingTransactionDetail> detailList = AccountingTransactionDetailService.selectByTransaction(main, partyDetail);
    CompanyAddress companyAddress = (CompanyAddressService.selectCompanyRegisteredAddress(main, partyDetail.getCompanyId()));
    //  
    companyAddress.getCompanyId().setCompanySettings(CompanySettingsService.selectIfNull(main, partyDetail.getCompanyId()));
    //  company = partyDetail.getCompanyId();
    dataTable = new PdfPTable(1);
    dataTable.setTotalWidth(pageWidth);
    dataTable.setLockedWidth(true);
    // totalWidth = pageWidth;

    createPageHeader(main, companyAddress, partyDetail);

    AccountingTransactionDetail detail = new AccountingTransactionDetail();
    detail.setAccountingLedgerId(new AccountingLedger(null, partyDetail.getNarration() != null ? ("NARRATION - " + partyDetail.getNarration()) : ""));
    detailList.add(detail);
    PdfPTable productTable = setJournalProduct(document, detailList, companyAddress.getCompanyId().getCompanySettings());
    PdfPCell productCell = new PdfPCell(productTable);
    productCell.setBorderWidthTop(0);
    productCell.setPadding(0);
    productCell.setBorderWidthBottom(0);
    dataTable.addCell(productCell);

    document.add(dataTable);

    PdfPTable table = new PdfPTable(1);
    table.setTotalWidth(pageWidth);
    table.setLockedWidth(true);
    table.getDefaultCell().setBorder(0);

    Double totalDebit = 0.0;
    Double totalCredit = 0.0;
    for (AccountingTransactionDetail d : detailList) {
      totalDebit += d.getDebit() != null ? d.getDebit() : 0.0;
      totalCredit += d.getCredit() != null ? d.getCredit() : 0.0;
    }

    PdfPTable total = new PdfPTable(new float[]{60, 20, 20});
    total.setTotalWidth(pageWidth);
    total.setLockedWidth(true);
    PdfPCell cell = new PdfPCell(new Phrase("TOTAL", fontCalculation()));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    total.addCell(cell);
    cell = new PdfPCell(new Phrase(String.format("%.2f", totalDebit), fontCalculation()));
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    total.addCell(cell);
    cell = new PdfPCell(new Phrase(String.format("%.2f", totalCredit), fontCalculation()));
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    total.addCell(cell);
    cell.setMinimumHeight(15);
    total.addCell(cell).setMinimumHeight(25);

    cell = new PdfPCell(total);
    cell.setPadding(0);
    cell.setBackgroundColor(BaseColor.WHITE);
    table.addCell(cell).setBorder(0);

    createFooterBar(document, writer, companyAddress, table, productTable.getTotalHeight());

    ExcelUtil.responseComplete(document, null);
  }

  private void createPageHeader(MainView main, CompanyAddress companyAddress, AccountingTransaction partyDetail) throws Exception {
    PageHeader header = new PageHeaderImpl();
    PdfPTable table = new PdfPTable(1);
    table.setTotalWidth(pageWidth);
    table.setLockedWidth(true);
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    tableMap = new LinkedHashMap<>();
    tableMap = createHeaderTables(table, main, companyAddress, partyDetail);
    PdfPTable headerCellTable = new PdfPTable(getCellWidth(tableMap.keySet().toArray(new Float[0])));
    headerCellTable.getDefaultCell().setBorder(0);
    for (Map.Entry<Float, PdfPTable> entry : tableMap.entrySet()) {
      headerCellTable.addCell(entry.getValue());
    }
    table.addCell(headerCellTable);
    if (companyAddress.getCompanyId().getCompanySettings().getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_MIDDLE)) {
      PdfPCell cell = new PdfPCell(header.setGStInvoiceLabel((partyDetail.getVoucherTypeId() != null ? StringUtil.toUpperCase(partyDetail.getVoucherTypeId().getTitle()) : "") + " VOUCHER"));
      cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell.setPaddingBottom(4);
      cell.setBorderWidthTop(0);
      table.addCell(cell);
    }

    PdfPCell headerCell = new PdfPCell(table);
    headerCell.setPadding(0);
    headerCell.setBorder(0);
    headerTable = table;
    headerHeight = table.getTotalHeight();

    headerCell = new PdfPCell(headerCell);
    headerCell.setBorder(0);
    headerCell.setPadding(0);
    dataTable.addCell(headerCell);
    dataTable.setSplitLate(false);
  }

  private Map<Float, PdfPTable> createHeaderTables(PdfPTable table, MainView main, CompanyAddress companyAddress, AccountingTransaction tran) throws Exception {
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    PageHeader header = new PageHeaderImpl();
    PdfPCell cell = new PdfPCell();
    if (companyAddress.getCompanyId().getCompanySettings().getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_TOP)) {
      String suff = (tran.getVoucherTypeId().getId() == AccountingConstant.VOUCHER_TYPE_JOURNAL.getId().intValue()) ? SystemConstants.PRINT_VOUCHER : "";
      cell = new PdfPCell(header.setGStInvoiceLabel(tran.getVoucherTypeId().getTitle().toUpperCase() + suff));
      cell.setBorder(0);
      table.addCell(cell).setBorder(0);
    }
    PdfPTable single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    single.addCell(header.setCompanyAddress(companyAddress, companyAddress.getCompanyId(), companyAddress.getCompanyId().getCompanySettings().getFileContent()));
    cell = new PdfPCell(header.setCompanyGST(companyAddress.getCompanyId(), CompanyLicenseService.licenseListByCompany(main, companyAddress.getCompanyId())));
    cell.setPaddingTop(0);
    cell.setPaddingBottom(0);
    cell.setPaddingLeft(2);
    cell.setBorder(0);
    single.addCell(cell);
    tableMap.put(34.5f, single);

    single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    single.addCell(header.setBillInvoice(tran.getDocumentNumber(), tran.getEntryDate(), 0, new float[]{34, 66}, true));

    tableMap.put(32f, single);

    return tableMap;
  }

  private PdfPTable setJournalProduct(Document document, List<AccountingTransactionDetail> partyDetails, CompanySettings companySettings) {
    float[] columns = new float[]{5, 55, 20, 20};
    PdfPTable productTable = new PdfPTable(columns);
    productTable.getDefaultCell().setBorderWidthBottom(0);
    productTable.setTotalWidth(document.getPageSize().getWidth());
    ExcelUtil.addHead(productTable, "Sl#");
    ExcelUtil.addHead(productTable, "Particulars");
    ExcelUtil.addHead(productTable, "Debit");
    ExcelUtil.addHead(productTable, "Credit");
    productTable.setHeaderRows(1);
    int i = 1;
    for (AccountingTransactionDetail detail : partyDetails) {
      if (detail.getId() != null) {
        ExcelUtil.addNumber(productTable, i, null);
      } else {
        ExcelUtil.addString(productTable, "", null);
      }
      ExcelUtil.addString(productTable, (detail.getAccountingLedgerId() != null ? detail.getAccountingLedgerId().getTitle() : ""), null);
      ExcelUtil.addDecimal(productTable, detail.getDebit(), Element.ALIGN_RIGHT);
      ExcelUtil.addDecimal(productTable, detail.getCredit(), Element.ALIGN_RIGHT);
      i++;
    }
    return productTable;
  }

  private void createFooterBar(Document document, PdfWriter writer, CompanyAddress companyAddress, PdfPTable fTable, float pHeight) {

    if (!document.isOpen()) {
      document.open();
    }

    companyAddress.getCompanyId().getCompanySettings().setDisclaimer(false);
    PageFooter pFoot = new PageFooterImpl();

    PdfPCell cell = new PdfPCell(pFoot.setDeclaration(companyAddress.getCompanyId().getCompanySettings(), companyAddress.getCompanyId().getCompanyName(), companyAddress.getDistrictId().getDistrictName()));
    cell.setPadding(0);
    cell.setBackgroundColor(BaseColor.WHITE);
    fTable.addCell(cell).setBorder(0);
    cell = new PdfPCell(pFoot.setWebMail(companyAddress.getEmail(), companyAddress.getWebsite()));
    cell.setPadding(0);
    cell.setBackgroundColor(BaseColor.WHITE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    fTable.addCell(cell).setBorder(0);

    fHeight = fTable.getTotalHeight();

    PdfPTable footer = new PdfPTable(fTable);
    footer.setTotalWidth(pageWidth);
    footer.setLockedWidth(true);
    sumTableHeight = pHeight + fHeight;
    //      Empty Rows
    float expectHeight = 0;
    if (companyAddress.getCompanyId().getCompanySettings().getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_TOP)) {
      expectHeight = writer.getCurrentPageNumber() * SystemConstants.PRINT_PORTRAIT_HEIGHT;
    } else {
      expectHeight = writer.getCurrentPageNumber() * SystemConstants.PRINT_PORTRAIT_HEIGHT;
      if (expectHeight < sumTableHeight) {
      }
    }
    float[] columns = new float[]{5, 55, 20, 20};
    if (companyAddress.getCompanyId().getCompanySettings().getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
      ExcelUtil.emptyRows(columns, dataTable.getTotalWidth(), document.getPageSize().getHeight(), headerHeight + 40.5f, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top() - headerHeight, writer.getDirectContent());
    } else {
      ExcelUtil.emptyRows(columns, dataTable.getTotalWidth(), writer.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top(), writer.getDirectContent());
    }

    if (isBreakable(SystemConstants.PRINT_PORTRAIT, writer.getCurrentPageNumber(), companyAddress.getCompanyId().getCompanySettings().getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES))) {
      document.newPage();
      if (companyAddress.getCompanyId().getCompanySettings().getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
        if (companyAddress.getCompanyId().getCompanySettings().getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
          headerTable.writeSelectedRows(0, -1, document.leftMargin(), document.top(), writer.getDirectContent());
        }
        ExcelUtil.emptyRows(columns, dataTable.getTotalWidth(), document.getPageSize().getHeight(), headerHeight + 40.5f, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
                document.top() - headerHeight, writer.getDirectContent());
      } else {
        ExcelUtil.emptyRows(columns, dataTable.getTotalWidth(), writer.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
                document.top(), writer.getDirectContent());
      }
    }

    footer.writeSelectedRows(0, -1, document.leftMargin(), footer.getTotalHeight() + document.bottomMargin(), writer.getDirectContent());
  }

//  public void setFirstRow(boolean firstRow) {
//    this.firstRow = firstRow;
//  }
//
//  public void setIterator(boolean iterator) {
//    this.iterator = iterator;
//  }
//
//  public boolean isIterator() {
//    return iterator;
//  }
//  public boolean isFirstRow() {
//    return firstRow;
//  }
  public boolean isBreakable(String printType, int pageNumber, boolean showHeaderonAllPages) {
    if (showHeaderonAllPages) {
      sumTableHeight += pageNumber * headerHeight;
    }
    if (printType.equals(SystemConstants.PRINT_PORTRAIT)) {
      float expectedHeight = pageNumber * SystemConstants.PRINT_PORTRAIT_HEIGHT;
      if (expectedHeight < sumTableHeight) {
        return true;
      }
    } else {
      float expectHeight = pageNumber * (SystemConstants.PRINT_LANDSCAPE_HEGHT - 65);
      if (pageNumber > 3) {
        expectHeight = pageNumber * (SystemConstants.PRINT_LANDSCAPE_HEGHT - 65);
      }
      if (expectHeight < sumTableHeight) {
        return true;
      }
    }
    return false;
  }

//  public void setTotalDebit(Double totalDebit) {
//    this.totalDebit = totalDebit;
//  }
//
//  public void setTotalCredit(Double totalCredit) {
//    this.totalCredit = totalCredit;
//  }
}
