/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.constant.ReportConstant;
import spica.reports.model.CompanyCustomerSales;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Company;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author Godson Joseph
 */
public class DebitCreditNoteReportService {

  public static List<CompanyCustomerSales> getDebitCreditReportList(Main main, Company company, Integer type, FilterParameters filterParameters) {
    String sql = "";
    ArrayList<Object> params = new ArrayList<>();
    params.add(company.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    if (filterParameters.getReportType() == null) {
      filterParameters.setReportType(((Integer) ReportConstant.SUMMARY_VIEW).toString());
    }
    if (filterParameters.getReportType().equals(((Integer) ReportConstant.SUMMARY_VIEW).toString())) {
      if (filterParameters.getFilterType() != null && AccountingConstant.CUSTOMER_DEBIT_CREDIT_NOTE.equals(filterParameters.getFilterType())) {
        sql = "SELECT dc_note.id,dc_note.document_no,dc_note.entry_date,\n"
                + "CASE WHEN dc_note.invoice_type=1 THEN 'DEBIT NOTE' ELSE 'CREDIT NOTE' END as invoice_type,\n"
                + "dc_note.invoice_date,party.customer_name as party,dc_note.invoice_no,\n"
                + "dc_note.assessable_value as taxable_value,CASE WHEN dc_note.is_interstate = 0 THEN dc_note.cgst_value ELSE 0 END as cgst,\n"
                + "CASE WHEN dc_note.is_interstate=0 THEN dc_note.sgst_value ELSE 0 END as sgst,\n"
                + "CASE WHEN dc_note.is_interstate=1 THEN dc_note.igst_value ELSE 0 END as igst,\n"
                + "dc_note.igst_value as gst_amount,dc_note.net_value as net_amount\n"
                + "FROM fin_debit_credit_note dc_note,fin_accounting_ledger ledger,scm_customer party\n"
                + "WHERE ledger.id = dc_note.accounting_ledger_id\n"
                + "AND party.id = ledger.entity_id AND dc_note.debit_credit_party=2\n"
                + "AND dc_note.status_id=2\n"
                + "AND dc_note.company_id=? \n"
                + "AND dc_note.entry_date::date>=?::date AND dc_note.entry_date::date<=?::date \n";
      } else if (filterParameters.getFilterType() != null && AccountingConstant.SUPPLIER_DEBIT_CREDIT_NOTE.equals(filterParameters.getFilterType())) {
        sql = " SELECT dc_note.id,dc_note.document_no,dc_note.entry_date,\n"
                + "CASE WHEN dc_note.invoice_type=1 THEN 'DEBIT NOTE' ELSE 'CREDIT NOTE' END as invoice_type,\n"
                + "dc_note.invoice_date,party.vendor_name as party,dc_note.invoice_no,\n"
                + "dc_note.assessable_value as taxable_value,CASE WHEN dc_note.is_interstate = 0 THEN dc_note.cgst_value ELSE 0 END as cgst,\n"
                + "CASE WHEN dc_note.is_interstate=0 THEN dc_note.sgst_value ELSE 0 END as sgst,\n"
                + "CASE WHEN dc_note.is_interstate=1 THEN dc_note.igst_value ELSE 0 END as igst,\n"
                + "dc_note.igst_value as gst_amount,dc_note.net_value as net_amount \n"
                + "FROM fin_debit_credit_note dc_note,fin_accounting_ledger ledger,scm_vendor party\n"
                + "WHERE  ledger.id = dc_note.accounting_ledger_id\n"
                + "AND party.id = ledger.entity_id AND dc_note.debit_credit_party=1\n"
                + "AND dc_note.status_id=2\n"
                + "AND dc_note.company_id=? \n"
                + "AND dc_note.entry_date::date>=?::date AND dc_note.entry_date::date<=?::date \n";
      }
    } else {
      if (filterParameters.getFilterType() != null && AccountingConstant.CUSTOMER_DEBIT_CREDIT_NOTE.equals(filterParameters.getFilterType())) {
        sql = "SELECT dc_note.id,dc_note.document_no,dc_note.entry_date,dc_note.invoice_type,\n"
                + "CASE WHEN dc_note.invoice_type=1 THEN 'DEBIT NOTE' ELSE 'CREDIT NOTE' END as invoice_type,\n"
                + "dc_note.invoice_date,party.customer_name as party,item.title as product_name,item.hsn_sac_code as hsn_code,tax.rate_percentage as tax_perc,item.ref_invoice_no,item.ref_invoice_date,\n"
                + "item.taxable_value,CASE WHEN dc_note.is_interstate = 0 THEN item.cgst_amount ELSE 0 END as cgst,\n"
                + "CASE WHEN dc_note.is_interstate=0 THEN item.sgst_amount ELSE 0 END as sgst,\n"
                + "CASE WHEN dc_note.is_interstate=1 THEN item.igst_amount ELSE 0 END as igst,\n"
                + "item.igst_amount as gst_amount,item.net_value  as netAmount\n"
                + "FROM fin_debit_credit_note dc_note,fin_accounting_ledger ledger,scm_customer party,fin_debit_credit_note_item item\n"
                + "LEFT JOIN scm_tax_code tax ON tax.id=item.tax_code_id\n"
                + "WHERE dc_note.id=item.debit_credit_note_id AND ledger.id = dc_note.accounting_ledger_id\n"
                + "AND party.id = ledger.entity_id AND dc_note.debit_credit_party=2\n"
                + "AND dc_note.status_id=2\n"
                + "AND dc_note.company_id=? \n"
                + "AND dc_note.entry_date::date>=?::date AND dc_note.entry_date::date<=?::date \n";
      } else if (filterParameters.getFilterType() != null && AccountingConstant.SUPPLIER_DEBIT_CREDIT_NOTE.equals(filterParameters.getFilterType())) {
        sql = " SELECT dc_note.id,dc_note.document_no,dc_note.entry_date,dc_note.invoice_type,\n"
                + "CASE WHEN dc_note.invoice_type=1 THEN 'DEBIT NOTE' ELSE 'CREDIT NOTE' END as invoice_type,\n"
                + "dc_note.invoice_date,party.vendor_name as party,item.title as product_name,item.hsn_sac_code as hsn_code,tax.rate_percentage as tax_perc,item.ref_invoice_no,item.ref_invoice_date,\n"
                + "item.taxable_value,CASE WHEN dc_note.is_interstate = 0 THEN item.cgst_amount ELSE 0 END as cgst,\n"
                + "CASE WHEN dc_note.is_interstate=0 THEN item.sgst_amount ELSE 0 END as sgst,\n"
                + "CASE WHEN dc_note.is_interstate=1 THEN item.igst_amount ELSE 0 END as igst,\n"
                + "item.igst_amount as gst_amount,item.net_value as netAmount\n"
                + "FROM fin_debit_credit_note dc_note,fin_accounting_ledger ledger,scm_vendor party,fin_debit_credit_note_item item\n"
                + "LEFT JOIN scm_tax_code tax ON tax.id=item.tax_code_id\n"
                + "WHERE dc_note.id=item.debit_credit_note_id AND ledger.id = dc_note.accounting_ledger_id\n"
                + "AND party.id = ledger.entity_id AND dc_note.debit_credit_party=1\n"
                + "AND dc_note.status_id=2\n"
                + "AND dc_note.company_id=? \n"
                + "AND dc_note.entry_date::date>=?::date AND dc_note.entry_date::date<=?::date \n";
      }
    }
    if (type != null && type != 0) {
      sql += " AND dc_note.invoice_type = ?";
      params.add(type);
    }
    if (filterParameters.getAccountGroup() != null) {
      sql += "AND dc_note.account_group_id=? \n";
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getAccount() != null) {
      sql += "AND dc_note.account_id=? \n";
      params.add(filterParameters.getAccount().getId());
    }
    if (filterParameters.getCustomer() != null) {
      sql += "AND ledger.entity_id=? \n";
      params.add(filterParameters.getAccount().getId());
    }
    sql += " ORDER BY dc_note.entry_date,dc_note.document_no ";
    return AppDb.getList(main.dbConnector(), CompanyCustomerSales.class, sql, params.toArray());
  }
}
