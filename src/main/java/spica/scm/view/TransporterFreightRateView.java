/*
 * @(#)TransporterFreightRateView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.TransporterFreightRate;
import spica.scm.service.TransporterFreightRateService;
import spica.scm.domain.TransporterRateCard;
import spica.scm.domain.State;
import spica.scm.domain.District;
import spica.scm.domain.TransFreightrateUomrange;
import spica.scm.domain.TransportFreightRateUom;
import spica.sys.FileConstant;
import wawo.app.faces.Jsf;
import wawo.entity.core.UserMessageException;

/**
 * TransporterFreightRateView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "transporterFreightRateView")
@ViewScoped
public class TransporterFreightRateView implements Serializable {

  private transient TransporterFreightRate transporterFreightRate;	//Domain object/selected Domain.
  private transient LazyDataModel<TransporterFreightRate> transporterFreightRateLazyModel; 	//For lazy loading datatable.
  private transient TransporterFreightRate[] transporterFreightRateSelected;	 //Selected Domain Array
  private TransFreightrateUomrange transFreightrateUomrange = null;
  private List<TransporterFreightRate> transporterFreightRateList;

  /**
   * Default Constructor.
   */
  public TransporterFreightRateView() {
    super();
  }

  public TransFreightrateUomrange getTransFreightrateUomrange() {
    if (transFreightrateUomrange == null) {
      transFreightrateUomrange = new TransFreightrateUomrange();
    }
    return transFreightrateUomrange;
  }

  public void setTransFreightrateUomrange(TransFreightrateUomrange transFreightrateUomrange) {
    this.transFreightrateUomrange = transFreightrateUomrange;
  }

  private TransporterRateCard parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (TransporterRateCard) Jsf.dialogParent(TransporterRateCard.class);
    getTransporterFreightRate().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Return TransporterFreightRate.
   *
   * @return TransporterFreightRate.
   */
  public TransporterFreightRate getTransporterFreightRate() {
    if (transporterFreightRate == null) {
      transporterFreightRate = new TransporterFreightRate();
    }
    return transporterFreightRate;
  }

  /**
   * Set TransporterFreightRate.
   *
   * @param transporterFreightRate.
   */
  public void setTransporterFreightRate(TransporterFreightRate transporterFreightRate) {
    this.transporterFreightRate = transporterFreightRate;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchTransporterFreightRate(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getTransporterFreightRate().reset();
          getTransporterFreightRate().setTransporterRateCardId(parent);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setTransporterFreightRate((TransporterFreightRate) TransporterFreightRateService.selectByPk(main, getTransporterFreightRate()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTransporterFreightRateList(main);
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
   * Create transporterFreightRateLazyModel.
   *
   * @param main
   */
  private void loadTransporterFreightRateList(final MainView main) {
    if (transporterFreightRateLazyModel == null) {
      transporterFreightRateLazyModel = new LazyDataModel<TransporterFreightRate>() {
        private List<TransporterFreightRate> list;

        @Override
        public List<TransporterFreightRate> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = TransporterFreightRateService.listPaged(main);
            main.commit(transporterFreightRateLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(TransporterFreightRate transporterFreightRate) {
          return transporterFreightRate.getId();
        }

        @Override
        public TransporterFreightRate getRowData(String rowKey) {
          if (list != null) {
            for (TransporterFreightRate obj : list) {
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
    String SUB_FOLDER = "scm_transporter_freight_rate/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTransporterFreightRate(MainView main) {
    return saveOrCloneTransporterFreightRate(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTransporterFreightRate(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTransporterFreightRate(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTransporterFreightRate(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            if (getTransporterFreightRate().getDestinationStateId() == null && getTransporterFreightRate().getDestinationDistrictId() == null) {
              throw new UserMessageException("select.source");
            }
            if (getTransporterFreightRate().getSourceStateId() == null && getTransporterFreightRate().getSourceDistrictId() == null) {
              throw new UserMessageException("select.destination");
            }
            TransporterFreightRateService.insertOrUpdate(main, getTransporterFreightRate());
            break;
          case "clone":
            TransporterFreightRateService.clone(main, getTransporterFreightRate());
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
   * Delete one or many TransporterFreightRate.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTransporterFreightRate(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(transporterFreightRateSelected)) {
        TransporterFreightRateService.deleteByPkArray(main, getTransporterFreightRateSelected()); //many record delete from list
        main.commit("success.delete");
        transporterFreightRateSelected = null;
      } else {
        TransporterFreightRateService.deleteByPk(main, getTransporterFreightRate());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TransporterFreightRate.
   *
   * @return
   */
  public LazyDataModel<TransporterFreightRate> getTransporterFreightRateLazyModel() {
    return transporterFreightRateLazyModel;
  }

  /**
   * Return TransporterFreightRate[].
   *
   * @return
   */
  public TransporterFreightRate[] getTransporterFreightRateSelected() {
    return transporterFreightRateSelected;
  }

  /**
   * Set TransporterFreightRate[].
   *
   * @param transporterFreightRateSelected
   */
  public void setTransporterFreightRateSelected(TransporterFreightRate[] transporterFreightRateSelected) {
    this.transporterFreightRateSelected = transporterFreightRateSelected;
  }

  /**
   * TransporterRateCard autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.transporterRateCardAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.transporterRateCardAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<TransporterRateCard> transporterRateCardAuto(String filter) {
    return ScmLookupView.transporterRateCardAuto(filter);
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
    return ScmLookupView.stateAuto(filter);
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
    if (getTransporterFreightRate().getDestinationStateId() != null && getTransporterFreightRate().getDestinationStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getTransporterFreightRate().getDestinationStateId().getId(), filter);
    } else {
      return ScmLookupView.districtAuto(filter);
    }
  }

  public List<District> districtAutoSource(String filter) {
    if (getTransporterFreightRate().getSourceStateId() != null && getTransporterFreightRate().getSourceStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getTransporterFreightRate().getSourceStateId().getId(), filter);
    } else {
      return ScmLookupView.districtAuto(filter);
    }
  }

  /**
   * TransportFreightRateUom autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.transportFreightRateUomAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.transportFreightRateUomAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<TransportFreightRateUom> transportFreightRateUomAuto(String filter) {
    return ScmLookupView.transportFreightRateUomAuto(filter);
  }

  public void transporterFreightRateDialogClose() {
    Jsf.returnDialog(null);
  }

  public void ajaxEventHandler(SelectEvent event) {
    System.err.println("ll");
  }
//  public void ajaxEventHandler(AjaxBehaviorEvent event) {
//    System.err.println("ll");
//  }

  public void isSourceDistrictSelect(SelectEvent event) {
    getTransporterFreightRate().setSourceStateId((State) event.getObject());
    getTransporterFreightRate().setSourceDistrictId(null);
  }

  public void isDestinationDistrictSelect(SelectEvent event) {
    getTransporterFreightRate().setDestinationStateId((State) event.getObject());
    getTransporterFreightRate().setDestinationDistrictId(null);
  }

  public void transporterFreightRateUomRangeNewPopup(TransporterFreightRate transporterFreightRate) {
    Jsf.popupForm(FileConstant.TRANSPORTER_FREIGHT_RATE_UOM_RANGE, transporterFreightRate);
  }

  public void transporterFreightRateUomRangeListPopup(TransporterFreightRate transporterFreightRate) {
    Jsf.popupList(FileConstant.TRANSPORTER_FREIGHT_RATE_UOM_RANGE, transporterFreightRate);
  }

  public void transporterUomRangeDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getTransporterFreightRate());
  }

  public void transporterUomRangeEditDialog(Integer id) {
    Jsf.openDialog("/scm/transporter/trans_freightrate_uomrange.xhtml", getTransporterFreightRate(), id);
  }

  public List<TransporterFreightRate> getTransporterFreightRateList(MainView main) {
    if (transporterFreightRateList == null) {
      try {
        transporterFreightRateList = TransporterFreightRateService.getTransporterFreightRateList(main, parent.getId());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return transporterFreightRateList;
  }

  public boolean isRange() {
    return getTransporterFreightRate().getFreightRateByBaseUomrange() != null && getTransporterFreightRate().getFreightRateByBaseUomrange() == 0;

  }

//  public boolean isRangeYes() {
//    return getTransporterFreightRate().getFreightRateByBaseUomrange() != null && getTransporterFreightRate().getFreightRateByBaseUomrange() == 1;
//
//  }
  public void onRowEdit(RowEditEvent event) {

    MainView main = Jsf.getMain();
    try {
      transporterFreightRate = (TransporterFreightRate) event.getObject();
      if (transporterFreightRate.getFreightRateByBaseUomrange() == 1) {
        transporterFreightRate.setFreightRateFixedPerBaseuom(null);
      }
      if (getTransporterFreightRate().getSourceStateId() == null && getTransporterFreightRate().getSourceDistrictId() == null) {
      main.error("select.source");
      return;
    }
    
    if (getTransporterFreightRate().getDestinationStateId() == null && getTransporterFreightRate().getDestinationDistrictId() == null) {
      main.error("select.destination");
      return;
    }
      TransporterFreightRateService.insertOrUpdate(main, transporterFreightRate);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }
}
