/*
 * @(#)ProductStatusView.java	1.0 Tue May 10 17:16:17 IST 2016 
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

import spica.scm.domain.ProductStatus;
import spica.scm.service.ProductStatusService;

/**
 * ProductStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 10 17:16:17 IST 2016
 */
@Named(value = "productStatusView")
@ViewScoped
public class ProductStatusView implements Serializable {

  private transient ProductStatus productStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductStatus> productStatusLazyModel; 	//For lazy loading datatable.
  private transient ProductStatus[] productStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ProductStatusView() {
    super();
  }

  /**
   * Return ProductStatus.
   *
   * @return ProductStatus.
   */
  public ProductStatus getProductStatus() {
    if (productStatus == null) {
      productStatus = new ProductStatus();
    }
    return productStatus;
  }

  /**
   * Set ProductStatus.
   *
   * @param productStatus.
   */
  public void setProductStatus(ProductStatus productStatus) {
    this.productStatus = productStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProductStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProductStatus((ProductStatus) ProductStatusService.selectByPk(main, getProductStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductStatusList(main);
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
   * Create productStatusLazyModel.
   *
   * @param main
   */
  private void loadProductStatusList(final MainView main) {
    if (productStatusLazyModel == null) {
      productStatusLazyModel = new LazyDataModel<ProductStatus>() {
        private List<ProductStatus> list;

        @Override
        public List<ProductStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductStatusService.listPaged(main);
            main.commit(productStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductStatus productStatus) {
          return productStatus.getId();
        }

        @Override
        public ProductStatus getRowData(String rowKey) {
          if (list != null) {
            for (ProductStatus obj : list) {
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
    String SUB_FOLDER = "scm_product_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductStatus(MainView main) {
    return saveOrCloneProductStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductStatusService.insertOrUpdate(main, getProductStatus());
            break;
          case "clone":
            ProductStatusService.clone(main, getProductStatus());
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
   * Delete one or many ProductStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productStatusSelected)) {
        ProductStatusService.deleteByPkArray(main, getProductStatusSelected()); //many record delete from list
        main.commit("success.delete");
        productStatusSelected = null;
      } else {
        ProductStatusService.deleteByPk(main, getProductStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductStatus.
   *
   * @return
   */
  public LazyDataModel<ProductStatus> getProductStatusLazyModel() {
    return productStatusLazyModel;
  }

  /**
   * Return ProductStatus[].
   *
   * @return
   */
  public ProductStatus[] getProductStatusSelected() {
    return productStatusSelected;
  }

  /**
   * Set ProductStatus[].
   *
   * @param productStatusSelected
   */
  public void setProductStatusSelected(ProductStatus[] productStatusSelected) {
    this.productStatusSelected = productStatusSelected;
  }

}
