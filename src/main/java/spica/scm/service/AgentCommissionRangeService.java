/*
 * @(#)AgentCommissionRangeService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.AgentCommissionRange;
import spica.scm.domain.SalesAgentContract;
import spica.scm.validate.AgentCommissionRangeIs;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;

/**
 * AgentCommissionRangeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class AgentCommissionRangeService {

  /**
   * AgentCommissionRange paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAgentCommissionRangeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_agent_commission_range", AgentCommissionRange.class, main);
    sql.main("select scm_agent_commission_range.id,scm_agent_commission_range.sales_agent_contracr_id,scm_agent_commission_range.range_from,scm_agent_commission_range.range_to,scm_agent_commission_range.value_fixed,scm_agent_commission_range.value_percentage from scm_agent_commission_range scm_agent_commission_range"); //Main query
    sql.count("select count(scm_agent_commission_range.id) as total from scm_agent_commission_range scm_agent_commission_range"); //Count query
    sql.join(""); //Join Query

    sql.string(null); //String search or sort fields
    sql.number(new String[]{"scm_agent_commission_range.id", "scm_agent_commission_range.sales_agent_contracr_id", "scm_agent_commission_range.range_from", "scm_agent_commission_range.range_to", "scm_agent_commission_range.value_fixed", "scm_agent_commission_range.value_percentage"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AgentCommissionRange.
   *
   * @param main
   * @return List of AgentCommissionRange
   */
  public static final List<AgentCommissionRange> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAgentCommissionRangeSqlPaged(main));
  }

//  /**
//   * Return list of AgentCommissionRange based on condition
//   * @param main
//   * @return List<AgentCommissionRange>
//   */
//  public static final List<AgentCommissionRange> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAgentCommissionRangeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AgentCommissionRange by key.
   *
   * @param main
   * @param AgentCommissionRange
   * @return AgentCommissionRange
   */
  public static final AgentCommissionRange selectByPk(Main main, AgentCommissionRange agentCommissionRange) {
    return (AgentCommissionRange) AppService.find(main, AgentCommissionRange.class, agentCommissionRange.getId());
  }

  /**
   * Insert AgentCommissionRange.
   *
   * @param main
   * @param AgentCommissionRange
   */
  public static final void insert(Main main, AgentCommissionRange agentCommissionRange) {
    AgentCommissionRangeIs.insertAble(main, agentCommissionRange);  //Validating
    AppService.insert(main, agentCommissionRange);

  }

  /**
   * Update AgentCommissionRange by key.
   *
   * @param main
   * @param AgentCommissionRange
   * @return AgentCommissionRange
   */
  public static final AgentCommissionRange updateByPk(Main main, AgentCommissionRange agentCommissionRange) {
    AgentCommissionRangeIs.updateAble(main, agentCommissionRange); //Validating
    return (AgentCommissionRange) AppService.update(main, agentCommissionRange);
  }

  /**
   * Insert or update AgentCommissionRange
   *
   * @param main
   * @param AgentCommissionRange
   */
  public static void insertOrUpdate(Main main, AgentCommissionRange agentCommissionRange) {
    if (agentCommissionRange.getId() == null) {
      insert(main, agentCommissionRange);
    } else {
      updateByPk(main, agentCommissionRange);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param AgentCommissionRange
   */
  public static void clone(Main main, AgentCommissionRange agentCommissionRange) {
    agentCommissionRange.setId(null); //Set to null for insert
    insert(main, agentCommissionRange);
  }

  /**
   * Delete AgentCommissionRange.
   *
   * @param main
   * @param AgentCommissionRange
   */
  public static final void deleteByPk(Main main, AgentCommissionRange agentCommissionRange) {
    AgentCommissionRangeIs.deleteAble(main, agentCommissionRange); //Validation
    AppService.delete(main, AgentCommissionRange.class, agentCommissionRange.getId());
  }

  /**
   * Delete Array of AgentCommissionRange.
   *
   * @param main
   * @param AgentCommissionRange
   */
  public static final void deleteByPkArray(Main main, AgentCommissionRange[] agentCommissionRange) {
    for (AgentCommissionRange e : agentCommissionRange) {
      deleteByPk(main, e);
    }
  }

  public static final AgentCommissionRange selectSalesAgentCommissionRange(Main main, long rangeId) {
    return (AgentCommissionRange) AppService.single(main, AgentCommissionRange.class, "select * from scm_agent_commission_range scm_agent_commission_range where scm_agent_commission_range.sales_agent_contracr_id = ? ", new Object[]{rangeId});
  }

  public static final List<AgentCommissionRange> getAgentCommissionList(Main main, SalesAgentContract salesAgentContract) {
    SqlPage sql = getAgentCommissionRangeSqlPaged(main);
    if (salesAgentContract.getId() == null) {
      return null;
    }
    sql.cond("where scm_agent_commission_range.sales_agent_contracr_id=? order by range_from desc");
    sql.param(salesAgentContract.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static final void deleteAgentCommissionRange(Main main, AgentCommissionRange agentCommissionRange) {
    AppService.deleteSql(main, AgentCommissionRange.class, "delete from scm_agent_commission_range where id = ?", new Object[]{agentCommissionRange.getId()});
  }
}
