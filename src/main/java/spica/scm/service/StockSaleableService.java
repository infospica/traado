/*
 * @(#)StockSaleableService.java	1.0 Wed May 25 16:00:02 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import spica.scm.common.ReturnItem;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.PurchaseReturnItem;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.domain.StockAdjustmentItem;
import spica.scm.domain.StockMovementItem;
import spica.scm.domain.StockNonSaleable;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.StockSaleable;
import spica.scm.domain.StockStatus;
import spica.scm.validate.StockSaleableIs;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.entity.core.AppDb;

/**
 * StockSaleableService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 16:00:02 IST 2016
 */
public abstract class StockSaleableService {

  public static final int STOCK_ITEM_DAMAGED_EXPIRED = 1;
  public static final int STOCK_ITEM_NON_MOVING_NEAR_EXPIRY = 2;
  public static final int PURCHASE_RETURN_FROM_SALES_RETURN = 2;
  public static final int PURCHASE_RETURN_FROM_STOCK = 1;

  private static final int STOCK_TYPE_PRODUCT_QTY = 0;
  private static final int STOCK_TYPE_FREE_QTY = 1;
  private static final int SALEABLE_QTY = 1;
  private static final int FREE_QTY = 2;
  private static final int EXCESS_QTY = 3;
  private static final int SHORTAGE_QTY = 4;
  private static final int DAMAGED_QTY = 5;
  private static final int EXPIRED_QTY = 6;
  private static final int FREE_DAMAGED_QTY = 7;
  private static final int FREE_EXPIRED_QTY = 8;
  private static final int FREE_SHORTAGE_QTY = 9;
  private static final Integer STOCK_MOVEMENT_SALEABLE_OUT = 1;
  private static final Integer STOCK_MOVEMENT_SALEABLE_IN = 2;
  private static final Integer STOCK_MOVEMENT_SALEABLE_FREE_IN = 3;
  private static final Integer STOCK_MOVEMENT_SALEABLE_FREE_OUT = 4;
  private static final Integer STOCK_MOVEMENT_SALEABLE_BLOCKED_IN = 5;
  private static final Integer STOCK_MOVEMENT_SALEABLE_BLOCKED_OUT = 6;

  /**
   * StockSaleable paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStockSaleableSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_stock_saleable", StockSaleable.class, main);
    sql.main("select scm_stock_saleable.id, scm_stock_saleable.product_detail_id, scm_stock_saleable.account_id, "
            + "scm_stock_saleable.quantity_in, scm_stock_saleable.quantity_out, scm_stock_saleable.stock_status_id, "
            + "scm_stock_saleable.product_entry_detail_id, scm_stock_saleable.ref_product_entry_detail_id, scm_stock_saleable.stock_type, "
            + "scm_stock_saleable.blocked_at, scm_stock_saleable.released_at, scm_stock_saleable.sales_return_type, "
            + "scm_stock_saleable.sales_invoice_item_id, scm_stock_saleable.sales_return_item_id, scm_stock_saleable.purchase_return_item_id "
            + "from scm_stock_saleable scm_stock_saleable "); //Main query
    sql.count("select count(scm_stock_saleable.id) from scm_stock_saleable scm_stock_saleable "); //Count query
    sql.join("left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_stock_saleable.product_detail_id) "
            + "left outer join scm_account scm_account on (scm_account.id = scm_stock_saleable.account_id) "
            + "left outer join scm_stock_status scm_stock_status on (scm_stock_status.id = scm_stock_saleable.stock_status_id) "
            + "left outer join scm_product_entry_detail scm_product_entry_detail on (scm_product_entry_detail.id = scm_stock_saleable.product_entry_detail_id) "
            + "left outer join scm_product_entry_detail scm_product_entry_detail_ref on (scm_product_entry_detail_ref.id = scm_stock_saleable.ref_product_entry_detail_id) "
            + "left outer join scm_sales_return_item scm_sales_return_item on (scm_sales_return_item.id = scm_stock_saleable.sales_return_item_id) "
            + "left outer join scm_sales_invoice_item scm_sales_invoice_item on (scm_sales_invoice_item.id = scm_stock_saleable.sales_invoice_item_id) "
            + "left outer join scm_purchase_return_item scm_purchase_return_item on (scm_purchase_return_item.id = scm_stock_saleable.purchase_return_item_id) "); //Join Query

    sql.string(new String[]{"scm_product_detail.batch_no", "scm_account.account_code", "scm_stock_status.title"}); //String search or sort fields
    sql.number(new String[]{"scm_stock_saleable.id", "scm_stock_saleable.quantity_in", "scm_stock_saleable.quantity_out"}); //Numeric search or sort fields    
    return sql;
  }

  /**
   * Return List of StockSaleable.
   *
   * @param main
   * @return List of StockSaleable
   */
  public static final List<StockSaleable> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStockSaleableSqlPaged(main));
  }

