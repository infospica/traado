/*
 * @(#)ContractStatusService.java	1.0 Thu Apr 07 11:31:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ContractStatus;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * ContractStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:25 IST 2016
 */
public abstract class ContractStatusService {

  /**
   * ContractStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getContractStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_contract_status", ContractStatus.class, main);
    sql.main("select scm_contract_status.id,scm_contract_status.title,scm_contract_status.display_color,scm_contract_status.sort_order,scm_contract_status.created_by,scm_contract_status.modified_by,scm_contract_status.created_at,scm_contract_status.modified_at from scm_contract_status scm_contract_status"); //Main query
    sql.count("select count(scm_contract_status.id) as total from scm_contract_status scm_contract_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_contract_status.title", "scm_contract_status.display_color", "scm_contract_status.created_by", "scm_contract_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_contract_status.id", "scm_contract_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_contract_status.created_at", "scm_contract_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ContractStatus.
   *
   * @param main
   * @return List of ContractStatus
   */
  public static final List<ContractStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getContractStatusSqlPaged(main));
  }

//  /**
//   * Return list of ContractStatus based on condition
//   * @param main
//   * @return List<ContractStatus>
//   */
//  public static final List<ContractStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getContractStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ContractStatus by key.
   *
   * @param main
   * @param contractStatus
   * @return ContractStatus
   */
  public static final ContractStatus selectByPk(Main main, ContractStatus contractStatus) {
    return (ContractStatus) AppService.find(main, ContractStatus.class, contractStatus.getId());
  }

  /**
   * Insert ContractStatus.
   *
   * @param main
   * @param contractStatus
   */
  public static final void insert(Main main, ContractStatus contractStatus) {
    insertAble(main, contractStatus);  //Validating
    AppService.insert(main, contractStatus);

  }

  /**
   * Update ContractStatus by key.
   *
   * @param main
   * @param contractStatus
   * @return ContractStatus
   */
  public static final ContractStatus updateByPk(Main main, ContractStatus contractStatus) {
    updateAble(main, contractStatus); //Validating
    return (ContractStatus) AppService.update(main, contractStatus);
  }

  /**
   * Insert or update ContractStatus
   *
   * @param main
   * @param ContractStatus
   */
  public static void insertOrUpdate(Main main, ContractStatus contractStatus) {
    if (contractStatus.getId() == null) {
      insert(main, contractStatus);
    } else {
      updateByPk(main, contractStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param contractStatus
   */
  public static void clone(Main main, ContractStatus contractStatus) {
    contractStatus.setId(null); //Set to null for insert
    insert(main, contractStatus);
  }

  /**
   * Delete ContractStatus.
   *
   * @param main
   * @param contractStatus
   */
  public static final void deleteByPk(Main main, ContractStatus contractStatus) {
    deleteAble(main, contractStatus); //Validation
    AppService.delete(main, ContractStatus.class, contractStatus.getId());
  }

  /**
   * Delete Array of ContractStatus.
   *
   * @param main
   * @param contractStatus
   */
  public static final void deleteByPkArray(Main main, ContractStatus[] contractStatus) {
    for (ContractStatus e : contractStatus) {
      deleteByPk(main, e);
    }
  }
   /**
   * Validate delete.
   *
   * @param main
   * @param contractStatus
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, ContractStatus contractStatus) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param contractStatus
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, ContractStatus contractStatus) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_contract_status where upper(title)=?", new Object[]{StringUtil.toUpperCase(contractStatus.getTitle())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param contractStatus
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, ContractStatus contractStatus) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_contract_status where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(contractStatus.getTitle()), contractStatus.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }
}
