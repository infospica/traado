/*
 * @(#)VendorService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import spica.fin.service.LedgerExternalService;
import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupBrands;
import spica.scm.domain.AccountGroupDetail;
import spica.scm.domain.AccountGroupDocPrefix;
import spica.scm.domain.Brand;
import spica.scm.domain.Company;
import spica.scm.domain.Contract;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorBank;
import spica.scm.domain.VendorCommodity;
import spica.scm.domain.VendorContact;
import spica.scm.domain.VendorLicense;
import spica.scm.validate.VendorIs;
import spica.scm.domain.SupplierBrand;
import spica.scm.domain.UserProfile;

/**
 * VendorService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class VendorService {

  public static final Integer VENDOR_TYPE_PRIMARY = 1;
  public static final Integer VENDOR_TYPE_SECONDARY = 2;
  public static final Integer IS_MANUFACTURER = 1;
  public static final String[] VENDOR_TYPE = new String[]{"1:Primary", "2:Secondary"};

  /**
   * Vendor paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getVendorSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_vendor", Vendor.class, main);
    sql.main("select scm_vendor.id,scm_vendor.supplier_group_id,scm_vendor.company_id,scm_vendor.is_manufacturer,scm_vendor.vendor_code,scm_vendor.vendor_name,scm_vendor.description,scm_vendor.pan_no,scm_vendor.tax_applicable,scm_vendor.status_id,scm_vendor.vendor_type,scm_vendor.parent_id,scm_vendor.cst_no,scm_vendor.vat_no,scm_vendor.tin_no,scm_vendor.gst_no,scm_vendor.created_at,scm_vendor.modified_at,scm_vendor.created_by,scm_vendor.modified_by,scm_vendor.taxable from scm_vendor scm_vendor "); //Main query
    sql.count("select count(scm_vendor.id) as total from scm_vendor scm_vendor "); //Count query
    sql.join("left outer join scm_supplier_group scm_supplier_group on (scm_supplier_group.id = scm_vendor.supplier_group_id) left outer join scm_company scm_vendorcompany_id on (scm_vendorcompany_id.id = scm_vendor.company_id) left outer join scm_status scm_vendorstatus_id on (scm_vendorstatus_id.id = scm_vendor.status_id) left outer join scm_vendor scm_vendorparent_id on (scm_vendorparent_id.id = scm_vendor.parent_id)"); //Join Query

    sql.string(new String[]{"scm_supplier_group.title", "scm_vendorcompany_id.company_name", "scm_vendor.vendor_name", "scm_vendor.description", "scm_vendor.pan_no", "scm_vendorstatus_id.title", "scm_vendorparent_id.vendor_name", "scm_vendor.cst_no", "scm_vendor.vat_no", "scm_vendor.tin_no", "scm_vendor.gst_no", "scm_vendor.created_by", "scm_vendor.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_vendor.id", "scm_vendor.tax_applicable", "scm_vendor.vendor_type"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_vendor.created_at", "scm_vendor.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Vendor.
   *
   * @param main
   * @param company
   * @return List of Vendor
   */
  public static final List<Vendor> listPaged(Main main, Company company) {
    SqlPage sql = getVendorSqlPaged(main);
    sql.cond("where scm_vendor.company_id = ?");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, getVendorSqlPaged(main));
  }

