/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.tax;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.domain.TaxCode;
import spica.fin.domain.VendorClaim;
import spica.fin.domain.VendorClaimDetail;
import spica.scm.common.ProductDetailSales;
import spica.scm.common.ProductSummary;
import spica.scm.common.ReturnItem;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Product;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.PurchaseOrder;
import spica.scm.domain.PurchaseOrderItem;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesServicesInvoice;
import spica.scm.domain.SalesServicesInvoiceItem;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import wawo.app.Main;

/**
 *
 * @author java-2
 */
public abstract class TaxCalculator implements Serializable {

  /* Path of view  of purchase and sales */
  public String getPurchaseOrderView() {
    return "/purchase/purchase_order.xhtml";
  }

  public String getSalesInvoiceView() {
    return "/sales/gst_india/sales_invoice_gst_india.xhtml";
  }

  public String getPurchaseReturnView() {
    return "/purchase_return/purchase_return.xhtml";
  }

  public String getProductEntryView() {
    return "/product_entry/product_entry.xhtml";
  }

  public String getSalesReturnView() {
    return "/sales/sales_return.xhtml";
  }

  public String getSalesReturnReceiptView() {
    return "/sales/gst_india/sales_return_consignment_receipt.xhtml";
  }

  public String getSalesOrderView() {
    return "/sales/sales_order.xhtml";
  }

  public String getSalesServiceInvoiceView() {
    return "/accounting/sales_services_invoice.xhtml";
  }

  public abstract String getPurchaseOrderForm();

  public abstract String getPurchaseReturnForm();

  public abstract String getProductEntryForm();

  public abstract String getSalesOrderForm();

  public abstract String getSalesInvoiceForm();

  public abstract String getSalesReturnForm();

  public abstract String getSalesInvoicePortrait();

  public abstract String getSalesInvoiceLandscapeSingleForm();

  public abstract String getSalesInvoiceLandscapeMultipleForm();

  public abstract String getSalesInvoicePrintForm();

  public abstract String getSalesReturnPrintLandScape();

  public abstract String getSalesReturnPrintPortrait();

  public abstract String getSalesInvoicePrintIText();

  public abstract String getSalesInvoicePrintITextTypeI();

  public abstract String getSalesInvoicePrintITextTypeII();

  public abstract String getSalesInvoicePrintSample();

  public abstract String getSalesReturnPrintIText();

  public abstract String getSalesServicesInvoicePrintIText();

  public abstract String getDebitCreditNotePrint();

  public abstract String getDebitCreditNotePrintIText();

  public abstract String getPurchaseReturnPrintIText();

  public abstract String getManageGallery();

  public abstract void processProductEntryCalculation(ProductEntry productEntry, List<ProductEntryDetail> productEntryDetailList, List<ProductEntryDetail> serviceEntryDetailList);

  public abstract void processPurchaseOrderCalculation(PurchaseOrder puchaseOrder, List<PurchaseOrderItem> purchaseOrderItemList);

  public abstract void processPurchaseReturnCalculation(PurchaseReturn puchaseReturn, List<PurchaseReturnItemReplica> purchaseReturnItemList);

  public abstract void processSalesInvoiceCalculation(SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItemList, boolean draftView);

  public abstract void processSalesReturnCalculation(SalesReturn salesReturn, List<SalesReturnItem> salesReturnItemList, boolean resetDiscounts);

  public abstract void processSalesReturnProductEntryCalculation(ProductEntry productEntry, List<ProductEntryDetail> productEntryDetailList);

  public abstract void processDebitNoteCalculation(DebitCreditNote debitCreditNote, List<DebitCreditNoteItem> debitCreditNoteItemList);

  public abstract void processCreditNoteCalculation(DebitCreditNote debitCreditNote, List<DebitCreditNoteItem> debitCreditNoteItemList);

  public abstract void productMarginCalculation(Main main, ProductEntry productEntry, List<ProductEntryDetail> productEntryDetailList);

  public abstract List<ProductSummary> productSummaryAuto(Main main, Account account, String filter, Integer productEntryId, Integer productDetailId);

  public abstract List<ProductSummary> productSummaryForSalesReturnAuto(Main main, SalesReturn salesReturn, SalesReturnItem salesReturnItem);

  public abstract List<ProductSummary> productSummaryForPurchaseOrder(Main main, Account account, String filter, Integer parentId, Integer childId);

  public abstract List<Product> selectProductForSales(Main main, SalesInvoice salseInvoice, String filter);

  public abstract List<ProductDetailSales> selectProductDetailForSales(Main main, SalesInvoice salseInvoice, SalesInvoiceItem salesInvoiceItem);

  public abstract void updateProductEntryDetail(Main main, Account account, ProductEntryDetail productEntryDetail);

  public abstract void updateSalesReturnItem(Main main, SalesReturnItem salesReturnItem);

  public abstract void updatePurchaseOrderItem(Main main, Account account, PurchaseOrderItem purchaseOrderItem);

  public abstract void updateSalesInvoiceItem(Main main, SalesInvoiceItem salesInvoiceItem);

  public abstract List<Product> selectProductForSalesReturn(Main main, ProductEntry salesReturn, String filter);

  public abstract void savePurchase(Main main, ProductEntry productEntry);

  public abstract void saveSales(Main main, SalesInvoice salseInvoice);

  public abstract void savePurchaseReturn(Main main, PurchaseReturn purchaseReturn);

  public abstract void saveSalesReturn(Main main, SalesReturn salesReturn);

  public abstract void saveSalesServicesInvoice(Main main, SalesServicesInvoice salesServicesInvoice);

  public abstract void saveDebitCreditNote(Main main, DebitCreditNote debitCreditNote);

  public abstract List<ProductDetailSales> getProductDetailCurrentStockAvailability(Main main, SalesInvoiceItem salesInvoiceItem, AccountGroup accountGroup, Product product, AccountGroupPriceList accountGroupPriceList);

  public abstract ProductDetailSales selectProductDetailSalesForConfirm(Main main, SalesInvoiceItem salesInvoiceItem);

  public abstract List<ProductDetailSales> selectCurrentSalesItemsFromStock(Main main, SalesInvoice salesInvoice, boolean isProforma);

  public abstract List<ReturnItem> getNonMovingProductsForPurchaseReturn(Main main, Product productId, Integer purchaseReturnId, Integer accountId, String productType, Integer purchaseReturnType, Date entryDate);

  public abstract List<ReturnItem> getDamagedAndExpiredProductsForPurchaseReturn(Main main, Product productId, Integer purchaseReturnId, Integer accountId, String productType, Integer purchaseReturnType, Date entryDate);

  public abstract void processSalesServicesInvoiceCalculation(SalesServicesInvoice salesServicesInvoice, List<SalesServicesInvoiceItem> salesServicesInvoiceItemList);

  public abstract void saveVendorClaim(Main main, VendorClaim vendorClaim, List<VendorClaimDetail> claimDetailList);

  public abstract List<ProductSummary> productSummaryBatchAuto(Main main, Account account, String filter, Integer productEntryId, Integer productDetailId, Integer productId);

  public abstract void calculateTcsonSales(SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItemList, Double customerNetAmount, TaxCode taxCode);

  public abstract List<ProductDetailSales> selectProductDetailSalesListForConfirm(Main main, SalesInvoice salesInvoice);

  public abstract List<ProductDetailSales> getProductDetailCurrentStockAvailabilityforSave(Main main, SalesInvoice salesInvoice);

}
