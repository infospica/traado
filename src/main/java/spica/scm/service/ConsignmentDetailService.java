/*
 * @(#)ConsignmentDetailService.java	1.0 Fri Jul 22 16:43:08 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountingFinancialYear;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.validate.ConsignmentDetailIs;
import spica.sys.SystemRuntimeConfig;

/**
 * ConsignmentDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 16:43:08 IST 2016
 */
public abstract class ConsignmentDetailService {

  /**
   * ConsignmentDetail paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getConsignmentDetailSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_consignment_detail", ConsignmentDetail.class, main);
    sql.main("select scm_consignment_detail.id,scm_consignment_detail.consignment_id,scm_consignment_detail.exit_document_no,scm_consignment_detail,"
            + "scm_consignment_detail.receipt_note,scm_consignment_detail.no_of_invoice,scm_consignment_detail.exit_document_file_path,scm_consignment_detail.exit_document_type_id,scm_consignment_detail.exit_document_2_no,scm_consignment_detail.exit_document_2_file_path,scm_consignment_detail.exit_document_2_type_id,scm_consignment_detail.entry_document_no,scm_consignment_detail.entry_document_file_path,scm_consignment_detail.entry_document_type_id,scm_consignment_detail.exit_checkpost_name,scm_consignment_detail.exit_checkpost_arrival_at,scm_consignment_detail.entry_checkpost_name,scm_consignment_detail.entry_checkpost_arrival_at,scm_consignment_detail.receipt_no,scm_consignment_detail.receipt_file_path,scm_consignment_detail.consignment_receipt_type_id,scm_consignment_detail.receipt_return_file_path,scm_consignment_detail.note,scm_consignment_detail.transporter_id,scm_consignment_detail.transporter_name,scm_consignment_detail.vehicle_no,scm_consignment_detail.vehicle_type,scm_consignment_detail.driver_name,scm_consignment_detail.driver_license_no from scm_consignment_detail scm_consignment_detail "); //Main query
    sql.count("select count(scm_consignment_detail.id) as total from scm_consignment_detail scm_consignment_detail "); //Count query
    sql.join("left outer join scm_consignment scm_consignment_detailconsignment_id on (scm_consignment_detailconsignment_id.id = scm_consignment_detail.consignment_id) left outer join scm_consignment_doc_type scm_consignment_detailexit_document_type_id on (scm_consignment_detailexit_document_type_id.id = scm_consignment_detail.exit_document_type_id) left outer join scm_consignment_doc_type scm_consignment_detailexit_document_2_type_id on (scm_consignment_detailexit_document_2_type_id.id = scm_consignment_detail.exit_document_2_type_id) left outer join scm_consignment_doc_type scm_consignment_detailentry_document_type_id on (scm_consignment_detailentry_document_type_id.id = scm_consignment_detail.entry_document_type_id) left outer join scm_consignment_receipt_type scm_consignment_detailconsignment_receipt_type_id on (scm_consignment_detailconsignment_receipt_type_id.id = scm_consignment_detail.consignment_receipt_type_id) left outer join scm_transporter scm_consignment_detailtransporter_id on (scm_consignment_detailtransporter_id.id = scm_consignment_detail.transporter_id)"); //Join Query

    sql.string(new String[]{"scm_consignment_detailconsignment_id.consignment_no", "scm_consignment_detail.exit_document_no", "scm_consignment_detail.exit_document_file_path", "scm_consignment_detailexit_document_type_id.title", "scm_consignment_detail.exit_document_2_no", "scm_consignment_detail.exit_document_2_file_path", "scm_consignment_detailexit_document_2_type_id.title", "scm_consignment_detail.entry_document_no", "scm_consignment_detail.entry_document_file_path", "scm_consignment_detailentry_document_type_id.title", "scm_consignment_detail.exit_checkpost_name", "scm_consignment_detail.entry_checkpost_name", "scm_consignment_detail.entry_checkpost_arrival_at", "scm_consignment_detail.receipt_no", "scm_consignment_detail.receipt_file_path", "scm_consignment_detailconsignment_receipt_type_id.title", "scm_consignment_detail.receipt_return_file_path", "scm_consignment_detail.note", "scm_consignment_detailtransporter_id.transporter_name", "scm_consignment_detail.transporter_name", "scm_consignment_detail.vehicle_no", "scm_consignment_detail.vehicle_type", "scm_consignment_detail.driver_name", "scm_consignment_detail.driver_license_no"}); //String search or sort fields
    sql.number(new String[]{"scm_consignment_detail.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_consignment_detail.exit_checkpost_arrival_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ConsignmentDetail.
   *
   * @param main
   * @return List of ConsignmentDetail
   */
  public static final List<ConsignmentDetail> listPaged(Main main) {
    return AppService.listPagedJpa(main, getConsignmentDetailSqlPaged(main));
  }

