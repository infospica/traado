/*
 * @(#)ConsignmentDocTypeService.java	1.0 Fri Jul 22 10:57:51 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ConsignmentDocType;
import spica.scm.validate.ConsignmentDocTypeIs;

/**
 * ConsignmentDocTypeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:51 IST 2016
 */
public abstract class ConsignmentDocTypeService {

  /**
   * ConsignmentDocType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getConsignmentDocTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_consignment_doc_type", ConsignmentDocType.class, main);
    sql.main("select scm_consignment_doc_type.id,scm_consignment_doc_type.title,scm_consignment_doc_type.transport_mode_id,scm_consignment_doc_type.document_category,scm_consignment_doc_type.created_by,scm_consignment_doc_type.modified_by,scm_consignment_doc_type.created_at,scm_consignment_doc_type.modified_at from scm_consignment_doc_type scm_consignment_doc_type "); //Main query
    sql.count("select count(scm_consignment_doc_type.id) as total from scm_consignment_doc_type scm_consignment_doc_type "); //Count query
    sql.join("left outer join scm_transport_mode scm_consignment_doc_typetransport_mode_id on (scm_consignment_doc_typetransport_mode_id.id = scm_consignment_doc_type.transport_mode_id)left outer join scm_state scm_consignment_doc_typestate_id on (scm_consignment_doc_typestate_id.id = scm_consignment_doc_type.state_id)"); //Join Query

    sql.string(new String[]{"scm_consignment_doc_type.title", "scm_consignment_doc_typetransport_mode_id.title", "scm_consignment_doc_type.created_by", "scm_consignment_doc_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_consignment_doc_type.id", "scm_consignment_doc_type.document_category"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_consignment_doc_type.created_at", "scm_consignment_doc_type.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ConsignmentDocType.
   *
   * @param main
   * @return List of ConsignmentDocType
   */
  public static final List<ConsignmentDocType> listPaged(Main main) {
    return AppService.listPagedJpa(main, getConsignmentDocTypeSqlPaged(main));
  }

//  /**
//   * Return list of ConsignmentDocType based on condition
//   * @param main
//   * @return List<ConsignmentDocType>
//   */
//  public static final List<ConsignmentDocType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getConsignmentDocTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ConsignmentDocType by key.
   *
   * @param main
   * @param consignmentDocType
   * @return ConsignmentDocType
   */
  public static final ConsignmentDocType selectByPk(Main main, ConsignmentDocType consignmentDocType) {
    return (ConsignmentDocType) AppService.find(main, ConsignmentDocType.class, consignmentDocType.getId());
  }

  /**
   * Insert ConsignmentDocType.
   *
   * @param main
   * @param consignmentDocType
   */
  public static final void insert(Main main, ConsignmentDocType consignmentDocType) {
    ConsignmentDocTypeIs.insertAble(main, consignmentDocType);  //Validating
    AppService.insert(main, consignmentDocType);

  }

  /**
   * Update ConsignmentDocType by key.
   *
   * @param main
   * @param consignmentDocType
   * @return ConsignmentDocType
   */
  public static final ConsignmentDocType updateByPk(Main main, ConsignmentDocType consignmentDocType) {
    ConsignmentDocTypeIs.updateAble(main, consignmentDocType); //Validating
    return (ConsignmentDocType) AppService.update(main, consignmentDocType);
  }

  /**
   * Insert or update ConsignmentDocType
   *
   * @param main
   * @param ConsignmentDocType
   */
  public static void insertOrUpdate(Main main, ConsignmentDocType consignmentDocType) {
    if (consignmentDocType.getId() == null) {
      insert(main, consignmentDocType);
    } else {
      updateByPk(main, consignmentDocType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param consignmentDocType
   */
  public static void clone(Main main, ConsignmentDocType consignmentDocType) {
    consignmentDocType.setId(null); //Set to null for insert
    insert(main, consignmentDocType);
  }

  /**
   * Delete ConsignmentDocType.
   *
   * @param main
   * @param consignmentDocType
   */
  public static final void deleteByPk(Main main, ConsignmentDocType consignmentDocType) {
    ConsignmentDocTypeIs.deleteAble(main, consignmentDocType); //Validation
    AppService.delete(main, ConsignmentDocType.class, consignmentDocType.getId());
  }

  /**
   * Delete Array of ConsignmentDocType.
   *
   * @param main
   * @param consignmentDocType
   */
  public static final void deleteByPkArray(Main main, ConsignmentDocType[] consignmentDocType) {
    for (ConsignmentDocType e : consignmentDocType) {
      deleteByPk(main, e);
    }
  }

  public static List<ConsignmentDocType> selectDocTypeByTransportMode(Main main, Integer transportModeId, Integer stateId) {
    if (transportModeId != null) {
      return AppService.list(main, ConsignmentDocType.class, "select * from scm_consignment_doc_type where transport_mode_id = ? and document_category != ? and state_id=?", new Object[]{transportModeId, 2, stateId});
    } else {
      return null;
    }
  }
}
