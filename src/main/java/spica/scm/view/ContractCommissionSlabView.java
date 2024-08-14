/*
 * @(#)ContractCommissionSlabView.java	1.0 Thu Apr 07 11:31:23 IST 2016 
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

import spica.scm.domain.ContractCommissionSlab;
import spica.scm.service.ContractCommissionSlabService;
import spica.scm.domain.Contract;
import wawo.app.faces.Jsf;

/**
 * ContractCommissionSlabView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:23 IST 2016
 */
@Named(value = "contractCommissionSlabView")
@ViewScoped
public class ContractCommissionSlabView implements Serializable {

  private transient ContractCommissionSlab contractCommissionSlab;	//Domain object/selected Domain.
  private transient LazyDataModel<ContractCommissionSlab> contractCommissionSlabLazyModel; 	//For lazy loading datatable.
  private transient ContractCommissionSlab[] contractCommissionSlabSelected;	 //Selected Domain Array

  private Contract parent;

  /**
   * Default Constructor.
   */
  public ContractCommissionSlabView() {
    super();
  }

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Contract) Jsf.dialogParent(Contract.class);
    getContractCommissionSlab().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Return ContractCommissionSlab.
   *
   * @return ContractCommissionSlab.
   */
  public ContractCommissionSlab getContractCommissionSlab() {
    if (contractCommissionSlab == null) {
      contractCommissionSlab = new ContractCommissionSlab();
    }
    return contractCommissionSlab;
  }

  /**
   * Set ContractCommissionSlab.
   *
   * @param contractCommissionSlab.
   */
  public void setContractCommissionSlab(ContractCommissionSlab contractCommissionSlab) {
    this.contractCommissionSlab = contractCommissionSlab;
  }

  public Contract getParent() {
    return parent;
  }

  public void setParent(Contract parent) {
    this.parent = parent;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchContractCommissionSlab(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getContractCommissionSlab().reset();
          getContractCommissionSlab().setContractId(parent);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setContractCommissionSlab((ContractCommissionSlab) ContractCommissionSlabService.selectByPk(main, getContractCommissionSlab()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadContractCommissionSlabList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create contractCommissionSlabLazyModel.
   *
   * @param main
   */
  private void loadContractCommissionSlabList(final MainView main) {
    if (contractCommissionSlabLazyModel == null) {
      contractCommissionSlabLazyModel = new LazyDataModel<ContractCommissionSlab>() {
        private List<ContractCommissionSlab> list;

        @Override
        public List<ContractCommissionSlab> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ContractCommissionSlabService.listPaged(main);
            main.commit(contractCommissionSlabLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ContractCommissionSlab contractCommissionSlab) {
          return contractCommissionSlab.getId();
        }

        @Override
        public ContractCommissionSlab getRowData(String rowKey) {
          if (list != null) {
            for (ContractCommissionSlab obj : list) {
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
    String SUB_FOLDER = "scm_contract_commission_slab/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveContractCommissionSlab(MainView main) {
    return saveOrCloneContractCommissionSlab(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneContractCommissionSlab(MainView main) {
    main.setViewType("newform");
    return saveOrCloneContractCommissionSlab(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneContractCommissionSlab(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ContractCommissionSlabService.insertOrUpdate(main, getContractCommissionSlab());
            break;
          case "clone":
            ContractCommissionSlabService.clone(main, getContractCommissionSlab());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many ContractCommissionSlab.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteContractCommissionSlab(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(contractCommissionSlabSelected)) {
        ContractCommissionSlabService.deleteByPkArray(main, getContractCommissionSlabSelected()); //many record delete from list
        main.commit("success.delete");
        contractCommissionSlabSelected = null;
      } else {
        ContractCommissionSlabService.deleteByPk(main, getContractCommissionSlab());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())) {
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
   * Return LazyDataModel of ContractCommissionSlab.
   *
   * @return
   */
  public LazyDataModel<ContractCommissionSlab> getContractCommissionSlabLazyModel() {
    return contractCommissionSlabLazyModel;
  }

  /**
   * Return ContractCommissionSlab[].
   *
   * @return
   */
  public ContractCommissionSlab[] getContractCommissionSlabSelected() {
    return contractCommissionSlabSelected;
  }

  /**
   * Set ContractCommissionSlab[].
   *
   * @param contractCommissionSlabSelected
   */
  public void setContractCommissionSlabSelected(ContractCommissionSlab[] contractCommissionSlabSelected) {
    this.contractCommissionSlabSelected = contractCommissionSlabSelected;
  }

  /**
   * Contract autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.contractAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.contractAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Contract> contractAuto(String filter) {
    return ScmLookupView.contractAuto(filter);
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void commissionSlabDialogClose() {
    Jsf.returnDialog(null);
  }
}
