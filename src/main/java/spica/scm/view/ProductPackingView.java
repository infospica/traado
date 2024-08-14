/*
 * @(#)ProductPackingView.java	1.0 Thu Aug 25 13:58:48 IST 2016 
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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ProductPacking;
import spica.scm.service.ProductPackingService;
import spica.scm.domain.PackType;
import spica.scm.domain.ProductUnit;
import spica.scm.service.ProductUnitService;

/**
 * ProductPackingView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Aug 25 13:58:48 IST 2016
 */
@Named(value = "productPackingView")
@ViewScoped
public class ProductPackingView implements Serializable {

  private transient ProductPacking productPacking;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductPacking> productPackingLazyModel; 	//For lazy loading datatable.
  private transient ProductPacking[] productPackingSelected;	 //Selected Domain Array
  private List<ProductUnit> selectedUnit;
  private List<ProductUnit> unitSelected;

  /**
   * Default Constructor.
   */
  public ProductPackingView() {
    super();
  }

  /**
   * Return ProductPacking.
   *
   * @return ProductPacking.
   */
  public ProductPacking getProductPacking() {
    if (productPacking == null) {
      productPacking = new ProductPacking();
    }
    return productPacking;
  }

  /**
   * Set ProductPacking.
   *
   * @param productPacking.
   */
  public void setProductPacking(ProductPacking productPacking) {
    this.productPacking = productPacking;
  }

  public List<ProductUnit> getSelectedUnit() {
    return selectedUnit;
  }

  public void setSelectedUnit(List<ProductUnit> selectedUnit) {
    this.selectedUnit = selectedUnit;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductPacking(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProductPacking().reset();
          selectedUnit = null;
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProductPacking((ProductPacking) ProductPackingService.selectByPk(main, getProductPacking()));
          selectedUnit = ProductUnitService.selectUnitByPacking(main, getProductPacking()); //3 Loading the selected records
        } else if (ViewType.list.toString().equals(viewType)) {
          unitSelected = null;
          loadProductPackingList(main);
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
   * Create productPackingLazyModel.
   *
   * @param main
   */
  private void loadProductPackingList(final MainView main) {
    if (productPackingLazyModel == null) {
      productPackingLazyModel = new LazyDataModel<ProductPacking>() {
        private List<ProductPacking> list;

        @Override
        public List<ProductPacking> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductPackingService.listPaged(main);
            main.commit(productPackingLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductPacking productPacking) {
          return productPacking.getId();
        }

        @Override
        public ProductPacking getRowData(String rowKey) {
          if (list != null) {
            for (ProductPacking obj : list) {
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
    String SUB_FOLDER = "scm_product_packing/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductPacking(MainView main) {
    return saveOrCloneProductPacking(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductPacking(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductPacking(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductPacking(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductPackingService.insertOrUpdate(main, getProductPacking(), selectedUnit);
            break;
          case "clone":
            ProductPackingService.clone(main, getProductPacking());
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
   * Delete one or many ProductPacking.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductPacking(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productPackingSelected)) {
        ProductPackingService.deleteByPkArray(main, getProductPackingSelected()); //many record delete from list
        main.commit("success.delete");
        productPackingSelected = null;
      } else {
        ProductPackingService.deleteByPk(main, getProductPacking());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductPacking.
   *
   * @return
   */
  public LazyDataModel<ProductPacking> getProductPackingLazyModel() {
    return productPackingLazyModel;
  }

  /**
   * Return ProductPacking[].
   *
   * @return
   */
  public ProductPacking[] getProductPackingSelected() {
    return productPackingSelected;
  }

  /**
   * Set ProductPacking[].
   *
   * @param productPackingSelected
   */
  public void setProductPackingSelected(ProductPacking[] productPackingSelected) {
    this.productPackingSelected = productPackingSelected;
  }

  /**
   * PackType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.packTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.packTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<PackType> packTypeAuto(String filter) {
    return ScmLookupView.packTypeAuto(filter);
  }

  public void packTypeSelectEvent(SelectEvent event) {
  }

  public boolean isPackTypePrimary() {
    return getProductPacking().getPackType() != null && getProductPacking().getPackType().getId() == 1;

  }

  public List<ProductUnit> getUnitSelected() {
    if (unitSelected == null) {
      unitSelected = ScmLookupView.productUnitAuto(null);
    }
    return unitSelected;
  }
}
