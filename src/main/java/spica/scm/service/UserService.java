/*
 * @(#)UserService.java	1.0 Wed Mar 30 11:28:07 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.addon.notification.Notify;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.domain.SalesAgentAccountGroup;
import spica.scm.domain.Status;
import spica.scm.domain.UserAccount;
import spica.scm.domain.UserCompany;
import spica.scm.domain.UserProfile;
import spica.sys.UserRuntimeService;
import spica.sys.domain.Role;
import spica.sys.domain.User;
import spica.sys.domain.UserRole;
import spica.sys.service.UserRoleService;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.config.AppConfig;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;
import wawo.entity.db.system.DbConnector;
import wawo.entity.util.Secure;
import wawo.entity.util.StringUtil;

/**
 * UserService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 11:28:07 IST 2016
 */
public abstract class UserService {

  /**
   * User paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getUserSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("sec_user", User.class, main);
    sql.main("select sec_user.id,sec_user.login,sec_user.name,sec_user.email,sec_user.password,sec_user.phone,sec_user.status_id,sec_user.created_by,sec_user.modified_by,sec_user.created_at,sec_user.modified_at,sec_user.user_profile_id from sec_user sec_user"); //Main query
    sql.count("select count(sec_user.id) from sec_user sec_user"); //Count query
    sql.join("left outer join scm_status scm_status on (scm_status.id = sec_user.status_id) left outer join scm_user_profile scm_user_profile on (scm_user_profile.id = sec_user.user_profile_id) "); //Join Query

    sql.string(new String[]{"sec_user.name", "sec_user.login", "sec_user.email", "sec_user.password", "sec_user.phone", "sec_user.created_by", "sec_user.modified_by"}); //String search or sort fields
    sql.number(new String[]{"sec_user.id", "sec_user.status_id"}); //Number search or sort fields
    sql.date(new String[]{"sec_user.created_at", "sec_user.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of User.
   *
   * @param main
   * @param company
   * @param userFilter
   * @return List of User
   */
  public static final List<User> listPaged(Main main, Company company, String userFilter) {

    SqlPage sql = getUserSqlPaged(main);
    main.clear();
    if ("1".equals(userFilter)) {
      sql.cond("where scm_user_profile.company_id = ? and scm_user_profile.vendor_id is NULL and scm_user_profile.customer_id is NULL and scm_user_profile.transporter_id is NULL");
      sql.param(company.getId());
    } else if ("2".equals(userFilter)) {
      sql.join("left outer join scm_vendor scm_vendor on (scm_vendor.id=scm_user_profile.vendor_id)");
      sql.cond("where scm_user_profile.company_id = ? and scm_user_profile.vendor_id is not NULL and scm_user_profile.customer_id is NULL and scm_user_profile.transporter_id is NULL");
      sql.param(company.getId());
    } else if ("3".equals(userFilter)) {
      sql.join("left outer join scm_customer scm_customer on(scm_customer.id=scm_user_profile.customer_id)");
      sql.cond("where scm_user_profile.company_id = ? and scm_user_profile.customer_id is not NULL and scm_user_profile.vendor_id is NULL and scm_user_profile.transporter_id is NULL");
      sql.param(company.getId());
    } else if ("4".equals(userFilter)) {
      sql.join("left outer join scm_transporter scm_transporter on(scm_transporter.id=scm_user_profile.transporter_id)");
      sql.cond("where scm_user_profile.company_id = ? and scm_user_profile.transporter_id is not NULL and scm_user_profile.vendor_id is NULL and scm_user_profile.customer_id is NULL");
      sql.param(company.getId());
    }

    return AppService.listPagedJpa(main, sql);
  }

  public static final List<Role> listRelatedRole(Main main, User user) {
    return AppService.list(main, Role.class, "select * from sec_role where id IN (select sec_user_role.role_id from sec_user_role where sec_user_role.user_id=?)", new Object[]{user.getId()});
  }

  public static final List<Company> listRelatedCompany(Main main, User user) {
    return AppService.list(main, Company.class, "select * from scm_company where id IN (select scm_user_company.company_id from scm_user_company where scm_user_company.user_id=?)", new Object[]{user.getId()});
  }

