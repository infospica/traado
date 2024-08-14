/*
 * @(#)AccountingTransactionView.java	1.0 Fri Feb 24 15:58:33 IST 2017 
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
import javax.servlet.http.Part;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.AccountingTransaction;
import spica.fin.service.AccountingTransactionService;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingDocType;
import spica.fin.domain.AccountingHead;
import spica.fin.view.FinLookupView;
import spica.scm.view.ScmLookupView;
import spica.sys.UserRuntimeView;

/**
 * AccountingTransactionView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:33 IST 2017
 */
@Named(value = "accountingTransactionView")
@ViewScoped
public class AccountingTransactionView implements Serializable {

  private transient AccountingTransaction accountingTransaction;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountingTransaction> accountingTransactionLazyModel; 	//For lazy loading datatable.
  private transient AccountingTransaction[] accountingTransactionSelected;	 //Selected Domain Array
  private transient Part referenceDocFilePathPart;
  private transient AccountingHead accountingHeadId;

  /**
   * Default Constructor.
   */
  public AccountingTransactionView() {
    super();
  }

  /**
   * Return AccountingTransaction.
   *
   * @return AccountingTransaction.
   */
  public AccountingTransaction getAccountingTransaction() {
    if (accountingTransaction == null) {
      accountingTransaction = new AccountingTransaction();
    }
    if (accountingTransaction.getCompanyId() == null) {
      accountingTransaction.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return accountingTransaction;
  }

  /**
   * Set AccountingTransaction.
   *
   * @param accountingTransaction.
   */
  public void setAccountingTransaction(AccountingTransaction accountingTransaction) {
    this.accountingTransaction = accountingTransaction;
  }

  public AccountingHead getAccountingHeadId() {
    return accountingHeadId;
  }

  public void setAccountingHeadId(AccountingHead accountingHeadId) {
    this.accountingHeadId = accountingHeadId;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountingTransaction(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getAccountingTransaction().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setAccountingTransaction((AccountingTransaction) AccountingTransactionService.selectByPk(main, getAccountingTransaction()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadAccountingTransactionList(main);
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
   * Create accountingTransactionLazyModel.
   *
   * @param main
   */
  private void loadAccountingTransactionList(final MainView main) {
    if (accountingTransactionLazyModel == null) {
      accountingTransactionLazyModel = new LazyDataModel<AccountingTransaction>() {
        private List<AccountingTransaction> list;

        @Override
        public List<AccountingTransaction> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountingTransactionService.listPaged(main);
            main.commit(accountingTransactionLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountingTransaction accountingTransaction) {
          return accountingTransaction.getId();
        }

        @Override
        public AccountingTransaction getRowData(String rowKey) {
          if (list != null) {
            for (AccountingTransaction obj : list) {
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
    String SUB_FOLDER = "fin_accounting_transaction/";
    if (referenceDocFilePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      // JsfIo.uploadPrivate(referenceDocFilePathPart, getAccountingTransaction().getReferenceDocFilePath(), SUB_FOLDER);
      // getAccountingTransaction().setReferenceDocFilePath(JsfIo.getDbPath(referenceDocFilePathPart, SUB_FOLDER));
      referenceDocFilePathPart = null;	//import to set as null.
    }
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountingTransaction(MainView main) {
    return saveOrCloneAccountingTransaction(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountingTransaction(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountingTransaction(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountingTransaction(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountingTransactionService.insertOrUpdate(main, getAccountingTransaction());
            break;
          case "clone":
            AccountingTransactionService.clone(main, getAccountingTransaction());
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
   * Delete one or many AccountingTransaction.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingTransaction(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(accountingTransactionSelected)) {
        AccountingTransactionService.deleteByPkArray(main, getAccountingTransactionSelected()); //many record delete from list
        main.commit("success.delete");
        accountingTransactionSelected = null;
      } else {
        AccountingTransactionService.deleteByPk(main, getAccountingTransaction());  //individual record delete from list or edit form
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
   * Return LazyDataModel of AccountingTransaction.
   *
   * @return
   */
  public LazyDataModel<AccountingTransaction> getAccountingTransactionLazyModel() {
    return accountingTransactionLazyModel;
  }

  /**
   * Return AccountingTransaction[].
   *
   * @return
   */
  public AccountingTransaction[] getAccountingTransactionSelected() {
    return accountingTransactionSelected;
  }

  /**
   * Set AccountingTransaction[].
   *
   * @param accountingTransactionSelected
   */
  public void setAccountingTransactionSelected(AccountingTransaction[] accountingTransactionSelected) {
    this.accountingTransactionSelected = accountingTransactionSelected;
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getReferenceDocFilePathPart() {
    return referenceDocFilePathPart;
  }

  /**
   * Set Part referenceDocFilePathPart.
   *
   * @param referenceDocFilePathPart.
   */
  public void setReferenceDocFilePathPart(Part referenceDocFilePathPart) {
    if (this.referenceDocFilePathPart == null || referenceDocFilePathPart != null) {
      this.referenceDocFilePathPart = referenceDocFilePathPart;
    }
  }

  /**
   * AccountingLedger autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingLedgerAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingLedgerAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> accountingLedgerAuto(String filter) {
    return FinLookupView.accountingLedgerAuto(filter, getAccountingTransaction().getCompanyId().getId());
  }

  /**
   * AccountingDocType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingDocTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingDocTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingDocType> accountingDocTypeAuto(String filter) {
    return ScmLookupView.accountingDocTypeAuto(filter);
  }

  public List<AccountingHead> accountingHeadAuto(String filter) {
    return ScmLookupView.accountingHeadAuto(filter);
  }
}
