/*
 * @(#)CustomerPricelistService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
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
import spica.scm.domain.CustomerPricelist;
import spica.scm.validate.CustomerPricelistIs;

/**
 * CustomerPricelistService
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016 
 */

public abstract class CustomerPricelistService {  
 
 /**
   * CustomerPricelist paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCustomerPricelistSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_customer_pricelist", CustomerPricelist.class, main);
    sql.main("select scm_customer_pricelist.id,scm_customer_pricelist.customer_id,scm_customer_pricelist.account_group_price_list_id,scm_customer_pricelist.sort_order,scm_customer_pricelist.status_id,scm_customer_pricelist.created_by,scm_customer_pricelist.modified_by,scm_customer_pricelist.created_at,scm_customer_pricelist.modified_at from scm_customer_pricelist scm_customer_pricelist "); //Main query
    sql.count("select count(scm_customer_pricelist.id) as total from scm_customer_pricelist scm_customer_pricelist "); //Count query
    sql.join("left outer join scm_customer scm_customer_pricelistcustomer_id on (scm_customer_pricelistcustomer_id.id = scm_customer_pricelist.customer_id) left outer join scm_status scm_customer_priceliststatus_id on (scm_customer_priceliststatus_id.id = scm_customer_pricelist.status_id)"); //Join Query

    sql.string(new String[]{"scm_customer_pricelistcustomer_id.customer_name","scm_customer_priceliststatus_id.title","scm_customer_pricelist.created_by","scm_customer_pricelist.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_customer_pricelist.id","scm_customer_pricelist.account_group_price_list_id","scm_customer_pricelist.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_customer_pricelist.created_at","scm_customer_pricelist.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of CustomerPricelist.
  * @param main
  * @return List of CustomerPricelist
  */
  public static final List<CustomerPricelist> listPaged(Main main) {
     return AppService.listPagedJpa(main, getCustomerPricelistSqlPaged(main));
  }

//  /**
//   * Return list of CustomerPricelist based on condition
//   * @param main
//   * @return List<CustomerPricelist>
//   */
//  public static final List<CustomerPricelist> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCustomerPricelistSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select CustomerPricelist by key.
  * @param main
  * @param customerPricelist
  * @return CustomerPricelist
  */
  public static final CustomerPricelist selectByPk(Main main, CustomerPricelist customerPricelist) {
    return (CustomerPricelist) AppService.find(main, CustomerPricelist.class, customerPricelist.getId());
  }

 /**
  * Insert CustomerPricelist.
  * @param main
  * @param customerPricelist
  */
  public static final void insert(Main main, CustomerPricelist customerPricelist) {
    CustomerPricelistIs.insertAble(main, customerPricelist);  //Validating
    AppService.insert(main, customerPricelist);

  }

 /**
  * Update CustomerPricelist by key.
  * @param main
  * @param customerPricelist
  * @return CustomerPricelist
  */
  public static final CustomerPricelist updateByPk(Main main, CustomerPricelist customerPricelist) {
    CustomerPricelistIs.updateAble(main, customerPricelist); //Validating
    return (CustomerPricelist) AppService.update(main, customerPricelist);
  }

  /**
   * Insert or update CustomerPricelist
   *
   * @param main
   * @param customerPricelist
   */
  public static void insertOrUpdate(Main main, CustomerPricelist customerPricelist) {
    if (customerPricelist.getId() == null) {
      insert(main, customerPricelist);
    }
    else{
      updateByPk(main, customerPricelist);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param customerPricelist
   */
  public static void clone(Main main, CustomerPricelist customerPricelist) {
    customerPricelist.setId(null); //Set to null for insert
    insert(main, customerPricelist);
  }

 /**
  * Delete CustomerPricelist.
  * @param main
  * @param customerPricelist
  */
  public static final void deleteByPk(Main main, CustomerPricelist customerPricelist) {
    CustomerPricelistIs.deleteAble(main, customerPricelist); //Validation
    AppService.delete(main, CustomerPricelist.class, customerPricelist.getId());
  }
	
 /**
  * Delete Array of CustomerPricelist.
  * @param main
  * @param customerPricelist
  */
  public static final void deleteByPkArray(Main main, CustomerPricelist[] customerPricelist) {
    for (CustomerPricelist e : customerPricelist) {
      deleteByPk(main, e);
    }
  }
}
