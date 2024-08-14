/*
 * @(#)PurchaseReturnStatusService.java	1.0 Wed May 25 13:23:32 IST 2016 
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
import spica.scm.domain.PurchaseReturnStatus;
import wawo.entity.core.UserMessageException;

/**
 * PurchaseReturnStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:32 IST 2016
 */
public abstract class PurchaseReturnStatusService {

  /**
   * PurchaseReturnStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReturnStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_return_status", PurchaseReturnStatus.class, main);
    sql.main("select scm_purchase_return_status.id,scm_purchase_return_status.title,scm_purchase_return_status.sort_order,scm_purchase_return_status.created_by,scm_purchase_return_status.modified_by,scm_purchase_return_status.created_at,scm_purchase_return_status.modified_at from scm_purchase_return_status scm_purchase_return_status"); //Main query
    sql.count("select count(scm_purchase_return_status.id) from scm_purchase_return_status scm_purchase_return_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_purchase_return_status.title", "scm_purchase_return_status.created_by", "scm_purchase_return_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_return_status.id", "scm_purchase_return_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_purchase_return_status.created_at", "scm_purchase_return_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseReturnStatus.
   *
   * @param main
   * @return List of PurchaseReturnStatus
   */
  public static final List<PurchaseReturnStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseReturnStatusSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseReturnStatus based on condition
//   * @param main
//   * @return List<PurchaseReturnStatus>
//   */
//  public static final List<PurchaseReturnStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseReturnStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PurchaseReturnStatus by key.
   *
   * @param main
   * @param purchaseReturnStatus
   * @return PurchaseReturnStatus
   */
  public static final PurchaseReturnStatus selectByPk(Main main, PurchaseReturnStatus purchaseReturnStatus) {
    return (PurchaseReturnStatus) AppService.find(main, PurchaseReturnStatus.class, purchaseReturnStatus.getId());
  }

  /**
   * Insert PurchaseReturnStatus.
   *
   * @param main
   * @param purchaseReturnStatus
   */
  public static final void insert(Main main, PurchaseReturnStatus purchaseReturnStatus) {
    insertAble(main, purchaseReturnStatus);  //Validating
    AppService.insert(main, purchaseReturnStatus);

  }

  /**
   * Update PurchaseReturnStatus by key.
   *
   * @param main
   * @param purchaseReturnStatus
   * @return PurchaseReturnStatus
   */
  public static final PurchaseReturnStatus updateByPk(Main main, PurchaseReturnStatus purchaseReturnStatus) {
    updateAble(main, purchaseReturnStatus); //Validating
    return (PurchaseReturnStatus) AppService.update(main, purchaseReturnStatus);
  }

  /**
   * Insert or update PurchaseReturnStatus
   *
   * @param main
   * @param PurchaseReturnStatus
   */
  public static void insertOrUpdate(Main main, PurchaseReturnStatus purchaseReturnStatus) {
    if (purchaseReturnStatus.getId() == null) {
      insert(main, purchaseReturnStatus);
    } else {
      updateByPk(main, purchaseReturnStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReturnStatus
   */
  public static void clone(Main main, PurchaseReturnStatus purchaseReturnStatus) {
    purchaseReturnStatus.setId(null); //Set to null for insert
    insert(main, purchaseReturnStatus);
  }

  /**
   * Delete PurchaseReturnStatus.
   *
   * @param main
   * @param purchaseReturnStatus
   */
  public static final void deleteByPk(Main main, PurchaseReturnStatus purchaseReturnStatus) {
    deleteAble(main, purchaseReturnStatus); //Validation
    AppService.delete(main, PurchaseReturnStatus.class, purchaseReturnStatus.getId());
  }

  /**
   * Delete Array of PurchaseReturnStatus.
   *
   * @param main
   * @param purchaseReturnStatus
   */
  public static final void deleteByPkArray(Main main, PurchaseReturnStatus[] purchaseReturnStatus) {
    for (PurchaseReturnStatus e : purchaseReturnStatus) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param purchaseReturnStatus
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, PurchaseReturnStatus purchaseReturnStatus) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param purchaseReturnStatus
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, PurchaseReturnStatus purchaseReturnStatus) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param purchaseReturnStatus
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, PurchaseReturnStatus purchaseReturnStatus) throws UserMessageException {

  }

}
