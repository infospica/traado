/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import spica.reports.model.CustomerLedgerReport;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Customer;
import spica.scm.domain.District;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.AppLookup;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;

/**
 *
 * @author godson
 */
public class CustomerLedgerReportService {

  public static List<CustomerLedgerReport> createCustomerLedgerReportList(MainView main, FilterParameters filterParameters) {
    List<Object> params = new ArrayList<Object>();
    String sql = "SELECT \n"
            + "AccLed.title as customer_name,COALESCE(AccGrp.group_name,'') as account_group,?::date as acc_date,'Opening Entry' as trans_title,'' as voucher_type,\n"
            + "'' as document_number,'' as ref_document_no,'' as narration,SUM(COALESCE(AccTransDet.debit,0)) as debit_amount,SUM(COALESCE(AccTransDet.credit,0)) as credit_amount,1 as is_opening\n"
            + "FROM \n"
            + "fin_accounting_transaction as AccTrans LEFT OUTER JOIN scm_account_group as AccGrp ON AccTrans.account_group_id=AccGrp.id,\n"
            + "fin_accounting_voucher_type as AccVouch,\n"
            + "fin_accounting_ledger as AccLed,\n"
            + "fin_accounting_transaction_detail as AccTransDet \n"
            + "WHERE \n"
            + "AccVouch.id = AccTrans.voucher_type_id AND\n"
            + "AccTrans.company_id = AccLed.company_id  AND\n"
            + "AccLed.accounting_entity_type_id =2 AND\n"
            + "(AccLed.id = AccTrans.ledger_credit_id OR\n"
            + "AccLed.id = AccTrans.ledger_debit_id )  AND\n"
            + "AccTrans.id = AccTransDet.accounting_transaction_id AND \n"
            + "AccLed.id = AccTransDet.accounting_ledger_id \n"
            + "AND AccTrans.company_id=?\n"
            + "AND AccTrans.entry_date::date < ?::date\n";
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(UserRuntimeView.instance().getCompany().getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));

