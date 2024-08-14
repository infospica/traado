/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.service;

import java.util.ArrayList;
import spica.reports.model.CompanyCustomerSales;
import spica.scm.domain.Company;
import spica.reports.model.FilterParameters;
import wawo.app.Main;
import wawo.entity.core.AppDb;
import java.util.List;
import spica.scm.domain.Brand;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.common.AppService;
import wawo.app.faces.AppLookup;

/**
 *
 * @author krishna.vm
 */
public class CompanyCustomerSalesService {

  public static List<CompanyCustomerSales> getCompanyCustomerSalesList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "";
    String sqlCondition = " AND scm_product.company_id =?";
    String sqlReturn = "";
    String sqlReturnCondition = "";
    String sqlReturnSplit = "";
    String sqlGroupBy = "";
    String sqlReturnGroupBy = "";
    String sqlOrderBy = "";
    if (!filterParameters.getReportType().equals("1") && !filterParameters.getReportType().equals("2")) {
      filterParameters.setIncludeReturn(false);
    }
    sql = "SELECT scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
            + "	    T_Dist.district_name as district_name,T_Terr.territory_name as territory_name, scm_sales_invoice.customer_pin as pin_code,\n"
            + "	    scm_sales_invoice.id,scm_sales_invoice.invoice_no as invoice_no,scm_sales_invoice.invoice_date as invoice_date,\n"
            + "	    T_Rep.name as rep_name,T_Agent.name as agent_name,scm_manufacture.code as mfr_code,\n"
            + "	    scm_product.product_name as product_name,scm_sales_invoice_item.hsn_code as hsn_code, \n"
            + "	    scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,            \n"
            + "     CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END as qty,\n"
            + "     CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN 0 ELSE COALESCE (scm_sales_invoice_item.product_qty_free,0) END as qty_free, \n"
            + "	    scm_sales_invoice_item.value_mrp as value_mrp,\n"
            + "	    scm_sales_invoice_item.value_prod_piece_selling as value_pts,scm_sales_invoice_item.value_ptr as value_ptr,\n"
            + "	    scm_sales_invoice_item.scheme_discount_value as scheme_discount,scm_sales_invoice_item.product_discount_value as product_discount,\n"
            + "	    COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0)) as invoice_discount, \n"
            + "     scm_sales_invoice_item.value_goods as goods_value,\n"
            + "     COALESCE(scm_sales_invoice_item.kerala_flood_cess_value,0) as state_cess, \n"
            + "	    CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone=1 THEN scm_sales_invoice_item.value_igst ELSE 0 END as igst,\n"
            + "	    CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone=1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END as cgst,\n"
            + "	    CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone=1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END as sgst,\n"
            + "     scm_sales_invoice_item.value_igst as gst_amount,\n"
            + "     scm_sales_invoice_item.value_taxable+scm_sales_invoice_item.value_igst as net_amount,            \n"
            + "     COALESCE(scm_sales_invoice_item.cash_discount_value,0) as cash_discount, \n"
            + "     COALESCE(scm_sales_invoice_item.value_taxable,0) as sales_value \n"
            + "     FROM \n"
            + "     scm_sales_invoice \n"
            + "	    LEFT JOIN scm_user_profile T_Rep ON vendor_sales_agent_person_profile_id = T_Rep.id\n"
            + "	    LEFT JOIN scm_user_profile T_Agent ON company_sales_agent_person_profile_id = T_Agent.id\n"
            + "	    LEFT JOIN scm_district T_Dist ON customer_district_id = T_Dist.id\n"
            + "	    LEFT JOIN scm_territory T_Terr ON customer_territory_id = T_Terr.id\n"
            + "	   ,scm_sales_invoice_item,scm_customer,scm_account,scm_product_detail,scm_product,scm_product_category,\n"
            + "     scm_product_batch LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id\n"
            + "     WHERE scm_sales_invoice.sales_invoice_status_id >= ? AND\n"
            + "     scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
            + "     AND scm_sales_invoice.customer_id = scm_customer.id\n"
            + "     AND scm_account.id = scm_product_detail.account_id\n"
            + "     AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
            + "     AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
            + "     AND scm_product_batch.product_id = scm_product.id\n"
            + "     AND scm_product.product_category_id = scm_product_category.id ";

