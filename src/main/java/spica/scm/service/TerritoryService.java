/*
 * @(#)TerritoryService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.District;
import spica.scm.domain.State;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.Territory;
import spica.scm.domain.TerritoryDistrict;
import spica.scm.domain.TerritoryState;
import spica.scm.domain.UserProfile;
import spica.scm.validate.ValidateUtil;
import wawo.app.faces.AppLookup;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * TerritoryService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TerritoryService {

  /**
   * Territory paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTerritorySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_territory", Territory.class, main);
    sql.main("select scm_territory.id,scm_territory.territory_name,scm_territory.sort_order,scm_territory.created_by,scm_territory.modified_by,scm_territory.created_at,scm_territory.modified_at,scm_territory.country_id,scm_territory.company_id from scm_territory scm_territory "); //Main query
    sql.count("select count(scm_territory.id) as total from scm_territory scm_territory "); //Count query
    sql.join("left outer join scm_country scm_territorycountry_id on (scm_territorycountry_id.id = scm_territory.country_id)"); //Join Query

    sql.string(new String[]{"scm_territory.territory_name", "scm_territory.created_by", "scm_territory.modified_by", "scm_territorycountry_id.country_name"}); //String search or sort fields
    sql.number(new String[]{"scm_territory.id", "scm_territory.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_territory.created_at", "scm_territory.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Territory.
   *
   * @param main
   * @return List of Territory
   */
  public static final List<Territory> listPaged(Main main, Company company) {
    SqlPage sql = getTerritorySqlPaged(main);
    sql.cond("where scm_territory.company_id = ?");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
    //return AppService.listPagedJpa(main, getTerritorySqlPaged(main));
  }

//  /**
//   * Return list of Territory based on condition
//   * @param main
//   * @return List<Territory>
//   */
//  public static final List<Territory> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTerritorySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Territory by key.
   *
   * @param main
   * @param territory
   * @return Territory
   */
  public static final Territory selectByPk(Main main, Territory territory) {
    return (Territory) AppService.find(main, Territory.class, territory.getId());
  }

  /**
   * Insert Territory.
   *
   * @param main
   * @param territory
   */
  public static final void insert(Main main, Territory territory) {
    insertAble(main, territory);  //Validating
    AppService.insert(main, territory);

  }

  /**
   * Update Territory by key.
   *
   * @param main
   * @param territory
   * @return Territory
   */
  public static final Territory updateByPk(Main main, Territory territory) {
    updateAble(main, territory); //Validating
    return (Territory) AppService.update(main, territory);
  }

  /**
   * Insert or update Territory
   *
   * @param main
   * @param territory
   */
  public static void insertOrUpdate(Main main, Territory territory, List<District> districtSelected, List<State> stateSelected) {

    if (territory.getId() != null) {
      territory.setTerritoryName(territory.getTerritoryName().toUpperCase());
      AppService.deleteSql(main, District.class, "delete from scm_territory_district where territory_id=?", new Object[]{territory.getId()});
      AppService.deleteSql(main, State.class, "delete from scm_territory_state where territory_id=?", new Object[]{territory.getId()});
      updateByPk(main, territory);
    } else {
      territory.setTerritoryName(territory.getTerritoryName().toUpperCase());
      insert(main, territory);
    }
    if (districtSelected != null) {
      insertArray(main, districtSelected.toArray(new District[districtSelected.size()]), territory);   //Inserting all the relation records.
    }
    if (stateSelected != null) {
      insertStateArray(main, stateSelected.toArray(new State[stateSelected.size()]), territory);
    }
  }

  public static void insertArray(Main main, District[] districtSelected, Territory territory) {
    if (districtSelected != null) {
      TerritoryDistrict bg;
      for (District district : districtSelected) {  //Reinserting
        bg = new TerritoryDistrict();
        bg.setTerritoryId(territory);
        bg.setDistrictId(district);
        TerritoryDistrictService.insert(main, bg);
      }
    }
  }

  public static void insertStateArray(Main main, State[] stateSelected, Territory territory) {
    if (stateSelected != null) {
      TerritoryState bg;
      for (State state : stateSelected) {  //Reinserting
        bg = new TerritoryState();
        bg.setTerritoryId(territory);
        bg.setStateId(state);
        TerritoryStateService.insert(main, bg);
      }
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param territory
   */
  public static void clone(Main main, Territory territory, List<District> districtSelected, List<State> stateSelected) {
    territory.setId(null); //Set to null for insert
    insertOrUpdate(main, territory, districtSelected, stateSelected);
  }

  /**
   * Delete Territory.
   *
   * @param main
   * @param territory
   */
  public static final void deleteByPk(Main main, Territory territory) {
    deleteAble(main, territory); //Validation
    AppService.deleteSql(main, TerritoryState.class, "delete from scm_territory_state where scm_territory_state.territory_id=?", new Object[]{territory.getId()});
    AppService.deleteSql(main, TerritoryDistrict.class, "delete from scm_territory_district where scm_territory_district.territory_id=?", new Object[]{territory.getId()});
    AppService.delete(main, Territory.class, territory.getId());
  }

  /**
   * Delete Array of Territory.
   *
   * @param main
   * @param territory
   */
  public static final void deleteByPkArray(Main main, Territory[] territory) {
    for (Territory e : territory) {
      deleteByPk(main, e);
    }
  }

  public static List<District> setDistrictByState(MainView main, Integer stateId) {
    return main.em().list(District.class, "select * from scm_district where scm_district.state_id=?", new Object[]{stateId});
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param territory
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, Territory territory) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param territory
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Territory territory) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_territory where company_id = ? and upper(territory_name)=?", new Object[]{territory.getCompanyId().getId(), StringUtil.toUpperCase(territory.getTerritoryName())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("territoryName"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param territory
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Territory territory) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_territory where company_id = ? and upper(territory_name)=? and id !=?", new Object[]{territory.getCompanyId().getId(), StringUtil.toUpperCase(territory.getTerritoryName()), territory.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("territoryName"));
    }
  }

  public static final List<UserProfile> selectCompanySalesAgentUserProfilebyCustomer(Company company, AccountGroup accountGroupId, Customer customer) {
    return AppLookup.getAutoFilter(UserProfile.class, "select * from scm_user_profile scm_user_profile "
            + "where scm_user_profile.id not in(select scm_sales_agent_contract.user_profile_id from scm_sales_agent_contract scm_sales_agent_contract where scm_sales_agent_contract.company_id = ?) "
            + "and scm_user_profile.company_id = ? and scm_user_profile.vendor_id is null and scm_user_profile.customer_id is null and scm_user_profile.transporter_id is null and designation_id = ? "
            + "and id in(select sales_agent_id from scm_sales_agent_account_group where account_group_id = ?)"
            + "and id in(select sales_agent_id from scm_sales_agent_territory where territory_id in\n"
            + "(select territory_id from scm_territory_district where district_id in(select district_id from scm_customer_address where customer_id = ?)))",
            new Object[]{company.getId(), company.getId(), UserProfileService.DESIGNATION_SALES_AGENT, accountGroupId.getId(), customer.getId()});
  }
}
