/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.tax;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spica.constant.AccountingConstant;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.DebitCreditNoteItem;
import spica.scm.common.InvoiceGroup;
import spica.scm.common.ProductDetailSales;
import spica.scm.common.ProductSummary;
import spica.scm.common.ReturnItem;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Product;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductPacking;
import spica.scm.domain.ProductPreset;
import spica.scm.domain.PurchaseOrder;
import spica.scm.domain.PurchaseOrderItem;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.domain.SalesServicesInvoice;
import spica.scm.domain.SalesServicesInvoiceItem;
import spica.fin.domain.TaxCode;
import spica.fin.domain.VendorClaim;
import spica.fin.domain.VendorClaimDetail;
import spica.fin.service.LedgerExternalDataService;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductPackingDetail;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.function.CessProcessor;
import spica.scm.function.SalesInvoiceProcessor;
import spica.scm.function.impl.SalesInvoiceCessProcessorImpl;
import spica.scm.function.impl.SalesInvoiceProcessorImpl;
import spica.scm.service.AccountService;
import spica.scm.service.PlatformService;
import spica.scm.service.ProductBatchService;
import spica.scm.service.ProductDetailService;
import spica.scm.service.ProductEntryDetailService;
import spica.scm.service.ProductPackingService;
import spica.scm.service.ProductPresetService;
import spica.scm.service.ProductService;
import spica.scm.service.SalesInvoiceService;
import spica.scm.service.SalesServicesInvoiceService;
import spica.scm.service.TradingVariationLogService;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.scm.util.SalesInvoiceUtil;
import spica.scm.view.AccountUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-2
 */
public class GstIndia extends TaxCalculator {

  //private final static Logger LOGGER = Logger.getLogger(GstIndia.class.getName());
  @Override
  public String getPurchaseOrderForm() {
    return "/scm/purchase/gst_india/purchase_order_gst_india.xhtml";
  }

  @Override
  public String getProductEntryForm() {
    return "/scm/product_entry/gst_india/product_entry_gst_india.xhtml";
  }

  @Override
  public String getSalesInvoiceForm() {
    return "/scm/sales/gst_india/sales_invoice_gst_india.xhtml";
  }

  @Override
  public String getPurchaseReturnForm() {
    return "/scm/purchase_return/purchase_return_gst_india.xhtml";
  }

  @Override
  public String getSalesOrderForm() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getSalesReturnForm() {
    return "/scm/sales/gst_india/sales_return_gst_india.xhtml";
  }

  @Override
  public String getSalesInvoicePortrait() {
    return "/sales/print_invoice_portrait";
  }

  @Override
  public String getSalesInvoiceLandscapeSingleForm() {
    return "/sales/print_invoice_landscape_single.xhtml";
  }

  @Override
  public String getSalesInvoiceLandscapeMultipleForm() {
    return "/sales/print_invoice_landscape_multiple.xhtml";
  }

  @Override
  public String getSalesInvoicePrintForm() {
    return "/sales/print_sales_invoice.xhtml";
  }

  @Override
  public String getSalesReturnPrintLandScape() {
    return "/sales/print_sales_return.xhtml";
  }

  @Override
  public String getSalesReturnPrintPortrait() {
    return "/sales/print_sales_return_portrait.xhtml";
  }

  @Override
  public String getSalesReturnPrintIText() {
    return "/sales/print_sales_return_itext.xhtml";
  }

  @Override
  public String getSalesInvoicePrintIText() {
    return "/sales/print_sales_invoice_itext.xhtml";
  }

  @Override
  public String getSalesInvoicePrintITextTypeI() {
    return "/sales/print_sales_invoice_itext_type_I.xhtml";
  }

  @Override
  public String getSalesInvoicePrintITextTypeII() {
    return "/sales/print_sales_invoice_itext_type_II.xhtml";
  }

  @Override
  public String getSalesInvoicePrintSample() {
    return "/sales/print_sales_invoice_sample.xhtml";
  }

  @Override
  public String getDebitCreditNotePrint() {
    return "/sales/print_debit_credit_note.xhtml";
  }

  @Override
  public String getDebitCreditNotePrintIText() {
    return "/sales/print_debit_credit_note_itext.xhtml";
  }

  @Override
  public String getSalesServicesInvoicePrintIText() {
    return "/accounting/print_sales_services_invoice_itext.xhtml";
  }

  @Override
  public String getPurchaseReturnPrintIText() {
    return "/purchase_return/print_purchase_return.xhtml";
  }

  @Override
  public String getManageGallery() {
    return "/addon/gallery.xhtml";
  }

  /**
   *
   * @param main
   * @param account
   * @param filter
   * @param productEntryId
   * @param productDetailId
   * @return
   */
  @Override
  public List<ProductSummary> productSummaryAuto(Main main, Account account, String filter, Integer productEntryId, Integer productDetailId) {
    String condition = "";
    StringBuilder sql = new StringBuilder();
    List<ProductSummary> list;
    filter = "%" + filter + "%";
    List<Object> params = new ArrayList<>();
    params.add(account.getId());
    params.add(account.getVendorId().getId());
    params.add(account.getVendorId().getId());
    params.add(filter);
    params.add(account.getId());
    params.add(account.getId());
    params.add(account.getVendorId().getId());
    params.add(account.getVendorId().getId());
    params.add(productEntryId);

    if (productDetailId != null) {
      //condition = " and t7.id not in(select product_detail_id from scm_product_entry_detail where product_entry_id = ? and product_detail_id <> ?) ";
      condition = " AND scm_product_detail.id != ? ";
      params.add(productDetailId);
    }

    params.add(filter);

    sql.append("SELECT prod.id as product_id, "
            + "NULL as product_detail_id,NULL as product_batch_id,prod.product_name,NULL as batch,NULL as expiry_date,NULL as mrp_value, "
            + "tax_code.rate_percentage as gst_percentage, "
            + "prod_cat.purchase_tax_code_id as gst_tax_code_id,getPackDimension(prod.id) as pack_size, prod_preset.id as productPresetId "
            + "FROM scm_product as prod "
            + "INNER JOIN scm_product_preset prod_preset on prod.id = prod_preset.product_id and prod_preset.account_id = ? "
            + "INNER JOIN scm_product_category prod_cat on prod.product_category_id = prod_cat.id "
            + "INNER JOIN scm_tax_code tax_code on tax_code.id = prod_cat.purchase_tax_code_id "
            + "WHERE prod.brand_id IN( SELECT brand_id FROM  scm_supplier_brand WHERE vendor_id = ?) "
            + "AND prod.commodity_id IN(SELECT commodity_id FROM scm_vendor_commodity WHERE vendor_id = ?) "
            + "AND UPPER(prod.product_name) LIKE UPPER(?) "
            + "UNION ALL "
            + "SELECT prod.id as product_id,prod_det.id,prod_batch.id,prod.product_name,prod_batch.batch_no as batch,to_char(prod_batch.expiry_date_actual, 'Mon-yy') as expiry_date "
            + ",prod_batch.value_mrp as mrp_value,tax_code.rate_percentage as gst_percentage, "
            + "prod_cat.purchase_tax_code_id as gst_tax_code_id,getPackDimension(prod.id) as pack_size, prod_preset.id as productPresetId "
            + "FROM scm_product as prod "
            + "INNER JOIN scm_product_preset prod_preset on prod.id = prod_preset.product_id and prod_preset.account_id = ? "
            + "INNER JOIN scm_product_category prod_cat on prod.product_category_id = prod_cat.id "
            + "INNER JOIN scm_tax_code tax_code on tax_code.id = prod_cat.purchase_tax_code_id "
            + "INNER JOIN scm_product_batch as prod_batch  ON prod.id = prod_batch.product_id "
            //            + "AND prod_batch.expiry_date_actual >= now() "
            + "LEFT JOIN scm_product_detail as prod_det ON prod_batch.id = prod_det.product_batch_id  AND prod_det.account_id = ? "
            + "WHERE brand_id IN( SELECT brand_id FROM  scm_supplier_brand WHERE vendor_id = ? ) "
            + "AND prod.commodity_id IN(SELECT commodity_id FROM scm_vendor_commodity WHERE vendor_id = ?) "
            + "AND prod_batch.id NOT IN( "
            + "SELECT scm_product_detail.product_batch_id from scm_product_entry_detail,scm_product_detail WHERE "
            + " scm_product_detail.id = scm_product_entry_detail.product_detail_id "
            + " AND scm_product_entry_detail.product_entry_id = ? ").append(condition).append(" ) AND UPPER(prod.product_name) LIKE UPPER(?) ORDER BY 4,3");

    list = AppDb.getList(main.dbConnector(), ProductSummary.class, sql.toString(), params.toArray());

    return list;
  }

  /**
   *
   * @param main
   * @param account
   * @param productEntryDetail
   */
  @Override
  public void updateProductEntryDetail(Main main, Account account, ProductEntryDetail productEntryDetail) {

    double mrpLteValue = 0;
    Product product = null;
    ProductPreset productPreset = null;
    ProductDetail productDetail = null;
    ProductBatch productBatch = null;
    Integer productDetailId;
    boolean productModified = false;
    boolean productDetailModified = false;
    Integer taxcodeId = productEntryDetail.getTaxCodeId() == null ? null : productEntryDetail.getTaxCodeId().getId();

    if (productEntryDetail.getProductSummary().getProductId() != null) {
      if ((productEntryDetail.getProduct() != null) && !(productEntryDetail.getProduct().getId().equals(productEntryDetail.getProductSummary().getProductId()))) {
        productModified = true;
      }
      product = ProductService.selectByPk(main, new Product(productEntryDetail.getProductSummary().getProductId()));
      if (productEntryDetail.getCurrentTaxCode() == null) {
        productEntryDetail.setTaxCodeId(getProductPurchaseTaxCode(main, product));
        productEntryDetail.setCurrentTaxCode(productEntryDetail.getTaxCodeId());
      }

      productEntryDetail.setProduct(product);
    }
    if (productEntryDetail.getProductSummary().getProductDetailId() != null) {
      // Set Product Details
      productDetailId = productEntryDetail.getProductDetailId() == null ? null : productEntryDetail.getProductDetailId().getProductBatchId().getId();

      productDetail = ProductDetailService.selectByPk(main, new ProductDetail(productEntryDetail.getProductSummary().getProductDetailId()));

      productBatch = productDetail.getProductBatchId();

      if (productDetailId != null) {
        if (!(productDetailId.equals(productBatch.getId()))) {
          productDetailModified = true;
        } else if (productEntryDetail.getValueMrp() != null && !productEntryDetail.getValueMrp().equals(productBatch.getValueMrp())) {
          productModified = true;
        }
      }
      productEntryDetail.setProductBatchId(productBatch);
      productEntryDetail.setProductDetailId(productDetail);
      productEntryDetail.setProduct(productBatch.getProductId());
      productEntryDetail.setValueMrp(productBatch.getValueMrp());
      productEntryDetail.setBatchNo(productBatch.getBatchNo());
      productEntryDetail.setExpiryDate(productBatch.getExpiryDateActual());
      mrpLteValue = ProductUtil.getMrpLteValue(productBatch.getValueMrp(), productEntryDetail.getTaxCodeId().getRatePercentage());
      productEntryDetail.getProductSummary().setProductName(productEntryDetail.getProduct().getProductName());

    } else if (productEntryDetail.getProductSummary().getProductBatchId() != null) {

      productBatch = ProductBatchService.selectByPk(main, new ProductBatch(productEntryDetail.getProductSummary().getProductBatchId()));
      productEntryDetail.setProductBatchId(productBatch);
      productEntryDetail.setValueMrp(productBatch.getValueMrp());
      productEntryDetail.setBatchNo(productBatch.getBatchNo());
      productEntryDetail.setExpiryDate(productBatch.getExpiryDateActual());
      mrpLteValue = ProductUtil.getMrpLteValue(productBatch.getValueMrp(), productEntryDetail.getTaxCodeId().getRatePercentage());
      productEntryDetail.getProductSummary().setProductName(productEntryDetail.getProduct().getProductName());

    } else {
      productEntryDetail.setProductDetailId(null);
    }

    // Sets Product preset details
    if (product != null) {
      productPreset = ProductPresetService.selectProductPresetByProductAndAccount(main, product.getId(), account);
    }

    if (productEntryDetail.getId() != null) {
      if (taxcodeId != null && !taxcodeId.equals(productEntryDetail.getTaxCodeId().getId())) {
        productModified = true;
      } else if (productEntryDetail.getCurrentTaxCode() != productEntryDetail.getTaxCodeId()) {
        productModified = true;
      }
      if (productModified || productDetailModified) {
        updateProductValues(mrpLteValue, productBatch, productPreset, productEntryDetail);
        if (productDetailModified) {
          productEntryDetail.setValueRate(null);
          productEntryDetail.setValueNet(null);
          productEntryDetail.setProductQuantity(null);
          productEntryDetail.setProductQuantityFree(null);
          productEntryDetail.setProductFreeQtySchemeId(null);
          productEntryDetail.setProductQuantityDamaged(null);
          productEntryDetail.setProductQuantityExcess(null);
          productEntryDetail.setProductQuantityShortage(null);
          productEntryDetail.setExcessQuantityDescription(null);
          productEntryDetail.setExcessQuantityNote(null);
          productEntryDetail.setSchemeDiscountPercentage(null);
          productEntryDetail.setSchemeDiscountValue(null);
          productEntryDetail.setSchemeDiscountValueDerived(null);
        }
      } else if (mrpLteValue > 0 && productEntryDetail.getId() == null) {
        calculateProductPtsValue(account, productEntryDetail, productPreset);
      }
    }
    if (productPreset != null && productEntryDetail.getMarginPercentage() == null) {
      updateProductValues(mrpLteValue, productBatch, productPreset, productEntryDetail);
    }
    if (product != null) {
      productEntryDetail.setPackingDescription(product.getPackingDescription());
    }
    productEntryDetail.getProductSummary().setProductName(productEntryDetail.getProduct().getProductName());
    productEntryDetail.getProductSummary().after();
  }

  private void updateProductValues(double mrpLteValue, ProductBatch productBatch, ProductPreset productPreset, ProductEntryDetail productEntryDetail) {
    if (productBatch != null) {
      setProductPresetToProductEntryDetail(productBatch, productPreset, productEntryDetail);
      productEntryDetail.getProductSummary().setProductPresetId(productPreset.getId());

      if (mrpLteValue > 0) {
        calculateProductPtsValue(productPreset.getAccountId(), productEntryDetail, productPreset);
      }
    }
  }

  private void calculateProductPtsValue(Account account, ProductEntryDetail productEntryDetail, ProductPreset productPreset) {
    double ptsValue;
    int decimalPrecision = account.getSupplierDecimalPrecision() == null ? 2 : account.getSupplierDecimalPrecision();
    if (account.getPtsDerivationCriteria().equals(AccountService.DERIVE_PTS_FROM_MRP_PTR)) {
      double mrpLteValue = ProductUtil.getMrpLteValue(productEntryDetail.getValueMrp(), productEntryDetail.getTaxCodeId().getRatePercentage());
      double ptrValue = ProductUtil.getPtrValue(productPreset.getPtrMarginPercentage(), mrpLteValue, productEntryDetail.getMrpltePtrRateDerivationCriterion());
      ptsValue = ProductUtil.getPtsValue(productPreset.getPtsMarginPercentage(), ptrValue, productEntryDetail.getPtrPtsRateDerivationCriterion());
      double ssValue = ProductUtil.getSsValue(productPreset.getMarginPercentage(), ptsValue, productEntryDetail.getPtsSsRateDerivationCriterion());
      productEntryDetail.setValuePtr(MathUtil.roundOff(ptrValue, decimalPrecision));
      productEntryDetail.setValuePts(MathUtil.roundOff(ptsValue, decimalPrecision));
      productEntryDetail.setValueSs(MathUtil.roundOff(ssValue, decimalPrecision));
      double expectedRate = ProductUtil.calculateExpectedRate(productEntryDetail.getProductEntryId(), productEntryDetail, productEntryDetail.getPtsSsRateDerivationCriterion());
      //productEntryDetail.setExpectedLandingRate(expectedRate);
      productEntryDetail.setValueExpected(expectedRate);
      productEntryDetail.setValuePtrPerProdPiece(productEntryDetail.getValuePtr());
      productEntryDetail.setValuePtsPerProdPiece(productEntryDetail.getValuePts());
      if (!AccountUtil.isSuperStockiest(account)) {
        productEntryDetail.setValueRate(productEntryDetail.getValuePts());
      }
    }
  }

  /**
   *
   * @param productEntry
   * @param productEntryDetailList
   */
  @Override
  public void processProductEntryCalculation(ProductEntry productEntry, List<ProductEntryDetail> productEntryDetailList, List<ProductEntryDetail> serviceEntryDetailList) {

    Double invoiceGoodsValue = 0.0, productsGoodsValue = 0.0;
    Double invoiceNetValue = 0.0;
    Double invoiceDiscount = 0.0;
    Double assessableValue = 0.0;
    Double groupAssessableValue;
    Double invoiceIgstValue = 0.0, invoiceCgstValue = 0.0, invoiceSgstValue = 0.0, serviceIgstService = 0.0;
    Double lineNetValue;
    Double goodsValue;
    Double lineDiscountValue;
    Double productAssasableValue = 0.0;
    Double invoiceNetProductValue = 0.0;
    int decimalPrecision = productEntry.getDecimalPrecision() == null ? 2 : productEntry.getDecimalPrecision();

    int productNetQty = 0;
    double boxNetQuntity = 0.0;

    double totalQty = 0.0;             //To calculate qty + free qty 

    boolean cashDiscountApplied = false;

    HashMap<Integer, InvoiceGroup> invoiceGroupMap = new HashMap<>();
    InvoiceGroup invoiceGroup;

    Double invoiceValue;
    Double invoiceRoundOff;

    Double productActualQty;
    int productActualFreeQty;
    double freeDiscountValue;

    boolean schemeApplicable = SystemConstants.FREE_SCHEME.equals(productEntry.getQuantityOrFree());

    int groupCount;

    for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
      if (productEntryDetail.getProductBatchId() != null && productEntryDetail.getProductQuantity() != null && productEntryDetail.getValueRate() != null) {

        productEntryDetail.setSchemeDiscountValueDerived(null);
        productEntryDetail.setProductDiscountValueDerived(null);
        productEntryDetail.setInvoiceDiscountPercDerived(null);
        productEntryDetail.setInvoiceDiscountValue(null);
        productEntryDetail.setInvoiceDiscountValueDerived(null);
        productEntryDetail.setCashDiscountValueDerived(null);

        if (schemeApplicable) {
          productActualQty = productEntryDetail.getProductQuantity() == null ? 0 : productEntryDetail.getProductQuantity();
          productActualFreeQty = 0;
        } else {
          productActualFreeQty = productEntryDetail.getProductQuantityFree() == null ? 0 : productEntryDetail.getProductQuantityFree();
          productActualQty = (productEntryDetail.getProductQuantity() == null ? 0 : productEntryDetail.getProductQuantity()) + productActualFreeQty;
        }

        goodsValue = productActualQty * productEntryDetail.getValueRate();
        lineNetValue = goodsValue;
        invoiceGoodsValue += goodsValue;
        productsGoodsValue = invoiceGoodsValue;
        productEntryDetail.setValueGoods(goodsValue);

        /**
         * Need to check line discount is applicable or not. If free quantity is specified in line, then the program will convert free as discount value.
         *
         */
        if (!schemeApplicable && productActualFreeQty > 0) {
          freeDiscountValue = productActualFreeQty * productEntryDetail.getValueRate();
          productEntryDetail.setSchemeDiscountValue(freeDiscountValue);
          lineNetValue -= freeDiscountValue;
          if (lineNetValue > 0) {
            productEntryDetail.setSchemeDiscountPercentage((freeDiscountValue * 100) / goodsValue);
          } else {
            productEntryDetail.setSchemeDiscountPercentage(100.00);
          }
          // Double productRateAfterDiscount = lineNetValue / productActualQty;
          productEntryDetail.setSchemeDiscountValueDerived(freeDiscountValue / productActualQty);
        } else if (!schemeApplicable && productEntry.getOpeningStockEntry() == 1 //scheme discount entering provision in opening stock
                && (productEntryDetail.getSchemeDiscountValue() != null || productEntryDetail.getSchemeDiscountPercentage() != null)) {
          if (productEntryDetail.getSchemeDiscountValue() != null) {
            freeDiscountValue = productEntryDetail.getSchemeDiscountValue();
            lineNetValue -= freeDiscountValue;
            if (lineNetValue > 0) {
              productEntryDetail.setSchemeDiscountPercentage((freeDiscountValue * 100) / goodsValue);
            } else {
              productEntryDetail.setSchemeDiscountPercentage(100.00);
            }
            productEntryDetail.setSchemeDiscountValueDerived(freeDiscountValue / productActualQty);
          } else if (productEntryDetail.getSchemeDiscountPercentage() != null) {
            freeDiscountValue = (goodsValue * productEntryDetail.getSchemeDiscountPercentage() / 100);
            productEntryDetail.setSchemeDiscountValue(freeDiscountValue);
            lineNetValue -= freeDiscountValue;
            productEntryDetail.setSchemeDiscountValueDerived(freeDiscountValue / productActualQty);
          }
        } else {
          productEntryDetail.setSchemeDiscountPercentage(null);
          productEntryDetail.setSchemeDiscountValue(null);
          productEntryDetail.setIsSchemeDiscountToCustomer(null);
          productEntryDetail.setSchemeDiscountReplacement(null);
        }

        // Applying line discount
        if ((productEntryDetail.getDiscountValue() != null && productEntryDetail.getDiscountValue() > 0)
                || (productEntryDetail.getDiscountPercentage() != null && productEntryDetail.getDiscountPercentage() > 0)) {
          if (SystemConstants.DISCOUNT_IN_PERCENTAGE.equals(productEntryDetail.getIsProductDiscountInPercentage())) {
            lineDiscountValue = (productEntryDetail.getDiscountPercentage() / 100) * lineNetValue;
            productEntryDetail.setDiscountValue(lineDiscountValue);
            productEntryDetail.setProductDiscountValueDerived(productEntryDetail.getDiscountValue() / productActualQty);
            lineNetValue -= productEntryDetail.getDiscountValue();
          } else {
            productEntryDetail.setDiscountPercentage((productEntryDetail.getDiscountValue() * 100) / lineNetValue);
            productEntryDetail.setProductDiscountValueDerived(productEntryDetail.getDiscountValue() / productActualQty);
            lineNetValue -= productEntryDetail.getDiscountValue();
          }
        } else {
          productEntryDetail.setDiscountPercentage(null);
          productEntryDetail.setDiscountValue(null);
          productEntryDetail.setIsProductDiscountInPercentage(null);
        }

        productEntryDetail.setValueNet(lineNetValue);
        productEntryDetail.setValueAssessable(lineNetValue);
        productEntryDetail.setPurchaseRatePerPiece(lineNetValue);

        invoiceNetValue += lineNetValue;

        productNetQty += productEntryDetail.getProductQuantity();

        boxNetQuntity += (productEntryDetail.getProductSecondaryQuantity() == null ? 1 : productEntryDetail.getProductSecondaryQuantity());

        //Calculate total Qty (qty+free qty)
        totalQty += (productEntryDetail.getProductQuantity() + (productEntryDetail.getProductQuantityFree() != null ? productEntryDetail.getProductQuantityFree() : 0));
        if (invoiceGroupMap.containsKey(productEntryDetail.getTaxCodeId().getId())) {
          InvoiceGroup group = invoiceGroupMap.get(productEntryDetail.getTaxCodeId().getId());
          group.setInvoiceNetAmount(MathUtil.roundOff((group.getInvoiceNetAmount() + lineNetValue), decimalPrecision));
          group.setInvoiceProductNetAmount(MathUtil.roundOff((group.getInvoiceProductNetAmount() + lineNetValue), decimalPrecision));
          group.setInvoiceGoodsAmount(MathUtil.roundOff((group.getInvoiceGoodsAmount() + goodsValue), decimalPrecision));
          group.setProductQuantity(group.getProductQuantity() + 1);
          group.setAssessableValue(MathUtil.roundOff(group.getInvoiceNetAmount(), decimalPrecision));
          invoiceGroupMap.put(productEntryDetail.getTaxCodeId().getId(), group);
        } else {
          InvoiceGroup group = new InvoiceGroup();
          group.setInvoiceNetAmount(MathUtil.roundOff(lineNetValue, decimalPrecision));
          group.setInvoiceProductNetAmount(MathUtil.roundOff(lineNetValue, decimalPrecision));
          group.setInvoiceGoodsAmount(MathUtil.roundOff(goodsValue, decimalPrecision));
          group.setTaxCode(productEntryDetail.getTaxCodeId());
          group.setAssessableValue(group.getInvoiceNetAmount());
          group.setProductQuantity(1);
          invoiceGroupMap.put(productEntryDetail.getTaxCodeId().getId(), group);
        }
      }
    }
    invoiceNetProductValue = invoiceNetValue;
    double serviceQty = 0.0, serviceNetValue = 0.0, serviceNetAmount = 0.0;
    double valueBeforeTax = invoiceNetValue;

