/*
 * @(#)SalesInvoiceItemService.java	1.0 Fri Oct 28 09:25:53 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import spica.constant.AccountingConstant;
import spica.scm.common.InvoiceItem;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductFreeQtyScheme;
import spica.scm.domain.ProductPacking;
import spica.scm.domain.ProductPricelist;
import spica.scm.domain.SalesInvoice;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.StockPreSale;
import spica.scm.domain.StockSaleable;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.MathUtil;
import spica.scm.util.SalesInvoiceItemUtil;
import spica.scm.util.SalesInvoiceUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * SalesInvoiceItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Oct 28 09:25:53 IST 2016
 */
public abstract class SalesInvoiceItemService {

  /**
   * SalesInvoiceItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesInvoiceItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_invoice_item", SalesInvoiceItem.class, main);
    sql.main("select scm_sales_invoice_item.id,scm_sales_invoice_item.sales_invoice_id,scm_sales_invoice_item.product_detail_id,scm_sales_invoice_item.is_tax_code_modified,"
            + "scm_sales_invoice_item.product_entry_detail_id,scm_sales_invoice_item.product_qty,scm_sales_invoice_item.product_qty_free,"
            + "scm_sales_invoice_item.value_prod_piece_selling,scm_sales_invoice_item.prod_piece_selling_forced,scm_sales_invoice_item.product_discount_value,"
            + "scm_sales_invoice_item.product_free_scheme_id,scm_sales_invoice_item.product_discount_actual,scm_sales_invoice_item.invoice_discount_actual,"
            + "scm_sales_invoice_item.sec_to_pri_quantity_actual,scm_sales_invoice_item.ter_to_sec_quantity_actual,scm_sales_invoice_item.product_hash,"
            + "scm_sales_invoice_item.value_mrp,scm_sales_invoice_item.value_pts,scm_sales_invoice_item.value_ptr,scm_sales_invoice_item.proforma_item_status,scm_sales_invoice_item.value_prod_piece_sold, "
            + "scm_sales_invoice_item.proforma_product_qty_variation,scm_sales_invoice_item.proforma_free_qty_variation,scm_sales_invoice_item.item_deleted,"
            + "scm_sales_invoice_item.sales_invoice_item_hash_code,scm_sales_invoice_item.invoice_discount_derived,scm_sales_invoice_item.value_sale,"
            + "scm_sales_invoice_item.value_vat,scm_sales_invoice_item.sales_order_item_id,scm_sales_invoice_item.value_cgst,scm_sales_invoice_item.value_sgst,"
            + "scm_sales_invoice_item.value_igst,scm_sales_invoice_item.tax_code_id, scm_sales_invoice_item.scheme_discount_value,scm_sales_invoice_item.scheme_discount_percentage,"
            + "scm_sales_invoice_item.product_discount_percentage,scm_sales_invoice_item.scheme_discount_actual,scm_sales_invoice_item.product_discount_derived,"
            + "scm_sales_invoice_item.scheme_discount_derived,scm_sales_invoice_item.invoice_discount_value,scm_sales_invoice_item.value_goods,scm_sales_invoice_item.account_id "
            + "from scm_sales_invoice_item scm_sales_invoice_item "); //Main query
    sql.count("select count(scm_sales_invoice_item.id) as total from scm_sales_invoice_item scm_sales_invoice_item "); //Count query
    sql.join("left outer join scm_sales_invoice scm_sales_invoice_itemsales_invoice_id on (scm_sales_invoice_itemsales_invoice_id.id = scm_sales_invoice_item.sales_invoice_id) "
            + "left outer join scm_product_detail scm_sales_invoice_itemproduct_detail_id on (scm_sales_invoice_itemproduct_detail_id.id = scm_sales_invoice_item.product_detail_id) "
            + "left outer join scm_tax_code scm_tax_code on (scm_tax_code.id = scm_sales_invoice_item.tax_code_id) "
            + "left outer join scm_account scm_account on (scm_account.id = scm_sales_invoice_item.account_id) "
            + "left outer join scm_product_entry_detail scm_sales_invoice_itemproduct_entry_detail_id on (scm_sales_invoice_itemproduct_entry_detail_id.id = scm_sales_invoice_item.product_entry_detail_id) "
            + "left outer join scm_product_free_qty_scheme scm_sales_invoice_itemproduct_free_scheme_id on (scm_sales_invoice_itemproduct_free_scheme_id.id = scm_sales_invoice_item.product_free_scheme_id) "
            + "left outer join scm_sales_order_item scm_sales_invoice_itemsales_order_item_id on (scm_sales_invoice_itemsales_order_item_id.id = scm_sales_invoice_item.sales_order_item_id)"); //Join Query

    sql.string(new String[]{"scm_sales_invoice_itemsales_invoice_id.invoice_no", "scm_sales_invoice_itemproduct_detail_id.batch_no", "scm_sales_invoice_item.product_hash", "scm_sales_invoice_item.sales_invoice_item_hash_code"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_invoice_item.id", "scm_sales_invoice_itemproduct_entry_detail_id.product_quantity", "scm_sales_invoice_item.product_qty", "scm_sales_invoice_item.product_qty_free", "scm_sales_invoice_item.value_prod_piece_selling", "scm_sales_invoice_item.prod_piece_selling_forced", "scm_sales_invoice_item.product_discount_value", "scm_sales_invoice_itemproduct_free_scheme_id.free_qty_scheme_unit_qty_for", "scm_sales_invoice_item.product_discount_actual", "scm_sales_invoice_item.invoice_discount_actual", "scm_sales_invoice_item.sec_to_pri_quantity_actual", "scm_sales_invoice_item.ter_to_sec_quantity_actual", "scm_sales_invoice_item.value_pts", "scm_sales_invoice_item.value_ptr", "scm_sales_invoice_item.proforma_item_status", "scm_sales_invoice_item.proforma_product_qty_variation", "scm_sales_invoice_item.proforma_free_qty_variation", "scm_sales_invoice_item.item_deleted", "scm_sales_invoice_item.invoice_discount_derived", "scm_sales_invoice_item.value_sale", "scm_sales_invoice_item.value_vat", "scm_sales_invoice_itemsales_order_item_id.qty_required", "scm_sales_invoice_item.value_cgst", "scm_sales_invoice_item.value_sgst", "scm_sales_invoice_item.value_igst"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesInvoiceItem.
   *
   * @param main
   * @return List of SalesInvoiceItem
   */
  public static final List<SalesInvoiceItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesInvoiceItemSqlPaged(main));
  }

  /**
   *
   * @param main
   * @param salesInvoice
   * @param taxCalculator
   * @return
   */
  public static final List<SalesInvoiceItem> selectSalesInvoiceItemBySalesInvoice(Main main, SalesInvoice salesInvoice, TaxCalculator taxCalculator, boolean draft) {
    String sql = "select row_number() OVER () as id, string_agg(id::varchar, '#') as sales_invoice_item_hash_code, sales_invoice_id, "
            + "CASE WHEN sum(coalesce(product_qty_free,0)) = 0 then NULL ELSE sum(coalesce(product_qty_free,0)) END as product_qty_free, "
            + "sum(product_qty) product_qty,value_prod_piece_selling,prod_piece_selling_forced,scheme_discount_percentage,product_discount_percentage,"
            + "CASE WHEN sum(coalesce(scheme_discount_value,0)) = 0 THEN NULL ELSE sum(coalesce(scheme_discount_value,0)) END as scheme_discount_value, "
            + "CASE WHEN sum(coalesce(product_discount_value,0)) = 0 THEN NULL ELSE sum(coalesce(product_discount_value,0)) END product_discount_value, "
            + "CASE WHEN sum(coalesce(invoice_discount_value,0)) = 0 THEN NULL ELSE sum(coalesce(invoice_discount_value,0)) END invoice_discount_value, "
            + "CASE WHEN sum(coalesce(cash_discount_value,0)) = 0 THEN NULL ELSE sum(coalesce(cash_discount_value,0)) END cash_discount_value, "
            + "sum(value_sale) value_sale,product_free_scheme_id,scheme_discount_actual,product_discount_actual, sec_to_pri_quantity_actual, ter_to_sec_quantity_actual,"
            + "product_hash, value_mrp, value_pts,value_ptr, tax_code_id, quantity_or_free,value_prod_piece_sold,hsn_code,"
            + "is_scheme_discount_in_percentage,is_product_discount_in_percentage,batch_no,expiry_date,scheme_discount_derived,product_discount_derived,"
            + "cash_discount_value_derived,invoice_discount_derived,is_tax_code_modified,sort_order,SUM(service_freight_value) as service_freight_value,"
            + "service_freight_value_derived,SUM(service2_value) as service2_value,service2_value_derived,"
            + "sum(kerala_flood_cess_value) kerala_flood_cess_value,kerala_flood_cess_value_derived,is_mrp_changed,"
            + "SUM(COALESCE(margin_value_deviation_der,0)) as  margin_value_deviation_der,actual_selling_price_derived ";
    if (!draft) {
      sql += ",sum(tcs_value) tcs_value,tcs_value_derived ";
    }
    sql += "from scm_sales_invoice_item "
            + "where sales_invoice_id = ? and (item_deleted = 0 or item_deleted is null) "
            + "GROUP BY sales_invoice_id,product_hash,value_prod_piece_selling,value_prod_piece_sold,prod_piece_selling_forced, "
            + "scheme_discount_percentage,product_discount_percentage,product_free_scheme_id,scheme_discount_actual,product_discount_actual,sec_to_pri_quantity_actual,ter_to_sec_quantity_actual, "
            + "value_mrp,value_pts,value_ptr, item_deleted, tax_code_id, quantity_or_free,is_scheme_discount_in_percentage,is_product_discount_in_percentage,hsn_code,"
            + "batch_no,expiry_date,scheme_discount_derived,product_discount_derived,cash_discount_value_derived,invoice_discount_derived,is_tax_code_modified,sort_order,"
            + "service2_value_derived,service_freight_value_derived,is_mrp_changed,kerala_flood_cess_value_derived,actual_selling_price_derived ";
    if (!draft) {
      sql += ",tcs_value_derived ";
    }
    sql += " order by sort_order";
    List<SalesInvoiceItem> list = AppService.list(main, SalesInvoiceItem.class, sql, new Object[]{salesInvoice.getId()});

    //taxCalculator.processSalesInvoiceCalculation(salesInvoice, list);
    //taxCalculator.processSalesInvoiceCalculation(salesInvoice, list);
    List<ProductDetailSales> productDetailSalesList = selectProductDetailSales(main, salesInvoice);
    for (SalesInvoiceItem salesInvoiceItem : list) {
      for (ProductDetailSales pds : productDetailSalesList) {
        if (pds.getProductHash().equals(salesInvoiceItem.getProductHash())) {
          salesInvoiceItem.setProductDetailSales(pds);
        }
      }
      updateSalesInvoiceItemDetails(main, salesInvoice, salesInvoiceItem, false);
    }
    return list;
  }

  private static void updateSalesInvoiceItemDetails(Main main, SalesInvoice salesInvoice, SalesInvoiceItem salesInvoiceItem, boolean lineItem) {
    double pQty;
    ProductBatch productBatch = null;
    ProductDetailSales productDetailSales = null;
    String hash = "";
    boolean isDraft = SystemConstants.DRAFT.equals(salesInvoice.getSalesInvoiceStatusId().getId());
    boolean taxVariation = (salesInvoice.getIsTaxCodeModified() != null && SystemConstants.TAX_CODE_MODIFIED.equals(salesInvoice.getIsTaxCodeModified()));

    if (isDraft && taxVariation && salesInvoiceItem.getIsTaxCodeModified() != null && salesInvoiceItem.getIsTaxCodeModified().equals(SystemConstants.TAX_CODE_MODIFIED)) {
      SalesInvoiceItem sitem = selectByPk(main, new SalesInvoiceItem(Integer.parseInt(salesInvoiceItem.getSalesInvoiceItemHashCode().split("#")[0])));
      if (sitem.getProductEntryDetailId() != null) {
        ProductPricelist pp = (ProductPricelist) AppService.single(main, ProductPricelist.class, "select * from scm_product_pricelist where account_group_price_list_id = ? and product_detail_id = ? and product_entry_detail_id = ? ", new Object[]{salesInvoice.getAccountGroupPriceListId().getId(), sitem.getProductDetailId().getId(), sitem.getProductEntryDetailId().getId()});
        if (pp != null) {
          salesInvoiceItem.setValueProdPieceSelling(MathUtil.roundOff(pp.getValuePtsPerProdPieceSell(), SystemConstants.TRADING_RATE_PRECISION));
          salesInvoiceItem.setTaxCodeId(sitem.getProductDetailId().getProductBatchId().getProductId().getProductCategoryId().getSalesTaxCodeId());
          hash = "";
          int counter = 0;
          for (String hashVal : salesInvoiceItem.getProductHash().split("#")) {
            if (counter == 0) {
              hash += hashVal;
            } else {
              if (counter == 1) {
                hash += "#" + MathUtil.roundOff(pp.getValuePtsPerProdPieceSell(), 2);
              } else {
                hash += "#" + hashVal;
              }
            }
            counter++;
          }
          salesInvoiceItem.setProductHash(hash);
        }
      }
    }
    productBatch = ProductBatchService.selectByPk(main, new ProductBatch(Integer.parseInt(salesInvoiceItem.getProductHash().substring(0, salesInvoiceItem.getProductHash().indexOf("#")))));
    if (productBatch == null) {
      SalesInvoiceItem sitem = selectByPk(main, new SalesInvoiceItem(Integer.parseInt(salesInvoiceItem.getSalesInvoiceItemHashCode().split("#")[0])));
      if (sitem.getProductDetailId() != null) {
        productBatch = sitem.getProductDetailId().getProductBatchId();
      }
    }
    if (productBatch != null) {
      salesInvoiceItem.setProduct(productBatch.getProductId());
      salesInvoiceItem.setIsNearExpiry(productBatch.getExpiryDateSales().compareTo(new Date()));
    }

    salesInvoiceItem.setOldProductQty(salesInvoiceItem.getProductQty());
    salesInvoiceItem.setOldFreeQty(salesInvoiceItem.getProductQtyFree());

    //  List<ProductDetailSales> productDetailSalesList = taxCalculator.selectProductDetailForSales(main, salesInvoice, salesInvoiceItem);
    //  salesInvoiceItem.setProductDetailSalesList(productDetailSalesList);
    if (lineItem) {
      productDetailSales = selectProductDetailSales(main, salesInvoiceItem);
      salesInvoiceItem.setProductDetailSales(productDetailSales);
    } else {
      productDetailSales = salesInvoiceItem.getProductDetailSales();
    }

//      String hashCode = salesInvoiceItem.getProductHash();
//      String h[] = hashCode.split("#");
//      ProductDetailSales ps = new ProductDetailSales();
//      ps.setBatchNo(salesInvoiceItem.getBatchNo());
//      ps.setProductBatchId(Integer.valueOf(h[0]));
//      salesInvoiceItem.setProductDetailSales(ps);
    if (productDetailSales != null && !StringUtil.isEmpty(productDetailSales.getBatchNo()) && isDraft) {
      //        if (productDetailSales.getActualPts() != null) {
//          salesInvoiceItem.setActualPts(productDetailSales.getActualPts());
//        }
      salesInvoiceItem.setValueProdPieceSelling(productDetailSales.getPricelistPts());
      //salesInvoiceItem.setProdPieceSellingForced(productDetailSales.getPricelistPts());
      salesInvoiceItem.setValueMrp(productDetailSales.getMrpValue());
      salesInvoiceItem.setValuePts(productDetailSales.getPricelistPts());
      salesInvoiceItem.setMarginValueDeviationDer(productDetailSales.getMarginValueDeviationDer());
      //salesInvoiceItem.setActualPts(productDetailSales.getActualPts());
      salesInvoiceItem.setValuePtr(salesInvoiceItem.getValuePtr() == null ? productDetailSales.getPricelistPtr() : salesInvoiceItem.getValuePtr());

      if (!StringUtil.isEmpty(productDetailSales.getProductHash())) {

        //if (salesInvoiceItem.getProductQty() > productDetailSales.getQuantityAvailable() && isDraft) {
        if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() > productDetailSales.getQuantityAvailable()) {
          salesInvoiceItem.setProductStockStatus(SystemConstants.PRODUCT_NOT_AVAILABLE_IN_STOCK);
        }
        salesInvoiceItem.setProductStockQuantity(productDetailSales.getQuantityAvailable());
        SalesInvoiceItemUtil.setCurrentAvailableQty(salesInvoiceItem, SalesInvoiceUtil.PRODUCT_EMPTY_QTY);

        // Sets product primary packing 
        if (productDetailSales.getPrimaryPackId() != null) {
          salesInvoiceItem.getProductDetailSales().setPrimaryPacking(ProductPackingService.selectByPk(main, new ProductPacking(productDetailSales.getPrimaryPackId())));
        }
        // Sets product secondary packing
        if (productDetailSales.getSecondaryPackId() != null) {
          salesInvoiceItem.getProductDetailSales().setSecondaryPacking(ProductPackingService.selectByPk(main, new ProductPacking(productDetailSales.getSecondaryPackId())));
        }
        // Sets product tertiary packing
        if (productDetailSales.getTertiaryPackId() != null) {
          salesInvoiceItem.getProductDetailSales().setTertiaryPacking(ProductPackingService.selectByPk(main, new ProductPacking(productDetailSales.getTertiaryPackId())));
        }

        //Product Packing details          
        if (productDetailSales.getTertiaryPackId() != null && salesInvoiceItem.getProductQty() != null) {
          pQty = salesInvoiceItem.getProductQty();
          salesInvoiceItem.setSecondaryQuantity(pQty / productDetailSales.getSecToPriQuantity());
          if (productDetailSales.getTerToSecQuantity() != null && salesInvoiceItem.getSecondaryQuantity() != null) {
            salesInvoiceItem.setTertiaryQuantity(salesInvoiceItem.getSecondaryQuantity() / productDetailSales.getTerToSecQuantity());
          }
        } else if (productDetailSales.getSecToPriQuantity() != null && salesInvoiceItem.getProductQty() != null) {
          pQty = salesInvoiceItem.getProductQty();
          salesInvoiceItem.setSecondaryQuantity(pQty / productDetailSales.getSecToPriQuantity());
        }
        // Finds Possible free Quantity          
        salesInvoiceItem.setPossibleFreeQuantity((productDetailSales.getQuantityAvailable() + productDetailSales.getQuantityFreeAvailable()) - (salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty()));
      } else {
        if (salesInvoice.getSalesInvoiceStatusId().getId().equals(SystemConstants.CONFIRMED)) {
          salesInvoiceItem.setProductStockStatus(SystemConstants.DRAFT);
        }
      }
    }
  }

  /**
   *
   * @param main
   * @param salesInvoiceItem
   * @return
   */
  private static ProductDetailSales selectProductDetailSales(Main main, SalesInvoiceItem salesInvoiceItem) {
    /**
     * SQL function parameters. 1. AccountGroup 2. Product 3. AccountGroupPriceList 4. SalesOrder
     *
     */
    SalesInvoice salesInvoice = salesInvoiceItem.getSalesInvoiceId();
    String sql = "";

    sql = "SELECT product_batch_id, batch_no, expiry_date_actual,to_char(expiry_date_actual,'Mon-yy') expiry_date, mrp_value, pricelist_pts,round(pricelist_ptr,2) pricelist_ptr,free_qty_scheme_id,"
            + "scheme_discount_per,product_discount_per, "
            + "SUM(quantity_available) quantity_available, SUM(quantity_free_available) quantity_free_available, pack_size, "
            + "primary_pack_id,secondary_pack_id,tertiary_pack_id,ter_to_sec_quantity,sec_to_pri_quantity,qty_or_free, "
            + "concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) as product_hash ";
    if (salesInvoice.getSalesInvoiceStatusId().getId() > 1) {
      sql += " ,marg_dev_per as margin_value_deviation_der ";
    }
    sql += "FROM getProductsDetailsForGstSale(?,?,?,?,?) "
            + "WHERE concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) = ? "
            + "and pricelist_pts = ? AND (free_qty_scheme_id = ? or free_qty_scheme_id is null) AND (product_discount_per = ? or product_discount_per is null) "
            + "GROUP by product_batch_id,batch_no, expiry_date_actual,mrp_value,pricelist_pts,round(pricelist_ptr,2),free_qty_scheme_id,scheme_discount_per,product_discount_per,pack_size, "
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity,qty_or_free";
    if (salesInvoice.getSalesInvoiceStatusId().getId() > 1) {
      sql += " ,marg_dev_per";
    }
    ProductDetailSales productDetailSales = (ProductDetailSales) AppDb.single(main.dbConnector(), ProductDetailSales.class, sql,
            new Object[]{salesInvoice.getAccountGroupId().getId(), salesInvoiceItem.getProduct().getId(), salesInvoice.getAccountGroupPriceListId().getId(),
              (salesInvoiceItem.getSalesInvoiceItemHashCode() != null ? salesInvoiceItem.getSalesInvoiceItemHashCode() : null),
              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoice.getInvoiceEntryDate()),
              salesInvoiceItem.getProductHash(), salesInvoiceItem.getValueProdPieceSelling(),
              salesInvoiceItem.getProductFreeQtyScheme() == null ? null : salesInvoiceItem.getProductFreeQtyScheme().getId(),
              salesInvoiceItem.getProductDiscountActual() == null ? 0 : salesInvoiceItem.getProductDiscountActual()});

    return productDetailSales;
  }

  private static List<ProductDetailSales> selectProductDetailSales(Main main, SalesInvoice salesInvoice) {
    /**
     * SQL function parameters. 1. AccountGroup 2. Product 3. AccountGroupPriceList 4. SalesOrder
     *
     */
    String sql = "";

    sql = "SELECT product_batch_id, batch_no, expiry_date_actual,to_char(expiry_date_actual,'Mon-yy') expiry_date, mrp_value, pricelist_pts,round(pricelist_ptr,2) pricelist_ptr,free_qty_scheme_id,"
            + "scheme_discount_per,product_discount_per, "
            + "SUM(quantity_available) quantity_available, SUM(quantity_free_available) quantity_free_available, pack_size, "
            + "primary_pack_id,secondary_pack_id,tertiary_pack_id,ter_to_sec_quantity,sec_to_pri_quantity,qty_or_free, "
            + "concat_ws('#',product_batch_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_per,product_discount_per,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0),pack_size) as product_hash ";
    if (salesInvoice.getSalesInvoiceStatusId().getId() > 1) {
      sql += " ,marg_dev_per as margin_value_deviation_der ";
    }
    sql += "FROM getproductsdetailsforgstsaleconfirm(?,?,?,?,?) "
            + "GROUP by product_batch_id,batch_no, expiry_date_actual,mrp_value,pricelist_pts,round(pricelist_ptr,2),free_qty_scheme_id,scheme_discount_per,product_discount_per,pack_size, "
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity,qty_or_free";
    if (salesInvoice.getSalesInvoiceStatusId().getId() > 1) {
      sql += " ,marg_dev_per";
    }
    List<ProductDetailSales> productDetailSalesList = AppDb.getList(main.dbConnector(), ProductDetailSales.class, sql,
            new Object[]{salesInvoice.getAccountGroupId().getId(), salesInvoice.getAccountGroupPriceListId().getId(),
              salesInvoice.getId(), salesInvoice.getSalesInvoiceStatusId().getId(),
              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesInvoice.getInvoiceEntryDate())});

    return productDetailSalesList;
  }

  /**
   * Select SalesInvoiceItem by key.
   *
   * @param main
   * @param salesInvoiceItem
   * @return SalesInvoiceItem
   */
  public static final SalesInvoiceItem
          selectByPk(Main main, SalesInvoiceItem salesInvoiceItem) {
    return (SalesInvoiceItem) AppService.find(main, SalesInvoiceItem.class,
            salesInvoiceItem.getId());
  }

  /**
   * Insert SalesInvoiceItem.
   *
   * @param main
   * @param salesInvoiceItem
   */
  public static final void insert(Main main, SalesInvoiceItem salesInvoiceItem) {
//    SalesInvoiceItemIs.insertAble(main, salesInvoiceItem);  //Validating
    AppService.insert(main, salesInvoiceItem);

  }

  /**
   * Update SalesInvoiceItem by key.
   *
   * @param main
   * @param salesInvoiceItem
   * @return SalesInvoiceItem
   */
  public static final SalesInvoiceItem updateByPk(Main main, SalesInvoiceItem salesInvoiceItem) {
//    SalesInvoiceItemIs.updateAble(main, salesInvoiceItem); //Validating
    return (SalesInvoiceItem) AppService.update(main, salesInvoiceItem);
  }

  /**
   * Insert or update SalesInvoiceItem
   *
   * @param main
   * @param salesInvoiceItem
   */
  public static void insertOrUpdate(Main main, SalesInvoiceItem salesInvoiceItem) {
    if (salesInvoiceItem.getId() == null) {
      insert(main, salesInvoiceItem);
    } else {
      updateByPk(main, salesInvoiceItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesInvoiceItem
   */
  public static void clone(Main main, SalesInvoiceItem salesInvoiceItem) {
    salesInvoiceItem.setId(null); //Set to null for insert
    insert(main, salesInvoiceItem);
  }

  /**
   * Delete SalesInvoiceItem.
   *
   * @param main
   * @param salesInvoiceItem
   */
  public static final void deleteByPk(Main main, SalesInvoiceItem salesInvoiceItem) {
//    SalesInvoiceItemIs.deleteAble(main, salesInvoiceItem); //Validation
    AppService.deleteSql(main, StockPreSale.class, "delete from scm_stock_presale where sales_invoice_item_id = ?", new Object[]{salesInvoiceItem.getId()});
    AppService.delete(main, SalesInvoiceItem.class, salesInvoiceItem.getId());
  }

  /**
   * Delete Array of SalesInvoiceItem.
   *
   * @param main
   * @param salesInvoiceItem
   */
  public static final void deleteByPkArray(Main main, SalesInvoiceItem[] salesInvoiceItem) {
    for (SalesInvoiceItem e : salesInvoiceItem) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param salesInvoice
   * @param salesInvoiceItem
   * @return
   */
  private static SalesInvoiceItem getSalesInvoiceItem(SalesInvoice salesInvoice, SalesInvoiceItem salesInvoiceItem) {
    double invoiceDisc;
    double schemeDisc;
    double lineDisc;
    double pts;

    salesInvoiceItem.setSalesInvoiceId(salesInvoice);
    invoiceDisc = salesInvoiceItem.getProductDetailSales().getInvoiceDiscountValue() == null ? 0 : salesInvoiceItem.getProductDetailSales().getInvoiceDiscountValue();
    lineDisc = salesInvoiceItem.getProductDetailSales().getProductDiscountPer() == null ? 0 : salesInvoiceItem.getProductDetailSales().getProductDiscountPer();
    schemeDisc = salesInvoiceItem.getProductDetailSales().getSchemeDiscountPer() == null ? 0 : salesInvoiceItem.getProductDetailSales().getSchemeDiscountPer();
    pts = salesInvoiceItem.getProductDetailSales().getPricelistPts() == null ? 0 : salesInvoiceItem.getProductDetailSales().getPricelistPts();
    salesInvoiceItem.setInvoiceDiscountActual(MathUtil.roundOff(invoiceDisc, 2));
    salesInvoiceItem.setSchemeDiscountActual(schemeDisc);
    salesInvoiceItem.setProductDiscountActual(MathUtil.roundOff(lineDisc, 3));
    salesInvoiceItem.setValueProdPieceSelling(pts);
    //salesInvoiceItem.setAccountId(salesInvoiceItem.getProductDetailId().getAccountId());
    salesInvoiceItem.setSecToPriQuantityActual(salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
    salesInvoiceItem.setTerToSecQuantityActual(salesInvoiceItem.getProductDetailSales().getTerToSecQuantity());
    salesInvoiceItem.setProductHash(salesInvoiceItem.getProductDetailSales().getProductHash());
    if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() > 0) {
      if (salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId() != null) {
        ProductFreeQtyScheme scheme = new ProductFreeQtyScheme();
        scheme.setId(salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId());
        salesInvoiceItem.setProductFreeQtyScheme(scheme);
      }
    }
    return salesInvoiceItem;
  }

  /**
   *
   * @param main
   * @param salesInvoiceItemList
   * @return
   */
  private static boolean isProductQuantityAvailableInStock(Main main, SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItemList, TaxCalculator taxCalculator) {
    boolean productNotAvailable = true;
    List<ProductDetailSales> productDetailSaleseList = taxCalculator.selectProductDetailSalesListForConfirm(main, salesInvoice);
    for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItemList) {

      ProductDetailSales pds = null;
      for (ProductDetailSales list : productDetailSaleseList) {
        if (salesInvoiceItem.getProductHash().equals(list.getProductHash())) {
          pds = list;
        }
      }

      /**
       * Checks the requested product is available in stock.
       *
       */
      if (pds != null && !StringUtil.isEmpty(pds.getProductHash())) {
        if (salesInvoiceItem.getProductQty() > pds.getQuantityAvailable()) {
          // The requested quantity not available in stock.
          salesInvoiceItem.setProductStockQuantity(pds.getQuantityAvailable());
          salesInvoiceItem.setProductStockStatus(SystemConstants.REQUESTED_QTY_NOT_AVAILABLE);
          productNotAvailable = false;
        }
      } else {
        // The requested Product Not available  in stock.
        salesInvoiceItem.setProductStockStatus(SystemConstants.PRODUCT_NOT_AVAILABLE);
        productNotAvailable = false;
      }
    }
    return productNotAvailable;
  }

  /**
   *
   * @param main
   * @param salesInvoice
   * @param salesInvoiceItemList
   * @param status
   *
   * @return
   */
  public static final int insertOrUpdateProformaSalesInvoice(Main main, SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItemList, int status, TaxCalculator taxCalculator) {
    List<SalesInvoiceItem> salesInvoiceItems = new ArrayList<>();
    List<SalesInvoiceItem> releasedSalesInvoiceItems = new ArrayList<>();
    List<StockSaleable> confirmedSalesInvoiceItemList;
    StockSaleable stockSaleableBlocked;
    boolean isTransactionBigins = false;
    boolean isProformaInvoice = salesInvoice.getSalesInvoiceType().equals(SystemConstants.SALES_INVOICE_TYPE_PROFORMA);
    List<String> salesInvoiceItemId;

    /**
     *
     */
    for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItemList) {
      if (salesInvoiceItem.getProductDetailId() != null && salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProdPieceSellingForced() != null && salesInvoiceItem.getValuePts() != null) {
        salesInvoiceItem = SalesInvoiceUtil.isProformaItemModified(salesInvoiceItem);
        if (salesInvoiceItem.getId() == null && StringUtil.isEmpty(salesInvoiceItem.getSalesInvoiceItemHashCode())) {
          isTransactionBigins = true;
          insert(main, salesInvoiceItem);
          salesInvoiceItem.setSalesInvoiceItemHashCode(String.valueOf(salesInvoiceItem.getId().intValue()));
          salesInvoiceItems.add(salesInvoiceItem);
        }
      }
    }

    if (isTransactionBigins) {
      main.em().flush();
    }

    if (isProductQuantityAvailableInStock(main, salesInvoice, salesInvoiceItemList, taxCalculator)) {

      for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItemList) {
        if (salesInvoiceItem.getProductDetailId() != null && salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProdPieceSellingForced() != null && salesInvoiceItem.getValuePts() != null) {
          salesInvoiceItemId = Arrays.asList(salesInvoiceItem.getSalesInvoiceItemHashCode().split("#"));
          if (salesInvoiceItem.getProformaItemStatus() != null) {

            if (salesInvoiceItem.getProformaItemStatus().equals(SalesInvoiceUtil.PROFORMA_ITEM_DELETED)) {
              StockSaleableService.releaseBlockedProductsFromStockBySalesInvoiceItem(main, salesInvoiceItemId);
              SalesInvoiceItemService.updateSalesInvoiceItemDeleted(main, salesInvoiceItem.getSalesInvoiceItemHashCode().split("#"));
              main.em().flush();

            } else {
              if (salesInvoiceItem.getProformaItemStatus().equals(SalesInvoiceUtil.PROFORMA_ITEM_FREE_QTY_DELETED)) {
                StockSaleableService.releaseBlockedFreeProductsFromStock(main, salesInvoiceItemId);
                main.em().flush();
              }

              if (salesInvoiceItem.getProformaItemStatus().equals(SalesInvoiceUtil.PROFORMA_ITEM_MODIFIED)) {
                // Releasing blocked products from stock.
                StockSaleableService.releaseBlockedProductsFromStockBySalesInvoiceItem(main, salesInvoiceItemId);
                releasedSalesInvoiceItems.add(salesInvoiceItem);
                SalesInvoiceItemService.updateSalesInvoiceItemDeleted(main, salesInvoiceItem.getSalesInvoiceItemHashCode().split("#"));
              }
            }
          }
        }
      }

      main.em().flush();
      if (!releasedSalesInvoiceItems.isEmpty()) {
        confirmedSalesInvoiceItemList = updateProductQuantityInStock(main, releasedSalesInvoiceItems, salesInvoice, true, taxCalculator);
        if (isProformaInvoice) {
          for (StockSaleable stockSaleable : confirmedSalesInvoiceItemList) {
            stockSaleableBlocked = SalesInvoiceUtil.getStockSaleableBlocked(stockSaleable);
            StockSaleableService.insert(main, stockSaleableBlocked);
          }
        }
      }
      if (!salesInvoiceItems.isEmpty()) {
        confirmedSalesInvoiceItemList = updateProductQuantityInStock(main, salesInvoiceItems, salesInvoice, true, taxCalculator);
        if (isProformaInvoice) {
          for (StockSaleable stockSaleable : confirmedSalesInvoiceItemList) {
            stockSaleableBlocked = SalesInvoiceUtil.getStockSaleableBlocked(stockSaleable);
            StockSaleableService.insert(main, stockSaleableBlocked);
          }
        }
      }

      if (status == SystemConstants.CONFIRMED) {
        salesInvoice = mergeProformaToInvoice(main, salesInvoice);
      }

    } else {
      return SalesInvoiceUtil.REQUESTED_FEW_ITEMS_NOT_AVAILABLE;
    }
    return SalesInvoiceUtil.REQUESTED_ITEMS_SOLD;
  }

  /**
   *
   * @param main
   * @param status
   * @param salesInvoice
   * @param proformaToInvoice
   * @param salesInvoiceItemList
   * @return
   */
  public static final int insertOrUpdateSalesInvoiceItems(Main main, SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItemList, int status, boolean proformaToInvoice, TaxCalculator taxCalculator) {
    SalesInvoiceItem salesItem;

    List<SalesInvoiceItem> salesItemList = getSalesInvoiceItem(salesInvoice, salesInvoiceItemList);
    saveSalesInvoiceItem(main, salesInvoice, salesItemList, taxCalculator);

    if (SystemConstants.CONFIRMED == status) {
      main.em().flush();

      salesInvoiceItemList = selectSalesInvoiceItemBySalesInvoice(main, salesInvoice, taxCalculator, false);
      /**
       * Checks the requested product is available in stock.
       *
       */
      if (isProductQuantityAvailableInStock(main, salesInvoice, salesInvoiceItemList, taxCalculator)) {

        /**
         * Updating the product in stock.
         *
         */
        List<StockSaleable> confirmedSalesInvoiceItemList = updateProductQuantityInStock(main, salesInvoiceItemList, salesInvoice, proformaToInvoice, taxCalculator);
        confirmedSalesInvoiceItemList = null;
        main.em().flush();

      } else {
        return SalesInvoiceUtil.REQUESTED_FEW_ITEMS_NOT_AVAILABLE;
      }
    }
    return SalesInvoiceUtil.REQUESTED_ITEMS_SOLD;
  }

  /**
   * Method to update the sold products quantity in stock.
   *
   * @param main
   * @param salesInvoiceItems
   * @param salesInvoice
   * @return
   */
  private static List<StockSaleable> updateProductQuantityInStock(Main main, List<SalesInvoiceItem> salesInvoiceItems, SalesInvoice salesInvoice, boolean isProforma, TaxCalculator taxCalculator) {
    int productQuantity;
    int productFreeQuantity = 0;
    int productFreeQuantityIn = 0;
    int stockOutQty;
    int counter;
    int currentQty = 0;
    StockSaleable stockSaleable = null;
    StockSaleable parentStockSaleable = null;
    List<StockSaleable> stockSaleableList = new ArrayList<>();
    List<SalesInvoiceItem> salesInvoiceItemList = new ArrayList<>();
    boolean schemeApplicable = false;

    List<ProductDetailSales> productDetailSalesList = taxCalculator.selectCurrentSalesItemsFromStock(main, salesInvoice, isProforma);

    SalesInvoiceItem invoiceItem = null;

    for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItems) {
      counter = 0;
      schemeApplicable = (SystemConstants.FREE_SCHEME.equals(salesInvoiceItem.getQuantityOrFree()));

      if (!schemeApplicable) {
        productQuantity = salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
        if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() > 0) {
          productQuantity += salesInvoiceItem.getProductQtyFree();
          productFreeQuantityIn = salesInvoiceItem.getProductQtyFree();
        }
      } else {
        productQuantity = salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
        productFreeQuantity = salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree();
      }

      stockOutQty = 0;

      for (ProductDetailSales productDetailSales : productDetailSalesList) {
        invoiceItem = null;
        counter++;

        if (salesInvoiceItem.getProductHash().equals(productDetailSales.getProductHash())) {

          if (productQuantity > 0) {

            if (productQuantity <= productDetailSales.getQuantityAvailable()) {
              invoiceItem = SalesInvoiceUtil.getSalesInvoiceItem(salesInvoiceItem, productDetailSales, (productQuantity - productFreeQuantityIn));
              if (productFreeQuantityIn > 0) {
                invoiceItem.setProductQtyFree(productFreeQuantityIn);
              }
              stockOutQty = productQuantity;
              productQuantity = 0;
              productFreeQuantityIn = 0;

            } else if (productQuantity > productDetailSales.getQuantityAvailable() && productDetailSales.getQuantityAvailable() > 0) {
              // line item product quantity have multiple product entry; 
              salesInvoiceItem.setMultipleProductEntry(true);
//              invoiceItem = SalesInvoiceUtil.getSalesInvoiceItem(salesInvoiceItem, productDetailSales, (productDetailSales.getQuantityAvailable() - productFreeQuantityIn));
//              if (productFreeQuantityIn > 0) {
//                invoiceItem.setProductQtyFree(productFreeQuantityIn);
//                productFreeQuantityIn = 0;
//              }
//              stockOutQty = productDetailSales.getQuantityAvailable();
//              productQuantity -= productDetailSales.getQuantityAvailable();

              int qtyAvailable = productDetailSales.getQuantityAvailable();
              int prodQty = salesInvoiceItem.getProductQty() >= qtyAvailable ? qtyAvailable : salesInvoiceItem.getProductQty();
              invoiceItem = SalesInvoiceUtil.getSalesInvoiceItem(salesInvoiceItem, productDetailSales, prodQty);
              qtyAvailable -= prodQty;
              if (productFreeQuantityIn > 0) {
                int freeQty = salesInvoiceItem.getProductQtyFree() >= qtyAvailable ? qtyAvailable : salesInvoiceItem.getProductQtyFree();
                invoiceItem.setProductQtyFree(freeQty);
                productFreeQuantityIn -= freeQty;
              }
              stockOutQty = productDetailSales.getQuantityAvailable();
              productQuantity -= productDetailSales.getQuantityAvailable();
            }
          }

          if (productFreeQuantity > 0) {

            if (salesInvoiceItem.getProductFreeQtyScheme() != null && salesInvoiceItem.getProductFreeQtyScheme().getId().equals(productDetailSales.getFreeQtySchemeId())) {

              if (productFreeQuantity <= productDetailSales.getQuantityFreeAvailable()) {
                if (invoiceItem != null) {
                  invoiceItem.setProductQtyFree(productFreeQuantity);
                  invoiceItem.setProductFreeQtyScheme(salesInvoiceItem.getProductFreeQtyScheme());
                } else {
                  invoiceItem = SalesInvoiceUtil.getSalesInvoiceItem(salesInvoiceItem, productDetailSales, 0);
                  invoiceItem.setProductQtyFree(productFreeQuantity);
                  invoiceItem.setProductFreeQtyScheme(salesInvoiceItem.getProductFreeQtyScheme());
                }

                productFreeQuantity = 0;

              } else if (productFreeQuantity > productDetailSales.getQuantityFreeAvailable() && productDetailSales.getQuantityFreeAvailable() > 0) {
                salesInvoiceItem.setMultipleProductEntry(true);

                if (invoiceItem != null) {
                  invoiceItem.setProductQtyFree(productDetailSales.getQuantityFreeAvailable());
                  invoiceItem.setProductFreeQtyScheme(salesInvoiceItem.getProductFreeQtyScheme());
                } else {
                  invoiceItem = SalesInvoiceUtil.getSalesInvoiceItem(salesInvoiceItem, productDetailSales, 0);
                  invoiceItem.setProductQtyFree(productDetailSales.getQuantityFreeAvailable());
                  invoiceItem.setProductFreeQtyScheme(salesInvoiceItem.getProductFreeQtyScheme());
                }
                productFreeQuantity -= productDetailSales.getQuantityFreeAvailable();
              }
            }
          }

          if (invoiceItem != null) {
            currentQty = productDetailSales.getQuantityAvailable() - stockOutQty;
            productDetailSales.setCurrentStockQty(currentQty);
            productDetailSales.setQuantityAvailable(currentQty);
            salesInvoiceItemList.add(invoiceItem);
          }
        }
        if (counter == productDetailSalesList.size() && productFreeQuantity > 0 && schemeApplicable) {
          for (ProductDetailSales productSalesDetail : productDetailSalesList) {
            if (salesInvoiceItem.getProductHash().equals(productSalesDetail.getProductHash())) {
              if (productFreeQuantity > 0) {
                if (productSalesDetail.getCurrentStockQty() != null) {
                  if (productFreeQuantity <= productSalesDetail.getCurrentStockQty()) {
                    SalesInvoiceItem stockOutSaleable = SalesInvoiceUtil.getSalesInvoiceItemFree(salesInvoiceItem, productSalesDetail, productFreeQuantity);
                    stockOutSaleable.setFreeInOut(SystemConstants.STOCK_SALEABLE_OUT_FOR_FREE);

                    SalesInvoiceItem stockInSaleableAsFree = SalesInvoiceUtil.getSalesInvoiceFreeItem(stockOutSaleable, SystemConstants.STOCK_SALEABLE_AS_FREE_IN);
                    salesInvoiceItemList.add(stockOutSaleable);

                    salesInvoiceItemList.add(stockInSaleableAsFree);

                    invoiceItem = SalesInvoiceUtil.getSalesInvoiceFreeItem(stockInSaleableAsFree, SystemConstants.STOCK_SALEABLE_AS_FREE_OUT);
                    invoiceItem.setProductQtyFree(productFreeQuantity);
                    salesInvoiceItemList.add(invoiceItem);
                    productFreeQuantity = 0;

                  } else if (productQuantity > productSalesDetail.getCurrentStockQty() && productSalesDetail.getCurrentStockQty() > 0) {
                    SalesInvoiceItem stockOutSaleable = SalesInvoiceUtil.getSalesInvoiceItem(salesInvoiceItem, productSalesDetail, productSalesDetail.getCurrentStockQty());
                    stockOutSaleable.setFreeInOut(SystemConstants.STOCK_SALEABLE_OUT_FOR_FREE);
                    SalesInvoiceItem stockInSaleableAsFree = SalesInvoiceUtil.getSalesInvoiceFreeItem(stockOutSaleable, SystemConstants.STOCK_SALEABLE_AS_FREE_IN);
                    salesInvoiceItemList.add(stockOutSaleable);
                    salesInvoiceItemList.add(stockInSaleableAsFree);
                    invoiceItem = SalesInvoiceUtil.getSalesInvoiceFreeItem(stockInSaleableAsFree, SystemConstants.STOCK_SALEABLE_AS_FREE_OUT);
                    invoiceItem.setProductQtyFree(productFreeQuantity);
                    salesInvoiceItemList.add(invoiceItem);
                    productFreeQuantity -= productDetailSales.getCurrentStockQty();
                  }
                }

              }
              Double totalQtyinItem = (invoiceItem.getProductQty() != null ? invoiceItem.getProductQty() : 0) + (invoiceItem.getProductQtyFree() != null ? invoiceItem.getProductQtyFree().doubleValue() : 0);
              invoiceItem.setKeralaFloodCessValue((invoiceItem.getKeralaFloodCessValueDerived() != null ? invoiceItem.getKeralaFloodCessValueDerived() : 0) * totalQtyinItem);
            }
            if (productFreeQuantity == 0) {
              break;
            }
          }
        }
        if (productQuantity == 0 && productFreeQuantity == 0) {
          break;
        }
      }
    }

    for (SalesInvoiceItem salesInvoiceDetails : salesInvoiceItemList) {
      if (salesInvoiceDetails.getFreeInOut().equals(SystemConstants.STOCK_SALEABLE_OUT)) {
        salesInvoiceDetails.setProductEntryDetailId(ProductEntryDetailService.selectByPk(main, salesInvoiceDetails.getProductEntryDetailId()));
        SalesInvoiceItemUtil.calculateSalesInvoiceItemValues(salesInvoiceDetails);
        salesInvoiceDetails.getSalesInvoiceId().setInvoiceEntryDate(salesInvoice.getInvoiceEntryDate());
        SalesInvoiceItemService.insert(main, salesInvoiceDetails);

        if ((salesInvoiceDetails.getProductQty() != null && salesInvoiceDetails.getProductQty() > 0) || (salesInvoiceDetails.getProductQtyFree() != null && salesInvoiceDetails.getProductQtyFree() > 0)) {
          stockSaleable = SalesInvoiceUtil.getStockSaleable(salesInvoiceDetails, SystemConstants.STOCK_TYPE_SALEABLE);
          if (salesInvoiceDetails.getProductFreeQtyScheme() == null && salesInvoiceDetails.getProductQtyFree() != null && salesInvoiceDetails.getProductQtyFree() > 0) {
            stockSaleable.setQuantityOut(salesInvoiceDetails.getProductQty().doubleValue() + salesInvoiceDetails.getProductQtyFree().doubleValue());
            StockSaleableService.insert(main, stockSaleable);
            stockSaleableList.add(stockSaleable);
          } else {
            if (salesInvoiceDetails.getProductQty() > 0) {
              stockSaleable.setQuantityOut(salesInvoiceDetails.getProductQty().doubleValue());
              StockSaleableService.insert(main, stockSaleable);
              stockSaleableList.add(stockSaleable);
            }
          }
        }
        if (salesInvoiceDetails.getProductFreeQtyScheme() != null && salesInvoiceDetails.getProductQtyFree() != null && salesInvoiceDetails.getProductQtyFree() > 0) {
          stockSaleable = SalesInvoiceUtil.getStockSaleable(salesInvoiceDetails, 1);
          stockSaleable.setQuantityOut(salesInvoiceDetails.getProductQtyFree().doubleValue());
          StockSaleableService.insert(main, stockSaleable);
          stockSaleableList.add(stockSaleable);
        }
      } else if (salesInvoiceDetails.getFreeInOut().equals(SystemConstants.STOCK_SALEABLE_OUT_FOR_FREE)) {
        parentStockSaleable = SalesInvoiceUtil.getStockOutSaleable(salesInvoiceDetails);
        parentStockSaleable.setQuantityOut(salesInvoiceDetails.getProductQty().doubleValue());
        StockSaleableService.insert(main, parentStockSaleable);
        //stockSaleableList.add(parentStockSaleable);

      } else if (salesInvoiceDetails.getFreeInOut().equals(SystemConstants.STOCK_SALEABLE_AS_FREE_IN)) {
        stockSaleable = SalesInvoiceUtil.getStockInFreeSaleable(salesInvoiceDetails, parentStockSaleable);
        stockSaleable.setQuantityIn(salesInvoiceDetails.getProductQtyFree().doubleValue());
        StockSaleableService.insert(main, stockSaleable);
        //stockSaleableList.add(stockSaleable);
      } else if (salesInvoiceDetails.getFreeInOut().equals(SystemConstants.STOCK_SALEABLE_AS_FREE_OUT)) {
        salesInvoiceDetails.setValueSale(SalesInvoiceUtil.getSalesItemSaleValue(salesInvoiceDetails));
        SalesInvoiceItemUtil.calculateSalesInvoiceItemValues(salesInvoiceDetails);
        salesInvoiceDetails.setProductEntryDetailId(ProductEntryDetailService.selectByPk(main, salesInvoiceDetails.getProductEntryDetailId()));
        SalesInvoiceItemService.insert(main, salesInvoiceDetails);
        parentStockSaleable = SalesInvoiceUtil.getStockOutFreeSaleable(salesInvoiceDetails, stockSaleable);
        parentStockSaleable.setQuantityOut(salesInvoiceDetails.getProductQtyFree().doubleValue());
        StockSaleableService.insert(main, parentStockSaleable);
        stockSaleableList.add(parentStockSaleable);
      }

    }

    for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItems) {
      List<Integer> ids = Arrays.stream(salesInvoiceItem.getSalesInvoiceItemHashCode().split("#")).map(str -> Integer.parseInt(str)).collect(Collectors.toList());
      main.em().executeJpql("delete from StockPreSale s where s.salesInvoiceItemId.id in ?1 ", new Object[]{ids});
      main.em().executeJpql("delete from SalesInvoiceItem s where s.id in ?1 ", new Object[]{ids});
    }
    //  }

    return stockSaleableList;
  }

  /**
   *
   * @param main
   * @param salesInvoice
   * @param salesInvoiceItemId
   * @param taxCalculator
   */
  public static SalesInvoiceItem insertOrUpdateSalesInvoiceItem(Main main, SalesInvoice salesInvoice, SalesInvoiceItem salesInvoiceItemArgs, List<ProductDetailSales> pdSalesList, TaxCalculator taxCalculator, Integer prevSortOrder, boolean save) {
    StockPreSale stockPreSaleOut = null;
    StockPreSale stockPreSaleFreeOut = null;
    StockPreSale stockPreSaleToStockOut = null;
    StockPreSale stockPreSaleConvertIn = null;
    StockPreSale stockPreSaleConvertOut = null;
    StockPreSale stockPreSaleToSaleable = null;
    List<Integer> salesInvoiceItemId = Collections.EMPTY_LIST;
    SalesInvoiceItem salesInvoiceItem = null, salesInvoiceItemOutput = null;

    Integer stockId = null;
    Integer freeStockId = null;
    String[] stockIds;

    int stockStatus = SystemConstants.SALES_INVOICE_TYPE_PROFORMA.equals(salesInvoice.getSalesInvoiceType()) ? StockStatusService.STOCK_STATUS_BLOCKED : StockStatusService.STOCK_STATUS_SALEABLE;
    boolean schemeApplicable = SystemConstants.FREE_SCHEME.equals(salesInvoiceItemArgs.getQuantityOrFree());

    int productQty;
    int productFreeQty = 0;
    int productFreeQuantityIn = 0;
    int counter = 0;
    int sortOrder = prevSortOrder == null ? 1 : prevSortOrder + 1;

    Integer sumOfProductQty = 0;
    Integer sumOfProductFreeQty = 0;
    Double sumOfSchemeDiscountValue = 0.0;
    Double sumOfProductDiscountValue = 0.0;
    Double sumOfInvoiceDiscountValue = 0.0;
    Double sumOfCashDiscountValue = 0.0;
    Double sumOfValueSale = 0.0;
    Double sumOfKeralaFloodCessValue = 0.0;
    StringBuilder itemIdHashCode = new StringBuilder();

    /**
     * Checking free scheme : if free scheme applicable then both product quantity will be the saleable quantity and free quantity considered as free. If free scheme not applicable
     * then both product quantity and saleable quantity will considered as saleable quantity.
     */
    if (!schemeApplicable) {
      productQty = salesInvoiceItemArgs.getProductQty() == null ? 0 : salesInvoiceItemArgs.getProductQty();
      if (salesInvoiceItemArgs.getProductQtyFree() != null && salesInvoiceItemArgs.getProductQtyFree() > 0) {
        productQty += salesInvoiceItemArgs.getProductQtyFree();
        productFreeQuantityIn = salesInvoiceItemArgs.getProductQtyFree();
      }
    } else {
      productQty = salesInvoiceItemArgs.getProductQty() == null ? 0 : salesInvoiceItemArgs.getProductQty();
      productFreeQty = salesInvoiceItemArgs.getProductQtyFree() == null ? 0 : salesInvoiceItemArgs.getProductQtyFree();
    }

    if (!StringUtil.isEmpty(salesInvoiceItemArgs.getSalesInvoiceItemHashCode())) {
      salesInvoiceItemId = Arrays.stream(salesInvoiceItemArgs.getSalesInvoiceItemHashCode().split("#")).map(str -> Integer.parseInt(str)).collect(Collectors.toList());
    }
    if (!salesInvoiceItemId.isEmpty()) {
      main.em().executeJpql("delete from StockPreSale s where s.salesInvoiceItemId.id in ?1 ", new Object[]{salesInvoiceItemId});
    }

    /**
     * Getting stock details of the product.
     */
    List<ProductDetailSales> productDetailSalesList = pdSalesList;
    if (StringUtil.isEmpty(productDetailSalesList)) {
      productDetailSalesList = taxCalculator.getProductDetailCurrentStockAvailability(main, salesInvoiceItemArgs,
              salesInvoice.getAccountGroupId(), salesInvoiceItemArgs.getProduct(), salesInvoice.getAccountGroupPriceListId());
      if (productDetailSalesList == null) {
        throw new UserMessageException("error.batch.missing");
      }
    }
    for (ProductDetailSales productDetailSales : productDetailSalesList) {
      Double detailQty = (productDetailSales.getQuantityAvailable() != null ? productDetailSales.getQuantityAvailable() : 0.0)
              + (productDetailSales.getQuantityFreeAvailable() != null ? productDetailSales.getQuantityFreeAvailable() : 0.0);
      if (detailQty > 0) {
        salesInvoiceItem = SalesInvoiceItemUtil.getSalesInvoiceItem(salesInvoiceItemArgs);
        if (salesInvoiceItemOutput == null) {
          salesInvoiceItemOutput = SalesInvoiceItemUtil.getSalesInvoiceItem(salesInvoiceItemArgs);
        }

        /**
         * Getting stock id of corresponding product. A product can have saleable quantity stock id and free quantity stock id.
         */
        stockIds = productDetailSales.getStockSaleableId().split("#");
        if (stockIds.length > 1) {
          stockId = Integer.parseInt(stockIds[0]);
          freeStockId = Integer.parseInt(stockIds[1]);
        } else {
          stockId = Integer.parseInt(stockIds[0]);
        }
        salesInvoiceItem.setMarginValueDeviationDer(productDetailSales.getMarginValueDeviationDer());
        salesInvoiceItem.setLandingRate(productDetailSales.getLandingRate());

        /**
         * Checking available stock quantity
         */
        if (productDetailSales.getQuantityAvailable() != null && productDetailSales.getQuantityAvailable() > 0 && productQty > 0) {
          if (productQty <= productDetailSales.getQuantityAvailable()) {

            salesInvoiceItem.setProductQty(productQty - productFreeQuantityIn);
            salesInvoiceItem.setProductDetailId(ProductDetailService.selectByPk(main, new ProductDetail(productDetailSales.getProductDetailId())));
            salesInvoiceItem.setAccountId(salesInvoiceItem.getProductDetailId().getAccountId());

            if (productFreeQuantityIn > 0) {
              salesInvoiceItem.setProductQtyFree(productFreeQuantityIn);
            }

            stockPreSaleOut = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, stockId, stockStatus, SystemConstants.STOCK_TYPE_SALEABLE);
            stockPreSaleOut.setQuantityOut(salesInvoiceItem.getProductQty() + productFreeQuantityIn);

            salesInvoiceItem.setProductEntryDetailId(stockPreSaleOut.getProductEntryDetailId());

            productDetailSales.setQuantityAvailable(productDetailSales.getQuantityAvailable() - productQty);

            productQty = 0;
            productFreeQuantityIn = 0;
            salesInvoiceItem.setMarginValueDeviationDer(productDetailSales.getMarginValueDeviationDer());
          } else if (productQty > productDetailSales.getQuantityAvailable()) {
//          int qtyAvailable = productDetailSales.getQuantityAvailable();
//          if (salesInvoiceItem.getProductQty() >= qtyAvailable) {
//            salesInvoiceItem.setProductQty(qtyAvailable);
//          } else {
//            salesInvoiceItem.setProductQtyFree(qtyAvailable - salesInvoiceItem.getProductQty());
//            productFreeQuantityIn -= salesInvoiceItem.getProductQtyFree();
//          }

            int qtyAvailable = productDetailSales.getQuantityAvailable();
            int prodQty = salesInvoiceItem.getProductQty() >= qtyAvailable ? qtyAvailable : salesInvoiceItem.getProductQty();
            salesInvoiceItem.setProductQty(prodQty);
            int qtyOut = prodQty;
            qtyAvailable -= prodQty;
            if (productFreeQuantityIn > 0) {
              int freeQty = productFreeQuantityIn >= qtyAvailable ? qtyAvailable : productFreeQuantityIn;
              qtyOut += freeQty;
              salesInvoiceItem.setProductQtyFree(freeQty);
              productFreeQuantityIn -= freeQty;
            }
            salesInvoiceItem.setProductDetailId(ProductDetailService.selectByPk(main, new ProductDetail(productDetailSales.getProductDetailId())));
            salesInvoiceItem.setAccountId(salesInvoiceItem.getProductDetailId().getAccountId());

            stockPreSaleOut = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, stockId, stockStatus, SystemConstants.STOCK_TYPE_SALEABLE);
            stockPreSaleOut.setQuantityOut(qtyOut);
            salesInvoiceItem.setProductEntryDetailId(stockPreSaleOut.getProductEntryDetailId());
            salesInvoiceItem.setMarginValueDeviationDer(productDetailSales.getMarginValueDeviationDer());
//          if (productFreeQuantityIn > 0) {
//            salesInvoiceItem.setProductQtyFree(productFreeQuantityIn - (salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree()));
//            productFreeQuantityIn -= salesInvoiceItem.getProductQtyFree();
//          }
            productQty -= productDetailSales.getQuantityAvailable();
            productDetailSales.setQuantityAvailable(0);
          }
        }

        /**
         * If requested saleable quantity is available then adjusting it with free quantity.
         */
        if (productQty > 0 && productDetailSales.getQuantityFreeAvailable() != null && productDetailSales.getQuantityFreeAvailable() > 0 && schemeApplicable) {
          stockPreSaleToSaleable = SalesInvoiceItemUtil.getFreeToSaleable(productQty, salesInvoice, salesInvoiceItem, productDetailSales, freeStockId, SystemConstants.STOCK_TYPE_FREE);
          stockPreSaleConvertIn = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToSaleable, SystemConstants.STOCK_SALEABLE_AS_FREE_IN, SystemConstants.STOCK_TYPE_SALEABLE);
          stockPreSaleConvertOut = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToSaleable, SystemConstants.STOCK_SALEABLE_AS_FREE_OUT, SystemConstants.STOCK_TYPE_SALEABLE);

          productQty -= stockPreSaleToSaleable.getQuantityIn();
          salesInvoiceItem.setProductQty((salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty()) + stockPreSaleToSaleable.getQuantityIn());
        }

        /**
         * Checking free stock quantity
         */
        if (productFreeQty > 0) {
          if (productDetailSales.getQuantityFreeAvailable() != null && productDetailSales.getQuantityFreeAvailable() > 0 && schemeApplicable) {
            if (productFreeQty <= productDetailSales.getQuantityFreeAvailable()) {
              salesInvoiceItem.setProductQtyFree(productFreeQty);
              productDetailSales.setQuantityFreeAvailable(productDetailSales.getQuantityFreeAvailable() - productFreeQty);
              productFreeQty = 0;

              stockPreSaleFreeOut = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, freeStockId, stockStatus, SystemConstants.STOCK_TYPE_FREE);
              stockPreSaleFreeOut.setQuantityOut(salesInvoiceItem.getProductQtyFree());

              if (productDetailSales.getFreeQtySchemeId() != null) {
                salesInvoiceItem.setProductFreeQtyScheme(SalesInvoiceItemUtil.getProductFreeQtyScheme(productDetailSales.getFreeQtySchemeId()));
                stockPreSaleFreeOut.setFreeSchemeId(salesInvoiceItem.getProductFreeQtyScheme());
              }
            } else if (productFreeQty > productDetailSales.getQuantityFreeAvailable()) {
              salesInvoiceItem.setProductQtyFree(productDetailSales.getQuantityFreeAvailable());

              stockPreSaleFreeOut = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, freeStockId, stockStatus, SystemConstants.STOCK_TYPE_FREE);
              stockPreSaleFreeOut.setQuantityOut(productDetailSales.getQuantityFreeAvailable());

              productFreeQty -= productDetailSales.getQuantityFreeAvailable();
              productDetailSales.setQuantityFreeAvailable(0);

              if (productDetailSales.getFreeQtySchemeId() != null) {
                salesInvoiceItem.setProductFreeQtyScheme(SalesInvoiceItemUtil.getProductFreeQtyScheme(productDetailSales.getFreeQtySchemeId()));
                stockPreSaleFreeOut.setFreeSchemeId(salesInvoiceItem.getProductFreeQtyScheme());

              }
            }
          }
        }

        /**
         * If requested free quantity is not available, adjusting it with saleable quantity.
         */
        if (productFreeQty > 0 && productDetailSales.getQuantityAvailable() != null && productDetailSales.getQuantityAvailable() > 0 && schemeApplicable) {
          stockPreSaleToStockOut = SalesInvoiceItemUtil.getSaleableToFree(productFreeQty, salesInvoice, salesInvoiceItem, productDetailSales, stockId, StockStatusService.STOCK_TYPE_SALEABLE);
          stockPreSaleConvertIn = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToStockOut, SystemConstants.STOCK_SALEABLE_AS_FREE_IN, StockStatusService.STOCK_TYPE_FREE);
          stockPreSaleConvertOut = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToStockOut, SystemConstants.STOCK_SALEABLE_AS_FREE_OUT, StockStatusService.STOCK_TYPE_FREE);

          salesInvoiceItem.setProductQtyFree((salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree()) + stockPreSaleToStockOut.getQuantityOut());
          productFreeQty -= stockPreSaleToStockOut.getQuantityOut();
        }

        if (salesInvoiceItem.getProductQtyFree() == null) {
          salesInvoiceItem.setSchemeDiscountValue(null);
          //salesInvoiceItem.setSchemeDiscountPercentage(null);        
        }
        salesInvoiceItem.setProductEntryDetailId(ProductEntryDetailService.selectByPk(main, salesInvoiceItem.getProductEntryDetailId()));
        SalesInvoiceItemUtil.calculateSalesInvoiceItemValues(salesInvoiceItem);

        if (!salesInvoiceItemId.isEmpty() && counter < salesInvoiceItemId.size()) {
          salesInvoiceItem.setId(salesInvoiceItemId.get(counter));
        }

        SalesInvoiceItemService.insertOrUpdate(main, salesInvoiceItem);
        salesInvoiceItem.setSalesInvoiceItemHashCode(String.valueOf(salesInvoiceItem.getId()));

        if (itemIdHashCode.length() == 0) {
          itemIdHashCode.append(salesInvoiceItem.getSalesInvoiceItemHashCode());
        } else {
          itemIdHashCode.append("#").append(salesInvoiceItem.getSalesInvoiceItemHashCode());
        }

        sumOfProductQty += salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
        sumOfProductFreeQty += salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree();
        sumOfSchemeDiscountValue += salesInvoiceItem.getSchemeDiscountValue() == null ? 0 : salesInvoiceItem.getSchemeDiscountValue();
        sumOfProductDiscountValue += salesInvoiceItem.getProductDiscountValue() == null ? 0 : salesInvoiceItem.getProductDiscountValue();
        sumOfInvoiceDiscountValue += salesInvoiceItem.getInvoiceDiscountValue() == null ? 0 : salesInvoiceItem.getInvoiceDiscountValue();
        sumOfCashDiscountValue += salesInvoiceItem.getCashDiscountValue() == null ? 0 : salesInvoiceItem.getCashDiscountValue();
        sumOfValueSale += salesInvoiceItem.getValueSale() == null ? 0 : salesInvoiceItem.getValueSale();
        sumOfKeralaFloodCessValue += salesInvoiceItem.getKeralaFloodCessValue() == null ? 0 : salesInvoiceItem.getKeralaFloodCessValue();

        if (salesInvoiceItem.getSortOrder() == null) {
          salesInvoiceItem.setSortOrder(sortOrder);
        }

        SalesInvoiceItemService.updateByPk(main, salesInvoiceItem);
        insertStockPreSale(main, stockPreSaleOut);
        insertStockPreSale(main, stockPreSaleFreeOut);
        insertStockPreSale(main, stockPreSaleToSaleable);
        insertStockPreSale(main, stockPreSaleToStockOut);
        insertStockPreSale(main, stockPreSaleConvertIn);
        insertStockPreSale(main, stockPreSaleConvertOut);
        counter++;
        if (productQty == 0 && productFreeQty == 0) {
          break;
        }
      }
    }

    if (counter < salesInvoiceItemId.size()) {
      for (int i = counter; i < salesInvoiceItemId.size(); i++) {
        main.em().executeJpql("delete from SalesInvoiceItem s where s.id = ?1 ", new Object[]{salesInvoiceItemId.get(i)});
      }
    }
    main.em().flush();

    if (!save) {
      if (salesInvoiceItemOutput != null) {
        salesInvoiceItemOutput.setId(salesInvoiceItem.getSortOrder());
        salesInvoiceItemOutput.setSortOrder(salesInvoiceItem.getSortOrder());
        salesInvoiceItemOutput.setProductQty(sumOfProductQty == 0 ? null : sumOfProductQty);
        salesInvoiceItemOutput.setProductQtyFree(sumOfProductFreeQty == 0 ? null : sumOfProductFreeQty);
        salesInvoiceItemOutput.setSchemeDiscountValue(sumOfSchemeDiscountValue == 0 ? null : sumOfSchemeDiscountValue);
        salesInvoiceItemOutput.setProductDiscountValue(sumOfProductDiscountValue == 0 ? null : sumOfProductDiscountValue);
        salesInvoiceItemOutput.setInvoiceDiscountValue(sumOfInvoiceDiscountValue == 0 ? null : sumOfInvoiceDiscountValue);
        salesInvoiceItemOutput.setCashDiscountValue(sumOfCashDiscountValue == 0 ? null : sumOfCashDiscountValue);
        salesInvoiceItemOutput.setKeralaFloodCessValue(sumOfKeralaFloodCessValue == 0 ? null : sumOfKeralaFloodCessValue);
        salesInvoiceItemOutput.setValueSale(sumOfValueSale == 0 ? null : sumOfValueSale);
        salesInvoiceItemOutput.setKeralaFloodCessValueDerived(salesInvoiceItem.getKeralaFloodCessValueDerived());
        //commented and passing as it is with out converting to null
//      salesInvoiceItemOutput.setProductQty(sumOfProductQty);
//      salesInvoiceItemOutput.setProductQtyFree(sumOfProductFreeQty);
//      salesInvoiceItemOutput.setSchemeDiscountValue(sumOfSchemeDiscountValue);
//      salesInvoiceItemOutput.setProductDiscountValue(sumOfProductDiscountValue);
//      salesInvoiceItemOutput.setInvoiceDiscountValue(sumOfInvoiceDiscountValue);
//      salesInvoiceItemOutput.setCashDiscountValue(sumOfCashDiscountValue);
//      salesInvoiceItemOutput.setKeralaFloodCessValue(sumOfKeralaFloodCessValue);
//      salesInvoiceItemOutput.setValueSale(sumOfValueSale);

        salesInvoiceItemOutput.setSalesInvoiceItemHashCode(itemIdHashCode.toString());
        updateSalesInvoiceItemDetails(main, salesInvoice, salesInvoiceItemOutput, true);
      }
    }
    return salesInvoiceItemOutput;
  }

  /**
   *
   * @param main
   * @param stockPreSale
   */
  private static void insertStockPreSale(Main main, StockPreSale stockPreSale) {
    if (stockPreSale != null) {
      StockPreSaleService.insert(main, stockPreSale);
    }
  }

  /**
   *
   * @param main
   * @param salesInvoice
   * @return
   */
  private static SalesInvoice mergeProformaToInvoice(Main main, SalesInvoice salesInvoice) {
    Integer salesInvoiceItemId;
    SalesInvoiceItem salesItem;

    List<SalesInvoiceItem> salesInvoiceItemList = selectBlockedSalesInvoiceItem(main, salesInvoice);

    SalesInvoice invoice = SalesInvoiceUtil.getSalesInvoice(salesInvoice);
    invoice.setInvoiceNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesInvoice.getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, true, salesInvoice.getFinancialYearId()));
    invoice.setParentId(salesInvoice);
    SalesInvoiceService.insert(main, invoice);

    for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItemList) {
      salesInvoiceItemId = salesInvoiceItem.getId();
      salesItem = SalesInvoiceUtil.cloneSalesInvoiceItem(salesInvoiceItem);
      salesItem.setSalesInvoiceId(invoice);
      insert(main, salesItem);
      StockSaleableService.releaseBlockedItemAndSaleableOut(main, salesInvoiceItemId, salesItem);
    }
    return invoice;
  }

  /**
   *
   * @param main
   * @param salesInvoiceItems
   */
  private static void updateSalesInvoiceItemDeleted(Main main, String[] salesInvoiceItems) {
    for (String id : salesInvoiceItems) {
      main.clear();
      main.param(SalesInvoiceUtil.PROFORMA_ITEM_DELETED);
      main.param(Integer.parseInt(id));
      AppService
              .updateSql(main, StockSaleable.class,
                      "update scm_sales_invoice_item set item_deleted = ? where id = ?", false);
      main.clear();

    }

  }

  /**
   *
   * @param main
   * @param salesInvoice
   * @return
   */
  private static List<SalesInvoiceItem> selectBlockedSalesInvoiceItem(Main main, SalesInvoice salesInvoice) {
    List<SalesInvoiceItem> list = AppService.list(main, SalesInvoiceItem.class,
            "select * from scm_sales_invoice_item where sales_invoice_id = ? and item_deleted = 0 order by id asc", new Object[]{salesInvoice.getId()});
    return list;
  }

  /**
   *
   * @param main
   * @param accountGroupId
   * @param product
   * @param accountGroupPriceListId
   * @return
   */
  public static List<ProductDetailSales> getProductCurrentStockAvailability(Main main, AccountGroup accountGroupId, Product product, AccountGroupPriceList accountGroupPriceListId) {
    /**
     * SQL Function - getProductsDetailsForSale(?,?,?,?) params. 1. Account Group Id. 2.Product Id 3.Account Group price list id. 4.Sales invoice item id.
     *
     */
    List<ProductDetailSales> productDetailSales = AppDb.getList(main.dbConnector(), ProductDetailSales.class,
            "select string_agg(stock_id::varchar, '#') stockSaleableId, product_entry_detail_id,product_detail_id, sum(quantity_available) quantity_available,  sum(quantity_free_available) quantity_free_available, "
            + "concat_ws('#',product_detail_id,pricelist_pts,round(pricelist_ptr,2),coalesce(free_qty_scheme_id,0),scheme_discount_value,product_discount_value,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0)) as product_hash, "
            + "expiry_date_actual,product_detail_id,batch_no, mrp_value,pricelist_pts,free_qty_scheme_id,scheme_discount_value,product_discount_value,pack_size, "
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity, account_id "
            + "from getProductsDetailsForGstSale(?,?,?,null,?) "
            + "group by product_entry_detail_id,expiry_date_actual,product_detail_id,batch_no, mrp_value,pricelist_pts,pricelist_ptr,free_qty_scheme_id,scheme_discount_value,product_discount_value,pack_size, "
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity, account_id "
            + "order by expiry_date_actual ASC, product_entry_detail_id ASC",
            new Object[]{accountGroupId == null ? null : accountGroupId.getId(),
              product == null ? null : product.getId(),
              accountGroupPriceListId == null ? null : accountGroupPriceListId.getId(),
              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date())});
    return productDetailSales;
  }

  public static List<InvoiceItem> selectSalesInvoiceItemForPrint(Main main, SalesInvoice salesInvoice) {
    List<InvoiceItem> list = null;
    list
            = AppDb.getList(main.dbConnector(), InvoiceItem.class,
                    "select t1.sort_order,t1.hsn_code,t6.product_name,t2.pack_size,t5.name as manufacture_name,t5.code as mfg_code,t1.batch_no,t6.packing_description as pack_description, \n"
                    + "to_char(t1.expiry_date,'Mon-yy') as expiry_date, "
                    + "round(t1.value_mrp,2) as value_mrp,round(t1.value_ptr,2) value_ptr,round(t1.value_pts,2) value_pts,round(t1.prod_piece_selling_forced,2) as value_rate,round(SUM(t1.value_taxable),2) as value_taxable, "
                    + "round(t1.scheme_discount_percentage,2) scheme_discount_percentage,round(t1.product_discount_percentage,2) product_discount_percentage,t1.product_hash,sum(t1.product_qty) product_qty, "
                    + "CASE WHEN sum(coalesce(t1.product_qty_free,0)) = 0 then NULL ELSE sum(coalesce(t1.product_qty_free,0)) END as product_qty_free, "
                    + "CASE WHEN sum(coalesce(t1.scheme_discount_value,0)) = 0 THEN NULL ELSE sum(coalesce(t1.scheme_discount_value,0)) END as scheme_discount_value, "
                    + "CASE WHEN sum(coalesce(t1.product_discount_value,0)) = 0 THEN NULL ELSE sum(coalesce(t1.product_discount_value,0)) END product_discount_value, "
                    + "CASE WHEN sum(coalesce(t1.cash_discount_value,0)) = 0 THEN NULL ELSE sum(coalesce(t1.cash_discount_value,0)) END cash_discount_value, "
                    + "CASE WHEN sum(coalesce(t1.invoice_discount_value,0)) = 0 THEN NULL ELSE sum(coalesce(t1.invoice_discount_value,0)) END invoice_discount_value, "
                    + "sum(t1.value_goods) value_goods,sum(t1.value_sale) value_sale,sum(t1.value_igst) value_igst,sum(t1.value_cgst) value_cgst,sum(t1.value_sgst) value_sgst, "
                    + "igst.rate_percentage as igst_tax_rate,cgst.rate_percentage as cgst_tax_rate,sgst.rate_percentage as sgst_tax_rate "
                    + "from scm_sales_invoice_item t1 "
                    + "left outer join scm_product_entry_detail t2 on t1.product_entry_detail_id = t2.id "
                    + "left outer join scm_product_detail t3 on t1.product_detail_id = t3.id "
                    + "left outer join scm_product_batch t4 on t3.product_batch_id = t4.id "
                    + "left outer join scm_manufacture t5 on t4.manufacture_id = t5.id "
                    + "left outer join scm_product t6 on t4.product_id = t6.id "
                    + "left outer join scm_tax_code igst on t1.tax_code_id = igst.id "
                    + "left outer join scm_tax_code cgst on cgst.parent_id = igst.id and cgst.tax_type = ? "
                    + "left outer join scm_tax_code sgst on sgst.parent_id = igst.id and sgst.tax_type = ? "
                    + "where t1.sales_invoice_id = ? and (t1.item_deleted = 0 or t1.item_deleted is null) "
                    + "GROUP BY t1.sort_order,t1.hsn_code,t6.product_name,t2.pack_size,t5.name,t5.code,t1.batch_no,t1.expiry_date, t6.packing_description,"
                    + "t1.value_mrp,t1.value_ptr,t1.value_pts,t1.prod_piece_selling_forced,t1.product_discount_percentage,t1.scheme_discount_percentage,t1.product_hash, "
                    + "t1.prod_piece_selling_forced,igst.rate_percentage,cgst.rate_percentage,sgst.rate_percentage"
                    + " ORDER BY t1.sort_order",
                    new Object[]{AccountingConstant.TAX_TYPE_CGST, AccountingConstant.TAX_TYPE_SGST, salesInvoice.getId()});
    return list;
  }

  public static void deleteBySalesInvoiceItemHashCode(MainView main, SalesInvoiceItem salesInvoiceItem) {
    if (!StringUtil.isEmpty(salesInvoiceItem.getSalesInvoiceItemHashCode())) {
      for (String sid : salesInvoiceItem.getSalesInvoiceItemHashCode().split("#")) {
        deleteByPk(main, new SalesInvoiceItem(Integer.parseInt(sid)));
      }
      main.em().flush();
    }
  }

  private static List<SalesInvoiceItem> getSalesInvoiceItem(SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItemList) {
    List<SalesInvoiceItem> inoiveItemList = new ArrayList<>();
    for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItemList) {
      if (salesInvoiceItem.getTaxCodeId() != null && (salesInvoiceItem.getProductQty() != null || salesInvoiceItem.getProductQtyFree() != null) && salesInvoiceItem.getProdPieceSellingForced() != null && salesInvoiceItem.getValuePts() != null) {

        double invoiceDisc;
        double schemeDisc;
        double lineDisc;
        double pts;

        salesInvoiceItem.setSalesInvoiceId(salesInvoice);
        invoiceDisc = salesInvoiceItem.getProductDetailSales().getInvoiceDiscountValue() == null ? 0 : salesInvoiceItem.getProductDetailSales().getInvoiceDiscountValue();
        lineDisc = salesInvoiceItem.getProductDetailSales().getProductDiscountPer() == null ? 0 : salesInvoiceItem.getProductDetailSales().getProductDiscountPer();
        schemeDisc = salesInvoiceItem.getProductDetailSales().getSchemeDiscountPer() == null ? 0 : salesInvoiceItem.getProductDetailSales().getSchemeDiscountPer();
        pts = salesInvoiceItem.getProductDetailSales().getPricelistPts() == null ? 0 : salesInvoiceItem.getProductDetailSales().getPricelistPts();
        salesInvoiceItem.setInvoiceDiscountActual(MathUtil.roundOff(invoiceDisc, 2));
        salesInvoiceItem.setSchemeDiscountActual(schemeDisc);
        salesInvoiceItem.setProductDiscountActual(MathUtil.roundOff(lineDisc, 3));
        salesInvoiceItem.setValueProdPieceSelling(pts);
        //salesInvoiceItem.setAccountId(salesInvoiceItem.getProductDetailId().getAccountId());
        salesInvoiceItem.setSecToPriQuantityActual(salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
        salesInvoiceItem.setTerToSecQuantityActual(salesInvoiceItem.getProductDetailSales().getTerToSecQuantity());
        salesInvoiceItem.setProductHash(salesInvoiceItem.getProductDetailSales().getProductHash());
        if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() > 0) {
          if (salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId() != null) {
            ProductFreeQtyScheme scheme = new ProductFreeQtyScheme();
            scheme.setId(salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId());
            salesInvoiceItem.setProductFreeQtyScheme(scheme);
          }
        }
        inoiveItemList.add(salesInvoiceItem);
      }
    }
    return inoiveItemList;
  }

  public static void saveSalesInvoiceItem(Main main, SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItemList, TaxCalculator taxCalculator) {

    List<ProductDetailSales> salesInvoiceProductDetailSalesList = taxCalculator.getProductDetailCurrentStockAvailabilityforSave(main, salesInvoice);
    for (SalesInvoiceItem salesInvoiceItemArgs : salesInvoiceItemList) {
      List<ProductDetailSales> productDetailSalesList = new ArrayList<>();
      for (ProductDetailSales list : salesInvoiceProductDetailSalesList) {
        if (list.getProductHash().equals(salesInvoiceItemArgs.getProductHash())) {
          productDetailSalesList.add(list);
        }
      }
      insertOrUpdateSalesInvoiceItem(main, salesInvoice, salesInvoiceItemArgs, productDetailSalesList, taxCalculator, salesInvoiceItemList.get(salesInvoiceItemList.size() - 1).getSortOrder(), true);
    }
  }

}
