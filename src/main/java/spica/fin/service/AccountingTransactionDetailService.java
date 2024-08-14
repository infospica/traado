/*
 * @(#)AccountingTransactionDetailService.java	1.0 Fri Apr 28 12:24:49 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.Date;
import java.util.List;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.domain.AccountingTransactionSettlement;
import spica.scm.domain.Company;
import spica.fin.common.FilterObjects;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;

/**
 * AccountingTransactionDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 28 12:24:49 IST 2017
 */
public abstract class AccountingTransactionDetailService {

  /**
   * AccountingTransactionDetail paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingTransactionDetailSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_transaction_detail", AccountingTransactionDetail.class, main);
    sql.main("select t1.id,t1.accounting_transaction_id,t1.accounting_ledger_id,t1.debit,t1.credit,t1.narration,t1.reference_document_no,t1.reference_document_type_id,t1.reference_document_copy_path,t1.instrument_date,t2.entry_date, t1.bank_date,t1.created_by,t1.modified_by,t1.created_at,t1.modified_at from fin_accounting_transaction_detail t1 "); //Main query
    sql.count("select count(t1.id) as total from fin_accounting_transaction_detail t1 "); //Count query
    sql.join("left outer join fin_accounting_transaction t2 on (t2.id = t1.accounting_transaction_id) left outer join fin_accounting_ledger t3 on (t3.id = t1.accounting_ledger_id) left outer join fin_accounting_doc_type t4 on (t4.id = t1.reference_document_type_id)"); //Join Query
    sql.orderBy("t1.accounting_transaction_id desc, t1.id asc, t1.debit desc,t1.credit desc ");
    sql.string(new String[]{"t2.document_number", "t1.narration", "t1.reference_document_no", "t4.title", "t1.reference_document_copy_path", "t1.created_by", "t1.modified_by"}); //String search or sort fields
    sql.number(new String[]{"t1.id", "t3.entity_id", "t1.debit", "t1.credit"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.instrument_date", "t1.bank_date", "t1.created_at", "t1.modified_at"});  //Date search or sort fields
    sql.orderBy("t2.entry_date desc");
    return sql;
  }

  private static SqlPage getAccountingTransactionDetailDebitSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_transaction_detail", AccountingTransactionDetail.class, main);

    sql.main("select * from (select distinct on (t1.accounting_transaction_id) accounting_transaction_id,t1.id, t2.voucher_type_id, t3.title title, sum(t1.debit) OVER (PARTITION BY t1.accounting_transaction_id) debit, t1.accounting_ledger_id,t2.entry_date,t3.title,t3.company_id,t2.narration from fin_accounting_transaction_detail t1"); //Main query
    sql.count("select count(tab.id) as total from fin_accounting_transaction tab "); //Count query
    sql.join("left outer join fin_accounting_transaction t2 on (t2.id = t1.accounting_transaction_id) left outer join fin_accounting_ledger t3 on (t3.id = t1.accounting_ledger_id) left outer join fin_accounting_doc_type t4 on (t4.id = t1.reference_document_type_id)"); //Join Query
    sql.join(" order by t1.accounting_transaction_id desc,t1.id asc) t2");
    //sql.orderBy("t2.entry_date desc, t2.accounting_transaction_id, t2.id");
    sql.string(new String[]{"t2.document_number", "t2.title"}); //String search or sort fields
    sql.number(new String[]{"t2.debit"}); //Numeric search or sort fields
    sql.date(new String[]{"t2.entry_date"});  //Date search or sort fields
    sql.orderBy("t2.entry_date desc");
    return sql;
  }

  /**
   * Return List of AccountingTransactionDetail.
   *
   * @param main
   * @return List of AccountingTransactionDetail
   */
  public static final List<AccountingTransactionDetail> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountingTransactionDetailSqlPaged(main));
  }

  public static final List<AccountingTransactionDetail> listAll(Main main, FilterObjects filterObjects, Company company) {
    SqlPage sql;
    if (filterObjects.getSelectedSummary() == 1 || filterObjects.getSelectedSummary() == 0) {
      sql = getAccountingTransactionDetailDebitSqlPaged(main);
      sql.cond("where t2.company_id=? ");
      main.param(company.getId());
    } else {
      sql = getAccountingTransactionDetailSqlPaged(main);
      sql.cond("where t3.company_id=? ");
      main.param(company.getId());
    }
    sql.cond("and to_char(t2.entry_date, 'yyyy-mm-dd') between to_char(?::date, 'yyyy-mm-dd') and to_char(?::date, 'yyyy-mm-dd')  ");
    main.param(filterObjects.getFromDate());
    main.param(filterObjects.getToDate());

    if (filterObjects.getSelectedVoucherType() != null) {
      sql.cond("and t2.voucher_type_id=? ");
      main.param(filterObjects.getSelectedVoucherType());
    }
    return AppService.listAllJpa(main, sql);
  }

  public static final AccountingTransaction sumUpTotal(Main main, FilterObjects filterObjects, AccountingTransaction at) {
    Object[] param;
    String sql = "select sum(t1.debit::float), sum(t1.credit::float) from fin_accounting_transaction_detail t1,fin_accounting_transaction t2,fin_accounting_ledger t3 where to_char(t2.entry_date, 'yyyy-mm-dd') between to_char(?::date, 'yyyy-mm-dd') and to_char(?::date, 'yyyy-mm-dd') and t2.id=t1.accounting_transaction_id and t3.id = t1.accounting_ledger_id and t3.company_id=?";
    param = new Object[]{filterObjects.getFromDate(), filterObjects.getToDate(), at.getCompanyId().getId()};
    if (filterObjects.getSelectedVoucherType() != null) {
      sql += " and t2.voucher_type_id=? ";
      param = new Object[]{filterObjects.getFromDate(), filterObjects.getToDate(), at.getCompanyId().getId(), filterObjects.getSelectedVoucherType()};
    }

    Object[] totalSum = (Object[]) AppService.single(main, null, sql, param);

    at.setDebitTotal((Double) totalSum[0]);
    at.setCreditTotal((Double) totalSum[1]);
    return at;
  }

  public static final List<AccountingTransactionDetail> selectByTransaction(Main main, AccountingTransaction accountingTransaction) {
    return AppService.list(main, AccountingTransactionDetail.class, "select * from fin_accounting_transaction_detail where accounting_transaction_id = ? order by id", new Object[]{accountingTransaction.getId()});
  }

