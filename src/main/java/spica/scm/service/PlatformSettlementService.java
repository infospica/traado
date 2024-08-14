/*
 * @(#)PlatformSettlementService.java	1.0 Thu Apr 20 15:04:15 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Platform;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.PlatformSettlement;
import spica.scm.domain.PlatformSettlementItem;
import spica.scm.validate.PlatformSettlementIs;
import spica.sys.SystemRuntimeConfig;

/**
 * PlatformSettlementService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:15 IST 2017
 */
public abstract class PlatformSettlementService {

  /**
   * PlatformSettlement paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPlatformSettlementSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_platform_settlement", PlatformSettlement.class, main);
    sql.main("select scm_platform_settlement.id,scm_platform_settlement.settlement_note,scm_platform_settlement.settlement_doc,scm_platform_settlement.settlement_type_id,scm_platform_settlement.status,scm_platform_settlement.created_by,scm_platform_settlement.modified_by,scm_platform_settlement.created_at,scm_platform_settlement.modified_at from scm_platform_settlement scm_platform_settlement "); //Main query
    sql.count("select count(scm_platform_settlement.id) as total from scm_platform_settlement scm_platform_settlement "); //Count query
    sql.join("left outer join scm_platform_settlement_type scm_platform_settlementsettlement_type_id on (scm_platform_settlementsettlement_type_id.id = scm_platform_settlement.settlement_type_id)"); //Join Query

    sql.string(new String[]{"scm_platform_settlement.settlement_note", "scm_platform_settlement.settlement_doc", "scm_platform_settlementsettlement_type_id.title", "scm_platform_settlement.created_by", "scm_platform_settlement.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_platform_settlement.id", "scm_platform_settlement.status"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_platform_settlement.created_at", "scm_platform_settlement.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PlatformSettlement.
   *
   * @param main
   * @return List of PlatformSettlement
   */
  public static final List<PlatformSettlement> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPlatformSettlementSqlPaged(main));
  }

  /**
   * Select PlatformSettlement by key.
   *
   * @param main
   * @param platformSettlement
   * @return PlatformSettlement
   */
  public static final PlatformSettlement selectByPk(Main main, PlatformSettlement platformSettlement) {
    PlatformSettlement ps = (PlatformSettlement) AppService.find(main, PlatformSettlement.class, platformSettlement.getId());
    ps.setPlatformSettlementIdPlatformSettlementItem(AppService.list(main, PlatformSettlementItem.class, "select * from scm_platform_settlement_item where platform_settlement_id=?", new Object[]{platformSettlement.getId()}));
    return ps;
  }

  /**
   * Insert PlatformSettlement.
   *
   * @param main
   * @param platformSettlement
   */
  public static final void insert(Main main, PlatformSettlement platformSettlement) {
    PlatformSettlementIs.insertAble(main, platformSettlement);  //Validating
    AppService.insert(main, platformSettlement);
    for (PlatformSettlementItem item : platformSettlement.getPlatformSettlementIdPlatformSettlementItem()) {
      PlatformSettlementItemService.insert(main, item);
    }

  }

  /**
   * Update PlatformSettlement by key.
   *
   * @param main
   * @param platformSettlement
   * @return PlatformSettlement
   */
  public static final PlatformSettlement updateByPk(Main main, PlatformSettlement platformSettlement) {
    PlatformSettlementIs.updateAble(main, platformSettlement); //Validating
    for (PlatformSettlementItem item : platformSettlement.getPlatformSettlementIdPlatformSettlementItem()) {
      PlatformSettlementItemService.updateByPk(main, item);
    }
    return (PlatformSettlement) AppService.update(main, platformSettlement);
  }

  /**
   * Insert or update PlatformSettlement
   *
   * @param main
   * @param platformSettlement
   */
  public static void insertOrUpdate(Main main, PlatformSettlement platformSettlement) {
    if (platformSettlement.getId() == null) {
      insert(main, platformSettlement);
    } else {
      updateByPk(main, platformSettlement);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param platformSettlement
   */
  public static void clone(Main main, PlatformSettlement platformSettlement) {
    platformSettlement.setId(null); //Set to null for insert
    insert(main, platformSettlement);
  }

  /**
   * Delete PlatformSettlement.
   *
   * @param main
   * @param platformSettlement
   */
  public static final void deleteByPk(Main main, PlatformSettlement platformSettlement) {
    PlatformSettlementIs.deleteAble(main, platformSettlement); //Validation
    AppService.delete(main, PlatformSettlement.class, platformSettlement.getId());
    main.clear();
    main.param(SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId());
    main.param(platformSettlement.getId());
    AppService.updateSql(main, Platform.class, "update scm_platform set status_id=? where id in(select platform_id from scm_platform_settlement_item where platform_settlement_id=?)", false);
    main.clear();
    AppService.deleteSql(main, Platform.class, "delete from scm_platform where parent_id in(select id from scm_platform where id in(select platform_id from scm_platform_settlement_item where platform_settlement_id=?))", new Object[]{platformSettlement.getId()});
    AppService.deleteSql(main, PlatformSettlementItem.class, "delete from scm_platform_settlement_item where platform_settlement_id=?", new Object[]{platformSettlement.getId()});
  }

  /**
   * Delete Array of PlatformSettlement.
   *
   * @param main
   * @param platformSettlement
   */
  public static final void deleteByPkArray(Main main, PlatformSettlement[] platformSettlement) {
    for (PlatformSettlement e : platformSettlement) {
      deleteByPk(main, e);
    }
  }
}
