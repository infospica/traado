/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import spica.reports.model.TradingAccountReport;
import spica.reports.service.TradingAccountReportService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import spica.sys.UserRuntimeView;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "tradingAccountReportView")
@ViewScoped
public class TradingAccountReportView implements Serializable {

  private List<TradingAccountReport> tradingAccountReportList;
  private transient Company company;
  private transient Account account;
  private transient Integer reportType;
  private transient AccountingFinancialYear financialYear;

  public List<TradingAccountReport> getTradingAccountReportList(MainView main) {
    if (StringUtil.isEmpty(tradingAccountReportList) && !main.hasError()) {
      setFinancialYear(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
      try  {
        if (getCompany() != null && reportType != null) {
          tradingAccountReportList = TradingAccountReportService.getTradingAccountReportList(main, getCompany(), account, financialYear, reportType);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      }finally{
        main.close();
      }
    }
    return tradingAccountReportList;
  }

  public void setTradingAccountReportList(List<TradingAccountReport> tradingAccountReportList) {
    this.tradingAccountReportList = tradingAccountReportList;
  }

  public void reset() {
    reportType = null;
    tradingAccountReportList = null;
    company = null;
  }

//  public List<AccountingFinancialYear> getFinancialYearList() {
//    List<AccountingFinancialYear> financialYearList = null;
//    MainView main = Jsf.getMain();
//    if (getCompany() != null) {
//      financialYearList = TradingAccountReportService.getFinancialYear(main, getCompany());
//    }
//    return financialYearList;
//  }

//  Getters and Setters
  public Company getCompany() {
    if (company == null) {
      company = UserRuntimeView.instance().getCompany();
    }
    return company;

  }

  public AccountingFinancialYear getFinancialYear() {
    return financialYear;
  }

  public void setFinancialYear(AccountingFinancialYear financialYear) {
    this.financialYear = financialYear;
  }

  public Integer getReportType() {
    return reportType;
  }

  public void setReportType(Integer reportType) {
    this.reportType = reportType;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

}
