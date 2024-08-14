/*
 * @(#)AccountingLedgerService.java	1.0 Fri Feb 24 15:58:34 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingEntityType;
import spica.fin.domain.AccountingGroup;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.TradeOutstanding;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Vendor;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * AccountingLedgerService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:34 IST 2017
 */
public abstract class AccountingLedgerService {

  /**
   * AccountingLedger paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountingLedgerSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_accounting_ledger", AccountingLedger.class, main);
    sql.main("select t1.id,t1.accounting_group_id,t1.entity_id,t1.accounting_entity_type_id,t1.title,t1.ledger_code,t1.company_id,t1.modified_at,t1.sort_order,t1.created_by,t1.modified_by,t1.created_at,t1.description,t1.opening_balance,t1.is_debit_or_credit,t1.billwise,t1.igst_id from fin_accounting_ledger t1 "); //Main query
    sql.count("select count(t1.id) as total from fin_accounting_ledger t1 "); //Count query
    sql.join("left outer join fin_accounting_group t2 on (t2.id = t1.accounting_group_id) "
            + "left outer join fin_accounting_entity_type t3 on (t3.id = t1.accounting_entity_type_id) "); //Join Query
    sql.orderBy("t1.sort_order,upper(t1.title)");
    sql.string(new String[]{"t2.title", "t3.title", "t1.title", "t1.created_by", "t1.modified_by"}); //String search or sort fields
    sql.number(new String[]{"t1.id", "t1.entity_id", "t1.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.modified_at", "t1.created_at"});  //Date search or sort fields
    return sql;

  }

  /**
   * Return List of AccountingLedger.
   *
   * @param main
   * @return List of AccountingLedger
   */
  public static final List<AccountingLedger> listPaged(Main main, Company company, AccountingGroup ag) {
    SqlPage sql = getAccountingLedgerSqlPaged(main);
    sql.cond("where t1.company_id = ?");
    sql.param(company.getId());
    if (ag != null) {
      sql.cond(" and t1.accounting_group_id=? ");
      sql.param(ag.getId());
    }
    return AppService.listPagedJpa(main, sql);
  }

  /**
   * Return list of AccountingLedger based on AccountingGroup
   *
   * @param main
   * @param company
   * @param accountingGroup
   * @return List<AccountingLedger>
   */
  public static final List<AccountingLedger> listPagedByGroup(Main main, Integer companyId, AccountingGroup accountingGroup) {
    SqlPage sql = getAccountingLedgerSqlPaged(main);
    sql.cond("where t1.company_id = ? and t1.accounting_group_id=?");
    sql.param(companyId);
    sql.param(accountingGroup.getId());
    return AppService.listPagedJpa(main, sql); // For pagination in view
    //  return AppService.listAllJpa(main, sql); // Return the full records
  }

