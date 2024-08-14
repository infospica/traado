/*
 * @(#)DistrictView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.Country;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.District;
import spica.scm.service.DistrictService;
import spica.scm.domain.State;

/**
 * DistrictView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "districtView")
@ViewScoped
public class DistrictView implements Serializable {

  private transient District district;	//Domain object/selected Domain.
  private transient LazyDataModel<District> districtLazyModel; 	//For lazy loading datatable.
  private transient District[] districtSelected;	 //Selected Domain Array
  private transient Country country;

  /**
   * Default Constructor.
   */
  public DistrictView() {
    super();
  }

  /**
   * Return District.
   *
   * @return District.
   */
  public District getDistrict() {
    if (district == null) {
      district = new District();
    }
    return district;
  }

  /**
   * Set District.
   *
   * @param district.
   */
  public void setDistrict(District district) {
    this.district = district;
  }

  /**
   *
   * @return
   */
  public Country getCountry() {
    return country;
  }

  /**
   *
   * @param country
   */
  public void setCountry(Country country) {
    this.country = country;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchDistrict(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getDistrict().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setDistrict((District) DistrictService.selectByPk(main, getDistrict()));
          setCountry(getDistrict().getStateId().getCountryId());
        } else if (ViewType.list.toString().equals(viewType)) {
          loadDistrictList(main);
          setCountry(null);
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
   * Create districtLazyModel.
   *
   * @param main
   */
  private void loadDistrictList(final MainView main) {
    if (districtLazyModel == null) {
      districtLazyModel = new LazyDataModel<District>() {
        private List<District> list;

        @Override
        public List<District> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = DistrictService.listPaged(main);
            main.commit(districtLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(District district) {
          return district.getId();
        }

        @Override
        public District getRowData(String rowKey) {
          if (list != null) {
            for (District obj : list) {
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
    String SUB_FOLDER = "scm_district/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveDistrict(MainView main) {
    return saveOrCloneDistrict(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneDistrict(MainView main) {
    main.setViewType("newform");
    return saveOrCloneDistrict(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneDistrict(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            DistrictService.insertOrUpdate(main, getDistrict());
            break;
          case "clone":
            DistrictService.clone(main, getDistrict());
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
   * Delete one or many District.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteDistrict(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(districtSelected)) {
        DistrictService.deleteByPkArray(main, getDistrictSelected()); //many record delete from list
        main.commit("success.delete");
        districtSelected = null;
      } else {
        DistrictService.deleteByPk(main, getDistrict());  //individual record delete from list or edit form
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
   * Return LazyDataModel of District.
   *
   * @return
   */
  public LazyDataModel<District> getDistrictLazyModel() {
    return districtLazyModel;
  }

  /**
   * Return District[].
   *
   * @return
   */
  public District[] getDistrictSelected() {
    return districtSelected;
  }

  /**
   * Set District[].
   *
   * @param districtSelected
   */
  public void setDistrictSelected(District[] districtSelected) {
    this.districtSelected = districtSelected;
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
    if (getCountry() != null) {
      return ScmLookupExtView.lookupStateByCountry(getCountry(), filter);
    }
    return null;
  }

  public void countrySelectEvent(SelectEvent event) {
    Country countryId = (Country) event.getObject();
    if (countryId != null) {
      if (getDistrict().getStateId() != null && !getDistrict().getStateId().getCountryId().getId().equals(countryId.getId())) {
        getDistrict().setStateId(null);
      }
    } else {
      getDistrict().setStateId(null);
    }
  }
}
