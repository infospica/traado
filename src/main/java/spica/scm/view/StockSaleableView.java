/*
 * @(#)StockSaleableView.java	1.0 Wed May 25 15:59:57 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * StockSaleableView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 15:59:57 IST 2016
 */
@Named(value = "stockSaleableView")
@ViewScoped
public class StockSaleableView implements Serializable {

//  private transient StockSaleable stockSaleable;	//Domain object/selected Domain.
//  private transient LazyDataModel<StockSaleable> stockSaleableLazyModel; 	//For lazy loading datatable.
//  private transient StockSaleable[] stockSaleableSelected;	 //Selected Domain Array
//  /**
//   * Default Constructor.
//   */   
//  public StockSaleableView() {
//    super();
//  }
// 
//  /**
//   * Return StockSaleable.
//   * @return StockSaleable.
//   */  
//  public StockSaleable getStockSaleable() {
//    if(stockSaleable == null) {
//      stockSaleable = new StockSaleable();
//    }
//    return stockSaleable;
//  }   
//  
//  /**
//   * Set StockSaleable.
//   * @param stockSaleable.
//   */   
//  public void setStockSaleable(StockSaleable stockSaleable) {
//    this.stockSaleable = stockSaleable;
//  }
// 
//  /**
//   * Change view of
//   * @param main
//   * @param viewType
//   * @return 
//   */
// public String switchStockSaleable(MainView main, String viewType) {
//   //this.main = main;
//   if (!StringUtil.isEmpty(viewType)) {
//      try {
//        main.setViewType(viewType);
//        if (ViewType.newform.toString().equals(viewType)) {
//          getStockSaleable().reset();
//        } else if (ViewType.editform.toString().equals(viewType)) {
//          setStockSaleable((StockSaleable) StockSaleableService.selectByPk(main, getStockSaleable()));
//        } else if (ViewType.list.toString().equals(viewType)) {
//          loadStockSaleableList(main);
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
//   * Create stockSaleableLazyModel.
//   * @param main
//   */
//  private void loadStockSaleableList(final MainView main) {
//    if (stockSaleableLazyModel == null) {
//      stockSaleableLazyModel = new LazyDataModel<StockSaleable>() {
//      private List<StockSaleable> list;      
//      @Override
//      public List<StockSaleable> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
//        try {
//          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
//          list = StockSaleableService.listPaged(main);
//          main.commit(stockSaleableLazyModel, first, pageSize);
//        } catch (Throwable t) {
//          main.rollback(t, "error.list");
//          return null;
//        } finally{
//          main.close();
//        }
//        return list;
//      }
//      @Override
//      public Object getRowKey(StockSaleable stockSaleable) {
//        return stockSaleable.getId();
//      }
//      @Override
//        public StockSaleable getRowData(String rowKey) {
//          if (list != null) {
//            for (StockSaleable obj : list) {
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
//    String SUB_FOLDER = "scm_stock_saleable/";	
//  }
//  
//  /**
//   * Insert or update.
//   * @param main
//   * @return the page to display.
//   */
//  public String saveStockSaleable(MainView main) {
//    return saveOrCloneStockSaleable(main, "save");
//  }
//
//  /**
//   * Duplicate or clone a record.
//   *
//   * @param main
//   * @return
//   */
//  public String cloneStockSaleable(MainView main) {
//    main.setViewType("newform");
//    return saveOrCloneStockSaleable(main, "clone"); 
//  }
//
//  /**
//   * Save or clone.
//   *
//   * @param main
//   * @param key
//   * @return
//   */
//  private String saveOrCloneStockSaleable(MainView main, String key) {
//    try {
//      uploadFiles(); //File upload
//      if (null != key) {
//        switch (key) {
//          case "save":
//            StockSaleableService.insertOrUpdate(main, getStockSaleable());
//            break;
//          case "clone":
//            StockSaleableService.clone(main, getStockSaleable());
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
//   * Delete one or many StockSaleable.
//   *
//   * @param main
//   * @return the page to display.
//   */
//  public String deleteStockSaleable(MainView main) {
//    try {
//      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(stockSaleableSelected)) {
//        StockSaleableService.deleteByPkArray(main, getStockSaleableSelected()); //many record delete from list
//        main.commit("success.delete");
//        stockSaleableSelected = null;
//      } else {
//        StockSaleableService.deleteByPk(main, getStockSaleable());  //individual record delete from list or edit form
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
//   * Return LazyDataModel of StockSaleable.
//   * @return
//   */
//  public LazyDataModel<StockSaleable> getStockSaleableLazyModel() {
//    return stockSaleableLazyModel;
//  }
//
// /**
//  * Return StockSaleable[].
//  * @return 
//  */
//  public StockSaleable[] getStockSaleableSelected() {
//    return stockSaleableSelected;
//  }
//  
//  /**
//   * Set StockSaleable[].
//   * @param stockSaleableSelected 
//   */
//  public void setStockSaleableSelected(StockSaleable[] stockSaleableSelected) {
//    this.stockSaleableSelected = stockSaleableSelected;
//  }
// 
//
//
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
//  * Account autocomplete filter.
//  * <pre>
//  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//  * If your list is smaller in size and is cached you can use.
//  * <o:converter list="#{ScmLookupView.accountAuto(null)}" converterId="omnifaces.ListConverter"  />
//  * Note:- ScmLookupView.accountAuto(null) Should be implemented to return full values from cache if the filter is null
//  * </pre>
//  * @param filter
//  * @return
//  */
//  public List<Account> accountAuto(String filter) {
//    return ScmLookupView.accountAuto(filter);
//  }
// /**
//  * StockStatus autocomplete filter.
//  * <pre>
//  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//  * If your list is smaller in size and is cached you can use.
//  * <o:converter list="#{ScmLookupView.stockStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
//  * Note:- ScmLookupView.stockStatusAuto(null) Should be implemented to return full values from cache if the filter is null
//  * </pre>
//  * @param filter
//  * @return
//  */
//  public List<StockStatus> stockStatusAuto(String filter) {
//    return ScmLookupView.stockStatusAuto(filter);
//  }
// 
// /**
//  * SalesOrderDetails autocomplete filter.
//  * <pre>
//  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//  * If your list is smaller in size and is cached you can use.
//  * <o:converter list="#{ScmLookupView.salesOrderDetailsAuto(null)}" converterId="omnifaces.ListConverter"  />
//  * Note:- ScmLookupView.salesOrderDetailsAuto(null) Should be implemented to return full values from cache if the filter is null
//  * </pre>
//  * @param filter
//  * @return
//  */
//  public List<SalesOrderDetails> salesOrderDetailsAuto(String filter) {
//    return ScmLookupView.salesOrderDetailsAuto(filter);
//  }
// /**
//  * SalesReturnDetail autocomplete filter.
//  * <pre>
//  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//  * If your list is smaller in size and is cached you can use.
//  * <o:converter list="#{ScmLookupView.salesReturnDetailAuto(null)}" converterId="omnifaces.ListConverter"  />
//  * Note:- ScmLookupView.salesReturnDetailAuto(null) Should be implemented to return full values from cache if the filter is null
//  * </pre>
//  * @param filter
//  * @return
//  */
////  public List<SalesReturnDetail> salesReturnDetailAuto(String filter) {
////    return null;
////    //return ScmLookupView.salesReturnDetailAuto(filter);
////  }
// /**
//  * PurchaseReturn autocomplete filter.
//  * <pre>
//  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//  * If your list is smaller in size and is cached you can use.
//  * <o:converter list="#{ScmLookupView.purchaseReturnAuto(null)}" converterId="omnifaces.ListConverter"  />
//  * Note:- ScmLookupView.purchaseReturnAuto(null) Should be implemented to return full values from cache if the filter is null
//  * </pre>
//  * @param filter
//  * @return
//  */
//  public List<PurchaseReturn> purchaseReturnAuto(String filter) {
//    return ScmLookupView.purchaseReturnAuto(filter);
//  }
}
