/*
 * @(#)CompanyBankContactService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.CompanyBank;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CompanyBankContact;
import spica.scm.validate.CompanyBankContactIs;

/**
 * CompanyBankContactService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CompanyBankContactService {

  /**
   * CompanyBankContact paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyBankContactSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_bank_contact", CompanyBankContact.class, main);
    sql.main("select scm_company_bank_contact.id,scm_company_bank_contact.company_bank_id,scm_company_bank_contact.contact_name,scm_company_bank_contact.designation_id,scm_company_bank_contact.phone_1,scm_company_bank_contact.phone_2,scm_company_bank_contact.fax,scm_company_bank_contact.email,scm_company_bank_contact.sort_order,scm_company_bank_contact.status_id,scm_company_bank_contact.created_by,scm_company_bank_contact.modified_by,scm_company_bank_contact.created_at,scm_company_bank_contact.modified_at from scm_company_bank_contact scm_company_bank_contact "); //Main query
    sql.count("select count(scm_company_bank_contact.id) from scm_company_bank_contact scm_company_bank_contact "); //Count query
    sql.join("left outer join scm_company_bank scm_company_bank_contactcompany_bank_id on (scm_company_bank_contactcompany_bank_id.id = scm_company_bank_contact.company_bank_id) left outer join scm_designation scm_company_bank_contactdesignation_id on (scm_company_bank_contactdesignation_id.id = scm_company_bank_contact.designation_id)left outer join scm_status scm_company_bank_contactstatus_id on (scm_company_bank_contactstatus_id.id = scm_company_bank_contact.status_id)"); //Join Query

    sql.string(new String[]{"scm_company_bank_contactcompany_bank_id.account_name", "scm_company_bank_contact.contact_name", "scm_company_bank_contactdesignation_id.title", "scm_company_bank_contact.phone_1", "scm_company_bank_contact.phone_2", "scm_company_bank_contact.phone_3", "scm_company_bank_contact.fax_1", "scm_company_bank_contact.fax_2", "scm_company_bank_contact.email", "scm_company_bank_contact.created_by", "scm_company_bank_contact.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company_bank_contact.id", "scm_company_bank_contact.sort_order", "scm_company_bank_contact.status_id"}); //Number search or sort fields
    sql.date(new String[]{"scm_company_bank_contact.created_at", "scm_company_bank_contact.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CompanyBankContact.
   *
   * @param main
   * @param companyBank
   * @return List of CompanyBankContact
   */
  public static final List<CompanyBankContact> listPaged(Main main, CompanyBank companyBank) {
    SqlPage sql = getCompanyBankContactSqlPaged(main);
    if (companyBank.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_bank_contact.company_bank_id=?");
    sql.param(companyBank.getId());
    return AppService.listPagedJpa(main, sql);
  }

  /**
   * Return all bank contact of a company.
   *
   * @param main
   * @param companyBank
   * @return
   */
  public static final List<CompanyBankContact> contactListByCompanyBank(Main main, CompanyBank companyBank) {
    SqlPage sql = getCompanyBankContactSqlPaged(main);
    if (companyBank.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_bank_contact.company_bank_id=?");
    sql.param(companyBank.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of CompanyBankContact based on condition
//   * @param main
//   * @return List<CompanyBankContact>
//   */
//  public static final List<CompanyBankContact> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyBankContactSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select CompanyBankContact by key.
   *
   * @param main
   * @param companyBankContact
   * @return CompanyBankContact
   */
  public static final CompanyBankContact selectByPk(Main main, CompanyBankContact companyBankContact) {
    return (CompanyBankContact) AppService.find(main, CompanyBankContact.class, companyBankContact.getId());
  }

  /**
   * Insert CompanyBankContact.
   *
   * @param main
   * @param companyBankContact
   */
  public static final void insert(Main main, CompanyBankContact companyBankContact) {
    CompanyBankContactIs.insertAble(main, companyBankContact);  //Validating
    AppService.insert(main, companyBankContact);

  }

  /**
   * Update CompanyBankContact by key.
   *
   * @param main
   * @param companyBankContact
   * @return CompanyBankContact
   */
  public static final CompanyBankContact updateByPk(Main main, CompanyBankContact companyBankContact) {
    CompanyBankContactIs.updateAble(main, companyBankContact); //Validating
    return (CompanyBankContact) AppService.update(main, companyBankContact);
  }

  /**
   * Insert or update CompanyBankContact
   *
   * @param main
   * @param companyBankContact
   */
  public static void insertOrUpdate(Main main, CompanyBankContact companyBankContact) {
    if (companyBankContact.getId() == null) {
      insert(main, companyBankContact);
    }
    updateByPk(main, companyBankContact);
  }

  /**
   * Making default for newly added or updated if isdefault is checked
   *
   * @param main
   * @param companyBankContact
   */
  public static void makeDefault(Main main, CompanyBankContact companyBankContact) {
    if (companyBankContact.getSortOrder() == 0) {
      main.param(1);
      main.param(companyBankContact.getCompanyBankId().getId());
      main.param(companyBankContact.getId());
      main.param(0);
      AppService.updateSql(main, CompanyBankContact.class, "update scm_company_bank_contact set modified_by = ?, modified_at = ?, sort_order = ? where company_bank_id = ? and id <> ? and sort_order = ?", true);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyBankContact
   */
  public static void clone(Main main, CompanyBankContact companyBankContact) {
    companyBankContact.setId(null); //Set to null for insert
    insert(main, companyBankContact);
  }

  /**
   * Delete CompanyBankContact.
   *
   * @param main
   * @param companyBankContact
   */
  public static final void deleteByPk(Main main, CompanyBankContact companyBankContact) {
    CompanyBankContactIs.deleteAble(main, companyBankContact); //Validation
    AppService.delete(main, CompanyBankContact.class, companyBankContact.getId());
  }

  /**
   * Delete Array of CompanyBankContact.
   *
   * @param main
   * @param companyBankContact
   */
  public static final void deleteByPkArray(Main main, CompanyBankContact[] companyBankContact) {
    for (CompanyBankContact e : companyBankContact) {
      deleteByPk(main, e);
    }
  }
}
