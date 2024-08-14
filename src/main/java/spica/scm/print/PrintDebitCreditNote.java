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
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.DebitCreditNoteItem;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyBank;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.CustomerAddress;
import spica.scm.print.Impl.PageFooterImpl;
import spica.scm.print.Impl.PageHeaderImpl;
import spica.scm.print.Impl.PageProductImpl;
import spica.sys.SystemConstants;
import wawo.app.faces.AppView;

/**
 *
 * @author godson
 */
public class PrintDebitCreditNote extends PdfUtil {

  private PdfPTable dataTable;
  private DebitCreditNote debitCreditNote;
  private Document document;
  private PageProduct pageProduct;
  private PdfPTable productTable;
  private CompanyAddress companyAddress;
  private CustomerAddress customerAddress;
  private List<CompanyLicense> companyLicenseList;
  private CompanySettings companySettings;
  private List<DebitCreditNoteItem> debitCreditNoteItemList;
  private boolean intrastate;
  private String name, address, state, gst, fssai;
  private float pHeight, fHeight, sumTableHeight, headerHeight;
  private Integer invoiceType, party;
  private transient CompanyBank companyBank;

  public PdfPTable initiatePrint(DebitCreditNote debitCreditNote, Document document) throws Exception {

    this.debitCreditNote = debitCreditNote;
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
      PdfPCell cell = new PdfPCell(header.setGStInvoiceLabel(debitNoteLabel));
      cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell.setPaddingBottom(4);
      cell.setBorderWidthTop(0);
      table.addCell(cell);
    }

    PdfPCell headerCell = new PdfPCell(table);
    headerCell.setPadding(0);
    headerCell.setBorder(0);
    headerHeight = table.getTotalHeight();

    getDataTable().addCell(headerCell);
  }

  private Map<Float, PdfPTable> createHeaderTables(PdfPTable table) throws Exception {
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    PageHeader header = new PageHeaderImpl();
    PdfPCell cell = new PdfPCell();
    String partyName = party == 1 ? "SUPPLIER " : "CUSTOMER ";
    String invoiceTypeName = invoiceType == 1 ? "DEBIT NOTE" : "CREDIT NOTE";
    debitNoteLabel = partyName + invoiceTypeName;
    if (companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_TOP)) {

      cell = new PdfPCell(header.setGStInvoiceLabel(debitNoteLabel));
      cell.setBorder(0);
      table.addCell(cell).setBorder(0);
    }
    PdfPTable single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    single.addCell(header.setCompanyAddress(companyAddress, debitCreditNote.getCompanyId(), companySettings.getFileContent()));
    cell = new PdfPCell(header.setCompanyGST(debitCreditNote.getCompanyId(), companyLicenseList));
    cell.setPaddingTop(0);
    cell.setPaddingBottom(0);
    cell.setPaddingLeft(2);
    cell.setBorder(0);
    single.addCell(cell);
    tableMap.put(34.5f, single);
    single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    single.addCell(header.setAddress(name, address, state));
    cell = new PdfPCell(header.setLicense(gst, fssai));
    cell.setPaddingTop(0);
    cell.setPaddingBottom(0);
    cell.setPaddingLeft(2);
    cell.setBorder(0);
    single.addCell(cell);
    tableMap.put(33.5f, single);
    single = new PdfPTable(1);
    single.getDefaultCell().setBorder(0);
    single.addCell(header.setBillInvoice(debitCreditNote.getDocumentNo(), debitCreditNote.getEntryDate(), 0, new float[]{34, 66}, false));

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

    productTable = pageProduct.setDebitCreditNoteProduct(getDebitCreditNoteItemList(), companySettings, debitCreditNote.getTaxableInvoice(), intrastate, party, debitCreditNote.getSezZone());
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
    }
    float[] columns = new float[]{};
    if (intrastate && debitCreditNote.getSezZone().intValue() == 0) {
      columns = debitCreditNoteIntrastateColumn();
    } else {
      columns = debitCreditNoteInterstateColumn();
    }
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
      cell = new PdfPCell(footer.setDeclaration(companySettings, debitCreditNote.getCompanyId().getCompanyName(), companyAddress.getDistrictId().getDistrictName()));
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
    PdfPCell cell = new PdfPCell(footer.setTotalItem(getDebitCreditNoteItemList().size()));
    cell.setPadding(0);
    taxTable.addCell(cell);
    cell = new PdfPCell(footer.setDebitCreidtNoteTaxation(debitCreditNote.getInvoiceGroup(), debitCreditNote.getTaxableInvoice(), intrastate, debitCreditNote.getSezZone()));
    cell.setPadding(0);
    taxTable.addCell(cell).setBorder(0);
    cell = new PdfPCell(footer.setAmountWords(debitCreditNote.getNetValue(), 0, null, null, null, null));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(35);
    cell = new PdfPCell(footer.setNote(null));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(35);
    cell = new PdfPCell(taxTable);
    cell.setPadding(0);
    table.addCell(cell);
    Map<String, String> billMap = new HashMap<>();
    billMap.put("TAXV", AppView.formatDecimal(debitCreditNote.getAssessableValue(), 2));
    if (debitCreditNote.getTaxableInvoice() == 1) {
      billMap.put("GST", AppView.formatDecimal(debitCreditNote.getIgstValue(), 2));
    }
    billMap.put("TOTAL", AppView.formatDecimal(debitCreditNote.getGrandTotal(), 2));
    billMap.put("ROUND", AppView.formatDecimal(debitCreditNote.getRoundOff(), 2));
    billMap.put("NET", AppView.formatDecimal(debitCreditNote.getNetValue(), 2));

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

  public PdfPTable getDataTable() {
    return dataTable;
  }

  public void setDataTable(PdfPTable dataTable) {
    this.dataTable = dataTable;
  }

  public DebitCreditNote getDebitCreditNote() {
    return debitCreditNote;
  }

  public void setDebitCreditNote(DebitCreditNote debitCreditNote) {
    this.debitCreditNote = debitCreditNote;
  }

  public Document getDocument() {
    return document;
  }

  public void setDocument(Document document) {
    this.document = document;
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

  public void setCustomerAddress(CustomerAddress customerAddress) {
    this.customerAddress = customerAddress;
  }

  public List<CompanyLicense> getCompanyLicenseList() {
    return companyLicenseList;
  }

  public void setCompanyLicenseList(List<CompanyLicense> companyLicenseList) {
    this.companyLicenseList = companyLicenseList;
  }

  public CompanySettings getCompanySettings() {
    return companySettings;
  }

  public void setCompanySettings(CompanySettings companySettings) {
    this.companySettings = companySettings;
  }

  public boolean isIntrastate() {
    return intrastate;
  }

  public void setIntrastate(boolean intrastate) {
    this.intrastate = intrastate;
  }

  public List<DebitCreditNoteItem> getDebitCreditNoteItemList() {
    return debitCreditNoteItemList;
  }

  public void setDebitCreditNoteItemList(List<DebitCreditNoteItem> debitCreditNoteItemList) {
    this.debitCreditNoteItemList = debitCreditNoteItemList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public Integer getInvoiceType() {
    return invoiceType;
  }

  public void setInvoiceType(Integer invoiceType) {
    this.invoiceType = invoiceType;
  }

  public Integer getParty() {
    return party;
  }

  public void setParty(Integer party) {
    this.party = party;
  }

  public CompanyBank getCompanyBank() {
    return companyBank;
  }

  public void setCompanyBank(CompanyBank companyBank) {
    this.companyBank = companyBank;
  }

}
