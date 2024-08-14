/*
 * @(#)TransportFreightRatebaseView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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

import spica.scm.domain.TransportFreightRatebase;
import spica.scm.service.TransportFreightRatebaseService;

/**
 * TransportFreightRatebaseView
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016 
 */

@Named(value="transportFreightRatebaseView")
@ViewScoped
public class TransportFreightRatebaseView implements Serializable{

  private transient TransportFreightRatebase transportFreightRatebase;	//Domain object/selected Domain.
  private transient LazyDataModel<TransportFreightRatebase> transportFreightRatebaseLazyModel; 	//For lazy loading datatable.
  private transient TransportFreightRatebase[] transportFreightRatebaseSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public TransportFreightRatebaseView() {
    super();
  }
 
  /**
   * Return TransportFreightRatebase.
   * @return TransportFreightRatebase.
   */  
  public TransportFreightRatebase getTransportFreightRatebase() {
    if(transportFreightRatebase == null) {
      transportFreightRatebase = new TransportFreightRatebase();
    }
    return transportFreightRatebase;
  }   
  
  /**
   * Set TransportFreightRatebase.
   * @param transportFreightRatebase.
   */   
  public void setTransportFreightRatebase(TransportFreightRatebase transportFreightRatebase) {
    this.transportFreightRatebase = transportFreightRatebase;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchTransportFreightRatebase(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getTransportFreightRatebase().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setTransportFreightRatebase((TransportFreightRatebase) TransportFreightRatebaseService.selectByPk(main, getTransportFreightRatebase()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTransportFreightRatebaseList(main);
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
   * Create transportFreightRatebaseLazyModel.
   * @param main
   */
  private void loadTransportFreightRatebaseList(final MainView main) {
    if (transportFreightRatebaseLazyModel == null) {
      transportFreightRatebaseLazyModel = new LazyDataModel<TransportFreightRatebase>() {
      private List<TransportFreightRatebase> list;      
      @Override
      public List<TransportFreightRatebase> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = TransportFreightRatebaseService.listPaged(main);
          main.commit(transportFreightRatebaseLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(TransportFreightRatebase transportFreightRatebase) {
        return transportFreightRatebase.getId();
      }
      @Override
        public TransportFreightRatebase getRowData(String rowKey) {
          if (list != null) {
            for (TransportFreightRatebase obj : list) {
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
    String SUB_FOLDER = "scm_transport_freight_ratebase/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveTransportFreightRatebase(MainView main) {
    return saveOrCloneTransportFreightRatebase(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTransportFreightRatebase(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTransportFreightRatebase(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTransportFreightRatebase(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TransportFreightRatebaseService.insertOrUpdate(main, getTransportFreightRatebase());
            break;
          case "clone":
            TransportFreightRatebaseService.clone(main, getTransportFreightRatebase());
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
   * Delete one or many TransportFreightRatebase.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTransportFreightRatebase(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(transportFreightRatebaseSelected)) {
        TransportFreightRatebaseService.deleteByPkArray(main, getTransportFreightRatebaseSelected()); //many record delete from list
        main.commit("success.delete");
        transportFreightRatebaseSelected = null;
      } else {
        TransportFreightRatebaseService.deleteByPk(main, getTransportFreightRatebase());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TransportFreightRatebase.
   * @return
   */
  public LazyDataModel<TransportFreightRatebase> getTransportFreightRatebaseLazyModel() {
    return transportFreightRatebaseLazyModel;
  }

 /**
  * Return TransportFreightRatebase[].
  * @return 
  */
  public TransportFreightRatebase[] getTransportFreightRatebaseSelected() {
    return transportFreightRatebaseSelected;
  }
  
  /**
   * Set TransportFreightRatebase[].
   * @param transportFreightRatebaseSelected 
   */
  public void setTransportFreightRatebaseSelected(TransportFreightRatebase[] transportFreightRatebaseSelected) {
    this.transportFreightRatebaseSelected = transportFreightRatebaseSelected;
  }
 


}
