/*
 * @(#)AccountingTransactionDetailItemService.java	1.0 Wed Aug 23 18:04:47 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.common.FilterObjects;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.AccountingTransactionDetailItem;
import wawo.entity.core.UserMessageException;

/**
 * AccountingTransactionDetailItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Aug 23 18:04:47 IST 2017
 */
public abstract class AccountingTransactionDetailItemService {

  public static final String COLOR_GREEN = "green";
  public static final String COLOR_BLUE = "blue";
  public static final String COLOR_BLACK = "#333";
  public static final String HEADER_VALUE_PAYABLE = "Payable";
  public static final String HEADER_VALUE_RECEIVABLE = "Receivable";
  public static final String HEADER_VALUE_DOC = "Document";

  /**
   * AccountingTransactionDetailItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingTransactionDetailItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_transaction_detail_item", AccountingTransactionDetailItem.class, main);
    sql.main("select fin_accounting_transaction_detail_item.id,fin_accounting_transaction_detail_item.parent_id,fin_accounting_transaction_detail_item.accounting_transaction_detail_id,fin_accounting_transaction_detail_item.document_type_id,fin_accounting_transaction_detail_item.document_number,fin_accounting_transaction_detail_item.document_date,fin_accounting_transaction_detail_item.refer_number,fin_accounting_transaction_detail_item.goods_amount,fin_accounting_transaction_detail_item.discount_amount,fin_accounting_transaction_detail_item.tax_amount,fin_accounting_transaction_detail_item.net_amount,fin_accounting_transaction_detail_item.balance_amount,fin_accounting_transaction_detail_item.record_type,fin_accounting_transaction_detail_item.status,fin_accounting_transaction_detail_item.processed_at,fin_accounting_transaction_detail_item.processed_by,fin_accounting_transaction_detail_item.created_by,fin_accounting_transaction_detail_item.modified_by,fin_accounting_transaction_detail_item.created_at,fin_accounting_transaction_detail_item.modified_at from fin_accounting_transaction_detail_item fin_accounting_transaction_detail_item "); //Main query
    sql.count("select count(fin_accounting_transaction_detail_item.id) as total from fin_accounting_transaction_detail_item fin_accounting_transaction_detail_item "); //Count query
    sql.join("left outer join fin_accounting_transaction_detail_item fin_accounting_transaction_detail_itemparent_id on (fin_accounting_transaction_detail_itemparent_id.id = fin_accounting_transaction_detail_item.parent_id) left outer join fin_accounting_transaction_detail fin_accounting_transaction_detail_itemaccounting_transaction_detail_id on (fin_accounting_transaction_detail_itemaccounting_transaction_detail_id.id = fin_accounting_transaction_detail_item.accounting_transaction_detail_id) left outer join fin_accounting_doc_type fin_accounting_transaction_detail_itemdocument_type_id on (fin_accounting_transaction_detail_itemdocument_type_id.id = fin_accounting_transaction_detail_item.document_type_id)"); //Join Query

    sql.string(new String[]{"fin_accounting_transaction_detail_itemparent_id.document_number", "fin_accounting_transaction_detail_itemdocument_type_id.title", "fin_accounting_transaction_detail_item.document_number", "fin_accounting_transaction_detail_item.refer_number", "fin_accounting_transaction_detail_item.processed_by", "fin_accounting_transaction_detail_item.created_by", "fin_accounting_transaction_detail_item.modified_by"}); //String search or sort fields
    sql.number(new String[]{"fin_accounting_transaction_detail_item.id", "fin_accounting_transaction_detail_itemaccounting_transaction_detail_id.debit", "fin_accounting_transaction_detail_item.goods_amount", "fin_accounting_transaction_detail_item.discount_amount", "fin_accounting_transaction_detail_item.tax_amount", "fin_accounting_transaction_detail_item.net_amount", "fin_accounting_transaction_detail_item.balance_amount", "fin_accounting_transaction_detail_item.record_type", "fin_accounting_transaction_detail_item.status"}); //Numeric search or sort fields
    sql.date(new String[]{"fin_accounting_transaction_detail_item.document_date", "fin_accounting_transaction_detail_item.processed_at", "fin_accounting_transaction_detail_item.created_at", "fin_accounting_transaction_detail_item.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountingTransactionDetailItem.
   *
   * @param main
   * @return List of AccountingTransactionDetailItem
   */
  public static final List<AccountingTransactionDetailItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountingTransactionDetailItemSqlPaged(main));
  }

  /**
   *
   * @param main
   * @return
   */
  public static final List<AccountingTransactionDetailItem> listPagedByPayable(Main main, int ledgerId, FilterObjects filterObjects, boolean showAll, Integer cheqEntryId) {
    String accountGrpCond = "";
    main.clear();
    main.param(AccountingConstant.RECORD_TYPE_PAYABLE);
    main.param(ledgerId);
    main.param(AccountingConstant.STATUS_NEW);
    main.param(AccountingConstant.STATUS_PARTIAL_PROCESSED);
    if (filterObjects != null && !showAll && filterObjects.getSelectedAccountGroup() != null) {
      accountGrpCond = "and t3.account_group_id=? ";
      main.param(filterObjects.getSelectedAccountGroup().getId());
    }
    if (cheqEntryId != null) {
      accountGrpCond += "and t1.id not in (select accounting_transaction_detail_item from fin_cheque_receipt_transaction_detail where cheque_entry_id not in (?)) ";
      main.param(cheqEntryId);
    }
    return (List<AccountingTransactionDetailItem>) AppService.list(main, AccountingTransactionDetailItem.class,
      "select * from fin_accounting_transaction_detail_item t1, fin_accounting_transaction_detail t2, fin_accounting_transaction t3 "
      + "where t2.id=t1.accounting_transaction_detail_id and t3.id = t2.accounting_transaction_id and t1.record_type=? and t2.accounting_ledger_id=? and (t1.status=? or t1.status=?) "
      + accountGrpCond
      + "order by t2.entry_date, t1.document_type_id",
      main.getParamData().toArray());
  }

  /**
   *
   * @param main
   * @return
   */
  public static final List<AccountingTransactionDetailItem> listPagedByReceivable(Main main, int ledgerId, FilterObjects filterObjects, Integer cheqEntryId) {
    String accountGrpCond = "";
    main.clear();
    main.param(AccountingConstant.RECORD_TYPE_RECEIVABLE);
    main.param(ledgerId);
    main.param(AccountingConstant.STATUS_NEW);
    main.param(AccountingConstant.STATUS_PARTIAL_PROCESSED);

    if (filterObjects != null && filterObjects.getSelectedAccountGroup() != null) {
      accountGrpCond = "and t3.account_group_id=? ";
      main.param(filterObjects.getSelectedAccountGroup().getId());
    }
    if (cheqEntryId != null) {
      accountGrpCond += "and t1.id not in (select accounting_transaction_detail_item from fin_cheque_receipt_transaction_detail where cheque_entry_id not in (?)) ";
      main.param(cheqEntryId);
    }
    return (List<AccountingTransactionDetailItem>) AppService.list(main, AccountingTransactionDetailItem.class,
      "select * from fin_accounting_transaction_detail_item t1, fin_accounting_transaction_detail t2, fin_accounting_transaction t3 "
      + "where t2.id=t1.accounting_transaction_detail_id and t3.id = t2.accounting_transaction_id and t1.record_type = ? and t2.accounting_ledger_id = ? and (t1.status = ? or t1.status = ?) "
      + accountGrpCond
      + "order by t2.entry_date, t1.document_type_id",
      main.getParamData().toArray());
  }

  public static final List<AccountingTransactionDetailItem> selectByTransactionDetail(Main main, int accountingTransactionDetailId) {
    return (List<AccountingTransactionDetailItem>) AppService.list(main, AccountingTransactionDetailItem.class, "select * from fin_accounting_transaction_detail_item where accounting_transaction_detail_id = ?", new Object[]{accountingTransactionDetailId});
  }

  public static final List<AccountingTransactionDetailItem> selectByTransactionDetailReceivable(Main main, int accountingTransactionDetailId) {
    return (List<AccountingTransactionDetailItem>) AppService.list(main, AccountingTransactionDetailItem.class, "select * from fin_accounting_transaction_detail_item where accounting_transaction_detail_id = ? and status=? and record_type=?", new Object[]{accountingTransactionDetailId, AccountingConstant.STATUS_PARTIAL_PROCESSED, AccountingConstant.RECORD_TYPE_RECEIVABLE});
  }

  public static final List<AccountingTransactionDetailItem> selectByTransactionDetailPayment(Main main, int accountingTransactionDetailId) {
    return (List<AccountingTransactionDetailItem>) AppService.list(main, AccountingTransactionDetailItem.class, "select * from fin_accounting_transaction_detail_item where accounting_transaction_detail_id = ? and parent_id is null", new Object[]{accountingTransactionDetailId});
  }
