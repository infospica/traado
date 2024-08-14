/*
 * @(#)ConsignmentStatusService.java	1.0 Fri Jul 22 10:57:51 IST 2016 
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
import spica.scm.domain.ConsignmentStatus;
import spica.scm.validate.ConsignmentStatusIs;
import wawo.app.faces.MainView;

/**
 * ConsignmentStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:51 IST 2016
 */
public abstract class ConsignmentStatusService {

  public static final Integer RECEIVED_DELIVERED = 8;
  public static final Integer LOST_FULLY_DAMAGED_DELIVERED = 10;

  /**
   * ConsignmentStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getConsignmentStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_consignment_status", ConsignmentStatus.class, main);
    sql.main("select scm_consignment_status.id,scm_consignment_status.title,scm_consignment_status.sort_order,scm_consignment_status.display_color,scm_consignment_status.created_by,scm_consignment_status.modified_by,scm_consignment_status.created_at,scm_consignment_status.modified_at from scm_consignment_status scm_consignment_status"); //Main query
    sql.count("select count(scm_consignment_status.id) as total from scm_consignment_status scm_consignment_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_consignment_status.title", "scm_consignment_status.display_color", "scm_consignment_status.created_by", "scm_consignment_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_consignment_status.id", "scm_consignment_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_consignment_status.created_at", "scm_consignment_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ConsignmentStatus.
   *
   * @param main
   * @return List of ConsignmentStatus
   */
  public static final List<ConsignmentStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getConsignmentStatusSqlPaged(main));
  }

//  /**
//   * Return list of ConsignmentStatus based on condition
//   * @param main
//   * @return List<ConsignmentStatus>
//   */
//  public static final List<ConsignmentStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getConsignmentStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ConsignmentStatus by key.
   *
   * @param main
   * @param consignmentStatus
   * @return ConsignmentStatus
   */
  public static final ConsignmentStatus selectByPk(Main main, ConsignmentStatus consignmentStatus) {
    return (ConsignmentStatus) AppService.find(main, ConsignmentStatus.class, consignmentStatus.getId());
  }

  /**
   * Insert ConsignmentStatus.
   *
   * @param main
   * @param consignmentStatus
   */
  public static final void insert(Main main, ConsignmentStatus consignmentStatus) {
    ConsignmentStatusIs.insertAble(main, consignmentStatus);  //Validating
    AppService.insert(main, consignmentStatus);

  }

  /**
   * Update ConsignmentStatus by key.
   *
   * @param main
   * @param consignmentStatus
   * @return ConsignmentStatus
   */
  public static final ConsignmentStatus updateByPk(Main main, ConsignmentStatus consignmentStatus) {
    ConsignmentStatusIs.updateAble(main, consignmentStatus); //Validating
    return (ConsignmentStatus) AppService.update(main, consignmentStatus);
  }

  /**
   * Insert or update ConsignmentStatus
   *
   * @param main
   * @param ConsignmentStatus
   */
  public static void insertOrUpdate(Main main, ConsignmentStatus consignmentStatus) {
    if (consignmentStatus.getId() == null) {
      insert(main, consignmentStatus);
    } else {
      updateByPk(main, consignmentStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param consignmentStatus
   */
  public static void clone(Main main, ConsignmentStatus consignmentStatus) {
    consignmentStatus.setId(null); //Set to null for insert
    insert(main, consignmentStatus);
  }

  /**
   * Delete ConsignmentStatus.
   *
   * @param main
   * @param consignmentStatus
   */
  public static final void deleteByPk(Main main, ConsignmentStatus consignmentStatus) {
    ConsignmentStatusIs.deleteAble(main, consignmentStatus); //Validation
    AppService.delete(main, ConsignmentStatus.class, consignmentStatus.getId());
  }

  /**
   * Delete Array of ConsignmentStatus.
   *
   * @param main
   * @param consignmentStatus
   */
  public static final void deleteByPkArray(Main main, ConsignmentStatus[] consignmentStatus) {
    for (ConsignmentStatus e : consignmentStatus) {
      deleteByPk(main, e);
    }
  }

  public static final List<ConsignmentStatus> selectConsignmentStatus(MainView main) {
    return AppService.list(main, ConsignmentStatus.class, "select scm_consignment_status.id ,scm_consignment_status.title from scm_consignment_status where scm_consignment_status.id in(?)", new Object[]{9});
  }
}
