/*
 * @(#)PurchaseReturnStockCatView.java	1.0 Wed May 25 13:23:25 IST 2016 
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

import spica.scm.domain.PurchaseReturnStockCat;
import spica.scm.service.PurchaseReturnStockCatService;

/**
 * PurchaseReturnStockCatView
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:25 IST 2016 
 */

@Named(value="purchaseReturnStockCatView")
@ViewScoped
public class PurchaseReturnStockCatView implements Serializable{

  private transient PurchaseReturnStockCat purchaseReturnStockCat;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseReturnStockCat> purchaseReturnStockCatLazyModel; 	//For lazy loading datatable.
  private transient PurchaseReturnStockCat[] purchaseReturnStockCatSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public PurchaseReturnStockCatView() {
    super();
  }
 
  /**
   * Return PurchaseReturnStockCat.
   * @return PurchaseReturnStockCat.
   */  
  public PurchaseReturnStockCat getPurchaseReturnStockCat() {
    if(purchaseReturnStockCat == null) {
      purchaseReturnStockCat = new PurchaseReturnStockCat();
    }
    return purchaseReturnStockCat;
  }   
  
  /**
   * Set PurchaseReturnStockCat.
   * @param purchaseReturnStockCat.
   */   
  public void setPurchaseReturnStockCat(PurchaseReturnStockCat purchaseReturnStockCat) {
    this.purchaseReturnStockCat = purchaseReturnStockCat;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchPurchaseReturnStockCat(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getPurchaseReturnStockCat().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setPurchaseReturnStockCat((PurchaseReturnStockCat) PurchaseReturnStockCatService.selectByPk(main, getPurchaseReturnStockCat()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPurchaseReturnStockCatList(main);
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
   * Create purchaseReturnStockCatLazyModel.
   * @param main
   */
  private void loadPurchaseReturnStockCatList(final MainView main) {
    if (purchaseReturnStockCatLazyModel == null) {
      purchaseReturnStockCatLazyModel = new LazyDataModel<PurchaseReturnStockCat>() {
      private List<PurchaseReturnStockCat> list;      
      @Override
      public List<PurchaseReturnStockCat> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = PurchaseReturnStockCatService.listPaged(main);
          main.commit(purchaseReturnStockCatLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(PurchaseReturnStockCat purchaseReturnStockCat) {
        return purchaseReturnStockCat.getId();
      }
      @Override
        public PurchaseReturnStockCat getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReturnStockCat obj : list) {
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
    String SUB_FOLDER = "scm_purchase_return_stock_cat/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String savePurchaseReturnStockCat(MainView main) {
    return saveOrClonePurchaseReturnStockCat(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseReturnStockCat(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseReturnStockCat(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseReturnStockCat(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseReturnStockCatService.insertOrUpdate(main, getPurchaseReturnStockCat());
            break;
          case "clone":
            PurchaseReturnStockCatService.clone(main, getPurchaseReturnStockCat());
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
   * Delete one or many PurchaseReturnStockCat.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseReturnStockCat(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseReturnStockCatSelected)) {
        PurchaseReturnStockCatService.deleteByPkArray(main, getPurchaseReturnStockCatSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseReturnStockCatSelected = null;
      } else {
        PurchaseReturnStockCatService.deleteByPk(main, getPurchaseReturnStockCat());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PurchaseReturnStockCat.
   * @return
   */
  public LazyDataModel<PurchaseReturnStockCat> getPurchaseReturnStockCatLazyModel() {
    return purchaseReturnStockCatLazyModel;
  }

 /**
  * Return PurchaseReturnStockCat[].
  * @return 
  */
  public PurchaseReturnStockCat[] getPurchaseReturnStockCatSelected() {
    return purchaseReturnStockCatSelected;
  }
  
  /**
   * Set PurchaseReturnStockCat[].
   * @param purchaseReturnStockCatSelected 
   */
  public void setPurchaseReturnStockCatSelected(PurchaseReturnStockCat[] purchaseReturnStockCatSelected) {
    this.purchaseReturnStockCatSelected = purchaseReturnStockCatSelected;
  }
 


}
