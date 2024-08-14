/*
 * @(#)PurchaseOrderDetailService.java	1.0 Wed Apr 13 15:41:17 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.PurchaseOrder;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.PurchaseOrderDetail;

/**
 * PurchaseOrderDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:17 IST 2016
 */
public abstract class PurchaseOrderDetailService {

  private static final int sortOrder = 1;

  /**
   * PurchaseOrderDetail paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseOrderDetailSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_order_detail", PurchaseOrderDetail.class, main);
    sql.main("select scm_purchase_order_detail.id,scm_purchase_order_detail.purchase_order_id,scm_purchase_order_detail.product_id,scm_purchase_order_detail.product_detail_id,scm_purchase_order_detail.quantity_required,scm_purchase_order_detail.product_name,scm_purchase_order_detail.order_price,scm_purchase_order_detail.prev_purchase_price from scm_purchase_order_detail scm_purchase_order_detail "); //Main query
    sql.count("select count(scm_purchase_order_detail.id) from scm_purchase_order_detail scm_purchase_order_detail "); //Count query
    sql.join("left outer join scm_purchase_order scm_purchase_order_detailpurchase_order_id on (scm_purchase_order_detailpurchase_order_id.id = scm_purchase_order_detail.purchase_order_id) left outer join scm_product scm_purchase_order_detailproduct_id on (scm_purchase_order_detailproduct_id.id = scm_purchase_order_detail.product_id) left outer join scm_product_detail scm_purchase_order_detailproduct_detail_id on (scm_purchase_order_detailproduct_detail_id.id = scm_purchase_order_detail.product_detail_id)"); //Join Query

    sql.string(new String[]{"scm_purchase_order_detailpurchase_order_id.purchase_order_no", "scm_purchase_order_detailproduct_id.product_name", "scm_purchase_order_detailproduct_detail_id.batch_no", "scm_purchase_order_detail.product_name"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_order_detail.id", "scm_purchase_order_detail.quantity_required", "scm_purchase_order_detail.order_price", "scm_purchase_order_detail.prev_purchase_price"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseOrderDetail.
   *
   * @param main
   * @return List of PurchaseOrderDetail
   */
  public static final List<PurchaseOrderDetail> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseOrderDetailSqlPaged(main));
  }

  /**
   * Return List of Purchase Order details.
   *
   * @param main
   * @param purchaseOrder
   * @return List of Purchase Order details
   */
  public static final List<PurchaseOrderDetail> selectPurchaseOrderDetailById(Main main, PurchaseOrder purchaseOrder) {
    main.param(0);
    main.param(purchaseOrder.getId());
    AppService.updateSql(main, PurchaseOrderDetail.class, "update scm_purchase_order_detail set sort_order=? where purchase_order_id=?", false);
    main.clear();

    SqlPage sql = getPurchaseOrderDetailSqlPaged(main);
    sql.cond("where scm_purchase_order_detail.purchase_order_id=?");
    sql.param(purchaseOrder.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of PurchaseOrderDetail based on condition
//   * @param main
//   * @return List<PurchaseOrderDetail>
//   */
//  public static final List<PurchaseOrderDetail> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseOrderDetailSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select PurchaseOrderDetail by key.
   *
   * @param main
   * @param purchaseOrderDetail
   * @return PurchaseOrderDetail
   */
  public static final PurchaseOrderDetail selectByPk(Main main, PurchaseOrderDetail purchaseOrderDetail) {
    return (PurchaseOrderDetail) AppService.find(main, PurchaseOrderDetail.class, purchaseOrderDetail.getId());
  }

  /**
   * Insert PurchaseOrderDetail.
   *
   * @param main
   * @param purchaseOrderDetail
   */
  public static final void insert(Main main, PurchaseOrderDetail purchaseOrderDetail) {
    AppService.insert(main, purchaseOrderDetail);
  }

  /**
   * Update PurchaseOrderDetail by key.
   *
   * @param main
   * @param purchaseOrderDetail
   * @return PurchaseOrderDetail
   */
  public static final PurchaseOrderDetail updateByPk(Main main, PurchaseOrderDetail purchaseOrderDetail) {
    return (PurchaseOrderDetail) AppService.update(main, purchaseOrderDetail);
  }

  /**
   * Insert or update PurchaseOrderDetail
   *
   * @param main
   * @param PurchaseOrderDetail
   */
  public static void insertOrUpdate(Main main, PurchaseOrderDetail purchaseOrderDetail) {
    if (purchaseOrderDetail.getId() == null) {
      insert(main, purchaseOrderDetail);
    } else {
      updateByPk(main, purchaseOrderDetail);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseOrderDetail
   */
  public static void clone(Main main, PurchaseOrderDetail purchaseOrderDetail) {
    purchaseOrderDetail.setId(null); //Set to null for insert
    insert(main, purchaseOrderDetail);
  }

  /**
   * Delete PurchaseOrderDetail.
   *
   * @param main
   * @param purchaseOrderDetail
   */
  public static final void deleteByPk(Main main, PurchaseOrderDetail purchaseOrderDetail) {
    AppService.delete(main, PurchaseOrderDetail.class, purchaseOrderDetail.getId());
  }

  /**
   * Delete Array of PurchaseOrderDetail.
   *
   * @param main
   * @param purchaseOrderDetail
   */
  public static final void deleteByPkArray(Main main, PurchaseOrderDetail[] purchaseOrderDetail) {
    for (PurchaseOrderDetail e : purchaseOrderDetail) {
      deleteByPk(main, e);
    }
  }

  /**
   * Update trash by key.
   *
   * @param main
   * @param purchaseOrderDetailId
   */
  public static final void updateTrashByPk(Main main, String purchaseOrderDetailId) {
    main.param(1);
    main.param(Integer.parseInt(purchaseOrderDetailId));
    AppService.updateSql(main, PurchaseOrderDetail.class, "update scm_purchase_order_detail set sort_order=? where id=?", false);
  }

  /**
   * Insert or update
   *
   * @param main
   * @param purchaseOrderDetailList
   * @param orderId
   * @param orderStatusId
   *
   */
  public static void insertOrUpdateOrderDetails(Main main, List<PurchaseOrderDetail> purchaseOrderDetailList, String orderId, String orderStatusId) {
    if (purchaseOrderDetailList != null) {
      for (PurchaseOrderDetail req : purchaseOrderDetailList) {
        insertOrUpdate(main, req);

      }
      AppService.deleteSql(main, PurchaseOrderDetail.class, "delete from scm_purchase_order_detail where sort_order=? and purchase_order_id=?", new Object[]{sortOrder, Integer.parseInt(orderId)});//deleting all preveously trashed records
      main.param(Integer.parseInt(orderStatusId));
      main.param(Integer.parseInt(orderId));
      AppService.updateSql(main, PurchaseOrder.class, "update scm_purchase_order set purchase_order_status_id=? where id=?", false);// drafr or confirmed
    }
  }
}
