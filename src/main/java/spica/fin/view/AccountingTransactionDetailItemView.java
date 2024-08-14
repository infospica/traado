/*
 * @(#)AccountingTransactionDetailItemView.java	1.0 Wed Aug 23 18:04:40 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.service.AccountingTransactionDetailItemService;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingDocType;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionSettlement;
import spica.fin.domain.ChequeEntry;
import static spica.fin.service.LedgerExternalDataService.getDetailItemNew;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountExpenseDetail;
import spica.fin.service.delete.AccountExpenseDetailService;
import spica.fin.service.ChequeEntryService;
import spica.fin.service.LedgerExternalDataService;
import spica.scm.domain.AccountGroup;
import spica.scm.util.MathUtil;
import spica.scm.view.PopUpView;
import spica.scm.view.ScmLookupExtView;
import spica.scm.view.ScmLookupView;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.entity.util.UniqueCheck;

/**
 * AccountingTransactionDetailItemView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Aug 23 18:04:40 IST 2017
 */
@Named(value = "actDetailItemView")
@ViewScoped
public class AccountingTransactionDetailItemView implements Serializable {

  private AccountingTransactionDetail accountingTransactionDetail;

  // private transient FilterObjects filterObjects;
  private Double totalAmount;
  private Double paymentAmount;
  private Double receivableAmount;
  private Double balanceAmount;

  private boolean isAutoFill;
  private List<ChequeEntry> chequeEntryList;

  private String headerValue;
  private String headerPayable;
  private String headerReceivale;
  private List<AccountGroup> accountGroupList;
  private boolean accountGroupError = false;
  private boolean savedSuccess = false;

