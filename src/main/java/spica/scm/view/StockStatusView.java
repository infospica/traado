/*
 * @(#)StockStatusView.java	1.0 Mon May 09 18:27:32 IST 2016 
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

import spica.scm.domain.StockStatus;
import spica.scm.service.StockStatusService;

/**
 * StockStatusView
 * @author	Spirit 1.2
 * @version	1.0, Mon May 09 18:27:32 IST 2016 
 */

@Named(value="stockStatusView")
@ViewScoped
public class StockStatusView implements Serializable{

  private transient StockStatus stockStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<StockStatus> stockStatusLazyModel; 	//For lazy loading datatable.
  private transient StockStatus[] stockStatusSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public StockStatusView() {
    super();
  }
 
  /**
   * Return StockStatus.
   * @return StockStatus.
   */  
  public StockStatus getStockStatus() {
    if(stockStatus == null) {
      stockStatus = new StockStatus();
    }
    return stockStatus;
  }   
  
  /**
   * Set StockStatus.
   * @param stockStatus.
   */   
  public void setStockStatus(StockStatus stockStatus) {
    this.stockStatus = stockStatus;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchStockStatus(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getStockStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setStockStatus((StockStatus) StockStatusService.selectByPk(main, getStockStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadStockStatusList(main);
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
   * Create stockStatusLazyModel.
   * @param main
   */
  private void loadStockStatusList(final MainView main) {
    if (stockStatusLazyModel == null) {
      stockStatusLazyModel = new LazyDataModel<StockStatus>() {
      private List<StockStatus> list;      
      @Override
      public List<StockStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = StockStatusService.listPaged(main);
          main.commit(stockStatusLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(StockStatus stockStatus) {
        return stockStatus.getId();
      }
      @Override
        public StockStatus getRowData(String rowKey) {
          if (list != null) {
            for (StockStatus obj : list) {
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
    String SUB_FOLDER = "scm_stock_status/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveStockStatus(MainView main) {
    return saveOrCloneStockStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneStockStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneStockStatus(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneStockStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            StockStatusService.insertOrUpdate(main, getStockStatus());
            break;
          case "clone":
            StockStatusService.clone(main, getStockStatus());
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
   * Delete one or many StockStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteStockStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(stockStatusSelected)) {
        StockStatusService.deleteByPkArray(main, getStockStatusSelected()); //many record delete from list
        main.commit("success.delete");
        stockStatusSelected = null;
      } else {
        StockStatusService.deleteByPk(main, getStockStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of StockStatus.
   * @return
   */
  public LazyDataModel<StockStatus> getStockStatusLazyModel() {
    return stockStatusLazyModel;
  }

 /**
  * Return StockStatus[].
  * @return 
  */
  public StockStatus[] getStockStatusSelected() {
    return stockStatusSelected;
  }
  
  /**
   * Set StockStatus[].
   * @param stockStatusSelected 
   */
  public void setStockStatusSelected(StockStatus[] stockStatusSelected) {
    this.stockStatusSelected = stockStatusSelected;
  }
 


}
