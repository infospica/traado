/*
 * @(#)PurchaseCreditOnView.java	1.0 Thu Apr 07 11:31:23 IST 2016 
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

import spica.scm.domain.PurchaseCreditOn;
import spica.scm.service.PurchaseCreditOnService;

/**
 * PurchaseCreditOnView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:23 IST 2016
 */
@Named(value = "purchaseCreditOnView")
@ViewScoped
public class PurchaseCreditOnView implements Serializable {

  private transient PurchaseCreditOn purchaseCreditOn;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseCreditOn> purchaseCreditOnLazyModel; 	//For lazy loading datatable.
  private transient PurchaseCreditOn[] purchaseCreditOnSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PurchaseCreditOnView() {
    super();
  }

  /**
   * Return PurchaseCreditOn.
   *
   * @return PurchaseCreditOn.
   */
  public PurchaseCreditOn getPurchaseCreditOn() {
    if (purchaseCreditOn == null) {
      purchaseCreditOn = new PurchaseCreditOn();
    }
    return purchaseCreditOn;
  }

  /**
   * Set PurchaseCreditOn.
   *
   * @param purchaseCreditOn.
   */
  public void setPurchaseCreditOn(PurchaseCreditOn purchaseCreditOn) {
    this.purchaseCreditOn = purchaseCreditOn;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPurchaseCreditOn(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getPurchaseCreditOn().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setPurchaseCreditOn((PurchaseCreditOn) PurchaseCreditOnService.selectByPk(main, getPurchaseCreditOn()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPurchaseCreditOnList(main);
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
   * Create purchaseCreditOnLazyModel.
   *
   * @param main
   */
  private void loadPurchaseCreditOnList(final MainView main) {
    if (purchaseCreditOnLazyModel == null) {
      purchaseCreditOnLazyModel = new LazyDataModel<PurchaseCreditOn>() {
        private List<PurchaseCreditOn> list;

        @Override
        public List<PurchaseCreditOn> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PurchaseCreditOnService.listPaged(main);
            main.commit(purchaseCreditOnLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PurchaseCreditOn purchaseCreditOn) {
          return purchaseCreditOn.getId();
        }

        @Override
        public PurchaseCreditOn getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseCreditOn obj : list) {
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
    String SUB_FOLDER = "scm_purchase_credit_on/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePurchaseCreditOn(MainView main) {
    return saveOrClonePurchaseCreditOn(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseCreditOn(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseCreditOn(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseCreditOn(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseCreditOnService.insertOrUpdate(main, getPurchaseCreditOn());
            break;
          case "clone":
            PurchaseCreditOnService.clone(main, getPurchaseCreditOn());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error" + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many PurchaseCreditOn.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseCreditOn(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseCreditOnSelected)) {
        PurchaseCreditOnService.deleteByPkArray(main, getPurchaseCreditOnSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseCreditOnSelected = null;
      } else {
        PurchaseCreditOnService.deleteByPk(main, getPurchaseCreditOn());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PurchaseCreditOn.
   *
   * @return
   */
  public LazyDataModel<PurchaseCreditOn> getPurchaseCreditOnLazyModel() {
    return purchaseCreditOnLazyModel;
  }

  /**
   * Return PurchaseCreditOn[].
   *
   * @return
   */
  public PurchaseCreditOn[] getPurchaseCreditOnSelected() {
    return purchaseCreditOnSelected;
  }

  /**
   * Set PurchaseCreditOn[].
   *
   * @param purchaseCreditOnSelected
   */
  public void setPurchaseCreditOnSelected(PurchaseCreditOn[] purchaseCreditOnSelected) {
    this.purchaseCreditOnSelected = purchaseCreditOnSelected;
  }

}
