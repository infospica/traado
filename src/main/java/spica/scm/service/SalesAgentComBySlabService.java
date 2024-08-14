/*
 * @(#)SalesAgentComBySlabService.java	1.0 Wed May 04 14:12:17 IST 2016
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
import spica.scm.domain.SalesAgentComBySlab;

/**
 * SalesAgentComBySlabService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 04 14:12:17 IST 2016
 */
public abstract class SalesAgentComBySlabService {

  /**
   * SalesAgentComBySlab paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentComBySlabSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_com_by_slab", SalesAgentComBySlab.class, main);
    sql.main("select scm_sales_agent_com_by_slab.id,scm_sales_agent_com_by_slab.sales_agent_contract_id,scm_sales_agent_com_by_slab.slab_from,scm_sales_agent_com_by_slab.slab_to,scm_sales_agent_com_by_slab.commission_value,scm_sales_agent_com_by_slab.created_by,scm_sales_agent_com_by_slab.modified_by,scm_sales_agent_com_by_slab.created_at,scm_sales_agent_com_by_slab.modified_at from scm_sales_agent_com_by_slab scm_sales_agent_com_by_slab "); //Main query
    sql.count("select count(scm_sales_agent_com_by_slab.id) from scm_sales_agent_com_by_slab scm_sales_agent_com_by_slab "); //Count query
    sql.join("left outer join scm_sales_agent_contract scm_sales_agent_com_by_slabsales_agent_contract_id on (scm_sales_agent_com_by_slabsales_agent_contract_id.id = scm_sales_agent_com_by_slab.sales_agent_contract_id)"); //Join Query

    sql.string(new String[]{"scm_sales_agent_com_by_slab.created_by", "scm_sales_agent_com_by_slab.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_com_by_slab.id", "scm_sales_agent_com_by_slab.slab_from", "scm_sales_agent_com_by_slab.slab_to", "scm_sales_agent_com_by_slab.commission_value"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_com_by_slabsales_agent_contract_id.applicable_from", "scm_sales_agent_com_by_slab.created_at", "scm_sales_agent_com_by_slab.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentComBySlab.
   *
   * @param main
   * @return List of SalesAgentComBySlab
   */
  public static final List<SalesAgentComBySlab> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesAgentComBySlabSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentComBySlab based on condition
//   * @param main
//   * @return List<SalesAgentComBySlab>
//   */
//  public static final List<SalesAgentComBySlab> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentComBySlabSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesAgentComBySlab by key.
   *
   * @param main
   * @param salesAgentComBySlab
   * @return SalesAgentComBySlab
   */
  public static final SalesAgentComBySlab selectByPk(Main main, SalesAgentComBySlab salesAgentComBySlab) {
    return (SalesAgentComBySlab) AppService.find(main, SalesAgentComBySlab.class, salesAgentComBySlab.getId());
  }

  /**
   * Insert SalesAgentComBySlab.
   *
   * @param main
   * @param salesAgentComBySlab
   */
  public static final void insert(Main main, SalesAgentComBySlab salesAgentComBySlab) {
    AppService.insert(main, salesAgentComBySlab);
  }

  /**
   * Update SalesAgentComBySlab by key.
   *
   * @param main
   * @param salesAgentComBySlab
   * @return SalesAgentComBySlab
   */
  public static final SalesAgentComBySlab updateByPk(Main main, SalesAgentComBySlab salesAgentComBySlab) {
    return (SalesAgentComBySlab) AppService.update(main, salesAgentComBySlab);
  }

  /**
   * Insert or update SalesAgentComBySlab
   *
   * @param main
   * @param SalesAgentComBySlab
   */
  public static void insertOrUpdate(Main main, SalesAgentComBySlab salesAgentComBySlab) {
    if (salesAgentComBySlab.getId() == null) {
      insert(main, salesAgentComBySlab);
    } else {
      updateByPk(main, salesAgentComBySlab);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesAgentComBySlab
   */
  public static void clone(Main main, SalesAgentComBySlab salesAgentComBySlab) {
    salesAgentComBySlab.setId(null); //Set to null for insert
    insert(main, salesAgentComBySlab);
  }

  /**
   * Delete SalesAgentComBySlab.
   *
   * @param main
   * @param salesAgentComBySlab
   */
  public static final void deleteByPk(Main main, SalesAgentComBySlab salesAgentComBySlab) {
    AppService.delete(main, SalesAgentComBySlab.class, salesAgentComBySlab.getId());
  }

  /**
   * Delete Array of SalesAgentComBySlab.
   *
   * @param main
   * @param salesAgentComBySlab
   */
  public static final void deleteByPkArray(Main main, SalesAgentComBySlab[] salesAgentComBySlab) {
    for (SalesAgentComBySlab e : salesAgentComBySlab) {
      deleteByPk(main, e);
    }
  }

  public static List<SalesAgentComBySlab> selectContractCommissionSlabBySalesContract(Main main, Integer salesAgentContractId) {
    return AppService.list(main, SalesAgentComBySlab.class, "select * from scm_sales_agent_com_by_slab where sales_agent_contract_id = ?", new Object[]{salesAgentContractId});
  }
}
