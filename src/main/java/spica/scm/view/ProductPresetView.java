/*
 * @(#)ProductPresetView.java	1.0 Tue Sep 20 15:27:07 IST 2016 
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

import spica.scm.domain.ProductPreset;
import spica.scm.service.ProductPresetService;
import spica.scm.domain.Product;
import spica.scm.domain.Account;
import spica.scm.domain.ProductPacking;
import spica.scm.domain.ProductPresetStatus;

/**
 * ProductPresetView
 * @author	Spirit 1.2
 * @version	1.0, Tue Sep 20 15:27:07 IST 2016 
 */

@Named(value="productPresetView")
@ViewScoped
public class ProductPresetView implements Serializable{

  private transient ProductPreset productPreset;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductPreset> productPresetLazyModel; 	//For lazy loading datatable.
  private transient ProductPreset[] productPresetSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public ProductPresetView() {
    super();
  }
 
  /**
   * Return ProductPreset.
   * @return ProductPreset.
   */  
  public ProductPreset getProductPreset() {
    if(productPreset == null) {
      productPreset = new ProductPreset();
    }
    return productPreset;
  }   
  
  /**
   * Set ProductPreset.
   * @param productPreset.
   */   
  public void setProductPreset(ProductPreset productPreset) {
    this.productPreset = productPreset;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchProductPreset(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getProductPreset().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setProductPreset((ProductPreset) ProductPresetService.selectByPk(main, getProductPreset()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductPresetList(main);
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
   * Create productPresetLazyModel.
   * @param main
   */
  private void loadProductPresetList(final MainView main) {
    if (productPresetLazyModel == null) {
      productPresetLazyModel = new LazyDataModel<ProductPreset>() {
      private List<ProductPreset> list;      
      @Override
      public List<ProductPreset> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = ProductPresetService.listPaged(main);
          main.commit(productPresetLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(ProductPreset productPreset) {
        return productPreset.getId();
      }
      @Override
        public ProductPreset getRowData(String rowKey) {
          if (list != null) {
            for (ProductPreset obj : list) {
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
    String SUB_FOLDER = "scm_product_preset/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveProductPreset(MainView main) {
    return saveOrCloneProductPreset(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductPreset(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductPreset(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductPreset(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductPresetService.insertOrUpdate(main, getProductPreset());
            break;
          case "clone":
            ProductPresetService.clone(main, getProductPreset());
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
   * Delete one or many ProductPreset.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductPreset(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productPresetSelected)) {
        ProductPresetService.deleteByPkArray(main, getProductPresetSelected()); //many record delete from list
        main.commit("success.delete");
        productPresetSelected = null;
      } else {
        ProductPresetService.deleteByPk(main, getProductPreset());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductPreset.
   * @return
   */
  public LazyDataModel<ProductPreset> getProductPresetLazyModel() {
    return productPresetLazyModel;
  }

 /**
  * Return ProductPreset[].
  * @return 
  */
  public ProductPreset[] getProductPresetSelected() {
    return productPresetSelected;
  }
  
  /**
   * Set ProductPreset[].
   * @param productPresetSelected 
   */
  public void setProductPresetSelected(ProductPreset[] productPresetSelected) {
    this.productPresetSelected = productPresetSelected;
  }
 


 /**
  * Product autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.productAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.productAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<Product> productAuto(String filter) {
    return ScmLookupView.productAuto(filter);
  }
 /**
  * Account autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.accountAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.accountAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<Account> accountAuto(String filter) {
    return ScmLookupView.accountAuto(filter);
  }
 /**
  * ProductPacking autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.productPackingAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.productPackingAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<ProductPacking> productPackingAuto(String filter) {
    return ScmLookupView.productPackingAuto(filter);
  }
 /**
  * ProductPresetStatus autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.productPresetStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.productPresetStatusAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<ProductPresetStatus> productPresetStatusAuto(String filter) {
    return ScmLookupView.productPresetStatusAuto(filter);
  }
}
