/*
 * @(#)ScmPurchaseReturnService.java	1.0 Sat Jul 22 17:33:14 IST 2017 
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
import spica.scm.domain.PurchaseReturn;

/**
 * PurchaseReturnGstService
 *
 * @author	Spirit 1.2
 * @version	1.0, Sat Jul 22 17:33:14 IST 2017
 */
public abstract class PurchaseReturnGstService {

  /**
   * PurchaseReturn paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReturnSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_return", PurchaseReturn.class, main);
    sql.main("select scm_purchase_return.id,scm_purchase_return.account_id,scm_purchase_return.purchase_return_status_id,scm_purchase_return.purchase_return_stock_cat,scm_purchase_return.purchase_return_stock_type,"
            + "scm_purchase_return.commodity_id,scm_purchase_return.product_type_id,scm_purchase_return.invoice_no,scm_purchase_return.invoice_date,scm_purchase_return.invoice_amount,"
            + "scm_purchase_return.invoice_amount_net,scm_purchase_return.invoice_amount_vat,scm_purchase_return.invoice_amount_cst,scm_purchase_return.invoice_amount_ed,scm_purchase_return.confirmed_by,"
            + "scm_purchase_return.confirmed_at,scm_purchase_return.to_be_approved_by,scm_purchase_return.approved_by,scm_purchase_return.approved_at,scm_purchase_return.actual_amount,"
            + "scm_purchase_return.actual_amount_net,scm_purchase_return.actual_amount_vat,scm_purchase_return.actual_amount_cst,scm_purchase_return.actual_amount_ed,scm_purchase_return.no_of_boxes,"
            + "scm_purchase_return.weight,scm_purchase_return.weight_unit,scm_purchase_return.invoice_amount_cgst,scm_purchase_return.invoice_amount_sgst,scm_purchase_return.invoice_amount_igst,"
            + "scm_purchase_return.actual_amount_cgst,scm_purchase_return.actual_amount_sgst,scm_purchase_return.actual_amount_igst,scm_purchase_return.tax_processor_document_id,scm_purchase_return.created_at,"
            + "scm_purchase_return.modified_at,scm_purchase_return.created_by,scm_purchase_return.modified_by from scm_purchase_return scm_purchase_return "); //Main query
    sql.count("select count(scm_purchase_return.id) as total from scm_purchase_return scm_purchase_return "); //Count query
    sql.join("left outer join scm_account scm_account on (scm_account.id = scm_purchase_return.account_id) "
            + "left outer join scm_purchase_return_status scm_purchase_return_status on (scm_purchase_return_status.id = scm_purchase_return.purchase_return_status_id) "
            + "left outer join scm_purchase_return_stock_cat scm_purchase_return_stock_cat on (scm_purchase_return_stock_cat.id = scm_purchase_return.purchase_return_stock_cat) "
            + "left outer join scm_purchase_return_stock_type scm_purchase_return_stock_type on (scm_purchase_return_stock_type.id = scm_purchase_return.purchase_return_stock_type) "
            + "left outer join scm_service_commodity scm_service_commodity on (scm_service_commodity.id = scm_purchase_return.commodity_id) "
            + "left outer join scm_product_type scm_product_type on (scm_product_type.id = scm_purchase_return.product_type_id) "
            + "left outer join scm_tax_processor scm_tax_processor on (scm_tax_processor.id = scm_purchase_return.tax_processor_id)"); //Join Query

    sql.string(new String[]{"scm_account.account_code", "scm_purchase_return_status.title", "scm_purchase_return_stock_cat.title", "scm_purchase_return_stock_type.title", "scm_service_commodity.title",
      "scm_product_type.title", "scm_purchase_return.invoice_no", "scm_purchase_return.confirmed_by", "scm_purchase_return.to_be_approved_by", "scm_purchase_return.approved_by", "scm_purchase_return.weight_unit",
      "scm_purchase_return.created_by", "scm_purchase_return.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_return.id", "scm_purchase_return.invoice_amount", "scm_purchase_return.invoice_amount_net", "scm_purchase_return.invoice_amount_vat",
      "scm_purchase_return.invoice_amount_cst", "scm_purchase_return.invoice_amount_ed", "scm_purchase_return.actual_amount", "scm_purchase_return.actual_amount_net", "scm_purchase_return.actual_amount_vat",
      "scm_purchase_return.actual_amount_cst", "scm_purchase_return.actual_amount_ed", "scm_purchase_return.no_of_boxes", "scm_purchase_return.weight", "scm_purchase_return.invoice_amount_cgst",
      "scm_purchase_return.invoice_amount_sgst", "scm_purchase_return.invoice_amount_igst", "scm_purchase_return.actual_amount_cgst", "scm_purchase_return.actual_amount_sgst", "scm_purchase_return.actual_amount_igst"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_purchase_return.invoice_date", "scm_purchase_return.confirmed_at", "scm_purchase_return.approved_at", "scm_purchase_return.created_at", "scm_purchase_return.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseReturn.
   *
   * @param main
   * @return List of PurchaseReturn
   */
  public static final List<PurchaseReturn> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseReturnSqlPaged(main));
  }

  /**
   * Select PurchaseReturn by key.
   *
   * @param main
   * @param purchaseReturn
   * @return PurchaseReturn
   */
  public static final PurchaseReturn selectByPk(Main main, PurchaseReturn purchaseReturn) {
    return (PurchaseReturn) AppService.find(main, PurchaseReturn.class, purchaseReturn.getId());
  }

  /**
   * Insert PurchaseReturn.
   *
   * @param main
   * @param purchaseReturn
   */
  public static final void insert(Main main, PurchaseReturn purchaseReturn) {
    AppService.insert(main, purchaseReturn);
  }

  /**
   * Update PurchaseReturn by key.
   *
   * @param main
   * @param purchaseReturn
   * @return PurchaseReturn
   */
  public static final PurchaseReturn updateByPk(Main main, PurchaseReturn purchaseReturn) {
    return (PurchaseReturn) AppService.update(main, purchaseReturn);
  }

  /**
   * Insert or update PurchaseReturn
   *
   * @param main
   * @param purchaseReturn
   */
  public static void insertOrUpdate(Main main, PurchaseReturn purchaseReturn) {
    if (purchaseReturn.getId() == null) {
      insert(main, purchaseReturn);
    } else {
      updateByPk(main, purchaseReturn);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReturn
   */
  public static void clone(Main main, PurchaseReturn purchaseReturn) {
    purchaseReturn.setId(null); //Set to null for insert
    insert(main, purchaseReturn);
  }

  /**
   * Delete PurchaseReturn.
   *
   * @param main
   * @param purchaseReturn
   */
  public static final void deleteByPk(Main main, PurchaseReturn purchaseReturn) {
    AppService.delete(main, PurchaseReturn.class, purchaseReturn.getId());
  }

  /**
   * Delete Array of PurchaseReturn.
   *
   * @param main
   * @param purchaseReturn
   */
  public static final void deleteByPkArray(Main main, PurchaseReturn[] purchaseReturn) {
    for (PurchaseReturn e : purchaseReturn) {
      deleteByPk(main, e);
    }
  }
}
