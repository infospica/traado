/*
 * @(#)ProductClassificationService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductClassification;
import spica.scm.validate.ProductClassificationIs;

/**
 * ProductClassificationService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class ProductClassificationService {

  /**
   * ProductClassification paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductClassificationSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_classification", ProductClassification.class, main);
    sql.main("select scm_product_classification.id,scm_product_classification.title,scm_product_classification.sort_order,scm_product_classification.created_by,scm_product_classification.modified_by,scm_product_classification.created_at,scm_product_classification.modified_at,scm_product_classification.status_id from scm_product_classification scm_product_classification "); //Main query
    sql.count("select count(scm_product_classification.id) as total from scm_product_classification scm_product_classification "); //Count query

    sql.string(new String[]{"scm_product_classification.title", "scm_product_classification.created_by", "scm_product_classification.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_classification.id", "scm_product_classification.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_classification.created_at", "scm_product_classification.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductClassification.
   *
   * @param main
   * @return List of ProductClassification
   */
  public static final List<ProductClassification> listPaged(Main main, Company company) {
    SqlPage sql = getProductClassificationSqlPaged(main);
    sql.cond(" where company_id=? ");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of ProductClassification based on condition
//   * @param main
//   * @return List<ProductClassification>
//   */
//  public static final List<ProductClassification> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductClassificationSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductClassification by key.
   *
   * @param main
   * @param productClassification
   * @return ProductClassification
   */
  public static final ProductClassification selectByPk(Main main, ProductClassification productClassification) {
    return (ProductClassification) AppService.find(main, ProductClassification.class, productClassification.getId());
  }

  /**
   * Insert ProductClassification.
   *
   * @param main
   * @param productClassification
   */
  public static final void insert(Main main, ProductClassification productClassification) {
    ProductClassificationIs.insertAble(main, productClassification);  //Validating
    AppService.insert(main, productClassification);

  }

  /**
   * Update ProductClassification by key.
   *
   * @param main
   * @param productClassification
   * @return ProductClassification
   */
  public static final ProductClassification updateByPk(Main main, ProductClassification productClassification) {
    ProductClassificationIs.updateAble(main, productClassification); //Validating
    return (ProductClassification) AppService.update(main, productClassification);
  }

  /**
   * Insert or update ProductClassification
   *
   * @param main
   * @param productClassification
   */
  public static void insertOrUpdate(Main main, ProductClassification productClassification) {
    if (productClassification.getId() == null) {
      insert(main, productClassification);
    } else {
      updateByPk(main, productClassification);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productClassification
   */
  public static void clone(Main main, ProductClassification productClassification) {
    productClassification.setId(null); //Set to null for insert
    insert(main, productClassification);
  }

  /**
   * Delete ProductClassification.
   *
   * @param main
   * @param productClassification
   */
  public static final void deleteByPk(Main main, ProductClassification productClassification) {
    ProductClassificationIs.deleteAble(main, productClassification); //Validation
    AppService.delete(main, ProductClassification.class, productClassification.getId());
  }

  /**
   * Delete Array of ProductClassification.
   *
   * @param main
   * @param productClassification
   */
  public static final void deleteByPkArray(Main main, ProductClassification[] productClassification) {
    for (ProductClassification e : productClassification) {
      deleteByPk(main, e);
    }
  }
}
