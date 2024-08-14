/*
 * @(#)AccountingLedgerService.java	1.0 Fri Feb 24 15:58:34 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingLedgerBalance;
import spica.fin.domain.AccountingTransactionDetail;
import spica.scm.domain.Company;
import spica.fin.common.FilterObjects;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.AppView;
import wawo.entity.core.AppDb;
import wawo.entity.core.Sql;
import wawo.entity.core.SqlPage;
import wawo.entity.core.SqlSearch;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.DateUtil;
import wawo.entity.util.StringUtil;

/**
 * AccountingLedgerService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:34 IST 2017
 */
public abstract class AccountingLedgerTransactionService {

  public static final String JOURNAL_POPUP = "/accounting/accounting_transaction.xhtml";
  public static final String BANK_RECONCILIATION_POPUP = "/accounting/bank_reconciliation.xhtml";
  public static final String BANK_CHARGES_POPUP = "/accounting/bank_charges.xhtml";
  public static final String LEDGER_TRANSACTION_POPUP = "/accounting/accounting_ledger_transaction.xhtml";

  /**
   * AccountingLedger paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getLedgerTransactionSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("", AccountingTransactionDetail.class, main);
    sql.main("select t1.accounting_ledger_id as id, t1.accounting_ledger_id, t2.title, t3.title, sum(t1.debit) as debit, sum(t1.credit) as credit from fin_accounting_transaction_detail t1 "); //Main query
    sql.count("select count(distinct t1.accounting_ledger_id) as total from fin_accounting_transaction_detail t1 "); //Count query
    sql.join("left outer join fin_accounting_ledger t2 on (t2.id = t1.accounting_ledger_id) "); //Join Query
    sql.join("left outer join fin_accounting_group t3 on (t3.id = t2.accounting_group_id) "); //Join Query
    sql.join("left outer join fin_accounting_transaction tran on (tran.id = t1.accounting_transaction_id) "); //Join Query
    sql.string(new String[]{"t2.title"}); //String search or sort fields
    sql.number(new String[]{"t2.opening_balance", "debit", "credit"}); //Numeric search or sort fields

    return sql;
  }

  /**
   *
   * @param main
   * @return
   */
  private static SqlSearch getLedgerTransactionSumSqlPaged(Main main) {
    SqlSearch sql = AppService.sqlSearch("fin_accounting_transaction_detail", AccountingTransactionDetail.class, main);
    sql.main("select row_number() OVER () as id, sum(t1.debit) as debit, sum(t1.credit) as credit from fin_accounting_transaction_detail t1 "); //Main query
    sql.join("left outer join fin_accounting_ledger t2 on (t2.id = t1.accounting_ledger_id) "); //Join Query
    sql.join("left outer join fin_accounting_group t3 on (t3.id = t2.accounting_group_id) "); //Join Query
    sql.join("left outer join fin_accounting_transaction tran on (tran.id = t1.accounting_transaction_id) "); //Join Query
    return sql;
  }

  /**
   * Return List of AccountingTransactionDetail.
   *
   * @param main
   * @return List of AccountingTransactionDetail
   */
  public static final List<AccountingTransactionDetail> listPaged(Main main, Company company, FilterObjects filterObjects) {
    SqlPage sql = getLedgerTransactionSqlPaged(main);
    whereConditions(main, sql, company, filterObjects, true);
    return AppService.listPagedJpa(main, sql);
  }

  /**
   *
   * @param main
   * @param company
   * @param selectedYear
   * @param selectedMonth
   * @return
   */
  public static final List<AccountingTransactionDetail> getSum(Main main, Company company, FilterObjects filterObjects) {
    SqlSearch sql = getLedgerTransactionSumSqlPaged(main);
    whereConditions(main, sql, company, filterObjects, false);
    return AppService.listAll(main, sql);
  }

