/*
 * @(#)PurchaseReceiptPlanService.java	1.0 Mon Apr 11 14:41:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import spica.scm.domain.PurchaseOrder;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.PurchaseReceiptPlan;
import spica.scm.domain.PurchaseShipmentPlan;
import spica.scm.validate.PurchaseReceiptPlanIs;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;

/**
 * PurchaseReceiptPlanService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Apr 11 14:41:21 IST 2016
 */
public abstract class PurchaseReceiptPlanService {

  public static final int PURCHASE_ORDER_STATUS_CONFIRMED = 2;

  /**
   * PurchaseReceiptPlan paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReceiptPlanSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_receipt_plan", PurchaseReceiptPlan.class, main);
    sql.main("select scm_purchase_receipt_plan.id,scm_purchase_receipt_plan.purchase_order_item_id,scm_purchase_receipt_plan.purchase_order_id,scm_purchase_receipt_plan.quantity_required,scm_purchase_receipt_plan.expected_date from scm_purchase_receipt_plan scm_purchase_receipt_plan "); //Main query
    sql.count("select count(scm_purchase_receipt_plan.id) from scm_purchase_receipt_plan scm_purchase_receipt_plan "); //Count query
    sql.join("left outer join scm_purchase_order scm_purchase_receipt_planpurchase_order_item_id on (scm_purchase_receipt_planpurchase_order_item_id.id = scm_purchase_receipt_plan.purchase_order_item_id) left outer join scm_purchase_order scm_purchase_receipt_planpurchase_order_id on (scm_purchase_receipt_planpurchase_order_id.id = scm_purchase_receipt_plan.purchase_order_id)"); //Join Query

    sql.string(new String[]{"scm_purchase_receipt_planpurchase_order_item_id.purchase_order_no"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_receipt_plan.id", "scm_purchase_receipt_plan.quantity_required"}); //Number search or sort fields
    sql.date(new String[]{"scm_purchase_receipt_plan.expected_date"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseReceiptPlan.
   *
   * @param main
   * @return List of PurchaseReceiptPlan
   */
  public static final List<PurchaseReceiptPlan> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseReceiptPlanSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseReceiptPlan based on condition
//   * @param main
//   * @return List<PurchaseReceiptPlan>
//   */
//  public static final List<PurchaseReceiptPlan> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseReceiptPlanSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PurchaseReceiptPlan by key.
   *
   * @param main
   * @param purchaseReceiptPlan
   * @return PurchaseReceiptPlan
   */
  public static final PurchaseReceiptPlan selectByPk(Main main, PurchaseReceiptPlan purchaseReceiptPlan) {
    return (PurchaseReceiptPlan) AppService.find(main, PurchaseReceiptPlan.class, purchaseReceiptPlan.getId());
  }

  /**
   * Insert PurchaseReceiptPlan.
   *
   * @param main
   * @param purchaseReceiptPlan
   */
  public static final void insert(Main main, PurchaseReceiptPlan purchaseReceiptPlan) {
    PurchaseReceiptPlanIs.insertAble(main, purchaseReceiptPlan);  //Validating
    AppService.insert(main, purchaseReceiptPlan);

  }

  /**
   * Update PurchaseReceiptPlan by key.
   *
   * @param main
   * @param purchaseReceiptPlan
   * @return PurchaseReceiptPlan
   */
  public static final PurchaseReceiptPlan updateByPk(Main main, PurchaseReceiptPlan purchaseReceiptPlan) {
    PurchaseReceiptPlanIs.updateAble(main, purchaseReceiptPlan); //Validating
    return (PurchaseReceiptPlan) AppService.update(main, purchaseReceiptPlan);
  }

