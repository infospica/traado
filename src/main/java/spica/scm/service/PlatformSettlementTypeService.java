/*
 * @(#)PlatformSettlementTypeService.java	1.0 Thu Apr 20 15:04:15 IST 2017 
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
import spica.scm.domain.PlatformSettlementType;
import spica.scm.validate.PlatformSettlementTypeIs;

/**
 * PlatformSettlementTypeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:15 IST 2017
 */
public abstract class PlatformSettlementTypeService {

  /**
   * PlatformSettlementType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPlatformSettlementTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_platform_settlement_type", PlatformSettlementType.class, main);
    sql.main("select scm_platform_settlement_type.id,scm_platform_settlement_type.title,scm_platform_settlement_type.sort_order,scm_platform_settlement_type.created_by,scm_platform_settlement_type.modified_by,scm_platform_settlement_type.created_at,scm_platform_settlement_type.modified_at from scm_platform_settlement_type scm_platform_settlement_type"); //Main query
    sql.count("select count(scm_platform_settlement_type.id) as total from scm_platform_settlement_type scm_platform_settlement_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_platform_settlement_type.title", "scm_platform_settlement_type.created_by", "scm_platform_settlement_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_platform_settlement_type.id", "scm_platform_settlement_type.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_platform_settlement_type.created_at", "scm_platform_settlement_type.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PlatformSettlementType.
   *
   * @param main
   * @return List of PlatformSettlementType
   */
  public static final List<PlatformSettlementType> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPlatformSettlementTypeSqlPaged(main));
  }

//  /**
//   * Return list of PlatformSettlementType based on condition
//   * @param main
//   * @return List<PlatformSettlementType>
//   */
//  public static final List<PlatformSettlementType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPlatformSettlementTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PlatformSettlementType by key.
   *
   * @param main
   * @param platformSettlementType
   * @return PlatformSettlementType
   */
  public static final PlatformSettlementType selectByPk(Main main, PlatformSettlementType platformSettlementType) {
    return (PlatformSettlementType) AppService.find(main, PlatformSettlementType.class, platformSettlementType.getId());
  }

  /**
   * Insert PlatformSettlementType.
   *
   * @param main
   * @param platformSettlementType
   */
  public static final void insert(Main main, PlatformSettlementType platformSettlementType) {
    PlatformSettlementTypeIs.insertAble(main, platformSettlementType);  //Validating
    AppService.insert(main, platformSettlementType);

  }

  /**
   * Update PlatformSettlementType by key.
   *
   * @param main
   * @param platformSettlementType
   * @return PlatformSettlementType
   */
  public static final PlatformSettlementType updateByPk(Main main, PlatformSettlementType platformSettlementType) {
    PlatformSettlementTypeIs.updateAble(main, platformSettlementType); //Validating
    return (PlatformSettlementType) AppService.update(main, platformSettlementType);
  }

  /**
   * Insert or update PlatformSettlementType
   *
   * @param main
   * @param platformSettlementType
   */
  public static void insertOrUpdate(Main main, PlatformSettlementType platformSettlementType) {
    if (platformSettlementType.getId() == null) {
      insert(main, platformSettlementType);
    } else {
      updateByPk(main, platformSettlementType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param platformSettlementType
   */
  public static void clone(Main main, PlatformSettlementType platformSettlementType) {
    platformSettlementType.setId(null); //Set to null for insert
    insert(main, platformSettlementType);
  }

  /**
   * Delete PlatformSettlementType.
   *
   * @param main
   * @param platformSettlementType
   */
  public static final void deleteByPk(Main main, PlatformSettlementType platformSettlementType) {
    PlatformSettlementTypeIs.deleteAble(main, platformSettlementType); //Validation
    AppService.delete(main, PlatformSettlementType.class, platformSettlementType.getId());
  }

  /**
   * Delete Array of PlatformSettlementType.
   *
   * @param main
   * @param platformSettlementType
   */
  public static final void deleteByPkArray(Main main, PlatformSettlementType[] platformSettlementType) {
    for (PlatformSettlementType e : platformSettlementType) {
      deleteByPk(main, e);
    }
  }
}
