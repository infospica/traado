/*
 * @(#)ProdDetailStatusService.java	1.0 Tue May 10 17:16:26 IST 2016 
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
import spica.scm.domain.ProductDetailStatus;
import spica.scm.validate.ProdDetailStatusIs;

/**
 * ProdDetailStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 10 17:16:26 IST 2016
 */
public abstract class ProdDetailStatusService {

  /**
   * ProdDetailStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProdDetailStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_prod_detail_status", ProductDetailStatus.class, main);
    sql.main("select scm_prod_detail_status.id,scm_prod_detail_status.title,scm_prod_detail_status.sort_order,scm_prod_detail_status.created_by,scm_prod_detail_status.modified_by,scm_prod_detail_status.created_at,scm_prod_detail_status.modified_at,scm_prod_detail_status.display_color from scm_prod_detail_status scm_prod_detail_status"); //Main query
    sql.count("select count(scm_prod_detail_status.id) from scm_prod_detail_status scm_prod_detail_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_prod_detail_status.title", "scm_prod_detail_status.created_by", "scm_prod_detail_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_prod_detail_status.id", "scm_prod_detail_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_prod_detail_status.created_at", "scm_prod_detail_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProdDetailStatus.
   *
   * @param main
   * @return List of ProdDetailStatus
   */
  public static final List<ProductDetailStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProdDetailStatusSqlPaged(main));
  }

//  /**
//   * Return list of ProdDetailStatus based on condition
//   * @param main
//   * @return List<ProdDetailStatus>
//   */
//  public static final List<ProdDetailStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProdDetailStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProdDetailStatus by key.
   *
   * @param main
   * @param prodDetailStatus
   * @return ProdDetailStatus
   */
  public static final ProductDetailStatus selectByPk(Main main, ProductDetailStatus prodDetailStatus) {
    return (ProductDetailStatus) AppService.find(main, ProductDetailStatus.class, prodDetailStatus.getId());
  }

  /**
   * Insert ProdDetailStatus.
   *
   * @param main
   * @param prodDetailStatus
   */
  public static final void insert(Main main, ProductDetailStatus prodDetailStatus) {
    ProdDetailStatusIs.insertAble(main, prodDetailStatus);  //Validating
    AppService.insert(main, prodDetailStatus);

  }

  /**
   * Update ProdDetailStatus by key.
   *
   * @param main
   * @param prodDetailStatus
   * @return ProdDetailStatus
   */
  public static final ProductDetailStatus updateByPk(Main main, ProductDetailStatus prodDetailStatus) {
    ProdDetailStatusIs.updateAble(main, prodDetailStatus); //Validating
    return (ProductDetailStatus) AppService.update(main, prodDetailStatus);
  }

  /**
   * Insert or update ProdDetailStatus
   *
   * @param main
   * @param ProdDetailStatus
   */
  public static void insertOrUpdate(Main main, ProductDetailStatus prodDetailStatus) {
    if (prodDetailStatus.getId() == null) {
      insert(main, prodDetailStatus);
    } else {
      updateByPk(main, prodDetailStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param prodDetailStatus
   */
  public static void clone(Main main, ProductDetailStatus prodDetailStatus) {
    prodDetailStatus.setId(null); //Set to null for insert
    insert(main, prodDetailStatus);
  }

  /**
   * Delete ProdDetailStatus.
   *
   * @param main
   * @param prodDetailStatus
   */
  public static final void deleteByPk(Main main, ProductDetailStatus prodDetailStatus) {
    ProdDetailStatusIs.deleteAble(main, prodDetailStatus); //Validation
    AppService.delete(main, ProductDetailStatus.class, prodDetailStatus.getId());
  }

  /**
   * Delete Array of ProdDetailStatus.
   *
   * @param main
   * @param prodDetailStatus
   */
  public static final void deleteByPkArray(Main main, ProductDetailStatus[] prodDetailStatus) {
    for (ProductDetailStatus e : prodDetailStatus) {
      deleteByPk(main, e);
    }
  }
}