    if (filterParameters.getAccountGroup() != null) {
      sql += "AND AccTrans.account_group_id =?\n";
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getSalesAgent() != null) {
      sql += "AND AccTrans.account_group_id IN(SELECT account_group_id FROM scm_sales_agent_account_group WHERE sales_agent_id =?) AND "
              + " AccLed.entity_id IN( SELECT customer_id FROM fin_trade_outstanding WHERE sales_agent_id =? and status = 1 and company_id =?)\n";
//              + " AND invoice_date::date < ?::date AND company_sales_agent_person_profile_id = ?)\n";
      params.add(filterParameters.getSalesAgent().getId());
      params.add(filterParameters.getSalesAgent().getId());
      params.add(UserRuntimeView.instance().getCompany().getId());
//      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    }
    if (filterParameters.getDistrict() != null && filterParameters.getDistrict().length > 0) {
      String p = "";
      for (District district : filterParameters.getDistrict()) {
        params.add(district.getId());
        p += "?,";
      }
      p = p.substring(0, p.length() - 1);
      sql += "AND AccLed.entity_id in(SELECT customer_id FROM scm_customer_address where district_id in (" + p + "))";
    }
    if (filterParameters.getCustomer() != null) {
      sql += "AND AccLed.entity_id =?\n";
      params.add(filterParameters.getCustomer().getId());
    }
    sql += "GROUP BY customer_name,account_group,acc_date,trans_title\n"
            + "UNION ALL\n"
            + "SELECT \n"
            + "AccLed.title as customer_name,COALESCE(AccGrp.group_name,'') account_group,AccTrans.entry_date::date as acc_date,\n"
            + "AccLed2.title as trans_title,AccVouch.title as voucher_type,\n"
            + "AccTrans.document_number as document_number,AccTrans.ref_document_no as ref_document_no,AccTrans.narration as acc_narration,\n"
            + "AccTransDet.debit as debit_amount,AccTransDet.credit  as credit_amount,2 as is_opening\n"
            + "FROM \n"
            + "fin_accounting_transaction as AccTrans LEFT OUTER JOIN scm_account_group as AccGrp ON AccTrans.account_group_id=AccGrp.id,\n"
            + "fin_accounting_voucher_type as AccVouch,\n"
            + "fin_accounting_ledger as AccLed,\n"
            + "fin_accounting_ledger as AccLed2,\n"
            + "fin_accounting_transaction_detail as AccTransDet \n"
            + "\n"
            + "WHERE \n"
            + "AccVouch.id = AccTrans.voucher_type_id AND\n"
            + "AccTrans.company_id = AccLed.company_id  AND\n"
            + "AccLed.accounting_entity_type_id =2 AND\n"
            + "((AccLed.id = AccTrans.ledger_credit_id AND AccLed2.id = AccTrans.ledger_debit_id) OR\n"
            + "(AccLed.id = AccTrans.ledger_debit_id AND AccLed2.id = AccTrans.ledger_credit_id))  AND\n"
            + "AccTrans.id = AccTransDet.accounting_transaction_id AND \n"
            + "AccLed.id = AccTransDet.accounting_ledger_id \n"
            + "AND AccTrans.company_id=?\n"
            + "AND AccTrans.entry_date::date >= ?::date AND AccTrans.entry_date::date <= ?::date\n";
    params.add(UserRuntimeView.instance().getCompany().getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getToDate()));
    if (filterParameters.getAccountGroup() != null) {
      sql += "AND AccTrans.account_group_id =?\n";
      params.add(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getSalesAgent() != null) {
      sql += "AND AccTrans.account_group_id IN(SELECT account_group_id FROM scm_sales_agent_account_group WHERE sales_agent_id =?) AND\n"
              + " AccLed.entity_id IN( SELECT customer_id FROM fin_trade_outstanding WHERE sales_agent_id =? and status = 1 and company_id =?)\n";
//              + " AND invoice_date::date < ?::date AND company_sales_agent_person_profile_id = ?)\n";
      params.add(filterParameters.getSalesAgent().getId());
      params.add(filterParameters.getSalesAgent().getId());
      params.add(UserRuntimeView.instance().getCompany().getId());
//      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    }
    if (filterParameters.getDistrict() != null && filterParameters.getDistrict().length > 0) {
      String p = "";
      for (District district : filterParameters.getDistrict()) {
        params.add(district.getId());
        p += "?,";
      }
      p = p.substring(0, p.length() - 1);
      sql += "AND AccLed.entity_id in(SELECT customer_id FROM scm_customer_address where district_id in (" + p + "))";
    }
    if (filterParameters.getCustomer() != null) {
      sql += "AND AccLed.entity_id =?\n";
      params.add(filterParameters.getCustomer().getId());
    }
    sql += "ORDER BY 1,2,3";
    return AppDb.getList(main.dbConnector(), CustomerLedgerReport.class, sql, params.toArray());
  }

  public static List<District> districtListbyCompany(String filter) {
    String sql = "SELECT scm_district.* FROM scm_district where id in"
            + "(SELECT scm_customer_address.district_id FROM scm_customer_address,scm_customer where scm_customer_address.customer_id=scm_customer.id \n"
            + "and scm_customer.company_id = ? group by scm_customer_address.district_id ) order by upper(scm_district.district_name) asc";
    return AppLookup.getAutoFilter(District.class, sql, new Object[]{UserRuntimeView.instance().getCompany().getId()});
  }

  public static List<District> districtListbyAccountGroup(FilterParameters filterParameters, String filter) {
    String sql = "SELECT scm_district.* FROM scm_district where id in"
            + "(select district_id from scm_customer_address where customer_id in(select customer.id from scm_customer customer inner join \n"
            + "scm_sales_account sales_account on customer.id = sales_account.customer_id \n"
            + "where sales_account.account_group_id = ? order by upper(customer_name) asc)) order by upper(scm_district.district_name) asc";
    return AppLookup.getAutoFilter(District.class, sql, new Object[]{filterParameters.getAccountGroup().getId()});
  }

  public static List<Customer> selectCustomerByDistrict(FilterParameters filterParameters, String filter) {
    List<Object> params = new ArrayList<Object>();
    String p = "";
    for (District district : filterParameters.getDistrict()) {
      p += "?,";
      params.add(district.getId());
    }
    p = p.substring(0, p.length() - 1);
    String sql = "SELECT c.* FROM scm_customer c where id in(SELECT customer_id FROM scm_customer_address where district_id in(" + p + ")) \n";

    if (!StringUtils.isEmpty(filter)) {
      sql += " AND upper(c.customer_name) like ? ";
      params.add("%" + filter.toUpperCase() + "%");
    }
    sql += "order by upper(c.customer_name) asc ";
    return AppLookup.getAutoFilter(Customer.class, sql, params.toArray());
  }
}
