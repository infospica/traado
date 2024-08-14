/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import spica.reports.model.CompanySalesRateDifference;
import spica.scm.domain.Company;
import spica.reports.model.FilterParameters;
import wawo.app.Main;
import wawo.entity.core.AppDb;
import java.util.List;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
/**
 *
 * @author krishna.vm
 */
public class CompanySalesRateDifferenceService {
  
  public static List<CompanySalesRateDifference> getCompanySalesRateDifferenceList(Main main, Company company, FilterParameters filterParameters) {
    String sql ="";
    String sqlCondition = " AND scm_product.company_id =?";
    String sqlOrderBy ="";        
        sql = "SELECT scm_vendor.vendor_name as supplier_name,scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n" +
              "scm_sales_invoice.invoice_no as invoice_no,scm_sales_invoice.invoice_date as invoice_date,\n" +
              "scm_manufacture.code as mfr_code,\n" +
              "scm_product.product_name as product_name,scm_product_category.hsn_code as hsn_code,getPackDimension(scm_product.id) as pack_type, \n" +
              "scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,            \n" +
              "CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END as qty,\n" +
              "CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN 0 ELSE COALESCE (scm_sales_invoice_item.product_qty_free,0) END as qty_free, \n" +
              "scm_sales_invoice_item.value_mrp as value_mrp,\n" +
              "scm_sales_invoice_item.prod_piece_selling_forced as value_pts,\n" +
              "scm_sales_invoice_item.value_prod_piece_selling as value_rate,\n" +
              "scm_sales_invoice_item.scheme_discount_value as scheme_discount,\n" +
              "scm_sales_invoice_item.product_discount_value as product_discount,\n" +
              "scm_sales_invoice_item.invoice_discount_derived as invoice_discount, \n" +
              "scm_sales_invoice_item.value_goods as goods_value,\n" +
              "CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN (product_qty+ COALESCE(product_qty_free,0))*(scm_sales_invoice_item.value_prod_piece_sold-scm_product_entry_detail.actual_selling_price_derived) \n" +
              "ELSE scm_sales_invoice_item.product_qty*(scm_sales_invoice_item.value_prod_piece_sold-scm_product_entry_detail.actual_selling_price_derived) END as rate_difference\n" +
              "FROM \n" +
              "scm_sales_invoice \n" +
              ",scm_sales_invoice_item,scm_product_entry_detail,scm_customer,scm_account,scm_vendor,scm_product_detail,scm_product,scm_product_category,\n" +
              "scm_product_batch LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id\n" +
              "WHERE scm_sales_invoice.sales_invoice_status_id = ? \n" +
              "AND ROUND(scm_product_entry_detail.actual_selling_price_derived,4)!=ROUND(scm_sales_invoice_item.value_prod_piece_sold,4) \n" +
              "AND scm_product_entry_detail.id = scm_sales_invoice_item.product_entry_detail_id\n" +
              "AND scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n" +
              "AND scm_sales_invoice.customer_id = scm_customer.id\n" +
              "AND scm_account.vendor_id =  scm_vendor.id AND scm_account.id = scm_product_detail.account_id\n" +
              "AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n" +
              "AND scm_product_detail.product_batch_id = scm_product_batch.id\n" +
              "AND scm_product_batch.product_id = scm_product.id\n" +              
              "AND scm_product.product_category_id = scm_product_category.id";
        
        sqlOrderBy =  " ORDER BY UPPER(scm_vendor.vendor_name),UPPER(scm_customer.customer_name),scm_sales_invoice.invoice_date,scm_sales_invoice.invoice_no ";
          
               
        ArrayList<Object> params = new ArrayList<>();
        params.add(SystemConstants.CONFIRMED);
        params.add(company.getId());
        
        if(filterParameters.getCustomer()!= null && filterParameters.getCustomer().getId()!=null){
            sqlCondition += " AND scm_sales_invoice.customer_id = ?";
             params.add(filterParameters.getCustomer().getId());
        }
        if(filterParameters.getAccount()!=null && filterParameters.getAccount().getId()!=null){
            sqlCondition += " AND scm_product_detail.account_id = ?";
            params.add(filterParameters.getAccount().getId());
        }   
        
        if(filterParameters.getFromDate()!=null){
            sqlCondition += " AND scm_sales_invoice.invoice_date >= ?::date";
            params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
        }
        if(filterParameters.getToDate()!=null){
            sqlCondition += " AND scm_sales_invoice.invoice_date <= ?::date";
            params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
        }  
    sql = sql + sqlCondition + sqlOrderBy ;
 
    return AppDb.getList(main.dbConnector(), CompanySalesRateDifference.class, sql,params.toArray());
  }
}
