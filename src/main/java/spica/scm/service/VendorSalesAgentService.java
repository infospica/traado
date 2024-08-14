/*
 * @(#)VendorSalesAgentService.java	1.0 Fri Dec 23 10:28:18 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.VendorSalesAgent;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Vendor;
import spica.scm.validate.VendorSalesAgentIs;

/**
 * VendorSalesAgentService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:18 IST 2016
 */
public abstract class VendorSalesAgentService {

  /**
   * VendorSalesAgent paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getVendorSalesAgentSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_vendor_sales_agent", VendorSalesAgent.class, main);
    sql.main("select scm_vendor_sales_agent.id,scm_vendor_sales_agent.company_id,scm_vendor_sales_agent.vendor_id,scm_vendor_sales_agent.created_by,scm_vendor_sales_agent.modified_by,scm_vendor_sales_agent.created_at,scm_vendor_sales_agent.modifeid_at,scm_vendor_sales_agent.user_profile_id from scm_vendor_sales_agent scm_vendor_sales_agent "); //Main query
    sql.count("select count(scm_vendor_sales_agent.id) as total from scm_vendor_sales_agent scm_vendor_sales_agent "); //Count query
    sql.join("left outer join scm_company scm_vendor_sales_agentcompany_id on (scm_vendor_sales_agentcompany_id.id = scm_vendor_sales_agent.company_id) left outer join scm_user_profile scm_vendor_sales_agentuser_profile_id on (scm_vendor_sales_agentuser_profile_id.id = scm_vendor_sales_agent.company_id) left outer join scm_vendor scm_vendor_sales_agentvendor_id on (scm_vendor_sales_agentvendor_id.id = scm_vendor_sales_agent.vendor_id)"); //Join Query

    sql.string(new String[]{"scm_vendor_sales_agentcompany_id.company_name", "scm_vendor_sales_agent.created_by", "scm_vendor_sales_agent.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_vendor_sales_agent.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_vendor_sales_agent.created_at", "scm_vendor_sales_agent.modifeid_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of VendorSalesAgent.
   *
   * @param main
   * @return List of VendorSalesAgent
   */
  public static final List<VendorSalesAgent> listPaged(Main main) {
    return AppService.listPagedJpa(main, getVendorSalesAgentSqlPaged(main));
  }

//  /**
//   * Return list of VendorSalesAgent based on condition
//   * @param main
//   * @return List<VendorSalesAgent>
//   */
//  public static final List<VendorSalesAgent> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getVendorSalesAgentSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select VendorSalesAgent by key.
   *
   * @param main
   * @param vendorSalesAgent
   * @return VendorSalesAgent
   */
  public static final VendorSalesAgent selectByPk(Main main, VendorSalesAgent vendorSalesAgent) {
    return (VendorSalesAgent) AppService.find(main, VendorSalesAgent.class, vendorSalesAgent.getId());
  }

  /**
   * Insert VendorSalesAgent.
   *
   * @param main
   * @param vendorSalesAgent
   */
  public static final void insert(Main main, VendorSalesAgent vendorSalesAgent) {
    VendorSalesAgentIs.insertAble(main, vendorSalesAgent);  //Validating
    AppService.insert(main, vendorSalesAgent);

  }

  /**
   * Update VendorSalesAgent by key.
   *
   * @param main
   * @param vendorSalesAgent
   * @return VendorSalesAgent
   */
  public static final VendorSalesAgent updateByPk(Main main, VendorSalesAgent vendorSalesAgent) {
    VendorSalesAgentIs.updateAble(main, vendorSalesAgent); //Validating
    return (VendorSalesAgent) AppService.update(main, vendorSalesAgent);
  }

  /**
   * Insert or update VendorSalesAgent
   *
   * @param main
   * @param vendorSalesAgent
   */
  public static void insertOrUpdate(Main main, VendorSalesAgent vendorSalesAgent) {
    if (vendorSalesAgent.getId() == null) {
      insert(main, vendorSalesAgent);
    } else {
      updateByPk(main, vendorSalesAgent);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param vendorSalesAgent
   */
  public static void clone(Main main, VendorSalesAgent vendorSalesAgent) {
    vendorSalesAgent.setId(null); //Set to null for insert
    insert(main, vendorSalesAgent);
  }

  /**
   * Delete VendorSalesAgent.
   *
   * @param main
   * @param vendorSalesAgent
   */
  public static final void deleteByPk(Main main, VendorSalesAgent vendorSalesAgent) {
    VendorSalesAgentIs.deleteAble(main, vendorSalesAgent); //Validation
    AppService.delete(main, VendorSalesAgent.class, vendorSalesAgent.getId());
  }

  /**
   * Delete Array of VendorSalesAgent.
   *
   * @param main
   * @param vendorSalesAgent
   */
  public static final void deleteByPkArray(Main main, VendorSalesAgent[] vendorSalesAgent) {
    for (VendorSalesAgent e : vendorSalesAgent) {
      deleteByPk(main, e);
    }
  }

  public static final List<VendorSalesAgent> vendorSalesAgentContarctListByCompany(Main main, Vendor vendor) {
    SqlPage sql = getVendorSalesAgentSqlPaged(main);
    if (vendor.getId() == null) {
      return null;
    }
    sql.cond("where scm_vendor_sales_agent.company_id=? and scm_vendor_sales_agent.vendor_id");
//    sql.param(company.getId());
    sql.param(vendor.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static void insertArray(Main main, UserProfile[] userProfileSelected, Company company) {
    if (userProfileSelected != null) {
      VendorSalesAgent vendorSalesAgent;
      for (UserProfile userProfile : userProfileSelected) {  //Reinserting
        vendorSalesAgent = new VendorSalesAgent();
        vendorSalesAgent.setUserProfileId(userProfile);
//        vendorSalesAgent.setVendorId(vendorId);
        vendorSalesAgent.setCompanyId(company);
        insert(main, vendorSalesAgent);
      }
    }
  }

  public static final void deleteVendorSalesAgent(Main main, VendorSalesAgent vendorSalesAgent) {
    AppService.deleteSql(main, VendorSalesAgent.class, "delete from scm_vendor_sales_agent where id = ?", new Object[]{vendorSalesAgent.getId()});
  }
}
