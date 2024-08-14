/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.fin.service;

import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.common.FilterObjects;
import spica.fin.domain.AccountingChequeBounce;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.common.BankReconciliation;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.ChequeEntry;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sanith
 */
public class BankReconciliationService {

  public static List<BankReconciliation> getBankReconciliationList(Main main, Integer status, FilterObjects filterObjects, AccountingLedger ledger) {
    //sums queries are based on these queries
    String sql = "select * from (SELECT t1.id,t1.document_type_id,tran.id as tran_id,tran.voucher_type_id,tran.entry_date, led.title particulars,led.id ledger_id,tran.voucher_type voucher_type,t6.title transaction_type,t1.document_number,t1.document_date,t1.refer_number refer_number,t1.processed_at,t1.net_amount,t1.cheque_entry_id, case when tran.bank_status is null then t1.bank_status else tran.bank_status end,t1.accounting_transaction_detail_id,tran.ledger_credit_id,tran.ledger_debit_id,tran.narration  \n"
            + "FROM fin_accounting_transaction_detail_item t1, fin_accounting_ledger led, fin_accounting_transaction_detail t3, "
            + " (SELECT acc_trans.id as id,t4.title as voucher_type,entry_date,bank_status,ledger_credit_id,ledger_debit_id,processed_at,t4.id  as voucher_type_id,acc_trans.narration \n"
            + "				   FROM fin_accounting_transaction as acc_trans,fin_accounting_voucher_type t4\n"
            + "WHERE acc_trans.company_id = ? AND acc_trans.voucher_type_id = t4.id  AND entry_date::date>=to_date(?, 'YYYY-MM-DD') AND entry_date::date <=to_date(?, 'YYYY-MM-DD')";

    main.clear();

    main.param(ledger.getCompanyId().getId());
    main.param(filterObjects.getFromDate());
    main.param(filterObjects.getToDate());

    if (status != null) {
      if (AccountingConstant.BANK_CHEQUE_ISSUED == status) {
        sql += " and acc_trans.processed_at is null";
      } else if (AccountingConstant.BANK_CHEQUE_RECONCILED == status) {
        sql += " and acc_trans.processed_at is not null";
      }
    }

    sql += " AND (ledger_credit_id = ? or ledger_debit_id = ?)) as tran, fin_accounting_doc_type t6 \n"
            + " WHERE t1.parent_id is null and t3.id = t1.accounting_transaction_detail_id and t3.accounting_ledger_id = led.id and tran.id=t3.accounting_transaction_id \n"
            + "and t6.id = t1.document_type_id ";

    main.param(ledger.getId());
    main.param(ledger.getId());

    if (status != null) {
      if (AccountingConstant.BANK_CHEQUE_ISSUED == status) {
        sql += " and (t1.bank_status = ? or tran.bank_status = ? or tran.bank_status is null) ";
        main.param(status);
      } else if (AccountingConstant.BANK_CHEQUE_RECONCILED == status) {
        sql += " and (t1.bank_status = ? or tran.bank_status = ? or tran.bank_status is null) ";
        main.param(status);
      } else { //dishounoured
        sql += " and (tran.bank_status = ?)";
      }
      main.param(status);
    }

    //sums queries are based on these queries
    sql += " union SELECT null as id,null as document_type_id,tran.id as tran_id,tran.voucher_type_id,tran.entry_date, led.title particulars,led.id ledger_id,tran.voucher_type voucher_type,'' as transaction_type,'' as document_number,tran.entry_date as document_date,'' refer_number,tran.processed_at,case when t3.credit is null then t3.debit else t3.credit end as total_amount,null, tran.bank_status ,t3.id,tran.ledger_credit_id,tran.ledger_debit_id,tran.narration  \n"
            + " FROM fin_accounting_ledger led, fin_accounting_transaction_detail t3, (SELECT acc_trans.id as id,t4.title as voucher_type,entry_date,bank_status,ledger_credit_id,ledger_debit_id,processed_at,total_amount,t4.id as voucher_type_id,acc_trans.narration"
            + " FROM fin_accounting_transaction as acc_trans,fin_accounting_voucher_type t4"
            + " WHERE company_id=? AND acc_trans.voucher_type_id = t4.id AND entry_date::date >= to_date(?, 'YYYY-MM-DD') AND entry_date::date <= to_date(?, 'YYYY-MM-DD' ) ";

    main.param(ledger.getCompanyId().getId());
    main.param(filterObjects.getFromDate());
    main.param(filterObjects.getToDate());

    if (status != null) {
      if (AccountingConstant.BANK_CHEQUE_ISSUED == status) {
        sql += " and acc_trans.processed_at is null";
      } else if (AccountingConstant.BANK_CHEQUE_RECONCILED == status) {
        sql += " and acc_trans.processed_at is not null";
      }
    }
    sql += " AND (ledger_credit_id=? or ledger_debit_id=?)) as tran WHERE t3.id not in(select accounting_transaction_detail_id from fin_accounting_transaction_detail_item) and t3.accounting_ledger_id=led.id"
            + " and tran.id=t3.accounting_transaction_id and led.id != ?";

    main.param(ledger.getId());
    main.param(ledger.getId());
    main.param(ledger.getId());

    if (status != null) {
      if (AccountingConstant.BANK_CHEQUE_ISSUED == status) {
        sql += " and (tran.bank_status = ? or tran.bank_status is null) ";
      } else if (AccountingConstant.BANK_CHEQUE_RECONCILED == status) {
        sql += " and (tran.bank_status = ? or tran.bank_status is null) ";
      } else { //dishounoured
        sql += " and (tran.bank_status = ?) ";
      }
      main.param(status);
    }

    sql += ") tab2 ";

    if (main.getPageData().getTotalRecords() == null && !StringUtil.isEmpty(main.getPageData().getSearchKeyWord())) {
      sql += " where ( UPPER(tab2.particulars) like ? OR CAST(tab2.entry_date AS TEXT) like ? OR  CAST(tab2.document_date AS TEXT) like ? OR CAST(tab2.net_amount AS TEXT) like ?  OR CAST( tab2.document_number AS TEXT ) like ? OR UPPER(tab2.refer_number) like ? OR UPPER(tab2.narration) like ?) ";
      String q = "%" + main.getPageData().getSearchKeyWord().toUpperCase() + "%";
      main.param(q);
      main.param(q);
      main.param(q);
      main.param(q);
      main.param(q);
      main.param(q);
      main.param(q);
    }
    sql += " order by tab2.entry_date DESC, tab2.document_date DESC ";
    return AppDb.getList(main.dbConnector(), BankReconciliation.class, sql, main.getParamData().toArray());

  }

