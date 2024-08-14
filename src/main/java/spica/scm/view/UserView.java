/*
 * @(#)UserView.java	1.0 Wed Mar 30 11:28:07 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.service.UserService;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import spica.sys.domain.Role;
import spica.sys.domain.User;
import spica.sys.view.SysLookupView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * UserView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 11:28:07 IST 2016
 */
@Named(value = "userView")
@ViewScoped
public class UserView implements Serializable {

  private transient User user;	//Domain object/selected Domain.
  private transient LazyDataModel<User> userLazyModel; 	//For lazy loading datatable.
  private transient User[] userSelected;	 //Selected Domain Array
  private List<Role> roleSelected; // 1 List to hold the relation selection
  private transient String userFilter;
  private List<Role> userRole;
  // private List<Company> companySelected;
  private List<Company> companySelected1;
  private transient List<Account> accountList;
  private transient Account defualtAccount;
  private Integer allAccPermission = 0;
  private transient String newPassword;
  private transient Integer notify = 0;

  public Account getDefualtAccount() {
    return defualtAccount;
  }

  public void setDefualtAccount(Account defualtAccount) {
    this.defualtAccount = defualtAccount;
  }

  /**
   * Default Constructor.
   */
  public List<Account> getAccountList() {
    if (accountList == null) {
      accountList = new ArrayList<>();
    }
    return accountList;
  }

  public void setAccountList(List<Account> accountList) {
    this.accountList = accountList;
  }

