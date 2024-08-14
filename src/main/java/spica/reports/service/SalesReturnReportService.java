/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import spica.constant.ReportConstant;
import spica.reports.model.FilterParameters;

import spica.reports.model.SalesReturnReport;
import spica.scm.domain.Company;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author sujesh
 */
public class SalesReturnReportService {

  public static List<SalesReturnReport> getSalesReturnReportList(Main main, Company company, FilterParameters filterParameters, int type) {
    String sql = "";
    String sqlCondition = " AND ret.company_id =?";
    String sqlOrderBy = " ";
    String sqlGroupBy = " ";

    /*sql = "SELECT sales_return_id,product_detail_hash,scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,scm_customer.state_id as state,scm_product.product_name,getPackDimension(scm_product.id) as pack_type ,\n"
            + "scm_product.hsn_code as hsn_code,T_Dist.district_name as district_name,T_Terr.territory_name as territory_name,\n"
            + "scm_sales_return.invoice_no as invoice_no,scm_sales_return.invoice_date as invoice_date,scm_sales_return.entry_date as entry_date,\n"
            + "scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,\n"
            + "scm_sales_return_item.value_mrp as value_mrp,scm_sales_return_item.landing_price_per_piece_company as rate,\n"
            + "scm_sales_return_item.value_goods as goods_value,scm_manufacture.code as mfr_code,\n"
            + "scm_sales_return_item.scheme_discount_value as scheme_discount,\n"
            + "(scm_sales_return_item.product_discount_value_derived*CASE WHEN sales_return_type = 1 THEN scm_sales_return_item.product_quantity+COALESCE(scm_sales_return_item.product_quantity_free,0) ELSE scm_sales_return_item.product_quantity_damaged END) as product_discount,\n"
            + "(scm_sales_return_item.invoice_discount_value_derived*CASE WHEN sales_return_type = 1 THEN scm_sales_return_item.product_quantity+COALESCE(scm_sales_return_item.product_quantity_free,0) ELSE scm_sales_return_item.product_quantity_damaged END) as invoice_discount, \n"
            + "COALESCE(scm_sales_return_item.value_cgst,0.0) as cgst,COALESCE(scm_sales_return_item.value_sgst,0.0) as sgst, COALESCE(scm_sales_return_item.value_igst,0.0) as igst,\n"
            + "scm_sales_return_item.value_igst as gst_amount,\n"
            + "(scm_sales_return_item.value_assessable+scm_sales_return_item.value_igst) as net_amount,\n"
            + "CASE WHEN sales_return_type = 1 THEN 'Non-Moving / Near Expiry' ELSE 'Damaged & Expired' END as return_type ,\n"
            + "CASE WHEN sales_return_type = 1 THEN scm_sales_return_item.product_quantity ELSE scm_sales_return_item.product_quantity_damaged END  as qty,\n"
            + "scm_sales_return_item.product_quantity_free as free_qty\n"
            + "FROM scm_sales_return \n"
            + "JOIN scm_customer ON scm_sales_return.customer_id = scm_customer.id\n"
            + "LEFT JOIN scm_customer_address ON scm_customer.id = scm_customer_address.customer_id\n"
            + "LEFT JOIN scm_district T_Dist ON scm_customer_address.district_id = T_Dist.id\n"
            + "LEFT JOIN scm_territory T_Terr ON scm_customer_address.territory_id = T_Terr.id,\n"
            + "scm_sales_return_item,scm_product_detail,scm_product,scm_product_category,scm_product_batch \n"
            + "LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id\n"
            + "WHERE scm_sales_return.sales_return_status_id > ?\n"
            + "AND scm_sales_return.id = scm_sales_return_item.sales_return_id\n"
            + "AND scm_sales_return_item.product_detail_id=scm_product_detail.id\n"
            + "AND scm_sales_return.customer_id=scm_customer.id\n"
            + "AND scm_product_batch.product_id=scm_product.id\n"
            + "AND scm_product_detail.product_batch_id=scm_product_batch.id\n"
            + "AND scm_product.product_category_id=scm_product_category.id\n"
            + "AND scm_sales_return.account_group_id=?";*/
    if (filterParameters.getReportType() != null && filterParameters.getReportType().equals(String.valueOf(ReportConstant.PRETURN_INVOICE_PRODUCT_WISE))) {
      sql = "select cust.customer_name,cust.gst_no,ret.id,ret.invoice_no,ret.entry_date,ret.invoice_date,man.code as mfr_code,prod.product_name,\n"
              + "ret_item.hsn_code,getPackDimension(prod.id) as pack_type,batch.batch_no,batch.expiry_date_actual as expiry_date,cust.state_id as state,\n"
              + "dist.district_name,ter.territory_name,ret.account_invoice_no as reg_no,\n"
              + "SUM((CASE WHEN ret.sales_return_type = 1 THEN ret_item.product_quantity else ret_item.product_quantity_damaged END ))as qty,\n"
              + "0 as qty_free,ret_item.value_mrp,ret_item.value_rate as rate,ret_item.value_pts,ret_item.value_ptr,SUM(COALESCE(ret_item.scheme_discount_value,0)) as scheme_discount,\n"
              + "SUM(COALESCE(ret_item.product_discount_value,0)) as product_discount,SUM(COALESCE(ret_item.invoice_discount_value,0)),SUM(COALESCE(ret_item.value_goods,0)) as goods_value,\n"
              + "SUM(COALESCE(ret_item.value_assessable,0)) as return_value,\n"
              + "null as state_cess,SUM(COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone!=1 THEN 0 ELSE ret_item.value_igst END,0)) as igst,\n"
              + "SUM(COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone!=1 THEN ret_item.value_cgst ELSE 0 END,0)) as cgst, \n"
              + "SUM(COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone!=1 THEN ret_item.value_sgst ELSE 0 END,0)) as sgst,\n"
              + "SUM(COALESCE(ret_item.value_igst,0)) as gst_amount,SUM((ret_item.value_assessable+COALESCE(ret_item.value_igst,0))) as net_amount,\n"
              + "SUM(COALESCE(ret_item.cash_discount_value,0)) as cash_discount,\n"
              + "SUM((ret_item.value_assessable+COALESCE(ret_item.value_igst,0))) as invoice_amount,\n"
              + "SUM(COALESCE(ret_item.value_assessable,0)) as return_value,0 as split_invoice,\n"
              + "CASE WHEN ret.sales_return_type = 1 THEN 'Non-Moving / Near Expiry' ELSE 'Damaged & Expired' END as return_type\n"
              + "from scm_sales_return ret\n"
              + "LEFT JOIN scm_territory ter on ret.territory_id = ter.id\n"
              + ",scm_sales_return_item ret_item,scm_customer cust,scm_company_address comp_add,scm_customer_address c_add,scm_district dist,\n"
              + "scm_product_detail prod_det,scm_product prod,scm_product_batch batch\n"
              + "LEFT JOIN scm_manufacture man on man.id=batch.manufacture_id\n"
              + "where ret.sales_return_status_id = 2 and ret.id=ret_item.sales_return_id and ret.customer_id=cust.id  \n"
              + "and dist.id=c_add.district_id\n"
              + "and prod_det.id=ret_item.product_detail_id and batch.id=prod_det.product_batch_id and prod.id=batch.product_id \n"
              + "and comp_add.company_id =ret.company_id and ret.customer_address_id=c_add.id \n";
      sqlGroupBy = " GROUP BY cust.customer_name,cust.gst_no,ret.id,ret.invoice_no,ret.entry_date,ret.invoice_date,man.code,prod.product_name,ret_item.hsn_code,\n"
              + " pack_type,batch.batch_no,batch.expiry_date_actual,cust.state_id,ret_item.value_mrp,ret_item.value_rate,ret_item.value_pts,ret_item.value_ptr,\n"
              + "dist.district_name,ret.account_invoice_no,ter.territory_name \n";

    } else {
      sql = "select ret.invoice_no,ret.account_invoice_no as reg_no,ret.invoice_date,ret.entry_date,cust.customer_name,cust.gst_no,dist.district_name,ter.territory_name,c_add.pin as pin_code,ret.id,\n"
              + "SUM(COALESCE(ret_item.value_goods,0)) as goods_value,SUM(COALESCE(ret_item.scheme_discount_value,0)) as scheme_discount,\n"
              + "SUM(COALESCE(ret_item.product_discount_value,0)) as product_discount,SUM(COALESCE(ret_item.invoice_discount_value,0)) as invoice_discount,\n"
              + "null as state_cess,COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone!=1 THEN 0 ELSE ret.invoice_amount_igst END,0) as igst,\n"
              + "COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone!=1 THEN ret.invoice_amount_cgst ELSE 0 END,0) as cgst,\n"
              + "COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone!=1 THEN ret.invoice_amount_sgst ELSE 0 END,0) as sgst,\n"
              + "ret.invoice_amount_igst as gst_amount,ret.invoice_amount as invoice_amount,ret.invoice_amount as net_amount,\n"
              + "ret.invoice_amount_assessable as sales_value,\n"
              + "SUM(COALESCE(ret_item.cash_discount_value,0)) as cash_discount,COALESCE(ret.invoice_amount_assessable,0) as return_value,0 as split_invoice,\n"
              + "CASE WHEN ret.sales_return_type = 1 THEN 'Non-Moving / Near Expiry' ELSE 'Damaged & Expired' END as return_type \n"
              + "from scm_sales_return ret\n"
              + "LEFT JOIN scm_territory ter on ter.id=ret.territory_id,scm_sales_return_item ret_item,\n"
              + "scm_customer cust,scm_customer_address c_add,scm_district dist,scm_company_address comp_add\n"
              + "where ret_item.sales_return_id=ret.id and ret.sales_return_status_id=2\n"
              + "and ret.customer_id=cust.id  and c_add.id=ret.customer_address_id and c_add.district_id=dist.id\n"
              + "and comp_add.company_id=ret.company_id\n";

      sqlGroupBy = " GROUP BY ret.invoice_no,ret.account_invoice_no,ret.entry_date,ret.invoice_date,cust.customer_name,cust.gst_no,dist.district_name,ter.territory_name,c_add.pin,ret.id,comp_add.state_id,\n"
              + "c_add.state_id,ret.invoice_amount_igst,ret.invoice_amount_cgst,ret.invoice_amount_sgst,ret.invoice_amount,cust.taxable,invoice_amount_assessable ";
    }

    sqlOrderBy += " ORDER BY entry_date,invoice_no ";

    ArrayList<Object> params = new ArrayList<>();
    params.add(company.getId());
    if (filterParameters.getAccountGroup() != null) {
      sqlCondition += " AND ret.account_group_id=? ";
      params.add(filterParameters.getAccountGroup().getId());
    }

    if (filterParameters.getAccount() != null && filterParameters.getAccount().getId() != null) {
      sqlCondition += " AND ret_item.account_id = ?";
      params.add(filterParameters.getAccount().getId());
    }

    if (filterParameters.getCustomer() != null && filterParameters.getCustomer().getId() != null) {
      sqlCondition += " AND ret.customer_id = ?";
      params.add(filterParameters.getCustomer().getId());
    }

    if (filterParameters.getFromDate() != null) {
      sqlCondition += " AND ret.entry_date >= ?::date";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    }
    if (filterParameters.getToDate() != null) {
      sqlCondition += " AND ret.entry_date <= ?::date";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    }
    if (type > 0) {
      sqlCondition += " AND ret.sales_return_type = ? ";
      params.add(type);
    }
    sql = sql + sqlCondition + sqlGroupBy + sqlOrderBy;
    return AppDb.getList(main.dbConnector(), SalesReturnReport.class, sql, params.toArray());
  }
}
