/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.fin.domain.AccountingChequeBounce;
import spica.fin.domain.AccountingLedger;
import spica.fin.service.AccountingChequeBounceService;
import spica.fin.service.AccountingLedgerService;
import spica.fin.service.BankReconciliationService;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;
import spica.constant.AccountingConstant;
import spica.fin.common.BankReconciliation;
import spica.fin.service.AccountingLedgerTransactionService;
import static spica.fin.service.BankReconciliationService.selectSumOfDebitCreditReconcilation;

/**
 *
 * @author sanith
 */
@Named(value = "bankReconciliationView")
@ViewScoped
public class BankReconciliationView implements Serializable {
  
  private transient List<BankReconciliation> bankReconciliationList;
  private transient Integer reconciliationOptions;
  private transient Date selectedDate;
  private transient BankReconciliation bankReconciliationSelected;
  private transient AccountingLedger bankchargeLedger;
  private transient AccountingChequeBounce chequeBounce;
  private transient AccountingLedger selectedLedger;
  private transient Double debit;
  private transient Double credit;
  @Inject
  private AccountingMainView accountingMainView;
  
  @PostConstruct
  public void init() {
    selectedLedger = (AccountingLedger) Jsf.popupParentValue(AccountingLedger.class);
    getChequeBounce().setAccountingLedgerId(selectedLedger);
    reconciliationOptions = AccountingConstant.BANK_CHEQUE_ISSUED;
  }
  
  public void switchBankReconciliation(MainView main, String viewType) {
    try {
      bankchargeLedger = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_BANK_CHARGE, selectedLedger.getCompanyId().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }
  
  public List<BankReconciliation> getBankReconciliationList(MainView main) {
    if (StringUtil.isEmpty(bankReconciliationList)) {
      try {
        bankReconciliationList = BankReconciliationService.getBankReconciliationList(main, getReconciliationOptions(), accountingMainView, selectedLedger);
        debit = selectSumOfDebitCreditReconcilation(main, bankchargeLedger.getCompanyId().getId(), accountingMainView, selectedLedger.getId(), AccountingConstant.IS_DEBIT);
        credit = selectSumOfDebitCreditReconcilation(main, bankchargeLedger.getCompanyId().getId(), accountingMainView, selectedLedger.getId(), AccountingConstant.IS_CREDIT);
        main.getPageData().setTotalRecords(bankReconciliationList != null ? (long) bankReconciliationList.size() : null);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return bankReconciliationList;
  }
  
  public String actionSave(MainView main) {
    try {
      BankReconciliationService.updateBankReconciliation(main, bankReconciliationList);
      setBankReconciliationList(null);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }
  
  public void loadBankReconciliation(MainView main) {
    try {
      // if (getBankReconciliationSelected().getId() != null) {
      AccountingLedger parent = getChequeBounce().getAccountingLedgerId();
      chequeBounce = AccountingChequeBounceService.selectByBankReconciliation(main, bankReconciliationSelected);
      getChequeBounce().setAccountingLedgerId(parent);
      // }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }
  
  public String actionDeleteBounce(MainView main) {
    try {
      BankReconciliationService.deleteBounce(main, getChequeBounce());
      setBankReconciliationList(null);;
      loadBankReconciliation(main);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }
  
  public String actionSaveBounce(MainView main) {
    try {
      AccountingChequeBounceService.insertOrUpdateChequeBounce(main, bankchargeLedger, bankReconciliationSelected, chequeBounce);
      setBankReconciliationList(null);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }
  
  public void calculatetax() {
    if (bankchargeLedger.getCgstId() != null && bankchargeLedger.getSgstId() != null) {
      chequeBounce.setCgstAmount(chequeBounce.getBankCharge() * bankchargeLedger.getCgstId().getRatePercentage() / 100);
      chequeBounce.setSgstAmount(chequeBounce.getBankCharge() * bankchargeLedger.getSgstId().getRatePercentage() / 100);
      chequeBounce.setTotalAmount(chequeBounce.getBankCharge() + chequeBounce.getCgstAmount() + chequeBounce.getSgstAmount());
    } else {
      chequeBounce.setTotalAmount(chequeBounce.getBankCharge());
    }
    chequeBounce.setPenaltyAmount(chequeBounce.getTotalAmount());
  }
  
  public void filterWithStatus(ValueChangeEvent event) {
    bankReconciliationList = null;
//    if ((event.getOldValue() == null || event.getNewValue() == null) || (!event.getNewValue().equals(event.getOldValue()))) {
//      bankReconciliationList = null;
//    }
  }
  
  public void dialogClose() {
    Jsf.closePopup(null);
  }
  
  public void applyAllProcessed() {
    for (BankReconciliation bc : bankReconciliationList) {
      bc.setProcessedAt(bc.getProcessedAt() == null ? selectedDate : bc.getProcessedAt());
    }
    Jsf.info("save.to.apply");
  }

//  public void applyAllDocumentDate() {
//    for (BankReconciliation bc : bankReconciliationList) {
//      bc.setDocumentDate(selectedDate);
//    }
//    Jsf.info("save.to.apply");
//  }
  public void backOrNext(int pos) {
    accountingMainView.setFromDate(wawo.entity.util.DateUtil.moveMonths(accountingMainView.getFromDate(), pos));
    bankReconciliationList = null;
  }
  
  public void setBankReconciliationList(List<BankReconciliation> bankReconciliationList) {
    this.bankReconciliationList = bankReconciliationList;
  }
  
  public Integer getReconciliationOptions() {
    return reconciliationOptions;
  }
  
  public void setReconciliationOptions(Integer reconciliationOptions) {
    this.reconciliationOptions = reconciliationOptions;
  }
  
  public Date getSelectedDate() {
    return selectedDate;
  }
  
  public void setSelectedDate(Date selectedDate) {
    this.selectedDate = selectedDate;
  }
  
  public BankReconciliation getBankReconciliationSelected() {
    if (bankReconciliationSelected == null) {
      bankReconciliationSelected = new BankReconciliation();
    }
    return bankReconciliationSelected;
  }
  
  public void setBankReconciliationSelected(BankReconciliation bankReconciliationSelected) {
    getChequeBounce().setBankCharge(null);
    getChequeBounce().setPenaltyAmount(null);
    this.bankReconciliationSelected = bankReconciliationSelected;
  }
  
  public AccountingChequeBounce getChequeBounce() {
    if (chequeBounce == null) {
      chequeBounce = new AccountingChequeBounce();
    }
    return chequeBounce;
  }
  
  public void setChequeBounce(AccountingChequeBounce chequeBounce) {
    this.chequeBounce = chequeBounce;
  }
  
  public AccountingLedger getBankchargeLedger() {
    return bankchargeLedger;
  }
  
  public void setBankchargeLedger(AccountingLedger bankchargeLedger) {
    this.bankchargeLedger = bankchargeLedger;
  }
  
  public void onFromDateSelect(SelectEvent event) {
    accountingMainView.setFromDate((Date) event.getObject());
    setBankReconciliationList(null);
  }
  
  public void onToDateSelect(SelectEvent event) {
    accountingMainView.setToDate((Date) event.getObject());
    setBankReconciliationList(null);
  }
  
  public void openBankChargesPopup() {
    Jsf.popupForm(AccountingLedgerTransactionService.BANK_CHARGES_POPUP, getBankchargeLedger());
  }
  
  public AccountingLedger getSelectedLedger() {
    return selectedLedger;
  }
  
  public void setSelectedLedger(AccountingLedger selectedLedger) {
    this.selectedLedger = selectedLedger;
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
}
