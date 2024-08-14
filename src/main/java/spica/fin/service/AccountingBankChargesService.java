/*
 * @(#)FinAccountingBankChargesService.java	1.0 Fri Apr 06 16:47:24 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import wawo.app.common.AppService;
import wawo.app.Main;
import spica.fin.domain.AccountingBankCharges;
import spica.fin.domain.AccountingLedger;
import spica.fin.common.FilterObjects;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.common.BankReconciliation;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;

/**
 * FinAccountingBankChargesService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 06 16:47:24 IST 2018
 */
public abstract class AccountingBankChargesService {

  /**
   * FinAccountingBankCharges paginated query.
   *
   * @param main
   * @return SqlPage
   */
//  private static SqlPage getAccountingBankChargesSqlPaged(Main main) {
//    SqlPage sql = AppService.sqlPage("fin_accounting_bank_charges", AccountingBankCharges.class, main);
//    sql.main("select fin_accounting_bank_charges.id,fin_accounting_bank_charges.accounting_ledger_id,fin_accounting_bank_charges.bank_charge,fin_accounting_bank_charges.igst_amount,fin_accounting_bank_charges.sgst_amount,fin_accounting_bank_charges.cgst_amount,fin_accounting_bank_charges.total_amount,fin_accounting_bank_charges.remarks from fin_accounting_bank_charges fin_accounting_bank_charges "); //Main query
//    sql.count("select count(fin_accounting_bank_charges.id) as total from fin_accounting_bank_charges fin_accounting_bank_charges "); //Count query
//    sql.join(""); //Join Query
//
//    sql.string(new String[]{"fin_accounting_bank_charges.remarks"}); //String search or sort fields
//    sql.number(new String[]{"fin_accounting_bank_charges.id", "fin_accounting_bank_chargesaccounting_ledger_id.entity_id", "fin_accounting_bank_charges.bank_charge", "fin_accounting_bank_charges.igst_amount", "fin_accounting_bank_charges.sgst_amount", "fin_accounting_bank_charges.cgst_amount", "fin_accounting_bank_charges.total_amount"}); //Numeric search or sort fields
//    sql.date(null);  //Date search or sort fields
//    return sql;
//  }
  public static List<BankReconciliation> getBankReconciliationList(Main main, Integer status, FilterObjects filterObjects, AccountingLedger ledger) {
    Integer lgId = ledger.getId();
    String sql = "SELECT t1.id,t1.document_type_id,tran.id as tran_id,tran.voucher_type_id,tran.entry_date,t2.title particulars,t2.id ledger_id,t4.title voucher_type,t6.title transaction_type,t1.document_number, "
            + "t1.document_date,t1.refer_number,t1.processed_at,t1.net_amount,t1.cheque_entry_id, t1.bank_status,t1.accounting_transaction_detail_id, "
            + "t7.id bank_charge_id, t7.bank_charge as bank_charge_amount, t7.igst_amount, t7.total_amount "
            + "FROM "
            + "fin_accounting_transaction_detail_item t1 "
            + "inner join fin_accounting_transaction_detail t3 on t3.id = t1.accounting_transaction_detail_id "
            + "inner join fin_accounting_transaction tran on tran.id = t3.accounting_transaction_id "
            + "inner join fin_accounting_ledger t2 on t3.accounting_ledger_id = t2.id "
            + "inner join fin_accounting_voucher_type t4 on tran.voucher_type_id = t4.id "
            + "inner join fin_accounting_doc_type t6 on t6.id = t1.document_type_id "
            + "left join fin_accounting_bank_charges t7 on t7.transaction_detail_item_id = t1.id "
            + "WHERE t1.parent_id is null and (tran.ledger_credit_id = ? or tran.ledger_debit_id = ?) and t2.company_id = ? and t4.id in(?,?) and to_char(tran.entry_date, 'yyyy-mm-dd') between to_char(?::date, 'yyyy-mm-dd') and to_char(?::date, 'yyyy-mm-dd') ";
    if (status == null) {
      return AppDb.getList(main.dbConnector(), BankReconciliation.class, sql, new Object[]{lgId, lgId, ledger.getCompanyId().getId(), AccountingConstant.VOUCHER_TYPE_PAYMENT.getId(), AccountingConstant.VOUCHER_TYPE_RECEIPT.getId(), filterObjects.getFromDate(), filterObjects.getToDate()});
    } else {
      sql += " and t1.bank_status = ? ";
      return AppDb.getList(main.dbConnector(), BankReconciliation.class, sql, new Object[]{lgId, lgId, ledger.getCompanyId().getId(), AccountingConstant.VOUCHER_TYPE_PAYMENT.getId(), AccountingConstant.VOUCHER_TYPE_RECEIPT.getId(), filterObjects.getFromDate(), filterObjects.getToDate(), status});
    }
  }

  /**
   * Return List of AccountingBankCharges.
   *
   * @param main
   * @return List of AccountingBankCharges
   */
//  public static final List<AccountingBankCharges> listPaged(Main main) {
//    return AppService.listPagedJpa(main, getAccountingBankChargesSqlPaged(main));
//  }
  /**
   * Select AccountingBankCharges by key.
   *
   * @param main
   * @param accountingBankCharges
   * @return AccountingBankCharges
   */
  public static final AccountingBankCharges selectByPk(Main main, AccountingBankCharges accountingBankCharges) {
    return (AccountingBankCharges) AppService.find(main, AccountingBankCharges.class, accountingBankCharges.getId());
  }

  /**
   * Insert AccountingBankCharges.
   *
   * @param main
   * @param accountingBankCharges
   */
  public static final void insert(Main main, AccountingBankCharges accountingBankCharges) {
    insertAble(main, accountingBankCharges);  //Validating
    AppService.insert(main, accountingBankCharges);

  }

