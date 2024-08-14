/*
 * @(#)VendorContactService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Vendor;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.VendorContact;
import spica.scm.validate.VendorContactIs;

/**
 * VendorContactService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class VendorContactService {

  /**
   * VendorContact paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getVendorContactSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_vendor_contact", VendorContact.class, main);
    sql.main("select scm_vendor_contact.id,scm_vendor_contact.vendor_id,scm_vendor_contact.designation_id,scm_vendor_contact.pan_no,scm_vendor_contact.user_profile_id,scm_vendor_contact.sort_order,scm_vendor_contact.status_id,scm_vendor_contact.created_by,scm_vendor_contact.modified_by,scm_vendor_contact.created_at,scm_vendor_contact.modified_at from scm_vendor_contact scm_vendor_contact "); //Main query
    sql.count("select count(scm_vendor_contact.id) as total from scm_vendor_contact scm_vendor_contact "); //Count query
    sql.join("left outer join scm_vendor scm_vendor_contactvendor_id on (scm_vendor_contactvendor_id.id = scm_vendor_contact.vendor_id) left outer join scm_designation scm_vendor_contactdesignation_id on (scm_vendor_contactdesignation_id.id = scm_vendor_contact.designation_id) left outer join scm_user_profile scm_vendor_contactuser_profile_id on (scm_vendor_contactuser_profile_id.id = scm_vendor_contact.user_profile_id) left outer join scm_status scm_vendor_contactstatus_id on (scm_vendor_contactstatus_id.id = scm_vendor_contact.status_id)"); //Join Query

    sql.string(new String[]{"scm_vendor_contactvendor_id.vendor_name", "scm_vendor_contactdesignation_id.title", "scm_vendor_contactuser_profile_id.user_code", "scm_vendor_contactstatus_id.title", "scm_vendor_contact.created_by", "scm_vendor_contact.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_vendor_contact.id", "scm_vendor_contact.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_vendor_contact.created_at", "scm_vendor_contact.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of VendorContact.
   *
   * @param main
   * @return List of VendorContact
   */
  public static final List<VendorContact> listPaged(Main main) {
    return AppService.listPagedJpa(main, getVendorContactSqlPaged(main));
  }

  /**
   * Return all contact of a vendor.
   *
   * @param main
   * @param vendor
   * @return
   */
  public static final List<VendorContact> contactListByVendor(Main main, Vendor vendor) {
    SqlPage sql = getVendorContactSqlPaged(main);
    if (vendor.getId() == null) {
      return null;
    }
    sql.cond("where scm_vendor_contact.vendor_id=?");
    sql.param(vendor.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of VendorContact based on condition
//   * @param main
//   * @return List<VendorContact>
//   */
//  public static final List<VendorContact> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getVendorContactSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select VendorContact by key.
   *
   * @param main
   * @param vendorContact
   * @return VendorContact
   */
  public static final VendorContact selectByPk(Main main, VendorContact vendorContact) {
    return (VendorContact) AppService.find(main, VendorContact.class, vendorContact.getId());
  }

  /**
   * Insert VendorContact.
   *
   * @param main
   * @param vendorContact
   */
  public static final void insert(Main main, VendorContact vendorContact) {
    VendorContactIs.insertAble(main, vendorContact);  //Validating
    AppService.insert(main, vendorContact);

  }

  /**
   * Update VendorContact by key.
   *
   * @param main
   * @param vendorContact
   * @return VendorContact
   */
  public static final VendorContact updateByPk(Main main, VendorContact vendorContact) {
    VendorContactIs.updateAble(main, vendorContact); //Validating
    return (VendorContact) AppService.update(main, vendorContact);
  }

  /**
   * Insert or update VendorContact
   *
   * @param main
   * @param vendorContact
   */
  public static void insertOrUpdate(Main main, VendorContact vendorContact) {
    if (vendorContact.getId() == null) {
      insert(main, vendorContact);
    } else {
      updateByPk(main, vendorContact);
    }
  }

  /**
   * Making default for newly added or updated if isdefault is checked
   *
   * @param main
   * @param vendorContact
   */
//  public static void makeDefault(Main main, VendorContact vendorContact) {
//    if (vendorContact.getSortOrder() == 0) {
//      main.param(1);
//      main.param(vendorContact.getVendorId().getId());
//      main.param(vendorContact.getId());
//      main.param(0);
//      AppService.updateSql(main, VendorContact.class, "update scm_vendor_contact set modified_by = ?,modified_at = ?, sort_order = ? where vendor_id = ? and id <> ? and sort_order = ?", true);
//    }
//  }
  /**
   * Clone and existing object
   *
   * @param main
   * @param vendorContact
   */
  public static void clone(Main main, VendorContact vendorContact) {
    vendorContact.setId(null); //Set to null for insert
    insert(main, vendorContact);
  }

  /**
   * Delete VendorContact.
   *
   * @param main
   * @param vendorContact
   */
  public static final void deleteByPk(Main main, VendorContact vendorContact) {
    VendorContactIs.deleteAble(main, vendorContact); //Validation
    AppService.delete(main, VendorContact.class, vendorContact.getId());
  }

  /**
   * Delete Array of VendorContact.
   *
   * @param main
   * @param vendorContact
   */
  public static final void deleteByPkArray(Main main, VendorContact[] vendorContact) {
    for (VendorContact e : vendorContact) {
      deleteByPk(main, e);
    }
  }

  public static void insertArray(Main main, UserProfile[] userProfileSelected, Vendor vendor) {
    if (userProfileSelected != null) {
      VendorContact vendorContact;
      for (UserProfile userProfile : userProfileSelected) {  //Reinserting
        vendorContact = new VendorContact();
        vendorContact.setUserProfileId(userProfile);
        vendorContact.setVendorId(vendor);
        vendorContact.setDesignationId(userProfile.getDesignationId());
        vendorContact.setStatusId(userProfile.getStatusId());
        insert(main, vendorContact);
      }
    }
  }

  public static final void deleteVendorContact(Main main, VendorContact vendorContact) {
    AppService.deleteSql(main, VendorContact.class, "delete from scm_vendor_contact where id = ?", new Object[]{vendorContact.getId()});
  }
}
