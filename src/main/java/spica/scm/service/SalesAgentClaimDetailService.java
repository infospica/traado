/*
 * @(#)SalesAgentClaimDetailDetailService.java	1.0 Fri May 19 11:03:21 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import spica.scm.domain.SalesAgentClaimDetail;
import spica.scm.validate.SalesAgentClaimDetailIs;
import spica.scm.domain.SalesAgentClaim;

/**
 * SalesAgentClaimDetailDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri May 19 11:03:21 IST 2017
 */
public abstract class SalesAgentClaimDetailService {

  /**
   * Select SalesAgentClaimDetail by key.
   *
   * @param main
   * @param scmSalesAgentClaimDetail
   * @return SalesAgentClaimDetail
   */
  public static final SalesAgentClaimDetail selectByPk(Main main, SalesAgentClaimDetail scmSalesAgentClaimDetail) {
    return (SalesAgentClaimDetail) AppService.find(main, SalesAgentClaimDetail.class, scmSalesAgentClaimDetail.getId());
  }

  /**
   * Insert SalesAgentClaimDetail.
   *
   * @param main
   * @param scmSalesAgentClaimDetail
   */
  public static final void insert(Main main, SalesAgentClaimDetail scmSalesAgentClaimDetail) {
    SalesAgentClaimDetailIs.insertAble(main, scmSalesAgentClaimDetail);  //Validating
    AppService.insert(main, scmSalesAgentClaimDetail);
    
  }

  /**
   * Update SalesAgentClaimDetail by key.
   *
   * @param main
   * @param scmSalesAgentClaimDetail
   * @return SalesAgentClaimDetail
   */
  public static final SalesAgentClaimDetail updateByPk(Main main, SalesAgentClaimDetail scmSalesAgentClaimDetail) {
    SalesAgentClaimDetailIs.updateAble(main, scmSalesAgentClaimDetail); //Validating
    return (SalesAgentClaimDetail) AppService.update(main, scmSalesAgentClaimDetail);
  }

  /**
   * Insert or update SalesAgentClaimDetail
   *
   * @param main
   * @param scmSalesAgentClaimDetail
   */
  public static void insertOrUpdate(Main main, SalesAgentClaimDetail scmSalesAgentClaimDetail) {
    if (scmSalesAgentClaimDetail.getId() == null) {
      insert(main, scmSalesAgentClaimDetail);
    } else {
      updateByPk(main, scmSalesAgentClaimDetail);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param scmSalesAgentClaimDetail
   */
  public static void clone(Main main, SalesAgentClaimDetail scmSalesAgentClaimDetail) {
    scmSalesAgentClaimDetail.setId(null); //Set to null for insert
    insert(main, scmSalesAgentClaimDetail);
  }

  /**
   * Delete SalesAgentClaimDetail.
   *
   * @param main
   * @param scmSalesAgentClaimDetail
   */
  public static final void deleteByPk(Main main, SalesAgentClaimDetail scmSalesAgentClaimDetail) {
    SalesAgentClaimDetailIs.deleteAble(main, scmSalesAgentClaimDetail); //Validation
    AppService.delete(main, SalesAgentClaimDetail.class, scmSalesAgentClaimDetail.getId());
  }

  /**
   * Delete Array of SalesAgentClaimDetail.
   *
   * @param main
   * @param scmSalesAgentClaimDetail
   */
  public static final void deleteByPkArray(Main main, SalesAgentClaimDetail[] scmSalesAgentClaimDetail) {
    for (SalesAgentClaimDetail e : scmSalesAgentClaimDetail) {
      deleteByPk(main, e);
    }
  }

  public static final List<SalesAgentClaimDetail> selectBySalesAgentClaim(Main main, SalesAgentClaim salesAgentClaim) {
    return AppService.list(main, SalesAgentClaimDetail.class, "select * from scm_sales_agent_claim_detail where sales_agent_claim_id=?", new Object[]{salesAgentClaim.getId()});
  }
  
  public static final void insertOrUpdate(Main main, List<SalesAgentClaimDetail> scmSalesAgentClaimDetail, SalesAgentClaim salesAgentClaim) {
    for (SalesAgentClaimDetail list : scmSalesAgentClaimDetail) {
      list.setSalesAgentClaimId(salesAgentClaim);
      insertOrUpdate(main, list);
    }
  }
}
