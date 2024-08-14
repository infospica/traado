/*
 *  @(#)UserRuntimeService            30 Mar, 2016
 * 
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 * 
 */
package spica.sys;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Contract;
import spica.scm.domain.Country;
import spica.scm.domain.Customer;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.Status;
import spica.scm.domain.StockStatus;
import spica.scm.domain.SupplierGroup;
import spica.scm.domain.Territory;
import spica.scm.domain.UserAccountPreferences;
import spica.scm.domain.UserProfile;
import spica.scm.service.UserProfileService;
import spica.scm.service.UserService;
import spica.scm.view.ScmLookupView;
import spica.sys.domain.Menu;
import spica.sys.domain.User;
import spica.sys.domain.UserMenuFavorite;
import wawo.app.AppUser;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.config.AppConfig;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.AppEm;
import wawo.entity.core.FileData;
import wawo.entity.util.Secure;
import wawo.entity.util.StringUtil;

/**
 * Provide service level run time data of a logged in user.
 *
 * @author <a href=mailto:arun.vc@infospica.com>Arun VC</a>
 */
public abstract class UserRuntimeService {

  static {
    AppEm.unwrapFindClass = Status.class; //dummy to get connection object for select
  }
  public static Status defaultStatus = null;
  private static final int ACTIVE_CONTRACT = 1;
  public static final int ACTIVE_ACCOUNT = 1;
  private static final int ACTIVE_STATUS = 1;
  private static final int SHOW_IN_SALES = 1;

  //USER
  static final int USER_COMOPANY = 1;
  static final int USER_VENDOR = 2;
  static final int USER_CUSTOMER = 3;
  static final int USER_TRANSPORTER = 4;

//  LEDGER AND ENTITY TYPE
  public static StockStatus STOCK_DAMAGED = new StockStatus(3);
  public static StockStatus STOCK_EXPIRED = new StockStatus(4);

  public static Country INDIA = new Country(2);
  public static Country UAE = new Country(4);
  public static Country USA = new Country(6);

  public static int ACCOUNTING_AUTO_WIDTH = 160;

  private static final String ACCOUNT_AUTO = "select id,company_trade_profile_id,account_code,vendor_id from scm_account where account_status_id=? and company_id=? and upper(account_title) like ? order by upper(account_title) asc";
  //private static final String ACCOUNT_GROUP_AUTO = "select * from scm_account_group where status_id = ? and company_id = ? and upper(group_name) like ? order by upper(group_name) asc";
  private static final String SUPPLIER_GROUP_AUTO = "select * from scm_supplier_group where status_id = ? and company_id = ? and upper(title) like ? order by upper(title) asc";

  private static final String GLOBAL_ACCOUNT_GROUP = "select * from scm_account_group where status_id = ? and company_id = ? and id in(select account_group_id "
          + "from scm_account_group_detail where account_id in(select id from scm_account where account_status_id = ? and show_in_sales=?)) order by upper(group_name) asc";

  private static final String GLOBAL_ACCOUNT_GROUP_AUTO = "select * from scm_account_group where status_id = ? and company_id = ? and id in(select account_group_id from scm_account_group_detail "
          + "where account_id in(select id from scm_account where account_status_id = ? and show_in_sales=?)) and upper(group_name) like ? order by upper(group_name) asc";

  private static final String ACCOUNT_GROUP_FILTER = "select * from scm_account_group where status_id = ? and company_id = ? and id in(select account_group_id "
          + "from scm_account_group_detail where account_id in(select id from scm_account where account_status_id = ? )) order by upper(group_name) asc";

  private static final String ACCOUNT_GROUP_FILTER_AUTO = "select * from scm_account_group where status_id = ? and company_id = ? and id in(select account_group_id from scm_account_group_detail "
          + "where account_id in(select id from scm_account where account_status_id = ? )) and upper(group_name) like ? order by upper(group_name) asc";

  public static List<Company> listCompany(AppEm em, User user) {
    if (!AppConfig.rootUser.equals(user.getLogin())) {
      //id, company_name, financial_year_start_date, country_tax_regime_id, gst_no, country_id, state_id, currency_id
      return em.list(Company.class, "select * from scm_company where id = ? or id in (select company_id from scm_user_company where user_id = ?) order by upper(company_name) asc", new Object[]{user.getUserProfileId().getCompanyId().getId(), user.getId()});
    } else {
      return em.list(Company.class, "select * from scm_company order by upper(company_name) asc", null);
    }
    //return null;

  }

