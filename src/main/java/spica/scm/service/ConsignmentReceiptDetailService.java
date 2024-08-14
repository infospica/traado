/*
 * @(#)ConsignmentReceiptDetailService.java	1.0 Fri Jul 22 10:57:51 IST 2016 
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
import spica.scm.domain.ConsignmentReceiptDetail;
import spica.scm.validate.ConsignmentReceiptDetailIs;

/**
 * ConsignmentReceiptDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:51 IST 2016
 */
public abstract class ConsignmentReceiptDetailService {

  /**
   * ConsignmentReceiptDetail paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getConsignmentReceiptDetailSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_consignment_receipt_detail", ConsignmentReceiptDetail.class, main);
    sql.main("select scm_consignment_receipt_detail.id,scm_consignment_receipt_detail.consignment_commodity_id,scm_consignment_receipt_detail.commodity_qty_good,scm_consignment_receipt_detail.commodity_qty_damaged,scm_consignment_receipt_detail.commodity_qty_shortage,scm_consignment_receipt_detail.commodity_qty_excess from scm_consignment_receipt_detail scm_consignment_receipt_detail "); //Main query
    sql.count("select count(scm_consignment_receipt_detail.id) as total from scm_consignment_receipt_detail scm_consignment_receipt_detail "); //Count query
    sql.join("left outer join scm_consignment_commodity scm_consignment_receipt_detailconsignment_commodity_id on (scm_consignment_receipt_detailconsignment_commodity_id.id = scm_consignment_receipt_detail.consignment_commodity_id)"); //Join Query

    sql.string(new String[]{"scm_consignment_receipt_detailconsignment_receipt_id.receipt_note", "scm_consignment_receipt_detailconsignment_commodity_id.commodity_name"}); //String search or sort fields
    sql.number(new String[]{"scm_consignment_receipt_detail.id", "scm_consignment_receipt_detail.commodity_qty_good", "scm_consignment_receipt_detail.commodity_qty_damaged", "scm_consignment_receipt_detail.commodity_qty_shortage", "scm_consignment_receipt_detail.commodity_qty_excess"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ConsignmentReceiptDetail.
   *
   * @param main
   * @return List of ConsignmentReceiptDetail
   */
  public static final List<ConsignmentReceiptDetail> listPaged(Main main) {
    return AppService.listPagedJpa(main, getConsignmentReceiptDetailSqlPaged(main));
  }

//  /**
//   * Return list of ConsignmentReceiptDetail based on condition
//   * @param main
//   * @return List<ConsignmentReceiptDetail>
//   */
//  public static final List<ConsignmentReceiptDetail> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getConsignmentReceiptDetailSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ConsignmentReceiptDetail by key.
   *
   * @param main
   * @param consignmentReceiptDetail
   * @return ConsignmentReceiptDetail
   */
  public static final ConsignmentReceiptDetail selectByPk(Main main, ConsignmentReceiptDetail consignmentReceiptDetail) {
    return (ConsignmentReceiptDetail) AppService.find(main, ConsignmentReceiptDetail.class, consignmentReceiptDetail.getId());
  }

  /**
   * Insert ConsignmentReceiptDetail.
   *
   * @param main
   * @param consignmentReceiptDetail
   */
  public static final void insert(Main main, ConsignmentReceiptDetail consignmentReceiptDetail) {
    ConsignmentReceiptDetailIs.insertAble(main, consignmentReceiptDetail);  //Validating
//    consignmentReceiptDetail.setConsignmentReceiptId( selectConsignmentReceipt(main, consignmentReceiptDetail.getConsignmentCommodityId().getConsignmentId().getId()));
    AppService.insert(main, consignmentReceiptDetail);

  }

  /**
   * Update ConsignmentReceiptDetail by key.
   *
   * @param main
   * @param consignmentReceiptDetail
   * @return ConsignmentReceiptDetail
   */
  public static final ConsignmentReceiptDetail updateByPk(Main main, ConsignmentReceiptDetail consignmentReceiptDetail) {
    ConsignmentReceiptDetailIs.updateAble(main, consignmentReceiptDetail); //Validating
    return (ConsignmentReceiptDetail) AppService.update(main, consignmentReceiptDetail);
  }

  /**
   * Insert or update ConsignmentReceiptDetail
   *
   * @param main
   * @param ConsignmentReceiptDetail
   */
  public static void insertOrUpdate(Main main, ConsignmentReceiptDetail consignmentReceiptDetail) {
    if (consignmentReceiptDetail.getId() == null) {
      insert(main, consignmentReceiptDetail);
    } else {
      updateByPk(main, consignmentReceiptDetail);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param consignmentReceiptDetail
   */
  public static void clone(Main main, ConsignmentReceiptDetail consignmentReceiptDetail) {
    consignmentReceiptDetail.setId(null); //Set to null for insert
    insert(main, consignmentReceiptDetail);
  }

  /**
   * Delete ConsignmentReceiptDetail.
   *
   * @param main
   * @param consignmentReceiptDetail
   */
  public static final void deleteByPk(Main main, ConsignmentReceiptDetail consignmentReceiptDetail) {
    ConsignmentReceiptDetailIs.deleteAble(main, consignmentReceiptDetail); //Validation
    AppService.delete(main, ConsignmentReceiptDetail.class, consignmentReceiptDetail.getId());
  }

  /**
   * Delete Array of ConsignmentReceiptDetail.
   *
   * @param main
   * @param consignmentReceiptDetail
   */
  public static final void deleteByPkArray(Main main, ConsignmentReceiptDetail[] consignmentReceiptDetail) {
    for (ConsignmentReceiptDetail e : consignmentReceiptDetail) {
      deleteByPk(main, e);
    }
  }
}
