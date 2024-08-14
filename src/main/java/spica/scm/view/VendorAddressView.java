/*
 * @(#)VendorAddressView.java	1.0 Wed Mar 30 12:35:25 IST 2016
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

import spica.scm.domain.VendorAddress;
import spica.scm.service.VendorAddressService;
import spica.scm.domain.Vendor;
import spica.scm.domain.AddressType;
import spica.scm.domain.Account;
import spica.scm.domain.Country;
import spica.scm.domain.State;
import spica.scm.domain.District;
import spica.scm.domain.Status;
import spica.scm.domain.Territory;
import spica.scm.service.AddressTypeService;
import wawo.app.faces.Jsf;

/**
 * VendorAddressView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "vendorAddressView")
@ViewScoped
public class VendorAddressView implements Serializable {

  private transient VendorAddress vendorAddress;	//Domain object/selected Domain.
  private transient LazyDataModel<VendorAddress> vendorAddressLazyModel; 	//For lazy loading datatable.
  private transient VendorAddress[] vendorAddressSelected;	 //Selected Domain Array

  private Vendor parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Vendor) Jsf.popupParentValue(Vendor.class);
    getVendorAddress().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public VendorAddressView() {
    super();
  }

  /**
   * Return VendorAddress.
   *
   * @return VendorAddress.
   */
  public VendorAddress getVendorAddress() {
    if (vendorAddress == null) {
      vendorAddress = new VendorAddress();
    }
    return vendorAddress;
  }

  /**
   * Set VendorAddress.
   *
   * @param vendorAddress.
   */
  public void setVendorAddress(VendorAddress vendorAddress) {
    this.vendorAddress = vendorAddress;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchVendorAddress(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getVendorAddress().reset();
          getVendorAddress().setVendorId(parent);
          getVendorAddress().setCountryId(parent.getCountryId());
          getVendorAddress().setStateId(parent.getStateId());
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setVendorAddress((VendorAddress) VendorAddressService.selectByPk(main, getVendorAddress()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadVendorAddressList(main);
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
   * Create vendorAddressLazyModel.
   *
   * @param main
   */
  private void loadVendorAddressList(final MainView main) {
    if (vendorAddressLazyModel == null) {
      vendorAddressLazyModel = new LazyDataModel<VendorAddress>() {
        private List<VendorAddress> list;

        @Override
        public List<VendorAddress> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = VendorAddressService.listPaged(main);
            main.commit(vendorAddressLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(VendorAddress vendorAddress) {
          return vendorAddress.getId();
        }

        @Override
        public VendorAddress getRowData(String rowKey) {
          if (list != null) {
            for (VendorAddress obj : list) {
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
    String SUB_FOLDER = "scm_vendor_address/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveVendorAddress(MainView main) {
    return saveOrCloneVendorAddress(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneVendorAddress(MainView main) {
    main.setViewType("newform");
    return saveOrCloneVendorAddress(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneVendorAddress(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            VendorAddressService.insertOrUpdate(main, getVendorAddress());
//            VendorAddressService.makeDefault(main, getVendorAddress());
            break;
          case "clone":
            VendorAddressService.clone(main, getVendorAddress());
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
   * Delete one or many VendorAddress.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteVendorAddress(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(vendorAddressSelected)) {
        VendorAddressService.deleteByPkArray(main, getVendorAddressSelected()); //many record delete from list
        main.commit("success.delete");
        vendorAddressSelected = null;
      } else {
        VendorAddressService.deleteByPk(main, getVendorAddress());  //individual record delete from list or edit form
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
   * Return LazyDataModel of VendorAddress.
   *
   * @return
   */
  public LazyDataModel<VendorAddress> getVendorAddressLazyModel() {
    return vendorAddressLazyModel;
  }

  /**
   * Return VendorAddress[].
   *
   * @return
   */
  public VendorAddress[] getVendorAddressSelected() {
    return vendorAddressSelected;
  }

  /**
   * Set VendorAddress[].
   *
   * @param vendorAddressSelected
   */
  public void setVendorAddressSelected(VendorAddress[] vendorAddressSelected) {
    this.vendorAddressSelected = vendorAddressSelected;
  }

  /**
   * Vendor autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.vendorAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.vendorAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Vendor> vendorAuto(String filter) {
    return ScmLookupView.vendorAuto(filter);
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
    return ScmLookupExtView.selectVendorAddressType(filter, getVendorAddress());
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
//    return ScmLookupView.stateAuto(filter);
    if (getVendorAddress().getCountryId() != null && getVendorAddress().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getVendorAddress().getCountryId().getId(), filter);
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
//    return ScmLookupView.districtAuto(filter);
    if (getVendorAddress().getStateId() != null && getVendorAddress().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getVendorAddress().getStateId().getId(), filter);
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
    if (getVendorAddress().getDistrictId() != null && getVendorAddress().getDistrictId().getId() != null) {
      return ScmLookupExtView.addressTerritoryAuto(getVendorAddress().getVendorId().getCompanyId().getId(), getVendorAddress().getDistrictId().getId(), filter);
    }
    return null;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void vendorAddressDialogClose() {
    Jsf.popupReturn(parent, getVendorAddress());
  }

  public void clearStateDistrict() {
    getVendorAddress().setStateId(null);
    getVendorAddress().setDistrictId(null);
    getVendorAddress().setTerritoryId(null);
  }

  public void clearDistrict() {
    getVendorAddress().setDistrictId(null);
    getVendorAddress().setTerritoryId(null);
  }

  public void clearTerritory() {
    getVendorAddress().setTerritoryId(null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  public boolean isRegisteredAddress() {
    boolean registeredAddress = false;
    if (getVendorAddress() != null && getVendorAddress().getAddressTypeId() != null) {
      return (AddressTypeService.REGISTERED_ADDRESS.equals(getVendorAddress().getAddressTypeId().getId()));
    }
    return registeredAddress;
  }
}
