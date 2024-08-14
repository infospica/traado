/*
 * @(#)VendorClaimStatusService.java	1.0 Fri May 19 11:03:21 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.VendorClaimStatus;
import wawo.entity.core.UserMessageException;

/**
 * VendorClaimStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri May 19 11:03:21 IST 2017
 */
public abstract class VendorClaimStatusService {

  /**
   * VendorClaimStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getVendorClaimStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_vendor_claim_status", VendorClaimStatus.class, main);
    sql.main("select scm_vendor_claim_status.id,scm_vendor_claim_status.title,scm_vendor_claim_status.sort_order,scm_vendor_claim_status.created_by,scm_vendor_claim_status.modified_by,scm_vendor_claim_status.created_at,scm_vendor_claim_status.modified_at from scm_vendor_claim_status scm_vendor_claim_status"); //Main query
    sql.count("select count(scm_vendor_claim_status.id) as total from scm_vendor_claim_status scm_vendor_claim_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_vendor_claim_status.title", "scm_vendor_claim_status.created_by", "scm_vendor_claim_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_vendor_claim_status.id", "scm_vendor_claim_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_vendor_claim_status.created_at", "scm_vendor_claim_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of VendorClaimStatus.
   *
   * @param main
   * @return List of VendorClaimStatus
   */
  public static final List<VendorClaimStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getVendorClaimStatusSqlPaged(main));
  }

//  /**
//   * Return list of VendorClaimStatus based on condition
//   * @param main
//   * @return List<VendorClaimStatus>
//   */
//  public static final List<VendorClaimStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getVendorClaimStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select VendorClaimStatus by key.
   *
   * @param main
   * @param scmVendorClaimStatus
   * @return VendorClaimStatus
   */
  public static final VendorClaimStatus selectByPk(Main main, VendorClaimStatus scmVendorClaimStatus) {
    return (VendorClaimStatus) AppService.find(main, VendorClaimStatus.class, scmVendorClaimStatus.getId());
  }

  /**
   * Insert VendorClaimStatus.
   *
   * @param main
   * @param scmVendorClaimStatus
   */
  public static final void insert(Main main, VendorClaimStatus scmVendorClaimStatus) {
    insertAble(main, scmVendorClaimStatus);  //Validating
    AppService.insert(main, scmVendorClaimStatus);

  }

  /**
   * Update VendorClaimStatus by key.
   *
   * @param main
   * @param scmVendorClaimStatus
   * @return VendorClaimStatus
   */
  public static final VendorClaimStatus updateByPk(Main main, VendorClaimStatus scmVendorClaimStatus) {
    updateAble(main, scmVendorClaimStatus); //Validating
    return (VendorClaimStatus) AppService.update(main, scmVendorClaimStatus);
  }

  /**
   * Insert or update VendorClaimStatus
   *
   * @param main
   * @param scmVendorClaimStatus
   */
  public static void insertOrUpdate(Main main, VendorClaimStatus scmVendorClaimStatus) {
    if (scmVendorClaimStatus.getId() == null) {
      insert(main, scmVendorClaimStatus);
    } else {
      updateByPk(main, scmVendorClaimStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param scmVendorClaimStatus
   */
  public static void clone(Main main, VendorClaimStatus scmVendorClaimStatus) {
    scmVendorClaimStatus.setId(null); //Set to null for insert
    insert(main, scmVendorClaimStatus);
  }

  /**
   * Delete VendorClaimStatus.
   *
   * @param main
   * @param scmVendorClaimStatus
   */
  public static final void deleteByPk(Main main, VendorClaimStatus scmVendorClaimStatus) {
    deleteAble(main, scmVendorClaimStatus); //Validation
    AppService.delete(main, VendorClaimStatus.class, scmVendorClaimStatus.getId());
  }

  /**
   * Delete Array of VendorClaimStatus.
   *
   * @param main
   * @param scmVendorClaimStatus
   */
  public static final void deleteByPkArray(Main main, VendorClaimStatus[] scmVendorClaimStatus) {
    for (VendorClaimStatus e : scmVendorClaimStatus) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param scmVendorClaimStatus
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, VendorClaimStatus scmVendorClaimStatus) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param scmVendorClaimStatus
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, VendorClaimStatus scmVendorClaimStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_vendor_claim_status where upper(title)=?", new Object[]{StringUtil.toUpperCase(scmVendorClaimStatus.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param scmVendorClaimStatus
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, VendorClaimStatus scmVendorClaimStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_vendor_claim_status where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(scmVendorClaimStatus.getTitle()), scmVendorClaimStatus.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
