/*
 * @(#)SalesReqItemService.java	1.0 Fri Feb 03 19:15:39 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.SalesReq;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesReqItem;
import spica.scm.validate.SalesReqItemIs;

/**
 * SalesReqItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 03 19:15:39 IST 2017
 */
public abstract class SalesReqItemService {

  /**
   * SalesReqItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesReqItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_req_item", SalesReqItem.class, main);
    sql.main("select scm_sales_req_item.id,scm_sales_req_item.sales_req_id,scm_sales_req_item.product_id, scm_sales_req_item.qty_required, scm_sales_req_item.qty_free_suggested,"
            + "scm_sales_req_item.free_qty_unit_suggested, scm_sales_req_item.free_qty_unit_qty_suggested,scm_sales_req_item.free_qty_unit_qty_free_suggested,"
            + "scm_sales_req_item.value_prod_piece_selling,scm_sales_req_item.value_prod_piece_selling_forced,scm_sales_req_item.discount_value_fixed,scm_sales_req_item.discount_value_percentage, scm_sales_req_item.tax_code_id  "
            + "from scm_sales_req_item scm_sales_req_item "); //Main query
    sql.count("select count(scm_sales_req_item.id) as total from scm_sales_req_item scm_sales_req_item "); //Count query
    sql.join("left outer join scm_sales_req scm_sales_req_itemsales_req_id on (scm_sales_req_itemsales_req_id.id = scm_sales_req_item.sales_req_id) "
            + "left outer join scm_tax_code scm_tax_code on (scm_tax_code.id = scm_sales_req_item.tax_code_id) "
            + "left outer join scm_product scm_sales_req_itemproduct_id on (scm_sales_req_itemproduct_id.id = scm_sales_req_item.product_id)"); //Join Query

    sql.string(new String[]{"scm_sales_req_itemsales_req_id.requisition_no", "scm_sales_req_itemproduct_id.product_name"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_req_item.id", "scm_sales_req_item.qty_required", "scm_sales_req_item.qty_free_suggested", "scm_sales_req_item.free_qty_unit_suggested",
      "scm_sales_req_item.free_qty_unit_qty_suggested", "scm_sales_req_item.free_qty_unit_qty_free_suggested", "scm_sales_req_item.value_prod_piece_selling",
      "scm_sales_req_item.discount_value_fixed", "scm_sales_req_item.discount_value_percentage"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesReqItem.
   *
   * @param main
   * @return List of SalesReqItem
   */
  public static final List<SalesReqItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesReqItemSqlPaged(main));
  }

//  /**
//   * Return list of SalesReqItem based on condition
//   * @param main
//   * @return List<SalesReqItem>
//   */
//  public static final List<SalesReqItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesReqItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesReqItem by key.
   *
   * @param main
   * @param salesReqItem
   * @return SalesReqItem
   */
  public static final SalesReqItem selectByPk(Main main, SalesReqItem salesReqItem) {
    return (SalesReqItem) AppService.find(main, SalesReqItem.class, salesReqItem.getId());
  }

  /**
   * Insert SalesReqItem.
   *
   * @param main
   * @param salesReqItem
   */
  public static final void insert(Main main, SalesReqItem salesReqItem) {
    SalesReqItemIs.insertAble(main, salesReqItem);  //Validating
    AppService.insert(main, salesReqItem);

  }

  /**
   * Update SalesReqItem by key.
   *
   * @param main
   * @param salesReqItem
   * @return SalesReqItem
   */
  public static final SalesReqItem updateByPk(Main main, SalesReqItem salesReqItem) {
    SalesReqItemIs.updateAble(main, salesReqItem); //Validating
    return (SalesReqItem) AppService.update(main, salesReqItem);
  }

  /**
   * Insert or update SalesReqItem
   *
   * @param main
   * @param salesReqItem
   */
  public static void insertOrUpdate(Main main, SalesReqItem salesReqItem) {
    if (salesReqItem.getId() == null) {
      insert(main, salesReqItem);
    } else {
      updateByPk(main, salesReqItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesReqItem
   */
  public static void clone(Main main, SalesReqItem salesReqItem) {
    salesReqItem.setId(null); //Set to null for insert
    insert(main, salesReqItem);
  }

  /**
   * Delete SalesReqItem.
   *
   * @param main
   * @param salesReqItem
   */
  public static final void deleteByPk(Main main, SalesReqItem salesReqItem) {
    SalesReqItemIs.deleteAble(main, salesReqItem); //Validation
    AppService.delete(main, SalesReqItem.class, salesReqItem.getId());
  }

  /**
   * Delete Array of SalesReqItem.
   *
   * @param main
   * @param salesReqItem
   */
  public static final void deleteByPkArray(Main main, SalesReqItem[] salesReqItem) {
    for (SalesReqItem e : salesReqItem) {
      deleteByPk(main, e);
    }
  }

  /**
   * Return List of Sales Req Details.
   *
   * @param main
   * @param salesReq
   * @return
   */
  public static List<SalesReqItem> selectSalesReqItemBySalesReq(Main main, SalesReq salesReq) {
    SqlPage sql = getSalesReqItemSqlPaged(main);
    sql.cond("where scm_sales_req_item.sales_req_id = ?");
    sql.param(salesReq.getId());
    return AppService.listAllJpa(main, sql);
  }

  /**
   *
   * @param main
   * @param salesReq
   * @param salesReqItemList
   */
  public static void insertOrUpdateSalesReqItem(Main main, SalesReq salesReq, List<SalesReqItem> salesReqItemList) {
    AppService.deleteSql(main, SalesReqItem.class, "delete from scm_sales_req_item where sales_req_id = ?", new Object[]{salesReq.getId()});
    if (salesReqItemList != null) {
      for (SalesReqItem salesReqItem : salesReqItemList) {
        if (salesReqItem.getProductId() != null && salesReqItem.getQtyRequired() != null) {
          salesReqItem.setId(null);
          salesReqItem.setSalesReqId(salesReq);
          if (salesReq.getSchemeRequired() != null && salesReq.getSchemeRequired().equals(SalesReqService.SALES_REQ_SCHEME_NOT_REQUIRED)) {
            salesReqItem.setDiscountValueFixed(null);
            salesReqItem.setDiscountValuePercentage(null);
            salesReqItem.setQtyFreeSuggested(null);
            salesReqItem.setValueProdPieceSellingForced(null);
          }
          insert(main, salesReqItem);
        }

      }
    }
  }
}
