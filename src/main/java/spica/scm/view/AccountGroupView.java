/*
 * @(#)AccountGroupView.java	1.0 Wed Apr 13 15:41:14 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.fin.domain.AccountingLedger;
import spica.fin.service.AccountingLedgerService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupDocPrefix;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Brand;
import spica.scm.domain.Company;
import spica.scm.domain.PrefixType;
import spica.scm.domain.Status;
import spica.scm.service.AccountGroupBrandsService;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.AccountGroupPriceListService;
import spica.scm.service.AccountGroupService;
import spica.scm.service.AccountService;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * AccountGroupView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:14 IST 2016
 */
@Named(value = "accountGroupView")
@ViewScoped
public class AccountGroupView implements Serializable {

  private transient AccountGroup accountGroup;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountGroup> accountGroupLazyModel; 	//For lazy loading datatable.
  private transient AccountGroup[] accountGroupSelected;	 //Selected Domain Array  
  private transient Account defualtAccount;
  private transient List<Account> accountList;
  private transient List<AccountGroupPriceList> accountGroupPriceList;
  private transient String priceListName;
  private transient boolean newPriceList;
  private transient boolean yearSequence;
  private transient AccountGroupDocPrefix accountGroupDocPrefix;
  private transient List<Brand> brandList;
  private transient String tempCode;
  private transient List<AccountGroupDocPrefix> accountGroupDocDebitCreditList;
  private transient List<AccountGroupDocPrefix> accountDocPrefixSalesList;
  private transient List<AccountGroupDocPrefix> accountDocPrefixPurchaseList;

  /**
   * Default Constructor.
   */
  public AccountGroupView() {
    super();
  }

