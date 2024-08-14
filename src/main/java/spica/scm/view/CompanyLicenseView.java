/*
 * @(#)CompanyLicenseView.java	1.0 Thu Jun 09 11:13:09 IST 2016 
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

import spica.scm.domain.CompanyLicense;
import spica.scm.service.CompanyLicenseService;
import spica.scm.domain.Company;
import spica.scm.domain.LicenseType;
import spica.scm.domain.Status;
import wawo.app.faces.Jsf;
import wawo.entity.util.DateUtil;

/**
 * CompanyLicenseView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 09 11:13:09 IST 2016
 */
@Named(value = "companyLicenseView")
@ViewScoped
public class CompanyLicenseView implements Serializable {

  private transient CompanyLicense companyLicense;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyLicense> companyLicenseLazyModel; 	//For lazy loading datatable.
  private transient CompanyLicense[] companyLicenseSelected;	 //Selected Domain Array
  private transient Part filePathPart;
  private Company parent;
  private transient Date fromMin;
  private transient Date fromMax;
  private transient Date toMin;
  private transient Date toMax;
  
  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Company) Jsf.popupParentValue(Company.class);
    getCompanyLicense().setId(Jsf.getParameterInt("id"));
    fromMin = DateUtil.instance().moveYears(new Date(), -20);
    fromMax = DateUtil.instance().moveYears(new Date(), 1);
    toMin = DateUtil.instance().moveYears(new Date(), -5);
    toMax = DateUtil.instance().moveYears(new Date(), 10);
  }

  public Company getParent() {
    return parent;
  }

  public void setParent(Company parent) {
    this.parent = parent;
  }

  /**
   * Default Constructor.
   */
  public CompanyLicenseView() {
    super();
  }

  /**
   * Return CompanyLicense.
   *
   * @return CompanyLicense.
   */
  public CompanyLicense getCompanyLicense() {
    if (companyLicense == null) {
      companyLicense = new CompanyLicense();
    }
    return companyLicense;
  }

  /**
   * Set CompanyLicense.
   *
   * @param companyLicense.
   */
  public void setCompanyLicense(CompanyLicense companyLicense) {
    this.companyLicense = companyLicense;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyLicense(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCompanyLicense().reset();
          getCompanyLicense().setCompanyId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompanyLicense((CompanyLicense) CompanyLicenseService.selectByPk(main, getCompanyLicense()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyLicenseList(main);
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
   * Create companyLicenseLazyModel.
   *
   * @param main
   */
  private void loadCompanyLicenseList(final MainView main) {
    if (companyLicenseLazyModel == null) {
      companyLicenseLazyModel = new LazyDataModel<CompanyLicense>() {
        private List<CompanyLicense> list;

        @Override
        public List<CompanyLicense> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyLicenseService.listPaged(main);
            main.commit(companyLicenseLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyLicense companyLicense) {
          return companyLicense.getId();
        }

        @Override
        public CompanyLicense getRowData(String rowKey) {
          if (list != null) {
            for (CompanyLicense obj : list) {
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
    String SUB_FOLDER = "scm_company_license/";
    if (filePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(filePathPart, getCompanyLicense().getFilePath(), SUB_FOLDER);
      getCompanyLicense().setFilePath(JsfIo.getDbPath(filePathPart, SUB_FOLDER));
      filePathPart = null;	//import to set as null.
    }
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyLicense(MainView main) {
    getCompanyLicense().setCompanyId(parent);
    return saveOrCloneCompanyLicense(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyLicense(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyLicense(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyLicense(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            Date formDate = getCompanyLicense().getValidFrom();
            Date toDate = getCompanyLicense().getValidTo();
            int num = 0;
            num = toDate.compareTo(formDate);
            if (num < 0 || num == 0) {
              main.error("error.date");
              return null;
            }
            CompanyLicenseService.insertOrUpdate(main, getCompanyLicense());
            break;
          case "clone":
            CompanyLicenseService.clone(main, getCompanyLicense());
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
   * Delete one or many CompanyLicense.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyLicense(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyLicenseSelected)) {
        CompanyLicenseService.deleteByPkArray(main, getCompanyLicenseSelected()); //many record delete from list
        main.commit("success.delete");
        companyLicenseSelected = null;
      } else {
        CompanyLicenseService.deleteByPk(main, getCompanyLicense());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyLicense.
   *
   * @return
   */
  public LazyDataModel<CompanyLicense> getCompanyLicenseLazyModel() {
    return companyLicenseLazyModel;
  }

  /**
   * Return CompanyLicense[].
   *
   * @return
   */
  public CompanyLicense[] getCompanyLicenseSelected() {
    return companyLicenseSelected;
  }

  /**
   * Set CompanyLicense[].
   *
   * @param companyLicenseSelected
   */
  public void setCompanyLicenseSelected(CompanyLicense[] companyLicenseSelected) {
    this.companyLicenseSelected = companyLicenseSelected;
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

  public void companyLicenseDocPopupClose() {
    Jsf.popupReturn(parent,null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  public Date getFromMin() {
    return fromMin;
  }

  public void setFromMin(Date fromMin) {
    this.fromMin = fromMin;
  }

  public Date getFromMax() {
    return fromMax;
  }

  public void setFromMax(Date fromMax) {
    this.fromMax = fromMax;
  }

  public Date getToMin() {
    return toMin;
  }

  public void setToMin(Date toMin) {
    this.toMin = toMin;
  }

  public Date getToMax() {
    return toMax;
  }

  public void setToMax(Date toMax) {
    this.toMax = toMax;
  }


  
  
  
}
