/*
 * @(#)DistrictService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.District;
import spica.scm.domain.Territory;
import spica.scm.domain.TerritoryDistrict;
import spica.scm.validate.ValidateUtil;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * DistrictService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class DistrictService {

  /**
   * District paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getDistrictSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_district", District.class, main);
    sql.main("select scm_district.id,scm_district.state_id,scm_district.district_name,scm_district.created_by,scm_district.modified_by,scm_district.created_at,scm_district.modified_at from scm_district scm_district "); //Main query
    sql.count("select count(scm_district.id) as total from scm_district scm_district "); //Count query
    sql.join("left outer join scm_state scm_districtstate_id on (scm_districtstate_id.id = scm_district.state_id)"); //Join Query

    sql.string(new String[]{"scm_districtstate_id.state_name", "scm_district.district_name", "scm_district.created_by", "scm_district.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_district.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_district.created_at", "scm_district.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of District.
   *
   * @param main
   * @return List of District
   */
  public static final List<District> listPaged(Main main) {
    return AppService.listPagedJpa(main, getDistrictSqlPaged(main));
  }

//  /**
//   * Return list of District based on condition
//   * @param main
//   * @return List<District>
//   */
//  public static final List<District> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getDistrictSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select District by key.
   *
   * @param main
   * @param district
   * @return District
   */
  public static final District selectByPk(Main main, District district) {
    return (District) AppService.find(main, District.class, district.getId());
  }

  /**
   * Insert District.
   *
   * @param main
   * @param district
   */
  public static final void insert(Main main, District district) {
    insertAble(main, district);  //Validating
    AppService.insert(main, district);

  }

  /**
   * Update District by key.
   *
   * @param main
   * @param district
   * @return District
   */
  public static final District updateByPk(Main main, District district) {
    updateAble(main, district); //Validating
    return (District) AppService.update(main, district);
  }

  /**
   * Insert or update District
   *
   * @param main
   * @param district
   */
  public static void insertOrUpdate(Main main, District district) {
    if (district.getId() == null) {
      district.setDistrictName(district.getDistrictName().toUpperCase());
      insert(main, district);
    } else {
      district.setDistrictName(district.getDistrictName().toUpperCase());
      updateByPk(main, district);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param district
   */
  public static void clone(Main main, District district) {
    district.setId(null); //Set to null for insert
    insert(main, district);
  }

  /**
   * Delete District.
   *
   * @param main
   * @param district
   */
  public static final void deleteByPk(Main main, District district) {
    deleteAble(main, district); //Validation
    AppService.deleteSql(main, TerritoryDistrict.class, "delete from scm_territory_district where scm_territory_district.district_id=?", new Object[]{district.getId()});
    AppService.delete(main, District.class, district.getId());
  }

  /**
   * Delete Array of District.
   *
   * @param main
   * @param district
   */
  public static final void deleteByPkArray(Main main, District[] district) {
    for (District e : district) {
      deleteByPk(main, e);
    }
  }

  public static List<District> selectByTerritory(Main main, Territory territory) {
    //,scm_district.state_id,scm_district.district_name,scm_district.created_by,scm_district.modified_by,scm_district.created_at,scm_district.modified_at
    return AppService.list(main, District.class, "select scm_district.id from scm_district scm_district left outer join scm_territory_district scm_territory_districtterritory_id on (scm_territory_districtterritory_id.district_id = scm_district.id)where scm_territory_districtterritory_id.territory_id=?", new Object[]{territory.getId()});
  }

  public static District selectByCompanyRegisteredAddress(Main main, Company parent) {
    return (District) AppService.single(main, District.class, "select * from scm_district where id in (select district_id from scm_company_address where company_id = ? and address_type_id = ?);", new Object[]{parent.getId(), AddressTypeService.REGISTERED_ADDRESS});
  }
  /**
   * Validate delete.
   *
   * @param main
   * @param district
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, District district) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param district
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, District district) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_district where upper(district_name)=? and state_id=?", new Object[]{StringUtil.toUpperCase(district.getDistrictName()), district.getStateId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("districtName"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param district
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, District district) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_district where upper(district_name)=? and id !=? and state_id=?", new Object[]{StringUtil.toUpperCase(district.getDistrictName()), district.getId(), district.getStateId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("districtName"));
    }
  }
}
