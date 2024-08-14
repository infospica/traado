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
import spica.scm.domain.CompanyBank;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorLicense;
import spica.scm.print.Impl.PageFooterImpl;
import spica.scm.print.Impl.PageHeaderImpl;
import spica.scm.print.Impl.PageProductImpl;
import spica.scm.util.MathUtil;
import spica.sys.SystemConstants;
import wawo.app.faces.AppView;

/**
 *
 * @author sujesh
 */
public class PrintPurchaseReturn extends PdfUtil {

  private PdfPTable dataTable;
  private PdfPTable headerTable;
  private Document document;
  private float totalWidth;
  private PurchaseReturn purchaseReturn;
  private CompanyAddress companyAddress;
  private List<CompanyLicense> companyLicenseList;
  private VendorAddress vendorAddress;
  private Vendor vendor;
  private CompanySettings companySettings;
  private boolean firstRow;
  private boolean iterator;
  private boolean intrastate;
  private transient float headerHeight;
  private RecipientAddress recipientAddress;
  private PageProduct pageProduct;
  private PdfPTable productTable;
  private List<PurchaseReturnItemReplica> purchaseReturnItemReplicaList;
  float pHeight, fHeight, sumTableHeight;
  private CompanyBank companyBank;
  private List<VendorLicense> vendorLicenseList;

  public PdfPTable initiatePrint(PurchaseReturn purchaseReturn, Document document, ItemTable itemTable, PdfWriter writer) throws Exception {
    this.purchaseReturn = purchaseReturn;
    this.document = document;
    recipientAddress = new RecipientAddress(vendor, vendorAddress);
    recipientAddress.setVendorLicenseList(vendorLicenseList);
    totalWidth = (purchaseReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) ? pageWidth : landScapeWidth;
    dataTable = new PdfPTable(1);
    dataTable.setTotalWidth(totalWidth);
    dataTable.setLockedWidth(true);
    createPageHeaderBar();
    if (companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
      dataTable.setHeaderRows(1);
    }
    createProductTable(itemTable, writer);
    return dataTable;
  }

  public void createPageHeaderBar() {
    PdfPTable table = new PdfPTable(1);
    table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
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
    table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
    PdfPCell headerCell = new PdfPCell(table);
    headerCell.setBorder(0);
    headerCell.setPadding(0);
    headerCell.setBackgroundColor(BaseColor.WHITE);

    headerTable = table;

    getDataTable().addCell(headerCell);
  }

