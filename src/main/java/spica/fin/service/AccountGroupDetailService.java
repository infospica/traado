/*
 * @(#)AccountGroupDetailService.java	1.0 Wed Apr 13 15:41:17 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.AccountGroupDetail;
import wawo.app.faces.MainView;
import wawo.entity.core.UserMessageException;

/**
 * AccountGroupDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:17 IST 2016
 */
public abstract class AccountGroupDetailService {

  /**
   * AccountGroupDetail paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountGroupDetailSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_account_group_detail", AccountGroupDetail.class, main);
    sql.main("select scm_account_group_detail.id,scm_account_group_detail.account_group_id,scm_account_group_detail.account_id from scm_account_group_detail scm_account_group_detail "); //Main query
    sql.count("select count(scm_account_group_detail.id) from scm_account_group_detail scm_account_group_detail "); //Count query
    sql.join("left outer join scm_account_group scm_account_group_detailaccount_group_id on (scm_account_group_detailaccount_group_id.id = scm_account_group_detail.account_group_id) left outer join scm_account scm_account_group_detailaccount_id on (scm_account_group_detailaccount_id.id = scm_account_group_detail.account_id)"); //Join Query

    sql.string(new String[]{"scm_account_group_detailaccount_group_id.group_name", "scm_account_group_detailaccount_id.account_code"}); //String search or sort fields
    sql.number(new String[]{"scm_account_group_detail.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountGroupDetail.
   *
   * @param main
   * @return List of AccountGroupDetail
   */
  public static final List<AccountGroupDetail> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountGroupDetailSqlPaged(main));
  }

//  /**
//   * Return list of AccountGroupDetail based on condition
//   * @param main
//   * @return List<AccountGroupDetail>
//   */
//  public static final List<AccountGroupDetail> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountGroupDetailSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountGroupDetail by key.
   *
   * @param main
   * @param accountGroupDetail
   * @return AccountGroupDetail
   */
  public static final AccountGroupDetail selectByPk(Main main, AccountGroupDetail accountGroupDetail) {
    return (AccountGroupDetail) AppService.find(main, AccountGroupDetail.class, accountGroupDetail.getId());
  }

  /**
   * Insert AccountGroupDetail.
   *
   * @param main
   * @param accountGroupDetail
   */
  public static final void insert(Main main, AccountGroupDetail accountGroupDetail) {
    insertAble(main, accountGroupDetail);  //Validating
    AppService.insert(main, accountGroupDetail);
    
  }

  /**
   * Update AccountGroupDetail by key.
   *
   * @param main
   * @param accountGroupDetail
   * @return AccountGroupDetail
   */
  public static final AccountGroupDetail updateByPk(Main main, AccountGroupDetail accountGroupDetail) {
    updateAble(main, accountGroupDetail); //Validating
    return (AccountGroupDetail) AppService.update(main, accountGroupDetail);
  }

  /**
   * Insert or update AccountGroupDetail
   *
   * @param main
   * @param accountGroupDetail
   */
  public static void insertOrUpdate(Main main, AccountGroupDetail accountGroupDetail) {
    if (accountGroupDetail.getId() == null) {
      insert(main, accountGroupDetail);
    }
    updateByPk(main, accountGroupDetail);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountGroupDetail
   */
  public static void clone(Main main, AccountGroupDetail accountGroupDetail) {
    accountGroupDetail.setId(null); //Set to null for insert
    insert(main, accountGroupDetail);
  }

  /**
   * Delete AccountGroupDetail.
   *
   * @param main
   * @param accountGroupDetail
   */
  public static final void deleteByPk(Main main, AccountGroupDetail accountGroupDetail) {
    deleteAble(main, accountGroupDetail); //Validation
    AppService.delete(main, AccountGroupDetail.class, accountGroupDetail.getId());
  }

  /**
   * Delete Array of AccountGroupDetail.
   *
   * @param main
   * @param accountGroupDetail
   */
  public static final void deleteByPkArray(Main main, AccountGroupDetail[] accountGroupDetail) {
    for (AccountGroupDetail e : accountGroupDetail) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @param account
   */
  public static void deleteAccountGroupRelation(MainView main, AccountGroup accountGroup, Account account) {
    AppService.deleteSql(main, AccountGroupDetail.class, "delete from scm_account_group_detail where account_group_id = ? and account_id = ?", new Object[]{accountGroup.getId(), account.getId()});
  }
  
  public static void insertPrimaryVendorsAccountGroup(Main main, Account account, AccountGroup accountGroup) {
    AccountGroupDetail agd = new AccountGroupDetail();
    agd.setAccountGroupId(accountGroup);
    agd.setAccountId(account);
    insert(main, agd);
  }
  
  /**
   * Method to insert AccountGroupDetail of secondary vendor account.
   * 
   * @param main
   * @param account 
   */
  public static void insertSecondaryVendorsAccountGroup(Main main, Account account) {
    AccountGroup defaultAccountGroup = (AccountGroup) AppService.single(main, AccountGroup.class, "select scm_account_group.* from scm_account_group_detail JOIN scm_account_group ON scm_account_group_detail.account_group_id = scm_account_group.id \n"
            + "AND scm_account_group.is_default =1 where scm_account_group_detail.account_id  IN ( select id from scm_account where vendor_id = ?) ", new Object[]{account.getVendorId().getVendorId().getId()});
    AccountGroupDetail agd = new AccountGroupDetail();
    agd.setAccountGroupId(defaultAccountGroup);
    agd.setAccountId(account);
    insert(main, agd);
  }
  
   private static final void deleteAble(Main main, AccountGroupDetail accountGroupDetail) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountGroupDetail
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountGroupDetail accountGroupDetail) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_account_group_detail where upper()=?", new Object[]{StringUtil.toUpperCase(accountGroupDetail.get())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountGroupDetail
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountGroupDetail accountGroupDetail) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_account_group_detail where upper()=? and id !=?", new Object[]{StringUtil.toUpperCase(accountGroupDetail.get()), accountGroupDetail.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
  
}
