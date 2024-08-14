/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import spica.constant.ReportConstant;
import spica.reports.model.CompanyCustomerSales;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Company;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author sujesh
 */
public abstract class SalesAgentReportService {



  public static List<CompanyCustomerSales> getCompanyCustomerSalesList(Main main, Company company, FilterParameters filterParameters, Integer status) {
    ArrayList<Object> params = new ArrayList<>();
    String sql = "";
    String salesSql = "";
    String salesReturnSql = "";
    String sqlSalesCondition = " AND scm_sales_invoice.company_id =? ";
    String sqlSalesReturnCondition = " AND scm_sales_return.company_id =? ";
    String sqlGroupBy = "";
    String sqlOrderBy = "";
    String sqlReturnGroupBy = "";
    String sqlReturnOrderBy = "";

    if ("1".equals(filterParameters.getReportType())) {  //INVOICE_WISE = 1;
      salesSql = "SELECT scm_sales_invoice.id,scm_sales_invoice.invoice_no as invoice_no,scm_sales_invoice.invoice_entry_date as entry_date, \n"
              + "scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,t2.name as agentName,\n"
              + "T_Dist.district_name as district_name,T_Terr.territory_name as territory_name, scm_sales_invoice.customer_pin as pin_code,	    \n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_goods,0)) as goods_value,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.scheme_discount_value,0)) as scheme_discount,SUM(COALESCE(scm_sales_invoice_item.product_discount_value,0)) as product_discount,\n"
              + "COALESCE(scm_sales_invoice.invoice_amount_discount,0) as invoice_discount,             \n"
              + "COALESCE(scm_sales_invoice.cash_discount_value,0) as cash_discount, \n"
              + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN scm_sales_invoice.invoice_amount_igst ELSE 0 END,0) as igst,\n"
              + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN 0 ELSE scm_sales_invoice.invoice_amount_igst/2.0 END,0) as cgst,\n"
              + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN 0 ELSE scm_sales_invoice.invoice_amount_igst/2.0 END,0) as sgst,\n"
              + "scm_sales_invoice.invoice_amount_igst as gst_amount,\n"
              + "scm_sales_invoice.kerala_flood_cess_net_value as kf_cess,\n"
              + "scm_sales_invoice.invoice_amount as net_amount,            \n"
              + "scm_sales_invoice.invoice_amount_assessable as sales_value,'1' as invoice_or_return \n"
              + "FROM \n"
              + "scm_sales_invoice \n"
              + " LEFT JOIN scm_sales_agent t2 ON scm_sales_invoice.company_sales_agent_person_profile_id = t2.id "
              + "LEFT JOIN scm_district T_Dist ON customer_district_id = T_Dist.id\n"
              + "LEFT JOIN scm_territory T_Terr ON customer_territory_id = T_Terr.id\n"
              + ",scm_sales_invoice_item,scm_customer,scm_account,scm_product_detail,scm_product,scm_product_batch,scm_product_category"
              + "   WHERE scm_sales_invoice.sales_invoice_status_id >= ? AND\n"
              + "   scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + "   AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + "   AND scm_account.id = scm_product_detail.account_id\n"
              + "   AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + "   AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "   AND scm_product_batch.product_id = scm_product.id\n"
              + "   AND scm_product.product_category_id = scm_product_category.id     ";

      sqlGroupBy = " GROUP BY scm_sales_invoice.id,scm_customer.customer_name ,scm_customer.gst_no ,\n"
              + "T_Dist.district_name ,T_Terr.territory_name , scm_sales_invoice.customer_pin ,\n"
              + "scm_sales_invoice.invoice_no ,scm_sales_invoice.invoice_entry_date ,\n"
              + "scm_sales_invoice.invoice_amount_igst,scm_sales_invoice.invoice_amount_cgst,scm_sales_invoice.invoice_amount_sgst,scm_sales_invoice.kerala_flood_cess_net_value,\n"
              + "scm_sales_invoice.invoice_amount_discount,scm_sales_invoice.invoice_amount_igst,scm_sales_invoice.invoice_amount,scm_sales_invoice.cash_discount_value,scm_sales_invoice.invoice_amount_assessable,scm_sales_invoice.is_sales_interstate,t2.name \n";
      sqlOrderBy = " ORDER BY scm_sales_invoice.invoice_entry_date,scm_sales_invoice.invoice_no ASC";

      salesReturnSql = "SELECT scm_sales_return.id,scm_sales_return.invoice_no as invoice_no,scm_sales_return.entry_date as entry_date,\n"
              + "scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,t2.name as agentName,\n"
              + "T_Dist.district_name as district_name,T_Terr.territory_name as territory_name,scm_customer_address.pin as pin_code,\n"
              + "SUM(COALESCE(-scm_sales_return_item.value_goods,0)) as goods_value,\n"
              + "SUM(COALESCE(-scm_sales_return_item.scheme_discount_value,0)) as scheme_discount,\n"
              + "SUM(COALESCE(-scm_sales_return_item.product_discount_value,0)) as product_discount,\n"
              + "SUM(COALESCE(-scm_sales_return_item.invoice_discount_value,0)) as invoice_discount,\n"
              + "SUM(COALESCE(-scm_sales_return_item.cash_discount_value,0)) as cash_discount, \n"
              + "COALESCE(CASE WHEN scm_customer.state_id = scm_company.state_id AND scm_sales_return.sez_zone= 0 THEN 0 ELSE -scm_sales_return.invoice_amount_igst END,0) as igst,\n"
              + "COALESCE(CASE WHEN scm_customer.state_id = scm_company.state_id AND scm_sales_return.sez_zone= 0 THEN -scm_sales_return.invoice_amount_cgst ELSE 0 END,0) as cgst,\n"
              + "COALESCE(CASE WHEN scm_customer.state_id = scm_company.state_id AND scm_sales_return.sez_zone= 0 THEN -scm_sales_return.invoice_amount_sgst ELSE 0 END,0) as sgst,\n"
              + "-scm_sales_return.invoice_amount_igst as gst_amount,\n"
              + "0 as kf_cess,\n"
              + "-scm_sales_return.invoice_amount as net_amount, \n"
              + "-scm_sales_return.invoice_amount_assessable as sales_value,'2' as invoice_or_return\n"
              + "FROM \n"
              + "scm_sales_return \n"
              + "JOIN scm_customer ON scm_sales_return.customer_id = scm_customer.id\n"
              + "LEFT JOIN scm_customer_address ON scm_customer.id = scm_customer_address.customer_id AND scm_customer_address.address_type_id=1\n"
              + "LEFT JOIN scm_sales_agent t2 ON scm_sales_return.sales_agent_id = t2.id LEFT JOIN scm_district T_Dist ON scm_customer_address.district_id = T_Dist.id\n"
              + "LEFT JOIN scm_territory T_Terr ON scm_customer_address.territory_id = T_Terr.id,\n"
              + "scm_sales_return_item,scm_account,scm_product_detail,scm_product,scm_product_batch,scm_product_category,scm_company\n"
              + "WHERE scm_sales_return.sales_return_status_id >= ? \n"
              + "AND scm_sales_return.id = scm_sales_return_item.sales_return_id\n"
              + "AND scm_account.id = scm_product_detail.account_id\n"
              + "AND scm_sales_return_item.product_detail_id = scm_product_detail.id\n"
              + "AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "AND scm_product_batch.product_id = scm_product.id\n"
              + "AND scm_product.product_category_id = scm_product_category.id    \n"
              + "AND scm_customer.company_id = scm_company.id\n";

      sqlReturnGroupBy = " GROUP BY scm_sales_return.id,scm_customer.customer_name ,scm_customer.gst_no ,\n"
              + "T_Dist.district_name ,T_Terr.territory_name ,\n"
              + "scm_sales_return.invoice_no ,scm_sales_return.entry_date ,\n"
              + "scm_sales_return.invoice_amount_igst,scm_sales_return.invoice_amount_cgst,scm_sales_return.invoice_amount_sgst,\n"
              + "scm_sales_return.invoice_amount_igst,scm_sales_return.invoice_amount,scm_customer.taxable,\n"
              + "scm_sales_return.invoice_amount_assessable,t2.name,scm_customer_address.pin,scm_customer.state_id,scm_company.state_id\n";

      sqlReturnOrderBy = " ORDER BY scm_sales_return.entry_date,scm_sales_return.invoice_no ASC ";

    } else if ("2".equals(filterParameters.getReportType())) {  //INVOICE_PRODUCT_WISE = 2;
      salesSql = "SELECT scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,scm_sales_invoice.id,\n"
              + "scm_sales_invoice.company_sales_agent_person_profile_id as agent,t2.name as agentName,\n"
              + "scm_sales_invoice.invoice_no as invoice_no,scm_sales_invoice.invoice_entry_date as entry_date,\n"
              + "scm_manufacture.code as mfr_code,\n"
              + "scm_product.product_name as product_name,scm_sales_invoice_item.hsn_code as hsn_code,getPackDimension(scm_product.id) as pack_type,\n"
              + "scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,\n"
              + "SUM(CASE WHEN scm_sales_invoice_item.quantity_or_free =0 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END) as qty,\n"
              + "SUM(CASE WHEN scm_sales_invoice_item.quantity_or_free =0 THEN 0 ELSE COALESCE (scm_sales_invoice_item.product_qty_free,0) END) as qty_free,\n"
              + "scm_sales_invoice_item.value_mrp as value_mrp,\n"
              + "scm_sales_invoice_item.value_prod_piece_selling as value_pts,scm_sales_invoice_item.value_ptr as value_ptr,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.scheme_discount_value,0)) as scheme_discount,SUM(COALESCE(scm_sales_invoice_item.product_discount_value,0)) as product_discount,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.invoice_discount_value,0)) as invoice_discount,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.cash_discount_value,0)) as cash_discount,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_goods,0)) as goods_value,\n"
              + "SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN scm_sales_invoice_item.value_igst ELSE 0 END) as igst,\n"
              + "SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN 0 ELSE scm_sales_invoice_item.value_igst/2.0 END) as cgst,\n"
              + "SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN 0 ELSE scm_sales_invoice_item.value_igst/2.0 END) as sgst,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_igst,0)) as gst_amount,SUM(COALESCE(scm_sales_invoice_item.kerala_flood_cess_value,0)) as kf_cess,\n"
              + "SUM(scm_sales_invoice_item.value_taxable+scm_sales_invoice_item.value_igst) as net_amount,\n"
              + "SUM(scm_sales_invoice_item.value_taxable) as sales_value,\n"
              + "SUM((COALESCE(scm_product_entry_detail.value_rate_per_prod_piece_der,0)*CASE WHEN scm_sales_invoice_item.quantity_or_free =0 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END)) as purchase_value,\n"
              + "'1' as invoice_or_return,scm_district.district_name\n"
              + "FROM\n"
              + "scm_sales_invoice LEFT JOIN scm_sales_agent t2 ON scm_sales_invoice.company_sales_agent_person_profile_id = t2.id\n"
              + ",scm_sales_invoice_item,scm_product_entry_detail,scm_customer,scm_district,scm_account,scm_product_detail,scm_product,scm_product_category,\n"
              + "scm_product_batch LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id\n"
              + "WHERE scm_sales_invoice.sales_invoice_status_id >=? AND\n"
              + "scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + "AND scm_sales_invoice_item.product_entry_detail_id= scm_product_entry_detail.id\n"
              + "AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + "AND scm_district.id = scm_sales_invoice.customer_district_id\n"
              + "AND scm_account.id = scm_product_detail.account_id\n"
              + "AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + "AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "AND scm_product_batch.product_id = scm_product.id\n"
              + "AND scm_product.product_category_id = scm_product_category.id\n";

      sqlGroupBy = " GROUP BY scm_customer.customer_name ,scm_customer.gst_no,scm_sales_invoice.id,scm_sales_invoice.company_sales_agent_person_profile_id,t2.name,\n"
              + "scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_entry_date,scm_manufacture.code ,scm_product.product_name ,scm_sales_invoice_item.hsn_code ,pack_type,\n"
              + "scm_product_batch.batch_no ,scm_product_batch.expiry_date_actual ,scm_sales_invoice_item.value_mrp,\n"
              + "scm_sales_invoice_item.value_prod_piece_selling,scm_sales_invoice_item.value_ptr,scm_district.district_name ";

      sqlOrderBy = " ORDER BY scm_sales_invoice.invoice_entry_date,scm_sales_invoice.invoice_no ";

      salesReturnSql = "SELECT scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,scm_sales_return.id,scm_sales_return.sales_agent_id as agent,t2.name as agentName, \n"
              + "scm_sales_return.invoice_no as invoice_no,scm_sales_return.entry_date as entry_date,\n"
              + "scm_manufacture.code as mfr_code,\n"
              + "scm_product.product_name as product_name,scm_sales_return_item.hsn_code as hsn_code,getPackDimension(scm_product.id) as pack_type,\n"
              + "scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,\n"
              + "-SUM(CASE WHEN sales_return_type = 1 THEN scm_sales_return_item.product_quantity ELSE scm_sales_return_item.product_quantity_damaged END)as qty,\n"
              + "-SUM(CASE WHEN scm_sales_return_item.quantity_or_free =1 THEN 0 ELSE COALESCE (-scm_sales_return_item.product_quantity_free,0) END) as qty_free,\n"
              + "scm_sales_return_item.value_mrp as value_mrp,\n"
              + "scm_sales_return_item.value_pts as value_pts,scm_sales_return_item.value_ptr as value_ptr,\n"
              + "-SUM(COALESCE(scm_sales_return_item.scheme_discount_value,0)) as scheme_discount,-SUM(COALESCE(scm_sales_return_item.product_discount_value,0)) as product_discount,\n"
              + "-SUM(COALESCE(scm_sales_return_item.invoice_discount_value,0)) as invoice_discount, \n"
              + "-SUM(COALESCE(scm_sales_return_item.cash_discount_value,0)) as cash_discount,\n"
              + "-SUM(COALESCE(scm_sales_return_item.value_goods,0)) as goods_value,\n"
              + "-SUM(CASE WHEN scm_customer.state_id = scm_company.state_id AND scm_sales_return.sez_zone= 0 THEN 0 ELSE COALESCE(scm_sales_return_item.value_igst,0) END) as igst,\n"
              + "-SUM(CASE WHEN scm_customer.state_id = scm_company.state_id AND scm_sales_return.sez_zone= 0 THEN COALESCE(scm_sales_return_item.value_cgst,0) ELSE 0 END) as cgst,\n"
              + "-SUM(CASE WHEN scm_customer.state_id = scm_company.state_id AND scm_sales_return.sez_zone= 0 THEN COALESCE(scm_sales_return_item.value_sgst,0) ELSE 0 END) as sgst,\n"
              + "-SUM(COALESCE(scm_sales_return_item.value_igst,0)) as gst_amount,\n"
              + " 0 as kf_cess,-SUM(COALESCE(scm_sales_return_item.value_igst,0)+scm_sales_return_item.value_assessable) as net_amount,\n"
              + "-SUM(COALESCE(scm_sales_return_item.value_assessable,0)) as sales_value,\n"
              + "-SUM((COALESCE(scm_product_entry_detail.value_rate_per_prod_piece_der,0)*CASE WHEN sales_return_type = 1 THEN scm_sales_return_item.product_quantity ELSE scm_sales_return_item.product_quantity_damaged END)) as purchase_value,\n"
              + "'2' as invoice_or_return,scm_district.district_name \n"
              + "FROM scm_sales_return \n"
              + "JOIN scm_customer ON scm_sales_return.customer_id = scm_customer.id\n"
              + "LEFT JOIN scm_sales_agent t2 ON scm_sales_return.sales_agent_id = t2.id ,\n"
              + "scm_customer_address,scm_district,scm_sales_return_item,scm_product_entry_detail,scm_tax_code,scm_account,scm_product_detail,scm_product,scm_product_category,\n"
              + "scm_product_batch LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id,scm_company\n"
              + "WHERE scm_sales_return.sales_return_status_id = ?\n"
              + "AND scm_sales_return.id = scm_sales_return_item.sales_return_id \n"
              + "AND scm_sales_return_item.product_entry_detail_id= scm_product_entry_detail.id\n"
              + "AND scm_sales_return_item.tax_code_id =scm_tax_code.id \n"
              + "AND scm_sales_return.customer_id = scm_customer.id\n"
              + "AND scm_customer_address.customer_id = scm_customer.id AND scm_customer_address.address_type_id=1\n"
              + "AND scm_district.id = scm_customer_address.district_id\n"
              + "AND scm_account.id = scm_product_detail.account_id\n"
              + "AND scm_sales_return_item.product_detail_id = scm_product_detail.id\n"
              + "AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "AND scm_product_batch.product_id = scm_product.id\n"
              + "AND scm_product.product_category_id = scm_product_category.id\n"
              + "AND scm_customer.company_id = scm_company.id\n";

      sqlReturnGroupBy = " GROUP BY scm_customer.customer_name ,scm_customer.gst_no,scm_sales_return.id,scm_sales_return.sales_agent_id,t2.name,scm_sales_return.invoice_no,\n"
              + "scm_sales_return.entry_date,scm_manufacture.code ,scm_product.product_name,scm_sales_return_item.hsn_code,pack_type,\n"
              + "scm_product_batch.batch_no,scm_product_batch.expiry_date_actual,scm_sales_return_item.value_mrp,\n"
              + "scm_sales_return_item.value_pts,scm_sales_return_item.value_ptr,scm_district.district_name ";

      sqlReturnOrderBy = " ORDER BY scm_sales_return.entry_date,scm_sales_return.invoice_no ";
    }

    ArrayList<Object> salesParams = new ArrayList<>();
    salesParams.add(SystemConstants.CONFIRMED);
    salesParams.add(company.getId());

    ArrayList<Object> salesReturnParams = new ArrayList<>();
    salesReturnParams.add(SystemConstants.CONFIRMED);
    salesReturnParams.add(company.getId());
    if (status != null) {
      sqlSalesCondition += " AND t2.status_id = ? ";
      sqlSalesReturnCondition += " AND t2.status_id = ? ";
      salesParams.add(status);
      salesReturnParams.add(status);
    }

    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      sqlSalesCondition += " AND scm_sales_invoice.account_group_id = ?";
      sqlSalesReturnCondition += " AND scm_sales_return.account_group_id = ?";
      salesParams.add(filterParameters.getAccountGroup().getId());
      salesReturnParams.add(filterParameters.getAccountGroup().getId());
    }

    if (filterParameters.getSalesAgent() != null && filterParameters.getSalesAgent().getId() != null) {
      sqlSalesCondition += " AND scm_sales_invoice.company_sales_agent_person_profile_id = ?";
      sqlSalesReturnCondition += " AND scm_sales_return.sales_agent_id = ?";
      salesParams.add(filterParameters.getSalesAgent().getId());
      salesReturnParams.add(filterParameters.getSalesAgent().getId());
    }
    if (filterParameters.getAccount() != null && filterParameters.getAccount().getId() != null) {
      sqlSalesCondition += " AND scm_product_detail.account_id = ?";
      sqlSalesReturnCondition += " AND scm_product_detail.account_id = ?";
      salesParams.add(filterParameters.getAccount().getId());
      salesReturnParams.add(filterParameters.getAccount().getId());
    }
    if (filterParameters.getFromDate() != null) {
      sqlSalesCondition += " AND scm_sales_invoice.invoice_entry_date::date >= ?::date";
      sqlSalesReturnCondition += " AND scm_sales_return.entry_date::date >= ?::date";
      salesParams.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
      salesReturnParams.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    }
    if (filterParameters.getToDate() != null) {
      sqlSalesCondition += " AND scm_sales_invoice.invoice_entry_date::date <= ?::date";
      sqlSalesReturnCondition += " AND scm_sales_return.entry_date::date <= ?::date";
      salesParams.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
      salesReturnParams.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    }
    if (filterParameters.getFilterOption() == ReportConstant.COMPANY_OPTION) {
      sqlSalesCondition += " AND t2.name is NULL ";
      sqlSalesReturnCondition += " AND t2.name is NULL ";
    }
    if (ReportConstant.SALES.equals(filterParameters.getFilterType())) {
      sql = salesSql + sqlSalesCondition + sqlGroupBy + sqlOrderBy;
      params = salesParams;
    } else if (ReportConstant.SALES_RETURN.equals(filterParameters.getFilterType())) {
      sql = salesReturnSql + sqlSalesReturnCondition + sqlReturnGroupBy + sqlReturnOrderBy;
      params = salesReturnParams;
    } else if (ReportConstant.ALL.equals(filterParameters.getFilterType())) {
      sql = "SELECT * FROM (" + salesSql + sqlSalesCondition + sqlGroupBy + " UNION ALL "
              + salesReturnSql + sqlSalesReturnCondition + sqlReturnGroupBy + ") as tab ORDER BY tab.entry_date,tab.invoice_no ASC";
      salesParams.addAll(salesReturnParams);
      params = salesParams;
    }
    return AppDb.getList(main.dbConnector(), CompanyCustomerSales.class, sql, params.toArray());

  }



  public static List<CompanyCustomerSales> dailySalesAgentReport(Main main, Company company) {
    String sql = "";
///////////////////////////////////////////////////////////////////////////////////////////////////invoice_amount_net
    String salesByAgent = "SELECT t1.invoice_no,scm_account_group.group_name,t2.name as agentName,t4.territory_name as territory_name,\n"
            + "CASE WHEN TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd')=? THEN t1.invoice_amount_net ELSE 0 END as today_sales,\n"
            + "CASE WHEN TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd') >= ? and TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd')<=? THEN t1.invoice_amount_net ELSE 0 END as this_month_sales,\n"
            + "CASE WHEN TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd') >= ? and TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd')<=? THEN t1.invoice_amount_net ELSE 0 END as last_month_sales,\n"
            + "'1' as invoice_or_return\n"
            + "FROM\n"
            + "scm_sales_invoice t1\n"
            + "LEFT JOIN scm_sales_agent t2 ON t1.company_sales_agent_person_profile_id = t2.id \n"
            + "LEFT JOIN scm_sales_agent_territory t3 ON t2.id = t3.sales_agent_id\n"
            + "LEFT JOIN scm_territory t4 ON t3.territory_id = t4.id\n"
            + "LEFT JOIN scm_account_group ON scm_account_group.id = t1.account_group_id\n"
            + "LEFT JOIN scm_territory_district t5 ON  t5.territory_id =t3.territory_id\n"
            + "LEFT JOIN scm_customer ON t1.customer_id = scm_customer.id\n"
            + "LEFT JOIN scm_customer_address ON scm_customer.id=scm_customer_address.customer_id \n"
            + "WHERE t1.sales_invoice_status_id = 2 \n"
            + "AND t5.district_id = scm_customer_address.district_id\n"
            + "AND t1.company_id=? \n"
            + "GROUP BY t1.invoice_no,group_name,agentName,territory_name,TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd'),invoice_amount_net,invoice_or_return ";

    String salesByCompany = "SELECT t1.invoice_no,scm_account_group.group_name,t2.name as agentName,t4.territory_name as territory_name,	   \n"
            + "CASE WHEN TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd')=? THEN t1.invoice_amount_net ELSE 0 END as today_sales,\n"
            + "CASE WHEN TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd') >= ? and TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd')<=? THEN t1.invoice_amount_net ELSE 0 END as this_month_sales,\n"
            + "CASE WHEN TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd') >= ? and TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd')<=? THEN t1.invoice_amount_net ELSE 0 END as last_month_sales,\n"
            + "'1' as invoice_or_return\n"
            + "FROM\n"
            + "scm_sales_invoice t1\n"
            + "LEFT JOIN scm_sales_agent t2 ON t1.company_sales_agent_person_profile_id = t2.id \n"
            + "LEFT JOIN scm_sales_agent_territory t3 ON t2.id = t3.sales_agent_id\n"
            + "LEFT JOIN scm_territory t4 ON t3.territory_id = t4.id\n"
            + "LEFT JOIN scm_account_group ON scm_account_group.id = t1.account_group_id\n"
            + "WHERE t1.sales_invoice_status_id = 2 \n"
            + "AND t1.company_sales_agent_person_profile_id is null\n"
            + "AND t1.company_id=? \n"
            + "GROUP BY t1.invoice_no,group_name,agentName,territory_name,invoice_amount_net,invoice_or_return,TO_CHAR(t1.invoice_entry_date,'YYYY-MM-dd') ";

    String returnByAgent = "SELECT t1.invoice_no,scm_account_group.group_name,t2.name as agentName,t4.territory_name as territory_name,	   \n"
            + "CASE WHEN TO_CHAR(t1.entry_date,'YYYY-MM-dd')=? THEN t1.invoice_amount_net ELSE 0 END as today_sales,\n"
            + "CASE WHEN TO_CHAR(t1.entry_date,'YYYY-MM-dd') >= ? and TO_CHAR(t1.entry_date,'YYYY-MM-dd')<=? THEN t1.invoice_amount_net ELSE 0 END as this_month_sales,\n"
            + "CASE WHEN TO_CHAR(t1.entry_date,'YYYY-MM-dd') >= ? and TO_CHAR(t1.entry_date,'YYYY-MM-dd')<=? THEN t1.invoice_amount_net ELSE 0 END as last_month_sales,\n"
            + "'2' as invoice_or_return\n"
            + "FROM\n"
            + "scm_sales_return t1\n"
            + "LEFT JOIN scm_sales_agent t2 ON t1.sales_agent_id = t2.id \n"
            + "LEFT JOIN scm_sales_agent_territory t3 ON t2.id = t3.sales_agent_id\n"
            + "LEFT JOIN scm_territory t4 ON t3.territory_id = t4.id\n"
            + "LEFT JOIN scm_account_group ON scm_account_group.id = t1.account_group_id\n"
            + "LEFT JOIN scm_territory_district t5 ON  t5.territory_id =t3.territory_id,\n"
            + "scm_customer,scm_customer_address\n"
            + "WHERE t1.sales_return_status_id = 2 \n"
            + "AND t1.customer_id = scm_customer.id AND scm_customer.id=scm_customer_address.customer_id\n"
            + "AND t5.district_id = scm_customer_address.district_id\n"
            + "AND t1.company_id=?\n"
            + "GROUP BY t1.invoice_no,group_name,agentName,territory_name,entry_date,invoice_amount_net,invoice_or_return ";

    String returnByCompany = "SELECT t1.invoice_no,scm_account_group.group_name,t2.name as agentName,t4.territory_name as territory_name,\n"
            + "CASE WHEN TO_CHAR(t1.entry_date,'YYYY-MM-dd')=? THEN t1.invoice_amount_net ELSE 0 END as today_sales,\n"
            + "CASE WHEN TO_CHAR(t1.entry_date,'YYYY-MM-dd') >= ? and TO_CHAR(t1.entry_date,'YYYY-MM-dd')<=? THEN t1.invoice_amount_net ELSE 0 END as this_month_sales,\n"
            + "CASE WHEN TO_CHAR(t1.entry_date,'YYYY-MM-dd') >= ? and TO_CHAR(t1.entry_date,'YYYY-MM-dd')<=? THEN t1.invoice_amount_net ELSE 0 END as last_month_sales,\n"
            + "'2' as invoice_or_return\n"
            + "FROM\n"
            + "scm_sales_return t1\n"
            + "LEFT JOIN scm_sales_agent t2 ON t1.sales_agent_id = t2.id \n"
            + "LEFT JOIN scm_sales_agent_territory t3 ON t2.id = t3.sales_agent_id\n"
            + "LEFT JOIN scm_territory t4 ON t3.territory_id = t4.id\n"
            + "LEFT JOIN scm_account_group ON scm_account_group.id = t1.account_group_id\n"
            + "WHERE t1.sales_return_status_id = 2 \n"
            + "AND t1.sales_agent_id is null\n"
            + "AND t1.company_id=? \n"
            + "GROUP BY t1.invoice_no,group_name,agentName,territory_name,entry_date,invoice_amount_net,invoice_or_return";

    sql = "SELECT tab2.group_name,tab2.agentName,tab2.territory_name,SUM(tab2.today_sales) as today_sales,SUM(tab2.today_return)as today_return,SUM(tab2.this_month_sales)as this_month_sales,\n"
            + "SUM(tab2.this_month_return) as this_month_return,SUM(tab2.last_month_sales) as last_month_sales,SUM(tab2.last_month_return) as last_month_return FROM\n"
            + "(SELECT tab.group_name,tab.agentName,tab.territory_name,CASE WHEN invoice_or_return='1' THEN SUM(tab.today_sales) ELSE 0 END as today_sales,\n"
            + "CASE WHEN invoice_or_return='2' THEN SUM(tab.today_sales) ELSE 0 END as today_return,\n"
            + "CASE WHEN invoice_or_return = '1' THEN SUM(tab.this_month_sales) ELSE 0 END as this_month_sales,\n"
            + "CASE WHEN invoice_or_return = '2' THEN SUM(tab.this_month_sales) ELSE 0 END as this_month_return,\n"
            + "CASE WHEN invoice_or_return = '1' THEN SUM(tab.last_month_sales) ELSE 0 END as last_month_sales,\n"
            + "CASE WHEN invoice_or_return = '2' THEN SUM(tab.last_month_sales) ELSE 0 END as last_month_return\n"
            + "FROM(\n"
            + salesByAgent + "UNION\n" + salesByCompany + "UNION\n" + returnByAgent + "UNION\n" + returnByCompany
            + ") tab\n"
            + "GROUP BY group_name,agentName,territory_name,invoice_or_return) tab2 \n"
            + "-- WHERE (today_sales>0 or today_return>0 or this_month_sales>0 or this_month_return>0 or last_month_return >0 or last_month_sales>0)\n"
            + "GROUP BY group_name,agentName,territory_name\n"
            + "ORDER BY group_name,territory_name,agentName";
    Calendar c = Calendar.getInstance();   // this takes current date
    c.set(Calendar.DAY_OF_MONTH, 1);
    Date thisMonthFirst = c.getTime();
    c.set(Calendar.DAY_OF_MONTH, 0);
    Date lastMonthLast = c.getTime();
    c.set(Calendar.DAY_OF_MONTH, 1);
    Date lastMonthFirst = c.getTime();
    ArrayList<Object> params = new ArrayList<>();
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(thisMonthFirst));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(lastMonthFirst));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(lastMonthLast));
    params.add(company.getId());
    params.addAll(params);
    params.addAll(params);
    return AppDb.getList(main.dbConnector(), CompanyCustomerSales.class, sql, params.toArray());
  }

  public static List<CompanyCustomerSales> dailyReport(Main main, Company company, FilterParameters filterParameters) {
    String sql = "";
    String sqlGroupBy = "";
    String sqlReturn = "";
    String sqlReturnGroupBy = "";
    ArrayList<Object> params = new ArrayList<>();
    sql = "select inv.id,cust.customer_name,inv.invoice_no,prod.product_name,SUM(s_item.product_qty) as qty,SUM(s_item.product_qty_free) as  qty_free,\n"
            + "SUM(s_item.value_taxable) as sales_value,"
            + "SUM(s_item.value_taxable+COALESCE(s_item.value_igst,0)+COALESCE(s_item.service_freight_value,0)+"
            + "COALESCE(s_item.service2_value,0)+COALESCE(s_item.tcs_value,0)+COALESCE(kerala_flood_cess_value,0)) as net_amount,"
            + "'1' as invoice_or_return\n"
            + "from scm_sales_invoice inv,scm_sales_invoice_item s_item,scm_product_detail prod_det,scm_product_batch batch,scm_product prod,scm_customer cust\n"
            + "where inv.id=s_item.sales_invoice_id and s_item.product_detail_id=prod_det.id and prod_det.product_batch_id=batch.id\n"
            + "and batch.product_id=prod.id and cust.id=inv.customer_id and inv.invoice_entry_date::date = ?::date and inv.company_id=? \n";
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    if (filterParameters.getSalesAgent() != null) {
      sql += " and inv.company_sales_agent_person_profile_id=? ";
      params.add(filterParameters.getSalesAgent().getId());
    } else {
      sql += " and inv.company_sales_agent_person_profile_id is null ";
    }
    if (filterParameters.getAccountGroup() != null) {
      sql += " and inv.account_group_id=? ";
      params.add(filterParameters.getAccountGroup().getId());
    }
    sqlGroupBy = "group by inv.id,cust.customer_name,inv.invoice_no,prod.product_name ";
    if (filterParameters.isIncludeReturn()) {
      sqlReturn = "select ret.id,cust.customer_name,ret.invoice_no,prod.product_name,-SUM(ret_item.product_quantity) as qty,-SUM(ret_item.product_quantity_free) as  qty_free,\n"
              + "-SUM(ret_item.value_assessable) as sales_value,-SUM(ret_item.value_assessable+ret_item.value_igst) as net_amount,'2' as invoice_or_return\n"
              + "from scm_sales_return ret,scm_sales_return_item ret_item,scm_product_detail prod_det,scm_product_batch batch,scm_product prod,scm_customer cust\n"
              + "where ret.id=ret_item.sales_return_id and ret_item.product_detail_id=prod_det.id and prod_det.product_batch_id=batch.id\n"
              + "and batch.product_id=prod.id and cust.id=ret.customer_id and ret.entry_date::date = ?::date and ret.company_id=? ";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
      params.add(company.getId());
      if (filterParameters.getSalesAgent() != null) {
        sqlReturn += " and ret.sales_agent_id=? ";
        params.add(filterParameters.getSalesAgent().getId());
      } else {
        sqlReturn += " and ret.sales_agent_id is null ";
      }
      if (filterParameters.getAccountGroup() != null) {
        sql += " and ret.account_group_id=? ";
        params.add(filterParameters.getAccountGroup().getId());
      }
      sqlReturnGroupBy = " GROUP BY ret.id,cust.customer_name,ret.invoice_no,prod.product_name";
    }
    sql = sql + sqlGroupBy + (filterParameters.isIncludeReturn() ? (" UNION ALL " + sqlReturn + sqlReturnGroupBy) : "") + " ORDER BY customer_name,invoice_no,product_name ";
    return AppDb.getList(main.dbConnector(), CompanyCustomerSales.class, sql, params.toArray());
  }
}
