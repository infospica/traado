/*
 * @(#)PlatformStatusView.java	1.0 Thu Apr 20 15:04:02 IST 2017 
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
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.PlatformStatus;
import spica.scm.service.PlatformStatusService;

/**
 * PlatformStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:02 IST 2017
 */
@Named(value = "platformStatusView")
@ViewScoped
public class PlatformStatusView implements Serializable {

  private transient PlatformStatus platformStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<PlatformStatus> platformStatusLazyModel; 	//For lazy loading datatable.
  private transient PlatformStatus[] platformStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PlatformStatusView() {
    super();
  }

  /**
   * Return PlatformStatus.
   *
   * @return PlatformStatus.
   */
  public PlatformStatus getPlatformStatus() {
    if (platformStatus == null) {
      platformStatus = new PlatformStatus();
    }
    return platformStatus;
  }

  /**
   * Set PlatformStatus.
   *
   * @param platformStatus.
   */
  public void setPlatformStatus(PlatformStatus platformStatus) {
    this.platformStatus = platformStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPlatformStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getPlatformStatus().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setPlatformStatus((PlatformStatus) PlatformStatusService.selectByPk(main, getPlatformStatus()));
        } else if (ViewTypes.isList(viewType)) {
          loadPlatformStatusList(main);
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
   * Create platformStatusLazyModel.
   *
   * @param main
   */
  private void loadPlatformStatusList(final MainView main) {
    if (platformStatusLazyModel == null) {
      platformStatusLazyModel = new LazyDataModel<PlatformStatus>() {
        private List<PlatformStatus> list;

        @Override
        public List<PlatformStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PlatformStatusService.listPaged(main);
            main.commit(platformStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PlatformStatus platformStatus) {
          return platformStatus.getId();
        }

        @Override
        public PlatformStatus getRowData(String rowKey) {
          if (list != null) {
            for (PlatformStatus obj : list) {
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
    String SUB_FOLDER = "scm_platform_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePlatformStatus(MainView main) {
    return saveOrClonePlatformStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePlatformStatus(MainView main) {
    main.setViewType("newform");
    return saveOrClonePlatformStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePlatformStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PlatformStatusService.insertOrUpdate(main, getPlatformStatus());
            break;
          case "clone":
            PlatformStatusService.clone(main, getPlatformStatus());
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
   * Delete one or many PlatformStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePlatformStatus(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(platformStatusSelected)) {
        PlatformStatusService.deleteByPkArray(main, getPlatformStatusSelected()); //many record delete from list
        main.commit("success.delete");
        platformStatusSelected = null;
      } else {
        PlatformStatusService.deleteByPk(main, getPlatformStatus());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (ViewTypes.isEdit(main.getViewType())) {
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
   * Return LazyDataModel of PlatformStatus.
   *
   * @return
   */
  public LazyDataModel<PlatformStatus> getPlatformStatusLazyModel() {
    return platformStatusLazyModel;
  }

  /**
   * Return PlatformStatus[].
   *
   * @return
   */
  public PlatformStatus[] getPlatformStatusSelected() {
    return platformStatusSelected;
  }

  /**
   * Set PlatformStatus[].
   *
   * @param platformStatusSelected
   */
  public void setPlatformStatusSelected(PlatformStatus[] platformStatusSelected) {
    this.platformStatusSelected = platformStatusSelected;
  }

}
