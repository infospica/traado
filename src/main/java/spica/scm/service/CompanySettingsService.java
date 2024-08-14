/*
 * @(#)CompanySettingsService.java	1.0 Mon Mar 11 16:27:37 IST 2019 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroupDocPrefix;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyFinancialYear;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.ProductEntry;
import spica.sys.SystemConstants;
import spica.sys.domain.SmsProvider;
import wawo.entity.core.AppEm;

/**
 * CompanySettingsService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Mar 11 16:27:37 IST 2019
 */
public abstract class CompanySettingsService {

  /**
   * CompanySettings paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanySettingsSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_settings", CompanySettings.class, main);
    sql.main("select scm_company_settings.id,scm_company_settings.company_id,scm_company_settings.print_invoice_footer,scm_company_settings.print_invoice_logo from scm_company_settings scm_company_settings"); //Main query
    sql.count("select count(scm_company_settings.id) as total from scm_company_settings scm_company_settings "); //Count query
    sql.join("left outer join scm_company scm_company_settingscompany_id on (scm_company_settingscompany_id.id = scm_company_settings.company_id)"); //Join Query
    if (!main.getAppUser().isRoot()) {
      sql.cond("where scm_company_settings.company_id is not null");
    }
    sql.string(new String[]{"scm_company_settingscompany_id.company_name", "scm_company_settings.print_invoice_footer", "scm_company_settings.print_invoice_logo"}); //String search or sort fields
    sql.number(new String[]{"scm_company_settings.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CompanySettings.
   *
   * @param main
   * @return List of CompanySettings
   */
  public static final List<CompanySettings> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCompanySettingsSqlPaged(main));
  }

