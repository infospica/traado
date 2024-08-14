/*
 * @(#)PlatformDescriptionService.java	1.0 Thu Apr 20 15:04:15 IST 2017 
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
import spica.scm.domain.PlatformDescription;
import spica.scm.validate.PlatformDescriptionIs;

/**
 * PlatformDescriptionService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:15 IST 2017
 */
public abstract class PlatformDescriptionService {

  /**
   * PlatformDescription paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPlatformDescriptionSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_platform_description", PlatformDescription.class, main);
    sql.main("select scm_platform_description.id,scm_platform_description.title,scm_platform_description.short_code,scm_platform_description.description,scm_platform_description.sort_order,scm_platform_description.created_by,scm_platform_description.modified_by,scm_platform_description.created_at,scm_platform_description.modified_at,scm_platform_description.display_color from scm_platform_description scm_platform_description"); //Main query
    sql.count("select count(scm_platform_description.id) as total from scm_platform_description scm_platform_description"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_platform_description.title", "scm_platform_description.short_code", "scm_platform_description.description", "scm_platform_description.created_by", "scm_platform_description.modified_by", "scm_platform_description.display_color"}); //String search or sort fields
    sql.number(new String[]{"scm_platform_description.id", "scm_platform_description.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_platform_description.created_at", "scm_platform_description.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PlatformDescription.
   *
   * @param main
   * @return List of PlatformDescription
   */
  public static final List<PlatformDescription> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPlatformDescriptionSqlPaged(main));
  }

//  /**
//   * Return list of PlatformDescription based on condition
//   * @param main
//   * @return List<PlatformDescription>
//   */
//  public static final List<PlatformDescription> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPlatformDescriptionSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PlatformDescription by key.
   *
   * @param main
   * @param platformDescription
   * @return PlatformDescription
   */
  public static final PlatformDescription selectByPk(Main main, PlatformDescription platformDescription) {
    return (PlatformDescription) AppService.find(main, PlatformDescription.class, platformDescription.getId());
  }

  /**
   * Insert PlatformDescription.
   *
   * @param main
   * @param platformDescription
   */
  public static final void insert(Main main, PlatformDescription platformDescription) {
    PlatformDescriptionIs.insertAble(main, platformDescription);  //Validating
    AppService.insert(main, platformDescription);

  }

  /**
   * Update PlatformDescription by key.
   *
   * @param main
   * @param platformDescription
   * @return PlatformDescription
   */
  public static final PlatformDescription updateByPk(Main main, PlatformDescription platformDescription) {
    PlatformDescriptionIs.updateAble(main, platformDescription); //Validating
    return (PlatformDescription) AppService.update(main, platformDescription);
  }

  /**
   * Insert or update PlatformDescription
   *
   * @param main
   * @param platformDescription
   */
  public static void insertOrUpdate(Main main, PlatformDescription platformDescription) {
    if (platformDescription.getId() == null) {
      insert(main, platformDescription);
    } else {
      updateByPk(main, platformDescription);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param platformDescription
   */
  public static void clone(Main main, PlatformDescription platformDescription) {
    platformDescription.setId(null); //Set to null for insert
    insert(main, platformDescription);
  }

  /**
   * Delete PlatformDescription.
   *
   * @param main
   * @param platformDescription
   */
  public static final void deleteByPk(Main main, PlatformDescription platformDescription) {
    PlatformDescriptionIs.deleteAble(main, platformDescription); //Validation
    AppService.delete(main, PlatformDescription.class, platformDescription.getId());
  }

  /**
   * Delete Array of PlatformDescription.
   *
   * @param main
   * @param platformDescription
   */
  public static final void deleteByPkArray(Main main, PlatformDescription[] platformDescription) {
    for (PlatformDescription e : platformDescription) {
      deleteByPk(main, e);
    }
  }
}
