/*
 * @(#)PlatformSettlementItemService.java	1.0 Thu Apr 20 15:04:15 IST 2017 
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
import spica.scm.domain.PlatformSettlementItem;
import spica.scm.validate.PlatformSettlementItemIs;

/**
 * PlatformSettlementItemService
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:15 IST 2017 
 */

public abstract class PlatformSettlementItemService {  
 
 /**
   * PlatformSettlementItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPlatformSettlementItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_platform_settlement_item", PlatformSettlementItem.class, main);
    sql.main("select scm_platform_settlement_item.id,scm_platform_settlement_item.platform_settlement_id,scm_platform_settlement_item.platform_id,scm_platform_settlement_item.created_by,scm_platform_settlement_item.modified_by,scm_platform_settlement_item.created_at,scm_platform_settlement_item.modified_at from scm_platform_settlement_item scm_platform_settlement_item "); //Main query
    sql.count("select count(scm_platform_settlement_item.id) as total from scm_platform_settlement_item scm_platform_settlement_item "); //Count query
    sql.join("left outer join scm_platform_settlement scm_platform_settlement_itemplatform_settlement_id on (scm_platform_settlement_itemplatform_settlement_id.id = scm_platform_settlement_item.platform_settlement_id) left outer join scm_platform scm_platform_settlement_itemplatform_id on (scm_platform_settlement_itemplatform_id.id = scm_platform_settlement_item.platform_id)"); //Join Query

    sql.string(new String[]{"scm_platform_settlement_itemplatform_settlement_id.settlement_note","scm_platform_settlement_item.created_by","scm_platform_settlement_item.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_platform_settlement_item.id","scm_platform_settlement_itemplatform_id.credit_amount_required"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_platform_settlement_item.created_at","scm_platform_settlement_item.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of PlatformSettlementItem.
  * @param main
  * @return List of PlatformSettlementItem
  */
  public static final List<PlatformSettlementItem> listPaged(Main main) {
     return AppService.listPagedJpa(main, getPlatformSettlementItemSqlPaged(main));
  }

//  /**
//   * Return list of PlatformSettlementItem based on condition
//   * @param main
//   * @return List<PlatformSettlementItem>
//   */
//  public static final List<PlatformSettlementItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPlatformSettlementItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select PlatformSettlementItem by key.
  * @param main
  * @param platformSettlementItem
  * @return PlatformSettlementItem
  */
  public static final PlatformSettlementItem selectByPk(Main main, PlatformSettlementItem platformSettlementItem) {
    return (PlatformSettlementItem) AppService.find(main, PlatformSettlementItem.class, platformSettlementItem.getId());
  }

 /**
  * Insert PlatformSettlementItem.
  * @param main
  * @param platformSettlementItem
  */
  public static final void insert(Main main, PlatformSettlementItem platformSettlementItem) {
    PlatformSettlementItemIs.insertAble(main, platformSettlementItem);  //Validating
    AppService.insert(main, platformSettlementItem);

  }

 /**
  * Update PlatformSettlementItem by key.
  * @param main
  * @param platformSettlementItem
  * @return PlatformSettlementItem
  */
  public static final PlatformSettlementItem updateByPk(Main main, PlatformSettlementItem platformSettlementItem) {
    PlatformSettlementItemIs.updateAble(main, platformSettlementItem); //Validating
    return (PlatformSettlementItem) AppService.update(main, platformSettlementItem);
  }

  /**
   * Insert or update PlatformSettlementItem
   *
   * @param main
   * @param platformSettlementItem
   */
  public static void insertOrUpdate(Main main, PlatformSettlementItem platformSettlementItem) {
    if (platformSettlementItem.getId() == null) {
      insert(main, platformSettlementItem);
    }
    else{
      updateByPk(main, platformSettlementItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param platformSettlementItem
   */
  public static void clone(Main main, PlatformSettlementItem platformSettlementItem) {
    platformSettlementItem.setId(null); //Set to null for insert
    insert(main, platformSettlementItem);
  }

 /**
  * Delete PlatformSettlementItem.
  * @param main
  * @param platformSettlementItem
  */
  public static final void deleteByPk(Main main, PlatformSettlementItem platformSettlementItem) {
    PlatformSettlementItemIs.deleteAble(main, platformSettlementItem); //Validation
    AppService.delete(main, PlatformSettlementItem.class, platformSettlementItem.getId());
  }
	
 /**
  * Delete Array of PlatformSettlementItem.
  * @param main
  * @param platformSettlementItem
  */
  public static final void deleteByPkArray(Main main, PlatformSettlementItem[] platformSettlementItem) {
    for (PlatformSettlementItem e : platformSettlementItem) {
      deleteByPk(main, e);
    }
  }
}
