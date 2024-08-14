/*
 * @(#)VendorLicenseView.java	1.0 Thu Jun 09 11:13:09 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.VendorLicense;
import spica.scm.service.VendorLicenseService;
import spica.scm.domain.Vendor;
import spica.scm.domain.LicenseType;
import spica.scm.domain.Status;
import wawo.app.faces.Jsf;

/**
 * VendorLicenseView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 09 11:13:09 IST 2016
 */
@Named(value = "vendorLicenseView")
@ViewScoped
public class VendorLicenseView implements Serializable {

  private transient VendorLicense vendorLicense;	//Domain object/selected Domain.
  private transient LazyDataModel<VendorLicense> vendorLicenseLazyModel; 	//For lazy loading datatable.
  private transient VendorLicense[] vendorLicenseSelected;	 //Selected Domain Array
  private transient Part filePathPart;

  private Vendor parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Vendor) Jsf.popupParentValue(Vendor.class);
    getVendorLicense().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public VendorLicenseView() {
    super();
  }

  public Vendor getParent() {
    return parent;
  }

  public void setParent(Vendor parent) {
    this.parent = parent;
  }

  /**
   * Return VendorLicense.
   *
   * @return VendorLicense.
   */
  public VendorLicense getVendorLicense() {
    if (vendorLicense == null) {
      vendorLicense = new VendorLicense();
    }
    return vendorLicense;
  }

  /**
   * Set VendorLicense.
   *
   * @param vendorLicense.
   */
  public void setVendorLicense(VendorLicense vendorLicense) {
    this.vendorLicense = vendorLicense;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchVendorLicense(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getVendorLicense().reset();
          getVendorLicense().setVendorId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setVendorLicense((VendorLicense) VendorLicenseService.selectByPk(main, getVendorLicense()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadVendorLicenseList(main);
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
   * Create vendorLicenseLazyModel.
   *
   * @param main
   */
  private void loadVendorLicenseList(final MainView main) {
    if (vendorLicenseLazyModel == null) {
      vendorLicenseLazyModel = new LazyDataModel<VendorLicense>() {
        private List<VendorLicense> list;

        @Override
        public List<VendorLicense> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = VendorLicenseService.listPaged(main);
            main.commit(vendorLicenseLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(VendorLicense vendorLicense) {
          return vendorLicense.getId();
        }

        @Override
        public VendorLicense getRowData(String rowKey) {
          if (list != null) {
            for (VendorLicense obj : list) {
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

  private void uploadFiles() throws IOException {
    String SUB_FOLDER = "scm_vendor_license/";
    if (filePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(filePathPart, getVendorLicense().getFilePath(), SUB_FOLDER);
      getVendorLicense().setFilePath(JsfIo.getDbPath(filePathPart, SUB_FOLDER));
      filePathPart = null;	//import to set as null.
    }
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveVendorLicense(MainView main) {
    return saveOrCloneVendorLicense(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneVendorLicense(MainView main) {
    main.setViewType("newform");
    return saveOrCloneVendorLicense(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneVendorLicense(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            Date formDate = getVendorLicense().getValidFrom();
            Date toDate = getVendorLicense().getValidTo();
            int num = 0;
            if (formDate != null && toDate != null) {
              num = toDate.compareTo(formDate);
              if (num < 0 || num == 0) {
                main.error("error.date");
                return null;
              }
            }
            VendorLicenseService.insertOrUpdate(main, getVendorLicense());
            break;
          case "clone":
            VendorLicenseService.clone(main, getVendorLicense());
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
   * Delete one or many VendorLicense.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteVendorLicense(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(vendorLicenseSelected)) {
        VendorLicenseService.deleteByPkArray(main, getVendorLicenseSelected()); //many record delete from list
        main.commit("success.delete");
        vendorLicenseSelected = null;
      } else {
        VendorLicenseService.deleteByPk(main, getVendorLicense());  //individual record delete from list or edit form
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
   * Return LazyDataModel of VendorLicense.
   *
   * @return
   */
  public LazyDataModel<VendorLicense> getVendorLicenseLazyModel() {
    return vendorLicenseLazyModel;
  }

  /**
   * Return VendorLicense[].
   *
   * @return
   */
  public VendorLicense[] getVendorLicenseSelected() {
    return vendorLicenseSelected;
  }

  /**
   * Set VendorLicense[].
   *
   * @param vendorLicenseSelected
   */
  public void setVendorLicenseSelected(VendorLicense[] vendorLicenseSelected) {
    this.vendorLicenseSelected = vendorLicenseSelected;
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getFilePathPart() {
    return filePathPart;
  }

  /**
   * Set Part filePathPart.
   *
   * @param filePathPart.
   */
  public void setFilePathPart(Part filePathPart) {
    if (this.filePathPart == null || filePathPart != null) {
      this.filePathPart = filePathPart;
    }
  }

  /**
   * Vendor autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.vendorAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.vendorAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Vendor> vendorAuto(String filter) {
    return ScmLookupView.vendorAuto(filter);
  }

  /**
   * LicenseType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.licenseTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.licenseTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<LicenseType> licenseTypeAuto(String filter) {
    return ScmLookupView.licenseTypeAuto(filter);
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void vendorLicenseDocDialogClose() {
    Jsf.popupReturn(parent, null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
}