    // Service value calculation
    if (!StringUtil.isEmpty(serviceEntryDetailList)) {
      for (ProductEntryDetail serviceEntryDetail : serviceEntryDetailList) {
        serviceEntryDetail.setInvoiceDiscountPercDerived(null);
        serviceEntryDetail.setInvoiceDiscountValue(null);
        serviceEntryDetail.setInvoiceDiscountValueDerived(null);
        serviceEntryDetail.setCashDiscountValueDerived(null);
        if (serviceEntryDetail.getValueRate() != null) {
          serviceQty = serviceEntryDetail.getProductQuantity() == null ? 1 : serviceEntryDetail.getProductQuantity() == 0 ? 1 : serviceEntryDetail.getProductQuantity();
          serviceNetValue = serviceQty * serviceEntryDetail.getValueRate();
          serviceNetAmount += serviceNetValue;
          serviceEntryDetail.setProductQuantity(serviceQty);
          serviceEntryDetail.setValueGoods(serviceNetValue);
          serviceEntryDetail.setValueAssessable(serviceNetValue);
          serviceEntryDetail.setValueRatePerProdPieceDer(serviceNetValue);
          serviceEntryDetail.setLandingPricePerPieceCompany(serviceNetValue);
          serviceEntryDetail.setValueNet(serviceNetValue);

          if (invoiceGroupMap.containsKey(serviceEntryDetail.getTaxCodeId().getId())) {
            InvoiceGroup group = invoiceGroupMap.get(serviceEntryDetail.getTaxCodeId().getId());
            group.setInvoiceNetAmount(MathUtil.roundOff((group.getInvoiceNetAmount() + serviceNetValue), decimalPrecision));
            group.setInvoiceGoodsAmount(MathUtil.roundOff((group.getInvoiceGoodsAmount() + serviceNetValue), decimalPrecision));
            group.setProductQuantity(group.getProductQuantity() + 1);
            group.setAssessableValue(MathUtil.roundOff(group.getAssessableValue() + serviceNetValue, decimalPrecision));
            invoiceGroupMap.put(serviceEntryDetail.getTaxCodeId().getId(), group);
          } else {
            InvoiceGroup group = new InvoiceGroup();
            group.setInvoiceNetAmount(MathUtil.roundOff(serviceNetValue, decimalPrecision));
            group.setInvoiceGoodsAmount(MathUtil.roundOff(serviceNetValue, decimalPrecision));
            group.setTaxCode(serviceEntryDetail.getTaxCodeId());
            group.setAssessableValue(group.getInvoiceNetAmount());
            group.setProductQuantity(1);
            invoiceGroupMap.put(serviceEntryDetail.getTaxCodeId().getId(), group);
          }
        }
      }
      invoiceGoodsValue += serviceNetAmount;
      invoiceNetValue += serviceNetAmount;
    }
    if (productEntry.getDiscountsApplicableForService() == SystemRuntimeConfig.DISCOUNT_APPLICABLE_FOR_SERVICES) {
      valueBeforeTax = invoiceNetValue;
    }
    // Apply Invoice Discount
    if (productEntry.getIsInvoiceDiscountInPercentage() != null) {
      Double netValue = (productEntry.getInvoiceAmount() == null ? 0 : (productEntry.getInvoiceAmount()) + (productEntry.getInvoiceAmountDiscountValue() == null ? 0 : productEntry.getInvoiceAmountDiscountValue()));
      if (netValue != null && netValue != 0) {
        if (SystemConstants.DISCOUNT_IN_VALUE.equals(productEntry.getIsInvoiceDiscountInPercentage())) {
          invoiceDiscount = productEntry.getInvoiceAmountDiscountValue();
          if (invoiceNetValue != 0) {
            productEntry.setInvoiceAmountDiscountPerc(MathUtil.roundOff((invoiceDiscount * 100) / invoiceNetValue, decimalPrecision));
          } else {
            productEntry.setInvoiceAmountDiscountPerc(0.0);
          }
        } else if (invoiceNetValue != 0) {
          invoiceDiscount = (invoiceNetValue * (productEntry.getInvoiceAmountDiscountPerc() / 100));
          productEntry.setInvoiceAmountDiscountValue(invoiceDiscount);
        }
      }
    } else {
      productEntry.setInvoiceAmountDiscountPerc(null);
      productEntry.setInvoiceAmountDiscountValue(null);
    }

    groupCount = invoiceGroupMap.size();
    Double groupDiscount = 0.0;
    // Applying Invoice Discount to tax group
    if (invoiceDiscount != null && invoiceDiscount > 0) {
      for (Map.Entry<Integer, InvoiceGroup> invoiceGroupSet : invoiceGroupMap.entrySet()) {

        invoiceGroup = invoiceGroupSet.getValue();
        Double valueBeforeInvoiceDisc = 0.0;
        for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
          if (productEntryDetail.getProductQuantity() != null && productEntryDetail.getValueRate() != null) {
            if (schemeApplicable) {
              productActualQty = productEntryDetail.getProductQuantity() == null ? 0 : productEntryDetail.getProductQuantity();
            } else {
              productActualFreeQty = productEntryDetail.getProductQuantityFree() == null ? 0 : productEntryDetail.getProductQuantityFree();
              productActualQty = (productEntryDetail.getProductQuantity() == null ? 0 : productEntryDetail.getProductQuantity()) + productActualFreeQty;
            }

            if (productEntryDetail != null && productEntryDetail.getTaxCodeId() != null) {
              if (invoiceGroup.getTaxCode().getId().equals(productEntryDetail.getTaxCodeId().getId())) {
                if (invoiceGroup.getInvoiceDiscount() == null) {
                  if (productEntry.getDiscountsApplicableForService() == SystemRuntimeConfig.DISCOUNT_APPLICABLE_FOR_SERVICES) {
                    groupDiscount = (invoiceGroup.getInvoiceNetAmount() / valueBeforeTax) * invoiceDiscount;
                  } else {
                    groupDiscount = (invoiceGroup.getInvoiceProductNetAmount() / valueBeforeTax) * invoiceDiscount;
                  }
                  invoiceGroup.setInvoiceDiscount(MathUtil.roundOff(groupDiscount, decimalPrecision));
                  valueBeforeInvoiceDisc = invoiceGroup.getInvoiceNetAmount();
                  groupAssessableValue = invoiceGroup.getInvoiceNetAmount() - groupDiscount;
                  invoiceGroup.setAssessableValue(MathUtil.roundOff(groupAssessableValue, decimalPrecision));

                  productAssasableValue += groupAssessableValue;
                }

                if (productEntryDetail.getValueNet() != null) {
                  Double lineInvoiceDisc = 0.0;
                  if (productEntry.getDiscountsApplicableForService() == SystemRuntimeConfig.DISCOUNT_APPLICABLE_FOR_SERVICES) {
                    lineInvoiceDisc = (productEntryDetail.getValueNet() / valueBeforeInvoiceDisc) * invoiceGroup.getInvoiceDiscount();
                  } else {
                    lineInvoiceDisc = (productEntryDetail.getValueNet() / invoiceGroup.getInvoiceProductNetAmount()) * invoiceGroup.getInvoiceDiscount();
                  }
                  lineInvoiceDisc = MathUtil.roundOff(lineInvoiceDisc, decimalPrecision);
                  productEntryDetail.setValueAssessable(productEntryDetail.getValueAssessable() - lineInvoiceDisc);
                  productEntryDetail.setPurchaseRatePerPiece(productEntryDetail.getValueAssessable());
                  productEntryDetail.setInvoiceDiscountValue(lineInvoiceDisc);
                  Double unitDiscount = lineInvoiceDisc / productActualQty;
                  productEntryDetail.setInvoiceDiscountValueDerived(unitDiscount);
                  double p = MathUtil.roundOff(((lineInvoiceDisc * 100) / productEntryDetail.getValueNet()), decimalPrecision);
                  productEntryDetail.setInvoiceDiscountPercDerived(p);
                }
              }
            }
          }
        }

        if (!StringUtil.isEmpty(serviceEntryDetailList)) {
          for (ProductEntryDetail serviceEntryDetail : serviceEntryDetailList) {
            if (productEntry.getDiscountsApplicableForService() == SystemRuntimeConfig.DISCOUNT_APPLICABLE_FOR_SERVICES) {
              if (serviceEntryDetail.getServiceCommodityId() != null && serviceEntryDetail.getValueRate() != null) {
                serviceQty = serviceEntryDetail.getProductQuantity() == null ? 1 : serviceEntryDetail.getProductQuantity() == 0 ? 1 : serviceEntryDetail.getProductQuantity();

                if (serviceEntryDetail != null && serviceEntryDetail.getTaxCodeId() != null) {
                  if (invoiceGroup.getTaxCode().getId().equals(serviceEntryDetail.getTaxCodeId().getId())) {
                    if (invoiceGroup.getInvoiceDiscount() == null) {
                      groupDiscount = (invoiceGroup.getInvoiceNetAmount() / invoiceNetValue) * invoiceDiscount;
                      invoiceGroup.setInvoiceDiscount(MathUtil.roundOff(groupDiscount, decimalPrecision));
                      groupAssessableValue = invoiceGroup.getInvoiceNetAmount() - groupDiscount;
                      invoiceGroup.setAssessableValue(MathUtil.roundOff(groupAssessableValue, decimalPrecision));
                      productAssasableValue += groupAssessableValue;
                    }

                    if (serviceEntryDetail.getValueNet() != null) {
                      Double lineInvoiceDisc = (serviceEntryDetail.getValueNet() / invoiceGroup.getInvoiceNetAmount()) * invoiceGroup.getInvoiceDiscount();
                      lineInvoiceDisc = MathUtil.roundOff(lineInvoiceDisc, decimalPrecision);
                      serviceEntryDetail.setValueAssessable(serviceEntryDetail.getValueAssessable() - lineInvoiceDisc);
                      serviceEntryDetail.setValueRatePerProdPieceDer(serviceEntryDetail.getValueAssessable());
                      serviceEntryDetail.setLandingPricePerPieceCompany(serviceEntryDetail.getValueAssessable());
                      serviceEntryDetail.setPurchaseRatePerPiece(serviceEntryDetail.getValueAssessable());
                      serviceEntryDetail.setInvoiceDiscountValue(lineInvoiceDisc);
                      Double unitDiscount = lineInvoiceDisc / serviceQty;
                      serviceEntryDetail.setInvoiceDiscountValueDerived(unitDiscount);
                      double p = MathUtil.roundOff(((lineInvoiceDisc * 100) / serviceEntryDetail.getValueNet()), decimalPrecision);
                      serviceEntryDetail.setInvoiceDiscountPercDerived(p);
                    }
                  }
                }
              }
            } else {
              serviceEntryDetail.setInvoiceDiscountPercDerived(null);
              serviceEntryDetail.setInvoiceDiscountValue(null);
              serviceEntryDetail.setInvoiceDiscountValueDerived(null);
            }
          }
        }
      }
      valueBeforeTax = valueBeforeTax - invoiceDiscount;
    } else {
      productAssasableValue = invoiceNetValue;
    }

    // Applying Cash Discount.
    if (productEntry.getCashDiscountApplicable() != null && productEntry.getCashDiscountValue() != null && productEntry.getCashDiscountValue() > 0) {
      for (Map.Entry<Integer, InvoiceGroup> invoiceGroupSet : invoiceGroupMap.entrySet()) {

        invoiceGroup = invoiceGroupSet.getValue();
        Double valueBeforeCashDisc = 0.0;
        invoiceGroup.setInvoiceProductNetAmount((invoiceGroup.getInvoiceProductNetAmount() != null ? invoiceGroup.getInvoiceProductNetAmount() : 0.0)
                - (invoiceGroup.getInvoiceDiscount() != null ? invoiceGroup.getInvoiceDiscount() : 0.0));
        for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
          if (productEntryDetail != null && productEntryDetail.getTaxCodeId() != null && productEntryDetail.getValueRate() != null && productEntryDetail.getProductQuantity() != null) {
            if (invoiceGroup.getTaxCode().getId().equals(productEntryDetail.getTaxCodeId().getId())) {
              if (invoiceGroup.getCashDiscount() == null) {
                if (productEntry.getDiscountsApplicableForService() == SystemRuntimeConfig.DISCOUNT_APPLICABLE_FOR_SERVICES) {
                  groupDiscount = (invoiceGroup.getAssessableValue() / valueBeforeTax) * productEntry.getCashDiscountValue();
                } else {
                  groupDiscount = (invoiceGroup.getInvoiceProductNetAmount() / valueBeforeTax) * productEntry.getCashDiscountValue();
                }
                invoiceGroup.setCashDiscount(MathUtil.roundOff(groupDiscount, decimalPrecision));
                if (productEntry.getCashDiscountTaxable() != null) {
                  valueBeforeCashDisc = invoiceGroup.getAssessableValue();
                  groupAssessableValue = invoiceGroup.getAssessableValue() - groupDiscount;
                } else {
                  groupAssessableValue = invoiceGroup.getAssessableValue();
                }
                invoiceGroup.setAssessableValue(MathUtil.roundOff(groupAssessableValue, decimalPrecision));
              }
              Double lineInvoiceDisc = 0.0;
              if (productEntry.getDiscountsApplicableForService() == SystemRuntimeConfig.DISCOUNT_APPLICABLE_FOR_SERVICES) {
                lineInvoiceDisc = productEntryDetail.getValueAssessable() / (valueBeforeCashDisc) * invoiceGroup.getCashDiscount();
              } else {
                lineInvoiceDisc = productEntryDetail.getValueAssessable() / (invoiceGroup.getInvoiceProductNetAmount() != null ? invoiceGroup.getInvoiceProductNetAmount() : 0) * invoiceGroup.getCashDiscount();
              }
              lineInvoiceDisc = MathUtil.roundOff(lineInvoiceDisc, decimalPrecision);
              productEntryDetail.setValueAssessable(productEntryDetail.getValueAssessable() - lineInvoiceDisc);
              productEntryDetail.setPurchaseRatePerPiece(productEntryDetail.getValueAssessable());
              Double qty = (productEntryDetail.getProductQuantity() != null ? productEntryDetail.getProductQuantity() : 0)
                      + (productEntryDetail.getProductQuantityFree() != null ? productEntryDetail.getProductQuantityFree() : 0);
              double unitDiscount = 0.0;
              if (qty > 0) {
                unitDiscount = lineInvoiceDisc / qty;
              }
              productEntryDetail.setCashDiscountValueDerived(unitDiscount);
            }
          }
        }

        if (!StringUtil.isEmpty(serviceEntryDetailList)) {
          for (ProductEntryDetail serviceEntryDetail : serviceEntryDetailList) {
            if (productEntry.getDiscountsApplicableForService() == SystemRuntimeConfig.DISCOUNT_APPLICABLE_FOR_SERVICES) {
              if (serviceEntryDetail.getServiceCommodityId() != null && serviceEntryDetail.getValueRate() != null && serviceEntryDetail.getTaxCodeId() != null) {
                if (invoiceGroup.getTaxCode().getId().equals(serviceEntryDetail.getTaxCodeId().getId())) {
                  if (invoiceGroup.getCashDiscount() == null) {
                    groupDiscount = (invoiceGroup.getAssessableValue() / valueBeforeTax) * productEntry.getCashDiscountValue();
                    invoiceGroup.setCashDiscount(MathUtil.roundOff(groupDiscount, decimalPrecision));
                    if (productEntry.getCashDiscountTaxable() != null) {
                      valueBeforeCashDisc = invoiceGroup.getAssessableValue();
                      groupAssessableValue = invoiceGroup.getAssessableValue() - groupDiscount;
                    } else {
                      groupAssessableValue = invoiceGroup.getAssessableValue();
                    }
                    invoiceGroup.setAssessableValue(MathUtil.roundOff(groupAssessableValue, decimalPrecision));
                  }

                  Double lineInvoiceDisc = ((serviceEntryDetail.getValueNet() - (serviceEntryDetail.getInvoiceDiscountValueDerived() == null ? 0 : serviceEntryDetail.getInvoiceDiscountValueDerived())) / valueBeforeCashDisc) * invoiceGroup.getCashDiscount();
                  lineInvoiceDisc = MathUtil.roundOff(lineInvoiceDisc, decimalPrecision);
                  serviceEntryDetail.setValueAssessable(serviceEntryDetail.getValueAssessable() - lineInvoiceDisc);
                  serviceEntryDetail.setValueRatePerProdPieceDer(serviceEntryDetail.getValueAssessable());
                  serviceEntryDetail.setLandingPricePerPieceCompany(serviceEntryDetail.getValueAssessable());
                  serviceEntryDetail.setPurchaseRatePerPiece(serviceEntryDetail.getValueAssessable());
                  Double unitDiscount = lineInvoiceDisc / (serviceEntryDetail.getProductQuantity() != 0 ? serviceEntryDetail.getProductQuantity() : 1);
                  serviceEntryDetail.setCashDiscountValueDerived(unitDiscount);
                }
              } else {
                serviceEntryDetail.setCashDiscountValueDerived(null);
              }
            }
          }
        }
      }
      if (productEntry.getCashDiscountTaxable() != null) {
        valueBeforeTax = valueBeforeTax - productEntry.getCashDiscountValue();
      }
    }

    /**
     * Find the tax values of products.
     */
    double lineQty = 0.0;
    double lineProdQty = 0.0;
    double lineProdFreeQty = 0.0;
    invoiceIgstValue = 0.0;
    invoiceCgstValue = 0.0;
    invoiceSgstValue = 0.0;
    for (Map.Entry<Integer, InvoiceGroup> invoiceGroupSet : invoiceGroupMap.entrySet()) {
      invoiceGroup = invoiceGroupSet.getValue();
      for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
        if (productEntryDetail != null && productEntryDetail.getTaxCodeId() != null && productEntryDetail.getValueRate() != null) {

          lineProdQty = productEntryDetail.getProductQuantity() == null ? 0.0 : productEntryDetail.getProductQuantity();
          lineProdFreeQty = productEntryDetail.getProductQuantityFree() == null ? 0.0 : productEntryDetail.getProductQuantityFree();
          lineQty = lineProdQty + lineProdFreeQty;

          if (invoiceGroup.getTaxCode().getId().equals(productEntryDetail.getTaxCodeId().getId())) {
            if (invoiceGroup.getInvoiceIgstValue() == null) {
              invoiceGroup.setAssessableValue(MathUtil.roundOff(invoiceGroup.getAssessableValue(), decimalPrecision));
              invoiceGroup.setInvoiceNetAmount(MathUtil.roundOff(invoiceGroup.getInvoiceNetAmount(), decimalPrecision));

              if (SystemConstants.INTRASTATE_PURCHASE.equals(productEntry.getBusinessArea()) && productEntry.getSezZone().intValue() == 0) {
                for (TaxCode taxCode : invoiceGroup.getTaxCode().getTaxCodeList()) {
                  if (AccountingConstant.TAX_TYPE_CGST == taxCode.getTaxType()) {
                    invoiceGroup.setInvoiceCgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), decimalPrecision));
                  } else if (AccountingConstant.TAX_TYPE_SGST == taxCode.getTaxType()) {
                    invoiceGroup.setInvoiceSgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), decimalPrecision));
                  }
                }
                invoiceCgstValue = invoiceCgstValue + invoiceGroup.getInvoiceCgstValue();
                invoiceSgstValue = invoiceSgstValue + invoiceGroup.getInvoiceSgstValue();
                invoiceGroup.setInvoiceIgstValue(MathUtil.roundOff((invoiceGroup.getInvoiceCgstValue() + invoiceGroup.getInvoiceSgstValue()), decimalPrecision));
              } else {
                invoiceGroup.setInvoiceIgstValue(MathUtil.roundOff(invoiceGroup.getAssessableValue() * (invoiceGroup.getTaxCode().getRatePercentage() / 100), decimalPrecision));
              }
              invoiceIgstValue = invoiceIgstValue + (invoiceGroup.getInvoiceIgstValue() == null ? 0 : invoiceGroup.getInvoiceIgstValue());
            }

            assessableValue = ((productEntryDetail.getValueNet() == null ? 0 : productEntryDetail.getValueNet()) - (productEntryDetail.getInvoiceDiscountValue() == null ? 0 : productEntryDetail.getInvoiceDiscountValue()));
            productEntryDetail.setValueIgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceIgstValue() / 2, decimalPrecision) * 2);
            if (SystemConstants.INTRASTATE_PURCHASE.equals(productEntry.getBusinessArea()) && productEntry.getSezZone().intValue() == 0) {
              productEntryDetail.setValueCgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceCgstValue(), decimalPrecision));
              productEntryDetail.setValueSgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceSgstValue(), decimalPrecision));
            }

            if (lineQty != 0) {
              if (schemeApplicable) {
                productEntryDetail.setValueAssessable(productEntryDetail.getValueAssessable() / lineProdQty);
                productEntryDetail.setPurchaseRatePerPiece(productEntryDetail.getValueAssessable());
              } else {
                productEntryDetail.setValueAssessable(productEntryDetail.getValueAssessable() / lineQty);
                productEntryDetail.setPurchaseRatePerPiece(productEntryDetail.getValueAssessable());
              }
            }

          }
