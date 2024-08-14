/*
 * @(#)SalesInvoiceStatusService.java	1.0 Fri Dec 23 10:28:18 IST 2016 
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
import spica.scm.domain.SalesInvoiceStatus;
import wawo.entity.core.UserMessageException;

/**
 * SalesInvoiceStatusService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:18 IST 2016
 */
public abstract class SalesInvoiceStatusService {

  /**
   * SalesInvoiceStatus paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesInvoiceStatusSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_invoice_status", SalesInvoiceStatus.class, main);
    sql.main("select scm_sales_invoice_status.id,scm_sales_invoice_status.title,scm_sales_invoice_status.sort_order,scm_sales_invoice_status.created_by,scm_sales_invoice_status.modified_by,scm_sales_invoice_status.created_at,scm_sales_invoice_status.modified_at from scm_sales_invoice_status scm_sales_invoice_status"); //Main query
    sql.count("select count(scm_sales_invoice_status.id) as total from scm_sales_invoice_status scm_sales_invoice_status"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_sales_invoice_status.title", "scm_sales_invoice_status.created_by", "scm_sales_invoice_status.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_invoice_status.id", "scm_sales_invoice_status.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_invoice_status.created_at", "scm_sales_invoice_status.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesInvoiceStatus.
   *
   * @param main
   * @return List of SalesInvoiceStatus
   */
  public static final List<SalesInvoiceStatus> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesInvoiceStatusSqlPaged(main));
  }

//  /**
//   * Return list of SalesInvoiceStatus based on condition
//   * @param main
//   * @return List<SalesInvoiceStatus>
//   */
//  public static final List<SalesInvoiceStatus> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesInvoiceStatusSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesInvoiceStatus by key.
   *
   * @param main
   * @param salesInvoiceStatus
   * @return SalesInvoiceStatus
   */
  public static final SalesInvoiceStatus selectByPk(Main main, SalesInvoiceStatus salesInvoiceStatus) {
    return (SalesInvoiceStatus) AppService.find(main, SalesInvoiceStatus.class, salesInvoiceStatus.getId());
  }

  /**
   * Insert SalesInvoiceStatus.
   *
   * @param main
   * @param salesInvoiceStatus
   */
  public static final void insert(Main main, SalesInvoiceStatus salesInvoiceStatus) {
    insertAble(main, salesInvoiceStatus);  //Validating
    AppService.insert(main, salesInvoiceStatus);

  }

  /**
   * Update SalesInvoiceStatus by key.
   *
   * @param main
   * @param salesInvoiceStatus
   * @return SalesInvoiceStatus
   */
  public static final SalesInvoiceStatus updateByPk(Main main, SalesInvoiceStatus salesInvoiceStatus) {
    updateAble(main, salesInvoiceStatus); //Validating
    return (SalesInvoiceStatus) AppService.update(main, salesInvoiceStatus);
  }

  /**
   * Insert or update SalesInvoiceStatus
   *
   * @param main
   * @param salesInvoiceStatus
   */
  public static void insertOrUpdate(Main main, SalesInvoiceStatus salesInvoiceStatus) {
    if (salesInvoiceStatus.getId() == null) {
      insert(main, salesInvoiceStatus);
    } else {
      updateByPk(main, salesInvoiceStatus);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesInvoiceStatus
   */
  public static void clone(Main main, SalesInvoiceStatus salesInvoiceStatus) {
    salesInvoiceStatus.setId(null); //Set to null for insert
    insert(main, salesInvoiceStatus);
  }

  /**
   * Delete SalesInvoiceStatus.
   *
   * @param main
   * @param salesInvoiceStatus
   */
  public static final void deleteByPk(Main main, SalesInvoiceStatus salesInvoiceStatus) {
    deleteAble(main, salesInvoiceStatus); //Validation
    AppService.delete(main, SalesInvoiceStatus.class, salesInvoiceStatus.getId());
  }

  /**
   * Delete Array of SalesInvoiceStatus.
   *
   * @param main
   * @param salesInvoiceStatus
   */
  public static final void deleteByPkArray(Main main, SalesInvoiceStatus[] salesInvoiceStatus) {
    for (SalesInvoiceStatus e : salesInvoiceStatus) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param salesInvoiceStatus
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, SalesInvoiceStatus salesInvoiceStatus) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param salesInvoiceStatus
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, SalesInvoiceStatus salesInvoiceStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_sales_invoice_status where upper(title)=?", new Object[]{StringUtil.toUpperCase(salesInvoiceStatus.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param salesInvoiceStatus
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, SalesInvoiceStatus salesInvoiceStatus) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_sales_invoice_status where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(salesInvoiceStatus.getTitle()), salesInvoiceStatus.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
