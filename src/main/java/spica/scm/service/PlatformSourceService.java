/*
 * @(#)PlatformSourceService.java	1.0 Thu Apr 20 15:04:15 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.scm.service;

import java.util.List;
import spica.scm.domain.PlatformSource;
import spica.scm.validate.PlatformSourceIs;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;

/**
 * PlatformSourceService
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:15 IST 2017 
 */

public abstract class PlatformSourceService {  
 
 /**
   * PlatformSource paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPlatformSourceSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_platform_source", PlatformSource.class, main);
    sql.main("select scm_platform_source.id,scm_platform_source.title,scm_platform_source.short_code,scm_platform_source.display_color,scm_platform_source.sort_order,scm_platform_source.created_by,scm_platform_source.modified_by,scm_platform_source.created_at,scm_platform_source.modified_at from scm_platform_source scm_platform_source"); //Main query
    sql.count("select count(scm_platform_source.id) as total from scm_platform_source scm_platform_source"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_platform_source.title","scm_platform_source.created_by","scm_platform_source.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_platform_source.id","scm_platform_source.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_platform_source.created_at","scm_platform_source.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of PlatformSource.
  * @param main
  * @return List of PlatformSource
  */
  public static final List<PlatformSource> listPaged(Main main) {
     return AppService.listPagedJpa(main, getPlatformSourceSqlPaged(main));
  }

//  /**
//   * Return list of PlatformSource based on condition
//   * @param main
//   * @return List<PlatformSource>
//   */
//  public static final List<PlatformSource> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPlatformSourceSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select PlatformSource by key.
  * @param main
  * @param platformSource
  * @return PlatformSource
  */
  public static final PlatformSource selectByPk(Main main, PlatformSource platformSource) {
    return (PlatformSource) AppService.find(main, PlatformSource.class, platformSource.getId());
  }

 /**
  * Insert PlatformSource.
  * @param main
  * @param platformSource
  */
  public static final void insert(Main main, PlatformSource platformSource) {
    PlatformSourceIs.insertAble(main, platformSource);  //Validating
    AppService.insert(main, platformSource);

  }

 /**
  * Update PlatformSource by key.
  * @param main
  * @param platformSource
  * @return PlatformSource
  */
  public static final PlatformSource updateByPk(Main main, PlatformSource platformSource) {
    PlatformSourceIs.updateAble(main, platformSource); //Validating
    return (PlatformSource) AppService.update(main, platformSource);
  }

  /**
   * Insert or update PlatformSource
   *
   * @param main
   * @param platformSource
   */
  public static void insertOrUpdate(Main main, PlatformSource platformSource) {
    if (platformSource.getId() == null) {
      insert(main, platformSource);
    }
    else{
      updateByPk(main, platformSource);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param platformSource
   */
  public static void clone(Main main, PlatformSource platformSource) {
    platformSource.setId(null); //Set to null for insert
    insert(main, platformSource);
  }

 /**
  * Delete PlatformSource.
  * @param main
  * @param platformSource
  */
  public static final void deleteByPk(Main main, PlatformSource platformSource) {
    PlatformSourceIs.deleteAble(main, platformSource); //Validation
    AppService.delete(main, PlatformSource.class, platformSource.getId());
  }
	
 /**
  * Delete Array of PlatformSource.
  * @param main
  * @param platformSource
  */
  public static final void deleteByPkArray(Main main, PlatformSource[] platformSource) {
    for (PlatformSource e : platformSource) {
      deleteByPk(main, e);
    }
  }
}
