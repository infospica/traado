/*
 * @(#)AccountingLedgerView.java	1.0 Fri Feb 24 15:58:33 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingGroup;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingLedgerBalance;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.service.AccountingLedgerTransactionService;
import spica.sys.UserRuntimeView;
import spica.fin.domain.ChequeEntry;
import spica.fin.service.AccountingLedgerService;
import static spica.fin.service.BankReconciliationService.selectSumOfDebitCreditReconcilation;
import spica.fin.service.ChequeEntryService;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.util.AppUtil;
import spica.scm.view.ScmLookupExtView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.AppView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * AccountingLedgerView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:33 IST 2017
 */
@Named(value = "ledgerTransactionView")
@ViewScoped
public class AccountingLedgerTransactionView implements Serializable {

  private transient LazyDataModel<AccountingTransactionDetail> accountingTransactionDetailLazyModel; 	//For lazy loading datatable.
  // private transient Part referenceDocumentCopyPathPart;
  private transient AccountingLedger accountingLedger;
  private transient Double debitTotal;
  private transient Double creditTotal;
  private transient Integer ledgerId;
  private Double closingBalanceCredit;
  private Double closingBalanceDebit;
  private transient List<ChequeEntry> relatedChequeEntryList;
  private transient List<AccountingLedgerBalance> ledgerbalanceList;

  private transient List<AccountingGroup> accountingGroupList;
  private transient List<AccountingLedger> relatedAccountingLedger;
  private transient String ledgerAddress;
  private Integer chequeEntryId;

  private transient Double debit;
  private transient Double credit;
  private transient Double processedAmountCredit;
  private transient Double processedAmountDebit;
  @Inject
  private AccountingMainView accountingMainView;

  /**
   * Default Constructor.
   */
  public AccountingLedgerTransactionView() {
    super();
  }

  @PostConstruct
  public void init() {
    accountingLedger = (AccountingLedger) Jsf.popupParentValue(AccountingLedger.class);
    if (accountingLedger != null) {
      try (MainView main = Jsf.getMain()) {
        accountingLedger = AccountingLedgerService.selectByPk(main, accountingLedger);
      }
    }
    accountingMainView.setSelectedAccountGroup(accountingLedger == null ? null : (accountingLedger.getAccountGroup() == null ? null : accountingLedger.getAccountGroup()));
  }

