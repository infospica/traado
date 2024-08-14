/*
 * @(#)CompanyAddressView.java	1.0 Wed Mar 30 12:35:25 IST 2016
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

import spica.scm.domain.CompanyAddress;
import spica.scm.service.CompanyAddressService;
import spica.scm.domain.Company;
import spica.scm.domain.Account;
import spica.scm.domain.AddressType;
import spica.scm.domain.Country;
import spica.scm.domain.State;
import spica.scm.domain.District;
import spica.scm.domain.Status;
import spica.scm.domain.Territory;
import spica.scm.service.AddressTypeService;
import spica.scm.service.DistrictService;
import wawo.app.faces.Jsf;

/**
 * CompanyAddressView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "companyAddressView")
@ViewScoped
public class CompanyAddressView implements Serializable {

  private transient CompanyAddress companyAddress;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyAddress> companyAddressLazyModel; 	//For lazy loading datatable.
  private transient CompanyAddress[] companyAddressSelected;	 //Selected Domain Array

  private Company parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Company) Jsf.popupParentValue(Company.class);
    getCompanyAddress().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public CompanyAddressView() {
    super();
  }

  /**
   * Return CompanyAddress.
   *
   * @return CompanyAddress.
   */
  public CompanyAddress getCompanyAddress() {
    if (companyAddress == null) {
      companyAddress = new CompanyAddress();
    }
    return companyAddress;
  }

  /**
   * Set CompanyAddress.
   *
   * @param companyAddress.
   */
  public void setCompanyAddress(CompanyAddress companyAddress) {
    this.companyAddress = companyAddress;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyAddress(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCompanyAddress().reset();
          getCompanyAddress().setCompanyId(parent);
          getCompanyAddress().setCountryId(parent.getCountryId());
          getCompanyAddress().setStateId(parent.getStateId());
          getCompanyAddress().setDistrictId(DistrictService.selectByCompanyRegisteredAddress(main, parent));
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompanyAddress((CompanyAddress) CompanyAddressService.selectByPk(main, getCompanyAddress()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyAddressList(main);
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
   * Create companyAddressLazyModel.
   *
   * @param main
   */
  private void loadCompanyAddressList(final MainView main) {
    if (companyAddressLazyModel == null) {
      companyAddressLazyModel = new LazyDataModel<CompanyAddress>() {
        private List<CompanyAddress> list;

        @Override
        public List<CompanyAddress> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyAddressService.listPaged(main);
            main.commit(companyAddressLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyAddress companyAddress) {
          return companyAddress.getId();
        }

        @Override
        public CompanyAddress getRowData(String rowKey) {
          if (list != null) {
            for (CompanyAddress obj : list) {
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
    String SUB_FOLDER = "scm_company_address/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyAddress(MainView main) {
    return saveOrCloneCompanyAddress(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyAddress(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyAddress(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyAddress(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanyAddressService.insertOrUpdate(main, getCompanyAddress());
//            CompanyAddressService.makeDefault(main, getCompanyAddress());
            break;
          case "clone":
            CompanyAddressService.clone(main, getCompanyAddress());
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
   * Delete one or many CompanyAddress.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyAddress(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyAddressSelected)) {
        CompanyAddressService.deleteByPkArray(main, getCompanyAddressSelected()); //many record delete from list
        main.commit("success.delete");
        companyAddressSelected = null;
      } else {
        CompanyAddressService.deleteByPk(main, getCompanyAddress());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyAddress.
   *
   * @return
   */
  public LazyDataModel<CompanyAddress> getCompanyAddressLazyModel() {
    return companyAddressLazyModel;
  }

  /**
   * Return CompanyAddress[].
   *
   * @return
   */
  public CompanyAddress[] getCompanyAddressSelected() {
    return companyAddressSelected;
  }

  /**
   * Set CompanyAddress[].
   *
   * @param companyAddressSelected
   */
  public void setCompanyAddressSelected(CompanyAddress[] companyAddressSelected) {
    this.companyAddressSelected = companyAddressSelected;
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
   * Account autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Account> accountAuto(String filter) {
    return ScmLookupView.accountAuto(filter);
  }

  /**
   * AddressType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.addressTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.addressTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AddressType> addressTypeAuto(String filter) {
    return ScmLookupView.addressTypeAuto(filter);
  }

  public List<AddressType> lookupAddressType(String filter) {
    return ScmLookupExtView.selectCompanyAddressType(filter, getCompanyAddress());
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
    if (getCompanyAddress().getCountryId() != null && getCompanyAddress().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getCompanyAddress().getCountryId().getId(), filter);
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
    if (getCompanyAddress().getStateId() != null && getCompanyAddress().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getCompanyAddress().getStateId().getId(), filter);
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
    if (getCompanyAddress().getDistrictId() != null && getCompanyAddress().getDistrictId().getId() != null) {
      return ScmLookupExtView.addressTerritoryAuto(getCompanyAddress().getCompanyId().getId(), getCompanyAddress().getDistrictId().getId(), filter);
    }
    return null;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void companyAddressPopupClose() {
    Jsf.popupReturn(parent, getCompanyAddress());
  }

  public void clearStateDistrict() {
    getCompanyAddress().setStateId(null);
    getCompanyAddress().setDistrictId(null);
    getCompanyAddress().setTerritoryId(null);
  }

  public void clearDistrict() {
    getCompanyAddress().setDistrictId(null);
    getCompanyAddress().setTerritoryId(null);
  }

  public void clearTerritory() {
    getCompanyAddress().setTerritoryId(null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  public boolean isRegisteredAddress() {
    boolean registeredAddress = false;
    if (getCompanyAddress() != null && getCompanyAddress().getAddressTypeId() != null) {
      return (AddressTypeService.REGISTERED_ADDRESS.equals(getCompanyAddress().getAddressTypeId().getId()));
    }
    return registeredAddress;
  }
}
