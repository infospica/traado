/*
 * @(#)PlatformService.java	1.0 Thu Apr 20 15:04:15 IST 2017
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.domain.DebitCreditNote;
import spica.scm.domain.Account;
import spica.scm.domain.Platform;
import spica.scm.domain.PlatformDescription;
import spica.scm.domain.PlatformSource;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.fin.domain.VendorClaim;
import spica.reports.model.CompanyCustomerSales;
import spica.reports.model.FilterParameters;
import spica.scm.common.PlatformSummary;
import spica.scm.domain.Company;
import spica.scm.domain.ProductMrpAdjustment;
import spica.scm.domain.ProductMrpAdjustmentItem;
import spica.scm.domain.ProductPricelist;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItem;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.validate.PlatformIs;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.util.StringUtil;

/**
 * PlatformService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:15 IST 2017
 */
public abstract class PlatformService {

  public static final Integer NORMAL_FUND_STATE = null;
  public static final Integer UNREALIZED_FUND_STATE = 0;
  public static final Integer REALIZED_FUND_STATE = 1;
  public static final int PLATFORM_DEBIT = 1;
  public static final int PLATFORM_CREDIT = 2;

  public static final String PLATFORM_SQL = "select scm_platform.id,scm_platform.document_no,scm_platform.parent_id,scm_platform.source_id,scm_platform.platform_desc_id,"
          + "scm_platform.credit_amount_required,scm_platform.debit_amount_required,scm_platform.account_id,scm_platform.customer_id,scm_platform.product_entry_id,"
          + "scm_platform.product_entry_detail_id,scm_platform.sales_invoice_id,scm_platform.sales_invoice_item_id,scm_platform.debit_credit_note_id,"
          + "scm_platform.sales_return_id,scm_platform.sales_return_item_id,scm_platform.status_id,"
          + "scm_platform.created_by,scm_platform.modified_by,scm_platform.product_detail_id,scm_platform.entry_date,"
          + "scm_platform.created_at,scm_platform.modified_at from scm_platform scm_platform ";

  public static final String PLATFORM_DESCRIPTION_SQL = "select distinct on (scm_platform.platform_desc_id) scm_platform.platform_desc_id,scm_platform.id,scm_platform.document_no,"
          + "scm_platform.parent_id,scm_platform.product_detail_id,scm_platform.entry_date,scm_platform.source_id,"
          + "sum(scm_platform.credit_amount_required) OVER (PARTITION BY scm_platform.platform_desc_id) credit_amount_required, "
          + "sum(scm_platform.debit_amount_required) OVER (PARTITION BY scm_platform.platform_desc_id) debit_amount_required, "
          + "scm_platform.account_id,scm_platform.customer_id,scm_platform.product_entry_id,scm_platform.product_entry_detail_id,scm_platform.sales_invoice_id,scm_platform.debit_credit_note_id, "
          + "scm_platform.sales_invoice_item_id,scm_platform.sales_return_id,scm_platform.sales_return_item_id,scm_platform.status_id,"
          + "scm_platform.created_by,scm_platform.modified_by,scm_platform.created_at,scm_platform.modified_at "
          + "from scm_platform scm_platform ";

  public static final String PLATFORM_ITEM_SQL = "select distinct on (scm_platform.sales_invoice_id,scm_platform.product_entry_id,scm_platform.sales_return_id) scm_platform.platform_desc_id,scm_platform.id,"
          + "scm_platform.document_no,scm_platform.product_detail_id,scm_platform.entry_date,scm_platform.parent_id,scm_platform.source_id,"
          + "sum(scm_platform.credit_amount_required) OVER (PARTITION BY scm_platform.sales_invoice_id,scm_platform.product_entry_id,scm_platform.sales_return_id,scm_platform.debit_credit_note_id) credit_amount_required, "
          + "sum(scm_platform.debit_amount_required) OVER (PARTITION BY scm_platform.sales_invoice_id,scm_platform.product_entry_id,scm_platform.sales_return_id,scm_platform.debit_credit_note_id) debit_amount_required, "
          + "scm_platform.account_id,scm_platform.customer_id,scm_platform.product_entry_id,scm_platform.product_entry_detail_id,scm_platform.sales_invoice_id,scm_platform.debit_credit_note_id,"
          + "scm_platform.sales_invoice_item_id,scm_platform.sales_return_id,scm_platform.sales_return_item_id,scm_platform.status_id,scm_platform.created_by,scm_platform.modified_by,"
          + "scm_platform.created_at,scm_platform.modified_at from scm_platform scm_platform ";

