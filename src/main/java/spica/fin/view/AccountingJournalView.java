/*
 * @(#)AccountingJournalView.java	1.0 Fri Feb 24 15:58:33 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.event.SelectEvent;
import spica.fin.domain.AccountingDocType;
import spica.fin.domain.AccountingHead;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingLedgerBalance;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingVoucherType;
import spica.fin.service.AccountingLedgerTransactionService;
import spica.fin.service.AccountingPrefixService;
import spica.fin.service.AccountingTransactionDetailService;
import spica.fin.service.AccountingTransactionService;
import spica.fin.service.LedgerExternalDataService;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.domain.AccountingTransactionSettlement;
import spica.fin.domain.ChequeEntry;
import spica.fin.service.AccountingLedgerService;
import spica.fin.service.AccountingTransactionDetailItemService;
import static spica.fin.service.AccountingTransactionService.hasBillItemsAttached;
import spica.fin.service.AccountingTransactionSettlementService;
import spica.fin.service.TaxCodeService;
import spica.fin.service.TradeOutstandingService;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.SalesReturn;
import spica.scm.print.PrintJournal;
import spica.scm.print.PrintchequeReceipt;
import spica.scm.service.CompanyAddressService;
import spica.scm.service.CompanyLicenseService;
import spica.scm.service.CompanySettingsService;
import spica.scm.util.MathUtil;
import spica.scm.view.ScmLookupExtView;
import spica.scm.view.ScmLookupView;
import spica.sys.FileConstant;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 * AccountingTransactionView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:33 IST 2017
 */
@Named(value = "journalView")
@ViewScoped
public class AccountingJournalView implements Serializable {

  private transient List<AccountingTransactionDetail> journalAllList = new ArrayList<>();
  private AccountingTransaction filterJournal;
  //private transient Part referenceDocFilePathPart;

  private Double debitOldValue;
  private Double debitTotal;
  private Double creditOldValue;
  private Double creditTotal;

  private AccountingLedgerBalance parent;
  private AccountingTransactionDetail accountingTransactionDetail = null;
  private AccountingLedger accountingLedger;
  private List<AccountGroup> accountGroupList; // To make it visble for jikku
  private String filterValue;
  private boolean journalChanged = false;
  private boolean editable = false;
  @Inject
  private AccountingMainView accountingMainView;
  private AccountingVoucherType accountingVoucherType;
  private boolean hasBill = false;

  /**
   * Default Constructor.
   */
  public AccountingJournalView() {
    super();
  }

