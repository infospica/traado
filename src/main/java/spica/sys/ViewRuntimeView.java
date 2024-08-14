/*
 *  @(#)ViewRuntimeView            29 Mar, 2016
 * 
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 * 
 */
package spica.sys;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Contract;
import spica.scm.domain.SupplierGroup;
import spica.scm.domain.UserAccountPreferences;
import spica.scm.service.AccountService;
import spica.scm.service.CompanySettingsService;
import spica.scm.tax.TaxCalculator;
import spica.sys.domain.User;
import wawo.app.config.AppConfig;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppEm;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 *
 * @author <a href=mailto:arun.vc@infospica.com>Arun VC</a>
 */
//@Named(value = "viewRuntimeView")
//@ViewScoped
public class ViewRuntimeView extends SysDateView implements Serializable {

  private Company company;
  private Account account;
  // private SupplierGroup supplierGroup;
  private AccountGroup accountGroup;
  private Contract contract;
  private List<Company> companyList;
  // private List<Account> accountList;
  private List<Account> navAccountList;
  // private List<SupplierGroup> supplierGroupList;
  private List<AccountGroup> accountGroupList;
  private UserAccountPreferences userAccountPreferences;

  public User getAppUser() {
    return null;
  }

  public UserRuntimeView get() {
    return UserRuntimeView.instance();
  }

  @PostConstruct
  public void init() {
    //  companyList = null;
    //   accountList = null;
    //   accountGroupList = null;
//    supplierGroupList = null;

  }

  public void loadView(AppEm em) {
    loadCompanyList(em);// Loads list of companies based on user.
    _changeCompany(em);
  }

