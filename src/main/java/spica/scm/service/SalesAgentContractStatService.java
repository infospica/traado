/*
 * @(#)SalesAgentContractStatService.java	1.0 Wed May 04 14:12:17 IST 2016 
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
import spica.scm.domain.SalesAgentContractStat;
import wawo.entity.core.UserMessageException;

/**
 * SalesAgentContractStatService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 04 14:12:17 IST 2016
 */
public abstract class SalesAgentContractStatService {

  /**
   * SalesAgentContractStat paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentContractStatSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_contract_stat", SalesAgentContractStat.class, main);
    sql.main("select scm_sales_agent_contract_stat.id,scm_sales_agent_contract_stat.title,scm_sales_agent_contract_stat.priority,scm_sales_agent_contract_stat.created_by,scm_sales_agent_contract_stat.modified_by,scm_sales_agent_contract_stat.created_at,scm_sales_agent_contract_stat.modified_at from scm_sales_agent_contract_stat scm_sales_agent_contract_stat"); //Main query
    sql.count("select count(scm_sales_agent_contract_stat.id) from scm_sales_agent_contract_stat scm_sales_agent_contract_stat"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_sales_agent_contract_stat.title", "scm_sales_agent_contract_stat.created_by", "scm_sales_agent_contract_stat.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_contract_stat.id", "scm_sales_agent_contract_stat.priority"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_contract_stat.created_at", "scm_sales_agent_contract_stat.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentContractStat.
   *
   * @param main
   * @return List of SalesAgentContractStat
   */
  public static final List<SalesAgentContractStat> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesAgentContractStatSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentContractStat based on condition
//   * @param main
//   * @return List<SalesAgentContractStat>
//   */
//  public static final List<SalesAgentContractStat> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentContractStatSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesAgentContractStat by key.
   *
   * @param main
   * @param salesAgentContractStat
   * @return SalesAgentContractStat
   */
  public static final SalesAgentContractStat selectByPk(Main main, SalesAgentContractStat salesAgentContractStat) {
    return (SalesAgentContractStat) AppService.find(main, SalesAgentContractStat.class, salesAgentContractStat.getId());
  }

  /**
   * Insert SalesAgentContractStat.
   *
   * @param main
   * @param salesAgentContractStat
   */
  public static final void insert(Main main, SalesAgentContractStat salesAgentContractStat) {
    insertAble(main, salesAgentContractStat);  //Validating
    AppService.insert(main, salesAgentContractStat);

  }

  /**
   * Update SalesAgentContractStat by key.
   *
   * @param main
   * @param salesAgentContractStat
   * @return SalesAgentContractStat
   */
  public static final SalesAgentContractStat updateByPk(Main main, SalesAgentContractStat salesAgentContractStat) {
    updateAble(main, salesAgentContractStat); //Validating
    return (SalesAgentContractStat) AppService.update(main, salesAgentContractStat);
  }

  /**
   * Insert or update SalesAgentContractStat
   *
   * @param main
   * @param SalesAgentContractStat
   */
  public static void insertOrUpdate(Main main, SalesAgentContractStat salesAgentContractStat) {
    if (salesAgentContractStat.getId() == null) {
      insert(main, salesAgentContractStat);
    } else {
      updateByPk(main, salesAgentContractStat);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesAgentContractStat
   */
  public static void clone(Main main, SalesAgentContractStat salesAgentContractStat) {
    salesAgentContractStat.setId(null); //Set to null for insert
    insert(main, salesAgentContractStat);
  }

  /**
   * Delete SalesAgentContractStat.
   *
   * @param main
   * @param salesAgentContractStat
   */
  public static final void deleteByPk(Main main, SalesAgentContractStat salesAgentContractStat) {
    deleteAble(main, salesAgentContractStat); //Validation
    AppService.delete(main, SalesAgentContractStat.class, salesAgentContractStat.getId());
  }

  /**
   * Delete Array of SalesAgentContractStat.
   *
   * @param main
   * @param salesAgentContractStat
   */
  public static final void deleteByPkArray(Main main, SalesAgentContractStat[] salesAgentContractStat) {
    for (SalesAgentContractStat e : salesAgentContractStat) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param salesAgentContractStat
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, SalesAgentContractStat salesAgentContractStat) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param salesAgentContractStat
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, SalesAgentContractStat salesAgentContractStat) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_sales_agent_contract_stat where upper(title)=?", new Object[]{StringUtil.toUpperCase(salesAgentContractStat.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param salesAgentContractStat
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, SalesAgentContractStat salesAgentContractStat) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_sales_agent_contract_stat where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(salesAgentContractStat.getTitle()), salesAgentContractStat.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

}
