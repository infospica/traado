/*
 * @(#)SalesReturnReceiptService.java	1.0 Fri Nov 24 18:28:44 IST 2017 
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
import spica.scm.domain.SalesReturnReceipt;
import spica.scm.validate.SalesReturnReceiptIs;

/**
 * SalesReturnReceiptService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Nov 24 18:28:44 IST 2017
 */
public abstract class SalesReturnReceiptService {

  /**
   * SalesReturnReceipt paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesReturnReceiptSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_return_receipt", SalesReturnReceipt.class, main);
    sql.main("select scm_sales_return_receipt.id,scm_sales_return_receipt.company_id,scm_sales_return_receipt.customer_id,scm_sales_return_receipt.consignment_no,scm_sales_return_receipt.entry_date,scm_sales_return_receipt.debit_note_no,scm_sales_return_receipt.debit_note_date,scm_sales_return_receipt.lr_number,scm_sales_return_receipt.note,scm_sales_return_receipt.created_by,scm_sales_return_receipt.created_at,scm_sales_return_receipt.modified_by,scm_sales_return_receipt.modified_at from scm_sales_return_receipt scm_sales_return_receipt "); //Main query
    sql.count("select count(scm_sales_return_receipt.id) as total from scm_sales_return_receipt scm_sales_return_receipt "); //Count query
    sql.join("left outer join scm_customer scm_sales_return_receiptcustomer_id on (scm_sales_return_receiptcustomer_id.id = scm_sales_return_receipt.customer_id) "
            + "left outer join scm_company scm_company on (scm_company.id = scm_sales_return_receipt.company_id)"); //Join Query

    sql.string(new String[]{"scm_sales_return_receiptcustomer_id.customer_name", "scm_sales_return_receipt.consignment_no", "scm_sales_return_receipt.debit_note_no", "scm_sales_return_receipt.lr_number", "scm_sales_return_receipt.note", "scm_sales_return_receipt.created_by", "scm_sales_return_receipt.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_return_receipt.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_return_receipt.entry_date", "scm_sales_return_receipt.debit_note_date", "scm_sales_return_receipt.created_at", "scm_sales_return_receipt.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesReturnReceipt.
   *
   * @param main
   * @return List of SalesReturnReceipt
   */
  public static final List<SalesReturnReceipt> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesReturnReceiptSqlPaged(main));
  }

  /**
   *
   * @param main
   * @param company
   * @return
   */
  public static final List<SalesReturnReceipt> listPagedByCompany(Main main, Company company) {
    SqlPage sql = getSalesReturnReceiptSqlPaged(main);
    sql.cond("where scm_sales_return_receipt.company_id = ?");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of SalesReturnReceipt based on condition
//   * @param main
//   * @return List<SalesReturnReceipt>
//   */
//  public static final List<SalesReturnReceipt> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesReturnReceiptSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesReturnReceipt by key.
   *
   * @param main
   * @param salesReturnReceipt
   * @return SalesReturnReceipt
   */
  public static final SalesReturnReceipt selectByPk(Main main, SalesReturnReceipt salesReturnReceipt) {
    return (SalesReturnReceipt) AppService.find(main, SalesReturnReceipt.class, salesReturnReceipt.getId());
  }

  /**
   * Insert SalesReturnReceipt.
   *
   * @param main
   * @param salesReturnReceipt
   */
  public static final void insert(Main main, SalesReturnReceipt salesReturnReceipt) {
    SalesReturnReceiptIs.insertAble(main, salesReturnReceipt);  //Validating
    AppService.insert(main, salesReturnReceipt);

  }

  /**
   * Update SalesReturnReceipt by key.
   *
   * @param main
   * @param salesReturnReceipt
   * @return SalesReturnReceipt
   */
  public static final SalesReturnReceipt updateByPk(Main main, SalesReturnReceipt salesReturnReceipt) {
    SalesReturnReceiptIs.updateAble(main, salesReturnReceipt); //Validating
    return (SalesReturnReceipt) AppService.update(main, salesReturnReceipt);
  }

  /**
   * Insert or update SalesReturnReceipt
   *
   * @param main
   * @param salesReturnReceipt
   */
  public static void insertOrUpdate(Main main, SalesReturnReceipt salesReturnReceipt) {
    if (salesReturnReceipt.getId() == null) {
      insert(main, salesReturnReceipt);
    } else {
      updateByPk(main, salesReturnReceipt);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesReturnReceipt
   */
  public static void clone(Main main, SalesReturnReceipt salesReturnReceipt) {
    salesReturnReceipt.setId(null); //Set to null for insert
    insert(main, salesReturnReceipt);
  }

  /**
   * Delete SalesReturnReceipt.
   *
   * @param main
   * @param salesReturnReceipt
   */
  public static final void deleteByPk(Main main, SalesReturnReceipt salesReturnReceipt) {
    SalesReturnReceiptIs.deleteAble(main, salesReturnReceipt); //Validation
    AppService.delete(main, SalesReturnReceipt.class, salesReturnReceipt.getId());
  }

  /**
   * Delete Array of SalesReturnReceipt.
   *
   * @param main
   * @param salesReturnReceipt
   */
  public static final void deleteByPkArray(Main main, SalesReturnReceipt[] salesReturnReceipt) {
    for (SalesReturnReceipt e : salesReturnReceipt) {
      deleteByPk(main, e);
    }
  }
}
