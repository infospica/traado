/*
 * @(#)TerritoryDistrictService.java	1.0 Wed Jun 22 14:03:12 IST 2016 
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
import spica.scm.domain.TerritoryDistrict;
import spica.scm.validate.TerritoryDistrictIs;

/**
 * TerritoryDistrictService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Jun 22 14:03:12 IST 2016
 */
public abstract class TerritoryDistrictService {

  /**
   * TerritoryDistrict paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTerritoryDistrictSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_territory_district", TerritoryDistrict.class, main);
    sql.main("select scm_territory_district.id,scm_territory_district.territory_id,scm_territory_district.district_id from scm_territory_district scm_territory_district "); //Main query
    sql.count("select count(scm_territory_district.id) as total from scm_territory_district scm_territory_district "); //Count query
    sql.join("left outer join scm_territory scm_territory_districtterritory_id on (scm_territory_districtterritory_id.id = scm_territory_district.territory_id) left outer join scm_district scm_territory_districtdistrict_id on (scm_territory_districtdistrict_id.id = scm_territory_district.district_id)"); //Join Query

    sql.string(new String[]{"scm_territory_districtterritory_id.territory_name", "scm_territory_districtdistrict_id.district_name"}); //String search or sort fields
    sql.number(new String[]{"scm_territory_district.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TerritoryDistrict.
   *
   * @param main
   * @return List of TerritoryDistrict
   */
  public static final List<TerritoryDistrict> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTerritoryDistrictSqlPaged(main));
  }

//  /**
//   * Return list of TerritoryDistrict based on condition
//   * @param main
//   * @return List<TerritoryDistrict>
//   */
//  public static final List<TerritoryDistrict> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTerritoryDistrictSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TerritoryDistrict by key.
   *
   * @param main
   * @param territoryDistrict
   * @return TerritoryDistrict
   */
  public static final TerritoryDistrict selectByPk(Main main, TerritoryDistrict territoryDistrict) {
    return (TerritoryDistrict) AppService.find(main, TerritoryDistrict.class, territoryDistrict.getId());
  }

  /**
   * Insert TerritoryDistrict.
   *
   * @param main
   * @param territoryDistrict
   */
  public static final void insert(Main main, TerritoryDistrict territoryDistrict) {
    TerritoryDistrictIs.insertAble(main, territoryDistrict);  //Validating
    AppService.insert(main, territoryDistrict);

  }

  /**
   * Update TerritoryDistrict by key.
   *
   * @param main
   * @param territoryDistrict
   * @return TerritoryDistrict
   */
  public static final TerritoryDistrict updateByPk(Main main, TerritoryDistrict territoryDistrict) {
    TerritoryDistrictIs.updateAble(main, territoryDistrict); //Validating
    return (TerritoryDistrict) AppService.update(main, territoryDistrict);
  }

  /**
   * Insert or update TerritoryDistrict
   *
   * @param main
   * @param TerritoryDistrict
   */
  public static void insertOrUpdate(Main main, TerritoryDistrict territoryDistrict) {
    if (territoryDistrict.getId() == null) {
      insert(main, territoryDistrict);
    } else {
      updateByPk(main, territoryDistrict);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param territoryDistrict
   */
  public static void clone(Main main, TerritoryDistrict territoryDistrict) {
    territoryDistrict.setId(null); //Set to null for insert
    insert(main, territoryDistrict);
  }

  /**
   * Delete TerritoryDistrict.
   *
   * @param main
   * @param territoryDistrict
   */
  public static final void deleteByPk(Main main, TerritoryDistrict territoryDistrict) {
    TerritoryDistrictIs.deleteAble(main, territoryDistrict); //Validation
    AppService.delete(main, TerritoryDistrict.class, territoryDistrict.getId());
  }

  /**
   * Delete Array of TerritoryDistrict.
   *
   * @param main
   * @param territoryDistrict
   */
  public static final void deleteByPkArray(Main main, TerritoryDistrict[] territoryDistrict) {
    for (TerritoryDistrict e : territoryDistrict) {
      deleteByPk(main, e);
    }
  }
}
