/*
 * @(#)AccountingTransactionSettlementService.java	1.0 Wed Aug 23 18:04:47 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.fin.domain.AccountingTransactionDetail;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.AccountingTransactionSettlement;
import wawo.entity.core.UserMessageException;

/**
 * AccountingTransactionSettlementService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Aug 23 18:04:47 IST 2017
 */
public abstract class AccountingTransactionSettlementService {

  /**
   * AccountingTransactionSettlement paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingTransactionSettlementSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_transaction_settlement", AccountingTransactionSettlement.class, main);
    sql.main("select fin_accounting_transaction_settlement.id,fin_accounting_transaction_settlement.transaction_detail_item_id,fin_accounting_transaction_settlement.adjusted_transaction_detail_item_id,fin_accounting_transaction_settlement.settled_amount,fin_accounting_transaction_settlement.created_by,fin_accounting_transaction_settlement.modified_by,fin_accounting_transaction_settlement.created_at,fin_accounting_transaction_settlement.modified_at from fin_accounting_transaction_settlement fin_accounting_transaction_settlement "); //Main query
    sql.count("select count(fin_accounting_transaction_settlement.id) as total from fin_accounting_transaction_settlement fin_accounting_transaction_settlement "); //Count query
    sql.join("left outer join fin_accounting_transaction_detail_item fin_accounting_transaction_settlementtransaction_detail_item_id on (fin_accounting_transaction_settlementtransaction_detail_item_id.id = fin_accounting_transaction_settlement.transaction_detail_item_id) left outer join fin_accounting_transaction_detail_item fin_accounting_transaction_settlementadjusted_transaction_detail_item_id on (fin_accounting_transaction_settlementadjusted_transaction_detail_item_id.id = fin_accounting_transaction_settlement.adjusted_transaction_detail_item_id)  left outer join fin_accounting_transaction_detail fin_accounting_transaction_detail_id on (fin_accounting_transaction_detail_id.id = fin_accounting_transaction_settlement.transaction_detail_id)"); //Join Query

    sql.string(new String[]{"fin_accounting_transaction_settlementtransaction_detail_item_id.document_number", "fin_accounting_transaction_settlementadjusted_transaction_detail_item_id.document_number", "fin_accounting_transaction_settlement.created_by", "fin_accounting_transaction_settlement.modified_by"}); //String search or sort fields
    sql.number(new String[]{"fin_accounting_transaction_settlement.id", "fin_accounting_transaction_settlement.settled_amount"}); //Numeric search or sort fields
    sql.date(new String[]{"fin_accounting_transaction_settlement.created_at", "fin_accounting_transaction_settlement.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountingTransactionSettlement.
   *
   * @param main
   * @return List of AccountingTransactionSettlement
   */
  public static final List<AccountingTransactionSettlement> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountingTransactionSettlementSqlPaged(main));
  }

