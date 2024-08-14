/*
 * @(#)PurchaseReqItemView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.PurchaseReqItem;
import spica.scm.service.PurchaseReqItemService;
import spica.scm.domain.PurchaseReq;
import spica.scm.domain.Product;
import spica.scm.domain.ProductDetail;

/**
 * PurchaseReqItemView
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016 
 */

@Named(value="purchaseReqItemView")
@ViewScoped
public class PurchaseReqItemView implements Serializable{

  private transient PurchaseReqItem purchaseReqItem;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseReqItem> purchaseReqItemLazyModel; 	//For lazy loading datatable.
  private transient PurchaseReqItem[] purchaseReqItemSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public PurchaseReqItemView() {
    super();
  }
 
  /**
   * Return PurchaseReqItem.
   * @return PurchaseReqItem.
   */  
  public PurchaseReqItem getPurchaseReqItem() {
    if(purchaseReqItem == null) {
      purchaseReqItem = new PurchaseReqItem();
    }
    return purchaseReqItem;
  }   
  
  /**
   * Set PurchaseReqItem.
   * @param purchaseReqItem.
   */   
  public void setPurchaseReqItem(PurchaseReqItem purchaseReqItem) {
    this.purchaseReqItem = purchaseReqItem;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchPurchaseReqItem(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getPurchaseReqItem().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setPurchaseReqItem((PurchaseReqItem) PurchaseReqItemService.selectByPk(main, getPurchaseReqItem()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPurchaseReqItemList(main);
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
   * Create purchaseReqItemLazyModel.
   * @param main
   */
  private void loadPurchaseReqItemList(final MainView main) {
    if (purchaseReqItemLazyModel == null) {
      purchaseReqItemLazyModel = new LazyDataModel<PurchaseReqItem>() {
      private List<PurchaseReqItem> list;      
      @Override
      public List<PurchaseReqItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = PurchaseReqItemService.listPaged(main);
          main.commit(purchaseReqItemLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(PurchaseReqItem purchaseReqItem) {
        return purchaseReqItem.getId();
      }
      @Override
        public PurchaseReqItem getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReqItem obj : list) {
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
    String SUB_FOLDER = "scm_purchase_req_item/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String savePurchaseReqItem(MainView main) {
    return saveOrClonePurchaseReqItem(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseReqItem(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseReqItem(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseReqItem(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseReqItemService.insertOrUpdate(main, getPurchaseReqItem());
            break;
          case "clone":
            PurchaseReqItemService.clone(main, getPurchaseReqItem());
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
   * Delete one or many PurchaseReqItem.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseReqItem(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseReqItemSelected)) {
        PurchaseReqItemService.deleteByPkArray(main, getPurchaseReqItemSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseReqItemSelected = null;
      } else {
        PurchaseReqItemService.deleteByPk(main, getPurchaseReqItem());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PurchaseReqItem.
   * @return
   */
  public LazyDataModel<PurchaseReqItem> getPurchaseReqItemLazyModel() {
    return purchaseReqItemLazyModel;
  }

 /**
  * Return PurchaseReqItem[].
  * @return 
  */
  public PurchaseReqItem[] getPurchaseReqItemSelected() {
    return purchaseReqItemSelected;
  }
  
  /**
   * Set PurchaseReqItem[].
   * @param purchaseReqItemSelected 
   */
  public void setPurchaseReqItemSelected(PurchaseReqItem[] purchaseReqItemSelected) {
    this.purchaseReqItemSelected = purchaseReqItemSelected;
  }
 


 /**
  * PurchaseReq autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.purchaseReqAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.purchaseReqAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<PurchaseReq> purchaseReqAuto(String filter) {
    return ScmLookupView.purchaseReqAuto(filter);
  }
 /**
  * Product autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.productAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.productAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<Product> productAuto(String filter) {
    return ScmLookupView.productAuto(filter);
  }
 /**
  * ProductDetail autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.productDetailAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.productDetailAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<ProductDetail> productDetailAuto(String filter) {
    return ScmLookupView.productDetailAuto(filter);
  }
}
