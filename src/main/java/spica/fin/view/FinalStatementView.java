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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.constant.AccountingConstant;
import spica.fin.common.FinalStatement;
import spica.fin.service.FinalStatementService;
import spica.sys.UserRuntimeView;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.export.ExcelSheet;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeService;

import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sanith
 */
@Named(value = "tradingaccountView")
@ViewScoped
public class FinalStatementView implements Serializable {

  private transient List<FinalStatement> tradingaccountExpenseList;
  private transient List<FinalStatement> tradingaccountIncomeList;

  private Double tradingLeftTotal;
  private Double tradingRightTotal;
  private Double profitAndLoss;
  private Account account;
  private List<AccountGroup> accountGroupList;
  private List<Account> accountList;

  @Inject
  private AccountingMainView accountingMainView;

  public void switchTrading(MainView main, String viewType) {
    try {
      accountingMainView.init(main, UserRuntimeView.instance().getCompany());
      if (StringUtil.isEmpty(tradingaccountExpenseList)) {
        main.getPageData().setPageSize(AccountingConstant.PAGE_SIZE * 2);
      }
      reset(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }
//  
//  public void switchProftAndLoss(MainView main, String viewType) {
//    reset();
//  }

  private void selectTradingLeftList(MainView main) {
    if (StringUtil.isEmpty(tradingaccountExpenseList)) {
      tradingaccountExpenseList = FinalStatementService.getPuchaseAccountlist(main, UserRuntimeView.instance().getCompany(), accountingMainView);
      tradingLeftTotal = getTradingList(tradingaccountExpenseList);
    }
  }

  public void setTradingaccountExpenseList(List<FinalStatement> tradingaccountExpenseList) {
    this.tradingaccountExpenseList = tradingaccountExpenseList;
  }

  private void selectTradingRightList(MainView main) {
    if (StringUtil.isEmpty(tradingaccountIncomeList)) {
      tradingaccountIncomeList = FinalStatementService.getSalesAccountlist(main, UserRuntimeView.instance().getCompany(), accountingMainView);
      tradingRightTotal = getTradingList(tradingaccountIncomeList);
      setTradingProfitAndLoss(main);
    }
  }

  private Double getTradingList(List<FinalStatement> list) {
    Double subTotal = 0.0;
    Double total = 0.0;
    FinalStatement pre = null;
    for (FinalStatement t : list) {
      if (StringUtil.neDouble(t.getTotalAmount(), 0.00)) {
        if ("dr".equals(t.getTrtype())) {
          subTotal += t.getTotalAmount();
          pre = t;
        }
        total += t.getTotalAmount();
      }
    }
    if (pre != null) {
      pre.setDebit(subTotal);
    }
    return total;
  }

  private transient FinalStatement closingStock;

  private void setTradingProfitAndLoss(MainView main) {
    //AccountingTradingAccount atc = new FinalStatement();
    Double openingBalance = FinalStatementService.getOpeningBalance(main, UserRuntimeView.instance().getCompany(), accountingMainView);
    FinalStatement fs = new FinalStatement();
    fs.setTrtype("exp");
    fs.setTotalAmount(openingBalance);
    fs.setTitle("Opening Stock");
    Double openingDamageExpiry = FinalStatementService.getOpeningDamageAndExpiry(main, UserRuntimeView.instance().getCompany().getId(), accountingMainView);
    getTradingaccountExpenseList().add(0, new FinalStatement("dr", openingDamageExpiry, null, "Opening Stock Damage & Expiry "));
    getTradingaccountExpenseList().add(1, new FinalStatement("dr", openingBalance, openingBalance + openingDamageExpiry, "Opening Stock "));

    tradingLeftTotal = tradingLeftTotal + openingBalance + openingDamageExpiry;

    Double closingBalance = FinalStatementService.getClosingBalance(main, UserRuntimeView.instance().getCompany(), accountingMainView);
    Double closingDamageExpiry = FinalStatementService.getClosingDamageAndExpiry(main, UserRuntimeView.instance().getCompany().getId(), accountingMainView);
    getTradingaccountIncomeList().add(new FinalStatement("dr", closingDamageExpiry, null, "Closing Stock Damage & Expiry"));
    getTradingaccountIncomeList().add(new FinalStatement("dr", closingBalance, closingBalance + closingDamageExpiry, "Closing Stock"));

    tradingRightTotal = tradingRightTotal + closingBalance + closingDamageExpiry;

    profitAndLoss = tradingRightTotal - tradingLeftTotal;

    fs = new FinalStatement();
    fs.setTrtype("exp");
    if (profitAndLoss > 0) {
      fs.setTotalAmount((profitAndLoss));
      fs.setTitle("Gross Profit (transferred to P&L)");
      getTradingaccountExpenseList().add(fs);

    } else {
      fs.setTotalAmount(profitAndLoss * -1);
      fs.setTitle("Gross Loss (transferred to P&L)");
      getTradingaccountIncomeList().add(fs);
    }
  }

  public void onFromDateSelect(SelectEvent event) {
    accountingMainView.setFromDate((Date) event.getObject());
    MainView main = Jsf.getMain();
    try {
      reset(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void onToDateSelect(SelectEvent event) {
    accountingMainView.setToDate((Date) event.getObject());
    MainView main = Jsf.getMain();
    try {
      reset(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<FinalStatement> getTradingaccountExpenseList() {
    if (tradingaccountExpenseList == null) {
      tradingaccountExpenseList = new ArrayList<>();
    }
    return tradingaccountExpenseList;
  }

  public List<FinalStatement> getTradingaccountIncomeList() {
    if (tradingaccountIncomeList == null) {
      tradingaccountIncomeList = new ArrayList<>();
    }
    return tradingaccountIncomeList;
  }

  public void setTradingaccountIncomeList(List<FinalStatement> tradingaccountIncomeList) {
    this.tradingaccountIncomeList = tradingaccountIncomeList;
  }

  public Double getTradingLeftTotal() {
    return tradingLeftTotal;
  }

  public void setTradingLeftTotal(Double tradingLeftTotal) {
    this.tradingLeftTotal = tradingLeftTotal;
  }

  public Double getTradingRightTotal() {
    return tradingRightTotal;
  }

  public void setTradingRightTotal(Double tradingRightTotal) {
    this.tradingRightTotal = tradingRightTotal;
  }

  public Double getProfitAndLoss() {
    return profitAndLoss;
  }

  public void setProfitAndLoss(Double profitAndLoss) {
    this.profitAndLoss = profitAndLoss;
  }

  //For P&L account
  private transient List<FinalStatement> pandlaccountExpenseList;
  private transient List<FinalStatement> pandlaccountIncomeList;
  private transient Double plIncome;
  private transient Double plExpense;
  private transient Double netProfit;

  private void selectPandLccountExpenseList(MainView main) {
    if (StringUtil.isEmpty(pandlaccountExpenseList)) {
      plExpense = 0.0;
      pandlaccountExpenseList = FinalStatementService.getPandLaccountExpenseList(main, UserRuntimeView.instance().getCompany(), accountingMainView);
      for (FinalStatement ex : pandlaccountExpenseList) {
        if (ex.getTotalAmount() != null) {
          plExpense += ex.getTotalAmount();
        }
      }
    }
  }

  private void selectPandLccountIncomeList(MainView main) {
    if (StringUtil.isEmpty(pandlaccountIncomeList)) {
      plIncome = 0.0;
      pandlaccountIncomeList = FinalStatementService.getPandLaccountIncomeList(main, UserRuntimeView.instance().getCompany(), accountingMainView);
      for (FinalStatement inc : pandlaccountIncomeList) {
        if (inc.getTotalAmount() != null) {
          plIncome += inc.getTotalAmount();
        }
      }
      calculateNetProfitAndLoss(main);
    }
  }

  private void calculateNetProfitAndLoss(MainView main) {
    FinalStatement pl = new FinalStatement();
    pl.setTrtype("exp");
    if (getProfitAndLoss() > 0) {
      pl.setTitle("Gross Profit b/d ");
      pl.setTotalAmount(getProfitAndLoss());
      getPandlaccountIncomeList().add(0, pl);
      plIncome += getProfitAndLoss();
    } else {
      pl.setTitle("Gross Loss b/d ");
      pl.setTotalAmount(getProfitAndLoss() == 0 ? 0 : getProfitAndLoss() * -1);
      plExpense += (getProfitAndLoss() * -1);
      getPandlaccountExpenseList().add(0, pl);
    }
    netProfit = plIncome - plExpense;
    pl = new FinalStatement();
    pl.setTrtype("exp");
    if (netProfit > 0) {
      pl.setTitle("Net Profit (Transferred to Capital A/c)");
      pl.setTotalAmount(netProfit);
      getPandlaccountExpenseList().add(pl);
    } else {
      pl.setTitle("Net Loss (Transferred to Capital A/c)");
      pl.setTotalAmount(netProfit == 0 ? 0 : netProfit * -1);
      getPandlaccountIncomeList().add(pl);
    }
  }

  private void reset(MainView main) {
    resetList();
    accountingMainView.setSelectedAccount(null);
    accountingMainView.setSelectedAccountGroup(null);
    accountList = null;
    accountGroupList = null;
    getAccountGroupList();
    getAccountList();
    fetchTradingDetails(main);
  }

  public Double getPlIncome() {
    return plIncome;
  }

  public void setPlIncome(Double salesPL) {
    this.plIncome = salesPL;
  }

  public Double getPlExpense() {
    return plExpense;
  }

  public void setPlExpense(Double purchasePL) {
    this.plExpense = purchasePL;
  }

  public List<FinalStatement> getPandlaccountExpenseList() {
    if (pandlaccountExpenseList == null) {
      pandlaccountExpenseList = new ArrayList<>();
    }
    return pandlaccountExpenseList;
  }

  public void setPandlaccountExpenseList(List<FinalStatement> pandlaccountExpenseList) {
    this.pandlaccountExpenseList = pandlaccountExpenseList;
  }

  public List<FinalStatement> getPandlaccountIncomeList() {
    if (pandlaccountIncomeList == null) {
      pandlaccountIncomeList = new ArrayList<>();
    }
    return pandlaccountIncomeList;
  }

  public void setPandlaccountIncomeList(List<FinalStatement> pandlaccountIncomeList) {
    this.pandlaccountIncomeList = pandlaccountIncomeList;
  }

  //For balance sheet
  private transient List<FinalStatement> balanceSheetAssetList;
  private transient List<FinalStatement> balanceSheetLiabilityList;

  private void calculateBalanceSheet() {
    FinalStatement pl = new FinalStatement();
    pl.setTrtype("lia");
    if (netProfit > 0) {
      pl.setTitle("Add + Net Profit");
      pl.setTotalAmount(netProfit);
      getBalanceSheetLiabilityList().add(pl);
    } else {
      pl.setTitle("Less - Net Loss");
      pl.setTotalAmount(netProfit);
      //  pl.setTotalAmount(netProfit == 0 ? 0 : netProfit * -1);
      getBalanceSheetLiabilityList().add(pl);
    }
  }

  private List<FinalStatement> selectBalanceSheetAssetList(MainView main) {
    if (StringUtil.isEmpty(balanceSheetAssetList)) {
      balanceSheetAssetList = FinalStatementService.getBalanceSheetAssets(main, UserRuntimeView.instance().getCompany(), accountingMainView);
      //Adding closing stock to last under current asset
      int i = 0;
      FinalStatement currentAsset = null;
      for (FinalStatement f : balanceSheetAssetList) {
        if (f.getParentId() != null && AccountingConstant.GROUP_CURRENT_ASSET.getId() == f.getParentId().intValue()) {
          currentAsset = f;
          break;
        }
        i++;
      }
      if (currentAsset != null) {
        closingStock.setHeadTitle(currentAsset.getHeadTitle());
        closingStock.setParentId(currentAsset.getParentId());
        getBalanceSheetAssetList().add(i + 1, closingStock);
      }
    }
    return balanceSheetAssetList;
  }

  private List<FinalStatement> selectBalanceSheetLiabilityList(MainView main) {
    if (StringUtil.isEmpty(balanceSheetLiabilityList)) {
      balanceSheetLiabilityList = FinalStatementService.getBalanceSheetLiability(main, UserRuntimeView.instance().getCompany(), accountingMainView);
      for (FinalStatement f : balanceSheetLiabilityList) {
        if (String.valueOf(f.getTotalAmount()).charAt(0) == '-') {
          f.setTotalAmount(f.getTotalAmount() * -1); //To remove negative
        }
      }
      calculateBalanceSheet();
    }
    return balanceSheetLiabilityList;
  }

  public void setBalanceSheetAssetList(List<FinalStatement> balanceSheetAssetList) {
    this.balanceSheetAssetList = balanceSheetAssetList;
  }

  public List<FinalStatement> getBalanceSheetAssetList() {
    return balanceSheetAssetList;
  }

  public List<FinalStatement> getBalanceSheetLiabilityList() {
    return balanceSheetLiabilityList;
  }

  public void setBalanceSheetLiabilityList(List<FinalStatement> balanceSheetLiabilityList) {
    this.balanceSheetLiabilityList = balanceSheetLiabilityList;
  }

  public void export(MainView main) {
    try {
      String period = "(" + SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(accountingMainView.getFromDate()) + " - " + SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(accountingMainView.getToDate()) + ")";
      ExcelSheet.createProfitAndLostReport(getTradingaccountExpenseList(), getTradingaccountIncomeList(), getPandlaccountExpenseList(),
              getPandlaccountIncomeList(), UserRuntimeView.instance().getCompany(), period, main);
    } catch (Throwable t) {
      main.error(t, "error.export", main.getAppUser().isRoot() ? new String[]{t.getMessage()} : null);
    } finally {
      main.close();
    }
  }

  public AccountingMainView getAccountingMainView() {
    return accountingMainView;
  }

  public void setAccountingMainView(AccountingMainView accountingMainView) {
    this.accountingMainView = accountingMainView;
  }

  public Account getAccount() {
    if (null == account) {
      account = new Account();
    }
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public List<AccountGroup> getAccountGroupList() {
    if (null == accountGroupList) {
      accountGroupList = new ArrayList<>();
    }
    if (StringUtil.isEmpty(accountGroupList)) {
      MainView main = Jsf.getMain();
      try {
        accountGroupList = UserRuntimeService.accountGroupAutoAll(main, null, UserRuntimeView.instance().getCompany().getId(), UserRuntimeView.instance().getAppUser());
      } catch (Throwable t) {
        main.error(t, "error.select", main.getAppUser().isRoot() ? new String[]{t.getMessage()} : null);
      } finally {
        main.close();
      }

    }
    return accountGroupList;
  }

  public List<Account> getAccountList() {
    if (null == accountList) {
      accountList = new ArrayList<>();
    }
    if (StringUtil.isEmpty(accountList)) {
      MainView main = Jsf.getMain();
      try {
        accountList = UserRuntimeService.accountAutoAll(main, "", UserRuntimeView.instance().getCompany().getId(), UserRuntimeView.instance().getAppUser());
      } catch (Throwable t) {
        main.error(t, "error.select", main.getAppUser().isRoot() ? new String[]{t.getMessage()} : null);
      } finally {
        main.close();
      }
    }
    return accountList;
  }

  private void fetchTradingDetails(MainView main) {
    selectTradingLeftList(main);
    selectTradingRightList(main);
    if (main.getViewPath().endsWith("pandl_account") || main.getViewPath().endsWith("balance_sheet")) {
      selectPandLccountExpenseList(main);
      selectPandLccountIncomeList(main);
    }
    if (main.getViewPath().endsWith("balance_sheet")) {
      selectBalanceSheetAssetList(main);
      selectBalanceSheetLiabilityList(main);
    }
  }

  public void selectAccountGroup(MainView main) {
    try {
      resetList();
      fetchTradingDetails(main);
    } catch (Throwable t) {
      main.error(t, "error.select", main.getAppUser().isRoot() ? new String[]{t.getMessage()} : null);
    } finally {
      main.close();
    }
  }

  public void selectAccount(MainView main) {
    try {
      resetList();
      fetchTradingDetails(main);
      if (accountingMainView.getSelectedAccount().getId() != null) {
        accountGroupList = UserRuntimeService.accountGroupByAccountAll(main, accountingMainView.getSelectedAccount());
      } else {
        accountGroupList = null;
        getAccountList();
      }
    } catch (Throwable t) {
      main.error(t, "error.select", main.getAppUser().isRoot() ? new String[]{t.getMessage()} : null);
    } finally {
      main.close();
    }
  }

  private void resetList() {
    setTradingaccountExpenseList(null);
    setTradingaccountIncomeList(null);
    setPandlaccountExpenseList(null);
    setPandlaccountIncomeList(null);
    setBalanceSheetAssetList(null);
    setBalanceSheetLiabilityList(null);
  }
}
