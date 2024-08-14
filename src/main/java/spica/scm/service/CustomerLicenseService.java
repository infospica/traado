/*
 * @(#)CustomerLicenseService.java	1.0 Thu Jun 09 11:13:13 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Customer;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CustomerLicense;
import spica.scm.validate.CustomerLicenseIs;
import spica.sys.UserRuntimeView;

/**
 * CustomerLicenseService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 09 11:13:13 IST 2016
 */
public abstract class CustomerLicenseService {

  /**
   * CustomerLicense paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCustomerLicenseSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_customer_license", CustomerLicense.class, main);
    sql.main("select scm_customer_license.id,scm_customer_license.customer_id,scm_customer_license.license_type_id,scm_customer_license.description,scm_customer_license.license_key,scm_customer_license.issued_at,scm_customer_license.valid_from,scm_customer_license.valid_to,scm_customer_license.file_path,scm_customer_license.sort_order,scm_customer_license.status_id,scm_customer_license.created_at,scm_customer_license.modified_at,scm_customer_license.created_by,scm_customer_license.modified_by from scm_customer_license scm_customer_license "); //Main query
    sql.count("select count(scm_customer_license.id) as total from scm_customer_license scm_customer_license "); //Count query
    sql.join("left outer join scm_customer scm_customer_licensecustomer_id on (scm_customer_licensecustomer_id.id = scm_customer_license.customer_id) left outer join scm_license_type scm_customer_licenselicense_type_id on (scm_customer_licenselicense_type_id.id = scm_customer_license.license_type_id) left outer join scm_status scm_customer_licensestatus_id on (scm_customer_licensestatus_id.id = scm_customer_license.status_id)"); //Join Query

    sql.string(new String[]{"scm_customer_licensecustomer_id.customer_name", "scm_customer_licenselicense_type_id.title", "scm_customer_license.description", "scm_customer_license.license_key", "scm_customer_license.file_path", "scm_customer_licensestatus_id.title", "scm_customer_license.created_by", "scm_customer_license.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_customer_license.id", "scm_customer_license.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_customer_license.issued_at", "scm_customer_license.valid_from", "scm_customer_license.valid_to", "scm_customer_license.created_at", "scm_customer_license.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CustomerLicense.
   *
   * @param main
   * @return List of CustomerLicense
   */
  public static final List<CustomerLicense> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCustomerLicenseSqlPaged(main));
  }

//  /**
//   * Return list of CustomerLicense based on condition
//   * @param main
//   * @return List<CustomerLicense>
//   */
//  public static final List<CustomerLicense> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCustomerLicenseSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select CustomerLicense by key.
   *
   * @param main
   * @param customerLicense
   * @return CustomerLicense
   */
  public static final CustomerLicense selectByPk(Main main, CustomerLicense customerLicense) {
    return (CustomerLicense) AppService.find(main, CustomerLicense.class, customerLicense.getId());
  }

  /**
   * Insert CustomerLicense.
   *
   * @param main
   * @param customerLicense
   */
  public static final void insert(Main main, CustomerLicense customerLicense) {
    CustomerLicenseIs.insertAble(main, customerLicense, UserRuntimeView.instance().getCompany().getId());  //Validating
    AppService.insert(main, customerLicense);

  }

  /**
   * Update CustomerLicense by key.
   *
   * @param main
   * @param customerLicense
   * @return CustomerLicense
   */
  public static final CustomerLicense updateByPk(Main main, CustomerLicense customerLicense) {
    CustomerLicenseIs.updateAble(main, customerLicense); //Validating
    return (CustomerLicense) AppService.update(main, customerLicense);
  }

  /**
   * Insert or update CustomerLicense
   *
   * @param main
   * @param CustomerLicense
   */
  public static void insertOrUpdate(Main main, CustomerLicense customerLicense) {
    if (customerLicense.getId() == null) {
      insert(main, customerLicense);
    } else {
      updateByPk(main, customerLicense);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param customerLicense
   */
  public static void clone(Main main, CustomerLicense customerLicense) {
    customerLicense.setId(null); //Set to null for insert
    insert(main, customerLicense);
  }

  /**
   * Delete CustomerLicense.
   *
   * @param main
   * @param customerLicense
   */
  public static final void deleteByPk(Main main, CustomerLicense customerLicense) {
    CustomerLicenseIs.deleteAble(main, customerLicense); //Validation
    AppService.delete(main, CustomerLicense.class, customerLicense.getId());
  }

  /**
   * Delete Array of CustomerLicense.
   *
   * @param main
   * @param customerLicense
   */
  public static final void deleteByPkArray(Main main, CustomerLicense[] customerLicense) {
    for (CustomerLicense e : customerLicense) {
      deleteByPk(main, e);
    }
  }

  /**
   * Return all license of a customer.
   *
   * @param main
   * @param customer
   * @return
   */
  public static final List<CustomerLicense> licenseListByCustomer(Main main, Customer customer) {
    main.clear();
    SqlPage sql = getCustomerLicenseSqlPaged(main);
    if (customer.getId() == null) {
      return null;
    }
    sql.cond("where scm_customer_license.customer_id=?");
    sql.param(customer.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static List<CustomerLicense> listActiveLicenseBycustomer(Main main, Customer customer, int status) {

    return AppService.list(main, CustomerLicense.class, "select id,customer_id,license_type_id,license_key,status_id from scm_customer_license where customer_id=? and status_id=?", new Object[]{customer.getId(), status});

  }
}
