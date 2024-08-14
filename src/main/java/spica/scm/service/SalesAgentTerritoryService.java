/*
 * @(#)SalesAgentTerritoryService.java	1.0 Tue Dec 19 11:35:13 IST 2017 
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
import spica.scm.domain.SalesAgentTerritory;
import spica.scm.validate.SalesAgentTerritoryIs;

/**
 * SalesAgentTerritoryService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Dec 19 11:35:13 IST 2017
 */
public abstract class SalesAgentTerritoryService {

  /**
   * SalesAgentTerritory paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentTerritorySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_territory", SalesAgentTerritory.class, main);
    sql.main("select t1.id,t1.sales_agent_id,t1.territory_id from scm_sales_agent_territory scm_sales_agent_territory "); //Main query
    sql.count("select count(t1.id) as total from scm_sales_agent_territory scm_sales_agent_territory "); //Count query
    sql.join("left outer join scm_user_profile t2 on (t2.id = t1.sales_agent_id) left outer join scm_territory t3 on (t3.id = t1.territory_id)"); //Join Query

    sql.string(new String[]{"t2.user_code", "t3.territory_name"}); //String search or sort fields
    sql.number(new String[]{"t1.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentTerritory.
   *
   * @param main
   * @return List of SalesAgentTerritory
   */
  public static final List<SalesAgentTerritory> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesAgentTerritorySqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentTerritory based on condition
//   * @param main
//   * @return List<SalesAgentTerritory>
//   */
//  public static final List<SalesAgentTerritory> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentTerritorySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesAgentTerritory by key.
   *
   * @param main
   * @param salesAgentTerritory
   * @return SalesAgentTerritory
   */
  public static final SalesAgentTerritory selectByPk(Main main, SalesAgentTerritory salesAgentTerritory) {
    return (SalesAgentTerritory) AppService.find(main, SalesAgentTerritory.class, salesAgentTerritory.getId());
  }

  /**
   * Insert SalesAgentTerritory.
   *
   * @param main
   * @param salesAgentTerritory
   */
  public static final void insert(Main main, SalesAgentTerritory salesAgentTerritory) {
    SalesAgentTerritoryIs.insertAble(main, salesAgentTerritory);  //Validating
    AppService.insert(main, salesAgentTerritory);

  }

  /**
   * Update SalesAgentTerritory by key.
   *
   * @param main
   * @param salesAgentTerritory
   * @return SalesAgentTerritory
   */
  public static final SalesAgentTerritory updateByPk(Main main, SalesAgentTerritory salesAgentTerritory) {
    SalesAgentTerritoryIs.updateAble(main, salesAgentTerritory); //Validating
    return (SalesAgentTerritory) AppService.update(main, salesAgentTerritory);
  }

  /**
   * Insert or update SalesAgentTerritory
   *
   * @param main
   * @param salesAgentTerritory
   */
  public static void insertOrUpdate(Main main, SalesAgentTerritory salesAgentTerritory) {
    if (salesAgentTerritory.getId() == null) {
      insert(main, salesAgentTerritory);
    } else {
      updateByPk(main, salesAgentTerritory);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesAgentTerritory
   */
  public static void clone(Main main, SalesAgentTerritory salesAgentTerritory) {
    salesAgentTerritory.setId(null); //Set to null for insert
    insert(main, salesAgentTerritory);
  }

  /**
   * Delete SalesAgentTerritory.
   *
   * @param main
   * @param salesAgentTerritory
   */
  public static final void deleteByPk(Main main, SalesAgentTerritory salesAgentTerritory) {
    SalesAgentTerritoryIs.deleteAble(main, salesAgentTerritory); //Validation
    AppService.delete(main, SalesAgentTerritory.class, salesAgentTerritory.getId());
  }

  /**
   * Delete Array of SalesAgentTerritory.
   *
   * @param main
   * @param salesAgentTerritory
   */
  public static final void deleteByPkArray(Main main, SalesAgentTerritory[] salesAgentTerritory) {
    for (SalesAgentTerritory e : salesAgentTerritory) {
      deleteByPk(main, e);
    }
  }
}