//  /**
//   * Return list of StockSaleable based on condition
//   * @param main
//   * @return List<StockSaleable>
//   */
//  public static final List<StockSaleable> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getStockSaleableSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select StockSaleable by key.
   *
   * @param main
   * @param stockSaleable
   * @return StockSaleable
   */
  public static final StockSaleable selectByPk(Main main, StockSaleable stockSaleable) {
    return (StockSaleable) AppService.find(main, StockSaleable.class, stockSaleable.getId());
  }

  /**
   * Insert StockSaleable.
   *
   * @param main
   * @param stockSaleable
   */
  public static final void insert(Main main, StockSaleable stockSaleable) {
    StockSaleableIs.insertAble(main, stockSaleable);  //Validating
    AppService.insert(main, stockSaleable);

  }

  /**
   * Update StockSaleable by key.
   *
   * @param main
   * @param stockSaleable
   * @return StockSaleable
   */
  public static final StockSaleable updateByPk(Main main, StockSaleable stockSaleable) {
    StockSaleableIs.updateAble(main, stockSaleable); //Validating
    return (StockSaleable) AppService.update(main, stockSaleable);
  }

  /**
   * Insert or update StockSaleable
   *
   * @param main
   * @param stockSaleable
   */
  public static void insertOrUpdate(Main main, StockSaleable stockSaleable) {
    if (stockSaleable.getId() == null) {
      insert(main, stockSaleable);
    } else {
      updateByPk(main, stockSaleable);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param stockSaleable
   */
  public static void clone(Main main, StockSaleable stockSaleable) {
    stockSaleable.setId(null); //Set to null for insert
    insert(main, stockSaleable);
  }

  /**
   * Delete StockSaleable.
   *
   * @param main
   * @param stockSaleable
   */
  public static final void deleteByPk(Main main, StockSaleable stockSaleable) {
    StockSaleableIs.deleteAble(main, stockSaleable); //Validation
    AppService.delete(main, StockSaleable.class, stockSaleable.getId());
  }

  /**
   * Delete Array of StockSaleable.
   *
   * @param main
   * @param stockSaleable
   */
  public static final void deleteByPkArray(Main main, StockSaleable[] stockSaleable) {
    for (StockSaleable e : stockSaleable) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param accId
   * @param commodityId
   * @param typeId
   * @param stockCatId
   * @param purReturnId
   * @return
   */
//  public static final List<ReturnItem> selectPurchaseReturnStockItemList(Main main, Integer accId, Integer commodityId, Integer typeId, Integer stockCatId, Integer purReturnId) {
//    /**
//     * The SQL function getProductsDetailsForPurchaseReturn(?,?,?,?) parameters 1. Account Id 2. Commodity Id 3. Product Type Id 4. Damaged(1) or Non-Moving(2) flag
//     */
//    String sql = "SELECT * FROM getProductsDetailsForPurchaseReturn(?,?,?,?)";
//    Object[] params;
//    if (purReturnId != null) {
//      sql += " where stock_id not in(select stock_saleable_id from scm_purchase_return_item where purchase_return_id=?)";
//      params = new Object[]{accId, commodityId, typeId, stockCatId, purReturnId};
//    } else {
//      params = new Object[]{accId, commodityId, typeId, stockCatId};
//    }
//
//    List<ReturnItem> list = AppDb.getList(main.dbConnector(), ReturnItem.class, sql, params);
//    return list;
//  }
  public static final List<ReturnItem> purchaseReturnStockItemList(Main main, Integer accId, Integer commodityId, Integer typeId, Integer stockCatId, Integer purReturnId) {
    String sql = "SELECT * FROM getProductsDetailsForPurchaseReturn(?,?,?,?)";
    Object[] params;
    if (purReturnId != null) {
      sql += " where stock_id in(select stock_saleable_id from scm_purchase_return_item where purchase_return_id=?)";
      params = new Object[]{accId, commodityId, typeId, stockCatId, purReturnId};
    } else {
      params = new Object[]{accId, commodityId, typeId, stockCatId};
    }

    List<ReturnItem> list = AppDb.getList(main.dbConnector(), ReturnItem.class, sql, params);
    return list;
  }

//  , (SUM(CASE WHEN scm_stock_saleable.stock_status_id='1' THEN scm_stock_saleable.quantity_in ELSE 0 END) - "
//            + "SUM(CASE WHEN scm_stock_saleable.stock_status_id='1' THEN scm_stock_saleable.quantity_out ELSE 0 END)) as quantity_in
//select product_detail_id from scm_stock_saleable scm_stock_saleable where purchase_return_item_id = 12 .....--65
  /**
   *
   * @param main
   * @param purchaseReturnItemList
   */
  public static void insertPurchaseReturnProductDetails(Main main, List<PurchaseReturnItem> purchaseReturnItemList) {
    for (PurchaseReturnItem purchaseReturnItem : purchaseReturnItemList) {
      StockSaleable stockSaleable = new StockSaleable();
      stockSaleable.setEntryDate(purchaseReturnItem.getPurchaseReturnId().getEntryDate());
      stockSaleable.setAccountId(purchaseReturnItem.getPurchaseReturnId().getAccountId());
      stockSaleable.setProductDetailId(purchaseReturnItem.getProductDetailId());
      stockSaleable.setRefProductEntryDetailId(purchaseReturnItem.getProductEntryDetailId());
      stockSaleable.setPurchaseReturnItemId(purchaseReturnItem);
      stockSaleable.setQuantityOut(purchaseReturnItem.getQuantityReturned().doubleValue());
      stockSaleable.setStockSaleableId(purchaseReturnItem.getStockSaleableId());
      if (purchaseReturnItem.getProductStockStatus() != null) {
        stockSaleable.setStockStatusId(new StockStatus(purchaseReturnItem.getProductStockStatus()));
      }
      stockSaleable.setStockType(purchaseReturnItem.getStockType());
      insert(main, stockSaleable);
    }

  }

  /**
   *
   * @param main
   * @param purchaseReturnItem
   * @param productDetailId
   */
  public static void deleteByPurchaseReturnAndProductDetail(Main main, PurchaseReturnItemReplica purchaseReturnItem, ProductDetail productDetailId) {
    for (String id : purchaseReturnItem.getPurchaseReturnItemHash().split("#")) {
      AppService.deleteSql(main, StockSaleable.class, "delete from scm_stock_saleable where purchase_return_item_id = ? and product_detail_id = ?", new Object[]{Integer.parseInt(id), productDetailId.getId()});
    }
  }

  /**
   *
   * @param main
   * @param productEntry
   */
  public static void insertProductEntryItems(Main main, ProductEntry productEntry) {
    List<ProductEntryDetail> list = AppService.list(main, ProductEntryDetail.class, "select * from scm_product_entry_detail where scm_product_entry_detail.product_entry_id = ? and is_service is null", new Object[]{productEntry.getId()});
    insertProductEntryDetail(main, productEntry, list);
  }

  public static void insertProductEntryDetail(Main main, ProductEntry productEntry, List<ProductEntryDetail> productEntryDetailList) {
    StockSaleable parentStockSaleable = null;
    boolean freeSchemeApplicable = (productEntry.getQuantityOrFree().equals(SystemConstants.FREE_SCHEME));

    for (ProductEntryDetail ped : productEntryDetailList) {
      parentStockSaleable = new StockSaleable();

      if (ped.getProductQuantity() != null && ped.getProductQuantity() >= 0) {
        parentStockSaleable = getStockSaleable(null, productEntry, ped, StockStatusService.STOCK_STATUS_SALEABLE, SALEABLE_QTY, freeSchemeApplicable);
        insert(main, parentStockSaleable);
      }

      if (ped.getProductQuantityFree() != null && ped.getProductQuantityFree() > 0 && freeSchemeApplicable) {
        insert(main, getStockSaleable(null, productEntry, ped, StockStatusService.STOCK_STATUS_SALEABLE, FREE_QTY, freeSchemeApplicable));
      }
      if (ped.getProductQuantityDamaged() != null && ped.getProductQuantityDamaged() > 0) {
        insert(main, getStockSaleable(parentStockSaleable, productEntry, ped, StockStatusService.STOCK_STATUS_DAMAGED, DAMAGED_QTY, freeSchemeApplicable));
        StockNonSaleableService.insert(main, getStockNonSaleable(productEntry, ped, StockStatusService.STOCK_STATUS_DAMAGED, DAMAGED_QTY));
      }
      if (ped.getProductQuantityShortage() != null && ped.getProductQuantityShortage() > 0) {
        insert(main, getStockSaleable(parentStockSaleable, productEntry, ped, StockStatusService.STOCK_STATUS_SHORTAGE, SHORTAGE_QTY, freeSchemeApplicable));
        // Need to do - platform entry for shortage
        PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.SHORTAGE, (ped.getProductQuantityShortage() * ped.getValueRatePerProdPieceDer()), null, productEntry, ped, PlatformService.NORMAL_FUND_STATE);
      }
      if (ped.getProductQuantityExcess() != null && ped.getProductQuantityExcess() > 0) {
        StockNonSaleableService.insert(main, getStockNonSaleable(productEntry, ped, StockStatusService.STOCK_STATUS_EXCESS, EXCESS_QTY));
        //PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.EXCESS, null, (ped.getProductQuantityExcess() * ped.getExpectedLandingRate()), productEntry, ped, PlatformService.NORMAL_FUND_STATE);
      }
    }
  }

  /**
   *
   * @param productEntryDetail
   * @param stockStatus
   * @param qtyType
   * @return
   */
  private static StockSaleable getStockSaleable(StockSaleable parentStockSaleable, ProductEntry productEntry, ProductEntryDetail productEntryDetail, Integer stockStatus, int qtyType, boolean freeSchemeApplicable) {
    StockSaleable ss = new StockSaleable();
    ss.setStockStatusId(new StockStatus(stockStatus));
    ss.setEntryDate(productEntry.getProductEntryDate());
    ss.setAccountId(productEntry.getAccountId());
    ss.setProductDetailId(productEntryDetail.getProductDetailId());
    ss.setProductEntryDetailId(productEntryDetail);
    ss.setRefProductEntryDetailId(productEntryDetail);
    ss.setStockSaleableId(parentStockSaleable);
    if (productEntryDetail.getSalesReturnItemId() != null) {
      ss.setSalesReturnItemId(productEntryDetail.getSalesReturnItemId());
    }
    if (SALEABLE_QTY == qtyType) {
      if (freeSchemeApplicable) {
        ss.setQuantityIn(productEntryDetail.getProductQuantity().doubleValue());
      } else {
        ss.setQuantityIn(productEntryDetail.getProductQuantity().doubleValue() + (productEntryDetail.getProductQuantityFree() == null ? 0 : productEntryDetail.getProductQuantityFree()));
      }
      ss.setStockType(0);
    } else if (FREE_QTY == qtyType) {
      ss.setQuantityIn(productEntryDetail.getProductQuantityFree().doubleValue());
      ss.setStockType(1);
    } else if (DAMAGED_QTY == qtyType) {
      ss.setQuantityOut(productEntryDetail.getProductQuantityDamaged().doubleValue());
      ss.setStockType(0);
    } else if (SHORTAGE_QTY == qtyType) {
      ss.setQuantityOut(productEntryDetail.getProductQuantityShortage().doubleValue());
      ss.setStockType(0);
    }
    return ss;
  }

  private static StockSaleable getSalesReturnStockSaleable(StockSaleable parentStockSaleable, SalesReturnItem salesReturnItem, Integer stockStatus, int qtyType) {
    StockSaleable ss = new StockSaleable();
    ss.setEntryDate(salesReturnItem.getSalesReturnId().getEntryDate());
    ss.setStockStatusId(new StockStatus(stockStatus));
    ss.setProductDetailId(salesReturnItem.getProductDetailId());
    ss.setAccountId(salesReturnItem.getAccountId());
    ss.setSalesReturnType(salesReturnItem.getSalesReturnId().getSalesReturnType());
    if (salesReturnItem.getSalesInvoiceItemId() != null) {
      //ss.setSalesInvoiceItemId(salesReturnItem.getSalesInvoiceItemId());
      ss.setRefProductEntryDetailId(salesReturnItem.getSalesInvoiceItemId().getProductEntryDetailId());
    } else {
      ss.setProductEntryDetailId(salesReturnItem.getProductEntryDetailId());
      ss.setRefProductEntryDetailId(salesReturnItem.getProductEntryDetailId());
    }
    ss.setSalesReturnItemId(salesReturnItem);
    ss.setStockSaleableId(parentStockSaleable);
    if (SALEABLE_QTY == qtyType) {
      if (salesReturnItem.getSalesReturnId() != null
              && salesReturnItem.getSalesReturnId().getSalesReturnType() == SystemConstants.SALES_RETURN_TYPE_DAMAGED) {
        ss.setQuantityIn(salesReturnItem.getProductQuantityDamaged());
      } else {
        ss.setQuantityIn(salesReturnItem.getProductQuantity().doubleValue());
      }
      ss.setStockType(0);
    } else if (DAMAGED_QTY == qtyType) {
      ss.setQuantityOut(salesReturnItem.getProductQuantityDamaged());
      ss.setStockType(0);
    }
    return ss;
  }

  /**
   *
   * @param productEntryDetail
   * @param stockStatus
   * @return
   */
  private static StockNonSaleable getStockNonSaleable(ProductEntry productEntry, ProductEntryDetail productEntryDetail, Integer stockStatus, int qtyType) {
    StockNonSaleable ss = new StockNonSaleable();
    ss.setEntryDate(productEntry.getProductEntryDate());
    ss.setStockStatusId(new StockStatus(stockStatus));
    ss.setAccountId(productEntry.getAccountId());
    ss.setProductDetailId(productEntryDetail.getProductDetailId());
    ss.setProductEntryDetailId(productEntryDetail);
    ss.setRefProductEntryDetailId(productEntryDetail);
    if (DAMAGED_QTY == qtyType) {
      ss.setQuantityIn(productEntryDetail.getProductQuantityDamaged().doubleValue());
      ss.setStockType(0);
    } else if (EXCESS_QTY == qtyType) {
      ss.setQuantityIn(productEntryDetail.getProductQuantityExcess().doubleValue());
      ss.setStockType(0);
    } else if (EXPIRED_QTY == qtyType) {
      //ss.setQuantityIn();
    } else if (FREE_DAMAGED_QTY == qtyType) {
      //ss.setQuantityIn();
    } else if (FREE_EXPIRED_QTY == qtyType) {
      //ss.setQuantityIn();
    }
    return ss;
  }

  private static StockNonSaleable getSalesReturnStockNonSaleable(SalesReturnItem salesReturnItem, Integer stockStatus, int qtyType) {
    StockNonSaleable ss = new StockNonSaleable();
    ss.setEntryDate(salesReturnItem.getSalesReturnId().getEntryDate());
    ss.setStockStatusId(new StockStatus(stockStatus));
    ss.setAccountId(salesReturnItem.getAccountId());
    ss.setProductDetailId(salesReturnItem.getProductDetailId());
    ss.setSalesReturnType(salesReturnItem.getSalesReturnId().getSalesReturnType());
    if (salesReturnItem.getSalesInvoiceItemId() != null) {
      ss.setSalesInvoiceItemId(salesReturnItem.getSalesInvoiceItemId());
      ss.setRefProductEntryDetailId(salesReturnItem.getSalesInvoiceItemId().getProductEntryDetailId());
    } else {
      ss.setProductEntryDetailId(salesReturnItem.getProductEntryDetailId());
      ss.setRefProductEntryDetailId(salesReturnItem.getProductEntryDetailId());
    }
    ss.setSalesReturnItemId(salesReturnItem);
    if (DAMAGED_QTY == qtyType) {
      ss.setQuantityIn(salesReturnItem.getProductQuantityDamaged());
      ss.setStockType(0);
    }
    return ss;
  }

  /**
   *
   * @param main
   * @param salesInvoice
   */
  public static final void releaseBlockedProductsFromStock(Main main, SalesInvoice salesInvoice) {
    List<StockSaleable> stockSaleableList = AppService.list(main, StockSaleable.class,
            "select * from scm_stock_saleable where stock_status_id = ? and quantity_out is null and sales_invoice_item_id in (select id from scm_sales_invoice_item where sales_invoice_id = ?) order by id asc",
            new Object[]{StockStatusService.STOCK_STATUS_BLOCKED, salesInvoice.getId()});
    releaseStockSaleableBlocked(main, stockSaleableList);
  }

  /**
   *
   * @param main
   * @param stockSaleableList
   */
  private static List<StockSaleable> releaseStockSaleableBlocked(Main main, List<StockSaleable> stockSaleableList) {
    StockSaleable stockSaleableReleased = null;
    List<StockSaleable> stockSaleableReleasedList = new ArrayList<>();
    List<StockSaleable> stockSaleableItemList = new ArrayList<>();
    for (StockSaleable stockSaleable : stockSaleableList) {
      stockSaleableReleased = getStockSaleable(stockSaleable, STOCK_MOVEMENT_SALEABLE_BLOCKED_OUT);
      insert(main, stockSaleableReleased);
      stockSaleableReleasedList.add(stockSaleableReleased);
    }

    for (StockSaleable stockSaleable : stockSaleableReleasedList) {
      stockSaleableReleased = getStockSaleable(stockSaleable, STOCK_MOVEMENT_SALEABLE_IN);
      if (stockSaleableReleased.getSalesInvoiceItemId() != null) {
        if (stockSaleableReleased.getSalesInvoiceItemId().getProductFreeQtyScheme() == null) {
          stockSaleableReleased.setStockType(SystemConstants.STOCK_TYPE_SALEABLE);
        }
      }
      insert(main, stockSaleableReleased);
      stockSaleableItemList.add(stockSaleableReleased);
    }
    return stockSaleableItemList;
  }

  /**
   *
   * @param stockSaleableItem
   * @param stockMovement
   * @return
   */
  private static StockSaleable getStockSaleable(StockSaleable stockSaleableItem, Integer stockMovement) {
    StockSaleable stockSaleable = new StockSaleable();
    stockSaleable.setAccountId(stockSaleableItem.getAccountId());
    stockSaleable.setProductDetailId(stockSaleableItem.getProductDetailId());
    if (stockMovement.equals(STOCK_MOVEMENT_SALEABLE_BLOCKED_OUT)) {
      stockSaleable.setQuantityOut(stockSaleableItem.getQuantityIn());
      stockSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_BLOCKED));
      stockSaleable.setReleasedAt(new Date());
    } else if (stockMovement.equals(STOCK_MOVEMENT_SALEABLE_IN)) {
      stockSaleable.setQuantityIn(stockSaleableItem.getQuantityOut());
      stockSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_SALEABLE));
    } else if (stockMovement.equals(STOCK_MOVEMENT_SALEABLE_BLOCKED_IN)) {
      stockSaleable.setQuantityIn(stockSaleableItem.getQuantityOut());
      stockSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_SALEABLE));
    }
    stockSaleable.setRefProductEntryDetailId(stockSaleableItem.getRefProductEntryDetailId());
    stockSaleable.setStockSaleableId(stockSaleableItem);
    stockSaleable.setStockType(stockSaleableItem.getStockType());
    stockSaleable.setSalesInvoiceItemId(stockSaleableItem.getSalesInvoiceItemId());
    return stockSaleable;
  }

  public static final List<StockSaleable> mergeSaleableBlockedToSaleable(Main main, SalesInvoice salesInvoice) {
    StockSaleable stockSaleableBlockedIn = null;
    List<StockSaleable> stockSaleableBlockedInList = new ArrayList<>();

    List<StockSaleable> stockSaleableList = AppService.list(main, StockSaleable.class,
            "select * from scm_stock_saleable where sales_invoice_item_id in (select id from scm_sales_invoice_item where sales_invoice_id = ?)", new Object[]{salesInvoice.getId()});

    for (StockSaleable stockSaleable : stockSaleableList) {
      stockSaleableBlockedIn = getStockSaleable(stockSaleable, STOCK_MOVEMENT_SALEABLE_BLOCKED_OUT);
      insert(main, stockSaleableBlockedIn);
      stockSaleableBlockedInList.add(stockSaleableBlockedIn);
    }
    stockSaleableList.clear();
    for (StockSaleable stockSaleable : stockSaleableBlockedInList) {
      stockSaleableBlockedIn = getStockSaleable(stockSaleable, STOCK_MOVEMENT_SALEABLE_BLOCKED_IN);
      insert(main, stockSaleableBlockedIn);
      stockSaleableList.add(stockSaleableBlockedIn);
    }
    return stockSaleableList;
  }

  public static final List<StockSaleable> releaseBlockedProductsFromStockBySalesInvoiceItem(Main main, List<String> salesInvoiceItemId) {
    String jpql = "select s from StockSaleable s where s.quantityIn is not null and s.stockStatusId.id = ?1 and s.salesInvoiceItemId.id in ?2 order by s.id asc";
    List<StockSaleable> stockSaleableList = main.em().listJpql(StockSaleable.class,
            jpql, new Object[]{StockStatusService.STOCK_STATUS_BLOCKED, salesInvoiceItemId});
    return releaseStockSaleableBlocked(main, stockSaleableList);
  }

  public static final void releaseBlockedItemAndSaleableOut(Main main, Integer salesInvoiceItemId, SalesInvoiceItem salesInvoiceItem) {
    StockSaleable saleable;
    String jpql = "select s from StockSaleable s where s.quantityIn is not null and s.stockStatusId.id = ?1 and s.salesInvoiceItemId.id = ?2 order by s.id asc";
    List<StockSaleable> stockSaleableList = main.em().listJpql(StockSaleable.class,
            jpql, new Object[]{StockStatusService.STOCK_STATUS_BLOCKED, salesInvoiceItemId});
    List<StockSaleable> saleableList = releaseStockSaleableBlocked(main, stockSaleableList);
    for (StockSaleable stockSaleable : saleableList) {
      saleable = cloneStockSaleable(stockSaleable);
      saleable.setSalesInvoiceItemId(salesInvoiceItem);
      saleable.setQuantityOut(stockSaleable.getQuantityIn());
      saleable.setQuantityIn(null);
      insert(main, saleable);
    }
  }

  private static StockSaleable cloneStockSaleable(StockSaleable stockSaleable) {
    StockSaleable saleable = new StockSaleable();
    saleable.setProductDetailId(stockSaleable.getProductDetailId());
    saleable.setAccountId(stockSaleable.getAccountId());
    saleable.setQuantityIn(stockSaleable.getQuantityIn());
    saleable.setQuantityOut(stockSaleable.getQuantityOut());
    saleable.setStockStatusId(stockSaleable.getStockStatusId());
    saleable.setStockSaleableId(stockSaleable.getStockSaleableId());
    saleable.setBlockedAt(stockSaleable.getBlockedAt());
    saleable.setReleasedAt(stockSaleable.getReleasedAt());
    saleable.setProductEntryDetailId(stockSaleable.getProductEntryDetailId());
    saleable.setSalesInvoiceItemId(stockSaleable.getSalesInvoiceItemId());
    saleable.setPurchaseReturnItemId(stockSaleable.getPurchaseReturnItemId());
    saleable.setSalesReturnItemId(stockSaleable.getSalesReturnItemId());
    saleable.setRefProductEntryDetailId(stockSaleable.getRefProductEntryDetailId());
    saleable.setStockType(stockSaleable.getStockType());
    return saleable;
  }

  public static void releaseBlockedFreeProductsFromStock(Main main, List<String> salesInvoiceItemId) {
    StockSaleable saleable;
    String jpql = "select s from StockSaleable s where s.quantityIn is not null and s.stockType = ?1 and s.stockStatusId.id = ?1 and s.salesInvoiceItemId.id = ?3 order by s.id asc";
    List<StockSaleable> stockSaleableList = main.em().listJpql(StockSaleable.class,
            jpql, new Object[]{SystemConstants.STOCK_TYPE_FREE, StockStatusService.STOCK_STATUS_BLOCKED, salesInvoiceItemId});
    List<StockSaleable> saleableList = releaseStockSaleableBlocked(main, stockSaleableList);
  }

  public static void insertSalesReturnItems(Main main, SalesReturn salesReturn) {
    List<SalesReturnItem> salesReturnItemList = AppService.list(main, SalesReturnItem.class,
            "select * from scm_sales_return_item where sales_return_id = ? order by id asc", new Object[]{salesReturn.getId()});
    StockSaleable stockSaleable = null;
    StockSaleable salesStockId = null;
    for (SalesReturnItem salesReturnItem : salesReturnItemList) {
      stockSaleable = null;
      if ((salesReturnItem.getProductQuantity() != null && salesReturnItem.getProductQuantity() > 0)
              || (salesReturnItem.getProductQuantityDamaged() != null && salesReturnItem.getProductQuantityDamaged() > 0)) {
        stockSaleable = getSalesReturnStockSaleable(null, salesReturnItem, StockStatusService.STOCK_STATUS_SALEABLE, SALEABLE_QTY);

        if (salesReturnItem.getSalesInvoiceItemId() != null) {
          salesStockId = (StockSaleable) AppService.single(main, StockSaleable.class,
                  "select * from scm_stock_saleable where sales_invoice_item_id = ? and quantity_out is not null and sales_return_item_id is null", new Object[]{salesReturnItem.getSalesInvoiceItemId().getId()});
          stockSaleable.setStockSaleableId(salesStockId);
        }
        insert(main, stockSaleable);
      }
      if (salesReturnItem.getProductEntryDetailId() != null) {
        main.clear();
        main.param(salesReturnItem.getProductEntryDetailId().getId());
        AppService.updateSql(main, StockSaleable.class, "UPDATE scm_stock_saleable set stock_availability = 1 where ref_product_entry_detail_id = ?", false);
      }
      if (salesReturnItem.getProductQuantityDamaged() != null && salesReturnItem.getProductQuantityDamaged() > 0) {
        stockSaleable = getSalesReturnStockSaleable(stockSaleable, salesReturnItem, StockStatusService.STOCK_STATUS_DAMAGED, DAMAGED_QTY);
        stockSaleable.setProductEntryDetailId(null);
        insert(main, stockSaleable);
        StockNonSaleableService.insert(main, getSalesReturnStockNonSaleable(salesReturnItem, StockStatusService.STOCK_STATUS_DAMAGED, DAMAGED_QTY));
      }
    }

  }

  public static void adjustSaleableStock(Main main, StockAdjustmentItem stockAdjustmentItem) {
    StockSaleable stockSaleable = new StockSaleable();
    stockSaleable.setAccountId(stockAdjustmentItem.getProductDetailId().getAccountId());
    stockSaleable.setEntryDate(stockAdjustmentItem.getStockAdjustmentId().getEntryDate());
    stockSaleable.setProductDetailId(stockAdjustmentItem.getProductDetailId());
    stockSaleable.setRefProductEntryDetailId(stockAdjustmentItem.getProductEntryDetailId());
    if (stockAdjustmentItem.getStockId() == null) {
      stockSaleable.setStockSaleableId(selectStockSaleableByProductEntryDetail(main, stockAdjustmentItem.getProductDetailId(), stockAdjustmentItem.getProductEntryDetailId()));
    } else {
      stockSaleable.setStockSaleableId(new StockSaleable(stockAdjustmentItem.getStockId()));
    }
    stockSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_SALEABLE));
    stockSaleable.setStockType(STOCK_TYPE_PRODUCT_QTY);
    stockSaleable.setStockAdjustmentItemId(stockAdjustmentItem);
    if (stockAdjustmentItem.getAdjustedQty() > 0) {
      stockSaleable.setQuantityIn(stockAdjustmentItem.getAdjustedQty().doubleValue());
    } else if (stockAdjustmentItem.getAdjustedQty() < 0) {
      stockSaleable.setQuantityOut(stockAdjustmentItem.getAdjustedQty().doubleValue() * -1);
    }
    insert(main, stockSaleable);
  }

  public static StockSaleable
          selectStockSaleableByProductEntryDetail(Main main, ProductDetail productDeatil, ProductEntryDetail productEntryDetail) {
    return (StockSaleable) AppService.single(main, StockSaleable.class,
            "select * from scm_stock_saleable where product_detail_id = ? and product_entry_detail_id = ? and quantity_in is not null",
            new Object[]{productDeatil.getId(), productEntryDetail.getId()});
  }

  public static void stockMovementSaleableToDamaged(Main main, StockMovementItem stockMovementItem) {
    StockSaleable stockSaleable = new StockSaleable();
    stockSaleable.setAccountId(stockMovementItem.getProductDetailId().getAccountId());
    stockSaleable.setEntryDate(stockMovementItem.getStockMovementId().getEntryDate());
    stockSaleable.setProductDetailId(stockMovementItem.getProductDetailId());
    stockSaleable.setRefProductEntryDetailId(stockMovementItem.getProductEntryDetailId());
    stockSaleable.setQuantityOut(stockMovementItem.getQuantityDamagedActual().doubleValue());
    stockSaleable.setStockType(STOCK_TYPE_PRODUCT_QTY);
    stockSaleable.setStockSaleableId(new StockSaleable(stockMovementItem.getStockId()));
    stockSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_DAMAGED));
    stockSaleable.setStockMovementItemId(stockMovementItem);
    insert(main, stockSaleable);

    StockNonSaleable stockNonSaleable = new StockNonSaleable();
    stockNonSaleable.setAccountId(stockSaleable.getAccountId());
    stockNonSaleable.setEntryDate(stockSaleable.getEntryDate());
    stockNonSaleable.setProductDetailId(stockSaleable.getProductDetailId());
    stockNonSaleable.setQuantityIn(stockSaleable.getQuantityOut());
    stockNonSaleable.setRefProductEntryDetailId(stockSaleable.getRefProductEntryDetailId());
    stockNonSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_DAMAGED));
    stockNonSaleable.setStockType(STOCK_TYPE_PRODUCT_QTY);
    stockNonSaleable.setStockMovementItemId(stockMovementItem);
    StockNonSaleableService.insert(main, stockNonSaleable);
  }

