/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.service;

import java.util.ArrayList;
import spica.reports.model.SalesRateDifference;
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
public class SalesRateDifferenceService {

  public static List<SalesRateDifference> getSalesRateDifferenceList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "";
    String sqlCondition = " AND scm_product.company_id =?";
    String sqlOrderBy = "";

    if (filterParameters == null || filterParameters.getReportType() == null || "1".equals(filterParameters.getReportType())) {  //INVOICE_PRODUCT_WISE = 1;
      sql = "SELECT scm_sales_invoice.id,scm_vendor.vendor_name as supplier_name,scm_sales_invoice.invoice_no as invoice_no,scm_sales_invoice.invoice_date as invoice_date, \n"
              + " scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
              + " scm_product.product_name as product_name,scm_product.hsn_code as hsn_code,getPackDimension(scm_product.id) as pack_type, \n"
              + " scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,            	    \n"
              + " CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END as qty,\n"
              + " CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN 0 ELSE COALESCE (scm_sales_invoice_item.product_qty_free,0) END as qty_free, \n"
              + " scm_sales_invoice_item.value_prod_piece_selling as value_pts,\n"
              + " CASE WHEN (scm_sales_invoice_item.value_prod_piece_selling - scm_sales_invoice_item.prod_piece_selling_forced)>0 \n"
              + " THEN (scm_sales_invoice_item.value_prod_piece_selling - scm_sales_invoice_item.prod_piece_selling_forced) ELSE 0 END  as rate_deviation,\n"
              + " scm_sales_invoice_item.value_mrp as value_mrp,\n"
              + " scm_sales_invoice_item.scheme_discount_value as scheme_discount,\n"
              + " CASE WHEN (scm_sales_invoice_item.scheme_discount_derived - scm_sales_invoice_item.scheme_discount_actual)>0 \n"
              + " THEN (scm_sales_invoice_item.scheme_discount_derived - scm_sales_invoice_item.scheme_discount_actual)*\n"
              + " CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END ELSE 0 END  as scheme_disc_deviation,\n"
              + " scm_sales_invoice_item.product_discount_value as product_discount,\n"
              + " CASE WHEN (scm_sales_invoice_item.product_discount_derived - scm_sales_invoice_item.product_discount_actual)>0 \n"
              + " THEN (scm_sales_invoice_item.product_discount_derived - scm_sales_invoice_item.product_discount_actual)*\n"
              + " CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END ELSE 0 END  as product_disc_deviation,\n"
              + " scm_sales_invoice_item.invoice_discount_derived as invoice_discount, \n"
              + " CASE WHEN (scm_sales_invoice_item.invoice_discount_derived - scm_sales_invoice_item.invoice_discount_actual)>0 \n"
              + " THEN (scm_sales_invoice_item.invoice_discount_derived - scm_sales_invoice_item.invoice_discount_actual)*\n"
              + " CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END ELSE 0 END  as invoice_disc_deviation,\n"
              + " scm_sales_invoice_item.value_goods as goods_value,scm_sales_invoice_item.landing_rate,\n"
              + " CASE WHEN is_sales_interstate =1 THEN scm_sales_invoice_item.value_igst ELSE 0 END as igst,\n"
              + " CASE WHEN is_sales_interstate =1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END as cgst,\n"
              + " CASE WHEN is_sales_interstate =1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END as sgst,\n"
              + " scm_sales_invoice_item.value_taxable+scm_sales_invoice_item.value_igst as net_amount            \n"
              + " FROM scm_sales_invoice,scm_sales_invoice_item,scm_customer,scm_account,scm_vendor,scm_product,scm_product_detail,scm_product_category,\n"
              + " scm_product_batch LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id\n"
              + " WHERE scm_sales_invoice.sales_invoice_status_id = ? AND\n"
              + " scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + " AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + " AND scm_account.vendor_id =  scm_vendor.id AND scm_account.id = scm_product_detail.account_id\n"
              + " AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + " AND scm_product_batch.product_id = scm_product.id\n"
              + " AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + " AND scm_product.product_category_id = scm_product_category.id  \n"
              + " AND (scm_sales_invoice_item.value_prod_piece_selling - scm_sales_invoice_item.prod_piece_selling_forced >0 OR \n"
              + "	scm_sales_invoice_item.scheme_discount_derived - scm_sales_invoice_item.scheme_discount_actual > 0 OR\n"
              + "	scm_sales_invoice_item.product_discount_derived - scm_sales_invoice_item.product_discount_actual > 0 OR\n"
              + "	scm_sales_invoice_item.invoice_discount_derived - scm_sales_invoice_item.invoice_discount_actual > 0 )    ";

      sqlOrderBy = " ORDER BY UPPER(scm_vendor.vendor_name),scm_sales_invoice.invoice_date,scm_sales_invoice.invoice_no ";

    } else if ("2".equals(filterParameters.getReportType())) {  //CUSTOMER_PRODUCT_WISE = 2;
      sql = "SELECT scm_sales_invoice.id,scm_vendor.vendor_name as supplier_name,scm_sales_invoice.invoice_no as invoice_no,scm_sales_invoice.invoice_date as invoice_date, \n"
              + " scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
              + " scm_product.product_name as product_name,scm_product.hsn_code as hsn_code,getPackDimension(scm_product.id) as pack_type, \n"
              + " scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,            	    \n"
              + " CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END as qty,\n"
              + " CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN 0 ELSE COALESCE (scm_sales_invoice_item.product_qty_free,0) END as qty_free, \n"
              + " scm_sales_invoice_item.value_prod_piece_selling as value_pts,\n"
              + " CASE WHEN (scm_sales_invoice_item.value_prod_piece_selling - scm_sales_invoice_item.prod_piece_selling_forced)>0 \n"
              + " THEN (scm_sales_invoice_item.value_prod_piece_selling - scm_sales_invoice_item.prod_piece_selling_forced) ELSE 0 END  as rate_deviation,\n"
              + " scm_sales_invoice_item.value_mrp as value_mrp,\n"
              + " scm_sales_invoice_item.scheme_discount_value as scheme_discount,\n"
              + " CASE WHEN (scm_sales_invoice_item.scheme_discount_derived - scm_sales_invoice_item.scheme_discount_actual)>0 \n"
              + " THEN (scm_sales_invoice_item.scheme_discount_derived - scm_sales_invoice_item.scheme_discount_actual)*\n"
              + " CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END ELSE 0 END  as scheme_disc_deviation,\n"
              + " scm_sales_invoice_item.product_discount_value as product_discount,\n"
              + " CASE WHEN (scm_sales_invoice_item.product_discount_derived - scm_sales_invoice_item.product_discount_actual)>0 \n"
              + " THEN (scm_sales_invoice_item.product_discount_derived - scm_sales_invoice_item.product_discount_actual)*\n"
              + " CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END ELSE 0 END  as product_disc_deviation,\n"
              + " scm_sales_invoice_item.invoice_discount_derived as invoice_discount, \n"
              + " CASE WHEN (scm_sales_invoice_item.invoice_discount_derived - scm_sales_invoice_item.invoice_discount_actual)>0 \n"
              + " THEN (scm_sales_invoice_item.invoice_discount_derived - scm_sales_invoice_item.invoice_discount_actual)*\n"
              + " CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END ELSE 0 END  as invoice_disc_deviation,\n"
              + " scm_sales_invoice_item.value_goods as goods_value,scm_sales_invoice_item.landing_rate,\n"
              + " CASE WHEN is_sales_interstate =1 THEN scm_sales_invoice_item.value_igst ELSE 0 END as igst,\n"
              + " CASE WHEN is_sales_interstate =1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END as cgst,\n"
              + " CASE WHEN is_sales_interstate =1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END as sgst,\n"
              + " scm_sales_invoice_item.value_taxable+scm_sales_invoice_item.value_igst as net_amount            \n"
              + " FROM scm_sales_invoice,scm_sales_invoice_item,scm_customer,scm_account,scm_vendor,scm_product,scm_product_detail,scm_product_category,\n"
              + " scm_product_batch LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id\n"
              + " WHERE scm_sales_invoice.sales_invoice_status_id = ? AND\n"
              + " scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + " AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + " AND scm_account.vendor_id =  scm_vendor.id AND scm_account.id = scm_product_detail.account_id\n"
              + " AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + " AND scm_product_batch.product_id = scm_product.id\n"
              + " AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + " AND scm_product.product_category_id = scm_product_category.id  \n"
              + " AND (scm_sales_invoice_item.value_prod_piece_selling - scm_sales_invoice_item.prod_piece_selling_forced >0 OR \n"
              + "	scm_sales_invoice_item.scheme_discount_derived - scm_sales_invoice_item.scheme_discount_actual > 0 OR\n"
              + "	scm_sales_invoice_item.product_discount_derived - scm_sales_invoice_item.product_discount_actual > 0 OR\n"
              + "	scm_sales_invoice_item.invoice_discount_derived - scm_sales_invoice_item.invoice_discount_actual > 0 )    ";

      sqlOrderBy = " ORDER BY UPPER(scm_vendor.vendor_name),scm_sales_invoice.invoice_date,scm_sales_invoice.invoice_no ";

    }

    ArrayList<Object> params = new ArrayList<>();
    params.add(SystemConstants.CONFIRMED);
    params.add(company.getId());

    if (filterParameters.getCustomer() != null && filterParameters.getCustomer().getId() != null) {
      sqlCondition += " AND scm_sales_invoice.customer_id = ?";
      params.add(filterParameters.getCustomer().getId());
    }
    if (filterParameters.getAccount() != null && filterParameters.getAccount().getId() != null) {
      sqlCondition += " AND scm_product_detail.account_id = ?";
      params.add(filterParameters.getAccount().getId());
    }

    if (filterParameters.getFromDate() != null) {
      sqlCondition += " AND scm_sales_invoice.invoice_date >= ?::date";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    }
    if (filterParameters.getToDate() != null) {
      sqlCondition += " AND scm_sales_invoice.invoice_date <= ?::date";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    }
    sql = sql + sqlCondition + sqlOrderBy;

    return AppDb.getList(main.dbConnector(), SalesRateDifference.class, sql, params.toArray());
  }
}
