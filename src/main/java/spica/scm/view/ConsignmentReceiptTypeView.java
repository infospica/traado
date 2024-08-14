/*
 * @(#)ConsignmentReceiptTypeView.java	1.0 Fri Jul 22 10:57:43 IST 2016 
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

import spica.scm.domain.ConsignmentReceiptType;
import spica.scm.service.ConsignmentReceiptTypeService;
import spica.scm.domain.TransportMode;

/**
 * ConsignmentReceiptTypeView
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:43 IST 2016 
 */

@Named(value="consignmentReceiptTypeView")
@ViewScoped
public class ConsignmentReceiptTypeView implements Serializable{

  private transient ConsignmentReceiptType consignmentReceiptType;	//Domain object/selected Domain.
  private transient LazyDataModel<ConsignmentReceiptType> consignmentReceiptTypeLazyModel; 	//For lazy loading datatable.
  private transient ConsignmentReceiptType[] consignmentReceiptTypeSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public ConsignmentReceiptTypeView() {
    super();
  }
 
  /**
   * Return ConsignmentReceiptType.
   * @return ConsignmentReceiptType.
   */  
  public ConsignmentReceiptType getConsignmentReceiptType() {
    if(consignmentReceiptType == null) {
      consignmentReceiptType = new ConsignmentReceiptType();
    }
    return consignmentReceiptType;
  }   
  
  /**
   * Set ConsignmentReceiptType.
   * @param consignmentReceiptType.
   */   
  public void setConsignmentReceiptType(ConsignmentReceiptType consignmentReceiptType) {
    this.consignmentReceiptType = consignmentReceiptType;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchConsignmentReceiptType(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getConsignmentReceiptType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setConsignmentReceiptType((ConsignmentReceiptType) ConsignmentReceiptTypeService.selectByPk(main, getConsignmentReceiptType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadConsignmentReceiptTypeList(main);
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
   * Create consignmentReceiptTypeLazyModel.
   * @param main
   */
  private void loadConsignmentReceiptTypeList(final MainView main) {
    if (consignmentReceiptTypeLazyModel == null) {
      consignmentReceiptTypeLazyModel = new LazyDataModel<ConsignmentReceiptType>() {
      private List<ConsignmentReceiptType> list;      
      @Override
      public List<ConsignmentReceiptType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = ConsignmentReceiptTypeService.listPaged(main);
          main.commit(consignmentReceiptTypeLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(ConsignmentReceiptType consignmentReceiptType) {
        return consignmentReceiptType.getId();
      }
      @Override
        public ConsignmentReceiptType getRowData(String rowKey) {
          if (list != null) {
            for (ConsignmentReceiptType obj : list) {
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
    String SUB_FOLDER = "scm_consignment_receipt_type/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveConsignmentReceiptType(MainView main) {
    return saveOrCloneConsignmentReceiptType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneConsignmentReceiptType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneConsignmentReceiptType(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneConsignmentReceiptType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ConsignmentReceiptTypeService.insertOrUpdate(main, getConsignmentReceiptType());
            break;
          case "clone":
            ConsignmentReceiptTypeService.clone(main, getConsignmentReceiptType());
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
   * Delete one or many ConsignmentReceiptType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteConsignmentReceiptType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(consignmentReceiptTypeSelected)) {
        ConsignmentReceiptTypeService.deleteByPkArray(main, getConsignmentReceiptTypeSelected()); //many record delete from list
        main.commit("success.delete");
        consignmentReceiptTypeSelected = null;
      } else {
        ConsignmentReceiptTypeService.deleteByPk(main, getConsignmentReceiptType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ConsignmentReceiptType.
   * @return
   */
  public LazyDataModel<ConsignmentReceiptType> getConsignmentReceiptTypeLazyModel() {
    return consignmentReceiptTypeLazyModel;
  }

 /**
  * Return ConsignmentReceiptType[].
  * @return 
  */
  public ConsignmentReceiptType[] getConsignmentReceiptTypeSelected() {
    return consignmentReceiptTypeSelected;
  }
  
  /**
   * Set ConsignmentReceiptType[].
   * @param consignmentReceiptTypeSelected 
   */
  public void setConsignmentReceiptTypeSelected(ConsignmentReceiptType[] consignmentReceiptTypeSelected) {
    this.consignmentReceiptTypeSelected = consignmentReceiptTypeSelected;
  }
 


 /**
  * TransportMode autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookup.transportModeAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookup.transportModeAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<TransportMode> transportModeAuto(String filter) {
    return ScmLookupView.transportModeAuto(filter);
  }
}
