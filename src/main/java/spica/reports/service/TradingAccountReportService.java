/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import spica.reports.model.TradingAccountReport;
import spica.scm.domain.Account;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author godson
 */
public class TradingAccountReportService {

  public static List<TradingAccountReport> getTradingAccountReportList(Main main, Company company, Account account, AccountingFinancialYear financialYear, Integer reportType) {
    String sql = "select * from getTradingAccountForFY(?,?,?,?,?,?,?,?,?);";
    List<Object> params = new ArrayList<Object>();
    params.add(company.getId());
    params.add(account == null ? null : account.getId());
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(financialYear.getStartDate());
    params.add(calendar.get(Calendar.DAY_OF_MONTH));
    params.add(calendar.get(Calendar.MONTH)+1);
    params.add(calendar.get(Calendar.YEAR));
    calendar.setTime(financialYear.getEndDate());
    params.add(calendar.get(Calendar.DAY_OF_MONTH));
    params.add(calendar.get(Calendar.MONTH)+1);
    params.add(calendar.get(Calendar.YEAR));
    params.add(reportType);
    return AppDb.getList(main.dbConnector(), TradingAccountReport.class, sql, params.toArray());
  }

//  public static List<AccountingFinancialYear> getFinancialYear(Main main, Company company) {
//    String sql = "select * from fin_accounting_financial_year where id in (select financial_year_id from fin_company_financial_year where company_id=?) ";
//    return AppDb.getList(main.dbConnector(), AccountingFinancialYear.class, sql, new Object[]{company.getId()});
//
//  }
}
