/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.io.IOException;
import java.util.List;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.TradeOutstanding;
import spica.fin.service.LedgerExternalService;
import spica.scm.domain.AddressType;
import spica.scm.domain.Company;
import spica.scm.domain.Consignment;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerImporter;
import spica.scm.domain.CustomerImporterLog;
import spica.scm.domain.CustomerTradeProfile;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesServicesInvoice;
import spica.scm.domain.Status;
import spica.scm.domain.TradeProfile;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.config.AppConfig;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-2
 */
public abstract class CustomerImporterService {

  public static List<CustomerImporter> selectAllCustomerImporter(Main main, Company company) {
    List<CustomerImporter> list = AppService.list(main, CustomerImporter.class, "select * from scm_customer_importer where company_id=?", new Object[]{company.getId()});
    return list;
  }

  public static void deleteAllCustomerImporter(Main main) {
    AppService.deleteSql(main, CustomerImporter.class, "delete from scm_customer_importer", null);
  }

  public static final AccountingLedger getSalesLedger(Main main, Company company) {
    return (AccountingLedger) AppService.single(main, AccountingLedger.class, " select * from fin_accounting_ledger where company_id = ? and ledger_code = 'SAL_PROD' ORDER BY sort_order LIMIT 1", new Object[]{company.getId()});
  }

  public static Customer insertOrUpdate(Main main, Customer customer, TradeProfile tradeTypeSelected, CustomerAddress regAddress) {

    if (!isExist(main, customer)) {
      AppService.insert(main, customer);
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
        regAddress.setTerritoryId(null);
      }
      LedgerExternalService.saveLedgerCustomer(main, customer);
      CustomerAddressService.insertOrUpdate(main, regAddress);
    } else {
      AppService.insert(main, new CustomerImporterLog(customer.getCustomerName(), customer.getGstNo(), "DUPLICATE"));
    }
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

