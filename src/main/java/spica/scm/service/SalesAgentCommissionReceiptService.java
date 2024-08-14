/*
 * @(#)SalesAgentCommissionReceiptService.java	1.0 Mon Dec 18 16:22:19 IST 2017 
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
import spica.scm.domain.SalesAgentCommissionReceipt;
import spica.scm.validate.SalesAgentCommissionReceiptIs;

/**
 * SalesAgentCommissionReceiptService
 * @author	Spirit 1.2
 * @version	1.0, Mon Dec 18 16:22:19 IST 2017 
 */

public abstract class SalesAgentCommissionReceiptService {  
 
 /**
   * SalesAgentCommissionReceipt paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentCommissionReceiptSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_commission_receipt", SalesAgentCommissionReceipt.class, main);
    sql.main("select scm_sales_agent_commission_receipt.id,scm_sales_agent_commission_receipt.user_profile_id,scm_sales_agent_commission_receipt.entry_date,scm_sales_agent_commission_receipt.commission_value,scm_sales_agent_commission_receipt.sales_agent_commission_status_id from scm_sales_agent_commission_receipt scm_sales_agent_commission_receipt "); //Main query
    sql.count("select count(scm_sales_agent_commission_receipt.id) as total from scm_sales_agent_commission_receipt scm_sales_agent_commission_receipt "); //Count query
    sql.join("left outer join scm_user_profile scm_sales_agent_commission_receiptuser_profile_id on (scm_sales_agent_commission_receiptuser_profile_id.id = scm_sales_agent_commission_receipt.user_profile_id)"); //Join Query

    sql.string(new String[]{"scm_sales_agent_commission_receiptuser_profile_id.user_code"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_commission_receipt.id","scm_sales_agent_commission_receipt.commission_value","scm_sales_agent_commission_receipt.sales_agent_commission_status_id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_commission_receipt.entry_date"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of SalesAgentCommissionReceipt.
  * @param main
  * @return List of SalesAgentCommissionReceipt
  */
  public static final List<SalesAgentCommissionReceipt> listPaged(Main main) {
     return AppService.listPagedJpa(main, getSalesAgentCommissionReceiptSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentCommissionReceipt based on condition
//   * @param main
//   * @return List<SalesAgentCommissionReceipt>
//   */
//  public static final List<SalesAgentCommissionReceipt> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentCommissionReceiptSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select SalesAgentCommissionReceipt by key.
  * @param main
  * @param salesAgentCommissionReceipt
  * @return SalesAgentCommissionReceipt
  */
  public static final SalesAgentCommissionReceipt selectByPk(Main main, SalesAgentCommissionReceipt salesAgentCommissionReceipt) {
    return (SalesAgentCommissionReceipt) AppService.find(main, SalesAgentCommissionReceipt.class, salesAgentCommissionReceipt.getId());
  }

 /**
  * Insert SalesAgentCommissionReceipt.
  * @param main
  * @param salesAgentCommissionReceipt
  */
  public static final void insert(Main main, SalesAgentCommissionReceipt salesAgentCommissionReceipt) {
    SalesAgentCommissionReceiptIs.insertAble(main, salesAgentCommissionReceipt);  //Validating
    AppService.insert(main, salesAgentCommissionReceipt);

  }

 /**
  * Update SalesAgentCommissionReceipt by key.
  * @param main
  * @param salesAgentCommissionReceipt
  * @return SalesAgentCommissionReceipt
  */
  public static final SalesAgentCommissionReceipt updateByPk(Main main, SalesAgentCommissionReceipt salesAgentCommissionReceipt) {
    SalesAgentCommissionReceiptIs.updateAble(main, salesAgentCommissionReceipt); //Validating
    return (SalesAgentCommissionReceipt) AppService.update(main, salesAgentCommissionReceipt);
  }

  /**
   * Insert or update SalesAgentCommissionReceipt
   *
   * @param main
   * @param salesAgentCommissionReceipt
   */
  public static void insertOrUpdate(Main main, SalesAgentCommissionReceipt salesAgentCommissionReceipt) {
    if (salesAgentCommissionReceipt.getId() == null) {
      insert(main, salesAgentCommissionReceipt);
    }
    else{
      updateByPk(main, salesAgentCommissionReceipt);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesAgentCommissionReceipt
   */
  public static void clone(Main main, SalesAgentCommissionReceipt salesAgentCommissionReceipt) {
    salesAgentCommissionReceipt.setId(null); //Set to null for insert
    insert(main, salesAgentCommissionReceipt);
  }

 /**
  * Delete SalesAgentCommissionReceipt.
  * @param main
  * @param salesAgentCommissionReceipt
  */
  public static final void deleteByPk(Main main, SalesAgentCommissionReceipt salesAgentCommissionReceipt) {
    SalesAgentCommissionReceiptIs.deleteAble(main, salesAgentCommissionReceipt); //Validation
    AppService.delete(main, SalesAgentCommissionReceipt.class, salesAgentCommissionReceipt.getId());
  }
	
 /**
  * Delete Array of SalesAgentCommissionReceipt.
  * @param main
  * @param salesAgentCommissionReceipt
  */
  public static final void deleteByPkArray(Main main, SalesAgentCommissionReceipt[] salesAgentCommissionReceipt) {
    for (SalesAgentCommissionReceipt e : salesAgentCommissionReceipt) {
      deleteByPk(main, e);
    }
  }
}
