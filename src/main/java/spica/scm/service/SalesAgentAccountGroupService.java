/*
 * @(#)SalesAgentAccountGroupService.java	1.0 Tue Dec 19 11:35:13 IST 2017 
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
import spica.scm.domain.SalesAgentAccountGroup;
import spica.scm.validate.SalesAgentAccountGroupIs;

/**
 * SalesAgentAccountGroupService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Dec 19 11:35:13 IST 2017
 */
public abstract class SalesAgentAccountGroupService {

  /**
   * SalesAgentAccountGroup paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentAccountGroupSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_account_group", SalesAgentAccountGroup.class, main);
    sql.main("select t1.id,t1.sales_agent_id,t1.account_group_id from scm_sales_agent_account_group scm_sales_agent_account_group "); //Main query
    sql.count("select count(t1.id) as total from scm_sales_agent_account_group scm_sales_agent_account_group "); //Count query
    sql.join("left outer join scm_user_profile t2 on (t2.id = t1.sales_agent_id) left outer join scm_account_group t3 on (t3.id = t1.account_group_id)"); //Join Query

    sql.string(new String[]{"t2.user_code", "t3.group_name"}); //String search or sort fields
    sql.number(new String[]{"t1.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentAccountGroup.
   *
   * @param main
   * @return List of SalesAgentAccountGroup
   */
  public static final List<SalesAgentAccountGroup> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesAgentAccountGroupSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentAccountGroup based on condition
//   * @param main
//   * @return List<SalesAgentAccountGroup>
//   */
//  public static final List<SalesAgentAccountGroup> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentAccountGroupSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesAgentAccountGroup by key.
   *
   * @param main
   * @param salesAgentAccountGroup
   * @return SalesAgentAccountGroup
   */
  public static final SalesAgentAccountGroup selectByPk(Main main, SalesAgentAccountGroup salesAgentAccountGroup) {
    return (SalesAgentAccountGroup) AppService.find(main, SalesAgentAccountGroup.class, salesAgentAccountGroup.getId());
  }

  /**
   * Insert SalesAgentAccountGroup.
   *
   * @param main
   * @param salesAgentAccountGroup
   */
  public static final void insert(Main main, SalesAgentAccountGroup salesAgentAccountGroup) {
    SalesAgentAccountGroupIs.insertAble(main, salesAgentAccountGroup);  //Validating
    AppService.insert(main, salesAgentAccountGroup);

  }

  /**
   * Update SalesAgentAccountGroup by key.
   *
   * @param main
   * @param salesAgentAccountGroup
   * @return SalesAgentAccountGroup
   */
  public static final SalesAgentAccountGroup updateByPk(Main main, SalesAgentAccountGroup salesAgentAccountGroup) {
    SalesAgentAccountGroupIs.updateAble(main, salesAgentAccountGroup); //Validating
    return (SalesAgentAccountGroup) AppService.update(main, salesAgentAccountGroup);
  }

  /**
   * Insert or update SalesAgentAccountGroup
   *
   * @param main
   * @param salesAgentAccountGroup
   */
  public static void insertOrUpdate(Main main, SalesAgentAccountGroup salesAgentAccountGroup) {
    if (salesAgentAccountGroup.getId() == null) {
      insert(main, salesAgentAccountGroup);
    } else {
      updateByPk(main, salesAgentAccountGroup);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesAgentAccountGroup
   */
  public static void clone(Main main, SalesAgentAccountGroup salesAgentAccountGroup) {
    salesAgentAccountGroup.setId(null); //Set to null for insert
    insert(main, salesAgentAccountGroup);
  }

  /**
   * Delete SalesAgentAccountGroup.
   *
   * @param main
   * @param salesAgentAccountGroup
   */
  public static final void deleteByPk(Main main, SalesAgentAccountGroup salesAgentAccountGroup) {
    SalesAgentAccountGroupIs.deleteAble(main, salesAgentAccountGroup); //Validation
    AppService.delete(main, SalesAgentAccountGroup.class, salesAgentAccountGroup.getId());
  }

  /**
   * Delete Array of SalesAgentAccountGroup.
   *
   * @param main
   * @param salesAgentAccountGroup
   */
  public static final void deleteByPkArray(Main main, SalesAgentAccountGroup[] salesAgentAccountGroup) {
    for (SalesAgentAccountGroup e : salesAgentAccountGroup) {
      deleteByPk(main, e);
    }
  }
}
