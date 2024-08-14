/*
 * @(#)TradeOutstandingService.java	1.0 Mon Dec 18 16:22:19 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.ArrayList;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.DebitCreditNote;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.SalesInvoice;
import wawo.app.common.AppService;
import wawo.app.Main;
import spica.scm.domain.SalesReturn;
import spica.fin.domain.TradeOutstanding;
import spica.fin.domain.VendorClaim;
import spica.scm.common.AccountWiseOutstanding;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Customer;
import spica.scm.domain.SalesServicesInvoice;
import spica.scm.domain.Vendor;
import spica.scm.service.AccountService;
import spica.scm.service.CustomerService;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 * TradeOutstandingService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Dec 18 16:22:19 IST 2017
 */
public abstract class TradeOutstandingService {

//  public static void updateCollectionAndBalance(Main main, AccountingTransaction tran, AccountingTransactionDetailItem p) {
//    //TODO fix me not doing for tax entry
//
//    if ((p.getAccountingTransactionDetailId().getAccountingTransactionId().getAccountingEntityTypeId() == null)) //Does nothing since this is not  coming from sale/return/purchase/return/opening entry
//    {
//      return;
//    }
//
//    String sumTotal = "select sum(value_after_tax::float) from fin_trade_outstanding where status=1 and entity_type_id = ? and entity_id = ?";
//    String sumTotalSelf = "select sum(value_after_tax::float) from fin_trade_outstanding where status=1 and entity_type_id is null and accounting_transaction_id=?";
//    String sqlOutstandingSelf = "select * from fin_trade_outstanding where status=1 and value_remaining_balance > 0 and entity_type_id is null and accounting_transaction_id=? order by id";
//
//    AccountingTransaction accountingTransactionId = p.getAccountingTransactionDetailId().getAccountingTransactionId();
//    Double total = (accountingTransactionId.getAccountingEntityTypeId() != null) ? sumTotal(main, sumTotal, accountingTransactionId) : (Double) AppService.single(main, null, sumTotalSelf, new Object[]{p.getId()});
//    List<TradeOutstanding> list = (accountingTransactionId.getAccountingEntityTypeId() != null) ? selectOutstanding(main, accountingTransactionId) : AppService.list(main, TradeOutstanding.class, sqlOutstandingSelf, new Object[]{p.getId()});
//    for (TradeOutstanding tradeOutstanding : list) {
//
//      TradeOutstanding s = new TradeOutstanding(tradeOutstanding);
//      s.setValueBeforeTax(p.getAdjustmentAmount() * (tradeOutstanding.getValueBeforeTax() / tradeOutstanding.getValueAfterTax()));
//      s.setValueAfterTax(p.getAdjustmentAmount() * (tradeOutstanding.getValueAfterTax() / total));
//      if (tradeOutstanding.getValueTax() != null) {
//        s.setValueTax(p.getAdjustmentAmount() * (tradeOutstanding.getValueTax() / tradeOutstanding.getValueAfterTax()));
//      }
//      s.setValueRemainingBalance(tradeOutstanding.getValueRemainingBalance() - s.getValueAfterTax());
//      s.setStatus(AccountingConstant.STATUS_PROCESSED);
//      s.setAccountingTransactionId(tran);
//      s.setEntryDate(accountingTransactionId.getEntryDate());
//      tradeOutstanding.setValueRemainingBalance(s.getValueRemainingBalance()); // Reducing the balance in the main
//      // if (s.getBalanceAmount() <= 0) { // if no more balnce setting it as processed
//      // s.setStatus(0);
//      // tradeOutstanding.setStatus(0); // Setting old as processed and will not be considered for future
//      // }
//      AppService.insert(main, s);
//
////      updateCollectionAndBalanceTax(main, s, accountingTransactionId, p);
//    }
//  }
//  private static void updateCollectionAndBalanceTax(Main main, TradeOutstanding tradeOut, AccountingTransaction accountingTransactionId, AccountingTransactionDetailItem p) {
//    String sumUpTotalBeforeTax = "select sum(value_before_tax::float) from fin_trade_outstanding_tax where status=1 and entity_type_id = ? and entity_id = ?";
//    String sumTotalTax = "select sum(value_tax::float) from fin_trade_outstanding_tax where status=1 and entity_type_id = ? and entity_id = ?";
//
//    String sumUpTotalBeforeTaxSelf = "select sum(value_before_tax::float) from fin_trade_outstanding_tax where status=1 and entity_type_id is null and accounting_transaction_id = ?";
//    String sumTotalTaxSelf = "select sum(value_tax::float) from fin_trade_outstanding_tax where status=1 and entity_type_id is null and accounting_transaction_id = ?";
//    String sqlTaxListSelf = "select * from fin_trade_outstanding_tax where status=1 and value_remaining_balance > 0 and entity_type_id is null and accounting_transaction_id = ? order by id";
//    List<TradeOutstandingTax> list = (accountingTransactionId.getAccountingEntityTypeId() != null) ? selectOutstandingTax(main, accountingTransactionId) : AppService.list(main, TradeOutstandingTax.class, sqlTaxListSelf, new Object[]{p.getId()});
//    Double gvTotal = (accountingTransactionId.getAccountingEntityTypeId() != null) ? sumTotal(main, sumUpTotalBeforeTax, accountingTransactionId) : (Double) AppService.single(main, null, sumUpTotalBeforeTaxSelf, new Object[]{p.getId()});
//    Double taxTotal = (accountingTransactionId.getAccountingEntityTypeId() != null) ? sumTotal(main, sumTotalTax, accountingTransactionId) : (Double) AppService.single(main, null, sumTotalTaxSelf, new Object[]{p.getId()});
//    TradeOutstandingTax tax;
//    for (TradeOutstandingTax tctd : list) {
//      tax = new TradeOutstandingTax(tctd);
//      tax.setValueBeforeTax(tradeOut.getValueBeforeTax() * tctd.getValueBeforeTax() / gvTotal);
//      if (tradeOut.getValueTax() != null) {
//        tax.setValueTax(tradeOut.getValueTax() * tctd.getValueTax() / taxTotal);
//        tax.setValueAfterTax(tax.getValueBeforeTax() + tax.getValueTax());
//        tax.setValueRemainingBalance(tctd.getValueRemainingBalance() - tax.getValueTax());
//      } else { //This should never happen since tax adjustment happen for items with tax
//        System.out.println("ERROR THIS SHOULD NEVER HAPPEN==============================================");
//        tax.setValueAfterTax(tax.getValueBeforeTax()); //Calculating the portion of Received 
//        tax.setValueRemainingBalance(tctd.getValueRemainingBalance());
//      }
//      tctd.setValueRemainingBalance(tax.getValueRemainingBalance());
//      tax.setEntryDate(accountingTransactionId.getEntryDate());
//      tax.setAccountingTransactionId(tradeOut.getAccountingTransactionId());
//      tax.setStatus(AccountingConstant.STATUS_PROCESSED);
//      AppService.insert(main, tax);
//    }
//  }
//  private static List<TradeOutstanding> selectOutstanding(Main main, AccountingTransaction at) {
//    String sqlOutstanding = "select * from fin_trade_outstanding where status=1 and value_remaining_balance > 0 and entity_type_id = ? and entity_id = ? order by id";
//    return AppService.list(main, TradeOutstanding.class, sqlOutstanding, new Object[]{at.getAccountingEntityTypeId().getId(), at.getEntityId()});
//  }
//  private static List<TradeOutstandingTax> selectOutstandingTax(Main main, AccountingTransaction at) {
//    String sqlTaxList = "select * from fin_trade_outstanding_tax where status=1 and value_remaining_balance > 0 and entity_type_id = ? and entity_id = ? order by id";
//    return AppService.list(main, TradeOutstandingTax.class, sqlTaxList, new Object[]{at.getAccountingEntityTypeId().getId(), at.getEntityId()});
//  }
  private static final Double sumTotal(Main main, String sql, AccountingTransaction tran) {
    return (Double) AppService.countDouble(main, sql, new Object[]{tran.getAccountingEntityTypeId().getId(), tran.getEntityId()});
  }

