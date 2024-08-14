/*
 * @(#)ExternalUserView.java	1.0 Thu Oct 20 11:33:28 IST 2016
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.ExternalUser;
import spica.scm.domain.Status;
import spica.scm.domain.UserProfile;
import spica.scm.service.ExternalUserService;
import spica.scm.service.UserProfileService;
import spica.scm.service.UserService;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import spica.sys.domain.User;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * ExternalUserView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Oct 20 11:33:28 IST 2016
 */
@Named(value = "externalUserView")
@ViewScoped
public class ExternalUserView implements Serializable {

  private transient ExternalUser externalUser;	//Domain object/selected Domain.
  private transient LazyDataModel<ExternalUser> externalUserLazyModel; 	//For lazy loading datatable.
  private transient ExternalUser[] externalUserSelected;	 //Selected Domain Array
  private Company company;
  private List<Company> companySelected1;
  private transient List<ExternalUser> externalUserList;
  private transient Integer verifyOtp;

  public List<Company> getCompanySelected1() {
    return companySelected1;
  }

  public void setCompanySelected1(List<Company> companySelected1) {
    this.companySelected1 = companySelected1;
  }

  /**
   * Default Constructor.
   */
  public ExternalUserView() {
    super();
  }

  @PostConstruct
  public void init() {
    Integer id = Jsf.getParameterInt("id");
    getExternalUser().setId(id);
  }