  /**
   * Platform paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPlatformPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_platform", Platform.class, main);
    sql.main("select distinct on(scm_platform.document_no) scm_platform.id,scm_platform.document_no,scm_platform.parent_id,scm_platform.source_id,scm_platform.platform_desc_id,scm_platform.credit_amount_required,sum(scm_platform.debit_amount_required) as debit_amount_required,"
            + "scm_platform.account_id,scm_platform.customer_id,scm_platform.product_entry_id,scm_platform.product_entry_detail_id,scm_platform.sales_invoice_id,scm_platform.debit_credit_note_id,"
            + "scm_platform.sales_invoice_item_id,scm_platform.status_id,scm_platform.created_by,scm_platform.modified_by,"
            + "scm_platform.sales_return_id,scm_platform.sales_return_item_id,scm_platform.product_detail_id,scm_platform.entry_date,"
            + "scm_platform.created_at,scm_platform.modified_at from scm_platform scm_platform "); //Main query
    sql.count("select count(scm_platform.id) as total from scm_platform scm_platform "); //Count query
    sql.join("left outer join scm_platform scm_platformparent_id on (scm_platformparent_id.id = scm_platform.parent_id) "
            + "left outer join scm_platform_source scm_platformsource_id on (scm_platformsource_id.id = scm_platform.source_id) "
            + "left outer join scm_platform_description scm_platformplatform_desc_id on (scm_platformplatform_desc_id.id = scm_platform.platform_desc_id) "
            + "left outer join scm_account scm_platformaccount_id on (scm_platformaccount_id.id = scm_platform.account_id) "
            + "left outer join scm_customer scm_platformcustomer_id on (scm_platformcustomer_id.id = scm_platform.customer_id) "
            + "left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_platform.product_detail_id) "
            + "left outer join scm_product_batch scm_product_batch on (scm_product_batch.id = scm_product_detail.product_batch_id) "
            + "left outer join scm_product scm_product on (scm_product.id = scm_product_batch.product_id) "
            + "left outer join scm_product_entry scm_platformproduct_entry_id on (scm_platformproduct_entry_id.id = scm_platform.product_entry_id) "
            + "left outer join scm_product_entry_detail scm_platformproduct_entry_detail_id on (scm_platformproduct_entry_detail_id.id = scm_platform.product_entry_detail_id) "
            + "left outer join scm_sales_invoice scm_platformsales_invoice_id on (scm_platformsales_invoice_id.id = scm_platform.sales_invoice_id) "
            + "left outer join scm_sales_invoice_item scm_platformsales_invoice_item_id on (scm_platformsales_invoice_item_id.id = scm_platform.sales_invoice_item_id) "
            //  + "left outer join scm_platform_settlement scm_platformplatform_settlement_id on (scm_platformplatform_settlement_id.id = scm_platform.platform_settlement_id) "
            + "left outer join scm_platform_status scm_platformstatus_id on (scm_platformstatus_id.id = scm_platform.status_id)"); //Join Query
    sql.string(new String[]{"scm_platform.document_no", "scm_product.product_name", "scm_product_batch.batch_no", "scm_platformsource_id.short_code", "scm_platformplatform_desc_id.title",
      "scm_platformaccount_id.account_code", "scm_platformcustomer_id.customer_name", "scm_platformproduct_entry_id.account_invoice_no", "scm_platformsales_invoice_id.invoice_no",
      "scm_platformstatus_id.title", "scm_platform.created_by", "scm_platform.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_platform.id", "scm_platformparent_id.credit_amount_required", "scm_platform.credit_amount_required", "scm_platform.debit_amount_required", "scm_platformproduct_entry_detail_id.product_quantity", "scm_platformsales_invoice_item_id.product_qty"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_platform.created_at", "scm_platform.entry_date", "scm_platform.modified_at"});  //Date search or sort fields
    return sql;
  }

  private static SqlPage getPlatformPagedSummary(Main main) {
    SqlPage sql = AppService.sqlPage("scm_platform", Platform.class, main);
    sql.main("select distinct on (scm_platform.platform_desc_id) scm_platform.platform_desc_id,scm_platform.id,scm_platform.document_no,scm_platform.parent_id,scm_platform.product_detail_id,scm_platform.entry_date, "
            + "scm_platform.source_id,sum(scm_platform.credit_amount_required) OVER (PARTITION BY scm_platform.platform_desc_id) credit_amount_required,\n"
            + "sum(scm_platform.debit_amount_required) OVER (PARTITION BY scm_platform.platform_desc_id) debit_amount_required,\n"
            + "scm_platform.account_id,scm_platform.customer_id,scm_platform.product_entry_id,scm_platform.product_entry_detail_id,scm_platform.sales_invoice_id,scm_platform.sales_invoice_item_id,scm_platform.debit_credit_note_id,"
            + "scm_platform.sales_return_id,scm_platform.sales_return_item_id,scm_platform.status_id,scm_platform.created_by,scm_platform.modified_by,scm_platform.created_at,scm_platform.modified_at \n"
            + "from scm_platform scm_platform "); //Main query
    sql.count("select count(scm_platform.id) as total from scm_platform scm_platform "); //Count query
    sql.join("left outer join scm_platform scm_platformparent_id on (scm_platformparent_id.id = scm_platform.parent_id) "
            + "left outer join scm_platform_source scm_platformsource_id on (scm_platformsource_id.id = scm_platform.source_id) "
            + "left outer join scm_platform_description scm_platformplatform_desc_id on (scm_platformplatform_desc_id.id = scm_platform.platform_desc_id) "
            + "left outer join scm_account scm_platformaccount_id on (scm_platformaccount_id.id = scm_platform.account_id) "
            + "left outer join scm_customer scm_platformcustomer_id on (scm_platformcustomer_id.id = scm_platform.customer_id) "
            + "left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_platform.product_detail_id) "
            + "left outer join scm_product_batch scm_product_batch on (scm_product_batch.id = scm_product_detail.product_batch_id) "
            + "left outer join scm_product scm_product on (scm_product.id = scm_product_batch.product_id) "
            + "left outer join scm_product_entry scm_platformproduct_entry_id on (scm_platformproduct_entry_id.id = scm_platform.product_entry_id) "
            + "left outer join scm_product_entry_detail scm_platformproduct_entry_detail_id on (scm_platformproduct_entry_detail_id.id = scm_platform.product_entry_detail_id) "
            + "left outer join scm_sales_invoice scm_platformsales_invoice_id on (scm_platformsales_invoice_id.id = scm_platform.sales_invoice_id) "
            + "left outer join scm_sales_invoice_item scm_platformsales_invoice_item_id on (scm_platformsales_invoice_item_id.id = scm_platform.sales_invoice_item_id) "
            //   + "left outer join scm_platform_settlement scm_platformplatform_settlement_id on (scm_platformplatform_settlement_id.id = scm_platform.platform_settlement_id) "
            + "left outer join scm_platform_status scm_platformstatus_id on (scm_platformstatus_id.id = scm_platform.status_id)"); //Join Query
    sql.string(new String[]{"scm_platform.document_no", "scm_product.product_name", "scm_product_batch.batch_no", "scm_platformsource_id.short_code", "scm_platformplatform_desc_id.title", "scm_platformaccount_id.account_code", "scm_platformcustomer_id.customer_name", "scm_platformproduct_entry_id.invoice_no", "scm_platformsales_invoice_id.invoice_no", "scm_platformstatus_id.title", "scm_platform.created_by", "scm_platform.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_platform.id", "scm_platformparent_id.credit_amount_required", "scm_platform.credit_amount_required", "scm_platform.debit_amount_required", "scm_platformproduct_entry_detail_id.product_quantity", "scm_platformsales_invoice_item_id.product_qty"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_platform.entry_date", "scm_platform.created_at", "scm_platform.modified_at"});  //Date search or sort fields
    return sql;
  }

  private static SqlPage getPlatformPagedSummary1(Main main, String perspective, Integer level, boolean settlement) {
    SqlPage sql = AppService.sqlPage("scm_platform", Platform.class, main);
    String sqlAdd = "scm_platform.document_no";
    if (!settlement) {
      if (perspective != null && StringUtil.equals(SystemConstants.PURCHASE, perspective)) {
        if (level != null && level.intValue() == SystemConstants.FIRST_LEVEL) {
          sqlAdd = " scm_platform.product_entry_id ";
        } else if (level != null && level.intValue() == SystemConstants.SECOND_LEVEL) {
          sqlAdd = " scm_platform.sales_invoice_id,scm_platform.sales_return_id,scm_platform.purchase_return_id,scm_platform.product_mrp_adjustment_id ";
        } else {
          sqlAdd = " scm_platform.product_detail_id ";
        }
      }
    } else {
      sqlAdd = " scm_platform.id ";
    }
    //sql.main("select distinct on (scm_platform.sales_invoice_id,scm_platform.product_entry_id) scm_platform.platform_desc_id,scm_platform.id,scm_platform.document_no,scm_platform.product_detail_id,scm_platform.entry_date,"
    sql.main("select distinct on (" + sqlAdd + ") scm_platform.platform_desc_id,scm_platform.id,scm_platform.document_no,scm_platform.product_detail_id,scm_platform.entry_date,"
            + "scm_platform.parent_id,scm_platform.source_id,sum(scm_platform.credit_amount_required) OVER (PARTITION BY " + sqlAdd + ") credit_amount_required,\n"
            + "sum(scm_platform.debit_amount_required) OVER (PARTITION BY  " + sqlAdd + ") debit_amount_required,\n"
            + "scm_platform.account_id,scm_platform.customer_id,scm_platform.product_entry_id,scm_platform.product_entry_detail_id,scm_platform.sales_invoice_id,scm_platform.sales_invoice_item_id,scm_platform.debit_credit_note_id,"
            + "scm_platform.sales_return_id,scm_platform.sales_return_item_id,scm_platform.purchase_return_id,scm_platform.purchase_return_item_id,"
            + "scm_platform.product_mrp_adjustment_id,\n"
            + "scm_platform.status_id,scm_platform.created_by,scm_platform.modified_by,scm_platform.created_at,scm_platform.modified_at \n"
            + "from scm_platform scm_platform "); //Main query
    sql.count("select count(scm_platform.id) as total from scm_platform scm_platform "); //Count query
    sql.join("left outer join scm_platform scm_platformparent_id on (scm_platformparent_id.id = scm_platform.parent_id) "
            + "left outer join scm_platform_source scm_platformsource_id on (scm_platformsource_id.id = scm_platform.source_id) "
            + "left outer join scm_platform_description scm_platformplatform_desc_id on (scm_platformplatform_desc_id.id = scm_platform.platform_desc_id) "
            + "left outer join scm_account scm_platformaccount_id on (scm_platformaccount_id.id = scm_platform.account_id) "
            + "left outer join scm_customer scm_platformcustomer_id on (scm_platformcustomer_id.id = scm_platform.customer_id) "
            + "left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_platform.product_detail_id) "
            + "left outer join scm_product_batch scm_product_batch on (scm_product_batch.id = scm_product_detail.product_batch_id) "
            + "left outer join scm_product scm_product on (scm_product.id = scm_product_batch.product_id) "
            + "left outer join scm_product_entry scm_platformproduct_entry_id on (scm_platformproduct_entry_id.id = scm_platform.product_entry_id) "
            + "left outer join scm_product_entry_detail scm_platformproduct_entry_detail_id on (scm_platformproduct_entry_detail_id.id = scm_platform.product_entry_detail_id) "
            + "left outer join scm_sales_invoice scm_platformsales_invoice_id on (scm_platformsales_invoice_id.id = scm_platform.sales_invoice_id) "
            + "left outer join scm_sales_invoice_item scm_platformsales_invoice_item_id on (scm_platformsales_invoice_item_id.id = scm_platform.sales_invoice_item_id) "
            //     + "left outer join scm_platform_settlement scm_platformplatform_settlement_id on (scm_platformplatform_settlement_id.id = scm_platform.platform_settlement_id) "
            + "left outer join scm_platform_status scm_platformstatus_id on (scm_platformstatus_id.id = scm_platform.status_id)"); //Join Query
    sql.string(new String[]{"scm_platform.document_no", "scm_product.product_name", "scm_product_batch.batch_no", "scm_platformsource_id.short_code", "scm_platformplatform_desc_id.title", "scm_platformaccount_id.account_code", "scm_platformcustomer_id.customer_name", "scm_platformproduct_entry_id.invoice_no", "scm_platformsales_invoice_id.invoice_no", "scm_platformstatus_id.title", "scm_platform.created_by", "scm_platform.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_platform.id", "scm_platformparent_id.credit_amount_required", "scm_platform.credit_amount_required", "scm_platform.debit_amount_required", "scm_platformproduct_entry_detail_id.product_quantity", "scm_platformsales_invoice_item_id.product_qty"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_platform.entry_date", "scm_platform.created_at", "scm_platform.modified_at"});  //Date search or sort fields
    return sql;
  }

//  private static SqlPage getPlatformFundPaged(Main main) {
//    SqlPage sql = AppService.sqlPage("scm_platform", Platform.class, main);
//    sql.main("select distinct on (scm_platform.parent_id)scm_platform.parent_id, scm_platform.platform_desc_id,scm_platform.id,scm_platform.source_id,\n"
//            + "sum(scm_platform.credit_amount_required) OVER (PARTITION BY scm_platform.parent_id) credit_amount_required,\n"
//            + "sum(scm_platform.debit_amount_required) OVER (PARTITION BY scm_platform.parent_id) debit_amount_required,\n"
//            + "scm_platform.account_id,scm_platform.customer_id,scm_platform.product_entry_id,scm_platform.product_entry_detail_id,scm_platform.sales_invoice_id,scm_platform.sales_invoice_item_id,\n"
//            + "scm_platform.status_id,scm_platform.created_by,scm_platform.modified_by,scm_platform.created_at,scm_platform.modified_at\n"
//            + "from scm_platform scm_platform "); //Main query
//    sql.count("select count(scm_platform.id) as total from scm_platform scm_platform "); //Count query
//    sql.join("left outer join scm_platform scm_platformparent_id on (scm_platformparent_id.id = scm_platform.parent_id) left outer join scm_platform_source scm_platformsource_id on (scm_platformsource_id.id = scm_platform.source_id) left outer join scm_platform_description scm_platformplatform_desc_id on (scm_platformplatform_desc_id.id = scm_platform.platform_desc_id) left outer join scm_account scm_platformaccount_id on (scm_platformaccount_id.id = scm_platform.account_id) left outer join scm_customer scm_platformcustomer_id on (scm_platformcustomer_id.id = scm_platform.customer_id) left outer join scm_product_entry scm_platformproduct_entry_id on (scm_platformproduct_entry_id.id = scm_platform.product_entry_id) left outer join scm_product_entry_detail scm_platformproduct_entry_detail_id on (scm_platformproduct_entry_detail_id.id = scm_platform.product_entry_detail_id) left outer join scm_sales_invoice scm_platformsales_invoice_id on (scm_platformsales_invoice_id.id = scm_platform.sales_invoice_id) left outer join scm_sales_invoice_item scm_platformsales_invoice_item_id on (scm_platformsales_invoice_item_id.id = scm_platform.sales_invoice_item_id) left outer join scm_platform_status scm_platformstatus_id on (scm_platformstatus_id.id = scm_platform.status_id)"); //Join Query
//    sql.string(new String[]{"scm_platformsource_id.short_code", "scm_platformplatform_desc_id.title", "scm_platformaccount_id.account_code", "scm_platformcustomer_id.customer_name", "scm_platformproduct_entry_id.invoice_no", "scm_platformsales_invoice_id.invoice_no", "scm_platformstatus_id.title", "scm_platform.created_by", "scm_platform.modified_by"}); //String search or sort fields
//    sql.number(new String[]{"scm_platform.id", "scm_platformparent_id.credit_amount_required", "scm_platform.credit_amount_required", "scm_platform.debit_amount_required", "scm_platformproduct_entry_detail_id.product_quantity", "scm_platformsales_invoice_item_id.product_qty"}); //Numeric search or sort fields
//    sql.date(new String[]{"scm_platform.created_at", "scm_platform.modified_at"});  //Date search or sort fields
//    return sql;
//  }
  public static final List<Platform> selectPlatformByAccount(Main main, Account accountId, PlatformSource[] selectSource, PlatformDescription[] selectedDescription, Integer selectedStatus, int selectedSummary, Platform p, int platformType) {
    StringBuilder sql = new StringBuilder();
    if (accountId != null) {
      main.clear();
      if (selectedSummary == 1 || selectedSummary == 0) {
        sql.append(PLATFORM_DESCRIPTION_SQL);
      } else if ((p != null && p.getLevel() == SystemConstants.SECOND_LEVEL) || selectedSummary == 2) {
        sql.append(PLATFORM_SQL);
      } else {
        sql.append(PLATFORM_ITEM_SQL);
      }
      Object[] params = getPlatformFilterCondition(sql, accountId.getId(), selectSource, selectedDescription, selectedStatus, selectedSummary, p, platformType);
      List<Platform> list = AppService.list(main, Platform.class, sql.toString(), params);
      return list;
    }
    return null;
  }

  private static Object[] getPlatformFilterCondition(StringBuilder sql, Integer accountId, PlatformSource[] selectSource, PlatformDescription[] selectedDescription, Integer selectedStatus, int selectedSummary, Platform platform, int platformType) {
    ArrayList<Object> params = new ArrayList<>();
    if (PLATFORM_DEBIT == platformType) {
      sql.append(" where scm_platform.credit_amount_required is null and scm_platform.account_id=? and (scm_platform.fund_state is null or scm_platform.fund_state = ?) ");
      params.add(accountId);
      params.add(REALIZED_FUND_STATE);
    } else if (PLATFORM_CREDIT == platformType) {
      sql.append(" where scm_platform.debit_amount_required is null and scm_platform.account_id=? and (scm_platform.fund_state is null or scm_platform.fund_state = ?) ");
      params.add(accountId);
      params.add(REALIZED_FUND_STATE);
    }

    if (!StringUtil.isEmpty(selectSource)) {
      sql.append(" and scm_platform.source_id in (");
      for (int i = 0; i < selectSource.length; i++) {
        if (i > 0) {
          sql.append(",");
        }
        sql.append("?");
        params.add(selectSource[i].getId());
      }
      sql.append(") ");
    }

    if (!StringUtil.isEmpty(selectedDescription)) {
      sql.append(" and scm_platform.platform_desc_id in (");
      for (int i = 0; i < selectedDescription.length; i++) {
        if (i > 0) {
          sql.append(",");
        }
        sql.append("?");
        params.add(selectedDescription[i].getId());
      }
      sql.append(") ");
    }

    sql.append(" and scm_platform.status_id = ? ");
    if (selectedStatus == null || SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId().equals(selectedStatus)) {
      params.add(SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId());
    } else {
      params.add(SystemRuntimeConfig.PLATFORM_STATUS_PROCESSED.getId());
    }

    addCondition(platform, sql, params);
    return params.toArray();
  }

  /**
   * Return List of Platform.
   *
   * @param main
   * @param accountId
   * @param selectSource
   * @param selectedDescription
   * @param selectedStatus
   * @param selectedSummary
   * @param p
   * @return List of Platform
   */
  public static final List<Platform> listPagedCredit(Main main, Account accountId, PlatformSource[] selectSource, PlatformDescription[] selectedDescription, Integer selectedStatus, int selectedSummary, Platform p, ProductEntry productEntry, boolean settlement) {
    return listPagedDebitOrCredit(main, accountId, selectSource, selectedDescription, selectedStatus, selectedSummary, p, productEntry, false, settlement);
  }

