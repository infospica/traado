/*
 * @(#)SalesAgentCommissionReceiptDetailService.java	1.0 Mon Dec 18 16:22:19 IST 2017 
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
import spica.scm.domain.SalesAgentCommissionReceiptDetail;
import spica.scm.validate.SalesAgentCommissionReceiptDetailIs;

/**
 * SalesAgentCommissionReceiptDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Dec 18 16:22:19 IST 2017
 */
public abstract class SalesAgentCommissionReceiptDetailService {

  /**
   * SalesAgentCommissionReceiptDetail paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentCommissionReceiptDetailSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_commission_receipt_detail", SalesAgentCommissionReceiptDetail.class, main);
    sql.main("select scm_sales_agent_commission_receipt_detail.id,scm_sales_agent_commission_receipt_detail.sales_agent_commission_receipt_id,scm_sales_agent_commission_receipt_detail.sales_invoice_id,scm_sales_agent_commission_receipt_detail.invoice_commission_value,scm_sales_agent_commission_receipt_detail.eligible_commission_value,scm_sales_agent_commission_receipt_detail.approved_commission_value from scm_sales_agent_commission_receipt_detail scm_sales_agent_commission_receipt_detail "); //Main query
    sql.count("select count(scm_sales_agent_commission_receipt_detail.id) as total from scm_sales_agent_commission_receipt_detail scm_sales_agent_commission_receipt_detail "); //Count query
    sql.join("left outer join scm_sales_agent_commission_receipt scm_sales_agent_commission_receipt_detailsales_agent_commission_receipt_id on (scm_sales_agent_commission_receipt_detailsales_agent_commission_receipt_id.id = scm_sales_agent_commission_receipt_detail.sales_agent_commission_receipt_id) left outer join scm_sales_invoice scm_sales_agent_commission_receipt_detailsales_invoice_id on (scm_sales_agent_commission_receipt_detailsales_invoice_id.id = scm_sales_agent_commission_receipt_detail.sales_invoice_id)"); //Join Query

    sql.string(new String[]{"scm_sales_agent_commission_receipt_detailsales_invoice_id.invoice_no"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_commission_receipt_detail.id", "scm_sales_agent_commission_receipt_detail.invoice_commission_value", "scm_sales_agent_commission_receipt_detail.eligible_commission_value", "scm_sales_agent_commission_receipt_detail.approved_commission_value"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent_commission_receipt_detailsales_agent_commission_receipt_id.entry_date"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentCommissionReceiptDetail.
   *
   * @param main
   * @return List of SalesAgentCommissionReceiptDetail
   */
  public static final List<SalesAgentCommissionReceiptDetail> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesAgentCommissionReceiptDetailSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentCommissionReceiptDetail based on condition
//   * @param main
//   * @return List<SalesAgentCommissionReceiptDetail>
//   */
//  public static final List<SalesAgentCommissionReceiptDetail> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentCommissionReceiptDetailSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesAgentCommissionReceiptDetail by key.
   *
   * @param main
   * @param salesAgentCommissionReceiptDetail
   * @return SalesAgentCommissionReceiptDetail
   */
  public static final SalesAgentCommissionReceiptDetail selectByPk(Main main, SalesAgentCommissionReceiptDetail salesAgentCommissionReceiptDetail) {
    return (SalesAgentCommissionReceiptDetail) AppService.find(main, SalesAgentCommissionReceiptDetail.class, salesAgentCommissionReceiptDetail.getId());
  }

  /**
   * Insert SalesAgentCommissionReceiptDetail.
   *
   * @param main
   * @param salesAgentCommissionReceiptDetail
   */
  public static final void insert(Main main, SalesAgentCommissionReceiptDetail salesAgentCommissionReceiptDetail) {
    SalesAgentCommissionReceiptDetailIs.insertAble(main, salesAgentCommissionReceiptDetail);  //Validating
    AppService.insert(main, salesAgentCommissionReceiptDetail);

  }

  /**
   * Update SalesAgentCommissionReceiptDetail by key.
   *
   * @param main
   * @param salesAgentCommissionReceiptDetail
   * @return SalesAgentCommissionReceiptDetail
   */
  public static final SalesAgentCommissionReceiptDetail updateByPk(Main main, SalesAgentCommissionReceiptDetail salesAgentCommissionReceiptDetail) {
    SalesAgentCommissionReceiptDetailIs.updateAble(main, salesAgentCommissionReceiptDetail); //Validating
    return (SalesAgentCommissionReceiptDetail) AppService.update(main, salesAgentCommissionReceiptDetail);
  }

  /**
   * Insert or update SalesAgentCommissionReceiptDetail
   *
   * @param main
   * @param salesAgentCommissionReceiptDetail
   */
  public static void insertOrUpdate(Main main, SalesAgentCommissionReceiptDetail salesAgentCommissionReceiptDetail) {
    if (salesAgentCommissionReceiptDetail.getId() == null) {
      insert(main, salesAgentCommissionReceiptDetail);
    } else {
      updateByPk(main, salesAgentCommissionReceiptDetail);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesAgentCommissionReceiptDetail
   */
  public static void clone(Main main, SalesAgentCommissionReceiptDetail salesAgentCommissionReceiptDetail) {
    salesAgentCommissionReceiptDetail.setId(null); //Set to null for insert
    insert(main, salesAgentCommissionReceiptDetail);
  }

  /**
   * Delete SalesAgentCommissionReceiptDetail.
   *
   * @param main
   * @param salesAgentCommissionReceiptDetail
   */
  public static final void deleteByPk(Main main, SalesAgentCommissionReceiptDetail salesAgentCommissionReceiptDetail) {
    SalesAgentCommissionReceiptDetailIs.deleteAble(main, salesAgentCommissionReceiptDetail); //Validation
    AppService.delete(main, SalesAgentCommissionReceiptDetail.class, salesAgentCommissionReceiptDetail.getId());
  }

  /**
   * Delete Array of SalesAgentCommissionReceiptDetail.
   *
   * @param main
   * @param salesAgentCommissionReceiptDetail
   */
  public static final void deleteByPkArray(Main main, SalesAgentCommissionReceiptDetail[] salesAgentCommissionReceiptDetail) {
    for (SalesAgentCommissionReceiptDetail e : salesAgentCommissionReceiptDetail) {
      deleteByPk(main, e);
    }
  }
}