  public static List<TradeOutstanding> selectOutstandingByOpeningLedger(Main main, AccountingLedger ledger) {
    List<TradeOutstanding> list = null;
    if (ledger != null && ledger.isDebtorsOrCreditors()) {
      //removed and t2.account_id is null to pull all previously it was showing only primary
      list = AppService.list(main, TradeOutstanding.class, "select t1.* from fin_trade_outstanding t1 "
              + "inner join scm_account t2 on t1.account_id = t2.id  "
              + "where t1.status = 1 and t1.entity_type_id = ? and t1.entity_id = ? ", new Object[]{ledger.getAccountingEntityTypeId().getId(), ledger.getEntityId()});
      if (StringUtil.isEmpty(list)) {
        list = newOutstandingForOpeningLedger(main, ledger);
      }
    }
    return list;
  }

  private static List<TradeOutstanding> newOutstandingForOpeningLedger(Main main, AccountingLedger accountingLedger) {
    List<TradeOutstanding> list = new ArrayList();
    if (accountingLedger.isVendor()) {
      Account accountId = AccountService.selectAccountByVendor(main, new Vendor(accountingLedger.getEntityId()));
      if (accountId != null) {
        list.add(new TradeOutstanding(accountingLedger, accountId, null));
      }
    } else if (accountingLedger.isCustomer()) {
      Customer cust = CustomerService.selectByPk(main, new Customer(accountingLedger.getEntityId()));
      List<Account> accountList = AccountService.selectAccountByCustomer(main, cust);
      if (accountList != null) {
        for (Account account : accountList) {
          list.add(new TradeOutstanding(accountingLedger, account, cust));
        }
      }
    }
    return list;
  }