  public static final List<Platform> listPagedDebit(Main main, Account accountId, PlatformSource[] selectSource, PlatformDescription[] selectedDescription, Integer selectedStatus, int selectedSummary, Platform p, ProductEntry productEntry, boolean settlement) {
    return listPagedDebitOrCredit(main, accountId, selectSource, selectedDescription, selectedStatus, selectedSummary, p, productEntry, true, settlement);
  }

  private static final List<Platform> listPagedDebitOrCredit(Main main, Account accountId, PlatformSource[] selectSource, PlatformDescription[] selectedDescription, Integer selectedStatus, int selectedSummary, Platform p, ProductEntry productEntry, boolean isDebit, boolean settlement) {
    List<Platform> list = null;
    main.getPageData().setPageSize(1000);
    if (accountId != null) {
      main.clear();
      SqlPage sql = null;
      if (selectedSummary == 1 || selectedSummary == 0) {
        sql = getPlatformPagedSummary(main);
      } else if ((p != null && p.getLevel() == SystemConstants.SECOND_LEVEL) || selectedSummary == 2) {
        sql = getPlatformPagedSummary1(main, SystemConstants.PURCHASE, p.getLevel(), settlement);
      } else {
        sql = getPlatformPagedSummary1(main, SystemConstants.PURCHASE, p.getLevel(), settlement);
      }
      sql.cond("where scm_platform.account_id=? and (scm_platform.fund_state is null or scm_platform.fund_state = ?) ");
      if (isDebit) {
        sql.cond("and COALESCE(scm_platform.credit_amount_required,0)=0 ");
      } else {
        sql.cond("and COALESCE(scm_platform.debit_amount_required,0) =0 ");
      }
      main.param(accountId.getId());
      main.param(1);
      filterCondition(main, sql, selectSource, selectedDescription, selectedStatus, selectedSummary, p, productEntry);
      if ((p != null && p.getLevel() == SystemConstants.SECOND_LEVEL) || selectedSummary == 2) {
        sql.groupBy("scm_platform.id");
      }
      list = AppService.listPagedJpa(main, sql);
    }
    return list;
  }

  public static final List<Platform> listPagedFund(Main main, Account accountId, PlatformSource[] selectSource, PlatformDescription[] selectedDescription, Integer selectedStatus, int selectedSummary, Platform p) {
    main.clear();
    SqlPage sql = null;
//    if (selectedSummary == 1 || selectedSummary == 0) {
//      sql = getPlatformPagedSummary(main);
//    } else if ((p != null && p.getLevel() == 2) || selectedSummary == 2) {
//      sql = getPlatformPaged(main);
//    } else {
//      sql = getPlatformPagedSummary1(main);
//    }
    if (accountId != null) {
      if (selectedSummary == 3) {
        sql = getPlatformPagedSummary(main);
        sql.cond("where scm_platform.debit_amount_required is null and scm_platform.account_id=? and scm_platform.fund_state = ? ");
        main.param(accountId.getId());
        main.param(0);
      } else if (selectedSummary == 4) {
        sql = getPlatformPagedSummary(main);
        sql.cond("where scm_platform.debit_amount_required is null and scm_platform.account_id=? and scm_platform.fund_state = ? and scm_platform.platform_desc_id=? and scm_platform.parent_id is not null and  scm_platform.status_id=?");
        main.param(accountId.getId());
        main.param(0);
        main.param(p.getPlatformDescId().getId());
        main.param(SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId());
      } else if (selectedSummary == 5 && !"Realized".equals(p.getDisplayText()) && p.getLevel() != 2) {
        sql = getPlatformPagedSummary1(main, null, null, false);
        sql.cond("where scm_platform.debit_amount_required is null and scm_platform.account_id=? and scm_platform.fund_state = ? and scm_platform.platform_desc_id=? and  scm_platform.status_id=? ");
        main.param(accountId.getId());
        main.param(0);
        main.param(p.getPlatformDescId().getId());
        main.param(SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId());
      } else if (selectedSummary == 5 && "Realized".equals(p.getDisplayText())) {
        sql = getPlatformPaged(main);
        sql.cond("where scm_platform.debit_amount_required is null and scm_platform.account_id=? and scm_platform.fund_state = ? and scm_platform.platform_desc_id=? and  scm_platform.status_id=? and scm_platform.parent_id =?");
        main.param(accountId.getId());
        main.param(0);
        main.param(p.getPlatformDescId().getId());
        main.param(SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId());
        main.param(p.getParentId().getId());
      } //    else if (selectedSummary == 5 && p.getDisplayText() != null) {
      //      sql = getPlatformPaged(main);
      //      sql.cond("where scm_platform.debit_amount_required is null and scm_platform.account_id=? and scm_platform.fund_state = ? and scm_platform.platform_desc_id=? and  scm_platform.status_id=? and scm_platform.parent_id =? and scm_platform.product_entry_id=?");
      //      main.param(accountId.getId());
      //      main.param(0);
      //      main.param(p.getPlatformDescId().getId());
      //      main.param(1);
      //      main.param(p.getParentId().getId());
      //      main.param(p.getProductEntryId().getId());
      //    }
      else {
        sql = getPlatformPaged(main);
        sql.cond("where scm_platform.debit_amount_required is null and scm_platform.account_id=? and scm_platform.fund_state = ? and scm_platform.platform_desc_id=? and scm_platform.status_id=? and scm_platform.product_entry_id=?");
        main.param(accountId.getId());
        main.param(0);
        main.param(p.getPlatformDescId().getId());
        main.param(SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId());
        main.param(p.getProductEntryId().getId());
      }
      //filterCondition(main, sql, selectSource, selectedDescription, selectedStatus, selectedYear, selectedSummary, p);
      return AppService.listPagedJpa(main, sql);
    }
    return null;
  }

  public static final void filterCondition(Main main, SqlPage sql, PlatformSource[] selectSource, PlatformDescription[] selectedDescription, Integer selectedStatus, int selectedSummary, Platform p, ProductEntry productEntry) {
    if (!StringUtil.isEmpty(selectSource)) {
      sql.cond("and scm_platform.source_id in (");
      for (int i = 0; i < selectSource.length; i++) {
        if (i > 0) {
          sql.cond(",");
        }
        sql.cond("?");
        main.param(selectSource[i].getId());
      }
      sql.cond(")");
    }
    if (!StringUtil.isEmpty(selectedDescription)) {
      sql.cond("and scm_platform.platform_desc_id in (");
      for (int i = 0; i < selectedDescription.length; i++) {
        if (i > 0) {
          sql.cond(",");
        }
        sql.cond("?");
        main.param(selectedDescription[i].getId());
      }
      sql.cond(")");
    }
    sql.cond(" and scm_platform.status_id = ? ");
    if (selectedStatus == null || selectedStatus.equals(SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId())) {
      main.param(SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId());
    } else {
      main.param(SystemRuntimeConfig.PLATFORM_STATUS_PROCESSED.getId());
    }
    if (productEntry != null) {
      sql.cond(" and scm_platform.product_entry_id = ? ");
      main.param(productEntry.getId());
    }
//    sql.cond("and date_part('year',scm_platform.entry_date)=?");
//
//    Calendar now = Calendar.getInstance();
//    int currentYear = now.get(Calendar.YEAR);
//    if (selectedYear == 0) {
//      main.param(currentYear);
//    } else {
//      main.param(selectedYear);
//    }
    StringBuilder sb = new StringBuilder();
    List<Object> params = new ArrayList<>();
    addCondition(p, sb, params);
    if (sb.length() > 0) {
      sql.cond(sb.toString());
      for (Object param : params) {
        main.param(param);
      }
    }
  }

