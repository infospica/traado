/*
 * @(#)VendorClaimStatusView.java	1.0 Fri May 19 11:03:19 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.fin.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypeAction;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.VendorClaimStatus;
import spica.fin.service.VendorClaimStatusService;

/**
 * VendorClaimStatusView
 * @author	Spirit 1.2
 * @version	1.0, Fri May 19 11:03:19 IST 2017 
 */

@Named(value="vendorClaimStatusView")
@ViewScoped
public class VendorClaimStatusView implements Serializable{

  private transient VendorClaimStatus scmVendorClaimStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<VendorClaimStatus> scmVendorClaimStatusLazyModel; 	//For lazy loading datatable.
  private transient VendorClaimStatus[] scmVendorClaimStatusSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public VendorClaimStatusView() {
    super();
  }
 
  /**
   * Return VendorClaimStatus.
   * @return VendorClaimStatus.
   */  
  public VendorClaimStatus getVendorClaimStatus() {
    if(scmVendorClaimStatus == null) {
      scmVendorClaimStatus = new VendorClaimStatus();
    }
    return scmVendorClaimStatus;
  }   
  
  /**
   * Set VendorClaimStatus.
   * @param scmVendorClaimStatus.
   */   
  public void setVendorClaimStatus(VendorClaimStatus scmVendorClaimStatus) {
    this.scmVendorClaimStatus = scmVendorClaimStatus;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchVendorClaimStatus(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getVendorClaimStatus().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setVendorClaimStatus((VendorClaimStatus) VendorClaimStatusService.selectByPk(main, getVendorClaimStatus()));
        } else if (ViewTypes.isList(viewType)) {
          loadVendorClaimStatusList(main);
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
   * Create scmVendorClaimStatusLazyModel.
   * @param main
   */
  private void loadVendorClaimStatusList(final MainView main) {
    if (scmVendorClaimStatusLazyModel == null) {
      scmVendorClaimStatusLazyModel = new LazyDataModel<VendorClaimStatus>() {
      private List<VendorClaimStatus> list;      
      @Override
      public List<VendorClaimStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = VendorClaimStatusService.listPaged(main);
          main.commit(scmVendorClaimStatusLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(VendorClaimStatus scmVendorClaimStatus) {
        return scmVendorClaimStatus.getId();
      }
      @Override
        public VendorClaimStatus getRowData(String rowKey) {
          if (list != null) {
            for (VendorClaimStatus obj : list) {
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
    String SUB_FOLDER = "scm_vendor_claim_status/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveVendorClaimStatus(MainView main) {
    return saveOrCloneVendorClaimStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneVendorClaimStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneVendorClaimStatus(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneVendorClaimStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            VendorClaimStatusService.insertOrUpdate(main, getVendorClaimStatus());
            break;
          case "clone":
            VendorClaimStatusService.clone(main, getVendorClaimStatus());
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
   * Delete one or many VendorClaimStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteVendorClaimStatus(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(scmVendorClaimStatusSelected)) {
        VendorClaimStatusService.deleteByPkArray(main, getVendorClaimStatusSelected()); //many record delete from list
        main.commit("success.delete");
        scmVendorClaimStatusSelected = null;
      } else {
        VendorClaimStatusService.deleteByPk(main, getVendorClaimStatus());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (ViewTypes.isEdit(main.getViewType())){
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
   * Return LazyDataModel of VendorClaimStatus.
   * @return
   */
  public LazyDataModel<VendorClaimStatus> getVendorClaimStatusLazyModel() {
    return scmVendorClaimStatusLazyModel;
  }

 /**
  * Return VendorClaimStatus[].
  * @return 
  */
  public VendorClaimStatus[] getVendorClaimStatusSelected() {
    return scmVendorClaimStatusSelected;
  }
  
  /**
   * Set VendorClaimStatus[].
   * @param scmVendorClaimStatusSelected 
   */
  public void setVendorClaimStatusSelected(VendorClaimStatus[] scmVendorClaimStatusSelected) {
    this.scmVendorClaimStatusSelected = scmVendorClaimStatusSelected;
  }
 


}
