/*
 * @(#)PurchaseReturnItemstatusView.java	1.0 Wed May 25 13:23:25 IST 2016 
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

import spica.scm.domain.PurchaseReturnItemStatus;
import spica.scm.service.PurchaseReturnItemstatusService;

/**
 * PurchaseReturnItemstatusView
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:25 IST 2016 
 */

@Named(value="purchaseReturnItemstatusView")
@ViewScoped
public class PurchaseReturnItemstatusView implements Serializable{

  private transient PurchaseReturnItemStatus purchaseReturnItemstatus;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseReturnItemStatus> purchaseReturnItemstatusLazyModel; 	//For lazy loading datatable.
  private transient PurchaseReturnItemStatus[] purchaseReturnItemstatusSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public PurchaseReturnItemstatusView() {
    super();
  }
 
  /**
   * Return PurchaseReturnItemStatus.
   * @return PurchaseReturnItemStatus.
   */  
  public PurchaseReturnItemStatus getPurchaseReturnItemstatus() {
    if(purchaseReturnItemstatus == null) {
      purchaseReturnItemstatus = new PurchaseReturnItemStatus();
    }
    return purchaseReturnItemstatus;
  }   
  
  /**
   * Set PurchaseReturnItemStatus.
   * @param purchaseReturnItemstatus.
   */   
  public void setPurchaseReturnItemstatus(PurchaseReturnItemStatus purchaseReturnItemstatus) {
    this.purchaseReturnItemstatus = purchaseReturnItemstatus;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchPurchaseReturnItemstatus(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getPurchaseReturnItemstatus().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setPurchaseReturnItemstatus((PurchaseReturnItemStatus) PurchaseReturnItemstatusService.selectByPk(main, getPurchaseReturnItemstatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPurchaseReturnItemstatusList(main);
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
   * Create purchaseReturnItemstatusLazyModel.
   * @param main
   */
  private void loadPurchaseReturnItemstatusList(final MainView main) {
    if (purchaseReturnItemstatusLazyModel == null) {
      purchaseReturnItemstatusLazyModel = new LazyDataModel<PurchaseReturnItemStatus>() {
      private List<PurchaseReturnItemStatus> list;      
      @Override
      public List<PurchaseReturnItemStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = PurchaseReturnItemstatusService.listPaged(main);
          main.commit(purchaseReturnItemstatusLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(PurchaseReturnItemStatus purchaseReturnItemstatus) {
        return purchaseReturnItemstatus.getId();
      }
      @Override
        public PurchaseReturnItemStatus getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReturnItemStatus obj : list) {
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
    String SUB_FOLDER = "scm_purchase_return_itemstatus/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String savePurchaseReturnItemstatus(MainView main) {
    return saveOrClonePurchaseReturnItemstatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseReturnItemstatus(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseReturnItemstatus(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseReturnItemstatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseReturnItemstatusService.insertOrUpdate(main, getPurchaseReturnItemstatus());
            break;
          case "clone":
            PurchaseReturnItemstatusService.clone(main, getPurchaseReturnItemstatus());
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
   * Delete one or many PurchaseReturnItemStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseReturnItemstatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseReturnItemstatusSelected)) {
        PurchaseReturnItemstatusService.deleteByPkArray(main, getPurchaseReturnItemstatusSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseReturnItemstatusSelected = null;
      } else {
        PurchaseReturnItemstatusService.deleteByPk(main, getPurchaseReturnItemstatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PurchaseReturnItemStatus.
   * @return
   */
  public LazyDataModel<PurchaseReturnItemStatus> getPurchaseReturnItemstatusLazyModel() {
    return purchaseReturnItemstatusLazyModel;
  }

 /**
  * Return PurchaseReturnItemStatus[].
  * @return 
  */
  public PurchaseReturnItemStatus[] getPurchaseReturnItemstatusSelected() {
    return purchaseReturnItemstatusSelected;
  }
  
  /**
   * Set PurchaseReturnItemStatus[].
   * @param purchaseReturnItemstatusSelected 
   */
  public void setPurchaseReturnItemstatusSelected(PurchaseReturnItemStatus[] purchaseReturnItemstatusSelected) {
    this.purchaseReturnItemstatusSelected = purchaseReturnItemstatusSelected;
  }
 


}
