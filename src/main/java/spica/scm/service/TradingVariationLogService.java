/*
 * @(#)PlatformService.java	1.0 Thu Dec 21 15:36:46 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.Platform;
import spica.scm.domain.PlatformDescription;
import spica.scm.domain.PlatformSource;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.TradingVariationLog;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;

/**
 * TradingVariationLogService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Dec 21 15:36:46 IST 2017
 */
public abstract class TradingVariationLogService {

  
  //FIXME many repetivive queries
  /**
   * TradingVariationLog paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTradingVariationLogSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_trading_variation_log", TradingVariationLog.class, main);
    sql.main("select t1.id,t1.document_no,t1.entry_date,t1.source_id,t1.platform_desc_id,t1.credit_amount_required,t1.debit_amount_required,t1.account_id,"
            + "t1.product_entry_id,t1.product_entry_detail_id,t1.created_by,t1.modified_by,t1.created_at,t1.modified_at "
            + "from scm_trading_variation_log t1 "); //Main query
    sql.count("select count(t1.id) as total from scm_trading_variation_log t1 "); //Count query
    sql.join("left outer join scm_platform_source t2 on (t2.id = t1.source_id) "
            + "left outer join scm_platform_description t3 on (t3.id = t1.platform_desc_id) "
            + "left outer join scm_account t4 on (t4.id = t1.account_id) "
            + "left outer join scm_product_entry t5 on (t5.id = t1.product_entry_id) "
            + "left outer join scm_product_entry_detail t6 on (t6.id = t1.product_entry_detail_id) "
            + "left outer join scm_product_detail t7 on (t7.id = t1.product_detail_id) "); //Join Query

    sql.string(new String[]{"t2.title", "t3.title", "t4.account_code", "t5.invoice_no", "t1.created_by", "t1.modified_by"}); //String search or sort fields
    sql.number(new String[]{"t1.id", "t1.debit_amount_required"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.created_at", "t1.modified_at"});  //Date search or sort fields
    return sql;
  }
    private static SqlPage getTradingVariation(Main main) {
    SqlPage sql = AppService.sqlPage("scm_trading_variation_log", TradingVariationLog.class, main);
    sql.main("select scm_trading_variation_log.id,scm_trading_variation_log.document_no,scm_trading_variation_log.entry_date,scm_trading_variation_log.source_id,scm_trading_variation_log.product_detail_id, "
            + "scm_trading_variation_log.account_id,scm_trading_variation_log.platform_desc_id,"
            + "scm_trading_variation_log.product_entry_id,scm_trading_variation_log.product_entry_detail_id,"
            + "scm_trading_variation_log.created_by,scm_trading_variation_log.modified_by,scm_trading_variation_log.created_at,scm_trading_variation_log.modified_at \n"
            + "from scm_trading_variation_log scm_trading_variation_log "); //Main query
    sql.count("select count(scm_trading_variation_log.id) as total from scm_trading_variation_log scm_trading_variation_log "); //Count query
    sql.join( "left outer join scm_platform_source scm_trading_variation_logsource_id on (scm_trading_variation_logsource_id.id = scm_trading_variation_log.source_id) "
            + "left outer join scm_platform_description scm_trading_variation_logplatform_desc_id on (scm_trading_variation_logplatform_desc_id.id = scm_trading_variation_log.platform_desc_id) "
            + "left outer join scm_account scm_trading_variation_logaccount_id on (scm_trading_variation_logaccount_id.id = scm_trading_variation_log.account_id) "
            + "left outer join scm_product_entry scm_trading_variation_logproduct_entry_id on (scm_trading_variation_logproduct_entry_id.id = scm_trading_variation_log.product_entry_id) "
            + "left outer join scm_product_entry_detail scm_trading_variation_logproduct_entry_detail_id on (scm_trading_variation_logproduct_entry_detail_id.id = scm_trading_variation_log.product_entry_detail_id) "
            + "left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_trading_variation_log.product_detail_id) ");
           
    sql.string(new String[]{"scm_trading_variation_log.document_no", "scm_trading_variation_logplatform_desc_id.title", "scm_trading_variation_logaccount_id.account_code",  "scm_trading_variation_logproduct_entry_id.invoice_no",  "scm_trading_variation_log.created_by", "scm_trading_variation_log.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_trading_variation_log.id", "scm_trading_variation_log.credit_amount_required", "scm_trading_variation_log.debit_amount_required", "scm_trading_variation_logproduct_entry_detail_id.product_quantity"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_trading_variation_log.entry_date", "scm_trading_variation_log.created_at", "scm_trading_variation_log.modified_at"});  //Date search or sort fields
    return sql;
  }
    private static SqlPage getTradingVariationSummary(Main main) {
    SqlPage sql = AppService.sqlPage("scm_trading_variation_log", TradingVariationLog.class, main);
    sql.main("select distinct on (scm_trading_variation_log.platform_desc_id) scm_trading_variation_log.platform_desc_id,scm_trading_variation_log.id,scm_trading_variation_log.document_no,scm_trading_variation_log.product_detail_id,scm_trading_variation_log.entry_date, "
            + "scm_trading_variation_log.source_id,sum(scm_trading_variation_log.credit_amount_required) OVER (PARTITION BY scm_trading_variation_log.platform_desc_id) credit_amount_required,\n"
            + "sum(scm_trading_variation_log.debit_amount_required) OVER (PARTITION BY scm_trading_variation_log.platform_desc_id) debit_amount_required,\n"
            + "scm_trading_variation_log.account_id,scm_trading_variation_log.product_entry_id,scm_trading_variation_log.product_entry_detail_id,"
            + "scm_trading_variation_log.created_by,scm_trading_variation_log.modified_by,scm_trading_variation_log.created_at,scm_trading_variation_log.modified_at \n"
            + "from scm_trading_variation_log scm_trading_variation_log "); //Main query
    sql.count("select count(scm_trading_variation_log.id) as total from scm_trading_variation_log scm_trading_variation_log "); //Count query
    sql.join( "left outer join scm_platform_source scm_trading_variation_logsource_id on (scm_trading_variation_logsource_id.id = scm_trading_variation_log.source_id) "
            + "left outer join scm_platform_description scm_trading_variation_logplatform_desc_id on (scm_trading_variation_logplatform_desc_id.id = scm_trading_variation_log.platform_desc_id) "
            + "left outer join scm_account scm_trading_variation_logaccount_id on (scm_trading_variation_logaccount_id.id = scm_trading_variation_log.account_id) "
            + "left outer join scm_product_entry scm_trading_variation_logproduct_entry_id on (scm_trading_variation_logproduct_entry_id.id = scm_trading_variation_log.product_entry_id) "
            + "left outer join scm_product_entry_detail scm_trading_variation_logproduct_entry_detail_id on (scm_trading_variation_logproduct_entry_detail_id.id = scm_trading_variation_log.product_entry_detail_id) "
            + "left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_trading_variation_log.product_detail_id) ");
           
    sql.string(new String[]{"scm_trading_variation_log.document_no", "scm_trading_variation_logplatform_desc_id.title", "scm_trading_variation_logaccount_id.account_code",  "scm_trading_variation_logproduct_entry_id.invoice_no",  "scm_trading_variation_log.created_by", "scm_trading_variation_log.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_trading_variation_log.id", "scm_trading_variation_log.credit_amount_required", "scm_trading_variation_log.debit_amount_required", "scm_trading_variation_logproduct_entry_detail_id.product_quantity"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_trading_variation_log.entry_date", "scm_trading_variation_log.created_at", "scm_trading_variation_log.modified_at"});  //Date search or sort fields
    return sql;
  }
    private static SqlPage getTradingVariationSummary1(Main main) {
    SqlPage sql = AppService.sqlPage("scm_trading_variation_log", TradingVariationLog.class, main);
    sql.main("select distinct on (scm_trading_variation_log.platform_desc_id) scm_trading_variation_log.platform_desc_id,scm_trading_variation_log.id,scm_trading_variation_log.document_no,scm_trading_variation_log.product_detail_id,scm_trading_variation_log.entry_date, "
            + "scm_trading_variation_log.source_id,sum(scm_trading_variation_log.credit_amount_required) OVER (PARTITION BY scm_trading_variation_log.platform_desc_id) credit_amount_required,\n"
            + "sum(scm_trading_variation_log.debit_amount_required) OVER (PARTITION BY scm_trading_variation_log.platform_desc_id) debit_amount_required,\n"
            + "scm_trading_variation_log.account_id,scm_trading_variation_log.product_entry_id,scm_trading_variation_log.product_entry_detail_id,"
            + "scm_trading_variation_log.created_by,scm_trading_variation_log.modified_by,scm_trading_variation_log.created_at,scm_trading_variation_log.modified_at \n"
            + "from scm_trading_variation_log scm_trading_variation_log "); //Main query
    sql.count("select count(scm_trading_variation_log.id) as total from scm_trading_variation_log scm_trading_variation_log "); //Count query
    sql.join( "left outer join scm_platform_source scm_trading_variation_logsource_id on (scm_trading_variation_logsource_id.id = scm_trading_variation_log.source_id) "
            + "left outer join scm_platform_description scm_trading_variation_logplatform_desc_id on (scm_trading_variation_logplatform_desc_id.id = scm_trading_variation_log.platform_desc_id) "
            + "left outer join scm_account scm_trading_variation_logaccount_id on (scm_trading_variation_logaccount_id.id = scm_trading_variation_log.account_id) "
            + "left outer join scm_product_entry scm_trading_variation_logproduct_entry_id on (scm_trading_variation_logproduct_entry_id.id = scm_trading_variation_log.product_entry_id) "
            + "left outer join scm_product_entry_detail scm_trading_variation_logproduct_entry_detail_id on (scm_trading_variation_logproduct_entry_detail_id.id = scm_trading_variation_log.product_entry_detail_id) "
            + "left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_trading_variation_log.product_detail_id) ");
           
    sql.string(new String[]{"scm_trading_variation_log.document_no", "scm_trading_variation_logplatform_desc_id.title", "scm_trading_variation_logaccount_id.account_code",  "scm_trading_variation_logproduct_entry_id.invoice_no",  "scm_trading_variation_log.created_by", "scm_trading_variation_log.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_trading_variation_log.id", "scm_trading_variation_log.credit_amount_required", "scm_trading_variation_log.debit_amount_required", "scm_trading_variation_logproduct_entry_detail_id.product_quantity"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_trading_variation_log.entry_date", "scm_trading_variation_log.created_at", "scm_trading_variation_log.modified_at"});  //Date search or sort fields
    return sql;
  }
  /**
   * Return List of TradingVariationLog.
   *
   * @param main
   * @return List of TradingVariationLog
   */
  public static final List<TradingVariationLog> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTradingVariationLogSqlPaged(main));
  }

  public static final List<TradingVariationLog> listPagedCredit(Main main, Account accountId, PlatformSource selectSource, int selectedSummary, TradingVariationLog p) {
    return listPagedDebitOrCredit(main, accountId, selectSource, selectedSummary, p, false);
  }

  public static final List<TradingVariationLog> listPagedDebit(Main main, Account accountId, PlatformSource selectSource, int selectedSummary, TradingVariationLog p) {
    return listPagedDebitOrCredit(main, accountId, selectSource, selectedSummary, p, true);
  }

  private static final List<TradingVariationLog> listPagedDebitOrCredit(Main main, Account accountId, PlatformSource selectSource, int selectedSummary, TradingVariationLog p, boolean isDebit) {
    if (accountId != null) {
      main.clear();
      SqlPage sql = null;
      if (selectedSummary == 1 || selectedSummary == 0) {
        sql = getTradingVariationSummary(main);
      } else if ((p != null && p.getLevel() == SystemConstants.SECOND_LEVEL) || selectedSummary == 2) {
        sql = getTradingVariation(main);
      } else {
        sql = getTradingVariationSummary1(main);
      }
      sql.cond("where scm_trading_variation_log.account_id = ? ");
      if (isDebit) {
        sql.cond("and scm_trading_variation_log.credit_amount_required is null");
      } else {
        sql.cond("and scm_trading_variation_log.debit_amount_required is null");
      }
      main.param(accountId.getId());
      //filterCondition(main, sql, selectSource, selectedDescription, selectedSummary, p);
      return AppService.listPagedJpa(main, sql);
    }
    return null;
  }

  /**
   * Select TradingVariationLog by key.
   *
   * @param main
   * @param platform
   * @return TradingVariationLog
   */
  public static final TradingVariationLog selectByPk(Main main, TradingVariationLog platform) {
    return (TradingVariationLog) AppService.find(main, TradingVariationLog.class, platform.getId());
  }

  /**
   * Insert TradingVariationLog.
   *
   * @param main
   * @param platform
   */
  public static final void insert(Main main, TradingVariationLog platform) {
    AppService.insert(main, platform);

  }

  /**
   * Update TradingVariationLog by key.
   *
   * @param main
   * @param platform
   * @return TradingVariationLog
   */
  public static final TradingVariationLog updateByPk(Main main, TradingVariationLog platform) {
    return (TradingVariationLog) AppService.update(main, platform);
  }

  /**
   * Insert or update TradingVariationLog
   *
   * @param main
   * @param platform
   */
  public static void insertOrUpdate(Main main, TradingVariationLog platform) {
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
  public static void clone(Main main, TradingVariationLog platform) {
    platform.setId(null); //Set to null for insert
    insert(main, platform);
  }

  /**
   * Delete TradingVariationLog.
   *
   * @param main
   * @param platform
   */
  public static final void deleteByPk(Main main, TradingVariationLog platform) {
    AppService.delete(main, TradingVariationLog.class, platform.getId());
  }

  /**
   * Delete Array of TradingVariationLog.
   *
   * @param main
   * @param platform
   */
  public static final void deleteByPkArray(Main main, TradingVariationLog[] platform) {
    for (TradingVariationLog e : platform) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param account
   * @param platformDesc
   * @param debit
   * @param credit
   * @param productEntry
   * @param productEntryDetail
   */
  public static final void insertPurchaseLog(Main main, Account account, PlatformDescription platformDesc, Double debit, Double credit, ProductEntry productEntry, ProductEntryDetail productEntryDetail) {
    double platformValue = debit == null ? credit : debit;
    TradingVariationLog tradingVariationLog = new TradingVariationLog(account, SystemRuntimeConfig.PURCHASE, debit, credit);
    tradingVariationLog.setProductEntryId(productEntry);
    tradingVariationLog.setProductEntryDetailId(productEntryDetail);
    if (platformValue >= SystemRuntimeConfig.PLATFORM_UNREALIZED_MAX_VALUE
            && platformValue <= SystemRuntimeConfig.PLATFORM_UNREALIZED_MIN_VALUE) {
      tradingVariationLog.setPlatformDescId(SystemRuntimeConfig.PLATFORM_UNREALIZED_AMOUNT);
    } else {
      tradingVariationLog.setPlatformDescId(platformDesc);
    }

    if (productEntryDetail != null) {
      tradingVariationLog.setProductDetailId(productEntryDetail.getProductDetailId());
    }
    if (productEntry != null) {
      tradingVariationLog.setDocumentNo(productEntry.getAccountInvoiceNo());
      tradingVariationLog.setEntryDate(productEntry.getProductEntryDate());
    }
    insertOrUpdate(main, tradingVariationLog);
  }
}
