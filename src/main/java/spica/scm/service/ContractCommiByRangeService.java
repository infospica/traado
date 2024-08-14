/*
 * @(#)ContractCommiByRangeService.java	1.0 Mon Aug 08 17:59:21 IST 2016
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
import spica.scm.domain.ContractCommiByRange;

/**
 * ContractCommiByRangeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class ContractCommiByRangeService {

  /**
   * ContractCommiByRange paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getContractCommiByRangeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_contract_comm_by_range", ContractCommiByRange.class, main);
    sql.main("select scm_contract_comm_by_range.id,scm_contract_comm_by_range.contract_id,scm_contract_comm_by_range.range_from,scm_contract_comm_by_range.range_to,scm_contract_comm_by_range.value_fixed,scm_contract_comm_by_range.value_percentage from scm_contract_comm_by_range scm_contract_comm_by_range "); //Main query
    sql.count("select count(scm_contract_comm_by_range.id) as total from scm_contract_comm_by_range scm_contract_comm_by_range "); //Count query
    sql.join("left outer join scm_contract scm_contract_comm_by_rangecontract_id on (scm_contract_comm_by_rangecontract_id.id = scm_contract_comm_by_range.contract_id)"); //Join Query

    sql.string(new String[]{"scm_contract_comm_by_rangecontract_id.contract_code"}); //String search or sort fields
    sql.number(new String[]{"scm_contract_comm_by_range.id", "scm_contract_comm_by_range.range_from", "scm_contract_comm_by_range.range_to", "scm_contract_comm_by_range.value_fixed", "scm_contract_comm_by_range.value_percentage"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ContractCommiByRange.
   *
   * @param main
   * @return List of ContractCommiByRange
   */
  public static final List<ContractCommiByRange> listPaged(Main main) {
    return AppService.listPagedJpa(main, getContractCommiByRangeSqlPaged(main));
  }

//  /**
//   * Return list of ContractCommiByRange based on condition
//   * @param main
//   * @return List<ContractCommiByRange>
//   */
//  public static final List<ContractCommiByRange> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getContractCommiByRangeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ContractCommiByRange by key.
   *
   * @param main
   * @param contractCommiByRange
   * @return ContractCommiByRange
   */
  public static final ContractCommiByRange selectByPk(Main main, ContractCommiByRange contractCommiByRange) {
    return (ContractCommiByRange) AppService.find(main, ContractCommiByRange.class, contractCommiByRange.getId());
  }

  /**
   * Insert ContractCommiByRange.
   *
   * @param main
   * @param contractCommiByRange
   */
  public static final void insert(Main main, ContractCommiByRange contractCommiByRange) {
    AppService.insert(main, contractCommiByRange);
  }

  /**
   * Update ContractCommiByRange by key.
   *
   * @param main
   * @param contractCommiByRange
   * @return ContractCommiByRange
   */
  public static final ContractCommiByRange updateByPk(Main main, ContractCommiByRange contractCommiByRange) {
    return (ContractCommiByRange) AppService.update(main, contractCommiByRange);
  }

  /**
   * Insert or update ContractCommiByRange
   *
   * @param main
   * @param contractCommiByRange
   */
  public static void insertOrUpdate(Main main, ContractCommiByRange contractCommiByRange) {
    if (contractCommiByRange.getId() == null) {
      insert(main, contractCommiByRange);
    } else {
      updateByPk(main, contractCommiByRange);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param contractCommiByRange
   */
  public static void clone(Main main, ContractCommiByRange contractCommiByRange) {
    contractCommiByRange.setId(null); //Set to null for insert
    insert(main, contractCommiByRange);
  }

  /**
   * Delete ContractCommiByRange.
   *
   * @param main
   * @param contractCommiByRange
   */
  public static final void deleteByPk(Main main, ContractCommiByRange contractCommiByRange) {
    AppService.delete(main, ContractCommiByRange.class, contractCommiByRange.getId());
  }

  /**
   * Delete Array of ContractCommiByRange.
   *
   * @param main
   * @param contractCommiByRange
   */
  public static final void deleteByPkArray(Main main, ContractCommiByRange[] contractCommiByRange) {
    for (ContractCommiByRange e : contractCommiByRange) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param contractId
   * @return
   */
  public static List<ContractCommiByRange> selectContractCommissionRangeByContract(Main main, Integer contractId) {
    return AppService.list(main, ContractCommiByRange.class, "select * from scm_contract_comm_by_range where contract_id = ?", new Object[]{contractId});
  }

  /**
   *
   * @param main
   * @param contractId
   */
  public static final void deleteContractCommissionRangeByContractId(Main main, Integer contractId) {
    AppService.deleteSql(main, ContractCommiByRange.class, "delete from scm_contract_comm_by_range where contract_id = ?", new Object[]{contractId});
  }

  /**
   *
   * @param main
   * @param activeContractId
   * @param renewedContract
   */
  public static final void selectInsertCommissionByRangeByContract(Main main, Integer activeContractId, Contract renewedContract) {
    List<ContractCommiByRange> list = selectContractCommissionRangeByContract(main, activeContractId);
    for (ContractCommiByRange contractCommissionByRange : list) {
      contractCommissionByRange.setId(null);
      contractCommissionByRange.setContractId(renewedContract);
      insert(main, contractCommissionByRange);
    }
  }

  public static void insertOrUpdateList(Main main, Contract contract, List<ContractCommiByRange> contractCommiByRangeList) {
    if (contractCommiByRangeList == null || contractCommiByRangeList.isEmpty()) {
      deleteContractCommissionRangeByContractId(main, contract.getId());
    } else {
      for (ContractCommiByRange contractCommiByRange : contractCommiByRangeList) {
        if (contractCommiByRange.getId() <= 0) {
          contractCommiByRange.setId(null);
          contractCommiByRange.setContractId(contract);
          insert(main, contractCommiByRange);
        }
      }
    }
  }
}
