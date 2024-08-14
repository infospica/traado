/*
 * @(#)VendorCommodityView.java	1.0 Mon Jun 13 13:51:31 IST 2016 
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

import spica.scm.domain.VendorCommodity;
import spica.scm.service.VendorCommodityService;
import spica.scm.domain.Vendor;
import spica.scm.domain.ServiceCommodity;

/**
 * VendorCommodityView
 * @author	Spirit 1.2
 * @version	1.0, Mon Jun 13 13:51:31 IST 2016 
 */

@Named(value="vendorCommodityView")
@ViewScoped
public class VendorCommodityView implements Serializable{

  private transient VendorCommodity vendorCommodity;	//Domain object/selected Domain.
  private transient LazyDataModel<VendorCommodity> vendorCommodityLazyModel; 	//For lazy loading datatable.
  private transient VendorCommodity[] vendorCommoditySelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public VendorCommodityView() {
    super();
  }
 
  /**
   * Return VendorCommodity.
   * @return VendorCommodity.
   */  
  public VendorCommodity getVendorCommodity() {
    if(vendorCommodity == null) {
      vendorCommodity = new VendorCommodity();
    }
    return vendorCommodity;
  }   
  
  /**
   * Set VendorCommodity.
   * @param vendorCommodity.
   */   
  public void setVendorCommodity(VendorCommodity vendorCommodity) {
    this.vendorCommodity = vendorCommodity;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchVendorCommodity(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getVendorCommodity().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setVendorCommodity((VendorCommodity) VendorCommodityService.selectByPk(main, getVendorCommodity()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadVendorCommodityList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally{
        main.close();
      }
    }
    return null;
  } 
  
  /**
   * Create vendorCommodityLazyModel.
   * @param main
   */
  private void loadVendorCommodityList(final MainView main) {
    if (vendorCommodityLazyModel == null) {
      vendorCommodityLazyModel = new LazyDataModel<VendorCommodity>() {
      private List<VendorCommodity> list;      
      @Override
      public List<VendorCommodity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = VendorCommodityService.listPaged(main);
          main.commit(vendorCommodityLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(VendorCommodity vendorCommodity) {
        return vendorCommodity.getId();
      }
      @Override
        public VendorCommodity getRowData(String rowKey) {
          if (list != null) {
            for (VendorCommodity obj : list) {
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
    String SUB_FOLDER = "scm_vendor_commodity/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveVendorCommodity(MainView main) {
    return saveOrCloneVendorCommodity(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneVendorCommodity(MainView main) {
    main.setViewType("newform");
    return saveOrCloneVendorCommodity(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneVendorCommodity(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            VendorCommodityService.insertOrUpdate(main, getVendorCommodity());
            break;
          case "clone":
            VendorCommodityService.clone(main, getVendorCommodity());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error."+ key);
    } finally {
      main.close();
    }
    return null;
  }

  
  /**
   * Delete one or many VendorCommodity.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteVendorCommodity(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(vendorCommoditySelected)) {
        VendorCommodityService.deleteByPkArray(main, getVendorCommoditySelected()); //many record delete from list
        main.commit("success.delete");
        vendorCommoditySelected = null;
      } else {
        VendorCommodityService.deleteByPk(main, getVendorCommodity());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())){
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
   * Return LazyDataModel of VendorCommodity.
   * @return
   */
  public LazyDataModel<VendorCommodity> getVendorCommodityLazyModel() {
    return vendorCommodityLazyModel;
  }

 /**
  * Return VendorCommodity[].
  * @return 
  */
  public VendorCommodity[] getVendorCommoditySelected() {
    return vendorCommoditySelected;
  }
  
  /**
   * Set VendorCommodity[].
   * @param vendorCommoditySelected 
   */
  public void setVendorCommoditySelected(VendorCommodity[] vendorCommoditySelected) {
    this.vendorCommoditySelected = vendorCommoditySelected;
  }
 


 /**
  * Vendor autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.vendorAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.vendorAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<Vendor> vendorAuto(String filter) {
    return ScmLookupView.vendorAuto(filter);
  }
 /**
  * Commodity autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.commodityAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.commodityAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<ServiceCommodity> commodityAuto(String filter) {
    return ScmLookupView.commodityAuto(filter);
  }
}
