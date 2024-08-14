/*
 * @(#)SalesAgentCommissionView.java	1.0 Wed May 04 14:12:11 IST 2016 
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

import spica.scm.domain.SalesAgentCommission;
import spica.scm.service.SalesAgentCommissionService;

/**
 * SalesAgentCommissionView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 04 14:12:11 IST 2016
 */
@Named(value = "salesAgentCommissionView")
@ViewScoped
public class SalesAgentCommissionView implements Serializable {

  private transient SalesAgentCommission salesAgentCommission;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentCommission> salesAgentCommissionLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentCommission[] salesAgentCommissionSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesAgentCommissionView() {
    super();
  }

  /**
   * Return SalesAgentCommission.
   *
   * @return SalesAgentCommission.
   */
  public SalesAgentCommission getSalesAgentCommission() {
    if (salesAgentCommission == null) {
      salesAgentCommission = new SalesAgentCommission();
    }
    return salesAgentCommission;
  }

  /**
   * Set SalesAgentCommission.
   *
   * @param salesAgentCommission.
   */
  public void setSalesAgentCommission(SalesAgentCommission salesAgentCommission) {
    this.salesAgentCommission = salesAgentCommission;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAgentCommission(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getSalesAgentCommission().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSalesAgentCommission((SalesAgentCommission) SalesAgentCommissionService.selectByPk(main, getSalesAgentCommission()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesAgentCommissionList(main);
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
   * Create salesAgentCommissionLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentCommissionList(final MainView main) {
    if (salesAgentCommissionLazyModel == null) {
      salesAgentCommissionLazyModel = new LazyDataModel<SalesAgentCommission>() {
        private List<SalesAgentCommission> list;

        @Override
        public List<SalesAgentCommission> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAgentCommissionService.listPaged(main);
            main.commit(salesAgentCommissionLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgentCommission salesAgentCommission) {
          return salesAgentCommission.getId();
        }

        @Override
        public SalesAgentCommission getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentCommission obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_commission/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentCommission(MainView main) {
    return saveOrCloneSalesAgentCommission(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentCommission(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesAgentCommission(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentCommission(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentCommissionService.insertOrUpdate(main, getSalesAgentCommission());
            break;
          case "clone":
            SalesAgentCommissionService.clone(main, getSalesAgentCommission());
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
   * Delete one or many SalesAgentCommission.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentCommission(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesAgentCommissionSelected)) {
        SalesAgentCommissionService.deleteByPkArray(main, getSalesAgentCommissionSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentCommissionSelected = null;
      } else {
        SalesAgentCommissionService.deleteByPk(main, getSalesAgentCommission());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesAgentCommission.
   *
   * @return
   */
  public LazyDataModel<SalesAgentCommission> getSalesAgentCommissionLazyModel() {
    return salesAgentCommissionLazyModel;
  }

  /**
   * Return SalesAgentCommission[].
   *
   * @return
   */
  public SalesAgentCommission[] getSalesAgentCommissionSelected() {
    return salesAgentCommissionSelected;
  }

  /**
   * Set SalesAgentCommission[].
   *
   * @param salesAgentCommissionSelected
   */
  public void setSalesAgentCommissionSelected(SalesAgentCommission[] salesAgentCommissionSelected) {
    this.salesAgentCommissionSelected = salesAgentCommissionSelected;
  }

}
