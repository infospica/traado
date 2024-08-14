/*
 * @(#)TerritoryView.java	1.0 Wed Oct 19 11:15:18 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Territory;
import spica.scm.service.TerritoryService;
import spica.scm.domain.Country;
import spica.scm.domain.District;
import spica.scm.domain.State;
import spica.scm.service.DistrictService;
import spica.scm.service.StateService;
import spica.sys.UserRuntimeView;

/**
 * TerritoryView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Oct 19 11:15:18 IST 2016
 */
@Named(value = "territoryView")
@ViewScoped
public class TerritoryView implements Serializable {

  private transient Territory territory;	//Domain object/selected Domain.
  private transient LazyDataModel<Territory> territoryLazyModel; 	//For lazy loading datatable.
  private transient Territory[] territorySelected;	 //Selected Domain Array
  private List<District> districtSelected;
  private List<State> stateSelected;

  /**
   * Default Constructor.
   */
  public TerritoryView() {
    super();
  }

  /**
   * Return Territory.
   *
   * @return Territory.
   */
  public Territory getTerritory() {
    if (territory == null) {
      territory = new Territory();
    }
    return territory;
  }

  /**
   * Set Territory.
   *
   * @param territory.
   */
  public void setTerritory(Territory territory) {
    this.territory = territory;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchTerritory(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getTerritory().reset();
          districtSelected = null;
          stateSelected = null;
          getTerritory().setCountryId(UserRuntimeView.instance().getCompany().getCountryId());
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setTerritory((Territory) TerritoryService.selectByPk(main, getTerritory()));
          districtSelected = DistrictService.selectByTerritory(main, getTerritory());
          stateSelected = StateService.selectStateByTerritory(main, getTerritory());
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTerritoryList(main);
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
   * Create territoryLazyModel.
   *
   * @param main
   */
  private void loadTerritoryList(final MainView main) {
    if (territoryLazyModel == null) {
      territoryLazyModel = new LazyDataModel<Territory>() {
        private List<Territory> list;

        @Override
        public List<Territory> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = TerritoryService.listPaged(main, UserRuntimeView.instance().getCompany());
            main.commit(territoryLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Territory territory) {
          return territory.getId();
        }

        @Override
        public Territory getRowData(String rowKey) {
          if (list != null) {
            for (Territory obj : list) {
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
    String SUB_FOLDER = "scm_territory/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTerritory(MainView main) {
    return saveOrCloneTerritory(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTerritory(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTerritory(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTerritory(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            getTerritory().setCompanyId(UserRuntimeView.instance().getCompany());
            TerritoryService.insertOrUpdate(main, getTerritory(), districtSelected, stateSelected);
            break;
          case "clone":
            TerritoryService.clone(main, getTerritory(), districtSelected, stateSelected);
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
   * Delete one or many Territory.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTerritory(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(territorySelected)) {
        TerritoryService.deleteByPkArray(main, getTerritorySelected()); //many record delete from list
        main.commit("success.delete");
        territorySelected = null;
      } else {
        TerritoryService.deleteByPk(main, getTerritory());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Territory.
   *
   * @return
   */
  public LazyDataModel<Territory> getTerritoryLazyModel() {
    return territoryLazyModel;
  }

  /**
   * Return Territory[].
   *
   * @return
   */
  public Territory[] getTerritorySelected() {
    return territorySelected;
  }

  /**
   * Set Territory[].
   *
   * @param territorySelected
   */
  public void setTerritorySelected(Territory[] territorySelected) {
    this.territorySelected = territorySelected;
  }

  /**
   * Country autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.countryAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.countryAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Country> countryAuto(String filter) {
    return ScmLookupView.countryAuto(filter);
  }

  public List<State> stateAuto(String filter) {
//    return ScmLookupView.stateAuto(filter);
    if (getTerritory().getCountryId() != null && getTerritory().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getTerritory().getCountryId().getId(), filter);
    }
    return null;
  }

  public void clearStateDistrict(AjaxBehaviorEvent event) {
    stateSelected = null;
    districtSelected = null;
  }

  public List<District> districtAuto() {
    List<District> dt = new ArrayList<>();
    if (stateSelected != null) {
      for (State a : stateSelected) {
        if (a.getId() != null) {
          dt.addAll(ScmLookupExtView.districtAuto(a.getId()));
        }
      }
    }
    return dt;
  }

  public List<District> getDistrictSelected() {
    return districtSelected;
  }

  public void setDistrictSelected(List<District> districtSelected) {
    this.districtSelected = districtSelected;
  }

  public List<State> getStateSelected() {
    return stateSelected;
  }

  public void setStateSelected(List<State> stateSelected) {
    this.stateSelected = stateSelected;
  }
}
