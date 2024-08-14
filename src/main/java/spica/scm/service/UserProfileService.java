/*
 * @(#)UserProfileService.java	1.0 Thu Oct 20 11:33:35 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.ExternalUser;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.SalesAgentAccountGroup;
import spica.scm.domain.SalesAgentTerritory;
import spica.scm.domain.Territory;
import spica.scm.domain.Transporter;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Vendor;
import spica.scm.validate.UserProfileIs;
import spica.sys.domain.User;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppEm;
import wawo.entity.core.SqlPage;

/**
 * UserProfileService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Oct 20 11:33:35 IST 2016
 */
public abstract class UserProfileService {

  //TODO move to sys runtime
  public static final int TOP_MANAGEMENT_DESIGNATION_LEVEL_ID = 1;
  public static final Integer DESIGNATION_SALES_AGENT = 14;
  public static final Integer DEPARTMENT_ID = 13;
  public static final Integer INDIVIDUAL = 1;
  /**
   * UserProfile paginated query.
   *
   * @param main
   * @return SqlPage
   */
  public static final String EMPLOYEE_URL = "/scm/sys/user_profile.xhtml";

  private static SqlPage getUserProfileSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_user_profile", UserProfile.class, main);
    sql.main("select scm_user_profile.id,scm_user_profile.company_id,scm_user_profile.vendor_id,scm_user_profile.transporter_id,scm_user_profile.customer_id,scm_user_profile.user_code,scm_user_profile.department_id,scm_user_profile.designation_id,scm_user_profile.name,scm_user_profile.address_1,scm_user_profile.address_2,scm_user_profile.phone_1,scm_user_profile.phone_2,scm_user_profile.email,scm_user_profile.pan_no,scm_user_profile.status_id,scm_user_profile.created_by,scm_user_profile.modified_by,scm_user_profile.created_at,scm_user_profile.modified_at,scm_user_profile.sales_agent_type from scm_user_profile scm_user_profile "); //Main query
    sql.count("select count(scm_user_profile.id) as total from scm_user_profile scm_user_profile "); //Count query
    sql.join("left outer join scm_company scm_user_profilecompany_id on (scm_user_profilecompany_id.id = scm_user_profile.company_id) left outer join scm_vendor scm_user_profilevendor_id on (scm_user_profilevendor_id.id = scm_user_profile.vendor_id)left outer join scm_transporter scm_user_profiletransporter_id on (scm_user_profiletransporter_id.id = scm_user_profile.transporter_id) left outer join scm_customer scm_user_profilecustomer_id on (scm_user_profilecustomer_id.id = scm_user_profile.customer_id) left outer join scm_department scm_user_profiledepartment_id on (scm_user_profiledepartment_id.id = scm_user_profile.department_id) left outer join scm_designation scm_user_profiledesignation_id on (scm_user_profiledesignation_id.id = scm_user_profile.designation_id) left outer join scm_status scm_user_profilestatus_id on (scm_user_profilestatus_id.id = scm_user_profile.status_id)"); //Join Query

