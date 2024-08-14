/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import spica.constant.AccountingConstant;
import spica.reports.model.FilterParameters;
import spica.scm.common.ChequeReview;
import wawo.app.Main;
import spica.scm.common.Dashboard;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.entity.core.AppDb;

/**
 * SalesAgentClaimService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri May 19 11:03:21 IST 2017
 */
public abstract class CompanyDashboardService {

  public static List<Dashboard> getDashboardSalesList(Main main, Company company, FilterParameters filterParameters) {
    List<Object> params = new ArrayList<Object>();
    String sql = "select sum(t1.total_amount::float) amount,to_char(t1.created_at, 'MM/YYYY') as year_month,to_char(t1.created_at,'Mon/YY') as mon,\n"
            + "  to_char(t1.created_at,'MM') as monsort, to_char(t1.created_at,'YYYY') as yr,t1.company_id,to_char(t1.created_at,'dd') as day_of_month\n"
            + "from fin_accounting_transaction t1\n"
            + "left join scm_sales_invoice t2 on t2.id = t1.entity_id\n"
            + "where t1.created_at::date >=?::date and t1.created_at::date<=?::date and t1.company_id=? and t2.sales_invoice_status_id=? \n"
            + "AND t1.voucher_type_id =? AND t1.accounting_entity_type_id =? \n";

    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    params.add(SystemConstants.CONFIRMED);
    params.add(AccountingConstant.VOUCHER_TYPE_SALES.getId());
    params.add(AccountingConstant.ACC_ENTITY_SALES.getId());

    if (filterParameters.getAccountGroup() != null) {
      sql += "and t2.account_group_id = ? ";
      params.add(filterParameters.getAccountGroup().getId());
    }
//    if (filterParameters.getAccount() != null) {
//      sql += "and t1.account_id = ? ";
//      params.add(filterParameters.getAccount().getId());
//    }
    sql += " group by t1.created_at,t1.company_id order by yr,monsort,day_of_month asc";

    return AppDb.getList(main.dbConnector(), Dashboard.class, sql, params.toArray());
  }

  public static List<Dashboard> getDashboardPurchaseList(Main main, Company company, FilterParameters filterParameters) {
    List<Object> params = new ArrayList<Object>();
    String sql = "select sum(t1.total_amount::float) amount,to_char(t1.created_at, 'MM/YYYY') as year_month,to_char(t1.created_at,'Mon/YY') as mon,\n"
            + " to_char(t1.created_at,'MM') as monsort, to_char(t1.created_at,'YYYY') as yr,t1.company_id,to_char(t1.created_at,'dd') as day_of_month \n"
            + "from fin_accounting_transaction t1\n"
            + "left join scm_product_entry t2 on t2.id = t1.entity_id\n"
            + "where t1.created_at::date >=?::date and t1.created_at::date<=?::date and t1.company_id=? and t2.product_entry_status_id=? \n"
            + "AND t1.voucher_type_id = ? AND t1.accounting_entity_type_id = ? AND t2.opening_stock_entry=0\n";
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    params.add(SystemConstants.CONFIRMED);
    params.add(AccountingConstant.VOUCHER_TYPE_PURCHASE.getId());
    params.add(AccountingConstant.ACC_ENTITY_PURCHASE.getId());

    if (filterParameters.getAccountGroup() != null && filterParameters.getAccount() == null) {
      sql += "and t1.account_group_id = ? ";
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getAccount() != null) {
      sql += "and t2.account_id = ? ";
      params.add(filterParameters.getAccount().getId());
    }
    sql += "group by t1.created_at,t1.company_id order by yr,monsort,day_of_month asc";

    return AppDb.getList(main.dbConnector(), Dashboard.class, sql, params.toArray());
  }