  public static void updateBankReconciliation(Main main, List<BankReconciliation> list) {
    main.clear();
    for (BankReconciliation bc : list) {
      boolean statusChange = (bc.getBankStatus() == null || bc.getBankStatus() == AccountingConstant.BANK_CHEQUE_RECIEVED || bc.getBankStatus() == AccountingConstant.BANK_CHEQUE_ISSUED || bc.getBankStatus() == AccountingConstant.BANK_CHEQUE_RECONCILED || bc.getBankStatus() == AccountingConstant.BANK_CHEQUE_BOUNCE_RESUBMITTED);
      if (bc.getProcessedAt() != null) {
        updateBankStatus(main, bc.getId(), bc.getChequeEntryId(), statusChange ? AccountingConstant.BANK_CHEQUE_RECONCILED : bc.getBankStatus(), bc.getProcessedAt(), bc.getTranId());
      } else {
        updateBankStatus(main, bc.getId(), bc.getChequeEntryId(), statusChange ? AccountingConstant.BANK_CHEQUE_ISSUED : bc.getBankStatus(), null, bc.getTranId()); //TODO do this on a seperate button click
      }
    }
  }

  public static void updateBankChequeReceieved(Main main, ChequeEntry ce) {
    if (ce != null) {
      ce.setStatusId(AccountingConstant.BANK_CHEQUE_RECIEVED);
      ChequeEntryService.insertOrUpdate(main, ce);
    }
  }

  public static void updateBankChequeIssued(Main main, ChequeEntry ce) {
    if (ce != null) {
      updateChequeEntryStatus(main, AccountingConstant.BANK_CHEQUE_ISSUED, ce.getId());
    }
  }