//          productEntryDetail.setValueRatePerProdPieceDer(ProductUtil.getProductLandingPrice(productEntryDetail));

//          New Logic
          productEntryDetail.setLandingPricePerPieceCompany(ProductUtil.getProductCompanyLandingRate(productEntryDetail));
          productEntryDetail.setValueRatePerProdPieceDer(ProductUtil.getValueRatePerProdPiece(productEntryDetail));   //This is the lan. rate to consider
          productEntryDetail.setActualSellingPriceDerived(ProductUtil.getActualSellingPriceDerived(productEntryDetail));
          if (serviceNetAmount > 0) {
            productEntryDetail.setValueServiceRateDerived(ProductUtil.getValueServiceRateDerived(productEntryDetail.getValueNet(), invoiceNetProductValue, serviceNetAmount, lineQty));

          }

          if (productEntryDetail.getValuePts() == null) {
            ProductUtil.getPtsValueFromRate(productEntry, productEntryDetail);
          }
        }
      }

      if (!StringUtil.isEmpty(serviceEntryDetailList) && serviceIgstService == 0) {
        for (ProductEntryDetail serviceEntryDetail : serviceEntryDetailList) {
          if (serviceEntryDetail != null && serviceEntryDetail.getTaxCodeId() != null && serviceEntryDetail.getValueRate() != null) {
            serviceQty = serviceEntryDetail.getProductQuantity() == null ? 1 : serviceEntryDetail.getProductQuantity() == 0 ? 1 : serviceEntryDetail.getProductQuantity();

            if (invoiceGroup.getTaxCode().getId().equals(serviceEntryDetail.getTaxCodeId().getId())) {
              if (invoiceGroup.getInvoiceIgstValue() == null) {
                invoiceGroup.setAssessableValue(MathUtil.roundOff(invoiceGroup.getAssessableValue(), decimalPrecision));
                invoiceGroup.setInvoiceNetAmount(MathUtil.roundOff(invoiceGroup.getInvoiceNetAmount(), decimalPrecision));

                if (SystemConstants.INTRASTATE_PURCHASE.equals(productEntry.getBusinessArea()) && productEntry.getSezZone().intValue() == 0) {
                  for (TaxCode taxCode : invoiceGroup.getTaxCode().getTaxCodeList()) {
                    if (AccountingConstant.TAX_TYPE_CGST == taxCode.getTaxType()) {
                      invoiceGroup.setInvoiceCgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), decimalPrecision));
                    } else if (AccountingConstant.TAX_TYPE_SGST == taxCode.getTaxType()) {
                      invoiceGroup.setInvoiceSgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), decimalPrecision));
                    }
                  }
                  invoiceCgstValue = invoiceCgstValue + invoiceGroup.getInvoiceCgstValue();
                  invoiceSgstValue = invoiceSgstValue + invoiceGroup.getInvoiceSgstValue();
                  invoiceGroup.setInvoiceIgstValue(MathUtil.roundOff((invoiceGroup.getInvoiceCgstValue() + invoiceGroup.getInvoiceSgstValue()), decimalPrecision));
                } else {
                  invoiceGroup.setInvoiceIgstValue((MathUtil.roundOff(invoiceGroup.getAssessableValue() * (invoiceGroup.getTaxCode().getRatePercentage() / 100) / 2, decimalPrecision)) * 2);
                }
                invoiceIgstValue = invoiceIgstValue + (invoiceGroup.getInvoiceIgstValue() == null ? 0 : invoiceGroup.getInvoiceIgstValue());
                serviceIgstService = serviceIgstService + invoiceGroup.getInvoiceIgstValue();
              }

              assessableValue = (serviceEntryDetail.getValueNet() == null ? 0 : serviceEntryDetail.getValueNet());// - (serviceEntryDetail.getInvoiceDiscountValueDerived() == null ? 0 : serviceEntryDetail.getInvoiceDiscountValueDerived()));
              serviceEntryDetail.setValueIgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceIgstValue(), decimalPrecision));
              if (SystemConstants.INTRASTATE_PURCHASE.equals(productEntry.getBusinessArea()) && productEntry.getSezZone().intValue() == 0) {
                serviceEntryDetail.setValueCgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceCgstValue(), decimalPrecision));
                serviceEntryDetail.setValueSgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceSgstValue(), decimalPrecision));
              }

              if (serviceQty != 0) {
                if (schemeApplicable) {
                  serviceEntryDetail.setValueAssessable(serviceEntryDetail.getValueAssessable() / serviceQty);
                  serviceEntryDetail.setPurchaseRatePerPiece(serviceEntryDetail.getValueAssessable());
                } else {
                  serviceEntryDetail.setValueAssessable(serviceEntryDetail.getValueAssessable() / serviceQty);
                  serviceEntryDetail.setPurchaseRatePerPiece(serviceEntryDetail.getValueAssessable());
                }
              }
            }
          }
        }
        // invoiceIgstValue += serviceIgstService;
      }
//      invoiceIgstValue = invoiceIgstValue + (invoiceGroup.getInvoiceIgstValue() == null ? 0 : invoiceGroup.getInvoiceIgstValue());
    }

    assessableValue = invoiceNetValue - (invoiceDiscount == null ? 0 : invoiceDiscount);
    if (productEntry.getCashDiscountTaxable() != null && productEntry.getCashDiscountValue() != null && productEntry.getCashDiscountValue() > 0) {
      assessableValue = assessableValue - productEntry.getCashDiscountValue();
      cashDiscountApplied = true;
    }

    productEntry.setInvoiceAmountGoods(MathUtil.roundOff(invoiceGoodsValue, decimalPrecision));
    productEntry.setInvoiceAmountNet(MathUtil.roundOff(invoiceNetValue, decimalPrecision));
    productEntry.setInvoiceAmountAssessable(MathUtil.roundOff(assessableValue, decimalPrecision));
    productEntry.setAssessableValue(MathUtil.roundOff(assessableValue, decimalPrecision));
    productEntry.setInvoiceAmountIgst(MathUtil.roundOff(invoiceIgstValue, decimalPrecision));
    if (SystemConstants.INTRASTATE_PURCHASE.equals(productEntry.getBusinessArea()) && productEntry.getSezZone().intValue() == 0) {
      productEntry.setInvoiceAmountCgst(MathUtil.roundOff(invoiceCgstValue, decimalPrecision));
      productEntry.setInvoiceAmountSgst(MathUtil.roundOff(invoiceSgstValue, decimalPrecision));
    }

    double grandTotal = MathUtil.roundOff(assessableValue + invoiceIgstValue, decimalPrecision);
    productEntry.setGrandTotal(grandTotal);
    if (productEntry.getCashDiscountApplicable() != null && productEntry.getCashDiscountValue() != null && productEntry.getCashDiscountValue() > 0 && !cashDiscountApplied) {
      grandTotal = productEntry.getGrandTotal() - productEntry.getCashDiscountValue();
      invoiceValue = Double.valueOf(Math.round(grandTotal));
    } else {
      invoiceValue = Double.valueOf(Math.round(productEntry.getGrandTotal()));
    }
    productEntry.setInvoiceAmountSubtotal(grandTotal);
    if (productEntry.getTcsNetValue() != null) {
      grandTotal += productEntry.getTcsNetValue();
      invoiceValue = Double.valueOf(Math.round(grandTotal));
    }
    invoiceRoundOff = invoiceValue - grandTotal;
    productEntry.setInvoiceValue(invoiceValue);

    if (productEntry.getInvoiceAmount() != null && invoiceValue != null) {
      productEntry.setInvoiceAmountVariation(productEntry.getInvoiceAmount() - invoiceValue);
      productEntry.setInvoiceValue(invoiceValue + (productEntry.getInvoiceAmountVariation() != null ? productEntry.getInvoiceAmountVariation() : 0));
    }

    productEntry.setInvoiceDiscountValue(productEntry.getInvoiceAmountDiscountValue());
    productEntry.setInvoiceRoundOff(MathUtil.roundOff(invoiceRoundOff, decimalPrecision));
    productEntry.setProductNetQuantity(productNetQty);
    productEntry.setProductSecondaryPackQuantity(boxNetQuntity);
    productEntry.setInvoiceGroup(new ArrayList<>(invoiceGroupMap.values()));
    productEntry.setTotalQty(totalQty);
  }

  /**
   *
   * @param productEntryDetail
   */
