/*
 * @(#)AccountGroupPriceListView.java	1.0 Wed Apr 13 15:41:14 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.LazyDataModel;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

import spica.scm.domain.AccountGroupPriceList;
import spica.scm.service.AccountGroupPriceListService;
import spica.scm.domain.AccountGroup;
import wawo.app.faces.Jsf;

/**
 * AccountGroupPriceListView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:14 IST 2016
 */
@Named(value = "accountGroupPriceListView")
@ViewScoped
public class AccountGroupPriceListView implements Serializable {

  private transient AccountGroupPriceList accountGroupPriceList;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountGroupPriceList> accountGroupPriceListLazyModel; 	//For lazy loading datatable.
  private transient AccountGroupPriceList[] accountGroupPriceListSelected;	 //Selected Domain Array
  private List<AccountGroupPriceList> listAccountGroupPriceList;
  private AccountGroup accountGroup;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    accountGroup = (AccountGroup) Jsf.popupParentValue(AccountGroup.class);
  }

  /**
   * Default Constructor.
   */
  public AccountGroupPriceListView() {
    super();
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public void setListAccountGroupPriceList(List<AccountGroupPriceList> listAccountGroupPriceList) {
    this.listAccountGroupPriceList = listAccountGroupPriceList;
  }

  /**
   * Return AccountGroupPriceList.
   *
   * @return AccountGroupPriceList.
   */
  public AccountGroupPriceList getAccountGroupPriceList() {
    if (accountGroupPriceList == null) {
      accountGroupPriceList = new AccountGroupPriceList();
    }
    return accountGroupPriceList;
  }

  /**
   * Set AccountGroupPriceList.
   *
   * @param accountGroupPriceList.
   */
  public void setAccountGroupPriceList(AccountGroupPriceList accountGroupPriceList) {
    this.accountGroupPriceList = accountGroupPriceList;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountGroupPriceList(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getAccountGroupPriceList().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setAccountGroupPriceList((AccountGroupPriceList) AccountGroupPriceListService.selectByPk(main, getAccountGroupPriceList()));
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
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountGroupPriceList(MainView main) {
    return saveOrCloneAccountGroupPriceList(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountGroupPriceList(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountGroupPriceList(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountGroupPriceList(MainView main, String key) {
    try {
      if (null != key) {
        switch (key) {
          case "save":
            AccountGroupPriceListService.insertOrUpdate(main, getAccountGroupPriceList());
            break;
          case "clone":
            AccountGroupPriceListService.clone(main, getAccountGroupPriceList());
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
   * Delete one or many AccountGroupPriceList.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountGroupPriceList(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(accountGroupPriceListSelected)) {
        AccountGroupPriceListService.deleteByPkArray(main, getAccountGroupPriceListSelected()); //many record delete from list
        main.commit("success.delete");
        accountGroupPriceListSelected = null;
      } else {
        AccountGroupPriceListService.deleteByPk(main, getAccountGroupPriceList());  //individual record delete from list or edit form
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
   *
   * @param main
   * @param accountGroupPriceList
   * @return
   */
  public String deleteAccountGroupPriceList(MainView main, AccountGroupPriceList accountGroupPriceList) {
    try {
      AccountGroupPriceListService.deleteByPk(main, accountGroupPriceList);
      setListAccountGroupPriceList(null);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Return LazyDataModel of AccountGroupPriceList.
   *
   * @return
   */
  public LazyDataModel<AccountGroupPriceList> getAccountGroupPriceListLazyModel() {
    return accountGroupPriceListLazyModel;
  }

  /**
   * Return AccountGroupPriceList[].
   *
   * @return
   */
  public AccountGroupPriceList[] getAccountGroupPriceListSelected() {
    return accountGroupPriceListSelected;
  }

  /**
   * Set AccountGroupPriceList[].
   *
   * @param accountGroupPriceListSelected
   */
  public void setAccountGroupPriceListSelected(AccountGroupPriceList[] accountGroupPriceListSelected) {
    this.accountGroupPriceListSelected = accountGroupPriceListSelected;
  }

  /**
   * AccountGroup autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountGroupAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountGroupAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountGroup> accountGroupAuto(String filter) {
    return ScmLookupView.accountGroupAuto(filter);
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void accountGroupPriceListDialogClose() {
    Jsf.popupReturn(accountGroup, null);
  }

  /**
   * Method handler for Account Group Price List datatable RowEditEvent event.
   *
   * Method will update the title field of account group price list object.
   *
   * @param event
   */
  public void onAccountGroupPriceListRowEdit(RowEditEvent event) {
    AccountGroupPriceList agpl = (AccountGroupPriceList) event.getObject();
    MainView main = Jsf.getMain();
    try {
      if (agpl.getId() == null) {
        agpl.setAccountGroupId(accountGroup);
        agpl.setIsDefault(0);
      }
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
   */
  public void actionNewAccountPriceList() {
    AccountGroupPriceList agp = new AccountGroupPriceList();
    listAccountGroupPriceList.add(0, agp);
  }

}
