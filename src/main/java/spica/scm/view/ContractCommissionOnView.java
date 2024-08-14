/*
 * @(#)ContractCommissionOnView.java	1.0 Thu Apr 07 11:31:23 IST 2016 
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

import spica.scm.domain.ContractCommissionOn;
import spica.scm.service.ContractCommissionOnService;

/**
 * ContractCommissionOnView
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:23 IST 2016 
 */

@Named(value="contractCommissionOnView")
@ViewScoped
public class ContractCommissionOnView implements Serializable{

  private transient ContractCommissionOn contractCommissionOn;	//Domain object/selected Domain.
  private transient LazyDataModel<ContractCommissionOn> contractCommissionOnLazyModel; 	//For lazy loading datatable.
  private transient ContractCommissionOn[] contractCommissionOnSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public ContractCommissionOnView() {
    super();
  }
 
  /**
   * Return ContractCommissionOn.
   * @return ContractCommissionOn.
   */  
  public ContractCommissionOn getContractCommissionOn() {
    if(contractCommissionOn == null) {
      contractCommissionOn = new ContractCommissionOn();
    }
    return contractCommissionOn;
  }   
  
  /**
   * Set ContractCommissionOn.
   * @param contractCommissionOn.
   */   
  public void setContractCommissionOn(ContractCommissionOn contractCommissionOn) {
    this.contractCommissionOn = contractCommissionOn;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchContractCommissionOn(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getContractCommissionOn().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setContractCommissionOn((ContractCommissionOn) ContractCommissionOnService.selectByPk(main, getContractCommissionOn()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadContractCommissionOnList(main);
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
   * Create contractCommissionOnLazyModel.
   * @param main
   */
  private void loadContractCommissionOnList(final MainView main) {
    if (contractCommissionOnLazyModel == null) {
      contractCommissionOnLazyModel = new LazyDataModel<ContractCommissionOn>() {
      private List<ContractCommissionOn> list;      
      @Override
      public List<ContractCommissionOn> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = ContractCommissionOnService.listPaged(main);
          main.commit(contractCommissionOnLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(ContractCommissionOn contractCommissionOn) {
        return contractCommissionOn.getId();
      }
      @Override
        public ContractCommissionOn getRowData(String rowKey) {
          if (list != null) {
            for (ContractCommissionOn obj : list) {
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
    String SUB_FOLDER = "scm_contract_commission_on/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveContractCommissionOn(MainView main) {
    return saveOrCloneContractCommissionOn(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneContractCommissionOn(MainView main) {
    main.setViewType("newform");
    return saveOrCloneContractCommissionOn(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneContractCommissionOn(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ContractCommissionOnService.insertOrUpdate(main, getContractCommissionOn());
            break;
          case "clone":
            ContractCommissionOnService.clone(main, getContractCommissionOn());
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
   * Delete one or many ContractCommissionOn.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteContractCommissionOn(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(contractCommissionOnSelected)) {
        ContractCommissionOnService.deleteByPkArray(main, getContractCommissionOnSelected()); //many record delete from list
        main.commit("success.delete");
        contractCommissionOnSelected = null;
      } else {
        ContractCommissionOnService.deleteByPk(main, getContractCommissionOn());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ContractCommissionOn.
   * @return
   */
  public LazyDataModel<ContractCommissionOn> getContractCommissionOnLazyModel() {
    return contractCommissionOnLazyModel;
  }

 /**
  * Return ContractCommissionOn[].
  * @return 
  */
  public ContractCommissionOn[] getContractCommissionOnSelected() {
    return contractCommissionOnSelected;
  }
  
  /**
   * Set ContractCommissionOn[].
   * @param contractCommissionOnSelected 
   */
  public void setContractCommissionOnSelected(ContractCommissionOn[] contractCommissionOnSelected) {
    this.contractCommissionOnSelected = contractCommissionOnSelected;
  }
 


}
