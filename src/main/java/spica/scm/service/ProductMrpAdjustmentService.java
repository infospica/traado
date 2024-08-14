/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductMrpAdjustment;
import spica.scm.domain.ProductMrpAdjustmentItem;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItem;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;

/**
 *
 * @author Anoop Jayachandran
 */
public abstract class ProductMrpAdjustmentService {

  /**
   * ProductMrpAdjustment paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductMrpAdjustmentSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_mrp_adjustment", ProductMrpAdjustment.class, main);
    sql.main("select scm_product_mrp_adjustment.id,scm_product_mrp_adjustment.note,scm_product_mrp_adjustment.product_id,scm_product_mrp_adjustment.status_id,"
            + "scm_product_mrp_adjustment.company_id,scm_product_mrp_adjustment.created_by,scm_product_mrp_adjustment.modified_by,scm_product_mrp_adjustment.created_at,"
            + "scm_product_mrp_adjustment.modified_at, scm_product_mrp_adjustment.confirmed_at, scm_product_mrp_adjustment.confirmed_by, "
            + "scm_product_mrp_adjustment.product_batch_id "
            + "from scm_product_mrp_adjustment scm_product_mrp_adjustment"); //Main query
    sql.count("select count(scm_product_mrp_adjustment.id) as total from scm_product_mrp_adjustment scm_product_mrp_adjustment"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_product_mrp_adjustment.note", "scm_product_mrp_adjustment.created_by", "scm_product_mrp_adjustment.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_mrp_adjustment.id", "scm_product_mrp_adjustment.status_id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_mrp_adjustment.created_at", "scm_product_mrp_adjustment.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductMrpAdjustment.
   *
   * @param main
   * @return List of ProductMrpAdjustment
   */
  public static final List<ProductMrpAdjustment> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductMrpAdjustmentSqlPaged(main));
  }

  /**
   *
   * @param main
   * @param account
   * @return
   */
  public static final List<ProductMrpAdjustment> listProductMrpAdjustmentByAccount(Main main, Company company) {
    SqlPage sql = getProductMrpAdjustmentSqlPaged(main);
    sql.cond("where scm_product_mrp_adjustment.company_id = ?");
    main.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of ProductMrpAdjustment based on condition
//   * @param main
//   * @return List<ProductMrpAdjustment>
//   */
//  public static final List<ProductMrpAdjustment> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductMrpAdjustmentSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductMrpAdjustment by key.
   *
   * @param main
   * @param productMrpAdjustment
   * @return ProductMrpAdjustment
   */
  public static final ProductMrpAdjustment selectByPk(Main main, ProductMrpAdjustment productMrpAdjustment) {
    return (ProductMrpAdjustment) AppService.find(main, ProductMrpAdjustment.class, productMrpAdjustment.getId());
  }

  /**
   * Insert ProductMrpAdjustment.
   *
   * @param main
   * @param productMrpAdjustment
   */
  public static final void insert(Main main, ProductMrpAdjustment productMrpAdjustment) {
    insertAble(main, productMrpAdjustment);  //Validating
    AppService.insert(main, productMrpAdjustment);

  }

  /**
   * Update ProductMrpAdjustment by key.
   *
   * @param main
   * @param productMrpAdjustment
   * @return ProductMrpAdjustment
   */
  public static final ProductMrpAdjustment updateByPk(Main main, ProductMrpAdjustment productMrpAdjustment) {
    updateAble(main, productMrpAdjustment); //Validating
    return (ProductMrpAdjustment) AppService.update(main, productMrpAdjustment);
  }

  /**
   * Insert or update ProductMrpAdjustment
   *
   * @param main
   * @param productMrpAdjustment
   */
  public static void insertOrUpdate(Main main, ProductMrpAdjustment productMrpAdjustment) {
    if (productMrpAdjustment.getId() == null) {
      insert(main, productMrpAdjustment);
    } else {
      updateByPk(main, productMrpAdjustment);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productMrpAdjustment
   */
  public static void clone(Main main, ProductMrpAdjustment productMrpAdjustment) {
    productMrpAdjustment.setId(null); //Set to null for insert
    insert(main, productMrpAdjustment);
  }

  /**
   * Delete ProductMrpAdjustment.
   *
   * @param main
   * @param productMrpAdjustment
   */
  public static final void deleteByPk(Main main, ProductMrpAdjustment productMrpAdjustment) {
    deleteAble(main, productMrpAdjustment); //Validation
    AppService.delete(main, ProductMrpAdjustment.class, productMrpAdjustment.getId());
  }

  /**
   * Delete Array of ProductMrpAdjustment.
   *
   * @param main
   * @param productMrpAdjustment
   */
  public static final void deleteByPkArray(Main main, ProductMrpAdjustment[] productMrpAdjustment) {
    for (ProductMrpAdjustment e : productMrpAdjustment) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param productMrpAdjustment
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, ProductMrpAdjustment productMrpAdjustment) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param productMrpAdjustment
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, ProductMrpAdjustment productMrpAdjustment) throws UserMessageException {
  }

  /**
   * Validate update.
   *
   * @param main
   * @param productMrpAdjustment
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, ProductMrpAdjustment productMrpAdjustment) throws UserMessageException {
  }

  public static void insertOrUpdate(Main main, ProductMrpAdjustment productMrpAdjustment, List<ProductMrpAdjustmentItem> productMrpAdjustmentItemList, boolean productModified) {

    insertOrUpdate(main, productMrpAdjustment);

    ProductMrpAdjustmentItemService.insertOrUpdate(main, productMrpAdjustment, productMrpAdjustmentItemList, productModified);

    if (SystemConstants.CONFIRMED.equals(productMrpAdjustment.getStatusId())) {

      // update product batch mrp
      ProductBatch batch = (ProductBatch) AppService.single(main, ProductBatch.class, "select * from scm_product_batch where id=?", new Object[]{productMrpAdjustment.getProductBatchId().getId()});
      batch.setValueMrp(productMrpAdjustment.getValueMrp());
      AppService.update(main, batch);
      // intsert update default price list value
      ProductPricelistService.updatePriceList(main, productMrpAdjustmentItemList);

      ProductPricelistService.updateCustomPriceList(main, productMrpAdjustment);

      // post rate difference to platform
      PlatformService.insertProductMrpAdjustment(main, productMrpAdjustment, productMrpAdjustmentItemList);

      // Flag mrp change in PE, SA, SR and PR
      updateIsMrpChangedFlag(main, productMrpAdjustment);
      // update mrp_adj_rate_diff in sales invoice
      updateMrpAdjustmentDiffInSales(main, productMrpAdjustmentItemList);
    }
  }

  private static void updateIsMrpChangedFlag(Main main, ProductMrpAdjustment productMrpAdjustment) {
    main.clear();
    main.param(SystemConstants.DEFAULT);
    //main.param(SystemConstants.DRAFT);
    main.param(productMrpAdjustment.getProductBatchId().getId());

    AppService.updateSql(main, ProductEntryDetail.class, "update scm_product_entry_detail set is_mrp_changed = ? "
            + "from scm_product_entry "
            + "where scm_product_entry_detail.product_entry_id = scm_product_entry.id "
            + "and product_detail_id in(select id from scm_product_detail where product_batch_id = ?)", false);

    AppService.updateSql(main, SalesInvoiceItem.class, "update scm_sales_invoice_item set is_mrp_changed = ? "
            + "from scm_sales_invoice "
            + "where scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id "
            + "and product_detail_id in (select id from scm_product_detail where product_batch_id = ?)", false);

    AppService.updateSql(main, SalesReturnItem.class, "update scm_sales_return_item set is_mrp_changed = ? "
            + "from scm_sales_return "
            + "where scm_sales_return.id = scm_sales_return_item.sales_return_id  "
            + "and product_detail_id in(select id from scm_product_detail where product_batch_id = ?)", false);

    AppService.updateSql(main, PurchaseReturnItem.class, "update scm_purchase_return_item set is_mrp_changed = ? "
            + "from scm_purchase_return "
            + "where scm_purchase_return.id = scm_purchase_return_item.purchase_return_id "
            + "and product_detail_id in(select id from scm_product_detail where product_batch_id = ?)", false);

    main.clear();

    main.param(SystemConstants.DEFAULT);
    //main.param(SystemConstants.CONFIRMED);
    main.param(productMrpAdjustment.getProductBatchId().getId());
    AppService.updateSql(main, ProductEntry.class, "update scm_product_entry set is_mrp_changed = ? from scm_product_entry_detail "
            + "where scm_product_entry_detail.product_entry_id = scm_product_entry.id  "
            + "and scm_product_entry_detail.product_detail_id in(select id from scm_product_detail where product_batch_id = ?)", false);

    AppService.updateSql(main, SalesInvoice.class, "update scm_sales_invoice set is_mrp_changed = ? from scm_sales_invoice_item "
            + "where scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id "
            + "and scm_sales_invoice_item.product_detail_id in (select id from scm_product_detail where product_batch_id = ?)", false);

    AppService.updateSql(main, SalesReturn.class, "update scm_sales_return set is_mrp_changed = ? from scm_sales_return_item "
            + "where scm_sales_return.id = scm_sales_return_item.sales_return_id "
            + "and scm_sales_return_item.product_detail_id in(select id from scm_product_detail where product_batch_id = ?)", false);

    AppService.updateSql(main, PurchaseReturn.class, "update scm_purchase_return  set is_mrp_changed = ? from scm_purchase_return_item "
            + "where scm_purchase_return.id = scm_purchase_return_item.purchase_return_id  "
            + "and scm_purchase_return_item.product_detail_id in(select id from scm_product_detail where product_batch_id = ?)", false);

    main.clear();
  }

  private static void updateMrpAdjustmentDiffInSales(Main main, List<ProductMrpAdjustmentItem> productMrpAdjustmentItems) {
    for (ProductMrpAdjustmentItem adjustmentItem : productMrpAdjustmentItems) {
      List<SalesInvoiceItem> salesInvoiceItemList = null;
      salesInvoiceItemList = AppService.list(main, SalesInvoiceItem.class, "select * from scm_sales_invoice_item "
              + "where scm_sales_invoice_item.product_entry_detail_id=? "
              + "and product_detail_id =?", new Object[]{adjustmentItem.getProductEntryDetailId().getId(), adjustmentItem.getProductDetailId().getId()});
      for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItemList) {
        salesInvoiceItem.setMrpAdjRateDiff(adjustmentItem.getExpectedLandingRate()
                - ((salesInvoiceItem.getLandingRate() != null ? salesInvoiceItem.getLandingRate() : 0.0)
                + (salesInvoiceItem.getMarginValueDeviationDer() != null ? salesInvoiceItem.getMarginValueDeviationDer() : 0.0)));
        AppService.update(main, salesInvoiceItem);
      }

    }
  }

}