  public static void replaceCustomer(Main main, Customer fromCustomer, Customer toCustomer) {
    String sql = null;
    sql = "INSERT INTO scm_sales_account(company_id,customer_id,account_group_id,customer_trade_profile_id,sales_credit_amount,sales_credit_days,outstanding_bill_limit_cnt,\n"
            + "outstanding_bill_limit_amt,account_group_price_list_id,created_by,created_at)\n"
            + "SELECT company_id,1155,account_group_id,customer_trade_profile_id,sales_credit_amount,sales_credit_days,outstanding_bill_limit_cnt,\n"
            + "outstanding_bill_limit_amt,account_group_price_list_id,'root',NOW() FROM scm_sales_account\n"
            + "WHERE customer_id =? AND account_group_id NOT IN(SELECT account_group_id FROM scm_sales_account WHERE customer_id =?)";
    AppDb.insert(main.dbConnector(), sql, new Object[]{fromCustomer.getId(), toCustomer.getId()});
    main.clear();
    main.param(toCustomer.getId());
    main.param(fromCustomer.getId());
    sql = "UPDATE fin_accounting_transaction_detail\n"
            + "SET accounting_ledger_id= (SELECT id FROM fin_accounting_ledger WHERE accounting_entity_type_id =2 AND entity_id =?)\n"
            + "WHERE accounting_ledger_id IN(SELECT id FROM fin_accounting_ledger WHERE accounting_entity_type_id =2 AND entity_id =?)";
    AppService.updateSql(main, AccountingTransactionDetail.class, sql, false);
    main.clear();
    main.param(toCustomer.getId());
    main.param(fromCustomer.getId());
    sql = " UPDATE fin_accounting_transaction SET ledger_debit_id = (SELECT id FROM fin_accounting_ledger WHERE accounting_entity_type_id =2 AND entity_id =?)\n"
            + " WHERE ledger_debit_id = (SELECT id FROM fin_accounting_ledger WHERE accounting_entity_type_id =2 AND entity_id =?)";
    AppService.updateSql(main, AccountingTransaction.class, sql, false);
    main.clear();
    main.param(toCustomer.getId());
    main.param(fromCustomer.getId());
    sql = " UPDATE fin_accounting_transaction SET ledger_credit_id = (SELECT id FROM fin_accounting_ledger WHERE accounting_entity_type_id =2 AND entity_id =?)\n"
            + " WHERE ledger_credit_id = (SELECT id FROM fin_accounting_ledger WHERE accounting_entity_type_id =2 AND entity_id =?)";
    AppService.updateSql(main, AccountingTransaction.class, sql, false);
    main.clear();
    main.param(toCustomer.getId());
    main.param(fromCustomer.getId());
    sql = " UPDATE scm_sales_services_invoice SET accounting_ledger_id = (SELECT id FROM fin_accounting_ledger WHERE accounting_entity_type_id =2 AND entity_id =?)\n"
            + " WHERE accounting_ledger_id = (SELECT id FROM fin_accounting_ledger WHERE accounting_entity_type_id =2 AND entity_id =?)";
    AppService.updateSql(main, SalesServicesInvoice.class, sql, false);
    main.clear();
    main.param(toCustomer.getId());
    main.param(fromCustomer.getId());
    //sql = "UPDATE fin_trade_outstanding_tax SET customer_id=? WHERE customer_id =?;";
    //AppDb.update(main.dbConnector(), sql, new Object[]{toCustomer.getId(), fromCustomer.getId()});
    sql = "UPDATE fin_trade_outstanding SET customer_id=? WHERE customer_id =?;";
    AppService.updateSql(main, TradeOutstanding.class, sql, false);
    main.clear();
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(toCustomer.getId());
    main.param(fromCustomer.getId());
    sql = " UPDATE scm_sales_invoice SET customer_id =?,customer_address_id=(SELECT id FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "shipping_address_id=(SELECT id FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "customer_address = (SELECT address FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "customer_country_id=(SELECT country_id FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "customer_state_id=(SELECT state_id FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "customer_district_id=(SELECT district_id FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "customer_territory_id=(SELECT territory_id FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "customer_pin=(SELECT pin FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "customer_phone_1=(SELECT phone_1 FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "customer_phone_2=(SELECT phone_2 FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "customer_fax_1=(SELECT fax_1 FROM scm_customer_address WHERE customer_id =? AND address_type_id=1),"
            + "customer_email=(SELECT email FROM scm_customer_address WHERE customer_id =? AND address_type_id=1) WHERE\n"
            + "customer_id =? ;";
    AppService.updateSql(main, SalesInvoice.class, sql, false);
    main.clear();
    main.param(toCustomer.getId());
    main.param(fromCustomer.getId());
    sql = " UPDATE scm_sales_invoice SET shipping_address_id=(SELECT id FROM scm_customer_address WHERE customer_id =? AND address_type_id=1) WHERE\n"
            + "customer_id =? ;";
    AppService.updateSql(main, SalesInvoice.class, sql, false);
    main.clear();
    main.param(toCustomer.getId());
    main.param(fromCustomer.getId());
    sql = "UPDATE scm_consignment SET customer_id=? WHERE customer_id =? ";
    AppService.updateSql(main, Consignment.class, sql, false);

//    sql="UPDATE fin_cheque_entry SET ledger_id = (SELECT id FROM fin_accounting_ledger WHERE accounting_entity_type_id =2 AND entity_id =?) "
//            + "WHERE ledger_id = (SELECT id FROM fin_accounting_ledger WHERE accounting_entity_type_id =2 AND entity_id =?)  ";
//    AppDb.update(main.dbConnector(), sql, new Object[]{toCustomer.getId(), fromCustomer.getId()});
    //  sql = "SELECT * FROM scm_platform WHERE sales_invoice_id IN \n"
    //          + "(SELECT id FROM scm_sales_return WHERE customer_id =?)";
    //  List<Platform> platformList;
    //  platformList=AppDb.getList(main.dbConnector(), Platform.class, sql, new Object[]{fromCustomer.getId()});
  }

  public static final boolean isExist(Main main, Customer customer) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_customer where upper(customer_name)=? and company_id=?", new Object[]{StringUtil.toUpperCase(customer.getCustomerName()), customer.getCompanyId().getId()})) {
      return true;
    }
    return false;
  }

  public static void deleteCompanyById(Main main, Integer id) throws IOException {
    AppDb.executeFromFile(main.dbConnector(), AppConfig.contextPrivateFolder + "company-delete.txt", ";", new Object[]{id});
  }
}
