/*
 * @(#)AccountingChequeBounceService.java	1.0 Thu Feb 08 11:53:13 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.constant.AccountingConstant;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.AccountingChequeBounce;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.domain.AccountingTransactionSettlement;
import spica.fin.common.BankReconciliation;
import spica.scm.domain.AccountGroup;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * AccountingChequeBounceService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Feb 08 11:53:13 IST 2018
 */
public abstract class AccountingChequeBounceService {

  /**
   * AccountingChequeBounce paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingChequeBounceSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_cheque_bounce", AccountingChequeBounce.class, main);
    sql.main("select fin_accounting_cheque_bounce.id,fin_accounting_cheque_bounce.transaction_detail_item_id,fin_accounting_cheque_bounce.accounting_ledger_id,fin_accounting_cheque_bounce.bank_charge,fin_accounting_cheque_bounce.igst_amount,fin_accounting_cheque_bounce.sgst_amount,fin_accounting_cheque_bounce.cgst_amount,fin_accounting_cheque_bounce.total_amount,fin_accounting_cheque_bounce.penalty_amount from fin_accounting_cheque_bounce fin_accounting_cheque_bounce "); //Main query
    sql.count("select count(fin_accounting_cheque_bounce.id) as total from fin_accounting_cheque_bounce fin_accounting_cheque_bounce "); //Count query
    sql.join("left outer join fin_accounting_transaction_detail_item fin_accounting_cheque_bouncetransaction_detail_item_id on (fin_accounting_cheque_bouncetransaction_detail_item_id.id = fin_accounting_cheque_bounce.transaction_detail_item_id) left outer join fin_accounting_ledger fin_accounting_cheque_bounceaccounting_ledger_id on (fin_accounting_cheque_bounceaccounting_ledger_id.id = fin_accounting_cheque_bounce.accounting_ledger_id)"); //Join Query

    sql.string(new String[]{"fin_accounting_cheque_bouncetransaction_detail_item_id.document_number"}); //String search or sort fields
    sql.number(new String[]{"fin_accounting_cheque_bounce.id", "fin_accounting_cheque_bounceaccounting_ledger_id.entity_id", "fin_accounting_cheque_bounce.bank_charge", "fin_accounting_cheque_bounce.igst_amount", "fin_accounting_cheque_bounce.sgst_amount", "fin_accounting_cheque_bounce.cgst_amount", "fin_accounting_cheque_bounce.total_amount", "fin_accounting_cheque_bounce.penalty_amount"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountingChequeBounce.
   *
   * @param main
   * @return List of AccountingChequeBounce
   */
  public static final List<AccountingChequeBounce> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountingChequeBounceSqlPaged(main));
  }

