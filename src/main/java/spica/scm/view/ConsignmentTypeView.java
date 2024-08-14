/*
 * @(#)ConsignmentTypeView.java	1.0 Fri Jul 22 10:57:43 IST 2016 
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

import spica.scm.domain.ConsignmentType;
import spica.scm.service.ConsignmentTypeService;

/**
 * ConsignmentTypeView
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:43 IST 2016 
 */

@Named(value="consignmentTypeView")
@ViewScoped
public class ConsignmentTypeView implements Serializable{

  private transient ConsignmentType consignmentType;	//Domain object/selected Domain.
  private transient LazyDataModel<ConsignmentType> consignmentTypeLazyModel; 	//For lazy loading datatable.
  private transient ConsignmentType[] consignmentTypeSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public ConsignmentTypeView() {
    super();
  }
 
  /**
   * Return ConsignmentType.
   * @return ConsignmentType.
   */  
  public ConsignmentType getConsignmentType() {
    if(consignmentType == null) {
      consignmentType = new ConsignmentType();
    }
    return consignmentType;
  }   
  
  /**
   * Set ConsignmentType.
   * @param consignmentType.
   */   
  public void setConsignmentType(ConsignmentType consignmentType) {
    this.consignmentType = consignmentType;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchConsignmentType(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getConsignmentType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setConsignmentType((ConsignmentType) ConsignmentTypeService.selectByPk(main, getConsignmentType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadConsignmentTypeList(main);
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
   * Create consignmentTypeLazyModel.
   * @param main
   */
  private void loadConsignmentTypeList(final MainView main) {
    if (consignmentTypeLazyModel == null) {
      consignmentTypeLazyModel = new LazyDataModel<ConsignmentType>() {
      private List<ConsignmentType> list;      
      @Override
      public List<ConsignmentType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = ConsignmentTypeService.listPaged(main);
          main.commit(consignmentTypeLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(ConsignmentType consignmentType) {
        return consignmentType.getId();
      }
      @Override
        public ConsignmentType getRowData(String rowKey) {
          if (list != null) {
            for (ConsignmentType obj : list) {
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
    String SUB_FOLDER = "scm_consignment_type/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveConsignmentType(MainView main) {
    return saveOrCloneConsignmentType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneConsignmentType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneConsignmentType(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneConsignmentType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ConsignmentTypeService.insertOrUpdate(main, getConsignmentType());
            break;
          case "clone":
            ConsignmentTypeService.clone(main, getConsignmentType());
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
   * Delete one or many ConsignmentType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteConsignmentType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(consignmentTypeSelected)) {
        ConsignmentTypeService.deleteByPkArray(main, getConsignmentTypeSelected()); //many record delete from list
        main.commit("success.delete");
        consignmentTypeSelected = null;
      } else {
        ConsignmentTypeService.deleteByPk(main, getConsignmentType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ConsignmentType.
   * @return
   */
  public LazyDataModel<ConsignmentType> getConsignmentTypeLazyModel() {
    return consignmentTypeLazyModel;
  }

 /**
  * Return ConsignmentType[].
  * @return 
  */
  public ConsignmentType[] getConsignmentTypeSelected() {
    return consignmentTypeSelected;
  }
  
  /**
   * Set ConsignmentType[].
   * @param consignmentTypeSelected 
   */
  public void setConsignmentTypeSelected(ConsignmentType[] consignmentTypeSelected) {
    this.consignmentTypeSelected = consignmentTypeSelected;
  }
 


}
