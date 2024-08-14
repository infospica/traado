/*
 * @(#)ProductPackingUnitService.java	1.0 Wed Oct 19 11:15:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.ProductPackingDetail;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductPackingUnit;
import spica.scm.domain.ProductUnit;
import spica.scm.validate.ProductPackingUnitIs;

/**
 * ProductPackingUnitService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Oct 19 11:15:25 IST 2016
 */
public abstract class ProductPackingUnitService {

  /**
   * ProductPackingUnit paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductPackingUnitSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_packing_unit", ProductPackingUnit.class, main);
    sql.main("select scm_product_packing_unit.id,scm_product_packing_unit.product_unit_id,scm_product_packing_unit.product_packing_id from scm_product_packing_unit scm_product_packing_unit "); //Main query
    sql.count("select count(scm_product_packing_unit.id) as total from scm_product_packing_unit scm_product_packing_unit "); //Count query
    sql.join("left outer join scm_product_packing scm_product_packing_unitproduct_packing_id on (scm_product_packing_unitproduct_packing_id.id = scm_product_packing_unit.product_packing_id)"); //Join Query

    sql.string(new String[]{"scm_product_packing_unitproduct_packing_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_product_packing_unit.id", "scm_product_packing_unit.product_unit_id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductPackingUnit.
   *
   * @param main
   * @return List of ProductPackingUnit
   */
  public static final List<ProductPackingUnit> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductPackingUnitSqlPaged(main));
  }

//  /**
//   * Return list of ProductPackingUnit based on condition
//   * @param main
//   * @return List<ProductPackingUnit>
//   */
//  public static final List<ProductPackingUnit> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductPackingUnitSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductPackingUnit by key.
   *
   * @param main
   * @param productPackingUnit
   * @return ProductPackingUnit
   */
  public static final ProductPackingUnit selectByPk(Main main, ProductPackingUnit productPackingUnit) {
    return (ProductPackingUnit) AppService.find(main, ProductPackingUnit.class, productPackingUnit.getId());
  }

  /**
   * Insert ProductPackingUnit.
   *
   * @param main
   * @param productPackingUnit
   */
  public static final void insert(Main main, ProductPackingUnit productPackingUnit) {
    ProductPackingUnitIs.insertAble(main, productPackingUnit);  //Validating
    AppService.insert(main, productPackingUnit);

  }

  /**
   * Update ProductPackingUnit by key.
   *
   * @param main
   * @param productPackingUnit
   * @return ProductPackingUnit
   */
  public static final ProductPackingUnit updateByPk(Main main, ProductPackingUnit productPackingUnit) {
    ProductPackingUnitIs.updateAble(main, productPackingUnit); //Validating
    return (ProductPackingUnit) AppService.update(main, productPackingUnit);
  }

  /**
   * Insert or update ProductPackingUnit
   *
   * @param main
   * @param productPackingUnit
   */
  public static void insertOrUpdate(Main main, ProductPackingUnit productPackingUnit) {
    if (productPackingUnit.getId() == null) {
      insert(main, productPackingUnit);
    } else {
      updateByPk(main, productPackingUnit);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productPackingUnit
   */
  public static void clone(Main main, ProductPackingUnit productPackingUnit) {
    productPackingUnit.setId(null); //Set to null for insert
    insert(main, productPackingUnit);
  }

  /**
   * Delete ProductPackingUnit.
   *
   * @param main
   * @param productPackingUnit
   */
  public static final void deleteByPk(Main main, ProductPackingUnit productPackingUnit) {
    ProductPackingUnitIs.deleteAble(main, productPackingUnit); //Validation
    AppService.delete(main, ProductPackingUnit.class, productPackingUnit.getId());
  }

  /**
   * Delete Array of ProductPackingUnit.
   *
   * @param main
   * @param productPackingUnit
   */
  public static final void deleteByPkArray(Main main, ProductPackingUnit[] productPackingUnit) {
    for (ProductPackingUnit e : productPackingUnit) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param productPackingDetailId
   * @return
   */
  public static List<ProductUnit> selectProductUnitByProductPacking(Main main, ProductPackingDetail productPackingDetailId) {
    if (productPackingDetailId != null) {
      return AppService.list(main, ProductUnit.class, "select * from scm_product_unit where id in(select product_unit_id from scm_product_packing_unit where product_packing_id = ?)", new Object[]{productPackingDetailId.getPackPrimary().getId()});
    }
    return null;
  }
}
