/*
 * @(#)TradeProfileService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerTradeProfile;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.TradeProfile;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * TradeProfileService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TradeProfileService {

  public static final int TRADE_TYPE_MARKETER = 1;
  public static final int TRADE_TYPE_SS = 4;
  public static final int TRADE_TYPE_CSA = 2;
  public static final int TRADE_TYPE_CF = 3;
  public static final int TRADE_TYPE_CONSUMER = 8;

  /**
   * TradeProfile paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTradeProfileSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_trade_profile", TradeProfile.class, main);
    sql.main("select scm_trade_profile.id,scm_trade_profile.title,scm_trade_profile.trade_level,scm_trade_profile.sort_order,scm_trade_profile.created_by,scm_trade_profile.modified_by,scm_trade_profile.created_at,scm_trade_profile.modified_at from scm_trade_profile scm_trade_profile"); //Main query
    sql.count("select count(scm_trade_profile.id) as total from scm_trade_profile scm_trade_profile"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_trade_profile.title", "scm_trade_profile.created_by", "scm_trade_profile.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_trade_profile.id", "scm_trade_profile.trade_level", "scm_trade_profile.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_trade_profile.created_at", "scm_trade_profile.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TradeProfile.
   *
   * @param main
   * @return List of TradeProfile
   */
  public static final List<TradeProfile> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTradeProfileSqlPaged(main));
  }

//  /**
//   * Return list of TradeProfile based on condition
//   * @param main
//   * @return List<TradeProfile>
//   */
//  public static final List<TradeProfile> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTradeProfileSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TradeProfile by key.
   *
   * @param main
   * @param tradeProfile
   * @return TradeProfile
   */
  public static final TradeProfile selectByPk(Main main, TradeProfile tradeProfile) {
    return (TradeProfile) AppService.find(main, TradeProfile.class, tradeProfile.getId());
  }

  /**
   * Insert TradeProfile.
   *
   * @param main
   * @param tradeProfile
   */
  public static final void insert(Main main, TradeProfile tradeProfile) {
    insertAble(main, tradeProfile);  //Validating
    AppService.insert(main, tradeProfile);

  }

  /**
   * Update TradeProfile by key.
   *
   * @param main
   * @param tradeProfile
   * @return TradeProfile
   */
  public static final TradeProfile updateByPk(Main main, TradeProfile tradeProfile) {
    updateAble(main, tradeProfile); //Validating
    return (TradeProfile) AppService.update(main, tradeProfile);
  }

  /**
   * Insert or update TradeProfile
   *
   * @param main
   * @param tradeProfile
   */
  public static void insertOrUpdate(Main main, TradeProfile tradeProfile) {
    if (tradeProfile.getId() == null) {
      insert(main, tradeProfile);
    } else {
      updateByPk(main, tradeProfile);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param tradeProfile
   */
  public static void clone(Main main, TradeProfile tradeProfile) {
    tradeProfile.setId(null); //Set to null for insert
    insert(main, tradeProfile);
  }

  /**
   * Delete TradeProfile.
   *
   * @param main
   * @param tradeProfile
   */
  public static final void deleteByPk(Main main, TradeProfile tradeProfile) {
    deleteAble(main, tradeProfile); //Validation
    AppService.delete(main, TradeProfile.class, tradeProfile.getId());
  }

  /**
   * Delete Array of TradeProfile.
   *
   * @param main
   * @param tradeProfile
   */
  public static final void deleteByPkArray(Main main, TradeProfile[] tradeProfile) {
    for (TradeProfile e : tradeProfile) {
      deleteByPk(main, e);
    }
  }

  public static List<TradeProfile> selectByCustomer(Main main, Customer customer) {
    SqlPage sql = getTradeProfileSqlPaged(main);
    if (customer.getId() == null) {
      return null;
    }
    sql.join("left outer join scm_customer_trade_profile scm_customer_trade_profilecustomer_id on (scm_customer_trade_profilecustomer_id.trade_profile_id = scm_trade_profile.id) where scm_customer_trade_profilecustomer_id.customer_id=?");
    sql.param(customer.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static TradeProfile selectTradeProfileByCustomer(Main main, Customer customer) {
    TradeProfile tradeProfile = null;
    tradeProfile = (TradeProfile) AppService.single(main, TradeProfile.class, "select * from scm_trade_profile where id in(select trade_profile_id from scm_customer_trade_profile where customer_id =?)",
            new Object[]{customer.getId()});
    return tradeProfile;
  }

  public static List<CustomerTradeProfile> customerTradeProfile(Main main, Customer customer) {
    List<CustomerTradeProfile> tradeProfileList = main.em().list(CustomerTradeProfile.class, "select * from scm_customer_trade_profile scm_customer_trade_profile where scm_customer_trade_profile.customer_id=?", new Object[]{customer.getId()});
    return tradeProfileList;
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param tradeProfile
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, TradeProfile tradeProfile) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param tradeProfile
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, TradeProfile tradeProfile) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_trade_profile where upper(title)=?", new Object[]{StringUtil.toUpperCase(tradeProfile.getTitle())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param tradeProfile
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, TradeProfile tradeProfile) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_trade_profile where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(tradeProfile.getTitle()), tradeProfile.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }
}