  public static List<Dashboard> getDashboardSalesServiceList(Main main, Company company, FilterParameters filterParameters) {
    List<Object> params = new ArrayList<Object>();
    String sql = "select sum(t1.total_amount::float) amount,to_char(t1.created_at, 'MM/YYYY') as year_month,to_char(t1.created_at,'Mon/YY') as mon,\n"
            + " to_char(t1.created_at,'MM') as monsort, to_char(t1.created_at,'YYYY') as yr,t1.company_id,to_char(t1.created_at,'dd') as day_of_month \n"
            + "from fin_accounting_transaction t1\n"
            + "left join scm_sales_services_invoice t2 on t2.id = t1.entity_id\n"
            + "where t1.created_at::date >=?::date and t1.created_at::date<=?::date and t1.company_id=? and t2.status_id=?\n"
            + "AND t1.voucher_type_id = ? AND t1.accounting_entity_type_id = ? \n";

    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    params.add(company.getId());
    params.add(SystemConstants.CONFIRMED);
    params.add(AccountingConstant.VOUCHER_TYPE_SALES.getId());
    params.add(AccountingConstant.ACC_ENTITY_SALES_SERVICES_INVOICE.getId());
    if (filterParameters.getAccountGroup() != null) {
      sql += " AND t1.account_group_id = ? \n";
      params.add(filterParameters.getAccountGroup().getId());
    }
    sql += "group by t1.created_at,t1.company_id order by yr,monsort ,day_of_month asc";
    return AppDb.getList(main.dbConnector(), Dashboard.class, sql, params.toArray());
  }

  public static List<Dashboard> getDashboardSalesOutstanding(Main main, Company company, Account account) {
    String sql = "SELECT ROUND(SUM(TAB.outstandingAmount),2) Outstanding_Amount,TAB.daysOutSlab as Outstanding_Level FROM\n"
            + "(SELECT t1.value_remaining_balance as outstandingAmount,\n"
            + "NOW()::date-t1.entry_date::date as daysOutstanding,\n"
            + "CASE WHEN (NOW()::date-t1.entry_date::date)/30<=1 THEN 0  \n"
            + "WHEN (NOW()::date-t1.entry_date::date)/30<=2 THEN 1\n"
            + "WHEN (NOW()::date-t1.entry_date::date)/30<=3 THEN 2\n"
            + "WHEN (NOW()::date-t1.entry_date::date)/30<=4 THEN 3\n"
            + "ELSE 4 END daysOutSlab\n"
            + "FROM fin_trade_outstanding t1 WHERE t1.entity_type_id=7 AND t1.status =1\n"
            + "AND t1.company_id=? AND t1.account_id=?) AS TAB GROUP BY TAB.daysOutSlab";
    return AppDb.getList(main.dbConnector(), Dashboard.class, sql, new Object[]{company.getId(), account.getId()});
  }

  public static List<Dashboard> getDashboardPurchaseOutstanding(Main main, Company company, Account account) {
    String sql = "SELECT ROUND(SUM(TAB.outstandingAmount),2) Outstanding_Amount,TAB.daysOutSlab as Outstanding_Level FROM\n"
            + "(SELECT t1.value_remaining_balance as outstandingAmount,\n"
            + "NOW()::date-t1.entry_date::date as daysOutstanding,\n"
            + "CASE WHEN (NOW()::date-t1.entry_date::date)/30<=1 THEN 0  \n"
            + "WHEN (NOW()::date-t1.entry_date::date)/30<=2 THEN 1\n"
            + "WHEN (NOW()::date-t1.entry_date::date)/30<=3 THEN 2\n"
            + "WHEN (NOW()::date-t1.entry_date::date)/30<=4 THEN 3\n"
            + "ELSE 4 END daysOutSlab\n"
            + "FROM fin_trade_outstanding t1 WHERE t1.entity_type_id=8 AND t1.status =1\n"
            + "AND t1.company_id=? AND t1.account_id=?) AS TAB GROUP BY TAB.daysOutSlab";
    return AppDb.getList(main.dbConnector(), Dashboard.class, sql, new Object[]{company.getId(), account.getId()});
  }

