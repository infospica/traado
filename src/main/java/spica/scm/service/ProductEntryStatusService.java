/*
 * @(#)ProductEntryStatusService.java	1.0 Thu Sep 08 18:33:22 IST 2016 
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
import spica.scm.domain.ProductEntryStatus;
import spica.scm.validate.ProductEntryStatusIs;

/**
 * ProductEntryStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Sep 08 18:33:22 IST 2016
 */
public abstract class ProductEntryStatusService {

  /**
   * ProductEntryStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductEntryStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_entry_status", ProductEntryStatus.class, main);
    sql.main("select scm_product_entry_status.id,scm_product_entry_status.title,scm_product_entry_status.sort_order,scm_product_entry_status.created_by,scm_product_entry_status.modified_by,scm_product_entry_status.created_at,scm_product_entry_status.modified_at from scm_product_entry_status scm_product_entry_status"); //Main query
    sql.count("select count(scm_product_entry_status.id) as total from scm_product_entry_status scm_product_entry_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_product_entry_status.title", "scm_product_entry_status.created_by", "scm_product_entry_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_entry_status.id", "scm_product_entry_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_entry_status.created_at", "scm_product_entry_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductEntryStatus.
   *
   * @param main
   * @return List of ProductEntryStatus
   */
  public static final List<ProductEntryStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductEntryStatusSqlPaged(main));
  }

//  /**
//   * Return list of ProductEntryStatus based on condition
//   * @param main
//   * @return List<ProductEntryStatus>
//   */
//  public static final List<ProductEntryStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductEntryStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductEntryStatus by key.
   *
   * @param main
   * @param productEntryStatus
   * @return ProductEntryStatus
   */
  public static final ProductEntryStatus selectByPk(Main main, ProductEntryStatus productEntryStatus) {
    return (ProductEntryStatus) AppService.find(main, ProductEntryStatus.class, productEntryStatus.getId());
  }

  /**
   * Insert ProductEntryStatus.
   *
   * @param main
   * @param productEntryStatus
   */
  public static final void insert(Main main, ProductEntryStatus productEntryStatus) {
    ProductEntryStatusIs.insertAble(main, productEntryStatus);  //Validating
    AppService.insert(main, productEntryStatus);

  }

  /**
   * Update ProductEntryStatus by key.
   *
   * @param main
   * @param productEntryStatus
   * @return ProductEntryStatus
   */
  public static final ProductEntryStatus updateByPk(Main main, ProductEntryStatus productEntryStatus) {
    ProductEntryStatusIs.updateAble(main, productEntryStatus); //Validating
    return (ProductEntryStatus) AppService.update(main, productEntryStatus);
  }

  /**
   * Insert or update ProductEntryStatus
   *
   * @param main
   * @param productEntryStatus
   */
  public static void insertOrUpdate(Main main, ProductEntryStatus productEntryStatus) {
    if (productEntryStatus.getId() == null) {
      insert(main, productEntryStatus);
    } else {
      updateByPk(main, productEntryStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productEntryStatus
   */
  public static void clone(Main main, ProductEntryStatus productEntryStatus) {
    productEntryStatus.setId(null); //Set to null for insert
    insert(main, productEntryStatus);
  }

  /**
   * Delete ProductEntryStatus.
   *
   * @param main
   * @param productEntryStatus
   */
  public static final void deleteByPk(Main main, ProductEntryStatus productEntryStatus) {
    ProductEntryStatusIs.deleteAble(main, productEntryStatus); //Validation
    AppService.delete(main, ProductEntryStatus.class, productEntryStatus.getId());
  }

  /**
   * Delete Array of ProductEntryStatus.
   *
   * @param main
   * @param productEntryStatus
   */
  public static final void deleteByPkArray(Main main, ProductEntryStatus[] productEntryStatus) {
    for (ProductEntryStatus e : productEntryStatus) {
      deleteByPk(main, e);
    }
  }
}
