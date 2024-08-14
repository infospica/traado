/*
 * @(#)PurchaseReturnStatusView.java	1.0 Wed May 25 13:23:25 IST 2016 
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

import spica.scm.domain.PurchaseReturnStatus;
import spica.scm.service.PurchaseReturnStatusService;

/**
 * PurchaseReturnStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:25 IST 2016
 */
@Named(value = "purchaseReturnStatusView")
@ViewScoped
public class PurchaseReturnStatusView implements Serializable {

  private transient PurchaseReturnStatus purchaseReturnStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseReturnStatus> purchaseReturnStatusLazyModel; 	//For lazy loading datatable.
  private transient PurchaseReturnStatus[] purchaseReturnStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PurchaseReturnStatusView() {
    super();
  }

  /**
   * Return PurchaseReturnStatus.
   *
   * @return PurchaseReturnStatus.
   */
  public PurchaseReturnStatus getPurchaseReturnStatus() {
    if (purchaseReturnStatus == null) {
      purchaseReturnStatus = new PurchaseReturnStatus();
    }
    return purchaseReturnStatus;
  }

  /**
   * Set PurchaseReturnStatus.
   *
   * @param purchaseReturnStatus.
   */
  public void setPurchaseReturnStatus(PurchaseReturnStatus purchaseReturnStatus) {
    this.purchaseReturnStatus = purchaseReturnStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPurchaseReturnStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getPurchaseReturnStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setPurchaseReturnStatus((PurchaseReturnStatus) PurchaseReturnStatusService.selectByPk(main, getPurchaseReturnStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPurchaseReturnStatusList(main);
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
   * Create purchaseReturnStatusLazyModel.
   *
   * @param main
   */
  private void loadPurchaseReturnStatusList(final MainView main) {
    if (purchaseReturnStatusLazyModel == null) {
      purchaseReturnStatusLazyModel = new LazyDataModel<PurchaseReturnStatus>() {
        private List<PurchaseReturnStatus> list;

        @Override
        public List<PurchaseReturnStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PurchaseReturnStatusService.listPaged(main);
            main.commit(purchaseReturnStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PurchaseReturnStatus purchaseReturnStatus) {
          return purchaseReturnStatus.getId();
        }

        @Override
        public PurchaseReturnStatus getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReturnStatus obj : list) {
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
    String SUB_FOLDER = "scm_purchase_return_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePurchaseReturnStatus(MainView main) {
    return saveOrClonePurchaseReturnStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseReturnStatus(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseReturnStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseReturnStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseReturnStatusService.insertOrUpdate(main, getPurchaseReturnStatus());
            break;
          case "clone":
            PurchaseReturnStatusService.clone(main, getPurchaseReturnStatus());
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
   * Delete one or many PurchaseReturnStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseReturnStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseReturnStatusSelected)) {
        PurchaseReturnStatusService.deleteByPkArray(main, getPurchaseReturnStatusSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseReturnStatusSelected = null;
      } else {
        PurchaseReturnStatusService.deleteByPk(main, getPurchaseReturnStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PurchaseReturnStatus.
   *
   * @return
   */
  public LazyDataModel<PurchaseReturnStatus> getPurchaseReturnStatusLazyModel() {
    return purchaseReturnStatusLazyModel;
  }

  /**
   * Return PurchaseReturnStatus[].
   *
   * @return
   */
  public PurchaseReturnStatus[] getPurchaseReturnStatusSelected() {
    return purchaseReturnStatusSelected;
  }

  /**
   * Set PurchaseReturnStatus[].
   *
   * @param purchaseReturnStatusSelected
   */
  public void setPurchaseReturnStatusSelected(PurchaseReturnStatus[] purchaseReturnStatusSelected) {
    this.purchaseReturnStatusSelected = purchaseReturnStatusSelected;
  }

}
