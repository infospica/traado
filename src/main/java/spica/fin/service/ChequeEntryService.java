/*
 * @(#)ChequeEntryService.java	1.0 Mon Sep 11 12:32:45 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import spica.constant.AccountingConstant;
import spica.fin.common.FilterObjects;
import spica.fin.domain.AccountingTransactionDetailItem;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.ChequeEntry;
import spica.fin.domain.ChequeReceiptTransactionDetail;
import spica.reports.model.FilterParameters;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import static wawo.app.common.AppService.sql;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * ChequeEntryService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Sep 11 12:32:45 IST 2017
 */
public abstract class ChequeEntryService {

  /**
   * ChequeEntry paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getChequeEntrySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_cheque_entry", ChequeEntry.class, main);
    sql.main("select fin_cheque_entry.id,fin_cheque_entry.cheque_no,fin_cheque_entry.company_id,fin_cheque_entry.cheque_date,fin_cheque_entry.bank_id,fin_cheque_entry.status_id,fin_cheque_entry.created_at,fin_cheque_entry.modified_at,fin_cheque_entry.created_by,fin_cheque_entry.modified_by,fin_cheque_entry.amount,fin_cheque_entry.note,fin_cheque_entry.ledger_id,fin_cheque_entry.entry_date,fin_cheque_entry.account_group_id,fin_cheque_entry.parent_id,fin_cheque_entry.in_or_out from fin_cheque_entry fin_cheque_entry "); //Main query
    sql.count("select count(fin_cheque_entry.id) as total from fin_cheque_entry fin_cheque_entry "); //Count query
    sql.join("left outer join fin_accounting_ledger fin_cheque_entryledger_id on (fin_cheque_entryledger_id.id = fin_cheque_entry.ledger_id) "
            + "left outer join scm_bank fin_cheque_entrybank_id on (fin_cheque_entrybank_id.id = fin_cheque_entry.bank_id) "
            + "left outer join scm_company scm_company on (scm_company.id = fin_cheque_entry.company_id)"); //Join Query

    sql.string(new String[]{"fin_cheque_entry.cheque_no", "fin_cheque_entryledger_id.title", "fin_cheque_entrybank_id.name", "fin_cheque_entry.created_by", "fin_cheque_entry.modified_by"}); //String search or sort fields
    sql.number(new String[]{"fin_cheque_entry.id", "fin_cheque_entry.amount", "fin_cheque_entry.status_id"}); //Numeric search or sort fields
    sql.date(new String[]{"fin_cheque_entry.cheque_date", "fin_cheque_entry.created_at", "fin_cheque_entry.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ChequeEntry.
   *
   * @param main
   * @return List of ChequeEntry
   */
  public static final List<ChequeEntry> listPaged(Main main) {
    return AppService.listPagedJpa(main, getChequeEntrySqlPaged(main));
  }

  public static final List<ChequeEntry> listAllByCustomer(Main main, Company company, Integer ledgerId, Integer customerId, FilterObjects filterObjects) {
    main.clear();
    SqlPage sql = getChequeEntrySqlPaged(main);
    sql.cond("where fin_cheque_entry.status_id=? and fin_cheque_entry.company_id = ? ");
    sql.param(AccountingConstant.BANK_CHEQUE_RECIEVED);
    sql.param(company.getId());
//    if (filterObjects.getChequeReviewType() != null && filterObjects.getChequeReviewType().equals(SystemConstants.CUSTOMER_OUTSTANDING)) {
//      sql.cond(" AND fin_cheque_entry.entry_date::date <= ?::date  ");
//      sql.param(filterObjects.getToDate());
//    } else {
    sql.cond(" AND fin_cheque_entry.cheque_date::date >= ?::date AND fin_cheque_entry.cheque_date::date <= ?::date  ");
    sql.param(company.getCurrentFinancialYear().getStartDate());
    sql.param(company.getCurrentFinancialYear().getEndDate());
    // }
    if (filterObjects != null && filterObjects.getSelectedAccountGroup() != null) {
      sql.cond(" and fin_cheque_entry.account_group_id = ? ");
      sql.param(filterObjects.getSelectedAccountGroup().getId());
    }
    if (customerId != null) {
      // sql.cond(" and fin_cheque_entry.account_group_id in (select account_group_id from scm_sales_account where customer_id=?)");
      sql.cond(" and ledger_id=?");
      sql.param(ledgerId);
    }
    // } else {
    //   return Collections.EMPTY_LIST;
    // }
    return AppService.listAllJpa(main, sql);
  }

