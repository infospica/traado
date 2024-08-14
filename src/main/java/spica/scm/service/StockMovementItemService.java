/*
 * @(#)StockMovementItemService.java	1.0 Thu Mar 29 12:11:05 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.common.StockAdjustmentDetail;
import spica.scm.domain.StockMovement;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.StockMovementItem;
import spica.scm.domain.StockNonSaleable;
import spica.scm.domain.StockSaleable;
import spica.scm.validate.StockMovementItemIs;
import spica.sys.SystemConstants;

/**
 * StockMovementItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Mar 29 12:11:05 IST 2018
 */
public abstract class StockMovementItemService {

  /**
   * StockMovementItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStockMovementItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_stock_movement_item", StockMovementItem.class, main);
    sql.main("select scm_stock_movement_item.id,scm_stock_movement_item.stock_movement_id,scm_stock_movement_item.product_id,scm_stock_movement_item.product_detail_id,scm_stock_movement_item.product_entry_detail_id,scm_stock_movement_item.quantity_purchased,scm_stock_movement_item.quantity_available,scm_stock_movement_item.quantity_damaged,scm_stock_movement_item.quantity_saleable_actual,scm_stock_movement_item.quantity_damaged_actual from scm_stock_movement_item scm_stock_movement_item "); //Main query
    sql.count("select count(scm_stock_movement_item.id) as total from scm_stock_movement_item scm_stock_movement_item "); //Count query
    sql.join("left outer join scm_stock_movement scm_stock_movement_itemstock_movement_id on (scm_stock_movement_itemstock_movement_id.id = scm_stock_movement_item.stock_movement_id) left outer join scm_product scm_stock_movement_itemproduct_id on (scm_stock_movement_itemproduct_id.id = scm_stock_movement_item.product_id) left outer join scm_product_detail scm_stock_movement_itemproduct_detail_id on (scm_stock_movement_itemproduct_detail_id.id = scm_stock_movement_item.product_detail_id) left outer join scm_product_entry_detail scm_stock_movement_itemproduct_entry_detail_id on (scm_stock_movement_itemproduct_entry_detail_id.id = scm_stock_movement_item.product_entry_detail_id)"); //Join Query

    sql.string(new String[]{"scm_stock_movement_itemstock_movement_id.reference_no", "scm_stock_movement_itemproduct_id.product_name", "scm_stock_movement_itemproduct_detail_id.batch_no"}); //String search or sort fields
    sql.number(new String[]{"scm_stock_movement_item.id", "scm_stock_movement_itemproduct_entry_detail_id.product_quantity", "scm_stock_movement_item.quantity_purchased", "scm_stock_movement_item.quantity_available", "scm_stock_movement_item.quantity_damaged", "scm_stock_movement_item.quantity_saleable_actual", "scm_stock_movement_item.quantity_damaged_actual"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of StockMovementItem.
   *
   * @param main
   * @return List of StockMovementItem
   */
  public static final List<StockMovementItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStockMovementItemSqlPaged(main));
  }

