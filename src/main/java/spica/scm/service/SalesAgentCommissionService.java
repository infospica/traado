/*
 * @(#)SalesAgentCommissionService.java	1.0 Wed May 04 14:12:17 IST 2016 
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
import spica.scm.domain.SalesAgentCommission;
import wawo.entity.core.UserMessageException;

/**
 * SalesAgentCommissionService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 04 14:12:17 IST 2016
 */
public abstract class SalesAgentCommissionService {

  /**
   * SalesAgentCommission paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentCommissionSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_commission", SalesAgentCommission.class, main);
    sql.main("select scm_sales_agent_commission.id,scm_sales_agent_commission.title,scm_sales_agent_commission.created_by,scm_sales_agent_commission.modified_by,scm_sales_agent_commission.created_at,scm_sales_agent_commission.modified_at from scm_sales_agent_commission scm_sales_agent_commission"); //Main query
    sql.count("select count(scm_sales_agent_commission.id) from scm_sales_agent_commission scm_sales_agent_commission"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_sales_agent_commission.title", "scm_sales_agent_commission.created_by", "scm_sales_agent_commission.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_commission.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_commission.created_at", "scm_sales_agent_commission.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentCommission.
   *
   * @param main
   * @return List of SalesAgentCommission
   */
  public static final List<SalesAgentCommission> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesAgentCommissionSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentCommission based on condition
//   * @param main
//   * @return List<SalesAgentCommission>
//   */
//  public static final List<SalesAgentCommission> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentCommissionSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesAgentCommission by key.
   *
   * @param main
   * @param salesAgentCommission
   * @return SalesAgentCommission
   */
  public static final SalesAgentCommission selectByPk(Main main, SalesAgentCommission salesAgentCommission) {
    return (SalesAgentCommission) AppService.find(main, SalesAgentCommission.class, salesAgentCommission.getId());
  }

  /**
   * Insert SalesAgentCommission.
   *
   * @param main
   * @param salesAgentCommission
   */
  public static final void insert(Main main, SalesAgentCommission salesAgentCommission) {
    insertAble(main, salesAgentCommission);  //Validating
    AppService.insert(main, salesAgentCommission);

  }

  /**
   * Update SalesAgentCommission by key.
   *
   * @param main
   * @param salesAgentCommission
   * @return SalesAgentCommission
   */
  public static final SalesAgentCommission updateByPk(Main main, SalesAgentCommission salesAgentCommission) {
    updateAble(main, salesAgentCommission); //Validating
    return (SalesAgentCommission) AppService.update(main, salesAgentCommission);
  }

  /**
   * Insert or update SalesAgentCommission
   *
   * @param main
   * @param SalesAgentCommission
   */
  public static void insertOrUpdate(Main main, SalesAgentCommission salesAgentCommission) {
    if (salesAgentCommission.getId() == null) {
      insert(main, salesAgentCommission);
    } else {
      updateByPk(main, salesAgentCommission);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesAgentCommission
   */
  public static void clone(Main main, SalesAgentCommission salesAgentCommission) {
    salesAgentCommission.setId(null); //Set to null for insert
    insert(main, salesAgentCommission);
  }

  /**
   * Delete SalesAgentCommission.
   *
   * @param main
   * @param salesAgentCommission
   */
  public static final void deleteByPk(Main main, SalesAgentCommission salesAgentCommission) {
    deleteAble(main, salesAgentCommission); //Validation
    AppService.delete(main, SalesAgentCommission.class, salesAgentCommission.getId());
  }

  /**
   * Delete Array of SalesAgentCommission.
   *
   * @param main
   * @param salesAgentCommission
   */
  public static final void deleteByPkArray(Main main, SalesAgentCommission[] salesAgentCommission) {
    for (SalesAgentCommission e : salesAgentCommission) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param salesAgentCommission
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, SalesAgentCommission salesAgentCommission) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param salesAgentCommission
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, SalesAgentCommission salesAgentCommission) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param salesAgentCommission
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, SalesAgentCommission salesAgentCommission) throws UserMessageException {

  }
}
