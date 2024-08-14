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
import spica.reports.model.SupplierOutstanding;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Vendor;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;

/**
 *
 * @author sujesh
 */
public abstract class SupplierOutStandingService {

  public static List selectOutstandingSuppliersList(Main main, Company company, FilterParameters filterParameters) {

    String sql = "SELECT t2.vendor_name as supplier,t1.document_no as document,t1.entry_date as invoiceDate,t1.value_after_tax as invoiceAmount,\n"
            + "t1.value_remaining_balance as oustandingAmount,NOW()::date-t1.entry_date::date as daysOutstanding,\n"
            + "t1.is_debit_or_credit as  debitOrCredit \n"
            + "from fin_trade_outstanding t1,scm_vendor t2\n"
            + "where t1.entity_type_id in (?,?,?,?) \n"
            + "AND t1.vendor_id=t2.id ANd t1.customer_id IS NULL "
            + "AND t2.company_id=? AND t1.value_remaining_balance>=1 AND t1.status=? AND t1.value_remaining_balance>0 \n";
    main.clear();
    main.param(AccountingConstant.ACC_ENTITY_PURCHASE.getId());
    main.param(AccountingConstant.ACC_ENTITY_PURCHASE_RETURN.getId());
    main.param(AccountingConstant.ACC_ENTITY_DEBIT_CREDIT_NOTE.getId());
    main.param(AccountingConstant.ACC_ENTITY_VENDOR.getId());

    main.param(company.getId());
    main.param(AccountingConstant.STATUS_NEW);

    if (filterParameters.getAccountGroup() != null && filterParameters.getAccount() == null) {
      sql += " AND t1.account_id IN(SELECT account_id FROM scm_account_group_detail WHERE account_group_id = ?) ";
      main.param(filterParameters.getAccountGroup().getId());
    }
    if (filterParameters.getAccount() != null) {
      sql += " AND t1.account_id = ? ";
      main.param(filterParameters.getAccount().getId());
    }

    if (filterParameters.getVendor() != null) {
      sql += " AND t1.vendor_id = ? ";
      main.param(filterParameters.getVendor().getId());
    }

    sql += " order by (NOW()::date-t1.entry_date::date) asc ";
    return AppDb.getList(main.dbConnector(), SupplierOutstanding.class, sql, main.getParamData().toArray());
  }

  public static List<Vendor> selectVendorAuto(MainView main, String filter, Company company) {
    if (company.getId() == null) {
      return null;
    }

    String sql = "SELECT t2.vendor_name ,t2.id \n"
            + "from fin_trade_outstanding t1,scm_vendor t2\n"
            + "where t1.entity_type_id in (?,?,?,?) \n"
            + "AND t1.vendor_id=t2.id ANd t1.customer_id IS NULL "
            + "AND t2.company_id=? AND t1.value_remaining_balance>=1 AND t1.status=? AND t1.value_remaining_balance>0   \n";
    main.clear();

    main.param(AccountingConstant.ACC_ENTITY_PURCHASE.getId());
    main.param(AccountingConstant.ACC_ENTITY_PURCHASE_RETURN.getId());
    main.param(AccountingConstant.ACC_ENTITY_DEBIT_CREDIT_NOTE.getId());
    main.param(AccountingConstant.ACC_ENTITY_VENDOR.getId());

    main.param(company.getId());
    main.param(AccountingConstant.STATUS_NEW);

    if (!StringUtils.isEmpty(filter)) {
      sql += " AND upper(t2.vendor_name) like ? ";
      main.param("%" + filter.toUpperCase() + "%");
    }
    sql += " group by t2.vendor_name ,t2.id order by upper(t2.vendor_name) asc";
    return main.em().list(Vendor.class, sql, main.getParamData().toArray());

  }

  public static List<AccountGroup> selectAccountGroupBySupplier(Main main, Vendor vendor, String filter) {

    main.clear();
    main.param(vendor.getId());
    String sql = "\n"
            + "SELECT t3.id,t3.group_name,t3.group_code fROM fin_trade_outstanding t1,scm_account_group_detail t2,scm_account_group t3\n"
            + "WHERE t1.account_id = t2.account_id\n"
            + "AND t3.id = t2.account_group_id\n"
            + "AND t1.vendor_id = ? \n";
    if (!StringUtils.isEmpty(filter)) {
      sql += " AND upper(t3.group_name) like ? ";
      main.param("%" + filter.toUpperCase() + "%");
    }
    sql += " GROUP BY t3.id,t3.group_name,t3.group_code order by upper(t3.group_name) asc";

    return AppService.list(main, AccountGroup.class, sql, main.getParamData().toArray());

  }
}