  public static List<Dashboard> getDashboardTopSellingByQty(Main main, Company company, Account account) {
    String sql = "SELECT scm_product.id as product_id,scm_product.product_name as product_name,\n"
            + "SUM(CASE WHEN scm_sales_invoice_item.quantity_or_free =1 THEN scm_sales_invoice_item.product_qty + COALESCE (scm_sales_invoice_item.product_qty_free,0) \n"
            + "ELSE scm_sales_invoice_item.product_qty END)  as product_qty_sold \n"
            + "FROM scm_sales_invoice,scm_sales_invoice_item,scm_product_detail,scm_product_batch,scm_product,scm_product_category \n"
            + "WHERE scm_sales_invoice.sales_invoice_status_id=2 \n"
            + "AND scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id \n"
            + "AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id \n"
            + "AND scm_product_batch.product_id = scm_product.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
            + "AND scm_product.product_category_id = scm_product_category.id \n"
            + "AND scm_sales_invoice.company_id = ? \n"
            + "AND scm_sales_invoice.invoice_date <=date_trunc('month', '2017-10-20'::date) - interval '1 day'\n"
            + "AND scm_sales_invoice.invoice_date >= date_trunc('month', '2017-10-20'::date - interval '3' month)\n"
            + "GROUP BY scm_product.id,scm_product.product_name \n"
            + "ORDER BY product_qty_sold DESC FETCH FIRST 10 ROWS ONLY";
    return AppDb.getList(main.dbConnector(), Dashboard.class, sql, new Object[]{company.getId()});
  }

  public static List<Dashboard> getDashboardTopSellingByRate(Main main, Company company, Account account) {
    String sql = "SELECT scm_product.id as product_id,scm_product.product_name as product_name,\n"
            + "SUM(scm_sales_invoice_item.value_taxable)  as product_amount_sold \n"
            + "FROM scm_sales_invoice,scm_sales_invoice_item,scm_product_detail,scm_product_batch,scm_product,scm_product_category \n"
            + "WHERE scm_sales_invoice.sales_invoice_status_id=2 \n"
            + "AND scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id \n"
            + "AND scm_sales_invoice_item.product_detail_id = scm_product_detail.id \n"
            + "AND scm_product_batch.product_id = scm_product.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id\n"
            + "AND scm_product.product_category_id = scm_product_category.id \n"
            + "AND scm_sales_invoice.company_id = ?\n"
            + "AND scm_sales_invoice.invoice_date <=date_trunc('month', '2017-10-20'::date) - interval '1 day'\n"
            + "AND scm_sales_invoice.invoice_date >= date_trunc('month', '2017-10-20'::date - interval '3' month)\n"
            + "GROUP BY scm_product.id,scm_product.product_name \n"
            + "ORDER BY product_amount_sold DESC FETCH FIRST 10 ROWS ONLY";
    return AppDb.getList(main.dbConnector(), Dashboard.class, sql, new Object[]{company.getId()});
  }

  public static List<Dashboard> getTodaysPurchase(Main main, Company company, Account account, Date asOnDate) {
    String sql = "SELECT SUM(invoice_amount) as invoice_amount \n"
            + "FROM  scm_product_entry \n"
            + "WHERE product_entry_status_id=2 AND opening_stock_entry = 0 AND \n"
            + "company_id = ?  AND product_entry_date =?";
    return AppDb.getList(main.dbConnector(), Dashboard.class, sql, new Object[]{company.getId(), asOnDate});
  }

  public static Dashboard getSalesBox(Main main, FilterParameters filterParameters) {
    String sql = "SELECT tab.todaysSalesValue as todaysAmount,tab2.weeklySalesValue as weeklyAmount,tab3.monthlySalesValue as monthlyAmount from \n"
            + "(select COALESCE (sum(total_amount),0) as todaysSalesValue from fin_accounting_transaction where created_at::date =?::date and company_id = ? and voucher_type_id = ? AND accounting_entity_type_id =?) as tab,\n"
            + "(select COALESCE (sum(total_amount),0) as weeklySalesValue from fin_accounting_transaction where created_at::date <=?::date and created_at::date >=?::date and company_id =? and voucher_type_id =? AND accounting_entity_type_id =? ) as tab2,\n"
            + "(select COALESCE (sum(total_amount),0) as monthlySalesValue from fin_accounting_transaction where created_at::date <=?::date and created_at::date >=?::date and company_id = ? and voucher_type_id = ? AND accounting_entity_type_id =? ) as tab3";
    Integer companyId = UserRuntimeView.instance().getCompany().getId();

    ArrayList<Object> params = new ArrayList();
    params.add(filterParameters.getToday());
    params.add(companyId);
    params.add(AccountingConstant.VOUCHER_TYPE_SALES.getId());
    params.add(AccountingConstant.ACC_ENTITY_SALES.getId());

    params.add(filterParameters.getToday());
    params.add(filterParameters.getFirstDayOfWeek());
    params.add(companyId);
    params.add(AccountingConstant.VOUCHER_TYPE_SALES.getId());
    params.add(AccountingConstant.ACC_ENTITY_SALES.getId());

    params.add(filterParameters.getToday());
    params.add(filterParameters.getFirstDayOfMonth());
    params.add(companyId);
    params.add(AccountingConstant.VOUCHER_TYPE_SALES.getId());
    params.add(AccountingConstant.ACC_ENTITY_SALES.getId());

    return (Dashboard) AppDb.single(main.dbConnector(), Dashboard.class, sql, params.toArray());

  }