  public ExternalUser getExternalUser() {
    if (externalUser == null) {
      externalUser = new ExternalUser();
      externalUser.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return externalUser;
  }

  /**
   * Set ExternalUser.
   *
   * @param externalUser.
   */
  public void setExternalUser(ExternalUser externalUser) {
    this.externalUser = externalUser;
  }

  public Company getCompany() {
    if (company == null) {
      company = UserRuntimeView.instance().getCompany();
    }
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public LazyDataModel<ExternalUser> getExternalUserLazyModel() {
    return externalUserLazyModel;
  }

  public void setExternalUserLazyModel(LazyDataModel<ExternalUser> externalUserLazyModel) {
    this.externalUserLazyModel = externalUserLazyModel;
  }

  public ExternalUser[] getExternalUserSelected() {
    return externalUserSelected;
  }

  public void setExternalUserSelected(ExternalUser[] externalUserSelected) {
    this.externalUserSelected = externalUserSelected;
  }

  public List<ExternalUser> getExternalUserList() {
    return externalUserList;
  }

  public void setExternalUserList(List<ExternalUser> externalUserList) {
    this.externalUserList = externalUserList;
  }

  public String switchExternalUser(MainView main, String viewType) {
    getCompany().setFlag(2);
    if (!StringUtil.isEmpty(viewType)) {
      try {
        if (UserRuntimeView.instance().getCompany() == null) {
          main.error("company.required");
          main.setViewType(ViewTypes.list);
          return null;
        }
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          setExternalUser(null);
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setExternalUser((ExternalUser) ExternalUserService.selectByPk(main, getExternalUser()));
          if (externalUser != null) {
            UserProfile userProfile = UserProfileService.getUserProfileForExternalUser(main, externalUser);
            User user = UserService.selectUserByUserProfile(main, userProfile);
            externalUser.setToken(user.getToken());
          }
        } else if (ViewTypes.isList(viewType)) {
          loadExternalUserList(main);
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
  private void loadExternalUserList(final MainView main) {
    if (externalUserLazyModel == null) {
      externalUserLazyModel = new LazyDataModel<ExternalUser>() {
        private List<ExternalUser> list;

        @Override
        public List<ExternalUser> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            if (getCompany() != null && getCompany().getId() != null) {
              if (company.getFlag() == 2) {
                list = ExternalUserService.listPagedByCompany(main, UserRuntimeView.instance().getCompany());
              }
            }
            main.commit(externalUserLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ExternalUser externalUser) {
          return externalUser.getId();
        }

        @Override
        public ExternalUser getRowData(String rowKey) {
          if (list != null) {
            for (ExternalUser obj : list) {
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
  public String saveExternalUser(MainView main) {
    return saveOrCloneExternalUser(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneExternalUser(MainView main) {
    main.setViewType("newform");
    return saveOrCloneExternalUser(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneExternalUser(MainView main, String key) {
    try {
      uploadFiles(); //File upload

      SystemRuntimeConfig.initRole(main);
      if (null != key) {
        if (key.equals("save")) {
          ExternalUserService.insertOrUpdate(main, getExternalUser());
          main.commit("success." + key);
          main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
        } else if (key.equals("clone")) {
          ExternalUserService.clone(main, getExternalUser());
          Jsf.warn("save.toclone");
          main.setViewType(ViewTypes.newform); // Change to ViewTypes.list to navigate to list page
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  public void updateExternalUser(MainView main) {
    try {
      ExternalUserService.updateByPk(main, externalUser);
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }

  }

  /**
   * Delete one or many
   *
   * @param main
   * @return the page to display.
   */
  public String deleteExternalUser(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(externalUserSelected)) {
        ExternalUserService.deleteByPkArray(main, getExternalUserSelected()); //many record delete from list
        main.commit("success.delete");
        externalUserSelected = null;
      } else {
        ExternalUserService.deleteByPk(main, getExternalUser());  //individual record delete from list or edit form
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

  public void ajaxBehaviorEventHandler(AjaxBehaviorEvent event) {
  }

  public void ajaxBehaviorEventHandler() {
  }

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

  public List<ExternalUser> getExternalUserList(MainView main) {
    try {
      if (UserRuntimeView.instance().getCompany() != null) {
        externalUserList = ExternalUserService.listPagedByCompany(main, UserRuntimeView.instance().getCompany());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return externalUserList;
  }

//
//  public void verifyPhone(MainView main) {
//    try {
//      Notify.otpSalesAgent(main, UserRuntimeView.instance().getCompany().getCompanySettings(), getExternalUser());
//      ExternalUserService.insertOrUpdate(main, getExternalUser(), null, null);
//      main.commit("success.save");
//    } catch (Throwable t) {
//      main.rollback(t, "error.save");
//    } finally {
//      main.close();
//    }
//    main.close();
//
//  }
//
//  public void verifyOtp(MainView main) {
//    try {
//      if (getVerifyOtp() != null && getExternalUser().getOtp().intValue() == getVerifyOtp()) {
//        getExternalUser().setPhoneVerified(1);
//        ExternalUserService.insertOrUpdate(main, getExternalUser(), null, null);
//        setVerifyOtp(null);
//        main.commit("success.save");
//      } else {
//        main.error("invalid.otp");
//      }
//    } catch (Throwable t) {
//      main.rollback(t, "error.save");
//    } finally {
//      main.close();
//    }
//  }
//
//  public void resendOtp(MainView main) {
//    try {
//      Notify.otpExternalUser(main, UserRuntimeView.instance().getCompany().getCompanySettings(), getExternalUser());
//      setVerifyOtp(null);
//      main.commit("success.send.sms");
//    } catch (Throwable t) {
//      main.rollback(t, "error.send.sms");
//    } finally {
//      main.close();
//    }
//
//  }
//
//  public void disableSMS(MainView main) {
//    try {
//      getExternalUser().setPhoneVerified(0);
//      getExternalUser().setOtp(null);
//      ExternalUserService.insertOrUpdate(main, getExternalUser(), null, null);
//      main.commit("success.save");
//    } catch (Throwable t) {
//      main.rollback(t, "error.save");
//    } finally {
//      main.close();
//    }
//  }
  public void createUserCode() {
    String code = (getExternalUser().getName()).replaceAll("\\s+", "");
    getExternalUser().setUserCode(code.toLowerCase());
  }

  public Integer getVerifyOtp() {
    return verifyOtp;
  }

  public void setVerifyOtp(Integer verifyOtp) {
    this.verifyOtp = verifyOtp;
  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    if (getExternalUser() != null) {
      return ScmLookupExtView.accountGroupAuto(getExternalUser().getCompanyId(), filter);
    }
    return null;
  }

  public void generateToken() {
    getExternalUser().setToken(new String(wawo.entity.util.RandomGen.getAlphaNumeric(25)));
  }
}
