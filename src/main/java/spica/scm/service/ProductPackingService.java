/*
 * @(#)ProductPackingService.java	1.0 Thu Aug 25 13:58:57 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductPacking;
import spica.scm.domain.ProductPackingUnit;
import spica.scm.domain.ProductUnit;
import spica.scm.validate.ProductPackingIs;
import wawo.app.faces.MainView;

/**
 * ProductPackingService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Aug 25 13:58:57 IST 2016
 */
public abstract class ProductPackingService {

  public static final int PACK_PRIMARY = 1;
  public static final int PACK_SECONDARY = 2;
  public static final int PACK_TERTIARY = 3;

  /**
   * ProductPacking paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductPackingSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_packing", ProductPacking.class, main);
    sql.main("select scm_product_packing.id,scm_product_packing.title,scm_product_packing.sort_order,scm_product_packing.pack_type,scm_product_packing.created_by,scm_product_packing.modified_by,scm_product_packing.created_at,scm_product_packing.modified_at from scm_product_packing scm_product_packing "); //Main query
    sql.count("select count(scm_product_packing.id) as total from scm_product_packing scm_product_packing "); //Count query
    sql.join("left outer join scm_pack_type scm_product_packingpack_type on (scm_product_packingpack_type.id = scm_product_packing.pack_type)"); //Join Query

    sql.string(new String[]{"scm_product_packing.title", "scm_product_packingpack_type.title", "scm_product_packing.created_by", "scm_product_packing.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_packing.id", "scm_product_packing.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_packing.created_at", "scm_product_packing.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductPacking.
   *
   * @param main
   * @return List of ProductPacking
   */
  public static final List<ProductPacking> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductPackingSqlPaged(main));
  }

//  /**
//   * Return list of ProductPacking based on condition
//   * @param main
//   * @return List<ProductPacking>
//   */
//  public static final List<ProductPacking> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductPackingSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductPacking by key.
   *
   * @param main
   * @param productPacking
   * @return ProductPacking
   */
  public static final ProductPacking selectByPk(Main main, ProductPacking productPacking) {
    return (ProductPacking) AppService.find(main, ProductPacking.class, productPacking.getId());
  }

  /**
   * Insert ProductPacking.
   *
   * @param main
   * @param productPacking
   */
  public static final void insert(Main main, ProductPacking productPacking) {
    ProductPackingIs.insertAble(main, productPacking);  //Validating
    AppService.insert(main, productPacking);

  }

  /**
   * Update ProductPacking by key.
   *
   * @param main
   * @param productPacking
   * @return ProductPacking
   */
  public static final ProductPacking updateByPk(Main main, ProductPacking productPacking) {
    ProductPackingIs.updateAble(main, productPacking); //Validating
    return (ProductPacking) AppService.update(main, productPacking);
  }

  /**
   * Insert or update ProductPacking
   *
   * @param main
   * @param productPacking
   */
  public static void insertOrUpdate(Main main, ProductPacking productPacking, List<ProductUnit> unitSelected) {
    if (productPacking.getId() != null) {
      AppService.deleteSql(main, ProductUnit.class, "delete from scm_product_packing_unit where product_packing_id=?", new Object[]{productPacking.getId()});
      updateByPk(main, productPacking);
    } else {
      insert(main, productPacking);
    }
    if (unitSelected != null) {
      insertArray(main, unitSelected.toArray(new ProductUnit[unitSelected.size()]), productPacking);   //Inserting all the relation records.
    }
  }

  public static void insertArray(Main main, ProductUnit[] unitSelected, ProductPacking productPacking) {
    if (unitSelected != null) {
      ProductPackingUnit bg;
      for (ProductUnit unit : unitSelected) {  //Reinserting
        bg = new ProductPackingUnit();
        bg.setProductPackingId(productPacking);
        bg.setProductUnitId(unit);
        ProductPackingUnitService.insert(main, bg);
      }
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productPacking
   */
  public static void clone(Main main, ProductPacking productPacking) {
    productPacking.setId(null); //Set to null for insert
    insert(main, productPacking);
  }

  /**
   * Delete ProductPacking.
   *
   * @param main
   * @param productPacking
   */
  public static final void deleteByPk(Main main, ProductPacking productPacking) {
    ProductPackingIs.deleteAble(main, productPacking); //Validation
    AppService.deleteSql(main, ProductPackingUnit.class, "delete from scm_product_packing_unit where scm_product_packing_unit.product_packing_id=?", new Object[]{productPacking.getId()});
    AppService.delete(main, ProductPacking.class, productPacking.getId());
  }

  /**
   * Delete Array of ProductPacking.
   *
   * @param main
   * @param productPacking
   */
  public static final void deleteByPkArray(Main main, ProductPacking[] productPacking) {
    for (ProductPacking e : productPacking) {
      deleteByPk(main, e);
    }
  }

  public static final List<ProductPacking> productPackingPrimary(MainView main, int id) {
    return AppService.list(main, ProductPacking.class, "select scm_product_packing.id,scm_product_packing.title from scm_product_packing scm_product_packing left outer join scm_product_packing_unit scm_product_packing_unit\n"
            + "on (scm_product_packing.id = scm_product_packing_unit.product_packing_id)\n"
            + "where scm_product_packing_unit.product_unit_id = ?", new Object[]{id});
  }

  public static final List<ProductPacking> productPackingPrimary(MainView main) {
    return AppService.list(main, ProductPacking.class, "select id,title from scm_product_packing where pack_type =?", new Object[]{PACK_PRIMARY});
  }

  public static final List<ProductPacking> productPackingSecondary(MainView main) {
    return AppService.list(main, ProductPacking.class, "select id,title from scm_product_packing where pack_type =?", new Object[]{PACK_SECONDARY});
  }

  public static final List<ProductPacking> productPackingTertiary(MainView main) {
    return AppService.list(main, ProductPacking.class, "select id,title from scm_product_packing where pack_type =?", new Object[]{PACK_TERTIARY});
  }

}
