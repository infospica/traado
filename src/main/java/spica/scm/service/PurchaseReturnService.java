/*
 * @(#)PurchaseReturnService.java	1.0 Wed May 25 13:23:32 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import spica.fin.service.LedgerExternalDataService;
import spica.scm.common.ProductInvoiceDetail;
import spica.scm.common.RelatedItems;
import spica.scm.domain.Account;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Consignment;
import spica.scm.domain.Platform;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntryDetail;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItem;
import spica.scm.domain.StockNonSaleable;
import spica.scm.domain.StockSaleable;
import spica.sys.SystemConstants;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 * PurchaseReturnService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:32 IST 2016
 */
public abstract class PurchaseReturnService {

  /**
   * PurchaseReturn paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReturnSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_return", PurchaseReturn.class, main);
    sql.main("select scm_purchase_return.id,scm_purchase_return.account_id,scm_purchase_return.company_id,scm_purchase_return.purchase_return_status_id,scm_purchase_return.no_of_boxes,scm_purchase_return.remarks,"
            + "scm_purchase_return.weight,scm_purchase_return.purchase_return_stock_cat,scm_purchase_return.purchase_return_stock_type,scm_purchase_return.commodity_id,"
            + "scm_purchase_return.product_type_id,scm_purchase_return.invoice_no,scm_purchase_return.invoice_date,scm_purchase_return.entry_date,scm_purchase_return.is_tax_code_modified,"
            + "scm_purchase_return.invoice_amount,scm_purchase_return.invoice_amount_net,scm_purchase_return.created_by,scm_purchase_return.modified_by,scm_purchase_return.created_at,"
            + "scm_purchase_return.modified_at,scm_purchase_return.confirmed_by,scm_purchase_return.confirmed_at,scm_purchase_return.to_be_approved_by,"
            + "scm_purchase_return.approved_by,scm_purchase_return.approved_at,scm_purchase_return.reference_no"
            + " from scm_purchase_return scm_purchase_return "); //Main query 
    sql.count("select count(scm_purchase_return.id) from scm_purchase_return scm_purchase_return "); //Count query
    sql.join("left outer join scm_account scm_account on (scm_account.id = scm_purchase_return.account_id) "
            + "left outer join scm_purchase_return_status scm_purchase_return_status on (scm_purchase_return_status.id = scm_purchase_return.purchase_return_status_id) "
            + "left outer join scm_purchase_return_stock_cat scm_purchase_return_stock_cat on (scm_purchase_return_stock_cat.id = scm_purchase_return.purchase_return_stock_cat) "
            + "left outer join scm_purchase_return_stock_type scm_purchase_return_stock_type on (scm_purchase_return_stock_type.id = scm_purchase_return.purchase_return_stock_type) "
            + "left outer join scm_service_commodity scm_service_commodity on (scm_service_commodity.id = scm_purchase_return.commodity_id) "
            + "left outer join scm_company scm_company on (scm_company.id = scm_purchase_return.company_id) "
            + "left outer join scm_product_type scm_product_type on (scm_product_type.id = scm_purchase_return.product_type_id)"); //Join Query

    sql.string(new String[]{"scm_purchase_return.remarks,", "scm_account.account_code", "scm_purchase_return_status.title", "scm_purchase_return_stock_cat.title", "scm_purchase_return_stock_type.title",
      "scm_service_commodity.title", "scm_product_type.title", "scm_purchase_return.invoice_no", "scm_purchase_return.created_by", "scm_purchase_return.modified_by", "scm_purchase_return.confirmed_by", "scm_purchase_return.to_be_approved_by", "scm_purchase_return.approved_by", "scm_purchase_return.reference_no"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_return.id", "scm_purchase_return.invoice_amount", "scm_purchase_return.invoice_amount_net"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_purchase_return.invoice_date", "scm_purchase_return.created_at", "scm_purchase_return.modified_at", "scm_purchase_return.confirmed_at", "scm_purchase_return.approved_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseReturn.
   *
   * @param main
   * @return List of PurchaseReturn
   */
  public static final List<PurchaseReturn> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseReturnSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseReturn based on condition
//   * @param main
//   * @return List<PurchaseReturn>
//   */
//  public static final List<PurchaseReturn> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseReturnSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PurchaseReturn by key.
   *
   * @param main
   * @param purchaseReturn
   * @return PurchaseReturn
   */
  public static final PurchaseReturn selectByPk(Main main, PurchaseReturn purchaseReturn) {
    return (PurchaseReturn) AppService.find(main, PurchaseReturn.class, purchaseReturn.getId());
  }

