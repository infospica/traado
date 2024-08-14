/*
 * @(#)SalesAccountService.java	1.0 Fri Dec 23 10:28:18 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Customer;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesAccount;
import spica.scm.validate.SalesAccountIs;
import wawo.app.faces.MainView;

/**
 * SalesAccountService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:18 IST 2016
 */
public abstract class SalesAccountService {

  /**
   * SalesAccount paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAccountSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_account", SalesAccount.class, main);
    sql.main("select scm_sales_account.id,scm_sales_account.company_id,scm_sales_account.customer_id,scm_sales_account.account_group_price_list_id,scm_sales_account.account_group_id,scm_sales_account.customer_trade_profile_id,scm_sales_account.sales_credit_amount,scm_sales_account.sales_credit_days,scm_sales_account.outstanding_bill_limit_cnt,scm_sales_account.outstanding_bill_limit_amt,scm_sales_account.created_by,scm_sales_account.modified_by,scm_sales_account.created_at,scm_sales_account.modified_at from scm_sales_account scm_sales_account "); //Main query
    sql.count("select count(scm_sales_account.id) as total from scm_sales_account scm_sales_account "); //Count query
    sql.join("left outer join scm_company scm_sales_accountcompany_id on (scm_sales_accountcompany_id.id = scm_sales_account.company_id) "
            + "left outer join scm_customer scm_sales_accountcustomer_id on (scm_sales_accountcustomer_id.id = scm_sales_account.customer_id) "
            + "left outer join scm_account_group_price_list scm_account_group_price_list on (scm_account_group_price_list.id = scm_sales_account.account_group_price_list_id) "
            + "left outer join scm_account_group scm_sales_accountaccount_group_id on (scm_sales_accountaccount_group_id.id = scm_sales_account.account_group_id)"); //Join Query

    sql.string(new String[]{"scm_sales_accountcompany_id.company_name", "scm_sales_accountcustomer_id.customer_name", "scm_sales_accountaccount_group_id.group_name", "scm_sales_account.created_by", "scm_sales_account.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_account.id", "scm_sales_account.customer_trade_profile_id", "scm_sales_account.sales_credit_amount", "scm_sales_account.sales_credit_days", "scm_sales_account.outstanding_bill_limit_cnt", "scm_sales_account.outstanding_bill_limit_amt"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_account.created_at", "scm_sales_account.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAccount.
   *
   * @param main
   * @return List of SalesAccount
   */
  public static final List<SalesAccount> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesAccountSqlPaged(main));
  }

//  /**
//   * Return list of SalesAccount based on condition
//   * @param main
//   * @return List<SalesAccount>
//   */
//  public static final List<SalesAccount> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesAccountSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesAccount by key.
   *
   * @param main
   * @param salesAccount
   * @return SalesAccount
   */
  public static final SalesAccount selectByPk(Main main, SalesAccount salesAccount) {
    return (SalesAccount) AppService.find(main, SalesAccount.class, salesAccount.getId());
  }

  /**
   * Insert SalesAccount.
   *
   * @param main
   * @param salesAccount
   */
  public static final void insert(Main main, SalesAccount salesAccount) {
    SalesAccountIs.insertAble(main, salesAccount);  //Validating
    AppService.insert(main, salesAccount);

  }

  /**
   * Update SalesAccount by key.
   *
   * @param main
   * @param salesAccount
   * @return SalesAccount
   */
  public static final SalesAccount updateByPk(Main main, SalesAccount salesAccount) {
    SalesAccountIs.updateAble(main, salesAccount); //Validating
    return (SalesAccount) AppService.update(main, salesAccount);
  }

  /**
   * Insert or update SalesAccount
   *
   * @param main
   * @param salesAccount
   */
  public static void insertOrUpdate(Main main, SalesAccount salesAccount) {
    if (salesAccount.getId() == null) {
      insert(main, salesAccount);
    } else {
      updateByPk(main, salesAccount);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesAccount
   */
  public static void clone(Main main, SalesAccount salesAccount) {
    salesAccount.setId(null); //Set to null for insert
    insert(main, salesAccount);
  }

  /**
   * Delete SalesAccount.
   *
   * @param main
   * @param salesAccount
   */
  public static final void deleteByPk(Main main, SalesAccount salesAccount) {
    SalesAccountIs.deleteAble(main, salesAccount); //Validation
    AppService.delete(main, SalesAccount.class, salesAccount.getId());
  }

  /**
   * Delete Array of SalesAccount.
   *
   * @param main
   * @param salesAccount
   */
  public static final void deleteByPkArray(Main main, SalesAccount[] salesAccount) {
    for (SalesAccount e : salesAccount) {
      deleteByPk(main, e);
    }
  }

  public static final List<SalesAccount> getSalesAccountList(Main main, Integer customerId, Integer companyId) {
    SqlPage sql = getSalesAccountSqlPaged(main);
    if (customerId == null) {
      return null;
    }
    sql.cond("where scm_sales_account.customer_id=? and scm_sales_account.company_id=?");
    sql.param(customerId);
    sql.param(companyId);
    return AppService.listAllJpa(main, sql);
  }

  public static void deleteSalesAccount(MainView main, SalesAccount salesAccount) {
    AppService.deleteSql(main, SalesAccount.class, "delete from scm_sales_account where scm_sales_account.account_group_id "
            + "not in(select scm_sales_invoice.account_group_id from scm_sales_invoice) "
            + "and scm_sales_account.id not in(select scm_sales_req.sales_account_id from scm_sales_req) and scm_sales_account.id=?", new Object[]{salesAccount.getId()});
  }

  public static final SalesAccount selectSalesAccountByCustomerAccountGroup(Main main, Customer customer, AccountGroup accountGroup) {
    return (SalesAccount) AppService.single(main, SalesAccount.class, "select * from scm_sales_account where customer_id = ? and account_group_id = ?", new Object[]{customer.getId(), accountGroup.getId()});
  }
}