  public static final List<AccountingLedger> listRelatedLedger(Main main, AccountingLedger ledger, FilterObjects filterObjects) {
    String sql = "select id, title, accounting_group_id from fin_accounting_ledger "
            + " where id in (select accounting_ledger_id from fin_accounting_transaction_detail "
            + " where accounting_transaction_id in(select t1.accounting_transaction_id from fin_accounting_transaction_detail t1 "
            + " left outer join fin_accounting_transaction tran on (tran.id = t1.accounting_transaction_id)"
            + " where t1.accounting_ledger_id=? "
            + " and to_char(tran.entry_date, 'yyyy-mm-dd') between to_char(?::date, 'yyyy-mm-dd') and to_char(?::date, 'yyyy-mm-dd')"
            + ")) "
            + " and id !=? and accounting_group_id in(?,?) order by lower(title)";
    return AppService.list(main, AccountingLedger.class, sql, new Object[]{ledger.getId(), filterObjects.getFromDate(), filterObjects.getToDate(), ledger.getId(), AccountingConstant.GROUP_SUNDRY_DEBTORS.getId(), AccountingConstant.GROUP_SUNDRY_CREDITORS.getId()});
  }

  /**
   * Return ledger list and closing balance.
   *
   * @param main
   * @param company
   * @param ledger
   * @param filterObjects
   * @return
   */
  public static List<AccountingLedgerBalance> getLedgerbalanceList(Main main, AccountingLedger ledger, FilterObjects filterObjects) {
    return getLedgerbalance(main, ledger, filterObjects, false);
  }

  /**
   * Return ledger.getOpeningBalance() closing balance.
   *
   * @param main
   * @param company
   * @param ledger
   * @return
   */
  public static void getLedgerBalanceClosing(Main main, AccountingLedger ledger, FilterObjects filterObjects) {
    FilterObjects f = new FilterObjects();
    f.setFromDate(filterObjects.getToDate());
    getLedgerbalance(main, ledger, f, true);
  }

  public static void getLedgerBalance(Main main, AccountingLedger ledger) {
    FilterObjects filterObjects = new FilterObjects();
    filterObjects.setToDate(new Date());
    setLedgerbalance(main, ledger);
  }

  public static void setLedgerbalance(Main main, AccountingLedger ledger) {
    ledger.setBalanceDrOrCr(main.em().countDouble("select (COALESCE(SUM(t1.debit), 0) - COALESCE(SUM(t1.credit), 0)) total from fin_accounting_transaction_detail t1 where t1.accounting_ledger_id=?", new Object[]{ledger.getId()}));
  }

