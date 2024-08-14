/*
 * @(#)ProductPackingUnitView.java	1.0 Wed Oct 19 11:15:18 IST 2016 
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

import spica.scm.domain.ProductPackingUnit;
import spica.scm.service.ProductPackingUnitService;
import spica.scm.domain.ProductPacking;
import spica.scm.domain.ProductUnit;

/**
 * ProductPackingUnitView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Oct 19 11:15:18 IST 2016
 */
@Named(value = "productPackingUnitView")
@ViewScoped
public class ProductPackingUnitView implements Serializable {

  private transient ProductPackingUnit productPackingUnit;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductPackingUnit> productPackingUnitLazyModel; 	//For lazy loading datatable.
  private transient ProductPackingUnit[] productPackingUnitSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ProductPackingUnitView() {
    super();
  }

  /**
   * Return ProductPackingUnit.
   *
   * @return ProductPackingUnit.
   */
  public ProductPackingUnit getProductPackingUnit() {
    if (productPackingUnit == null) {
      productPackingUnit = new ProductPackingUnit();
    }
    return productPackingUnit;
  }

  /**
   * Set ProductPackingUnit.
   *
   * @param productPackingUnit.
   */
  public void setProductPackingUnit(ProductPackingUnit productPackingUnit) {
    this.productPackingUnit = productPackingUnit;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductPackingUnit(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProductPackingUnit().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProductPackingUnit((ProductPackingUnit) ProductPackingUnitService.selectByPk(main, getProductPackingUnit()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductPackingUnitList(main);
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
   * Create productPackingUnitLazyModel.
   *
   * @param main
   */
  private void loadProductPackingUnitList(final MainView main) {
    if (productPackingUnitLazyModel == null) {
      productPackingUnitLazyModel = new LazyDataModel<ProductPackingUnit>() {
        private List<ProductPackingUnit> list;

        @Override
        public List<ProductPackingUnit> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductPackingUnitService.listPaged(main);
            main.commit(productPackingUnitLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductPackingUnit productPackingUnit) {
          return productPackingUnit.getId();
        }

        @Override
        public ProductPackingUnit getRowData(String rowKey) {
          if (list != null) {
            for (ProductPackingUnit obj : list) {
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
    String SUB_FOLDER = "scm_product_packing_unit/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductPackingUnit(MainView main) {
    return saveOrCloneProductPackingUnit(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductPackingUnit(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductPackingUnit(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductPackingUnit(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductPackingUnitService.insertOrUpdate(main, getProductPackingUnit());
            break;
          case "clone":
            ProductPackingUnitService.clone(main, getProductPackingUnit());
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
   * Delete one or many ProductPackingUnit.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductPackingUnit(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productPackingUnitSelected)) {
        ProductPackingUnitService.deleteByPkArray(main, getProductPackingUnitSelected()); //many record delete from list
        main.commit("success.delete");
        productPackingUnitSelected = null;
      } else {
        ProductPackingUnitService.deleteByPk(main, getProductPackingUnit());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductPackingUnit.
   *
   * @return
   */
  public LazyDataModel<ProductPackingUnit> getProductPackingUnitLazyModel() {
    return productPackingUnitLazyModel;
  }

  /**
   * Return ProductPackingUnit[].
   *
   * @return
   */
  public ProductPackingUnit[] getProductPackingUnitSelected() {
    return productPackingUnitSelected;
  }

  /**
   * Set ProductPackingUnit[].
   *
   * @param productPackingUnitSelected
   */
  public void setProductPackingUnitSelected(ProductPackingUnit[] productPackingUnitSelected) {
    this.productPackingUnitSelected = productPackingUnitSelected;
  }

  /**
   * ProductPacking autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productPackingAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productPackingAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductPacking> productPackingAuto(String filter) {
    return ScmLookupView.productPackingAuto(filter);
  }

  public List<ProductUnit> productUnitAuto(String filter) {
    return ScmLookupView.productUnitAuto(filter);
  }
}
