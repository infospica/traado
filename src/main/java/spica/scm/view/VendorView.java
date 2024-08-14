/*
 * @(#)VendorView.java	1.0 Wed Mar 30 12:35:25 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import spica.scm.domain.AccountStatus;
import spica.scm.domain.AddressType;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Country;
import spica.scm.domain.District;
import spica.scm.domain.Brand;
import spica.scm.domain.Manufacture;
import spica.scm.domain.State;
import spica.scm.domain.Status;
import spica.scm.domain.SupplierGroup;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorBank;
import spica.scm.domain.VendorContact;
import spica.scm.domain.VendorLicense;
import spica.scm.domain.VendorSalesAgent;
import spica.scm.service.AccountService;
import spica.scm.service.AddressTypeService;
import spica.scm.service.BrandService;
import spica.scm.service.CommodityService;
import spica.scm.service.DesignationService;
import spica.scm.service.ManufactureService;
import spica.scm.service.SupplierBrandService;
import spica.scm.service.StateService;
import spica.scm.service.StatusService;
import spica.scm.service.VendorAddressService;
import spica.scm.service.VendorBankService;
import spica.scm.service.VendorCommodityService;
import spica.scm.service.VendorContactService;
import spica.scm.service.VendorLicenseService;
import spica.scm.service.VendorSalesAgentService;
import spica.scm.service.VendorService;
import spica.scm.util.AppUtil;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * VendorView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "vendorView")
@ViewScoped
public class VendorView implements Serializable {

  private transient Vendor vendor;	//Domain object/selected Domain.
  private transient LazyDataModel<Vendor> vendorLazyModel; 	//For lazy loading datatable.
  private transient Vendor[] vendorSelected;	 //Selected Domain Array
  private List<VendorAddress> vendorAddressList;
  private List<VendorContact> vendorContactList;
  private List<VendorBank> vendorBankList;
  private List<ServiceCommodity> commodityList;
  private List<VendorLicense> vendorLicenseList;
  private int activeIndex = 0; //tab active index, reset to 0 when new records
  private boolean renderPrimaryVendor;
  private boolean editable = true;
  private transient VendorAddress vendorRegAddress;
  private transient Integer pinLength;
  private transient Integer pinValue;
  private transient boolean manufacturer;
  private transient Manufacture manufacture;
  private transient boolean accountExist;
  private transient Brand defaultBrand;
  private transient List<Brand> brandList;
  private transient Brand brand;
  private transient Boolean removableFlag;

  @PostConstruct
  public void init() {
    Integer id = (Integer) Jsf.popupParentValue(Integer.class);
    if (id != null) {
      getVendor().setId(id);
    }
  }

  /**
   * Default Constructor.
   */
  public VendorView() {
    super();
  }

  /**
   * Return Vendor.
   *
   * @return Vendor.
   */
  public Vendor getVendor() {
    if (vendor == null) {
      vendor = new Vendor();
    }
    if (vendor.getCompanyId() == null) {
      vendor.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return vendor;
  }

  public VendorAddress getVendorRegAddress() {
    if (vendorRegAddress == null) {
      vendorRegAddress = new VendorAddress();
    }
    return vendorRegAddress;
  }

  public void setVendorRegAddress(VendorAddress vendorRegAddress) {
    this.vendorRegAddress = vendorRegAddress;
  }

  public boolean isManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(boolean manufacturer) {
    this.manufacturer = manufacturer;
  }

  /**
   * Set Vendor.
   *
   * @param vendor.
   */
  public void setVendor(Vendor vendor) {
    this.vendor = vendor;
  }

  public Manufacture getManufacture() {
    if (manufacture == null) {
      manufacture = new Manufacture();
    }
    manufacture.setCompanyId(getVendor().getCompanyId());
    return manufacture;
  }

  public void setManufacture(Manufacture manufacture) {
    this.manufacture = manufacture;
  }

  public boolean isRenderPrimaryVendor() {
    return renderPrimaryVendor;
  }

  public void setRenderPrimaryVendor(boolean renderPrimaryVendor) {
    this.renderPrimaryVendor = renderPrimaryVendor;
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public boolean isAccountExist() {
    return accountExist;
  }

  public void setAccountExist(boolean accountExist) {
    this.accountExist = accountExist;
  }

  public Brand getDefaultBrand() {
    return defaultBrand;
  }

  public void setDefaultBrand(Brand defaultBrand) {
    this.defaultBrand = defaultBrand;
  }

  public List<Brand> getBrandList() {
    if (brandList == null) {
      brandList = new ArrayList<>();
    }
    return brandList;
  }

  public void setBrandList(List<Brand> brandList) {
    this.brandList = brandList;
    if (getRemovableFlag() != null && !getRemovableFlag()) {
      if (brand != null && brand.getId() != null) {
        brandList.add(brand);
        Jsf.getMain().error("brand.cant.remove");
      }
    }
    setBrand(null);
  }

  public Brand getBrand() {
    return brand;
  }

  public void setBrand(Brand brand) {
    this.brand = brand;
  }

  public Boolean getRemovableFlag() {
    return removableFlag;
  }

  public void setRemovableFlag(Boolean removableFlag) {
    this.removableFlag = removableFlag;
  }

  /**
   * Method returns vendor type list. 1. Primary 2. Secondary
   *
   * @return
   */
  public List<SelectItem> getVendorType() {
    List<SelectItem> list = new ArrayList<>();
    SelectItem item;
    String types[];
    for (String vtype : VendorService.VENDOR_TYPE) {
      types = vtype.split(":");
      item = new SelectItem();
      item.setIntValue(Integer.parseInt(types[0]));
      item.setItemLabel(types[1]);
      list.add(item);
    }
    return list;
  }

  /**
   * Method to lookup for primary vendors of current company.
   *
   * @return
   */
  public List<Vendor> selectPrimaryVendor() {
    List<Vendor> list = ScmLookupExtView.selectPrimaryVendors(getVendor(), getVendor().getCompanyId());
    return list;
  }

  /**
   * Vendor Type select one menu item select event handler. renderPrimaryVendor
   *
   * @param event
   */
  public void vendorTypeSelectEvent(SelectEvent event) {
    Integer vtype = (Integer) event.getObject();
    setRenderPrimaryVendor(false);
    if (vtype == 1) {
      getVendor().setVendorType(vtype);
      getVendor().setVendorId(null);
      getVendor().setSupplierGroupId(null);
      setCommodityList(null);
    } else if (vtype == 2) {
      setRenderPrimaryVendor(true);
      getVendor().setVendorId(null);
      setCommodityList(null);
    }
  }

  /**
   * Primary vendor select one menu item select event handler.
   *
   * @param event
   */
  public void primaryVendorSelectEvent(SelectEvent event) {
    Vendor vd = (Vendor) event.getObject();
    if (vd != null) {
      if (vd.getSupplierGroupId() != null) {
        getVendor().setSupplierGroupId(vd.getSupplierGroupId());
      } else {
        getVendor().setSupplierGroupId(null);
      }
      setCommodityList(ScmLookupExtView.selectCommodityByVendor(vd));
    } else {
      setCommodityList(null);
      getVendor().setVendorId(null);
      getVendor().setSupplierGroupId(null);
    }
  }

  public boolean isSecondaryVendor() {
    boolean rvalue = false;
    if (getVendor() != null && getVendor().getVendorType() != null) {
      rvalue = getVendor().getVendorType().equals(VendorService.VENDOR_TYPE_SECONDARY);
    }
    return rvalue;
  }

  /**
   * Method to select the list of commodity.
   *
   * @return
   */
  public List<ServiceCommodity> selectCommodity(MainView main) {
    List<ServiceCommodity> list = null;
    if (getVendor().getCompanyId() != null) {
      list = ScmLookupExtView.lookupCommodityByCountryAndCompany(getVendor().getCompanyId());

      if (main.isEdit() && !StringUtil.isEmpty(getCommodityList())) {
        for (ServiceCommodity com : getCommodityList()) {
          for (ServiceCommodity comm : list) {
            if (com.getId().equals(comm.getId())) {
              comm.setPurchased(com.isPurchased());
              break;
            }
          }
        }
      }
    }
    return list;
  }

  public String actionUpdateVendorView() {
    return null;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchVendor(MainView main, String viewType) {
    vendorAddressList = null;  // Set to null to fetch the latest record
    vendorContactList = null;
    vendorBankList = null;
    vendorLicenseList = null;
    salesAgentList = null;
//this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        if (UserRuntimeView.instance().getCompany() == null) {
          main.error("company.required");
          main.setViewType(ViewTypes.list);
          return null;
        } else if (getVendor() != null && main.isNew()) {
          // To do Commodity validation.
        }
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          setActiveIndex(0);
          getVendor().reset();
          setVendorRegAddress(null);
          setRenderPrimaryVendor(false);
          setCommodityList(null);
          setBrandList(null);
          getVendor().setTaxApplicable(0);
          getVendor().setCountryId(getVendor().getCompanyId().getCountryId());
          getVendor().setCurrencyId(getVendor().getCompanyId().getCountryId().getCurrencyId());
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setVendor((Vendor) VendorService.selectByPk(main, getVendor()));
          brandList = SupplierBrandService.selectBrandByVendor(main, getVendor());
          if (getVendor() != null && getVendor().getIsManufacturer() != null && getVendor().getIsManufacturer().equals(VendorService.IS_MANUFACTURER)) {
            setManufacture(ManufactureService.selectByVendor(main, getVendor()));
            setManufacturer(false);
          }
          if (getVendor().getCountryId().equals(UserRuntimeService.USA)) {
            pinLength = 5;
            pinValue = 99999;
          } else {
            pinLength = 6;
            pinValue = 999999;
          }
          setVendorRegAddress(VendorAddressService.selectDefaultVendorAddress(main, getVendor()));
          setEditable(VendorService.isVendorHasAccountOrSecondaryVendor(main, getVendor()));
          setAccountExist(AccountService.isVendorHaveAccount(main, getVendor()));
          setRenderPrimaryVendor(false);
          if (getVendor().getVendorType().equals(VendorService.VENDOR_TYPE_SECONDARY)) {
            setRenderPrimaryVendor(true);
          }
          setCommodityList(CommodityService.selectByVendor(main, getVendor()));
        } else if (ViewType.list.toString().equals(viewType)) {
          setActiveIndex(0);
          setEditable(true);
          setAccountExist(false);
          setRenderPrimaryVendor(false);
          setVendorRegAddress(null);
          setManufacture(null);
          setManufacturer(false);
          setBrandList(null);
          loadVendorList(main);
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
   * Create vendorLazyModel.
   *
   * @param main
   */
  private void loadVendorList(final MainView main) {
    if (vendorLazyModel == null) {
      vendorLazyModel = new LazyDataModel<Vendor>() {
        private List<Vendor> list;

        @Override
        public List<Vendor> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            if (UserRuntimeView.instance().getCompany() != null) {
              list = VendorService.listPaged(main, getVendor().getCompanyId());
              main.commit(vendorLazyModel, first, pageSize);
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
        public Object getRowKey(Vendor vendor) {
          return vendor.getId();
        }

        @Override
        public Vendor getRowData(String rowKey) {
          if (list != null) {
            for (Vendor obj : list) {
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
    String SUB_FOLDER = "scm_vendor/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveVendor(MainView main) {
    return saveOrCloneVendor(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneVendor(MainView main) {
    main.setViewType("newform");
    return saveOrCloneVendor(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneVendor(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            if (getVendor().getVendorType().equals(VendorService.VENDOR_TYPE_PRIMARY)) {
              getVendor().setVendorId(null);
            }
            if (getVendorRegAddress() != null) {
              getVendor().setVendorAddress(getVendorRegAddress().getAddress());
              getVendor().setVendorPin(getVendorRegAddress().getPin());
            }
            if (isManufacturer()) {
              getVendor().setIsManufacturer(VendorService.IS_MANUFACTURER);
            }
            VendorService.insertOrUpdate(main, getVendor());

            VendorCommodityService.insertVendorCommodity(main, getVendor(), getCommodityList());

            if (getVendorRegAddress() != null && getVendorRegAddress().getId() == null) {
              AddressType addressType = new AddressType();
              addressType.setId(AddressTypeService.REGISTERED_ADDRESS);
              getVendorRegAddress().setAddressTypeId(addressType);
              getVendorRegAddress().setVendorId(getVendor());
              getVendorRegAddress().setCountryId(getVendor().getCountryId());
              getVendorRegAddress().setStateId(getVendor().getStateId());
              getVendorRegAddress().setSortOrder(1);
              Status status = new Status();
              status.setId(StatusService.STATUS_ACTIVE);
              getVendorRegAddress().setStatusId(status);
            } else {
              getVendorRegAddress().setCountryId(getVendor().getCountryId());
              getVendorRegAddress().setStateId(getVendor().getStateId());
            }
            VendorAddressService.insertOrUpdate(main, getVendorRegAddress());

            if (getVendor().getIsManufacturer() != null && getVendor().getIsManufacturer() == VendorService.IS_MANUFACTURER) {
              ManufactureService.insertOrUpdateVendorAsManufacturer(main, getManufacture(), getVendor(), getVendorRegAddress());
            }

            SupplierBrandService.insertOrUpdate(main, getVendor(), getBrandList());

            break;
          case "clone":
            VendorService.clone(main, getVendor());
            VendorCommodityService.insertVendorCommodity(main, getVendor(), getCommodityList());
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
   * Delete one or many Vendor.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteVendor(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(vendorSelected)) {
        VendorService.deleteByPkArray(main, getVendorSelected()); //many record delete from list
        main.commit("success.delete");
        vendorSelected = null;
      } else {
        VendorService.deleteByPk(main, getVendor());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Vendor.
   *
   * @return
   */
  public LazyDataModel<Vendor> getVendorLazyModel() {
    return vendorLazyModel;
  }

  /**
   * Return Vendor[].
   *
   * @return
   */
  public Vendor[] getVendorSelected() {
    return vendorSelected;
  }

  /**
   * Set Vendor[].
   *
   * @param vendorSelected
   */
  public void setVendorSelected(Vendor[] vendorSelected) {
    this.vendorSelected = vendorSelected;
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<Country> countryAuto(String filter) {
    return ScmLookupView.countryAuto(filter);
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<State> stateAuto(String filter) {
    if (getVendor().getCountryId() != null && getVendor().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getVendor().getCountryId().getId(), filter);
    }
    return null;
  }

  public List<District> districtAuto(String filter) {
    if (getVendor().getStateId() != null && getVendor().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getVendor().getStateId().getId(), filter);
    }
    return null;
  }

  public List<VendorLicense> getVendorLicenseList(MainView main) {
    if (vendorLicenseList == null) {
      try {
        vendorLicenseList = VendorLicenseService.licenseListByVendor(main, getVendor());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return vendorLicenseList;
  }

  public void setVendorLicenseList(List<VendorLicense> vendorLicenseList) {
    this.vendorLicenseList = vendorLicenseList;
  }

  /**
   * Return all address of a vendor.
   *
   * @param main
   * @return
   */
  public List<VendorAddress> getVendorAddressList(MainView main) {
    if (vendorAddressList == null) {
      try {
        vendorAddressList = VendorAddressService.addressListByVendor(main, getVendor());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return vendorAddressList;
  }

  /**
   * Return all contact of a vendor.
   *
   * @param main
   * @return
   */
  public List<VendorContact> getVendorContactList(MainView main) {
    if (vendorContactList == null) {
      try {
        vendorContactList = VendorContactService.contactListByVendor(main, getVendor());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return vendorContactList;
  }

  /**
   * Return all vendor bank of a vendor.
   *
   * @param main
   * @return
   */
  public List<VendorBank> getVendorBankList(MainView main) {
    if (vendorBankList == null) {
      try {
        vendorBankList = VendorBankService.bankListByVendor(main, getVendor());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return vendorBankList;
  }

  public void vendorCommoditySelectDialog() {
    Jsf.popupList(FileConstant.VENDOR_COMMODITY, getVendor());
  }

  public void vendorCommodityDialogReturn() {
    Jsf.closeDialog(getVendor());
    setCommodityList(null); // Reset to null to fetch updated list
  }

  public String deleteVendorCommodity(MainView main, ServiceCommodity commodity) {
    try {
      VendorCommodityService.deleteVendorCommodityRelation(main, commodity, getVendor());
      setCommodityList(null);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Opening add new page in a dialog.
   */
  public void vendorAddressNewDialog() {
    Jsf.popupForm(FileConstant.VENDOR_ADDRESS, getVendor());
  }

  /**
   * Closing the dialog.
   *
   */
  public void vendorAddressDialogReturn() {
    VendorAddress address = (VendorAddress) Jsf.popupParentValue(VendorAddress.class);
    if (address != null && address.getAddressTypeId() != null && address.getAddressTypeId().getId().equals(AddressTypeService.REGISTERED_ADDRESS)) {
      setVendorRegAddress(address);
    }
    vendorAddressList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void vendorAddressEditDialog(Integer id) {
    Jsf.popupForm(FileConstant.VENDOR_ADDRESS, getVendor(), id);
  }

  /**
   * Opening add new page in a dialog.
   */
  public void vendorContactNewDialog() {
    Jsf.openDialog(FileConstant.VENDOR_CONTACT, getVendor());
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void vendorContactEditDialog(Integer id) {
    Jsf.openDialog(FileConstant.VENDOR_CONTACT, getVendor(), id);
  }

  /**
   * Closing the dialog.
   *
   */
  public void vendorContactDialogReturn() {
    vendorContactList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening add new page in a dialog.
   */
  public void vendorBankNewDialog() {
    Jsf.popupForm(FileConstant.VENDOR_BANK, getVendor());
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void vendorBankEditDialog(Integer id) {
    Jsf.popupForm(FileConstant.VENDOR_BANK, getVendor(), id);
  }

  /**
   * Closing the dialog.
   *
   */
  public void vendorBankDialogReturn() {
    vendorBankList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening add new page in a dialog.
   */
  public void vendorLicenseNewDialog() {
    Jsf.popupForm(FileConstant.VENDOR_LICENSE, getVendor());
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void vendorLicenseEditDialog(Integer id) {
    Jsf.popupForm(FileConstant.VENDOR_LICENSE, getVendor(), id);
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param vendorBank
   */
  public void bankContactDialog(VendorBank vendorBank) {
    Jsf.popupList(FileConstant.VENDOR_BANK_CONTACT, vendorBank);
  }

  /**
   *
   * @param vendorBank
   */
  public void bankContactDialogNewForm(VendorBank vendorBank) {
    Jsf.popupForm(FileConstant.VENDOR_BANK_CONTACT, vendorBank);
  }

  /**
   * Closing the dialog.
   *
   */
  public void vendorLicenseDialogReturn() {
    vendorLicenseList = null; // Reset to null to fetch updated list
  }

  public void userProfileNewDialog(int k) {
    Jsf.popupList(FileConstant.USER_PROFILE, getVendor());
  }

  public void userProfileNewPopup(int k) {
    getVendor().setFlag(k);
    Jsf.popupList(FileConstant.USER_PROFILE, getVendor());
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  public Integer getPinLength() {
    return pinLength;
  }

  public void setPinLength(Integer pinLength) {
    this.pinLength = pinLength;
  }

  public Integer getPinValue() {
    return pinValue;
  }

  public void setPinValue(Integer pinValue) {
    this.pinValue = pinValue;
  }

  /**
   * BusinessModel autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.businessModelAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.businessModelAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
//  public List<BusinessModel> businessModelAuto(String filter) {
//    return ScmLookupView.businessModelAuto(filter);
//  }
  /**
   * AccountStatus autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountStatusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountStatus> accountStatusAuto(String filter) {
    return ScmLookupView.accountStatusAuto(filter);
  }

  public void setCommodityList(List<ServiceCommodity> commodityList) {
    this.commodityList = commodityList;
  }

  public List<ServiceCommodity> getCommodityList() {
    return commodityList;
  }

  public String deleteVendorContact(MainView main, VendorContact vendorContact) {
    if (vendorContact.getId() == null) {
      vendorContactList.remove(vendorContact);
    } else {
      try {
        VendorContactService.deleteVendorContact(main, vendorContact);
        main.commit("success.delete");
        vendorContactList.remove(vendorContact);
      } catch (Throwable t) {
        main.rollback(t, "error.delete");
      } finally {
        main.close();
      }
    }
    return null;
  }

  private List<VendorSalesAgent> salesAgentList;

  public void vendorSalesAgentPopupReturned() {
    salesAgentList = null; // Reset to null to fetch updated list
  }

  public List<VendorSalesAgent> getVendorSalesAgentList(MainView main) {
    if (salesAgentList == null) {
      try {
        salesAgentList = VendorSalesAgentService.vendorSalesAgentContarctListByCompany(main, getVendor());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return salesAgentList;
  }

  public String deleteVendorSalesAgent(MainView main, VendorSalesAgent vendorSalesAgent) {
    if (vendorSalesAgent.getId() == null) {
      salesAgentList.remove(vendorSalesAgent);
    } else {
      try {
        VendorSalesAgentService.deleteVendorSalesAgent(main, vendorSalesAgent);
        main.commit("success.delete");
        salesAgentList.remove(vendorSalesAgent);
      } catch (Throwable t) {
        main.rollback(t, "error.delete");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   *
   * @param desigantionId
   * @return
   */
  public boolean isSalesAgent(Integer desigantionId) {
    return (desigantionId != null && DesignationService.VENDOR_SALES_AGENT.equals(desigantionId));
  }

  /**
   * GSTIN ajax change event handler.
   */
  public void gstinChangeEventHandler() {
    if (!StringUtil.isEmpty(getVendor().getGstNo())) {
      MainView main = Jsf.getMain();
      try {
        updateStateAndPan(main, getVendor().getGstNo());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    } else {
      getVendor().getCompanyId().setStateId(null);
      getVendor().getCompanyId().setPanNo(null);
      getVendorRegAddress().setDistrictId(null);
    }
  }

  private void updateStateAndPan(MainView main, String gstin) {
    if (AppUtil.isValidGstin(gstin)) {
      getVendor().setPanNo(gstin.substring(2, 12));
      if (getVendor().getCountryId() != null) {
        State state = StateService.selectStateByStateCodeAndCountry(main, getVendor().getCountryId(), gstin.substring(0, 2));
        if (state != null) {
          getVendor().setStateId(state);
          if (getVendorRegAddress().getDistrictId() != null && !getVendorRegAddress().getDistrictId().getStateId().getId().equals(state.getId())) {
            getVendorRegAddress().setDistrictId(null);
          }
        } else {
          getVendor().setStateId(null);
          getVendorRegAddress().setDistrictId(null);
        }
      }
    }
  }

  /**
   *
   * @param event
   */
  public void countrySelectEvent(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      Country country = (Country) event.getObject();
      if (country != null) {
        if (country.equals(UserRuntimeService.USA)) {
          pinLength = 5;
          pinValue = 99999;
        } else {
          pinLength = 6;
          pinValue = 999999;
        }
        if (getVendor().getCountryId() == null) {
          getVendor().setCountryId(country);
          if (!StringUtil.isEmpty(getVendor().getGstNo())) {
            updateStateAndPan(main, getVendor().getGstNo());
          }
        } else if (!getVendor().getCountryId().getId().equals(country.getId())) {
          getVendor().setCountryId(country);
          if (!StringUtil.isEmpty(getVendor().getGstNo())) {
            updateStateAndPan(main, getVendor().getGstNo());
          } else {
            getVendor().setStateId(null);
          }
          getVendorRegAddress().setDistrictId(null);
        }
        if (country.getCurrencyId() != null) {
          getVendor().setCurrencyId(country.getCurrencyId());
        }
      } else {
        getVendor().setStateId(null);
        getVendorRegAddress().setDistrictId(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param event
   */
  public void stateSelectEvent(SelectEvent event) {
    State state = (State) event.getObject();
    if (state != null) {
      if (getVendor().getStateId() == null) {
        getVendor().setStateId(state);
      } else if (!getVendor().getStateId().getId().equals(state.getId())) {
        getVendor().setStateId(state);
        getVendorRegAddress().setDistrictId(null);
      }
    } else {
      getVendor().setStateId(null);
      getVendorRegAddress().setDistrictId(null);
    }
  }

  public void openCommodityPopup() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.COMMODITY, new ServiceCommodity(), null); // opens a new form if id is null else edit
    }
  }

  public List<SupplierGroup> lookupSupplierGroupByCompany() {
    List<SupplierGroup> list = null;
    if (getVendor().getCompanyId() != null) {
      list = ScmLookupExtView.lookupSupplierGroup(getVendor().getCompanyId());
    }
    return list;
  }

  public void openManufacturerPopup() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.MANUFACTURER, getManufacture(), getManufacture() == null ? null : getManufacture().getId());
    }
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<Brand> brandByCompanyAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      if (getBrandList() != null && !getBrandList().isEmpty()) {
        if (defaultBrand == null) {
          defaultBrand = BrandService.selectByPk(main, getBrandList().get(0));
        }
      }
      return ScmLookupExtView.brandByCompanyAuto(filter, getVendor().getCompanyId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param event
   */
  public void onBrandSelect(SelectEvent event) {
    Set<Brand> tempSet = new HashSet<>();
    List<Brand> tempList;
    if (getBrandList().size() > 1) {
      for (Brand brand : getBrandList()) {
        tempSet.add(brand);
      }
      tempList = new ArrayList<>(tempSet);
      setBrandList(tempList);
    }
  }

  /**
   *
   * @param event
   */
  public void onBrandUnSelect(UnselectEvent event) {
    brand = (Brand) event.getObject();
    MainView main = Jsf.getMain();
    try {
      removableFlag = VendorService.isRemovableBrand(main, brand, getVendor());
      if (getBrandList().size() == 1 && removableFlag) {
        getBrandList().clear();
        setDefaultBrand(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param context
   * @param component
   * @param value
   * @throws ValidatorException
   */
  public void vendorValidation(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    List<Brand> list = (List<Brand>) value;
    String validateParam = (String) Jsf.getParameter("validateManufactures");
    if (!StringUtil.isEmpty(validateParam)) {
      if (list != null && list.size() <= 1) {
        Jsf.error(component, "error.vendor.exist");
      } else if (list != null && list.size() > 1) {
        MainView main = Jsf.getMain();
        try {
          long exist = VendorService.isVendorExist(main, getVendor().getId(), list);
          if (exist > 0) {
            Jsf.error(component, "error.vendor.exist");
          }
        } catch (Throwable t) {
          main.rollback(t, "error.select");
        } finally {
          main.close();
        }
      }
    }
  }

  //FIXME move all these to popupview and use integer id instead of object
  public void countryOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.BASIC_COUNTRY, new District()); // opens a new form if id is null else edit
    }
  }

  public void stateOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.BASIC_STATE, new State()); // opens a new form if id is null else edit
    }
  }

  public void districtOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.BASIC_DISTRICT, new District()); // opens a new form if id is null else edit
    }
  }

  public void supplierGroupOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.SUPPLIER_GROUP, new SupplierGroup()); // opens a new form if id is null else edit
    }
  }

  public void brandOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.BASIC_BRAND, new Brand()); // opens a new form if id is null else edit
    }
  }

  public List<SupplierGroup> supplierGroupAuto(String filter) {
    return ScmLookupExtView.supplierGroupAutoByComapny(getVendor().getCompanyId(), filter);
  }
}