    sql.string(new String[]{"scm_user_profilecompany_id.company_name", "scm_user_profilevendor_id.vendor_name", "scm_user_profiletransporter_id.transporter_name", "scm_user_profilecustomer_id.customer_name", "scm_user_profile.user_code", "scm_user_profiledepartment_id.title", "scm_user_profiledesignation_id.title", "scm_user_profile.name", "scm_user_profile.address_1", "scm_user_profile.address_2", "scm_user_profile.phone_1", "scm_user_profile.phone_2", "scm_user_profile.email", "scm_user_profile.pan_no", "scm_user_profilestatus_id.title", "scm_user_profile.created_by", "scm_user_profile.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_user_profile.id", "scm_user_profile.sales_agent_type"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_user_profile.created_at", "scm_user_profile.modified_at"});  //Date search or sort fields
    return sql;
  }

  public static final List<UserProfile> listPaged(Main main) {
    return AppService.listPagedJpa(main, getUserProfileSqlPaged(main));
  }

  /**
   * Select UserProfile by key.
   *
   * @param main
   * @param userProfile
   * @return UserProfile
   */
  public static final UserProfile selectByPk(Main main, UserProfile userProfile) {
    return (UserProfile) AppService.find(main, UserProfile.class, userProfile.getId());
  }

  /**
   * Insert UserProfile.
   *
   * @param main
   * @param userProfile
   */
  public static final void insert(Main main, UserProfile userProfile) {
    UserProfileIs.insertAble(main, userProfile);  //Validating
    AppService.insert(main, userProfile);

  }

  /**
   * Update UserProfile by key.
   *
   * @param main
   * @param userProfile
   * @return UserProfile
   */
  public static final UserProfile updateByPk(Main main, UserProfile userProfile) {
    UserProfileIs.updateAble(main, userProfile); //Validating
    return (UserProfile) AppService.update(main, userProfile);
  }

  /**
   * Insert or update UserProfile
   *
   * @param main
   * @param userProfile
   */
  public static void insertOrUpdate(Main main, UserProfile userProfile) {
    if (userProfile.getId() == null) {
      insert(main, userProfile);
    } else {
      updateByPk(main, userProfile);
    }
  }

  public static void insertOrUpdateForSalesAgent(Main main, UserProfile userProfile) {
    if (userProfile.getId() == null) {
      AppService.insert(main, userProfile);
    } else {
      AppService.update(main, userProfile);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param userProfile
   */
  public static void clone(Main main, UserProfile userProfile, List<AccountGroup> agList, List<Territory> tList) {
    main.em().detach(userProfile);
    userProfile.setId(null); //Set to null for insert
    insertOrUpdate(main, userProfile);
  }

  /**
   * Delete UserProfile.
   *
   * @param main
   * @param userProfile
   */
  public static final void deleteByPk(Main main, UserProfile userProfile) {
    UserProfileIs.deleteAble(main, userProfile); //Validation
    AppService.deleteSql(main, SalesAgentTerritory.class, "delete from scm_sales_agent_territory t where t.sales_agent_id=?", new Object[]{userProfile.getId()});
    AppService.deleteSql(main, SalesAgentAccountGroup.class, "delete from scm_sales_agent_account_group t where t.sales_agent_id=?", new Object[]{userProfile.getId()});
    AppService.deleteSql(main, User.class, "delete from sec_user sec_user where sec_user.user_profile_id=?", new Object[]{userProfile.getId()});
    AppService.delete(main, UserProfile.class, userProfile.getId());
  }

  /**
   * Delete Array of UserProfile.
   *
   * @param main
   * @param userProfile
   */
  public static final void deleteByPkArray(Main main, UserProfile[] userProfile) {
    for (UserProfile e : userProfile) {
      deleteByPk(main, e);
    }
  }

  public static List<UserProfile> listPagedNotInCompanyContact(Main main, Company company) {
    SqlPage sql = getUserProfileSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_user_profile.id not in(select scm_company_contact.user_profile_id from scm_company_contact scm_company_contact where scm_company_contact.company_id =?)and scm_user_profile.company_id=? and scm_user_profile.vendor_id is null and scm_user_profile.customer_id is null and scm_user_profile.transporter_id is null");
    sql.param(company.getId());
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static List<UserProfile> listPagedNotInVendorContact(Main main, Vendor vendor) {
    main.clear();
    SqlPage sql = getUserProfileSqlPaged(main);
    if (vendor.getId() == null) {
      return null;
    }
    sql.cond("where scm_user_profile.vendor_id = ? and  scm_user_profile.id not in(select scm_vendor_contact.user_profile_id from scm_vendor_contact scm_vendor_contact where scm_vendor_contact.vendor_id =?)");
    sql.param(vendor.getId());
    sql.param(vendor.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static List<UserProfile> listPagedNotInTransporterContact(Main main, Transporter transporter) {
    SqlPage sql = getUserProfileSqlPaged(main);
    if (transporter.getId() == null) {
      return null;
    }
    sql.cond("where scm_user_profile.id not in(select scm_transporter_contact.user_profile_id from scm_transporter_contact scm_transporter_contact where scm_transporter_contact.transporter_id =?) and scm_user_profile.transporter_id=?");
    sql.param(transporter.getId());
    sql.param(transporter.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static List<UserProfile> listPagedNotInCustomerContact(Main main, Customer customer) {
    SqlPage sql = getUserProfileSqlPaged(main);
    if (customer.getId() == null) {
      return null;
    }
    sql.cond("where scm_user_profile.id not in(select scm_customer_contact.user_profile_id from scm_customer_contact scm_customer_contact where scm_customer_contact.customer_id =?) and scm_user_profile.customer_id=?");
    sql.param(customer.getId());
    sql.param(customer.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static List<UserProfile> companyEmployee(Main main, Company company) {
    SqlPage sql = getUserProfileSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_user_profile.company_id=? and scm_user_profile.customer_id is null and scm_user_profile.vendor_id is null and scm_user_profile.transporter_id is null and sales_agent_id is null");
    sql.param(company.getId());

    return AppService.listPagedJpa(main, sql);

  }

  public static List<UserProfile> listPagedNotInCompanyInvestor(Main main, Company company) {
    SqlPage sql = getUserProfileSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_user_profile.id not in(select scm_company_investor.user_profile_id from scm_company_investor scm_company_investor where scm_company_investor.company_id =?)and scm_user_profile.company_id=? and scm_user_profile.vendor_id is null and scm_user_profile.customer_id is null and scm_user_profile.transporter_id is null and scm_user_profiledesignation_id.designation_level=?");
    sql.param(company.getId());
    sql.param(company.getId());
    sql.param(TOP_MANAGEMENT_DESIGNATION_LEVEL_ID);
    return AppService.listPagedJpa(main, sql);
  }

  public static List<UserProfile> listPagedNotInCompanySalesAgent(Main main, Company company, AccountGroup accountGroup, Territory territory) {
    SqlPage sql = getUserProfileSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    String cond = "where scm_user_profile.id not in(select scm_sales_agent_contract.user_profile_id from scm_sales_agent_contract scm_sales_agent_contract where scm_sales_agent_contract.company_id =?) "
            + "and scm_user_profile.company_id=? and scm_user_profile.vendor_id is null and scm_user_profile.customer_id is null and scm_user_profile.transporter_id is null and scm_user_profiledesignation_id.id=?";
    sql.param(company.getId());
    sql.param(company.getId());
    sql.param(DESIGNATION_SALES_AGENT);
    if (accountGroup != null && accountGroup.getId() != null) {
      cond += " and scm_user_profile.id in (select sales_agent_id from scm_sales_agent_account_group where account_group_id = ? )";
      sql.param(accountGroup.getId());
    }
    if (territory != null && territory.getId() != null) {
      cond += " and scm_user_profile.id in (select sales_agent_id from scm_sales_agent_territory where territory_id = ? ) ";
      sql.param(territory.getId());
    }
    sql.cond(cond);
    return AppService.listPagedJpa(main, sql);
  }

  public static List<UserProfile> listPagedBySalesAgent(Main main, Company company) {
    main.clear();
    SqlPage sql = getUserProfileSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_user_profile.company_id=? and scm_user_profile.vendor_id is null and scm_user_profile.customer_id is null and scm_user_profile.transporter_id is null and scm_user_profiledesignation_id.id=?");
    //sql.param(company.getId());
    sql.param(company.getId());
    sql.param(DESIGNATION_SALES_AGENT);
    return AppService.listPagedJpa(main, sql);
  }
//
//  public static void insertArray1(Main main, UserProfile[] userProfileSelected, AccountGroup accountGroup) {
//    if (userProfileSelected != null) {
//      SalesAgentAccountGroup salesAgentAccountGroup;
//      for (UserProfile userProfile : userProfileSelected) {  //Reinserting
//        salesAgentAccountGroup = new SalesAgentAccountGroup();
//        salesAgentAccountGroup.setAccountGroupId(accountGroup);
//        salesAgentAccountGroup.setSalesAgentId(userProfile);
//        SalesAgentAccountGroupService.insert(main, salesAgentAccountGroup);
//      }
//    }
//  }
//
//  public static void insertArray2(Main main, UserProfile[] userProfileSelected, Territory territory) {
//    if (userProfileSelected != null) {
//      SalesAgentTerritory salesAgentTerritory;
//      for (UserProfile userProfile : userProfileSelected) {  //Reinserting
//        salesAgentTerritory = new SalesAgentTerritory();
//        salesAgentTerritory.setTerritoryId(territory);
//        salesAgentTerritory.setSalesAgentId(userProfile);
//        SalesAgentTerritoryService.insert(main, salesAgentTerritory);
//      }
//    }
//  }
//
//  public static void insertSalesAgentAccountGroup(Main main, UserProfile userProfile, List<AccountGroup> accountGroupList) {
//    if (accountGroupList != null && !accountGroupList.isEmpty()) {
//      if (userProfile.getId() != null) {
//        AppService.deleteSql(main, SalesAgentAccountGroup.class, "delete from scm_sales_agent_account_group where sales_agent_id = ?", new Object[]{userProfile.getId()});
//      }
//      SalesAgentAccountGroup salesAgentAccountGroup;
//      for (AccountGroup accountGroup : accountGroupList) {
//        salesAgentAccountGroup = new SalesAgentAccountGroup();
//        salesAgentAccountGroup.setAccountGroupId(accountGroup);
//        salesAgentAccountGroup.setSalesAgentId(userProfile);
//        SalesAgentAccountGroupService.insert(main, salesAgentAccountGroup);
//      }
//    }
//  }
//
//  public static void insertSalesAgentTerittory(Main main, UserProfile userProfile, List<Territory> territoryList) {
//    if (territoryList != null && !territoryList.isEmpty()) {
//      if (userProfile.getId() != null) {
//        AppService.deleteSql(main, SalesAgentTerritory.class, "delete from scm_sales_agent_territory where sales_agent_id = ?", new Object[]{userProfile.getId()});
//      }
//      SalesAgentTerritory salesAgentTerritory;
//      for (Territory territory : territoryList) {
//        salesAgentTerritory = new SalesAgentTerritory();
//        salesAgentTerritory.setTerritoryId(territory);
//        salesAgentTerritory.setSalesAgentId(userProfile);
//        SalesAgentTerritoryService.insert(main, salesAgentTerritory);
//      }
//    }
//  }

  public static long isSalesAgentExist(AppEm em, Integer salesAgentId, List<AccountGroup> accountGroupList) {
    StringBuilder sb = new StringBuilder();
    String whereCondition = "";
    for (AccountGroup agrp : accountGroupList) {
      if (sb.length() == 0) {
        sb.append(agrp.getId());
      } else {
        sb.append(",").append(agrp.getId());
      }
    }
    if (salesAgentId != null) {
      whereCondition = " sales_agent_id <> " + salesAgentId + " and ";
    }
    long count = em.count("select count(account_group_id) accountGroup_count from scm_sales_agent_account_group"
            + "where " + whereCondition + " sales_agent_id in ( "
            + "select sales_agent_id from scm_sales_agent_account_group where account_group_id in (" + sb.toString() + ") "
            + "GROUP by sales_agent_id "
            + "having count(sales_agent_id) = " + accountGroupList.size()
            + ") GROUP by sales_agent_id having count(account_group_id) = " + accountGroupList.size(), null);

    return count;
  }

  public static long isTerritoryExist(AppEm em, Integer salesAgentId, List<Territory> territoryList) {
    StringBuilder sb = new StringBuilder();
    String whereCondition = "";
    for (Territory terr : territoryList) {
      if (sb.length() == 0) {
        sb.append(terr.getId());
      } else {
        sb.append(",").append(terr.getId());
      }
    }
    if (salesAgentId != null) {
      whereCondition = " sales_agent_id <> " + salesAgentId + " and ";
    }
    long count = em.count("select count(territory_id) territory_count from scm_sales_agent_territory"
            + "where " + whereCondition + " sales_agent_id in ( "
            + "select sales_agent_id from scm_sales_agent_territory where territory_id in (" + sb.toString() + ") "
            + "GROUP by sales_agent_id "
            + "having count(sales_agent_id) = " + territoryList.size()
            + ") GROUP by sales_agent_id having count(territory_id)) = " + territoryList.size(), null);

    return count;
  }

  public static List<AccountGroup> selectAccountGroupBySalesAgent(MainView main, UserProfile userProfile) {
    return AppService.list(main, AccountGroup.class, "select id, group_name from scm_account_group where id in (select account_group_id from scm_sales_agent_account_group where sales_agent_id = ?)", new Object[]{userProfile.getId()});
  }

  public static List<Territory> selectTerritoryBySalesAgent(MainView main, UserProfile userProfile) {
    return AppService.list(main, Territory.class, "select id, territory_name from scm_territory where id in (select territory_id from scm_sales_agent_territory where sales_agent_id = ?)", new Object[]{userProfile.getId()});
  }

  public static List<AccountGroup> selectAgentAccountGroupByCompany(MainView main, Integer companyId) {
    String sql = "select t1.id,t1.group_name from scm_account_group t1 \n"
            + "left join scm_sales_agent_account_group t2 on t2.account_group_id = t1.id\n"
            + "left join scm_user_profile t3 on t3.id = t2.sales_agent_id \n"
            + "where t3.company_id = ? group by t1.id,t1.group_name";
    return AppService.list(main, AccountGroup.class, sql, new Object[]{companyId});
  }

  public static List<Territory> selectAgentTerritoryByCompany(Main main, Integer companyId) {
    String sql = "select t1.id,t1.territory_name from scm_territory t1 \n"
            + "left join scm_sales_agent_territory t2 on t2.territory_id = t1.id\n"
            + "left join scm_user_profile t3 on t3.id = t2.sales_agent_id \n"
            + "where t3.company_id = ? and t1.company_id =? group by t1.id,t1.territory_name";
    return AppService.list(main, Territory.class, sql, new Object[]{companyId, companyId});
  }

  public static UserProfile getUserProfileForSalesAgent(Main main, SalesAgent salesAgent) {
    return (UserProfile) AppService.single(main, UserProfile.class, "select * from scm_user_profile where sales_agent_id=?", new Object[]{salesAgent.getId()});
  }

  public static UserProfile getUserProfileForExternalUser(Main main, ExternalUser externalUser) {
    return (UserProfile) AppService.single(main, UserProfile.class, "select * from scm_user_profile where external_user_id=?", new Object[]{externalUser.getId()});
  }
}
