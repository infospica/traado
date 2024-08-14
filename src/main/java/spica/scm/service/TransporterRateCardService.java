/*
 * @(#)TransporterRateCardService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Transporter;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.TransporterRateCard;
import spica.scm.validate.TransporterRateCardIs;

/**
 * TransporterRateCardService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TransporterRateCardService {

  /**
   * TransporterRateCard paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTransporterRateCardSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_transporter_rate_card", TransporterRateCard.class, main);
    sql.main("select scm_transporter_rate_card.id,scm_transporter_rate_card.transporter_id,scm_transporter_rate_card.transport_mode_id,scm_transporter_rate_card.lr_rate_fixed from scm_transporter_rate_card scm_transporter_rate_card "); //Main query
    sql.count("select count(scm_transporter_rate_card.id) as total from scm_transporter_rate_card scm_transporter_rate_card "); //Count query
    sql.join("left outer join scm_transporter scm_transporter_rate_cardtransporter_id on (scm_transporter_rate_cardtransporter_id.id = scm_transporter_rate_card.transporter_id) left outer join scm_transport_mode scm_transporter_rate_cardtransport_mode_id on (scm_transporter_rate_cardtransport_mode_id.id = scm_transporter_rate_card.transport_mode_id)"); //Join Query

    sql.string(new String[]{"scm_transporter_rate_cardtransporter_id.transporter_name", "scm_transporter_rate_cardtransport_mode_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_transporter_rate_card.id", "scm_transporter_rate_card.lr_rate_fixed"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TransporterRateCard.
   *
   * @param main
   * @return List of TransporterRateCard
   */
  public static final List<TransporterRateCard> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTransporterRateCardSqlPaged(main));
  }

//  /**
//   * Return list of TransporterRateCard based on condition
//   * @param main
//   * @return List<TransporterRateCard>
//   */
//  public static final List<TransporterRateCard> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTransporterRateCardSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TransporterRateCard by key.
   *
   * @param main
   * @param transporterRateCard
   * @return TransporterRateCard
   */
  public static final TransporterRateCard selectByPk(Main main, TransporterRateCard transporterRateCard) {
    return (TransporterRateCard) AppService.find(main, TransporterRateCard.class, transporterRateCard.getId());
  }

  /**
   * Insert TransporterRateCard.
   *
   * @param main
   * @param transporterRateCard
   */
  public static final void insert(Main main, TransporterRateCard transporterRateCard) {
    TransporterRateCardIs.insertAble(main, transporterRateCard);  //Validating
    AppService.insert(main, transporterRateCard);

  }

  /**
   * Update TransporterRateCard by key.
   *
   * @param main
   * @param transporterRateCard
   * @return TransporterRateCard
   */
  public static final TransporterRateCard updateByPk(Main main, TransporterRateCard transporterRateCard) {
    TransporterRateCardIs.updateAble(main, transporterRateCard); //Validating
    return (TransporterRateCard) AppService.update(main, transporterRateCard);
  }

  /**
   * Insert or update TransporterRateCard
   *
   * @param main
   * @param transporterRateCard
   */
  public static void insertOrUpdate(Main main, TransporterRateCard transporterRateCard) {
    if (transporterRateCard.getId() == null) {
      insert(main, transporterRateCard);
    } else {
      updateByPk(main, transporterRateCard);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param transporterRateCard
   */
  public static void clone(Main main, TransporterRateCard transporterRateCard) {
    transporterRateCard.setId(null); //Set to null for insert
    insert(main, transporterRateCard);
  }

  /**
   * Delete TransporterRateCard.
   *
   * @param main
   * @param transporterRateCard
   */
  public static final void deleteByPk(Main main, TransporterRateCard transporterRateCard) {
    TransporterRateCardIs.deleteAble(main, transporterRateCard); //Validation
    AppService.delete(main, TransporterRateCard.class, transporterRateCard.getId());
  }

  /**
   * Delete Array of TransporterRateCard.
   *
   * @param main
   * @param transporterRateCard
   */
  public static final void deleteByPkArray(Main main, TransporterRateCard[] transporterRateCard) {
    for (TransporterRateCard e : transporterRateCard) {
      deleteByPk(main, e);
    }
  }

  public static void insertArray(Main main, Transporter transporter, List<TransporterRateCard> transportRateCard) {

    for (TransporterRateCard rateCard : transportRateCard) {
      rateCard.setTransporterId(transporter);
      rateCard.setLrRateFixed(rateCard.getLrRateFixed());
      rateCard.setTransportModeId(rateCard.getTransportModeId());
      insert(main, rateCard);
    }

  }

  public static final List<TransporterRateCard> rateCardListByTransporter(Main main, Transporter transporter) {
    SqlPage sql = getTransporterRateCardSqlPaged(main);
    if (transporter.getId() == null) {
      return null;
    }
    sql.cond("where scm_transporter_rate_card.transporter_id=?");
    sql.param(transporter.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static final void deleteTransporterRateCard(Main main, TransporterRateCard transporterRateCard) {
    
    AppService.deleteSql(main, TransporterRateCard.class, "delete from scm_transporter_freight_rate where transporter_rate_card_id = ?", new Object[]{transporterRateCard.getId()});
    AppService.deleteSql(main, TransporterRateCard.class, "delete from scm_transporter_rate_card where id = ?", new Object[]{transporterRateCard.getId()});
//    AppService.deleteSql(main, TransporterRateCard.class, "delete from scm_trans_freightrate_uomrange where transporter_freight_rate_id = ?", new Object[]{transporterRateCard.});
  }
}
