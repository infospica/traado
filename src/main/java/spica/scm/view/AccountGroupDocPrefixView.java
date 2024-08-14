/*
 * @(#)AccountGroupView.java	1.0 Fri Feb 03 19:15:30 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Status;
import spica.scm.domain.PrefixType;
import spica.scm.domain.AccountGroupDocPrefix;
import spica.scm.service.AccountGroupDocPrefixService;
import wawo.app.faces.Jsf;

/**
 * AccountGroupView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 03 19:15:30 IST 2017
 */
@Named(value = "accountGroupDocPrefixView")
@ViewScoped
public class AccountGroupDocPrefixView implements Serializable {

  private transient AccountGroupDocPrefix accountGroupDocPrefix;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountGroupDocPrefix> accountGroupDocPrefixLazyModel; 	//For lazy loading datatable.
  private transient AccountGroupDocPrefix[] accountGroupDocPrefixSelected;	 //Selected Domain Array
  private transient Account account;
  private transient AccountGroup accountGroup;
  private transient boolean yearSequence;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    setAccount((Account) Jsf.popupParentValue(Account.class));
    getAccountGroupDocPrefix().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public AccountGroupDocPrefixView() {
    super();
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public AccountGroup getAccountGroup() {
    if (accountGroup == null) {
      accountGroup = new AccountGroup();
    }
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public boolean isYearSequence() {
    return yearSequence;
  }

  public void setYearSequence(boolean yearSequence) {
    this.yearSequence = yearSequence;
  }

  /**
   * Return AccountGroup.
   *
   * @return AccountGroup.
   */
  public AccountGroupDocPrefix getAccountGroupDocPrefix() {
    if (accountGroupDocPrefix == null) {
      accountGroupDocPrefix = new AccountGroupDocPrefix();
    }
    return accountGroupDocPrefix;
  }

  /**
   * Set AccountGroup.
   *
   * @param accountGroupDocPrefix.
   */
  public void setAccountGroupDocPrefix(AccountGroupDocPrefix accountGroupDocPrefix) {
    this.accountGroupDocPrefix = accountGroupDocPrefix;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountGroupDocPrefix(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        setYearSequence(false);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getAccountGroupDocPrefix().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setAccountGroupDocPrefix((AccountGroupDocPrefix) AccountGroupDocPrefixService.selectByPk(main, getAccountGroupDocPrefix()));
          if (getAccountGroupDocPrefix() != null && getAccountGroupDocPrefix().getYearSequence() != null && getAccountGroupDocPrefix().getYearSequence() != 0) {
            setYearSequence(true);
          }
        } else if (ViewType.list.toString().equals(viewType)) {
          loadAccountGroupList(main);
        }
      } catch (Throwable t) {
        main.rollback(t, viewType);
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
    if (accountGroupDocPrefixLazyModel == null) {
      accountGroupDocPrefixLazyModel = new LazyDataModel<AccountGroupDocPrefix>() {
        private List<AccountGroupDocPrefix> list;

        @Override
        public List<AccountGroupDocPrefix> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountGroupDocPrefixService.listPaged(main);
            main.commit(accountGroupDocPrefixLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountGroupDocPrefix accountGroupDocPrefix) {
          return accountGroupDocPrefix.getId();
        }

        @Override
        public AccountGroupDocPrefix getRowData(String rowKey) {
          if (list != null) {
            for (AccountGroupDocPrefix obj : list) {
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

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountGroupDocPrefix(MainView main) {
    return saveOrCloneAccountGroupDocPrefix(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountGroupDocPrefix(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountGroupDocPrefix(main, "clone");
  }

  /**
   * Insert book genre relation.
   *
   * @param main
   * @return
   */
  public void insertAccountGroupDetails(MainView main) {
    try {
      //AccountGroupService.insertArray(main, getAccountGroupSelected(), getAccount());
      main.commit("success.select");
      Jsf.popupReturn(account, null);
    } catch (Throwable t) {
      main.rollback(t, "error.list");
    } finally {
      main.close();
    }
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountGroupDocPrefix(MainView main, String key) {
    try {
      if (null != key) {
        switch (key) {
          case "save":
            AccountGroupDocPrefixService.insertOrUpdate(main, getAccountGroupDocPrefix());
            break;
          case "clone":
            AccountGroupDocPrefixService.clone(main, getAccountGroupDocPrefix());
            break;
        }
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

  /**
   * Delete one or many AccountGroup.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountGroup(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(accountGroupDocPrefixSelected)) {
        AccountGroupDocPrefixService.deleteByPkArray(main, getAccountGroupDocPrefixSelected()); //many record delete from list
        main.commit("success.delete");
        accountGroupDocPrefixSelected = null;
      } else {
        AccountGroupDocPrefixService.deleteByPk(main, getAccountGroupDocPrefix());  //individual record delete from list or edit form
        main.commit("success.delete");
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
  public LazyDataModel<AccountGroupDocPrefix> getAccountGroupDocPrefixLazyModel() {
    return accountGroupDocPrefixLazyModel;
  }

  /**
   * Return AccountGroup[].
   *
   * @return
   */
  public AccountGroupDocPrefix[] getAccountGroupDocPrefixSelected() {
    return accountGroupDocPrefixSelected;
  }

  /**
   * Set AccountGroup[].
   *
   * @param accountGroupDocPrefixSelected
   */
  public void setAccountGroupDocPrefixSelected(AccountGroupDocPrefix[] accountGroupDocPrefixSelected) {
    this.accountGroupDocPrefixSelected = accountGroupDocPrefixSelected;
  }

  /**
   * Status autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.statusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.statusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  /**
   * PrefixType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.prefixTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.prefixTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<PrefixType> prefixTypeAuto(String filter) {
    return ScmLookupView.prefixTypeAuto(filter);
  }

  public void accountGroupDocPrefixReturn() {
    Jsf.popupReturn(getAccount(), null);
  }

  public void yearSequenceChangeHandler() {
    if (getAccountGroupDocPrefix().getYearSequence() == 0) {
      setYearSequence(false);
    } else {
      setYearSequence(true);
    }
  }
}
