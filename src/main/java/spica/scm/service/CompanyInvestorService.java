/*
 * @(#)CompanyInvestorService.java	1.0 Fri Oct 21 18:55:37 IST 2016 
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
import spica.scm.domain.CompanyInvestor;
import spica.scm.domain.UserProfile;
import spica.scm.validate.CompanyInvestorIs;

/**
 * CompanyInvestorService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Oct 21 18:55:37 IST 2016
 */
public abstract class CompanyInvestorService {

  /**
   * CompanyInvestor paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyInvestorSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_investor", CompanyInvestor.class, main);
    sql.main("select scm_company_investor.id,scm_company_investor.profit_percentage,scm_company_investor.company_id,scm_company_investor.user_profile_id,scm_company_investor.designation_id,scm_company_investor.investment_amount,scm_company_investor.share_value,scm_company_investor.share_count,scm_company_investor.investment_date,scm_company_investor.status_id,scm_company_investor.created_by,scm_company_investor.modified_by,scm_company_investor.created_at,scm_company_investor.modified_at from scm_company_investor scm_company_investor "); //Main query
    sql.count("select count(scm_company_investor.id) as total from scm_company_investor scm_company_investor "); //Count query
    sql.join("left outer join scm_company scm_company_investorcompany_id on (scm_company_investorcompany_id.id = scm_company_investor.company_id) left outer join scm_user_profile scm_company_investoruser_profile_id on (scm_company_investoruser_profile_id.id = scm_company_investor.user_profile_id) left outer join scm_designation scm_company_investordesignation_id on (scm_company_investordesignation_id.id = scm_company_investor.designation_id) left outer join scm_status scm_company_investorstatus_id on (scm_company_investorstatus_id.id = scm_company_investor.status_id)"); //Join Query

    sql.string(new String[]{"scm_company_investorcompany_id.company_name", "scm_company_investoruser_profile_id.user_code", "scm_company_investordesignation_id.title", "scm_company_investor.share_value", "scm_company_investorstatus_id.title", "scm_company_investor.created_by", "scm_company_investor.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company_investor.id", "scm_company_investor.investment_amount", "scm_company_investor.share_count"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_company_investor.investment_date", "scm_company_investor.created_at", "scm_company_investor.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CompanyInvestor.
   *
   * @param main
   * @return List of CompanyInvestor
   */
  public static final List<CompanyInvestor> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCompanyInvestorSqlPaged(main));
  }

//  /**
//   * Return list of CompanyInvestor based on condition
//   * @param main
//   * @return List<CompanyInvestor>
//   */
//  public static final List<CompanyInvestor> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyInvestorSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select CompanyInvestor by key.
   *
   * @param main
   * @param companyInvestor
   * @return CompanyInvestor
   */
  public static final CompanyInvestor selectByPk(Main main, CompanyInvestor companyInvestor) {
    return (CompanyInvestor) AppService.find(main, CompanyInvestor.class, companyInvestor.getId());
  }

  /**
   * Insert CompanyInvestor.
   *
   * @param main
   * @param companyInvestor
   */
  public static final void insert(Main main, CompanyInvestor companyInvestor) {
    CompanyInvestorIs.insertAble(main, companyInvestor);  //Validating
    AppService.insert(main, companyInvestor);

  }

  /**
   * Update CompanyInvestor by key.
   *
   * @param main
   * @param companyInvestor
   * @return CompanyInvestor
   */
  public static final CompanyInvestor updateByPk(Main main, CompanyInvestor companyInvestor) {
    CompanyInvestorIs.updateAble(main, companyInvestor); //Validating
    return (CompanyInvestor) AppService.update(main, companyInvestor);
  }

  /**
   * Insert or update CompanyInvestor
   *
   * @param main
   * @param companyInvestor
   */
  public static void insertOrUpdate(Main main, CompanyInvestor companyInvestor) {
    if (companyInvestor.getId() == null) {
      insert(main, companyInvestor);
    } else {
      updateByPk(main, companyInvestor);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyInvestor
   */
  public static void clone(Main main, CompanyInvestor companyInvestor) {
    companyInvestor.setId(null); //Set to null for insert
    insert(main, companyInvestor);
  }

  /**
   * Delete CompanyInvestor.
   *
   * @param main
   * @param companyInvestor
   */
  public static final void deleteByPk(Main main, CompanyInvestor companyInvestor) {
    CompanyInvestorIs.deleteAble(main, companyInvestor); //Validation
    AppService.delete(main, CompanyInvestor.class, companyInvestor.getId());
  }

  /**
   * Delete Array of CompanyInvestor.
   *
   * @param main
   * @param companyInvestor
   */
  public static final void deleteByPkArray(Main main, CompanyInvestor[] companyInvestor) {
    for (CompanyInvestor e : companyInvestor) {
      deleteByPk(main, e);
    }
  }

  public static final List<CompanyInvestor> investorListByCompany(Main main, Company company) {
    SqlPage sql = getCompanyInvestorSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_investor.company_id=?");
    sql.param(company.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static void insertArray(Main main, UserProfile[] userProfileSelected, Company company) {
    if (userProfileSelected != null) {
      CompanyInvestor companyInvestor;
      for (UserProfile userProfile : userProfileSelected) {  //Reinserting
        companyInvestor = new CompanyInvestor();
        companyInvestor.setUserProfileId(userProfile);
        companyInvestor.setCompanyId(company);
//        companyContact.setContactName(humanResources.getFirstName());
        companyInvestor.setDesignationId(userProfile.getDesignationId());
//        companyContact.setPhone(humanResources.getPhoneNo());
//        companyContact.setEmail(humanResources.getEmail());
//        companyContact.setSortOrder(1);
        companyInvestor.setInvestmentAmount(0.00);
        companyInvestor.setShareCount(0);
        companyInvestor.setShareValue(null);
        companyInvestor.setInvestmentDate(null);
        companyInvestor.setStatusId(userProfile.getStatusId());
        insert(main, companyInvestor);
      }
    }
  }

  public static final void deleteCompanyInvestor(Main main, CompanyInvestor companyInvestor) {
    AppService.deleteSql(main, CompanyInvestor.class, "delete from scm_company_investor where id = ?", new Object[]{companyInvestor.getId()});
  }
//
//  public static CompanyInvestor totalInvestmentAmount(Main main, Company company) {
//    return (CompanyInvestor) AppService.single(main,CompanyInvestor.class, "SELECT SUM(investment_amount) investment_amount FROM scm_company_investor where company_id=?", new Object[]{company.getId()});
//  }
}
