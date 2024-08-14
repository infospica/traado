/*
 * @(#)ProductUnitService.java	1.0 Wed Oct 19 11:15:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.ProductPacking;
import spica.scm.domain.ProductPackingUnit;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductUnit;
import spica.scm.validate.ProductUnitIs;
import wawo.app.faces.MainView;

/**
 * ProductUnitService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Oct 19 11:15:25 IST 2016
 */
public abstract class ProductUnitService {

  /**
   * ProductUnit paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductUnitSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_unit", ProductUnit.class, main);
    sql.main("select scm_product_unit.id,scm_product_unit.title,scm_product_unit.symbol,scm_product_unit.sort_order,scm_product_unit.created_by,scm_product_unit.modified_by,scm_product_unit.created_at,scm_product_unit.modified_at from scm_product_unit scm_product_unit"); //Main query
    sql.count("select count(scm_product_unit.id) as total from scm_product_unit scm_product_unit"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_product_unit.title", "scm_product_unit.symbol", "scm_product_unit.created_by", "scm_product_unit.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_unit.id", "scm_product_unit.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_unit.created_at", "scm_product_unit.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductUnit.
   *
   * @param main
   * @return List of ProductUnit
   */
  public static final List<ProductUnit> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductUnitSqlPaged(main));
  }

//  /**
//   * Return list of ProductUnit based on condition
//   * @param main
//   * @return List<ProductUnit>
//   */
//  public static final List<ProductUnit> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductUnitSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductUnit by key.
   *
   * @param main
   * @param productUnit
   * @return ProductUnit
   */
  public static final ProductUnit selectByPk(Main main, ProductUnit productUnit) {
    return (ProductUnit) AppService.find(main, ProductUnit.class, productUnit.getId());
  }

  /**
   * Insert ProductUnit.
   *
   * @param main
   * @param productUnit
   */
  public static final void insert(Main main, ProductUnit productUnit) {
    ProductUnitIs.insertAble(main, productUnit);  //Validating
    AppService.insert(main, productUnit);

  }

  /**
   * Update ProductUnit by key.
   *
   * @param main
   * @param productUnit
   * @return ProductUnit
   */
  public static final ProductUnit updateByPk(Main main, ProductUnit productUnit) {
    ProductUnitIs.updateAble(main, productUnit); //Validating
    return (ProductUnit) AppService.update(main, productUnit);
  }

  /**
   * Insert or update ProductUnit
   *
   * @param main
   * @param productUnit
   */
  public static void insertOrUpdate(Main main, ProductUnit productUnit) {
    if (productUnit.getId() == null) {
      insert(main, productUnit);
    } else {
      updateByPk(main, productUnit);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productUnit
   */
  public static void clone(Main main, ProductUnit productUnit) {
    productUnit.setId(null); //Set to null for insert
    insert(main, productUnit);
  }

  /**
   * Delete ProductUnit.
   *
   * @param main
   * @param productUnit
   */
  public static final void deleteByPk(Main main, ProductUnit productUnit) {
    ProductUnitIs.deleteAble(main, productUnit); //Validation
    AppService.deleteSql(main, ProductPackingUnit.class, "delete from scm_product_packing_unit where scm_product_packing_unit.product_unit_id=?", new Object[]{productUnit.getId()});
    AppService.delete(main, ProductUnit.class, productUnit.getId());
  }

  /**
   * Delete Array of ProductUnit.
   *
   * @param main
   * @param productUnit
   */
  public static final void deleteByPkArray(Main main, ProductUnit[] productUnit) {
    for (ProductUnit e : productUnit) {
      deleteByPk(main, e);
    }
  }

  public static final List<ProductUnit> productPackingUnit(MainView main) {
    return AppService.list(main, ProductUnit.class, "select id,title from scm_product_unit", new Object[]{});
  }

  public static List<ProductUnit> selectUnitByPacking(Main main, ProductPacking productPacking) {
    SqlPage sql = getProductUnitSqlPaged(main);
    if (productPacking.getId() == null) {
      return null;
    }
    sql.join("left outer join scm_product_packing_unit scm_product_packing_unit_id on (scm_product_packing_unit_id.product_unit_id = scm_product_unit.id) where scm_product_packing_unit_id.product_packing_id=?");
    sql.param(productPacking.getId());
    return AppService.listAllJpa(main, sql);
  }
}
