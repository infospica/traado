/*
 * @(#)VendorAddressService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Consignment;
import spica.scm.domain.Vendor;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.VendorAddress;
import spica.scm.validate.VendorAddressIs;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 * VendorAddressService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class VendorAddressService {

  /**
   * VendorAddress paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getVendorAddressSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_vendor_address", VendorAddress.class, main);
    sql.main("select scm_vendor_address.id,scm_vendor_address.vendor_id,scm_vendor_address.address_type_id,scm_vendor_address.address,scm_vendor_address.country_id,scm_vendor_address.state_id,scm_vendor_address.district_id,scm_vendor_address.pin,scm_vendor_address.territory_id,scm_vendor_address.phone_1,scm_vendor_address.phone_2,scm_vendor_address.phone_3,scm_vendor_address.fax_1,scm_vendor_address.fax_2,scm_vendor_address.email,scm_vendor_address.sort_order,scm_vendor_address.status_id,scm_vendor_address.created_by,scm_vendor_address.modified_by,scm_vendor_address.created_at,scm_vendor_address.modified_at from scm_vendor_address scm_vendor_address "); //Main query
    sql.count("select count(scm_vendor_address.id) as total from scm_vendor_address scm_vendor_address "); //Count query
    sql.join("left outer join scm_vendor scm_vendor_addressvendor_id on (scm_vendor_addressvendor_id.id = scm_vendor_address.vendor_id) left outer join scm_address_type scm_vendor_addressaddress_type_id on (scm_vendor_addressaddress_type_id.id = scm_vendor_address.address_type_id) left outer join scm_country scm_vendor_addresscountry_id on (scm_vendor_addresscountry_id.id = scm_vendor_address.country_id) left outer join scm_state scm_vendor_addressstate_id on (scm_vendor_addressstate_id.id = scm_vendor_address.state_id) left outer join scm_district scm_vendor_addressdistrict_id on (scm_vendor_addressdistrict_id.id = scm_vendor_address.district_id) left outer join scm_territory scm_vendor_addressterritory_id on (scm_vendor_addressterritory_id.id = scm_vendor_address.territory_id) left outer join scm_status scm_vendor_addressstatus_id on (scm_vendor_addressstatus_id.id = scm_vendor_address.status_id)"); //Join Query

    sql.string(new String[]{"scm_vendor_addressvendor_id.vendor_name", "scm_vendor_addressaddress_type_id.title", "scm_vendor_address.address", "scm_vendor_addresscountry_id.country_name", "scm_vendor_addressstate_id.state_name", "scm_vendor_addressdistrict_id.district_name", "scm_vendor_address.pin", "scm_vendor_addressterritory_id.territory_name", "scm_vendor_address.phone_1", "scm_vendor_address.phone_2", "scm_vendor_address.phone_3", "scm_vendor_address.fax_1", "scm_vendor_address.fax_2", "scm_vendor_address.email", "scm_vendor_addressstatus_id.title", "scm_vendor_address.created_by", "scm_vendor_address.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_vendor_address.id", "scm_vendor_address.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_vendor_address.created_at", "scm_vendor_address.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of VendorAddress.
   *
   * @param main
   * @return List of VendorAddress
   */
  public static final List<VendorAddress> listPaged(Main main) {
    return AppService.listPagedJpa(main, getVendorAddressSqlPaged(main));
  }

  /**
   * Return all vendor address of a vendor.
   *
   * @param main
   * @param vendor
   * @return
   */
  public static final List<VendorAddress> addressListByVendor(Main main, Vendor vendor) {
    SqlPage sql = getVendorAddressSqlPaged(main);
    if (vendor.getId() == null) {
      return null;
    }
    sql.cond("where scm_vendor_address.vendor_id=?");
    sql.param(vendor.getId());
    return AppService.listAllJpa(main, sql);
  }

//  /**
//   * Return list of VendorAddress based on condition
//   * @param main
//   * @return List<VendorAddress>
//   */
//  public static final List<VendorAddress> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getVendorAddressSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select VendorAddress by key.
   *
   * @param main
   * @param vendorAddress
   * @return VendorAddress
   */
  public static final VendorAddress selectByPk(Main main, VendorAddress vendorAddress) {
    return (VendorAddress) AppService.find(main, VendorAddress.class, vendorAddress.getId());
  }

  /**
   * Insert VendorAddress.
   *
   * @param main
   * @param vendorAddress
   */
  public static final void insert(Main main, VendorAddress vendorAddress) {
    VendorAddressIs.insertAble(main, vendorAddress);  //Validating
    AppService.insert(main, vendorAddress);

  }

  /**
   * Update VendorAddress by key.
   *
   * @param main
   * @param vendorAddress
   * @return VendorAddress
   */
  public static final VendorAddress updateByPk(Main main, VendorAddress vendorAddress) {
    VendorAddressIs.updateAble(main, vendorAddress); //Validating
    return (VendorAddress) AppService.update(main, vendorAddress);
  }

  /**
   * Insert or update VendorAddress
   *
   * @param main
   * @param vendorAddress
   */
  public static void insertOrUpdate(Main main, VendorAddress vendorAddress) {
    if (vendorAddress.getId() == null) {
      insert(main, vendorAddress);
    } else {
      updateByPk(main, vendorAddress);
    }
  }


  /**
   * Clone and existing object
   *
   * @param main
   * @param vendorAddress
   */
  public static void clone(Main main, VendorAddress vendorAddress) {
    vendorAddress.setId(null); //Set to null for insert
    insert(main, vendorAddress);
  }

  /**
   * Delete VendorAddress.
   *
   * @param main
   * @param vendorAddress
   */
  public static final void deleteByPk(Main main, VendorAddress vendorAddress) {
    VendorAddressIs.deleteAble(main, vendorAddress); //Validation
    AppService.delete(main, VendorAddress.class, vendorAddress.getId());
  }

  /**
   * Delete Array of VendorAddress.
   *
   * @param main
   * @param vendorAddress
   */
  public static final void deleteByPkArray(Main main, VendorAddress[] vendorAddress) {
    for (VendorAddress e : vendorAddress) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param vendorId
   * @return
   */
  public static VendorAddress selectDefaultVendorAddress(Main main, Vendor vendorId) {
    return (VendorAddress) AppService.single(main, VendorAddress.class, "select * from scm_vendor_address where vendor_id = ? and address_type_id = ?", new Object[]{vendorId.getId(), AddressTypeService.REGISTERED_ADDRESS});
  }
}
