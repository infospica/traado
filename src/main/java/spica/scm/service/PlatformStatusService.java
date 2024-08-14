/*
 * @(#)PlatformStatusService.java	1.0 Thu Apr 20 15:04:15 IST 2017 
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
import spica.scm.domain.PlatformStatus;
import spica.scm.validate.PlatformStatusIs;

/**
 * PlatformStatusService
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:15 IST 2017 
 */

public abstract class PlatformStatusService {  
 
 /**
   * PlatformStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPlatformStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_platform_status", PlatformStatus.class, main);
    sql.main("select scm_platform_status.id,scm_platform_status.title,scm_platform_status.sort_order,scm_platform_status.created_by,scm_platform_status.modified_by,scm_platform_status.created_at,scm_platform_status.modified_at from scm_platform_status scm_platform_status"); //Main query
    sql.count("select count(scm_platform_status.id) as total from scm_platform_status scm_platform_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_platform_status.title","scm_platform_status.created_by","scm_platform_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_platform_status.id","scm_platform_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_platform_status.created_at","scm_platform_status.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of PlatformStatus.
  * @param main
  * @return List of PlatformStatus
  */
  public static final List<PlatformStatus> listPaged(Main main) {
     return AppService.listPagedJpa(main, getPlatformStatusSqlPaged(main));
  }

//  /**
//   * Return list of PlatformStatus based on condition
//   * @param main
//   * @return List<PlatformStatus>
//   */
//  public static final List<PlatformStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPlatformStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select PlatformStatus by key.
  * @param main
  * @param platformStatus
  * @return PlatformStatus
  */
  public static final PlatformStatus selectByPk(Main main, PlatformStatus platformStatus) {
    return (PlatformStatus) AppService.find(main, PlatformStatus.class, platformStatus.getId());
  }

 /**
  * Insert PlatformStatus.
  * @param main
  * @param platformStatus
  */
  public static final void insert(Main main, PlatformStatus platformStatus) {
    PlatformStatusIs.insertAble(main, platformStatus);  //Validating
    AppService.insert(main, platformStatus);

  }

 /**
  * Update PlatformStatus by key.
  * @param main
  * @param platformStatus
  * @return PlatformStatus
  */
  public static final PlatformStatus updateByPk(Main main, PlatformStatus platformStatus) {
    PlatformStatusIs.updateAble(main, platformStatus); //Validating
    return (PlatformStatus) AppService.update(main, platformStatus);
  }

  /**
   * Insert or update PlatformStatus
   *
   * @param main
   * @param platformStatus
   */
  public static void insertOrUpdate(Main main, PlatformStatus platformStatus) {
    if (platformStatus.getId() == null) {
      insert(main, platformStatus);
    }
    else{
      updateByPk(main, platformStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param platformStatus
   */
  public static void clone(Main main, PlatformStatus platformStatus) {
    platformStatus.setId(null); //Set to null for insert
    insert(main, platformStatus);
  }

 /**
  * Delete PlatformStatus.
  * @param main
  * @param platformStatus
  */
  public static final void deleteByPk(Main main, PlatformStatus platformStatus) {
    PlatformStatusIs.deleteAble(main, platformStatus); //Validation
    AppService.delete(main, PlatformStatus.class, platformStatus.getId());
  }
	
 /**
  * Delete Array of PlatformStatus.
  * @param main
  * @param platformStatus
  */
  public static final void deleteByPkArray(Main main, PlatformStatus[] platformStatus) {
    for (PlatformStatus e : platformStatus) {
      deleteByPk(main, e);
    }
  }
}