  /**
   * Return AccountGroup.
   *
   * @return AccountGroup.
   */
  public AccountGroup getAccountGroup() {
    if (accountGroup == null) {
      accountGroup = new AccountGroup();
      accountGroup.setAccountGroupDocPrefix(new AccountGroupDocPrefix());
    }
    if (accountGroup.getCompanyId() == null) {
      accountGroup.setCompanyId(UserRuntimeView.instance().getCompany());
      accountGroup.setFinancialYear(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    return accountGroup;
  }

  public AccountGroupDocPrefix getAccountGroupDocPrefix() {
    return accountGroupDocPrefix;
  }

  public void setAccountGroupDocPrefix(AccountGroupDocPrefix accountGroupDocPrefix) {
    this.accountGroupDocPrefix = accountGroupDocPrefix;
  }

  public Account getDefualtAccount() {
    return defualtAccount;
  }

  public void setDefualtAccount(Account defualtAccount) {
    this.defualtAccount = defualtAccount;
  }

  public List<Account> getAccountList() {
    if (accountList == null) {
      accountList = new ArrayList<>();
    }
    return accountList;
  }

  public void setAccountList(List<Account> accountList) {
    this.accountList = accountList;
  }

  public boolean isYearSequence() {
    return yearSequence;
  }

  public void setYearSequence(boolean yearSequence) {
    this.yearSequence = yearSequence;
  }

  public boolean isPriceListApplicable() {
    if (getAccountGroup().getIsDefault() != null && getAccountGroup().getIsDefault() == 1) {
      return true;
    }
    return true;
  }

  public List<AccountGroupPriceList> getAccountGroupPriceList() {
    return accountGroupPriceList;
  }

  public void setAccountGroupPriceList(List<AccountGroupPriceList> accountGroupPriceList) {
    this.accountGroupPriceList = accountGroupPriceList;
  }

  public List<Brand> getBrandList() {
    return brandList;
  }

  public void setBrandList(List<Brand> brandList) {
    this.brandList = brandList;
  }

  public List<AccountGroupPriceList> getAccountGroupPriceList(MainView main) {
    if (getAccountGroupPriceList() == null) {
      try {
        setAccountGroupPriceList(AccountGroupPriceListService.selectAccountGroupPriceListByAccountGroup(main, getAccountGroup()));
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return getAccountGroupPriceList();
  }

  public List<Brand> lookupVendorBrands(MainView main) {
    try {
      if (!StringUtil.isEmpty(getAccountList())) {
        return AccountGroupService.lookupBrandBySupplier(main, getAccountList());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public String getPriceListName() {
    return priceListName;
  }

  public void setPriceListName(String priceListName) {
    this.priceListName = priceListName;
  }

  public boolean isNewPriceList() {
    return newPriceList;
  }

  public void setNewPriceList(boolean newPriceList) {
    this.newPriceList = newPriceList;
  }

  public String actionReturnNull() {
    return null;
  }

  /**
   * Set AccountGroup.
   *
   * @param accountGroup.
   */
  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public String getTempCode() {
    return tempCode;
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionNewAccoutPriceList(MainView main) {
    if (!StringUtil.isEmpty(priceListName)) {
      try {
        AccountGroupPriceList accountGroupPrice = new AccountGroupPriceList();
        accountGroupPrice.setTitle(priceListName);
        accountGroupPrice.setAccountGroupId(accountGroup);
        AccountGroupPriceListService.insert(main, accountGroupPrice);
        setAccountGroupPriceList(null);
        main.commit("success.save");
        setPriceListName("");
      } catch (Throwable t) {
        main.rollback(t, "error.save");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   *
   * @param event
   */
  public void onAccountGroupPriceListRowEdit(RowEditEvent event) {
    AccountGroupPriceList agpl = (AccountGroupPriceList) event.getObject();
    MainView main = Jsf.getMain();
    try {
      AccountGroupPriceListService.insertOrUpdate(main, agpl);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param main
   * @param accountGrpPriceVar
   * @return
   */
  public String deleteAccountGroupPriceList(MainView main, AccountGroupPriceList accountGrpPriceVar) {
    try {
      AccountGroupPriceListService.deleteByPk(main, accountGrpPriceVar);
      setAccountGroupPriceList(null);
      setAccountList(null);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;

  }

  /**
   *
   * @param main
   * @return
   */
  public String actionNewForm(MainView main) {
    setAccountList(null);
    main.setViewType(ViewType.newform.toString());
    return null;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountGroup(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        setAccountDocPrefixPurchaseList(null);
        setAccountDocPrefixSalesList(null);
        setAccountGroupDocDebitCreditList(null);
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          if (getAccountGroup().getCompanyId() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          }
          getAccountGroup().reset();
          setAccountGroupPriceList(null);
          setBrandList(null);
          getAccountGroup().setServiceAsExpense(SystemConstants.SERVICE_AS_NON_EXPENSE);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          tempCode = getAccountGroup().getGroupCode();
          setAccountGroupPriceList(null);
          setAccountGroup((AccountGroup) AccountGroupService.selectByPk(main, getAccountGroup()));
          getAccountGroup().setFinancialYear(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
          getAccountGroup().setAccountGroupDocPrefix(AccountGroupDocPrefixService.selectAccountGroupDocPrefixByAccountGroup(main, getAccountGroup()));
          accountList = AccountGroupService.selectAccoutByAccountGroup(main, getAccountGroup());
          brandList = AccountGroupBrandsService.selectBrandByAccountGroup(main, getAccountGroup());
          if (accountList != null && !accountList.isEmpty()) {
            defualtAccount = accountList.get(0);
          }

        } else if (ViewType.list.toString().equals(viewType)) {
          getAccountGroup().reset();
          setAccountList(null);
          setDefualtAccount(null);
          setAccountGroupPriceList(null);
          setBrandList(null);
          loadAccountGroupList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create accountGroupLazyModel.
   *
   * @param main
   */
  private void loadAccountGroupList(final MainView main) {
    if (accountGroupLazyModel == null) {
      accountGroupLazyModel = new LazyDataModel<AccountGroup>() {
        private List<AccountGroup> list;

        @Override
        public List<AccountGroup> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getAccountGroup().getCompanyId() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = AccountGroupService.listPagedAccountGroup(main, getAccountGroup().getCompanyId());
              main.commit(accountGroupLazyModel, first, pageSize);
            }
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountGroup accountGroup) {
          return accountGroup.getId();
        }

        @Override
        public AccountGroup getRowData(String rowKey) {
          if (list != null) {
            for (AccountGroup obj : list) {
              if (rowKey.equals(obj.getId().toString())) {
                return obj;
              }
            }
          }
          return null;
        }
      };
    }
  }

  private void uploadFiles() {
    String SUB_FOLDER = "scm_account_group/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountGroup(MainView main) {
    return saveOrCloneAccountGroup(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountGroup(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountGroup(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountGroup(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            getAccountGroup().setCompanyId(UserRuntimeView.instance().getCompany());
            getAccountGroup().setIsDefault(0);
            getAccountGroup().setGroupCode(getAccountGroup().getGroupCode().toUpperCase());
            setAccountGroup(AccountGroupService.insertOrUpdate(main, getAccountGroup(), getAccountList(), getBrandList()));
            AccountGroupBrandsService.insertOrUpdate(main, getAccountGroup(), getBrandList());
            if (getTempCode() != null && !getTempCode().equals(getAccountGroup().getGroupCode())) {
              AccountGroupDocPrefixService.updateDocPrefixByAccountGroup(main, getTempCode(), getAccountGroup(), getAccountGroup().getFinancialYear());
            }
            AccountGroupDocPrefixService.insertOrUpdateCustomAccountGroupPrefix(main, getAccountGroup());
            break;
          case "clone":
            getAccountGroup().setCompanyId(UserRuntimeView.instance().getCompany());
            getAccountGroup().setIsDefault(0);
            getAccountGroup().setGroupCode(getAccountGroup().getGroupCode().toUpperCase());
            AccountGroupService.clone(main, getAccountGroup());
            AccountGroupBrandsService.insertOrUpdate(main, getAccountGroup(), getBrandList());
            break;
        }
        AccountGroupService.updateAccountGroup(main, accountGroup);
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  /**
   * Delete one or many AccountGroup.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountGroup(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(accountGroupSelected)) {
        int rvalue = AccountGroupService.deleteByPkArray(main, getAccountGroupSelected()); //many record delete from list
        if (rvalue == 1) {
          main.commit("error.foreignkey.violates.constraint");
          return null;
        } else {
          main.commit("success.delete");
        }
        accountGroupSelected = null;
      } else {
        int rvalue = AccountGroupService.deleteByPk(main, getAccountGroup());  //individual record delete from list or edit form
        if (rvalue == 1) {
          main.commit("error.foreignkey.violates.constraint");
          return null;
        } else {
          main.commit("success.delete");
        }
        if ("editform".equals(main.getViewType())) {
          main.setViewType(ViewTypes.newform);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Return LazyDataModel of AccountGroup.
   *
   * @return
   */
  public LazyDataModel<AccountGroup> getAccountGroupLazyModel() {
    return accountGroupLazyModel;
  }

  /**
   * Return AccountGroup[].
   *
   * @return
   */
  public AccountGroup[] getAccountGroupSelected() {
    return accountGroupSelected;
  }

  /**
   * Set AccountGroup[].
   *
   * @param accountGroupSelected
   */
  public void setAccountGroupSelected(AccountGroup[] accountGroupSelected) {
    this.accountGroupSelected = accountGroupSelected;
  }

  /**
   * Company autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.companyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.companyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Company> companyAuto(String filter) {
    return ScmLookupView.companyAuto(filter);
  }

  /**
   * Account autocomplete filter.
   *
   * Method to select list of Account by its business type. (Eg. SS,CSA,C&F).
   *
   * @param main
   * @param filter
   * @return
   */
  public List<Account> accountByCompanyTradeProfileAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      if (getAccountList() != null && !getAccountList().isEmpty()) {
        if (defualtAccount == null) {
          defualtAccount = AccountService.selectByPk(main, getAccountList().get(0));
        }
        return ScmLookupExtView.accountByTradeTypeAuto(filter, getAccountGroup().getCompanyId().getId(), defualtAccount.getCompanyTradeProfileId().getId());
      } else {
        return ScmLookupExtView.accountByPrimaryVendorAuto(filter, getAccountGroup().getCompanyId().getId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param event
   */
  public void onAccountUnSelect(UnselectEvent event) {
    MainView main = Jsf.getMain();
    try {
      Account account = (Account) event.getObject();
      if (account.getAccountId() == null) {
        List<Account> tempList = AccountGroupService.selectChildAccountLists(main, account);
        for (Account acc : tempList) {
          if (acc.getAccountId() != null) {
            if (account.getId().intValue() == acc.getAccountId().getId()) {
              if (getAccountList().contains(acc)) {
                getAccountList().remove(acc);
              }
            }
          }
        }
      }
      if (getAccountList().size() == 1) {
        getAccountList().clear();
        setDefualtAccount(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @return
   */
  public String addNewAccountGroup() {
    if (getAccountGroupPriceList() != null && (getAccountGroupPriceList().isEmpty() || getAccountGroupPriceList().get(0).getId() != null)) {
      AccountGroupPriceList agp = new AccountGroupPriceList();
      agp.setAccountGroupId(getAccountGroup());
      agp.setIsDefault(0);
      getAccountGroupPriceList().add(0, agp);
    }
    return null;

  }

  /**
   *
   * @param agpl
   */
  public void defaultProductPriceListFormDialog(AccountGroupPriceList agpl) {
    Jsf.popupList(FileConstant.PRODUCT_PRICE_LIST_VO, getAccountGroup(), agpl.getId());
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<PrefixType> prefixTypeAuto(String filter) {
    return ScmLookupView.prefixTypeAuto(filter);
  }

  /**
   *
   */
  public void yearSequenceChangeHandler() {
    if (getAccountGroup().getAccountGroupDocPrefix().getYearSequence() == 0) {
      setYearSequence(false);
    } else {
      setYearSequence(true);
    }
  }

  /**
   *
   * @param context
   * @param component
   * @param value
   * @throws ValidatorException
   */
  public void accountGroupValidation(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    List<Account> list = (List<Account>) value;
    String validateParam = (String) Jsf.getParameter("validateAccounts");
    if (!StringUtil.isEmpty(validateParam)) {
      if (list != null && list.size() <= 1) {
        Jsf.error(component, "error.account.group.exist");
      } else if (list != null && list.size() > 1) {
        MainView main = Jsf.getMain();
        try {
          long exist = AccountGroupService.isAccountGroupExist(main, getAccountGroup().getId(), list);
          if (exist > 0) {
            Jsf.error(component, "error.account.group.exist");
          }
        } catch (Throwable t) {
          main.rollback(t, "error.select");
        } finally {
          main.close();
        }
      }
    }
  }

  public void actionDeletePriceList(MainView main, AccountGroupPriceList accountGroupPriceList) {
    try {
      if (accountGroupPriceList != null) {
        if (accountGroupPriceList.getId() != null) {
          AccountGroupPriceListService.deleteAccountGroupPriceList(main, accountGroupPriceList);
        }
        getAccountGroupPriceList().remove(accountGroupPriceList);
      } else {
        getAccountGroupPriceList().remove(accountGroupPriceList);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public List<AccountGroupDocPrefix> getAccountGroupDocDebitCreditList() {
    return accountGroupDocDebitCreditList;
  }

  public void setAccountGroupDocDebitCreditList(List<AccountGroupDocPrefix> accountGroupDocDebitCreditList) {
    this.accountGroupDocDebitCreditList = accountGroupDocDebitCreditList;
  }

  public List<AccountGroupDocPrefix> getAccountGroupDocDebitCreditList(MainView main) {
    if (getAccountGroupDocDebitCreditList() == null) {
      if (main.isEdit()) {
        try {
          setAccountGroupDocDebitCreditList(AccountGroupDocPrefixService.selectDebitCreditNoteCustomAccountGroupDocPrefix(main, getAccountGroup()));
        } catch (Throwable t) {
          main.rollback(t, "error.select");
        } finally {
          main.close();
        }
      }
    }
    return accountGroupDocDebitCreditList;
  }

  public List<AccountGroupDocPrefix> getSalesAccountGroupDocPrefix(MainView main) {
    try {
      if (getAccountDocPrefixSalesList() == null && main.isEdit()) {
        setAccountDocPrefixSalesList(AccountGroupDocPrefixService.selectSalesCustomAccountGroupDocPrefix(main, getAccountGroup()));
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return getAccountDocPrefixSalesList();
  }

  public List<AccountGroupDocPrefix> getPurchaseAccountGroupDocPrefix(MainView main) {
    try {
      if (getAccountDocPrefixPurchaseList() == null && main.isEdit()) {
        setAccountDocPrefixPurchaseList(AccountGroupDocPrefixService.selectPurchaseCustomAccountGroupDocPrefix(main, getAccountGroup()));
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return getAccountDocPrefixPurchaseList();
  }

  public List<AccountGroupDocPrefix> getAccountDocPrefixSalesList() {
    return accountDocPrefixSalesList;
  }

  public void setAccountDocPrefixSalesList(List<AccountGroupDocPrefix> accountDocPrefixSalesList) {
    this.accountDocPrefixSalesList = accountDocPrefixSalesList;
  }

  public List<AccountGroupDocPrefix> getAccountDocPrefixPurchaseList() {
    return accountDocPrefixPurchaseList;
  }

  public void setAccountDocPrefixPurchaseList(List<AccountGroupDocPrefix> accountDocPrefixPurchaseList) {
    this.accountDocPrefixPurchaseList = accountDocPrefixPurchaseList;
  }

//  public List<AccountingLedger> getExpenseLedgerList(MainView main) {
//    return AccountingLedgerService.selectLedgerListByLedgerExpense(main, UserRuntimeView.instance().getCompany().getId());
//  }
  public void updateExpenseLedger() {
    if (getAccountGroup().getServiceAsExpense().intValue() == 0) {
      getAccountGroup().setExpenseLedgerId(null);
    }
  }

  public void updateExpenseLedger2() {
    if (getAccountGroup().getService2Enabled().intValue() == 0) {
      getAccountGroup().setService2LedgerId(null);
    }
  }

  public List<AccountingLedger> salesExpenseLedgerAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return AccountingLedgerService.selectLedgerListByLedgerExpense(main, UserRuntimeView.instance().getCompany().getId(), filter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void openAccount() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      // opens a new form if id is null else edit
      Jsf.popupForm(FileConstant.ACCOUNT, null);
    }
  }

  public void openBrand() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      // opens a new form if id is null else edit
      Jsf.popupForm(FileConstant.BASIC_BRAND, null);
    }
  }
}
