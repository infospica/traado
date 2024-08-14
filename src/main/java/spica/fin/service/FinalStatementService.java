/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.fin.service;

import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.common.FinalStatement;
import spica.scm.domain.Company;
import spica.sys.SystemRuntimeConfig;
import spica.fin.common.FilterObjects;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.util.DateUtil;

/**
 *
 * @author sanith
 */
public class FinalStatementService {

  private static final String dateCond = "and t2.company_id=? and to_char(t1.entry_date, 'yyyy-mm') between to_char(?::date, 'yyyy-mm') and to_char(?::date, 'yyyy-mm')";

  //For trading account
//  public static List<FinalStatement> getTradingaccountExpenseList(MainView main, Company company, FilterObjects filterObjects) {
//
//    String sql = "select t.* from (select COALESCE(sum(t1.debit),0) - COALESCE(sum(t1.credit),0) total_amount,'dr' as trtype,'Purchase' title, 1 orderby from fin_accounting_transaction_detail t1, fin_accounting_ledger t2\n"
//            + "where t2.id=t1.accounting_ledger_id and t2.ledger_code in (" + AccountingConstant.LEDGER_CODE_PURCHASES + ") " + dateCond + "\n"
//            + " union\n"
//            + "select COALESCE(sum(t1.credit),0) - COALESCE(sum(t1.debit),0) total_amount,'cr' as trtype,'Less Purchase Return' title, 2 orderby from fin_accounting_transaction_detail t1, fin_accounting_ledger t2\n"
//            + "where t2.id=t1.accounting_ledger_id and t2.ledger_code in (" + AccountingConstant.LEDGER_CODE_PURCHASE_RETURNS + ") " + dateCond + "\n"
//            + " union \n"
//            + "select COALESCE(sum(t1.debit),0) - COALESCE(sum(t1.credit),0) total_amount,'exp' as trtype,t2.title, 3 orderby from fin_accounting_transaction_detail t1, fin_accounting_ledger t2\n"
//            + "where t2.id=t1.accounting_ledger_id and t2.accounting_group_id=? " + dateCond + " group by t2.title) t order by t.orderby";
//    return AppDb.getList(main.dbConnector(), FinalStatement.class, sql, new Object[]{company.getId(), filterObjects.getFromDate(), filterObjects.getToDate(), company.getId(), filterObjects.getFromDate(), filterObjects.getToDate(), AccountingConstant.GROUP_DIRECT_EXPENSE.getId(), company.getId(), filterObjects.getFromDate(), filterObjects.getToDate()});
//  }
//
//  public static List<FinalStatement> getTradingaccountIncomeList(MainView main, Company company, FilterObjects filterObjects) {
//    String sql = "select t.* from (select COALESCE(sum(t1.credit),0) - COALESCE(sum(t1.debit),0) total_amount,'dr' as trtype,'Sales' title, 1 orderby from fin_accounting_transaction_detail t1, fin_accounting_ledger t2\n"
//            + "where t2.id=t1.accounting_ledger_id  and t2.ledger_code in (" + AccountingConstant.LEDGER_CODE_SALESS + ") " + dateCond + " \n"
//            + "union\n"
//            + "select COALESCE(sum(t1.debit),0) - COALESCE(sum(t1.credit),0) total_amount,'cr' as trtype,'Less Sales Return' title, 2 orderby from fin_accounting_transaction_detail t1, fin_accounting_ledger t2\n"
//            + "where t2.id=t1.accounting_ledger_id and t2.ledger_code in ('" + AccountingConstant.LEDGER_CODE_SALES_RETURN + "') " + dateCond + " \n"
//            + "union \n"
//            + "select COALESCE(sum(t1.credit),0) - COALESCE(sum(t1.debit),0) total_amount,'exp' as trtype,t2.title, 3 orderby from fin_accounting_transaction_detail t1, fin_accounting_ledger t2\n"
//            + "where t2.id=t1.accounting_ledger_id and t2.accounting_group_id=? " + dateCond + "  group by t2.title) t order by t.orderby";
//    return AppDb.getList(main.dbConnector(), FinalStatement.class, sql, new Object[]{company.getId(), filterObjects.getFromDate(), filterObjects.getToDate(), company.getId(), filterObjects.getFromDate(), filterObjects.getToDate(), AccountingConstant.GROUP_DIRECT_INCOME.getId(), company.getId(), filterObjects.getFromDate(), filterObjects.getToDate()});
//  }
  public static final Double getClosingBalance(Main main, Company company, FilterObjects filterObjects) {
    String sql;
    main.clear();
    filterObjects.setToDate(DateUtil.moveDays(filterObjects.getToDate(), 1)); // getClosingStockByCAP works on LESS THAN DATE so to Conider selected date need to add 1 day
    int day = filterObjects.getToDay();
    int month = filterObjects.getToMonth();
    int year = filterObjects.getToYear();
//    if (month == 11) {
//      year = year + 1;
//      month = 1;
//    } else {
    month += 1; //To get the currents months add two more
    // }
    main.param(company.getId());
    if (filterObjects.getSelectedAccount() != null) {
      sql = "SELECT sum(quantity_available*tot_value) FROM getClosingStockByCAP(?,?, null, ?,?,?)";
      main.param(filterObjects.getSelectedAccount().getId());
    } else {
      sql = "SELECT sum(quantity_available*tot_value) FROM getClosingStockByCAP(?,null, null, ?,?,?)";
    }
    main.param(day);
    main.param(month);
    main.param(year);
    filterObjects.setToDate(DateUtil.moveDays(filterObjects.getToDate(), -1));
    return AppService.countDouble(main, sql, main.getParamData().toArray());
  }

