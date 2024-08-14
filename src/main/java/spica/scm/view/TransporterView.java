/*
 * @(#)TransporterView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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
import spica.scm.domain.AddressType;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Transporter;
import spica.scm.service.TransporterService;
import spica.scm.domain.Company;
import spica.scm.domain.Country;
import spica.scm.domain.CountryTaxRegime;
import spica.scm.domain.District;
import spica.scm.domain.State;
import spica.scm.domain.Status;
import spica.scm.domain.TransportMode;
import spica.scm.domain.TransporterAddress;
import spica.scm.domain.TransporterContact;
import spica.scm.domain.TransporterFreightRate;
import spica.scm.domain.TransporterRateCard;
import spica.scm.service.AddressTypeService;
import spica.scm.service.CountryTaxRegimeService;
import spica.scm.service.StateService;
import spica.scm.service.StatusService;
import spica.scm.service.TransporterAddressService;
import spica.scm.service.TransporterContactService;
import spica.scm.service.TransporterFreightRateService;
import spica.scm.service.TransporterRateCardService;
import spica.scm.util.AppUtil;
import spica.scm.util.CountryTaxRegimeUtil;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * TransporterView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "transporterView")
@ViewScoped
public class TransporterView implements Serializable {

  private transient Transporter transporter;	//Domain object/selected Domain.
  private transient LazyDataModel<Transporter> transporterLazyModel; 	//For lazy loading datatable.
  private transient Transporter[] transporterSelected;	 //Selected Domain Array
  private List<TransporterAddress> transporterAddressList;
  private List<TransporterContact> transporterContactList;
  private List<TransportMode> transportModeSelected;
  private TransporterRateCard transporterRateCard;
  private List<TransporterFreightRate> transporterFreightRateList;
  int mdVal;
  private int activeIndex = 0;
  private TransporterAddress transporterAddress;
  // HashMap rateCard = null;
  private transient Integer pinLength;
  private transient Integer pinValue;
  private transient boolean gst;

  /**
   * Default Constructor.
   */
  public TransporterView() {
    super();
  }

  /**
   * Return Transporter.
   *
   * @return Transporter.
   */
  public Transporter getTransporter() {
    if (transporter == null) {
      transporter = new Transporter();
    }
    return transporter;
  }

  public TransporterAddress getTransporterAddress() {
    if (transporterAddress == null) {
      transporterAddress = new TransporterAddress();
    }
    return transporterAddress;
  }

  public void setTransporterAddress(TransporterAddress transporterAddress) {
    this.transporterAddress = transporterAddress;
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  /**
   * Set Transporter.
   *
   * @param transporter.
   */
  public void setTransporter(Transporter transporter) {
    this.transporter = transporter;
  }

  public TransporterRateCard getTransporterRateCard() {
    if (transporterRateCard == null) {
      transporterRateCard = new TransporterRateCard();
    }
    return transporterRateCard;
  }

  public void setTransporterRateCard(TransporterRateCard transporterRateCard) {
    this.transporterRateCard = transporterRateCard;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchTransporter(MainView main, String viewType) {
    //this.main = main;
    transporterContactList = null;
    transporterAddressList = null;
    transporterFreightRateList = null;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          }
          setActiveIndex(0);
          getTransporter().reset();
          transportModeSelected = null;
          setTransporterAddress(null);
          getTransporter().setCompanyId(UserRuntimeView.instance().getCompany());
          getTransporter().setCountryId(getTransporter().getCompanyId().getCountryId());
          getTransporter().setTransporterType(TransporterService.TRANSPORTER_TYPE_INDIVIDUAL);
          getTransporter().setCurrencyId(UserRuntimeView.instance().getCompany().getCurrencyId());
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setTransporter((Transporter) TransporterService.selectByPk(main, getTransporter()));
          transportModeSelected = TransporterService.selectedTransportModeByTransporter(main, getTransporter());
          setTransporterAddress(TransporterAddressService.selectTransporterRegisteredAddress(main, getTransporter()));
          if (getTransporter().getCountryId().equals(UserRuntimeService.USA)) {
            pinLength = 5;
            pinValue = 99999;
          } else {
            pinLength = 6;
            pinValue = 999999;
          }
//          setActiveIndex(0);
          checktaxregime(main, getTransporter().getCountryId());
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTransporterList(main);
          setActiveIndex(0);
          setTransporterAddress(null);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void checktaxregime(MainView main, Country country) {
    setGst(false);
    CountryTaxRegime countryTaxRegime = CountryTaxRegimeService.selectRegimeByCountry(main, country);
    if (countryTaxRegime != null && countryTaxRegime.getRegime() != null && countryTaxRegime.getRegime() == CountryTaxRegimeUtil.GST_REGIME) {
      setGst(true);
    } else {
      setGst(false);
    }
  }

  /**
   * Create transporterLazyModel.
   *
   * @param main
   */
  private void loadTransporterList(final MainView main) {
    if (transporterLazyModel == null) {
      transporterLazyModel = new LazyDataModel<Transporter>() {
        private List<Transporter> list;

        @Override
        public List<Transporter> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            if (UserRuntimeView.instance().getCompany() != null) {
              list = TransporterService.listPaged(main, UserRuntimeView.instance().getCompany());
              main.commit(transporterLazyModel, first, pageSize);
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
        public Object getRowKey(Transporter transporter) {
          return transporter.getId();
        }

        @Override
        public Transporter getRowData(String rowKey) {
          if (list != null) {
            for (Transporter obj : list) {
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
    String SUB_FOLDER = "scm_transporter/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTransporter(MainView main) {
    return saveOrCloneTransporter(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTransporter(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTransporter(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTransporter(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            getTransporter().setCompanyId(UserRuntimeView.instance().getCompany());
            if (getTransporterAddress() != null) {
              getTransporter().setTransporterAddress(getTransporterAddress().getAddress());
              getTransporter().setTransporterPin(getTransporterAddress().getPin());
            }
            TransporterService.insertOrUpdate(main, getTransporter());

            if (getTransporterAddress() != null && getTransporterAddress().getId() == null) {
              AddressType addressType = new AddressType();
              addressType.setId(AddressTypeService.REGISTERED_ADDRESS);
              getTransporterAddress().setAddressTypeId(addressType);
              getTransporterAddress().setTransporterId(getTransporter());
              getTransporterAddress().setCountryId(getTransporter().getCountryId());
              getTransporterAddress().setStateId(getTransporter().getStateId());
              getTransporterAddress().setSortOrder(1);
              Status status = new Status();
              status.setId(StatusService.STATUS_ACTIVE);
              getTransporterAddress().setStatusId(status);
            } else {
              getTransporterAddress().setCountryId(getTransporter().getCountryId());
              getTransporterAddress().setStateId(getTransporter().getStateId());
            }
            TransporterAddressService.insertOrUpdate(main, getTransporterAddress());
            break;
          case "clone":
            TransporterService.clone(main, getTransporter());
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
   * Delete one or many Transporter.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTransporter(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(transporterSelected)) {
        TransporterService.deleteByPkArray(main, getTransporterSelected()); //many record delete from list
        main.commit("success.delete");
        transporterSelected = null;
      } else {
        TransporterService.deleteByPk(main, getTransporter());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Transporter.
   *
   * @return
   */
  public LazyDataModel<Transporter> getTransporterLazyModel() {
    return transporterLazyModel;
  }

  /**
   * Return Transporter[].
   *
   * @return
   */
  public Transporter[] getTransporterSelected() {
    return transporterSelected;
  }

  /**
   * Set Transporter[].
   *
   * @param transporterSelected
   */
  public void setTransporterSelected(Transporter[] transporterSelected) {
    this.transporterSelected = transporterSelected;
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
   * Status autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.statusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.statusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  public List<TransporterAddress> getTransporterAddressList(MainView main) {
    if (transporterAddressList == null) {
      try {
        transporterAddressList = TransporterAddressService.addressListByTransporter(main, getTransporter());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return transporterAddressList;
  }

  /**
   * Return all transporter contact of a transporter.
   *
   * @param main
   * @return
   */
  public List<TransporterContact> getTransporterContactList(MainView main) {
    if (transporterContactList == null) {
      try {
        transporterContactList = TransporterContactService.contactListByTransporter(main, getTransporter());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return transporterContactList;
  }

  /**
   * Opening add new page in a dialog.
   */
  public void transporterAddressNewPopup() {
    Jsf.popupForm(FileConstant.TRANSPORTER_ADDRESS, getTransporter());
  }

  /**
   * Closing the dialog.
   *
   *
   */
  public void transporterAddressPopupReturned() {
    transporterAddressList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void transporterAddressEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.TRANSPORTER_ADDRESS, getTransporter(), id);
  }

  /**
   * Opening add new page in a dialog.
   */
  public void transporterContactNewDialog() {
    Jsf.openDialog(FileConstant.TRANSPORTER_CONTACT, getTransporter());
  }

  /**
   * Closing the dialog.
   *
   *
   */
  public void transporterContactPopupReturned() {
    transporterContactList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void transporterContactEditDialog(Integer id) {
    Jsf.openDialog(FileConstant.TRANSPORTER_CONTACT, getTransporter(), id);
  }

  public List<TransportMode> getTransportModeSelected() {
    return transportModeSelected;
  }

  public void setTransportModeSelected(List<TransportMode> transportModeSelected) {
    this.transportModeSelected = transportModeSelected;
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

//  public void transportModeEvent(AjaxBehaviorEvent event) {
//    List<TransporterRateCard> transRateCard = new ArrayList<>();
//    boolean isNew = false;
//    boolean exist = false;
//    if (transporterRateCard == null) {
//      transporterRateCard = new ArrayList<>();
////      rateCard = new HashMap<>();
//      isNew = true;
//    }
//    if (transporterRateCard.size() > transportModeSelected.size()) {
//      for (TransporterRateCard tc : transporterRateCard) {
//        for (TransportMode tm : transportModeSelected) {
//          if (tc.getTransportModeId().getId().equals(tm.getId())) {
//            transRateCard.add(tc);
//          }
//        }
//      }
//      transporterRateCard = new ArrayList<>(transRateCard);
//
//    } else {
//      for (TransportMode tm : transportModeSelected) {
//        if (isNew) {
//          TransporterRateCard trc = new TransporterRateCard();
//          trc.setTransportModeId(tm);
//          transporterRateCard.add(trc);
//        } else {
//          for (TransporterRateCard tcrr : transporterRateCard) {
//            if (tcrr.getTransportModeId().getId().equals(tm.getId())) {
//              exist = true;
//              break;
//            }
//          }
//          if (!exist) {
//            TransporterRateCard trc = new TransporterRateCard();
//            trc.setTransportModeId(tm);
//            transporterRateCard.add(trc);
//          }
//          exist = false;
//        }
//      }
//    }
//  }
  public void transporterFreightRateNewDialog() {
    Jsf.openDialog(FileConstant.TRANSPORTER_FREIGHT_RATE, getTransporterRateCard());
  }

  public void transporterFreightRateEditDialog(Integer id) {
    Jsf.openDialog(FileConstant.TRANSPORTER_FREIGHT_RATE, getTransporterRateCard(), id);
  }

  public void transporterFreightRateDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getTransporterRateCard());
    transporterFreightRateList = null;
  }
//  

  public List<TransporterFreightRate> getTransporterFreightRateList(MainView main) {
    if (transporterFreightRateList == null) {
      try {
        transporterFreightRateList = TransporterFreightRateService.getTransporterFreightRateList(main, getTransporterRateCard().getId());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return transporterFreightRateList;
  }

  public String saveTransporterRateCard(MainView main) {
    try {
      TransporterRateCardService.insertOrUpdate(main, getTransporterRateCard());
      main.commit("success.save");
      main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public void setTransporterMode(MainView main, int modeVal) {
    transporterFreightRateList = null;
    try {
      if (getTransporter() != null && getTransporter().getId() != null) {
        setTransporterRateCard(TransporterService.selectedTransportRateCardByTransporter(main, getTransporter(), modeVal));
        List<TransportMode> ctList = ScmLookupView.transportMode(main);
        for (TransportMode ct : ctList) {
          if (ct.getId() == modeVal) {
            mdVal = modeVal;
            getTransporterRateCard().setTransportModeId(ct);
            break;
          }
        }
        getTransporterRateCard().setTransporterId(getTransporter());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public String deleteTransporterRateCard(MainView main) {
    try {
      TransporterRateCardService.deleteTransporterRateCard(main, getTransporterRateCard());
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  public String deleteTransporterFreightRateValue(MainView main, TransporterFreightRate transporterFreightRate) {
    if (transporterFreightRate.getId() == null) {
      transporterFreightRateList.remove(transporterFreightRate);
    } else {
      try {
        TransporterFreightRateService.deleteByTransporterFreightRate(main, transporterFreightRate);
        main.commit("success.delete");
        transporterFreightRateList.remove(transporterFreightRate);
      } catch (Throwable t) {
        main.rollback(t, "error.delete");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public String addNewRateCard() {

    if (transporterFreightRateList != null && (transporterFreightRateList.isEmpty() || transporterFreightRateList.get(0).getId() != null)) {
      TransporterFreightRate tfr = new TransporterFreightRate();
      tfr.setTransporterRateCardId(getTransporterRateCard());
      tfr.setFreightRateByBaseUomrange(0);
//      tfr.setFreightRateFixedPerBaseuom(null);
      transporterFreightRateList.add(0, tfr);
    }
    return null;

  }

  public boolean isRateCardExist() {
    MainView main = Jsf.getMain();
    try {
      return TransporterService.selectedTransportRateCardByTransporter(main, getTransporter(), mdVal) != null;
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return false;
  }

  public void userProfileNewPopup(int k) {
    Jsf.popupList(FileConstant.USER_PROFILE, getTransporter());
  }

  public String deleteTransporterContact(MainView main, TransporterContact transporterContact) {
    if (transporterContact.getId() == null) {
      transporterContactList.remove(transporterContact);
    } else {
      try {
        TransporterContactService.deleteTransporterContact(main, transporterContact);
        main.commit("success.delete");
        transporterContactList.remove(transporterContact);
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
    if (getTransporter().getCountryId() != null && getTransporter().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getTransporter().getCountryId().getId(), filter);
    }
    return null;
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<District> districtAuto(String filter) {
    if (getTransporter().getStateId() != null && getTransporter().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getTransporter().getStateId().getId(), filter);
    }
    return null;
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
        if (getTransporter().getCountryId() == null) {
          getTransporter().setCountryId(country);
          if (!StringUtil.isEmpty(getTransporter().getGstNo())) {
            updateStateAndPan(main, getTransporter().getGstNo());
          }
        } else if (!getTransporter().getCountryId().getId().equals(country.getId())) {
          getTransporter().setCountryId(country);
          if (!StringUtil.isEmpty(getTransporter().getGstNo())) {
            updateStateAndPan(main, getTransporter().getGstNo());
          } else {
            getTransporter().setStateId(null);
          }
          getTransporterAddress().setDistrictId(null);
        }
        if (country.getCurrencyId() != null) {
          getTransporter().setCurrencyId(country.getCurrencyId());
        }
        checktaxregime(main, country);
      } else {
        getTransporter().setStateId(null);
        getTransporterAddress().setDistrictId(null);
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
      if (getTransporter().getStateId() == null) {
        getTransporter().setStateId(state);
      } else if (!getTransporter().getStateId().getId().equals(state.getId())) {
        getTransporter().setStateId(state);
        getTransporterAddress().setDistrictId(null);
      }
    } else {
      getTransporter().setStateId(null);
      getTransporterAddress().setDistrictId(null);
    }
  }

  private void updateStateAndPan(MainView main, String gstin) {
    if (AppUtil.isValidGstin(gstin)) {
      getTransporter().setPanNo(gstin.substring(2, 12));
      if (getTransporter().getCountryId() != null) {
        State state = StateService.selectStateByStateCodeAndCountry(main, getTransporter().getCountryId(), gstin.substring(0, 2));
        if (state != null) {
          getTransporter().setStateId(state);
          if (getTransporterAddress().getDistrictId() != null && !getTransporterAddress().getDistrictId().getStateId().getId().equals(state.getId())) {
            getTransporterAddress().setDistrictId(null);
          }
        } else {
          getTransporter().setStateId(null);
          getTransporterAddress().setDistrictId(null);
        }
      }
    }
  }

  /**
   * GSTIN ajax change event handler.
   */
  public void gstinChangeEventHandler() {
    MainView main = Jsf.getMain();
    try {
      if (!StringUtil.isEmpty(getTransporter().getGstNo())) {
        updateStateAndPan(main, getTransporter().getGstNo());
      } else {
        // getCompany().setStateId(null);
        //   getCompany().setPanNo(null);
        getTransporterAddress().setDistrictId(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public boolean isGst() {
    return gst;
  }

  public void setGst(boolean gst) {
    this.gst = gst;
  }
}
