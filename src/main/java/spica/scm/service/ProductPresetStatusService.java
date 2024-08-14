/*
 * @(#)ProductPresetStatusService.java	1.0 Tue Sep 20 15:27:15 IST 2016 
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
import spica.scm.domain.ProductPresetStatus;
import spica.scm.validate.ProductPresetStatusIs;

/**
 * ProductPresetStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Sep 20 15:27:15 IST 2016
 */
public abstract class ProductPresetStatusService {

  /**
   * ProductPresetStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductPresetStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_preset_status", ProductPresetStatus.class, main);
    sql.main("select scm_product_preset_status.id,scm_product_preset_status.title,scm_product_preset_status.sort_order,scm_product_preset_status.display_color,scm_product_preset_status.created_by,scm_product_preset_status.modified_by,scm_product_preset_status.created_at,scm_product_preset_status.modified_at from scm_product_preset_status scm_product_preset_status"); //Main query
    sql.count("select count(scm_product_preset_status.id) as total from scm_product_preset_status scm_product_preset_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_product_preset_status.title", "scm_product_preset_status.display_color", "scm_product_preset_status.created_by", "scm_product_preset_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_preset_status.id", "scm_product_preset_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_preset_status.created_at", "scm_product_preset_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductPresetStatus.
   *
   * @param main
   * @return List of ProductPresetStatus
   */
  public static final List<ProductPresetStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductPresetStatusSqlPaged(main));
  }

//  /**
//   * Return list of ProductPresetStatus based on condition
//   * @param main
//   * @return List<ProductPresetStatus>
//   */
//  public static final List<ProductPresetStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductPresetStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductPresetStatus by key.
   *
   * @param main
   * @param productPresetStatus
   * @return ProductPresetStatus
   */
  public static final ProductPresetStatus selectByPk(Main main, ProductPresetStatus productPresetStatus) {
    return (ProductPresetStatus) AppService.find(main, ProductPresetStatus.class, productPresetStatus.getId());
  }

  /**
   * Insert ProductPresetStatus.
   *
   * @param main
   * @param productPresetStatus
   */
  public static final void insert(Main main, ProductPresetStatus productPresetStatus) {
    ProductPresetStatusIs.insertAble(main, productPresetStatus);  //Validating
    AppService.insert(main, productPresetStatus);

  }

  /**
   * Update ProductPresetStatus by key.
   *
   * @param main
   * @param productPresetStatus
   * @return ProductPresetStatus
   */
  public static final ProductPresetStatus updateByPk(Main main, ProductPresetStatus productPresetStatus) {
    ProductPresetStatusIs.updateAble(main, productPresetStatus); //Validating
    return (ProductPresetStatus) AppService.update(main, productPresetStatus);
  }

  /**
   * Insert or update ProductPresetStatus
   *
   * @param main
   * @param productPresetStatus
   */
  public static void insertOrUpdate(Main main, ProductPresetStatus productPresetStatus) {
    if (productPresetStatus.getId() == null) {
      insert(main, productPresetStatus);
    } else {
      updateByPk(main, productPresetStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productPresetStatus
   */
  public static void clone(Main main, ProductPresetStatus productPresetStatus) {
    productPresetStatus.setId(null); //Set to null for insert
    insert(main, productPresetStatus);
  }

  /**
   * Delete ProductPresetStatus.
   *
   * @param main
   * @param productPresetStatus
   */
  public static final void deleteByPk(Main main, ProductPresetStatus productPresetStatus) {
    ProductPresetStatusIs.deleteAble(main, productPresetStatus); //Validation
    AppService.delete(main, ProductPresetStatus.class, productPresetStatus.getId());
  }

  /**
   * Delete Array of ProductPresetStatus.
   *
   * @param main
   * @param productPresetStatus
   */
  public static final void deleteByPkArray(Main main, ProductPresetStatus[] productPresetStatus) {
    for (ProductPresetStatus e : productPresetStatus) {
      deleteByPk(main, e);
    }
  }
}