//  /**
//   * Return list of CompanySettings based on condition
//   * @param main
//   * @return List<CompanySettings>
//   */
//  public static final List<CompanySettings> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanySettingsSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select CompanySettings by key.
   *
   * @param main
   * @param companySettings
   * @return CompanySettings
   */
  public static final CompanySettings selectByPk(Main main, CompanySettings companySettings) {
    return (CompanySettings) AppService.find(main, CompanySettings.class, companySettings.getId());
  }

  /**
   * Insert CompanySettings.
   *
   * @param main
   * @param companySettings
   */
  public static final void insert(Main main, CompanySettings companySettings) {
    //CompanySettingsIs.insertAble(main, companySettings);  //Validating
    AppService.insert(main, companySettings);

  }

  /**
   * Update CompanySettings by key.
   *
   * @param main
   * @param companySettings
   * @return CompanySettings
   */
  public static final CompanySettings updateByPk(Main main, CompanySettings companySettings) {
    // CompanySettingsIs.updateAble(main, companySettings); //Validating
    return (CompanySettings) AppService.update(main, companySettings);
  }

  /**
   * Insert or update CompanySettings
   *
   * @param main
   * @param companySettings
   */
  public static void insertOrUpdate(Main main, CompanySettings companySettings) {
    if (companySettings.getId() == null) {
      insert(main, companySettings);
    } else {
      updateByPk(main, companySettings);
    }
    main.clear();
    main.param(companySettings.getLimitReturnBySales());
    main.param(companySettings.getCompanyId().getId());
    AppService.updateSql(main, Account.class, "update scm_account set modified_by=?, modified_at=?, limit_return_by_sales = ? where company_id = ?", true);

    if (companySettings.getEnableCompanyPrefix() != null && companySettings.getEnableCompanyPrefix().intValue() == SystemConstants.YES) {
      CompanySettingsService.updatePurchaseIndex(main, companySettings, companySettings.getCompanyId().getCurrentFinancialYear());
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companySettings
   */
  public static void clone(Main main, CompanySettings companySettings) {
    companySettings.setId(null); //Set to null for insert
    insert(main, companySettings);
  }

  /**
   * Delete CompanySettings.
   *
   * @param main
   * @param companySettings
   */
  public static final void deleteByPk(Main main, CompanySettings companySettings) {
    // CompanySettingsIs.deleteAble(main, companySettings); //Validation
    AppService.delete(main, CompanySettings.class, companySettings.getId());
  }

  /**
   * Delete Array of CompanySettings.
   *
   * @param main
   * @param companySettings
   */
  public static final void deleteByPkArray(Main main, CompanySettings[] companySettings) {
    for (CompanySettings e : companySettings) {
      deleteByPk(main, e);
    }
  }

  public static CompanySettings selectIfNull(Main main, Company company) {
    return selectByCompany(main.em(), company);
  }

  public static CompanySettings selectByCompany(AppEm em, Company company) {
    if (company.getCompanySettings() == null) {
      company.setCompanySettings((CompanySettings) em.single(CompanySettings.class, "select * from scm_company_settings where company_id=?", new Object[]{company.getId()}));
    }
    return company.getCompanySettings();
  }

  public static void insertDefaultSettings(Main main, Company company) {
    CompanySettings settings = (CompanySettings) AppService.single(main, CompanySettings.class, "select * from scm_company_settings where company_id is null");
    CompanySettings companySettings = new CompanySettings(settings);
    companySettings.setCompanyId(company);
    insert(main, companySettings);
  }

  public static List<SmsProvider> selectServiceProviders(Main main) {
    return AppService.list(main, SmsProvider.class, "select * from sys_sms_provider", null);
  }

  public static AccountGroupDocPrefix selectPurchasePrefix(Main main, CompanySettings companySettings, Integer type, AccountingFinancialYear financialYear) {
    return (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class, "select * from scm_account_group_doc_prefix where account_id is null and account_group_id is null "
            + "and company_id=? and prefix_type_id = ? and financial_year_id=? ",
            new Object[]{companySettings.getCompanyId().getId(), type, financialYear.getId()});
  }

  public static void updatePurchaseIndex(Main main, CompanySettings companySettings, AccountingFinancialYear financialYear) {

    main.clear();
    main.param(financialYear.getId());
    main.param(companySettings.getCompanyId().getId());
    if (companySettings.getPurchaseDocPrefix() == null) {
      AppService.updateSql(main, ProductEntry.class, "UPDATE scm_product_entry set reference_no= '" + companySettings.getPurchasePrefix() + "' || LPAD(TAB.row_number::VARCHAR,5,'0') FROM \n"
              + "(SELECT account_invoice_no,product_entry_date,id ,ROW_NUMBER () OVER (ORDER BY product_entry_date,account_invoice_no,id)\n"
              + "FROM scm_product_entry WHERE financial_year_id=? and company_id=? \n"
              + "AND product_entry_status_id>=2 AND opening_stock_entry=0) as TAB where  scm_product_entry.id=TAB.id ", false);

      String purReferenceNo = (String) AppService.first(main, "select reference_no from scm_product_entry \n"
              + "WHERE financial_year_id=? and company_id=? AND product_entry_status_id>=2 AND opening_stock_entry=0 order by reference_no desc limit 1");
      Integer purIndex = 1;
      if (purReferenceNo != null) {
        purReferenceNo = purReferenceNo.replaceAll(companySettings.getPurchasePrefix(), "");
        purIndex = Integer.parseInt(purReferenceNo);
      }
      AppService.updateSql(main, ProductEntry.class, "UPDATE scm_purchase_return set reference_no= '" + companySettings.getPurchaseReturnPrefix() + "' || LPAD(TAB.row_number::VARCHAR,5,'0') FROM \n"
              + "(SELECT invoice_no,entry_date,id ,ROW_NUMBER () OVER (ORDER BY entry_date,invoice_no,id)\n"
              + "FROM scm_purchase_return WHERE financial_year_id=? and company_id=?\n"
              + "AND purchase_return_status_id>=2 ) as TAB where  scm_purchase_return.id=TAB.id  ", false);
      String purRetReferenceNo = (String) AppService.first(main, "select reference_no from scm_purchase_return \n"
              + "WHERE financial_year_id=? and company_id=? AND purchase_return_status_id>=2  order by reference_no desc limit 1");
      Integer purRetIndex = 1;
      if (purRetReferenceNo != null) {
        purRetReferenceNo = purRetReferenceNo.replaceAll(companySettings.getPurchaseReturnPrefix(), "");
        purRetIndex = Integer.parseInt(purRetReferenceNo);
      }
      AccountGroupDocPrefixService.insertPurchasePrefixesBasedOnCompany(main, companySettings.getCompanyId(), companySettings.getPurchasePrefix(),
              companySettings.getPurchaseReturnPrefix(), purIndex + 1, purRetIndex + 1, companySettings.getCompanyId().getCurrentFinancialYear());
    }
  }

  public static List<CompanyFinancialYear> getFinancialYearList(Main main, Company company) {
    return AppService.list(main, CompanyFinancialYear.class, "select * from fin_company_financial_year where company_id=? order by financial_year_id desc",
            new Object[]{company.getId()});
  }
}
