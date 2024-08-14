/*
 * @(#)PurchaseReqStatusService.java	1.0 Mon Apr 11 14:41:21 IST 2016 
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
import spica.scm.domain.PurchaseReqStatus;
import wawo.entity.core.UserMessageException;

/**
 * PurchaseReqStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Apr 11 14:41:21 IST 2016
 */
public abstract class PurchaseReqStatusService {

  /**
   * PurchaseReqStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReqStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_req_status", PurchaseReqStatus.class, main);
    sql.main("select scm_purchase_req_status.id,scm_purchase_req_status.title,scm_purchase_req_status.sort_order,scm_purchase_req_status.created_by,scm_purchase_req_status.modified_by,scm_purchase_req_status.created_at,scm_purchase_req_status.modified_at from scm_purchase_req_status scm_purchase_req_status"); //Main query
    sql.count("select count(scm_purchase_req_status.id) from scm_purchase_req_status scm_purchase_req_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_purchase_req_status.title", "scm_purchase_req_status.created_by", "scm_purchase_req_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_req_status.id", "scm_purchase_req_status.sort_order"}); //Number search or sort fields
    sql.date(new String[]{"scm_purchase_req_status.created_at", "scm_purchase_req_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseReqStatus.
   *
   * @param main
   * @return List of PurchaseReqStatus
   */
  public static final List<PurchaseReqStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseReqStatusSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseReqStatus based on condition
//   * @param main
//   * @return List<PurchaseReqStatus>
//   */
//  public static final List<PurchaseReqStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseReqStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PurchaseReqStatus by key.
   *
   * @param main
   * @param purchaseReqStatus
   * @return PurchaseReqStatus
   */
  public static final PurchaseReqStatus selectByPk(Main main, PurchaseReqStatus purchaseReqStatus) {
    return (PurchaseReqStatus) AppService.find(main, PurchaseReqStatus.class, purchaseReqStatus.getId());
  }

  /**
   * Insert PurchaseReqStatus.
   *
   * @param main
   * @param purchaseReqStatus
   */
  public static final void insert(Main main, PurchaseReqStatus purchaseReqStatus) {
    insertAble(main, purchaseReqStatus);  //Validating
    AppService.insert(main, purchaseReqStatus);

  }

  /**
   * Update PurchaseReqStatus by key.
   *
   * @param main
   * @param purchaseReqStatus
   * @return PurchaseReqStatus
   */
  public static final PurchaseReqStatus updateByPk(Main main, PurchaseReqStatus purchaseReqStatus) {
    updateAble(main, purchaseReqStatus); //Validating
    return (PurchaseReqStatus) AppService.update(main, purchaseReqStatus);
  }

  /**
   * Insert or update PurchaseReqStatus
   *
   * @param main
   * @param PurchaseReqStatus
   */
  public static void insertOrUpdate(Main main, PurchaseReqStatus purchaseReqStatus) {
    if (purchaseReqStatus.getId() == null) {
      insert(main, purchaseReqStatus);
    } else {
      updateByPk(main, purchaseReqStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReqStatus
   */
  public static void clone(Main main, PurchaseReqStatus purchaseReqStatus) {
    purchaseReqStatus.setId(null); //Set to null for insert
    insert(main, purchaseReqStatus);
  }

  /**
   * Delete PurchaseReqStatus.
   *
   * @param main
   * @param purchaseReqStatus
   */
  public static final void deleteByPk(Main main, PurchaseReqStatus purchaseReqStatus) {
    deleteAble(main, purchaseReqStatus); //Validation
    AppService.delete(main, PurchaseReqStatus.class, purchaseReqStatus.getId());
  }

  /**
   * Delete Array of PurchaseReqStatus.
   *
   * @param main
   * @param purchaseReqStatus
   */
  public static final void deleteByPkArray(Main main, PurchaseReqStatus[] purchaseReqStatus) {
    for (PurchaseReqStatus e : purchaseReqStatus) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param purchaseReqStatus
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, PurchaseReqStatus purchaseReqStatus) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param purchaseReqStatus
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, PurchaseReqStatus purchaseReqStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_purchase_req_status where upper(title)=?", new Object[]{StringUtil.toUpperCase(purchaseReqStatus.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param purchaseReqStatus
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, PurchaseReqStatus purchaseReqStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_purchase_req_status where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(purchaseReqStatus.getTitle()), purchaseReqStatus.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
