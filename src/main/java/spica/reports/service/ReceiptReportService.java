/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.service;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import spica.constant.AccountingConstant;
import spica.reports.model.FilterParameters;
import spica.reports.model.ReceiptReport;
import spica.scm.domain.Customer;
import spica.scm.domain.District;
import spica.scm.domain.Territory;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;

/**
 *
 * @author sujesh
 */
public class ReceiptReportService {

  public static List<ReceiptReport> selectReceiptReportList(Main main, Integer companyId, FilterParameters filterParameters, Territory territory, Integer entityTypeId, District district, Integer status) {
    // List<Object> params = new ArrayList<>();
    String sql = " SELECT T1.document_number as cheque_no,T1.document_date as cheque_date,T2.customer_id,T2.customer,T2.customer_address,T2.invoiceno,T2.fin_doc_no,"
            + "T2.entry_date,T2.narration,T2.processed_at,T1.net_amount as amount\n"
            + "FROM\n"
            + "fin_accounting_transaction_detail_item T1,\n"
            + "(SELECT DISTINCT fin_accounting_transaction_detail.id as accounting_transaction_detail_id,fin_accounting_transaction.ref_document_no as invoiceno, fin_accounting_transaction.document_number as fin_doc_no,\n"
            + "fin_accounting_ledger.entity_id as customer_id, fin_accounting_ledger.title as customer,scm_customer_address.address as customer_address"
            + ",fin_accounting_transaction.entry_date,fin_accounting_transaction.narration,fin_accounting_transaction.processed_at\n"
            + "FROM fin_accounting_transaction_detail,fin_accounting_transaction,fin_accounting_ledger,\n"
            + "scm_customer_address,scm_territory_district,scm_bank\n"
            + "WHERE fin_accounting_transaction.id = fin_accounting_transaction_detail.accounting_transaction_id\n"
            + "AND fin_accounting_ledger.id = fin_accounting_transaction_detail.accounting_ledger_id\n"
            + "AND fin_accounting_ledger.entity_id = scm_customer_address.customer_id\n"
            + "AND scm_customer_address.district_id = scm_territory_district.district_id AND\n"
            + "fin_accounting_transaction.company_id =? AND fin_accounting_transaction.voucher_type_id =? ";

    String where = ") as T2 where T1.accounting_transaction_detail_id =T2.accounting_transaction_detail_id";

    main.clear();
    main.param(companyId);
    main.param(AccountingConstant.VOUCHER_TYPE_RECEIPT.getId());
    if (filterParameters.getFromDate() != null && filterParameters.getToDate() != null) {
      sql += " AND fin_accounting_transaction.entry_date::date >= ?::date AND fin_accounting_transaction.entry_date <=?::date \n";
      main.param(filterParameters.getFromDate());
      main.param(filterParameters.getToDate());
    }
    if (filterParameters.getAccountGroup() != null) {
      sql += " AND fin_accounting_transaction.account_group_id = ? ";
      main.param(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getCustomer() != null) {
      sql += " AND fin_accounting_ledger.entity_id = ? ";
      main.param(filterParameters.getCustomer().getId());
    }
    if (territory != null) {
      sql += " AND scm_territory_district.territory_id = ? ";
      main.param(territory.getId());
    }
    if (status != null) {
      where += " AND T1.status = ?";
      main.param(status);
    }

    String group = " order by 4, 2 , 1";
    String query = sql + where + group;
    return AppDb.getList(main.dbConnector(), ReceiptReport.class, query, main.getParamData().toArray());
  }

  public static List<Customer> selectCustomerAuto(Main main, Integer companyId, FilterParameters filterParameters, String filter) {
    String sql = "select t3.id,t3.customer_name\n"
            + "from fin_accounting_transaction t1\n"
            + "left join fin_accounting_ledger t2 on t1.ledger_credit_id = t2.id\n"
            + "left join scm_customer t3 on t2.entity_id = t3.id\n"
            + "where t1.voucher_type_id = ? and t1.company_id = ? and t2.accounting_entity_type_id = ? \n";

    main.clear();
    main.param(AccountingConstant.VOUCHER_TYPE_RECEIPT.getId());
    main.param(companyId);
    main.param(AccountingConstant.ACC_ENTITY_CUSTOMER.getId());
    if (filterParameters != null) {
      if (filterParameters.getAccountGroup() != null) {
        sql += " and t1.account_group_id = ? ";
        main.param(filterParameters.getAccountGroup().getId());
      }
      if (filterParameters.getFromDate() != null && filterParameters.getToDate() != null) {
        sql += " AND t1.entry_date::date >= ?::date AND t1.entry_date <=?::date";
        main.param(filterParameters.getFromDate());
        main.param(filterParameters.getToDate());
      }
      if (filter != null) {
        sql += " AND upper(t3.customer_name) like upper(?) ";
        filter = "%" + filter + "%";
        main.param(filter);
      }
    }
    sql += " group by t3.id,t3.customer_name order by customer_name asc";
    return AppService.list(main, Customer.class, sql, main.getParamData().toArray());
  }

  public static List selectTerritoryByCustomer(Main main, List<Integer> customerList, Integer companyId) {

    String p = "";
    String sql = "select t1.id,t1.territory_name from scm_territory t1\n"
            + "left join scm_territory_district t2 on t2.territory_id = t1.id\n"
            + "left join scm_customer_address t3 on t3.district_id = t2.district_id\n"
            + "left join scm_customer t4 on t4.id = t3.customer_id\n";
    main.clear();
    for (Integer id : customerList) {
      p += "?,";
      main.param(id);
    }
    String result = StringUtils.chop(p);
    sql += "where t4.id in(" + result + ") and t1.company_id = ? group by t1.id , t1.territory_name order by t1.territory_name asc \n";
    main.param(companyId);
    return AppService.list(main, Territory.class, sql, main.getParamData().toArray());

  }

}
