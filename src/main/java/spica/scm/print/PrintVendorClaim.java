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
import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import spica.constant.ReportConstant;
import spica.fin.domain.VendorClaim;
import spica.fin.domain.VendorClaimDetail;
import spica.fin.service.VendorClaimService;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorLicense;
import spica.scm.print.Impl.PageFooterImpl;
import spica.scm.print.Impl.PageHeaderImpl;
import spica.scm.print.Impl.PageProductImpl;
import spica.scm.service.CompanyAddressService;
import spica.scm.service.CompanyLicenseService;
import spica.scm.service.CompanySettingsService;
import spica.scm.service.VendorAddressService;
import spica.scm.service.VendorLicenseService;
import spica.sys.ClaimConstants;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.app.faces.AppView;

/**
 *
 * @author sujesh
 */
public class PrintVendorClaim extends PdfUtil {

//  private CompanyAddress companyAddress;
//  private Vendor vendor;
//  private VendorAddress vendorAddress;
  private VendorClaim vendorClaim;

//  private List<CompanyLicense> companyLicenseList;
//  private List<VendorLicense> vendorLicenseList;
//  private List<VendorClaimDetail> vendorClaimDetailList;
  private String printType;
  private boolean firstRow;
  private boolean iterator;
  private boolean billTop;
  private boolean intraStateSales;
  private transient float headerHeight;
  private float[] columns;
  float pHeight, fHeight, sumTableHeight;
  private float totalWidth;
  private PdfPTable dataTable;
  private Double totalExpense;

  public PdfPTable initiatePrint(Main main, VendorClaim vendorClaim, String commissionType) {
    this.printType = printType;
    this.vendorClaim = vendorClaim;
    CompanySettingsService.selectIfNull(main, getVendorClaim().getCompanyId());
    dataTable = new PdfPTable(1);
    dataTable.setTotalWidth(pageWidth);
    dataTable.setLockedWidth(true);
    totalWidth = pageWidth;
    createPageHeader(main);
    if (commissionType.equals(ReportConstant.COMMISSION_CLAIM)) {
      setSupplierCommission();
    } else if (commissionType.equals(ReportConstant.SALES_COMMISSION)) {
      setSalesCommission(main);
    }
    createDataTable();
    return dataTable;
  }

  private void createPageHeader(Main main) {
    PdfPCell headerCell = new PdfPCell(getHeader(main));
    headerCell.setBorder(0);
    headerCell.setPadding(0);
    dataTable.addCell(headerCell);
    dataTable.setSplitLate(false);

  }

  private PdfPTable getHeader(Main main) {
    PdfPTable table = new PdfPTable(1);
    //  try {
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    setFirstRow(true);
    setIterator(true);
    table.setTotalWidth(pageWidth);
    do {
      tableMap = new LinkedHashMap<>();
      tableMap = createHeaderTables(main, isFirstRow(), table);
      PdfPTable headerTable = new PdfPTable(getCellWidth(tableMap.keySet().toArray(new Float[0])));
      headerTable.getDefaultCell().setBorder(0);
      for (Map.Entry<Float, PdfPTable> entry : tableMap.entrySet()) {
        headerTable.addCell(entry.getValue());
      }
      table.addCell(headerTable);
    } while (isIterator());

//    } catch (Exception e) {
//      Logger.getLogger(PrintSalesInvoice.class
//              .getName()).log(Level.SEVERE, null, e);
//    }
    headerHeight = table.getTotalHeight();
    return table;
  }