//  /**
//   * Return list of Vendor based on condition
//   * @param main
//   * @return List<Vendor>
//   */
//  public static final List<Vendor> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getVendorSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Vendor by key.
   *
   * @param main
   * @param vendor
   * @return Vendor
   */
  public static final Vendor selectByPk(Main main, Vendor vendor) {
    return (Vendor) AppService.find(main, Vendor.class, vendor.getId());
  }

  /**
   * Insert Vendor.
   *
   * @param main
   * @param vendor
   */
  public static final void insert(Main main, Vendor vendor) {
    VendorIs.insertAble(main, vendor);  //Validating
    AppService.insert(main, vendor);
    LedgerExternalService.saveLedgerVendor(main, vendor);
  }

  /**
   * Update Vendor by key.
   *
   * @param main
   * @param vendor
   * @return Vendor
   */
  public static final Vendor updateByPk(Main main, Vendor vendor) {
    VendorIs.updateAble(main, vendor); //Validating
    LedgerExternalService.saveLedgerVendor(main, vendor);
    return (Vendor) AppService.update(main, vendor);
  }

  /**
   * Insert or update Vendor
   *
   * @param main
   * @param vendor
   */
  public static void insertOrUpdate(Main main, Vendor vendor) {
    if (vendor.getId() == null) {
      insert(main, vendor);
    } else {
      updateByPk(main, vendor);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param vendor
   */
  public static void clone(Main main, Vendor vendor) {
    vendor.setId(null); //Set to null for insert
    insert(main, vendor);
  }

  /**
   * Delete Vendor.
   *
   * @param main
   * @param vendor
   */
  public static final void deleteByPk(Main main, Vendor vendor) {
    LedgerExternalService.deleteLedgerVendor(main, vendor);
    AppService.deleteSql(main, Contract.class, "DELETE FROM scm_contract WHERE scm_contract.account_id = (SELECT id FROM scm_account WHERE vendor_id = ?)", new Object[]{vendor.getId()});
    AppService.deleteSql(main, AccountGroupDetail.class, "DELETE FROM scm_account_group_detail WHERE scm_account_group_detail.account_id = (SELECT id FROM scm_account WHERE vendor_id = ?)", new Object[]{vendor.getId()});
    AppService.deleteSql(main, AccountGroupDocPrefix.class, "DELETE FROM scm_account_group_doc_prefix WHERE scm_account_group_doc_prefix.account_id = (SELECT id FROM scm_account WHERE vendor_id = ?)", new Object[]{vendor.getId()});
    AppService.deleteSql(main, Account.class, "delete from scm_account where vendor_id=?", new Object[]{vendor.getId()});
    AppService.deleteSql(main, VendorAddress.class, "delete from scm_vendor_address where vendor_id=?", new Object[]{vendor.getId()});
    AppService.deleteSql(main, VendorContact.class, "delete from scm_vendor_contact where vendor_id=?", new Object[]{vendor.getId()});
    AppService.deleteSql(main, VendorBank.class, "delete from scm_vendor_bank where vendor_id = ?", new Object[]{vendor.getId()});
    AppService.deleteSql(main, VendorLicense.class, "delete from scm_vendor_license where vendor_id = ?", new Object[]{vendor.getId()});
    AppService.deleteSql(main, VendorCommodity.class, "delete from scm_vendor_commodity where vendor_id = ?", new Object[]{vendor.getId()});
    AppService.deleteSql(main, SupplierBrand.class, "delete from scm_supplier_brand where vendor_id = ?", new Object[]{vendor.getId()});
    AppService.deleteSql(main, UserProfile.class, "delete from scm_user_profile where vendor_id = ? ", new Object[]{vendor.getId()});
//    AppService.deleteSql(main, Account.class, "delete from scm_account scm_account where scm_account.vendor_id=?", new Object[]{vendor.getId()});
    AppService.delete(main, Vendor.class, vendor.getId());
  }

  /**
   * Delete Array of Vendor.
   *
   * @param main
   * @param vendor
   */
  public static final void deleteByPkArray(Main main, Vendor[] vendor) {
    for (Vendor e : vendor) {
      deleteByPk(main, e);
    }
  }

  public static final boolean isVendorHasAccountOrSecondaryVendor(Main main, Vendor vendor) {
    long accountCount = AppService.count(main, "select count(id) from scm_account where vendor_id = ?", new Object[]{vendor.getId()});
    long secondaryCount = AppService.count(main, "select count(id) from scm_vendor where parent_id = ?", new Object[]{vendor.getId()});
    return ((accountCount + secondaryCount) <= 0);
  }

  public static long isVendorExist(Main main, Integer vendorId, List<Brand> brandList) {
    StringBuilder sb = new StringBuilder();
    String whereCondition = "";
    int listSize = brandList.size();
    for (Brand brand : brandList) {
      if (sb.length() == 0) {
        sb.append(brand.getId());
      } else {
        sb.append(",").append(brand.getId());
      }
    }
    if (vendorId != null) {
      whereCondition = " vendor_id <> " + vendorId + " and ";
    }
    long count = main.em().count("select count(brand_id) manufacture_count from scm_supplier_brand "
            + "where " + whereCondition + " vendor_id in ( "
            + "select vendor_id from scm_supplier_brand where brand_id in (" + sb.toString() + ") "
            + "GROUP by vendor_id "
            + "having count(vendor_id) = " + listSize
            + ") GROUP by vendor_id having count(brand_id) = " + listSize, null);

    return count;
  }

  public static boolean isRemovableBrand(Main main, Brand brand, Vendor vendor) {
    if (vendor.getId() == null) {
      return true;
    }
    Account account = AccountService.selectAccountByVendor(main, vendor);
    String sql = null;
    if (account != null && account.getId() != null) {
      sql = "SELECT AccGrpBrd.* FROM scm_account_group_detail as AccGrp,scm_account_group_brands as AccGrpBrd   \n"
              + "WHERE AccGrp.account_group_id=AccGrpBrd.account_group_id  AND AccGrp.account_id  = ? AND AccGrpBrd.brand_id= ? ";

      List<AccountGroupBrands> listBrands = AppService.list(main, AccountGroupBrands.class, sql, new Object[]{account.getId(), brand.getId()});
      if (listBrands != null && listBrands.size() > 1) {
        return false;
      } else {
        sql = "SELECT COUNT(ProdPre.account_id) FROM scm_product AS Prod,scm_product_preset AS ProdPre WHERE Prod.id = ProdPre.product_id "
                + "AND ProdPre.account_id = ? AND  Prod.brand_id= ? ";
        if (AppService.count(main, sql, new Object[]{account.getId(), brand.getId()}) > 1) {
          return false;
        }
      }
    }
    return true;
  }

  public static List<UserProfile> selectVendorsByCustomerAndAccountGroup(Main main, Integer customerId, Integer companyId, AccountGroup accountGroup) {
    main.clear();
    String sql = "select t1.* from scm_user_profile t1,scm_sales_agent_territory t2,scm_territory_district t3,scm_customer_address t4\n"
            + "where t1.id = t2.sales_agent_id\n"
            + "AND t2.territory_id = t3.territory_id\n"
            + "AND t3.district_id = t4.district_id\n"
            + "AND t4.customer_id = ? and t1.company_id = ? \n";
    main.param(customerId);
    main.param(companyId);
    if (accountGroup != null && accountGroup.getId() != null) {
      sql += " AND t1.id in( select sales_agent_id from scm_sales_agent_account_group where account_group_id = ?)";
      main.param(accountGroup.getId());
    } else {
      sql += "AND t1.id in( select sales_agent_id from scm_sales_agent_account_group where account_group_id in( select ag.id from scm_account_group ag inner join scm_sales_account sa on ag.id = sa.account_group_id where sa.customer_id = ? ))";
      main.param(customerId);
    }
    return AppService.list(main, UserProfile.class, sql, main.getParamData().toArray());
  }
}
