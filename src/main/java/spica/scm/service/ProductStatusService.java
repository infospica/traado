/*
 * @(#)ProductStatusService.java	1.0 Tue May 10 17:16:26 IST 2016 
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
import spica.scm.domain.ProductStatus;
import spica.scm.validate.ProductStatusIs;

/**
 * ProductStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 10 17:16:26 IST 2016
 */
public abstract class ProductStatusService {

  /**
   * ProductStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_status", ProductStatus.class, main);
    sql.main("select scm_product_status.id,scm_product_status.title,scm_product_status.sort_order,scm_product_status.created_by,scm_product_status.modified_by,scm_product_status.created_at,scm_product_status.modified_at,scm_product_status.display_color from scm_product_status scm_product_status"); //Main query
    sql.count("select count(scm_product_status.id) from scm_product_status scm_product_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_product_status.title", "scm_product_status.created_by", "scm_product_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_status.id", "scm_product_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_status.created_at", "scm_product_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductStatus.
   *
   * @param main
   * @return List of ProductStatus
   */
  public static final List<ProductStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductStatusSqlPaged(main));
  }

//  /**
//   * Return list of ProductStatus based on condition
//   * @param main
//   * @return List<ProductStatus>
//   */
//  public static final List<ProductStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductStatus by key.
   *
   * @param main
   * @param productStatus
   * @return ProductStatus
   */
  public static final ProductStatus selectByPk(Main main, ProductStatus productStatus) {
    return (ProductStatus) AppService.find(main, ProductStatus.class, productStatus.getId());
  }

  /**
   * Insert ProductStatus.
   *
   * @param main
   * @param productStatus
   */
  public static final void insert(Main main, ProductStatus productStatus) {
    ProductStatusIs.insertAble(main, productStatus);  //Validating
    AppService.insert(main, productStatus);

  }

  /**
   * Update ProductStatus by key.
   *
   * @param main
   * @param productStatus
   * @return ProductStatus
   */
  public static final ProductStatus updateByPk(Main main, ProductStatus productStatus) {
    ProductStatusIs.updateAble(main, productStatus); //Validating
    return (ProductStatus) AppService.update(main, productStatus);
  }

  /**
   * Insert or update ProductStatus
   *
   * @param main
   * @param ProductStatus
   */
  public static void insertOrUpdate(Main main, ProductStatus productStatus) {
    if (productStatus.getId() == null) {
      insert(main, productStatus);
    } else {
      updateByPk(main, productStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productStatus
   */
  public static void clone(Main main, ProductStatus productStatus) {
    productStatus.setId(null); //Set to null for insert
    insert(main, productStatus);
  }

  /**
   * Delete ProductStatus.
   *
   * @param main
   * @param productStatus
   */
  public static final void deleteByPk(Main main, ProductStatus productStatus) {
    ProductStatusIs.deleteAble(main, productStatus); //Validation
    AppService.delete(main, ProductStatus.class, productStatus.getId());
  }

  /**
   * Delete Array of ProductStatus.
   *
   * @param main
   * @param productStatus
   */
  public static final void deleteByPkArray(Main main, ProductStatus[] productStatus) {
    for (ProductStatus e : productStatus) {
      deleteByPk(main, e);
    }
  }
}