  /**
   * Insert PurchaseReturn.
   *
   * @param main
   * @param purchaseReturn
   */
  public static final void insert(Main main, PurchaseReturn purchaseReturn) {
    AppService.insert(main, purchaseReturn);
  }

  /**
   * Update PurchaseReturn by key.
   *
   * @param main
   * @param purchaseReturn
   * @return PurchaseReturn
   */
  public static final PurchaseReturn updateByPk(Main main, PurchaseReturn purchaseReturn) {
    return (PurchaseReturn) AppService.update(main, purchaseReturn);
  }

  /**
   * Insert or update PurchaseReturn
   *
   * @param main
   * @param purchaseReturn
   */
  public static void insertOrUpdate(Main main, PurchaseReturn purchaseReturn) {
    if (purchaseReturn.getId() == null) {
      insert(main, purchaseReturn);
      AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseReturn.getAccountId(), PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, true, purchaseReturn.getFinancialYearId());
    } else {
      if (SystemConstants.CONFIRMED == purchaseReturn.getPurchaseReturnStatusId().getId()) {
        if (purchaseReturn.getConfirmedAt() == null && purchaseReturn.getAccountId() != null) {
          purchaseReturn.setInvoiceNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, purchaseReturn.getAccountId(), PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, false, purchaseReturn.getFinancialYearId()));
          AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseReturn.getAccountId(), PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, false, purchaseReturn.getFinancialYearId());
        }
        if (StringUtil.isEmpty(purchaseReturn.getReferenceNo())) {
          purchaseReturn.setReferenceNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, purchaseReturn.getCompanyId(), PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, purchaseReturn.getFinancialYearId()));
          AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseReturn.getCompanyId(), PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, purchaseReturn.getFinancialYearId());
        }
        purchaseReturn.setConfirmedAt(new Date());
        purchaseReturn.setConfirmedBy(main.getAppUser().getLogin());
      }
      updateByPk(main, purchaseReturn);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReturn
   */
  public static void clone(Main main, PurchaseReturn purchaseReturn) {
    purchaseReturn.setId(null); //Set to null for insert
    insert(main, purchaseReturn);
  }

  /**
   * Delete PurchaseReturn.
   *
   * @param main
   * @param purchaseReturn
   */
  public static final void deleteByPk(Main main, PurchaseReturn purchaseReturn) {
    resetPurchaseReturnStatusToDraft(main, purchaseReturn);
    AppService.deleteSql(main, PurchaseReturn.class, "delete from scm_purchase_return_item scm_purchase_return_item where scm_purchase_return_item.purchase_return_id=?", new Object[]{purchaseReturn.getId()});
    AppService.delete(main, PurchaseReturn.class, purchaseReturn.getId());
  }

  /**
   * Delete Array of PurchaseReturn.
   *
   * @param main
   * @param purchaseReturn
   */
