/*
 * @(#)SalesReturnStatusView.java	1.0 Mon Jan 29 16:45:18 IST 2018 
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesReturnStatus;
import spica.scm.service.SalesReturnStatusService;

/**
 * SalesReturnStatusView
 * @author	Spirit 1.2
 * @version	1.0, Mon Jan 29 16:45:18 IST 2018 
 */

@Named(value="salesReturnStatusView")
@ViewScoped
public class SalesReturnStatusView implements Serializable{

  private transient SalesReturnStatus salesReturnStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesReturnStatus> salesReturnStatusLazyModel; 	//For lazy loading datatable.
  private transient SalesReturnStatus[] salesReturnStatusSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public SalesReturnStatusView() {
    super();
  }
 
  /**
   * Return SalesReturnStatus.
   * @return SalesReturnStatus.
   */  
  public SalesReturnStatus getSalesReturnStatus() {
    if(salesReturnStatus == null) {
      salesReturnStatus = new SalesReturnStatus();
    }
    return salesReturnStatus;
  }   
  
  /**
   * Set SalesReturnStatus.
   * @param salesReturnStatus.
   */   
  public void setSalesReturnStatus(SalesReturnStatus salesReturnStatus) {
    this.salesReturnStatus = salesReturnStatus;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchSalesReturnStatus(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getSalesReturnStatus().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setSalesReturnStatus((SalesReturnStatus) SalesReturnStatusService.selectByPk(main, getSalesReturnStatus()));
        } else if (main.isList()) {
          loadSalesReturnStatusList(main);
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
   * Create salesReturnStatusLazyModel.
   * @param main
   */
  private void loadSalesReturnStatusList(final MainView main) {
    if (salesReturnStatusLazyModel == null) {
      salesReturnStatusLazyModel = new LazyDataModel<SalesReturnStatus>() {
      private List<SalesReturnStatus> list;      
      @Override
      public List<SalesReturnStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = SalesReturnStatusService.listPaged(main);
          main.commit(salesReturnStatusLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(SalesReturnStatus salesReturnStatus) {
        return salesReturnStatus.getId();
      }
      @Override
        public SalesReturnStatus getRowData(String rowKey) {
          if (list != null) {
            for (SalesReturnStatus obj : list) {
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
    String SUB_FOLDER = "scm_sales_return_status/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveSalesReturnStatus(MainView main) {
    return saveOrCloneSalesReturnStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesReturnStatus(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneSalesReturnStatus(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesReturnStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesReturnStatusService.insertOrUpdate(main, getSalesReturnStatus());
            break;
          case "clone":
            SalesReturnStatusService.clone(main, getSalesReturnStatus());
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
   * Delete one or many SalesReturnStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesReturnStatus(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(salesReturnStatusSelected)) {
        SalesReturnStatusService.deleteByPkArray(main, getSalesReturnStatusSelected()); //many record delete from list
        main.commit("success.delete");
        salesReturnStatusSelected = null;
      } else {
        SalesReturnStatusService.deleteByPk(main, getSalesReturnStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesReturnStatus.
   * @return
   */
  public LazyDataModel<SalesReturnStatus> getSalesReturnStatusLazyModel() {
    return salesReturnStatusLazyModel;
  }

 /**
  * Return SalesReturnStatus[].
  * @return 
  */
  public SalesReturnStatus[] getSalesReturnStatusSelected() {
    return salesReturnStatusSelected;
  }
  
  /**
   * Set SalesReturnStatus[].
   * @param salesReturnStatusSelected 
   */
  public void setSalesReturnStatusSelected(SalesReturnStatus[] salesReturnStatusSelected) {
    this.salesReturnStatusSelected = salesReturnStatusSelected;
  }
 


}
