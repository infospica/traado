/*
 * @(#)SalesAgentCommisionClaimService.java	1.0 Mon Jun 05 13:13:48 IST 2017 
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
import spica.scm.domain.SalesAgentCommisionClaim;
import spica.scm.validate.SalesAgentCommisionClaimIs;

/**
 * SalesAgentCommisionClaimService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jun 05 13:13:48 IST 2017
 */
public abstract class SalesAgentCommisionClaimService {

  public static final String COMMISION_POPUP = "/commision/sales_agent_commision.xhtml";

  /**
   * SalesAgentCommisionClaim paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentCommisionClaimSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_commision_claim", SalesAgentCommisionClaim.class, main);
    sql.main("select scm_sales_agent_commision_claim.id,scm_sales_agent_commision_claim.user_profile_id,scm_sales_agent_commision_claim.commision_total,scm_sales_agent_commision_claim.created_by,scm_sales_agent_commision_claim.modified_by,scm_sales_agent_commision_claim.created_at,scm_sales_agent_commision_claim.modified_at,scm_sales_agent_commision_claim.status from scm_sales_agent_commision_claim scm_sales_agent_commision_claim "); //Main query
    sql.count("select count(scm_sales_agent_commision_claim.id) as total from scm_sales_agent_commision_claim scm_sales_agent_commision_claim "); //Count query
    sql.join("left outer join scm_user_profile scm_sales_agent_commision_claimuser_profile_id on (scm_sales_agent_commision_claimuser_profile_id.id = scm_sales_agent_commision_claim.user_profile_id)"); //Join Query

    sql.string(new String[]{"scm_sales_agent_commision_claimuser_profile_id.user_code", "scm_sales_agent_commision_claim.created_by", "scm_sales_agent_commision_claim.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_commision_claim.id", "scm_sales_agent_commision_claim.commision_total", "scm_sales_agent_commision_claim.status"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_commision_claim.created_at", "scm_sales_agent_commision_claim.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentCommisionClaim.
   *
   * @param main
   * @return List of SalesAgentCommisionClaim
   */
  public static final List<SalesAgentCommisionClaim> listPaged(Main main) {
    main.clear();
    return AppService.listPagedJpa(main, getSalesAgentCommisionClaimSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentCommisionClaim based on condition
//   * @param main
//   * @return List<SalesAgentCommisionClaim>
//   */
//  public static final List<SalesAgentCommisionClaim> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentCommisionClaimSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesAgentCommisionClaim by key.
   *
   * @param main
   * @param scmSalesAgentCommisionClaim
   * @return SalesAgentCommisionClaim
   */
  public static final SalesAgentCommisionClaim selectByPk(Main main, SalesAgentCommisionClaim scmSalesAgentCommisionClaim) {
    return (SalesAgentCommisionClaim) AppService.find(main, SalesAgentCommisionClaim.class, scmSalesAgentCommisionClaim.getId());
  }

  /**
   * Insert SalesAgentCommisionClaim.
   *
   * @param main
   * @param scmSalesAgentCommisionClaim
   */
  public static final void insert(Main main, SalesAgentCommisionClaim scmSalesAgentCommisionClaim) {
    SalesAgentCommisionClaimIs.insertAble(main, scmSalesAgentCommisionClaim);  //Validating
    AppService.insert(main, scmSalesAgentCommisionClaim);

  }

  /**
   * Update SalesAgentCommisionClaim by key.
   *
   * @param main
   * @param scmSalesAgentCommisionClaim
   * @return SalesAgentCommisionClaim
   */
  public static final SalesAgentCommisionClaim updateByPk(Main main, SalesAgentCommisionClaim scmSalesAgentCommisionClaim) {
    SalesAgentCommisionClaimIs.updateAble(main, scmSalesAgentCommisionClaim); //Validating
    return (SalesAgentCommisionClaim) AppService.update(main, scmSalesAgentCommisionClaim);
  }

  /**
   * Insert or update SalesAgentCommisionClaim
   *
   * @param main
   * @param scmSalesAgentCommisionClaim
   */
  public static void insertOrUpdate(Main main, SalesAgentCommisionClaim scmSalesAgentCommisionClaim) {
    if (scmSalesAgentCommisionClaim.getId() == null) {
      insert(main, scmSalesAgentCommisionClaim);
    } else {
      updateByPk(main, scmSalesAgentCommisionClaim);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param scmSalesAgentCommisionClaim
   */
  public static void clone(Main main, SalesAgentCommisionClaim scmSalesAgentCommisionClaim) {
    scmSalesAgentCommisionClaim.setId(null); //Set to null for insert
    insert(main, scmSalesAgentCommisionClaim);
  }

  /**
   * Delete SalesAgentCommisionClaim.
   *
   * @param main
   * @param scmSalesAgentCommisionClaim
   */
  public static final void deleteByPk(Main main, SalesAgentCommisionClaim scmSalesAgentCommisionClaim) {
    SalesAgentCommisionClaimIs.deleteAble(main, scmSalesAgentCommisionClaim); //Validation
    AppService.delete(main, SalesAgentCommisionClaim.class, scmSalesAgentCommisionClaim.getId());
  }

  /**
   * Delete Array of SalesAgentCommisionClaim.
   *
   * @param main
   * @param scmSalesAgentCommisionClaim
   */
  public static final void deleteByPkArray(Main main, SalesAgentCommisionClaim[] scmSalesAgentCommisionClaim) {
    for (SalesAgentCommisionClaim e : scmSalesAgentCommisionClaim) {
      deleteByPk(main, e);
    }
  }
}
