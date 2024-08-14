/*
 * @(#)TransporterRateCardView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.TransporterRateCard;
import spica.scm.service.TransporterRateCardService;
import spica.scm.domain.Transporter;
import spica.scm.domain.TransportMode;

/**
 * TransporterRateCardView
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016 
 */

@Named(value="transporterRateCardView")
@ViewScoped
public class TransporterRateCardView implements Serializable{

  private transient TransporterRateCard transporterRateCard;	//Domain object/selected Domain.
  private transient LazyDataModel<TransporterRateCard> transporterRateCardLazyModel; 	//For lazy loading datatable.
  private transient TransporterRateCard[] transporterRateCardSelected;	 //Selected Domain Array
  
  /** 
   * Default Constructor.
   */   
  public TransporterRateCardView() {
    super();
  }
 
  /**
   * Return TransporterRateCard.
   * @return TransporterRateCard.
   */  
  public TransporterRateCard getTransporterRateCard() {
    if(transporterRateCard == null) {
      transporterRateCard = new TransporterRateCard();
    }
    return transporterRateCard;
  }   
  
  /**
   * Set TransporterRateCard.
   * @param transporterRateCard.
   */   
  public void setTransporterRateCard(TransporterRateCard transporterRateCard) {
    this.transporterRateCard = transporterRateCard;
  }


  
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchTransporterRateCard(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getTransporterRateCard().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setTransporterRateCard((TransporterRateCard) TransporterRateCardService.selectByPk(main, getTransporterRateCard()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTransporterRateCardList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally{
        main.close();
      }
    }
    return null;
  } 
  
  /**
   * Create transporterRateCardLazyModel.
   * @param main
   */
  private void loadTransporterRateCardList(final MainView main) {
    if (transporterRateCardLazyModel == null) {
      transporterRateCardLazyModel = new LazyDataModel<TransporterRateCard>() {
      private List<TransporterRateCard> list;      
      @Override
      public List<TransporterRateCard> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = TransporterRateCardService.listPaged(main);
          main.commit(transporterRateCardLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(TransporterRateCard transporterRateCard) {
        return transporterRateCard.getId();
      }
      @Override
        public TransporterRateCard getRowData(String rowKey) {
          if (list != null) {
            for (TransporterRateCard obj : list) {
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
    String SUB_FOLDER = "scm_transporter_rate_card/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveTransporterRateCard(MainView main) {
    return saveOrCloneTransporterRateCard(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTransporterRateCard(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTransporterRateCard(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTransporterRateCard(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TransporterRateCardService.insertOrUpdate(main, getTransporterRateCard());
            break;
          case "clone":
            TransporterRateCardService.clone(main, getTransporterRateCard());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error."+ key);
    } finally {
      main.close();
    }
    return null;
  }

  
  /**
   * Delete one or many TransporterRateCard.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTransporterRateCard(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(transporterRateCardSelected)) {
        TransporterRateCardService.deleteByPkArray(main, getTransporterRateCardSelected()); //many record delete from list
        main.commit("success.delete");
        transporterRateCardSelected = null;
      } else {
        TransporterRateCardService.deleteByPk(main, getTransporterRateCard());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())){
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
   * Return LazyDataModel of TransporterRateCard.
   * @return
   */
  public LazyDataModel<TransporterRateCard> getTransporterRateCardLazyModel() {
    return transporterRateCardLazyModel;
  }

 /**
  * Return TransporterRateCard[].
  * @return 
  */
  public TransporterRateCard[] getTransporterRateCardSelected() {
    return transporterRateCardSelected;
  }
  
  /**
   * Set TransporterRateCard[].
   * @param transporterRateCardSelected 
   */
  public void setTransporterRateCardSelected(TransporterRateCard[] transporterRateCardSelected) {
    this.transporterRateCardSelected = transporterRateCardSelected;
  }
 


 /**
  * Transporter autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.transporterAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.transporterAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<Transporter> transporterAuto(String filter) {
    return ScmLookupView.transporterAuto(filter);
  }
 /**
  * TransportMode autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.transportModeAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.transportModeAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<TransportMode> transportModeAuto(String filter) {
    return ScmLookupView.transportModeAuto(filter);
  }
    
}
