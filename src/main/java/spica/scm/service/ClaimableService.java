/*
 * @(#)ClaimableService.java	1.0 Thu Apr 07 11:31:25 IST 2016 
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
import spica.scm.domain.Claimable;
import spica.scm.domain.Contract;
import wawo.app.faces.MainView;
import wawo.entity.core.UserMessageException;

/**
 * ClaimableService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:25 IST 2016
 */
public abstract class ClaimableService {

  /**
   * Claimable paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getClaimableSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_claimable", Claimable.class, main);
    sql.main("select scm_claimable.id,scm_claimable.title,scm_claimable.created_by,scm_claimable.modified_by,scm_claimable.created_at,scm_claimable.modified_at from scm_claimable scm_claimable"); //Main query
    sql.count("select count(scm_claimable.id) from scm_claimable scm_claimable"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_claimable.title", "scm_claimable.created_by", "scm_claimable.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_claimable.id"}); //Number search or sort fields
    sql.date(new String[]{"scm_claimable.created_at", "scm_claimable.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Claimable.
   *
   * @param main
   * @return List of Claimable
   */
  public static final List<Claimable> listPaged(Main main) {
    return AppService.listPagedJpa(main, getClaimableSqlPaged(main));
  }

//  /**
//   * Return list of Claimable based on condition
//   * @param main
//   * @return List<Claimable>
//   */
//  public static final List<Claimable> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getClaimableSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Claimable by key.
   *
   * @param main
   * @param claimable
   * @return Claimable
   */
  public static final Claimable selectByPk(Main main, Claimable claimable) {
    return (Claimable) AppService.find(main, Claimable.class, claimable.getId());
  }

  /**
   * Insert Claimable.
   *
   * @param main
   * @param claimable
   */
  public static final void insert(Main main, Claimable claimable) {
    insertAble(main, claimable);  //Validating
    AppService.insert(main, claimable);

  }

  /**
   * Update Claimable by key.
   *
   * @param main
   * @param claimable
   * @return Claimable
   */
  public static final Claimable updateByPk(Main main, Claimable claimable) {
    updateAble(main, claimable); //Validating
    return (Claimable) AppService.update(main, claimable);
  }

  /**
   * Insert or update Claimable
   *
   * @param main
   * @param claimable
   */
  public static void insertOrUpdate(Main main, Claimable claimable) {
    if (claimable.getId() == null) {
      insert(main, claimable);
    } else {
      updateByPk(main, claimable);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param claimable
   */
  public static void clone(Main main, Claimable claimable) {
    claimable.setId(null); //Set to null for insert
    insert(main, claimable);
  }

  /**
   * Delete Claimable.
   *
   * @param main
   * @param claimable
   */
  public static final void deleteByPk(Main main, Claimable claimable) {
    deleteAble(main, claimable); //Validation
    AppService.delete(main, Claimable.class, claimable.getId());
  }

  /**
   * Delete Array of Claimable.
   *
   * @param main
   * @param claimable
   */
  public static final void deleteByPkArray(Main main, Claimable[] claimable) {
    for (Claimable e : claimable) {
      deleteByPk(main, e);
    }
  }

  public static List<Claimable> listPagedNotInContract(Main main, Contract contract) {
    SqlPage sql = getClaimableSqlPaged(main);
    if (contract.getId() == null) {
      return null;
    }
    sql.cond("where scm_claimable.id not in(select scm_contract_claimable.claimable_id from scm_contract_claimable scm_contract_claimable where scm_contract_claimable.contract_id =?)");
    sql.param(contract.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static List<Claimable> selectByContract(MainView main, Contract contract) {
    SqlPage sql = getClaimableSqlPaged(main);
    sql.join("left outer join scm_contract_claimable scm_contract_claimable on (scm_claimable.id = scm_contract_claimable.claimable_id)");
    if (contract.getId() == null) {
      return null;
    }
    sql.cond("where scm_contract_claimable.contract_id = ?");
    sql.param(contract.getId());
    return AppService.listAllJpa(main, sql);
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param claimable
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, Claimable claimable) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param claimable
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Claimable claimable) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param claimable
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Claimable claimable) throws UserMessageException {

  }
}
