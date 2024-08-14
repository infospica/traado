/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductCategoryLog;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;

/**
 *
 * @author Anoop Jayachandran
 */
public abstract class ProductCategoryLogService {

  /**
   * ProductCategoryLog paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductCategoryLogSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_category_log", ProductCategoryLog.class, main);
    sql.main("select scm_product_category_log.id,scm_product_category_log.product_category_id,scm_product_category_log.commodity_id,scm_product_category_log.title,"
            + "scm_product_category_log.sort_order,scm_product_category_log.status_id,scm_product_category_log.hsn_code,scm_product_category_log.created_at,"
            + "scm_product_category_log.created_by from scm_product_category_log scm_product_category_log "); //Main query
    sql.count("select count(scm_product_category_log.id) as total from scm_product_category_log scm_product_category_log "); //Count query
    sql.join("left outer join scm_product_category scm_product_category on (scm_product_category.id = scm_product_category_log.product_category_id) "); //Join Query

    sql.string(new String[]{"scm_product_category_log.title", "scm_product_category_log.hsn_code", "scm_product_category_log.created_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_category_log.id", "scm_product_category_log.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_category_log.created_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductCategoryLog.
   *
   * @param main
   * @return List of ProductCategoryLog
   */
  public static final List<ProductCategoryLog> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductCategoryLogSqlPaged(main));
  }

//  /**
//   * Return list of ProductCategoryLog based on condition
//   * @param main
//   * @return List<ProductCategoryLog>
//   */
//  public static final List<ProductCategoryLog> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductCategoryLogSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductCategoryLog by key.
   *
   * @param main
   * @param productCategoryLog
   * @return ProductCategoryLog
   */
  public static final ProductCategoryLog selectByPk(Main main, ProductCategoryLog productCategoryLog) {
    return (ProductCategoryLog) AppService.find(main, ProductCategoryLog.class, productCategoryLog.getId());
  }

  /**
   * Insert ProductCategoryLog.
   *
   * @param main
   * @param productCategoryLog
   */
  public static final void insert(Main main, ProductCategoryLog productCategoryLog) {
    //ProductCategoryLogIs.insertAble(main, productCategoryLog);  //Validating
    AppService.insert(main, productCategoryLog);

  }

  /**
   * Update ProductCategoryLog by key.
   *
   * @param main
   * @param productCategoryLog
   * @return ProductCategoryLog
   */
  public static final ProductCategoryLog updateByPk(Main main, ProductCategoryLog productCategoryLog) {
    //ProductCategoryLogIs.updateAble(main, productCategoryLog); //Validating
    return (ProductCategoryLog) AppService.update(main, productCategoryLog);
  }

  /**
   * Insert or update ProductCategoryLog
   *
   * @param main
   * @param productCategoryLog
   */
  public static void insertOrUpdate(Main main, ProductCategoryLog productCategoryLog) {
    if (productCategoryLog.getId() == null) {
      insert(main, productCategoryLog);
    } else {
      updateByPk(main, productCategoryLog);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productCategoryLog
   */
  public static void clone(Main main, ProductCategoryLog productCategoryLog) {
    productCategoryLog.setId(null); //Set to null for insert
    insert(main, productCategoryLog);
  }

  /**
   * Delete ProductCategoryLog.
   *
   * @param main
   * @param productCategoryLog
   */
  public static final void deleteByPk(Main main, ProductCategoryLog productCategoryLog) {
    //ProductCategoryLogIs.deleteAble(main, productCategoryLog); //Validation
    AppService.delete(main, ProductCategoryLog.class, productCategoryLog.getId());
  }

  /**
   * Delete Array of ProductCategoryLog.
   *
   * @param main
   * @param productCategoryLog
   */
  public static final void deleteByPkArray(Main main, ProductCategoryLog[] productCategoryLog) {
    for (ProductCategoryLog e : productCategoryLog) {
      deleteByPk(main, e);
    }
  }
  
  public static final void insetProductCategoryLog(Main main, ProductCategory productCategory){
    insert(main, new ProductCategoryLog(productCategory));
  }
}