  private static List<AccountingLedgerBalance> getLedgerbalance(Main main, AccountingLedger ledger, FilterObjects filterObjects, boolean currentBalanceOnly) {
    main.getParamData().clearParam();
    main.param(ledger.getId());
    main.param(ledger.getCompanyId().getId());
    main.param(ledger.getCompanyId().getId());
    main.param(filterObjects.getFromDate());
    main.param(filterObjects.getToDate());
    String sql;
    //filterObjects.setOpeningBalance(0.00); //store the opening balance in this field
    String cond = "and tran.company_id=? and t3.company_id=? and to_char(tran.entry_date, 'yyyy-mm-dd') between to_char(?::date, 'yyyy-mm-dd') and to_char(?::date, 'yyyy-mm-dd')";
    //TODO condition applied twice to solve the
    String condCnt = "and tran.company_id=? and t3.company_id=? and to_char(tran.entry_date, 'yyyy-mm-dd') < to_char(?::date, 'yyyy-mm-dd') and to_char(tran.entry_date, 'yyyy-mm-dd') < to_char(?::date, 'yyyy-mm-dd')";

    if (filterObjects.getSelectedAccountGroup() != null) {
      main.param(filterObjects.getSelectedAccountGroup().getId());
      cond += " and (tran.account_group_id = ?)";
      condCnt += " and (tran.account_group_id = ?)";
    }

    String join = " from fin_accounting_ledger led, "
            + "(select t1.accounting_transaction_id,t1.credit,t1.debit,tran.ledger_debit_id,tran.ledger_credit_id,tran.entry_date,tran.ref_document_no,tran.document_number,tran.narration,tran.total_amount,vt.title,tran.processed_at, "
            + "CASE WHEN (t1.credit <= 0 or t1.credit is null) THEN tran.ledger_credit_id ELSE tran.ledger_debit_id end as lid,tran.id "
            + "from fin_accounting_transaction_detail t1,fin_accounting_transaction tran ,fin_accounting_ledger t3,fin_accounting_voucher_type vt "
            + "where t1.accounting_transaction_id=tran.id and vt.id=tran.voucher_type_id "
            + "and ( CASE WHEN (t1.credit <= 0 or t1.credit is null) THEN t3.id=tran.ledger_debit_id ELSE t3.id=tran.ledger_credit_id end) "
            + "and t1.accounting_ledger_id=? ";

    String filter = setFilters(main, filterObjects, true);
    sql = "select tab.id,led.title cr_title,led.title dr_title,tab.credit, tab.debit ,tab.title voucherType,tab.id voucherTypeId,tab.processed_at, "
            + "CASE WHEN (tab.credit <= 0 or tab.credit is null) THEN 'dr' ELSE 'cr' end as trtype, "
            + "tab.entry_date,tab.ref_document_no,tab.document_number,tab.narration,led.id ledger_id " + join + cond + ")tab where tab.lid=led.id " + filter;
    sql += " order by tab.entry_date, tab.id";
    ledger.setBalanceDrOrCr(main.em().countDouble("select (COALESCE(SUM(tab.debit), 0) - COALESCE(SUM(tab.credit), 0)) total " + join + condCnt + ")tab where tab.lid=led.id " + filter, main.getParamData().toArray()));

//}
    List<AccountingLedgerBalance> list = null;
    if (!currentBalanceOnly) {
      list = AppDb.getList(main.dbConnector(), AccountingLedgerBalance.class, sql, main.getParamData().toArray());
    }
    boolean showOpen = true;
    if (!(StringUtil.gtDouble(ledger.getBalanceCredit(), 0.00) || StringUtil.gtDouble(ledger.getBalanceDebit(), 0.00))) {
      if (filterObjects.getFromDate() != null && DateUtil.getDate(filterObjects.getFromDate()) == DateUtil.getDate(ledger.getCompanyId().getFinancialYearStartDate()) && DateUtil.getMonth(filterObjects.getFromDate()) == DateUtil.getMonth(ledger.getCompanyId().getFinancialYearStartDate())) {
        showOpen = false;
        main.param(AccountingConstant.LEDGER_CODE_OPENING_ENTRY);
        ledger.setBalanceDrOrCr(main.em().countDouble("select (COALESCE(SUM(tab.debit), 0) - COALESCE(SUM(tab.credit), 0)) total " + join + cond + ")tab where tab.lid=led.id " + filter + " and led.ledger_code=?", main.getParamData().toArray()));
      }
    }
    if (showOpen) {
      AccountingLedgerBalance ol = new AccountingLedgerBalance();
      Double opb = (ledger.getBalanceCredit() != null) ? ledger.getBalanceCredit() : ledger.getBalanceDebit();
      if (opb != null && (opb > 0 || opb < 0)) {
        ol.setVoucherType("Opening");
        ol.setDocumentNumber("OPB");
        ol.setEntryDate(filterObjects.getFromDate());
        if (ledger.getBalanceCredit() != null) {
          ol.setColor(AccountingConstant.COLOR_CR);
          ol.setBalanceLine("-" + AppView.formatDecimal(opb, 2));
          ol.setDrTitle("Opening Balance");
          ol.setTrtype("cr");
          ol.setCredit(opb);
        } else {
          ol.setColor(AccountingConstant.COLOR_DR);
          ol.setBalanceLine(AppView.formatDecimal(opb, 2));
          ol.setCrTitle("Opening Balance");
          ol.setTrtype("dr");
          ol.setDebit(opb);
        }
        list.add(0, ol);
      }
    }

    return list;
  }

