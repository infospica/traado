/*
 * @(#)StockStatusService.java	1.0 Mon May 09 18:27:36 IST 2016
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
import spica.scm.domain.StockStatus;
import spica.scm.validate.StockStatusIs;

/**
 * StockStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon May 09 18:27:36 IST 2016
 */
public abstract class StockStatusService {

  public static final int STOCK_STATUS_SALEABLE = 1;
  public static final int STOCK_STATUS_SHORTAGE = 2;
  public static final int STOCK_STATUS_DAMAGED = 3;
  public static final int STOCK_STATUS_EXPIRED = 4;
  public static final int STOCK_STATUS_EXCESS = 5;
  public static final int STOCK_STATUS_BLOCKED = 6;
  public static final int STOCK_STATUS_BLOCKED_DAMAGED = 7;
  public static final int STOCK_STATUS_BLOCKED_EXPIRED = 8;
  public static final int STOCK_TYPE_SALEABLE = 0;
  public static final int STOCK_TYPE_FREE = 1;

  /**
   * StockStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStockStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_stock_status", StockStatus.class, main);
    sql.main("select scm_stock_status.id,scm_stock_status.title,scm_stock_status.sort_order,scm_stock_status.created_by,scm_stock_status.modified_by,scm_stock_status.created_at,scm_stock_status.modified_at from scm_stock_status scm_stock_status"); //Main query
    sql.count("select count(scm_stock_status.id) from scm_stock_status scm_stock_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_stock_status.title", "scm_stock_status.created_by", "scm_stock_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_stock_status.id", "scm_stock_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_stock_status.created_at", "scm_stock_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of StockStatus.
   *
   * @param main
   * @return List of StockStatus
   */
  public static final List<StockStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStockStatusSqlPaged(main));
  }

//  /**
//   * Return list of StockStatus based on condition
//   * @param main
//   * @return List<StockStatus>
//   */
//  public static final List<StockStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getStockStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select StockStatus by key.
   *
   * @param main
   * @param stockStatus
   * @return StockStatus
   */
  public static final StockStatus selectByPk(Main main, StockStatus stockStatus) {
    return (StockStatus) AppService.find(main, StockStatus.class, stockStatus.getId());
  }

  /**
   * Insert StockStatus.
   *
   * @param main
   * @param stockStatus
   */
  public static final void insert(Main main, StockStatus stockStatus) {
    StockStatusIs.insertAble(main, stockStatus);  //Validating
    AppService.insert(main, stockStatus);

  }

  /**
   * Update StockStatus by key.
   *
   * @param main
   * @param stockStatus
   * @return StockStatus
   */
  public static final StockStatus updateByPk(Main main, StockStatus stockStatus) {
    StockStatusIs.updateAble(main, stockStatus); //Validating
    return (StockStatus) AppService.update(main, stockStatus);
  }

  /**
   * Insert or update StockStatus
   *
   * @param main
   * @param StockStatus
   */
  public static void insertOrUpdate(Main main, StockStatus stockStatus) {
    if (stockStatus.getId() == null) {
      insert(main, stockStatus);
    } else {
      updateByPk(main, stockStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param stockStatus
   */
  public static void clone(Main main, StockStatus stockStatus) {
    stockStatus.setId(null); //Set to null for insert
    insert(main, stockStatus);
  }

  /**
   * Delete StockStatus.
   *
   * @param main
   * @param stockStatus
   */
  public static final void deleteByPk(Main main, StockStatus stockStatus) {
    StockStatusIs.deleteAble(main, stockStatus); //Validation
    AppService.delete(main, StockStatus.class, stockStatus.getId());
  }

  /**
   * Delete Array of StockStatus.
   *
   * @param main
   * @param stockStatus
   */
  public static final void deleteByPkArray(Main main, StockStatus[] stockStatus) {
    for (StockStatus e : stockStatus) {
      deleteByPk(main, e);
    }
  }
}