  public List<Account> accountAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      if (!StringUtil.isEmpty(companySelected1)) {
        Integer[] companyId = new Integer[companySelected1.size()];
        int i = 0;
        for (Company companu : companySelected1) {
          if (companu.getId() != null) {
            companyId[i++] = companu.getId();
            //ac.addAll(SysLookupView.accountAuto(companu.getId()));
          }
        }
        return UserRuntimeService.accountAuto(main, filter, companyId, getUser());
      } else {
        return UserRuntimeService.accountAuto(main, filter, new Integer[]{UserRuntimeView.instance().getCompany().getId()}, getUser());
        //  ac.addAll(SysLookupView.accountAuto(UserRuntimeView.instance().getCompany().getId()));
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

//  public List<Company> getCompanySelected() {
//    return companySelected;
//  }
//
//  public void setCompanySelected(List<Company> companySelected) {
//    this.companySelected = companySelected;
//  }
  public List<Company> getCompanySelected1() {
    return companySelected1;
  }

  public void setCompanySelected1(List<Company> companySelected1) {
    this.companySelected1 = companySelected1;
  }

  public void updateAccount(AjaxBehaviorEvent event) {
    //System.out.println(event.getSource());
  }

  public List<Role> getUserRole() {
    return userRole;
  }

  public void setUserRole(List<Role> userRole) {
    this.userRole = userRole;
  }

  public Integer getAllAccPermission() {
    return allAccPermission;
  }

  public void setAllAccPermission(Integer allAccPermission) {
    this.allAccPermission = allAccPermission;
  }

  public List<Role> displayUserRole(MainView main) {
    try {
      if (getUser().getId() != null) {
        if (main.getAppUser().isRoot() || (getUser().getUserProfileId().getCompanyId() != null && getUser().getUserProfileId().getVendorId() == null && getUser().getUserProfileId().getCustomerId() == null && getUser().getUserProfileId().getTransporterId() == null)) {
          userRole = SysLookupView.roleCompany(main);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return userRole;
  }

  public boolean renderedUserRole(MainView main) {

    if (main.getAppUser().isRoot() || (getUser().getUserProfileId().getCompanyId() != null && getUser().getUserProfileId().getVendorId() == null && getUser().getUserProfileId().getCustomerId() == null && getUser().getUserProfileId().getTransporterId() == null)) {
      return true;
    }
    return false;
  }

  public String displayUserName() {
    if (getUser().getUserProfileId() == null) {
      return getUser().getName();
    } else if (getUser().getUserProfileId().getVendorId() != null) {
      return getUser().getUserProfileId().getVendorId().getVendorName();
    } else if (getUser().getUserProfileId().getCustomerId() != null) {
      return getUser().getUserProfileId().getCustomerId().getCustomerName();
    } else if (getUser().getUserProfileId().getTransporterId() != null) {
      return getUser().getUserProfileId().getTransporterId().getTransporterName();
    } else if (getUser().getUserProfileId().getCompanyId() != null) {
      return getUser().getUserProfileId().getCompanyId().getCompanyName();
    }
    return "Unknown";
  }

  public List<Company> displayCompanyName(MainView main) {
    try {
      return SysLookupView.companyName(main, UserRuntimeView.instance().getCompany(), UserRuntimeView.instance().getAppUser());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public UserView() {
    super();
  }

  public String getUserFilter() {
    return userFilter;
  }

  public void setUserFilter(String userFilter) {
    this.userFilter = userFilter;
  }

  /**
   * Return User.
   *
   * @return User.
   */
  public User getUser() {
    if (user == null) {
      user = new User();
    }
    return user;
  }

  /**
   * Set User.
   *
   * @param user.
   */
  public void setUser(User user) {
    this.newPassword = "";
    this.user = user;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchUser(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew()) {
          getUser().reset();
          roleSelected = null;
          companySelected1 = null;
          accountList = null;
        } else if (main.isEdit()) {
          setUser((User) UserService.selectByPk(main, getUser()));
          roleSelected = UserService.listRelatedRole(main, getUser());
          companySelected1 = UserService.listRelatedCompany(main, getUser());
          accountList = UserService.listRelatedAccount(main, getUser());
          if (StringUtil.isEmpty(accountList)) {
            if (UserService.hasAllAccountPermission(main, user)) {
              setAllAccPermission(1);
            } else {
              setAllAccPermission(0);
            }
          } else {
            setAllAccPermission(0);
          }
        } else if (main.isList()) {
          setUserFilter("1");
          loadUserList(main);
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
   * Create userLazyModel.
   *
   * @param main
   */
  private void loadUserList(final MainView main) {
    if (userLazyModel == null) {
      userLazyModel = new LazyDataModel<User>() {
        private List<User> list;

        @Override
        public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = UserService.listPaged(main, UserRuntimeView.instance().getCompany(), getUserFilter());
            main.commit(userLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(User user) {
          return user.getId();
        }

        @Override
        public User getRowData(String rowKey) {
          if (list != null) {
            for (User obj : list) {
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
    String SUB_FOLDER = "sec_user/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveUser(MainView main) {
    return saveOrCloneUser(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneUser(MainView main) {
    main.setViewType("newform");
    return saveOrCloneUser(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneUser(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            UserService.insertOrUpdate(main, getUser(), roleSelected, companySelected1, accountList, allAccPermission, notify == 1);
            main.commit("success." + key);
            main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
            break;
          case "clone":
            UserService.clone(main, getUser(), roleSelected, companySelected1, accountList, allAccPermission);
            Jsf.warn("save.toclone");
            main.setViewType(ViewTypes.newform); // Change to ViewTypes.list to navigate to list page
            break;
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many User.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteUser(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(userSelected)) {
        UserService.deleteByPkArray(main, getUserSelected()); //many record delete from list
        main.commit("success.delete");
        userSelected = null;
      } else {
        UserService.deleteByPk(main, getUser());  //individual record delete from list or edit form
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
   * Return LazyDataModel of User.
   *
   * @return
   */
  public LazyDataModel<User> getUserLazyModel() {
    return userLazyModel;
  }

  /**
   * Return User[].
   *
   * @return
   */
  public User[] getUserSelected() {
    return userSelected;
  }

  /**
   * Set User[].
   *
   * @param userSelected
   */
  public void setUserSelected(User[] userSelected) {
    this.userSelected = userSelected;
  }

  public List<Role> getRoleSelected() {
    return roleSelected;
  }

  public void setRoleSelected(List<Role> roleSelected) {
    this.roleSelected = roleSelected;
  }

  public void onAccountSelect(SelectEvent event) {
    Set<Account> tempSet = new HashSet<>();
    List<Account> tempList = null;
    if (getAccountList().size() > 1) {
      for (Account acnt : getAccountList()) {
        tempSet.add(acnt);
      }
      tempList = new ArrayList<>(tempSet);
      setAccountList(tempList);
    }
  }

  public void onAccountUnSelect(UnselectEvent event) {
    //   Account unselectedAccount = (Account) event.getObject();
    //   System.out.println("Size :" + getAccountList().size());
    if (getAccountList().size() == 1) {
      getAccountList().clear();
      setDefualtAccount(null);
    }
  }

  public void resetPassword(MainView main) {
    try {
      UserService.changePassword(main, getUser(), newPassword, true); //many record delete from list
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public Integer getNotify() {
    return notify;
  }

  public void setNotify(Integer notify) {
    this.notify = notify;
  }

}
