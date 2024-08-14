/*
 * @(#)CommodityView.java	1.0 Mon Aug 08 17:59:15 IST 2016
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

import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.service.CommodityService;
import spica.fin.domain.TaxCode;
import spica.scm.util.CountryTaxRegimeUtil;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * CommodityView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "servicesView")
@ViewScoped
public class ServicesView implements Serializable {

  private transient ServiceCommodity commodity;	//Domain object/selected Domain.
  private transient LazyDataModel<ServiceCommodity> commodityLazyModel; 	//For lazy loading datatable.
  private transient ServiceCommodity[] commoditySelected;	 //Selected Domain Array
  private transient Company company;

  @PostConstruct
  public void init() {
    commodity = (ServiceCommodity) Jsf.popupParentValue(ServiceCommodity.class);
  }

  /**
   * Default Constructor.
   */
  public ServicesView() {
    super();
  }

  /**
   * Return Commodity.
   *
   * @return Commodity.
   */
  public ServiceCommodity getCommodity() {
    if (commodity == null) {
      commodity = new ServiceCommodity();
    }
    return commodity;
  }

  /**
   * Set Commodity.
   *
   * @param commodity.
   */
  public void setCommodity(ServiceCommodity commodity) {
    this.commodity = commodity;
  }

  public Company getCompany() {
    if (company == null) {
      company = UserRuntimeView.instance().getCompany();
    }
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  /**
   * Return LazyDataModel of Commodity.
   *
   * @return
   */
  public LazyDataModel<ServiceCommodity> getCommodityLazyModel() {
    return commodityLazyModel;
  }

  /**
   * Return Commodity[].
   *
   * @return
   */
  public ServiceCommodity[] getCommoditySelected() {
    return commoditySelected;
  }

  /**
   * Set Commodity[].
   *
   * @param commoditySelected
   */
  public void setCommoditySelected(ServiceCommodity[] commoditySelected) {
    this.commoditySelected = commoditySelected;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCommodity(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCommodity().reset();
          getCommodity().setCommodityOrService(CommodityService.SERVICE);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCommodity((ServiceCommodity) CommodityService.selectByPk(main, getCommodity()));
        } else if (ViewType.list.toString().equals(viewType)) {
          setCompany(null);
          loadCommodityList(main);
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
   * Create commodityLazyModel.
   *
   * @param main
   */
  private void loadCommodityList(final MainView main) {
    if (commodityLazyModel == null) {
      commodityLazyModel = new LazyDataModel<ServiceCommodity>() {
        private List<ServiceCommodity> list;

        @Override
        public List<ServiceCommodity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getCompany() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = CommodityService.listPaged(main, getCompany(), CommodityService.SERVICE);
              main.commit(commodityLazyModel, first, pageSize);
            }
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ServiceCommodity commodity) {
          return commodity.getId();
        }

        @Override
        public ServiceCommodity getRowData(String rowKey) {
          if (list != null) {
            for (ServiceCommodity obj : list) {
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
    String SUB_FOLDER = "scm_service_commodity/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCommodity(MainView main) {
    return saveOrCloneCommodity(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCommodity(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCommodity(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCommodity(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            getCommodity().setCompanyId(getCompany());
            CommodityService.insertOrUpdate(main, getCommodity());
            break;
          case "clone":    
            CommodityService.clone(main, getCommodity());
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
   * Delete one or many Commodity.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCommodity(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(commoditySelected)) {
        CommodityService.deleteByPkArray(main, getCommoditySelected()); //many record delete from list
        main.commit("success.delete");
        commoditySelected = null;
      } else {
        CommodityService.deleteByPk(main, getCommodity());  //individual record delete from list or edit form
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

  public boolean isEditable(ServiceCommodity commodity) {
    if (commodity != null) {
      if (!UserRuntimeView.instance().getAppUser().isRoot()) {
        if (commodity.getCompanyId() != null) {
          return true;
        } else {
          return false;
        }
      }
    }
    return true;
  }

  public List<TaxCode> lookupGstTaxCode() {
    if (getCompany() != null) {
      return ScmLookupExtView.lookupGstTaxCode(getCompany());
    }
    return null;
  }

  public List<TaxCode> lookupTdsTaxCode() {
    if (getCompany() != null) {
      return ScmLookupExtView.lookupTdsTaxCode(getCompany());
    }
    return null;
  }

  public boolean isGstRegime() {
    return CountryTaxRegimeUtil.isGstRegime(getCompany());
  }
}