  @Inject
  private AccountingMainView accountingMainView;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    setAccountingTransactionDetail((AccountingTransactionDetail) Jsf.popupParentValue(AccountingTransactionDetail.class));
    accountingTransactionDetail.setAccountingTransactionDetailItemSelected(null);
//    accountingMainView.setSelectedAccountGroup(null);
    accountingTransactionDetail.setShowAllBills(false);
  }

  /**
   * Default Constructor.
   */
  public AccountingTransactionDetailItemView() {
    super();
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountingTransactionDetailItem(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        savedSuccess = false;
        if (main.isNew() && !main.hasError()) {
          addHeader();
          chequeEntryList = null;
          AccountingTransaction tran = getAccountingTransactionDetail().getAccountingTransactionId();
          addNewTransactionItem();
          if (getAccountingTransactionDetail() != null && getAccountingTransactionDetail().getId() == null) {

            if (!(StringUtil.isEmpty(getAccountingTransactionDetail().getDetailItemPayable()) || StringUtil.isEmpty(getAccountingTransactionDetail().getDetailItemReceivable()))) {
              // edit = true;
              // calculateAmounts();
              // return null;
            }
            if (tran.isReceipt() || tran.isPayment()) {
              addReceiptOrPayment(main);
              accountGroupList = null;
              lookupGroupAuto();
            } else if (tran.isPurchase() || tran.isExpenses() || tran.isCreditNote()) {
            } else if (tran.isSales() || tran.isDebitNote()) {
            }
          } else {
          }
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  public void accountGroupChanged(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      accountingTransactionDetail.setAccountingTransactionDetailItemSelected(null);
      accountingTransactionDetail.setShowAllBills(false);
      accountingMainView.setSelectedAccountGroup((AccountGroup) event.getObject());
      addReceiptOrPayment(main);
      accountingTransactionDetail.getAccountingTransactionId().setAccountGroupId(accountingMainView.getSelectedAccountGroup());
      docTypeChanged(null);//to reload based on a/c group
      settlementChequeEntryItem(main, setAndGetChequeEntryId());
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  public void showAllEvent(MainView main) {
    try {
      addReceiptOrPayment(main);
      accountingTransactionDetail.getAccountingTransactionId().setAccountGroupId(accountingMainView.getSelectedAccountGroup());
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  private void addReceiptOrPayment(MainView main) {
    if (getAccountingTransactionDetail().getAccountingTransactionId().isPayment()) {
      OutstandingUtil.addPayment(main, getAccountingTransactionDetail(), accountingMainView);
      calculateBalanceAmount();
    }
    if (getAccountingTransactionDetail().getAccountingTransactionId().isReceipt()) {
      OutstandingUtil.addReceipt(main, getAccountingTransactionDetail(), accountingMainView);
      calculateBalanceAmount();
    }
  }

  private void addHeader() {
    AccountingTransaction tran = getAccountingTransactionDetail().getAccountingTransactionId();
    headerValue = tran.getVoucherTypeId().getTitle() + " / " + AccountingTransactionDetailItemService.HEADER_VALUE_DOC;
    headerPayable = tran.isPayment() ? AccountingTransactionDetailItemService.HEADER_VALUE_PAYABLE : AccountingTransactionDetailItemService.HEADER_VALUE_RECEIVABLE;
    headerReceivale = tran.isPayment() ? AccountingTransactionDetailItemService.HEADER_VALUE_RECEIVABLE : AccountingTransactionDetailItemService.HEADER_VALUE_PAYABLE;
  }

  public List<ChequeEntry> getChequeEntryList() {
    if (chequeEntryList == null && accountingTransactionDetail.getId() == null && accountingTransactionDetail.getAccountingTransactionId().isReceipt()) {
      chequeEntryList = FinLookupView.checkEntryNonReconciled(getAccountingTransactionDetail().getAccountingLedgerId(), accountingMainView);
    }
    return chequeEntryList;
  }

  public void docTypeChanged(AjaxBehaviorEvent event) {
    if (accountingTransactionDetail.getAccountingTransactionId().isPayment() || accountingTransactionDetail.getAccountingTransactionId().isReceipt()) {
      AccountingTransactionDetailItem item = accountingTransactionDetail.getDetailItem().get(0);
      item.setChequeEntryId(null);

      if (!StringUtil.isEmpty(chequeEntryList)) {
        reset(item);
      }
      chequeEntryList = null;
      accountingTransactionDetail.setAccountingTransactionDetailItemSelected(null);
      getChequeEntryList();
      chequeSelect = (!StringUtil.isEmpty(chequeEntryList) && item.getDocumentTypeId() != null && item.getDocumentTypeId().isCheque() && item.getId() == null);
      if (chequeSelect) {
        reset(item);
      }
      calculateBalanceAmount();
    }
  }

  public void chequeChanged(AjaxBehaviorEvent event) {
    if (hasChange && accountingTransactionDetail.getDetailItem() != null) {
      hasChange = false;
      MainView main = Jsf.getMain();
      try {
        settlementChequeEntryItem(main, setAndGetChequeEntryId());
        calculateAmounts();
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  private void reset(AccountingTransactionDetailItem item) {
    item.setNetAmount(null);
    item.setDocumentDate(null);
    item.setDocumentNumber(null);
  }

  private Integer setAndGetChequeEntryId() {
    for (AccountingTransactionDetailItem tdi : accountingTransactionDetail.getDetailItem()) {
      if (tdi.getChequeEntryId() != null) {
        tdi.setDocumentDate(tdi.getChequeEntryId().getChequeDate());
        tdi.setNetAmount(tdi.getChequeEntryId().getAmount());
        tdi.setDocumentNumber(tdi.getChequeEntryId().getChequeNo());
        return tdi.getChequeEntryId().getId();
        //  tdi.getAccountingTransactionDetailId().getAccountingTransactionId().setNarration(tdi.getChequeEntryId().getn);
        // tdi.setNote(tdi.getChequeEntryId().getNote()); title="#{detailItemVar.note}"
      } else if (chequeSelect) {
        reset(tdi);
      }
    }
    return null;
  }

  private transient boolean hasChange = false;

  public void chequeValueChanged(ValueChangeEvent event) {
    if ((event.getOldValue() == null || event.getNewValue() == null) || (!event.getNewValue().equals(event.getOldValue()))) {
      hasChange = true;
    }
  }

  private void uploadFiles() {
    String SUB_FOLDER = "fin_accounting_transaction_detail_item/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public void saveAccountingTransactionDetailItem(MainView main) {
    savedSuccess = true;
    saveOrCloneItem(main, false);
  }

  private transient boolean settleFirst = false; //if true settle with adjustment else settle payment first  //FIXME move to sys config

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private boolean saveOrCloneItem(MainView main, boolean selfAdjust) {
    try {
      uploadFiles(); //File upload

      //  getAccountingTransactionDetail().getDetailItem().removeIf(atdItem -> atdItem.getNetAmount() == null);
      if (!StringUtil.isEmpty(accountingTransactionDetail.getAccountingTransactionDetailItemSelected())) {
        List<AccountingTransactionDetailItem> receivable = new ArrayList();
        if (settleFirst) { //First do the settlement then do the payment else reverse order
          addReceivableArray(accountingTransactionDetail.getAccountingTransactionDetailItemSelected(), receivable);
          addReceivable(accountingTransactionDetail.getDetailItem(), receivable);
        } else {
          addReceivable(accountingTransactionDetail.getDetailItem(), receivable);
          addReceivableArray(accountingTransactionDetail.getAccountingTransactionDetailItemSelected(), receivable);
        }
        if (isValid(main)) {
          doSettlement(accountingTransactionDetail.getDetailItemPayable(), receivable);
        } else {
          return false;
        }
      } else {
        if (isValid(main)) {
          doSettlement(accountingTransactionDetail.getDetailItemPayable(), getAccountingTransactionDetail().getDetailItem());
        } else {
          return false;
        }
      }
      if (!selfAdjust) {
        dialogClose();
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return true;
  }

  private boolean isValid(MainView main) {
    accountGroupError = false;
    if (!StringUtil.isEmpty(accountingTransactionDetail.getDetailItem())) {
      AccountingTransactionDetailItem item = accountingTransactionDetail.getDetailItem().get(0);
      if (item.getNetAmount() == null) {
        return false;
      }
      if (accountingTransactionDetail.getAccountingTransactionId().isJournalSettlement()) {
        if (!StringUtil.isEmpty(acountingDocTypes) && StringUtil.isEmpty(item.getDocumentNumber())) {
          item.setDocumentNumber(acountingDocTypes.get(0).getTitle());
        }
      }
    }
    if (accountingMainView.getSelectedAccountGroup() == null) {
      if (!StringUtil.isEmpty(lookupGroupAuto())) {
        //  boolean hasAdjustment = false;
        if (lookupGroupAuto().size() < 2) {
          accountingMainView.setSelectedAccountGroup(lookupGroupAuto().get(0));
          accountingTransactionDetail.getAccountingTransactionId().setAccountGroupId(accountingMainView.getSelectedAccountGroup());
        } else { //looping and checking for unique a/g
          Integer id = null;
          AccountGroup ag = null;
          AccountingTransactionDetailItem it = accountingTransactionDetail.getDetailItem().get(0);
          if (!StringUtil.isEmpty(accountingTransactionDetail.getDetailItemPayable())) {
            for (AccountingTransactionDetailItem item : accountingTransactionDetail.getDetailItemPayable()) {
              if (item.getAdjustmentAmount() != null && item.getAdjustmentAmount() > 0) {
                //   hasAdjustment = true;
                ag = item.getAccountingTransactionDetailId().getAccountingTransactionId().getAccountGroupId();
                if (ag != null && id == null) {
                  id = ag.getId();
                }
                if (ag != null && id != null && (id.intValue() != ag.getId() || (it.getChequeEntryId() != null && it.getChequeEntryId().getAccountGroup() != null && ag.getId().intValue() != it.getChequeEntryId().getAccountGroup().getId()))) {
                  accountGroupError = true;
                  return false;
                }
              }
            }
            if (!StringUtil.isEmpty(accountingTransactionDetail.getAccountingTransactionDetailItemSelected())) {
              for (AccountingTransactionDetailItem item : accountingTransactionDetail.getAccountingTransactionDetailItemSelected()) {
                ag = item.getAccountingTransactionDetailId().getAccountingTransactionId().getAccountGroupId();
                if (ag != null && id == null) {
                  id = ag.getId();
                }
                if (ag != null && (id.intValue() != ag.getId() || (it.getChequeEntryId().getAccountGroup() != null && ag.getId().intValue() != it.getChequeEntryId().getAccountGroup().getId()))) {
                  accountGroupError = true;
                  return false;
                }
              }
            }

          }
          if (id != null) {
            accountingMainView.setSelectedAccountGroup(ag);
            accountingTransactionDetail.getAccountingTransactionId().setAccountGroupId(accountingMainView.getSelectedAccountGroup());
          } else { //TODO comment else not required here handle in the main tran save for multi account
            accountGroupError = true;
            return false;
          }
        }
      }
    }
    return true;
  }

  private void addReceivable(List<AccountingTransactionDetailItem> items, List<AccountingTransactionDetailItem> receivable) {
    for (AccountingTransactionDetailItem item : items) {
      receivable.add(item);
    }
  }

  private void addReceivableArray(AccountingTransactionDetailItem[] items, List<AccountingTransactionDetailItem> receivable) {
    for (AccountingTransactionDetailItem item : items) {
      receivable.add(item);
    }
  }

  /**
   * Delete one or many AccountingTransactionDetailItem.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingTransactionDetailItem(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(accountingTransactionDetail.getAccountingTransactionDetailItemSelected())) {
        AccountingTransactionDetailItemService.deleteByPkArray(main, accountingTransactionDetail.getAccountingTransactionDetailItemSelected()); //many record delete from list
        main.commit("success.delete");
        accountingTransactionDetail.setAccountingTransactionDetailItemSelected(null);
      } else {
        AccountingTransactionDetailItemService.deleteByPk(main, getAccountingTransactionDetail().getDetailItem().get(0));  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
          main.setViewType(ViewTypes.newform);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   */
  private void addNewTransactionItem() {
    AccountingTransaction tran = accountingTransactionDetail.getAccountingTransactionId();

    AccountingTransactionDetailItem item = null;
    if (!StringUtil.isEmpty(accountingTransactionDetail.getDetailItem())) {
      item = accountingTransactionDetail.getDetailItem().get(0);
    } else {
      item = getDetailItemNew(accountingTransactionDetail, null, null, null, null, null, null);
    }
    item.setDocumentDate(accountingTransactionDetail.getAccountingTransactionId().getEntryDate());
    chequeSelect = false;
    if (tran.isPayment()) {
      item.setDocumentTypeId(find(AccountingConstant.DOC_TYPE_PAYMENT_CHEQUE));
    } else if (tran.isReceipt()) {
      getChequeEntryList();
//      item.setDocumentTypeId(AccountingConstant.DOC_TYPE_RECEIPT_CHEQUE);
      chequeSelect = (!StringUtil.isEmpty(chequeEntryList) && (item.getDocumentTypeId() != null && item.getDocumentTypeId().isCheque()) && item.getId() == null);

    } else {
      if (tran.getEntityId() == null) { //TODO where is it needed and why not purchase
        //  tran.setEntityId(accountingTransactionDetail.getAccountingLedgerId().getEntityId());
        //  tran.setAccountingEntityTypeId(accountingTransactionDetail.getAccountingLedgerId().getAccountingEntityTypeId());
      }
    }
    Double val = null;
//    if ((tran.isReceipt() || tran.isPayment())) {
//      val = tran.getTotalAmount();
//    } else
    if (tran.isPurchase() || tran.isCreditNote() || tran.isExpenses() || tran.isReceipt()) { //Receipt comes here
      val = accountingTransactionDetail.getCredit();
    } else if (tran.isSales() || tran.isDebitNote() || tran.isPayment()) { // Payment comes here
      val = accountingTransactionDetail.getDebit();
    }
    if (val != null) {
      item.setAdjustmentAmount(val);
      item.setNetAmount(val);
      item.setBalanceAmount(val);
    }
  }

  public boolean showAccountGroup() {
    return (accountingTransactionDetail != null && (accountingTransactionDetail.getAccountingTransactionId().isJournal() || accountingTransactionDetail.getAccountingTransactionId().isPayment() || accountingTransactionDetail.getAccountingTransactionId().isReceipt()) && accountingTransactionDetail.getAccountingLedgerId() != null && accountingTransactionDetail.getAccountingLedgerId().isDebtorsOrCreditors());
  }

  public List<AccountGroup> lookupGroupAuto() {

    if (showAccountGroup() && accountGroupList == null) {
      if (accountingTransactionDetail.getAccountingLedgerId().isCustomer()) {
        accountGroupList = ScmLookupExtView.accountGroupByCustomerId(accountingTransactionDetail.getAccountingLedgerId().getEntityId());
      } else if (accountingTransactionDetail.getAccountingLedgerId().getEntityId() != null) {
        accountGroupList = ScmLookupExtView.accountGroupByVendor(accountingTransactionDetail.getAccountingLedgerId().getEntityId());
      }
//accountGroupList = FinLookupView.accountGroupAuto(accountingTransactionDetail.getAccountingLedgerId());
    }
    return accountGroupList;
  }

  /**
   *
   * @param main
   * @param transactionItem
   */
  public void actionDeleteTransactionItem(MainView main, AccountingTransactionDetailItem transactionItem) {
    try {
      if (transactionItem != null && transactionItem.getId() == null) {
        accountingTransactionDetail.removeDetailItem(transactionItem);
      }
      if (StringUtil.isEmpty(accountingTransactionDetail.getDetailItem())) {
        addNewTransactionItem();
      }
      calculateAmounts();
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  /**
   *
   */
  public void dialogClose() {
    if (getAccountingTransactionDetail() != null && !StringUtil.isEmpty(getAccountingTransactionDetail().getDetailItem())) {
      AccountingTransaction tran = getAccountingTransactionDetail().getAccountingTransactionId();
      if (tran.isPayment() && !tran.isJournalSettlement()) {
        getAccountingTransactionDetail().setDebit(getAccountingTransactionDetail().getDetailItem().get(0).getNetAmount());
      } else if (tran.isReceipt() && !tran.isJournalSettlement()) {
        getAccountingTransactionDetail().setCredit(getAccountingTransactionDetail().getDetailItem().get(0).getNetAmount());
      }
    }
    Jsf.popupReturn(AccountingTransactionDetail.class, getAccountingTransactionDetail());

  }

  public void remoteCloseCommand(MainView main) {
    if (!savedSuccess) {
      savedSuccess = saveOrCloneItem(main, false);
    }
    if (savedSuccess) {
      if (!StringUtil.isEmpty(accountingTransactionDetail.getDetailItem())) {
        AccountingTransactionDetailItem item = accountingTransactionDetail.getDetailItem().get(0);
        if (StringUtil.isEmpty(item.getDocumentNumber())) {
          main.error("documentno.required");
          return;
        }
      }
      if (!accountGroupError) {
        Jsf.closePopup(getAccountingTransactionDetail());
        Jsf.execute("parent.transactionDetailItemReturned()");
      } else {
        main.error("account.group.required");
      }
    }
    savedSuccess = false;
  }

  /**
   *
   * @param event
   */
  public void onRowSelect(SelectEvent event) {
    calculateBalanceAmount();
  }

  /**
   *
   * @param event
   */
  public void onRowUnselect(UnselectEvent event) {
    calculateBalanceAmount();
  }

  public void onToggleSelect(ToggleSelectEvent event) {
    calculateBalanceAmount();
  }

  /**
   *
   */
  public void calculateBalanceAmount() {
    resetAdjustment();
    if (isAutoFill) {
      autoFill(accountingTransactionDetail.getDetailItemPayable(), false);
    }
  }

  private void resetAdjustment() {
    for (AccountingTransactionDetailItem item : accountingTransactionDetail.getDetailItemPayable()) {
      item.setAdjustmentAmount(0.0);
      item.setStatus(AccountingConstant.STATUS_NEW);
    }
    for (AccountingTransactionDetailItem item : getAccountingTransactionDetail().getDetailItem()) {
      item.setAdjustmentAmount(0.0);
      item.setBalanceAmount(item.getNetAmount());
      item.setStatus(AccountingConstant.STATUS_NEW);
    }
    calculateAmounts();
  }

  private transient Double adjTotal; //to show the current running total
  private transient Double oldAdjVal;
  private transient Double newAdjVal;

  public void adjustmentValueChange(ValueChangeEvent e) {
    oldAdjVal = (e.getOldValue() != null) ? Double.valueOf(String.valueOf(e.getOldValue())) : null;
    newAdjVal = (e.getNewValue() != null) ? Double.valueOf(String.valueOf(e.getNewValue())) : null;
  }

  public void resetBalanceAmount(AccountingTransactionDetailItem item) {

    if (oldAdjVal != null) {
      changeBalance(item, oldAdjVal);
    }
    if (newAdjVal != null) {
      changeBalance(item, null);
    }
  }

  public void setAdjustmentBal(AccountingTransactionDetailItem item, boolean add) {
    if (add) {
      item.setAdjustmentAmount(item.getBalanceAmount());
      changeBalance(item, null);
    } else {
      changeBalance(item, item.getAdjustmentAmount());
      item.setAdjustmentAmount(0.00);
      changeBalance(item, 0.00); //To change color
    }
  }

  public void autoFill(List<AccountingTransactionDetailItem> itemList, boolean reset) {
    if (reset) {
      resetAdjustment();
    }
    for (AccountingTransactionDetailItem item : itemList) {
      setAdjustmentBal(item, isAutoFill);
    }
  }

  /**
   *
   */
  private void calculateAmounts() {
    totalAmount = 0.0;
    paymentAmount = 0.0;
    receivableAmount = 0.0;
    balanceAmount = 0.0;
    adjTotal = 0.0;
    for (AccountingTransactionDetailItem atdi : accountingTransactionDetail.getDetailItem()) {
      if (atdi.getNetAmount() != null) {
        totalAmount += atdi.getNetAmount();
        paymentAmount += atdi.getNetAmount();
        balanceAmount += atdi.getNetAmount();
      }
    }
    if (accountingTransactionDetail.getAccountingTransactionDetailItemSelected() != null) { //Adding the receivable 
      for (AccountingTransactionDetailItem atd : accountingTransactionDetail.getAccountingTransactionDetailItemSelected()) {
        receivableAmount += atd.getBalanceAmount();
      }
      totalAmount = paymentAmount + receivableAmount;
      balanceAmount = paymentAmount + receivableAmount;
    }
    for (AccountingTransactionDetailItem atdi : accountingTransactionDetail.getDetailItemPayable()) {
      if (atdi.getAdjustmentAmount() == null) {
        atdi.setAdjustmentAmount(0.0);
      }
      changeBalance(atdi, null);
    }
  }

  private void changeBalance(AccountingTransactionDetailItem atdi, Double addBack) {
    if (addBack != null) {
      balanceAmount += addBack;
      adjTotal -= addBack;
    } else {
      if (atdi.getAdjustmentAmount() > balanceAmount) {
        atdi.setAdjustmentAmount(balanceAmount);
      }
      balanceAmount -= atdi.getAdjustmentAmount();
      adjTotal += atdi.getAdjustmentAmount();
    }

    if (atdi.getAdjustmentAmount() < atdi.getBalanceAmount() && atdi.getAdjustmentAmount() != 0) {
      atdi.setFontColor(AccountingTransactionDetailItemService.COLOR_BLUE);
    } else if (atdi.getAdjustmentAmount().doubleValue() == atdi.getBalanceAmount().doubleValue()) {
      atdi.setFontColor(AccountingTransactionDetailItemService.COLOR_GREEN);
    }
    if (atdi.getAdjustmentAmount() == 0) {
      atdi.setFontColor(AccountingTransactionDetailItemService.COLOR_BLACK);
    }
  }

  /**
   *
   * @param payableList
   * @param receivableList
   */
  public void doSettlement(List<AccountingTransactionDetailItem> payableList, List<AccountingTransactionDetailItem> receivableList) {
    //   boolean hasNext = true;
    accountingTransactionDetail.getSettlementList().clear();

    for (AccountingTransactionDetailItem payable : payableList) {
      if (payable.getStatus() == AccountingConstant.STATUS_PARTIAL_PROCESSED || payable.getStatus() == AccountingConstant.STATUS_NEW) {
        for (AccountingTransactionDetailItem recieve : receivableList) {
          if (recieve.getStatus() == AccountingConstant.STATUS_PARTIAL_PROCESSED || recieve.getStatus() == AccountingConstant.STATUS_NEW) {
            if (payable.getAdjustmentAmount() == null) {
              payable.setAdjustmentAmount(0.0);
            }
            if (payable.getAdjustmentAmount().doubleValue() > recieve.getBalanceAmount().doubleValue() && payable.getAdjustmentAmount().doubleValue() > 0) {
              payable.setBalanceAmount(MathUtil.roundOff(payable.getBalanceAmount().doubleValue() - recieve.getBalanceAmount().doubleValue(), 2));
              payable.setAdjustmentAmount(MathUtil.roundOff(payable.getAdjustmentAmount().doubleValue() - recieve.getBalanceAmount().doubleValue(), 2));
              LedgerExternalDataService.settle(accountingTransactionDetail, payable, recieve, recieve.getBalanceAmount());
              recieve.setBalanceAmount(0.00);
              recieve.setStatus(AccountingConstant.STATUS_PROCESSED);
              payable.setStatus(AccountingConstant.STATUS_PARTIAL_PROCESSED);
              continue;
            } else if (payable.getAdjustmentAmount().doubleValue() < recieve.getBalanceAmount().doubleValue() && payable.getAdjustmentAmount().doubleValue() > 0) {
              recieve.setBalanceAmount(MathUtil.roundOff(recieve.getBalanceAmount().doubleValue() - payable.getAdjustmentAmount().doubleValue(), 2));
              payable.setBalanceAmount(MathUtil.roundOff(payable.getBalanceAmount().doubleValue() - payable.getAdjustmentAmount().doubleValue(), 2));
              LedgerExternalDataService.settle(accountingTransactionDetail, payable, recieve, payable.getAdjustmentAmount());
              if (payable.getBalanceAmount() > 0) {
                payable.setStatus(AccountingConstant.STATUS_PARTIAL_PROCESSED);
              } else {
                payable.setStatus(AccountingConstant.STATUS_PROCESSED);
              }
              break;
            } else if (payable.getAdjustmentAmount().doubleValue() == recieve.getBalanceAmount().doubleValue() && payable.getAdjustmentAmount().doubleValue() > 0) {
              payable.setBalanceAmount(MathUtil.roundOff(payable.getBalanceAmount().doubleValue() - payable.getAdjustmentAmount().doubleValue(), 2));
              recieve.setBalanceAmount(0.00);
              LedgerExternalDataService.settle(accountingTransactionDetail, payable, recieve, payable.getAdjustmentAmount());
              break;
            } else if (recieve.getBalanceAmount().doubleValue() > 0 && payable.getAdjustmentAmount().doubleValue() == 0) { //balance as new advance
              if (accountingTransactionDetail.getAccountingTransactionId().isReceipt()) {
                recieve.setRecordType(AccountingConstant.RECORD_TYPE_PAYABLE);
              } else if (accountingTransactionDetail.getAccountingTransactionId().isPayment()) {
                recieve.setRecordType(AccountingConstant.RECORD_TYPE_RECEIVABLE);
              }
            }
          }
        }
      }
    }
    if (!StringUtil.isEmpty(accountingTransactionDetail.getDetailItem())) {
      AccountingTransactionDetailItem item = accountingTransactionDetail.getDetailItem().get(0);

      String pay = getPayNarration(item);
      if (!StringUtil.isEmpty(accountingTransactionDetail.getSettlementList())) {
        //  item = accountingTransactionDetail.getDetailItem().get(0);
        String bills = pay + ", Doc No";
        String adj = " Adj No";

        boolean adjTrue = false;
        String ref;
        UniqueCheck uk = new UniqueCheck();
        for (AccountingTransactionSettlement settled : accountingTransactionDetail.getSettlementList()) {
          ref = getReference(settled.getTransactionDetailItemId(), uk);
          if (ref != null) {
            bills += " [" + ref + "]";
          }
          if (!item.equals(settled.getAdjustedTransactionDetailItemId())) {
            ref = getReference(settled.getAdjustedTransactionDetailItemId(), uk);
            if (ref != null) {
              adj += " [" + ref + "]";
              adjTrue = true;
            }
          }
        }
        uk.close();
        if (adjTrue) {
          bills += adj;
        }
        for (AccountingTransactionDetailItem tdi : accountingTransactionDetail.getDetailItem()) {
          if (tdi.getChequeEntryId() != null) {
            if (!StringUtil.isEmpty(tdi.getChequeEntryId().getNote())) {
              bills += ", Cheque Note [" + tdi.getChequeEntryId().getNote() + "]";
            }
          }
        }
        accountingTransactionDetail.getAccountingTransactionId().setNarration(bills);
      } else if (accountingTransactionDetail.getAccountingTransactionId().isPayment()) {
        //item = accountingTransactionDetail.getDetailItem().get(0);
        String nar = "Advance Paid";
        if (!accountingTransactionDetail.getAccountingLedgerId().isDebtorsOrCreditors()) {
          nar = "Paid";
        }
        if (!StringUtil.isEmpty(item.getDocumentNumber())) {
          nar += " Doc No [" + item.getDocumentNumber() + "]";
        }
        accountingTransactionDetail.getAccountingTransactionId().setNarration(nar);
      } else if (accountingTransactionDetail.getAccountingTransactionId().isReceipt()) {
        // item = accountingTransactionDetail.getDetailItem().get(0);
        String nar = "Advance Received";
        if (!accountingTransactionDetail.getAccountingLedgerId().isDebtorsOrCreditors()) {
          nar = "Received";
        }
        if (!StringUtil.isEmpty(item.getDocumentNumber())) {
          nar += " Doc No [" + item.getDocumentNumber() + "]";
        }
        for (AccountingTransactionDetailItem tdi : accountingTransactionDetail.getDetailItem()) {
          if (tdi.getChequeEntryId() != null) {
            if (!StringUtil.isEmpty(tdi.getChequeEntryId().getNote())) {
              nar += ", Cheque Note [" + tdi.getChequeEntryId().getNote() + "]";
            }
          }
        }
        accountingTransactionDetail.getAccountingTransactionId().setNarration(nar);
      } else {
        accountingTransactionDetail.getAccountingTransactionId().setNarration(pay);
      }
    }
    if (!StringUtil.isEmpty(accountingTransactionDetail.getAccountingTransactionId().getNarration())) {
      if (!StringUtil.isEmpty(accountingTransactionDetail.getAccountingTransactionId().getTempNote())) {
        accountingTransactionDetail.getAccountingTransactionId().setTempNote(" " + accountingTransactionDetail.getAccountingTransactionId().getTempNote());
      }
    }
    //   accountingTransactionDetail.getAccountingTransactionId().setNarration(accountingTransactionDetail.getAccountingTransactionId().getNarration() + accountingTransactionDetail.getAccountingTransactionId().getTempNote());
  }

  private String getReference(AccountingTransactionDetailItem item, UniqueCheck uk) {
    String s = item.getAccountingTransactionDetailId().getAccountingTransactionId().getDocumentNumber();
    boolean isNew = false;
    if (!StringUtil.isEmpty(item.getDocumentNumber())) {
      if (!uk.exist(item.getDocumentNumber())) {
        s += ", ";
        s = item.getDocumentNumber();
        isNew = true;
      }
    }
    if (!StringUtil.isEmpty(item.getReferNumber())) {
      if (!uk.exist(item.getReferNumber())) {
        s += ", ";
        s += item.getReferNumber();
        isNew = true;
      }
    }
    if (isNew) {
      return s;
    } else {
      return null;
    }
  }

  public static void showDetails(AccountingTransactionDetailItem item) {
    AccountingTransaction tran = item.getAccountingTransactionDetailId().getAccountingTransactionId();
    MainView main = Jsf.getMain();
    try {
      //TODO fixme debig and see some issues     
      if (tran.isPurchase() || tran.isPayment()) {
        if (tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_EXPENSES.getId()) {
          AccountExpenseDetail expenseDetail = AccountExpenseDetailService.selectByAccountingTransaction(main, item.getAccountingTransactionDetailId().getAccountingTransactionId().getId());
          if (expenseDetail != null) {
            if (expenseDetail.isIsReimbursable()) {
              Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getProductEntryView().replaceFirst("/scm", ""), tran.getEntityId(), tran.getEntityId());
            } else {
              Jsf.popupForm(FileConstant.PURCHASE_EXPENSE_REIMBURSE, item, tran.getEntityId());
            }
          }
        } else if (tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_PURCHASE.getId()) {
          if (tran.isPurchase()) {
            Jsf.popupForm(FileConstant.PRODUCT_ENTRY_GST_INDIA, item, tran.getEntityId());
          }
        }
      } else if (tran.isSales() || tran.isReceipt()) {
        if (tran.getAccountingEntityTypeId() != null && tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_SALES_SERVICES_INVOICE.getId().intValue()) {
          Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesServiceInvoiceView().replaceFirst("/scm", ""), tran.getEntityId(), tran.getEntityId());
        } else if (tran.getAccountingEntityTypeId() != null && tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_SALES_RETURN.getId().intValue()) {
          Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesReturnView().replaceFirst("/scm", ""), tran.getEntityId(), tran.getEntityId());
        } else {
          Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesInvoiceView().replaceFirst("/scm", ""), tran.getEntityId(), tran.getEntityId());
        }
      } else if (tran.isDebitNote()) {
        if (tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_EXPENSES.getId().intValue()) {
          Jsf.popupForm(FileConstant.EXPENSE, item, tran.getEntityId());
        } else if (tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_DEBIT_CREDIT_NOTE.getId().intValue()) {
          if (item.getAccountingTransactionDetailId().getAccountingLedgerId().isCustomer()) {
            Jsf.popupForm(FileConstant.CUSTOMER_DEBIT_NOTE, item, tran.getEntityId());
          } else if (item.getAccountingTransactionDetailId().getAccountingLedgerId().isVendor()) {
            Jsf.popupForm(FileConstant.SUPPLIER_DEBIT_NOTE, item, tran.getEntityId());
          }

        } else if (tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_VENDOR_CLAIM.getId().intValue()) {
          PopUpView.showVendorClaim(tran.getEntityId());
        } else if (tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_PURCHASE_RETURN.getId().intValue()) {
          Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getPurchaseReturnView().replaceFirst("/scm", ""), tran.getEntityId(), tran.getEntityId());
        }
      } else if (tran.isCreditNote()) {
        if (tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_EXPENSES.getId().intValue()) {
          Jsf.popupForm(FileConstant.EXPENSE, item, tran.getEntityId());
        } else if (tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_DEBIT_CREDIT_NOTE.getId().intValue()) {
          if (item.getAccountingTransactionDetailId().getAccountingLedgerId().isCustomer()) {
            Jsf.popupForm(FileConstant.CUSTOMER_CREDIT_NOTE, item, tran.getEntityId());
          } else if (item.getAccountingTransactionDetailId().getAccountingLedgerId().isVendor()) {
            Jsf.popupForm(FileConstant.SUPPLIER_CREDIT_NOTE, item, tran.getEntityId());
          }
        } else if (tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_SALES_RETURN.getId().intValue()) {
          Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesReturnView().replaceFirst("/scm", ""), tran.getEntityId(), tran.getEntityId());
        }
      } else if (tran.isExpenses()) {
        if (item.getAccountingTransactionDetailId().getAccountingLedgerId().isCustomer()) {
          Jsf.popupForm(FileConstant.PURCHASE_EXPENSE_REIMBURSE, item, tran.getEntityId());
        }
        if (tran.getAccountingEntityTypeId() != null && tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_SALES_EXPENSES.getId().intValue()) {
          Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesInvoiceView().replaceFirst("/scm", ""), tran.getEntityId(), tran.getEntityId());
        }
        if (tran.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_VENDOR_CLAIM.getId().intValue()) {
          PopUpView.showVendorClaim(tran.getEntityId());
        }
      } else if (tran.isOpeningEntry()) {
        if ((tran.getAccountingEntityTypeId().getId().intValue() == AccountingConstant.ACC_ENTITY_CUSTOMER.getId())
                || (tran.getAccountingEntityTypeId().getId().intValue() == AccountingConstant.ACC_ENTITY_VENDOR.getId())) {
          Jsf.popupForm(FileConstant.ACCOUNTING_LEDGER, item.getAccountingTransactionDetailId().getAccountingLedgerId(), item.getAccountingTransactionDetailId().getAccountingLedgerId().getId());
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
//     Jsf.popupForm(FileConstant.ACCOUNTING_LEDGER, accountingLedger, accountingLedger.getId());
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Double getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(Double paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public Double getReceivableAmount() {
    return receivableAmount;
  }

  public void setReceivableAmount(Double receivableAmount) {
    this.receivableAmount = receivableAmount;
  }

  public Double getBalanceAmount() {
    return balanceAmount;
  }

  public void setBalanceAmount(Double balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public boolean isIsAutoFill() {
    return isAutoFill;
  }

  public void setIsAutoFill(boolean isAutoFill) {
    this.isAutoFill = isAutoFill;
  }

  public String getHeaderValue() {
    return headerValue;
  }

  public void setHeaderValue(String headerValue) {
    this.headerValue = headerValue;
  }

  public String getHeaderPayable() {
    return headerPayable;
  }

  public void setHeaderPayable(String headerPayable) {
    this.headerPayable = headerPayable;
  }

  public String getHeaderReceivale() {
    return headerReceivale;
  }

  public void setHeaderReceivale(String headerReceivale) {
    this.headerReceivale = headerReceivale;
  }

  /**
   * AccountingTransactionDetailItem autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingTransactionDetailItemAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingTransactionDetailItemAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingTransactionDetailItem> accountingTransactionDetailItemAuto(String filter) {
    return ScmLookupView.accountingTransactionDetailItemAuto(filter);
  }

  /**
   * AccountingTransactionDetail autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingTransactionDetailAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingTransactionDetailAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingTransactionDetail> accountingTransactionDetailAuto(String filter) {
    return ScmLookupView.accountingTransactionDetailAuto(filter);
  }

  /**
   * AccountingDocType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingDocTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingDocTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  private transient boolean isBank = false;
  private transient boolean isCashAC = false;
  private List<AccountingDocType> acountingDocTypes;

  private AccountingDocType find(AccountingDocType dt) {
    if (acountingDocTypes != null) {
      for (AccountingDocType acountingDocType : acountingDocTypes) {
        if (acountingDocType.getId() == dt.getId().intValue()) {
          return acountingDocType;
        }
      }
    }
    return dt;
  }

  public void loadAccountingDocType() {
    acountingDocTypes = FinLookupView.accountingDocTypeByVoucher(accountingTransactionDetail.getVoucherTypeId().getId());
    AccountingTransactionDetail ot = getAccountingTransactionDetail().getOtherDetail();
    if (ot != null && ot.getAccountingLedgerId() != null) {
      isBank = false;
      isCashAC = false;
      if (ot.getAccountingLedgerId().getAccountingGroupId() != null) {
        if (AccountingConstant.GROUP_CASH_ACCOUNT.getId().intValue() == ot.getAccountingLedgerId().getAccountingGroupId().getId()) {
          isCashAC = true;
          accountingTransactionDetail.getDetailItem().get(0).setDocumentTypeId(find(AccountingConstant.DOC_TYPE_RECEIPT_CASH));
        }
        if (AccountingConstant.GROUP_BANK_ACCOUNT.getId().intValue() == ot.getAccountingLedgerId().getAccountingGroupId().getId() || AccountingConstant.GROUP_BANK_OD.getId().intValue() == ot.getAccountingLedgerId().getAccountingGroupId().getId()) {
          isBank = true;
          accountingTransactionDetail.getDetailItem().get(0).setDocumentTypeId(find(AccountingConstant.DOC_TYPE_RECEIPT_CHEQUE));
          //    chequeSelect = true;
//          item.setDocumentTypeId(AccountingConstant.DOC_TYPE_RECEIPT_CHEQUE);
          if (accountingTransactionDetail.getAccountingTransactionId().isPayment()) {
            acountingDocTypes.removeIf(item -> (AccountingConstant.DOC_TYPE_PAYMENT_CASH.getId().intValue() == item.getId() || AccountingConstant.DOC_TYPE_PAYMENT_BILL_ADJUSTMENT.getId().intValue() == item.getId())); //remvoing cash from bank
          } else if (accountingTransactionDetail.getAccountingTransactionId().isReceipt()) {
            acountingDocTypes.removeIf(item -> (AccountingConstant.DOC_TYPE_RECEIPT_CASH.getId().intValue() == item.getId() || AccountingConstant.DOC_TYPE_RECEIPT_BILL_ADJUSTMENT.getId().intValue() == item.getId())); //removing cash from bank
            chequeSelect = (!StringUtil.isEmpty(chequeEntryList) && (accountingTransactionDetail.getDetailItem().get(0).getDocumentTypeId() != null && accountingTransactionDetail.getDetailItem().get(0).getDocumentTypeId().isCheque()) && accountingTransactionDetail.getDetailItem().get(0).getId() == null);
          }
        } else if (AccountingConstant.GROUP_CASH_ACCOUNT.getId().intValue() == ot.getAccountingLedgerId().getAccountingGroupId().getId()) {
          if (accountingTransactionDetail.getAccountingTransactionId().isPayment()) {
            acountingDocTypes.removeIf(item -> (AccountingConstant.DOC_TYPE_PAYMENT_CASH.getId().intValue() != item.getId())); //removing everything else for cash
          } else if (accountingTransactionDetail.getAccountingTransactionId().isReceipt()) {
            acountingDocTypes.removeIf(item -> (AccountingConstant.DOC_TYPE_RECEIPT_CASH.getId().intValue() != item.getId())); //removing everything else for cash
            chequeSelect = (!StringUtil.isEmpty(chequeEntryList) && (accountingTransactionDetail.getDetailItem().get(0).getDocumentTypeId() != null && accountingTransactionDetail.getDetailItem().get(0).getDocumentTypeId().isCheque()) && accountingTransactionDetail.getDetailItem().get(0).getId() == null);

          }
        } else { //Journal
          if (accountingTransactionDetail.getAccountingTransactionId().isPayment()) {
            accountingTransactionDetail.getDetailItem().get(0).setDocumentTypeId(find(AccountingConstant.DOC_TYPE_JOURNAL_ADJUSTMENT));
            if (accountingTransactionDetail.getAccountingTransactionId().isJournalSettlement()) {
              acountingDocTypes.removeIf(item -> (AccountingConstant.DOC_TYPE_PAYMENT_BILL_ADJUSTMENT.getId().intValue() != item.getId())); //removing everything else for adjustment
            }
          } else if (accountingTransactionDetail.getAccountingTransactionId().isReceipt()) {
            accountingTransactionDetail.getDetailItem().get(0).setDocumentTypeId(find(AccountingConstant.DOC_TYPE_JOURNAL_ADJUSTMENT));
            if (accountingTransactionDetail.getAccountingTransactionId().isJournalSettlement()) {
              acountingDocTypes.removeIf(item -> (AccountingConstant.DOC_TYPE_RECEIPT_BILL_ADJUSTMENT.getId().intValue() != item.getId())); //removing everything else for adjustment
            }
          }
        }
      }
    }
  }

  public List<AccountingDocType> accountingDocType() {
    return acountingDocTypes;
  }

  public AccountingTransactionDetail getAccountingTransactionDetail() {
    return accountingTransactionDetail;
  }

  public void setAccountingTransactionDetail(AccountingTransactionDetail accountingTransactionDetail) {
    this.accountingTransactionDetail = accountingTransactionDetail;
  }

  private transient boolean chequeSelect = false;

  public boolean isChequeSelect() {
    return chequeSelect;
  }

  private String getPayNarration(AccountingTransactionDetailItem item) {
    if (item.getDocumentTypeId() != null) {
      String pay = "" + item.getDocumentTypeId().getTitle();
      if (!StringUtil.isEmpty(item.getDocumentNumber())) {
        pay += " [" + item.getDocumentNumber();
        if (!StringUtil.isEmpty(item.getReferNumber())) {
          pay += ", " + item.getReferNumber();
        }
        pay += "]";
      }
      return pay;
    }
    return "";
  }

  public boolean showSettlement() {
    return (getAccountingTransactionDetail().getAccountingLedgerId().isDebtorsOrCreditors() && (getAccountingTransactionDetail().getAccountingTransactionId().isPayment() || getAccountingTransactionDetail().getAccountingTransactionId().isReceipt() || getAccountingTransactionDetail().getAccountingTransactionId().isJournalSettlement()));
  }

  public boolean isIsBank() {
    return isBank;
  }

  public void setIsBank(boolean isBank) {
    this.isBank = isBank;
  }

  public boolean isIsCashAC() {
    return isCashAC;
  }

  public void setIsCashAC(boolean isCashAC) {
    this.isCashAC = isCashAC;
  }

  public Double getAdjTotal() {
    return adjTotal;
  }

  public void setAdjTotal(Double adjTotal) {
    this.adjTotal = adjTotal;
  }

  public boolean hasSelfAdjust() {
    return (getAccountingTransactionDetail().getAccountingTransactionDetailItemSelected() != null && getAccountingTransactionDetail().getAccountingTransactionDetailItemSelected().length > 0 && (getAccountingTransactionDetail().getDetailItem() == null || getAccountingTransactionDetail().getDetailItem().get(0).getNetAmount() == null || getAccountingTransactionDetail().getDetailItem().get(0).getNetAmount() < 1));
  }

  private void settlementChequeEntryItem(MainView main, Integer chequeEntryId) {
    accountingTransactionDetail.setAccountingTransactionDetailItemSelected(null);
    if (chequeEntryId != null) {
      paymentAmount = accountingTransactionDetail.getDetailItem().get(0).getNetAmount();
      receivableAmount = 0.0;
      totalAmount = 0.0;

      List<AccountingTransactionDetailItem> selectedList = new ArrayList<>();
      List<AccountingTransactionDetailItem> payableList = ChequeEntryService.loadTransactionDetailItemByChequeEntry(main, chequeEntryId, AccountingConstant.RECORD_TYPE_PAYABLE);
      List<AccountingTransactionDetailItem> receivableList = ChequeEntryService.loadTransactionDetailItemByChequeEntry(main, chequeEntryId, AccountingConstant.RECORD_TYPE_RECEIVABLE);
      if (accountingTransactionDetail != null) {
        for (AccountingTransactionDetailItem pdt : payableList) {
//          if (balanceAmount <= 0) {
//            break;
//          }
          for (AccountingTransactionDetailItem dt : accountingTransactionDetail.getDetailItemReceivable()) {
            if (pdt.getId().intValue() == dt.getId()) {
              dt.setSettlement(true);
              receivableAmount += dt.getBalanceAmount();
              selectedList.add(dt);
              //   setAdjustmentBal(dt, true);
              //   selectedList.add(dt)
              break;
            }
          }
        }
        balanceAmount = paymentAmount + receivableAmount;
        totalAmount = balanceAmount;
        accountingTransactionDetail.setAccountingTransactionDetailItemSelected(selectedList.toArray(new AccountingTransactionDetailItem[selectedList.size()]));
        resetAdjustment();
        for (AccountingTransactionDetailItem rdt : receivableList) {
//          if (balanceAmount == 0) {
//            break;
//          }
          for (AccountingTransactionDetailItem dt : accountingTransactionDetail.getDetailItemPayable()) {
            if (rdt.getId().intValue() == dt.getId()) {
              dt.setSettlement(true);
              setAdjustmentBal(dt, true);
              break;
            }
          }
        }
      }
    }
  }
}