  public static void insertSalesByAccount(Main main, SalesInvoice salesInvoice, AccountingTransaction tran) {
    List<AccountWiseOutstanding> list = AppDb.getList(main.dbConnector(), AccountWiseOutstanding.class, "select account_id as id,sales_invoice_id,account_id,sum(value_sale) as value_sale, "
            + "sum(value_cgst) value_cgst, sum(value_sgst) value_sgst, sum(value_igst) value_igst, sum(invoice_discount_value) invoice_discount_value, sum(cash_discount_value) cash_discount_value "
            + "from scm_sales_invoice_item where sales_invoice_id = ? group by account_id,sales_invoice_id "
            + "order by account_id", new Object[]{salesInvoice.getId()});
    Double valueBeforeTax;
    Account accountId = null;
    boolean hasCashDiscount = ((salesInvoice.getCashDiscountApplicable() != null) && (salesInvoice.getCashDiscountValue() != null && salesInvoice.getCashDiscountValue() > 0));
    boolean isCashDiscountTaxable = (salesInvoice.getCashDiscountTaxable() != null && salesInvoice.getCashDiscountTaxable().equals(SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE));
    for (AccountWiseOutstanding sitem : list) {
      accountId = AccountService.selectByPk(main, new Account(sitem.getAccountId()));
      valueBeforeTax = sitem.getInvoiceDiscountValue() != null ? sitem.getValueSale() - sitem.getInvoiceDiscountValue() : sitem.getValueSale();
      Double invoiceAmount = (((sitem.getValueSale() == null ? 0 : sitem.getValueSale()) + (sitem.getValueIgst() == null ? 0 : sitem.getValueIgst())) - (sitem.getInvoiceDiscountValue() == null ? 0 : sitem.getInvoiceDiscountValue()));
      if (hasCashDiscount) {
        if (isCashDiscountTaxable) {
          valueBeforeTax -= sitem.getCashDiscountValue() == null ? salesInvoice.getCashDiscountValue() : sitem.getCashDiscountValue();
        }
        invoiceAmount -= sitem.getCashDiscountValue() == null ? salesInvoice.getCashDiscountValue() : sitem.getCashDiscountValue();
      }
      //insertOutstanding(main, new TradeOutstanding(salesInvoice, accountId), valueBeforeTax, sitem.getValueIgst(), invoiceAmount);      
      TradeOutstanding to = new TradeOutstanding(salesInvoice, accountId);
      to.setIsDebitOrCredit(AccountingConstant.IS_DEBIT); // As per customer perspective
      insertOutstanding(main, to, tran, valueBeforeTax, sitem.getValueIgst(), invoiceAmount);
    }
//    TradeOutstandingTaxService.insertSalesTaxByAccount(main, salesInvoice);
  }

