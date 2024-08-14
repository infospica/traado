/*
 * @(#)TransporterAddressService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
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
import spica.scm.domain.TransporterAddress;
import spica.scm.validate.TransporterAddressIs;

/**
 * TransporterAddressService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TransporterAddressService {

  /**
   * TransporterAddress paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTransporterAddressSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_transporter_address", TransporterAddress.class, main);
    sql.main("select scm_transporter_address.id,scm_transporter_address.transporter_id,scm_transporter_address.address_type_id,scm_transporter_address.address,scm_transporter_address.country_id,scm_transporter_address.state_id,scm_transporter_address.district_id,scm_transporter_address.territory_id,scm_transporter_address.pin,scm_transporter_address.phone_1,scm_transporter_address.phone_2,scm_transporter_address.phone_3,scm_transporter_address.fax_1,scm_transporter_address.fax_2,scm_transporter_address.email,scm_transporter_address.website,scm_transporter_address.sort_order,scm_transporter_address.status_id,scm_transporter_address.created_by,scm_transporter_address.modified_by,scm_transporter_address.created_at,scm_transporter_address.modified_at from scm_transporter_address scm_transporter_address "); //Main query
    sql.count("select count(scm_transporter_address.id) as total from scm_transporter_address scm_transporter_address "); //Count query
    sql.join("left outer join scm_transporter scm_transporter_addresstransporter_id on (scm_transporter_addresstransporter_id.id = scm_transporter_address.transporter_id) left outer join scm_address_type scm_transporter_addressaddress_type_id on (scm_transporter_addressaddress_type_id.id = scm_transporter_address.address_type_id) left outer join scm_country scm_transporter_addresscountry_id on (scm_transporter_addresscountry_id.id = scm_transporter_address.country_id) left outer join scm_state scm_transporter_addressstate_id on (scm_transporter_addressstate_id.id = scm_transporter_address.state_id) left outer join scm_district scm_transporter_addressdistrict_id on (scm_transporter_addressdistrict_id.id = scm_transporter_address.district_id) left outer join scm_territory scm_transporter_addressterritory_id on (scm_transporter_addressterritory_id.id = scm_transporter_address.territory_id) left outer join scm_status scm_transporter_addressstatus_id on (scm_transporter_addressstatus_id.id = scm_transporter_address.status_id)"); //Join Query

    sql.string(new String[]{"scm_transporter_addresstransporter_id.transporter_name", "scm_transporter_addressaddress_type_id.title", "scm_transporter_address.address", "scm_transporter_addresscountry_id.country_name", "scm_transporter_addressstate_id.state_name", "scm_transporter_addressdistrict_id.district_name", "scm_transporter_addressterritory_id.territory_name", "scm_transporter_address.pin", "scm_transporter_address.phone_1", "scm_transporter_address.phone_2", "scm_transporter_address.phone_3", "scm_transporter_address.fax_1", "scm_transporter_address.fax_2", "scm_transporter_address.email", "scm_transporter_address.website", "scm_transporter_addressstatus_id.title", "scm_transporter_address.created_by", "scm_transporter_address.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_transporter_address.id", "scm_transporter_address.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_transporter_address.created_at", "scm_transporter_address.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TransporterAddress.
   *
   * @param main
   * @return List of TransporterAddress
   */
  public static final List<TransporterAddress> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTransporterAddressSqlPaged(main));
  }
  public static final TransporterAddress selectTransporterRegisteredAddress(Main main, Transporter transporter) {
    return (TransporterAddress) AppService.single(main, TransporterAddress.class, "select * from scm_transporter_address where transporter_id = ? and address_type_id = ?", new Object[]{transporter.getId(), AddressTypeService.REGISTERED_ADDRESS});
  }
//  /**
//   * Return list of TransporterAddress based on condition
//   * @param main
//   * @return List<TransporterAddress>
//   */
//  public static final List<TransporterAddress> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTransporterAddressSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TransporterAddress by key.
   *
   * @param main
   * @param transporterAddress
   * @return TransporterAddress
   */
  public static final TransporterAddress selectByPk(Main main, TransporterAddress transporterAddress) {
    return (TransporterAddress) AppService.find(main, TransporterAddress.class, transporterAddress.getId());
  }

  /**
   * Insert TransporterAddress.
   *
   * @param main
   * @param transporterAddress
   */
  public static final void insert(Main main, TransporterAddress transporterAddress) {
    TransporterAddressIs.insertAble(main, transporterAddress);  //Validating
    AppService.insert(main, transporterAddress);

  }

  /**
   * Update TransporterAddress by key.
   *
   * @param main
   * @param transporterAddress
   * @return TransporterAddress
   */
  public static final TransporterAddress updateByPk(Main main, TransporterAddress transporterAddress) {
    TransporterAddressIs.updateAble(main, transporterAddress); //Validating
    return (TransporterAddress) AppService.update(main, transporterAddress);
  }

  /**
   * Insert or update TransporterAddress
   *
   * @param main
   * @param transporterAddress
   */
  public static void insertOrUpdate(Main main, TransporterAddress transporterAddress) {
    if (transporterAddress.getId() == null) {
      insert(main, transporterAddress);
    } else {
      updateByPk(main, transporterAddress);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param transporterAddress
   */
  public static void clone(Main main, TransporterAddress transporterAddress) {
    transporterAddress.setId(null); //Set to null for insert
    insert(main, transporterAddress);
  }

  /**
   * Delete TransporterAddress.
   *
   * @param main
   * @param transporterAddress
   */
  public static final void deleteByPk(Main main, TransporterAddress transporterAddress) {
    TransporterAddressIs.deleteAble(main, transporterAddress); //Validation
    AppService.delete(main, TransporterAddress.class, transporterAddress.getId());
  }

  /**
   * Delete Array of TransporterAddress.
   *
   * @param main
   * @param transporterAddress
   */
  public static final void deleteByPkArray(Main main, TransporterAddress[] transporterAddress) {
    for (TransporterAddress e : transporterAddress) {
      deleteByPk(main, e);
    }
  }

  public static final List<TransporterAddress> addressListByTransporter(Main main, Transporter transporter) {
    SqlPage sql = getTransporterAddressSqlPaged(main);
    if (transporter.getId() == null) {
      return null;
    }
    sql.cond("where scm_transporter_address.transporter_id=?");
    sql.param(transporter.getId());
    return AppService.listAllJpa(main, sql);
  }
}
