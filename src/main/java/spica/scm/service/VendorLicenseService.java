/*
 * @(#)VendorLicenseService.java	1.0 Thu Jun 09 11:13:13 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Vendor;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.VendorLicense;
import spica.scm.validate.VendorLicenseIs;
import spica.sys.UserRuntimeView;

/**
 * VendorLicenseService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 09 11:13:13 IST 2016
 */
public abstract class VendorLicenseService {

  /**
   * VendorLicense paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getVendorLicenseSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_vendor_license", VendorLicense.class, main);
    sql.main("select scm_vendor_license.id,scm_vendor_license.vendor_id,scm_vendor_license.license_type_id,scm_vendor_license.description,scm_vendor_license.license_key,scm_vendor_license.issued_at,scm_vendor_license.valid_from,scm_vendor_license.valid_to,scm_vendor_license.file_path,scm_vendor_license.sort_order,scm_vendor_license.status_id,scm_vendor_license.created_at,scm_vendor_license.modified_at,scm_vendor_license.created_by,scm_vendor_license.modified_by from scm_vendor_license scm_vendor_license "); //Main query
    sql.count("select count(scm_vendor_license.id) as total from scm_vendor_license scm_vendor_license "); //Count query
    sql.join("left outer join scm_vendor scm_vendor_licensevendor_id on (scm_vendor_licensevendor_id.id = scm_vendor_license.vendor_id) left outer join scm_license_type scm_vendor_licenselicense_type_id on (scm_vendor_licenselicense_type_id.id = scm_vendor_license.license_type_id) left outer join scm_status scm_vendor_licensestatus_id on (scm_vendor_licensestatus_id.id = scm_vendor_license.status_id)"); //Join Query

    sql.string(new String[]{"scm_vendor_licensevendor_id.vendor_name", "scm_vendor_licenselicense_type_id.title", "scm_vendor_license.description", "scm_vendor_license.license_key", "scm_vendor_license.file_path", "scm_vendor_licensestatus_id.title", "scm_vendor_license.created_by", "scm_vendor_license.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_vendor_license.id", "scm_vendor_license.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_vendor_license.issued_at", "scm_vendor_license.valid_from", "scm_vendor_license.valid_to", "scm_vendor_license.created_at", "scm_vendor_license.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of VendorLicense.
   *
   * @param main
   * @return List of VendorLicense
   */
  public static final List<VendorLicense> listPaged(Main main) {
    return AppService.listPagedJpa(main, getVendorLicenseSqlPaged(main));
  }

//  /**
//   * Return list of VendorLicense based on condition
//   * @param main
//   * @return List<VendorLicense>
//   */
//  public static final List<VendorLicense> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getVendorLicenseSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select VendorLicense by key.
   *
   * @param main
   * @param vendorLicense
   * @return VendorLicense
   */
  public static final VendorLicense selectByPk(Main main, VendorLicense vendorLicense) {
    return (VendorLicense) AppService.find(main, VendorLicense.class, vendorLicense.getId());
  }

  /**
   * Insert VendorLicense.
   *
   * @param main
   * @param vendorLicense
   */
  public static final void insert(Main main, VendorLicense vendorLicense) {
    VendorLicenseIs.insertAble(main, vendorLicense, UserRuntimeView.instance().getCompany().getId());  //Validating
    AppService.insert(main, vendorLicense);

  }

  /**
   * Update VendorLicense by key.
   *
   * @param main
   * @param vendorLicense
   * @return VendorLicense
   */
  public static final VendorLicense updateByPk(Main main, VendorLicense vendorLicense) {
    VendorLicenseIs.updateAble(main, vendorLicense); //Validating
    return (VendorLicense) AppService.update(main, vendorLicense);
  }

  /**
   * Insert or update VendorLicense
   *
   * @param main
   * @param VendorLicense
   */
  public static void insertOrUpdate(Main main, VendorLicense vendorLicense) {
    if (vendorLicense.getId() == null) {
      insert(main, vendorLicense);
    } else {
      updateByPk(main, vendorLicense);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param vendorLicense
   */
  public static void clone(Main main, VendorLicense vendorLicense) {
    vendorLicense.setId(null); //Set to null for insert
    insert(main, vendorLicense);
  }

  /**
   * Delete VendorLicense.
   *
   * @param main
   * @param vendorLicense
   */
  public static final void deleteByPk(Main main, VendorLicense vendorLicense) {
    VendorLicenseIs.deleteAble(main, vendorLicense); //Validation
    AppService.delete(main, VendorLicense.class, vendorLicense.getId());
  }

  /**
   * Delete Array of VendorLicense.
   *
   * @param main
   * @param vendorLicense
   */
  public static final void deleteByPkArray(Main main, VendorLicense[] vendorLicense) {
    for (VendorLicense e : vendorLicense) {
      deleteByPk(main, e);
    }
  }

  /**
   * Return all license of a vendor.
   *
   * @param main
   * @param vendor
   * @return
   */
  public static final List<VendorLicense> licenseListByVendor(Main main, Vendor vendor) {
    main.clear();
    SqlPage sql = getVendorLicenseSqlPaged(main);
    if (vendor.getId() == null) {
      return null;
    }
    sql.cond("where scm_vendor_license.vendor_id=?");
    sql.param(vendor.getId());
    return AppService.listAllJpa(main, sql);
  }
}
