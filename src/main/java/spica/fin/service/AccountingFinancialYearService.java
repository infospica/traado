/*
 * @(#)AccountingFinancialYearService.java	1.0 Fri Apr 03 18:00:17 IST 2020 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import spica.sys.SystemRuntimeConfig;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;

/**
 * AccountingFinancialYearService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 03 18:00:17 IST 2020
 */
public abstract class AccountingFinancialYearService {

  private static SqlPage getFinancialYearSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_financial_year", AccountingFinancialYear.class, main);
    sql.main("select fin_accounting_financial_year.id,fin_accounting_financial_year.title,fin_accounting_financial_year.description,"
            + "fin_accounting_financial_year.start_date,fin_accounting_financial_year.end_date,fin_accounting_financial_year.country_id\n"
            + " from fin_accounting_financial_year"); //Main query  
    sql.count("select count(fin_accounting_financial_year.id) from fin_accounting_financial_year fin_accounting_financial_year "); //Count query
    sql.string(new String[]{"fin_accounting_financial_year.description", "fin_accounting_financial_year.title"}); //String search or sort fields
    sql.number(new String[]{"fin_accounting_financial_year.id"}); //Numeric search or sort fields
    sql.date(new String[]{"fin_accounting_financial_year.start_date", "fin_accounting_financial_year.end_date"});  //Date search or sort fields
    return sql;
  }

  public static final List<AccountingFinancialYear> listPaged(Main main, Company company) {
    if (company != null && company.getCountryId() != null && company.getFinancialYearStartDate() != null) {
      SqlPage sql = getFinancialYearSqlPaged(main);
      main.clear();
      sql.cond("where fin_accounting_financial_year.id not in(select financial_year_id from fin_company_financial_year where company_id =?) "
              + "and  fin_accounting_financial_year.country_id=? "
              + "and fin_accounting_financial_year.start_date::date>=TO_DATE(?,'yyyy-mm-dd')");
      sql.orderBy("fin_accounting_financial_year.id desc");
      sql.param(company.getId());
      sql.param(company.getCountryId().getId());
      sql.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(company.getFinancialYearStartDate()));

      return AppService.listPagedJpa(main, sql);
    } else {
      return null;
    }
  }

  /**
   * Select AccountingFinancialYear by key.
   *
   * @param main
   * @param accountingFinancialYear
   * @return AccountingFinancialYear
   */
  public static final AccountingFinancialYear selectByPk(Main main, AccountingFinancialYear accountingFinancialYear) {
    return (AccountingFinancialYear) AppService.find(main, AccountingFinancialYear.class, accountingFinancialYear.getId());
  }

  /**
   * Insert AccountingFinancialYear.
   *
   * @param main
   * @param accountingFinancialYear
   */
  public static final void insert(Main main, AccountingFinancialYear accountingFinancialYear) {
    AppService.insert(main, accountingFinancialYear);
  }

  /**
   * Update AccountingFinancialYear by key.
   *
   * @param main
   * @param accountingFinancialYear
   * @return AccountingFinancialYear
   */
  public static final AccountingFinancialYear updateByPk(Main main, AccountingFinancialYear accountingFinancialYear) {
    return (AccountingFinancialYear) AppService.update(main, accountingFinancialYear);
  }

  /**
   * Insert or update AccountingFinancialYear
   *
   * @param main
   * @param accountingFinancialYear
   */
  public static void insertOrUpdate(Main main, AccountingFinancialYear accountingFinancialYear) {
    if (accountingFinancialYear.getId() == null) {
      insert(main, accountingFinancialYear);
    } else {
      updateByPk(main, accountingFinancialYear);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingFinancialYear
   */
  public static void clone(Main main, AccountingFinancialYear accountingFinancialYear) {
    accountingFinancialYear.setId(null); //Set to null for insert
    insert(main, accountingFinancialYear);
  }

  /**
   * Delete AccountingFinancialYear.
   *
   * @param main
   * @param accountingFinancialYear
   */
  public static final void deleteByPk(Main main, AccountingFinancialYear accountingFinancialYear) {
    AppService.delete(main, AccountingFinancialYear.class, accountingFinancialYear.getId());
  }

  /**
   * Delete Array of AccountingFinancialYear.
   *
   * @param main
   * @param accountingFinancialYear
   */
  public static final void deleteByPkArray(Main main, AccountingFinancialYear[] accountingFinancialYear) {
    for (AccountingFinancialYear e : accountingFinancialYear) {
      deleteByPk(main, e);
    }
  }

}
