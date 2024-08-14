/*
 * @(#)ProdFreeQtySchemeRangeService.java	1.0 Fri Jan 13 15:50:59 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProdFreeQtySchemeRange;
import spica.scm.domain.ProductFreeQtyScheme;
import spica.scm.validate.ProdFreeQtySchemeRangeIs;
import wawo.app.faces.MainView;

/**
 * ProdFreeQtySchemeRangeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jan 13 15:50:59 IST 2017
 */
public abstract class ProdFreeQtySchemeRangeService {

  /**
   * ProdFreeQtySchemeRange paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProdFreeQtySchemeRangeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_prod_free_qty_scheme_range", ProdFreeQtySchemeRange.class, main);
    sql.main("select scm_prod_free_qty_scheme_range.id,scm_prod_free_qty_scheme_range.product_free_qty_scheme_id,scm_prod_free_qty_scheme_range.range_from,scm_prod_free_qty_scheme_range.range_to,scm_prod_free_qty_scheme_range.free_qty from scm_prod_free_qty_scheme_range scm_prod_free_qty_scheme_range "); //Main query
    sql.count("select count(scm_prod_free_qty_scheme_range.id) as total from scm_prod_free_qty_scheme_range scm_prod_free_qty_scheme_range "); //Count query
    sql.join("left outer join scm_product_free_qty_scheme scm_prod_free_qty_scheme_rangeproduct_free_qty_scheme_id on (scm_prod_free_qty_scheme_rangeproduct_free_qty_scheme_id.id = scm_prod_free_qty_scheme_range.product_free_qty_scheme_id)"); //Join Query

    sql.string(null); //String search or sort fields
    sql.number(new String[]{"scm_prod_free_qty_scheme_range.id", "scm_prod_free_qty_scheme_rangeproduct_free_qty_scheme_id.product_free_qty", "scm_prod_free_qty_scheme_range.range_from", "scm_prod_free_qty_scheme_range.range_to", "scm_prod_free_qty_scheme_range.free_qty"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProdFreeQtySchemeRange.
   *
   * @param main
   * @return List of ProdFreeQtySchemeRange
   */
  public static final List<ProdFreeQtySchemeRange> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProdFreeQtySchemeRangeSqlPaged(main));
  }

