/*
 * @(#)ConsignmentDocTypeView.java	1.0 Fri Jul 22 10:57:43 IST 2016 
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

import spica.scm.domain.ConsignmentDocType;
import spica.scm.service.ConsignmentDocTypeService;
import spica.scm.domain.TransportMode;

/**
 * ConsignmentDocTypeView
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:43 IST 2016 
 */

@Named(value="consignmentDocTypeView")
@ViewScoped
public class ConsignmentDocTypeView implements Serializable{

  private transient ConsignmentDocType consignmentDocType;	//Domain object/selected Domain.
  private transient LazyDataModel<ConsignmentDocType> consignmentDocTypeLazyModel; 	//For lazy loading datatable.
  private transient ConsignmentDocType[] consignmentDocTypeSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public ConsignmentDocTypeView() {
    super();
  }
 
  /**
   * Return ConsignmentDocType.
   * @return ConsignmentDocType.
   */  
  public ConsignmentDocType getConsignmentDocType() {
    if(consignmentDocType == null) {
      consignmentDocType = new ConsignmentDocType();
    }
    return consignmentDocType;
  }   
  
  /**
   * Set ConsignmentDocType.
   * @param consignmentDocType.
   */   
  public void setConsignmentDocType(ConsignmentDocType consignmentDocType) {
    this.consignmentDocType = consignmentDocType;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchConsignmentDocType(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getConsignmentDocType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setConsignmentDocType((ConsignmentDocType) ConsignmentDocTypeService.selectByPk(main, getConsignmentDocType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadConsignmentDocTypeList(main);
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
   * Create consignmentDocTypeLazyModel.
   * @param main
   */
  private void loadConsignmentDocTypeList(final MainView main) {
    if (consignmentDocTypeLazyModel == null) {
      consignmentDocTypeLazyModel = new LazyDataModel<ConsignmentDocType>() {
      private List<ConsignmentDocType> list;      
      @Override
      public List<ConsignmentDocType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = ConsignmentDocTypeService.listPaged(main);
          main.commit(consignmentDocTypeLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(ConsignmentDocType consignmentDocType) {
        return consignmentDocType.getId();
      }
      @Override
        public ConsignmentDocType getRowData(String rowKey) {
          if (list != null) {
            for (ConsignmentDocType obj : list) {
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
    String SUB_FOLDER = "scm_consignment_doc_type/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveConsignmentDocType(MainView main) {
    return saveOrCloneConsignmentDocType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneConsignmentDocType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneConsignmentDocType(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneConsignmentDocType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ConsignmentDocTypeService.insertOrUpdate(main, getConsignmentDocType());
            break;
          case "clone":
            ConsignmentDocTypeService.clone(main, getConsignmentDocType());
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
   * Delete one or many ConsignmentDocType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteConsignmentDocType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(consignmentDocTypeSelected)) {
        ConsignmentDocTypeService.deleteByPkArray(main, getConsignmentDocTypeSelected()); //many record delete from list
        main.commit("success.delete");
        consignmentDocTypeSelected = null;
      } else {
        ConsignmentDocTypeService.deleteByPk(main, getConsignmentDocType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ConsignmentDocType.
   * @return
   */
  public LazyDataModel<ConsignmentDocType> getConsignmentDocTypeLazyModel() {
    return consignmentDocTypeLazyModel;
  }

 /**
  * Return ConsignmentDocType[].
  * @return 
  */
  public ConsignmentDocType[] getConsignmentDocTypeSelected() {
    return consignmentDocTypeSelected;
  }
  
  /**
   * Set ConsignmentDocType[].
   * @param consignmentDocTypeSelected 
   */
  public void setConsignmentDocTypeSelected(ConsignmentDocType[] consignmentDocTypeSelected) {
    this.consignmentDocTypeSelected = consignmentDocTypeSelected;
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
