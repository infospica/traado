/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.addon.service;

import java.util.List;
import spica.constant.ReportConstant;
import spica.addon.model.CalendarReport;
import spica.reports.model.FilterParameters;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author sujesh
 */
public abstract class CalendarReportService {

  public static List selectCalendarReportByType(Main main, Integer companyId, FilterParameters filterParameters) {
    if (filterParameters.getFilterType().equals(ReportConstant.SALES)) {
      return selectSalesInvoiceByDate(main, companyId, filterParameters);
    } else if (filterParameters.getFilterType().equals(ReportConstant.SALES_ORDER)) {
      return selectSalesOrderByDate(main, companyId, filterParameters);
    } else if (filterParameters.getFilterType().equals(ReportConstant.SALES_RETURN)) {
      return selectSalesReturnnByDate(main, companyId, filterParameters);
    } else if (filterParameters.getFilterType().equals(ReportConstant.PURCHASE_ENTRY)) {
      return selectPurchaseEntryByDate(main, companyId, filterParameters);
    } else if (filterParameters.getFilterType().equals(ReportConstant.PURCHASE_ORDER)) {
      return selectPurchaseOrderByDate(main, companyId, filterParameters);
    } else if (filterParameters.getFilterType().equals(ReportConstant.PURCHASE_RETURN)) {
      return selectPurchaseReturnByDate(main, companyId, filterParameters);
    } else if (filterParameters.getFilterType().equals(ReportConstant.SALES_SERVICE)) {
      return selectSalesServiceInvoiceByDate(main, companyId, filterParameters);
    }
    return null;
  }

  public static List selectSalesInvoiceByDate(Main main, Integer companyId, FilterParameters filterParameters) {
    String sql = "select scm_sales_invoice.id,scm_sales_invoice.invoice_no as invoice_number,scm_sales_invoice.sales_invoice_status_id as status,\n"
            + "scm_customer.customer_name as party_name, scm_sales_invoice.invoice_entry_date as date,scm_sales_invoice.invoice_amount_net as amount \n"
            + "from scm_sales_invoice scm_sales_invoice,scm_customer scm_customer\n"
            + "where scm_sales_invoice.invoice_entry_date::date >= ?::date  AND scm_sales_invoice.invoice_entry_date::date <= ?::date\n"
            + "AND scm_sales_invoice.company_id = ?\n"
            + "AND scm_sales_invoice.customer_id=scm_customer.id";
    return getCalendarData(main, companyId, filterParameters, sql);
  }

  public static List selectSalesOrderByDate(Main main, Integer companyId, FilterParameters filterParameters) {
    String sql = "select t1.id,t2.customer_name as party_name,t1.sales_order_no as invoice_no, t1.sales_order_status_id as status,t1.sales_order_date as date,t1.total_amount as amount\n"
            + "from scm_sales_order t1, scm_customer t2\n"
            + "WHERE t1.sales_order_date::date >= ?::date  AND t1.sales_order_date::date <= ? ::date\n"
            + "AND t1.company_id = ? \n"
            + "AND t1.customer_id = t2.id";
    return getCalendarData(main, companyId, filterParameters, sql);
  }

  public static List selectSalesReturnnByDate(Main main, Integer companyId, FilterParameters filterParameters) {
    String sql = "select scm_sales_return.id,scm_sales_return.invoice_no as invoice_number,scm_sales_return.sales_return_status_id as status,scm_customer.customer_name as party_name, \n"
            + "scm_sales_return.entry_date as date,scm_sales_return.invoice_amount_net as amount\n"
            + "from scm_sales_return scm_sales_return,scm_customer scm_customer\n"
            + "where scm_sales_return.entry_date::date >= ?::date  AND scm_sales_return.entry_date::date <= ? ::date\n"
            + "AND scm_sales_return.company_id = ?\n"
            + "AND scm_sales_return.customer_id=scm_customer.id";

    return getCalendarData(main, companyId, filterParameters, sql);
  }

  public static List selectPurchaseEntryByDate(Main main, Integer companyId, FilterParameters filterParameters) {
    String sql = " select t1.id, t1.invoice_no as invoice_number, t1.product_entry_status_id as status,t3.vendor_name as party_name,t1.product_entry_date as date , t1.invoice_amount_net as amount\n"
            + " from scm_product_entry t1 ,scm_account t2, scm_vendor t3 \n"
            + " WHERE t1.product_entry_date::date >= ?::date  AND t1.product_entry_date::date::date <= ? ::date \n"
            + " AND t1.company_id = ? AND t1.account_id = t2.id AND t2.vendor_id = t3.id";
    return getCalendarData(main, companyId, filterParameters, sql);
  }

  public static List selectPurchaseOrderByDate(Main main, Integer companyId, FilterParameters filterParameters) {
    String sql = "SELECT t1.id,t1.purchase_order_no as invoice_number ,t1.purchase_order_status_id as status,t3.vendor_name as party_name,t1.purchase_order_date as date  \n"
            + "FROM scm_purchase_order t1,scm_account t2,scm_vendor t3\n"
            + "WHERE t1.purchase_order_date::date >= ?::date  AND t1.purchase_order_date::date::date <= ? ::date\n"
            + "AND t1.account_id = t2.id AND t2.company_id = ? AND t2.vendor_id = t3.id";
    return getCalendarData(main, companyId, filterParameters, sql);
  }

  public static List selectPurchaseReturnByDate(Main main, Integer companyId, FilterParameters filterParameters) {
    String sql = "SELECT t1.id,t1.invoice_no as invoice_number ,t1.purchase_return_status_id as status,t3.vendor_name as party_name,t1.created_at as date ,t1.invoice_amount as amount \n"
            + "FROM scm_purchase_return t1,scm_account t2,scm_vendor t3\n"
            + "WHERE t1.created_at::date >= ?::date  AND t1.created_at::date::date <= ? ::date\n"
            + "AND t1.company_id = ? AND t1.account_id = t2.id  AND t2.vendor_id = t3.id";
    return getCalendarData(main, companyId, filterParameters, sql);
  }

  public static List selectSalesServiceInvoiceByDate(Main main, Integer companyId, FilterParameters filterParameters) {
    String sql = "select t1.id,t1.serial_no as invoice_number,t1.status_id as status,t2.title as party_name,t1.entry_date as date,t1.net_value as amount from \n"
            + "scm_sales_services_invoice t1\n"
            + "left join fin_accounting_ledger t2 on t2.id = t1.accounting_ledger_id\n"
            + "where t1.created_at::date >=?::date  AND t1.created_at::date::date <= ? ::date\n"
            + "AND t1.company_id = ?";
    return getCalendarData(main, companyId, filterParameters, sql);
  }

  public static List getCalendarData(Main main, Integer companyId, FilterParameters filterParameters, String sql) {
    main.clear();
    main.param(filterParameters.getFromDate());
    main.param(filterParameters.getToDate());
    main.param(companyId);
    return AppDb.getList(main.dbConnector(), CalendarReport.class, sql, main.getParamData().toArray());
  }

}
