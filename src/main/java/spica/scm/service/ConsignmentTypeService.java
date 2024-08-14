/*
 * @(#)ConsignmentTypeService.java	1.0 Fri Jul 22 10:57:51 IST 2016 
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
import spica.scm.domain.ConsignmentType;
import spica.scm.validate.ConsignmentTypeIs;

/**
 * ConsignmentTypeService
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:51 IST 2016 
 */

public abstract class ConsignmentTypeService {  
 
 /**
   * ConsignmentType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getConsignmentTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_consignment_type", ConsignmentType.class, main);
    sql.main("select scm_consignment_type.id,scm_consignment_type.title,scm_consignment_type.created_by,scm_consignment_type.modified_by,scm_consignment_type.created_at,scm_consignment_type.modified_at from scm_consignment_type scm_consignment_type"); //Main query
    sql.count("select count(scm_consignment_type.id) as total from scm_consignment_type scm_consignment_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_consignment_type.title","scm_consignment_type.created_by","scm_consignment_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_consignment_type.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_consignment_type.created_at","scm_consignment_type.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of ConsignmentType.
  * @param main
  * @return List of ConsignmentType
  */
  public static final List<ConsignmentType> listPaged(Main main) {
     return AppService.listPagedJpa(main, getConsignmentTypeSqlPaged(main));
  }

//  /**
//   * Return list of ConsignmentType based on condition
//   * @param main
//   * @return List<ConsignmentType>
//   */
//  public static final List<ConsignmentType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getConsignmentTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select ConsignmentType by key.
  * @param main
  * @param consignmentType
  * @return ConsignmentType
  */
  public static final ConsignmentType selectByPk(Main main, ConsignmentType consignmentType) {
    return (ConsignmentType) AppService.find(main, ConsignmentType.class, consignmentType.getId());
  }

 /**
  * Insert ConsignmentType.
  * @param main
  * @param consignmentType
  */
  public static final void insert(Main main, ConsignmentType consignmentType) {
    ConsignmentTypeIs.insertAble(main, consignmentType);  //Validating
    AppService.insert(main, consignmentType);

  }

 /**
  * Update ConsignmentType by key.
  * @param main
  * @param consignmentType
  * @return ConsignmentType
  */
  public static final ConsignmentType updateByPk(Main main, ConsignmentType consignmentType) {
    ConsignmentTypeIs.updateAble(main, consignmentType); //Validating
    return (ConsignmentType) AppService.update(main, consignmentType);
  }

  /**
   * Insert or update ConsignmentType
   *
   * @param main
   * @param ConsignmentType
   */
  public static void insertOrUpdate(Main main, ConsignmentType consignmentType) {
    if (consignmentType.getId() == null) {
      insert(main, consignmentType);
    }
    else{
        updateByPk(main, consignmentType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param consignmentType
   */
  public static void clone(Main main, ConsignmentType consignmentType) {
    consignmentType.setId(null); //Set to null for insert
    insert(main, consignmentType);
  }

 /**
  * Delete ConsignmentType.
  * @param main
  * @param consignmentType
  */
  public static final void deleteByPk(Main main, ConsignmentType consignmentType) {
    ConsignmentTypeIs.deleteAble(main, consignmentType); //Validation
    AppService.delete(main, ConsignmentType.class, consignmentType.getId());
  }
	
 /**
  * Delete Array of ConsignmentType.
  * @param main
  * @param consignmentType
  */
  public static final void deleteByPkArray(Main main, ConsignmentType[] consignmentType) {
    for (ConsignmentType e : consignmentType) {
      deleteByPk(main, e);
    }
  }
}
