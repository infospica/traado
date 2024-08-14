/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import spica.scm.common.ProductRateSummary;
import spica.scm.common.StockOfProduct;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Product;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductPricelist;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItem;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.util.MathUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;

/**
 *
 * @author java-2
 */
public abstract class ProductRateSummaryService {

  /**
   *
   * @param main
   * @param productCategory
   * @return
   */
  public static List<ProductRateSummary> getProductRateSummaryList(Main main, ProductCategory productCategory) {
    String sql = "select t1.id, t1.product_name, t3.id as product_detail_id, t2.batch_no, t2.expiry_date_actual,t2.value_mrp,t4.pts_derivation_criteria, "
            + "t5.mrplte_ptr_rate_derivation_criterion,t5.ptr_pts_rate_derivation_criterion,t5.ptr_margin_percentage,t5.pts_margin_percentage, "
            + "t6.value_ptr_per_prod_piece as actual_ptr, t6.value_pts_per_prod_piece as actual_pts,0.0 as new_ptr, 0.0 as new_pts from scm_product t1 "
            + "inner join scm_product_batch t2 on t2.product_id = t1.id "
            + "inner join scm_product_detail t3 on t3.product_batch_id = t2.id "
            + "inner join scm_account t4 on t4.id = t3.account_id "
            + "inner join scm_product_preset t5 on t5.id = t3.product_preset_id "
            + "inner join scm_product_pricelist t6 on t6.product_detail_id = t3.id "
            + "where t1.product_category_id = ? "
            + "group by t1.id, t1.product_name, t3.id, t2.batch_no, t2.expiry_date_actual,t2.value_mrp,t4.pts_derivation_criteria, "
            + "t5.mrplte_ptr_rate_derivation_criterion,t5.ptr_pts_rate_derivation_criterion,t5.ptr_margin_percentage,t5.pts_margin_percentage, "
            + "t6.value_ptr_per_prod_piece,t6.value_pts_per_prod_piece,new_ptr,new_pts order by t1.product_name;";
    return AppDb.getList(main.dbConnector(), ProductRateSummary.class, sql, new Object[]{productCategory.getId()});
  }

