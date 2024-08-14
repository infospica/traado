/*
 * @(#)AccountingHeadService.java	1.0 Fri Apr 28 12:24:49 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.AccountingHead;
import wawo.entity.core.UserMessageException;

/**
 * AccountingHeadService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 28 12:24:49 IST 2017
 */
public abstract class AccountingHeadService {

  /**
   * AccountingHead paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingHeadSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_head", AccountingHead.class, main);
    sql.main("select t1.id,t1.title,t1.sort_order from fin_accounting_head t1"); //Main query
    sql.count("select count(t1.id) as total from fin_accounting_head t1"); //Count query
    sql.join(""); //Join Query
    sql.orderBy("t1.sort_order,upper(t1.title)");

    sql.string(new String[]{"t1.title"}); //String search or sort fields
    sql.number(new String[]{"t1.id", "t1.sort_order"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountingHead.
   *
   * @param main
   * @return List of AccountingHead
   */
  public static final List<AccountingHead> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountingHeadSqlPaged(main));
  }

//  /**
//   * Return list of AccountingHead based on condition
//   * @param main
//   * @return List<AccountingHead>
//   */
//  public static final List<AccountingHead> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountingHeadSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountingHead by key.
   *
   * @param main
   * @param accountingHead
   * @return AccountingHead
   */
  public static final AccountingHead selectByPk(Main main, AccountingHead accountingHead) {
    return (AccountingHead) AppService.find(main, AccountingHead.class, accountingHead.getId());
  }

  /**
   * Insert AccountingHead.
   *
   * @param main
   * @param accountingHead
   */
  public static final void insert(Main main, AccountingHead accountingHead) {
    insertAble(main, accountingHead);  //Validating
    AppService.insert(main, accountingHead);

  }

  /**
   * Update AccountingHead by key.
   *
   * @param main
   * @param accountingHead
   * @return AccountingHead
   */
  public static final AccountingHead updateByPk(Main main, AccountingHead accountingHead) {
    updateAble(main, accountingHead); //Validating
    return (AccountingHead) AppService.update(main, accountingHead);
  }

  /**
   * Insert or update AccountingHead
   *
   * @param main
   * @param accountingHead
   */
  public static void insertOrUpdate(Main main, AccountingHead accountingHead) {
    if (accountingHead.getId() == null) {
      insert(main, accountingHead);
    } else {
      updateByPk(main, accountingHead);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingHead
   */
  public static void clone(Main main, AccountingHead accountingHead) {
    accountingHead.setId(null); //Set to null for insert
    insert(main, accountingHead);
  }

  /**
   * Delete AccountingHead.
   *
   * @param main
   * @param accountingHead
   */
  public static final void deleteByPk(Main main, AccountingHead accountingHead) {
    deleteAble(main, accountingHead); //Validation
    AppService.delete(main, AccountingHead.class, accountingHead.getId());
  }

  /**
   * Delete Array of AccountingHead.
   *
   * @param main
   * @param accountingHead
   */
  public static final void deleteByPkArray(Main main, AccountingHead[] accountingHead) {
    for (AccountingHead e : accountingHead) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param accountingHead
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingHead accountingHead) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingHead
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingHead accountingHead) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_head where upper(title)=?", new Object[]{StringUtil.toUpperCase(accountingHead.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingHead
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingHead accountingHead) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_head where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingHead.getTitle()), accountingHead.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
