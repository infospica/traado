/*
 * @(#)PurchaseReceiptPlanView.java	1.0 Mon Apr 11 14:41:12 IST 2016 
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

import spica.scm.domain.PurchaseReceiptPlan;
import spica.scm.service.PurchaseReceiptPlanService;
import spica.scm.domain.PurchaseOrder;
import spica.scm.domain.Product;

/**
 * PurchaseReceiptPlanView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Apr 11 14:41:12 IST 2016
 */
@Named(value = "purchaseReceiptPlanView")
@ViewScoped
public class PurchaseReceiptPlanView implements Serializable {

  private transient PurchaseReceiptPlan purchaseReceiptPlan;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseReceiptPlan> purchaseReceiptPlanLazyModel; 	//For lazy loading datatable.
  private transient PurchaseReceiptPlan[] purchaseReceiptPlanSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PurchaseReceiptPlanView() {
    super();
  }

  /**
   * Return PurchaseReceiptPlan.
   *
   * @return PurchaseReceiptPlan.
   */
  public PurchaseReceiptPlan getPurchaseReceiptPlan() {
    if (purchaseReceiptPlan == null) {
      purchaseReceiptPlan = new PurchaseReceiptPlan();
    }
    return purchaseReceiptPlan;
  }

  /**
   * Set PurchaseReceiptPlan.
   *
   * @param purchaseReceiptPlan.
   */
  public void setPurchaseReceiptPlan(PurchaseReceiptPlan purchaseReceiptPlan) {
    this.purchaseReceiptPlan = purchaseReceiptPlan;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPurchaseReceiptPlan(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getPurchaseReceiptPlan().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setPurchaseReceiptPlan((PurchaseReceiptPlan) PurchaseReceiptPlanService.selectByPk(main, getPurchaseReceiptPlan()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPurchaseReceiptPlanList(main);
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
   * Create purchaseReceiptPlanLazyModel.
   *
   * @param main
   */
  private void loadPurchaseReceiptPlanList(final MainView main) {
    if (purchaseReceiptPlanLazyModel == null) {
      purchaseReceiptPlanLazyModel = new LazyDataModel<PurchaseReceiptPlan>() {
        private List<PurchaseReceiptPlan> list;

        @Override
        public List<PurchaseReceiptPlan> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PurchaseReceiptPlanService.listPaged(main);
            main.commit(purchaseReceiptPlanLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PurchaseReceiptPlan purchaseReceiptPlan) {
          return purchaseReceiptPlan.getId();
        }

        @Override
        public PurchaseReceiptPlan getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReceiptPlan obj : list) {
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
    String SUB_FOLDER = "scm_purchase_receipt_plan/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePurchaseReceiptPlan(MainView main) {
    return saveOrClonePurchaseReceiptPlan(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseReceiptPlan(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseReceiptPlan(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseReceiptPlan(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseReceiptPlanService.insertOrUpdate(main, getPurchaseReceiptPlan());
            break;
          case "clone":
            PurchaseReceiptPlanService.clone(main, getPurchaseReceiptPlan());
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
   * Delete one or many PurchaseReceiptPlan.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseReceiptPlan(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseReceiptPlanSelected)) {
        PurchaseReceiptPlanService.deleteByPkArray(main, getPurchaseReceiptPlanSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseReceiptPlanSelected = null;
      } else {
        PurchaseReceiptPlanService.deleteByPk(main, getPurchaseReceiptPlan());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PurchaseReceiptPlan.
   *
   * @return
   */
  public LazyDataModel<PurchaseReceiptPlan> getPurchaseReceiptPlanLazyModel() {
    return purchaseReceiptPlanLazyModel;
  }

  /**
   * Return PurchaseReceiptPlan[].
   *
   * @return
   */
  public PurchaseReceiptPlan[] getPurchaseReceiptPlanSelected() {
    return purchaseReceiptPlanSelected;
  }

  /**
   * Set PurchaseReceiptPlan[].
   *
   * @param purchaseReceiptPlanSelected
   */
  public void setPurchaseReceiptPlanSelected(PurchaseReceiptPlan[] purchaseReceiptPlanSelected) {
    this.purchaseReceiptPlanSelected = purchaseReceiptPlanSelected;
  }

  /**
   * PurchaseOrder autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.purchaseOrderAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.purchaseOrderAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<PurchaseOrder> purchaseOrderAuto(String filter) {
    return ScmLookupView.purchaseOrderAuto(filter);
  }

  /**
   * Product autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Product> productAuto(String filter) {
    return ScmLookupView.productAuto(filter);
  }
}
