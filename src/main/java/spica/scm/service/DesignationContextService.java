/*
 * @(#)DesignationContextService.java	1.0 Fri Jun 17 17:57:48 IST 2016 
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
import spica.scm.domain.DesignationContext;

/**
 * DesignationContextService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jun 17 17:57:48 IST 2016
 */
public abstract class DesignationContextService {

  /**
   * DesignationContext paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getDesignationContextSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_designation_context", DesignationContext.class, main);
    sql.main("select scm_designation_context.id,scm_designation_context.designation_id,scm_designation_context.system_context_id from scm_designation_context scm_designation_context "); //Main query
    sql.count("select count(scm_designation_context.id) as total from scm_designation_context scm_designation_context "); //Count query
    sql.join("left outer join scm_designation scm_designation_contextdesignation_id on (scm_designation_contextdesignation_id.id = scm_designation_context.designation_id) left outer join scm_system_context scm_designation_contextsystem_context_id on (scm_designation_contextsystem_context_id.id = scm_designation_context.system_context_id)"); //Join Query

    sql.string(new String[]{"scm_designation_contextdesignation_id.title", "scm_designation_contextsystem_context_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_designation_context.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of DesignationContext.
   *
   * @param main
   * @return List of DesignationContext
   */
  public static final List<DesignationContext> listPaged(Main main) {
    return AppService.listPagedJpa(main, getDesignationContextSqlPaged(main));
  }

//  /**
//   * Return list of DesignationContext based on condition
//   * @param main
//   * @return List<DesignationContext>
//   */
//  public static final List<DesignationContext> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getDesignationContextSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select DesignationContext by key.
   *
   * @param main
   * @param designationContext
   * @return DesignationContext
   */
  public static final DesignationContext selectByPk(Main main, DesignationContext designationContext) {
    return (DesignationContext) AppService.find(main, DesignationContext.class, designationContext.getId());
  }

  /**
   * Insert DesignationContext.
   *
   * @param main
   * @param designationContext
   */
  public static final void insert(Main main, DesignationContext designationContext) {
    AppService.insert(main, designationContext);
  }

  /**
   * Update DesignationContext by key.
   *
   * @param main
   * @param designationContext
   * @return DesignationContext
   */
  public static final DesignationContext updateByPk(Main main, DesignationContext designationContext) {
    return (DesignationContext) AppService.update(main, designationContext);
  }

  /**
   * Insert or update DesignationContext
   *
   * @param main
   * @param DesignationContext
   */
  public static void insertOrUpdate(Main main, DesignationContext designationContext) {
    if (designationContext.getId() == null) {
      insert(main, designationContext);
    } else {
      updateByPk(main, designationContext);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param designationContext
   */
  public static void clone(Main main, DesignationContext designationContext) {
    designationContext.setId(null); //Set to null for insert
    insert(main, designationContext);
  }

  /**
   * Delete DesignationContext.
   *
   * @param main
   * @param designationContext
   */
  public static final void deleteByPk(Main main, DesignationContext designationContext) {
    AppService.delete(main, DesignationContext.class, designationContext.getId());
  }

  /**
   * Delete Array of DesignationContext.
   *
   * @param main
   * @param designationContext
   */
  public static final void deleteByPkArray(Main main, DesignationContext[] designationContext) {
    for (DesignationContext e : designationContext) {
      deleteByPk(main, e);
    }
  }
}