//  private void parseFreeToDiscountValue(ProductEntryDetail productEntryDetail) {
//    int totalQty;
//    if (productEntryDetail.getProductQuantityFree() != null) {
//      if (productEntryDetail.getProductQuantity() != null && productEntryDetail.getValueRate() != null) {
//        totalQty = productEntryDetail.getProductQuantity() + productEntryDetail.getProductQuantityFree();
//        Double netValue = totalQty * productEntryDetail.getValueRate();
//        Double freeDiscountValue = productEntryDetail.getProductQuantityFree() * productEntryDetail.getValueRate();
//        productEntryDetail.setSchemeDiscountValue(freeDiscountValue);
//        productEntryDetail.setSchemeDiscountPercentage((freeDiscountValue * 100) / netValue);
//        Double netValAfterDiscount = netValue - freeDiscountValue;
//        Double productRateAfterDiscount = netValAfterDiscount / totalQty;
//        productEntryDetail.setSchemeDiscountValueDerived(freeDiscountValue / totalQty);
//      }
//    }
//  }
  /**
   *
   * @param puchaseOrder
   * @param purchaseOrderItemList
   */
  @Override
  public void processPurchaseOrderCalculation(PurchaseOrder puchaseOrder, List<PurchaseOrderItem> purchaseOrderItemList
  ) {
    Double invoiceNetValue = 0.0;
    Double invoiceIgstValue = 0.0;
    Double lineNetValue = 0.0;
    Integer productNetQty = 0;

    HashMap<Integer, InvoiceGroup> invoiceGroupMap = new HashMap<>();
    InvoiceGroup invoiceGroup;

    for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItemList) {
      if (purchaseOrderItem.getProductId() != null && purchaseOrderItem.getProductBatchId() != null && purchaseOrderItem.getQtyRequired() != null && purchaseOrderItem.getOrderPricePerPiece() != null) {

        lineNetValue = purchaseOrderItem.getQtyRequired() * purchaseOrderItem.getOrderPricePerPiece();
        purchaseOrderItem.setNetValue(lineNetValue);

        invoiceNetValue = invoiceNetValue + lineNetValue;

        productNetQty += purchaseOrderItem.getQtyRequired();

        if (purchaseOrderItem.getTaxCode() != null) {
          if (invoiceGroupMap.containsKey(purchaseOrderItem.getTaxCode().getId())) {
            InvoiceGroup group = invoiceGroupMap.get(purchaseOrderItem.getTaxCode().getId());
            group.setInvoiceNetAmount(group.getInvoiceNetAmount() + lineNetValue);
            group.setAssessableValue(group.getInvoiceNetAmount());
            group.setProductQuantity(group.getProductQuantity() + 1);
            invoiceGroupMap.put(purchaseOrderItem.getTaxCode().getId(), group);
          } else {
            InvoiceGroup group = new InvoiceGroup();
            group.setInvoiceNetAmount(lineNetValue);
            group.setTaxCode(purchaseOrderItem.getTaxCode());
            group.setAssessableValue(group.getInvoiceNetAmount());
            group.setProductQuantity(1);
            invoiceGroupMap.put(purchaseOrderItem.getTaxCode().getId(), group);
          }
        }
      }
    }

    /**
     * Find the tax values of products.
     */
    for (Map.Entry<Integer, InvoiceGroup> invoiceGroupSet : invoiceGroupMap.entrySet()) {
      invoiceGroup = invoiceGroupSet.getValue();
      invoiceGroup.setAssessableValue(MathUtil.roundOff(invoiceGroup.getAssessableValue(), 2));
      invoiceGroup.setInvoiceNetAmount(MathUtil.roundOff(invoiceGroup.getInvoiceNetAmount(), 2));
      invoiceGroup.setInvoiceIgstValue(invoiceGroup.getAssessableValue() * (invoiceGroup.getTaxCode().getRatePercentage() / 100));
      invoiceGroup.setInvoiceIgstValue(MathUtil.roundOff(invoiceGroup.getInvoiceIgstValue(), 2));
      for (TaxCode taxCode : invoiceGroup.getTaxCode().getTaxCodeList()) {
        if (AccountingConstant.TAX_TYPE_CGST == taxCode.getTaxType()) {
          invoiceGroup.setInvoiceCgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), 2));
        } else if (AccountingConstant.TAX_TYPE_SGST == taxCode.getTaxType()) {
          invoiceGroup.setInvoiceSgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), 2));
        }
      }
      invoiceIgstValue = invoiceIgstValue + invoiceGroup.getInvoiceIgstValue();
    }

    puchaseOrder.setGoodsValue(MathUtil.roundOff(invoiceNetValue, 2));

    puchaseOrder.setTaxValue(MathUtil.roundOff(invoiceIgstValue, 2));

    puchaseOrder.setNetAmount(invoiceNetValue + invoiceIgstValue);

    puchaseOrder.setNetAmount(MathUtil.roundOff(puchaseOrder.getNetAmount(), 2));

    puchaseOrder.setProductNetQuantity(productNetQty);
    puchaseOrder.setInvoiceGroup(new ArrayList<>(invoiceGroupMap.values()));

  }

  /**
   *
   * @param main
   * @param account
   * @param filter
   * @param purchseOrderId
   * @param purchaseOrderItemId
   * @return
   */
  @Override
  public List<ProductSummary> productSummaryForPurchaseOrder(Main main, Account account,
          String filter, Integer purchseOrderId,
          Integer purchaseOrderItemId
  ) {
    String condition = "";
    List<ProductSummary> list = null;
    filter = "%" + filter + "%";
    List<Object> params = new ArrayList<>();
    params.add(account.getId());

    if (purchseOrderId != null && purchaseOrderItemId != null) {
      condition = " and scm_product_batch.id not in (select product_batch_id from scm_purchase_order_item where purchase_order_id = ? and id <> ?) ";
      params.add(purchseOrderId);
      params.add(purchaseOrderItemId);
    } else if (purchseOrderId != null) {
      condition = " and scm_product_batch.id not in (select product_batch_id from scm_purchase_order_item where purchase_order_id = ?) ";
      params.add(purchseOrderId);
    }
    params.add(account.getCompanyId().getId());
    params.add(filter);

    String sql = "select scm_product.id as product_id, scm_product.product_name as product_name,scm_product.pack_size, "
            + "to_char(scm_product_batch.expiry_date_actual, 'Mon-yy') as expiry_date, scm_product_batch.batch_no as batch, scm_product_batch.value_mrp as mrp_value, "
            + "CASE WHEN scm_product_category_tax_code.id is not null then scm_product_category_tax_code.id ELSE scm_commodity_tax_code.id END as gst_tax_code_id, "
            + "CASE WHEN scm_product_category_tax_code.id is not null then scm_product_category_tax_code.rate_percentage ELSE scm_commodity_tax_code.rate_percentage END as gst_percentage, "
            + "scm_product_batch.id as product_batch_id "
            + "from scm_product "
            + "inner join scm_product_preset scm_product_preset on scm_product_preset.product_id = scm_product.id and scm_product_preset.account_id = ? "
            + "inner join scm_product_batch scm_product_batch on scm_product_batch.product_id = scm_product.id "
            + condition
            + "inner JOIN scm_service_commodity scm_service_commodity on scm_product.commodity_id = scm_service_commodity.id "
            + "inner JOIN scm_product_category scm_product_category on scm_product.product_category_id = scm_product_category.id "
            + "inner JOIN scm_tax_code scm_commodity_tax_code on scm_service_commodity.purchase_tax_code_id = scm_commodity_tax_code.id "
            + "inner JOIN scm_tax_head scm_commodity_tax_head on scm_commodity_tax_head.id = scm_commodity_tax_code.tax_head_id "
            + "inner JOIN scm_tax_code scm_product_category_tax_code on scm_product_category_tax_code.id = scm_product_category.purchase_tax_code_id "
            + "inner JOIN scm_tax_head scm_product_category_tax_head on scm_product_category_tax_head.id = scm_product_category_tax_code.tax_head_id "
            + "where scm_product.company_id = ? and upper(scm_product.product_name) like upper(?) order by scm_product_batch.id asc";

    list = AppDb.getList(main.dbConnector(), ProductSummary.class,
            sql, params.toArray());

    return list;
  }

  /**
   *
   * @param main
   * @param product
   * @return
   */
  private TaxCode getProductPurchaseTaxCode(Main main, Product product) {
    TaxCode taxCode = null;
    if (product.getProductCategoryId() != null) {
      taxCode = product.getProductCategoryId().getPurchaseTaxCodeId();
    } else if (product.getCommodityId() != null) {
      taxCode = product.getCommodityId().getPurchaseTaxCodeId();
    }
    return taxCode;
  }

  /**
   *
   * @param main
   * @param product
   * @return
   */
  private TaxCode getProductSalesTaxCode(Main main, Product product) {
    TaxCode taxCode = null;
    if (product.getProductCategoryId() != null) {
      taxCode = product.getProductCategoryId().getSalesTaxCodeId();
    } else if (product.getCommodityId() != null) {
      taxCode = product.getCommodityId().getSalesTaxCodeId();
    }
    return taxCode;
  }

  /**
   *
   * @param main
   * @param account
   * @param purchaseOrderItem
   */
  @Override
  public void updatePurchaseOrderItem(Main main, Account account, PurchaseOrderItem purchaseOrderItem) {
    double mrpLteValue = 0;
    Product product = null;
    if (purchaseOrderItem.getProductSummary() != null) {
      if (purchaseOrderItem.getProductSummary().getProductId() != null) {
        product = new Product();
        product.setId(purchaseOrderItem.getProductSummary().getProductId());
        product = ProductService.selectByPk(main, product);
        product.setProductName(purchaseOrderItem.getProductSummary().getProductName());
        purchaseOrderItem.setProductId(product);
        //Set Product Tax Code
        purchaseOrderItem.setTaxCode(getProductPurchaseTaxCode(main, product));
      }
      if (purchaseOrderItem.getProductSummary().getProductBatchId() != null) {
        // Set Product Details
        ProductBatch productBatch = ProductBatchService.selectByPk(main, new ProductBatch(purchaseOrderItem.getProductSummary().getProductBatchId()));
        purchaseOrderItem.setProductBatchId(productBatch);
        purchaseOrderItem.setProductId(productBatch.getProductId());
        purchaseOrderItem.getProductSummary().setProductName(purchaseOrderItem.getProductId().getProductName());
      } else {
        purchaseOrderItem.setProductBatchId(null);
      }
      purchaseOrderItem.getProductSummary().setProductName(purchaseOrderItem.getProductId().getProductName());
      purchaseOrderItem.getProductSummary().after();
    }
  }

  /**
   *
   * @param main
   * @param productEntry
   * @param productEntryDetailList
   */
  @Override
  public void productMarginCalculation(Main main, ProductEntry productEntry, List<ProductEntryDetail> productEntryDetailList) {
    Double marginPercentage;
    Double vendorMarginPercentage = null;
    Integer ssRateCrieria;
    Double landingPrice;
    Double expectedLandingRate;
    Double marginValue;
    Double deviationPerc;
    double vendorMarginValue;
    double productQuantity = 0;
    int productFreeQty = 0;

    if (productEntryDetailList != null && !productEntryDetailList.isEmpty()) {
      for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
        if (productEntryDetail.getProductDetailId() != null) {

          productEntryDetail.setMarginValueDeviation(null);
          productEntryDetail.setMarginPercentageDeviation(null);
          productEntryDetail.setValueRatePerProdPieceDer(null);
          productEntryDetail.setActualSellingPriceDerived(null);
          productEntryDetail.setVendorMarginValue(null);
          productEntryDetail.setExpectedLandingRate(null);

          boolean schemeApplicable = SystemConstants.FREE_SCHEME.equals(productEntry.getQuantityOrFree());
          productQuantity = productEntryDetail.getProductQuantity();

          if (!schemeApplicable) {
            productFreeQty = productEntryDetail.getProductQuantityFree() == null ? 0 : productEntryDetail.getProductQuantityFree();
            productQuantity = productEntryDetail.getProductQuantity() + productFreeQty;
          }

          ssRateCrieria = productEntryDetail.getPtsSsRateDerivationCriterion();

          marginPercentage = productEntryDetail.getMarginPercentage() == null ? 0.0 : productEntryDetail.getMarginPercentage();

          if (productEntry.getVendorReservePercentage() != null && productEntry.getVendorReservePercentage() > 0) {
            vendorMarginPercentage = productEntry.getVendorReservePercentage();
          }

          // Finds Landing price
          productEntryDetail.setLandingPricePerPieceCompany(ProductUtil.getProductCompanyLandingRate(productEntryDetail));
          landingPrice = MathUtil.roundOff(ProductUtil.getValueRatePerProdPiece(productEntryDetail), 2);

          double actualSellingPriceDerived = ProductUtil.getActualSellingPriceDerived(productEntryDetail);

          productEntryDetail.setValueRatePerProdPieceDer(landingPrice);
          productEntryDetail.setActualSellingPriceDerived(MathUtil.roundOff(actualSellingPriceDerived, 2));

          if (vendorMarginPercentage != null && vendorMarginPercentage > 0) {

            double expectedLandingPriceLevel1 = 0;
            double expectedLandingPriceLevel2 = 0;

            expectedLandingPriceLevel1 = MathUtil.roundOff(ProductUtil.getExpectedLandingRate(ssRateCrieria, actualSellingPriceDerived, marginPercentage, productEntry.getAccountId()), 2);
            expectedLandingPriceLevel2 = MathUtil.roundOff(ProductUtil.getExpectedLandingRate(ssRateCrieria, expectedLandingPriceLevel1, vendorMarginPercentage, productEntry.getAccountId()), 2);

            productEntryDetail.setExpectedLandingRate(MathUtil.roundOff(expectedLandingPriceLevel2, 2));

            double marginPerc = 0.0;

            if (expectedLandingPriceLevel2 > landingPrice) { // We have extra margin

              marginPerc = MathUtil.roundOff(ProductUtil.getSsMargin(ssRateCrieria, expectedLandingPriceLevel2, landingPrice), 3);
              marginValue = MathUtil.roundOff((expectedLandingPriceLevel2 - landingPrice) * productQuantity, 2);
              productEntryDetail.setMarginValueDeviation(marginValue);
              productEntryDetail.setMarginPercentageDeviation(marginPerc);
              productEntryDetail.setMarginValueDeviationDer(marginValue / productQuantity);

              if (marginValue != 0) {
                //PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.MARGIN_DIFFERENCE, null, Math.abs(marginValue), productEntry, productEntryDetail, PlatformService.NORMAL_FUND_STATE);
                TradingVariationLogService.insertPurchaseLog(main, productEntry.getAccountId(), SystemRuntimeConfig.MARGIN_DIFFERENCE, null, Math.abs(marginValue), productEntry, productEntryDetail);
              }

              vendorMarginValue = MathUtil.roundOff((expectedLandingPriceLevel1 - expectedLandingPriceLevel2) * productQuantity, 2);
              productEntryDetail.setVendorMarginValue(vendorMarginValue);
              productEntryDetail.setVendorMarginValueDer(vendorMarginValue / productQuantity);
              if (vendorMarginValue != 0) {
                //PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.VENDOR_RESERVE, null, Math.abs(vendorMarginValue), productEntry, productEntryDetail, PlatformService.UNREALIZED_FUND_STATE);
                TradingVariationLogService.insertPurchaseLog(main, productEntry.getAccountId(), SystemRuntimeConfig.VENDOR_RESERVE, null, Math.abs(vendorMarginValue), productEntry, productEntryDetail);
              }

            } else if (expectedLandingPriceLevel1 > landingPrice && expectedLandingPriceLevel2 < landingPrice) {

              /**
               * This has to be the price in which we need to return to the supplier as system already posted the difference in platform
               */
              productEntryDetail.setExpectedLandingRate(landingPrice);

              vendorMarginValue = MathUtil.roundOff((expectedLandingPriceLevel1 - landingPrice) * productQuantity, 2);
              productEntryDetail.setVendorMarginValue(vendorMarginValue);
              productEntryDetail.setVendorMarginValueDer(vendorMarginValue / productQuantity);
              if (vendorMarginValue != 0) {
                //PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.VENDOR_RESERVE, null, Math.abs(vendorMarginValue), productEntry, productEntryDetail, PlatformService.UNREALIZED_FUND_STATE);
                TradingVariationLogService.insertPurchaseLog(main, productEntry.getAccountId(), SystemRuntimeConfig.VENDOR_RESERVE, null, Math.abs(vendorMarginValue), productEntry, productEntryDetail);
              }
            } else if (expectedLandingPriceLevel1 < landingPrice) {

              /**
               * This has to be the price in which we need to return to the supplier as system already posted the difference in platform
               */
              productEntryDetail.setExpectedLandingRate(expectedLandingPriceLevel1);

              marginPerc = MathUtil.roundOff(ProductUtil.getSsMargin(ssRateCrieria, expectedLandingPriceLevel1, landingPrice), 3);
              marginValue = MathUtil.roundOff((expectedLandingPriceLevel1 - landingPrice) * productQuantity, 2);
              productEntryDetail.setMarginValueDeviation(marginValue);
              productEntryDetail.setMarginPercentageDeviation(marginPerc);
              productEntryDetail.setMarginValueDeviationDer(marginValue / productQuantity);
              if (marginValue != 0) {
                //PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.MARGIN_DIFFERENCE, Math.abs(marginValue), null, productEntry, productEntryDetail, PlatformService.NORMAL_FUND_STATE);
                TradingVariationLogService.insertPurchaseLog(main, productEntry.getAccountId(), SystemRuntimeConfig.MARGIN_DIFFERENCE, Math.abs(marginValue), null, productEntry, productEntryDetail);
              }

            }

          } else if (marginPercentage != null) {

            double expectedLandingPrice = MathUtil.roundOff(ProductUtil.getExpectedLandingRate(ssRateCrieria, actualSellingPriceDerived, marginPercentage, productEntry.getAccountId()), 2);

            /**
             * This has to be the price in which we need to return to the supplier as system already posted the difference in platform
             */
            productEntryDetail.setExpectedLandingRate(expectedLandingPrice);

            double ssMarginPerc = ProductUtil.getSsMargin(ssRateCrieria, actualSellingPriceDerived, landingPrice);

            if (ssMarginPerc > marginPercentage) {
              // Excess margin and record the +ve deviation
              deviationPerc = ssMarginPerc - marginPercentage;
              productEntryDetail.setMarginPercentageDeviation(MathUtil.roundOff(deviationPerc, 4));

              marginValue = expectedLandingPrice - landingPrice;

              productEntryDetail.setMarginValueDeviation(MathUtil.roundOff((marginValue * productQuantity), 4));
              productEntryDetail.setMarginValueDeviationDer(marginValue);
              // Platform entry
              if (productEntryDetail.getMarginValueDeviation() != 0) {
                //PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.MARGIN_DIFFERENCE, null, Math.abs(productEntryDetail.getMarginValueDeviation()), productEntry, productEntryDetail, PlatformService.NORMAL_FUND_STATE);
                TradingVariationLogService.insertPurchaseLog(main, productEntry.getAccountId(), SystemRuntimeConfig.MARGIN_DIFFERENCE, null, Math.abs(productEntryDetail.getMarginValueDeviation()), productEntry, productEntryDetail);
              }
            } else if (marginPercentage.equals(ssMarginPerc)) {
              productEntryDetail.setMarginPercentageDeviation(0.0);
              productEntryDetail.setMarginValueDeviation(0.0);
              productEntryDetail.setMarginValueDeviationDer(0.0);
            } else if (ssMarginPerc < marginPercentage) {
              // Less margin and record the -ve deviation
              deviationPerc = marginPercentage - ssMarginPerc;
              productEntryDetail.setMarginPercentageDeviation(MathUtil.roundOff(deviationPerc, 4));
              marginValue = expectedLandingPrice - landingPrice;
              productEntryDetail.setMarginValueDeviation(MathUtil.roundOff((marginValue * productQuantity), 4));
              productEntryDetail.setMarginValueDeviationDer(marginValue);
              // Platform entry
              if (productEntryDetail.getMarginValueDeviation() != 0) {
                //PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.MARGIN_DIFFERENCE, Math.abs(productEntryDetail.getMarginValueDeviation()), null, productEntry, productEntryDetail, PlatformService.NORMAL_FUND_STATE);
                TradingVariationLogService.insertPurchaseLog(main, productEntry.getAccountId(), SystemRuntimeConfig.MARGIN_DIFFERENCE, Math.abs(productEntryDetail.getMarginValueDeviation()), null, productEntry, productEntryDetail);
              }
            }
          }
          /**
           * Free quantity for company as replacement need to hit in platform.
           */
          if ((productEntryDetail.getSchemeDiscountReplacement() != null && productEntryDetail.getSchemeDiscountReplacement() == ProductEntryDetailService.FREE_AS_REPLACEMENT)
                  || (productEntryDetail.getProductDiscountReplacement() != null && productEntryDetail.getProductDiscountReplacement() == ProductEntryDetailService.FREE_AS_REPLACEMENT)) {
            Double replacementValue = (productEntryDetail.getSchemeDiscountValue() != null ? productEntryDetail.getSchemeDiscountValue() : 0.0)
                    + (productEntryDetail.getDiscountValue() != null ? productEntryDetail.getDiscountValue() : 0.0);
            if (replacementValue != 0) {
              PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.REPLACEMENT, null, Math.abs(replacementValue), productEntry, productEntryDetail, PlatformService.NORMAL_FUND_STATE);
            }
          }
          /**
           * Free quantity for company need to hit in platform as unrealized value.
           */
          if (productEntryDetail.getProductFreeQtySchemeId() != null) {
            if (productEntryDetail.getProductQuantityFree() > 0) {
              if (ProductEntryDetailService.FREE_TO_COMPANY.equals(productEntryDetail.getProductFreeQtySchemeId().getIsFreeQtyToCustomer())) {
                double freeQtyRate = productEntryDetail.getProductQuantityFree() * productEntryDetail.getExpectedLandingRate();
                if (freeQtyRate != 0) {
                  PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.FREE_QUANTITY, null, Math.abs(freeQtyRate), productEntry, productEntryDetail, PlatformService.UNREALIZED_FUND_STATE);
                }
              }
            }
          }
          ProductEntryDetailService.insertOrUpdate(main, productEntryDetail);
        }
      }
    }
  }

  /**
   *
   * @param main
   * @param productEntry
   */
  @Override
  public void savePurchase(Main main, ProductEntry productEntry) {
    LedgerExternalDataService.savePurchase(main, productEntry, productEntry.getCompanyId().getCurrencyId(), productEntry.getCompanyId());
  }

  @Override
  public void savePurchaseReturn(Main main, PurchaseReturn purchaseReturn) {
    LedgerExternalDataService.savePurchaseReturn(main, purchaseReturn, purchaseReturn.getCompanyId().getCurrencyId(), purchaseReturn.getCompanyId());
  }

  @Override
  public void saveSales(Main main, SalesInvoice salesInvoice) {
    if (salesInvoice.getInvoiceAmount() != null && salesInvoice.getInvoiceAmount() > 0) {
      LedgerExternalDataService.saveSales(main, salesInvoice, salesInvoice.getCompanyId().getCurrencyId(), salesInvoice.getCompanyId());
    }
  }

  @Override
  public void saveSalesReturn(Main main, SalesReturn salesReturn) {
    if (salesReturn.getInvoiceAmount() != null && salesReturn.getInvoiceAmount() > 0) {
      LedgerExternalDataService.saveSalesReturn(main, salesReturn, salesReturn.getCompanyId().getCurrencyId(), salesReturn.getCompanyId());
    }
  }

  @Override
  public void saveSalesServicesInvoice(Main main, SalesServicesInvoice salesServicesInvoice) {
    LedgerExternalDataService.saveSalesServicesInvoice(main, salesServicesInvoice, salesServicesInvoice.getCompanyId().getCurrencyId(), salesServicesInvoice.getCompanyId());
  }

  @Override
  public void saveDebitCreditNote(Main main, DebitCreditNote debitCreditNote) {
    LedgerExternalDataService.saveDebitCreditNote(main, debitCreditNote, debitCreditNote.getCompanyId());
  }

  /**
   *
   * @param productDetail
   * @param productPreset
   * @param productEntryDetail
   */
  private static void setProductPresetToProductEntryDetail(ProductBatch productDetail, ProductPreset productPreset, ProductEntryDetail productEntryDetail) {
    ProductPackingDetail productPackingDetail = productDetail.getProductPackingDetailId();
    // SS margin
    productEntryDetail.setMarginPercentage(productPreset.getMarginPercentage());
    // PTR margin
    productEntryDetail.setPtrMarginPercentage(productPreset.getPtrMarginPercentage());
    // PTS Margin
    productEntryDetail.setPtsMarginPercentage(productPreset.getPtsMarginPercentage());

    // Primary Pack
    productEntryDetail.setPackPrimary(productPackingDetail.getPackPrimary());
    // Secondary Pack
    productEntryDetail.setPackSecondary(productPackingDetail.getPackSecondary());
    // Tertiary Pack
    productEntryDetail.setPackTertiary(productPackingDetail.getPackTertiary());

    // Secondary QTY
    productEntryDetail.setPackSecondaryPrimaryQty(productPackingDetail.getPackSecondaryPrimaryQty());
    //Tertiary QTY
    productEntryDetail.setPackTertiarySecondaryQty(productPackingDetail.getPackTertiarySecondaryQty());
    // primary dimension
    productEntryDetail.setPackPrimaryDimension(productPackingDetail.getPackPrimaryDimension());

    productEntryDetail.setDimensionGroupQty(productPackingDetail.getPrimaryDimensionGroupQty());
    //productEntryDetail.setDimensionContentQty(productPreset.getPrimaryDimensionContentQty());
    productEntryDetail.setDimensionUnitQty(productPackingDetail.getPrimaryDimensionUnitQty());

    // Pack purchase default
    productEntryDetail.setPackPurchaseDefault(productDetail.getDefaultProductPackingId());

    // Pack Dimension size
    //productEntryDetail.setPackDimensionSize(productPackingDetail.getPackPrimaryDimension());
    // primary dimension content qty
    //productEntryDetail.setPrimaryDimensionContentQtyUnit(productPreset.getPrimaryDimensionContentQtyUnit());
    //expiry sales days
    productEntryDetail.setExpirySalesDays(productPreset.getProductId().getExpirySalesDays());

    // Rate criteria
    productEntryDetail.setMrpltePtrRateDerivationCriterion(productPreset.getMrpltePtrRateDerivationCriterion());
    // Rate criteria
    productEntryDetail.setPtrPtsRateDerivationCriterion(productPreset.getPtrPtsRateDerivationCriterion());
    // Rate criteria
    productEntryDetail.setPtsSsRateDerivationCriterion(productPreset.getPtsSsRateDerivationCriterion());

    productEntryDetail.setPackSize(productPreset.getProductId().getPackSize() + productPreset.getProductId().getProductUnitId().getSymbol());

  }

  @Override
  public void processSalesInvoiceCalculation(SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItemList, boolean draftView) {
    SalesInvoiceProcessor processor = new SalesInvoiceProcessorImpl();
    processor.processSalesInvoice(salesInvoice, salesInvoiceItemList, draftView);
  }

  @Override
  public void updateSalesInvoiceItem(Main main, SalesInvoiceItem salesInvoiceItem) {
    ProductDetailSales pds = salesInvoiceItem.getProductDetailSales();

    ProductBatch productBatch = ProductBatchService.selectByPk(main, new ProductBatch(pds.getProductBatchId()));
    //salesInvoiceItem.setProductDetailId(pDetail);

    // Sets product selling price
    salesInvoiceItem.setValueProdPieceSelling(pds.getPricelistPts());
    salesInvoiceItem.setProdPieceSellingForced(pds.getPricelistPts());
    salesInvoiceItem.setValueMrp(pds.getMrpValue());
    salesInvoiceItem.setValuePts(pds.getPricelistPts());
    //salesInvoiceItem.setActualPts(pds.getActualPts());
    salesInvoiceItem.setValuePtr(pds.getPricelistPtr());
    salesInvoiceItem.setExpiryDate(productBatch.getExpiryDateActual());
    salesInvoiceItem.setBatchNo(productBatch.getBatchNo());
    salesInvoiceItem.setIsNearExpiry(productBatch.getExpiryDateSales().compareTo(new Date()));
    salesInvoiceItem.setQuantityOrFree(pds.getQtyOrFree());
    salesInvoiceItem.setActualSellingPriceDerived(pds.getActualPts());
    // Sets Product Tax Code
    salesInvoiceItem.setTaxCodeId(getProductSalesTaxCode(main, productBatch.getProductId()));

    // Sets product primary packing
    if (salesInvoiceItem.getProductDetailSales().getPrimaryPackId() != null) {
      salesInvoiceItem.getProductDetailSales().setPrimaryPacking(ProductPackingService.selectByPk(main, new ProductPacking(salesInvoiceItem.getProductDetailSales().getPrimaryPackId())));
    }
    // Sets product secondary packing
    if (salesInvoiceItem.getProductDetailSales().getSecondaryPackId() != null) {
      salesInvoiceItem.getProductDetailSales().setSecondaryPacking(ProductPackingService.selectByPk(main, new ProductPacking(salesInvoiceItem.getProductDetailSales().getSecondaryPackId())));
    }
    // Sets product tertiary packing
    if (salesInvoiceItem.getProductDetailSales().getTertiaryPackId() != null) {
      salesInvoiceItem.getProductDetailSales().setTertiaryPacking(ProductPackingService.selectByPk(main, new ProductPacking(salesInvoiceItem.getProductDetailSales().getTertiaryPackId())));
    }

    if (salesInvoiceItem.getSchemeDiscountValue() == null && pds.getSchemeDiscountPer() != null && pds.getSchemeDiscountPer() > 0) {
      salesInvoiceItem.setSchemeDiscountPercentage(pds.getSchemeDiscountPer());
      salesInvoiceItem.setSchemeDiscountValue(salesInvoiceItem.getProdPieceSellingForced() * (pds.getSchemeDiscountPer() / 100));
      salesInvoiceItem.setSchemeDiscountActual(pds.getSchemeDiscountPer());
      salesInvoiceItem.setIsSchemeDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
    } else {
      salesInvoiceItem.setSchemeDiscountValue(null);
      salesInvoiceItem.setSchemeDiscountActual(null);
      salesInvoiceItem.setSchemeDiscountPercentage(null);
    }

    if (salesInvoiceItem.getProductDiscountValue() == null && pds.getProductDiscountPer() != null && pds.getProductDiscountPer() > 0) {
      salesInvoiceItem.setProductDiscountPercentage(pds.getProductDiscountPer());
      salesInvoiceItem.setProductDiscountValue(salesInvoiceItem.getProdPieceSellingForced() * (pds.getProductDiscountPer() / 100));
      salesInvoiceItem.setProductDiscountActual(pds.getProductDiscountPer());
      salesInvoiceItem.setIsProductDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
    } else {
      salesInvoiceItem.setProductDiscountValue(null);
      salesInvoiceItem.setProductDiscountActual(null);
      salesInvoiceItem.setProductDiscountPercentage(null);
    }

    salesInvoiceItem.setValueGoods(null);
    salesInvoiceItem.setValueSale(null);
    salesInvoiceItem.setProductQty(null);
    salesInvoiceItem.setProductQtyFree(null);
    salesInvoiceItem.setTertiaryQuantity(null);
    salesInvoiceItem.setSecondaryQuantity(null);
  }

  @Override
  public List<Product> selectProductForSales(Main main, SalesInvoice salesInvoice, String filter) {
    List<Product> productList = null;
    if (salesInvoice.getAccountGroupId() != null) {
      /**
       * SELECT * FROM getProductsOfAccountGroup(?,?,?)
       *
       * Params : 1. Account Group id 2. Commodity Id 3. Product Type Id
       *
       */
      String sql = "select prodid as id, prodname as product_name from getproductsofaccountgroupforsales(?,?,?) ";
      if (!StringUtil.isEmpty(filter)) {
        sql += " where upper(prodname) like ? order by upper(prodname) asc";
        productList = AppDb.getList(main.dbConnector(), Product.class, sql,
                new Object[]{salesInvoice.getAccountGroupId() == null ? null : salesInvoice.getAccountGroupId().getId(),
                  null, StringUtil.isEmpty(salesInvoice.getProductType()) ? null : salesInvoice.getProductType(), "%" + filter.toUpperCase() + "%"});
      } else {
        sql += " order by upper(prodname) asc";
        productList = AppDb.getList(main.dbConnector(), Product.class, sql, new Object[]{salesInvoice.getAccountGroupId() == null ? null : salesInvoice.getAccountGroupId().getId(),
          null, StringUtil.isEmpty(salesInvoice.getProductType()) ? null : salesInvoice.getProductType()});
      }
    }
    return productList;
  }

  @Override
  public List<ProductDetailSales> selectProductDetailForSales(Main main, SalesInvoice salesInvoice, SalesInvoiceItem salesInvoiceItem) {
    /**
     * SQL Function - getproductsdetailsforgstsale(?,?,?,?) params. 1. Account Group Id. 2.Product Id 3.Account Group price ilist id. 4.Sales invoice item id.
     *
     */
    List<ProductDetailSales> productDetailSales = AppDb.getList(main.dbConnector(), ProductDetailSales.class,
            "SELECT product_batch_id, batch_no, expiry_date_actual, to_char(expiry_date_actual,'Mon-yy') expiry_date, mrp_value,actual_pts, "
            + "pricelist_pts, round(pricelist_ptr,2) pricelist_ptr, free_qty_scheme_id,scheme_discount_per,product_discount_per, "
            + "sum(quantity_available) quantity_available,sum(quantity_free_available) quantity_free_available,pack_size, "
            + "primary_pack_id,secondary_pack_id,tertiary_pack_id,ter_to_sec_quantity,sec_to_pri_quantity, free_scheme_name, qty_or_free, "
            + "concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,"
            + "coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) as product_hash,pack_description "
            + "FROM getproductsdetailsforgstsale(?,?,?,?,?) "
            + "GROUP by product_batch_id,batch_no,pack_description, expiry_date_actual,mrp_value,pricelist_pts,pricelist_ptr,free_qty_scheme_id,scheme_discount_per,"
            + "product_discount_per,pack_size,primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity, free_scheme_name,actual_pts, qty_or_free "
            + "order by expiry_date_actual",
            new Object[]{salesInvoice.getAccountGroupId() == null ? null : salesInvoice.getAccountGroupId().getId(),
              salesInvoiceItem.getProduct() == null ? null : salesInvoiceItem.getProduct().getId(),
              salesInvoice.getAccountGroupPriceListId() == null ? null : salesInvoice.getAccountGroupPriceListId().getId(),
              (salesInvoiceItem.getSalesInvoiceItemHashCode() != null ? salesInvoiceItem.getSalesInvoiceItemHashCode() : null),
              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoice.getInvoiceEntryDate())});
    return productDetailSales;
  }

  @Override
  public void processSalesReturnCalculation(SalesReturn salesReturn, List<SalesReturnItem> salesReturnItemList, boolean resetDiscounts) {

    Double invoiceNetValue = 0.0;
    Double assessableValue = 0.0;
    Double invoiceIgstValue = 0.0, invoiceCgstValue = 0.0, invoiceSgstValue = 0.0;
    Double invoiceTotCgstValue = 0.0, invoiceTotSgstValue = 0.0;
    Double goodsValue = 0.0;
    Double lineNetValue = 0.0;
    Double invoiceGoodsValue = 0.0;
    Double creditSettlementAmount = 0.0;
    boolean isDamagedReturn = (salesReturn.getSalesReturnType().equals(SystemConstants.SALES_RETURN_TYPE_DAMAGED));
    double productNetQty = 0.0;

    HashMap<Integer, InvoiceGroup> invoiceGroupMap = new HashMap<>();
    InvoiceGroup invoiceGroup;

    Double invoiceValue = 0.0;
    Double invoiceRoundOff = 0.0;

    Double productActualQty;
    boolean isTaxable = salesReturn.getIsTaxable() != null ? salesReturn.getIsTaxable().equals(SystemConstants.DEFAULT) : true;

    for (SalesReturnItem salesReturnItem : salesReturnItemList) {
      productActualQty = isDamagedReturn ? salesReturnItem.getProductQuantityDamaged() : salesReturnItem.getProductQuantity() == null ? null : salesReturnItem.getProductQuantity().doubleValue();
      if (salesReturnItem.getProductBatchId() != null && productActualQty != null && salesReturnItem.getValueRate() != null) {

        goodsValue = MathUtil.roundOff((productActualQty * salesReturnItem.getValueRate()), 2);
        invoiceGoodsValue += goodsValue;
        salesReturnItem.setValueGoods(goodsValue);
        salesReturnItem.setValueIgst(null);
        salesReturnItem.setValueCgst(null);
        salesReturnItem.setValueSgst(null);

        lineNetValue = goodsValue;

        /**
         * Need to check line discount is applicable or not. If free quantity is specified in line, then the program will convert free as discount value.
         *
         */
        if (salesReturnItem.getSchemeDiscountValueDerived() != null && salesReturnItem.getSalesInvoiceId() != null) {
          if (salesReturnItem.getSalesInvoiceId() != null) {
            salesReturnItem.setSchemeDiscountValue(salesReturnItem.getSchemeDiscountValueDerived() * productActualQty);
          } else if (salesReturnItem.getSchemeDiscountValue() == null && resetDiscounts) {
            salesReturnItem.setSchemeDiscountValue(salesReturnItem.getSchemeDiscountValueDerived() * productActualQty);
          }
          lineNetValue -= salesReturnItem.getSchemeDiscountValue() == null ? 0 : salesReturnItem.getSchemeDiscountValue();
        } else if (salesReturnItem.getSchemeDiscountValue() != null || salesReturnItem.getSchemeDiscountPercentage() != null) {
          if ((salesReturnItem.getIsSchemeDiscountInValue() != null && SystemConstants.YES.intValue() == salesReturnItem.getIsSchemeDiscountInValue()) || salesReturnItem.getSchemeDiscountValue() != null) {
            salesReturnItem.setSchemeDiscountPercentage(salesReturnItem.getSchemeDiscountValue() * 100 / lineNetValue);
          } else if (salesReturnItem.getSchemeDiscountPercentage() != null) {
            salesReturnItem.setSchemeDiscountValue(lineNetValue * salesReturnItem.getSchemeDiscountPercentage() / 100);
          }
          lineNetValue -= salesReturnItem.getSchemeDiscountValue() == null ? 0 : salesReturnItem.getSchemeDiscountValue();
          salesReturnItem.setSchemeDiscountValueDerived(salesReturnItem.getSchemeDiscountValue() / productActualQty);

        } else if (salesReturnItem.getSchemeDiscountValue() == null && salesReturnItem.getSchemeDiscountPercentage() == null && salesReturnItem.getSalesInvoiceId() == null) {
          salesReturnItem.setSchemeDiscountValueDerived(null);
        }

        // Applying line discount
        if (salesReturnItem.getProductDiscountValueDerived() != null && salesReturnItem.getSalesInvoiceId() != null) {
          if (salesReturnItem.getSalesInvoiceId() != null) {
            salesReturnItem.setProductDiscountValue(salesReturnItem.getProductDiscountValueDerived() * productActualQty);
          } else if (salesReturnItem.getProductDiscountValue() == null && resetDiscounts) {
            salesReturnItem.setProductDiscountValue(salesReturnItem.getProductDiscountValueDerived() * productActualQty);
          }
          lineNetValue -= salesReturnItem.getProductDiscountValue() == null ? 0 : salesReturnItem.getProductDiscountValue();
        } else if (salesReturnItem.getProductDiscountValue() != null || salesReturnItem.getProductDiscountPercentage() != null) {
          if ((salesReturnItem.getIsProductDiscountInValue() != null && SystemConstants.YES.intValue() == salesReturnItem.getIsProductDiscountInValue()) || salesReturnItem.getProductDiscountValue() != null) {
            salesReturnItem.setProductDiscountPercentage(salesReturnItem.getProductDiscountValue() * 100 / lineNetValue);
          } else if (salesReturnItem.getProductDiscountPercentage() != null) {
            salesReturnItem.setProductDiscountValue(lineNetValue * salesReturnItem.getProductDiscountPercentage() / 100);
          }
          lineNetValue -= salesReturnItem.getProductDiscountValue() == null ? 0 : salesReturnItem.getProductDiscountValue();
          salesReturnItem.setProductDiscountValueDerived(salesReturnItem.getProductDiscountValue() / productActualQty);
        } else if (salesReturnItem.getProductDiscountValue() == null && salesReturnItem.getProductDiscountPercentage() == null && salesReturnItem.getSalesInvoiceId() == null) {
          salesReturnItem.setProductDiscountValueDerived(null);
        }

        // Applying invoice discount
        if (salesReturnItem.getInvoiceDiscountValueDerived() != null && salesReturnItem.getSalesInvoiceId() != null) {
          if (salesReturnItem.getSalesInvoiceId() != null) {
            salesReturnItem.setInvoiceDiscountValue(salesReturnItem.getInvoiceDiscountValueDerived() * productActualQty);
          } else if (salesReturnItem.getInvoiceDiscountValue() == null && resetDiscounts) {
            salesReturnItem.setInvoiceDiscountValue(salesReturnItem.getInvoiceDiscountValueDerived() * productActualQty);
          }
          lineNetValue -= salesReturnItem.getInvoiceDiscountValue() == null ? 0 : salesReturnItem.getInvoiceDiscountValue();
        } else if (salesReturnItem.getInvoiceDiscountValue() != null) {
          lineNetValue -= salesReturnItem.getInvoiceDiscountValue() == null ? 0 : salesReturnItem.getInvoiceDiscountValue();
          salesReturnItem.setInvoiceDiscountValueDerived(salesReturnItem.getInvoiceDiscountValue() / productActualQty);
        } else if (salesReturnItem.getInvoiceDiscountValue() == null && salesReturnItem.getSalesInvoiceId() == null) {
          salesReturnItem.setInvoiceDiscountValueDerived(null);
        }

        // New Change 09-06-2020 - Credit Settlement Amount is reduced from taxable value.
        if (salesReturnItem.getCreditSettlementAmount() != null) {
          if (salesReturnItem.getCreditSettlementAmount() <= lineNetValue) {
            lineNetValue -= salesReturnItem.getCreditSettlementAmount();
          } else {
            salesReturnItem.setCreditSettlementAmount(null);
          }
          creditSettlementAmount += (salesReturnItem.getCreditSettlementAmount() != null ? salesReturnItem.getCreditSettlementAmount() : 0);
        }
        salesReturnItem.setValueNet(lineNetValue);
        salesReturnItem.setValueAssessable(lineNetValue);

        invoiceNetValue += lineNetValue;

        productNetQty += productActualQty;

        if (invoiceGroupMap.containsKey(salesReturnItem.getTaxCodeId() == null ? null : salesReturnItem.getTaxCodeId().getId())) {
          InvoiceGroup group = invoiceGroupMap.get(salesReturnItem.getTaxCodeId().getId());
          group.setInvoiceNetAmount(group.getInvoiceNetAmount() + goodsValue);
          group.setProductQuantity(group.getProductQuantity() + 1);
          group.setAssessableValue(MathUtil.roundOff((group.getAssessableValue() + lineNetValue), 2));
          invoiceGroupMap.put(salesReturnItem.getTaxCodeId().getId(), group);
        } else {
          InvoiceGroup group = new InvoiceGroup();
          group.setInvoiceNetAmount(goodsValue);
          group.setTaxCode(salesReturnItem.getTaxCodeId());
          group.setAssessableValue(lineNetValue);
          group.setProductQuantity(1);
          invoiceGroupMap.put(salesReturnItem.getTaxCodeId().getId(), group);
        }
      }
    }

    /**
     * Find the tax values of products.
     */
    if (isTaxable) {

      for (Map.Entry<Integer, InvoiceGroup> invoiceGroupSet : invoiceGroupMap.entrySet()) {
        invoiceGroup = invoiceGroupSet.getValue();
        invoiceCgstValue = 0.0;
        invoiceSgstValue = 0.0;
        for (SalesReturnItem salesReturnItem : salesReturnItemList) {
          if (salesReturnItem != null && salesReturnItem.getTaxCodeId() != null) {
            if (invoiceGroup.getTaxCode().getId().equals(salesReturnItem.getTaxCodeId().getId())) {
              if (invoiceGroup.getInvoiceIgstValue() == null) {
                invoiceGroup.setAssessableValue(MathUtil.roundOff(invoiceGroup.getAssessableValue(), 2));
                invoiceGroup.setInvoiceNetAmount(MathUtil.roundOff(invoiceGroup.getInvoiceNetAmount(), 2));

                if (SalesInvoiceUtil.INTRA_STATE_SALES == salesReturn.getBusinessArea() && salesReturn.getSezZone().intValue() == 0) {
                  for (TaxCode taxCode : invoiceGroup.getTaxCode().getTaxCodeList()) {
                    if (AccountingConstant.TAX_TYPE_CGST == taxCode.getTaxType()) {
                      invoiceGroup.setInvoiceCgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), 2));
                    } else if (AccountingConstant.TAX_TYPE_SGST == taxCode.getTaxType()) {
                      invoiceGroup.setInvoiceSgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), 2));
                    }
                  }
                  invoiceCgstValue += invoiceGroup.getInvoiceCgstValue();
                  invoiceSgstValue += invoiceGroup.getInvoiceSgstValue();
                  invoiceTotCgstValue += invoiceGroup.getInvoiceCgstValue();
                  invoiceTotSgstValue += invoiceGroup.getInvoiceSgstValue();
                  invoiceGroup.setInvoiceIgstValue(MathUtil.roundOff((invoiceCgstValue + invoiceSgstValue), 2));
                } else {
                  invoiceGroup.setInvoiceIgstValue(MathUtil.roundOff(invoiceGroup.getAssessableValue() * (invoiceGroup.getTaxCode().getRatePercentage() / 100) / 2, 2) * 2);
                }
                invoiceIgstValue += invoiceGroup.getInvoiceIgstValue();
              }

              assessableValue = ((salesReturnItem.getValueNet() == null ? 0 : salesReturnItem.getValueNet()) - (salesReturnItem.getInvoiceDiscountValueDerived() == null ? 0 : salesReturnItem.getInvoiceDiscountValueDerived()));
              salesReturnItem.setValueIgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceIgstValue(), 2));
              if (SalesInvoiceUtil.INTRA_STATE_SALES == salesReturn.getBusinessArea() && salesReturn.getSezZone().intValue() == 0) {
                salesReturnItem.setValueCgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceCgstValue(), 2));
                salesReturnItem.setValueSgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceSgstValue(), 2));
              }
            }
          }
        }
      }
    }

    assessableValue = invoiceNetValue;

    salesReturn.setInvoiceAmountNet(MathUtil.roundOff(invoiceNetValue, 2));
    salesReturn.setInvoiceAmountAssessable(MathUtil.roundOff(assessableValue, 2));
    salesReturn.setAssessableValue(MathUtil.roundOff(assessableValue, 2));
    if (isTaxable) {
      salesReturn.setInvoiceAmountIgst(MathUtil.roundOff(invoiceIgstValue, 2));
      if (SalesInvoiceUtil.INTRA_STATE_SALES == salesReturn.getBusinessArea() && salesReturn.getSezZone().intValue() == 0) {
        salesReturn.setInvoiceAmountCgst(MathUtil.roundOff(invoiceTotCgstValue, 2));
        salesReturn.setInvoiceAmountSgst(MathUtil.roundOff(invoiceTotSgstValue, 2));
      }
    } else {
      salesReturn.setInvoiceAmountIgst(null);
      salesReturn.setInvoiceAmountCgst(null);
      salesReturn.setInvoiceAmountSgst(null);
    }

    double grandTotal = MathUtil.roundOff(assessableValue + invoiceIgstValue, 2);
    salesReturn.setGrandTotal(grandTotal);

    if (salesReturn.isDisableRoundOff()) {
      invoiceValue = salesReturn.getGrandTotal();
    } else {
      invoiceValue = Double.valueOf(Math.round(salesReturn.getGrandTotal()));
      invoiceRoundOff = invoiceValue - grandTotal;
      salesReturn.setInvoiceRoundOff(MathUtil.roundOff(invoiceRoundOff, 2));
    }
    salesReturn.setNetCreditSettlementAmount(creditSettlementAmount);
    salesReturn.setInvoiceValue(invoiceValue);
    salesReturn.setInvoiceAmountGoods(invoiceGoodsValue);
    salesReturn.setInvoiceAmount(invoiceValue);
    salesReturn.setProductNetQuantity(productNetQty);
    salesReturn.setInvoiceGroup(new ArrayList<>(invoiceGroupMap.values()));
  }

  @Override
  public List<Product> selectProductForSalesReturn(Main main, ProductEntry salesReturn, String filter) {
    List<Product> productList = null;
    if (salesReturn != null && salesReturn.getAccountId() != null && salesReturn.getCustomerId() != null) {
      if (!StringUtil.isEmpty(filter)) {
        productList = AppService.list(main, Product.class, "select t1.* from scm_product t1 "
                + "inner join scm_product_detail t2 on t1.id = t2.product_id and account_id = ? "
                + "where upper(t1.product_name) like upper(?)"
                + "group by t1.id;", new Object[]{salesReturn.getAccountId().getId(), salesReturn.getCustomerId().getId(), "%" + filter + "%"});
      } else {
        productList = AppService.list(main, Product.class, "select t1.* from scm_product t1 "
                + "inner join scm_product_detail t2 on t1.id = t2.product_id and account_id = ? "
                + "group by t1.id;", new Object[]{salesReturn.getAccountId().getId(), salesReturn.getCustomerId().getId()});
      }

    }
    return productList;
  }

  @Override
  public List<ProductDetailSales> getProductDetailCurrentStockAvailability(Main main, SalesInvoiceItem salesInvoiceItem, AccountGroup accountGroup, Product product, AccountGroupPriceList accountGroupPriceList) {
    String sql = "select string_agg(stock_id::varchar, '#') stockSaleableId, product_entry_detail_id,purchase_invoice,product_detail_id, sum(quantity_available) quantity_available,  sum(quantity_free_available) quantity_free_available, "
            + "concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) as product_hash, "
            + "expiry_date_actual,product_detail_id,batch_no, mrp_value,pricelist_pts,free_qty_scheme_id,scheme_discount_per,product_discount_per,pack_size, "
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity,account_id,marg_dev_per as margin_value_deviation_der,landing_rate "
            + "from getproductsdetailsforgstsale(?,?,?,null,?) "
            + "where "
            + "concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) = ? "
            + "group by product_entry_detail_id,purchase_invoice,product_batch_id,expiry_date_actual,product_detail_id,batch_no, mrp_value,pricelist_pts,pricelist_ptr,free_qty_scheme_id,scheme_discount_per,product_discount_per,pack_size, "
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity,account_id,product_entry_date,marg_dev_per,landing_rate "
            + "order by expiry_date_actual::date ASC, product_entry_date ASC";

    List<ProductDetailSales> productDetailSales = AppDb.getList(main.dbConnector(), ProductDetailSales.class, sql,
            new Object[]{accountGroup == null ? null : accountGroup.getId(),
              product == null ? null : product.getId(),
              accountGroupPriceList == null ? null : accountGroupPriceList.getId(),
              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoiceItem.getSalesInvoiceId().getInvoiceEntryDate()),
              salesInvoiceItem.getProductHash()});
