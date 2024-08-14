/*
 * @(#)VendorSalesAgentView.java	1.0 Fri Dec 23 10:28:08 IST 2016 
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

import spica.scm.domain.VendorSalesAgent;
import spica.scm.service.VendorSalesAgentService;
import spica.scm.domain.Company;
import wawo.app.faces.Jsf;

/**
 * VendorSalesAgentView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:08 IST 2016
 */
@Named(value = "vendorSalesAgentView")
@ViewScoped
public class VendorSalesAgentView implements Serializable {

  private transient VendorSalesAgent vendorSalesAgent;	//Domain object/selected Domain.
  private transient LazyDataModel<VendorSalesAgent> vendorSalesAgentLazyModel; 	//For lazy loading datatable.
  private transient VendorSalesAgent[] vendorSalesAgentSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public VendorSalesAgentView() {
    super();
  }

  /**
   * Return VendorSalesAgent.
   *
   * @return VendorSalesAgent.
   */
  public VendorSalesAgent getVendorSalesAgent() {
    if (vendorSalesAgent == null) {
      vendorSalesAgent = new VendorSalesAgent();
    }
    return vendorSalesAgent;
  }

  /**
   * Set VendorSalesAgent.
   *
   * @param vendorSalesAgent.
   */
  public void setVendorSalesAgent(VendorSalesAgent vendorSalesAgent) {
    this.vendorSalesAgent = vendorSalesAgent;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchVendorSalesAgent(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getVendorSalesAgent().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setVendorSalesAgent((VendorSalesAgent) VendorSalesAgentService.selectByPk(main, getVendorSalesAgent()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadVendorSalesAgentList(main);
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
   * Create vendorSalesAgentLazyModel.
   *
   * @param main
   */
  private void loadVendorSalesAgentList(final MainView main) {
    if (vendorSalesAgentLazyModel == null) {
      vendorSalesAgentLazyModel = new LazyDataModel<VendorSalesAgent>() {
        private List<VendorSalesAgent> list;

        @Override
        public List<VendorSalesAgent> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = VendorSalesAgentService.listPaged(main);
            main.commit(vendorSalesAgentLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(VendorSalesAgent vendorSalesAgent) {
          return vendorSalesAgent.getId();
        }

        @Override
        public VendorSalesAgent getRowData(String rowKey) {
          if (list != null) {
            for (VendorSalesAgent obj : list) {
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
    String SUB_FOLDER = "scm_vendor_sales_agent/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveVendorSalesAgent(MainView main) {
    return saveOrCloneVendorSalesAgent(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneVendorSalesAgent(MainView main) {
    main.setViewType("newform");
    return saveOrCloneVendorSalesAgent(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneVendorSalesAgent(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            VendorSalesAgentService.insertOrUpdate(main, getVendorSalesAgent());
            break;
          case "clone":
            VendorSalesAgentService.clone(main, getVendorSalesAgent());
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
   * Delete one or many VendorSalesAgent.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteVendorSalesAgent(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(vendorSalesAgentSelected)) {
        VendorSalesAgentService.deleteByPkArray(main, getVendorSalesAgentSelected()); //many record delete from list
        main.commit("success.delete");
        vendorSalesAgentSelected = null;
      } else {
        VendorSalesAgentService.deleteByPk(main, getVendorSalesAgent());  //individual record delete from list or edit form
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
   * Return LazyDataModel of VendorSalesAgent.
   *
   * @return
   */
  public LazyDataModel<VendorSalesAgent> getVendorSalesAgentLazyModel() {
    return vendorSalesAgentLazyModel;
  }

  /**
   * Return VendorSalesAgent[].
   *
   * @return
   */
  public VendorSalesAgent[] getVendorSalesAgentSelected() {
    return vendorSalesAgentSelected;
  }

  /**
   * Set VendorSalesAgent[].
   *
   * @param vendorSalesAgentSelected
   */
  public void setVendorSalesAgentSelected(VendorSalesAgent[] vendorSalesAgentSelected) {
    this.vendorSalesAgentSelected = vendorSalesAgentSelected;
  }

  /**
   * Company autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.companyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.companyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Company> companyAuto(String filter) {
    return ScmLookupView.companyAuto(filter);
  }

  public void onRowEdit(VendorSalesAgent vendorSalesAgent) {
    MainView main = Jsf.getMain();;
    try {
      VendorSalesAgentService.insertOrUpdate(main, vendorSalesAgent);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

}
