/*
 * @(#)ProductFreeQtySchemeService.java	1.0 Fri Jan 13 15:50:59 IST 2017 
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
import spica.scm.domain.ProductFreeQtyScheme;
import spica.scm.validate.ProductFreeQtySchemeIs;
import wawo.app.faces.MainView;
import wawo.entity.core.UserMessageException;

/**
 * ProductFreeQtySchemeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jan 13 15:50:59 IST 2017
 */
public abstract class ProductFreeQtySchemeService {

  /**
   * ProductFreeQtyScheme paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductFreeQtySchemeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_free_qty_scheme", ProductFreeQtyScheme.class, main);
    sql.main("select scm_product_free_qty_scheme.id,scm_product_free_qty_scheme.scheme_name,scm_product_free_qty_scheme.is_free_qty_to_customer,scm_product_free_qty_scheme.replacement,scm_product_free_qty_scheme.free_quantity_scheme_unit_id,scm_product_free_qty_scheme.free_qty_scheme_unit_qty_for,scm_product_free_qty_scheme.free_qty_scheme_unit_qty_free,scm_product_free_qty_scheme.free_quantity_scheme_by_range,scm_product_free_qty_scheme.product_detail_id,scm_product_free_qty_scheme.created_by,scm_product_free_qty_scheme.modified_by,scm_product_free_qty_scheme.created_at,scm_product_free_qty_scheme.modified_at from scm_product_free_qty_scheme scm_product_free_qty_scheme"); //Main query
    sql.count("select count(scm_product_free_qty_scheme.id) as total from scm_product_free_qty_scheme scm_product_free_qty_scheme"); //Count query
    sql.join("left outer join scm_product_detail scm_product_free_qty_schemeproduct_detail_id on (scm_product_free_qty_schemeproduct_detail_id.id = scm_product_free_qty_scheme.product_detail_id) "
            + "left outer join scm_product_packing scm_product_free_qty_schemeproduct_packing_id on (scm_product_free_qty_schemeproduct_packing_id.id = scm_product_free_qty_scheme.free_quantity_scheme_unit_id)"); //Join Query

    sql.string(new String[]{"scm_product_free_qty_scheme.scheme_name,scm_product_free_qty_scheme.created_by", "scm_product_free_qty_scheme.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_free_qty_scheme.id", "scm_product_free_qty_scheme.free_quantity_scheme_unit_id", "scm_product_free_qty_scheme.free_qty_scheme_unit_qty_for", "scm_product_free_qty_scheme.free_qty_scheme_unit_qty_free", "scm_product_free_qty_scheme.free_quantity_scheme_by_range", "scm_product_free_qty_scheme.product_detail_id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_free_qty_scheme.created_at", "scm_product_free_qty_scheme.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductFreeQtyScheme.
   *
   * @param main
   * @return List of ProductFreeQtyScheme
   */
  public static final List<ProductFreeQtyScheme> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductFreeQtySchemeSqlPaged(main));
  }