//    System.out.println(accountGroup.getId()+ ","+
//               product.getId()+ ","+
//              accountGroupPriceList.getId()+ ","+
//              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoiceItem.getSalesInvoiceId().getInvoiceEntryDate())+ ","+
//              salesInvoiceItem.getProductHash());
    return productDetailSales;
  }

  @Override
  public ProductDetailSales selectProductDetailSalesForConfirm(Main main, SalesInvoiceItem salesInvoiceItem) {
    /**
     * SQL Function - getproductsdetailsforsaleconfirm(?,?,?,?) params. 1. Account Group Id. 2. Account Group price list id. 3. SalesInvoice id. 4. Sales invoice status id.
     *
     */
    SalesInvoice salesInvoice = salesInvoiceItem.getSalesInvoiceId();
    ProductDetailSales productDetailSales = (ProductDetailSales) AppDb.single(main.dbConnector(), ProductDetailSales.class,
            "SELECT batch_no, expiry_date_actual,to_char(expiry_date_actual,'Mon-yy') expiry_date, "
            + "mrp_value, pricelist_pts, free_qty_scheme_id,scheme_discount_per,product_discount_per, "
            + "sum(quantity_available) quantity_available,  sum(quantity_free_available) quantity_free_available, pack_size, "
            + "primary_pack_id,secondary_pack_id,tertiary_pack_id,ter_to_sec_quantity,sec_to_pri_quantity, "
            + "concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) as product_hash "
            + "FROM getproductsdetailsforgstsaleconfirm(?,?,?,?,?) "
            + "WHERE concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) = ? "
            + "GROUP by product_batch_id,batch_no, expiry_date_actual,mrp_value,pricelist_pts,pricelist_ptr,free_qty_scheme_id,scheme_discount_per,product_discount_per,pack_size,"
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity ",
            new Object[]{salesInvoice.getAccountGroupId().getId(), salesInvoice.getAccountGroupPriceListId().getId(), salesInvoice.getId(),
              salesInvoice.getSalesInvoiceStatusId().getId(), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoice.getInvoiceEntryDate()),
              salesInvoiceItem.getProductHash()});
    return productDetailSales;
  }

  @Override
  public List<ProductDetailSales> selectCurrentSalesItemsFromStock(Main main, SalesInvoice salesInvoice, boolean isProforma) {
    /**
     * SQL Function - getproductsdetailsforsaleconfirm(?,?,?,?) params. 1. Account Group Id. 2. Account Group price list id. 3. SalesInvoice id. 4. Sales invoice status id.
     *
     */
    List<ProductDetailSales> productDetailSales = AppDb.getList(main.dbConnector(), ProductDetailSales.class,
            "SELECT concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) as product_hash,marg_dev_per as margin_value_deviation_der,"
            + " * FROM getproductsdetailsforgstsaleconfirm(?,?,?,?,?) order by pre_sale desc,expiry_date_actual::date ASC, product_entry_date ASC",
            new Object[]{salesInvoice.getAccountGroupId().getId(), salesInvoice.getAccountGroupPriceListId().getId(),
              salesInvoice.getId(), isProforma ? SystemConstants.CONFIRMED : salesInvoice.getSalesInvoiceStatusId().getId(),
              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoice.getInvoiceEntryDate())});
