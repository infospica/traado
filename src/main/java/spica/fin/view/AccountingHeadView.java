/*
 * @(#)AccountingHeadView.java	1.0 Fri Apr 28 12:24:49 IST 2017 
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

import spica.fin.domain.AccountingHead;
import spica.fin.service.AccountingHeadService;

/**
 * AccountingHeadView
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 28 12:24:49 IST 2017 
 */

@Named(value="accountingHeadView")
@ViewScoped
public class AccountingHeadView implements Serializable{

  private transient AccountingHead accountingHead;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountingHead> accountingHeadLazyModel; 	//For lazy loading datatable.
  private transient AccountingHead[] accountingHeadSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public AccountingHeadView() {
    super();
  }
 
  /**
   * Return AccountingHead.
   * @return AccountingHead.
   */  
  public AccountingHead getAccountingHead() {
    if(accountingHead == null) {
      accountingHead = new AccountingHead();
    }
    return accountingHead;
  }   
  
  /**
   * Set AccountingHead.
   * @param accountingHead.
   */   
  public void setAccountingHead(AccountingHead accountingHead) {
    this.accountingHead = accountingHead;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchAccountingHead(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getAccountingHead().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setAccountingHead((AccountingHead) AccountingHeadService.selectByPk(main, getAccountingHead()));
        } else if (ViewTypes.isList(viewType)) {
          loadAccountingHeadList(main);
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
   * Create accountingHeadLazyModel.
   * @param main
   */
  private void loadAccountingHeadList(final MainView main) {
    if (accountingHeadLazyModel == null) {
      accountingHeadLazyModel = new LazyDataModel<AccountingHead>() {
      private List<AccountingHead> list;      
      @Override
      public List<AccountingHead> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = AccountingHeadService.listPaged(main);
          main.commit(accountingHeadLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(AccountingHead accountingHead) {
        return accountingHead.getId();
      }
      @Override
        public AccountingHead getRowData(String rowKey) {
          if (list != null) {
            for (AccountingHead obj : list) {
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
    String SUB_FOLDER = "fin_accounting_head/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveAccountingHead(MainView main) {
    return saveOrCloneAccountingHead(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountingHead(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountingHead(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountingHead(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountingHeadService.insertOrUpdate(main, getAccountingHead());
            break;
          case "clone":
            AccountingHeadService.clone(main, getAccountingHead());
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
   * Delete one or many AccountingHead.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingHead(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(accountingHeadSelected)) {
        AccountingHeadService.deleteByPkArray(main, getAccountingHeadSelected()); //many record delete from list
        main.commit("success.delete");
        accountingHeadSelected = null;
      } else {
        AccountingHeadService.deleteByPk(main, getAccountingHead());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (ViewTypes.isEdit(main.getViewType())){
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
   * Return LazyDataModel of AccountingHead.
   * @return
   */
  public LazyDataModel<AccountingHead> getAccountingHeadLazyModel() {
    return accountingHeadLazyModel;
  }

 /**
  * Return AccountingHead[].
  * @return 
  */
  public AccountingHead[] getAccountingHeadSelected() {
    return accountingHeadSelected;
  }
  
  /**
   * Set AccountingHead[].
   * @param accountingHeadSelected 
   */
  public void setAccountingHeadSelected(AccountingHead[] accountingHeadSelected) {
    this.accountingHeadSelected = accountingHeadSelected;
  }
 


}
