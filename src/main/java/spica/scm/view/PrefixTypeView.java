/*
 * @(#)PrefixTypeView.java	1.0 Fri Oct 28 09:25:45 IST 2016 
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

import spica.scm.domain.PrefixType;
import spica.scm.service.PrefixTypeService;

/**
 * PrefixTypeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Oct 28 09:25:45 IST 2016
 */
@Named(value = "prefixTypeView")
@ViewScoped
public class PrefixTypeView implements Serializable {

  private transient PrefixType prefixType;	//Domain object/selected Domain.
  private transient LazyDataModel<PrefixType> prefixTypeLazyModel; 	//For lazy loading datatable.
  private transient PrefixType[] prefixTypeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PrefixTypeView() {
    super();
  }

  /**
   * Return PrefixType.
   *
   * @return PrefixType.
   */
  public PrefixType getPrefixType() {
    if (prefixType == null) {
      prefixType = new PrefixType();
    }
    return prefixType;
  }

  /**
   * Set PrefixType.
   *
   * @param prefixType.
   */
  public void setPrefixType(PrefixType prefixType) {
    this.prefixType = prefixType;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPrefixType(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getPrefixType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setPrefixType((PrefixType) PrefixTypeService.selectByPk(main, getPrefixType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPrefixTypeList(main);
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
   * Create prefixTypeLazyModel.
   *
   * @param main
   */
  private void loadPrefixTypeList(final MainView main) {
    if (prefixTypeLazyModel == null) {
      prefixTypeLazyModel = new LazyDataModel<PrefixType>() {
        private List<PrefixType> list;

        @Override
        public List<PrefixType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PrefixTypeService.listPaged(main);
            main.commit(prefixTypeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PrefixType prefixType) {
          return prefixType.getId();
        }

        @Override
        public PrefixType getRowData(String rowKey) {
          if (list != null) {
            for (PrefixType obj : list) {
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
    String SUB_FOLDER = "scm_prefix_type/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePrefixType(MainView main) {
    return saveOrClonePrefixType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePrefixType(MainView main) {
    main.setViewType("newform");
    return saveOrClonePrefixType(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePrefixType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PrefixTypeService.insertOrUpdate(main, getPrefixType());
            break;
          case "clone":
            PrefixTypeService.clone(main, getPrefixType());
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
   * Delete one or many PrefixType.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePrefixType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(prefixTypeSelected)) {
        PrefixTypeService.deleteByPkArray(main, getPrefixTypeSelected()); //many record delete from list
        main.commit("success.delete");
        prefixTypeSelected = null;
      } else {
        PrefixTypeService.deleteByPk(main, getPrefixType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PrefixType.
   *
   * @return
   */
  public LazyDataModel<PrefixType> getPrefixTypeLazyModel() {
    return prefixTypeLazyModel;
  }

  /**
   * Return PrefixType[].
   *
   * @return
   */
  public PrefixType[] getPrefixTypeSelected() {
    return prefixTypeSelected;
  }

  /**
   * Set PrefixType[].
   *
   * @param prefixTypeSelected
   */
  public void setPrefixTypeSelected(PrefixType[] prefixTypeSelected) {
    this.prefixTypeSelected = prefixTypeSelected;
  }

}
