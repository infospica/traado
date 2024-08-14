/*
 * @(#)ProductTypeView.java	1.0 Wed Mar 30 12:35:25 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Product;
import spica.scm.domain.ProductType;
import spica.scm.domain.Status;
import spica.scm.service.ProductTypeService;
import spica.sys.SystemConstants;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * ProductTypeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "productTypeView")
@ViewScoped
public class ProductTypeView implements Serializable {

  private transient ProductType productType;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductType> productTypeLazyModel; 	//For lazy loading datatable.
  private transient ProductType[] productTypeSelected;	 //Selected Domain Array  
  private transient SelectItem selectItemProductType;

  private transient Product product;

  @PostConstruct
  public void init() {
    product = (Product) Jsf.popupParentValue(Product.class);
    if (product != null) {
      setProductType(product.getProductTypeId());
    }
  }

  /**
   * Default Constructor.
   */
  public ProductTypeView() {
    super();
  }

  /**
   * Return ProductType.
   *
   * @return ProductType.
   */
  public ProductType getProductType() {
    if (productType == null) {
      productType = new ProductType();
    }
    return productType;
  }

  /**
   * Set ProductType.
   *
   * @param productType.
   */
  public void setProductType(ProductType productType) {
    this.productType = productType;
  }

  public SelectItem getSelectItemProductType() {
    if (selectItemProductType == null) {
      selectItemProductType = new SelectItem();
    }
    return selectItemProductType;
  }

  public void setSelectItemProductType(SelectItem selectItemProductType) {
    this.selectItemProductType = selectItemProductType;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductType(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProductType().reset();
          setSelectItemProductType(null);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProductType((ProductType) ProductTypeService.selectByPk(main, getProductType()));
          getSelectItemProductType().setItemLabel(getProductType().getTitle());
          getSelectItemProductType().setTextValue(getProductType().getProductTypeCode());
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductTypeList(main);
          setSelectItemProductType(null);
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
   * Create productTypeLazyModel.
   *
   * @param main
   */
  private void loadProductTypeList(final MainView main) {
    if (productTypeLazyModel == null) {
      productTypeLazyModel = new LazyDataModel<ProductType>() {
        private List<ProductType> list;

        @Override
        public List<ProductType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductTypeService.listPaged(main);
            main.commit(productTypeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductType productType) {
          return productType.getId();
        }

        @Override
        public ProductType getRowData(String rowKey) {
          if (list != null) {
            for (ProductType obj : list) {
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
    String SUB_FOLDER = "scm_product_type/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductType(MainView main) {
    return saveOrCloneProductType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductType(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            if (getSelectItemProductType() != null) {
              getProductType().setTitle(getSelectItemProductType().getItemLabel());
              getProductType().setProductTypeCode(getSelectItemProductType().getTextValue());
            }
            ProductTypeService.insertOrUpdate(main, getProductType());
            break;
          case "clone":
            ProductTypeService.clone(main, getProductType());
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
   * Delete one or many ProductType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productTypeSelected)) {
        ProductTypeService.deleteByPkArray(main, getProductTypeSelected()); //many record delete from list
        main.commit("success.delete");
        productTypeSelected = null;
      } else {
        ProductTypeService.deleteByPk(main, getProductType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductType.
   *
   * @return
   */
  public LazyDataModel<ProductType> getProductTypeLazyModel() {
    return productTypeLazyModel;
  }

  /**
   * Return ProductType[].
   *
   * @return
   */
  public ProductType[] getProductTypeSelected() {
    return productTypeSelected;
  }

  /**
   * Set ProductType[].
   *
   * @param productTypeSelected
   */
  public void setProductTypeSelected(ProductType[] productTypeSelected) {
    this.productTypeSelected = productTypeSelected;
  }

  public List<SelectItem> getProductTypeList() {
    SelectItem selectItem;
    List<SelectItem> list = new ArrayList<>();
    for (String pt : SystemConstants.PRODUCT_TYPES) {
      String s[] = pt.split(":");
      selectItem = new SelectItem();
      selectItem.setTextValue(s[0]);
      selectItem.setItemLabel(s[1]);
      list.add(selectItem);
    }
    return list;
  }

  public List<ServiceCommodity> commodityAuto(String filter) {
    return ScmLookupView.commodityAuto(filter);
  }

  public void productTypeReturn() {
    product.setProductTypeId(getProductType());
    Jsf.popupReturn(product, null);
  }

  public void productTypeClose() {
    Jsf.popupClose(product);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
}
