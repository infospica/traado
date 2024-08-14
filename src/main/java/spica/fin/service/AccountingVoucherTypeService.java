/*
 * @(#)AccountingVoucherTypeService.java	1.0 Fri Apr 28 12:24:49 IST 2017 
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
import spica.fin.domain.AccountingVoucherType;
import wawo.entity.core.UserMessageException;

/**
 * AccountingVoucherTypeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 28 12:24:49 IST 2017
 */
public abstract class AccountingVoucherTypeService {

  /**
   * AccountingVoucherType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingVoucherTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_voucher_type", AccountingVoucherType.class, main);
    sql.main("select t1.id,t1.title,t1.sort_order,t1.created_by,t1.modified_by,t1.created_at,t1.modified_at,t1.style_icon,t1.line_break,t1.description,t1.display_color from fin_accounting_voucher_type t1"); //Main query
    sql.count("select count(t1.id) as total from fin_accounting_voucher_type t1"); //Count query
    sql.orderBy("t1.sort_order,upper(t1.title)");
    sql.cond(""); //Join Query

    sql.string(new String[]{"t1.title", "t1.created_by", "t1.modified_by", "t1.style_icon", "t1.line_break"}); //String search or sort fields
    sql.number(new String[]{"t1.id", "t1.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.created_at", "t1.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountingVoucherType.
   *
   * @param main
   * @return List of AccountingVoucherType
   */
  public static final List<AccountingVoucherType> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountingVoucherTypeSqlPaged(main));
  }

//  /**
//   * Return list of AccountingVoucherType based on condition
//   * @param main
//   * @return List<AccountingVoucherType>
//   */
//  public static final List<AccountingVoucherType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountingVoucherTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountingVoucherType by key.
   *
   * @param main
   * @param accountingVoucherType
   * @return AccountingVoucherType
   */
  public static final AccountingVoucherType selectByPk(Main main, AccountingVoucherType accountingVoucherType) {
    return (AccountingVoucherType) AppService.find(main, AccountingVoucherType.class, accountingVoucherType.getId());
  }

  /**
   * Insert AccountingVoucherType.
   *
   * @param main
   * @param accountingVoucherType
   */
  public static final void insert(Main main, AccountingVoucherType accountingVoucherType) {
    insertAble(main, accountingVoucherType);  //Validating
    AppService.insert(main, accountingVoucherType);

  }

  /**
   * Update AccountingVoucherType by key.
   *
   * @param main
   * @param accountingVoucherType
   * @return AccountingVoucherType
   */
  public static final AccountingVoucherType updateByPk(Main main, AccountingVoucherType accountingVoucherType) {
    updateAble(main, accountingVoucherType); //Validating
    return (AccountingVoucherType) AppService.update(main, accountingVoucherType);
  }

  /**
   * Insert or update AccountingVoucherType
   *
   * @param main
   * @param accountingVoucherType
   */
  public static void insertOrUpdate(Main main, AccountingVoucherType accountingVoucherType) {
    if (accountingVoucherType.getId() == null) {
      insert(main, accountingVoucherType);
    } else {
      updateByPk(main, accountingVoucherType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingVoucherType
   */
  public static void clone(Main main, AccountingVoucherType accountingVoucherType) {
    accountingVoucherType.setId(null); //Set to null for insert
    insert(main, accountingVoucherType);
  }

  /**
   * Delete AccountingVoucherType.
   *
   * @param main
   * @param accountingVoucherType
   */
  public static final void deleteByPk(Main main, AccountingVoucherType accountingVoucherType) {
    deleteAble(main, accountingVoucherType); //Validation
    AppService.delete(main, AccountingVoucherType.class, accountingVoucherType.getId());
  }

  /**
   * Delete Array of AccountingVoucherType.
   *
   * @param main
   * @param accountingVoucherType
   */
  public static final void deleteByPkArray(Main main, AccountingVoucherType[] accountingVoucherType) {
    for (AccountingVoucherType e : accountingVoucherType) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param accountingVoucherType
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingVoucherType accountingVoucherType) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingVoucherType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingVoucherType accountingVoucherType) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_voucher_type where upper(title)=?", new Object[]{StringUtil.toUpperCase(accountingVoucherType.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingVoucherType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingVoucherType accountingVoucherType) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_voucher_type where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingVoucherType.getTitle()), accountingVoucherType.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
