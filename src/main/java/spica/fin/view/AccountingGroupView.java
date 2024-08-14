/*
 * @(#)AccountingGroupView.java	1.0 Fri Apr 28 12:24:49 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.fin.domain.AccountingGroup;
import spica.fin.domain.AccountingHead;
import spica.fin.domain.AccountingLedger;
import spica.fin.service.AccountingGroupService;
import spica.fin.view.FinLookupView;
import spica.fin.service.AccountingLedgerService;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeView;
import wawo.app.config.AppConfig;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * AccountingGroupView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 28 12:24:49 IST 2017
 */
@Named(value = "accountingGroupView")
@ViewScoped
public class AccountingGroupView implements Serializable {

  private transient AccountingGroup accountingGroup;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountingGroup> accountingGroupLazyModel; 	//For lazy loading datatable.
  private transient AccountingGroup[] accountingGroupSelected;	 //Selected Domain Array
  private transient LazyDataModel<AccountingLedger> accountingLedgerLazyModel; 	//For lazy loading datatable.
  private transient AccountingHead selectedAccountingHead;	//Domain object/selected Domain.

  /**
   * Default Constructor.
   */
  public AccountingGroupView() {
    super();
  }

  /**
   * Return AccountingGroup.
   *
   * @return AccountingGroup.
   */
  public AccountingGroup getAccountingGroup() {
    if (accountingGroup == null) {
      accountingGroup = new AccountingGroup();
    }
    if(accountingGroup.getCompanyId() == null){
      accountingGroup.setCompanyId(UserRuntimeView.instance().getCompany().getId());
    }
    return accountingGroup;
  }

  /**
   * Set AccountingGroup.
   *
   * @param accountingGroup.
   */
  public void setAccountingGroup(AccountingGroup accountingGroup) {
    this.accountingGroup = accountingGroup;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountingGroup(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getAccountingGroup().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setAccountingGroup((AccountingGroup) AccountingGroupService.selectByPk(main, getAccountingGroup()));
          main.getPageData().reset();
        } else if (ViewTypes.isList(viewType)) {
           getAccountingGroup().setCompanyId(null);
          loadAccountingGroupList(main);
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
   * Create accountingGroupLazyModel.
   *
   * @param main
   */
  private void loadAccountingGroupList(final MainView main) {
    if (accountingGroupLazyModel == null) {
      accountingGroupLazyModel = new LazyDataModel<AccountingGroup>() {
        private List<AccountingGroup> list;

        @Override
        public List<AccountingGroup> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountingGroupService.listPaged(main, getSelectedAccountingHead());
            main.commit(accountingGroupLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountingGroup accountingGroup) {
          return accountingGroup.getId();
        }

        @Override
        public AccountingGroup getRowData(String rowKey) {
          if (list != null) {
            for (AccountingGroup obj : list) {
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
    String SUB_FOLDER = "fin_accounting_group/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountingGroup(MainView main) {
    return saveOrCloneAccountingGroup(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountingGroup(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountingGroup(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountingGroup(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            //   if (getAccountingGroup().getParentId() != null && getAccountingGroup().getGrandParentId() == null) {
            //   getAccountingGroup().setGrandParentId(getAccountingGroup().getParentId().getId());
            // } else if (getAccountingGroup().getParentId() != null && getAccountingGroup().getGrandParentId() != null) {

            // }
            getAccountingGroup().setOpeningEntry(0);
            AccountingGroupService.insertOrUpdate(main, getAccountingGroup());
            break;
          case "clone":
            AccountingGroupService.clone(main, getAccountingGroup());
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
   * Delete one or many AccountingGroup.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingGroup(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(accountingGroupSelected)) {
        AccountingGroupService.deleteByPkArray(main, getAccountingGroupSelected()); //many record delete from list
        main.commit("success.delete");
        accountingGroupSelected = null;
      } else {
        AccountingGroupService.deleteByPk(main, getAccountingGroup());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (ViewTypes.isEdit(main.getViewType())) {
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
   * Return LazyDataModel of AccountingGroup.
   *
   * @return
   */
  public LazyDataModel<AccountingGroup> getAccountingGroupLazyModel() {
    return accountingGroupLazyModel;
  }

  /**
   * Return AccountingGroup[].
   *
   * @return
   */
  public AccountingGroup[] getAccountingGroupSelected() {
    return accountingGroupSelected;
  }

  /**
   * Set AccountingGroup[].
   *
   * @param accountingGroupSelected
   */
  public void setAccountingGroupSelected(AccountingGroup[] accountingGroupSelected) {
    this.accountingGroupSelected = accountingGroupSelected;
  }

  /**
   * AccountingGroup autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingGroupAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingGroupAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingGroup> accountingGroupAuto(String filter) {
    return FinLookupView.accountingGroupAuto(filter, getAccountingGroup().getCompanyId());
  }

  /**
   * AccountingHead autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingHeadAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingHeadAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingHead> accountingHeadAuto(String filter) {
    return FinLookupView.accountingHeadAuto();
  }

  /**
   *
   * @return
   */
  public List<AccountingHead> accountingHeadSelect() {
    return FinLookupView.accountingHeadAuto();
  }

  /**
   * Create accountingLedgerLazyModel.
   *
   * @param main
   */
  public void loadAccountingLedgerList(final MainView main) {
    if (accountingLedgerLazyModel == null) {
      accountingLedgerLazyModel = new LazyDataModel<AccountingLedger>() {
        private List<AccountingLedger> list;

        @Override
        public List<AccountingLedger> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountingLedgerService.listPagedByGroup(main, getAccountingGroup().getCompanyId(), getAccountingGroup());
            main.commit(getAccountingLedgerLazyModel(), first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountingLedger accountingLedger) {
          return accountingLedger.getId();
        }

        @Override
        public AccountingLedger getRowData(String rowKey) {
          if (list != null) {
            for (AccountingLedger obj : list) {
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

  public LazyDataModel<AccountingLedger> getAccountingLedgerLazyModel() {
    return accountingLedgerLazyModel;
  }

  public void openLedgerPopup(AccountingLedger accountingLedger) {
    Jsf.popupForm(FileConstant.ACCOUNTING_LEDGER, accountingLedger, accountingLedger.getId());
  }

  public AccountingHead getSelectedAccountingHead() {
    return selectedAccountingHead;
  }

  public void setSelectedAccountingHead(AccountingHead selectedAccountingHead) {
    this.selectedAccountingHead = selectedAccountingHead;
  }

}
