/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.print;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import spica.scm.common.InvoiceItem;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerLicense;
import spica.scm.domain.Manufacture;
import spica.scm.domain.SalesReturn;
import spica.scm.print.Impl.PageFooterImpl;
import spica.scm.print.Impl.PageHeaderImpl;
import spica.scm.print.Impl.PageProductImpl;
import spica.scm.util.MathUtil;
import spica.scm.util.SalesInvoiceUtil;
import spica.sys.SystemConstants;
import static spica.sys.SystemRuntimeConfig.SDF_DD_MM_YYYY;
import wawo.app.faces.AppView;

/**
 *
 * @author sujesh
 */
public class PrintSalesReturn extends PdfUtil {

  private PdfPTable dataTable;
  private Document document;
  private float totalWidth;

  private SalesReturn salesReturn;
  private PageProduct pageProduct;
  private PdfPTable productTable;
  private CompanySettings companySettings;
  private CompanyAddress companyAddress;
  private CustomerAddress customerAddress;
  private List<CustomerLicense> customerLicenseList;
  private List<CompanyLicense> companyLicenseList;
  private ConsignmentDetail consignmentDetail;
  private List<Manufacture> manufactureList;
  private List<InvoiceItem> invoiceItemList;

  private transient boolean firstRow;
  private transient boolean iterator;
  private transient float headerHeight;
  float pHeight, fHeight, sumTableHeight;

  public PdfPTable initiatePrint(SalesReturn salesReturn, Document document) throws Exception {
    this.salesReturn = salesReturn;
    this.document = document;
    totalWidth = (salesReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) ? pageWidth : landScapeWidth;

    dataTable = new PdfPTable(1);
    dataTable.setTotalWidth(totalWidth);
    dataTable.setLockedWidth(true);

    createPageHeaderBar();
    if (companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
      dataTable.setHeaderRows(1);
    }
    createProductTable();

    return dataTable;
  }

  public void createPageHeaderBar() {
    PdfPTable table = new PdfPTable(1);
    try {
      Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
      setFirstRow(true);
      setIterator(true);
      table.setTotalWidth(getTotalWidth());
      table.setLockedWidth(true);
      do {
        tableMap = new LinkedHashMap<>();
        tableMap = createHeaderTables(isFirstRow(), table);
        PdfPTable headerTable = new PdfPTable(getCellWidth(tableMap.keySet().toArray(new Float[0])));
        headerTable.getDefaultCell().setBorder(0);
        headerTable.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
        for (Map.Entry<Float, PdfPTable> entry : tableMap.entrySet()) {
          headerTable.addCell(entry.getValue());
        }
        table.addCell(headerTable);
      } while (isIterator());
    } catch (Exception e) {
      Logger.getLogger(PrintSalesInvoice.class.getName()).log(Level.SEVERE, null, e);
    }
    headerHeight = table.getTotalHeight();

    PdfPCell headerCell = new PdfPCell(table);
    headerCell.setBorder(0);
    headerCell.setPadding(0);
    headerCell.setBackgroundColor(BaseColor.WHITE);
    getDataTable().addCell(headerCell);
  }

  public Map<Float, PdfPTable> createHeaderTables(boolean firstRow, PdfPTable table) throws Exception {
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    PageHeader header = new PageHeaderImpl();
    Date date = salesReturn.getInvoiceDate();
    String strDate = SDF_DD_MM_YYYY.format(date);
    if (salesReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT) || companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
      if (isFirstRow()) {
        if (salesReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT) && companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_TOP)) {
          PdfPCell cell = new PdfPCell(header.setSalesReturnLabel(salesReturn.getSalesReturnType(), salesReturn.getSalesReturnStatusId().getId()));
          cell.setBorder(0);
          cell.setPaddingBottom(4);
          cell.setBackgroundColor(BaseColor.WHITE);
          table.addCell(cell);
        }
        tableMap.put(40f, header.setCompanyAddress(companyAddress, salesReturn.getCompanyId(), companySettings.getFileContent()));
        tableMap.put(32f, header.setCompanyGST(salesReturn.getCompanyId(), companyLicenseList));
        tableMap.put(28f, header.setBillInvoice(salesReturn.getInvoiceNo(), salesReturn.getEntryDate(), 0, new float[]{50, 50}, false));
        setFirstRow(false);
        setIterator(true);
      } else {
        if ((salesReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT) && companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_MIDDLE))
                || salesReturn.getPrintType().equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
          PdfPCell cell = new PdfPCell(header.setSalesReturnLabel(salesReturn.getSalesReturnType(), salesReturn.getSalesReturnStatusId().getId()));
          cell.setBackgroundColor(BaseColor.WHITE);
          table.addCell(cell);
        }
        tableMap.put(40f, header.setCustomerAddress(salesReturn.getCustomerId(), customerAddress, true));
        tableMap.put(32f, header.setCustomerGST(salesReturn.getCustomerId(), customerLicenseList, SystemConstants.PRINT_MULTIPLE_LINE));
