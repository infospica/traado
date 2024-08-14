/*
 *  @(#)UserRuntimeView            29 Mar, 2016
 * 
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 * 
 */
package spica.sys;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.PrimeFaces;
import spica.constant.AccountingConstant;
import spica.fin.domain.TaxCode;
import spica.scm.domain.Company;
import spica.scm.service.RoleService;
import spica.scm.service.UserService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.CountryTaxRegimeUtil;
import static spica.sys.SystemRuntimeConfig.MENU_COMPANY_ID;
import static spica.sys.SystemRuntimeConfig.MENU_DASHBOARD_ID;
import static spica.sys.SystemRuntimeConfig.MENU_IMPORT_ID;
import static spica.sys.SystemRuntimeConfig.MENU_IMPORT_SALES_ID;
import static spica.sys.SystemRuntimeConfig.MENU_PLATFORM_ID;
import static spica.sys.SystemRuntimeConfig.MENU_PRODUCT_ID;
import static spica.sys.SystemRuntimeConfig.MENU_PURCHASE_ID;
import static spica.sys.SystemRuntimeConfig.MENU_SALES_ID;
import static spica.sys.SystemRuntimeConfig.MENU_STOCK_ID;
import static spica.sys.SystemRuntimeConfig.MENU_STOCK_AND_SALES_ID;
import static spica.sys.SystemRuntimeConfig.MENU_STOCK_LEDGER_ID;
import static spica.sys.SystemRuntimeConfig.MENU_PRODUCT_CATEGORY_TAX;
import static spica.sys.SystemRuntimeConfig.MENU_MRP_ADJUSTMENT;
import static spica.sys.SystemRuntimeConfig.MENU_PRODUCT_INVENTORY;
import static spica.sys.SystemRuntimeConfig.MENU_PURCHASE_REGISTER;
import static spica.sys.SystemRuntimeConfig.MENU_STOCK_ADJUSTMENT;
import static spica.sys.SystemRuntimeConfig.MENU_STOCK_AUDIT;
import static spica.sys.SystemRuntimeConfig.MENU_STOCK_MOVEMENT;
import spica.sys.domain.Menu;
import spica.sys.domain.User;
import spica.sys.domain.UserMenuFavorite;
import spica.sys.view.PrivilegeChecked;
import wawo.app.config.AppConfig;
import wawo.app.config.AppFactory;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import static wawo.app.faces.UserState.LANDING_PAGE;
import wawo.entity.core.AppEm;
import wawo.entity.util.StringUtil;

/**
 * Provide user runtime features of the logged in user at view tier.
 *
 * @author <a href=mailto:arun.vc@infospica.com>Arun VC</a>
 */
@ManagedBean(name = "userRuntimeView")
@SessionScoped
//@Named(value = "userRuntimeView")
//@SessionScoped
public class UserRuntimeView extends ViewRuntimeView implements Serializable {

  private User appUser;
  private List<Menu> menuList;
  private List<Menu> quickMenuList;
  private short failedAttempt = 0;
  private List<Menu> megaMenu;
  private String search;
  private boolean preferedMenu;

  public UserRuntimeView() {
  }

  public static UserRuntimeView instance(HttpServletRequest request) {
    return (UserRuntimeView) request.getSession().getAttribute(AppConfig.userView);
  }

  public static UserRuntimeView instance() {
    return (UserRuntimeView) Jsf.getSessionBean(AppConfig.userView);
  }

  @PostConstruct
  public void init() {
    appUser = null;
    super.init();
  }

  public void actionValidateSession() {
    actionValidateSession(appUser);
  }

  public Date getToday() {
    return new Date();
  }

