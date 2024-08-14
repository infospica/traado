/*
 * @(#)CustomerBankContactService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.CustomerBank;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CustomerBankContact;
import spica.scm.validate.CustomerBankContactIs;

/**
 * CustomerBankContactService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CustomerBankContactService {

  /**
   * CustomerBankContact paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCustomerBankContactSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_customer_bank_contact", CustomerBankContact.class, main);
    sql.main("select scm_customer_bank_contact.id,scm_customer_bank_contact.customer_bank_ac_id,scm_customer_bank_contact.contact_name,scm_customer_bank_contact.designation_id,scm_customer_bank_contact.phone_1,scm_customer_bank_contact.phone_2,scm_customer_bank_contact.fax_1,scm_customer_bank_contact.email,scm_customer_bank_contact.sort_order,scm_customer_bank_contact.created_by,scm_customer_bank_contact.modified_by,scm_customer_bank_contact.created_at,scm_customer_bank_contact.modified_at,scm_customer_bank_contact.status_id from scm_customer_bank_contact scm_customer_bank_contact "); //Main query
    sql.count("select count(scm_customer_bank_contact.id) from scm_customer_bank_contact scm_customer_bank_contact "); //Count query
    sql.join("left outer join scm_customer_bank scm_customer_bank_contactcustomer_bank_ac_id on (scm_customer_bank_contactcustomer_bank_ac_id.id = scm_customer_bank_contact.customer_bank_ac_id) left outer join scm_designation scm_customer_bank_contactdesignation_id on (scm_customer_bank_contactdesignation_id.id = scm_customer_bank_contact.designation_id) left outer join scm_status scm_customer_bank_contactstatus_id on (scm_customer_bank_contactstatus_id.id = scm_customer_bank_contact.status_id)"); //Join Query

    sql.string(new String[]{"scm_customer_bank_contactcustomer_bank_ac_id.account_name", "scm_customer_bank_contact.contact_name", "scm_customer_bank_contactdesignation_id.title", "scm_customer_bank_contact.phone_1", "scm_customer_bank_contact.phone_2", "scm_customer_bank_contact.fax_1", "scm_customer_bank_contact.email", "scm_customer_bank_contact.created_by", "scm_customer_bank_contact.modified_by", "scm_customer_bank_contactstatus_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_customer_bank_contact.id", "scm_customer_bank_contact.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_customer_bank_contact.created_at", "scm_customer_bank_contact.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CustomerBankContact.
   *
   * @param main
   * @param parent
   * @return List of CustomerBankContact
   */
  public static final List<CustomerBankContact> listPaged(Main main, CustomerBank parent) {
    SqlPage sql = getCustomerBankContactSqlPaged(main);
    if (parent.getId() == null) {
      return null;
    }
    sql.cond("where scm_customer_bank_contact.customer_bank_ac_id=?");
    sql.param(parent.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of CustomerBankContact based on condition
//   * @param main
//   * @return List<CustomerBankContact>
//   */
//  public static final List<CustomerBankContact> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCustomerBankContactSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select CustomerBankContact by key.
   *
   * @param main
   * @param customerBankContact
   * @return CustomerBankContact
   */
  public static final CustomerBankContact selectByPk(Main main, CustomerBankContact customerBankContact) {
    return (CustomerBankContact) AppService.find(main, CustomerBankContact.class, customerBankContact.getId());
  }

  /**
   * Insert CustomerBankContact.
   *
   * @param main
   * @param customerBankContact
   */
  public static final void insert(Main main, CustomerBankContact customerBankContact) {
    CustomerBankContactIs.insertAble(main, customerBankContact);  //Validating
    AppService.insert(main, customerBankContact);

  }

  /**
   * Update CustomerBankContact by key.
   *
   * @param main
   * @param customerBankContact
   * @return CustomerBankContact
   */
  public static final CustomerBankContact updateByPk(Main main, CustomerBankContact customerBankContact) {
    CustomerBankContactIs.updateAble(main, customerBankContact); //Validating
    return (CustomerBankContact) AppService.update(main, customerBankContact);
  }

  /**
   * Insert or update CustomerBankContact
   *
   * @param main
   * @param customerBankContact
   */
  public static void insertOrUpdate(Main main, CustomerBankContact customerBankContact) {
    if (customerBankContact.getId() == null) {
      insert(main, customerBankContact);
    } else {
      updateByPk(main, customerBankContact);
    }
  }

  /**
   * Making default for newly added or updated if IsDefault is checked
   *
   * @param main
   * @param customerBankContact
   */
  public static void makeDefault(Main main, CustomerBankContact customerBankContact) {
    if (customerBankContact.getSortOrder() == 0) {
      main.param(1);
      main.param(customerBankContact.getCustomerBankAcId().getId());
      main.param(customerBankContact.getId());
      main.param(0);
      AppService.updateSql(main, CustomerBankContact.class, "update scm_customer_bank_contact set modified_by=?,modified_at=?,sort_order=? where customer_bank_ac_id=? and id !=? and sort_order=?", true);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param customerBankContact
   */
  public static void clone(Main main, CustomerBankContact customerBankContact) {
    customerBankContact.setId(null); //Set to null for insert
    insert(main, customerBankContact);
  }

  /**
   * Delete CustomerBankContact.
   *
   * @param main
   * @param customerBankContact
   */
  public static final void deleteByPk(Main main, CustomerBankContact customerBankContact) {
    CustomerBankContactIs.deleteAble(main, customerBankContact); //Validation
    AppService.delete(main, CustomerBankContact.class, customerBankContact.getId());
  }

  /**
   * Delete Array of CustomerBankContact.
   *
   * @param main
   * @param customerBankContact
   */
  public static final void deleteByPkArray(Main main, CustomerBankContact[] customerBankContact) {
    for (CustomerBankContact e : customerBankContact) {
      deleteByPk(main, e);
    }
  }
}
