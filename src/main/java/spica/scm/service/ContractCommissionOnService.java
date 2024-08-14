/*
 * @(#)ContractCommissionOnService.java	1.0 Thu Apr 07 11:31:25 IST 2016 
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
import spica.scm.domain.ContractCommissionOn;
import wawo.entity.core.UserMessageException;

/**
 * ContractCommissionOnService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:25 IST 2016
 */
public abstract class ContractCommissionOnService {

  /**
   * ContractCommissionOn paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getContractCommissionOnSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_contract_commission_on", ContractCommissionOn.class, main);
    sql.main("select scm_contract_commission_on.id,scm_contract_commission_on.title,scm_contract_commission_on.created_by,scm_contract_commission_on.modified_by,scm_contract_commission_on.created_at,scm_contract_commission_on.modifed_at from scm_contract_commission_on scm_contract_commission_on"); //Main query
    sql.count("select count(scm_contract_commission_on.id) from scm_contract_commission_on scm_contract_commission_on"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_contract_commission_on.title", "scm_contract_commission_on.created_by", "scm_contract_commission_on.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_contract_commission_on.id"}); //Number search or sort fields
    sql.date(new String[]{"scm_contract_commission_on.created_at", "scm_contract_commission_on.modifed_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ContractCommissionOn.
   *
   * @param main
   * @return List of ContractCommissionOn
   */
  public static final List<ContractCommissionOn> listPaged(Main main) {
    return AppService.listPagedJpa(main, getContractCommissionOnSqlPaged(main));
  }

//  /**
//   * Return list of ContractCommissionOn based on condition
//   * @param main
//   * @return List<ContractCommissionOn>
//   */
//  public static final List<ContractCommissionOn> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getContractCommissionOnSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ContractCommissionOn by key.
   *
   * @param main
   * @param contractCommissionOn
   * @return ContractCommissionOn
   */
  public static final ContractCommissionOn selectByPk(Main main, ContractCommissionOn contractCommissionOn) {
    return (ContractCommissionOn) AppService.find(main, ContractCommissionOn.class, contractCommissionOn.getId());
  }

  /**
   * Insert ContractCommissionOn.
   *
   * @param main
   * @param contractCommissionOn
   */
  public static final void insert(Main main, ContractCommissionOn contractCommissionOn) {
    insertAble(main, contractCommissionOn);  //Validating
    AppService.insert(main, contractCommissionOn);

  }

  /**
   * Update ContractCommissionOn by key.
   *
   * @param main
   * @param contractCommissionOn
   * @return ContractCommissionOn
   */
  public static final ContractCommissionOn updateByPk(Main main, ContractCommissionOn contractCommissionOn) {
    updateAble(main, contractCommissionOn); //Validating
    return (ContractCommissionOn) AppService.update(main, contractCommissionOn);
  }

  /**
   * Insert or update ContractCommissionOn
   *
   * @param main
   * @param ContractCommissionOn
   */
  public static void insertOrUpdate(Main main, ContractCommissionOn contractCommissionOn) {
    if (contractCommissionOn.getId() == null) {
      insert(main, contractCommissionOn);
    } else {
      updateByPk(main, contractCommissionOn);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param contractCommissionOn
   */
  public static void clone(Main main, ContractCommissionOn contractCommissionOn) {
    contractCommissionOn.setId(null); //Set to null for insert
    insert(main, contractCommissionOn);
  }

  /**
   * Delete ContractCommissionOn.
   *
   * @param main
   * @param contractCommissionOn
   */
  public static final void deleteByPk(Main main, ContractCommissionOn contractCommissionOn) {
    deleteAble(main, contractCommissionOn); //Validation
    AppService.delete(main, ContractCommissionOn.class, contractCommissionOn.getId());
  }

  /**
   * Delete Array of ContractCommissionOn.
   *
   * @param main
   * @param contractCommissionOn
   */
  public static final void deleteByPkArray(Main main, ContractCommissionOn[] contractCommissionOn) {
    for (ContractCommissionOn e : contractCommissionOn) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param contractCommissionOn
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, ContractCommissionOn contractCommissionOn) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param contractCommissionOn
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, ContractCommissionOn contractCommissionOn) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param contractCommissionOn
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, ContractCommissionOn contractCommissionOn) throws UserMessageException {

  }

}