//    System.out.println(salesInvoice.getAccountGroupId().getId()+","+ salesInvoice.getAccountGroupPriceListId().getId()+","+ salesInvoice.getId()+","+ (isProforma ? SystemConstants.CONFIRMED : salesInvoice.getSalesInvoiceStatusId().getId())+","+
//              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoice.getInvoiceEntryDate()));
//    
    return productDetailSales;
  }

  /**
   * Method to calculate Purchase Return values.
   *
   * @param puchaseReturn
   * @param purchaseReturnItemList
   */
  @Override
  public void processPurchaseReturnCalculation(PurchaseReturn puchaseReturn, List<PurchaseReturnItemReplica> purchaseReturnItemReplicaList) {
    Double invoiceNetValue = 0.0;
    Double invoiceIgstValue = 0.0, invoiceCgstValue = 0.0, invoiceSgstValue = 0.0;
    Double invoiceTotIgstValue = 0.0, invoiceTotCgstValue = 0.0, invoiceTotSgstValue = 0.0;
    Double lineNetValue = 0.0, lineDiscountValue = 0.0;
    Double productNetQty = 0.0;

    HashMap<Integer, InvoiceGroup> invoiceGroupMap = new HashMap<>();
    InvoiceGroup invoiceGroup;

    for (PurchaseReturnItemReplica purchaseReturnItem : purchaseReturnItemReplicaList) {
      if (purchaseReturnItem.getQuantityReturned() != null && purchaseReturnItem.getValueRate() != null) {

        lineDiscountValue = 0.0;

        lineNetValue = purchaseReturnItem.getQuantityReturned() * purchaseReturnItem.getValueRate();
        purchaseReturnItem.setValueGoods(lineNetValue);
        if ((purchaseReturnItem.getSchemeDiscountValue() != null && purchaseReturnItem.getSchemeDiscountValue() > 0)
                || (purchaseReturnItem.getSchemeDiscountPercentage() != null && purchaseReturnItem.getSchemeDiscountPercentage() > 0)) {
          if (SystemConstants.DISCOUNT_IN_PERCENTAGE.equals(purchaseReturnItem.getIsSchemeDiscountInPercentage())) {
            if (purchaseReturnItem.getSchemeDiscountPercentage() != null) {
              lineDiscountValue = (purchaseReturnItem.getSchemeDiscountPercentage() / 100) * lineNetValue;
              purchaseReturnItem.setSchemeDiscountValue(lineDiscountValue);
              purchaseReturnItem.setSchemeDiscountValueDerived(purchaseReturnItem.getSchemeDiscountValue() / purchaseReturnItem.getQuantityReturned());
              lineNetValue -= purchaseReturnItem.getSchemeDiscountValue();
            }
          } else {
            if (purchaseReturnItem.getSchemeDiscountValue() != null) {
              purchaseReturnItem.setSchemeDiscountPercentage((purchaseReturnItem.getSchemeDiscountValue() * 100) / lineNetValue);
              purchaseReturnItem.setSchemeDiscountValueDerived(purchaseReturnItem.getSchemeDiscountValue() / purchaseReturnItem.getQuantityReturned());
              lineNetValue -= purchaseReturnItem.getSchemeDiscountValue();
            }
          }
        }
        if ((purchaseReturnItem.getProductDiscountValue() != null && purchaseReturnItem.getProductDiscountValue() > 0)
                || (purchaseReturnItem.getProductDiscountPerc() != null && purchaseReturnItem.getProductDiscountPerc() > 0)) {
          if (SystemConstants.DISCOUNT_IN_PERCENTAGE.equals(purchaseReturnItem.getIsProductDiscountInPercentage())) {
            if (purchaseReturnItem.getProductDiscountPerc() != null) {
              lineDiscountValue = (purchaseReturnItem.getProductDiscountPerc() / 100) * lineNetValue;
              purchaseReturnItem.setProductDiscountValue(lineDiscountValue);
              purchaseReturnItem.setProductDiscountValueDerived(purchaseReturnItem.getProductDiscountValue() / purchaseReturnItem.getQuantityReturned());
              lineNetValue -= purchaseReturnItem.getProductDiscountValue();
            }
          } else {
            if (purchaseReturnItem.getProductDiscountValue() != null) {
              purchaseReturnItem.setProductDiscountPerc((purchaseReturnItem.getProductDiscountValue() * 100) / lineNetValue);
              purchaseReturnItem.setProductDiscountValueDerived(purchaseReturnItem.getProductDiscountValue() / purchaseReturnItem.getQuantityReturned());
              lineNetValue -= purchaseReturnItem.getProductDiscountValue();
            }
          }
        }
        if ((purchaseReturnItem.getInvoiceDiscountValue() != null && purchaseReturnItem.getInvoiceDiscountValue() > 0)
                || (purchaseReturnItem.getInvoiceDiscountPercentage() != null && purchaseReturnItem.getInvoiceDiscountPercentage() > 0)) {
          if (purchaseReturnItem.getInvoiceDiscountPercentage() != null) {
            if (purchaseReturnItem.getInvoiceDiscountPercentage() != null) {
              lineDiscountValue = (purchaseReturnItem.getInvoiceDiscountPercentage() / 100) * lineNetValue;
              purchaseReturnItem.setInvoiceDiscountValue(lineDiscountValue);
              purchaseReturnItem.setInvoiceDiscountValueDerived(purchaseReturnItem.getInvoiceDiscountValue() / purchaseReturnItem.getQuantityReturned());
              lineNetValue -= purchaseReturnItem.getInvoiceDiscountValue();
            }
          } else {
            if (purchaseReturnItem.getInvoiceDiscountPercentage() != null) {
              purchaseReturnItem.setInvoiceDiscountPercentage((purchaseReturnItem.getInvoiceDiscountValue() * 100) / lineNetValue);
              purchaseReturnItem.setInvoiceDiscountValueDerived(purchaseReturnItem.getInvoiceDiscountValue() / purchaseReturnItem.getQuantityReturned());
              lineNetValue -= purchaseReturnItem.getInvoiceDiscountValue();
            }
          }
        }
        purchaseReturnItem.setValueAssessable(lineNetValue);
        invoiceNetValue += lineNetValue;

        productNetQty += purchaseReturnItem.getQuantityReturned();

        if (purchaseReturnItem.getTaxCode() != null) {
          if (invoiceGroupMap.containsKey(purchaseReturnItem.getTaxCode().getId())) {
            InvoiceGroup group = invoiceGroupMap.get(purchaseReturnItem.getTaxCode().getId());
            group.setInvoiceNetAmount(group.getInvoiceNetAmount() + purchaseReturnItem.getValueGoods());
            group.setAssessableValue(group.getAssessableValue() + purchaseReturnItem.getValueAssessable());
            group.setProductQuantity(group.getProductQuantity() + 1);
            invoiceGroupMap.put(purchaseReturnItem.getTaxCode().getId(), group);
          } else {
            InvoiceGroup group = new InvoiceGroup();
            group.setInvoiceNetAmount(purchaseReturnItem.getValueGoods());
            group.setTaxCode(purchaseReturnItem.getTaxCode());
            group.setAssessableValue(purchaseReturnItem.getValueAssessable());
            group.setProductQuantity(1);
            invoiceGroupMap.put(purchaseReturnItem.getTaxCode().getId(), group);
          }
        }
        purchaseReturnItem.setLandingPricePerPieceCompany(lineNetValue / purchaseReturnItem.getQuantityReturned());
      }
    }

    /**
     * Find the tax values of products.
     */
    double assessableValue;
    for (Map.Entry<Integer, InvoiceGroup> invoiceGroupSet : invoiceGroupMap.entrySet()) {
      invoiceGroup = invoiceGroupSet.getValue();

      for (PurchaseReturnItemReplica purchaseReturnItem : purchaseReturnItemReplicaList) {
        if (purchaseReturnItem != null && purchaseReturnItem.getTaxCode() != null) {
          if (invoiceGroup.getTaxCode().getId().equals(purchaseReturnItem.getTaxCode().getId())) {
            if (invoiceGroup.getInvoiceIgstValue() == null) {

              if (SystemConstants.INTRASTATE_PURCHASE.equals(puchaseReturn.getBusinessArea()) && puchaseReturn.getSezZone().intValue() == 0) {
                for (TaxCode taxCode : invoiceGroup.getTaxCode().getTaxCodeList()) {
                  if (AccountingConstant.TAX_TYPE_CGST == taxCode.getTaxType()) {
                    invoiceGroup.setInvoiceCgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), 2));
                  } else if (AccountingConstant.TAX_TYPE_SGST == taxCode.getTaxType()) {
                    invoiceGroup.setInvoiceSgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), 2));
                  }
                }
                invoiceCgstValue += invoiceGroup.getInvoiceCgstValue();
                invoiceSgstValue += invoiceGroup.getInvoiceSgstValue();
                invoiceTotCgstValue += invoiceGroup.getInvoiceCgstValue();
                invoiceTotCgstValue += invoiceGroup.getInvoiceSgstValue();
                invoiceGroup.setInvoiceIgstValue(MathUtil.roundOff(invoiceGroup.getInvoiceCgstValue() + invoiceGroup.getInvoiceSgstValue(), 2));
              } else {
                invoiceGroup.setInvoiceIgstValue(MathUtil.roundOff(invoiceGroup.getAssessableValue() * (invoiceGroup.getTaxCode().getRatePercentage() / 100) / 2, 2) * 2);
              }
              invoiceIgstValue += invoiceGroup.getInvoiceIgstValue();
            }

            assessableValue = (purchaseReturnItem.getValueAssessable() == null ? 0 : purchaseReturnItem.getValueAssessable());
            if (invoiceGroup.getAssessableValue() != 0) {
              purchaseReturnItem.setValueIgst((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceIgstValue());
              if (SystemConstants.INTRASTATE_PURCHASE.equals(puchaseReturn.getBusinessArea()) && puchaseReturn.getSezZone().intValue() == 0) {
                purchaseReturnItem.setValueCgst((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceCgstValue());
                purchaseReturnItem.setValueSgst((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceSgstValue());
              }
            }
            purchaseReturnItem.setValueNet(purchaseReturnItem.getValueAssessable() + purchaseReturnItem.getValueIgst());
          }
        }
      }
    }

    puchaseReturn.setInvoiceAmountNet(MathUtil.roundOff(invoiceNetValue, 2));
    puchaseReturn.setAssessableValue(MathUtil.roundOff(invoiceNetValue, 2));
    puchaseReturn.setInvoiceAmountIgst(MathUtil.roundOff(invoiceIgstValue, 2));

    if (SystemConstants.INTRASTATE_PURCHASE.equals(puchaseReturn.getBusinessArea()) && puchaseReturn.getSezZone().intValue() == 0) {
      puchaseReturn.setInvoiceAmountCgst(MathUtil.roundOff(invoiceCgstValue, 2));
      puchaseReturn.setInvoiceAmountSgst(MathUtil.roundOff(invoiceSgstValue, 2));
    }

    double grandTotal = MathUtil.roundOff(invoiceNetValue + invoiceIgstValue, 2);
    puchaseReturn.setGrandTotal(grandTotal);
    double invoiceValue = Double.valueOf(Math.round(puchaseReturn.getGrandTotal()));
    double invoiceRoundOff = invoiceValue - grandTotal;
    puchaseReturn.setInvoiceValue(invoiceValue);
    puchaseReturn.setInvoiceAmount(invoiceValue);
    puchaseReturn.setInvoiceRoundOff(MathUtil.roundOff(invoiceRoundOff, 2));
    puchaseReturn.setProductNetQuantity(productNetQty);
    puchaseReturn.setInvoiceGroup(new ArrayList<>(invoiceGroupMap.values()));
  }

  @Override
  public List<ReturnItem> getDamagedAndExpiredProductsForPurchaseReturn(Main main, Product productId, Integer purchaseReturnId, Integer accountId, String productType, Integer purchaseReturnType, Date entryDate) {
    /**
     * The SQL function getProductsDetailsForPurchaseReturnGst(?,?,?) parameters 1. Account Id 2. Product Type(GL,CC) 3. Damaged(1) or Non-Moving(2) flag
     */
    String sql = "select product_name,product_detail_id,product_entry_id,product_entry_detail_id,stock_status_id,sum(coalesce(quantity_available,0)) as quantity_available,sum(coalesce(quantity_free_available,0)) as quantity_free_available,batch_no,expiry_date_actual, "
            + "mrp_value,pack_size,default_pack,primary_pack_id,secondary_pack_id,tertiary_pack_id,ter_to_sec_quantity,sec_to_pri_quantity,primary_pack_dimension,group_dimension_quantity,hsn_code, "
            + "dimension_unit_quantity,dimension_content_quantity,free_qty_scheme_id,value_rate,landing_rate,landing_rate_company,scheme_discount,product_discount,prod_discount_pack,invoice_discount,product_key,product_source_type,"
            + "supp_invoice_no as reference_no,supp_invoice_date as reference_date,ref_sales_invoice_id "
            + "from getproductsdetailsforpurchasereturngst(?,?,?,?,?,?) ";

    String groupBy = " group by product_name,product_detail_id,product_entry_detail_id,product_entry_id,stock_status_id,batch_no,expiry_date_actual,mrp_value,pack_size,default_pack,primary_pack_id,secondary_pack_id,tertiary_pack_id,"
            + "ter_to_sec_quantity,sec_to_pri_quantity,primary_pack_dimension,group_dimension_quantity,dimension_unit_quantity,dimension_content_quantity,free_qty_scheme_id,value_rate,hsn_code,"
            + "landing_rate,landing_rate_company,scheme_discount,product_discount,prod_discount_pack,invoice_discount,product_key,product_source_type,supp_invoice_no,supp_invoice_date,ref_sales_invoice_id ";
    ArrayList<Object> params = new ArrayList<>();
    if (purchaseReturnId != null) {
      sql += " where product_key not in(select product_key from scm_purchase_return_item where purchase_return_id = ?) ";
      params.add(accountId);
      params.add(purchaseReturnType);
      params.add(productType);
      if (productId != null && productId.getId() != null) {
        params.add(productId.getId());
      } else {
        params.add(null);
      }
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(entryDate));
      params.add(0);
      params.add(purchaseReturnId);
    } else {
      params.add(accountId);
      params.add(purchaseReturnType);
      params.add(productType);
      if (productId != null && productId.getId() != null) {
        params.add(productId.getId());
      } else {
        params.add(null);
      }
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(entryDate));
      params.add(0);
    }
    sql += groupBy;
    List<ReturnItem> list = AppDb.getList(main.dbConnector(), ReturnItem.class, sql, params.toArray());
    return list;
  }

  @Override
  public List<ReturnItem> getNonMovingProductsForPurchaseReturn(Main main, Product productId, Integer purchaseReturnId, Integer accountId, String productType, Integer purchaseReturnType, Date entryDate) {
    /**
     * The SQL function getProductsDetailsForPurchaseReturnGst(?,?,?) parameters 1. Account Id 2. Product Type(GL,CC) 3. Damaged(1) or Non-Moving(2) flag
     */
    String sql = "select product_name,product_detail_id,product_entry_id,product_entry_detail_id,stock_status_id,sum(coalesce(quantity_available,0)) as quantity_available,sum(coalesce(quantity_free_available,0)) as quantity_free_available,batch_no,expiry_date_actual, "
            + "mrp_value,pack_size,default_pack,primary_pack_id,secondary_pack_id,tertiary_pack_id,ter_to_sec_quantity,sec_to_pri_quantity,primary_pack_dimension,group_dimension_quantity,hsn_code, "
            + "dimension_unit_quantity,dimension_content_quantity,free_qty_scheme_id,value_rate,landing_rate,landing_rate_company,scheme_discount,product_discount,prod_discount_pack,invoice_discount,product_key,"
            + "supp_invoice_no as reference_no,supp_invoice_date as reference_date,ref_sales_invoice_id "
            + "from getproductsdetailsforpurchasereturngst(?,?,?,?,?,?) ";
    String groupBy = " group by product_name,product_detail_id,product_entry_id,product_entry_detail_id,stock_status_id,batch_no,expiry_date_actual,mrp_value,pack_size,default_pack,primary_pack_id,secondary_pack_id,tertiary_pack_id,"
            + "ter_to_sec_quantity,sec_to_pri_quantity,primary_pack_dimension,group_dimension_quantity,dimension_unit_quantity,dimension_content_quantity,free_qty_scheme_id,value_rate,hsn_code,"
            + "landing_rate,landing_rate_company,scheme_discount,product_discount,prod_discount_pack,invoice_discount,product_key,supp_invoice_no,supp_invoice_date,ref_sales_invoice_id ";
    ArrayList<Object> params = new ArrayList<>();
    if (purchaseReturnId != null) {
      params.add(accountId);
      params.add(purchaseReturnType);
      params.add(productType);
      sql += " where product_key not in(select product_key from scm_purchase_return_item where purchase_return_id = ?) ";
      if (productId != null && productId.getId() != null) {
        params.add(productId.getId());
      } else {
        params.add(null);
      }
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(entryDate));
      params.add(purchaseReturnId);
    } else {
      params.add(accountId);
      params.add(purchaseReturnType);
      params.add(productType);
      if (productId != null && productId.getId() != null) {
        params.add(productId.getId());
      } else {
        params.add(null);
      }
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(entryDate));
    }
    params.add(0);
    sql += groupBy;
    List<ReturnItem> list = AppDb.getList(main.dbConnector(), ReturnItem.class, sql, params.toArray());
    return list;
  }

  @Override
  public List<ProductSummary> productSummaryForSalesReturnAuto(Main main, SalesReturn salesReturn, SalesReturnItem salesReturnItem) {
    List<ProductSummary> list;
    List<Object> params = new ArrayList<>();
    StringBuilder queryString = new StringBuilder();
    params.add(salesReturn.getAccountGroupId().getId());
    params.add(salesReturnItem.getProduct().getId());
    params.add(salesReturn.getCustomerId().getId());
    params.add(salesReturn.getAccountGroupId().getId());
    params.add(SystemConstants.CONFIRMED);
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesReturn.getEntryDate()));
    params.add(salesReturn.getAccountGroupId().getId());
    params.add(salesReturnItem.getProduct().getId());
    params.add(salesReturn.getAccountGroupId().getId());
    params.add(salesReturnItem.getProduct().getId());
