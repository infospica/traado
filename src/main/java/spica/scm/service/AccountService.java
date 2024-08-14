/*
 * @(#)AccountService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupDetail;
import spica.scm.domain.AccountGroupDocPrefix;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Company;
import spica.scm.domain.Contract;
import spica.scm.domain.Customer;
import spica.scm.domain.TradeProfile;
import spica.scm.domain.Vendor;
import spica.scm.validate.AccountIs;
import spica.scm.view.ScmLookupSql;

/**
 * AccountService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class AccountService {

  public static final Integer ACTIVE = 1;
  public static final Integer DRAFT = 2;
  // public static final Integer INTERSTATE_PURCHASE = 1;
  // public static final Integer INTRASTATE_PURCHASE = 2;
  //public static final Integer FREE_SCHEME = 1;
  //public static final Integer QUANTITY_SCHEME = 0;
  public static final Integer PURCHASE_CHANNEL = 1;
  public static final Integer SALES_CHANNEL = 2;
  public static final Integer INDIVIDUAL_SUPPLIER = 1;
  public static final Integer GROUP_SUPPLIER = 2;
  public static final Integer RENDER_PTR = 1;
  public static final Integer RENDER_PTS = 1;
  public static final Integer PTS_DERIVATION_CRITERIA = 1;
  public static final Integer DERIVE_PTS_FROM_MRP_PTR = 1;
  public static final Integer DERIVE_PTS_FROM_RATE = 2;
  public static final Integer DERIVE_PTS_FROM_LANDING_RATE = 3;

  /**
   * Account paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_account", Account.class, main);
    sql.main("select scm_account.id,scm_account.vendor_id,scm_account.company_id,scm_account.company_trade_profile_id,scm_account.account_code,scm_account.account_title,"
            + "scm_account.account_id,scm_account.vendor_bank_id,scm_account.company_bank_id,scm_account.vendor_address_id,scm_account.company_address_id,scm_account.account_status_id,"
            + "scm_account.is_purchase_interstate,scm_account.purchase_level,scm_account.statutory_form_type,scm_account.commodity_movement_type,scm_account.vendor_tin_no,"
            + "scm_account.vendor_cst_no,scm_account.vendor_gst_no,scm_account.note,scm_account.is_scheme_applicable, scm_account.purchase_channel,scm_account.sales_channel,"
            + "scm_account.created_by,scm_account.modified_by,scm_account.created_at,scm_account.modified_at from scm_account scm_account "); //Main query
    sql.count("select count(scm_account.id) as total from scm_account scm_account "); //Count query
    sql.join("left outer join scm_vendor scm_accountvendor_id on (scm_accountvendor_id.id = scm_account.vendor_id) "
            + "left outer join scm_company scm_accountcompany_id on (scm_accountcompany_id.id = scm_account.company_id) "
            + "left outer join scm_account primary_account on (primary_account.id = scm_account.account_id) "
            + "left outer join scm_trade_profile scm_accountcompany_trade_profile_id on (scm_accountcompany_trade_profile_id.id = scm_account.company_trade_profile_id) "
            + "left outer join scm_vendor_bank scm_accountvendor_bank_id on (scm_accountvendor_bank_id.id = scm_account.vendor_bank_id) "
            + "left outer join scm_company_bank scm_accountcompany_bank_id on (scm_accountcompany_bank_id.id = scm_account.company_bank_id) "
            + "left outer join scm_vendor_address scm_accountvendor_address_id on (scm_accountvendor_address_id.id = scm_account.vendor_address_id) "
            + "left outer join scm_company_address scm_accountcompany_address_id on (scm_accountcompany_address_id.id = scm_account.company_address_id) "
            + "left outer join scm_account_status scm_accountaccount_status_id on (scm_accountaccount_status_id.id = scm_account.account_status_id)"); //Join Query

    sql.string(new String[]{"primary_account.account_code", "scm_accountvendor_id.vendor_name", "scm_accountcompany_id.company_name", "scm_accountcompany_trade_profile_id.title", "scm_account.account_code", "scm_account.account_title", "scm_account.vendor_tin_no", "scm_account.vendor_cst_no", "scm_account.vendor_gst_no", "scm_accountvendor_bank_id.account_name", "scm_accountcompany_bank_id.account_name", "scm_accountvendor_address_id.address", "scm_accountcompany_address_id.address", "scm_accountaccount_status_id.title", "scm_account.note", "scm_account.created_by", "scm_account.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_account.id", "scm_account.is_purchase_interstate", "scm_account.purchase_level", "scm_account.statutory_form_type", "scm_account.commodity_movement_type"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_account.created_at", "scm_account.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Account.
   *
   * @param main
   * @param company
   * @return List of Account
   */
  public static final List<Account> listPaged(Main main, Company company) {
    SqlPage sql = getAccountSqlPaged(main);
    sql.cond("where scm_account.company_id = ?");
    sql.param(company.getId());
    if (main.getPageData().getSortField() == null) {
      sql.orderBy("UPPER(scm_account.account_code)");
    }
    List<Account> accountList = AppService.listPagedJpa(main, sql);
    for (Account account : accountList) {
      Contract contract = ContractService.selectContractByAccount(main, account.getId());
      if (contract != null) {
        account.setContract(contract);
      }
    }
    return accountList;
  }

  /**
   *
   * @param main
   * @param vendor
   * @return
   */
  public static Account selectAccountByVendor(Main main, Vendor vendor) {
    return (Account) AppService.single(main, Account.class, "select * from scm_account scm_account where scm_account.vendor_id=?", new Object[]{vendor.getId()});
  }

