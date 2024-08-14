/*
 * @(#)PlatformSettlementTypeView.java	1.0 Thu Apr 20 15:04:02 IST 2017 
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

import spica.scm.domain.PlatformSettlementType;
import spica.scm.service.PlatformSettlementTypeService;

/**
 * PlatformSettlementTypeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:02 IST 2017
 */
@Named(value = "platformSettlementTypeView")
@ViewScoped
public class PlatformSettlementTypeView implements Serializable {

  private transient PlatformSettlementType platformSettlementType;	//Domain object/selected Domain.
  private transient LazyDataModel<PlatformSettlementType> platformSettlementTypeLazyModel; 	//For lazy loading datatable.
  private transient PlatformSettlementType[] platformSettlementTypeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PlatformSettlementTypeView() {
    super();
  }

  /**
   * Return PlatformSettlementType.
   *
   * @return PlatformSettlementType.
   */
  public PlatformSettlementType getPlatformSettlementType() {
    if (platformSettlementType == null) {
      platformSettlementType = new PlatformSettlementType();
    }
    return platformSettlementType;
  }

  /**
   * Set PlatformSettlementType.
   *
   * @param platformSettlementType.
   */
  public void setPlatformSettlementType(PlatformSettlementType platformSettlementType) {
    this.platformSettlementType = platformSettlementType;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPlatformSettlementType(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getPlatformSettlementType().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setPlatformSettlementType((PlatformSettlementType) PlatformSettlementTypeService.selectByPk(main, getPlatformSettlementType()));
        } else if (ViewTypes.isList(viewType)) {
          loadPlatformSettlementTypeList(main);
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
   * Create platformSettlementTypeLazyModel.
   *
   * @param main
   */
  private void loadPlatformSettlementTypeList(final MainView main) {
    if (platformSettlementTypeLazyModel == null) {
      platformSettlementTypeLazyModel = new LazyDataModel<PlatformSettlementType>() {
        private List<PlatformSettlementType> list;

        @Override
        public List<PlatformSettlementType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PlatformSettlementTypeService.listPaged(main);
            main.commit(platformSettlementTypeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PlatformSettlementType platformSettlementType) {
          return platformSettlementType.getId();
        }

        @Override
        public PlatformSettlementType getRowData(String rowKey) {
          if (list != null) {
            for (PlatformSettlementType obj : list) {
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
    String SUB_FOLDER = "scm_platform_settlement_type/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePlatformSettlementType(MainView main) {
    return saveOrClonePlatformSettlementType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePlatformSettlementType(MainView main) {
    main.setViewType("newform");
    return saveOrClonePlatformSettlementType(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePlatformSettlementType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PlatformSettlementTypeService.insertOrUpdate(main, getPlatformSettlementType());
            break;
          case "clone":
            PlatformSettlementTypeService.clone(main, getPlatformSettlementType());
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
   * Delete one or many PlatformSettlementType.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePlatformSettlementType(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(platformSettlementTypeSelected)) {
        PlatformSettlementTypeService.deleteByPkArray(main, getPlatformSettlementTypeSelected()); //many record delete from list
        main.commit("success.delete");
        platformSettlementTypeSelected = null;
      } else {
        PlatformSettlementTypeService.deleteByPk(main, getPlatformSettlementType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PlatformSettlementType.
   *
   * @return
   */
  public LazyDataModel<PlatformSettlementType> getPlatformSettlementTypeLazyModel() {
    return platformSettlementTypeLazyModel;
  }

  /**
   * Return PlatformSettlementType[].
   *
   * @return
   */
  public PlatformSettlementType[] getPlatformSettlementTypeSelected() {
    return platformSettlementTypeSelected;
  }

  /**
   * Set PlatformSettlementType[].
   *
   * @param platformSettlementTypeSelected
   */
  public void setPlatformSettlementTypeSelected(PlatformSettlementType[] platformSettlementTypeSelected) {
    this.platformSettlementTypeSelected = platformSettlementTypeSelected;
  }

}
