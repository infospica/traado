/*
 * @(#)ManufactureProductService.java	1.0 Mon Aug 21 14:03:19 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Brand;
import spica.scm.domain.Manufacture;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ManufactureProduct;
import spica.scm.domain.Product;
import spica.scm.domain.Vendor;

/**
 * ManufactureProductService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 21 14:03:19 IST 2017
 */
public abstract class ManufactureProductService {

  /**
   * ManufactureProductService paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getManufactureProductSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_manufacture_product", ManufactureProduct.class, main);
    sql.main("select t1.id,t1.product_id,t1.manufacture_id from scm_manufacture_product t1 "); //Main query
    sql.count("select count(t1.id) as total from scm_manufacture_product t1 "); //Count query
    sql.join("left outer join scm_product t2 on (t2.id = t1.product_id) left outer join scm_manufacture t3 on (t3.id = t1.manufacture_id)"); //Join Query

    sql.string(new String[]{"t2.product_name", "t3.name"}); //String search or sort fields
    sql.number(new String[]{"t1.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ManufactureProduct.
   *
   * @param main
   * @return List of ManufactureProduct
   */
  public static final List<ManufactureProduct> listPaged(Main main) {
    return AppService.listPagedJpa(main, getManufactureProductSqlPaged(main));
  }

//  /**
//   * Return list of ManufactureProduct based on condition
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
   * Select ManufactureProduct by key.
   *
   * @param main
   * @param manufactureProduct
   * @return ManufactureProduct
   */
  public static final ManufactureProduct selectByPk(Main main, ManufactureProduct manufactureProduct) {
    return (ManufactureProduct) AppService.find(main, ManufactureProduct.class, manufactureProduct.getId());
  }

  /**
   * Insert ManufactureProduct.
   *
   * @param main
   * @param manufactureProduct
   */
  public static final void insert(Main main, ManufactureProduct manufactureProduct) {
//    ManufactureProductIs.insertAble(main, manufactureProduct);  //Validating
    AppService.insert(main, manufactureProduct);

  }

  /**
   * Update ManufactureProduct by key.
   *
   * @param main
   * @param manufactureProduct
   * @return ManufactureProduct
   */
  public static final ManufactureProduct updateByPk(Main main, ManufactureProduct manufactureProduct) {
    //   ManufactureProductIs.updateAble(main, manufactureProduct); //Validating
    return (ManufactureProduct) AppService.update(main, manufactureProduct);
  }

  /**
   * Insert or update ManufactureProduct
   *
   * @param main
   * @param manufactureProduct
   */
  public static void insertOrUpdate(Main main, ManufactureProduct manufactureProduct) {
    if (manufactureProduct.getId() == null) {
      insert(main, manufactureProduct);
    } else {
      updateByPk(main, manufactureProduct);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param manufactureProduct
   */
  public static void clone(Main main, ManufactureProduct manufactureProduct) {
    manufactureProduct.setId(null); //Set to null for insert
    insert(main, manufactureProduct);
  }

  /**
   * Delete ManufactureProduct.
   *
   * @param main
   * @param manufactureProduct
   */
  public static final void deleteByPk(Main main, ManufactureProduct manufactureProduct) {
//    ManufactureProductIs.deleteAble(main, manufactureProduct); //Validation
    AppService.delete(main, ManufactureProduct.class, manufactureProduct.getId());
  }

  /**
   * Delete Array of ManufactureProduct.
   *
   * @param main
   * @param manufactureProducts
   */
  public static final void deleteByPkArray(Main main, ManufactureProduct[] manufactureProducts) {
    for (ManufactureProduct e : manufactureProducts) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param product
   * @param manufactureList
   */
  public static void insertOrUpdate(Main main, Product product, List<Manufacture> manufactureList) {
    if (product.getId() != null) {
      AppService.deleteSql(main, ManufactureProduct.class, "delete from scm_manufacture_product where product_id = ?", new Object[]{product.getId()});
      if (manufactureList != null && !manufactureList.isEmpty()) {
        for (Manufacture manufacture : manufactureList) {
          ManufactureProductService.insert(main, new ManufactureProduct(manufacture, product));
        }
      }
    }
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
