/*
 * @(#)AccountGroupService.java	1.0 Wed Apr 13 15:41:17 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import spica.fin.service.AccountGroupDetailService;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import spica.scm.domain.Account;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupDetail;
import spica.scm.domain.AccountGroupDocPrefix;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Brand;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerTradeProfile;
import spica.scm.domain.Status;
import spica.scm.domain.Vendor;
import spica.scm.validate.AccountGroupIs;
import spica.sys.SystemConstants;
import wawo.app.faces.AppLookup;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.AppEm;
import wawo.entity.util.StringUtil;

/**
 * AccountGroupService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:17 IST 2016
 */
public abstract class AccountGroupService {

  public static final Integer ACCOUNT_GROUP_DEFAULT = 1;

  /**
   * AccountGroup paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountGroupSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_account_group", AccountGroup.class, main);
    sql.main("select scm_account_group.id,scm_account_group.company_id,scm_account_group.group_name,scm_account_group.group_code,scm_account_group.note,scm_account_group.sort_order,"
            + "scm_account_group.created_by,scm_account_group.modified_by,scm_account_group.created_at,scm_account_group.modified_at from scm_account_group scm_account_group "); //Main query
    sql.count("select count(scm_account_group.id) as total from scm_account_group scm_account_group "); //Count query
    sql.join("left outer join scm_company scm_account_groupcompany_id on (scm_account_groupcompany_id.id = scm_account_group.company_id)"); //Join Query

    sql.string(new String[]{"scm_account_groupcompany_id.company_name", "scm_account_group.group_code", "scm_account_group.group_name", "scm_account_group.note", "scm_account_group.created_by", "scm_account_group.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_account_group.id", "scm_account_group.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_account_group.created_at", "scm_account_group.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountGroup.
   *
   * @param main
   * @return List of AccountGroup
   */
  public static final List<AccountGroup> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountGroupSqlPaged(main));
  }

  /**
   * Select the list of related records based on parent id.
   *
   * @param main
   * @param company
   * @return
   */
  public static final List<AccountGroup> listPagedAccountGroup(Main main, Company company) {
    SqlPage sql = getAccountGroupSqlPaged(main);
    sql.cond("where scm_account_group.company_id = ? and scm_account_group.is_default = ?");
    sql.param(company.getId());
    sql.param(0);
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of AccountGroup based on condition
//   * @param main
//   * @return List<AccountGroup>
//   */
//  public static final List<AccountGroup> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountGroupSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountGroup by key.
   *
   * @param main
   * @param accountGroup
   * @return AccountGroup
   */
  public static final AccountGroup selectByPk(Main main, AccountGroup accountGroup) {
    return (AccountGroup) AppService.find(main, AccountGroup.class, accountGroup.getId());
  }

  /**
   * Insert AccountGroup.
   *
   * @param main
   * @param accountGroup
   */
  public static final void insert(Main main, AccountGroup accountGroup) {
    AccountGroupIs.insertAble(main, accountGroup);  //Validating
    AppService.insert(main, accountGroup);

  }

  /**
   * Update AccountGroup by key.
   *
   * @param main
   * @param accountGroup
   * @return AccountGroup
   */
  public static final AccountGroup updateByPk(Main main, AccountGroup accountGroup) {
    AccountGroupIs.updateAble(main, accountGroup); //Validating
    return (AccountGroup) AppService.update(main, accountGroup);
  }

  /**
   *
   * @param main
   * @param accountGroup
   */
  public static void insertOrUpdate(Main main, AccountGroup accountGroup) {
    if (accountGroup.getId() == null) {
      insert(main, accountGroup);
    } else {
      updateByPk(main, accountGroup);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountGroup
   */
  public static void clone(Main main, AccountGroup accountGroup) {
    accountGroup.setId(null); //Set to null for insert
    insert(main, accountGroup);
  }

  /**
   * Delete AccountGroup.
   *
   * @param main
   * @param accountGroup
   */
  public static final int deleteByPk(Main main, AccountGroup accountGroup) {
    AccountGroupIs.deleteAble(main, accountGroup); //Validation
    AppService.deleteSql(main, AccountGroupDetail.class, "delete from scm_account_group_detail where account_group_id = ?", new Object[]{accountGroup.getId()});
    AppService.deleteSql(main, AccountGroupDetail.class, "delete from scm_account_group_doc_prefix where account_group_id = ?", new Object[]{accountGroup.getId()});
    AppService.deleteSql(main, AccountGroupDetail.class, "delete from scm_account_group_price_list where account_group_id = ?", new Object[]{accountGroup.getId()});
    AppService.delete(main, AccountGroup.class, accountGroup.getId());
    return 0;
  }

  /**
   * Delete Array of AccountGroup.
   *
   * @param main
   * @param accountGroup
   */
  public static final int deleteByPkArray(Main main, AccountGroup[] accountGroup) {
    int rvalue = 0;
    for (AccountGroup e : accountGroup) {
      rvalue = deleteByPk(main, e);
      if (rvalue == 1) {
        return 1;
      }
    }
    return 0;
  }

  /**
   *
   * @param main
   * @param account
   * @return
   */
  public static final List<AccountGroup> selectByAccount(Main main, Account account) {
    List<AccountGroup> accountGroupList = main.em().list(AccountGroup.class, "select sag.id, sag.company_id, sag.account_id, sag.group_name, sag.invoice_prefix, sag.invoice_no_counter, sag.note, sag.sort_order, sag.is_default, sag.prefix_type_id "
            + "from scm_account_group sag "
            + "where sag.account_id = ? and sag.prefix_type_id not in(1,2,3,6) order by sag.is_default desc, sag.id desc ", new Object[]{account.getId()});
    return accountGroupList;
  }

  /**
   * Select the list of related records based on parent id.
   *
   * @param main
   * @param accountGroupSelected
   * @param account
   */
  public static void insertArray(Main main, AccountGroup[] accountGroupSelected, Account account) {
    if (accountGroupSelected != null) {
      AccountGroupDetail accountGroupDetail;
      for (AccountGroup accountGroup : accountGroupSelected) {  //Reinserting
        accountGroupDetail = new AccountGroupDetail();
        accountGroupDetail.setAccountId(account);
        accountGroupDetail.setAccountGroupId(accountGroup);
        AccountGroupDetailService.insert(main, accountGroupDetail);
      }
    }
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @param accountList
   * @return
   */
  public static AccountGroup insertOrUpdate(Main main, AccountGroup accountGroup, List<Account> accountList, List<Brand> bList) {
    boolean isNew = false;
    List<Account> list = null;
    List<Account> accList = new ArrayList<>(accountList);

    if (accountGroup != null && accountGroup.getId() == null) {
      isNew = true;
    }

    for (Account account : accountList) {
      if (account.getAccountId() == null) {
        list = selectChildAccountLists(main, account);
        if (!StringUtil.isEmpty(list)) {
          for (Account ac : accList) {
            if (!accList.contains(ac)) {
              accList.add(ac);
            }
          }
        }
      }
    }

    list = null;
    accountGroup.setAccountGroupHashCode(getAccountGroupHashCode(accList, bList));
    insertOrUpdate(main, accountGroup);

    if (accountList != null && !accountList.isEmpty()) {
      if (accountGroup.getId() != null) {
        AppService.deleteSql(main, AccountGroupDetail.class, "delete from scm_account_group_detail where account_group_id = ?", new Object[]{accountGroup.getId()});
      }
      main.em().flush();
      insertAccountGroupDetail(main, accountGroup, accList);
    }

    if (isNew) {
//      AccountGroupDocPrefixService.insertSalesPrefixes(main, null, accountGroup);
      AccountGroupPriceList accountGroupPriceList = new AccountGroupPriceList();
      accountGroupPriceList.setAccountGroupId(accountGroup);
      accountGroupPriceList.setTitle(accountGroup.getGroupCode() + "_PL");
      accountGroupPriceList.setIsDefault(1);
      AccountGroupPriceListService.insert(main, accountGroupPriceList);
    } else {
      AccountGroupDocPrefixService.insertOrUpdate(main, accountGroup.getAccountGroupDocPrefix());
    }

    return accountGroup;
  }

  private static void insertAccountGroupDetail(Main main, AccountGroup accountGroup, List<Account> accountList) {
    for (Account account : accountList) {
      if (!AppService.exist(main, "select '1' from scm_account_group_detail where account_group_id = ? AND account_id = ?", new Object[]{accountGroup.getId(), account.getId()})) {
        AccountGroupDetailService.insert(main, new AccountGroupDetail(accountGroup, account));
      }
    }
  }

  private static String getAccountGroupHashCode(List<Account> acList, List<Brand> bList) {
    acList.sort((a1, a2) -> a1.getId().compareTo(a2.getId()));
    bList.sort((b1, b2) -> b1.getId().compareTo(b2.getId()));

    String brandHash = bList.stream().map(b -> b.getId().toString()).collect(Collectors.joining("#"));
    String accountHash = acList.stream().map(a -> a.getId().toString()).collect(Collectors.joining("#"));

    return (accountHash + "@" + brandHash);
  }

  public static List<Account> selectAccoutByAccountGroup(MainView main, AccountGroup accountGroup) {
    return AppService.list(main, Account.class, "select * from scm_account where id in (select account_id from scm_account_group_detail where account_group_id = ?)", new Object[]{accountGroup.getId()});
  }

  /**
   *
   * @param main
   * @param account
   * @return AccountGroup
   */
  public static AccountGroup insertDefaultAccountGroupAndDetail(Main main, Account account, String groupName, String prefix) {
    /**
     * Insert Default Account group
     */
    AccountGroup defualtAccountGroup = getAccountGroup(account.getCompanyId(), groupName, prefix, new Status(StatusService.STATUS_ACTIVE), ACCOUNT_GROUP_DEFAULT);
    insert(main, defualtAccountGroup);

    /**
     * Insert AccountGroupDetail for default AccountGroup
     */
    AccountGroupDetailService.insertPrimaryVendorsAccountGroup(main, account, defualtAccountGroup);

    // Selecting list of brands
    List<Brand> brandList = AppService.list(main, Brand.class, "select * from scm_brand where id in(select brand_id from scm_supplier_brand where vendor_id = ?)", new Object[]{account.getVendorId().getId()});

    // Mapping brands with Account Group
    AccountGroupBrandsService.insertOrUpdate(main, defualtAccountGroup, brandList);

    return defualtAccountGroup;
  }

  /**
   *
   * @param company
   * @param groupName
   * @param prefix
   * @param statusId
   * @param isDefualt
   * @return
   */
  private static AccountGroup getAccountGroup(Company company, String groupName, String prefix, Status statusId, int isDefualt) {
    AccountGroup accountGroup = new AccountGroup();
    accountGroup.setGroupName(groupName);
    accountGroup.setCompanyId(company);
    accountGroup.setSortOrder(1);
    accountGroup.setIsDefault(isDefualt);
    accountGroup.setGroupCode(prefix);
    accountGroup.setStatusId(statusId);
    accountGroup.setServiceAsExpense(SystemConstants.SERVICE_AS_NON_EXPENSE);
    return accountGroup;
  }

  /**
   *
   * @param main
   * @param accountGroupId
   * @return
   */
  public static List<AccountGroupDocPrefix> selectAccountPurchasePrefixes(Main main, Integer accountGroupId) {
    return AppService.list(main, AccountGroupDocPrefix.class, "select * from scm_account_group_doc_prefix where account_group_id = ? and prefix_type_id in (1,2,3)", new Object[]{accountGroupId});
  }

  /**
   *
   * @param main
   * @param accountId
   * @return
   */
  public static List<AccountGroup> selectAccountSalesPurchasePrefixes(MainView main, Integer accountId) {
    return AppService.list(main, AccountGroup.class, "select * from scm_account_group where account_id = ? and prefix_type_id in (5)", new Object[]{accountId});
  }

  public static void updateSalesRequisitionPrefixSequence(Main main, AccountGroup accountGroup) {
    main.param(accountGroup.getId());
    AppService.updateSql(main, AccountGroup.class, "update scm_account_group set invoice_no_counter = (invoice_no_counter + 1) where id = ?", false);
    main.clear();
  }

  public static long isAccountGroupExist(Main main, Integer accountGroupId, List<Account> accountList) {
    StringBuilder sb = new StringBuilder();
    String whereCondition = "";
    for (Account act : accountList) {
      if (sb.length() == 0) {
        sb.append(act.getId());
      } else {
        sb.append(",").append(act.getId());
      }
    }
    if (accountGroupId != null) {
      whereCondition = " account_group_id <> " + accountGroupId + " and ";
    }
    long count = main.em().count("select count(account_id) account_count from scm_account_group_detail "
            + "where " + whereCondition + " account_group_id in ( "
            + "select account_group_id from scm_account_group_detail where account_id in (" + sb.toString() + ") "
            + "GROUP by account_group_id "
            + "having count(account_group_id) = " + accountList.size()
            + ") GROUP by account_group_id having count(account_id) = " + accountList.size(), null);

    return count;
  }

  public static AccountGroup selectDefaultAccountGroup(Main main, Account account) {
    return (AccountGroup) AppService.single(main, AccountGroup.class, "select * from scm_account_group where account_id = ? and is_default = ?", new Object[]{account.getId(), 1});
  }

  public static List<AccountGroup> customerAccountGroup(Main main, Company company, CustomerTradeProfile cuTrade) {
    List<AccountGroup> accountGroupList = main.em().list(AccountGroup.class, "select scm_account_group.id,scm_account_group.group_name\n"
            + "from scm_account_group scm_account_group \n"
            + "left join scm_account_group_detail scm_account_group_detail on (scm_account_group_detail.account_group_id = scm_account_group.id) \n"
            + "left join scm_account scm_account_agd on (scm_account_group_detail.account_id = scm_account_agd.id) \n"
            + "left join scm_account scm_account_ag on (scm_account_group.account_id = scm_account_ag.id) \n"
            + "left join scm_trade_profile tp1 on tp1.id = scm_account_agd.company_trade_profile_id \n"
            + "left join scm_trade_profile tp2 on tp2.id = scm_account_ag.company_trade_profile_id \n"
            + "where (scm_account_group.account_id is null or scm_account_group.is_default = ?) \n"
            + "and (tp1.trade_level < ? or tp2.trade_level < ?)\n"
            + "and scm_account_group.company_id = ?", new Object[]{1, cuTrade.getTradeProfileId().getTradeLevel(), cuTrade.getTradeProfileId().getTradeLevel(), company.getId()});
    return accountGroupList;
  }

  /**
   *
   * @param main
   * @param account
   * @return
   */
  public static final AccountGroup selectDefaultAccountGroupByAccount(Main main, Account account) {
    Account primaryVendorAccount = account;
    if (account.getVendorId().getVendorType().equals(VendorService.VENDOR_TYPE_SECONDARY) && AccountService.GROUP_SUPPLIER.equals(account.getSalesChannel())) {
      primaryVendorAccount = account.getAccountId();
    }
    AccountGroup accountGroup = (AccountGroup) AppService.single(main, AccountGroup.class, "select scm_account_group.* from scm_account_group scm_account_group "
            + "inner join scm_account_group_detail scm_account_group_detail on scm_account_group_detail.account_group_id = scm_account_group.id "
            + "and scm_account_group.is_default = 1 and scm_account_group_detail.account_id = ?", new Object[]{primaryVendorAccount.getId()});

    return accountGroup;
  }

  public static final void updateDefaultAccountGroupStatus(Main main, AccountGroup defaultAccountGroup) {
    main.clear();
    main.param(defaultAccountGroup.getId());
    AppService.updateSql(main, AccountGroup.class, "update scm_account_group set status_id = 1 where id = ?", false);
    main.clear();
  }

  public static final List<Brand> lookupBrandBySupplier(Main main, List<Account> accountList) {
    List<Vendor> vlist = null;
    List<Integer> ids = new ArrayList<>();
    if (!StringUtil.isEmpty(accountList)) {
      for (Account acc : accountList) {

        ids.add(acc.getVendorId().getId());

        vlist = AppService.list(main, Vendor.class, "select id from scm_vendor where id in (select vendor_id from scm_account where account_id = ?)", new Object[]{acc.getId()});

        if (!StringUtil.isEmpty(vlist)) {
          for (Vendor v : vlist) {
            ids.add(v.getId());
          }
        }
        vlist = null;
      }

      String jpql = "select b from Brand b where b.id in(select sb.brandId.id from SupplierBrand sb where sb.vendorId in ?1)";
      return main.em().listJpql(Brand.class, jpql, new Object[]{ids});

    }
    return null;

  }

  public static final void updateAccountGroup(Main main, AccountGroup accountGroup) {
    if (accountGroup != null && accountGroup.getId() != null) {
      AppService.update(main, accountGroup);
    }
  }

//  public static AccountGroup selectDefalutGroupByAccount(Main main, Account account) {
//    String sql = "select g.id \n"
//            + "from scm_account_group_detail d,scm_account_group g\n"
//            + "where d.account_group_id=g.id\n"
//            + "AND g.is_default=1 \n"
//            + "AND d.account_id= ? ";
//     return  (AccountGroup) AppService.single(main, AccountGroup.class, sql, new Object[]{account.getId()});
//
//  }
  public static List<Account> selectChildAccountLists(Main main, Account account) {
    String sql = "SELECT * FROM scm_account WHERE account_id = ?";
    return AppService.list(main, Account.class, sql, new Object[]{account.getId()});
  }

  public static List<AccountGroup> selectAccountGroupByCustomer(Main main, Customer customer, String filter) {
    List<Object> params = new ArrayList<>();
    params.add(customer.getId());
    String sql = "\n"
            + "SELECT t3.id,t3.group_name,t3.group_code fROM fin_trade_outstanding t1,scm_account_group_detail t2,scm_account_group t3\n"
            + "WHERE t1.account_id = t2.account_id\n"
            + "AND t3.id = t2.account_group_id\n"
            + "AND t1.customer_id = ? \n";
    if (!StringUtils.isEmpty(filter)) {
      sql += " AND upper(t3.group_name) like ? ";
      params.add("%" + filter.toUpperCase() + "%");
    }
    sql += " GROUP BY t3.id,t3.group_name,t3.group_code";

    return AppService.list(main, AccountGroup.class, sql, params.toArray());

  }

  public static List<AccountGroup> selectAccountGroupByCompany(Main main, Company company) {
    return AppService.list(main, AccountGroup.class, "select * from scm_account_group where company_id=? AND status_id = 1 order by group_name ASC", new Object[]{company.getId()});
  }
}
