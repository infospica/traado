/*
 * @(#)CompanyBranchService.java	1.0 Fri Jun 10 11:11:50 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CompanyBranch;
import spica.scm.validate.CompanyBranchIs;

/**
 * CompanyBranchService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jun 10 11:11:50 IST 2016
 */
public abstract class CompanyBranchService {

  /**
   * CompanyBranch paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyBranchSqlPaged(Main main) {
      SqlPage sql = AppService.sqlPage("scm_company_branch", CompanyBranch.class, main);
    sql.main("select scm_company_branch.id,scm_company_branch.company_id,scm_company_branch.branch_name,scm_company_branch.address,scm_company_branch.country_id,scm_company_branch.state_id,scm_company_branch.district_id,scm_company_branch.pin,scm_company_branch.territory_id,scm_company_branch.phone_1,scm_company_branch.phone_2,scm_company_branch.phone_3,scm_company_branch.fax_1,scm_company_branch.fax_2,scm_company_branch.email,scm_company_branch.sort_order,scm_company_branch.status_id,scm_company_branch.created_by,scm_company_branch.modified_by,scm_company_branch.created_at,scm_company_branch.modified_at from scm_company_branch scm_company_branch "); //Main query
    sql.count("select count(scm_company_branch.id) as total from scm_company_branch scm_company_branch "); //Count query
    sql.join("left outer join scm_company scm_company_branchcompany_id on (scm_company_branchcompany_id.id = scm_company_branch.company_id) left outer join scm_country scm_company_branchcountry_id on (scm_company_branchcountry_id.id = scm_company_branch.country_id) left outer join scm_state scm_company_branchstate_id on (scm_company_branchstate_id.id = scm_company_branch.state_id) left outer join scm_district scm_company_branchdistrict_id on (scm_company_branchdistrict_id.id = scm_company_branch.district_id) left outer join scm_territory scm_company_branchterritory_id on (scm_company_branchterritory_id.id = scm_company_branch.territory_id) left outer join scm_status scm_company_branchstatus_id on (scm_company_branchstatus_id.id = scm_company_branch.status_id)"); //Join Query

    sql.string(new String[]{"scm_company_branchcompany_id.company_name","scm_company_branch.branch_name","scm_company_branch.address","scm_company_branchcountry_id.country_name","scm_company_branchstate_id.state_name","scm_company_branchdistrict_id.district_name","scm_company_branch.pin","scm_company_branchterritory_id.territory_name","scm_company_branch.phone_1","scm_company_branch.phone_2","scm_company_branch.phone_3","scm_company_branch.fax_1","scm_company_branch.fax_2","scm_company_branch.email","scm_company_branchstatus_id.title","scm_company_branch.created_by","scm_company_branch.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company_branch.id","scm_company_branch.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_company_branch.created_at","scm_company_branch.modified_at"});  //Date search or sort fields
      return sql;
  }

  /**
   * Return List of CompanyBranch.
   *
   * @param main
   * @return List of CompanyBranch
   */
  public static final List<CompanyBranch> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCompanyBranchSqlPaged(main));
  }

  public static final List<CompanyBranch> branchesListByCompany(Main main, Company company) {
    SqlPage sql = getCompanyBranchSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_branch.company_id=?");
    sql.param(company.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of CompanyBranch based on condition
//   * @param main
//   * @return List<CompanyBranch>
//   */
//  public static final List<CompanyBranch> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyBranchSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select CompanyBranch by key.
   *
   * @param main
   * @param companyBranch
   * @return CompanyBranch
   */
  public static final CompanyBranch selectByPk(Main main, CompanyBranch companyBranch) {
    return (CompanyBranch) AppService.find(main, CompanyBranch.class, companyBranch.getId());
  }

  /**
   * Insert CompanyBranch.
   *
   * @param main
   * @param companyBranch
   */
  public static final void insert(Main main, CompanyBranch companyBranch) {
    CompanyBranchIs.insertAble(main, companyBranch);  //Validating
    AppService.insert(main, companyBranch);

  }

  /**
   * Update CompanyBranch by key.
   *
   * @param main
   * @param companyBranch
   * @return CompanyBranch
   */
  public static final CompanyBranch updateByPk(Main main, CompanyBranch companyBranch) {
    CompanyBranchIs.updateAble(main, companyBranch); //Validating
    return (CompanyBranch) AppService.update(main, companyBranch);
  }

  /**
   * Insert or update CompanyBranch
   *
   * @param main
   * @param CompanyBranch
   */
  public static void insertOrUpdate(Main main, CompanyBranch companyBranch) {
    if (companyBranch.getId() == null) {
      insert(main, companyBranch);
    } else {
      updateByPk(main, companyBranch);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyBranch
   */
  public static void clone(Main main, CompanyBranch companyBranch) {
    companyBranch.setId(null); //Set to null for insert
    insert(main, companyBranch);
  }

  /**
   * Delete CompanyBranch.
   *
   * @param main
   * @param companyBranch
   */
  public static final void deleteByPk(Main main, CompanyBranch companyBranch) {
    CompanyBranchIs.deleteAble(main, companyBranch); //Validation
    AppService.delete(main, CompanyBranch.class, companyBranch.getId());
  }

  /**
   * Delete Array of CompanyBranch.
   *
   * @param main
   * @param companyBranch
   */
  public static final void deleteByPkArray(Main main, CompanyBranch[] companyBranch) {
    for (CompanyBranch e : companyBranch) {
      deleteByPk(main, e);
    }
  }
}