//  public static final void deleteByPkArray(Main main, PurchaseReturn[] purchaseReturn) {
//    for (PurchaseReturn e : purchaseReturn) {
//      deleteByPk(main, e);
//    }
//  }
  public static List<PurchaseReturn> listPurchaseReturnByAccount(Main main, Account account, AccountingFinancialYear accountingFinancialYear) {
    if (account.getId() == null) {
      return null;
    }
    SqlPage sql = getPurchaseReturnSqlPaged(main);
    sql.cond("where scm_purchase_return.account_id = ? AND scm_purchase_return.entry_date::date >= ?::date AND scm_purchase_return.entry_date::date <= ?::date ");
    sql.param(account.getId());
    sql.param(accountingFinancialYear.getStartDate());
    sql.param(accountingFinancialYear.getEndDate());
    return AppService.listPagedJpa(main, sql);
  }

  public static List<PurchaseReturn> selectByPRConsignment(MainView main, Consignment consignment) {
    SqlPage sql = getPurchaseReturnSqlPaged(main);
    sql.join("left outer join scm_consignment_reference scm_consignment_reference on (scm_purchase_return.id = scm_consignment_reference.purchase_return_id)");
    if (consignment.getId() == null) {
      return null;
    }
    sql.cond("where scm_consignment_reference.consignment_id = ?");
    sql.param(consignment.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static List<PurchaseReturn> selectPR(MainView main, Consignment consignment) {
    SqlPage sql = getPurchaseReturnSqlPaged(main);
    if (consignment.getId() == null) {
      return null;
    }
    sql.cond("where scm_purchase_return.id not in(select scm_consignment_reference.purchase_return_id from scm_consignment_reference scm_consignment_reference where scm_consignment_reference.consignment_id =?)");
    sql.param(consignment.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static List<PurchaseReturn> selectPurchaseReturnInvoice(Main main, Account accountId, int status) {
    SqlPage sql = getPurchaseReturnSqlPaged(main);
    sql.cond("where scm_purchase_return.account_id = ? and scm_purchase_return.invoice_no not in(select scm_consignment_commodity.invoice_no from scm_consignment_commodity)and scm_purchase_return.purchase_return_status_id =?");
    sql.param(accountId.getId());
    sql.param(status);
    return AppService.listAllJpa(main, sql);
  }

  public static List<RelatedItems> selectRelatedItemsOfPurchaseReturn(Main main, PurchaseReturn purchaseReturn) {
    StringBuilder sql = new StringBuilder();
    sql.append("select id, invoice_no, 'Invoice Entry' as title from scm_product_entry where id in(select product_entry_id from scm_product_entry_detail \n"
            + "where id in(select product_entry_detail_id from scm_purchase_return_item where purchase_return_id = ?))");

    return AppDb.getList(main.dbConnector(), RelatedItems.class, sql.toString(), new Object[]{purchaseReturn.getId()});
  }

  /**
   *
   * @param main
   * @param purchaseReturn
   */
  public static void resetPurchaseReturnStatusToDraft(Main main, PurchaseReturn purchaseReturn) {

    AppService.deleteSql(main, Platform.class, "delete from scm_platform where purchase_return_item_id in (select id from scm_purchase_return_item where purchase_return_id = ?)", new Object[]{purchaseReturn.getId()});
    AppService.deleteSql(main, Platform.class, "delete from scm_platform where purchase_return_id = ?", new Object[]{purchaseReturn.getId()});

    AppService.deleteSql(main, StockNonSaleable.class, "delete from scm_stock_non_saleable where purchase_return_item_id in (select id from scm_purchase_return_item where purchase_return_id = ?) ",
            new Object[]{purchaseReturn.getId()});

    AppService.deleteSql(main, StockSaleable.class, "delete from scm_stock_saleable where purchase_return_item_id in (select id from scm_purchase_return_item where purchase_return_id = ?) ", new Object[]{purchaseReturn.getId()});

    LedgerExternalDataService.deletePurchaseReturn(main, purchaseReturn);

    long taxChangeCount = AppService.count(main, "select count(t1.id) from scm_purchase_return_item t1 "
            + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
            + "inner join scm_product_batch t3 on t2.product_batch_id = t3.id "
            + "inner join scm_product t4 on t3.product_id = t4.id "
            + "inner join scm_product_category t5 on t4.product_category_id = t5.id "
            + "inner join scm_tax_code t6 on t5.purchase_tax_code_id = t6.id "
            + "inner join scm_tax_code t7 on t7.id = t1.tax_code_id "
            + "where t1.purchase_return_id = ? and t7.rate_percentage != t6.rate_percentage", new Object[]{purchaseReturn.getId()});

    if (taxChangeCount == 0) {
      main.clear();
      main.param(SystemConstants.DRAFT);
      main.param(purchaseReturn.getId());
      AppService.updateSql(main, PurchaseReturn.class, "update scm_purchase_return set purchase_return_status_id = ? where id = ?", false);
      main.clear();
    } else {
      main.clear();
      main.param(SystemConstants.DRAFT);
      main.param(purchaseReturn.getId());
      AppService.updateSql(main, PurchaseReturn.class, "update scm_purchase_return set purchase_return_status_id = ?, is_tax_code_modified = 1 where id = ?", false);
      main.clear();

      main.param(purchaseReturn.getId());
      AppService.updateSql(main, PurchaseReturnItem.class, "update scm_purchase_return_item set is_tax_code_modified = 1 "
              + "where id in(select t1.id from scm_purchase_return_item t1 "
              + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
              + "inner join scm_product_batch t3 on t2.product_batch_id = t3.id "
              + "inner join scm_product t4 on t3.product_id = t4.id "
              + "inner join scm_product_category t5 on t4.product_category_id = t5.id "
              + "inner join scm_tax_code t6 on t5.purchase_tax_code_id = t6.id "
              + "inner join scm_tax_code t7 on t7.id = t1.tax_code_id "
              + "where t1.purchase_return_id = ? and t7.rate_percentage != t6.rate_percentage)", false);
      main.clear();
    }

  }

  public static boolean isProductValueMrpChanged(Main main, PurchaseReturn purchaseReturn) {
    return AppService.exist(main, "select 1 from scm_purchase_return_item where purchase_return_id = ? and is_mrp_changed = ?", new Object[]{purchaseReturn.getId(), SystemConstants.DEFAULT});
  }

  public static boolean isPurchaseReturnEditable(MainView main, PurchaseReturn updatePurchaseReturn) {
    PurchaseReturn purchaseReturn = selectByPk(main, updatePurchaseReturn);
    if (purchaseReturn.getModifiedAt() == null) {
      return true;
    } else if (purchaseReturn.getModifiedAt() != null && updatePurchaseReturn.getModifiedAt() == null) {
      return false;
    } else {
      return isPastModifiedDate(purchaseReturn.getModifiedAt(), updatePurchaseReturn.getModifiedAt());
    }
  }

  private static boolean isPastModifiedDate(Date currentDate, Date modifiedDate) {
    return !currentDate.after(modifiedDate);
  }

  public static List<ProductInvoiceDetail> purchaseHistoryList(Main main, Account account, ProductDetail productDetail, ProductEntryDetail productEntryDetail) {
    List<ProductInvoiceDetail> productInvoiceDetailList = new ArrayList<>();
    String sql = "select entry.id,entry.invoice_no,entry.invoice_date,ped.batch_no,ped.value_pts,ped.scheme_discount_value_derived as scheme_discount,ped.scheme_discount_percentage,"
            + "ped.product_discount_value_derived as product_discount,ped.discount_percentage as product_discount_percentage,ped.invoice_discount_value_derived as invoice_discount,"
            + "ped.invoice_discount_perc_derived as invoice_discount_percentage,\n"
            + "ped.value_rate as rate,ped.invoice_discount_value_derived,ped.invoice_discount_value,ped.landing_price_per_piece_company as land_rate,ped.is_scheme_discount_to_customer,\n"
            + "ped.is_product_discount_to_customer,(ped.product_quantity+COALESCE(ped.product_quantity_free,0)) as qty,CASE WHEN entry.sales_return_id is null THEN 0 ELSE 1 END as is_sales_return_invoice\n"
            + "from scm_product_entry_detail ped,scm_product_entry entry \n"
            + "where ped.product_detail_id=? and entry.account_id = ? and ped.id=? and ped.product_entry_id = entry.id order by entry.invoice_date desc\n";
    productInvoiceDetailList = AppDb.getList(main.dbConnector(), ProductInvoiceDetail.class, sql, new Object[]{productDetail.getId(), account.getId(), productEntryDetail.getId()});
    return productInvoiceDetailList;
  }
}
