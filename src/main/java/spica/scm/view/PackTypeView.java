/*
 * @(#)PackTypeView.java	1.0 Thu Aug 25 13:58:48 IST 2016 
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

import spica.scm.domain.PackType;
import spica.scm.service.PackTypeService;

/**
 * PackTypeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Aug 25 13:58:48 IST 2016
 */
@Named(value = "packTypeView")
@ViewScoped
public class PackTypeView implements Serializable {

  private transient PackType packType;	//Domain object/selected Domain.
  private transient LazyDataModel<PackType> packTypeLazyModel; 	//For lazy loading datatable.
  private transient PackType[] packTypeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PackTypeView() {
    super();
  }

  /**
   * Return PackType.
   *
   * @return PackType.
   */
  public PackType getPackType() {
    if (packType == null) {
      packType = new PackType();
    }
    return packType;
  }

  /**
   * Set PackType.
   *
   * @param packType.
   */
  public void setPackType(PackType packType) {
    this.packType = packType;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPackType(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getPackType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setPackType((PackType) PackTypeService.selectByPk(main, getPackType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPackTypeList(main);
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
   * Create packTypeLazyModel.
   *
   * @param main
   */
  private void loadPackTypeList(final MainView main) {
    if (packTypeLazyModel == null) {
      packTypeLazyModel = new LazyDataModel<PackType>() {
        private List<PackType> list;

        @Override
        public List<PackType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PackTypeService.listPaged(main);
            main.commit(packTypeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PackType packType) {
          return packType.getId();
        }

        @Override
        public PackType getRowData(String rowKey) {
          if (list != null) {
            for (PackType obj : list) {
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
    String SUB_FOLDER = "scm_pack_type/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePackType(MainView main) {
    return saveOrClonePackType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePackType(MainView main) {
    main.setViewType("newform");
    return saveOrClonePackType(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePackType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PackTypeService.insertOrUpdate(main, getPackType());
            break;
          case "clone":
            PackTypeService.clone(main, getPackType());
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
   * Delete one or many PackType.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePackType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(packTypeSelected)) {
        PackTypeService.deleteByPkArray(main, getPackTypeSelected()); //many record delete from list
        main.commit("success.delete");
        packTypeSelected = null;
      } else {
        PackTypeService.deleteByPk(main, getPackType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PackType.
   *
   * @return
   */
  public LazyDataModel<PackType> getPackTypeLazyModel() {
    return packTypeLazyModel;
  }

  /**
   * Return PackType[].
   *
   * @return
   */
  public PackType[] getPackTypeSelected() {
    return packTypeSelected;
  }

  /**
   * Set PackType[].
   *
   * @param packTypeSelected
   */
  public void setPackTypeSelected(PackType[] packTypeSelected) {
    this.packTypeSelected = packTypeSelected;
  }

}
