/*
 * @(#)PurchaseReturnItemstatusService.java	1.0 Wed May 25 13:23:32 IST 2016 
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
import spica.scm.domain.PurchaseReturnItemStatus;
import wawo.entity.core.UserMessageException;

/**
 * PurchaseReturnItemstatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:32 IST 2016
 */
public abstract class PurchaseReturnItemstatusService {

  public static final int DAMAGED = 1;
  public static final int EXPIRED = 2;
  public static final int NONMOVING = 3;

  public static final String STATUS_DAMAGED = "Damaged";
  public static final String STATUS_EXPIRED = "Expired";
  public static final String STATUS_NONMOVING = "Non-Moving";

  /**
   * PurchaseReturnItemStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReturnItemstatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_return_itemstatus", PurchaseReturnItemStatus.class, main);
    sql.main("select scm_purchase_return_itemstatus.id,scm_purchase_return_itemstatus.title,scm_purchase_return_itemstatus.priority,scm_purchase_return_itemstatus.created_by,scm_purchase_return_itemstatus.modified_by,scm_purchase_return_itemstatus.created_at,scm_purchase_return_itemstatus.modified_at from scm_purchase_return_itemstatus scm_purchase_return_itemstatus"); //Main query
    sql.count("select count(scm_purchase_return_itemstatus.id) from scm_purchase_return_itemstatus scm_purchase_return_itemstatus"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_purchase_return_itemstatus.title", "scm_purchase_return_itemstatus.created_by", "scm_purchase_return_itemstatus.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_return_itemstatus.id", "scm_purchase_return_itemstatus.priority"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_purchase_return_itemstatus.created_at", "scm_purchase_return_itemstatus.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseReturnItemStatus.
   *
   * @param main
   * @return List of PurchaseReturnItemStatus
   */
  public static final List<PurchaseReturnItemStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseReturnItemstatusSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseReturnItemStatus based on condition
//   * @param main
//   * @return List<PurchaseReturnItemstatus>
//   */
//  public static final List<PurchaseReturnItemstatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseReturnItemstatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PurchaseReturnItemStatus by key.
   *
   * @param main
   * @param purchaseReturnItemstatus
   * @return PurchaseReturnItemStatus
   */
  public static final PurchaseReturnItemStatus selectByPk(Main main, PurchaseReturnItemStatus purchaseReturnItemstatus) {
    return (PurchaseReturnItemStatus) AppService.find(main, PurchaseReturnItemStatus.class, purchaseReturnItemstatus.getId());
  }

  /**
   * Insert PurchaseReturnItemStatus.
   *
   * @param main
   * @param purchaseReturnItemstatus
   */
  public static final void insert(Main main, PurchaseReturnItemStatus purchaseReturnItemstatus) {
    insertAble(main, purchaseReturnItemstatus);  //Validating
    AppService.insert(main, purchaseReturnItemstatus);

  }

  /**
   * Update PurchaseReturnItemStatus by key.
   *
   * @param main
   * @param purchaseReturnItemstatus
   * @return PurchaseReturnItemStatus
   */
  public static final PurchaseReturnItemStatus updateByPk(Main main, PurchaseReturnItemStatus purchaseReturnItemstatus) {
    updateAble(main, purchaseReturnItemstatus); //Validating
    return (PurchaseReturnItemStatus) AppService.update(main, purchaseReturnItemstatus);
  }

  /**
   * Insert or update PurchaseReturnItemStatus
   *
   * @param main
   * @param PurchaseReturnItemstatus
   */
  public static void insertOrUpdate(Main main, PurchaseReturnItemStatus purchaseReturnItemstatus) {
    if (purchaseReturnItemstatus.getId() == null) {
      insert(main, purchaseReturnItemstatus);
    } else {
      updateByPk(main, purchaseReturnItemstatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReturnItemstatus
   */
  public static void clone(Main main, PurchaseReturnItemStatus purchaseReturnItemstatus) {
    purchaseReturnItemstatus.setId(null); //Set to null for insert
    insert(main, purchaseReturnItemstatus);
  }

  /**
   * Delete PurchaseReturnItemStatus.
   *
   * @param main
   * @param purchaseReturnItemstatus
   */
  public static final void deleteByPk(Main main, PurchaseReturnItemStatus purchaseReturnItemstatus) {
    deleteAble(main, purchaseReturnItemstatus); //Validation
    AppService.delete(main, PurchaseReturnItemStatus.class, purchaseReturnItemstatus.getId());
  }

  /**
   * Delete Array of PurchaseReturnItemStatus.
   *
   * @param main
   * @param purchaseReturnItemstatus
   */
  public static final void deleteByPkArray(Main main, PurchaseReturnItemStatus[] purchaseReturnItemstatus) {
    for (PurchaseReturnItemStatus e : purchaseReturnItemstatus) {
      deleteByPk(main, e);
    }
  }
  /**
   * Validate delete.
   *
   * @param main
   * @param purchaseReturnItemstatus
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, PurchaseReturnItemStatus purchaseReturnItemstatus) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param purchaseReturnItemstatus
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, PurchaseReturnItemStatus purchaseReturnItemstatus) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param purchaseReturnItemstatus
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, PurchaseReturnItemStatus purchaseReturnItemstatus) throws UserMessageException {

  }
}
