/*
 * @(#)TerritoryStateService.java	1.0 Tue May 24 12:29:21 IST 2016 
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
import spica.scm.domain.TerritoryState;
import spica.scm.validate.TerritoryStateIs;

/**
 * TerritoryStateService
 * @author	Spirit 1.2
 * @version	1.0, Tue May 24 12:29:21 IST 2016 
 */

public abstract class TerritoryStateService {  
 
 /**
   * TerritoryState paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTerritoryStateSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_territory_state", TerritoryState.class, main);
    sql.main("select scm_territory_state.id,scm_territory_state.territory_id,scm_territory_state.state_id from scm_territory_state scm_territory_state "); //Main query
    sql.count("select count(scm_territory_state.id) from scm_territory_state scm_territory_state "); //Count query
    sql.join("left outer join scm_territory scm_territory_stateterritory_id on (scm_territory_stateterritory_id.id = scm_territory_state.territory_id) left outer join scm_state scm_territory_statestate_id on (scm_territory_statestate_id.id = scm_territory_state.state_id)"); //Join Query

    sql.string(new String[]{"scm_territory_stateterritory_id.territory_name","scm_territory_statestate_id.state_name"}); //String search or sort fields
    sql.number(new String[]{"scm_territory_state.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of TerritoryState.
  * @param main
  * @return List of TerritoryState
  */
  public static final List<TerritoryState> listPaged(Main main) {
     return AppService.listPagedJpa(main, getTerritoryStateSqlPaged(main));
  }

//  /**
//   * Return list of TerritoryState based on condition
//   * @param main
//   * @return List<TerritoryState>
//   */
//  public static final List<TerritoryState> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTerritoryStateSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select TerritoryState by key.
  * @param main
  * @param territoryState
  * @return TerritoryState
  */
  public static final TerritoryState selectByPk(Main main, TerritoryState territoryState) {
    return (TerritoryState) AppService.find(main, TerritoryState.class, territoryState.getId());
  }

 /**
  * Insert TerritoryState.
  * @param main
  * @param territoryState
  */
  public static final void insert(Main main, TerritoryState territoryState) {
    TerritoryStateIs.insertAble(main, territoryState);  //Validating
    AppService.insert(main, territoryState);

  }

 /**
  * Update TerritoryState by key.
  * @param main
  * @param territoryState
  * @return TerritoryState
  */
  public static final TerritoryState updateByPk(Main main, TerritoryState territoryState) {
    TerritoryStateIs.updateAble(main, territoryState); //Validating
    return (TerritoryState) AppService.update(main, territoryState);
  }

  /**
   * Insert or update TerritoryState
   *
   * @param main
   * @param TerritoryState
   */
  public static void insertOrUpdate(Main main, TerritoryState territoryState) {
    if (territoryState.getId() == null) {
      insert(main, territoryState);
    }
    else{
        updateByPk(main, territoryState);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param territoryState
   */
  public static void clone(Main main, TerritoryState territoryState) {
    territoryState.setId(null); //Set to null for insert
    insert(main, territoryState);
  }

 /**
  * Delete TerritoryState.
  * @param main
  * @param territoryState
  */
  public static final void deleteByPk(Main main, TerritoryState territoryState) {
    TerritoryStateIs.deleteAble(main, territoryState); //Validation
    AppService.delete(main, TerritoryState.class, territoryState.getId());
  }
	
 /**
  * Delete Array of TerritoryState.
  * @param main
  * @param territoryState
  */
  public static final void deleteByPkArray(Main main, TerritoryState[] territoryState) {
    for (TerritoryState e : territoryState) {
      deleteByPk(main, e);
    }
  }
}
