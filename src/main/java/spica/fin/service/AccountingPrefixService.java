/*
 * @(#)FinAccountingPrefixService.java	1.0 Thu May 11 17:20:19 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.fin.domain.AccountingPrefix;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingVoucherType;
import spica.scm.domain.Company;
import spica.sys.common.DocumentNumberGen;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;

/**
 * FinAccountingPrefixService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu May 11 17:20:19 IST 2017
 */
public abstract class AccountingPrefixService {

  /**
   * FinAccountingPrefix paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getFinAccountingPrefixSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_prefix", AccountingPrefix.class, main);
    sql.main("select fin_accounting_prefix.id,fin_accounting_prefix.doc_prefix,fin_accounting_prefix.doc_number_counter,fin_accounting_prefix.padding,fin_accounting_prefix.year_sequence,"
            + "fin_accounting_prefix.year_padding,fin_accounting_prefix.start_year_only,fin_accounting_prefix.sort_order,fin_accounting_prefix.status_id,fin_accounting_prefix.created_by,fin_accounting_prefix.created_at,"
            + "fin_accounting_prefix.modified_by,fin_accounting_prefix.modified_at,fin_accounting_prefix.voucher_type_id,fin_accounting_prefix.company_id from fin_accounting_prefix fin_accounting_prefix "); //Main query
    sql.count("select count(fin_accounting_prefix.id) as total from fin_accounting_prefix fin_accounting_prefix "); //Count query
    sql.join("left outer join fin_accounting_voucher_type t2 on (t2.id = fin_accounting_prefix.voucher_type_id) ");
    //  + "left outer join scm_company t3 on (t3.id = fin_accounting_prefix.company_id)"); //Join Query

    sql.string(new String[]{"fin_accounting_prefix.doc_prefix", "t2.title", "fin_accounting_prefix.created_by", "fin_accounting_prefix.modified_by"}); //String search or sort fields
    sql.number(new String[]{"fin_accounting_prefix.id", "fin_accounting_prefix.doc_number_counter", "fin_accounting_prefix.padding", "fin_accounting_prefix.year_sequence", "fin_accounting_prefix.year_padding", "fin_accounting_prefix.sort_order", "fin_accounting_prefix.status_id"}); //Numeric search or sort fields
    sql.date(new String[]{"fin_accounting_prefix.created_at", "fin_accounting_prefix.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of FinAccountingPrefix.
   *
   * @param main
   * @return List of FinAccountingPrefix
   */
  public static final List<AccountingPrefix> listPaged(Main main, Company company) {
    if (company != null) {
      SqlPage sql = getFinAccountingPrefixSqlPaged(main);
      sql.cond("where fin_accounting_prefix.company_id = ? and financial_year_id = ?");
      sql.param(company.getId());
      sql.param(company.getCurrentFinancialYear().getId());
      return AppService.listPagedJpa(main, getFinAccountingPrefixSqlPaged(main));
    }
    return null;
  }

//  /**
//   * Return list of FinAccountingPrefix based on condition
//   * @param main
//   * @return List<FinAccountingPrefix>
//   */
//  public static final List<FinAccountingPrefix> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getFinAccountingPrefixSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select FinAccountingPrefix by key.
   *
   * @param main
   * @param finAccountingPrefix
   * @return FinAccountingPrefix
   */
  public static final AccountingPrefix selectByPk(Main main, AccountingPrefix finAccountingPrefix) {
    return (AccountingPrefix) AppService.find(main, AccountingPrefix.class, finAccountingPrefix.getId());
  }

  public static final AccountingPrefix selectByVoucherType(Main main, AccountingVoucherType accountingVoucherType, Company companyId) {
    return (AccountingPrefix) AppService.single(main, AccountingPrefix.class, "select * from fin_accounting_prefix where voucher_type_id=? and company_id=? and financial_year_id=?", new Object[]{accountingVoucherType.getId(), companyId.getId(), companyId.getCurrentFinancialYear().getId()});
  }

  /**
   * Insert FinAccountingPrefix.
   *
   * @param main
   * @param finAccountingPrefix
   */
  public static final void insert(Main main, AccountingPrefix finAccountingPrefix) {
    insertAble(main, finAccountingPrefix);  //Validating
    AppService.insert(main, finAccountingPrefix);

  }

  /**
   * Update FinAccountingPrefix by key.
   *
   * @param main
   * @param finAccountingPrefix
   * @return FinAccountingPrefix
   */
  public static final AccountingPrefix updateByPk(Main main, AccountingPrefix finAccountingPrefix) {
    updateAble(main, finAccountingPrefix); //Validating
    return (AccountingPrefix) AppService.update(main, finAccountingPrefix);
  }

