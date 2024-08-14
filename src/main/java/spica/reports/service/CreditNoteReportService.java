/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import spica.reports.model.CreditNoteReport;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Company;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author Godson Joseph
 */
public class CreditNoteReportService {

  public static List<CreditNoteReport> getCreditNoteSalesReportList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "";
    ArrayList<Object> params = new ArrayList<>();
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

    sql = "SELECT\n"
            + "scm_sales_invoice.id,scm_customer.customer_name,scm_sales_invoice.invoice_no,scm_tax_code.rate_percentage,scm_product.product_name,\n"
            + "scm_product_entry_detail.margin_percentage,\n"
            + "scm_sales_invoice_item.prod_piece_selling_forced as rate,\n"
            + "scm_sales_invoice_item.product_hash,\n"
            + "SUM(scm_sales_invoice_item.product_qty) as qty,\n"
            + "SUM(COALESCE(scm_sales_invoice_item.product_qty_free,0)) as qty_free,\n"
            + "SUM(scm_sales_invoice_item.product_qty*scm_sales_invoice_item.prod_piece_selling_forced) as gross_value,\n"
            + "CASE WHEN SUM(COALESCE(scm_sales_invoice_item.product_qty_free,0))=0 THEN \n"
            + "SUM(((COALESCE(scm_sales_invoice_item.scheme_discount_derived,0)+COALESCE(scm_sales_invoice_item.product_discount_derived,0)+COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)+\n"
            + "COALESCE(scm_sales_invoice_item.cash_discount_value_derived,0))*scm_sales_invoice_item.product_qty))\n"
            + "ELSE \n"
            + "SUM(((COALESCE(scm_sales_invoice_item.product_discount_derived,0)+COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)+\n"
            + "COALESCE(scm_sales_invoice_item.cash_discount_value_derived,0))*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0))))\n"
            + "END  as disc_amount,\n"
            + "SUM(scm_sales_invoice_item.product_qty*scm_sales_invoice_item.prod_piece_selling_forced)-"
            + "CASE WHEN SUM(COALESCE(scm_sales_invoice_item.product_qty_free,0))=0 THEN \n"
            + "SUM(((COALESCE(scm_sales_invoice_item.scheme_discount_derived,0)+COALESCE(scm_sales_invoice_item.product_discount_derived,0)+COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)+\n"
            + "COALESCE(scm_sales_invoice_item.cash_discount_value_derived,0))*scm_sales_invoice_item.product_qty))\n"
            + "ELSE \n"
            + "SUM(((COALESCE(scm_sales_invoice_item.product_discount_derived,0)+COALESCE(scm_sales_invoice_item.invoice_discount_derived,0)+\n"
            + "COALESCE(scm_sales_invoice_item.cash_discount_value_derived,0))*(scm_sales_invoice_item.product_qty+COALESCE(scm_sales_invoice_item.product_qty_free,0))))\n"
            + "END as net_value,\n";
    if (filterParameters.getFilterOption() == 0) {
      sql += "            scm_product_entry_detail.value_rate_per_prod_piece_der as billing_sd,scm_product_entry_detail.value_rate_per_prod_piece_der\n";
    } else if (filterParameters.getFilterOption() == 1) {
      sql += "            scm_product_entry_detail.value_rate as billing_sd,scm_product_entry_detail.value_rate\n";
    } else if (filterParameters.getFilterOption() == 2) {
      sql += "            scm_sales_invoice_item.value_prod_piece_selling as billing_sd,scm_sales_invoice_item.value_prod_piece_selling\n";
    }
    sql += "-(scm_sales_invoice_item.value_prod_piece_sold +\n"
            + "(CASE WHEN SUM(COALESCE(scm_sales_invoice_item.product_qty_free,0))=0  THEN 0 ELSE COALESCE(scm_sales_invoice_item.scheme_discount_derived,0) END))as diff\n"
            + "\n"
            + "FROM\n"
            + "scm_sales_invoice,scm_sales_invoice_item,scm_product_entry_detail,scm_product_detail,scm_product_batch,scm_product,scm_tax_code,scm_customer\n"
            + "WHERE\n"
            + "scm_sales_invoice.id=scm_sales_invoice_item.sales_invoice_id\n"
            + "AND scm_sales_invoice_item.product_entry_detail_id=scm_product_entry_detail.id\n"
            + "AND scm_product_entry_detail.product_detail_id=scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id=scm_product_batch.id\n"
            + "AND scm_product_batch.product_id=scm_product.id\n"
            + "AND scm_tax_code.id=scm_sales_invoice_item.tax_code_id\n"
            + "AND scm_customer.id=scm_sales_invoice.customer_id\n"
            + "AND scm_sales_invoice.company_id=? \n"
            + "AND scm_sales_invoice.sales_invoice_status_id>=2\n"
            + "AND scm_sales_invoice.invoice_entry_date::date>=TO_DATE(?,'YYYY-MM-DD')  AND scm_sales_invoice.invoice_entry_date::date<=TO_DATE(?,'YYYY-MM-DD') \n";

