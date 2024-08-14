/*
 * @(#)AccountingTransactionService.java	1.0 Fri Feb 24 15:58:34 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingChequeBounce;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.domain.AccountingTransactionSettlement;
import spica.fin.domain.ChequeEntry;
import spica.fin.domain.ChequeReceiptTransactionDetail;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.TradeOutstanding;
import spica.fin.domain.VendorClaim;

import static spica.fin.service.AccountingTransactionDetailService.selectByTransaction;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * AccountingTransactionService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:34 IST 2017
 */
public abstract class AccountingTransactionService {

  /**
   * AccountingTransaction paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingTransactionSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_transaction", AccountingTransaction.class, main);
    sql.main("select fin_accounting_transaction.id,fin_accounting_transaction.accounting_ledger_id,fin_accounting_transaction.transaction_seq_no,fin_accounting_transaction.accounting_doc_type_id,fin_accounting_transaction.reference_doc_file_path,fin_accounting_transaction.ref_document_no,fin_accounting_transaction.narration,fin_accounting_transaction.debit,fin_accounting_transaction.credit,fin_accounting_transaction.currency,fin_accounting_transaction.entry_date,fin_accounting_transaction.created_by,fin_accounting_transaction.created_at,fin_accounting_transaction.modified_by,fin_accounting_transaction.modified_at from fin_accounting_transaction fin_accounting_transaction "); //Main query
    sql.count("select count(fin_accounting_transaction.id) as total from fin_accounting_transaction fin_accounting_transaction "); //Count query
    sql.join("left outer join fin_accounting_ledger fin_accounting_transactionaccounting_ledger_id on (fin_accounting_transactionaccounting_ledger_id.id = fin_accounting_transaction.accounting_ledger_id) left outer join fin_accounting_doc_type fin_accounting_transactionaccounting_doc_type_id on (fin_accounting_transactionaccounting_doc_type_id.id = fin_accounting_transaction.accounting_doc_type_id)"); //Join Query
    //   sql.orderBy("fin_accounting_transaction.id desc");
    sql.string(new String[]{"fin_accounting_transaction.transaction_seq_no", "fin_accounting_transactionaccounting_doc_type_id.title", "fin_accounting_transaction.reference_doc_file_path", "fin_accounting_transaction.narration", "fin_accounting_transaction.ref_document_no"}); //String search or sort fields
    sql.number(new String[]{"fin_accounting_transaction.id", "fin_accounting_transactionaccounting_ledger_id.entity_id", "fin_accounting_transaction.debit", "fin_accounting_transaction.credit", "fin_accounting_transaction.currency"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountingTransaction.
   *
   * @param main
   * @return List of AccountingTransaction
   */
  public static final List<AccountingTransaction> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountingTransactionSqlPaged(main));
  }

  public static final List<AccountingTransaction> listAll(Main main) {
    return AppService.listAllJpa(main, getAccountingTransactionSqlPaged(main));
  }

  /**
   * Select AccountingTransaction by key.
   *
   * @param main
   * @param accountingTransaction
   * @return AccountingTransaction
   */
  public static final AccountingTransaction selectByPk(Main main, AccountingTransaction accountingTransaction) {
    return (AccountingTransaction) AppService.find(main, AccountingTransaction.class, accountingTransaction.getId());
  }

  /**
   * Insert AccountingTransaction.
   *
   * @param main
   * @param accountingTransaction
   */
  public static final void insert(Main main, AccountingTransaction accountingTransaction) {
    insertAble(main, accountingTransaction);  //Validating
    AppService.insert(main, accountingTransaction);

  }

  /**
   * Update AccountingTransaction by key.
   *
   * @param main
   * @param tran
   * @return AccountingTransaction
   */
  public static final AccountingTransaction updateByPk(Main main, AccountingTransaction tran) {
    updateAble(main, tran); //Validating
    return (AccountingTransaction) AppService.update(main, tran);
  }

  public static void insertOrUpdateAll(Main main, AccountingTransaction tran) {

    if (tran.getCurrencyId() == null) {
      tran.setCurrencyId(tran.getCompanyId().getCurrencyId());
    }
    insertOrUpdate(main, tran);

    // List<AccountingTransactionDetailItem> itemList = new ArrayList<>();
    for (AccountingTransactionDetail detail : tran.getTransactionDetail()) {
      if (detail.getAccountingLedgerId() != null) {
        detail.setEntryDate(tran.getEntryDate()); //TODO remove entry date fromthis db
        AccountingTransactionDetailService.insertOrUpdate(main, detail);
        if (detail.getDetailItem() != null) {
          for (AccountingTransactionDetailItem item : detail.getDetailItem()) {
            settleItem(main, tran, detail, item);
          }
          finalSettle(main, tran, detail);
        }
      }
    }
  }

  public static void finalSettle(Main main, AccountingTransaction tran, AccountingTransactionDetail detail) {
    //TODO test the below code
    if (!StringUtil.isEmpty(detail.getSettlementList())) {
      for (AccountingTransactionSettlement s : detail.getSettlementList()) {
        s.setTransactionDetailId(detail);
        s.setAdjustedTransactionDetailId(s.getTransactionDetailItemId().getAccountingTransactionDetailId());
        //    TradeOutstandingService.updateCollectionAndBalance(main, tran, s.getTransactionDetailItemId());
        AccountingTransactionDetailItemService.insertOrUpdate(main, s.getTransactionDetailItemId());
        AccountingTransactionDetailItemService.insertOrUpdate(main, s.getAdjustedTransactionDetailItemId());
        AccountingTransactionSettlementService.insertOrUpdate(main, s);
      }
    }
  }

  private static void settleItem(Main main, AccountingTransaction tran, AccountingTransactionDetail detail, AccountingTransactionDetailItem item) {
    if (item.getRecordType() == null) {
      if (tran.isPurchase() || tran.isExpenses() || tran.isCreditNote()) {
        item.setRecordType(AccountingConstant.RECORD_TYPE_PAYABLE);
      } else if (tran.isSales() || tran.isDebitNote()) {
        item.setRecordType(AccountingConstant.RECORD_TYPE_RECEIVABLE);
      } else if (tran.isReceipt()) {
        item.setRecordType(AccountingConstant.RECORD_TYPE_RECEIVED);
      } else if (tran.isPayment()) {
        item.setRecordType(AccountingConstant.RECORD_TYPE_PAYED);
      } else if (tran.isJournal()) { //TODO may be self adusted
        if (StringUtil.gtDouble(detail.getCredit(), 0.0)) {
          item.setRecordType(AccountingConstant.RECORD_TYPE_PAYED);
        } else if (StringUtil.gtDouble(detail.getDebit(), 0.0)) {
          item.setRecordType(AccountingConstant.RECORD_TYPE_RECEIVED);
        }
      }
    }

    if (item.getDocumentTypeId() == null) {
      if (tran.isExpenses() || tran.isCreditNote()) {
        item.setDocumentTypeId(AccountingConstant.DOC_TYPE_PAYABLE_BILL);
      } else if (tran.isPurchase()) {
        item.setDocumentTypeId(AccountingConstant.DOC_TYPE_PURCHASE_BILL);
      } else if (tran.isDebitNote()) {
        item.setDocumentTypeId(AccountingConstant.DOC_TYPE_RECEIVABLE_BILL);
      } else if (tran.isSales()) {
        item.setDocumentTypeId(AccountingConstant.DOC_TYPE_SALES_BILL);
      } else if (tran.isReceipt()) {
        item.setDocumentTypeId(AccountingConstant.DOC_TYPE_RECEIPT_CASH);
      } else if (tran.isPayment()) {
        item.setDocumentTypeId(AccountingConstant.DOC_TYPE_PAYMENT_CASH);
      } else if (tran.isJournal()) {
        item.setDocumentTypeId(AccountingConstant.DOC_TYPE_RECEIPT_BILL_ADJUSTMENT);
      }
    }// else {
    if (tran.isReceipt() || tran.isPayment()) {
      //  if (item.isCheque()) {
      item.setBankStatus(AccountingConstant.BANK_CHEQUE_ISSUED);
      // }
    } else if (tran.isJournal() && item.getDocumentTypeId() != null && StringUtil.equalsInt(item.getDocumentTypeId().getId(), AccountingConstant.DOC_TYPE_PAYMENT_CHEQUE.getId())) {
      item.setBankStatus(AccountingConstant.BANK_CHEQUE_ISSUED);
    }
    //}

    if (item.getDocumentDate() == null) {
      item.setDocumentDate(new Date());
    }

    if (item.getChequeEntryId() != null) {
      BankReconciliationService.updateBankChequeIssued(main, item.getChequeEntryId());
    }

//            item.sett
//            item.setNetAmount(tran.getCreditTotal());
//            item.setBalanceAmount(tran.getCreditTotal());
    if (!StringUtil.isEmpty(detail.getSettlementList())) {
      if (item.getBalanceAmount() > 0 && item.getBalanceAmount().doubleValue() != item.getNetAmount().doubleValue()) {
        item.setStatus(AccountingConstant.STATUS_PROCESSED);
        AccountingTransactionDetailItem partItem = new AccountingTransactionDetailItem(item);
        partItem.setAccountingTransactionDetailId(detail);
        setPayOrReceive(tran, partItem);
        partItem.setStatus(AccountingConstant.STATUS_PARTIAL_PROCESSED);
        AccountingTransactionDetailItemService.insertOrUpdate(main, partItem);
      } else if (item.getBalanceAmount() > 0 && item.getBalanceAmount().doubleValue() == item.getNetAmount().doubleValue()) {
        setPayOrReceive(tran, item); //May be this can be treated as paid
      }
    } else { //if no settlement then its advance
      setPayOrReceive(tran, item);
    }

    if (tran.isJournalSettlement()) { //in case of journal between parties the payable in one party means recievable in another
      for (AccountingTransactionDetail d : tran.getTransactionDetail()) {
        if (d.getAccountingLedgerId().isDebtorsOrCreditors()) {
          if (d.getAccountingLedgerId().getId().intValue() != item.getAccountingTransactionDetailId().getAccountingLedgerId().getId()) {

            AccountingTransactionDetailItem partItem = new AccountingTransactionDetailItem(item);
            partItem.setAccountingTransactionDetailId(d);
            partItem.setParentId(null);
              //in case these is adjustment with bill and balance is 0 then making as payable for another party
            partItem.setBalanceAmount(item.getNetAmount());
            partItem.setStatus(AccountingConstant.STATUS_NEW); //setting to new
             setPayOrReceive(tran, partItem);
            //  setPayOrReceive(tran, partItem);//  partItem.setStatus(AccountingConstant.STATUS_PARTIAL_PROCESSED);
           // partItem.setRecordType(item.getRecordType() == AccountingConstant.RECORD_TYPE_RECEIVABLE ? AccountingConstant.RECORD_TYPE_PAYABLE : AccountingConstant.RECORD_TYPE_RECEIVABLE);
            AccountingTransactionDetailItemService.insertOrUpdate(main, partItem);
            //  partItem.setStatus(AccountingConstant.STATUS_PARTIAL_PROCESSED);
          }
        }
      }
    }

    AccountingTransactionDetailItemService.insertOrUpdate(main, item);
  }

  public static void setPayOrReceive(AccountingTransaction tran, AccountingTransactionDetailItem item) {
    if (tran.isReceipt()) {
      item.setRecordType(AccountingConstant.RECORD_TYPE_PAYABLE);
    } else if (tran.isPayment()) {
      item.setRecordType(AccountingConstant.RECORD_TYPE_RECEIVABLE);
    } else if (tran.isJournal()) { //TODO may be self adusted
      if (StringUtil.gtDouble(item.getAccountingTransactionDetailId().getCredit(), 0.0)) {
        item.setRecordType(AccountingConstant.RECORD_TYPE_PAYABLE);
      } else if (StringUtil.gtDouble(item.getAccountingTransactionDetailId().getDebit(), 0.0)) {
        item.setRecordType(AccountingConstant.RECORD_TYPE_RECEIVABLE);
      }
    }
  }

  /**
   * Insert or update AccountingTransaction
   *
   * @param main
   * @param tran
   */
  public static void insertOrUpdate(Main main, AccountingTransaction tran) {
    if (tran.getId() == null) {
      //if (StringUtil.isEmpty(accountingTransaction.getDocumentNumber())) {
      tran.setDocumentNumber(AccountingPrefixService.getPrefixOnSave(main, tran, tran.getCompanyId()));
      // }
      insert(main, tran);
    } else {
      updateByPk(main, tran);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingTransaction
   */
  public static void clone(Main main, AccountingTransaction accountingTransaction) {
    accountingTransaction.setId(null); //Set to null for insert
    insert(main, accountingTransaction);
  }

  public static String hasBillItemsAttached(Main main, List<AccountingTransactionDetail> accountingTransactionDetailList) {

    String existing = "";
    int i = 0;
    for (AccountingTransactionDetail td : accountingTransactionDetailList) {
      List<AccountingTransaction> exist = (List<AccountingTransaction>) main.em().list(AccountingTransaction.class, "select * from fin_accounting_transaction where id in (select accounting_transaction_id from fin_accounting_transaction_detail where id in (select transaction_detail_id from fin_accounting_transaction_settlement where adjusted_transaction_detail_id=? ))", new Object[]{td.getId()});
      if (!StringUtil.isEmpty(exist)) {

        for (AccountingTransaction t : exist) {
          if (i > 0) {
            existing += ",";
          }
          existing += t.getDocumentNumber();
          i++;
        }
      }
    }
    return existing;
  }

  /**
   * Delete AccountingTransaction.
   *
   * @param main
   * @param accountingTransaction
   */
  public static final void deleteByPk(Main main, AccountingTransaction accountingTransaction) {
    deleteAble(main, accountingTransaction); //Validation
    List<AccountingTransactionDetail> accountingTransactionDetailList = selectByTransaction(main, accountingTransaction);
    String existing = hasBillItemsAttached(main, accountingTransactionDetailList);
    if (!StringUtil.isEmpty(existing)) {
      throw new UserMessageException("already.used", new String[]{existing});
    }
    updateSettlement(main, accountingTransactionDetailList);
    ChequeEntry ce;
    for (AccountingTransactionDetail td : accountingTransactionDetailList) {
      ce = (ChequeEntry) AppService.single(main, ChequeEntry.class, "select id from fin_cheque_entry where id in (select cheque_entry_id from fin_accounting_transaction_detail_item where accounting_transaction_detail_id = ?)", new Object[]{td.getId()});
      BankReconciliationService.updateBankChequeReceieved(main, ce);
      AppService.deleteSql(main, ChequeReceiptTransactionDetail.class, "delete from fin_cheque_receipt_transaction_detail where accounting_transaction_detail_item in (select id from fin_accounting_transaction_detail_item where accounting_transaction_detail_id = ?)", new Object[]{td.getId()});

      AppService.deleteSql(main, AccountingChequeBounce.class, "delete from fin_accounting_cheque_bounce where transaction_detail_item_id in (select id from fin_accounting_transaction_detail_item where accounting_transaction_detail_id=?)", new Object[]{td.getId()});
      AppService.deleteSql(main, AccountingTransactionSettlement.class, "delete from fin_accounting_transaction_settlement where transaction_detail_id=? or adjusted_transaction_detail_id=?", new Object[]{td.getId(), td.getId()});
      AppService.deleteSql(main, AccountingTransactionDetailItem.class, "delete from fin_accounting_transaction_detail_item where accounting_transaction_detail_id=?", new Object[]{td.getId()});

    }
    AppService.deleteSql(main, AccountingTransactionDetail.class, "delete from fin_accounting_transaction_detail where accounting_transaction_id=?", new Object[]{accountingTransaction.getId()});
    // AppService.deleteSql(main, AccountingTransaction.class, "delete from fin_accounting_transaction where id=?", new Object[]{accountingTransaction.getId()});
    AppService.deleteSql(main, TradeOutstanding.class, "delete from fin_trade_outstanding where parent_id in (select id from fin_trade_outstanding where accounting_transaction_id=? )",
      new Object[]{accountingTransaction.getId()});
    AppService.deleteSql(main, TradeOutstanding.class, "delete from fin_trade_outstanding where accounting_transaction_id=?", new Object[]{accountingTransaction.getId()});
    //  AppService.deleteSql(main, TradeOutstandingTax.class, "delete from fin_trade_outstanding_tax where accounting_transaction_id=?", new Object[]{accountingTransaction.getId()});

    AppService.deleteSql(main, AccountingTransaction.class, "delete from fin_accounting_transaction where id=?", new Object[]{accountingTransaction.getId()});
    //TODO update chequ entry status too

  }

  public static final void deleteByTransaction(Main main, AccountingTransaction accountingTransaction) {
    //AccountingTransactionIs.deleteAble(main, accountingTransaction); //Validation
    AppService.deleteSql(main, AccountingTransactionDetail.class, "delete from fin_accounting_transaction_detail where accounting_transaction_id=?", new Object[]{accountingTransaction.getId()});
    //  AppService.delete(main, AccountingTransaction.class, accountingTransaction.getId());
  }

  public static final AccountingTransaction selectByEntityType(Main main, AccountingTransaction accountingTransaction) {
    AccountingTransaction at = (AccountingTransaction) AppService.single(main, AccountingTransaction.class, "select * from fin_accounting_transaction where accounting_entity_type_id=? and entity_id=?", new Object[]{accountingTransaction.getAccountingEntityTypeId().getId(), accountingTransaction.getEntityId()});
    return at;
  }

  /**
   * Delete Array of AccountingTransaction.
   *
   * @param main
   * @param accountingTransaction
   */
  public static final void deleteByPkArray(Main main, AccountingTransaction[] accountingTransaction) {
    for (AccountingTransaction e : accountingTransaction) {
      deleteByPk(main, e);
    }
  }

  private static void updateSettlement(Main main, List<AccountingTransactionDetail> detailList) {
    boolean hasOpening = false;
    for (AccountingTransactionDetail detail : detailList) {
      if (AccountingConstant.LEDGER_CODE_OPENING_ENTRY.equalsIgnoreCase(detail.getAccountingLedgerId().getLedgerCode())) {
        hasOpening = true; break;
      }
    }
    int i = 0;
    for (AccountingTransactionDetail detail : detailList) {
  //    if(i == 0)
  //      main.em().update(detail); //Dummy update to start transaction
      List<AccountingTransactionSettlement> slist = AppService.list(main, AccountingTransactionSettlement.class, "select * from fin_accounting_transaction_settlement where transaction_detail_id = ?", new Object[]{detail.getId()});
      
      i++;
      updateSettlementBalance(main, slist);
      main.clear();
      if (hasOpening) {
        if (detail.getAccountingLedgerId().getOpeningBalance() == null) {
          detail.getAccountingLedgerId().setOpeningBalance(0.0);
        }
//        if (detail.getAccountingLedgerId().getOpeningBalance() != null) {
        Double val = detail.getCredit() != null ? detail.getCredit() : detail.getDebit();
        if (val != null && val > 0) {
          if (detail.getCredit() != null) {
            if (detail.getAccountingLedgerId().getIsDebitOrCredit() == AccountingConstant.IS_CREDIT) {
              if (val > detail.getAccountingLedgerId().getOpeningBalance()) {
                val = val - detail.getAccountingLedgerId().getOpeningBalance();
                detail.getAccountingLedgerId().setIsDebitOrCredit(AccountingConstant.IS_DEBIT);
              } else {
                val = detail.getAccountingLedgerId().getOpeningBalance() - val;
              }
            } else {
              val = detail.getAccountingLedgerId().getOpeningBalance() + val;
            }
          } else if (detail.getDebit() != null) {
            if (detail.getAccountingLedgerId().getIsDebitOrCredit() == AccountingConstant.IS_DEBIT) {
              if (val > detail.getAccountingLedgerId().getOpeningBalance()) {
                val = val - detail.getAccountingLedgerId().getOpeningBalance();
                detail.getAccountingLedgerId().setIsDebitOrCredit(AccountingConstant.IS_CREDIT);
              } else {
                val = detail.getAccountingLedgerId().getOpeningBalance() - val;
              }
            } else {
              val = detail.getAccountingLedgerId().getOpeningBalance() + val;
            }
          }

          if (val != null && val > 0) {
            if (val < 1) {
              main.param(null);
            } else {
              main.param(val);
            }
            main.param(detail.getAccountingLedgerId().getIsDebitOrCredit());
            main.param(detail.getAccountingLedgerId().getId());
            AppService.updateSql(main, AccountingLedger.class, "update fin_accounting_ledger set modified_by=?, modified_at = ?, opening_balance = ?, is_debit_or_credit=? where id=?", true);

          } else {
            main.param(detail.getAccountingLedgerId().getId());
            AppService.updateSql(main, AccountingLedger.class, "update fin_accounting_ledger set modified_by=?, modified_at = ?, opening_balance = null, is_debit_or_credit = null where id=?", true);
          }

        }
//          Double val = detail.getCredit() != null ? detail.getCredit() : detail.getDebit();
//          if (val != null && val > 0) {            
//            val = detail.getAccountingLedgerId().getOpeningBalance() - val;
//            if (val < 1) {
//              main.param(null);
//            } else {
//              main.param(val);
//            }
//            main.param(detail.getAccountingLedgerId().getId());
//            AppService.updateSql(main, AccountingLedger.class, "update fin_accounting_ledger set  modified_by=?, modified_at = ?, opening_balance = ? where id=?", true);
//         
//          } else {
//            main.param(detail.getAccountingLedgerId().getId());
//            AppService.updateSql(main, AccountingLedger.class, "update fin_accounting_ledger set  modified_by=?, modified_at = ?, opening_balance = null where id=?", true);
//          }
//        }
        main.clear();
      }
    }
  }

  private static void updateSettlementBalance(Main main, List<AccountingTransactionSettlement> sList) {
    if (!StringUtil.isEmpty(sList)) {
      for (AccountingTransactionSettlement ts : sList) {
        main.clear();
        main.param(ts.getSettledAmount());
        main.param(ts.getTransactionDetailItemId().getId());
        AppService.updateSql(main, AccountingTransactionDetailItem.class, "update fin_accounting_transaction_detail_item set status = 1, balance_amount=balance_amount+? where id = ?", false);
        main.clear();
        main.param(ts.getSettledAmount());
        main.param(ts.getAdjustedTransactionDetailItemId().getId());
        AppService.updateSql(main, AccountingTransactionDetailItem.class, "update fin_accounting_transaction_detail_item set status = 1, balance_amount=balance_amount+? where id = ?", false);
      }
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param accountingTransaction
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingTransaction accountingTransaction) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingTransaction
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingTransaction accountingTransaction) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_transaction where upper(transaction_seq_no)=?", new Object[]{StringUtil.toUpperCase(accountingTransaction.getTransactionSeqNo())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingTransaction
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingTransaction accountingTransaction) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_transaction where upper(transaction_seq_no)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingTransaction.getTransactionSeqNo()), accountingTransaction.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  public static AccountingTransaction selectByDebitCreditNote(Main main, DebitCreditNote debitCreditNote) {
    String sql = "select * from fin_accounting_transaction where entity_id=? and accounting_entity_type_id=? and company_id = ?";
    return (AccountingTransaction) AppService.single(main, AccountingTransaction.class, sql, new Object[]{debitCreditNote.getId(), AccountingConstant.ACC_ENTITY_DEBIT_CREDIT_NOTE.getId(), debitCreditNote.getCompanyId().getId()});
  }

  public static AccountingTransaction selectByVendorClaim(Main main, VendorClaim vendorClaim) {
    String sql = "select * from fin_accounting_transaction where entity_id=? and accounting_entity_type_id=? and company_id = ?";
    return (AccountingTransaction) AppService.single(main, AccountingTransaction.class, sql, new Object[]{vendorClaim.getId(), AccountingConstant.ACC_ENTITY_VENDOR_CLAIM.getId(), vendorClaim.getCompanyId().getId()});
  }

  public static List<AccountingTransaction> selectByVendorClaimList(Main main, VendorClaim vendorClaim) {
    String sql = "select * from fin_accounting_transaction where entity_id=? and accounting_entity_type_id=? and company_id = ?";
    return AppService.list(main, AccountingTransaction.class, sql, new Object[]{vendorClaim.getId(), AccountingConstant.ACC_ENTITY_VENDOR_CLAIM.getId(), vendorClaim.getCompanyId().getId()});
  }

  private void addDetailItem(AccountingTransaction tran) {
    List<AccountingTransactionDetailItem> itemList;
    for (AccountingTransactionDetail detail : tran.getTransactionDetail()) {
      if (detail.getDetailItem() == null) {
        if (tran.isJournal() && detail.getCredit() != null) {
          itemList = new ArrayList<>();
          AccountingTransactionDetailItem item = new AccountingTransactionDetailItem();
          item.setAccountingTransactionDetailId(detail);
          item.setNetAmount(detail.getCredit());
          item.setBalanceAmount(detail.getCredit());
          itemList.add(item);
//            detail.setDetailItem(itemList);
        }
      }
    }
  }
}
