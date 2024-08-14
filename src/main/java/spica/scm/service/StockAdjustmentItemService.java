/*
 * @(#)StockAdjustmentItemService.java	1.0 Wed Mar 21 14:37:41 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.common.StockAdjustmentDetail;
import spica.scm.domain.StockAdjustment;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.StockAdjustmentItem;
import spica.scm.validate.StockAdjustmentItemIs;
import spica.sys.SystemConstants;

/**
 * StockAdjustmentItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 21 14:37:41 IST 2018
 */
public abstract class StockAdjustmentItemService {

  /**
   * StockAdjustmentItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStockAdjustmentItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_stock_adjustment_item", StockAdjustmentItem.class, main);
    sql.main("select scm_stock_adjustment_item.id,scm_stock_adjustment_item.stock_adjustment_id,scm_stock_adjustment_item.product_id,scm_stock_adjustment_item.product_detail_id,"
            + "scm_stock_adjustment_item.product_entry_id,scm_stock_adjustment_item.product_entry_detail_id,scm_stock_adjustment_item.quantity_purchased,"
            + "scm_stock_adjustment_item.quantity_available,scm_stock_adjustment_item.quantity_saleable_actual,scm_stock_adjustment_item.quantity_damaged, "
            + "scm_stock_adjustment_item.quantity_damaged_actual from scm_stock_adjustment_item scm_stock_adjustment_item "); //Main query
    sql.count("select count(scm_stock_adjustment_item.id) as total from scm_stock_adjustment_item scm_stock_adjustment_item "); //Count query
    sql.join("left outer join scm_stock_adjustment scm_stock_adjustment_itemstock_adjustment_id on (scm_stock_adjustment_itemstock_adjustment_id.id = scm_stock_adjustment_item.stock_adjustment_id) left outer join scm_product scm_stock_adjustment_itemproduct_id on (scm_stock_adjustment_itemproduct_id.id = scm_stock_adjustment_item.product_id) left outer join scm_product_detail scm_stock_adjustment_itemproduct_detail_id on (scm_stock_adjustment_itemproduct_detail_id.id = scm_stock_adjustment_item.product_detail_id) left outer join scm_product_entry scm_stock_adjustment_itemproduct_entry_id on (scm_stock_adjustment_itemproduct_entry_id.id = scm_stock_adjustment_item.product_entry_id) left outer join scm_product_entry_detail scm_stock_adjustment_itemproduct_entry_detail_id on (scm_stock_adjustment_itemproduct_entry_detail_id.id = scm_stock_adjustment_item.product_entry_detail_id)"); //Join Query

    sql.string(new String[]{"scm_stock_adjustment_itemstock_adjustment_id.invoice_no", "scm_stock_adjustment_itemproduct_id.product_name", "scm_stock_adjustment_itemproduct_detail_id.batch_no", "scm_stock_adjustment_itemproduct_entry_id.invoice_no"}); //String search or sort fields
    sql.number(new String[]{"scm_stock_adjustment_item.id", "scm_stock_adjustment_itemproduct_entry_detail_id.product_quantity", "scm_stock_adjustment_item.quantity_purchased", "scm_stock_adjustment_item.quantity_available", "scm_stock_adjustment_item.quantity_saleable_actual"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of StockAdjustmentItem.
   *
   * @param main
   * @return List of StockAdjustmentItem
   */
  public static final List<StockAdjustmentItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStockAdjustmentItemSqlPaged(main));
  }

