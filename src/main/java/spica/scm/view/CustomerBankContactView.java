/*
 * @(#)CustomerBankContactView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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

import spica.scm.domain.CustomerBankContact;
import spica.scm.service.CustomerBankContactService;
import spica.scm.domain.CustomerBank;
import spica.scm.domain.Designation;
import spica.scm.domain.Status;
import spica.scm.service.DesignationService;
import wawo.app.faces.Jsf;

/**
 * CustomerBankContactView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "customerBankContactView")
@ViewScoped
public class CustomerBankContactView implements Serializable {

  private transient CustomerBankContact customerBankContact;	//Domain object/selected Domain.
  private transient LazyDataModel<CustomerBankContact> customerBankContactLazyModel; 	//For lazy loading datatable.
  private transient CustomerBankContact[] customerBankContactSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CustomerBankContactView() {
    super();
  }

  /**
   * Return CustomerBankContact.
   *
   * @return CustomerBankContact.
   */
  public CustomerBankContact getCustomerBankContact() {
    if (customerBankContact == null) {
      customerBankContact = new CustomerBankContact();
    }
    return customerBankContact;
  }

  /**
   * Set CustomerBankContact.
   *
   * @param customerBankContact.
   */
  public void setCustomerBankContact(CustomerBankContact customerBankContact) {
    this.customerBankContact = customerBankContact;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCustomerBankContact(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        getCustomerBankContact().setCustomerBankAcId(parent);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCustomerBankContact().reset();
          getCustomerBankContact().setCustomerBankAcId(parent); //1 set the parent for new records
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCustomerBankContact((CustomerBankContact) CustomerBankContactService.selectByPk(main, getCustomerBankContact()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCustomerBankContactList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private CustomerBank parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (CustomerBank) Jsf.popupParentValue(CustomerBank.class);
    getCustomerBankContact().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create customerBankContactLazyModel.
   *
   * @param main
   */
  private void loadCustomerBankContactList(final MainView main) {
    if (customerBankContactLazyModel == null) {
      customerBankContactLazyModel = new LazyDataModel<CustomerBankContact>() {
        private List<CustomerBankContact> list;

        @Override
        public List<CustomerBankContact> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CustomerBankContactService.listPaged(main, parent); //passing the parent to exclude from select list which are already selected
            main.commit(customerBankContactLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CustomerBankContact customerBankContact) {
          return customerBankContact.getId();
        }

        @Override
        public CustomerBankContact getRowData(String rowKey) {
          if (list != null) {
            for (CustomerBankContact obj : list) {
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
    String SUB_FOLDER = "scm_customer_bank_contact/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCustomerBankContact(MainView main) {
    return saveOrCloneCustomerBankContact(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCustomerBankContact(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCustomerBankContact(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCustomerBankContact(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CustomerBankContactService.insertOrUpdate(main, getCustomerBankContact());
//            CustomerBankContactService.makeDefault(main, getCustomerBankContact());
            break;
          case "clone":
            CustomerBankContactService.clone(main, getCustomerBankContact());
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
   * Delete one or many CustomerBankContact.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCustomerBankContact(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(customerBankContactSelected)) {
        CustomerBankContactService.deleteByPkArray(main, getCustomerBankContactSelected()); //many record delete from list
        main.commit("success.delete");
        customerBankContactSelected = null;
      } else {
        CustomerBankContactService.deleteByPk(main, getCustomerBankContact());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CustomerBankContact.
   *
   * @return
   */
  public LazyDataModel<CustomerBankContact> getCustomerBankContactLazyModel() {
    return customerBankContactLazyModel;
  }

  /**
   * Return CustomerBankContact[].
   *
   * @return
   */
  public CustomerBankContact[] getCustomerBankContactSelected() {
    return customerBankContactSelected;
  }

  /**
   * Set CustomerBankContact[].
   *
   * @param customerBankContactSelected
   */
  public void setCustomerBankContactSelected(CustomerBankContact[] customerBankContactSelected) {
    this.customerBankContactSelected = customerBankContactSelected;
  }

  /**
   * CustomerBank autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.customerBankAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.customerBankAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<CustomerBank> customerBankAuto(String filter) {
    return ScmLookupView.customerBankAuto(filter);
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
  public void customerBankContactPopupClose() {
    Jsf.popupClose(parent);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  public List<Designation> selectDesignationByCustomerContext(MainView main) {
    return DesignationService.selectDesignationByCustomerContext(main);
  }
}
