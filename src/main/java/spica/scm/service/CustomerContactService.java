/*
 * @(#)CustomerContactService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
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
import spica.scm.domain.CustomerContact;
import spica.scm.domain.UserProfile;
import spica.scm.validate.CustomerContactIs;

/**
 * CustomerContactService
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016 
 */

public abstract class CustomerContactService {  
 
 /**
   * CustomerContact paginated query.
   *
   * @param main
   * @return SqlPage
   */
    private static SqlPage getCustomerContactSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_customer_contact", CustomerContact.class, main);
    sql.main("select scm_customer_contact.id,scm_customer_contact.customer_id,scm_customer_contact.designation_id,scm_customer_contact.user_profile_id,scm_customer_contact.sort_order,scm_customer_contact.status_id,scm_customer_contact.created_by,scm_customer_contact.modified_by,scm_customer_contact.created_at,scm_customer_contact.modified_at from scm_customer_contact scm_customer_contact "); //Main query
    sql.count("select count(scm_customer_contact.id) as total from scm_customer_contact scm_customer_contact "); //Count query
    sql.join("left outer join scm_customer scm_customer_contactcustomer_id on (scm_customer_contactcustomer_id.id = scm_customer_contact.customer_id) left outer join scm_designation scm_customer_contactdesignation_id on (scm_customer_contactdesignation_id.id = scm_customer_contact.designation_id) left outer join scm_user_profile scm_customer_contactuser_profile_id on (scm_customer_contactuser_profile_id.id = scm_customer_contact.user_profile_id) left outer join scm_status scm_customer_contactstatus_id on (scm_customer_contactstatus_id.id = scm_customer_contact.status_id)"); //Join Query

    sql.string(new String[]{"scm_customer_contactcustomer_id.customer_name","scm_customer_contactdesignation_id.title","scm_customer_contactuser_profile_id.user_code","scm_customer_contactstatus_id.title","scm_customer_contact.created_by","scm_customer_contact.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_customer_contact.id","scm_customer_contact.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_customer_contact.created_at","scm_customer_contact.modified_at"});  //Date search or sort fields
    return sql;
  }
	/**
   * Return all customer address of a customer.
   *
   * @param main
   * @param customer
   * @return
   */
  public static final List<CustomerContact> contactListByCustomer(Main main, Customer customer) {
    SqlPage sql = getCustomerContactSqlPaged(main);
    if (customer.getId() == null) {
      return null;
    }
    sql.cond("where scm_customer_contact.customer_id=?");
    sql.param(customer.getId());
    return AppService.listAllJpa(main, sql);
  }

 /**
  * Return List of CustomerContact.
  * @param main
  * @return List of CustomerContact
  */
  public static final List<CustomerContact> listPaged(Main main) {
     return AppService.listPagedJpa(main, getCustomerContactSqlPaged(main));
  }

//  /**
//   * Return list of CustomerContact based on condition
//   * @param main
//   * @return List<CustomerContact>
//   */
//  public static final List<CustomerContact> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCustomerContactSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select CustomerContact by key.
  * @param main
  * @param customerContact
  * @return CustomerContact
  */
  public static final CustomerContact selectByPk(Main main, CustomerContact customerContact) {
    return (CustomerContact) AppService.find(main, CustomerContact.class, customerContact.getId());
  }

 /**
  * Insert CustomerContact.
  * @param main
  * @param customerContact
  */
  public static final void insert(Main main, CustomerContact customerContact) {
    CustomerContactIs.insertAble(main, customerContact);  //Validating
    AppService.insert(main, customerContact);

  }

 /**
  * Update CustomerContact by key.
  * @param main
  * @param customerContact
  * @return CustomerContact
  */
  public static final CustomerContact updateByPk(Main main, CustomerContact customerContact) {
    CustomerContactIs.updateAble(main, customerContact); //Validating
    return (CustomerContact) AppService.update(main, customerContact);
  }

  /**
   * Insert or update CustomerContact
   *
   * @param main
   * @param customerContact
   */
  public static void insertOrUpdate(Main main, CustomerContact customerContact) {
    if (customerContact.getId() == null) {
      insert(main, customerContact);
    } else {
    updateByPk(main, customerContact);
    }
  }
/**
 * Making default for newly added or updated if isdefault is checked  
 * @param main
 * @param customerContact 
 */
  public static void makeDefault(Main main, CustomerContact customerContact) {
    if (customerContact.getSortOrder()== 0) {
      main.param(1);
      main.param(customerContact.getCustomerId().getId());
      main.param(customerContact.getId());
      main.param(0);
      AppService.updateSql(main, CustomerContact.class, "update scm_customer_contact set modified_by=?,modified_at=?,sort_order=? where customer_id=? and id !=? and sort_order=?", true);
    }
  }
  /**
   * Clone and existing object
   *
   * @param main
   * @param customerContact
   */
  public static void clone(Main main, CustomerContact customerContact) {
    customerContact.setId(null); //Set to null for insert
    insert(main, customerContact);
  }

 /**
  * Delete CustomerContact.
  * @param main
  * @param customerContact
  */
  public static final void deleteByPk(Main main, CustomerContact customerContact) {
    CustomerContactIs.deleteAble(main, customerContact); //Validation
    AppService.delete(main, CustomerContact.class, customerContact.getId());
  }
	
 /**
  * Delete Array of CustomerContact.
  * @param main
  * @param customerContact
  */
  public static final void deleteByPkArray(Main main, CustomerContact[] customerContact) {
    for (CustomerContact e : customerContact) {
      deleteByPk(main, e);
    }
  }
  
  public static void insertArray(Main main, UserProfile[] userProfileSelected, Customer customer) {
    if (userProfileSelected != null) {
      CustomerContact customerContact;
      for (UserProfile userProfile : userProfileSelected) {  //Reinserting
        customerContact = new CustomerContact();
        customerContact.setUserProfileId(userProfile);
        customerContact.setCustomerId(customer);
        customerContact.setDesignationId(userProfile.getDesignationId());
        customerContact.setStatusId(userProfile.getStatusId());
        insert(main, customerContact);
      }
    }
  }
  
  public static final void deleteCustomerContact(Main main,CustomerContact customerContact) {
    AppService.deleteSql(main, CustomerContact.class, "delete from scm_customer_contact where id = ?", new Object[]{customerContact.getId()});
  }
}
