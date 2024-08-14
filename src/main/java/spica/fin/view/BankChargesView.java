/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.fin.view;

import spica.fin.common.BankReconciliation;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.fin.domain.AccountingLedger;
import spica.fin.service.AccountingLedgerService;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingBankCharges;
import spica.fin.service.AccountingBankChargesService;
import spica.scm.util.MathUtil;

/**
 *
 * @author sanith
 */
@Named(value = "bankChargesView")
@ViewScoped
public class BankChargesView implements Serializable {

  private transient List<BankReconciliation> bankReconciliationList;
  private transient Integer reconciliationOptions;
  private transient BankReconciliation bankReconciliationSelected;
  private transient AccountingLedger bankchargeLedger;
  private transient AccountingLedger selectedLedger;
  @Inject
  private AccountingMainView accountingMainView;

  @PostConstruct
  public void init() {
    selectedLedger = (AccountingLedger) Jsf.popupParentValue(AccountingLedger.class);
    reconciliationOptions = AccountingConstant.BANK_CHEQUE_ISSUED;
  }

  public void switchBankCharges(MainView main, String viewType) {
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
        bankReconciliationList = AccountingBankChargesService.getBankReconciliationList(main, getReconciliationOptions(), accountingMainView, selectedLedger);
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
      AccountingBankChargesService.insert(main, bankchargeLedger, selectedLedger, bankReconciliationList);
      setBankReconciliationList(null);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public void calculatetax(BankReconciliation bankReconciliation) {
    double bankcharge = bankReconciliation.getBankChargeAmount();
    if (bankchargeLedger.getCgstId() != null && bankchargeLedger.getSgstId() != null) {
      bankReconciliation.setIgstId(bankchargeLedger.getIgstId());
      bankReconciliation.setCgstId(bankchargeLedger.getCgstId());
      bankReconciliation.setSgstId(bankchargeLedger.getSgstId());
      double sgst = MathUtil.roundOff(bankcharge * bankchargeLedger.getSgstId().getRatePercentage() / 100, 2);
      double cgst = MathUtil.roundOff(bankcharge * bankchargeLedger.getCgstId().getRatePercentage() / 100, 2);
      bankReconciliation.setSgstAmount(sgst);
      bankReconciliation.setCgstAmount(cgst);
      bankReconciliation.setIgstAmount(cgst + sgst);
      bankReconciliation.setTotalAmount(bankcharge + (cgst + sgst));
    } else if (bankchargeLedger.getIgstId() != null) {
      double igst = MathUtil.roundOff(bankcharge * bankchargeLedger.getIgstId().getRatePercentage() / 100, 2);
      bankReconciliation.setTotalAmount(bankcharge + (igst));
    } else {
      bankReconciliation.setTotalAmount(bankReconciliation.getBankChargeAmount());
    }
  }

  public void filterWithStatus(ValueChangeEvent event) {
    if ((event.getOldValue() == null || event.getNewValue() == null) || (!event.getNewValue().equals(event.getOldValue()))) {
      bankReconciliationList = null;
    }
  }

  public void dialogClose() {
    Jsf.closePopup(null);
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

  public BankReconciliation getBankReconciliationSelected() {
    if (bankReconciliationSelected == null) {
      bankReconciliationSelected = new BankReconciliation();
    }
    return bankReconciliationSelected;
  }

  public void setBankReconciliationSelected(BankReconciliation bankReconciliationSelected) {
    this.bankReconciliationSelected = bankReconciliationSelected;
  }

  public AccountingLedger getBankchargeLedger() {
    return bankchargeLedger;
  }

  public void setBankchargeLedger(AccountingLedger bankchargeLedger) {
    this.bankchargeLedger = bankchargeLedger;
  }

  public void backOrNext(int pos) {
    accountingMainView.setFromDate(wawo.entity.util.DateUtil.moveMonths(accountingMainView.getFromDate(), pos));
    bankReconciliationList = null;
  }

  public void onFromDateSelect(SelectEvent event) {
    accountingMainView.setFromDate((Date) event.getObject());
    setBankReconciliationList(null);
  }

  public void onToDateSelect(SelectEvent event) {
    accountingMainView.setToDate((Date) event.getObject());
    setBankReconciliationList(null);
  }

  public void bankChargeEvent(BankReconciliation bankReconciliation) {
    if (bankReconciliation != null) {
      if (bankReconciliation.getBankChargeAmount() != null) {
        calculatetax(bankReconciliation);
      } else {
        bankReconciliation.setIgstAmount(null);
        bankReconciliation.setCgstAmount(null);
        bankReconciliation.setSgstAmount(null);
      }
    }
  }

  public void actionDeleteBankCharge(MainView main, BankReconciliation bankReconciliation) {
    try {
      AccountingBankChargesService.deleteByPk(main, new AccountingBankCharges(bankReconciliation.getBankChargeId()));
      bankReconciliation.setBankChargeAmount(null);
      bankReconciliation.setIgstAmount(null);
      bankReconciliation.setCgstAmount(null);
      bankReconciliation.setSgstAmount(null);
      bankReconciliation.setBankChargeId(null);
      bankReconciliation.setTotalAmount(null);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public AccountingLedger getSelectedLedger() {
    return selectedLedger;
  }

}