  private static void addCondition(Platform platform, StringBuilder sql, List<Object> params) {
    if (platform != null) {
      if (platform.getLevel() == null || platform.getLevel() == SystemConstants.FIRST_LEVEL) {
        sql.append(" and scm_platform.platform_desc_id = ? ");
        params.add(platform.getPlatformDescId().getId());
      } else if (platform.getLevel() == SystemConstants.SECOND_LEVEL) {
        sql.append(" and scm_platform.platform_desc_id = ?  and scm_platform.product_entry_id = ? ");
        params.add(platform.getPlatformDescId().getId());
        params.add(platform.getProductEntryId().getId());
      } else if (platform.getLevel() == SystemConstants.THIRD_LEVEL) {
        sql.append(" and scm_platform.platform_desc_id = ?  and scm_platform.product_entry_id = ? ");
        params.add(platform.getPlatformDescId().getId());
        params.add(platform.getProductEntryId().getId());
        if (platform.getSalesReturnId() != null) {
          sql.append(" and scm_platform.sales_return_id = ? ");
          params.add(platform.getSalesReturnId().getId());
        } else if (platform.getSalesInvoiceId() != null) {
          sql.append(" and scm_platform.sales_invoice_id = ? ");
          params.add(platform.getSalesInvoiceId().getId());
        } else if (platform.getPurchaseReturnId() != null) {
          sql.append(" and scm_platform.purchase_return_id = ? ");
          params.add(platform.getPurchaseReturnId().getId());
        } else if (platform.getProductMrpAdjustmentId() != null) {
          sql.append(" and scm_platform.product_mrp_adjustment_id =? ");
          params.add(platform.getProductMrpAdjustmentId().getId());

        }
      }
    }
  }

  /**
   * Select Platform by key.
   *
   * @param main
   * @param platform
   * @return Platform
   */
  public static final Platform
          selectByPk(Main main, Platform platform) {
    return (Platform) AppService.find(main, Platform.class,
            platform.getId());
  }

  /**
   * Insert Platform.
   *
   * @param main
   * @param platform
   */
  public static final void insert(Main main, Platform platform) {
    PlatformIs.insertAble(main, platform);  //Validating
    AppService.insert(main, platform);

  }

  public static final void insertSales(Main main, Account account, PlatformDescription platformDesc, Double debit, Double credit, SalesInvoice salesInvoice, SalesInvoiceItem saleInvoiceItem, Integer fundState) {
    double platformValue = debit == null ? credit : debit;
    Platform p = getPlatform(account, SystemRuntimeConfig.SALES, debit, credit, fundState);
    p.setSalesInvoiceId(salesInvoice);
    p.setSalesInvoiceItemId(saleInvoiceItem);
    if (platformValue >= SystemRuntimeConfig.PLATFORM_UNREALIZED_MAX_VALUE
            && platformValue <= SystemRuntimeConfig.PLATFORM_UNREALIZED_MIN_VALUE) {
      p.setPlatformDescId(SystemRuntimeConfig.PLATFORM_UNREALIZED_AMOUNT);
    } else {
      p.setPlatformDescId(platformDesc);
    }
    if (saleInvoiceItem != null) {
      p.setProductDetailId(saleInvoiceItem.getProductDetailId());
    }
    if (salesInvoice != null) {
      p.setDocumentNo(salesInvoice.getInvoiceNo());
      p.setEntryDate(salesInvoice.getInvoiceEntryDate());
      p.setCustomerId(salesInvoice.getCustomerId());
      if (saleInvoiceItem.getProductEntryDetailId() != null) {
        p.setProductEntryId(saleInvoiceItem.getProductEntryDetailId().getProductEntryId());
        p.setProductEntryDetailId(saleInvoiceItem.getProductEntryDetailId());
      }
      p.setDescription(SystemConstants.RATE_DIFFERENCE + " <" + salesInvoice.getInvoiceNo() + ">");
    }
    insertOrUpdate(main, p);
  }

  public static final void insertMarginDifference(Main main, Account account, PlatformDescription platformDesc, Double debit, Double credit, ProductEntry productEntry, ProductEntryDetail productEntryDetail, SalesInvoice salesInvoice, SalesInvoiceItem salesInvoiceItem, Integer fundState) {
    double platformValue = debit == null ? credit : debit;
    PlatformSource source = null;
    if (salesInvoice != null) {
      source = SystemRuntimeConfig.SALES;
    } else {
      source = SystemRuntimeConfig.PURCHASE;
    }
    Platform p = getPlatform(account, source, debit, credit, fundState);
    p.setProductEntryId(productEntry);
    p.setProductEntryDetailId(productEntryDetail);
    p.setSalesInvoiceId(salesInvoice);
    p.setSalesInvoiceItemId(salesInvoiceItem);

    if (platformValue >= SystemRuntimeConfig.PLATFORM_UNREALIZED_MAX_VALUE
            && platformValue <= SystemRuntimeConfig.PLATFORM_UNREALIZED_MIN_VALUE) {
      p.setPlatformDescId(SystemRuntimeConfig.PLATFORM_UNREALIZED_AMOUNT);
    } else {
      p.setPlatformDescId(platformDesc);
    }

    if (productEntryDetail != null) {
      p.setProductDetailId(productEntryDetail.getProductDetailId());
    }
    if (productEntry != null) {
      p.setDocumentNo(productEntry.getAccountInvoiceNo());
      p.setEntryDate(productEntry.getProductEntryDate());
    }
    if (productEntry.getSalesReturnId() == null) {
      p.setDescription(SystemConstants.MARGIN_DIFFERENCE + " <" + productEntry.getAccountInvoiceNo() + ">");
    } else {
      p.setDescription(SystemConstants.MARGIN_DIFFERENCE + " <" + productEntry.getAccountInvoiceNo() + ">**");
    }
    insertOrUpdate(main, p);
  }

  public static final void insertPurchase(Main main, Account account, PlatformDescription platformDesc, Double debit, Double credit, ProductEntry productEntry, ProductEntryDetail productEntryDetail, Integer fundState) {
    double platformValue = debit == null ? credit : debit;
    Platform p = getPlatform(account, SystemRuntimeConfig.PURCHASE, debit, credit, fundState);
    p.setProductEntryId(productEntry);
    p.setProductEntryDetailId(productEntryDetail);
    if (platformValue >= SystemRuntimeConfig.PLATFORM_UNREALIZED_MAX_VALUE
            && platformValue <= SystemRuntimeConfig.PLATFORM_UNREALIZED_MIN_VALUE) {
      p.setPlatformDescId(SystemRuntimeConfig.PLATFORM_UNREALIZED_AMOUNT);
    } else {
      p.setPlatformDescId(platformDesc);
    }

    if (productEntryDetail != null) {
      p.setProductDetailId(productEntryDetail.getProductDetailId());
    }
    if (productEntry != null) {
      p.setDocumentNo(productEntry.getAccountInvoiceNo());
      p.setEntryDate(productEntry.getProductEntryDate());
      if (platformDesc.equals(SystemRuntimeConfig.SHORTAGE)) {
        p.setDescription(SystemConstants.SHORTAGE_QUANTITY + " <" + productEntry.getAccountInvoiceNo() + ">");
      } else if (platformDesc.equals(SystemRuntimeConfig.REPLACEMENT)) {
        p.setDescription(SystemConstants.REPLACEMENT + " <" + productEntry.getAccountInvoiceNo() + ">");
      } else {
        p.setDescription(SystemConstants.INVOICE_DIFFERENCE + " <" + productEntry.getAccountInvoiceNo() + ">");
      }
    }
    insertOrUpdate(main, p);
  }

  public static final void insertPurchaseReturn(Main main, Account account, PlatformDescription platformDesc, Double debit, Double credit, PurchaseReturn purchaseReturn, PurchaseReturnItem purchaseReturnItem, Integer fundState) {
    double platformValue = debit == null ? credit : debit;
    Platform p = getPlatform(account, SystemRuntimeConfig.PURCHASE_RETURN, debit, credit, fundState);
    p.setPurchaseReturnId(purchaseReturn);
    p.setPurchaseReturnItemId(purchaseReturnItem);
    p.setEntryDate(purchaseReturn.getEntryDate());
    if (purchaseReturnItem.getProductEntryDetailId() != null) {
      p.setProductEntryDetailId(purchaseReturnItem.getProductEntryDetailId());
      if (purchaseReturnItem.getProductEntryDetailId().getProductEntryId() != null) {
        p.setProductEntryId(purchaseReturnItem.getProductEntryDetailId().getProductEntryId());
      } else {
        p.setProductEntryId(ProductEntryService.getProductEntryByDetailId(main, purchaseReturnItem.getProductEntryDetailId().getId()));
      }
    }
    if (platformValue >= SystemRuntimeConfig.PLATFORM_UNREALIZED_MAX_VALUE
            && platformValue <= SystemRuntimeConfig.PLATFORM_UNREALIZED_MIN_VALUE) {
      p.setPlatformDescId(SystemRuntimeConfig.PLATFORM_UNREALIZED_AMOUNT);
    } else {
      p.setPlatformDescId(platformDesc);
    }

    if (purchaseReturnItem != null) {
      p.setProductDetailId(purchaseReturnItem.getProductDetailId());
    }
    if (purchaseReturn != null) {
      p.setDocumentNo(purchaseReturn.getInvoiceNo());
    }
    if (platformDesc.equals(SystemRuntimeConfig.RATE_DIFFERENCE)) {
      p.setDescription(SystemConstants.RATE_DIFFERENCE + " <" + purchaseReturn.getInvoiceNo() + ">");
    }
    insertOrUpdate(main, p);
  }

