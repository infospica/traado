/*
 * @(#)ClaimFrequencyService.java	1.0 Fri Jun 17 17:57:48 IST 2016 
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
import spica.scm.domain.ClaimFrequency;
import wawo.entity.core.UserMessageException;

/**
 * ClaimFrequencyService
 * @author	Spirit 1.2
 * @version	1.0, Fri Jun 17 17:57:48 IST 2016 
 */

public abstract class ClaimFrequencyService {  
 
 /**
   * ClaimFrequency paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getClaimFrequencySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_claim_frequency", ClaimFrequency.class, main);
    sql.main("select scm_claim_frequency.id,scm_claim_frequency.title,scm_claim_frequency.created_by,scm_claim_frequency.modified_by,scm_claim_frequency.created_at,scm_claim_frequency.modified_at from scm_claim_frequency scm_claim_frequency"); //Main query
    sql.count("select count(scm_claim_frequency.id) as total from scm_claim_frequency scm_claim_frequency"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_claim_frequency.title","scm_claim_frequency.created_by","scm_claim_frequency.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_claim_frequency.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_claim_frequency.created_at","scm_claim_frequency.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of ClaimFrequency.
  * @param main
  * @return List of ClaimFrequency
  */
  public static final List<ClaimFrequency> listPaged(Main main) {
     return AppService.listPagedJpa(main, getClaimFrequencySqlPaged(main));
  }

//  /**
//   * Return list of ClaimFrequency based on condition
//   * @param main
//   * @return List<ClaimFrequency>
//   */
//  public static final List<ClaimFrequency> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getClaimFrequencySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select ClaimFrequency by key.
  * @param main
  * @param claimFrequency
  * @return ClaimFrequency
  */
  public static final ClaimFrequency selectByPk(Main main, ClaimFrequency claimFrequency) {
    return (ClaimFrequency) AppService.find(main, ClaimFrequency.class, claimFrequency.getId());
  }

 /**
  * Insert ClaimFrequency.
  * @param main
  * @param claimFrequency
  */
  public static final void insert(Main main, ClaimFrequency claimFrequency) {
    insertAble(main, claimFrequency);  //Validating
    AppService.insert(main, claimFrequency);

  }

 /**
  * Update ClaimFrequency by key.
  * @param main
  * @param claimFrequency
  * @return ClaimFrequency
  */
  public static final ClaimFrequency updateByPk(Main main, ClaimFrequency claimFrequency) {
    updateAble(main, claimFrequency); //Validating
    return (ClaimFrequency) AppService.update(main, claimFrequency);
  }

  /**
   * Insert or update ClaimFrequency
   *
   * @param main
   * @param ClaimFrequency
   */
  public static void insertOrUpdate(Main main, ClaimFrequency claimFrequency) {
    if (claimFrequency.getId() == null) {
      insert(main, claimFrequency);
    }
    else{
        updateByPk(main, claimFrequency);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param claimFrequency
   */
  public static void clone(Main main, ClaimFrequency claimFrequency) {
    claimFrequency.setId(null); //Set to null for insert
    insert(main, claimFrequency);
  }

 /**
  * Delete ClaimFrequency.
  * @param main
  * @param claimFrequency
  */
  public static final void deleteByPk(Main main, ClaimFrequency claimFrequency) {
    deleteAble(main, claimFrequency); //Validation
    AppService.delete(main, ClaimFrequency.class, claimFrequency.getId());
  }
	
 /**
  * Delete Array of ClaimFrequency.
  * @param main
  * @param claimFrequency
  */
  public static final void deleteByPkArray(Main main, ClaimFrequency[] claimFrequency) {
    for (ClaimFrequency e : claimFrequency) {
      deleteByPk(main, e);
    }
  }
  /**
   * Validate delete.
   *
   * @param main
   * @param claimFrequency
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, ClaimFrequency claimFrequency) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param claimFrequency
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, ClaimFrequency claimFrequency) throws UserMessageException {

  }

  /**
   * Validate update.
   *
   * @param main
   * @param claimFrequency
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, ClaimFrequency claimFrequency) throws UserMessageException {

  }

}
