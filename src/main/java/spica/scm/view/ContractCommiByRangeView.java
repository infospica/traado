/*
 * @(#)ContractCommiByRangeView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ContractCommiByRange;
import spica.scm.service.ContractCommiByRangeService;
import spica.scm.domain.Contract;
import wawo.app.faces.Jsf;

/**
 * ContractCommiByRangeView
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016 
 */

@Named(value="contractCommiByRangeView")
@ViewScoped
public class ContractCommiByRangeView implements Serializable{

  private transient ContractCommiByRange contractCommiByRange;	//Domain object/selected Domain.
  private transient LazyDataModel<ContractCommiByRange> contractCommiByRangeLazyModel; 	//For lazy loading datatable.
  private transient ContractCommiByRange[] contractCommiByRangeSelected;	 //Selected Domain Array
  
  private Contract parent;
  
  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Contract) Jsf.dialogParent(Contract.class);
    getContractCommiByRange().setId(Jsf.getParameterInt("id"));
  }
  
  /**
   * Default Constructor.
   */   
  public ContractCommiByRangeView() {
    super();
  }
  
  public Contract getParent() {
    return parent;
  }

  public void setParent(Contract parent) {
    this.parent = parent;
  }
 
  /**
   * Return ContractCommiByRange.
   * @return ContractCommiByRange.
   */  
  public ContractCommiByRange getContractCommiByRange() {
    if(contractCommiByRange == null) {
      contractCommiByRange = new ContractCommiByRange();
    }
    return contractCommiByRange;
  }   
  
  /**
   * Set ContractCommiByRange.
   * @param contractCommiByRange.
   */   
  public void setContractCommiByRange(ContractCommiByRange contractCommiByRange) {
    this.contractCommiByRange = contractCommiByRange;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchContractCommiByRange(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getContractCommiByRange().reset();
          getContractCommiByRange().setContractId(parent);          
        } else if (ViewType.editform.toString().equals(viewType)) {
          setContractCommiByRange((ContractCommiByRange) ContractCommiByRangeService.selectByPk(main, getContractCommiByRange()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadContractCommiByRangeList(main);
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
   * Create contractCommiByRangeLazyModel.
   * @param main
   */
  private void loadContractCommiByRangeList(final MainView main) {
    if (contractCommiByRangeLazyModel == null) {
      contractCommiByRangeLazyModel = new LazyDataModel<ContractCommiByRange>() {
      private List<ContractCommiByRange> list;      
      @Override
      public List<ContractCommiByRange> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = ContractCommiByRangeService.listPaged(main);
          main.commit(contractCommiByRangeLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(ContractCommiByRange contractCommiByRange) {
        return contractCommiByRange.getId();
      }
      @Override
        public ContractCommiByRange getRowData(String rowKey) {
          if (list != null) {
            for (ContractCommiByRange obj : list) {
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
    String SUB_FOLDER = "scm_contract_commi_by_range/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveContractCommiByRange(MainView main) {
    return saveOrCloneContractCommiByRange(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneContractCommiByRange(MainView main) {
    main.setViewType("newform");
    return saveOrCloneContractCommiByRange(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneContractCommiByRange(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ContractCommiByRangeService.insertOrUpdate(main, getContractCommiByRange());
            break;
          case "clone":
            ContractCommiByRangeService.clone(main, getContractCommiByRange());
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
   * Delete one or many ContractCommiByRange.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteContractCommiByRange(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(contractCommiByRangeSelected)) {
        ContractCommiByRangeService.deleteByPkArray(main, getContractCommiByRangeSelected()); //many record delete from list
        main.commit("success.delete");
        contractCommiByRangeSelected = null;
      } else {
        ContractCommiByRangeService.deleteByPk(main, getContractCommiByRange());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ContractCommiByRange.
   * @return
   */
  public LazyDataModel<ContractCommiByRange> getContractCommiByRangeLazyModel() {
    return contractCommiByRangeLazyModel;
  }

 /**
  * Return ContractCommiByRange[].
  * @return 
  */
  public ContractCommiByRange[] getContractCommiByRangeSelected() {
    return contractCommiByRangeSelected;
  }
  
  /**
   * Set ContractCommiByRange[].
   * @param contractCommiByRangeSelected 
   */
  public void setContractCommiByRangeSelected(ContractCommiByRange[] contractCommiByRangeSelected) {
    this.contractCommiByRangeSelected = contractCommiByRangeSelected;
  }
 


 /**
  * Contract autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.contractAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.contractAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<Contract> contractAuto(String filter) {
    return ScmLookupView.contractAuto(filter);
  }
  
  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void commissionByRangeDialogClose() {
    Jsf.returnDialog(null);
  }
}
