/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.fin.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import spica.scm.common.InvoiceGroup;
import spica.fin.domain.AccountingDocType;
import spica.fin.domain.AccountingEntityType;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.domain.AccountingVoucherType;
import spica.scm.domain.Company;
import spica.scm.domain.Currency;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.SalesAgentClaim;
import spica.scm.domain.SalesAgentClaimDetail;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesServicesInvoice;
import spica.fin.domain.TaxCode;
import wawo.app.Main;
import wawo.app.common.AppService;

import spica.constant.AccountingConstant;
import spica.fin.domain.AccountExpenseDetail;
import spica.fin.domain.AccountingBankCharges;
import spica.fin.domain.AccountingChequeBounce;
import spica.fin.domain.AccountingTransactionSettlement;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.TradeOutstanding;
import spica.fin.domain.VendorClaim;
import spica.fin.domain.VendorClaimDetail;
import spica.scm.domain.Account;
import spica.scm.service.AccountService;
import spica.scm.service.CustomerService;
import spica.sys.ClaimConstants;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sanith
 */
public abstract class LedgerExternalDataService {

  public static void deleteSalesReturn(Main main, SalesReturn invoice) {
    delete(main, AccountingConstant.ACC_ENTITY_SALES_RETURN.getId(), invoice.getId());
  }

  public static void deleteSalesInvoice(Main main, SalesInvoice invoice) {
    delete(main, AccountingConstant.ACC_ENTITY_SALES.getId(), invoice.getId());
    delete(main, AccountingConstant.ACC_ENTITY_SALES_EXPENSES.getId(), invoice.getId());
  }

  public static void deleteSalesServicesInvoice(Main main, SalesServicesInvoice invoice) {
    delete(main, AccountingConstant.ACC_ENTITY_SALES_SERVICES_INVOICE.getId(), invoice.getId());
  }

  public static void deletePurchaseReturn(Main main, PurchaseReturn invoice) {
    delete(main, AccountingConstant.ACC_ENTITY_PURCHASE_RETURN.getId(), invoice.getId());
  }

  public static void deleteProductEntry(Main main, ProductEntry invoice) {
    delete(main, AccountingConstant.ACC_ENTITY_PURCHASE.getId(), invoice.getId());
  }

  public static void deleteChequebounce(Main main, AccountingChequeBounce chequeBounce) {
    BankReconciliationService.updateBankChequeIssued(main, chequeBounce);
    delete(main, AccountingConstant.ACC_ENTITY_CHEQUE_BOUNCE.getId(), chequeBounce.getId());
  }

  public static void deleteBankCharges(Main main, AccountingBankCharges bankCharges) {
    delete(main, AccountingConstant.ACC_ENTITY_BANK_CHARGES.getId(), bankCharges.getId());
  }

  public static void delete(Main main, Integer entityTypeId, Integer entityId) {
    Object[] param = new Object[]{entityTypeId, entityId};
    List<AccountingTransaction> tranList
            = (List<AccountingTransaction>) AppService.list(main, AccountingTransaction.class,
                    "select id from fin_accounting_transaction where accounting_entity_type_id = ? and entity_id = ?", param);
    if (tranList != null) {
      for (AccountingTransaction tran : tranList) {
        AccountingTransactionService.deleteByPk(main, tran);
      }
    }
    AppService.deleteSql(main, TradeOutstanding.class, "delete from fin_trade_outstanding where entity_type_id = ? and entity_id = ? ", param);
    //  AppService.deleteSql(main, TradeOutstandingTax.class, "delete from fin_trade_outstanding where entity_type_id = ? and entity_id = ?", param);
  }