//  /**
//   * Return list of StockMovementItem based on condition
//   * @param main
//   * @return List<StockMovementItem>
//   */
//  public static final List<StockMovementItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getStockMovementItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select StockMovementItem by key.
   *
   * @param main
   * @param stockMovementItem
   * @return StockMovementItem
   */
  public static final StockMovementItem selectByPk(Main main, StockMovementItem stockMovementItem) {
    return (StockMovementItem) AppService.find(main, StockMovementItem.class, stockMovementItem.getId());
  }

  /**
   * Insert StockMovementItem.
   *
   * @param main
   * @param stockMovementItem
   */
  public static final void insert(Main main, StockMovementItem stockMovementItem) {
    StockMovementItemIs.insertAble(main, stockMovementItem);  //Validating
    AppService.insert(main, stockMovementItem);

  }

  /**
   * Update StockMovementItem by key.
   *
   * @param main
   * @param stockMovementItem
   * @return StockMovementItem
   */
  public static final StockMovementItem updateByPk(Main main, StockMovementItem stockMovementItem) {
    StockMovementItemIs.updateAble(main, stockMovementItem); //Validating
    return (StockMovementItem) AppService.update(main, stockMovementItem);
  }

  /**
   * Insert or update StockMovementItem
   *
   * @param main
   * @param stockMovementItem
   */
  public static void insertOrUpdate(Main main, StockMovementItem stockMovementItem) {
    if (stockMovementItem.getId() == null) {
      insert(main, stockMovementItem);
    } else {
      updateByPk(main, stockMovementItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param stockMovementItem
   */
  public static void clone(Main main, StockMovementItem stockMovementItem) {
    stockMovementItem.setId(null); //Set to null for insert
    insert(main, stockMovementItem);
  }

  /**
   * Delete StockMovementItem.
   *
   * @param main
   * @param stockMovementItem
   */
  public static final void deleteByPk(Main main, StockMovementItem stockMovementItem) {
    StockMovementItemIs.deleteAble(main, stockMovementItem); //Validation
    AppService.deleteSql(main, StockSaleable.class, "delete from scm_stock_saleable where stock_movement_item_id=?", new Object[]{stockMovementItem.getId()});
    AppService.deleteSql(main, StockNonSaleable.class, "delete from scm_stock_non_saleable where stock_movement_item_id=?", new Object[]{stockMovementItem.getId()});
    AppService.delete(main, StockMovementItem.class, stockMovementItem.getId());
  }

  /**
   * Delete Array of StockMovementItem.
   *
   * @param main
   * @param stockMovementItem
   */
  public static final void deleteByPkArray(Main main, StockMovementItem[] stockMovementItem) {
    for (StockMovementItem e : stockMovementItem) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param stockMovement
   * @return
   */
  public static final List<StockMovementItem> selectStockMovementItemByStockMovement(Main main, StockMovement stockMovement) {
    List<StockMovementItem> list;
    StockAdjustmentDetail stockAdjustmentDetail;
    if (SystemConstants.CONFIRMED.equals(stockMovement.getStatusId())) {
      list = AppService.list(main, StockMovementItem.class, "select ROW_NUMBER() OVER (ORDER BY stock_movement_id ) as id, stock_movement_id,product_detail_id,"
              + "product_id,quantity_purchased,quantity_available,sum(quantity_saleable_actual) quantity_saleable_actual, "
              + "quantity_damaged,sum(quantity_damaged_actual) quantity_damaged_actual "
              + "from scm_stock_movement_item where stock_movement_id = ? "
              + "GROUP BY stock_movement_id,product_id,product_detail_id,quantity_purchased,quantity_available,quantity_damaged", new Object[]{stockMovement.getId()});
    } else {
      list = AppService.list(main, StockMovementItem.class, "select * from scm_stock_movement_item where stock_movement_id = ?", new Object[]{stockMovement.getId()});
      for (StockMovementItem item : list) {
        stockAdjustmentDetail = StockMovementService.selectStockAdjustmentDetailByProductDetail(main, stockMovement, item.getProductDetailId());
        if (stockAdjustmentDetail != null) {
          item.setQuantityPurchased(stockAdjustmentDetail.getQuantityIn());
          item.setQuantityAvailable(stockAdjustmentDetail.getQuantitySaleable());
          item.setQuantityDamaged(stockAdjustmentDetail.getQuantityDamaged());
        }
      }
    }
    return list;
  }

  public static List<StockMovementItem> getStockMovementItemForExpiry(Main main, StockMovement stockMovement) {
    return AppService.list(main, StockMovementItem.class, "select * from scm_stock_movement_item where stock_movement_id=?", new Object[]{stockMovement.getId()});
  }

//    public static Account getAccountById(Main main, Integer accountId) {
//    Account account = (Account) AppService.single(main, Account.class, ScmLookupSql.ACCOUNT_BYID, new Object[]{accountId});
//    return account;
//  }
}
