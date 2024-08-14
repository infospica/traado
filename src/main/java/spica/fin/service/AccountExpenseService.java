/*
 * @(#)AccountExpenseService.java	1.0 Mon Nov 27 13:55:47 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.scm.domain.Account;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.AccountExpense;
import spica.fin.domain.AccountExpenseDetail;
import spica.fin.domain.AccountExpenseTransaction;
import spica.scm.domain.AccountExpenseAccounts;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupDetail;
import spica.scm.domain.Company;
import spica.scm.domain.Consignment;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 * AccountExpenseService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Nov 27 13:55:47 IST 2017
 */
public abstract class AccountExpenseService {

  /**
   * AccountExpense paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountExpenseSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_account_expense", AccountExpense.class, main);
    sql.main("select t1.id,t1.company_id,t1.account_id,t1.consignment_purchase_id,t1.account_group_id,t1.consignment_sales_id,t1.status,t1.entry_date,t1.created_at,t1.modified_at,t1.expense_type,t1.reimbursable_expense_amount,t1.non_reimbursable_expense_amount,t1.narration from scm_account_expense t1 "); //Main query
    sql.count("select count(t1.id) as total from scm_account_expense t1 "); //Count query
    sql.join("left outer join scm_company t2 on (t2.id = t1.company_id) left outer join scm_account t3 on (t3.id = t1.account_id) left outer join scm_consignment t4 on (t4.id = t1.consignment_purchase_id) left outer join scm_account_group t5 on (t5.id = t1.account_group_id) left outer join scm_consignment t6 on (t6.id = t1.consignment_sales_id)"); //Join Query

    sql.string(new String[]{"t2.company_name", "t3.account_code", "t4.consignment_no", "t5.group_name", "t6.consignment_no", "t1.narration"}); //String search or sort fields
    sql.number(new String[]{"t1.id", "t1.status", "t1.expense_type", "t1.non_reimbursable_expense_amount", "t1.reimbursable_expense_amount"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.entry_date", "t1.created_at", "t1.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountExpense.
   *
   * @param main
   * @return List of AccountExpense
   */
  public static final List<AccountExpense> listPaged(Main main, Company company, int type) {
    SqlPage sql = getAccountExpenseSqlPaged(main);
    sql.cond("where t1.company_id = ?");
    if (type == 1) {
      sql.cond("and t1.account_group_id is null");
    } else if (type == 2) {
      sql.cond("and t1.account_id is null");
    }
    main.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<AccountExpense> listPagedByAccount(MainView main, Company company, Account account) {
    SqlPage sql = getAccountExpenseSqlPaged(main);
    sql.cond("where t1.company_id=? and t1.account_id = ?");
    main.param(company.getId());
    main.param(account.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<AccountExpense> listPagedByAccountGroup(MainView main, Company company, AccountGroup accountGroup) {
    SqlPage sql = getAccountExpenseSqlPaged(main);
    sql.cond("where t1.company_id = ? and t1.account_group_id = ?");
    main.param(company.getId());
    main.param(accountGroup.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of AccountExpense based on condition
//   * @param main
//   * @return List<AccountExpense>
//   */
//  public static final List<AccountExpense> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountExpenseSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountExpense by key.
   *
   * @param main
   * @param accountExpense
   * @return AccountExpense
   */
  public static final AccountExpense selectByPk(Main main, AccountExpense accountExpense) {
    return (AccountExpense) AppService.find(main, AccountExpense.class, accountExpense.getId());
  }

  /**
   * Insert AccountExpense.
   *
   * @param main
   * @param accountExpense
   */
  public static final void insert(Main main, AccountExpense accountExpense) {
    AppService.insert(main, accountExpense);
  }

  /**
   * Update AccountExpense by key.
   *
   * @param main
   * @param accountExpense
   * @return AccountExpense
   */
  public static final AccountExpense updateByPk(Main main, AccountExpense accountExpense) {
    return (AccountExpense) AppService.update(main, accountExpense);
  }

  /**
   * Insert or update AccountExpense
   *
   * @param main
   * @param accountExpense
   */
  public static void insertOrUpdate(Main main, AccountExpense accountExpense) {
    if (accountExpense.getId() == null) {
      insert(main, accountExpense);
    } else {
      updateByPk(main, accountExpense);
    }
    Double amount = 0.0;
    if (accountExpense.isConfirmed() && accountExpense.getAccountId() == null && accountExpense.getAccountGroupId() != null) {
      String selectSql = "select * from scm_account_group_detail where account_group_id = ?";
      List<AccountGroupDetail> list = AppService.list(main, AccountGroupDetail.class, selectSql, new Object[]{accountExpense.getAccountGroupId().getId()});
      for (AccountGroupDetail agd : list) {
        amount = calculateSalesAmountByAccount(main, accountExpense.getConsignmentSalesId(), agd.getAccountId());
        insertExpenseAccount(main, accountExpense, agd.getAccountId(), amount);
      }
    } else if (accountExpense.isConfirmed() && accountExpense.getAccountId() != null) {
      for (AccountExpenseDetail exp : accountExpense.getAccountExpenseDetailList()) {
        if (exp.getPaymentValue() != null) {
          amount += exp.getPaymentValue();
        }
      }
      insertExpenseAccount(main, accountExpense, accountExpense.getAccountId(), amount);
    }
  }

  private static void insertExpenseAccount(Main main, AccountExpense accountExpense, Account account, Double amount) {
    AccountExpenseAccounts ea = new AccountExpenseAccounts();
    ea.setAccountExpenseId(accountExpense);
    ea.setAccountId(account);
    ea.setAmount(amount); //FIXME presision issue with long to double
    AppService.insert(main, ea);
  }

  private static Double calculateSalesAmountByAccount(Main main, Consignment commodity, Account account) {
    String selectSql = "select sum(value_sale) from scm_sales_invoice_item t1, scm_consignment_commodity t2, scm_consignment t3\n"
            + "where t3.id=t2.consignment_id \n"
            + "and t2.sales_invoice_id=t1.sales_invoice_id\n"
            + "and t3.id=? \n"
            + "and t1.account_id=? group by t2.sales_invoice_id";
    return AppService.countDouble(main, selectSql, new Object[]{commodity.getId(), account.getId()});
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountExpense
   */
  public static void clone(Main main, AccountExpense accountExpense) {
    accountExpense.setId(null); //Set to null for insert
    insert(main, accountExpense);
  }

  /**
   * Delete AccountExpense.
   *
   * @param main
   * @param accountExpense
   */
  public static final void deleteByPk(Main main, AccountExpense accountExpense) {
    if (accountExpense.isDraft()) {
      AppService.deleteSql(main, AccountExpenseDetail.class, "delete from scm_account_expense_detail where account_expense_id=?", new Object[]{accountExpense.getId()});
      AppService.deleteSql(main, AccountExpenseTransaction.class, "delete from scm_account_expense_transaction where expense_id=?", new Object[]{accountExpense.getId()});
      AppService.deleteSql(main, AccountExpenseAccounts.class, "delete from scm_account_expense_accounts where account_expense_id=?", new Object[]{accountExpense.getId()});
      AppService.delete(main, AccountExpense.class, accountExpense.getId());
    } else {
      Jsf.error("error.delete.confirm");
    }
  }

  /**
   * Delete Array of AccountExpense.
   *
   * @param main
   * @param accountExpense
   */
  public static final void deleteByPkArray(Main main, AccountExpense[] accountExpense) {
    for (AccountExpense e : accountExpense) {
      deleteByPk(main, e);
    }
  }

  public static final AccountExpense selectByConsignment(Main main, Consignment consignment) {
    if (consignment.getAccountGroupId() != null) {
      return (AccountExpense) AppService.single(main, AccountExpense.class, "select * from scm_account_expense where consignment_sales_id=? and account_group_id=?", new Object[]{consignment.getId(), consignment.getAccountGroupId().getId()});
    }
    return (AccountExpense) AppService.single(main, AccountExpense.class, "select * from scm_account_expense where consignment_purchase_id=? and account_id=?", new Object[]{consignment.getId(), consignment.getAccountId().getId()});
  }

}