  public static final List<Account> listRelatedAccount(Main main, User user) {
    return AppService.list(main, Account.class, "select * from scm_account where id IN (select scm_user_account.account_id from scm_user_account where scm_user_account.user_id=?)", new Object[]{user.getId()});
  }

  public static final boolean hasAllAccountPermission(Main main, User user) {
    return AppService.exist(main, "select id from scm_user_account where user_id=? and account_id is null", new Object[]{user.getId()});
  }
//  /**
//   * Return list of User based on condition
//   * @param main
//   * @return List<User>
//   */
//  public static final List<User> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getUserSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select User by key.
   *
   * @param main
   * @param user
   * @return User
   */
  public static final User selectByPk(Main main, User user) {
    return (User) AppService.find(main, User.class, user.getId());
  }

  /**
   * Insert User.
   *
   * @param main
   * @param user
   */
  public static final void insert(Main main, User user, boolean notify) {
    insertAble(main, user);  //Validating
    boolean genPwd = false;
    String pwdTxt = user.getPassword();
    if (StringUtil.isEmpty(pwdTxt)) {
      pwdTxt = new String(wawo.entity.util.RandomGen.getAlphaNumeric(15));
      genPwd = true;
    }
    user.setPassword(Secure.hashArgon2(pwdTxt));
    AppService.insert(main, user);
    if (notify) {
      Notify.registration(main, user, genPwd ? pwdTxt : "");
    }
  }

  /**
   * Update User by key.
   *
   * @param main
   * @param user
   * @return User
   */
  public static final User updateByPk(Main main, User user) {
    updateAble(main, user); //Validating
    return (User) AppService.update(main, user);
  }

  public static void insertArray(Main main, List<Role> roleSelected, List<Company> companySelected1, List<Account> accountList, User user, int allAccPermission) {
    if (roleSelected != null) {
      AppService.deleteSql(main, UserRole.class, "delete from sec_user_role where user_id = ? ", new Object[]{user.getId()});
      UserRole ur;
      for (Role role : roleSelected) {  //Reinserting
        ur = new UserRole();
        ur.setRoleId(role);
        ur.setUserId(user);
        UserRoleService.insert(main, ur);
      }
    }
    if (companySelected1 != null) {
      AppService.deleteSql(main, UserCompany.class, "delete from scm_user_company where user_id = ? ", new Object[]{user.getId()});
      UserCompany uc;
      for (Company company : companySelected1) {  //Reinserting
        uc = new UserCompany();
        uc.setCompanyId(company);
        uc.setUserId(user);
        AppService.insert(main, uc);
      }
    }
    if (allAccPermission == 1) {
      AppService.deleteSql(main, UserAccount.class, "delete from scm_user_account where user_id = ? ", new Object[]{user.getId()});
      UserAccount ua;
      ua = new UserAccount();
      ua.setAccountId(null);
      ua.setUserId(user);
      AppService.insert(main, ua);
    } else {
      AppService.deleteSql(main, UserAccount.class, "delete from scm_user_account where user_id = ? ", new Object[]{user.getId()});
      if (accountList != null) {
        UserAccount ua;
        for (Account account : accountList) {  //Reinserting
          ua = new UserAccount();
          ua.setAccountId(account);
          ua.setUserId(user);
          AppService.insert(main, ua);
        }
      }
    }
  }
//  public static void insertCompanyArray(Main main, List<Company> companySelected1, User user) {
//    if (companySelected1 != null) {
//      UserCompany uc;
//      for (Company company : companySelected1) {  //Reinserting
//        uc = new UserCompany();
//        uc.setCompanyId(company);
//        uc.setUserId(user);
//        UserCompanyService.insert(main, uc);
//      }
//    }
//  }