  public void changeCompany(MainView main) {
    try {
      _changeCompany(main.em());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void _changeCompany(AppEm em) {
    // accountList = null;
    account = null;
    //   supplierGroup = null;
    // supplierGroupList = null;
    accountGroup = null;
    accountGroupList = null;
    navAccountList = null;
    //loadSupplierGroup(em);
    loadAccountGroup(em);
//    loadAccount(em);
    loadNavAccount(em);
    if (getCompany() != null && getCompany().getCompanySettings() == null) {
      getCompany().setCompanySettings(CompanySettingsService.selectByCompany(em, getCompany()));
    }
  }

  public void changeCompanyListener(MainView main) {
    try {
      getAppUser().getUserProfileId().setPreferedAccountGroup(null);
      changeCompany(main);
      main.getPageData().reset();
      main.getPageData().setSearchKeyWord(null);
      main.setViewType(ViewTypes.list);
      getAppUser().getUserProfileId().setActiveCompany(company);
      UserRuntimeService.insertOrUpdate(main, getAppUser());
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "select.error");
    } finally {
      main.close();
    }
  }

//  private void loadAccount(AppEm em) {
//    if (company != null) {
//      loadYearComboValues(company);
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
  private void loadNavAccount(AppEm em) {
    if (company != null) {
      loadYearComboValues(company);
      if (navAccountList == null) {
        navAccountList = UserRuntimeService.listNavAccount(em, company, getAppUser());
        if (!StringUtil.isEmpty(navAccountList)) {
          if (getUserAccountPreferences(em).getAccountId() == null) {
            account = navAccountList.get(0); //setting the first record as default company
          } else {
//            account = getAppUser().getUserProfileId().getPreferedAccount();
            account = userAccountPreferences.getAccountId();
          }
          changeAccount(em, account);
        }
      }
    }
  }

//  private void loadSupplierGroup(AppEm em) {
//    if (company != null && supplierGroupList == null) {
//      supplierGroupList = UserRuntimeService.listSupplierGroup(em, company, getAppUser());
//      if (!StringUtil.isEmpty(supplierGroupList)) {
//        supplierGroup = supplierGroupList.get(0);
//      }
//    }
//  }
  private void loadAccountGroup(AppEm em) {
    if (company != null && accountGroupList == null) {
      accountGroupList = UserRuntimeService.listAccountGroupList(em, company, getAppUser());
      if (!StringUtil.isEmpty(accountGroupList)) {
        if (getUserAccountPreferences(em).getAccountGroupId() == null) {
          accountGroup = accountGroupList.get(0);
        } else {
//          accountGroup = getAppUser().getUserProfileId().getPreferedAccountGroup();
          accountGroup = userAccountPreferences.getAccountGroupId();
        }
        changeAccountGroup();
      }
    }
  }

  private void loadCompanyList(AppEm em) {
    if (companyList == null) {
      companyList = UserRuntimeService.listCompany(em, getAppUser());
      if (!StringUtil.isEmpty(companyList)) {
        if (getAppUser().getUserProfileId().getActiveCompany() != null) {
          company = getAppUser().getUserProfileId().getActiveCompany();//companyList.get(0); //setting the first record as default company
        } else {
          company = companyList.get(0);
        }
      }
    }
  }

  public List<Company> getCompanyList(MainView main) {
    if (companyList == null) {
      try {
        loadCompanyList(main.em());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companyList;
  }

//  public List<Account> getAccountList() {
//    return accountList;
//  }
//
//  public void setAccountList(List<Account> accountList) {
//    this.accountList = accountList;
//  }
  public List<Account> accountAuto(String filter) {
    if (getCompany() != null) {
      MainView main = Jsf.getMain();
      try {
        return UserRuntimeService.accountAuto(main, filter, getCompany().getId(), getAppUser());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public List<AccountGroup> accountGroupAutoAll(String filter) {
    List<AccountGroup> list = accountGroupAuto(filter);
    if (list != null || list.size() > 0) {
      list.add(0, new AccountGroup(0, "All"));
    }
    return list;

  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    if (getCompany() != null) {
      MainView main = Jsf.getMain();
      try {
        return UserRuntimeService.accountGroupAuto(main, filter, getCompany().getId(), getAppUser());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public List<SupplierGroup> supplierGroupAuto(String filter) {
    if (getCompany() != null) {
      MainView main = Jsf.getMain();
      try {
        return UserRuntimeService.supplierGroupAuto(main, filter, getCompany().getId(), getAppUser());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Account getAccount() {
    return account;
  }

//  public SupplierGroup getSupplierGroup() {
//    return supplierGroup;
//  }
//
//  public void setSupplierGroup(SupplierGroup supplierGroup) {
//    this.supplierGroup = supplierGroup;
//  }
  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    if (accountGroup != null) {
      if (this.accountGroup == null || !this.accountGroup.getId().equals(accountGroup.getId())) {
        MainView main = Jsf.getMain();
        try {
          changeAccountGroup();
          UserAccountPreferences preferences = getUserAccountPreferences(main.em());
          preferences.setAccountGroupId(accountGroup);
//          getAppUser().getUserProfileId().setPreferedAccountGroup(accountGroup);
//          UserRuntimeService.insertOrUpdate(main, getAppUser());
          UserRuntimeService.insertOrUpdate(main, preferences);
        } catch (Throwable t) {
          Jsf.fatal(t, "system.error.unable.to.process.your.request");
        } finally {
          if (main != null) {
            main.close();
          }
        }
      }
    }
    this.accountGroup = accountGroup;
  }

//  public List<SupplierGroup> getSupplierGroupList() {
//    return supplierGroupList;
//  }
//
//  public void setSupplierGroupList(List<SupplierGroup> supplierGroupList) {
//    this.supplierGroupList = supplierGroupList;
//  }
  public List<AccountGroup> getAccountGroupList() {
    return accountGroupList;
  }

  public void setAccountGroupList(List<AccountGroup> accountGroupList) {
    this.accountGroupList = accountGroupList;
  }

  public Account getAccountCurrent() {
    if (account == null) {
      throw new UserMessageException("account.required");
    }
    return account;
  }

  public Account getPrimaryAccount() {
    return getAccountCurrent().getAccountId() == null ? account : account.getAccountId();
  }

  public void setAccount(Account account) {
    if (account != null) {
      if (this.account == null || this.account.getId() != account.getId()) {
        MainView main = Jsf.getMain();
        try {
          changeAccount(main, account);
          UserAccountPreferences preferences = getUserAccountPreferences(main.em());
          preferences.setAccountId(account);
//          getAppUser().getUserProfileId().setPreferedAccount(account);
//          UserRuntimeService.insertOrUpdate(main, getAppUser());
          UserRuntimeService.insertOrUpdate(main, preferences);
        } catch (Throwable t) {
          Jsf.fatal(t, "system.error.unable.to.process.your.request");
        } finally {
          if (main != null) {
            main.close();
          }
        }
      }
    }
    this.account = account;
  }

  private void changeAccount(AppEm em, Account account) {
    if (AccountService.INDIVIDUAL_SUPPLIER.equals(account.getPurchaseChannel())) {
      contract = UserRuntimeService.selectActiveContract(em, account);
    } else {
      contract = UserRuntimeService.selectActiveContract(em, account.getAccountId() == null ? account : account.getAccountId());
    }
    MainView main = Jsf.getMain();
    if (main != null) {
      String path = main.getViewPath();
      TaxCalculator tc = get().getTaxCalculator(getCompany());
      if (path.endsWith(FileConstant.PURCHASE_CONSIGNMENT_URL) || path.endsWith(FileConstant.PURCHASE_RETURN_CONSIGNMENT_URL) || path.endsWith(FileConstant.PURCHASE_CONSIGNMENT_RECEIPT_INFO)
              || path.endsWith(FileConstant.PURCHASE_REQ) || path.endsWith(FileConstant.PURCHASE_ORDER) || path.endsWith(FileConstant.PRODUCT)
              || path.endsWith(FileConstant.OPENING_STOCK_ENTRY) || path.endsWith(tc.getProductEntryView()) || path.endsWith(FileConstant.STOCK_ADJUSTMENT)
              || path.endsWith(FileConstant.PURCHASE_RETURN) || path.endsWith(FileConstant.DEBIT_NOTE_PURCHASE) || path.endsWith(FileConstant.CREDIT_NOTE_PURCHASE) || path.endsWith(FileConstant.EXPENSE_REIMP_ENDS_WITH)) {
        main.getPageData().reset();
        main.setViewType(ViewTypes.list);
      }
    }
  }

  private void changeAccountGroup() {
    MainView main = Jsf.getMain();
    if (main != null) {
      String path = main.getViewPath();
      TaxCalculator tc = get().getTaxCalculator(getCompany());
      if (path.endsWith(FileConstant.SALES_CONSIGNMENT_URL) || path.endsWith(FileConstant.SALES_RETURN_CONSIGNMENT) || path.endsWith(tc.getSalesInvoiceView())
              || path.endsWith(FileConstant.SALES_REQUEST) || path.endsWith(FileConstant.SALES_ORDER) || path.endsWith(FileConstant.SALES_RETURN)
              || path.endsWith(FileConstant.SALES_INVOICE) || path.endsWith(FileConstant.DEBIT_NOTE_SALES) || path.endsWith(FileConstant.CREDIT_NOTE_SALES)
              || path.endsWith(FileConstant.ACCOUNTING_CHEQUE_ENTRY) || path.endsWith(FileConstant.EXPENSE_REIMP_ENDS_WITH)
              || path.endsWith(FileConstant.SALES_SERVICE_INVOICE)) {
        main.getPageData().reset();
        main.setViewType(ViewTypes.list);
      }
    }
  }

  public void changeAccount(MainView main, Account account) {
    try {
      changeAccount(main.em(), account);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

//  public void changeAccountGroup(MainView main, AccountGroup accountGroup) {
//    changeAccountGroup();
//  }
  public Contract getContract() {
    if (account == null) {
      throw new UserMessageException("account.required");
    }
    return contract;
  }

  public void setCompanyList(List<Company> companyList) {
    this.companyList = companyList;
  }

  public List<Account> getNavAccountList() {
    return navAccountList;
  }

  public void setNavAccountList(List<Account> navAccountList) {
    this.navAccountList = navAccountList;
  }

  public UserAccountPreferences getUserAccountPreferences(AppEm em) {
    userAccountPreferences = UserRuntimeService.getUserAccountPreferences(em, company, getAppUser().getUserProfileId());
    if (userAccountPreferences == null) {
      userAccountPreferences = new UserAccountPreferences();
      if (getAppUser().getUserProfileId().getName().equals(AppConfig.rootUser)) {
        userAccountPreferences.setUserId(null);
      } else {
        userAccountPreferences.setUserId(getAppUser().getUserProfileId());
      }
      userAccountPreferences.setCompanyId(company);
    }
    return userAccountPreferences;
  }

  public void setUserAccountPreferences(UserAccountPreferences userAccountPreferences) {
    this.userAccountPreferences = userAccountPreferences;
  }

  public Date getMinEntryDate() {
    return SystemRuntimeConfig.getMinEntryDate(getCompany());
  }

  public Date getMaxEntryDate() {
    return SystemRuntimeConfig.getMaxEntryDate(getCompany());
  }

}
