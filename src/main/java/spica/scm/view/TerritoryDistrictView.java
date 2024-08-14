/*
 * @(#)TerritoryDistrictView.java	1.0 Wed Jun 22 14:03:04 IST 2016 
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

import spica.scm.domain.TerritoryDistrict;
import spica.scm.service.TerritoryDistrictService;
import spica.scm.domain.Territory;
import spica.scm.domain.District;

/**
 * TerritoryDistrictView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Jun 22 14:03:04 IST 2016
 */
@Named(value = "territoryDistrictView")
@ViewScoped
public class TerritoryDistrictView implements Serializable {

  private transient TerritoryDistrict territoryDistrict;	//Domain object/selected Domain.
  private transient LazyDataModel<TerritoryDistrict> territoryDistrictLazyModel; 	//For lazy loading datatable.
  private transient TerritoryDistrict[] territoryDistrictSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public TerritoryDistrictView() {
    super();
  }

  /**
   * Return TerritoryDistrict.
   *
   * @return TerritoryDistrict.
   */
  public TerritoryDistrict getTerritoryDistrict() {
    if (territoryDistrict == null) {
      territoryDistrict = new TerritoryDistrict();
    }
    return territoryDistrict;
  }

  /**
   * Set TerritoryDistrict.
   *
   * @param territoryDistrict.
   */
  public void setTerritoryDistrict(TerritoryDistrict territoryDistrict) {
    this.territoryDistrict = territoryDistrict;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchTerritoryDistrict(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getTerritoryDistrict().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setTerritoryDistrict((TerritoryDistrict) TerritoryDistrictService.selectByPk(main, getTerritoryDistrict()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTerritoryDistrictList(main);
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
   * Create territoryDistrictLazyModel.
   *
   * @param main
   */
  private void loadTerritoryDistrictList(final MainView main) {
    if (territoryDistrictLazyModel == null) {
      territoryDistrictLazyModel = new LazyDataModel<TerritoryDistrict>() {
        private List<TerritoryDistrict> list;

        @Override
        public List<TerritoryDistrict> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = TerritoryDistrictService.listPaged(main);
            main.commit(territoryDistrictLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(TerritoryDistrict territoryDistrict) {
          return territoryDistrict.getId();
        }

        @Override
        public TerritoryDistrict getRowData(String rowKey) {
          if (list != null) {
            for (TerritoryDistrict obj : list) {
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
    String SUB_FOLDER = "scm_territory_district/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTerritoryDistrict(MainView main) {
    return saveOrCloneTerritoryDistrict(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTerritoryDistrict(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTerritoryDistrict(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTerritoryDistrict(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TerritoryDistrictService.insertOrUpdate(main, getTerritoryDistrict());
            break;
          case "clone":
            TerritoryDistrictService.clone(main, getTerritoryDistrict());
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
   * Delete one or many TerritoryDistrict.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTerritoryDistrict(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(territoryDistrictSelected)) {
        TerritoryDistrictService.deleteByPkArray(main, getTerritoryDistrictSelected()); //many record delete from list
        main.commit("success.delete");
        territoryDistrictSelected = null;
      } else {
        TerritoryDistrictService.deleteByPk(main, getTerritoryDistrict());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TerritoryDistrict.
   *
   * @return
   */
  public LazyDataModel<TerritoryDistrict> getTerritoryDistrictLazyModel() {
    return territoryDistrictLazyModel;
  }

  /**
   * Return TerritoryDistrict[].
   *
   * @return
   */
  public TerritoryDistrict[] getTerritoryDistrictSelected() {
    return territoryDistrictSelected;
  }

  /**
   * Set TerritoryDistrict[].
   *
   * @param territoryDistrictSelected
   */
  public void setTerritoryDistrictSelected(TerritoryDistrict[] territoryDistrictSelected) {
    this.territoryDistrictSelected = territoryDistrictSelected;
  }

  /**
   * Territory autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.territoryAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.territoryAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Territory> territoryAuto(String filter) {
    return ScmLookupView.territoryAuto(filter);
  }

  /**
   * District autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.districtAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.districtAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<District> districtAuto(String filter) {
    return ScmLookupView.districtAuto(filter);
  }
}
