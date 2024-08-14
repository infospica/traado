/*
 * @(#)CompanyReferenceDocService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.CompanyReference;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CompanyReferenceDoc;
import spica.scm.validate.CompanyReferenceDocIs;

/**
 * CompanyReferenceDocService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CompanyReferenceDocService {

  /**
   * CompanyReferenceDoc paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyReferenceDocSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_reference_doc", CompanyReferenceDoc.class, main);
    sql.main("select scm_company_reference_doc.id,scm_company_reference_doc.company_reference_id,scm_company_reference_doc.document_title,scm_company_reference_doc.document_file_path from scm_company_reference_doc scm_company_reference_doc "); //Main query
    sql.count("select count(scm_company_reference_doc.id) from scm_company_reference_doc scm_company_reference_doc "); //Count query
    sql.join("left outer join scm_company_reference scm_company_reference_doccompany_reference_id on (scm_company_reference_doccompany_reference_id.id = scm_company_reference_doc.company_reference_id)"); //Join Query

    sql.string(new String[]{"scm_company_reference_doccompany_reference_id.reference_title", "scm_company_reference_doc.document_file_path","scm_company_reference_doc.document_title"}); //String search or sort fields
    sql.number(new String[]{"scm_company_reference_doc.id"}); //Number search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CompanyReferenceDoc.
   *
   * @param main
   * @param companyReference
   * @return List of CompanyReferenceDoc
   */
  public static final List<CompanyReferenceDoc> listPaged(Main main, CompanyReference companyReference) {
    SqlPage sql = getCompanyReferenceDocSqlPaged(main);
    if (companyReference.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_reference_doc.company_reference_id=?");
    sql.param(companyReference.getId());
    return AppService.listPagedJpa(main, sql);
  }

  /**
   * Return all vendor reference doc of a vendor.
   *
   * @param main
   * @param companyReference
   * @return
   */
  public static final List<CompanyReferenceDoc> referenceDocListByCompany(Main main, CompanyReference companyReference) {
    SqlPage sql = getCompanyReferenceDocSqlPaged(main);
    if (companyReference.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_reference_doc.company_reference_id=?");
    sql.param(companyReference.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of CompanyReferenceDoc based on condition
//   * @param main
//   * @return List<CompanyReferenceDoc>
//   */
//  public static final List<CompanyReferenceDoc> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyReferenceDocSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select CompanyReferenceDoc by key.
   *
   * @param main
   * @param companyReferenceDoc
   * @return CompanyReferenceDoc
   */
  public static final CompanyReferenceDoc selectByPk(Main main, CompanyReferenceDoc companyReferenceDoc) {
    return (CompanyReferenceDoc) AppService.find(main, CompanyReferenceDoc.class, companyReferenceDoc.getId());
  }

  /**
   * Insert CompanyReferenceDoc.
   *
   * @param main
   * @param companyReferenceDoc
   */
  public static final void insert(Main main, CompanyReferenceDoc companyReferenceDoc) {
    CompanyReferenceDocIs.insertAble(main, companyReferenceDoc);  //Validating
    AppService.insert(main, companyReferenceDoc);

  }

  /**
   * Update CompanyReferenceDoc by key.
   *
   * @param main
   * @param companyReferenceDoc
   * @return CompanyReferenceDoc
   */
  public static final CompanyReferenceDoc updateByPk(Main main, CompanyReferenceDoc companyReferenceDoc) {
    CompanyReferenceDocIs.updateAble(main, companyReferenceDoc); //Validating
    return (CompanyReferenceDoc) AppService.update(main, companyReferenceDoc);
  }

  /**
   * Insert or update CompanyReferenceDoc
   *
   * @param main
   * @param CompanyReferenceDoc
   */
  public static void insertOrUpdate(Main main, CompanyReferenceDoc companyReferenceDoc) {
    if (companyReferenceDoc.getId() == null) {
      insert(main, companyReferenceDoc);
    }
    updateByPk(main, companyReferenceDoc);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyReferenceDoc
   */
  public static void clone(Main main, CompanyReferenceDoc companyReferenceDoc) {
    companyReferenceDoc.setId(null); //Set to null for insert
    insert(main, companyReferenceDoc);
  }

  /**
   * Delete CompanyReferenceDoc.
   *
   * @param main
   * @param companyReferenceDoc
   */
  public static final void deleteByPk(Main main, CompanyReferenceDoc companyReferenceDoc) {
    CompanyReferenceDocIs.deleteAble(main, companyReferenceDoc); //Validation
    AppService.delete(main, CompanyReferenceDoc.class, companyReferenceDoc.getId());
  }

  /**
   * Delete Array of CompanyReferenceDoc.
   *
   * @param main
   * @param companyReferenceDoc
   */
  public static final void deleteByPkArray(Main main, CompanyReferenceDoc[] companyReferenceDoc) {
    for (CompanyReferenceDoc e : companyReferenceDoc) {
      deleteByPk(main, e);
    }
  }
}
