/*
 * @(#)PlatformSourceView.java	1.0 Thu Apr 20 15:04:02 IST 2017 
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

import spica.scm.domain.PlatformSource;
import spica.scm.service.PlatformSourceService;

/**
 * PlatformSourceView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:02 IST 2017
 */
@Named(value = "platformSourceView")
@ViewScoped
public class PlatformSourceView implements Serializable {

  private transient PlatformSource platformSource;	//Domain object/selected Domain.
  private transient LazyDataModel<PlatformSource> platformSourceLazyModel; 	//For lazy loading datatable.
  private transient PlatformSource[] platformSourceSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PlatformSourceView() {
    super();
  }

  /**
   * Return PlatformSource.
   *
   * @return PlatformSource.
   */
  public PlatformSource getPlatformSource() {
    if (platformSource == null) {
      platformSource = new PlatformSource();
    }
    return platformSource;
  }

  /**
   * Set PlatformSource.
   *
   * @param platformSource.
   */
  public void setPlatformSource(PlatformSource platformSource) {
    this.platformSource = platformSource;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPlatformSource(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getPlatformSource().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setPlatformSource((PlatformSource) PlatformSourceService.selectByPk(main, getPlatformSource()));
        } else if (ViewTypes.isList(viewType)) {
          loadPlatformSourceList(main);
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
   * Create platformSourceLazyModel.
   *
   * @param main
   */
  private void loadPlatformSourceList(final MainView main) {
    if (platformSourceLazyModel == null) {
      platformSourceLazyModel = new LazyDataModel<PlatformSource>() {
        private List<PlatformSource> list;

        @Override
        public List<PlatformSource> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PlatformSourceService.listPaged(main);
            main.commit(platformSourceLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PlatformSource platformSource) {
          return platformSource.getId();
        }

        @Override
        public PlatformSource getRowData(String rowKey) {
          if (list != null) {
            for (PlatformSource obj : list) {
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
    String SUB_FOLDER = "scm_platform_source/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePlatformSource(MainView main) {
    return saveOrClonePlatformSource(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePlatformSource(MainView main) {
    main.setViewType("newform");
    return saveOrClonePlatformSource(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePlatformSource(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PlatformSourceService.insertOrUpdate(main, getPlatformSource());
            break;
          case "clone":
            PlatformSourceService.clone(main, getPlatformSource());
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
   * Delete one or many PlatformSource.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePlatformSource(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(platformSourceSelected)) {
        PlatformSourceService.deleteByPkArray(main, getPlatformSourceSelected()); //many record delete from list
        main.commit("success.delete");
        platformSourceSelected = null;
      } else {
        PlatformSourceService.deleteByPk(main, getPlatformSource());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PlatformSource.
   *
   * @return
   */
  public LazyDataModel<PlatformSource> getPlatformSourceLazyModel() {
    return platformSourceLazyModel;
  }

  /**
   * Return PlatformSource[].
   *
   * @return
   */
  public PlatformSource[] getPlatformSourceSelected() {
    return platformSourceSelected;
  }

  /**
   * Set PlatformSource[].
   *
   * @param platformSourceSelected
   */
  public void setPlatformSourceSelected(PlatformSource[] platformSourceSelected) {
    this.platformSourceSelected = platformSourceSelected;
  }

}