  public static final List<ChequeEntry> listPagedByCompany(Main main, Company company, AccountGroup group, Integer inOrOut, List<Integer> chequeStatus, String chequeReviewType) {
    SqlPage sql = getChequeEntrySqlPaged(main);
    sql.cond("where fin_cheque_entry.company_id = ? and in_or_out=?");

    sql.param(company.getId());
    sql.param(inOrOut);

    if (!chequeStatus.isEmpty()) {
      String p = "";
      for (Integer status : chequeStatus) {
        p += "?,";
        sql.param(status);
      }
      p = StringUtils.chop(p);
      sql.cond(" and fin_cheque_entry.status_id in(" + p + ") ");
    }
    if (group != null && group.getId() != null) {
      sql.cond(" and fin_cheque_entry.account_group_id = ?");
      sql.param(group.getId());
    }

    // boolean finyear = true;
    if (!StringUtil.isEmpty(chequeReviewType)) {
      // finyear = false;
      if (chequeReviewType.equals(SystemConstants.CHEQUE_TODAY)) {
        sql.cond(" and fin_cheque_entry.status_id in(5,9) and fin_cheque_entry.cheque_date::date = ?::date");
        sql.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()));
      } else if (chequeReviewType.equals(SystemConstants.CHEQUES_OVERDUE)) {
        sql.cond(" and fin_cheque_entry.status_id in(5,9) and fin_cheque_entry.cheque_date::date < ?::date");
        sql.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()));
      } else if (chequeReviewType.equals(SystemConstants.UNDATED_CHEQUE)) {
        sql.cond(" and fin_cheque_entry.status_id in(5,9) and fin_cheque_entry.cheque_date is null ");
      }// else {
      //finyear = true;
      //}

    } else {
      sql.cond(" and fin_cheque_entry.entry_date::date >= ?::date AND fin_cheque_entry.entry_date::date <= ?::date  ");
      sql.param(company.getCurrentFinancialYear().getStartDate());
      sql.param(company.getCurrentFinancialYear().getEndDate());
    }

    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of ChequeEntry based on condition
