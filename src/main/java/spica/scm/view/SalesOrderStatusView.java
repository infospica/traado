/*
 * @(#)SalesOrderStatusView.java	1.0 Mon May 09 18:27:32 IST 2016 
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

import spica.scm.domain.SalesOrderStatus;
import spica.scm.service.SalesOrderStatusService;

/**
 * SalesOrderStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon May 09 18:27:32 IST 2016
 */
@Named(value = "salesOrderStatusView")
@ViewScoped
public class SalesOrderStatusView implements Serializable {

  private transient SalesOrderStatus salesOrderStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesOrderStatus> salesOrderStatusLazyModel; 	//For lazy loading datatable.
  private transient SalesOrderStatus[] salesOrderStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesOrderStatusView() {
    super();
  }

  /**
   * Return SalesOrderStatus.
   *
   * @return SalesOrderStatus.
   */
  public SalesOrderStatus getSalesOrderStatus() {
    if (salesOrderStatus == null) {
      salesOrderStatus = new SalesOrderStatus();
    }
    return salesOrderStatus;
  }

  /**
   * Set SalesOrderStatus.
   *
   * @param salesOrderStatus.
   */
  public void setSalesOrderStatus(SalesOrderStatus salesOrderStatus) {
    this.salesOrderStatus = salesOrderStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesOrderStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getSalesOrderStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSalesOrderStatus((SalesOrderStatus) SalesOrderStatusService.selectByPk(main, getSalesOrderStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesOrderStatusList(main);
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
   * Create salesOrderStatusLazyModel.
   *
   * @param main
   */
  private void loadSalesOrderStatusList(final MainView main) {
    if (salesOrderStatusLazyModel == null) {
      salesOrderStatusLazyModel = new LazyDataModel<SalesOrderStatus>() {
        private List<SalesOrderStatus> list;

        @Override
        public List<SalesOrderStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesOrderStatusService.listPaged(main);
            main.commit(salesOrderStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesOrderStatus salesOrderStatus) {
          return salesOrderStatus.getId();
        }

        @Override
        public SalesOrderStatus getRowData(String rowKey) {
          if (list != null) {
            for (SalesOrderStatus obj : list) {
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
    String SUB_FOLDER = "scm_sales_order_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesOrderStatus(MainView main) {
    return saveOrCloneSalesOrderStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesOrderStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesOrderStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesOrderStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesOrderStatusService.insertOrUpdate(main, getSalesOrderStatus());
            break;
          case "clone":
            SalesOrderStatusService.clone(main, getSalesOrderStatus());
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
   * Delete one or many SalesOrderStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesOrderStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesOrderStatusSelected)) {
        SalesOrderStatusService.deleteByPkArray(main, getSalesOrderStatusSelected()); //many record delete from list
        main.commit("success.delete");
        salesOrderStatusSelected = null;
      } else {
        SalesOrderStatusService.deleteByPk(main, getSalesOrderStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesOrderStatus.
   *
   * @return
   */
  public LazyDataModel<SalesOrderStatus> getSalesOrderStatusLazyModel() {
    return salesOrderStatusLazyModel;
  }

  /**
   * Return SalesOrderStatus[].
   *
   * @return
   */
  public SalesOrderStatus[] getSalesOrderStatusSelected() {
    return salesOrderStatusSelected;
  }

  /**
   * Set SalesOrderStatus[].
   *
   * @param salesOrderStatusSelected
   */
  public void setSalesOrderStatusSelected(SalesOrderStatus[] salesOrderStatusSelected) {
    this.salesOrderStatusSelected = salesOrderStatusSelected;
  }

}