  public Map<Float, PdfPTable> createHeaderTables(boolean firstRow, PdfPTable table) throws Exception {
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    PageHeader header = new PageHeaderImpl();
    if (purchaseReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT) || companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
      if (isFirstRow()) {
        if (purchaseReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT) && companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_TOP)) {
          PdfPCell cell = new PdfPCell(header.setPurchaseReturnLabel(purchaseReturn.getPurchaseReturnStockCat().getId(), purchaseReturn.getPurchaseReturnStatusId().getId()));
          cell.setBackgroundColor(BaseColor.WHITE);
          cell.setBorder(0);
          cell.setPaddingBottom(4);
          table.addCell(cell);
        }
        tableMap.put(40f, header.setCompanyAddress(companyAddress, purchaseReturn.getCompanyId(), companySettings.getFileContent()));
        tableMap.put(32f, header.setCompanyGST(purchaseReturn.getCompanyId(), companyLicenseList));
        tableMap.put(28f, header.setBillInvoice(purchaseReturn.getInvoiceNo(), purchaseReturn.getEntryDate(), 0, new float[]{50, 50}, false));
        setFirstRow(false);
        setIterator(true);
      } else {
        if ((purchaseReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT) && companySettings.getPrintBillTitlePosition().equals(SystemConstants.PRINT_BILL_TITLE_MIDDLE))
                || purchaseReturn.getPrintType().equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
          table.addCell(header.setPurchaseReturnLabel(purchaseReturn.getPurchaseReturnStockCat().getId(), purchaseReturn.getPurchaseReturnStatusId().getId()));
        }
        tableMap.put(30f, header.setRecipientAddress(recipientAddress, true));
        tableMap.put(24f, header.setRecipientGST(recipientAddress, SystemConstants.PRINT_MULTIPLE_LINE));
        tableMap.put(26f, header.setRecipientShipingAddress(recipientAddress.getShippingAddress()));
        tableMap.put(20f, header.setCarrierDetails(null, SystemConstants.PRINT_MULTIPLE_LINE, false, null, null, null));
        setIterator(false);
      }
    } else {
      if (companySettings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
        PdfPCell cell = new PdfPCell(header.setPurchaseReturnLabel(purchaseReturn.getPurchaseReturnStockCat().getId(), purchaseReturn.getPurchaseReturnStatusId().getId()));
        cell.setBorder(0);
        table.addCell(cell);
        PdfPTable single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
        single.addCell(header.setCompanyAddress(companyAddress, purchaseReturn.getCompanyId(), companySettings.getFileContent()));
        cell = new PdfPCell(header.setCompanyGST(purchaseReturn.getCompanyId(), companyLicenseList));
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setPaddingLeft(2);
        cell.setBorder(0);
        single.addCell(cell);
        tableMap.put(27.5f, single);
        single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.addCell(header.setRecipientAddress(recipientAddress, true));

        cell = new PdfPCell(header.setRecipientGST(recipientAddress, SystemConstants.PRINT_SINGLE_LINE));
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setPaddingLeft(2);
        cell.setBorder(0);
        single.addCell(cell);
        tableMap.put(25f, single);
        single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.addCell(header.setShipingAddress(null));
        tableMap.put(25.5f, single);
        single = new PdfPTable(1);
        single.getDefaultCell().setBorder(0);
        single.addCell(header.setBillInvoice(purchaseReturn.getInvoiceNo(), purchaseReturn.getEntryDate(), 0, new float[]{50, 50}, false));
        cell = new PdfPCell(header.setCarrierDetails(null, SystemConstants.PRINT_SINGLE_LINE, false, null, null, null));
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

  private void createProductTable(ItemTable itemTable, PdfWriter writer) throws Exception {
    pageProduct = new PageProductImpl();
    intrastate = purchaseReturn.getIsReturnInterstate() == 1 ? false : true;
    productTable = pageProduct.setProductItems(itemTable, writer, totalWidth);
    PdfPCell productCell = new PdfPCell(productTable);
    productCell.setPadding(0);
    productCell.setBorder(0);

    getDataTable().addCell(productCell);
  }

  public void createFooterBar(Document document, PdfWriter writer, float[] columns) {
    pHeight = productTable.getTotalHeight();
    if (!document.isOpen()) {
      document.open();
    }
    PdfPTable footer = new PdfPTable(createFooterTable());
    footer.setTotalWidth(totalWidth);
    footer.setLockedWidth(true);
    sumTableHeight = pHeight + fHeight;
    //      Empty Rows
    float expectHeight = 0;
    if (purchaseReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) {
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

    if (companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
      pageProduct.emptyRows(columns, dataTable.getTotalWidth(), document.getPageSize().getHeight(), headerHeight + 40.5f, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top() - headerHeight, writer.getDirectContent());
    } else {
      pageProduct.emptyRows(columns, dataTable.getTotalWidth(), writer.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
              document.top(), writer.getDirectContent());
    }

    if (isBreakable(purchaseReturn.getPrintType(), writer.getCurrentPageNumber(), companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES))) {
      document.newPage();
      if (companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
        if (companySettings.getHeaderOnAllPages().equals(SystemConstants.PRINT_SHOW_HEADER_ON_ALL_PAGES)) {
          headerTable.writeSelectedRows(0, -1, document.leftMargin(), document.top(), writer.getDirectContent());
        }
        pageProduct.emptyRows(columns, dataTable.getTotalWidth(), document.getPageSize().getHeight(), headerHeight + 40.5f, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
                document.top() - headerHeight, writer.getDirectContent());
      } else {
        pageProduct.emptyRows(columns, dataTable.getTotalWidth(), writer.getPageSize().getHeight(), 55, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
                document.top(), writer.getDirectContent());
      }
    }

    if (purchaseReturn.getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) {
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
      for (PurchaseReturnItemReplica item : getPurchaseReturnItemReplicaList()) {
        sDisc += item.getSchemeDiscountValue() != null ? item.getSchemeDiscountValue() : 0;
        pDisc += item.getProductDiscountValue() != null ? item.getProductDiscountValue() : 0;
      }
      sDisc = MathUtil.roundOff(sDisc, 2);
      pDisc = MathUtil.roundOff(pDisc, 2);
      PageFooter footer = new PageFooterImpl();
      table.setTotalWidth(totalWidth);
      table.setLockedWidth(true);
      table.getDefaultCell().setBorder(0);

      String totalQty = purchaseReturn.getProductNetQuantity() != null ? AppView.formatDecimal(purchaseReturn.getProductNetQuantity(), 2) : "0";
      PdfPCell cell = new PdfPCell(footer.setItemQty(getPurchaseReturnItemReplicaList().size(), totalQty.toString(), null, null, sDisc, pDisc, null));
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell);
      cell = new PdfPCell(createTaxationTable());
      cell.setPadding(0);
      cell.setBackgroundColor(BaseColor.WHITE);
      table.addCell(cell).setBorder(0);
      cell = new PdfPCell(footer.setDeclaration(companySettings, purchaseReturn.getCompanyId().getCompanyName(), companyAddress.getDistrictId().getDistrictName()));
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
    if (!intrastate || purchaseReturn.getSezZone().intValue() == 1) {
      cell = new PdfPCell(footer.setInterStateTaxation(purchaseReturn.getInvoiceGroup()));
      cell.setPadding(0);
      taxTable.addCell(cell).setBorder(0);
    } else {
      cell = new PdfPCell(footer.setIntraStateTaxation(purchaseReturn.getInvoiceGroup()));
      cell.setPadding(0);
      taxTable.addCell(cell).setBorder(0);
    }
    cell = new PdfPCell(footer.setAmountWords(purchaseReturn.getInvoiceAmount(), companySettings.getShowManufacturer(), null, null, null, null));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(35);
    cell = new PdfPCell(footer.setNote(purchaseReturn.getNoteForCustomerOrSupplier()));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(35);
    cell = new PdfPCell(taxTable);
    cell.setPadding(0);
    table.addCell(cell);
    Map<String, String> billMap = new HashMap<>();
    billMap.put("GST", AppView.formatDecimal(purchaseReturn.getInvoiceAmountIgst(), 2));
    billMap.put("ROUND", AppView.formatDecimal(purchaseReturn.getInvoiceRoundOff(), 2));
    billMap.put("NET", AppView.formatDecimal(purchaseReturn.getInvoiceAmount(), 2));
    billMap.put("TAXV", AppView.formatDecimal(purchaseReturn.getInvoiceAmountNet(), 2));

    cell = new PdfPCell(footer.setBillAmount(billMap, false));
    cell.setPadding(3);
    table.addCell(cell);
    return table;
  }

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

  /**
   * Get Cell width
   *
   * @param keys
   * @return
   */
  public float[] getCellWidth(Float keys[]) {
    float[] array = new float[keys.length];
    int index = 0;
    for (Float element : keys) {
      array[index++] = element;
    }
    return array;
  }

  // Getters & Setters
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

  public CompanyAddress getCompanyAddress() {
    return companyAddress;
  }

  public void setCompanyAddress(CompanyAddress companyAddress) {
    this.companyAddress = companyAddress;
  }

  public float getHeaderHeight() {
    return headerHeight;
  }

  public void setHeaderHeight(float headerHeight) {
    this.headerHeight = headerHeight;
  }

  public VendorAddress getVendorAddress() {
    return vendorAddress;
  }

  public void setVendorAddress(VendorAddress vendorAddress) {
    this.vendorAddress = vendorAddress;
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

  public boolean isFirstRow() {
    return firstRow;
  }

  public void setPurchaseReturn(PurchaseReturn purchaseReturn) {
    this.purchaseReturn = purchaseReturn;
  }

  public void setFirstRow(boolean firstRow) {
    this.firstRow = firstRow;
  }

  public void setIterator(boolean iterator) {
    this.iterator = iterator;
  }

  public PurchaseReturn getPurchaseReturn() {
    return purchaseReturn;
  }

  public boolean isIterator() {
    return iterator;
  }

  public Vendor getVendor() {
    return vendor;
  }

  public void setVendor(Vendor vendor) {
    this.vendor = vendor;
  }

  public RecipientAddress getRecipientAddress() {
    return recipientAddress;
  }

  public void setRecipientAddress(RecipientAddress recipientAddress) {
    this.recipientAddress = recipientAddress;
  }

  public PageProduct getPageProduct() {
    return pageProduct;
  }

  public void setPageProduct(PageProduct pageProduct) {
    this.pageProduct = pageProduct;
  }

  public PdfPTable getProductTable() {
    return productTable;
  }

  public void setProductTable(PdfPTable productTable) {
    this.productTable = productTable;
  }

  public List<PurchaseReturnItemReplica> getPurchaseReturnItemReplicaList() {
    return purchaseReturnItemReplicaList;
  }

  public void setPurchaseReturnItemReplicaList(List<PurchaseReturnItemReplica> purchaseReturnItemReplicaList) {
    this.purchaseReturnItemReplicaList = purchaseReturnItemReplicaList;
  }

  public CompanyBank getCompanyBank() {
    return companyBank;
  }

  public void setCompanyBank(CompanyBank companyBank) {
    this.companyBank = companyBank;
  }

  public PdfPTable getHeaderTable() {
    return headerTable;
  }

  public void setHeaderTable(PdfPTable headerTable) {
    this.headerTable = headerTable;
  }

  public List<VendorLicense> getVendorLicenseList() {
    return vendorLicenseList;
  }

  public void setVendorLicenseList(List<VendorLicense> vendorLicenseList) {
    this.vendorLicenseList = vendorLicenseList;
  }

}
