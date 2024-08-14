/*
 * @(#)ContractCommissionTypeService.java	1.0 Thu Apr 07 11:31:25 IST 2016 
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
import spica.scm.domain.ContractCommissionType;
import wawo.entity.core.UserMessageException;

/**
 * ContractCommissionTypeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:25 IST 2016
 */
public abstract class ContractCommissionTypeService {

  /**
   * ContractCommissionType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getContractCommissionTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_contract_commission_type", ContractCommissionType.class, main);
    sql.main("select scm_contract_commission_type.id,scm_contract_commission_type.title,scm_contract_commission_type.is_percentage,scm_contract_commission_type.is_slab from scm_contract_commission_type scm_contract_commission_type"); //Main query
    sql.count("select count(scm_contract_commission_type.id) from scm_contract_commission_type scm_contract_commission_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_contract_commission_type.title"}); //String search or sort fields
    sql.number(new String[]{"scm_contract_commission_type.id", "scm_contract_commission_type.is_percentage", "scm_contract_commission_type.is_slab"}); //Number search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ContractCommissionType.
   *
   * @param main
   * @return List of ContractCommissionType
   */
  public static final List<ContractCommissionType> listPaged(Main main) {
    return AppService.listPagedJpa(main, getContractCommissionTypeSqlPaged(main));
  }

//  /**
//   * Return list of ContractCommissionType based on condition
//   * @param main
//   * @return List<ContractCommissionType>
//   */
//  public static final List<ContractCommissionType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getContractCommissionTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ContractCommissionType by key.
   *
   * @param main
   * @param contractCommissionType
   * @return ContractCommissionType
   */
  public static final ContractCommissionType selectByPk(Main main, ContractCommissionType contractCommissionType) {
    return (ContractCommissionType) AppService.find(main, ContractCommissionType.class, contractCommissionType.getId());
  }

  /**
   * Insert ContractCommissionType.
   *
   * @param main
   * @param contractCommissionType
   */
  public static final void insert(Main main, ContractCommissionType contractCommissionType) {
    insertAble(main, contractCommissionType);  //Validating
    AppService.insert(main, contractCommissionType);

  }

  /**
   * Update ContractCommissionType by key.
   *
   * @param main
   * @param contractCommissionType
   * @return ContractCommissionType
   */
  public static final ContractCommissionType updateByPk(Main main, ContractCommissionType contractCommissionType) {
    updateAble(main, contractCommissionType); //Validating
    return (ContractCommissionType) AppService.update(main, contractCommissionType);
  }

  /**
   * Insert or update ContractCommissionType
   *
   * @param main
   * @param ContractCommissionType
   */
  public static void insertOrUpdate(Main main, ContractCommissionType contractCommissionType) {
    if (contractCommissionType.getId() == null) {
      insert(main, contractCommissionType);
    } else {
      updateByPk(main, contractCommissionType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param contractCommissionType
   */
  public static void clone(Main main, ContractCommissionType contractCommissionType) {
    contractCommissionType.setId(null); //Set to null for insert
    insert(main, contractCommissionType);
  }

  /**
   * Delete ContractCommissionType.
   *
   * @param main
   * @param contractCommissionType
   */
  public static final void deleteByPk(Main main, ContractCommissionType contractCommissionType) {
    deleteAble(main, contractCommissionType); //Validation
    AppService.delete(main, ContractCommissionType.class, contractCommissionType.getId());
  }

  /**
   * Delete Array of ContractCommissionType.
   *
   * @param main
   * @param contractCommissionType
   */
  public static final void deleteByPkArray(Main main, ContractCommissionType[] contractCommissionType) {
    for (ContractCommissionType e : contractCommissionType) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param contractCommissionType
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, ContractCommissionType contractCommissionType) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param contractCommissionType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, ContractCommissionType contractCommissionType) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param contractCommissionType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, ContractCommissionType contractCommissionType) throws UserMessageException {

  }

}
