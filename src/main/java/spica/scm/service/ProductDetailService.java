/*
 * @(#)ProductDetailService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductPreset;
import spica.scm.validate.ProductDetailIs;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 * ProductDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class ProductDetailService {

  public static final Integer PRODUCT_DETAILS_STATUS_ACTIVE = 1;

  /**
   * ProductDetail paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductDetailSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_detail", ProductDetail.class, main);
    sql.main("select scm_product_detail.id,scm_product_detail.account_id,scm_product_detail.created_by,scm_product_detail.modified_by,scm_product_detail.created_at,"
            + "scm_product_detail.modified_at from scm_product_detail scm_product_detail "); //Main query
    sql.count("select count(scm_product_detail.id) as total from scm_product_detail scm_product_detail "); //Count query
    sql.join("left outer join scm_account scm_product_detailaccount_id on (scm_product_detailaccount_id.id = scm_product_detail.account_id) "); //Join Query

    sql.string(new String[]{"scm_product_detail.created_by", "scm_product_detail.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_detail.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_detail.created_at", "scm_product_detail.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductDetail.
   *
   * @param main
   * @return List of ProductDetail
   */
  public static final List<ProductDetail> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductDetailSqlPaged(main));
  }

//  /**
//   * Return list of ProductDetail based on condition
//   * @param main
//   * @return List<ProductDetail>
//   */
//  public static final List<ProductDetail> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductDetailSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductDetail by key.
   *
   * @param main
   * @param productDetail
   * @return ProductDetail
   */
  public static final ProductDetail selectByPk(Main main, ProductDetail productDetail) {
    return (ProductDetail) AppService.find(main, ProductDetail.class, productDetail.getId());
  }

  /**
   * Insert ProductDetail.
   *
   * @param main
   * @param productDetail
   */
  public static final void insert(Main main, ProductDetail productDetail) {
    ProductDetailIs.insertAble(main, productDetail);  //Validating
    AppService.insert(main, productDetail);

  }

  /**
   * Update ProductDetail by key.
   *
   * @param main
   * @param productDetail
   * @return ProductDetail
   */
  public static final ProductDetail updateByPk(Main main, ProductDetail productDetail) {
    ProductDetailIs.updateAble(main, productDetail); //Validating
    return (ProductDetail) AppService.update(main, productDetail);
  }

  /**
   * Insert or update ProductDetail
   *
   * @param main
   * @param productDetail
   */
  public static void insertOrUpdate(Main main, ProductDetail productDetail) {
    if (productDetail.getId() == null) {
      insert(main, productDetail);
    } else {
      updateByPk(main, productDetail);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productDetail
   */
  public static void clone(Main main, ProductDetail productDetail) {
    productDetail.setId(null); //Set to null for insert
    insert(main, productDetail);
  }

  /**
   * Delete ProductDetail.
   *
   * @param main
   * @param productDetail
   */
  public static final void deleteByPk(Main main, ProductDetail productDetail) {
    ProductDetailIs.deleteAble(main, productDetail); //Validation
    AppService.delete(main, ProductDetail.class, productDetail.getId());
  }

  /**
   * Delete Array of ProductDetail.
   *
   * @param main
   * @param productDetail
   */
  public static final void deleteByPkArray(Main main, ProductDetail[] productDetail) {
    for (ProductDetail e : productDetail) {
      deleteByPk(main, e);
    }
  }

  public static ProductDetail getProductDetailObj(MainView main, Integer prodDetailId) {
    ProductDetail pd = (ProductDetail) AppService.single(main, ProductDetail.class, "select * from scm_product_detail where id=?", new Object[]{prodDetailId});
    return pd;
  }

  public static ProductDetail insertOrUpdate(Main main, Account account, ProductBatch productBatchId, ProductDetail productDetail) {
    ProductPreset productPreset = (ProductPreset) AppService.single(main, ProductPreset.class, "select * from scm_product_preset where account_id = ? and product_id = ? ", new Object[]{account.getId(), productBatchId.getProductId().getId()});

    if (productDetail == null) {
      productDetail = (ProductDetail) AppService.single(main, ProductDetail.class, "select * from scm_product_detail where account_id=? and product_batch_id=? and product_preset_id=? order by id asc limit 1",
              new Object[]{account.getId(), productBatchId.getId(), productPreset.getId()});
      if (productDetail == null) {
        productDetail = new ProductDetail();
      }
    }
    productDetail.setAccountId(account);
    productDetail.setProductBatchId(productBatchId);
    productDetail.setProductPresetId(productPreset);
    insertOrUpdate(main, productDetail);
    return productDetail;
  }

  public static ProductDetail getProductDetailByBatchForSalesReturn(MainView main, Account accountId, ProductBatch batchId, ProductPreset presetId) {
    ProductDetail pd = null;
    if (accountId != null && batchId != null && presetId != null) {
      pd = (ProductDetail) AppService.single(main, ProductDetail.class, "select * from scm_product_detail where account_id=? and "
              + "product_batch_id=? and product_preset_id=? limit 1",
              new Object[]{accountId.getId(), batchId.getId(), presetId.getId()});
    }
    return pd;
  }
}
