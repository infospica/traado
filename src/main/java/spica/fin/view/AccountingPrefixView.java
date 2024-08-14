/*
 * @(#)FinAccountingPrefixView.java	1.0 Thu May 11 17:20:19 IST 2017 
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

import spica.fin.domain.AccountingPrefix;
import spica.fin.domain.AccountingVoucherType;
import spica.scm.domain.Status;
import spica.fin.service.AccountingPrefixService;
import spica.scm.view.ScmLookupView;
import spica.sys.UserRuntimeView;


/**
 * FinAccountingPrefixView
 * @author	Spirit 1.2
 * @version	1.0, Thu May 11 17:20:19 IST 2017 
 */

@Named(value="finAccountingPrefixView")
@ViewScoped
public class AccountingPrefixView implements Serializable{

  private transient AccountingPrefix finAccountingPrefix;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountingPrefix> finAccountingPrefixLazyModel; 	//For lazy loading datatable.
  private transient AccountingPrefix[] finAccountingPrefixSelected;	 //Selected Domain Array
  private transient boolean yearSequence;
  /**
   * Default Constructor.
   */   
  public AccountingPrefixView() {
    super();
  }
 
  /**
   * Return FinAccountingPrefix.
   * @return FinAccountingPrefix.
   */  
  public AccountingPrefix getFinAccountingPrefix() {
    if(finAccountingPrefix == null) {
      finAccountingPrefix = new AccountingPrefix();
    }
    if (finAccountingPrefix.getCompanyId() == null) {
      finAccountingPrefix.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return finAccountingPrefix;
  }   
  
  /**
   * Set FinAccountingPrefix.
   * @param finAccountingPrefix.
   */   
  public void setFinAccountingPrefix(AccountingPrefix finAccountingPrefix) {
    this.finAccountingPrefix = finAccountingPrefix;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchFinAccountingPrefix(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getFinAccountingPrefix().reset();
          getFinAccountingPrefix().setYearSequence(0);
          getFinAccountingPrefix().setYearPadding(2);
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setFinAccountingPrefix((AccountingPrefix) AccountingPrefixService.selectByPk(main, getFinAccountingPrefix()));
        } else if (ViewTypes.isList(viewType)) {
          getFinAccountingPrefix().setCompanyId(null);
          loadFinAccountingPrefixList(main);
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
   * Create finAccountingPrefixLazyModel.
   * @param main
   */
  private void loadFinAccountingPrefixList(final MainView main) {
    if (finAccountingPrefixLazyModel == null) {
      finAccountingPrefixLazyModel = new LazyDataModel<AccountingPrefix>() {
      private List<AccountingPrefix> list;      
      @Override
      public List<AccountingPrefix> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = AccountingPrefixService.listPaged(main, getFinAccountingPrefix().getCompanyId());
          main.commit(finAccountingPrefixLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(AccountingPrefix finAccountingPrefix) {
        return finAccountingPrefix.getId();
      }
      @Override
        public AccountingPrefix getRowData(String rowKey) {
          if (list != null) {
            for (AccountingPrefix obj : list) {
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
    String SUB_FOLDER = "fin_accounting_prefix/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveFinAccountingPrefix(MainView main) {
    return saveOrCloneFinAccountingPrefix(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneFinAccountingPrefix(MainView main) {
    main.setViewType("newform");
    return saveOrCloneFinAccountingPrefix(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneFinAccountingPrefix(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountingPrefixService.insertOrUpdate(main, finAccountingPrefix);
            break;
          case "clone":
            AccountingPrefixService.clone(main, getFinAccountingPrefix());
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
   * Delete one or many FinAccountingPrefix.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteFinAccountingPrefix(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(finAccountingPrefixSelected)) {
        AccountingPrefixService.deleteByPkArray(main, getFinAccountingPrefixSelected()); //many record delete from list
        main.commit("success.delete");
        finAccountingPrefixSelected = null;
      } else {
        AccountingPrefixService.deleteByPk(main, getFinAccountingPrefix());  //individual record delete from list or edit form
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
   * Return LazyDataModel of FinAccountingPrefix.
   * @return
   */
  public LazyDataModel<AccountingPrefix> getFinAccountingPrefixLazyModel() {
    return finAccountingPrefixLazyModel;
  }

 /**
  * Return FinAccountingPrefix[].
  * @return 
  */
  public AccountingPrefix[] getFinAccountingPrefixSelected() {
    return finAccountingPrefixSelected;
  }
  
  /**
   * Set FinAccountingPrefix[].
   * @param finAccountingPrefixSelected 
   */
  public void setFinAccountingPrefixSelected(AccountingPrefix[] finAccountingPrefixSelected) {
    this.finAccountingPrefixSelected = finAccountingPrefixSelected;
  }

  public boolean isYearSequence() {
    return yearSequence;
  }

  public void setYearSequence(boolean yearSequence) {
    this.yearSequence = yearSequence;
  }
 


 /**
  * FinPrefixType autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.finPrefixTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.finPrefixTypeAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<AccountingVoucherType> finVoucherTypeAuto(String filter) {
    return ScmLookupView.finVoucherTypeAuto(filter);
  }
  
   public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
  
  public void yearSequenceChangeHandler() {
    if (getFinAccountingPrefix().getYearSequence() == 0) {
      setYearSequence(false);
    } else {
      setYearSequence(true);
    }
  }
}