  /**
   * Select AccountingLedger by key.
   *
   * @param main
   * @param accountingLedger
   * @return AccountingLedger
   */
  public static final AccountingLedger selectByPk(Main main, AccountingLedger accountingLedger) {
    return (AccountingLedger) AppService.find(main, AccountingLedger.class, accountingLedger.getId());
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
      insertAble(main, accountingLedger);  //Validating
      AppService.insert(main, accountingLedger);
    } else {
      updateAble(main, accountingLedger); //Validating
      accountingLedger = (AccountingLedger) AppService.update(main, accountingLedger);
    }
    LedgerExternalService.saveLedgerTaxCode(main, accountingLedger.getCompanyId()); // Inserting tax codes for company if its not already created.
  }

  public static AccountingLedger selectLedgerByEntity(Main main, Integer entityId, AccountingEntityType entityType, Integer companyId) {
    return (AccountingLedger) AppService.single(main, AccountingLedger.class, "select * from fin_accounting_ledger where entity_id=? and accounting_entity_type_id =? and company_id = ? ", new Object[]{entityId, entityType.getId(), companyId});
  }

  public static AccountingLedger selectLedgerByEntityAndCode(Main main, Integer entityId, AccountingEntityType entityType, Integer companyId, String ledgerCode) {
    return (AccountingLedger) AppService.single(main, AccountingLedger.class, "select * from fin_accounting_ledger where entity_id=? and accounting_entity_type_id =? and company_id = ? and ledger_code=?", new Object[]{entityId, entityType.getId(), companyId, ledgerCode});
  }

  public static AccountingLedger selectLedgerByLedgerCode(Main main, String ledgerCode, Integer companyId) {
    return (AccountingLedger) AppService.single(main, AccountingLedger.class, "select * from fin_accounting_ledger where company_id = ? and ledger_code=? ", new Object[]{companyId, ledgerCode});
  }

  public static void insertOrUpdateOpeningBalance(Main main, AccountingLedger mainLedger, List<TradeOutstanding> tradeOutstaningList) {
    LedgerExternalDataService.saveOpeningEntry(main, mainLedger, tradeOutstaningList, mainLedger.getCompanyId());
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountingLedger
   */
  public static void clone(Main main, AccountingLedger accountingLedger) {
    accountingLedger.setId(null); //Set to null for insert
    insertOrUpdate(main, accountingLedger);
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
   * Method to create default Accounting Ledgers for a company.
   *
   * @param main
   * @param company
   */
  public static void insertCompanyAccountingLedger(Main main, Company company) {
    List<AccountingLedger> list = AppService.list(main, AccountingLedger.class, "select * from fin_accounting_ledger where company_id is null order by accounting_group_id asc", null);
    for (AccountingLedger accountingPrefix : list) {
      insert(main, getAccountingLedger(accountingPrefix, company));
    }
  }

  /**
   * Method to get a new AccountingLedger object.
   *
   * @param accountingLedger
   * @param company
   * @return
   */
  private static AccountingLedger getAccountingLedger(AccountingLedger accountingLedger, Company company) {
    AccountingLedger ledger = new AccountingLedger();
    ledger.setCompanyId(company);
    ledger.setAccountingEntityTypeId(accountingLedger.getAccountingEntityTypeId());
    ledger.setAccountingGroupId(accountingLedger.getAccountingGroupId());
    ledger.setBillwise(accountingLedger.getBillwise());
    ledger.setDescription(accountingLedger.getDescription());
    ledger.setEntityId(accountingLedger.getEntityId());
    ledger.setIsDebitOrCredit(accountingLedger.getIsDebitOrCredit());
    ledger.setOpeningBalance(accountingLedger.getOpeningBalance());
    ledger.setTitle(accountingLedger.getTitle());
    ledger.setSortOrder(accountingLedger.getSortOrder());
    ledger.setLedgerCode(accountingLedger.getLedgerCode());
    ledger.setGstTaxNo(company.getGstNo());
    ledger.setPin(company.getCompanyPin());
    ledger.setAddress(company.getCompanyPin());
    ledger.setStateId(company.getStateId());
    ledger.setCurrencyId(company.getCurrencyId());
    ledger.setCountryId(company.getCountryId());
    return ledger;
  }

  /**
   * Method to get the AccountingLedger based on its company and ledger_code.
   *
   * @param main
   * @param companyId
   * @param ledgerCode
   * @return
   */
  public static final AccountingLedger selectAccoutingLedgerByCompanyAndLedgerCode(Main main, Company companyId, String ledgerCode) {
    return (AccountingLedger) AppService.single(main, AccountingLedger.class, "select * from fin_accounting_ledger where company_id = ? and ledger_code = ?", new Object[]{companyId.getId(), ledgerCode});
  }

  /**
   *
   * @param main
   * @param vendor
   * @return
   */
  public static final AccountingLedger selectSupplierAccountingLedger(Main main, Vendor vendor) {
    return (AccountingLedger) AppService.single(main, AccountingLedger.class, "select * from fin_accounting_ledger where accounting_entity_type_id = ? and entity_id = ?", new Object[]{AccountingConstant.ACC_ENTITY_VENDOR.getId(), vendor.getId()});
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
   * @param ledger
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AccountingLedger ledger) throws UserMessageException {
    if (AppService.exist(main, "select '1' from fin_accounting_ledger where upper(title)=? and accounting_group_id =? and company_id=?", new Object[]{StringUtil.toUpperCase(ledger.getTitle()), ledger.getAccountingGroupId().getId(), ledger.getCompanyId().getId()})) {
      throw new UserMessageException("name.exist", new String[]{ledger.getTitle()});
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param ledger
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AccountingLedger ledger) throws UserMessageException {
    if (AppService.exist(main, "select '1' from fin_accounting_ledger where upper(title)=? and accounting_group_id =? and company_id=? and id !=?", new Object[]{StringUtil.toUpperCase(ledger.getTitle()), ledger.getAccountingGroupId().getId(), ledger.getCompanyId().getId(), ledger.getId()})) {
      throw new UserMessageException("name.exist", new String[]{ledger.getTitle()});
    }
  }

  public static AccountGroup selectAccountGroupByCustomerAndCompany(Main main, Integer customerId, Integer companyId) {
    String sql = "SELECT t2.* from scm_sales_account t1, scm_account_group t2 WHERE t1.customer_id = ? AND t1.company_id = ? \n"
            + "AND t1.account_group_id = t2.id";
    return (AccountGroup) AppService.single(main, AccountGroup.class, sql, new Object[]{customerId, companyId});
  }

  public static AccountGroup selectAccountGroupBySalesAgent(Main main, Integer agentId) {
    String sql = " SELECT * FROM scm_sales_agent_account_group t1, scm_account_group t2 WHERE t1.sales_agent_id = ? AND t1.account_group_id = t2.id ";
    return (AccountGroup) AppService.single(main, AccountGroup.class, sql, new Object[]{agentId});
  }

  public static List<AccountingLedger> selectLedgerListByLedgerExpense(Main main, Integer companyId, String filter) {
    return AppService.list(main, AccountingLedger.class, "select * from fin_accounting_ledger where company_id = ? and accounting_group_id = ?  and upper(title) like ? ", new Object[]{companyId, AccountingConstant.GROUP_DIRECT_EXPENSE.getId(), "%" + filter.toUpperCase() + "%"});
  }

}
