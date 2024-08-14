/*
 * @(#)AccountExpenseTransactionService.java	1.0 Fri Jan 12 10:05:47 IST 2018 
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
import spica.fin.domain.AccountExpenseTransaction;
import wawo.entity.core.UserMessageException;

/**
 * AccountExpenseTransactionService
 * @author	Spirit 1.2
 * @version	1.0, Fri Jan 12 10:05:47 IST 2018 
 */

public abstract class AccountExpenseTransactionService {  
 
 /**
   * AccountExpenseTransaction paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountExpenseTransactionSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_account_expense_transaction", AccountExpenseTransaction.class, main);
    sql.main("select scm_account_expense_transaction.id,scm_account_expense_transaction.expense_id,scm_account_expense_transaction.account_id,scm_account_expense_transaction.transaction_id,scm_account_expense_transaction.amount from scm_account_expense_transaction scm_account_expense_transaction "); //Main query
    sql.count("select count(scm_account_expense_transaction.id) as total from scm_account_expense_transaction scm_account_expense_transaction "); //Count query
    sql.join("left outer join scm_account_expense_detail scm_account_expense_transactionexpense_id on (scm_account_expense_transactionexpense_id.id = scm_account_expense_transaction.expense_id) left outer join scm_account scm_account_expense_transactionaccount_id on (scm_account_expense_transactionaccount_id.id = scm_account_expense_transaction.account_id) left outer join fin_accounting_transaction scm_account_expense_transactiontransaction_id on (scm_account_expense_transactiontransaction_id.id = scm_account_expense_transaction.transaction_id)"); //Join Query

    sql.string(new String[]{"scm_account_expense_transactionaccount_id.account_code","scm_account_expense_transactiontransaction_id.document_number"}); //String search or sort fields
    sql.number(new String[]{"scm_account_expense_transaction.id","scm_account_expense_transactionexpense_id.status","scm_account_expense_transaction.amount"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of AccountExpenseTransaction.
  * @param main
  * @return List of AccountExpenseTransaction
  */
  public static final List<AccountExpenseTransaction> listPaged(Main main) {
     return AppService.listPagedJpa(main, getAccountExpenseTransactionSqlPaged(main));
  }

//  /**
//   * Return list of AccountExpenseTransaction based on condition
//   * @param main
//   * @return List<AccountExpenseTransaction>
//   */
//  public static final List<AccountExpenseTransaction> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountExpenseTransactionSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select AccountExpenseTransaction by key.
  * @param main
  * @param accountExpenseTransaction
  * @return AccountExpenseTransaction
  */
  public static final AccountExpenseTransaction selectByPk(Main main, AccountExpenseTransaction accountExpenseTransaction) {
    return (AccountExpenseTransaction) AppService.find(main, AccountExpenseTransaction.class, accountExpenseTransaction.getId());
  }

 /**
  * Insert AccountExpenseTransaction.
  * @param main
  * @param accountExpenseTransaction
  */
  public static final void insert(Main main, AccountExpenseTransaction accountExpenseTransaction) {
    AppService.insert(main, accountExpenseTransaction);
  }

 /**
  * Update AccountExpenseTransaction by key.
  * @param main
  * @param accountExpenseTransaction
  * @return AccountExpenseTransaction
  */
  public static final AccountExpenseTransaction updateByPk(Main main, AccountExpenseTransaction accountExpenseTransaction) {
    return (AccountExpenseTransaction) AppService.update(main, accountExpenseTransaction);
  }

  /**
   * Insert or update AccountExpenseTransaction
   *
   * @param main
   * @param accountExpenseTransaction
   */
  public static void insertOrUpdate(Main main, AccountExpenseTransaction accountExpenseTransaction) {
    if (accountExpenseTransaction.getId() == null) {
      insert(main, accountExpenseTransaction);
    }
    else{
      updateByPk(main, accountExpenseTransaction);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountExpenseTransaction
   */
  public static void clone(Main main, AccountExpenseTransaction accountExpenseTransaction) {
    accountExpenseTransaction.setId(null); //Set to null for insert
    insert(main, accountExpenseTransaction);
  }

 /**
  * Delete AccountExpenseTransaction.
  * @param main
  * @param accountExpenseTransaction
  */
  public static final void deleteByPk(Main main, AccountExpenseTransaction accountExpenseTransaction) {
    AppService.delete(main, AccountExpenseTransaction.class, accountExpenseTransaction.getId());
  }
	
 /**
  * Delete Array of AccountExpenseTransaction.
  * @param main
  * @param accountExpenseTransaction
  */
  public static final void deleteByPkArray(Main main, AccountExpenseTransaction[] accountExpenseTransaction) {
    for (AccountExpenseTransaction e : accountExpenseTransaction) {
      deleteByPk(main, e);
    }
  }
}
