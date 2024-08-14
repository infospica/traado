/*
 * @(#)CompanyTypeService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.scm.service;

import java.util.List;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CompanyType;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * CompanyTypeService
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016 
 */

public abstract class CompanyTypeService {  
 
 /**
   * CompanyType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_type", CompanyType.class, main);
    sql.main("select scm_company_type.id,scm_company_type.title,scm_company_type.sort_order,scm_company_type.created_by,scm_company_type.modified_by,scm_company_type.created_at,scm_company_type.modified_at from scm_company_type scm_company_type"); //Main query
    sql.count("select count(scm_company_type.id) as total from scm_company_type scm_company_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_company_type.title","scm_company_type.created_by","scm_company_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company_type.id","scm_company_type.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_company_type.created_at","scm_company_type.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of CompanyType.
  * @param main
  * @return List of CompanyType
  */
  public static final List<CompanyType> listPaged(Main main) {
     return AppService.listPagedJpa(main, getCompanyTypeSqlPaged(main));
  }

//  /**
//   * Return list of CompanyType based on condition
//   * @param main
//   * @return List<CompanyType>
//   */
//  public static final List<CompanyType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select CompanyType by key.
  * @param main
  * @param companyType
  * @return CompanyType
  */
  public static final CompanyType selectByPk(Main main, CompanyType companyType) {
    return (CompanyType) AppService.find(main, CompanyType.class, companyType.getId());
  }

 /**
  * Insert CompanyType.
  * @param main
  * @param companyType
  */
  public static final void insert(Main main, CompanyType companyType) {
    insertAble(main, companyType);  //Validating
    AppService.insert(main, companyType);

  }

 /**
  * Update CompanyType by key.
  * @param main
  * @param companyType
  * @return CompanyType
  */
  public static final CompanyType updateByPk(Main main, CompanyType companyType) {
    updateAble(main, companyType); //Validating
    return (CompanyType) AppService.update(main, companyType);
  }

  /**
   * Insert or update CompanyType
   *
   * @param main
   * @param companyType
   */
  public static void insertOrUpdate(Main main, CompanyType companyType) {
    if (companyType.getId() == null) {
      insert(main, companyType);
    }
    else{
      updateByPk(main, companyType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyType
   */
  public static void clone(Main main, CompanyType companyType) {
    companyType.setId(null); //Set to null for insert
    insert(main, companyType);
  }

 /**
  * Delete CompanyType.
  * @param main
  * @param companyType
  */
  public static final void deleteByPk(Main main, CompanyType companyType) {
    deleteAble(main, companyType); //Validation
    AppService.delete(main, CompanyType.class, companyType.getId());
  }
	
 /**
  * Delete Array of CompanyType.
  * @param main
  * @param companyType
  */
  public static final void deleteByPkArray(Main main, CompanyType[] companyType) {
    for (CompanyType e : companyType) {
      deleteByPk(main, e);
    }
  }
  /**
   * Validate delete.
   *
   * @param main
   * @param companyType
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, CompanyType companyType) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param companyType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, CompanyType companyType) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_company_type where upper(title)=?", new Object[]{StringUtil.toUpperCase(companyType.getTitle())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param companyType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, CompanyType companyType) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_company_type where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(companyType.getTitle()), companyType.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

}
