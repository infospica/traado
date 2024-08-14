/*
 * @(#)PurchaseCreditOnService.java	1.0 Thu Apr 07 11:31:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.scm.service;

import java.util.List;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.PurchaseCreditOn;
import wawo.entity.core.UserMessageException;

/**
 * PurchaseCreditOnService
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:25 IST 2016 
 */

public abstract class PurchaseCreditOnService {  
 
 /**
   * PurchaseCreditOn paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseCreditOnSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_credit_on", PurchaseCreditOn.class, main);
    sql.main("select scm_purchase_credit_on.id,scm_purchase_credit_on.title,scm_purchase_credit_on.created_by,scm_purchase_credit_on.modified_by,scm_purchase_credit_on.created_at,scm_purchase_credit_on.modified_at from scm_purchase_credit_on scm_purchase_credit_on"); //Main query
    sql.count("select count(scm_purchase_credit_on.id) from scm_purchase_credit_on scm_purchase_credit_on"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_purchase_credit_on.title","scm_purchase_credit_on.created_by","scm_purchase_credit_on.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_credit_on.id"}); //Number search or sort fields
    sql.date(new String[]{"scm_purchase_credit_on.created_at","scm_purchase_credit_on.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of PurchaseCreditOn.
  * @param main
  * @return List of PurchaseCreditOn
  */
  public static final List<PurchaseCreditOn> listPaged(Main main) {
     return AppService.listPagedJpa(main, getPurchaseCreditOnSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseCreditOn based on condition
//   * @param main
//   * @return List<PurchaseCreditOn>
//   */
//  public static final List<PurchaseCreditOn> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseCreditOnSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select PurchaseCreditOn by key.
  * @param main
  * @param purchaseCreditOn
  * @return PurchaseCreditOn
  */
  public static final PurchaseCreditOn selectByPk(Main main, PurchaseCreditOn purchaseCreditOn) {
    return (PurchaseCreditOn) AppService.find(main, PurchaseCreditOn.class, purchaseCreditOn.getId());
  }

 /**
  * Insert PurchaseCreditOn.
  * @param main
  * @param purchaseCreditOn
  */
  public static final void insert(Main main, PurchaseCreditOn purchaseCreditOn) {
    insertAble(main, purchaseCreditOn);  //Validating
    AppService.insert(main, purchaseCreditOn);

  }

 /**
  * Update PurchaseCreditOn by key.
  * @param main
  * @param purchaseCreditOn
  * @return PurchaseCreditOn
  */
  public static final PurchaseCreditOn updateByPk(Main main, PurchaseCreditOn purchaseCreditOn) {
    updateAble(main, purchaseCreditOn); //Validating
    return (PurchaseCreditOn) AppService.update(main, purchaseCreditOn);
  }

  /**
   * Insert or update PurchaseCreditOn
   *
   * @param main
   * @param PurchaseCreditOn
   */
  public static void insertOrUpdate(Main main, PurchaseCreditOn purchaseCreditOn) {
    if (purchaseCreditOn.getId() == null) {
      insert(main, purchaseCreditOn);
    } else {
    updateByPk(main, purchaseCreditOn);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseCreditOn
   */
  public static void clone(Main main, PurchaseCreditOn purchaseCreditOn) {
    purchaseCreditOn.setId(null); //Set to null for insert
    insert(main, purchaseCreditOn);
  }

 /**
  * Delete PurchaseCreditOn.
  * @param main
  * @param purchaseCreditOn
  */
  public static final void deleteByPk(Main main, PurchaseCreditOn purchaseCreditOn) {
    deleteAble(main, purchaseCreditOn); //Validation
    AppService.delete(main, PurchaseCreditOn.class, purchaseCreditOn.getId());
  }
	
 /**
  * Delete Array of PurchaseCreditOn.
  * @param main
  * @param purchaseCreditOn
  */
  public static final void deleteByPkArray(Main main, PurchaseCreditOn[] purchaseCreditOn) {
    for (PurchaseCreditOn e : purchaseCreditOn) {
      deleteByPk(main, e);
    }
  }
  /**
   * Validate delete.
   *
   * @param main
   * @param purchaseCreditOn
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, PurchaseCreditOn purchaseCreditOn) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param purchaseCreditOn
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, PurchaseCreditOn purchaseCreditOn) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param purchaseCreditOn
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, PurchaseCreditOn purchaseCreditOn) throws UserMessageException {

  }
}
