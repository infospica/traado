/*
 * @(#)TaxProcessorService.java	1.0 Mon Jul 24 18:06:26 IST 2017 
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
import spica.fin.domain.TaxProcessor;
import wawo.entity.core.UserMessageException;

/**
 * TaxProcessorService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jul 24 18:06:26 IST 2017
 */
public abstract class TaxProcessorService {

  /**
   * TaxProcessor paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTaxProcessorSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_tax_processor", TaxProcessor.class, main);
    sql.main("select scm_tax_processor.id,scm_tax_processor.processor_class,scm_tax_processor.description,scm_tax_processor.title,scm_tax_processor.status_id,scm_tax_processor.created_at,scm_tax_processor.modified_at,scm_tax_processor.created_by,scm_tax_processor.modified_by from scm_tax_processor scm_tax_processor "); //Main query
    sql.count("select count(scm_tax_processor.id) as total from scm_tax_processor scm_tax_processor "); //Count query
    sql.join("left outer join scm_status scm_tax_processorstatus_id on (scm_tax_processorstatus_id.id = scm_tax_processor.status_id)"); //Join Query

    sql.string(new String[]{"scm_tax_processor.processor_class", "scm_tax_processor.description", "scm_tax_processor.created_by", "scm_tax_processor.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_tax_processor.id", "scm_tax_processorcountry_tax_regime_id.regime"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_tax_processor.created_at", "scm_tax_processor.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TaxProcessor.
   *
   * @param main
   * @return List of TaxProcessor
   */
  public static final List<TaxProcessor> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTaxProcessorSqlPaged(main));
  }

//  /**
//   * Return list of TaxProcessor based on condition
//   * @param main
//   * @return List<TaxProcessor>
//   */
//  public static final List<TaxProcessor> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTaxProcessorSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TaxProcessor by key.
   *
   * @param main
   * @param taxProcessor
   * @return TaxProcessor
   */
  public static final TaxProcessor selectByPk(Main main, TaxProcessor taxProcessor) {
    return (TaxProcessor) AppService.find(main, TaxProcessor.class, taxProcessor.getId());
  }

  /**
   * Insert TaxProcessor.
   *
   * @param main
   * @param taxProcessor
   */
  public static final void insert(Main main, TaxProcessor taxProcessor) {
    insertAble(main, taxProcessor);  //Validating
    AppService.insert(main, taxProcessor);

  }

  /**
   * Update TaxProcessor by key.
   *
   * @param main
   * @param taxProcessor
   * @return TaxProcessor
   */
  public static final TaxProcessor updateByPk(Main main, TaxProcessor taxProcessor) {
    updateAble(main, taxProcessor); //Validating
    return (TaxProcessor) AppService.update(main, taxProcessor);
  }

  /**
   * Insert or update TaxProcessor
   *
   * @param main
   * @param taxProcessor
   */
  public static void insertOrUpdate(Main main, TaxProcessor taxProcessor) {
    if (taxProcessor.getId() == null) {
      insert(main, taxProcessor);
    } else {
      updateByPk(main, taxProcessor);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param taxProcessor
   */
  public static void clone(Main main, TaxProcessor taxProcessor) {
    taxProcessor.setId(null); //Set to null for insert
    insert(main, taxProcessor);
  }

  /**
   * Delete TaxProcessor.
   *
   * @param main
   * @param taxProcessor
   */
  public static final void deleteByPk(Main main, TaxProcessor taxProcessor) {
    deleteAble(main, taxProcessor); //Validation
    AppService.delete(main, TaxProcessor.class, taxProcessor.getId());
  }

  /**
   * Delete Array of TaxProcessor.
   *
   * @param main
   * @param taxProcessor
   */
  public static final void deleteByPkArray(Main main, TaxProcessor[] taxProcessor) {
    for (TaxProcessor e : taxProcessor) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param taxProcessor
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, TaxProcessor taxProcessor) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param taxProcessor
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, TaxProcessor taxProcessor) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_tax_processor where upper(processor_name)=?", new Object[]{StringUtil.toUpperCase(taxProcessor.getProcessorName())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param taxProcessor
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, TaxProcessor taxProcessor) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_tax_processor where upper(processor_name)=? and id !=?", new Object[]{StringUtil.toUpperCase(taxProcessor.getProcessorName()), taxProcessor.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
