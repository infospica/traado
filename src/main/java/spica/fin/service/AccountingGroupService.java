/*
 * @(#)AccountingGroupService.java	1.0 Fri Apr 28 12:24:49 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.fin.domain.AccountingGroup;
import spica.fin.domain.AccountingHead;
import static wawo.entity.util.Secure.main;

import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * AccountingGroupService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 28 12:24:49 IST 2017
 */
public abstract class AccountingGroupService {

  /**
   * AccountingGroup paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingGroupSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_group", AccountingGroup.class, main);
    sql.main("select t1.id,t1.parent_id,t1.title,t1.accounting_head_id,t1.company_id,t1.created_by,t1.modified_by,t1.modified_at,t1.created_at,t1.sort_order,t1.description from fin_accounting_group t1 "); //Main query
    sql.count("select count(t1.id) as total from fin_accounting_group t1 "); //Count query
    sql.join("left outer join fin_accounting_group t2 on (t2.id = t1.parent_id) left outer join fin_accounting_head t3 on (t3.id = t1.accounting_head_id)"); //Join Query
    sql.orderBy("upper(t1.title)");
    sql.string(new String[]{"t2.title", "t1.title", "t3.title", "t1.created_by", "t1.modified_by"}); //String search or sort fields
    sql.number(new String[]{"t1.id", "t1.company_id", "t1.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.modified_at", "t1.created_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountingGroup.
   *
   * @param main
   * @return List of AccountingGroup
   */
  public static final List<AccountingGroup> listPaged(Main main, AccountingHead ah) {
    SqlPage sql = getAccountingGroupSqlPaged(main);
    main.clear();
    sql.cond("where t1.opening_entry=0");
    if (ah != null) {
      sql.cond("and t1.accounting_head_id  = ?");
      main.param(ah.getId());
    }
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of AccountingGroup based on condition
//   * @param main
//   * @return List<AccountingGroup>
//   */
//  public static final List<AccountingGroup> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountingGroupSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountingGroup by key.
   *
   * @param main
   * @param accountingGroup
   * @return AccountingGroup
   */
  public static final AccountingGroup selectByPk(Main main, AccountingGroup accountingGroup) {
    return (AccountingGroup) AppService.find(main, AccountingGroup.class, accountingGroup.getId());
  }

  private static final AccountingGroup selectByParent(Main main, AccountingGroup accountingGroup) {
    return (AccountingGroup) AppService.single(main, AccountingGroup.class, "select * from fin_accounting_group where id = ?", new Object[]{accountingGroup.getParentId().getId()});
  }

  /**
   * Insert AccountingGroup.
   *
   * @param main
   * @param accountingGroup
   */
  public static final void insert(Main main, AccountingGroup accountingGroup) {
    insertAble(main, accountingGroup);  //Validating
    AppService.insert(main, accountingGroup);

  }

  /**
   * Update AccountingGroup by key.
   *
   * @param main
   * @param accountingGroup
   * @return AccountingGroup
   */
  public static final AccountingGroup updateByPk(Main main, AccountingGroup accountingGroup) {
    updateAble(main, accountingGroup); //Validating
    return (AccountingGroup) AppService.update(main, accountingGroup);
  }

  /**
   * Insert or update AccountingGroup
   *
   * @param main
   * @param accountingGroup
   */
  public static void insertOrUpdate(Main main, AccountingGroup accountingGroup) {
    if (accountingGroup.getParentId() != null) {
      AccountingGroup ag = AccountingGroupService.selectByParent(main, accountingGroup);
      if (ag.getGrandParentId() != null) {
        accountingGroup.setGrandParentId(ag.getGrandParentId());
      } else {
        accountingGroup.setGrandParentId(ag.getId());
      }
    }
    if (accountingGroup.getId() == null) {
      insert(main, accountingGroup);
    } else {
      updateByPk(main, accountingGroup);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingGroup
   */
  public static void clone(Main main, AccountingGroup accountingGroup) {
    accountingGroup.setId(null); //Set to null for insert
    insertOrUpdate(main, accountingGroup);
  }

  /**
   * Delete AccountingGroup.
   *
   * @param main
   * @param accountingGroup
   */
  public static final void deleteByPk(Main main, AccountingGroup accountingGroup) {
    deleteAble(main, accountingGroup); //Validation
    AppService.delete(main, AccountingGroup.class, accountingGroup.getId());
  }

  /**
   * Delete Array of AccountingGroup.
   *
   * @param main
   * @param accountingGroup
   */
  public static final void deleteByPkArray(Main main, AccountingGroup[] accountingGroup) {
    for (AccountingGroup e : accountingGroup) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param accountingGroup
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingGroup accountingGroup) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingGroup
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingGroup accountingGroup) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_group where upper(title)=?", new Object[]{StringUtil.toUpperCase(accountingGroup.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingGroup
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingGroup accountingGroup) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_group where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingGroup.getTitle()), accountingGroup.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  public static List<AccountingGroup> accountingGroupAutoAll(Main main, String filter) {
    if (StringUtil.isEmpty(filter)) {
      return AppService.list(main, AccountingGroup.class, "select id,title from fin_accounting_group order by title asc", new Object[]{});
    } else {
      return AppService.list(main, AccountingGroup.class, "select id,title from fin_accounting_group where UPPER(title) like ?  order by title asc",
              new Object[]{"%" + filter.toUpperCase() + "%"});

    }
  }
}