//        tableMap.put(26f, header.setShipingAddress(customerAddress));
        tableMap.put(28f, header.setCarrierDetails(consignmentDetail, SystemConstants.PRINT_MULTIPLE_LINE, true, salesReturn.getDebitNoteNo(), strDate, salesReturn.getAccountInvoiceNo()));
        setIterator(false);
      }
    } else {
      if (companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
        PdfPCell cell = new PdfPCell(header.setSalesReturnLabel(salesReturn.getSalesReturnType(), salesReturn.getSalesReturnStatusId().getId()));
        cell.setBorder(0);
        table.addCell(cell);
        PdfPTable single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.addCell(header.setCompanyAddress(companyAddress, salesReturn.getCompanyId(), companySettings.getFileContent()));
        cell = new PdfPCell(header.setCompanyGST(salesReturn.getCompanyId(), companyLicenseList));
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setPaddingLeft(2);
        cell.setBorder(0);
        single.addCell(cell);
        tableMap.put(40f, single);
        single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.addCell(header.setCustomerAddress(salesReturn.getCustomerId(), customerAddress, true));

        cell = new PdfPCell(header.setCustomerGST(salesReturn.getCustomerId(), customerLicenseList, SystemConstants.PRINT_SINGLE_LINE));
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setPaddingLeft(2);
        cell.setBorder(0);
        single.addCell(cell);
        tableMap.put(32f, single);
//        single = new PdfPTable(1);
//        single.getDefaultCell().setBorder(0);
//        single.addCell(header.setShipingAddress(customerAddress));
//        tableMap.put(25.5f, single);
        single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.addCell(header.setBillInvoice(salesReturn.getInvoiceNo(), salesReturn.getEntryDate(), 0, new float[]{50, 50}, false));
        cell = new PdfPCell(header.setCarrierDetails(consignmentDetail, SystemConstants.PRINT_SINGLE_LINE, true, salesReturn.getDebitNoteNo(), strDate, salesReturn.getAccountInvoiceNo()));
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setPaddingLeft(3);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setBorder(0);
        single.addCell(cell);

        tableMap.put(28f, single);
        setIterator(false);
      }

    }
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

  private void createProductTable() throws Exception {
    pageProduct = new PageProductImpl();

    productTable = pageProduct.setSalesReturnProductTable(getInvoiceItemList(), companySettings, salesReturn.getPrintType());
    PdfPCell productCell = new PdfPCell(productTable);
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
    footer.setTotalWidth(totalWidth);
    footer.setLockedWidth(true);
    sumTableHeight = pHeight + headerHeight + fHeight;
    //      Empty Rows
    float expectHeight = 0;
    if (salesReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) {
      if (companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_TOP)) {
        expectHeight = writer.getCurrentPageNumber() * SystemConstants.PRINT_PORTRAIT_HEIGHT;
      } else {
        expectHeight = writer.getCurrentPageNumber() * SystemConstants.PRINT_PORTRAIT_HEIGHT;
        if (expectHeight < sumTableHeight) {
        }
      }
    } else if (companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
      expectHeight = writer.getCurrentPageNumber() * SystemConstants.PRINT_LANDSCAPE_HEGHT;
    } else if (companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
      expectHeight = writer.getCurrentPageNumber() * SystemConstants.PRINT_LANDSCAPE_HEGHT;

    }
    float[] columns = new float[]{};
    if (salesReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) {
      if (companySettings.getShowManufacturer() == 1) {
        columns = salesReturnPortraitColumnsWithMfg();
      } else {
        columns = salesReturnPortraitColumnsWithOutMfg();
      }
    } else {
      if (companySettings.getShowManufacturer() == 1) {
        columns = salesReturnLandscapeColumnsWithMfg();
      } else {
        columns = salesReturnLandscapeColumnsWithOutMfg();
      }
    }