  public Map<Float, PdfPTable> createHeaderTables(Main main, boolean firstRow, PdfPTable table) {

    CompanyAddress companyAddress = CompanyAddressService.selectCompanyRegisteredAddress(main, getVendorClaim().getCompanyId());
    VendorAddress vendorAddress = VendorAddressService.selectDefaultVendorAddress(main, getVendorClaim().getVendorId());
    List<CompanyLicense> companyLicenseList = CompanyLicenseService.licenseListByCompany(main, getVendorClaim().getCompanyId());
    List<VendorLicense> vendorLicenseList = VendorLicenseService.licenseListByVendor(main, getVendorClaim().getVendorId());

//      print.setCompanyAddress(companyAddress);
//      print.setCompanyLicenseList(licenseList);
//      print.setVendor(getVendorClaim().getVendorId());
//      print.setVendorAddress(vAddress);
//      print.setVendorLicenseList(vendorLicenseList);
//      print.setVendorClaimDetailList(detailList);
    Map<Float, PdfPTable> tableMap = new LinkedHashMap<>();
    PageHeader pageHeader = new PageHeaderImpl();
    if (isFirstRow()) {
      tableMap.put(40f, pageHeader.setCompanyAddress(companyAddress, getVendorClaim().getCompanyId(), getVendorClaim().getCompanyId().getCompanySettings().getFileContent()));
      tableMap.put(32f, pageHeader.setCompanyGST(getVendorClaim().getCompanyId(), companyLicenseList));
      tableMap.put(28f, new PdfPTable(new float[1]));
      setFirstRow(false);
      setIterator(true);
    } else {
      table.addCell(pageHeader.setGStInvoiceLabel(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? ReportConstant.COMMISSION_CLAIM : ReportConstant.COMMISSION_EXPENSE));
      tableMap.put(30f, pageHeader.setVendorAddress(getVendorClaim().getVendorId(), vendorAddress));
      tableMap.put(24f, pageHeader.setVendorGST(getVendorClaim().getVendorId(), vendorLicenseList, SystemConstants.PRINT_MULTIPLE_LINE));
      tableMap.put(26f, new PdfPTable(new float[1]));
      tableMap.put(20f, new PdfPTable(new float[1]));
      setIterator(false);
    }
    return tableMap;
  }

  private void setSupplierCommission() {

  }

  private void setSalesCommission(Main main) {
    //  PageProduct pageProduct = new PageProductImpl();
    PdfPCell productCell = new PdfPCell(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? createDataTable() : createExpenseTable(main));
    productCell.setBorder(0);
    productCell.setPadding(0);
    dataTable.addCell(productCell);
    dataTable.setSplitLate(false);

  }

