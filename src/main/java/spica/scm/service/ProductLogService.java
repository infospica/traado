/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Product;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductLog;

/**
 *
 * @author Anoop Jayachandran
 */
public abstract class ProductLogService {

  /**
   * ProductLog paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductLogSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_log", ProductLog.class, main);
    sql.main("select scm_product_log.id,scm_product_log.product_id,scm_product_log.product_category_id,scm_product_log.company_id,scm_product_log.product_name,"
            + "scm_product_log.product_type_id,scm_product_log.product_classification_id,scm_product_log.product_status_id,scm_product_log.created_at,"
            + "scm_product_log.created_by,scm_product_log.commodity_id,scm_product_log.product_name_chemical,scm_product_log.product_name_generic,"
            + "scm_product_log.pack_size,scm_product_log.product_unit_id from scm_product_log scm_product_log "); //Main query
    sql.count("select count(scm_product_log.id) as total from scm_product_log scm_product_log "); //Count query
    sql.join("left outer join scm_product scm_product on (scm_product.id = scm_product_log.product_id) "); //Join Query

    sql.string(new String[]{"scm_product_log.product_name", "scm_product.created_by", "scm_product_log.product_name_chemical",
      "scm_product_log.product_name_generic"}); //String search or sort fields
    sql.number(new String[]{"scm_product_log.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product.created_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductLog.
   *
   * @param main
   * @return List of ProductLog
   */
  public static final List<ProductLog> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductLogSqlPaged(main));
  }

//  /**
//   * Return list of ProductLog based on condition
//   * @param main
//   * @return List<ProductLog>
//   */
//  public static final List<ProductLog> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductLogSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductLog by key.
   *
   * @param main
   * @param productLog
   * @return ProductLog
   */
  public static final ProductLog selectByPk(Main main, ProductLog productLog) {
    return (ProductLog) AppService.find(main, ProductLog.class, productLog.getId());
  }

  /**
   * Insert ProductLog.
   *
   * @param main
   * @param productLog
   */
  public static final void insert(Main main, ProductLog productLog) {
    //ProductLogIs.insertAble(main, productLog);  //Validating
    AppService.insert(main, productLog);

  }

  /**
   * Update ProductLog by key.
   *
   * @param main
   * @param productLog
   * @return ProductLog
   */
  public static final ProductLog updateByPk(Main main, ProductLog productLog) {
    //ProductLogIs.updateAble(main, product); //Validating
    return (ProductLog) AppService.update(main, productLog);
  }

  /**
   * Insert or update ProductLog
   *
   * @param main
   * @param productLog
   */
  public static void insertOrUpdate(Main main, ProductLog productLog) {
    if (productLog.getId() == null) {
      insert(main, productLog);
    } else {
      updateByPk(main, productLog);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param product
   */
  public static void clone(Main main, ProductLog productLog) {
    productLog.setId(null); //Set to null for insert
    insert(main, productLog);
  }

  /**
   * Delete ProductLog.
   *
   * @param main
   * @param product
   */
  public static final void deleteByPk(Main main, ProductLog productLog) {
    //ProductLogIs.deleteAble(main, productLog); //Validation
    AppService.delete(main, ProductLog.class, productLog.getId());
  }

  /**
   * Delete Array of ProductLog.
   *
   * @param main
   * @param product
   */
  public static final void deleteByPkArray(Main main, ProductLog[] productLog) {
    for (ProductLog e : productLog) {
      deleteByPk(main, e);
    }
  }

  public static final void insertProductLog(Main main, Product product) {
    insert(main, new ProductLog(product));
  }
}