  public List<AccountingLedgerBalance> getLedgerbalanceList(MainView main) {
    if (accountingLedger != null && ledgerbalanceList == null) {
      try {
        crLineTotal = 0.00;
        drLineTotal = 0.00;
        relatedLedgerList = null;
        debitTotal = 0.00;
        creditTotal = 0.00;
        accountingLedger = AccountingLedgerService.selectByPk(main, accountingLedger);
        ledgerbalanceList = AccountingLedgerTransactionService.getLedgerbalanceList(main, getAccountingLedger(), accountingMainView);
        //crLineTotal = accountingLedger.getBalanceCredit() == null ? 0.0 : accountingLedger.getBalanceCredit();
        //drLineTotal = accountingLedger.getBalanceDebit() == null ? 0.0 : accountingLedger.getBalanceDebit();

        long now = new Date().getTime();
        for (AccountingLedgerBalance ab : ledgerbalanceList) {
          if (ab.getDebit() != null) {
            debitTotal += ab.getDebit();
          }
          if (ab.getCredit() != null) {
            creditTotal += ab.getCredit();
          }
          getBalance(ab);
          OutstandingUtil.setDueDays(now, ab);
        }
        closingBalanceCredit = (creditTotal == null ? 0 : creditTotal);// + (accountingLedger.getBalanceCredit() == null ? 0 : accountingLedger.getBalanceCredit());
        closingBalanceDebit = (debitTotal == null ? 0 : debitTotal);// + (accountingLedger.getBalanceDebit() == null ? 0 : accountingLedger.getBalanceDebit());

        if (accountingLedger.isBankAc()) {
          debit = selectSumOfDebitCreditReconcilation(main, accountingLedger.getCompanyId().getId(), accountingMainView, accountingLedger.getId(), AccountingConstant.IS_DEBIT);
          credit = selectSumOfDebitCreditReconcilation(main, accountingLedger.getCompanyId().getId(), accountingMainView, accountingLedger.getId(), AccountingConstant.IS_CREDIT);
          if (closingBalanceCredit > 0) {
            processedAmountCredit = (closingBalanceCredit - debit);
          }
          if (closingBalanceDebit > 0) {
            processedAmountDebit = (closingBalanceDebit - credit);
          }
        }

      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return ledgerbalanceList;
  }

  public void setLedgerbalanceList(List<AccountingLedgerBalance> ledgerbalanceList) {
    this.ledgerbalanceList = ledgerbalanceList;
  }

  public void detailAction(MainView main) {
    try {
      if (accountingLedger != null && accountingLedger.getId() != null) {
        main.setViewType(ViewTypes.editform);
        reset();
        accountingMainView.setSelectedAccountGroup(null);
        accountingMainView.setSelectedLedger(null);
      }
    } finally {
      main.close();
    }
  }

  public void ledgerValueChanged(ValueChangeEvent event) {
    reset();
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountingTransactionDetail(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        ledgerAddress = null;
        main.setViewType(viewType);

        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          accountingMainView.setSelectedLedger(null);
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          if (ledgerId != null) {
            accountingMainView.setSelectedAccountGroup(null);
            accountingMainView.setSelectedLedger(null);
            setAccountingLedger(AccountingLedgerTransactionService.getLedgerById(main, ledgerId));
            ledgerId = null;
          }
        } else if (main.isList()) {
          relatedChequeEntryList = null;
          accountingMainView.init(main, UserRuntimeView.instance().getCompany());
          accountingMainView.setSelectedLedger(null);
          accountingMainView.setSelectedAccountingGroup(null);
          accountingMainView.setSelectedAccountGroup(null);
          accountingMainView.setExportLedger(null);
          accountingLedger = null;
          reset();
          loadAccountingTransactionDetailList(main);
          main.clear();
          // calculateTotal(main);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void loadAccountingTransactionDetailList(final MainView main) {
    ledgerId = null;
    if (accountingTransactionDetailLazyModel == null) {
      accountingTransactionDetailLazyModel = new LazyDataModel<AccountingTransactionDetail>() {
        private List<AccountingTransactionDetail> list;

        @Override
        public List<AccountingTransactionDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            main.clear();
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountingLedgerTransactionService.listPaged(main, UserRuntimeView.instance().getCompany(), accountingMainView);
            main.commit(accountingTransactionDetailLazyModel, first, pageSize);
            List<AccountingTransactionDetail> sumList = AccountingLedgerTransactionService.getSum(main, UserRuntimeView.instance().getCompany(), accountingMainView);
            for (AccountingTransactionDetail atd : sumList) {
              debitTotal = atd.getDebit();
              creditTotal = atd.getCredit();
            }
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountingTransactionDetail accountingTransactionDetail) {
          return accountingTransactionDetail.getId();
        }

        @Override
        public AccountingTransactionDetail getRowData(String rowKey) {
          if (list != null) {
            for (AccountingTransactionDetail obj : list) {
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

  public void reset() {
    MainView main = Jsf.getMain();
    main.getPageData().reset();
    main.getPageData().setTotalRecords(null);
    main.getPageData().setSearchKeyWord(null);
    if (main.isList()) {
      //accountingTransactionDetailLazyModel = null;
      //loadAccountingTransactionDetailList(main);
      // calculateTotal(main);
    } else {
      ledgerbalanceList = null;
      relatedChequeEntryList = null;
      balance = null;
    }
  }

  public void backOrNext(int pos, Company company, boolean from) {
    if (accountingMainView.backOrNext(pos, company.getCurrentFinancialYear(), from)) {
      reset();
    }
  }

  public void onFromDateSelect(SelectEvent event) {
    accountingMainView.setFromDate((Date) event.getObject());
    reset();
  }

  public void onToDateSelect(SelectEvent event) {
    accountingMainView.setToDate((Date) event.getObject());
    reset();
  }

  public void groupFilter(SelectEvent event) {
    accountingMainView.setSelectedAccountingGroup((AccountingGroup) event.getObject());
    reset();
  }

  public void accountGroupFilter(SelectEvent event) {
    accountingMainView.setSelectedAccountGroup((AccountGroup) event.getObject());
    reset();
  }

  public void accountGroupOutstandingFilter(SelectEvent event) {
    accountingMainView.setSelectedAccountGroup((AccountGroup) event.getObject());
    MainView main = Jsf.getMain();
    try {
      actionOutStanding(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void ledgerFilter(SelectEvent event) {
    accountingMainView.setSelectedLedger((AccountingLedger) event.getObject());
    reset();
  }

  private transient Double crLineTotal = 0.00;
  private transient Double drLineTotal = 0.00;

  public Double getCrLineTotal(Double amt) {
    if (amt != null) {
      crLineTotal += amt;
    }
    return crLineTotal;
  }

  public void setCrLineTotal(Double crLineTotal) {
    this.crLineTotal = crLineTotal;
  }

  private void getBalance(AccountingLedgerBalance lb) {
    if ("dr".equals(lb.getTrtype())) {
      if (lb.getDebit() != null) {
        drLineTotal += lb.getDebit();
      }
    } else {
      if (lb.getCredit() != null) {
        crLineTotal += lb.getCredit();
      }
    }

    if (crLineTotal >= drLineTotal) {
      lb.setColor(AccountingConstant.COLOR_CR);
      lb.setBalanceLine("-" + AppView.formatDecimal(crLineTotal - drLineTotal, 2));
    } else if (drLineTotal >= crLineTotal) {
      lb.setColor(AccountingConstant.COLOR_DR);
      lb.setBalanceLine(AppView.formatDecimal(drLineTotal - crLineTotal, 2));
    }
  }

  /**
   *
   * @param main
   * @return
   */
  //FIXME ARUN why this is required
  public String journalDetails(MainView main, AccountingLedgerBalance accountingLedgerBalance) {
    Jsf.popupForm(AccountingLedgerTransactionService.JOURNAL_POPUP, accountingLedgerBalance, accountingLedgerBalance.getId()); //1 to set the labels to edit
    return null;
  }

  /**
   * AccountingGroup autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingGroupAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingGroupAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingGroup> accountingGroupAuto(String filter) {
    return FinLookupView.accountingGroupAuto(filter, UserRuntimeView.instance().getCompany().getId());
  }

  public List<AccountGroup> lookupGroupAuto() {
    if (getAccountingLedger().isCustomer()) {
      return ScmLookupExtView.accountGroupByCustomerId(getAccountingLedger().getEntityId());
    } else if (getAccountingLedger().isVendor()) {
      return ScmLookupExtView.accountGroupByVendor(getAccountingLedger().getEntityId());
    }
    return null;
    // return FinLookupView.accountGroupAuto(getAccountingLedger());
  }

  private transient List<AccountingLedger> relatedLedgerList;

  public List<AccountingLedger> getRelatedLedgerList(MainView main) {
    if (relatedLedgerList == null) {
      try {
        relatedLedgerList = AccountingLedgerTransactionService.listRelatedLedger(main, accountingLedger, accountingMainView);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return relatedLedgerList;
  }

  /**
   * Return LazyDataModel of AccountingTransactionDetail.
   *
   * @return
   */
  public LazyDataModel<AccountingTransactionDetail> getAccountingTransactionDetailLazyModel() {
    return accountingTransactionDetailLazyModel;
  }

  public AccountingLedger getAccountingLedger() {
    return accountingLedger;
  }

  public void setAccountingLedger(AccountingLedger accountingLedger) {
    if (accountingLedger != null) {
      accountingMainView.setExportLedger(accountingLedger);
    }
    this.accountingLedger = accountingLedger;
  }

  public Double getDebitTotal() {
    return debitTotal;
  }

  public void setDebitTotal(Double debitTotal) {
    this.debitTotal = debitTotal;
  }

  public Double getCreditTotal() {
    return creditTotal;
  }

  public void setCreditTotal(Double creditTotal) {
    this.creditTotal = creditTotal;
  }

  public void bankBalance() {
    Jsf.popupForm(AccountingLedgerTransactionService.BANK_RECONCILIATION_POPUP, getAccountingLedger());
  }

  public void openBankChargesPopup() {
    Jsf.popupForm(AccountingLedgerTransactionService.BANK_CHARGES_POPUP, getAccountingLedger());
  }

  public Integer getLedgerId() {
    return ledgerId;
  }

  public void setLedgerId(Integer ledgerId) {
    this.ledgerId = ledgerId;
  }

  public Double getClosingBalanceCredit() {
    return closingBalanceCredit;
  }

  public void setClosingBalanceCredit(Double closingBalanceCredit) {
    this.closingBalanceCredit = closingBalanceCredit;
  }

  public Double getClosingBalanceDebit() {
    return closingBalanceDebit;
  }

  public void setClosingBalanceDebit(Double closingBalanceDebit) {
    this.closingBalanceDebit = closingBalanceDebit;
  }

  public void loadRelatedChequeEntryList(MainView main) {
    if (getAccountingLedger() != null && getAccountingLedger().isDebtorsOrCreditors()) {
      if (StringUtil.isEmpty(relatedChequeEntryList)) {
        try {
          relatedChequeEntryList = ChequeEntryService.listAllByCustomer(main, UserRuntimeView.instance().getCompany(), getAccountingLedger().getId(), getAccountingLedger().getEntityId(), accountingMainView);
          balance = null;
          getBalanceTotal();
        } catch (Throwable t) {
          main.rollback(t, "error.select");
        } finally {
          main.close();
        }
      }
    }
    if (StringUtil.isEmpty(relatedChequeEntryList)) {
      relatedChequeEntryList = Collections.EMPTY_LIST;
    }
  }

  public List<ChequeEntry> getRelatedChequeEntryList() {
    return relatedChequeEntryList;
  }

  public Long daysRemaining(ChequeEntry ce) {
    return AppUtil.daysBetween(new Date(), ce.getChequeDate());
  }

  private boolean isCreditMore = false;
  private Double balance = 0.00;

  public Double getBalanceTotal() {
    if (balance == null) {
      balance = 0.00;

      Double chequeTotal = 0.00;
      for (ChequeEntry chequeEntry : relatedChequeEntryList) {
        if (chequeEntry.getAmount() != null) {
          chequeTotal += chequeEntry.getAmount();
        }
      }
      balance = getClosingBalanceDebit() - getClosingBalanceCredit();
      balance -= chequeTotal;
      isCreditMore = balance > 0;
    }
    return balance;
  }

  public boolean getIsCreditMore() {
    return isCreditMore;
  }

  public void setRelatedChequeEntryList(List<ChequeEntry> relatedChequeEntryList) {
    this.relatedChequeEntryList = relatedChequeEntryList;
  }

  /////////To show outstanding 
  private transient AccountingTransactionDetail transactionDetail;

  public void actionOutStanding(MainView main) {
    try {
      resetDetail();
      OutstandingUtil.addPayment(main, transactionDetail, accountingMainView, chequeEntryId);
      sum();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void chequeEntryInOutStanding(MainView main, AccountingLedger accountingLedger, Integer chequeEntryId) {
    try {
      if (accountingLedger != null) {
        this.accountingLedger = accountingLedger;
        this.chequeEntryId = chequeEntryId;
        if (chequeEntryId == null) {
          this.chequeEntryId = 0; //setting as 0 to trigger the not in condition
        }
        resetDetail();
        OutstandingUtil.addPayment(main, transactionDetail, accountingMainView, this.chequeEntryId);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void resetDetail() {
    if (transactionDetail == null) {
      transactionDetail = new AccountingTransactionDetail();
    }
    transactionDetail.reset();
    transactionDetail.setAccountingLedgerId(getAccountingLedger());
  }

  private void sum() {
    debitTotal = transactionDetail.getDetailItemPayable().stream().mapToDouble(item -> item.getBalanceAmount()).sum();
    creditTotal = transactionDetail.getDetailItemReceivable().stream().mapToDouble(item -> item.getBalanceAmount()).sum();
  }

  public boolean isChequeEntry() {
    return (chequeEntryId != null);
  }

  //////////////////////////////////
  public AccountingTransactionDetail getTransactionDetail() {
    return transactionDetail;
  }

  public Double getRemainingPayable() {
    if (debitTotal != null && creditTotal != null) {
      return debitTotal > creditTotal ? (debitTotal - creditTotal) : null;
    } else {
      return 0.0;
    }
  }

  public Double getRemainingReceivable() {
    if (debitTotal != null && creditTotal != null) {
      return debitTotal < creditTotal ? (creditTotal - debitTotal) : 0.0;
    } else {
      return 0.0;
    }
  }

  private AccountingLedger filterLedger;

  public List<AccountingLedger> accountingLedgerAuto(String filter) {
    return FinLookupView.accountingLedgerAuto(filter, UserRuntimeView.instance().getCompany().getId());
  }

  public AccountingLedger getFilterLedger() {
    return filterLedger;
  }

  public void setFilterLedger(AccountingLedger filterLedger) {
    this.filterLedger = filterLedger;
  }

  public void selectAccountingGroup(MainView main) {
    setAccountingGroupList(FinLookupView.accountingGroupAuto(" ", UserRuntimeView.instance().getCompany().getId()));
  }

  public void selectRelatedLedger(MainView main) {
    setRelatedAccountingLedger(getRelatedLedgerList(main));
  }

  public List<AccountingGroup> getAccountingGroupList() {
    return accountingGroupList;
  }

  public void setAccountingGroupList(List<AccountingGroup> accountingGroupList) {
    this.accountingGroupList = accountingGroupList;
  }

  public List<AccountingLedger> getRelatedAccountingLedger() {
    return relatedAccountingLedger;
  }

  public void setRelatedAccountingLedger(List<AccountingLedger> relatedAccountingLedger) {
    this.relatedAccountingLedger = relatedAccountingLedger;
  }

  public String getLedgerAddress() {
    ledgerAddress = null;
    if (accountingLedger != null && accountingLedger.getAccountingEntityTypeId() != null) {
      if (accountingLedger.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_VENDOR.getId() || accountingLedger.getAccountingEntityTypeId().getId() == AccountingConstant.ACC_ENTITY_CUSTOMER.getId()) {
        ledgerAddress = accountingLedger.getAddress();
      }
    }
    return ledgerAddress;
  }

  public Double getDebit() {
    return debit;
  }

  public void setDebit(Double debit) {
    this.debit = debit;
  }

  public Double getCredit() {
    return credit;
  }

  public void setCredit(Double credit) {
    this.credit = credit;
  }

  public Double getProcessedAmountCredit() {
    return processedAmountCredit;
  }

  public void setProcessedAmountCredit(Double processedAmountCredit) {
    this.processedAmountCredit = processedAmountCredit;
  }

  public Double getProcessedAmountDebit() {
    return processedAmountDebit;
  }

  public void setProcessedAmountDebit(Double processedAmountDebit) {
    this.processedAmountDebit = processedAmountDebit;
  }

}
