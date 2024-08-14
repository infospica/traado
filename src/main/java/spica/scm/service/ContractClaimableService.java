/*
 * @(#)ContractClaimableService.java	1.0 Wed Apr 13 15:41:17 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Claimable;
import spica.scm.domain.Contract;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ContractClaimable;

/**
 * ContractClaimableService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:17 IST 2016
 */
public abstract class ContractClaimableService {

  /**
   * ContractClaimable paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getContractClaimableSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_contract_claimable", ContractClaimable.class, main);
    sql.main("select scm_contract_claimable.id,scm_contract_claimable.contract_id,scm_contract_claimable.claimable_id from scm_contract_claimable scm_contract_claimable "); //Main query
    sql.count("select count(scm_contract_claimable.id) from scm_contract_claimable scm_contract_claimable "); //Count query
    sql.join("left outer join scm_contract scm_contract_claimablecontract_id on (scm_contract_claimablecontract_id.id = scm_contract_claimable.contract_id) left outer join scm_claimable scm_contract_claimableclaimable_id on (scm_contract_claimableclaimable_id.id = scm_contract_claimable.claimable_id)"); //Join Query

    sql.string(new String[]{"scm_contract_claimableclaimable_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_contract_claimable.id", "scm_contract_claimablecontract_id.version_no"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ContractClaimable.
   *
   * @param main
   * @return List of ContractClaimable
   */
  public static final List<ContractClaimable> listPaged(Main main) {
    return AppService.listPagedJpa(main, getContractClaimableSqlPaged(main));
  }

//  /**
//   * Return list of ContractClaimable based on condition
//   * @param main
//   * @return List<ContractClaimable>
//   */
//  public static final List<ContractClaimable> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getContractClaimableSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ContractClaimable by key.
   *
   * @param main
   * @param contractClaimable
   * @return ContractClaimable
   */
  public static final ContractClaimable selectByPk(Main main, ContractClaimable contractClaimable) {
    return (ContractClaimable) AppService.find(main, ContractClaimable.class, contractClaimable.getId());
  }

  /**
   * Insert ContractClaimable.
   *
   * @param main
   * @param contractClaimable
   */
  public static final void insert(Main main, ContractClaimable contractClaimable) {
    AppService.insert(main, contractClaimable);
  }

  /**
   * Update ContractClaimable by key.
   *
   * @param main
   * @param contractClaimable
   * @return ContractClaimable
   */
  public static final ContractClaimable updateByPk(Main main, ContractClaimable contractClaimable) {
    return (ContractClaimable) AppService.update(main, contractClaimable);
  }

  /**
   * Insert or update ContractClaimable
   *
   * @param main
   * @param ContractClaimable
   */
  public static void insertOrUpdate(Main main, ContractClaimable contractClaimable) {
    if (contractClaimable.getId() == null) {
      insert(main, contractClaimable);
    }
    updateByPk(main, contractClaimable);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param contractClaimable
   */
  public static void clone(Main main, ContractClaimable contractClaimable) {
    contractClaimable.setId(null); //Set to null for insert
    insert(main, contractClaimable);
  }

  /**
   * Delete ContractClaimable.
   *
   * @param main
   * @param contractClaimable
   */
  public static final void deleteByPk(Main main, ContractClaimable contractClaimable) {
    AppService.delete(main, ContractClaimable.class, contractClaimable.getId());
  }

  /**
   * Delete Array of ContractClaimable.
   *
   * @param main
   * @param contractClaimable
   */
  public static final void deleteByPkArray(Main main, ContractClaimable[] contractClaimable) {
    for (ContractClaimable e : contractClaimable) {
      deleteByPk(main, e);
    }
  }

  public static void insertArray(Main main, Claimable[] claimableSelected, Contract contract) {
    if (claimableSelected != null) {
      ContractClaimable contractClaimable;
      for (Claimable claimable : claimableSelected) {  //Reinserting
        contractClaimable = new ContractClaimable();
        contractClaimable.setClaimableId(claimable);
        contractClaimable.setContractId(contract);
        insert(main, contractClaimable);
      }
    }
  }

  public static void deleteContractClaimableRelation(Main main, Claimable claimable, Contract contract) {
    AppService.deleteSql(main, ContractClaimable.class, "delete from scm_contract_claimable where claimable_id = ? and contract_id = ?", new Object[]{claimable.getId(), contract.getId()});
  }

  public static final void selectInsertClaimableByContract(Main main, Integer activeContractId, Contract renewedContract) {

    List<ContractClaimable> claimableList = AppService.list(main,  ContractClaimable.class, "select * from scm_contract_claimable where contract_id = ?", new Object[]{activeContractId});
    ContractClaimable cc;
    for (ContractClaimable claimable : claimableList) {
      cc = new ContractClaimable();
      cc.setClaimableId(claimable.getClaimableId());
      cc.setContractId(renewedContract);
      insert(main, cc);      
    }
  }
}