  /**
   * Insert or update User
   *
   * @param main
   * @param User
   */
  public static void insertOrUpdate(Main main, User user, List<Role> roleSelected, List<Company> companySelected1, List<Account> accountList, int allAccPermission, boolean notify) {
    if (user.getId() == null) {
      insert(main, user, notify);
    } else {
      AppService.deleteSql(main, UserRole.class, "delete from sec_user_role where user_id=?", new Object[]{user.getId()}); //Deleting based on user id
      updateByPk(main, user);
    }
    if (roleSelected != null) {
      insertArray(main, roleSelected, companySelected1, accountList, user, allAccPermission);   //Inserting all the relation records.
    }
//    if(companySelected1!= null){
//      insertCompanyArray(main, companySelected1, user);
//    }

  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param user
   */
  public static void clone(Main main, User user, List<Role> roleSelected, List<Company> companySelected1, List<Account> accountList, int allAccPermission) {
    main.em().detach(user);
    user.setId(null); //Set to null for insert
    // insertOrUpdate(main, user, roleSelected, companySelected1, accountList, allAccPermission, false);
  }

  /**
   * Delete User.
   *
   * @param main
   * @param user
   */
  public static final void deleteByPk(Main main, User user) {
    AppService.deleteSql(main, SalesAgentAccountGroup.class, "delete from scm_sales_agent_account_group t where t.sales_agent_id=?", new Object[]{user.getId()});
    AppService.deleteSql(main, UserRole.class, "delete from sec_user_role where sec_user_role.user_id=?", new Object[]{user.getId()});
    AppService.delete(main, User.class, user.getId());
  }

  /**
   * Delete Array of User.
   *
   * @param main
   * @param user
   */
  public static final void deleteByPkArray(Main main, User[] user) {
    for (User e : user) {
      deleteByPk(main, e);
    }
  }

  public static void deleteUserLogin(Main main, String usrCode) {
    AppService.deleteSql(main, User.class, "delete from sec_user where login=?", new Object[]{usrCode});
  }

  public static final User selectUser(Main main, String userCode) {
    return (User) AppService.single(main, User.class, "select * from sec_user sec_user where upper(sec_user.login) = ?", new Object[]{StringUtil.toUpperCase(userCode.toString())});
  }

  public static final User selectUserByUserProfile(Main main, UserProfile userProfile) {
    User user = null;
    if (userProfile != null) {
      user = (User) AppService.single(main, User.class, "select * from sec_user sec_user where user_profile_id = ?", new Object[]{userProfile.getId()});
    }
    return user;
  }

  public static void changePassword(MainView main, User user, String newPassword, boolean mailpwd) {
    if (StringUtil.isEmpty(newPassword)) {
      newPassword = new String(wawo.entity.util.RandomGen.getAlphaNumeric(15));
      mailpwd = true;
    }
    user.setPassword(Secure.hashArgon2(newPassword));
//    main.param(user.getPassword());
//    main.param(user.getId());
//    AppService.updateSql(main, User.class, "update sec_user set modified_by = ?, modified_at = ?, password = ? where id = ?", true);
    AppService.update(main, user);
    Notify.resetPassword(main, user, mailpwd ? newPassword : "");
  }

  public static void userValidate(MainView main, String email, String phone, String userCode) {
    User user = new User();
    user.setEmail(email);
    user.setPhone(phone);
    user.setLogin(userCode);
    insertAble(main, user);
  }

  public static final User selectUserByToken(DbConnector dbConnector, String token) {
    User user = null;
    if (token != null) {
      user = (User) AppDb.single(dbConnector, User.class, "select * from sec_user sec_user where token = ? and status_id=1", new Object[]{token});
    }
    return user;
  }

  /**
   * Validate insert.
   *
   * @param main
   * @param user
   * @throws UserMessageException
   */
  public static final void insertAble(Main main, User user) throws UserMessageException {
    if (AppService.exist(main, "select '1' from sec_user where login=? or phone=? or upper(email)=?", new Object[]{user.getLogin(), user.getPhone(), StringUtil.toUpperCase(user.getEmail())})) {
      throw new UserMessageException("code.exist", new String[]{"/ email / phone"});
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param user
   * @throws UserMessageException
   */
  public static final void updateAble(Main main, User user) throws UserMessageException {
//    if (AppService.exist(main, "select '1' from sec_user where (upper(login)=? and id !=?) or (phone=? and id !=?) or (upper(email)=? and id !=?)", new Object[]{user.getLogin().toString(), user.getId(), user.getPhone(), user.getId(), StringUtil.toUpperCase(user.getEmail().toString()), user.getId()})) {
//      throw new UserMessageException("name.exist");
//    }

  }
}
