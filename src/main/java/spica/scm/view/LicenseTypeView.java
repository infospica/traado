/*
 * @(#)LicenseTypeView.java	1.0 Thu Jun 09 11:13:09 IST 2016 
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

import spica.scm.domain.LicenseType;
import spica.scm.service.LicenseTypeService;

/**
 * LicenseTypeView
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 09 11:13:09 IST 2016 
 */

@Named(value="licenseTypeView")
@ViewScoped
public class LicenseTypeView implements Serializable{

  private transient LicenseType licenseType;	//Domain object/selected Domain.
  private transient LazyDataModel<LicenseType> licenseTypeLazyModel; 	//For lazy loading datatable.
  private transient LicenseType[] licenseTypeSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public LicenseTypeView() {
    super();
  }
 
  /**
   * Return LicenseType.
   * @return LicenseType.
   */  
  public LicenseType getLicenseType() {
    if(licenseType == null) {
      licenseType = new LicenseType();
    }
    return licenseType;
  }   
  
  /**
   * Set LicenseType.
   * @param licenseType.
   */   
  public void setLicenseType(LicenseType licenseType) {
    this.licenseType = licenseType;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchLicenseType(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getLicenseType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setLicenseType((LicenseType) LicenseTypeService.selectByPk(main, getLicenseType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadLicenseTypeList(main);
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
   * Create licenseTypeLazyModel.
   * @param main
   */
  private void loadLicenseTypeList(final MainView main) {
    if (licenseTypeLazyModel == null) {
      licenseTypeLazyModel = new LazyDataModel<LicenseType>() {
      private List<LicenseType> list;      
      @Override
      public List<LicenseType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = LicenseTypeService.listPaged(main);
          main.commit(licenseTypeLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(LicenseType licenseType) {
        return licenseType.getId();
      }
      @Override
        public LicenseType getRowData(String rowKey) {
          if (list != null) {
            for (LicenseType obj : list) {
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
    String SUB_FOLDER = "scm_license_type/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveLicenseType(MainView main) {
    return saveOrCloneLicenseType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneLicenseType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneLicenseType(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneLicenseType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            LicenseTypeService.insertOrUpdate(main, getLicenseType());
            break;
          case "clone":
            LicenseTypeService.clone(main, getLicenseType());
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
   * Delete one or many LicenseType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteLicenseType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(licenseTypeSelected)) {
        LicenseTypeService.deleteByPkArray(main, getLicenseTypeSelected()); //many record delete from list
        main.commit("success.delete");
        licenseTypeSelected = null;
      } else {
        LicenseTypeService.deleteByPk(main, getLicenseType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of LicenseType.
   * @return
   */
  public LazyDataModel<LicenseType> getLicenseTypeLazyModel() {
    return licenseTypeLazyModel;
  }

 /**
  * Return LicenseType[].
  * @return 
  */
  public LicenseType[] getLicenseTypeSelected() {
    return licenseTypeSelected;
  }
  
  /**
   * Set LicenseType[].
   * @param licenseTypeSelected 
   */
  public void setLicenseTypeSelected(LicenseType[] licenseTypeSelected) {
    this.licenseTypeSelected = licenseTypeSelected;
  }
 


}
