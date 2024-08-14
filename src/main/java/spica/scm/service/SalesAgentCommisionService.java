/*
 * @(#)SalesAgentCommisionService.java	1.0 Mon Jun 05 13:13:48 IST 2017 
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
import spica.scm.domain.SalesAgentCommision;
import spica.scm.domain.UserProfile;
import spica.scm.validate.SalesAgentCommisionIs;

/**
 * SalesAgentCommisionService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jun 05 13:13:48 IST 2017
 */
public abstract class SalesAgentCommisionService {

  /**
   * SalesAgentCommision paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentCommisionSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_commision", SalesAgentCommision.class, main);
    sql.main("select scm_sales_agent_commision.id,scm_sales_agent_commision.user_profile_id,scm_sales_agent_commision.sales_invoice_id,scm_sales_agent_commision.contract_id,scm_sales_agent_commision.commission_applicable_on,scm_sales_agent_commision.commision_actual_value,scm_sales_agent_commision.commision_provisioned_value,scm_sales_agent_commision.commision_actual_percent,scm_sales_agent_commision.commision_provisioned_percent,scm_sales_agent_commision.created_by,scm_sales_agent_commision.modified_by,scm_sales_agent_commision.created_at,scm_sales_agent_commision.modified_at from scm_sales_agent_commision scm_sales_agent_commision "); //Main query
    sql.count("select count(scm_sales_agent_commision.id) as total from scm_sales_agent_commision scm_sales_agent_commision "); //Count query
    sql.join("left outer join scm_user_profile scm_sales_agent_commisionuser_profile_id on (scm_sales_agent_commisionuser_profile_id.id = scm_sales_agent_commision.user_profile_id) left outer join scm_sales_invoice scm_sales_agent_commisionsales_invoice_id on (scm_sales_agent_commisionsales_invoice_id.id = scm_sales_agent_commision.sales_invoice_id) left outer join scm_contract scm_sales_agent_commisioncontract_id on (scm_sales_agent_commisioncontract_id.id = scm_sales_agent_commision.contract_id)"); //Join Query

    sql.string(new String[]{"scm_sales_agent_commisionuser_profile_id.user_code", "scm_sales_agent_commisionsales_invoice_id.invoice_no", "scm_sales_agent_commisioncontract_id.contract_code", "scm_sales_agent_commision.commission_applicable_on", "scm_sales_agent_commision.created_by", "scm_sales_agent_commision.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_commision.id", "scm_sales_agent_commision.commision_actual_value", "scm_sales_agent_commision.commision_provisioned_value", "scm_sales_agent_commision.commision_actual_percent", "scm_sales_agent_commision.commision_provisioned_percent"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_commision.created_at", "scm_sales_agent_commision.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentCommision.
   *
   * @param main
   * @return List of SalesAgentCommision
   */
  public static final List<SalesAgentCommision> listPaged(Main main, UserProfile userProfileId, int selectedMonth, int selectedYear) {
    main.clear();
    SqlPage sql = getSalesAgentCommisionSqlPaged(main);
    sql.cond("where scm_sales_agent_commision.user_profile_id = ? and date_part('month',scm_sales_agent_commision.created_at)=? and date_part('year',scm_sales_agent_commision.created_at)=?");
    sql.param(userProfileId.getId());
    sql.param(selectedMonth);
    sql.param(selectedYear);
    return AppService.listAllJpa(main, sql);
  }

//  /**
//   * Return list of SalesAgentCommision based on condition
//   * @param main
//   * @return List<SalesAgentCommision>
//   */
//  public static final List<SalesAgentCommision> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentCommisionSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
//  public static final SalesAgentCommision selectByUserProfile(Main main, UserProfile userProfileId, int selectedMonth, int selectedYear) {
//    String sql = "select * from scm_sales_agent_commision where user_profile_id = ? and date_part('month',created_at)=? and date_part('year',created_at)=?";
//    //  return AppService.listPagedJpa(main, sql); // For pagination in view
//    return (SalesAgentCommision) AppService.single(main, SalesAgentCommision.class, sql, new Object[]{userProfileId.getId(), selectedMonth, selectedYear});
//  }
  /**
   * Select SalesAgentCommision by key.
   *
   * @param main
   * @param scmSalesAgentCommision
   * @return SalesAgentCommision
   */
  public static final SalesAgentCommision selectByPk(Main main, SalesAgentCommision scmSalesAgentCommision) {
    return (SalesAgentCommision) AppService.find(main, SalesAgentCommision.class, scmSalesAgentCommision.getSalesAgentId().getId());
  }

  /**
   * Insert SalesAgentCommision.
   *
   * @param main
   * @param scmSalesAgentCommision
   */
  public static final void insert(Main main, SalesAgentCommision scmSalesAgentCommision) {
    SalesAgentCommisionIs.insertAble(main, scmSalesAgentCommision);  //Validating
    AppService.insert(main, scmSalesAgentCommision);

  }

  /**
   * Update SalesAgentCommision by key.
   *
   * @param main
   * @param scmSalesAgentCommision
   * @return SalesAgentCommision
   */
  public static final SalesAgentCommision updateByPk(Main main, SalesAgentCommision scmSalesAgentCommision) {
    SalesAgentCommisionIs.updateAble(main, scmSalesAgentCommision); //Validating
    return (SalesAgentCommision) AppService.update(main, scmSalesAgentCommision);
  }

  /**
   * Insert or update SalesAgentCommision
   *
   * @param main
   * @param scmSalesAgentCommision
   */
  public static void insertOrUpdate(Main main, SalesAgentCommision scmSalesAgentCommision) {
    if (scmSalesAgentCommision.getId() == null) {
      insert(main, scmSalesAgentCommision);
    } else {
      updateByPk(main, scmSalesAgentCommision);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param scmSalesAgentCommision
   */
  public static void clone(Main main, SalesAgentCommision scmSalesAgentCommision) {
    scmSalesAgentCommision.setId(null); //Set to null for insert
    insert(main, scmSalesAgentCommision);
  }

  /**
   * Delete SalesAgentCommision.
   *
   * @param main
   * @param scmSalesAgentCommision
   */
  public static final void deleteByPk(Main main, SalesAgentCommision scmSalesAgentCommision) {
    SalesAgentCommisionIs.deleteAble(main, scmSalesAgentCommision); //Validation
    AppService.delete(main, SalesAgentCommision.class, scmSalesAgentCommision.getId());
  }

  /**
   * Delete Array of SalesAgentCommision.
   *
   * @param main
   * @param scmSalesAgentCommision
   */
  public static final void deleteByPkArray(Main main, SalesAgentCommision[] scmSalesAgentCommision) {
    for (SalesAgentCommision e : scmSalesAgentCommision) {
      deleteByPk(main, e);
    }
  }
}
