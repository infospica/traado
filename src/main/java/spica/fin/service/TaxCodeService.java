/*
 * @(#)TaxCodeService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.constant.AccountingConstant;
import spica.scm.domain.Company;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.TaxCode;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * TaxCodeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TaxCodeService {

  /**
   * TaxCode paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTaxCodeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_tax_code", TaxCode.class, main);
    sql.main("select scm_tax_code.id,scm_tax_code.code,scm_tax_code.state_id,scm_tax_code.rate_percentage,scm_tax_code.sort_order,scm_tax_code.display_color,scm_tax_code.tax_head_id,scm_tax_code.abatement_percentage,scm_tax_code.status_id,scm_tax_code.company_id,scm_tax_code.created_at,scm_tax_code.modified_at,scm_tax_code.created_by,scm_tax_code.modified_by,scm_tax_code.parent_id,scm_tax_code.country_id from scm_tax_code scm_tax_code "); //Main query
    sql.count("select count(scm_tax_code.id) as total from scm_tax_code scm_tax_code "); //Count query
    sql.join("left outer join scm_state scm_tax_codestate_id on (scm_tax_codestate_id.id = scm_tax_code.state_id) left outer join scm_tax_head scm_tax_codetax_head_id on (scm_tax_codetax_head_id.id = scm_tax_code.tax_head_id) left outer join scm_status scm_tax_codestatus_id on (scm_tax_codestatus_id.id = scm_tax_code.status_id) left outer join scm_company scm_tax_codecompany_id on (scm_tax_codecompany_id.id = scm_tax_code.company_id)"); //Join Query

    sql.string(new String[]{"scm_tax_code.code", "scm_tax_codestate_id.state_name", "scm_tax_code.display_color", "scm_tax_codetax_head_id.head", "scm_tax_codestatus_id.title", "scm_tax_codecompany_id.company_name", "scm_tax_code.created_by", "scm_tax_code.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_tax_code.id", "scm_tax_code.rate_percentage", "scm_tax_code.sort_order", "scm_tax_code.abatement_percentage"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_tax_code.created_at", "scm_tax_code.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TaxCode.
   *
   * @param main
   * @return List of TaxCode
   */
  public static final List<TaxCode> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTaxCodeSqlPaged(main));
  }

  /**
   * Return List of TaxCode.
   *
   * @param main
   * @param company
   * @return List of TaxCode
   */
  public static final List<TaxCode> listPaged(Main main, Company company) {
    if (company != null && company.getId() != null) {
      SqlPage sql = getTaxCodeSqlPaged(main);
      sql.cond("where scm_tax_code.country_id = ? ");
      sql.param(company.getCountryId().getId());
      return AppService.listPagedJpa(main, sql);
    }
    return null;
  }

  /**
   * Select TaxCode by key.
   *
   * @param main
   * @param taxCode
   * @return TaxCode
   */
  public static final TaxCode selectByPk(Main main, TaxCode taxCode) {
    return (TaxCode) AppService.find(main, TaxCode.class, taxCode.getId());
  }

  /**
   * Insert TaxCode.
   *
   * @param main
   * @param taxCode
   */
//  public static final void insert(Main main, TaxCode taxCode) {
//    
//    //saveTaxCodeAccountingLedger(main, taxCode);    
//  }
  /**
   * Update TaxCode by key.
   *
   * @param main
   * @param taxCode
   * @return TaxCode
   */
