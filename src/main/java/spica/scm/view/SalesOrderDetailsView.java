/*
 * @(#)SalesOrderDetailsView.java	1.0 Tue May 17 15:23:05 IST 2016 
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

import spica.scm.domain.SalesOrderDetails;
import spica.scm.service.SalesOrderDetailsService;
import spica.scm.domain.SalesOrder;
import spica.scm.domain.Product;
import spica.scm.domain.ProductDetail;

/**
 * SalesOrderDetailsView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 17 15:23:05 IST 2016
 */
@Named(value = "salesOrderDetailsView")
@ViewScoped
public class SalesOrderDetailsView implements Serializable {

  private transient SalesOrderDetails salesOrderDetails;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesOrderDetails> salesOrderDetailsLazyModel; 	//For lazy loading datatable.
  private transient SalesOrderDetails[] salesOrderDetailsSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesOrderDetailsView() {
    super();
  }

  /**
   * Return SalesOrderDetails.
   *
   * @return SalesOrderDetails.
   */
  public SalesOrderDetails getSalesOrderDetails() {
    if (salesOrderDetails == null) {
      salesOrderDetails = new SalesOrderDetails();
    }
    return salesOrderDetails;
  }

  /**
   * Set SalesOrderDetails.
   *
   * @param salesOrderDetails.
   */
  public void setSalesOrderDetails(SalesOrderDetails salesOrderDetails) {
    this.salesOrderDetails = salesOrderDetails;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesOrderDetails(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getSalesOrderDetails().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSalesOrderDetails((SalesOrderDetails) SalesOrderDetailsService.selectByPk(main, getSalesOrderDetails()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesOrderDetailsList(main);
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
   * Create salesOrderDetailsLazyModel.
   *
   * @param main
   */
  private void loadSalesOrderDetailsList(final MainView main) {
    if (salesOrderDetailsLazyModel == null) {
      salesOrderDetailsLazyModel = new LazyDataModel<SalesOrderDetails>() {
        private List<SalesOrderDetails> list;

        @Override
        public List<SalesOrderDetails> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesOrderDetailsService.listPaged(main);
            main.commit(salesOrderDetailsLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesOrderDetails salesOrderDetails) {
          return salesOrderDetails.getId();
        }

        @Override
        public SalesOrderDetails getRowData(String rowKey) {
          if (list != null) {
            for (SalesOrderDetails obj : list) {
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
    String SUB_FOLDER = "scm_sales_order_details/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesOrderDetails(MainView main) {
    return saveOrCloneSalesOrderDetails(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesOrderDetails(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesOrderDetails(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesOrderDetails(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesOrderDetailsService.insertOrUpdate(main, getSalesOrderDetails());
            break;
          case "clone":
            SalesOrderDetailsService.clone(main, getSalesOrderDetails());
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
   * Delete one or many SalesOrderDetails.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesOrderDetails(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesOrderDetailsSelected)) {
        SalesOrderDetailsService.deleteByPkArray(main, getSalesOrderDetailsSelected()); //many record delete from list
        main.commit("success.delete");
        salesOrderDetailsSelected = null;
      } else {
        SalesOrderDetailsService.deleteByPk(main, getSalesOrderDetails());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesOrderDetails.
   *
   * @return
   */
  public LazyDataModel<SalesOrderDetails> getSalesOrderDetailsLazyModel() {
    return salesOrderDetailsLazyModel;
  }

  /**
   * Return SalesOrderDetails[].
   *
   * @return
   */
  public SalesOrderDetails[] getSalesOrderDetailsSelected() {
    return salesOrderDetailsSelected;
  }

  /**
   * Set SalesOrderDetails[].
   *
   * @param salesOrderDetailsSelected
   */
  public void setSalesOrderDetailsSelected(SalesOrderDetails[] salesOrderDetailsSelected) {
    this.salesOrderDetailsSelected = salesOrderDetailsSelected;
  }

  /**
   * SalesOrder autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.salesOrderAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.salesOrderAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesOrder> salesOrderAuto(String filter) {
    return ScmLookupView.salesOrderAuto(filter);
  }

  /**
   * Product autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Product> productAuto(String filter) {
    return ScmLookupView.productAuto(filter);
  }

  /**
   * ProductDetail autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productDetailAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productDetailAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductDetail> productDetailAuto(String filter) {
    return ScmLookupView.productDetailAuto(filter);
  }
}
