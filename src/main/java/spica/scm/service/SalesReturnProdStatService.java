/*
 * @(#)SalesReturnProdStatService.java	1.0 Mon May 09 18:27:36 IST 2016 
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
import spica.scm.domain.SalesReturnProdStat;
import wawo.entity.core.UserMessageException;

/**
 * SalesReturnProdStatService
 * @author	Spirit 1.2
 * @version	1.0, Mon May 09 18:27:36 IST 2016 
 */

public abstract class SalesReturnProdStatService {  
 
 /**
   * SalesReturnProdStat paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesReturnProdStatSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_return_prod_stat", SalesReturnProdStat.class, main);
    sql.main("select scm_sales_return_prod_stat.id,scm_sales_return_prod_stat.title,scm_sales_return_prod_stat.sort_order,scm_sales_return_prod_stat.created_by,scm_sales_return_prod_stat.modified_by,scm_sales_return_prod_stat.created_at,scm_sales_return_prod_stat.modified_at from scm_sales_return_prod_stat scm_sales_return_prod_stat"); //Main query
    sql.count("select count(scm_sales_return_prod_stat.id) from scm_sales_return_prod_stat scm_sales_return_prod_stat"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_sales_return_prod_stat.title","scm_sales_return_prod_stat.created_by","scm_sales_return_prod_stat.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_return_prod_stat.id","scm_sales_return_prod_stat.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_return_prod_stat.created_at","scm_sales_return_prod_stat.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of SalesReturnProdStat.
  * @param main
  * @return List of SalesReturnProdStat
  */
  public static final List<SalesReturnProdStat> listPaged(Main main) {
     return AppService.listPagedJpa(main, getSalesReturnProdStatSqlPaged(main));
  }

//  /**
//   * Return list of SalesReturnProdStat based on condition
//   * @param main
//   * @return List<SalesReturnProdStat>
//   */
//  public static final List<SalesReturnProdStat> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesReturnProdStatSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select SalesReturnProdStat by key.
  * @param main
  * @param salesReturnProdStat
  * @return SalesReturnProdStat
  */
  public static final SalesReturnProdStat selectByPk(Main main, SalesReturnProdStat salesReturnProdStat) {
    return (SalesReturnProdStat) AppService.find(main, SalesReturnProdStat.class, salesReturnProdStat.getId());
  }

 /**
  * Insert SalesReturnProdStat.
  * @param main
  * @param salesReturnProdStat
  */
  public static final void insert(Main main, SalesReturnProdStat salesReturnProdStat) {
    insertAble(main, salesReturnProdStat);  //Validating
    AppService.insert(main, salesReturnProdStat);

  }

 /**
  * Update SalesReturnProdStat by key.
  * @param main
  * @param salesReturnProdStat
  * @return SalesReturnProdStat
  */
  public static final SalesReturnProdStat updateByPk(Main main, SalesReturnProdStat salesReturnProdStat) {
    updateAble(main, salesReturnProdStat); //Validating
    return (SalesReturnProdStat) AppService.update(main, salesReturnProdStat);
  }

  /**
   * Insert or update SalesReturnProdStat
   *
   * @param main
   * @param SalesReturnProdStat
   */
  public static void insertOrUpdate(Main main, SalesReturnProdStat salesReturnProdStat) {
    if (salesReturnProdStat.getId() == null) {
      insert(main, salesReturnProdStat);
    }
    else{
        updateByPk(main, salesReturnProdStat);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesReturnProdStat
   */
  public static void clone(Main main, SalesReturnProdStat salesReturnProdStat) {
    salesReturnProdStat.setId(null); //Set to null for insert
    insert(main, salesReturnProdStat);
  }

 /**
  * Delete SalesReturnProdStat.
  * @param main
  * @param salesReturnProdStat
  */
  public static final void deleteByPk(Main main, SalesReturnProdStat salesReturnProdStat) {
    deleteAble(main, salesReturnProdStat); //Validation
    AppService.delete(main, SalesReturnProdStat.class, salesReturnProdStat.getId());
  }
	
 /**
  * Delete Array of SalesReturnProdStat.
  * @param main
  * @param salesReturnProdStat
  */
  public static final void deleteByPkArray(Main main, SalesReturnProdStat[] salesReturnProdStat) {
    for (SalesReturnProdStat e : salesReturnProdStat) {
      deleteByPk(main, e);
    }
  }
  /**
   * Validate delete.
   *
   * @param main
   * @param salesReturnProdStat
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, SalesReturnProdStat salesReturnProdStat) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param salesReturnProdStat
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, SalesReturnProdStat salesReturnProdStat) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param salesReturnProdStat
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, SalesReturnProdStat salesReturnProdStat) throws UserMessageException {

  }

}
