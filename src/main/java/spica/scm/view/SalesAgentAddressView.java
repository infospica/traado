/*
 * @(#)SalesAgentAddressView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypeAction;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesAgentAddress;
import spica.scm.service.SalesAgentAddressService;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.AddressType;
import spica.scm.domain.Country;
import spica.scm.domain.State;
import spica.scm.domain.District;
import spica.scm.domain.Territory;
import wawo.app.faces.Jsf;

/**
 * SalesAgentAddressView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "salesAgentAddressView")
@ViewScoped
public class SalesAgentAddressView implements Serializable {

  private transient SalesAgentAddress salesAgentAddress;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentAddress> salesAgentAddressLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentAddress[] salesAgentAddressSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesAgentAddressView() {
    super();
  }

  /**
   * Return SalesAgentAddress.
   *
   * @return SalesAgentAddress.
   */
  public SalesAgentAddress getSalesAgentAddress() {
    if (salesAgentAddress == null) {
      salesAgentAddress = new SalesAgentAddress();
    }
    return salesAgentAddress;
  }

  /**
   * Set SalesAgentAddress.
   *
   * @param salesAgentAddress.
   */
  public void setSalesAgentAddress(SalesAgentAddress salesAgentAddress) {
    this.salesAgentAddress = salesAgentAddress;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAgentAddress(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getSalesAgentAddress().reset();
          getSalesAgentAddress().setSalesAgentId(parent);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSalesAgentAddress((SalesAgentAddress) SalesAgentAddressService.selectByPk(main, getSalesAgentAddress()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesAgentAddressList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }
  private SalesAgent parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (SalesAgent) Jsf.dialogParent(SalesAgent.class);
    getSalesAgentAddress().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create salesAgentAddressLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentAddressList(final MainView main) {
    if (salesAgentAddressLazyModel == null) {
      salesAgentAddressLazyModel = new LazyDataModel<SalesAgentAddress>() {
        private List<SalesAgentAddress> list;

        @Override
        public List<SalesAgentAddress> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAgentAddressService.listPaged(main);
            main.commit(salesAgentAddressLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgentAddress salesAgentAddress) {
          return salesAgentAddress.getId();
        }

        @Override
        public SalesAgentAddress getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentAddress obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_address/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentAddress(MainView main) {
    return saveOrCloneSalesAgentAddress(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentAddress(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesAgentAddress(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentAddress(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentAddressService.insertOrUpdate(main, getSalesAgentAddress());
//            SalesAgentAddressService.makeDefault(main, getSalesAgentAddress());
            break;
          case "clone":
            SalesAgentAddressService.clone(main, getSalesAgentAddress());
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
   * Delete one or many SalesAgentAddress.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentAddress(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesAgentAddressSelected)) {
        SalesAgentAddressService.deleteByPkArray(main, getSalesAgentAddressSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentAddressSelected = null;
      } else {
        SalesAgentAddressService.deleteByPk(main, getSalesAgentAddress());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesAgentAddress.
   *
   * @return
   */
  public LazyDataModel<SalesAgentAddress> getSalesAgentAddressLazyModel() {
    return salesAgentAddressLazyModel;
  }

  /**
   * Return SalesAgentAddress[].
   *
   * @return
   */
  public SalesAgentAddress[] getSalesAgentAddressSelected() {
    return salesAgentAddressSelected;
  }

  /**
   * Set SalesAgentAddress[].
   *
   * @param salesAgentAddressSelected
   */
  public void setSalesAgentAddressSelected(SalesAgentAddress[] salesAgentAddressSelected) {
    this.salesAgentAddressSelected = salesAgentAddressSelected;
  }

  /**
   * SalesAgent autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.salesAgentAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.salesAgentAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesAgent> salesAgentAuto(String filter) {
    return ScmLookupView.salesAgentAuto(filter);
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
//    if (getSalesAgentAddress().getCountryId()!=null) {
//      return ScmLookupExtView.addressCountryAuto(filter, getSalesAgentAddress().getCountryId().getId());
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
    if (getSalesAgentAddress().getCountryId() != null && getSalesAgentAddress().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getSalesAgentAddress().getCountryId().getId(), filter);
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
    if (getSalesAgentAddress().getStateId() != null && getSalesAgentAddress().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getSalesAgentAddress().getStateId().getId(), filter);
    }
    return null;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
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
    if (getSalesAgentAddress().getDistrictId() != null && getSalesAgentAddress().getDistrictId().getId() != null) {
      return ScmLookupExtView.addressTerritoryAuto(getSalesAgentAddress().getSalesAgentId().getCompanyId().getId(), getSalesAgentAddress().getDistrictId().getId(), filter);
    }
    return null;
  }

  public void salesAgentAddressDialogClose() {
    Jsf.returnDialog(null);
  }

  public void clearStateDistrict() {
    getSalesAgentAddress().setStateId(null);
    getSalesAgentAddress().setDistrictId(null);
    getSalesAgentAddress().setTerritoryId(null);
  }

  public void clearDistrict() {
    getSalesAgentAddress().setDistrictId(null);
    getSalesAgentAddress().setTerritoryId(null);
  }

  public void clearTerritory() {
    getSalesAgentAddress().setTerritoryId(null);
  }
}
