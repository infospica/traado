/*
 * @(#)StatusService.java	1.0 Tue Jun 28 12:38:26 IST 2016 
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
import spica.scm.domain.Status;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * StatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Jun 28 12:38:26 IST 2016
 */
public abstract class StatusService {

  public static final Integer STATUS_ACTIVE = 1;
  public static final Integer STATUS_INACTIVE = 3;

  /**
   * Status paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_status", Status.class, main);
    sql.main("select scm_status.id,scm_status.title,scm_status.sort_order,scm_status.created_by,scm_status.modified_by,scm_status.created_at,scm_status.modified_at from scm_status scm_status"); //Main query
    sql.count("select count(scm_status.id) from scm_status scm_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_status.title", "scm_status.created_by", "scm_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_status.id", "scm_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_status.created_at", "scm_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Status.
   *
   * @param main
   * @return List of Status
   */
  public static final List<Status> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStatusSqlPaged(main));
  }

//  /**
//   * Return list of Status based on condition
//   * @param main
//   * @return List<Status>
//   */
//  public static final List<Status> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Status by key.
   *
   * @param main
   * @param status
   * @return Status
   */
  public static final Status selectByPk(Main main, Status status) {
    return (Status) AppService.find(main, Status.class, status.getId());
  }

  /**
   * Insert Status.
   *
   * @param main
   * @param status
   */
  public static final void insert(Main main, Status status) {
    insertAble(main, status);  //Validating
    AppService.insert(main, status);

  }

  /**
   * Update Status by key.
   *
   * @param main
   * @param status
   * @return Status
   */
  public static final Status updateByPk(Main main, Status status) {
    updateAble(main, status); //Validating
    return (Status) AppService.update(main, status);
  }

  /**
   * Insert or update Status
   *
   * @param main
   * @param Status
   */
  public static void insertOrUpdate(Main main, Status status) {
    if (status.getId() == null) {
      insert(main, status);
    } else {
      updateByPk(main, status);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param status
   */
  public static void clone(Main main, Status status) {
    status.setId(null); //Set to null for insert
    insert(main, status);
  }

  /**
   * Delete Status.
   *
   * @param main
   * @param status
   */
  public static final void deleteByPk(Main main, Status status) {
    deleteAble(main, status); //Validation
    AppService.delete(main, Status.class, status.getId());
  }

  /**
   * Delete Array of Status.
   *
   * @param main
   * @param status
   */
  public static final void deleteByPkArray(Main main, Status[] status) {
    for (Status e : status) {
      deleteByPk(main, e);
    }
  }
  /**
   * Validate delete.
   *
   * @param main
   * @param status
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, Status status) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param status
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Status status) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_status where upper(title)=?", new Object[]{StringUtil.toUpperCase(status.getTitle())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param status
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Status status) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_status where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(status.getTitle()), status.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }
}
