/*
 * @(#)CustomerBankView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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

import spica.scm.domain.CustomerBank;
import spica.scm.service.CustomerBankService;
import spica.scm.domain.Customer;
import spica.scm.domain.Bank;
import spica.scm.domain.CustomerBankContact;
import spica.scm.domain.Status;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * CustomerBankView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "customerBankView")
@ViewScoped
public class CustomerBankView implements Serializable {

  private transient CustomerBank customerBank;	//Domain object/selected Domain.
  private transient LazyDataModel<CustomerBank> customerBankLazyModel; 	//For lazy loading datatable.
  private transient CustomerBank[] customerBankSelected;	 //Selected Domain Array
  private List<CustomerBankContact> customerBankContactList;
  private int activeIndex = 0; //tab active index, reset to 0 when new records

  /**
   * Default Constructor.
   */
  public CustomerBankView() {
    super();
  }

  /**
   * Return CustomerBank.
   *
   * @return CustomerBank.
   */
  public CustomerBank getCustomerBank() {
    if (customerBank == null) {
      customerBank = new CustomerBank();
    }
    return customerBank;
  }

  /**
   * Set CustomerBank.
   *
   * @param customerBank.
   */
  public void setCustomerBank(CustomerBank customerBank) {
    this.customerBank = customerBank;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCustomerBank(MainView main, String viewType) {
    //this.main = main;
    customerBankContactList = null;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        getCustomerBank().setCustomerId(parent);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          activeIndex = 0;
          getCustomerBank().reset();
          getCustomerBank().setCustomerId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCustomerBank((CustomerBank) CustomerBankService.selectByPk(main, getCustomerBank()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCustomerBankList(main);
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
    parent = (Customer) Jsf.popupParentValue(Customer.class);
    getCustomerBank().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create customerAddressLazyModel.
   *
   * @param main
   */
  /**
   * Create customerBankLazyModel.
   *
   * @param main
   */
  private void loadCustomerBankList(final MainView main) {
    if (customerBankLazyModel == null) {
      customerBankLazyModel = new LazyDataModel<CustomerBank>() {
        private List<CustomerBank> list;

        @Override
        public List<CustomerBank> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CustomerBankService.listPaged(main);
            main.commit(customerBankLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CustomerBank customerBank) {
          return customerBank.getId();
        }

        @Override
        public CustomerBank getRowData(String rowKey) {
          if (list != null) {
            for (CustomerBank obj : list) {
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
    String SUB_FOLDER = "scm_customer_bank/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCustomerBank(MainView main) {
    return saveOrCloneCustomerBank(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCustomerBank(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCustomerBank(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCustomerBank(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CustomerBankService.insertOrUpdate(main, getCustomerBank());
//            CustomerBankService.makeDefault(main, getCustomerBank());
            break;
          case "clone":
            CustomerBankService.clone(main, getCustomerBank());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error" + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many CustomerBank.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCustomerBank(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(customerBankSelected)) {
        CustomerBankService.deleteByPkArray(main, getCustomerBankSelected()); //many record delete from list
        main.commit("success.delete");
        customerBankSelected = null;
      } else {
        CustomerBankService.deleteByPk(main, getCustomerBank());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CustomerBank.
   *
   * @return
   */
  public LazyDataModel<CustomerBank> getCustomerBankLazyModel() {
    return customerBankLazyModel;
  }

  /**
   * Return CustomerBank[].
   *
   * @return
   */
  public CustomerBank[] getCustomerBankSelected() {
    return customerBankSelected;
  }

  /**
   * Set CustomerBank[].
   *
   * @param customerBankSelected
   */
  public void setCustomerBankSelected(CustomerBank[] customerBankSelected) {
    this.customerBankSelected = customerBankSelected;
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
   * BankAccountType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.bankAccountTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.bankAccountTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
//  public List<BankAccountType> bankAccountTypeAuto(String filter) {
//    return ScmLookupView.bankAccountTypeAuto(filter);
//  }
  /**
   * Bank autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.bankAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.bankAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Bank> bankAuto(String filter) {
    return ScmLookupExtView.bankAutoFilterByCompanyCountry(UserRuntimeView.instance().getCompany().getCountryId().getId(), filter);
    //return ScmLookupView.bankAuto(filter);
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void customerBankPopupClose() {
    Jsf.popupReturn(parent, null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
}
