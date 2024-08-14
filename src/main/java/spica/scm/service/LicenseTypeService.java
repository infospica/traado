/*
 * @(#)LicenseTypeService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
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
import spica.scm.domain.LicenseType;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * LicenseTypeService
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016 
 */

public abstract class LicenseTypeService {  
 
 /**
   * LicenseType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getLicenseTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_license_type", LicenseType.class, main);
    sql.main("select scm_license_type.id,scm_license_type.title,scm_license_type.sort_order,scm_license_type.created_by,scm_license_type.modified_by,scm_license_type.created_at,scm_license_type.modified_at from scm_license_type scm_license_type"); //Main query
    sql.count("select count(scm_license_type.id) as total from scm_license_type scm_license_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_license_type.title","scm_license_type.created_by","scm_license_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_license_type.id","scm_license_type.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_license_type.created_at","scm_license_type.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of LicenseType.
  * @param main
  * @return List of LicenseType
  */
  public static final List<LicenseType> listPaged(Main main) {
     return AppService.listPagedJpa(main, getLicenseTypeSqlPaged(main));
  }

//  /**
//   * Return list of LicenseType based on condition
//   * @param main
//   * @return List<LicenseType>
//   */
//  public static final List<LicenseType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getLicenseTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select LicenseType by key.
  * @param main
  * @param licenseType
  * @return LicenseType
  */
  public static final LicenseType selectByPk(Main main, LicenseType licenseType) {
    return (LicenseType) AppService.find(main, LicenseType.class, licenseType.getId());
  }

 /**
  * Insert LicenseType.
  * @param main
  * @param licenseType
  */
  public static final void insert(Main main, LicenseType licenseType) {
    insertAble(main, licenseType);  //Validating
    AppService.insert(main, licenseType);

  }

 /**
  * Update LicenseType by key.
  * @param main
  * @param licenseType
  * @return LicenseType
  */
  public static final LicenseType updateByPk(Main main, LicenseType licenseType) {
    updateAble(main, licenseType); //Validating
    return (LicenseType) AppService.update(main, licenseType);
  }

  /**
   * Insert or update LicenseType
   *
   * @param main
   * @param licenseType
   */
  public static void insertOrUpdate(Main main, LicenseType licenseType) {
    if (licenseType.getId() == null) {
      insert(main, licenseType);
    }
    else{
      updateByPk(main, licenseType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param licenseType
   */
  public static void clone(Main main, LicenseType licenseType) {
    licenseType.setId(null); //Set to null for insert
    insert(main, licenseType);
  }

 /**
  * Delete LicenseType.
  * @param main
  * @param licenseType
  */
  public static final void deleteByPk(Main main, LicenseType licenseType) {
    deleteAble(main, licenseType); //Validation
    AppService.delete(main, LicenseType.class, licenseType.getId());
  }
	
 /**
  * Delete Array of LicenseType.
  * @param main
  * @param licenseType
  */
  public static final void deleteByPkArray(Main main, LicenseType[] licenseType) {
    for (LicenseType e : licenseType) {
      deleteByPk(main, e);
    }
  }
  
  /**
   * Validate delete.
   *
   * @param main
   * @param licenseType
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, LicenseType licenseType) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param licenseType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, LicenseType licenseType) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_license_type where upper(title)=?", new Object[]{StringUtil.toUpperCase(licenseType.getTitle())})) {
      throw new UserMessageException("name.exist");
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param licenseType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, LicenseType licenseType) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_license_type where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(licenseType.getTitle()), licenseType.getId()})) {
      throw new UserMessageException("name.exist");
    }
  }

}