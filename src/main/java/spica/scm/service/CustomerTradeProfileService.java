/*
 * @(#)CustomerTradeProfileService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CustomerTradeProfile;
import spica.scm.validate.CustomerTradeProfileIs;

/**
 * CustomerTradeProfileService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class CustomerTradeProfileService {

  /**
   * CustomerTradeProfile paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCustomerTradeProfileSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_customer_trade_profile", CustomerTradeProfile.class, main);
    sql.main("select scm_customer_trade_profile.id,scm_customer_trade_profile.customer_id,scm_customer_trade_profile.trade_profile_id from scm_customer_trade_profile scm_customer_trade_profile "); //Main query
    sql.count("select count(scm_customer_trade_profile.id) as total from scm_customer_trade_profile scm_customer_trade_profile "); //Count query
    sql.join("left outer join scm_customer scm_customer_trade_profilecustomer_id on (scm_customer_trade_profilecustomer_id.id = scm_customer_trade_profile.customer_id) left outer join scm_trade_profile scm_customer_trade_profiletrade_profile_id on (scm_customer_trade_profiletrade_profile_id.id = scm_customer_trade_profile.trade_profile_id)"); //Join Query

    sql.string(new String[]{"scm_customer_trade_profilecustomer_id.customer_name", "scm_customer_trade_profiletrade_profile_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_customer_trade_profile.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CustomerTradeProfile.
   *
   * @param main
   * @return List of CustomerTradeProfile
   */
  public static final List<CustomerTradeProfile> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCustomerTradeProfileSqlPaged(main));
  }

//  /**
//   * Return list of CustomerTradeProfile based on condition
//   * @param main
//   * @return List<CustomerTradeProfile>
//   */
//  public static final List<CustomerTradeProfile> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCustomerTradeProfileSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select CustomerTradeProfile by key.
   *
   * @param main
   * @param customerTradeProfile
   * @return CustomerTradeProfile
   */
  public static final CustomerTradeProfile selectByPk(Main main, CustomerTradeProfile customerTradeProfile) {
    return (CustomerTradeProfile) AppService.find(main, CustomerTradeProfile.class, customerTradeProfile.getId());
  }

  /**
   * Insert CustomerTradeProfile.
   *
   * @param main
   * @param customerTradeProfile
   */
  public static final void insert(Main main, CustomerTradeProfile customerTradeProfile) {
    CustomerTradeProfileIs.insertAble(main, customerTradeProfile);  //Validating
    AppService.insert(main, customerTradeProfile);

  }

  /**
   * Update CustomerTradeProfile by key.
   *
   * @param main
   * @param customerTradeProfile
   * @return CustomerTradeProfile
   */
  public static final CustomerTradeProfile updateByPk(Main main, CustomerTradeProfile customerTradeProfile) {
    CustomerTradeProfileIs.updateAble(main, customerTradeProfile); //Validating
    return (CustomerTradeProfile) AppService.update(main, customerTradeProfile);
  }

  /**
   * Insert or update CustomerTradeProfile
   *
   * @param main
   * @param customerTradeProfile
   */
  public static void insertOrUpdate(Main main, CustomerTradeProfile customerTradeProfile) {
    if (customerTradeProfile.getId() == null) {
      insert(main, customerTradeProfile);
    } else {
      updateByPk(main, customerTradeProfile);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param customerTradeProfile
   */
  public static void clone(Main main, CustomerTradeProfile customerTradeProfile) {
    customerTradeProfile.setId(null); //Set to null for insert
    insert(main, customerTradeProfile);
  }

  /**
   * Delete CustomerTradeProfile.
   *
   * @param main
   * @param customerTradeProfile
   */
  public static final void deleteByPk(Main main, CustomerTradeProfile customerTradeProfile) {
    CustomerTradeProfileIs.deleteAble(main, customerTradeProfile); //Validation
    AppService.delete(main, CustomerTradeProfile.class, customerTradeProfile.getId());
  }

  /**
   * Delete Array of CustomerTradeProfile.
   *
   * @param main
   * @param customerTradeProfile
   */
  public static final void deleteByPkArray(Main main, CustomerTradeProfile[] customerTradeProfile) {
    for (CustomerTradeProfile e : customerTradeProfile) {
      deleteByPk(main, e);
    }
  }
}