  public static final Double getOpeningBalance(Main main, Company company, FilterObjects filterObjects) {
    String sql;
    main.clear();
    int month = filterObjects.getFromMonth();
    int year = filterObjects.getFromYear();
    month += 1;
    main.param(company.getId());
    if (filterObjects.getSelectedAccount() != null) {
      sql = "SELECT sum(quantity_available*tot_value) FROM getClosingStockByCAP(?,?, null, null,?,?)";
      main.param(filterObjects.getSelectedAccount().getId());
    } else {
      sql = "SELECT sum(quantity_available*tot_value) FROM getClosingStockByCAP(?,null, null, null,?,?)";
    }
    main.param(month);
    main.param(year);
    return AppService.countDouble(main, sql, main.getParamData().toArray());
  }

  // for P&L account
  public static List<FinalStatement> getPandLaccountExpenseList(MainView main, Company company, FilterObjects filterObjects) {
    main.clear();
    String sql = "select t2.id as ledger_id,COALESCE(sum(t1.debit),0) - COALESCE(sum(t1.credit),0) total_amount,'exp' as trtype,t2.title, 3 orderby from fin_accounting_transaction_detail t1, fin_accounting_ledger t2 ,fin_accounting_transaction t3 \n"
            + "where t2.id=t1.accounting_ledger_id and t2.accounting_group_id=? " + dateCond
            + " and t1.accounting_transaction_id = t3.id  ";
    Integer id = null;
    if (filterObjects.getSelectedAccount() != null && filterObjects.getSelectedAccount().getId() != null) {
      if (filterObjects.getSelectedAccountGroup() == null || filterObjects.getSelectedAccountGroup().getId() == null) {
        sql += " AND t3.account_group_id in ( select account_group_id from scm_account_group_detail where account_id = ? ) ";
        id = filterObjects.getSelectedAccount().getId();
      } else {
        sql += " AND t3.account_group_id = ? \n";
        id = filterObjects.getSelectedAccountGroup().getId();
      }
    }
    sql += " group by t2.title,t2.id ";
    main.param(AccountingConstant.GROUP_INDIRECT_EXPENSE.getId());
    main.param(company.getId());
    main.param(filterObjects.getFromDate());
    main.param(filterObjects.getToDate());
    if (id != null) {
      main.param(id);
    }
    return AppDb.getList(main.dbConnector(), FinalStatement.class, sql, main.getParamData().toArray());
  }

