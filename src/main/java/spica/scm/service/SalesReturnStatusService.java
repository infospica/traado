/*
 * @(#)SalesReturnStatService.java	1.0 Mon May 09 18:27:36 IST 2016 
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
import spica.scm.domain.SalesReturnStatus;
import spica.scm.validate.SalesReturnStatusIs;

/**
 * SalesReturnStatService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon May 09 18:27:36 IST 2016
 */
public abstract class SalesReturnStatusService {

  /**
   * SalesReturnStat paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesReturnStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_return_status", SalesReturnStatus.class, main);
    sql.main("select scm_sales_return_status.id,scm_sales_return_status.title,scm_sales_return_status.sort_order,scm_sales_return_status.created_by,scm_sales_return_status.modified_by,scm_sales_return_status.created_at,scm_sales_return_status.modified_at from scm_sales_return_status scm_sales_return_status"); //Main query
    sql.count("select count(scm_sales_return_status.id) from scm_sales_return_status scm_sales_return_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_sales_return_status.title", "scm_sales_return_status.created_by", "scm_sales_return_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_return_status.id", "scm_sales_return_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_return_status.created_at", "scm_sales_return_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesReturnStatus.
   *
   * @param main
   * @return List of SalesReturnStatus
   */
  public static final List<SalesReturnStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesReturnStatusSqlPaged(main));
  }

//  /**
//   * Return list of SalesReturnStat based on condition
//   * @param main
//   * @return List<SalesReturnStat>
//   */
//  public static final List<SalesReturnStat> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesReturnStatSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesReturnStat by key.
   *
   * @param main
   * @param salesReturnStatus
   * @return SalesReturnStatus
   */
  public static final SalesReturnStatus selectByPk(Main main, SalesReturnStatus salesReturnStatus) {
    return (SalesReturnStatus) AppService.find(main, SalesReturnStatus.class, salesReturnStatus.getId());
  }

  /**
   * Insert SalesReturnStatus.
   *
   * @param main
   * @param salesReturnStatus
   */
  public static final void insert(Main main, SalesReturnStatus salesReturnStatus) {
    SalesReturnStatusIs.insertAble(main, salesReturnStatus);  //Validating
    AppService.insert(main, salesReturnStatus);

  }

  /**
   * Update SalesReturnStat by key.
   *
   * @param main
   * @param salesReturnStatus
   * @return SalesReturnStatus
   */
  public static final SalesReturnStatus updateByPk(Main main, SalesReturnStatus salesReturnStatus) {
    SalesReturnStatusIs.updateAble(main, salesReturnStatus); //Validating
    return (SalesReturnStatus) AppService.update(main, salesReturnStatus);
  }

  /**
   * Insert or update SalesReturnStatus
   *
   * @param main
   * @param salesReturnStatus
   */
  public static void insertOrUpdate(Main main, SalesReturnStatus salesReturnStatus) {
    if (salesReturnStatus.getId() == null) {
      insert(main, salesReturnStatus);
    } else {
      updateByPk(main, salesReturnStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesReturnStatus
   */
  public static void clone(Main main, SalesReturnStatus salesReturnStatus) {
    salesReturnStatus.setId(null); //Set to null for insert
    insert(main, salesReturnStatus);
  }

  /**
   * Delete SalesReturnStatus.
   *
   * @param main
   * @param salesReturnStatus
   */
  public static final void deleteByPk(Main main, SalesReturnStatus salesReturnStatus) {
    SalesReturnStatusIs.deleteAble(main, salesReturnStatus); //Validation
    AppService.delete(main, SalesReturnStatus.class, salesReturnStatus.getId());
  }

  /**
   * Delete Array of SalesReturnStatus.
   *
   * @param main
   * @param salesReturnStatus
   */
  public static final void deleteByPkArray(Main main, SalesReturnStatus[] salesReturnStatus) {
    for (SalesReturnStatus e : salesReturnStatus) {
      deleteByPk(main, e);
    }
  }
}
