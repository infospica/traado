/*
 * @(#)SalesReturnProdStatView.java	1.0 Mon May 09 18:27:32 IST 2016 
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

import spica.scm.domain.SalesReturnProdStat;
import spica.scm.service.SalesReturnProdStatService;

/**
 * SalesReturnProdStatView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon May 09 18:27:32 IST 2016
 */
@Named(value = "salesReturnProdStatView")
@ViewScoped
public class SalesReturnProdStatView implements Serializable {

  private transient SalesReturnProdStat salesReturnProdStat;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesReturnProdStat> salesReturnProdStatLazyModel; 	//For lazy loading datatable.
  private transient SalesReturnProdStat[] salesReturnProdStatSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesReturnProdStatView() {
    super();
  }

  /**
   * Return SalesReturnProdStat.
   *
   * @return SalesReturnProdStat.
   */
  public SalesReturnProdStat getSalesReturnProdStat() {
    if (salesReturnProdStat == null) {
      salesReturnProdStat = new SalesReturnProdStat();
    }
    return salesReturnProdStat;
  }

  /**
   * Set SalesReturnProdStat.
   *
   * @param salesReturnProdStat.
   */
  public void setSalesReturnProdStat(SalesReturnProdStat salesReturnProdStat) {
    this.salesReturnProdStat = salesReturnProdStat;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesReturnProdStat(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getSalesReturnProdStat().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSalesReturnProdStat((SalesReturnProdStat) SalesReturnProdStatService.selectByPk(main, getSalesReturnProdStat()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesReturnProdStatList(main);
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
   * Create salesReturnProdStatLazyModel.
   *
   * @param main
   */
  private void loadSalesReturnProdStatList(final MainView main) {
    if (salesReturnProdStatLazyModel == null) {
      salesReturnProdStatLazyModel = new LazyDataModel<SalesReturnProdStat>() {
        private List<SalesReturnProdStat> list;

        @Override
        public List<SalesReturnProdStat> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesReturnProdStatService.listPaged(main);
            main.commit(salesReturnProdStatLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesReturnProdStat salesReturnProdStat) {
          return salesReturnProdStat.getId();
        }

        @Override
        public SalesReturnProdStat getRowData(String rowKey) {
          if (list != null) {
            for (SalesReturnProdStat obj : list) {
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
    String SUB_FOLDER = "scm_sales_return_prod_stat/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesReturnProdStat(MainView main) {
    return saveOrCloneSalesReturnProdStat(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesReturnProdStat(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesReturnProdStat(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesReturnProdStat(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesReturnProdStatService.insertOrUpdate(main, getSalesReturnProdStat());
            break;
          case "clone":
            SalesReturnProdStatService.clone(main, getSalesReturnProdStat());
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
   * Delete one or many SalesReturnProdStat.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesReturnProdStat(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesReturnProdStatSelected)) {
        SalesReturnProdStatService.deleteByPkArray(main, getSalesReturnProdStatSelected()); //many record delete from list
        main.commit("success.delete");
        salesReturnProdStatSelected = null;
      } else {
        SalesReturnProdStatService.deleteByPk(main, getSalesReturnProdStat());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesReturnProdStat.
   *
   * @return
   */
  public LazyDataModel<SalesReturnProdStat> getSalesReturnProdStatLazyModel() {
    return salesReturnProdStatLazyModel;
  }

  /**
   * Return SalesReturnProdStat[].
   *
   * @return
   */
  public SalesReturnProdStat[] getSalesReturnProdStatSelected() {
    return salesReturnProdStatSelected;
  }

  /**
   * Set SalesReturnProdStat[].
   *
   * @param salesReturnProdStatSelected
   */
  public void setSalesReturnProdStatSelected(SalesReturnProdStat[] salesReturnProdStatSelected) {
    this.salesReturnProdStatSelected = salesReturnProdStatSelected;
  }

}
