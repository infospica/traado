/*
 * @(#)PurchaseReturnItemService.java	1.0 Wed May 25 13:23:32 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.List;
import spica.fin.domain.TaxCode;
import spica.fin.service.TaxCodeService;
import spica.scm.common.RelatedItems;
import spica.scm.common.ReturnItem;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.PurchaseReturn;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.PurchaseReturnItem;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.domain.PurchaseReturnItemStatus;
import spica.scm.domain.StockNonSaleable;
import spica.scm.domain.StockSaleable;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.entity.core.AppDb;

/**
 * PurchaseReturnItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:32 IST 2016
 */
public abstract class PurchaseReturnItemService {

  /**
   * PurchaseReturnItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReturnItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_return_item", PurchaseReturnItem.class, main);
    sql.main("select scm_purchase_return_item.id,scm_purchase_return_item.purchase_return_id,scm_purchase_return_item.product_detail_id,scm_purchase_return_item.product_entry_detail_id,"
            + "scm_purchase_return_item.purchase_return_item_status_id,scm_purchase_return_item.stock_saleable_id,scm_purchase_return_item.stock_non_saleable_id,"
            + "scm_purchase_return_item.actual_qty,scm_purchase_return_item.quantity_returned,scm_purchase_return_item.tax_code_id,scm_purchase_return_item.is_tax_code_modified,"
            + "scm_purchase_return_item.value_net,"
            + "scm_purchase_return_item.value_mrp,scm_purchase_return_item.landing_price_per_piece_company,"
            + "scm_purchase_return_item.value_cgst,scm_purchase_return_item.value_sgst,scm_purchase_return_item.value_igst,product_key "
            + "from scm_purchase_return_item scm_purchase_return_item "); //Main query
    sql.count("select count(scm_purchase_return_item.id) from scm_purchase_return_item scm_purchase_return_item "); //Count query
    sql.join("left outer join scm_purchase_return scm_purchase_return on (scm_purchase_return.id = scm_purchase_return_item.purchase_return_id) "
            + "left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_purchase_return_item.product_detail_id) "
            + "left outer join scm_purchase_return_itemstatus scm_purchase_return_itemstatus on (scm_purchase_return_itemstatus.id = scm_purchase_return_item.purchase_return_item_status_id) "
            + "left outer join scm_product_entry_detail scm_product_entry_detail on (scm_product_entry_detail.id = scm_purchase_return_item.product_entry_detail_id) "
            + "left outer join scm_stock_saleable scm_stock_saleable on (scm_stock_saleable.id = scm_purchase_return_item.stock_saleable_id) "
            + "left outer join scm_tax_code scm_tax_code on (scm_tax_code.id = scm_purchase_return_item.tax_code_id) "
            + "left outer join scm_stock_non_saleable scm_stock_non_saleable on (scm_stock_non_saleable.id = scm_purchase_return_item.stock_non_saleable_id)"); //Join Query

    sql.string(new String[]{"scm_purchase_return.invoice_no", "scm_product_detail.batch_no", "scm_purchase_return_itemstatus.title"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_return_item.id", "scm_purchase_return_item.quantity_returned", "scm_purchase_return_item.product_detail_id",
      "scm_purchase_return_item.product_entry_detail_id", "scm_purchase_return_item.stock_saleable_id", "scm_purchase_return_item.stock_non_saleable_id",
      "scm_purchase_return_item.value_net"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseReturnItem.
   *
   * @param main
   * @return List of PurchaseReturnItem
   */
  public static final List<PurchaseReturnItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseReturnItemSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseReturnItem based on condition
//   * @param main
//   * @return List<PurchaseReturnItem>
//   */
//  public static final List<PurchaseReturnItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseReturnItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PurchaseReturnItem by key.
   *
   * @param main
   * @param purchaseReturnItem
   * @return PurchaseReturnItem
   */
  public static final PurchaseReturnItem selectByPk(Main main, PurchaseReturnItem purchaseReturnItem) {
    return (PurchaseReturnItem) AppService.find(main, PurchaseReturnItem.class, purchaseReturnItem.getId());
  }

  /**
   * Insert PurchaseReturnItem.
   *
   * @param main
   * @param purchaseReturnItem
   */
  public static final void insert(Main main, PurchaseReturnItem purchaseReturnItem) {
    AppService.insert(main, purchaseReturnItem);
  }

