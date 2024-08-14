/*
 * @(#)BankService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
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
import spica.scm.domain.Bank;
import spica.scm.domain.Company;
import spica.scm.validate.ValidateUtil;

import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * BankService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class BankService {

  /**
   * Bank paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getBankSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_bank", Bank.class, main);
    sql.main("select scm_bank.id,scm_bank.name,scm_bank.sort_order,scm_bank.created_by,scm_bank.modified_by,scm_bank.created_at,scm_bank.modified_at,scm_bank.country_id from scm_bank scm_bank "); //Main query
    sql.count("select count(scm_bank.id) from scm_bank scm_bank "); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_bank.name", "scm_bank.created_by", "scm_bank.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_bank.id", "scm_bank.sort_order"}); //Number search or sort fields
    sql.date(new String[]{"scm_bank.created_at", "scm_bank.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Bank.
   *
   * @param main
   * @return List of Bank
   */
  public static final List<Bank> listPaged(Main main, Company company) {
    SqlPage sql = getBankSqlPaged(main);
    sql.cond("where scm_bank.country_id = ?");
    sql.param(company.getCountryId().getId());
    return AppService.listPagedJpa(main, sql);
    //return AppService.listPagedJpa(main, getBankSqlPaged(main));
  }

//  /**
//   * Return list of Bank based on condition
//   * @param main
//   * @return List<Bank>
//   */
//  public static final List<Bank> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getBankSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Bank by key.
   *
   * @param main
   * @param bank
   * @return Bank
   */
  public static final Bank selectByPk(Main main, Bank bank) {
    return (Bank) AppService.find(main, Bank.class, bank.getId());
  }

  /**
   * Insert Bank.
   *
   * @param main
   * @param bank
   */
  public static final void insert(Main main, Bank bank) {
    insertAble(main, bank);  //Validating
    AppService.insert(main, bank);

  }

  /**
   * Update Bank by key.
   *
   * @param main
   * @param bank
   * @return Bank
   */
  public static final Bank updateByPk(Main main, Bank bank) {
    updateAble(main, bank); //Validating
    return (Bank) AppService.update(main, bank);
  }

  /**
   * Insert or update Bank
   *
   * @param main
   * @param bank
   */
  public static void insertOrUpdate(Main main, Bank bank) {
    if (bank.getId() == null) {
      insert(main, bank);
    } else {
      updateByPk(main, bank);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param bank
   */
  public static void clone(Main main, Bank bank) {
    bank.setId(null); //Set to null for insert
    insert(main, bank);
  }

  /**
   * Delete Bank.
   *
   * @param main
   * @param bank
   */
  public static final void deleteByPk(Main main, Bank bank) {
    deleteAble(main, bank); //Validation
    AppService.delete(main, Bank.class, bank.getId());
  }

  /**
   * Delete Array of Bank.
   *
   * @param main
   * @param bank
   */
  public static final void deleteByPkArray(Main main, Bank[] bank) {
    for (Bank e : bank) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param bank
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, Bank bank) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param bank
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Bank bank) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_bank where country_id = ? and  upper(name)=?", new Object[]{bank.getCountryTd().getId(), StringUtil.toUpperCase(bank.getName())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("name"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param bank
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Bank bank) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_bank where country_id = ? and  upper(name)=? and id !=?", new Object[]{bank.getCountryTd().getId(), StringUtil.toUpperCase(bank.getName()), bank.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("name"));
    }
  }
}
