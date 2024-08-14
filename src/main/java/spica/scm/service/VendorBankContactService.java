/*
 * @(#)VendorBankContactService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.VendorBank;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.VendorBankContact;
import spica.scm.validate.VendorBankContactIs;

/**
 * VendorBankContactService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class VendorBankContactService {

  /**
   * VendorBankContact paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getVendorBankContactSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_vendor_bank_contact", VendorBankContact.class, main);
    sql.main("select scm_vendor_bank_contact.id,scm_vendor_bank_contact.vendor_bank_id,scm_vendor_bank_contact.contact_name,scm_vendor_bank_contact.designation_id,scm_vendor_bank_contact.phone_1,scm_vendor_bank_contact.phone_2,scm_vendor_bank_contact.fax_1,scm_vendor_bank_contact.email,scm_vendor_bank_contact.sort_order,scm_vendor_bank_contact.status_id,scm_vendor_bank_contact.created_by,scm_vendor_bank_contact.modified_by,scm_vendor_bank_contact.created_at,scm_vendor_bank_contact.modified_at from scm_vendor_bank_contact scm_vendor_bank_contact "); //Main query
    sql.count("select count(scm_vendor_bank_contact.id) from scm_vendor_bank_contact scm_vendor_bank_contact "); //Count query
    sql.join("left outer join scm_vendor_bank scm_vendor_bank_contactvendor_bank_id on (scm_vendor_bank_contactvendor_bank_id.id = scm_vendor_bank_contact.vendor_bank_id) left outer join scm_designation scm_vendor_bank_contactdesignation_id on (scm_vendor_bank_contactdesignation_id.id = scm_vendor_bank_contact.designation_id)left outer join scm_status scm_vendor_bank_contactstatus_id on (scm_vendor_bank_contactstatus_id.id = scm_vendor_bank_contact.status_id)"); //Join Query

    sql.string(new String[]{"scm_vendor_bank_contactvendor_bank_id.account_name", "scm_vendor_bank_contact.contact_name", "scm_vendor_bank_contactdesignation_id.title", "scm_vendor_bank_contact.phone_1", "scm_vendor_bank_contact.phone_2", "scm_vendor_bank_contact.fax_1", "scm_vendor_bank_contact.email", "scm_vendor_bank_contact.created_by", "scm_vendor_bank_contact.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_vendor_bank_contact.id", "scm_vendor_bank_contact.sort_order", "scm_vendor_bank_contact.status_id"}); //Number search or sort fields
    sql.date(new String[]{"scm_vendor_bank_contact.created_at", "scm_vendor_bank_contact.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of VendorBankContact.
   *
   * @param main
   * @param vendorBank
   * @return List of VendorBankContact
   */
  public static final List<VendorBankContact> listPaged(Main main, VendorBank vendorBank) {
    SqlPage sql = getVendorBankContactSqlPaged(main);
    if (vendorBank.getId() == null) {
      return null;
    }
    sql.cond("where scm_vendor_bank_contact.vendor_bank_id=?");
    sql.param(vendorBank.getId());
    return AppService.listPagedJpa(main, sql);
  }

  /**
   * Return all bank contact of a vendor.
   *
   * @param main
   * @param vendorBank
   * @return
   */
  public static final List<VendorBankContact> contactListByVendorBank(Main main, VendorBank vendorBank) {
    SqlPage sql = getVendorBankContactSqlPaged(main);
    if (vendorBank.getId() == null) {
      return null;
    }
    sql.cond("where scm_vendor_bank_contact.vendor_bank_id=?");
    sql.param(vendorBank.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of VendorBankContact based on condition
//   * @param main
//   * @return List<VendorBankContact>
//   */
//  public static final List<VendorBankContact> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getVendorBankContactSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select VendorBankContact by key.
   *
   * @param main
   * @param vendorBankContact
   * @return VendorBankContact
   */
  public static final VendorBankContact selectByPk(Main main, VendorBankContact vendorBankContact) {
    return (VendorBankContact) AppService.find(main, VendorBankContact.class, vendorBankContact.getId());
  }

  /**
   * Insert VendorBankContact.
   *
   * @param main
   * @param vendorBankContact
   */
  public static final void insert(Main main, VendorBankContact vendorBankContact) {
    VendorBankContactIs.insertAble(main, vendorBankContact);  //Validating
    AppService.insert(main, vendorBankContact);

  }

  /**
   * Update VendorBankContact by key.
   *
   * @param main
   * @param vendorBankContact
   * @return VendorBankContact
   */
  public static final VendorBankContact updateByPk(Main main, VendorBankContact vendorBankContact) {
    VendorBankContactIs.updateAble(main, vendorBankContact); //Validating
    return (VendorBankContact) AppService.update(main, vendorBankContact);
  }

  /**
   * Insert or update VendorBankContact
   *
   * @param main
   * @param vendorBankContact
   */
  public static void insertOrUpdate(Main main, VendorBankContact vendorBankContact) {
    if (vendorBankContact.getId() == null) {
      insert(main, vendorBankContact);
    } else {
      updateByPk(main, vendorBankContact);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param vendorBankContact
   */
  public static void clone(Main main, VendorBankContact vendorBankContact) {
    vendorBankContact.setId(null); //Set to null for insert
    insert(main, vendorBankContact);
  }

  /**
   * Delete VendorBankContact.
   *
   * @param main
   * @param vendorBankContact
   */
  public static final void deleteByPk(Main main, VendorBankContact vendorBankContact) {
    VendorBankContactIs.deleteAble(main, vendorBankContact); //Validation
    AppService.delete(main, VendorBankContact.class, vendorBankContact.getId());
  }

  /**
   * Delete Array of VendorBankContact.
   *
   * @param main
   * @param vendorBankContact
   */
  public static final void deleteByPkArray(Main main, VendorBankContact[] vendorBankContact) {
    for (VendorBankContact e : vendorBankContact) {
      deleteByPk(main, e);
    }
  }
}
