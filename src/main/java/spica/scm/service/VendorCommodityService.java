/*
 * @(#)VendorCommodityService.java	1.0 Mon Jun 13 13:51:36 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Vendor;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.VendorCommodity;
import spica.scm.validate.VendorCommodityIs;

/**
 * VendorCommodityService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jun 13 13:51:36 IST 2016
 */
public abstract class VendorCommodityService {

  /**
   * VendorCommodity paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getVendorCommoditySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_vendor_commodity", VendorCommodity.class, main);
    sql.main("select scm_vendor_commodity.id,scm_vendor_commodity.vendor_id,scm_vendor_commodity.commodity_id from scm_vendor_commodity scm_vendor_commodity "); //Main query
    sql.count("select count(scm_vendor_commodity.id) from scm_vendor_commodity scm_vendor_commodity "); //Count query
    sql.join("left outer join scm_vendor scm_vendor_commodityvendor_id on (scm_vendor_commodityvendor_id.id = scm_vendor_commodity.vendor_id) left outer join scm_service_commodity scm_vendor_commoditycommodity_id on (scm_vendor_commoditycommodity_id.id = scm_vendor_commodity.commodity_id)"); //Join Query

    sql.string(new String[]{"scm_vendor_commodityvendor_id.vendor_name", "scm_vendor_commoditycommodity_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_vendor_commodity.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of VendorCommodity.
   *
   * @param main
   * @return List of VendorCommodity
   */
  public static final List<VendorCommodity> listPaged(Main main) {
    return AppService.listPagedJpa(main, getVendorCommoditySqlPaged(main));
  }

//  /**
//   * Return list of VendorCommodity based on condition
//   * @param main
//   * @return List<VendorCommodity>
//   */
//  public static final List<VendorCommodity> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getVendorCommoditySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select VendorCommodity by key.
   *
   * @param main
   * @param vendorCommodity
   * @return VendorCommodity
   */
  public static final VendorCommodity selectByPk(Main main, VendorCommodity vendorCommodity) {
    return (VendorCommodity) AppService.find(main, VendorCommodity.class, vendorCommodity.getId());
  }

  /**
   * Insert VendorCommodity.
   *
   * @param main
   * @param vendorCommodity
   */
  public static final void insert(Main main, VendorCommodity vendorCommodity) {
    VendorCommodityIs.insertAble(main, vendorCommodity);  //Validating
    AppService.insert(main, vendorCommodity);

  }

  /**
   * Update VendorCommodity by key.
   *
   * @param main
   * @param vendorCommodity
   * @return VendorCommodity
   */
  public static final VendorCommodity updateByPk(Main main, VendorCommodity vendorCommodity) {
    VendorCommodityIs.updateAble(main, vendorCommodity); //Validating
    return (VendorCommodity) AppService.update(main, vendorCommodity);
  }

  /**
   * Insert or update VendorCommodity
   *
   * @param main
   * @param VendorCommodity
   */
  public static void insertOrUpdate(Main main, VendorCommodity vendorCommodity) {
    if (vendorCommodity.getId() == null) {
      insert(main, vendorCommodity);
    } else {
      updateByPk(main, vendorCommodity);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param vendorCommodity
   */
  public static void clone(Main main, VendorCommodity vendorCommodity) {
    vendorCommodity.setId(null); //Set to null for insert
    insert(main, vendorCommodity);
  }

  /**
   * Delete VendorCommodity.
   *
   * @param main
   * @param vendorCommodity
   */
  public static final void deleteByPk(Main main, VendorCommodity vendorCommodity) {
    VendorCommodityIs.deleteAble(main, vendorCommodity); //Validation
    AppService.delete(main, VendorCommodity.class, vendorCommodity.getId());
  }

  /**
   * Delete Array of VendorCommodity.
   *
   * @param main
   * @param vendorCommodity
   */
  public static final void deleteByPkArray(Main main, VendorCommodity[] vendorCommodity) {
    for (VendorCommodity e : vendorCommodity) {
      deleteByPk(main, e);
    }
  }

  public static void deleteVendorCommodityRelation(Main main, ServiceCommodity commodity, Vendor vendor) {
    AppService.deleteSql(main, VendorCommodity.class, "delete from scm_vendor_commodity where commodity_id = ? and vendor_id = ?", new Object[]{commodity.getId(), vendor.getId()});
  }

  public static void insertArray(Main main, ServiceCommodity[] commoditySelected, Vendor vendor) {
    if (commoditySelected != null) {
      VendorCommodity VendorCommodity;
      for (ServiceCommodity commodity : commoditySelected) {  //Reinserting
        VendorCommodity = new VendorCommodity();
        VendorCommodity.setCommodityId(commodity);
        VendorCommodity.setVendorId(vendor);
        insert(main, VendorCommodity);
      }
    }
  }

  public static void insertVendorCommodity(Main main, Vendor vendor, List<ServiceCommodity> commoditySelected) {
    AppService.deleteSql(main, VendorCommodity.class, "delete from scm_vendor_commodity where vendor_id = ?", new Object[]{vendor.getId()});
    if (commoditySelected != null) {
      VendorCommodity VendorCommodity;
      for (ServiceCommodity commodity : commoditySelected) {  //Reinserting
        VendorCommodity = new VendorCommodity();
        VendorCommodity.setCommodityId(commodity);
        VendorCommodity.setVendorId(vendor);
        insert(main, VendorCommodity);
      }
    }
  }
}