//  /**
//   * Return list of AccountingChequeBounce based on condition
//   * @param main
//   * @return List<AccountingChequeBounce>
//   */
//  public static final List<AccountingChequeBounce> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountingChequeBounceSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountingChequeBounce by key.
   *
   * @param main
   * @param finAccountingChequeBounce
   * @return AccountingChequeBounce
   */
  public static final AccountingChequeBounce selectByPk(Main main, AccountingChequeBounce finAccountingChequeBounce) {
    return (AccountingChequeBounce) AppService.find(main, AccountingChequeBounce.class, finAccountingChequeBounce.getId());
  }

  /**
   * Insert AccountingChequeBounce.
   *
   * @param main
   * @param finAccountingChequeBounce
   */
  private static final void insert(Main main, AccountingChequeBounce finAccountingChequeBounce) {
    insertAble(main, finAccountingChequeBounce);  //Validating
    AppService.insert(main, finAccountingChequeBounce);

  }

  /**
   * Update AccountingChequeBounce by key.
   *
   * @param main
   * @param finAccountingChequeBounce
   * @return AccountingChequeBounce
   */
  private static final AccountingChequeBounce updateByPk(Main main, AccountingChequeBounce finAccountingChequeBounce) {
    updateAble(main, finAccountingChequeBounce); //Validating
    return (AccountingChequeBounce) AppService.update(main, finAccountingChequeBounce);
  }

  /**
   * Insert or update AccountingChequeBounce
   *
   * @param main
   * @param finAccountingChequeBounce
   */
  public static void insertOrUpdate(Main main, AccountingChequeBounce finAccountingChequeBounce) {
    if (finAccountingChequeBounce.getId() == null) {
      insert(main, finAccountingChequeBounce);
    } else {
      updateByPk(main, finAccountingChequeBounce);
    }
  }

  private static String getNarration(Main main, BankReconciliation br) {
    String txt = "";
    //TODO possible bug to do may not get the entry for settlement, see the new method in settlemetn service
    if (br.getId() != null) {
      List<AccountingTransactionSettlement> settlementList = AccountingTransactionSettlementService.selectByTransactionDetailPayment(main, br.getId());
      if (!StringUtil.isEmpty(settlementList)) {
        int i = 0;
        for (AccountingTransactionSettlement list : settlementList) {
          AccountingTransactionDetailItem item = AccountingTransactionDetailItemService.selectByPk(main, list.getTransactionDetailItemId());
          if (item.getDocumentNumber() != null) {
            if (i == 0) {
              txt += " Adjusted with ";
            }
            if (i > 0) {
              txt += ", ";
            }
            txt += item.getDocumentNumber();
            i++;
          }
        }
      }
    }
    return txt;
  }

  public static void insertOrUpdateChequeBounce(Main main, AccountingLedger bankchargeLedger, BankReconciliation bankReconciliation, AccountingChequeBounce chequeBounce) {
    AccountGroup group = null;
    String narration = "";
    if (chequeBounce.getTransactionDetailItemId() == null && bankReconciliation.getId() != null) {
      AccountingTransactionDetailItem item = AccountingTransactionDetailItemService.selectByPk(main, new AccountingTransactionDetailItem(bankReconciliation.getId()));
      chequeBounce.setTransactionDetailItemId(item);
      narration = getNarration(main, bankReconciliation);

    } else if (bankReconciliation.getId() == null) {
      AccountingTransaction tran = AccountingTransactionService.selectByPk(main, new AccountingTransaction(bankReconciliation.getTranId()));
      chequeBounce.setAccountingTransactionId(tran);
      group = tran.getAccountGroupId();
      narration = "Adjusted with " + tran.getRefDocumentNo() + " " + tran.getDocumentNumber();
    }
    AccountingChequeBounceService.insertOrUpdate(main, chequeBounce);
    if (group == null) {
      group = getAccountGroup(main, bankReconciliation);
    }
    if (bankReconciliation.isReciept(chequeBounce.getAccountingLedgerId().getId())) {
      AccountingLedger penaltyLedger = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_BANK_CHARGE_RECEIVABLE, bankchargeLedger.getCompanyId().getId());
      //AccountingLedger bankchargeReceivableLedger = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_CHEQUE_BOUNCE_RECEIVABLE, company.getId());
      AccountingLedger partyLedger = AccountingLedgerService.selectByPk(main, new AccountingLedger(bankReconciliation.getLedgerId()));

      if (chequeBounce.getBankCharge() != null && chequeBounce.getBankCharge() > 0) {
        AccountingTransaction tran = LedgerExternalDataService.getTransactionBounce(main, chequeBounce.getId(), bankReconciliation.getProcessedAt(), bankchargeLedger.getCompanyId().getCurrencyId(), bankchargeLedger.getCompanyId());
        tran.setNarration("Cheque bounce bank charges paid. Party is " + partyLedger.getTitle() + ". Cheque No [" + bankReconciliation.getDocumentNumber() + "]" + narration);
        LedgerExternalDataService.getTransactionDetailNew(tran, chequeBounce.getAccountingLedgerId(), null, chequeBounce.getTotalAmount());
        LedgerExternalDataService.getTransactionDetailNew(tran, bankchargeLedger, chequeBounce.getBankCharge(), null);
        //TODO FIXME what about IGST?     based on bank location need to add igst  
        //TODO bank location and find igst or sgst
        //Also option to choose expense ledger to manage different tax slab

        if (chequeBounce.getCgstAmount() != null && chequeBounce.getSgstAmount() != null) {
          LedgerExternalDataService.getTransactionDetailGstInput(main, tran, bankchargeLedger.getCgstId(), chequeBounce.getCgstAmount(), null, bankchargeLedger.getCompanyId().getId());
          LedgerExternalDataService.getTransactionDetailGstInput(main, tran, bankchargeLedger.getSgstId(), chequeBounce.getSgstAmount(), null, bankchargeLedger.getCompanyId().getId());
        } else if (chequeBounce.getIgstAmount() != null) {
          LedgerExternalDataService.getTransactionDetailGstInput(main, tran, bankchargeLedger.getIgstId(), chequeBounce.getIgstAmount(), null, bankchargeLedger.getCompanyId().getId());
        }
        LedgerExternalDataService.setFirstAsCredit(tran);
        tran.setAccountGroupId(group);
        AccountingTransactionService.insertOrUpdateAll(main, tran);//TODO this tax could get refund
      }

      //Debit note to party for the bounced cheque aount
      if (chequeBounce.getPenaltyAmount() != null && chequeBounce.getPenaltyAmount() > 0) {
        AccountingTransaction tran = LedgerExternalDataService.getTransactionBounceDebitNote(main, chequeBounce.getId(), bankReconciliation.getProcessedAt(), bankchargeLedger.getCompanyId().getCurrencyId(), bankchargeLedger.getCompanyId());
        tran.setNarration("Cheque bounce penalty receivable. Cheque No [" + bankReconciliation.getDocumentNumber() + "]" + narration);
        AccountingTransactionDetail detail = LedgerExternalDataService.getTransactionDetailNew(tran, partyLedger, chequeBounce.getPenaltyAmount(), null);
        LedgerExternalDataService.getTransactionDetailNew(tran, penaltyLedger, null, chequeBounce.getPenaltyAmount());
        LedgerExternalDataService.getDetailItemDebitNote(detail, chequeBounce.getPenaltyAmount(), chequeBounce.getPenaltyAmount(), chequeBounce.getPenaltyAmount(), null, null);
        LedgerExternalDataService.setFirstAsDebit(tran);
        tran.setAccountGroupId(group);
        AccountingTransactionService.insertOrUpdateAll(main, tran);
      }

      AccountingTransaction tran = LedgerExternalDataService.getTransactionBounceDebitNote(main, chequeBounce.getId(), bankReconciliation.getProcessedAt(), bankchargeLedger.getCompanyId().getCurrencyId(), bankchargeLedger.getCompanyId());
      tran.setNarration("Cheque bounce amount recievable. Cheque No [" + bankReconciliation.getDocumentNumber() + "]" + narration);
      AccountingTransactionDetail detail = LedgerExternalDataService.getTransactionDetailNew(tran, partyLedger, bankReconciliation.getNetAmount(), null);
      LedgerExternalDataService.getTransactionDetailNew(tran, chequeBounce.getAccountingLedgerId(), null, bankReconciliation.getNetAmount());
      LedgerExternalDataService.getDetailItemDebitNote(detail, bankReconciliation.getNetAmount(), bankReconciliation.getNetAmount(), bankReconciliation.getNetAmount(), null, null);
      LedgerExternalDataService.setFirstAsDebit(tran);
      tran.setAccountGroupId(group);
      AccountingTransactionService.insertOrUpdateAll(main, tran);

//      tran = LedgerExternalDataService.getTransactionBounceReverseJournal(main, chequeBounce.getId(), bankReconciliation.getProcessedAt(), bankchargeLedger.getCompanyId().getCurrencyId(), bankchargeLedger.getCompanyId());
//      tran.setNarration("Cheque bounce reverse entry amount recievable. Cheque No [" + bankReconciliation.getDocumentNumber() + "]" + narration);
//      LedgerExternalDataService.getTransactionDetailNew(tran, partyLedger, bankReconciliation.getNetAmount(), null);
//      LedgerExternalDataService.getTransactionDetailNew(tran, chequeBounce.getAccountingLedgerId(), null, bankReconciliation.getNetAmount());
//      LedgerExternalDataService.setFirstAsDebit(tran);
//      AccountingTransactionService.insertOrUpdateAll(main, tran);
    } else {
      AccountingLedger penaltyLedger = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_BANK_CHARGE_PAYABLE, bankchargeLedger.getCompanyId().getId());
      //   AccountingLedger bankchargePayableLedger = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_CHEQUE_BOUNCE_PAYABLE, company.getId());
      AccountingLedger partyLedger = AccountingLedgerService.selectByPk(main, new AccountingLedger(bankReconciliation.getLedgerId()));

      if (chequeBounce.getPenaltyAmount() != null && chequeBounce.getPenaltyAmount() > 0) {
        AccountingTransaction tran = LedgerExternalDataService.getTransactionBounceCreditNote(main, chequeBounce.getId(), bankReconciliation.getProcessedAt(), bankchargeLedger.getCompanyId().getCurrencyId(), bankchargeLedger.getCompanyId());
        tran.setNarration("Cheque bounce bank penalty payable to " + partyLedger.getTitle() + ". Cheque No [" + bankReconciliation.getDocumentNumber() + "]" + narration);
        AccountingTransactionDetail detail = LedgerExternalDataService.getTransactionDetailNew(tran, partyLedger, null, chequeBounce.getPenaltyAmount());
        LedgerExternalDataService.getTransactionDetailNew(tran, penaltyLedger, chequeBounce.getPenaltyAmount(), null);
        LedgerExternalDataService.getDetailItemCreditNote(detail, chequeBounce.getPenaltyAmount(), chequeBounce.getPenaltyAmount(), chequeBounce.getPenaltyAmount(), null, null);
        LedgerExternalDataService.setFirstAsCredit(tran);
        tran.setAccountGroupId(group);
        AccountingTransactionService.insertOrUpdateAll(main, tran);
      }

      AccountingTransaction tran = LedgerExternalDataService.getTransactionBounceCreditNote(main, chequeBounce.getId(), bankReconciliation.getProcessedAt(), bankchargeLedger.getCompanyId().getCurrencyId(), bankchargeLedger.getCompanyId());
      tran.setNarration("Cheque bounce amount payable. Cheque No [" + bankReconciliation.getDocumentNumber() + "]" + narration);
      AccountingTransactionDetail detail = LedgerExternalDataService.getTransactionDetailNew(tran, partyLedger, null, bankReconciliation.getNetAmount());
      LedgerExternalDataService.getTransactionDetailNew(tran, chequeBounce.getAccountingLedgerId(), bankReconciliation.getNetAmount(), null);
      LedgerExternalDataService.getDetailItemCreditNote(detail, bankReconciliation.getNetAmount(), bankReconciliation.getNetAmount(), bankReconciliation.getNetAmount(), null, null);
      LedgerExternalDataService.setFirstAsCredit(tran);
      tran.setAccountGroupId(group);
      AccountingTransactionService.insertOrUpdateAll(main, tran);

    }
    BankReconciliationService.updateBankChequeBounce(main, bankReconciliation);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param finAccountingChequeBounce
   */
  public static void clone(Main main, AccountingChequeBounce finAccountingChequeBounce) {
    finAccountingChequeBounce.setId(null); //Set to null for insert
    insert(main, finAccountingChequeBounce);
  }

  /**
   * Delete AccountingChequeBounce.
   *
   * @param main
   * @param finAccountingChequeBounce
   */
  public static final void deleteByPk(Main main, AccountingChequeBounce finAccountingChequeBounce) {
    deleteAble(main, finAccountingChequeBounce); //Validation
    AppService.delete(main, AccountingChequeBounce.class, finAccountingChequeBounce.getId());
  }

  /**
   * Delete Array of AccountingChequeBounce.
   *
   * @param main
   * @param finAccountingChequeBounce
   */
  public static final void deleteByPkArray(Main main, AccountingChequeBounce[] finAccountingChequeBounce) {
    for (AccountingChequeBounce e : finAccountingChequeBounce) {
      deleteByPk(main, e);
    }
  }

  public static AccountingChequeBounce selectByBankReconciliation(Main main, BankReconciliation br) {
    if (br.getId() != null) {
      return (AccountingChequeBounce) AppService.single(main, AccountingChequeBounce.class, "select * from fin_accounting_cheque_bounce where transaction_detail_item_id=?", new Object[]{br.getId()});
    } else {
      return (AccountingChequeBounce) AppService.single(main, AccountingChequeBounce.class, "select * from fin_accounting_cheque_bounce where accounting_transaction_id=?", new Object[]{br.getTranId()});
    }
  }

  private static final void deleteAble(Main main, AccountingChequeBounce finAccountingChequeBounce) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param finAccountingChequeBounce
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingChequeBounce finAccountingChequeBounce) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_cheque_bounce where upper(bank_charge)=?", new Object[]{StringUtil.toUpperCase(finAccountingChequeBounce.getBankCharge())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param finAccountingChequeBounce
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingChequeBounce finAccountingChequeBounce) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_cheque_bounce where upper(bank_charge)=? and id !=?", new Object[]{StringUtil.toUpperCase(finAccountingChequeBounce.getBankCharge()), finAccountingChequeBounce.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  private static AccountGroup getAccountGroup(Main main, BankReconciliation bankReconciliation) {

    //  AccountingLedger ledger = AccountingLedgerService.selectByPk(main, new AccountingLedger(ledgerId));
    AccountGroup group = null;
    if (bankReconciliation.getAccountingTransactionDetailId() != null) {
      group = (AccountGroup) AppService.single(main, AccountGroup.class, "select * from scm_account_group t1 join fin_accounting_transaction t2 on t1.id=t2.account_group_id\n"
              + "join fin_accounting_transaction_detail t3 on t2.id = t3.accounting_transaction_id and t3.id = ? ", new Object[]{bankReconciliation.getAccountingTransactionDetailId()});
    }
    return (group);
  }

}
