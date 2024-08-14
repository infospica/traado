/*
 * @(#)TransportFreightRatebaseService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
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
import spica.scm.domain.TransportFreightRatebase;
import spica.scm.validate.TransportFreightRatebaseIs;

/**
 * TransportFreightRatebaseService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TransportFreightRatebaseService {

  /**
   * TransportFreightRatebase paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTransportFreightRatebaseSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_transport_freight_ratebase", TransportFreightRatebase.class, main);
    sql.main("select scm_transport_freight_ratebase.id,scm_transport_freight_ratebase.title,scm_transport_freight_ratebase.created_by,scm_transport_freight_ratebase.modified_by,scm_transport_freight_ratebase.created_at,scm_transport_freight_ratebase.modified_at from scm_transport_freight_ratebase scm_transport_freight_ratebase"); //Main query
    sql.count("select count(scm_transport_freight_ratebase.id) as total from scm_transport_freight_ratebase scm_transport_freight_ratebase"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_transport_freight_ratebase.title", "scm_transport_freight_ratebase.created_by", "scm_transport_freight_ratebase.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_transport_freight_ratebase.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_transport_freight_ratebase.created_at", "scm_transport_freight_ratebase.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TransportFreightRatebase.
   *
   * @param main
   * @return List of TransportFreightRatebase
   */
  public static final List<TransportFreightRatebase> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTransportFreightRatebaseSqlPaged(main));
  }

//  /**
//   * Return list of TransportFreightRatebase based on condition
//   * @param main
//   * @return List<TransportFreightRatebase>
//   */
//  public static final List<TransportFreightRatebase> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTransportFreightRatebaseSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TransportFreightRatebase by key.
   *
   * @param main
   * @param transportFreightRatebase
   * @return TransportFreightRatebase
   */
  public static final TransportFreightRatebase selectByPk(Main main, TransportFreightRatebase transportFreightRatebase) {
    return (TransportFreightRatebase) AppService.find(main, TransportFreightRatebase.class, transportFreightRatebase.getId());
  }

  /**
   * Insert TransportFreightRatebase.
   *
   * @param main
   * @param transportFreightRatebase
   */
  public static final void insert(Main main, TransportFreightRatebase transportFreightRatebase) {
    TransportFreightRatebaseIs.insertAble(main, transportFreightRatebase);  //Validating
    AppService.insert(main, transportFreightRatebase);

  }

  /**
   * Update TransportFreightRatebase by key.
   *
   * @param main
   * @param transportFreightRatebase
   * @return TransportFreightRatebase
   */
  public static final TransportFreightRatebase updateByPk(Main main, TransportFreightRatebase transportFreightRatebase) {
    TransportFreightRatebaseIs.updateAble(main, transportFreightRatebase); //Validating
    return (TransportFreightRatebase) AppService.update(main, transportFreightRatebase);
  }

  /**
   * Insert or update TransportFreightRatebase
   *
   * @param main
   * @param transportFreightRatebase
   */
  public static void insertOrUpdate(Main main, TransportFreightRatebase transportFreightRatebase) {
    if (transportFreightRatebase.getId() == null) {
      insert(main, transportFreightRatebase);
    } else {
      updateByPk(main, transportFreightRatebase);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param transportFreightRatebase
   */
  public static void clone(Main main, TransportFreightRatebase transportFreightRatebase) {
    transportFreightRatebase.setId(null); //Set to null for insert
    insert(main, transportFreightRatebase);
  }

  /**
   * Delete TransportFreightRatebase.
   *
   * @param main
   * @param transportFreightRatebase
   */
  public static final void deleteByPk(Main main, TransportFreightRatebase transportFreightRatebase) {
    TransportFreightRatebaseIs.deleteAble(main, transportFreightRatebase); //Validation
    AppService.delete(main, TransportFreightRatebase.class, transportFreightRatebase.getId());
  }

  /**
   * Delete Array of TransportFreightRatebase.
   *
   * @param main
   * @param transportFreightRatebase
   */
  public static final void deleteByPkArray(Main main, TransportFreightRatebase[] transportFreightRatebase) {
    for (TransportFreightRatebase e : transportFreightRatebase) {
      deleteByPk(main, e);
    }
  }
}