  private static String setFilters(Main main, FilterObjects filterObjects, boolean tab) {
    String sql = "";

    if (filterObjects.getSelectedLedger() != null) {
      sql += " and led.id = ? ";
      main.param(filterObjects.getSelectedLedger().getId());
    }

    String table = "tran";
    if (tab) {
      table = "tab";
    }
    if (!StringUtil.isEmpty(main.getPageData().getSearchKeyWord())) {
      sql += " and (" + table + ".narration like ?";
      main.param("%" + main.getPageData().getSearchKeyWord() + "%");
      if (tab) {
        sql += " or " + table + ".credit::text like ?";
        main.param("%" + main.getPageData().getSearchKeyWord() + "%");
        sql += " or " + table + ".debit::text like ?";
        main.param("%" + main.getPageData().getSearchKeyWord() + "%");
      } else {
        sql += " or " + table + ".total_amount::text like ?";
        main.param("%" + main.getPageData().getSearchKeyWord() + "%");
      }
      sql += " or " + table + ".entry_date::text like ?";
      main.param("%" + main.getPageData().getSearchKeyWord() + "%");
      sql += " or " + table + ".document_number::text like ? )";
      main.param("%" + main.getPageData().getSearchKeyWord() + "%");
    }
    return sql;
  }

  /**
   *
   * @param main
   * @param id
   * @return
   */
  public static final AccountingLedger getLedgerById(Main main, Integer id) {
    return (AccountingLedger) AppService.single(main, AccountingLedger.class, "select * from fin_accounting_ledger where id=?", new Object[]{id});
  }

  /**
   *
   * @param man
   * @param filterObjects
   * @return
   */
//  public static final Double getOpeningBalance(Main main, Company company, AccountingLedger ledger, FilterObjects filterObjects) {
//    Long sum = 0l;
//    main.clear();
//    main.param(company.getId());
//    main.param(ledger.getId());
//    main.param(filterObjects.getFromDate());
//
//    String sql = " select (COALESCE(SUM(debit), 0) - COALESCE(SUM(credit), 0)) from fin_accounting_transaction_detail t1 ";
//    sql += "left outer join fin_accounting_transaction tran on (tran.id = t1.accounting_transaction_id) ";
//
//    String where = "where tran.company_id=? and t1.accounting_ledger_id = ? and to_char(tran.entry_date, 'yyyy-mm-dd') < to_char(?::date, 'yyyy-mm-dd') ";
//    if (filterObjects.getSelectedLedger() != null) {
//      // sql += "left outer join fin_accounting_ledger tran on (tran.id = t1.accounting_ledger_id) ";
//      where += "and (tran.ledger_credit_id=? or tran.ledger_debit_id=?)"; //TODO this query not correct 
//      main.param(filterObjects.getSelectedLedger().getId());
//      main.param(filterObjects.getSelectedLedger().getId());
//    }
//
//    if (filterObjects.getSelectedAccountGroup() != null) {
//      where += getAccountGroupQuery(main, filterObjects);
//    }
//    sum = AppService.count(main, sql + where, main.getParamData().toArray());
//    if (sum == 0) {
//      return 0.00;
//    }
//    return sum.doubleValue();
//  }
  /**
   *
   * @param sql
   * @param company
   * @param selectedYear
   * @param selectedMonth
   */
  private static final void whereConditions(Main main, Sql sql, Company company, FilterObjects filterObjects, boolean isGroupBy) {

    sql.cond("where t2.company_id = ?");
    sql.param(company.getId());
    if (filterObjects.getSelectedAccountingGroup() == null) {
      sql.cond(" and to_char(tran.entry_date, 'yyyy-mm-dd') between to_char(?::date, 'yyyy-mm-dd') and to_char(?::date, 'yyyy-mm-dd')");
    } else {
      sql.cond(" and t3.id=? and to_char(tran.entry_date, 'yyyy-mm-dd') between to_char(?::date, 'yyyy-mm-dd') and to_char(?::date, 'yyyy-mm-dd') ");
      sql.param(filterObjects.getSelectedAccountingGroup().getId());
    }
    sql.param(filterObjects.getFromDate());
    sql.param(filterObjects.getToDate());

    if (isGroupBy) {
      if (!StringUtil.isEmpty(main.getPageData().getSortField())) {
        sql.groupBy("t1.accounting_ledger_id,t2.title, t3.title");
      } else {
        sql.groupBy("t1.accounting_ledger_id,t2.title, t3.title");
      }
    }
  }