  public static void updateBankChequeIssued(Main main, AccountingChequeBounce cb) {
    if (cb != null) {
      if (cb.getTransactionDetailItemId() != null) {
        Integer ceId = cb.getTransactionDetailItemId().getChequeEntryId() != null ? cb.getTransactionDetailItemId().getChequeEntryId().getId() : null;
        updateBankStatus(main, cb.getTransactionDetailItemId().getId(), ceId, AccountingConstant.BANK_CHEQUE_ISSUED, null, cb.getTransactionDetailItemId().getAccountingTransactionDetailId().getAccountingTransactionId().getId());
      } else if (cb.getAccountingTransactionId() != null) {
        updateBankStatus(main, null, null, AccountingConstant.BANK_CHEQUE_ISSUED, null, cb.getAccountingTransactionId().getId());
      }
    }
  }

  public static void updateBankChequeBounce(Main main, BankReconciliation bc) {
    updateBankStatus(main, bc.getId(), bc.getChequeEntryId(), AccountingConstant.BANK_CHEQUE_BOUNCE, bc.getProcessedAt(), bc.getTranId());
  }

  public static void updateBankChequeCancel(Main main, BankReconciliation bc) { //Not implemented now for future
    updateBankStatus(main, bc.getId(), bc.getChequeEntryId(), AccountingConstant.BANK_CHEQUE_CANCELED, bc.getProcessedAt(), bc.getTranId());
  }

  private static void updateBankStatus(Main main, Integer itemId, Integer chequeEntryId, Integer status, Date processedDate, Integer tranId) {
    main.clear();
    if (itemId != null) {
      main.param(processedDate);
      main.param(status);
      main.param(itemId);
      AccountingTransactionDetailItem item = AccountingTransactionDetailItemService.selectByPk(main, new AccountingTransactionDetailItem(itemId));
      item.setBankStatus(status);
      item.setProcessedAt(processedDate);
      AppService.update(main, item);
      if (chequeEntryId != null) {
        updateChequeEntryStatus(main, status, chequeEntryId);
      }
    }
    if (tranId != null) {
      AccountingTransaction transaction = AccountingTransactionService.selectByPk(main, new AccountingTransaction(tranId));
      if (transaction != null) {
        transaction.setProcessedAt(processedDate);
        transaction.setBankStatus(status);
        AppService.update(main, transaction);
      }
    }
  }

  private static void updateChequeEntryStatus(Main main, Integer chequeStatus, Integer id) {
    if (id != null) {
      main.clear();
      main.param(chequeStatus);
      main.param(id);
      AppService.updateSql(main, ChequeEntry.class, "update fin_cheque_entry set modified_by=?, modified_at = ?, status_id = ? where id = ?", true);
    }
  }

  public static void deleteBounce(MainView main, AccountingChequeBounce chequeBounce) {
    if (chequeBounce.getId() != null) {
      AppService.delete(main, AccountingChequeBounce.class, chequeBounce.getId());
      LedgerExternalDataService.deleteChequebounce(main, chequeBounce);
    }
  }

  public static Double selectSumOfDebitCreditReconcilation(Main main, Integer companyId, FilterObjects filterObjects, Integer ledgerId, int dc) {
    String sql = null;
    if (dc == AccountingConstant.IS_DEBIT) {
      sql = "select sum(tran.total_amount::float) from fin_accounting_transaction tran where ledger_debit_id = ? ";
    } else {
      sql = "select sum(tran.total_amount::float) from fin_accounting_transaction tran where ledger_credit_id = ? ";
    }
    sql += "and to_char(tran.entry_date, 'yyyy-mm-dd') between to_char(?::date, 'yyyy-mm-dd') and to_char(?::date, 'yyyy-mm-dd') and tran.processed_at is null and tran.company_id = ?";
    // + "and tran.bank_status in(?,?,?) and tran.processed_at is null and tran.company_id = ? ";
    //AccountingConstant.BANK_CHEQUE_ISSUED, AccountingConstant.BANK_CHEQUE_RECIEVED,AccountingConstant.BANK_CHEQUE_BOUNCE_RESUBMITTED,
    return AppService.countDouble(main, sql, new Object[]{ledgerId, filterObjects.getFromDate(), filterObjects.getToDate(), companyId});
  }

}
