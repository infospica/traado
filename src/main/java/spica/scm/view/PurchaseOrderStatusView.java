/*
 * @(#)PurchaseOrderStatusView.java	1.0 Mon Apr 11 14:41:12 IST 2016 
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

import spica.scm.domain.PurchaseOrderStatus;
import spica.scm.service.PurchaseOrderStatusService;

/**
 * PurchaseOrderStatusView
 * @author	Spirit 1.2
 * @version	1.0, Mon Apr 11 14:41:12 IST 2016 
 */

@Named(value="purchaseOrderStatusView")
@ViewScoped
public class PurchaseOrderStatusView implements Serializable{

  private transient PurchaseOrderStatus purchaseOrderStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseOrderStatus> purchaseOrderStatusLazyModel; 	//For lazy loading datatable.
  private transient PurchaseOrderStatus[] purchaseOrderStatusSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public PurchaseOrderStatusView() {
    super();
  }
 
  /**
   * Return PurchaseOrderStatus.
   * @return PurchaseOrderStatus.
   */  
  public PurchaseOrderStatus getPurchaseOrderStatus() {
    if(purchaseOrderStatus == null) {
      purchaseOrderStatus = new PurchaseOrderStatus();
    }
    return purchaseOrderStatus;
  }   
  
  /**
   * Set PurchaseOrderStatus.
   * @param purchaseOrderStatus.
   */   
  public void setPurchaseOrderStatus(PurchaseOrderStatus purchaseOrderStatus) {
    this.purchaseOrderStatus = purchaseOrderStatus;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchPurchaseOrderStatus(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getPurchaseOrderStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setPurchaseOrderStatus((PurchaseOrderStatus) PurchaseOrderStatusService.selectByPk(main, getPurchaseOrderStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPurchaseOrderStatusList(main);
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
   * Create purchaseOrderStatusLazyModel.
   * @param main
   */
  private void loadPurchaseOrderStatusList(final MainView main) {
    if (purchaseOrderStatusLazyModel == null) {
      purchaseOrderStatusLazyModel = new LazyDataModel<PurchaseOrderStatus>() {
      private List<PurchaseOrderStatus> list;      
      @Override
      public List<PurchaseOrderStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = PurchaseOrderStatusService.listPaged(main);
          main.commit(purchaseOrderStatusLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(PurchaseOrderStatus purchaseOrderStatus) {
        return purchaseOrderStatus.getId();
      }
      @Override
        public PurchaseOrderStatus getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseOrderStatus obj : list) {
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
    String SUB_FOLDER = "scm_purchase_order_status/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String savePurchaseOrderStatus(MainView main) {
    return saveOrClonePurchaseOrderStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseOrderStatus(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseOrderStatus(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseOrderStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseOrderStatusService.insertOrUpdate(main, getPurchaseOrderStatus());
            break;
          case "clone":
            PurchaseOrderStatusService.clone(main, getPurchaseOrderStatus());
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
   * Delete one or many PurchaseOrderStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseOrderStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseOrderStatusSelected)) {
        PurchaseOrderStatusService.deleteByPkArray(main, getPurchaseOrderStatusSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseOrderStatusSelected = null;
      } else {
        PurchaseOrderStatusService.deleteByPk(main, getPurchaseOrderStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PurchaseOrderStatus.
   * @return
   */
  public LazyDataModel<PurchaseOrderStatus> getPurchaseOrderStatusLazyModel() {
    return purchaseOrderStatusLazyModel;
  }

 /**
  * Return PurchaseOrderStatus[].
  * @return 
  */
  public PurchaseOrderStatus[] getPurchaseOrderStatusSelected() {
    return purchaseOrderStatusSelected;
  }
  
  /**
   * Set PurchaseOrderStatus[].
   * @param purchaseOrderStatusSelected 
   */
  public void setPurchaseOrderStatusSelected(PurchaseOrderStatus[] purchaseOrderStatusSelected) {
    this.purchaseOrderStatusSelected = purchaseOrderStatusSelected;
  }
 


}