  public static final void insertSalesReturn(Main main, Account account, PlatformDescription platformDesc, Double debit, Double credit, SalesReturn salesReturn, SalesReturnItem salesReturnItem, Integer fundState) {
    double platformValue = debit == null ? credit : debit;
    Platform p = getPlatform(account, SystemRuntimeConfig.SALES_RETURN, debit, credit, fundState);
    p.setSalesReturnId(salesReturn);
    p.setSalesReturnItemId(salesReturnItem);
    p.setSalesInvoiceId(salesReturnItem.getSalesInvoiceId());
    p.setSalesInvoiceItemId(salesReturnItem.getSalesInvoiceItemId());
    if (salesReturnItem.getProductEntryDetailId() != null) {
      p.setProductEntryId(salesReturnItem.getProductEntryDetailId().getProductEntryId());
      p.setProductEntryDetailId(salesReturnItem.getProductEntryDetailId());
    }
    if (platformValue >= SystemRuntimeConfig.PLATFORM_UNREALIZED_MAX_VALUE
            && platformValue <= SystemRuntimeConfig.PLATFORM_UNREALIZED_MIN_VALUE) {
      p.setPlatformDescId(SystemRuntimeConfig.PLATFORM_UNREALIZED_AMOUNT);
    } else {
      p.setPlatformDescId(platformDesc);
    }
    if (salesReturnItem != null) {
      p.setProductDetailId(salesReturnItem.getProductDetailId());
    }
    if (salesReturn != null) {
      p.setDocumentNo(salesReturn.getInvoiceNo());
      p.setEntryDate(salesReturn.getEntryDate());
    }
    if (platformDesc.equals(SystemRuntimeConfig.RATE_DIFFERENCE)) {
      p.setDescription(SystemConstants.RATE_DIFFERENCE + " <" + salesReturn.getInvoiceNo() + ">");
    } else if (platformDesc.equals(SystemRuntimeConfig.MARGIN_DIFFERENCE)) {
      p.setDescription(SystemConstants.MARGIN_DIFFERENCE + " <" + salesReturn.getInvoiceNo() + ">");
    } else if (platformDesc.equals(SystemRuntimeConfig.MRP_ADJUSTMENT)) {
      p.setDescription(SystemConstants.MRP_ADJUSTMENT + " <" + salesReturn.getInvoiceNo() + ">");
    }
    insertOrUpdate(main, p);
  }

  public static final void insertDebitCreditNote(Main main, Account account, PlatformDescription platformDesc, Double debit, Double credit, DebitCreditNote debitCreditNote, Integer fundState) {
    Platform p = getPlatform(account, SystemRuntimeConfig.DEBIT_CREDIT_NOTE, debit, credit, fundState);
    p.setDebitCreditNoteId(debitCreditNote);
    p.setPlatformDescId(platformDesc);
    p.setDocumentNo(debitCreditNote.getInvoiceNo());
    //p.setEntryDate(debitCreditNote.getEntryDate());
    p.setEntryDate(new Date());
    insertOrUpdate(main, p);
  }

  public static final void insertVendorClaim(Main main, Account account, PlatformDescription platformDesc, Double debit, Double credit, VendorClaim vendorClaim, Integer fundState) {
    Platform p = getPlatform(account, SystemRuntimeConfig.VENDOR_CLAIM, debit, credit, fundState);
    p.setVendorClaimId(vendorClaim);
    p.setPlatformDescId(platformDesc);
    insertOrUpdate(main, p);
  }

  private static Platform getPlatform(Account account, PlatformSource source, Double debit, Double credit, Integer fundState) {
    Platform platform = new Platform();
    platform.setAccountId(account);
    platform.setSourceId(source);
    platform.setCreditAmountRequired(credit);
    platform.setDebitAmountRequired(debit);
    platform.setFundState(fundState);
    platform.setStatusId(SystemRuntimeConfig.PLATFORM_STATUS_NEW);
    return platform;
  }

  /**
   * Update Platform by key.
   *
   * @param main
   * @param platform
   * @return Platform
   */
  public static final Platform updateByPk(Main main, Platform platform) {
    PlatformIs.updateAble(main, platform); //Validating
    return (Platform) AppService.update(main, platform);
  }

