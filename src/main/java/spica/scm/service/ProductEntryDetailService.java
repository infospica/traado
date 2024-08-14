/*
 * @(#)ProductEntryDetailService.java	1.0 Thu Sep 08 18:33:22 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import spica.scm.common.ProductSummary;
import spica.scm.domain.Account;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntry;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductPackingDetail;
import spica.scm.domain.ProductPreset;
import spica.scm.domain.SalesReturnItem;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.AppUtil;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.scm.util.ReferenceInvoice;
import spica.scm.validate.ProductEntryDetailIs;
import spica.scm.view.AccountUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.faces.MainServlet;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 * ProductEntryDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Sep 08 18:33:22 IST 2016
 */
public abstract class ProductEntryDetailService {

  private final static Logger LOGGER = Logger.getLogger(ProductEntryDetailService.class.getName());
  public final static Integer FREE_TO_CUSTOMER = 1;
  public final static Integer FREE_TO_COMPANY = 0;
  public final static Integer FREE_AS_REPLACEMENT = 1;

  /**
   * ProductEntryDetail paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductEntryDetailSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_entry_detail", ProductEntryDetail.class, main);
    sql.main("select scm_product_entry_detail.id,scm_product_entry_detail.product_entry_id,scm_product_entry_detail.product_detail_id,"
            + "scm_product_entry_detail.product_quantity,scm_product_entry_detail.product_quantity_shortage,scm_product_entry_detail.product_quantity_damaged,"
            + "scm_product_entry_detail.product_quantity_free,scm_product_entry_detail.product_free_qty_scheme_id,scm_product_entry_detail.product_quantity_excess,"
            + "scm_product_entry_detail.excess_quantity_description,scm_product_entry_detail.excess_quantity_note,scm_product_entry_detail.value_rate,"
            + "scm_product_entry_detail.value_goods,scm_product_entry_detail.value_assessable,scm_product_entry_detail.value_mrp,scm_product_entry_detail.value_vat,"
            + "scm_product_entry_detail.value_pts,scm_product_entry_detail.value_ptr,scm_product_entry_detail.tax_code_id,"
            + "scm_product_entry_detail.discount_value,scm_product_entry_detail.discount_percentage,"
            + "scm_product_entry_detail.is_product_discount_to_customer,scm_product_entry_detail.vendor_reserve_perc_forced,"
            + "scm_product_entry_detail.value_rate_per_prod_piece_der,scm_product_entry_detail.margin_percentage_deviation,"
            + "scm_product_entry_detail.value_pts_per_prod_piece,scm_product_entry_detail.pack_primary,scm_product_entry_detail.pack_secondary,"
            + "scm_product_entry_detail.pack_tertiary,scm_product_entry_detail.pack_tertiary_secondary_qty,scm_product_entry_detail.pack_secondary_primary_qty,"
            + "scm_product_entry_detail.pack_primary_dimension,scm_product_entry_detail.dimension_group_qty,scm_product_entry_detail.dimension_unit_qty,"
            + "scm_product_entry_detail.dimension_content_qty,scm_product_entry_detail.vat_refund_eligible,"
            + "scm_product_entry_detail.pack_size,scm_product_entry_detail.is_tax_code_modified,"
            + "scm_product_entry_detail.invoice_discount_value_derived,scm_product_entry_detail.invoice_discount_perc_derived,scm_product_entry_detail.product_discount_value_derived,scm_product_entry_detail.margin_percentage,scm_product_entry_detail.vendor_reserve_percentage,"
            + "scm_product_entry_detail.pts_margin_percentage,scm_product_entry_detail.ptr_margin_percentage,scm_product_entry_detail.value_ptr_per_prod_piece,"
            + "scm_product_entry_detail.mrplte_ptr_rate_derivation_criterion,scm_product_entry_detail.ptr_pts_rate_derivation_criterion,scm_product_entry_detail.pts_ss_rate_derivation_criterion,"
            + "scm_product_entry_detail.pack_purchase_default,scm_product_entry_detail.pack_dimension_size,scm_product_entry_detail.expiry_sales_days,"
            + "scm_product_entry_detail.product_tertairy_quantity,scm_product_entry_detail.product_secondary_quantity,scm_product_entry_detail.value_box_rate,scm_product_entry_detail.scheme_discount_pack_unit,"
            + "scm_product_entry_detail.product_discount_pack_unit,scm_product_entry_detail.margin_value_deviation,scm_product_entry_detail.vendor_margin_value,scm_product_entry_detail.landing_price_per_piece_company,"
            + "scm_product_entry_detail.note,scm_product_entry_detail.value_cst,scm_product_entry_detail.expected_landing_rate,scm_product_entry_detail.value_cgst,scm_product_entry_detail.value_sgst,"
            + "scm_product_entry_detail.value_igst,scm_product_entry_detail.tax_refund_eligible,scm_product_entry_detail.created_at,scm_product_entry_detail.modified_at,scm_product_entry_detail.created_by,"
            + "scm_product_entry_detail.scheme_discount_value_derived,scm_product_entry_detail.scheme_discount_percentage,scm_product_entry_detail.scheme_discount_value,"
            + "scm_product_entry_detail.scheme_discount_replacement,scm_product_entry_detail.is_scheme_discount_to_customer,scm_product_entry_detail.modified_by,scm_product_entry_detail.cash_discount_value_derived,scm_product_entry_detail.invoice_discount_value "
            + "from scm_product_entry_detail scm_product_entry_detail "); //Main query
    sql.count("select count(scm_product_entry_detail.id) as total from scm_product_entry_detail scm_product_entry_detail "); //Count query
    sql.join("left outer join scm_product_entry scm_product_entry on (scm_product_entry.id = scm_product_entry_detail.product_entry_id) "
            + "left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_product_entry_detail.product_detail_id) "
            + "left outer join scm_product_free_qty_scheme scm_product_free_qty_scheme on (scm_product_free_qty_scheme.id = scm_product_entry_detail.product_free_qty_scheme_id) "
            + "left outer join scm_tax_code scm_tax_code on (scm_tax_code.id = scm_product_entry_detail.tax_code_id) "
            + "left outer join scm_product_packing scm_product_packing_primary on (scm_product_packing_primary.id = scm_product_entry_detail.pack_primary) "
            + "left outer join scm_product_packing scm_product_packing_secondary on (scm_product_packing_secondary.id = scm_product_entry_detail.pack_secondary) "
            + "left outer join scm_product_packing scm_product_packing_tertiary on (scm_product_packing_tertiary.id = scm_product_entry_detail.pack_tertiary) "
            + "left outer join scm_product_packing scm_product_packing_default on (scm_product_packing_default.id = scm_product_entry_detail.pack_purchase_default) "
            + "left outer join scm_product_packing scm_product_packing_product_discount on (scm_product_packing_product_discount.id = scm_product_entry_detail.product_discount_pack_unit) "
            + "left outer join scm_product_packing scm_product_packing_scheme_discount on (scm_product_packing_scheme_discount.id = scm_product_entry_detail.scheme_discount_pack_unit) "); //Join Query

    sql.string(new String[]{"scm_product_entry.invoice_no", "scm_product_entry_detail.excess_quantity_description", "scm_product_entry_detail.excess_quantity_note",
      "scm_tax_code.code", "scm_excise_duty.title", "scm_product_packing_primary.title", "scm_product_packing_secondary.title", "scm_product_packing_tertiary.title",
      "scm_prod_ent_det_buyback_stat.title", "scm_prod_ent_det_rtn_status.title", "scm_product_entry_detail.pack_size", "scm_product_packing_default.title",
      "scm_product_entry_detail.pack_dimension_size", "scm_product_packing_product_discount.title", "scm_product_entry_detail.note",
      "scm_product_packing_scheme_discount.title", "scm_product_entry_detail.created_by", "scm_product_entry_detail.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_entry_detail.id", "scm_product_entry_detail.product_quantity", "scm_product_entry_detail.product_quantity_shortage", "scm_product_entry_detail.product_quantity_damaged",
      "scm_product_entry_detail.product_quantity_free", "scm_product_free_qty_scheme.free_qty_scheme_unit_qty_for", "scm_product_entry_detail.product_quantity_excess",
      "scm_product_entry_detail.value_rate", "scm_product_entry_detail.value_mrp", "scm_product_entry_detail.value_vat", "scm_product_entry_detail.value_pts",
      "scm_product_entry_detail.value_ptr", "scm_product_entry_detail.discount_value", "scm_product_entry_detail.discount_percentage", "scm_product_entry_detail.is_product_discount_to_customer",
      "scm_product_entry_detail.vendor_reserve_perc_forced", "scm_product_entry_detail.value_rate_per_prod_piece_der", "scm_product_entry_detail.margin_percentage_deviation",
      "scm_product_entry_detail.value_pts_per_prod_piece", "scm_product_entry_detail.pack_tertiary_secondary_qty", "scm_product_entry_detail.pack_secondary_primary_qty",
      "scm_product_entry_detail.pack_primary_dimension", "scm_product_entry_detail.dimension_group_qty", "scm_product_entry_detail.dimension_unit_qty",
      "scm_product_entry_detail.dimension_content_qty", "scm_product_entry_detail.vat_refund_eligible",
      "scm_product_entry_detail.invoice_discount_value_derived", "scm_product_entry_detail.product_discount_value_derived", "scm_product_entry_detail.margin_percentage",
      "scm_product_entry_detail.vendor_reserve_percentage", "scm_product_entry_detail.pts_margin_percentage", "scm_product_entry_detail.ptr_margin_percentage", "scm_product_entry_detail.value_ptr_per_prod_piece",
      "scm_product_entry_detail.mrplte_ptr_rate_derivation_criterion", "scm_product_entry_detail.ptr_pts_rate_derivation_criterion", "scm_product_entry_detail.pts_ss_rate_derivation_criterion",
      "scm_product_entry_detail.expiry_sales_days", "scm_product_entry_detail.product_tertairy_quantity", "scm_product_entry_detail.product_secondary_quantity", "scm_product_entry_detail.value_box_rate",
      "scm_product_entry_detail.scheme_discount_pack_unit", "scm_product_entry_detail.margin_value_deviation", "scm_product_entry_detail.vendor_margin_value",
      "scm_product_entry_detail.landing_price_per_piece_company", "scm_product_entry_detail.value_cst", "scm_product_entry_detail.expected_landing_rate", "scm_product_entry_detail.value_cgst",
      "scm_product_entry_detail.value_sgst", "scm_product_entry_detail.value_igst", "scm_product_entry_detail.tax_refund_eligible"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_entry_detail.created_at", "scm_product_entry_detail.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductEntryDetail.
   *
   * @param main
   * @return List of ProductEntryDetail
   */
  public static final List<ProductEntryDetail> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductEntryDetailSqlPaged(main));
  }

  /**
   * Select ProductEntryDetail by key.
   *
   * @param main
   * @param productEntryDetail
   * @return ProductEntryDetail
   */
  public static final ProductEntryDetail selectByPk(Main main, ProductEntryDetail productEntryDetail) {
    return (ProductEntryDetail) AppService.find(main, ProductEntryDetail.class, productEntryDetail.getId());
  }

  /**
   * Insert ProductEntryDetail.
   *
   * @param main
   * @param productEntryDetail
   */
  public static final void insert(Main main, ProductEntryDetail productEntryDetail) {
    ProductEntryDetailIs.insertAble(main, productEntryDetail);  //Validating
    AppService.insert(main, productEntryDetail);

  }

  /**
   * Update ProductEntryDetail by key.
   *
   * @param main
   * @param productEntryDetail
   * @return ProductEntryDetail
   */
  public static final ProductEntryDetail updateByPk(Main main, ProductEntryDetail productEntryDetail) {
    ProductEntryDetailIs.updateAble(main, productEntryDetail); //Validating
    return (ProductEntryDetail) AppService.update(main, productEntryDetail);
  }

  /**
   * Insert or update ProductEntryDetail
   *
   * @param main
   * @param productEntryDetail
   */
  public static void insertOrUpdate(Main main, ProductEntryDetail productEntryDetail) {
    if (productEntryDetail.getProductEntryId().getOpeningStockEntry() == SystemConstants.OPENING_STOCK_ENTRY) {
      if (productEntryDetail.getProductQuantityFree() == null && productEntryDetail.getSchemeDiscountValue() == null
              && productEntryDetail.getSchemeDiscountPercentage() == null && productEntryDetail.getProductEntryId().getSalesReturnId() == null) {
        productEntryDetail.setProductFreeQtySchemeId(null);
        productEntryDetail.setSchemeDiscountPercentage(null);
        productEntryDetail.setSchemeDiscountValue(null);
        productEntryDetail.setSchemeDiscountValueDerived(null);
        productEntryDetail.setIsSchemeDiscountToCustomer(null);
      }
    } else {
      if (productEntryDetail.getProductQuantityFree() == null && productEntryDetail.getProductEntryId().getSalesReturnId() == null) {
        productEntryDetail.setProductFreeQtySchemeId(null);
        productEntryDetail.setSchemeDiscountPercentage(null);
        productEntryDetail.setSchemeDiscountValue(null);
        productEntryDetail.setSchemeDiscountValueDerived(null);
        productEntryDetail.setIsSchemeDiscountToCustomer(null);
      }
    }
    if (!AccountUtil.isSuperStockiest(productEntryDetail.getProductEntryId().getAccountId())) {
      productEntryDetail.setExpectedLandingRate(productEntryDetail.getActualSellingPriceDerived());
    }
    updateDiscountBeneficiary(productEntryDetail);
    if (productEntryDetail.getId() == null) {
      if (productEntryDetail.getProductDetailId() != null) {
        setProductPresetToProductEntryDetail(main, productEntryDetail);
      }
      insert(main, productEntryDetail);
    } else {
      updateByPk(main, productEntryDetail);
    }
  }

  /**
   *
   * @param productEntryDetail
   */
  private static void updateDiscountBeneficiary(ProductEntryDetail productEntryDetail) {
    if ((productEntryDetail.getDiscountValue() != null || productEntryDetail.getDiscountPercentage() != null) && productEntryDetail.getIsLineDiscountToCustomer() == null) {
      if (AccountUtil.isSuperStockiest(productEntryDetail.getProductEntryId().getAccountId())) {
        productEntryDetail.setIsLineDiscountToCustomer(SystemConstants.DISCOUNT_FOR_COMPANY);
      } else {
        productEntryDetail.setIsLineDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
      }
    }

    if (productEntryDetail.getProductFreeQtySchemeId() == null && productEntryDetail.getProductQuantityFree() != null && productEntryDetail.getProductQuantityFree() > 0
            && productEntryDetail.getIsSchemeDiscountToCustomer() == null
            && productEntryDetail.getProductEntryId().getQuantityOrFree().equals(SystemConstants.QUANTITY_SCHEME)) {

      if (AccountUtil.isSuperStockiest(productEntryDetail.getProductEntryId().getAccountId())) {
        productEntryDetail.setIsSchemeDiscountToCustomer(SystemConstants.DISCOUNT_FOR_COMPANY);
      } else {
        productEntryDetail.setIsSchemeDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
      }
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productEntryDetail
   */
  public static void clone(Main main, ProductEntryDetail productEntryDetail) {
    productEntryDetail.setId(null); //Set to null for insert
    insert(main, productEntryDetail);
  }

  /**
   * Delete ProductEntryDetail.
   *
   * @param main
   * @param productEntryDetail
   */
  public static final void deleteByPk(Main main, ProductEntryDetail productEntryDetail) {
    ProductEntryDetailIs.deleteAble(main, productEntryDetail); //Validation
    referenceErrorMessages(main, productEntryDetail.getId(), SystemConstants.LINE_ITEM, SystemConstants.SALES_INVOICE_ITEM);
    AppService.delete(main, ProductEntryDetail.class, productEntryDetail.getId());
    main.em().flush();
  }

  /**
   * Delete Array of ProductEntryDetail.
   *
   * @param main
   * @param productEntryDetail
   */
  public static final void deleteByPkArray(Main main, ProductEntryDetail[] productEntryDetail) {
    for (ProductEntryDetail e : productEntryDetail) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param productEntryDetailList
   * @param productEntry
   * @param prodEntryStatusId
   */
  public static void insertOrUpdate(MainServlet main, List<ProductEntryDetail> productEntryDetailList, ProductEntry productEntry, String prodEntryStatusId) {
    if (productEntryDetailList != null) {
      for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
        productEntryDetail.setProductEntryId(productEntry);
        insertOrUpdate(main, productEntryDetail);
      }

      //AppService.deleteSql(main, PurchaseOrderItem.class, "delete from scm_purchase_order_item where sort_order = ? and purchase_order_id = ?", new Object[]{SORT_ORDER, purchaseOrder.getId()});//deleting all preveously trashed records
      main.param(Integer.parseInt(prodEntryStatusId));
      main.param(productEntry.getId());
      AppService.updateSql(main, ProductEntry.class, "update scm_product_entry set product_entry_status_id = ? where id = ?", false);// draft or confirmed
    }
  }

  /**
   *
   * @param main
   * @param productEntryDetail
   */
  public static final void insertOrUpdateProductEntryDetail(Main main, ProductEntryDetail productEntryDetail) {
    setProductPresetToProductEntryDetail(main, productEntryDetail);
    insertOrUpdate(main, productEntryDetail);
  }

  /**
   *
   * @param main
   * @param productEntry
   * @return
   */
  public static List<ProductEntryDetail> selectProductEntryDetailByProductEntryId(Main main, ProductEntry productEntry, TaxCalculator taxCalculator) {
    List<ProductEntryDetail> productEntryDetailList = null;
    if (productEntry.getId() != null) {
      productEntryDetailList = AppService.list(main, ProductEntryDetail.class, "select * from scm_product_entry_detail where product_entry_id = ? and is_service IS NULL order by id asc",
              new Object[]{productEntry.getId()});
      updateProductSummery(main, productEntryDetailList);

      if (SystemConstants.TAX_CODE_MODIFIED.equals(productEntry.getIsTaxCodeModified())) {
        updateProductTaxVariation(main, productEntryDetailList, taxCalculator);
      }
    }
    return productEntryDetailList;
  }

  /**
   *
   * @param main
   * @param productEntryDetailList
   */
  private static void updateProductSummery(Main main, List<ProductEntryDetail> productEntryDetailList) {
    for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
      productEntryDetail.setProduct(productEntryDetail.getProductDetailId().getProductBatchId().getProductId());
      productEntryDetail.setProductBatchId(productEntryDetail.getProductBatchId());
      productEntryDetail.setProductSummary(new ProductSummary(productEntryDetail.getProduct().getId(), productEntryDetail.getProductDetailId().getProductBatchId().getId(), productEntryDetail.getProduct().getProductName()));
      productEntryDetail.getProductSummary().after();
      productEntryDetail.after();
      productEntryDetail.setProductInvoiceDetailList(ProductEntryService.selectLastFivePurchaseByProductDetail(main, productEntryDetail));
      productEntryDetail.setCurrentTaxCode(productEntryDetail.getTaxCodeId());
    }
  }

  /**
   *
   * @param main
   * @param productEntryDetailList
   */
  private static void updateProductTaxVariation(Main main, List<ProductEntryDetail> productEntryDetailList, TaxCalculator taxCalculator) {
    for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
      if (productEntryDetail.getIsTaxCodeModified() != null && SystemConstants.TAX_CODE_MODIFIED.equals(productEntryDetail.getIsTaxCodeModified())) {
        taxCalculator.updateProductEntryDetail(main, productEntryDetail.getProductEntryId().getAccountId(), productEntryDetail);
      }
    }
  }

  public static final ProductEntryDetail selectProductEntryDetailByProductEntryDetailId(MainView main, ProductEntryDetail productEntryDetail) {
    return (ProductEntryDetail) AppService.single(main, ProductEntryDetail.class, "select * from scm_product_entry_detail scm_product_entry_detail where scm_product_entry_detail.id = ?", new Object[]{productEntryDetail.getId()});
  }

  public static ProductEntryDetail selectExcessQtyDetails(Main main, String productEntryDetailId) {
    return (ProductEntryDetail) AppService.single(main, ProductEntryDetail.class, "select id,excess_quantity_description,excess_quantity_note from scm_product_entry_detail where id = ?", new Object[]{Integer.parseInt(productEntryDetailId)});
  }

  public static void updateExcessQuantityDetails(MainView main, String excessDescription, String excessNote, String productEntryDetailId) {
    main.param(excessDescription);
    main.param(excessNote);
    main.param(Integer.parseInt(productEntryDetailId));
    AppService.updateSql(main, ProductEntryDetail.class, "update scm_product_entry_detail set excess_quantity_description = ?, excess_quantity_note = ? where id = ? ", false);
  }

  /**
   *
   * @param main
   * @param account
   * @param prodDetailId
   * @return
   */
  public static final boolean validateProductDetail(Main main, Account account, int prodDetailId) {
    return AppService.exist(main, "select scm_product_entry_detail.id from scm_product_entry_detail "
            + "left outer join scm_product_entry on(scm_product_entry_detail.product_entry_id=scm_product_entry.id) "
            + "left outer join scm_product_detail on(scm_product_entry_detail.product_detail_id=scm_product_detail.id) "
            + "where scm_product_entry.product_entry_status_id = ? and scm_product_entry.account_id = ? and scm_product_entry_detail.product_detail_id = ? ",
            new Object[]{SystemConstants.DRAFT, account.getId(), prodDetailId});

  }

  /**
   *
   * @param main
   * @param productEntryDetail
   */
  public static void updateProductEntryDetail(MainServlet main, ProductEntryDetail productEntryDetail) {
    ProductEntryDetail ped = selectByPk(main, productEntryDetail);
    if (ped.getId() != null) {
      ped.setProductQuantity(productEntryDetail.getProductQuantity());
      ped.setProductTertairyQuantity(productEntryDetail.getProductTertairyQuantity());
      ped.setProductSecondaryQuantity(productEntryDetail.getProductSecondaryQuantity());
      ped.setProductQuantityDamaged(productEntryDetail.getProductQuantityDamaged());
      ped.setProductQuantityShortage(productEntryDetail.getProductQuantityShortage());
      ped.setProductQuantityExcess(productEntryDetail.getProductQuantityExcess());
      ped.setExcessQuantityDescription(productEntryDetail.getExcessQuantityDescription());
      ped.setExcessQuantityNote(productEntryDetail.getExcessQuantityNote());
      ped.setProductQuantityFree(productEntryDetail.getProductQuantityFree());
      if (productEntryDetail.getProductQuantityFree() != null && productEntryDetail.getProductQuantityFree() > 0) {
        ped.setProductFreeQtySchemeId(productEntryDetail.getProductFreeQtySchemeId());
        ped.setSchemeDiscountPackUnit(productEntryDetail.getSchemeDiscountPackUnit());
      } else {
        ped.setProductFreeQtySchemeId(null);
        ped.setSchemeDiscountPackUnit(null);
      }
      ped.setValueBoxRate(productEntryDetail.getValueBoxRate());
      ped.setValueMrp(productEntryDetail.getValueMrp());
      ped.setValuePtr(productEntryDetail.getValuePtr());
      ped.setValuePts(productEntryDetail.getValuePts());
      ped.setValueRate(productEntryDetail.getValueRate());
      ped.setValueVat(productEntryDetail.getValueVat());
      ped.setValuePtrPerProdPiece(productEntryDetail.getValuePtrPerProdPiece());
      ped.setValueRatePerProdPieceDer(productEntryDetail.getValueRatePerProdPieceDer());

      ped.setDiscountValue(productEntryDetail.getDiscountValue());
      ped.setDiscountPercentage(productEntryDetail.getDiscountPercentage());
      ped.setProductDiscountPackUnit(productEntryDetail.getProductDiscountPackUnit());
      ped.setInvoiceDiscountValueDerived(productEntryDetail.getInvoiceDiscountValueDerived());
      ped.setProductDiscountValueDerived(productEntryDetail.getProductDiscountValueDerived());
      ped.setIsLineDiscountToCustomer(productEntryDetail.getIsLineDiscountToCustomer());

      updateByPk(main, ped);
    }
  }
  /**
   *
   * @param main
   * @param productEntryDetailId
   */
  public static final void updateProdutQuantity(Main main, ProductEntryDetail productEntryDetailId) {
    String updateQry = "update scm_product_entry_detail set product_quantity=?,product_quantity_free=?, product_quantity_shortage = ?, product_quantity_damaged = ?, product_quantity_excess = ?,"
            + "excess_quantity_description = ?, excess_quantity_note = ?,product_secondary_quantity=?,product_tertairy_quantity=? where id = ?";

    main.clear();
    main.param(productEntryDetailId.getProductQuantity());
    main.param(productEntryDetailId.getProductQuantityFree());
    main.param(productEntryDetailId.getProductQuantityShortage());
    main.param(productEntryDetailId.getProductQuantityDamaged());
    main.param(productEntryDetailId.getProductQuantityExcess());
    main.param(productEntryDetailId.getExcessQuantityDescription());
    main.param(productEntryDetailId.getExcessQuantityNote());
    main.param(productEntryDetailId.getProductSecondaryQuantity());
    main.param(productEntryDetailId.getProductTertairyQuantity());
    main.param(productEntryDetailId.getId());
    AppService.updateSql(main, ProductEntryDetail.class, updateQry, false);
    main.clear();
  }

  /**
   *
   * @param main
   * @param productEntryDetailId
   */
  public static void updateProdutPurchaseBenefit(Main main, ProductEntryDetail productEntryDetailId) {
    String updateQry = "update scm_product_entry_detail set is_product_discount_to_customer = ? where id = ?";

    main.clear();
    main.param(productEntryDetailId.getIsLineDiscountToCustomer());
    main.param(productEntryDetailId.getId());
    AppService.updateSql(main, ProductEntryDetail.class, updateQry, false);
    main.clear();
  }

  public static void updateProductFreeQtyScheme(MainView main, ProductEntryDetail productEntryDetail) {
    String updateQry = "update scm_product_entry_detail set product_free_qty_scheme_id = ? where id = ?";
    main.clear();
    main.param(productEntryDetail.getProductFreeQtySchemeId().getId());
    main.param(productEntryDetail.getId());
    AppService.updateSql(main, ProductEntryDetail.class, updateQry, false);
    main.clear();
  }

  /**
   *
   * @param main
   * @param productEntryDetail
   */
  private static void setProductPresetToProductEntryDetail(Main main, ProductEntryDetail productEntryDetail) {
    ProductPreset productPreset = null;
    ProductDetail productDetail = productEntryDetail.getProductDetailId();
    ProductBatch productBatch = productEntryDetail.getProductBatchId();
    ProductPackingDetail productPackingDetail = productBatch.getProductPackingDetailId();

    if (productDetail != null && productDetail.getProductPresetId() != null) {
      productPreset = productDetail.getProductPresetId();
    } else {
      productPreset = (ProductPreset) AppService.single(main, ProductPreset.class, "select * from scm_product_preset where account_id = ? and product_id = ?", new Object[]{productDetail.getAccountId().getId(), productBatch.getProductId().getId()});
    }

    if (productPreset != null) {
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
      productEntryDetail.setDimensionUnitQty(productPackingDetail.getPrimaryDimensionUnitQty());

      //expiry sales days
      productEntryDetail.setExpirySalesDays(productBatch.getProductId().getExpirySalesDays());

      // Rate criteria
      productEntryDetail.setMrpltePtrRateDerivationCriterion(productPreset.getMrpltePtrRateDerivationCriterion());
      // Rate criteria
      productEntryDetail.setPtrPtsRateDerivationCriterion(productPreset.getPtrPtsRateDerivationCriterion());
      // Rate criteria
      productEntryDetail.setPtsSsRateDerivationCriterion(productPreset.getPtsSsRateDerivationCriterion());

      productEntryDetail.setPackSize(productBatch.getProductId().getPackSize() + productBatch.getProductId().getProductUnitId().getSymbol());
    }
  }

  public static boolean isProductDetailAvailed(Main main, ProductDetail productDetail, ProductEntryDetail productEntryDetail) {
    boolean exist = false;
    if (productEntryDetail != null && productEntryDetail.getId() != null) {
      exist = AppService.exist(main, "select 1 from scm_product_entry_detail where product_detail_id = ? and id <> ? ", new Object[]{productDetail.getId(), productEntryDetail.getId()});
    } else {
      exist = AppService.exist(main, "select 1 from scm_product_entry_detail where product_detail_id = ?", new Object[]{productDetail.getId()});
    }
    return exist;
  }

  public static void insertSalesReturnInvoiceEntryItem(Main main, ProductEntry productEntry, List<SalesReturnItem> openingSalesReturnList, TaxCalculator taxCalculator) {
    ProductEntryDetail productEntryDetail;
    ProductDetail productDetailId = null;
    List<ProductEntryDetail> productEntryDetailList = new ArrayList<>();
    Double invoiceDiscountValue = null;

    for (SalesReturnItem salesReturnItem : openingSalesReturnList) {
      productEntryDetail = new ProductEntryDetail();
      productEntryDetail.setProductEntryId(productEntry);
      //productEntryDetail.setSalesReturnItemId(salesReturnItem);
      productEntryDetail.setProductBatchId(salesReturnItem.getProductBatchId());

      if (!StringUtil.isEmpty(salesReturnItem.getProductDetailHash())) {
        productDetailId = ProductDetailService.selectByPk(main, new ProductDetail(Integer.parseInt(salesReturnItem.getProductDetailHash())));
      } else {
        productDetailId = ProductDetailService.insertOrUpdate(main, productEntry.getAccountId(), salesReturnItem.getProductBatchId(), null);
      }

      if (salesReturnItem.getProductQuantity() != null) {
        productEntryDetail.setProductQuantity(salesReturnItem.getProductQuantity().doubleValue());
      }

      if (salesReturnItem.getProductQuantityDamaged() != null) {
        productEntryDetail.setProductQuantity(salesReturnItem.getProductQuantityDamaged());
        productEntryDetail.setProductQuantityDamaged(salesReturnItem.getProductQuantityDamaged());
      }
      productEntryDetail.setBatchNo(salesReturnItem.getProductBatchId().getBatchNo());
      productEntryDetail.setExpiryDate(salesReturnItem.getProductBatchId().getExpiryDateActual());
      productEntryDetail.setValueRate(salesReturnItem.getPurchaseRate());
      if (salesReturnItem.getValuePtr() != null) {
        productEntryDetail.setValuePtr(MathUtil.roundOff(salesReturnItem.getValuePtr(), 2));
      }
      productEntryDetail.setValuePts(MathUtil.roundOff(salesReturnItem.getValueRate(), 2));
      productEntryDetail.setValueMrp(salesReturnItem.getValueMrp());
      productEntryDetail.setTaxCodeId(salesReturnItem.getTaxCodeId());
      productEntryDetail.setProductDetailId(productDetailId);
      productEntryDetail.setPackPurchaseDefault(salesReturnItem.getProductBatchId().getDefaultProductPackingId());
      setProductPresetToProductEntryDetail(main, productEntryDetail);
      productEntryDetail.setValuePtrPerProdPiece(productEntryDetail.getValuePtr());
      productEntryDetail.setActualSellingPriceDerived(ProductUtil.getActualPtsDerived(salesReturnItem));
      productEntryDetail.setValuePtsPerProdPiece(salesReturnItem.getValueRate());
      productEntryDetail.setPurchaseRatePerPiece(salesReturnItem.getPurchaseRate());
      productEntryDetail.setExpectedLandingRate(salesReturnItem.getExpectedLandingRate());
      productEntryDetail.setPackingDescription(salesReturnItem.getPackDescription());
      salesReturnItem.setProductEntryDetailId(productEntryDetail);
      salesReturnItem.setProductDetailId(productDetailId);
      productEntryDetail.setSchemeDiscountPercentage(salesReturnItem.getSchemeDiscountPercentage());
      productEntryDetail.setDiscountPercentage(salesReturnItem.getProductDiscountPercentage());
//      productEntryDetail.setInvoiceDiscountValue(salesReturnItem.getInvoiceDiscountValue());
      productEntryDetailList.add(productEntryDetail);
    }

    if (invoiceDiscountValue != null) {
      productEntry.setInvoiceDiscountValue(invoiceDiscountValue);
      productEntry.setIsInvoiceDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
    }

    taxCalculator.processSalesReturnProductEntryCalculation(productEntry, productEntryDetailList);
    if (AccountUtil.isSuperStockiest(productEntry.getAccountId())) {
      taxCalculator.productMarginCalculation(main, productEntry, productEntryDetailList);
      main.em().flush();
    }

    for (ProductEntryDetail productEntryDetail1 : productEntryDetailList) {
      insertOrUpdate(main, productEntryDetail1);
      //StockSaleableService.insertProductEntryDetail(main, productEntry, productEntryDetailList);
    }
  }

  public static final List<ProductEntryDetail> selectServiceEntryDetails(Main main, ProductEntry productEntry) {
    List<ProductEntryDetail> list = null;
    if (productEntry != null) {
      list = AppService.list(main, ProductEntryDetail.class, "select * from scm_product_entry_detail where product_entry_id = ? and is_service = ?", new Object[]{productEntry.getId(), CommodityService.SERVICE});
    }
    return list;
  }

  private static void referenceErrorMessages(Main main, Integer id, String invoiceNo, String type) {
    String existing = "";
    int i = 0;
    String sql = "";
    if (type.equals(SystemConstants.SALES_INVOICE_ITEM)) {
      sql = "select invoice_no,customer_id from scm_sales_invoice where id in(select sales_invoice_id from scm_sales_invoice_item where product_entry_detail_id in\n"
              + "(select id from scm_product_entry_detail where id =?))";
    }
    List<ReferenceInvoice> exist = (List<ReferenceInvoice>) AppDb.getList(main.dbConnector(), ReferenceInvoice.class, sql, new Object[]{id});

    for (ReferenceInvoice t : exist) {
      if (i > 0) {
        existing += ",";
      }
      existing += t.getInvoiceNo();
      i++;
    }
    AppUtil.referenceError(main, invoiceNo, existing);

  }
}
