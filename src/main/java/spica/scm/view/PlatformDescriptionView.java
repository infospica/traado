/*
 * @(#)PlatformDescriptionView.java	1.0 Thu Apr 20 15:04:02 IST 2017 
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

import spica.scm.domain.PlatformDescription;
import spica.scm.service.PlatformDescriptionService;

/**
 * PlatformDescriptionView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:02 IST 2017
 */
@Named(value = "platformDescriptionView")
@ViewScoped
public class PlatformDescriptionView implements Serializable {

  private transient PlatformDescription platformDescription;	//Domain object/selected Domain.
  private transient LazyDataModel<PlatformDescription> platformDescriptionLazyModel; 	//For lazy loading datatable.
  private transient PlatformDescription[] platformDescriptionSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PlatformDescriptionView() {
    super();
  }

  /**
   * Return PlatformDescription.
   *
   * @return PlatformDescription.
   */
  public PlatformDescription getPlatformDescription() {
    if (platformDescription == null) {
      platformDescription = new PlatformDescription();
    }
    return platformDescription;
  }

  /**
   * Set PlatformDescription.
   *
   * @param platformDescription.
   */
  public void setPlatformDescription(PlatformDescription platformDescription) {
    this.platformDescription = platformDescription;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPlatformDescription(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getPlatformDescription().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setPlatformDescription((PlatformDescription) PlatformDescriptionService.selectByPk(main, getPlatformDescription()));
        } else if (ViewTypes.isList(viewType)) {
          loadPlatformDescriptionList(main);
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
   * Create platformDescriptionLazyModel.
   *
   * @param main
   */
  private void loadPlatformDescriptionList(final MainView main) {
    if (platformDescriptionLazyModel == null) {
      platformDescriptionLazyModel = new LazyDataModel<PlatformDescription>() {
        private List<PlatformDescription> list;

        @Override
        public List<PlatformDescription> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PlatformDescriptionService.listPaged(main);
            main.commit(platformDescriptionLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PlatformDescription platformDescription) {
          return platformDescription.getId();
        }

        @Override
        public PlatformDescription getRowData(String rowKey) {
          if (list != null) {
            for (PlatformDescription obj : list) {
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
    String SUB_FOLDER = "scm_platform_description/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePlatformDescription(MainView main) {
    return saveOrClonePlatformDescription(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePlatformDescription(MainView main) {
    main.setViewType("newform");
    return saveOrClonePlatformDescription(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePlatformDescription(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PlatformDescriptionService.insertOrUpdate(main, getPlatformDescription());
            break;
          case "clone":
            PlatformDescriptionService.clone(main, getPlatformDescription());
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
   * Delete one or many PlatformDescription.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePlatformDescription(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(platformDescriptionSelected)) {
        PlatformDescriptionService.deleteByPkArray(main, getPlatformDescriptionSelected()); //many record delete from list
        main.commit("success.delete");
        platformDescriptionSelected = null;
      } else {
        PlatformDescriptionService.deleteByPk(main, getPlatformDescription());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PlatformDescription.
   *
   * @return
   */
  public LazyDataModel<PlatformDescription> getPlatformDescriptionLazyModel() {
    return platformDescriptionLazyModel;
  }

  /**
   * Return PlatformDescription[].
   *
   * @return
   */
  public PlatformDescription[] getPlatformDescriptionSelected() {
    return platformDescriptionSelected;
  }

  /**
   * Set PlatformDescription[].
   *
   * @param platformDescriptionSelected
   */
  public void setPlatformDescriptionSelected(PlatformDescription[] platformDescriptionSelected) {
    this.platformDescriptionSelected = platformDescriptionSelected;
  }

}
