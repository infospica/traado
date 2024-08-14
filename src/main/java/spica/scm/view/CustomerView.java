/*
 * @(#)CustomerView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.constant.AccountingConstant;
import spica.fin.view.FinLookupView;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.ChequeEntry;
import spica.fin.service.AccountingLedgerService;
import spica.fin.service.ChequeEntryService;
import spica.fin.view.AccountingMainView;
import spica.scm.domain.Company;
import spica.scm.domain.Country;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerBank;
import spica.scm.domain.CustomerContact;
import spica.scm.domain.District;
import spica.scm.domain.SalesAccount;
import spica.scm.domain.State;
import spica.scm.domain.Territory;
import spica.scm.domain.TradeProfile;
import spica.scm.service.AddressTypeService;
import spica.scm.service.CustomerAddressService;
import spica.scm.service.CustomerBankService;
import spica.scm.service.CustomerContactService;
import spica.scm.service.CustomerService;
import spica.scm.service.SalesAccountService;
import spica.scm.service.StateService;
import spica.scm.service.TradeProfileService;
import spica.scm.util.AppUtil;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import spica.reports.model.CustomerOutstanding;
import spica.reports.model.FilterParameters;
import spica.reports.service.CustomerOutstandingService;
import spica.scm.common.CustomerHealth;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.CustomerLicense;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.Transporter;
import spica.scm.domain.UserProfile;
import spica.scm.service.AccountService;
import spica.scm.service.CustomerLicenseService;
import spica.scm.service.VendorService;
import wawo.app.faces.Jsf;
import wawo.entity.util.DateUtil;

/**
 * CustomerView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "customerView")
@ViewScoped
public class CustomerView implements Serializable {

  private transient Customer customer;	//Domain object/selected Domain.
  private transient LazyDataModel<Customer> customerLazyModel; 	//For lazy loading datatable.
  private transient Customer[] customerSelected;	 //Selected Domain Array
  private List<CustomerAddress> customerAddressList;
  private List<CustomerContact> customerContactList;
  private List<CustomerBank> customerBankList;
  private List<CustomerLicense> customerLicenseList;
  private int activeIndex = 0; //tab active index, reset to 0 when new records
  private List<TradeProfile> tradeProfileSelected;//1 List to hold the relation selection
  //private List<TradeProfile> selectedTradeProfile;//1 List to hold the relation selection
  private TradeProfile tradeProfile;
  private List<Customer> customerBranchList;
  // private CustomerTradeProfile customerTradeProfile = null;
  private transient CustomerAddress customerRegAddress;
  private transient Integer pinLength;
  private transient Integer pinValue;
  private AccountGroup accountGroup;
  private UserProfile salesAgent;
  private Account account;

  @Inject
  private AccountingMainView accountingMainView;

  //TODO move customer health to a new view if required
  @PostConstruct
  public void init() {
    Integer customerId = (Integer) Jsf.popupParentValue(Integer.class);
    if (customerId != null) {
      setCustomer(new Customer(customerId));
    } else { //To upport account group by filter from sales invoice
      SalesInvoice invoice = (SalesInvoice) Jsf.popupParentValue(SalesInvoice.class);
      if (invoice != null) {
        setCustomer(invoice.getCustomerId());
        setAccountGroup(invoice.getAccountGroupId());
      }
    }
  }

  /**
   * Default Constructor.
   */
  public CustomerView() {
    super();
  }

  /**
   * Return Customer.
   *
   * @return Customer.
   */
  public Customer getCustomer() {
    if (customer == null) {
      customer = new Customer();
    }
    if (customer.getCompanyId() == null) {
      customer.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return customer;
  }

  /**
   * Set Customer.
   *
   * @param customer.
   */
  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public CustomerAddress getCustomerRegAddress() {
    if (customerRegAddress == null) {
      customerRegAddress = new CustomerAddress();
    }
    return customerRegAddress;
  }

  public void setCustomerRegAddress(CustomerAddress customerRegAddress) {
    this.customerRegAddress = customerRegAddress;
  }
//
//  public CustomerTradeProfile getCustomerTradeProfile() {
//    return customerTradeProfile;
//  }
//
//  public void setCustomerTradeProfile(CustomerTradeProfile customerTradeProfile) {
//    this.customerTradeProfile = customerTradeProfile;
//  }

//  public Company getCompany() {
//    return UserRuntimeView.instance().getCompany();
//  }
  public TradeProfile getTradeProfile() {
    return tradeProfile;
  }

  public void setTradeProfile(TradeProfile tradeProfile) {
    this.tradeProfile = tradeProfile;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCustomer(MainView main, String viewType) {
    //this.main = main;
    customerAddressList = null;  // Set to null to fetch the latest record
    customerContactList = null;
    customerBankList = null;
    customerBranchList = null;
    customerLicenseList = null;
    salesAccountList = null;
    if (!StringUtil.isEmpty(viewType)) {
      customerOutstandingList = null;
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          }
          setActiveIndex(0);
          getCustomer().reset();
          getCustomerRegAddress().reset();
          getCustomer().setCountryId(getCustomer().getCompanyId().getCountryId());
          getCustomer().setCurrencyId(getCustomer().getCompanyId().getCurrencyId());
          tradeProfile = null; //2 Reset to null for new records          
        } else if (main.isEdit() && !main.hasError()) {
          setActiveIndex(0);
          setCustomer((Customer) CustomerService.selectByPk(main, getCustomer()));
          if (getCustomer().getCountryId().equals(UserRuntimeService.USA)) {
            pinLength = 5;
            pinValue = 99999;
          } else {
            pinLength = 6;
            pinValue = 999999;
          }
          setCustomerRegAddress(CustomerAddressService.selectCustomerRegisteredAddress(main, getCustomer()));
          tradeProfile = TradeProfileService.selectTradeProfileByCustomer(main, getCustomer()); //3 Loading the selected records
          relatedChequeEntryList = null;
          accountingLedger = null;
//          if (getCustomer().getCurrencyId() == null) {
//            getCustomer().setCurrencyId(getCompany().getCurrencyId());
//          }
        } else if (main.isList()) {
          accountList = null;
          salesAgentList = null;
          getCustomer().setCompanyId(null);
          getCustomerRegAddress().reset();
          loadCustomerList(main);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create customerLazyModel.
   *
   * @param main
   */
  private void loadCustomerList(final MainView main) {
    if (customerLazyModel == null) {
      customerLazyModel = new LazyDataModel<Customer>() {
        private List<Customer> list;

        @Override
        public List<Customer> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            if (UserRuntimeView.instance().getCompany() != null) {
              list = CustomerService.listPaged(main, getCustomer().getCompanyId());
              main.commit(customerLazyModel, first, pageSize);
            }
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Customer customer) {
          return customer.getId();
        }

        @Override
        public Customer getRowData(String rowKey) {
          if (list != null) {
            for (Customer obj : list) {
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
    String SUB_FOLDER = "scm_customer/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCustomer(MainView main) {
    return saveOrCloneCustomer(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCustomer(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCustomer(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCustomer(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CustomerService.insertOrUpdate(main, getCustomer(), getTradeProfile(), getCustomerRegAddress());
            break;
          case "clone":
            CustomerService.clone(main, getCustomer(), getTradeProfile(), getCustomerRegAddress());
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
   * Delete one or many Customer.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCustomer(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(customerSelected)) {
        CustomerService.deleteByPkArray(main, getCustomerSelected()); //many record delete from list
        main.commit("success.delete");
        customerSelected = null;
      } else {
        CustomerService.deleteByPk(main, getCustomer());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Customer.
   *
   * @return
   */
  public LazyDataModel<Customer> getCustomerLazyModel() {
    return customerLazyModel;
  }

  /**
   * Return Customer[].
   *
   * @return
   */
  public Customer[] getCustomerSelected() {
    return customerSelected;
  }

  /**
   * Set Customer[].
   *
   * @param customerSelected
   */
  public void setCustomerSelected(Customer[] customerSelected) {
    this.customerSelected = customerSelected;
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
   * Return all customer address of a customer.
   *
   * @param main
   * @return
   */
  /**
   * Company autocomplete filter.
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
//    return ScmLookupExtView.customerAuto(filter, UserRuntimeView.instance().getCompany().getId());
    if (getCustomer() != null && getCustomer().getId() != null) {
//      if(ScmLookupExtView.parentCustomerAuto(getCustomer().getId(), filter).size()==0)
//        return null;
//      else
      return ScmLookupExtView.parentCustomerAuto(getCustomer().getId(), filter, getCustomer().getCompanyId().getId());
    }
    return ScmLookupExtView.customerAuto(getCustomer().getCompanyId().getId());
  }

  public List<CustomerAddress> getCustomerAddressList(MainView main) {
    if (customerAddressList == null) {
      try {
        customerAddressList = CustomerAddressService.addressListByCustomer(main, getCustomer());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return customerAddressList;
  }

  public List<Country> countryAuto(String filter) {
    return ScmLookupView.countryAuto(filter);
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<State> stateAuto(String filter) {
    if (getCustomer().getCountryId() != null && getCustomer().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getCustomer().getCountryId().getId(), filter);
    }
    return null;
  }

  public List<District> districtAuto(String filter) {
    if (getCustomer().getStateId() != null && getCustomer().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getCustomer().getStateId().getId(), filter);
    }
    return null;
  }

  public List<Territory> territoryAuto(String filter) {
    if (getCustomerRegAddress().getDistrictId() != null && getCustomerRegAddress().getDistrictId().getId() != null) {
      return ScmLookupExtView.addressTerritoryAuto(getCustomer().getCompanyId().getId(), getCustomerRegAddress().getDistrictId().getId(), filter);
    }
    return null;
  }

  /**
   *
   * @param event
   */
//  public void countrySelectEvent(SelectEvent event) {
//    Country country = (Country) event.getObject();
//    if (country != null) {
//      if (getCustomer().getCountryId() == null) {
//        getCustomer().setCountryId(country);
//      } else if (!getCustomer().getCountryId().getId().equals(country.getId())) {
//        getCustomer().setCountryId(country);
//        getCustomer().setStateId(null);
//        getCustomerRegAddress().setDistrictId(null);
//      }
//    } else {
//      getCustomer().setStateId(null);
//      getCustomerRegAddress().setDistrictId(null);
//    }
//  }
  /**
   *
   * @param event
   */
  public void stateSelectEvent(SelectEvent event) {
    State state = (State) event.getObject();
    if (state != null) {
      if (getCustomer().getStateId() == null) {
        getCustomer().setStateId(state);
      } else if (!getCustomer().getStateId().getId().equals(state.getId())) {
        getCustomer().setStateId(state);
        getCustomerRegAddress().setDistrictId(null);
      }
    } else {
      getCustomer().setStateId(null);
      getCustomerRegAddress().setDistrictId(null);
    }
  }

  /**
   * Return all customer address of a customer.
   *
   * @param main
   * @return
   */
  public List<CustomerContact> getCustomerContactList(MainView main) {
    if (customerContactList == null) {
      try {
        customerContactList = CustomerContactService.contactListByCustomer(main, getCustomer());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return customerContactList;
  }

  /**
   * Return all branches of a Customer.
   *
   * @param main
   * @return
   */
  public List<Customer> getCustomerBranchList(MainView main) {
    if (customerBranchList == null) {
      try {
        customerBranchList = CustomerService.branchListByCustomer(main, getCustomer());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return customerBranchList;
  }

  /**
   * Opening add new page in a dialog.
   */
  public void customerAddressNewPopup() {
    Jsf.popupForm(FileConstant.CUSTOMER_ADDRESS, getCustomer());
  }

  /**
   * Closing the dialog.
   *
   * @param event
   */
  public void customerAddressPopupReturned() {
    CustomerAddress address = (CustomerAddress) Jsf.popupParentValue(CustomerAddress.class);
    if (address != null && address.getAddressTypeId() != null && address.getAddressTypeId().getId().equals(AddressTypeService.REGISTERED_ADDRESS)) {
      setCustomerRegAddress(address);
    }
    customerAddressList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void customerAddressEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.CUSTOMER_ADDRESS, getCustomer(), id);
  }

  /**
   * Opening add new page in a dialog.
   */
  public void customerContactNewDialog() {
    Jsf.openDialog(FileConstant.CUSTOMER_CONTACT, getCustomer());
  }

  /**
   * Closing the dialog.
   *
   * @param event
   */
  public void customerContactPopupReturned() {
    customerContactList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void customerContactEditDialog(Integer id) {
    Jsf.openDialog(FileConstant.CUSTOMER_CONTACT, getCustomer(), id);
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  /**
   * Return all bank contact of a customer.
   *
   * @param main
   * @return
   */
  public List<CustomerBank> getCustomerBankList(MainView main) {
    if (customerBankList == null) {
      try {
        customerBankList = CustomerBankService.bankListByCustomer(main, getCustomer());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return customerBankList;
  }

  /**
   * Opening add new page in a dialog.
   */
  public void customerBankNewPopup() {
    Jsf.popupForm(FileConstant.CUSTOMER_BANK, getCustomer());
  }

  /**
   * Closing the dialog.
   *
   * @param event
   */
  public void customerBankPopupReturned() {
    customerBankList = null; // Reset to null to fetch updated list
  }

  public void customerSaalesAccPopupReturned() {
    salesAccountList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void customerBankEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.CUSTOMER_BANK, getCustomer(), id);
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param customerBank
   */
  public void customerBankContactListPopup(CustomerBank customerBank) {
    Jsf.popupList(FileConstant.CUSTOMER_BANK_CONTACT, customerBank);
  }

  public void customerBankContactNewPopup(CustomerBank customerBank) {
    Jsf.popupForm(FileConstant.CUSTOMER_BANK_CONTACT, customerBank);
  }

  public void customerBankContactDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getCustomer());
  }

  public void addNewSalesAccount() {
    Jsf.popupForm(FileConstant.CUSTOMER_SALES_ACCOUNT, getCustomer());
  }

  public void customerSalesAccEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.CUSTOMER_SALES_ACCOUNT, getCustomer(), id);
  }

  public void userProfileNewDialog(int k) {
    Jsf.popupList(FileConstant.USER_PROFILE, getCustomer());
  }

  public List<TradeProfile> getTradeProfileSelected() {
    if (tradeProfileSelected == null) {
      tradeProfileSelected = ScmLookupExtView.tradeProfileByCustomer();
    }
    return tradeProfileSelected;
  }

  public void setTradeProfileSelected(List<TradeProfile> tradeProfileSelected) {
    this.tradeProfileSelected = tradeProfileSelected;
  }

  public Integer getPinLength() {
    return pinLength;
  }

  public void setPinLength(Integer pinLength) {
    this.pinLength = pinLength;
  }

  public Integer getPinValue() {
    return pinValue;
  }

  public void setPinValue(Integer pinValue) {
    this.pinValue = pinValue;
  }

  public String deleteCustomerContact(MainView main, CustomerContact customerContact) {
    if (customerContact.getId() == null) {
      customerContactList.remove(customerContact);
    } else {
      try {
        CustomerContactService.deleteCustomerContact(main, customerContact);
        main.commit("success.delete");
        customerContactList.remove(customerContact);
      } catch (Throwable t) {
        main.rollback(t, "error.delete");
      } finally {
        main.close();
      }
    }
    return null;
  }

  private transient List<SalesAccount> salesAccountList;

  public List<SalesAccount> getSalesAccountList(MainView main) {
    if (salesAccountList == null) {
      try {
        salesAccountList = SalesAccountService.getSalesAccountList(main, getCustomer().getId(), getCustomer().getCompanyId().getId());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return salesAccountList;
  }

  public void onRowEdit(RowEditEvent event) {
    MainView main = null;
    try {
      SalesAccount salesAccount = (SalesAccount) event.getObject();
      main = Jsf.getMain();
      salesAccount.setCustomerId(getCustomer());
      salesAccount.setCompanyId(getCustomer().getCompanyId());
      SalesAccountService.insertOrUpdate(main, salesAccount);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  /**
   * GSTIN ajax change event handler.
   */
  public void gstinChangeEventHandler() {
    MainView main = Jsf.getMain();
    try {
      if (!StringUtil.isEmpty(getCustomer().getGstNo())) {
        updateStateAndPan(main, getCustomer().getGstNo());
      } else {
        getCustomer().setGstNo(null);
        getCustomer().setStateId(null);
        getCustomer().setPanNo(null);
        getCustomerRegAddress().setDistrictId(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void updateStateAndPan(MainView main, String gstin) {
    if (AppUtil.isValidGstin(gstin)) {
      getCustomer().setPanNo(gstin.substring(2, 12));

      if (getCustomer().getCountryId() != null) {
        State state = StateService.selectStateByStateCodeAndCountry(main, getCustomer().getCountryId(), gstin.substring(0, 2));
        if (state != null) {
          getCustomer().setStateId(state);
          if (getCustomerRegAddress().getDistrictId() != null && !getCustomerRegAddress().getDistrictId().getStateId().getId().equals(state.getId())) {
            getCustomerRegAddress().setDistrictId(null);
          }
        } else {
          getCustomer().setStateId(null);
          getCustomerRegAddress().setDistrictId(null);
        }
      }
    }
  }

  /**
   *
   * @param event
   */
  public void countrySelectEvent(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      Country country = (Country) event.getObject();
      if (country != null) {
        if (country.equals(UserRuntimeService.USA)) {
          pinLength = 5;
          pinValue = 99999;
        } else {
          pinLength = 6;
          pinValue = 999999;
        }
        if (getCustomer().getCountryId() == null) {
          getCustomer().setCountryId(country);
          if (!StringUtil.isEmpty(getCustomer().getGstNo())) {
            updateStateAndPan(main, getCustomer().getGstNo());
          }
        } else if (!getCustomer().getCountryId().getId().equals(country.getId())) {
          getCustomer().setCountryId(country);
          if (!StringUtil.isEmpty(getCustomer().getGstNo())) {
            updateStateAndPan(main, getCustomer().getGstNo());
          } else {
            getCustomer().setStateId(null);
          }
          getCustomerRegAddress().setDistrictId(null);
        }
        if (country.getCurrencyId() != null) {
          getCustomer().setCurrencyId(country.getCurrencyId());
        }
      } else {
        getCustomer().setStateId(null);
        getCustomerRegAddress().setDistrictId(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<AccountingLedger> accountingLedgerSalesItemsAuto(String filter) {
    return FinLookupView.ledgerSalesProductAuto(filter, getCustomer().getCompanyId().getId());
  }

  public boolean isConsumer() {
    return (tradeProfile != null && tradeProfile.getId().equals(TradeProfileService.TRADE_TYPE_CONSUMER));
  }

  public void customerLicenseNewDialog() {
    Jsf.popupForm(FileConstant.CUSTOMER_LICENSE, getCustomer());
  }

  public void customerLicenseEditDialog(Integer id) {
    Jsf.popupForm(FileConstant.CUSTOMER_LICENSE, getCustomer(), id);
  }

  public void customerLicenseDialogReturn() {
    customerLicenseList = null; // Reset to null to fetch updated list
  }

  public List<CustomerLicense> getCustomerLicenseList(MainView main) {
    if (customerLicenseList == null) {
      try {
        customerLicenseList = CustomerLicenseService.licenseListByCustomer(main, getCustomer());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return customerLicenseList;
  }

  public void setCustomerLicenseList(List<CustomerLicense> customerLicenseList) {
    this.customerLicenseList = customerLicenseList;
  }

  public List<Transporter> transporterAuto(String filter) {
    return ScmLookupExtView.transporterByCompanyAuto(getCustomer().getCompanyId().getId(), filter);
  }

  public void countryOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.BASIC_COUNTRY, new District()); // opens a new form if id is null else edit
    }
  }

  public void stateOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.BASIC_STATE, new State()); // opens a new form if id is null else edit
    }
  }

  public void districtOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.BASIC_DISTRICT, new District()); // opens a new form if id is null else edit
    }
  }
  private transient List<CustomerOutstanding> customerOutstandingList;
  private Double receivableAmount = 0.0;
  private Double receivedAmount = 0.0;
  private Double balance = 0.0;
  private Double billAmount = 0.0;
  private Double cumulative = 0.0;
  private FilterParameters filterParameters;

  public void outStandingByCustomer(MainView main) {
    if (StringUtil.isEmpty(customerOutstandingList) && !main.hasError()) {
      getFilterParameters().setCustomer(getCustomer());
      getFilterParameters().setAccountGroup(getAccountGroup());
//      getFilterParameters().setVendor(getSalesAgent());
      try {
        customerOutstandingList = CustomerOutstandingService.getCustomerOutstandingList(main, filterParameters, AccountingConstant.ACC_ENTITY_CUSTOMER.getId(), null, null, null);
        Integer customerId = 0;
        receivableAmount = 0.0;
        receivedAmount = 0.0;
        billAmount = 0.0;
        balance = 0.0;
        cumulative = 0.0;
        for (CustomerOutstanding outStanding : customerOutstandingList) {
          billAmount += (outStanding.getInvoiceamount() == null ? 0 : outStanding.getInvoiceamount());
          balance += (outStanding.getOustandingamount() == null ? 0 : outStanding.getOustandingamount());
          outStanding.setReceivedAmount((outStanding.getInvoiceamount() == null ? 0 : outStanding.getInvoiceamount()) - (outStanding.getOustandingamount() == null ? 0 : outStanding.getOustandingamount()));
          receivedAmount += (outStanding.getReceivedAmount() == null ? 0 : outStanding.getReceivedAmount());
          if (StringUtils.isEmpty(outStanding.getAgentName())) {
            outStanding.setAgentName(getCustomer().getCompanyId().getCompanyName());
          }
          cumulative = customerId.intValue() == outStanding.getCustomerId() ? cumulative : 0.0;
          cumulative += (outStanding.getOustandingamount() == null ? 0 : outStanding.getOustandingamount());
          outStanding.setCumulativeAmount(cumulative);
          customerId = outStanding.getCustomerId();

        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
//    return null;
  }

  public Double getBillAmount() {
    return billAmount;
  }

  public Double getReceivedAmount() {
    return receivedAmount;
  }

  public Double getBalance() {
    return balance;
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
    }
    return filterParameters;
  }
  /////////To show outstanding 
  private transient AccountingTransactionDetail transactionDetail;
  // private transient Double debitTotal;
  // private transient Double creditTotal;
  private AccountingLedger accountingLedger;

  public void actionOutStandingAmount(MainView main) {
    if (transactionDetail == null) {
      transactionDetail = new AccountingTransactionDetail();
    }
    transactionDetail.reset();
    try {
      AccountingLedger accLedger = AccountingLedgerService.selectLedgerByEntity(main, getCustomer().getId(), AccountingConstant.ACC_ENTITY_CUSTOMER, getCustomer().getCompanyId().getId());
//      accountingLedger = (AccountingLedger) AppService.single(m, AccountingLedger.class, "select * from fin_accounting_ledger where entity_id=? and accounting_entity_type_id=?", new Object[]{getCustomer().getId(), AccountingConstant.ACC_ENTITY_CUSTOMER.getId()});
      Jsf.popupForm("/accounting/accounting_ledger_transaction.xhtml", accLedger, accLedger.getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public AccountingLedger getAccountingLedger() {
    return accountingLedger;
  }

  private CustomerHealth customerHealth;
  private transient List<ChequeEntry> relatedChequeEntryList;
  private transient Double crLineTotal = 0.00;
  private transient Double drLineTotal = 0.00;

  public void getSalesHealthStatus(MainView main) {
    try {
      Calendar c = Calendar.getInstance();   // this takes current date
      c.set(Calendar.DAY_OF_MONTH, 1);
      // System.out.println(c.getTime());
      Date date = DateUtil.moveMonths(c.getTime(), -3);
      customerHealth = new CustomerHealth();
      customerHealth.setLastFromDate(date);
      customerHealth.setLastToDate(DateUtil.moveDays(c.getTime(), -1));
      customerHealth.setPreviousFromDate(DateUtil.moveMonths(c.getTime(), -1));
      customerHealth.setPreviousToDate(DateUtil.moveDays(c.getTime(), -1));
      customerHealth.setCurrentFromDate(c.getTime());
      customerHealth.setCurrentToDate(new Date());
      customerHealth.setCustomerId(getCustomer().getId());
      customerHealth = CustomerService.selectLastMonthsSales(main, customerHealth, getCustomer().getCompanyId().getId(), getAccountGroup(), getSalesAgent(), getAccount());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void loadRelatedChequeEntryList(MainView main) {
    if (StringUtil.isEmpty(relatedChequeEntryList)) {
      crLineTotal = 0.0;
      try {
        accountingMainView.setSelectedAccountGroup(getAccountGroup());
        if (getAccountingLedger() == null) {
          accountingLedger = AccountingLedgerService.selectLedgerByEntity(main, getCustomer().getId(), AccountingConstant.ACC_ENTITY_CUSTOMER, getCustomer().getCompanyId().getId());
        }
        relatedChequeEntryList = ChequeEntryService.listAllByCustomer(main, getCustomer().getCompanyId(), getAccountingLedger().getId(), getAccountingLedger().getEntityId(), accountingMainView);
        for (ChequeEntry cheque : relatedChequeEntryList) {
          crLineTotal += cheque.getAmount();
          cheque.setCrLine(crLineTotal);
        }
        balance = null;
//          getBalanceTotal();
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  public CustomerHealth getCustomerHealth() {
    return customerHealth;
  }

  public List<ChequeEntry> getRelatedChequeEntryList() {
    return relatedChequeEntryList;
  }

  public Double getCrLineTotal() {
    return crLineTotal;
  }

  public List<CustomerOutstanding> getCustomerOutstandingList() {
    return customerOutstandingList;
  }

  public Double getCumulative() {
    return cumulative;
  }

  public void showCustomerHealth(Integer entityId) {
    Jsf.popupForm(FileConstant.CUSTOMER_HEALTH_POPUP, entityId, entityId);
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public List<AccountGroup> getAccountGroupList() {
    return ScmLookupExtView.accountGroupByCustomerId(getCustomer().getId());
  }

  private transient List<Account> accountList = null;

  public List<Account> getAccountList(MainView main) {
    try {
      if (accountList == null) {
        accountList = AccountService.selectAccountByCustomer(main, getCustomer());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return accountList;
  }

  private transient List<UserProfile> salesAgentList = null;

  public List getSalesAgentList(MainView main) {
    if (salesAgentList == null) {
      try {

        salesAgentList = VendorService.selectVendorsByCustomerAndAccountGroup(main, getCustomer().getId(), getCustomer().getCompanyId().getId(), getAccountGroup());

      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return salesAgentList;
  }

  public UserProfile getSalesAgent() {
    return salesAgent;
  }

  public void setSalesAgent(UserProfile salesAgent) {
    this.salesAgent = salesAgent;
  }

  public void reset() {
    relatedChequeEntryList = null;
    customerOutstandingList = null;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

}
