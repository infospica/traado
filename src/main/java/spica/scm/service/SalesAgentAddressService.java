/*
 * @(#)SalesAgentAddressService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.scm.service;

import java.util.List;
import spica.scm.domain.SalesAgent;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesAgentAddress;

/**
 * SalesAgentAddressService
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016 
 */

public abstract class SalesAgentAddressService {  
 
 /**
   * SalesAgentAddress paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentAddressSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_address", SalesAgentAddress.class, main);
    sql.main("select scm_sales_agent_address.id,scm_sales_agent_address.sales_agent_id,scm_sales_agent_address.address_type_id,scm_sales_agent_address.address,scm_sales_agent_address.country_id,scm_sales_agent_address.state_id,scm_sales_agent_address.district_id,scm_sales_agent_address.pin,scm_sales_agent_address.territory_id,scm_sales_agent_address.phone_1,scm_sales_agent_address.phone_2,scm_sales_agent_address.phone_3,scm_sales_agent_address.fax_1,scm_sales_agent_address.fax_2,scm_sales_agent_address.email,scm_sales_agent_address.sort_order,scm_sales_agent_address.status,scm_sales_agent_address.created_by,scm_sales_agent_address.modified_by,scm_sales_agent_address.created_at,scm_sales_agent_address.modified_at from scm_sales_agent_address scm_sales_agent_address "); //Main query
    sql.count("select count(scm_sales_agent_address.id) from scm_sales_agent_address scm_sales_agent_address "); //Count query
    sql.join("left outer join scm_sales_agent scm_sales_agent_addresssales_agent_id on (scm_sales_agent_addresssales_agent_id.id = scm_sales_agent_address.sales_agent_id) left outer join scm_address_type scm_sales_agent_addressaddress_type_id on (scm_sales_agent_addressaddress_type_id.id = scm_sales_agent_address.address_type_id) left outer join scm_country scm_sales_agent_addresscountry_id on (scm_sales_agent_addresscountry_id.id = scm_sales_agent_address.country_id) left outer join scm_state scm_sales_agent_addressstate_id on (scm_sales_agent_addressstate_id.id = scm_sales_agent_address.state_id) left outer join scm_district scm_sales_agent_addressdistrict_id on (scm_sales_agent_addressdistrict_id.id = scm_sales_agent_address.district_id)"); //Join Query

    sql.string(new String[]{"scm_sales_agent_addresssales_agent_id.name","scm_sales_agent_addressaddress_type_id.title","scm_sales_agent_address.address","scm_sales_agent_addresscountry_id.country_name","scm_sales_agent_addressstate_id.state_name","scm_sales_agent_addressdistrict_id.district_name","scm_sales_agent_address.pin","scm_sales_agent_address.phone_1","scm_sales_agent_address.phone_2","scm_sales_agent_address.phone_3","scm_sales_agent_address.fax_1","scm_sales_agent_address.fax_2","scm_sales_agent_address.email","scm_sales_agent_address.created_by","scm_sales_agent_address.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_agent_address.id","scm_sales_agent_address.territory_id","scm_sales_agent_address.sort_order","scm_sales_agent_address.status"}); //Number search or sort fields
    sql.date(new String[]{"scm_sales_agent_address.created_at","scm_sales_agent_address.modified_at"});  //Date search or sort fields
    return sql;
  }
	/**
   * Return all sales agent address of a sales agent.
   *
   * @param main
   * @param salesAgent
   * @return
   */
  public static final List<SalesAgentAddress> addressListBySalesAgent(Main main, SalesAgent salesAgent) {
    SqlPage sql = getSalesAgentAddressSqlPaged(main);
    if (salesAgent.getId() == null) {
      return null;
    }
    sql.cond("where scm_sales_agent_address.sales_agent_id=?");
    sql.param(salesAgent.getId());
    return AppService.listAllJpa(main, sql);
  }
 /**
  * Return List of SalesAgentAddress.
  * @param main
  * @return List of SalesAgentAddress
  */
  public static final List<SalesAgentAddress> listPaged(Main main) {
     return AppService.listPagedJpa(main, getSalesAgentAddressSqlPaged(main));
  }

//  /**
//   * Return list of SalesAgentAddress based on condition
//   * @param main
//   * @return List<SalesAgentAddress>
//   */
//  public static final List<SalesAgentAddress> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAgentAddressSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select SalesAgentAddress by key.
  * @param main
  * @param salesAgentAddress
  * @return SalesAgentAddress
  */
  public static final SalesAgentAddress selectByPk(Main main, SalesAgentAddress salesAgentAddress) {
    return (SalesAgentAddress) AppService.find(main, SalesAgentAddress.class, salesAgentAddress.getId());
  }

 /**
  * Insert SalesAgentAddress.
  * @param main
  * @param salesAgentAddress
  */
  public static final void insert(Main main, SalesAgentAddress salesAgentAddress) {
    AppService.insert(main, salesAgentAddress);
  }

 /**
  * Update SalesAgentAddress by key.
  * @param main
  * @param salesAgentAddress
  * @return SalesAgentAddress
  */
  public static final SalesAgentAddress updateByPk(Main main, SalesAgentAddress salesAgentAddress) {
    return (SalesAgentAddress) AppService.update(main, salesAgentAddress);
  }

  /**
   * Insert or update SalesAgentAddress
   *
   * @param main
   * @param salesAgentAddress
   */
  public static void insertOrUpdate(Main main, SalesAgentAddress salesAgentAddress) {
    if (salesAgentAddress.getId() == null) {
      insert(main, salesAgentAddress);
    } else {
    updateByPk(main, salesAgentAddress);
    }
  }
/**
 * Making default for newly added or updated if isdefault is checked  
 * @param main
 * @param salesAgentAddress 
 */
  public static void makeDefault(Main main, SalesAgentAddress salesAgentAddress) {
    if (salesAgentAddress.getSortOrder()==0) {
      main.param(1);
      main.param(salesAgentAddress.getSalesAgentId().getId());
      main.param(salesAgentAddress.getId());
      main.param(0);
      main.param(salesAgentAddress.getAddressTypeId().getId());
      AppService.updateSql(main, SalesAgentAddress.class, "update scm_sales_agent_address set modified_at=?,modified_by=?,sort_order=? where sales_agent_id=? and id !=? and sort_order=? and address_type_id=?", false);
    }
  }
  /**
   * Clone and existing object
   *
   * @param main
   * @param salesAgentAddress
   */
  public static void clone(Main main, SalesAgentAddress salesAgentAddress) {
    salesAgentAddress.setId(null); //Set to null for insert
    insert(main, salesAgentAddress);
  }

 /**
  * Delete SalesAgentAddress.
  * @param main
  * @param salesAgentAddress
  */
  public static final void deleteByPk(Main main, SalesAgentAddress salesAgentAddress) {
    AppService.delete(main, SalesAgentAddress.class, salesAgentAddress.getId());
  }
	
 /**
  * Delete Array of SalesAgentAddress.
  * @param main
  * @param salesAgentAddress
  */
  public static final void deleteByPkArray(Main main, SalesAgentAddress[] salesAgentAddress) {
    for (SalesAgentAddress e : salesAgentAddress) {
      deleteByPk(main, e);
    }
  }
}
