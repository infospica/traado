/*
 * @(#)StockNonSaleableService.java	1.0 Wed May 25 16:00:02 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.common.ReturnItem;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.PurchaseReturnItem;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.domain.StockAdjustmentItem;
import spica.scm.domain.StockMovementItem;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.StockNonSaleable;
import spica.scm.domain.StockSaleable;
import spica.scm.domain.StockStatus;
import spica.scm.validate.StockNonSaleableIs;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;

/**
 * StockNonSaleableService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 16:00:02 IST 2016
 */
public abstract class StockNonSaleableService {

  /**
   * StockNonSaleable paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStockNonSaleableSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_stock_non_saleable", StockNonSaleable.class, main);
    sql.main("select scm_stock_non_saleable.id,scm_stock_non_saleable.product_detail_id,scm_stock_non_saleable.account_id,scm_stock_non_saleable.stock_status_id, "
            + "scm_stock_non_saleable.quantity_in,scm_stock_non_saleable.quantity_out,scm_stock_non_saleable.product_entry_detail_id, "
            + "scm_stock_non_saleable.ref_product_entry_detail_id,scm_stock_non_saleable.sales_invoice_item_id,scm_stock_non_saleable.sales_return_type,"
            + "scm_stock_non_saleable.sales_return_detail_id,scm_stock_non_saleable.purchase_return_item_id "
            + "from scm_stock_non_saleable scm_stock_non_saleable "); //Main query
    sql.count("select count(scm_stock_non_saleable.id) from scm_stock_non_saleable scm_stock_non_saleable "); //Count query
    sql.join("left outer join scm_product_detail scm_stock_non_saleableproduct_detail_id on (scm_stock_non_saleableproduct_detail_id.id = scm_stock_non_saleable.product_detail_id) "
            + "left outer join scm_account scm_stock_non_saleableaccount_id on (scm_stock_non_saleableaccount_id.id = scm_stock_non_saleable.account_id) "
            + "left outer join scm_stock_status scm_stock_non_saleablestock_status_id on (scm_stock_non_saleablestock_status_id.id = scm_stock_non_saleable.stock_status_id) "
            + "left outer join scm_product_entry_detail scm_product_entry_detail on (scm_product_entry_detail.id = scm_stock_non_saleable.product_entry_detail_id) "
            + "left outer join scm_product_entry_detail scm_product_entry_detail_ref on (scm_product_entry_detail_ref.id = scm_stock_non_saleable.ref_product_entry_detail_id) "
            + "left outer join scm_product_entry_detail scm_sales_return_detail on (scm_sales_return_detail.id = scm_stock_non_saleable.sales_return_detail_id) "
            + "left outer join scm_sales_invoice_item scm_sales_invoice_item on (scm_sales_invoice_item.id = scm_stock_non_saleable.sales_invoice_item_id) "
            + "left outer join scm_purchase_return_item scm_purchase_return_item on (scm_purchase_return_item.id = scm_stock_non_saleable.purchase_return_item_id) "); //Join Query

    sql.string(new String[]{"scm_stock_non_saleableproduct_detail_id.batch_no", "scm_stock_non_saleableaccount_id.account_code", "scm_stock_non_saleablestock_status_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_stock_non_saleable.id", "scm_stock_non_saleable.quantity_in", "scm_stock_non_saleable.quantity_out"}); //Numeric search or sort fields    
    return sql;
  }

  /**
   * Return List of StockNonSaleable.
   *
   * @param main
   * @return List of StockNonSaleable
   */
  public static final List<StockNonSaleable> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStockNonSaleableSqlPaged(main));
  }