  /**
   * Insert or update FinAccountingPrefix
   *
   * @param main
   * @param finAccountingPrefix
   */
  public static void insertOrUpdate(Main main, AccountingPrefix finAccountingPrefix) {
    if (finAccountingPrefix.getId() == null) {
      insert(main, finAccountingPrefix);
    } else {
      updateByPk(main, finAccountingPrefix);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param finAccountingPrefix
   */
  public static void clone(Main main, AccountingPrefix finAccountingPrefix) {
    finAccountingPrefix.setId(null); //Set to null for insert
    insert(main, finAccountingPrefix);
  }

  /**
   * Delete FinAccountingPrefix.
   *
   * @param main
   * @param finAccountingPrefix
   */
  public static final void deleteByPk(Main main, AccountingPrefix finAccountingPrefix) {
    deleteAble(main, finAccountingPrefix); //Validation
    AppService.delete(main, AccountingPrefix.class, finAccountingPrefix.getId());
  }

  /**
   * Delete Array of FinAccountingPrefix.
   *
   * @param main
   * @param finAccountingPrefix
   */
  public static final void deleteByPkArray(Main main, AccountingPrefix[] finAccountingPrefix) {
    for (AccountingPrefix e : finAccountingPrefix) {
      deleteByPk(main, e);
    }
  }

  public static String getPrefixOnSelect(Main main, AccountingTransaction at) {
    return getPrefix(main, at, false);
  }

  public static String getPrefixOnSave(Main main, AccountingTransaction at, Company company) {
    return getPrefix(main, at, true);
  }

  private static String getPrefix(Main main, AccountingTransaction tran, boolean onSave) {
    String sql = "select * from fin_accounting_prefix where voucher_type_id = ? and company_id = ? and financial_year_id=?";
    //   String sql = "select * from fin_accounting_prefix where voucher_type_id = ? and company_id = ?";
    //  String sqlUpdate = "update fin_accounting_prefix set modified_by=?, modified_at=?, doc_number_counter = (doc_number_counter + 1) where voucher_type_id = ? and company_id = ? ";
    AccountingPrefix prefix = null;
    main.clear();
    main.param(tran.getVoucherTypeId().getId());
    main.param(tran.getCompanyId().getId());
    main.param(tran.getCompanyId().getCurrentFinancialYear().getId());
    //int finMonth = wawo.entity.util.DateUtil.getMonth(company.getCurrentFinancialYear().getStartDate());
    if (onSave) {
      prefix = (AccountingPrefix) AppService.single(main, AccountingPrefix.class, sql, main.getParamData().toArray());
      if (prefix == null) {
        throw new UserMessageException("define.accounting.prefix", new String[]{tran.getCompanyId().getCurrentFinancialYear().getTitle()});
      }
      String documentNoChanged = DocumentNumberGen.getPrefixLiteral(prefix, tran.getCompanyId().getCurrentFinancialYear().getStartDate());
      //   AppService.updateSql(main, AccountingPrefix.class, sqlUpdate, true);
      if (tran.getDocumentNumber() != null && tran.getDocumentNumber().equals(documentNoChanged)) {
        return tran.getDocumentNumber();
      } else {
        prefix.setDocNumberCounter(prefix.getDocNumberCounter() + 1);
        AppService.update(main, prefix);
        return documentNoChanged;
      }
    } else {
      prefix = (AccountingPrefix) AppService.single(main, AccountingPrefix.class, sql, main.getParamData().toArray());
      if (prefix == null) {
        throw new UserMessageException("define.accounting.prefix", new String[]{tran.getCompanyId().getCurrentFinancialYear().getTitle()});
      }
      return "TMP/" + DocumentNumberGen.getPrefixLiteral(prefix, tran.getCompanyId().getCurrentFinancialYear().getStartDate());
    }
  }

  /**
   * Method to insert default Accounting prefixes.
   *
   * @param main
   * @param company
   */
  public static void insertCompanyAccountingPrefix(Main main, Company company) {
    List<AccountingPrefix> list = AppService.list(main, AccountingPrefix.class, "select * from fin_accounting_prefix where company_id is null order by voucher_type_id asc", null);
    for (AccountingPrefix accountingPrefix : list) {
      insert(main, getAccountingPrefix(accountingPrefix, company));
    }
  }

  /**
   *
   * @param accountingPrefix
   * @param company
   * @return
   */
  private static AccountingPrefix getAccountingPrefix(AccountingPrefix accountingPrefix, Company company) {
    AccountingPrefix prefix = new AccountingPrefix();
    prefix.setCompanyId(company);
    prefix.setDocNumberCounter(accountingPrefix.getDocNumberCounter());
    prefix.setDocPrefix(accountingPrefix.getDocPrefix());
    prefix.setPadding(accountingPrefix.getPadding());
    prefix.setVoucherTypeId(accountingPrefix.getVoucherTypeId());
    prefix.setYearPadding(accountingPrefix.getYearPadding());
    prefix.setYearSequence(accountingPrefix.getYearSequence());
    prefix.setFinancialYearId(company.getCurrentFinancialYear());
    return prefix;
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param finAccountingPrefix
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AccountingPrefix finAccountingPrefix) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param finAccountingPrefix
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingPrefix finAccountingPrefix) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_prefix where upper(doc_prefix)=?", new Object[]{StringUtil.toUpperCase(finAccountingPrefix.getDocPrefix())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param finAccountingPrefix
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingPrefix finAccountingPrefix) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_accounting_prefix where upper(doc_prefix)=? and id !=?", new Object[]{StringUtil.toUpperCase(finAccountingPrefix.getDocPrefix()), finAccountingPrefix.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
