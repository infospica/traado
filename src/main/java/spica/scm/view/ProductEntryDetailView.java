/*
 * @(#)ProductEntryDetailView.java	1.0 Thu Sep 08 18:33:15 IST 2016 
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

import spica.scm.domain.ProductEntryDetail;
import spica.scm.service.ProductEntryDetailService;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProdEntDetBuybackStat;
import spica.scm.domain.ProdEntDetRtnStatus;

/**
 * ProductEntryDetailView
 * @author	Spirit 1.2
 * @version	1.0, Thu Sep 08 18:33:15 IST 2016 
 */

@Named(value="productEntryDetailView")
@ViewScoped
public class ProductEntryDetailView implements Serializable{

//  private transient ProductEntryDetail productEntryDetail;	//Domain object/selected Domain.
//  private transient LazyDataModel<ProductEntryDetail> productEntryDetailLazyModel; 	//For lazy loading datatable.
//  private transient ProductEntryDetail[] productEntryDetailSelected;	 //Selected Domain Array
//  /**
//   * Default Constructor.
//   */   
//  public ProductEntryDetailView() {
//    super();
//  }
// 
//  /**
//   * Return ProductEntryDetail.
//   * @return ProductEntryDetail.
//   */  
//  public ProductEntryDetail getProductEntryDetail() {
//    if(productEntryDetail == null) {
//      productEntryDetail = new ProductEntryDetail();
//    }
//    return productEntryDetail;
//  }   
//  
//  /**
//   * Set ProductEntryDetail.
//   * @param productEntryDetail.
//   */   
//  public void setProductEntryDetail(ProductEntryDetail productEntryDetail) {
//    this.productEntryDetail = productEntryDetail;
//  }
// 
//  /**
//   * Change view of
//   * @param main
//   * @param viewType
//   * @return 
//   */
// public String switchProductEntryDetail(MainView main, String viewType) {
//   //this.main = main;
//   if (!StringUtil.isEmpty(viewType)) {
//      try {
//        main.setViewType(viewType);
//        if (ViewType.newform.toString().equals(viewType)) {
//          getProductEntryDetail().reset();
//        } else if (ViewType.editform.toString().equals(viewType)) {
//          setProductEntryDetail((ProductEntryDetail) ProductEntryDetailService.selectByPk(main, getProductEntryDetail()));
//        } else if (ViewType.list.toString().equals(viewType)) {
//          loadProductEntryDetailList(main);
//        }
//      } catch (Throwable t) {
//        main.rollback(t);
//      } finally{
//        main.close();
//      }
//    }
//    return null;
//  } 
//  
//  /**
//   * Create productEntryDetailLazyModel.
//   * @param main
//   */
//  private void loadProductEntryDetailList(final MainView main) {
//    if (productEntryDetailLazyModel == null) {
//      productEntryDetailLazyModel = new LazyDataModel<ProductEntryDetail>() {
//      private List<ProductEntryDetail> list;      
//      @Override
//      public List<ProductEntryDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
//        try {
//          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
//          list = ProductEntryDetailService.listPaged(main);
//          main.commit(productEntryDetailLazyModel, first, pageSize);
//        } catch (Throwable t) {
//          main.rollback(t, "error.list");
//          return null;
//        } finally{
//          main.close();
//        }
//        return list;
//      }
//      @Override
//      public Object getRowKey(ProductEntryDetail productEntryDetail) {
//        return productEntryDetail.getId();
//      }
//      @Override
//        public ProductEntryDetail getRowData(String rowKey) {
//          if (list != null) {
//            for (ProductEntryDetail obj : list) {
//              if (rowKey.equals(obj.getId().toString())) {
//                return obj;
//              }
//            }
//          }
//          return null;
//        }
//      };
//    }
//  }
//
//  private void uploadFiles() {
//    String SUB_FOLDER = "scm_product_entry_detail/";	
//  }
//  
//  /**
//   * Insert or update.
//   * @param main
//   * @return the page to display.
//   */
//  public String saveProductEntryDetail(MainView main) {
//    return saveOrCloneProductEntryDetail(main, "save");
//  }
//
//  /**
//   * Duplicate or clone a record.
//   *
//   * @param main
//   * @return
//   */
//  public String cloneProductEntryDetail(MainView main) {
//    main.setViewType("newform");
//    return saveOrCloneProductEntryDetail(main, "clone"); 
//  }
//
//  /**
//   * Save or clone.
//   *
//   * @param main
//   * @param key
//   * @return
//   */
//  private String saveOrCloneProductEntryDetail(MainView main, String key) {
//    try {
//      uploadFiles(); //File upload
//      if (null != key) {
//        switch (key) {
//          case "save":
//            ProductEntryDetailService.insertOrUpdate(main, getProductEntryDetail());
//            break;
//          case "clone":
//            ProductEntryDetailService.clone(main, getProductEntryDetail());
//            break;
//        }
//        main.commit("success." + key);
//        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
//      }
//    } catch (Throwable t) {
//      main.rollback(t, "error."+ key);
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  
//  /**
//   * Delete one or many ProductEntryDetail.
//   *
//   * @param main
//   * @return the page to display.
//   */
//  public String deleteProductEntryDetail(MainView main) {
//    try {
//      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productEntryDetailSelected)) {
//        ProductEntryDetailService.deleteByPkArray(main, getProductEntryDetailSelected()); //many record delete from list
//        main.commit("success.delete");
//        productEntryDetailSelected = null;
//      } else {
//        ProductEntryDetailService.deleteByPk(main, getProductEntryDetail());  //individual record delete from list or edit form
//        main.commit("success.delete");
//        if ("editform".equals(main.getViewType())){
//          main.setViewType(ViewTypes.newform);
//        }
//      }
//    } catch (Throwable t) {
//      main.rollback(t, "error.delete");
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  /**
//   * Return LazyDataModel of ProductEntryDetail.
//   * @return
//   */
//  public LazyDataModel<ProductEntryDetail> getProductEntryDetailLazyModel() {
//    return productEntryDetailLazyModel;
//  }
//
// /**
//  * Return ProductEntryDetail[].
//  * @return 
//  */
//  public ProductEntryDetail[] getProductEntryDetailSelected() {
//    return productEntryDetailSelected;
//  }
//  
//  /**
//   * Set ProductEntryDetail[].
//   * @param productEntryDetailSelected 
//   */
//  public void setProductEntryDetailSelected(ProductEntryDetail[] productEntryDetailSelected) {
//    this.productEntryDetailSelected = productEntryDetailSelected;
//  }
// 
//
//
// /**
//  * ProductEntry autocomplete filter.
//  * <pre>
//  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//  * If your list is smaller in size and is cached you can use.
//  * <o:converter list="#{ScmLookupView.productEntryAuto(null)}" converterId="omnifaces.ListConverter"  />
//  * Note:- ScmLookupView.productEntryAuto(null) Should be implemented to return full values from cache if the filter is null
//  * </pre>
//  * @param filter
//  * @return
//  */
//  public List<ProductEntry> productEntryAuto(String filter) {
//    return ScmLookupView.productEntryAuto(filter);
//  }
// /**
//  * ProductDetail autocomplete filter.
//  * <pre>
//  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//  * If your list is smaller in size and is cached you can use.
//  * <o:converter list="#{ScmLookupView.productDetailAuto(null)}" converterId="omnifaces.ListConverter"  />
//  * Note:- ScmLookupView.productDetailAuto(null) Should be implemented to return full values from cache if the filter is null
//  * </pre>
//  * @param filter
//  * @return
//  */
//  public List<ProductDetail> productDetailAuto(String filter) {
//    return ScmLookupView.productDetailAuto(filter);
//  }
// /**
//  * ProdEntDetBuybackStat autocomplete filter.
//  * <pre>
//  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//  * If your list is smaller in size and is cached you can use.
//  * <o:converter list="#{ScmLookupView.prodEntDetBuybackStatAuto(null)}" converterId="omnifaces.ListConverter"  />
//  * Note:- ScmLookupView.prodEntDetBuybackStatAuto(null) Should be implemented to return full values from cache if the filter is null
//  * </pre>
//  * @param filter
//  * @return
//  */
//  public List<ProdEntDetBuybackStat> prodEntDetBuybackStatAuto(String filter) {
//    return ScmLookupView.prodEntDetBuybackStatAuto(filter);
//  }
// /**
//  * ProdEntDetRtnStatus autocomplete filter.
//  * <pre>
//  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//  * If your list is smaller in size and is cached you can use.
//  * <o:converter list="#{ScmLookupView.prodEntDetRtnStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
//  * Note:- ScmLookupView.prodEntDetRtnStatusAuto(null) Should be implemented to return full values from cache if the filter is null
//  * </pre>
//  * @param filter
//  * @return
//  */
//  public List<ProdEntDetRtnStatus> prodEntDetRtnStatusAuto(String filter) {
//    return ScmLookupView.prodEntDetRtnStatusAuto(filter);
//  }
}