//      Add Extra Lines
    if (companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
      pageProduct.emptyRows(columns, dataTable.getTotalWidth(), document.getPageSize().getHeight(), headerHeight + 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top() - headerHeight, writer.getDirectContent());
    } else {
      pageProduct.emptyRows(columns, dataTable.getTotalWidth(), document.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top(), writer.getDirectContent());
    }

    if (isBreakable(salesReturn.getPrintType(), writer.getCurrentPageNumber())) {
      document.newPage();
      pageProduct.emptyRows(columns, dataTable.getTotalWidth(), writer.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top(), writer.getDirectContent());
    }

    if (salesReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) {
      footer.writeSelectedRows(0, -1, document.leftMargin(), footer.getTotalHeight() + document.bottomMargin(), writer.getDirectContent());
    } else {
      footer.writeSelectedRows(0, -1, document.leftMargin(), footer.getTotalHeight() + document.bottomMargin(), writer.getDirectContent());
    }
  }

  public PdfPTable createFooterTable() {
    PdfPTable table = new PdfPTable(1);
    try {
      Double sDisc = 0.0;
      Double pDisc = 0.0;
      for (InvoiceItem item : getInvoiceItemList()) {
        sDisc += item.getSchemeDiscountValue() != null ? item.getSchemeDiscountValue() : 0;
        pDisc += item.getProductDiscountValue() != null ? item.getProductDiscountValue() : 0;
      }
      sDisc = MathUtil.roundOff(sDisc, 2);
      pDisc = MathUtil.roundOff(pDisc, 2);
      PageFooter footer = new PageFooterImpl();
      table.setTotalWidth(totalWidth);
      table.setLockedWidth(true);
      table.getDefaultCell().setBorder(0);
      Double qty = 0.00;
      if (salesReturn.getProductNetQuantity() == null) {
        for (InvoiceItem list : getInvoiceItemList()) {
          qty += list.getProductQty();
        }
        salesReturn.setProductNetQuantity(qty);
      }
      String totalQty = salesReturn.getProductNetQuantity() != null ? AppView.formatDecimal(salesReturn.getProductNetQuantity(), 2) : "0";
      PdfPCell cell = new PdfPCell(footer.setItemQty(getInvoiceItemList().size(), totalQty.toString(), null, null, sDisc, pDisc,
              salesReturn.getNetCreditSettlementAmount()));
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell);
      cell = new PdfPCell(createTaxationTable());
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell).setBorder(0);
      cell = new PdfPCell(footer.setDeclaration(companySettings, salesReturn.getCompanyId().getCompanyName(), companyAddress.getDistrictId().getDistrictName()));
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell).setBorder(0);
      cell = new PdfPCell(footer.setWebMail(companyAddress.getEmail(), companyAddress.getWebsite()));
      cell.setPadding(0);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell).setBorder(0);

    } catch (Exception ex) {
      Logger.getLogger(PrintSalesInvoice.class.getName()).log(Level.SEVERE, null, ex);
    }
    fHeight = table.getTotalHeight();

    return table;
  }

  public PdfPTable createTaxationTable() throws Exception {
    PageFooter footer = new PageFooterImpl();
    PdfPTable table = new PdfPTable(new float[]{70, 30});
    table.setTotalWidth(totalWidth);
    table.setLockedWidth(true);
    PdfPTable taxTable = new PdfPTable(1);
    PdfPCell cell = new PdfPCell();
    int salesMode = SalesInvoiceUtil.getSalesMode(salesReturn.getCompanyId(), salesReturn.getCustomerId());
    if (SalesInvoiceUtil.INTER_STATE_SALES == salesMode || salesReturn.getSezZone() == 1) {
      cell = new PdfPCell(footer.setInterStateTaxation(salesReturn.getInvoiceGroup()));
      cell.setPadding(0);
      taxTable.addCell(cell).setBorder(0);
    } else {
      cell = new PdfPCell(footer.setIntraStateTaxation(salesReturn.getInvoiceGroup()));
      cell.setPadding(0);
      taxTable.addCell(cell).setBorder(0);
    }
    cell = new PdfPCell(footer.setAmountWords(salesReturn.getInvoiceAmount(), companySettings.getShowManufacturer(), getManufactureList(), null, null, null));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(30);
    cell = new PdfPCell(footer.setNote(salesReturn.getNoteForCustomerOrSupplier()));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(25);
    cell = new PdfPCell(taxTable);
    cell.setPadding(0);
    table.addCell(cell);
    Map<String, String> billMap = new HashMap<>();
    billMap.put("GST", AppView.formatDecimal(salesReturn.getInvoiceAmountIgst(), 2));
    if (!salesReturn.isDisableRoundOff()) {
      billMap.put("ROUND", AppView.formatDecimal(salesReturn.getInvoiceRoundOff(), 2));
    } else {
      billMap.put("EMPTY", " ");
    }
    billMap.put("NET", AppView.formatDecimal(salesReturn.getInvoiceAmount(), 2));
    billMap.put("TAXV", salesReturn.getInvoiceAmountNet().toString());

    cell = new PdfPCell(footer.setBillAmount(billMap, false));
    cell.setPadding(3);
    table.addCell(cell);
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

  // getters and setters
  public SalesReturn getSalesReturn() {
    return salesReturn;
  }

  public void setSalesReturn(SalesReturn salesReturn) {
    this.salesReturn = salesReturn;
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

  public ConsignmentDetail getConsignmentDetail() {
    return consignmentDetail;
  }

  public void setConsignmentDetail(ConsignmentDetail consignmentDetail) {
    this.consignmentDetail = consignmentDetail;
  }

  public List<Manufacture> getManufactureList() {
    return manufactureList;
  }

  public void setManufactureList(List<Manufacture> manufactureList) {
    this.manufactureList = manufactureList;
  }

  public PdfPTable getDataTable() {
    return dataTable;
  }

  public void setDataTable(PdfPTable dataTable) {
    this.dataTable = dataTable;
  }

  public Document getDocument() {
    return document;
  }

  public void setDocument(Document document) {
    this.document = document;
  }

  public float getTotalWidth() {
    return totalWidth;
  }

  public void setTotalWidth(float totalWidth) {
    this.totalWidth = totalWidth;
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

  public List<InvoiceItem> getInvoiceItemList() {
    return invoiceItemList;
  }

  public void setInvoiceItemList(List<InvoiceItem> invoiceItemList) {
    this.invoiceItemList = invoiceItemList;
  }

}
