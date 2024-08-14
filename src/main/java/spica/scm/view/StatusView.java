/*
 * @(#)StatusView.java	1.0 Tue Jun 28 12:38:19 IST 2016 
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

import spica.scm.domain.Status;
import spica.scm.service.StatusService;

/**
 * StatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Jun 28 12:38:19 IST 2016
 */
@Named(value = "statusView")
@ViewScoped
public class StatusView implements Serializable {

  private transient Status status;	//Domain object/selected Domain.
  private transient LazyDataModel<Status> statusLazyModel; 	//For lazy loading datatable.
  private transient Status[] statusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public StatusView() {
    super();
  }

  /**
   * Return Status.
   *
   * @return Status.
   */
  public Status getStatus() {
    if (status == null) {
      status = new Status();
    }
    return status;
  }

  /**
   * Set Status.
   *
   * @param status.
   */
  public void setStatus(Status status) {
    this.status = status;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setStatus((Status) StatusService.selectByPk(main, getStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadStatusList(main);
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
   * Create statusLazyModel.
   *
   * @param main
   */
  private void loadStatusList(final MainView main) {
    if (statusLazyModel == null) {
      statusLazyModel = new LazyDataModel<Status>() {
        private List<Status> list;

        @Override
        public List<Status> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = StatusService.listPaged(main);
            main.commit(statusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Status status) {
          return status.getId();
        }

        @Override
        public Status getRowData(String rowKey) {
          if (list != null) {
            for (Status obj : list) {
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
    String SUB_FOLDER = "scm_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveStatus(MainView main) {
    return saveOrCloneStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            StatusService.insertOrUpdate(main, getStatus());
            break;
          case "clone":
            StatusService.clone(main, getStatus());
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
   * Delete one or many Status.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(statusSelected)) {
        StatusService.deleteByPkArray(main, getStatusSelected()); //many record delete from list
        main.commit("success.delete");
        statusSelected = null;
      } else {
        StatusService.deleteByPk(main, getStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Status.
   *
   * @return
   */
  public LazyDataModel<Status> getStatusLazyModel() {
    return statusLazyModel;
  }

  /**
   * Return Status[].
   *
   * @return
   */
  public Status[] getStatusSelected() {
    return statusSelected;
  }

  /**
   * Set Status[].
   *
   * @param statusSelected
   */
  public void setStatusSelected(Status[] statusSelected) {
    this.statusSelected = statusSelected;
  }

}
