/*
 * @(#)CustomerAddressView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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

import spica.scm.domain.CustomerAddress;
import spica.scm.service.CustomerAddressService;
import spica.scm.domain.Customer;
import spica.scm.domain.AddressType;
import spica.scm.domain.Country;
import spica.scm.domain.State;
import spica.scm.domain.District;
import spica.scm.domain.Status;
import spica.scm.domain.Territory;
import spica.scm.service.AddressTypeService;
import wawo.app.faces.Jsf;

/**
 * CustomerAddressView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "customerAddressView")
@ViewScoped
public class CustomerAddressView implements Serializable {

  private transient CustomerAddress customerAddress;	//Domain object/selected Domain.
  private transient LazyDataModel<CustomerAddress> customerAddressLazyModel; 	//For lazy loading datatable.
  private transient CustomerAddress[] customerAddressSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CustomerAddressView() {
    super();
  }

  /**
   * Return CustomerAddress.
   *
   * @return CustomerAddress.
   */
  public CustomerAddress getCustomerAddress() {
    if (customerAddress == null) {
      customerAddress = new CustomerAddress();
    }
    return customerAddress;
  }

  /**
   * Set CustomerAddress.
   *
   * @param customerAddress.
   */
  public void setCustomerAddress(CustomerAddress customerAddress) {
    this.customerAddress = customerAddress;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCustomerAddress(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCustomerAddress().reset();
          getCustomerAddress().setCustomerId(parent);
          getCustomerAddress().setCountryId(parent.getCountryId());
          getCustomerAddress().setStateId(parent.getStateId());
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCustomerAddress((CustomerAddress) CustomerAddressService.selectByPk(main, getCustomerAddress()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCustomerAddressList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }
  private Customer parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Customer) Jsf.popupParentValue(Customer.class);
    getCustomerAddress().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create customerAddressLazyModel.
   *
   * @param main
   */
  private void loadCustomerAddressList(final MainView main) {
    if (customerAddressLazyModel == null) {
      customerAddressLazyModel = new LazyDataModel<CustomerAddress>() {
        private List<CustomerAddress> list;

        @Override
        public List<CustomerAddress> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CustomerAddressService.listPaged(main);
            main.commit(customerAddressLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CustomerAddress customerAddress) {
          return customerAddress.getId();
        }

        @Override
        public CustomerAddress getRowData(String rowKey) {
          if (list != null) {
            for (CustomerAddress obj : list) {
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
    String SUB_FOLDER = "scm_customer_address/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCustomerAddress(MainView main) {
    return saveOrCloneCustomerAddress(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCustomerAddress(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCustomerAddress(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCustomerAddress(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CustomerAddressService.insertOrUpdate(main, getCustomerAddress());
//            CustomerAddressService.makeDefault(main, getCustomerAddress());
            break;
          case "clone":
            CustomerAddressService.clone(main, getCustomerAddress());
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
   * Delete one or many CustomerAddress.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCustomerAddress(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(customerAddressSelected)) {
        CustomerAddressService.deleteByPkArray(main, getCustomerAddressSelected()); //many record delete from list
        main.commit("success.delete");
        customerAddressSelected = null;
      } else {
        CustomerAddressService.deleteByPk(main, getCustomerAddress());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CustomerAddress.
   *
   * @return
   */
  public LazyDataModel<CustomerAddress> getCustomerAddressLazyModel() {
    return customerAddressLazyModel;
  }

  /**
   * Return CustomerAddress[].
   *
   * @return
   */
  public CustomerAddress[] getCustomerAddressSelected() {
    return customerAddressSelected;
  }

  /**
   * Set CustomerAddress[].
   *
   * @param customerAddressSelected
   */
  public void setCustomerAddressSelected(CustomerAddress[] customerAddressSelected) {
    this.customerAddressSelected = customerAddressSelected;
  }

  /**
   * Customer autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.customerAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.customerAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Customer> customerAuto(String filter) {
    return ScmLookupView.customerAuto(filter);
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
    return ScmLookupExtView.selectCustomerAddressType(filter, getCustomerAddress());
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

//    if (getCustomerAddress().getCountryId()!=null) {
//      return ScmLookupExtView.addressCountryAuto(filter, getCustomerAddress().getCountryId().getId());
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
//    return ScmLookupView.stateAuto(filter);
    if (getCustomerAddress().getCountryId() != null && getCustomerAddress().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getCustomerAddress().getCountryId().getId(), filter);
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
    if (getCustomerAddress().getStateId() != null && getCustomerAddress().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getCustomerAddress().getStateId().getId(), filter);
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
    if (getCustomerAddress().getDistrictId() != null && getCustomerAddress().getDistrictId().getId() != null) {
      return ScmLookupExtView.addressTerritoryAuto(getCustomerAddress().getCustomerId().getCompanyId().getId(), getCustomerAddress().getDistrictId().getId(), filter);
    }
    return null;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void customerAddressPopupClose() {
    Jsf.popupReturn(parent, getCustomerAddress());
  }

  public void clearStateDistrict() {
    getCustomerAddress().setStateId(null);
    getCustomerAddress().setDistrictId(null);
    getCustomerAddress().setTerritoryId(null);
  }

  public void clearDistrict() {
    getCustomerAddress().setDistrictId(null);
    getCustomerAddress().setTerritoryId(null);
  }

  public void clearTerritory() {
    getCustomerAddress().setTerritoryId(null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  public boolean isRegisteredAddress() {
    boolean registeredAddress = false;
    if (getCustomerAddress() != null && getCustomerAddress().getAddressTypeId() != null) {
      return (AddressTypeService.REGISTERED_ADDRESS.equals(getCustomerAddress().getAddressTypeId().getId()));
    }
    return registeredAddress;
  }
}
