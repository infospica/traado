/*
 * @(#)ChequeEntryView.java	1.0 Mon Sep 11 12:32:41 IST 2017
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetailItem;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.ChequeEntry;
import spica.fin.service.AccountingLedgerTransactionService;
import spica.fin.service.ChequeEntryService;
import spica.scm.common.ChequeEntryStatus;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Bank;
import spica.scm.domain.SalesAccount;
import spica.scm.service.SalesAccountService;
import spica.scm.util.AppUtil;
import spica.scm.view.ScmLookupExtView;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.entity.util.DateUtil;

/**
 * ChequeEntryView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Sep 11 12:32:41 IST 2017
 */
@Named(value = "chequeEntryView")
@ViewScoped
public class ChequeEntryView implements Serializable {

  private transient ChequeEntry chequeEntry;	//Domain object/selected Domain.
  private transient LazyDataModel<ChequeEntry> chequeEntryLazyModel; 	//For lazy loading datatable.
//  private transient LazyDataModel<ChequeEntry> chequeStatusEntryLazyModel;
  private transient ChequeEntry[] chequeEntrySelected;	 //Selected Domain Array
  private transient Date today = new Date();
  private transient Date chequeDateMin = new Date();
  private ChequeEntryStatus[] selectedStatus;
  private List<ChequeEntryStatus> statusName;
  private List<Integer> chequeStatus;
  private List<AccountGroup> accountGroupList;
  private boolean accountCombo = false;
  private transient List<AccountingTransactionDetailItem> transactionDetailItemSelectedPayable;
  private transient List<AccountingTransactionDetailItem> transactionDetailItemSelectedReceivable;
  private AccountGroup accountGroup;
  private String chequeReviewType = null;
  private ChequeEntry selectedChequeEntry;
  private int type;
  private String narration;
  private Integer inOrOut;

  @Inject
  private AccountingMainView accountingMainView;

  /**
   * Default Constructor.
   */
  public ChequeEntryView() {
    super();
//    Calendar cal = Calendar.getInstance();
//    cal.setTime(today);
//    cal.add(Calendar.DAY_OF_MONTH, getChequePresentDays()); //Allow only 90 days prior from current day   
//    chequeDateMin = cal.getTime();
    chequeDateMin = DateUtil.moveDays(today, getChequePresentDays()); //Allow only 90 days prior from current day   
  }

  @PostConstruct
  public void init() {
    setInOrOut(null);
    if (accountingMainView != null && accountingMainView.getChequeReviewType() != null) {
      chequeReviewType = accountingMainView.getChequeReviewType();
      inOrOut = accountingMainView.getChequeInOrOut();
      accountingMainView.setChequeReviewType(null);
    }
    Integer id = (Integer) Jsf.popupParentValue(Integer.class);
    if (id != null) {
      getChequeEntry().setId(id);
    }
  }

  public int getChequePresentDays() {
    return (int) AccountingConstant.CHEQUE_PRESENT_DAYS * -1;
  }

  public Date getChequeDateMin() {
    return chequeDateMin;
  }

