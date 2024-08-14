/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.List;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Account;
import spica.scm.domain.Company;

import spica.scm.domain.PurchaseLedger;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author fify
 */
public class PurchaseLedgerService {

  public static List<PurchaseLedger> getPurchaseLedgerList(Main main, Company company, FilterParameters filterParameters, boolean includeReturn, boolean damageExpiry) {
    ArrayList<Object> params = new ArrayList<>();
    // String sql = "SELECT ROW_NUMBER () OVER (ORDER BY entry_date),* FROM getPurchaseLedgerForProduct(?,?,?,?,?,?)";
    String sql = "SELECT scm_vendor.vendor_name as supplier_name,scm_product_entry.id,\n"
            + "scm_product_entry.invoice_no as invoice_no,scm_product_entry.invoice_date as invoice_date,\n"
            + "scm_product.product_name as product_name,scm_product_category.hsn_code as hsn_code, \n"
            + "scm_product_entry.reference_no reference_no,\n"
            + "scm_product_batch.batch_no as batch_no,getPackDimension(scm_product.id) as pack_type,\n"
            + "scm_product_batch.expiry_date_actual as expiry_date,scm_product_entry_detail.value_mrp as value_mrp,\n"
            + "(CASE WHEN scm_product_entry.quantity_or_free = 1 THEN (scm_product_entry_detail.product_quantity+COALESCE(scm_product_entry_detail.product_quantity_free,0)) ELSE scm_product_entry_detail.product_quantity END) as purchase_qty, \n"
            + "scm_product_entry_detail.value_rate_per_prod_piece_der as purchase_rate,											\n"
            + "scm_product_entry_detail.value_goods as goods_value,scm_product_entry_detail.scheme_discount_value as scheme_discount,\n"
            + "scm_product_entry_detail.discount_value as product_discount,scm_product_entry_detail.invoice_discount_value as invoice_discount,\n"
            + "(scm_product_entry_detail.value_rate_per_prod_piece_der*(scm_product_entry_detail.product_quantity +COALESCE(scm_product_entry_detail.product_quantity_free,0))) as taxable_value,\n"
            + "getTaxPercentage(scm_product_entry_detail.tax_code_id) as tax_percentage,scm_product_entry_detail.value_cgst,scm_product_entry_detail.value_sgst,\n"
            + "CASE WHEN scm_product_entry_detail.value_sgst is null THEN scm_product_entry_detail.value_igst ELSE NULL  END as value_igst,\n"
            + "scm_product_entry_detail.value_igst as tax_value,		  \n"
            + "((scm_product_entry_detail.value_rate_per_prod_piece_der*(scm_product_entry_detail.product_quantity +COALESCE(scm_product_entry_detail.product_quantity_free,0)))+scm_product_entry_detail.value_igst) as bill_amount,1 as purchase_or_return,\n"
            + " '' as return_type\n"
            + "FROM scm_product_entry_detail,scm_product_entry,scm_account,scm_vendor,scm_product_detail,\n"
            + "scm_product,scm_product_category,scm_product_batch \n"
            + "WHERE scm_product_entry_detail.product_entry_id = scm_product_entry.id AND \n"
            + "scm_product_entry.product_entry_status_id >=2  AND  scm_product_entry.opening_stock_entry!=2 AND \n"
            + "scm_product_entry.account_id = scm_account.id AND scm_account.vendor_id =  scm_vendor.id AND \n"
            + "scm_product_entry_detail.product_detail_id = scm_product_detail.id  AND \n"
            + "scm_product_detail.product_batch_id = scm_product_batch.id  AND \n"
            + "scm_product_batch.product_id = scm_product.id  AND scm_product.product_category_id = scm_product_category.id AND \n"
            + "scm_product_entry.company_id = ? AND	\n"
            + "scm_product_entry.product_entry_date >= to_date(?, 'YYYY-MM-DD') AND \n"
            + "scm_product_entry.product_entry_date <= to_date(?, 'YYYY-MM-DD') \n";

    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    String cond = "";
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccount() == null) {
      cond += "AND scm_product_entry.account_id in(select account_id from scm_account_group_detail where account_group_id = ?) \n";
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getAccount() != null) {
      cond += "AND scm_product_entry.account_id = ? ";
      params.add(filterParameters.getAccount().getId());
    }
    String returnSql = "";
    String returnCond = "";
    if (includeReturn) {
      returnSql += " UNION \n"
              + "SELECT scm_vendor.vendor_name as supplier_name,scm_purchase_return.id,\n"
              + "scm_purchase_return.invoice_no as invoice_no,scm_purchase_return.invoice_date as invoice_date,\n"
              + "scm_product.product_name as product_name,scm_product_category.hsn_code as hsn_code,\n"
              + "scm_purchase_return.reference_no AS reference_no,\n"
              + "scm_product_batch.batch_no as batch_no,getPackDimension(scm_product.id) as pack_type,\n"
              + "scm_product_batch.expiry_date_actual as expiry_date,scm_purchase_return_item.value_mrp as value_mrp,\n"
              + "-scm_purchase_return_item.quantity_returned as purchase_qty, \n"
              + "-scm_purchase_return_item.landing_price_per_piece_company as purchase_rate,											\n"
              + "-scm_purchase_return_item.value_goods as goods_value,-scm_purchase_return_item.scheme_discount_value as scheme_discount,\n"
              + "-scm_purchase_return_item.product_discount_value as product_discount,-scm_purchase_return_item.invoice_discount_value as invoice_discount,\n"
              + "-(scm_purchase_return_item.value_net-scm_purchase_return_item.value_igst) as taxable_value,getTaxPercentage(scm_purchase_return_item.tax_code_id) as tax_percentage,\n"
              + "-scm_purchase_return_item.value_cgst as value_cgst ,-scm_purchase_return_item.value_sgst as value_sgst,\n"
              + "CASE WHEN scm_purchase_return_item.value_cgst is null THEN -scm_purchase_return_item.value_igst ELSE null END as value_igst,	\n"
              + "-scm_purchase_return_item.value_igst as tax_value,		  \n"
              + "-(scm_purchase_return_item.value_net) as bill_amount,2 as purchase_or_return,\n"
              + "CASE WHEN scm_purchase_return.purchase_return_stock_cat = 1 THEN 'DAMAGED & EXPIRED' ELSE 'NON-MOVING & NEAR EXPIRY' END as return_type\n"
              + "FROM scm_purchase_return_item,scm_purchase_return,scm_account,scm_vendor,scm_product_detail,\n"
              + "scm_product,scm_product_category,scm_product_batch \n"
              + "WHERE scm_purchase_return_item.purchase_return_id = scm_purchase_return.id AND \n"
              + "scm_purchase_return.purchase_return_status_id >=2  AND \n"
              + "scm_purchase_return.account_id = scm_account.id AND scm_account.vendor_id =  scm_vendor.id AND \n"
              + "scm_purchase_return_item.product_detail_id = scm_product_detail.id  AND \n"
              + "scm_product_detail.product_batch_id = scm_product_batch.id  AND \n"
              + "scm_product_batch.product_id = scm_product.id  AND scm_product.product_category_id = scm_product_category.id AND \n"
              + "scm_purchase_return.company_id = ? AND	\n"
              + "scm_purchase_return.entry_date >= to_date(?, 'YYYY-MM-DD') AND \n"
              + "scm_purchase_return.entry_date <= to_date(?, 'YYYY-MM-DD') \n";
      params.add(company.getId());
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

      if (filterParameters.getAccountGroup() != null && filterParameters.getAccount() == null) {
        returnCond += " AND scm_purchase_return.account_id in(select account_id from scm_account_group_detail where account_group_id = ?) \n";
        params.add(filterParameters.getAccountGroup().getId());
      }
      if (filterParameters.getAccount() != null) {
        returnCond += " AND scm_purchase_return.account_id = ?";
        params.add(filterParameters.getAccount().getId());
      }
      if (!damageExpiry) {
        returnCond += " AND scm_purchase_return.purchase_return_stock_cat=2";
      }
    }
    String order = " order by product_name";
    sql = sql + cond + returnSql + returnCond + order;
    return AppDb.getList(main.dbConnector(), PurchaseLedger.class, sql, params.toArray());
  }

  public static List<PurchaseLedger> getProductEntrySqlPaged(Main main, Company company, FilterParameters filterParameters, boolean includeReturn, boolean damageExpiry) {
    ArrayList<Object> params = new ArrayList<>();
    String sql = "select scm_vendor.vendor_name as supplier_name,scm_product_entry.id,scm_product_entry.account_id,scm_product_entry.invoice_no,scm_product_entry.account_invoice_no,\n"
            + "scm_product_entry.invoice_date,scm_product_entry.product_entry_date as entry_date,\n"
            + "scm_product_entry.reference_no AS reference_no,scm_product_entry.account_invoice_no,\n"
            + "scm_product_entry.invoice_amount as bill_amount,scm_product_entry.invoice_amount_goods as goods_value,SUM(COALESCE(t1.scheme_discount_value,0)) as  scheme_discount,\n"
            + "SUM(COALESCE(t1.discount_value,0)) as  product_discount,SUM(COALESCE(t1.invoice_discount_value,0)) as  invoice_discount,\n"
            + "scm_product_entry.invoice_amount_assessable as taxable_value,\n"
            + "scm_product_entry.invoice_amount_cgst as value_cgst,scm_product_entry.invoice_amount_sgst as value_sgst,\n"
            + "CASE WHEN scm_product_entry.invoice_amount_cgst is null THEN scm_product_entry.invoice_amount_igst ELSE null END  as value_igst,\n"
            + "scm_product_entry.invoice_amount_igst as tax_value,\n"
            + "1 as purchase_or_return,\n"
            + " '' as return_type\n"
            + "from scm_product_entry scm_product_entry,scm_account,scm_vendor,scm_product_entry_detail t1    \n"
            + "where scm_product_entry.company_id = ? and t1.product_entry_id=scm_product_entry.id and\n"
            + "scm_account.id = scm_product_entry.account_id and scm_account.vendor_id=scm_vendor.id \n"
            + "and scm_product_entry.opening_stock_entry=0 and scm_product_entry.product_entry_status_id= 2  \n"
            + "AND scm_product_entry.product_entry_date ::date >= ? ::date AND scm_product_entry.product_entry_date ::date <= ? ::date\n";
    String groupBy = "group by supplier_name,scm_product_entry.id,scm_product_entry.account_id,scm_product_entry.invoice_no,scm_product_entry.account_invoice_no,\n"
            + "scm_product_entry.invoice_date,scm_product_entry.product_entry_date,bill_amount,goods_value,taxable_value,scm_product_entry.invoice_amount_cgst,\n"
            + "scm_product_entry.invoice_amount_sgst,scm_product_entry.invoice_amount_igst, purchase_or_return,return_type,scm_product_entry.account_invoice_no,scm_product_entry.reference_no";
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    String cond = "";
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccount() == null) {
      cond += " AND scm_product_entry.account_id in(select account_id from scm_account_group_detail where account_group_id = ?) \n";
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getAccount() != null) {
      cond += " AND scm_product_entry.account_id = ? ";
      params.add(filterParameters.getAccount().getId());
    }
    String returnSql = "";
    String returnCond = "";
    String returnGroupBY = "";
    if (includeReturn) {
      returnSql += " UNION\n "
              + "select scm_vendor.vendor_name as supplier_name,scm_purchase_return.id,scm_purchase_return.account_id,scm_purchase_return.invoice_no,'' as account_invoice_no,\n"
              + "scm_purchase_return.invoice_date,scm_purchase_return.entry_date,\n"
              + "scm_purchase_return.reference_no AS reference_no,'' as account_invoice_no,\n"
              + "-scm_purchase_return.invoice_amount as bill_amount,SUM(value_goods) as goods_value,"
              + "SUM(COALESCE(t1.scheme_discount_value,0)) as scheme_discount,\n"
              + "SUM(COALESCE(t1.product_discount_value,0)) as product_discount,SUM(COALESCE(t1.invoice_discount_value,0)) as invoice_discount,"
              + "-scm_purchase_return.invoice_amount_net as taxable_value,\n"
              + "-scm_purchase_return.invoice_amount_cgst as value_cgst,-scm_purchase_return.invoice_amount_sgst as value_sgst,\n"
              + "CASE WHEN invoice_amount_cgst is null THEN -invoice_amount_igst ELSE null END  as value_igst,"
              + "-invoice_amount_igst as tax_value,"
              + "2 as purchase_or_return,\n"
              + "CASE WHEN scm_purchase_return.purchase_return_stock_cat = 1 THEN 'DAMAGED & EXPIRED' ELSE 'NON-MOVING & NEAR EXPIRY' END as return_type\n"
              + "from scm_purchase_return scm_purchase_return,scm_account,scm_vendor,scm_purchase_return_item t1     \n"
              + "where scm_purchase_return.company_id = ? and t1.purchase_return_id=scm_purchase_return.id and\n"
              + "scm_account.id = scm_purchase_return.account_id and scm_account.vendor_id=scm_vendor.id \n"
              + "and scm_purchase_return.purchase_return_status_id >=2  \n"
              + "AND scm_purchase_return.entry_date ::date >= ?::date AND scm_purchase_return.entry_date ::date <= ?::date\n";

      returnGroupBY += "GROUP BY supplier_name,scm_purchase_return.id,scm_purchase_return.account_id,scm_purchase_return.invoice_no, account_invoice_no,\n"
              + "scm_purchase_return.invoice_date,scm_purchase_return.entry_date,\n"
              + "scm_purchase_return.invoice_amount,-scm_purchase_return.invoice_amount_net,-scm_purchase_return.invoice_amount_net,\n"
              + "scm_purchase_return.invoice_amount_cgst,scm_purchase_return.invoice_amount_sgst,-invoice_amount_igst,\n"
              + " purchase_or_return, return_type,scm_purchase_return.reference_no ";
      params.add(company.getId());
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

      if (filterParameters.getAccountGroup() != null && filterParameters.getAccount() == null) {
        returnCond += " AND scm_purchase_return.account_id in(select account_id from scm_account_group_detail where account_group_id = ?) \n";
        params.add(filterParameters.getAccountGroup().getId());
      }
      if (filterParameters.getAccount() != null) {
        returnCond += " AND scm_purchase_return.account_id = ?";
        params.add(filterParameters.getAccount().getId());
      }
      if (!damageExpiry) {
        returnCond += " AND scm_purchase_return.purchase_return_stock_cat=2";
      }
    }
    String order = " order by invoice_no";
    sql = sql + cond + groupBy + returnSql + returnCond + returnGroupBY + order;
    return AppDb.getList(main.dbConnector(), PurchaseLedger.class, sql, params.toArray());
  }

  public static List<PurchaseLedger> getPurchaseRegisterProductWise(Main main, Company company, FilterParameters filterParameters, boolean includeReturn, boolean damageExpiry, List<Account> accountList) {
    ArrayList<Object> params = new ArrayList<>();
    String sql = "SELECT scm_product_entry.id,scm_product_entry.invoice_no as invoice_no,scm_vendor.vendor_name as supplier_name,\n"
            + "scm_product_entry.invoice_date as invoice_date,scm_product_entry.product_entry_date as entry_date,\n"
            + "scm_product_entry.reference_no reference_no,scm_product_entry.account_invoice_no,\n"
            + "scm_product.product_name as product_name,scm_product_category.hsn_code as hsn_code,\n"
            + "scm_product_batch.batch_no as batch_no,getPackDimension(scm_product.id) as pack_type,\n"
            + "scm_product_batch.expiry_date_actual as expiry_date,scm_product_entry_detail.value_mrp as value_mrp,\n"
            + "(CASE WHEN scm_product_entry.quantity_or_free = 1 THEN (scm_product_entry_detail.product_quantity+COALESCE(scm_product_entry_detail.product_quantity_free,0)) ELSE scm_product_entry_detail.product_quantity END) as purchase_qty,\n"
            + "scm_product_entry_detail.value_rate_per_prod_piece_der as purchase_rate,\n"
            + "scm_product_entry_detail.value_goods as goods_value,scm_product_entry_detail.scheme_discount_value as scheme_discount,\n"
            + "scm_product_entry_detail.discount_value as product_discount,scm_product_entry_detail.invoice_discount_value as invoice_discount,\n"
            + "(scm_product_entry_detail.value_rate_per_prod_piece_der*(scm_product_entry_detail.product_quantity +COALESCE(scm_product_entry_detail.product_quantity_free,0))) as taxable_value,\n"
            + "getTaxPercentage(scm_product_entry_detail.tax_code_id) as tax_percentage,\n"
            + "scm_product_entry_detail.value_cgst as value_cgst,scm_product_entry_detail.value_sgst as value_sgst,\n"
            + "CASE WHEN scm_product_entry_detail.value_sgst is null THEN scm_product_entry_detail.value_igst ELSE NULL END as value_igst,\n"
            + "scm_product_entry_detail.value_igst as tax_value,\n"
            + "((scm_product_entry_detail.value_rate_per_prod_piece_der*(scm_product_entry_detail.product_quantity +COALESCE(scm_product_entry_detail.product_quantity_free,0)))+scm_product_entry_detail.value_igst) as bill_amount,1 as purchase_or_return,\n"
            + "'' as return_type\n"
            + "FROM scm_product_entry_detail,scm_product_entry,scm_account,scm_vendor,scm_product_detail,\n"
            + "scm_product,scm_product_category,scm_product_batch\n"
            + "WHERE scm_product_entry_detail.product_entry_id = scm_product_entry.id AND\n"
            + "scm_product_entry.product_entry_status_id >=2 AND scm_product_entry.opening_stock_entry!=2 AND\n"
            + "scm_product_entry.account_id = scm_account.id AND scm_account.vendor_id = scm_vendor.id AND\n"
            + "scm_product_entry_detail.product_detail_id = scm_product_detail.id AND\n"
            + "scm_product_detail.product_batch_id = scm_product_batch.id AND\n"
            + "scm_product_batch.product_id = scm_product.id AND scm_product.product_category_id = scm_product_category.id AND\n"
            + "scm_product_entry.company_id = ? AND\n"
            + "scm_product_entry.product_entry_date >= to_date(?, 'YYYY-MM-DD') AND\n"
            + "scm_product_entry.product_entry_date <= to_date(?, 'YYYY-MM-DD')\n";

    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    String cond = "";
    if (filterParameters.getAccountGroup() != null && accountList == null) {
      cond += " AND scm_product_entry.account_id in(select account_id from scm_account_group_detail where account_group_id = ?)\n"
              + "AND scm_product.brand_id IN(SELECT brand_id FROM scm_account_group_brands WHERE account_group_id = ?)\n";
      params.add(filterParameters.getAccountGroup().getId());
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (accountList != null) {
      cond += " AND scm_product_entry.account_id IN(";
      int i = 1;
      for (Account account : accountList) {
        if (i != accountList.size()) {
          cond += account.getId() + ",";
        } else {
          cond += account.getId() + ")";
        }
        i++;
      }
    }
    String returnSql = "";
    String returnCond = "";
    if (includeReturn) {
      returnSql += " UNION \n"
              + "SELECT scm_purchase_return.id,scm_purchase_return.invoice_no as invoice_no,scm_vendor.vendor_name as supplier_name,\n"
              + "scm_purchase_return.entry_date as entry_date,scm_purchase_return.invoice_date as invoice_date,\n"
              + "scm_purchase_return.reference_no AS reference_no,'' as account_invoice_no,\n"
              + "scm_product.product_name as product_name,scm_product_category.hsn_code as hsn_code,\n"
              + "scm_product_batch.batch_no as batch_no,getPackDimension(scm_product.id) as pack_type,\n"
              + "scm_product_batch.expiry_date_actual as expiry_date,scm_purchase_return_item.value_mrp as value_mrp,\n"
              + "-scm_purchase_return_item.quantity_returned as purchase_qty,\n"
              + "-scm_purchase_return_item.landing_price_per_piece_company as purchase_rate,\n"
              + "-scm_purchase_return_item.value_goods as goods_value,-scm_purchase_return_item.scheme_discount_value as scheme_discount,\n"
              + "-scm_purchase_return_item.product_discount_value as product_discount,-scm_purchase_return_item.invoice_discount_value as invoice_discount,\n"
              + "-(scm_purchase_return_item.value_net-scm_purchase_return_item.value_igst) as taxable_value,getTaxPercentage(scm_purchase_return_item.tax_code_id) as tax_percentage,\n"
              + "-scm_purchase_return_item.value_cgst as value_cgst ,-scm_purchase_return_item.value_sgst as value_sgst,\n"
              + "CASE WHEN scm_purchase_return_item.value_cgst is null THEN -scm_purchase_return_item.value_igst ELSE null END as value_igst,\n"
              + "-scm_purchase_return_item.value_igst as tax_value,\n"
              + "-(scm_purchase_return_item.value_net) as bill_amount,2 as purchase_or_return,\n"
              + "CASE WHEN scm_purchase_return.purchase_return_stock_cat = 1 THEN 'DAMAGED & EXPIRED' ELSE 'NON-MOVING & NEAR EXPIRY' END as return_type\n"
              + "FROM scm_purchase_return_item,scm_purchase_return,scm_account,scm_vendor,scm_product_detail,\n"
              + "scm_product,scm_product_category,scm_product_batch\n"
              + "WHERE scm_purchase_return_item.purchase_return_id = scm_purchase_return.id AND\n"
              + "scm_purchase_return.purchase_return_status_id >=2 AND\n"
              + "scm_purchase_return.account_id = scm_account.id AND scm_account.vendor_id = scm_vendor.id AND\n"
              + "scm_purchase_return_item.product_detail_id = scm_product_detail.id AND\n"
              + "scm_product_detail.product_batch_id = scm_product_batch.id AND\n"
              + "scm_product_batch.product_id = scm_product.id AND scm_product.product_category_id = scm_product_category.id AND\n"
              + "scm_purchase_return.company_id = ? AND\n"
              + "scm_purchase_return.entry_date >= to_date(?, 'YYYY-MM-DD') AND\n"
              + "scm_purchase_return.entry_date <= to_date(?, 'YYYY-MM-DD')\n";
      params.add(company.getId());
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

      if (filterParameters.getAccountGroup() != null && accountList == null) {
        returnCond += " AND scm_purchase_return.account_id in(select account_id from scm_account_group_detail where account_group_id = ?)\n"
                + "AND scm_product.brand_id IN(SELECT brand_id FROM scm_account_group_brands WHERE account_group_id = ?)\n";
        params.add(filterParameters.getAccountGroup().getId());
        params.add(filterParameters.getAccountGroup().getId());
      }
      if (accountList != null) {
        if (accountList != null) {
          cond += " AND scm_product_entry.account_id IN(";
          int i = 1;
          for (Account account : accountList) {
            if (i != accountList.size()) {
              cond += account.getId() + ",";
            } else {
              cond += account.getId() + ")";
            }
            i++;
          }
        }
      }
      if (!damageExpiry) {
        returnCond += " AND scm_purchase_return.purchase_return_stock_cat=2";
      }
    }
    String order = " order by product_name";
    sql = sql + cond + returnSql + returnCond + order;
    return AppDb.getList(main.dbConnector(), PurchaseLedger.class, sql, params.toArray());
  }

  public static List<PurchaseLedger> getPurchaseRegisterInvoiceWise(Main main, Company company, FilterParameters filterParameters, boolean includeReturn, boolean damageExpiry, List<Account> accountList) {
    ArrayList<Object> params = new ArrayList<>();
    String sql = "SELECT scm_product_entry.id,scm_product_entry.invoice_no as invoice_no,scm_product_entry.account_invoice_no,scm_vendor.vendor_name as supplier_name,\n"
            + "scm_product_entry.invoice_date as invoice_date,scm_product_entry.product_entry_date as entry_date,scm_product_entry.account_invoice_no as reference_no,\n"
            + "SUM(scm_product_entry_detail.value_goods) as goods_value,SUM(COALESCE(scm_product_entry_detail.scheme_discount_value,0)) as scheme_discount,\n"
            + "SUM(COALESCE(scm_product_entry_detail.discount_value,0)) as product_discount,SUM(COALESCE(scm_product_entry_detail.invoice_discount_value,0)) as invoice_discount,\n"
            + "SUM(COALESCE((scm_product_entry_detail.landing_price_per_piece_company*(scm_product_entry_detail.product_quantity +\n"
            + "COALESCE(scm_product_entry_detail.product_quantity_free,0))),0)) as taxable_value,\n"
            + "SUM(COALESCE(scm_product_entry_detail.value_cgst,0)) as value_cgst,SUM(COALESCE(scm_product_entry_detail.value_sgst,0)) as value_sgst,\n"
            + "SUM(COALESCE(CASE WHEN scm_product_entry_detail.value_sgst is null THEN scm_product_entry_detail.value_igst ELSE NULL END,0)) as value_igst,\n"
            + "SUM(COALESCE(scm_product_entry_detail.value_igst,0)) as tax_value,\n"
            + "SUM(COALESCE(((scm_product_entry_detail.landing_price_per_piece_company*\n"
            + "  (scm_product_entry_detail.product_quantity +COALESCE(scm_product_entry_detail.product_quantity_free,0)))+\n"
            + " scm_product_entry_detail.value_igst),0)) as bill_amount,1 as purchase_or_return,\n"
            + "'' as return_type\n"
            + "FROM scm_product_entry_detail,scm_product_entry,scm_account,scm_vendor,scm_product_detail,\n"
            + "scm_product,scm_product_category,scm_product_batch\n"
            + "WHERE scm_product_entry_detail.product_entry_id = scm_product_entry.id AND\n"
            + "scm_product_entry.product_entry_status_id >=2 AND scm_product_entry.opening_stock_entry!=2 AND\n"
            + "scm_product_entry.account_id = scm_account.id AND scm_account.vendor_id = scm_vendor.id AND\n"
            + "scm_product_entry_detail.product_detail_id = scm_product_detail.id AND\n"
            + "scm_product_detail.product_batch_id = scm_product_batch.id AND\n"
            + "scm_product_batch.product_id = scm_product.id AND scm_product.product_category_id = scm_product_category.id AND\n"
            + "scm_product_entry.company_id = ? AND\n"
            + "scm_product_entry.product_entry_date >= to_date(?, 'YYYY-MM-DD') AND\n"
            + "scm_product_entry.product_entry_date <= to_date(?, 'YYYY-MM-DD')\n";

    String groupBy = "GROUP BY scm_product_entry.id,scm_product_entry.invoice_no,scm_vendor.vendor_name ,scm_product_entry.account_invoice_no,\n"
            + "scm_product_entry.invoice_date ,\n"
            + "scm_product_entry.reference_no \n";
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    String cond = "";
    if (filterParameters.getAccountGroup() != null && accountList == null) {
      cond += " AND scm_product_entry.account_id in(select account_id from scm_account_group_detail where account_group_id = ?)\n"
              + "AND scm_product.brand_id IN(SELECT brand_id FROM scm_account_group_brands WHERE account_group_id = ?) \n";
      params.add(filterParameters.getAccountGroup().getId());
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (accountList != null) {
      if (accountList != null) {
        cond += " AND scm_product_entry.account_id IN(";
        int i = 1;
        for (Account account : accountList) {
          if (i != accountList.size()) {
            cond += account.getId() + ",";
          } else {
            cond += account.getId() + ")";
          }
          i++;
        }
      }
    }
    String returnSql = "";
    String returnCond = "";
    String returnGroupBY = "";
    if (includeReturn) {
      returnSql += " UNION\n "
              + "SELECT scm_purchase_return.id,scm_purchase_return.invoice_no as invoice_no,'' as account_invoice_no,scm_vendor.vendor_name as supplier_name,\n"
              + "scm_purchase_return.invoice_date as invoice_date,scm_purchase_return.entry_date as entry_date,\n"
              + "scm_purchase_return.reference_no AS reference_no,\n"
              + "SUM(COALESCE(-scm_purchase_return_item.value_goods,0)) as goods_value,\n"
              + "			 SUM(COALESCE(-scm_purchase_return_item.scheme_discount_value,0)) as scheme_discount,\n"
              + "SUM(COALESCE(-scm_purchase_return_item.product_discount_value,0)) as product_discount,\n"
              + "						  SUM(COALESCE(-scm_purchase_return_item.invoice_discount_value,0)) as invoice_discount,\n"
              + "SUM(COALESCE(-(scm_purchase_return_item.value_net-scm_purchase_return_item.value_igst),0)) as taxable_value,\n"
              + "SUM(COALESCE(-scm_purchase_return_item.value_cgst,0)) as value_cgst ,SUM(COALESCE(-scm_purchase_return_item.value_sgst,0)) as value_sgst,\n"
              + "SUM(COALESCE(CASE WHEN scm_purchase_return_item.value_cgst is null THEN -scm_purchase_return_item.value_igst ELSE null END,0)) as value_igst,\n"
              + "SUM(COALESCE(-scm_purchase_return_item.value_igst,0)) as tax_value,\n"
              + "SUM(COALESCE(-(scm_purchase_return_item.value_net),0)) as bill_amount,2 as purchase_or_return ,\n"
              + "CASE WHEN scm_purchase_return.purchase_return_stock_cat = 1 THEN 'DAMAGED & EXPIRED' ELSE 'NON-MOVING & NEAR EXPIRY' END as return_type\n"
              + "FROM scm_purchase_return_item,scm_purchase_return,scm_account,scm_vendor,scm_product_detail,\n"
              + "scm_product,scm_product_category,scm_product_batch\n"
              + "WHERE scm_purchase_return_item.purchase_return_id = scm_purchase_return.id AND\n"
              + "scm_purchase_return.purchase_return_status_id >=2 AND\n"
              + "scm_purchase_return.account_id = scm_account.id AND scm_account.vendor_id = scm_vendor.id AND\n"
              + "scm_purchase_return_item.product_detail_id = scm_product_detail.id AND\n"
              + "scm_product_detail.product_batch_id = scm_product_batch.id AND\n"
              + "scm_product_batch.product_id = scm_product.id AND scm_product.product_category_id = scm_product_category.id AND\n"
              + "scm_purchase_return.company_id = ? AND\n"
              + "scm_purchase_return.entry_date >= to_date(?, 'YYYY-MM-DD') AND\n"
              + "scm_purchase_return.entry_date <= to_date(?, 'YYYY-MM-DD')\n";

      returnGroupBY += "GROUP BY scm_vendor.vendor_name ,scm_purchase_return.id,\n"
              + "scm_purchase_return.invoice_no ,scm_purchase_return.invoice_date ,\n"
              + "scm_purchase_return.reference_no\n";
      params.add(company.getId());
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

      if (filterParameters.getAccountGroup() != null && accountList == null) {
        returnCond += " AND scm_purchase_return.account_id in(select account_id from scm_account_group_detail where account_group_id = ?)\n"
                + "AND scm_product.brand_id IN(SELECT brand_id FROM scm_account_group_brands WHERE account_group_id = ?)\n";
        params.add(filterParameters.getAccountGroup().getId());
        params.add(filterParameters.getAccountGroup().getId());
      }
      if (accountList != null) {
        if (accountList != null) {
          cond += " AND scm_product_entry.account_id IN(";
          int i = 1;
          for (Account account : accountList) {
            if (i != accountList.size()) {
              cond += account.getId() + ",";
            } else {
              cond += account.getId() + ")";
            }
            i++;
          }
        }
      }
      if (!damageExpiry) {
        returnCond += " AND scm_purchase_return.purchase_return_stock_cat=2";
      }
    }
    String order = " order by invoice_no";
    sql = sql + cond + groupBy + returnSql + returnCond + returnGroupBY + order;
    return AppDb.getList(main.dbConnector(), PurchaseLedger.class, sql, params.toArray());
  }
}
