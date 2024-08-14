/*
 * @(#)AccountGroupService.java	1.0 Fri Feb 03 19:15:39 IST 2017
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupDocPrefix;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingPrefix;
import spica.fin.domain.DebitCreditNote;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyFinancialYear;
import spica.scm.domain.PrefixType;
import spica.scm.domain.Status;
import spica.scm.view.AccountUtil;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import spica.sys.common.DocumentNumberGen;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.util.StringUtil;

/**
 * AccountGroupDocPrefixService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 03 19:15:39 IST 2017
 */
public abstract class AccountGroupDocPrefixService {

  /**
   * AccountGroupDocPrefix paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountGroupSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_account_group_doc_prefix", AccountGroupDocPrefix.class, main);
    sql.main("select scm_account_group_doc_prefix.id,scm_account_group_doc_prefix.account_id, scm_account_group_doc_prefix.vendor_id, scm_account_group_doc_prefix.account_group_id, scm_account_group_doc_prefix.doc_prefix,"
            + "scm_account_group_doc_prefix.doc_number_counter,scm_account_group_doc_prefix.padding,scm_account_group_doc_prefix.year_sequence,scm_account_group_doc_prefix.draft_doc_number_counter,"
            + "scm_account_group_doc_prefix.sort_order,scm_account_group_doc_prefix.created_by,scm_account_group_doc_prefix.modified_by,"
            + "scm_account_group_doc_prefix.created_at,scm_account_group_doc_prefix.modified_at,"
            + "scm_account_group_doc_prefix.status_id,scm_account_group_doc_prefix.year_padding,scm_account_group_doc_prefix.start_year_only,scm_account_group_doc_prefix.prefix_type_id "
            + "from scm_account_group_doc_prefix scm_account_group_doc_prefix "); //Main query
    sql.count("select count(scm_account_group_doc_prefix.id) as total from scm_account_group_doc_prefix scm_account_group_doc_prefix "); //Count query
    sql.join("left outer join scm_account scm_account on (scm_account.id = scm_account_group_doc_prefix.account_id) "
            + "left outer join scm_vendor scm_vendor on (scm_vendor.id = scm_account_group_doc_prefix.vendor_id) "
            + "left outer join scm_account_group scm_account_group on (scm_account_group.id = scm_account_group_doc_prefix.account_group_id) "
            + "left outer join scm_status scm_account_group_doc_prefixstatus_id on (scm_account_group_doc_prefixstatus_id.id = scm_account_group_doc_prefix.status_id)"
            + "left outer join scm_prefix_type scm_account_group_doc_prefixprefix_type_id on (scm_account_group_doc_prefixprefix_type_id.id = scm_account_group_doc_prefix.prefix_type_id) "); //Join Query

    sql.string(new String[]{"scm_account_group.group_name", "scm_account_group_doc_prefix.doc_prefix", "scm_account_group_doc_prefix.created_by",
      "scm_account_group_doc_prefix.modified_by", "scm_account_group_doc_prefixstatus_id.title", "scm_account_group_doc_prefixprefix_type_id.title"
    }); //String search or sort fields
    sql.number(new String[]{"scm_account_group_doc_prefix.id", "scm_account_group_doc_prefix.doc_number_counter", "scm_account_group_doc_prefix.sort_order",
      "scm_account_group_doc_prefix.padding", "scm_account_group_doc_prefix.year_sequence", "scm_account_group_doc_prefix.year_padding"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_account_group_doc_prefix.created_at", "scm_account_group_doc_prefix.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountGroupDocPrefix.
   *
   * @param main
   * @return List of AccountGroupDocPrefix
   */
  public static final List<AccountGroupDocPrefix> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountGroupSqlPaged(main));
  }

  /**
   * Select AccountGroupDocPrefix by key.
   *
   * @param main
   * @param accountGroupDocPrefix
   * @return AccountGroupDocPrefix
   */
  public static final AccountGroupDocPrefix selectByPk(Main main, AccountGroupDocPrefix accountGroupDocPrefix) {
    return (AccountGroupDocPrefix) AppService.find(main, AccountGroupDocPrefix.class, accountGroupDocPrefix.getId());
  }

  /**
   * Insert AccountGroupDocPrefix.
   *
   * @param main
   * @param accountGroupDocPrefix
   */
  public static final void insert(Main main, AccountGroupDocPrefix accountGroupDocPrefix) {
    AppService.insert(main, accountGroupDocPrefix);
  }

  /**
   * Update AccountGroupDocPrefix by key.
   *
   * @param main
   * @param accountGroupDocPrefix
   * @return AccountGroupDocPrefix
   */
  public static final AccountGroupDocPrefix updateByPk(Main main, AccountGroupDocPrefix accountGroupDocPrefix) {
    //AccountGroupDocPrefixIs.updateAble(main, accountGroupDocPrefix); //Validating
    return (AccountGroupDocPrefix) AppService.update(main, accountGroupDocPrefix);
  }

  /**
   * Insert or update AccountGroupDocPrefix
   *
   * @param main
   * @param accountGroupDocPrefix
   */
  public static void insertOrUpdate(Main main, AccountGroupDocPrefix accountGroupDocPrefix) {
    if (accountGroupDocPrefix.getId() == null) {
      insert(main, accountGroupDocPrefix);
    } else {
      updateByPk(main, accountGroupDocPrefix);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountGroupDocPrefix
   */
  public static void clone(Main main, AccountGroupDocPrefix accountGroupDocPrefix) {
    accountGroupDocPrefix.setId(null); //Set to null for insert
    insert(main, accountGroupDocPrefix);
  }

  /**
   * Delete AccountGroupDocPrefix.
   *
   * @param main
   * @param accountGroupDocPrefix
   */
  public static final void deleteByPk(Main main, AccountGroupDocPrefix accountGroupDocPrefix) {
    //AccountGroupDocPrefixIs.deleteAble(main, accountGroupDocPrefix); //Validation
    AppService.delete(main, AccountGroup.class, accountGroupDocPrefix.getId());
  }

  /**
   * Delete Array of AccoAccountGroupDocPrefixuntGroup.
   *
   * @param main
   * @param accountGroupDocPrefixes
   */
  public static final void deleteByPkArray(Main main, AccountGroupDocPrefix[] accountGroupDocPrefixes) {
    for (AccountGroupDocPrefix e : accountGroupDocPrefixes) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param accountGroupDocPrefix
   * @return
   */
  private static Integer getNextSequence(AccountGroupDocPrefix accountGroupDocPrefix, boolean draft) {
    double maxValue;
    Integer nextSequence = 0;
    if (accountGroupDocPrefix != null) {
      maxValue = Math.pow(10, accountGroupDocPrefix.getPadding());
      if ((accountGroupDocPrefix.getDocNumberCounter() + 1) >= maxValue) {
        //nextSequence = 1;
        nextSequence = (accountGroupDocPrefix.getDocNumberCounter() + 1);
      } else {
        nextSequence = (accountGroupDocPrefix.getDocNumberCounter() + 1);
      }
    }
    return nextSequence;
  }

  /**
   *
   * @param account
   * @param accountGroup
   * @param accountCode
   * @param statusId
   * @param prefix
   * @param prefixTypeId
   * @return
   */
  public static final AccountGroupDocPrefix getAccountGroupDocPrefix(Company companyId, Account account, AccountGroup accountGroup, String accountCode, Status statusId, String prefix, Integer prefixTypeId, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix accountGroupDocPrefix = new AccountGroupDocPrefix();
    accountGroupDocPrefix.setAccountId(account);
    accountGroupDocPrefix.setAccountGroupId(accountGroup);
    accountGroupDocPrefix.setDocPrefix(prefix + "/" + accountCode); // Removed slash after account code
    accountGroupDocPrefix.setDocNumberCounter(1);
    accountGroupDocPrefix.setDraftDocNumberCounter(1);
    accountGroupDocPrefix.setPadding(4);
    accountGroupDocPrefix.setYearSequence(0);
    accountGroupDocPrefix.setYearPadding(2);
    accountGroupDocPrefix.setStartYearOnly(1);
    accountGroupDocPrefix.setStatusId(statusId);
    accountGroupDocPrefix.setCompanyId(companyId);
    accountGroupDocPrefix.setPrefixTypeId(new PrefixType(prefixTypeId));
    accountGroupDocPrefix.setFinancialYearId(financialYear);
    return accountGroupDocPrefix;
  }

  /**
   *
   * @param main
   * @param account
   * @param accountGroup
   */
  public static final void insertDefaultAccountGroupDocPrifix(Main main, Account account, AccountGroup accountGroup) {
    if (account.getCompanyId() != null) {
      List<CompanyFinancialYear> financialYearList = AppService.list(main, CompanyFinancialYear.class, "select * from fin_company_fiancial_year where company_id=?",
              new Object[]{account.getCompanyId().getId()});
      for (CompanyFinancialYear finYear : financialYearList) {
        AccountingFinancialYear financialYear = finYear.getFinancialYearId();
        Status activeStatus = UserRuntimeService.activeGenericStatus(main.em());
        String prefix = "";
        if (account != null) {
          prefix = account.getAccountCode();
        }
        insertSalesPrefixes(main, account, accountGroup);
        insertPurchasePrefixes(main, account, accountGroup);

        //Value Credit Note prefix
        insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), account, accountGroup, prefix, activeStatus, PrefixTypeService.VALUE_CREDIT_NOTE_TAXABLE_PREFIX, PrefixTypeService.VALUE_CREDIT_NOTE_TAXABLE_PREFIX_ID, financialYear));
        insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), account, accountGroup, prefix, activeStatus, PrefixTypeService.VALUE_CREDIT_NOTE_NON_TAXABLE_PREFIX, PrefixTypeService.VALUE_CREDIT_NOTE_NON_TAXABLE_PREFIX_ID, financialYear));

        // Value Debit Note prefix
        insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), account, accountGroup, prefix, activeStatus, PrefixTypeService.VALUE_DEBIT_NOTE_TAXABLE_PREFIX, PrefixTypeService.VALUE_DEBIT_NOTE_TAXABLE_PREFIX_ID, financialYear));
        insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), account, accountGroup, prefix, activeStatus, PrefixTypeService.VALUE_DEBIT_NOTE_NON_TAXABLE_PREFIX, PrefixTypeService.VALUE_DEBIT_NOTE_NON_TAXABLE_PREFIX_ID, financialYear));

        // Sales service prefix
        //  insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), account, null, prefix, activeStatus, PrefixTypeService.SALES_SERVICES_TAXABLE_PREFIX, PrefixTypeService.SALES_SERVICES_TAXABLE_PREFIX_ID,financialYear));
        //  insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), account, null, prefix, activeStatus, PrefixTypeService.SALES_SERVICES_NON_TAXABLE_PREFIX, PrefixTypeService.SALES_SERVICES_NON_TAXABLE_PREFIX_ID,financialYear));
      }
    }
  }

  public static void insertOrUpdateCustomAccountGroupPrefix(Main main, AccountGroup accountGroup) {
    if (accountGroup.getCompanyId() != null) {
      List<CompanyFinancialYear> financialYearList = AppService.list(main, CompanyFinancialYear.class, "select * from fin_company_fiancial_year where company_id=?",
              new Object[]{accountGroup.getCompanyId().getId()});
      for (CompanyFinancialYear finYear : financialYearList) {
        AccountingFinancialYear financialYear = finYear.getFinancialYearId();
        Status activeStatus = UserRuntimeService.activeGenericStatus(main.em());
        String prefix = accountGroup.getGroupCode();
        if (selectDebitCreditNoteCustomAccountGroupDocPrefix(main, accountGroup).size() == 0) {
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.VALUE_CREDIT_NOTE_TAXABLE_PREFIX, PrefixTypeService.VALUE_CREDIT_NOTE_TAXABLE_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.VALUE_CREDIT_NOTE_NON_TAXABLE_PREFIX, PrefixTypeService.VALUE_CREDIT_NOTE_NON_TAXABLE_PREFIX_ID, financialYear));

          //Value Debit Note prefix
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.VALUE_DEBIT_NOTE_TAXABLE_PREFIX, PrefixTypeService.VALUE_DEBIT_NOTE_TAXABLE_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.VALUE_DEBIT_NOTE_NON_TAXABLE_PREFIX, PrefixTypeService.VALUE_DEBIT_NOTE_NON_TAXABLE_PREFIX_ID, financialYear));

          //Value Debit Note prefix
