/*
 * @(#)ManufactureView.java	1.0 Mon Aug 21 14:02:20 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.Country;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Manufacture;
import spica.scm.service.ManufactureService;
import spica.scm.domain.State;
import spica.scm.domain.District;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * ManufactureView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 21 14:02:20 IST 2017
 */
@Named(value = "manufactureView")
@ViewScoped
public class ManufactureView implements Serializable {
  
  private transient Manufacture manufacture;	//Domain object/selected Domain.
  private transient LazyDataModel<Manufacture> manufactureLazyModel; 	//For lazy loading datatable.
  private transient Manufacture[] manufactureSelected;	 //Selected Domain Array

  @PostConstruct
  public void init() {
    manufacture = (Manufacture) Jsf.popupParentValue(Manufacture.class);
    //getGenre().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public ManufactureView() {
    super();
  }

  /**
   * Return Manufacture.
   *
   * @return Manufacture.
   */
  public Manufacture getManufacture() {
    if (manufacture == null) {
      manufacture = new Manufacture();
    }
    return manufacture;
  }

  /**
   * Set Manufacture.
   *
   * @param manufacture.
   */
  public void setManufacture(Manufacture manufacture) {
    this.manufacture = manufacture;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchManufacture(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          }
          getManufacture().reset();
          getManufacture().setCountryId(UserRuntimeView.instance().getCompany().getCountryId());
        } else if (main.isEdit() && !main.hasError()) {
          setManufacture((Manufacture) ManufactureService.selectByPk(main, getManufacture()));
        } else if (main.isList()) {
          loadManufactureList(main);
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
   * Create manufactureLazyModel.
   *
   * @param main
   */
  private void loadManufactureList(final MainView main) {
    if (manufactureLazyModel == null) {
      manufactureLazyModel = new LazyDataModel<Manufacture>() {
        private List<Manufacture> list;
        
        @Override
        public List<Manufacture> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (UserRuntimeView.instance().getCompany() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = ManufactureService.listPagedByCompany(main, UserRuntimeView.instance().getCompany());
              main.commit(manufactureLazyModel, first, pageSize);
            }
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }
        
        @Override
        public Object getRowKey(Manufacture manufacture) {
          return manufacture.getId();
        }
        
        @Override
        public Manufacture getRowData(String rowKey) {
          if (list != null) {
            for (Manufacture obj : list) {
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
    String SUB_FOLDER = "scm_manufacture/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveManufacture(MainView main) {
    return saveOrCloneManufacture(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneManufacture(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneManufacture(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneManufacture(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            if (UserRuntimeView.instance().getCompany() != null) {
              getManufacture().setCompanyId(UserRuntimeView.instance().getCompany());
            }
            getManufacture().setCode(getManufacture().getCode().toUpperCase());
            ManufactureService.insertOrUpdate(main, getManufacture());
            break;
          case "clone":
            ManufactureService.clone(main, getManufacture());
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
   * Delete one or many Manufacture.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteManufacture(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(manufactureSelected)) {
        ManufactureService.deleteByPkArray(main, getManufactureSelected()); //many record delete from list
        main.commit("success.delete");
        manufactureSelected = null;
      } else {
        ManufactureService.deleteByPk(main, getManufacture());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
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
   * Return LazyDataModel of Manufacture.
   *
   * @return
   */
  public LazyDataModel<Manufacture> getManufactureLazyModel() {
    return manufactureLazyModel;
  }

  /**
   * Return Manufacture[].
   *
   * @return
   */
  public Manufacture[] getManufactureSelected() {
    return manufactureSelected;
  }

  /**
   * Set Manufacture[].
   *
   * @param manufactureSelected
   */
  public void setManufactureSelected(Manufacture[] manufactureSelected) {
    this.manufactureSelected = manufactureSelected;
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
    if (getManufacture().getCountryId() != null && getManufacture().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getManufacture().getCountryId().getId(), filter);
    }
    return null;
  }

  /**
   * District autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.districtAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.districtAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<District> districtAuto(String filter) {
    if (getManufacture().getStateId() != null && getManufacture().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getManufacture().getStateId().getId(), filter);
    }
    return null;
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

  /**
   * Country auto complete item select event handler.
   *
   * Method resets the value of Company State object while selecting a new Country
   *
   * @param event
   */
  public void countrySelectEvent(SelectEvent event) {
    Country country = (Country) event.getObject();
    if (country != null) {
      if (getManufacture().getCountryId() == null) {
        getManufacture().setCountryId(country);
      } else if (!getManufacture().getCountryId().getId().equals(country.getId())) {
        getManufacture().setStateId(null);
        getManufacture().setDistrictId(null);
      }
    } else {
      getManufacture().setStateId(null);
      getManufacture().setDistrictId(null);
    }
  }

  /**
   * Company state select event handler.
   *
   * Method sets the selected state in company state object.
   *
   * @param event
   */
  public void stateSelectEvent(SelectEvent event) {
    State state = (State) event.getObject();
    if (state != null) {
      if (getManufacture().getStateId() == null) {
        getManufacture().setStateId(state);
      } else if (!getManufacture().getStateId().getId().equals(state.getId())) {
        getManufacture().setDistrictId(null);
      }
    } else {
      getManufacture().setDistrictId(null);
    }
  }

  /**
   * Company state select event handler.
   *
   * Method sets the selected state in company state object.
   *
   * @param event
   */
  public void districtSelectEvent(SelectEvent event) {
    District district = (District) event.getObject();
    getManufacture().setDistrictId(district);
  }
}
