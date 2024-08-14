/*
 * @(#)ContractCommissionTypeView.java	1.0 Thu Apr 07 11:31:23 IST 2016 
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

import spica.scm.domain.ContractCommissionType;
import spica.scm.service.ContractCommissionTypeService;

/**
 * ContractCommissionTypeView
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:23 IST 2016 
 */

@Named(value="contractCommissionTypeView")
@ViewScoped
public class ContractCommissionTypeView implements Serializable{

  private transient ContractCommissionType contractCommissionType;	//Domain object/selected Domain.
  private transient LazyDataModel<ContractCommissionType> contractCommissionTypeLazyModel; 	//For lazy loading datatable.
  private transient ContractCommissionType[] contractCommissionTypeSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public ContractCommissionTypeView() {
    super();
  }
 
  /**
   * Return ContractCommissionType.
   * @return ContractCommissionType.
   */  
  public ContractCommissionType getContractCommissionType() {
    if(contractCommissionType == null) {
      contractCommissionType = new ContractCommissionType();
    }
    return contractCommissionType;
  }   
  
  /**
   * Set ContractCommissionType.
   * @param contractCommissionType.
   */   
  public void setContractCommissionType(ContractCommissionType contractCommissionType) {
    this.contractCommissionType = contractCommissionType;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchContractCommissionType(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getContractCommissionType().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setContractCommissionType((ContractCommissionType) ContractCommissionTypeService.selectByPk(main, getContractCommissionType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadContractCommissionTypeList(main);
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
   * Create contractCommissionTypeLazyModel.
   * @param main
   */
  private void loadContractCommissionTypeList(final MainView main) {
    if (contractCommissionTypeLazyModel == null) {
      contractCommissionTypeLazyModel = new LazyDataModel<ContractCommissionType>() {
      private List<ContractCommissionType> list;      
      @Override
      public List<ContractCommissionType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = ContractCommissionTypeService.listPaged(main);
          main.commit(contractCommissionTypeLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(ContractCommissionType contractCommissionType) {
        return contractCommissionType.getId();
      }
      @Override
        public ContractCommissionType getRowData(String rowKey) {
          if (list != null) {
            for (ContractCommissionType obj : list) {
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
    String SUB_FOLDER = "scm_contract_commission_type/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveContractCommissionType(MainView main) {
    return saveOrCloneContractCommissionType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneContractCommissionType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneContractCommissionType(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneContractCommissionType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ContractCommissionTypeService.insertOrUpdate(main, getContractCommissionType());
            break;
          case "clone":
            ContractCommissionTypeService.clone(main, getContractCommissionType());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error"+ key);
    } finally {
      main.close();
    }
    return null;
  }

  
  /**
   * Delete one or many ContractCommissionType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteContractCommissionType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(contractCommissionTypeSelected)) {
        ContractCommissionTypeService.deleteByPkArray(main, getContractCommissionTypeSelected()); //many record delete from list
        main.commit("success.delete");
        contractCommissionTypeSelected = null;
      } else {
        ContractCommissionTypeService.deleteByPk(main, getContractCommissionType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ContractCommissionType.
   * @return
   */
  public LazyDataModel<ContractCommissionType> getContractCommissionTypeLazyModel() {
    return contractCommissionTypeLazyModel;
  }

 /**
  * Return ContractCommissionType[].
  * @return 
  */
  public ContractCommissionType[] getContractCommissionTypeSelected() {
    return contractCommissionTypeSelected;
  }
  
  /**
   * Set ContractCommissionType[].
   * @param contractCommissionTypeSelected 
   */
  public void setContractCommissionTypeSelected(ContractCommissionType[] contractCommissionTypeSelected) {
    this.contractCommissionTypeSelected = contractCommissionTypeSelected;
  }
 


}
