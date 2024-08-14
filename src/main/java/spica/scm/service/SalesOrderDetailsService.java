/*
 * @(#)SalesOrderDetailsService.java	1.0 Tue May 17 15:23:09 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.scm.service;

import java.util.List;
import spica.scm.domain.SalesOrder;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesOrderDetails;
import spica.scm.validate.SalesOrderDetailsIs;

/**
 * SalesOrderDetailsService
 * @author	Spirit 1.2
 * @version	1.0, Tue May 17 15:23:09 IST 2016 
 */

public abstract class SalesOrderDetailsService {  
 
 /**
   * SalesOrderDetails paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesOrderDetailsSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_order_details", SalesOrderDetails.class, main);
    sql.main("select scm_sales_order_details.id,scm_sales_order_details.scm_sales_order_id,scm_sales_order_details.product_id,scm_sales_order_details.product_detail_id,scm_sales_order_details.product_quantity,scm_sales_order_details.product_actual_price,scm_sales_order_details.product_fixed_discount,scm_sales_order_details.product_percent_discount,scm_sales_order_details.product_discounted_price,scm_sales_order_details.sort_order from scm_sales_order_details scm_sales_order_details "); //Main query
    sql.count("select count(scm_sales_order_details.id) from scm_sales_order_details scm_sales_order_details "); //Count query
    sql.join("left outer join scm_sales_order scm_sales_order_detailsscm_sales_order_id on (scm_sales_order_detailsscm_sales_order_id.id = scm_sales_order_details.scm_sales_order_id) left outer join scm_product scm_sales_order_detailsproduct_id on (scm_sales_order_detailsproduct_id.id = scm_sales_order_details.product_id) left outer join scm_product_detail scm_sales_order_detailsproduct_detail_id on (scm_sales_order_detailsproduct_detail_id.id = scm_sales_order_details.product_detail_id)"); //Join Query

    sql.string(new String[]{"scm_sales_order_detailsscm_sales_order_id.order_no","scm_sales_order_detailsproduct_id.product_name","scm_sales_order_detailsproduct_detail_id.batch_no"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_order_details.id","scm_sales_order_details.product_quantity","scm_sales_order_details.product_actual_price","scm_sales_order_details.product_fixed_discount","scm_sales_order_details.product_percent_discount","scm_sales_order_details.product_discounted_price"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of SalesOrderDetails.
  * @param main
  * @return List of SalesOrderDetails
  */
  public static final List<SalesOrderDetails> listPaged(Main main) {
     return AppService.listPagedJpa(main, getSalesOrderDetailsSqlPaged(main));
  }

//  /**
//   * Return list of SalesOrderDetails based on condition
//   * @param main
//   * @return List<SalesOrderDetails>
//   */
//  public static final List<SalesOrderDetails> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesOrderDetailsSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select SalesOrderDetails by key.
  * @param main
  * @param salesOrderDetails
  * @return SalesOrderDetails
  */
  public static final SalesOrderDetails selectByPk(Main main, SalesOrderDetails salesOrderDetails) {
    return (SalesOrderDetails) AppService.find(main, SalesOrderDetails.class, salesOrderDetails.getId());
  }

 /**
  * Insert SalesOrderDetails.
  * @param main
  * @param salesOrderDetails
  */
  public static final void insert(Main main, SalesOrderDetails salesOrderDetails) {
    SalesOrderDetailsIs.insertAble(main, salesOrderDetails);  //Validating
    AppService.insert(main, salesOrderDetails);

  }

 /**
  * Update SalesOrderDetails by key.
  * @param main
  * @param salesOrderDetails
  * @return SalesOrderDetails
  */
  public static final SalesOrderDetails updateByPk(Main main, SalesOrderDetails salesOrderDetails) {
    SalesOrderDetailsIs.updateAble(main, salesOrderDetails); //Validating
    return (SalesOrderDetails) AppService.update(main, salesOrderDetails);
  }

  /**
   * Insert or update SalesOrderDetails
   *
   * @param main
   * @param SalesOrderDetails
   */
  public static void insertOrUpdate(Main main, SalesOrderDetails salesOrderDetails) {
    if (salesOrderDetails.getId() == null) {
      insert(main, salesOrderDetails);
    }
    else{
        updateByPk(main, salesOrderDetails);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesOrderDetails
   */
  public static void clone(Main main, SalesOrderDetails salesOrderDetails) {
    salesOrderDetails.setId(null); //Set to null for insert
    insert(main, salesOrderDetails);
  }

 /**
  * Delete SalesOrderDetails.
  * @param main
  * @param salesOrderDetails
  */
  public static final void deleteByPk(Main main, SalesOrderDetails salesOrderDetails) {
    SalesOrderDetailsIs.deleteAble(main, salesOrderDetails); //Validation
    AppService.delete(main, SalesOrderDetails.class, salesOrderDetails.getId());
  }
	
 /**
  * Delete Array of SalesOrderDetails.
  * @param main
  * @param salesOrderDetails
  */
  public static final void deleteByPkArray(Main main, SalesOrderDetails[] salesOrderDetails) {
    for (SalesOrderDetails e : salesOrderDetails) {
      deleteByPk(main, e);
    }
  }
  
  public static void insertOrUpdateSalesOrderDetails(Main main, SalesOrder salesOrder, List<SalesOrderDetails> salesOrderDetailsList) {
        //main.param(salesOrder.getFixedDiscount());
        //main.param(salesOrder.getPercentDiscount());
        //main.param(salesOrder.getSalesOrderStatId().getId());        
        main.param(salesOrder.getModifiedBy());
        main.param(salesOrder.getModifiedAt());
        main.param(salesOrder.getApprovedBy());
        main.param(salesOrder.getApprovedAt());
        main.param(salesOrder.getId());
        AppService.updateSql(main, SalesOrder.class, "update scm_sales_order set fixed_discount = ?, percent_discount = ?, sales_order_stat_id = ?, modified_by = ?,"
                + " modified_at = ?, approved_by = ?, approved_at = ? where id = ? ", false);
        main.clear();
        if (salesOrderDetailsList != null && !salesOrderDetailsList.isEmpty()) {
            for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                insertOrUpdate(main, salesOrderDetails);
            }
            AppService.deleteSql(main, SalesOrderDetails.class, "delete from scm_sales_order_details where sort_order=?", new Object[]{1});//deleting all preveously trashed records
        }
    }
  
  /**
     * Update SalesReqDetails trash by key.
     *
     * @param main
     * @param salesOrderDetailsId
     */
    public static final void updateSalesOrderDetailsTrashByPk(Main main, String salesOrderDetailsId) {
        main.param(1);
        main.param(Integer.parseInt(salesOrderDetailsId));
        AppService.updateSql(main, SalesOrderDetails.class, "update scm_sales_order_details set sort_order = ? where id = ?", false);
    }
  
  
    /**
     * Return List of Sales Req Details.
     *
     * @param main
     * @param salesOrder
     * @return
     */
    public static List<SalesOrderDetails> selectSalesOrderDetailsBySalesOrder(Main main, SalesOrder salesOrder) {
        main.param(0);
        main.param(salesOrder.getId());
        AppService.updateSql(main, SalesOrderDetails.class, "update scm_sales_order_details set sort_order = ? where scm_sales_order_id = ?", false);
        main.clear();

        SqlPage sql = getSalesOrderDetailsSqlPaged(main);
        sql.cond("where scm_sales_order_details.scm_sales_order_id = ?");
        sql.param(salesOrder.getId());
        return AppService.listAllJpa(main, sql);
    }

}