  public static List<FinalStatement> getPandLaccountIncomeList(MainView main, Company company, FilterObjects filterObjects) {
    main.clear();
    String sql = "select t2.id as ledger_id, COALESCE(sum(t1.credit),0) - COALESCE(sum(t1.debit),0)  total_amount,'exp' as trtype,t2.title, 3 orderby from fin_accounting_transaction_detail t1, fin_accounting_ledger t2,fin_accounting_transaction t3 \n"
            + "where t2.id=t1.accounting_ledger_id and t2.accounting_group_id=? " + dateCond
            + " and t1.accounting_transaction_id = t3.id  ";
    Integer id = null;
    if (filterObjects.getSelectedAccount() != null && filterObjects.getSelectedAccount().getId() != null) {
      if (filterObjects.getSelectedAccountGroup() == null || filterObjects.getSelectedAccountGroup().getId() == null) {
        sql += " AND t3.account_group_id in ( select account_group_id from scm_account_group_detail where account_id = ? ) ";
        id = filterObjects.getSelectedAccount().getId();
      } else {
        sql += " AND t3.account_group_id = ? \n";
        id = filterObjects.getSelectedAccountGroup().getId();
      }
    }
    sql += " group by t2.title,t2.id ";
    main.param(AccountingConstant.GROUP_INDIRECT_INCOME.getId());
    main.param(company.getId());
    main.param(filterObjects.getFromDate());
    main.param(filterObjects.getToDate());
    if (id != null) {
      main.param(id);
    }
    return AppDb.getList(main.dbConnector(), FinalStatement.class, sql, main.getParamData().toArray());
  }

  //For balance sheet
  private static final String dateLess = "and t2.company_id=? and to_char(t1.entry_date, 'yyyy-mm') <= to_char(?::date, 'yyyy-mm')  and t1.accounting_transaction_id = t3.id  ";

  public static List<FinalStatement> getBalanceSheetAssets(MainView main, Company company, FilterObjects filterObjects) {
    main.clear();
    String bSheetSql = "SELECT AG.id,AG.parent_id,case when HT.title is null then AG.title else HT.title end as head_title,AG.title ,coalesce(sum(t1.debit),0) - coalesce(sum(t1.credit),0) total_amount\n"
            + "FROM fin_accounting_transaction_detail t1,fin_accounting_ledger t2,fin_accounting_transaction t3 ,fin_accounting_group AG \n"
            + "LEFT OUTER JOIN fin_accounting_group HT ON AG.parent_id =HT.id\n"
            + "WHERE t2.id=t1.accounting_ledger_id AND t2.accounting_group_id = AG.id \n"
            + "and ((HT.accounting_head_id in (?)  and AG.accounting_head_id in (?)) "
            + "OR (AG.parent_id is null and AG.accounting_head_id in (?))) "
            + dateLess;
    Integer id = null;
    if (filterObjects.getSelectedAccount() != null && filterObjects.getSelectedAccount().getId() != null) {
      if (filterObjects.getSelectedAccountGroup() == null || filterObjects.getSelectedAccountGroup().getId() == null) {
        bSheetSql += " AND t3.account_group_id in ( select account_group_id from scm_account_group_detail where account_id = ? ) \n";
        id = filterObjects.getSelectedAccount().getId();
      } else {
        bSheetSql += " AND t3.account_group_id = ? \n";
        id = filterObjects.getSelectedAccountGroup().getId();
      }
    }

    bSheetSql += "GROUP BY HT.title,AG.title,HT.sort_order, AG.sort_order,AG.id,AG.parent_id \n"
            + "ORDER BY HT.sort_order, AG.sort_order";
    main.param(AccountingConstant.HEAD_ASSET.getId());
    main.param(AccountingConstant.HEAD_ASSET.getId());
    main.param(AccountingConstant.HEAD_ASSET.getId());
    main.param(company.getId());
    main.param(filterObjects.getToDate());
    if (id != null) {
      main.param(id);
    }
    return AppDb.getList(main.dbConnector(), FinalStatement.class, bSheetSql, main.getParamData().toArray());
  }