//    public static Account selectAccountByVendorPrimary(Main main, Vendor vendor) {
//    return (Account) AppService.single(main, Account.class, "select * from scm_account scm_account where scm_account.vendor_id=? and account_id is null", new Object[]{vendor.getId()});
//  }
  /**
   * Return List of Vendor.
   *
   * @param main
   * @param company
   * @return List of Vendor
   */
//  public static final List<Account> selectAccountByCompany(Main main, Company company) {
//    SqlPage sql = getAccountSqlPaged(main);
//    sql.cond("where scm_account.company_id = ?");
//    sql.param(company.getId());
//    return AppService.listPagedJpa(main, getAccountSqlPaged(main));
//  }
//  /**
//   * Return list of Account based on condition
//   * @param main
//   * @return List<Account>
//   */
//  public static final List<Account> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Account by key.
   *
   * @param main
   * @param account
   * @return Account
   */
  public static final Account selectByPk(Main main, Account account) {
    return (Account) AppService.find(main, Account.class, account.getId());
  }

  /**
   * Insert Account.
   *
   * @param main
   * @param account
   */
  public static final void insert(Main main, Account account) {
    AccountIs.insertAble(main, account);  //Validating
    AppService.insert(main, account);

  }

  /**
   * Update Account by key.
   *
   * @param main
   * @param account
   * @return Account
   */
  public static final Account updateByPk(Main main, Account account) {
    AccountIs.updateAble(main, account); //Validating
    return (Account) AppService.update(main, account);
  }

  /**
   * Insert or update Account
   *
   * @param main
   * @param account
   */
  public static void insertOrUpdate(Main main, Account account) {
    if (account.getVendorId().getVendorType().equals(VendorService.VENDOR_TYPE_SECONDARY)) {
      account.setAccountId(selectAccountByVendor(main, account.getVendorId().getVendorId()));
    }
    if (account.getId() == null) {
      insert(main, account);
    } else {
      updateByPk(main, account);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param account
   */
  public static void clone(Main main, Account account) {
    account.setId(null); //Set to null for insert
    insert(main, account);
  }

  /**
   * Delete Account.
   *
   * @param main
   * @param account
   */
  public static final void deleteByPk(Main main, Account account) {
    AccountIs.deleteAble(main, account); //Validation
    AccountGroup defaultAccountGroup = AccountGroupService.selectDefaultAccountGroupByAccount(main, account);
    if (account.getVendorId().getVendorType().equals(VendorService.VENDOR_TYPE_PRIMARY)) {
      AppService.deleteSql(main, AccountGroupPriceList.class, "delete from scm_account_group_price_list "
              + "where account_group_id in (select account_group_id from scm_account_group_detail where account_id = ?)", new Object[]{account.getId()});
      AppService.deleteSql(main, AccountGroupDocPrefix.class, "delete from scm_account_group_doc_prefix "
              + "where account_group_id in (select account_group_id from scm_account_group_detail where account_id = ?)", new Object[]{account.getId()});
      AppService.deleteSql(main, AccountGroupDetail.class, "delete from scm_account_group_detail where account_id = ?", new Object[]{account.getId()});
      AppService.deleteSql(main, AccountGroup.class, "delete from scm_account_group where id = ?", new Object[]{defaultAccountGroup.getId()});
      AppService.deleteSql(main, AccountGroupDocPrefix.class, "delete from scm_account_group_doc_prefix where account_id = ?", new Object[]{account.getId()});
      AppService.deleteSql(main, Contract.class, "delete from scm_contract where account_id = ?", new Object[]{account.getId()});
      AppService.delete(main, Account.class, account.getId());
    } else if (account.getVendorId().getVendorType().equals(VendorService.VENDOR_TYPE_SECONDARY)) {
      AppService.deleteSql(main, AccountGroupDetail.class, "delete from scm_account_group_detail where account_group_id = ? and account_id = ?", new Object[]{defaultAccountGroup.getId(), account.getId()});
      AppService.deleteSql(main, AccountGroupDocPrefix.class, "delete from scm_account_group_doc_prefix where account_id = ?", new Object[]{account.getId()});
      if (AccountService.INDIVIDUAL_SUPPLIER.equals(account.getSalesChannel())) {
        AppService.deleteSql(main, AccountGroupPriceList.class, "delete from scm_account_group_price_list where account_group_id  = ?", new Object[]{defaultAccountGroup.getId()});
        AppService.deleteSql(main, AccountGroupDocPrefix.class, "delete from scm_account_group_doc_prefix where account_group_id = ?", new Object[]{defaultAccountGroup.getId()});
        AppService.deleteSql(main, AccountGroupDetail.class, "delete from scm_account_group_detail where account_id = ?", new Object[]{account.getId()});
        AppService.deleteSql(main, AccountGroup.class, "delete from scm_account_group where id = ?", new Object[]{defaultAccountGroup.getId()});
        AppService.deleteSql(main, AccountGroupDocPrefix.class, "delete from scm_account_group_doc_prefix where account_id = ?", new Object[]{account.getId()});
      }
      AppService.delete(main, Account.class, account.getId());
    }

  }

  /**
   * Delete Array of Account.
   *
   * @param main
   * @param account
   */
  public static final void deleteByPkArray(Main main, Account[] account) {
    for (Account e : account) {
      deleteByPk(main, e);
    }
  }

  /**
   * The method updates the status of Account to inactive state. While updating an Account to inactive then the active Contract should be updated to inactive status.
   *
   * @param main
   * @param accountId
   * @param accountStatusInactive
   * @param contractStatusInactive
   */
  public static void updateAccountStatus(Main main, Integer accountId, int accountStatusInactive, int contractStatusInactive) {
    main.param(accountStatusInactive);
    main.param(accountId);
    AppService.updateSql(main, Account.class, "update scm_account set account_status_id = ? where id = ?", false);
    main.clear();
    main.param(contractStatusInactive);
    main.param(accountId);
    main.param(1);
    AppService.updateSql(main, Contract.class, "update scm_contract set contract_status_id = ? where account_id = ? and contract_status_id = ?", false);
    main.clear();
  }

  public static final TradeProfile selectTradeProfileByVendor(Main main, Vendor vendorId) {
    return (TradeProfile) AppService.single(main, TradeProfile.class, "select * from scm_trade_profile where id = (select company_trade_profile_id from scm_account where vendor_id = ?)", new Object[]{vendorId.getId()});
  }

  /**
   *
   * @param main
   * @param account
   * @param accountStatusActive
   */
  public static final void updateAccountStatus(Main main, Account account, int accountStatusActive) {
    main.clear();
    main.param(accountStatusActive);
    main.param(account.getId());
    AppService.updateSql(main, Account.class, "update scm_account set account_status_id = ? where id = ?", false);
    main.clear();
  }

  /**
   *
   * @param main
   * @param company
   * @return
   */
  public static boolean isCompanyHaveAccount(Main main, Company company) {
    return AppService.exist(main, "select '1' from scm_account where company_id = ?", new Object[]{company.getId()});
  }

  /**
   *
   * @param main
   * @param vendor
   * @return
   */
  public static boolean isVendorHaveAccount(Main main, Vendor vendor) {
    return AppService.exist(main, "select '1' from scm_account where vendor_id = ?", new Object[]{vendor.getId()});
  }

  public static List<Account> selectAccountByCustomer(Main main, Customer customer) {
    if (customer != null) {
      return AppService.list(main, Account.class, "select t1.* from scm_account t1 "
              + "inner join scm_account_group_detail t2 on t1.id = t2.account_id "
              + "inner join scm_sales_account t3 on t3.account_group_id = t2.account_group_id and t3.customer_id = ?", new Object[]{customer.getId()});
    }
    return null;
  }

  public static final void updateAccount(Main main, Account account) {
    if (account != null && account.getId() != null) {
      AppService.update(main, account);
    }
  }
  public static Account selectById(Main main, Integer accountId) {
    return (Account) AppService.single(main, Account.class, ScmLookupSql.ACCOUNT_BYID, new Object[]{accountId});
  }
//    public static List<Account> selectAccountByCustomerPrimary(Main main, Customer customer) {
//    if (customer != null) {
//      return AppService.list(main, Account.class, "select t1.* from scm_account t1 "
//              + "inner join scm_account_group_detail t2 on t1.id = t2.account_id and t1.account_id is null "
//              + "inner join scm_sales_account t3 on t3.account_group_id = t2.account_group_id and t3.customer_id = ?", new Object[]{customer.getId()});
//    }
//    return null;
//  }
//  public static List<Account> selectAccountsByCompany(Main main, Company company) {
//    if (company != null && company.getId() != null) {
//      return AppService.list(main, Account.class, "select id,vendor_id,account_code,account_title,account_id from scm_account where company_id=? and account_status_id = 1 order by account_title ASC", new Object[]{company.getId()});
//    }
//    return null;
//  }
}
