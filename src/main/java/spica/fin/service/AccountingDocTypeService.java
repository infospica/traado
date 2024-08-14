/*
 * @(#)AccountingDocTypeService.java	1.0 Fri Feb 24 15:58:34 IST 2017 
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
import spica.fin.domain.AccountingDocType;
import wawo.entity.core.UserMessageException;

/**
 * AccountingDocTypeService
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:34 IST 2017 
 */

public abstract class AccountingDocTypeService {  
 
 /**
   * AccountingDocType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingDocTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_doc_type", AccountingDocType.class, main);
    sql.main("select fin_accounting_doc_type.id,fin_accounting_doc_type.title,fin_accounting_doc_type.sort_order,fin_accounting_doc_type.voucher_type_id from fin_accounting_doc_type fin_accounting_doc_type"); //Main query
    sql.count("select count(fin_accounting_doc_type.id) as total from fin_accounting_doc_type fin_accounting_doc_type"); //Count query
    sql.join(""); //Join Query
    sql.orderBy("fin_accounting_doc_type.sort_order,upper(fin_accounting_doc_type.title)");

    sql.string(new String[]{"fin_accounting_doc_type.title"}); //String search or sort fields
    sql.number(new String[]{"fin_accounting_doc_type.id","fin_accounting_doc_type.sort_order"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of AccountingDocType.
  * @param main
  * @return List of AccountingDocType
  */
  public static final List<AccountingDocType> listPaged(Main main) {
     return AppService.listPagedJpa(main, getAccountingDocTypeSqlPaged(main));
  }

//  /**
//   * Return list of AccountingDocType based on condition
//   * @param main
//   * @return List<AccountingDocType>
//   */
//  public static final List<AccountingDocType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountingDocTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select AccountingDocType by key.
  * @param main
  * @param accountingDocType
  * @return AccountingDocType
  */
  public static final AccountingDocType selectByPk(Main main, AccountingDocType accountingDocType) {
    return (AccountingDocType) AppService.find(main, AccountingDocType.class, accountingDocType.getId());
  }

 /**
  * Insert AccountingDocType.
  * @param main
  * @param accountingDocType
  */
  public static final void insert(Main main, AccountingDocType accountingDocType) {
    insertAble(main, accountingDocType);  //Validating
    AppService.insert(main, accountingDocType);

  }

 /**
  * Update AccountingDocType by key.
  * @param main
  * @param accountingDocType
  * @return AccountingDocType
  */
  public static final AccountingDocType updateByPk(Main main, AccountingDocType accountingDocType) {
    updateAble(main, accountingDocType); //Validating
    return (AccountingDocType) AppService.update(main, accountingDocType);
  }

  /**
   * Insert or update AccountingDocType
   *
   * @param main
   * @param accountingDocType
   */
  public static void insertOrUpdate(Main main, AccountingDocType accountingDocType) {
    if (accountingDocType.getId() == null) {
      insert(main, accountingDocType);
    }
    else{
      updateByPk(main, accountingDocType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingDocType
   */
  public static void clone(Main main, AccountingDocType accountingDocType) {
    accountingDocType.setId(null); //Set to null for insert
    insert(main, accountingDocType);
  }

 /**
  * Delete AccountingDocType.
  * @param main
  * @param accountingDocType
  */
  public static final void deleteByPk(Main main, AccountingDocType accountingDocType) {
    deleteAble(main, accountingDocType); //Validation
    AppService.delete(main, AccountingDocType.class, accountingDocType.getId());
  }
	
 /**
  * Delete Array of AccountingDocType.
  * @param main
  * @param accountingDocType
  */
  public static final void deleteByPkArray(Main main, AccountingDocType[] accountingDocType) {
    for (AccountingDocType e : accountingDocType) {
      deleteByPk(main, e);
    }
  }
  
    /**
   * Validate delete.
   *
   * @param main
   * @param accountingDocType
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingDocType accountingDocType) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingDocType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingDocType accountingDocType) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_doc_type where upper(title)=?", new Object[]{StringUtil.toUpperCase(accountingDocType.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingDocType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingDocType accountingDocType) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_doc_type where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingDocType.getTitle()), accountingDocType.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
