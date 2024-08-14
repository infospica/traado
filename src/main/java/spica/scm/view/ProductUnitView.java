/*
 * @(#)ProductUnitView.java	1.0 Wed Oct 19 11:15:18 IST 2016 
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
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypeAction;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ProductUnit;
import spica.scm.service.ProductUnitService;

/**
 * ProductUnitView
 * @author	Spirit 1.2
 * @version	1.0, Wed Oct 19 11:15:18 IST 2016 
 */

@Named(value="productUnitView")
@ViewScoped
public class ProductUnitView implements Serializable{

  private transient ProductUnit productUnit;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductUnit> productUnitLazyModel; 	//For lazy loading datatable.
  private transient ProductUnit[] productUnitSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public ProductUnitView() {
    super();
  }
 
  /**
   * Return ProductUnit.
   * @return ProductUnit.
   */  
  public ProductUnit getProductUnit() {
    if(productUnit == null) {
      productUnit = new ProductUnit();
    }
    return productUnit;
  }   
  
  /**
   * Set ProductUnit.
   * @param productUnit.
   */   
  public void setProductUnit(ProductUnit productUnit) {
    this.productUnit = productUnit;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchProductUnit(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProductUnit().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProductUnit((ProductUnit) ProductUnitService.selectByPk(main, getProductUnit()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductUnitList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally{
        main.close();
      }
    }
    return null;
  } 
  
  /**
   * Create productUnitLazyModel.
   * @param main
   */
  private void loadProductUnitList(final MainView main) {
    if (productUnitLazyModel == null) {
      productUnitLazyModel = new LazyDataModel<ProductUnit>() {
      private List<ProductUnit> list;      
      @Override
      public List<ProductUnit> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = ProductUnitService.listPaged(main);
          main.commit(productUnitLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(ProductUnit productUnit) {
        return productUnit.getId();
      }
      @Override
        public ProductUnit getRowData(String rowKey) {
          if (list != null) {
            for (ProductUnit obj : list) {
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
    String SUB_FOLDER = "scm_product_unit/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveProductUnit(MainView main) {
    return saveOrCloneProductUnit(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductUnit(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductUnit(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductUnit(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductUnitService.insertOrUpdate(main, getProductUnit());
            break;
          case "clone":
            ProductUnitService.clone(main, getProductUnit());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error."+ key);
    } finally {
      main.close();
    }
    return null;
  }

  
  /**
   * Delete one or many ProductUnit.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductUnit(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productUnitSelected)) {
        ProductUnitService.deleteByPkArray(main, getProductUnitSelected()); //many record delete from list
        main.commit("success.delete");
        productUnitSelected = null;
      } else {
        ProductUnitService.deleteByPk(main, getProductUnit());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())){
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
   * Return LazyDataModel of ProductUnit.
   * @return
   */
  public LazyDataModel<ProductUnit> getProductUnitLazyModel() {
    return productUnitLazyModel;
  }

 /**
  * Return ProductUnit[].
  * @return 
  */
  public ProductUnit[] getProductUnitSelected() {
    return productUnitSelected;
  }
  
  /**
   * Set ProductUnit[].
   * @param productUnitSelected 
   */
  public void setProductUnitSelected(ProductUnit[] productUnitSelected) {
    this.productUnitSelected = productUnitSelected;
  }
 


}
