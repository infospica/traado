/*
 * @(#)CompanyLicenseService.java	1.0 Thu Jun 09 11:13:13 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CompanyLicense;
import spica.scm.validate.CompanyLicenseIs;
import spica.sys.UserRuntimeView;

/**
 * CompanyLicenseService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 09 11:13:13 IST 2016
 */
public abstract class CompanyLicenseService {

  /**
   * CompanyLicense paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyLicenseSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_license", CompanyLicense.class, main);
    sql.main("select scm_company_license.id,scm_company_license.company_id,scm_company_license.license_type_id,scm_company_license.description,scm_company_license.license_key,scm_company_license.issued_at,scm_company_license.valid_from,scm_company_license.valid_to,scm_company_license.file_path,scm_company_license.sort_order,scm_company_license.status_id,scm_company_license.created_by,scm_company_license.modified_by,scm_company_license.created_at,scm_company_license.modified_at from scm_company_license scm_company_license "); //Main query
    sql.count("select count(scm_company_license.id) as total from scm_company_license scm_company_license "); //Count query
    sql.join("left outer join scm_company scm_company_licensecompany_id on (scm_company_licensecompany_id.id = scm_company_license.company_id) left outer join scm_license_type scm_company_licenselicense_type_id on (scm_company_licenselicense_type_id.id = scm_company_license.license_type_id) left outer join scm_status scm_company_licensestatus_id on (scm_company_licensestatus_id.id = scm_company_license.status_id)"); //Join Query

    sql.string(new String[]{"scm_company_licensecompany_id.company_name", "scm_company_license.issued_at", "scm_company_licenselicense_type_id.title", "scm_company_license.description", "scm_company_license.license_key", "scm_company_license.file_path", "scm_company_licensestatus_id.title", "scm_company_license.created_by", "scm_company_license.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company_license.id", "scm_company_license.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_company_license.valid_from", "scm_company_license.valid_to", "scm_company_license.created_at", "scm_company_license.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CompanyLicense.
   *
   * @param main
   * @return List of CompanyLicense
   */
  public static final List<CompanyLicense> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCompanyLicenseSqlPaged(main));
  }

//  /**
//   * Return list of CompanyLicense based on condition
//   * @param main
//   * @return List<CompanyLicense>
//   */
//  public static final List<CompanyLicense> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyLicenseSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select CompanyLicense by key.
   *
   * @param main
   * @param companyLicense
   * @return CompanyLicense
   */
  public static final CompanyLicense selectByPk(Main main, CompanyLicense companyLicense) {
    return (CompanyLicense) AppService.find(main, CompanyLicense.class, companyLicense.getId());
  }

  /**
   * Insert CompanyLicense.
   *
   * @param main
   * @param companyLicense
   */
  public static final void insert(Main main, CompanyLicense companyLicense) {
    CompanyLicenseIs.insertAble(main, companyLicense, UserRuntimeView.instance().getCompany().getId());  //Validating
    AppService.insert(main, companyLicense);

  }

  /**
   * Update CompanyLicense by key.
   *
   * @param main
   * @param companyLicense
   * @return CompanyLicense
   */
  public static final CompanyLicense updateByPk(Main main, CompanyLicense companyLicense) {
    CompanyLicenseIs.updateAble(main, companyLicense); //Validating
    return (CompanyLicense) AppService.update(main, companyLicense);
  }

  /**
   * Insert or update CompanyLicense
   *
   * @param main
   * @param CompanyLicense
   */
  public static void insertOrUpdate(Main main, CompanyLicense companyLicense) {
    if (companyLicense.getId() == null) {
      insert(main, companyLicense);
    } else {
      updateByPk(main, companyLicense);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyLicense
   */
  public static void clone(Main main, CompanyLicense companyLicense) {
    companyLicense.setId(null); //Set to null for insert
    insert(main, companyLicense);
  }

  /**
   * Delete CompanyLicense.
   *
   * @param main
   * @param companyLicense
   */
  public static final void deleteByPk(Main main, CompanyLicense companyLicense) {
    CompanyLicenseIs.deleteAble(main, companyLicense); //Validation
    AppService.delete(main, CompanyLicense.class, companyLicense.getId());
  }

  /**
   * Delete Array of CompanyLicense.
   *
   * @param main
   * @param companyLicense
   */
  public static final void deleteByPkArray(Main main, CompanyLicense[] companyLicense) {
    for (CompanyLicense e : companyLicense) {
      deleteByPk(main, e);
    }
  }

  /**
   * Return all license of a company.
   *
   * @param main
   * @param company
   * @return
   */
  public static final List<CompanyLicense> licenseListByCompany(Main main, Company company) {
    main.clear();
    SqlPage sql = getCompanyLicenseSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_license.company_id = ?");
    sql.param(company.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static List<CompanyLicense> listActiveLicenseByCompany(Main main, Company company, int status) {

    return AppService.list(main, CompanyLicense.class, "select id,company_id,license_type_id,license_key,status_id from scm_company_license Where company_id=? and status_id=?", new Object[]{company.getId(), status});
  }

}
