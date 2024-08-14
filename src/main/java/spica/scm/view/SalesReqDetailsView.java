/*
 * @(#)SalesReqDetailsView.java	1.0 Tue May 10 17:16:17 IST 2016
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

import spica.scm.domain.SalesReqDetails;
import spica.scm.service.SalesReqDetailsService;
import spica.scm.domain.SalesReq;
import spica.scm.domain.ProductDetail;

/**
 * SalesReqDetailsView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 10 17:16:17 IST 2016
 */
@Named(value = "salesReqDetailsView")
@ViewScoped
public class SalesReqDetailsView implements Serializable {

  private transient SalesReqDetails salesReqDetails;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesReqDetails> salesReqDetailsLazyModel; 	//For lazy loading datatable.
  private transient SalesReqDetails[] salesReqDetailsSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesReqDetailsView() {
    super();
  }

  /**
   * Return SalesReqDetails.
   *
   * @return SalesReqDetails.
   */
  public SalesReqDetails getSalesReqDetails() {
    if (salesReqDetails == null) {
      salesReqDetails = new SalesReqDetails();
    }
    return salesReqDetails;
  }

  /**
   * Set SalesReqDetails.
   *
   * @param salesReqDetails.
   */
  public void setSalesReqDetails(SalesReqDetails salesReqDetails) {
    this.salesReqDetails = salesReqDetails;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesReqDetails(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getSalesReqDetails().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSalesReqDetails((SalesReqDetails) SalesReqDetailsService.selectByPk(main, getSalesReqDetails()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesReqDetailsList(main);
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
   * Create salesReqDetailsLazyModel.
   *
   * @param main
   */
  private void loadSalesReqDetailsList(final MainView main) {
    if (salesReqDetailsLazyModel == null) {
      salesReqDetailsLazyModel = new LazyDataModel<SalesReqDetails>() {
        private List<SalesReqDetails> list;

        @Override
        public List<SalesReqDetails> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesReqDetailsService.listPaged(main);
            main.commit(salesReqDetailsLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesReqDetails salesReqDetails) {
          return salesReqDetails.getId();
        }

        @Override
        public SalesReqDetails getRowData(String rowKey) {
          if (list != null) {
            for (SalesReqDetails obj : list) {
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
    String SUB_FOLDER = "scm_sales_req_details/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesReqDetails(MainView main) {
    return saveOrCloneSalesReqDetails(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesReqDetails(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesReqDetails(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesReqDetails(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesReqDetailsService.insertOrUpdate(main, getSalesReqDetails());
            break;
          case "clone":
            SalesReqDetailsService.clone(main, getSalesReqDetails());
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
   * Delete one or many SalesReqDetails.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesReqDetails(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesReqDetailsSelected)) {
        SalesReqDetailsService.deleteByPkArray(main, getSalesReqDetailsSelected()); //many record delete from list
        main.commit("success.delete");
        salesReqDetailsSelected = null;
      } else {
        SalesReqDetailsService.deleteByPk(main, getSalesReqDetails());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesReqDetails.
   *
   * @return
   */
  public LazyDataModel<SalesReqDetails> getSalesReqDetailsLazyModel() {
    return salesReqDetailsLazyModel;
  }

  /**
   * Return SalesReqDetails[].
   *
   * @return
   */
  public SalesReqDetails[] getSalesReqDetailsSelected() {
    return salesReqDetailsSelected;
  }

  /**
   * Set SalesReqDetails[].
   *
   * @param salesReqDetailsSelected
   */
  public void setSalesReqDetailsSelected(SalesReqDetails[] salesReqDetailsSelected) {
    this.salesReqDetailsSelected = salesReqDetailsSelected;
  }

  /**
   * SalesReq autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.salesReqAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.salesReqAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesReq> salesReqAuto(String filter) {
    return ScmLookupView.salesReqAuto(filter);
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
