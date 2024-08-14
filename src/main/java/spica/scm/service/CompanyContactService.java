/*
 * @(#)CompanyContactService.java	1.0 Wed Mar 30 12:35:28 IST 2016
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
import spica.scm.domain.CompanyContact;
import spica.scm.domain.UserProfile;
import spica.scm.validate.CompanyContactIs;

/**
 * CompanyContactService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CompanyContactService {

  /**
   * CompanyContact paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyContactSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_contact", CompanyContact.class, main);
    sql.main("select scm_company_contact.id,scm_company_contact.company_id,scm_company_contact.user_profile_id,scm_company_contact.designation_id,scm_company_contact.sort_order,scm_company_contact.status_id,scm_company_contact.created_by,scm_company_contact.modified_by,scm_company_contact.created_at,scm_company_contact.modified_at from scm_company_contact scm_company_contact "); //Main query
    sql.count("select count(scm_company_contact.id) as total from scm_company_contact scm_company_contact "); //Count query
    sql.join("left outer join scm_company scm_company_contactcompany_id on (scm_company_contactcompany_id.id = scm_company_contact.company_id) left outer join scm_user_profile scm_company_contactuser_profile_id on (scm_company_contactuser_profile_id.id = scm_company_contact.user_profile_id) left outer join scm_designation scm_company_contactdesignation_id on (scm_company_contactdesignation_id.id = scm_company_contact.designation_id) left outer join scm_status scm_company_contactstatus_id on (scm_company_contactstatus_id.id = scm_company_contact.status_id)"); //Join Query

    sql.string(new String[]{"scm_company_contactcompany_id.company_name", "scm_company_contactuser_profile_id.user_code", "scm_company_contactdesignation_id.title", "scm_company_contactstatus_id.title", "scm_company_contact.created_by", "scm_company_contact.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company_contact.id", "scm_company_contact.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_company_contact.created_at", "scm_company_contact.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CompanyContact.
   *
   * @param main
   * @return List of CompanyContact
   */
  public static final List<CompanyContact> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCompanyContactSqlPaged(main));
  }

  /**
   * Return all contact of a company.
   *
   * @param main
   * @param company
   * @return
   */
  public static final List<CompanyContact> contactListByCompany(Main main, Company company) {
    SqlPage sql = getCompanyContactSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_contact.company_id=?");
    sql.param(company.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of CompanyContact based on condition
//   * @param main
//   * @return List<CompanyContact>
//   */
//  public static final List<CompanyContact> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyContactSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select CompanyContact by key.
   *
   * @param main
   * @param companyContact
   * @return CompanyContact
   */
  public static final CompanyContact selectByPk(Main main, CompanyContact companyContact) {
    return (CompanyContact) AppService.find(main, CompanyContact.class, companyContact.getId());
  }

  /**
   * Insert CompanyContact.
   *
   * @param main
   * @param companyContact
   */
  public static final void insert(Main main, CompanyContact companyContact) {
    CompanyContactIs.insertAble(main, companyContact);  //Validating
    AppService.insert(main, companyContact);

  }

  /**
   * Update CompanyContact by key.
   *
   * @param main
   * @param companyContact
   * @return CompanyContact
   */
  public static final CompanyContact updateByPk(Main main, CompanyContact companyContact) {
    CompanyContactIs.updateAble(main, companyContact); //Validating
    return (CompanyContact) AppService.update(main, companyContact);
  }

  /**
   * Insert or update CompanyContact
   *
   * @param main
   * @param companyContact
   */
  public static void insertOrUpdate(Main main, CompanyContact companyContact) {
    if (companyContact.getId() == null) {
      insert(main, companyContact);
    } else {
      updateByPk(main, companyContact);
    }
  }

  /**
   * Making default for newly added or updated if isdefault is checked
   *
   * @param main
   * @param companyContact
   */
  public static void makeDefault(Main main, CompanyContact companyContact) {
    if (companyContact.getSortOrder() == 0) {
      main.param(1);
      main.param(companyContact.getCompanyId().getId());
      main.param(companyContact.getId());
      main.param(0);
      AppService.updateSql(main, CompanyContact.class, "update scm_company_contact set modified_by = ?, modified_at = ?, sort_order = ? where company_id = ? and id <> ? and sort_order = ?", true);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyContact
   */
  public static void clone(Main main, CompanyContact companyContact) {
    companyContact.setId(null); //Set to null for insert
    insert(main, companyContact);
  }

  /**
   * Delete CompanyContact.
   *
   * @param main
   * @param companyContact
   */
  public static final void deleteByPk(Main main, CompanyContact companyContact) {
    CompanyContactIs.deleteAble(main, companyContact); //Validation
    AppService.delete(main, CompanyContact.class, companyContact.getId());
  }

  /**
   * Delete Array of CompanyContact.
   *
   * @param main
   * @param companyContact
   */
  public static final void deleteByPkArray(Main main, CompanyContact[] companyContact) {
    for (CompanyContact e : companyContact) {
      deleteByPk(main, e);
    }
  }

  public static void insertArray(Main main, UserProfile[] userProfileSelected, Company company) {
    if (userProfileSelected != null) {
      CompanyContact companyContact;
      for (UserProfile userProfile : userProfileSelected) {  //Reinserting
        companyContact = new CompanyContact();
        companyContact.setUserProfileId(userProfile);
        companyContact.setCompanyId(company);
//        companyContact.setContactName(humanResources.getFirstName());
        companyContact.setDesignationId(userProfile.getDesignationId());
//        companyContact.setPhone(humanResources.getPhoneNo());
//        companyContact.setEmail(humanResources.getEmail());
//        companyContact.setSortOrder(1);
        companyContact.setStatusId(userProfile.getStatusId());
        insert(main, companyContact);
      }
    }
  }

  public static final void deleteCompanyContact(Main main, CompanyContact companyContact) {
    AppService.deleteSql(main, CompanyContact.class, "delete from scm_company_contact where id = ?", new Object[]{companyContact.getId()});
  }
}
