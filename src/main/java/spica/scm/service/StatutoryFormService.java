/*
 * @(#)StatutoryFormService.java	1.0 Thu Jun 16 11:34:18 IST 2016 
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
import spica.scm.domain.StatutoryForm;
import wawo.entity.core.UserMessageException;

/**
 * StatutoryFormService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 16 11:34:18 IST 2016
 */
public abstract class StatutoryFormService {

  /**
   * StatutoryForm paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStatutoryFormSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_statutory_form", StatutoryForm.class, main);
    sql.main("select scm_statutory_form.id,scm_statutory_form.title,scm_statutory_form.created_by,scm_statutory_form.modified_by,scm_statutory_form.created_at,scm_statutory_form.modified_at from scm_statutory_form scm_statutory_form"); //Main query
    sql.count("select count(scm_statutory_form.id) from scm_statutory_form scm_statutory_form"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_statutory_form.title", "scm_statutory_form.created_by", "scm_statutory_form.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_statutory_form.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_statutory_form.created_at", "scm_statutory_form.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of StatutoryForm.
   *
   * @param main
   * @return List of StatutoryForm
   */
  public static final List<StatutoryForm> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStatutoryFormSqlPaged(main));
  }

//  /**
//   * Return list of StatutoryForm based on condition
//   * @param main
//   * @return List<StatutoryForm>
//   */
//  public static final List<StatutoryForm> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getStatutoryFormSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select StatutoryForm by key.
   *
   * @param main
   * @param statutoryForm
   * @return StatutoryForm
   */
  public static final StatutoryForm selectByPk(Main main, StatutoryForm statutoryForm) {
    return (StatutoryForm) AppService.find(main, StatutoryForm.class, statutoryForm.getId());
  }

  /**
   * Insert StatutoryForm.
   *
   * @param main
   * @param statutoryForm
   */
  public static final void insert(Main main, StatutoryForm statutoryForm) {
    insertAble(main, statutoryForm);  //Validating
    AppService.insert(main, statutoryForm);

  }

  /**
   * Update StatutoryForm by key.
   *
   * @param main
   * @param statutoryForm
   * @return StatutoryForm
   */
  public static final StatutoryForm updateByPk(Main main, StatutoryForm statutoryForm) {
    updateAble(main, statutoryForm); //Validating
    return (StatutoryForm) AppService.update(main, statutoryForm);
  }

  /**
   * Insert or update StatutoryForm
   *
   * @param main
   * @param StatutoryForm
   */
  public static void insertOrUpdate(Main main, StatutoryForm statutoryForm) {
    if (statutoryForm.getId() == null) {
      insert(main, statutoryForm);
    } else {
      updateByPk(main, statutoryForm);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param statutoryForm
   */
  public static void clone(Main main, StatutoryForm statutoryForm) {
    statutoryForm.setId(null); //Set to null for insert
    insert(main, statutoryForm);
  }

  /**
   * Delete StatutoryForm.
   *
   * @param main
   * @param statutoryForm
   */
  public static final void deleteByPk(Main main, StatutoryForm statutoryForm) {
    deleteAble(main, statutoryForm); //Validation
    AppService.delete(main, StatutoryForm.class, statutoryForm.getId());
  }

  /**
   * Delete Array of StatutoryForm.
   *
   * @param main
   * @param statutoryForm
   */
  public static final void deleteByPkArray(Main main, StatutoryForm[] statutoryForm) {
    for (StatutoryForm e : statutoryForm) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param statutoryForm
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, StatutoryForm statutoryForm) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param statutoryForm
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, StatutoryForm statutoryForm) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param statutoryForm
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, StatutoryForm statutoryForm) throws UserMessageException {

  }
}
