/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import spica.scm.domain.Department;
import spica.scm.domain.Designation;
import spica.scm.domain.ExternalUser;
import spica.scm.domain.UserProfile;
import spica.scm.validate.ExternalUserIs;
import spica.sys.domain.User;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.SqlPage;

/**
 *
 * @author Godson Joseph
 */
public class ExternalUserService {

  /**
   * ExternalUser paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getExternalUserSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_external_user", ExternalUser.class, main);
    sql.main("select scm_external_user.id,scm_external_user.company_id,scm_external_user.user_code,scm_external_user.name,scm_external_user.address_1,scm_external_user.address_2,scm_external_user.phone_1,scm_external_user.phone_2,scm_external_user.email,scm_external_user.pan_no,scm_external_user.status_id,scm_external_user.created_by,scm_external_user.modified_by,scm_external_user.created_at,scm_external_user.modified_at from scm_external_user scm_external_user "); //Main query
    sql.count("select count(scm_external_user.id) as total from scm_external_user scm_external_user "); //Count query
    sql.join("join scm_company scm_external_usercompany_id on (scm_external_usercompany_id.id = scm_external_user.company_id)   join scm_status scm_external_userstatus_id on (scm_external_userstatus_id.id = scm_external_user.status_id)"); //Join Query

    sql.string(new String[]{"scm_external_usercompany_id.company_name", "scm_external_user.user_code", "scm_external_user.name", "scm_external_user.address_1", "scm_external_user.address_2", "scm_external_user.phone_1", "scm_external_user.phone_2", "scm_external_user.email", "scm_external_user.pan_no", "scm_external_userstatus_id.title", "scm_external_user.created_by", "scm_external_user.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_external_user.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_external_user.created_at", "scm_external_user.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ExternalUser.
   *
   * @param main
   * @param company
   * @return List of ExternalUser
   */
  public static List<ExternalUser> listPaged(MainView main, Company company) {
    SqlPage sql = getExternalUserSqlPaged(main);
    sql.cond("where scm_external_user.company_id=?");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

  /**
   * Select ExternalUser by key.
   *
   * @param main
   * @param externalUser
   * @return ExternalUser
   */
  public static final ExternalUser selectByPk(Main main, ExternalUser externalUser) {
    return (ExternalUser) AppService.find(main, ExternalUser.class, externalUser.getId());
  }

  /**
   * Insert ExternalUser.
   *
   * @param main
   * @param externalUser
   */
  public static final void insert(Main main, ExternalUser externalUser) {
    ExternalUserIs.insertAble(main, externalUser);  //Validating
    AppService.insert(main, externalUser);

  }

  /**
   * Update ExternalUser by key.
   *
   * @param main
   * @param externalUser
   * @return ExternalUser
   */
  public static final ExternalUser updateByPk(Main main, ExternalUser externalUser) {
    ExternalUserIs.updateAble(main, externalUser); //Validating
    return (ExternalUser) AppService.update(main, externalUser);
  }

  /**
   * Insert or update UserProfile
   *
   * @param main
   * @param userProfile
   */
  public static void insertOrUpdate(Main main, ExternalUser externalUser) {
    if (externalUser.getId() == null) {
      insert(main, externalUser);
    } else {
      updateByPk(main, externalUser);
    }
    UserProfile profile = null;
    if (externalUser != null && externalUser.getId() != null) {
      profile = UserProfileService.getUserProfileForExternalUser(main, externalUser);
    }
    profile = insertOrUpdateUserProfile(main, externalUser, profile);
    User user = null;
    if (externalUser != null && externalUser.getId() != null && profile != null && profile.getId() != null) {
      user = UserService.selectUserByUserProfile(main, profile);
    }
    insertOrUpdateUser(main, externalUser, profile, user);
//    LedgerExternalService.saveLedgerSalesAgent(main, externalUser);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param userProfile
   */
  public static void clone(Main main, ExternalUser externalUser) {
    main.em().detach(externalUser);
    externalUser.setId(null); //Set to null for insert
    // insertOrUpdate(main, externalUser);
  }

  /**
   * Delete UserProfile.
   *
   * @param main
   * @param userProfile
   */
  public static final void deleteByPk(Main main, ExternalUser externalUser) {
    ExternalUserIs.deleteAble(main, externalUser); //Validation
    AppService.deleteSql(main, UserProfile.class, "delete from sex_user where user_profile_id in(select id from scm_user_profile t where t.external_user_id=?)",
            new Object[]{externalUser.getId()});
    AppService.deleteSql(main, UserProfile.class, "delete from scm_user_profile t where t.sales_agent_id=?", new Object[]{externalUser.getId()});
    AppService.delete(main, ExternalUser.class, externalUser.getId());
  }

  /**
   * Delete Array of UserProfile.
   *
   * @param main
   * @param userProfile
   */
  public static final void deleteByPkArray(Main main, ExternalUser[] externalUsers) {
    for (ExternalUser e : externalUsers) {
      deleteByPk(main, e);
    }
  }

  public static List<ExternalUser> listPagedNotInCompanyContact(Main main, Company company) {
    SqlPage sql = getExternalUserSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_external_user.id not in(select scm_company_contact.user_profile_id from scm_company_contact scm_company_contact where scm_company_contact.company_id =?)and scm_user_profile.company_id=? and scm_user_profile.vendor_id is null and scm_user_profile.customer_id is null and scm_user_profile.transporter_id is null");
    sql.param(company.getId());
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static List<ExternalUser> listPagedByCompany(Main main, Company company) {
    SqlPage sql = getExternalUserSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    String cond = " where scm_external_user.company_id=? ";
    sql.param(company.getId());
    sql.cond(cond);
    return AppService.listPagedJpa(main, sql);
  }

  public static List<ExternalUser> listPagedByExternalUser(Main main, Company company) {
    main.clear();
    SqlPage sql = getExternalUserSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_external_user.company_id=? ");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

  private static UserProfile insertOrUpdateUserProfile(Main main, ExternalUser externalUser, UserProfile profile) {
    if (profile == null || (profile != null && profile.getId() == null)) {
      profile = new UserProfile();
    }
    profile.setExternalUserId(externalUser);
    profile.setAddress1(externalUser.getAddress1());
    profile.setAddress2(externalUser.getAddress2());
    profile.setCompanyId(externalUser.getCompanyId());
    profile.setCreatedAt(externalUser.getCreatedAt());
    profile.setCreatedBy(externalUser.getCreatedBy());
    profile.setDepartmentId(new Department(UserProfileService.DEPARTMENT_ID));
    profile.setDesignationId(new Designation(UserProfileService.DESIGNATION_SALES_AGENT));
    profile.setEmail(externalUser.getEmail());
    profile.setGstNo(externalUser.getGstNo());
    profile.setName(externalUser.getName());
    profile.setPhone1(externalUser.getPhone1());
    profile.setPhone2(externalUser.getPhone2());
    profile.setStatusId(externalUser.getStatusId());
    profile.setUserCode(externalUser.getUserCode());
    UserProfileService.insertOrUpdateForSalesAgent(main, profile);
    return profile;
  }

  private static void insertOrUpdateUser(Main main, ExternalUser externalUser, UserProfile profile, User user) {
    if (user == null || (user != null && user.getId() == null)) {
      user = new User();
    }
    user.setName(externalUser.getName());
    user.setEmail(externalUser.getEmail());
    user.setPhone(externalUser.getPhone1());
    user.setCreatedBy(externalUser.getCreatedBy());
    user.setCreatedAt(externalUser.getCreatedAt());
    user.setLogin(externalUser.getName());
    user.setStatusId(externalUser.getStatusId());
    user.setUserProfileId(profile);
    user.setToken(externalUser.getToken());
    UserService.insertOrUpdate(main, user, null, null, null, 0, true);
  }
//
//  private static List<ExternalUser> externalUsers(Main main, Company company, String filter) {
//    if (company.getId() == null) {
//      return null;
//    }
//    String sql = "select scm_external_user.id,scm_external_user.company_id,\n"
//            + "scm_external_user.user_code,scm_external_user.department_id,scm_external_user.name,scm_external_user.address_1,scm_external_user.address_2,scm_external_user.phone_1,\n"
//            + "scm_external_user.phone_2,scm_external_user.email,scm_external_user.pan_no,scm_external_user.status_id,scm_external_user.created_by,scm_external_user.modified_by,\n"
//            + "scm_external_user.created_at,scm_external_user.modified_at from scm_external_user scm_external_user  "
//            + "left outer join scm_company scm_external_usercompany_id on (scm_external_usercompany_id.id = scm_external_user.company_id) \n"
//            + " where  scm_external_user.company_id=? \n"
//            + "and upper(scm_external_user.name) like ?\n"
//            + "ORDER BY scm_external_user.user_code";
//
//    return main.em().list(ExternalUser.class, sql, new Object[]{company.getId(), "%" + filter.toUpperCase() + "%"});
//
//  }

}