//    params.add(salesReturnItem.getProductBatchId().getId());

    queryString.append("select sales_invoice_item_ids,product_id,product_name,batch,pack_size,expiry_date,product_batch_id,sales_invoice_id,mrp_value,gst_tax_code_id, gst_percentage, invoice_no,invoice_date, prod_piece_selling_forced,\n"
            + "scheme_discount_derived,product_discount_derived,invoice_discount_derived, product_qty, product_detail_hash,concat_ws('#',product_id,0,0,product_name,coalesce(sales_invoice_id,0),\n"
            + "coalesce(product_batch_id,0),coalesce(scheme_discount_derived,0),coalesce(product_discount_derived,0)) as product_hash,product_preset_id,account_code, scheme_discount_percentage,\n"
            + "product_discount_percentage,value_ptr,value_pts,returned_qty ,hsn_code,account_status_id from(\n"
            + "select null as sales_invoice_item_ids,\n"
            + "t5.id as product_id,t5.product_name,null as pack_size,null as batch,null as expiry_date,null as product_batch_id,\n"
            + "null as sales_invoice_id, null as mrp_value, t2.sales_tax_code_id as gst_tax_code_id, t3.rate_percentage as gst_percentage,\n"
            + "null as invoice_no, null as invoice_date, null prod_piece_selling_forced,null as scheme_discount_derived, null as product_discount_derived,\n"
            + "null as invoice_discount_derived, null as product_qty, null as product_detail_hash,t1.id as product_preset_id,t6.account_code as account_code,\n"
            + "null as product_discount_percentage,null as scheme_discount_percentage,null as value_ptr,null as value_pts,null as returned_qty,t5.hsn_code,\n"
            + "t6.account_status_id\n"
            + "from scm_product t5\n"
            + "inner join scm_product_category t2 on t5.product_category_id = t2.id\n"
            + "inner join scm_tax_code t3 on t3.id = t2.sales_tax_code_id\n"
            + "inner join scm_product_preset t1 on t1.product_id = t5.id\n"
            + "inner join scm_account t6 on t6.id = t1.account_id and t6.id in(select account_id from scm_account_group_detail where account_group_id = ?)\n"
            + "where t5.id =?")
            .append(" UNION ALL ")
            .append("SELECT STRING_AGG(t1.id::text, ',') as sales_invoice_item_ids,\n"
                    + "t5.id as product_id,t5.product_name as product_name,t7.pack_size as pack_size,t4.batch_no as batch,to_char(t4.expiry_date_actual, 'Mon-yy') as expiry_date,\n"
                    + "t4.id as product_batch_id, t2.id as sales_invoice_id,t4.value_mrp as mrp_value, t1.tax_code_id as gst_tax_code_id, t6.rate_percentage as gst_percentage,\n"
                    + "t2.invoice_no as invoice_no,t2.invoice_date as invoice_date, t1.prod_piece_selling_forced as prod_piece_selling_forced,t1.scheme_discount_derived as scheme_discount_derived\n"
                    + ",t1.product_discount_derived as product_discount_derived,\n");
