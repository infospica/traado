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
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorLicense;
import spica.scm.print.CellProperty;
import spica.scm.print.PrintPurchaseReturn;
import spica.scm.print.ItemTable;
import spica.scm.print.PageFooterEvent;
import spica.scm.service.CompanyAddressService;
import spica.scm.service.CompanyLicenseService;
import spica.scm.service.PurchaseReturnItemService;
import spica.scm.service.VendorAddressService;
import spica.scm.service.VendorLicenseService;
import spica.scm.service.VendorService;
import spica.scm.tax.TaxCalculator;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import static spica.sys.SystemRuntimeConfig.SDF_MMM_YY;
import spica.sys.UserRuntimeView;
import wawo.app.faces.AppView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
@Named(value = "purchaseReturnPrintView")
@ViewScoped
public class PurchaseReturnPrintView implements Serializable {

  private PurchaseReturn purchaseReturn;
  private float columnWidth[];

  @PostConstruct
  public void init() {
    purchaseReturn = (PurchaseReturn) Jsf.popupParentValue(PurchaseReturn.class);
  }

  public void printIText(MainView main) {
    try {
      CompanySettings companySettings = UserRuntimeView.instance().getCompany().getCompanySettings();
      CompanyAddress companyAddress = CompanyAddressService.selectCompanyRegisteredAddress(main, getPurchaseReturn().getCompanyId());
      VendorAddress vendorAddress = VendorAddressService.selectDefaultVendorAddress(main, getPurchaseReturn().getAccountId().getVendorId());
      List<CompanyLicense> companyLicenseList = CompanyLicenseService.listActiveLicenseByCompany(main, getPurchaseReturn().getCompanyId(), SystemConstants.ACTIVE_LICENSE);
      Vendor vendor = VendorService.selectByPk(main, new Vendor(vendorAddress.getVendorId().getId()));
      List<VendorLicense> vendorLicenseList = VendorLicenseService.licenseListByVendor(main, vendor);
      getPurchaseReturn().setDecimalPrecision(UserRuntimeView.instance().getAccount().getSupplierDecimalPrecision() == null ? 2 : UserRuntimeView.instance().getAccount().getSupplierDecimalPrecision());
      TaxCalculator taxCalculator = SystemRuntimeConfig.getTaxCalculator(getPurchaseReturn().getTaxProcessorId().getProcessorClass());
      List<PurchaseReturnItemReplica> replicaList = PurchaseReturnItemService.listPurchaseReturnItemByPurchaseReturn(main, getPurchaseReturn());
      taxCalculator.processPurchaseReturnCalculation(getPurchaseReturn(), replicaList);
      PrintPurchaseReturn printPurchaseReturn = new PrintPurchaseReturn();
      printPurchaseReturn.setCompanyAddress(companyAddress);
      printPurchaseReturn.setVendorAddress(vendorAddress);
      printPurchaseReturn.setCompanyLicenseList(companyLicenseList);
      printPurchaseReturn.setCompanySettings(companySettings);
      printPurchaseReturn.setVendor(vendor);
      printPurchaseReturn.setPurchaseReturnItemReplicaList(replicaList);
      printPurchaseReturn.setVendorLicenseList(vendorLicenseList);
      generatePrint(printPurchaseReturn);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    }
  }

  private void generatePrint(PrintPurchaseReturn printPurchaseReturn) throws Exception {
    Document document = null;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
    String fileName = getPurchaseReturn().getInvoiceNo() + ".pdf";
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
    PdfWriter pdfWriter;
    Rectangle pageSize = null;
    if (getPurchaseReturn().getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) {
      pageSize = new Rectangle(595, 842);
    } else {
      pageSize = new Rectangle(842, 595);
    }
    document = new Document(pageSize, 20, 20, 20, 20);
    pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
    pdfWriter.setPageEvent(new PageFooterEvent(pageSize.getWidth()));
    document.open();

    document.add(printPurchaseReturn.initiatePrint(getPurchaseReturn(), document, generateItemTable(printPurchaseReturn.getPurchaseReturnItemReplicaList()), pdfWriter)); // creating header & product table
    printPurchaseReturn.createFooterBar(document, pdfWriter, columnWidth); // For generating footer and vertical lines 
    document.close();
  }