  /**
   * Insert or update Platform
   *
   * @param main
   * @param platform
   */
  public static void insertOrUpdate(Main main, Platform platform) {
    if (platform.getId() == null) {
      insert(main, platform);
    } else {
      updateByPk(main, platform);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param platform
   */
  public static void clone(Main main, Platform platform) {
    platform.setId(null); //Set to null for insert
    insert(main, platform);
  }

  /**
   * Delete Platform.
   *
   * @param main
   * @param platform
   */
  public static final void deleteByPk(Main main, Platform platform) {
    PlatformIs.deleteAble(main, platform); //Validation
    AppService
            .delete(main, Platform.class,
                    platform.getId());
  }

  /**
   * Delete Array of Platform.
   *
   * @param main
   * @param platform
   */
  public static final void deleteByPkArray(Main main, Platform[] platform) {
    for (Platform e : platform) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param platform
   * @param platformStatus
   */
  public static void updatePlatformStatus(Main main, Platform platform, Integer platformStatus) {
    String sql = "update scm_platform set modified_by=?, modified_at = ?, status_id = ? where id = ?";
    main.clear();
    main.param(platformStatus);
    main.param(platform.getId());
    AppService
            .updateSql(main, Platform.class,
                    sql, true);
  }

  /**
   *
   * @param main
   * @param platformList
   * @param platformStatus
   */
  public static void updatePlatformStatus(Main main, List<Platform> platformList, Integer platformStatus) {
    if (!StringUtil.isEmpty(platformList)) {
      for (Platform platform : platformList) {
        updatePlatformStatus(main, platform, platformStatus);
      }
    }
  }

  public static void salesInvoiceReverseEntry(Main main, SalesInvoice salesInvoice) {
    List<Platform> platformList = AppService.list(main, Platform.class,
            "select * from scm_platform where sales_invoice_id = ?", new Object[]{salesInvoice.getId()});
    Platform platform = null;
    for (Platform p : platformList) {
      platform = new Platform();
      platform.setAccountId(p.getAccountId());
      platform.setSourceId(p.getSourceId());
      if (p.getCreditAmountRequired() != null) {
        platform.setDebitAmountRequired(p.getCreditAmountRequired());
      } else if (p.getDebitAmountRequired() != null) {
        platform.setCreditAmountRequired(p.getDebitAmountRequired());
      }
      platform.setFundState(p.getFundState());
      platform.setReverseEntry(AccountingConstant.REVERSE_ENTRY);
      platform.setStatusId(SystemRuntimeConfig.PLATFORM_STATUS_NEW);
      platform.setSalesInvoiceId(p.getSalesInvoiceId());
      platform.setSalesInvoiceItemId(p.getSalesInvoiceItemId());
      platform.setPlatformDescId(p.getPlatformDescId());
      platform.setProductDetailId(p.getProductDetailId());
      platform.setDocumentNo(p.getDocumentNo());
      platform.setEntryDate(p.getEntryDate());
      platform.setProductEntryId(p.getProductEntryId());
      platform.setProductEntryDetailId(p.getProductEntryDetailId());
      insert(main, platform);
    }
  }

  public static void salesInvoicePlatformEntry(Main main, SalesInvoice salesInvoice) {
    double rateDiff = 0, purchasedQty = 0.0, purchasedFreeQty = 0.0;
    int productQty = 0;
    int productFreeQty = 0;
    double marginValue = 0.0;
    double vendorMarginValue = 0.0;
    boolean freeScheme;
    boolean schemeApplicable;

    List<SalesInvoiceItem> list = AppService.list(main, SalesInvoiceItem.class,
            "select * from scm_sales_invoice_item where sales_invoice_id = ? and item_deleted = 0 order by id asc", new Object[]{salesInvoice.getId()});
    for (SalesInvoiceItem sitem : list) {

      freeScheme = SystemConstants.FREE_SCHEME.equals(sitem.getQuantityOrFree());

      productFreeQty = sitem.getProductQtyFree() == null ? 0 : sitem.getProductQtyFree();

      productQty = sitem.getProductQty() + (freeScheme ? 0 : productFreeQty);

      //Platform Entry for Rate Difference
      rateDiff = (sitem.getRateDiffPerPiece() != null ? sitem.getRateDiffPerPiece() : 0) + (sitem.getMarginValueDeviationDer() != null ? sitem.getMarginValueDeviationDer() : 0);

      if (rateDiff != 0) {
        if (rateDiff > 0) {
          rateDiff = rateDiff * productQty;
          PlatformService.insertSales(main, sitem.getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, null, Math.abs(rateDiff), salesInvoice, sitem, PlatformService.NORMAL_FUND_STATE);
        } else {
          rateDiff = rateDiff * productQty;
          PlatformService.insertSales(main, sitem.getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, Math.abs(rateDiff), null, salesInvoice, sitem, PlatformService.NORMAL_FUND_STATE);
        }
      }

      if (sitem.getProductEntryDetailId() != null) {

        schemeApplicable = SystemConstants.FREE_SCHEME.equals(sitem.getProductEntryDetailId().getProductEntryId().getQuantityOrFree());
        purchasedQty = sitem.getProductEntryDetailId().getProductQuantity();

        if (!schemeApplicable) {
          purchasedFreeQty = sitem.getProductEntryDetailId().getProductQuantityFree() == null ? 0 : sitem.getProductEntryDetailId().getProductQuantityFree();
          purchasedQty += purchasedFreeQty;
        }

//        In custom pricelist scheme discount and product discounts are not passed, so there will be a margin deviation to consider
//        if (salesInvoice.getAccountGroupPriceListId().getIsDefault() == 0) {
//          marginValue = 0;
//          if (sitem.getProductEntryDetailId().getIsSchemeDiscountToCustomer() != null && sitem.getProductEntryDetailId().getIsSchemeDiscountToCustomer().equals(SystemConstants.DISCOUNT_FOR_CUSTOMER)) {
//            marginValue = ((sitem.getProductEntryDetailId().getSchemeDiscountValueDerived() != null ? sitem.getProductEntryDetailId().getSchemeDiscountValueDerived() : 0) * productQty);
//          }
//          if (sitem.getProductEntryDetailId().getIsLineDiscountToCustomer() != null && sitem.getProductEntryDetailId().getIsLineDiscountToCustomer().equals(SystemConstants.DISCOUNT_FOR_CUSTOMER)) {
//            marginValue += ((sitem.getProductEntryDetailId().getProductDiscountValueDerived() != null ? sitem.getProductEntryDetailId().getProductDiscountValueDerived() : 0) * productQty);
//          }
//          if (marginValue != 0) {
//            PlatformService.insertMarginDifference(main, sitem.getAccountId(), SystemRuntimeConfig.MARGIN_DIFFERENCE, null, Math.abs(marginValue), sitem.getProductEntryDetailId().getProductEntryId(), sitem.getProductEntryDetailId(), salesInvoice, sitem, PlatformService.NORMAL_FUND_STATE);
//          }
//        }
        if (sitem.getProductEntryDetailId().getVendorMarginValue() != null && sitem.getProductEntryDetailId().getVendorMarginValue() != 0 && purchasedQty > 0) {
          vendorMarginValue = (sitem.getProductEntryDetailId().getVendorMarginValue() / purchasedQty) * productQty;
          if (vendorMarginValue != 0) {
            if (vendorMarginValue > 0) {
              PlatformService.insertMarginDifference(main, sitem.getAccountId(), SystemRuntimeConfig.VENDOR_RESERVE, null, Math.abs(vendorMarginValue), sitem.getProductEntryDetailId().getProductEntryId(), sitem.getProductEntryDetailId(), salesInvoice, sitem, PlatformService.UNREALIZED_FUND_STATE);
            } else {
              PlatformService.insertMarginDifference(main, sitem.getAccountId(), SystemRuntimeConfig.VENDOR_RESERVE, Math.abs(vendorMarginValue), null, sitem.getProductEntryDetailId().getProductEntryId(), sitem.getProductEntryDetailId(), salesInvoice, sitem, PlatformService.UNREALIZED_FUND_STATE);
            }
          }
        }
      }
    }
  }

  /**
   *
   * @param main
   * @param productMrpAdjustment
   * @param productMrpAdjustmentItemList
   */
  public static void insertProductMrpAdjustment(Main main, ProductMrpAdjustment productMrpAdjustment, List<ProductMrpAdjustmentItem> productMrpAdjustmentItemList) {
    for (ProductMrpAdjustmentItem productMrpAdjustmentItem : productMrpAdjustmentItemList) {

      if (productMrpAdjustmentItem.getValueDebit() != null && productMrpAdjustmentItem.getValueDebit() != 0) {

        insertProductMrpAdjustment(main, productMrpAdjustmentItem.getProductDetailId().getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, productMrpAdjustmentItem.getValueDebit(), null, productMrpAdjustment, productMrpAdjustmentItem, NORMAL_FUND_STATE);

      } else if (productMrpAdjustmentItem.getValueCredit() != null && productMrpAdjustmentItem.getValueCredit() != 0) {

        insertProductMrpAdjustment(main, productMrpAdjustmentItem.getProductDetailId().getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, null, productMrpAdjustmentItem.getValueCredit(), productMrpAdjustment, productMrpAdjustmentItem, NORMAL_FUND_STATE);

      }
    }
  }

  public static final void insertProductMrpAdjustment(Main main, Account account, PlatformDescription platformDesc, Double debit, Double credit, ProductMrpAdjustment productMrpAdjustment, ProductMrpAdjustmentItem productMrpAdjustmentItem, Integer fundState) {
    double platformValue = debit == null ? credit : debit;
    Platform p = getPlatform(account, SystemRuntimeConfig.PURCHASE, debit, credit, fundState);
    p.setProductEntryId(productMrpAdjustmentItem.getProductEntryDetailId().getProductEntryId());
    p.setProductEntryDetailId(productMrpAdjustmentItem.getProductEntryDetailId());
    p.setProductMrpAdjustmentId(productMrpAdjustment);
    p.setProductMrpAdjustmentItemId(productMrpAdjustmentItem);
    p.setProductDetailId(productMrpAdjustmentItem.getProductDetailId());
    p.setDocumentNo(productMrpAdjustmentItem.getInvoiceNo());
    p.setEntryDate(new Date());
    if (platformValue >= SystemRuntimeConfig.PLATFORM_UNREALIZED_MAX_VALUE
            && platformValue <= SystemRuntimeConfig.PLATFORM_UNREALIZED_MIN_VALUE) {
      p.setPlatformDescId(SystemRuntimeConfig.PLATFORM_UNREALIZED_AMOUNT);
    } else {
      p.setPlatformDescId(platformDesc);
    }
    insertOrUpdate(main, p);
  }

  public static void salesReturnPlatformEntry(Main main, SalesReturn salesReturn, List<SalesReturnItem> salesReturnItems) {
    double difference;
    double marginValueDiff = 0.0, vendorMarginValue = 0.0;
    Double productQty = null;
    ProductPricelist productPricelist = null;
    boolean isDamagedReturn = (salesReturn.getSalesReturnType().equals(SystemConstants.SALES_RETURN_TYPE_DAMAGED));

    for (SalesReturnItem salesReturnItem : salesReturnItems) {
      productPricelist = null;
      productQty = isDamagedReturn ? salesReturnItem.getProductQuantityDamaged() : salesReturnItem.getProductQuantity();

      difference = (salesReturnItem.getSalesInvoiceItemId().getRateDiffPerPiece() != null ? salesReturnItem.getSalesInvoiceItemId().getRateDiffPerPiece() : 0.0)
              + (salesReturnItem.getSalesInvoiceItemId().getMarginValueDeviationDer() != null ? salesReturnItem.getSalesInvoiceItemId().getMarginValueDeviationDer() : 0.0);

      if (difference > 0) {
        difference = difference * productQty;
        PlatformService.insertSalesReturn(main, salesReturnItem.getProductDetailId().getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, Math.abs(difference), null, salesReturn, salesReturnItem, PlatformService.NORMAL_FUND_STATE);
      } else if (difference < 0) {
        difference = difference * productQty;
        PlatformService.insertSalesReturn(main, salesReturnItem.getProductDetailId().getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, null, Math.abs(difference), salesReturn, salesReturnItem, PlatformService.NORMAL_FUND_STATE);

      }

      if (salesReturnItem.getSalesInvoiceId() != null && salesReturnItem.getSalesInvoiceItemId() != null && salesReturnItem.getSalesInvoiceItemId().getIsMrpChanged() != null
              && salesReturnItem.getSalesInvoiceItemId().getIsMrpChanged().intValue() == 1) {
        Double mrpAdjRateDiff = salesReturnItem.getSalesInvoiceItemId().getMrpAdjRateDiff() != null ? salesReturnItem.getSalesInvoiceItemId().getMrpAdjRateDiff() : 0.0;
        if (mrpAdjRateDiff > 0) {
          mrpAdjRateDiff = mrpAdjRateDiff * productQty;
          PlatformService.insertSalesReturn(main, salesReturnItem.getProductDetailId().getAccountId(), SystemRuntimeConfig.MRP_ADJUSTMENT, null, Math.abs(mrpAdjRateDiff), salesReturn, salesReturnItem, PlatformService.NORMAL_FUND_STATE);
        } else if (mrpAdjRateDiff < 0) {
          mrpAdjRateDiff = mrpAdjRateDiff * productQty;
          PlatformService.insertSalesReturn(main, salesReturnItem.getProductDetailId().getAccountId(), SystemRuntimeConfig.MRP_ADJUSTMENT, Math.abs(mrpAdjRateDiff), null, salesReturn, salesReturnItem, PlatformService.NORMAL_FUND_STATE);
        }
      }

//      Credit Settlement platform Entry
      if (salesReturnItem.getCreditSettlementAmount() != null && salesReturnItem.getCreditSettlementAmount() > 0) {
        PlatformService.insertSalesReturn(main, salesReturnItem.getAccountId(), SystemRuntimeConfig.CUSTOMER_CREDIT_ADJUSTMENT, null, salesReturnItem.getCreditSettlementAmount(), salesReturn, salesReturnItem, NORMAL_FUND_STATE);
      }
    }
  }

  public static void deleteByDebitCreditNote(MainView main, DebitCreditNote debitCreditNote) {
    String sql = " delete from scm_platform where debit_credit_note_id = ? ";
    AppService
            .deleteSql(main, Platform.class,
                    sql, new Object[]{debitCreditNote.getId()});
  }

  public static List<ProductEntry> getProductEntryList(Main main, Account account) {
    return AppService.list(main, ProductEntry.class, "select * from scm_product_entry where account_id=?",
            new Object[]{account.getId()});
  }

  public static List<PlatformSummary> getPlatformDetailList(Main main, Company company, Account account, ProductEntry productEntry) {
    String sql = "SELECT \n"
            + "CASE \n"
            + "WHEN platform_desc_id =1 THEN 'Shortage'\n"
            + "WHEN platform_desc_id =2 THEN 'Excess'\n"
            + "WHEN platform_desc_id =3 THEN 'Supplier Reserve'\n"
            + "WHEN platform_desc_id =4 THEN 'Invoice Difference'\n"
            + "WHEN platform_desc_id =5 THEN 'Free Quantity'\n"
            + "WHEN platform_desc_id =6 THEN 'Margin Difference'\n"
            + "WHEN platform_desc_id =7 THEN 'Discount'\n"
            + "WHEN platform_desc_id =8 THEN 'Rate Difference'\n"
            + "WHEN platform_desc_id =9 THEN 'Platform Difference'\n"
            + "WHEN platform_desc_id =10 THEN 'Platform_Unrealized_Amount'\n"
            + "WHEN platform_desc_id =14 THEN 'Supplier Claim'\n"
            + "WHEN platform_desc_id =15 THEN 'Self Settlement Balance'\n"
            + "WHEN platform_desc_id =16 THEN 'Credit Settlement Adjustment'\n"
            + "ELSE '<SOMETHING WENT WRONG>' END as platform_desc,\n"
            + "PL1.entry_date,\n"
            + "AC1.account_title,\n"
            + "CUS1.customer_name,\n"
            + "PE1.account_invoice_no as purchase_invoice,\n"
            + "SA1.invoice_no as sales_invoice,\n"
            + "SR1.invoice_no as sales_return_invoice,\n"
            + "PR1.invoice_no as purchase_return_invoice,\n"
            + "MRP1.invoice_no as mrp_adj_invoice,\n"
            + "CASE \n"
            + "WHEN PL1.sales_invoice_item_id IS NOT NULL THEN SA1.product_name\n"
            + "WHEN PL1.sales_return_item_id IS NOT NULL THEN SR1.product_name\n"
            + "WHEN PL1.purchase_return_item_id IS NOT NULL THEN PR1.product_name\n"
            + "WHEN PL1.product_mrp_adjustment_item_id IS NOT NULL THEN MRP1.product_name \n"
            + "WHEN PL1.product_entry_detail_id IS NOT NULL THEN PE2.product_name\n"
            + "ELSE '<NA>' END as product_name,\n"
            + "\n"
            + "COALESCE(PL1.credit_amount_required,0) as credit_amount_required,\n"
            + "COALESCE(PL1.debit_amount_required,0)  as debit_amount_required\n"
            + "FROM \n"
            + "scm_account as AC1,\n"
            + "scm_product_entry as PE1,\n"
            + "scm_platform as PL1 \n"
            + "LEFT OUTER JOIN scm_customer as CUS1 ON PL1.customer_id = CUS1.id \n"
            + "LEFT OUTER JOIN \n"
            + "(SELECT scm_product_entry.id as PE_id,scm_product_entry_detail.id as PEINV_id,invoice_no,invoice_date,scm_product.product_name,scm_product_entry_detail.batch_no\n"
            + "FROM scm_product_entry,scm_product_entry_detail,scm_product_detail,scm_product_batch,scm_product\n"
            + "WHERE scm_product_entry.id = scm_product_entry_detail.product_entry_id AND scm_product_entry_detail.product_detail_id=scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_batch.product_id = scm_product.id \n"
            + "AND  scm_product_detail.account_id =? AND scm_product_entry.company_id=?\n"
            + ") as PE2 ON PL1.product_entry_detail_id = PE2.PEINV_id \n"
            + "\n"
            + "LEFT OUTER JOIN \n"
            + "(SELECT scm_sales_invoice.id as SA_id,scm_sales_invoice_item.id as SAINV_id,invoice_no,invoice_date,scm_product.product_name,scm_sales_invoice_item.batch_no\n"
            + "FROM scm_sales_invoice,scm_sales_invoice_item,scm_product_detail,scm_product_batch,scm_product\n"
            + "WHERE scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id AND scm_sales_invoice_item.product_detail_id=scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_batch.product_id = scm_product.id \n"
            + "AND  scm_product_detail.account_id =? AND scm_sales_invoice.company_id=?\n"
            + ") as SA1 ON PL1.sales_invoice_item_id = SA1.SAINV_id \n"
            + "\n"
            + "LEFT OUTER JOIN (SELECT scm_sales_return.id as SR_id,scm_sales_return_item.id as SRINV_id,invoice_no,invoice_date,scm_product.product_name,scm_product_batch.batch_no\n"
            + "FROM scm_sales_return,scm_sales_return_item,scm_product_detail,scm_product_batch,scm_product\n"
            + "WHERE scm_sales_return.id = scm_sales_return_item.sales_return_id AND scm_sales_return_item.product_detail_id=scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_batch.product_id = scm_product.id \n"
            + "AND  scm_product_detail.account_id =? AND scm_sales_return.company_id=?\n"
            + ") as SR1 ON PL1.sales_return_item_id = SR1.SRINV_id \n"
            + "\n"
            + "LEFT OUTER JOIN (SELECT scm_purchase_return.id as PR_id,scm_purchase_return_item.id as PRINV_id,invoice_no,invoice_date,scm_product.product_name,scm_product_batch.batch_no\n"
            + "FROM scm_purchase_return,scm_purchase_return_item,scm_product_detail,scm_product_batch,scm_product\n"
            + "WHERE scm_purchase_return.id = scm_purchase_return_item.purchase_return_id AND scm_purchase_return_item.product_detail_id=scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_batch.product_id = scm_product.id \n"
            + "AND  scm_product_detail.account_id =? AND scm_purchase_return.company_id=?\n"
            + ") as PR1 ON PL1.purchase_return_item_id = PR1.PRINV_id  \n"
            + "\n"
            + "LEFT OUTER JOIN (SELECT scm_product_mrp_adjustment.id as PR_id,scm_product_mrp_adjustment_item.id as PRINV_id,invoice_no,scm_product.product_name,scm_product_batch.batch_no\n"
            + "FROM scm_product_mrp_adjustment,scm_product_mrp_adjustment_item,scm_product_detail,scm_product_batch,scm_product\n"
            + "WHERE scm_product_mrp_adjustment.id = scm_product_mrp_adjustment_item.product_mrp_adjustment_id AND scm_product_mrp_adjustment_item.product_detail_id=scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_batch.product_id = scm_product.id \n"
            + "AND  scm_product_detail.account_id =? AND scm_product_mrp_adjustment.company_id=?\n"
            + ") as MRP1 ON PL1.product_mrp_adjustment_item_id = MRP1.PRINV_id  \n"
            + "\n"
            + "WHERE \n"
            + "PL1.status_id=1\n"
            + "AND PL1.account_id=AC1.id\n"
            + "AND PL1.product_entry_id = PE1.id AND \n"
            + "PL1.account_id =? \n";

    List<Object> params = new ArrayList<Object>();
    params.add(account.getId());
    params.add(company.getId());
    params.addAll(params);
    params.addAll(params);
    params.add(account.getId());
    params.add(company.getId());
    params.add(account.getId());
    if (productEntry != null && productEntry.getId() != null) {
      sql += "AND PL1.product_entry_id=? \n";
      params.add(productEntry.getId());
    }
    sql += " ORDER BY 2,5,1 ";
    return AppDb.getList(main.dbConnector(), PlatformSummary.class, sql, params.toArray());
  }

  public static List<PlatformSummary> getTradingDifferenceList(Main main, Company company, Account account, ProductEntry productEntry, FilterParameters filterParameters) {
    List<Object> params = new ArrayList<Object>();
    Calendar cal = Calendar.getInstance();
//    cal.setTime(filterParameters.getFromDate());
//    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    cal.set(2021, 01, 28);
    Date lastDate = cal.getTime();
    cal.set(2020, 03, 01);
    Date firstDate = cal.getTime();
    String sql = "SELECT row_number() OVER () as id,TAB.id as product_entry_id,TAB.invoice_id,TAB.trade_event,TAB.product_entry_date,TAB.account_invoice_no,TAB.invoice_no,TAB.entry_date,TAB.invoice_date,\n"
            + "TAB.parent_invoice_no,TAB.customer_name,TAB.invoice_type, \n"
            + "TAB.product_name,TAB.batch_no,TAB.tax_rate,TAB.landing_price,\n"
            + "TAB.selling_price,TAB.sold_price,TAB.margin_difference_per_unit,TAB.total_qty,TAB.margin,TAB.total_margin_difference FROM \n"
            + "(SELECT scm_product_entry.id,scm_product_entry.product_entry_date, scm_sales_invoice.id as invoice_id,scm_sales_invoice_item.id as SAINV_id,'SALES' as trade_event,scm_product_entry.invoice_no as account_invoice_no,\n"
            + " scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_entry_date as entry_date, scm_sales_invoice.invoice_date,'' as parent_invoice_no,scm_customer.customer_name, \n"
            + "scm_product.product_name,scm_sales_invoice_item.batch_no,scm_tax_code.rate_percentage as tax_rate,\n"
            + "ROUND(scm_product_entry_detail.value_rate_per_prod_piece_der,3) as landing_price,\n"
            + "CASE WHEN scm_product_entry_detail.pts_ss_rate_derivation_criterion=2 THEN ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der/(1-scm_product_entry_detail.margin_percentage/100.0)),3)\n"
            + "ELSE ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der* (1+scm_product_entry_detail.margin_percentage/100.0)),3) END as selling_price,\n"
            + "ROUND(scm_sales_invoice_item.value_prod_piece_sold,3) as sold_price,\n"
            + "CASE WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=2 THEN COALESCE(scm_product_entry_detail.margin_percentage,0)::text || '% MARKDOWN'::text\n"
            + "WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=1 THEN COALESCE(scm_product_entry_detail.margin_percentage,0)::text || '% MARKUP'::text ELSE '0 %'::text END as margin,\n"
            + "ROUND(scm_sales_invoice_item.value_prod_piece_sold,3) - (CASE WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=2  AND COALESCE(scm_product_entry_detail.margin_percentage,0)!=0\n"
            + "	THEN ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der/(1-scm_product_entry_detail.margin_percentage/100.0)),3)\n"
            + "	WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=1  AND COALESCE(scm_product_entry_detail.margin_percentage,0)!=0\n"
            + "	THEN ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der*(1+scm_product_entry_detail.margin_percentage/100.0)),3)\n"
            + "    ELSE ROUND(scm_product_entry_detail.value_rate_per_prod_piece_der,3) END) as margin_difference_per_unit,\n"
            + "(COALESCE(scm_sales_invoice_item.product_qty,0)+COALESCE(scm_sales_invoice_item.product_qty_free,0))  as total_qty,\n"
            + "(ROUND(scm_sales_invoice_item.value_prod_piece_sold,3) - (CASE WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=2  AND COALESCE(scm_product_entry_detail.margin_percentage,0)!=0\n"
            + "	THEN ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der/(1-scm_product_entry_detail.margin_percentage/100.0)),3)\n"
            + "	WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=1  AND COALESCE(scm_product_entry_detail.margin_percentage,0)!=0\n"
            + "	THEN ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der*(1+scm_product_entry_detail.margin_percentage/100.0)),3)\n"
            + "    ELSE ROUND(scm_product_entry_detail.value_rate_per_prod_piece_der,3) END))*\n"
            + "(COALESCE(scm_sales_invoice_item.product_qty,0)+COALESCE(scm_sales_invoice_item.product_qty_free,0))  as total_margin_difference,2 as invoice_type \n"
            + "FROM scm_sales_invoice,\n"
            + "scm_sales_invoice_item,\n"
            + "scm_product_entry_detail,\n"
            + "scm_product_entry,\n"
            + "scm_product_detail,\n"
            + "scm_product_batch,\n"
            + "scm_product,\n"
            + "scm_customer,\n"
            + "scm_tax_code \n"
            + "WHERE scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id  AND scm_sales_invoice_item.product_entry_detail_id= scm_product_entry_detail.id \n"
            + "AND scm_product_entry_detail.product_entry_id= scm_product_entry.id AND scm_customer.id = scm_sales_invoice.customer_id \n"
            + "AND scm_sales_invoice_item.product_detail_id=scm_product_detail.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_batch.product_id = scm_product.id \n"
            + "AND scm_tax_code.id=scm_sales_invoice_item.tax_code_id\n"
            + "AND  scm_sales_invoice.company_id=? AND scm_product_entry.account_id=?\n";
    params.add(company.getId());
    params.add(account.getId());
    if (productEntry != null) {
      sql += " AND scm_product_entry.id=? ";
      params.add(productEntry.getId());
    }
    if (filterParameters != null) {
      sql += " AND  scm_sales_invoice.invoice_entry_date::date >= ?::date AND scm_sales_invoice.invoice_entry_date::date <= ?::date ";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(firstDate));
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(lastDate));
    }
    sql += "UNION ALL \n"
            + "SELECT scm_product_entry.id,scm_product_entry.product_entry_date,scm_sales_return.id as invoice_id,scm_sales_return_item.id as SRINV_id,'SALES RETURN' as trade_event,scm_product_entry.invoice_no as account_invoice_no,"
            + "scm_sales_return.invoice_no,scm_sales_return.entry_date,scm_sales_return.invoice_date,scm_sales_invoice.invoice_no as parent_invoice_no, scm_customer.customer_name,\n"
            + "scm_product.product_name,scm_product_batch.batch_no,scm_tax_code.rate_percentage as tax_rate,\n"
            + "ROUND(scm_product_entry_detail.value_rate_per_prod_piece_der,3) as landing_price,\n"
            + "CASE WHEN scm_product_entry_detail.pts_ss_rate_derivation_criterion=2 THEN ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der/(1-scm_product_entry_detail.margin_percentage/100.0)),3)\n"
            + "ELSE ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der* (1+scm_product_entry_detail.margin_percentage/100.0)),3) END as selling_price,\n"
            + "ROUND(scm_sales_invoice_item.value_prod_piece_sold,3) as sold_price,\n"
            + "CASE WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=2 THEN COALESCE(scm_product_entry_detail.margin_percentage,0)::text || '% MARKDOWN'::text\n"
            + "WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=1 THEN COALESCE(scm_product_entry_detail.margin_percentage,0)::text || '% MARKUP'::text ELSE '0 %'::text END as margin,\n"
            + "ROUND(scm_sales_invoice_item.value_prod_piece_sold,3) - (CASE WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=2  AND COALESCE(scm_product_entry_detail.margin_percentage,0)!=0\n"
            + "	THEN ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der/(1-scm_product_entry_detail.margin_percentage/100.0)),3)\n"
            + "	WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=1  AND COALESCE(scm_product_entry_detail.margin_percentage,0)!=0\n"
            + "	THEN ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der*(1+scm_product_entry_detail.margin_percentage/100.0)),3)\n"
            + "    ELSE ROUND(scm_product_entry_detail.value_rate_per_prod_piece_der,3) END) *-1 as margin_difference_per_unit,\n"
            + "(COALESCE(scm_sales_return_item.product_quantity,0)+COALESCE(scm_sales_return_item.product_quantity_damaged,0))  as total_qty,\n"
            + "(ROUND(scm_sales_invoice_item.value_prod_piece_sold,3) - (CASE WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=2  AND COALESCE(scm_product_entry_detail.margin_percentage,0)!=0\n"
            + "	THEN ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der/(1-scm_product_entry_detail.margin_percentage/100.0)),3)\n"
            + "	WHEN COALESCE(scm_product_entry_detail.pts_ss_rate_derivation_criterion,0)=1  AND COALESCE(scm_product_entry_detail.margin_percentage,0)!=0\n"
            + "	THEN ROUND((scm_product_entry_detail.value_rate_per_prod_piece_der*(1+scm_product_entry_detail.margin_percentage/100.0)),3)\n"
            + "    ELSE ROUND(scm_product_entry_detail.value_rate_per_prod_piece_der,3) END))*\n"
            + "(COALESCE(scm_sales_return_item.product_quantity,0)+COALESCE(scm_sales_return_item.product_quantity_damaged,0)) *-1 as total_margin_difference,4 as invoice_type \n"
            + "FROM scm_sales_return,scm_sales_return_item,scm_sales_invoice_item,scm_sales_invoice,scm_product_entry_detail,scm_product_entry,scm_customer,\n"
            + "scm_product_detail,scm_product_batch,scm_product,scm_tax_code \n"
            + "WHERE scm_sales_return.id = scm_sales_return_item.sales_return_id \n"
            + "AND scm_sales_invoice_item.product_entry_detail_id= scm_product_entry_detail.id\n"
            + "AND scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id\n"
            + "AND scm_customer.id = scm_sales_return.customer_id \n"
            + "AND scm_product_entry_detail.product_entry_id= scm_product_entry.id\n"
            + "AND scm_sales_return_item.product_detail_id=scm_product_detail.id\n"
            + "AND scm_sales_invoice_item.id=scm_sales_return_item.sales_invoice_item_id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_batch.product_id = scm_product.id \n"
            + "AND scm_tax_code.id=scm_sales_return_item.tax_code_id \n"
            + "AND  scm_sales_return.company_id=? and scm_product_entry.account_id=? \n";
    params.add(company.getId());
    params.add(account.getId());
    if (productEntry != null) {
      sql += " AND scm_product_entry.id=? ";
      params.add(productEntry.getId());
    }
    if (filterParameters != null) {
      sql += " AND  scm_sales_return.entry_date::date >= ?::date AND scm_sales_return.entry_date::date <= ?::date ";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(firstDate));
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(lastDate));
    }
    sql += "UNION ALL\n"
            + "SELECT scm_product_entry.id,scm_product_entry.product_entry_date,scm_purchase_return.id as invoice_id,scm_purchase_return_item.id as PRINV_id,'PURCHASE RETURN' as trade_event,scm_product_entry.invoice_no as account_invoice_no,\n"
            + "scm_purchase_return.invoice_no,scm_purchase_return.entry_date,scm_purchase_return.invoice_date,'' as parent_invoice_no,'' as customer_name,\n"
            + "scm_product.product_name,scm_product_batch.batch_no,scm_tax_code.rate_percentage as tax_rate,\n"
            + "ROUND(scm_purchase_return_item.actual_landing_price_per_piece_company,3) as landing_price,\n"
            + "0 as selling_price,\n"
            + "ROUND(scm_purchase_return_item.landing_price_per_piece_company,3) as sold_price,\n"
            + "'0' as margin,\n"
            + "0 as margin_difference_per_unit,\n"
            + "COALESCE(scm_purchase_return_item.quantity_returned,0)  as total_qty,\n"
            + "(ROUND(scm_purchase_return_item.actual_landing_price_per_piece_company,3) -ROUND(scm_purchase_return_item.landing_price_per_piece_company,3))*\n"
            + "COALESCE(scm_purchase_return_item.quantity_returned,0) as total_margin_difference,3 as invoice_type \n"
            + "FROM scm_purchase_return,scm_purchase_return_item,scm_product_entry_detail,scm_product_entry,scm_product_detail,scm_product_batch,scm_product,scm_tax_code \n"
            + "WHERE scm_purchase_return.id = scm_purchase_return_item.purchase_return_id AND scm_purchase_return_item.product_detail_id=scm_product_detail.id\n"
            + "AND scm_purchase_return_item.product_entry_detail_id= scm_product_entry_detail.id\n"
            + "AND scm_product_entry_detail.product_entry_id= scm_product_entry.id\n"
            + "AND scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_batch.product_id = scm_product.id \n"
            + "AND scm_tax_code.id=scm_purchase_return_item.tax_code_id \n"
            + "AND  scm_purchase_return.company_id=? and scm_product_entry.account_id=?  ";
    params.add(company.getId());
    params.add(account.getId());
    if (productEntry != null) {
      sql += " AND scm_product_entry.id=? ";
      params.add(productEntry.getId());
    }
    if (filterParameters != null) {
      sql += " AND  scm_purchase_return.entry_date::date >= ?::date AND scm_purchase_return.entry_date::date <= ?::date ";
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(firstDate));
      params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(lastDate));
    }
    sql += " ORDER BY 4 desc,8 asc,7,6) TAB ";
    return AppDb.getList(main.dbConnector(), PlatformSummary.class, sql, params.toArray());
  }

  public static List<CompanyCustomerSales> getDebitCreditList(Main main, Company company, Account account, ProductEntry productEntry, FilterParameters filterParameters) {

    List<Object> params = new ArrayList<Object>();
    Calendar cal = Calendar.getInstance();
    cal.setTime(filterParameters.getFromDate());
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    Date lastDate = cal.getTime();
    String sql = "SELECT dc_note.id,dc_note.document_no,dc_note.entry_date,dc_note.invoice_type,\n"
            + "CASE WHEN dc_note.invoice_type=1 THEN 'DEBIT NOTE' ELSE 'CREDIT NOTE' END as invoice_type,\n"
            + "dc_note.invoice_date,party.customer_name as party,item.title as product_name,item.hsn_sac_code as hsn_code,tax.rate_percentage as tax_perc,item.ref_invoice_no,item.ref_invoice_date,\n"
            + "item.taxable_value,CASE WHEN dc_note.is_interstate = 0 THEN item.cgst_amount ELSE 0 END as cgst,\n"
            + "CASE WHEN dc_note.is_interstate=0 THEN item.sgst_amount ELSE 0 END as sgst,\n"
            + "CASE WHEN dc_note.is_interstate=1 THEN item.igst_amount ELSE 0 END as igst,\n"
            + "item.igst_amount as gst_amount,item.net_value  as net_amount\n"
            + "FROM fin_debit_credit_note dc_note,fin_accounting_ledger ledger,scm_customer party,fin_debit_credit_note_item item\n"
            + "LEFT JOIN scm_tax_code tax ON tax.id=item.tax_code_id\n"
            + "LEFT JOIN scm_product_entry_detail ON scm_product_entry_detail.id = item.product_entry_detail_id\n"
            + "LEFT JOIN scm_product_entry ON scm_product_entry_detail.product_entry_id = scm_product_entry.id\n"
            + "LEFT JOIN scm_sales_invoice_item ON item.sales_invoice_item_id=scm_sales_invoice_item.id\n"
            + "WHERE dc_note.id=item.debit_credit_note_id AND ledger.id = dc_note.accounting_ledger_id\n"
            + "AND party.id = ledger.entity_id AND dc_note.debit_credit_party=2\n"
            + "AND dc_note.status_id=2\n"
            + "AND dc_note.company_id=? \n"
            + "AND dc_note.account_id=? \n"
            + "AND dc_note.entry_date::date>=?::date AND dc_note.entry_date::date<=?::date \n";

    params.add(company.getId());
    params.add(account.getId());
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(filterParameters.getFromDate()));
    params.add(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(lastDate));
    if (productEntry != null) {
      sql += " AND scm_product_entry.id=? ";
      params.add(productEntry.getId());
    }
    return AppDb.getList(main.dbConnector(), CompanyCustomerSales.class, sql, params.toArray());
  }
}
