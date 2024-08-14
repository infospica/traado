/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.fin.service;

import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.common.FilterObjects;
import spica.fin.common.TrialBalanceReport;
import spica.fin.domain.AccountingGroup;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author sujesh
 */
public class TrialBalanceReportService {

  public static List loadTrialBalance(Main main, Integer companyId, FilterObjects filterObjects, String type, AccountingGroup accountingGroup) {
    if ((type.equals(SystemConstants.ALL)) && accountingGroup != null && accountingGroup.getId() != null) {
      return loadAllTrialBalanceByGroupId(main, companyId, accountingGroup.getId(), filterObjects);
    } else {
      return selectTrialBalanceByCompanyAndPeriodAndType(main, companyId, filterObjects, type);
    }
  }

  private static String getOpeningBalance() {
    String sql = "(select(COALESCE(SUM(tab.debit), 0) - COALESCE(SUM(tab.credit), 0)) opening_balance \n"
            + "from fin_accounting_ledger led, (select dt.accounting_transaction_id,dt.credit,dt.debit,tran.ledger_debit_id,tran.ledger_credit_id,tran.entry_date,tran.document_number,\n"
            + "tran.narration,tran.total_amount,vt.title, CASE WHEN (dt.credit <= 0 or dt.credit is null) THEN tran.ledger_credit_id ELSE tran.ledger_debit_id end as lid,tran.id \n"
            + "from \n"
            + "fin_accounting_transaction_detail dt,\n"
            + "fin_accounting_transaction tran ,\n"
            + "fin_accounting_ledger t3,\n"
            + "fin_accounting_voucher_type vt \n"
            + "where dt.accounting_transaction_id=tran.id \n"
            + "and vt.id=tran.voucher_type_id and ( CASE WHEN (dt.credit <= 0 or dt.credit is null) THEN t3.id=tran.ledger_debit_id ELSE t3.id=tran.ledger_credit_id end) \n"
            + "and dt.accounting_ledger_id = t1.accounting_ledger_id\n"
            + "and tran.company_id=? and t3.company_id=? and to_char(tran.entry_date, 'yyyy-mm-dd') < to_char(?::date, 'yyyy-mm-dd'))tab where tab.lid=led.id )";
    return sql;
  }

  private static List loadGroupWiseTrialBalance(Main main, Integer companyId, FilterObjects filterObjects, String type) {
    main.clear();
    String sql = "SELECT grp.id,grp.title as ledger_group, SUM(grp.opening_balance) as opening_balance,SUM(grp.debit) as debit,SUM(grp.credit) as credit FROM \n"
            + "(select  t1.accounting_ledger_id, t3.title, \n"
            + getOpeningBalance() + ",\n"
            + "sum(t1.debit) as debit, sum(t1.credit) as credit, t3.id  \n"
            + "from fin_accounting_transaction_detail t1  \n"
            + "left outer join fin_accounting_ledger t2 on (t2.id = t1.accounting_ledger_id)  \n"
            + "left outer join fin_accounting_group t3 on (t3.id = t2.accounting_group_id)  \n"
            + "left outer join fin_accounting_transaction tran on (tran.id = t1.accounting_transaction_id) \n"
            + " where t2.company_id = ?  and to_char(tran.entry_date, 'yyyy-mm-dd') \n"
            + "between to_char(?::date, 'yyyy-mm-dd') and to_char(?::date, 'yyyy-mm-dd')\n"
            + "group by  t3.id,t1.accounting_ledger_id, t3.title)grp \n"
            + "group by grp.id,grp.title order by grp.title";
    main.param(companyId);
    main.param(companyId);
    main.param(filterObjects.getFromDate());
    main.param(companyId);
    main.param(filterObjects.getFromDate());
    main.param(filterObjects.getToDate());

    return AppDb.getList(main.dbConnector(), TrialBalanceReport.class, sql, main.getParamData().toArray());
  }

