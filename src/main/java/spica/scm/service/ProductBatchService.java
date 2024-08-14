/*
 * @(#)ProductBatchService.java	1.0 Mon May 21 17:45:02 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Product;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntry;
import spica.scm.validate.ProductBatchIs;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 * ProductBatchService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon May 21 17:45:02 IST 2018
 */
public abstract class ProductBatchService {

  /**
   * ProductBatch paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductBatchSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_batch", ProductBatch.class, main);
    sql.main("select scm_product_batch.id,scm_product_batch.product_id,scm_product_batch.product_packing_detail_id,scm_product_batch.default_product_packing_detail_id,scm_product_batch.batch_no,scm_product_batch.expiry_date_actual,scm_product_batch.expiry_date_sales,scm_product_batch.value_mrp,scm_product_batch.is_saleable,scm_product_batch.created_by,scm_product_batch.modified_by,scm_product_batch.created_at,scm_product_batch.modified_at from scm_product_batch scm_product_batch "); //Main query
    sql.count("select count(scm_product_batch.id) as total from scm_product_batch scm_product_batch "); //Count query
    sql.join("left outer join scm_product scm_product_batchproduct_id on (scm_product_batchproduct_id.id = scm_product_batch.product_id) left outer join scm_product_packing_detail scm_product_batchproduct_packing_detail_id on (scm_product_batchproduct_packing_detail_id.id = scm_product_batch.product_packing_detail_id) left outer join scm_product_packing_detail scm_product_batchdefault_product_packing_detail_id on (scm_product_batchdefault_product_packing_detail_id.id = scm_product_batch.default_product_packing_detail_id)"); //Join Query

    sql.string(new String[]{"scm_product_batchproduct_id.product_name", "scm_product_batch.batch_no", "scm_product_batch.created_by", "scm_product_batch.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_batch.id", "scm_product_batchproduct_packing_detail_id.pack_tertiary_secondary_qty", "scm_product_batchdefault_product_packing_detail_id.pack_tertiary_secondary_qty", "scm_product_batch.value_mrp", "scm_product_batch.is_saleable"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_batch.expiry_date_actual", "scm_product_batch.expiry_date_sales", "scm_product_batch.created_at", "scm_product_batch.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductBatch.
   *
   * @param main
   * @return List of ProductBatch
   */
  public static final List<ProductBatch> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductBatchSqlPaged(main));
  }