    if ("1".equals(filterParameters.getReportType())) {  //INVOICE_WISE = 1;
      sql = "SELECT scm_sales_invoice.invoice_no as invoice_no,scm_sales_invoice.invoice_date as invoice_date, \n"
              + "scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
              + "T_Dist.district_name as district_name,T_Terr.territory_name as territory_name, scm_sales_invoice.customer_pin as pin_code,scm_sales_invoice.id,	    \n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_goods,0)) as goods_value,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.scheme_discount_value,0)) as scheme_discount,SUM(COALESCE(scm_sales_invoice_item.product_discount_value,0)) as product_discount,\n"
              + "SUM((COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0)))) as invoice_discount,             \n"
              + "COALESCE(scm_sales_invoice.kerala_flood_cess_net_value,0) as state_cess, \n"
              + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN SUM(COALESCE(scm_sales_invoice_item.value_igst,0)) ELSE 0 END,0) as igst,\n"
              + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE SUM(COALESCE(scm_sales_invoice_item.value_cgst,0))END,0) as cgst,\n"
              + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE SUM(COALESCE(scm_sales_invoice_item.value_sgst,0)) END,0) as sgst,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_igst,0)) as gst_amount,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)+COALESCE(scm_sales_invoice_item.value_igst,0)+\n"
              + "(CASE WHEN scm_sales_invoice.cash_discount_taxable is null OR scm_sales_invoice.cash_discount_taxable = 0 THEN -COALESCE(scm_sales_invoice_item.cash_discount_value,0) ELSE 0 END)) as net_amount, \n"
              + "SUM(COALESCE(scm_sales_invoice_item.cash_discount_value,0)) as cash_discount, \n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)) as sales_value,1 as sales_or_return,'SL' as invoice_type  \n"
              + "FROM \n"
              + "scm_sales_invoice \n"
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

      sqlGroupBy += " GROUP BY scm_customer.customer_name ,scm_customer.gst_no ,scm_sales_invoice.id,scm_sales_invoice.sez_zone,\n"
              + "T_Dist.district_name ,T_Terr.territory_name , scm_sales_invoice.customer_pin ,\n"
              + "scm_sales_invoice.invoice_no ,scm_sales_invoice.invoice_date ,scm_sales_invoice.is_sales_interstate \n";
      sqlOrderBy += "  ORDER BY invoice_date,invoice_no \n";

      sqlReturn = "select ret.invoice_no,ret.entry_date as invoice_date,cust.customer_name,cust.gst_no,dist.district_name,ter.territory_name,c_add.pin as pin_code,ret.id,\n"
              + "-SUM(COALESCE(ret_item.value_goods,0)) as goods_value,-SUM(COALESCE(ret_item.scheme_discount_value,0)) as scheme_discount,\n"
              + "-SUM(COALESCE(ret_item.product_discount_value,0)) as product_discount,-SUM(COALESCE(ret_item.invoice_discount_value,0)) as invoice_discount,\n"
              + "null as state_cess,COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone = 0 THEN 0 ELSE -SUM(COALESCE(ret_item.value_igst,0)) END,0) as igst,\n"
              + "COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone = 0 THEN -SUM(COALESCE(ret_item.value_cgst,0)) ELSE 0 END,0) as cgst,\n"
              + "COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone = 0 THEN -SUM(COALESCE(ret_item.value_sgst,0)) ELSE 0 END,0) as sgst,\n"
              + "-SUM(COALESCE(ret_item.value_igst,0)) as gst_amount,\n"
              + "-SUM(ret_item.value_assessable+COALESCE(ret_item.value_igst,0)) as net_amount ,\n"
              + "-SUM(COALESCE(ret_item.cash_discount_value,0)) as cash_discount,-SUM(COALESCE(ret_item.value_assessable,0)) as sales_value,2 as sales_or_return,\n"
              + "CASE WHEN ret.sales_return_type = 1 THEN 'SR-S' ELSE 'SR-D' END as invoice_type\n"
              + "from scm_sales_return ret\n"
              + "LEFT JOIN scm_territory ter on ter.id=ret.territory_id,scm_sales_return_item ret_item,\n"
              + "scm_customer cust,scm_customer_address c_add,scm_district dist,scm_company_address comp_add\n"
              + "where ret_item.sales_return_id=ret.id and ret.sales_return_status_id=2\n"
              + "and ret.customer_id=cust.id  and c_add.id=ret.customer_address_id and c_add.district_id=dist.id\n"
              + "and comp_add.company_id=ret.company_id\n";

      sqlReturnGroupBy = "GROUP BY ret.invoice_no,ret.entry_date,cust.customer_name,ret.sez_zone,cust.taxable,cust.gst_no,dist.district_name,ter.territory_name,c_add.pin,ret.id,comp_add.state_id,\n"
              + "c_add.state_id\n";

    } else if ("2".equals(filterParameters.getReportType())) {  //INVOICE_PRODUCT_WISE = 2;
      sql = "SELECT scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
              + "scm_sales_invoice.id,scm_sales_invoice.invoice_no as invoice_no,scm_sales_invoice.invoice_date as invoice_date,\n"
              + "scm_manufacture.code as mfr_code,T_Dist.district_name as district_name,\n"
              + "Tab_prod_class.p_class as product_classification,\n"
              + "scm_product.product_name as product_name,scm_sales_invoice_item.hsn_code as hsn_code,getPackDimension(scm_product.id) as pack_type, \n"
              + "scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.product_qty,0)) as qty,\n"
              + " SUM(COALESCE(scm_sales_invoice_item.product_qty_free,0)) as qty_free, \n"
              + "scm_sales_invoice_item.value_mrp as value_mrp,scm_sales_invoice_item.prod_piece_selling_forced as rate,\n"
              + "scm_sales_invoice_item.landing_rate, scm_product_entry_detail.value_rate as purchase_value,\n"
              + "scm_sales_invoice_item.value_prod_piece_selling as value_pts,scm_sales_invoice_item.value_ptr as value_ptr,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.scheme_discount_value,0)) as scheme_discount,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.product_discount_value,0)) as product_discount,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0))) as invoice_discount, \n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_goods,0)) as goods_value,scm_tax_code.rate_percentage as tax_perc,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.kerala_flood_cess_value,0)) as state_cess, \n"
              + "SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN scm_sales_invoice_item.value_igst ELSE 0 END) as igst,\n"
              + "SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END) as cgst,\n"
              + "SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END) as sgst,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_igst,0)) as gst_amount,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)+COALESCE(scm_sales_invoice_item.value_igst,0)+\n"
              + "CASE WHEN scm_sales_invoice.cash_discount_taxable is null OR scm_sales_invoice.cash_discount_taxable = 0 \n"
              + "THEN -COALESCE(scm_sales_invoice_item.cash_discount_value,0) ELSE 0 END) as net_amount,"
              + "SUM(COALESCE(scm_sales_invoice_item.cash_discount_value,0)) as cash_discount, \n"
              + " SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)) as sales_value,1 as sales_or_return,'SL' as invoice_type \n"
              + " FROM \n"
              + " scm_sales_invoice \n"
              + "LEFT JOIN scm_district T_Dist ON customer_district_id = T_Dist.id\n"
              + ",scm_sales_invoice_item,scm_tax_code,scm_customer,scm_account,scm_product_detail,scm_product \n"
              + "LEFT OUTER JOIN \n"
              + "(SELECT scm_product_classification_detail.product_id as p_id,string_agg(scm_product_classification.title,',') as p_class FROM scm_product_classification,scm_product_classification_detail\n"
              + "WHERE scm_product_classification.id =scm_product_classification_detail.classification_id\n"
              + "GROUP BY scm_product_classification_detail.product_id) as Tab_prod_class ON scm_product.id =Tab_prod_class.p_id\n"
              + ",scm_product_category,scm_product_entry_detail,\n"
              + " scm_product_batch LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id\n"
              + " WHERE scm_sales_invoice.sales_invoice_status_id >= ? \n"
              + "AND scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + " AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + " AND scm_account.id = scm_product_detail.account_id\n"
              + " AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + " AND scm_sales_invoice_item.tax_code_id =scm_tax_code.id\n"
              + " AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + " AND scm_product_batch.product_id = scm_product.id\n"
              + " AND scm_product_entry_detail.id=scm_sales_invoice_item.product_entry_detail_id\n"
              + " AND scm_product.product_category_id = scm_product_category.id\n";
      sqlGroupBy = " group by scm_customer.customer_name ,scm_customer.gst_no ,scm_sales_invoice.id,scm_sales_invoice.invoice_no ,scm_sales_invoice.invoice_date,\n"
              + "scm_manufacture.code,scm_product.product_name,scm_sales_invoice_item.hsn_code,pack_type,scm_product_batch.batch_no,scm_product_batch.expiry_date_actual,\n"
              + "scm_sales_invoice_item.value_mrp,scm_sales_invoice_item.prod_piece_selling_forced,scm_sales_invoice_item.value_prod_piece_selling,\n"
              + "scm_sales_invoice_item.value_ptr,scm_tax_code.rate_percentage,tab_prod_class.p_class,scm_sales_invoice_item.landing_rate, scm_product_entry_detail.value_rate,\n"
              + "T_Dist.district_name ";

      sqlReturn = " select cust.customer_name,cust.gst_no,ret.id,ret.invoice_no,ret.entry_date as invoice_date,man.code as mfr_code,dist.district_name,\n"
              + "Tab_prod_class.p_class as product_classification,\n"
              + "prod.product_name,\n"
              + "ret_item.hsn_code,getPackDimension(prod.id) as pack_type,batch.batch_no,batch.expiry_date_actual as expiry_date,\n"
              + "SUM(CASE WHEN ret.sales_return_type = 1 THEN -ret_item.product_quantity else -ret_item.product_quantity_damaged END )as qty,\n"
              + "0 as qty_free,ret_item.value_mrp,ret_item.value_rate as rate,\n"
              + "CASE WHEN ret_item.sales_invoice_id is null THEN ret_item.purchase_rate ELSE scm_sales_invoice_item.landing_rate END as landing_rate,\n"
              + "scm_product_entry_detail.value_rate as purchase_value,\n"
              + " ret_item.value_pts,ret_item.value_ptr,\n"
              + "-SUM(COALESCE(ret_item.scheme_discount_value,0)) as scheme_discount,-SUM(COALESCE(ret_item.product_discount_value,0)) as product_discount,\n"
              + "-SUM(COALESCE(ret_item.invoice_discount_value,0)) as invoice_discount_value,-SUM(COALESCE(ret_item.value_goods,0)) as goods_value,\n"
              + "scm_tax_code.rate_percentage as tax_perc,\n"
              + "null as state_cess,SUM(COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone = 0 THEN 0 ELSE -ret_item.value_igst END,0)) as igst,\n"
              + "SUM(COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone = 0 THEN -ret_item.value_cgst ELSE 0 END,0)) as cgst, \n"
              + "SUM(COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone = 0 THEN -ret_item.value_sgst ELSE 0 END,0)) as sgst,\n"
              + "-SUM(COALESCE(ret_item.value_igst,0)) as gst_amount,-SUM(ret_item.value_assessable+COALESCE(ret_item.value_igst,0)) as net_amount,\n"
              + "-SUM(COALESCE(ret_item.cash_discount_value,0)) as cash_discount,-SUM(COALESCE(ret_item.value_assessable,0)) as sales_value,\n"
              + "2 as sales_or_return,CASE WHEN ret.sales_return_type = 1 THEN 'SR-S' ELSE 'SR-D' END as invoice_type\n"
              + "from scm_sales_return ret,scm_sales_return_item ret_item \n"
              + "LEFT JOIN scm_sales_invoice_item ON ret_item.sales_invoice_item_id = scm_sales_invoice_item.id \n"
              + ",scm_tax_code,scm_customer cust,scm_company_address comp_add,scm_customer_address c_add,scm_district dist,\n"
              + "scm_product_detail prod_det,scm_product prod \n"
              + "LEFT OUTER JOIN \n"
              + "(SELECT scm_product_classification_detail.product_id as p_id,string_agg(scm_product_classification.title,',') as p_class FROM scm_product_classification,scm_product_classification_detail\n"
              + "WHERE scm_product_classification.id =scm_product_classification_detail.classification_id\n"
              + "GROUP BY scm_product_classification_detail.product_id) as Tab_prod_class ON prod.id =Tab_prod_class.p_id\n"
              + ",scm_product_entry_detail,scm_product_batch batch\n"
              + "LEFT JOIN scm_manufacture man on man.id=batch.manufacture_id\n"
              + "where ret.sales_return_status_id = 2 and ret.id=ret_item.sales_return_id and ret.customer_id=cust.id  \n"
              + "and prod_det.id=ret_item.product_detail_id and batch.id=prod_det.product_batch_id and prod.id=batch.product_id \n"
              + "and ret_item.tax_code_id = scm_tax_code.id\n"
              + "and comp_add.company_id =ret.company_id and ret.customer_address_id=c_add.id  \n"
              + "and c_add.district_id=dist.id \n"
              + "and scm_product_entry_detail.id=ret_item.product_entry_detail_id\n";
      sqlReturnGroupBy = "group by cust.customer_name ,cust.gst_no ,ret.id,ret.invoice_no ,ret.invoice_date,man.code,prod.product_name,ret_item.hsn_code,pack_type,batch.batch_no,\n"
              + "batch.expiry_date_actual,ret_item.value_mrp,ret_item.value_rate,ret_item.value_pts,ret_item.value_ptr,scm_tax_code.rate_percentage,tab_prod_class.p_class,dist.district_name,"
              + "scm_sales_invoice_item.landing_rate,ret_item.sales_invoice_id,ret_item.purchase_rate, scm_product_entry_detail.value_rate ";
      sqlOrderBy = " ORDER BY invoice_date,invoice_no ";

    } else if ("3".equals(filterParameters.getReportType())) {  //CUSTOMER_WISE = 3;
      sql = "SELECT scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
              + "T_Dist.district_name as district_name,T_Terr.territory_name as territory_name, scm_sales_invoice.customer_pin as pin_code,	    \n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_goods,0)) as goods_value,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.scheme_discount_value,0))  as scheme_discount,SUM(COALESCE(scm_sales_invoice_item.product_discount_value,0)) as product_discount,\n"
              + "SUM((COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0)))) as invoice_discount,             \n"
              + "SUM(COALESCE(scm_sales_invoice_item.kerala_flood_cess_value,0)) as state_cess, \n"
              + "SUM(COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN scm_sales_invoice_item.value_igst ELSE 0 END,0)) as igst,\n"
              + "SUM(COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END,0)) as cgst,\n"
              + "SUM(COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END,0)) as sgst,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_igst,0))  as gst_amount,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)+COALESCE(scm_sales_invoice_item.value_igst,0)+\n"
              + "CASE WHEN scm_sales_invoice.cash_discount_taxable is null OR scm_sales_invoice.cash_discount_taxable = 0 \n"
              + "THEN -COALESCE(scm_sales_invoice_item.cash_discount_value,0) ELSE 0 END) as net_amount,"
              + "SUM(COALESCE(scm_sales_invoice_item.cash_discount_value,0)) as cash_discount, \n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)) as sales_value \n"
              + "FROM \n"
              + "scm_sales_invoice \n"
              + "LEFT JOIN scm_district T_Dist ON customer_district_id = T_Dist.id\n"
              + "LEFT JOIN scm_territory T_Terr ON customer_territory_id = T_Terr.id\n"
              + ",scm_sales_invoice_item,scm_customer,scm_account,scm_product_detail,scm_product,scm_product_batch,scm_product_category \n"
              + " WHERE scm_sales_invoice.sales_invoice_status_id >= ? AND\n"
              + "scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + "   AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + "   AND scm_account.id = scm_product_detail.account_id\n"
              + "   AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + "   AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "   AND scm_product_batch.product_id = scm_product.id\n"
              + "   AND scm_product.product_category_id = scm_product_category.id     ";

      sqlGroupBy = " GROUP BY scm_customer.customer_name ,scm_customer.gst_no ,\n"
              + "T_Dist.district_name ,T_Terr.territory_name , scm_sales_invoice.customer_pin ,\n"
              + "scm_sales_invoice.is_sales_interstate \n"
              + " ORDER BY UPPER(scm_customer.customer_name) ";

    } else if ("4".equals(filterParameters.getReportType())) {  //CUSTOMER_INVOICE_WISE = 4;
      sql = "SELECT scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
              + "T_Dist.district_name as district_name,T_Terr.territory_name as territory_name, scm_sales_invoice.customer_pin as pin_code,	    \n"
              + "scm_sales_invoice.id,scm_sales_invoice.invoice_no as invoice_no,scm_sales_invoice.invoice_date as invoice_date, \n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_goods,0)) as goods_value,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.scheme_discount_value,0)) as scheme_discount,SUM(COALESCE(scm_sales_invoice_item.product_discount_value,0)) as product_discount,\n"
              + "SUM((COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0)))) as invoice_discount,             \n"
              + "SUM(COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN scm_sales_invoice_item.value_igst ELSE 0 END,0)) as igst,\n"
              + "SUM(COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END,0)) as cgst,\n"
              + "SUM(COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END,0)) as sgst,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_igst,0))  as gst_amount,\n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)+COALESCE(scm_sales_invoice_item.value_igst,0)+\n"
              + "CASE WHEN scm_sales_invoice.cash_discount_taxable is null OR scm_sales_invoice.cash_discount_taxable = 0 \n"
              + "THEN -COALESCE(scm_sales_invoice_item.cash_discount_value,0) ELSE 0 END) as net_amount,"
              + "SUM(COALESCE(scm_sales_invoice_item.cash_discount_value,0)) as cash_discount, \n"
              + "SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)) as sales_value \n"
              + "FROM \n"
              + "scm_sales_invoice \n"
              + "LEFT JOIN scm_user_profile T_Rep ON vendor_sales_agent_person_profile_id = T_Rep.id\n"
              + "LEFT JOIN scm_user_profile T_Agent ON company_sales_agent_person_profile_id = T_Agent.id\n"
              + "LEFT JOIN scm_district T_Dist ON customer_district_id = T_Dist.id\n"
              + "LEFT JOIN scm_territory T_Terr ON customer_territory_id = T_Terr.id\n"
              + ",scm_sales_invoice_item,scm_customer,scm_account,scm_product,scm_product_batch,scm_product_detail,scm_product_category "
              + " WHERE scm_sales_invoice.sales_invoice_status_id >= ? AND\n"
              + "scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + "   AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + "   AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + "   AND scm_account.id = scm_product_detail.account_id\n"
              + "   AND scm_product_batch.product_id = scm_product.id\n"
              + "   AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "   AND scm_product.product_category_id = scm_product_category.id     ";

      sqlGroupBy = " GROUP BY scm_customer.customer_name ,scm_customer.gst_no ,\n"
              + "T_Dist.district_name ,T_Terr.territory_name , scm_sales_invoice.customer_pin ,scm_sales_invoice.id, \n"
              + "scm_sales_invoice.invoice_no ,scm_sales_invoice.invoice_date ,scm_sales_invoice.is_sales_interstate \n"
              + " ORDER BY UPPER(scm_customer.customer_name),scm_sales_invoice.invoice_date,scm_sales_invoice.invoice_no ";

    } else if ("5".equals(filterParameters.getReportType())) {  //CUSTOMER_PRODUCT_WISE = 5;

      sql = "SELECT scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
              + "	    T_Dist.district_name as district_name,T_Terr.territory_name as territory_name, scm_sales_invoice.customer_pin as pin_code,\n"
              + "	    scm_manufacture.code as mfr_code,scm_product.product_name as product_name,scm_sales_invoice_item.hsn_code as hsn_code,getPackDimension(scm_product.id) as pack_type, \n"
              + "	    scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,            \n"
              + "     SUM(scm_sales_invoice_item.product_qty) as qty,\n"
              + "     SUM(COALESCE (scm_sales_invoice_item.product_qty_free,0)) as qty_free, \n"
              + "	    scm_sales_invoice_item.value_mrp as value_mrp,scm_sales_invoice_item.prod_piece_selling_forced as rate,\n"
              + "	    scm_sales_invoice_item.value_prod_piece_selling as value_pts,scm_sales_invoice_item.value_ptr as value_ptr,\n"
              + "	    SUM(scm_sales_invoice_item.scheme_discount_value) as scheme_discount,SUM(scm_sales_invoice_item.product_discount_value) as product_discount,\n"
              + "	    SUM((COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0)))) as invoice_discount, \n"
              + "     SUM(scm_sales_invoice_item.value_goods) as goods_value,scm_tax_code.rate_percentage as tax_perc, \n"
              + "     SUM(COALESCE(scm_sales_invoice_item.kerala_flood_cess_value,0)) as state_cess, \n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN scm_sales_invoice_item.value_igst ELSE 0 END) as igst,\n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END) as cgst,\n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END) as sgst,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_igst,0))  as gst_amount,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)+COALESCE(scm_sales_invoice_item.value_igst,0)+\n"
              + "     CASE WHEN scm_sales_invoice.cash_discount_taxable is null OR scm_sales_invoice.cash_discount_taxable = 0 \n"
              + "     THEN -COALESCE(scm_sales_invoice_item.cash_discount_value,0) ELSE 0 END) as net_amount,"
              + "     SUM(COALESCE(scm_sales_invoice_item.cash_discount_value,0)) as cash_discount, \n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)) as sales_value \n"
              + "     FROM \n"
              + "     scm_sales_invoice \n"
              + "	    LEFT JOIN scm_district T_Dist ON customer_district_id = T_Dist.id\n"
              + "	    LEFT JOIN scm_territory T_Terr ON customer_territory_id = T_Terr.id\n"
              + "	   ,scm_sales_invoice_item,scm_tax_code,scm_customer,scm_account,scm_product_detail,scm_product,scm_product_category,"
              + "     scm_product_batch LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id\n"
              + "     WHERE scm_sales_invoice.sales_invoice_status_id >= ? AND\n"
              + "     scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + "     AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + "     AND scm_sales_invoice_item.tax_code_id =scm_tax_code.id\n"
              + "     AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + "     AND scm_account.id = scm_product_detail.account_id\n"
              + "     AND scm_product_batch.product_id = scm_product.id\n"
              + "     AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "     AND scm_product.product_category_id = scm_product_category.id ";
      sqlGroupBy = " GROUP BY scm_customer.customer_name ,scm_customer.gst_no ,scm_tax_code.rate_percentage,\n"
              + "T_Dist.district_name ,T_Terr.territory_name , scm_sales_invoice.customer_pin ,scm_sales_invoice_item.prod_piece_selling_forced,\n"
              + "scm_manufacture.code,scm_product.product_name,scm_sales_invoice_item.hsn_code ,pack_type,\n"
              + "scm_product_batch.batch_no,scm_product_batch.expiry_date_actual,scm_sales_invoice_item.value_mrp,\n"
              + "scm_sales_invoice_item.value_prod_piece_selling,scm_sales_invoice_item.value_ptr,scm_sales_invoice.is_sales_interstate \n"
              + " ORDER BY UPPER(scm_customer.customer_name),UPPER(scm_product.product_name) ";

    } else if ("6".equals(filterParameters.getReportType())) {  //TERRITORY_WISE = 6;
      sql = "SELECT T_Dist.district_name as district_name,T_Terr.territory_name as territory_name,\n"
              + "	    SUM(scm_sales_invoice_item.scheme_discount_value) as scheme_discount,SUM(scm_sales_invoice_item.product_discount_value) as product_discount,\n"
              + "	    SUM((COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0)))) as invoice_discount, \n"
              + "     SUM(scm_sales_invoice_item.value_goods) as goods_value,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.kerala_flood_cess_value,0)) as state_cess, \n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN scm_sales_invoice_item.value_igst ELSE 0 END) as igst,\n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END) as cgst,\n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END) as sgst,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_igst,0))  as gst_amount,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)+COALESCE(scm_sales_invoice_item.value_igst,0)+\n"
              + "     CASE WHEN scm_sales_invoice.cash_discount_taxable is null OR scm_sales_invoice.cash_discount_taxable = 0 \n"
              + "     THEN -COALESCE(scm_sales_invoice_item.cash_discount_value,0) ELSE 0 END) as net_amount,"
              + "     SUM(COALESCE(scm_sales_invoice_item.cash_discount_value,0)) as cash_discount, \n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)) as sales_value \n"
              + "     FROM \n"
              + "     scm_sales_invoice \n"
              + "	    LEFT JOIN scm_district T_Dist ON customer_district_id = T_Dist.id\n"
              + "	    LEFT JOIN scm_territory T_Terr ON customer_territory_id = T_Terr.id\n"
              + "	   ,scm_sales_invoice_item,scm_customer,scm_account,scm_product,scm_product_batch,scm_product_detail,scm_product_category\n"
              + "     WHERE scm_sales_invoice.sales_invoice_status_id >= ? AND\n"
              + "     scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + "     AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + "     AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + "     AND scm_account.id = scm_product_detail.account_id\n"
              + "     AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "     AND scm_product_batch.product_id = scm_product.id\n"
              + "     AND scm_product.product_category_id = scm_product_category.id ";
      sqlGroupBy = " GROUP BY  \n"
              + "T_Dist.district_name ,T_Terr.territory_name  ,\n"
              + "scm_sales_invoice.is_sales_interstate \n"
              + " ORDER BY UPPER(T_Terr.territory_name) ";

    } else if ("7".equals(filterParameters.getReportType())) {  //TERRITORY_CUSTOMER_WISE = 7;

      sql = "SELECT T_Dist.district_name as district_name,T_Terr.territory_name as territory_name,\n"
              + "     scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
              + "	    SUM(scm_sales_invoice_item.scheme_discount_value) as scheme_discount,SUM(scm_sales_invoice_item.product_discount_value) as product_discount,\n"
              + "	    SUM((COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0)))) as invoice_discount, \n"
              + "     SUM(scm_sales_invoice_item.value_goods) as goods_value,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.kerala_flood_cess_value,0)) as state_cess, \n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN scm_sales_invoice_item.value_igst ELSE 0 END) as igst,\n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END) as cgst,\n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END) as sgst,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_igst,0))  as gst_amount,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)+COALESCE(scm_sales_invoice_item.value_igst,0)+\n"
              + "     CASE WHEN scm_sales_invoice.cash_discount_taxable is null OR scm_sales_invoice.cash_discount_taxable = 0 \n"
              + "     THEN -COALESCE(scm_sales_invoice_item.cash_discount_value,0) ELSE 0 END) as net_amount,"
              + "     SUM(COALESCE(scm_sales_invoice_item.cash_discount_value,0)) as cash_discount,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)) as sales_value \n"
              + "     FROM \n"
              + "     scm_sales_invoice \n"
              + "	    LEFT JOIN scm_district T_Dist ON customer_district_id = T_Dist.id\n"
              + "	    LEFT JOIN scm_territory T_Terr ON customer_territory_id = T_Terr.id\n"
              + "	   ,scm_sales_invoice_item,scm_customer,scm_account,scm_product,scm_product_batch,scm_product_detail,scm_product_category\n"
              + "     WHERE scm_sales_invoice.sales_invoice_status_id >= ? AND\n"
              + "     scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + "     AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + "     AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + "     AND scm_account.id = scm_product_detail.account_id\n"
              + "     AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "     AND scm_product_batch.product_id = scm_product.id\n"
              + "     AND scm_product.product_category_id = scm_product_category.id ";
      sqlGroupBy = " GROUP BY  \n"
              + "T_Dist.district_name ,T_Terr.territory_name  ,\n"
              + "scm_customer.customer_name,scm_customer.gst_no,scm_sales_invoice.is_sales_interstate \n"
              + " ORDER BY UPPER(T_Terr.territory_name),UPPER(scm_customer.customer_name) ";

    } else if ("8".equals(filterParameters.getReportType())) {  //TERRITORY_CUSTOMER_PRODUCT_WISE = 8;

      sql = "SELECT T_Dist.district_name as district_name,T_Terr.territory_name as territory_name,\n"
              + "     scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
              + "	    scm_manufacture.code as mfr_code,scm_product.product_name as product_name,scm_sales_invoice_item.hsn_code as hsn_code,getPackDimension(scm_product.id) as pack_type, \n"
              + "	    scm_product_batch.batch_no as batch_no,scm_product_batch.expiry_date_actual as expiry_date,            \n"
              + "	    SUM(scm_sales_invoice_item.scheme_discount_value) as scheme_discount,SUM(scm_sales_invoice_item.product_discount_value) as product_discount,\n"
              + "	    SUM((COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0)))) as invoice_discount, \n"
              + "     SUM(scm_sales_invoice_item.value_goods) as goods_value,scm_tax_code.rate_percentage as tax_perc,\n"
              + "     SUM(scm_sales_invoice_item.product_qty) as qty,\n"
              + "     SUM(COALESCE (scm_sales_invoice_item.product_qty_free,0)) as qty_free, \n"
              + "	    scm_sales_invoice_item.value_mrp as value_mrp,scm_sales_invoice_item.prod_piece_selling_forced as rate,\n"
              + "	    scm_sales_invoice_item.value_prod_piece_selling as value_pts,scm_sales_invoice_item.value_ptr as value_ptr,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.kerala_flood_cess_value,0)) as state_cess, \n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN scm_sales_invoice_item.value_igst ELSE 0 END) as igst,\n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END) as cgst,\n"
              + "	    SUM(CASE WHEN is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END) as sgst,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_igst,0))  as gst_amount,\n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)+COALESCE(scm_sales_invoice_item.value_igst,0)+\n"
              + "     CASE WHEN scm_sales_invoice.cash_discount_taxable is null OR scm_sales_invoice.cash_discount_taxable = 0 \n"
              + "     THEN -COALESCE(scm_sales_invoice_item.cash_discount_value,0) ELSE 0 END) as net_amount,"
              + "     SUM(COALESCE(scm_sales_invoice_item.cash_discount_value,0)) as cash_discount, \n"
              + "     SUM(COALESCE(scm_sales_invoice_item.value_taxable,0)) as sales_value \n"
              + "     FROM \n"
              + "     scm_sales_invoice \n"
              + "	    LEFT JOIN scm_district T_Dist ON customer_district_id = T_Dist.id\n"
              + "	    LEFT JOIN scm_territory T_Terr ON customer_territory_id = T_Terr.id\n"
              + "	   ,scm_sales_invoice_item,scm_tax_code,scm_customer,scm_account,scm_product_detail,scm_product,scm_product_category,\n"
              + "     scm_product_batch LEFT JOIN scm_manufacture ON scm_product_batch.manufacture_id = scm_manufacture.id\n"
              + "     WHERE scm_sales_invoice.sales_invoice_status_id >= ? AND\n"
              + "     scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
              + "     AND scm_sales_invoice.customer_id = scm_customer.id\n"
              + "     AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
              + "     AND scm_account.id = scm_product_detail.account_id\n"
              + "     AND scm_sales_invoice_item.tax_code_id = scm_tax_code.id\n"
              + "     AND scm_product_batch.product_id = scm_product.id\n"
              + "     AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "     AND scm_product.product_category_id = scm_product_category.id ";
      sqlGroupBy = " GROUP BY  \n"
              + "T_Dist.district_name ,T_Terr.territory_name  ,\n"
              + "scm_customer.customer_name,scm_customer.gst_no,scm_tax_code.rate_percentage,scm_sales_invoice.is_sales_interstate ,scm_sales_invoice_item.prod_piece_selling_forced,\n"
              + "scm_manufacture.code,scm_product.product_name,scm_sales_invoice_item.hsn_code ,pack_type,\n"
              + "scm_product_batch.batch_no,scm_product_batch.expiry_date_actual,scm_sales_invoice_item.value_mrp, \n"
              + "scm_sales_invoice_item.value_prod_piece_selling,scm_sales_invoice_item.value_ptr \n"
              + " ORDER BY UPPER(T_Terr.territory_name),UPPER(scm_customer.customer_name),UPPER(scm_product.product_name) ";

    }

    ArrayList<Object> params = new ArrayList<>();
    ArrayList<Object> paramsReturn = new ArrayList<>();
    params.add(SystemConstants.CONFIRMED);
    params.add(company.getId());

    if (filterParameters.isIncludeReturn()) {
      sqlReturnCondition += " AND ret.company_id=? ";
      paramsReturn.add(company.getId());
    }
    if (filterParameters.getBrand() != null && filterParameters.getBrand().getId() != null) {
      sqlCondition += " AND scm_sales_invoice.account_group_id IN(SELECT scm_account_group.id FROM scm_account_group_brands, scm_account_group WHERE \n"
              + "scm_account_group.id =scm_account_group_brands.account_group_id AND scm_account_group.company_id =? "
              + "AND scm_account_group_brands.brand_id =?) ";
      params.add(company.getId());
      params.add(filterParameters.getBrand().getId());
      if (filterParameters.isIncludeReturn()) {
        sqlReturnCondition += "  AND ret.account_group_id IN(SELECT scm_account_group.id FROM scm_account_group_brands, scm_account_group WHERE \n"
                + "scm_account_group.id =scm_account_group_brands.account_group_id AND scm_account_group.company_id =? "
                + "AND scm_account_group_brands.brand_id =?) ";
        paramsReturn.add(company.getId());
        paramsReturn.add(filterParameters.getBrand().getId());
      }
    } else {
      if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
        sqlCondition += " AND scm_sales_invoice.account_group_id = ?";
        params.add(filterParameters.getAccountGroup().getId());
        if (filterParameters.isIncludeReturn()) {
          sqlReturnCondition += " AND ret.account_group_id=? ";
          paramsReturn.add(filterParameters.getAccountGroup().getId());
        }
      }

      if (filterParameters.getCustomer() != null && filterParameters.getCustomer().getId() != null) {
        sqlCondition += " AND scm_sales_invoice.customer_id = ?";
        params.add(filterParameters.getCustomer().getId());
        if (filterParameters.isIncludeReturn()) {
          sqlReturnCondition += " AND ret.customer_id=? ";
          paramsReturn.add(filterParameters.getCustomer().getId());
        }
      }
      if (filterParameters.getAccount() != null && filterParameters.getAccount().getId() != null) {
        sqlCondition += " AND scm_product_detail.account_id = ?";
        params.add(filterParameters.getAccount().getId());
      }
    }
    if (filterParameters.getFromDate() != null) {
      sqlCondition += " AND scm_sales_invoice.invoice_date::date >= to_date(?,'YYYY-MM-DD')";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
      if (filterParameters.isIncludeReturn()) {
        sqlReturnCondition += " AND ret.entry_date::date >= to_date(?,'YYYY-MM-DD') ";
        paramsReturn.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
      }
    }
    if (filterParameters.getToDate() != null) {
      sqlCondition += " AND scm_sales_invoice.invoice_date::date <= to_date(?,'YYYY-MM-DD')";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
      if (filterParameters.isIncludeReturn()) {
        sqlReturnCondition += " AND ret.entry_date::date <= to_date(?,'YYYY-MM-DD') ";
        paramsReturn.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
      }
    }
    if (filterParameters.isIncludeSales()) {
      sql = sql + sqlCondition + sqlGroupBy;
      if (filterParameters.isIncludeReturn()) {
        sql += " UNION ALL "
                + (sqlReturn + sqlReturnCondition + sqlReturnGroupBy);
        params.addAll(paramsReturn);
      }
    } else {
      if (filterParameters.isIncludeReturn()) {
        sql = sqlReturn + sqlReturnCondition + sqlReturnGroupBy;
        params.clear();
        params.addAll(paramsReturn);
      }
    }
    sql += sqlOrderBy;

    return AppDb.getList(main.dbConnector(), CompanyCustomerSales.class, sql, params.toArray());
  }

  public static List<Brand> brandList(Main main, Company company, String filter) {
    if (filter != null) {
      return AppLookup.getAutoFilter(Brand.class, "SELECT id,name,code FROM scm_brand WHERE id IN"
              + "(SELECT scm_account_group_brands.brand_id FROM scm_account_group_brands, scm_account_group WHERE\n"
              + "scm_account_group.id =scm_account_group_brands.account_group_id AND scm_account_group.company_id =?)"
              + " AND UPPER(name) like ?", new Object[]{company.getId(), "%" + filter.toUpperCase() + "%"});
    } else {
      return AppService.list(main, Brand.class, "SELECT id,name,code FROM scm_brand WHERE id IN"
              + "(SELECT scm_account_group_brands.brand_id FROM scm_account_group_brands, scm_account_group WHERE\n"
              + "scm_account_group.id =scm_account_group_brands.account_group_id AND scm_account_group.company_id =?)", new Object[]{company.getId()});
    }
  }

  public static List<CompanyCustomerSales> getSalesRegisterList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "";
    String sqlCondition = " AND scm_product.company_id =?";
    String sqlReturn = "";
    String sqlReturnCondition = "";
    String sqlGroupBy = "";
    String sqlReturnGroupBy = "";
    String sqlOrderBy = "";
    sql = "SELECT scm_sales_invoice.invoice_no as invoice_no,scm_sales_invoice.invoice_date as invoice_date, \n"
            + "scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,\n"
            + "T_Dist.district_name as district_name,T_Terr.territory_name as territory_name, scm_sales_invoice.customer_pin as pin_code,scm_sales_invoice.id,	    \n"
            + "SUM(COALESCE(scm_sales_invoice_item.value_goods,0)) as goods_value,\n"
            + "SUM(COALESCE(scm_sales_invoice_item.scheme_discount_value,0)) as scheme_discount,SUM(COALESCE(scm_sales_invoice_item.product_discount_value,0)) as product_discount,\n"
            + "SUM((COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0)))) as invoice_discount,             \n"
            + "COALESCE(scm_sales_invoice.kerala_flood_cess_net_value,0) as state_cess, \n"
            + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN scm_sales_invoice.invoice_amount_igst ELSE 0 END,0) as igst,\n"
            + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice.invoice_amount_cgst END,0) as cgst,\n"
            + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE scm_sales_invoice.invoice_amount_sgst END,0) as sgst,\n"
            + "scm_sales_invoice.invoice_amount_igst as gst_amount,\n"
            + "COALESCE(scm_sales_invoice.freight_rate,0)+COALESCE(scm_sales_invoice.service2_rate,0) as service_taxable_amount,\n"
            + "COALESCE(scm_sales_invoice.kerala_flood_cess_service1_value,0)+COALESCE(scm_sales_invoice.kerala_flood_cess_service2_value,0) as service_cess_amount,\n"
            + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN COALESCE(scm_sales_invoice.service_value_igst,0)+COALESCE(scm_sales_invoice.service2_igst,0) ELSE 0 END,0) as service_igst,\n"
            + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE COALESCE(scm_sales_invoice.service_value_cgst,0)+COALESCE(scm_sales_invoice.service2_cgst,0) END,0) as service_cgst,\n"
            + "COALESCE(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone =1 THEN 0 ELSE COALESCE(scm_sales_invoice.service_value_sgst,0)+COALESCE(scm_sales_invoice.service2_sgst,0) END,0) as service_sgst,\n"
            + "COALESCE(scm_sales_invoice.service_value_igst,0)+COALESCE(scm_sales_invoice.service2_igst,0) as service_gst_amount,\n"
            + "scm_sales_invoice.invoice_amount_subtotal as invoice_amount,scm_sales_invoice.tcs_applicable_amount,scm_sales_invoice.tcs_net_value as tcs_net_amount,\n"
            + "scm_sales_invoice.invoice_amount as net_amount,            \n"
            + "COALESCE(scm_sales_invoice.cash_discount_value,0) as cash_discount, \n"
            + "COALESCE(scm_sales_invoice.invoice_amount_assessable,0) as sales_value,1 as sales_or_return,'SL' as invoice_type \n"
            + "FROM \n"
            + "scm_sales_invoice \n"
            + "LEFT JOIN scm_district T_Dist ON customer_district_id = T_Dist.id\n"
            + "LEFT JOIN scm_territory T_Terr ON customer_territory_id = T_Terr.id\n"
            + ",scm_sales_invoice_item,scm_customer,scm_account,scm_product_detail,scm_product,scm_product_batch,scm_product_category\n"
            + "   WHERE scm_sales_invoice.sales_invoice_status_id >= ? AND\n"
            + "   scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
            + "   AND scm_sales_invoice.customer_id = scm_customer.id\n"
            + "   AND scm_account.id = scm_product_detail.account_id\n"
            + "   AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
            + "   AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
            + "   AND scm_product_batch.product_id = scm_product.id\n"
            + "   AND scm_product.product_category_id = scm_product_category.id \n";

    sqlGroupBy += " GROUP BY scm_customer.customer_name ,scm_customer.gst_no ,scm_sales_invoice.id,scm_sales_invoice.sez_zone,\n"
            + "T_Dist.district_name ,T_Terr.territory_name , scm_sales_invoice.customer_pin ,\n"
            + "scm_sales_invoice.invoice_no ,scm_sales_invoice.invoice_date ,\n"
            + "scm_sales_invoice.invoice_amount_igst,scm_sales_invoice.invoice_amount_cgst,scm_sales_invoice.invoice_amount_sgst,\n"
            + "scm_sales_invoice.invoice_amount_discount,scm_sales_invoice.invoice_amount_igst,scm_sales_invoice.invoice_amount,\n"
            + "scm_sales_invoice.cash_discount_value,scm_sales_invoice.is_sales_interstate,scm_sales_invoice.invoice_amount_assessable,\n"
            + "freight_rate,service2_rate,service_value_igst,service2_igst,service_value_cgst,service2_cgst,service_value_sgst,service2_sgst,\n"
            + "invoice_amount_subtotal,tcs_applicable_amount,tcs_net_value,kerala_flood_cess_service1_value,kerala_flood_cess_service2_value \n";
    sqlOrderBy += "  ORDER BY invoice_date,invoice_no \n";

    sqlReturn = "select ret.invoice_no,ret.entry_date as invoice_date,cust.customer_name,cust.gst_no,dist.district_name,ter.territory_name,c_add.pin as pin_code,ret.id,\n"
            + "-SUM(COALESCE(ret_item.value_goods,0)) as goods_value,-SUM(COALESCE(ret_item.scheme_discount_value,0)) as scheme_discount,\n"
            + "-SUM(COALESCE(ret_item.product_discount_value,0)) as product_discount,-SUM(COALESCE(ret_item.invoice_discount_value,0)) as invoice_discount,\n"
            + "null as state_cess,COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone = 0 THEN 0 ELSE -ret.invoice_amount_igst END,0) as igst,\n"
            + "COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone = 0 THEN -ret.invoice_amount_cgst ELSE 0 END,0) as cgst,\n"
            + "COALESCE(CASE WHEN comp_add.state_id =c_add.state_id AND ret.sez_zone = 0 THEN -ret.invoice_amount_sgst ELSE 0 END,0) as sgst,\n"
            + "-ret.invoice_amount_igst as gst_amount,\n"
            + "0 as service_taxable_value,0as service_cess_amount,0 as service_igst,0 as service_cgst,0 as service_sgst,0 as service_gst_amount,-ret.invoice_amount as invoice_amount,\n"
            + "0 as tcs_applicable_amount,0 as tcs_net_value,\n"
            + "-ret.invoice_amount as net_amount,\n"
            + "-SUM(COALESCE(ret_item.cash_discount_value,0)) as cash_discount,-COALESCE(ret.invoice_amount_assessable,0) as sales_value,2 as sales_or_return,\n"
            + "CASE WHEN ret.sales_return_type = 1 THEN 'SR-S' ELSE 'SR-D' END as invoice_type\n"
            + "from scm_sales_return ret\n"
            + "LEFT JOIN scm_territory ter on ter.id=ret.territory_id,scm_sales_return_item ret_item,\n"
            + "scm_customer cust,scm_customer_address c_add,scm_district dist,scm_company_address comp_add\n"
            + "where ret_item.sales_return_id=ret.id and ret.sales_return_status_id=2\n"
            + "and ret.customer_id=cust.id  and c_add.id=ret.customer_address_id and c_add.district_id=dist.id\n"
            + "and comp_add.company_id=ret.company_id\n";

    sqlReturnGroupBy = "GROUP BY ret.invoice_no,ret.entry_date,cust.customer_name,ret.sez_zone,cust.taxable,cust.gst_no,dist.district_name,ter.territory_name,c_add.pin,ret.id,comp_add.state_id,\n"
            + "c_add.state_id,ret.invoice_amount_igst,ret.invoice_amount_cgst,ret.invoice_amount_sgst,ret.invoice_amount\n";

    ArrayList<Object> params = new ArrayList<>();
    ArrayList<Object> paramsReturn = new ArrayList<>();
    params.add(SystemConstants.CONFIRMED);
    params.add(company.getId());

    if (filterParameters.isIncludeReturn()) {
      sqlReturnCondition += " AND ret.company_id=? ";
      paramsReturn.add(company.getId());
    }
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      sqlCondition += " AND scm_sales_invoice.account_group_id = ?";
      params.add(filterParameters.getAccountGroup().getId());
      if (filterParameters.isIncludeReturn()) {
        sqlReturnCondition += " AND ret.account_group_id=? ";
        paramsReturn.add(filterParameters.getAccountGroup().getId());
      }
    }

    if (filterParameters.getCustomer() != null && filterParameters.getCustomer().getId() != null) {
      sqlCondition += " AND scm_sales_invoice.customer_id = ?";
      params.add(filterParameters.getCustomer().getId());
      if (filterParameters.isIncludeReturn()) {
        sqlReturnCondition += " AND ret.customer_id=? ";
        paramsReturn.add(filterParameters.getCustomer().getId());
      }
    }
    if (filterParameters.getAccount() != null && filterParameters.getAccount().getId() != null) {
      sqlCondition += " AND scm_product_detail.account_id = ?";
      params.add(filterParameters.getAccount().getId());
    }
    if (filterParameters.getFromDate() != null) {
      sqlCondition += " AND scm_sales_invoice.invoice_date::date >= ?::date";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
      if (filterParameters.isIncludeReturn()) {
        sqlReturnCondition += " AND ret.entry_date::date >= ?::date ";
        paramsReturn.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
      }
    }
    if (filterParameters.getToDate() != null) {
      sqlCondition += " AND scm_sales_invoice.invoice_date::date <= ?::date";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
      if (filterParameters.isIncludeReturn()) {
        sqlReturnCondition += " AND ret.entry_date::date <= ?::date ";
        paramsReturn.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
      }
    }
    if (filterParameters.isIncludeReturn()) {
      sql = sql + sqlCondition + sqlGroupBy + " UNION ALL "
              + (sqlReturn + sqlReturnCondition + sqlReturnGroupBy)
              + sqlOrderBy;
      params.addAll(paramsReturn);
    } else {
      sql = sql + sqlCondition + sqlGroupBy + sqlOrderBy;
    }
    return AppDb.getList(main.dbConnector(), CompanyCustomerSales.class, sql, params.toArray());
  }
}