//  public static final TaxCode updateByPk(Main main, TaxCode taxCode) {
//    TaxCodeIs.updateAble(main, taxCode); //Validating
//    //saveTaxCodeAccountingLedger(main, taxCode);    
//    return (TaxCode) AppService.update(main, taxCode);
//  }
  /**
   * Insert or update TaxCode
   *
   * @param main
   * @param taxCode
   */
  public static void insertOrUpdate(Main main, TaxCode taxCode) {
    if (taxCode.getTaxHeadId().getId() == AccountingConstant.TAX_HEAD_GST.getId()) {
      taxCode.setTaxType(AccountingConstant.TAX_TYPE_IGST);
    }
    if (taxCode.getId() == null) {
      insertAble(main, taxCode);  //Validating
      AppService.insert(main, taxCode);
      if (taxCode.getTaxHeadId().getId() == AccountingConstant.TAX_HEAD_GST.getId()) {
        setTaxCodeValues(main, "CGST", AccountingConstant.TAX_TYPE_CGST, taxCode);
        setTaxCodeValues(main, "SGST", AccountingConstant.TAX_TYPE_SGST, taxCode);
      }
    } else {
      updateAble(main, taxCode); //Validating
      taxCode = (TaxCode) AppService.update(main, taxCode);
      if (taxCode.getTaxHeadId().getId() == AccountingConstant.TAX_HEAD_GST.getId()) {
        long count = AppService.count(main, "select count(id) from scm_tax_code where parent_id = ?", new Object[]{taxCode.getId()});
        if (count == 0) {
          setTaxCodeValues(main, "CGST", AccountingConstant.TAX_TYPE_CGST, taxCode);
          setTaxCodeValues(main, "SGST", AccountingConstant.TAX_TYPE_SGST, taxCode);
        } else {
          updateTaxcode(main, "CGST", AccountingConstant.TAX_TYPE_CGST, taxCode);
          updateTaxcode(main, "SGST", AccountingConstant.TAX_TYPE_SGST, taxCode);
        }
      }
    }
  }

  public static final void updateTaxcode(Main main, String title, int taxType, TaxCode taxCode) {
    Double ratePercentage = taxCode.getRatePercentage() / 2;
    main.clear();
    main.param(title + " " + ratePercentage + " %");
    main.param(ratePercentage);
    main.param(taxCode.getId());
    main.param(taxType);
    AppService.updateSql(main, TaxCode.class, "update scm_tax_code set code=?,rate_percentage=? where parent_id = ? and tax_type = ?", false);
  }

  public static final List<TaxCode> selectChildTaxCode(Main main, TaxCode taxCode, Company company) {
    return AppService.list(main, TaxCode.class, "select * from scm_tax_code where parent_id = ? and company_id = ?", new Object[]{taxCode.getId(), company.getId()});
  }

  public static final TaxCode selectTaxCodeById(Main main, Integer taxCodeId) {
    return (TaxCode) AppService.single(main, TaxCode.class, "select * from scm_tax_code where id = ? ", new Object[]{taxCodeId});
  }

  /**
   *
   * @param main
   * @param title
   * @param type
   * @param taxCode
   */
  private static void setTaxCodeValues(Main main, String title, int type, TaxCode taxCode) {
    Double ratePercentage = taxCode.getRatePercentage() / 2;
    TaxCode tc = new TaxCode();
    tc.setParentId(taxCode);
    tc.setCode(title + " " + ratePercentage + " %");
    tc.setRatePercentage(ratePercentage);
    tc.setAbatementPercentage(taxCode.getAbatementPercentage());
    tc.setCompanyId(taxCode.getCompanyId());
    tc.setCountryId(taxCode.getCountryId());
    tc.setDisplayColor(taxCode.getDisplayColor());
    tc.setSortOrder(taxCode.getSortOrder());
    tc.setStateId(taxCode.getStateId());
    tc.setStatusId(taxCode.getStatusId());
    tc.setTaxHeadId(taxCode.getTaxHeadId());
    tc.setTaxType(type);
    AppService.insert(main, tc);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param taxCode
   */
  public static void clone(Main main, TaxCode taxCode) {
    taxCode.setId(null); //Set to null for insert
    insertOrUpdate(main, taxCode);
  }

  /**
   * Delete TaxCode.
   *
   * @param main
   * @param taxCode
   */
  public static final void deleteByPk(Main main, TaxCode taxCode) {
    deleteAble(main, taxCode); //Validation
    AppService.deleteSql(main, TaxCode.class, "delete from scm_tax_code where parent_id = ? ", new Object[]{taxCode.getId()});
    AppService.delete(main, TaxCode.class, taxCode.getId());
  }

  /**
   * Delete Array of TaxCode.
   *
   * @param main
   * @param taxCode
   */
  public static final void deleteByPkArray(Main main, TaxCode[] taxCode) {
    for (TaxCode e : taxCode) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param taxCode
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, TaxCode taxCode) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param taxCode
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, TaxCode taxCode) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_tax_code where country_id = ? and upper(code) = ? and rate_percentage = ? and tax_head_id = ?",
            new Object[]{taxCode.getCountryId().getId(), StringUtil.toUpperCase(taxCode.getCode()), taxCode.getRatePercentage(), taxCode.getTaxHeadId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param taxCode
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, TaxCode taxCode) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_tax_code where country_id = ? and upper(code)=? and rate_percentage = ? and id <> ? and tax_head_id = ?",
            new Object[]{taxCode.getCountryId().getId(), StringUtil.toUpperCase(taxCode.getCode()), taxCode.getRatePercentage(), taxCode.getId(), taxCode.getTaxHeadId().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  public static final List<TaxCode> selectIgstTaxes(Main main) {
    String sql = "select id,code,rate_percentage from scm_tax_code where tax_head_id = ? AND tax_type=?";
    return AppService.list(main, TaxCode.class, sql, new Object[]{AccountingConstant.TAX_HEAD_GST.getId(), AccountingConstant.TAX_TYPE_IGST});
  }

}