  /**
   * Insert or update PurchaseReceiptPlan
   *
   * @param main
   * @param PurchaseReceiptPlan
   */
  public static void insertOrUpdate(Main main, PurchaseReceiptPlan purchaseReceiptPlan) {
    if (purchaseReceiptPlan.getId() == null) {
//      AppService.deleteSql(main, PurchaseReceiptPlan.class, "delete from scm_purchase_receipt_plan where user_id=?", new Object[]{user.getId()}); //Deleting based on user id
      insert(main, purchaseReceiptPlan);
    }
    updateByPk(main, purchaseReceiptPlan);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReceiptPlan
   */
  public static void clone(Main main, PurchaseReceiptPlan purchaseReceiptPlan) {
    purchaseReceiptPlan.setId(null); //Set to null for insert
    insert(main, purchaseReceiptPlan);
  }

  /**
   * Delete PurchaseReceiptPlan.
   *
   * @param main
   * @param purchaseReceiptPlan
   */
  public static final void deleteByPk(Main main, PurchaseReceiptPlan purchaseReceiptPlan) {
    PurchaseReceiptPlanIs.deleteAble(main, purchaseReceiptPlan); //Validation
    AppService.delete(main, PurchaseReceiptPlan.class, purchaseReceiptPlan.getId());
  }

  /**
   * Delete Array of PurchaseReceiptPlan.
   *
   * @param main
   * @param purchaseReceiptPlan
   */
  public static final void deleteByPkArray(Main main, PurchaseReceiptPlan[] purchaseReceiptPlan) {
    for (PurchaseReceiptPlan e : purchaseReceiptPlan) {
      deleteByPk(main, e);
    }
  }

  public static void insertOrUpdate(MainView main, List<PurchaseShipmentPlan> purchaseShipmentPlanList, Date datePlan1, Date datePlan2, Date datePlan3) {
    int i = 0;
    for (PurchaseShipmentPlan shipPlan : purchaseShipmentPlanList) {
      if (i == 0) {
        AppService.deleteSql(main, PurchaseShipmentPlan.class, "delete from scm_purchase_receipt_plan where purchase_order_id=? ", new Object[]{shipPlan.getPurchaseOrderId()});
      }
      if (shipPlan.getQty1() != null) {
        insertRecieptPlan(main, datePlan1, shipPlan, 1);
      }
      if (shipPlan.getQty2() != null) {
        insertRecieptPlan(main, datePlan2, shipPlan, 2);
      }
      if (shipPlan.getQty3() != null) {
        insertRecieptPlan(main, datePlan3, shipPlan, 3);
      }
      i++;
    }
  }

  private static void insertRecieptPlan(Main main, Date date, PurchaseShipmentPlan plan, int seq) {
    String sql = "INSERT INTO scm_purchase_receipt_plan (quantity_required, expected_date, purchase_order_item_id, purchase_order_id, id) VALUES (?, ?, ?,?,?)";
    main.getParamData().clearParam();
    if (seq == 1) {
      main.param(plan.getQty1());
    } else if (seq == 2) {
      main.param(plan.getQty2());
    } else if (seq == 3) {
      main.param(plan.getQty3());
    }
    main.param(date);
    main.param(plan.getId());
    main.param(plan.getPurchaseOrderId());
    AppService.insertSql(main, PurchaseReceiptPlan.class, "scm_purchase_receipt_plan", sql, false);

  }

  public static List<PurchaseReceiptPlan> getShipmentDate(Main main, PurchaseOrder purchaseOrder) {
    return AppDb.getList(main.dbConnector(), PurchaseReceiptPlan.class, "select distinct expected_date from scm_purchase_receipt_plan where purchase_order_id=?", new Object[]{purchaseOrder.getId()});//distinct,order by 1

  }

  public static void updatePurchaseOrderStatus(MainView main, PurchaseOrder purchaseOrder) {
    main.clear();
    main.param(4);
    main.param(purchaseOrder.getId());
    AppService.updateSql(main, PurchaseOrder.class, "update scm_purchase_order set modified_by=?,modified_at=? ,purchase_order_status_id=? where id=?", true);
  }

  public static Long getShipmentCount(Main main, PurchaseOrder purchaseOrder) {
    if (purchaseOrder.getId() == null) {
      return new Long(0);
    }
    return main.em().count("select count(id) from scm_purchase_receipt_plan where scm_purchase_receipt_plan.purchase_order_id = ?", new Object[]{purchaseOrder.getId()});
  }
}
