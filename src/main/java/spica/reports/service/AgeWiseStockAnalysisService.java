/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import spica.reports.model.AgeWiseStockAnalysis;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Company;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author godson
 */
public class AgeWiseStockAnalysisService {

  public static List<AgeWiseStockAnalysis> getAgeWiseStockAnalysisList(Main main, Company company, FilterParameters filterParameters) {
    String sql = "SELECT scm_product.product_name,scm_product_entry.invoice_no,TO_CHAR(scm_product_entry.product_entry_date,'dd-MM-YYYY') as product_entry_date,TAB.current_qty,(TAB.sale_rate*TAB.current_qty) as taxable_amount,\n"
            + "extract(day from now()::timestamp-scm_product_entry.product_entry_date) as days,scm_product.hsn_code,scm_tax_code.rate_percentage\n"
            + "FROM scm_product_entry,scm_product_entry_detail,getStockAuditReport(?,?,?,NULL,?,?) as TAB,scm_product,\n"
            + "scm_product_category,scm_tax_code \n"
            + "WHERE scm_product_entry.id =scm_product_entry_detail.product_entry_id \n"
            + "AND scm_product_entry_detail.id = TAB.product_ent_det_id AND scm_product_entry.product_entry_status_id=2\n"
            + "AND scm_product.id = TAB.prod_id AND scm_product.product_category_id = scm_product_category.id \n"
            + "AND scm_tax_code.id = scm_product_category.sales_tax_code_id";

    ArrayList<Object> params = new ArrayList<>();
    params.add(company.getId());
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      params.add(filterParameters.getAccountGroup().getId());
    } else {
      params.add(null);
    }
    if (filterParameters.getAccount() != null && filterParameters.getAccount().getId() != null) {
      params.add(filterParameters.getAccount().getId());
    } else {
      params.add(null);
    }
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    return AppDb.getList(main.dbConnector(), AgeWiseStockAnalysis.class, sql, params.toArray());
  }

}
