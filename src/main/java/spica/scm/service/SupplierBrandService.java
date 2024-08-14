/*
 * @(#)SupplierBrandService.java	1.0 Mon Aug 21 14:03:19 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupBrands;
import spica.scm.domain.Brand;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SupplierBrand;
import spica.scm.domain.Vendor;
import spica.scm.validate.SupplierBrandIs;
import wawo.entity.core.AppDb;

/**
 * SupplierBrandService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 21 14:03:19 IST 2017
 */
public abstract class SupplierBrandService {

  /**
   * SupplierBrand paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSupplierBrandSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_supplier_brand", SupplierBrand.class, main);
    sql.main("select t1.id,t1.brand_id,t1.vendor_id from scm_supplier_brand t1 "); //Main query
    sql.count("select count(t1.id) as total from scm_supplier_brand t1 "); //Count query
    sql.join("left outer join scm_vendor t2 on (t2.id = t1.vendor_id) left outer join scm_brand t3 on (t3.id = t1.brand_id)"); //Join Query

    sql.string(new String[]{"t2.vendor_name", "t3.name"}); //String search or sort fields
    sql.number(new String[]{"t1.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SupplierBrand.
   *
   * @param main
   * @return List of SupplierBrand
   */
  public static final List<SupplierBrand> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSupplierBrandSqlPaged(main));
  }

//  /**
//   * Return list of SupplierBrand based on condition
//   * @param main
//   * @return List<ProductManufacture>
//   */
//  public static final List<ProductManufacture> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductManufactureSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SupplierBrand by key.
   *
   * @param main
   * @param supplierBrand
   * @return SupplierBrand
   */
  public static final SupplierBrand selectByPk(Main main, SupplierBrand supplierBrand) {
    return (SupplierBrand) AppService.find(main, SupplierBrand.class, supplierBrand.getId());
  }

  /**
   * Insert SupplierBrand.
   *
   * @param main
   * @param supplierBrand
   */
  public static final void insert(Main main, SupplierBrand supplierBrand) {
    SupplierBrandIs.insertAble(main, supplierBrand);  //Validating
    AppService.insert(main, supplierBrand);

  }

  /**
   * Update SupplierBrand by key.
   *
   * @param main
   * @param supplierBrand
   * @return SupplierBrand
   */
  public static final SupplierBrand updateByPk(Main main, SupplierBrand supplierBrand) {
    SupplierBrandIs.updateAble(main, supplierBrand); //Validating
    return (SupplierBrand) AppService.update(main, supplierBrand);
  }

  /**
   * Insert or update SupplierBrand
   *
   * @param main
   * @param supplierBrand
   */
  public static void insertOrUpdate(Main main, SupplierBrand supplierBrand) {
    if (supplierBrand.getId() == null) {
      insert(main, supplierBrand);
    } else {
      updateByPk(main, supplierBrand);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param supplierBrand
   */
  public static void clone(Main main, SupplierBrand supplierBrand) {
    supplierBrand.setId(null); //Set to null for insert
    insert(main, supplierBrand);
  }

  /**
   * Delete SupplierBrand.
   *
   * @param main
   * @param supplierBrand
   */
  public static final void deleteByPk(Main main, SupplierBrand supplierBrand) {
    SupplierBrandIs.deleteAble(main, supplierBrand); //Validation
    AppService.delete(main, SupplierBrand.class, supplierBrand.getId());
  }

  /**
   * Delete Array of SupplierBrand.
   *
   * @param main
   * @param supplierBrands
   */
  public static final void deleteByPkArray(Main main, SupplierBrand[] supplierBrands) {
    for (SupplierBrand e : supplierBrands) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param vendor
   * @param brandList
   */
  public static void insertOrUpdate(Main main, Vendor vendor, List<Brand> brandList) {
    if (vendor.getId() != null) {
      AppService.deleteSql(main, SupplierBrand.class, "delete from scm_supplier_brand where vendor_id = ?", new Object[]{vendor.getId()});
      if (brandList != null && !brandList.isEmpty()) {
        for (Brand brand : brandList) {
          SupplierBrandService.insert(main, new SupplierBrand(brand, vendor));
        }
      }
      Account account = AccountService.selectAccountByVendor(main, vendor);
      if (account != null) {
        AccountGroup group = AccountGroupService.selectDefaultAccountGroupByAccount(main, account);
        if (group != null) {
          if (brandList != null && !brandList.isEmpty()) {
            for (Brand brand : brandList) {
              AppService.deleteSql(main, AccountGroupBrands.class, "delete from scm_account_group_brands where account_group_id =? and brand_id=? ", new Object[]{group.getId(), brand.getId()});
              AccountGroupBrandsService.insert(main, new AccountGroupBrands(brand, group));
            }
          }
          String code = updateHashCode(brandList, group, account);
          group.setAccountGroupHashCode(code);
          AppService.insert(main, group);
        }
      }
    }
  }

  public static String updateHashCode(List<Brand> brandList, AccountGroup group, Account account) {
    String hashCode = "", brandCode;
    String accId;
    if (brandList != null && !brandList.isEmpty()) {
      List<String> bList = new ArrayList<String>();
      for (Brand b : brandList) {
        bList.add(String.valueOf(b.getId()));
      }
      Collections.sort(bList);
      if (group.getAccountGroupHashCode() != null && !group.getAccountGroupHashCode().isEmpty()) {
        String hash[] = group.getAccountGroupHashCode().split("@");
        accId = hash[0];
      } else {
        accId = String.valueOf(account.getId());
      }
      brandCode = getHashCode(bList);
      hashCode = accId + "@" + brandCode;
    }
    return hashCode;

  }

  public static String getHashCode(List<String> bList) {
    String hc = "";
    for (int i = 0; i < bList.size(); i++) {
      hc += bList.get(i);
      if (i + 1 < bList.size()) {
        hc += "#";
      }
    }
    return hc;
  }

  /**
   *
   * @param main
   * @param vendor
   * @return
   */
  public static List<Brand> selectBrandByVendor(Main main, Vendor vendor) {
    return AppService.list(main, Brand.class, "select id, code from scm_brand where id in (select brand_id from scm_supplier_brand where vendor_id = ?)", new Object[]{vendor.getId()});
  }
}
