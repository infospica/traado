/*
 * @(#)ContractCommissionSlabService.java	1.0 Thu Apr 07 11:31:25 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Contract;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ContractCommissionSlab;

/**
 * ContractCommissionSlabService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:25 IST 2016
 */
public abstract class ContractCommissionSlabService {

  /**
   * ContractCommissionSlab paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getContractCommissionSlabSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_contract_commission_slab", ContractCommissionSlab.class, main);
    sql.main("select scm_contract_commission_slab.id,scm_contract_commission_slab.contract_id,scm_contract_commission_slab.commission_value,scm_contract_commission_slab.slab_from,scm_contract_commission_slab.slab_to from scm_contract_commission_slab scm_contract_commission_slab "); //Main query
    sql.count("select count(scm_contract_commission_slab.id) from scm_contract_commission_slab scm_contract_commission_slab "); //Count query
    sql.join("left outer join scm_contract scm_contract_commission_slabcontract_id on (scm_contract_commission_slabcontract_id.id = scm_contract_commission_slab.contract_id)"); //Join Query

    sql.string(null); //String search or sort fields
    sql.number(new String[]{"scm_contract_commission_slab.id", "scm_contract_commission_slabcontract_id.version_no", "scm_contract_commission_slab.commission_value", "scm_contract_commission_slab.slab_from", "scm_contract_commission_slab.slab_to"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ContractCommissionSlab.
   *
   * @param main
   * @return List of ContractCommissionSlab
   */
  public static final List<ContractCommissionSlab> listPaged(Main main) {
    return AppService.listPagedJpa(main, getContractCommissionSlabSqlPaged(main));
  }

//  /**
//   * Return list of ContractCommissionSlab based on condition
//   * @param main
//   * @return List<ContractCommissionSlab>
//   */
//  public static final List<ContractCommissionSlab> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getContractCommissionSlabSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ContractCommissionSlab by key.
   *
   * @param main
   * @param contractCommissionSlab
   * @return ContractCommissionSlab
   */
  public static final ContractCommissionSlab selectByPk(Main main, ContractCommissionSlab contractCommissionSlab) {
    return (ContractCommissionSlab) AppService.find(main, ContractCommissionSlab.class, contractCommissionSlab.getId());
  }

  /**
   * Insert ContractCommissionSlab.
   *
   * @param main
   * @param contractCommissionSlab
   */
  public static final void insert(Main main, ContractCommissionSlab contractCommissionSlab) {
    AppService.insert(main, contractCommissionSlab);
  }

  /**
   * Update ContractCommissionSlab by key.
   *
   * @param main
   * @param contractCommissionSlab
   * @return ContractCommissionSlab
   */
  public static final ContractCommissionSlab updateByPk(Main main, ContractCommissionSlab contractCommissionSlab) {
    return (ContractCommissionSlab) AppService.update(main, contractCommissionSlab);
  }

  /**
   * Insert or update ContractCommissionSlab
   *
   * @param main
   * @param ContractCommissionSlab
   */
  public static void insertOrUpdate(Main main, ContractCommissionSlab contractCommissionSlab) {
    if (contractCommissionSlab.getId() == null) {
      insert(main, contractCommissionSlab);
    }
    updateByPk(main, contractCommissionSlab);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param contractCommissionSlab
   */
  public static void clone(Main main, ContractCommissionSlab contractCommissionSlab) {
    contractCommissionSlab.setId(null); //Set to null for insert
    insert(main, contractCommissionSlab);
  }

  /**
   * Delete ContractCommissionSlab.
   *
   * @param main
   * @param contractCommissionSlab
   */
  public static final void deleteByPk(Main main, ContractCommissionSlab contractCommissionSlab) {
    AppService.delete(main, ContractCommissionSlab.class, contractCommissionSlab.getId());
  }

  /**
   * Delete Array of ContractCommissionSlab.
   *
   * @param main
   * @param contractCommissionSlab
   */
  public static final void deleteByPkArray(Main main, ContractCommissionSlab[] contractCommissionSlab) {
    for (ContractCommissionSlab e : contractCommissionSlab) {
      deleteByPk(main, e);
    }
  }

  public static final List<ContractCommissionSlab> selectContractCommissionSlabByContract(Main main, long contractId) {
    return AppService.list(main, ContractCommissionSlab.class, "select * from scm_contract_commission_slab where contract_id = ?", new Object[]{contractId});
  }

  public static final void deleteContractCommissionSlabByContractId(Main main, Integer contractId) {
    AppService.deleteSql(main, ContractCommissionSlab.class, "delete from scm_contract_commission_slab where contract_id = ?", new Object[]{contractId});
  }

  public static final void selectInsertCommissionSlabByContract(Main main, Integer activeContractId, Contract renewedContract) {
    List<ContractCommissionSlab> list = selectContractCommissionSlabByContract(main, activeContractId);
    for (ContractCommissionSlab contractCommissionSlab : list) {
      contractCommissionSlab.setId(null);
      contractCommissionSlab.setContractId(renewedContract);
      insert(main, contractCommissionSlab);
    }
  }
}