//  /**
//   * Return list of ProductBatch based on condition
//   * @param main
//   * @return List<ProductBatch>
//   */
//  public static final List<ProductBatch> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductBatchSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductBatch by key.
   *
   * @param main
   * @param productBatch
   * @return ProductBatch
   */
  public static final ProductBatch selectByPk(Main main, ProductBatch productBatch) {
    return (ProductBatch) AppService.find(main, ProductBatch.class, productBatch.getId());
  }

  /**
   * Insert ProductBatch.
   *
   * @param main
   * @param productBatch
   */
  public static final void insert(Main main, ProductBatch productBatch) {
    ProductBatchIs.insertAble(main, productBatch);  //Validating
    AppService.insert(main, productBatch);

  }

  /**
   * Update ProductBatch by key.
   *
   * @param main
   * @param productBatch
   * @return ProductBatch
   */
  public static final ProductBatch updateByPk(Main main, ProductBatch productBatch) {
    ProductBatchIs.updateAble(main, productBatch); //Validating
    if (!isProductBatchSalesDone(main, productBatch)) {
      main.clear();
      main.param(productBatch.getBatchNo());
      main.param(productBatch.getExpiryDateActual());
      main.param(productBatch.getId());
      AppService.updateSql(main, ProductEntry.class, "UPDATE scm_product_entry_detail SET batch_no=?,\n"
              + "expiry_date=? WHERE product_detail_id IN(SELECT id FROM scm_product_detail  WHERE product_batch_id=?)", false);
    }
    return (ProductBatch) AppService.update(main, productBatch);
  }

  /**
   * Insert or update ProductBatch
   *
   * @param main
   * @param productBatch
   */
  public static void insertOrUpdate(Main main, ProductBatch productBatch) {
    if (productBatch.getId() == null) {
      insert(main, productBatch);
    } else {
      updateByPk(main, productBatch);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productBatch
   */
  public static void clone(Main main, ProductBatch productBatch) {
    productBatch.setId(null); //Set to null for insert
    insert(main, productBatch);
  }

  /**
   * Delete ProductBatch.
   *
   * @param main
   * @param productBatch
   */
  public static final void deleteByPk(Main main, ProductBatch productBatch) {
    ProductBatchIs.deleteAble(main, productBatch); //Validation
    AppService.deleteSql(main, ProductDetail.class, "delete from scm_product_detail where product_batch_id = ?", new Object[]{productBatch.getId()});
    AppService.delete(main, ProductBatch.class, productBatch.getId());
  }

  /**
   * Delete Array of ProductBatch.
   *
   * @param main
   * @param productBatch
   */
  public static final void deleteByPkArray(Main main, ProductBatch[] productBatch) {
    for (ProductBatch e : productBatch) {
      deleteByPk(main, e);
    }
  }

  public static final List<ProductBatch> selectProductBatchByProduct(Main main, Product product, String filter) {
    if (StringUtil.isEmpty(filter)) {
      return AppService.list(main, ProductBatch.class, "select * from scm_product_batch where product_id = ? ", new Object[]{product.getId()});
    } else {
      filter = "%" + filter.toUpperCase() + "%";
      return AppService.list(main, ProductBatch.class, "select * from scm_product_batch where product_id = ? and upper(batch_no) like ? ", new Object[]{product.getId(), filter});
    }
  }

  /**
   * Method to get the list of product batch.
   *
   * @param main
   * @param product
   * @param account
   * @return
   */
  public static List<ProductBatch> selectProductBatchByProduct(Main main, Product product, Account account) {
    List<ProductBatch> productBatchStockDetails = null;
    List<ProductBatch> productBatchList = AppService.list(main, ProductBatch.class, "select * from scm_product_batch where product_id = ? ", new Object[]{product.getId()});

    Integer productQty = null;
    Integer productFreeQty = null;
    Integer isPurchased = 0;

    if (account != null) {
      AccountGroup accountGroup = (AccountGroup) AppService.single(main, AccountGroup.class, "select * from scm_account_group where is_default = ? and id in (select account_group_id from scm_account_group_detail where account_id = ?)", new Object[]{SystemConstants.DEFAULT, account.getId()});
      AccountGroupPriceList accountGroupPriceList = (AccountGroupPriceList) AppService.single(main, AccountGroupPriceList.class, "select * from scm_account_group_price_list where is_default = ? and account_group_id = ?", new Object[]{SystemConstants.DEFAULT, accountGroup.getId()});

      if (accountGroup != null && accountGroupPriceList != null) {
        productBatchStockDetails = AppDb.getList(main.dbConnector(), ProductBatch.class,
                "SELECT product_batch_id as id, sum(quantity_available) quantity_available, sum(quantity_free_available) quantity_free_available "
                + "FROM getProductsDetailsForGstSale(?,?,?,?,?) where account_id = ? "
                + "GROUP by product_batch_id",
                new Object[]{accountGroup.getId(), product.getId(), accountGroupPriceList.getId(), null, SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()), account.getId()});

        for (ProductBatch productBatch : productBatchList) {
          isPurchased = 0;
          productQty = null;
          productFreeQty = null;
          if (!StringUtil.isEmpty(productBatchStockDetails)) {
            for (ProductBatch pb : productBatchStockDetails) {
              if (productBatch.getId().equals(pb.getId())) {
                productQty = pb.getQuantityAvailable();
                productFreeQty = pb.getQuantityFreeAvailable();
                isPurchased = 1;
                break;
              }
            }
          } else {
            if (AppService.exist(main, "select '1' from scm_product_entry_detail,scm_product_entry where product_detail_id in(select id "
                    + "from scm_product_detail where product_batch_id = ?) \n"
                    + "and scm_product_entry.id=scm_product_entry_detail.product_entry_id and scm_product_entry.product_entry_status_id =? ",
                    new Object[]{productBatch.getId(), SystemConstants.CONFIRMED})) {
              isPurchased = 1;
            }
          }
          productBatch.setQuantityAvailable(productQty);
          productBatch.setQuantityFreeAvailable(productFreeQty);
          productBatch.setIsPurchased(isPurchased);
        }
      }

    }

    return productBatchList;
  }

  /**
   *
   * @param main
   * @param productBatch
   * @return
   */
  public static boolean isProductBatchTraded(Main main, ProductBatch productBatch) {
    return AppService.exist(main, "select '1' from scm_product_detail where product_batch_id = ?", new Object[]{productBatch.getId()});
  }

  public static boolean isProductConfirmed(Main main, ProductBatch productBatch) {
    String sql = "select '1' from scm_product_entry where id in \n"
            + " ( select t2.product_entry_id from scm_product_entry_detail t2,scm_product_detail t3 where t2.product_detail_id = t3.id and t3.product_batch_id = ? )\n"
            + " and product_entry_status_id = ?";
    boolean t = AppService.exist(main, sql, new Object[]{productBatch.getId(), SystemConstants.CONFIRMED});
    return t;
  }

  public static boolean isProductBatchSalesDone(Main main, ProductBatch productBatch) {
    return AppService.exist(main, "select '1' from scm_sales_invoice_item where product_detail_id in"
            + "(select id from scm_product_detail where product_batch_id = ?)", new Object[]{productBatch.getId()});
  }
}
