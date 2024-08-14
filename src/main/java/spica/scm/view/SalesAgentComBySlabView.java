/*
 * @(#)SalesAgentComBySlabView.java	1.0 Wed May 04 14:12:11 IST 2016
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

import spica.scm.domain.SalesAgentComBySlab;
import spica.scm.service.SalesAgentComBySlabService;
import spica.scm.domain.SalesAgentContract;
import wawo.app.faces.Jsf;

/**
 * SalesAgentComBySlabView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 04 14:12:11 IST 2016
 */
@Named(value = "salesAgentComBySlabView")
@ViewScoped
public class SalesAgentComBySlabView implements Serializable {

  private transient SalesAgentComBySlab salesAgentComBySlab;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentComBySlab> salesAgentComBySlabLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentComBySlab[] salesAgentComBySlabSelected;	 //Selected Domain Array

  private SalesAgentContract parentSalesAgentContract;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parentSalesAgentContract = (SalesAgentContract) Jsf.dialogParent(SalesAgentContract.class);
    getSalesAgentComBySlab().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public SalesAgentComBySlabView() {
    super();
  }

  /**
   * Return SalesAgentComBySlab.
   *
   * @return SalesAgentComBySlab.
   */
  public SalesAgentComBySlab getSalesAgentComBySlab() {
    if (salesAgentComBySlab == null) {
      salesAgentComBySlab = new SalesAgentComBySlab();
    }
    return salesAgentComBySlab;
  }

  /**
   * Set SalesAgentComBySlab.
   *
   * @param salesAgentComBySlab.
   */
  public void setSalesAgentComBySlab(SalesAgentComBySlab salesAgentComBySlab) {
    this.salesAgentComBySlab = salesAgentComBySlab;
  }

  public SalesAgentContract getParentSalesAgentContract() {
    return parentSalesAgentContract;
  }

  public void setParentSalesAgentContract(SalesAgentContract parentSalesAgentContract) {
    this.parentSalesAgentContract = parentSalesAgentContract;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAgentComBySlab(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getSalesAgentComBySlab().reset();
          getSalesAgentComBySlab().setSalesAgentContractId(parentSalesAgentContract);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSalesAgentComBySlab((SalesAgentComBySlab) SalesAgentComBySlabService.selectByPk(main, getSalesAgentComBySlab()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesAgentComBySlabList(main);
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
   * Create salesAgentComBySlabLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentComBySlabList(final MainView main) {
    if (salesAgentComBySlabLazyModel == null) {
      salesAgentComBySlabLazyModel = new LazyDataModel<SalesAgentComBySlab>() {
        private List<SalesAgentComBySlab> list;

        @Override
        public List<SalesAgentComBySlab> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAgentComBySlabService.listPaged(main);
            main.commit(salesAgentComBySlabLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgentComBySlab salesAgentComBySlab) {
          return salesAgentComBySlab.getId();
        }

        @Override
        public SalesAgentComBySlab getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentComBySlab obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_com_by_slab/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentComBySlab(MainView main) {
    return saveOrCloneSalesAgentComBySlab(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentComBySlab(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesAgentComBySlab(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentComBySlab(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentComBySlabService.insertOrUpdate(main, getSalesAgentComBySlab());
            break;
          case "clone":
            SalesAgentComBySlabService.clone(main, getSalesAgentComBySlab());
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
   * Delete one or many SalesAgentComBySlab.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentComBySlab(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesAgentComBySlabSelected)) {
        SalesAgentComBySlabService.deleteByPkArray(main, getSalesAgentComBySlabSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentComBySlabSelected = null;
      } else {
        SalesAgentComBySlabService.deleteByPk(main, getSalesAgentComBySlab());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesAgentComBySlab.
   *
   * @return
   */
  public LazyDataModel<SalesAgentComBySlab> getSalesAgentComBySlabLazyModel() {
    return salesAgentComBySlabLazyModel;
  }

  /**
   * Return SalesAgentComBySlab[].
   *
   * @return
   */
  public SalesAgentComBySlab[] getSalesAgentComBySlabSelected() {
    return salesAgentComBySlabSelected;
  }

  /**
   * Set SalesAgentComBySlab[].
   *
   * @param salesAgentComBySlabSelected
   */
  public void setSalesAgentComBySlabSelected(SalesAgentComBySlab[] salesAgentComBySlabSelected) {
    this.salesAgentComBySlabSelected = salesAgentComBySlabSelected;
  }

  /**
   * SalesAgentContract autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.salesAgentContractAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.salesAgentContractAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesAgentContract> salesAgentContractAuto(String filter) {
    return ScmLookupView.salesAgentContractAuto(filter);
  }
}
