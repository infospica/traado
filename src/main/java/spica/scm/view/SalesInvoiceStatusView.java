/*
 * @(#)SalesInvoiceStatusView.java	1.0 Fri Dec 23 10:28:08 IST 2016 
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

import spica.scm.domain.SalesInvoiceStatus;
import spica.scm.service.SalesInvoiceStatusService;

/**
 * SalesInvoiceStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:08 IST 2016
 */
@Named(value = "salesInvoiceStatusView")
@ViewScoped
public class SalesInvoiceStatusView implements Serializable {

  private transient SalesInvoiceStatus salesInvoiceStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesInvoiceStatus> salesInvoiceStatusLazyModel; 	//For lazy loading datatable.
  private transient SalesInvoiceStatus[] salesInvoiceStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesInvoiceStatusView() {
    super();
  }

  /**
   * Return SalesInvoiceStatus.
   *
   * @return SalesInvoiceStatus.
   */
  public SalesInvoiceStatus getSalesInvoiceStatus() {
    if (salesInvoiceStatus == null) {
      salesInvoiceStatus = new SalesInvoiceStatus();
    }
    return salesInvoiceStatus;
  }

  /**
   * Set SalesInvoiceStatus.
   *
   * @param salesInvoiceStatus.
   */
  public void setSalesInvoiceStatus(SalesInvoiceStatus salesInvoiceStatus) {
    this.salesInvoiceStatus = salesInvoiceStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesInvoiceStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getSalesInvoiceStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setSalesInvoiceStatus((SalesInvoiceStatus) SalesInvoiceStatusService.selectByPk(main, getSalesInvoiceStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesInvoiceStatusList(main);
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
   * Create salesInvoiceStatusLazyModel.
   *
   * @param main
   */
  private void loadSalesInvoiceStatusList(final MainView main) {
    if (salesInvoiceStatusLazyModel == null) {
      salesInvoiceStatusLazyModel = new LazyDataModel<SalesInvoiceStatus>() {
        private List<SalesInvoiceStatus> list;

        @Override
        public List<SalesInvoiceStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesInvoiceStatusService.listPaged(main);
            main.commit(salesInvoiceStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesInvoiceStatus salesInvoiceStatus) {
          return salesInvoiceStatus.getId();
        }

        @Override
        public SalesInvoiceStatus getRowData(String rowKey) {
          if (list != null) {
            for (SalesInvoiceStatus obj : list) {
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
    String SUB_FOLDER = "scm_sales_invoice_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesInvoiceStatus(MainView main) {
    return saveOrCloneSalesInvoiceStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesInvoiceStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesInvoiceStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesInvoiceStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesInvoiceStatusService.insertOrUpdate(main, getSalesInvoiceStatus());
            break;
          case "clone":
            SalesInvoiceStatusService.clone(main, getSalesInvoiceStatus());
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
   * Delete one or many SalesInvoiceStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesInvoiceStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesInvoiceStatusSelected)) {
        SalesInvoiceStatusService.deleteByPkArray(main, getSalesInvoiceStatusSelected()); //many record delete from list
        main.commit("success.delete");
        salesInvoiceStatusSelected = null;
      } else {
        SalesInvoiceStatusService.deleteByPk(main, getSalesInvoiceStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesInvoiceStatus.
   *
   * @return
   */
  public LazyDataModel<SalesInvoiceStatus> getSalesInvoiceStatusLazyModel() {
    return salesInvoiceStatusLazyModel;
  }

  /**
   * Return SalesInvoiceStatus[].
   *
   * @return
   */
  public SalesInvoiceStatus[] getSalesInvoiceStatusSelected() {
    return salesInvoiceStatusSelected;
  }

  /**
   * Set SalesInvoiceStatus[].
   *
   * @param salesInvoiceStatusSelected
   */
  public void setSalesInvoiceStatusSelected(SalesInvoiceStatus[] salesInvoiceStatusSelected) {
    this.salesInvoiceStatusSelected = salesInvoiceStatusSelected;
  }

}
