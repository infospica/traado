/*
 * @(#)PurchaseReturnStockTypeService.java	1.0 Wed May 25 13:23:32 IST 2016 
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
import spica.scm.domain.PurchaseReturnStockType;
import wawo.entity.core.UserMessageException;

/**
 * PurchaseReturnStockTypeService
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:32 IST 2016 
 */

public abstract class PurchaseReturnStockTypeService {  
 
 /**
   * PurchaseReturnStockType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReturnStockTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_return_stock_type", PurchaseReturnStockType.class, main);
    sql.main("select scm_purchase_return_stock_type.id,scm_purchase_return_stock_type.title,scm_purchase_return_stock_type.priority,scm_purchase_return_stock_type.created_by,scm_purchase_return_stock_type.modified_by,scm_purchase_return_stock_type.created_at,scm_purchase_return_stock_type.modified_at from scm_purchase_return_stock_type scm_purchase_return_stock_type"); //Main query
    sql.count("select count(scm_purchase_return_stock_type.id) from scm_purchase_return_stock_type scm_purchase_return_stock_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_purchase_return_stock_type.title","scm_purchase_return_stock_type.created_by","scm_purchase_return_stock_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_return_stock_type.id","scm_purchase_return_stock_type.priority"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_purchase_return_stock_type.created_at","scm_purchase_return_stock_type.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of PurchaseReturnStockType.
  * @param main
  * @return List of PurchaseReturnStockType
  */
  public static final List<PurchaseReturnStockType> listPaged(Main main) {
     return AppService.listPagedJpa(main, getPurchaseReturnStockTypeSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseReturnStockType based on condition
//   * @param main
//   * @return List<PurchaseReturnStockType>
//   */
//  public static final List<PurchaseReturnStockType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseReturnStockTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select PurchaseReturnStockType by key.
  * @param main
  * @param purchaseReturnStockType
  * @return PurchaseReturnStockType
  */
  public static final PurchaseReturnStockType selectByPk(Main main, PurchaseReturnStockType purchaseReturnStockType) {
    return (PurchaseReturnStockType) AppService.find(main, PurchaseReturnStockType.class, purchaseReturnStockType.getId());
  }

 /**
  * Insert PurchaseReturnStockType.
  * @param main
  * @param purchaseReturnStockType
  */
  public static final void insert(Main main, PurchaseReturnStockType purchaseReturnStockType) {
    insertAble(main, purchaseReturnStockType);  //Validating
    AppService.insert(main, purchaseReturnStockType);

  }

 /**
  * Update PurchaseReturnStockType by key.
  * @param main
  * @param purchaseReturnStockType
  * @return PurchaseReturnStockType
  */
  public static final PurchaseReturnStockType updateByPk(Main main, PurchaseReturnStockType purchaseReturnStockType) {
    updateAble(main, purchaseReturnStockType); //Validating
    return (PurchaseReturnStockType) AppService.update(main, purchaseReturnStockType);
  }

  /**
   * Insert or update PurchaseReturnStockType
   *
   * @param main
   * @param PurchaseReturnStockType
   */
  public static void insertOrUpdate(Main main, PurchaseReturnStockType purchaseReturnStockType) {
    if (purchaseReturnStockType.getId() == null) {
      insert(main, purchaseReturnStockType);
    }
    else{
        updateByPk(main, purchaseReturnStockType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReturnStockType
   */
  public static void clone(Main main, PurchaseReturnStockType purchaseReturnStockType) {
    purchaseReturnStockType.setId(null); //Set to null for insert
    insert(main, purchaseReturnStockType);
  }

 /**
  * Delete PurchaseReturnStockType.
  * @param main
  * @param purchaseReturnStockType
  */
  public static final void deleteByPk(Main main, PurchaseReturnStockType purchaseReturnStockType) {
    deleteAble(main, purchaseReturnStockType); //Validation
    AppService.delete(main, PurchaseReturnStockType.class, purchaseReturnStockType.getId());
  }
	
 /**
  * Delete Array of PurchaseReturnStockType.
  * @param main
  * @param purchaseReturnStockType
  */
  public static final void deleteByPkArray(Main main, PurchaseReturnStockType[] purchaseReturnStockType) {
    for (PurchaseReturnStockType e : purchaseReturnStockType) {
      deleteByPk(main, e);
    }
  }
  /**
   * Validate delete.
   *
   * @param main
   * @param purchaseReturnStockType
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, PurchaseReturnStockType purchaseReturnStockType) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param purchaseReturnStockType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, PurchaseReturnStockType purchaseReturnStockType) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param purchaseReturnStockType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, PurchaseReturnStockType purchaseReturnStockType) throws UserMessageException {

  }
}