//-------------------Showing invoice discount + cash discount from sales as invoice_discount_derived (For GECO, must change later)
    if (true) {  //---------ADD required condition later(Should also add condition for the calculation using sales invoice)
      queryString.append("CASE WHEN t1.invoice_discount_derived IS NULL THEN t1.cash_discount_value_derived \n"
              + "ELSE (CASE WHEN t1.cash_discount_value_derived IS NULL THEN t1.invoice_discount_derived \n"
              + "ELSE t1.cash_discount_value_derived+t1.invoice_discount_derived END) END as invoice_discount_derived,");
    } else {
      queryString.append("t1.invoice_discount_derived as invoice_discount_derived,\n");
    }
    queryString.append("sum(coalesce(t1.product_qty,0) + coalesce(t1.product_qty_free,0)) product_qty, string_agg(t3.id::varchar, '#') product_detail_hash,\n"
            + "null as product_preset_id,null as account_code,t1.product_discount_percentage as product_discount_percentage,t1.scheme_discount_percentage ,t1.value_ptr,t1.value_pts,\n"
            + "SUM(COALESCE(t8.returned_qty,0)) as returned_qty,t1.hsn_code,\n"
            + "1 as account_status_id\n"
            + "from scm_sales_invoice_item t1\n"
            + "inner join scm_sales_invoice t2 on t1.sales_invoice_id = t2.id and t2.customer_id = ? and t2.account_group_id = ? and t2.sales_invoice_status_id >= ?\n"
            + "and t2.invoice_entry_date::date <= to_date(?, 'YYYY-MM-DD') inner join scm_product_detail t3 on t3.id = t1.product_detail_id\n"
            + "inner join scm_product_batch t4 on t4.id = t3.product_batch_id inner join scm_product t5 on t5.id = t4.product_id\n"
            + "inner join scm_tax_code t6 on t6.id = t1.tax_code_id inner join scm_product_entry_detail t7 on t7.id=t1.product_entry_detail_id\n"
            + "left join (SELECT sales_invoice_item_id,SUM(COALESCE(product_quantity,0)+COALESCE(product_quantity_damaged,0)) as returned_qty FROM  scm_sales_return_item,\n"
            + "scm_sales_return  WHERE scm_sales_return_item.sales_return_id =scm_sales_return.id AND sales_invoice_item_id IS NOT NULL AND \n"
            + "scm_sales_return.account_group_id = ? AND scm_sales_return.sales_return_status_id=2 GROUP BY sales_invoice_item_id) as t8  ON t8.sales_invoice_item_id=t1.id \n"
            + "where t5.id = ? group by t5.id,t1.hsn_code,t5.product_name,t7.pack_size,t4.batch_no,t4.expiry_date_actual,\n"
            + "t4.id,t2.id,t4.value_mrp,t1.tax_code_id,t6.rate_percentage, t2.invoice_no,t2.invoice_date,t1.prod_piece_selling_forced,\n"
            + "t1.scheme_discount_derived,t1.product_discount_derived,t1.invoice_discount_derived,t1.scheme_discount_percentage,t1.product_discount_percentage,t1.value_ptr,t1.value_pts,"
            + "t1.cash_discount_value_derived\n"
    )
            .append(" union all ")
            .append("select null as sales_invoice_item_ids,t5.id as product_id,t5.product_name,t8.pack_size,t4.batch_no as batch,to_char(t4.expiry_date_actual, 'Mon-yy') as expiry_date,\n"
                    + "t4.id as product_batch_id, null as sales_invoice_id, t4.value_mrp as mrp_value, t2.sales_tax_code_id as gst_tax_code_id,\n"
                    + "t3.rate_percentage as gst_percentage, null as invoice_no, null as invoice_date, null prod_piece_selling_forced,null as scheme_discount_derived,\n"
                    + "null as product_discount_derived, null as invoice_discount_derived, null as product_qty, t7.id::varchar as product_detail_hash,t1.id as product_preset_id,\n"
                    + "t6.account_code, null as product_discount_percentage,null as scheme_discount_percentage, null as value_ptr,null as value_pts,null as returned_qty,t5.hsn_code,\n"
                    + "t6.account_status_id from scm_product t5\n"
                    + "inner join scm_product_category t2 on t5.product_category_id = t2.id inner join scm_tax_code t3 on t3.id = t2.sales_tax_code_id\n"
                    + "inner join scm_product_preset t1 on t1.product_id = t5.id inner join scm_account t6 on t6.id = t1.account_id\n"
                    + "and t6.id in(select account_id from scm_account_group_detail where account_group_id = ?)inner join scm_product_batch t4 on t4.product_id = t5.id\n"
                    + "left join scm_product_detail t7 on t7.product_batch_id = t4.id and t7.account_id = t6.id\n"
                    + "left join scm_product_entry_detail t8 on t8.product_detail_id=t7.id where t5.id =?)tab\n"
                    + "group by sales_invoice_item_ids,product_id,hsn_code,product_name,batch,pack_size,expiry_date,product_batch_id,sales_invoice_id,mrp_value,gst_tax_code_id, gst_percentage,\n"
                    + "invoice_no,invoice_date, prod_piece_selling_forced,scheme_discount_derived,product_discount_derived,invoice_discount_derived,\n"
                    + "product_qty, product_detail_hash, product_hash,product_preset_id,account_code,\n"
                    + "scheme_discount_percentage,product_discount_percentage,value_ptr,value_pts,returned_qty,account_status_id order by batch");

    list = AppDb.getList(main.dbConnector(), ProductSummary.class, queryString.toString(), params.toArray());

    return list;
  }

  @Override
  public void updateSalesReturnItem(Main main, SalesReturnItem salesReturnitem) {
    Product product = null;
    ProductSummary productBatchSummary = salesReturnitem.getProductBatchSummary();

    if (salesReturnitem.getProduct() != null) {
      product = salesReturnitem.getProduct();
      if (salesReturnitem.getCurrentTaxCode() == null) {
        salesReturnitem.setTaxCodeId(getProductPurchaseTaxCode(main, product));
        salesReturnitem.setCurrentTaxCode(salesReturnitem.getTaxCodeId());
      }

    }

    if (productBatchSummary.getProductBatchId() != null) {
      salesReturnitem.setProductBatchId(ProductBatchService.selectByPk(main, new ProductBatch(productBatchSummary.getProductBatchId())));
      salesReturnitem.setValueMrp(salesReturnitem.getProductBatchId().getValueMrp());
      if (salesReturnitem.getProductSummary() == null && salesReturnitem.getProductBatchId() != null) {
        salesReturnitem.setProductSummary(new ProductSummary(salesReturnitem.getProductBatchId().getProductId().getId(), salesReturnitem.getProductBatchId().getId(), salesReturnitem.getProductBatchId().getProductId().getProductName()));
      }
    }

    if (productBatchSummary.getProductDetailHash() != null) {
      // Set Product Details
      salesReturnitem.setProductDetailHash(productBatchSummary.getProductDetailHash());

      ProductDetail productDetail = ProductDetailService.selectByPk(main, new ProductDetail(Integer.parseInt(productBatchSummary.getProductDetailHash().split("#")[0])));
      salesReturnitem.setProductPreset(productDetail.getProductPresetId());
      salesReturnitem.setAccountId(productDetail.getAccountId());
      //productBatch = ProductBatchService.selectByPk(main, new ProductBatch(salesReturnitem.getProductSummary().getProductBatchId()));
      //salesReturnitem.setProductBatchId(productBatch);
      //salesReturnitem.setProductDetailId(productDetail);
      //salesReturnitem.setProduct(productBatch.getProductId());
      //salesReturnitem.setValueMrp(productBatch.getValueMrp());
      //salesReturnitem.getProductSummary().setProductName(salesReturnitem.getProduct().getProductName());
    } else {
      salesReturnitem.setProductDetailId(null);
      salesReturnitem.setProductDetailHash(null);
    }

    if (productBatchSummary.getProductPresetId() != null) {
      salesReturnitem.setProductPreset(ProductPresetService.selectByPk(main, new ProductPreset(productBatchSummary.getProductPresetId())));
      salesReturnitem.setAccountId(salesReturnitem.getProductPreset().getAccountId());
    }

    if (productBatchSummary.getSalesInvoiceId() != null) {
      salesReturnitem.setSalesInvoiceId(SalesInvoiceService.selectByPk(main, new SalesInvoice(productBatchSummary.getSalesInvoiceId())));
    }
    salesReturnitem.setSchemeDiscountValueDerived(productBatchSummary.getSchemeDiscountDerived());
    salesReturnitem.setSchemeDiscountPercentage(productBatchSummary.getSchemeDiscountPercentage());
    salesReturnitem.setProductDiscountValueDerived(productBatchSummary.getProductDiscountDerived());
    salesReturnitem.setProductDiscountPercentage(productBatchSummary.getProductDiscountPercentage());
    salesReturnitem.setInvoiceDiscountValueDerived(productBatchSummary.getInvoiceDiscountDerived());
    if (product != null) {
      salesReturnitem.setPackDescription(product.getPackingDescription());
    }
    salesReturnitem.getProductBatchSummary().after();
    salesReturnitem.setProductHashCode(salesReturnitem.getProductBatchSummary().getProductCode());
  }

  @Override
  public void processSalesServicesInvoiceCalculation(SalesServicesInvoice salesServicesInvoice, List<SalesServicesInvoiceItem> salesServicesInvoiceItemList) {
    Double invoiceNetValue = 0.0;
    Double invoiceIgstValue = 0.0, invoiceCgstValue = 0.0, invoiceSgstValue = 0.0;
    Double invoiceTdsValue = 0.0;
    Double invoiceAssessableValue = 0.0;

    HashMap<Integer, InvoiceGroup> invoiceGroupMap = new HashMap<>();
    InvoiceGroup invoiceGroup;

    for (SalesServicesInvoiceItem salesServicesInvoiceItem : salesServicesInvoiceItemList) {
      if (salesServicesInvoiceItem.getCommodityId() != null && salesServicesInvoiceItem.getTaxableValue() != null) {

        if (salesServicesInvoice.getTdsTaxCodeId() != null) {
          salesServicesInvoiceItem.setTdsTaxCodeId(salesServicesInvoice.getTdsTaxCodeId());
        }

        if (salesServicesInvoiceItem.getTdsTaxCodeId() != null) {
          salesServicesInvoiceItem.setTdsAmount(salesServicesInvoiceItem.getTaxableValue() * salesServicesInvoiceItem.getTdsTaxCodeId().getRatePercentage() / 100);
          invoiceTdsValue += salesServicesInvoiceItem.getTdsAmount();
        }

        salesServicesInvoiceItem.setIgstAmount(MathUtil.roundOff((salesServicesInvoiceItem.getTaxableValue() * salesServicesInvoiceItem.getIgstId().getRatePercentage() / 100), 2));
        salesServicesInvoiceItem.setIgstAmount(MathUtil.roundOff(salesServicesInvoiceItem.getIgstAmount(), 2));
        if (SalesServicesInvoiceService.SALES_INTRASTATE.equals(salesServicesInvoice.getSalesArea())) {
          if (!StringUtil.isEmpty(salesServicesInvoiceItem.getIgstId().getTaxCodeList())) {
            for (TaxCode taxCode : salesServicesInvoiceItem.getIgstId().getTaxCodeList()) {
              if (AccountingConstant.TAX_TYPE_CGST == taxCode.getTaxType()) {
                salesServicesInvoiceItem.setCgstAmount(MathUtil.roundOff((salesServicesInvoiceItem.getTaxableValue() * (taxCode.getRatePercentage() / 100)), 2));
              } else if (AccountingConstant.TAX_TYPE_SGST == taxCode.getTaxType()) {
                salesServicesInvoiceItem.setSgstAmount(MathUtil.roundOff((salesServicesInvoiceItem.getTaxableValue() * (taxCode.getRatePercentage() / 100)), 2));
              }
            }
          } else {
            salesServicesInvoiceItem.setCgstAmount(salesServicesInvoiceItem.getIgstAmount() / 2);
            salesServicesInvoiceItem.setSgstAmount(salesServicesInvoiceItem.getIgstAmount() / 2);
          }
          invoiceCgstValue += salesServicesInvoiceItem.getCgstAmount();
          invoiceSgstValue += salesServicesInvoiceItem.getSgstAmount();
        }

        salesServicesInvoiceItem.setNetValue((salesServicesInvoiceItem.getTaxableValue() + salesServicesInvoiceItem.getIgstAmount()) - (salesServicesInvoiceItem.getTdsAmount() == null ? 0 : salesServicesInvoiceItem.getTdsAmount()));

        invoiceAssessableValue += salesServicesInvoiceItem.getTaxableValue();
        invoiceIgstValue += salesServicesInvoiceItem.getIgstAmount();
        invoiceNetValue += salesServicesInvoiceItem.getNetValue();

        if (salesServicesInvoiceItem.getIgstId() != null) {
          if (invoiceGroupMap.containsKey(salesServicesInvoiceItem.getIgstId().getId())) {
            InvoiceGroup group = invoiceGroupMap.get(salesServicesInvoiceItem.getIgstId().getId());
            group.setInvoiceIgstValue(group.getInvoiceIgstValue() + salesServicesInvoiceItem.getIgstAmount());
            group.setInvoiceTdsValue((salesServicesInvoiceItem.getTdsAmount() == null ? 0.0 : salesServicesInvoiceItem.getTdsAmount()) + group.getInvoiceTdsValue());
            if (SalesServicesInvoiceService.SALES_INTRASTATE.intValue() == (salesServicesInvoice.getSalesArea())) {
              group.setInvoiceCgstValue(group.getInvoiceCgstValue() + salesServicesInvoiceItem.getCgstAmount());
              group.setInvoiceSgstValue(group.getInvoiceSgstValue() + salesServicesInvoiceItem.getSgstAmount());
            }
            group.setAssessableValue(group.getAssessableValue() + salesServicesInvoiceItem.getTaxableValue());
            group.setInvoiceNetAmount(group.getInvoiceNetAmount() + salesServicesInvoiceItem.getNetValue());
            group.setProductQuantity(group.getProductQuantity() + 1);
            invoiceGroupMap.put(salesServicesInvoiceItem.getIgstId().getId(), group);
          } else {
            InvoiceGroup group = new InvoiceGroup();
            group.setTaxCode(salesServicesInvoiceItem.getIgstId());
            group.setTdsTaxCode(salesServicesInvoiceItem.getTdsTaxCodeId());
            group.setInvoiceIgstValue(salesServicesInvoiceItem.getIgstAmount());
            group.setInvoiceTdsValue(salesServicesInvoiceItem.getTdsAmount() == null ? 0.0 : salesServicesInvoiceItem.getTdsAmount());
            if (SalesServicesInvoiceService.SALES_INTRASTATE.intValue() == (salesServicesInvoice.getSalesArea())) {
              group.setInvoiceCgstValue(salesServicesInvoiceItem.getCgstAmount());
              group.setInvoiceSgstValue(salesServicesInvoiceItem.getSgstAmount());
            }
            group.setAssessableValue(salesServicesInvoiceItem.getTaxableValue());
            group.setInvoiceNetAmount(salesServicesInvoiceItem.getNetValue());
            group.setProductQuantity(1);
            invoiceGroupMap.put(salesServicesInvoiceItem.getIgstId().getId(), group);
          }
        }
      }
    }

    salesServicesInvoice.setAssessableValue(invoiceAssessableValue);
    salesServicesInvoice.setTdsValue(invoiceTdsValue);
    salesServicesInvoice.setIgstValue(invoiceIgstValue);
    if (SalesServicesInvoiceService.SALES_INTRASTATE.equals(salesServicesInvoice.getSalesArea())) {
      salesServicesInvoice.setCgstValue(invoiceCgstValue);
      salesServicesInvoice.setSgstValue(invoiceSgstValue);
    }
//    if (invoiceTdsValue != null && invoiceTdsValue > 0) {
//      invoiceNetValue = invoiceNetValue - invoiceTdsValue;
//    }
    salesServicesInvoice.setGrandTotal(invoiceNetValue);
    salesServicesInvoice.setNetValue(Double.valueOf(Math.round(salesServicesInvoice.getGrandTotal())));
    salesServicesInvoice.setRoundOffValue(salesServicesInvoice.getNetValue() - invoiceNetValue);
    salesServicesInvoice.setInvoiceGroup(new ArrayList<>(invoiceGroupMap.values()));
  }

  @Override
  public void processDebitNoteCalculation(DebitCreditNote debitCreditNote, List<DebitCreditNoteItem> debitCreditNoteItemList) {
    Double invoiceNetValue = 0.0;
    Double invoiceIgstValue = 0.0, invoiceCgstValue = 0.0, invoiceSgstValue = 0.0;
    Double invoiceAssessableValue = 0.0;
    Double grandTotal = 0.0;
    Double roundOff = 0.0;

    HashMap<Integer, InvoiceGroup> invoiceGroupMap = new HashMap<>();

    if (!StringUtil.isEmpty(debitCreditNoteItemList)) {

      for (DebitCreditNoteItem debitCreditNoteItem : debitCreditNoteItemList) {

        if (debitCreditNoteItem.getTitle() != null && debitCreditNoteItem.getTaxableValue() != null) {

          debitCreditNoteItem.setNetValue(debitCreditNoteItem.getTaxableValue());

          if (debitCreditNoteItem.getTaxCodeId() != null) {
            debitCreditNoteItem.setIgstAmount(debitCreditNoteItem.getTaxableValue() * debitCreditNoteItem.getTaxCodeId().getRatePercentage() / 100);
            debitCreditNoteItem.setIgstAmount(MathUtil.roundOff(debitCreditNoteItem.getIgstAmount() / 2, 2) * 2);
            if (debitCreditNote.getSalesArea() != null && (SystemConstants.INTRASTATE.intValue() == debitCreditNote.getSalesArea())) {

              if (!StringUtil.isEmpty(debitCreditNoteItem.getTaxCodeId().getTaxCodeList())) {
                for (TaxCode taxCode : debitCreditNoteItem.getTaxCodeId().getTaxCodeList()) {
                  if (AccountingConstant.TAX_TYPE_CGST == taxCode.getTaxType()) {
                    debitCreditNoteItem.setCgstAmount(MathUtil.roundOff((debitCreditNoteItem.getTaxableValue() * (taxCode.getRatePercentage() / 100)), 2));
                  } else if (AccountingConstant.TAX_TYPE_SGST == taxCode.getTaxType()) {
                    debitCreditNoteItem.setSgstAmount(MathUtil.roundOff((debitCreditNoteItem.getTaxableValue() * (taxCode.getRatePercentage() / 100)), 2));
                  }
                }
              } else {
                debitCreditNoteItem.setCgstAmount(debitCreditNoteItem.getIgstAmount() / 2);
                debitCreditNoteItem.setSgstAmount(debitCreditNoteItem.getIgstAmount() / 2);
              }
              invoiceCgstValue += debitCreditNoteItem.getCgstAmount();
              invoiceSgstValue += debitCreditNoteItem.getSgstAmount();

            }
            debitCreditNoteItem.setNetValue((debitCreditNoteItem.getTaxableValue() + debitCreditNoteItem.getIgstAmount()));
            invoiceIgstValue += debitCreditNoteItem.getIgstAmount();
          }

          invoiceAssessableValue += debitCreditNoteItem.getTaxableValue();
          invoiceNetValue += debitCreditNoteItem.getNetValue();

          if (debitCreditNoteItem.getTaxCodeId() != null) {
            if (invoiceGroupMap.containsKey(debitCreditNoteItem.getTaxCodeId().getId())) {
              InvoiceGroup group = invoiceGroupMap.get(debitCreditNoteItem.getTaxCodeId().getId());
              group.setInvoiceIgstValue(group.getInvoiceIgstValue() + debitCreditNoteItem.getIgstAmount());

              if (debitCreditNote.getSalesArea() != null && (SystemConstants.INTRASTATE.intValue() == (debitCreditNote.getSalesArea()))) {
                group.setInvoiceCgstValue(group.getInvoiceCgstValue() + debitCreditNoteItem.getCgstAmount());
                group.setInvoiceSgstValue(group.getInvoiceSgstValue() + debitCreditNoteItem.getSgstAmount());
              }
              group.setAssessableValue(group.getAssessableValue() + debitCreditNoteItem.getTaxableValue());
              group.setInvoiceNetAmount(group.getInvoiceNetAmount() + debitCreditNoteItem.getNetValue());
              group.setProductQuantity(group.getProductQuantity() + 1);
              invoiceGroupMap.put(debitCreditNoteItem.getTaxCodeId().getId(), group);
            } else {
              InvoiceGroup group = new InvoiceGroup();
              group.setTaxCode(debitCreditNoteItem.getTaxCodeId());
              group.setInvoiceIgstValue(debitCreditNoteItem.getIgstAmount());

              if (debitCreditNote.getSalesArea() != null && (SystemConstants.INTRASTATE.intValue() == (debitCreditNote.getSalesArea()))) {
                group.setInvoiceCgstValue(debitCreditNoteItem.getCgstAmount());
                group.setInvoiceSgstValue(debitCreditNoteItem.getSgstAmount());
              }
              group.setAssessableValue(debitCreditNoteItem.getTaxableValue());
              group.setInvoiceNetAmount(debitCreditNoteItem.getNetValue());
              group.setProductQuantity(1);
              invoiceGroupMap.put(debitCreditNoteItem.getTaxCodeId().getId(), group);
            }
          }
        }
      }
    }
    debitCreditNote.setAssessableValue(invoiceAssessableValue);
    debitCreditNote.setIgstValue(invoiceIgstValue);
    if (debitCreditNote.getSalesArea() != null && (SystemConstants.INTRASTATE.intValue() == debitCreditNote.getSalesArea())) {
      debitCreditNote.setCgstValue(invoiceCgstValue);
      debitCreditNote.setSgstValue(invoiceSgstValue);
    }
    grandTotal = invoiceNetValue;
    invoiceNetValue = Double.valueOf(Math.round(grandTotal));
    roundOff = invoiceNetValue - grandTotal;
    debitCreditNote.setRoundOff(roundOff);
    debitCreditNote.setGrandTotal(grandTotal);
    debitCreditNote.setNetValue(invoiceNetValue);
    debitCreditNote.setInvoiceGroup(new ArrayList<>(invoiceGroupMap.values()));
  }

  @Override
  public void processCreditNoteCalculation(DebitCreditNote debitCreditNote, List<DebitCreditNoteItem> debitCreditNoteItemList) {
  }

  /**
   *
   * @param productEntry
   * @param productEntryDetailList
   */
  @Override
  public void processSalesReturnProductEntryCalculation(ProductEntry productEntry, List<ProductEntryDetail> productEntryDetailList) {

    Double invoiceGoodsValue = 0.0;
    Double invoiceNetValue = 0.0;
    Double lineNetValue;

    Double invoiceValue;
    Double invoiceRoundOff;

    Double productActualQty;
    int productActualFreeQty;
    //Double invoiceDiscountValue = null;
    double actualLandingRate = 0.0;

    boolean schemeApplicable = SystemConstants.FREE_SCHEME.equals(productEntry.getQuantityOrFree());

    for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
      if (productEntryDetail.getProductBatchId() != null && productEntryDetail.getProductQuantity() != null && productEntryDetail.getValueRate() != null) {

        productEntryDetail.setSchemeDiscountValueDerived(null);
        productEntryDetail.setProductDiscountValueDerived(null);
        productEntryDetail.setInvoiceDiscountValueDerived(null);
        productEntryDetail.setCashDiscountValueDerived(null);
        productEntryDetail.setValueSgst(0.0);
        productEntryDetail.setValueCgst(0.0);
        productEntryDetail.setValueIgst(0.0);

        if (schemeApplicable) {
          productActualQty = productEntryDetail.getProductQuantity() == null ? 0 : productEntryDetail.getProductQuantity();
          productActualFreeQty = 0;
        } else {
          productActualFreeQty = productEntryDetail.getProductQuantityFree() == null ? 0 : productEntryDetail.getProductQuantityFree();
          productActualQty = (productEntryDetail.getProductQuantity() == null ? 0 : productEntryDetail.getProductQuantity()) + productActualFreeQty;
        }

        lineNetValue = productActualQty * productEntryDetail.getValueRate();
        invoiceGoodsValue += lineNetValue;
        productEntryDetail.setValueGoods(lineNetValue);
        actualLandingRate = productEntryDetail.getValueRate();

        /**
         * Scheme Discount.
         *
         */
        if (!schemeApplicable && productEntryDetail.getSchemeDiscountPercentage() != null && productEntryDetail.getSchemeDiscountPercentage() > 0) {
          productEntryDetail.setIsSchemeDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
          productEntryDetail.setIsSchemeDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
          productEntryDetail.setSchemeDiscountValueDerived((actualLandingRate * productEntryDetail.getSchemeDiscountPercentage() / 100));
          productEntryDetail.setSchemeDiscountValue(productEntryDetail.getSchemeDiscountValueDerived() * 100);
          actualLandingRate -= productEntryDetail.getSchemeDiscountValueDerived();
        }

        // Applying Product Discount
        if ((productEntryDetail.getDiscountPercentage() != null && productEntryDetail.getDiscountPercentage() > 0)) {
          productEntryDetail.setIsProductDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
          productEntryDetail.setIsLineDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
          productEntryDetail.setProductDiscountValueDerived((actualLandingRate * productEntryDetail.getDiscountPercentage() / 100));
          productEntryDetail.setDiscountValue(productEntryDetail.getProductDiscountValueDerived() * 100);
          actualLandingRate -= productEntryDetail.getProductDiscountValueDerived();
        }

        if (productEntryDetail.getInvoiceDiscountValue() != null && productEntryDetail.getInvoiceDiscountValue() > 0) {
          productEntryDetail.setInvoiceDiscountValueDerived((productEntryDetail.getInvoiceDiscountValue() / productActualQty));
          // invoiceDiscountValue = (invoiceDiscountValue != null) ? (invoiceDiscountValue + productEntryDetail.getInvoiceDiscountValue()) : productEntryDetail.getInvoiceDiscountValue();
          //actualSellingPriceDerived -= productEntryDetail.getInvoiceDiscountValueDerived();
        }

        productEntryDetail.setValueNet(lineNetValue);
        if (productActualQty != null && productActualQty != 0) {
          productEntryDetail.setValueAssessable(lineNetValue / productActualQty);
        } else {
          productEntryDetail.setValueAssessable(0.0);
        }
        productEntryDetail.setLandingPricePerPieceCompany(actualLandingRate);
        productEntryDetail.setValueRatePerProdPieceDer(actualLandingRate);
        invoiceNetValue += lineNetValue;

      }
    }

    productEntry.setInvoiceAmountGoods(MathUtil.roundOff(invoiceGoodsValue, 2));
    productEntry.setInvoiceAmountNet(MathUtil.roundOff(invoiceNetValue, 2));
    productEntry.setInvoiceAmountAssessable(MathUtil.roundOff(invoiceNetValue, 2));
    // productEntry.setAssessableValue(MathUtil.roundOff(assessableValue, 2));
    productEntry.setInvoiceAmountIgst(0.0);
    productEntry.setInvoiceAmountCgst(0.0);
    productEntry.setInvoiceAmountSgst(0.0);

    double grandTotal = MathUtil.roundOff(invoiceNetValue, 2);
    productEntry.setGrandTotal(grandTotal);
    invoiceValue = Double.valueOf(Math.round(productEntry.getGrandTotal()));
    invoiceRoundOff = invoiceValue - grandTotal;
    productEntry.setInvoiceValue(invoiceValue);
    if (productEntry.getInvoiceAmount() != null && invoiceValue != null) {
      productEntry.setInvoiceAmountVariation(productEntry.getInvoiceAmount() - invoiceValue);
    }
    productEntry.setInvoiceRoundOff(MathUtil.roundOff(invoiceRoundOff, 2));

  }

  @Override
  public void saveVendorClaim(Main main, VendorClaim vendorClaim, List<VendorClaimDetail> claimDetailList) {
    LedgerExternalDataService.saveVendorClaim(main, vendorClaim, claimDetailList);
  }

  @Override
  public List<ProductSummary> productSummaryBatchAuto(Main main, Account account, String filter, Integer productEntryId, Integer productDetailId, Integer productId) {
    String condition = "";
    String prodCond = "";
    StringBuilder sql = new StringBuilder();
    List<ProductSummary> list;
    filter = "%" + filter.trim() + "%";
    List<Object> params = new ArrayList<>();
    params.add(account.getId());
    if (productId != null) {
      params.add(productId);
    }
    params.add(account.getVendorId().getId());
    params.add(account.getVendorId().getId());
    params.add(account.getId());
    params.add(account.getId());
    if (productId != null) {
      params.add(productId);
    }
    params.add(account.getVendorId().getId());
    params.add(account.getVendorId().getId());
//    params.add(productEntryId);

    if (productDetailId != null) {
      //condition = " and t7.id not in(select product_detail_id from scm_product_entry_detail where product_entry_id = ? and product_detail_id <> ?) ";
      condition = " AND scm_product_detail.id != ? ";
//      params.add(productDetailId);
    }
    if (productId != null) {
      prodCond = " prod.id = ? AND ";
    }

    params.add(filter);

    sql.append("SELECT prod.id as product_id, \n"
            + "NULL as product_detail_id,NULL as product_batch_id,prod.product_name,NULL as batch,NULL as expiry_date,NULL as mrp_value, \n"
            + "tax_code.rate_percentage as gst_percentage, \n"
            + "prod_cat.purchase_tax_code_id as gst_tax_code_id,getPackDimension(prod.id) as pack_size, prod_preset.id as productPresetId \n"
            + "FROM scm_product as prod \n"
            + "INNER JOIN scm_product_preset prod_preset on prod.id = prod_preset.product_id and prod_preset.account_id = ? \n"
            + "INNER JOIN scm_product_category prod_cat on prod.product_category_id = prod_cat.id \n"
            + "INNER JOIN scm_tax_code tax_code on tax_code.id = prod_cat.purchase_tax_code_id \n"
            + "WHERE ").append(prodCond).append("prod.brand_id IN( SELECT brand_id FROM  scm_supplier_brand WHERE vendor_id = ?) \n"
            + "AND prod.commodity_id IN(SELECT commodity_id FROM scm_vendor_commodity WHERE vendor_id = ?) \n"
            + "UNION ALL \n"
            + "SELECT prod.id as product_id,prod_det.id,prod_batch.id,prod.product_name,prod_batch.batch_no as batch,to_char(prod_batch.expiry_date_actual, 'Mon-yy') as expiry_date \n"
            + ",prod_batch.value_mrp as mrp_value,tax_code.rate_percentage as gst_percentage, \n"
            + "prod_cat.purchase_tax_code_id as gst_tax_code_id,getPackDimension(prod.id) as pack_size, prod_preset.id as productPresetId \n"
            + "FROM scm_product as prod \n"
            + "INNER JOIN scm_product_preset prod_preset on prod.id = prod_preset.product_id and prod_preset.account_id = ? \n"
            + "INNER JOIN scm_product_category prod_cat on prod.product_category_id = prod_cat.id \n"
            + "INNER JOIN scm_tax_code tax_code on tax_code.id = prod_cat.purchase_tax_code_id \n"
            + "INNER JOIN scm_product_batch as prod_batch  ON prod.id = prod_batch.product_id \n"
            //            + "AND prod_batch.expiry_date_actual >= now() "
            + "LEFT JOIN scm_product_detail as prod_det ON prod_batch.id = prod_det.product_batch_id  AND prod_det.account_id = ? \n"
            + "WHERE ").append(prodCond).append("brand_id IN( SELECT brand_id FROM  scm_supplier_brand WHERE vendor_id = ? ) \n"
            + "AND prod.commodity_id IN(SELECT commodity_id FROM scm_vendor_commodity WHERE vendor_id = ?) \n"
            + " AND UPPER(prod_batch.batch_no) LIKE UPPER(?) ORDER BY 4,3");

    list = AppDb.getList(main.dbConnector(), ProductSummary.class, sql.toString(), params.toArray());

    return list;
  }

  @Override
  public void calculateTcsonSales(SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItemList, Double customerNetAmount, TaxCode taxCode) {
    if (customerNetAmount != null) {
      Double invoiceRoundOff = salesInvoice.getRoundOff() != null ? salesInvoice.getRoundOff() : (salesInvoice.getRoundOffForced() != null ? salesInvoice.getRoundOffForced() : 0.0);
      Double invoiceValue = salesInvoice.getInvoiceAmountSubtotal();
      Double grandTotal = 0.0;
      salesInvoice.setTcsApplicableAmount(calculateTcsApplicableAmount(customerNetAmount, salesInvoice.getInvoiceAmountSubtotal()));
      if (salesInvoice.getTcsApplicableAmount() != null) {
        CessProcessor cessProcessor = new SalesInvoiceCessProcessorImpl();
        salesInvoice.setTcsTaxCodeId(taxCode);
        salesInvoice.setTcsNetValue(cessProcessor.processSalesInvoiceCess(taxCode, invoiceValue));
        invoiceValue += salesInvoice.getTcsNetValue();
        grandTotal = MathUtil.roundOff(invoiceValue, 2);
        invoiceValue = Double.valueOf(Math.round(grandTotal));
        invoiceRoundOff = invoiceValue - grandTotal;
        salesInvoice.setInvoiceValue(invoiceValue);
        salesInvoice.setInvoiceAmount(invoiceValue);
        salesInvoice.setInvoiceRoundOff(MathUtil.roundOff(invoiceRoundOff, 2));
        salesInvoice.setRoundOff(salesInvoice.getInvoiceRoundOff());
        int productActualQty = 0;
        for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItemList) {
          if (!(salesInvoiceItem.getQuantityOrFree() == SystemConstants.FREE_SCHEME)) {
            productActualQty = salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
            if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() > 0) {
              productActualQty += salesInvoiceItem.getProductQtyFree();
            }
          } else {
            productActualQty = salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
          }
          salesInvoiceItem.setTcsValue(salesInvoiceItem.getValueTaxable() / salesInvoice.getInvoiceAmountAssessable() * salesInvoice.getTcsNetValue());
          salesInvoiceItem.setTcsValueDerived(salesInvoiceItem.getTcsValue() / productActualQty);
        }
      }
    }
  }

  private Double calculateTcsApplicableAmount(Double netSalesorPurchase, Double currentInvoiceAmount) {
    Double tcsApplicableAmount = 0.0;
    if (StringUtil.gtDouble(netSalesorPurchase, SystemConstants.TCS_APPLICABLE_AMOUNT)) {
      tcsApplicableAmount = currentInvoiceAmount;
    } else if (StringUtil.gtDouble((netSalesorPurchase + currentInvoiceAmount), SystemConstants.TCS_APPLICABLE_AMOUNT)) {
      tcsApplicableAmount = (netSalesorPurchase + currentInvoiceAmount) - SystemConstants.TCS_APPLICABLE_AMOUNT;
    } else {
      tcsApplicableAmount = null;
    }
    return tcsApplicableAmount;

  }

  @Override
  public List<ProductDetailSales> getProductDetailCurrentStockAvailabilityforSave(Main main, SalesInvoice salesInvoice) {
    String sql = "select string_agg(stock_id::varchar, '#') stockSaleableId, product_entry_detail_id,purchase_invoice,product_detail_id, sum(quantity_available) quantity_available,  sum(quantity_free_available) quantity_free_available, "
            + "concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) as product_hash, "
            + "expiry_date_actual,product_detail_id,batch_no, mrp_value,pricelist_pts,free_qty_scheme_id,scheme_discount_per,product_discount_per,pack_size, "
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity,account_id,marg_dev_per as margin_value_deviation_der,landing_rate "
            + "from getproductsdetailsforgstsaleconfirm(?,?,?,?,?) "
            + "group by product_entry_detail_id,purchase_invoice,product_batch_id,expiry_date_actual,product_detail_id,batch_no, mrp_value,pricelist_pts,pricelist_ptr,free_qty_scheme_id,scheme_discount_per,product_discount_per,pack_size, "
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity,account_id,product_entry_date,marg_dev_per,landing_rate "
            + "order by expiry_date_actual::date ASC, product_entry_date ASC";

    List<ProductDetailSales> productDetailSales = AppDb.getList(main.dbConnector(), ProductDetailSales.class, sql,
            new Object[]{salesInvoice.getAccountGroupId() == null ? null : salesInvoice.getAccountGroupId().getId(),
              salesInvoice.getAccountGroupPriceListId() == null ? null : salesInvoice.getAccountGroupPriceListId().getId(),
              salesInvoice.getId(), salesInvoice.getSalesInvoiceStatusId().getId(), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoice.getInvoiceEntryDate())});
//    System.out.println(accountGroup.getId()+ ","+
//               product.getId()+ ","+
//              accountGroupPriceList.getId()+ ","+
//              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoiceItem.getSalesInvoiceId().getInvoiceEntryDate())+ ","+
//              salesInvoiceItem.getProductHash());
    return productDetailSales;
  }

  @Override
  public List<ProductDetailSales> selectProductDetailSalesListForConfirm(Main main, SalesInvoice salesInvoice) {
    /**
     * SQL Function - getproductsdetailsforsaleconfirm(?,?,?,?) params. 1. Account Group Id. 2. Account Group price list id. 3. SalesInvoice id. 4. Sales invoice status id.
     *
     */
    List<ProductDetailSales> productDetailSalesList = AppDb.getList(main.dbConnector(), ProductDetailSales.class,
            "SELECT batch_no, expiry_date_actual,to_char(expiry_date_actual,'Mon-yy') expiry_date, "
            + "mrp_value, pricelist_pts, free_qty_scheme_id,scheme_discount_per,product_discount_per, "
            + "sum(quantity_available) quantity_available,  sum(quantity_free_available) quantity_free_available, pack_size, "
            + "primary_pack_id,secondary_pack_id,tertiary_pack_id,ter_to_sec_quantity,sec_to_pri_quantity, "
            + "concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) as product_hash "
            + "FROM getproductsdetailsforgstsaleconfirm(?,?,?,?,?) "
            + "GROUP by product_batch_id,batch_no, expiry_date_actual,mrp_value,pricelist_pts,pricelist_ptr,free_qty_scheme_id,scheme_discount_per,product_discount_per,pack_size,"
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity ",
            new Object[]{salesInvoice.getAccountGroupId().getId(), salesInvoice.getAccountGroupPriceListId().getId(), salesInvoice.getId(),
              salesInvoice.getSalesInvoiceStatusId().getId(), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoice.getInvoiceEntryDate())});
    return productDetailSalesList;
  }

}