//   * @param main
//   * @return List<ChequeEntry>
//   */
//  public static final List<ChequeEntry> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getChequeEntrySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ChequeEntry by key.
   *
   * @param main
   * @param chequeEntry
   * @return ChequeEntry
   */
  public static final ChequeEntry selectByPk(Main main, ChequeEntry chequeEntry) {
    return (ChequeEntry) AppService.find(main, ChequeEntry.class, chequeEntry.getId());
  }

  /**
   * Insert ChequeEntry.
   *
   * @param main
   * @param chequeEntry
   */
  public static final void insert(Main main, ChequeEntry chequeEntry) {
    insertAble(main, chequeEntry);  //Validating
    AppService.insert(main, chequeEntry);

  }

  /**
   * Update ChequeEntry by key.
   *
   * @param main
   * @param chequeEntry
   * @return ChequeEntry
   */
  public static final ChequeEntry updateByPk(Main main, ChequeEntry chequeEntry) {
    updateAble(main, chequeEntry); //Validating
    return (ChequeEntry) AppService.update(main, chequeEntry);
  }

  /**
   * Insert or update ChequeEntry
   *
   * @param main
   * @param chequeEntry
   */
  public static void insertOrUpdate(Main main, ChequeEntry chequeEntry) {
    if (chequeEntry.getId() == null) {
      insert(main, chequeEntry);
    } else {
      if (AccountingConstant.BANK_CHEQUE_CANCELED == chequeEntry.getStatusId() || AccountingConstant.BANK_CHEQUE_RETURNED == chequeEntry.getStatusId()) {
        AppService.deleteSql(main, ChequeReceiptTransactionDetail.class, "delete from fin_cheque_receipt_transaction_detail where cheque_entry_id=?", new Object[]{chequeEntry.getId()});
      }
      updateByPk(main, chequeEntry);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param chequeEntry
   */
  public static void clone(Main main, ChequeEntry chequeEntry) {
    chequeEntry.setId(null); //Set to null for insert
    insert(main, chequeEntry);
  }

  /**
   * Delete ChequeEntry.
   *
   * @param main
   * @param chequeEntry
   */
  public static final void deleteByPk(Main main, ChequeEntry chequeEntry) {
    deleteAble(main, chequeEntry); //Validation
    AppService.delete(main, ChequeEntry.class, chequeEntry.getId());
  }

  /**
   * Delete Array of ChequeEntry.
   *
   * @param main
   * @param chequeEntry
   */
  public static final void deleteByPkArray(Main main, ChequeEntry[] chequeEntry) {
    for (ChequeEntry e : chequeEntry) {
      deleteByPk(main, e);
    }
  }

//  public static final void updateStatus(Main main, ChequeEntry chequeEntry) {
//    String sql = "update fin_cheque_entry set modified_by=?, modified_at = ?, status_id = ? where id = ?";
//    main.clear();
//    main.param(AccountingConstant.BANK_CHEQUE_RECIEVED);
//    main.param(chequeEntry.getId());
//    AppService.updateSql(main, ChequeEntry.class, sql, true);
//  }
  /**
   * Validate delete.
   *
   * @param main
   * @param chequeEntry
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, ChequeEntry chequeEntry) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param chequeEntry
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, ChequeEntry chequeEntry) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_cheque_entry where upper(cheque_no)=?", new Object[]{StringUtil.toUpperCase(chequeEntry.getChequeNo())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param chequeEntry
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, ChequeEntry chequeEntry) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_cheque_entry where upper(cheque_no)=? and id !=?", new Object[]{StringUtil.toUpperCase(chequeEntry.getChequeNo()), chequeEntry.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  public static void insertTransactionDetailItem(Main main, ChequeEntry chequeEntry, List<AccountingTransactionDetailItem> payableList, List<AccountingTransactionDetailItem> receivableList) {
    AppService.deleteSql(main, AccountingTransactionDetailItem.class, "DELETE FROM fin_cheque_receipt_transaction_detail WHERE cheque_entry_id = ? ", new Object[]{chequeEntry.getId()});
    main.em().flush();
    List<AccountingTransactionDetailItem> transactionDetailItemList = new ArrayList<>();
    if (!StringUtil.isEmpty(payableList)) {
      transactionDetailItemList.addAll(payableList);
    }
    if (!StringUtil.isEmpty(receivableList)) {
      transactionDetailItemList.addAll(receivableList);
    }

    for (AccountingTransactionDetailItem item : transactionDetailItemList) {
      ChequeReceiptTransactionDetail detail = new ChequeReceiptTransactionDetail();
      detail.setAccountingTransactionDetailItemId(item);
      detail.setChequeEntryId(chequeEntry);
      detail.setPayable(item.getRecordType().intValue() == AccountingConstant.RECORD_TYPE_PAYABLE ? AccountingConstant.RECORD_TYPE_PAYABLE : AccountingConstant.RECORD_TYPE_RECEIVABLE);
      AppService.insert(main, detail);
    }
  }

  public static List<AccountingTransactionDetailItem> loadTransactionDetailItemByChequeEntry(Main main, Integer id, Integer type) {
    String sql = "select t1.* from fin_accounting_transaction_detail_item t1,fin_cheque_receipt_transaction_detail t2 \n"
            + "where t2.accounting_transaction_detail_item = t1.id AND t2.cheque_entry_id = ? AND t2.payable = ? ";
    return AppService.list(main, AccountingTransactionDetailItem.class, sql, new Object[]{id, type});
  }

}
