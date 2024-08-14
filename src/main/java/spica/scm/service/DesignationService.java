/*
 * @(#)DesignationService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
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
import spica.scm.domain.Designation;
import spica.scm.validate.ValidateUtil;
import wawo.app.faces.MainView;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * DesignationService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class DesignationService {

  public static final int DESIGNATION_LEVEL = 1;
  public static final int COMPANY_SYSTEM_CONTEXT = 1;
  public static final int VENDOR_SYSTEM_CONTEXT = 2;
  public static final int CUSTOMER_SYSTEM_CONTEXT = 3;
  public static final int TRANSPORTER_SYSTEM_CONTEXT = 4;
  public static final Integer VENDOR_SALES_AGENT = 15;

  /**
   * Designation paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getDesignationSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_designation", Designation.class, main);
    sql.main("select scm_designation.id,scm_designation.title,scm_designation.department_id,scm_designation.sort_order,scm_designation.created_by,scm_designation.modified_by,scm_designation.created_at,scm_designation.modified_at,scm_designation.designation_level from scm_designation scm_designation "); //Main query
    sql.count("select count(scm_designation.id) as total from scm_designation scm_designation "); //Count query
    sql.join("left outer join scm_department scm_designationdepartment_id on (scm_designationdepartment_id.id = scm_designation.department_id)"); //Join Query

    sql.string(new String[]{"scm_designation.title", "scm_designationdepartment_id.title", "scm_designation.created_by", "scm_designation.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_designation.id", "scm_designation.sort_order", "scm_designation.designation_level"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_designation.created_at", "scm_designation.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Designation.
   *
   * @param main
   * @return List of Designation
   */
  public static final List<Designation> listPaged(Main main) {
    return AppService.listPagedJpa(main, getDesignationSqlPaged(main));
  }

//  /**
//   * Return list of Designation based on condition
//   * @param main
//   * @return List<Designation>
//   */
//  public static final List<Designation> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getDesignationSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Designation by key.
   *
   * @param main
   * @param designation
   * @return Designation
   */
  public static final Designation selectByPk(Main main, Designation designation) {
    return (Designation) AppService.find(main, Designation.class, designation.getId());
  }

  /**
   * Insert Designation.
   *
   * @param main
   * @param designation
   */
  public static final void insert(Main main, Designation designation) {
    insertAble(main, designation);  //Validating
    AppService.insert(main, designation);

  }

  /**
   * Update Designation by key.
   *
   * @param main
   * @param designation
   * @return Designation
   */
  public static final Designation updateByPk(Main main, Designation designation) {
    updateAble(main, designation); //Validating
    return (Designation) AppService.update(main, designation);
  }

  /**
   * Insert or update Designation
   *
   * @param main
   * @param Designation
   */
  public static void insertOrUpdate(Main main, Designation designation) {
    if (designation.getId() == null) {
      insert(main, designation);
    } else {
      updateByPk(main, designation);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param designation
   */
  public static void clone(Main main, Designation designation) {
    designation.setId(null); //Set to null for insert
    insert(main, designation);
  }

  /**
   * Delete Designation.
   *
   * @param main
   * @param designation
   */
  public static final void deleteByPk(Main main, Designation designation) {
    deleteAble(main, designation); //Validation
    AppService.delete(main, Designation.class, designation.getId());
  }

  /**
   * Delete Array of Designation.
   *
   * @param main
   * @param designation
   */
  public static final void deleteByPkArray(Main main, Designation[] designation) {
    for (Designation e : designation) {
      deleteByPk(main, e);
    }
  }

  public static final List<Designation> selectDesignationByDepartment(MainView main, int id) {
    return AppService.list(main, Designation.class, "select id,title from scm_designation where department_id=?", new Object[]{id});
  }

  public static final List<Designation> selectDesignationByLevel(MainView main, int id) {
    return AppService.list(main, Designation.class, "select id,title from scm_designation where department_id=? and designation_level=?", new Object[]{id, DESIGNATION_LEVEL});
  }

  public static final List<Designation> selectDesignationByCompanyContext(MainView main) {
    return AppService.list(main, Designation.class, "select scm_designation.id ,scm_designation.title ||' / '|| scm_department.title as title from scm_designation left outer join scm_department on(scm_department.id=scm_designation.department_id) where scm_department.system_context_id=?", new Object[]{COMPANY_SYSTEM_CONTEXT});
  }

  public static final List<Designation> selectDesignationByVendorContext(MainView main) {
    return AppService.list(main, Designation.class, "select scm_designation.id ,scm_designation.title ||' / '|| scm_department.title as title from scm_designation left outer join scm_department on(scm_department.id=scm_designation.department_id) where scm_department.system_context_id=?", new Object[]{VENDOR_SYSTEM_CONTEXT});
  }

  public static final List<Designation> selectDesignationByCustomerContext(MainView main) {
    return AppService.list(main, Designation.class, "select scm_designation.id ,scm_designation.title ||' / '|| scm_department.title as title from scm_designation left outer join scm_department on(scm_department.id=scm_designation.department_id) where scm_department.system_context_id=?", new Object[]{CUSTOMER_SYSTEM_CONTEXT});
  }

  public static final List<Designation> selectDesignationByTransporterContext(MainView main) {
    return AppService.list(main, Designation.class, "select scm_designation.id ,scm_designation.title ||' / '|| scm_department.title as title from scm_designation left outer join scm_department on(scm_department.id=scm_designation.department_id) where scm_department.system_context_id=?", new Object[]{TRANSPORTER_SYSTEM_CONTEXT});
  }

  public static final List<Designation> selectDesignationByCompanyContextInvestor(MainView main) {
    return AppService.list(main, Designation.class, "select scm_designation.id ,scm_designation.title ||' / '|| scm_department.title as title from scm_designation left outer join scm_department on(scm_department.id=scm_designation.department_id) where scm_department.system_context_id=? and scm_designation.designation_level=?", new Object[]{COMPANY_SYSTEM_CONTEXT, DESIGNATION_LEVEL});
  }

  public static final List<Designation> selectDesignationSalesAgent(MainView main, int id) {
    return AppService.list(main, Designation.class, "select id,title from scm_designation where id=?", new Object[]{14});
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param designation
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, Designation designation) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param designation
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Designation designation) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_designation where upper(title)=? and department_id=?", new Object[]{StringUtil.toUpperCase(designation.getTitle()), designation.getDepartmentId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param designation
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Designation designation) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_designation where upper(title)=? and id !=? and department_id=?", new Object[]{StringUtil.toUpperCase(designation.getTitle()), designation.getId(), designation.getDepartmentId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

}
