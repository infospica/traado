/*
 * @(#)AccountingEntityTypeView.java	1.0 Fri Feb 24 15:58:33 IST 2017 
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

import spica.fin.domain.AccountingEntityType;
import spica.fin.service.AccountingEntityTypeService;

/**
 * AccountingEntityTypeView
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:33 IST 2017 
 */

@Named(value="accountingEntityTypeView")
@ViewScoped
public class AccountingEntityTypeView implements Serializable{

  private transient AccountingEntityType accountingEntityType;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountingEntityType> accountingEntityTypeLazyModel; 	//For lazy loading datatable.
  private transient AccountingEntityType[] accountingEntityTypeSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public AccountingEntityTypeView() {
    super();
  }
 
  /**
   * Return AccountingEntityType.
   * @return AccountingEntityType.
   */  
  public AccountingEntityType getAccountingEntityType() {
    if(accountingEntityType == null) {
      accountingEntityType = new AccountingEntityType();
    }
    return accountingEntityType;
  }   
  
  /**
   * Set AccountingEntityType.
   * @param accountingEntityType.
   */   
  public void setAccountingEntityType(AccountingEntityType accountingEntityType) {
    this.accountingEntityType = accountingEntityType;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchAccountingEntityType(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getAccountingEntityType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setAccountingEntityType((AccountingEntityType) AccountingEntityTypeService.selectByPk(main, getAccountingEntityType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadAccountingEntityTypeList(main);
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
   * Create accountingEntityTypeLazyModel.
   * @param main
   */
  private void loadAccountingEntityTypeList(final MainView main) {
    if (accountingEntityTypeLazyModel == null) {
      accountingEntityTypeLazyModel = new LazyDataModel<AccountingEntityType>() {
      private List<AccountingEntityType> list;      
      @Override
      public List<AccountingEntityType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = AccountingEntityTypeService.listPaged(main);
          main.commit(accountingEntityTypeLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(AccountingEntityType accountingEntityType) {
        return accountingEntityType.getId();
      }
      @Override
        public AccountingEntityType getRowData(String rowKey) {
          if (list != null) {
            for (AccountingEntityType obj : list) {
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
    String SUB_FOLDER = "fin_accounting_entity_type/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveAccountingEntityType(MainView main) {
    return saveOrCloneAccountingEntityType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountingEntityType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountingEntityType(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountingEntityType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountingEntityTypeService.insertOrUpdate(main, getAccountingEntityType());
            break;
          case "clone":
            AccountingEntityTypeService.clone(main, getAccountingEntityType());
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
   * Delete one or many AccountingEntityType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingEntityType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(accountingEntityTypeSelected)) {
        AccountingEntityTypeService.deleteByPkArray(main, getAccountingEntityTypeSelected()); //many record delete from list
        main.commit("success.delete");
        accountingEntityTypeSelected = null;
      } else {
        AccountingEntityTypeService.deleteByPk(main, getAccountingEntityType());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())){
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
   * Return LazyDataModel of AccountingEntityType.
   * @return
   */
  public LazyDataModel<AccountingEntityType> getAccountingEntityTypeLazyModel() {
    return accountingEntityTypeLazyModel;
  }

 /**
  * Return AccountingEntityType[].
  * @return 
  */
  public AccountingEntityType[] getAccountingEntityTypeSelected() {
    return accountingEntityTypeSelected;
  }
  
  /**
   * Set AccountingEntityType[].
   * @param accountingEntityTypeSelected 
   */
  public void setAccountingEntityTypeSelected(AccountingEntityType[] accountingEntityTypeSelected) {
    this.accountingEntityTypeSelected = accountingEntityTypeSelected;
  }
 


}