  /**
   *
   * @param main
   * @param productRateSummaryList
   */
  public static void updateProductPriceList(Main main, List<ProductRateSummary> productRateSummaryList) {
    List<ProductPricelist> defaultProductPriceList = null;
    List<ProductPricelist> customProductPriceList = null;
    StockOfProduct stockOfProduct = null;
    Double oldPts = null;
    long availableQty;
    AccountGroup accountGroup;
    AccountGroupPriceList accountGroupPriceList;
    Product product;
    for (ProductRateSummary productRateSummary : productRateSummaryList) {
      defaultProductPriceList = AppService.list(main, ProductPricelist.class, "select t1.* from scm_product_pricelist t1 "
              + "inner join scm_account_group_price_list t2 on t2.id = t1.account_group_price_list_id and t2.is_default = ? "
              + "inner join scm_product_detail t3 on t1.product_detail_id = t3.id and t3.id = ? "
              + "inner join scm_account t4 on t4.id = t3.account_id and t4.account_status_id = ? and t4.pts_derivation_criteria = ? ",
              new Object[]{SystemConstants.DEFAULT, productRateSummary.getProductDetailId(), AccountService.ACTIVE, AccountService.DERIVE_PTS_FROM_MRP_PTR});

      customProductPriceList = AppService.list(main, ProductPricelist.class, "select t1.* from scm_product_pricelist t1 "
              + "inner join scm_account_group_price_list t2 on t2.id = t1.account_group_price_list_id and t2.is_default <> ? "
              + "inner join scm_product_detail t3 on t1.product_detail_id = t3.id and t3.id = ? "
              + "inner join scm_account t4 on t4.id = t3.account_id and t4.account_status_id = ? and t4.pts_derivation_criteria = ? "
              + "order by t2.id", new Object[]{SystemConstants.DEFAULT, productRateSummary.getProductDetailId(), AccountService.ACTIVE, AccountService.DERIVE_PTS_FROM_MRP_PTR});

      for (ProductPricelist productPrice : defaultProductPriceList) {
        product = productPrice.getProductDetailId().getProductBatchId().getProductId();
        oldPts = productPrice.getValuePtsPerProdPieceSell();

        accountGroupPriceList = productPrice.getAccountGroupPriceListId();
        accountGroup = accountGroupPriceList.getAccountGroupId();
        availableQty = AppService.count(main, "select sum(quantity_available)::integer from getProductsDetailsForGstSale(?,?,?,null,?) "
                + "where product_detail_id = ? and pricelist_pts = ? and pricelist_ptr = ?",
                new Object[]{accountGroup.getId(), product.getId(), accountGroupPriceList.getId(), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()), productPrice.getProductDetailId(),
                  productPrice.getValuePtsPerProdPieceSell(), productPrice.getValuePtrPerProdPieceSell()});

        //stockOfProduct = (StockOfProduct) AppService.single(main, StockOfProduct.class, "select * from getstockofproduct(?,?) where product_detail_id = ?",
        //        new Object[]{productPrice.getProductDetailId().getAccountId().getId(), product.getId(), productPrice.getProductDetailId().getId()});
        productPrice.setValuePtrPerProdPieceSell(MathUtil.roundOff(productRateSummary.getNewPtr(), 2));
        productPrice.setValuePtsPerProdPieceSell(MathUtil.roundOff(productRateSummary.getNewPts(), 2));

        //double ptsVariation = (productRateSummary.getNewPts() - oldPts) * (stockOfProduct.getQuantitySaleable() == null ? 0 : stockOfProduct.getQuantitySaleable());
        double ptsVariation = (productRateSummary.getNewPts() - oldPts) * (availableQty);
        if (ptsVariation != 0) {
          if (ptsVariation > 0) {
            PlatformService.insertSales(main, productPrice.getProductDetailId().getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, Math.abs(ptsVariation), null, null, null, PlatformService.NORMAL_FUND_STATE);
          } else {
            PlatformService.insertSales(main, productPrice.getProductDetailId().getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, null, Math.abs(ptsVariation), null, null, PlatformService.NORMAL_FUND_STATE);
          }
        }

        ProductPricelistService.updateByPk(main, productPrice);
      }

      for (ProductPricelist customProductPrice : customProductPriceList) {
        customProductPrice.setValuePtrPerProdPieceSell(null);
        customProductPrice.setValuePtsPerProdPieceSell(null);
      }
    }
  }

  /**
   *
   * @param main
   * @param productCategory
   * @return
   */
  public static long isProductCategoryConsumed(Main main, ProductCategory productCategory) {
    long count = AppService.count(main, "select sum(product_count) product_count from ( "
            + "select count(id) product_count from scm_product_entry "
            + "where product_entry_status_id = ? and id in(select product_entry_id from scm_product_entry_detail "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?)))) "
            + "union all "
            + "select count(id) product_count from scm_sales_invoice "
            + "where sales_invoice_status_id = ? and id in(select sales_invoice_id from scm_sales_invoice_item  "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?)))) "
            + "union all "
            + "select count(id) product_count from scm_sales_return "
            + "where sales_return_status_id = ? and id in(select sales_return_id from scm_sales_return_item  "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?)))) "
            + "union all "
            + "select count(id) product_count from scm_purchase_return "
            + "where purchase_return_status_id = ? and id in(select purchase_return_id from scm_purchase_return_item  "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))))) tab ",
            new Object[]{SystemConstants.DRAFT, productCategory.getId(), SystemConstants.DRAFT, productCategory.getId(),
              SystemConstants.DRAFT, productCategory.getId(), SystemConstants.DRAFT, productCategory.getId()});
    return count;
  }

  /**
   *
   * @param main
   * @param productCategory
   */
  public static void updateTaxCodeModifiedFlag(Main main, ProductCategory productCategory) {
    // updates the is_tax_code_modified flag of Product entry and Product entry detail
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(productCategory.getId());
    AppService.updateSql(main, ProductEntry.class, "update scm_product_entry set is_tax_code_modified = ? "
            + "where product_entry_status_id = ? and id in(select product_entry_id from scm_product_entry_detail "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))))", false);

    // updates the is_tax_code_modified flag of Product entry detail
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(productCategory.getId());
    main.param(productCategory.getId());
    AppService.updateSql(main, ProductEntryDetail.class, "update scm_product_entry_detail set is_tax_code_modified = ? "
            + "where product_entry_id in( "
            + "select id from scm_product_entry "
            + "where product_entry_status_id = ? and id in(select product_entry_id from scm_product_entry_detail "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))))) "
            + "and product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?)))", false);

    // updates the is_tax_code_modified flag of Sales Invoice
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(productCategory.getId());
    AppService.updateSql(main, SalesInvoice.class, "update scm_sales_invoice set is_tax_code_modified = ? "
            + "where sales_invoice_status_id = ? and id in(select sales_invoice_id from scm_sales_invoice_item "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))))", false);

    // updates the is_tax_code_modified flag of Sales Invoice item
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(productCategory.getId());
    main.param(productCategory.getId());
    AppService.updateSql(main, SalesInvoiceItem.class, "update scm_sales_invoice_item set is_tax_code_modified = ? "
            + "where sales_invoice_id in(select id from scm_sales_invoice "
            + "where sales_invoice_status_id = ? and id in(select sales_invoice_id from scm_sales_invoice_item  "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))))) "
            + "and product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?)))", false);

    // updates the is_tax_code_modified flag of Sales Return and Sales Return Item
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(productCategory.getId());
    AppService.updateSql(main, SalesReturn.class, "update scm_sales_return set is_tax_code_modified = ? "
            + "where sales_return_status_id = ? and id in(select sales_return_id from scm_sales_return_item "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))))", false);

    // updates the is_tax_code_modified flag of Sales Return Item
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(productCategory.getId());
    main.param(productCategory.getId());
    AppService.updateSql(main, SalesReturnItem.class, "update scm_sales_return_item set is_tax_code_modified = ? "
            + "where sales_return_id in(select id from scm_sales_return "
            + "where sales_return_status_id = ? and id in(select sales_return_id from scm_sales_return_item  "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))))) "
            + "and product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))) ", false);

    // updates the is_tax_code_modified flag of Purchase Return
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(productCategory.getId());
    AppService.updateSql(main, PurchaseReturn.class, "update scm_purchase_return set is_tax_code_modified = ? "
            + "where purchase_return_status_id = ? and id in(select purchase_return_id from scm_purchase_return_item "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))))", false);

    // updates the is_tax_code_modified flag of Purchase Return Item
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(productCategory.getId());
    main.param(productCategory.getId());
    AppService.updateSql(main, PurchaseReturnItem.class, "update scm_purchase_return_item set is_tax_code_modified = ? "
            + "where purchase_return_id in(select id from scm_purchase_return "
            + "where purchase_return_status_id = ? and id in(select purchase_return_id from scm_purchase_return_item "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))))) "
            + "and product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?)))", false);
    main.clear();

  }

}
