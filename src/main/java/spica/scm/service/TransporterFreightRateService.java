/*
 * @(#)TransporterFreightRateService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.TransFreightrateUomrange;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.TransporterFreightRate;
import spica.scm.validate.TransporterFreightRateIs;

/**
 * TransporterFreightRateService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TransporterFreightRateService {

  /**
   * TransporterFreightRate paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTransporterFreightRateSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_transporter_freight_rate", TransporterFreightRate.class, main);
    sql.main("select scm_transporter_freight_rate.id,scm_transporter_freight_rate.transporter_rate_card_id,scm_transporter_freight_rate.destination_state_id,scm_transporter_freight_rate.destination_district_id,scm_transporter_freight_rate.source_state_id,scm_transporter_freight_rate.source_district_id,scm_transporter_freight_rate.freight_rate_base_uom_id,scm_transporter_freight_rate.freight_rate_fixed_per_baseuom,scm_transporter_freight_rate.freight_rate_by_base_uomrange from scm_transporter_freight_rate scm_transporter_freight_rate "); //Main query
    sql.count("select count(scm_transporter_freight_rate.id) as total from scm_transporter_freight_rate scm_transporter_freight_rate "); //Count query
    sql.join("left outer join scm_transporter_rate_card scm_transporter_freight_ratetransporter_rate_card_id on (scm_transporter_freight_ratetransporter_rate_card_id.id = scm_transporter_freight_rate.transporter_rate_card_id) left outer join scm_state scm_transporter_freight_ratedestination_state_id on (scm_transporter_freight_ratedestination_state_id.id = scm_transporter_freight_rate.destination_state_id) left outer join scm_district scm_transporter_freight_ratedestination_district_id on (scm_transporter_freight_ratedestination_district_id.id = scm_transporter_freight_rate.destination_district_id) left outer join scm_state scm_transporter_freight_ratesource_state_id on (scm_transporter_freight_ratesource_state_id.id = scm_transporter_freight_rate.source_state_id) left outer join scm_district scm_transporter_freight_ratesource_district_id on (scm_transporter_freight_ratesource_district_id.id = scm_transporter_freight_rate.source_district_id) left outer join scm_transport_freight_rate_uom scm_transporter_freight_ratefreight_rate_base_uom_id on (scm_transporter_freight_ratefreight_rate_base_uom_id.id = scm_transporter_freight_rate.freight_rate_base_uom_id)"); //Join Query

    sql.string(new String[]{"scm_transporter_freight_ratedestination_state_id.state_name", "scm_transporter_freight_ratedestination_district_id.district_name", "scm_transporter_freight_ratesource_state_id.state_name", "scm_transporter_freight_ratesource_district_id.district_name", "scm_transporter_freight_ratefreight_rate_base_uom_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_transporter_freight_rate.id", "scm_transporter_freight_ratetransporter_rate_card_id.lr_rate_fixed", "scm_transporter_freight_rate.freight_rate_fixed_per_baseuom", "scm_transporter_freight_rate.freight_rate_by_base_uomrange"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TransporterFreightRate.
   *
   * @param main
   * @return List of TransporterFreightRate
   */
  public static final List<TransporterFreightRate> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTransporterFreightRateSqlPaged(main));
  }

//  /**
//   * Return list of TransporterFreightRate based on condition
//   * @param main
//   * @return List<TransporterFreightRate>
//   */
//  public static final List<TransporterFreightRate> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTransporterFreightRateSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TransporterFreightRate by key.
   *
   * @param main
   * @param transporterFreightRate
   * @return TransporterFreightRate
   */
  public static final TransporterFreightRate selectByPk(Main main, TransporterFreightRate transporterFreightRate) {
    return (TransporterFreightRate) AppService.find(main, TransporterFreightRate.class, transporterFreightRate.getId());
  }

  /**
   * Insert TransporterFreightRate.
   *
   * @param main
   * @param transporterFreightRate
   */
  public static final void insert(Main main, TransporterFreightRate transporterFreightRate) {
    TransporterFreightRateIs.insertAble(main, transporterFreightRate);  //Validating
    AppService.insert(main, transporterFreightRate);

  }

  /**
   * Update TransporterFreightRate by key.
   *
   * @param main
   * @param transporterFreightRate
   * @return TransporterFreightRate
   */
  public static final TransporterFreightRate updateByPk(Main main, TransporterFreightRate transporterFreightRate) {
    TransporterFreightRateIs.updateAble(main, transporterFreightRate); //Validating
    return (TransporterFreightRate) AppService.update(main, transporterFreightRate);
  }

  /**
   * Insert or update TransporterFreightRate
   *
   * @param main
   * @param transporterFreightRate
   */
  public static void insertOrUpdate(Main main, TransporterFreightRate transporterFreightRate) {
    if (transporterFreightRate.getId() == null) {
      insert(main, transporterFreightRate);
    } else {
      updateByPk(main, transporterFreightRate);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param transporterFreightRate
   */
  public static void clone(Main main, TransporterFreightRate transporterFreightRate) {
    transporterFreightRate.setId(null); //Set to null for insert
    insert(main, transporterFreightRate);
  }

  /**
   * Delete TransporterFreightRate.
   *
   * @param main
   * @param transporterFreightRate
   */
  public static final void deleteByPk(Main main, TransporterFreightRate transporterFreightRate) {
    TransporterFreightRateIs.deleteAble(main, transporterFreightRate); //Validation
    AppService.delete(main, TransporterFreightRate.class, transporterFreightRate.getId());
  }

  /**
   * Delete Array of TransporterFreightRate.
   *
   * @param main
   * @param transporterFreightRate
   */
  public static final void deleteByPkArray(Main main, TransporterFreightRate[] transporterFreightRate) {
    for (TransporterFreightRate e : transporterFreightRate) {
      deleteByPk(main, e);
    }
  }

  public static final List<TransporterFreightRate> getTransporterFreightRateList(Main main, Integer transporterRateCardId) {
    SqlPage sql = getTransporterFreightRateSqlPaged(main);
    if (transporterRateCardId == null) {
      return null;
    }
    sql.cond("where scm_transporter_freight_rate.transporter_rate_card_id=?");
    sql.param(transporterRateCardId);
    return AppService.listAllJpa(main, sql);
  }

  public static final void deleteByTransporterFreightRate(Main main, TransporterFreightRate transporterFreightRate) {
    AppService.deleteSql(main, TransFreightrateUomrange.class, "delete from scm_trans_freightrate_uomrange where transporter_freight_rate_id = ?", new Object[]{transporterFreightRate.getId()});
    AppService.deleteSql(main, TransporterFreightRate.class, "delete from scm_transporter_freight_rate where id = ?", new Object[]{transporterFreightRate.getId()});
  }
}
