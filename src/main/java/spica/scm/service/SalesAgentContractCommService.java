/*
 * @(#)SalesAgentContractCommService.java	1.0 Sat May 06 14:29:13 IST 2017 
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
import spica.scm.domain.SalesAgentContractComm;
import spica.scm.validate.SalesAgentContractCommIs;

/**
 * SalesAgentContractCommService
 *
 * @author	Spirit 1.2
 * @version	1.0, Sat May 06 14:29:13 IST 2017
 */
public abstract class SalesAgentContractCommService {

  /**
   * SalesAgentContractComm paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentContractCommSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_contract_comm", SalesAgentContractComm.class, main);
    sql.main("select scm_sales_agent_contract_comm.id,scm_sales_agent_contract_comm.sales_agent_contract_id,scm_sales_agent_contract_comm.value_fixed,scm_sales_agent_contract_comm.value_percentage,scm_sales_agent_contract_comm.range_from,scm_sales_agent_contract_comm.range_to from scm_sales_agent_contract_comm scm_sales_agent_contract_comm "); //Main query
    sql.count("select count(scm_sales_agent_contract_comm.id) as total from scm_sales_agent_contract_comm scm_sales_agent_contract_comm "); //Count query
    sql.join("left outer join scm_sales_agent_contract scm_sales_agent_contract_commsales_agent_contracr_id on (scm_sales_agent_contract_commsales_agent_contract_id.id = scm_sales_agent_contract_comm.sales_agent_contracr_id)"); //Join Query

    sql.string(null); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_contract_comm.id", "scm_sales_agent_contract_comm.value_fixed", "scm_sales_agent_contract_comm.value_percentage", "scm_sales_agent_contract_comm.range_from", "scm_sales_agent_contract_comm.range_to"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_contract_commsales_agent_contracr_id.valid_from"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentContractComm.
   *
   * @param main
   * @return List of SalesAgentContractComm
   */
  public static final List<SalesAgentContractComm> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesAgentContractCommSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentContractComm based on condition
//   * @param main
//   * @return List<SalesAgentContractComm>
//   */
//  public static final List<SalesAgentContractComm> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentContractCommSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesAgentContractComm by key.
   *
   * @param main
   * @param scmSalesAgentContractComm
   * @return SalesAgentContractComm
   */
  public static final SalesAgentContractComm selectByPk(Main main, SalesAgentContractComm scmSalesAgentContractComm) {
    return (SalesAgentContractComm) AppService.find(main, SalesAgentContractComm.class, scmSalesAgentContractComm.getId());
  }

  /**
   * Insert SalesAgentContractComm.
   *
   * @param main
   * @param scmSalesAgentContractComm
   */
  public static final void insert(Main main, SalesAgentContractComm scmSalesAgentContractComm) {
    SalesAgentContractCommIs.insertAble(main, scmSalesAgentContractComm);  //Validating
    AppService.insert(main, scmSalesAgentContractComm);

  }

  /**
   * Update SalesAgentContractComm by key.
   *
   * @param main
   * @param scmSalesAgentContractComm
   * @return SalesAgentContractComm
   */
  public static final SalesAgentContractComm updateByPk(Main main, SalesAgentContractComm scmSalesAgentContractComm) {
    SalesAgentContractCommIs.updateAble(main, scmSalesAgentContractComm); //Validating
    return (SalesAgentContractComm) AppService.update(main, scmSalesAgentContractComm);
  }

  /**
   * Insert or update SalesAgentContractComm
   *
   * @param main
   * @param scmSalesAgentContractComm
   */
  public static void insertOrUpdate(Main main, SalesAgentContractComm scmSalesAgentContractComm) {
    if (scmSalesAgentContractComm.getId() == null) {
      insert(main, scmSalesAgentContractComm);
    } else {
      updateByPk(main, scmSalesAgentContractComm);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param scmSalesAgentContractComm
   */
  public static void clone(Main main, SalesAgentContractComm scmSalesAgentContractComm) {
    scmSalesAgentContractComm.setId(null); //Set to null for insert
    insert(main, scmSalesAgentContractComm);
  }

  /**
   * Delete SalesAgentContractComm.
   *
   * @param main
   * @param scmSalesAgentContractComm
   */
  public static final void deleteByPk(Main main, SalesAgentContractComm scmSalesAgentContractComm) {
    SalesAgentContractCommIs.deleteAble(main, scmSalesAgentContractComm); //Validation
    AppService.delete(main, SalesAgentContractComm.class, scmSalesAgentContractComm.getId());
  }

  /**
   * Delete Array of SalesAgentContractComm.
   *
   * @param main
   * @param scmSalesAgentContractComm
   */
  public static final void deleteByPkArray(Main main, SalesAgentContractComm[] scmSalesAgentContractComm) {
    for (SalesAgentContractComm e : scmSalesAgentContractComm) {
      deleteByPk(main, e);
    }
  }
}
