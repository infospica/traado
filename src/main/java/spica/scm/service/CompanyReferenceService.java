/*
 * @(#)CompanyReferenceService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
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
import spica.scm.domain.CompanyReference;
import spica.scm.domain.CompanyReferenceDoc;
import spica.scm.validate.CompanyReferenceIs;

/**
 * CompanyReferenceService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CompanyReferenceService {

  /**
   * CompanyReference paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyReferenceSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_reference", CompanyReference.class, main);
    sql.main("select scm_company_reference.id,scm_company_reference.company_id,scm_company_reference.reference_title,scm_company_reference.reference_description,scm_company_reference.reference_note,scm_company_reference.created_by,scm_company_reference.modified_by,scm_company_reference.created_at,scm_company_reference.modified_at from scm_company_reference scm_company_reference "); //Main query
    sql.count("select count(scm_company_reference.id) from scm_company_reference scm_company_reference "); //Count query
    sql.join("left outer join scm_company scm_company_referencecompany_id on (scm_company_referencecompany_id.id = scm_company_reference.company_id)"); //Join Query

    sql.string(new String[]{"scm_company_referencecompany_id.company_name", "scm_company_reference.reference_title", "scm_company_reference.reference_description", "scm_company_reference.reference_note", "scm_company_reference.created_by", "scm_company_reference.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company_reference.id"}); //Number search or sort fields
    sql.date(new String[]{"scm_company_reference.created_at", "scm_company_reference.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CompanyReference.
   *
   * @param main
   * @return List of CompanyReference
   */
  public static final List<CompanyReference> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCompanyReferenceSqlPaged(main));
  }

  /**
   * Return all reference of a company.
   *
   * @param main
   * @param company
   * @return
   */
  public static final List<CompanyReference> referenceListByCompany(Main main, Company company) {
    SqlPage sql = getCompanyReferenceSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_reference.company_id=?");
    sql.param(company.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of CompanyReference based on condition
//   * @param main
//   * @return List<CompanyReference>
//   */
//  public static final List<CompanyReference> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyReferenceSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select CompanyReference by key.
   *
   * @param main
   * @param companyReference
   * @return CompanyReference
   */
  public static final CompanyReference selectByPk(Main main, CompanyReference companyReference) {
    return (CompanyReference) AppService.find(main, CompanyReference.class, companyReference.getId());
  }

  /**
   * Insert CompanyReference.
   *
   * @param main
   * @param companyReference
   */
  public static final void insert(Main main, CompanyReference companyReference) {
    CompanyReferenceIs.insertAble(main, companyReference);  //Validating
    AppService.insert(main, companyReference);

  }

  /**
   * Update CompanyReference by key.
   *
   * @param main
   * @param companyReference
   * @return CompanyReference
   */
  public static final CompanyReference updateByPk(Main main, CompanyReference companyReference) {
    CompanyReferenceIs.updateAble(main, companyReference); //Validating
    return (CompanyReference) AppService.update(main, companyReference);
  }

  /**
   * Insert or update CompanyReference
   *
   * @param main
   * @param CompanyReference
   */
  public static void insertOrUpdate(Main main, CompanyReference companyReference) {
    if (companyReference.getId() == null) {
      insert(main, companyReference);
    }
    updateByPk(main, companyReference);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyReference
   */
  public static void clone(Main main, CompanyReference companyReference) {
    companyReference.setId(null); //Set to null for insert
    insert(main, companyReference);
  }

  /**
   * Delete CompanyReference.
   *
   * @param main
   * @param companyReference
   */
  public static final void deleteByPk(Main main, CompanyReference companyReference) {
    CompanyReferenceIs.deleteAble(main, companyReference); //Validation
    AppService.deleteSql(main, CompanyReferenceDoc.class, "delete from scm_company_reference_doc scm_company_reference_doc where scm_company_reference_doc.company_reference_id=?", new Object[]{companyReference.getId()});
    AppService.delete(main, CompanyReference.class, companyReference.getId());
  }

  /**
   * Delete Array of CompanyReference.
   *
   * @param main
   * @param companyReference
   */
  public static final void deleteByPkArray(Main main, CompanyReference[] companyReference) {
    for (CompanyReference e : companyReference) {
      deleteByPk(main, e);
    }
  }
}
