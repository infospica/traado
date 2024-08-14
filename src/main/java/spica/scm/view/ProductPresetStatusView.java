/*
 * @(#)ProductPresetStatusView.java	1.0 Tue Sep 20 15:27:07 IST 2016 
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

import spica.scm.domain.ProductPresetStatus;
import spica.scm.service.ProductPresetStatusService;

/**
 * ProductPresetStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Sep 20 15:27:07 IST 2016
 */
@Named(value = "productPresetStatusView")
@ViewScoped
public class ProductPresetStatusView implements Serializable {

  private transient ProductPresetStatus productPresetStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductPresetStatus> productPresetStatusLazyModel; 	//For lazy loading datatable.
  private transient ProductPresetStatus[] productPresetStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ProductPresetStatusView() {
    super();
  }

  /**
   * Return ProductPresetStatus.
   *
   * @return ProductPresetStatus.
   */
  public ProductPresetStatus getProductPresetStatus() {
    if (productPresetStatus == null) {
      productPresetStatus = new ProductPresetStatus();
    }
    return productPresetStatus;
  }

  /**
   * Set ProductPresetStatus.
   *
   * @param productPresetStatus.
   */
  public void setProductPresetStatus(ProductPresetStatus productPresetStatus) {
    this.productPresetStatus = productPresetStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductPresetStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getProductPresetStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setProductPresetStatus((ProductPresetStatus) ProductPresetStatusService.selectByPk(main, getProductPresetStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductPresetStatusList(main);
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
   * Create productPresetStatusLazyModel.
   *
   * @param main
   */
  private void loadProductPresetStatusList(final MainView main) {
    if (productPresetStatusLazyModel == null) {
      productPresetStatusLazyModel = new LazyDataModel<ProductPresetStatus>() {
        private List<ProductPresetStatus> list;

        @Override
        public List<ProductPresetStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductPresetStatusService.listPaged(main);
            main.commit(productPresetStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductPresetStatus productPresetStatus) {
          return productPresetStatus.getId();
        }

        @Override
        public ProductPresetStatus getRowData(String rowKey) {
          if (list != null) {
            for (ProductPresetStatus obj : list) {
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
    String SUB_FOLDER = "scm_product_preset_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductPresetStatus(MainView main) {
    return saveOrCloneProductPresetStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductPresetStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductPresetStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductPresetStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductPresetStatusService.insertOrUpdate(main, getProductPresetStatus());
            break;
          case "clone":
            ProductPresetStatusService.clone(main, getProductPresetStatus());
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
   * Delete one or many ProductPresetStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductPresetStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productPresetStatusSelected)) {
        ProductPresetStatusService.deleteByPkArray(main, getProductPresetStatusSelected()); //many record delete from list
        main.commit("success.delete");
        productPresetStatusSelected = null;
      } else {
        ProductPresetStatusService.deleteByPk(main, getProductPresetStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductPresetStatus.
   *
   * @return
   */
  public LazyDataModel<ProductPresetStatus> getProductPresetStatusLazyModel() {
    return productPresetStatusLazyModel;
  }

  /**
   * Return ProductPresetStatus[].
   *
   * @return
   */
  public ProductPresetStatus[] getProductPresetStatusSelected() {
    return productPresetStatusSelected;
  }

  /**
   * Set ProductPresetStatus[].
   *
   * @param productPresetStatusSelected
   */
  public void setProductPresetStatusSelected(ProductPresetStatus[] productPresetStatusSelected) {
    this.productPresetStatusSelected = productPresetStatusSelected;
  }

}
