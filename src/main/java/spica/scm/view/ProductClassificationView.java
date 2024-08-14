/*
 * @(#)ProductClassificationView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ProductClassification;
import spica.scm.service.ProductClassificationService;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Status;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * ProductClassificationView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "productClassificationView")
@ViewScoped
public class ProductClassificationView implements Serializable {

  private transient ProductClassification productClassification;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductClassification> productClassificationLazyModel; 	//For lazy loading datatable.
  private transient ProductClassification[] productClassificationSelected;	 //Selected Domain Array

  @PostConstruct
  public void init() {
    Integer productclassificationId = (Integer) Jsf.popupParentValue(Integer.class);
    getProductClassification().setId(productclassificationId);
  }

  /**
   * Default Constructor.
   */
  public ProductClassificationView() {
    super();
  }

  /**
   * Return ProductClassification.
   *
   * @return ProductClassification.
   */
  public ProductClassification getProductClassification() {
    if (productClassification == null) {
      productClassification = new ProductClassification();
    }
    return productClassification;
  }

  /**
   * Set ProductClassification.
   *
   * @param productClassification.
   */
  public void setProductClassification(ProductClassification productClassification) {
    this.productClassification = productClassification;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductClassification(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProductClassification().reset();
          getProductClassification().setCompanyId(UserRuntimeView.instance().getCompany());
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProductClassification((ProductClassification) ProductClassificationService.selectByPk(main, getProductClassification()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductClassificationList(main);
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
   * Create productClassificationLazyModel.
   *
   * @param main
   */
  private void loadProductClassificationList(final MainView main) {
    if (productClassificationLazyModel == null) {
      productClassificationLazyModel = new LazyDataModel<ProductClassification>() {
        private List<ProductClassification> list;

        @Override
        public List<ProductClassification> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductClassificationService.listPaged(main, UserRuntimeView.instance().getCompany());
            main.commit(productClassificationLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductClassification productClassification) {
          return productClassification.getId();
        }

        @Override
        public ProductClassification getRowData(String rowKey) {
          if (list != null) {
            for (ProductClassification obj : list) {
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
    String SUB_FOLDER = "scm_product_classification/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductClassification(MainView main) {
    return saveOrCloneProductClassification(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductClassification(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductClassification(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductClassification(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductClassificationService.insertOrUpdate(main, getProductClassification());
            break;
          case "clone":
            ProductClassificationService.clone(main, getProductClassification());
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
   * Delete one or many ProductClassification.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductClassification(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productClassificationSelected)) {
        ProductClassificationService.deleteByPkArray(main, getProductClassificationSelected()); //many record delete from list
        main.commit("success.delete");
        productClassificationSelected = null;
      } else {
        ProductClassificationService.deleteByPk(main, getProductClassification());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductClassification.
   *
   * @return
   */
  public LazyDataModel<ProductClassification> getProductClassificationLazyModel() {
    return productClassificationLazyModel;
  }

  /**
   * Return ProductClassification[].
   *
   * @return
   */
  public ProductClassification[] getProductClassificationSelected() {
    return productClassificationSelected;
  }

  /**
   * Set ProductClassification[].
   *
   * @param productClassificationSelected
   */
  public void setProductClassificationSelected(ProductClassification[] productClassificationSelected) {
    this.productClassificationSelected = productClassificationSelected;
  }

  /**
   * Commodity autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.commodityAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.commodityAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ServiceCommodity> commodityAuto(String filter) {
    return ScmLookupView.commodityAuto(filter);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  public void productClassifiactionTypeClose() {
    Jsf.popupClose(productClassification);
  }
}
