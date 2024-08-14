/*
 * @(#)DepartmentService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
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
import spica.scm.domain.Department;
import spica.scm.domain.Designation;
import spica.scm.validate.ValidateUtil;
import wawo.app.faces.MainView;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * DepartmentService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class DepartmentService {

  /**
   * Department paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getDepartmentSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_department", Department.class, main);
    sql.main("select scm_department.id,scm_department.title,scm_department.sort_order,scm_department.created_by,scm_department.modified_by,scm_department.created_at,scm_department.modified_at,scm_department.system_context_id from scm_department scm_department "); //Main query
    sql.count("select count(scm_department.id) as total from scm_department scm_department "); //Count query
    sql.join("left outer join scm_system_context scm_departmentsystem_context_id on (scm_departmentsystem_context_id.id = scm_department.system_context_id)"); //Join Query

    sql.string(new String[]{"scm_department.title", "scm_department.created_by", "scm_department.modified_by", "scm_departmentsystem_context_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_department.id", "scm_department.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_department.created_at", "scm_department.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Department.
   *
   * @param main
   * @return List of Department
   */
  public static final List<Department> listPaged(Main main) {
    return AppService.listPagedJpa(main, getDepartmentSqlPaged(main));
  }

//  /**
//   * Return list of Department based on condition
//   * @param main
//   * @return List<Department>
//   */
//  public static final List<Department> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getDepartmentSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Department by key.
   *
   * @param main
   * @param department
   * @return Department
   */
  public static final Department selectByPk(Main main, Department department) {
    return (Department) AppService.find(main, Department.class, department.getId());
  }

  /**
   * Insert Department.
   *
   * @param main
   * @param department
   */
  public static final void insert(Main main, Department department) {
    insertAble(main, department);  //Validating
    AppService.insert(main, department);

  }

  /**
   * Update Department by key.
   *
   * @param main
   * @param department
   * @return Department
   */
  public static final Department updateByPk(Main main, Department department) {
    updateAble(main, department); //Validating
    return (Department) AppService.update(main, department);
  }

  /**
   * Insert or update Department
   *
   * @param main
   * @param department
   */
  public static void insertOrUpdate(Main main, Department department) {
    if (department.getId() == null) {
      insert(main, department);
    } else {
      updateByPk(main, department);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param department
   */
  public static void clone(Main main, Department department) {
    department.setId(null); //Set to null for insert
    insert(main, department);
  }

  /**
   * Delete Department.
   *
   * @param main
   * @param department
   */
  public static final void deleteByPk(Main main, Department department) {
    deleteAble(main, department); //Validation
    AppService.deleteSql(main, Designation.class, "delete from scm_designation scm_designation where scm_designation.department_id=?", new Object[]{department.getId()});
    AppService.delete(main, Department.class, department.getId());
  }

  /**
   * Delete Array of Department.
   *
   * @param main
   * @param department
   */
  public static final void deleteByPkArray(Main main, Department[] department) {
    for (Department e : department) {
      deleteByPk(main, e);
    }
  }

  public static final List<Department> selectCompanyDepartment(MainView main, int k) {
    if (k == 2) {
      return AppService.list(main, Department.class, "select id,title from scm_department where system_context_id=? and  id = ?", new Object[]{1, 13});
    }
    return AppService.list(main, Department.class, "select id,title from scm_department where system_context_id=?", new Object[]{1});
  }

  public static final List<Department> selectVendorDepartment(MainView main) {
    return AppService.list(main, Department.class, "select id,title from scm_department where system_context_id=?", new Object[]{2});
  }

  public static final List<Department> selectCustomerDepartment(MainView main) {
    return AppService.list(main, Department.class, "select id,title from scm_department where system_context_id=?", new Object[]{3});
  }

  public static final List<Department> selectTransporterDepartment(MainView main) {
    return AppService.list(main, Department.class, "select id,title from scm_department where system_context_id=?", new Object[]{4});
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param department
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, Department department) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param department
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Department department) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_department where upper(title)=? and system_context_id=?", new Object[]{StringUtil.toUpperCase(department.getTitle()), department.getSystemContextId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param department
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Department department) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_department where upper(title)=? and id !=? and system_context_id=?", new Object[]{StringUtil.toUpperCase(department.getTitle()), department.getId(), department.getSystemContextId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }
}