  public static List<Menu> listMenuAndChildTree(AppEm em, AppUser appUser, Integer companyId) {
    return _listMenuAndChild(em, appUser, "select id, title, parent_id, icon_path, page_url from sec_menu where 1=1", false, companyId);
  }

  public static List<Menu> listMenuAndChild(AppEm em, AppUser appUser) {
    return _listMenuAndChild(em, appUser, "select id, title, parent_id, icon_path, page_url from sec_menu where status=1", true, null);
  }

  public static List<Menu> _listMenuAndChild(AppEm em, AppUser appUser, String sql, boolean isMenu, Integer companyId) {
    Object[] paramMain = null, paramChild = null;
    if (!appUser.isRoot()) {
      if (isMenu) {
        sql += " and id in(select distinct menu_id from sec_role_menu where role_id in (select role_id from sec_user_role where user_id =?))";
        paramMain = new Object[]{appUser.getId()};
      } else { //tree in role query
        sql += " and id in(select distinct menu_id from sec_role_menu where role_id in (select id from sec_role where company_id=? and is_admin=true))";
        paramMain = new Object[]{companyId};

      }
    }
    String sqlParent = sql + " and parent_id is null order by sort_order asc";
    String sqlChild = sql + " and parent_id = ? order by sort_order asc";

    List<Menu> parentMenu = em.list(Menu.class, sqlParent, paramMain);

    for (Menu parent : parentMenu) {
      if (!appUser.isRoot()) {
        if (isMenu) {
          paramChild = new Object[]{appUser.getId(), parent.getId()};
        } else {
          paramChild = new Object[]{companyId, parent.getId()};
        }
      } else {

        paramChild = new Object[]{parent.getId()};

      }
      List<Menu> childMenu = em.list(Menu.class, sqlChild, paramChild);
      for (Menu menu : childMenu) {
        menu.setParentId(parent);
      }
      parent.setParentIdMenu(childMenu);
    }
    return parentMenu;
  }

