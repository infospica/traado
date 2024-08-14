/*
 * @(#)BrandService.java	1.0 Mon Aug 21 14:03:19 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.Brand;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * BrandService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 21 14:03:19 IST 2017
 */
public abstract class BrandService {

  /**
   * Brand paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getBrandSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_brand", Brand.class, main);
    sql.main("select t1.id,t1.company_id,t1.name,t1.code,t1.description,t1.created_by,t1.modified_by,t1.created_at,t1.modified_at from scm_brand t1 "); //Main query
    sql.count("select count(t1.id) as total from scm_brand t1 "); //Count query
    sql.join("left outer join scm_company t4 on (t4.id = t1.company_id)"); //Join Query

    sql.string(new String[]{"t1.name", "t1.code", "t1.description", "t1.created_by", "t1.modified_by"}); //String search or sort fields
    sql.number(new String[]{"t1.id"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.created_at", "t1.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Brand.
   *
   * @param main
   * @return List of Brand
   */
  public static final List<Brand> listPaged(Main main) {
    return AppService.listPagedJpa(main, getBrandSqlPaged(main));
  }

  public static final List<Brand> listPagedByCompany(Main main, Company company) {
    SqlPage sql = getBrandSqlPaged(main);
    sql.cond("where t1.company_id = ?");
    main.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of Brand based on condition
//   * @param main
//   * @return List<Brand>
//   */
//  public static final List<Brand> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getBrandSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Brand by key.
   *
   * @param main
   * @param brand
   * @return Brand
   */
  public static final Brand selectByPk(Main main, Brand brand) {
    return (Brand) AppService.find(main, Brand.class, brand.getId());
  }

  /**
   * Insert Brand.
   *
   * @param main
   * @param brand
   */
  public static final void insert(Main main, Brand brand) {
    insertAble(main, brand);  //Validating
    AppService.insert(main, brand);

  }

  /**
   * Update Brand by key.
   *
   * @param main
   * @param brand
   * @return Brand
   */
  public static final Brand updateByPk(Main main, Brand brand) {
    updateAble(main, brand); //Validating
    return (Brand) AppService.update(main, brand);
  }

  /**
   * Insert or update Brand
   *
   * @param main
   * @param brand
   */
  public static void insertOrUpdate(Main main, Brand brand) {
    if (brand.getId() == null) {
      insert(main, brand);
    } else {
      updateByPk(main, brand);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param brand
   */
  public static void clone(Main main, Brand brand) {
    brand.setId(null); //Set to null for insert
    insert(main, brand);
  }

  /**
   * Delete Brand.
   *
   * @param main
   * @param brand
   */
  public static final void deleteByPk(Main main, Brand brand) {
    deleteAble(main, brand); //Validation
    AppService.delete(main, Brand.class, brand.getId());
  }

  /**
   * Delete Array of Brand.
   *
   * @param main
   * @param brand
   */
  public static final void deleteByPkArray(Main main, Brand[] brand) {
    for (Brand e : brand) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param brand
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, Brand brand) throws UserMessageException {
  }

  /**
   * Validate insert.
   *
   * @param main
   * @param brand
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Brand brand) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_brand where company_id = ? and upper(code)=?", new Object[]{brand.getCompanyId().getId(), StringUtil.toUpperCase(brand.getCode())})) {
      throw new UserMessageException("code.exist", ValidateUtil.getFieldName("brandCode"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param brand
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Brand brand) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_brand where company_id = ? and upper(code)=? and id !=?", new Object[]{brand.getCompanyId().getId(), StringUtil.toUpperCase(brand.getCode()), brand.getId()})) {
      throw new UserMessageException("code.exist", ValidateUtil.getFieldName("brandCode"));
    }
  }
}
