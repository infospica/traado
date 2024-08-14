/*
 * @(#)CustomerLicenseView.java	1.0 Thu Jun 09 11:13:09 IST 2016 
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

import spica.scm.domain.CustomerLicense;
import spica.scm.service.CustomerLicenseService;
import spica.scm.domain.Customer;
import spica.scm.domain.LicenseType;
import spica.scm.domain.Status;
import wawo.app.faces.Jsf;

/**
 * CustomerLicenseView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 09 11:13:09 IST 2016
 */
@Named(value = "customerLicenseView")
@ViewScoped
public class CustomerLicenseView implements Serializable {

  private transient CustomerLicense customerLicense;	//Domain object/selected Domain.
  private transient LazyDataModel<CustomerLicense> customerLicenseLazyModel; 	//For lazy loading datatable.
  private transient CustomerLicense[] customerLicenseSelected;	 //Selected Domain Array
  private transient Part filePathPart;

  private Customer parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Customer) Jsf.popupParentValue(Customer.class);
    getCustomerLicense().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public CustomerLicenseView() {
    super();
  }

  public Customer getParent() {
    return parent;
  }

  public void setParent(Customer parent) {
    this.parent = parent;
  }

  /**
   * Return CustomerLicense.
   *
   * @return CustomerLicense.
   */
  public CustomerLicense getCustomerLicense() {
    if (customerLicense == null) {
      customerLicense = new CustomerLicense();
    }
    return customerLicense;
  }

  /**
   * Set CustomerLicense.
   *
   * @param customerLicense.
   */
  public void setCustomerLicense(CustomerLicense customerLicense) {
    this.customerLicense = customerLicense;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCustomerLicense(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCustomerLicense().reset();
          getCustomerLicense().setCustomerId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCustomerLicense((CustomerLicense) CustomerLicenseService.selectByPk(main, getCustomerLicense()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCustomerLicenseList(main);
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
   * Create customerLicenseLazyModel.
   *
   * @param main
   */
  private void loadCustomerLicenseList(final MainView main) {
    if (customerLicenseLazyModel == null) {
      customerLicenseLazyModel = new LazyDataModel<CustomerLicense>() {
        private List<CustomerLicense> list;

        @Override
        public List<CustomerLicense> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CustomerLicenseService.listPaged(main);
            main.commit(customerLicenseLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CustomerLicense customerLicense) {
          return customerLicense.getId();
        }

        @Override
        public CustomerLicense getRowData(String rowKey) {
          if (list != null) {
            for (CustomerLicense obj : list) {
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
    String SUB_FOLDER = "scm_customer_license/";
    if (filePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(filePathPart, getCustomerLicense().getFilePath(), SUB_FOLDER);
      getCustomerLicense().setFilePath(JsfIo.getDbPath(filePathPart, SUB_FOLDER));
      filePathPart = null;	//import to set as null.
    }
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCustomerLicense(MainView main) {
    return saveOrCloneCustomerLicense(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCustomerLicense(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCustomerLicense(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCustomerLicense(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            Date formDate = getCustomerLicense().getValidFrom();
            Date toDate = getCustomerLicense().getValidTo();
            int num = 0;
            if (formDate != null && toDate != null) {
              num = toDate.compareTo(formDate);
              if (num < 0 || num == 0) {
                main.error("error.date");
                return null;
              }
            }
            CustomerLicenseService.insertOrUpdate(main, getCustomerLicense());
            break;
          case "clone":
            CustomerLicenseService.clone(main, getCustomerLicense());
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
   * Delete one or many CustomerLicense.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCustomerLicense(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(customerLicenseSelected)) {
        CustomerLicenseService.deleteByPkArray(main, getCustomerLicenseSelected()); //many record delete from list
        main.commit("success.delete");
        customerLicenseSelected = null;
      } else {
        CustomerLicenseService.deleteByPk(main, getCustomerLicense());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CustomerLicense.
   *
   * @return
   */
  public LazyDataModel<CustomerLicense> getCustomerLicenseLazyModel() {
    return customerLicenseLazyModel;
  }

  /**
   * Return CustomerLicense[].
   *
   * @return
   */
  public CustomerLicense[] getCustomerLicenseSelected() {
    return customerLicenseSelected;
  }

  /**
   * Set CustomerLicense[].
   *
   * @param customerLicenseSelected
   */
  public void setCustomerLicenseSelected(CustomerLicense[] customerLicenseSelected) {
    this.customerLicenseSelected = customerLicenseSelected;
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
   * Customer autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.customerAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.customerAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Customer> customerAuto(String filter) {
    return ScmLookupView.customerAuto(filter);
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
  public void customerLicenseDocDialogClose() {
    Jsf.popupReturn(parent, null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
}