  private static List loadAllTrialBalance(Main main, Integer companyId, FilterObjects filterObjects, String type) {
    main.clear();
    String sql = "select t1.accounting_ledger_id as id, t1.accounting_ledger_id, t2.title as ledger, t3.title as ledger_group, \n"
            + getOpeningBalance() + ",\n"
            + "sum(t1.debit) as debit, sum(t1.credit) as credit  \n"
            + "from fin_accounting_transaction_detail t1  \n"
            + "left outer join fin_accounting_ledger t2 on (t2.id = t1.accounting_ledger_id)  \n"
            + "left outer join fin_accounting_group t3 on (t3.id = t2.accounting_group_id)  \n"
            + "left outer join fin_accounting_transaction tran on (tran.id = t1.accounting_transaction_id) \n"
            + " where t2.company_id = ?  and to_char(tran.entry_date, 'yyyy-mm-dd') \n"
            + "between to_char(?::date, 'yyyy-mm-dd') and to_char(?::date, 'yyyy-mm-dd')\n"
            + "group by t1.accounting_ledger_id,t2.title, t3.title order by t2.title,t3.title ASC";

    main.param(companyId);
    main.param(companyId);
    main.param(filterObjects.getFromDate());
    main.param(companyId);
    main.param(filterObjects.getFromDate());
    main.param(filterObjects.getToDate());

    return AppDb.getList(main.dbConnector(), TrialBalanceReport.class, sql, main.getParamData().toArray());
  }

