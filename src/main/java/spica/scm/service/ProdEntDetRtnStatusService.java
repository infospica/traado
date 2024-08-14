/*
 * @(#)ProdEntDetRtnStatusService.java	1.0 Thu Sep 08 18:33:22 IST 2016 
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
import spica.scm.domain.ProdEntDetRtnStatus;
import spica.scm.validate.ProdEntDetRtnStatusIs;

/**
 * ProdEntDetRtnStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Sep 08 18:33:22 IST 2016
 */
public abstract class ProdEntDetRtnStatusService {

  /**
   * ProdEntDetRtnStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProdEntDetRtnStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_prod_ent_det_rtn_status", ProdEntDetRtnStatus.class, main);
    sql.main("select scm_prod_ent_det_rtn_status.id,scm_prod_ent_det_rtn_status.title,scm_prod_ent_det_rtn_status.display_color,scm_prod_ent_det_rtn_status.sort_order,scm_prod_ent_det_rtn_status.created_by,scm_prod_ent_det_rtn_status.modified_by,scm_prod_ent_det_rtn_status.created_at,scm_prod_ent_det_rtn_status.modified_at from scm_prod_ent_det_rtn_status scm_prod_ent_det_rtn_status"); //Main query
    sql.count("select count(scm_prod_ent_det_rtn_status.id) as total from scm_prod_ent_det_rtn_status scm_prod_ent_det_rtn_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_prod_ent_det_rtn_status.title", "scm_prod_ent_det_rtn_status.display_color", "scm_prod_ent_det_rtn_status.created_by", "scm_prod_ent_det_rtn_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_prod_ent_det_rtn_status.id", "scm_prod_ent_det_rtn_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_prod_ent_det_rtn_status.created_at", "scm_prod_ent_det_rtn_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProdEntDetRtnStatus.
   *
   * @param main
   * @return List of ProdEntDetRtnStatus
   */
  public static final List<ProdEntDetRtnStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProdEntDetRtnStatusSqlPaged(main));
  }

//  /**
//   * Return list of ProdEntDetRtnStatus based on condition
//   * @param main
//   * @return List<ProdEntDetRtnStatus>
//   */
//  public static final List<ProdEntDetRtnStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProdEntDetRtnStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProdEntDetRtnStatus by key.
   *
   * @param main
   * @param prodEntDetRtnStatus
   * @return ProdEntDetRtnStatus
   */
  public static final ProdEntDetRtnStatus selectByPk(Main main, ProdEntDetRtnStatus prodEntDetRtnStatus) {
    return (ProdEntDetRtnStatus) AppService.find(main, ProdEntDetRtnStatus.class, prodEntDetRtnStatus.getId());
  }

  /**
   * Insert ProdEntDetRtnStatus.
   *
   * @param main
   * @param prodEntDetRtnStatus
   */
  public static final void insert(Main main, ProdEntDetRtnStatus prodEntDetRtnStatus) {
    ProdEntDetRtnStatusIs.insertAble(main, prodEntDetRtnStatus);  //Validating
    AppService.insert(main, prodEntDetRtnStatus);

  }

  /**
   * Update ProdEntDetRtnStatus by key.
   *
   * @param main
   * @param prodEntDetRtnStatus
   * @return ProdEntDetRtnStatus
   */
  public static final ProdEntDetRtnStatus updateByPk(Main main, ProdEntDetRtnStatus prodEntDetRtnStatus) {
    ProdEntDetRtnStatusIs.updateAble(main, prodEntDetRtnStatus); //Validating
    return (ProdEntDetRtnStatus) AppService.update(main, prodEntDetRtnStatus);
  }

  /**
   * Insert or update ProdEntDetRtnStatus
   *
   * @param main
   * @param prodEntDetRtnStatus
   */
  public static void insertOrUpdate(Main main, ProdEntDetRtnStatus prodEntDetRtnStatus) {
    if (prodEntDetRtnStatus.getId() == null) {
      insert(main, prodEntDetRtnStatus);
    } else {
      updateByPk(main, prodEntDetRtnStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param prodEntDetRtnStatus
   */
  public static void clone(Main main, ProdEntDetRtnStatus prodEntDetRtnStatus) {
    prodEntDetRtnStatus.setId(null); //Set to null for insert
    insert(main, prodEntDetRtnStatus);
  }

  /**
   * Delete ProdEntDetRtnStatus.
   *
   * @param main
   * @param prodEntDetRtnStatus
   */
  public static final void deleteByPk(Main main, ProdEntDetRtnStatus prodEntDetRtnStatus) {
    ProdEntDetRtnStatusIs.deleteAble(main, prodEntDetRtnStatus); //Validation
    AppService.delete(main, ProdEntDetRtnStatus.class, prodEntDetRtnStatus.getId());
  }

  /**
   * Delete Array of ProdEntDetRtnStatus.
   *
   * @param main
   * @param prodEntDetRtnStatus
   */
  public static final void deleteByPkArray(Main main, ProdEntDetRtnStatus[] prodEntDetRtnStatus) {
    for (ProdEntDetRtnStatus e : prodEntDetRtnStatus) {
      deleteByPk(main, e);
    }
  }
}