  public static final AccountingTransaction getTransactionBouncePayment(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_PAYMENT, entityId, AccountingConstant.ACC_ENTITY_CHEQUE_BOUNCE, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionBounceReverseJournal(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_JOURNAL, entityId, AccountingConstant.ACC_ENTITY_CHEQUE_BOUNCE, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionBounce(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_EXPENSE, entityId, AccountingConstant.ACC_ENTITY_CHEQUE_BOUNCE, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionBounceDebitNote(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_DEBIT_NOTE, entityId, AccountingConstant.ACC_ENTITY_CHEQUE_BOUNCE, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionBounceCreditNote(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_CREDIT_NOTE, entityId, AccountingConstant.ACC_ENTITY_CHEQUE_BOUNCE, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionExpenseBankCharge(Main main, Integer entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_PAYMENT, entityId, AccountingConstant.ACC_ENTITY_BANK_CHARGES, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionExpensePayment(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_PAYMENT, entityId, AccountingConstant.ACC_ENTITY_EXPENSES, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionExpense(Main main, Integer entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_EXPENSE, entityId, AccountingConstant.ACC_ENTITY_EXPENSES, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionSalesExpense(Main main, Integer entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_EXPENSE, entityId, AccountingConstant.ACC_ENTITY_SALES_EXPENSES, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionExpenseDebitNote(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_DEBIT_NOTE, entityId, AccountingConstant.ACC_ENTITY_EXPENSES, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionExpenseCreditNote(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_CREDIT_NOTE, entityId, AccountingConstant.ACC_ENTITY_EXPENSES, "", entryDate, currency, company);
  }

  private static final AccountingTransaction getTransactionPurchase(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_PURCHASE, entityId, AccountingConstant.ACC_ENTITY_PURCHASE, "", entryDate, currency, company);
  }

  private static final AccountingTransaction getTransactionPurchaseReturn(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_DEBIT_NOTE, entityId, AccountingConstant.ACC_ENTITY_PURCHASE_RETURN, "", entryDate, currency, company);
  }

  private static final AccountingTransaction getTransactionSales(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_SALES, entityId, AccountingConstant.ACC_ENTITY_SALES, "", entryDate, currency, company);
  }

  private static final AccountingTransaction getTransactionSalesReturn(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_CREDIT_NOTE, entityId, AccountingConstant.ACC_ENTITY_SALES_RETURN, "", entryDate, currency, company);
  }

  private static final AccountingTransaction getTransactionSalesServices(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_SALES, entityId, AccountingConstant.ACC_ENTITY_SALES_SERVICES_INVOICE, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionSelfAdjust(Main main, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_JOURNAL, null, null, "", entryDate, currency, company);
  }

  public static final AccountingTransaction getTransactionClaimExpense(Main main, Integer entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_EXPENSE, entityId, AccountingConstant.ACC_ENTITY_VENDOR_CLAIM, "", entryDate, currency, company);
  }

  private static final AccountingTransaction getTransactionNew(Main main, AccountingVoucherType voucherType, Integer entityId, AccountingEntityType entityType, String documentNumber, Date entryDate, Currency currency, Company company) {
    AccountingTransaction at = new AccountingTransaction();
    at.setCurrencyId(currency);
    at.setEntryDate(entryDate);
    at.setVoucherTypeId(voucherType);
    at.setEntityId(entityId);
    at.setNarration(documentNumber);
    at.setAccountingEntityTypeId(entityType);
    at.setCompanyId(company);
    return at;
  }

  public static final AccountingTransactionDetail getTransactionDetailVendor(Main main, AccountingTransaction accountingTransaction, Integer vendorId, Double debit, Double credit, Integer companyId) {
    return getTransactionDetail(main, AccountingConstant.ACC_ENTITY_VENDOR, vendorId, accountingTransaction, debit, credit, companyId);
  }

  public static final AccountingTransactionDetail getTransactionDetailCustomer(Main main, AccountingTransaction accountingTransaction, Integer vendorId, Double debit, Double credit, Integer companyId) {
    return getTransactionDetail(main, AccountingConstant.ACC_ENTITY_CUSTOMER, vendorId, accountingTransaction, debit, credit, companyId);
  }

  public static final void computeTax(AccountingTransactionDetail transactionDetail, Double debit, Double credit) {
    _computeTax(transactionDetail, debit, credit, false);
  }

  public static final void computeTaxInclusive(AccountingTransactionDetail transactionDetail, Double debit, Double credit) {
    _computeTax(transactionDetail, debit, credit, true);
  }

  private static final void _computeTax(AccountingTransactionDetail transactionDetail, Double debit, Double credit, boolean inclusive) {
    if (transactionDetail.getTaxCodeId() != null) {
      if (debit != null && debit > 0) {
        transactionDetail.setDebit(_tax(debit, transactionDetail.getTaxCodeId().getRatePercentage(), inclusive));
      }
      if (credit != null && credit > 0) {
        transactionDetail.setCredit(_tax(credit, transactionDetail.getTaxCodeId().getRatePercentage(), inclusive));
      }
    }
  }

  private static Double _tax(Double value, Double percentage, boolean inclusive) {
    if (value == null || percentage == null || percentage == 0) {
      return null;
    }
    if (inclusive) {
      return value - (value / (1 + (percentage / 100)));
    } else {
      return (value * percentage) / 100;
    }
  }

  public static final AccountingTransactionDetail getTransactionDetailTaxComputed(Main main, AccountingTransaction accountingTransaction, TaxCode taxCode, Double debit, Double credit, Integer companyId, String ledgerCode) {
    AccountingTransactionDetail detail = getTransactionDetailGst(main, accountingTransaction, taxCode, null, null, companyId, ledgerCode);
    computeTax(detail, debit, credit);
    return detail;
  }

  public static final AccountingTransactionDetail getTransactionDetailGstInput(Main main, AccountingTransaction accountingTransaction, TaxCode taxCodeId, Double debit, Double credit, Integer companyId) {
    return getTransactionDetailGst(main, accountingTransaction, taxCodeId, debit, credit, companyId, AccountingConstant.LEDGER_CODE_TAX_INPUT);
  }

  public static final AccountingTransactionDetail getTransactionDetailGstOutput(Main main, AccountingTransaction accountingTransaction, TaxCode taxCodeId, Double debit, Double credit, Integer companyId) {
    return getTransactionDetailGst(main, accountingTransaction, taxCodeId, debit, credit, companyId, AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
  }

  private static final AccountingTransactionDetail getTransactionDetailGst(Main main, AccountingTransaction accountingTransaction, TaxCode taxCodeId, Double debit, Double credit, Integer companyId, String ledgerCode) {
    if (taxCodeId == null) {
      return null;
    }
    AccountingTransactionDetail detail = getTransactionDetailTax(main, AccountingConstant.ACC_ENTITY_TAX, taxCodeId.getId(), accountingTransaction, debit, credit, companyId, ledgerCode);
    detail.setTaxCodeId(taxCodeId);
    return detail;
  }

  private static AccountingTransactionDetail getTransactionDetailTax(Main main, AccountingEntityType entityType, Integer entityId, AccountingTransaction accountingTransaction, Double debit, Double credit, Integer companyId, String ledgerCode) {
    AccountingLedger al = AccountingLedgerService.selectLedgerByEntityAndCode(main, entityId, entityType, companyId, ledgerCode);
    return getTransactionDetail(main, accountingTransaction, al, debit, credit);
  }

  private static AccountingTransactionDetail getTransactionDetail(Main main, AccountingEntityType entityType, Integer entityId, AccountingTransaction accountingTransaction, Double debit, Double credit, Integer companyId) {
    AccountingLedger al = AccountingLedgerService.selectLedgerByEntity(main, entityId, entityType, companyId);
    return getTransactionDetail(main, accountingTransaction, al, debit, credit);
  }

  public static final AccountingTransactionDetail getTransactionDetailDebitRoundOff(Main main, AccountingTransaction accountingTransaction, Double debit, Double credit, Company company) {
    AccountingLedger roundOffLedger = AccountingLedgerService.selectAccoutingLedgerByCompanyAndLedgerCode(main, company, AccountingConstant.LEDGER_CODE_PURCHASE_ROUND_OFF);
    return getTransactionDetail(main, accountingTransaction, roundOffLedger, debit, credit);
  }

  public static final AccountingTransactionDetail getTransactionDetailCreditRoundOff(Main main, AccountingTransaction accountingTransaction, Double debit, Double credit, Company company) {
    AccountingLedger roundOffLedger = AccountingLedgerService.selectAccoutingLedgerByCompanyAndLedgerCode(main, company, AccountingConstant.LEDGER_CODE_SALES_ROUND_OFF);
    return getTransactionDetail(main, accountingTransaction, roundOffLedger, debit, credit);
  }

  public static final AccountingTransactionDetail getTransactionDetail(Main main, AccountingTransaction accountingTransaction, AccountingLedger ledger, Double debit, Double credit) {

    int id = 0;
    if (accountingTransaction.getId() == null) {
      id = 0;
    } else {
      id = accountingTransaction.getId();
    }
    AccountingTransactionDetail atd = (AccountingTransactionDetail) AppService.single(main, AccountingTransactionDetail.class, "select * from fin_accounting_transaction_detail where accounting_transaction_id=? and accounting_ledger_id=?", new Object[]{id, ledger.getId()});
    if (atd == null) {
      return getTransactionDetailNew(accountingTransaction, ledger, debit, credit);
    }
    return getTransactionDetails(accountingTransaction, atd, ledger, debit, credit);
  }

  public static final AccountingTransactionDetail getTransactionDetailNew(AccountingTransaction accountingTransaction, AccountingLedger ledger, Double debit, Double credit) {
    return getTransactionDetails(accountingTransaction, new AccountingTransactionDetail(), ledger, debit, credit);
  }

  private static final AccountingTransactionDetail getTransactionDetails(AccountingTransaction accountingTransaction, AccountingTransactionDetail atd, AccountingLedger ledger, Double debit, Double credit) {
    atd.setAccountingTransactionId(accountingTransaction);
    atd.setAccountingLedgerId(ledger);
    atd.setEntryDate(accountingTransaction.getEntryDate());
    atd.setDebit(debit);
    atd.setCredit(credit);
    accountingTransaction.addTransactionDetail(atd);
    return atd;
  }

  public static final AccountingTransactionDetailItem getDetailItemExpensePayable(AccountingTransactionDetail detail, Double netamount, Double goods, Double balance, Double discount, Double tax) {
    return getDetailItemNew(detail, AccountingConstant.DOC_TYPE_EXPENSE_PAYABLE_BILL, netamount, goods, balance, discount, tax);
  }

  public static final AccountingTransactionDetailItem getDetailItemExpenseReceivable(AccountingTransactionDetail detail, Double netamount, Double goods, Double balance, Double discount, Double tax) {
    return getDetailItemNew(detail, AccountingConstant.DOC_TYPE_EXPENSE_RECEIVABLE_BILL, netamount, goods, balance, discount, tax);
  }

  public static final AccountingTransactionDetailItem getDetailItemCreditNote(AccountingTransactionDetail detail, Double netamount, Double goods, Double balance, Double discount, Double tax) {
    return getDetailItemNew(detail, AccountingConstant.DOC_TYPE_CREDIT_NOTE, netamount, goods, balance, discount, tax);
  }

  public static final AccountingTransactionDetailItem getDetailItemDebitNote(AccountingTransactionDetail detail, Double netamount, Double goods, Double balance, Double discount, Double tax) {
    return getDetailItemNew(detail, AccountingConstant.DOC_TYPE_DEBIT_NOTE, netamount, goods, balance, discount, tax);
  }

  public static final AccountingTransactionDetailItem getDetailItemChequeReceived(AccountingTransactionDetail detail, Double netamount, Double goods, Double balance, Double discount, Double tax) {
    return getDetailItemNew(detail, AccountingConstant.DOC_TYPE_RECEIPT_CHEQUE, netamount, goods, balance, discount, tax);
  }

  public static final AccountingTransactionDetailItem getDetailItemChequePaid(AccountingTransactionDetail detail, Double netamount, Double goods, Double balance, Double discount, Double tax) {
    return getDetailItemNew(detail, AccountingConstant.DOC_TYPE_PAYMENT_CHEQUE, netamount, goods, balance, discount, tax);
  }

  public static final AccountingTransactionDetailItem getDetailItemNew(AccountingTransactionDetail detail, AccountingDocType docType, Double netamount, Double goods, Double balance, Double discount, Double tax) {
    AccountingTransactionDetailItem item = new AccountingTransactionDetailItem();
    item.setAccountingTransactionDetailId(detail);
    if (balance == null) {
      balance = netamount;
    }
    item.setBalanceAmount(balance);
    item.setNetAmount(netamount);
    if (goods == null) {
      goods = netamount;
    }
    item.setGoodsAmount(goods);
    item.setDiscountAmount(discount);
    item.setTaxAmount(tax);

    if (item.getRecordType() == null) {
      AccountingTransaction tran = detail.getAccountingTransactionId();
      if (tran.isPurchase() || tran.isExpenses() || tran.isCreditNote()) {
        //if sales expense then receivable
        if (tran.getAccountingEntityTypeId() != null && AccountingConstant.ACC_ENTITY_SALES_EXPENSES.getId().intValue() == tran.getAccountingEntityTypeId().getId()) {
          item.setRecordType(AccountingConstant.RECORD_TYPE_RECEIVABLE);
        } else {
          item.setRecordType(AccountingConstant.RECORD_TYPE_PAYABLE);
        }
      } else if (tran.isSales() || tran.isDebitNote()) {
        item.setRecordType(AccountingConstant.RECORD_TYPE_RECEIVABLE);
      } else if (tran.isReceipt()) {
        item.setRecordType(AccountingConstant.RECORD_TYPE_RECEIVED);
      } else if (tran.isPayment()) {
        item.setRecordType(AccountingConstant.RECORD_TYPE_PAYED);
      }
    }
    item.setStatus(AccountingConstant.STATUS_NEW);
    item.setDocumentTypeId(docType); // CHECK
    detail.addDetailItem(item);
    return item;
  }

  public static void savePurchase(Main main, ProductEntry productEntry, Currency currency, Company company) {
    if (!isValid(company)) {
      return;
    }
    AccountingTransaction tran = getTransactionPurchase(main, productEntry.getId(), productEntry.getProductEntryDate(), currency, company);
    getTransactionDetailVendor(main, tran, productEntry.getAccountId().getVendorId().getId(), null, productEntry.getInvoiceValue(), productEntry.getAccountId().getCompanyId().getId());

    if (productEntry.getCashDiscountValue() != null && productEntry.getCashDiscountTaxable() != null && !productEntry.getCashDiscountTaxable().equals(SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE)) {
      AccountingLedger cashDiscountRecieved = AccountingLedgerService.selectAccoutingLedgerByCompanyAndLedgerCode(main, company, AccountingConstant.LEDGER_CODE_CASH_DISCOUNT_RECIEVED);
      getTransactionDetail(main, tran, cashDiscountRecieved, null, productEntry.getCashDiscountValue());
    }

    getTransactionDetail(main, tran, productEntry.getAccountId().getPurchaseLedgerId(), productEntry.getAssessableValue() - ((productEntry.getCashDiscountTaxable() != null && productEntry.getCashDiscountTaxable() == 1) ? 0 : productEntry.getCashDiscountValue() != null ? productEntry.getCashDiscountValue() : 0), null);
    addTaxAndRoundOff(main, tran, productEntry.getInvoiceGroup(), (productEntry.getInvoiceRoundOff() + (productEntry.getInvoiceAmountVariation() != null ? productEntry.getInvoiceAmountVariation() : 0)), productEntry.getAccountId().getCompanyId(), AccountingConstant.LEDGER_CODE_TAX_INPUT);
    cleanupIfExist(main, tran);
    setFirstAsCredit(tran);
    tran.setAccountGroupId(productEntry.getAccountGroupId());
    tran.setNarration(productEntry.getInvoiceNo());
    tran.setRefDocumentNo(productEntry.getAccountInvoiceNo());
    for (AccountingTransactionDetail atd : tran.getTransactionDetail()) {
      atd.setEntryDate(productEntry.getProductEntryDate());
      if (tran.getVoucherTypeId().getId() == AccountingConstant.VOUCHER_TYPE_PURCHASE.getId().intValue()) {
        if (atd.getCredit() != null) {
          AccountingTransactionDetailItem item = getDetailItemNew(atd, AccountingConstant.DOC_TYPE_PURCHASE_BILL, atd.getCredit(), productEntry.getInvoiceAmountNet(), atd.getCredit(), productEntry.getInvoiceDiscountValue(), productEntry.getInvoiceAmountIgst());
          item.setDocumentDate(productEntry.getProductEntryDate());
          item.setDocumentNumber(productEntry.getAccountInvoiceNo());
          item.setReferNumber(productEntry.getInvoiceNo());
        }
      }
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);
    TradeOutstandingService.insertProductEntryByAccount(main, productEntry, tran);
  }

  public static void savePurchaseReturn(Main main, PurchaseReturn purchaseReturn, Currency currency, Company company) {
    if (!isValid(company)) {
      return;
    }
    AccountingTransaction tran = getTransactionPurchaseReturn(main, purchaseReturn.getId(), purchaseReturn.getEntryDate(), currency, company);
    getTransactionDetailVendor(main, tran, purchaseReturn.getAccountId().getVendorId().getId(), purchaseReturn.getInvoiceAmount(), null, purchaseReturn.getAccountId().getCompanyId().getId());
    getTransactionDetail(main, tran, purchaseReturn.getAccountingLedgerId(), null, purchaseReturn.getInvoiceAmountNet());//have to check
    addTaxAndRoundOff(main, tran, purchaseReturn.getInvoiceGroup(), purchaseReturn.getInvoiceRoundOff(), purchaseReturn.getAccountId().getCompanyId(), AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
    cleanupIfExist(main, tran);
    setFirstAsDebit(tran);
    tran.setNarration(purchaseReturn.getInvoiceNo());
    tran.setRefDocumentNo(purchaseReturn.getInvoiceNo());
    tran.setAccountGroupId(purchaseReturn.getAccountGroup());
    for (AccountingTransactionDetail atd : tran.getTransactionDetail()) {
      atd.setEntryDate(purchaseReturn.getEntryDate());
      if (tran.getVoucherTypeId().getId() == AccountingConstant.VOUCHER_TYPE_DEBIT_NOTE.getId().intValue()) {
        if (atd.getDebit() != null) {
          AccountingTransactionDetailItem item = getDetailItemNew(atd, AccountingConstant.DOC_TYPE_DEBIT_NOTE, atd.getDebit(), purchaseReturn.getInvoiceAmountNet(), atd.getDebit(), purchaseReturn.getInvoiceDiscountValue(), purchaseReturn.getInvoiceAmountIgst());
          item.setDocumentDate(purchaseReturn.getEntryDate());
          item.setDocumentNumber(purchaseReturn.getInvoiceNo());
        }
      }
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);
    TradeOutstandingService.insertPurchaseReturnByAccount(main, purchaseReturn, tran);
  }

  public static void saveSalesCancel(Main main, SalesInvoice salesInvoice, Currency currency, Company company) {
    if (!isValid(company)) {
      return;
    }
    AccountingTransaction tran = getTransactionSales(main, salesInvoice.getId(), salesInvoice.getInvoiceEntryDate(), currency, company);
    tran.setAccountGroupId(salesInvoice.getAccountGroupId());
    Double total = setCessAndFrieghtExpense(main, salesInvoice, currency, company, true);
    getTransactionDetailCustomer(main, tran, salesInvoice.getCustomerId().getId(), null, total, salesInvoice.getCustomerId().getCompanyId().getId());
    if (salesInvoice.getCashDiscountValue() != null && salesInvoice.getCashDiscountTaxable() != null && !salesInvoice.getCashDiscountTaxable().equals(SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE)) {
      AccountingLedger cashDiscountAllowed = AccountingLedgerService.selectAccoutingLedgerByCompanyAndLedgerCode(main, company, AccountingConstant.LEDGER_CODE_CASH_DISCOUNT_ALLOWED);
      getTransactionDetail(main, tran, cashDiscountAllowed, null, salesInvoice.getCashDiscountValue());
    }
    getTransactionDetail(main, tran, salesInvoice.getCustomerId().getSalesLedgerId(), salesInvoice.getAssessableValue(), null);
    addTaxAndRoundOffReverse(main, tran, salesInvoice.getInvoiceGroup(), salesInvoice.getInvoiceRoundOff(), salesInvoice.getCustomerId().getCompanyId(), AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
    setFirstAsCredit(tran);
    tran.setNarration(salesInvoice.getInvoiceNo() + " [Cancelled]");
    tran.setRefDocumentNo(salesInvoice.getInvoiceNo());
    for (AccountingTransactionDetail atd : tran.getTransactionDetail()) {
      atd.setEntryDate(salesInvoice.getInvoiceEntryDate());
      if (tran.getVoucherTypeId().getId() == AccountingConstant.VOUCHER_TYPE_SALES.getId().intValue()) {
        if (atd.getCredit() != null) {
          AccountingTransactionDetailItem item = getDetailItemNew(atd, AccountingConstant.DOC_TYPE_SALES_BILL, atd.getCredit(), salesInvoice.getInvoiceAmountNet(), atd.getCredit(), salesInvoice.getInvoiceDiscountValue(), salesInvoice.getInvoiceAmountIgst());
          item.setDocumentDate(salesInvoice.getInvoiceEntryDate());
          item.setDocumentNumber(salesInvoice.getInvoiceNo());
          item.setRecordType(AccountingConstant.RECORD_TYPE_PAYABLE); //Forcing as payable since its cancel
        }
      }
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);
    TradeOutstandingService.insertSalesCancelByAccount(main, salesInvoice, tran);
  }

  public static void saveSales(Main main, SalesInvoice salesInvoice, Currency currency, Company company) {
    if (!isValid(company)) {
      return;
    }
    AccountingTransaction tran = getTransactionSales(main, salesInvoice.getId(), salesInvoice.getInvoiceEntryDate(), currency, company);
    tran.setAccountGroupId(salesInvoice.getAccountGroupId());
    Double total = setCessAndFrieghtExpense(main, salesInvoice, currency, company, false);
    getTransactionDetailCustomer(main, tran, salesInvoice.getCustomerId().getId(), total, null, salesInvoice.getCustomerId().getCompanyId().getId());
    if (salesInvoice.getCashDiscountValue() != null && salesInvoice.getCashDiscountTaxable() != null && !salesInvoice.getCashDiscountTaxable().equals(SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE)) {
      AccountingLedger cashDiscountAllowed = AccountingLedgerService.selectAccoutingLedgerByCompanyAndLedgerCode(main, company, AccountingConstant.LEDGER_CODE_CASH_DISCOUNT_ALLOWED);
      getTransactionDetail(main, tran, cashDiscountAllowed, salesInvoice.getCashDiscountValue(), null);
    }
    getTransactionDetail(main, tran, salesInvoice.getCustomerId().getSalesLedgerId(), null, salesInvoice.getAssessableValue() - ((salesInvoice.getCashDiscountTaxable() != null && salesInvoice.getCashDiscountTaxable() == 1) ? 0 : salesInvoice.getCashDiscountValue() != null ? salesInvoice.getCashDiscountValue() : 0));
    addTaxAndRoundOff(main, tran, salesInvoice.getInvoiceGroup(), salesInvoice.getInvoiceRoundOff(), salesInvoice.getCustomerId().getCompanyId(), AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
    setFirstAsDebit(tran);
    tran.setNarration(salesInvoice.getInvoiceNo());
    tran.setRefDocumentNo(salesInvoice.getInvoiceNo());
    for (AccountingTransactionDetail atd : tran.getTransactionDetail()) {
      atd.setEntryDate(salesInvoice.getInvoiceEntryDate());
      if (tran.getVoucherTypeId().getId() == AccountingConstant.VOUCHER_TYPE_SALES.getId().intValue()) {
        if (atd.getDebit() != null) {
          AccountingTransactionDetailItem item = getDetailItemNew(atd, AccountingConstant.DOC_TYPE_SALES_BILL, atd.getDebit(), salesInvoice.getInvoiceAmountNet(), atd.getDebit(), salesInvoice.getInvoiceDiscountValue(), salesInvoice.getInvoiceAmountIgst());
          item.setDocumentDate(salesInvoice.getInvoiceEntryDate());
          item.setDocumentNumber(salesInvoice.getInvoiceNo());
        }
      }
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);
    TradeOutstandingService.insertSalesByAccount(main, salesInvoice, tran);
  }

  private static Double setCessAndFrieghtExpense(Main main, SalesInvoice salesInvoice, Currency currency, Company company, boolean canceled) {
    //for frieght/service value
    Double total = salesInvoice.getInvoiceValue();
    Double service1Cess = null;
    Double service2Cess = null;
    if (StringUtil.equalsInt(salesInvoice.getServiceAsExpense(), SystemConstants.SERVICE_AS_EXPENSE)) { // Reduce frieght and tax
      if (salesInvoice.getFreightRate() != null || salesInvoice.getService2Rate() != null) {
        if (salesInvoice.getFreightRate() != null) {     //First Expense
          total -= salesInvoice.getFreightRate();
          if (salesInvoice.getKeralaFloodCessTaxCodeId() != null) {
            service1Cess = _tax(salesInvoice.getFreightRate(), salesInvoice.getKeralaFloodCessTaxCodeId().getRatePercentage(), false);
            total -= service1Cess;
          }
          if (salesInvoice.getServiceValueIgst() != null) {
            total -= salesInvoice.getServiceValueIgst();
          }
          saveSalesExpense(main, salesInvoice, currency, company, service1Cess, canceled, salesInvoice.getAccountGroupId().getExpenseLedgerId(), salesInvoice.getFreightRate(),
                  salesInvoice.getServiceValueIgst(), salesInvoice.getServiceValueCgst(), salesInvoice.getServiceValueSgst(), salesInvoice.getServiceTaxCodeId());
        }
        if (salesInvoice.getService2Rate() != null) {    //Second expense
          total -= salesInvoice.getService2Rate();
          if (salesInvoice.getKeralaFloodCessTaxCodeId() != null) {
            service2Cess = _tax(salesInvoice.getService2Rate(), salesInvoice.getKeralaFloodCessTaxCodeId().getRatePercentage(), false);
            total -= service2Cess;
          }
          if (salesInvoice.getService2Igst() != null) {
            total -= salesInvoice.getService2Igst();
          }
          saveSalesExpense(main, salesInvoice, currency, company, service2Cess, canceled, salesInvoice.getAccountGroupId().getService2LedgerId(), salesInvoice.getService2Rate(),
                  salesInvoice.getService2Igst(), salesInvoice.getService2Cgst(), salesInvoice.getService2Sgst(), salesInvoice.getService2TaxCodeId());
        }
        //  salesInvoice.getAccountGroupId().getExpenseLedgerId()
      } else if (StringUtil.equalsInt(salesInvoice.getServiceAsExpense(), SystemConstants.SERVICE_AS_NON_EXPENSE)) { //Adding service tax to tax in sales
        if (salesInvoice.getServiceValueIgst() != null) {
          boolean found = true;
          for (InvoiceGroup ig : salesInvoice.getInvoiceGroup()) {
            if (ig.getTaxCode() != null && salesInvoice.getServiceTaxCodeId() != null) {
              if (StringUtil.equalsInt(ig.getTaxCode().getId(), salesInvoice.getServiceTaxCodeId().getId())) {
                found = true;
                ig.setInvoiceIgstValue(ig.getInvoiceIgstValue() + salesInvoice.getServiceValueIgst());
                if (ig.getInvoiceCgstValue() != null) {
                  ig.setInvoiceCgstValue(ig.getInvoiceCgstValue() + salesInvoice.getServiceValueCgst());
                } else {
                  ig.setInvoiceCgstValue(salesInvoice.getServiceValueCgst());
                }
                if (ig.getInvoiceSgstValue() != null) {
                  ig.setInvoiceSgstValue(ig.getInvoiceSgstValue() + salesInvoice.getServiceValueSgst());
                } else {
                  ig.setInvoiceSgstValue(salesInvoice.getServiceValueSgst());
                }
                break;
              }
            }
          }
          if (!found) {
            InvoiceGroup ig = new InvoiceGroup();
            ig.setAssessableValue(salesInvoice.getFreightRate());
            ig.setTaxCode(salesInvoice.getServiceTaxCodeId());
            ig.setInvoiceIgstValue(salesInvoice.getServiceValueIgst());
            ig.setInvoiceSgstValue(salesInvoice.getServiceValueSgst());
            ig.setInvoiceCgstValue(salesInvoice.getServiceValueCgst());
            salesInvoice.getInvoiceGroup().add(ig);
          }

        }
      }
    }
    //cess posting to sales
    if (StringUtil.gtDouble(salesInvoice.getKeralaFloodCessNetValue(), 0.0)) {

      InvoiceGroup ig = new InvoiceGroup();
      ig.setAssessableValue(salesInvoice.getInvoiceValue());//TODO now where to get cess accesable value
      ig.setTaxCode(salesInvoice.getKeralaFloodCessTaxCodeId());
      Double cess = salesInvoice.getKeralaFloodCessNetValue();
      ig.setInvoiceCessValue(cess);
      salesInvoice.getInvoiceGroup().add(ig);
    }
//    TCS posting to Sales
    if (StringUtil.gtDouble(salesInvoice.getTcsNetValue(), 0.0)) {

      InvoiceGroup ig = new InvoiceGroup();
      ig.setAssessableValue(salesInvoice.getInvoiceValue());
      ig.setTaxCode(salesInvoice.getTcsTaxCodeId());
      Double tcs = salesInvoice.getTcsNetValue();
      ig.setInvoiceTcsValue(tcs);
      salesInvoice.getInvoiceGroup().add(ig);
    }
    return total;
  }

  private static void saveSalesExpense(Main main, SalesInvoice si, Currency currency, Company company, Double cess, boolean canceled, AccountingLedger ledgerId, Double serviceRate, Double serviceIgst, Double serviceCgst, Double serviceSgst, TaxCode serviceTaxCodeId) {
    if (ledgerId == null) {
      throw new UserMessageException("choose.expense.ledger");
    }
    AccountingTransaction tran = LedgerExternalDataService.getTransactionSalesExpense(main, si.getId(), si.getInvoiceEntryDate(), currency, company);
    if (canceled) {
      tran.setNarration(si.getInvoiceNo() + " Sales Expenses [Canceled]");
    } else {
      tran.setNarration(si.getInvoiceNo() + " Sales Expenses");
    }

    tran.setRefDocumentNo(si.getInvoiceNo());

    tran.setAccountGroupId(si.getAccountGroupId());
    Double total = serviceRate;
    total += serviceIgst != null ? serviceIgst : 0;
    total += cess != null ? cess : 0;

    AccountingTransactionDetail receivableDetail;
    AccountingTransactionDetail taxDetail;
    AccountingTransactionDetailItem item;
    if (canceled) {
      receivableDetail = getTransactionDetailCustomer(main, tran, si.getCustomerId().getId(), null, total, si.getCustomerId().getCompanyId().getId());
      taxDetail = LedgerExternalDataService.getTransactionDetailNew(tran, ledgerId, serviceIgst != null ? serviceRate : total, null);
      LedgerExternalDataService.setFirstAsCredit(tran);
      item = LedgerExternalDataService.getDetailItemExpensePayable(receivableDetail, total, serviceRate, total, null, serviceIgst);
      item.setRecordType(AccountingConstant.RECORD_TYPE_PAYABLE); //Forcing as payable since its cancel
    } else {
      receivableDetail = getTransactionDetailCustomer(main, tran, si.getCustomerId().getId(), total, null, si.getCustomerId().getCompanyId().getId());
      taxDetail = LedgerExternalDataService.getTransactionDetailNew(tran, ledgerId, null, serviceIgst != null ? serviceRate : total);
      LedgerExternalDataService.setFirstAsDebit(tran);
      item = LedgerExternalDataService.getDetailItemExpenseReceivable(receivableDetail, total, serviceRate, total, null, serviceIgst);
    }

    item.setDocumentDate(si.getInvoiceEntryDate());
    //getAccountExpense().getConsignmentPurchaseId() == null ? getAccountExpense().getConsignmentSalesId().getConsignmentNo() : getAccountExpense().getConsignmentPurchaseId().getConsignmentNo()
    item.setDocumentNumber(si.getInvoiceNo()); //TODO this should be bill no from above
    if (serviceIgst != null) {
      addTaxAndSub(main, tran, serviceTaxCodeId, canceled ? true : false, AccountingConstant.LEDGER_CODE_TAX_OUTPUT, serviceIgst, serviceCgst, serviceSgst, null, null);
      addTaxAndSub(main, tran, si.getKeralaFloodCessTaxCodeId(), canceled ? true : false, AccountingConstant.LEDGER_CODE_TAX_OUTPUT, null, null, null, cess, null);
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);

  }

  public static void saveSalesReturn(Main main, SalesReturn salesReturn, Currency currency, Company company) {
    if (!isValid(company)) {
      return;
    }
    AccountingTransaction tran = getTransactionSalesReturn(main, salesReturn.getId(), salesReturn.getEntryDate(), currency, company);
    tran.setAccountGroupId(salesReturn.getAccountGroupId());
    getTransactionDetailCustomer(main, tran, salesReturn.getCustomerId().getId(), null, salesReturn.getInvoiceValue(), salesReturn.getCompanyId().getId());
    getTransactionDetail(main, tran, salesReturn.getAccountingLedgerId(), salesReturn.getAssessableValue(), null);
    addTaxAndRoundOff(main, tran, salesReturn.getInvoiceGroup(), salesReturn.getInvoiceRoundOff(), salesReturn.getCompanyId(), AccountingConstant.LEDGER_CODE_TAX_INPUT);
    cleanupIfExist(main, tran);
    setFirstAsCredit(tran);
    tran.setNarration(salesReturn.getInvoiceNo());
    tran.setRefDocumentNo(salesReturn.getInvoiceNo());
    for (AccountingTransactionDetail atd : tran.getTransactionDetail()) {
      atd.setEntryDate(salesReturn.getEntryDate());
      if (atd.getCredit() != null && tran.getVoucherTypeId().getId() == AccountingConstant.VOUCHER_TYPE_CREDIT_NOTE.getId().intValue()) {
        AccountingTransactionDetailItem item = getDetailItemNew(atd, AccountingConstant.DOC_TYPE_CREDIT_NOTE, atd.getCredit(), salesReturn.getInvoiceAmountNet(), atd.getCredit(), null, salesReturn.getInvoiceAmountIgst());
        item.setDocumentDate(salesReturn.getEntryDate());
        item.setDocumentNumber(salesReturn.getAccountInvoiceNo());
        item.setReferNumber(salesReturn.getInvoiceNo());
      }
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);
    TradeOutstandingService.insertSalesReturnByAccount(main, salesReturn, tran);
  }

  public static void saveSalesServicesInvoice(Main main, SalesServicesInvoice salesServicesInvoice, Currency currency, Company company) {
    if (!isValid(company)) {
      return;
    }
    AccountingTransaction tran = getTransactionSalesServices(main, salesServicesInvoice.getId(), salesServicesInvoice.getEntryDate(), currency, company);
    getTransactionDetail(main, tran, salesServicesInvoice.getAccountingLedgerId(), salesServicesInvoice.getNetValue(), null);
    getTransactionDetail(main, tran, salesServicesInvoice.getSalesServicesLedgerId(), null, salesServicesInvoice.getAssessableValue());
    addTaxAndRoundOff(main, tran, salesServicesInvoice.getInvoiceGroup(), salesServicesInvoice.getRoundOffValue(), salesServicesInvoice.getCompanyId(), AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
    cleanupIfExist(main, tran);
    setFirstAsDebit(tran);
    tran.setNarration(salesServicesInvoice.getSerialNo());
    tran.setRefDocumentNo(salesServicesInvoice.getSerialNo());
    tran.setAccountGroupId(salesServicesInvoice.getAccountGroupId());
    //  tran.setAccountGroupId(accountGroupId); //TODO set account Group
    for (AccountingTransactionDetail atd : tran.getTransactionDetail()) {
      atd.setEntryDate(salesServicesInvoice.getEntryDate());
      if (tran.getVoucherTypeId().getId() == AccountingConstant.VOUCHER_TYPE_SALES.getId().intValue()) {
        if (atd.getDebit() != null) {
          AccountingTransactionDetailItem item = getDetailItemNew(atd, AccountingConstant.DOC_TYPE_SALES_BILL, atd.getDebit(), salesServicesInvoice.getAssessableValue(), atd.getDebit(), null, salesServicesInvoice.getIgstValue());
          item.setDocumentDate(salesServicesInvoice.getEntryDate());
          item.setDocumentNumber(salesServicesInvoice.getSerialNo());
        }
      }
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);
    TradeOutstandingService.insertSalesServicesInvoice(main, salesServicesInvoice, tran);
  }

  public static void saveOpeningEntry(Main main, AccountingLedger mainLedger, List<TradeOutstanding> tradeOutstaningList, Company company) {
    if (mainLedger.getOpeningBalance() != null && mainLedger.getOpeningBalance() > 0) {
      if (mainLedger.isVendorOrCustomers()) {
        LedgerExternalDataService.saveLedgerOpeningEntryByAccountGroup(main, mainLedger, company.getCurrencyId(), company, tradeOutstaningList);
      } else {
        LedgerExternalDataService.saveLedgerOpeningEntry(main, mainLedger, company.getCurrencyId(), company, null);
      }
    }
  }

  private static final void saveLedgerOpeningEntryByAccountGroup(Main main, AccountingLedger mainLedger, Currency currency, Company company, List<TradeOutstanding> tradeOutstaningList) {
    AccountingTransaction tran;
    for (TradeOutstanding to : tradeOutstaningList) {
      if (to.getValueRemainingBalance() != null && to.getValueRemainingBalance() > 0) {
        to.setGroupTotal(to.getValueRemainingBalance()); //TODO this can be removed once this rule is confirded
        tran = saveLedgerOpeningEntry(main, mainLedger, currency, company, to);
        TradeOutstandingService.insertOutstanding(main, to, tran, to.getValueRemainingBalance(), null, to.getValueRemainingBalance());
      }
    }

  }

  private static final AccountingTransaction saveLedgerOpeningEntry(Main main, AccountingLedger mainLedger, Currency currency, Company company, TradeOutstanding to) {
    AccountingLedger openingLedger = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_OPENING_ENTRY, company.getId());

    AccountingTransaction tran = getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_OPEN_BALANCE, mainLedger.getEntityId(), mainLedger.getAccountingEntityTypeId(), null, mainLedger.getCompanyId().getFinancialYearStartDate(), currency, company);
    tran.setNarration((to == null || to.getAccountGroupId() == null) ? "Opening Balance" : "Opening Balance, " + to.getAccountGroupId().getGroupName() + " [" + to.getAccountId().getAccountTitle() + "]");
    tran.setCurrencyId(mainLedger.getCurrencyId());
    if (to != null) {
      tran.setAccountGroupId(to.getAccountGroupId());
    }
    //  tran.setDocumentNumber(AccountingPrefixService.getPrefixOnSave(main, tran, mainLedger.getCompanyId()));
    Double amount = (to == null) ? mainLedger.getOpeningBalance() : to.getGroupTotal();
    Integer isDrCr = (to == null) ? mainLedger.getIsDebitOrCredit() : to.getIsDebitOrCredit();
    tran.setTotalAmount(amount);
    AccountingTransactionDetail mainDetail;
    if (isDrCr == AccountingConstant.IS_DEBIT) {
      mainDetail = getTransactionDetailNew(tran, mainLedger, amount, null);
      getTransactionDetailNew(tran, openingLedger, null, amount);
      setFirstAsDebit(tran);
    } else {
      mainDetail = getTransactionDetailNew(tran, mainLedger, null, amount);
      getTransactionDetailNew(tran, openingLedger, amount, null);
      setFirstAsCredit(tran);
    }
    if (mainLedger.isDebtorsOrCreditors()) {
      AccountingTransactionDetailItem mainItem = getDetailItemNew(mainDetail, null, amount, amount, amount, null, null);
      mainItem.setDocumentDate(tran.getEntryDate());
      mainItem.setDocumentNumber((to == null || to.getAccountGroupId() == null) ? "Opening Entry" : "Opening Entry " + to.getAccountGroupId().getGroupCode());
      if (isDrCr == AccountingConstant.IS_DEBIT) {
        mainItem.setDocumentTypeId(AccountingConstant.DOC_TYPE_RECEIVABLE_BILL);
        mainItem.setRecordType(AccountingConstant.RECORD_TYPE_RECEIVABLE);
      } else {
        mainItem.setDocumentTypeId(AccountingConstant.DOC_TYPE_PAYABLE_BILL);
        mainItem.setRecordType(AccountingConstant.RECORD_TYPE_PAYABLE);
      }
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);
    return tran;
  }

  public static void setDebitCreditLedger(AccountingTransaction tran) {
    AccountingTransactionDetail bigDebit = null;
    AccountingTransactionDetail bigCredit = null;
    Double debitTotal = 0.00;
    Double creditTotal = 0.00;
    for (AccountingTransactionDetail d : tran.getTransactionDetail()) {
      if (d.getCredit() != null) {
        creditTotal += d.getCredit();
        if (bigCredit == null || bigCredit.getCredit() < d.getCredit()) {
          bigCredit = d;
        }
      }
      if (d.getDebit() != null) {
        debitTotal += d.getDebit();
        if (bigDebit == null || bigDebit.getDebit() < d.getDebit()) {
          bigDebit = d;
        }
      }
    }
    if (bigDebit != null) {
      tran.setLedgerDebitId(bigDebit.getAccountingLedgerId());
    }
    if (bigCredit != null) {
      tran.setLedgerCreditId(bigCredit.getAccountingLedgerId());
    }
    tran.setBiggestCredit(bigCredit);
    tran.setBiggestDebit(bigDebit);

    Double total = debitTotal < creditTotal ? creditTotal : debitTotal;
    tran.setTotalAmount(total);
  }

  public static void setFirstAsCredit(AccountingTransaction tran) {
    tran.setLedgerDebitId(tran.getTransactionDetail().get(1).getAccountingLedgerId());
    tran.setLedgerCreditId(tran.getTransactionDetail().get(0).getAccountingLedgerId());
    tran.setTotalAmount(tran.getTransactionDetail().get(0).getCredit());
  }

  public static void setFirstAsDebit(AccountingTransaction tran) {
    tran.setLedgerCreditId(tran.getTransactionDetail().get(1).getAccountingLedgerId());
    tran.setLedgerDebitId(tran.getTransactionDetail().get(0).getAccountingLedgerId());
    tran.setTotalAmount(tran.getTransactionDetail().get(0).getDebit());
  }

  public static void settle(AccountingTransactionDetail attachedDetail, AccountingTransactionDetailItem payable, AccountingTransactionDetailItem receivable, Double adjAmt) {
    AccountingTransactionSettlement settlement = new AccountingTransactionSettlement();
    settlement.setTransactionDetailItemId(payable);
    settlement.setAdjustedTransactionDetailItemId(receivable);
    settlement.setSettledAmount(adjAmt);
    attachedDetail.getSettlementList().add(settlement);
    if (payable.getBalanceAmount() > 0) {
      payable.setStatus(AccountingConstant.STATUS_PARTIAL_PROCESSED);
    } else {
      payable.setStatus(AccountingConstant.STATUS_PROCESSED);
    }
    if (receivable.getBalanceAmount() > 0) {
      receivable.setStatus(AccountingConstant.STATUS_PARTIAL_PROCESSED);
    } else {
      receivable.setStatus(AccountingConstant.STATUS_PROCESSED);
    }
  }

  private static void cleanupIfExist(Main main, AccountingTransaction tran) {
    AccountingTransaction t = AccountingTransactionService.selectByEntityType(main, tran);
    if (t != null && t.getId() != null) {
      List<Integer> list = new ArrayList<>();
      for (AccountingTransactionDetail acd : tran.getTransactionDetail()) {
        if (acd.getId() != null) {
          list.add(acd.getId());
        }
      }
      if (!StringUtil.isEmpty(list)) {
        main.em().executeJpql("delete from AccountingTransactionDetail o where o.id not in ?1 and o.accountingTransactionId.id=?2 ", new Object[]{list, t.getId()});
      }
    }
  }

  private static void addTaxAndRoundOffReverse(Main main, AccountingTransaction tran, List<InvoiceGroup> invoiceGroupList, Double roundOff, Company companyId, String ledgerCode) {
    boolean isDebit = tran.isPurchase() || tran.isCreditNote();
    isDebit = !isDebit; //reverse entry
    for (InvoiceGroup ig : invoiceGroupList) {//TaxCode method is same for cess and other tax code
      addTaxAndSub(main, tran, ig.getTaxCode(), isDebit, ledgerCode, ig.getInvoiceIgstValue(), ig.getInvoiceCgstValue(), ig.getInvoiceSgstValue(), ig.getInvoiceCessValue(), ig.getInvoiceTcsValue());
//      LedgerExternalDataService.addTaxCode(main, tran, ig, isDebit, companyId, ledgerCode);
    }
    addRoundOff(main, tran, roundOff, isDebit, companyId);
  }

  private static void addTaxAndRoundOff(Main main, AccountingTransaction tran, List<InvoiceGroup> invoiceGroupList, Double roundOff, Company companyId, String ledgerCode) {
    boolean isDebit = tran.isPurchase() || tran.isCreditNote();
    for (InvoiceGroup ig : invoiceGroupList) { //TaxCode method is same for cess and other tax code
      // LedgerExternalDataService.addTaxCode(main, tran, ig, isDebit, companyId, ledgerCode);
      addTaxAndSub(main, tran, ig.getTaxCode(), isDebit, ledgerCode, ig.getInvoiceIgstValue(), ig.getInvoiceCgstValue(), ig.getInvoiceSgstValue(), ig.getInvoiceCessValue(), ig.getInvoiceTcsValue());

    }
    addRoundOff(main, tran, roundOff, isDebit, companyId);
  }

  public static void saveSalesAgentCommission(Main main, List<SalesAgentClaimDetail> salesAgentClaimDetail, SalesAgentClaim salesAgentClaim) {
    // check whether entityid and entitytypeid already exist 
    if (!isValid(salesAgentClaim.getCompanyId())) {
      return;
    }
    Double commissionClaim = (salesAgentClaim.getTotalAmountClaim() + salesAgentClaim.getGstAmountClaim()) - salesAgentClaim.getTdsAmountClaim();
    Double addedIgst = salesAgentClaim.getTotalAmountClaim() + salesAgentClaim.getGstAmountClaim();
    AccountingLedger atd = AccountingLedgerService.selectLedgerByEntity(main, salesAgentClaim.getSalesAgentId().getId(), AccountingConstant.ACC_ENTITY_AGENT, salesAgentClaim.getCompanyId().getId());
    AccountingLedger atdDesc = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_COMMISSION_PAYABLE, salesAgentClaim.getCompanyId().getId());
    AccountingLedger atdTaxIgst = AccountingLedgerService.selectLedgerByEntityAndCode(main, salesAgentClaim.getTaxCodeIgstId().getId(), AccountingConstant.ACC_ENTITY_TAX, salesAgentClaim.getCompanyId().getId(), AccountingConstant.LEDGER_CODE_TAX_INPUT);
    AccountingLedger atdTaxTds = AccountingLedgerService.selectLedgerByEntityAndCode(main, salesAgentClaim.getTaxCodeId().getId(), AccountingConstant.ACC_ENTITY_TAX, salesAgentClaim.getCompanyId().getId(), AccountingConstant.LEDGER_CODE_TAX_TDS_LIABILITY);
    AccountingTransaction at = new AccountingTransaction();
    at.setAccountingEntityTypeId(AccountingConstant.ACC_ENTITY_AGENT);
    at.setEntityId(salesAgentClaim.getSalesAgentId().getId());
    at.setEntryDate(salesAgentClaim.getCreatedAt());
    at.setTotalAmount(addedIgst);
    at.setLedgerDebitId(atdDesc);
    at.setLedgerCreditId(atd);
    at.setNarration("commission for" + at.getEntryDate().toString());
    at.setVoucherTypeId(AccountingConstant.VOUCHER_TYPE_JOURNAL);
    at.setCompanyId(salesAgentClaim.getCompanyId());
    AccountingTransactionService.insertOrUpdate(main, at);

    for (SalesAgentClaimDetail list : salesAgentClaimDetail) {
      AccountingTransactionDetail details = new AccountingTransactionDetail();
      details.setAccountingLedgerId(atdDesc);
      details.setAccountingTransactionId(at);
      details.setDebit(list.getApprovedAmount());
      AccountingTransactionDetailService.insertOrUpdate(main, details);

      for (TaxCode tc : salesAgentClaim.getTaxCodeIgstId().getTaxCodeList()) {
        details = new AccountingTransactionDetail();
        details.setAccountingLedgerId(atdTaxIgst);
        details.setAccountingTransactionId(at);
        details.setDebit(salesAgentClaim.getGstAmountClaim() * (tc.getRatePercentage() / 100));
        AccountingTransactionDetailService.insertOrUpdate(main, details);
        //  AccountingTransactionDetailService.insertOrUpdate(main, details);
      }

      details = new AccountingTransactionDetail();
      details.setAccountingLedgerId(atd);
      details.setAccountingTransactionId(at);
      details.setCredit(commissionClaim);
      AccountingTransactionDetailService.insertOrUpdate(main, details);

      AccountingTransactionDetailItem item = new AccountingTransactionDetailItem();
      item.setAccountingTransactionDetailId(details);
      item.setBalanceAmount(commissionClaim);
      item.setNetAmount(commissionClaim);
      // item.setGoodsAmount(pe.getInvoiceAmountNet());
      // item.setDiscountAmount(pe.getInvoiceDiscountValue());// hav to check
      // item.setTaxAmount(pe.getInvoiceAmountIgst()); //have to check
      item.setDocumentDate(salesAgentClaim.getCreatedAt());
      item.setDocumentNumber(salesAgentClaim.getId().toString()); // TODO generate debit not number
      // item.setReferNumber(pe.getInvoiceNo());
      item.setDocumentTypeId(AccountingConstant.DOC_TYPE_PAYABLE_BILL); // have to check
      item.setRecordType(AccountingConstant.RECORD_TYPE_PAYABLE);
      item.setStatus(AccountingConstant.STATUS_NEW);
      AccountingTransactionDetailItemService.insertOrUpdate(main, item);

      details = new AccountingTransactionDetail();
      details.setAccountingLedgerId(atdTaxTds);
      details.setAccountingTransactionId(at);
      details.setCredit(salesAgentClaim.getTdsAmountClaim());
      AccountingTransactionDetailService.insertOrUpdate(main, details);

    }
  }

  public static Double editTaxes(List<AccountingTransactionDetail> detailList, Double debit, Double credit) {
    Double deductionTotal = 0.00;
    for (AccountingTransactionDetail td : detailList) {
      if (td.getTaxCodeId() != null) {
        TaxCode tax = td.getTaxCodeId();
        if (AccountingConstant.TAX_HEAD_TDS.getId() == tax.getTaxHeadId().getId()) {
          LedgerExternalDataService.computeTax(td, credit, debit);
          deductionTotal += td.getCredit();
        } else if (AccountingConstant.TAX_HEAD_GST_CESS.getId() == tax.getTaxHeadId().getId()) {
          LedgerExternalDataService.computeTax(td, debit, credit);
        } else if (AccountingConstant.TAX_TYPE_CGST == tax.getTaxType()) {
          LedgerExternalDataService.computeTax(td, debit, credit);
        } else if (AccountingConstant.TAX_TYPE_SGST == tax.getTaxType()) {
          LedgerExternalDataService.computeTax(td, debit, credit);
        } else if (AccountingConstant.TAX_TYPE_IGST == tax.getTaxType()) {
          LedgerExternalDataService.computeTax(td, debit, credit);
        } else {
          throw new RuntimeException("Begger did not implement");
        }
      }
    }
    return deductionTotal;
  }

  private static void addRoundOff(Main main, AccountingTransaction tran, Double roundOff, boolean isDebit, Company companyId) {
    // Round off value ledger entry
    if (roundOff != null && roundOff != 0) {
      if (isDebit) {
        LedgerExternalDataService.getTransactionDetailDebitRoundOff(main, tran, roundOff, null, companyId);
      } else {
        LedgerExternalDataService.getTransactionDetailCreditRoundOff(main, tran, null, roundOff, companyId);
      }
    }
  }

  public static void addTaxExpense(Main main, AccountingTransaction tran, AccountExpenseDetail exp) {
    addTaxAndSub(main, tran, exp.getTaxCode(), true, AccountingConstant.LEDGER_CODE_TAX_INPUT, exp.getIgstAmount(), exp.getCgstAmount(), exp.getSgstAmount(), null, null);
  }

  private static void addTaxAndSub(Main main, AccountingTransaction tran, TaxCode mainTax, boolean isDebit, String ledgerCode, Double igst, Double cgst, Double sgst, Double cess, Double tcs) {
    if (StringUtil.gtDouble(sgst, 0.0) && StringUtil.gtDouble(cgst, 0.0)) {
      List<TaxCode> child = mainTax.getTaxCodeList();//TaxCodeService.selectChildTaxCode(main, exp.getTaxCode(), tran.getCompanyId());
      if (child != null) {
        for (TaxCode tax : child) {
          if (AccountingConstant.TAX_HEAD_TDS.getId().intValue() == tax.getTaxHeadId().getId()) {
            throw new RuntimeException("Begger did not implement never be tds ");
          } else if (AccountingConstant.TAX_HEAD_GST_CESS.getId().intValue() == tax.getTaxHeadId().getId()) {
            LedgerExternalDataService.getTransactionDetailGst(main, tran, tax, null, !isDebit ? cess : null, tran.getCompanyId().getId(), AccountingConstant.LEDGER_CODE_TAX_CESS_LIABILITY);
          } else if (AccountingConstant.TAX_TYPE_CGST == tax.getTaxType()) {
            LedgerExternalDataService.getTransactionDetailGst(main, tran, tax, isDebit ? cgst : null, !isDebit ? cgst : null, tran.getCompanyId().getId(), ledgerCode);
          } else if (AccountingConstant.TAX_TYPE_SGST == tax.getTaxType()) {
            LedgerExternalDataService.getTransactionDetailGst(main, tran, tax, isDebit ? sgst : null, !isDebit ? sgst : null, tran.getCompanyId().getId(), ledgerCode);
          } else if (AccountingConstant.TAX_TYPE_IGST == tax.getTaxType()) {
            throw new RuntimeException("Begger did not implement, Never be igst here");
          } else {
            throw new RuntimeException("Begger did not implement");
          }
        }
      }

    } else if (StringUtil.gtDouble(igst, 0.0)) {
      LedgerExternalDataService.getTransactionDetailGst(main, tran, mainTax, isDebit ? igst : null, !isDebit ? igst : null, tran.getCompanyId().getId(), ledgerCode);
    }
    if (StringUtil.gtDouble(cess, 0.0)) { //FIXME if cess is also a child of GST then this is not needed it will work in child loop
      LedgerExternalDataService.getTransactionDetailGst(main, tran, mainTax, isDebit ? cess : null, !isDebit ? cess : null, tran.getCompanyId().getId(), AccountingConstant.LEDGER_CODE_TAX_CESS_LIABILITY);
    }
    if (StringUtil.gtDouble(tcs, 0.0)) { //FIXME if cess is also a child of GST then this is not needed it will work in child loop
      String ledger = isDebit ? AccountingConstant.LEDGER_CODE_TAX_TCS_ASSET : AccountingConstant.LEDGER_CODE_TAX_TCS_LIABILITY;
      LedgerExternalDataService.getTransactionDetailGst(main, tran, mainTax, isDebit ? tcs : null, !isDebit ? tcs : null, tran.getCompanyId().getId(), ledger);
    }
    // if (tds != null) { //FIXME tds wont work because of ledger code
    //   LedgerExternalDataService.getTransactionDetailGst(main, tran, invoiceGroup.getTdsTaxCode(), !isDebit ? invoiceGroup.getInvoiceTdsValue() : null, isDebit ? invoiceGroup.getInvoiceIgstValue() : null, companyId.getId(), ledgerCode);
    // }
  }

  public static void setChildTaxCodes(Main main, AccountingLedger ledger) {
    if (ledger.getIgstId() != null) {
      List<TaxCode> taxCodeList = TaxCodeService.selectChildTaxCode(main, ledger.getIgstId(), ledger.getCompanyId());
      for (TaxCode tc : taxCodeList) {
        if (tc.getTaxType() == AccountingConstant.TAX_TYPE_CGST) {
          ledger.setCgstId(tc);
        } else if (tc.getTaxType() == AccountingConstant.TAX_TYPE_SGST) {
          ledger.setSgstId(tc);
        }
      }
    }
  }

  private static final AccountingTransaction getTransactionDebitNote(Main main, int entityId, Date entryDate, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_DEBIT_NOTE, entityId, AccountingConstant.ACC_ENTITY_DEBIT_CREDIT_NOTE, "", entryDate, company.getCurrencyId(), company);
  }

  private static final AccountingTransaction getTransactionCreditNote(Main main, int entityId, Date entryDate, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_CREDIT_NOTE, entityId, AccountingConstant.ACC_ENTITY_DEBIT_CREDIT_NOTE, "", entryDate, company.getCurrencyId(), company);
  }

  public static void saveDebitCreditNote(Main main, DebitCreditNote debitCreditNote, Company company) {
    if (!isValid(company)) {
      return;
    }
    AccountingTransaction tran = null;
    AccountingLedger salesOrPurchase = null;
    if (debitCreditNote.isCustomer()) {
      salesOrPurchase = (CustomerService.selectSalesLedgerByCustomer(main, debitCreditNote.getAccountingLedgerId().getEntityId()));
    } else if (debitCreditNote.isSupplier()) {
      salesOrPurchase = (debitCreditNote.getAccountId().getPurchaseLedgerId());
    }

    if (debitCreditNote.getInvoiceType() == (SystemConstants.DEBIT_NOTE.intValue())) {
      // AccountingLedger drNote = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_DEBIT_NOTE, company.getId());
      tran = getTransactionDebitNote(main, debitCreditNote.getId(), debitCreditNote.getEntryDate(), company);
      getTransactionDetail(main, tran, debitCreditNote.getAccountingLedgerId(), debitCreditNote.getNetValue(), null); //dr
      getTransactionDetail(main, tran, salesOrPurchase, null, debitCreditNote.getAssessableValue()); //cr
      addTaxAndRoundOff(main, tran, debitCreditNote.getInvoiceGroup(), debitCreditNote.getRoundOff(), company, AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
      setFirstAsDebit(tran);
    } else {
      // AccountingLedger crNote = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_CREDIT_NOTE, company.getId());
      tran = getTransactionCreditNote(main, debitCreditNote.getId(), debitCreditNote.getEntryDate(), company);
      getTransactionDetail(main, tran, debitCreditNote.getAccountingLedgerId(), null, debitCreditNote.getNetValue());//cr
      getTransactionDetail(main, tran, salesOrPurchase, debitCreditNote.getAssessableValue(), null); //dr
      addTaxAndRoundOff(main, tran, debitCreditNote.getInvoiceGroup(), debitCreditNote.getRoundOff(), company, AccountingConstant.LEDGER_CODE_TAX_INPUT);
      setFirstAsCredit(tran);
    }
    tran.setAccountGroupId(debitCreditNote.getAccountGroupId());
    tran.setNarration(debitCreditNote.getDocumentNo()); //TODO
    tran.setRefDocumentNo(debitCreditNote.getDocumentNo());

    tran.setNarration(debitCreditNote.getNarration());

    // cleanupIfExist(main, tran);
    for (AccountingTransactionDetail atd : tran.getTransactionDetail()) {
      atd.setEntryDate(debitCreditNote.getEntryDate()); //TODO remove entry date fromthis db
      if (tran.getVoucherTypeId().getId() == AccountingConstant.VOUCHER_TYPE_DEBIT_NOTE.getId().intValue()) {
        if (atd.getDebit() != null) {
          AccountingTransactionDetailItem item = getDetailItemNew(atd, AccountingConstant.DOC_TYPE_DEBIT_NOTE, atd.getDebit(), debitCreditNote.getAssessableValue(), atd.getDebit(), null, debitCreditNote.getIgstValue());
          item.setDocumentDate(debitCreditNote.getEntryDate());
          item.setDocumentNumber(debitCreditNote.getDocumentNo());
          item.setReferNumber(debitCreditNote.getInvoiceNo());
        }
      } else if (tran.getVoucherTypeId().getId() == AccountingConstant.VOUCHER_TYPE_CREDIT_NOTE.getId().intValue()) {
        if (atd.getCredit() != null) {
          AccountingTransactionDetailItem item = getDetailItemNew(atd, AccountingConstant.DOC_TYPE_CREDIT_NOTE, atd.getCredit(), debitCreditNote.getAssessableValue(), atd.getCredit(), null, debitCreditNote.getIgstValue());
          item.setDocumentDate(debitCreditNote.getEntryDate());
          item.setDocumentNumber(debitCreditNote.getDocumentNo());
          item.setReferNumber(debitCreditNote.getInvoiceNo());
        }
      }
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);
    TradeOutstandingService.insertDebitCreditNoteByAccount(main, debitCreditNote, tran);

  }

  public static void saveVendorClaim(Main main, VendorClaim vendorClaim, List<VendorClaimDetail> claimDetailList) {
    Company company = vendorClaim.getCompanyId();
    if (!isValid(company)) {
      return;
    }
    AccountingTransaction tran = null;
    Account acc = AccountService.selectByPk(main, vendorClaim.getAccountId());
    AccountingLedger vendorClaimLedger = acc.getSupplierClaimLedgerId() != null ? acc.getSupplierClaimLedgerId() : acc.getPurchaseLedgerId();
    AccountingLedger vendorLedger = AccountingLedgerService.selectSupplierAccountingLedger(main, vendorClaim.getVendorId());
    List<InvoiceGroup> invoiceGroupList = new ArrayList<>();
    InvoiceGroup invoiceGroup = new InvoiceGroup();
    invoiceGroup.setTaxCode(vendorClaim.getService().getSalesTaxCodeId());//SerVice TaxCode;
    invoiceGroup.setInvoiceIgstValue(vendorClaim.getTaxValueIgst());
    invoiceGroup.setInvoiceCgstValue(vendorClaim.getTaxValueCgst());
    invoiceGroup.setInvoiceSgstValue(vendorClaim.getTaxValueSgst());
    invoiceGroupList.add(invoiceGroup);

    // AccountingLedger drNote = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_DEBIT_NOTE, company.getId());
    tran = getTransactionVendorClaim(main, vendorClaim.getId(), vendorClaim.getEntryDate(), company.getCurrencyId(), company);
    getTransactionDetail(main, tran, vendorLedger, vendorClaim.getCommissionClaim(), null); //dr

    if (vendorClaim.getTdsValue() != null) {
      AccountingLedger atdTaxTds = AccountingLedgerService.selectLedgerByEntityAndCode(main, vendorClaim.getTdsTaxCode().getId(), AccountingConstant.ACC_ENTITY_TAX, vendorClaim.getCompanyId().getId(), AccountingConstant.LEDGER_CODE_TAX_TDS_LIABILITY);
      getTransactionDetail(main, tran, atdTaxTds, vendorClaim.getTdsValue(), null); //dr
    }
    getTransactionDetail(main, tran, vendorClaimLedger, null, vendorClaim.getCommissionAmount()); //cr
    addTaxAndRoundOff(main, tran, invoiceGroupList, null, company, AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
    // setFirstAsDebit(tran);
    LedgerExternalDataService.setDebitCreditLedger(tran);

    //1 invoice group creation    
    //2 narration
    double taxValue = vendorClaim.getTaxValueIgst() == null ? (vendorClaim.getTaxValueCgst() == null ? 0.0 : vendorClaim.getTaxValueCgst()) + (vendorClaim.getTaxValueSgst() == null ? 0.0 : vendorClaim.getTaxValueSgst()) : vendorClaim.getTaxValueIgst();
    tran.setAccountGroupId(vendorClaim.getAccountGroupId());
    ///  tran.setNarration(vendorClaim.get); //TODO
    tran.setRefDocumentNo(vendorClaim.getInvoiceNumber());

    for (AccountingTransactionDetail atd : tran.getTransactionDetail()) {
      atd.setEntryDate(tran.getEntryDate()); //TODO remove entry date fromthis db
      if (atd.getDebit() != null) {
        AccountingTransactionDetailItem item = getDetailItemNew(atd, AccountingConstant.DOC_TYPE_DEBIT_NOTE, atd.getDebit(), vendorClaim.getCommissionAmount(), atd.getDebit(), null, taxValue);
        item.setDocumentDate(tran.getEntryDate());
        item.setDocumentNumber(vendorClaim.getInvoiceNumber());
        // item.setReferNumber(vendorClaim.getInvoiceNumber());
      }
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);
    TradeOutstandingService.insertVendorClaimByAccount(main, vendorClaim, tran);
    makeExpense(main, vendorClaim, claimDetailList);

  }

  private static void makeExpense(Main main, VendorClaim vendorClaim, List<VendorClaimDetail> claimDetailList) {
    List<VendorClaimDetail> expenseLedeger = new ArrayList<>();
    Double expense = calculateExpense(claimDetailList, expenseLedeger);
    if (expense > 0) {
      for (VendorClaimDetail dtl : expenseLedeger) {
        AccountingLedger vendorLedger = AccountingLedgerService.selectSupplierAccountingLedger(main, vendorClaim.getVendorId());
        AccountingTransaction tran = LedgerExternalDataService.getTransactionClaimExpense(main, vendorClaim.getId(), vendorClaim.getEntryDate(), vendorClaim.getCompanyId().getCurrencyId(), vendorClaim.getCompanyId());

        tran.setRefDocumentNo(vendorClaim.getInvoiceNumber());
        tran.setNarration("Reimbursable expense");
        if (vendorClaim.getNarration() != null) {
          tran.setNarration(tran.getNarration() + ", " + vendorClaim.getNarration());
        }
        tran.setAccountGroupId(vendorClaim.getAccountGroupId());
        AccountingTransactionDetail payableDetail = LedgerExternalDataService.getTransactionDetailNew(tran, vendorLedger, null, dtl.getTaxableAmount());
        AccountingTransactionDetail taxDetail = LedgerExternalDataService.getTransactionDetailNew(tran, dtl.getAccountingLedgerId(), dtl.getTaxableAmount(), null);
        LedgerExternalDataService.setFirstAsCredit(tran);

        AccountingTransactionDetailItem item = LedgerExternalDataService.getDetailItemExpensePayable(payableDetail, dtl.getTaxableAmount(), null, null, null, null);
        item.setDocumentDate(vendorClaim.getEntryDate());
        item.setDocumentNumber(vendorClaim.getInvoiceNumber()); //TODO this should be bill no from above    
        AccountingTransactionService.insertOrUpdateAll(main, tran);
      }
    }
  }

  private static Double calculateExpense(List<VendorClaimDetail> claimDetailList, List<VendorClaimDetail> expenseLedeger) {
    Double expense = 0.0;
    for (VendorClaimDetail dtl : claimDetailList) {
      if (dtl.getClaimType().intValue() == ClaimConstants.CLAIM_EXPENSES) {
        if (dtl.getTaxableAmount() != null) {
          expense += dtl.getTaxableAmount();
          expenseLedeger.add(dtl);
        }
      }
    }
    return expense;
  }

  private static final AccountingTransaction getTransactionVendorClaim(Main main, int entityId, Date entryDate, Currency currency, Company company) {
    return getTransactionNew(main, AccountingConstant.VOUCHER_TYPE_DEBIT_NOTE, entityId, AccountingConstant.ACC_ENTITY_VENDOR_CLAIM, "", entryDate, currency, company);
  }

  private static boolean isValid(Company company) {
    if (company.getCompanySettings() == null || company.getCompanySettings().getTradeToAccounting() == null || company.getCompanySettings().getTradeToAccounting() == AccountingConstant.TRADE_TO_ACCOUNTING_ALLOW_ALL) {
      return true;
    } else if (company.getCompanySettings().getTradeToAccounting() == AccountingConstant.TRADE_TO_ACCOUNTING_NONE) {
      return false;
    } else if (company.getCompanySettings().getTradeToAccounting() == AccountingConstant.TRADE_TO_ACCOUNTING_ONLY_FOR_CURRENT_YEAR) {
      if (company.getCurrentFinancialYear().getId().intValue() == company.getCurrentCompanyFinancialYear().getFinancialYearId().getId()) {
        return true;
      } else {
        return false;
      }
    }
    return true;
  }

}
