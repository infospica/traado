/*
 * @(#)ContractBenefitTypeView.java	1.0 Thu Jun 23 15:26:32 IST 2016 
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ContractBenefitType;
import spica.scm.service.ContractBenefitTypeService;

/**
 * ContractBenefitTypeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 23 15:26:32 IST 2016
 */
@Named(value = "contractBenefitTypeView")
@ViewScoped
public class ContractBenefitTypeView implements Serializable {

  private transient ContractBenefitType contractBenefitType;	//Domain object/selected Domain.
  private transient LazyDataModel<ContractBenefitType> contractBenefitTypeLazyModel; 	//For lazy loading datatable.
  private transient ContractBenefitType[] contractBenefitTypeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ContractBenefitTypeView() {
    super();
  }

  /**
   * Return ContractBenefitType.
   *
   * @return ContractBenefitType.
   */
  public ContractBenefitType getContractBenefitType() {
    if (contractBenefitType == null) {
      contractBenefitType = new ContractBenefitType();
    }
    return contractBenefitType;
  }

  /**
   * Set ContractBenefitType.
   *
   * @param contractBenefitType.
   */
  public void setContractBenefitType(ContractBenefitType contractBenefitType) {
    this.contractBenefitType = contractBenefitType;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchContractBenefitType(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getContractBenefitType().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setContractBenefitType((ContractBenefitType) ContractBenefitTypeService.selectByPk(main, getContractBenefitType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadContractBenefitTypeList(main);
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
   * Create contractBenefitTypeLazyModel.
   *
   * @param main
   */
  private void loadContractBenefitTypeList(final MainView main) {
    if (contractBenefitTypeLazyModel == null) {
      contractBenefitTypeLazyModel = new LazyDataModel<ContractBenefitType>() {
        private List<ContractBenefitType> list;

        @Override
        public List<ContractBenefitType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ContractBenefitTypeService.listPaged(main);
            main.commit(contractBenefitTypeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ContractBenefitType contractBenefitType) {
          return contractBenefitType.getId();
        }

        @Override
        public ContractBenefitType getRowData(String rowKey) {
          if (list != null) {
            for (ContractBenefitType obj : list) {
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
    String SUB_FOLDER = "scm_contract_benefit_type/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveContractBenefitType(MainView main) {
    return saveOrCloneContractBenefitType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneContractBenefitType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneContractBenefitType(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneContractBenefitType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ContractBenefitTypeService.insertOrUpdate(main, getContractBenefitType());
            break;
          case "clone":
            ContractBenefitTypeService.clone(main, getContractBenefitType());
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
   * Delete one or many ContractBenefitType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteContractBenefitType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(contractBenefitTypeSelected)) {
        ContractBenefitTypeService.deleteByPkArray(main, getContractBenefitTypeSelected()); //many record delete from list
        main.commit("success.delete");
        contractBenefitTypeSelected = null;
      } else {
        ContractBenefitTypeService.deleteByPk(main, getContractBenefitType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ContractBenefitType.
   *
   * @return
   */
  public LazyDataModel<ContractBenefitType> getContractBenefitTypeLazyModel() {
    return contractBenefitTypeLazyModel;
  }

  /**
   * Return ContractBenefitType[].
   *
   * @return
   */
  public ContractBenefitType[] getContractBenefitTypeSelected() {
    return contractBenefitTypeSelected;
  }

  /**
   * Set ContractBenefitType[].
   *
   * @param contractBenefitTypeSelected
   */
  public void setContractBenefitTypeSelected(ContractBenefitType[] contractBenefitTypeSelected) {
    this.contractBenefitTypeSelected = contractBenefitTypeSelected;
  }

}
