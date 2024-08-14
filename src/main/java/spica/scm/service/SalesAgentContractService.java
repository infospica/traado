/*
 * @(#)SalesAgentContractService.java	1.0 Fri Dec 23 10:28:18 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.fin.service.LedgerExternalService;
import spica.scm.domain.Company;
import spica.scm.domain.SalesAgent;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesAgentContract;
import spica.scm.domain.UserProfile;
import spica.scm.validate.SalesAgentContractIs;
import wawo.app.faces.MainView;

/**
 * SalesAgentContractService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:18 IST 2016
 */
public abstract class SalesAgentContractService {

  /**
   * SalesAgentContract paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentContractSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_contract", SalesAgentContract.class, main);
    sql.main("select scm_sales_agent_contract.id,scm_sales_agent_contract.company_id,scm_sales_agent_contract.commission_type,scm_sales_agent_contract.valid_from,scm_sales_agent_contract.valid_to,scm_sales_agent_contract.commission_applicable_on,scm_sales_agent_contract.commission_value_fixed,scm_sales_agent_contract.commission_value_percentage,scm_sales_agent_contract.commission_by_range,scm_sales_agent_contract.created_by,scm_sales_agent_contract.modified_by,scm_sales_agent_contract.created_at,scm_sales_agent_contract.modifeid_at,scm_sales_agent_contract.user_profile_id,scm_sales_agent_contract.commission_on from scm_sales_agent_contract scm_sales_agent_contract "); //Main query
    sql.count("select count(scm_sales_agent_contract.id) as total from scm_sales_agent_contract scm_sales_agent_contract "); //Count query
    sql.join("left outer join scm_company scm_sales_agent_contractcompany_id on (scm_sales_agent_contractcompany_id.id = scm_sales_agent_contract.company_id) left outer join scm_user_profile scm_sales_agent_contractuser_profile_id on (scm_sales_agent_contractuser_profile_id.id = scm_sales_agent_contract.company_id)"); //Join Query

    sql.string(new String[]{"scm_sales_agent_contractcompany_id.company_name", "scm_sales_agent_contract.created_by", "scm_sales_agent_contract.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_contract.id", "scm_sales_agent_contract.commission_applicable_on", "scm_sales_agent_contract.commission_value_fixed", "scm_sales_agent_contract.commission_value_percentage", "scm_sales_agent_contract.commission_by_range"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_contract.valid_from", "scm_sales_agent_contract.valid_to", "scm_sales_agent_contract.created_at", "scm_sales_agent_contract.modifeid_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentContract.
   *
   * @param main
   * @return List of SalesAgentContract
   */
  public static final List<SalesAgentContract> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesAgentContractSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentContract based on condition
//   * @param main
//   * @return List<SalesAgentContract>
//   */
//  public static final List<SalesAgentContract> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentContractSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  public static final SalesAgentContract listPagedBySalesAgent(Main main, SalesAgent salesAgent) {
    return (SalesAgentContract) AppService.single(main, SalesAgentContract.class, "select * from scm_sales_agent_contract scm_sales_agent_contract where scm_sales_agent_contract.sales_agent_id = ? order by id desc limit ?", new Object[]{salesAgent.getId(), 1});
  }

  /**
   * Select SalesAgentContract by key.
   *
   * @param main
   * @param salesAgentContract
   * @return SalesAgentContract
   */
  public static final SalesAgentContract selectByPk(Main main, SalesAgentContract salesAgentContract) {
    return (SalesAgentContract) AppService.find(main, SalesAgentContract.class, salesAgentContract.getId());
  }

  /**
   * Insert or update SalesAgentContract
   *
   * @param main
   * @param salesAgentContract
   */
  public static void insertOrUpdate(Main main, SalesAgentContract salesAgentContract) {
    if (salesAgentContract.getId() == null) {
      SalesAgentContractIs.insertAble(main, salesAgentContract);  //Validating
      AppService.insert(main, salesAgentContract);
    } else {
      SalesAgentContractIs.updateAble(main, salesAgentContract); //Validating
      salesAgentContract = (SalesAgentContract) AppService.update(main, salesAgentContract);
    }
    LedgerExternalService.saveLedgerSalesAgent(main, salesAgentContract.getSalesAgentId());
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesAgentContract
   */
  public static void clone(Main main, SalesAgentContract salesAgentContract) {
    salesAgentContract.setId(null); //Set to null for insert
    insertOrUpdate(main, salesAgentContract);
  }

  /**
   * Delete SalesAgentContract.
   *
   * @param main
   * @param salesAgentContract
   */
  public static final void deleteByPk(Main main, SalesAgentContract salesAgentContract) {
    LedgerExternalService.deleteLedgerSalesAgent(main, salesAgentContract.getSalesAgentId());
    AppService.delete(main, SalesAgentContract.class, salesAgentContract.getId());
  }

  /**
   * Delete Array of SalesAgentContract.
   *
   * @param main
   * @param salesAgentContract
   */
  public static final void deleteByPkArray(Main main, SalesAgentContract[] salesAgentContract) {
    for (SalesAgentContract e : salesAgentContract) {
      deleteByPk(main, e);
    }
  }

  public static final List<SalesAgentContract> salesAgentContarctListByCompany(Main main, Company company) {
    SqlPage sql = getSalesAgentContractSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_sales_agent_contract.company_id=?");
    sql.param(company.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static void insertArray(MainView main, SalesAgent[] salesAgentSelected, Company company) {
    if (salesAgentSelected != null) {
      SalesAgentContract salesAgentContract;
      for (SalesAgent salesAgent : salesAgentSelected) {  //Reinserting
        salesAgentContract = new SalesAgentContract();
        salesAgentContract.setSalesAgentId(salesAgent);
        salesAgentContract.setCompanyId(company);
        salesAgentContract.setCommissionApplicableOn(0);
        salesAgentContract.setCommissionByRange(0);
        salesAgentContract.setCommissionValueFixed(0.0);
        salesAgentContract.setCommissionValuePercentage(0.0);
        salesAgentContract.setValidFrom(null);
        salesAgentContract.setValidTo(null);
        AppService.insert(main, salesAgentContract);
      }
    }
  }

  public static final void deleteSalesAgentContract(Main main, SalesAgentContract salesAgentContract) {
    AppService.deleteSql(main, SalesAgentContract.class, "delete from scm_sales_agent_contract where id = ?", new Object[]{salesAgentContract.getId()});
  }
}