  public static List<FinalStatement> getBalanceSheetLiability(MainView main, Company company, FilterObjects filterObjects) {
    main.clear();
    String bSheetSql = "SELECT AG.id,AG.parent_id,case when HT.title is null then AG.title else HT.title end as head_title,AG.title ,coalesce(sum(t1.debit),0) - coalesce(sum(t1.credit),0) total_amount\n"
            + "FROM fin_accounting_transaction_detail t1,fin_accounting_ledger t2,fin_accounting_transaction t3 , fin_accounting_group AG \n"
            + "LEFT OUTER JOIN fin_accounting_group HT ON AG.parent_id =HT.id\n"
            + "WHERE t2.id=t1.accounting_ledger_id AND t2.accounting_group_id = AG.id \n"
            + "and ((HT.accounting_head_id in (?,?)  and AG.accounting_head_id in (?,?)) "
            + "OR (AG.parent_id is null and AG.accounting_head_id in (?,?))) "
            + dateLess;
    Integer id = null;
    if (filterObjects.getSelectedAccount() != null && filterObjects.getSelectedAccount().getId() != null) {
      if (filterObjects.getSelectedAccountGroup() == null || filterObjects.getSelectedAccountGroup().getId() == null) {
        bSheetSql += " AND t3.account_group_id in ( select account_group_id from scm_account_group_detail where account_id = ? ) \n";
        id = filterObjects.getSelectedAccount().getId();
      } else {
        bSheetSql += " AND t3.account_group_id = ? \n";
        id = filterObjects.getSelectedAccountGroup().getId();
      }
    }
    bSheetSql += "GROUP BY HT.title,AG.title,HT.sort_order, AG.sort_order,AG.id,AG.parent_id \n"
            + "ORDER BY HT.sort_order, AG.sort_order";
    main.param(AccountingConstant.HEAD_LIABILITY.getId());
    main.param(AccountingConstant.HEAD_CAPITAL.getId());
    main.param(AccountingConstant.HEAD_LIABILITY.getId());
    main.param(AccountingConstant.HEAD_CAPITAL.getId());
    main.param(AccountingConstant.HEAD_LIABILITY.getId());
    main.param(AccountingConstant.HEAD_CAPITAL.getId());
    main.param(company.getId());
    main.param(filterObjects.getToDate());
    if (id != null) {
      main.param(id);
    }
    return AppDb.getList(main.dbConnector(), FinalStatement.class, bSheetSql, main.getParamData().toArray());
  }

  public static List getPuchaseAccountlist(MainView main, Company company, FilterObjects filterObjects) {
    return getTradinglist(main, company, filterObjects, false, AccountingConstant.GROUP_PURCHASE_ACCOUNT.getId(), AccountingConstant.GROUP_DIRECT_EXPENSE.getId());
  }

  public static List getSalesAccountlist(MainView main, Company company, FilterObjects filterObjects) {
    return getTradinglist(main, company, filterObjects, true, AccountingConstant.GROUP_SALES_ACCOUNT.getId(), AccountingConstant.GROUP_DIRECT_INCOME.getId());
  }

