/*
 * @(#)ScmSalesAgentServicesView.java	1.0 Mon Sep 11 12:32:41 IST 2017 
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
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesAgentServices;
import spica.scm.service.SalesAgentServicesService;
import spica.scm.domain.UserProfile;

/**
 * ScmSalesAgentServicesView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Sep 11 12:32:41 IST 2017
 */
@Named(value = "salesAgentServicesView")
@ViewScoped
public class SalesAgentServicesView implements Serializable {

  private transient SalesAgentServices salesAgentServices;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentServices> salesAgentServicesLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentServices[] salesAgentServicesSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesAgentServicesView() {
    super();
  }

  /**
   * Return ScmSalesAgentServices.
   *
   * @return ScmSalesAgentServices.
   */
  public SalesAgentServices getSalesAgentServices() {
    if (salesAgentServices == null) {
      salesAgentServices = new SalesAgentServices();
    }
    return salesAgentServices;
  }

  /**
   * Set ScmSalesAgentServices.
   *
   * @param scmSalesAgentServices.
   */
  public void setSalesAgentServices(SalesAgentServices salesAgentServices) {
    this.salesAgentServices = salesAgentServices;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAgentServices(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getSalesAgentServices().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setSalesAgentServices((SalesAgentServices) SalesAgentServicesService.selectByPk(main, getSalesAgentServices()));
        } else if (main.isList()) {
          loadSalesAgentServicesList(main);
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
   * Create scmSalesAgentServicesLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentServicesList(final MainView main) {
    if (salesAgentServicesLazyModel == null) {
      salesAgentServicesLazyModel = new LazyDataModel<SalesAgentServices>() {
        private List<SalesAgentServices> list;

        @Override
        public List<SalesAgentServices> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAgentServicesService.listPaged(main);
            main.commit(salesAgentServicesLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgentServices salesAgentServices) {
          return salesAgentServices.getId();
        }

        @Override
        public SalesAgentServices getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentServices obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_services/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentServices(MainView main) {
    return saveOrCloneSalesAgentServices(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentServices(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneSalesAgentServices(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentServices(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentServicesService.insertOrUpdate(main, getSalesAgentServices());
            break;
          case "clone":
            SalesAgentServicesService.clone(main, getSalesAgentServices());
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
   * Delete one or many ScmSalesAgentServices.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentServices(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(salesAgentServicesSelected)) {
        SalesAgentServicesService.deleteByPkArray(main, getSalesAgentServicesSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentServicesSelected = null;
      } else {
        SalesAgentServicesService.deleteByPk(main, getSalesAgentServices());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
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
   * Return LazyDataModel of ScmSalesAgentServices.
   *
   * @return
   */
  public LazyDataModel<SalesAgentServices> getSalesAgentServicesLazyModel() {
    return salesAgentServicesLazyModel;
  }

  /**
   * Return ScmSalesAgentServices[].
   *
   * @return
   */
  public SalesAgentServices[] getSalesAgentServicesSelected() {
    return salesAgentServicesSelected;
  }

  /**
   * Set ScmSalesAgentServices[].
   *
   * @param scmSalesAgentServicesSelected
   */
  public void setSalesAgentServicesSelected(SalesAgentServices[] salesAgentServicesSelected) {
    this.salesAgentServicesSelected = salesAgentServicesSelected;
  }

  public List<UserProfile> UserProfileAuto(String filter) {
    return ScmLookupView.userProfileAuto(filter);
  }

}
