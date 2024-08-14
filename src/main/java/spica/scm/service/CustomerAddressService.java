/*
 * @(#)CustomerAddressService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
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
import spica.scm.domain.CustomerAddress;
import spica.scm.validate.CustomerAddressIs;

/**
 * CustomerAddressService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CustomerAddressService {

  /**
   * CustomerAddress paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCustomerAddressSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_customer_address", CustomerAddress.class, main);
    sql.main("select scm_customer_address.id,scm_customer_address.customer_id,scm_customer_address.address_type_id,scm_customer_address.address,scm_customer_address.country_id,scm_customer_address.state_id,scm_customer_address.district_id,scm_customer_address.pin,scm_customer_address.territory_id,scm_customer_address.phone_1,scm_customer_address.phone_2,scm_customer_address.phone_3,scm_customer_address.fax_1,scm_customer_address.fax_2,scm_customer_address.email,scm_customer_address.sort_order,scm_customer_address.status_id,scm_customer_address.created_by,scm_customer_address.modified_by,scm_customer_address.created_at,scm_customer_address.modified_at from scm_customer_address scm_customer_address "); //Main query
    sql.count("select count(scm_customer_address.id) as total from scm_customer_address scm_customer_address "); //Count query
    sql.join("left outer join scm_customer scm_customer_addresscustomer_id on (scm_customer_addresscustomer_id.id = scm_customer_address.customer_id) left outer join scm_address_type scm_customer_addressaddress_type_id on (scm_customer_addressaddress_type_id.id = scm_customer_address.address_type_id) left outer join scm_country scm_customer_addresscountry_id on (scm_customer_addresscountry_id.id = scm_customer_address.country_id) left outer join scm_state scm_customer_addressstate_id on (scm_customer_addressstate_id.id = scm_customer_address.state_id) left outer join scm_district scm_customer_addressdistrict_id on (scm_customer_addressdistrict_id.id = scm_customer_address.district_id) left outer join scm_territory scm_customer_addressterritory_id on (scm_customer_addressterritory_id.id = scm_customer_address.territory_id) left outer join scm_status scm_customer_addressstatus_id on (scm_customer_addressstatus_id.id = scm_customer_address.status_id)"); //Join Query

    sql.string(new String[]{"scm_customer_addresscustomer_id.customer_name","scm_customer_addressaddress_type_id.title","scm_customer_address.address","scm_customer_addresscountry_id.country_name","scm_customer_addressstate_id.state_name","scm_customer_addressdistrict_id.district_name","scm_customer_address.pin","scm_customer_addressterritory_id.territory_name","scm_customer_address.phone_1","scm_customer_address.phone_2","scm_customer_address.phone_3","scm_customer_address.fax_1","scm_customer_address.fax_2","scm_customer_address.email","scm_customer_addressstatus_id.title","scm_customer_address.created_by","scm_customer_address.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_customer_address.id","scm_customer_address.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_customer_address.created_at","scm_customer_address.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return all customer address of a customer.
   *
   * @param main
   * @param customer
   * @return
   */
  public static final List<CustomerAddress> addressListByCustomer(Main main, Customer customer) {
    SqlPage sql = getCustomerAddressSqlPaged(main);
    if (customer.getId() == null) {
      return null;
    }
    sql.cond("where scm_customer_address.customer_id=?");
    sql.param(customer.getId());
    return AppService.listAllJpa(main, sql);
  }

  /**
   * Return List of CustomerAddress.
   *
   * @param main
   * @return List of CustomerAddress
   */
  public static final List<CustomerAddress> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCustomerAddressSqlPaged(main));
  }

//  /**
//   * Return list of CustomerAddress based on condition
//   * @param main
//   * @return List<CustomerAddress>
//   */
//  public static final List<CustomerAddress> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCustomerAddressSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select CustomerAddress by key.
   *
   * @param main
   * @param customerAddress
   * @return CustomerAddress
   */
  public static final CustomerAddress selectByPk(Main main, CustomerAddress customerAddress) {
    return (CustomerAddress) AppService.find(main, CustomerAddress.class, customerAddress.getId());
  }

  /**
   * Insert CustomerAddress.
   *
   * @param main
   * @param customerAddress
   */
  public static final void insert(Main main, CustomerAddress customerAddress) {
    CustomerAddressIs.insertAble(main, customerAddress);  //Validating
    AppService.insert(main, customerAddress);

  }

  /**
   * Update CustomerAddress by key.
   *
   * @param main
   * @param customerAddress
   * @return CustomerAddress
   */
  public static final CustomerAddress updateByPk(Main main, CustomerAddress customerAddress) {
    CustomerAddressIs.updateAble(main, customerAddress); //Validating
    return (CustomerAddress) AppService.update(main, customerAddress);
  }

  /**
   * Insert or update CustomerAddress
   *
   * @param main
   * @param customerAddress
   */
  public static void insertOrUpdate(Main main, CustomerAddress customerAddress) {
    if (customerAddress.getId() == null) {
      insert(main, customerAddress);
    } else {
      updateByPk(main, customerAddress);
    }
  }

  /**
   * Making default for newly added or updated if isdefault is checked
   *
   * @param main
   * @param customerAddress
   */
  public static void makeDefault(Main main, CustomerAddress customerAddress) {
    if (customerAddress.getSortOrder()== 0) {
      main.param(1);
      main.param(customerAddress.getCustomerId().getId());
      main.param(customerAddress.getId());
      main.param(0);
      main.param(customerAddress.getAddressTypeId().getId());
      AppService.updateSql(main, CustomerAddress.class, "update scm_customer_address set modified_by=?,modified_at=?,sort_order=? where customer_id=? and id !=? and sort_order=? and address_type_id=?", true);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param customerAddress
   */
  public static void clone(Main main, CustomerAddress customerAddress) {
    customerAddress.setId(null); //Set to null for insert
    insert(main, customerAddress);
  }

  /**
   * Delete CustomerAddress.
   *
   * @param main
   * @param customerAddress
   */
  public static final void deleteByPk(Main main, CustomerAddress customerAddress) {
    CustomerAddressIs.deleteAble(main, customerAddress); //Validation
    AppService.delete(main, CustomerAddress.class, customerAddress.getId());
  }

  /**
   * Delete Array of CustomerAddress.
   *
   * @param main
   * @param customerAddress
   */
  public static final void deleteByPkArray(Main main, CustomerAddress[] customerAddress) {
    for (CustomerAddress e : customerAddress) {
      deleteByPk(main, e);
    }
  }
  
  public static final CustomerAddress selectCustomerRegisteredAddress(Main main, Customer customer) {
    return (CustomerAddress) AppService.single(main, CustomerAddress.class, "select * from scm_customer_address where customer_id = ? and address_type_id = ?", new Object[]{customer.getId(), AddressTypeService.REGISTERED_ADDRESS});
  }
}
