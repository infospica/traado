/*
 * @(#)TransporterAddressView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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

import spica.scm.domain.TransporterAddress;
import spica.scm.service.TransporterAddressService;
import spica.scm.domain.Transporter;
import spica.scm.domain.AddressType;
import spica.scm.domain.Country;
import spica.scm.domain.State;
import spica.scm.domain.District;
import spica.scm.domain.Territory;
import spica.scm.domain.Status;
import spica.scm.service.CountryService;
import spica.scm.service.StateService;
import wawo.app.faces.Jsf;

/**
 * TransporterAddressView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "transporterAddressView")
@ViewScoped
public class TransporterAddressView implements Serializable {

  private transient TransporterAddress transporterAddress;	//Domain object/selected Domain.
  private transient LazyDataModel<TransporterAddress> transporterAddressLazyModel; 	//For lazy loading datatable.
  private transient TransporterAddress[] transporterAddressSelected;	 //Selected Domain Array

  private Transporter parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Transporter) Jsf.popupParentValue(Transporter.class);
    getTransporterAddress().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public TransporterAddressView() {
    super();
  }

  public Transporter getParent() {
    return parent;
  }

  public void setParent(Transporter parent) {
    this.parent = parent;
  }

  /**
   * Return TransporterAddress.
   *
   * @return TransporterAddress.
   */
  public TransporterAddress getTransporterAddress() {
    if (transporterAddress == null) {
      transporterAddress = new TransporterAddress();
    }
    return transporterAddress;
  }

  /**
   * Set TransporterAddress.
   *
   * @param transporterAddress.
   */
  public void setTransporterAddress(TransporterAddress transporterAddress) {
    this.transporterAddress = transporterAddress;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchTransporterAddress(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getTransporterAddress().reset();
          getTransporterAddress().setTransporterId(parent);
          getTransporterAddress().setCountryId(CountryService.selectDefaultCountry(main));
          getTransporterAddress().setStateId(StateService.selectDefaultStateByCountry(main, getTransporterAddress().getCountryId()));
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setTransporterAddress((TransporterAddress) TransporterAddressService.selectByPk(main, getTransporterAddress()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTransporterAddressList(main);
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
   * Create transporterAddressLazyModel.
   *
   * @param main
   */
  private void loadTransporterAddressList(final MainView main) {
    if (transporterAddressLazyModel == null) {
      transporterAddressLazyModel = new LazyDataModel<TransporterAddress>() {
        private List<TransporterAddress> list;

        @Override
        public List<TransporterAddress> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = TransporterAddressService.listPaged(main);
            main.commit(transporterAddressLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(TransporterAddress transporterAddress) {
          return transporterAddress.getId();
        }

        @Override
        public TransporterAddress getRowData(String rowKey) {
          if (list != null) {
            for (TransporterAddress obj : list) {
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
    String SUB_FOLDER = "scm_transporter_address/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTransporterAddress(MainView main) {
    return saveOrCloneTransporterAddress(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTransporterAddress(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTransporterAddress(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTransporterAddress(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TransporterAddressService.insertOrUpdate(main, getTransporterAddress());
//            TransporterAddressService.makeDefault(main, getTransporterAddress());
            break;
          case "clone":
            TransporterAddressService.clone(main, getTransporterAddress());
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
   * Delete one or many TransporterAddress.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTransporterAddress(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(transporterAddressSelected)) {
        TransporterAddressService.deleteByPkArray(main, getTransporterAddressSelected()); //many record delete from list
        main.commit("success.delete");
        transporterAddressSelected = null;
      } else {
        TransporterAddressService.deleteByPk(main, getTransporterAddress());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TransporterAddress.
   *
   * @return
   */
  public LazyDataModel<TransporterAddress> getTransporterAddressLazyModel() {
    return transporterAddressLazyModel;
  }

  /**
   * Return TransporterAddress[].
   *
   * @return
   */
  public TransporterAddress[] getTransporterAddressSelected() {
    return transporterAddressSelected;
  }

  /**
   * Set TransporterAddress[].
   *
   * @param transporterAddressSelected
   */
  public void setTransporterAddressSelected(TransporterAddress[] transporterAddressSelected) {
    this.transporterAddressSelected = transporterAddressSelected;
  }

  /**
   * Transporter autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.transporterAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.transporterAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Transporter> transporterAuto(String filter) {
    return ScmLookupView.transporterAuto(filter);
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
//    if (getTransporterAddress().getCountryId()!=null) {
//      return ScmLookupExtView.addressCountryAuto(filter, getTransporterAddress().getCountryId().getId());
//    } else {
//      return ScmLookupExtView.addressCountryAuto(filter, 0);
//    }
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
    if (getTransporterAddress().getCountryId() != null && getTransporterAddress().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getTransporterAddress().getCountryId().getId(), filter);
    }
    return null;
//    return ScmLookupView.stateAuto(filter);
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
//    return ScmLookupView.districtAuto(filter);
    if (getTransporterAddress().getStateId() != null && getTransporterAddress().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getTransporterAddress().getStateId().getId(), filter);
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
    if (getTransporterAddress().getDistrictId() != null && getTransporterAddress().getDistrictId().getId() != null) {
      return ScmLookupExtView.addressTerritoryAuto(getParent().getCompanyId().getId(),getTransporterAddress().getDistrictId().getId(), filter);
    }
    return null;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void transporterAddressPopupClose() {
    Jsf.popupReturn(parent, null);
  }

  public void clearStateDistrict() {
    getTransporterAddress().setStateId(null);
    getTransporterAddress().setDistrictId(null);
    getTransporterAddress().setTerritoryId(null);
  }

  public void clearDistrict() {
    getTransporterAddress().setDistrictId(null);
    getTransporterAddress().setTerritoryId(null);
  }

  public void clearTerritory() {
    getTransporterAddress().setTerritoryId(null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
}
