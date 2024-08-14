/*
 * @(#)CustomerService.java	1.0 Wed Mar 30 12:35:28 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import spica.fin.service.LedgerExternalService;
import java.util.List;
import spica.fin.domain.AccountingLedger;
import spica.scm.common.CustomerHealth;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AddressType;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerBank;
import spica.scm.domain.CustomerContact;
import spica.scm.domain.CustomerTradeProfile;
import spica.scm.domain.SalesAccount;
import spica.scm.domain.Status;
import spica.scm.domain.TradeProfile;
import spica.scm.validate.CustomerIs;
import spica.scm.domain.CustomerLicense;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.UserProfile;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;

/**
 * CustomerService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CustomerService {

  /**
   * Customer paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCustomerSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_customer", Customer.class, main);
    sql.main("select tab1.id,tab1.company_id,tab1.customer_company_id,tab1.customer_id,tab1.customer_name,tab1.description,"
            + "tab1.country_id,tab1.state_id,tab1.pan_no,tab1.cst_no,tab1.vat_no,tab1.tin_no,tab1.gst_no,"
            + "tab1.status_id,tab1.created_at,tab1.modified_at,tab1.created_by,tab1.modified_by,tab1.taxable,tab1.transporter_id,tab1.fssai_no "
            + "from scm_customer tab1 "); //Main query
    sql.count("select count(tab1.id) as total from scm_customer tab1 "); //Count query
    sql.join("left outer join scm_company tab2 on (tab2.id = tab1.company_id) "
            + "left outer join scm_customer tab3 on (tab3.id = tab1.customer_id) "
            + "left outer join scm_country tab4 on (tab4.id = tab1.country_id) "
            + "left outer join scm_state tab5 on (tab5.id = tab1.state_id) "
            + "left outer join scm_status tab6 on (tab6.id = tab1.status_id)"
            + "left outer join scm_transporter scm_customertransporter_id on (scm_customertransporter_id.id = tab1.transporter_id)"); //Join Query

    sql.string(new String[]{"tab2.company_name", "tab3.customer_name", "tab1.customer_name", "tab1.description", "tab4.country_name", "tab5.state_name", "tab1.pan_no", "tab1.cst_no", "tab1.vat_no", "tab1.tin_no", "tab1.gst_no", "tab6.title", "tab1.created_by", "tab1.modified_by", "tab1.fssai_no"}); //String search or sort fields
    sql.number(new String[]{"tab1.id"}); //Numeric search or sort fields
    sql.date(new String[]{"tab1.created_at", "tab1.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Customer.
   *
   * @param main
   * @param company
   * @return List of Customer
   */
  public static List<Customer> listPaged(Main main, Company company) {
    SqlPage sql = getCustomerSqlPaged(main);
    sql.cond("where tab1.company_id=?");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

  /**
   * Return all branches of a customer.
   *
   * @param main
   * @param customer
   * @return
   */
  public static final List<Customer> branchListByCustomer(Main main, Customer customer) {
    SqlPage sql = getCustomerSqlPaged(main);
    if (customer.getId() == null) {
      return null;
    }
    sql.cond("where tab1.customer_id=?");
    sql.param(customer.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of Customer based on condition
//   * @param main
//   * @return List<Customer>
//   */
//  public static final List<Customer> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCustomerSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select Customer by key.
   *
   * @param main
   * @param customer
   * @return Customer
   */
  public static final Customer selectByPk(Main main, Customer customer) {
    return (Customer) AppService.find(main, Customer.class, customer.getId());
  }

//  /**
//   * Insert Customer.
//   *
//   * @param main
//   * @param customer
//   */
//  public static final void insert(Main main, Customer customer) {
//    CustomerIs.insertAble(main, customer);  //Validating
//    AppService.insert(main, customer);
//  }
//
//  /**
//   * Update Customer by key.
//   *
//   * @param main
//   * @param customer
//   * @return Customer
//   */
//  public static final Customer updateByPk(Main main, Customer customer) {
//    CustomerIs.updateAble(main, customer); //Validating
//    return (Customer) AppService.update(main, customer);
//  }
  /**
   * Insert or update Customer
   *
   * @param main
   * @param customer
   * @param tradeTypeSelected
   */
  public static Customer insertOrUpdate(Main main, Customer customer, TradeProfile tradeTypeSelected, CustomerAddress regAddress) {
    if (customer.getId() != null) {
      CustomerIs.updateAble(main, customer); //Validating
      customer = (Customer) AppService.update(main, customer);
    } else {
      if (regAddress != null) {
        regAddress.setId(null);
      }
      CustomerIs.insertAble(main, customer);  //Validating    
      AppService.insert(main, customer);

    }
    if (tradeTypeSelected != null) {
      main.em().executeJpql("delete from CustomerTradeProfile c where c.customerId.id = ?1", new Object[]{customer.getId()});
      insertCustomerTradeProfile(main, customer, tradeTypeSelected);
    }
    //TODO if Address methods are properly implemented this code can be reused for all addresses.

    if (regAddress != null) {
      customer.setCustomerAddress(regAddress.getAddress());
      customer.setCustomerPin(regAddress.getPin());
      if (regAddress.getId() == null) {
        AddressType addressType = new AddressType();
        addressType.setId(AddressTypeService.REGISTERED_ADDRESS);
        Status status = new Status();
        status.setId(StatusService.STATUS_ACTIVE);
        regAddress.setAddressTypeId(addressType);
        regAddress.setStatusId(status);
        regAddress.setCustomerId(customer);
      }
      regAddress.setCountryId(customer.getCountryId());
      regAddress.setStateId(customer.getStateId());
    }
    LedgerExternalService.saveLedgerCustomer(main, customer);
    CustomerAddressService.insertOrUpdate(main, regAddress);

    return customer;

  }

  /**
   *
   * @param main
   * @param customer
   * @param tradeProfile
   */
  private static void insertCustomerTradeProfile(Main main, Customer customer, TradeProfile tradeProfile) {
    if (tradeProfile != null) {
      CustomerTradeProfile ctp = new CustomerTradeProfile();
      ctp.setCustomerId(customer);
      ctp.setTradeProfileId(tradeProfile);
      CustomerTradeProfileService.insert(main, ctp);
    }
  }

  public static TradeProfile getCustomerTradeProfile(Main main, Customer cust) {
    CustomerTradeProfile tp = (CustomerTradeProfile) main.em().single(CustomerTradeProfile.class, "SELECT * FROM scm_customer_trade_profile WHERE customer_id=?", new Object[]{cust.getId()});
    return tp.getTradeProfileId();
  }

  /**
   * Select the list of related records based on parent id.
   *
   * @param main
   * @param tradeTypeSelected
   * @param customer
   */
  public static void insertArray(Main main, TradeProfile[] tradeTypeSelected, Customer customer) {
    if (tradeTypeSelected != null) {
      CustomerTradeProfile bg;
      for (TradeProfile tradetype : tradeTypeSelected) {  //Reinserting
        bg = new CustomerTradeProfile();
        bg.setCustomerId(customer);
        bg.setTradeProfileId(tradetype);
//        CustomerTradeProfile ctp = (CustomerTradeProfile) AppService.single(main, CustomerTradeProfile.class, "select * from scm_customer_trade_profile where customer_id=? and trade_profile_id=?", new Object[]{customer.getId(), tradetype.getId()});
        if (!AppService.exist(main, "select id from scm_customer_trade_profile where customer_id=? and trade_profile_id=?", new Object[]{customer.getId(), tradetype.getId()})) {
          CustomerTradeProfileService.insert(main, bg);
        }
      }
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param customer
   */
  public static void clone(Main main, Customer customer, TradeProfile tradeTypeSelected, CustomerAddress regAddress) {

    List<SalesAccount> salesAcList = SalesAccountService.getSalesAccountList(main, customer.getId(), customer.getCompanyId().getId());
    main.em().detach(customer);
    customer.setId(null); //Set to null for insert
    insertOrUpdate(main, customer, tradeTypeSelected, regAddress);
    for (SalesAccount salesAccount : salesAcList) {
      SalesAccount sa = new SalesAccount(salesAccount, customer);
      SalesAccountService.insertOrUpdate(main, sa);
    }
  }

  /**
   * Delete Customer.
   *
   * @param main
   * @param customer
   */
  public static final void deleteByPk(Main main, Customer customer) {
    LedgerExternalService.deleteLedgerCustomer(main, customer);
    AppService.deleteSql(main, CustomerAddress.class, "delete from scm_customer_address where scm_customer_address.customer_id=?", new Object[]{customer.getId()});
    AppService.deleteSql(main, CustomerContact.class, "delete from scm_customer_contact where scm_customer_contact.customer_id=?", new Object[]{customer.getId()});
    AppService.deleteSql(main, CustomerBank.class, "delete from scm_customer_bank where scm_customer_bank.customer_id=?", new Object[]{customer.getId()});
    AppService.deleteSql(main, CustomerTradeProfile.class, "delete from scm_customer_trade_profile where scm_customer_trade_profile.customer_id=?", new Object[]{customer.getId()});
    AppService.deleteSql(main, CustomerLicense.class, "delete from scm_customer_license where customer_id=?", new Object[]{customer.getId()});
    AppService.deleteSql(main, SalesAccount.class, "delete from scm_sales_account where customer_id=?", new Object[]{customer.getId()});
    AppService.delete(main, Customer.class, customer.getId());
  }

  /**
   * Delete Array of Customer.
   *
   * @param main
   * @param customer
   */
  public static final void deleteByPkArray(Main main, Customer[] customer) {
    for (Customer e : customer) {
      deleteByPk(main, e);
    }
  }

  public static final AccountingLedger selectSalesLedgerByCustomer(Main main, Integer customerId) {
    String sql = " select * from fin_accounting_ledger t1 JOIN scm_customer t2 ON t2.sales_ledger_id = t1.id AND t2.id = ? ";
    AccountingLedger ledger = (AccountingLedger) AppService.single(main, AccountingLedger.class, sql, new Object[]{customerId});
    return ledger;
  }

  public static CustomerHealth selectLastMonthsSales(Main main, CustomerHealth health, Integer companyId, AccountGroup accountGroup, UserProfile salesAgent, Account account) {
    String where = " t2.entity_id = ? AND t2.id=t1.ledger_debit_id AND t1.company_id = ? AND t1.entry_date::date >= ?::date AND t1.entry_date <=?::date ";
    if ((account != null && account.getId() != null) && (accountGroup == null || accountGroup.getId() == null)) {
      where += " AND t1.account_group_id in( select account_group_id from scm_account_group_detail where account_id = ? ) ";
    }
    if (accountGroup != null && accountGroup.getId() != null) {
      where += " AND t1.account_group_id = ? ";
    }
    if (salesAgent != null && salesAgent.getId() != null) {
      where += " AND t1.entity_id in ( select id from scm_sales_invoice where company_sales_agent_person_profile_id = ? )";
    }

    String sql = "select tab1.last_three_months,tab2.previous_month,tab3.current_month from \n"
            + "(select sum(t1.total_amount) as last_three_months from fin_accounting_transaction t1,fin_accounting_ledger t2\n"
            + "where " + where + " )tab1,\n"
            + "(select sum(t1.total_amount) as previous_month from fin_accounting_transaction t1,fin_accounting_ledger t2\n"
            + "where " + where + " )tab2,\n"
            + "(select sum(t1.total_amount) as current_month from fin_accounting_transaction t1,fin_accounting_ledger t2\n"
            + "where " + where + " )tab3\n";
    main.clear();
    main.param(health.getCustomerId());
    main.param(companyId);
    main.param(health.getLastFromDate());
    main.param(health.getLastToDate());
    if ((account != null && account.getId() != null) && (accountGroup == null || accountGroup.getId() == null)) {
      main.param(account.getId());
    }
    if (accountGroup != null && accountGroup.getId() != null) {
      main.param(accountGroup.getId());
    }
    if (salesAgent != null && salesAgent.getId() != null) {
      main.param(salesAgent.getId());
    }

    main.param(health.getCustomerId());
    main.param(companyId);
    main.param(health.getPreviousFromDate());
    main.param(health.getPreviousToDate());
    if ((account != null && account.getId() != null) && (accountGroup == null || accountGroup.getId() == null)) {
      main.param(account.getId());
    }
    if (accountGroup != null && accountGroup.getId() != null) {
      main.param(accountGroup.getId());
    }
    if (salesAgent != null && salesAgent.getId() != null) {
      main.param(salesAgent.getId());
    }

    main.param(health.getCustomerId());
    main.param(companyId);
    main.param(health.getCurrentFromDate());
    main.param(health.getCurrentToDate());
    if ((account != null && account.getId() != null) && (accountGroup == null || accountGroup.getId() == null)) {
      main.param(account.getId());
    }
    if (accountGroup != null && accountGroup.getId() != null) {
      main.param(accountGroup.getId());
    }
    if (salesAgent != null && salesAgent.getId() != null) {
      main.param(salesAgent.getId());
    }

    health = (CustomerHealth) AppDb.single(main.dbConnector(), CustomerHealth.class, sql, main.getParamData().toArray());
    return health;
  }

  public static Double customerTotalSalesValue(Main main, SalesInvoice salesInvoice) {
    return AppService.countDouble(main, "select SUM(scm_sales_invoice.invoice_amount) from scm_sales_invoice\n"
            + "where scm_sales_invoice.customer_id=? and scm_sales_invoice.financial_year_id=?\n"
            + "and scm_sales_invoice.company_id=? and scm_sales_invoice.sales_invoice_status_id>=2", new Object[]{salesInvoice.getCustomerId().getId(), salesInvoice.getFinancialYearId().getId(),
              salesInvoice.getCompanyId().getId()});
  }
}
