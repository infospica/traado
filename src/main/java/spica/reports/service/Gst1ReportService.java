/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import spica.reports.model.DocumentSummary;
import spica.reports.model.FilterParameters;
import spica.reports.model.GstB2csReport;
import spica.reports.model.GstCdnReport;
import spica.reports.model.GstHsnReport;
import spica.reports.model.Gst1Report;
import spica.scm.domain.Company;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author godson
 */
public class Gst1ReportService {

  public static List<Gst1Report> getGstB2bReportList(Main main, FilterParameters filterParameters, Company company) {
    String sql = "SELECT id,gst_no,sales_invoice_status_id,customer_name,invoice_no,entry_date,ROUND(bill_amount,2) as bill_amount,"
            + "ROUND(SUM(invoice_amount),2) as invoice_amount,\n"
            + "customer_state_id,rate_percentage,ROUND(SUM(tax_amount),2) as tax_amount,\n"
            + "ROUND(SUM(service_tax_amount),2) as service_tax_amount,ROUND(SUM(COALESCE(taxable_value,0)),2) as taxable_value,ROUND(SUM(COALESCE(service_taxable_value,0)),2) as service_taxable_value,\n"
            + "state_code,state_name,ROUND(COALESCE(kf_cess_value,0),2) as  kf_cess_value FROM\n"
            + "(SELECT id,gst_no,sales_invoice_status_id,customer_name,invoice_no,entry_date,bill_amount,invoice_amount,customer_state_id,rate_percentage,\n"
            + "CASE WHEN product_or_service=1 THEN tax_amount END as tax_amount, CASE WHEN product_or_service=2 THEN tax_amount END as service_tax_amount,\n"
            + "CASE WHEN product_or_service=1 THEN taxable_value END as taxable_value,CASE WHEN product_or_service=2 THEN taxable_value END as service_taxable_value,\n"
            + "state_code,state_name,kf_cess_value FROM\n"
            + "(SELECT scm_sales_invoice.id,scm_customer.gst_no,scm_sales_invoice.sales_invoice_status_id,scm_customer.customer_name,scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_entry_date as entry_date,\n"
            + "scm_sales_invoice.invoice_amount as bill_amount,SUM(scm_sales_invoice_item.value_taxable + scm_sales_invoice_item.value_igst) as invoice_amount,\n"
            + "scm_sales_invoice.customer_state_id,scm_tax_code.rate_percentage,SUM(value_igst) as tax_amount,\n"
            + "SUM(scm_sales_invoice_item.value_taxable) as taxable_value,\n"
            + "scm_state.state_code,scm_state.state_name,scm_sales_invoice.kerala_flood_cess_net_value as kf_cess_value, 1 as product_or_service \n"
            + "FROM scm_sales_invoice, scm_sales_invoice_item, scm_customer, scm_tax_code, scm_state\n"
            + "WHERE scm_sales_invoice_item.sales_invoice_id = scm_sales_invoice.id \n"
            + "AND scm_sales_invoice.customer_id = scm_customer.id \n"
            + "AND scm_sales_invoice_item.tax_code_id = scm_tax_code.id \n"
            + "AND scm_sales_invoice.invoice_entry_date::date >= ?::date \n"
            + "AND scm_sales_invoice.invoice_entry_date::date <= ?::date \n"
            + "AND scm_sales_invoice.company_id = ?\n"
            + "AND scm_sales_invoice.customer_state_id = scm_state.id\n"
            + "AND scm_customer.gst_no IS NOT NULL\n"
            + "AND scm_customer.gst_no <> '' \n"
            + "AND scm_sales_invoice.sales_invoice_status_id in(2,3) \n"
            + "GROUP BY scm_sales_invoice.id,scm_customer.gst_no,scm_customer.customer_name,scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_entry_date,\n"
            + "scm_sales_invoice.invoice_amount,scm_sales_invoice.customer_state_id,scm_tax_code.rate_percentage,scm_state.state_code,scm_state.state_name,\n"
            + "scm_sales_invoice.kerala_flood_cess_net_value,scm_sales_invoice.invoice_amount_assessable ,scm_sales_invoice.sales_invoice_status_id\n"
            + "UNION ALL \n"
            + "SELECT scm_sales_invoice.id,scm_customer.gst_no,scm_sales_invoice.sales_invoice_status_id,scm_customer.customer_name,scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_entry_date as entry_date,\n"
            + "scm_sales_invoice.invoice_amount as bill_amount,freight_rate+service_value_igst as invoice_amount,\n"
            + "scm_sales_invoice.customer_state_id,scm_tax_code.rate_percentage,scm_sales_invoice.service_value_igst as tax_amount,\n"
            + "scm_sales_invoice.freight_rate as taxable_value,\n"
            + "scm_state.state_code,scm_state.state_name,scm_sales_invoice.kerala_flood_cess_service1_value as kf_cess_value,2 as product_or_service \n"
            + "FROM scm_sales_invoice, scm_sales_invoice_item, scm_customer, scm_tax_code, scm_state\n"
            + "WHERE scm_sales_invoice_item.sales_invoice_id = scm_sales_invoice.id \n"
            + "AND scm_sales_invoice.customer_id = scm_customer.id \n"
            + "AND scm_sales_invoice.service_tax_code_id = scm_tax_code.id \n"
            + "AND scm_sales_invoice.invoice_entry_date::date >= ?::date \n"
            + "AND scm_sales_invoice.invoice_entry_date::date <= ?::date \n"
            + "AND scm_sales_invoice.company_id = ?\n"
            + "AND scm_sales_invoice.customer_state_id = scm_state.id\n"
            + "AND scm_customer.gst_no IS NOT NULL\n"
            + "AND scm_customer.gst_no <> '' \n"
            + "AND scm_sales_invoice.sales_invoice_status_id in(2,3) \n"
            + "GROUP BY scm_sales_invoice.id,scm_customer.gst_no,scm_customer.customer_name,scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_entry_date,\n"
            + "scm_sales_invoice.invoice_amount,scm_sales_invoice.customer_state_id,scm_tax_code.rate_percentage,scm_state.state_code,scm_state.state_name,\n"
            + "scm_sales_invoice.kerala_flood_cess_net_value ,scm_sales_invoice.sales_invoice_status_id\n"
            + "UNION ALL\n"
            + "SELECT scm_sales_invoice.id,scm_customer.gst_no,scm_sales_invoice.sales_invoice_status_id,scm_customer.customer_name,scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_entry_date as entry_date,\n"
            + "scm_sales_invoice.invoice_amount as bill_amount,service2_rate+service2_igst as invoice_amount,\n"
            + "scm_sales_invoice.customer_state_id,scm_tax_code.rate_percentage,scm_sales_invoice.service2_igst as tax_amount,\n"
            + "scm_sales_invoice.service2_rate as taxable_value,\n"
            + "scm_state.state_code,scm_state.state_name,scm_sales_invoice.kerala_flood_cess_service2_value as kf_cess_value,2 as product_or_service \n"
            + "FROM scm_sales_invoice, scm_sales_invoice_item, scm_customer, scm_tax_code, scm_state\n"
            + "WHERE scm_sales_invoice_item.sales_invoice_id = scm_sales_invoice.id \n"
            + "AND scm_sales_invoice.customer_id = scm_customer.id \n"
            + "AND scm_sales_invoice.service2_tax_code_id = scm_tax_code.id \n"
            + "AND scm_sales_invoice.invoice_entry_date::date >= ?::date \n"
            + "AND scm_sales_invoice.invoice_entry_date::date <= ?::date \n"
            + "AND scm_sales_invoice.company_id = ?\n"
            + "AND scm_sales_invoice.customer_state_id = scm_state.id\n"
            + "AND scm_customer.gst_no IS NOT NULL\n"
            + "AND scm_customer.gst_no <> '' \n"
            + "AND scm_sales_invoice.sales_invoice_status_id in(2,3) \n"
            + "GROUP BY scm_sales_invoice.id,scm_customer.gst_no,scm_customer.customer_name,scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_entry_date,\n"
            + "scm_sales_invoice.invoice_amount,scm_sales_invoice.customer_state_id,scm_tax_code.rate_percentage,scm_state.state_code,scm_state.state_name,\n"
            + "scm_sales_invoice.kerala_flood_cess_net_value ,scm_sales_invoice.sales_invoice_status_id) tab\n"
            + "GROUP BY id,gst_no,sales_invoice_status_id,customer_name,invoice_no,entry_date,bill_amount,customer_state_id,rate_percentage,state_code,state_name,kf_cess_value,product_or_service,\n"
            + "tax_amount,service_tax_amount,taxable_value,service_taxable_value,invoice_amount) tab2\n"
            + "GROUP BY id,gst_no,sales_invoice_status_id,customer_name,invoice_no,entry_date,bill_amount,customer_state_id,rate_percentage,state_code,state_name,kf_cess_value\n"
            + "ORDER BY UPPER(invoice_no), entry_date";
    List<Object> params = new ArrayList<Object>();
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    return AppDb.getList(main.dbConnector(), Gst1Report.class, sql, params.toArray());
  }

