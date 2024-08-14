/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import spica.constant.AccountingConstant;

import spica.reports.model.CustomerOutstanding;
import spica.reports.model.FilterParameters;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Customer;
import spica.scm.domain.District;
import spica.scm.domain.Product;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.Territory;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;

/**
 *
 * @author krishna.vm
 */
public class CustomerOutstandingService {

  public static List<CustomerOutstanding> getCustomerOutstandingList(Main main, FilterParameters filterParameters, Integer entityTypeId, District district, Integer isInvoiceWise, Territory territory) {

    String sql = "SELECT 1 as pos,T4.id,T4.title as name,T4.entity_id as customer_id ,T3.id as tran_id,T3.entity_id,T3.accounting_entity_type_id as entityTypeId, COALESCE(T3.ref_document_no,T3.document_number) as invoiceno, \n"
            + " T3.entry_date as invoicedate, NOW()::date-T3.entry_date::date as daysOutstanding, \n"
            + "CASE WHEN (T2.credit <= 0 or T2.credit is null) THEN T1.net_amount ELSE -T1.net_amount END as invoiceamount,\n"
            + "CASE WHEN (T2.credit <= 0 or T2.credit is null) THEN T1.balance_amount ELSE -T1.balance_amount END as oustandingamount,\n"
            + "(select name as agent_name from scm_sales_agent where id = ( select company_sales_agent_person_profile_id from scm_sales_invoice \n"
            + "where id = T3.entity_id and T3.accounting_entity_type_id=7))"
            //  + "T3.narration "
            //  + ",T3.account_group_id ,\n"
            //  + "T3.ledger_credit_id ,T3.ledger_debit_id\n"
            + " ,T6.district_name as district,T7.title as voucher_type,T5.phone_1,T5.phone_2,T5.address,T7.id as voucher_type_id \n";

    String sqlFrom = "FROM fin_accounting_transaction_detail_item as T1,\n"
            + "fin_accounting_transaction_detail as T2,\n"
            + "fin_accounting_transaction as T3,\n"
            + "fin_accounting_ledger as T4\n"
            + ",scm_customer_address T5,scm_district T6 ,fin_accounting_voucher_type T7\n";

    String cond = "WHERE T1.accounting_transaction_detail_id = T2.id\n"
            + "AND T2.accounting_transaction_id = T3.id and T3.company_id=? \n"
            + "and T3.id not in(select id from fin_accounting_transaction \n"
            + "where entity_id in(select id from scm_sales_invoice where company_id=? and sales_invoice_status_id=0) and accounting_entity_type_id=?)\n"
            + "AND T4.id = T2.accounting_ledger_id AND T3.entry_date::date<=?::date\n";

    main.clear();
    main.param(UserRuntimeView.instance().getCompany().getId());
    main.param(UserRuntimeView.instance().getCompany().getId());
    main.param(AccountingConstant.ACC_ENTITY_SALES.getId());
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));

    if (filterParameters.getAccountGroup() != null && filterParameters.getAccount() == null) {
      cond += " and T3.account_group_id = ?";
      main.param(filterParameters.getAccountGroup().getId());
    }

    if (filterParameters.getSalesAgent() != null) {
      // cond += " T3.entity_id in( select id from scm_sales_invoice where company_sales_agent_person_profile_id = ? AND company_id = ? ) AND ";
      //Changed as per sales agents all customer outstanding is shown
      cond += " and T5.customer_id in( select customer_id from scm_sales_invoice where company_sales_agent_person_profile_id = ? AND company_id = ? )";
      main.param(filterParameters.getSalesAgent().getId());
      main.param(UserRuntimeView.instance().getCompany().getId());
    }

    if (territory != null && territory.getId() != null) {
      cond += " and T5.district_id in(select district_id from scm_territory_district where territory_id =?)";
      main.param(territory.getId());
    }

    if (district != null) {
      cond += " and T6.id=?";
      main.param(district.getId());
    }

    if (filterParameters.getCustomer() != null) {
      cond += " and T5.customer_id = ?";
      main.param(filterParameters.getCustomer().getId());
    }

    if (filterParameters.getAccount() != null) {
      cond += " and T3.id IN(SELECT accounting_transaction_id FROM fin_trade_outstanding WHERE account_id  = ?)";
      main.param(filterParameters.getAccount().getId());
    }

    if (filterParameters.getProductHash() != null) {
      cond += " and T3.id IN(\n"
              + "SELECT id FROM fin_accounting_transaction WHERE account_group_id =? AND voucher_type_id =4 AND  entity_id IN(\n"
              + "SELECT SaleInv.id FROM scm_sales_invoice as SaleInv,scm_sales_invoice_item as SaleInvItm WHERE \n"
              + "SaleInv.account_group_id =? AND SaleInv.id =SaleInvItm.sales_invoice_id AND SaleInv.sales_invoice_status_id>=2 AND \n"
              + "SaleInvItm.product_detail_id IN(SELECT id FROM scm_product_detail WHERE product_batch_id\n"
              + "IN(SELECT id FROM scm_product_batch WHERE product_id IN(SELECT CAST (id as INTEGER) FROM regexp_split_to_table(?, E'\\\\#') as id) )))\n"
              + "UNION \n"
              + "SELECT id FROM fin_accounting_transaction WHERE  account_group_id =? AND voucher_type_id =6 AND  entity_id IN(\n"
              + "SELECT SaleRet.id FROM scm_sales_return as SaleRet,scm_sales_return_item as SaleRetItm WHERE \n"
              + "SaleRet.account_group_id =? AND SaleRet.id = SaleRetItm.sales_return_id AND SaleRet.sales_return_status_id =2 AND \n"
              + "SaleRetItm.product_detail_id IN(SELECT id FROM scm_product_detail WHERE product_batch_id\n"
              + "IN(SELECT id FROM scm_product_batch WHERE product_id IN(SELECT CAST (id as INTEGER) FROM regexp_split_to_table(?, E'\\\\#') as id) )))\n"
              + ")";
      main.param(filterParameters.getAccountGroup().getId());
      main.param(filterParameters.getAccountGroup().getId());
      main.param(filterParameters.getProductHash());
      main.param(filterParameters.getAccountGroup().getId());
      main.param(filterParameters.getAccountGroup().getId());
      main.param(filterParameters.getProductHash());

    }
    cond += " and T5.customer_id=T4.entity_id and T5.district_id=T6.id AND T7.id=T3.voucher_type_id and T5.address_type_id =1 and T1.status in (?,?) and T4.accounting_entity_type_id=? ";
    main.param(AccountingConstant.STATUS_NEW);
    main.param(AccountingConstant.STATUS_PARTIAL_PROCESSED);
    main.param(entityTypeId);

