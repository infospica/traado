/*
 * @(#)AccountStatusService.java	1.0 Thu Apr 07 11:31:25 IST 2016 
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
import spica.scm.domain.AccountStatus;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * AccountStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:25 IST 2016
 */
public abstract class AccountStatusService {

  public static final Integer ACCOUNT_STATUS_ACTIVE = 1;
  public static final Integer ACCOUNT_STATUS_DRAFT = 2;
  public static final Integer ACCOUNT_STATUS_INACTIVE = 3;

  /**
   * AccountStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_account_status", AccountStatus.class, main);
    sql.main("select scm_account_status.id,scm_account_status.title,scm_account_status.display_color,scm_account_status.sort_order,scm_account_status.created_by,scm_account_status.modified_by,scm_account_status.created_at,scm_account_status.modified_at from scm_account_status scm_account_status"); //Main query
    sql.count("select count(scm_account_status.id) as total from scm_account_status scm_account_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_account_status.title", "scm_account_status.display_color", "scm_account_status.created_by", "scm_account_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_account_status.id", "scm_account_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_account_status.created_at", "scm_account_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountStatus.
   *
   * @param main
   * @return List of AccountStatus
   */
  public static final List<AccountStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountStatusSqlPaged(main));
  }

//  /**
//   * Return list of AccountStatus based on condition
//   * @param main
//   * @return List<AccountStatus>
//   */
//  public static final List<AccountStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountStatus by key.
   *
   * @param main
   * @param accountStatus
   * @return AccountStatus
   */
  public static final AccountStatus selectByPk(Main main, AccountStatus accountStatus) {
    return (AccountStatus) AppService.find(main, AccountStatus.class, accountStatus.getId());
  }

  /**
   * Insert AccountStatus.
   *
   * @param main
   * @param accountStatus
   */
  public static final void insert(Main main, AccountStatus accountStatus) {
    insertAble(main, accountStatus);  //Validating
    AppService.insert(main, accountStatus);

  }

  /**
   * Update AccountStatus by key.
   *
   * @param main
   * @param accountStatus
   * @return AccountStatus
   */
  public static final AccountStatus updateByPk(Main main, AccountStatus accountStatus) {
    updateAble(main, accountStatus); //Validating
    return (AccountStatus) AppService.update(main, accountStatus);
  }

  /**
   * Insert or update AccountStatus
   *
   * @param main
   * @param accountStatus
   */
  public static void insertOrUpdate(Main main, AccountStatus accountStatus) {
    if (accountStatus.getId() == null) {
      insert(main, accountStatus);
    } else {
      updateByPk(main, accountStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountStatus
   */
  public static void clone(Main main, AccountStatus accountStatus) {
    accountStatus.setId(null); //Set to null for insert
    insert(main, accountStatus);
  }

  /**
   * Delete AccountStatus.
   *
   * @param main
   * @param accountStatus
   */
  public static final void deleteByPk(Main main, AccountStatus accountStatus) {
    deleteAble(main, accountStatus); //Validation
    AppService.delete(main, AccountStatus.class, accountStatus.getId());
  }

  /**
   * Delete Array of AccountStatus.
   *
   * @param main
   * @param accountStatus
   */
  public static final void deleteByPkArray(Main main, AccountStatus[] accountStatus) {
    for (AccountStatus e : accountStatus) {
      deleteByPk(main, e);
    }
  }
  
    /**
   * Validate delete.
   *
   * @param main
   * @param accountStatus
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountStatus accountStatus) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountStatus
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountStatus accountStatus) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_account_status where upper(title)=?", new Object[]{StringUtil.toUpperCase(accountStatus.getTitle())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountStatus
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountStatus accountStatus) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_account_status where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountStatus.getTitle()), accountStatus.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }
}
