/*
 * @(#)StateView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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

import spica.scm.domain.State;
import spica.scm.service.StateService;
import spica.scm.domain.Country;

/**
 * StateView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "stateView")
@ViewScoped
public class StateView implements Serializable {

  private transient State state;	//Domain object/selected Domain.
  private transient LazyDataModel<State> stateLazyModel; 	//For lazy loading datatable.
  private transient State[] stateSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public StateView() {
    super();
  }

  /**
   * Return State.
   *
   * @return State.
   */
  public State getState() {
    if (state == null) {
      state = new State();
    }
    return state;
  }

  /**
   * Set State.
   *
   * @param state.
   */
  public void setState(State state) {
    this.state = state;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchState(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getState().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setState((State) StateService.selectByPk(main, getState()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadStateList(main);
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
   * Create stateLazyModel.
   *
   * @param main
   */
  private void loadStateList(final MainView main) {
    if (stateLazyModel == null) {
      stateLazyModel = new LazyDataModel<State>() {
        private List<State> list;

        @Override
        public List<State> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = StateService.listPaged(main);
            main.commit(stateLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(State state) {
          return state.getId();
        }

        @Override
        public State getRowData(String rowKey) {
          if (list != null) {
            for (State obj : list) {
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
    String SUB_FOLDER = "scm_state/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveState(MainView main) {
    return saveOrCloneState(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneState(MainView main) {
    main.setViewType("newform");
    return saveOrCloneState(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneState(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            StateService.insertOrUpdate(main, getState());
            break;
          case "clone":
            StateService.clone(main, getState());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error" + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many State.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteState(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(stateSelected)) {
        StateService.deleteByPkArray(main, getStateSelected()); //many record delete from list
        main.commit("success.delete");
        stateSelected = null;
      } else {
        StateService.deleteByPk(main, getState());  //individual record delete from list or edit form
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
   * Return LazyDataModel of State.
   *
   * @return
   */
  public LazyDataModel<State> getStateLazyModel() {
    return stateLazyModel;
  }

  /**
   * Return State[].
   *
   * @return
   */
  public State[] getStateSelected() {
    return stateSelected;
  }

  /**
   * Set State[].
   *
   * @param stateSelected
   */
  public void setStateSelected(State[] stateSelected) {
    this.stateSelected = stateSelected;
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
}
