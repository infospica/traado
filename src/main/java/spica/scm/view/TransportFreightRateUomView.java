/*
 * @(#)TransportFreightRateUomView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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

import spica.scm.domain.TransportFreightRateUom;
import spica.scm.service.TransportFreightRateUomService;
import spica.scm.domain.TransportFreightRatebase;

/**
 * TransportFreightRateUomView
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016 
 */

@Named(value="transportFreightRateUomView")
@ViewScoped
public class TransportFreightRateUomView implements Serializable{

  private transient TransportFreightRateUom transportFreightRateUom;	//Domain object/selected Domain.
  private transient LazyDataModel<TransportFreightRateUom> transportFreightRateUomLazyModel; 	//For lazy loading datatable.
  private transient TransportFreightRateUom[] transportFreightRateUomSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public TransportFreightRateUomView() {
    super();
  }
 
  /**
   * Return TransportFreightRateUom.
   * @return TransportFreightRateUom.
   */  
  public TransportFreightRateUom getTransportFreightRateUom() {
    if(transportFreightRateUom == null) {
      transportFreightRateUom = new TransportFreightRateUom();
    }
    return transportFreightRateUom;
  }   
  
  /**
   * Set TransportFreightRateUom.
   * @param transportFreightRateUom.
   */   
  public void setTransportFreightRateUom(TransportFreightRateUom transportFreightRateUom) {
    this.transportFreightRateUom = transportFreightRateUom;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchTransportFreightRateUom(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getTransportFreightRateUom().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setTransportFreightRateUom((TransportFreightRateUom) TransportFreightRateUomService.selectByPk(main, getTransportFreightRateUom()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTransportFreightRateUomList(main);
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
   * Create transportFreightRateUomLazyModel.
   * @param main
   */
  private void loadTransportFreightRateUomList(final MainView main) {
    if (transportFreightRateUomLazyModel == null) {
      transportFreightRateUomLazyModel = new LazyDataModel<TransportFreightRateUom>() {
      private List<TransportFreightRateUom> list;      
      @Override
      public List<TransportFreightRateUom> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = TransportFreightRateUomService.listPaged(main);
          main.commit(transportFreightRateUomLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(TransportFreightRateUom transportFreightRateUom) {
        return transportFreightRateUom.getId();
      }
      @Override
        public TransportFreightRateUom getRowData(String rowKey) {
          if (list != null) {
            for (TransportFreightRateUom obj : list) {
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
    String SUB_FOLDER = "scm_transport_freight_rate_uom/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveTransportFreightRateUom(MainView main) {
    return saveOrCloneTransportFreightRateUom(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTransportFreightRateUom(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTransportFreightRateUom(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTransportFreightRateUom(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TransportFreightRateUomService.insertOrUpdate(main, getTransportFreightRateUom());
            break;
          case "clone":
            TransportFreightRateUomService.clone(main, getTransportFreightRateUom());
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
   * Delete one or many TransportFreightRateUom.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTransportFreightRateUom(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(transportFreightRateUomSelected)) {
        TransportFreightRateUomService.deleteByPkArray(main, getTransportFreightRateUomSelected()); //many record delete from list
        main.commit("success.delete");
        transportFreightRateUomSelected = null;
      } else {
        TransportFreightRateUomService.deleteByPk(main, getTransportFreightRateUom());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TransportFreightRateUom.
   * @return
   */
  public LazyDataModel<TransportFreightRateUom> getTransportFreightRateUomLazyModel() {
    return transportFreightRateUomLazyModel;
  }

 /**
  * Return TransportFreightRateUom[].
  * @return 
  */
  public TransportFreightRateUom[] getTransportFreightRateUomSelected() {
    return transportFreightRateUomSelected;
  }
  
  /**
   * Set TransportFreightRateUom[].
   * @param transportFreightRateUomSelected 
   */
  public void setTransportFreightRateUomSelected(TransportFreightRateUom[] transportFreightRateUomSelected) {
    this.transportFreightRateUomSelected = transportFreightRateUomSelected;
  }
 


 /**
  * TransportFreightRatebase autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.transportFreightRatebaseAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.transportFreightRatebaseAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<TransportFreightRatebase> transportFreightRatebaseAuto(String filter) {
    return ScmLookupView.transportFreightRatebaseAuto(filter);
  }
}
