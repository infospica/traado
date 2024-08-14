/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import spica.reports.model.FilterParameters;
import spica.reports.model.Gst3bReport;
import spica.scm.domain.Company;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author godson
 */
public class Gst3bReportService {

  public static List<Gst3bReport> getGst3bPurchaseReportList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "SELECT tab.group_name,tab.account_group_id,tab.id,tab.document_no,tab.invoice_no,tab.invoice_date,tab.entry_date,tab.supplier_name,tab.gst_no,tab.bill_amount,tab.tax_percentage,\n"
            + "SUM(tab.taxable_value) as taxable_value,SUM(tab.tax_data) as tax_data,tab.is_interstate,tab.place_of_supply,tab.type,tab.tcs_value FROM\n"
            + "(SELECT scm_account_group.group_name,scm_account_group.id as account_group_id, scm_product_entry.id,scm_product_entry.invoice_no as invoice_no,\n"
            + "scm_product_entry.account_invoice_no as document_no,TO_CHAR(scm_product_entry.invoice_date,'dd-Mon-YYYY') as invoice_date,\n"
            + "TO_CHAR(scm_product_entry.product_entry_date,'dd-Mon-YYYY') as entry_date,scm_vendor.vendor_name as supplier_name,\n"
            + "scm_vendor.gst_no as gst_no,scm_product_entry.invoice_amount as bill_amount,\n"
            + "scm_tax_code.rate_percentage as tax_percentage,\n"
            + "SUM((CASE WHEN is_service=2 THEN scm_product_entry_detail.value_goods ELSE\n"
            + "(scm_product_entry_detail.value_assessable*(scm_product_entry_detail.product_quantity+COALESCE(scm_product_entry_detail.product_quantity_free,0))) END)) as taxable_value,\n"
            + "SUM(value_igst) as tax_data,(CASE WHEN scm_product_entry.is_purchase_interstate=1 OR scm_product_entry.sez_zone = 1 THEN 1 ELSE 0 END)as is_interstate, \n"
            + "CONCAT(scm_state.state_code,'-',scm_state.state_name) as place_of_supply, 'purchase' as type,scm_product_entry.tcs_net_value as tcs_value \n"
            + "FROM scm_product_entry_detail,scm_product_entry,scm_account,scm_vendor,scm_tax_code, scm_state ,scm_account_group_detail , scm_account_group\n"
            + "WHERE scm_product_entry_detail.product_entry_id = scm_product_entry.id\n"
            + "AND scm_product_entry.account_id = scm_account.id \n"
            + "AND scm_account.vendor_id = scm_vendor.id\n"
            + "AND scm_vendor.state_id=scm_state.id\n"
            + "AND scm_tax_code.id = scm_product_entry_detail.tax_code_id\n"
            + "AND scm_product_entry.opening_stock_entry=0 AND scm_product_entry.product_entry_status_id =2\n"
            + "AND scm_product_entry.company_id=?\n"
            + "AND scm_product_entry.product_entry_date::date >=?::date AND scm_product_entry.product_entry_date::date <=?::date\n"
            + "AND scm_account.id = scm_account_group_detail.account_id\n"
            + "AND scm_account_group_detail.account_group_id = scm_account_group.id\n"
            + "AND scm_account_group.is_default = 1\n"
            + "GROUP BY scm_account_group.group_name,scm_account_group.id,scm_product_entry.id,scm_product_entry.invoice_no,TO_CHAR(scm_product_entry.invoice_date,'dd-Mon-YYYY'),\n"
            + "TO_CHAR(scm_product_entry.product_entry_date,'dd-Mon-YYYY'),scm_vendor.vendor_name,scm_product_entry.account_invoice_no, scm_state.state_code, scm_state.state_name,\n"
            + "scm_vendor.gst_no,scm_product_entry.invoice_amount,product_entry_id,scm_tax_code.rate_percentage,scm_product_entry.is_purchase_interstate,scm_product_entry_detail.is_service,\n"
            + "scm_product_entry_detail.product_quantity,scm_product_entry_detail.product_quantity_free,scm_product_entry.tcs_net_value\n"
            + "ORDER BY supplier_name\n"
            + ")tab\n"
            + "GROUP BY tab.group_name,tab.account_group_id,tab.id,tab.invoice_no,tab.document_no,tab.invoice_date,tab.entry_date,tab.supplier_name,tab.gst_no,tab.bill_amount,tab.tax_percentage,\n"
            + "tab.is_interstate,tab.place_of_supply,tab.type,tab.tcs_value ORDER BY tab.supplier_name\n";

    main.clear();
    main.param(company.getId());
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

