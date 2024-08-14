/*
 * @(#)CurrencyService.java	1.0 Fri Apr 28 12:24:49 IST 2017 
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
import spica.scm.domain.Currency;
import spica.scm.validate.CurrencyIs;
import wawo.entity.core.SqlSearch;

/**
 * CurrencyService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 28 12:24:49 IST 2017
 */
public abstract class CurrencyService {

  /**
   * Currency paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCurrencySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_currency", Currency.class, main);
    sql.main("select t1.id,t1.title,t1.code,t1.symbol,t1.sort_order,t1.created_by,t1.modified_by,t1.created_at,t1.modified_at from fin_currency t1"); //Main query
    sql.count("select count(t1.id) as total from fin_currency t1"); //Count query
    sql.join(""); //Join Query
    sql.orderBy("t1.sort_order,upper(t1.title)");

    sql.string(new String[]{"t1.title", "t1.created_by", "t1.modified_by", "t1.code", "t1.symbol"}); //String search or sort fields
    sql.number(new String[]{"t1.id", "t1.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.created_at", "t1.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Currency.
   *
   * @param main
   * @return List of Currency
   */
  public static final List<Currency> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCurrencySqlPaged(main));
  }

//  /**
//   * Return list of Currency based on condition
//   * @param main
//   * @return List<Currency>
//   */
//  public static final List<Currency> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCurrencySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Currency by key.
   *
   * @param main
   * @param currency
   * @return Currency
   */
  public static final Currency selectByPk(Main main, Currency currency) {
    return (Currency) AppService.find(main, Currency.class, currency.getId());
  }

  /**
   * Insert Currency.
   *
   * @param main
   * @param currency
   */
  public static final void insert(Main main, Currency currency) {
    CurrencyIs.insertAble(main, currency);  //Validating
    AppService.insert(main, currency);

  }

  /**
   * Update Currency by key.
   *
   * @param main
   * @param currency
   * @return Currency
   */
  public static final Currency updateByPk(Main main, Currency currency) {
    CurrencyIs.updateAble(main, currency); //Validating
    return (Currency) AppService.update(main, currency);
  }

  /**
   * Insert or update Currency
   *
   * @param main
   * @param currency
   */
  public static void insertOrUpdate(Main main, Currency currency) {
    if (currency.getId() == null) {
      insert(main, currency);
    } else {
      updateByPk(main, currency);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param currency
   */
  public static void clone(Main main, Currency currency) {
    currency.setId(null); //Set to null for insert
    insert(main, currency);
  }

  /**
   * Delete Currency.
   *
   * @param main
   * @param currency
   */
  public static final void deleteByPk(Main main, Currency currency) {
    CurrencyIs.deleteAble(main, currency); //Validation
    AppService.delete(main, Currency.class, currency.getId());
  }

  /**
   * Delete Array of Currency.
   *
   * @param main
   * @param currency
   */
  public static final void deleteByPkArray(Main main, Currency[] currency) {
    for (Currency e : currency) {
      deleteByPk(main, e);
    }
  }

  public static List loadCurrencyList(Main main) {
    SqlSearch sql = AppService.sqlSearch("fin_currency", Currency.class, main);
    sql.main("select * from fin_currency "); //Main query  
    return AppService.listAll(main, sql);
  }

}
