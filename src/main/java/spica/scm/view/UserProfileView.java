/*
 * @(#)UserProfileView.java	1.0 Thu Oct 20 11:33:28 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import spica.scm.common.SelectItemInteger;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.Department;
import spica.scm.domain.Designation;
import spica.scm.domain.SalesAgentContract;
import spica.scm.domain.SalesAgentContractComm;
import spica.scm.domain.Status;
import spica.scm.domain.Territory;
import spica.scm.domain.Transporter;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Vendor;
import spica.scm.service.AccountGroupService;
import spica.scm.service.CompanyContactService;
import spica.scm.service.CompanyInvestorService;
import spica.scm.service.CustomerContactService;
import spica.scm.service.DepartmentService;
import spica.scm.service.DesignationService;
import spica.scm.service.RoleService;
import spica.scm.service.SalesAgentContractCommService;
import spica.scm.service.TerritoryService;
import spica.scm.service.TransporterContactService;
import spica.scm.service.UserProfileService;
import spica.scm.service.UserService;
import spica.scm.service.VendorContactService;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import spica.sys.domain.Role;
import spica.sys.domain.User;
import wawo.app.config.AppFactory;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppEm;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * UserProfileView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Oct 20 11:33:28 IST 2016
 */
@Named(value = "userProfileView")
@ViewScoped
public class UserProfileView implements Serializable {

  private transient UserProfile userProfile;	//Domain object/selected Domain.
  private transient LazyDataModel<UserProfile> userProfileLazyModel; 	//For lazy loading datatable.
  private transient UserProfile[] userProfileSelected;	 //Selected Domain Array
  private Company company;
  private Vendor vendor = null;
  private Customer customer = null;
  private Transporter transporter = null;
  int loginVal = 0;
  private List<Role> roleSelected;
  private List<Company> companySelected1;
  private List<Account> accountList;
  private SalesAgentContract salesAgent;
  // private SalesAgentContractComm salesAgentContractComm;
  //private transient Services defaultServices;
  //private transient List<Services> servicesList;
  private transient int currentRow;
  private transient List<UserProfile> userProfileList;
  private transient List<AccountGroup> accountGroupList;
  private transient AccountGroup defaultAccountGroup;
  private transient List<Territory> territoryList;
  private transient Territory defaultTerritory;
  private transient AccountGroup accountGroup;
  private transient Territory territory;
  private Integer notify = 0;

  public List<Company> getCompanySelected1() {
    return companySelected1;
  }

  public void setCompanySelected1(List<Company> companySelected1) {
    this.companySelected1 = companySelected1;
  }
  String confPassword = "";
  String password = "";

  /**
   * Default Constructor.
   */
  public UserProfileView() {
    super();
  }

  @PostConstruct
  public void init() {
    company = (Company) Jsf.popupParentValue(Company.class);
    vendor = (Vendor) Jsf.popupParentValue(Vendor.class);
    transporter = (Transporter) Jsf.popupParentValue(Transporter.class);
    customer = (Customer) Jsf.popupParentValue(Customer.class);
    Integer id = Jsf.getParameterInt("id");
    getUserProfile().setId(id);
  }

  /**
   * Return UserProfile.
   *
   * @return UserProfile.
   */
  public UserProfile getUserProfile() {
    if (userProfile == null) {
      userProfile = new UserProfile();
    }
    return userProfile;
  }

  /**
   * Set UserProfile.
   *
   * @param userProfile.
   */
  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

  public int getLoginVal() {
    return loginVal;
  }

  public void setLoginVal(int loginVal) {
    this.loginVal = loginVal;
  }

  public String getConfPassword() {
    return confPassword;
  }

