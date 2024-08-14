/*
 * @(#)TransportModeService.java	1.0 Fri Jul 22 10:57:51 IST 2016 
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
import spica.scm.domain.TransportMode;
import spica.scm.validate.TransportModeIs;

/**
 * TransportModeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:51 IST 2016
 */
public abstract class TransportModeService {

  /**
   * TransportMode paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTransportModeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_transport_mode", TransportMode.class, main);
    sql.main("select scm_transport_mode.id,scm_transport_mode.title,scm_transport_mode.sort_order,scm_transport_mode.created_by,scm_transport_mode.modified_by,scm_transport_mode.created_at,scm_transport_mode.modified_at from scm_transport_mode scm_transport_mode"); //Main query
    sql.count("select count(scm_transport_mode.id) as total from scm_transport_mode scm_transport_mode"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_transport_mode.title", "scm_transport_mode.created_by", "scm_transport_mode.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_transport_mode.id", "scm_transport_mode.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_transport_mode.created_at", "scm_transport_mode.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TransportMode.
   *
   * @param main
   * @return List of TransportMode
   */
  public static final List<TransportMode> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTransportModeSqlPaged(main));
  }

//  /**
//   * Return list of TransportMode based on condition
//   * @param main
//   * @return List<TransportMode>
//   */
//  public static final List<TransportMode> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTransportModeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TransportMode by key.
   *
   * @param main
   * @param transportMode
   * @return TransportMode
   */
  public static final TransportMode selectByPk(Main main, TransportMode transportMode) {
    return (TransportMode) AppService.find(main, TransportMode.class, transportMode.getId());
  }

  /**
   * Insert TransportMode.
   *
   * @param main
   * @param transportMode
   */
  public static final void insert(Main main, TransportMode transportMode) {
    TransportModeIs.insertAble(main, transportMode);  //Validating
    AppService.insert(main, transportMode);

  }

  /**
   * Update TransportMode by key.
   *
   * @param main
   * @param transportMode
   * @return TransportMode
   */
  public static final TransportMode updateByPk(Main main, TransportMode transportMode) {
    TransportModeIs.updateAble(main, transportMode); //Validating
    return (TransportMode) AppService.update(main, transportMode);
  }

  /**
   * Insert or update TransportMode
   *
   * @param main
   * @param TransportMode
   */
  public static void insertOrUpdate(Main main, TransportMode transportMode) {
    if (transportMode.getId() == null) {
      insert(main, transportMode);
    } else {
      updateByPk(main, transportMode);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param transportMode
   */
  public static void clone(Main main, TransportMode transportMode) {
    transportMode.setId(null); //Set to null for insert
    insert(main, transportMode);
  }

  /**
   * Delete TransportMode.
   *
   * @param main
   * @param transportMode
   */
  public static final void deleteByPk(Main main, TransportMode transportMode) {
    TransportModeIs.deleteAble(main, transportMode); //Validation
    AppService.delete(main, TransportMode.class, transportMode.getId());
  }

  /**
   * Delete Array of TransportMode.
   *
   * @param main
   * @param transportMode
   */
  public static final void deleteByPkArray(Main main, TransportMode[] transportMode) {
    for (TransportMode e : transportMode) {
      deleteByPk(main, e);
    }
  }
}