  /**
   * Return ChequeEntry.
   *
   * @return ChequeEntry.
   */
  public ChequeEntry getChequeEntry() {
    if (chequeEntry == null) {
      chequeEntry = new ChequeEntry();
    }
    if (chequeEntry.getCompanyId() == null) {
      chequeEntry.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return chequeEntry;
  }

  /**
   * Set ChequeEntry.
   *
   * @param chequeEntry.
   */
  public void setChequeEntry(ChequeEntry chequeEntry) {
    this.chequeEntry = chequeEntry;
  }

  /**
   * Return LazyDataModel of ChequeEntry.
   *
   * @return
   */
  public LazyDataModel<ChequeEntry> getChequeEntryLazyModel() {
    return chequeEntryLazyModel;
  }

  /**
   * Return ChequeEntry[].
   *
   * @return
   */
  public ChequeEntry[] getChequeEntrySelected() {
    return chequeEntrySelected;
  }

  /**
   * Set ChequeEntry[].
   *
   * @param chequeEntrySelected
   */
  public void setChequeEntrySelected(ChequeEntry[] chequeEntrySelected) {
    this.chequeEntrySelected = chequeEntrySelected;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchChequeEntry(MainView main, String viewType, Integer inOrOutValue) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getChequeEntry().reset();
          getChequeEntry().setAccountGroup(null);
          setInOrOut(inOrOutValue);
          getChequeEntry().setEntryDate(SystemRuntimeConfig.getMaxEntryDate(getChequeEntry().getCompanyId()));
          getChequeEntry().setStatusId(AccountingConstant.BANK_CHEQUE_RECIEVED);
          getChequeEntry().setInOrOut(inOrOut);
        } else if (main.isEdit() && !main.hasError()) {
          setChequeEntry((ChequeEntry) ChequeEntryService.selectByPk(main, getChequeEntry()));
          chequePresentColor(getChequeEntry());
          loadTransactionDetailItem(main);
        } else if (main.isList()) {
          if (accountingMainView == null || (accountingMainView != null && accountingMainView.getChequeInOrOut() == null)) {
            setInOrOut(inOrOutValue);
          }
          getChequeEntry().setCompanyId(null);
          accountingMainView.init(main, getChequeEntry().getCompanyId());
          if (!StringUtil.isEmpty(chequeReviewType)) {
            chequeStatus = new ArrayList<>();
            chequeStatus.add(AccountingConstant.BANK_CHEQUE_RECIEVED);
            chequeStatus.add(AccountingConstant.BANK_CHEQUE_BOUNCE_RESUBMITTED);

          }
          if (StringUtil.isEmpty(chequeReviewType)) {
            chequeStatus = new ArrayList<>();
            chequeStatus.add(AccountingConstant.BANK_CHEQUE_BOUNCE);
            chequeStatus.add(AccountingConstant.BANK_CHEQUE_CANCELED);
            chequeStatus.add(AccountingConstant.BANK_CHEQUE_RECIEVED);
            chequeStatus.add(AccountingConstant.BANK_CHEQUE_RETURNED);
          }
          if (chequeStatus.contains(4)) {
            chequeStatus.add(AccountingConstant.BANK_CHEQUE_BOUNCE_HIDE);
          }
          loadChequeEntryList(main);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create chequeEntryLazyModel.
   *
   * @param main
   */
  private void loadChequeEntryList(final MainView main) {
    if (chequeEntryLazyModel == null) {
      chequeEntryLazyModel = new LazyDataModel<ChequeEntry>() {
        private List<ChequeEntry> list;

        @Override
        public List<ChequeEntry> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ChequeEntryService.listPagedByCompany(main, getChequeEntry().getCompanyId(), getAccountGroup(), inOrOut, getChequeStatus(), getChequeReviewType());
            for (ChequeEntry obj : list) {
              chequePresentColor(obj);
            }
            main.commit(chequeEntryLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ChequeEntry chequeEntry) {
          return chequeEntry.getId();
        }

        @Override
        public ChequeEntry getRowData(String rowKey) {
          if (list != null) {
            for (ChequeEntry obj : list) {
              if (rowKey.equals(obj.getId().toString())) {
                return obj;
              }
            }
          }
          return null;
        }
      };
    }
  }

  private void uploadFiles() {
    String SUB_FOLDER = "fin_cheque_entry/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveChequeEntry(MainView main) {
    return saveOrCloneChequeEntry(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneChequeEntry(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneChequeEntry(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneChequeEntry(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ChequeEntryService.insertOrUpdate(main, getChequeEntry());
            if (!StringUtil.isEmpty(getTransactionDetailItemSelectedPayable()) || !StringUtil.isEmpty(getTransactionDetailItemSelectedReceivable())) {
              ChequeEntryService.insertTransactionDetailItem(main, getChequeEntry(), getTransactionDetailItemSelectedPayable(), getTransactionDetailItemSelectedReceivable());
            }
            break;
          case "clone":
            getChequeEntry().setStatusId(AccountingConstant.BANK_CHEQUE_RECIEVED);
            getChequeEntry().setParentId(null);
            ChequeEntryService.clone(main, getChequeEntry());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many ChequeEntry.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteChequeEntry(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(chequeEntrySelected)) {
        ChequeEntryService.deleteByPkArray(main, getChequeEntrySelected()); //many record delete from list
        main.commit("success.delete");
        chequeEntrySelected = null;
      } else {
        ChequeEntryService.deleteByPk(main, getChequeEntry());  //individual record delete from list or edit form
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

  private transient String chequePresent;
  private transient String chequePresentColor;

  public void chequePresentColor(ChequeEntry ce) {
    if (ce.getChequeDate() == null) {
      chequePresent = "Undated Cheque";
      chequePresentColor = "magenta";
    } else {
      long days = AppUtil.daysBetween(new Date(), ce.getChequeDate());

      if (days >= 0) {
        if (days == 0) {
          chequePresent = "Due today";
          chequePresentColor = "green";
        } else {
          chequePresent = "Due in " + days + " days";
          chequePresentColor = "#1f3d55";
        }

      } else {
        days = days * -1;
        if (days > AccountingConstant.CHEQUE_PRESENT_DAYS) {
          chequePresent = "Expired (" + (days - AccountingConstant.CHEQUE_PRESENT_DAYS) + ")";
          chequePresentColor = "black";
        } else {
          chequePresentColor = (days > (AccountingConstant.CHEQUE_PRESENT_DAYS - 10)) ? "red" : "green";
          days = AccountingConstant.CHEQUE_PRESENT_DAYS - days;
          if (days == 0) {
            chequePresent = "Valid for today";
          } else {
            chequePresent = "Valid for " + days + " days";
          }

        }
      }
    }
    ce.setChequePresent(chequePresent);
    ce.setChequePresentColor(chequePresentColor);
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<Bank> bankAuto(String filter) {
    return ScmLookupExtView.lookupBankByCountry(filter, getChequeEntry().getCompanyId());
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> accountingLedgerSalesAuto(String filter) {
    return FinLookupView.ledgerDebtorsAndCreditorsAuto(filter, getChequeEntry().getCompanyId().getId());
  }

  public void returnToParty(MainView main, ChequeEntry ce) {
    try {
      ce.setStatusId(AccountingConstant.BANK_CHEQUE_RETURNED);
      ChequeEntryService.insertOrUpdate(main, ce);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void cancel(MainView main, ChequeEntry ce) {
    try {
      ce.setStatusId(AccountingConstant.BANK_CHEQUE_CANCELED);
      ChequeEntryService.insertOrUpdate(main, ce);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void resubmit(MainView main, ChequeEntry ce) {
    try {
      ce.setStatusId(AccountingConstant.BANK_CHEQUE_BOUNCE_HIDE);
      ChequeEntry chequeEntry = new ChequeEntry(ce);
      chequeEntry.setNote("Represent");
      ChequeEntryService.insertOrUpdate(main, ce);
      ChequeEntryService.insertOrUpdate(main, chequeEntry);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public String getChequePresent() {
    return chequePresent;
  }

  public String getChequePresentColor(ChequeEntry ce) {
    chequePresentColor(ce);
    return chequePresentColor;
  }

  public ChequeEntryStatus[] getSelectedStatus() {
    return selectedStatus;
  }

  public void setSelectedStatus(ChequeEntryStatus[] selectedStatus) {
    this.selectedStatus = selectedStatus;
  }

  public List<ChequeEntryStatus> getStatusName() {
    if (statusName == null) {
      statusName = new ArrayList<>();
      statusName.add(new ChequeEntryStatus(AccountingConstant.BANK_CHEQUE_RECIEVED, "Received"));
      statusName.add(new ChequeEntryStatus(AccountingConstant.BANK_CHEQUE_ISSUED, "Deposited"));
      statusName.add(new ChequeEntryStatus(AccountingConstant.BANK_CHEQUE_RECONCILED, "Cleared"));
      statusName.add(new ChequeEntryStatus(AccountingConstant.BANK_CHEQUE_BOUNCE, "Dishonoured"));
      statusName.add(new ChequeEntryStatus(AccountingConstant.BANK_CHEQUE_BOUNCE_RESUBMITTED, "Represent"));
      statusName.add(new ChequeEntryStatus(AccountingConstant.BANK_CHEQUE_RETURNED, "Returned"));
      statusName.add(new ChequeEntryStatus(AccountingConstant.BANK_CHEQUE_CANCELED, "Cancelled"));
    }
    return statusName;
  }

  public List<Integer> getChequeStatus() {
    return chequeStatus;
  }

  public void setChequeStatus(List<Integer> chequeStatus) {
    this.chequeStatus = chequeStatus;
  }

  public void accountGroupSelect(SelectEvent event) {
    accountingMainView.setSelectedAccountGroup((AccountGroup) event.getObject());
  }

  public void partySelectevent(SelectEvent event) {
    AccountingLedger le = (AccountingLedger) event.getObject();
    getChequeEntry().setAccountGroup(null);
    accountCombo = false;
    if (le.isCustomer()) {
      accountGroupList = ScmLookupExtView.accountGroupByCustomerId(le.getEntityId());
    } else if (le.isVendor()) {
      accountGroupList = ScmLookupExtView.accountGroupByVendor(le.getEntityId());
    }
    if (accountGroupList.size() == 1) {
      getChequeEntry().setAccountGroup(accountGroupList.get(0));
    } else if (accountGroupList.size() > 1) {
      accountCombo = true;
    }
    accountingMainView.setSelectedAccountGroup(getChequeEntry().getAccountGroup());
  }

  public boolean isAccountCombo() {
    return accountCombo;
  }

  public List<AccountingTransactionDetailItem> getTransactionDetailItemSelectedPayable() {
    return transactionDetailItemSelectedPayable;
  }

  public void setTransactionDetailItemSelectedPayable(List<AccountingTransactionDetailItem> transactionDetailItemSelectedPayable) {
    this.transactionDetailItemSelectedPayable = transactionDetailItemSelectedPayable;
  }

  public List<AccountingTransactionDetailItem> getTransactionDetailItemSelectedReceivable() {
    return transactionDetailItemSelectedReceivable;
  }

  public void setTransactionDetailItemSelectedReceivable(List<AccountingTransactionDetailItem> transactionDetailItemSelectedReceivable) {
    this.transactionDetailItemSelectedReceivable = transactionDetailItemSelectedReceivable;
  }

  private void loadTransactionDetailItem(MainView main) {
    if (getChequeEntry() != null && getChequeEntry().getId() != null) {
      transactionDetailItemSelectedPayable = ChequeEntryService.loadTransactionDetailItemByChequeEntry(main, getChequeEntry().getId(), AccountingConstant.RECORD_TYPE_PAYABLE);
      transactionDetailItemSelectedReceivable = ChequeEntryService.loadTransactionDetailItemByChequeEntry(main, getChequeEntry().getId(), AccountingConstant.RECORD_TYPE_RECEIVABLE);
      accountingMainView.setSelectedAccountGroup(getChequeEntry().getAccountGroup());
    }
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public AccountingMainView getAccountingMainView() {
    return accountingMainView;
  }

  public void setChequeViewType() {

  }

  public void setChequeStatus() {

  }

  public String getChequeReviewType() {
    return chequeReviewType;
  }

  public void setChequeReviewType(String chequeReviewType) {
    this.chequeReviewType = chequeReviewType;
  }

  public ChequeEntry getSelectedChequeEntry() {
    return selectedChequeEntry;
  }

  public int getType() {
    return type;
  }

  public void typeChequeEntry(ChequeEntry cvar, int type) {
    selectedChequeEntry = cvar;
    this.type = type;
  }

  public void submitOrReturnChequEntry(MainView main) {
    if (!StringUtil.isEmpty(narration)) {
      getSelectedChequeEntry().setNote(selectedChequeEntry.getNote() + "," + narration);
    }
    if (getType() == 2 && selectedChequeEntry != null) {
      resubmit(main, selectedChequeEntry);
    } else if (getType() == 1 && selectedChequeEntry != null) {
      returnToParty(main, selectedChequeEntry);
    }
    Jsf.update("f1");
    Jsf.closeDialog("confirmDialogWidget");
  }

  public String getNarration() {
    return narration;
  }

  public void setNarration(String narration) {
    this.narration = narration;
  }

  public void journalPopup(ChequeEntry ce) {
    Jsf.popupForm(AccountingLedgerTransactionService.JOURNAL_POPUP, ce);
  }

  public void ledgerPopup(AccountingLedger accountingLedger) {
    Jsf.popupForm(AccountingLedgerTransactionService.LEDGER_TRANSACTION_POPUP, accountingLedger);
  }

  public List<AccountGroup> getAccountGroupList() {
    return accountGroupList;
  }

  public void setAccountGroupList(List<AccountGroup> accountGroupList) {
    this.accountGroupList = accountGroupList;
  }

  public Integer getInOrOut() {
    return inOrOut;
  }

  public void setInOrOut(Integer inOrOut) {
    this.inOrOut = inOrOut;
  }

}
