/*
 * @(#)ProductDetailView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductDetailStatus;
import spica.scm.service.ProductDetailService;
import wawo.app.faces.Jsf;

/**
 * ProductDetailView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "productDetailView")
@ViewScoped
public class ProductDetailView implements Serializable {

  private transient ProductDetail productDetail;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductDetail> productDetailLazyModel; 	//For lazy loading datatable.
  private transient ProductDetail[] productDetailSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ProductDetailView() {
    super();
  }

  /**
   * Return ProductDetail.
   *
   * @return ProductDetail.
   */
  public ProductDetail getProductDetail() {
    if (productDetail == null) {
      productDetail = new ProductDetail();
    }
    return productDetail;
  }

  /**
   * Set ProductDetail.
   *
   * @param productDetail.
   */
  public void setProductDetail(ProductDetail productDetail) {
    this.productDetail = productDetail;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductDetail(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getProductDetail().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setProductDetail((ProductDetail) ProductDetailService.selectByPk(main, getProductDetail()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductDetailList(main);
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
   * Create productDetailLazyModel.
   *
   * @param main
   */
  private void loadProductDetailList(final MainView main) {
    if (productDetailLazyModel == null) {
      productDetailLazyModel = new LazyDataModel<ProductDetail>() {
        private List<ProductDetail> list;

        @Override
        public List<ProductDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductDetailService.listPaged(main);
            main.commit(productDetailLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductDetail productDetail) {
          return productDetail.getId();
        }

        @Override
        public ProductDetail getRowData(String rowKey) {
          if (list != null) {
            for (ProductDetail obj : list) {
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
    String SUB_FOLDER = "scm_product_detail/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductDetail(MainView main) {
    return saveOrCloneProductDetail(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductDetail(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductDetail(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductDetail(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductDetailService.insertOrUpdate(main, getProductDetail());
            break;
          case "clone":
            ProductDetailService.clone(main, getProductDetail());
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
   * Delete one or many ProductDetail.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductDetail(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productDetailSelected)) {
        ProductDetailService.deleteByPkArray(main, getProductDetailSelected()); //many record delete from list
        main.commit("success.delete");
        productDetailSelected = null;
      } else {
        ProductDetailService.deleteByPk(main, getProductDetail());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductDetail.
   *
   * @return
   */
  public LazyDataModel<ProductDetail> getProductDetailLazyModel() {
    return productDetailLazyModel;
  }

  /**
   * Return ProductDetail[].
   *
   * @return
   */
  public ProductDetail[] getProductDetailSelected() {
    return productDetailSelected;
  }

  /**
   * Set ProductDetail[].
   *
   * @param productDetailSelected
   */
  public void setProductDetailSelected(ProductDetail[] productDetailSelected) {
    this.productDetailSelected = productDetailSelected;
  }

  /**
   * ProdDetailStatus autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.prodDetailStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.prodDetailStatusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductDetailStatus> prodDetailStatusAuto(String filter) {
    return ScmLookupView.prodDetailStatusAuto(filter);
  }

  public void onRowEdit(RowEditEvent event) {
    MainView main = Jsf.getMain();
    try {
      productDetail = (ProductDetail) event.getObject();
      ProductDetailService.insertOrUpdate(main, productDetail);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }
}
