/*
 * @(#)TransportFreightRateUomService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
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
import spica.scm.domain.TransportFreightRateUom;
import spica.scm.validate.TransportFreightRateUomIs;

/**
 * TransportFreightRateUomService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TransportFreightRateUomService {

  /**
   * TransportFreightRateUom paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTransportFreightRateUomSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_transport_freight_rate_uom", TransportFreightRateUom.class, main);
    sql.main("select scm_transport_freight_rate_uom.id,scm_transport_freight_rate_uom.title,scm_transport_freight_rate_uom.transport_freight_ratebase_id,scm_transport_freight_rate_uom.created_by,scm_transport_freight_rate_uom.modified_by,scm_transport_freight_rate_uom.created_at,scm_transport_freight_rate_uom.modified_at from scm_transport_freight_rate_uom scm_transport_freight_rate_uom "); //Main query
    sql.count("select count(scm_transport_freight_rate_uom.id) as total from scm_transport_freight_rate_uom scm_transport_freight_rate_uom "); //Count query
    sql.join("left outer join scm_transport_freight_ratebase scm_transport_freight_rate_uomtransport_freight_ratebase_id on (scm_transport_freight_rate_uomtransport_freight_ratebase_id.id = scm_transport_freight_rate_uom.transport_freight_ratebase_id)"); //Join Query

    sql.string(new String[]{"scm_transport_freight_rate_uom.title", "scm_transport_freight_rate_uomtransport_freight_ratebase_id.title", "scm_transport_freight_rate_uom.created_by", "scm_transport_freight_rate_uom.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_transport_freight_rate_uom.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_transport_freight_rate_uom.created_at", "scm_transport_freight_rate_uom.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TransportFreightRateUom.
   *
   * @param main
   * @return List of TransportFreightRateUom
   */
  public static final List<TransportFreightRateUom> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTransportFreightRateUomSqlPaged(main));
  }

//  /**
//   * Return list of TransportFreightRateUom based on condition
//   * @param main
//   * @return List<TransportFreightRateUom>
//   */
//  public static final List<TransportFreightRateUom> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTransportFreightRateUomSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TransportFreightRateUom by key.
   *
   * @param main
   * @param transportFreightRateUom
   * @return TransportFreightRateUom
   */
  public static final TransportFreightRateUom selectByPk(Main main, TransportFreightRateUom transportFreightRateUom) {
    return (TransportFreightRateUom) AppService.find(main, TransportFreightRateUom.class, transportFreightRateUom.getId());
  }

  /**
   * Insert TransportFreightRateUom.
   *
   * @param main
   * @param transportFreightRateUom
   */
  public static final void insert(Main main, TransportFreightRateUom transportFreightRateUom) {
    TransportFreightRateUomIs.insertAble(main, transportFreightRateUom);  //Validating
    AppService.insert(main, transportFreightRateUom);

  }

  /**
   * Update TransportFreightRateUom by key.
   *
   * @param main
   * @param transportFreightRateUom
   * @return TransportFreightRateUom
   */
  public static final TransportFreightRateUom updateByPk(Main main, TransportFreightRateUom transportFreightRateUom) {
    TransportFreightRateUomIs.updateAble(main, transportFreightRateUom); //Validating
    return (TransportFreightRateUom) AppService.update(main, transportFreightRateUom);
  }

  /**
   * Insert or update TransportFreightRateUom
   *
   * @param main
   * @param transportFreightRateUom
   */
  public static void insertOrUpdate(Main main, TransportFreightRateUom transportFreightRateUom) {
    if (transportFreightRateUom.getId() == null) {
      insert(main, transportFreightRateUom);
    } else {
      updateByPk(main, transportFreightRateUom);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param transportFreightRateUom
   */
  public static void clone(Main main, TransportFreightRateUom transportFreightRateUom) {
    transportFreightRateUom.setId(null); //Set to null for insert
    insert(main, transportFreightRateUom);
  }

  /**
   * Delete TransportFreightRateUom.
   *
   * @param main
   * @param transportFreightRateUom
   */
  public static final void deleteByPk(Main main, TransportFreightRateUom transportFreightRateUom) {
    TransportFreightRateUomIs.deleteAble(main, transportFreightRateUom); //Validation
    AppService.delete(main, TransportFreightRateUom.class, transportFreightRateUom.getId());
  }

  /**
   * Delete Array of TransportFreightRateUom.
   *
   * @param main
   * @param transportFreightRateUom
   */
  public static final void deleteByPkArray(Main main, TransportFreightRateUom[] transportFreightRateUom) {
    for (TransportFreightRateUom e : transportFreightRateUom) {
      deleteByPk(main, e);
    }
  }
}
