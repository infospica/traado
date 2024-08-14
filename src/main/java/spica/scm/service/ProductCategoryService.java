/*
 * @(#)ProductCategoryService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductPreset;
import spica.scm.domain.ProductPricelist;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.scm.validate.ProductCategoryIs;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;

/**
 * ProductCategoryService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class ProductCategoryService {

  /**
   * ProductCategory paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductCategorySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_category", ProductCategory.class, main);
    sql.main("select scm_product_category.id,scm_product_category.commodity_id,scm_product_category.title,scm_product_category.sort_order,scm_product_category.status_id,"
            + "scm_product_category.hsn_code,scm_product_category.purchase_tax_code_id,scm_product_category.sales_tax_code_id,"
            + "scm_product_category.purchase_cess_tax_code_id,scm_product_category.sales_cess_tax_code_id,"
            + "scm_product_category.created_at,scm_product_category.modified_at,scm_product_category.created_by,scm_product_category.modified_by "
            + "from scm_product_category scm_product_category "); //Main query
    sql.count("select count(scm_product_category.id) as total from scm_product_category scm_product_category "); //Count query
    sql.join("left outer join scm_service_commodity scm_product_categorycommodity_id on (scm_product_categorycommodity_id.id = scm_product_category.commodity_id) "
            + "left outer join scm_status scm_product_categorystatus_id on (scm_product_categorystatus_id.id = scm_product_category.status_id) "
            + "left outer join scm_tax_code scm_product_categorypurchase_tax_code_id on (scm_product_categorypurchase_tax_code_id.id = scm_product_category.purchase_tax_code_id) "
            + "left outer join scm_tax_code scm_product_categorysales_tax_code_id on (scm_product_categorysales_tax_code_id.id = scm_product_category.sales_tax_code_id) "
            + "left outer join scm_tax_code scm_product_categorypurchase_cess_tax_code_id on (scm_product_categorypurchase_cess_tax_code_id.id = scm_product_category.purchase_cess_tax_code_id) "
            + "left outer join scm_tax_code scm_product_categorysales_cess_tax_code_id on (scm_product_categorysales_cess_tax_code_id.id = scm_product_category.sales_cess_tax_code_id) "); //Join Query

    sql.string(new String[]{"scm_product_categorycommodity_id.title", "scm_product_category.title", "scm_product_categorystatus_id.title", "scm_product_category.hsn_code",
      "scm_product_category.created_by", "scm_product_category.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_category.id", "scm_product_category.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_category.created_at", "scm_product_category.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductCategory.
   *
   * @param main
   * @return List of ProductCategory
   */
  public static final List<ProductCategory> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductCategorySqlPaged(main));
  }

  public static final List<ProductCategory> listPagedByCountryAndCompany(Main main, Company company, ServiceCommodity commodity) {
    SqlPage sql = getProductCategorySqlPaged(main);
    if (commodity != null && commodity.getId() != null) {
      sql.cond("where commodity_id = ? ");
      sql.param(commodity.getId());
    } else {
      sql.cond("where commodity_id in (select id from scm_service_commodity where company_id = ? or (country_id = ? and company_id is null))");
      sql.param(company.getId());
      sql.param(company.getCountryId().getId());
    }

    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of ProductCategory based on condition
//   * @param main
//   * @return List<ProductCategory>
//   */
//  public static final List<ProductCategory> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductCategorySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductCategory by key.
   *
   * @param main
   * @param productCategory
   * @return ProductCategory
   */
  public static final ProductCategory selectByPk(Main main, ProductCategory productCategory) {
    return (ProductCategory) AppService.find(main, ProductCategory.class, productCategory.getId());
  }

  /**
   * Insert ProductCategory.
   *
   * @param main
   * @param productCategory
   */
  public static final void insert(Main main, ProductCategory productCategory) {
    ProductCategoryIs.insertAble(main, productCategory);  //Validating
    AppService.insert(main, productCategory);
    ProductCategoryLogService.insetProductCategoryLog(main, productCategory);
  }

  /**
   * Update ProductCategory by key.
   *
   * @param main
   * @param productCategory
   * @return ProductCategory
   */
  public static final ProductCategory updateByPk(Main main, ProductCategory productCategory) {
    ProductCategoryIs.updateAble(main, productCategory); //Validating
    ProductCategoryLogService.insetProductCategoryLog(main, productCategory);
    return (ProductCategory) AppService.update(main, productCategory);
  }

  /**
   * Insert or update ProductCategory
   *
   * @param main
   * @param productCategory
   */
  public static void insertOrUpdate(Main main, ProductCategory productCategory) {
    if (productCategory.getId() == null) {
      insert(main, productCategory);
    } else {
      updateByPk(main, productCategory);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productCategory
   */
  public static void clone(Main main, ProductCategory productCategory) {
    productCategory.setId(null); //Set to null for insert
    insert(main, productCategory);
  }

  /**
   * Delete ProductCategory.
   *
   * @param main
   * @param productCategory
   */
  public static final void deleteByPk(Main main, ProductCategory productCategory) {
    ProductCategoryIs.deleteAble(main, productCategory); //Validation    
    AppService.delete(main, ProductCategory.class, productCategory.getId());
  }

  /**
   * Delete Array of ProductCategory.
   *
   * @param main
   * @param productCategory
   */
  public static final void deleteByPkArray(Main main, ProductCategory[] productCategory) {
    for (ProductCategory e : productCategory) {
      deleteByPk(main, e);
    }
  }

  public static ProductCategory categoryExciseDuty(Main main, int id) {
    return (ProductCategory) AppService.single(main, ProductCategory.class, "select * from scm_product_category where id = ?", new Object[]{id});
  }

  public static List<ProductCategory> productCategoryAuto(Main main, Integer id) {
    return AppService.list(main, ProductCategory.class, "select id,title from scm_product_category where commodity_id = ? and status_id=?", new Object[]{id, 1});

  }

  public static void updateProductPriceList(Main main, ProductCategory productCategory) {
    ProductPreset productPreset = null;
    double mrpLte, valuePts, valuePtr, oldPts;
    long availableQty;
    AccountGroup accountGroup;
    AccountGroupPriceList accountGroupPriceList;
    Product product;
    List<ProductPricelist> defaultProductPriceList = AppService.list(main, ProductPricelist.class, "select t1.* from scm_product_pricelist t1 "
            + "inner join scm_account_group_price_list t2 on t2.id = t1.account_group_price_list_id and t2.is_default = ? "
            + "inner join scm_product_detail t3 on t1.product_detail_id = t3.id "
            + "inner join scm_account t6 on t6.id = t3.account_id and t6.account_status_id = ? and t6.pts_derivation_criteria = ? "
            + "inner join scm_product_batch t5 on t5.id = t3.product_batch_id  "
            + "inner join scm_product t4 on t5.product_id = t4.id and t4.product_category_id = ? "
            + "order by t2.id", new Object[]{SystemConstants.DEFAULT, AccountService.ACTIVE, AccountService.DERIVE_PTS_FROM_MRP_PTR, productCategory.getId()});

    List<ProductPricelist> customProductPriceList = AppService.list(main, ProductPricelist.class, "select t1.* from scm_product_pricelist t1 "
            + "inner join scm_account_group_price_list t2 on t2.id = t1.account_group_price_list_id and t2.is_default <> ? "
            + "inner join scm_product_detail t3 on t1.product_detail_id = t3.id "
            + "inner join scm_account t6 on t6.id = t3.account_id and t6.account_status_id = ? and t6.pts_derivation_criteria = ? "
            + "inner join scm_product_batch t5 on t5.id = t3.product_batch_id  "
            + "inner join scm_product t4 on t5.product_id = t4.id and t4.product_category_id = ? "
            + "order by t2.id", new Object[]{SystemConstants.DEFAULT, AccountService.ACTIVE, AccountService.DERIVE_PTS_FROM_MRP_PTR, productCategory.getId()});

    for (ProductPricelist productPrice : defaultProductPriceList) {
      product = productPrice.getProductDetailId().getProductBatchId().getProductId();
      oldPts = productPrice.getValuePtsPerProdPieceSell();

      accountGroupPriceList = productPrice.getAccountGroupPriceListId();
      accountGroup = accountGroupPriceList.getAccountGroupId();
      availableQty = AppService.count(main, "select COALESCE(sum(quantity_available)::integer,0) from getProductsDetailsForGstSale(?,?,?,null,?) "
              + "where product_detail_id = ? and pricelist_pts = ? and pricelist_ptr = ?",
              new Object[]{accountGroup.getId(), product.getId(), accountGroupPriceList.getId(), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()), productPrice.getProductDetailId().getId(),
                productPrice.getValuePtsPerProdPieceSell(), productPrice.getValuePtrPerProdPieceSell()});

      productPreset = ProductPresetService.selectProductPresetByProductAndAccount(main, product.getId(), productPrice.getProductDetailId().getAccountId());
      mrpLte = ProductUtil.getMrpLteValue(productPrice.getProductDetailId().getProductBatchId().getValueMrp(), productCategory.getPurchaseTaxCodeId().getRatePercentage());

      valuePtr = ProductUtil.getPtrValue(productPreset.getPtrMarginPercentage(), mrpLte, productPreset.getMrpltePtrRateDerivationCriterion());
      valuePts = ProductUtil.getPtsValue(productPreset.getPtsMarginPercentage(), valuePtr, productPreset.getPtrPtsRateDerivationCriterion());

      productPrice.setValuePtrPerProdPieceSell(MathUtil.roundOff(valuePtr, 2));
      productPrice.setValuePtsPerProdPieceSell(MathUtil.roundOff(valuePts, 2));

      // Update the PTS variation in platform.
      //StockOfProduct stockOfProduct = (StockOfProduct) AppService.single(main, StockOfProduct.class, "select * from getstockofproduct(?,?) where product_detail_id = ?",
      //        new Object[]{productPreset.getAccountId().getId(), productPreset.getProductId().getId(), productPrice.getProductDetailId().getId()});
      //double ptsVariation = (valuePts - oldPts) * (stockOfProduct.getQuantitySaleable() == null ? 0 : stockOfProduct.getQuantitySaleable());
      double ptsVariation = (valuePts - oldPts) * (availableQty);
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