  public static Dashboard getPurchaseBox(Main main, FilterParameters filterParameters) {
    String sql = "SELECT tab.todaysPurchaseValue as todaysAmount,tab2.weeklyPurchaseValue as weeklyAmount,tab3.monthlyPurchaseValue as monthlyAmount from \n"
            + "(select COALESCE (sum(total_amount),0) as todaysPurchaseValue from fin_accounting_transaction where created_at::date =?::date and company_id = ? and voucher_type_id = ? AND accounting_entity_type_id =?) as tab,\n"
            + "(select COALESCE (sum(total_amount),0) as weeklyPurchaseValue from fin_accounting_transaction where created_at::date <=?::date and created_at::date >=?::date and company_id =? and voucher_type_id =? AND accounting_entity_type_id =? ) as tab2,\n"
            + "(select COALESCE (sum(total_amount),0) as monthlyPurchaseValue from fin_accounting_transaction where created_at::date <=?::date and created_at::date >=?::date and company_id = ? and voucher_type_id = ? AND accounting_entity_type_id =? ) as tab3";
    Integer companyId = UserRuntimeView.instance().getCompany().getId();

    ArrayList<Object> params = new ArrayList();
    params.add(filterParameters.getToday());
    params.add(companyId);
    params.add(AccountingConstant.VOUCHER_TYPE_PURCHASE.getId());
    params.add(AccountingConstant.ACC_ENTITY_PURCHASE.getId());

    params.add(filterParameters.getToday());
    params.add(filterParameters.getFirstDayOfWeek());
    params.add(companyId);
    params.add(AccountingConstant.VOUCHER_TYPE_PURCHASE.getId());
    params.add(AccountingConstant.ACC_ENTITY_PURCHASE.getId());

    params.add(filterParameters.getToday());
    params.add(filterParameters.getFirstDayOfMonth());
    params.add(companyId);
    params.add(AccountingConstant.VOUCHER_TYPE_PURCHASE.getId());
    params.add(AccountingConstant.ACC_ENTITY_PURCHASE.getId());

    return (Dashboard) AppDb.single(main.dbConnector(), Dashboard.class, sql, params.toArray());

  }

  public static Dashboard getOutstandingBox(Main main, FilterParameters filterParameters) {

    String sql = "SELECT tab.outstandingAmount as todaysAmount,tab2.weeklyOutstandingAmount as weeklyAmount,tab3.monthlyOutstandingAmount as monthlyAmount from \n"
            + "(select COALESCE (sum(value_remaining_balance),0) as outstandingAmount from fin_trade_outstanding where entry_date::date = ?::date and company_id = ? ) as tab,\n"
            + "(select COALESCE (sum(value_remaining_balance),0) as weeklyOutstandingAmount from fin_trade_outstanding where entry_date::date <= ?::date and entry_date::date >= ?::date and company_id = ? ) as tab2,\n"
            + "(select COALESCE (sum(value_remaining_balance),0) as monthlyOutstandingAmount from fin_trade_outstanding where entry_date::date <= ?::date and entry_date::date >= ?::date and company_id = ? ) as tab3";
    Integer companyId = UserRuntimeView.instance().getCompany().getId();

    ArrayList<Object> params = new ArrayList();
    params.add(filterParameters.getToday());
    params.add(companyId);
    params.add(filterParameters.getToday());
    params.add(filterParameters.getFirstDayOfWeek());
    params.add(companyId);
    params.add(filterParameters.getToday());
    params.add(filterParameters.getFirstDayOfMonth());
    params.add(companyId);

    return (Dashboard) AppDb.single(main.dbConnector(), Dashboard.class, sql, params.toArray());

  }

