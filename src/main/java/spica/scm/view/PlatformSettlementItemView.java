/*
 * @(#)PlatformSettlementItemView.java	1.0 Thu Apr 20 15:04:02 IST 2017 
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

import spica.scm.domain.PlatformSettlementItem;
import spica.scm.service.PlatformSettlementItemService;
import spica.scm.domain.PlatformSettlement;
import spica.scm.domain.Platform;

/**
 * PlatformSettlementItemView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:02 IST 2017
 */
@Named(value = "platformSettlementItemView")
@ViewScoped
public class PlatformSettlementItemView implements Serializable {

  private transient PlatformSettlementItem platformSettlementItem;	//Domain object/selected Domain.
  private transient LazyDataModel<PlatformSettlementItem> platformSettlementItemLazyModel; 	//For lazy loading datatable.
  private transient PlatformSettlementItem[] platformSettlementItemSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public PlatformSettlementItemView() {
    super();
  }

  /**
   * Return PlatformSettlementItem.
   *
   * @return PlatformSettlementItem.
   */
  public PlatformSettlementItem getPlatformSettlementItem() {
    if (platformSettlementItem == null) {
      platformSettlementItem = new PlatformSettlementItem();
    }
    return platformSettlementItem;
  }

  /**
   * Set PlatformSettlementItem.
   *
   * @param platformSettlementItem.
   */
  public void setPlatformSettlementItem(PlatformSettlementItem platformSettlementItem) {
    this.platformSettlementItem = platformSettlementItem;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPlatformSettlementItem(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getPlatformSettlementItem().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setPlatformSettlementItem((PlatformSettlementItem) PlatformSettlementItemService.selectByPk(main, getPlatformSettlementItem()));
        } else if (ViewTypes.isList(viewType)) {
          loadPlatformSettlementItemList(main);
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
   * Create platformSettlementItemLazyModel.
   *
   * @param main
   */
  private void loadPlatformSettlementItemList(final MainView main) {
    if (platformSettlementItemLazyModel == null) {
      platformSettlementItemLazyModel = new LazyDataModel<PlatformSettlementItem>() {
        private List<PlatformSettlementItem> list;

        @Override
        public List<PlatformSettlementItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PlatformSettlementItemService.listPaged(main);
            main.commit(platformSettlementItemLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PlatformSettlementItem platformSettlementItem) {
          return platformSettlementItem.getId();
        }

        @Override
        public PlatformSettlementItem getRowData(String rowKey) {
          if (list != null) {
            for (PlatformSettlementItem obj : list) {
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
    String SUB_FOLDER = "scm_platform_settlement_item/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePlatformSettlementItem(MainView main) {
    return saveOrClonePlatformSettlementItem(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePlatformSettlementItem(MainView main) {
    main.setViewType("newform");
    return saveOrClonePlatformSettlementItem(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePlatformSettlementItem(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PlatformSettlementItemService.insertOrUpdate(main, getPlatformSettlementItem());
            break;
          case "clone":
            PlatformSettlementItemService.clone(main, getPlatformSettlementItem());
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
   * Delete one or many PlatformSettlementItem.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePlatformSettlementItem(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(platformSettlementItemSelected)) {
        PlatformSettlementItemService.deleteByPkArray(main, getPlatformSettlementItemSelected()); //many record delete from list
        main.commit("success.delete");
        platformSettlementItemSelected = null;
      } else {
        PlatformSettlementItemService.deleteByPk(main, getPlatformSettlementItem());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PlatformSettlementItem.
   *
   * @return
   */
  public LazyDataModel<PlatformSettlementItem> getPlatformSettlementItemLazyModel() {
    return platformSettlementItemLazyModel;
  }

  /**
   * Return PlatformSettlementItem[].
   *
   * @return
   */
  public PlatformSettlementItem[] getPlatformSettlementItemSelected() {
    return platformSettlementItemSelected;
  }

  /**
   * Set PlatformSettlementItem[].
   *
   * @param platformSettlementItemSelected
   */
  public void setPlatformSettlementItemSelected(PlatformSettlementItem[] platformSettlementItemSelected) {
    this.platformSettlementItemSelected = platformSettlementItemSelected;
  }

  /**
   * PlatformSettlement autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.platformSettlementAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.platformSettlementAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<PlatformSettlement> platformSettlementAuto(String filter) {
    return ScmLookupView.platformSettlementAuto(filter);
  }

  /**
   * Platform autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.platformAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.platformAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Platform> platformAuto(String filter) {
    return ScmLookupView.platformAuto(filter);
  }
}
