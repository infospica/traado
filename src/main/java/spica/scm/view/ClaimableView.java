/*
 * @(#)ClaimableView.java	1.0 Wed Apr 13 15:41:14 IST 2016
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

import spica.scm.domain.Claimable;
import spica.scm.domain.Contract;
import spica.scm.service.ClaimableService;
import spica.scm.service.ContractClaimableService;
import wawo.app.faces.Jsf;

/**
 * ClaimableView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:14 IST 2016
 */
@Named(value = "claimableView")
@ViewScoped
public class ClaimableView implements Serializable {

  private transient Claimable claimable;	//Domain object/selected Domain.
  private transient LazyDataModel<Claimable> claimableLazyModel; 	//For lazy loading datatable.
  private transient Claimable[] claimableSelected;	 //Selected Domain Array

  private Contract contract;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    contract = (Contract) Jsf.dialogParent(Contract.class);
    //getGenre().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public ClaimableView() {
    super();
  }

  public Contract getContract() {
    return contract;
  }

  public void setContract(Contract contract) {
    this.contract = contract;
  }

  /**
   * Return Claimable.
   *
   * @return Claimable.
   */
  public Claimable getClaimable() {
    if (claimable == null) {
      claimable = new Claimable();
    }
    return claimable;
  }

  /**
   * Set Claimable.
   *
   * @param claimable.
   */
  public void setClaimable(Claimable claimable) {
    this.claimable = claimable;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchClaimable(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getClaimable().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setClaimable((Claimable) ClaimableService.selectByPk(main, getClaimable()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadClaimableList(main);
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
   * Create claimableLazyModel.
   *
   * @param main
   */
  private void loadClaimableList(final MainView main) {
    if (claimableLazyModel == null) {
      claimableLazyModel = new LazyDataModel<Claimable>() {
        private List<Claimable> list;

        @Override
        public List<Claimable> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            if (contract != null && contract.getId() != null) {
              list = ClaimableService.listPagedNotInContract(main, contract); //passing the parent to exclude from select list which are already selected
            } else {
              list = ClaimableService.listPaged(main);
            }
            main.commit(claimableLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Claimable claimable) {
          return claimable.getId();
        }

        @Override
        public Claimable getRowData(String rowKey) {
          if (list != null) {
            for (Claimable obj : list) {
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
    String SUB_FOLDER = "scm_claimable/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveClaimable(MainView main) {
    return saveOrCloneClaimable(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneClaimable(MainView main) {
    main.setViewType("newform");
    return saveOrCloneClaimable(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneClaimable(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ClaimableService.insertOrUpdate(main, getClaimable());
            break;
          case "clone":
            ClaimableService.clone(main, getClaimable());
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
   * Delete one or many Claimable.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteClaimable(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(claimableSelected)) {
        ClaimableService.deleteByPkArray(main, getClaimableSelected()); //many record delete from list
        main.commit("success.delete");
        claimableSelected = null;
      } else {
        ClaimableService.deleteByPk(main, getClaimable());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Claimable.
   *
   * @return
   */
  public LazyDataModel<Claimable> getClaimableLazyModel() {
    return claimableLazyModel;
  }

  /**
   * Return Claimable[].
   *
   * @return
   */
  public Claimable[] getClaimableSelected() {
    return claimableSelected;
  }

  /**
   * Set Claimable[].
   *
   * @param claimableSelected
   */
  public void setClaimableSelected(Claimable[] claimableSelected) {
    this.claimableSelected = claimableSelected;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void contractClaimableDialogClose() {
    Jsf.returnDialog(null);
  }

  public String insertContractClaimable(MainView main) {
    try {
      ContractClaimableService.insertArray(main, getClaimableSelected(), getContract());
      main.commit("success.select");
    } catch (Throwable t) {
      main.rollback(t, "error.list");
    } finally {
      main.close();
    }
    return null;
  }
}