  /**
   * Update PurchaseReturnItem by key.
   *
   * @param main
   * @param purchaseReturnItem
   * @return PurchaseReturnItem
   */
  public static final PurchaseReturnItem updateByPk(Main main, PurchaseReturnItem purchaseReturnItem) {
    return (PurchaseReturnItem) AppService.update(main, purchaseReturnItem);
  }

  /**
   * Insert or update PurchaseReturnItem
   *
   * @param main
   * @param purchaseReturnItem
   */
  public static void insertOrUpdate(Main main, PurchaseReturnItem purchaseReturnItem) {
    if (purchaseReturnItem.getId() == null) {
      insert(main, purchaseReturnItem);
    } else {
      updateByPk(main, purchaseReturnItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReturnItem
   */
  public static void clone(Main main, PurchaseReturnItem purchaseReturnItem) {
    purchaseReturnItem.setId(null); //Set to null for insert
    insert(main, purchaseReturnItem);
  }

  /**
   * Delete PurchaseReturnItem.
   *
   * @param main
   * @param purchaseReturnItem
   */
  public static final void deleteByPk(Main main, PurchaseReturnItem purchaseReturnItem) {
    if (purchaseReturnItem.getId() != null) {
      AppService.delete(main, PurchaseReturnItem.class, purchaseReturnItem.getId());
    }
  }

  /**
   * Delete Array of PurchaseReturnItem.
   *
   * @param main
   * @param purchaseReturnItem
   */
  public static final void deleteByPkArray(Main main, PurchaseReturnItem[] purchaseReturnItem) {
    for (PurchaseReturnItem e : purchaseReturnItem) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param purchaseReturnItemList
   */
  public static List<PurchaseReturnItem> insertPurchaseReturnItemList(Main main, PurchaseReturn purchaseReturn, List<PurchaseReturnItemReplica> purchaseReturnItemList) {

    double returnQty = 0.0;
    PurchaseReturnItem pitem = null;
    List<ReturnItem> stockItemList = null;
    List<PurchaseReturnItem> purchaseReturnItemsList = new ArrayList<>();
    if (SystemConstants.CONFIRMED.equals(purchaseReturn.getPurchaseReturnStatusId().getId())) {
      if (PurchaseReturnStockCatService.STOCK_DAMAGED_AND_EXPIRED == purchaseReturn.getPurchaseReturnStockCat().getId()) {
        stockItemList = selectProductFromStock(main, purchaseReturn, null, purchaseReturn.getPurchaseReturnStockCat().getId());
      } else {
        stockItemList = selectProductFromStock(main, purchaseReturn, "GL", purchaseReturn.getPurchaseReturnStockCat().getId());
      }
    }
    for (PurchaseReturnItemReplica purchaseReturnItem : purchaseReturnItemList) {

      if (SystemConstants.DRAFT.equals(purchaseReturn.getPurchaseReturnStatusId().getId())) {
        ProductEntryDetail ped = new ProductEntryDetail(purchaseReturnItem.getProductEntryDetailId());
        purchaseReturnItem.setProductEntryDetail(ped);
        insertOrUpdate(main, new PurchaseReturnItem(purchaseReturnItem, purchaseReturnItem.getId()));
      } else if (SystemConstants.CONFIRMED.equals(purchaseReturn.getPurchaseReturnStatusId().getId())) {
        returnQty = purchaseReturnItem.getQuantityReturned();

        for (ReturnItem item : stockItemList) {
          if (item.getInvoiceProductKey().equals(purchaseReturnItem.getInvoiceProductKey())) {
            if (returnQty != 0) {
              pitem = getPurchaseReturnItem(purchaseReturn, purchaseReturnItem, item);
              pitem.setQuantityReturned(returnQty);
              insertOrUpdate(main, pitem);
              insertPurchaseReturnPlatformEntries(main, purchaseReturn, pitem);
              purchaseReturnItemsList.add(pitem);
            } else {
              break;
            }
          }
        }
      }
    }
    return purchaseReturnItemsList;
  }

  private static void insertPurchaseReturnPlatformEntries(Main main, PurchaseReturn purchaseReturn, PurchaseReturnItem purchaseReturnItem) {

    double rateDifference = purchaseReturnItem.getActualLandingPricePerPieceCompany() - purchaseReturnItem.getLandingPricePerPieceCompany();
    if (Math.abs(rateDifference) > 1) {
      rateDifference = purchaseReturnItem.getQuantityReturned() * rateDifference;
      if (rateDifference > 0) {
        PlatformService.insertPurchaseReturn(main, purchaseReturn.getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, Math.abs(rateDifference), null, purchaseReturn, purchaseReturnItem, PlatformService.NORMAL_FUND_STATE);
      } else {
        PlatformService.insertPurchaseReturn(main, purchaseReturn.getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, null, Math.abs(rateDifference), purchaseReturn, purchaseReturnItem, PlatformService.NORMAL_FUND_STATE);
      }
    }
  }

  private static PurchaseReturnItem getPurchaseReturnItem(PurchaseReturn purchaseReturn, PurchaseReturnItemReplica purchaseReturnItem, ReturnItem item) {
    PurchaseReturnItem pitem = new PurchaseReturnItem(purchaseReturnItem);
    pitem.setProductEntryDetailId(new ProductEntryDetail(item.getProductEntryDetailId()));
    pitem.setActualQty(item.getQuantityAvailable());
    if (PurchaseReturnStockCatService.STOCK_DAMAGED_AND_EXPIRED == purchaseReturn.getPurchaseReturnStockCat().getId()) {
      pitem.setStockNonSaleableId(new StockNonSaleable(item.getStockId()));
    } else if (PurchaseReturnStockCatService.STOCK_NON_MOVING_AND_NEAR_EXPIRY == (purchaseReturn.getPurchaseReturnStockCat().getId())) {
      pitem.setStockSaleableId(new StockSaleable(item.getStockId()));
    }
    return pitem;
  }

  /**
   *
   * @param main
   * @param purchaseReturn
   * @return
   */
  public static List<PurchaseReturnItemReplica> listPurchaseReturnItemByPurchaseReturn(Main main, PurchaseReturn purchaseReturn) {
    if (purchaseReturn.getId() == null) {
      return null;
    }

    List<PurchaseReturnItemReplica> list = AppDb.getList(main.dbConnector(), PurchaseReturnItemReplica.class,
            "select t1.id,t1.product_detail_id,t1.product_entry_detail_id,t2.product_entry_id,t1.tax_code_id,t1.purchase_return_item_status_id,sum(coalesce(t1.quantity_returned,0)) as quantity_returned, "
            + "sum(coalesce(t1.actual_qty,0)) as actual_qty,sum(t1.value_net) as value_net,t1.value_mrp,sum(t1.value_cgst) value_cgst,sum(t1.value_sgst) as value_sgst,"
            + "t1.reference_no,t1.reference_date,t1.ref_sales_invoice_id,t1.tax_code_id, "
            + "sum(t1.value_igst) as value_igst,t1.landing_price_per_piece_company,sum(t1.value_goods),t1.is_tax_code_modified,t1.actual_landing_price_per_piece_company,"
            + "t1.product_discount_value,t1.product_discount_perc,t1.product_discount_value_derived,t1.is_product_discount_in_percentage,t1.hsn_code, "
            //            + "string_agg(t1.id::varchar, '#') as purchase_return_item_hash,"   -- Grouping based on purchase_return_item_hash was removed (By Godson) 
            + "t1.product_key,t1.value_rate,t1.actual_value_rate,t1.scheme_discount_value,t1.scheme_discount_percentage,t1.invoice_discount_value,t1.invoice_discount_percentage "
            + "from scm_purchase_return_item t1 left join scm_product_entry_detail t2 on t1.product_entry_detail_id= t2.id where t1.purchase_return_id = ? "
            + "group by t1.id,t1.product_detail_id,t2.product_entry_id,t1.tax_code_id,t1.purchase_return_item_status_id,t1.value_mrp,t1.product_entry_detail_id, "
            + "t1.product_discount_value,t1.product_discount_perc,t1.product_discount_value_derived,t1.is_product_discount_in_percentage,t1.ref_sales_invoice_id,"
            + "t1.landing_price_per_piece_company,t1.is_tax_code_modified,t1.actual_landing_price_per_piece_company,t1.product_key,t1.value_rate,t1.actual_value_rate,"
            + "t1.scheme_discount_value,t1.scheme_discount_percentage,t1.invoice_discount_value,t1.invoice_discount_percentage,t1.reference_no,t1.hsn_code,t1.reference_date "
            + "ORDER BY t1.id", new Object[]{purchaseReturn.getId()});

    for (PurchaseReturnItemReplica purchaseReturnItemReplica : list) {
      purchaseReturnItemReplica.setPurchaseReturn(purchaseReturn);
      purchaseReturnItemReplica.setProductDetail(ProductDetailService.selectByPk(main, new ProductDetail(purchaseReturnItemReplica.getProductDetailId())));
      if (purchaseReturnItemReplica.getProductEntryDetailId() != null) {
        purchaseReturnItemReplica.setProductEntryDetail(ProductEntryDetailService.selectByPk(main, new ProductEntryDetail(purchaseReturnItemReplica.getProductEntryDetailId())));
      }
      purchaseReturnItemReplica.setTaxCode(TaxCodeService.selectByPk(main, new TaxCode(purchaseReturnItemReplica.getTaxCodeId())));
      purchaseReturnItemReplica.setPurchaseReturnItemStatus(PurchaseReturnItemstatusService.selectByPk(main, new PurchaseReturnItemStatus(purchaseReturnItemReplica.getPurchaseReturnItemStatusId())));
    }

    return list;
    //return AppService.list(main, PurchaseReturnItem.class, "select * from scm_purchase_return_item where purchase_return_id = ? ", new Object[]{purchaseReturn.getId()});
  }

  /**
   *
   * @param main
   * @param purchaseReturn
   * @return
   */
  public static boolean isPurchaseReturnItemExist(Main main, PurchaseReturn purchaseReturn) {
    if (purchaseReturn != null) {
      return AppService.exist(main, "select 1 from scm_purchase_return_item where purchase_return_id = ?", new Object[]{purchaseReturn.getId()});
    }
    return false;
  }

  public static void updatePurchaseReturnItemQty(Main main, PurchaseReturnItem purchaseReturnItem) {
    main.clear();
    main.param(purchaseReturnItem.getActualQty());
    main.param(purchaseReturnItem.getId());
    AppService.updateSql(main, PurchaseReturnItem.class, "update scm_purchase_return_item set actual_qty = ? where id = ?", false);
    main.clear();
  }

  public static List<ReturnItem> selectProductFromStock(Main main, PurchaseReturn purchaseReturn, String productType, Integer purchaseReturnType) {
    List<ReturnItem> returnItem = AppDb.getList(main.dbConnector(), ReturnItem.class, "SELECT * FROM getproductsdetailsforpurchasereturngst(?,?,?,?,?,?) ",
            new Object[]{purchaseReturn.getAccountId().getId(), purchaseReturnType, productType, null, SystemRuntimeConfig.SDF_YYYY_MM_DD.format(purchaseReturn.getEntryDate()), 1});
    return returnItem;
  }

//  public static void deleteByPurchaseReturnHash(Main main, PurchaseReturnItemReplica purchaseReturnItem) {
//    deleteByPk(main,purchaseReturnItem);
//    main.em().flush();
//  }
//  public static List selectProductByNameAndBatch(String filter, Account accountId) {
//    String sql = "select * from scm_product "
//            + "where upper(product_name) like upper(?) and id in(select product_id from scm_product_batch "
//            + "where id in(select product_batch_id from scm_product_detail where account_id = ?))";
//    if (!StringUtil.isEmpty(filter)) {
//      return AppLookup.getAutoFilter(Product.class,sql,new Object[]{"%" + filter.toUpperCase() + "%", accountId.getId()});
//    }
//    return AppLookup.getAutoFilter(Product.class, "select * from scm_product "
//            + "where id in(select product_id from scm_product_batch "
//            + "where id in(select product_batch_id from scm_product_detail where account_id = ?)) ",
//            new Object[]{accountId.getId()});
//  }
  public static List<ProductBatch> batchByProduct(Main main, Product product, String filter) {
    return AppService.list(main, ProductBatch.class, "select * from scm_product_batch where product_id = ? and UPPER(batch_no) LIKE ?",
            new Object[]{product.getId(), "%" + filter.toUpperCase() + "%"});
  }

  public static List<RelatedItems> getRelatedItemsList(Main main, Integer productEntryDetailId, String entryDate) {
    List<RelatedItems> list = new ArrayList<>();
    if (productEntryDetailId != null) {
      list = AppDb.getList(main.dbConnector(), RelatedItems.class, "SELECT entity_id as id,invoice_no,source_type as title FROM getProductReturnSourceDetails(?,?)",
              new Object[]{productEntryDetailId, entryDate});
    }
    return list;
  }
}