  public static final List<ConsignmentDetail> listPagedPurchaseConsignmentDetailByAccount(Main main, Account account, AccountingFinancialYear accountingFinancialYear) {
    SqlPage sql = getConsignmentDetailSqlPaged(main);
    sql.param(account.getId());
    sql.param(SystemRuntimeConfig.CONSIGNMENT_TYPE_PURCHASE);
    sql.cond("where scm_consignment_detail.consignment_id in (select id from scm_consignment where account_id = ? and consignment_type_id  = ? "
            + "AND consignment_date::date >= ?::date AND consignment_date::date <= ?::date)");
    main.param(accountingFinancialYear.getStartDate());
    main.param(accountingFinancialYear.getEndDate());
    return AppService.listPagedJpa(main, getConsignmentDetailSqlPaged(main));
  }

  public static final List<ConsignmentDetail> listPagedSalesReturnConsignmentByAccount(Main main, AccountGroup accountGroup, AccountingFinancialYear accountingFinancialYear) {
    SqlPage sql = getConsignmentDetailSqlPaged(main);
    sql.param(accountGroup.getId());
    sql.param(SystemRuntimeConfig.CONSIGNMENT_TYPE_SALES_RETURN);
    sql.cond("where scm_consignment_detail.consignment_id in (select id from scm_consignment where account_group_id = ? and consignment_type_id  = ? "
            + "AND consignment_date::date >= ?::date AND consignment_date::date <= ?::date)");
    main.param(accountingFinancialYear.getStartDate());
    main.param(accountingFinancialYear.getEndDate());
    return AppService.listPagedJpa(main, getConsignmentDetailSqlPaged(main));
  }

//  /**
//   * Return list of ConsignmentDetail based on condition
//   * @param main
//   * @return List<ConsignmentDetail>
//   */
//  public static final List<ConsignmentDetail> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getConsignmentDetailSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ConsignmentDetail by key.
   *
   * @param main
   * @param consignmentDetail
   * @return ConsignmentDetail
   */
  public static final ConsignmentDetail selectByPk(Main main, ConsignmentDetail consignmentDetail) {
    return (ConsignmentDetail) AppService.find(main, ConsignmentDetail.class, consignmentDetail.getId());
  }

  /**
   * Insert ConsignmentDetail.
   *
   * @param main
   * @param consignmentDetail
   */
  public static final void insert(Main main, ConsignmentDetail consignmentDetail) {
    ConsignmentDetailIs.insertAble(main, consignmentDetail);  //Validating
    AppService.insert(main, consignmentDetail);

  }

  /**
   * Update ConsignmentDetail by key.
   *
   * @param main
   * @param consignmentDetail
   * @return ConsignmentDetail
   */
  public static final ConsignmentDetail updateByPk(Main main, ConsignmentDetail consignmentDetail) {
    ConsignmentDetailIs.updateAble(main, consignmentDetail); //Validating
    return (ConsignmentDetail) AppService.update(main, consignmentDetail);
  }

  /**
   * Insert or update ConsignmentDetail
   *
   * @param main
   * @param ConsignmentDetail
   */
  public static void insertOrUpdate(Main main, ConsignmentDetail consignmentDetail) {
    if (consignmentDetail.getId() == null) {
      insert(main, consignmentDetail);
    } else {
      updateByPk(main, consignmentDetail);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param consignmentDetail
   */
  public static void clone(Main main, ConsignmentDetail consignmentDetail) {
    consignmentDetail.setId(null); //Set to null for insert
    insert(main, consignmentDetail);
  }

  /**
   * Delete ConsignmentDetail.
   *
   * @param main
   * @param consignmentDetail
   */
  public static final void deleteByPk(Main main, ConsignmentDetail consignmentDetail) {
    ConsignmentDetailIs.deleteAble(main, consignmentDetail); //Validation
    AppService.delete(main, ConsignmentDetail.class, consignmentDetail.getId());
  }

  /**
   * Delete Array of ConsignmentDetail.
   *
   * @param main
   * @param consignmentDetail
   */
  public static final void deleteByPkArray(Main main, ConsignmentDetail[] consignmentDetail) {
    for (ConsignmentDetail e : consignmentDetail) {
      deleteByPk(main, e);
    }
  }

  public static final ConsignmentDetail selectConsignmentDetailByConsignment(Main main, long consignmentId) {
    return (ConsignmentDetail) AppService.single(main, ConsignmentDetail.class, "select * from scm_consignment_detail scm_consignment_detail where scm_consignment_detail.consignment_id = ? ", new Object[]{consignmentId});
  }

  public static ConsignmentDetail selectConsignMentDetailBySalesInvoice(Main main, Integer salesInvoiceId) {
    String sql = "SELECT detail.id,detail.consignment_id,detail.lr_date,detail.exit_document_no \n"
            + "FROM  scm_consignment_detail detail,scm_sales_invoice invoice,scm_consignment_commodity commodity\n"
            + "WHERE invoice.id=commodity.sales_invoice_id\n"
            + "AND commodity.consignment_id=detail.consignment_id\n"
            + "AND invoice.id= ? AND detail.lr_date IS NOT NULL";
    return (ConsignmentDetail) AppService.single(main, ConsignmentDetail.class, sql, new Object[]{salesInvoiceId});
  }
}