//    insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, null, prefix, activeStatus, PrefixTypeService.SALES_SERVICES_TAXABLE_PREFIX, PrefixTypeService.SALES_SERVICES_TAXABLE_PREFIX_ID,financialYear));
//    insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, null, prefix, activeStatus, PrefixTypeService.SALES_SERVICES_NON_TAXABLE_PREFIX, PrefixTypeService.SALES_SERVICES_NON_TAXABLE_PREFIX_ID,financialYear));
        }
        /**
         * Sales Invoice prefix.
         */
        if (selectSalesCustomAccountGroupDocPrefix(main, accountGroup).size() == 0) {
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.SALES_INVOICE_PREFIX, PrefixTypeService.SALES_INVOICE_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.SALES_REQUISITION_PREFIX, PrefixTypeService.SALES_REQUISITION_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.SALES_ORDER_PREFIX, PrefixTypeService.SALES_ORDER_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.SALES_RETURN_SALEABLE_INVOICE_PREFIX, PrefixTypeService.SALES_RETURN_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.SALES_INVOICE_CONSIGNMENT_PREFIX, PrefixTypeService.SALES_INVOICE_CONSIGNMENT_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.SALES_RETURN_CONSIGNMENT_PREFIX, PrefixTypeService.SALES_RETURN_CONSIGNMENT_PREFIX_ID, financialYear));
        }
        /**
         * Purchase Requisition Prefix
         */
        if (selectPurchaseCustomAccountGroupDocPrefix(main, accountGroup).size() == 0) {
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX, PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX, PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX, PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX, PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX, PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.PURCHASE_RETURN_CONSIGNMENT_PREFIX, PrefixTypeService.PURCHASE_RETURN_CONSIGNMENT_PREFIX_ID, financialYear));
          insert(main, getAccountGroupDocPrefix(accountGroup.getCompanyId(), null, accountGroup, prefix, activeStatus, PrefixTypeService.STOCK_ADJUSTMENT_PREFIX, PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, financialYear));
        }
      }
    }
  }

  /**
   *
   * @param main
   * @param company
   */
  public static void insertOrUpdateCompanySalesServicesPrefix(Main main, Company company) {
    List<CompanyFinancialYear> financialYearList = AppService.list(main, CompanyFinancialYear.class, "select * from fin_company_fiancial_year where company_id=?",
            new Object[]{company.getId()});
    for (CompanyFinancialYear finYear : financialYearList) {
      AccountingFinancialYear financialYear = finYear.getFinancialYearId();
      AppService.deleteSql(main, AccountGroupDocPrefix.class, "delete from scm_account_group_doc_prefix where company_id = ? and prefix_type_id in (?,?)", new Object[]{company.getId(), PrefixTypeService.SALES_SERVICES_TAXABLE_PREFIX_ID, PrefixTypeService.SALES_SERVICES_NON_TAXABLE_PREFIX_ID});
      insert(main, getAccountGroupDocPrefix(company, null, null, company.getCompanyCode(), UserRuntimeService.activeGenericStatus(main.em()), PrefixTypeService.SALES_SERVICES_TAXABLE_PREFIX, PrefixTypeService.SALES_SERVICES_TAXABLE_PREFIX_ID, financialYear));
      insert(main, getAccountGroupDocPrefix(company, null, null, company.getCompanyCode(), UserRuntimeService.activeGenericStatus(main.em()), PrefixTypeService.SALES_SERVICES_NON_TAXABLE_PREFIX, PrefixTypeService.SALES_SERVICES_NON_TAXABLE_PREFIX_ID, financialYear));
    }
  }

  /**
   *
   * @param main
   * @param account
   * @param accountGroup
   */
  public static final void insertSalesPrefixes(Main main, Account account, AccountGroup accountGroup) {
    if (account.getCompanyId() != null) {
      List<CompanyFinancialYear> financialYearList = AppService.list(main, CompanyFinancialYear.class, "select * from fin_company_fiancial_year where company_id=?",
              new Object[]{account.getCompanyId().getId()});
      for (CompanyFinancialYear finYear : financialYearList) {
        AccountingFinancialYear financialYear = finYear.getFinancialYearId();
        Status activeStatus = UserRuntimeService.activeGenericStatus(main.em());
        String prefix = "";
        Company companyId = null;
        if (account != null) {
          prefix = account.getAccountCode();
          companyId = account.getCompanyId();
//      if (account.getSalesChannel().equals(AccountService.GROUP_SUPPLIER)) {
//        prefix = account.getVendorId().getSupplierGroupId().getCode();
//      } else {
//        prefix = account.getAccountCode();
//      }
        } else if (accountGroup != null) {
          prefix = accountGroup.getGroupCode();
          companyId = accountGroup.getCompanyId();
        }
        if (selectSalesCustomAccountGroupDocPrefix(main, accountGroup).size() == 0) {
          /**
           * Sales Invoice prefix.
           */
          insert(main, getAccountGroupDocPrefix(companyId, null, accountGroup, prefix, account != null ? activeStatus : accountGroup.getStatusId(), PrefixTypeService.SALES_INVOICE_PREFIX, PrefixTypeService.SALES_INVOICE_PREFIX_ID, financialYear));

          /**
           * Sales Requisition prefix.
           */
          insert(main, getAccountGroupDocPrefix(companyId, null, accountGroup, prefix, account != null ? activeStatus : accountGroup.getStatusId(), PrefixTypeService.SALES_REQUISITION_PREFIX, PrefixTypeService.SALES_REQUISITION_PREFIX_ID, financialYear));

          /**
           * Sales Order prefix.
           */
          insert(main, getAccountGroupDocPrefix(companyId, null, accountGroup, prefix, account != null ? activeStatus : accountGroup.getStatusId(), PrefixTypeService.SALES_ORDER_PREFIX, PrefixTypeService.SALES_ORDER_PREFIX_ID, financialYear));

          /**
           * Sales Return Saleable Invoice prefix
           */
          insert(main, getAccountGroupDocPrefix(companyId, null, accountGroup, prefix, account != null ? activeStatus : accountGroup.getStatusId(), PrefixTypeService.SALES_RETURN_SALEABLE_INVOICE_PREFIX, PrefixTypeService.SALES_RETURN_PREFIX_ID, financialYear));

          /**
           * Sales Invoice Consignment prefix.
           */
          insert(main, getAccountGroupDocPrefix(companyId, null, accountGroup, prefix, account != null ? activeStatus : accountGroup.getStatusId(), PrefixTypeService.SALES_INVOICE_CONSIGNMENT_PREFIX, PrefixTypeService.SALES_INVOICE_CONSIGNMENT_PREFIX_ID, financialYear));

          /**
           * Sales Return Consignment prefix.
           */
          insert(main, getAccountGroupDocPrefix(companyId, null, accountGroup, prefix, account != null ? activeStatus : accountGroup.getStatusId(), PrefixTypeService.SALES_RETURN_CONSIGNMENT_PREFIX, PrefixTypeService.SALES_RETURN_CONSIGNMENT_PREFIX_ID, financialYear));
        }
      }
    }
  }

  /**
   *
   * @param main
   * @param account
   * @param accountGroup
   */
  public static final void insertPurchasePrefixes(Main main, Account account, AccountGroup accountGroup) {
    if (account.getCompanyId() != null) {
      List<CompanyFinancialYear> financialYearList = AppService.list(main, CompanyFinancialYear.class, "select * from fin_company_fiancial_year where company_id=?",
              new Object[]{account.getCompanyId().getId()});
      for (CompanyFinancialYear finYear : financialYearList) {
        AccountingFinancialYear financialYear = finYear.getFinancialYearId();
        String prefix = "";
        Company companyId = null;
        if (account != null) {
          prefix = account.getAccountCode();
          companyId = account.getCompanyId();
//      if (AccountService.GROUP_SUPPLIER.equals(account.getPurchaseChannel())) {
//        prefix = account.getVendorId().getSupplierGroupId().getCode();
//      } else {
//        prefix = account.getAccountCode();
//      }
        }

        /**
         * Purchase Requisition Prefix
         */
        insert(main, getAccountGroupDocPrefix(companyId, account, accountGroup, prefix, accountGroup.getStatusId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX, PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, financialYear));

        /**
         * Purchase Order Prefix
         */
        insert(main, getAccountGroupDocPrefix(companyId, account, accountGroup, prefix, accountGroup.getStatusId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX, PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, financialYear));

        /**
         * Purchase Return Prefix
         */
        insert(main, getAccountGroupDocPrefix(companyId, account, accountGroup, prefix, accountGroup.getStatusId(), PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX, PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, financialYear));

        /**
         * Product Entry Prefix
         */
        insert(main, getAccountGroupDocPrefix(companyId, account, accountGroup, prefix, accountGroup.getStatusId(), PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX, PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX_ID, financialYear));

        /**
         * Purchase Consignment Prefix
         */
        insert(main, getAccountGroupDocPrefix(companyId, account, accountGroup, prefix, accountGroup.getStatusId(), PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX, PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX_ID, financialYear));

        /**
         * Purchase Return Consignment Prefix
         */
        insert(main, getAccountGroupDocPrefix(companyId, account, accountGroup, prefix, accountGroup.getStatusId(), PrefixTypeService.PURCHASE_RETURN_CONSIGNMENT_PREFIX, PrefixTypeService.PURCHASE_RETURN_CONSIGNMENT_PREFIX_ID, financialYear));

        /**
         * Stock Adjustment Prefix
         */
        insert(main, getAccountGroupDocPrefix(companyId, account, accountGroup, prefix, accountGroup.getStatusId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX, PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, financialYear));
      }
    }
  }

  /**
   *
   * @param main
   * @param account
   * @return
   */
  public static final List<AccountGroupDocPrefix> selectDefaultAccountGroupDocPrefix(Main main, Account account) {
    AccountGroup defaultAccountGroup = AccountGroupService.selectDefaultAccountGroupByAccount(main, account);

    return AppService.list(main, AccountGroupDocPrefix.class,
            "select * from scm_account_group_doc_prefix "
            + "where (account_group_id = ? or account_id = ?) and financial_year_id =? and prefix_type_id not in(1,2,3,9,10,15,16,17,18,19,6,12) "
            + "order by account_group_id asc", new Object[]{defaultAccountGroup.getId(), account.getId(), account.getFinancialYear().getId()});
  }

  /**
   *
   * @param main
   * @param account
   * @param accountGroup
   * @return
   */
  public static List<AccountGroupDocPrefix> selectAccountGroupDocPurchasePrefixes(Main main, Account account, AccountGroup accountGroup) {
    if (AccountUtil.isScondaryVendorAccount(account)) {
      if (AccountService.GROUP_SUPPLIER.equals(account.getPurchaseChannel())) {
        accountGroup = AccountGroupService.selectDefaultAccountGroupByAccount(main, account.getAccountId());

        return AppService.list(main, AccountGroupDocPrefix.class,
                "select * from scm_account_group_doc_prefix where account_id = ? and account_group_id = ?"
                + " and financial_year_id =? and prefix_type_id in (1,2,3,15,16,17)",
                new Object[]{account.getAccountId().getId(), accountGroup.getId(), account.getFinancialYear().getId()});

      } else if (account != null && accountGroup != null) {
        return AppService.list(main, AccountGroupDocPrefix.class,
                "select * from scm_account_group_doc_prefix where account_id = ? and account_group_id = ? "
                + " and financial_year_id =? and prefix_type_id in (1,2,3,15,16,17)", new Object[]{account.getId(), accountGroup.getId(), account.getFinancialYear().getId()});
      } else {
        return null;

      }
    } else {
      return AppService.list(main, AccountGroupDocPrefix.class,
              "select * from scm_account_group_doc_prefix where account_id = ? and account_group_id = ? "
              + " and financial_year_id =? and prefix_type_id in (1,2,3,15,16,17)", new Object[]{account.getId(), accountGroup.getId(), account.getFinancialYear().getId()});
    }
  }

  /**
   *
   * @param main
   * @param account
   * @return
   */
  public static List<AccountGroupDocPrefix> selectDebitCreditNoteAccountGroupDocPrefix(Main main, Account account, AccountGroup accountGroup) {
    if (AccountUtil.isScondaryVendorAccount(account)) {
      if (AccountService.GROUP_SUPPLIER.equals(account.getSalesChannel())) {
        accountGroup = AccountGroupService.selectDefaultAccountGroupByAccount(main, account.getAccountId());

        return AppService.list(main, AccountGroupDocPrefix.class,
                "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id in(9,10,18,19) "
                + " and financial_year_id=?", new Object[]{accountGroup.getId(), account.getFinancialYear().getId()});

      } else {
        return AppService.list(main, AccountGroupDocPrefix.class,
                "select * from scm_account_group_doc_prefix where account_id = ? and prefix_type_id in(9,10,18,19) "
                + " and financial_year_id=?", new Object[]{account.getId(), account.getFinancialYear().getId()});

      }
    }
    return AppService.list(main, AccountGroupDocPrefix.class,
            "select * from scm_account_group_doc_prefix where account_id = ? and prefix_type_id in(9,10,18,19)  and financial_year_id=?",
            new Object[]{account.getId(), account.getFinancialYear().getId()});
  }

  public static List<AccountGroupDocPrefix> selectDebitCreditNoteCustomAccountGroupDocPrefix(Main main, AccountGroup accountGroup) {
    return AppService.list(main, AccountGroupDocPrefix.class,
            "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id in(9,10,18,19) and financial_year_id=?",
            new Object[]{accountGroup.getId(), accountGroup.getFinancialYear().getId()});
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @return
   */
  public static AccountGroupDocPrefix
          selectAccountGroupDocPrefixByAccountGroup(MainView main, AccountGroup accountGroup) {
    return (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
            "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id = ? and financial_year_id=?", new Object[]{accountGroup.getId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, accountGroup.getFinancialYear().getId()});
  }

  /**
   *
   * @param main
   * @param accountId
   * @param prefixTypeId
   * @return
   */
  public static final AccountGroupDocPrefix getAccountGroupDocPrefixByAccountPrefixType(Main main, Account accountId, Integer prefixTypeId, boolean isDraftPrefix, AccountingFinancialYear financialYear) {
    Status activeStatus = UserRuntimeService.activeGenericStatus(main.em());
    AccountGroupDocPrefix accountGroupDocPrefix = null;
    String sql = "";
    ArrayList<Object> params = new ArrayList<>();
    if (isDraftPrefix) {
      sql = "select agdp.id,agdp.account_group_id,agdp.doc_prefix,agdp.draft_doc_number_counter as doc_number_counter,agdp.padding,agdp.year_sequence,agdp.year_padding,"
              + "agdp.prefix_type_id,agdp.sort_order,agdp.status_id,agdp.account_id,agdp.vendor_id,agdp.start_year_only from scm_account_group_doc_prefix agdp "
              + "inner join scm_account_group on agdp.account_group_id = scm_account_group.id "
              + "inner join scm_account_group_detail on scm_account_group_detail.account_group_id = scm_account_group.id "
              + "where scm_account_group_detail.account_id = ? and agdp.account_id = ? and prefix_type_id = ? and scm_account_group.is_default = 1 "
              + " and agdp.financial_year_id=?";
      params.add(accountId.getId());
      if (accountId.getAccountId() != null && accountId.getPurchaseChannel() == 2) {
        params.add(accountId.getAccountId().getId());
      } else {
        params.add(accountId.getId());
      }
      params.add(prefixTypeId);
      params.add(financialYear.getId());
      accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class, sql, params.toArray());

    } else {
      sql = "select scm_account_group_doc_prefix.* from scm_account_group_doc_prefix "
              + "inner join scm_account_group on scm_account_group_doc_prefix.account_group_id = scm_account_group.id "
              + "inner join scm_account_group_detail on scm_account_group_detail.account_group_id = scm_account_group.id "
              + "where scm_account_group_detail.account_id = ? and scm_account_group_doc_prefix.account_id = ? and prefix_type_id = ? and scm_account_group.is_default = 1"
              + " and scm_account_group_doc_prefix.financial_year_id=?";
      params.add(accountId.getId());
      if (accountId.getAccountId() != null && accountId.getPurchaseChannel() == 2) {
        params.add(accountId.getAccountId().getId());
      } else {
        params.add(accountId.getId());
      }
      params.add(prefixTypeId);
      params.add(financialYear.getId());
      accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class, sql, params.toArray());
    }
    if (accountGroupDocPrefix == null && prefixTypeId == PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID) {
      Account ac = (Account) AppService.single(main, Account.class,
              "select * from scm_account where id = ?", new Object[]{accountId.getId()});
      AccountGroup acg = (AccountGroup) AppService.single(main, AccountGroup.class, "select * from scm_account_group where id in"
              + "(select account_group_id from scm_account_group_detail where account_id=?) and is_default = 1", new Object[]{accountId.getId()});
      accountGroupDocPrefix = getAccountGroupDocPrefix(ac.getCompanyId(), ac, acg, ac.getAccountCode(), activeStatus, PrefixTypeService.STOCK_MOVEMENT_PREFIX, prefixTypeId, financialYear);
      insert(main, accountGroupDocPrefix);
      main.em().flush();
    }
    return accountGroupDocPrefix;
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @param prefixTypeId
   * @return
   */
  public static final AccountGroupDocPrefix getAccountGroupDocPrefixByAccountGroupPrefixType(Main main, AccountGroup accountGroup, Integer prefixTypeId, boolean isDraftPrefix, AccountingFinancialYear financialYear) {
    Status activeStatus = UserRuntimeService.activeGenericStatus(main.em());
    AccountGroupDocPrefix accountGroupDocPrefix = null;

    if (isDraftPrefix) {
      if (accountGroup.getIsDefault() == SystemConstants.DEFAULT && prefixTypeId == PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID) {
        accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
                "select  agdp.id,agdp.account_group_id,agdp.doc_prefix,agdp.draft_doc_number_counter as doc_number_counter,agdp.padding,agdp.year_sequence,agdp.year_padding,"
                + "agdp.prefix_type_id,agdp.sort_order,agdp.status_id,agdp.account_id,agdp.vendor_id,agdp.start_year_only "
                + "from scm_account_group_doc_prefix agdp where agdp.account_group_id = ? and agdp.prefix_type_id = ? "
                + "and agdp.account_id in(select id from scm_account where account_id is null and id in"
                + "(select account_id from scm_account_group_detail where account_group_id = ?)) "
                + " and agdp.financial_year_id=?", new Object[]{accountGroup.getId(), prefixTypeId, accountGroup.getId(), financialYear.getId()});

      } else {
        accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
                "select  agdp.id,agdp.account_group_id,agdp.doc_prefix,agdp.draft_doc_number_counter as doc_number_counter,agdp.padding,agdp.year_sequence,agdp.year_padding,"
                + "agdp.prefix_type_id,agdp.sort_order,agdp.status_id,agdp.account_id,agdp.vendor_id,agdp.start_year_only "
                + "from scm_account_group_doc_prefix agdp where agdp.account_group_id = ? and agdp.prefix_type_id = ? "
                + " and agdp.financial_year_id=?", new Object[]{accountGroup.getId(), prefixTypeId, financialYear.getId()});

      }
    } else {
      if (accountGroup.getIsDefault() == SystemConstants.DEFAULT && prefixTypeId == PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID) {
        accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
                "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id = ? "
                + "and account_id in(select id from scm_account where account_id is null and id in"
                + "(select account_id from scm_account_group_detail where account_group_id = ?)) "
                + " and financial_year_id=?", new Object[]{accountGroup.getId(), prefixTypeId, accountGroup.getId(), financialYear.getId()});

      } else {
        accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
                "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id = ? "
                + " and financial_year_id=?", new Object[]{accountGroup.getId(), prefixTypeId, financialYear.getId()});

      }
    }
    if (accountGroupDocPrefix == null && prefixTypeId == PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID) {
      AccountGroup ag = (AccountGroup) AppService.single(main, AccountGroup.class,
              "select * from scm_account_group where id = ?", new Object[]{accountGroup.getId()});
      accountGroupDocPrefix = getAccountGroupDocPrefix(ag.getCompanyId(), null, ag, ag.getGroupCode(), activeStatus, PrefixTypeService.STOCK_MOVEMENT_PREFIX, prefixTypeId, financialYear);
      insert(main, accountGroupDocPrefix);
      main.em().flush();
    }

    return accountGroupDocPrefix;
  }

  /**
   *
   * @param main
   * @param accountId
   * @param prefixType
   * @param isDraftPrefix
   * @return
   */
  public static final String getNextPurchasePrefixSequence(Main main, Account accountId, Integer prefixType, boolean isDraftPrefix, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix accountGroupDocPrefix = null;
    Company company = CompanyService.selectByPk(main, accountId.getCompanyId());
    if (StringUtil.equalsInt(prefixType, PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID) && accountId == null) {
      accountGroupDocPrefix = getDocPrefixBasedOnCompanyByAccountPrefixType(main, company, prefixType, isDraftPrefix, financialYear);
    } else {
      accountGroupDocPrefix = getAccountGroupDocPrefixByAccountPrefixType(main, accountId, prefixType, isDraftPrefix, financialYear);
    }
    if (accountGroupDocPrefix != null) {
      return DocumentNumberGen.getPrefixLiteral(accountGroupDocPrefix, financialYear.getStartDate());
    }
    return null;
  }

  /**
   *
   * @param main
   * @param accountId
   * @param prefixType
   * @param draftPrefix
   */
  public static final void updatePurchasePrefixSequence(Main main, Account accountId, Integer prefixType, boolean draftPrefix, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix accountGroupDocPrefix = null;
    Company company = CompanyService.selectByPk(main, accountId.getCompanyId());
    if (StringUtil.equalsInt(prefixType, PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID) && accountId == null) {
      accountGroupDocPrefix = getDocPrefixBasedOnCompanyByAccountPrefixType(main, company, prefixType, draftPrefix, financialYear);
    } else {
      accountGroupDocPrefix = getAccountGroupDocPrefixByAccountPrefixType(main, accountId, prefixType, draftPrefix, financialYear);
    }
    if (draftPrefix) {
      accountGroupDocPrefix.setDraftDocNumberCounter(getNextSequence(accountGroupDocPrefix, draftPrefix));
    } else {
      accountGroupDocPrefix.setDocNumberCounter(getNextSequence(accountGroupDocPrefix, draftPrefix));
    }
    insertOrUpdate(main, accountGroupDocPrefix);
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @param prefixTypeId
   * @param draftPrefix
   * @return
   */
  public static final String getNextSalesPrefixSequence(Main main, AccountGroup accountGroup, Integer prefixTypeId, boolean draftPrefix, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix accountGroupDocPrefix = getAccountGroupDocPrefixByAccountGroupPrefixType(main, accountGroup, prefixTypeId, draftPrefix, financialYear);
    if (accountGroupDocPrefix != null) {
      Company company = CompanyService.selectByPk(main, accountGroup.getCompanyId());
      return DocumentNumberGen.getPrefixLiteral(accountGroupDocPrefix, financialYear.getStartDate());
    }
    return null;
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @param prefixTypeId
   * @param draftPrefix
   */
  public static final void updateSalesPrefixSequence(Main main, AccountGroup accountGroup, Integer prefixTypeId, boolean draftPrefix, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix accountGroupDocPrefix = getAccountGroupDocPrefixByAccountGroupPrefixType(main, accountGroup, prefixTypeId, draftPrefix, financialYear);
    if (draftPrefix) {
      accountGroupDocPrefix.setDraftDocNumberCounter(getNextSequence(accountGroupDocPrefix, draftPrefix));
    } else {
      accountGroupDocPrefix.setDocNumberCounter(getNextSequence(accountGroupDocPrefix, draftPrefix));
    }
    insertOrUpdate(main, accountGroupDocPrefix);
  }

  /**
   *
   * @param main
   * @param ledger
   * @param prefixTypeId
   * @return
   */
  public static final String getNextSalesServicesPefixSequence(Main main, AccountingLedger ledger, Integer prefixTypeId, AccountingFinancialYear financialYear) {
    String prefix = null;
    AccountGroupDocPrefix accountGroupDocPrefix = null;

    if (ledger.getAccountingEntityTypeId().getId().intValue() == AccountingConstant.ACC_ENTITY_VENDOR.getId()) {
      accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select t1.* from scm_account_group_doc_prefix t1 "
              + "inner join scm_account t2 on t1.account_id = t2.id and t2.vendor_id = ? and t1.prefix_type_id = ? "
              + " and t1.financial_year_id=?", new Object[]{ledger.getEntityId(), prefixTypeId, financialYear.getId()});

    } else {
      accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select * from scm_account_group_doc_prefix where company_id = ? and prefix_type_id = ? and account_id is null "
              + " and t1.financial_year_id=?", new Object[]{ledger.getCompanyId().getId(), prefixTypeId, financialYear.getId()});
    }
    if (accountGroupDocPrefix != null) {
      prefix = DocumentNumberGen.getPrefixLiteral(accountGroupDocPrefix, financialYear.getStartDate());
    }
    return prefix;
  }

  /**
   *
   * @param main
   * @param debitCreditNote
   * @param PrefixId
   * @return
   */
  public static final String getNextDebitCreditNotePefixSequence(Main main, DebitCreditNote debitCreditNote, Integer prefixTypeId, boolean draftPrefix, AccountingFinancialYear financialYear) {
    String prefix = null;
    AccountGroupDocPrefix accountGroupDocPrefix = null;

    if (debitCreditNote.getAccountId() != null && debitCreditNote.isSupplier()) {
      int id = debitCreditNote.getAccountId().getId();
      if (debitCreditNote.getAccountId().getPurchaseChannel() == 2) {
        id = debitCreditNote.getAccountId().getAccountId() != null ? debitCreditNote.getAccountId().getAccountId().getId() : id;

      }
      accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select * from scm_account_group_doc_prefix where account_id = ? and prefix_type_id = ? and financial_year_id=?", new Object[]{id, prefixTypeId, financialYear.getId()});

    } else if (debitCreditNote.getAccountGroupId() != null) {
      if (SystemConstants.DEFAULT.intValue() == debitCreditNote.getAccountGroupId().getIsDefault()) {
        accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
                "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id = ? "
                + "AND account_id IN (SELECT id FROM scm_account  WHERE account_id IS NULL)  and financial_year_id=?",
                new Object[]{debitCreditNote.getAccountGroupId().getId(), prefixTypeId, financialYear.getId()});

      } else {
        accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
                "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id = ?  and financial_year_id=?",
                new Object[]{debitCreditNote.getAccountGroupId().getId(), prefixTypeId, financialYear.getId()});
      }
    }
    if (accountGroupDocPrefix != null) {
      if (draftPrefix) {
        accountGroupDocPrefix.setDocNumberCounter(accountGroupDocPrefix.getDraftDocNumberCounter());
      }
      prefix = DocumentNumberGen.getPrefixLiteral(accountGroupDocPrefix, financialYear.getStartDate());
    }
    return prefix;
  }

  /**
   *
   * @param main
   * @param debitCreditNote
   * @param prefixTypeId
   * @param draftPrefix
   */
  public static final void updateDebitCreditNotePrefixSequence(Main main, DebitCreditNote debitCreditNote, Integer prefixTypeId, boolean draftPrefix, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix accountGroupDocPrefix = null;

    if (debitCreditNote.getAccountId() != null && debitCreditNote.isSupplier()) {
      int id = debitCreditNote.getAccountId().getId();
      if (debitCreditNote.getAccountId().getPurchaseChannel() == 2) {
        id = debitCreditNote.getAccountId().getAccountId() != null ? debitCreditNote.getAccountId().getAccountId().getId() : id;

      }
      accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select * from scm_account_group_doc_prefix where account_id = ? and prefix_type_id = ?   and financial_year_id=?", new Object[]{id, prefixTypeId, financialYear.getId()});

    } else if (debitCreditNote.getAccountGroupId() != null) {
      if (SystemConstants.DEFAULT.intValue() == debitCreditNote.getAccountGroupId().getIsDefault()) {
        accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
                "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id = ? "
                + "AND account_id IN (SELECT id FROM scm_account  WHERE account_id IS NULL)   and financial_year_id=?",
                new Object[]{debitCreditNote.getAccountGroupId().getId(), prefixTypeId, financialYear.getId()});

      } else {
        accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
                "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id = ?   and financial_year_id=?",
                new Object[]{debitCreditNote.getAccountGroupId().getId(), prefixTypeId, financialYear.getId()});
      }
    }
    if (accountGroupDocPrefix != null) {
      if (draftPrefix) {
//        accountGroupDocPrefix.setDocNumberCounter(accountGroupDocPrefix.getDraftDocNumberCounter());
        accountGroupDocPrefix.setDraftDocNumberCounter(getNextSequence(accountGroupDocPrefix, draftPrefix));
      } else {
        accountGroupDocPrefix.setDocNumberCounter(getNextSequence(accountGroupDocPrefix, draftPrefix));
      }
      insertOrUpdate(main, accountGroupDocPrefix);
    }
  }

  /**
   *
   * @param main
   * @param ledger
   * @param prefixTypeId
   * @param draftPrefix
   */
  public static final void updateSalesServicesPrefixSequence(Main main, AccountingLedger ledger, Integer prefixTypeId, boolean draftPrefix, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix accountGroupDocPrefix = null;

    if (ledger.getAccountingEntityTypeId().getId().equals(AccountingConstant.ACC_ENTITY_VENDOR.getId())) {
      accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select t1.* from scm_account_group_doc_prefix t1 "
              + "inner join scm_account t2 on t1.account_id = t2.id and t2.vendor_id = ? and t1.prefix_type_id = ? and financial_year_id=?",
              new Object[]{ledger.getEntityId(), prefixTypeId, financialYear.getId()});

    } else {
      accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select * from scm_account_group_doc_prefix where company_id = ? and prefix_type_id = ? and account_id is null and financial_year_id=?",
              new Object[]{ledger.getCompanyId().getId(), prefixTypeId, financialYear.getId()});
    }

    if (accountGroupDocPrefix != null) {
      if (draftPrefix) {
        accountGroupDocPrefix.setDraftDocNumberCounter(getNextSequence(accountGroupDocPrefix, draftPrefix));
      } else {
        accountGroupDocPrefix.setDocNumberCounter(getNextSequence(accountGroupDocPrefix, draftPrefix));
      }
      insertOrUpdate(main, accountGroupDocPrefix);
    }
  }

  /**
   *
   * @param main
   * @param account
   * @return
   */
  public static final String getProductEntryInvoiceNumber(Main main, Company company, Account account, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix salesReqDocPrefix = null;

    if (account == null) {
      salesReqDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select * from scm_account_group_doc_prefix where account_id is null and account_group_id is null and company_id=? and prefix_type_id = ? "
              + " and financial_year_id=?", new Object[]{company.getId(), PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX_ID, financialYear.getId()});

    } else {
      salesReqDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select * from scm_account_group_doc_prefix where account_id = ? and prefix_type_id = ?  and financial_year_id=?",
              new Object[]{account.getId(), PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX_ID, financialYear.getId()});
    }
    if (salesReqDocPrefix != null) {
      return DocumentNumberGen.getPrefixLiteral(salesReqDocPrefix, financialYear.getStartDate());
    }
    return null;
  }

  /**
   *
   * @param main
   * @param account
   */
  public static final void updateProductEntryPrefixSequence(Main main, Company company, Account account, AccountingFinancialYear financialYear) {
    main.clear();
    main.param(PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX_ID);
    if (account == null) {
      main.param(company.getId());
      main.param(financialYear.getId());
      AppService.updateSql(main, AccountGroupDocPrefix.class,
              "update scm_account_group_doc_prefix set doc_number_counter = (doc_number_counter + 1) where account_id is null and account_group_id is null"
              + " and prefix_type_id = ? and company_id = ?  and financial_year_id=?", false);
    } else {
      main.param(account.getId());
      main.param(financialYear.getId());
      AppService.updateSql(main, AccountGroupDocPrefix.class,
              "update scm_account_group_doc_prefix set doc_number_counter = (doc_number_counter + 1) where prefix_type_id = ? and account_id = ?  and financial_year_id=?", false);
    }
    main.clear();
  }

  /**
   *
   * @param main
   * @param account
   * @param defaultAccountGroup
   */
  public static final void updateAccountGroupDocPrefixStatus(Main main, Account account, AccountGroup defaultAccountGroup, AccountingFinancialYear financialYear) {
    main.clear();
    main.param(account.getId());
    main.param(defaultAccountGroup.getId());
    main.param(financialYear.getId());
    AppService.updateSql(main, AccountGroupDocPrefix.class,
            "update scm_account_group_doc_prefix set status_id = 1 where account_id = ? and account_group_id = ?  and financial_year_id=?", false);
    main.clear();
  }

  public static void updateDocPrefixByAccount(Main main, String tempCode, Account account, AccountingFinancialYear financialYear) {

    AccountGroup group = AccountGroupService.selectDefaultAccountGroupByAccount(main, account);

    String sql = "update scm_account_group_doc_prefix agdp \n"
            + "SET doc_prefix=REPLACE(doc_prefix,?,?) where agdp.account_group_id = ?  and financial_year_id=?";
    main.clear();
    main.param(tempCode);
    main.param(account.getAccountCode());
    main.param(group.getId());
    main.param(financialYear.getId());
    AppService.updateSql(main, AccountGroupDocPrefix.class, sql, false);
    main.clear();
  }

  public static void updateDocPrefixByAccountGroup(Main main, String tempCode, AccountGroup group, AccountingFinancialYear financialYear) {
    String sql = "update scm_account_group_doc_prefix agdp \n"
            + "SET doc_prefix=REPLACE(doc_prefix,?,?) where agdp.account_group_id = ?  and financial_year_id=?";
    main.clear();
    main.param(tempCode);
    main.param(group.getGroupCode());
    main.param(group.getId());
    main.param(financialYear.getId());
    AppService.updateSql(main, AccountGroupDocPrefix.class, sql, false);
    main.clear();
  }

  public static List<AccountGroupDocPrefix> selectSalesCustomAccountGroupDocPrefix(Main main, AccountGroup accountGroup) {
    return AppService.list(main, AccountGroupDocPrefix.class,
            "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id in(4,5,7,8,13,14) and financial_year_id=?",
            new Object[]{accountGroup.getId(), accountGroup.getFinancialYear().getId()});
  }

  public static List<AccountGroupDocPrefix> selectPurchaseCustomAccountGroupDocPrefix(Main main, AccountGroup accountGroup) {
    return AppService.list(main, AccountGroupDocPrefix.class,
            "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id in(1,2,3,15,16,17,22) and financial_year_id=?",
            new Object[]{accountGroup.getId(), accountGroup.getFinancialYear().getId()});
  }

