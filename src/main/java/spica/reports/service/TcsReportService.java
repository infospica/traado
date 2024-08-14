/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import spica.reports.model.CompanyCustomerSales;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Company;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author godson
 */
public class TcsReportService {

  public static List<CompanyCustomerSales> getTcsReportList(Main main, FilterParameters filterParameters, Company company) {
    String sql = "select scm_sales_invoice.id,scm_customer.customer_name,scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_entry_date as entry_date,\n"
            + "scm_customer.gst_no,scm_customer.pan_no,scm_sales_invoice.invoice_amount,\n"
            + "scm_sales_invoice.invoice_amount_subtotal,scm_sales_invoice.tcs_net_value as tcs_net_amount from scm_sales_invoice,scm_customer\n"
            + "where scm_sales_invoice.customer_id =scm_customer.id\n"
            + "and scm_sales_invoice.sales_invoice_status_id >=2\n"
            + "and scm_sales_invoice.tcs_net_value is not null\n"
            + "and scm_sales_invoice.company_id=?\n"
            + "and scm_sales_invoice.invoice_entry_date::date >=TO_DATE(?,'yyyy-mm-dd')\n"
            + "and scm_sales_invoice.invoice_entry_date::date <=TO_DATE(?,'yyyy-mm-dd') \n";
    List<Object> params = new ArrayList<Object>();
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    if (filterParameters != null & filterParameters.getAccountGroup() != null) {
      sql += " and scm_sales_invoice.account_group_id=? ";
      params.add(filterParameters.getAccountGroup().getId());
    }
    sql += " ORDER BY customer_name,invoice_entry_date,invoice_no";

    return AppDb.getList(main.dbConnector(), CompanyCustomerSales.class, sql, params.toArray());
  }

}