  private ItemTable generateItemTable(List<PurchaseReturnItemReplica> purchaseReturnItemReplicaList) {
    ItemTable table = new ItemTable();
    List<CellProperty> properties = new LinkedList<>();
    List<CellProperty> dataList = new LinkedList<>();
    table.setGstFlag(true);
    AppView appView = new AppView();
    boolean intrastate = purchaseReturn.getIsReturnInterstate() == 1 ? false : true;
    table.setPortrait(getPurchaseReturn().getPrintType().equals(SystemConstants.PRINT_PORTRAIT));
    properties.add(new CellProperty("Sl", 3f, false, true, true));
    properties.add(new CellProperty("Hsn Code", 6f, true, true, true));
    properties.add(new CellProperty("Product", 16.5f, false, true, true));
    properties.add(new CellProperty("Batch", 8.5f, false, true, true));
    properties.add(new CellProperty("Exp. Date", 5f, true, true, true));
    properties.add(new CellProperty("Tax %", 4f, true, true, true));
    if (!getPurchaseReturn().getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) {
      properties.add(new CellProperty("Reference No.", 8.5f, false, true, true));
      properties.add(new CellProperty("Reference Date", 6f, true, true, true));
    }
    properties.add(new CellProperty("Return Qty", 5f, true, true, true));
    properties.add(new CellProperty("S.Disc %", 5f, false, true, true));
    properties.add(new CellProperty("P.Disc %", 4f, true, true, true));
    properties.add(new CellProperty("Rate", 5f, false, true, true));
    if (intrastate && purchaseReturn.getSezZone().intValue() == 0) {
      properties.add(new CellProperty("CGST", 5f, false, true, true));
      properties.add(new CellProperty("SGST", 5f, false, true, true));
    } else {
      properties.add(new CellProperty("IGST", 5f, false, true, true));
    }
    properties.add(new CellProperty("MRP", 6f, false, true, true));
    properties.add(new CellProperty("Goods Value", 6f, true, true, true));
    properties.add(new CellProperty("Net Amount", 6f, false, true, true));
    table.setCellProperties(properties);
    int i = 0;
    SimpleDateFormat dateRefformat = new SimpleDateFormat("dd-mm-yyyy");
    for (PurchaseReturnItemReplica item : purchaseReturnItemReplicaList) {
      i += 1;
      dataList.add(new CellProperty(String.valueOf(i), SystemConstants.LEFT));
      dataList.add(new CellProperty(String.valueOf(item.getProductDetail().getProductPresetId().getProductId().getHsnCode()), SystemConstants.LEFT));
      dataList.add(new CellProperty(String.valueOf(item.getProductDetail().getProductBatchId().getProductId().getProductName()), SystemConstants.LEFT));
      dataList.add(new CellProperty(String.valueOf(item.getProductDetail().getProductBatchId().getBatchNo()), SystemConstants.LEFT));
      dataList.add(new CellProperty(String.valueOf(SDF_MMM_YY.format(item.getProductDetail().getProductBatchId().getExpiryDateActual())), SystemConstants.LEFT));
      dataList.add(new CellProperty(String.valueOf(item.getTaxCode().getRatePercentage()), SystemConstants.RIGHT));
      if (!getPurchaseReturn().getPrintType().equals(SystemConstants.PRINT_PORTRAIT)) {
        dataList.add(new CellProperty(String.valueOf(item.getReferenceNo() == null ? "" : item.getReferenceNo()), SystemConstants.LEFT));
        dataList.add(new CellProperty(String.valueOf(item.getReferenceDate() == null ? "" : dateRefformat.format(item.getReferenceDate())), SystemConstants.LEFT));
      }
      dataList.add(new CellProperty(String.valueOf(item.getQuantityReturned()), SystemConstants.RIGHT));
      dataList.add(new CellProperty(String.valueOf(item.getSchemeDiscountPercentage() == null ? "" : appView.decimal(item.getSchemeDiscountPercentage())), SystemConstants.RIGHT));
      dataList.add(new CellProperty(String.valueOf(item.getProductDiscountPerc() == null ? "" : item.getProductDiscountPerc()), SystemConstants.RIGHT));
      dataList.add(new CellProperty(String.valueOf(appView.decimal(item.getValueRate())), SystemConstants.RIGHT));
      if (intrastate && purchaseReturn.getSezZone().intValue() == 0) {
        dataList.add(new CellProperty(String.valueOf(appView.decimal(item.getValueCgst())), SystemConstants.RIGHT));
        dataList.add(new CellProperty(String.valueOf(appView.decimal(item.getValueSgst())), SystemConstants.RIGHT));
      } else {
        dataList.add(new CellProperty(String.valueOf(appView.decimal(item.getValueIgst())), SystemConstants.RIGHT));
      }
      dataList.add(new CellProperty(String.valueOf(appView.decimal(item.getValueMrp())), SystemConstants.RIGHT));
      dataList.add(new CellProperty(String.valueOf(appView.decimal(item.getValueGoods())), SystemConstants.RIGHT));
      dataList.add(new CellProperty(String.valueOf(appView.decimal(item.getValueNet())), SystemConstants.RIGHT));
    }
    table.setData(dataList);
    columnWidth = table.getColumnWidth();
    return table;
  }

  public PurchaseReturn getPurchaseReturn() {
    return purchaseReturn;
  }
}