//   public static final List<AccountGroupDocPrefix> selectSalesCustomAccountGroupDocPrefix(Main main, AccountGroup accountGroup) {
//
//    return AppService.list(main, AccountGroupDocPrefix.class, "select * from scm_account_group_doc_prefix "
//            + "where account_group_id = ? and prefix_type_id not in(1,2,3,9,10,15,16,17,18,19,6,12) order by account_group_id asc", new Object[]{accountGroup.getId()});
//  }
  public static final void insertPurchasePrefixes(Main main, Company company) {
    List<CompanyFinancialYear> financialYearList = AppService.list(main, CompanyFinancialYear.class, "select * from fin_company_fiancial_year where company_id=?",
            new Object[]{company.getId()});
    for (CompanyFinancialYear finYear : financialYearList) {
      AccountingFinancialYear financialYear = finYear.getFinancialYearId();
      Company companyId = CompanyService.selectByPk(main, company);
      String prefix = companyId.getCompanyCode();

      /**
       * Purchase Requisition Prefix
       */
      AccountGroupDocPrefix accountGroupDocPrefix = getAccountGroupDocPrefix(companyId, null, null, prefix, UserRuntimeService.activeGenericStatus(main.em()), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX, PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, financialYear);
      AccountGroupDocPrefix existingDocPrefix = docPrefixExists(main, company, accountGroupDocPrefix, financialYear);
      if (existingDocPrefix == null) {
        insert(main, accountGroupDocPrefix);
      } else {
        existingDocPrefix.setDocPrefix(accountGroupDocPrefix.getDocPrefix());
        updateByPk(main, existingDocPrefix);
      }

      /**
       * Purchase Order Prefix
       */
      accountGroupDocPrefix = getAccountGroupDocPrefix(companyId, null, null, prefix, UserRuntimeService.activeGenericStatus(main.em()), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX, PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, financialYear);
      existingDocPrefix = docPrefixExists(main, company, accountGroupDocPrefix, financialYear);
      if (existingDocPrefix == null) {
        insert(main, accountGroupDocPrefix);
      } else {
        existingDocPrefix.setDocPrefix(accountGroupDocPrefix.getDocPrefix());
        updateByPk(main, existingDocPrefix);
      }

      /**
       * Purchase Return Prefix
       */
      accountGroupDocPrefix = getAccountGroupDocPrefix(companyId, null, null, prefix, UserRuntimeService.activeGenericStatus(main.em()), PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX, PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, financialYear);
      existingDocPrefix = docPrefixExists(main, company, accountGroupDocPrefix, financialYear);
      if (existingDocPrefix == null) {
        insert(main, accountGroupDocPrefix);
      } else {
        existingDocPrefix.setDocPrefix(accountGroupDocPrefix.getDocPrefix());
        updateByPk(main, existingDocPrefix);
      }

      /**
       * Product Entry Prefix
       */
      accountGroupDocPrefix = getAccountGroupDocPrefix(companyId, null, null, prefix, UserRuntimeService.activeGenericStatus(main.em()), PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX, PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX_ID, financialYear);
      existingDocPrefix = docPrefixExists(main, company, accountGroupDocPrefix, financialYear);
      if (existingDocPrefix == null) {
        insert(main, accountGroupDocPrefix);
      } else {
        existingDocPrefix.setDocPrefix(accountGroupDocPrefix.getDocPrefix());
        updateByPk(main, existingDocPrefix);
      }

      /**
       * Purchase Consignment Prefix
       */
      accountGroupDocPrefix = getAccountGroupDocPrefix(companyId, null, null, prefix, UserRuntimeService.activeGenericStatus(main.em()), PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX, PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX_ID, financialYear);
      existingDocPrefix = docPrefixExists(main, company, accountGroupDocPrefix, financialYear);
      if (existingDocPrefix == null) {
        insert(main, accountGroupDocPrefix);
      } else {
        existingDocPrefix.setDocPrefix(accountGroupDocPrefix.getDocPrefix());
        updateByPk(main, existingDocPrefix);
      }

      /**
       * Purchase Return Consignment Prefix
       */
      accountGroupDocPrefix = getAccountGroupDocPrefix(companyId, null, null, prefix, UserRuntimeService.activeGenericStatus(main.em()), PrefixTypeService.PURCHASE_RETURN_CONSIGNMENT_PREFIX, PrefixTypeService.PURCHASE_RETURN_CONSIGNMENT_PREFIX_ID, financialYear);
      existingDocPrefix = docPrefixExists(main, company, accountGroupDocPrefix, financialYear);
      if (existingDocPrefix == null) {
        insert(main, accountGroupDocPrefix);
      } else {
        existingDocPrefix.setDocPrefix(accountGroupDocPrefix.getDocPrefix());
        updateByPk(main, existingDocPrefix);
      }

      /**
       * Stock Adjustment Prefix
       */
      accountGroupDocPrefix = getAccountGroupDocPrefix(companyId, null, null, prefix, UserRuntimeService.activeGenericStatus(main.em()), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX, PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, financialYear);
      existingDocPrefix = docPrefixExists(main, company, accountGroupDocPrefix, financialYear);
      if (existingDocPrefix == null) {
        insert(main, accountGroupDocPrefix);
      } else {
        existingDocPrefix.setDocPrefix(accountGroupDocPrefix.getDocPrefix());
        updateByPk(main, existingDocPrefix);

      }
    }
  }

  private static AccountGroupDocPrefix docPrefixExists(Main main, Company companyId, AccountGroupDocPrefix accountGroupDocPrefix, AccountingFinancialYear financialYear) {
    return (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
            "select * from scm_account_group_doc_prefix where company_id=? and prefix_type_id=? "
            + " and account_group_id is null and account_id is null and financial_year_id=?",
            new Object[]{companyId.getId(), accountGroupDocPrefix.getPrefixTypeId().getId(), financialYear.getId()});
  }

  public static List<AccountGroupDocPrefix> selectDocPurchasePrefixesBasedOnCompany(Main main, Company company, AccountingFinancialYear financialYear) {
    return AppService.list(main, AccountGroupDocPrefix.class,
            "select * from scm_account_group_doc_prefix "
            + "where account_group_id is null and account_id is null and company_id=? and prefix_type_id in (1,2,3,15,16,17) "
            + "and financial_year_id=? ", new Object[]{company.getId(), financialYear.getId()});
  }

  /**
   *
   * @param main
   * @param company
   * @param prefixTypeId
   * @return
   */
  public static final AccountGroupDocPrefix getDocPrefixBasedOnCompanyByAccountPrefixType(Main main, Company company, Integer prefixTypeId, boolean isDraftPrefix, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix accountGroupDocPrefix = null;

    if (isDraftPrefix) {
      accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select agdp.id,agdp.account_group_id,agdp.doc_prefix,agdp.draft_doc_number_counter as doc_number_counter,agdp.padding,agdp.year_sequence,agdp.year_padding,"
              + "agdp.prefix_type_id,agdp.sort_order,agdp.status_id,agdp.account_id,agdp.vendor_id,agdp.start_year_only"
              + " from scm_account_group_doc_prefix agdp where company_id=? and account_group_id is null "
              + "and account_id is null and prefix_type_id = ? and agdp.financial_year_id=?", new Object[]{company.getId(), prefixTypeId, financialYear.getId()});

    } else {
      accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select scm_account_group_doc_prefix.* from scm_account_group_doc_prefix  where company_id=? and account_group_id is null \n"
              + "and account_id is null and prefix_type_id = ? and financial_year_id=?", new Object[]{company.getId(), prefixTypeId, financialYear.getId()});

    }
    return accountGroupDocPrefix;
  }

  public static final void insertPurchasePrefixesBasedOnCompany(Main main, Company company, String purPrefix, String purRetPrefix, int purIndex, int purRetIndex, AccountingFinancialYear financialYear) {
    /**
     * Product Entry Prefix
     */
    AccountGroupDocPrefix purchaseDocPrefix = getAccountGroupDocPrefix(company, null, null, purPrefix, new Status(1), PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX, PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX_ID, financialYear);
    purchaseDocPrefix.setDocNumberCounter(purIndex);
    purchaseDocPrefix.setDocPrefix(purPrefix.toUpperCase());
    purchaseDocPrefix.setPadding(5);
    insert(main, purchaseDocPrefix);

    /**
     * Purchase Return Prefix
     */
    AccountGroupDocPrefix purchaseRetDocPrefix = getAccountGroupDocPrefix(company, null, null, purRetPrefix, new Status(1), PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX, PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, financialYear);
    purchaseRetDocPrefix.setDocNumberCounter(purRetIndex);
    purchaseRetDocPrefix.setDocPrefix(purRetPrefix.toUpperCase());
    purchaseRetDocPrefix.setPadding(5);
    insert(main, purchaseRetDocPrefix);

  }

  public static final String getNextPurchasePrefixSequence(Main main, Company company, Integer prefixType, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix accountGroupDocPrefix = null;
    accountGroupDocPrefix = getDocPrefixBasedOnCompanyByAccountPrefixType(main, company, prefixType, false, financialYear);
    if (accountGroupDocPrefix != null) {
      return DocumentNumberGen.getPrefixLiteral(accountGroupDocPrefix, financialYear.getStartDate());
    }
    return null;
  }

  public static final void updatePurchasePrefixSequence(Main main, Company company, Integer prefixType, AccountingFinancialYear financialYear) {
    AccountGroupDocPrefix accountGroupDocPrefix = null;
    accountGroupDocPrefix = getDocPrefixBasedOnCompanyByAccountPrefixType(main, company, prefixType, false, financialYear);
    if (accountGroupDocPrefix != null) {
      accountGroupDocPrefix.setDocNumberCounter(getNextSequence(accountGroupDocPrefix, false));
      insertOrUpdate(main, accountGroupDocPrefix);
    }
  }

  public static final void addNewPrefixesForNewFinancialYear(Main main, Company company, AccountingFinancialYear accountingFinancialYear) {
//    AppService.insertSql(main, AccountGroupDocPrefix.class, "scm_account_group_doc_prefix", "INSERT INTO scm_account_group_doc_prefix (id, account_group_id, doc_prefix, doc_number_counter, padding, year_sequence, year_padding, prefix_type_id, \n"
//            + "sort_order, status_id, created_by, created_at, account_id, vendor_id, draft_doc_number_counter, company_id, start_year_only, financial_year_id)  (\n"
//            + "SELECT nextval('s_scm_account_group_doc_prefix'),account_group_id,doc_prefix,1,padding,year_sequence,year_padding, prefix_type_id, \n"
//            + "sort_order, status_id,created_by,CURRENT_DATE,account_id, vendor_id,1, company_id, start_year_only, 3\n"
//            + "FROM scm_account_group_doc_prefix where company_id=25 and financial_year_id=2);  ", true)

    if (company != null && accountingFinancialYear != null) {
      CompanyFinancialYear lastFinYear = (CompanyFinancialYear) AppService.single(main, CompanyFinancialYear.class,
              "select id,financial_year_id from fin_company_financial_year where company_id=? and financial_year_id!=? ORDER BY id DESC limit 1",
              new Object[]{company.getId(), accountingFinancialYear.getId()});
      addNewPrefixesForTransactions(main, company, lastFinYear, accountingFinancialYear);
      addNewPrefixesForAccounting(main, company, lastFinYear, accountingFinancialYear);
    }
  }

  private static final void addNewPrefixesForTransactions(Main main, Company company, CompanyFinancialYear lastFinYear, AccountingFinancialYear accountingFinancialYear) {
    List<AccountGroupDocPrefix> docPrefixList = new ArrayList<>();

    if (lastFinYear != null) {
      docPrefixList = AppService.list(main, AccountGroupDocPrefix.class,
              "select * from scm_account_group_doc_prefix where company_id=? and financial_year_id=?",
              new Object[]{company.getId(), lastFinYear.getFinancialYearId().getId()});
      for (AccountGroupDocPrefix agdp : docPrefixList) {
        AccountGroupDocPrefix groupDocPrefix = new AccountGroupDocPrefix();
        groupDocPrefix.setAccountGroupId(agdp.getAccountGroupId());
        groupDocPrefix.setAccountId(agdp.getAccountId());
        groupDocPrefix.setCompanyId(agdp.getCompanyId());
        groupDocPrefix.setCreatedAt(new Date());
        groupDocPrefix.setCreatedBy(UserRuntimeView.instance().getAppUser().getName());
        groupDocPrefix.setDocNumberCounter(1);
        groupDocPrefix.setDocPrefix(agdp.getDocPrefix());
        groupDocPrefix.setDraftDocNumberCounter(1);
        groupDocPrefix.setFinancialYearId(accountingFinancialYear);
        groupDocPrefix.setPadding(agdp.getPadding());
        groupDocPrefix.setPrefixTypeId(agdp.getPrefixTypeId());
        groupDocPrefix.setSortOrder(agdp.getSortOrder());
        groupDocPrefix.setStartYearOnly(agdp.getStartYearOnly());
        groupDocPrefix.setStatusId(new Status(1));
        groupDocPrefix.setVendorId(agdp.getVendorId());
        groupDocPrefix.setYearPadding(agdp.getYearPadding());
        groupDocPrefix.setYearSequence(agdp.getYearSequence());
        insert(main, groupDocPrefix);
      }
    }
  }

  private static final void addNewPrefixesForAccounting(Main main, Company company, CompanyFinancialYear lastFinYear, AccountingFinancialYear accountingFinancialYear) {

    AppDb.execute(main.dbConnector(), "INSERT INTO fin_accounting_prefix(id, doc_prefix, doc_number_counter, padding, year_sequence, year_padding,\n"
            + "sort_order, status_id, created_by, created_at, modified_by, modified_at, voucher_type_id, company_id, financial_year_id, start_year_only)\n"
            + "SELECT nextval('s_fin_accounting_prefix'),doc_prefix, 1, padding, year_sequence, year_padding,\n"
            + "sort_order, status_id, created_by, CURRENT_DATE, modified_by, modified_at, voucher_type_id, company_id, ?, start_year_only\n"
            + " FROM fin_accounting_prefix WHERE company_id =? AND financial_year_id=?;", new Object[]{accountingFinancialYear.getId(),
              company.getId(), lastFinYear.getFinancialYearId().getId()});
  }
}