//  public static final List<AccountingTransactionDetail> selectByLedgerId(Main main, AccountingTransactionDetail detail) {
//    List<AccountingTransactionDetail> accountingTransactionList = AppService.list(main, AccountingTransactionDetail.class, "select * from fin_accounting_transaction_detail where accounting_ledger_id = ?", new Object[]{detail.getAccountingLedgerId().getId()});
//    return accountingTransactionList;
//  }
  /**
   * Select AccountingTransactionDetail by key.
   *
   * @param main
   * @param accountingTransactionDetail
   * @return AccountingTransactionDetail
   */
  public static final AccountingTransactionDetail selectByPk(Main main, AccountingTransactionDetail accountingTransactionDetail) {
    return (AccountingTransactionDetail) AppService.find(main, AccountingTransactionDetail.class, accountingTransactionDetail.getId());
  }

  /**
   * Insert AccountingTransactionDetail.
   *
   * @param main
   * @param accountingTransactionDetail
   */
  public static final void insert(Main main, AccountingTransactionDetail accountingTransactionDetail) {
    insertAble(main, accountingTransactionDetail);  //Validating
    if (accountingTransactionDetail.getEntryDate() == null) {
      accountingTransactionDetail.setEntryDate(new Date());
    }
    AppService.insert(main, accountingTransactionDetail);

  }

  /**
   * Update AccountingTransactionDetail by key.
   *
   * @param main
   * @param accountingTransactionDetail
   * @return AccountingTransactionDetail
   */
  public static final AccountingTransactionDetail updateByPk(Main main, AccountingTransactionDetail accountingTransactionDetail) {
    updateAble(main, accountingTransactionDetail); //Validating
    return (AccountingTransactionDetail) AppService.update(main, accountingTransactionDetail);
  }

  /**
   * Insert or update AccountingTransactionDetail
   *
   * @param main
   * @param accountingTransactionDetail
   */
  public static void insertOrUpdate(Main main, AccountingTransactionDetail accountingTransactionDetail) {
    if (accountingTransactionDetail.getId() == null) {
      insert(main, accountingTransactionDetail);
    } else {
      updateByPk(main, accountingTransactionDetail);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingTransactionDetail
   */
  public static void clone(Main main, AccountingTransactionDetail accountingTransactionDetail) {
    accountingTransactionDetail.setId(null); //Set to null for insert
    insert(main, accountingTransactionDetail);
  }

  /**
   * Delete AccountingTransactionDetail.
   *
   * @param main
   * @param accountingTransactionDetail
   */
  public static final void deleteByPk(Main main, AccountingTransactionDetail accountingTransactionDetail) {
    deleteAble(main, accountingTransactionDetail); //Validation
    AppService.delete(main, AccountingTransactionDetail.class, accountingTransactionDetail.getId());
  }

  /**
   * Delete Array of AccountingTransactionDetail.
   *
   * @param main
   * @param accountingTransactionDetail
   */
  public static final void deleteByPkArray(Main main, AccountingTransactionDetail[] accountingTransactionDetail) {
    for (AccountingTransactionDetail e : accountingTransactionDetail) {
      deleteByPk(main, e);
    }
  }

  public static final void actionDeleteTransactionExpense(Main main, AccountingTransaction accountingTransaction) {

    List<AccountingTransactionDetail> accountingTransactionDetailList = selectByTransaction(main, accountingTransaction);
    if (accountingTransactionDetailList != null) {
      accountingTransactionDetailList.remove(0);
    }
    for (AccountingTransactionDetail list : accountingTransactionDetailList) {
      AppService.deleteSql(main, AccountingTransactionSettlement.class, "delete from fin_accounting_transaction_settlement where transaction_detail_id=?", new Object[]{list.getId()});
      AppService.deleteSql(main, AccountingTransactionDetailItem.class, "delete from fin_accounting_transaction_detail_item where accounting_transaction_detail_id=?", new Object[]{list.getId()});
      AppService.deleteSql(main, AccountingTransactionDetail.class, "delete from fin_accounting_transaction_detail where id=?", new Object[]{list.getId()});
    }

  }

  /**
   * Validate delete.
   *
   * @param main
   * @param accountingTransactionDetail
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingTransactionDetail accountingTransactionDetail) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingTransactionDetail
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingTransactionDetail accountingTransactionDetail) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_transaction_detail where upper(debit)=?", new Object[]{StringUtil.toUpperCase(accountingTransactionDetail.getDebit())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingTransactionDetail
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingTransactionDetail accountingTransactionDetail) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_transaction_detail where upper(debit)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingTransactionDetail.getDebit()), accountingTransactionDetail.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  public static AccountingTransactionDetail selectTransactionDetailByEntityTypeAndEntityId(Main main, Integer entityTypeId, Integer entityId) {
    String sql = "select t2.* from fin_accounting_transaction t1\n"
            + "left join fin_accounting_transaction_detail t2 on t2.accounting_transaction_id = t1.id\n"
            + "where t1.accounting_entity_type_id = ? and t1.entity_id = ? \n"
            + "AND t1.ledger_debit_id = t2.accounting_ledger_id";
    return (AccountingTransactionDetail) AppService.single(main, AccountingTransactionDetail.class, sql, new Object[]{entityTypeId, entityId});
  }

}