//  /**
//   * Return list of ProdFreeQtySchemeRange based on condition
//   * @param main
//   * @return List<ProdFreeQtySchemeRange>
//   */
//  public static final List<ProdFreeQtySchemeRange> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProdFreeQtySchemeRangeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProdFreeQtySchemeRange by key.
   *
   * @param main
   * @param prodFreeQtySchemeRange
   * @return ProdFreeQtySchemeRange
   */
  public static final ProdFreeQtySchemeRange selectByPk(Main main, ProdFreeQtySchemeRange prodFreeQtySchemeRange) {
    return (ProdFreeQtySchemeRange) AppService.find(main, ProdFreeQtySchemeRange.class, prodFreeQtySchemeRange.getId());
  }

  /**
   * Insert ProdFreeQtySchemeRange.
   *
   * @param main
   * @param prodFreeQtySchemeRange
   */
  public static final void insert(Main main, ProdFreeQtySchemeRange prodFreeQtySchemeRange) {
    ProdFreeQtySchemeRangeIs.insertAble(main, prodFreeQtySchemeRange);  //Validating
    AppService.insert(main, prodFreeQtySchemeRange);

  }

  /**
   * Update ProdFreeQtySchemeRange by key.
   *
   * @param main
   * @param prodFreeQtySchemeRange
   * @return ProdFreeQtySchemeRange
   */
  public static final ProdFreeQtySchemeRange updateByPk(Main main, ProdFreeQtySchemeRange prodFreeQtySchemeRange) {
    ProdFreeQtySchemeRangeIs.updateAble(main, prodFreeQtySchemeRange); //Validating
    return (ProdFreeQtySchemeRange) AppService.update(main, prodFreeQtySchemeRange);
  }

  /**
   * Insert or update ProdFreeQtySchemeRange
   *
   * @param main
   * @param prodFreeQtySchemeRange
   */
  public static void insertOrUpdate(Main main, ProdFreeQtySchemeRange prodFreeQtySchemeRange) {
    if (prodFreeQtySchemeRange.getId() == null) {
      insert(main, prodFreeQtySchemeRange);
    } else {
      updateByPk(main, prodFreeQtySchemeRange);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param prodFreeQtySchemeRange
   */
  public static void clone(Main main, ProdFreeQtySchemeRange prodFreeQtySchemeRange) {
    prodFreeQtySchemeRange.setId(null); //Set to null for insert
    insert(main, prodFreeQtySchemeRange);
  }

  /**
   * Delete ProdFreeQtySchemeRange.
   *
   * @param main
   * @param prodFreeQtySchemeRange
   */
  public static final void deleteByPk(Main main, ProdFreeQtySchemeRange prodFreeQtySchemeRange) {
    ProdFreeQtySchemeRangeIs.deleteAble(main, prodFreeQtySchemeRange); //Validation
    AppService.delete(main, ProdFreeQtySchemeRange.class, prodFreeQtySchemeRange.getId());
  }

  /**
   * Delete Array of ProdFreeQtySchemeRange.
   *
   * @param main
   * @param prodFreeQtySchemeRange
   */
  public static final void deleteByPkArray(Main main, ProdFreeQtySchemeRange[] prodFreeQtySchemeRange) {
    for (ProdFreeQtySchemeRange e : prodFreeQtySchemeRange) {
      deleteByPk(main, e);
    }
  }

  public static final List<ProdFreeQtySchemeRange> selectProdFreeQtySchemeRange(MainView main, int productFreeQtyScheme) {
    List<ProdFreeQtySchemeRange> prodFreeQtySchemeRange = AppService.list(main, ProdFreeQtySchemeRange.class, "select * from scm_prod_free_qty_scheme_range scm_prod_free_qty_scheme_range where scm_prod_free_qty_scheme_range.product_free_qty_scheme_id = ?", new Object[]{productFreeQtyScheme});
    return prodFreeQtySchemeRange;
  }

  public static final void deleteProdFreeQtySchemeRange(Main main, int prodFreeQtySchemeId) {
    AppService.deleteSql(main, ProdFreeQtySchemeRange.class, "delete from scm_prod_free_qty_scheme_range scm_prod_free_qty_scheme_range where scm_prod_free_qty_scheme_range.product_free_qty_scheme_id=?", new Object[]{prodFreeQtySchemeId});

  }

  public static final List<ProdFreeQtySchemeRange> prodFreeQtySchemeRangeList(Main main, int id) {
    SqlPage sql = getProdFreeQtySchemeRangeSqlPaged(main);
    sql.cond("where scm_prod_free_qty_scheme_range.product_free_qty_scheme_id=?");
    sql.param(id);
    return AppService.listAllJpa(main, sql);
  }

  public static void insertArray(Main main, List<ProdFreeQtySchemeRange> prodFreeQtySchemeRanges, ProductFreeQtyScheme productFreeQtySchemeId) {
    if (prodFreeQtySchemeRanges != null) {
//      if (prodFreeQtySchemeRanges == null || prodFreeQtySchemeRanges.isEmpty()) {
      deleteRangeBySchemeId(main, productFreeQtySchemeId.getId());
//    }else{
      ProdFreeQtySchemeRange prodFreeQtySchemeRange;
      for (ProdFreeQtySchemeRange prodFreeQtySchemeRangess : prodFreeQtySchemeRanges) {  //Reinserting
        prodFreeQtySchemeRange = new ProdFreeQtySchemeRange();
        prodFreeQtySchemeRange.setProductFreeQtySchemeId(productFreeQtySchemeId);
        prodFreeQtySchemeRange.setRangeFrom(prodFreeQtySchemeRangess.getRangeFrom());
        prodFreeQtySchemeRange.setRangeTo(prodFreeQtySchemeRangess.getRangeTo());
        prodFreeQtySchemeRange.setFreeQty(prodFreeQtySchemeRangess.getFreeQty());
        insert(main, prodFreeQtySchemeRange);
      }
//      }
    }
  }
  public static final void deleteRangeBySchemeId(Main main, Integer schemeId) {
    AppService.deleteSql(main, ProdFreeQtySchemeRange.class, "delete from scm_prod_free_qty_scheme_range where product_free_qty_scheme_id = ?", new Object[]{schemeId});
  }

}
