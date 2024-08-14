/*
 * @(#)TransporterService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.fin.service.LedgerExternalService;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.TransportMode;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.Transporter;
import spica.scm.domain.TransporterAddress;
import spica.scm.domain.TransporterContact;
import spica.scm.domain.TransporterRateCard;
import spica.scm.validate.TransporterIs;
import wawo.app.faces.MainView;

/**
 * TransporterService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TransporterService {

  /**
   * Transporter paginated query.
   *
   * @param main
   * @return SqlPage
   */
  public static final int byRoad = 1;
  public static final int byRail = 3;
  public static final int byAir = 2;
  public static final int bySea = 4;
  public static final int byCourier = 5;
  public static final Integer TRANSPORTER_TYPE_INDIVIDUAL = 1;

  private static SqlPage getTransporterSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_transporter", Transporter.class, main);
    sql.main("select scm_transporter.id,scm_transporter.company_id,scm_transporter.transporter_name,scm_transporter.description,scm_transporter.note,scm_transporter.sort_order,scm_transporter.status_id,scm_transporter.created_by,scm_transporter.modified_by,scm_transporter.created_at,scm_transporter.modified_at,scm_transporter.cst_no,scm_transporter.vat_no,scm_transporter.tin_no,scm_transporter.gst_no,scm_transporter.pan_no,scm_transporter.country_id,scm_transporter.state_id, scm_transporter.taxable from scm_transporter scm_transporter "); //Main query
    sql.count("select count(scm_transporter.id) as total from scm_transporter scm_transporter "); //Count query
    sql.join("left outer join scm_company scm_transportercompany_id on (scm_transportercompany_id.id = scm_transporter.company_id) left outer join scm_status scm_transporterstatus_id on (scm_transporterstatus_id.id = scm_transporter.status_id)"); //Join Query

    sql.string(new String[]{"scm_transportercompany_id.company_name", "scm_transporter.transporter_name", "scm_transporter.description", "scm_transporter.note", "scm_transporterstatus_id.title", "scm_transporter.created_by", "scm_transporter.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_transporter.id", "scm_transporter.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_transporter.created_at", "scm_transporter.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Transporter.
   *
   * @param main
   * @return List of Transporter
   */
  public static List<Transporter> listPaged(MainView main, Company company) {
    SqlPage sql = getTransporterSqlPaged(main);
    sql.cond("where scm_transporter.company_id=?");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of Transporter based on condition
//   * @param main
//   * @return List<Transporter>
//   */
//  public static final List<Transporter> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTransporterSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Transporter by key.
   *
   * @param main
   * @param transporter
   * @return Transporter
   */
  public static final Transporter selectByPk(Main main, Transporter transporter) {
    return (Transporter) AppService.find(main, Transporter.class, transporter.getId());
  }

  /**
   * Insert or update Transporter
   *
   * @param main
   * @param transporter
   * @param transportRateCard
   */
  public static void insertOrUpdate(Main main, Transporter transporter) {
    if (transporter.getId() == null) {
      TransporterIs.insertAble(main, transporter);  //Validating
      AppService.insert(main, transporter);
    } else {
      TransporterIs.updateAble(main, transporter); //Validating     
      transporter = (Transporter) AppService.update(main, transporter);
    }
    LedgerExternalService.saveLedgerTransporter(main, transporter);
//    if (transportModeSelected != null) {
//      insertArray(main, transportModeSelected, transporter);   //Inserting all the relation records.
//    }
  }

  public static void insertArray(Main main, List<TransportMode> transportModeSelected, Transporter transporter) {
    if (transportModeSelected != null) {
      TransporterRateCard tsm;
      for (TransportMode shMode : transportModeSelected) {
        tsm = new TransporterRateCard();
        tsm.setTransportModeId(shMode);
        tsm.setTransporterId(transporter);
        TransporterRateCardService.insert(main, tsm);
      }
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param transporter
   */
  public static void clone(Main main, Transporter transporter) {
    transporter.setId(null); //Set to null for insert
    insertOrUpdate(main, transporter);
  }

  /**
   * Delete Transporter.
   *
   * @param main
   * @param transporter
   */
  public static final void deleteByPk(Main main, Transporter transporter) {
    LedgerExternalService.deleteLedgerTransporter(main, transporter);
    AppService.deleteSql(main, TransporterAddress.class, "delete from scm_transporter_address scm_transporter_address where scm_transporter_address.transporter_id=?", new Object[]{transporter.getId()});
    AppService.deleteSql(main, TransporterContact.class, "delete from scm_transporter_contact scm_transporter_contact where scm_transporter_contact.transporter_id=?", new Object[]{transporter.getId()});
    AppService.deleteSql(main, TransporterRateCard.class, "delete from scm_transporter_rate_card scm_transporter_rate_card where scm_transporter_rate_card.transporter_id=?", new Object[]{transporter.getId()});
    AppService.delete(main, Transporter.class, transporter.getId());
  }

  /**
   * Delete Array of Transporter.
   *
   * @param main
   * @param transporter
   */
  public static final void deleteByPkArray(Main main, Transporter[] transporter) {
    for (Transporter e : transporter) {
      deleteByPk(main, e);
    }
  }

//  public static final List<TransporterRateCard> selectedTransportRateCardByTransporter(Main main, Transporter transporter) {
//    return AppService.list(main, TransporterRateCard.class, "select scm_transporter_rate_card_id.lr_rate_fixed ,scm_transporter_rate_card_id.id ,scm_transporter_rate_card_id.transport_mode_id from \n" +
//"scm_transporter_rate_card scm_transporter_rate_card_id left outer join scm_transport_mode scm_transport_mode on (scm_transport_mode.id = scm_transporter_rate_card_id.transport_mode_id)\n" +
//"where scm_transporter_rate_card_id.transporter_id=?", new Object[]{transporter.getId()});
//  }
  public static final List<TransportMode> selectedTransportModeByTransporter(Main main, Transporter transporter) {
    return AppService.list(main, TransportMode.class, "select scm_transport_mode.id,scm_transport_mode.title from scm_transport_mode scm_transport_mode "
            + "left outer join scm_transporter_rate_card scm_transporter_rate_card_id on(scm_transporter_rate_card_id.transport_mode_id=scm_transport_mode.id)"
            + "where scm_transporter_rate_card_id.transporter_id=?", new Object[]{transporter.getId()});
  }

  public static TransporterRateCard selectedTransportRateCardByTransporter(Main main, Transporter transporter, int id) {
    return (TransporterRateCard) AppService.single(main, TransporterRateCard.class, "select * from scm_transporter_rate_card scm_transporter_rate_card where scm_transporter_rate_card.transporter_id=? and scm_transporter_rate_card.transport_mode_id=?", new Object[]{transporter.getId(), id});
//  and scm_transporter_rate_card.transport_mode_id=?
  }

  public static Transporter selectTransporterByCustomer(Main main, Customer customer) {
    return (Transporter) AppService.single(main, Transporter.class, "SELECT t1.* FROM scm_transporter t1,scm_customer t2 WHERE t2.transporter_id = t1.id AND t2.id = ? ", new Object[]{customer.getId()});
  }

}