//  /**
//   * Return list of StockNonSaleable based on condition
//   * @param main
//   * @return List<StockNonSaleable>
//   */
//  public static final List<StockNonSaleable> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getStockNonSaleableSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select StockNonSaleable by key.
   *
   * @param main
   * @param stockNonSaleable
   * @return StockNonSaleable
   */
  public static final StockNonSaleable selectByPk(Main main, StockNonSaleable stockNonSaleable) {
    return (StockNonSaleable) AppService.find(main, StockNonSaleable.class, stockNonSaleable.getId());
  }

  /**
   * Insert StockNonSaleable.
   *
   * @param main
   * @param stockNonSaleable
   */
  public static final void insert(Main main, StockNonSaleable stockNonSaleable) {
    StockNonSaleableIs.insertAble(main, stockNonSaleable);  //Validating
    AppService.insert(main, stockNonSaleable);
    
  }

  /**
   * Update StockNonSaleable by key.
   *
   * @param main
   * @param stockNonSaleable
   * @return StockNonSaleable
   */
  public static final StockNonSaleable updateByPk(Main main, StockNonSaleable stockNonSaleable) {
    StockNonSaleableIs.updateAble(main, stockNonSaleable); //Validating
    return (StockNonSaleable) AppService.update(main, stockNonSaleable);
  }

  /**
   * Insert or update StockNonSaleable
   *
   * @param main
   * @param stockNonSaleable
   */
  public static void insertOrUpdate(Main main, StockNonSaleable stockNonSaleable) {
    if (stockNonSaleable.getId() == null) {
      insert(main, stockNonSaleable);
    } else {
      updateByPk(main, stockNonSaleable);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param stockNonSaleable
   */
  public static void clone(Main main, StockNonSaleable stockNonSaleable) {
    stockNonSaleable.setId(null); //Set to null for insert
    insert(main, stockNonSaleable);
  }

  /**
   * Delete StockNonSaleable.
   *
   * @param main
   * @param stockNonSaleable
   */
  public static final void deleteByPk(Main main, StockNonSaleable stockNonSaleable) {
    StockNonSaleableIs.deleteAble(main, stockNonSaleable); //Validation
    AppService.delete(main, StockNonSaleable.class, stockNonSaleable.getId());
  }

  /**
   * Delete Array of StockNonSaleable.
   *
   * @param main
   * @param stockNonSaleable
   */
  public static final void deleteByPkArray(Main main, StockNonSaleable[] stockNonSaleable) {
    for (StockNonSaleable e : stockNonSaleable) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param accountId
   * @param stockCategoryId
   * @param purchaseReturnId
   * @return
   */
  public static final List<ReturnItem> selectPurchaseReturnStockItemList(Main main, Integer accountId, Integer stockCategoryId, Integer purchaseReturnId) {

    /**
     * The SQL function getProductsDetailsForPurchaseReturn(?,?,?,?) parameters 1. Account Id 2. Commodity Id 3. Product Type Id 4. Damaged(1) or Non-Moving(2) flag
     */
    String sql = "SELECT * FROM getProductsDetailsForPurchaseReturn(?,?,?,?)";
    Object[] params;
    if (purchaseReturnId != null) {
      sql += " where stock_id not in(select stock_non_saleable_id from scm_purchase_return_item where purchase_return_id=?)";
      params = new Object[]{accountId, null, null, stockCategoryId, purchaseReturnId};
    } else {
      params = new Object[]{accountId, null, null, stockCategoryId};
    }
    
    List<ReturnItem> list = AppDb.getList(main.dbConnector(), ReturnItem.class, sql, params);
    return list;
  }

  /**
   *
   * @param main
   * @param accountId
   * @param stockCategoryId
   * @param purchaseReturnId
   * @return
   */
  public static final List<ReturnItem> purchaseReturnStockItemList(Main main, Integer accountId, Integer stockCategoryId, Integer purchaseReturnId) {
    /**
     * The SQL function getProductsDetailsForPurchaseReturn(?,?,?,?) parameters 1. Account Id 2. Commodity Id 3. Product Type Id 4. Damaged(1) or Non-Moving(2) flag
     */
    String sql = "SELECT * FROM getProductsDetailsForPurchaseReturn(?,?,?,?)";
    Object[] params;
    if (purchaseReturnId != null) {
      sql += " where stock_id in(select stock_non_saleable_id from scm_purchase_return_item where purchase_return_id=?)";
      params = new Object[]{accountId, null, null, stockCategoryId, purchaseReturnId};
    } else {
      params = new Object[]{accountId, null, null, stockCategoryId};
    }
    List<ReturnItem> list = AppDb.getList(main.dbConnector(), ReturnItem.class, sql, params);
    return list;
  }

  /**
   *
   * @param main
   * @param purchaseReturnItemList
   */
  public static void insertPurchaseReturnProductDetails(Main main, List<PurchaseReturnItem> purchaseReturnItemList) {
    for (PurchaseReturnItem purchaseReturnItem : purchaseReturnItemList) {
      StockNonSaleable stockNonSaleable = new StockNonSaleable();
      if (purchaseReturnItem.getPurchaseReturnId() != null && purchaseReturnItem.getPurchaseReturnId().getAccountId() != null) {
        stockNonSaleable.setAccountId(purchaseReturnItem.getPurchaseReturnId().getAccountId());
      }
      stockNonSaleable.setEntryDate(purchaseReturnItem.getPurchaseReturnId().getEntryDate());
      stockNonSaleable.setProductDetailId(purchaseReturnItem.getProductDetailId());
      stockNonSaleable.setRefProductEntryDetailId(purchaseReturnItem.getProductEntryDetailId());
      stockNonSaleable.setPurchaseReturnItemId(purchaseReturnItem);
      stockNonSaleable.setQuantityOut(purchaseReturnItem.getQuantityReturned().doubleValue());
      stockNonSaleable.setStockNonSaleableId(purchaseReturnItem.getStockNonSaleableId());
      stockNonSaleable.setStockType(purchaseReturnItem.getStockType());
      if (purchaseReturnItem.getProductStockStatus() != null) {
        if (purchaseReturnItem.getProductStockStatus().equals(SystemConstants.PRODUCT_STOCK_STATUS_DAMAGED)) {
          stockNonSaleable.setStockStatusId(UserRuntimeService.STOCK_DAMAGED);
        } else if (purchaseReturnItem.getPurchaseReturnItemStatusId().getId().equals(SystemConstants.PRODUCT_STOCK_STATUS_EXPIRED)) {
          stockNonSaleable.setStockStatusId(UserRuntimeService.STOCK_EXPIRED);
        }
      }
      insert(main, stockNonSaleable);
    }
  }

  /**
   *
   * @param main
   * @param purchaseReturnItem
   * @param productDetailId
   */
  public static void deleteByPurchaseReturnAndProductDetail(MainView main, PurchaseReturnItemReplica purchaseReturnItem, ProductDetail productDetailId) {
    for (String id : purchaseReturnItem.getPurchaseReturnItemHash().split("#")) {
      AppService.deleteSql(main, StockNonSaleable.class, "delete from scm_stock_non_saleable where purchase_return_item_id = ? and product_detail_id = ?", new Object[]{Integer.parseInt(id), productDetailId.getId()});      
    }    
  }
  
  public static void adjustDamagedStock(Main main, StockAdjustmentItem stockAdjustmentItem) {
    StockNonSaleable stockNonSaleable = new StockNonSaleable();
    stockNonSaleable.setAccountId(stockAdjustmentItem.getProductDetailId().getAccountId());
    stockNonSaleable.setEntryDate(stockAdjustmentItem.getStockAdjustmentId().getEntryDate());
    stockNonSaleable.setProductDetailId(stockAdjustmentItem.getProductDetailId());
    stockNonSaleable.setRefProductEntryDetailId(stockAdjustmentItem.getProductEntryDetailId());
    
    stockNonSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_DAMAGED));
    stockNonSaleable.setStockType(0);
    stockNonSaleable.setStockAdjustmentItemId(stockAdjustmentItem);
    if (stockAdjustmentItem.getAdjustedQty() > 0) {
      stockNonSaleable.setQuantityIn(stockAdjustmentItem.getAdjustedQty().doubleValue());
    } else if (stockAdjustmentItem.getAdjustedQty() < 0) {
      stockNonSaleable.setQuantityOut(stockAdjustmentItem.getAdjustedQty().doubleValue() * -1);
    }
    insert(main, stockNonSaleable);
  }
  
  public static void stockMovementDamagedToSaleable(Main main, StockMovementItem stockMovementItem) {
    StockNonSaleable stockNonSaleable = new StockNonSaleable();
    stockNonSaleable.setAccountId(stockMovementItem.getProductDetailId().getAccountId());
    stockNonSaleable.setEntryDate(stockMovementItem.getStockMovementId().getEntryDate());
    stockNonSaleable.setProductDetailId(stockMovementItem.getProductDetailId());
    stockNonSaleable.setRefProductEntryDetailId(stockMovementItem.getProductEntryDetailId());
    stockNonSaleable.setQuantityOut(stockMovementItem.getQuantitySaleableActual().doubleValue());
    stockNonSaleable.setStockType(SystemConstants.PRODUCT_QTY_TYPE_SALEABLE);
    stockNonSaleable.setStockNonSaleableId(new StockNonSaleable(stockMovementItem.getStockId()));
    stockNonSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_SALEABLE));
    stockNonSaleable.setStockMovementItemId(stockMovementItem);
    insert(main, stockNonSaleable);
    
    StockSaleable stockSaleable = new StockSaleable();
    stockSaleable.setAccountId(stockMovementItem.getProductDetailId().getAccountId());
    stockSaleable.setEntryDate(stockMovementItem.getStockMovementId().getEntryDate());
    stockSaleable.setProductDetailId(stockMovementItem.getProductDetailId());
    stockSaleable.setRefProductEntryDetailId(stockMovementItem.getProductEntryDetailId());
    stockSaleable.setQuantityIn(stockMovementItem.getQuantitySaleableActual().doubleValue());
    stockSaleable.setStockType(SystemConstants.PRODUCT_QTY_TYPE_SALEABLE);
    stockSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_SALEABLE));
    stockSaleable.setStockMovementItemId(stockMovementItem);
    StockSaleableService.insert(main, stockSaleable);
  }

  /**
   *
   * @param main
   * @param stockAdjustmentItemList
   */
  public static void stockAdjustmentExcessToSaleable(Main main, List<StockAdjustmentItem> stockAdjustmentItemList) {
    for (StockAdjustmentItem stockAdjustmentItem : stockAdjustmentItemList) {
      StockNonSaleable stockNonSaleable = new StockNonSaleable(stockAdjustmentItem, StockStatusService.STOCK_STATUS_EXCESS, SystemConstants.PRODUCT_QTY_TYPE_SALEABLE);
      stockNonSaleable.setQuantityOut(stockAdjustmentItem.getQuantityExcessActual().doubleValue());
      insertOrUpdate(main, stockNonSaleable);
      
      StockSaleable stockSaleable = new StockSaleable(stockAdjustmentItem, StockStatusService.STOCK_STATUS_SALEABLE, SystemConstants.PRODUCT_QTY_TYPE_SALEABLE);
      stockSaleable.setQuantityIn(stockAdjustmentItem.getQuantityExcessActual().doubleValue());
      StockSaleableService.insertOrUpdate(main, stockSaleable);
      
      PlatformService.insertPurchase(main, stockSaleable.getAccountId(), SystemRuntimeConfig.EXCESS, null, (stockSaleable.getQuantityIn() * stockAdjustmentItem.getProductEntryDetailId().getExpectedLandingRate()), stockAdjustmentItem.getProductEntryDetailId().getProductEntryId(), stockAdjustmentItem.getProductEntryDetailId(), PlatformService.NORMAL_FUND_STATE);
    }
  }

  /**
   *
   * @param main
   * @param stockAdjustmentItemList
   */
  public static void stockAdjustmentExcessFlushOut(Main main, List<StockAdjustmentItem> stockAdjustmentItemList) {
    StockNonSaleable stockNonSaleable = null;
    for (StockAdjustmentItem stockAdjustmentItem : stockAdjustmentItemList) {
      stockNonSaleable = new StockNonSaleable(stockAdjustmentItem, StockStatusService.STOCK_STATUS_EXCESS, SystemConstants.PRODUCT_QTY_TYPE_SALEABLE);
      stockNonSaleable.setQuantityOut(stockAdjustmentItem.getQuantityExcessActual().doubleValue());
      insertOrUpdate(main, stockNonSaleable);
    }
    
  }
}
