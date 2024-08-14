/*
 * @(#)PurchaseReturnStockCatService.java	1.0 Wed May 25 13:23:32 IST 2016 
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
import spica.scm.domain.PurchaseReturnStockCat;
import wawo.entity.core.UserMessageException;

/**
 * PurchaseReturnStockCatService
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:32 IST 2016 
 */

public abstract class PurchaseReturnStockCatService {  
 
  public static final int STOCK_DAMAGED_AND_EXPIRED = 1;
  public static final int STOCK_NON_MOVING_AND_NEAR_EXPIRY = 2;
 /**
   * PurchaseReturnStockCat paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReturnStockCatSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_return_stock_cat", PurchaseReturnStockCat.class, main);
    sql.main("select scm_purchase_return_stock_cat.id,scm_purchase_return_stock_cat.title,scm_purchase_return_stock_cat.priority,scm_purchase_return_stock_cat.created_by,scm_purchase_return_stock_cat.modified_by,scm_purchase_return_stock_cat.created_at,scm_purchase_return_stock_cat.modified_at from scm_purchase_return_stock_cat scm_purchase_return_stock_cat"); //Main query
    sql.count("select count(scm_purchase_return_stock_cat.id) from scm_purchase_return_stock_cat scm_purchase_return_stock_cat"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_purchase_return_stock_cat.title","scm_purchase_return_stock_cat.created_by","scm_purchase_return_stock_cat.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_return_stock_cat.id","scm_purchase_return_stock_cat.priority"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_purchase_return_stock_cat.created_at","scm_purchase_return_stock_cat.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of PurchaseReturnStockCat.
  * @param main
  * @return List of PurchaseReturnStockCat
  */
  public static final List<PurchaseReturnStockCat> listPaged(Main main) {
     return AppService.listPagedJpa(main, getPurchaseReturnStockCatSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseReturnStockCat based on condition
//   * @param main
//   * @return List<PurchaseReturnStockCat>
//   */
//  public static final List<PurchaseReturnStockCat> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseReturnStockCatSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select PurchaseReturnStockCat by key.
  * @param main
  * @param purchaseReturnStockCat
  * @return PurchaseReturnStockCat
  */
  public static final PurchaseReturnStockCat selectByPk(Main main, PurchaseReturnStockCat purchaseReturnStockCat) {
    return (PurchaseReturnStockCat) AppService.find(main, PurchaseReturnStockCat.class, purchaseReturnStockCat.getId());
  }

 /**
  * Insert PurchaseReturnStockCat.
  * @param main
  * @param purchaseReturnStockCat
  */
  public static final void insert(Main main, PurchaseReturnStockCat purchaseReturnStockCat) {
    insertAble(main, purchaseReturnStockCat);  //Validating
    AppService.insert(main, purchaseReturnStockCat);

  }

 /**
  * Update PurchaseReturnStockCat by key.
  * @param main
  * @param purchaseReturnStockCat
  * @return PurchaseReturnStockCat
  */
  public static final PurchaseReturnStockCat updateByPk(Main main, PurchaseReturnStockCat purchaseReturnStockCat) {
    updateAble(main, purchaseReturnStockCat); //Validating
    return (PurchaseReturnStockCat) AppService.update(main, purchaseReturnStockCat);
  }

  /**
   * Insert or update PurchaseReturnStockCat
   *
   * @param main
   * @param PurchaseReturnStockCat
   */
  public static void insertOrUpdate(Main main, PurchaseReturnStockCat purchaseReturnStockCat) {
    if (purchaseReturnStockCat.getId() == null) {
      insert(main, purchaseReturnStockCat);
    }
    else{
        updateByPk(main, purchaseReturnStockCat);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReturnStockCat
   */
  public static void clone(Main main, PurchaseReturnStockCat purchaseReturnStockCat) {
    purchaseReturnStockCat.setId(null); //Set to null for insert
    insert(main, purchaseReturnStockCat);
  }

 /**
  * Delete PurchaseReturnStockCat.
  * @param main
  * @param purchaseReturnStockCat
  */
  public static final void deleteByPk(Main main, PurchaseReturnStockCat purchaseReturnStockCat) {
    deleteAble(main, purchaseReturnStockCat); //Validation
    AppService.delete(main, PurchaseReturnStockCat.class, purchaseReturnStockCat.getId());
  }
	
 /**
  * Delete Array of PurchaseReturnStockCat.
  * @param main
  * @param purchaseReturnStockCat
  */
  public static final void deleteByPkArray(Main main, PurchaseReturnStockCat[] purchaseReturnStockCat) {
    for (PurchaseReturnStockCat e : purchaseReturnStockCat) {
      deleteByPk(main, e);
    }
  }
   /**
   * Validate delete.
   *
   * @param main
   * @param purchaseReturnStockCat
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, PurchaseReturnStockCat purchaseReturnStockCat) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param purchaseReturnStockCat
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, PurchaseReturnStockCat purchaseReturnStockCat) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param purchaseReturnStockCat
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, PurchaseReturnStockCat purchaseReturnStockCat) throws UserMessageException {

  }
}
