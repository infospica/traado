/*
 * @(#)AccountingEntityTypeService.java	1.0 Fri Feb 24 15:58:34 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.fin.service;

import java.util.List;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.AccountingEntityType;
import wawo.entity.core.UserMessageException;

/**
 * AccountingEntityTypeService
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:34 IST 2017 
 */

public abstract class AccountingEntityTypeService {  
 
 /**
   * AccountingEntityType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingEntityTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_entity_type", AccountingEntityType.class, main);
    sql.main("select fin_accounting_entity_type.id,fin_accounting_entity_type.title,fin_accounting_entity_type.sort_order from fin_accounting_entity_type fin_accounting_entity_type"); //Main query
    sql.count("select count(fin_accounting_entity_type.id) as total from fin_accounting_entity_type fin_accounting_entity_type"); //Count query
    sql.join(""); //Join Query
    sql.orderBy("fin_accounting_entity_type.sort_order,upper(fin_accounting_entity_type.title)");
    sql.string(new String[]{"fin_accounting_entity_type.title"}); //String search or sort fields
    sql.number(new String[]{"fin_accounting_entity_type.id","fin_accounting_entity_type.sort_order"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of AccountingEntityType.
  * @param main
  * @return List of AccountingEntityType
  */
  public static final List<AccountingEntityType> listPaged(Main main) {
     return AppService.listPagedJpa(main, getAccountingEntityTypeSqlPaged(main));
  }

//  /**
//   * Return list of AccountingEntityType based on condition
//   * @param main
//   * @return List<AccountingEntityType>
//   */
//  public static final List<AccountingEntityType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountingEntityTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select AccountingEntityType by key.
  * @param main
  * @param accountingEntityType
  * @return AccountingEntityType
  */
  public static final AccountingEntityType selectByPk(Main main, AccountingEntityType accountingEntityType) {
    return (AccountingEntityType) AppService.find(main, AccountingEntityType.class, accountingEntityType.getId());
  }

 /**
  * Insert AccountingEntityType.
  * @param main
  * @param accountingEntityType
  */
  public static final void insert(Main main, AccountingEntityType accountingEntityType) {
    insertAble(main, accountingEntityType);  //Validating
    AppService.insert(main, accountingEntityType);

  }

 /**
  * Update AccountingEntityType by key.
  * @param main
  * @param accountingEntityType
  * @return AccountingEntityType
  */
  public static final AccountingEntityType updateByPk(Main main, AccountingEntityType accountingEntityType) {
    updateAble(main, accountingEntityType); //Validating
    return (AccountingEntityType) AppService.update(main, accountingEntityType);
  }

  /**
   * Insert or update AccountingEntityType
   *
   * @param main
   * @param accountingEntityType
   */
  public static void insertOrUpdate(Main main, AccountingEntityType accountingEntityType) {
    if (accountingEntityType.getId() == null) {
      insert(main, accountingEntityType);
    }
    else{
      updateByPk(main, accountingEntityType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingEntityType
   */
  public static void clone(Main main, AccountingEntityType accountingEntityType) {
    accountingEntityType.setId(null); //Set to null for insert
    insert(main, accountingEntityType);
  }

 /**
  * Delete AccountingEntityType.
  * @param main
  * @param accountingEntityType
  */
  public static final void deleteByPk(Main main, AccountingEntityType accountingEntityType) {
    deleteAble(main, accountingEntityType); //Validation
    AppService.delete(main, AccountingEntityType.class, accountingEntityType.getId());
  }
	
 /**
  * Delete Array of AccountingEntityType.
  * @param main
  * @param accountingEntityType
  */
  public static final void deleteByPkArray(Main main, AccountingEntityType[] accountingEntityType) {
    for (AccountingEntityType e : accountingEntityType) {
      deleteByPk(main, e);
    }
  }
  
  /**
   * Validate delete.
   *
   * @param main
   * @param accountingEntityType
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingEntityType accountingEntityType) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingEntityType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingEntityType accountingEntityType) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_entity_type where upper(title)=?", new Object[]{StringUtil.toUpperCase(accountingEntityType.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingEntityType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingEntityType accountingEntityType) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_entity_type where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingEntityType.getTitle()), accountingEntityType.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