  public void setConfPassword(String confPassword) {
    this.confPassword = confPassword;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Company getCompany() {
    if (company == null) {
      company = new Company();
    }
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

//  public Services getDefaultServices() {
//    return defaultServices;
//  }
//
//  public void setDefaultServices(Services defaultServices) {
//    this.defaultServices = defaultServices;
//  }
//
//  public List<Services> getServicesList() {
//    return servicesList;
//  }
//
//  public void setServicesList(List<Services> servicesList) {
//    this.servicesList = servicesList;
//  }
  public int getCurrentRow() {
    return currentRow;
  }

  public void setCurrentRow(int currentRow) {
    this.currentRow = currentRow;
  }

  public List<AccountGroup> getAccountGroupList() {
    return accountGroupList;
  }

  public void setAccountGroupList(List<AccountGroup> accountGroupList) {
    this.accountGroupList = accountGroupList;
  }

  public AccountGroup getDefaultAccountGroup() {
    return defaultAccountGroup;
  }

  public void setDefaultAccountGroup(AccountGroup defaultAccountGroup) {
    this.defaultAccountGroup = defaultAccountGroup;
  }

  public List<Territory> getTerritoryList() {
    return territoryList;
  }

  public void setTerritoryList(List<Territory> territoryList) {
    this.territoryList = territoryList;
  }

  public Territory getDefaultTerritory() {
    return defaultTerritory;
  }

  public void setDefaultTerritory(Territory defaultTerritory) {
    this.defaultTerritory = defaultTerritory;
  }

  /**
   * Return LazyDataModel of UserProfile.
   *
   * @return
   */
  public LazyDataModel<UserProfile> getUserProfileLazyModel() {
    return userProfileLazyModel;
  }

  /**
   * Return UserProfile[].
   *
   * @return
   */
  public UserProfile[] getUserProfileSelected() {
    return userProfileSelected;
  }

  /**
   * Set UserProfile[].
   *
   * @param userProfileSelected
   */
  public void setUserProfileSelected(UserProfile[] userProfileSelected) {
    this.userProfileSelected = userProfileSelected;
  }

  public String getPopupTitle() {
    if (customer != null) {
      return customer.getCustomerName();
    } else if (transporter != null) {
      return transporter.getTransporterName();
    } else if (vendor != null) {
      return vendor.getVendorName();
    } else if (company != null) {
      return StringUtil.isEmpty(company.getCompanyName()) ? UserRuntimeView.instance().getCompany().getCompanyName() : company.getCompanyName();
    }
    return "";
  }

  public String switchSalesAgent(MainView main, String viewType) {
    getCompany().setFlag(2);
    setSalesAgent(null);
    return switchUserProfile(main, viewType);
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchUserProfile(MainView main, String viewType) {
    //this.main = main;
    User usr1 = new User();
    if (!StringUtil.isEmpty(viewType)) {
      try {
        if (UserRuntimeView.instance().getCompany() == null) {
          main.error("company.required");
          main.setViewType(ViewTypes.list);
          return null;
        }
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getUserProfile().reset();
          setLoginVal(0);
          setRoleSelected(null);

          if (isSalesAgentView()) {
            addNewSalesAgentCommision();
            getUserProfile().setSalesAgentType(UserProfileService.INDIVIDUAL);
            Department dept = new Department();
            dept.setId(UserProfileService.DEPARTMENT_ID);

            Designation desig = new Designation();
            desig.setId(UserProfileService.DESIGNATION_SALES_AGENT);

            getUserProfile().setDepartmentId(dept);
            getUserProfile().setDesignationId(desig);
            getUserProfile().setCurrencyId(UserRuntimeView.instance().getCompany().getCurrencyId());
          }

        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {

          setUserProfile((UserProfile) UserProfileService.selectByPk(main, getUserProfile()));
          usr1 = UserService.selectUser(main, getUserProfile().getUserCode());

          if (isSalesAgentView()) {
//            setSalesAgent((SalesAgentContract) SalesAgentContractService.listPagedByUserProfile(main, getUserProfile()));
//            setAccountGroupList(UserProfileService.selectAccountGroupBySalesAgent(main, getUserProfile()));
//            setTerritoryList(UserProfileService.selectTerritoryBySalesAgent(main, getUserProfile()));
//
//            if (getSalesAgent().getSalesAgentContractComm() != null) {
//              Collections.sort(getSalesAgent().getSalesAgentContractComm());
//            }
//
//            if (!StringUtil.isEmpty(getAccountGroupList())) {
//              setDefaultAccountGroup(getAccountGroupList().get(0));
//            }
//
//            if (!StringUtil.isEmpty(getTerritoryList())) {
//              setDefaultTerritory(getTerritoryList().get(0));
//            }
          }

          if (usr1 != null) {
            setLoginVal(1);
            roleSelected = UserService.listRelatedRole(main, usr1);
          } else {
            roleSelected = null;
            setLoginVal(0);
          }
        } else if (ViewTypes.isList(viewType)) {
          setAccountGroupList(null);
          setDefaultAccountGroup(null);
          setTerritoryList(null);
          setDefaultTerritory(null);
          setSalesAgent(null);
          loadUserProfileList(main);
          // setCompany(null);
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
   * Create userProfileLazyModel.
   *
   * @param main
   */
  private void loadUserProfileList(final MainView main) {
    if (userProfileLazyModel == null) {
      userProfileLazyModel = new LazyDataModel<UserProfile>() {
        private List<UserProfile> list;

        @Override
        public List<UserProfile> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            //list = UserProfileService.listPaged(main);
            if (company != null && company.getId() != null) {
              if (company.getFlag() == 0) {
                if (UserProfileService.EMPLOYEE_URL.equals(main.getViewPath())) {
                  list = UserProfileService.companyEmployee(main, UserRuntimeView.instance().getCompany());
                } else {
                  list = UserProfileService.listPagedNotInCompanyContact(main, UserRuntimeView.instance().getCompany());
                }

              } else if (company.getFlag() == 1) {
                list = UserProfileService.listPagedNotInCompanyInvestor(main, UserRuntimeView.instance().getCompany());

              } else if (company.getFlag() == 2) {
                list = UserProfileService.listPagedNotInCompanySalesAgent(main, UserRuntimeView.instance().getCompany(), getAccountGroup(), getTerritory());
              }
            } else if (vendor != null && vendor.getId() != null) {
              list = UserProfileService.listPagedNotInVendorContact(main, vendor);
            } else if (transporter != null && transporter.getId() != null) {
              list = UserProfileService.listPagedNotInTransporterContact(main, transporter);
            } else if (customer != null && customer.getId() != null) {
              list = UserProfileService.listPagedNotInCustomerContact(main, customer);
            } else if (company == null && vendor == null && customer == null && transporter == null) {
              if (UserRuntimeView.instance().getCompany() != null) {
                list = UserProfileService.companyEmployee(main, UserRuntimeView.instance().getCompany());
              }
            } else {
              // list = UserProfileService.listPaged(main, UserRuntimeView.instance().getCompany());
              list = UserProfileService.companyEmployee(main, UserRuntimeView.instance().getCompany());
            }
            main.commit(userProfileLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(UserProfile userProfile) {
          return userProfile.getId();
        }

        @Override
        public UserProfile getRowData(String rowKey) {
          if (list != null) {
            for (UserProfile obj : list) {
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
    String SUB_FOLDER = "scm_user_profile/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveUserProfile(MainView main) {
    return saveOrCloneUserProfile(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneUserProfile(MainView main) {
    main.setViewType("newform");
    return saveOrCloneUserProfile(main, "clone");
  }

  private boolean isSalesAgentView() {
    return (getCompany() != null && getCompany().getFlag() == 2);
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneUserProfile(MainView main, String key) {
    try {
      uploadFiles(); //File upload

      SystemRuntimeConfig.initRole(main);
      if (vendor != null) {
        getRoleSelected().add(SystemRuntimeConfig.VENDOR_ROLE);
      } else if (customer != null) {
        getRoleSelected().add(SystemRuntimeConfig.CUSTOMER_ROLE);
      } else if (transporter != null) {
        getRoleSelected().add(SystemRuntimeConfig.TRANSPORTER_ROLE);
      } else if (isSalesAgentView()) {
        getRoleSelected().add(SystemRuntimeConfig.COMPANY_SALES_AGENT);
      }
      boolean setUserCode = vendor != null || customer != null || transporter != null;
      if (null != key) {
        if (key.equals("save") || key.equals("clone")) {

//          if (StringUtil.isEmpty(getUserProfile().getPanNo()) && getUserProfile().getDesignationId().getId().equals(UserProfileService.DESIGNATION_SALES_AGENT)) {
//            main.error("insert.pan");
//            return null;
//          }
          if (vendor != null) {
            getUserProfile().setVendorId(vendor);
          } else if (customer != null) {
            getUserProfile().setCustomerId(customer);
          } else if (transporter != null) {
            getUserProfile().setTransporterId(transporter);
          }

          if (getSalesAgent() != null && isSalesAgentView()) {
            if (getSalesAgent().getCommissionByRange() == 2) {
              getSalesAgent().setSalesAgentContractComm(null);
            } else {
              getSalesAgent().setCommissionValuePercentage(null);
              getSalesAgent().setCommissionValueFixed(null);
            }
            if (getSalesAgent().getCommissionType() == 1) { //fixed
              getSalesAgent().setCommissionValuePercentage(null);
            } else {
              getSalesAgent().setCommissionValueFixed(null);
            }
//            getUserProfile().addUserProfileIdSalesAgentContract(getSalesAgent());
          }

          getUserProfile().setCompanyId(UserRuntimeView.instance().getCompany());

          if (key.equals("save")) {
            if (userProfile.getId() == null) {
              UserService.userValidate(main, userProfile.getEmail(), userProfile.getPhone1(), userProfile.getUserCode());
            }
            UserProfileService.insertOrUpdate(main, getUserProfile());
          } else if (key.equals("clone")) {
            UserProfileService.clone(main, getUserProfile(), getAccountGroupList(), getTerritoryList());
          }

          User user = UserService.selectUserByUserProfile(main, getUserProfile());
          if (user == null) {
            user = new User();
          }
          user.setName(userProfile.getName());
          user.setEmail(userProfile.getEmail());
          user.setPhone(userProfile.getPhone1());
          if (userProfile.getUserCode() == null) {
            user.setLogin(userProfile.getEmail());
          } else {
            user.setLogin(userProfile.getUserCode());
          }

          if (loginVal != 0) {
            if (user.getId() == null) {
              Status st = new Status();
              st.setId(1);
              user.setStatusId(st);
              user.setUserProfileId(getUserProfile());
            }
            UserService.insertOrUpdate(main, user, roleSelected, companySelected1, accountList, 0, notify == 1);
          } else if (loginVal == 0) {
            UserService.deleteUserLogin(main, getUserProfile().getUserCode());
          }

          // main.commit("success." + key);
          if (setUserCode && main.isNew()) {
            getUserProfile().setUserCode(getUserProfile().getId().toString());
            UserProfileService.updateByPk(main, userProfile);
            if (loginVal != 0) {
              user.setLogin(getUserProfile().getUserCode());
              UserService.updateByPk(main, user);
            }
          }

          main.commit("success." + key);

        }
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  public void updateUserProfile(MainView main) {
    try {
      UserProfileService.updateByPk(main, userProfile);
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }

  }

  /**
   * Delete one or many UserProfile.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteUserProfile(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(userProfileSelected)) {
        UserProfileService.deleteByPkArray(main, getUserProfileSelected()); //many record delete from list
        main.commit("success.delete");
        userProfileSelected = null;
      } else {
        UserProfileService.deleteByPk(main, getUserProfile());  //individual record delete from list or edit form
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
   *
   * @param filter
   * @return
   */
  public List<Transporter> transporterAuto(String filter) {
    return ScmLookupView.transporterAuto(filter);
  }

  /**
   * Department autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.departmentAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.departmentAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Department> departmentAuto(String filter) {
    return ScmLookupView.departmentAuto(filter);
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
   * Status autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.statusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.statusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  /**
   *
   */
  public void userProfileDialogClose() {
    if (vendor != null) {
      Jsf.popupClose(vendor);
    } else if (customer != null) {
      Jsf.popupClose(customer);
    } else if (transporter != null) {
      Jsf.popupClose(transporter);
    } else if (company != null && company.getFlag() != 2) {
      Jsf.popupClose(company);
    }
  }

  /**
   *
   * @param main
   */
  public void insertUserProfileContact(MainView main) {
    try {
      if (vendor != null) {
        VendorContactService.insertArray(main, getUserProfileSelected(), vendor);
        Jsf.popupReturn(vendor, null);
      } else if (customer != null) {
        CustomerContactService.insertArray(main, getUserProfileSelected(), customer);
        Jsf.popupReturn(customer, null);
      } else if (transporter != null) {
        TransporterContactService.insertArray(main, getUserProfileSelected(), transporter);
        Jsf.popupReturn(transporter, null);
      } else if (company != null) {
        if (company.getFlag() == 0) {
          CompanyContactService.insertArray(main, getUserProfileSelected(), UserRuntimeView.instance().getCompany());
        } else if (company.getFlag() == 1) {
          CompanyInvestorService.insertArray(main, getUserProfileSelected(), UserRuntimeView.instance().getCompany());
        } else if (company.getFlag() == 2) {
//          SalesAgentContractService.insertArray(main, getUserProfileSelected(), UserRuntimeView.instance().getCompany());
        }
        Jsf.popupReturn(company, null);
      }
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.list");
    } finally {
      main.close();
    }
  }

  public boolean isVendorUserProfile() {
    return (UserRuntimeView.instance().getCompany() != null && vendor != null);
  }

  public boolean isCustomerUserProfile() {
    return (UserRuntimeView.instance().getCompany() != null && customer != null);
  }

  public boolean isTransporterUserProfile() {
    return (UserRuntimeView.instance().getCompany() != null && transporter != null);
  }

  public boolean isCompanyUserProfile() {
    return (company != null && vendor == null && customer == null && transporter == null);
  }

  public List<Department> selectCompanyDepartment(MainView main) {
    try {
      return DepartmentService.selectCompanyDepartment(main, company.getFlag());
    } finally {
      main.close();
    }
  }

  public List<Department> selectVendorDepartment(MainView main) {
    try {
      return DepartmentService.selectVendorDepartment(main);
    } finally {
      main.close();
    }
  }

  public List<Department> selectCustomerDepartment(MainView main) {
    try {
      return DepartmentService.selectCustomerDepartment(main);
    } finally {
      main.close();
    }
  }

  public List<Department> selectTransporterDepartment(MainView main) {
    try {
      return DepartmentService.selectTransporterDepartment(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param main
   * @return
   */
  public List<Designation> selectDesignationByDepartment(MainView main) {

    try {
      if (getUserProfile().getDepartmentId() != null) {
        if (company != null && company.getFlag() == 0 || customer != null || transporter != null || vendor != null) {
          return DesignationService.selectDesignationByDepartment(main, getUserProfile().getDepartmentId().getId());
        }
        if (company != null && company.getFlag() == 2 || customer != null || transporter != null || vendor != null) {
          return DesignationService.selectDesignationSalesAgent(main, getUserProfile().getDepartmentId().getId());
        } else {
          return DesignationService.selectDesignationByLevel(main, getUserProfile().getDepartmentId().getId());
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void departmentSelectEvent(SelectEvent event) {
    Department depart = (Department) event.getObject();
    getUserProfile().setDepartmentId(depart);
  }

  public List<Role> getRoleSelected() {
    if (roleSelected == null) {
      roleSelected = new ArrayList<>();
    }
    return roleSelected;
  }

  public void setRoleSelected(List<Role> roleSelected) {
    this.roleSelected = roleSelected;
  }

  public void ajaxBehaviorEventHandler(AjaxBehaviorEvent event) {
  }

  public void createLoginSelectEventHandler(AjaxBehaviorEvent event) {
    if (getCompany().getFlag() == 2) {
      if (getLoginVal() == 1) {
        Role userRole = new Role();
        userRole.setId(RoleService.USER_ROLE);
        getRoleSelected().add(userRole);
      } else {
        setRoleSelected(null);
      }
    }
  }

  public void commissionValueSelectEventHandler(AjaxBehaviorEvent event) {
    getSalesAgent().setCommissionValueFixed(null);
    getSalesAgent().setCommissionValuePercentage(null);
  }

  public static void resetPassword() {
  }

  public boolean isCompanyLogin() {
    return company != null && (vendor == null && customer == null && transporter == null) && company.getFlag() != 2;
  }

  public boolean isLoginCreated() {
    return getLoginVal() != 0;
  }

  public SalesAgentContract getSalesAgent() {
    if (salesAgent == null) {
      salesAgent = new SalesAgentContract();
    }
    return salesAgent;
  }

  public void setSalesAgent(SalesAgentContract salesAgent) {
    this.salesAgent = salesAgent;
  }

  public List<SelectItemInteger> getSelectItemYesNo() {
    List<SelectItemInteger> listYesNo = new ArrayList<>();
    SelectItemInteger si1 = new SelectItemInteger();
    si1.setItemLabel("Yes");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItemInteger si2 = new SelectItemInteger();
    si2.setItemLabel("No");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
  }

  public List<SelectItem> getListCommissionOn() {
    List<SelectItem> listYesNo = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Sales");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("Collection");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
  }

  public List<SelectItem> getListCommissionValueType() {
    List<SelectItem> listYesNo = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Fixed");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("Percentage");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
  }

  public void ajaxBehaviorEventHandler() {
  }

  public boolean isFixedCommissionByRange() {
    if (getSalesAgent() != null && getSalesAgent().getCommissionType() != null && getSalesAgent().getCommissionByRange() != null) {
      return (getSalesAgent().getCommissionType() == 1 && getSalesAgent().getCommissionByRange() == 2);
    }
    return false;
  }

  public boolean isPercentageCommissionByRange() {
    if (getSalesAgent() != null && getSalesAgent().getCommissionType() != null && getSalesAgent().getCommissionByRange() != null) {
      return (getSalesAgent().getCommissionType() == 2 && getSalesAgent().getCommissionByRange() == 2);
    }
    return false;
  }

  public boolean isCommisionByRange() {
    if (getSalesAgent() != null) {
      return (getSalesAgent().getCommissionByRange() != null && getSalesAgent().getCommissionByRange() == 1);
    }
    return false;
  }

  public boolean isFixedCommissionValue() {
    return (getSalesAgent().getCommissionType() == 1);
  }

  public boolean isPercentageCommissionValue() {
    return (getSalesAgent().getCommissionType() == 2);
  }

  /**
   *
   * @param main
   * @param salesAgentContractComm
   */
  public void saveCommission(MainView main, SalesAgentContractComm salesAgentContractComm) {
    try {
      boolean isNew = salesAgentContractComm.getId() == null;
      if (isNew) {
        getSalesAgent().getSalesAgentContractComm().add(salesAgentContractComm);
      } else {
        SalesAgentContractCommService.insertOrUpdate(main, salesAgentContractComm);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @return
   */
  public String addNewSalesAgentCommision() {
    if (StringUtil.isEmpty(getSalesAgent().getSalesAgentContractComm())) {
      actionAddNewCommissionRange(null);
    } else {
      actionAddNewCommissionRange(getSalesAgent().getSalesAgentContractComm().get(getSalesAgent().getSalesAgentContractComm().size() - 1));
    }
    return null;
  }

  /**
   *
   * @param main
   * @param selectedCommissionRange
   */
  public void deleteContractCommissionRange(MainView main, SalesAgentContractComm selectedCommissionRange) {
    try {
      if (getSalesAgent().getSalesAgentContractComm() != null) {
        if (getSalesAgent() != null && getSalesAgent().getId() != null) {
          if (selectedCommissionRange.getId() != null) {
            SalesAgentContractCommService.deleteByPk(main, selectedCommissionRange);
            main.commit();
          }
          getSalesAgent().getSalesAgentContractComm().remove(selectedCommissionRange);
        } else {
          getSalesAgent().getSalesAgentContractComm().remove(selectedCommissionRange);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param main
   * @param filter
   * @return
   */
//  public List<Services> salesAgentServicesAuto(MainView main, String filter) {
//    if (getServicesList() != null && !getServicesList().isEmpty()) {
//      if (defaultServices == null) {
//        defaultServices = ServicesService.selectByPk(main, getServicesList().get(0));
//      }
//      return ScmLookupExtView.salesAgentServicesByServicesAuto(filter);
//    } else {
//      return ScmLookupExtView.salesAgentServicesByServicesAuto1(filter);
//    }
//  }
  /**
   * Primefaces ajax itemSelect event handler. This method removes duplicate Account objects from the accoutList object.
   *
   * @param event
   */
//  public void onServicesSelect(SelectEvent event) {
//    Set<Services> tempSet = new HashSet<>();
//    List<Services> tempList;
//    if (getServicesList().size() > 1) {
//      for (Services serv : getServicesList()) {
//        tempSet.add(serv);
//      }
//      tempList = new ArrayList<>(tempSet);
//      setServicesList(tempList);
//    }
//  }
  /**
   *
   * @param event
   */
  public void onServicesUnSelect(UnselectEvent event) {
//    if (getServicesList().size() == 1) {
//      getServicesList().clear();
//      setDefaultServices(null);
//    }
  }

  /**
   *
   * @param context
   * @param component
   * @param value
   * @throws ValidatorException
   */
//  public void salesAgentServicesValidation(FacesContext context, UIComponent component, Object value) throws ValidatorException {
//    List<Services> list = (List<Services>) value;
//    String validateParam = (String) Jsf.getParameter("validateServices");
//    if (!StringUtil.isEmpty(validateParam)) {
//      if (list != null && list.size() <= 1) {
//        Jsf.error(component, "error.sales.agent.services.exist");
//      } else if (list != null && list.size() > 1) {
//        AppEm em = null;
//        try {
//          em = AppFactory.getNewEm();
//          long exist = UserProfileService.isSalesAgentServicesExist(em, getUserProfile().getId(), list);
//          if (exist > 0) {
//            Jsf.error(component, "error.sales.agent.services.exist");
//          }
//        } finally {
//          if (em != null) {
//            em.close();
//          }
//        }
//      }
//    }
//  }
  /**
   *
   * @param main
   */
  public void actionInsertOrUpdateSalesAgentCommissionRange(MainView main) {
    try {
      Integer rowIndex = Jsf.getParameterInt("rownumber");
      setCurrentRow(rowIndex);
      if (getSalesAgent() != null && getSalesAgent().getSalesAgentContractComm() != null && !getSalesAgent().getSalesAgentContractComm().isEmpty()) {
        SalesAgentContractComm salesAgentContractComm = getSalesAgent().getSalesAgentContractComm().get(rowIndex);
        if (getSalesAgent().getId() != null) {
          salesAgentContractComm.setSalesAgentContractId(getSalesAgent());
          SalesAgentContractCommService.insertOrUpdate(main, salesAgentContractComm);
        } else {
          getSalesAgent().getSalesAgentContractComm().get(rowIndex).setSalesAgentContractId(getSalesAgent());
        }

        if (rowIndex == getSalesAgent().getSalesAgentContractComm().size() - 1) {
          actionAddNewCommissionRange(salesAgentContractComm);
        }
      }

    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param commitionRange
   */
  public void actionAddNewCommissionRange(SalesAgentContractComm commitionRange) {
    SalesAgentContractComm salesAgentContractComm = new SalesAgentContractComm();
    if (commitionRange != null) {
      if (commitionRange.getRangeTo() != null) {
        salesAgentContractComm.setRangeFrom(commitionRange.getRangeTo() + 1);
      }
    }
    if (!getSalesAgent().getSalesAgentContractComm().isEmpty()) {
      Collections.sort(getSalesAgent().getSalesAgentContractComm());
    }
    salesAgentContractComm.setSalesAgentContractId(getSalesAgent());
    getSalesAgent().getSalesAgentContractComm().add(salesAgentContractComm);
  }

  /**
   *
   * @param salesAgentContractComm
   * @param rangeTo
   * @return
   */
  private boolean isValidToRange(SalesAgentContractComm salesAgentContractComm, Integer rangeTo) {
    return ((salesAgentContractComm.getRangeFrom() != null) && (rangeTo > salesAgentContractComm.getRangeFrom()));
  }

  /**
   *
   * @param salesAgentContractCommList
   * @param rowIndex
   * @param rangeTo
   * @return
   */
  private int isRangeDuplicated(List<SalesAgentContractComm> salesAgentContractCommList, Integer rowIndex, Integer rangeTo) {
    String range1;
    String range2;
    int rcount;
    int index1 = 0;
    int index2;

    for (SalesAgentContractComm salesAgentContract : salesAgentContractCommList) {
      if (rowIndex == index1) {
        range1 = salesAgentContract.getRangeFrom() + "" + rangeTo;
      } else {
        range1 = salesAgentContract.getRangeFrom() + "" + salesAgentContract.getRangeTo();
      }
      rcount = 0;
      index2 = 0;
      index1++;
      for (SalesAgentContractComm sacc : salesAgentContractCommList) {
        if (index2 == rowIndex) {
          range2 = sacc.getRangeFrom() + "" + rangeTo;
        } else {
          range2 = sacc.getRangeFrom() + "" + salesAgentContract.getRangeTo();
        }
        if (range1.equals(range2)) {
          rcount++;
        }
        if (rcount > 1) {
          return index2;
        }
        index2++;
      }
    }
    return -1;
  }

  /**
   *
   * @param rowIndex
   * @return
   */
  public boolean rangeToRequired(int rowIndex) {
    List<SalesAgentContractComm> list = getSalesAgent().getSalesAgentContractComm();
    if (list != null) {
      if (list.size() == 1) {
        return false;
      } else if ((list.size() - 1) != rowIndex) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   * @param context
   * @param component
   * @param value
   * @throws ValidatorException
   */
  public void validateRangeFrom(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    Integer intValue = (Integer) value;
    int rowIndex = Integer.parseInt(component.getClientId().split(":")[1]);
    SalesAgentContractComm prevSalesAgentContractComm;
    if (intValue != null && rowIndex != 0) {
      prevSalesAgentContractComm = getSalesAgent().getSalesAgentContractComm().get(rowIndex - 1);
      if (prevSalesAgentContractComm.getRangeTo() != null && intValue <= prevSalesAgentContractComm.getRangeTo()) {
        Jsf.error(component, "error.invalid.rangefrom");
      }
    }
  }

  /**
   *
   * @param context
   * @param component
   * @param value
   * @throws ValidatorException
   */
  public void validateRangeTo(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    Integer intValue = (Integer) value;
    int rowIndex = Integer.parseInt(component.getClientId().split(":")[1]);
    SalesAgentContractComm salesAgentContractCom = getSalesAgent().getSalesAgentContractComm().get(rowIndex);

    if (intValue != null) {
      if (!isValidToRange(salesAgentContractCom, intValue)) {
        Jsf.error(component, "error.invalid.rangeto");
      }
    }

    int repeatIndex = isRangeDuplicated(getSalesAgent().getSalesAgentContractComm(), rowIndex, intValue);
    if (repeatIndex == rowIndex) {
      Jsf.error(component, "error.range.duplicated");
    }
  }

  public List<UserProfile> getUserProfileList(MainView main) {
    try {
      if (UserRuntimeView.instance().getCompany() != null) {
        userProfileList = UserProfileService.listPagedNotInCompanySalesAgent(main, UserRuntimeView.instance().getCompany(), getAccountGroup(), getTerritory());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return userProfileList;
  }

  public List<AccountGroup> accountGroupBySalesAgentAuto(String filter) {

    MainView main = Jsf.getMain();
    try {
      if (getAccountGroupList() != null && !getAccountGroupList().isEmpty()) {
        if (defaultAccountGroup == null) {
          defaultAccountGroup = AccountGroupService.selectByPk(main, getAccountGroupList().get(0));
        }
      }
      return ScmLookupExtView.accountGroupBySalesAgentAuto(filter, UserRuntimeView.instance().getCompany().getId());
//      } else {
//        return ScmLookupExtView.accountGroupBySalesAgentAuto1(filter);
//      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;

  }

  public void onAccountGroupSelect(SelectEvent event) {
    Set<AccountGroup> tempSet = new HashSet<>();
    List<AccountGroup> tempList;
    if (getAccountGroupList().size() > 1) {
      for (AccountGroup acntgrp : getAccountGroupList()) {
        tempSet.add(acntgrp);
      }
      tempList = new ArrayList<>(tempSet);
      setAccountGroupList(tempList);
    }
  }

  /**
   *
   * @param event
   */
  public void onAccountGroupUnSelect(UnselectEvent event) {
    if (getAccountGroupList().size() == 1) {
      getAccountGroupList().clear();
      setDefaultAccountGroup(null);
    }
  }

  public void salesAgentValidation(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    List<AccountGroup> list = (List<AccountGroup>) value;
    String validateParam = (String) Jsf.getParameter("validateSalesAgents");
    if (!StringUtil.isEmpty(validateParam)) {
      if (list != null && list.size() <= 1) {
        Jsf.error(component, "error.sales.agent.exist");
      } else if (list != null && list.size() > 1) {
        AppEm em = null;
        try {
          em = AppFactory.getNewEm();
          long exist = UserProfileService.isSalesAgentExist(em, getUserProfile().getId(), list);
          if (exist > 0) {
            Jsf.error(component, "error.sales.agent.exist");
          }
        } finally {
          if (em != null) {
            em.close();
          }
        }
      }
    }
  }

  public List<Territory> territoryBySalesAgentAuto(String filter) {

    List<Territory> territoryList = null;
    MainView main = Jsf.getMain();
    try {
      if (getTerritoryList() != null && !getTerritoryList().isEmpty()) {
        if (defaultTerritory == null) {
          defaultTerritory = TerritoryService.selectByPk(main, getTerritoryList().get(0));
        }
      }
      territoryList = ScmLookupExtView.territoryBySalesAgentAuto(filter, UserRuntimeView.instance().getCompany().getId());
      //   } else {
//        return ScmLookupExtView.territoryBySalesAgentAuto1(filter);
      // }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return territoryList;
  }

  public void onTerritorySelect(SelectEvent event) {
    Set<Territory> tempSet = new HashSet<>();
    List<Territory> tempList;
    if (getTerritoryList().size() > 1) {
      for (Territory ter : getTerritoryList()) {
        tempSet.add(ter);
      }
      tempList = new ArrayList<>(tempSet);
      setTerritoryList(tempList);
    }
  }

  /**
   *
   * @param event
   */
  public void onTerritoryUnSelect(UnselectEvent event) {
    if (getTerritoryList().size() == 1) {
      getTerritoryList().clear();
      setDefaultTerritory(null);
    }
  }

  public void territoryValidation(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    MainView main = Jsf.getMain();
    try {
      List<Territory> list = (List<Territory>) value;
      String validateParam = (String) Jsf.getParameter("validateSTerritory");
      if (!StringUtil.isEmpty(validateParam)) {
        if (list != null && list.size() <= 1) {
          Jsf.error(component, "error.territory.exist");
        } else if (list != null && list.size() > 1) {

          long exist = UserProfileService.isTerritoryExist(main.em(), getUserProfile().getId(), list);
          if (exist > 0) {
            Jsf.error(component, "error.territory.exist");
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void gstinChangeEventHandler() {
    try {
      getUserProfile().setPanNo(getUserProfile().getGstNo().substring(2, 12));
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public void createUserCode() {
    String code = (getUserProfile().getName()).replaceAll("\\s+", "");
    getUserProfile().setUserCode(code.toLowerCase());
  }

  public List getSalesAgentAccountGroup(MainView main) {
    try {
      return UserProfileService.selectAgentAccountGroupByCompany(main, UserRuntimeView.instance().getCompany().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<Territory> getSalesAgentTerritory(MainView main) {
    try {
      return UserProfileService.selectAgentTerritoryByCompany(main, UserRuntimeView.instance().getCompany().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public Territory getTerritory() {
    return territory;
  }

  public void setTerritory(Territory territory) {
    this.territory = territory;
  }

  public void setCompanyFlag(Integer flag) {
    getCompany().setFlag(flag.intValue());
  }

  public Integer getNotify() {
    return notify;
  }

  public void setNotify(Integer notify) {
    this.notify = notify;
  }
}
