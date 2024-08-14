/*
 * @(#)StateService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Country;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.State;
import spica.scm.domain.Territory;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * StateService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class StateService {

  /**
   * State paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStateSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_state", State.class, main);
    sql.main("select scm_state.id,scm_state.country_id,scm_state.state_name,scm_state.state_code,scm_state.created_by,scm_state.modified_by,scm_state.created_at,scm_state.modified_at from scm_state scm_state "); //Main query
    sql.count("select count(scm_state.id) as total from scm_state scm_state "); //Count query
    sql.join("left outer join scm_country scm_statecountry_id on (scm_statecountry_id.id = scm_state.country_id)"); //Join Query

    sql.string(new String[]{"scm_statecountry_id.country_name", "scm_state.state_name", "scm_state.state_code", "scm_state.created_by", "scm_state.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_state.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_state.created_at", "scm_state.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of State.
   *
   * @param main
   * @return List of State
   */
  public static final List<State> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStateSqlPaged(main));
  }

//  /**
//   * Return list of State based on condition
//   * @param main
//   * @return List<State>
//   */
//  public static final List<State> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getStateSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select State by key.
   *
   * @param main
   * @param state
   * @return State
   */
  public static final State selectByPk(Main main, State state) {
    return (State) AppService.find(main, State.class, state.getId());
  }

  /**
   * Insert State.
   *
   * @param main
   * @param state
   */
  public static final void insert(Main main, State state) {
    insertAble(main, state);  //Validating    
    AppService.insert(main, state);

  }

  /**
   * Update State by key.
   *
   * @param main
   * @param state
   * @return State
   */
  public static final State updateByPk(Main main, State state) {
    updateAble(main, state); //Validating    
    return (State) AppService.update(main, state);
  }

  /**
   * Insert or update State
   *
   * @param main
   * @param state
   */
  public static void insertOrUpdate(Main main, State state) {
    if (state.getId() == null) {
      insert(main, state);
    } else {
      updateByPk(main, state);
    }
  }

  private static void toUpperNameAndCode(State state) {
    state.setStateName(state.getStateName().toUpperCase());
    state.setStateCode(state.getStateCode().toUpperCase());
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param state
   */
  public static void clone(Main main, State state) {
    state.setId(null); //Set to null for insert
    insert(main, state);
  }

  /**
   * Delete State.
   *
   * @param main
   * @param state
   */
  public static final void deleteByPk(Main main, State state) {
    deleteAble(main, state); //Validation
    AppService.delete(main, State.class, state.getId());
  }

  /**
   * Delete Array of State.
   *
   * @param main
   * @param state
   */
  public static final void deleteByPkArray(Main main, State[] state) {
    for (State e : state) {
      deleteByPk(main, e);
    }
  }

  public static State selectDefaultStateByCountry(Main main, Country country) {
    if (country != null) {
      return (State) AppService.single(main, State.class, "select * from scm_state where country_id = ? and sort_order = ? limit 1", new Object[]{country.getId(), 1});
    }
    return null;
  }

  public static List<State> selectStateByTerritory(Main main, Territory territory) {
    //,scm_district.state_id,scm_district.district_name,scm_district.created_by,scm_district.modified_by,scm_district.created_at,scm_district.modified_at
    return AppService.list(main, State.class, "select scm_state.id from scm_state scm_state left outer join scm_territory_state scm_territory_stateterritory_id on (scm_territory_stateterritory_id.state_id = scm_state.id)where scm_territory_stateterritory_id.territory_id=?", new Object[]{territory.getId()});
  }

  public static State selectStateByStateCodeAndCountry(Main main, Country countryId, String stateCode) {
    return (State) AppService.single(main, State.class, "select * from scm_state where country_id = ? and state_code = ?", new Object[]{countryId.getId(), stateCode});
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param state
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, State state) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param state
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, State state) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_state where upper(state_name)=? and country_id=?", new Object[]{StringUtil.toUpperCase(state.getStateName()), state.getCountryId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("stateName"));
    }
    if (AppService.exist(main, "select id from scm_state where upper(state_code)=? and country_id=?", new Object[]{StringUtil.toUpperCase(state.getStateCode()), state.getCountryId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("stateCode"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param state
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, State state) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_state where upper(state_name)=? and id !=? and country_id=?", new Object[]{StringUtil.toUpperCase(state.getStateName()), state.getId(), state.getCountryId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("stateName"));
    }
    if (AppService.exist(main, "select id from scm_state where upper(state_code)=? and id !=? and country_id=?", new Object[]{StringUtil.toUpperCase(state.getStateCode()), state.getId(), state.getCountryId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("stateCode"));
    }
  }
}
