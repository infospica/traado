/*
 * @(#)BankAccountTypeService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
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
import spica.scm.domain.BankAccountType;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * BankAccountTypeService
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016 
 */

public abstract class BankAccountTypeService {  
 
 /**
   * BankAccountType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getBankAccountTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_bank_account_type", BankAccountType.class, main);
    sql.main("select scm_bank_account_type.id,scm_bank_account_type.title,scm_bank_account_type.sort_order,scm_bank_account_type.created_by,scm_bank_account_type.modified_by,scm_bank_account_type.created_at,scm_bank_account_type.modified_at from scm_bank_account_type scm_bank_account_type"); //Main query
    sql.count("select count(scm_bank_account_type.id) from scm_bank_account_type scm_bank_account_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_bank_account_type.title","scm_bank_account_type.created_by","scm_bank_account_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_bank_account_type.id","scm_bank_account_type.sort_order"}); //Number search or sort fields
    sql.date(new String[]{"scm_bank_account_type.created_at","scm_bank_account_type.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of BankAccountType.
  * @param main
  * @return List of BankAccountType
  */
  public static final List<BankAccountType> listPaged(Main main) {
     return AppService.listPagedJpa(main, getBankAccountTypeSqlPaged(main));
  }

//  /**
//   * Return list of BankAccountType based on condition
//   * @param main
//   * @return List<BankAccountType>
//   */
//  public static final List<BankAccountType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getBankAccountTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select BankAccountType by key.
  * @param main
  * @param bankAccountType
  * @return BankAccountType
  */
  public static final BankAccountType selectByPk(Main main, BankAccountType bankAccountType) {
    return (BankAccountType) AppService.find(main, BankAccountType.class, bankAccountType.getId());
  }

 /**
  * Insert BankAccountType.
  * @param main
  * @param bankAccountType
  */
  public static final void insert(Main main, BankAccountType bankAccountType) {
    insertAble(main, bankAccountType);  //Validating
    AppService.insert(main, bankAccountType);

  }

 /**
  * Update BankAccountType by key.
  * @param main
  * @param bankAccountType
  * @return BankAccountType
  */
  public static final BankAccountType updateByPk(Main main, BankAccountType bankAccountType) {
    updateAble(main, bankAccountType); //Validating
    return (BankAccountType) AppService.update(main, bankAccountType);
  }

  /**
   * Insert or update BankAccountType
   *
   * @param main
   * @param bankAccountType
   */
  public static void insertOrUpdate(Main main, BankAccountType bankAccountType) {
    if (bankAccountType.getId() == null) {
      insert(main, bankAccountType);
    } else {
    updateByPk(main, bankAccountType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param bankAccountType
   */
  public static void clone(Main main, BankAccountType bankAccountType) {
    bankAccountType.setId(null); //Set to null for insert
    insert(main, bankAccountType);
  }

 /**
  * Delete BankAccountType.
  * @param main
  * @param bankAccountType
  */
  public static final void deleteByPk(Main main, BankAccountType bankAccountType) {
    AppService.delete(main, BankAccountType.class, bankAccountType.getId());
  }
	
 /**
  * Delete Array of BankAccountType.
  * @param main
  * @param bankAccountType
  */
  public static final void deleteByPkArray(Main main, BankAccountType[] bankAccountType) {
    for (BankAccountType e : bankAccountType) {
      deleteByPk(main, e);
    }
  }
  
  public static BankAccountType selectBankAccountTypeBySortOrder(Main main){
    // select * from scm_bank_account_type where sort_order = (select min(sort_order) from scm_bank_account_type)
    return (BankAccountType) AppService.single(main, BankAccountType.class, "select * from scm_bank_account_type where sort_order = ? order by title limit 1 ",new Object[]{1});
  }
  
  /**
   * Validate insert.
   *
   * @param main
   * @param bankAccountType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, BankAccountType bankAccountType) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_bank_account_type where upper(title)=?", new Object[]{StringUtil.toUpperCase(bankAccountType.getTitle())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param bankAccountType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, BankAccountType bankAccountType) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_bank_account_type where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(bankAccountType.getTitle()), bankAccountType.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }
}