//  /**
//   * Return list of AccountingTransactionSettlement based on condition
//   * @param main
//   * @return List<AccountingTransactionSettlement>
//   */
//  public static final List<AccountingTransactionSettlement> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountingTransactionSettlementSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountingTransactionSettlement by key.
   *
   * @param main
   * @param accountingTransactionSettlement
   * @return AccountingTransactionSettlement
   */
  public static final AccountingTransactionSettlement selectByPk(Main main, AccountingTransactionSettlement accountingTransactionSettlement) {
    return (AccountingTransactionSettlement) AppService.find(main, AccountingTransactionSettlement.class, accountingTransactionSettlement.getId());
  }

  public static final List<AccountingTransactionSettlement> selectByTransactionDetail(Main main, int transactionDetailItemId) {
    return AppService.list(main, AccountingTransactionSettlement.class, "select * from fin_accounting_transaction_settlement where transaction_detail_item_id = ?", new Object[]{transactionDetailItemId});
  }

  public static final List<AccountingTransactionSettlement> selectDetailItemUsedIn(Main main, int transactionDetailItemId) {
    return AppService.list(main, AccountingTransactionSettlement.class, "select * from fin_accounting_transaction_settlement where adjusted_transaction_detail_item_id in (select id from fin_accounting_transaction_detail_item where parent_id =?)", new Object[]{transactionDetailItemId});
  }

  public static final List<AccountingTransactionSettlement> selectByTransactionDetailPayment(Main main, int transactionDetailItemId) {
    return AppService.list(main, AccountingTransactionSettlement.class, "select * from fin_accounting_transaction_settlement where adjusted_transaction_detail_item_id = ?", new Object[]{transactionDetailItemId});
  }

  public static final List<AccountingTransactionSettlement> selectByTransactionDetailPaymentAndAdjusted(Main main, int transactionDetailItemId, AccountingTransactionDetail detail) {
    return AppService.list(main, AccountingTransactionSettlement.class, "select * from fin_accounting_transaction_settlement where adjusted_transaction_detail_item_id = ? or (transaction_detail_id = ? or adjusted_transaction_detail_id = ?)", new Object[]{transactionDetailItemId, detail.getId(), detail.getId()});
  }

  public static final List<AccountingTransactionSettlement> selectAdjustedOnly(Main main, int transactionDetailItemId, AccountingTransactionDetail detail) {
    return AppService.list(main, AccountingTransactionSettlement.class, "select * from fin_accounting_transaction_settlement where adjusted_transaction_detail_item_id != ? and (transaction_detail_id = ? or adjusted_transaction_detail_id = ?)", new Object[]{transactionDetailItemId, detail.getId(), detail.getId()});
  }

  public static final List<AccountingTransactionSettlement> selectByTransactionDetailReciept(Main main, int transactionDetailItemId) {
    return AppService.list(main, AccountingTransactionSettlement.class, "select * from fin_accounting_transaction_settlement where transaction_detail_item_id = ?", new Object[]{transactionDetailItemId});

  }

  /**
   * Insert AccountingTransactionSettlement.
   *
   * @param main
   * @param accountingTransactionSettlement
   */
  public static final void insert(Main main, AccountingTransactionSettlement accountingTransactionSettlement) {
    insertAble(main, accountingTransactionSettlement);  //Validating
    AppService.insert(main, accountingTransactionSettlement);

  }

  /**
   * Update AccountingTransactionSettlement by key.
   *
   * @param main
   * @param accountingTransactionSettlement
   * @return AccountingTransactionSettlement
   */
  public static final AccountingTransactionSettlement updateByPk(Main main, AccountingTransactionSettlement accountingTransactionSettlement) {
    updateAble(main, accountingTransactionSettlement); //Validating
    return (AccountingTransactionSettlement) AppService.update(main, accountingTransactionSettlement);
  }

  /**
   * Insert or update AccountingTransactionSettlement
   *
   * @param main
   * @param accountingTransactionSettlement
   */
  public static void insertOrUpdate(Main main, AccountingTransactionSettlement accountingTransactionSettlement) {
    if (accountingTransactionSettlement.getId() == null) {
      insert(main, accountingTransactionSettlement);
    } else {
      updateByPk(main, accountingTransactionSettlement);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingTransactionSettlement
   */
  public static void clone(Main main, AccountingTransactionSettlement accountingTransactionSettlement) {
    accountingTransactionSettlement.setId(null); //Set to null for insert
    insert(main, accountingTransactionSettlement);
  }

  /**
   * Delete AccountingTransactionSettlement.
   *
   * @param main
   * @param accountingTransactionSettlement
   */
  public static final void deleteByPk(Main main, AccountingTransactionSettlement accountingTransactionSettlement) {
    deleteAble(main, accountingTransactionSettlement); //Validation
    AppService.delete(main, AccountingTransactionSettlement.class, accountingTransactionSettlement.getId());
  }

  /**
   * Delete Array of AccountingTransactionSettlement.
   *
   * @param main
   * @param accountingTransactionSettlement
   */
  public static final void deleteByPkArray(Main main, AccountingTransactionSettlement[] accountingTransactionSettlement) {
    for (AccountingTransactionSettlement e : accountingTransactionSettlement) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param accountingTransactionSettlement
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingTransactionSettlement accountingTransactionSettlement) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingTransactionSettlement
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingTransactionSettlement accountingTransactionSettlement) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_transaction_settlement where upper(settled_amount)=?", new Object[]{StringUtil.toUpperCase(accountingTransactionSettlement.getSettledAmount())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingTransactionSettlement
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingTransactionSettlement accountingTransactionSettlement) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_transaction_settlement where upper(settled_amount)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingTransactionSettlement.getSettledAmount()), accountingTransactionSettlement.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
