/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.fin.view;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import spica.fin.common.FilterObjects;
import spica.fin.domain.AccountingLedgerBalance;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.service.AccountingTransactionDetailItemService;
import wawo.app.faces.MainView;

/**
 *
 * @author arun
 */
public class OutstandingUtil {

  /**
   *
   * @param main
   */
  public static void addPayment(MainView main, AccountingTransactionDetail td, FilterObjects filterObjects) {
    addPayment(main, td, filterObjects, null);
  }

  public static void addPayment(MainView main, AccountingTransactionDetail td, FilterObjects filterObjects, Integer chequeEntryId) {
    td.setDetailItemPayable(AccountingTransactionDetailItemService.listPagedByPayable(main, td.getAccountingLedgerId().getId(), filterObjects, td.isShowAllBills(), chequeEntryId));
    calculatePayableRunningBalance(td, new Date().getTime());
    td.setDetailItemReceivable(AccountingTransactionDetailItemService.listPagedByReceivable(main, td.getAccountingLedgerId().getId(), filterObjects, chequeEntryId));
    calculateReceivableRunningBalance(td, new Date().getTime());
  }

  public static void addReceipt(MainView main, AccountingTransactionDetail td, FilterObjects filterObjects) {
    addReceipt(main, td, filterObjects, null);
  }

  public static void addReceipt(MainView main, AccountingTransactionDetail td, FilterObjects filterObjects, Integer chequeEntryId) {
    td.setDetailItemReceivable(AccountingTransactionDetailItemService.listPagedByPayable(main, td.getAccountingLedgerId().getId(), filterObjects, td.isShowAllBills(), chequeEntryId));
    calculateReceivableRunningBalance(td, new Date().getTime());
    td.setDetailItemPayable(AccountingTransactionDetailItemService.listPagedByReceivable(main, td.getAccountingLedgerId().getId(), filterObjects, chequeEntryId));
    calculatePayableRunningBalance(td, new Date().getTime());
  }

  private static void calculatePayableRunningBalance(AccountingTransactionDetail td, long today) {
    Double runningBalance = 0.0;
    long diff;
    for (AccountingTransactionDetailItem item : td.getDetailItemPayable()) {
      diff = today - item.getDocumentDate().getTime();
      item.setDueDays(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
      runningBalance += item.getBalanceAmount();
      item.setRunningBalance(runningBalance);
    }
  }

  private static void calculateReceivableRunningBalance(AccountingTransactionDetail td, long today) {
    Double runningBalance = 0.0;
    long diff;
    for (AccountingTransactionDetailItem item : td.getDetailItemReceivable()) {
      diff = today - item.getDocumentDate().getTime();
      item.setDueDays(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
      runningBalance += item.getBalanceAmount();
      item.setRunningBalance(runningBalance);
    }
  }

  public static void setDueDays(long today, AccountingLedgerBalance alb) {
    long diff = today - alb.getEntryDate().getTime();
    alb.setDueDays(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
  }
}
