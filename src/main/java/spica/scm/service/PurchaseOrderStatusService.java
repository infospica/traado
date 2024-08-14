/*
 * @(#)PurchaseOrderStatusService.java	1.0 Mon Apr 11 14:41:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.PurchaseOrderStatus;
import wawo.entity.core.UserMessageException;

/**
 * PurchaseOrderStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Apr 11 14:41:21 IST 2016
 */
public abstract class PurchaseOrderStatusService {

  /**
   * PurchaseOrderStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseOrderStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_order_status", PurchaseOrderStatus.class, main);
    sql.main("select scm_purchase_order_status.id,scm_purchase_order_status.title,scm_purchase_order_status.sort_order,scm_purchase_order_status.created_by,scm_purchase_order_status.modified_by,scm_purchase_order_status.created_at,scm_purchase_order_status.modified_at from scm_purchase_order_status scm_purchase_order_status"); //Main query
    sql.count("select count(scm_purchase_order_status.id) from scm_purchase_order_status scm_purchase_order_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_purchase_order_status.title", "scm_purchase_order_status.created_by", "scm_purchase_order_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_order_status.id", "scm_purchase_order_status.sort_order"}); //Number search or sort fields
    sql.date(new String[]{"scm_purchase_order_status.created_at", "scm_purchase_order_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseOrderStatus.
   *
   * @param main
   * @return List of PurchaseOrderStatus
   */
  public static final List<PurchaseOrderStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseOrderStatusSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseOrderStatus based on condition
//   * @param main
//   * @return List<PurchaseOrderStatus>
//   */
//  public static final List<PurchaseOrderStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseOrderStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PurchaseOrderStatus by key.
   *
   * @param main
   * @param purchaseOrderStatus
   * @return PurchaseOrderStatus
   */
  public static final PurchaseOrderStatus selectByPk(Main main, PurchaseOrderStatus purchaseOrderStatus) {
    return (PurchaseOrderStatus) AppService.find(main, PurchaseOrderStatus.class, purchaseOrderStatus.getId());
  }

  /**
   * Insert PurchaseOrderStatus.
   *
   * @param main
   * @param purchaseOrderStatus
   */
  public static final void insert(Main main, PurchaseOrderStatus purchaseOrderStatus) {
    insertAble(main, purchaseOrderStatus);  //Validating
    AppService.insert(main, purchaseOrderStatus);

  }

  /**
   * Update PurchaseOrderStatus by key.
   *
   * @param main
   * @param purchaseOrderStatus
   * @return PurchaseOrderStatus
   */
  public static final PurchaseOrderStatus updateByPk(Main main, PurchaseOrderStatus purchaseOrderStatus) {
    updateAble(main, purchaseOrderStatus); //Validating
    return (PurchaseOrderStatus) AppService.update(main, purchaseOrderStatus);
  }

  /**
   * Insert or update PurchaseOrderStatus
   *
   * @param main
   * @param PurchaseOrderStatus
   */
  public static void insertOrUpdate(Main main, PurchaseOrderStatus purchaseOrderStatus) {
    if (purchaseOrderStatus.getId() == null) {
      insert(main, purchaseOrderStatus);
    } else {
      updateByPk(main, purchaseOrderStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseOrderStatus
   */
  public static void clone(Main main, PurchaseOrderStatus purchaseOrderStatus) {
    purchaseOrderStatus.setId(null); //Set to null for insert
    insert(main, purchaseOrderStatus);
  }

  /**
   * Delete PurchaseOrderStatus.
   *
   * @param main
   * @param purchaseOrderStatus
   */
  public static final void deleteByPk(Main main, PurchaseOrderStatus purchaseOrderStatus) {
    deleteAble(main, purchaseOrderStatus); //Validation
    AppService.delete(main, PurchaseOrderStatus.class, purchaseOrderStatus.getId());
  }

  /**
   * Delete Array of PurchaseOrderStatus.
   *
   * @param main
   * @param purchaseOrderStatus
   */
  public static final void deleteByPkArray(Main main, PurchaseOrderStatus[] purchaseOrderStatus) {
    for (PurchaseOrderStatus e : purchaseOrderStatus) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param purchaseOrderStatus
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, PurchaseOrderStatus purchaseOrderStatus) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param purchaseOrderStatus
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, PurchaseOrderStatus purchaseOrderStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_purchase_order_status where upper(title)=?", new Object[]{StringUtil.toUpperCase(purchaseOrderStatus.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param purchaseOrderStatus
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, PurchaseOrderStatus purchaseOrderStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_purchase_order_status where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(purchaseOrderStatus.getTitle()), purchaseOrderStatus.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
