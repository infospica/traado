/*
 * @(#)SalesAgentCommisionPayableView.java	1.0 Mon Jun 05 13:13:48 IST 2017 
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

import spica.scm.domain.SalesAgentCommisionPayable;
import spica.scm.service.SalesAgentCommisionPayableService;
import spica.scm.domain.SalesAgentCommisionClaim;
import spica.scm.domain.SalesAgentCommision;

/**
 * SalesAgentCommisionPayableView
 * @author	Spirit 1.2
 * @version	1.0, Mon Jun 05 13:13:48 IST 2017 
 */

@Named(value="scmSalesAgentCommisionPayableView")
@ViewScoped
public class SalesAgentCommisionPayableView implements Serializable{

  private transient SalesAgentCommisionPayable scmSalesAgentCommisionPayable;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentCommisionPayable> scmSalesAgentCommisionPayableLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentCommisionPayable[] scmSalesAgentCommisionPayableSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public SalesAgentCommisionPayableView() {
    super();
  }
 
  /**
   * Return SalesAgentCommisionPayable.
   * @return SalesAgentCommisionPayable.
   */  
  public SalesAgentCommisionPayable getSalesAgentCommisionPayable() {
    if(scmSalesAgentCommisionPayable == null) {
      scmSalesAgentCommisionPayable = new SalesAgentCommisionPayable();
    }
    return scmSalesAgentCommisionPayable;
  }   
  
  /**
   * Set SalesAgentCommisionPayable.
   * @param scmSalesAgentCommisionPayable.
   */   
  public void setSalesAgentCommisionPayable(SalesAgentCommisionPayable scmSalesAgentCommisionPayable) {
    this.scmSalesAgentCommisionPayable = scmSalesAgentCommisionPayable;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchSalesAgentCommisionPayable(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getSalesAgentCommisionPayable().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setSalesAgentCommisionPayable((SalesAgentCommisionPayable) SalesAgentCommisionPayableService.selectByPk(main, getSalesAgentCommisionPayable()));
        } else if (ViewTypes.isList(viewType)) {
          loadSalesAgentCommisionPayableList(main);
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
   * Create scmSalesAgentCommisionPayableLazyModel.
   * @param main
   */
  private void loadSalesAgentCommisionPayableList(final MainView main) {
    if (scmSalesAgentCommisionPayableLazyModel == null) {
      scmSalesAgentCommisionPayableLazyModel = new LazyDataModel<SalesAgentCommisionPayable>() {
      private List<SalesAgentCommisionPayable> list;      
      @Override
      public List<SalesAgentCommisionPayable> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = SalesAgentCommisionPayableService.listPaged(main);
          main.commit(scmSalesAgentCommisionPayableLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(SalesAgentCommisionPayable scmSalesAgentCommisionPayable) {
        return scmSalesAgentCommisionPayable.getId();
      }
      @Override
        public SalesAgentCommisionPayable getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentCommisionPayable obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_commision_payable/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentCommisionPayable(MainView main) {
    return saveOrCloneSalesAgentCommisionPayable(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentCommisionPayable(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesAgentCommisionPayable(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentCommisionPayable(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentCommisionPayableService.insertOrUpdate(main, getSalesAgentCommisionPayable());
            break;
          case "clone":
            SalesAgentCommisionPayableService.clone(main, getSalesAgentCommisionPayable());
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
   * Delete one or many SalesAgentCommisionPayable.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentCommisionPayable(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(scmSalesAgentCommisionPayableSelected)) {
        SalesAgentCommisionPayableService.deleteByPkArray(main, getSalesAgentCommisionPayableSelected()); //many record delete from list
        main.commit("success.delete");
        scmSalesAgentCommisionPayableSelected = null;
      } else {
        SalesAgentCommisionPayableService.deleteByPk(main, getSalesAgentCommisionPayable());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesAgentCommisionPayable.
   * @return
   */
  public LazyDataModel<SalesAgentCommisionPayable> getSalesAgentCommisionPayableLazyModel() {
    return scmSalesAgentCommisionPayableLazyModel;
  }

 /**
  * Return SalesAgentCommisionPayable[].
  * @return 
  */
  public SalesAgentCommisionPayable[] getSalesAgentCommisionPayableSelected() {
    return scmSalesAgentCommisionPayableSelected;
  }
  
  /**
   * Set SalesAgentCommisionPayable[].
   * @param scmSalesAgentCommisionPayableSelected 
   */
  public void setSalesAgentCommisionPayableSelected(SalesAgentCommisionPayable[] scmSalesAgentCommisionPayableSelected) {
    this.scmSalesAgentCommisionPayableSelected = scmSalesAgentCommisionPayableSelected;
  }
 


 /**
  * SalesAgentCommisionClaim autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.scmSalesAgentCommisionClaimAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.scmSalesAgentCommisionClaimAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<SalesAgentCommisionClaim> scmSalesAgentCommisionClaimAuto(String filter) {
    return ScmLookupView.scmSalesAgentCommisionClaimAuto(filter);
  }
 /**
  * SalesAgentCommision autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.scmSalesAgentCommisionAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.scmSalesAgentCommisionAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<SalesAgentCommision> scmSalesAgentCommisionAuto(String filter) {
    return ScmLookupView.scmSalesAgentCommisionAuto(filter);
  }
}
