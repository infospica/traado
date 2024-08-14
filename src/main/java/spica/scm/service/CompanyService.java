/*
 * @(#)CompanyService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.fin.service.CompanyFinancialYearService;
import spica.fin.service.LedgerExternalService;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyBank;
import spica.scm.domain.CompanyBranch;
import spica.scm.domain.CompanyContact;
import spica.scm.domain.CompanyFinancialYear;
import spica.scm.domain.CompanyInvestor;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanyReference;
import spica.scm.domain.CompanyWarehouse;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesReturn;
import spica.scm.validate.CompanyIs;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.config.AppConfig;
import wawo.entity.core.SqlPage;

/**
 * CompanyService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CompanyService {

  /**
   * Company paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company", Company.class, main);
    sql.main("select scm_company.id,scm_company.company_name,scm_company.company_type_id,scm_company.description,scm_company.company_id,scm_company.financial_year_start_date,scm_company.country_id,scm_company.state_id,scm_company.country_tax_regime_id,scm_company.pan_no,scm_company.cst_no,scm_company.vat_no,scm_company.tin_no,scm_company.gst_no,scm_company.status_id,scm_company.created_at,scm_company.modified_at,scm_company.created_by,scm_company.modified_by,scm_company.currency_id,scm_company.taxable,scm_company.business_operation from scm_company scm_company "); //Main query
    sql.count("select count(scm_company.id) as total from scm_company scm_company "); //Count query
    sql.join("left outer join scm_company_type scm_companycompany_type_id on (scm_companycompany_type_id.id = scm_company.company_type_id) left outer join scm_company scm_companycompany_id on (scm_companycompany_id.id = scm_company.company_id) left outer join scm_country scm_companycountry_id on (scm_companycountry_id.id = scm_company.country_id) left outer join scm_state scm_companystate_id on (scm_companystate_id.id = scm_company.state_id) left outer join scm_country_tax_regime scm_companycountry_tax_id on (scm_companycountry_tax_id.id = scm_company.country_tax_regime_id) left outer join scm_status scm_companystatus_id on (scm_companystatus_id.id = scm_company.status_id)"); //Join Query
    sql.orderBy("upper(scm_company.company_name) asc");
    sql.string(new String[]{"scm_company.company_name", "scm_companycompany_type_id.title", "scm_company.description", "scm_companycompany_id.company_name", "scm_companycountry_id.country_name", "scm_companystate_id.state_name", "scm_company.pan_no", "scm_company.cst_no", "scm_company.vat_no", "scm_company.tin_no", "scm_company.gst_no", "scm_companystatus_id.title", "scm_company.created_by", "scm_company.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company.id", "scm_companycountry_tax_id.regime"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_company.financial_year_start_date", "scm_company.created_at", "scm_company.modified_at"});  //Date search or sort fields
    return sql;
  }

  public static SqlPage getPermitedCompanies(Main main, SqlPage sql) {
    if (!AppConfig.rootUser.equals(main.getAppUser().getLogin())) {
      sql.cond("where scm_company.id = ? or scm_company.id in (select company_id from scm_user_company where user_id = ?)");
      main.param(UserRuntimeView.instance().getAppUser().getUserProfileId().getCompanyId().getId());
      main.param(UserRuntimeView.instance().getAppUser().getId());
    }
    return sql;
  }

  /**
   * Return List of Company.
   *
   * @param main
   * @return List of Company
   */
  public static final List<Company> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPermitedCompanies(main, getCompanySqlPaged(main)));
  }

  /**
   * Return all warehouse of a company.
   *
   * @param main
   * @param company
   * @return
   */
  public static final List<Company> branchListByCompany(Main main, Company company) {
    SqlPage sql = getCompanySqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_company.company_id=?");
    sql.param(company.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of Company based on condition
//   * @param main
//   * @return List<Company>
//   */
//  public static final List<Company> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select Company by key.
   *
   * @param main
   * @param company
   * @return Company
   */
  public static final Company selectByPk(Main main, Company company) {
    return selectById(main, company.getId());
  }

  public static final Company selectById(Main main, Integer id) {
    Company c = (Company) AppService.find(main, Company.class, id);
    if(c!=null){
     c.setCompanySettings(CompanySettingsService.selectIfNull(main, c));
    }
    return c;
  }

  /**
   * Insert Company.
   *
   * @param main
   * @param company
   */
  private static final void insert(Main main, Company company) {
    CompanyIs.insertAble(main, company);  //Validating
    AppService.insert(main, company);
    CompanyFinancialYearService.insertByCompany(main, company);
    LedgerExternalService.createDefaultPrefixAndLedger(main, company);
    CompanySettingsService.insertDefaultSettings(main, company);
  }

  /**
   * Update Company by key.
   *
   * @param main
   * @param company
   * @return Company
   */
  public static final Company updateByPk(Main main, Company company) {
    CompanyIs.updateAble(main, company); //Validating
    return (Company) AppService.update(main, company);
  }

  /**
   * Insert or update Company
   *
   * @param main
   * @param Company
   */
  public static void insertOrUpdate(Main main, Company company) {
    if (company.getId() == null) {
      insert(main, company);
      main.em().flush();
    } else {
      updateByPk(main, company);
    }
    AccountGroupDocPrefixService.insertOrUpdateCompanySalesServicesPrefix(main, company);
    if (company.getDocPrefixBasedOnCompany() == 1) {
      AccountGroupDocPrefixService.insertPurchasePrefixes(main, company);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param company
   */
  public static void clone(Main main, Company company) {
    company.setId(null); //Set to null for insert
    insert(main, company);
  }

  /**
   * Delete Company.
   *
   * @param main
   * @param company
   */
  public static final void deleteByPk(Main main, Company company) {
    CompanyIs.deleteAble(main, company); //Validation
    AppService.deleteSql(main, CompanyAddress.class, "delete from scm_company_address scm_company_address where scm_company_address.company_id=?", new Object[]{company.getId()});
    AppService.deleteSql(main, CompanyContact.class, "delete from scm_company_contact scm_company_contact where scm_company_contact.company_id=?", new Object[]{company.getId()});
    AppService.deleteSql(main, CompanyBank.class, "delete from scm_company_bank scm_company_bank where scm_company_bank.company_id=?", new Object[]{company.getId()});
    AppService.deleteSql(main, CompanyReference.class, "delete from scm_company_reference scm_company_reference where scm_company_reference.company_id=?", new Object[]{company.getId()});
    AppService.deleteSql(main, CompanyBranch.class, "delete from scm_company_branch scm_company_branch where scm_company_branch.company_id=?", new Object[]{company.getId()});
    AppService.deleteSql(main, CompanyWarehouse.class, "delete from scm_company_warehouse scm_company_warehouse where scm_company_warehouse.company_id=?", new Object[]{company.getId()});
    AppService.deleteSql(main, CompanyLicense.class, "delete from scm_company_license scm_company_license where scm_company_license.company_id=?", new Object[]{company.getId()});
    AppService.deleteSql(main, CompanyInvestor.class, "delete from scm_company_investor scm_company_investor where scm_company_investor.company_id=?", new Object[]{company.getId()});
    AppService.deleteSql(main, CompanyFinancialYear.class, "delete from fin_company_financial_year where company_id=?", new Object[]{company.getId()});
    AppService.delete(main, Company.class, company.getId());
  }

  /**
   * Delete Array of Company.
   *
   * @param main
   * @param company
   */
  public static final void deleteByPkArray(Main main, Company[] company) {
    for (Company e : company) {
      deleteByPk(main, e);
    }
  }

  public static final Company getCompanyBySalesInvoice(Main main, SalesInvoice salesInvoice) {
    if (salesInvoice != null) {
      return (Company) AppService.single(main, Company.class, "select * from scm_company where id in (select company_id from scm_sales_invoice where id = ?) ",
              new Object[]{salesInvoice.getId()});
    }
    return null;
  }

  public static final Company getCompanyBySalesReturn(Main main, SalesReturn salesReturn) {
    if (salesReturn != null) {
      return (Company) AppService.single(main, Company.class, "select * from scm_company where id in (select company_id from scm_sales_return where id = ?) ",
              new Object[]{salesReturn.getId()});
    }
    return null;
  }

  public static final Company getCompanyByPurchaseReturn(Main main, PurchaseReturn purchaseReturn) {
    if (purchaseReturn != null) {
      return (Company) AppService.single(main, Company.class, "select * from scm_company where id in (select company_id from scm_purchase_return where id = ?) ",
              new Object[]{purchaseReturn.getId()});
    }
    return null;
  }

  public static final Company getCompanyByProductEntry(Main main, ProductEntry productEntry) {
    if (productEntry != null) {
      return (Company) AppService.single(main, Company.class, "select * from scm_company where id in (select company_id from scm_product_entry where id = ?) ",
              new Object[]{productEntry.getId()});
    }
    return null;
  }
}
