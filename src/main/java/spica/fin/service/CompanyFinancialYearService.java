/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.fin.service;

import java.util.List;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyFinancialYear;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppEm;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;

/**
 *
 * @author Arun . VC
 */
public abstract class CompanyFinancialYearService {

  public static void insertByCompany(Main main, Company companyId) {
    CompanyFinancialYear cf = new CompanyFinancialYear();
    cf.setIsCurrentFinancialYear(1);
    AccountingFinancialYear fin = (AccountingFinancialYear) AppService.single(main, AccountingFinancialYear.class, "select * from fin_accounting_financial_year where country_id=? and start_date<=current_date and end_date>=current_date ", new Object[]{companyId.getCountryId().getId()});
    if (fin == null) {
      throw new UserMessageException("No Financial Year defined in your country");
    }
    cf.setFinancialYearId(fin);
    companyId.addCompanyFinancialYear(cf);
    insert(main, cf);
  }

  /**
   * CompanyFinancialYear paginated query.
   *
   * @param main
   * @return SqlPage
   */
//  private static SqlPage getCompanyFinancialYearSqlPaged(Main main) {
//    SqlPage sql = AppService.sqlPage("fin_company_financial_year", CompanyFinancialYear.class, main);
//    sql.main("select fin_company_financial_year.id,fin_company_financial_year.company_id,fin_company_financial_year.financial_year_id,fin_company_financial_year.is_current_financial_year from fin_company_financial_year fin_company_financial_year "); //Main query
//    sql.count("select count(fin_company_financial_year.id) as total from fin_company_financial_year fin_company_financial_year "); //Count query
//    sql.join("left outer join scm_company fin_company_financial_yearcompany_id on (fin_company_financial_yearcompany_id.id = fin_company_financial_year.company_id) left outer join fin_accounting_financial_year fin_company_financial_yearfinancial_year_id on (fin_company_financial_yearfinancial_year_id.id = fin_company_financial_year.financial_year_id)"); //Join Query
//
//    sql.string(new String[]{"fin_company_financial_yearcompany_id.company_name","fin_company_financial_yearfinancial_year_id.description"}); //String search or sort fields
//    sql.number(new String[]{"fin_company_financial_year.id","fin_company_financial_year.is_current_financial_year"}); //Numeric search or sort fields
//    sql.date(null);  //Date search or sort fields
//    return sql;
//  }
  /**
   * Return List of CompanyFinancialYear.
   *
   * @param main
   * @return List of CompanyFinancialYear
   */
//  public static final List<CompanyFinancialYear> listPaged(Main main) {
//     return AppService.listPagedJpa(main, getCompanyFinancialYearSqlPaged(main));
//  }
//  /**
//   * Return list of CompanyFinancialYear based on condition
//   * @param main
//   * @return List<CompanyFinancialYear>
//   */
//  public static final List<CompanyFinancialYear> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyFinancialYearSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select CompanyFinancialYear by key.
   *
   * @param main
   * @param companyFinancialYear
   * @return CompanyFinancialYear
   */
  public static final CompanyFinancialYear selectByPk(Main main, CompanyFinancialYear companyFinancialYear) {
    return (CompanyFinancialYear) AppService.find(main, CompanyFinancialYear.class, companyFinancialYear.getId());
  }

  /**
   * Insert CompanyFinancialYear.
   *
   * @param main
   * @param companyFinancialYear
   */
  public static final void insert(Main main, CompanyFinancialYear companyFinancialYear) {
    AppService.insert(main, companyFinancialYear);

  }

  /**
   * Update CompanyFinancialYear by key.
   *
   * @param main
   * @param companyFinancialYear
   * @return CompanyFinancialYear
   */
  public static final CompanyFinancialYear updateByPk(Main main, CompanyFinancialYear companyFinancialYear) {
    return (CompanyFinancialYear) AppService.update(main, companyFinancialYear);
  }

  /**
   * Insert or update CompanyFinancialYear
   *
   * @param main
   * @param companyFinancialYear
   */
  public static void insertOrUpdate(Main main, CompanyFinancialYear companyFinancialYear) {
    if (companyFinancialYear.getId() == null) {
      insert(main, companyFinancialYear);
    } else {
      updateByPk(main, companyFinancialYear);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyFinancialYear
   */
  public static void clone(Main main, CompanyFinancialYear companyFinancialYear) {
    companyFinancialYear.setId(null); //Set to null for insert
    insert(main, companyFinancialYear);
  }

  /**
   * Delete CompanyFinancialYear.
   *
   * @param main
   * @param companyFinancialYear
   */
  public static final void deleteByPk(Main main, CompanyFinancialYear companyFinancialYear) {
    AppService.delete(main, CompanyFinancialYear.class, companyFinancialYear.getId());
  }

  /**
   * Delete Array of CompanyFinancialYear.
   *
   * @param main
   * @param companyFinancialYear
   */
  public static final void deleteByPkArray(Main main, CompanyFinancialYear[] companyFinancialYear) {
    for (CompanyFinancialYear e : companyFinancialYear) {
      deleteByPk(main, e);
    }
  }

  public static final void addNewFinancialYear(Main main, AccountingFinancialYear accountingFinancialYear, Company company) {
    if (!AppService.exist(main, "select '1' from fin_company_financial_year where financial_year_id=? and company_id=?",
            new Object[]{accountingFinancialYear.getId(), company.getId()})) {
      CompanyFinancialYear companyFinancialYear = new CompanyFinancialYear();
      companyFinancialYear.setCompanyId(company);
      companyFinancialYear.setFinancialYearId(accountingFinancialYear);
      companyFinancialYear.setIsCurrentFinancialYear(0);
      companyFinancialYear.setClosureStatus(0);
      companyFinancialYear.setReadyForClosure(0);
      insert(main, companyFinancialYear);
    }
  }

  public static final void updateCurrentFinancialYear(Main main, CompanyFinancialYear companyFinancialYear) {
    if (companyFinancialYear.getCompanyId() != null) {
      main.clear();
      main.param(companyFinancialYear.getCompanyId().getId());
      AppService.updateSql(main, CompanyFinancialYear.class, "update fin_company_financial_year set is_current_financial_year=0 where company_id=?", true);
      main.clear();
      main.param(companyFinancialYear.getId());
      AppService.updateSql(main, CompanyFinancialYear.class, "update fin_company_financial_year set is_current_financial_year=1 where id=?", true);
    }
  }
}
