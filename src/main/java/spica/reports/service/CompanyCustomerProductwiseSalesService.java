/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;

import spica.reports.model.CompanyCustomerProductwiseSales;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 *
 * @author krishna.vm
 */
public class CompanyCustomerProductwiseSalesService {

  public static List<CompanyCustomerProductwiseSales> getCompanyCustomerProductwiseSalesList(Main main, Company company, FilterParameters filterParameters, List<Product> productList) {
    String sql = "SELECT scm_customer.customer_name as customer_name,scm_customer.gst_no as gst_no,scm_product.product_name as product_name,scm_product.hsn_code as hsn_code, \n"
            + "CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty+ COALESCE (scm_sales_invoice_item.product_qty_free,0) ELSE scm_sales_invoice_item.product_qty END as qty,\n"
            + "CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN 0 ELSE COALESCE (scm_sales_invoice_item.product_qty_free,0) END as qty_free, \n"
            + "scm_sales_invoice_item.value_sale as goods_value,\n"
            + "scm_sales_invoice_item.value_sale+scm_sales_invoice_item.value_igst as net_amount,scm_sales_invoice.invoice_entry_date,scm_sales_invoice.invoice_amount_igst as tax \n"
            + "FROM \n"
            + "scm_sales_invoice,scm_sales_invoice_item,scm_customer,scm_product_detail,scm_product_batch,scm_product_category,scm_product \n"
            + "WHERE scm_sales_invoice.sales_invoice_status_id = ? AND\n"
            + "scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
            + "AND scm_sales_invoice.customer_id = scm_customer.id\n"
            + "AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
            + "AND scm_product_batch.product_id = scm_product.id\n"
            + "AND scm_product.product_category_id = scm_product_category.id\n"
            + "AND scm_product.company_id =? ";

    ArrayList<Object> params = new ArrayList<>();
    if (main.getAppUser().isRoot() || AppService.exist(main, "select id from scm_user_account where user_id=? and account_id is null", new Object[]{main.getAppUser().getId()})) {
      params.add(SystemConstants.CONFIRMED);
      params.add(company.getId());
    } else {
      sql += " AND scm_sales_invoice.account_id IN(select account_id from scm_user_account where user_id=?)";
      params.add(SystemConstants.CONFIRMED);
      params.add(company.getId());
      params.add(main.getAppUser().getId());
    }
    if (filterParameters.getFromDate() != null) {
      sql += " AND scm_sales_invoice.invoice_entry_date >= ?::date";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    }
    if (filterParameters.getToDate() != null) {
      sql += " AND scm_sales_invoice.invoice_entry_date <= ?::date";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    }
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      sql += " AND scm_sales_invoice.account_group_id = ?";
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getAccount() != null && filterParameters.getAccount().getId() != null) {
      sql += " AND scm_product_detail.account_id = ?";
      params.add(filterParameters.getAccount().getId());
    }
    if (filterParameters.getCustomer() != null) {
      sql += " AND scm_sales_invoice.customer_id=? ";
      params.add(filterParameters.getCustomer().getId());
    }
    if (!StringUtil.isEmpty(productList)) {
      sql += " AND scm_product.id in(";
      int i = 1;
      for (Product list : productList) {
        sql += list.getId();
        if (i != productList.size()) {
          sql += ",";
        } else {
          sql += ")";
        }
        i++;
      }
    }
    return AppDb.getList(main.dbConnector(), CompanyCustomerProductwiseSales.class, sql, params.toArray());
  }

}