  @PostConstruct
  public void init() {
    parent = (AccountingLedgerBalance) Jsf.popupParentValue(AccountingLedgerBalance.class);
    filterJournal = (AccountingTransaction) Jsf.popupParentValue(AccountingTransaction.class);
    ChequeEntry ce = (ChequeEntry) Jsf.popupParentValue(ChequeEntry.class);
    SalesReturn sr = (SalesReturn) Jsf.popupParentValue(SalesReturn.class);
    MainView main = Jsf.getMain();
    try {
      if (ce != null) {
        filterJournal = null;
        for (AccountingVoucherType vt : ScmLookupView.accountingVoucherType(main)) {
          if (ce.getInOrOut() == 1 && vt.getId() == AccountingConstant.VOUCHER_TYPE_RECEIPT.getId().intValue()) {
            newTransactionWith(main, vt, ce.getLedgerId());
            break;
          } else if (ce.getInOrOut() == 0 && vt.getId() == AccountingConstant.VOUCHER_TYPE_PAYMENT.getId().intValue()) {
            newTransactionWith(main, vt, ce.getLedgerId());
            break;
          }
        }
      }
      if (sr != null) {
        filterJournal = null;
        for (AccountingVoucherType vt : ScmLookupView.accountingVoucherType(main)) {
          if (vt.getId() == AccountingConstant.VOUCHER_TYPE_JOURNAL.getId().intValue()) {
            AccountingLedger al = AccountingLedgerService.selectLedgerByEntity(main, sr.getCustomerId().getId(), AccountingConstant.ACC_ENTITY_CUSTOMER, sr.getCompanyId().getId());
            newTransactionWith(main, vt, al);
            break;
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void newTransactionWith(MainView main, AccountingVoucherType v, AccountingLedger l1) {
    newTransaction(main, v);
    addNewJournal(true);
    getFilterJournal().getTransactionDetail().get(0).setAccountingLedgerId(l1);
  }

  private boolean newinstance = true;

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchJournal(MainView main, String viewType) {
    if (newinstance) {
      accountingMainView.setSelectedSummary(1);
      accountingMainView.setSelectedVoucherType(null);
      newinstance = false;
    }
//    accountingTransactionDetail = null;
    journalChanged = false;
    editable = false;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        if (parent != null) {
          viewType = ViewTypes.editform;
          getFilterJournal().setId(parent.getId());
        }
        SystemRuntimeConfig.initCurrency(main);
        if (ViewTypes.isList(viewType)) {
          if (journalAllList != null) {
            journalAllList.clear();
          }
          accountGroupList = null;
          accountingLedger = null;
          getFilterJournal().setCompanyId(null);
          accountingMainView.init(main, getFilterJournal().getCompanyId());
        } else if (main.isEdit() && !main.hasError()) {
          filterJournal = (AccountingTransaction) AccountingTransactionService.selectByPk(main, filterJournal);
          filterJournal.setTransactionDetail(AccountingTransactionDetailService.selectByTransaction(main, filterJournal));
          journalAllList = filterJournal.getTransactionDetail();
          String existing = hasBillItemsAttached(main, journalAllList);
          if (!StringUtil.isEmpty(existing)) {
            Jsf.warn("already.used", new String[]{existing});
          } else {
            editable = true;
          }

//         boolean acGroup = false;
          if (filterJournal.getAccountGroupId() == null) {
            findPartyAndIndex(); //to find the party
            loadAccountGroup(partyDetail);
          }

          for (AccountingTransactionDetail td : filterJournal.getTransactionDetail()) {
//            if (!acGroup) {
//              acGroup = loadAccountGroup(td);
//            }
            AccountingLedgerTransactionService.setLedgerbalance(main, td.getAccountingLedgerId());
          }
          main.close(); // to do causing problem with out close due to below 
          sumUpTotal(journalAllList, true);
          setSettlementValue(main);
        } else if (main.isNew() && !main.hasError()) {
          accountingMainView.setSelectedAccountGroup(null);
//          partyLedger = null;
        }
        getFilterJournal().setJournalSettlement(getFilterJournal().isJournal());
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  public void backOrNext(int pos, Company company, boolean from) {
    if (accountingMainView.backOrNext(pos, company.getCurrentFinancialYear(), from)) {
      journalAllList.clear();
    }
  }

  public void onFromDateSelect(SelectEvent event) {
    accountingMainView.setFromDate((Date) event.getObject());
    journalAllList.clear();
  }

  public void onToDateSelect(SelectEvent event) {
    accountingMainView.setToDate((Date) event.getObject());
    journalAllList.clear();
  }

  public void voucherTypeFilter(SelectEvent event) {
    accountingMainView.setSelectedVoucherType((Long) event.getObject());
    journalAllList.clear();
  }

  public void summaryFilter(SelectEvent event) {
    accountingMainView.setSelectedSummary((int) event.getObject());
    journalAllList.clear();
  }

  private boolean removeFromDb(AccountingTransactionDetail item) {
    return (item.getId() != null && removeFromSave(item));
  }

  private boolean removeFromSave(AccountingTransactionDetail item) {
    return (item.getAccountingLedgerId() == null || (item.getDebit() == null && item.getCredit() == null));
  }

//  public void save(MainView main) {
//    save(main, true);
//  }
  public void save(MainView main, boolean backToNew) {
    try {

      if (main.isEdit() && !isEditable()) {
        main.error("save.not.allowed");
        return;
      }
      List<AccountingTransactionDetail> deleteFromdbList = filterJournal.getTransactionDetail().stream().filter(item -> removeFromDb(item)).collect(Collectors.toList());
      filterJournal.getTransactionDetail().removeIf(item -> removeFromSave(item));

      if (filterJournal.getAccountGroupId() == null && !StringUtil.isEmpty(accountGroupList)) {
        if (accountGroupList.size() == 1) {
          filterJournal.setAccountGroupId(accountGroupList.get(0));
        } else {
          main.error("account.group.required");
          return;
        }
      }

      if (filterJournal.isJournal() || filterJournal.isContra()) {
        sumUpTotal(filterJournal.getTransactionDetail(), false);
        if (debitTotal.doubleValue() != creditTotal) {
          filterJournal.getTransactionDetail().addAll(deleteFromdbList); //adding it back cause delete didnt happen
          main.error("amount.not.tally");
          addJournalRow();
          return;
        }
        if (StringUtil.isEmpty(filterJournal.getTransactionDetail())) {
          main.error("select.ledger");
          return;
        }

        //May be this will never happen
        for (AccountingTransactionDetail atd : deleteFromdbList) { // removing the null or cleared entries
          AccountingTransactionDetailService.deleteByPk(main, atd);
        }

        hasBill = false;

        if (filterJournal.isJournalSettlement()) {
          int size = filterJournal.getTransactionDetail().size();
          if (size > 1) {
            // AccountingTransactionDetail partyDetail = null;
            int drOrCrIndex = findPartyAndIndex();
            if (partyDetail != null) {
              if (StringUtil.isEmpty(partyDetail.getDetailItem())) {
                hasBill = true;
              } else {
                for (AccountingTransactionDetailItem item : partyDetail.getDetailItem()) { // to solve mismatch in item and main total
                  if (item.getNetAmount() == null || item.getNetAmount().doubleValue() != creditTotal) {

                    hasBill = true;
                    break;
                  }
                }
              }
              if (hasBill) {
                Jsf.execute("docItemFocus(" + StringUtil.gtDouble(partyDetail.getDebit(), 0.00) + "," + drOrCrIndex + ")");
                return;
              }
            }
          }
          hasBill = false;
        }
        LedgerExternalDataService.setDebitCreditLedger(filterJournal);
      } else if (isCreditFixed() || isDebitFixed()) {
        accountingTransactionDetail.setAccountingTransactionId(filterJournal);

        if (isCreditFixed()) {
          setTotal(true);
          if (accountingTransactionDetail.getCredit() == null) {
            main.error("enter.the.amount");
            return;
          }
          Double total = 0.00;
          if (creditTotal == null || debitTotal == null) {
            total = accountingTransactionDetail.getCredit();
          } else {
            total = creditTotal < debitTotal ? debitTotal : creditTotal;
          }
          if (filterJournal.isExpenses()) { // for expense added tax to bill value
            if (!StringUtil.isEmpty(accountingTransactionDetail.getDetailItem())) {
              for (AccountingTransactionDetailItem item : accountingTransactionDetail.getDetailItem()) {
                item.setNetAmount(total);
                item.setAdjustmentAmount(total);
                item.setBalanceAmount(total);
                item.setTaxAmount(tax);
                break;
              }
            }
          }
          setToTransaction(filterJournal.getTransactionDetail().get(0), accountingTransactionDetail, total);
        } else if (isDebitFixed()) {
          setTotal(false);
          if (accountingTransactionDetail.getDebit() == null) {
            main.error("enter.the.amount");
            return;
          }
          Double total = 0.00;
          if (creditTotal == null || debitTotal == null) {
            total = accountingTransactionDetail.getDebit();
          } else {
            total = debitTotal < creditTotal ? creditTotal : debitTotal;
          }
          setToTransaction(accountingTransactionDetail, filterJournal.getTransactionDetail().get(0), total);
        }
        filterJournal.getTransactionDetail().add(0, accountingTransactionDetail); // Adding as first element to save
      }
      AccountingTransactionService.insertOrUpdateAll(main, filterJournal);

      main.commit("success.save");
      sumUpTotal(filterJournal.getTransactionDetail(), !(filterJournal.isJournal() || filterJournal.isContra())); // not of journal instruct to set the top value and remove from list

      if (backToNew) {
        accountingTransactionDetail = null;
        main.setViewType(ViewTypes.editform);
      } else { //its clone
        cloneTransaction(main);
      }

    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  private int findPartyAndIndex() { //always finds the last party
    int drOrCrIndex = 0;
    int i = 0;
    partyDetail = null; //resetting previous
    for (AccountingTransactionDetail di : filterJournal.getTransactionDetail()) {
      if (di.getAccountingLedgerId() != null) {
        if (di.getAccountingLedgerId().isDebtorsOrCreditors()) {
          if (di.getAccountingLedgerId().getBillwise() != null && (di.getAccountingLedgerId().getBillwise() == AccountingConstant.BILLWISE)) { //checking only if bill wise is enabled
            partyDetail = di;
            drOrCrIndex = i;
            if (!StringUtil.isEmpty(di.getDetailItem())) {
              return drOrCrIndex; //not empty
            }         // break;
          }
        }
      }
      i++;
    }
    return drOrCrIndex;
  }

  private void setToTransaction(AccountingTransactionDetail debit, AccountingTransactionDetail credit, Double total) {
    filterJournal.setLedgerCreditId(credit.getAccountingLedgerId());
    filterJournal.setLedgerDebitId(debit.getAccountingLedgerId());
    filterJournal.setTotalAmount(total);
    if (filterJournal.isSales()) {
      setDeailItemTotal(debit.getDetailItem(), total);
    } else if (filterJournal.isPurchase()) {
      setDeailItemTotal(credit.getDetailItem(), total);
    }
  }

  private void setDeailItemTotal(List<AccountingTransactionDetailItem> itemList, Double total) {
    if (itemList != null) {
      if (!(filterJournal.isPayment() || filterJournal.isReceipt())) {
        for (AccountingTransactionDetailItem item : itemList) {
          item.setNetAmount(total);
          item.setBalanceAmount(total);
          item.setTaxAmount(tax);
          item.setGoodsAmount(total - tax);
          break;
        }
      }
    }
  }

  public void actionOpenDetailItemPopup(MainView main, AccountingTransactionDetail detail) {
    actionOpenDetailItemPopup(main, detail, true);
  }

  public void actionOpenDetailItemPopup(MainView main, AccountingTransactionDetail detail, boolean isTopCredit) {
    try {
      if (detail.getAccountingLedgerId() == null) {
        main.error("select.ledger");
        return;
      }

      detail.setAccountingTransactionId(filterJournal);
      detail.setVoucherTypeId(filterJournal.getVoucherTypeId());
      getAccountingTransactionDetail().setAccountingTransactionId(filterJournal);
      accountingTransactionDetail.setVoucherTypeId(filterJournal.getVoucherTypeId());

      if (filterJournal.isPurchase() || filterJournal.isCreditNote() || filterJournal.isExpenses()) {
        if (accountingTransactionDetail.getAccountingLedgerId() == null) {
          main.error("select.ledger");
          return;
        }
        setCreditPop(detail, accountingTransactionDetail.getAccountingLedgerId().getBillwise() != null && (accountingTransactionDetail.getAccountingLedgerId().getBillwise() == AccountingConstant.BILLWISE || accountingTransactionDetail.getAccountingLedgerId().isDebtorsOrCreditors()));
      } else if ((filterJournal.isPayment() || filterJournal.isReceipt()) && (detail.getAccountingLedgerId().getBillwise() != null && (detail.getAccountingLedgerId().getBillwise() == AccountingConstant.BILLWISE && detail.getAccountingLedgerId().isDebtorsOrCreditors()))) {
        setDebitPop(detail, isTopCredit);
      } else if (filterJournal.isSales() || filterJournal.isDebitNote()) {
        setCreditPop(detail, true); //TODO check bot object are same
      } else if (filterJournal.isJournal()) {
        clearDetail(detail);
        findPartyAndIndex(); //to find the party
        loadAccountGroup(partyDetail);
//        AccountingLedgerTransactionService.setLedgerbalance(main, partyDetail.getAccountingLedgerId());
//        for (AccountingTransactionDetail item : filterJournal.getTransactionDetail()) {
//          if (loadAccountGroup(item)) {
//            break;
//          }
//        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public boolean isAccountGroup() {
    return (accountGroupList != null && accountGroupList.size() > 1);
  }

  private void setCreditPop(AccountingTransactionDetail popAccTran, boolean billwise) {
    if (popAccTran.getTaxCodeId() == null && billwise) {
      if (accountingTransactionDetail.getAccountingLedgerId() == null) {
        Jsf.error("select.ledger");
        return;
      }
      if (filterJournal.isSales() || filterJournal.isDebitNote()) {
        setTotal(false);
      } else if (!filterJournal.isJournalSettlement()) {
        setTotal(true);
      }
      loadAccountGroup(accountingTransactionDetail);
      accountingTransactionDetail.setOtherDetail(popAccTran); //to passthe other main object
      accountingMainView.setSelectedAccountGroup(filterJournal.getAccountGroupId());
      LedgerExternalDataService.setDebitCreditLedger(filterJournal);
      Jsf.popupForm(FileConstant.ACCOUNTING_DETAILS_ITEM, accountingTransactionDetail, accountingTransactionDetail.getId());
    }
  }

  private void setDebitPop(AccountingTransactionDetail popAccTran, boolean isTopCredit) {
    if (popAccTran.getTaxCodeId() == null) {
      if (!filterJournal.isJournalSettlement()) {
        setTotal(isTopCredit);
      }
      popAccTran.setOtherDetail(accountingTransactionDetail); //to passthe other main object      
      loadAccountGroup(popAccTran);
      accountingMainView.setSelectedAccountGroup(filterJournal.getAccountGroupId());
      LedgerExternalDataService.setDebitCreditLedger(filterJournal);
      Jsf.popupForm(FileConstant.ACCOUNTING_DETAILS_ITEM, popAccTran, popAccTran.getId());
    }
  }

  private void loadAccountGroup(AccountingTransactionDetail detail) {
    if (detail != null && detail.getAccountingLedgerId() != null) {
      accountingMainView.setSelectedAccountGroup(null);
      // partyLedger = detail.getAccountingLedgerId();
      if (detail.getAccountingLedgerId().isCustomer()) {
        accountGroupList = ScmLookupExtView.accountGroupByCustomerId(detail.getAccountingLedgerId().getEntityId());
      }
      if (detail.getAccountingLedgerId().isVendor()) {
        accountGroupList = ScmLookupExtView.accountGroupByVendor(detail.getAccountingLedgerId().getEntityId());
      }
      // return true;
    }
//    else{
//      partyLedger = null;
//      accountGroupList = null;
//      accountingMainView.setSelectedAccountGroup(null);
//    }
    //   return false;
  }

  private void setTotal(boolean isTopCredit) {
    tax = 0.00;
    if (isTopCredit) {
      accountingTransactionDetail.setCredit(null);
      filterJournal.getTransactionDetail().stream().mapToDouble(item -> debitSumFilter(item, accountingTransactionDetail)).sum();
    } else {
      accountingTransactionDetail.setDebit(null);
      filterJournal.getTransactionDetail().stream().mapToDouble(item -> creditSumFilter(item, accountingTransactionDetail)).sum();
    }
  }

  private transient Double tax;

  private Double debitSumFilter(AccountingTransactionDetail detail, AccountingTransactionDetail topLedger) {
    Double debit = (detail.getAccountingLedgerId() != null && detail.getDebit() != null) ? detail.getDebit() : 0.00;
    if (topLedger.getCredit() == null) {
      topLedger.setCredit(debit);
    } else if (detail.getAccountingLedgerId() != null) { // tds then reduce
      if (detail.getAccountingLedgerId().isTds()) {
        topLedger.setCredit(topLedger.getCredit() - detail.getCredit());
      } else {
        topLedger.setCredit(topLedger.getCredit() + debit);
        if (detail.getAccountingLedgerId().isTax()) {
          tax += debit;
        }
      }
    }
    return debit;
  }

  private Double creditSumFilter(AccountingTransactionDetail detail, AccountingTransactionDetail topLedger) {
    Double credit = (detail.getAccountingLedgerId() != null && detail.getCredit() != null) ? detail.getCredit() : 0.00;
    if (topLedger.getDebit() == null) {
      topLedger.setDebit(credit);
    } else if (detail.getAccountingLedgerId() != null) { // tds then reduce
      if (detail.getAccountingLedgerId().isTds()) {
        topLedger.setDebit(topLedger.getDebit() - detail.getDebit());
      } else {
        topLedger.setDebit(topLedger.getDebit() + credit);
        if (detail.getAccountingLedgerId().isTax()) {
          tax += credit;
        }
      }
    }
    return credit;
  }

  public void transactionDetailItemDialogReturn() {
    AccountingTransactionDetail parent = (AccountingTransactionDetail) Jsf.popupReturnValue(null, AccountingTransactionDetail.class);
    if (filterJournal.isJournalSettlement()) { // while visiting bill settle we change to payment/receipt turning it back to journal before save
      filterJournal.setVoucherTypeId(AccountingConstant.VOUCHER_TYPE_JOURNAL);
    }
    // System.out.println(partyDetail);

    // AccountingTransactionDetail partyDetail = filterJournal.getTransactionDetail().get(0);
    //  partyDetail = filterJournal.getTransactionDetail().get(1);
    findPartyAndIndex(); //To show the item in the current page to set the list from the popup this is required
//    System.out.println(partyDetail);
//    if (partyDetail != null && partyDetail.getAccountingLedgerId() != null && !partyDetail.getAccountingLedgerId().isDebtorsOrCreditors() && filterJournal.getTransactionDetail() != null && filterJournal.getTransactionDetail().size() > 1) {
//      partyDetail = filterJournal.getTransactionDetail().get(1);
//    }
  }

  public void openLedgerPopup() {
    Jsf.popupForm(FileConstant.ACCOUNTING_LEDGER, new AccountingLedger(), null);
  }

  public void addNewJournal(boolean isAdd) {
    if (StringUtil.isEmpty(filterJournal.getTransactionDetail()) || (filterJournal.getTransactionDetail().get(0).getId() != null && filterJournal.getTransactionDetail().get(filterJournal.getTransactionDetail().size() - 1).getId() != null)) {
      filterJournal.addTransactionDetail(new AccountingTransactionDetail());
      if (filterJournal.isPurchase() || filterJournal.isSales() || filterJournal.isExpenses()) {
        filterJournal.addTransactionDetail(new AccountingTransactionDetail()); //Tax 1 cgst
        filterJournal.addTransactionDetail(new AccountingTransactionDetail()); //Tax 2 sgst
        filterJournal.addTransactionDetail(new AccountingTransactionDetail()); //Round off        
      } else if (filterJournal.isPayment() || filterJournal.isReceipt() || filterJournal.isJournal() || filterJournal.isContra()) {
        filterJournal.addTransactionDetail(new AccountingTransactionDetail()); //For payment party
      }
    }
  }

  private void resetCredit(AccountingTransactionDetail journal, int pos) {
    AccountingTransactionDetail j = filterJournal.getTransactionDetail().get(pos);
    Double val = j.getCredit();
    if (val == null) {
      val = 0.0;
    }
    if (debitOldValue != null && journal != j && debitOldValue == val.doubleValue()) {
      if (journal.getDebit() >= val) {
        if ((j.getDebit() == null || j.getDebit() == 0) && (j.getCredit() == null || j.getCredit() == 0)) {
          filterJournal.getTransactionDetail().get(pos).setDebit(null);
          filterJournal.getTransactionDetail().get(pos).setCredit(journal.getDebit());
        }
      }
    }
  }

  public void onDebitBlur(MainView main, AccountingTransactionDetail detail, int index) {
    try {
      if (journalChanged) {
        if (detail.getDebit() != null && detail.getDebit() > 0) {
          if (filterJournal.getTransactionDetail().size() < 3) {
            resetCredit(detail, 0);
            resetCredit(detail, 1);
          }
        }
        clearDetail(detail);
        addJournalRow();
      }
      if (StringUtil.gtDouble(detail.getDebit(), 0.0)) {
        detail.setCredit(null); // set the next row with this value
        showBill(main, detail, index);
      }
      main.clear();
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void onCreditBlur(MainView main, AccountingTransactionDetail detail, int index) {
    try {
      if (journalChanged) {
        if (detail.getCredit() != null && detail.getCredit() > 0) {
          if (filterJournal.getTransactionDetail().size() < 3) {
            resetDebit(detail, 0);
            resetDebit(detail, 1);
          }
        }
        clearDetail(detail);
        addJournalRow();
      }
      if (StringUtil.gtDouble(detail.getCredit(), 0.0)) {
        detail.setDebit(null); // set the next row with this value //we need rowindex + sequence no
        showBill(main, detail, index);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void showBill(MainView main, AccountingTransactionDetail drOrCr, int index) {
    LedgerExternalDataService.setDebitCreditLedger(filterJournal);
    if (!isValidDrCr()) {
      //Jsf.error("select.ledger");
      return;
    }
    if (hasBill) { // only for 2nd row we are doing this on other rows it should work from the confirm check
      journalBillSettle(main, drOrCr);
      hasBill = false;
    } else {
      if (!drOrCr.getAccountingLedgerId().isDebtorsOrCreditors()) {
        journalBillSettle(main, filterJournal.getBiggestCredit().getAccountingLedgerId().isDebtorsOrCreditors() ? filterJournal.getBiggestCredit() : filterJournal.getBiggestDebit());
      } else { //when both dr only opening the last party
        if (partyDetail == null) {
          findPartyAndIndex();
        }
        if (drOrCr.getAccountingLedgerId().getId().intValue() == partyDetail.getAccountingLedgerId().getId()) {
          journalBillSettle(main, drOrCr);
        }
      }
    }
  }

  private boolean isValidDrCr() {
    return (filterJournal.getBiggestCredit() != null && filterJournal.getBiggestDebit() != null && filterJournal.getBiggestDebit().getAccountingLedgerId() != null && filterJournal.getBiggestCredit().getAccountingLedgerId() != null);
  }

  private void journalBillSettle(MainView main, AccountingTransactionDetail drOrCr) { //Detail should always be dr/cr
    //  if (index > 0) { //ie alteast there is two records, checking the first is not empty 
    if (!drOrCr.getAccountingLedgerId().isDebtorsOrCreditors()) {
      return;
    }
    if (((drOrCr.getDebit() == null && filterJournal.getBiggestDebit().getDebit() == null) || (drOrCr.getCredit() == null && filterJournal.getBiggestCredit().getCredit() == null))) {
      return;
    }
    journalChanged = false;
    boolean c = filterJournal.getBiggestCredit().getAccountingLedgerId().isDebtorsOrCreditors();
    boolean d = filterJournal.getBiggestDebit().getAccountingLedgerId().isDebtorsOrCreditors();
    if (c && d) {
      accountingTransactionDetail = drOrCr.getAccountingLedgerId().getId().intValue() == filterJournal.getBiggestCredit().getAccountingLedgerId().getId() ? filterJournal.getBiggestDebit() : filterJournal.getBiggestCredit();
    } else {
      accountingTransactionDetail = c ? filterJournal.getBiggestDebit() : filterJournal.getBiggestCredit();
    }
    openPayOrReceipt(main, drOrCr, StringUtil.gtDouble(accountingTransactionDetail.getCredit(), 0.0));
  }

//  private void journalBillSettle(MainView main, AccountingTransactionDetail detail) {
//    journalChanged = false;
//    //  if (index > 0) { //ie alteast there is two records, checking the first is not empty 
//    AccountingTransactionDetail atd = filterJournal.getTransactionDetail().get(0);
//    
//    //Finding the largest nest party
//    
//    if (atd.getAccountingLedgerId() == null || detail.getAccountingLedgerId() == null) {
//      Jsf.error("select.ledger");
//      return;
//    }
//    if (((detail.getDebit() == null && atd.getDebit() == null) || (detail.getCredit() == null && atd.getCredit() == null))) {
//      return;
//    }
//    if (atd.getAccountingLedgerId().isDebtorsOrCreditors()) {
//      accountingTransactionDetail = filterJournal.getTransactionDetail().get(1);
//      openPayOrReceipt(main, atd, StringUtil.gtDouble(accountingTransactionDetail.getCredit(), 0.0));
//    } else if (detail.getAccountingLedgerId().isDebtorsOrCreditors()) {
//      accountingTransactionDetail = atd;
//      openPayOrReceipt(main, detail, StringUtil.gtDouble(atd.getCredit(), 0.0));
//    }
//    //  }
//  }
  private void openPayOrReceipt(MainView main, AccountingTransactionDetail detail, boolean pay) {
    List<AccountingVoucherType> list = ScmLookupView.accountingVoucherType(main);
    int id = pay ? AccountingConstant.VOUCHER_TYPE_PAYMENT.getId() : AccountingConstant.VOUCHER_TYPE_RECEIPT.getId();
    for (AccountingVoucherType accountingVoucherType : list) {
      if (id == accountingVoucherType.getId()) {
        filterJournal.setVoucherTypeId(accountingVoucherType);
      }
    }
    actionOpenDetailItemPopup(main, detail, pay);
  }

  private void resetDebit(AccountingTransactionDetail journal, int pos) {
    AccountingTransactionDetail j = filterJournal.getTransactionDetail().get(pos);
    Double val = j.getDebit();
    if (val == null) {
      val = 0.0;
    }
    if (creditOldValue != null && journal != j && creditOldValue == val.doubleValue()) {
      if (journal.getCredit() >= val) {
        if ((j.getDebit() == null || j.getDebit() == 0) && (j.getCredit() == null || j.getCredit() == 0)) {
          filterJournal.getTransactionDetail().get(pos).setCredit(null);
          filterJournal.getTransactionDetail().get(pos).setDebit(journal.getCredit());
        }
      }
    }
  }

  public void onDebitValueChanged(ValueChangeEvent event) {

    debitOldValue = (Double) event.getOldValue();
    if (debitOldValue == null) {
      debitOldValue = 0.0;
    }
    Double nVal = (Double) event.getNewValue();
    if (nVal == null) {
      nVal = 0.0;
    }
    journalChanged = nVal.doubleValue() != debitOldValue;
  }

  public void onCreditValueChanged(ValueChangeEvent event) {
    creditOldValue = (Double) event.getOldValue();
    if (creditOldValue == null) {
      creditOldValue = 0.0;
    }
    Double nVal = (Double) event.getNewValue();
    if (nVal == null) {
      nVal = 0.0;
    }
    journalChanged = nVal.doubleValue() != creditOldValue;
  }

  private void sumUpTotal(List<AccountingTransactionDetail> journalList, boolean edit) {
    debitTotal = 0.0;
    creditTotal = 0.0;
    int i = 0;

    for (AccountingTransactionDetail j : journalList) {
      if (j.getDebit() != null) {
        debitTotal += j.getDebit();
        if (filterJournal.getVoucherTypeId() != null) {
          if (isDebitFixed()) { //add to top ledger and remove from the list 
            if (edit) { // remove from last
              if (i == 0) {
                accountingTransactionDetail = j;
              }
              i++;
            }
          }
        }
      }
      if (j.getCredit() != null) {
        creditTotal += j.getCredit();
        if (filterJournal.getVoucherTypeId() != null) {
          if (isCreditFixed()) { //add to top ledger and remove from the list 
            if (edit) {
              if (filterJournal.isExpenses()) {
                accountingTransactionDetail = journalList.get(0); // Take the first one this is expense ledger rest is tax
              } else {
                if (i == 0) {
                  accountingTransactionDetail = j;
                }
                i++;
              }
            }
          }
        }
      }
    }
    if (edit && accountingTransactionDetail != null) {
      filterJournal.removeTransactionDetail(accountingTransactionDetail);
    }
    creditTotal = MathUtil.roundOff(creditTotal, 2);
    debitTotal = MathUtil.roundOff(debitTotal, 2);
    filterJournal.setCreditTotal(creditTotal);
    filterJournal.setDebitTotal(debitTotal);

  }

  private void addJournalRow() {
    sumUpTotal(filterJournal.getTransactionDetail(), false);
    // boolean hasLedger = true;
    if (debitTotal.doubleValue() != creditTotal) {
      AccountingTransactionDetail last = filterJournal.getTransactionDetail().get(filterJournal.getTransactionDetail().size() - 1);
      if (!(last.getDebit() == null && last.getCredit() == null)) {
        if (last.getAccountingLedgerId() == null) { // add to last field
          Double val = 0.00;
          if (debitTotal.doubleValue() < creditTotal) {
            val = last.getDebit();
            last.setDebit(creditTotal - debitTotal.doubleValue());
            if (val != null) {
              last.setDebit(last.getDebit() + val);
            }
            val = last.getCredit();
            if (val != null) {
              last.setDebit(last.getDebit() - val);
            }
            if (last.getDebit() <= 0.00) {
              last.setDebit(null);
            }
            last.setCredit(null);
          } else {
            val = last.getCredit();
            last.setCredit(debitTotal.doubleValue() - creditTotal);
            if (val != null) {
              last.setCredit(last.getCredit() + val);
            }
            val = last.getDebit();
            if (val != null) {
              last.setCredit(last.getCredit() - val);
              if (last.getCredit() <= 0.00) {
                last.setCredit(null);
              }
            }
            last.setDebit(null);
          }
        } else {
          AccountingTransactionDetail next = new AccountingTransactionDetail();
          if (debitTotal.doubleValue() < creditTotal) {
            next.setDebit(creditTotal - debitTotal.doubleValue());
          } else {
            next.setCredit(debitTotal.doubleValue() - creditTotal);
          }
          filterJournal.addTransactionDetail(next);
        }
        sumUpTotal(filterJournal.getTransactionDetail(), false);
      }
    } else if (debitTotal != null && debitTotal.doubleValue() > 0) {
      sumUpTotal(filterJournal.getTransactionDetail(), false); //TODO do we need this
    }
  }

  public List<AccountingTransactionDetail> getJournalList(MainView main) {
    if (main.isForm() && StringUtil.isEmpty(getFilterJournal().getTransactionDetail())) {
      try {
        //journalList = AccountingTransactionService.listAll(main);
        addNewJournal(false); //By default add a new row when list is loaded, comment it if this is not required
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return getFilterJournal().getTransactionDetail();
  }

  public List<AccountingTransactionDetail> getJournalAllList(MainView main) {
    if (main.isList() && StringUtil.isEmpty(journalAllList)) {
      try {
        AccountingTransactionDetailService.sumUpTotal(main, accountingMainView, getFilterJournal());
        creditTotal = filterJournal.getCreditTotal();
        debitTotal = filterJournal.getDebitTotal();
        journalAllList = AccountingTransactionDetailService.listAll(main, accountingMainView, getFilterJournal().getCompanyId());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return journalAllList;
  }

  public AccountingTransaction getFilterJournal() {
    if (filterJournal == null) {
      filterJournal = new AccountingTransaction();
    } else if (accountingMainView.getSelectedAccountGroup() != null && filterJournal.getAccountGroupId() != null) {
      if (filterJournal.getAccountGroupId().getId().intValue() != accountingMainView.getSelectedAccountGroup().getId()) {
        filterJournal.setAccountGroupId(accountingMainView.getSelectedAccountGroup());
      }
    }
    if (filterJournal.getCompanyId() == null) {
      filterJournal.setCompanyId(UserRuntimeView.instance().getCompany());
      filterJournal.setEntryDate(SystemRuntimeConfig.getMaxEntryDate(filterJournal.getCompanyId()));
      filterJournal.setCurrencyId(filterJournal.getCompanyId().getCurrencyId());
    }
    return filterJournal;
  }

  public void setFilterJournal(AccountingTransaction filterJournal) {
    this.filterJournal = filterJournal;
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

  public void cloneTransaction(MainView main) {
    clearTran(main, false);
  }

  public void newTransaction(MainView main, AccountingVoucherType accountingVoucherType) {
    this.accountingVoucherType = accountingVoucherType;
    accountingTransactionDetail = null;
    clearTran(main, true);
  }

  private void clearTran(MainView main, boolean isNew) {
    try {
      parent = null;
      creditTotal = null;
      debitTotal = null;
      hasBill = false;
      main.setViewType("newform");
      if (isNew) {
        filterJournal = null;
      }
      if (filterJournal != null) {
        filterJournal.setId(null);
        filterJournal.setNarration("");
        filterJournal.setAccountGroupId(null);
        for (AccountingTransactionDetail detail : filterJournal.getTransactionDetail()) {
          detail.setId(null);
          clearDetail(detail);
          // detail.setOtherDetail(null);
        }
      }
      accountGroupList = null;
      totalPaid = 0.0;
      advancePaid = 0.0;
      getAccountingTransactionDetail();

      getAccountingTransactionDetail().setId(null);
      getAccountingTransactionDetail().setDebit(null);
      getAccountingTransactionDetail().setCredit(null);

      if (getFilterJournal().getVoucherTypeId() == null) {
        getFilterJournal().setVoucherTypeId(accountingVoucherType);
      }
      getFilterJournal().setDocumentNumber(AccountingPrefixService.getPrefixOnSelect(main, filterJournal));
      //   if (partyDetail != null) {
      clearDetail(partyDetail);
//        StringUtil.clearList(partyDetail.getDetailItem());
//        StringUtil.clearList(partyDetail.getSettlementAdjustedList());
//        StringUtil.clearList(partyDetail.getSettlementList());
      //   }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void clearDetail(AccountingTransactionDetail detail) {
    if (detail != null) {
      StringUtil.clearList(detail.getDetailItem());
      StringUtil.clearList(detail.getDetailItemPayable());
      StringUtil.clearList(detail.getDetailItemReceivable());
      StringUtil.clearList(detail.getSettlementAdjustedList());
      StringUtil.clearList(detail.getSettlementList());
      StringUtil.clearList(detail.getSettlementUsedList());
    }
  }

  private transient Integer oldId;
  private transient String color;
  private int cnt;

  public String getColor(Integer id) {
    if (oldId != null && id.intValue() == oldId) {

    } else {
      oldId = id;
      cnt++;
      if (cnt % 2 == 0) {
        color = "#3498db"; //"#05659E";
      } else {
        color = "#0dccc0";//"#D85426";
      }
    }
    return color;
  }

  public String getBreadCrumb() {
    return filterJournal.getVoucherTypeId().getTitle() + " / " + filterJournal.getDocumentNumber();
  }

  public AccountingLedger getAccountingLedger() {
    return accountingLedger;
  }

  public void setAccountingLedger(AccountingLedger accountingLedger) {
    this.accountingLedger = accountingLedger;
  }

  public boolean isCreditFixed() {
    return (filterJournal.isPayment() || filterJournal.isPurchase() || filterJournal.isCreditNote() || filterJournal.isExpenses());
  }

  public boolean isDebitFixed() {
    return (filterJournal.isReceipt() || filterJournal.isSales() || filterJournal.isDebitNote());
  }

  public void showSettlement(MainView main, AccountingTransactionDetail t) {
    t.setAccountingTransactionId(filterJournal);
    actionOpenDetailItemPopup(main, accountingTransactionDetail);
  }

  public void addNewTaxRaw() {
    AccountingTransactionDetail item = filterJournal.getTransactionDetail().get(filterJournal.getTransactionDetail().size() - 1);
    if (item.getAccountingLedgerId() != null && (item.getCredit() != null || item.getDebit() != null)) {
      LedgerExternalDataService.getTransactionDetailNew(filterJournal, null, null, null);
    }
    sumUpTotal(filterJournal.getTransactionDetail(), false);
  }

  public void showBalanceAndAccountGroup(MainView main, AccountingTransactionDetail detail) {
    try {
      clearDetail(detail);
      detail.setAccountingTransactionId(getFilterJournal());
      AccountingLedgerTransactionService.getLedgerBalance(main, detail.getAccountingLedgerId());
      loadAccountGroup(detail);
      accountingMainView.setSelectedAccountGroup(null);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void showBalanceAndAddRowPayment(MainView main, AccountingTransactionDetail detail) { //TODO where it is used
    try {
      AccountingLedgerTransactionService.getLedgerBalance(main, detail.getAccountingLedgerId());
      if (detail.getAccountingLedgerId() != null && detail.getAccountingLedgerId().isTds()) {
        AccountingTransactionDetail item = filterJournal.getTransactionDetail().get(0);
        if (item.getDebit() != null) {
          detail.setTaxCodeId(TaxCodeService.selectTaxCodeById(main, detail.getAccountingLedgerId().getEntityId()));
          LedgerExternalDataService.computeTaxInclusive(detail, null, item.getDebit()); //reverse set credit for tax
        } else if (item.getCredit() != null) {
          detail.setTaxCodeId(TaxCodeService.selectTaxCodeById(main, detail.getAccountingLedgerId().getEntityId()));
          LedgerExternalDataService.computeTaxInclusive(detail, item.getCredit(), null); //reverse set for credit
        }
      }
      addNewTaxRaw();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void showBalanceAndComputeTax(MainView main, AccountingTransactionDetail detail) {
    try {
      AccountingLedgerTransactionService.getLedgerBalance(main, detail.getAccountingLedgerId());
      if (detail.getAccountingLedgerId() != null) {
        AccountingTransactionDetail item = filterJournal.getTransactionDetail().get(0);
        if (item.getDebit() != null) {
          detail.setTaxCodeId(TaxCodeService.selectTaxCodeById(main, detail.getAccountingLedgerId().getEntityId()));
          LedgerExternalDataService.computeTax(detail, item.getDebit(), null);
        } else if (item.getCredit() != null) {
          detail.setTaxCodeId(TaxCodeService.selectTaxCodeById(main, detail.getAccountingLedgerId().getEntityId()));
          LedgerExternalDataService.computeTax(detail, null, item.getCredit());
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void showBalanceAndAddRow(MainView main, AccountingTransactionDetail detail) {
    try {
      AccountingLedgerTransactionService.getLedgerBalance(main, detail.getAccountingLedgerId());
      addNewTaxRaw();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public AccountingTransactionDetail getAccountingTransactionDetail() {
    if (accountingTransactionDetail == null) {
      accountingTransactionDetail = new AccountingTransactionDetail();
    }
    return accountingTransactionDetail;
  }

  public void setAccountingTransactionDetail(AccountingTransactionDetail accountingTransactionDetail) {
    this.accountingTransactionDetail = accountingTransactionDetail;
  }

  public void actionDeleteTransaction(MainView main) {
    try {
      if (filterJournal.getBankStatus() == null || (filterJournal.getBankStatus() != null && filterJournal.getBankStatus().intValue() != AccountingConstant.BANK_CHEQUE_BOUNCE)) {
        int id = filterJournal.getId();
        AccountingTransactionService.deleteByPk(main, filterJournal);
        main.commit("success.delete");
        main.setViewType(ViewTypes.list);
        if (journalAllList != null) {
          journalAllList.removeIf(item -> (item.getAccountingTransactionId().getId() == id));
        }
      } else {
        main.error("error.delete.bounce", new String[]{"This cheque is bounced; Please remove this cheque from ' Dishonoured ' in Bank Reconciliation.. "});
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    //return null;
  }

  public String getTopPlaceholder() {
    if (filterJournal.isPayment() || filterJournal.isReceipt()) {
      return " Bank/Cash";
    }
    if (filterJournal.isExpenses()) {
      return " Bank/Debtors/Creditors";
    }
    if (filterJournal.isSales() || filterJournal.isPurchase() || filterJournal.isDebitNote() || filterJournal.isCreditNote()) {
      return " Debtors/Creditors";
    }
    return "";
  }

  public List<AccountingLedger> accountingLedgerAuto(String filter) {
    if (filterJournal.isPayment() || filterJournal.isReceipt() || filterJournal.isJournal()) {
      return FinLookupView.accountingLedgerAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isSales()) {
      return FinLookupView.ledgerSalesAllAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isPurchase()) {
      return FinLookupView.ledgerPurchaseAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isCreditNote()) {
      return FinLookupView.ledgerExpenseDirectIndirectAndFixedAssetAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isDebitNote()) {
      return FinLookupView.ledgerExpenseDirectIndirectAndFixedAssetAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isExpenses()) {
      return FinLookupView.ledgerExpenseDirectIndirectAndFixedAssetAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isContra()) {
      return FinLookupView.ledgerPaymentAuto(filter, getFilterJournal().getCompanyId().getId());
    } else {
      return null;
    }
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> accountingLedgerPaymentAuto(String filter) {
    if (filterJournal.isPayment() || filterJournal.isReceipt()) {
      return FinLookupView.ledgerPaymentAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isSales()) {
      return FinLookupView.ledgerDebtorsAndCreditorsAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isPurchase()) {
      return FinLookupView.ledgerDebtorsAndCreditorsAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isCreditNote()) {
      return FinLookupView.ledgerDebtorsAndCreditorsAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isDebitNote()) {
      return FinLookupView.ledgerDebtorsAndCreditorsAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isExpenses()) {
      return FinLookupView.ledgerExpensePaymentAgainstAuto(filter, getFilterJournal().getCompanyId().getId());
    } else {
      return null;
    }
  }

  public List<AccountingLedger> ledgerRoundOffAuto(String filter) {
    return FinLookupView.ledgerRoundOffAuto(filter, getFilterJournal().getCompanyId().getId());
  }

  public List<AccountingLedger> accountingLedgerTaxAuto(String filter) {
    if (filterJournal.isPayment()) {
      return FinLookupView.ledgerTaxTdsAuto(filter, getFilterJournal().getCompanyId().getId());
    } else if (filterJournal.isExpenses() || filterJournal.isPurchase()) { //inputtax
      return FinLookupView.ledgerTaxAuto(filter, getFilterJournal().getCompanyId().getId(), true);
    } else if (filterJournal.isSales()) { //output tax
      return FinLookupView.ledgerTaxAuto(filter, getFilterJournal().getCompanyId().getId(), false);
    } else {
      throw new UnsupportedOperationException("Begger, Not yet implemented");
    }
  }

  public List<AccountingDocType> accountingDocTypeAuto(String filter) {
    return ScmLookupView.accountingDocTypeAuto(filter);
  }

  public List<AccountingHead> accountingHeadAuto(String filter) {
    return ScmLookupView.accountingHeadAuto(filter);
  }

  public Double getTotalPaid() {
    return totalPaid;
  }

  public Double getAdvancePaid() {
    return advancePaid;
  }

  public AccountingTransactionDetail getPartyDetail() {
    return partyDetail;
  }
  //private transient boolean settled;
  private transient Double totalPaid;
  private transient Double advancePaid;
  private transient AccountingTransactionDetail partyDetail;
  //private transient AccountingLedger partyLedger;

  /**
   *
   * @param main
   */
  private void setSettlementValue(MainView main) {

    if (filterJournal.getId() != null) {
      findPartyAndIndex();
      if (partyDetail == null) { //only for journal findPartyAndIndex is going to work
        partyDetail = filterJournal.getTransactionDetail().get(0);
      }
      if (filterJournal.isPayment() || filterJournal.isReceipt() || filterJournal.isJournal()) {
        partyDetail.setDetailItem(AccountingTransactionDetailItemService.selectByTransactionDetailPayment(main, partyDetail.getId()));
        if (StringUtil.isEmpty(partyDetail.getDetailItem()) && filterJournal.isJournal()) {
          partyDetail = filterJournal.getTransactionDetail().get(1);
          partyDetail.setDetailItem(AccountingTransactionDetailItemService.selectByTransactionDetailPayment(main, partyDetail.getId()));
        }
        totalPaid = 0.0;
        advancePaid = 0.0;
        if (!StringUtil.isEmpty(partyDetail.getDetailItem())) {
          AccountingTransactionDetailItem item = partyDetail.getDetailItem().get(0);

          partyDetail.setSettlementList(AccountingTransactionSettlementService.selectByTransactionDetailPayment(main, item.getId()));
          partyDetail.setSettlementAdjustedList(AccountingTransactionSettlementService.selectAdjustedOnly(main, item.getId(), partyDetail));
//           if (StringUtil.isEmpty(partyDetail.getSettlementAdjustedList())){
//               partyDetail.setSettlementAdjustedList(AccountingTransactionSettlementService.selectAdjustedOnly(main, item.getId(), filterJournal.getTransactionDetail().get(1)));
//           }
          partyDetail.setSettlementUsedList(AccountingTransactionSettlementService.selectDetailItemUsedIn(main, item.getId()));
          boolean settled = !StringUtil.isEmpty(partyDetail.getSettlementList());

          if (settled) {
            for (AccountingTransactionSettlement as : partyDetail.getSettlementList()) {
              totalPaid += as.getSettledAmount();
            }
            advancePaid = item.getBalanceAmount() != null ? item.getBalanceAmount() : 0.0;
          }
        }
        // partyDetail.setDetailItemReceivable(AccountingTransactionDetailItemService.listPagedByReceivable(main, partyDetail.getAccountingLedgerId().getId()));
      } else {// if (!filterJournal.isJournal()) {
        List<AccountingTransactionDetailItem> transactionItem = null;
        if (filterJournal.isOpeningEntry()) {
          // partyDetail = filterJournal.getTransactionDetail().get(0);
          transactionItem = AccountingTransactionDetailItemService.selectByTransactionDetail(main, partyDetail.getId());
        } else if (!filterJournal.isContra()) {
          transactionItem = AccountingTransactionDetailItemService.selectByTransactionDetail(main, accountingTransactionDetail.getId());
        }
        if (!StringUtil.isEmpty(transactionItem)) {
          partyDetail.setDetailItem(transactionItem);
          AccountingTransactionDetailItem item = transactionItem.get(0);
          List<AccountingTransactionSettlement> settlement = AccountingTransactionSettlementService.selectByTransactionDetail(main, item.getId());
          partyDetail.setSettlementList(settlement);
          //    if (StringUtil.isEmpty(settlement)) {
          partyDetail.setSettlementUsedList(AccountingTransactionSettlementService.selectByTransactionDetailPayment(main, item.getId()));
          //  }
        }
      }
    }
  }

  public void showDetails(AccountingTransactionDetailItem item) {
    if (item.getAccountingTransactionDetailId() == null) {
      item.getAccountingTransactionDetailId().setAccountingTransactionId(filterJournal);
    }
    AccountingTransactionDetailItemView.showDetails(item);
  }

  public void showDetailsSettlement(AccountingTransactionDetailItem item) {
    AccountingTransactionDetailItemView.showDetails(item);
  }

  public void editEntryDate(MainView main) {
    try {
      AccountingTransactionService.updateByPk(main, filterJournal);
      for (AccountingTransactionDetail detail : filterJournal.getTransactionDetail()) {
        if (detail.getAccountingLedgerId() != null) {
          detail.setEntryDate(filterJournal.getEntryDate()); //TODO remove entry date fromthis db
          AccountingTransactionDetailService.insertOrUpdate(main, detail);
        }
      }
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void updateAccountGroup(MainView main) {
    try {
      AccountingTransactionService.updateByPk(main, filterJournal);
      main.clear();
      main.param(filterJournal.getAccountGroupId().getId());
      main.param(filterJournal.getId());
      TradeOutstandingService.updateAccountGroup(main);
      main.clear();

      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public String getFilterValue() {
    return filterValue;
  }

  public void setFilterValue(String filterValue) {
    this.filterValue = filterValue;
  }

  public List<AccountGroup> getAccountGroupList() {
    return accountGroupList;
  }

  public boolean isEditable() {
    return editable && getFilterJournal().getEntityId() == null && !getFilterJournal().isOpeningEntry() && (getFilterJournal().isJournal() || getFilterJournal().isContra() || getFilterJournal().isDebitNote() || getFilterJournal().isCreditNote() || getFilterJournal().isExpenses());
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public void showPdf() {
    try {
      Jsf.popupForm(FileConstant.PRINT_CHEQUE_RECEIPT, getFilterJournal());
    } catch (Exception ex) {
      Logger.getLogger(VendorClaimView.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  //FIXME proper handling of pdf documents
  public void printReceipt(MainView main) {
    Document document = null;
    try {
      if (partyDetail == null) {
        setSettlementValue(main);
      }

      FacesContext fc = FacesContext.getCurrentInstance();
      HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
      String sub_file = "RCP_";
      sub_file += partyDetail.getDetailItem().get(0).getDocumentTypeId().getId().intValue() == AccountingConstant.DOC_TYPE_RECEIPT_CHEQUE.getId() ? "CHEQUE" : "CASH";
      String fileName = sub_file + "_" + partyDetail.getAccountingTransactionId().getDocumentNumber() + ".pdf";
      response.setContentType("application/pdf");
      response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

      PdfWriter pdfWriter;
      Rectangle pageSize = new Rectangle(595, 842);
      document = new Document(pageSize, 20, 20, 20, 20);
      pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
      document.open();

      PrintchequeReceipt printReceipt = new PrintchequeReceipt();
      printReceipt.setPartyDetail(partyDetail);
      printReceipt.setCompany(getFilterJournal().getCompanyId());
      printReceipt.setCompanyAddress(CompanyAddressService.selectCompanyRegisteredAddress(main, partyDetail.getAccountingTransactionId().getCompanyId()));
      printReceipt.setCompanyLicenseList(CompanyLicenseService.licenseListByCompany(main, partyDetail.getAccountingTransactionId().getCompanyId()));
      printReceipt.setCompanySettings(CompanySettingsService.selectByCompany(main.em(), partyDetail.getAccountingTransactionId().getCompanyId()));

      document.add(printReceipt.initiatePrint(main));
      printReceipt.createFooterBar(document, pdfWriter);
      document.close();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }
//
//  public void confirmAndAdd(MainView main) {
//    try {
//      save(main, false);
//      cloneTransaction(main);
//
////      showBalanceAndAccountGroup(main, tempAccountTransactionDetail);
//    } catch (Throwable t) {
//      main.rollback(t, "error.save");
//    } finally {
//      main.close();
//    }
//  }

  public String getPartyLedger() {
    if (partyDetail != null) {
      if (partyDetail.getAccountingLedgerId() != null) {
        return partyDetail.getAccountingLedgerId().getTitle();
      }
    }
    return "";
  }

  public void print() {
    Jsf.popupForm("/accounting/print_journal.xhtml", getFilterJournal());
  }

  public void printJournal(MainView main) {
    try {
      if (partyDetail == null) {
        setSettlementValue(main);
      }
      // String sub_file = "RCP_";
      // sub_file += partyDetail.getDetailItem().get(0).getDocumentTypeId().getId().intValue() == AccountingConstant.DOC_TYPE_RECEIPT_CHEQUE.getId() ? "CHEQUE" : "CASH";
      String fileName = filterJournal.getVoucherTypeId().getTitle() + "_" + partyDetail.getAccountingTransactionId().getDocumentNumber() + ".pdf";
      new PrintJournal().initiatePrint(main, fileName, filterJournal);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }
}
