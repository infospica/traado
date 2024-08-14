/*
 * @(#)ContractBenefitTypeService.java	1.0 Thu Jun 23 15:26:44 IST 2016 
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
import spica.scm.domain.ContractBenefitType;
import wawo.entity.core.UserMessageException;

/**
 * ContractBenefitTypeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 23 15:26:44 IST 2016
 */
public abstract class ContractBenefitTypeService {

  /**
   * ContractBenefitType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getContractBenefitTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_contract_benefit_type", ContractBenefitType.class, main);
    sql.main("select scm_contract_benefit_type.id,scm_contract_benefit_type.title,scm_contract_benefit_type.is_percentage,scm_contract_benefit_type.is_slab,scm_contract_benefit_type.created_by,scm_contract_benefit_type.modified_by,scm_contract_benefit_type.created_at,scm_contract_benefit_type.modified_at from scm_contract_benefit_type scm_contract_benefit_type"); //Main query
    sql.count("select count(scm_contract_benefit_type.id) as total from scm_contract_benefit_type scm_contract_benefit_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_contract_benefit_type.title"}); //String search or sort fields
    sql.number(new String[]{"scm_contract_benefit_type.id", "scm_contract_benefit_type.is_percentage", "scm_contract_benefit_type.is_slab"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ContractBenefitType.
   *
   * @param main
   * @return List of ContractBenefitType
   */
  public static final List<ContractBenefitType> listPaged(Main main) {
    return AppService.listPagedJpa(main, getContractBenefitTypeSqlPaged(main));
  }

//  /**
//   * Return list of ContractBenefitType based on condition
//   * @param main
//   * @return List<ContractBenefitType>
//   */
//  public static final List<ContractBenefitType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getContractBenefitTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ContractBenefitType by key.
   *
   * @param main
   * @param contractBenefitType
   * @return ContractBenefitType
   */
  public static final ContractBenefitType selectByPk(Main main, ContractBenefitType contractBenefitType) {
    return (ContractBenefitType) AppService.find(main, ContractBenefitType.class, contractBenefitType.getId());
  }

  /**
   * Insert ContractBenefitType.
   *
   * @param main
   * @param contractBenefitType
   */
  public static final void insert(Main main, ContractBenefitType contractBenefitType) {
    insertAble(main, contractBenefitType);  //Validating
    AppService.insert(main, contractBenefitType);

  }

  /**
   * Update ContractBenefitType by key.
   *
   * @param main
   * @param contractBenefitType
   * @return ContractBenefitType
   */
  public static final ContractBenefitType updateByPk(Main main, ContractBenefitType contractBenefitType) {
    updateAble(main, contractBenefitType); //Validating
    return (ContractBenefitType) AppService.update(main, contractBenefitType);
  }

  /**
   * Insert or update ContractBenefitType
   *
   * @param main
   * @param ContractBenefitType
   */
  public static void insertOrUpdate(Main main, ContractBenefitType contractBenefitType) {
    if (contractBenefitType.getId() == null) {
      insert(main, contractBenefitType);
    } else {
      updateByPk(main, contractBenefitType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param contractBenefitType
   */
  public static void clone(Main main, ContractBenefitType contractBenefitType) {
    contractBenefitType.setId(null); //Set to null for insert
    insert(main, contractBenefitType);
  }

  /**
   * Delete ContractBenefitType.
   *
   * @param main
   * @param contractBenefitType
   */
  public static final void deleteByPk(Main main, ContractBenefitType contractBenefitType) {
    deleteAble(main, contractBenefitType); //Validation
    AppService.delete(main, ContractBenefitType.class, contractBenefitType.getId());
  }

  /**
   * Delete Array of ContractBenefitType.
   *
   * @param main
   * @param contractBenefitType
   */
  public static final void deleteByPkArray(Main main, ContractBenefitType[] contractBenefitType) {
    for (ContractBenefitType e : contractBenefitType) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param contractBenefitType
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, ContractBenefitType contractBenefitType) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param contractBenefitType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, ContractBenefitType contractBenefitType) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param contractBenefitType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, ContractBenefitType contractBenefitType) throws UserMessageException {

  }
}
