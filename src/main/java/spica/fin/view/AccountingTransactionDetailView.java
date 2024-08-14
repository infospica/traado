/*
 * @(#)AccountingTransactionDetailView.java	1.0 Fri Apr 28 12:24:49 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.service.AccountingTransactionDetailService;
import spica.fin.domain.AccountingDocType;
import spica.scm.view.ScmLookupView;

/**
 * AccountingTransactionDetailView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 28 12:24:49 IST 2017
 */
@Named(value = "accountingTransactionDetailView")
@ViewScoped
public class AccountingTransactionDetailView implements Serializable {

  private transient AccountingTransactionDetail accountingTransactionDetail;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountingTransactionDetail> accountingTransactionDetailLazyModel; 	//For lazy loading datatable.
  private transient AccountingTransactionDetail[] accountingTransactionDetailSelected;	 //Selected Domain Array
  private transient Part referenceDocumentCopyPathPart;

  /**
   * Default Constructor.
   */
  public AccountingTransactionDetailView() {
    super();
  }

  /**
   * Return AccountingTransactionDetail.
   *
   * @return AccountingTransactionDetail.
   */
  public AccountingTransactionDetail getAccountingTransactionDetail() {
    if (accountingTransactionDetail == null) {
      accountingTransactionDetail = new AccountingTransactionDetail();
    }
    return accountingTransactionDetail;
  }

  /**
   * Set AccountingTransactionDetail.
   *
   * @param accountingTransactionDetail.
   */
  public void setAccountingTransactionDetail(AccountingTransactionDetail accountingTransactionDetail) {
    this.accountingTransactionDetail = accountingTransactionDetail;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountingTransactionDetail(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getAccountingTransactionDetail().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setAccountingTransactionDetail((AccountingTransactionDetail) AccountingTransactionDetailService.selectByPk(main, getAccountingTransactionDetail()));
        } else if (ViewTypes.isList(viewType)) {
          loadAccountingTransactionDetailList(main);
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
   * Create accountingTransactionDetailLazyModel.
   *
   * @param main
   */
  private void loadAccountingTransactionDetailList(final MainView main) {
    if (accountingTransactionDetailLazyModel == null) {
      accountingTransactionDetailLazyModel = new LazyDataModel<AccountingTransactionDetail>() {
        private List<AccountingTransactionDetail> list;

        @Override
        public List<AccountingTransactionDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountingTransactionDetailService.listPaged(main);
            main.commit(accountingTransactionDetailLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountingTransactionDetail accountingTransactionDetail) {
          return accountingTransactionDetail.getId();
        }

        @Override
        public AccountingTransactionDetail getRowData(String rowKey) {
          if (list != null) {
            for (AccountingTransactionDetail obj : list) {
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

  private void uploadFiles() throws IOException {
    String SUB_FOLDER = "fin_accounting_transaction_detail/";
    if (referenceDocumentCopyPathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(referenceDocumentCopyPathPart, getAccountingTransactionDetail().getReferenceDocumentCopyPath(), SUB_FOLDER);
      getAccountingTransactionDetail().setReferenceDocumentCopyPath(JsfIo.getDbPath(referenceDocumentCopyPathPart, SUB_FOLDER));
      referenceDocumentCopyPathPart = null;	//import to set as null.
    }
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountingTransactionDetail(MainView main) {
    return saveOrCloneAccountingTransactionDetail(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountingTransactionDetail(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountingTransactionDetail(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountingTransactionDetail(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountingTransactionDetailService.insertOrUpdate(main, getAccountingTransactionDetail());
            break;
          case "clone":
            AccountingTransactionDetailService.clone(main, getAccountingTransactionDetail());
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
   * Delete one or many AccountingTransactionDetail.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingTransactionDetail(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(accountingTransactionDetailSelected)) {
        AccountingTransactionDetailService.deleteByPkArray(main, getAccountingTransactionDetailSelected()); //many record delete from list
        main.commit("success.delete");
        accountingTransactionDetailSelected = null;
      } else {
        AccountingTransactionDetailService.deleteByPk(main, getAccountingTransactionDetail());  //individual record delete from list or edit form
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
   * Return LazyDataModel of AccountingTransactionDetail.
   *
   * @return
   */
  public LazyDataModel<AccountingTransactionDetail> getAccountingTransactionDetailLazyModel() {
    return accountingTransactionDetailLazyModel;
  }

  /**
   * Return AccountingTransactionDetail[].
   *
   * @return
   */
  public AccountingTransactionDetail[] getAccountingTransactionDetailSelected() {
    return accountingTransactionDetailSelected;
  }

  /**
   * Set AccountingTransactionDetail[].
   *
   * @param accountingTransactionDetailSelected
   */
  public void setAccountingTransactionDetailSelected(AccountingTransactionDetail[] accountingTransactionDetailSelected) {
    this.accountingTransactionDetailSelected = accountingTransactionDetailSelected;
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getReferenceDocumentCopyPathPart() {
    return referenceDocumentCopyPathPart;
  }

  /**
   * Set Part referenceDocumentCopyPathPart.
   *
   * @param referenceDocumentCopyPathPart.
   */
  public void setReferenceDocumentCopyPathPart(Part referenceDocumentCopyPathPart) {
    if (this.referenceDocumentCopyPathPart == null || referenceDocumentCopyPathPart != null) {
      this.referenceDocumentCopyPathPart = referenceDocumentCopyPathPart;
    }
  }

  /**
   * AccountingTransaction autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingTransactionAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingTransactionAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
//  public List<AccountingTransaction> accountingTransactionAuto(String filter) {
//    return ScmLookupView.accountingTransactionAuto(filter);
//  }
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
//  public List<AccountingLedger> accountingLedgerAuto(String filter) {
//    return ScmLookupView.accountingLedgerAuto(filter);
//  }
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
}
