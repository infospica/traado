/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import spica.reports.model.PurchaseReturnReport;
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
public class PurchaseReturnReportService {

  public static List<PurchaseReturnReport> getPurchaseReturnReportList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "";
    String sqlCondition = " AND scm_account.company_id =?";
    String sqlOrderBy = "";
    String sqlGroupBy = "";

    if (filterParameters == null || filterParameters.getReportType() == null || "1".equals(filterParameters.getReportType())) {  //INVOICE_WISE = 1;
      sql = "SELECT \n"
              + "scm_purchase_return.id,scm_vendor.vendor_name as supplier_name,scm_vendor.gst_no as gst_no,\n"
              + "scm_purchase_return.invoice_no as invoice_no,scm_purchase_return.invoice_date as invoice_date,\n"
              + "scm_purchase_return.invoice_amount_net as goods_value,\n"
              + "(CASE WHEN scm_purchase_return.is_return_interstate = 1 OR scm_purchase_return.sez_zone = 1 THEN scm_purchase_return.invoice_amount_igst ELSE 0 END)as igst,"
              + "(CASE WHEN scm_purchase_return.is_return_interstate = 1 OR scm_purchase_return.sez_zone = 1 THEN 0 ELSE scm_purchase_return.invoice_amount_cgst END) as cgst,"
              + "(CASE WHEN scm_purchase_return.is_return_interstate = 1 OR scm_purchase_return.sez_zone = 1 THEN 0 ELSE scm_purchase_return.invoice_amount_sgst END) as sgst,\n"
              + "scm_purchase_return.invoice_amount as net_amount,SUM(COALESCE(scm_purchase_return_item.scheme_discount_value,0)) as scheme_discount ,\n"
              + "scm_purchase_return_item.scheme_discount_percentage, SUM(COALESCE(scm_purchase_return_item.product_discount_value,0)) as product_discount,\n"
              + "scm_purchase_return_item.product_discount_perc as product_discount_percentage,\n"
              + "CASE WHEN purchase_return_stock_cat = 1 THEN 'Damaged & Expired' ELSE 'Non-Moving / Near Expiry' END as return_type,\n"
              + "scm_purchase_return.no_of_boxes as box_count,scm_purchase_return.entry_date as entry_date\n"
              + "FROM scm_purchase_return,scm_purchase_return_item,\n"
              + "scm_account,scm_vendor\n"
              + "WHERE\n"
              + "scm_purchase_return.purchase_return_status_id > ? \n"
              + "AND scm_purchase_return.id = scm_purchase_return_item.purchase_return_id\n"
              + "AND scm_account.vendor_id =  scm_vendor.id AND scm_account.id = scm_purchase_return.account_id \n";
      sqlGroupBy = "GROUP BY scm_purchase_return.id,scm_vendor.vendor_name,scm_vendor.gst_no,scm_purchase_return.invoice_no,scm_purchase_return.invoice_date,\n"
              + "scm_purchase_return.invoice_amount_net,scm_purchase_return.invoice_amount_cgst,scm_purchase_return.invoice_amount_sgst,scm_purchase_return.invoice_amount_igst,\n"
              + "scm_purchase_return.invoice_amount,scm_purchase_return_item.scheme_discount_percentage,scm_purchase_return_item.product_discount_perc,\n"
              + "return_type,scm_purchase_return.no_of_boxes,scm_purchase_return.entry_date \n";
      sqlOrderBy = " ORDER BY UPPER(scm_vendor.vendor_name),scm_purchase_return.invoice_date,scm_purchase_return.invoice_no ";

    } else if ("2".equals(filterParameters.getReportType())) {  //INVOICE_PRODUCT_WISE = 2;
      sql = "SELECT \n"
              + "scm_purchase_return.id,scm_vendor.vendor_name as supplier_name,scm_vendor.gst_no as gst_no,\n"
              + "scm_purchase_return.invoice_no as invoice_no,scm_purchase_return.invoice_date as invoice_date,\n"
              + "scm_manufacture.code as mfr_code,scm_product.product_name as product_name,scm_purchase_return_item.hsn_code as hsn_code,\n"
              + "getPackDimension(scm_product.id) as pack_type,scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,\n"
              + "scm_purchase_return_item.quantity_returned  as qty,scm_purchase_return_item.value_mrp as value_mrp,scm_purchase_return_item.landing_price_per_piece_company as rate,\n"
              + "scm_purchase_return_item.value_goods as goods_value,\n"
              + "(CASE WHEN scm_purchase_return.is_return_interstate = 1 OR scm_purchase_return.sez_zone = 1 THEN scm_purchase_return_item.value_igst ELSE 0 END)as igst,"
              + "(CASE WHEN scm_purchase_return.is_return_interstate = 1 OR scm_purchase_return.sez_zone = 1 THEN 0 ELSE scm_purchase_return_item.value_cgst END) as cgst,"
              + "(CASE WHEN scm_purchase_return.is_return_interstate = 1 OR scm_purchase_return.sez_zone = 1 THEN 0 ELSE scm_purchase_return_item.value_sgst END) as sgst,\n" 
              + "(scm_purchase_return_item.value_goods + scm_purchase_return_item.value_igst) as net_amount,\n"
              + "scm_purchase_return_item.scheme_discount_value as scheme_discount,scm_purchase_return_item.scheme_discount_percentage,\n"
              + "scm_purchase_return_item.product_discount_value as product_discount,scm_purchase_return_item.product_discount_perc as product_discount_percentage,\n"
              + "CASE WHEN purchase_return_stock_cat = 1 THEN 'Damaged & Expired' ELSE 'Non-Moving / Near Expiry' END as return_type,\n"
              + "scm_purchase_return.no_of_boxes as box_count,scm_purchase_return.entry_date as entry_date\n"
              + "FROM scm_purchase_return,scm_purchase_return_item,\n"
              + "scm_account,scm_vendor,\n"
              + "scm_product,scm_product_detail,scm_product_category,scm_product_batch LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id\n"
              + "WHERE\n"
              + "scm_purchase_return.purchase_return_status_id > ? \n"
              + "AND scm_account.vendor_id =  scm_vendor.id AND scm_account.id = scm_purchase_return.account_id\n"
              + "AND scm_purchase_return.id = scm_purchase_return_item.purchase_return_id\n"
              + "AND scm_purchase_return_item.product_detail_id = scm_product_detail.id\n"
              + "AND scm_product_batch.product_id = scm_product.id\n"
              + "AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "AND scm_product.product_category_id = scm_product_category.id \n";

      sqlOrderBy = " ORDER BY UPPER(scm_vendor.vendor_name),scm_purchase_return.invoice_date,scm_purchase_return.invoice_no ";

    } 

    ArrayList<Object> params = new ArrayList<>();
    params.add(SystemConstants.DRAFT);
    params.add(company.getId());

    if (filterParameters.getAccount() != null && filterParameters.getAccount().getId() != null) {
      sqlCondition += " AND scm_purchase_return.account_id = ? ";
      params.add(filterParameters.getAccount().getId());
    }

    if (filterParameters.getFromDate() != null) {
      sqlCondition += " AND scm_purchase_return.entry_date >= ?::date ";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    }
    if (filterParameters.getToDate() != null) {
      sqlCondition += " AND scm_purchase_return.entry_date <= ?::date ";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    }
    sql = sql + sqlCondition + sqlGroupBy + sqlOrderBy;

    return AppDb.getList(main.dbConnector(), PurchaseReturnReport.class, sql, params.toArray());
  }
}