  public static final User loginCheck(AppEm em, String login, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {

    if (AppConfig.rootUser.equals(login)) {

//      User user = new User();
//      user.setLogin(AppConfig.rootUser);
//      user.setName(AppConfig.rootUser);
//      user.setPhone("9446900729");
//      user.setEmail("info@infospica.com");
//
//      UserProfile p = new UserProfile(); // Creating an empty dummy profile will not be saved to db
//      user.setUserProfileId(p);
//      p.setUserCode(user.getLogin());
//      p.setName(user.getName());
//      p.setEmail(user.getEmail());
//      p.setPhone1(user.getPhone());
//      return user;
      //Create root user and password /TODO comment this and use file in prod
      FileData fd = new FileData(AppConfig.contextPrivateFolder + "resources/x-config.is");
      fd.update("u", AppConfig.rootUser);
      fd.update("n", AppConfig.rootUser);
      fd.update("p", "$argon2i$v=19$m=65536,t=2,p=1$ATa7QYHuL7gY262Ys79jeQ$VZy/6Epf6aOHxi8qqAhePcI+qQjztFLThAWs480w41Y");
      fd.update("e", "info@infospica.com");
      fd.update("ph", "9446900729");
      fd.save();
//
//      //check for user id and password is right
      if (StringUtil.equals(fd.select("u"), login) && Secure.hashArgon2Verify(fd.select("p"), password)) {
        User user = new User();
        user.setLogin(fd.select("u"));
        user.setName(fd.select("n"));
        user.setPhone(fd.select("ph"));
        user.setEmail(fd.select("e"));

        UserProfile p = new UserProfile(); // Creating an empty dummy profile will not be saved to db
        user.setUserProfileId(p);
        p.setUserCode(user.getLogin());
        p.setName(user.getName());
        p.setEmail(user.getEmail());
        p.setPhone1(user.getPhone());
        p.setPreferedMenu(false);
        return user;
      }
    } else {
      User usr = getLoginUser(em, login);
      if (usr != null && Secure.hashArgon2Verify(usr.getPassword(), password)) {
        return usr;
      }
    }
    return null;
  }

  static User getLoginUser(AppEm em, String login) {
    Object obj = em.single(User.class, "select * from sec_user where (login = ? or email = ?) ", new Object[]{login, login});
    if (obj != null) {
      User usr = (User) obj;
      usr.setCompanyAdmin(false);
      Long count = em.count("select count(id) from sec_role where is_admin = true and id in(select role_id from sec_user_role where user_id=?)", new Object[]{usr.getId()});
      if (count > 0) {
        usr.setCompanyAdmin(true);
      }
      return usr;
    }
    return null;
  }

  static void insertOrUpdate(Main main, User appUser) {
    if (appUser.getUserProfileId() != null) {
      if (appUser.getLogin().equals(AppConfig.rootUser)) {
        UserRuntimeService.updateRootProfile(appUser);
      } else {
        UserService.updateByPk(main, appUser);
        UserProfileService.insertOrUpdate(main, appUser.getUserProfileId());
      }
    }
  }

  private static void updateRootProfile(User appUser) {
    FileData fd = new FileData(AppConfig.contextPrivateFolder + "resources/x-config.is");
    fd.update("n", appUser.getName());
    fd.update("e", appUser.getEmail());
    fd.update("ph", appUser.getPhone());
    fd.save();
  }

  public static final List<Account> listCompanyAccount(AppEm em, Company company, AppUser appUser) {
    String allAcc = "select * from scm_account scm_account where account_status_id=? and scm_account.company_id=?";
    if (AppConfig.rootUser.equals(appUser.getLogin()) || AppDb.exist(em.getDbConnector(), "select id from scm_user_account where user_id=? and account_id is null", new Object[]{appUser.getId()})) {
      return em.list(Account.class, allAcc, new Object[]{ACTIVE_ACCOUNT, company.getId()});
    } else {
      return em.list(Account.class, allAcc + " and scm_account.id in (select account_id from scm_user_account where user_id=?) ", new Object[]{ACTIVE_ACCOUNT, company.getId(), appUser.getId()});
    }
  }

  /**
   * Return list of Account Group
   *
   * @param em
   * @param company
   * @param appUser
   * @return
   */
  public static List<AccountGroup> listAccountGroupList(AppEm em, Company company, AppUser appUser) {
    if (company != null) {
      if (AppConfig.rootUser.equals(appUser.getLogin()) || AppDb.exist(em.getDbConnector(), "select id from scm_user_account where user_id=? and account_id is null", new Object[]{appUser.getId()})) {
        return em.list(AccountGroup.class, GLOBAL_ACCOUNT_GROUP, new Object[]{ACTIVE_STATUS, company.getId(), ACTIVE_STATUS, SHOW_IN_SALES});
      } else {
        return em.list(AccountGroup.class, "SELECT * FROM scm_account_group WHERE id IN( \n"
                + "SELECT t2.account_group_id FROM scm_account_group_detail t2,scm_user_account t3 WHERE \n"
                + "t2.account_id = t3.account_id and t3.user_id = ? AND t2.account_id IN(\n"
                + "SELECT id FROM scm_account WHERE account_status_id = ? AND show_in_sales=1 \n"
                + ")) AND status_id=1 and company_id = ? ORDER BY UPPER(group_name) asc ", new Object[]{appUser.getId(), ACTIVE_ACCOUNT, company.getId()});
      }
    }
    return null;
  }

  /**
   * Return List of Supplier Group.
   *
   * @param em
   * @param company
   * @param appUser
   * @return
   */
//  public static List<SupplierGroup> listSupplierGroup(AppEm em, Company company, AppUser appUser) {
//    String allSupplierGroup = "select * from scm_supplier_group where company_id = ? order by upper(title) asc";
//    if (company != null) {
//      if (AppConfig.rootUser.equals(appUser.getLogin()) || AppDb.exist(em.getDbConnector(), "select id from scm_user_account where user_id=? and account_id is null", new Object[]{appUser.getId()})) {
//        return em.list(SupplierGroup.class, allSupplierGroup, new Object[]{company.getId()});
//      } else {
//        return em.list(SupplierGroup.class, "select t1.* from scm_supplier_group t1 "
//          + "inner join scm_vendor t2 on t1.id = t2.supplier_group_id and t1.company_id = ? "
//          + "inner join scm_account t3 on t2.id = t3.vendor_id "
//          + "inner join scm_user_account t4 on t3.id = t4.account_id and t4.user_id = ? "
//          + "order by upper(t1.title) asc", new Object[]{company.getId(), appUser.getId()});
//      }
//    }
//    return null;
//  }
  public static final Integer DESIGNATION_SALES_AGENT = 14;

  public static List<Territory> selectTerritoryBySalesAgent(Main main, Integer salesAgentId, Integer companyId) {
    String sql = "select T1.id,T1.territory_name from scm_territory T1\n"
            + "left join scm_sales_agent_territory T2 on T2.territory_id = T1.id\n"
            + "left join scm_sales_agent T3 on T3.id = T2.sales_agent_id\n"
            + "where T3.id = ? AND T3.company_id = ? ";
    return AppService.list(main, Territory.class, sql, new Object[]{salesAgentId, companyId});
  }

  public static List<AccountGroup> accountGroupBySalesAgentAll(Main main, String filter, SalesAgent salesAgent) {
    List<AccountGroup> list = accountGroupBySalesAgent(main, filter, salesAgent);
    if (list != null || list.size() > 0) {
      list.add(0, new AccountGroup(0, "All"));
    }
    return list;
  }

  public static List<AccountGroup> accountGroupBySalesAgent(Main main, String filter, SalesAgent salesAgent) {
    String sql = null;
    if (salesAgent != null && salesAgent.getId() != null) {
      sql = "select t2.id,t2.group_name,t2.group_code from scm_sales_agent_account_group t1,scm_account_group t2\n"
              + "where t1.account_group_id=t2.id\n"
              + "AND t1.sales_agent_id= ? and upper(t2.group_name) like ?";
      return AppService.list(main, AccountGroup.class, sql, new Object[]{salesAgent.getId(), "%" + filter.toUpperCase() + "%"});
    }
    return null;
//      else {
//        sql = "select id,group_name,group_code from scm_account_group where company_id = ? ";
//        return AppService.list(main, AccountGroup.class, sql, new Object[]{userProfile.getCompanyId().getId(), "%" + filter.toUpperCase() + "%"});
//      }
  }

  public static List<AccountGroup> accountGroupByAccountAll(MainView main, Account account) {
    List<AccountGroup> list = accountGroupByAccount(main, account);
    if (list != null || list.size() > 0) {
      list.add(0, new AccountGroup(0, "All"));
    }
    return list;
  }

  public static List<AccountGroup> accountGroupByAccount(MainView main, Account account) {
    String sql = "select t2.* from scm_account_group_detail t1 , scm_account_group t2 WHERE t1.account_id = ? AND t1.account_group_id = t2.id";
    return AppService.list(main, AccountGroup.class, sql, new Object[]{account.getId()});
  }

  public static final List<Account> accountAutoAll(Main main, String filter, Integer companyId, User appUser) {
    List<Account> list = accountAuto(main, filter, companyId, appUser);
    if (list != null || list.size() > 0) {
      list.add(0, new Account(0, "All"));
    }
    return list;
  }

  public static final List<Account> accountAuto(Main main, String filter, Integer companyId, AppUser appUser) {
    Object[] params = new Object[]{ACTIVE_ACCOUNT, companyId, "%" + filter.toUpperCase() + "%"};
    if (AppConfig.rootUser.equals(appUser.getLogin()) || AppService.exist(main, "select id from scm_user_account where user_id=? and account_id is null", new Object[]{appUser.getId()})) {
      return AppService.list(main, ScmLookupView.accountClass(), ACCOUNT_AUTO, params);
    } else {
      return AppService.list(main, Account.class, "select * from scm_account scm_account where account_status_id=? and scm_account.company_id=? and scm_account.id in (select account_id from scm_user_account where user_id=?)  "
              + "and (upper(account_title) like ?) order by upper(account_code) asc", new Object[]{ACTIVE_ACCOUNT, companyId, appUser.getId(), "%" + filter.toUpperCase() + "%"});
    }
  }

  public static final List<AccountGroup> accountGroupAutoAll(Main main, String filter, Integer companyId, User appUser) {
    List<AccountGroup> list = accountGroupAuto(main, filter, companyId, appUser);
    if (list != null || list.size() > 0) {
      list.add(0, new AccountGroup(0, "All"));
    }
    return list;
  }

  public static final List<AccountGroup> accountGroupAuto(Main main, String filter, Integer companyId, User appUser) {
    if (AppConfig.rootUser.equals(appUser.getLogin()) || AppService.exist(main, "select id from scm_user_account where user_id=? and account_id is null", new Object[]{appUser.getId()})) {
      if (StringUtil.isEmpty(filter)) {
        return AppService.list(main, AccountGroup.class, GLOBAL_ACCOUNT_GROUP, new Object[]{ACTIVE_STATUS, companyId, ACTIVE_STATUS, SHOW_IN_SALES});
      } else {
        return AppService.list(main, AccountGroup.class, GLOBAL_ACCOUNT_GROUP_AUTO, new Object[]{ACTIVE_STATUS, companyId, ACTIVE_STATUS, SHOW_IN_SALES, "%" + filter.toUpperCase() + "%"});
      }
    } else {
      return AppService.list(main, AccountGroup.class, "select t1.* from scm_account_group t1 "
              + "inner join scm_account_group_detail t2 on t1.id = t2.account_group_id and t1.company_id = ? "
              + "inner join scm_user_account t3 on t2.account_id = t3.account_id and t3.user_id = ? "
              + "and t1.status_id = ? and upper(t1.group_name) like ?  GROUP BY t1.id  order by upper(t1.group_name) asc", new Object[]{companyId, appUser.getId(), ACTIVE_STATUS, "%" + filter.toUpperCase() + "%"});
    }
  }

  public static final List<AccountGroup> accountGroupFilterAutoAll(Main main, String filter, Integer companyId, User appUser) {
    List<AccountGroup> list = new ArrayList<>();
    if (AppConfig.rootUser.equals(appUser.getLogin()) || AppService.exist(main, "select id from scm_user_account where user_id=? and account_id is null", new Object[]{appUser.getId()})) {
      if (StringUtil.isEmpty(filter)) {
        list = AppService.list(main, AccountGroup.class, ACCOUNT_GROUP_FILTER, new Object[]{ACTIVE_STATUS, companyId, ACTIVE_STATUS});
      } else {
        list = AppService.list(main, AccountGroup.class, ACCOUNT_GROUP_FILTER_AUTO, new Object[]{ACTIVE_STATUS, companyId, ACTIVE_STATUS, "%" + filter.toUpperCase() + "%"});
      }
    } else {
      list = AppService.list(main, AccountGroup.class, "select t1.* from scm_account_group t1 "
              + "inner join scm_account_group_detail t2 on t1.id = t2.account_group_id and t1.company_id = ? "
              + "inner join scm_user_account t3 on t2.account_id = t3.account_id and t3.user_id = ? "
              + "and t1.status_id = ? and upper(t1.group_name) like ?  GROUP BY t1.id  order by upper(t1.group_name) asc", new Object[]{companyId, appUser.getId(), ACTIVE_STATUS, "%" + filter.toUpperCase() + "%"});
    }
    if (list != null || list.size() > 0) {
      list.add(0, new AccountGroup(0, "All"));
    }
    return list;
  }

  /**
   * Return list of Customers based on User Profile
   *
   * @param main
   * @param filter
   * @param company
   * @return
   */
  public static List<Customer> getCustomerList(Main main, Company company, String filter) {
    String allCustomer = "select * from scm_customer where company_id = ?  AND upper(customer_name) like ? order by upper(customer_name) asc";
    if (company != null) {
      if (AppConfig.rootUser.equals(main.getAppUser().getLogin()) || AppDb.exist(main.em().getDbConnector(), "select id from scm_user_account where user_id=? and account_id is null", new Object[]{main.getAppUser().getId()})) {
        return AppService.list(main, Customer.class, allCustomer, new Object[]{company.getId(), "%" + filter.toUpperCase() + "%"});
      } else {
        return AppService.list(main, Customer.class, "select t0.* from \n"
                + "scm_customer t0,scm_sales_account t1,scm_account_group_detail t2,scm_user_account t3\n"
                + "WHERE \n"
                + "t0.id = t1.customer_id AND t1.account_group_id = t2.account_group_id AND \n"
                + "t2.account_id = t3.account_id AND t0.company_id =? AND  t3.user_id =?  AND upper(t0.customer_name) like ? \n"
                + "order by upper(t0.customer_name) asc ", new Object[]{company.getId(), main.getAppUser().getId(), "%" + filter.toUpperCase() + "%"});
      }
    }
    return null;
  }

  public static final List<SupplierGroup> supplierGroupAuto(Main main, String filter, Integer companyId, User appUser) {
    Object[] params = new Object[]{ACTIVE_STATUS, companyId, "%" + filter.toUpperCase() + "%"};
    if (AppConfig.rootUser.equals(appUser.getLogin()) || AppService.exist(main, "select id from scm_user_account where user_id=? and account_id is null", new Object[]{appUser.getId()})) {
      return AppService.list(main, SupplierGroup.class, SUPPLIER_GROUP_AUTO, params);
    } else {
      return AppService.list(main, SupplierGroup.class, "select t1.* from scm_supplier_group t1 "
              + "inner join scm_vendor t2 on t1.id = t2.supplier_group_id and t1.company_id = ? "
              + "inner join scm_account t3 on t2.id = t3.vendor_id "
              + "inner join scm_user_account t4 on t3.id = t4.account_id and t4.user_id = ? "
              + "and upper(t1.title) like ? AND t1.status_id = ? order by upper(t1.title) asc", new Object[]{companyId, appUser.getId(), "%" + filter.toUpperCase() + "%", ACTIVE_STATUS});
    }
  }

  public static final List<Account> accountAuto(Main main, String filter, Integer[] companyId, AppUser appUser) {
    Object[] params = new Object[companyId.length + 4];
    params[0] = ACTIVE_ACCOUNT;
    params[1] = appUser.getId();
    params[2] = "%" + filter.toUpperCase() + "%";
    params[3] = "%" + filter.toUpperCase() + "%";

    String s = "";
    int i = 4;
    for (Integer id : companyId) {
      params[i] = id;
      if (i > 4) {
        s += ",";
      }
      s += "?";
      i++;
    }
    return AppService.list(main, Account.class, "select * from scm_account scm_account where account_status_id=? and scm_account.id not in (select account_id from scm_user_account where user_id=? and account_id is not null) and (upper(account_code) like ? or upper(account_title) like ?) and scm_account.company_id in (" + s + ") order by upper(account_code) asc", params);
  }

  /**
   * Return List of Vendor.
   *
   * @param em
   * @param account
   * @return List of Vendor
   */
  protected static final Contract selectActiveContract(AppEm em, Account account) {
    return (Contract) em.single(Contract.class, "select * from scm_contract where account_id = ? and contract_status_id=?", new Object[]{account.getId(), ACTIVE_CONTRACT});
  }

  public static final Status activeGenericStatus(AppEm em) {
    if (defaultStatus == null) {
      defaultStatus = (Status) em.single(Status.class, "select * from scm_status where id = ?", new Object[]{1});
    }
    return defaultStatus;
  }

  public static final void addOrRemoveFav(Main main, Menu menu, User user) {
    UserMenuFavorite umf = new UserMenuFavorite();
    umf.setUserId((User) main.getAppUser());
    umf.setMenuId(menu);

    if (AppService.exist(main, "select '1' from sec_menu_user_favorite where user_id = ? and menu_id = ? ", new Object[]{umf.getUserId().getId(), menu.getId()})) {
      AppService.deleteSql(main, UserMenuFavorite.class, "delete from sec_menu_user_favorite where user_id = ? and menu_id = ? ", new Object[]{umf.getUserId().getId(), menu.getId()});
      menu.setFavorite(false);
      user.getFavoriteMenuList().removeIf(item -> (item.getMenuId().getId() == menu.getId() && item.getUserId().getId() == umf.getUserId().getId()));
    } else {
      AppService.insert(main, umf);
      menu.setFavorite(true);
      user.getFavoriteMenuList().add(umf);
    }

  }

//   public static List<Notification> listNotification(AppEm em,User user){
//    return em.list(Notification.class, "select id,subject,description,created_at,open_at,status,sender_id,receiver_id from scm_notification where receiver_id=? order by id desc", new Object[]{user.getId()});
//   }
  public static final List<Account> listNavAccount(AppEm em, Company company, AppUser appUser) {
    String allAcc = "select tab.id , tab.vendor_id , tab.company_id , tab.company_trade_profile_id, tab.account_code, tab.vendor_bank_id, tab.company_bank_id, tab.vendor_address_id ,\n"
            + "tab.company_address_id, tab.account_status_id, tab.is_purchase_interstate, tab.purchase_level, tab.statutory_form_type, tab.commodity_movement_type,"
            + " tab.note, tab.vendor_tin_no,tab.account_title,\n"
            + "tab.vendor_cst_no, CASE WHEN tab.account_id=0 THEN null Else tab.account_id End as account_id, tab.vendor_gst_no, tab.created_at ,"
            + " tab.modified_at, tab.created_by, tab.modified_by, tab.purchase_ledger_id, tab.sales_ledger_id, tab.is_scheme_applicable,\n"
            + "tab.purchase_channel, tab.sales_channel, tab.render_ptr, tab.render_pts, tab.pts_derivation_criteria, tab.show_in_sales, tab.supplier_decimal_precision,\n"
            + " tab.limit_return_by_sales\n"
            + " from (\n"
            + "SELECT P.id , P.vendor_id , P.company_id , P.company_trade_profile_id, P.account_code, P.vendor_bank_id, P.company_bank_id, P.vendor_address_id ,\n"
            + "P.company_address_id, P.account_status_id, P.is_purchase_interstate, P.purchase_level, P.statutory_form_type, P.commodity_movement_type, P.note, P.vendor_tin_no,\n"
            + "P.vendor_cst_no, P.vendor_gst_no, P.created_at , P.modified_at, P.created_by, P.modified_by, P.purchase_ledger_id, P.sales_ledger_id, P.is_scheme_applicable,\n"
            + "P.purchase_channel, P.sales_channel, P.render_ptr, P.render_pts, P.pts_derivation_criteria, P.show_in_sales, P.supplier_decimal_precision, P.limit_return_by_sales,\n"
            + " CASE WHEN P.account_id IS NOT NULL THEN '----' || P.account_title ELSE P.account_title END\n"
            + ",COALESCE(P.account_id,P.id) as parent_id,COALESCE(P.account_id,0) account_id FROM scm_account as P Where P.account_status_id = ? and P.company_id = ?";
    String order = " ORDER BY parent_id,account_id) tab";
    if (AppConfig.rootUser.equals(appUser.getLogin()) || AppDb.exist(em.getDbConnector(), "select id from scm_user_account where user_id=? and account_id is null", new Object[]{appUser.getId()})) {
      return em.list(Account.class, allAcc + order, new Object[]{ACTIVE_ACCOUNT, company.getId()});
    } else {
      return em.list(Account.class, allAcc + " and P.id in (select account_id from scm_user_account where user_id=?)" + order, new Object[]{ACTIVE_ACCOUNT, company.getId(), appUser.getId()});
    }
  }

  public static UserAccountPreferences getUserAccountPreferences(AppEm em, Company companyId, UserProfile userProfile) {
    UserAccountPreferences preferences = null;
    if (userProfile.getName().equals(AppConfig.rootUser)) {
      preferences = (UserAccountPreferences) em.single(UserAccountPreferences.class, "select * from scm_user_account_preferences where company_id=? and user_id is null",
              new Object[]{companyId.getId()});
    } else {
      preferences = (UserAccountPreferences) em.single(UserAccountPreferences.class, "select * from scm_user_account_preferences where company_id=? and user_id=?",
              new Object[]{companyId.getId(), userProfile.getId()});
    }
    return preferences;
  }

  public static void insertOrUpdate(Main main, UserAccountPreferences preferences) {
    if (preferences.getId() != null) {
      AppService.insert(main, preferences);
    } else {
      AppService.update(main, preferences);
    }
  }
}
