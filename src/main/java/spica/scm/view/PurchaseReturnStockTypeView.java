/*
 * @(#)PurchaseReturnStockTypeView.java	1.0 Wed May 25 13:23:25 IST 2016 
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

import spica.scm.domain.PurchaseReturnStockType;
import spica.scm.service.PurchaseReturnStockTypeService;

/**
 * PurchaseReturnStockTypeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:25 IST 2016
 */
@Named(value = "purchaseReturnStockTypeView")
@ViewScoped
public class PurchaseReturnStockTypeView implements Serializable {

  private transient PurchaseReturnStockType purchaseReturnStockType;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseReturnStockType> purchaseReturnStockTypeLazyModel; 	//For lazy loading datatable.
  private transient PurchaseReturnStockType[] purchaseReturnStockTypeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PurchaseReturnStockTypeView() {
    super();
  }

  /**
   * Return PurchaseReturnStockType.
   *
   * @return PurchaseReturnStockType.
   */
  public PurchaseReturnStockType getPurchaseReturnStockType() {
    if (purchaseReturnStockType == null) {
      purchaseReturnStockType = new PurchaseReturnStockType();
    }
    return purchaseReturnStockType;
  }

  /**
   * Set PurchaseReturnStockType.
   *
   * @param purchaseReturnStockType.
   */
  public void setPurchaseReturnStockType(PurchaseReturnStockType purchaseReturnStockType) {
    this.purchaseReturnStockType = purchaseReturnStockType;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPurchaseReturnStockType(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getPurchaseReturnStockType().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setPurchaseReturnStockType((PurchaseReturnStockType) PurchaseReturnStockTypeService.selectByPk(main, getPurchaseReturnStockType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPurchaseReturnStockTypeList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create purchaseReturnStockTypeLazyModel.
   *
   * @param main
   */
  private void loadPurchaseReturnStockTypeList(final MainView main) {
    if (purchaseReturnStockTypeLazyModel == null) {
      purchaseReturnStockTypeLazyModel = new LazyDataModel<PurchaseReturnStockType>() {
        private List<PurchaseReturnStockType> list;

        @Override
        public List<PurchaseReturnStockType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PurchaseReturnStockTypeService.listPaged(main);
            main.commit(purchaseReturnStockTypeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PurchaseReturnStockType purchaseReturnStockType) {
          return purchaseReturnStockType.getId();
        }

        @Override
        public PurchaseReturnStockType getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReturnStockType obj : list) {
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
    String SUB_FOLDER = "scm_purchase_return_stock_type/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePurchaseReturnStockType(MainView main) {
    return saveOrClonePurchaseReturnStockType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseReturnStockType(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseReturnStockType(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseReturnStockType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseReturnStockTypeService.insertOrUpdate(main, getPurchaseReturnStockType());
            break;
          case "clone":
            PurchaseReturnStockTypeService.clone(main, getPurchaseReturnStockType());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many PurchaseReturnStockType.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseReturnStockType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseReturnStockTypeSelected)) {
        PurchaseReturnStockTypeService.deleteByPkArray(main, getPurchaseReturnStockTypeSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseReturnStockTypeSelected = null;
      } else {
        PurchaseReturnStockTypeService.deleteByPk(main, getPurchaseReturnStockType());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())) {
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
   * Return LazyDataModel of PurchaseReturnStockType.
   *
   * @return
   */
  public LazyDataModel<PurchaseReturnStockType> getPurchaseReturnStockTypeLazyModel() {
    return purchaseReturnStockTypeLazyModel;
  }

  /**
   * Return PurchaseReturnStockType[].
   *
   * @return
   */
  public PurchaseReturnStockType[] getPurchaseReturnStockTypeSelected() {
    return purchaseReturnStockTypeSelected;
  }

  /**
   * Set PurchaseReturnStockType[].
   *
   * @param purchaseReturnStockTypeSelected
   */
  public void setPurchaseReturnStockTypeSelected(PurchaseReturnStockType[] purchaseReturnStockTypeSelected) {
    this.purchaseReturnStockTypeSelected = purchaseReturnStockTypeSelected;
  }

}