  public static void insertSalesCancelByAccount(Main main, SalesInvoice salesInvoice, AccountingTransaction tran) {
    List<AccountWiseOutstanding> list = AppDb.getList(main.dbConnector(), AccountWiseOutstanding.class, "select account_id as id,sales_invoice_id,account_id,sum(value_sale) as value_sale, "
            + "sum(value_cgst) value_cgst, sum(value_sgst) value_sgst, sum(value_igst) value_igst, sum(invoice_discount_value) invoice_discount_value, sum(cash_discount_value) cash_discount_value "
            + "from scm_sales_invoice_item where sales_invoice_id = ? group by account_id,sales_invoice_id "
            + "order by account_id", new Object[]{salesInvoice.getId()});
    Double valueBeforeTax;
    Account accountId = null;
    TradeOutstanding tradeOutstanding = null;
    boolean hasCashDiscount = ((salesInvoice.getCashDiscountApplicable() != null) && (salesInvoice.getCashDiscountValue() != null && salesInvoice.getCashDiscountValue() > 0));
    boolean isCashDiscountTaxable = (salesInvoice.getCashDiscountTaxable() != null && salesInvoice.getCashDiscountTaxable().equals(SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE));
    for (AccountWiseOutstanding sitem : list) {
      accountId = AccountService.selectByPk(main, new Account(sitem.getAccountId()));
      valueBeforeTax = sitem.getInvoiceDiscountValue() != null ? sitem.getValueSale() - sitem.getInvoiceDiscountValue() : sitem.getValueSale();
      Double invoiceAmount = (((sitem.getValueSale() == null ? 0 : sitem.getValueSale()) + (sitem.getValueIgst() == null ? 0 : sitem.getValueIgst())) - (sitem.getInvoiceDiscountValue() == null ? 0 : sitem.getInvoiceDiscountValue()));
      if (hasCashDiscount) {
        if (isCashDiscountTaxable) {
          valueBeforeTax -= sitem.getCashDiscountValue() == null ? salesInvoice.getCashDiscountValue() : sitem.getCashDiscountValue();
        }
        invoiceAmount -= sitem.getCashDiscountValue() == null ? salesInvoice.getCashDiscountValue() : sitem.getCashDiscountValue();
      }
      tradeOutstanding = new TradeOutstanding(salesInvoice, accountId);
      //tradeOutstanding.setIsDebitOrCredit(AccountingConstant.IS_DEBIT);
      tradeOutstanding.setIsDebitOrCredit(AccountingConstant.IS_CREDIT); // As per customer perspective
      insertOutstanding(main, tradeOutstanding, tran, valueBeforeTax, sitem.getValueIgst(), invoiceAmount);
    }
    //  TradeOutstandingTaxService.insertSalesCancelTaxByAccount(main, salesInvoice);
  }

  public static void insertSalesServicesInvoice(Main main, SalesServicesInvoice salesServicesInvoice, AccountingTransaction tran) {
    //    insertOutstanding(main, new TradeOutstanding(salesServicesInvoice, accountId), salesServicesInvoice.getAssessableValue(), salesServicesInvoice.getIgstValue(), salesServicesInvoice.getNetValue());
    Account account = null;
    if (AccountingConstant.ACC_ENTITY_VENDOR.getId().equals(salesServicesInvoice.getAccountingLedgerId().getAccountingEntityTypeId().getId())) {
      account = AccountService.selectAccountByVendor(main, new Vendor(salesServicesInvoice.getAccountingLedgerId().getEntityId()));
    }
    TradeOutstanding tradeOutstanding = new TradeOutstanding(salesServicesInvoice, account);
    tradeOutstanding.setIsDebitOrCredit(AccountingConstant.IS_DEBIT); // As per customer perspective
    insertOutstanding(main, tradeOutstanding, tran, salesServicesInvoice.getAssessableValue(), salesServicesInvoice.getIgstValue(), salesServicesInvoice.getNetValue());
    //  TradeOutstandingTaxService.insertSalesServiceInvoiceByAccount(main, salesServicesInvoice, accountId);
  }

