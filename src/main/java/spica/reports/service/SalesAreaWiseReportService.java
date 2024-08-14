/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.List;
import spica.reports.model.FilterParameters;
import spica.reports.model.SalesAreaWiseReport;
import spica.scm.domain.Company;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author sujesh
 */
public abstract class SalesAreaWiseReportService {

  public static List<SalesAreaWiseReport> selectAreaWiseReportByCompanyAndGroup(Main main, Company company, FilterParameters filterParameters, boolean salesReturn, boolean damageExpiry) {
    List<SalesAreaWiseReport> reportList = null;
    main.clear();
    String sql = "", return_sql = "", return_group = "", return_cond = "";
    String sales_sql = "(select t1.id,t6.product_name,t6.pack_size,t7.title as unit ,t2.product_qty as quantity, t2.product_qty_free as free_quantity,"
            + "SUM(t2.value_goods) as value_goods,t2.value_prod_piece_selling as rate,\n"
            + "t1.invoice_no as invoice_number,t9.pin as pincode,t1.invoice_date,t2.value_mrp as mrp,SUM(t2.value_taxable) as deducted_value,t8.gst_no,t2.hsn_code, "
            + "SUM(value_taxable+t2.value_igst+COALESCE(service_freight_value,0)+COALESCE(service2_value,0)+COALESCE(kerala_flood_cess_value,0)+COALESCE(tcs_value,0)) as creditSales,\n"
            + "t10.district_name,t10.id as district_id,t11.id as account_group_id,t11.group_name as account_group_name,t8.customer_name,'SI' as invoice_type,t8.id as customer_id ,0 as sales_return_type\n"
            + "from scm_sales_invoice t1\n"
            + "left join scm_sales_invoice_item t2 on t2.sales_invoice_id = t1.id\n"
            + "inner join scm_product_entry_detail t3 on t3.id = t2.product_entry_detail_id\n"
            + "inner join scm_product_detail t4 on t4.id= t3.product_detail_id\n"
            + "inner join scm_product_preset t5 on t5.id = t4.product_preset_id\n"
            + "inner join scm_product t6 on t6.id = t5.product_id\n"
            + "inner join scm_product_unit t7 on t7.id = t6.product_unit_id\n"
            + "inner join scm_customer t8 on t8.id = t1.customer_id\n"
            + "inner join scm_customer_address t9 on t9.customer_id = t8.id\n"
            + "inner join scm_district t10 on t10.id = t9.district_id\n"
            + "inner join scm_account_group t11 on t11.id = t1.account_group_id\n"
            + "where t1.company_id = ?   AND t1.invoice_date::date >=?::date and t1.invoice_date::date <= ?::date  and t1.sales_invoice_status_id = 2  ";
    String sales_group = " group by t1.id,t6.product_name,t6.pack_size,t7.title ,t2.product_qty , t2.product_qty_free ,t2.value_prod_piece_selling ,\n"
            + "t1.invoice_no ,t9.pin ,t1.invoice_date,t2.value_mrp ,t8.gst_no,t2.hsn_code,t1.account_group_id,t1.id,t8.id,\n"
            + "t10.district_name,t10.id ,t11.id ,t11.group_name ,t8.customer_name  order by t1.account_group_id , t10.id,t8.id desc) ";
    String sales_cond = "";
    main.param(company.getId());
    main.param(filterParameters.getFromDate());
    main.param(filterParameters.getToDate());
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      sales_cond = " AND t1.account_group_id = ? ";
      main.param(filterParameters.getAccountGroup().getId());
    }

    if (salesReturn) {
      return_sql = " UNION (select t1.id,t6.product_name,t6.pack_size,t7.title as unit ,-COALESCE(t2.product_quantity,t2.product_quantity_damaged) as quantity, 0 as free_quantity,"
              + "-SUM(t2.value_goods) as value_goods,t2.value_rate as rate,\n"
              + "t1.invoice_no as invoice_number,t9.pin as pincode,t1.invoice_date,t2.value_mrp as mrp,-SUM(t2.value_assessable) as deducted_value,t8.gst_no,t2.hsn_code,"
              + " -SUM(t2.value_igst+t2.value_assessable) as creditSales,\n"
              + "t10.district_name,t10.id as district_id,t11.id as account_group_id,t11.group_name as account_group_name,t8.customer_name,'SR' as invoice_type,t8.id as customer_id ,t1.sales_return_type\n"
              + "from scm_sales_return t1\n"
              + "left join scm_sales_return_item t2 on t2.sales_return_id = t1.id\n"
              + "inner join scm_product_entry_detail t3 on t3.id = t2.product_entry_detail_id\n"
              + "inner join scm_product_detail t4 on t4.id= t3.product_detail_id\n"
              + "inner join scm_product_preset t5 on t5.id = t4.product_preset_id\n"
              + "inner join scm_product t6 on t6.id = t5.product_id\n"
              + "inner join scm_product_unit t7 on t7.id = t6.product_unit_id\n"
              + "inner join scm_customer t8 on t8.id = t1.customer_id\n"
              + "inner join scm_customer_address t9 on t9.customer_id = t8.id\n"
              + "inner join scm_district t10 on t10.id = t9.district_id\n"
              + "inner join scm_account_group t11 on t11.id = t1.account_group_id\n"
              + "where t1.company_id = ?   AND t1.invoice_date::date >=?::date and t1.invoice_date::date <=?::date  and t1.sales_return_status_id = 2  \n";
      return_group = " group by t1.id,t6.product_name,t6.pack_size,t7.title ,t2.product_quantity,t2.product_quantity_damaged ,t2.value_rate ,\n"
              + "t1.invoice_no ,t9.pin ,t1.invoice_date,t2.value_mrp ,t2.value_assessable ,t8.gst_no,t2.hsn_code, t2.value_igst,t1.account_group_id,t1.id,t8.id,\n"
              + "t10.district_name,t10.id ,t11.id ,t11.group_name ,t8.customer_name,t1.sales_return_type  \n"
              + "order by t1.account_group_id , t10.id,t8.id desc)";
      main.param(company.getId());
      main.param(filterParameters.getFromDate());
      main.param(filterParameters.getToDate());
      if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
        return_cond = " AND t1.account_group_id = ? ";
        main.param(filterParameters.getAccountGroup().getId());
      }
      if (!damageExpiry) {
        return_cond += " AND t1.sales_return_type = 1 ";
      }
    }

    if (!salesReturn) {
      sql = sales_sql + sales_cond + sales_group;
    } else {
      String bg = "select * from (\n";
      String end = "\n) tab  order by account_group_id,district_id,customer_id desc\n";
      sql = bg + sales_sql + sales_cond + sales_group + return_sql + return_cond + return_group + end;
    }

    return AppDb.getList(main.dbConnector(), SalesAreaWiseReport.class, sql, main.getParamData().toArray());
  }

}