//  /**
//   * Return list of AccountingTransactionDetailItem based on condition
//   * @param main
//   * @return List<AccountingTransactionDetailItem>
//   */
//  public static final List<AccountingTransactionDetailItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountingTransactionDetailItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select AccountingTransactionDetailItem by key.
   *
   * @param main
   * @param accountingTransactionDetailItem
   * @return AccountingTransactionDetailItem
   */
  public static final AccountingTransactionDetailItem selectByPk(Main main, AccountingTransactionDetailItem accountingTransactionDetailItem) {
    return (AccountingTransactionDetailItem) AppService.find(main, AccountingTransactionDetailItem.class, accountingTransactionDetailItem.getId());
  }

  /**
   * Insert AccountingTransactionDetailItem.
   *
   * @param main
   * @param accountingTransactionDetailItem
   */
  private static final void insert(Main main, AccountingTransactionDetailItem accountingTransactionDetailItem) {
    insertAble(main, accountingTransactionDetailItem);  //Validating
    AppService.insert(main, accountingTransactionDetailItem);

  }

  /**
   * Update AccountingTransactionDetailItem by key.
   *
   * @param main
   * @param accountingTransactionDetailItem
   * @return AccountingTransactionDetailItem
   */
  private static final AccountingTransactionDetailItem updateByPk(Main main, AccountingTransactionDetailItem accountingTransactionDetailItem) {
    updateAble(main, accountingTransactionDetailItem); //Validating
    return (AccountingTransactionDetailItem) AppService.update(main, accountingTransactionDetailItem);
  }

  /**
   * Insert or update AccountingTransactionDetailItem
   *
   * @param main
   * @param accountingTransactionDetailItem
   */
  public static void insertOrUpdate(Main main, AccountingTransactionDetailItem accountingTransactionDetailItem) {
    if (accountingTransactionDetailItem.getId() == null) {
      insert(main, accountingTransactionDetailItem);
    } else {
      updateByPk(main, accountingTransactionDetailItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingTransactionDetailItem
   */
  public static void clone(Main main, AccountingTransactionDetailItem accountingTransactionDetailItem) {
    accountingTransactionDetailItem.setId(null); //Set to null for insert
    insert(main, accountingTransactionDetailItem);
  }

  /**
   * Delete AccountingTransactionDetailItem.
   *
   * @param main
   * @param accountingTransactionDetailItem
   */
  public static final void deleteByPk(Main main, AccountingTransactionDetailItem accountingTransactionDetailItem) {
    deleteAble(main, accountingTransactionDetailItem); //Validation
    AppService.delete(main, AccountingTransactionDetailItem.class, accountingTransactionDetailItem.getId());
  }

  /**
   * Delete Array of AccountingTransactionDetailItem.
   *
   * @param main
   * @param accountingTransactionDetailItem
   */
  public static final void deleteByPkArray(Main main, AccountingTransactionDetailItem[] accountingTransactionDetailItem) {
    for (AccountingTransactionDetailItem e : accountingTransactionDetailItem) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param accountingTransactionDetailItem
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingTransactionDetailItem accountingTransactionDetailItem) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingTransactionDetailItem
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingTransactionDetailItem accountingTransactionDetailItem) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_transaction_detail_item where upper(document_number)=?", new Object[]{StringUtil.toUpperCase(accountingTransactionDetailItem.getDocumentNumber())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingTransactionDetailItem
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingTransactionDetailItem accountingTransactionDetailItem) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_transaction_detail_item where upper(document_number)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingTransactionDetailItem.getDocumentNumber()), accountingTransactionDetailItem.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
