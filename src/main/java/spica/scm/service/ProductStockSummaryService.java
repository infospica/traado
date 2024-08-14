/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.common.ProductStockDetail;
import spica.scm.common.ProductStockSummary;
import spica.scm.domain.AccountGroup;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author keerthana
 */
public abstract class ProductStockSummaryService {

  public static List<ProductStockSummary> selectProductStockSummaryList(Main main, AccountGroup accountGroup) {

    String sql = " SELECT account_id,product_id,product_name,SUM(quantity_saleable) as saleable,SUM(quantity_free_saleable) as free_saleable,\n"
            + "   SUM(quantity_blocked) as blocked,SUM(quantity_free_blocked) as free_blocked,SUM(quantity_damaged) as damaged,SUM(quantity_free_damaged) as free_damaged,\n"
            + "   SUM(quantity_expired) as expired,SUM(quantity_free_expired) as free_expired,SUM(quantity_excess) as excess\n"
            + "   FROM getstockbyaccount(NULL,?) GROUP BY account_id,product_id,product_name order by product_name";

    return AppDb.getList(main.dbConnector(), ProductStockSummary.class, sql, new Object[]{accountGroup.getId()});
  }

  public static List<ProductStockDetail> selectProductStockDetailList(Main main, ProductStockSummary productStockSummary) {
    String sql = "SELECT tab.account_id,purchase_invoice_no,purchase_invoice_date,product_entry_detail_id,product_id,tab.product_detail_id,product_name,tab.batch_no,tab.pack_size,\n"
            + "expiry_date_actual,mrp_value,sales_invoice_no,tab.sales_invoice_id ,\n"
            + "SUM(quantity_saleable) as saleable,SUM(quantity_free_saleable) as free_saleable, \n"
            + "SUM(quantity_blocked) as blocked,SUM(quantity_free_blocked) as free_blocked,SUM(quantity_damaged) as damaged,SUM(quantity_free_damaged) as free_damaged, \n"
            + "SUM(quantity_expired) as expired,SUM(quantity_free_expired) as free_expired, SUM(quantity_excess) as excess,ped.product_entry_id,\n"
            + "CASE WHEN entry.sales_return_id is null THEN '1' ELSE '2' END as purchase_type \n"
            + "FROM getstockofproduct(?,?) tab,scm_product_entry_detail ped,scm_product_entry entry where tab.product_entry_detail_id = ped.id \n"
            + "and ped.product_entry_id =entry.id \n"
            + "GROUP BY tab.account_id,purchase_invoice_no,purchase_invoice_date,product_entry_detail_id,product_id,tab.product_detail_id,product_name,\n"
            + "tab.batch_no,tab.pack_size,expiry_date_actual,mrp_value,sales_invoice_no,ped.product_entry_id,tab.sales_invoice_id,purchase_type \n"
            + "ORDER BY expiry_date_actual,product_detail_id ";

    return AppDb.getList(main.dbConnector(), ProductStockDetail.class, sql, new Object[]{productStockSummary.getAccountId(), productStockSummary.getProductId()});
  }

}