    return AppDb.getList(main.dbConnector(), Gst3bReport.class, sql, main.getParamData().toArray());
  }

  public static List<Gst3bReport> getGst3bSalesReportList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "SELECT account_group_id,group_name,id,invoice_no as document_no,invoice_no,invoice_date,entry_date,customer_name,gst_no,sales_invoice_status_id,bill_amount, \n"
            + "CASE WHEN (product_or_service=1) THEN tax_percentage END AS tax_percentage,\n"
            + "CASE WHEN (product_or_service=2) THEN tax_percentage END AS service_tax_percentage, \n"
            + "CASE WHEN (product_or_service=1) THEN taxable_value END AS taxable_value,\n"
            + "CASE WHEN (product_or_service=2) THEN taxable_value END AS service_taxable_value, \n"
            + "CASE WHEN (product_or_service=1) THEN tax_data END AS tax_data,\n"
            + "CASE WHEN (product_or_service=2) THEN tax_data END AS service_tax_data, is_interstate,place_of_supply,kf_cess_value,type,product_or_service,tcs_value\n"
            + "FROM(" //                    product
            + "SELECT scm_account_group.id as account_group_id,scm_account_group.group_name, scm_sales_invoice.id,scm_sales_invoice.invoice_no,TO_CHAR(scm_sales_invoice.invoice_date,'dd-Mon-YYYY') as invoice_date,TO_CHAR(scm_sales_invoice.invoice_entry_date,'dd-Mon-YYYY') as entry_date,\n"
            + "scm_customer.customer_name,scm_customer.gst_no,scm_sales_invoice.sales_invoice_status_id,\n"
            + "scm_sales_invoice.invoice_amount as bill_amount,scm_tax_code.rate_percentage as tax_percentage,"
            + "SUM(scm_sales_invoice_item.value_taxable) as taxable_value,\n"
            + "SUM(scm_sales_invoice_item.value_igst) as tax_data,\n"
            + "(CASE WHEN scm_sales_invoice.is_sales_interstate = 1 OR scm_sales_invoice.sez_zone = 1 THEN 1 ELSE 0 END)as is_interstate,  CONCAT(scm_state.state_code,'-',scm_state.state_name) as place_of_supply,\n"
            + "(COALESCE(scm_sales_invoice.kerala_flood_cess_net_value,0)+COALESCE(scm_sales_invoice.kerala_flood_cess_service1_value,0)+"
            + "COALESCE(scm_sales_invoice.kerala_flood_cess_service2_value,0)) as kf_cess_value,COALESCE(scm_sales_invoice.tcs_net_value,0) as tcs_value,\n"
            + "'sales' as type ,1 as product_or_service \n"
            + "FROM scm_sales_invoice, scm_sales_invoice_item, scm_customer, scm_tax_code, scm_state,scm_account_group\n"
            + "WHERE scm_sales_invoice_item.sales_invoice_id = scm_sales_invoice.id \n"
            + "AND scm_sales_invoice.customer_id = scm_customer.id \n"
            + "AND scm_sales_invoice_item.tax_code_id = scm_tax_code.id \n"
            + "AND scm_sales_invoice.company_id = ? \n"
            + "AND scm_sales_invoice.invoice_entry_date::date >= ?::date \n"
            + "AND scm_sales_invoice.invoice_entry_date::date <= ?::date \n"
            + "AND scm_customer.state_id=scm_state.id \n"
            + "AND scm_sales_invoice.sales_invoice_status_id in(2,3)\n"
            + "AND scm_sales_invoice.account_group_id = scm_account_group.id\n"
            + "GROUP BY scm_account_group.id ,scm_account_group.group_name,scm_sales_invoice.id,scm_customer.gst_no,scm_customer.customer_name,scm_sales_invoice.invoice_no,TO_CHAR(scm_sales_invoice.invoice_entry_date,'dd-Mon-YYYY'),\n"
            + "scm_sales_invoice.invoice_amount,scm_tax_code.rate_percentage,scm_sales_invoice.invoice_amount_assessable ,scm_sales_invoice.sales_invoice_status_id,\n"
            + "TO_CHAR(scm_sales_invoice.invoice_date,'dd-Mon-YYYY'),scm_sales_invoice.is_sales_interstate,  scm_state.state_code, scm_state.state_name,\n"
            + "scm_sales_invoice.kerala_flood_cess_net_value,scm_sales_invoice.kerala_flood_cess_service1_value,scm_sales_invoice.kerala_flood_cess_service2_value,\n"
            + "scm_sales_invoice.tcs_net_value \n"
            + "UNION ALL \n" //first service
            + "SELECT scm_account_group.id as account_group_id,scm_account_group.group_name,scm_sales_invoice.id,scm_sales_invoice.invoice_no,TO_CHAR(scm_sales_invoice.invoice_date,'dd-Mon-YYYY') as invoice_date,TO_CHAR(scm_sales_invoice.invoice_entry_date,'dd-Mon-YYYY') as entry_date,\n"
            + "scm_customer.customer_name,scm_customer.gst_no,scm_sales_invoice.sales_invoice_status_id,\n"
            + "scm_sales_invoice.invoice_amount as bill_amount,scm_tax_code.rate_percentage as tax_percentage,scm_sales_invoice.freight_rate as taxable_value,\n"
            + "scm_sales_invoice.service_value_igst as tax_data,\n"
            + "(CASE WHEN scm_sales_invoice.is_sales_interstate = 1 OR scm_sales_invoice.sez_zone = 1 THEN 1 ELSE 0 END)as is_interstate,  CONCAT(scm_state.state_code,'-',scm_state.state_name) as place_of_supply,\n"
            + "(COALESCE(scm_sales_invoice.kerala_flood_cess_net_value,0)+COALESCE(scm_sales_invoice.kerala_flood_cess_service1_value,0)+"
            + "COALESCE(scm_sales_invoice.kerala_flood_cess_service2_value,0)) as kf_cess_value,COALESCE(scm_sales_invoice.tcs_net_value,0) as tcs_value,\n"
            + "'sales' as type ,2 as product_or_service \n"
            + "FROM scm_sales_invoice, scm_sales_invoice_item, scm_customer, scm_tax_code, scm_state,scm_tax_code service_tax,scm_account_group\n"
            + "WHERE scm_sales_invoice_item.sales_invoice_id = scm_sales_invoice.id \n"
            + "AND scm_sales_invoice.customer_id = scm_customer.id \n"
            + "AND scm_sales_invoice.service_tax_code_id = scm_tax_code.id\n"
            + "AND scm_sales_invoice.company_id = ?\n"
            + "AND scm_sales_invoice.invoice_entry_date::date >= ?::date \n"
            + "AND scm_sales_invoice.invoice_entry_date::date <= ?::date \n"
            + "AND scm_customer.state_id=scm_state.id \n"
            + "AND scm_sales_invoice.sales_invoice_status_id in(2,3)\n"
            + "AND scm_sales_invoice.account_group_id = scm_account_group.id\n"
            + "GROUP BY scm_account_group.id ,scm_account_group.group_name,scm_sales_invoice.id,scm_customer.gst_no,scm_customer.customer_name,scm_sales_invoice.invoice_no,TO_CHAR(scm_sales_invoice.invoice_entry_date,'dd-Mon-YYYY'),\n"
            + "scm_sales_invoice.invoice_amount,scm_tax_code.rate_percentage,scm_sales_invoice.invoice_amount_assessable ,scm_sales_invoice.sales_invoice_status_id,\n"
            + "TO_CHAR(scm_sales_invoice.invoice_date,'dd-Mon-YYYY'),scm_sales_invoice.is_sales_interstate,  scm_state.state_code, scm_state.state_name,\n"
            + "scm_sales_invoice.kerala_flood_cess_net_value,scm_sales_invoice.kerala_flood_cess_service1_value,scm_sales_invoice.kerala_flood_cess_service2_value, \n"
            + "scm_sales_invoice.tcs_net_value \n"
            + "UNION ALL \n" //second service
            + "SELECT scm_account_group.id as account_group_id,scm_account_group.group_name,scm_sales_invoice.id,scm_sales_invoice.invoice_no,TO_CHAR(scm_sales_invoice.invoice_date,'dd-Mon-YYYY') as invoice_date,TO_CHAR(scm_sales_invoice.invoice_entry_date,'dd-Mon-YYYY') as entry_date,\n"
            + "scm_customer.customer_name,scm_customer.gst_no,scm_sales_invoice.sales_invoice_status_id,\n"
            + "scm_sales_invoice.invoice_amount as bill_amount,scm_tax_code.rate_percentage as tax_percentage,scm_sales_invoice.service2_rate as taxable_value,\n"
            + "scm_sales_invoice.service2_igst as tax_data,\n"
            + "(CASE WHEN scm_sales_invoice.is_sales_interstate = 1 OR scm_sales_invoice.sez_zone = 1 THEN 1 ELSE 0 END)as is_interstate,  CONCAT(scm_state.state_code,'-',scm_state.state_name) as place_of_supply,\n"
            + "(COALESCE(scm_sales_invoice.kerala_flood_cess_net_value,0)+COALESCE(scm_sales_invoice.kerala_flood_cess_service1_value,0)+"
            + "COALESCE(scm_sales_invoice.kerala_flood_cess_service2_value,0)) as kf_cess_value,COALESCE(scm_sales_invoice.tcs_net_value,0) as tcs_value,\n"
            + "'sales' as type ,2 as product_or_service \n"
            + "FROM scm_sales_invoice, scm_sales_invoice_item, scm_customer, scm_tax_code, scm_state,scm_tax_code service_tax,scm_account_group\n"
            + "WHERE scm_sales_invoice_item.sales_invoice_id = scm_sales_invoice.id \n"
            + "AND scm_sales_invoice.customer_id = scm_customer.id \n"
            + "AND scm_sales_invoice.service2_tax_code_id = scm_tax_code.id\n"
            + "AND scm_sales_invoice.company_id = ?\n"
            + "AND scm_sales_invoice.invoice_entry_date::date >= ?::date \n"
            + "AND scm_sales_invoice.invoice_entry_date::date <= ?::date \n"
            + "AND scm_customer.state_id=scm_state.id \n"
            + "AND scm_sales_invoice.sales_invoice_status_id in(2,3)\n"
            + "AND scm_sales_invoice.account_group_id = scm_account_group.id\n"
            + "GROUP BY scm_account_group.id ,scm_account_group.group_name,scm_sales_invoice.id,scm_customer.gst_no,scm_customer.customer_name,scm_sales_invoice.invoice_no,TO_CHAR(scm_sales_invoice.invoice_entry_date,'dd-Mon-YYYY'),\n"
            + "scm_sales_invoice.invoice_amount,scm_tax_code.rate_percentage,scm_sales_invoice.invoice_amount_assessable ,scm_sales_invoice.sales_invoice_status_id,\n"
            + "TO_CHAR(scm_sales_invoice.invoice_date,'dd-Mon-YYYY'),scm_sales_invoice.is_sales_interstate,  scm_state.state_code, scm_state.state_name,\n"
            + "scm_sales_invoice.kerala_flood_cess_net_value,scm_sales_invoice.kerala_flood_cess_service1_value,scm_sales_invoice.kerala_flood_cess_service2_value,\n"
            + "scm_sales_invoice.tcs_net_value ) tab\n"
            + "GROUP BY account_group_id,group_name,id,invoice_no,invoice_date,entry_date,customer_name,gst_no,tax_percentage,sales_invoice_status_id,bill_amount, is_interstate,place_of_supply,kf_cess_value,type,\n"
            + "product_or_service,taxable_value,tax_data,tcs_value\n"
            + "ORDER BY UPPER (tab.invoice_no), tab.invoice_date,product_or_service\n";
    ArrayList<Object> params = new ArrayList<>();
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    return AppDb.getList(main.dbConnector(), Gst3bReport.class, sql, params.toArray());
  }

  public static List<Gst3bReport> getGst3bSalesReturnReportList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "SELECT tab.account_group_id,tab.group_name,tab.id,tab.invoice_no as document_no,tab.invoice_no,tab.invoice_date,tab.entry_date,tab.customer_name,tab.gst_no,tab.bill_amount,tab.tax_percentage,SUM(tab.taxable_value) as taxable_value,\n"
            + "SUM(tab.tax_data) as tax_data, tab.is_sales_interstate as is_interstate, tab.sales_return_type, tab.return_type, tab.place_of_supply,'sales_return' as type \n"
            + "FROM(SELECT scm_account_group.id as account_group_id,scm_account_group.group_name ,scm_sales_return.id,scm_sales_return.invoice_no, TO_CHAR(scm_sales_return.invoice_date,'dd-Mon-YYYY')  as invoice_date,\n"
            + "TO_CHAR(scm_sales_return.entry_date,'dd-Mon-YYYY') as entry_date,scm_customer.customer_name,scm_customer.gst_no,\n"
            + "scm_sales_return.invoice_amount as bill_amount,scm_tax_code.rate_percentage as tax_percentage,\n"
            + "SUM(scm_sales_return_item.value_assessable) as taxable_value,SUM(scm_sales_return_item.value_igst) as tax_data,\n"
            + "CASE WHEN scm_customer_address.state_id!=scm_company_address.state_id OR scm_sales_return.sez_zone = 1 THEN 1 ELSE 0 END as is_sales_interstate,scm_sales_return.sales_return_type,\n"
            + "CASE WHEN sales_return_type = 1 THEN 'Non-Moving / Near Expiry' ELSE 'Damaged & Expired' END as return_type, CONCAT(scm_state.state_code,'-',scm_state.state_name) as place_of_supply \n"
            + "FROM scm_sales_return, scm_sales_return_item,scm_customer_address,scm_company_address,scm_tax_code,scm_customer,scm_state,scm_account_group\n"
            + "WHERE scm_sales_return_item.sales_return_id = scm_sales_return.id \n"
            + "AND scm_sales_return.sales_return_status_id =2 \n"
            + "AND scm_sales_return.company_id = scm_company_address.company_id \n"
            + "AND scm_sales_return.customer_address_id = scm_customer_address.id\n"
            + "AND scm_sales_return.customer_id = scm_customer.id \n"
            + "AND scm_tax_code.id = scm_sales_return_item.tax_code_id \n"
            + "AND scm_sales_return.company_id = ? \n"
            + "AND scm_customer.state_id = scm_state.id \n"
            + "AND scm_customer.state_id=scm_state.id \n"
            + "AND scm_sales_return.entry_date::date >=?::date AND scm_sales_return.entry_date::date <=?::date \n"
            + "AND scm_sales_return.account_group_id = scm_account_group.id\n"
            + "GROUP BY scm_account_group.id ,scm_account_group.group_name,scm_sales_return.id,scm_sales_return.invoice_no,invoice_date,entry_date,scm_customer.customer_name,scm_customer.gst_no,bill_amount,\n"
            + "tax_percentage,is_sales_interstate,scm_sales_return.sales_return_type, scm_state.state_code, scm_state.state_name,scm_customer.taxable)  tab\n"
            + "GROUP BY tab.account_group_id,tab.group_name,tab.id,tab.invoice_no, tab.invoice_date,tab.entry_date,tab.customer_name,tab.gst_no,tab.bill_amount,tab.tax_percentage, tab.is_sales_interstate,\n"
            + " tab.sales_return_type,tab.return_type,tab.place_of_supply  \n"
            + "ORDER BY tab.invoice_no";

    main.clear();
    main.param(company.getId());
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

    return AppDb.getList(main.dbConnector(), Gst3bReport.class, sql, main.getParamData().toArray());
  }

  public static List<Gst3bReport> getGst3bPurchaseReturnReportList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "select tab.account_group_id,tab.group_name,tab.id,tab.invoice_no as document_no,tab.invoice_no,tab.invoice_date,tab.entry_date,tab.supplier_name,tab.gst_no,tab.state_code,tab.bill_amount,tab.tax_percentage,\n"
            + "SUM(tab.taxable_value) as taxable_value,SUM(tab.tax_data) as tax_data,tab.is_interstate,tab.place_of_supply,tab.type from\n"
            + "(SELECT scm_purchase_return_item.id as scm_purchase_return_item_id,scm_account_group.id as account_group_id,scm_account_group.group_name, scm_purchase_return.id,scm_purchase_return.invoice_no, TO_CHAR(scm_purchase_return.invoice_date,'dd-Mon-YYYY')  as invoice_date,\n"
            + "TO_CHAR(scm_purchase_return.entry_date,'dd-Mon-YYYY') as entry_date,\n"
            + "scm_vendor.vendor_name as supplier_name,scm_vendor.gst_no,scm_state.state_code,\n"
            + "scm_purchase_return.invoice_amount as bill_amount,\n"
            + "scm_tax_code.rate_percentage as tax_percentage,\n"
            + "scm_purchase_return_item.value_net-value_igst as taxable_value,\n"
            + "scm_purchase_return_item.value_igst as tax_data,\n"
            + "CASE WHEN scm_purchase_return.is_return_interstate = 1 OR scm_purchase_return.sez_zone=1 THEN 1 ELSE 0 END as is_interstate,\n"
            + "CONCAT(scm_state.state_code,'-',scm_state.state_name) as place_of_supply,'purchase' as type  \n"
            + "FROM scm_purchase_return, scm_purchase_return_item,scm_vendor_address,scm_company_address,scm_tax_code,scm_vendor,scm_state,scm_account,scm_account_group,scm_account_group_detail\n"
            + "WHERE scm_purchase_return_item.purchase_return_id = scm_purchase_return.id \n"
            + "AND scm_purchase_return.purchase_return_status_id =2 \n"
            + "AND scm_vendor_address.address_type_id =1 AND scm_company_address.address_type_id =1 \n"
            + "AND scm_purchase_return.company_id = scm_company_address.company_id\n"
            + "AND scm_purchase_return.account_id = scm_account.id \n"
            + "AND scm_account.vendor_id = scm_vendor.id \n"
            + "AND scm_vendor.id = scm_vendor_address.vendor_id  \n"
            + "AND scm_tax_code.id = scm_purchase_return_item.tax_code_id \n"
            + "AND scm_purchase_return.company_id = ? \n"
            + "AND scm_vendor.state_id = scm_state.id \n"
            + "AND scm_purchase_return.entry_date::date >=? ::date AND scm_purchase_return.entry_date::date <=?::date \n"
            + "AND scm_purchase_return.account_id = scm_account_group_detail.account_id\n"
            + "AND scm_account_group.id = scm_account_group_detail.account_group_id\n"
            + "AND scm_account_group.is_default = 1\n"
            + "GROUP BY scm_purchase_return_item.id,scm_account_group.id ,scm_account_group.group_name,scm_purchase_return.id,scm_vendor.gst_no,scm_vendor.vendor_name,entry_date,scm_state.state_code,scm_state.state_name,\n"
            + "scm_purchase_return.invoice_no,invoice_date,bill_amount,tax_percentage,tax_data,is_interstate,taxable_value \n"
            + "ORDER BY UPPER(scm_purchase_return.invoice_no), TO_CHAR(scm_purchase_return.invoice_date,'dd-Mon-YYYY'))tab\n"
            + "GROUP BY tab.account_group_id,tab.group_name,tab.id,tab.invoice_no,tab.invoice_date,tab.entry_date,tab.supplier_name,tab.gst_no,tab.state_code,tab.bill_amount,tab.tax_percentage,\n"
            + "tab.is_interstate,tab.place_of_supply,tab.type\n"
            + "ORDER BY tab.invoice_no,tab.invoice_date";

    main.clear();
    main.param(company.getId());
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

    return AppDb.getList(main.dbConnector(), Gst3bReport.class, sql, main.getParamData().toArray());
  }

  public static List<Gst3bReport> getGst3bCdnrCReportList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "select t4.id as account_group_id,t4.group_name, t1.id,fin_debit_credit_note.document_no as document_no,fin_debit_credit_note.document_no as invoice_no, TO_CHAR(fin_debit_credit_note.invoice_date,'dd-Mon-YYYY')  as invoice_date,\n"
            + "TO_CHAR(fin_debit_credit_note.entry_date,'dd-Mon-YYYY') as entry_date, scm_vendor.vendor_name as receiver_name,scm_vendor.gst_no,\n"
            + "fin_debit_credit_note.net_value as bill_amount,scm_tax_code.rate_percentage as tax_percentage,\n"
            + "SUM(fin_debit_credit_note_item.taxable_value) as taxable_value,\n"
            + "SUM(COALESCE(fin_debit_credit_note_item.igst_amount,0)) as tax_data,\n"
            + "CASE WHEN fin_debit_credit_note.is_interstate = 1 OR fin_debit_credit_note.sez_zone = 1 THEN 1 ELSE 0 END as is_interstate,\n"
            + "CONCAT(scm_state.state_code,'-',scm_state.state_name) as place_of_supply,'cdnr_c' as type\n"
            + "FROM scm_vendor, scm_tax_code, scm_state, fin_debit_credit_note, fin_accounting_ledger, fin_debit_credit_note_item,scm_vendor_address,scm_company_address,fin_accounting_transaction_detail_item t1,fin_accounting_transaction_detail t2,fin_accounting_transaction t3,scm_account_group t4\n"
            + "WHERE fin_debit_credit_note_item.debit_credit_note_id = fin_debit_credit_note.id\n"
            + "AND fin_debit_credit_note.accounting_ledger_id = fin_accounting_ledger.id \n"
            + "AND fin_accounting_ledger.entity_id = scm_vendor.id\n"
            + "AND scm_vendor_address.address_type_id =1 AND scm_company_address.address_type_id =1 \n"
            + "AND fin_debit_credit_note_item.tax_code_id= scm_tax_code.id \n"
            + "AND fin_debit_credit_note.company_id = ? \n"
            + "AND t1.accounting_transaction_detail_id = t2.id \n"
            + "AND t2.accounting_transaction_id = t3.id \n"
            + "AND t3.accounting_entity_type_id = 9 \n"
            + "AND t3.entity_id = fin_debit_credit_note.id\n"
            + "AND fin_debit_credit_note.entry_date::date >= TO_DATE(?,'yyyy-mm-dd') \n"
            + "AND fin_debit_credit_note.entry_date::date <= TO_DATE(?,'yyyy-mm-dd')\n"
            + "AND fin_debit_credit_note.invoice_type = 2\n"
            + "AND fin_debit_credit_note.status_id =2\n"
            + "AND fin_debit_credit_note.debit_credit_party =1\n"
            + "AND fin_debit_credit_note.company_id = scm_company_address.company_id\n"
            + "AND fin_accounting_ledger.entity_id = scm_vendor_address.vendor_id \n"
            + "AND scm_vendor.state_id = scm_state.id \n"
            + "AND t3.account_group_id = t4.id\n"
            + "GROUP BY t4.id,t4.group_name,t1.id,fin_debit_credit_note.id,scm_vendor.gst_no,scm_vendor.vendor_name,fin_debit_credit_note.document_no,invoice_date ,fin_debit_credit_note.entry_date ,\n"
            + "fin_debit_credit_note.net_value, scm_tax_code.rate_percentage,scm_state.state_code,scm_state.state_name,fin_debit_credit_note.sez_zone, \n"
            + "scm_vendor_address.state_id,scm_company_address.state_id,fin_debit_credit_note.is_interstate \n"
            + "UNION ALL \n"
            + "select t4.id as account_group_id,t4.group_name, t1.id,fin_debit_credit_note.document_no as document_no,fin_debit_credit_note.document_no as invoice_no, TO_CHAR(fin_debit_credit_note.invoice_date,'dd-Mon-YYYY')  as invoice_date,\n"
            + "TO_CHAR(fin_debit_credit_note.entry_date,'dd-Mon-YYYY') as entry_date, scm_customer.customer_name as receiver_name,scm_customer.gst_no,\n"
            + "fin_debit_credit_note.net_value as bill_amount,scm_tax_code.rate_percentage as tax_percentage,\n"
            + "SUM(fin_debit_credit_note_item.taxable_value) as taxable_value,\n"
            + "SUM(COALESCE(fin_debit_credit_note_item.igst_amount,0)) as tax_data,\n"
            + "CASE WHEN fin_debit_credit_note.is_interstate = 1 OR fin_debit_credit_note.sez_zone = 1 THEN 1 ELSE 0 END as is_interstate,\n"
            + "CONCAT(scm_state.state_code,'-',scm_state.state_name) as place_of_supply,'cdnr_c' as type\n"
            + "FROM scm_customer, scm_tax_code, scm_state, fin_debit_credit_note, fin_accounting_ledger, fin_debit_credit_note_item,scm_customer_address,scm_company_address,fin_accounting_transaction_detail_item t1,fin_accounting_transaction_detail t2,fin_accounting_transaction t3,scm_account_group t4\n"
            + "WHERE fin_debit_credit_note_item.debit_credit_note_id = fin_debit_credit_note.id\n"
            + "AND fin_debit_credit_note.accounting_ledger_id = fin_accounting_ledger.id \n"
            + "AND fin_accounting_ledger.entity_id = scm_customer.id\n"
            + "AND scm_customer_address.address_type_id =1 AND scm_company_address.address_type_id =1 \n"
            + "AND fin_debit_credit_note_item.tax_code_id= scm_tax_code.id \n"
            + "AND fin_debit_credit_note.company_id = ? \n"
            + "AND t1.accounting_transaction_detail_id = t2.id \n"
            + "AND t2.accounting_transaction_id = t3.id \n"
            + "AND t3.accounting_entity_type_id = 9 \n"
            + "AND t3.entity_id = fin_debit_credit_note.id\n"
            + "AND fin_debit_credit_note.entry_date::date >= TO_DATE(?,'yyyy-mm-dd') \n"
            + "AND fin_debit_credit_note.entry_date::date <= TO_DATE(?,'yyyy-mm-dd') \n"
            + "AND fin_debit_credit_note.invoice_type = 2\n"
            + "AND fin_debit_credit_note.status_id =2\n"
            + "AND fin_debit_credit_note.debit_credit_party =2\n"
            + "AND fin_debit_credit_note.company_id = scm_company_address.company_id\n"
            + "AND fin_accounting_ledger.entity_id = scm_customer_address.customer_id \n"
            + "AND scm_customer.state_id = scm_state.id \n"
            + "AND t3.account_group_id = t4.id\n"
            + "GROUP BY t4.id ,t4.group_name,t1.id,fin_debit_credit_note.id,scm_customer.gst_no,customer_name,fin_debit_credit_note.document_no,fin_debit_credit_note.entry_date,invoice_date,bill_amount,scm_state.state_name,\n"
            + "scm_state.state_code,scm_tax_code.rate_percentage,scm_customer_address.state_id,scm_company_address.state_id,fin_debit_credit_note.is_interstate \n"
            + "ORDER BY 6,5 ";
    main.clear();
    main.param(company.getId());
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    main.param(company.getId());
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

    return AppDb.getList(main.dbConnector(), Gst3bReport.class, sql, main.getParamData().toArray());
  }

  public static List<Gst3bReport> getGst3bCdnrDReportList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "select t3.account_group_id,t4.group_name, t1.id,fin_debit_credit_note.document_no as document_no,fin_debit_credit_note.document_no as invoice_no, TO_CHAR(fin_debit_credit_note.invoice_date,'dd-Mon-YYYY')  as invoice_date,\n"
            + "TO_CHAR(fin_debit_credit_note.entry_date,'dd-Mon-YYYY') as entry_date,scm_vendor.vendor_name as receiver_name,scm_vendor.gst_no, \n"
            + "fin_debit_credit_note.net_value as bill_amount,scm_tax_code.rate_percentage as tax_percentage,\n"
            + "SUM(fin_debit_credit_note_item.taxable_value) as taxable_value,\n"
            + "SUM(COALESCE(fin_debit_credit_note_item.igst_amount,0)) as tax_data,\n"
            + "CASE WHEN fin_debit_credit_note.is_interstate = 1 OR fin_debit_credit_note.sez_zone=1 THEN 1 ELSE 0 END as is_interstate,\n"
            + "CONCAT(scm_state.state_code,'-',scm_state.state_name) as place_of_supply,'cdnr_d' as type\n"
            + "FROM scm_vendor, scm_tax_code, scm_state, fin_debit_credit_note, fin_accounting_ledger, fin_debit_credit_note_item,scm_vendor_address,scm_company_address,\n"
            + "fin_accounting_transaction_detail_item t1,fin_accounting_transaction_detail t2,fin_accounting_transaction t3 ,scm_account_group t4\n"
            + "WHERE fin_debit_credit_note_item.debit_credit_note_id = fin_debit_credit_note.id\n"
            + "AND fin_debit_credit_note.accounting_ledger_id = fin_accounting_ledger.id \n"
            + "AND fin_accounting_ledger.entity_id = scm_vendor.id\n"
            + "AND scm_vendor_address.address_type_id =1 AND scm_company_address.address_type_id =1 \n"
            + "AND fin_debit_credit_note_item.tax_code_id= scm_tax_code.id \n"
            + "AND fin_debit_credit_note.company_id = ? \n"
            + "AND t1.accounting_transaction_detail_id = t2.id \n"
            + "AND t2.accounting_transaction_id = t3.id \n"
            + "AND t3.accounting_entity_type_id = 9 \n"
            + "AND t3.entity_id = fin_debit_credit_note.id\n"
            + "AND fin_debit_credit_note.entry_date::date >= TO_DATE(?,'yyyy-mm-dd') AND fin_debit_credit_note.entry_date::date <= TO_DATE(?,'yyyy-mm-dd') \n"
            + "AND fin_debit_credit_note.invoice_type = 1 AND fin_debit_credit_note.status_id =2\n"
            + "AND fin_debit_credit_note.debit_credit_party = 1 AND fin_debit_credit_note.company_id = scm_company_address.company_id\n"
            + "AND fin_accounting_ledger.entity_id = scm_vendor_address.vendor_id AND scm_vendor.state_id = scm_state.id\n"
            + "AND t3.account_group_id = t4.id\n"
            + "GROUP BY t3.account_group_id,t4.group_name,t1.id,fin_debit_credit_note.id,scm_vendor.gst_no,scm_vendor.vendor_name,fin_debit_credit_note.document_no,invoice_date ,fin_debit_credit_note.entry_date ,\n"
            + "fin_debit_credit_note.net_value, scm_tax_code.rate_percentage,scm_state.state_code,scm_state.state_name,fin_debit_credit_note.sez_zone, \n"
            + "scm_vendor_address.state_id,scm_company_address.state_id,fin_debit_credit_note.is_interstate\n"
            + "UNION ALL\n"
            + "select t3.account_group_id,t4.group_name,t1.id,fin_debit_credit_note.document_no as document_no,fin_debit_credit_note.document_no as invoice_no, TO_CHAR(fin_debit_credit_note.invoice_date,'dd-Mon-YYYY')  as invoice_date,\n"
            + "TO_CHAR(fin_debit_credit_note.entry_date,'dd-Mon-YYYY') as entry_date, scm_customer.customer_name as receiver_name,\n"
            + "scm_customer.gst_no,fin_debit_credit_note.net_value as bill_amount,scm_tax_code.rate_percentage as tax_percentage,\n"
            + "SUM(fin_debit_credit_note_item.taxable_value) as taxable_value,\n"
            + "SUM(COALESCE(fin_debit_credit_note_item.igst_amount,0)) as tax_data,\n"
            + "CASE WHEN fin_debit_credit_note.is_interstate = 1 OR fin_debit_credit_note.sez_zone=1  THEN 1 ELSE 0 END as is_interstate,\n"
            + "CONCAT(scm_state.state_code,'-',scm_state.state_name) as place_of_supply,'cdnr_d' as type\n"
            + "FROM scm_customer, scm_tax_code, scm_state, fin_debit_credit_note, fin_accounting_ledger, fin_debit_credit_note_item,scm_customer_address,scm_company_address,\n"
            + "fin_accounting_transaction_detail_item t1,fin_accounting_transaction_detail t2,fin_accounting_transaction t3 ,scm_account_group t4\n"
            + "WHERE fin_debit_credit_note_item.debit_credit_note_id = fin_debit_credit_note.id\n"
            + "AND fin_debit_credit_note.accounting_ledger_id = fin_accounting_ledger.id \n"
            + "AND fin_accounting_ledger.entity_id = scm_customer.id\n"
            + "AND scm_customer_address.address_type_id =1 AND scm_company_address.address_type_id =1 \n"
            + "AND fin_debit_credit_note_item.tax_code_id= scm_tax_code.id \n"
            + "AND fin_debit_credit_note.company_id = ?\n"
            + "AND t1.accounting_transaction_detail_id = t2.id \n"
            + "AND t2.accounting_transaction_id = t3.id \n"
            + "AND t3.accounting_entity_type_id = 9 \n"
            + "AND t3.entity_id = fin_debit_credit_note.id\n"
            + "AND fin_debit_credit_note.entry_date::date >= TO_DATE(?,'yyyy-mm-dd') AND fin_debit_credit_note.entry_date::date <= TO_DATE(?,'yyyy-mm-dd') \n"
            + "AND fin_debit_credit_note.invoice_type = 1 AND fin_debit_credit_note.status_id =2\n"
            + "AND fin_debit_credit_note.company_id = scm_company_address.company_id\n"
            + "AND fin_accounting_ledger.entity_id = scm_customer_address.customer_id\n"
            + "AND scm_customer.state_id = scm_state.id AND fin_debit_credit_note.debit_credit_party = 2\n"
            + "AND t3.account_group_id = t4.id\n"
            + "GROUP BY t3.account_group_id,t4.group_name,t1.id,scm_customer.gst_no,scm_customer.taxable,scm_customer.customer_name,fin_debit_credit_note.document_no,invoice_date ,fin_debit_credit_note.entry_date ,\n"
            + "fin_debit_credit_note.net_value, scm_tax_code.rate_percentage,scm_state.state_code,scm_state.state_name,fin_debit_credit_note.is_interstate,\n"
            + "scm_customer_address.state_id,scm_company_address.state_id,fin_debit_credit_note.sez_zone \n"
            + "ORDER BY 6,5  ";

    main.clear();
    main.param(company.getId());
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

    main.param(company.getId());
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

    return AppDb.getList(main.dbConnector(), Gst3bReport.class, sql, main.getParamData().toArray());
  }
}
