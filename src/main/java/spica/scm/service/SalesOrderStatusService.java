/*
 * @(#)SalesOrderStatusService.java	1.0 Mon May 09 18:27:36 IST 2016 
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
import spica.scm.domain.SalesOrderStatus;
import wawo.entity.core.UserMessageException;

/**
 * SalesOrderStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon May 09 18:27:36 IST 2016
 */
public abstract class SalesOrderStatusService {

  /**
   * SalesOrderStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesOrderStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_order_status", SalesOrderStatus.class, main);
    sql.main("select scm_sales_order_status.id,scm_sales_order_status.title,scm_sales_order_status.sort_order,scm_sales_order_status.created_by,scm_sales_order_status.modified_by,scm_sales_order_status.created_at,scm_sales_order_status.modified_at from scm_sales_order_status scm_sales_order_status"); //Main query
    sql.count("select count(scm_sales_order_status.id) from scm_sales_order_status scm_sales_order_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_sales_order_status.title", "scm_sales_order_status.created_by", "scm_sales_order_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_order_status.id", "scm_sales_order_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_order_status.created_at", "scm_sales_order_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesOrderStatus.
   *
   * @param main
   * @return List of SalesOrderStatus
   */
  public static final List<SalesOrderStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesOrderStatusSqlPaged(main));
  }

//  /**
//   * Return list of SalesOrderStatus based on condition
//   * @param main
//   * @return List<SalesOrderStatus>
//   */
//  public static final List<SalesOrderStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesOrderStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesOrderStatus by key.
   *
   * @param main
   * @param salesOrderStatus
   * @return SalesOrderStatus
   */
  public static final SalesOrderStatus selectByPk(Main main, SalesOrderStatus salesOrderStatus) {
    return (SalesOrderStatus) AppService.find(main, SalesOrderStatus.class, salesOrderStatus.getId());
  }

  /**
   * Insert SalesOrderStatus.
   *
   * @param main
   * @param salesOrderStatus
   */
  public static final void insert(Main main, SalesOrderStatus salesOrderStatus) {
    insertAble(main, salesOrderStatus);  //Validating
    AppService.insert(main, salesOrderStatus);

  }

  /**
   * Update SalesOrderStatus by key.
   *
   * @param main
   * @param salesOrderStatus
   * @return SalesOrderStatus
   */
  public static final SalesOrderStatus updateByPk(Main main, SalesOrderStatus salesOrderStatus) {
    updateAble(main, salesOrderStatus); //Validating
    return (SalesOrderStatus) AppService.update(main, salesOrderStatus);
  }

  /**
   * Insert or update SalesOrderStatus
   *
   * @param main
   * @param salesOrderStatus
   */
  public static void insertOrUpdate(Main main, SalesOrderStatus salesOrderStatus) {
    if (salesOrderStatus.getId() == null) {
      insert(main, salesOrderStatus);
    } else {
      updateByPk(main, salesOrderStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesOrderStatus
   */
  public static void clone(Main main, SalesOrderStatus salesOrderStatus) {
    salesOrderStatus.setId(null); //Set to null for insert
    insert(main, salesOrderStatus);
  }

  /**
   * Delete SalesOrderStatus.
   *
   * @param main
   * @param salesOrderStatus
   */
  public static final void deleteByPk(Main main, SalesOrderStatus salesOrderStatus) {
    deleteAble(main, salesOrderStatus); //Validation
    AppService.delete(main, SalesOrderStatus.class, salesOrderStatus.getId());
  }

  /**
   * Delete Array of SalesOrderStatus.
   *
   * @param main
   * @param salesOrderStatus
   */
  public static final void deleteByPkArray(Main main, SalesOrderStatus[] salesOrderStatus) {
    for (SalesOrderStatus e : salesOrderStatus) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param salesOrderStatus
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, SalesOrderStatus salesOrderStatus) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param salesOrderStatus
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, SalesOrderStatus salesOrderStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_sales_order_status where upper(title)=?", new Object[]{StringUtil.toUpperCase(salesOrderStatus.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param salesOrderStatus
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, SalesOrderStatus salesOrderStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_sales_order_status where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(salesOrderStatus.getTitle()), salesOrderStatus.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

}
