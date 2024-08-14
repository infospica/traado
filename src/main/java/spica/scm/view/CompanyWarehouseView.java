/*
 * @(#)CompanyWarehouseView.java	1.0 Wed Mar 30 12:35:25 IST 2016
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.CompanyWarehouse;
import spica.scm.service.CompanyWarehouseService;
import spica.scm.domain.Company;
import spica.scm.domain.Country;
import spica.scm.domain.State;
import spica.scm.domain.District;
import spica.scm.domain.Status;
import spica.scm.domain.Territory;
import spica.scm.service.DistrictService;
import wawo.app.faces.Jsf;

/**
 * CompanyWarehouseView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "companyWarehouseView")
@ViewScoped
public class CompanyWarehouseView implements Serializable {

  private transient CompanyWarehouse companyWarehouse;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyWarehouse> companyWarehouseLazyModel; 	//For lazy loading datatable.
  private transient CompanyWarehouse[] companyWarehouseSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CompanyWarehouseView() {
    super();
  }

  /**
   * Return CompanyWarehouse.
   *
   * @return CompanyWarehouse.
   */
  public CompanyWarehouse getCompanyWarehouse() {
    if (companyWarehouse == null) {
      companyWarehouse = new CompanyWarehouse();
    }
    return companyWarehouse;
  }

  /**
   * Set CompanyWarehouse.
   *
   * @param companyWarehouse.
   */
  public void setCompanyWarehouse(CompanyWarehouse companyWarehouse) {
    this.companyWarehouse = companyWarehouse;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyWarehouse(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCompanyWarehouse().reset();
          getCompanyWarehouse().setCompanyId(parent);
          getCompanyWarehouse().setCountryId(parent.getCountryId());
          getCompanyWarehouse().setStateId(parent.getStateId());
          getCompanyWarehouse().setDistrictId(DistrictService.selectByCompanyRegisteredAddress(main, parent));
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompanyWarehouse((CompanyWarehouse) CompanyWarehouseService.selectByPk(main, getCompanyWarehouse()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyWarehouseList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }
  private Company parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Company) Jsf.popupParentValue(Company.class);
    getCompanyWarehouse().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create companyWarehouseLazyModel.
   *
   * @param main
   */
  private void loadCompanyWarehouseList(final MainView main) {
    if (companyWarehouseLazyModel == null) {
      companyWarehouseLazyModel = new LazyDataModel<CompanyWarehouse>() {
        private List<CompanyWarehouse> list;

        @Override
        public List<CompanyWarehouse> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyWarehouseService.listPaged(main);
            main.commit(companyWarehouseLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyWarehouse companyWarehouse) {
          return companyWarehouse.getId();
        }

        @Override
        public CompanyWarehouse getRowData(String rowKey) {
          if (list != null) {
            for (CompanyWarehouse obj : list) {
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
    String SUB_FOLDER = "scm_company_warehouse/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyWarehouse(MainView main) {
    return saveOrCloneCompanyWarehouse(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyWarehouse(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyWarehouse(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyWarehouse(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanyWarehouseService.insertOrUpdate(main, getCompanyWarehouse());
            break;
          case "clone":
            CompanyWarehouseService.clone(main, getCompanyWarehouse());
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
   * Delete one or many CompanyWarehouse.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyWarehouse(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyWarehouseSelected)) {
        CompanyWarehouseService.deleteByPkArray(main, getCompanyWarehouseSelected()); //many record delete from list
        main.commit("success.delete");
        companyWarehouseSelected = null;
      } else {
        CompanyWarehouseService.deleteByPk(main, getCompanyWarehouse());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyWarehouse.
   *
   * @return
   */
  public LazyDataModel<CompanyWarehouse> getCompanyWarehouseLazyModel() {
    return companyWarehouseLazyModel;
  }

  /**
   * Return CompanyWarehouse[].
   *
   * @return
   */
  public CompanyWarehouse[] getCompanyWarehouseSelected() {
    return companyWarehouseSelected;
  }

  /**
   * Set CompanyWarehouse[].
   *
   * @param companyWarehouseSelected
   */
  public void setCompanyWarehouseSelected(CompanyWarehouse[] companyWarehouseSelected) {
    this.companyWarehouseSelected = companyWarehouseSelected;
  }

  /**
   * Company autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.companyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.companyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Company> companyAuto(String filter) {
    return ScmLookupView.companyAuto(filter);
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
    if (getCompanyWarehouse().getCountryId() != null && getCompanyWarehouse().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getCompanyWarehouse().getCountryId().getId(), filter);
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
    if (getCompanyWarehouse().getStateId() != null && getCompanyWarehouse().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getCompanyWarehouse().getStateId().getId(), filter);
    }
    return null;
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
    if (getCompanyWarehouse().getDistrictId() != null && getCompanyWarehouse().getDistrictId().getId() != null) {
      return ScmLookupExtView.addressTerritoryAuto(getCompanyWarehouse().getCompanyId().getId(), getCompanyWarehouse().getDistrictId().getId(), filter);
    }
    return null;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void companyWarehouseDocPopupClose() {
    Jsf.popupReturn(parent, null);
  }

  public void clearStateDistrict() {
    getCompanyWarehouse().setStateId(null);
    getCompanyWarehouse().setDistrictId(null);
    getCompanyWarehouse().setTerritoryId(null);
  }

  public void clearDistrict() {
    getCompanyWarehouse().setDistrictId(null);
    getCompanyWarehouse().setTerritoryId(null);
  }

  public void clearTerritory() {
    getCompanyWarehouse().setTerritoryId(null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
}