  /**
   * Select AccountingLedger by key.
   *
   * @param main
   * @param accountingLedger
   * @return AccountingLedger
   */
  public static final AccountingLedger
          selectByPk(Main main, AccountingLedger accountingLedger) {
    return (AccountingLedger) AppService.find(main, AccountingLedger.class,
            accountingLedger.getId());
  }

  /**
   * Insert AccountingLedger.
   *
   * @param main
   * @param accountingLedger
   */
  public static final void insert(Main main, AccountingLedger accountingLedger) {
    insertAble(main, accountingLedger);  //Validating
    AppService.insert(main, accountingLedger);

  }

  /**
   * Update AccountingLedger by key.
   *
   * @param main
   * @param accountingLedger
   * @return AccountingLedger
   */
  public static final AccountingLedger updateByPk(Main main, AccountingLedger accountingLedger) {
    updateAble(main, accountingLedger); //Validating
    return (AccountingLedger) AppService.update(main, accountingLedger);
  }

  /**
   * Insert or update AccountingLedger
   *
   * @param main
   * @param accountingLedger
   */
  public static void insertOrUpdate(Main main, AccountingLedger accountingLedger) {
    if (accountingLedger.getId() == null) {
      insert(main, accountingLedger);
    } else {
      updateByPk(main, accountingLedger);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingLedger
   */
  public static void clone(Main main, AccountingLedger accountingLedger) {
    accountingLedger.setId(null); //Set to null for insert
    insert(main, accountingLedger);
  }

  /**
   * Delete AccountingLedger.
   *
   * @param main
   * @param accountingLedger
   */
  public static final void deleteByPk(Main main, AccountingLedger accountingLedger) {
    deleteAble(main, accountingLedger); //Validation
    AppService.delete(main, AccountingLedger.class, accountingLedger.getId());
  }

  /**
   * Delete Array of AccountingLedger.
   *
   * @param main
   * @param accountingLedger
   */
  public static final void deleteByPkArray(Main main, AccountingLedger[] accountingLedger) {
    for (AccountingLedger e : accountingLedger) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param accountingLedger
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingLedger accountingLedger) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param accountingLedger
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingLedger accountingLedger) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_ledger where upper(entity_id)=?", new Object[]{StringUtil.toUpperCase(accountingLedger.getEntityId())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param accountingLedger
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingLedger accountingLedger) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_ledger where upper(entity_id)=? and id !=?", new Object[]{StringUtil.toUpperCase(accountingLedger.getEntityId()), accountingLedger.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  public static final void selectLedgerBalance(Main main, AccountingLedger accountingLedger) {
    String sql = "select (SUM(coalesce(debit,0)) - SUM(coalesce(credit,0))) as balance from fin_accounting_transaction_detail where accounting_ledger_id = ?";
    Double balance = AppService.countDouble(main, sql, new Object[]{accountingLedger.getId()});
    if (balance <= 0) {
      accountingLedger.setBalanceCredit(balance);
    } else {
      accountingLedger.setBalanceDebit(balance);
    }
  }
}
