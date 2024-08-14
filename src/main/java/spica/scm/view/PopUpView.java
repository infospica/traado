/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.service.AccountingLedgerService;
import spica.fin.service.AccountingLedgerTransactionService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.Product;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnSplit;
import spica.scm.service.ProductEntryService;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
@Named(value = "popUpView")
@ApplicationScoped
public class PopUpView implements Serializable {

  public void showCustomerPopUpForm(Integer entityId) {
    Jsf.popupForm(FileConstant.CUSTOMER, entityId, entityId);
  }

  public void productClassificationOpen(Product product) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.PRODUCT_CLASSIFFICATION, product != null ? product.getProductClassificationId() == null ? null : product.getProductClassificationId().getId() : null, product != null ? product.getProductClassificationId() == null ? null : product.getProductClassificationId().getId() : null); // opens a new form if id is null else edit
    }
  }

  public void showAccountGroup(Integer accountGroupId) {
    Jsf.popupForm(FileConstant.ACCOUNT_GROUP, accountGroupId, accountGroupId);
  }

  public void showTerritory(Integer territoryId) {
    Jsf.popupForm(FileConstant.TERRITORY, territoryId, territoryId);
  }

  public void showSalesAgent(Integer salesAgentId) {
    Jsf.popupForm(FileConstant.SALES_AGENT, salesAgentId, salesAgentId);
  }

  public void showPriceList(Integer priceListId) {
    Jsf.popupForm(FileConstant.SALES_AGENT, priceListId, priceListId);
  }

  public void showDepartment(Integer departmentId) {
    Jsf.popupForm(FileConstant.DEPARTMENT, departmentId, departmentId);
  }

  public void showDesignation(Integer designationId) {
    Jsf.popupForm(FileConstant.DESIGNATION, designationId, designationId);
  }

  public void showActiveAccountContractWithTab() {
    Account account = new Account();
    account.setId(UserRuntimeView.instance().getAccount().getId());
    Jsf.popupForm(FileConstant.ACCOUNT, account, UserRuntimeView.instance().getAccount().getId());
  }

  public void showSalesInvoice(Integer salesInvoiceId) {
    //Jsf.popupForm(FileConstant.SALES_INVOICE, salesInvoiceId, salesInvoiceId);
    Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesInvoiceView().replaceFirst("/scm", ""), salesInvoiceId, salesInvoiceId);
  }

  public void showProductCategory(Integer productCategoryId) {
    Jsf.popupForm(FileConstant.PRODUCT_CATEGORY, productCategoryId, productCategoryId); // opens a new form if id is null else edit
  }

  public void showCommodity(Integer commodityId) {
    Jsf.popupForm(FileConstant.COMMODITY, commodityId, commodityId);
  }

  public void showProductType(Integer productId) {
    Jsf.popupForm(FileConstant.PRODUCT_TYPE, productId, productId);
  }

  public void showProductPackingOpen(Integer productId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.PRODUCT_PACKING_DETAIL, productId);
    }
  }

  public void showManuFacture(Integer manufactureId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.MANUFACTURER, manufactureId, manufactureId); // opens a new form if id is null else edit
    }
  }

  public void showStockAdjustMent(Integer stockAdjId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.STOCK_ADJUSTMENT, stockAdjId, stockAdjId); // opens a new form if id is null else edit
    }
  }

  public void showSalesReturn(Integer salesReturnId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.SALES_RETURN, salesReturnId, salesReturnId); // opens a new form if id is null else edit
    }
  }

  public void showSalesReturnSplit(SalesReturnSplit salesReturnSplitId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupFormWithClose(FileConstant.SALES_RETURN_SPLIT, salesReturnSplitId.getId());
    }
  }

  public void showSalesReturnSplitById(Integer salesReturnSplitId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupFormWithClose(FileConstant.SALES_RETURN_SPLIT, salesReturnSplitId, salesReturnSplitId);
    }
  }

  public void showPurchaseReturn(Integer purchaseReturnId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.PURCHASE_RETURN, purchaseReturnId, purchaseReturnId); // opens a new form if id is null else edit
    }
  }

  public void showPurchase(Integer purchaseId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.PRODUCT_ENTRY_GST_INDIA, purchaseId, purchaseId); // opens a new form if id is null else edit
    }
  }

  public void showStockMovement(Integer stockMovementId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.STOCK_MOVEMENT, stockMovementId, stockMovementId); // opens a new form if id is null else edit
    }
  }

  public void showOpeningStock(Integer stockId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.OPENING_STOCK_ENTRY, stockId, stockId); // opens a new form if id is null else edit
    }
  }

  public static void showVendorClaim(Integer vendorCliamId) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.VENDOR_CLAIM, vendorCliamId, vendorCliamId); // opens a new form if id is null else edit
    }
  }

  public void showProduct(Integer id) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.SHOW_PRODUCT_MASTER, new Product(id), id); // opens a new form if id is null else edit
    }
  }

  public void showProduct(Product product) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.SHOW_PRODUCT_MASTER, product, product.getId()); // opens a new form if id is null else edit
    }
  }

  public void showProductForClone(Product product, String name) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      if (product == null || (product != null && product.getId() == null)) {
        product = new Product(null, name);
        product.setIsPopup(1);
      }
      Jsf.popupForm(FileConstant.SHOW_PRODUCT_MASTER, product, product.getId()); // opens a new form if id is null else edit
    }
  }

  public void showProductMaster(Integer id) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.SHOW_PRODUCT_MASTER_DETAIL, new Product(id), id); // opens a new form if id is null else edit
    }
  }

  public void showProductMaster(Product product) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.SHOW_PRODUCT_MASTER_DETAIL, product, product.getId()); // opens a new form if id is null else edit
    }
  }

  public void showAccountingTransaction(Integer id) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.ACCOUNTING_TRANSACTION, new AccountingTransaction(id), id); // opens a new form if id is null else edit
    }
  }

  public void showSupplier(Integer id) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.VENDOR, id, id);
    }
  }

  public void showLedgerBalance(Integer id) {
    showLedgerBalance(new AccountingLedger(id));
  }

  public void showLedgerBalance(AccountingLedger ledger) {
    if (UserRuntimeView.instance().getReferencePopupAllowed() && ledger != null) {
      Jsf.popupForm(AccountingLedgerTransactionService.LEDGER_TRANSACTION_POPUP, ledger, ledger.getId());
    }
  }

  public void showLedgerBalance(MainView main, Integer id, AccountGroup accoutGroup) {
    if (UserRuntimeView.instance().getReferencePopupAllowed() && id != null) {
      try {
        AccountingLedger acl = AccountingLedgerService.selectByPk(main, new AccountingLedger(id));
        acl.setAccountGroup(accoutGroup);
        showLedgerBalance(acl);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  /**
   *
   * @param main
   * @param item
   * @return
   */
  public void journalDetails(AccountingTransactionDetail detail) {
    if (UserRuntimeView.instance().getReferencePopupAllowed() && detail != null && detail.getAccountingTransactionId() != null) {
      Jsf.popupForm(AccountingLedgerTransactionService.JOURNAL_POPUP, detail.getAccountingTransactionId(), detail.getAccountingTransactionId().getId());
    }
  }

  public void showJournalByAccountingTransactionId(Integer transaction) {
    if (UserRuntimeView.instance().getReferencePopupAllowed() && transaction != null) {
      Jsf.popupForm(AccountingLedgerTransactionService.JOURNAL_POPUP, new AccountingTransaction(transaction), transaction);
    }
  }

  public void showPurchaseByDetailId(MainView main, Integer productEntryDetailId) {
    if (productEntryDetailId != null) {
      ProductEntry productEntry = ProductEntryService.getProductEntryByDetailId(main, productEntryDetailId);
      if (UserRuntimeView.instance().getReferencePopupAllowed()) {
        if (productEntry.getOpeningStockEntry() != null && productEntry.getOpeningStockEntry().intValue() == 0) {
          showPurchase(productEntry.getId());
        }
        if (productEntry.getOpeningStockEntry() != null && productEntry.getOpeningStockEntry().intValue() == 1) {
          showOpeningStock(productEntry.getId());
        }
        if (productEntry.getOpeningStockEntry() != null && productEntry.getOpeningStockEntry().intValue() == 2 && productEntry.getSalesReturnId() != null) {
          showSalesReturn(productEntry.getSalesReturnId().getId());
        }
      }
    }
  }

  public void showDebitCreditNote(Integer id) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.DEBIT_CREDIT_NOTE, id, id); // opens a new form if id is null else edit
    }
  }

  public void showCustomerOutstandingRefer(Integer entityId, Integer entityTypeId, Integer transactionId) {
    if (entityId == null && entityTypeId == null && transactionId == null) { //cheque in hand popup
      Jsf.popupForm(FileConstant.CHEQUE_ENTRY, transactionId, transactionId);
    } else if (entityTypeId == AccountingConstant.ACC_ENTITY_SALES.getId()) {
      showSalesInvoice(entityId);
    } else if (entityTypeId == AccountingConstant.ACC_ENTITY_SALES_RETURN.getId()) {
      showSalesReturn(entityId);
    } else if (entityTypeId == AccountingConstant.ACC_ENTITY_SALES_EXPENSES.getId()) {
      showSalesInvoice(entityId); //FIXME is this correct
    } else {
      Jsf.popupForm(FileConstant.ACCOUNTING_TRANSACTION, new AccountingTransaction(transactionId), transactionId);
    }
  }

  public void purchasefromSalesPopup(MainView main, ProductEntry productEntry) {
    Jsf.popupForm(FileConstant.PURCHASE_FROM_SALES, productEntry); // opens a new form if id is null else edit
  }

  public void showInvoices(Integer id, String type) {

    if (type.equals("SALES")) {
      showSalesInvoice(id);
    } else if (type.equals("SALES_RETURN")) {
      showSalesReturn(id);
    } else if (type.equals("PURCHASE_RETURN")) {
      showPurchaseReturn(id);
    } else if (type.equals("DEBIT_CREDIT")) {
      showDebitCreditNote(id);
    }
  }

  public void salesReturnfromPurchaseReturnPopup(MainView main, SalesReturn salesReturn) {
    Jsf.popupForm(FileConstant.SALES_RETURN_FROM_PUR_RET, salesReturn); // opens a new form if id is null else edit
  }

  public void openSalesExpenseLedger() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.ACCOUNTING_LEDGER, new Product(), null);
    }
  }

  public void openSalesExpenseLedger2() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.ACCOUNTING_LEDGER, new Product(), null);
    }
  }

  public void openConsignment(ProductEntry productEntry) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      // opens a new form if id is null else edit
      Jsf.popupForm(FileConstant.CONSIGNMENT_RECEIPT_INFO, productEntry);
    }
  }

  public void showCompanySmsLogInDetail(Company company) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.SMS_LOG_DETAIL, company.getId(), company.getId());
    }

  }

  public void accountGroupDocPrefixEditDialog(Integer prefixId) {
    Jsf.popupForm(FileConstant.ACCOUNT_GROUP_DOC_PREFIX, prefixId, prefixId);

  }

  public void financialYearListPopup(MainView main, CompanySettings companySettings) {
    Jsf.popupForm(FileConstant.FINANCIAL_YEAR, companySettings); // opens a new form if id is null else edit
  }
}
