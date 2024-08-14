/*
 * @(#)SalesAgentCommisionPayableService.java	1.0 Mon Jun 05 13:13:48 IST 2017 
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
import spica.scm.domain.SalesAgentCommisionPayable;
import spica.scm.validate.SalesAgentCommisionPayableIs;

/**
 * SalesAgentCommisionPayableService
 * @author	Spirit 1.2
 * @version	1.0, Mon Jun 05 13:13:48 IST 2017 
 */

public abstract class SalesAgentCommisionPayableService {  
 
 /**
   * SalesAgentCommisionPayable paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentCommisionPayableSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_commision_payable", SalesAgentCommisionPayable.class, main);
    sql.main("select scm_sales_agent_commision_payable.id,scm_sales_agent_commision_payable.sales_agent_commision_claim_id,scm_sales_agent_commision_payable.sales_agent_commision_id,scm_sales_agent_commision_payable.commision_payable,scm_sales_agent_commision_payable.status,scm_sales_agent_commision_payable.created_by,scm_sales_agent_commision_payable.modified_by,scm_sales_agent_commision_payable.created_at,scm_sales_agent_commision_payable.modified_at from scm_sales_agent_commision_payable scm_sales_agent_commision_payable "); //Main query
    sql.count("select count(scm_sales_agent_commision_payable.id) as total from scm_sales_agent_commision_payable scm_sales_agent_commision_payable "); //Count query
    sql.join("left outer join scm_sales_agent_commision_claim scm_sales_agent_commision_payablesales_agent_commision_claim_id on (scm_sales_agent_commision_payablesales_agent_commision_claim_id.id = scm_sales_agent_commision_payable.sales_agent_commision_claim_id) left outer join scm_sales_agent_commision scm_sales_agent_commision_payablesales_agent_commision_id on (scm_sales_agent_commision_payablesales_agent_commision_id.id = scm_sales_agent_commision_payable.sales_agent_commision_id)"); //Join Query

    sql.string(new String[]{"scm_sales_agent_commision_payablesales_agent_commision_id.commission_applicable_on","scm_sales_agent_commision_payable.created_by","scm_sales_agent_commision_payable.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_commision_payable.id","scm_sales_agent_commision_payablesales_agent_commision_claim_id.commision_total","scm_sales_agent_commision_payable.commision_payable","scm_sales_agent_commision_payable.status"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_commision_payable.created_at","scm_sales_agent_commision_payable.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of SalesAgentCommisionPayable.
  * @param main
  * @return List of SalesAgentCommisionPayable
  */
  public static final List<SalesAgentCommisionPayable> listPaged(Main main) {
     return AppService.listPagedJpa(main, getSalesAgentCommisionPayableSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentCommisionPayable based on condition
//   * @param main
//   * @return List<SalesAgentCommisionPayable>
//   */
//  public static final List<SalesAgentCommisionPayable> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentCommisionPayableSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select SalesAgentCommisionPayable by key.
  * @param main
  * @param scmSalesAgentCommisionPayable
  * @return SalesAgentCommisionPayable
  */
  public static final SalesAgentCommisionPayable selectByPk(Main main, SalesAgentCommisionPayable scmSalesAgentCommisionPayable) {
    return (SalesAgentCommisionPayable) AppService.find(main, SalesAgentCommisionPayable.class, scmSalesAgentCommisionPayable.getId());
  }

 /**
  * Insert SalesAgentCommisionPayable.
  * @param main
  * @param scmSalesAgentCommisionPayable
  */
  public static final void insert(Main main, SalesAgentCommisionPayable scmSalesAgentCommisionPayable) {
    SalesAgentCommisionPayableIs.insertAble(main, scmSalesAgentCommisionPayable);  //Validating
    AppService.insert(main, scmSalesAgentCommisionPayable);

  }

 /**
  * Update SalesAgentCommisionPayable by key.
  * @param main
  * @param scmSalesAgentCommisionPayable
  * @return SalesAgentCommisionPayable
  */
  public static final SalesAgentCommisionPayable updateByPk(Main main, SalesAgentCommisionPayable scmSalesAgentCommisionPayable) {
    SalesAgentCommisionPayableIs.updateAble(main, scmSalesAgentCommisionPayable); //Validating
    return (SalesAgentCommisionPayable) AppService.update(main, scmSalesAgentCommisionPayable);
  }

  /**
   * Insert or update SalesAgentCommisionPayable
   *
   * @param main
   * @param scmSalesAgentCommisionPayable
   */
  public static void insertOrUpdate(Main main, SalesAgentCommisionPayable scmSalesAgentCommisionPayable) {
    if (scmSalesAgentCommisionPayable.getId() == null) {
      insert(main, scmSalesAgentCommisionPayable);
    }
    else{
      updateByPk(main, scmSalesAgentCommisionPayable);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param scmSalesAgentCommisionPayable
   */
  public static void clone(Main main, SalesAgentCommisionPayable scmSalesAgentCommisionPayable) {
    scmSalesAgentCommisionPayable.setId(null); //Set to null for insert
    insert(main, scmSalesAgentCommisionPayable);
  }

 /**
  * Delete SalesAgentCommisionPayable.
  * @param main
  * @param scmSalesAgentCommisionPayable
  */
  public static final void deleteByPk(Main main, SalesAgentCommisionPayable scmSalesAgentCommisionPayable) {
    SalesAgentCommisionPayableIs.deleteAble(main, scmSalesAgentCommisionPayable); //Validation
    AppService.delete(main, SalesAgentCommisionPayable.class, scmSalesAgentCommisionPayable.getId());
  }
	
 /**
  * Delete Array of SalesAgentCommisionPayable.
  * @param main
  * @param scmSalesAgentCommisionPayable
  */
  public static final void deleteByPkArray(Main main, SalesAgentCommisionPayable[] scmSalesAgentCommisionPayable) {
    for (SalesAgentCommisionPayable e : scmSalesAgentCommisionPayable) {
      deleteByPk(main, e);
    }
  }
}
