/*
 * @(#)CustomerContactView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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

import spica.scm.domain.CustomerContact;
import spica.scm.service.CustomerContactService;
import spica.scm.domain.Customer;
import spica.scm.domain.Designation;
import spica.scm.domain.Status;
import spica.scm.service.DesignationService;
import wawo.app.faces.Jsf;

/**
 * CustomerContactView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "customerContactView")
@ViewScoped
public class CustomerContactView implements Serializable {

  private transient CustomerContact customerContact;	//Domain object/selected Domain.
  private transient LazyDataModel<CustomerContact> customerContactLazyModel; 	//For lazy loading datatable.
  private transient CustomerContact[] customerContactSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CustomerContactView() {
    super();
  }

  /**
   * Return CustomerContact.
   *
   * @return CustomerContact.
   */
  public CustomerContact getCustomerContact() {
    if (customerContact == null) {
      customerContact = new CustomerContact();
    }
    return customerContact;
  }

  /**
   * Set CustomerContact.
   *
   * @param customerContact.
   */
  public void setCustomerContact(CustomerContact customerContact) {
    this.customerContact = customerContact;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCustomerContact(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCustomerContact().reset();
          getCustomerContact().setCustomerId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCustomerContact((CustomerContact) CustomerContactService.selectByPk(main, getCustomerContact()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCustomerContactList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }
  private Customer parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Customer) Jsf.dialogParent(Customer.class);
    getCustomerContact().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create customerContactLazyModel.
   *
   * @param main
   */
  private void loadCustomerContactList(final MainView main) {
    if (customerContactLazyModel == null) {
      customerContactLazyModel = new LazyDataModel<CustomerContact>() {
        private List<CustomerContact> list;

        @Override
        public List<CustomerContact> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CustomerContactService.listPaged(main);
            main.commit(customerContactLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CustomerContact customerContact) {
          return customerContact.getId();
        }

        @Override
        public CustomerContact getRowData(String rowKey) {
          if (list != null) {
            for (CustomerContact obj : list) {
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
    String SUB_FOLDER = "scm_customer_contact/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCustomerContact(MainView main) {
    return saveOrCloneCustomerContact(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCustomerContact(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCustomerContact(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCustomerContact(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CustomerContactService.insertOrUpdate(main, getCustomerContact());
//            CustomerContactService.makeDefault(main, getCustomerContact());
            break;
          case "clone":
            CustomerContactService.clone(main, getCustomerContact());
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
   * Delete one or many CustomerContact.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCustomerContact(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(customerContactSelected)) {
        CustomerContactService.deleteByPkArray(main, getCustomerContactSelected()); //many record delete from list
        main.commit("success.delete");
        customerContactSelected = null;
      } else {
        CustomerContactService.deleteByPk(main, getCustomerContact());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CustomerContact.
   *
   * @return
   */
  public LazyDataModel<CustomerContact> getCustomerContactLazyModel() {
    return customerContactLazyModel;
  }

  /**
   * Return CustomerContact[].
   *
   * @return
   */
  public CustomerContact[] getCustomerContactSelected() {
    return customerContactSelected;
  }

  /**
   * Set CustomerContact[].
   *
   * @param customerContactSelected
   */
  public void setCustomerContactSelected(CustomerContact[] customerContactSelected) {
    this.customerContactSelected = customerContactSelected;
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
   * Designation autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.designationAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.designationAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Designation> designationAuto(String filter) {
    return ScmLookupView.designationAuto(filter);
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void customerContactDialogClose() {
    Jsf.returnDialog(null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  public List<Designation> selectDesignationByCustomerContext(MainView main) {
    try {
      return DesignationService.selectDesignationByCustomerContext(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void onRowEdit(CustomerContact customerContact) {
    MainView main = Jsf.getMain();
    try {
      CustomerContactService.insertOrUpdate(main, customerContact);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }
}
