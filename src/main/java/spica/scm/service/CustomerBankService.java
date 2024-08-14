/*
 * @(#)CustomerBankService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.scm.service;

import java.util.List;
import spica.scm.domain.Customer;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CustomerBank;
import spica.scm.domain.CustomerBankContact;
import spica.scm.validate.CustomerBankIs;

/**
 * CustomerBankService
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016 
 */

public abstract class CustomerBankService {  
 
 /**
   * CustomerBank paginated query.
   *
   * @param main
   * @return SqlPage
   */
   private static SqlPage getCustomerBankSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_customer_bank", CustomerBank.class, main);
    sql.main("select scm_customer_bank.id,scm_customer_bank.customer_id,scm_customer_bank.bank_account_type_id,scm_customer_bank.account_name,scm_customer_bank.account_no,scm_customer_bank.bank_id,scm_customer_bank.branch_name,scm_customer_bank.ifsc_code,scm_customer_bank.branch_address,scm_customer_bank.sort_order,scm_customer_bank.created_by,scm_customer_bank.modified_by,scm_customer_bank.created_at,scm_customer_bank.modified_at,scm_customer_bank.branch_code,scm_customer_bank.status_id from scm_customer_bank scm_customer_bank "); //Main query
    sql.count("select count(scm_customer_bank.id) from scm_customer_bank scm_customer_bank "); //Count query
    sql.join("left outer join scm_customer scm_customer_bankcustomer_id on (scm_customer_bankcustomer_id.id = scm_customer_bank.customer_id) left outer join scm_bank_account_type scm_customer_bankbank_account_type_id on (scm_customer_bankbank_account_type_id.id = scm_customer_bank.bank_account_type_id) left outer join scm_bank scm_customer_bankbank_id on (scm_customer_bankbank_id.id = scm_customer_bank.bank_id) left outer join scm_status scm_customer_bankstatus_id on (scm_customer_bankstatus_id.id = scm_customer_bank.status_id)"); //Join Query

    sql.string(new String[]{"scm_customer_bankcustomer_id.customer_name","scm_customer_bankbank_account_type_id.title","scm_customer_bank.account_name","scm_customer_bank.account_no","scm_customer_bankbank_id.name","scm_customer_bank.branch_name","scm_customer_bank.ifsc_code","scm_customer_bank.branch_address","scm_customer_bank.created_by","scm_customer_bank.modified_by","scm_customer_bank.branch_code","scm_customer_bank.contact_no","scm_customer_bank.email","scm_customer_bankstatus_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_customer_bank.id","scm_customer_bank.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_customer_bank.created_at","scm_customer_bank.modified_at"});  //Date search or sort fields
    return sql;
  }
	/**
   * Return all customer address of a customer.
   *
   * @param main
   * @param customer
   * @return
   */
  public static final List<CustomerBank> bankListByCustomer(Main main, Customer customer) {
    SqlPage sql = getCustomerBankSqlPaged(main);
    if (customer.getId() == null) {
      return null;
    }
    sql.cond("where scm_customer_bank.customer_id=?");
    sql.param(customer.getId());
    return AppService.listAllJpa(main, sql);
  }
 /**
  * Return List of CustomerBank.
  * @param main
  * @return List of CustomerBank
  */
  public static final List<CustomerBank> listPaged(Main main) {
     return AppService.listPagedJpa(main, getCustomerBankSqlPaged(main));
  }

//  /**
//   * Return list of CustomerBank based on condition
//   * @param main
//   * @return List<CustomerBank>
//   */
//  public static final List<CustomerBank> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCustomerBankSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select CustomerBank by key.
  * @param main
  * @param customerBank
  * @return CustomerBank
  */
  public static final CustomerBank selectByPk(Main main, CustomerBank customerBank) {
    return (CustomerBank) AppService.find(main, CustomerBank.class, customerBank.getId());
  }

 /**
  * Insert CustomerBank.
  * @param main
  * @param customerBank
  */
  public static final void insert(Main main, CustomerBank customerBank) {
    CustomerBankIs.insertAble(main, customerBank);  //Validating
    AppService.insert(main, customerBank);

  }

 /**
  * Update CustomerBank by key.
  * @param main
  * @param customerBank
  * @return CustomerBank
  */
  public static final CustomerBank updateByPk(Main main, CustomerBank customerBank) {
    CustomerBankIs.updateAble(main, customerBank); //Validating
    return (CustomerBank) AppService.update(main, customerBank);
  }

  /**
   * Insert or update CustomerBank
   *
   * @param main
   * @param customerBank
   */
  public static void insertOrUpdate(Main main, CustomerBank customerBank) {
    if (customerBank.getId() == null) {
      insert(main, customerBank);
    } else {
    updateByPk(main, customerBank);
    }
  }
/**
   * Making default for newly added or updated if isdefault is checked
   *
   * @param main
   * @param customerBank
   */
  public static void makeDefault(Main main, CustomerBank customerBank) {
    if (customerBank.getSortOrder()== 0) {
      main.param(1);
      main.param(customerBank.getCustomerId().getId());
      main.param(customerBank.getId());
      main.param(0);
      AppService.updateSql(main, CustomerBank.class, "update scm_customer_bank set modified_by=?,modified_at=?,sort_order=? where customer_id=? and id !=? and sort_order=?", true);
    }
  }
  /**
   * Clone and existing object
   *
   * @param main
   * @param customerBank
   */
  public static void clone(Main main, CustomerBank customerBank) {
    customerBank.setId(null); //Set to null for insert
    insert(main, customerBank);
  }

 /**
  * Delete CustomerBank.
  * @param main
  * @param customerBank
  */
  public static final void deleteByPk(Main main, CustomerBank customerBank) {
    CustomerBankIs.deleteAble(main, customerBank); //Validation
    AppService.deleteSql(main, CustomerBankContact.class, "delete from scm_customer_bank_contact scm_customer_bank_contact where scm_customer_bank_contact.customer_bank_ac_id=?", new Object[]{customerBank.getId()});
    AppService.delete(main, CustomerBank.class, customerBank.getId());
  }
	
 /**
  * Delete Array of CustomerBank.
  * @param main
  * @param customerBank
  */
  public static final void deleteByPkArray(Main main, CustomerBank[] customerBank) {
    for (CustomerBank e : customerBank) {
      deleteByPk(main, e);
    }
  }
}
