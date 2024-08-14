/*
 * @(#)PeriodService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
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
import spica.scm.domain.Period;
import wawo.entity.core.UserMessageException;

/**
 * PeriodService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class PeriodService {

  /**
   * Period paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPeriodSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_period", Period.class, main);
    sql.main("select scm_period.id,scm_period.title,scm_period.days,scm_period.sort_order,scm_period.created_by,scm_period.modified_by,scm_period.created_at,scm_period.modified_at from scm_period scm_period"); //Main query
    sql.count("select count(scm_period.id) as total from scm_period scm_period"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_period.title", "scm_period.created_by", "scm_period.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_period.id", "scm_period.days", "scm_period.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_period.created_at", "scm_period.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Period.
   *
   * @param main
   * @return List of Period
   */
  public static final List<Period> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPeriodSqlPaged(main));
  }

//  /**
//   * Return list of Period based on condition
//   * @param main
//   * @return List<Period>
//   */
//  public static final List<Period> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPeriodSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Period by key.
   *
   * @param main
   * @param period
   * @return Period
   */
  public static final Period selectByPk(Main main, Period period) {
    return (Period) AppService.find(main, Period.class, period.getId());
  }

  /**
   * Insert Period.
   *
   * @param main
   * @param period
   */
  public static final void insert(Main main, Period period) {
    insertAble(main, period);  //Validating
    AppService.insert(main, period);

  }

  /**
   * Update Period by key.
   *
   * @param main
   * @param period
   * @return Period
   */
  public static final Period updateByPk(Main main, Period period) {
    updateAble(main, period); //Validating
    return (Period) AppService.update(main, period);
  }

  /**
   * Insert or update Period
   *
   * @param main
   * @param period
   */
  public static void insertOrUpdate(Main main, Period period) {
    if (period.getId() == null) {
      insert(main, period);
    } else {
      updateByPk(main, period);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param period
   */
  public static void clone(Main main, Period period) {
    period.setId(null); //Set to null for insert
    insert(main, period);
  }

  /**
   * Delete Period.
   *
   * @param main
   * @param period
   */
  public static final void deleteByPk(Main main, Period period) {
    deleteAble(main, period); //Validation
    AppService.delete(main, Period.class, period.getId());
  }

  /**
   * Delete Array of Period.
   *
   * @param main
   * @param period
   */
  public static final void deleteByPkArray(Main main, Period[] period) {
    for (Period e : period) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param period
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, Period period) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param period
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Period period) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_period where upper(title)=?", new Object[]{StringUtil.toUpperCase(period.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param period
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Period period) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_period where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(period.getTitle()), period.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