  private static List getTradinglist(MainView main, Company company, FilterObjects filterObjects, boolean sales, Integer id1, Integer id2) {
    main.clear();

    main.param(company.getId());
    main.param(id1);
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getToDate()));

    String cond = "g.id as Ledger_grp_id,led.id ledger_id,g.title as ledger_group,led.title as title,";
    String frmQry = "";
    if (sales) {
      if (filterObjects.getSelectedAccount() != null && filterObjects.getSelectedAccount().getId() != null) {
        frmQry = ",fin_trade_outstanding as trados ";
        cond += " COALESCE(sum(trados.value_after_tax),0) total_amount";
      } else {
        cond += " COALESCE(sum(detail.credit),0) - COALESCE(sum(detail.debit),0) total_amount";
      }
    } else {
      cond += " COALESCE(sum(detail.debit),0) - COALESCE(sum(detail.credit),0) total_amount";
    }
    cond += " FROM fin_accounting_group as g,fin_accounting_ledger as led, fin_accounting_transaction as tran, fin_accounting_transaction_detail as detail"
            + frmQry
            + " WHERE led.accounting_group_id = g.id  AND led.id = detail.accounting_ledger_id  and detail.accounting_transaction_id = tran.id "
            + " and tran.company_id = ? and g.id=? and (tran.entry_date::date >= ?::date and tran.entry_date::date <= ?::date)";

    boolean ag = false, a = false;
    if (filterObjects.getSelectedAccountGroup() != null && filterObjects.getSelectedAccountGroup().getId() != null) {
      cond += " and tran.account_group_id = ? \n";
      main.param(filterObjects.getSelectedAccountGroup().getId());
      ag = true;
    } else if (filterObjects.getSelectedAccount() != null && (filterObjects.getSelectedAccount().getId() != null && filterObjects.getSelectedAccount().getId() != 0)) {
      if (!sales) {
        cond += " and tran.account_group_id in ( select account_group_id from scm_account_group_detail where account_id = ? ) ";
      } else {
        cond += " and trados.accounting_transaction_id in(tran.id) and  trados.account_id = ?";
      }
      main.param(filterObjects.getSelectedAccount().getId());
      a = true;
    }
    cond += " group by led.id, g.id ";

    String sql = "select tab.* from ( SELECT 'dr' as trtype,1 orderby, " + cond + " UNION SELECT 'exp' as trtype,2 orderby, " + cond + " ) tab order by tab.trtype, tab.total_amount desc";

    main.param(company.getId());
    main.param(id2);
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getFromDate()));
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterObjects.getToDate()));
    if (a) {
      main.param(filterObjects.getSelectedAccount().getId());
    } else if (ag) {
      main.param(filterObjects.getSelectedAccountGroup().getId());
    }
    //  System.out.print(">>" + sql);

    return AppDb.getList(main.dbConnector(), FinalStatement.class, sql, main.getParamData().toArray());

  }

  public static final Double getOpeningDamageAndExpiry(Main main, Integer companyId, FilterObjects filterObjects) {
    main.clear();
    String sql = null;
    Integer month = filterObjects.getFromMonth();
    Integer year = filterObjects.getFromYear();
    month += 1;
    main.param(companyId);
    if (filterObjects.getSelectedAccount() != null) {
      main.param(filterObjects.getSelectedAccount().getId());
      sql = "SELECT sum(tot_value) FROM getClosingDamExpStockByCAP(?,?,NULL,NULL,?,?)";
    } else {
      sql = "SELECT sum(tot_value) FROM getClosingDamExpStockByCAP(?,NULL,NULL,NULL,?,?)";
    }
    main.param(month);
    main.param(year);
    return AppService.countDouble(main, sql, main.getParamData().toArray());
  }

  public static final Double getClosingDamageAndExpiry(Main main, Integer companyId, FilterObjects filterObjects) {
    main.clear();
    String sql = null;
    filterObjects.setToDate(DateUtil.moveDays(filterObjects.getToDate(), 1)); // getClosingStockByCAP works on LESS THAN DATE so to Conider selected date need to add 1 day
    int day = filterObjects.getToDay();
    Integer month = filterObjects.getToMonth();
    Integer year = filterObjects.getToYear();
//    if (month == 11) {
//      year = year + 1;
//      month = 1;
//    } else {
    month += 1; //To get the currents months add 1 more
    //  }
    main.param(companyId);
    if (filterObjects.getSelectedAccount() != null) {
      main.param(filterObjects.getSelectedAccount().getId());
      sql = "SELECT sum(quantity_available*tot_value) FROM getClosingDamExpStockByCAP(?,?,NULL,?,?,?)";
    } else {
      sql = "SELECT sum(quantity_available*tot_value) FROM getClosingDamExpStockByCAP(?,NULL,NULL,?,?,?)";
    }
    main.param(day);
    main.param(month);
    main.param(year);
    filterObjects.setToDate(DateUtil.moveDays(filterObjects.getToDate(), -1));
    return AppService.countDouble(main, sql, main.getParamData().toArray());
  }
}
