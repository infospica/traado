/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.reports.model.CompanyGstInAndOut;
import spica.reports.model.FilterParameters;
import spica.reports.model.GstReport;
import spica.reports.model.GstReportSummary;
import spica.scm.domain.Company;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;

/**
 *
 * @author Anoop Jayachandran
 */
public abstract class CompanyGstInAndOutService {

  /**
   *
   * @return
   */
  public static List<CompanyGstInAndOut> selectCompanyGstInAndOut(FilterParameters filterParameters) {
    List<CompanyGstInAndOut> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      if (i % 2 == 0) {
        list.add(new CompanyGstInAndOut(i + 1, "INV" + i, 100.0, null, null, new Date(), "Invoice Entry", 1, "Trader " + 1));
      } else {
        list.add(new CompanyGstInAndOut(i + 1, "INV" + i, 100.0, 50.00, 50.00, new Date(), "SALES", 0, "Trader " + 1));
      }

    }
    return list;
  }

  public static List taxInputOutput(MainView main, Company company, FilterParameters filterParameters) {

    String sql = "SELECT SUM(debit) as debit,SUM(credit) as credit,fal.ledger_code ledgercode,fag.title as taxtype \n"
            + "from fin_accounting_transaction_detail fad,fin_accounting_ledger fal,fin_accounting_group fag \n"
            + "WHERE fad.accounting_ledger_id=fal.id \n"
            + "AND fal.accounting_group_id=fag.id \n"
            + "AND fal.company_id=? \n"
            + "AND (fal.ledger_code=? or fal.ledger_code=?) \n"
            + "AND (fag.id in (?,?,?) )  AND (fad.entry_date >= ? ::date AND fad.entry_date <= ?::date )\n"
            + " GROUP BY fag.id,fal.ledger_code ORDER BY fal.ledger_code ASC";
    return AppDb.getList(main.dbConnector(), GstReport.class, sql, new Object[]{company.getId(), AccountingConstant.LEDGER_CODE_TAX_OUTPUT, AccountingConstant.LEDGER_CODE_TAX_INPUT,
      AccountingConstant.ACC_HEAD_TAX_IGST.getId(), AccountingConstant.ACC_HEAD_TAX_CGST.getId(), AccountingConstant.ACC_HEAD_TAX_SGST.getId(), filterParameters.getFromDate(), filterParameters.getToDate()});

    // return (TaxType) AppService.single(main,TaxType.class, "select SUM(invoice_amount_cgst) as cgst,SUM(invoice_amount_sgst) as sgst,SUM(invoice_amount_igst) as igst from scm_sales_invoice where company_id=?", new Object[]{company.getId()});
  }

  public static List taxInputOutputDetail(MainView main, Company company, FilterParameters filterParameters) {

    String sql = "select  SUM(debit) as debit,SUM(credit) as credit,fal.ledger_code as ledgercode,fag.title as taxtype,fal.title,fal.entity_id as entityid\n"
            + "from fin_accounting_transaction_detail fad,fin_accounting_ledger fal,fin_accounting_group fag\n"
            + "WHERE fad.accounting_ledger_id=fal.id\n"
            + "AND fal.accounting_group_id=fag.id\n"
            + "AND fal.company_id=?\n"
            + "AND (fal.ledger_code=?)\n"
            + "AND (fag.id in (?,?,?) )\n"
            + "AND (fad.entry_date >= ?::date AND fad.entry_date <= ?::date )\n"
            + "GROUP BY fal.title,fag.id,fal.ledger_code,fal.title,fal.entity_id";
    return AppDb.getList(main.dbConnector(), GstReport.class, sql, new Object[]{company.getId(), filterParameters.getReportType(), AccountingConstant.ACC_HEAD_TAX_IGST.getId(), AccountingConstant.ACC_HEAD_TAX_CGST.getId(),
      AccountingConstant.ACC_HEAD_TAX_SGST.getId(), filterParameters.getFromDate(), filterParameters.getToDate()});

  }

  public static List getLedgerDetails(Main main, GstReportSummary reportView, Company company, Integer taxType) {

    String sql = "select  debit ,credit ,fal.ledger_code as ledgercode,fag.title as taxtype,fal.title,fal.entity_id as entityid,fat.ref_document_no as document,fad.accounting_transaction_id as transactionid,fad.id as detailid\n"
            + "from fin_accounting_transaction_detail fad,fin_accounting_ledger fal,fin_accounting_group fag,fin_accounting_transaction fat\n"
            + "WHERE fad.accounting_ledger_id=fal.id\n"
            + "AND fal.accounting_group_id=fag.id\n"
            + "AND fal.company_id=?\n"
            + "AND (fal.ledger_code=?)\n"
            + "AND (fag.id in (?) )\n"
            + "AND fad.accounting_transaction_id=fat.id\n"
            + "AND (fad.entry_date >= ?::date AND fad.entry_date <= ?::date )";

    return AppDb.getList(main.dbConnector(), GstReport.class, sql, new Object[]{company.getId(),reportView.getLedgerCode(), taxType,
      reportView.getFromDate(), reportView.getToDate()});

  }
}