  public static List<GstCdnReport> getGstCdnrCReportList(Main main, FilterParameters filterParameters, Company company) {
    String sql = "SELECT SR.id,scm_customer.gst_no,scm_customer.customer_name as name, SR.entry_date  as refund_date,SR.note,\n"
            + "SR.invoice_no as refund_number,\n"
            + "SR.reference_no as receipt_no,SR.reference_invoice_date as reciept_date,scm_state.state_code,scm_state.state_name,\n"
            + "SR.invoice_amount as bill_amount,SUM(SR_Item.value_assessable+COALESCE(SR_Item.value_igst,0)) as refund_value,\n"
            + "CASE WHEN SR.is_taxable =1 THEN scm_tax_code.rate_percentage ELSE 0 END as tax_rate,\n"
            + "SUM(SR_Item.value_assessable) as taxable_value,\n"
            + "CASE WHEN scm_customer_address.state_id!=scm_company_address.state_id OR SR.sez_zone = 1 THEN 1 ELSE 0 END as is_interstate ,'C' as document_type,\n"
            + "CASE WHEN sales_return_type = 1 THEN 'Non-Moving / Near Expiry' ELSE 'Damaged & Expired' END as return_type,0 as is_split\n"
            + "FROM  scm_sales_return_item as SR_Item,\n"
            + "scm_sales_return as SR,scm_customer_address,scm_company_address,scm_tax_code,scm_customer,scm_state\n"
            + "WHERE    SR_Item.sales_return_id = SR.id AND \n"
            + "SR.sales_return_status_id =2 AND\n"
            + "(COALESCE(SR.return_split,0)=0 OR (COALESCE(SR.return_split,0)=1 AND COALESCE(SR.return_split_for_gst_filing,0)=0))\n"
            + "AND scm_customer_address.address_type_id =1 AND scm_company_address.address_type_id =1 \n"
            + "AND SR.company_id = scm_company_address.company_id \n"
            + "AND SR.customer_address_id = scm_customer_address.id \n"
            + "AND SR.customer_id = scm_customer.id \n"
            + "AND scm_tax_code.id = SR_Item.tax_code_id \n"
            + "AND SR.company_id = ? \n"
            + "AND scm_customer.state_id = scm_state.id \n"
            + "AND scm_customer.gst_no IS NOT NULL\n"
            + "AND scm_customer.gst_no <> '' \n"
            + "AND SR.entry_date::date >=?::date AND SR.entry_date::date <=?::date \n"
            + "GROUP BY SR.id,scm_customer.gst_no,scm_customer.customer_name,reciept_date,scm_state.state_code,scm_state.state_name, \n"
            + "SR.note,refund_number,refund_date,bill_amount,tax_rate,is_interstate,document_type,SR.sez_zone,scm_customer_address.state_id\n"
            + "UNION ALL\n"
            + "SELECT SR.id,scm_customer.gst_no,scm_customer.customer_name as name, SR.entry_date  as refund_date,SR.note,\n"
            + "SR.invoice_no as refund_number,\n"
            + "SR.reference_no as receipt_no,SR.reference_invoice_date as reciept_date,scm_state.state_code,scm_state.state_name,\n"
            + "SR.invoice_amount as bill_amount,SUM(SR_Item.value_assessable+COALESCE(SR_Item.value_igst,0)) as refund_value,\n"
            + "CASE WHEN SR.is_taxable =1 THEN scm_tax_code.rate_percentage ELSE 0 END as tax_rate,\n"
            + "SUM(SR_Item.value_assessable) as taxable_value,\n"
            + "CASE WHEN scm_customer_address.state_id!=scm_company_address.state_id OR SR.sez_zone = 1 THEN 1 ELSE 0 END as is_interstate ,'C' as document_type,\n"
            + "CASE WHEN sales_return_type = 1 THEN 'Non-Moving / Near Expiry' ELSE 'Damaged & Expired' END as return_type,1 as is_split\n"
            + "FROM  scm_sales_return_item_split as SR_Item,\n"
            + "scm_sales_return_split as SR,scm_customer_address,scm_company_address,scm_tax_code,scm_customer,scm_state\n"
            + "WHERE    SR_Item.sales_return_split_id = SR.id AND SR.sales_return_status_id =2 AND\n"
            + "(COALESCE(SR.return_split,0)=1 AND COALESCE(SR.return_split_for_gst_filing,0)=1)\n"
            + "AND scm_customer_address.address_type_id =1 AND scm_company_address.address_type_id =1 \n"
            + "AND SR.company_id = scm_company_address.company_id \n"
            + "AND SR.customer_id = scm_customer_address.customer_id \n"
            + "AND SR.customer_id = scm_customer.id \n"
            + "AND scm_customer.gst_no IS NOT NULL\n"
            + "AND scm_customer.gst_no <> '' \n"
            + "AND scm_tax_code.id = SR_Item.tax_code_id \n"
            + "AND SR.company_id = ? \n"
            + "AND scm_customer.state_id = scm_state.id \n"
            + "AND SR.entry_date::date >=?::date AND SR.entry_date::date <=?::date \n"
            + "GROUP BY SR.id,scm_customer.gst_no,scm_customer.customer_name,reciept_date,scm_state.state_code,scm_state.state_name, \n"
            + "SR.note,refund_number,refund_date,bill_amount,tax_rate,is_interstate,document_type, SR.sez_zone,scm_customer_address.state_id\n"
            + "UNION ALL\n"
            + "select deb_cred.id,cust.gst_no,cust.customer_name as name,deb_cred.entry_date as refund_date,dc_item.description as note,\n"
            + "deb_cred.document_no as refund_number,dc_item.ref_invoice_no as receipt_no,dc_item.ref_invoice_date as reciept_date,scm_state.state_code,\n"
            + "scm_state.state_name,deb_cred.net_value as bill_amount,\n"
            + "SUM(COALESCE(dc_item.net_value,0)) as refund_value,scm_tax_code.rate_percentage as tax_rate,SUM(COALESCE(dc_item.taxable_value,0)) as taxable_value,\n"
            + "CASE WHEN comp_add.state_id!= cust.state_id OR deb_cred.sez_zone = 1 THEN 1 ELSE 0 END as is_interstate,\n"
            + "CASE WHEN deb_cred.invoice_type = 2 THEN 'C' ELSE 'D' END as document_type,'' as return_type,2 as is_split\n"
            + "from fin_debit_credit_note_item dc_item,\n"
            + "fin_debit_credit_note deb_cred,fin_accounting_ledger ledger,scm_customer cust,scm_state,scm_tax_code,scm_company_address comp_add\n"
            + "where deb_cred.accounting_ledger_id = ledger.id AND ledger.entity_id = cust.id AND deb_cred.debit_credit_party = 2 AND comp_add.company_id=deb_cred.company_id \n"
            + "AND deb_cred.status_id = 2 \n"
            + "AND cust.gst_no IS NOT NULL\n"
            + "AND cust.gst_no <> '' \n"
            + "AND dc_item.debit_credit_note_id = deb_cred.id AND cust.state_id = scm_state.id AND dc_item.tax_code_id = scm_tax_code.id\n"
            + "AND deb_cred.company_id =?  AND deb_cred.entry_date::date >=?::date AND deb_cred.entry_date::date <=?::date \n"
            + "GROUP BY deb_cred.id,cust.gst_no,cust.customer_name,deb_cred.entry_date,cust.state_id ,deb_cred.sez_zone,comp_add.state_id,dc_item.description,deb_cred.document_no,dc_item.ref_invoice_no,\n"
            + "dc_item.ref_invoice_date,scm_state.state_code,scm_state.state_name,deb_cred.net_value,scm_tax_code.rate_percentage,is_interstate\n"
            + "ORDER BY name,refund_number,tax_rate";
    return AppDb.getList(main.dbConnector(), GstCdnReport.class, sql, new Object[]{company.getId(), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()),
      SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()), company.getId(), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()),
      SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()), company.getId(), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()),
      SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate())});
  }

  public static List<GstCdnReport> getGstCdnrDReportList(Main main, FilterParameters filterParameters, Company company) {
    String sql = "SELECT scm_purchase_return.id,scm_vendor.gst_no,scm_vendor.vendor_name as name, scm_purchase_return.invoice_date  as reciept_date,'',\n"
            + "scm_purchase_return.invoice_no as refund_number,scm_purchase_return.entry_date as refund_date,scm_state.state_code,scm_state.state_name,\n"
            + "scm_purchase_return.invoice_amount as refund_value,\n"
            + "scm_tax_code.rate_percentage as tax_rate,\n"
            + "scm_purchase_return.invoice_amount_net as taxable_value,\n"
            + "CASE WHEN scm_vendor_address.state_id!=scm_company_address.state_id THEN 1 ELSE 0 END as is_interstate, 'D' as document_type,\n"
            + "CASE WHEN purchase_return_stock_cat = 2 THEN 'Non-Moving / Near Expiry' ELSE 'Damaged & Expired' END as return_type \n"
            + "FROM scm_purchase_return, scm_purchase_return_item,scm_vendor_address,scm_company_address,scm_tax_code,scm_vendor,scm_state\n"
            + "WHERE scm_purchase_return_item.purchase_return_id = scm_purchase_return.id \n"
            + "AND scm_purchase_return.purchase_return_status_id =2 \n"
            + "AND scm_vendor_address.address_type_id =1 AND scm_company_address.address_type_id =1 \n"
            + "AND scm_purchase_return.company_id = scm_company_address.company_id\n"
            + "AND scm_purchase_return.account_id = scm_vendor_address.vendor_id \n"
            + "AND scm_purchase_return.account_id = scm_vendor.id \n"
            + "AND scm_tax_code.id = scm_purchase_return_item.tax_code_id \n"
            + "AND scm_purchase_return.company_id = ?\n"
            + "AND scm_vendor.state_id = scm_state.id \n"
            + "AND scm_purchase_return.entry_date::date >=?::date AND scm_purchase_return.entry_date::date <=?::date \n"
            + "GROUP BY scm_purchase_return.id,scm_vendor.gst_no,scm_vendor.vendor_name,reciept_date,scm_state.state_code,scm_state.state_name,\n"
            + "refund_number,refund_date,refund_value,tax_rate,is_interstate,taxable_value, document_type ";
    List<Object> params = new ArrayList<Object>();
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    return AppDb.getList(main.dbConnector(), GstCdnReport.class, sql, params.toArray());
  }

  public static List<GstB2csReport> getGstB2csReportList(Main main, FilterParameters filterParameters, Company company) {
    String sql = "SELECT TAB.rate_percentage,TAB.state_code,TAB.state_name,SUM(TAB.taxable_amount) as taxable_value,TAB.type,SUM(COALESCE(TAB.cess_amount,0)) as cess_amount FROM\n"
            + "(select t1.rate_percentage ,t2.invoice_no,\n"
            + "SUM(t3.value_taxable) as taxable_amount\n"
            + ",t5.state_code,t5.state_name,1 as type,SUM(COALESCE(t3.kerala_flood_cess_value,0)) as cess_amount\n"
            + "from scm_tax_code t1,scm_sales_invoice t2,scm_sales_invoice_item t3,scm_customer t4,scm_state t5\n"
            + "where t1.id=t3.tax_code_id\n"
            + "AND t2.id = t3.sales_invoice_id\n"
            + "AND t2.customer_id = t4.id\n"
            + "AND COALESCE(t4.gst_no,'')=''\n"
            + "AND t2.sales_invoice_status_id in(2,3) \n"
            + "AND (t2.invoice_entry_date::date >= ?::date AND t2.invoice_entry_date::date <= ?::date) \n"
            + "AND t2.company_id = ?\n"
            + "AND t4.state_id = t5.id\n"
            + "group by t2.invoice_no,t1.rate_percentage,t5.state_code,t5.state_name\n"
            + "UNION ALL\n" //service 1
            + "select t1.rate_percentage ,t2.invoice_no,\n"
            + "t2.freight_rate as taxable_amount\n"
            + ",t5.state_code,t5.state_name,1 as type,t2.kerala_flood_cess_service1_value\n"
            + "from scm_tax_code t1,scm_sales_invoice t2,scm_customer t4,scm_state t5\n"
            + "where t1.id=t2.service_tax_code_id\n"
            + "AND t2.customer_id = t4.id\n"
            + "AND COALESCE(t4.gst_no,'')=''\n"
            + "AND t2.sales_invoice_status_id in(2,3) \n"
            + "AND (t2.invoice_entry_date::date >= ?::date AND t2.invoice_entry_date::date <= ?::date) \n"
            + "AND t2.company_id = ?\n"
            + "AND t4.state_id = t5.id\n"
            + "group by t2.invoice_no,t2.freight_rate,t1.rate_percentage,t5.state_code,t5.state_name,t2.kerala_flood_cess_service1_value \n"
            + "UNION ALL\n" //service 2
            + "select t1.rate_percentage ,t2.invoice_no,\n"
            + "t2.service2_rate as taxable_amount\n"
            + ",t5.state_code,t5.state_name,1 as type,t2.kerala_flood_cess_service2_value\n"
            + "from scm_tax_code t1,scm_sales_invoice t2,scm_customer t4,scm_state t5\n"
            + "where t1.id=t2.service2_tax_code_id\n"
            + "AND t2.customer_id = t4.id\n"
            + "AND COALESCE(t4.gst_no,'')=''\n"
            + "AND t2.sales_invoice_status_id in(2,3) \n"
            + "AND (t2.invoice_entry_date::date >= ?::date AND t2.invoice_entry_date::date <= ?::date) \n"
            + "AND t2.company_id = ?\n"
            + "AND t4.state_id = t5.id\n"
            + "group by t2.invoice_no,t2.service2_rate,t1.rate_percentage,t5.state_code,t5.state_name,t2.kerala_flood_cess_service2_value \n"
            + "UNION ALL \n" //sales return
            + "select t1.rate_percentage ,t2.invoice_no,\n"
            + "-SUM(t3.value_assessable) as taxable_amount\n"
            + ",t5.state_code,t5.state_name,2 as type,null as cess_amount\n"
            + "from scm_tax_code t1,scm_sales_return t2,scm_sales_return_item t3,scm_customer t4,scm_state t5\n"
            + "where t1.id=t3.tax_code_id\n"
            + "AND t2.id = t3.sales_return_id\n"
            + "AND t2.customer_id = t4.id\n"
            + "AND COALESCE(t4.gst_no,'')=''\n"
            + "AND t2.sales_return_status_id =2\n"
            + "AND (t2.entry_date::date >= ?::date AND t2.entry_date::date <= ?::date) \n"
            + "AND t2.company_id = ?\n"
            + "AND t4.state_id = t5.id\n"
            + "group by t2.invoice_no,t1.rate_percentage,t5.state_code,t5.state_name\n"
            + "UNION ALL\n" //debit credit note
            + "select t1.rate_percentage ,t2.invoice_no,\n"
            + "CASE WHEN t2.invoice_type = 1 THEN SUM(t3.taxable_value) ELSE \n"
            + "-SUM(t3.taxable_value) END as taxable_amount\n"
            + ",t5.state_code,t5.state_name,2 as type,null as cess_amount\n"
            + "from scm_tax_code t1,fin_debit_credit_note t2,fin_debit_credit_note_item t3,scm_customer t4,scm_state t5,fin_accounting_ledger t6\n"
            + "where t1.id=t3.tax_code_id\n"
            + "AND t2.id = t3.debit_credit_note_id\n"
            + "AND t6.id=t2.accounting_ledger_id\n"
            + "AND t6.entity_id = t4.id\n"
            + "AND t6.accounting_entity_type_id=2\n"
            + "AND COALESCE(t4.gst_no,'')=''\n"
            + "AND t2.status_id =2\n"
            + "AND (t2.entry_date::date >= ?::date AND t2.entry_date::date <= ?::date) \n"
            + "AND t2.company_id = ?\n"
            + "AND t4.state_id = t5.id\n"
            + "group by t2.invoice_no,t1.rate_percentage,t5.state_code,t5.state_name,invoice_type"
            + ") AS TAB\n"
            + "GROUP BY TAB.type,TAB.rate_percentage,TAB.state_code,TAB.state_name";
    List<Object> paramsAll = new ArrayList<Object>();
    List<Object> params = new ArrayList<Object>();
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    paramsAll.addAll(params);
    paramsAll.addAll(params);
    paramsAll.addAll(params);
    paramsAll.addAll(params);
    paramsAll.addAll(params);
    return AppDb.getList(main.dbConnector(), GstB2csReport.class, sql, paramsAll.toArray());
  }

  public static List<GstHsnReport> getGstHsnReportList(Main main, FilterParameters filterParameters, Company company) {
    String salesHSN = "SELECT scm_sales_invoice_item.hsn_code as hsn_code,UPPER(scm_product_category.title) as product_category_title,\n"
            + "UPPER(scm_service_commodity.title) as commodity_title,UPPER(scm_product_packing.title) as product_packing_title,\n"
            + "SUM((scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0))) as total_quantity,\n"
            + "SUM((scm_sales_invoice_item.value_taxable+COALESCE(value_igst,0)+COALESCE(kerala_flood_cess_value,0))) as total_value,"
            + "SUM(scm_sales_invoice_item.value_taxable) as total_taxable_value,\n"
            + "SUM(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN scm_sales_invoice_item.value_igst ELSE 0 END) as total_igst,\n"
            + "SUM(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN 0 ELSE scm_sales_invoice_item.value_cgst END) as total_cgst,\n"
            + "SUM(CASE WHEN scm_sales_invoice.is_sales_interstate =1 OR scm_sales_invoice.sez_zone = 1 THEN 0 ELSE scm_sales_invoice_item.value_sgst END) as total_sgst,\n"
            + "scm_sales_invoice.is_sales_interstate as is_sales_interstate, SUM(COALESCE(scm_sales_invoice_item.kerala_flood_cess_value,0)) as kf_cess_value,'1' as invoice_or_return\n"
            + "FROM scm_sales_invoice,scm_sales_invoice_item,scm_product,scm_product_batch,scm_product_detail,scm_service_commodity,scm_product_packing,scm_product_packing_detail,\n"
            + "scm_product_category\n"
            + "WHERE scm_sales_invoice_item.sales_invoice_id = scm_sales_invoice.id\n"
            + "AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
            + "AND scm_product_batch.product_id = scm_product.id\n"
            + "AND scm_product.commodity_id = scm_service_commodity.id\n"
            + "AND scm_product.product_packing_detail_id = scm_product_packing_detail.id\n"
            + "AND scm_product.product_category_id=scm_product_category.id \n"
            + "AND scm_sales_invoice.sales_invoice_status_id in(2,3)\n"
            + "AND scm_product_packing_detail.pack_primary = scm_product_packing.id\n"
            + "AND scm_sales_invoice.invoice_entry_date::date >= ?::date AND scm_sales_invoice.invoice_entry_date::date <= ?::date AND scm_sales_invoice.company_id = ?\n"
            + " \n";
    ArrayList<Object> params = new ArrayList<>();
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    if (filterParameters.getAccountGroup() != null) {
      salesHSN += " AND scm_sales_invoice.account_group_id=? ";
      params.add(filterParameters.getAccountGroup().getId());
    }
    String salesGroupBy = " GROUP BY scm_sales_invoice.is_sales_interstate,scm_sales_invoice_item.hsn_code,scm_sales_invoice.sez_zone,scm_service_commodity.title,"
            + "scm_product_packing.title,invoice_or_return,scm_product_category.title \n";
    String returnHSN = "SELECT SR_Item.hsn_code as hsn_code,UPPER(scm_product_category.title) as product_category_title,\n"
            + "UPPER(scm_service_commodity.title),UPPER(scm_product_packing.title),\n"
            + "-1 * SUM(( CASE WHEN SR.sales_return_type=2 THEN (SR_Item.product_quantity_damaged) ELSE (SR_Item.product_quantity+COALESCE(SR_Item.product_quantity_free,0)) END)) as total_quantity,\n"
            + "-1 *SUM((SR_Item.value_assessable+COALESCE(value_igst,0))) as total_value,-1*SUM(SR_Item.value_assessable) as total_taxable_value,\n"
            + "-1 *SUM(CASE WHEN scm_customer_address.state_id != scm_company_address.state_id OR SR.sez_zone = 1 THEN SR_Item.value_igst ELSE 0 END) as total_igst,\n"
            + "-1 *SUM(CASE WHEN scm_customer_address.state_id != scm_company_address.state_id OR SR.sez_zone = 1 THEN 0 ELSE SR_Item.value_cgst END) as total_cgst,\n"
            + "-1 *SUM(CASE WHEN scm_customer_address.state_id != scm_company_address.state_id OR SR.sez_zone = 1 THEN 0 ELSE SR_Item.value_sgst END) as total_sgst,\n"
            + "CASE WHEN scm_customer_address.state_id!=scm_company_address.state_id THEN 1 ELSE 0 END as is_sales_interstate, '0' as kf_cess_value,'2' as invoice_or_return\n"
            + "FROM scm_sales_return as SR,scm_sales_return_item as SR_Item,scm_product,scm_product_batch,scm_product_detail,scm_service_commodity,scm_product_packing,scm_product_packing_detail,scm_customer,\n"
            + "scm_customer_address,scm_company_address,scm_product_category\n"
            + "WHERE SR.sales_return_status_id =2 \n"
            + "AND (COALESCE(SR.return_split,0)=0 OR (COALESCE(SR.return_split,0)=1 AND COALESCE(SR.return_split_for_gst_filing,0)=0))\n"
            + "AND scm_customer_address.address_type_id =1 AND scm_company_address.address_type_id =1\n"
            + "AND SR.company_id = scm_company_address.company_id\n"
            + "AND SR.customer_id =scm_customer.id \n"
            + "AND SR.customer_address_id = scm_customer_address.id\n"
            + "AND SR_Item.sales_return_id = SR.id\n"
            + "AND SR_Item.product_detail_id = scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
            + "AND scm_product_batch.product_id = scm_product.id\n"
            + "AND scm_product.commodity_id = scm_service_commodity.id\n"
            + "AND scm_product.product_packing_detail_id = scm_product_packing_detail.id\n"
            + "AND scm_product_packing_detail.pack_primary = scm_product_packing.id\n"
            + "AND scm_product.product_category_id=scm_product_category.id \n"
            + "AND (SR.entry_date::date >= ?::date AND SR.entry_date::date <= ?::date) AND SR.company_id = ?\n";
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    if (filterParameters.getAccountGroup() != null) {
      returnHSN += " AND SR.account_group_id = ? ";
      params.add(filterParameters.getAccountGroup().getId());
    }
    String returnGroupBy = " GROUP BY is_sales_interstate,SR_Item.hsn_code,scm_service_commodity.title,SR.sez_zone,scm_product_packing.title,invoice_or_return,"
            + " scm_customer.taxable,scm_product_category.title ";
    String returnSplitHSN = " SELECT SR_Item.hsn_code as hsn_code,UPPER(scm_product_category.title) as product_category_title,\n"
            + "UPPER(scm_service_commodity.title),UPPER(scm_product_packing.title),\n"
            + "-1 * SUM(( CASE WHEN SR.sales_return_type=2 THEN (SR_Item.product_quantity_damaged) ELSE (SR_Item.product_quantity+COALESCE(SR_Item.product_quantity_free,0)) END)) as total_quantity,\n"
            + "-1 *SUM((SR_Item.value_assessable+COALESCE(value_igst,0))) as total_value,-1*SUM(SR_Item.value_assessable) as total_taxable_value,\n"
            + "-1 *SUM(CASE WHEN scm_customer_address.state_id != scm_company_address.state_id OR SR.sez_zone = 1 THEN SR_Item.value_igst ELSE 0 END) as total_igst,\n"
            + "-1 *SUM(CASE WHEN scm_customer_address.state_id != scm_company_address.state_id OR SR.sez_zone = 1 THEN 0 ELSE SR_Item.value_cgst END) as total_cgst,\n"
            + "-1 *SUM(CASE WHEN scm_customer_address.state_id != scm_company_address.state_id OR SR.sez_zone = 1 THEN 0 ELSE SR_Item.value_sgst END) as total_sgst,\n"
            + "CASE WHEN scm_customer_address.state_id!=scm_company_address.state_id THEN 1 ELSE 0 END as is_sales_interstate, '0' as kf_cess_value,'2' as invoice_or_return\n"
            + "FROM scm_sales_return_split as SR,scm_sales_return_item_split as SR_Item,scm_product,scm_product_batch,scm_product_detail,scm_service_commodity,scm_product_packing,scm_product_packing_detail,\n"
            + "scm_customer_address,scm_company_address,scm_customer,scm_product_category\n"
            + "WHERE SR.sales_return_status_id =2 AND\n"
            + "(COALESCE(SR.return_split,0)=1 AND COALESCE(SR.return_split_for_gst_filing,0)=1)\n"
            + "AND scm_customer_address.address_type_id =1 AND scm_company_address.address_type_id =1\n"
            + "AND SR.company_id = scm_company_address.company_id\n"
            + "AND SR.customer_id = scm_customer.id\n"
            + "AND SR.customer_address_id = scm_customer_address.id\n"
            + "AND SR_Item.sales_return_split_id = SR.id\n"
            + "AND SR_Item.product_detail_id = scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
            + "AND scm_product_batch.product_id = scm_product.id\n"
            + "AND scm_product.commodity_id = scm_service_commodity.id\n"
            + "AND scm_product.product_packing_detail_id = scm_product_packing_detail.id\n"
            + "AND scm_product_packing_detail.pack_primary = scm_product_packing.id\n"
            + "AND scm_product.product_category_id=scm_product_category.id \n"
            + "AND (SR.entry_date::date >= ?::date AND SR.entry_date::date <= ?::date) AND SR.company_id = ? \n";
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    if (filterParameters.getAccountGroup() != null) {
      returnSplitHSN += " AND SR.account_group_id = ? ";
      params.add(filterParameters.getAccountGroup().getId());
    }
    String returnSplitGroupBy = " GROUP BY is_sales_interstate,SR_Item.hsn_code,scm_service_commodity.title,SR.sez_zone,scm_product_packing.title,invoice_or_return,"
            + "scm_customer.taxable,scm_product_category.title  \n";
    String debitCreditHSN = " SELECT \n"
            + "dc_item.hsn_sac_code as hsn_code,TAB.product_category_title,\n"
            + "CASE WHEN dc_item.product_id IS NULL THEN UPPER(dc_item.title) ELSE UPPER(TAB.commodity) END,UPPER(TAB.packing),\n"
            + "NULL as total_quantity,\n"
            + "CASE WHEN deb_cred.invoice_type = 2 THEN -1 ELSE 1 END *SUM(COALESCE(dc_item.net_value,0)) as total_value,\n"
            + "CASE WHEN deb_cred.invoice_type = 2 THEN -1 ELSE 1 END *SUM(COALESCE(dc_item.taxable_value,0)) as total_taxable_value,\n"
            + "CASE WHEN deb_cred.invoice_type = 2 THEN -1 ELSE 1 END *SUM(CASE WHEN scm_customer_address.state_id != scm_company_address.state_id OR deb_cred.sez_zone = 1 THEN COALESCE(dc_item.igst_amount,0) ELSE 0 END) as total_igst,\n"
            + "CASE WHEN deb_cred.invoice_type = 2 THEN -1 ELSE 1 END *SUM(CASE WHEN scm_customer_address.state_id != scm_company_address.state_id OR deb_cred.sez_zone = 1 THEN 0 ELSE COALESCE(dc_item.cgst_amount,0) END) as total_cgst,\n"
            + "CASE WHEN deb_cred.invoice_type = 2 THEN -1 ELSE 1 END *SUM(CASE WHEN scm_customer_address.state_id != scm_company_address.state_id OR deb_cred.sez_zone = 1 THEN 0 ELSE COALESCE(dc_item.sgst_amount,0) END) as total_sgst,\n"
            + "CASE WHEN scm_customer_address.state_id!=scm_company_address.state_id THEN 1 ELSE 0 END as is_sales_interstate,\n"
            + " '0' as kf_cess_value,CASE WHEN deb_cred.invoice_type = 2 THEN '2' ELSE '1' END as invoice_or_return\n"
            + "FROM fin_debit_credit_note as deb_cred,fin_debit_credit_note_item as dc_item \n"
            + "LEFT JOIN (SELECT scm_product.id as id,UPPER(scm_product_category.title) as product_category_title,\n"
            + "scm_service_commodity.title as commodity,scm_product_packing.title as packing FROM \n"
            + "scm_product,scm_service_commodity,scm_product_packing,scm_product_packing_detail,scm_product_category \n"
            + "WHERE scm_product.company_id=? AND scm_product.commodity_id = scm_service_commodity.id\n"
            + "AND scm_product.product_packing_detail_id = scm_product_packing_detail.id\n"
            + "AND scm_product_packing_detail.pack_primary = scm_product_packing.id\n"
            + "AND scm_product.product_category_id = scm_product_category.id) as TAB ON dc_item.product_id = TAB.id,\n"
            + "fin_accounting_ledger,scm_customer,\n"
            + "scm_customer_address,scm_company_address\n"
            + "WHERE dc_item.debit_credit_note_id = deb_cred.id AND deb_cred.company_id =?\n"
            + "AND deb_cred.entry_date::date >=?::date AND deb_cred.entry_date::date <=?::date\n"
            + "AND deb_cred.accounting_ledger_id = fin_accounting_ledger.id AND fin_accounting_ledger.entity_id = scm_customer.id AND deb_cred.debit_credit_party = 2\n"
            + "AND scm_customer.id = scm_customer_address.customer_id AND scm_customer_address.address_type_id =1\n"
            + "AND deb_cred.company_id = scm_company_address.company_id AND scm_company_address.address_type_id =1\n"
            + "AND deb_cred.status_id = 2 ";
    params.add(company.getId());
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    if (filterParameters.getAccountGroup() != null) {
      debitCreditHSN += " AND deb_cred.account_group_id = ? ";
      params.add(filterParameters.getAccountGroup().getId());
    }
    String debitCreditGroupBy = " GROUP BY dc_item.product_id,TAB.product_category_title,UPPER(dc_item.title),dc_item.hsn_sac_code,UPPER(TAB.commodity),UPPER(TAB.packing),"
            + "deb_cred.invoice_type,is_sales_interstate,deb_cred.sez_zone ";
    String service1HSN = " SELECT UPPER(scm_service_commodity.hsn_sac_code) as hsn_code,null as product_category_title,\n"
            + "title as commodity_title,'' as product_packing_title, 0 as total_quantity,\n"
            + "SUM(COALESCE(freight_rate,0)+COALESCE(service_value_igst,0)+COALESCE(kerala_flood_cess_service1_value,0)) as total_value,\n"
            + "SUM(COALESCE(freight_rate,0)) as total_taxable_value,CASE WHEN is_sales_interstate=1 OR scm_sales_invoice.sez_zone = 1 THEN SUM(COALESCE(service_value_igst,0)) END as total_igst,\n"
            + "CASE WHEN is_sales_interstate=0 AND scm_sales_invoice.sez_zone = 0 THEN SUM(COALESCE(service_value_cgst,0)) END as total_cgst,\n"
            + "CASE WHEN is_sales_interstate=0 AND scm_sales_invoice.sez_zone = 0 THEN SUM(COALESCE(service_value_sgst,0)) END as total_sgst,is_sales_interstate,"
            + "COALESCE(scm_sales_invoice.kerala_flood_cess_service1_value,0) as kf_cess_value,'3' as invoice_or_return\n"
            + "from scm_sales_invoice,scm_service_commodity where scm_sales_invoice.services_id=scm_service_commodity.id and scm_sales_invoice.sales_invoice_status_id not in(0,1,-1) and\n"
            + "scm_sales_invoice.invoice_entry_date::date >= ?::date AND scm_sales_invoice.invoice_entry_date::date <= ?::date AND scm_sales_invoice.company_id = ?\n";
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    if (filterParameters.getAccountGroup() != null) {
      service1HSN += " AND scm_sales_invoice.account_group_id = ? ";
      params.add(filterParameters.getAccountGroup().getId());
    }
    String service1GroupBy = " GROUP BY scm_service_commodity.hsn_sac_code,product_category_title,total_quantity,commodity_title,product_packing_title ,"
            + "is_sales_interstate,scm_sales_invoice.sez_zone,invoice_or_return,kerala_flood_cess_service1_value ";
    String service2HSN = " SELECT UPPER(scm_service_commodity.hsn_sac_code) as hsn_code,null as product_category_title,\n"
            + "title as commodity_title,'' as product_packing_title, 0 as total_quantity,\n"
            + "SUM(COALESCE(service2_rate,0)+COALESCE(service2_igst,0)+COALESCE(kerala_flood_cess_service2_value,0)) as total_value,\n"
            + "SUM(COALESCE(service2_rate,0)) as total_taxable_value,CASE WHEN is_sales_interstate=1 OR scm_sales_invoice.sez_zone = 1 THEN SUM(COALESCE(service2_igst,0)) END as total_igst,\n"
            + "CASE WHEN is_sales_interstate=0 AND scm_sales_invoice.sez_zone = 0 THEN SUM(COALESCE(service2_cgst,0)) END as total_cgst,\n"
            + "CASE WHEN is_sales_interstate=0 AND scm_sales_invoice.sez_zone = 0 THEN SUM(COALESCE(service2_sgst,0)) END as total_sgst,is_sales_interstate,"
            + "COALESCE(scm_sales_invoice.kerala_flood_cess_service2_value,0) as kf_cess_value,'3' as invoice_or_return\n"
            + "from scm_sales_invoice,scm_service_commodity where scm_sales_invoice.services2_id=scm_service_commodity.id and scm_sales_invoice.sales_invoice_status_id not in(0,1,-1) and\n"
            + "scm_sales_invoice.invoice_entry_date::date >= ?::date AND scm_sales_invoice.invoice_entry_date::date <= ?::date AND scm_sales_invoice.company_id = ?\n";
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    if (filterParameters.getAccountGroup() != null) {
      service2HSN += " AND scm_sales_invoice.account_group_id = ? ";
      params.add(filterParameters.getAccountGroup().getId());
    }
    String service2GroupBy = " GROUP BY scm_service_commodity.hsn_sac_code,product_category_title,total_quantity,commodity_title,product_packing_title ,"
            + "is_sales_interstate,scm_sales_invoice.sez_zone,invoice_or_return,kerala_flood_cess_service2_value ";
    String sql = "SELECT UPPER(hsn_code) AS hsn_code,product_category_title,commodity_title,product_packing_title,SUM(total_quantity) AS total_quantity,SUM(total_value) AS total_value,\n"
            + "SUM(COALESCE(total_taxable_value,0)) AS total_taxable_value,SUM(COALESCE(total_igst,0)) AS total_igst,SUM(COALESCE(total_cgst,0)) AS total_cgst,SUM(COALESCE(total_sgst,0)) AS total_sgst,\n"
            + "SUM(COALESCE(kf_cess_value,0)) AS kf_cess_value,invoice_or_return\n"
            + "FROM ("
            + salesHSN + salesGroupBy + " UNION ALL " + returnHSN + returnGroupBy + " UNION ALL " + returnSplitHSN + returnSplitGroupBy + " UNION ALL "
            + debitCreditHSN + debitCreditGroupBy + " UNION ALL "
            + service1HSN + service1GroupBy + " UNION ALL "
            + service2HSN + service2GroupBy + ") AS TAB\n"
            + " GROUP BY hsn_code,commodity_title,product_category_title,product_packing_title,invoice_or_return ORDER BY UPPER(hsn_code)";

    return AppDb.getList(main.dbConnector(), GstHsnReport.class, sql, params.toArray());

  }

  public static List<Gst1Report> getSalesInvoiceDraftList(Main main, FilterParameters filterParameters, Company company) {
    String sql = "select id,invoice_no,invoice_entry_date as entry_date,invoice_amount, 'SALES' as type from scm_sales_invoice WHERE sales_invoice_status_id = 1 AND "
            + "invoice_entry_date::date >= ?::date AND invoice_entry_date::date <= ?::date AND company_id=?";

    return AppDb.getList(main.dbConnector(), Gst1Report.class,
            sql, new Object[]{SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()), company.getId()});
  }

  public static List<Gst1Report> getSalesReturnDraftList(Main main, FilterParameters filterParameters, Company company) {
    String sql = "select id,invoice_no,entry_date,invoice_amount, 'SALES_RETURN' as type from scm_sales_return WHERE sales_return_status_id = 1 AND\n"
            + "entry_date::date >= ?::date AND entry_date::date <= ?::date AND company_id=?";

    return AppDb.getList(main.dbConnector(), Gst1Report.class,
            sql, new Object[]{SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()), company.getId()});
  }

  public static List<Gst1Report> getDebitCreditNoteDraftList(Main main, FilterParameters filterParameters, Company company) {
    String sql = "select id,document_no as invoice_no,entry_date,net_value as invoice_amount, 'DEBIT_CREDIT' as type from fin_debit_credit_note WHERE status_id = 1 AND\n"
            + "entry_date::date >= ?::date AND entry_date::date <= ?::date AND company_id=?";

    return AppDb.getList(main.dbConnector(), Gst1Report.class,
            sql, new Object[]{SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()), company.getId()});
  }

  public static List<Gst1Report> getPurchaseReturnDraftList(Main main, FilterParameters filterParameters, Company company) {
    String sql = "select id,invoice_no,entry_date,invoice_amount, 'PURCHASE_RETURN' as type from scm_purchase_return WHERE purchase_return_status_id = 1 AND "
            + "entry_date::date >= ?::date AND entry_date::date <= ?::date AND company_id=?";

    return AppDb.getList(main.dbConnector(), Gst1Report.class,
            sql, new Object[]{SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()), company.getId()});
  }

  public static List<DocumentSummary> getDocumentSummaryList(Main main, FilterParameters filterParameters, Company company) {
    String sql = "SELECT * FROM getGSTDocumentSummary(?,?,?) ";
    return AppDb.getList(main.dbConnector(), DocumentSummary.class, sql, new Object[]{company.getId(),
      SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate())});
  }
}