//  public static void salesInvoiceCancelled(Main main, SalesInvoice salesInvoice) {
//    StockSaleable stockSaleable = null;
//    List<StockSaleable> stockSaleableList = AppService.list(main, StockSaleable.class,
//            "select * from scm_stock_saleable where sales_invoice_item_id in (select id from scm_sales_invoice_item where sales_invoice_id = ?) order by id asc",
//            new Object[]{salesInvoice.getId()});
//    for (StockSaleable stock : stockSaleableList) {
//      stockSaleable = new StockSaleable(stock);
//      stockSaleable.setQuantityIn(stock.getQuantityOut());
//      stockSaleable.setStockSaleableId(stock);
//      insert(main, stockSaleable);
//    }
//  }
  public static void stockMovementSaleableToExpired(Main main, StockMovementItem stockMovementItem) {
    StockSaleable stockSaleable = new StockSaleable();
    stockSaleable.setAccountId(stockMovementItem.getProductDetailId().getAccountId());
    stockSaleable.setEntryDate(stockMovementItem.getStockMovementId().getEntryDate());
    stockSaleable.setProductDetailId(stockMovementItem.getProductDetailId());
    stockSaleable.setRefProductEntryDetailId(stockMovementItem.getProductEntryDetailId());
    stockSaleable.setQuantityOut(stockMovementItem.getQuantityExpired().doubleValue());
    stockSaleable.setStockType(STOCK_TYPE_PRODUCT_QTY);
    stockSaleable.setStockSaleableId(new StockSaleable(stockMovementItem.getStockId()));
    stockSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_EXPIRED));
    stockSaleable.setStockMovementItemId(stockMovementItem);
    insert(main, stockSaleable);

    StockNonSaleable stockNonSaleable = new StockNonSaleable();
    stockNonSaleable.setAccountId(stockSaleable.getAccountId());
    stockNonSaleable.setEntryDate(stockSaleable.getEntryDate());
    stockNonSaleable.setProductDetailId(stockSaleable.getProductDetailId());
    stockNonSaleable.setQuantityIn(stockSaleable.getQuantityOut());
    stockNonSaleable.setRefProductEntryDetailId(stockSaleable.getRefProductEntryDetailId());
    stockNonSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_EXPIRED));
    stockNonSaleable.setStockType(STOCK_TYPE_PRODUCT_QTY);
    stockNonSaleable.setStockMovementItemId(stockMovementItem);
    StockNonSaleableService.insert(main, stockNonSaleable);
  }
}
