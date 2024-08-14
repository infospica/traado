/*
 * @(#)TransFreightrateUomrangeService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
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
import spica.scm.domain.TransFreightrateUomrange;
import spica.scm.domain.TransporterFreightRate;
import spica.scm.validate.TransFreightrateUomrangeIs;

/**
 * TransFreightrateUomrangeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TransFreightrateUomrangeService {

  /**
   * TransFreightrateUomrange paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTransFreightrateUomrangeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_trans_freightrate_uomrange", TransFreightrateUomrange.class, main);
    sql.main("select scm_trans_freightrate_uomrange.id,scm_trans_freightrate_uomrange.transporter_freight_rate_id,scm_trans_freightrate_uomrange.range_from,scm_trans_freightrate_uomrange.range_to,scm_trans_freightrate_uomrange.rate_fixed_per_base_uom from scm_trans_freightrate_uomrange scm_trans_freightrate_uomrange"); //Main query
    sql.count("select count(scm_trans_freightrate_uomrange.id) as total from scm_trans_freightrate_uomrange scm_trans_freightrate_uomrange"); //Count query
    sql.join(""); //Join Query

    sql.string(null); //String search or sort fields
    sql.number(new String[]{"scm_trans_freightrate_uomrange.id", "scm_trans_freightrate_uomrange.transporter_freight_rate_id", "scm_trans_freightrate_uomrange.range_from", "scm_trans_freightrate_uomrange.range_to", "scm_trans_freightrate_uomrange.rate_fixed_per_base_uom"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TransFreightrateUomrange.
   *
   * @param main
   * @return List of TransFreightrateUomrange
   */
  public static final List<TransFreightrateUomrange> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTransFreightrateUomrangeSqlPaged(main));
  }

//  /**
//   * Return list of TransFreightrateUomrange based on condition
//   * @param main
//   * @return List<TransFreightrateUomrange>
//   */
//  public static final List<TransFreightrateUomrange> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTransFreightrateUomrangeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TransFreightrateUomrange by key.
   *
   * @param main
   * @param transFreightrateUomrange
   * @return TransFreightrateUomrange
   */
  public static final TransFreightrateUomrange selectByPk(Main main, TransFreightrateUomrange transFreightrateUomrange) {
    return (TransFreightrateUomrange) AppService.find(main, TransFreightrateUomrange.class, transFreightrateUomrange.getId());
  }

  /**
   * Insert TransFreightrateUomrange.
   *
   * @param main
   * @param transFreightrateUomrange
   */
  public static final void insert(Main main, TransFreightrateUomrange transFreightrateUomrange) {
    TransFreightrateUomrangeIs.insertAble(main, transFreightrateUomrange);  //Validating
    AppService.insert(main, transFreightrateUomrange);

  }

  /**
   * Update TransFreightrateUomrange by key.
   *
   * @param main
   * @param transFreightrateUomrange
   * @return TransFreightrateUomrange
   */
  public static final TransFreightrateUomrange updateByPk(Main main, TransFreightrateUomrange transFreightrateUomrange) {
    TransFreightrateUomrangeIs.updateAble(main, transFreightrateUomrange); //Validating
    return (TransFreightrateUomrange) AppService.update(main, transFreightrateUomrange);
  }

  /**
   * Insert or update TransFreightrateUomrange
   *
   * @param main
   * @param transFreightrateUomrange
   */
  public static void insertOrUpdate(Main main, TransFreightrateUomrange transFreightrateUomrange) {
    if (transFreightrateUomrange.getId() == null) {
      insert(main, transFreightrateUomrange);
    } else {
      updateByPk(main, transFreightrateUomrange);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param transFreightrateUomrange
   */
  public static void clone(Main main, TransFreightrateUomrange transFreightrateUomrange) {
    transFreightrateUomrange.setId(null); //Set to null for insert
    insert(main, transFreightrateUomrange);
  }

  /**
   * Delete TransFreightrateUomrange.
   *
   * @param main
   * @param transFreightrateUomrange
   */
  public static final void deleteByPk(Main main, TransFreightrateUomrange transFreightrateUomrange) {
    TransFreightrateUomrangeIs.deleteAble(main, transFreightrateUomrange); //Validation
    AppService.delete(main, TransFreightrateUomrange.class, transFreightrateUomrange.getId());
  }

  /**
   * Delete Array of TransFreightrateUomrange.
   *
   * @param main
   * @param transFreightrateUomrange
   */
  public static final void deleteByPkArray(Main main, TransFreightrateUomrange[] transFreightrateUomrange) {
    for (TransFreightrateUomrange e : transFreightrateUomrange) {
      deleteByPk(main, e);
    }
  }

  public static final TransFreightrateUomrange selectTransFreightrateUomrange(Main main, long rangeId) {
    return (TransFreightrateUomrange) AppService.single(main, TransFreightrateUomrange.class, "select * from scm_trans_freightrate_uomrange scm_trans_freightrate_uomrange where scm_trans_freightrate_uomrange.transporter_freight_rate_id = ? ", new Object[]{rangeId});
  }

  public static final List<TransFreightrateUomrange> getTransporterRateList(Main main, TransporterFreightRate transporterFreightRate) {
    SqlPage sql = getTransFreightrateUomrangeSqlPaged(main);
    if (transporterFreightRate.getId() == null) {
      return null;
    }
    sql.cond("where scm_trans_freightrate_uomrange.transporter_freight_rate_id=? order by range_from desc");
    sql.param(transporterFreightRate.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static final void deleteTransFreightrateUomrange(Main main, TransFreightrateUomrange TransFreightrateUomrange) {
    AppService.deleteSql(main, TransFreightrateUomrange.class, "delete from scm_trans_freightrate_uomrange where id = ?", new Object[]{TransFreightrateUomrange.getId()});
  }
}
