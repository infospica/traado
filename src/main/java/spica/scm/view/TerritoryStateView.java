/*
 * @(#)TerritoryStateView.java	1.0 Tue May 24 12:29:16 IST 2016 
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

import spica.scm.domain.TerritoryState;
import spica.scm.service.TerritoryStateService;
import spica.scm.domain.Territory;
import spica.scm.domain.State;

/**
 * TerritoryStateView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 24 12:29:16 IST 2016
 */
@Named(value = "territoryStateView")
@ViewScoped
public class TerritoryStateView implements Serializable {

  private transient TerritoryState territoryState;	//Domain object/selected Domain.
  private transient LazyDataModel<TerritoryState> territoryStateLazyModel; 	//For lazy loading datatable.
  private transient TerritoryState[] territoryStateSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public TerritoryStateView() {
    super();
  }

  /**
   * Return TerritoryState.
   *
   * @return TerritoryState.
   */
  public TerritoryState getTerritoryState() {
    if (territoryState == null) {
      territoryState = new TerritoryState();
    }
    return territoryState;
  }

  /**
   * Set TerritoryState.
   *
   * @param territoryState.
   */
  public void setTerritoryState(TerritoryState territoryState) {
    this.territoryState = territoryState;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchTerritoryState(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getTerritoryState().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setTerritoryState((TerritoryState) TerritoryStateService.selectByPk(main, getTerritoryState()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTerritoryStateList(main);
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
   * Create territoryStateLazyModel.
   *
   * @param main
   */
  private void loadTerritoryStateList(final MainView main) {
    if (territoryStateLazyModel == null) {
      territoryStateLazyModel = new LazyDataModel<TerritoryState>() {
        private List<TerritoryState> list;

        @Override
        public List<TerritoryState> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = TerritoryStateService.listPaged(main);
            main.commit(territoryStateLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(TerritoryState territoryState) {
          return territoryState.getId();
        }

        @Override
        public TerritoryState getRowData(String rowKey) {
          if (list != null) {
            for (TerritoryState obj : list) {
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
    String SUB_FOLDER = "scm_territory_state/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTerritoryState(MainView main) {
    return saveOrCloneTerritoryState(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTerritoryState(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTerritoryState(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTerritoryState(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TerritoryStateService.insertOrUpdate(main, getTerritoryState());
            break;
          case "clone":
            TerritoryStateService.clone(main, getTerritoryState());
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
   * Delete one or many TerritoryState.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTerritoryState(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(territoryStateSelected)) {
        TerritoryStateService.deleteByPkArray(main, getTerritoryStateSelected()); //many record delete from list
        main.commit("success.delete");
        territoryStateSelected = null;
      } else {
        TerritoryStateService.deleteByPk(main, getTerritoryState());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TerritoryState.
   *
   * @return
   */
  public LazyDataModel<TerritoryState> getTerritoryStateLazyModel() {
    return territoryStateLazyModel;
  }

  /**
   * Return TerritoryState[].
   *
   * @return
   */
  public TerritoryState[] getTerritoryStateSelected() {
    return territoryStateSelected;
  }

  /**
   * Set TerritoryState[].
   *
   * @param territoryStateSelected
   */
  public void setTerritoryStateSelected(TerritoryState[] territoryStateSelected) {
    this.territoryStateSelected = territoryStateSelected;
  }

  /**
   * Territory autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.territoryAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.territoryAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Territory> territoryAuto(String filter) {
    return ScmLookupView.territoryAuto(filter);
  }

  /**
   * State autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.stateAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.stateAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<State> stateAuto(String filter) {
    return ScmLookupView.stateAuto(filter);
  }
}
