/*
 * @(#)CountryService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
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
import spica.scm.domain.Country;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * CountryService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class CountryService {

  /**
   * Country paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCountrySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_country", Country.class, main);
    sql.main("select scm_country.id,scm_country.country_name,scm_country.country_code,scm_country.sort_order,scm_country.created_by,scm_country.modified_by,scm_country.created_at,scm_country.modified_at from scm_country scm_country"); //Main query
    sql.count("select count(scm_country.id) as total from scm_country scm_country"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_country.country_name", "scm_country.country_code", "scm_country.created_by", "scm_country.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_country.id", "scm_country.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_country.created_at", "scm_country.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Country.
   *
   * @param main
   * @return List of Country
   */
  public static final List<Country> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCountrySqlPaged(main));
  }

//  /**
//   * Return list of Country based on condition
//   * @param main
//   * @return List<Country>
//   */
//  public static final List<Country> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCountrySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Country by key.
   *
   * @param main
   * @param country
   * @return Country
   */
  public static final Country selectByPk(Main main, Country country) {
    return (Country) AppService.find(main, Country.class, country.getId());
  }

  /**
   * Insert Country.
   *
   * @param main
   * @param country
   */
  public static final void insert(Main main, Country country) {
    insertAble(main, country);  //Validating    
    AppService.insert(main, country);
  }

  /**
   * Update Country by key.
   *
   * @param main
   * @param country
   * @return Country
   */
  public static final Country updateByPk(Main main, Country country) {
    updateAble(main, country); //Validating   
    return (Country) AppService.update(main, country);
  }

  /**
   * Insert or update Country
   *
   * @param main
   * @param country
   */
  public static void insertOrUpdate(Main main, Country country) {
    if (country.getId() == null) {
      toUpperNameAndCode(country);
      insert(main, country);
//      updateDefaultCountry(main, country);
    } else {
      toUpperNameAndCode(country);
      updateByPk(main, country);
//      updateDefaultCountry(main, country);
    }
  }

  private static void toUpperNameAndCode(Country country) {
    country.setCountryName(country.getCountryName().toUpperCase());
    country.setCountryCode(country.getCountryCode().toUpperCase());
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param country
   */
  public static void clone(Main main, Country country) {
    country.setId(null); //Set to null for insert
    insert(main, country);
  }

  /**
   * Delete Country.
   *
   * @param main
   * @param country
   */
  public static final void deleteByPk(Main main, Country country) {
    deleteAble(main, country); //Validation
    AppService.delete(main, Country.class, country.getId());
  }

  /**
   * Delete Array of Country.
   *
   * @param main
   * @param country
   */
  public static final void deleteByPkArray(Main main, Country[] country) {
    for (Country e : country) {
      deleteByPk(main, e);
    }
  }
//  private static void updateDefaultCountry(Main main, Country country) {
//    if (country.getIsDefault() == 1) {
//      AppService.updateSql(main, Country.class, "update scm_country set is_default = 0 where id <> " + country.getId(), false);
//    } 
//  }
//  

  public static Country selectDefaultCountry(Main main) {
    return (Country) AppService.single(main, Country.class, "select * from scm_country where sort_order = ?", new Object[]{1});
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param country
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, Country country) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param country
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Country country) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_country where upper(country_name)=?", new Object[]{StringUtil.toUpperCase(country.getCountryName())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("name"));
    }
    if (AppService.exist(main, "select id from scm_country where upper(country_code)=?", new Object[]{StringUtil.toUpperCase(country.getCountryCode())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("countryCode"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param country
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Country country) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_country where upper(country_name)=? and id !=?", new Object[]{StringUtil.toUpperCase(country.getCountryName()), country.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("name"));
    }
    if (AppService.exist(main, "select id from scm_country where upper(country_code)=? and id !=?", new Object[]{StringUtil.toUpperCase(country.getCountryCode()), country.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("countryCode"));
    }
  }
}