//    main.param(UserRuntimeView.instance().getCompany().getId());
    //Main coustomer outstanding query ends
    //Cheque in hand query is added
    String sqlCheque = "SELECT 2 as pos,ce.id,T4.title as name,T4.entity_id as customer_id,ce.id as tran_id,null as entity_id,null as entityTypeId,ce.cheque_no as invoiceno,ce.cheque_date as invoicedate,NOW()::date-COALESCE(ce.cheque_date::date,NOW()::date) as daysoutstanding,0 as invoiceamount,0-ce.amount as oustandingamount,'' as agent_name,\n"
            + "T6.district_name as district,'Cheque In Hand' as voucher_type,T5.phone_1,T5.phone_2,T5.address,null as voucher_type_id FROM fin_cheque_entry ce,fin_accounting_ledger T4,\n"
            + "scm_customer_address T5, scm_district T6 where ce.ledger_id = T4.id and ce.company_id=? and T4.company_id=? and T5.customer_id=T4.entity_id and T5.district_id=T6.id AND T5.address_type_id =1 \n"
            + "AND T4.accounting_entity_type_id=? AND ce.cheque_date::date<=?::date and ce.status_id in (?,?)";

    main.param(UserRuntimeView.instance().getCompany().getId());
    main.param(UserRuntimeView.instance().getCompany().getId());
    main.param(entityTypeId);
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    main.param(AccountingConstant.BANK_CHEQUE_RECIEVED);
    main.param(AccountingConstant.BANK_CHEQUE_BOUNCE_RESUBMITTED);
    String sqlChequeCond = "";

    if (filterParameters.getAccountGroup() != null && filterParameters.getAccount() == null) {
      sqlChequeCond += " and ce.account_group_id = ?";
      main.param(filterParameters.getAccountGroup().getId());
    }

    if (filterParameters.getSalesAgent() != null) {
      // cond += " T3.entity_id in( select id from scm_sales_invoice where company_sales_agent_person_profile_id = ? AND company_id = ? ) AND ";
      //Changed as per sales agents all customer outstanding is shown
      sqlChequeCond += " and T5.customer_id in( select customer_id from scm_sales_invoice where company_sales_agent_person_profile_id = ? AND company_id = ? )";
      main.param(filterParameters.getSalesAgent().getId());
      main.param(UserRuntimeView.instance().getCompany().getId());
    }

    if (territory != null && territory.getId() != null) {
      sqlChequeCond += " and T5.district_id in(select district_id from scm_territory_district where territory_id =?)";
      main.param(territory.getId());
    }

    if (district != null) {
      sqlChequeCond += " and T6.id=?";
      main.param(district.getId());
    }

    if (filterParameters.getCustomer() != null) {
      sqlChequeCond += " and T5.customer_id = ?";
      main.param(filterParameters.getCustomer().getId());
    }

    if (filterParameters.getAccount() != null) {
      //Ignored, Not applicable to cheque in hand
    }

    if (filterParameters.getProductHash() != null) {
      //Ignored Not applicable to cheque in hand
    }
    String sqlOrderBy = "";
    if (isInvoiceWise != null && isInvoiceWise.intValue() == SystemConstants.SHOW_AGE_WISE) {
      sqlOrderBy = " ) tab order by tab.district,tab.name,tab.daysOutstanding desc,tab.agent_name";
    } else {
      sqlOrderBy = " ) tab order by tab.name,tab.pos,tab.daysOutstanding desc,tab.invoicedate desc";
    }

    sql = "select * from (" + sql + sqlFrom + cond + " union " + sqlCheque + sqlChequeCond + sqlOrderBy;
    return AppDb.getList(main.dbConnector(), CustomerOutstanding.class, sql, main.getParamData().toArray());

  }

  public static List<AccountGroup> selectAccountGroupByCustomer(Main main, Customer customer, String filter) {

    List<Object> params = new ArrayList<>();
    params.add(customer.getId());
    String sql = "\n"
            + "SELECT t3.id,t3.group_name,t3.group_code fROM fin_trade_outstanding t1,scm_account_group_detail t2,scm_account_group t3\n"
            + "WHERE t1.account_id = t2.account_id\n"
            + "AND t3.id = t2.account_group_id\n"
            + "AND t1.customer_id = ? \n";
    if (!StringUtils.isEmpty(filter)) {
      sql += " AND upper(t3.group_name) like ? ";
      params.add("%" + filter.toUpperCase() + "%");
    }
    sql += " GROUP BY t3.id,t3.group_name,t3.group_code";

    return AppService.list(main, AccountGroup.class, sql, params.toArray());

  }

  public static List<District> selectDistrict(Main main) {
    return AppService.list(main, District.class, "select * from scm_district", new Object[]{});
  }

  public static List<District> selectDistrict(Main main, SalesAgent salesAgent, Territory territory) {
    List<Object> params = new ArrayList<>();
    String sql = "select T1.id,T1.district_name from scm_district T1,scm_territory_district T2,scm_sales_agent_territory T3\n"
            + "WHERE \n"
            + " T3.territory_id = T2.territory_id\n"
            + "AND T1.id = T2.district_id   \n";
    if (salesAgent != null && salesAgent.getId() != null) {
      sql += " AND T3.sales_agent_id = ? ";
      params.add(salesAgent.getId());
    }
    if (territory != null && territory.getId() != null) {
      sql += " AND T3.territory_id = ? ";
      params.add(territory.getId());
    }
    String group = " group by T1.id,T1.district_name ";
    String order = " order by T1.district_name ASC";

    return AppService.list(main, District.class, sql + group + order, params.toArray());
  }

  public static List<District> selectDistrictByAccountGroup(MainView main, AccountGroup accountGroup) {
    String sql = "select T1.id,T1.district_name from scm_district T1\n"
            + "left join scm_sales_account T2 on T2.account_group_id = ? \n"
            + "left join scm_customer T3 on T3.id = T2.customer_id \n"
            + "left join scm_customer_address T4 on T4.customer_id = T3.id\n"
            + "where T4.district_id = T1.id GROUP BY T1.id,T1.district_name Order by T1.district_name ASC";
    return AppService.list(main, District.class, sql, new Object[]{accountGroup.getId()});
  }

  public static List<Territory> slectTerritoryList(MainView main, AccountGroup accountGroup, Integer companyId) {
    List<Object> params = new ArrayList<>();
    String sql = "select t.id,t.territory_name from scm_territory t where t.id in (\n"
            + " select t1.territory_id from scm_territory_district t1,scm_customer_address t2,scm_customer t3\n"
            + "where t1.district_id = t2.district_id\n"
            + "and t2.customer_id = t3.id\n"
            + "and t3.company_id = ? \n";
    params.add(companyId);
    if (accountGroup != null && accountGroup.getId() != null) {
      sql += " and t3.id in (select customer_id FROM scm_sales_account t4 WHERE t4.account_group_id = ? AND t4.company_id = ? ) ";
      params.add(accountGroup.getId());
      params.add(companyId);
    }
    sql += " ) and t.company_id = ? ";
    params.add(companyId);
    return AppService.list(main, Territory.class, sql, params.toArray());
  }

  public static List<Product> getProductList(Main main, AccountGroup accountGroup) {
    List<Object> params = new ArrayList<>();
    params.add(accountGroup.getId());
    params.add(accountGroup.getId());
    String sql = "SELECT * FROM scm_product WHERE id IN(\n"
            + "SELECT product_id FROM scm_product_batch WHERE id IN(\n"
            + "SELECT product_batch_id FROM scm_product_detail WHERE account_id IN(\n"
            + "SELECT account_id FROM scm_account_group_detail WHERE account_group_id =?))) "
            + " AND brand_id IN(SELECT brand_id FROM scm_account_group_brands WHERE account_group_id =?) ORDER BY UPPER(product_name)";
    return AppService.list(main, Product.class, sql, params.toArray());
  }
}
