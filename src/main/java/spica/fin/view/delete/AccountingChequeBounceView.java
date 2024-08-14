/*
 * @(#)AccountingChequeBounceView.java	1.0 Thu Feb 08 11:53:11 IST 2018 
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

import spica.fin.domain.AccountingChequeBounce;
import spica.fin.service.AccountingChequeBounceService;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.domain.AccountingLedger;
import spica.fin.view.FinLookupView;
import spica.scm.view.ScmLookupView;
import spica.sys.UserRuntimeView;

/**
 * AccountingChequeBounceView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Feb 08 11:53:11 IST 2018
 */
@Named(value = "finAccountingChequeBounceView")
@ViewScoped
public class AccountingChequeBounceView implements Serializable {

  private transient AccountingChequeBounce finAccountingChequeBounce;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountingChequeBounce> finAccountingChequeBounceLazyModel; 	//For lazy loading datatable.
  private transient AccountingChequeBounce[] finAccountingChequeBounceSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public AccountingChequeBounceView() {
    super();
  }

  /**
   * Return AccountingChequeBounce.
   *
   * @return AccountingChequeBounce.
   */
  public AccountingChequeBounce getAccountingChequeBounce() {
    if (finAccountingChequeBounce == null) {
      finAccountingChequeBounce = new AccountingChequeBounce();
    }
    return finAccountingChequeBounce;
  }

  /**
   * Set AccountingChequeBounce.
   *
   * @param finAccountingChequeBounce.
   */
  public void setAccountingChequeBounce(AccountingChequeBounce finAccountingChequeBounce) {
    this.finAccountingChequeBounce = finAccountingChequeBounce;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountingChequeBounce(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getAccountingChequeBounce().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setAccountingChequeBounce((AccountingChequeBounce) AccountingChequeBounceService.selectByPk(main, getAccountingChequeBounce()));
        } else if (main.isList()) {
          loadAccountingChequeBounceList(main);
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
   * Create finAccountingChequeBounceLazyModel.
   *
   * @param main
   */
  private void loadAccountingChequeBounceList(final MainView main) {
    if (finAccountingChequeBounceLazyModel == null) {
      finAccountingChequeBounceLazyModel = new LazyDataModel<AccountingChequeBounce>() {
        private List<AccountingChequeBounce> list;

        @Override
        public List<AccountingChequeBounce> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountingChequeBounceService.listPaged(main);
            main.commit(finAccountingChequeBounceLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountingChequeBounce finAccountingChequeBounce) {
          return finAccountingChequeBounce.getId();
        }

        @Override
        public AccountingChequeBounce getRowData(String rowKey) {
          if (list != null) {
            for (AccountingChequeBounce obj : list) {
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
    String SUB_FOLDER = "fin_accounting_cheque_bounce/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountingChequeBounce(MainView main) {
    try {
      return saveOrCloneAccountingChequeBounce(main, "save");
    } finally {
      main.close();
    }
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountingChequeBounce(MainView main) {
    try {
      main.setViewType(ViewTypes.newform);
      return saveOrCloneAccountingChequeBounce(main, "clone");
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
  private String saveOrCloneAccountingChequeBounce(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountingChequeBounceService.insertOrUpdate(main, getAccountingChequeBounce());
            break;
          case "clone":
            AccountingChequeBounceService.clone(main, getAccountingChequeBounce());
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
   * Delete one or many AccountingChequeBounce.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingChequeBounce(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(finAccountingChequeBounceSelected)) {
        AccountingChequeBounceService.deleteByPkArray(main, getAccountingChequeBounceSelected()); //many record delete from list
        main.commit("success.delete");
        finAccountingChequeBounceSelected = null;
      } else {
        AccountingChequeBounceService.deleteByPk(main, getAccountingChequeBounce());  //individual record delete from list or edit form
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
   * Return LazyDataModel of AccountingChequeBounce.
   *
   * @return
   */
  public LazyDataModel<AccountingChequeBounce> getAccountingChequeBounceLazyModel() {
    return finAccountingChequeBounceLazyModel;
  }

  /**
   * Return AccountingChequeBounce[].
   *
   * @return
   */
  public AccountingChequeBounce[] getAccountingChequeBounceSelected() {
    return finAccountingChequeBounceSelected;
  }

  /**
   * Set AccountingChequeBounce[].
   *
   * @param finAccountingChequeBounceSelected
   */
  public void setAccountingChequeBounceSelected(AccountingChequeBounce[] finAccountingChequeBounceSelected) {
    this.finAccountingChequeBounceSelected = finAccountingChequeBounceSelected;
  }

  /**
   * FinAccountingTransactionDetailItem autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.finAccountingTransactionDetailItemAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.finAccountingTransactionDetailItemAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingTransactionDetailItem> finAccountingTransactionDetailItemAuto(String filter) {
    return ScmLookupView.accountingTransactionDetailItemAuto(filter);
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
}
