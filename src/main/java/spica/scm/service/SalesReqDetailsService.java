/*
 * @(#)SalesReqDetailsService.java	1.0 Wed May 25 16:00:02 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.SalesReq;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesReqDetails;
import spica.scm.validate.SalesReqDetailsIs;

/**
 * SalesReqDetailsService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 16:00:02 IST 2016
 */
public abstract class SalesReqDetailsService {

  /**
   * SalesReqDetails paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesReqDetailsSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_req_details", SalesReqDetails.class, main);
    sql.main("select scm_sales_req_details.id,scm_sales_req_details.sales_req_id,scm_sales_req_details.product_quantity,scm_sales_req_details.product_actual_price,scm_sales_req_details.product_fixed_discount,scm_sales_req_details.product_percent_discount,scm_sales_req_details.product_discounted_price,scm_sales_req_details.sort_order,scm_sales_req_details.product_detail_id from scm_sales_req_details scm_sales_req_details "); //Main query
    sql.count("select count(scm_sales_req_details.id) from scm_sales_req_details scm_sales_req_details "); //Count query
    sql.join("left outer join scm_sales_req scm_sales_req_detailssales_req_id on (scm_sales_req_detailssales_req_id.id = scm_sales_req_details.sales_req_id) left outer join scm_product_detail scm_sales_req_detailsproduct_detail_id on (scm_sales_req_detailsproduct_detail_id.id = scm_sales_req_details.product_detail_id)"); //Join Query

    sql.string(new String[]{"scm_sales_req_detailssales_req_id.requisition_no", "scm_sales_req_detailsproduct_detail_id.batch_no"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_req_details.id", "scm_sales_req_details.product_quantity", "scm_sales_req_details.product_actual_price", "scm_sales_req_details.product_fixed_discount", "scm_sales_req_details.product_percent_discount", "scm_sales_req_details.product_discounted_price", "scm_sales_req_details.sort_order"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesReqDetails.
   *
   * @param main
   * @return List of SalesReqDetails
   */
  public static final List<SalesReqDetails> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesReqDetailsSqlPaged(main));
  }

//  /**
//   * Return list of SalesReqDetails based on condition
//   * @param main
//   * @return List<SalesReqDetails>
//   */
//  public static final List<SalesReqDetails> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesReqDetailsSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesReqDetails by key.
   *
   * @param main
   * @param salesReqDetails
   * @return SalesReqDetails
   */
  public static final SalesReqDetails selectByPk(Main main, SalesReqDetails salesReqDetails) {
    return (SalesReqDetails) AppService.find(main, SalesReqDetails.class, salesReqDetails.getId());
  }

  /**
   * Insert SalesReqDetails.
   *
   * @param main
   * @param salesReqDetails
   */
  public static final void insert(Main main, SalesReqDetails salesReqDetails) {
    SalesReqDetailsIs.insertAble(main, salesReqDetails);  //Validating
    AppService.insert(main, salesReqDetails);

  }

  /**
   * Update SalesReqDetails by key.
   *
   * @param main
   * @param salesReqDetails
   * @return SalesReqDetails
   */
  public static final SalesReqDetails updateByPk(Main main, SalesReqDetails salesReqDetails) {
    SalesReqDetailsIs.updateAble(main, salesReqDetails); //Validating
    return (SalesReqDetails) AppService.update(main, salesReqDetails);
  }

  /**
   * Insert or update SalesReqDetails
   *
   * @param main
   * @param salesReqDetails
   */
  public static void insertOrUpdate(Main main, SalesReqDetails salesReqDetails) {
    if (salesReqDetails.getId() == null) {
      insert(main, salesReqDetails);
    } else {
      updateByPk(main, salesReqDetails);
    }
  }

  /**
   *
   * @param main
   * @param salesReq
   * @param salesReqDetailsList
   */
  public static void insertOrUpdateSalesReqDetails(Main main, SalesReq salesReq, List<SalesReqDetails> salesReqDetailsList) {
    //main.param(salesReq.getFixedDiscount());
    //main.param(salesReq.getPercentDiscount());
    //main.param(salesReq.getRequisitionStatusId().getId());
    main.param(salesReq.getNote());
    main.param(salesReq.getModifiedBy());
    main.param(salesReq.getModifiedAt());
    //main.param(salesReq.getConfirmedBy());
    //main.param(salesReq.getConfirmedAt());
    main.param(salesReq.getId());
    AppService.updateSql(main, SalesReq.class, "update scm_sales_req set fixed_discount = ?, percent_discount = ?, requisition_status_id = ?, note = ?, modified_by = ?,"
            + " modified_at = ?, confirmed_by = ?, confirmed_at = ? where id = ? ", false);
    main.clear();
    if (salesReqDetailsList != null && !salesReqDetailsList.isEmpty()) {
      for (SalesReqDetails salesReqDetails : salesReqDetailsList) {
        insertOrUpdate(main, salesReqDetails);
      }
      AppService.deleteSql(main, SalesReqDetails.class, "delete from scm_sales_req_details where sort_order=?", new Object[]{1});//deleting all preveously trashed records
    }
  }

  /**
   * Update SalesReqDetails trash by key.
   *
   * @param main
   * @param salesReqDetailsId
   */
  public static final void updateSalesReqDetailsTrashByPk(Main main, String salesReqDetailsId) {
    main.param(1);
    main.param(Integer.parseInt(salesReqDetailsId));
    AppService.updateSql(main, SalesReqDetails.class, "update scm_purchase_req_detail set sort_order = ? where id = ?", false);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesReqDetails
   */
  public static void clone(Main main, SalesReqDetails salesReqDetails) {
    salesReqDetails.setId(null); //Set to null for insert
    insert(main, salesReqDetails);
  }

  /**
   * Delete SalesReqDetails.
   *
   * @param main
   * @param salesReqDetails
   */
  public static final void deleteByPk(Main main, SalesReqDetails salesReqDetails) {
    SalesReqDetailsIs.deleteAble(main, salesReqDetails); //Validation
    AppService.delete(main, SalesReqDetails.class, salesReqDetails.getId());
  }

  /**
   * Delete Array of SalesReqDetails.
   *
   * @param main
   * @param salesReqDetails
   */
  public static final void deleteByPkArray(Main main, SalesReqDetails[] salesReqDetails) {
    for (SalesReqDetails e : salesReqDetails) {
      deleteByPk(main, e);
    }
  }

  /**
   * Return List of Sales Req Details.
   *
   * @param main
   * @param salesReq
   * @return
   */
  public static List<SalesReqDetails> selectSalesReqDetailsBySalesReq(Main main, SalesReq salesReq) {
    SqlPage sql = getSalesReqDetailsSqlPaged(main);
    sql.cond("where scm_sales_req_details.sales_req_id = ?");
    sql.param(salesReq.getId());
    return AppService.listAllJpa(main, sql);
  }

}