    if (filterParameters.getAccountGroup() != null) {
      sql += "AND scm_sales_invoice.account_group_id=? \n";
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getAccount() != null) {
      sql += "AND scm_product_detail.account_id=? \n";
      params.add(filterParameters.getAccount().getId());
    }
    sql += "group by scm_sales_invoice.id,scm_customer.customer_name,scm_sales_invoice.invoice_no,scm_tax_code.rate_percentage,scm_product.product_name,\n"
            + "scm_sales_invoice_item.prod_piece_selling_forced,scm_product_entry_detail.margin_percentage ,scm_sales_invoice_item.product_hash,\n"
            + "scm_sales_invoice_item.value_prod_piece_sold,scm_sales_invoice_item.scheme_discount_derived,scm_sales_invoice_item.value_prod_piece_selling\n";
    if (filterParameters.getFilterOption() == 0) {
      sql += "            ,scm_product_entry_detail.value_rate_per_prod_piece_der\n";
    } else if (filterParameters.getFilterOption() == 1) {
      sql += "            ,scm_product_entry_detail.value_rate\n";
    }
    sql += "             ORDER BY 3,4,5 ";
    return AppDb.getList(main.dbConnector(), CreditNoteReport.class, sql, params.toArray());
  }

  public static List<CreditNoteReport> getCreditNoteReturnReportList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "";
    ArrayList<Object> params = new ArrayList<>();
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

    sql = "select scm_sales_return.id,scm_customer.customer_name,scm_sales_return.invoice_no,scm_tax_code.rate_percentage,scm_product.product_name,\n"
            + "-SUM(COALESCE(scm_sales_return_item.product_quantity,scm_sales_return_item.product_quantity_damaged) )as qty,\n"
            + "-SUM(COALESCE(scm_sales_return_item.product_quantity_free,0)) as qty_free,\n"
            + "scm_sales_return_item.value_rate as rate,\n"
            + "SUM(-COALESCE(scm_sales_return_item.product_quantity,scm_sales_return_item.product_quantity_damaged)*scm_sales_return_item.value_rate) as gross_value,\n"
            + "SUM(((CASE WHEN COALESCE(scm_sales_return_item.product_quantity_free,0)=0 THEN COALESCE(scm_sales_return_item.scheme_discount_value_derived,0) ELSE 0 END)\n"
            + "+COALESCE(scm_sales_return_item.product_discount_value_derived,0)+COALESCE(scm_sales_return_item.invoice_discount_value_derived,0)+\n"
            + "COALESCE(scm_sales_return_item.cash_discount_value_derived,0))*-COALESCE(scm_sales_return_item.product_quantity,scm_sales_return_item.product_quantity_damaged)) as disc_amount,\n"
            + "SUM((scm_sales_return_item.return_rate_per_piece+\n"
            + "(CASE WHEN COALESCE(scm_sales_return_item.product_quantity_free,0)=0 THEN 0 ELSE COALESCE(scm_sales_return_item.scheme_discount_value_derived,0) END))*\n"
            + "-COALESCE(scm_sales_return_item.product_quantity,scm_sales_return_item.product_quantity_damaged)) as net_value,\n"
            + "scm_product_entry_detail.margin_percentage,";
    if (filterParameters.getFilterOption() == 0) {
      sql += "            scm_product_entry_detail.value_rate_per_prod_piece_der as billing_sd,scm_product_entry_detail.value_rate_per_prod_piece_der\n";
    } else if (filterParameters.getFilterOption() == 1) {
      sql += "            scm_product_entry_detail.value_rate as billing_sd,scm_product_entry_detail.value_rate \n";
    } else if (filterParameters.getFilterOption() == 2) {
      sql += "            scm_sales_return_item.value_pts as billing_sd,scm_sales_return_item.value_pts\n";
    }
    sql += "-(scm_sales_return_item.return_rate_per_piece +\n"
            + "(CASE WHEN COALESCE(scm_sales_return_item.product_quantity_free,0)=0 THEN 0 ELSE COALESCE(scm_sales_return_item.scheme_discount_value_derived,0) END))as diff\n"
            + "from scm_sales_return,scm_sales_return_item,scm_product_entry_detail,scm_product_detail,scm_product_batch,scm_product,scm_tax_code,scm_customer\n"
            + "where scm_sales_return.id=scm_sales_return_item.sales_return_id\n"
            + "AND scm_sales_return_item.product_entry_detail_id=scm_product_entry_detail.id\n"
            + "AND scm_product_entry_detail.product_detail_id=scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id=scm_product_batch.id\n"
            + "AND scm_product_batch.product_id=scm_product.id\n"
            + "AND scm_tax_code.id=scm_sales_return_item.tax_code_id\n"
            + "AND scm_customer.id=scm_sales_return.customer_id\n"
            + "AND scm_sales_return.company_id=? "
            + "AND scm_sales_return.sales_return_status_id=2\n"
            + "AND scm_sales_return.entry_date::date>=TO_DATE(?,'YYYY-MM-DD')  AND scm_sales_return.entry_date::date<=TO_DATE(?,'YYYY-MM-DD')\n";

    if (filterParameters.getAccountGroup() != null) {
      sql += "AND scm_sales_return.account_group_id=? \n";
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getAccount() != null) {
      sql += "AND scm_product_detail.account_id=? \n";
      params.add(filterParameters.getAccount().getId());
    }
    sql += "group by scm_sales_return.id,scm_customer.customer_name,scm_sales_return.invoice_no,scm_tax_code.rate_percentage,scm_product.product_name,\n"
            + "scm_sales_return_item.value_rate,scm_product_entry_detail.margin_percentage ,scm_sales_return_item.product_quantity_free,\n"
            + "scm_sales_return_item.return_rate_per_piece,scm_sales_return_item.scheme_discount_value_derived,scm_sales_return_item.value_pts\n";
    if (filterParameters.getFilterOption() == 0) {
      sql += "            ,scm_product_entry_detail.value_rate_per_prod_piece_der\n";
    } else if (filterParameters.getFilterOption() == 1) {
      sql += "            ,scm_product_entry_detail.value_rate\n";
    }
    sql += "             ORDER BY 3,4 ";
    return AppDb.getList(main.dbConnector(), CreditNoteReport.class, sql, params.toArray());
  }
}
