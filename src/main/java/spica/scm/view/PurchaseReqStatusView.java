/*
 * @(#)PurchaseReqStatusView.java	1.0 Mon Apr 11 14:41:12 IST 2016 
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

import spica.scm.domain.PurchaseReqStatus;
import spica.scm.service.PurchaseReqStatusService;

/**
 * PurchaseReqStatusView
 * @author	Spirit 1.2
 * @version	1.0, Mon Apr 11 14:41:12 IST 2016 
 */

@Named(value="purchaseReqStatusView")
@ViewScoped
public class PurchaseReqStatusView implements Serializable{

  private transient PurchaseReqStatus purchaseReqStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseReqStatus> purchaseReqStatusLazyModel; 	//For lazy loading datatable.
  private transient PurchaseReqStatus[] purchaseReqStatusSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public PurchaseReqStatusView() {
    super();
  }
 
  /**
   * Return PurchaseReqStatus.
   * @return PurchaseReqStatus.
   */  
  public PurchaseReqStatus getPurchaseReqStatus() {
    if(purchaseReqStatus == null) {
      purchaseReqStatus = new PurchaseReqStatus();
    }
    return purchaseReqStatus;
  }   
  
  /**
   * Set PurchaseReqStatus.
   * @param purchaseReqStatus.
   */   
  public void setPurchaseReqStatus(PurchaseReqStatus purchaseReqStatus) {
    this.purchaseReqStatus = purchaseReqStatus;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchPurchaseReqStatus(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getPurchaseReqStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setPurchaseReqStatus((PurchaseReqStatus) PurchaseReqStatusService.selectByPk(main, getPurchaseReqStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPurchaseReqStatusList(main);
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
   * Create purchaseReqStatusLazyModel.
   * @param main
   */
  private void loadPurchaseReqStatusList(final MainView main) {
    if (purchaseReqStatusLazyModel == null) {
      purchaseReqStatusLazyModel = new LazyDataModel<PurchaseReqStatus>() {
      private List<PurchaseReqStatus> list;      
      @Override
      public List<PurchaseReqStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = PurchaseReqStatusService.listPaged(main);
          main.commit(purchaseReqStatusLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(PurchaseReqStatus purchaseReqStatus) {
        return purchaseReqStatus.getId();
      }
      @Override
        public PurchaseReqStatus getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReqStatus obj : list) {
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
    String SUB_FOLDER = "scm_purchase_req_status/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String savePurchaseReqStatus(MainView main) {
    return saveOrClonePurchaseReqStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseReqStatus(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseReqStatus(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseReqStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseReqStatusService.insertOrUpdate(main, getPurchaseReqStatus());
            break;
          case "clone":
            PurchaseReqStatusService.clone(main, getPurchaseReqStatus());
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
   * Delete one or many PurchaseReqStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseReqStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseReqStatusSelected)) {
        PurchaseReqStatusService.deleteByPkArray(main, getPurchaseReqStatusSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseReqStatusSelected = null;
      } else {
        PurchaseReqStatusService.deleteByPk(main, getPurchaseReqStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PurchaseReqStatus.
   * @return
   */
  public LazyDataModel<PurchaseReqStatus> getPurchaseReqStatusLazyModel() {
    return purchaseReqStatusLazyModel;
  }

 /**
  * Return PurchaseReqStatus[].
  * @return 
  */
  public PurchaseReqStatus[] getPurchaseReqStatusSelected() {
    return purchaseReqStatusSelected;
  }
  
  /**
   * Set PurchaseReqStatus[].
   * @param purchaseReqStatusSelected 
   */
  public void setPurchaseReqStatusSelected(PurchaseReqStatus[] purchaseReqStatusSelected) {
    this.purchaseReqStatusSelected = purchaseReqStatusSelected;
  }
 


}
