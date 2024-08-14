/*
 * @(#)SalesAgentContractStatView.java	1.0 Wed May 04 14:12:11 IST 2016 
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

import spica.scm.domain.SalesAgentContractStat;
import spica.scm.service.SalesAgentContractStatService;

/**
 * SalesAgentContractStatView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 04 14:12:11 IST 2016
 */
@Named(value = "salesAgentContractStatView")
@ViewScoped
public class SalesAgentContractStatView implements Serializable {

  private transient SalesAgentContractStat salesAgentContractStat;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentContractStat> salesAgentContractStatLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentContractStat[] salesAgentContractStatSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesAgentContractStatView() {
    super();
  }

  /**
   * Return SalesAgentContractStat.
   *
   * @return SalesAgentContractStat.
   */
  public SalesAgentContractStat getSalesAgentContractStat() {
    if (salesAgentContractStat == null) {
      salesAgentContractStat = new SalesAgentContractStat();
    }
    return salesAgentContractStat;
  }

  /**
   * Set SalesAgentContractStat.
   *
   * @param salesAgentContractStat.
   */
  public void setSalesAgentContractStat(SalesAgentContractStat salesAgentContractStat) {
    this.salesAgentContractStat = salesAgentContractStat;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAgentContractStat(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getSalesAgentContractStat().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSalesAgentContractStat((SalesAgentContractStat) SalesAgentContractStatService.selectByPk(main, getSalesAgentContractStat()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesAgentContractStatList(main);
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
   * Create salesAgentContractStatLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentContractStatList(final MainView main) {
    if (salesAgentContractStatLazyModel == null) {
      salesAgentContractStatLazyModel = new LazyDataModel<SalesAgentContractStat>() {
        private List<SalesAgentContractStat> list;

        @Override
        public List<SalesAgentContractStat> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAgentContractStatService.listPaged(main);
            main.commit(salesAgentContractStatLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgentContractStat salesAgentContractStat) {
          return salesAgentContractStat.getId();
        }

        @Override
        public SalesAgentContractStat getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentContractStat obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_contract_stat/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentContractStat(MainView main) {
    return saveOrCloneSalesAgentContractStat(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentContractStat(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesAgentContractStat(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentContractStat(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentContractStatService.insertOrUpdate(main, getSalesAgentContractStat());
            break;
          case "clone":
            SalesAgentContractStatService.clone(main, getSalesAgentContractStat());
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
   * Delete one or many SalesAgentContractStat.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentContractStat(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesAgentContractStatSelected)) {
        SalesAgentContractStatService.deleteByPkArray(main, getSalesAgentContractStatSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentContractStatSelected = null;
      } else {
        SalesAgentContractStatService.deleteByPk(main, getSalesAgentContractStat());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesAgentContractStat.
   *
   * @return
   */
  public LazyDataModel<SalesAgentContractStat> getSalesAgentContractStatLazyModel() {
    return salesAgentContractStatLazyModel;
  }

  /**
   * Return SalesAgentContractStat[].
   *
   * @return
   */
  public SalesAgentContractStat[] getSalesAgentContractStatSelected() {
    return salesAgentContractStatSelected;
  }

  /**
   * Set SalesAgentContractStat[].
   *
   * @param salesAgentContractStatSelected
   */
  public void setSalesAgentContractStatSelected(SalesAgentContractStat[] salesAgentContractStatSelected) {
    this.salesAgentContractStatSelected = salesAgentContractStatSelected;
  }

}
