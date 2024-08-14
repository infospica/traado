/*
 * @(#)TransportModeView.java	1.0 Fri Jul 22 10:57:43 IST 2016 
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

import spica.scm.domain.TransportMode;
import spica.scm.service.TransportModeService;

/**
 * TransportModeView
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:43 IST 2016 
 */

@Named(value="transportModeView")
@ViewScoped
public class TransportModeView implements Serializable{

  private transient TransportMode transportMode;	//Domain object/selected Domain.
  private transient LazyDataModel<TransportMode> transportModeLazyModel; 	//For lazy loading datatable.
  private transient TransportMode[] transportModeSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public TransportModeView() {
    super();
  }
 
  /**
   * Return TransportMode.
   * @return TransportMode.
   */  
  public TransportMode getTransportMode() {
    if(transportMode == null) {
      transportMode = new TransportMode();
    }
    return transportMode;
  }   
  
  /**
   * Set TransportMode.
   * @param transportMode.
   */   
  public void setTransportMode(TransportMode transportMode) {
    this.transportMode = transportMode;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchTransportMode(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getTransportMode().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setTransportMode((TransportMode) TransportModeService.selectByPk(main, getTransportMode()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTransportModeList(main);
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
   * Create transportModeLazyModel.
   * @param main
   */
  private void loadTransportModeList(final MainView main) {
    if (transportModeLazyModel == null) {
      transportModeLazyModel = new LazyDataModel<TransportMode>() {
      private List<TransportMode> list;      
      @Override
      public List<TransportMode> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = TransportModeService.listPaged(main);
          main.commit(transportModeLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(TransportMode transportMode) {
        return transportMode.getId();
      }
      @Override
        public TransportMode getRowData(String rowKey) {
          if (list != null) {
            for (TransportMode obj : list) {
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
    String SUB_FOLDER = "scm_transport_mode/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveTransportMode(MainView main) {
    return saveOrCloneTransportMode(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTransportMode(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTransportMode(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTransportMode(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TransportModeService.insertOrUpdate(main, getTransportMode());
            break;
          case "clone":
            TransportModeService.clone(main, getTransportMode());
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
   * Delete one or many TransportMode.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTransportMode(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(transportModeSelected)) {
        TransportModeService.deleteByPkArray(main, getTransportModeSelected()); //many record delete from list
        main.commit("success.delete");
        transportModeSelected = null;
      } else {
        TransportModeService.deleteByPk(main, getTransportMode());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TransportMode.
   * @return
   */
  public LazyDataModel<TransportMode> getTransportModeLazyModel() {
    return transportModeLazyModel;
  }

 /**
  * Return TransportMode[].
  * @return 
  */
  public TransportMode[] getTransportModeSelected() {
    return transportModeSelected;
  }
  
  /**
   * Set TransportMode[].
   * @param transportModeSelected 
   */
  public void setTransportModeSelected(TransportMode[] transportModeSelected) {
    this.transportModeSelected = transportModeSelected;
  }
 


}
