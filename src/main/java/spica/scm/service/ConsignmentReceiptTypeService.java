/*
 * @(#)ConsignmentReceiptTypeService.java	1.0 Fri Jul 22 10:57:51 IST 2016 
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
import spica.scm.domain.ConsignmentReceiptType;
import spica.scm.validate.ConsignmentReceiptTypeIs;

/**
 * ConsignmentReceiptTypeService
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:51 IST 2016 
 */

public abstract class ConsignmentReceiptTypeService {  
 
 /**
   * ConsignmentReceiptType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getConsignmentReceiptTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_consignment_receipt_type", ConsignmentReceiptType.class, main);
    sql.main("select scm_consignment_receipt_type.id,scm_consignment_receipt_type.title,scm_consignment_receipt_type.transport_mode_id,scm_consignment_receipt_type.created_by,scm_consignment_receipt_type.modified_by,scm_consignment_receipt_type.created_at,scm_consignment_receipt_type.modified_at from scm_consignment_receipt_type scm_consignment_receipt_type "); //Main query
    sql.count("select count(scm_consignment_receipt_type.id) as total from scm_consignment_receipt_type scm_consignment_receipt_type "); //Count query
    sql.join("left outer join scm_transport_mode scm_consignment_receipt_typetransport_mode_id on (scm_consignment_receipt_typetransport_mode_id.id = scm_consignment_receipt_type.transport_mode_id)"); //Join Query

    sql.string(new String[]{"scm_consignment_receipt_type.title","scm_consignment_receipt_typetransport_mode_id.title","scm_consignment_receipt_type.created_by","scm_consignment_receipt_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_consignment_receipt_type.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_consignment_receipt_type.created_at","scm_consignment_receipt_type.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of ConsignmentReceiptType.
  * @param main
  * @return List of ConsignmentReceiptType
  */
  public static final List<ConsignmentReceiptType> listPaged(Main main) {
     return AppService.listPagedJpa(main, getConsignmentReceiptTypeSqlPaged(main));
  }

//  /**
//   * Return list of ConsignmentReceiptType based on condition
//   * @param main
//   * @return List<ConsignmentReceiptType>
//   */
//  public static final List<ConsignmentReceiptType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getConsignmentReceiptTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select ConsignmentReceiptType by key.
  * @param main
  * @param consignmentReceiptType
  * @return ConsignmentReceiptType
  */
  public static final ConsignmentReceiptType selectByPk(Main main, ConsignmentReceiptType consignmentReceiptType) {
    return (ConsignmentReceiptType) AppService.find(main, ConsignmentReceiptType.class, consignmentReceiptType.getId());
  }

 /**
  * Insert ConsignmentReceiptType.
  * @param main
  * @param consignmentReceiptType
  */
  public static final void insert(Main main, ConsignmentReceiptType consignmentReceiptType) {
    ConsignmentReceiptTypeIs.insertAble(main, consignmentReceiptType);  //Validating
    AppService.insert(main, consignmentReceiptType);

  }

 /**
  * Update ConsignmentReceiptType by key.
  * @param main
  * @param consignmentReceiptType
  * @return ConsignmentReceiptType
  */
  public static final ConsignmentReceiptType updateByPk(Main main, ConsignmentReceiptType consignmentReceiptType) {
    ConsignmentReceiptTypeIs.updateAble(main, consignmentReceiptType); //Validating
    return (ConsignmentReceiptType) AppService.update(main, consignmentReceiptType);
  }

  /**
   * Insert or update ConsignmentReceiptType
   *
   * @param main
   * @param ConsignmentReceiptType
   */
  public static void insertOrUpdate(Main main, ConsignmentReceiptType consignmentReceiptType) {
    if (consignmentReceiptType.getId() == null) {
      insert(main, consignmentReceiptType);
    }
    else{
        updateByPk(main, consignmentReceiptType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param consignmentReceiptType
   */
  public static void clone(Main main, ConsignmentReceiptType consignmentReceiptType) {
    consignmentReceiptType.setId(null); //Set to null for insert
    insert(main, consignmentReceiptType);
  }

 /**
  * Delete ConsignmentReceiptType.
  * @param main
  * @param consignmentReceiptType
  */
  public static final void deleteByPk(Main main, ConsignmentReceiptType consignmentReceiptType) {
    ConsignmentReceiptTypeIs.deleteAble(main, consignmentReceiptType); //Validation
    AppService.delete(main, ConsignmentReceiptType.class, consignmentReceiptType.getId());
  }
	
 /**
  * Delete Array of ConsignmentReceiptType.
  * @param main
  * @param consignmentReceiptType
  */
  public static final void deleteByPkArray(Main main, ConsignmentReceiptType[] consignmentReceiptType) {
    for (ConsignmentReceiptType e : consignmentReceiptType) {
      deleteByPk(main, e);
    }
  }
}