  public static List<Dashboard> selectTopOutstanding(Main main, FilterParameters filterParameters, int limit, Integer entityType) {
    String sql = "";
    if (Objects.equals(entityType, AccountingConstant.ACC_ENTITY_CUSTOMER.getId())) {
      sql = " select  SUM(coalesce(tab.receivable,0)-coalesce(tab.payable,0)) as outstanding_amount";
    } else if (Objects.equals(entityType, AccountingConstant.ACC_ENTITY_VENDOR.getId())) {
      sql = " select SUM(coalesce(tab.payable,0)-coalesce(tab.receivable,0)) as outstanding_amount";
    }
    sql += ", tab.name,tab.id from\n"
            + "(select T3.id, T3.title as name,case when T1.record_type=2 then SUM(T1.balance_amount) END as receivable,\n"
            + "case when T1.record_type=1 then SUM(T1.balance_amount) END as payable from fin_accounting_transaction_detail_item T1 \n"
            + "left join fin_accounting_transaction_detail T2 on T2.id=T1.accounting_transaction_detail_id\n"
            + "left join fin_accounting_ledger T3 on T3.id=T2.accounting_ledger_id\n"
            + "left join fin_accounting_transaction T4 on T4.id = T2.accounting_transaction_id\n"
            + "where T3.company_id=? and T3.accounting_entity_type_id=? \n";

    ArrayList<Object> params = new ArrayList<>();
    params.add(UserRuntimeView.instance().getCompany().getId());
    params.add(entityType);

    String group = "group by T3.id, T3.title,T1.record_type) tab group by tab.id, tab.name order by outstanding_amount desc limit ?";
    params.add(limit);
    sql += group;
    return AppDb.getList(main.dbConnector(), Dashboard.class, sql, params.toArray());
  }

  public static ChequeReview selectChequeBoxItems(Main main, Company company, Integer inOrOut) {
    String sql = "select T1.cheque_today,T2.cheque_over_due,T3.undatedCheque,T4.cheque_receipt from \n"
            + "(select count(id) as cheque_today from fin_cheque_entry  where status_id in(5,9) and cheque_date::date = ?::date and company_id = ? and in_or_out=?)as T1,\n"
            + "(select count(id) as cheque_over_due from fin_cheque_entry  where status_id in(5,9) and cheque_date::date < ?::date  and company_id = ? and in_or_out=?) as T2,\n"
            + "(select count(id) as undatedCheque from fin_cheque_entry  where status_id in(5,9) and cheque_date is null  and company_id = ? and in_or_out=? ) as T3,\n"
            + "(select count(t1.id) as cheque_receipt from fin_cheque_entry t1,fin_accounting_transaction_detail_item t2,fin_accounting_ledger t3,scm_bank t4\n"
            + "where t1.status_id = 6 and t2.cheque_entry_id = t1.id  AND t1.company_id = ?  and t1.in_or_out=? AND t2.created_at::date = ?::date  AND t1.ledger_id = t3.id AND t2.document_type_id=16 \n"
            + "AND t1.bank_id = t4.id ) as T4";
    main.clear();
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()));
    main.param(company.getId());
    main.param(inOrOut);
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()));
    main.param(company.getId());
    main.param(inOrOut);
    main.param(company.getId());
    main.param(inOrOut);
    main.param(company.getId());
    main.param(inOrOut);
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()));
    return (ChequeReview) AppDb.single(main.dbConnector(), ChequeReview.class, sql, main.getParamData().toArray());
  }

  public static Dashboard getReceiptBox(Main main, FilterParameters filterParameters) {
    return null;
  }

}
