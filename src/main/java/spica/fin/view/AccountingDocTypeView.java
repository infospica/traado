/*
 * @(#)AccountingDocTypeView.java	1.0 Fri Feb 24 15:58:33 IST 2017 
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.AccountingDocType;
import spica.fin.domain.AccountingVoucherType;
import spica.fin.service.AccountingDocTypeService;
import spica.scm.view.ScmLookupView;

/**
 * AccountingDocTypeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:33 IST 2017
 */
@Named(value = "accountingDocTypeView")
@ViewScoped
public class AccountingDocTypeView implements Serializable {

  private transient AccountingDocType accountingDocType;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountingDocType> accountingDocTypeLazyModel; 	//For lazy loading datatable.
  private transient AccountingDocType[] accountingDocTypeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public AccountingDocTypeView() {
    super();
  }

  /**
   * Return AccountingDocType.
   *
   * @return AccountingDocType.
   */
  public AccountingDocType getAccountingDocType() {
    if (accountingDocType == null) {
      accountingDocType = new AccountingDocType();
    }
    return accountingDocType;
  }

  /**
   * Set AccountingDocType.
   *
   * @param accountingDocType.
   */
  public void setAccountingDocType(AccountingDocType accountingDocType) {
    this.accountingDocType = accountingDocType;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountingDocType(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getAccountingDocType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setAccountingDocType((AccountingDocType) AccountingDocTypeService.selectByPk(main, getAccountingDocType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadAccountingDocTypeList(main);
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
   * Create accountingDocTypeLazyModel.
   *
   * @param main
   */
  private void loadAccountingDocTypeList(final MainView main) {
    if (accountingDocTypeLazyModel == null) {
      accountingDocTypeLazyModel = new LazyDataModel<AccountingDocType>() {
        private List<AccountingDocType> list;

        @Override
        public List<AccountingDocType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountingDocTypeService.listPaged(main);
            main.commit(accountingDocTypeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountingDocType accountingDocType) {
          return accountingDocType.getId();
        }

        @Override
        public AccountingDocType getRowData(String rowKey) {
          if (list != null) {
            for (AccountingDocType obj : list) {
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
    String SUB_FOLDER = "fin_accounting_doc_type/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountingDocType(MainView main) {
    return saveOrCloneAccountingDocType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountingDocType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountingDocType(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountingDocType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountingDocTypeService.insertOrUpdate(main, getAccountingDocType());
            break;
          case "clone":
            AccountingDocTypeService.clone(main, getAccountingDocType());
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
   * Delete one or many AccountingDocType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingDocType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(accountingDocTypeSelected)) {
        AccountingDocTypeService.deleteByPkArray(main, getAccountingDocTypeSelected()); //many record delete from list
        main.commit("success.delete");
        accountingDocTypeSelected = null;
      } else {
        AccountingDocTypeService.deleteByPk(main, getAccountingDocType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of AccountingDocType.
   *
   * @return
   */
  public LazyDataModel<AccountingDocType> getAccountingDocTypeLazyModel() {
    return accountingDocTypeLazyModel;
  }

  /**
   * Return AccountingDocType[].
   *
   * @return
   */
  public AccountingDocType[] getAccountingDocTypeSelected() {
    return accountingDocTypeSelected;
  }

  /**
   * Set AccountingDocType[].
   *
   * @param accountingDocTypeSelected
   */
  public void setAccountingDocTypeSelected(AccountingDocType[] accountingDocTypeSelected) {
    this.accountingDocTypeSelected = accountingDocTypeSelected;
  }

  /**
   * AccountingVoucherType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingVoucherTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingVoucherTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingVoucherType> accountingVoucherTypeAuto(String filter) {
    return ScmLookupView.accountingVoucherTypeAuto(filter);
  }

}
