/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.fin.common.TrialBalanceReport;
import spica.fin.domain.AccountingGroup;
import spica.fin.service.AccountingGroupService;
import spica.fin.service.TrialBalanceReportService;
import spica.scm.domain.Company;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
@Named(value = "trialBalanceReportView")
@ViewScoped
public class TrialBalanceReportView implements Serializable {

  private TrialBalanceReport trialBalanceReport;
  private List<TrialBalanceReport> trialBalanceReportList;
  private List<TrialBalanceReport> trialBalanceList;
  private List<TrialBalanceReport> ledgerOutstandingList;
  private Company company;
  private transient Double debit, credit, opBalance, totBalance;
  private transient String type;
  private List<AccountingGroup> accountingGroupList;
  private transient AccountingGroup selectedAccountingGroup;
  private boolean grouping = true;
  @Inject
  private AccountingMainView accountingMainView;
  private AccountingGroup accountingGroup;

  @PostConstruct
  public void init() {
    trialBalanceReport = (TrialBalanceReport) Jsf.popupParentValue(TrialBalanceReport.class);
  }

  public void reset() {
    trialBalanceReport = null;
    trialBalanceReportList = null;
    company = null;
    setSelectedAccountingGroup(null);
    grouping = true;
  }

  private void loadTrialBalanceList(MainView main) {
    try {
      if (trialBalanceReportList == null) {
        trialBalanceReportList = TrialBalanceReportService.loadTrialBalance(main, UserRuntimeView.instance().getCompany().getId(), accountingMainView, getType(), selectedAccountingGroup);
        if (trialBalanceReportList != null) {
          debit = 0.0;
          credit = 0.0;
          opBalance = 0.0;
          totBalance = 0.0;
          for (TrialBalanceReport report : trialBalanceReportList) {
            Double tmpDebit = 0.00, tmpCredit = 0.00, sub = 0.00, op = 0.00;
            debit += report.getDebit() == null ? 0.0 : report.getDebit();
            credit += report.getCredit() == null ? 0.0 : report.getCredit();
            op = report.getOpeningBalance() == null ? 0.0 : report.getOpeningBalance();;
            opBalance += op;
            tmpDebit = report.getDebit() == null ? 0.00 : report.getDebit();
            tmpCredit = report.getCredit() == null ? 0.00 : report.getCredit();
            sub = round(tmpDebit - tmpCredit, 3);
            report.setBalance(op + sub);
            totBalance += report.getBalance();
            if (grouping && getType().equals(SystemConstants.ALL)) {
              AccountingGroup ag = new AccountingGroup();
              ag.setId(report.getLedgerGrpId());
              ag.setTitle(report.getLedgerGroup());
              if (!getAccountingGroupList().contains(ag)) {
                accountingGroupList.add(ag);
              }
            }
          }
          grouping = false;//
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

//  public Integer getCompanyId() {
//    if (company == null) {
//      company = UserRuntimeView.instance().getCompany();
//    }
//    return company.getId();
//  }
  public void backOrNext(int pos, Company company, boolean from) {
    if (accountingMainView.backOrNext(pos, company.getCurrentFinancialYear(), from)) {
      reset();
    }
  }

  public TrialBalanceReport getTrialBalanceReport() {
    return trialBalanceReport;
  }

  public List<TrialBalanceReport> getTrialBalanceReportList(MainView main) {
    loadTrialBalanceList(main);
    return trialBalanceReportList;
  }

  public Double getDebit() {
    return debit;
  }

  public Double getCredit() {
    return credit;
  }

  public Double getOpBalance() {
    return opBalance;
  }

  public Double getTotBalance() {
    return totBalance;
  }

  public String getType() {
    if (type == null) {
      type = SystemConstants.BY_GROUP;
    }
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }
    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
  }

  public void showGroupDetail(Integer groupId, MainView main) {
    trialBalanceList = null;
    Jsf.popupList(FileConstant.TRIAL_BALANCE, new TrialBalanceReport(groupId));

  }

  private void loadGroupDetail(MainView main) {
    try {
      if (trialBalanceList == null) {
        trialBalanceList = TrialBalanceReportService.loadAllTrialBalanceByGroupId(main, UserRuntimeView.instance().getCompany().getId(), getTrialBalanceReport().getLedgerGrpId(), accountingMainView);
        if (trialBalanceList != null) {
          debit = 0.0;
          credit = 0.0;
          opBalance = 0.0;
          totBalance = 0.0;
          for (TrialBalanceReport report : trialBalanceList) {
            Double tmpDebit = 0.00, tmpCredit = 0.00, sub = 0.00, op = 0.00;
            debit += report.getDebit() == null ? 0.0 : report.getDebit();
            credit += report.getCredit() == null ? 0.0 : report.getCredit();
            op = report.getOpeningBalance() == null ? 0.0 : report.getOpeningBalance();;
            opBalance += op;
            tmpDebit = report.getDebit() == null ? 0.00 : report.getDebit();
            tmpCredit = report.getCredit() == null ? 0.00 : report.getCredit();
            sub = round(tmpDebit - tmpCredit, 3);
            report.setBalance(op + sub);
            totBalance += report.getBalance();
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<TrialBalanceReport> getTrialBalanceList() {
    if (trialBalanceList == null || trialBalanceList.size() == 0) {
      MainView main = Jsf.getMain();
      try {
        loadGroupDetail(main);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return trialBalanceList;
  }

  public void onFromDateSelect(SelectEvent event) {
    accountingMainView.setFromDate((Date) event.getObject());
    reset();
  }

  public void onToDateSelect(SelectEvent event) {
    accountingMainView.setToDate((Date) event.getObject());
    reset();
  }

  public void showLedgerOutstanding(MainView main) {
    Jsf.popupList(FileConstant.LEDGER_OUTSTANDING, new TrialBalanceReport());
  }

  public List<TrialBalanceReport> loadLedgerOutstanding(MainView main) {
    try {
      if (getLedgerOutstandingList() == null) {
        ledgerOutstandingList = TrialBalanceReportService.loadLedgerOutstanding(main, UserRuntimeView.instance().getCompany().getId(), getAccountingGroup());
        for (TrialBalanceReport ledger : ledgerOutstandingList) {
          if (ledger.getOutstanding() != null) {
            ledger.setBalance(Math.abs(ledger.getLedgerBalance()) - ledger.getOutstanding());
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return ledgerOutstandingList;
  }

  public List<TrialBalanceReport> getLedgerOutstandingList() {
    return ledgerOutstandingList;
  }

  public void setLedgerOutstandingList(List<TrialBalanceReport> ledgerOutstandingList) {
    this.ledgerOutstandingList = ledgerOutstandingList;
  }

  public List<AccountingGroup> getAccountingGroupList() {
    if (accountingGroupList == null) {
      accountingGroupList = new ArrayList<>();
    }
    return accountingGroupList;
  }

  public AccountingGroup getSelectedAccountingGroup() {
    return selectedAccountingGroup;
  }

  public void setSelectedAccountingGroup(AccountingGroup selectedAccountingGroup) {
    this.selectedAccountingGroup = selectedAccountingGroup;
  }

  public void setTrialBalanceReportList(List<TrialBalanceReport> trialBalanceReportList) {
    this.trialBalanceReportList = trialBalanceReportList;
  }

  public AccountingGroup getAccountingGroup() {
    return accountingGroup;
  }

  public void setAccountingGroup(AccountingGroup accountingGroup) {
    this.accountingGroup = accountingGroup;
  }

  public List<AccountingGroup> accountingGroupAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return AccountingGroupService.accountingGroupAutoAll(main, filter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }
}
