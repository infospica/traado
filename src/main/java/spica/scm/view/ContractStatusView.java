/*
 * @(#)ContractStatusView.java	1.0 Thu Apr 07 11:31:23 IST 2016 
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

import spica.scm.domain.ContractStatus;
import spica.scm.service.ContractStatusService;

/**
 * ContractStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:23 IST 2016
 */
@Named(value = "contractStatusView")
@ViewScoped
public class ContractStatusView implements Serializable {

  private transient ContractStatus contractStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<ContractStatus> contractStatusLazyModel; 	//For lazy loading datatable.
  private transient ContractStatus[] contractStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ContractStatusView() {
    super();
  }

  /**
   * Return ContractStatus.
   *
   * @return ContractStatus.
   */
  public ContractStatus getContractStatus() {
    if (contractStatus == null) {
      contractStatus = new ContractStatus();
    }
    return contractStatus;
  }

  /**
   * Set ContractStatus.
   *
   * @param contractStatus.
   */
  public void setContractStatus(ContractStatus contractStatus) {
    this.contractStatus = contractStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchContractStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getContractStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setContractStatus((ContractStatus) ContractStatusService.selectByPk(main, getContractStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadContractStatusList(main);
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
   * Create contractStatusLazyModel.
   *
   * @param main
   */
  private void loadContractStatusList(final MainView main) {
    if (contractStatusLazyModel == null) {
      contractStatusLazyModel = new LazyDataModel<ContractStatus>() {
        private List<ContractStatus> list;

        @Override
        public List<ContractStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ContractStatusService.listPaged(main);
            main.commit(contractStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ContractStatus contractStatus) {
          return contractStatus.getId();
        }

        @Override
        public ContractStatus getRowData(String rowKey) {
          if (list != null) {
            for (ContractStatus obj : list) {
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
    String SUB_FOLDER = "scm_contract_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveContractStatus(MainView main) {
    return saveOrCloneContractStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneContractStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneContractStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneContractStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ContractStatusService.insertOrUpdate(main, getContractStatus());
            break;
          case "clone":
            ContractStatusService.clone(main, getContractStatus());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error" + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many ContractStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteContractStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(contractStatusSelected)) {
        ContractStatusService.deleteByPkArray(main, getContractStatusSelected()); //many record delete from list
        main.commit("success.delete");
        contractStatusSelected = null;
      } else {
        ContractStatusService.deleteByPk(main, getContractStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ContractStatus.
   *
   * @return
   */
  public LazyDataModel<ContractStatus> getContractStatusLazyModel() {
    return contractStatusLazyModel;
  }

  /**
   * Return ContractStatus[].
   *
   * @return
   */
  public ContractStatus[] getContractStatusSelected() {
    return contractStatusSelected;
  }

  /**
   * Set ContractStatus[].
   *
   * @param contractStatusSelected
   */
  public void setContractStatusSelected(ContractStatus[] contractStatusSelected) {
    this.contractStatusSelected = contractStatusSelected;
  }

}