//  /**
//   * Return list of ProductFreeQtyScheme based on condition
//   * @param main
//   * @return List<ProductFreeQtyScheme>
//   */
//  public static final List<ProductFreeQtyScheme> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductFreeQtySchemeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductFreeQtyScheme by key.
   *
   * @param main
   * @param productFreeQtyScheme
   * @return ProductFreeQtyScheme
   */
  public static final ProductFreeQtyScheme selectByPk(Main main, ProductFreeQtyScheme productFreeQtyScheme) {
    return (ProductFreeQtyScheme) AppService.find(main, ProductFreeQtyScheme.class, productFreeQtyScheme.getId());
  }

  /**
   * Insert ProductFreeQtyScheme.
   *
   * @param main
   * @param productFreeQtyScheme
   */
  public static final void insert(Main main, ProductFreeQtyScheme productFreeQtyScheme) {
    ProductFreeQtySchemeIs.insertAble(main, productFreeQtyScheme);  //Validating
    AppService.insert(main, productFreeQtyScheme);

  }

  /**
   * Update ProductFreeQtyScheme by key.
   *
   * @param main
   * @param productFreeQtyScheme
   * @return ProductFreeQtyScheme
   */
  public static final ProductFreeQtyScheme updateByPk(Main main, ProductFreeQtyScheme productFreeQtyScheme) {
    ProductFreeQtySchemeIs.updateAble(main, productFreeQtyScheme); //Validating
    return (ProductFreeQtyScheme) AppService.update(main, productFreeQtyScheme);
  }

  /**
   * Insert or update ProductFreeQtyScheme
   *
   * @param main
   * @param productFreeQtyScheme
   */
  public static void insertOrUpdate(Main main, ProductFreeQtyScheme productFreeQtyScheme) {
    ProductFreeQtyScheme productFreeQtySchemeObj;
    productFreeQtySchemeObj = (ProductFreeQtyScheme) AppService.single(main, ProductFreeQtyScheme.class, "select * from scm_product_free_qty_scheme where scheme_code=? and product_detail_id=?", new Object[]{productFreeQtyScheme.getSchemeCode(), productFreeQtyScheme.getProductDetailId().getId()});
    if (productFreeQtySchemeObj != null) {
      productFreeQtyScheme = productFreeQtySchemeObj;
      throw new UserMessageException("scheme exist");
    } else {
      productFreeQtyScheme.setId(null);
      insert(main, productFreeQtyScheme);
    }

  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productFreeQtyScheme
   */
  public static void clone(Main main, ProductFreeQtyScheme productFreeQtyScheme) {
    productFreeQtyScheme.setId(null); //Set to null for insert
    insert(main, productFreeQtyScheme);
  }

  /**
   * Delete ProductFreeQtyScheme.
   *
   * @param main
   * @param productFreeQtyScheme
   */
  public static final void deleteByPk(Main main, ProductFreeQtyScheme productFreeQtyScheme) {
    ProductFreeQtySchemeIs.deleteAble(main, productFreeQtyScheme); //Validation
    AppService.delete(main, ProductFreeQtyScheme.class, productFreeQtyScheme.getId());
  }

  /**
   * Delete Array of ProductFreeQtyScheme.
   *
   * @param main
   * @param productFreeQtyScheme
   */
  public static final void deleteByPkArray(Main main, ProductFreeQtyScheme[] productFreeQtyScheme) {
    for (ProductFreeQtyScheme e : productFreeQtyScheme) {
      deleteByPk(main, e);
    }
  }

  public static final ProductFreeQtyScheme selectProductFreeQtySchemeById(MainView main, int productFreeQtyScheme) {
    return (ProductFreeQtyScheme) AppService.single(main, ProductFreeQtyScheme.class, "select * from scm_product_free_qty_scheme scm_product_free_qty_scheme where scm_product_free_qty_scheme.id = ?", new Object[]{productFreeQtyScheme});
  }

  public static ProductFreeQtyScheme checkSchemeExist(Main main, int id, int freeQtySchemeUnitQtyFor, int freeQtySchemeUnitQtyFree) {
    return (ProductFreeQtyScheme) AppService.single(main, ProductFreeQtyScheme.class, "select id from scm_product_free_qty_scheme scm_product_free_qty_scheme where scm_product_free_qty_scheme.product_detail_id = ? and scm_product_free_qty_scheme.free_qty_scheme_unit_qty_for = ? and scm_product_free_qty_scheme.free_qty_scheme_unit_qty_free =?", new Object[]{id, freeQtySchemeUnitQtyFor, freeQtySchemeUnitQtyFree});

  }

  public static List<ProductFreeQtyScheme> listPagedScheme(Main main, int pdId) {
    SqlPage sql = getProductFreeQtySchemeSqlPaged(main);
    sql.cond("where scm_product_free_qty_scheme.product_detail_id=?");
    sql.param(pdId);
    return AppService.listPagedJpa(main, sql);
  }

  public static void deleteFreeQtyScheme(MainView main, ProductFreeQtyScheme productFreeQtyScheme) {
    AppService.deleteSql(main, ProductFreeQtyScheme.class, "delete from scm_product_free_qty_scheme where id = ? and id not in(select product_free_qty_scheme_id from scm_product_entry_detail)", new Object[]{productFreeQtyScheme.getId()});
  }
}
