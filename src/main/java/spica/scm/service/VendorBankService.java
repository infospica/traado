/*
 * @(#)VendorBankService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import spica.scm.domain.Vendor;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.VendorBank;
import spica.scm.domain.VendorBankContact;
import spica.scm.validate.VendorBankIs;
import wawo.app.faces.MainView;

/**
 * VendorBankService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class VendorBankService {

  /**
   * VendorBank paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getVendorBankSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_vendor_bank", VendorBank.class, main);
    sql.main("select scm_vendor_bank.id,scm_vendor_bank.vendor_id,scm_vendor_bank.bank_account_type_id,scm_vendor_bank.account_name,scm_vendor_bank.account_no,scm_vendor_bank.bank_id,scm_vendor_bank.branch_name,scm_vendor_bank.branch_code,scm_vendor_bank.ifsc_code,scm_vendor_bank.branch_address,scm_vendor_bank.sort_order,scm_vendor_bank.status_id,scm_vendor_bank.created_by,scm_vendor_bank.modified_by,scm_vendor_bank.created_at,scm_vendor_bank.modified_at from scm_vendor_bank scm_vendor_bank "); //Main query
    sql.count("select count(scm_vendor_bank.id) as total from scm_vendor_bank scm_vendor_bank "); //Count query
    sql.join("left outer join scm_vendor scm_vendor_bankvendor_id on (scm_vendor_bankvendor_id.id = scm_vendor_bank.vendor_id) left outer join scm_bank_account_type scm_vendor_bankbank_account_type_id on (scm_vendor_bankbank_account_type_id.id = scm_vendor_bank.bank_account_type_id) left outer join scm_bank scm_vendor_bankbank_id on (scm_vendor_bankbank_id.id = scm_vendor_bank.bank_id) left outer join scm_status scm_vendor_bankstatus_id on (scm_vendor_bankstatus_id.id = scm_vendor_bank.status_id)"); //Join Query

    sql.string(new String[]{"scm_vendor_bankvendor_id.vendor_name", "scm_vendor_bankbank_account_type_id.title", "scm_vendor_bank.account_name", "scm_vendor_bank.account_no", "scm_vendor_bankbank_id.name", "scm_vendor_bank.branch_name", "scm_vendor_bank.branch_code", "scm_vendor_bank.ifsc_code", "scm_vendor_bank.branch_address", "scm_vendor_bankstatus_id.title", "scm_vendor_bank.created_by", "scm_vendor_bank.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_vendor_bank.id", "scm_vendor_bank.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_vendor_bank.created_at", "scm_vendor_bank.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of VendorBank.
   *
   * @param main
   * @param company
   * @return List of VendorBank
   */
  public static final List<VendorBank> listPaged(Main main, Company company) {
    SqlPage sql = getVendorBankSqlPaged(main);
    sql.cond("where scm_vendor_bank.vendor_id in(select scm_vendor.id from scm_vendor scm_vendor where scm_vendor.company_id=?)");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, getVendorBankSqlPaged(main));
  }

  /**
   * Return all bank of a vendor.
   *
   * @param main
   * @param vendor
   * @return
   */
  public static final List<VendorBank> bankListByVendor(Main main, Vendor vendor) {
    SqlPage sql = getVendorBankSqlPaged(main);
    if (vendor.getId() == null) {
      return null;
    }
    sql.cond("where scm_vendor_bank.vendor_id=?");
    sql.param(vendor.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of VendorBank based on condition
//   * @param main
//   * @return List<VendorBank>
//   */
//  public static final List<VendorBank> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getVendorBankSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select VendorBank by key.
   *
   * @param main
   * @param vendorBank
   * @return VendorBank
   */
  public static final VendorBank selectByPk(Main main, VendorBank vendorBank) {
    return (VendorBank) AppService.find(main, VendorBank.class, vendorBank.getId());
  }

  /**
   * Insert VendorBank.
   *
   * @param main
   * @param vendorBank
   */
  public static final void insert(Main main, VendorBank vendorBank) {
    VendorBankIs.insertAble(main, vendorBank);  //Validating
    AppService.insert(main, vendorBank);

  }

  /**
   * Update VendorBank by key.
   *
   * @param main
   * @param vendorBank
   * @return VendorBank
   */
  public static final VendorBank updateByPk(Main main, VendorBank vendorBank) {
    VendorBankIs.updateAble(main, vendorBank); //Validating
    return (VendorBank) AppService.update(main, vendorBank);
  }

  /**
   * Insert or update VendorBank
   *
   * @param main
   * @param VendorBank
   */
  public static void insertOrUpdate(Main main, VendorBank vendorBank) {
    if (vendorBank.getId() == null) {
      insert(main, vendorBank);
    } else {
      updateByPk(main, vendorBank);
    }
  }

  /**
   * Making default for newly added or updated if isdefault is checked
   *
   * @param main
   * @param vendorBank
   */
//  public static void makeDefault(Main main, VendorBank vendorBank) {
//    if (vendorBank.getSortOrder() == 0) {
//      main.param(1);
//      main.param(vendorBank.getVendorId().getId());
//      main.param(vendorBank.getId());
//      main.param(0);
//      AppService.updateSql(main, VendorBank.class, "update scm_vendor_bank set modified_by = ?, lasdt_modified_at = ?, sort_order = ? where vendor_id = ? and id <> ? and sort_order = ?", true);
//    }
//  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param vendorBank
   */
  public static void clone(Main main, VendorBank vendorBank) {
    vendorBank.setId(null); //Set to null for insert
    insert(main, vendorBank);
  }

  /**
   * Delete VendorBank.
   *
   * @param main
   * @param vendorBank
   */
  public static final void deleteByPk(Main main, VendorBank vendorBank) {
    VendorBankIs.deleteAble(main, vendorBank); //Validation
    AppService.deleteSql(main, VendorBankContact.class, "delete from scm_vendor_bank_contact scm_vendor_bank_contact where scm_vendor_bank_contact.vendor_bank_id=?", new Object[]{vendorBank.getId()});
    AppService.delete(main, VendorBank.class, vendorBank.getId());
  }

  /**
   * Delete Array of VendorBank.
   *
   * @param main
   * @param vendorBank
   */
  public static final void deleteByPkArray(Main main, VendorBank[] vendorBank) {
    for (VendorBank e : vendorBank) {
      deleteByPk(main, e);
    }
  }
}
