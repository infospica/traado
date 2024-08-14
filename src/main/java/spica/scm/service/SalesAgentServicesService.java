/*
 * @(#)ScmSalesAgentServicesService.java	1.0 Mon Sep 11 12:32:45 IST 2017 
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
import spica.scm.domain.SalesAgentServices;
import spica.scm.validate.SalesAgentServicesIs;

/**
 * ScmSalesAgentServicesService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Sep 11 12:32:45 IST 2017
 */
public abstract class SalesAgentServicesService {

  /**
   * ScmSalesAgentServices paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentServicesSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_services", SalesAgentServices.class, main);
    sql.main("select scm_sales_agent_services.id,scm_sales_agent_services.user_profile_id,scm_sales_agent_services.services_id,scm_sales_agent_services.created_at,scm_sales_agent_services.modified_at,scm_sales_agent_services.created_by,scm_sales_agent_services.modified_by from scm_sales_agent_services scm_sales_agent_services "); //Main query
    sql.count("select count(scm_sales_agent_services.id) as total from scm_sales_agent_services scm_sales_agent_services "); //Count query
    sql.join("left outer join scm_user_profile scm_sales_agent_servicesuser_profile_id on (scm_sales_agent_servicesuser_profile_id.id = scm_sales_agent_services.user_profile_id) left outer join scm_services scm_sales_agent_servicesservices_id on (scm_sales_agent_servicesservices_id.id = scm_sales_agent_services.services_id)"); //Join Query

    sql.string(new String[]{"scm_sales_agent_servicesuser_profile_id.user_code", "scm_sales_agent_servicesservices_id.title", "scm_sales_agent_services.created_by", "scm_sales_agent_services.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_services.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_services.created_at", "scm_sales_agent_services.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ScmSalesAgentServices.
   *
   * @param main
   * @return List of ScmSalesAgentServices
   */
  public static final List<SalesAgentServices> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesAgentServicesSqlPaged(main));
  }

//  /**
//   * Return list of ScmSalesAgentServices based on condition
//   * @param main
//   * @return List<ScmSalesAgentServices>
//   */
//  public static final List<ScmSalesAgentServices> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getScmSalesAgentServicesSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ScmSalesAgentServices by key.
   *
   * @param main
   * @param scmSalesAgentServices
   * @return ScmSalesAgentServices
   */
  public static final SalesAgentServices selectByPk(Main main, SalesAgentServices salesAgentServices) {
    return (SalesAgentServices) AppService.find(main, SalesAgentServices.class, salesAgentServices.getId());
  }

  /**
   * Insert ScmSalesAgentServices.
   *
   * @param main
   * @param scmSalesAgentServices
   */
  public static final void insert(Main main, SalesAgentServices salesAgentServices) {
    SalesAgentServicesIs.insertAble(main, salesAgentServices);  //Validating
    AppService.insert(main, salesAgentServices);

  }

  /**
   * Update ScmSalesAgentServices by key.
   *
   * @param main
   * @param scmSalesAgentServices
   * @return ScmSalesAgentServices
   */
  public static final SalesAgentServices updateByPk(Main main, SalesAgentServices salesAgentServices) {
    SalesAgentServicesIs.updateAble(main, salesAgentServices); //Validating
    return (SalesAgentServices) AppService.update(main, salesAgentServices);
  }

  /**
   * Insert or update ScmSalesAgentServices
   *
   * @param main
   * @param scmSalesAgentServices
   */
  public static void insertOrUpdate(Main main, SalesAgentServices salesAgentServices) {
    if (salesAgentServices.getId() == null) {
      insert(main, salesAgentServices);
    } else {
      updateByPk(main, salesAgentServices);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param scmSalesAgentServices
   */
  public static void clone(Main main, SalesAgentServices salesAgentServices) {
    salesAgentServices.setId(null); //Set to null for insert
    insert(main, salesAgentServices);
  }

  /**
   * Delete ScmSalesAgentServices.
   *
   * @param main
   * @param scmSalesAgentServices
   */
  public static final void deleteByPk(Main main, SalesAgentServices salesAgentServices) {
    SalesAgentServicesIs.deleteAble(main, salesAgentServices); //Validation
    AppService.delete(main, SalesAgentServices.class, salesAgentServices.getId());
  }

  /**
   * Delete Array of ScmSalesAgentServices.
   *
   * @param main
   * @param scmSalesAgentServices
   */
  public static final void deleteByPkArray(Main main, SalesAgentServices[] salesAgentServices) {
    for (SalesAgentServices e : salesAgentServices) {
      deleteByPk(main, e);
    }
  }
}