  public static List loadAllTrialBalanceByGroupId(Main main, Integer companyId, Integer groupId, FilterObjects filterObjects) {

    main.clear();
    String byGroup = " Tab.Ledger_grp_id,Tab.ledger_group ";
    String byAll = " Tab.Ledger_grp_id,Tab.ledger_id, Tab.ledger_group, Tab.ledger ";
    String apnd = byAll;
    String sql = "SELECT " + apnd + ", SUM(Tab.open_dr)- SUM(Tab.open_cr) as opening_balance,\n"
            + "SUM(Tab.amt_dr) as debit, SUM(Tab.amt_cr)  as credit FROM\n"
            + "(SELECT FinAccTransDr.entry_date,FinAccTransDr.id as accounting_transaction_id,FinAccGrp.id as Ledger_grp_id,FinAccLedg.id ledger_id,FinAccGrp.title as ledger_group,FinAccLedg.title as ledger,\n"
            + "CASE WHEN FinAccTransDr.entry_date::date < ?::date THEN detail.debit  ELSE 0 END as open_dr,\n"
            + "0 as open_cr,\n"
            + "CASE WHEN FinAccTransDr.entry_date::date >= ?::date THEN detail.debit  ELSE 0 END as amt_dr,\n"
            + "0 as amt_cr\n"
            + "FROM fin_accounting_group as FinAccGrp,fin_accounting_ledger as FinAccLedg,\n"
            + "fin_accounting_transaction as FinAccTransDr, fin_accounting_transaction_detail as detail\n"
            + "WHERE FinAccGrp.id = FinAccLedg.accounting_group_id  AND FinAccLedg.id =detail.accounting_ledger_id AND FinAccLedg.company_id =? AND\n"
            + " FinAccGrp.id=? AND FinAccTransDr.entry_date::date <= ?::date and detail.accounting_transaction_id = FinAccTransDr.id  \n"
            + "UNION ALL \n"
            + "SELECT FinAccTransCr.entry_date,FinAccTransCr.id as accounting_transaction_id,FinAccGrp.id as Ledger_grp_id,FinAccLedg.id ledger_id,FinAccGrp.title as ledger_group,FinAccLedg.title as ledger,\n"
            + "0 as open_dr,\n"
            + "CASE WHEN FinAccTransCr.entry_date::date < ?::date THEN detail.credit  ELSE 0 END as open_cr,\n"
            + "0 as amt_dr,\n"
            + "CASE WHEN FinAccTransCr.entry_date::date >= ?::date THEN detail.credit  ELSE 0 END as amt_cr \n"
            + "FROM fin_accounting_group as FinAccGrp,fin_accounting_ledger as FinAccLedg,\n"
            + "fin_accounting_transaction as FinAccTransCr , fin_accounting_transaction_detail as detail \n"
            + "WHERE FinAccGrp.id = FinAccLedg.accounting_group_id  AND FinAccLedg.id =detail.accounting_ledger_id AND FinAccLedg.company_id =? AND \n"
            + " FinAccGrp.id = ? AND FinAccTransCr.entry_date::date <= ?::date AND detail.accounting_transaction_id = FinAccTransCr.id ) As Tab GROUP BY " + apnd;
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getFromDate()));
    main.param(companyId);
    main.param(groupId);
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getToDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getFromDate()));
    main.param(companyId);
    main.param(groupId);
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getToDate()));
    return AppDb.getList(main.dbConnector(), TrialBalanceReport.class, sql, main.getParamData().toArray());
  }

  public static List loadLedgerOutstanding(Main main, Integer companyId, AccountingGroup accountingGroup) {
    main.clear();
    String sql = "select *\n"
            + ",\n"
            + "ABS((select  COALESCE(sum(item.balance_amount),0) as receivable \n"
            + " from fin_accounting_transaction_detail_item item \n"
            + "            left outer join fin_accounting_transaction_detail t1 on (t1.id = item.accounting_transaction_detail_id)  \n"
            + "            left outer join  fin_accounting_ledger t2  on (t2.id = t1.accounting_ledger_id) \n"
            + "            where item.record_type = ? and item.status in (?,?)\n";
    main.param(AccountingConstant.RECORD_TYPE_RECEIVABLE);
    main.param(AccountingConstant.STATUS_NEW);
    main.param(AccountingConstant.STATUS_PARTIAL_PROCESSED);
    if (accountingGroup != null && accountingGroup.getId() != null) {
      sql += "            and t2.accounting_group_id=?\n";
      main.param(accountingGroup.getId());
    }
    sql += "            and tab.id = t2.id)\n"
            + "- (select  COALESCE(sum(item.balance_amount),0) as  payable\n"
            + " from fin_accounting_transaction_detail_item item \n"
            + "            left outer join fin_accounting_transaction_detail t1  on (t1.id = item.accounting_transaction_detail_id)  \n"
            + "            left outer join  fin_accounting_ledger t2  on (t2.id = t1.accounting_ledger_id) \n"
            + "            where item.record_type = ? and item.status in (?,?)\n";
    main.param(AccountingConstant.RECORD_TYPE_PAYABLE);
    main.param(AccountingConstant.STATUS_NEW);
    main.param(AccountingConstant.STATUS_PARTIAL_PROCESSED);
    if (accountingGroup != null && accountingGroup.getId() != null) {
      sql += "            and t2.accounting_group_id=?\n";
      main.param(accountingGroup.getId());
    }
    sql += "            and tab.id = t2.id)) as outstanding\n"
            + "from (";

    sql += "select t1.accounting_ledger_id as id, t1.accounting_ledger_id, t2.title as ledger, t3.title as ledger_group, \n"
            + " ABS(COALESCE(SUM(t1.debit), 0) - COALESCE(SUM(t1.credit), 0)) as ledger_balance\n"
            + " from fin_accounting_transaction_detail t1  \n"
            + " left outer join fin_accounting_ledger t2 on (t2.id = t1.accounting_ledger_id)  \n"
            + " left outer join fin_accounting_group t3 on (t3.id = t2.accounting_group_id)  \n"
            // + " left outer join fin_accounting_transaction tran on (tran.id = t1.accounting_transaction_id) \n"
            //            + " left outer join fin_accounting_transaction_detail_item item on (t1.id = item.accounting_transaction_detail_id) \n"
            + " where t2.company_id = ? AND t3.id IN(?,?) \n";
    main.param(companyId);
    main.param(AccountingConstant.GROUP_SUNDRY_CREDITORS.getId());
    main.param(AccountingConstant.GROUP_SUNDRY_DEBTORS.getId());
    if (accountingGroup != null && accountingGroup.getId() != null) {
      sql += "            and t3.id =?\n";
      main.param(accountingGroup.getId());
    }
    sql += " group by t1.accounting_ledger_id,t2.title, t3.title order by t2.title ASC ";

    sql += ") as tab";

    return AppDb.getList(main.dbConnector(), TrialBalanceReport.class, sql, main.getParamData().toArray());

  }

  public static List selectTrialBalanceByCompanyAndPeriodAndType(Main main, Integer companyId, FilterObjects filterObjects, String type) {
    main.clear();
    String byGroup = " Tab.Ledger_grp_id,Tab.ledger_group ";
    String byAll = " Tab.Ledger_grp_id,Tab.ledger_id, Tab.ledger_group, Tab.ledger ";
    String apnd = (type.equals(SystemConstants.BY_GROUP)) ? byGroup : byAll;
    String order = (type.equals(SystemConstants.BY_GROUP)) ? "" : " ORDER BY Tab.ledger_id ";
    String sql = "SELECT " + apnd + ", SUM(Tab.open_dr)- SUM(Tab.open_cr) as opening_balance,\n"
            + "SUM(Tab.amt_dr) as debit, SUM(Tab.amt_cr)  as credit FROM\n"
            + "(SELECT FinAccTransDr.entry_date,FinAccTransDr.id as accounting_transaction_id, FinAccGrp.id as Ledger_grp_id,FinAccLedg.id ledger_id,FinAccGrp.title as ledger_group,FinAccLedg.title as ledger,\n"
            + "CASE WHEN FinAccTransDr.entry_date::date < ?::date THEN detail.debit  ELSE 0 END as open_dr,\n"
            + "0 as open_cr,\n"
            + "CASE WHEN FinAccTransDr.entry_date::date >= ?::date THEN detail.debit  ELSE 0 END as amt_dr,\n"
            + "0 as amt_cr\n"
            + "FROM fin_accounting_group as FinAccGrp,fin_accounting_ledger as FinAccLedg,\n"
            + "fin_accounting_transaction as FinAccTransDr, fin_accounting_transaction_detail as detail\n"
            + "WHERE FinAccGrp.id = FinAccLedg.accounting_group_id  AND FinAccLedg.id =detail.accounting_ledger_id  AND FinAccLedg.company_id =? AND\n"
            + "FinAccTransDr.entry_date::date <= ?::date and detail.accounting_transaction_id = FinAccTransDr.id \n"
            + "UNION ALL \n"
            + "SELECT FinAccTransCr.entry_date,FinAccTransCr.id as accounting_transaction_id, FinAccGrp.id as Ledger_grp_id,FinAccLedg.id ledger_id,FinAccGrp.title as ledger_group,FinAccLedg.title as ledger,\n"
            + "0 as open_dr,\n"
            + "CASE WHEN FinAccTransCr.entry_date::date < ?::date THEN detail.credit  ELSE 0 END as open_cr,\n"
            + "0 as amt_dr,\n"
            + "CASE WHEN FinAccTransCr.entry_date::date >= ?::date THEN detail.credit  ELSE 0 END as amt_cr \n"
            + "FROM fin_accounting_group as FinAccGrp,fin_accounting_ledger as FinAccLedg,\n"
            + "fin_accounting_transaction as FinAccTransCr,\n"
            + "        fin_accounting_transaction_detail as detail\n"
            + "WHERE FinAccGrp.id = FinAccLedg.accounting_group_id  AND FinAccLedg.id =detail.accounting_ledger_id  AND FinAccLedg.company_id =? AND\n"
            + "FinAccTransCr.entry_date::date <=?::date and detail.accounting_transaction_id = FinAccTransCr.id ) \n"
            + "As Tab GROUP BY " + apnd + order;
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getFromDate()));
    main.param(companyId);
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getToDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getFromDate()));
    main.param(companyId);
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getToDate()));
    return AppDb.getList(main.dbConnector(), TrialBalanceReport.class, sql, main.getParamData().toArray());

  }

}
