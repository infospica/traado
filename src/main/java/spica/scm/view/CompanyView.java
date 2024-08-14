/*
 * @(#)CompanyView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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
import spica.scm.domain.AddressType;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyBank;
import spica.scm.domain.CompanyBranch;
import spica.scm.domain.CompanyContact;
import spica.scm.domain.CompanyInvestor;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanyReference;
import spica.scm.domain.CompanyType;
import spica.scm.domain.CompanyWarehouse;
import spica.scm.domain.Country;
import spica.scm.domain.CountryTaxRegime;
import spica.scm.domain.Designation;
import spica.scm.domain.District;
import spica.scm.domain.SalesAgentContract;
import spica.scm.domain.State;
import spica.scm.domain.Status;
import spica.scm.domain.UserProfile;
import spica.scm.service.AccountService;
import spica.scm.service.AddressTypeService;
import spica.scm.service.CompanyAddressService;
import spica.scm.service.CompanyBankService;
import spica.scm.service.CompanyBranchService;
import spica.scm.service.CompanyContactService;
import spica.scm.service.CompanyInvestorService;
import spica.scm.service.CompanyLicenseService;
import spica.scm.service.CompanyReferenceService;
import spica.scm.service.CompanyService;
import spica.scm.service.CompanyWarehouseService;
import spica.scm.service.CountryTaxRegimeService;
import spica.scm.service.SalesAgentContractService;
import spica.scm.service.StateService;
import spica.scm.service.StatusService;
import spica.scm.service.UserProfileService;
import spica.scm.util.AppUtil;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * CompanyView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "companyView")
@ViewScoped
public class CompanyView implements Serializable {

  private transient Company company;	//Domain object/selected Domain.
  private transient LazyDataModel<Company> companyLazyModel; 	//For lazy loading datatable.
  private transient Company[] companySelected;	 //Selected Domain Array
  private transient List<CompanyAddress> companyAddressList;
  private transient List<CompanyContact> companyContactList;
  private transient List<SalesAgentContract> salesAgentList;
  private transient List<CompanyInvestor> companyInvestorList;
  private transient List<CompanyBank> companyBankList;
  private transient List<CompanyReference> companyReferenceList;
  private transient List<CompanyLicense> companyLicenseList;
  private transient List<CompanyWarehouse> companyWarehouseList;
  private transient List<CompanyBranch> companyBranchesList;
  private transient List<Company> companyBranchList;
  private int activeIndex = 0; //tab active index, reset to 0 when new records
  private Double totalAmt = 0.0;
  private transient List<UserProfile> userProfileList;
  private transient CompanyAddress companyRegAddress;
  private transient boolean wareHouse;
  private transient Integer pinLength;
  private transient Integer pinValue;
  private transient boolean accountExist;
  private Part logoFilePath;
  private transient boolean docPrefixBoolean = false;

  /**
   * Default Constructor.
   */
  public CompanyView() {
    super();
  }

  /**
   * Return Company.
   *
   * @return Company.
   */
  public Company getCompany() {
    if (company == null) {
      company = new Company();
    }
    return company;
  }

  /**
   * Set Company.
   *
   * @param company.
   */
  public void setCompany(Company company) {
    this.company = company;
  }

  public CompanyAddress getCompanyRegAddress() {
    if (companyRegAddress == null) {
      companyRegAddress = new CompanyAddress();
    }
    return companyRegAddress;
  }

  public void setCompanyRegAddress(CompanyAddress companyRegAddress) {
    this.companyRegAddress = companyRegAddress;
  }

  public boolean isAccountExist() {
    return accountExist;
  }

  public void setAccountExist(boolean accountExist) {
    this.accountExist = accountExist;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompany(MainView main, String viewType) {
    //this.main = main;
    companyAddressList = null; // Set to null to fetch the latest record
    companyContactList = null; // Set to null to fetch the latest record   
    salesAgentList = null;
    companyBankList = null; // Set to null to fetch the latest record   
    companyReferenceList = null;
    companyLicenseList = null;
    companyWarehouseList = null;
    companyBranchList = null;
    companyBranchesList = null;
    companyInvestorList = null;
    userProfileList = null;
    logoFilePath = null;

    setTotalAmt(null);
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          setActiveIndex(0);
          getCompany().reset();
          getCompany().setDocPrefixBasedOnCompany(0);
          setTotalAmt(null);
          setCompanyRegAddress(null);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompany((Company) CompanyService.selectByPk(main, UserRuntimeView.instance().getCompany()));
          setCompanyRegAddress(CompanyAddressService.selectCompanyRegisteredAddress(main, getCompany()));
          setAccountExist(AccountService.isCompanyHaveAccount(main, getCompany()));
          setActiveIndex(0);
          if (getCompany().getCountryId().equals(UserRuntimeService.USA)) {
            pinLength = 5;
            pinValue = 99999;
          } else {
            pinLength = 6;
            pinValue = 999999;
          }
          //if(UserRuntimeView.instance().getAccount()==null){
          //  UserRuntimeView.instance().changeCompany(main);
          //}
        } else if (ViewType.list.toString().equals(viewType)) {
          setCompanyRegAddress(null);
          setAccountExist(false);
          loadCompanyList(main);
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
   * Create companyLazyModel.
   *
   * @param main
   */
  private void loadCompanyList(final MainView main) {
    if (companyLazyModel == null) {
      companyLazyModel = new LazyDataModel<Company>() {
        private List<Company> list;

        @Override
        public List<Company> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyService.listPaged(main);
            main.commit(companyLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Company company) {
          return company.getId();
        }

        @Override
        public Company getRowData(String rowKey) {
          if (list != null) {
            for (Company obj : list) {
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
    String SUB_FOLDER = "scm_company/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompany(MainView main) {
    return saveOrCloneCompany(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompany(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompany(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompany(MainView main, String key) {
    try {
      uploadFiles(); //File upload      
      if (getCompany().getGstNo() != null) {
        getCompany().setGstNo(getCompany().getGstNo().toUpperCase());
      }
      if (null != key) {
        switch (key) {
          case "save":
            if (getCompanyRegAddress() != null) {
              getCompany().setCompanyAddress(getCompanyRegAddress().getAddress());
              getCompany().setCompanyPin(getCompanyRegAddress().getPin());
            }
            CompanyService.insertOrUpdate(main, getCompany());
            if (getCompanyRegAddress() != null && getCompanyRegAddress().getId() == null) {
              AddressType addressType = new AddressType();
              addressType.setId(AddressTypeService.REGISTERED_ADDRESS);
              getCompanyRegAddress().setAddressTypeId(addressType);
              getCompanyRegAddress().setCompanyId(getCompany());
              getCompanyRegAddress().setCountryId(getCompany().getCountryId());
              getCompanyRegAddress().setStateId(getCompany().getStateId());
              Status status = new Status();
              status.setId(StatusService.STATUS_ACTIVE);
              getCompanyRegAddress().setStatusId(status);

            } else {
              getCompanyRegAddress().setCountryId(getCompany().getCountryId());
              getCompanyRegAddress().setStateId(getCompany().getStateId());
            }
            CompanyAddressService.insertOrUpdate(main, getCompanyRegAddress());
            if (isWareHouse()) {
              CompanyWarehouse companyWarehouse = new CompanyWarehouse();
              companyWarehouse.setCompanyId(getCompany());
              if (getCompany().getCompanyName().length() >= 4) {
                companyWarehouse.setWarehouseName(getCompany().getCompanyName().substring(0, 4) + "_WHO1");
              } else {
                companyWarehouse.setWarehouseName(getCompany().getCompanyName() + "_WHO1");
              }
              companyWarehouse.setAddress(getCompanyRegAddress().getAddress());
              companyWarehouse.setCountryId(getCompany().getCountryId());
              companyWarehouse.setStateId(getCompany().getStateId());
              companyWarehouse.setDistrictId(getCompanyRegAddress().getDistrictId());
              companyWarehouse.setPhone1(getCompanyRegAddress().getPhone1());
              companyWarehouse.setEmail(getCompanyRegAddress().getEmail());
              companyWarehouse.setSortOrder(1);
              Status status = new Status();
              status.setId(StatusService.STATUS_ACTIVE);
              companyWarehouse.setStatusId(status);
              CompanyWarehouseService.insertOrUpdate(main, companyWarehouse);
              setWareHouse(false);
            }
            break;
          case "clone":
            CompanyService.clone(main, getCompany());
            break;
        }
        main.commit("success." + key);
//        UserRuntimeView.instance().setCompanyList(null);
//        UserRuntimeView.instance().getCompanyList();
        UserRuntimeView.instance().getCompanyList(main).remove(getCompany());
        UserRuntimeView.instance().getCompanyList(main).add(getCompany());
        UserRuntimeView.instance().setCompany(getCompany());
        UserRuntimeView.instance().changeCompany(main);
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
   * Delete one or many Company.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompany(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companySelected)) {
        CompanyService.deleteByPkArray(main, getCompanySelected()); //many record delete from list
        main.commit("success.delete");
        companySelected = null;
      } else {
        CompanyService.deleteByPk(main, getCompany());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())) {
          main.setViewType(ViewTypes.newform);
        }
      }
      UserRuntimeView.instance().getCompanyList(main).remove(getCompany());
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Return LazyDataModel of Company.
   *
   * @return
   */
  public LazyDataModel<Company> getCompanyLazyModel() {
    return companyLazyModel;
  }

  /**
   * Return Company[].
   *
   * @return
   */
  public Company[] getCompanySelected() {
    return companySelected;
  }

  /**
   * Set Company[].
   *
   * @param companySelected
   */
  public void setCompanySelected(Company[] companySelected) {
    this.companySelected = companySelected;
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
    if (getCompany() != null && getCompany().getId() != null) {
      return ScmLookupExtView.parentCompanyAuto(getCompany().getId(), filter);
    }
    return ScmLookupView.companyAuto(filter);
  }

  /**
   * Country autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.countryAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.countryAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Country> countryAuto(String filter) {
    return ScmLookupView.countryAuto(filter);
  }

  /**
   * State autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.stateAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.stateAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<State> stateAuto(String filter) {
    if (getCompany().getCountryId() != null && getCompany().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getCompany().getCountryId().getId(), filter);
    }
    return null;
  }

  /**
   * District autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.districtAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.districtAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<District> districtAuto(String filter) {
    if (getCompany().getStateId() != null && getCompany().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getCompany().getStateId().getId(), filter);
    }
    return null;
  }

  /**
   * Return all company address of a company.
   *
   * @param main
   * @return
   */
  public List<CompanyAddress> getCompanyAddressList(MainView main) {
    if (companyAddressList == null) {
      try {
        companyAddressList = CompanyAddressService.addressListByCompany(main, getCompany());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return companyAddressList;
  }

  /**
   * Return all company contact of a company.
   *
   * @param main
   * @return
   */
  public List<CompanyContact> getCompanyContactList(MainView main) {
    if (companyContactList == null) {
      try {
        companyContactList = CompanyContactService.contactListByCompany(main, getCompany());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companyContactList;
  }

  /**
   *
   * @param main
   * @return
   */
  public List<SalesAgentContract> getCompanySalesAgentList(MainView main) {
    if (salesAgentList == null) {
      try {
        salesAgentList = SalesAgentContractService.salesAgentContarctListByCompany(main, getCompany());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return salesAgentList;
  }

  /**
   *
   * @param main
   * @return
   */
  public List<UserProfile> getUserProfileList(MainView main) {
    if (userProfileList == null) {
      try {
        if (UserRuntimeView.instance().getCompany() != null) {
          userProfileList = UserProfileService.listPagedNotInCompanySalesAgent(main, UserRuntimeView.instance().getCompany(), null, null);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return userProfileList;
  }

  /**
   * Return all bank of a company.
   *
   * @param main
   * @return
   */
  public List<CompanyBank> getCompanyBankList(MainView main) {
    if (companyBankList == null) {
      try {
        companyBankList = CompanyBankService.bankListByCompany(main, getCompany());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return companyBankList;
  }

  /**
   *
   * @param main
   * @return
   */
  public List<CompanyLicense> getCompanyLicenseList(MainView main) {
    if (companyLicenseList == null) {
      try {
        companyLicenseList = CompanyLicenseService.licenseListByCompany(main, getCompany());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companyLicenseList;
  }

  public void setCompanyLicenseList(List<CompanyLicense> companyLicenseList) {
    this.companyLicenseList = companyLicenseList;
  }

  /**
   * Return all reference of a company.
   *
   * @param main
   * @return
   */
  public List<CompanyReference> getCompanyReferenceList(MainView main) {
    if (companyReferenceList == null) {
      try {
        companyReferenceList = CompanyReferenceService.referenceListByCompany(main, getCompany());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companyReferenceList;
  }

  /**
   * Return all warehouse of a company.
   *
   * @param main
   * @return
   */
  public List<CompanyWarehouse> getCompanyWarehouseList(MainView main) {
    if (companyWarehouseList == null) {
      try {
        companyWarehouseList = CompanyWarehouseService.warehouseListByCompany(main, getCompany());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companyWarehouseList;
  }

  /**
   *
   * @param main
   * @return
   */
  public List<CompanyBranch> getCompanyBranchesList(MainView main) {
    if (companyBranchesList == null) {
      try {
        companyBranchesList = CompanyBranchService.branchesListByCompany(main, getCompany());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companyBranchesList;
  }

  /**
   * Return all branches of a company.
   *
   * @param main
   * @return
   */
  public List<Company> getCompanyBranchList(MainView main) {
    if (companyBranchList == null) {
      try {
        companyBranchList = CompanyService.branchListByCompany(main, getCompany());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companyBranchList;
  }

  /**
   * Opening add new page in a dialog.
   */
  public void companyAddressNewPopup() {
    Jsf.popupForm(FileConstant.COMPANY_ADDRESS, getCompany()); // opens a new form if id is null else edit
  }

  /**
   * Closing the dialog.
   *
   */
  public void companyAddressPopupReturned() {
    CompanyAddress address = (CompanyAddress) Jsf.popupParentValue(CompanyAddress.class);
    if (address != null && address.getAddressTypeId() != null && address.getAddressTypeId().getId().equals(AddressTypeService.REGISTERED_ADDRESS)) {
      setCompanyRegAddress(address);
    }
    companyAddressList = null; // Reset to null to fetch updated lis
  }

  /**
   *
   * @param k
   */
  public void userProfileNewPopup(int k) {
    getCompany().setFlag(k);
    Jsf.popupForm(FileConstant.USER_PROFILE, getCompany());
  }

  /**
   *
   * @param k
   * @param userProfile
   */
  public void userProfileEditPopup(int k, UserProfile userProfile) {
    getCompany().setFlag(k);
    Jsf.popupForm(FileConstant.USER_PROFILE, getCompany(), userProfile.getId());

  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void companyAddressEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.COMPANY_ADDRESS, getCompany(), id);
  }

  /**
   * Closing the dialog.
   *
   */
  public void companyContactPopupReturned() {
    companyContactList = null; // Reset to null to fetch updated list
  }

  /**
   *
   */
  public void companySalesAgentPopupReturned() {
    userProfileList = null;
    //salesAgentList = null; // Reset to null to fetch updated list
  }

  /**
   *
   */
  public void companyInvestorPopupReturned() {
    companyInvestorList = null;
  }

  /**
   * Opening add new page in a dialog.
   */
  public void companyBankNewPopup() {
    Jsf.popupForm(FileConstant.COMPANY_BANK, getCompany());
  }

  /**
   * Closing the dialog.
   *
   */
  public void companyBankPopupReturned() {
    companyBankList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void companyBankEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.COMPANY_BANK, getCompany(), id);
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param companyBank
   */
  public void bankContactPopup(CompanyBank companyBank) {
    Jsf.popupList(FileConstant.COMPANY_BANK_CONTACT, companyBank);
  }

  public void bankContactPopupNewForm(CompanyBank companyBank) {
    Jsf.popupForm(FileConstant.COMPANY_BANK_CONTACT, companyBank);
  }

  /**
   * Opening add new page in a dialog.
   */
  public void companyReferenceNewPopup() {
    Jsf.popupForm(FileConstant.COMPANY_REFERENCE, getCompany());
  }

  /**
   * Closing the dialog.
   *
   */
  public void companyReferencePopupReturned() {
    companyReferenceList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void companyReferenceEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.COMPANY_REFERENCE, getCompany(), id);
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param companyReference
   */
  public void companyReferenceDocPopup(CompanyReference companyReference) {
    Jsf.popupList(FileConstant.COMPANY_REFERENCE_DOC, companyReference);
  }

  /**
   *
   * @param companyReference
   */
  public void companyReferenceDocPopupNewForm(CompanyReference companyReference) {
    Jsf.popupForm(FileConstant.COMPANY_REFERENCE_DOC, companyReference);
  }

  /**
   * Opening add new page in a dialog.
   */
  public void companyLicenseNewPopup() {
    Jsf.popupForm(FileConstant.COMPANY_LICENSE, getCompany());
  }

  /**
   * Closing the dialog.
   *
   */
  public void companyLicensePopupReturned() {
    companyLicenseList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void companyLicenseEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.COMPANY_LICENSE, getCompany(), id);
  }

  /**
   * Opening add new page in a dialog.
   */
  public void companyWarehouseNewPopup() {
    Jsf.popupForm(FileConstant.COMPANY_WAREHOUSE, getCompany());
  }

  /**
   * Closing the dialog.
   *
   */
  public void companyWarehousePopupReturned() {
    companyWarehouseList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void companyWarehouseEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.COMPANY_WAREHOUSE, getCompany(), id);
  }

  /**
   * Opening add new page in a dialog.
   */
  public void companyBranchNewPopup() {
    Jsf.popupForm(FileConstant.COMPANY_BRANCH, getCompany());
  }

  /**
   * Closing the dialog.
   *
   */
  public void companyBranchPopupReturned() {
    companyBranchesList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void companyBranchEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.COMPANY_BRANCH, getCompany(), id);
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<CompanyType> companyTypeAuto(String filter) {
    return ScmLookupView.companyTypeAuto(filter);

  }

  /**
   *
   * @param filter
   * @return
   */
  public List<Designation> designationAuto(String filter) {
    return ScmLookupView.designationAuto(filter);
  }

  /**
   *
   * @param event
   */
  public void companyInvestorPopupReturn(SelectEvent event) {
    companyInvestorList = null; // Reset to null to fetch updated list
  }

  /**
   *
   * @param main
   * @return
   */
  public List<CompanyInvestor> getCompanyInvestorList(MainView main) {
    if (companyInvestorList == null) {
      try {
        companyInvestorList = CompanyInvestorService.investorListByCompany(main, getCompany());
        totalInvestmentAmt(main);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companyInvestorList;
  }

  /**
   *
   * @param main
   */
  private void totalInvestmentAmt(MainView main) {
    if (!StringUtil.isEmpty(companyInvestorList)) {
      totalAmt = 0.0;
      for (CompanyInvestor amtList : companyInvestorList) {
        totalAmt = totalAmt + amtList.getInvestmentAmount();
      }
      setTotalAmt(totalAmt);
    }
  }

  /**
   *
   * @param main
   * @param companyContact
   * @return
   */
  public String deleteCompanyContact(MainView main, CompanyContact companyContact) {
    if (companyContact.getId() == null) {
      companyContactList.remove(companyContact);
    } else {
      try {
        CompanyContactService.deleteCompanyContact(main, companyContact);
        main.commit("success.delete");
        companyContactList.remove(companyContact);
      } catch (Throwable t) {
        main.rollback(t, "error.delete");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   *
   * @param main
   * @param salesAgentContract
   * @return
   */
  public String deleteCompanySalesAgentContract(MainView main, SalesAgentContract salesAgentContract) {
    if (salesAgentContract.getId() == null) {
      salesAgentList.remove(salesAgentContract);
    } else {
      try {
        SalesAgentContractService.deleteSalesAgentContract(main, salesAgentContract);
        main.commit("success.delete");
        salesAgentList.remove(salesAgentContract);
      } catch (Throwable t) {
        main.rollback(t, "error.delete");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   *
   * @param main
   * @param CompanyInvestor
   */
  public void onRowEditInvestor(MainView main, CompanyInvestor CompanyInvestor) {
    try {
      CompanyInvestorService.insertOrUpdate(main, CompanyInvestor);
      main.commit("success.save");
      totalInvestmentAmt(main);
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param main
   * @param companyInvestor
   * @return
   */
  public String deleteCompanyInvestor(MainView main, CompanyInvestor companyInvestor) {
    if (companyInvestor.getId() == null) {
      companyInvestorList.remove(companyInvestor);
    } else {
      try {
        CompanyInvestorService.deleteCompanyInvestor(main, companyInvestor);
        main.commit("success.delete");
        companyInvestorList.remove(companyInvestor);
        totalInvestmentAmt(main);
      } catch (Throwable t) {
        main.rollback(t, "error.delete");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   *
   * @param main
   */
  public void editCompany(MainView main) {
    try {
      main.setViewType(ViewTypes.editform);
      UserRuntimeView.instance().changeCompany(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @return
   */
  public boolean isShareVal() {
    return getCompany().getCompanyTypeId() != null && (getCompany().getCompanyTypeId().getId() == 2 || getCompany().getCompanyTypeId().getId() == 4);
  }

  /**
   *
   * @return
   */
  public boolean isProfitShare() {
    return getCompany().getCompanyTypeId() != null && (getCompany().getCompanyTypeId().getId() == 1 || getCompany().getCompanyTypeId().getId() == 3);
  }

  /**
   *
   * @return
   */
  public Double getTotalAmt() {
    return totalAmt;
  }

  /**
   *
   * @param totalAmt
   */
  public void setTotalAmt(Double totalAmt) {
    this.totalAmt = totalAmt;
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

  /**
   * Country auto complete item select event handler.
   *
   * Method resets the value of Company State object while selecting a new Country
   *
   * @param event
   */
  public void countrySelectEvent(SelectEvent event) {
    Country country = (Country) event.getObject();
    if (country != null) {
      if (country.equals(UserRuntimeService.USA)) {
        pinLength = 5;
        pinValue = 99999;
      } else {
        pinLength = 6;
        pinValue = 999999;
      }
      if (getCompany().getCountryId() == null) {
        getCompany().setCountryId(country);
      } else if (!getCompany().getCountryId().getId().equals(country.getId())) {
        getCompany().setStateId(null);
        getCompany().setCountryTaxRegimeId(null);
        getCompanyRegAddress().setDistrictId(null);
      }
      if (country.getCurrencyId() != null) {
        getCompany().setCurrencyId(country.getCurrencyId());
      }
    } else {
      getCompany().setStateId(null);
      getCompanyRegAddress().setDistrictId(null);
      getCompany().setCountryTaxRegimeId(null);
    }
  }

  /**
   * Company state select event handler.
   *
   * Method sets the selected state in company state object.
   *
   * @param event
   */
  public void stateSelectEvent(SelectEvent event) {
    State state = (State) event.getObject();
    getCompany().setStateId(state);
  }

  /**
   * Method to get the list of country taxes based in country.
   *
   * @return countryTaxList
   */
  public List<CountryTaxRegime> selectCountryTaxByCountry() {
    List<CountryTaxRegime> list = null;
    if (getCompany() != null && getCompany().getCountryId() != null) {
      list = ScmLookupExtView.selectCountryTaxRegimeByCountry(getCompany().getCountryId());
      if (list != null && !list.isEmpty() && getCompany().getCountryTaxRegimeId() == null) {
        getCompany().setCountryTaxRegimeId(list.get(0));
      }
    }
    return list;
  }

  /**
   *
   * @param regimeId
   * @return
   */
  public String displayRegime(Integer regimeId) {
    String regime = "";
    if (regimeId != null) {
      regime = CountryTaxRegimeService.REGIME[regimeId - 1];
    }
    return regime;
  }

  /**
   *
   * @param event
   */
  public void countryTaxRegimeSelectEvent(SelectEvent event) {
    //CountryTaxRegime ctr = (CountryTaxRegime) event.getObject();
    //getCompany().setCountryTaxRegimeId(ctr);
  }

  /**
   * GSTIN ajax change event handler.
   */
  public void gstinChangeEventHandler() {
    MainView main = Jsf.getMain();
    try {
      if (!StringUtil.isEmpty(getCompany().getGstNo())) {
        if (AppUtil.isValidGstin(getCompany().getGstNo())) {
          getCompany().setPanNo(getCompany().getGstNo().substring(2, 12));
          try {
            State state = StateService.selectStateByStateCodeAndCountry(main, getCompany().getCountryId(), getCompany().getGstNo().substring(0, 2));
            if (state != null) {
              getCompany().setStateId(state);
              if (getCompanyRegAddress().getDistrictId() != null && !getCompanyRegAddress().getDistrictId().getStateId().getId().equals(state.getId())) {
                getCompanyRegAddress().setDistrictId(null);
              }
            } else {
              getCompany().setStateId(null);
              getCompanyRegAddress().setDistrictId(null);
            }
          } finally {
            main.close();
          }
        }
      } else {
        getCompany().setStateId(null);
        getCompany().setPanNo(null);
        getCompanyRegAddress().setDistrictId(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public boolean isWareHouse() {
    return wareHouse;
  }

  public void setWareHouse(boolean wareHouse) {
    this.wareHouse = wareHouse;
  }
//FIXME move to popupview

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

  public void countryOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.BASIC_COUNTRY, new District()); // opens a new form if id is null else edit
    }
  }

  public boolean isDocPrefixBoolean() {
    if (StringUtil.equalsInt(getCompany().getDocPrefixBasedOnCompany(), 1)) {
      docPrefixBoolean = true;
    }
    return docPrefixBoolean;
  }

  public void setDocPrefixBoolean(boolean docPrefixBoolean) {
    if (docPrefixBoolean) {
      getCompany().setDocPrefixBasedOnCompany(1);
    }
    this.docPrefixBoolean = docPrefixBoolean;
  }

}
