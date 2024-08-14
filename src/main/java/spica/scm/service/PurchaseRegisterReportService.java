/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.service;

import java.util.List;
import spica.reports.model.FilterParameters;
import spica.scm.domain.ProductEntry;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;

/**
 *
 * @author sujesh
 */
public abstract class PurchaseRegisterReportService {

  private static String getProductEntrySqlPaged() {
    String sql = "select scm_product_entry.id,scm_product_entry.opening_stock_entry,scm_product_entry.account_id,scm_product_entry.consignment_id,scm_product_entry.contract_id,\n"
            + "scm_product_entry.invoice_no,scm_product_entry.invoice_date,scm_product_entry.product_entry_date,scm_product_entry.invoice_amount,scm_product_entry.invoice_amount_net,\n"
            + "scm_product_entry.invoice_amount_assessable,scm_product_entry.invoice_amount_vat,scm_product_entry.invoice_amount_cst,scm_product_entry.invoice_amount_ex_duty,\n"
            + "scm_product_entry.invoice_amount_discount_value,scm_product_entry.invoice_amount_discount_perc,scm_product_entry.is_invoice_discount_to_customer,\n"
            + "scm_product_entry.vendor_reserve_percentage,scm_product_entry.total_expense_amount,scm_product_entry.credit_period,scm_product_entry.purchase_credit_days,\n"
            + "scm_product_entry.purchase_credit_day_appli_from,scm_product_entry.sales_credit_days,scm_product_entry.product_entry_status_id,scm_product_entry.note,\n"
            + "scm_product_entry.invoice_amount_variation,scm_product_entry.line_item_discount,scm_product_entry.invoice_round_off,scm_product_entry.is_tax_code_modified, \n"
            + "scm_product_entry.account_invoice_no,scm_product_entry.invoice_amount_cgst,scm_product_entry.invoice_amount_sgst,scm_product_entry.invoice_amount_igst,\n"
            + "scm_product_entry.tax_processor_id,scm_product_entry.record_type,scm_product_entry.confirmed_at,scm_product_entry.confirmed_by,scm_product_entry.created_at,\n"
            + "scm_product_entry.modified_at,scm_product_entry.created_by,scm_product_entry.modified_by, scm_product_entry.special_discount_applicable,\n"
            + "scm_product_entry.cash_discount_applicable,scm_product_entry.cash_discount_taxable,scm_product_entry.cash_discount_value,scm_product_entry.special_discount_value,\n"
            + "scm_product_entry.company_id,scm_product_entry.is_invoice_discount_in_percentage \n"
            + "from scm_product_entry scm_product_entry  \n"
            + "left outer join scm_account scm_product_entryaccount_id on (scm_product_entryaccount_id.id = scm_product_entry.account_id) \n"
            + "left outer join scm_consignment scm_product_entryconsignment_id on (scm_product_entryconsignment_id.id = scm_product_entry.consignment_id) \n"
            + "left outer join scm_contract scm_product_entrycontract_id on (scm_product_entrycontract_id.id = scm_product_entry.contract_id) \n"
            + "left outer join scm_company scm_company on (scm_company.id = scm_product_entry.company_id) \n"
            + "left outer join scm_product_entry_status product_entry_status on (product_entry_status.id = scm_product_entry.product_entry_status_id)";
    return sql;
  }

  /**
   * Return List of ProductEntry.
   *
   * @param main
   * @return List of ProductEntry
   */
  public static final List<ProductEntry> selectAllEntry(Main main,FilterParameters filterParameters) {
    String cond = "where scm_product_entry.company_id = ? and scm_product_entry.opening_stock_entry=0 and scm_product_entry.product_entry_status_id= ? ";
    cond += " AND scm_product_entry.product_entry_date ::date >= ? ::date AND scm_product_entry.product_entry_date ::date <= ? ::date " ;
    String sql = getProductEntrySqlPaged() + cond;
    return AppService.list(main, ProductEntry.class, sql, new Object[]{UserRuntimeView.instance().getCompany().getId(),SystemConstants.CONFIRMED,SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()),SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate())});
  }

  public static final List<ProductEntry> selectAllPurchaseEntryByAccount(Main main, FilterParameters filterParameters) {
    String cond = "where scm_product_entry.account_id = ? and scm_product_entry.company_id = ?  and scm_product_entry.opening_stock_entry=0 and scm_product_entry.product_entry_status_id= ? ";
    cond += " AND scm_product_entry.product_entry_date ::date >= ? ::date AND scm_product_entry.product_entry_date ::date <= ? ::date " ;
    String orderBy = "order by scm_product_entry.invoice_date desc";
    String sql = getProductEntrySqlPaged() + cond + orderBy;
    return AppService.list(main, ProductEntry.class, sql, new Object[]{filterParameters.getAccount().getId(),UserRuntimeView.instance().getCompany().getId(),SystemConstants.CONFIRMED,SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()),SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate())});
  }

  public static final List<ProductEntry> selectAllPurchaseEntryByAccountGroup(Main main, FilterParameters filterParameters) {
    String cond = "where scm_product_entry.account_id IN ( select account_id from scm_account_group_detail where account_group_id = ? ) and scm_product_entry.company_id = ?  and scm_product_entry.opening_stock_entry=0 and scm_product_entry.product_entry_status_id= ? ";
    cond += " AND scm_product_entry.product_entry_date ::date >= ? ::date AND scm_product_entry.product_entry_date ::date <= ? ::date " ;
    String orderBy = "order by scm_product_entry.invoice_date desc";
    String sql = getProductEntrySqlPaged() + cond + orderBy;
    return AppService.list(main, ProductEntry.class, sql, new Object[]{filterParameters.getAccountGroup().getId(),UserRuntimeView.instance().getCompany().getId(),SystemConstants.CONFIRMED,SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()),SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate())});

  }

}