//  /**
//   * Return list of StockAdjustmentItem based on condition
//   * @param main
//   * @return List<StockAdjustmentItem>
//   */
//  public static final List<StockAdjustmentItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getStockAdjustmentItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select StockAdjustmentItem by key.
   *
   * @param main
   * @param stockAdjustmentItem
   * @return StockAdjustmentItem
   */
  public static final StockAdjustmentItem selectByPk(Main main, StockAdjustmentItem stockAdjustmentItem) {
    return (StockAdjustmentItem) AppService.find(main, StockAdjustmentItem.class, stockAdjustmentItem.getId());
  }

  /**
   * Insert StockAdjustmentItem.
   *
   * @param main
   * @param stockAdjustmentItem
   */
  public static final void insert(Main main, StockAdjustmentItem stockAdjustmentItem) {
    StockAdjustmentItemIs.insertAble(main, stockAdjustmentItem);  //Validating
    AppService.insert(main, stockAdjustmentItem);

  }

  /**
   * Update StockAdjustmentItem by key.
   *
   * @param main
   * @param stockAdjustmentItem
   * @return StockAdjustmentItem
   */
  public static final StockAdjustmentItem updateByPk(Main main, StockAdjustmentItem stockAdjustmentItem) {
    StockAdjustmentItemIs.updateAble(main, stockAdjustmentItem); //Validating
    return (StockAdjustmentItem) AppService.update(main, stockAdjustmentItem);
  }

  /**
   * Insert or update StockAdjustmentItem
   *
   * @param main
   * @param stockAdjustmentItem
   */
  public static void insertOrUpdate(Main main, StockAdjustmentItem stockAdjustmentItem) {
    if (stockAdjustmentItem.getId() == null) {
      insert(main, stockAdjustmentItem);
    } else {
      updateByPk(main, stockAdjustmentItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param stockAdjustmentItem
   */
  public static void clone(Main main, StockAdjustmentItem stockAdjustmentItem) {
    stockAdjustmentItem.setId(null); //Set to null for insert
    insert(main, stockAdjustmentItem);
  }

  /**
   * Delete StockAdjustmentItem.
   *
   * @param main
   * @param stockAdjustmentItem
   */
  public static final void deleteByPk(Main main, StockAdjustmentItem stockAdjustmentItem) {
    StockAdjustmentItemIs.deleteAble(main, stockAdjustmentItem); //Validation
    AppService.delete(main, StockAdjustmentItem.class, stockAdjustmentItem.getId());
  }

  /**
   * Delete Array of StockAdjustmentItem.
   *
   * @param main
   * @param stockAdjustmentItem
   */
  public static final void deleteByPkArray(Main main, StockAdjustmentItem[] stockAdjustmentItem) {
    for (StockAdjustmentItem e : stockAdjustmentItem) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param stockAdjustment
   * @return
   */
  public static List<StockAdjustmentItem> selectStockAdjustmentItemByStockAdjustment(Main main, StockAdjustment stockAdjustment) {
    List<StockAdjustmentItem> list = null;
    String sql = "";
    if (stockAdjustment.getStockAdjustmentType() == SystemConstants.STOCK_ADJUSTMENT_TYPE_SALEABLE) {
      sql = "select row_number() OVER () as id,stock_adjustment_id, product_id,product_detail_id,SUM(quantity_purchased) as quantity_purchased,\n"
              + "SUM(quantity_available) as quantity_available,SUM(quantity_saleable_actual) quantity_saleable_actual,SUM(quantity_damaged) quantity_damaged,\n"
              + "SUM(quantity_damaged_actual) quantity_damaged_actual,SUM(quantity_excess) quantity_excess,SUM(quantity_excess_actual) quantity_excess_actual,"
              + "(SUM(land_rate)/COUNT(land_rate)) as land_rate,account_id \n"
              + "from scm_stock_adjustment_item \n"
              + "where stock_adjustment_id = ? \n"
              + "group by stock_adjustment_id, product_id,product_detail_id,account_id ";
    } else {
      sql = "select * from scm_stock_adjustment_item where stock_adjustment_id=?";
    }
    list = AppService.list(main, StockAdjustmentItem.class, sql, new Object[]{stockAdjustment.getId()});

    updateCurrentStockQuantity(list);

    return list;
  }

  /**
   *
   * @param main
   * @param stockAdjustmentItemList
   */
  private static void updateCurrentStockQuantity(List<StockAdjustmentItem> stockAdjustmentItemList) {
    for (StockAdjustmentItem item : stockAdjustmentItemList) {
      item.setStockAdjustmentDetail(new StockAdjustmentDetail());
      if (item.getAccountId() != null) {
        item.getStockAdjustmentDetail().setAccountId(item.getAccountId().getId());
      } else {
        item.getStockAdjustmentDetail().setAccountId(item.getProductDetailId().getAccountId().getId());
      }
      item.getStockAdjustmentDetail().setBatchNo(item.getProductDetailId().getProductBatchId().getBatchNo());
      item.getStockAdjustmentDetail().setMrpValue(item.getProductDetailId().getProductBatchId().getValueMrp());
      item.getStockAdjustmentDetail().setExpiryDateActual(item.getProductDetailId().getProductBatchId().getExpiryDateActual());
      if (item.getProductEntryDetailId() != null) {
        item.getStockAdjustmentDetail().setPtsValue(item.getProductEntryDetailId().getValuePts());
        item.getStockAdjustmentDetail().setPackSize(item.getProductEntryDetailId().getPackSize());
      } else {
        item.getStockAdjustmentDetail().setPackSize(item.getProductId().getPackSize() + " " + item.getProductId().getProductUnitId().getSymbol());
      }
      item.getStockAdjustmentDetail().setProductBatchId(item.getProductDetailId().getProductBatchId().getId());
    }
  }

  public static final void deleteByProductDetail(Main main, StockAdjustmentItem stockAdjustmentItem) {
    StockAdjustmentItemIs.deleteAble(main, stockAdjustmentItem); //Validation
    AppService.deleteSql(main, StockAdjustmentItem.class, "delete from scm_stock_adjustment_item where stock_adjustment_id = ? and product_detail_id = ?",
            new Object[]{stockAdjustmentItem.getStockAdjustmentId().getId(), stockAdjustmentItem.getProductDetailId().getId()});
  }

  public static final void deleteByProductDetailOriginal(Main main, StockAdjustmentItem stockAdjustmentItem) {
    AppService.deleteSql(main, StockAdjustmentItem.class, "delete from scm_stock_adjustment_item where stock_adjustment_id = ? and product_detail_id = ?",
            new Object[]{stockAdjustmentItem.getStockAdjustmentId().getId(), stockAdjustmentItem.getProductDetailOriginal().getId()});
  }
}