  private PdfPTable createDataTable() {

    PdfPTable productTable;
    float horizontal = 0;
    columns = new float[]{14, 8, 8, 10, 10, 10, 10, 10, 10, 10};
    productTable = new PdfPTable(columns);
    productTable.getDefaultCell().setBorderWidthBottom(0);
    productTable.setTotalWidth(pageWidth);
    productTable.addCell(new PdfPCell(new Phrase("Description of Services", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("HSN", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Taxable Value", fontProductHead())));

    PdfPTable cgstTable = new PdfPTable(1);
    cgstTable.getDefaultCell().setPadding(0);
    cgstTable.addCell(new PdfPCell(new Phrase("CGST", fontProductHead())));
    PdfPTable cgst = new PdfPTable(2);
    cgst.addCell(new PdfPCell(new Phrase("Rate", fontProductHead())));
    cgst.addCell(new PdfPCell(new Phrase("Amount", fontProductHead())));
    cgst.setPaddingTop(0);
    cgstTable.addCell(cgst);
    PdfPCell cgstCell = new PdfPCell(cgstTable);
    cgstCell.setColspan(2);
    productTable.addCell(cgstCell);

    PdfPTable sgstTable = new PdfPTable(1);
    sgstTable.getDefaultCell().setPadding(0);
    sgstTable.addCell(new PdfPCell(new Phrase("SGST", fontProductHead())));
    PdfPTable sgst = new PdfPTable(2);
    sgst.addCell(new PdfPCell(new Phrase("Rate", fontProductHead())));
    sgst.addCell(new PdfPCell(new Phrase("Amount", fontProductHead())));
    sgst.setPaddingTop(0);
    sgstTable.addCell(cgst);
    PdfPCell sgstCell = new PdfPCell(sgstTable);
    sgstCell.setColspan(2);
    productTable.addCell(sgstCell);

    PdfPTable igstTable = new PdfPTable(1);
    igstTable.getDefaultCell().setPadding(0);
    igstTable.addCell(new PdfPCell(new Phrase("IGST", fontProductHead())));
    PdfPTable igst = new PdfPTable(2);
    igst.addCell(new PdfPCell(new Phrase("Rate", fontProductHead())));
    igst.addCell(new PdfPCell(new Phrase("Amount", fontProductHead())));
    igst.setPaddingTop(0);
    igstTable.addCell(igst);
    PdfPCell igstCell = new PdfPCell(igstTable);
    igstCell.setColspan(2);
    productTable.addCell(igstCell);
    productTable.addCell(new PdfPCell(new Phrase("Total", fontProductHead())));

    String monthName = new DateFormatSymbols().getMonths()[getVendorClaim().getClaimMonth() - 1];
    String desc = "Sales commission on " + monthName + " " + getVendorClaim().getClaimYear();
    PdfPCell cell = new PdfPCell(new Phrase(desc, fontProduct()));
    cell.setBorderWidthBottom(horizontal);
    cell.setBorderWidthTop(0);
    cell.setBorderWidthRight(0);
    cell.setBorderWidthLeft(0);
    productTable.addCell(cell);

    cell = new PdfPCell(new Phrase(getVendorClaim().getService().getHsnSacCode(), fontProduct()));
    cell.setBorderWidthBottom(horizontal);
    cell.setBorderWidthTop(0);
    cell.setBorderWidthRight(0);
    cell.setBorderWidthLeft(0);
    productTable.addCell(cell);

    cell = new PdfPCell(new Phrase(getVendorClaim().getCommissionAmount().toString(), fontProduct()));
    cell.setBorderWidthBottom(horizontal);
    cell.setBorderWidthTop(0);
    cell.setBorderWidthRight(0);
    cell.setBorderWidthLeft(0);
    productTable.addCell(cell);

    cell = new PdfPCell(new Phrase(getVendorClaim().getCgstTaxCode() != null ? getVendorClaim().getCgstTaxCode().getRatePercentage().toString() + "%" : "", fontProduct()));
    cell.setBorderWidthBottom(horizontal);
    cell.setBorderWidthTop(0);
    cell.setBorderWidthRight(0);
    cell.setBorderWidthLeft(0);
    productTable.addCell(cell);

    cell = new PdfPCell(new Phrase(getVendorClaim().getTaxValueCgst() == null ? "" : getVendorClaim().getTaxValueCgst().toString(), fontProduct()));
    cell.setBorderWidthBottom(horizontal);
    cell.setBorderWidthTop(0);
    cell.setBorderWidthRight(0);
    cell.setBorderWidthLeft(0);
    productTable.addCell(cell);

    cell = new PdfPCell(new Phrase(getVendorClaim().getSgstTaxCode() != null ? getVendorClaim().getSgstTaxCode().getRatePercentage().toString() + "%" : "", fontProduct()));
    cell.setBorderWidthBottom(horizontal);
    cell.setBorderWidthTop(0);
    cell.setBorderWidthRight(0);
    cell.setBorderWidthLeft(0);
    productTable.addCell(cell);

    cell = new PdfPCell(new Phrase(getVendorClaim().getTaxValueSgst() == null ? "" : getVendorClaim().getTaxValueSgst().toString(), fontProduct()));
    cell.setBorderWidthBottom(horizontal);
    cell.setBorderWidthTop(0);
    cell.setBorderWidthRight(0);
    cell.setBorderWidthLeft(0);
    productTable.addCell(cell);

    cell = new PdfPCell(new Phrase(getVendorClaim().getIgstTaxCode() != null ? getVendorClaim().getIgstTaxCode().getRatePercentage().toString() + "%" : "", fontProduct()));
    cell.setBorderWidthBottom(horizontal);
    cell.setBorderWidthTop(0);
    cell.setBorderWidthRight(0);
    cell.setBorderWidthLeft(0);
    productTable.addCell(cell);

    cell = new PdfPCell(new Phrase(getVendorClaim().getTaxValueIgst() == null ? "" : getVendorClaim().getTaxValueIgst().toString(), fontProduct()));
    cell.setBorderWidthBottom(horizontal);
    cell.setBorderWidthTop(0);
    cell.setBorderWidthRight(0);
    cell.setBorderWidthLeft(0);
    productTable.addCell(cell);

    if (getVendorClaim().getCommissionClaim() != null) {
      cell = new PdfPCell(new Phrase(getVendorClaim().getCommissionClaim().toString(), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);
    }
    return productTable;
  }

  private PdfPTable createExpenseTable(Main main) {
    List<VendorClaimDetail> vendorClaimDetailList = VendorClaimService.selectVendorClaimDetailByClaim(main, getVendorClaim());

    float horizontal = 0;
    columns = new float[]{14, 8, 8, 10, 10, 10, 10, 10, 10, 10};
    PdfPTable productTable = new PdfPTable(columns);
    productTable = new PdfPTable(columns);
    productTable.getDefaultCell().setBorderWidthBottom(0);
    productTable.setTotalWidth(pageWidth);
    productTable.addCell(new PdfPCell(new Phrase("Description of Expenses", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("HSN", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Taxable Value", fontProductHead())));

    PdfPTable cgstTable = new PdfPTable(1);
    cgstTable.getDefaultCell().setPadding(0);
    cgstTable.addCell(new PdfPCell(new Phrase("CGST", fontProductHead())));
    PdfPTable cgst = new PdfPTable(2);
    cgst.addCell(new PdfPCell(new Phrase("Rate", fontProductHead())));
    cgst.addCell(new PdfPCell(new Phrase("Amount", fontProductHead())));
    cgst.setPaddingTop(0);
    cgstTable.addCell(cgst);
    PdfPCell cgstCell = new PdfPCell(cgstTable);
    cgstCell.setColspan(2);
    productTable.addCell(cgstCell);

    PdfPTable sgstTable = new PdfPTable(1);
    sgstTable.getDefaultCell().setPadding(0);
    sgstTable.addCell(new PdfPCell(new Phrase("SGST", fontProductHead())));
    PdfPTable sgst = new PdfPTable(2);
    sgst.addCell(new PdfPCell(new Phrase("Rate", fontProductHead())));
    sgst.addCell(new PdfPCell(new Phrase("Amount", fontProductHead())));
    sgst.setPaddingTop(0);
    sgstTable.addCell(cgst);
    PdfPCell sgstCell = new PdfPCell(sgstTable);
    sgstCell.setColspan(2);
    productTable.addCell(sgstCell);

    PdfPTable igstTable = new PdfPTable(1);
    igstTable.getDefaultCell().setPadding(0);
    igstTable.addCell(new PdfPCell(new Phrase("IGST", fontProductHead())));
    PdfPTable igst = new PdfPTable(2);
    igst.addCell(new PdfPCell(new Phrase("Rate", fontProductHead())));
    igst.addCell(new PdfPCell(new Phrase("Amount", fontProductHead())));
    igst.setPaddingTop(0);
    igstTable.addCell(igst);
    PdfPCell igstCell = new PdfPCell(igstTable);
    igstCell.setColspan(2);
    productTable.addCell(igstCell);
    productTable.addCell(new PdfPCell(new Phrase("Total", fontProductHead())));
    totalExpense = 0.0;
    for (VendorClaimDetail detail : vendorClaimDetailList) {
      if (detail.getClaimType().intValue() == ClaimConstants.CLAIM_EXPENSES) {
        PdfPCell cell = new PdfPCell(new Phrase(detail.getAccountingLedgerId() != null ? detail.getAccountingLedgerId().getTitle() : "", fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);

        cell = new PdfPCell(new Phrase("", fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);

        cell = new PdfPCell(new Phrase(detail.getTaxableAmount() != null ? detail.getTaxableAmount().toString() : "", fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);

        cell = new PdfPCell(new Phrase("", fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);

        cell = new PdfPCell(new Phrase("", fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);

        cell = new PdfPCell(new Phrase("", fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);

        cell = new PdfPCell(new Phrase("", fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);

        cell = new PdfPCell(new Phrase("", fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);

        cell = new PdfPCell(new Phrase("", fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);

        cell = new PdfPCell(new Phrase(detail.getTaxableAmount() != null ? detail.getTaxableAmount().toString() : "", fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);
        totalExpense += detail.getTaxableAmount() == null ? 0.0 : detail.getTaxableAmount();
      }
    }

    return productTable;
  }

  public void createFooterBar(Main main, Document document, PdfWriter writer) {
    CompanyAddress companyAddress = CompanyAddressService.selectCompanyRegisteredAddress(main, getVendorClaim().getCompanyId());
    PdfPTable footer = new PdfPTable(createFooterTable(companyAddress));
    footer.setTotalWidth(totalWidth);
    footer.setLockedWidth(true);
    PageProduct pageProduct = new PageProductImpl();
    pageProduct.emptyRows(columns, dataTable.getTotalWidth(), document.getPageSize().getHeight(), headerHeight + 100, writer.getPageSize()).writeSelectedRows(0, -1, document.leftMargin(),
            document.top() - headerHeight - 15, writer.getDirectContent());
    footer.writeSelectedRows(0, -1, document.leftMargin(), footer.getTotalHeight() + document.bottomMargin(), writer.getDirectContent());
  }

  public PdfPTable createFooterTable(CompanyAddress companyAddress) {
    PdfPTable table = new PdfPTable(1);
    PageFooter footer = new PageFooterImpl();
    table.setTotalWidth(totalWidth);
    table.setLockedWidth(true);
    table.getDefaultCell().setBorder(0);
    PdfPCell cell = new PdfPCell(createTaxationTable());
    cell.setPadding(0);
    cell.setBackgroundColor(BaseColor.WHITE);
    table.addCell(cell).setBorder(0);
    cell = new PdfPCell(footer.setDeclaration(getVendorClaim().getCompanyId().getCompanySettings(), getVendorClaim().getCompanyId().getCompanyName(), companyAddress.getDistrictId().getDistrictName()));
    cell.setPadding(0);
    cell.setBackgroundColor(BaseColor.WHITE);
    table.addCell(cell).setBorder(0);
    cell = new PdfPCell(footer.setWebMail(companyAddress.getEmail(), companyAddress.getWebsite()));
    cell.setPadding(0);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setBackgroundColor(BaseColor.WHITE);
    table.addCell(cell).setBorder(0);
    fHeight = table.getTotalHeight();
    return table;
  }

  public PdfPTable createTaxationTable() {
    PageFooter footer = new PageFooterImpl();
    PdfPTable table = new PdfPTable(new float[]{70, 30});
    table.setTotalWidth(totalWidth);
    table.setLockedWidth(true);
    PdfPTable taxTable = new PdfPTable(1);
    PdfPCell cell = new PdfPCell();
    cell.setPadding(0);
    taxTable.addCell(cell);
    cell = new PdfPCell(setIntraStateTaxation());
    cell.setPadding(0);
    taxTable.addCell(cell).setBorder(0);
//    }
    cell = new PdfPCell(footer.setAmountWords(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getCommissionClaim() : totalExpense, getVendorClaim().getCompanyId().getCompanySettings().getShowManufacturer(), null, null, null, null));
    cell.setPadding(0);
    taxTable.addCell(cell).setMinimumHeight(35);
    cell = new PdfPCell(taxTable);
    cell.setPadding(0);
    table.addCell(cell);
    Map<String, String> billMap = new HashMap<>();
    billMap.put("Taxable Value", AppView.formatDecimal(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getCommissionAmount() : totalExpense, 2));
    billMap.put("GST", AppView.formatDecimal(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getGstAmount(getVendorClaim()) : null, 2));
    billMap.put("L-TDS", AppView.formatDecimal(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getTdsValue() : null, 2));
    billMap.put("NET", AppView.formatDecimal(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getCommissionClaim() : totalExpense, 2));

    cell = new PdfPCell(footer.setBillAmount(billMap, false));
    cell.setPadding(3);
    table.addCell(cell);
    return table;
  }

  private PdfPTable setIntraStateTaxation() {

    PdfPTable table = new PdfPTable(7);
    table.getDefaultCell().setBorder(0);

    PdfPCell taxableLabel = new PdfPCell(new Phrase("Taxable", fontTax()));
    PdfPCell sgstLabel = new PdfPCell(new Phrase("SGST", fontTax()));
    PdfPCell staxLabel = new PdfPCell(new Phrase("Tax", fontTax()));
    PdfPCell cgstLabel = new PdfPCell(new Phrase("CGST", fontTax()));
    PdfPCell ctaxLabel = new PdfPCell(new Phrase("Tax", fontTax()));
    PdfPCell igstLabel = new PdfPCell(new Phrase("IGST", fontTax()));
    PdfPCell igstTaxLabel = new PdfPCell(new Phrase("Tax", fontTax()));
//    PdfPCell kfLabel = new PdfPCell(new Phrase("KFCess", fontTax()));

    taxableLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    sgstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    staxLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    cgstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    ctaxLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    igstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    igstTaxLabel.setHorizontalAlignment(Element.ALIGN_CENTER);

    taxableLabel.setBorderWidthLeft(0);
    sgstLabel.setBorderWidthLeft(0);
    staxLabel.setBorderWidthLeft(0);
    cgstLabel.setBorderWidthLeft(0);
    ctaxLabel.setBorderWidthLeft(0);
    igstLabel.setBorderWidthLeft(0);
    igstLabel.setBorderWidthRight(0);
    igstTaxLabel.setBorderWidthLeft(0);

    taxableLabel.setBorderWidthTop(0);
    sgstLabel.setBorderWidthTop(0);
    staxLabel.setBorderWidthTop(0);
    cgstLabel.setBorderWidthTop(0);
    ctaxLabel.setBorderWidthTop(0);
    igstLabel.setBorderWidthTop(0);
    igstTaxLabel.setBorderWidthTop(0);

    table.addCell(taxableLabel);
    table.addCell(sgstLabel);
    table.addCell(staxLabel);
    table.addCell(cgstLabel);
    table.addCell(ctaxLabel);
    table.addCell(igstLabel);
    table.addCell(igstTaxLabel);

    // Add Tax Values
//    PdfPCell gstValue = new PdfPCell(new Phrase(String.valueOf(getVendorClaim().getIgstTaxCode() == null ? "" : getVendorClaim().getIgstTaxCode().getRatePercentage()), fontTax()));
    PdfPCell taxableValue = new PdfPCell(new Phrase(AppView.formatDecimal(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getCommissionAmount() : totalExpense, 2), fontTax()));
    PdfPCell sgstValue = new PdfPCell(new Phrase(AppView.formatDecimal(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getTaxValueSgst() : null, 2), fontTax()));
    PdfPCell staxValue = new PdfPCell(new Phrase(String.valueOf(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getSgstTaxCode() == null ? "" : getVendorClaim().getSgstTaxCode().getRatePercentage() : ""), fontTax()));
    PdfPCell cgstValue = new PdfPCell(new Phrase(AppView.formatDecimal(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getTaxValueCgst() : null, 2), fontTax()));
    PdfPCell ctaxValue = new PdfPCell(new Phrase(String.valueOf(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getCgstTaxCode() == null ? "" : getVendorClaim().getCgstTaxCode().getRatePercentage() : ""), fontTax()));
    PdfPCell igstValue = new PdfPCell(new Phrase(AppView.formatDecimal(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getTaxValueIgst() : null, 2), fontTax()));
    PdfPCell igstTaxValue = new PdfPCell(new Phrase(String.valueOf(getVendorClaim().getPrintType().equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? getVendorClaim().getIgstTaxCode() == null ? "" : getVendorClaim().getIgstTaxCode().getRatePercentage() : ""), fontTax()));
//      PdfPCell kfValue = new PdfPCell(new Phrase(AppView.formatDecimal(invgrp.get, 2), fontTax()));

    taxableValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
    sgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
    staxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
    cgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
    ctaxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
    igstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
    igstTaxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

    table.addCell(taxableValue);
    table.addCell(sgstValue);
    table.addCell(staxValue);
    table.addCell(cgstValue);
    table.addCell(ctaxValue);
    table.addCell(igstValue);
    table.addCell(igstTaxValue);

    return table;

  }

  private Double getGstAmount(VendorClaim vendorClaim) {
    double igst = getVendorClaim().getTaxValueIgst() == null ? 0.0 : getVendorClaim().getTaxValueIgst();
    double sgst = getVendorClaim().getTaxValueSgst() == null ? 0.0 : getVendorClaim().getTaxValueSgst();
    double cgst = getVendorClaim().getTaxValueCgst() == null ? 0.0 : getVendorClaim().getTaxValueCgst();
    return igst + sgst + cgst;
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

  public boolean isBillTop() {
    return billTop;
  }

  public void setBillTop(boolean billTop) {
    this.billTop = billTop;
  }

  public boolean isIntraStateSales() {
    return intraStateSales;
  }

  public void setIntraStateSales(boolean intraStateSales) {
    this.intraStateSales = intraStateSales;
  }

//  public CompanyAddress getCompanyAddress() {
//    return companyAddress;
//  }
//
//  public void setCompanyAddress(CompanyAddress companyAddress) {
//    this.companyAddress = companyAddress;
//  }
  public float getHeaderHeight() {
    return headerHeight;
  }

  public void setHeaderHeight(float headerHeight) {
    this.headerHeight = headerHeight;
  }

//
//  public void setCompanyLicenseList(List<CompanyLicense> companyLicenseList) {
//    this.companyLicenseList = companyLicenseList;
//  }
//
//  public void setVendor(Vendor vendor) {
//    this.vendor = vendor;
//  }
//
//  public void setVendorAddress(VendorAddress vendorAddress) {
//    this.vendorAddress = vendorAddress;
//  }
//
//  public void setVendorLicenseList(List<VendorLicense> vendorLicenseList) {
//    this.vendorLicenseList = vendorLicenseList;
//  }
  public VendorClaim getVendorClaim() {
    return vendorClaim;
  }

  public void setVendorClaim(VendorClaim vendorClaim) {
    this.vendorClaim = vendorClaim;
  }
//
//  public List<VendorClaimDetail> getVendorClaimDetailList() {
//    return vendorClaimDetailList;
//  }
//
//  public void setVendorClaimDetailList(List<VendorClaimDetail> vendorClaimDetailList) {
//    this.vendorClaimDetailList = vendorClaimDetailList;
//  }

}
