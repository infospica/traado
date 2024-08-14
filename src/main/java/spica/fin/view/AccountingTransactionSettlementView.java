/*
 * @(#)AccountingTransactionSettlementView.java	1.0 Wed Aug 23 18:04:40 IST 2017 
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
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypeAction;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.AccountingTransactionSettlement;
import spica.fin.service.AccountingTransactionSettlementService;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.scm.view.ScmLookupView;

/**
 * AccountingTransactionSettlementView
 * @author	Spirit 1.2
 * @version	1.0, Wed Aug 23 18:04:40 IST 2017 
 */

@Named(value="accountingTransactionSettlementView")
@ViewScoped
public class AccountingTransactionSettlementView implements Serializable{

  private transient AccountingTransactionSettlement accountingTransactionSettlement;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountingTransactionSettlement> accountingTransactionSettlementLazyModel; 	//For lazy loading datatable.
  private transient AccountingTransactionSettlement[] accountingTransactionSettlementSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public AccountingTransactionSettlementView() {
    super();
  }
 
  /**
   * Return AccountingTransactionSettlement.
   * @return AccountingTransactionSettlement.
   */  
  public AccountingTransactionSettlement getAccountingTransactionSettlement() {
    if(accountingTransactionSettlement == null) {
      accountingTransactionSettlement = new AccountingTransactionSettlement();
    }
    return accountingTransactionSettlement;
  }   
  
  /**
   * Set AccountingTransactionSettlement.
   * @param accountingTransactionSettlement.
   */   
  public void setAccountingTransactionSettlement(AccountingTransactionSettlement accountingTransactionSettlement) {
    this.accountingTransactionSettlement = accountingTransactionSettlement;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchAccountingTransactionSettlement(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getAccountingTransactionSettlement().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setAccountingTransactionSettlement((AccountingTransactionSettlement) AccountingTransactionSettlementService.selectByPk(main, getAccountingTransactionSettlement()));
        } else if (main.isList()) {
          loadAccountingTransactionSettlementList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally{
        main.close();
      }
    }
    return null;
  } 
  
  /**
   * Create accountingTransactionSettlementLazyModel.
   * @param main
   */
  private void loadAccountingTransactionSettlementList(final MainView main) {
    if (accountingTransactionSettlementLazyModel == null) {
      accountingTransactionSettlementLazyModel = new LazyDataModel<AccountingTransactionSettlement>() {
      private List<AccountingTransactionSettlement> list;      
      @Override
      public List<AccountingTransactionSettlement> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = AccountingTransactionSettlementService.listPaged(main);
          main.commit(accountingTransactionSettlementLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(AccountingTransactionSettlement accountingTransactionSettlement) {
        return accountingTransactionSettlement.getId();
      }
      @Override
        public AccountingTransactionSettlement getRowData(String rowKey) {
          if (list != null) {
            for (AccountingTransactionSettlement obj : list) {
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
    String SUB_FOLDER = "fin_accounting_transaction_settlement/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveAccountingTransactionSettlement(MainView main) {
    return saveOrCloneAccountingTransactionSettlement(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountingTransactionSettlement(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneAccountingTransactionSettlement(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountingTransactionSettlement(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountingTransactionSettlementService.insertOrUpdate(main, getAccountingTransactionSettlement());
            break;
          case "clone":
            AccountingTransactionSettlementService.clone(main, getAccountingTransactionSettlement());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error."+ key);
    } finally {
      main.close();
    }
    return null;
  }

  
  /**
   * Delete one or many AccountingTransactionSettlement.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingTransactionSettlement(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(accountingTransactionSettlementSelected)) {
        AccountingTransactionSettlementService.deleteByPkArray(main, getAccountingTransactionSettlementSelected()); //many record delete from list
        main.commit("success.delete");
        accountingTransactionSettlementSelected = null;
      } else {
        AccountingTransactionSettlementService.deleteByPk(main, getAccountingTransactionSettlement());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()){
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
   * Return LazyDataModel of AccountingTransactionSettlement.
   * @return
   */
  public LazyDataModel<AccountingTransactionSettlement> getAccountingTransactionSettlementLazyModel() {
    return accountingTransactionSettlementLazyModel;
  }

 /**
  * Return AccountingTransactionSettlement[].
  * @return 
  */
  public AccountingTransactionSettlement[] getAccountingTransactionSettlementSelected() {
    return accountingTransactionSettlementSelected;
  }
  
  /**
   * Set AccountingTransactionSettlement[].
   * @param accountingTransactionSettlementSelected 
   */
  public void setAccountingTransactionSettlementSelected(AccountingTransactionSettlement[] accountingTransactionSettlementSelected) {
    this.accountingTransactionSettlementSelected = accountingTransactionSettlementSelected;
  }
 


 /**
  * AccountingTransactionDetailItem autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.accountingTransactionDetailItemAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.accountingTransactionDetailItemAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<AccountingTransactionDetailItem> accountingTransactionDetailItemAuto(String filter) {
    return ScmLookupView.accountingTransactionDetailItemAuto(filter);
  }
}
