/*
 * @(#)SalesReqStatusService.java	1.0 Mon May 09 18:27:36 IST 2016 
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
import spica.scm.domain.SalesReqStatus;
import wawo.entity.core.UserMessageException;

/**
 * SalesReqStatusService
 * @author	Spirit 1.2
 * @version	1.0, Mon May 09 18:27:36 IST 2016 
 */

public abstract class SalesReqStatusService {  
 
 /**
   * SalesReqStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesReqStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_req_status", SalesReqStatus.class, main);
    sql.main("select scm_sales_req_status.id,scm_sales_req_status.title,scm_sales_req_status.sort_order,scm_sales_req_status.created_by,scm_sales_req_status.modified_by,scm_sales_req_status.created_at,scm_sales_req_status.modified_at from scm_sales_req_status scm_sales_req_status"); //Main query
    sql.count("select count(scm_sales_req_status.id) from scm_sales_req_status scm_sales_req_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_sales_req_status.title","scm_sales_req_status.created_by","scm_sales_req_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_req_status.id","scm_sales_req_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_req_status.created_at","scm_sales_req_status.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of SalesReqStatus.
  * @param main
  * @return List of SalesReqStatus
  */
  public static final List<SalesReqStatus> listPaged(Main main) {
     return AppService.listPagedJpa(main, getSalesReqStatusSqlPaged(main));
  }

//  /**
//   * Return list of SalesReqStatus based on condition
//   * @param main
//   * @return List<SalesReqStatus>
//   */
//  public static final List<SalesReqStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesReqStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select SalesReqStatus by key.
  * @param main
  * @param salesReqStatus
  * @return SalesReqStatus
  */
  public static final SalesReqStatus selectByPk(Main main, SalesReqStatus salesReqStatus) {
    return (SalesReqStatus) AppService.find(main, SalesReqStatus.class, salesReqStatus.getId());
  }

 /**
  * Insert SalesReqStatus.
  * @param main
  * @param salesReqStatus
  */
  public static final void insert(Main main, SalesReqStatus salesReqStatus) {
    insertAble(main, salesReqStatus);  //Validating
    AppService.insert(main, salesReqStatus);

  }

 /**
  * Update SalesReqStatus by key.
  * @param main
  * @param salesReqStatus
  * @return SalesReqStatus
  */
  public static final SalesReqStatus updateByPk(Main main, SalesReqStatus salesReqStatus) {
    updateAble(main, salesReqStatus); //Validating
    return (SalesReqStatus) AppService.update(main, salesReqStatus);
  }

  /**
   * Insert or update SalesReqStatus
   *
   * @param main
   * @param SalesReqStatus
   */
  public static void insertOrUpdate(Main main, SalesReqStatus salesReqStatus) {
    if (salesReqStatus.getId() == null) {
      insert(main, salesReqStatus);
    }
    else{
        updateByPk(main, salesReqStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesReqStatus
   */
  public static void clone(Main main, SalesReqStatus salesReqStatus) {
    salesReqStatus.setId(null); //Set to null for insert
    insert(main, salesReqStatus);
  }

 /**
  * Delete SalesReqStatus.
  * @param main
  * @param salesReqStatus
  */
  public static final void deleteByPk(Main main, SalesReqStatus salesReqStatus) {
    deleteAble(main, salesReqStatus); //Validation
    AppService.delete(main, SalesReqStatus.class, salesReqStatus.getId());
  }
	
 /**
  * Delete Array of SalesReqStatus.
  * @param main
  * @param salesReqStatus
  */
  public static final void deleteByPkArray(Main main, SalesReqStatus[] salesReqStatus) {
    for (SalesReqStatus e : salesReqStatus) {
      deleteByPk(main, e);
    }
  }
  /**
   * Validate delete.
   *
   * @param main
   * @param salesReqStatus
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, SalesReqStatus salesReqStatus) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param salesReqStatus
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, SalesReqStatus salesReqStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_sales_req_status where upper(title)=?", new Object[]{StringUtil.toUpperCase(salesReqStatus.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param salesReqStatus
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, SalesReqStatus salesReqStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_sales_req_status where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(salesReqStatus.getTitle()), salesReqStatus.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
