/*
 * @(#)AccountStatusView.java	1.0 Thu Apr 07 11:31:23 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.scm.view;

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

import spica.scm.domain.AccountStatus;
import spica.scm.service.AccountStatusService;

/**
 * AccountStatusView
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:23 IST 2016 
 */

@Named(value="accountStatusView")
@ViewScoped
public class AccountStatusView implements Serializable{

  private transient AccountStatus accountStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountStatus> accountStatusLazyModel; 	//For lazy loading datatable.
  private transient AccountStatus[] accountStatusSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public AccountStatusView() {
    super();
  }
 
  /**
   * Return AccountStatus.
   * @return AccountStatus.
   */  
  public AccountStatus getAccountStatus() {
    if(accountStatus == null) {
      accountStatus = new AccountStatus();
    }
    return accountStatus;
  }   
  
  /**
   * Set AccountStatus.
   * @param accountStatus.
   */   
  public void setAccountStatus(AccountStatus accountStatus) {
    this.accountStatus = accountStatus;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchAccountStatus(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getAccountStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setAccountStatus((AccountStatus) AccountStatusService.selectByPk(main, getAccountStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadAccountStatusList(main);
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
   * Create accountStatusLazyModel.
   * @param main
   */
  private void loadAccountStatusList(final MainView main) {
    if (accountStatusLazyModel == null) {
      accountStatusLazyModel = new LazyDataModel<AccountStatus>() {
      private List<AccountStatus> list;      
      @Override
      public List<AccountStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = AccountStatusService.listPaged(main);
          main.commit(accountStatusLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(AccountStatus accountStatus) {
        return accountStatus.getId();
      }
      @Override
        public AccountStatus getRowData(String rowKey) {
          if (list != null) {
            for (AccountStatus obj : list) {
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
    String SUB_FOLDER = "scm_account_status/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveAccountStatus(MainView main) {
    return saveOrCloneAccountStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountStatus(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountStatusService.insertOrUpdate(main, getAccountStatus());
            break;
          case "clone":
            AccountStatusService.clone(main, getAccountStatus());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error"+ key);
    } finally {
      main.close();
    }
    return null;
  }

  
  /**
   * Delete one or many AccountStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(accountStatusSelected)) {
        AccountStatusService.deleteByPkArray(main, getAccountStatusSelected()); //many record delete from list
        main.commit("success.delete");
        accountStatusSelected = null;
      } else {
        AccountStatusService.deleteByPk(main, getAccountStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of AccountStatus.
   * @return
   */
  public LazyDataModel<AccountStatus> getAccountStatusLazyModel() {
    return accountStatusLazyModel;
  }

 /**
  * Return AccountStatus[].
  * @return 
  */
  public AccountStatus[] getAccountStatusSelected() {
    return accountStatusSelected;
  }
  
  /**
   * Set AccountStatus[].
   * @param accountStatusSelected 
   */
  public void setAccountStatusSelected(AccountStatus[] accountStatusSelected) {
    this.accountStatusSelected = accountStatusSelected;
  }
 


}
