/*
 * @(#)AccountExpenseDetailService.java	1.0 Mon Nov 27 13:55:47 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service.delete;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.AccountExpenseDetail;

/**
 * AccountExpenseDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Nov 27 13:55:47 IST 2017
 */
public abstract class AccountExpenseDetailService {

  /**
   * AccountExpenseDetail paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountExpenseDetailSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_account_expense_detail", AccountExpenseDetail.class, main);
    sql.main("select t1.id,t1.account_expense_id,t1.vendor_ledger_id,t1.expense_ledger_id,t1.bank_cash_ledger_id,t1.consignment_receipt_no,t1.consignment_delivery_no,t1.payment_type,t1.payment_document_no,t1.payment_date,t1.payment_value,t1.payment_doc_value,t1.note,t1.is_reimbursable,t1.is_tax,t1.tax_amount,t1.created_at,t1.modified_at from scm_account_expense_detail scm_account_expense_detail "); //Main query
    sql.count("select count(t1.id) as total from scm_account_expense_detail scm_account_expense_detail "); //Count query
    sql.join("left outer join scm_account_expense t2 on (t2.id = t1.account_expense_id) left outer join fin_accounting_ledger t3 on (t3.id = t1.vendor_ledger_id) left outer join fin_accounting_ledger t4 on (t4.id = t1.expense_ledger_id) left outer join fin_accounting_ledger t5 on (t5.id = t1.bank_cash_ledger_id) left outer join fin_accounting_doc_type t6 on (t6.id = t1.payment_type)"); //Join Query

    sql.string(new String[]{"t1.consignment_receipt_no", "t1.consignment_delivery_no", "t6.title", "t1.payment_document_no", "t1.note", "t1.is_reimbursable", "t1.is_tax"}); //String search or sort fields
    sql.number(new String[]{"t1.id", "t2.status", "t3.entity_id", "t4.entity_id", "t5.entity_id", "t1.payment_value", "t1.payment_doc_value", "t1.tax_amount"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.payment_date", "t1.created_at", "t1.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountExpenseDetail.
   *
   * @param main
   * @return List of AccountExpenseDetail
   */
  public static final List<AccountExpenseDetail> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountExpenseDetailSqlPaged(main));
  }

//  /**
//   * Return list of AccountExpenseDetail based on condition
//   * @param main
//   * @return List<AccountExpenseDetail>
//   */
//  public static final List<AccountExpenseDetail> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountExpenseDetailSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountExpenseDetail by key.
   *
   * @param main
   * @param accountExpenseDetail
   * @return AccountExpenseDetail
   */
  public static final AccountExpenseDetail selectByPk(Main main, AccountExpenseDetail accountExpenseDetail) {
    return (AccountExpenseDetail) AppService.find(main, AccountExpenseDetail.class, accountExpenseDetail.getId());
  }

  /**
   * Insert AccountExpenseDetail.
   *
   * @param main
   * @param accountExpenseDetail
   */
  public static final void insert(Main main, AccountExpenseDetail accountExpenseDetail) {
    AppService.insert(main, accountExpenseDetail);
  }

  /**
   * Update AccountExpenseDetail by key.
   *
   * @param main
   * @param accountExpenseDetail
   * @return AccountExpenseDetail
   */
  public static final AccountExpenseDetail updateByPk(Main main, AccountExpenseDetail accountExpenseDetail) {
    return (AccountExpenseDetail) AppService.update(main, accountExpenseDetail);
  }

  /**
   * Insert or update AccountExpenseDetail
   *
   * @param main
   * @param accountExpenseDetail
   */
  public static void insertOrUpdate(Main main, AccountExpenseDetail accountExpenseDetail) {
    if (accountExpenseDetail.getId() == null) {
      insert(main, accountExpenseDetail);
    } else {
      updateByPk(main, accountExpenseDetail);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountExpenseDetail
   */
  public static void clone(Main main, AccountExpenseDetail accountExpenseDetail) {
    accountExpenseDetail.setId(null); //Set to null for insert
    insert(main, accountExpenseDetail);
  }

  /**
   * Delete AccountExpenseDetail.
   *
   * @param main
   * @param accountExpenseDetail
   */
  public static final void deleteByPk(Main main, AccountExpenseDetail accountExpenseDetail) {
    AppService.delete(main, AccountExpenseDetail.class, accountExpenseDetail.getId());
  }

  /**
   * Delete Array of AccountExpenseDetail.
   *
   * @param main
   * @param accountExpenseDetail
   */
  public static final void deleteByPkArray(Main main, AccountExpenseDetail[] accountExpenseDetail) {
    for (AccountExpenseDetail e : accountExpenseDetail) {
      deleteByPk(main, e);
    }
  }

  public static AccountExpenseDetail selectByAccountingTransaction(Main main, Integer id) {

    return (AccountExpenseDetail) AppService.single(main, AccountExpenseDetail.class, "SELECT * FROM scm_account_expense_detail WHERE transaction_id = ? ", new Object[]{id});

  }
}
