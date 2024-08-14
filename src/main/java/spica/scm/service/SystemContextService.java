/*
 * @(#)SystemContextService.java	1.0 Fri Jun 17 17:57:48 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Department;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SystemContext;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * SystemContextService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jun 17 17:57:48 IST 2016
 */
public abstract class SystemContextService {

  /**
   * SystemContext paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSystemContextSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_system_context", SystemContext.class, main);
    sql.main("select scm_system_context.id,scm_system_context.title,scm_system_context.sort_order,scm_system_context.created_by,scm_system_context.modified_by,scm_system_context.created_at,scm_system_context.modified_at from scm_system_context scm_system_context"); //Main query
    sql.count("select count(scm_system_context.id) as total from scm_system_context scm_system_context"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_system_context.title", "scm_system_context.created_by", "scm_system_context.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_system_context.id", "scm_system_context.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_system_context.created_at", "scm_system_context.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SystemContext.
   *
   * @param main
   * @return List of SystemContext
   */
  public static final List<SystemContext> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSystemContextSqlPaged(main));
  }

//  /**
//   * Return list of SystemContext based on condition
//   * @param main
//   * @return List<SystemContext>
//   */
//  public static final List<SystemContext> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSystemContextSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SystemContext by key.
   *
   * @param main
   * @param systemContext
   * @return SystemContext
   */
  public static final SystemContext selectByPk(Main main, SystemContext systemContext) {
    return (SystemContext) AppService.find(main, SystemContext.class, systemContext.getId());
  }

  /**
   * Insert SystemContext.
   *
   * @param main
   * @param systemContext
   */
  public static final void insert(Main main, SystemContext systemContext) {
    insertAble(main, systemContext);  //Validating
    AppService.insert(main, systemContext);

  }

  /**
   * Update SystemContext by key.
   *
   * @param main
   * @param systemContext
   * @return SystemContext
   */
  public static final SystemContext updateByPk(Main main, SystemContext systemContext) {
    updateAble(main, systemContext); //Validating
    return (SystemContext) AppService.update(main, systemContext);
  }

  /**
   * Insert or update SystemContext
   *
   * @param main
   * @param SystemContext
   */
  public static void insertOrUpdate(Main main, SystemContext systemContext) {
    if (systemContext.getId() == null) {
      insert(main, systemContext);
    } else {
      updateByPk(main, systemContext);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param systemContext
   */
  public static void clone(Main main, SystemContext systemContext) {
    systemContext.setId(null); //Set to null for insert
    insert(main, systemContext);
  }

  /**
   * Delete SystemContext.
   *
   * @param main
   * @param systemContext
   */
  public static final void deleteByPk(Main main, SystemContext systemContext) {
    deleteAble(main, systemContext); //Validation
    AppService.deleteSql(main, Department.class, "delete from scm_department scm_department where scm_department.system_context_id=?", new Object[]{systemContext.getId()});
    AppService.delete(main, SystemContext.class, systemContext.getId());
  }

  /**
   * Delete Array of SystemContext.
   *
   * @param main
   * @param systemContext
   */
  public static final void deleteByPkArray(Main main, SystemContext[] systemContext) {
    for (SystemContext e : systemContext) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param systemContext
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, SystemContext systemContext) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param systemContext
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, SystemContext systemContext) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_system_context where upper(title)=?", new Object[]{StringUtil.toUpperCase(systemContext.getTitle())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param systemContext
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, SystemContext systemContext) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_system_context where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(systemContext.getTitle()), systemContext.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }
}
