/*
 * @(#)CountryTaxRegimeService.java	1.0 Mon Jul 24 18:06:26 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Country;
import spica.scm.domain.CountryTaxRegime;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;

/**
 * CountryTaxRegimeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jul 24 18:06:26 IST 2017
 */
public abstract class CountryTaxRegimeService {

  public static final String[] REGIME = {"VAT", "GST", "ST"};

  /**
   * CountryTaxRegime paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCountryTaxRegimeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_country_tax_regime", CountryTaxRegime.class, main);
    sql.main("select scm_country_tax_regime.id,scm_country_tax_regime.country_id,scm_country_tax_regime.regime,scm_country_tax_regime.valid_from,scm_country_tax_regime.valid_upto,scm_country_tax_regime.sort_order,scm_country_tax_regime.title,scm_country_tax_regime.tax_processor_id,scm_country_tax_regime.created_at,scm_country_tax_regime.modified_at,scm_country_tax_regime.created_by,scm_country_tax_regime.modified_by from scm_country_tax_regime scm_country_tax_regime "); //Main query
    sql.count("select count(scm_country_tax_regime.id) as total from scm_country_tax_regime scm_country_tax_regime "); //Count query
    sql.join("left outer join scm_country scm_country_tax_regimecountry_id on (scm_country_tax_regimecountry_id.id = scm_country_tax_regime.country_id) left outer join scm_tax_processor scm_country_tax_regimetax_processor_id on(scm_country_tax_regimetax_processor_id.id = scm_country_tax_regime.tax_processor_id)"); //Join Query

    sql.string(new String[]{"scm_country_tax_regimecountry_id.country_name", "scm_country_tax_regime.title", "scm_country_tax_regime.created_by", "scm_country_tax_regime.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_country_tax_regime.id", "scm_country_tax_regime.regime", "scm_country_tax_regime.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_country_tax_regime.valid_from", "scm_country_tax_regime.valid_upto", "scm_country_tax_regime.created_at", "scm_country_tax_regime.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CountryTaxRegime.
   *
   * @param main
   * @return List of CountryTaxRegime
   */
  public static final List<CountryTaxRegime> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCountryTaxRegimeSqlPaged(main));
  }

//  /**
//   * Return list of CountryTaxRegime based on condition
//   * @param main
//   * @return List<CountryTaxRegime>
//   */
//  public static final List<CountryTaxRegime> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCountryTaxRegimeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select CountryTaxRegime by key.
   *
   * @param main
   * @param countryTaxRegime
   * @return CountryTaxRegime
   */
  public static final CountryTaxRegime selectByPk(Main main, CountryTaxRegime countryTaxRegime) {
    return (CountryTaxRegime) AppService.find(main, CountryTaxRegime.class, countryTaxRegime.getId());
  }

  /**
   * Insert CountryTaxRegime.
   *
   * @param main
   * @param countryTaxRegime
   */
  public static final void insert(Main main, CountryTaxRegime countryTaxRegime) {
    insertAble(main, countryTaxRegime);  //Validating
    AppService.insert(main, countryTaxRegime);

  }

  /**
   * Update CountryTaxRegime by key.
   *
   * @param main
   * @param countryTaxRegime
   * @return CountryTaxRegime
   */
  public static final CountryTaxRegime updateByPk(Main main, CountryTaxRegime countryTaxRegime) {
    updateAble(main, countryTaxRegime); //Validating
    return (CountryTaxRegime) AppService.update(main, countryTaxRegime);
  }

  /**
   * Insert or update CountryTaxRegime
   *
   * @param main
   * @param countryTaxRegime
   */
  public static void insertOrUpdate(Main main, CountryTaxRegime countryTaxRegime) {
    if (countryTaxRegime.getId() == null) {
      insert(main, countryTaxRegime);
    } else {
      updateByPk(main, countryTaxRegime);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param countryTaxRegime
   */
  public static void clone(Main main, CountryTaxRegime countryTaxRegime) {
    countryTaxRegime.setId(null); //Set to null for insert
    insert(main, countryTaxRegime);
  }

  /**
   * Delete CountryTaxRegime.
   *
   * @param main
   * @param countryTaxRegime
   */
  public static final void deleteByPk(Main main, CountryTaxRegime countryTaxRegime) {
    deleteAble(main, countryTaxRegime); //Validation
    AppService.delete(main, CountryTaxRegime.class, countryTaxRegime.getId());
  }

  /**
   * Delete Array of CountryTaxRegime.
   *
   * @param main
   * @param countryTaxRegime
   */
  public static final void deleteByPkArray(Main main, CountryTaxRegime[] countryTaxRegime) {
    for (CountryTaxRegime e : countryTaxRegime) {
      deleteByPk(main, e);
    }
  }

  public static final CountryTaxRegime selectRegimeByCountry(Main main, Country country) {
    CountryTaxRegime ctr = (CountryTaxRegime) AppService.single(main, CountryTaxRegime.class, "select * from scm_country_tax_regime where country_id = ? limit 1", new Object[]{country.getId()});
    return ctr;
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param countryTaxRegime
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, CountryTaxRegime countryTaxRegime) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param countryTaxRegime
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, CountryTaxRegime countryTaxRegime) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_country_tax_regime where upper(regime)=?", new Object[]{StringUtil.toUpperCase(countryTaxRegime.getRegime())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param countryTaxRegime
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, CountryTaxRegime countryTaxRegime) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_country_tax_regime where upper(regime)=? and id !=?", new Object[]{StringUtil.toUpperCase(countryTaxRegime.getRegime()), countryTaxRegime.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
