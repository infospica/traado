/*
 * @(#)TaxHeadService.java	1.0 Sat Jul 22 17:33:14 IST 2017 
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
import spica.fin.domain.TaxHead;
import wawo.entity.core.UserMessageException;

/**
 * TaxHeadService
 *
 * @author	Spirit 1.2
 * @version	1.0, Sat Jul 22 17:33:14 IST 2017
 */
public abstract class TaxHeadService {

  public static final String[] APPLIED_ON = {"Product Commodity", "Product Category", "HSN & SAC"};
  public static final String[] COMPUTED_ON = {"MRP", "Net Value", "Gross Value", "Tax"};
  public static final Integer PRODUCT_COMMODITY = 1;
  public static final Integer PRODUCT_CATEGORY = 2;
  public static final Integer HSN_AND_SAC = 3;
  public static final Integer COMPUTED_ON_MRP = 1;
  public static final Integer COMPUTED_ON_NET_VALUE = 2;
  public static final Integer COMPUTED_ON_GROSS_VALUE = 3;
  public static final Integer COMPUTED_ON_TAX = 4;
  public static final Integer TAX_HEAD_GST = 4;
  public static final Integer TAX_HEAD_GST_CESS = 8;
  public static final Integer TAX_HEAD_TDS = 10;

  /**
   * TaxHead paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTaxHeadSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_tax_head", TaxHead.class, main);
    sql.main("select scm_tax_head.id,scm_tax_head.country_tax_regime_id,scm_tax_head.head,scm_tax_head.applied_on,scm_tax_head.computed_on,scm_tax_head.abatement_percentage,scm_tax_head.sort_order,scm_tax_head.created_at,scm_tax_head.modified_at,scm_tax_head.created_by,scm_tax_head.modified_by,scm_tax_head.status_id,scm_tax_head.applied_region from scm_tax_head scm_tax_head "); //Main query
    sql.count("select count(scm_tax_head.id) as total from scm_tax_head scm_tax_head "); //Count query
    sql.join("left outer join scm_country_tax_regime scm_tax_headcountry_tax_id on (scm_tax_headcountry_tax_id.id = scm_tax_head.country_tax_regime_id) left outer join scm_status scm_tax_headstatus_id on (scm_tax_headstatus_id.id = scm_tax_head.status_id)"); //Join Query

    sql.string(new String[]{"scm_tax_head.head", "scm_tax_head.created_by", "scm_tax_head.modified_by", "scm_tax_headstatus_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_tax_head.id", "scm_tax_headcountry_tax_id.regime", "scm_tax_head.applied_on", "scm_tax_head.computed_on", "scm_tax_head.abatement_percentage", "scm_tax_head.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_tax_head.created_at", "scm_tax_head.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TaxHead.
   *
   * @param main
   * @return List of TaxHead
   */
  public static final List<TaxHead> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTaxHeadSqlPaged(main));
  }

//  /**
//   * Return list of TaxHead based on condition
//   * @param main
//   * @return List<TaxHead>
//   */
//  public static final List<TaxHead> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTaxHeadSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TaxHead by key.
   *
   * @param main
   * @param taxHead
   * @return TaxHead
   */
  public static final TaxHead selectByPk(Main main, TaxHead taxHead) {
    return (TaxHead) AppService.find(main, TaxHead.class, taxHead.getId());
  }

  /**
   * Insert TaxHead.
   *
   * @param main
   * @param taxHead
   */
  public static final void insert(Main main, TaxHead taxHead) {
    insertAble(main, taxHead);  //Validating
    AppService.insert(main, taxHead);

  }

  /**
   * Update TaxHead by key.
   *
   * @param main
   * @param taxHead
   * @return TaxHead
   */
  public static final TaxHead updateByPk(Main main, TaxHead taxHead) {
    updateAble(main, taxHead); //Validating
    return (TaxHead) AppService.update(main, taxHead);
  }

  /**
   * Insert or update TaxHead
   *
   * @param main
   * @param taxHead
   */
  public static void insertOrUpdate(Main main, TaxHead taxHead) {
    if (taxHead.getId() == null) {
      insert(main, taxHead);
    } else {
      updateByPk(main, taxHead);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param taxHead
   */
  public static void clone(Main main, TaxHead taxHead) {
    taxHead.setId(null); //Set to null for insert
    insert(main, taxHead);
  }

  /**
   * Delete TaxHead.
   *
   * @param main
   * @param taxHead
   */
  public static final void deleteByPk(Main main, TaxHead taxHead) {
    deleteAble(main, taxHead); //Validation
    AppService.delete(main, TaxHead.class, taxHead.getId());
  }

  /**
   * Delete Array of TaxHead.
   *
   * @param main
   * @param taxHead
   */
  public static final void deleteByPkArray(Main main, TaxHead[] taxHead) {
    for (TaxHead e : taxHead) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param taxHead
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, TaxHead taxHead) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param taxHead
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, TaxHead taxHead) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_tax_head where upper(head)=?", new Object[]{StringUtil.toUpperCase(taxHead.getHead())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param taxHead
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, TaxHead taxHead) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_tax_head where upper(head)=? and id !=?", new Object[]{StringUtil.toUpperCase(taxHead.getHead()), taxHead.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

}