  public static final void insertProductEntryByAccount(Main main, ProductEntry productEntry, AccountingTransaction tran) {
    TradeOutstanding tradeOutstanding = new TradeOutstanding(productEntry);
    tradeOutstanding.setIsDebitOrCredit(AccountingConstant.IS_CREDIT); // As per customer perspective
    insertOutstanding(main, tradeOutstanding, tran, productEntry.getInvoiceAmountAssessable(), productEntry.getInvoiceAmountIgst(), productEntry.getInvoiceValue());
//    insertOutstanding(main, new TradeOutstanding(productEntry), productEntry.getInvoiceAmountAssessable(), productEntry.getInvoiceAmountIgst(), productEntry.getInvoiceValue());
    //  TradeOutstandingTaxService.insertProductEntryTaxByAccount(main, productEntry);

  }

  public static final void insertSalesReturnByAccount(Main main, SalesReturn salesReturn, AccountingTransaction tran) {
    Account accountId = null;
    List<AccountWiseOutstanding> list = AppDb.getList(main.dbConnector(), AccountWiseOutstanding.class, "select sales_return_id,account_id,product_discount_value,scheme_discount_value,invoice_discount_value, "
            + "sum(value_cgst) value_cgst,sum(value_sgst) value_sgst,sum(value_igst) value_igst,sum(value_assessable) value_assessable, sum(value_goods) value_goods "
            + "from scm_sales_return_item where sales_return_id = ? "
            + "group by sales_return_id,account_id,product_discount_value,scheme_discount_value,invoice_discount_value", new Object[]{salesReturn.getId()});

    for (AccountWiseOutstanding sitem : list) {
      accountId = AccountService.selectByPk(main, new Account(sitem.getAccountId()));
      Double invoiceAmount = (((sitem.getValueAssessable() == null ? 0 : sitem.getValueAssessable()) + (sitem.getValueIgst() == null ? 0 : sitem.getValueIgst())));
      TradeOutstanding tradeOutstanding = new TradeOutstanding(salesReturn, accountId);
      tradeOutstanding.setIsDebitOrCredit(AccountingConstant.IS_CREDIT); // As per customer perspective
      insertOutstanding(main, tradeOutstanding, tran, sitem.getValueAssessable(), sitem.getValueIgst(), invoiceAmount);
      //insertOutstanding(main, new TradeOutstanding(salesReturn, accountId), sitem.getValueAssessable(), sitem.getValueIgst(), invoiceAmount);
    }
    //  TradeOutstandingTaxService.insertSalesReturnTaxByAccount(main, salesReturn);
  }

  public static final void insertPurchaseReturnByAccount(Main main, PurchaseReturn purchaseReturn, AccountingTransaction tran) {
    TradeOutstanding tradeOutstanding = new TradeOutstanding(purchaseReturn);
    tradeOutstanding.setIsDebitOrCredit(AccountingConstant.IS_DEBIT); // As per customer perspective
    insertOutstanding(main, tradeOutstanding, tran, purchaseReturn.getAssessableValue(), purchaseReturn.getInvoiceAmountIgst(), purchaseReturn.getInvoiceAmount());
    // insertOutstanding(main, new TradeOutstanding(purchaseReturn), purchaseReturn.getAssessableValue(), purchaseReturn.getInvoiceAmountIgst(), purchaseReturn.getInvoiceAmount());
    //   TradeOutstandingTaxService.insertPurchaseReturnTaxByAccount(main, purchaseReturn);
  }
//
//  public static final void insertOutstandingOpeningEntry(Main main, AccountingTransaction accountingTransaction, List<TradeOutstanding> tradeOutstandingList) {
//    for (TradeOutstanding tradeOutstanding : tradeOutstandingList) {
//      insertOutstanding(main, tradeOutstanding, accountingTransaction, tradeOutstanding.getValueRemainingBalance(), null, tradeOutstanding.getValueRemainingBalance());
//    }
//  }