  public String loginCheck() {
    AppEm em = null;
    try {
      em = AppFactory.getNewEm();
      referencePopupAllowed = true; //TODO based on user profile allow this
      init();
      appUser = UserRuntimeService.loginCheck(em, getLoginId(), getPassword());
      if (appUser == null) {
        Jsf.error("user.message.login.failed");
        failedAttempt++;
        return null;
      }
      loadAfterLogin(em);
      return (LANDING_PAGE);
    } catch (Throwable t) {
      Jsf.fatal(t, "system.error.unable.to.process.your.request");
      failedAttempt++;
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return null;
  }

  public String loginAs(MainView main, User usr) {
    try {
      this.init();
      appUser = UserRuntimeService.getLoginUser(main.em(), usr.getLogin());
      loadAfterLogin(main.em());
      return (LANDING_PAGE);
    } catch (Throwable t) {
      Jsf.fatal(t, "system.error.unable.to.process.your.request");
      failedAttempt++;
    } finally {
      if (main != null) {
        main.close();
      }
    }
    return null;
  }

  public void changePassword(MainView main) {
    try {
      UserService.changePassword(main, appUser, getPassword(), false); //many record delete from list
      main.commit("success.save");
    } catch (Throwable t) {
      Jsf.fatal(t, "system.error.unable.to.process.your.request");
    } finally {
      if (main != null) {
        main.close();
      }
    }
  }

  private void loadAfterLogin(AppEm em) throws SQLException {
    menuList = UserRuntimeService.listMenuAndChild(em, getAppUser()); //Load menu list based on user role.
    megaMenu = menuList;
    if (menuActive == null && !StringUtil.isEmpty(menuList)) {
      menuActive = menuList.get(0);
    }
    // initAll(em);
    loadView(em); //TODO remove this from here
    setAccountRender();
    loadMegaMenuList();
    // loadNotification(em);
  }

//  private void initAll(AppEm em) {
//    loadCompanyList(em);// Loads list of companies based on user.
//    _changeCompany(em);
//   // setAccountRender();
//  }
//
//  public void changeCompany(MainView main) {
//    _changeCompany(main.em());
//  }
//
//  private void _changeCompany(AppEm em) {
//    accountList = null;
//    account = null;
//    supplierGroup = null;
//    supplierGroupList = null;
//    accountGroup = null;
//    accountGroupList = null;
//    navAccountList = null;
//    loadSupplierGroup(em);
//    loadAccountGroup(em);
//    loadAccount(em);
//    loadNavAccount(em);
//    if (getCompany().getCompanySettings() == null) {
//      getCompany().setCompanySettings(CompanySettingsService.selectByCompany(em, getCompany()));
//    }
//  }
//
//  public void changeCompanyListener(MainView main) {
//    try {
//      getAppUser().getUserProfileId().setPreferedAccountGroup(null);
//      changeCompany(main);
//      main.getPageData().reset();
//      main.getPageData().setSearchKeyWord(null);
//      main.setViewType(ViewTypes.list);
//      getAppUser().getUserProfileId().setActiveCompany(company);
//      UserRuntimeService.insertOrUpdate(main, getAppUser());
//      main.commit();
//    } catch (Throwable t) {
//      main.rollback(t, "select.error");
//    } finally {
//      main.close();
//    }
//  }
//
//  private void loadAccount(AppEm em) {
//    if (company != null) {
//      loadYearComboValues(company.getFinancialYearStartDate());
//      if (accountList == null) {
//        accountList = UserRuntimeService.listCompanyAccount(em, company, getAppUser());
//        if (!StringUtil.isEmpty(accountList)) {
//          if (getUserAccountPreferences(em).getAccountId() == null) {
//            account = accountList.get(0); //setting the first record as default company
//          } else {
////            account = getAppUser().getUserProfileId().getPreferedAccount();
//            account = userAccountPreferences.getAccountId();
//          }
//          changeAccount(em, account);
//        }
//      }
//    }
//  }
//
//  private void loadNavAccount(AppEm em) {
//    if (company != null) {
//      loadYearComboValues(company.getFinancialYearStartDate());
//      if (navAccountList == null) {
//        navAccountList = UserRuntimeService.listNavAccount(em, company, getAppUser());
//        if (!StringUtil.isEmpty(navAccountList)) {
//          if (getUserAccountPreferences(em).getAccountId() == null) {
//            account = accountList.get(0); //setting the first record as default company
//          } else {
////            account = getAppUser().getUserProfileId().getPreferedAccount();
//            account = userAccountPreferences.getAccountId();
//          }
//          changeAccount(em, account);
//        }
//      }
//    }
//  }
//
//  private void loadSupplierGroup(AppEm em) {
//    if (company != null && supplierGroupList == null) {
//      supplierGroupList = UserRuntimeService.listSupplierGroup(em, company, getAppUser());
//      if (!StringUtil.isEmpty(supplierGroupList)) {
//        supplierGroup = supplierGroupList.get(0);
//      }
//    }
//  }
//
//  private void loadAccountGroup(AppEm em) {
//    if (company != null && accountGroupList == null) {
//      accountGroupList = UserRuntimeService.listAccountGroupList(em, company, getAppUser());
//      if (!StringUtil.isEmpty(accountGroupList)) {
//        if (getUserAccountPreferences(em).getAccountGroupId() == null) {
//          accountGroup = accountGroupList.get(0);
//        } else {
////          accountGroup = getAppUser().getUserProfileId().getPreferedAccountGroup();
//          accountGroup = userAccountPreferences.getAccountGroupId();
//        }
//        changeAccountGroup();
//      }
//    }
//  }
//
//  private void loadCompanyList(AppEm em) {
//    if (companyList == null) {
//      companyList = UserRuntimeService.listCompany(em, getAppUser());
//      if (!StringUtil.isEmpty(companyList)) {
//        if (getAppUser().getUserProfileId().getActiveCompany() != null) {
//          company = getAppUser().getUserProfileId().getActiveCompany();//companyList.get(0); //setting the first record as default company
//        } else {
//          company = companyList.get(0);
//        }
//      }
//    }
//  }
  public User getAppUser() {
    return appUser;
  }

  /**
   * Updates User Profile information.
   *
   * @param main
   * @return
   */
  public String actionEditProfile(MainView main) {
    try {
      UserRuntimeService.insertOrUpdate(main, getAppUser());
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Method to show all the menus in view page.
   *
   * @return
   */
  public List<Menu> getMenuList() {
    return menuList;
  }

  /**
   *
   * @return
   */
//  public List<Company> getCompanyList(MainView m) {
//    if (companyList == null) {
//      try (MainView main = m) {
//        loadCompanyList(main.em());
//      }
//    }
//    return companyList;
//  }
//
//  public List<Account> getAccountList() {
//    return accountList;
//  }
//
//  public void setAccountList(List<Account> accountList) {
//    this.accountList = accountList;
//  }
//
//  public List<Account> accountAuto(String filter) {
//    if (getCompany() != null) {
//      return UserRuntimeService.accountAuto(filter, getCompany().getId(), getAppUser());
//    }
//    return null;
//  }
//
//  public List<AccountGroup> accountGroupAuto(String filter) {
//    if (getCompany() != null) {
//      return UserRuntimeService.accountGroupAuto(filter, getCompany().getId(), getAppUser());
//    }
//    return null;
//  }
//
//  public List<SupplierGroup> supplierGroupAuto(String filter) {
//    if (getCompany() != null) {
//      return UserRuntimeService.supplierGroupAuto(filter, getCompany().getId(), getAppUser());
//    }
//    return null;
//  }
//
//  public Company getCompany() {
//    return company;
//  }
//
//  public void setCompany(Company company) {
//    this.company = company;
//  }
//
//  public Account getAccount() {
//    return account;
//  }
//
//  public SupplierGroup getSupplierGroup() {
//    return supplierGroup;
//  }
//
//  public void setSupplierGroup(SupplierGroup supplierGroup) {
//    this.supplierGroup = supplierGroup;
//  }
//
//  public AccountGroup getAccountGroup() {
//    return accountGroup;
//  }
//
//  public void setAccountGroup(AccountGroup accountGroup) {
//    if (accountGroup != null) {
//      if (this.accountGroup == null || !this.accountGroup.getId().equals(accountGroup.getId())) {
//        MainView main = (MainView) Jsf.getViewBean(AppConfig.mainView);
//        try {
//          changeAccountGroup(main, accountGroup);
//          UserAccountPreferences preferences = getUserAccountPreferences(main.em());
//          preferences.setAccountGroupId(accountGroup);
////          getAppUser().getUserProfileId().setPreferedAccountGroup(accountGroup);
////          UserRuntimeService.insertOrUpdate(main, getAppUser());
//          UserRuntimeService.insertOrUpdate(main, preferences);
//        } catch (Throwable t) {
//          Jsf.fatal(t, "system.error.unable.to.process.your.request");
//        } finally {
//          if (main != null) {
//            main.close();
//          }
//        }
//      }
//    }
//    this.accountGroup = accountGroup;
//  }
//
//  public List<SupplierGroup> getSupplierGroupList() {
//    return supplierGroupList;
//  }
//
//  public void setSupplierGroupList(List<SupplierGroup> supplierGroupList) {
//    this.supplierGroupList = supplierGroupList;
//  }
//
//  public List<AccountGroup> getAccountGroupList() {
//    return accountGroupList;
//  }
//
//  public void setAccountGroupList(List<AccountGroup> accountGroupList) {
//    this.accountGroupList = accountGroupList;
//  }
//
//  public Account getAccountCurrent() {
//    if (account == null) {
//      throw new UserMessageException("account.required");
//    }
//    return account;
//  }
//
//  public Account getPrimaryAccount() {
//    return getAccountCurrent().getAccountId() == null ? account : account.getAccountId();
//  }
//
//  public void setAccount(Account account) {
//    if (account != null) {
//      if (this.account == null || this.account.getId() != account.getId()) {
//        MainView main = (MainView) Jsf.getViewBean(AppConfig.mainView);
//        try {
//          changeAccount(main, account);
//          UserAccountPreferences preferences = getUserAccountPreferences(main.em());
//          preferences.setAccountId(account);
////          getAppUser().getUserProfileId().setPreferedAccount(account);
////          UserRuntimeService.insertOrUpdate(main, getAppUser());
//          UserRuntimeService.insertOrUpdate(main, preferences);
//        } catch (Throwable t) {
//          Jsf.fatal(t, "system.error.unable.to.process.your.request");
//        } finally {
//          if (main != null) {
//            main.close();
//          }
//        }
//      }
//    }
//    this.account = account;
//  }
//
//  private void changeAccount(AppEm em, Account account) {
//    if (AccountService.INDIVIDUAL_SUPPLIER.equals(account.getPurchaseChannel())) {
//      contract = UserRuntimeService.selectActiveContract(em, account);
//    } else {
//      contract = UserRuntimeService.selectActiveContract(em, account.getAccountId() == null ? account : account.getAccountId());
//    }
//    MainView main = (MainView) Jsf.getViewBean(AppConfig.mainView);
//    if (main != null) {
//      String path = main.getViewPath();
//      TaxCalculator tc = getTaxCalculator();
//      if (path.endsWith(FileConstant.PURCHASE_CONSIGNMENT_URL) || path.endsWith(FileConstant.PURCHASE_RETURN_CONSIGNMENT_URL) || path.endsWith(FileConstant.PURCHASE_CONSIGNMENT_RECEIPT_INFO)
//              || path.endsWith(FileConstant.PURCHASE_REQ) || path.endsWith(FileConstant.PURCHASE_ORDER) || path.endsWith(FileConstant.PRODUCT)
//              || path.endsWith(FileConstant.OPENING_STOCK_ENTRY) || path.endsWith(tc.getProductEntryView()) || path.endsWith(FileConstant.STOCK_ADJUSTMENT)
//              || path.endsWith(FileConstant.PURCHASE_RETURN) || path.endsWith(FileConstant.DEBIT_NOTE_PURCHASE) || path.endsWith(FileConstant.CREDIT_NOTE_PURCHASE) || path.endsWith(FileConstant.EXPENSE_REIMP_ENDS_WITH)) {
//        main.getPageData().reset();
//        main.setViewType(ViewTypes.list);
//      }
//    }
//  }
//
//  private void changeAccountGroup() {
//    MainView main = (MainView) Jsf.getViewBean(AppConfig.mainView);
//    if (main != null) {
//      String path = main.getViewPath();
//    //  TaxCalculator tc = getTaxCalculator();
//      if (path.endsWith(FileConstant.SALES_CONSIGNMENT_URL) || path.endsWith(FileConstant.SALES_RETURN_CONSIGNMENT) || path.endsWith(tc.getSalesInvoiceView())
//              || path.endsWith(FileConstant.SALES_REQUEST) || path.endsWith(FileConstant.SALES_ORDER) || path.endsWith(FileConstant.SALES_RETURN)
//              || path.endsWith(FileConstant.SALES_INVOICE) || path.endsWith(FileConstant.DEBIT_NOTE_SALES) || path.endsWith(FileConstant.CREDIT_NOTE_SALES)
//              || path.endsWith(FileConstant.ACCOUNTING_CHEQUE_ENTRY) || path.endsWith(FileConstant.EXPENSE_REIMP_ENDS_WITH)
//              || path.endsWith(FileConstant.SALES_SERVICE_INVOICE)) {
//        main.getPageData().reset();
//        main.setViewType(ViewTypes.list);
//      }
//    }
//  }
  public TaxCalculator getTaxCalculator() {
    return SystemRuntimeConfig.getTaxCalculator(getCompany().getCountryTaxRegimeId().getTaxProcessorId().getProcessorClass());
  }

  public TaxCalculator getTaxCalculator(Company company) {
    return SystemRuntimeConfig.getTaxCalculator(company.getCountryTaxRegimeId().getTaxProcessorId().getProcessorClass());
  }

//  public void changeAccount(MainView main, Account account) {
//    changeAccount(main.em(), account);
//  }
//
//  public void changeAccountGroup(MainView main, AccountGroup accountGroup) {
//    changeAccountGroup();
//  }
//
//  public Contract getContract() {
//    if (account == null) {
//      throw new UserMessageException("account.required");
//    }
//    return contract;
//  }
//
//  public void setCompanyList(List<Company> companyList) {
//    this.companyList = companyList;
//  }
  public int userLevel() {
    if (appUser != null) {
      if (appUser.getUserProfileId() == null) {
        return 0;
      } else if (appUser.getUserProfileId().getCustomerId() != null) {
        return UserRuntimeService.USER_CUSTOMER;
      } else if (appUser.getUserProfileId().getVendorId() != null) {
        return UserRuntimeService.USER_VENDOR;
      } else if (appUser.getUserProfileId().getTransporterId() != null) {
        return UserRuntimeService.USER_TRANSPORTER;
      } else if (appUser.getUserProfileId().getCompanyId() != null) {
        return UserRuntimeService.USER_COMOPANY;
      } else {
        return 0;
      }
    }
    return 0;
  }

  //Account render menus
  private boolean accountRender = false;
  private boolean supplierGroupRender = false;
  private boolean accountGroupRender = false;

  private void setAccountRender() {
    accountRender = false;
    if (menuActive.getParentId() != null && menuActive.getParentId().getId() != null) {
      int id = menuActive.getParentId().getId();
      if (id == MENU_PRODUCT_ID || id == MENU_PURCHASE_ID || id == MENU_STOCK_ID || id == MENU_PLATFORM_ID || id == MENU_IMPORT_ID) {
        if (menuActive.getId() == MENU_STOCK_AND_SALES_ID || menuActive.getId() == MENU_STOCK_LEDGER_ID || menuActive.getId() == MENU_PRODUCT_CATEGORY_TAX
                || menuActive.getId() == MENU_MRP_ADJUSTMENT || menuActive.getId() == MENU_STOCK_ADJUSTMENT || menuActive.getId() == MENU_STOCK_MOVEMENT
                || menuActive.getId() == MENU_IMPORT_SALES_ID || menuActive.getId() == MENU_PRODUCT_INVENTORY || menuActive.getId() == MENU_STOCK_AUDIT
                || menuActive.getId() == MENU_PURCHASE_REGISTER) {
          accountRender = false;
        } else {
          accountRender = true;
        }
      }
    } else {
      accountRender = true;
    }
  }

  private void setSupplierGroupRender() {
    supplierGroupRender = false;
    if (menuActive.getParentId() != null && menuActive.getParentId().getId() != null) {
      int id = menuActive.getParentId().getId();
      if (id == MENU_COMPANY_ID || id == MENU_DASHBOARD_ID) {
        supplierGroupRender = true;
      }
    }
  }

  private void setAccountGroupRender() {
    accountGroupRender = false;
    if (menuActive.getParentId() != null && menuActive.getParentId().getId() != null) {
      int id = menuActive.getParentId().getId();
      if (id == MENU_SALES_ID || menuActive.getId() == MENU_IMPORT_SALES_ID) {
        accountGroupRender = true;
      }
      if (menuActive.getId() == MENU_PRODUCT_INVENTORY) {
        accountGroupRender = true;
      }
    }
  }

  public boolean isAccountRender() {
    return accountRender;
  }

  public void setAccountRender(boolean accountRender) {
    this.accountRender = accountRender;
  }

  public boolean isSupplierGroupRender() {
    return supplierGroupRender;
  }

  public void setSupplierGroupRender(boolean supplierGroupRender) {
    this.supplierGroupRender = supplierGroupRender;
  }

  public boolean isAccountGroupRender() {
    return accountGroupRender;
  }

  public void setAccountGroupRender(boolean accountGroupRender) {
    this.accountGroupRender = accountGroupRender;
  }

  //Privileges
  private Menu menuActive;
  private PrivilegeChecked privilege;
  private Map<Integer, PrivilegeChecked> menuPrivilegeMap = new ConcurrentHashMap<>();
  private boolean referencePopupAllowed = false;

  public boolean getReferencePopupAllowed() {
    return referencePopupAllowed;
  }

  public void setReferencePopupAllowed(boolean referencePopupAllowed) {
    this.referencePopupAllowed = referencePopupAllowed;
  }

  public void setMenuActive(Menu menuActive) {
    this.menuActive = menuActive;
    if (quickMenuList == null) {
      quickMenuList = new ArrayList<>();
    }
    if (!quickMenuList.contains(menuActive)) {
      if (menuActive.getParentId() != null) {
        quickMenuList.add(0, menuActive);
        if (quickMenuList.size() > 10) {
          quickMenuList.remove(10);
        }
      }
    }
//    setCompanyRender();
    setAccountRender();
    setAccountGroupRender();
    setSupplierGroupRender();
  }

  public void menuRemove(Menu menuActive) {
    if (quickMenuList != null) {
      quickMenuList.remove(menuActive);
    }
  }

  public Menu getMenuActive() {
    return menuActive;
  }

  public boolean isFavorite() {
    if (menuActive != null) {
      return menuActive.isFavorite();
    }
    return false;
  }

  public PrivilegeChecked getPrivilege() {
    return privilege;
  }

  public void loadPermission(MainView main) {
    if (appUser == null) {
      Jsf.pageForward(AppConfig.userHome);
    } else if (!appUser.isRoot()) {
      if (menuActive != null) {
        permissionCheck(main, menuActive.getId(), appUser.getId());
      }
    } else {
      privilege = new PrivilegeChecked(true); //For root full permission by default
    }
  }

  private void permissionCheck(MainView main, Integer menuId, Integer userId) {
    //check user security
    privilege = menuPrivilegeMap.get(menuId);
    if (privilege == null) {
      try {
        privilege = RoleService.getPermission(main, menuId, userId);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
      if (privilege != null) {
        menuPrivilegeMap.put(menuId, privilege);
      }
    }
    if (privilege == null) {
      main.error("no.permission");
      Jsf.pageForward(AppConfig.userHome);
    }
    if (privilege == null) {
      privilege = new PrivilegeChecked(); ///empty with no previlage
    }
  }

  /// Year based on company finacial yearList
  // private List<Integer> yearList = new ArrayList<>();
//  private Map<Integer, String> monthsMap = new HashMap<Integer, String>();
//  private Map<Integer, String> monthsFiscalMap = new LinkedHashMap<Integer, String>();
//  // private Map<Integer, String> monthsYearMap = new LinkedHashMap<Integer, String>();
//  private int startFiscalMonth = Calendar.MARCH;
//  private int startFiscalYear = Calendar.getInstance().get(Calendar.YEAR);
//  private int endFiscalYear = Calendar.getInstance().get(Calendar.YEAR);
//  private Date minDate;
//
//  private void loadYearComboValues(Date toDate) {
//    startFiscalMonth = Calendar.MARCH;
//    startFiscalYear = Calendar.getInstance().get(Calendar.YEAR);
//    endFiscalYear = Calendar.getInstance().get(Calendar.YEAR);
//    Calendar now = Calendar.getInstance();
//    startFiscalMonth = 1;
//    int to = getCurrentYear() - 3;
//    if (toDate != null) {
//      Calendar toCal = Calendar.getInstance();
//      toCal.setTime(toDate);
//      to = toCal.get(Calendar.YEAR);
//      startFiscalMonth = toCal.get(Calendar.MONTH) + 1;
//    }
//
//    monthsMap.clear();
//    monthsFiscalMap.clear();
//    String[] months = new DateFormatSymbols().getMonths();
//    for (int i = 1; i < months.length; i++) {
//      //monthsMap.put(i, (i < 10 ? "0" : "") + (i + " - " + months[i - 1]));
//      monthsMap.put(i, months[i - 1]);
//    }
//
//    for (int i = startFiscalMonth; i < months.length; i++) {
//      //monthsFiscalMap.put(i, (i < 10 ? "0" : "") + (i + " - " + months[i - 1]));
//      monthsFiscalMap.put(i, months[i - 1]);
//    }
//    if (startFiscalMonth > 1) {
//      for (int i = 1; i < startFiscalMonth; i++) {
//        monthsFiscalMap.put(i, (i < 10 ? "0" : "") + (i + " - " + months[i - 1]));
//        monthsFiscalMap.put(i, months[i - 1]);
//      }
//    }
//    startFiscalMonth = monthsFiscalMap.keySet().iterator().next();
//    if (startFiscalMonth > getCurrentMonth()) {
//      startFiscalYear--;
//    } else {
//      endFiscalYear++;
//    }
//  }
//
//  public int getStartFiscalMonth() {
//    return startFiscalMonth;
//  }
//
//  public int getStartFiscalYear() {
//    return startFiscalYear;
//  }
//
//  public Date getStartFiscalDate() {
//    Calendar cal = Calendar.getInstance();
//    cal.set(startFiscalYear, startFiscalMonth - 1, 1);
//    return cal.getTime();
//  }
//
//  public Date getEndFiscalDate() {
//    Calendar cal = Calendar.getInstance();
//    cal.setTime(getStartFiscalDate());
//    cal.add(Calendar.YEAR, 1);
//    cal.add(Calendar.DATE, -1);
//    return cal.getTime();
//  }
//
//  public int getCurrentYear() {
//    return Calendar.getInstance().get(Calendar.YEAR);
//  }
//
//  public int getCurrentMonth() {
//    return Calendar.getInstance().get(Calendar.MONTH) + 1;
//  }
//
//  public int getCurrenFiscalDate() {
//    return Calendar.getInstance().get(Calendar.MONTH) + 1;
//  }
//
//    private Date minDate;
//  public Date getMinDate() {
//    if (minDate == null) {
//      //  DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
//      try {
//        minDate = AppConfig.displayDatePattern.parse("01-01-2000");
//      } catch (ParseException ex) {
//        Logger.getLogger(UserRuntimeView.class.getName()).log(Level.SEVERE, null, ex);
//      }
//    }
//    return minDate;
//  }
  /**
   *
   * @return
   */
  public boolean isVatRegime() {
    return CountryTaxRegimeUtil.isVatRegime(getCompany());
  }

  /**
   *
   * @param company
   * @return
   */
  public boolean isVatRegime(Company company) {
    return CountryTaxRegimeUtil.isVatRegime(company);
  }

  /**
   *
   * @return
   */
  public boolean isGstRegime() {
    return CountryTaxRegimeUtil.isGstRegime(getCompany());
  }

  /**
   *
   * @param company
   * @return
   */
  public boolean isGstRegime(Company company) {
    return CountryTaxRegimeUtil.isGstRegime(company);
  }

  public int getAccountingAutoWidth() {
    return UserRuntimeService.ACCOUNTING_AUTO_WIDTH;
  }

  public List<Menu> getQuickMenuList() {
    return quickMenuList;
  }

  public void setQuickMenuList(List<Menu> quickMenuList) {
    this.quickMenuList = quickMenuList;
  }

  public short getFailedAttempt() {
    return failedAttempt;
  }

  public void setFailedAttempt(short failedAttempt) {
    this.failedAttempt = failedAttempt;
  }

  public TaxCode getGstTaxCode(TaxCode taxCode, int taxCodeType) {
    if (taxCode != null) {
      if (AccountingConstant.TAX_TYPE_IGST == taxCodeType) {
        return taxCode;
      } else {
        for (TaxCode txCode : taxCode.getTaxCodeList()) {
          if (taxCodeType == txCode.getTaxType()) {
            return txCode;
          }
        }
      }
    }
    return taxCode;
  }

  public void viewCalc() {
    Map<String, Object> options = new HashMap<String, Object>();
    options.put("width", 550);
    options.put("height", 400);
    options.put("contentWidth", "100%");
    options.put("contentHeight", "100%");
    options.put("position", "top");
    options.put("headerElement", "customheader");
    PrimeFaces.current().dialog().openDynamic("calc/calc", options, null);
  }

  public void addOrRemoveFav(MainView main) {
    addOrRemoveFav(main, menuActive);
  }

  public void addOrRemoveFav(MainView main, Menu menu) {
    try {
      UserRuntimeService.addOrRemoveFav(main, menu, getAppUser());
      setFav(menu.getParentId() == null ? menu : menu.getParentId());
      main.commit();
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      if (main != null) {
        main.close();
      }
    }
  }

  public void showUserSetting() {
    Jsf.popupForm(FileConstant.USER_SETTINGS, getAppUser());
  }

  public void userSettingPopupClose() {
    Jsf.closePopup(getAppUser());
  }

  private void loadMegaMenuList() {
    if (getAppUser().getUserProfileId().getMenuStatus() == null) {
      getAppUser().getUserProfileId().setMenuStatus(SystemRuntimeConfig.TOGGLE_MEGA_MENU_ALL);
    }
    if (getAppUser().getFavoriteMenuList() != null) {
      for (Menu m : menuList) {
        setFav(m);
      }
    }
  }

  private void setFav(Menu m) {
    boolean found = false;
    for (Menu sm : m.getParentIdMenu()) {
      for (UserMenuFavorite fm : getAppUser().getFavoriteMenuList()) {
        if (sm.getId().intValue() == fm.getMenuId().getId()) {
          sm.setFavorite(true);
          found = true;
        }
      }
    }
    m.setFavFound(found);
  }

  public boolean isValidMenu(Menu menu) {
    if (menu.isFavorite() || appUser.getUserProfileId().getMenuStatus() == SystemRuntimeConfig.TOGGLE_MEGA_MENU_ALL) {
      if (!StringUtil.isEmpty(search)) {
        return (menu.getTitle().toLowerCase().contains(search.toLowerCase()));
      }
      return true;
    }
    return false;
  }

  public List<SelectItem> getMegaMenuFilters() {
    List<SelectItem> megaMenuFilters = new ArrayList<>();
    megaMenuFilters.add(new SelectItem(SystemRuntimeConfig.TOGGLE_MEGA_MENU_ALL, "All"));
    megaMenuFilters.add(new SelectItem(SystemRuntimeConfig.TOGGLE_MEGA_MENU_FAVORITE, "Favorite"));
    return megaMenuFilters;
  }

  public void changeMenuStatusFilter(MainView main) {
    if (menuList != null) {
      if (getAppUser().getId() != null) {
        try {
          UserRuntimeService.insertOrUpdate(main, getAppUser());
          main.commit();
        } catch (Throwable t) {
          main.rollback(t);
        } finally {
          main.close();
        }
      }
    }
  }

  public void clearSerach() {
    setSearch("");
  }

  public List<Menu> getMegaMenu() {
    return megaMenu;
  }

  public void setMegaMenu(List<Menu> megaMenu) {
    this.megaMenu = megaMenu;
  }

  public String getSearch() {
    return search;
  }

  public void setSearch(String search) {
    this.search = search;
  }

  public boolean isPreferedMenu() {
    return preferedMenu;
  }

  public void setPreferedMenu(boolean preferedMenu) {
    this.preferedMenu = preferedMenu;
  }

  public String exportFileName() {
    String name = getMenuActive().getTitle();
    name = name.contains("Report") ? name : name + "_Report";
    return name.replaceAll("\\s+", "_").toLowerCase();
  }

//  public List<Account> getNavAccountList() {
//    return navAccountList;
//  }
//
//  public UserAccountPreferences getUserAccountPreferences(AppEm em) {
//    userAccountPreferences = UserRuntimeService.getUserAccountPreferences(em, company, getAppUser().getUserProfileId());
//    if (userAccountPreferences == null) {
//      userAccountPreferences = new UserAccountPreferences();
//      if (getAppUser().getUserProfileId().getName().equals(AppConfig.rootUser)) {
//        userAccountPreferences.setUserId(null);
//      } else {
//        userAccountPreferences.setUserId(getAppUser().getUserProfileId());
//      }
//      userAccountPreferences.setCompanyId(company);
//    }
//    return userAccountPreferences;
//  }
//
//  public void setUserAccountPreferences(UserAccountPreferences userAccountPreferences) {
//    this.userAccountPreferences = userAccountPreferences;
//  }
  public String exportFileName(String file) {
    String name = getMenuActive().getTitle();
    if (!file.isEmpty() && name.equals(SystemConstants.CUSTOMER_REPORT)) {
      name += file;
    }
    name = name.contains("Report") ? name : name + "_Report";
    return name.replaceAll("\\s+", "_").toLowerCase();
  }

//    public void postRenderView(PostRenderViewEvent e) {
//      Jsf.getMain().close();
//    }
//  public Date getMinEntryDate() {
//    return getCompany().getCurrentFinancialYear().getStartDate();
//  }
//
//  public Date getMaxEntryDate() {
//    Date today = new Date();
//    if (today.before(getCompany().getCurrentFinancialYear().getEndDate())) {
//      return today;
//    }
//    return getCompany().getCurrentFinancialYear().getEndDate();
//  }
}
