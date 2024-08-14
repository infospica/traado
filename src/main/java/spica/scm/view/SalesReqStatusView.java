/*
 * @(#)SalesReqStatusView.java	1.0 Mon May 09 18:27:32 IST 2016 
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

import spica.scm.domain.SalesReqStatus;
import spica.scm.service.SalesReqStatusService;

/**
 * SalesReqStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon May 09 18:27:32 IST 2016
 */
@Named(value = "salesReqStatusView")
@ViewScoped
public class SalesReqStatusView implements Serializable {

  private transient SalesReqStatus salesReqStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesReqStatus> salesReqStatusLazyModel; 	//For lazy loading datatable.
  private transient SalesReqStatus[] salesReqStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesReqStatusView() {
    super();
  }

  /**
   * Return SalesReqStatus.
   *
   * @return SalesReqStatus.
   */
  public SalesReqStatus getSalesReqStatus() {
    if (salesReqStatus == null) {
      salesReqStatus = new SalesReqStatus();
    }
    return salesReqStatus;
  }

  /**
   * Set SalesReqStatus.
   *
   * @param salesReqStatus.
   */
  public void setSalesReqStatus(SalesReqStatus salesReqStatus) {
    this.salesReqStatus = salesReqStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesReqStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getSalesReqStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSalesReqStatus((SalesReqStatus) SalesReqStatusService.selectByPk(main, getSalesReqStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesReqStatusList(main);
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
   * Create salesReqStatusLazyModel.
   *
   * @param main
   */
  private void loadSalesReqStatusList(final MainView main) {
    if (salesReqStatusLazyModel == null) {
      salesReqStatusLazyModel = new LazyDataModel<SalesReqStatus>() {
        private List<SalesReqStatus> list;

        @Override
        public List<SalesReqStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesReqStatusService.listPaged(main);
            main.commit(salesReqStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesReqStatus salesReqStatus) {
          return salesReqStatus.getId();
        }

        @Override
        public SalesReqStatus getRowData(String rowKey) {
          if (list != null) {
            for (SalesReqStatus obj : list) {
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
    String SUB_FOLDER = "scm_sales_req_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesReqStatus(MainView main) {
    return saveOrCloneSalesReqStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesReqStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesReqStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesReqStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesReqStatusService.insertOrUpdate(main, getSalesReqStatus());
            break;
          case "clone":
            SalesReqStatusService.clone(main, getSalesReqStatus());
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
   * Delete one or many SalesReqStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesReqStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesReqStatusSelected)) {
        SalesReqStatusService.deleteByPkArray(main, getSalesReqStatusSelected()); //many record delete from list
        main.commit("success.delete");
        salesReqStatusSelected = null;
      } else {
        SalesReqStatusService.deleteByPk(main, getSalesReqStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesReqStatus.
   *
   * @return
   */
  public LazyDataModel<SalesReqStatus> getSalesReqStatusLazyModel() {
    return salesReqStatusLazyModel;
  }

  /**
   * Return SalesReqStatus[].
   *
   * @return
   */
  public SalesReqStatus[] getSalesReqStatusSelected() {
    return salesReqStatusSelected;
  }

  /**
   * Set SalesReqStatus[].
   *
   * @param salesReqStatusSelected
   */
  public void setSalesReqStatusSelected(SalesReqStatus[] salesReqStatusSelected) {
    this.salesReqStatusSelected = salesReqStatusSelected;
  }

}