  public static final void insertOutstanding(Main main, TradeOutstanding to, AccountingTransaction tran, Double valueBeforeTax, Double tax, Double valueAfterTax) {
    to.setAccountingTransactionId(tran);
    to.setDocumentNo(tran.getDocumentNumber());
    to.setEntryDate(tran.getEntryDate());
    if (valueBeforeTax != null && valueBeforeTax > 0) {
      to.setValueBeforeTax(valueBeforeTax);
      to.setValueTax(tax);
      to.setValueAfterTax(valueAfterTax);
      to.setValueRemainingBalance(valueAfterTax);
      to.setStatus(AccountingConstant.STATUS_NEW);
      to.setAccountingTransactionId(tran);
      AppService.insert(main, to);
    }
  }

  /**
   *
   * @param main
   * @param debitCreditNote
   */
  public static void insertDebitCreditNoteByAccount(Main main, DebitCreditNote debitCreditNote, AccountingTransaction tran) {
    if (debitCreditNote.getInvoiceType().intValue() == SystemConstants.CREDIT_NOTE) {
      debitCreditNote.setInvoiceType(AccountingConstant.IS_CREDIT);
    }
    insertOutstanding(main, new TradeOutstanding(debitCreditNote), tran, debitCreditNote.getAssessableValue(), debitCreditNote.getIgstValue(), debitCreditNote.getNetValue());
    if (debitCreditNote.getInvoiceType().intValue() == AccountingConstant.IS_CREDIT) {
      debitCreditNote.setInvoiceType(SystemConstants.CREDIT_NOTE);
    }
//    if (debitCreditNote.getTaxableInvoice() != null && debitCreditNote.getTaxableInvoice() == 1) {
//      TradeOutstandingTaxService.insertDebitCreditNoteTaxByAccount(main, debitCreditNote);
//    }

  }

  public static void updateAccountGroup(MainView main) {
    AppService.updateSql(main, TradeOutstanding.class, "update fin_trade_outstanding set account_group_id = ? where accounting_transaction_id =? and account_group_id is null", false);
  }

  public static final Double selectCustomerOutstandingValue(Main main, Customer customer, AccountGroup accountGroup) {
    Double outstanding = null;

    outstanding = main.em().countDouble("select sum(value_remaining_balance) as outstanding from fin_trade_outstanding where customer_id = ? "
            + "and account_group_id = ? and company_id = ? and status = ?", new Object[]{customer.getId(), accountGroup.getId(), customer.getCompanyId().getId(), AccountingConstant.STATUS_NEW});

    return outstanding;
  }

  public static void deleteByEntityIdAndEntityType(Main main, Integer entityId, Integer entityType) {
    String sql = " delete from fin_trade_outstanding where entity_id = ? and entity_type_id = ? ";
    AppService.deleteSql(main, TradeOutstanding.class, sql, new Object[]{entityId, entityType});

  }

  public static void insertVendorClaimByAccount(Main main, VendorClaim vendorClaim, AccountingTransaction tran) {

    Double taxValue = vendorClaim.getTaxValueIgst() == null ? (vendorClaim.getTaxValueCgst() == null ? 0.0 : vendorClaim.getTaxValueCgst()) + (vendorClaim.getTaxValueSgst() == null ? 0.0 : vendorClaim.getTaxValueSgst()) : vendorClaim.getTaxValueIgst();
    insertOutstanding(main, new TradeOutstanding(vendorClaim), tran, vendorClaim.getCommissionAmount(), taxValue, vendorClaim.getCommissionClaim());
//    if (debitCreditNote.getInvoiceType().intValue() == AccountingConstant.IS_CREDIT) {
//      debitCreditNote.setInvoiceType(SystemConstants.CREDIT_NOTE);
//    }
//    if (debitCreditNote.getTaxableInvoice() != null && debitCreditNote.getTaxableInvoice() == 1) {
//      TradeOutstandingTaxService.insertDebitCreditNoteTaxByAccount(main, debitCreditNote);
//    }

  }

}
