/*
 * @(#)SalesAgentService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.service.LedgerExternalService;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.Designation;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.SalesAgentAccountGroup;
import spica.scm.domain.SalesAgentContract;
import spica.scm.domain.SalesAgentContractComm;
import spica.scm.domain.SalesAgentTerritory;
import spica.scm.domain.Territory;
import spica.scm.domain.UserProfile;
import spica.scm.validate.SalesAgentIs;
import wawo.app.faces.AppLookup;
import wawo.app.faces.MainView;
import wawo.entity.core.AppEm;
import wawo.entity.util.StringUtil;

/**
 * SalesAgentService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class SalesAgentService {

  /**
   * SalesAgent paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent", SalesAgent.class, main);
    sql.main("select scm_sales_agent.id,scm_sales_agent.company_id,scm_sales_agent.user_code,scm_sales_agent.department_id,scm_sales_agent.name,scm_sales_agent.address_1,scm_sales_agent.address_2,scm_sales_agent.phone_1,scm_sales_agent.phone_2,scm_sales_agent.email,scm_sales_agent.pan_no,scm_sales_agent.status_id,scm_sales_agent.created_by,scm_sales_agent.modified_by,scm_sales_agent.created_at,scm_sales_agent.modified_at from scm_sales_agent scm_sales_agent "); //Main query
    sql.count("select count(scm_sales_agent.id) as total from scm_sales_agent scm_sales_agent "); //Count query
    sql.join("join scm_company scm_sales_agentcompany_id on (scm_sales_agentcompany_id.id = scm_sales_agent.company_id) join scm_department scm_sales_agentdepartment_id on (scm_sales_agentdepartment_id.id = scm_sales_agent.department_id)   join scm_status scm_sales_agentstatus_id on (scm_sales_agentstatus_id.id = scm_sales_agent.status_id)"); //Join Query

    sql.string(new String[]{"scm_sales_agentcompany_id.company_name", "scm_sales_agent.user_code", "scm_sales_agent.name", "scm_sales_agent.address_1", "scm_sales_agent.address_2", "scm_sales_agent.phone_1", "scm_sales_agent.phone_2", "scm_sales_agent.email", "scm_sales_agent.pan_no", "scm_sales_agentstatus_id.title", "scm_sales_agent.created_by", "scm_sales_agent.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_agent.created_at", "scm_sales_agent.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgent.
   *
   * @param main
   * @param company
   * @return List of SalesAgent
   */
  public static List<SalesAgent> listPaged(MainView main, Company company) {
    SqlPage sql = getSalesAgentSqlPaged(main);
    sql.cond("where scm_sales_agent.company_id=?");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

  /**
   * Select SalesAgent by key.
   *
   * @param main
   * @param salesAgent
   * @return SalesAgent
   */
  public static final SalesAgent selectByPk(Main main, SalesAgent salesAgent) {
    return (SalesAgent) AppService.find(main, SalesAgent.class, salesAgent.getId());
  }

  /**
   * Insert SalesAgent.
   *
   * @param main
   * @param salesAgent
   */
  public static final void insert(Main main, SalesAgent salesAgent) {
    SalesAgentIs.insertAble(main, salesAgent);  //Validating
    AppService.insert(main, salesAgent);

  }

  /**
   * Update SalesAgent by key.
   *
   * @param main
   * @param salesAgent
   * @return SalesAgent
   */
  public static final SalesAgent updateByPk(Main main, SalesAgent salesAgent) {
    SalesAgentIs.updateAble(main, salesAgent); //Validating
    return (SalesAgent) AppService.update(main, salesAgent);
  }

  /**
   * Insert or update UserProfile
   *
   * @param main
   * @param userProfile
   */
  public static void insertOrUpdate(Main main, SalesAgent salesAgent, List<AccountGroup> agList, List<Territory> tList) {
    if (salesAgent.getId() == null) {
      //for clone
      if (!StringUtil.isEmpty(salesAgent.getSalesAgentContract())) {
        for (SalesAgentContract sac : salesAgent.getSalesAgentContract()) {
          sac.setId(null);
          if (!StringUtil.isEmpty(sac.getSalesAgentContractComm())) {
            for (SalesAgentContractComm cc : sac.getSalesAgentContractComm()) {
              cc.setId(null);
            }
          }
        }
      }
      insert(main, salesAgent);
    } else {
      updateByPk(main, salesAgent);
    }
    if (agList != null) {
      insertSalesAgentAccountGroup(main, salesAgent, agList);
    }
    if (tList != null) {
      insertSalesAgentTerittory(main, salesAgent, tList);
    }
    UserProfile profile = null;
    if (salesAgent != null && salesAgent.getId() != null) {
      profile = UserProfileService.getUserProfileForSalesAgent(main, salesAgent);
    }
    insertOrUpdateUserProfile(main, salesAgent, profile);
    LedgerExternalService.saveLedgerSalesAgent(main, salesAgent);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param userProfile
   */
  public static void clone(Main main, SalesAgent salesAgent, List<AccountGroup> agList, List<Territory> tList) {
    main.em().detach(salesAgent);
    salesAgent.setId(null); //Set to null for insert
    insertOrUpdate(main, salesAgent, agList, tList);
  }

  /**
   * Delete UserProfile.
   *
   * @param main
   * @param userProfile
   */
  public static final void deleteByPk(Main main, SalesAgent salesAgent) {
    SalesAgentIs.deleteAble(main, salesAgent); //Validation
    AppService.deleteSql(main, SalesAgentContractComm.class, "delete from scm_sales_agent_contract_comm t where t.sales_agent_contract_id in"
            + "(select id from scm_sales_agent_contract where sales_agent_id =?)", new Object[]{salesAgent.getId()});
    AppService.deleteSql(main, SalesAgentContract.class, "delete from scm_sales_agent_contract t where t.sales_agent_id=?", new Object[]{salesAgent.getId()});
    AppService.deleteSql(main, SalesAgentTerritory.class, "delete from scm_sales_agent_territory t where t.sales_agent_id=?", new Object[]{salesAgent.getId()});
    AppService.deleteSql(main, SalesAgentAccountGroup.class, "delete from scm_sales_agent_account_group t where t.sales_agent_id=?", new Object[]{salesAgent.getId()});
    AppService.deleteSql(main, UserProfile.class, "delete from scm_user_profile t where t.sales_agent_id=?", new Object[]{salesAgent.getId()});
    AppService.delete(main, SalesAgent.class, salesAgent.getId());
  }

  /**
   * Delete Array of UserProfile.
   *
   * @param main
   * @param userProfile
   */
  public static final void deleteByPkArray(Main main, SalesAgent[] salesAgents) {
    for (SalesAgent e : salesAgents) {
      deleteByPk(main, e);
    }
  }

  public static List<SalesAgent> listPagedNotInCompanyContact(Main main, Company company) {
    SqlPage sql = getSalesAgentSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_sales_agent.id not in(select scm_company_contact.user_profile_id from scm_company_contact scm_company_contact where scm_company_contact.company_id =?)and scm_user_profile.company_id=? and scm_user_profile.vendor_id is null and scm_user_profile.customer_id is null and scm_user_profile.transporter_id is null");
    sql.param(company.getId());
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static List<SalesAgent> listPagedByCompany(Main main, Company company, AccountGroup accountGroup, Territory territory) {
    SqlPage sql = getSalesAgentSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    String cond = " where scm_sales_agent.company_id=? ";
    sql.param(company.getId());
    if (accountGroup != null && accountGroup.getId() != null) {
      cond += " and scm_sales_agent.id in (select sales_agent_id from scm_sales_agent_account_group where account_group_id = ? )";
      sql.param(accountGroup.getId());
    }
    if (territory != null && territory.getId() != null) {
      cond += " and scm_sales_agent.id in (select sales_agent_id from scm_sales_agent_territory where territory_id = ? ) ";
      sql.param(territory.getId());
    }
    sql.cond(cond);
    return AppService.listPagedJpa(main, sql);
  }

  public static List<SalesAgent> listPagedBySalesAgent(Main main, Company company) {
    main.clear();
    SqlPage sql = getSalesAgentSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_sales_agent.company_id=? ");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static void insertArray1(Main main, SalesAgent[] salesAgentSelected, AccountGroup accountGroup) {
    if (salesAgentSelected != null) {
      SalesAgentAccountGroup salesAgentAccountGroup;
      for (SalesAgent salesAgent : salesAgentSelected) {  //Reinserting
        salesAgentAccountGroup = new SalesAgentAccountGroup();
        salesAgentAccountGroup.setAccountGroupId(accountGroup);
        salesAgentAccountGroup.setSalesAgentId(salesAgent);
        SalesAgentAccountGroupService.insert(main, salesAgentAccountGroup);
      }
    }
  }

  public static void insertArray2(Main main, SalesAgent[] salesAgentSelected, Territory territory) {
    if (salesAgentSelected != null) {
      SalesAgentTerritory salesAgentTerritory;
      for (SalesAgent salesAgent : salesAgentSelected) {  //Reinserting
        salesAgentTerritory = new SalesAgentTerritory();
        salesAgentTerritory.setTerritoryId(territory);
        salesAgentTerritory.setSalesAgentId(salesAgent);
        SalesAgentTerritoryService.insert(main, salesAgentTerritory);
      }
    }
  }

  public static void insertSalesAgentAccountGroup(Main main, SalesAgent salesAgent, List<AccountGroup> accountGroupList) {
    if (accountGroupList != null && !accountGroupList.isEmpty()) {
      if (salesAgent.getId() != null) {
        AppService.deleteSql(main, SalesAgentAccountGroup.class, "delete from scm_sales_agent_account_group where sales_agent_id = ?", new Object[]{salesAgent.getId()});
      }
      SalesAgentAccountGroup salesAgentAccountGroup;
      for (AccountGroup accountGroup : accountGroupList) {
        salesAgentAccountGroup = new SalesAgentAccountGroup();
        salesAgentAccountGroup.setAccountGroupId(accountGroup);
        salesAgentAccountGroup.setSalesAgentId(salesAgent);
        SalesAgentAccountGroupService.insert(main, salesAgentAccountGroup);
      }
    }
  }

  public static void insertOrUpdateUserProfile(Main main, SalesAgent salesAgent, UserProfile profile) {
    if (profile == null || (profile != null && profile.getId() == null)) {
      profile = new UserProfile();
    }
    profile.setSalesAgentId(salesAgent);
    profile.setAddress1(salesAgent.getAddress1());
    profile.setAddress2(salesAgent.getAddress2());
    profile.setCompanyId(salesAgent.getCompanyId());
    profile.setCreatedAt(salesAgent.getCreatedAt());
    profile.setCreatedBy(salesAgent.getCreatedBy());
    profile.setCurrencyId(salesAgent.getCurrencyId());
    profile.setDepartmentId(salesAgent.getDepartmentId());
    profile.setDesignationId(new Designation(UserProfileService.DESIGNATION_SALES_AGENT));
    profile.setEmail(salesAgent.getEmail());
    profile.setGstNo(salesAgent.getGstNo());
    profile.setName(salesAgent.getName());
    profile.setPanNo(salesAgent.getPanNo());
    profile.setPhone1(salesAgent.getPhone1());
    profile.setPhone2(salesAgent.getPhone2());
    profile.setStatusId(salesAgent.getStatusId());
    profile.setUserCode(salesAgent.getUserCode());
    UserProfileService.insertOrUpdateForSalesAgent(main, profile);

  }

  public static void insertSalesAgentTerittory(Main main, SalesAgent salesAgent, List<Territory> territoryList) {
    if (territoryList != null && !territoryList.isEmpty()) {
      if (salesAgent.getId() != null) {
        AppService.deleteSql(main, SalesAgentTerritory.class, "delete from scm_sales_agent_territory where sales_agent_id = ?", new Object[]{salesAgent.getId()});
      }
      SalesAgentTerritory salesAgentTerritory;
      for (Territory territory : territoryList) {
        salesAgentTerritory = new SalesAgentTerritory();
        salesAgentTerritory.setTerritoryId(territory);
        salesAgentTerritory.setSalesAgentId(salesAgent);
        SalesAgentTerritoryService.insert(main, salesAgentTerritory);
      }
    }
  }

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

  public static List<AccountGroup> selectAccountGroupBySalesAgent(MainView main, SalesAgent salesAgent) {
    return AppService.list(main, AccountGroup.class, "select id, group_name from scm_account_group where id in "
            + "(select account_group_id from scm_sales_agent_account_group where sales_agent_id = ?)", new Object[]{salesAgent.getId()});
  }

  public static List<Territory> selectTerritoryBySalesAgent(MainView main, SalesAgent salesAgent) {
    return AppService.list(main, Territory.class, "select id, territory_name from scm_territory where id in (select territory_id from "
            + "scm_sales_agent_territory where sales_agent_id = ?)", new Object[]{salesAgent.getId()});
  }

  public static List<AccountGroup> selectAgentAccountGroupByCompany(MainView main, Integer companyId) {
    String sql = "select t1.id,t1.group_name from scm_account_group t1 \n"
            + "left join scm_sales_agent_account_group t2 on t2.account_group_id = t1.id\n"
            + "left join scm_sales_agent t3 on t3.id = t2.sales_agent_id \n"
            + "where t3.company_id = ? group by t1.id,t1.group_name";
    return AppService.list(main, AccountGroup.class, sql, new Object[]{companyId});
  }

  public static List<Territory> selectAgentTerritoryByCompany(Main main, Integer companyId) {
    String sql = "select t1.id,t1.territory_name from scm_territory t1 \n"
            + "left join scm_sales_agent_territory t2 on t2.territory_id = t1.id\n"
            + "left join scm_sales_agent t3 on t3.id = t2.sales_agent_id \n"
            + "where t3.company_id = ? and t1.company_id =? group by t1.id,t1.territory_name";
    return AppService.list(main, Territory.class, sql, new Object[]{companyId, companyId});
  }

  public static List<SalesAgent> salesAgentsAll(Main main, Company company, String filter) {
    List<SalesAgent> list = salesAgents(main, company, filter);
    if (list != null || list.size() > 0) {
      list.add(0, new SalesAgent(0, "All"));
    }
    return list;
  }

  private static List<SalesAgent> salesAgents(Main main, Company company, String filter) {
    if (company.getId() == null) {
      return null;
    }
    String sql = "select scm_sales_agent.id,scm_sales_agent.company_id,\n"
            + "scm_sales_agent.user_code,scm_sales_agent.department_id,scm_sales_agent.name,scm_sales_agent.address_1,scm_sales_agent.address_2,scm_sales_agent.phone_1,\n"
            + "scm_sales_agent.phone_2,scm_sales_agent.email,scm_sales_agent.pan_no,scm_sales_agent.status_id,scm_sales_agent.created_by,scm_sales_agent.modified_by,\n"
            + "scm_sales_agent.created_at,scm_sales_agent.modified_at from scm_sales_agent scm_sales_agent  "
            + "left outer join scm_company scm_sales_agentcompany_id on (scm_sales_agentcompany_id.id = scm_sales_agent.company_id) \n"
            + " where  scm_sales_agent.company_id=? \n"
            + "and upper(scm_sales_agent.name) like ?\n"
            + "ORDER BY scm_sales_agent.user_code";

    return main.em().list(SalesAgent.class, sql, new Object[]{company.getId(), "%" + filter.toUpperCase() + "%"});

  }

  public static final List<SalesAgent> selectSalesAgentbyCustomer(String filter, Company company, AccountGroup accountGroupId, Customer customer) {
    String sql = "select * from scm_sales_agent scm_sales_agent "
            + "where scm_sales_agent.company_id=? and scm_sales_agent.id in(select sales_agent_id from scm_sales_agent_account_group where account_group_id = ?)"
            + "and scm_sales_agent.id in(select sales_agent_id from scm_sales_agent_territory where territory_id in\n"
            + "(select territory_id from scm_territory_district where district_id in(select district_id from scm_customer_address where customer_id = ?))) "
            + "and status_id=1";
    if (!StringUtil.isEmpty(filter)) {
      sql += " AND upper(scm_sales_agent.name) LIKE ? ";
      return AppLookup.getAutoFilter(SalesAgent.class, sql, new Object[]{company.getId(), accountGroupId, customer.getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(SalesAgent.class, sql, new Object[]{company.getId(), accountGroupId.getId(), customer.getId()});
  }

  public static List<UserProfile> selectSalesAgentFromSalesInvoice(Main main, Company company, String filter) {
    if (company.getId() == null) {
      return null;
    }
    String sql = "select T2.name ,T2.id from scm_sales_agent T2 where T2.id in \n"
            + "( select T1.company_sales_agent_person_profile_id from scm_sales_invoice T1,fin_accounting_transaction T3 \n"
            + " where T1.id = T3.entity_id \n"
            + " and T3.accounting_entity_type_id=? \n"
            + " AND T3.company_id = ? AND T1.company_sales_agent_person_profile_id is not null ) AND upper(T2.name) like ? order by T2.name  ASC";
    return main.em().list(UserProfile.class, sql, new Object[]{AccountingConstant.ACC_ENTITY_SALES.getId(), company.getId(), "%" + filter.toUpperCase() + "%"});
  }

}
