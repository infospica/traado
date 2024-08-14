/*
 * @(#)ProductEntryStatusView.java	1.0 Thu Sep 08 18:33:15 IST 2016 
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

import spica.scm.domain.ProductEntryStatus;
import spica.scm.service.ProductEntryStatusService;

/**
 * ProductEntryStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Sep 08 18:33:15 IST 2016
 */
@Named(value = "productEntryStatusView")
@ViewScoped
public class ProductEntryStatusView implements Serializable {

  private transient ProductEntryStatus productEntryStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductEntryStatus> productEntryStatusLazyModel; 	//For lazy loading datatable.
  private transient ProductEntryStatus[] productEntryStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ProductEntryStatusView() {
    super();
  }

  /**
   * Return ProductEntryStatus.
   *
   * @return ProductEntryStatus.
   */
  public ProductEntryStatus getProductEntryStatus() {
    if (productEntryStatus == null) {
      productEntryStatus = new ProductEntryStatus();
    }
    return productEntryStatus;
  }

  /**
   * Set ProductEntryStatus.
   *
   * @param productEntryStatus.
   */
  public void setProductEntryStatus(ProductEntryStatus productEntryStatus) {
    this.productEntryStatus = productEntryStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductEntryStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getProductEntryStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setProductEntryStatus((ProductEntryStatus) ProductEntryStatusService.selectByPk(main, getProductEntryStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductEntryStatusList(main);
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
   * Create productEntryStatusLazyModel.
   *
   * @param main
   */
  private void loadProductEntryStatusList(final MainView main) {
    if (productEntryStatusLazyModel == null) {
      productEntryStatusLazyModel = new LazyDataModel<ProductEntryStatus>() {
        private List<ProductEntryStatus> list;

        @Override
        public List<ProductEntryStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductEntryStatusService.listPaged(main);
            main.commit(productEntryStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductEntryStatus productEntryStatus) {
          return productEntryStatus.getId();
        }

        @Override
        public ProductEntryStatus getRowData(String rowKey) {
          if (list != null) {
            for (ProductEntryStatus obj : list) {
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
    String SUB_FOLDER = "scm_product_entry_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductEntryStatus(MainView main) {
    return saveOrCloneProductEntryStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductEntryStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductEntryStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductEntryStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductEntryStatusService.insertOrUpdate(main, getProductEntryStatus());
            break;
          case "clone":
            ProductEntryStatusService.clone(main, getProductEntryStatus());
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
   * Delete one or many ProductEntryStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductEntryStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productEntryStatusSelected)) {
        ProductEntryStatusService.deleteByPkArray(main, getProductEntryStatusSelected()); //many record delete from list
        main.commit("success.delete");
        productEntryStatusSelected = null;
      } else {
        ProductEntryStatusService.deleteByPk(main, getProductEntryStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductEntryStatus.
   *
   * @return
   */
  public LazyDataModel<ProductEntryStatus> getProductEntryStatusLazyModel() {
    return productEntryStatusLazyModel;
  }

  /**
   * Return ProductEntryStatus[].
   *
   * @return
   */
  public ProductEntryStatus[] getProductEntryStatusSelected() {
    return productEntryStatusSelected;
  }

  /**
   * Set ProductEntryStatus[].
   *
   * @param productEntryStatusSelected
   */
  public void setProductEntryStatusSelected(ProductEntryStatus[] productEntryStatusSelected) {
    this.productEntryStatusSelected = productEntryStatusSelected;
  }

}
