/*
 * @(#)ConsignmentStatusView.java	1.0 Fri Jul 22 10:57:43 IST 2016 
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

import spica.scm.domain.ConsignmentStatus;
import spica.scm.service.ConsignmentStatusService;

/**
 * ConsignmentStatusView
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:43 IST 2016 
 */

@Named(value="consignmentStatusView")
@ViewScoped
public class ConsignmentStatusView implements Serializable{

  private transient ConsignmentStatus consignmentStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<ConsignmentStatus> consignmentStatusLazyModel; 	//For lazy loading datatable.
  private transient ConsignmentStatus[] consignmentStatusSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public ConsignmentStatusView() {
    super();
  }
 
  /**
   * Return ConsignmentStatus.
   * @return ConsignmentStatus.
   */  
  public ConsignmentStatus getConsignmentStatus() {
    if(consignmentStatus == null) {
      consignmentStatus = new ConsignmentStatus();
    }
    return consignmentStatus;
  }   
  
  /**
   * Set ConsignmentStatus.
   * @param consignmentStatus.
   */   
  public void setConsignmentStatus(ConsignmentStatus consignmentStatus) {
    this.consignmentStatus = consignmentStatus;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchConsignmentStatus(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getConsignmentStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setConsignmentStatus((ConsignmentStatus) ConsignmentStatusService.selectByPk(main, getConsignmentStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadConsignmentStatusList(main);
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
   * Create consignmentStatusLazyModel.
   * @param main
   */
  private void loadConsignmentStatusList(final MainView main) {
    if (consignmentStatusLazyModel == null) {
      consignmentStatusLazyModel = new LazyDataModel<ConsignmentStatus>() {
      private List<ConsignmentStatus> list;      
      @Override
      public List<ConsignmentStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = ConsignmentStatusService.listPaged(main);
          main.commit(consignmentStatusLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(ConsignmentStatus consignmentStatus) {
        return consignmentStatus.getId();
      }
      @Override
        public ConsignmentStatus getRowData(String rowKey) {
          if (list != null) {
            for (ConsignmentStatus obj : list) {
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
    String SUB_FOLDER = "scm_consignment_status/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveConsignmentStatus(MainView main) {
    return saveOrCloneConsignmentStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneConsignmentStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneConsignmentStatus(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneConsignmentStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ConsignmentStatusService.insertOrUpdate(main, getConsignmentStatus());
            break;
          case "clone":
            ConsignmentStatusService.clone(main, getConsignmentStatus());
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
   * Delete one or many ConsignmentStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteConsignmentStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(consignmentStatusSelected)) {
        ConsignmentStatusService.deleteByPkArray(main, getConsignmentStatusSelected()); //many record delete from list
        main.commit("success.delete");
        consignmentStatusSelected = null;
      } else {
        ConsignmentStatusService.deleteByPk(main, getConsignmentStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ConsignmentStatus.
   * @return
   */
  public LazyDataModel<ConsignmentStatus> getConsignmentStatusLazyModel() {
    return consignmentStatusLazyModel;
  }

 /**
  * Return ConsignmentStatus[].
  * @return 
  */
  public ConsignmentStatus[] getConsignmentStatusSelected() {
    return consignmentStatusSelected;
  }
  
  /**
   * Set ConsignmentStatus[].
   * @param consignmentStatusSelected 
   */
  public void setConsignmentStatusSelected(ConsignmentStatus[] consignmentStatusSelected) {
    this.consignmentStatusSelected = consignmentStatusSelected;
  }
 


}
