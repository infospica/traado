/*
 * @(#)AccountExpenseDetailView.java	1.0 Mon Nov 27 13:55:44 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view.delete;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.AccountExpenseDetail;
import spica.fin.service.delete.AccountExpenseDetailService;
import spica.fin.domain.AccountExpense;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingDocType;
import spica.fin.view.FinLookupView;
import spica.scm.view.ScmLookupView;
import spica.sys.UserRuntimeView;

/**
 * AccountExpenseDetailView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Nov 27 13:55:44 IST 2017
 */
@Named(value = "accountExpenseDetailView")
@ViewScoped
public class AccountExpenseDetailView implements Serializable {

  private transient AccountExpenseDetail accountExpenseDetail;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountExpenseDetail> accountExpenseDetailLazyModel; 	//For lazy loading datatable.
  private transient AccountExpenseDetail[] accountExpenseDetailSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public AccountExpenseDetailView() {
    super();
  }

  /**
   * Return AccountExpenseDetail.
   *
   * @return AccountExpenseDetail.
   */
  public AccountExpenseDetail getAccountExpenseDetail() {
    if (accountExpenseDetail == null) {
      accountExpenseDetail = new AccountExpenseDetail();
    }
    return accountExpenseDetail;
  }

  /**
   * Set AccountExpenseDetail.
   *
   * @param accountExpenseDetail.
   */
  public void setAccountExpenseDetail(AccountExpenseDetail accountExpenseDetail) {
    this.accountExpenseDetail = accountExpenseDetail;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountExpenseDetail(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getAccountExpenseDetail().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setAccountExpenseDetail((AccountExpenseDetail) AccountExpenseDetailService.selectByPk(main, getAccountExpenseDetail()));
        } else if (main.isList()) {
          loadAccountExpenseDetailList(main);
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
   * Create accountExpenseDetailLazyModel.
   *
   * @param main
   */
  private void loadAccountExpenseDetailList(final MainView main) {
    if (accountExpenseDetailLazyModel == null) {
      accountExpenseDetailLazyModel = new LazyDataModel<AccountExpenseDetail>() {
        private List<AccountExpenseDetail> list;

        @Override
        public List<AccountExpenseDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountExpenseDetailService.listPaged(main);
            main.commit(accountExpenseDetailLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountExpenseDetail accountExpenseDetail) {
          return accountExpenseDetail.getId();
        }

        @Override
        public AccountExpenseDetail getRowData(String rowKey) {
          if (list != null) {
            for (AccountExpenseDetail obj : list) {
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
    String SUB_FOLDER = "scm_account_expense_detail/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountExpenseDetail(MainView main) {
    return saveOrCloneAccountExpenseDetail(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountExpenseDetail(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneAccountExpenseDetail(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountExpenseDetail(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountExpenseDetailService.insertOrUpdate(main, getAccountExpenseDetail());
            break;
          case "clone":
            AccountExpenseDetailService.clone(main, getAccountExpenseDetail());
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
   * Delete one or many AccountExpenseDetail.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountExpenseDetail(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(accountExpenseDetailSelected)) {
        AccountExpenseDetailService.deleteByPkArray(main, getAccountExpenseDetailSelected()); //many record delete from list
        main.commit("success.delete");
        accountExpenseDetailSelected = null;
      } else {
        AccountExpenseDetailService.deleteByPk(main, getAccountExpenseDetail());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
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
   * Return LazyDataModel of AccountExpenseDetail.
   *
   * @return
   */
  public LazyDataModel<AccountExpenseDetail> getAccountExpenseDetailLazyModel() {
    return accountExpenseDetailLazyModel;
  }

  /**
   * Return AccountExpenseDetail[].
   *
   * @return
   */
  public AccountExpenseDetail[] getAccountExpenseDetailSelected() {
    return accountExpenseDetailSelected;
  }

  /**
   * Set AccountExpenseDetail[].
   *
   * @param accountExpenseDetailSelected
   */
  public void setAccountExpenseDetailSelected(AccountExpenseDetail[] accountExpenseDetailSelected) {
    this.accountExpenseDetailSelected = accountExpenseDetailSelected;
  }

  /**
   * AccountExpense autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountExpenseAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountExpenseAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountExpense> accountExpenseAuto(String filter) {
    return ScmLookupView.accountExpenseAuto(filter);
  }

  /**
   * FinAccountingLedger autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.finAccountingLedgerAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.finAccountingLedgerAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> finAccountingLedgerAuto(String filter) {
    return FinLookupView.accountingLedgerAuto(filter, UserRuntimeView.instance().getCompany().getId());
  }

  /**
   * FinAccountingDocType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.finAccountingDocTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.finAccountingDocTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingDocType> finAccountingDocTypeAuto(String filter) {
    return ScmLookupView.accountingDocTypeAuto(filter);
  }
}
