/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.lang.Integer;
import java.util.logging.Logger;
import spica.scm.domain.StockAndSales;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Account;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author fify
 */
public class StockAndSalesService {

  static Logger logger = Logger.getLogger(StockAndSalesService.class.getName());

  public static List<StockAndSales> getStockAndSalesList(Main main, AccountGroup accountGroup, Account account, Date fromDate, Date toDate, String reportType, boolean includeZeroQty, boolean includeDamaged, boolean includeSaleable) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(toDate);

    /*getStockAndSales(IN compid integer,IN accGroupid integer,IN accid integer,IN prodid integer,IN fromDate varchar,IN toDate varchar )*/
    Integer accGroupId = null;
    Integer accId = null;
    if (accountGroup != null) {
      accGroupId = accountGroup.getId();
    }
    if (account != null) {
      accId = account.getId();
    }
    if ("3".equals(reportType) || "4".equals(reportType)) {
      fromDate = UserRuntimeView.instance().getCompany().getCurrentFinancialYear().getStartDate();
    }
    String startDate = SystemRuntimeConfig.SDF_YYYY_MM_DD.format(fromDate);
    String endDate = SystemRuntimeConfig.SDF_YYYY_MM_DD.format(toDate);

    String sql = "SELECT * FROM getStockAndSales(?,?,?,NULL,?,?) ORDER BY UPPER(product_name)";
    if ("1".equals(reportType)) {
      sql = " SELECT prod_id,product_name,prod_packing,SUM(COALESCE(qty_available,0)) as qty_available,SUM(COALESCE(op_stock_value,0)) as op_stock_value,"
              + " SUM(COALESCE(open_entry_qty,0)) as open_entry_qty,SUM(COALESCE(open_entry_value,0)) as open_entry_value,SUM((COALESCE(current_qty,0)*ave_rate))/"
              + " CASE WHEN SUM(COALESCE(current_qty,0))=0 THEN 1 ELSE SUM(COALESCE(current_qty,0)) END as ave_rate,"
              + " SUM(COALESCE(purchase_qty,0)) as purchase_qty,SUM(COALESCE(purchase_value,0)) as purchase_value,"
              + " SUM(COALESCE(purchase_return_qty,0)) as purchase_return_qty,SUM(COALESCE(purchase_return_value,0)) as purchase_return_value,"
              + " SUM(COALESCE(secondlast_month_sale_qty,0)) as secondlast_month_sale_qty,SUM(COALESCE(secondlast_month_sale_value,0)) as secondlast_month_sale_value,"
              + " SUM(COALESCE(last_month_sale_qty,0)) as last_month_sale_qty,SUM(COALESCE(last_month_sale_value,0)) as last_month_sale_value,"
              + " SUM(COALESCE(sales_qty,0)) as sales_qty,SUM(COALESCE(sales_value,0)) as sales_value,"
              + " SUM(COALESCE(sales_return_qty,0)) as sales_return_qty,SUM(COALESCE(sales_return_value,0)) as sales_return_value,"
              + " SUM(COALESCE(sales_purch_value,0)) as sales_purch_value,SUM(COALESCE(sales_return_purch_value,0)) as sales_return_purch_value,"
              + " SUM(COALESCE(accgrp_sales_qty,0)) as accgrp_sales_qty,SUM(COALESCE(accgrp_sales_value,0)) as accgrp_sales_value,"
              + " SUM(COALESCE(accgrp_sales_return_qty,0)) as accgrp_sales_return_qty,SUM(COALESCE(accgrp_sales_return_value,0)) as accgrp_sales_return_value,"
              + " SUM(COALESCE(accgrp_sales_purch_value,0)) as accgrp_sales_purch_value,SUM(COALESCE(accgrp_sales_return_purch_value,0)) as accgrp_sales_return_purch_value,"
              + " SUM(COALESCE(stock_adjust_qty,0)) as stock_adjust_qty,SUM(COALESCE(stock_adjust_value,0)) as stock_adjust_value,"
              + " SUM(COALESCE(current_qty,0)) as current_qty,SUM(COALESCE(tot_value,0)) as tot_value,"
              + " SUM(COALESCE(sales_value,0)-COALESCE(sales_return_value,0)) as net_sales "
              + " FROM getStockAndSales(?,?,?,NULL,?,?) GROUP BY prod_id,product_name,prod_packing ORDER BY UPPER(product_name)";
    } else if ("3".equals(reportType)) {
      sql = "SELECT * FROM getStockAuditReport(?,?,?,NULL,?,?) \n";
      if (includeSaleable) {
        if (!includeZeroQty && !includeDamaged) {
          sql += " where (COALESCE(current_qty,0))>0";
        } else if (!includeZeroQty && includeDamaged) {
          sql += " where (COALESCE(current_qty,0)+COALESCE(current_dam_qty,0))>0";
        }
      } else {
        if (!includeZeroQty && !includeDamaged) {
          sql += " where (COALESCE(current_qty,0))<0";
        } else if (!includeZeroQty && includeDamaged) {
          sql += " where (COALESCE(current_dam_qty,0))>0";
        }
      }

      sql += " ORDER BY UPPER(product_name) ";
    } else if ("4".equals(reportType)) {
      sql = "SELECT prod_id,product_name,hsn_code,packing_desc,prod_packing,prod_mrp_value,SUM(qty_available) as qty_available,SUM(qty_free_available) as qty_free_available,\n"
              + "SUM(current_qty) as current_qty,SUM(tot_value) as tot_value,SUM(COALESCE(current_dam_qty,0)) as  current_dam_qty,"
              + "SUM(COALESCE(tot_dam_value)) as tot_dam_value \n"
              + " FROM getStockAuditReport(?,?,?,NULL,?,?) ";
      if (includeSaleable) {
        if (!includeZeroQty && !includeDamaged) {
          sql += " where (COALESCE(current_qty,0))>0";
        } else if (!includeZeroQty && includeDamaged) {
          sql += " where (COALESCE(current_qty,0)+COALESCE(current_dam_qty,0))>0";
        }
      } else {
        if (!includeZeroQty && !includeDamaged) {
          sql += " where (COALESCE(current_qty,0))<0";
        } else if (!includeZeroQty && includeDamaged) {
          sql += " where (COALESCE(current_dam_qty,0))>0";
        }
      }
      sql += " group by prod_id,product_name,hsn_code,packing_desc,prod_packing,prod_mrp_value\n"
              + " ORDER BY UPPER(product_name) ";
    }

    return AppDb.getList(main.dbConnector(), StockAndSales.class, sql, new Object[]{UserRuntimeView.instance().getCompany().getId(), accGroupId, accId, startDate, endDate});
  }
}