  public static final void insert(Main main, AccountingLedger bankchargeLedger, AccountingLedger bankLedger, List<BankReconciliation> bankReconciliationList) {
    AccountingBankCharges accountingBankCharges = null;
    for (BankReconciliation bankReconciliation : bankReconciliationList) {

      if (bankReconciliation.getBankChargeAmount() != null && bankReconciliation.getBankChargeAmount() > 0 && bankReconciliation.getBankChargeId() == null) {

        accountingBankCharges = new AccountingBankCharges();
        accountingBankCharges.setAccountingLedgerId(bankchargeLedger);
        accountingBankCharges.setBankCharge(bankReconciliation.getBankChargeAmount());
        accountingBankCharges.setCgstAmount(bankReconciliation.getCgstAmount());
        accountingBankCharges.setSgstAmount(bankReconciliation.getSgstAmount());
        accountingBankCharges.setIgstAmount(bankReconciliation.getIgstAmount());
        accountingBankCharges.setTotalAmount(bankReconciliation.getTotalAmount());
        AccountingTransactionDetailItem dtl = (AccountingTransactionDetailItem) AppService.single(main, AccountingTransactionDetailItem.class, "select * from fin_accounting_transaction_detail_item where id=?", new Object[]{bankReconciliation.getId()});
        accountingBankCharges.setTransactionDetailItemId(dtl);
        insertOrUpdate(main, accountingBankCharges);
        main.em().flush();
        AccountingTransaction tran = LedgerExternalDataService.getTransactionExpenseBankCharge(main, accountingBankCharges.getId(), new Date(), bankchargeLedger.getCompanyId().getCurrencyId(), bankchargeLedger.getCompanyId());
        tran.setNarration("Bank charges paid for Document No [" + bankReconciliation.getDocumentNumber() + "]");
        LedgerExternalDataService.getTransactionDetailNew(tran, bankLedger, null, bankReconciliation.getTotalAmount());
        LedgerExternalDataService.getTransactionDetailNew(tran, bankchargeLedger, bankReconciliation.getBankChargeAmount(), null);
        //TODO FIXME what about IGST?    need to go inside taxcalculator or    
        //TODO bank location and find igst or sgst
        //Also option to choose expense ledger to manage different tax slab
        if (bankchargeLedger.getCgstId() != null && bankchargeLedger.getSgstId() != null) {
          LedgerExternalDataService.getTransactionDetailGstInput(main, tran, bankchargeLedger.getCgstId(), bankReconciliation.getCgstAmount(), null, bankchargeLedger.getCompanyId().getId());
          LedgerExternalDataService.getTransactionDetailGstInput(main, tran, bankchargeLedger.getSgstId(), bankReconciliation.getSgstAmount(), null, bankchargeLedger.getCompanyId().getId());
        } else if (bankchargeLedger.getIgstId() != null) {
          LedgerExternalDataService.getTransactionDetailGstInput(main, tran, bankchargeLedger.getIgstId(), bankReconciliation.getIgstAmount(), null, bankchargeLedger.getCompanyId().getId());
        }
        LedgerExternalDataService.setFirstAsCredit(tran);
        AccountingTransactionService.insertOrUpdateAll(main, tran);//TODO this tax could get refund    

      }
    }
  }

  /**
   * Update AccountingBankCharges by key.
   *
   * @param main
   * @param accountingBankCharges
   * @return AccountingBankCharges
   */
  public static final AccountingBankCharges updateByPk(Main main, AccountingBankCharges accountingBankCharges) {
    updateAble(main, accountingBankCharges); //Validating
    return (AccountingBankCharges) AppService.update(main, accountingBankCharges);
  }

  /**
   * Insert or update AccountingBankCharges
   *
   * @param main
   * @param accountingBankCharges
   */
  public static void insertOrUpdate(Main main, AccountingBankCharges accountingBankCharges) {
    if (accountingBankCharges.getId() == null) {
      insert(main, accountingBankCharges);
    } else {
      updateByPk(main, accountingBankCharges);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingBankCharges
   */
  public static void clone(Main main, AccountingBankCharges accountingBankCharges) {
    accountingBankCharges.setId(null); //Set to null for insert
    insert(main, accountingBankCharges);
  }

  /**
   * Delete AccountingBankCharges.
   *
   * @param main
   * @param accountingBankCharges
   */
  public static final void deleteByPk(Main main, AccountingBankCharges accountingBankCharges) {
    deleteAble(main, accountingBankCharges); //Validation
    //accountingBankCharges = selectByPk(main, accountingBankCharges);
    LedgerExternalDataService.deleteBankCharges(main, accountingBankCharges);
    AppService.delete(main, AccountingBankCharges.class, accountingBankCharges.getId());
  }

  /**
   * Delete Array of AccountingBankCharges.
   *
   * @param main
   * @param accountingBankCharges
   */
  public static final void deleteByPkArray(Main main, AccountingBankCharges[] accountingBankCharges) {
    for (AccountingBankCharges e : accountingBankCharges) {
      deleteByPk(main, e);
    }
  }

  private static final void deleteAble(Main main, AccountingBankCharges accountingBankCharges) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingBankCharges
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingBankCharges accountingBankCharges) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_bank_charges where upper(bank_charge)=?", new Object[]{StringUtil.toUpperCase(accountingBankCharges.getBankCharge())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingBankCharges
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingBankCharges accountingBankCharges) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_bank_charges where upper(bank_charge)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingBankCharges.getBankCharge()), accountingBankCharges.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
