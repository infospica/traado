/*
 * @(#)ProductService.java	1.0 Mon Aug 08 17:59:21 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductPreset;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.fin.domain.TaxCode;
import spica.scm.common.ProductInvoiceDetail;
import spica.scm.common.ProductProfile;
import spica.scm.common.ProductRate;
import spica.scm.common.ProductStockSummary;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Manufacture;
import spica.scm.domain.ManufactureProduct;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductClassification;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductPricelist;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItem;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.scm.validate.ValidateUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * ProductService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class ProductService {

  public static final boolean FILTER_PRODUCTS_BY_BRAND_AND_VENDOR_COMMODITY = true;

  /**
   * Product paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product", Product.class, main);
    sql.main("select scm_product.id,scm_product.hsn_code,scm_product.commodity_id,scm_product.product_category_id,scm_product.company_id,scm_product.product_name,scm_product.product_name_chemical,scm_product.product_name_generic,scm_product.product_type_id,scm_product.product_classification_id,scm_product.pack_size,scm_product.product_unit_id,scm_product.product_status_id,scm_product.created_at,scm_product.modified_at,scm_product.created_by,scm_product.modified_by,scm_product.brand_id,scm_product.product_packing_detail_id from scm_product scm_product "); //Main query
    sql.count("select count(scm_product.id) as total from scm_product scm_product "); //Count query
    sql.join("left outer join scm_product_category scm_productproduct_category_id on (scm_productproduct_category_id.id = scm_product.product_category_id) "
            + "left outer join scm_company scm_productcompany_id on (scm_productcompany_id.id = scm_product.company_id) "
            + "left outer join scm_brand scm_brand on (scm_brand.id = scm_product.brand_id) "
            + "left outer join scm_product_type scm_productproduct_type_id on (scm_productproduct_type_id.id = scm_product.product_type_id) "
            + "left outer join scm_product_classification scm_productproduct_classification_id on (scm_productproduct_classification_id.id = scm_product.product_classification_id) "
            + "left outer join scm_product_status scm_productproduct_status_id on (scm_productproduct_status_id.id = scm_product.product_status_id) "
            + "left outer join scm_service_commodity scm_productcommodity_id on (scm_productcommodity_id.id = scm_product.commodity_id) "
            + "left outer join scm_product_unit scm_productproduct_unit_id on (scm_productproduct_unit_id.id = scm_product.product_unit_id)"
            + "left outer join scm_product_packing_detail scm_product_packing_detail_id on(scm_product_packing_detail_id.id=scm_product.product_packing_detail_id)"); //Join Query
    //"scm_productcommodity_id.sales_tax_code_id.code","scm_productcommodity_id.purchase_tax_code_id.code",
    sql.string(new String[]{"scm_brand.name", "scm_productproduct_category_id.title", "scm_productcommodity_id.title", "scm_productcompany_id.company_name", "scm_product.product_name", "scm_productproduct_type_id.title", "scm_productproduct_classification_id.title", "scm_productproduct_unit_id.title", "scm_productproduct_status_id.title", "scm_product.created_by", "scm_product.modified_by", "scm_product_packing_detail_id.title"}); //String search or sort fields
    sql.number(new String[]{"scm_product.id", "scm_product.pack_size"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product.created_at", "scm_product.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Product.
   *
   * @param main
   * @param company
   * @param account
   * @param productFilter
   * @return List of Product
   */
  public static final List<Product> listPaged(Main main, Company company, Account account, boolean productFilter) {
    SqlPage sql = getProductSqlPaged(main);
    main.clear();
    if (productFilter && account != null) {
      sql.cond("where scm_product.company_id = ? and scm_product.commodity_id in (select commodity_id from scm_vendor_commodity where vendor_id = ?) "
              + "and brand_id IN(SELECT brand_id FROM  scm_supplier_brand WHERE vendor_id = ?) ");
      sql.param(company.getId());
      sql.param(account.getVendorId().getId());
      sql.param(account.getVendorId().getId());
    } else if (account != null && !productFilter) {
      sql.join("left outer join scm_product_preset scm_product_preset on (scm_product.id = scm_product_preset.product_id) ");
      sql.cond("where scm_product.company_id = ? and scm_product_preset.account_id = ? "
              + "and scm_product.commodity_id in (select commodity_id from scm_vendor_commodity where vendor_id = ?) "
              + "and brand_id IN(SELECT brand_id FROM  scm_supplier_brand WHERE vendor_id = ?) ");
      sql.param(company.getId());
      sql.param(account.getId());
      sql.param(account.getVendorId().getId());
      sql.param(account.getVendorId().getId());
    } else {
      sql.cond("where scm_product.company_id = ?");
      sql.param(company.getId());
    }
    return AppService.listPagedJpa(main, sql);
  }

  public static List<Product> selectProductByCompany(Main main, Company company) {
    return AppService.list(main, Product.class, "select * from scm_product where company_id = ?", new Object[]{company.getId()});
  }

  /**
   * Select Product by key.
   *
   * @param main
   * @param product
   * @return Product
   */
  public static final Product selectByPk(Main main, Product product) {
    return (Product) AppService.find(main, Product.class, product.getId());
  }

  /**
   * Insert Product.
   *
   * @param main
   * @param product
   */
  public static final void insert(Main main, Product product) {
    insertAble(main, product);  //Validating
    AppService.insert(main, product);

  }

  /**
   * Update Product by key.
   *
   * @param main
   * @param product
   * @return Product
   */
  public static final Product updateByPk(Main main, Product product) {
    updateAble(main, product); //Validating
    return (Product) AppService.update(main, product);
  }

  /**
   * Insert or update Product
   *
   * @param main
   * @param product
   */
  public static void insertOrUpdate(Main main, Product product) {
    if (product.getId() == null) {
      insert(main, product);
      ProductLogService.insertProductLog(main, product);
    } else {
      updateByPk(main, product);
      ProductLogService.insertProductLog(main, product);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param product
   */
  public static void clone(Main main, Product product) {
    product.setId(null); //Set to null for insert
    insert(main, product);
  }

  /**
   * Delete Product.
   *
   * @param main
   * @param product
   */
  public static final void deleteByPk(Main main, Product product) {
    AppService.deleteSql(main, ProductPreset.class, "delete from scm_product_preset where scm_product_preset.product_id = ?", new Object[]{product.getId()});
    AppService.deleteSql(main, ProductDetail.class, "delete from scm_product_batch where product_id = ?", new Object[]{product.getId()});
    AppService.deleteSql(main, ManufactureProduct.class, "delete from scm_manufacture_product where product_id = ?", new Object[]{product.getId()});
    AppService.delete(main, Product.class, product.getId());
  }

  /**
   * Delete Array of Product.
   *
   * @param main
   * @param product
   */
  public static final void deleteByPkArray(Main main, Product[] product) {
    for (Product e : product) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param id
   * @return
   */
  public static List<Product> productByAccount(Main main, int id) {
    List<Product> pList = main.em().list(Product.class, "select scm_product.* from scm_product scm_product left outer join scm_product_preset scm_product_preset "
            + "on(scm_product.id=scm_product_preset.product_id) where scm_product_preset.account_id = ? ", new Object[]{id});
    return pList;
  }

  /**
   *
   * @param main
   * @param product
   * @return
   */
  public static List<Manufacture> selectManufactureByProduct(Main main, Product product) {
    return AppService.list(main, Manufacture.class, "select id, code from scm_manufacture where id in (select manufacture_id from scm_manufacture_product where product_id = ?)", new Object[]{product.getId()});
  }

  /**
   * Selecting product for sales invoice entry.
   *
   * @param main
   * @param accountGroupId
   * @param taxCodeId
   * @param productType
   * @param filter
   * @return
   */
//  public static List<Product> selectProductForSales(Main main, AccountGroup accountGroupId, TaxCode taxCodeId, String productType, String filter) {
//
//    /**
//     * SELECT * FROM getProductsOfAccountGroup(?,?,?)
//     *
//     * Params : 1. Account Group id 2. Commodity Id 3. Product Type Id
//     *
//     */
//    List<Product> productList = null;
//    String sql = "select * from scm_product scm_product where id in (select prodid from getproductsofaccountgroupforsales(?,?,?)) ";
//    if (!StringUtil.isEmpty(filter)) {
//      sql += " and upper(scm_product.product_name) like ? order by upper(scm_product.product_name) asc";
//      productList = AppDb.getList(main.dbConnector(), Product.class, sql, new Object[]{accountGroupId == null ? null : accountGroupId.getId(), taxCodeId == null ? null : taxCodeId.getId(), productType, "%" + filter.toUpperCase() + "%"});
//    } else {
//      sql += " order by upper(scm_product.product_name) asc";
//      productList = AppDb.getList(main.dbConnector(), Product.class, sql, new Object[]{accountGroupId == null ? null : accountGroupId.getId(), taxCodeId == null ? null : taxCodeId.getId(), productType});
//    }
//    return productList;
//  }
  public static final List<ProductDetailSales> selectProductDetailForSales(Main main, AccountGroup accountGroupId, Product product, AccountGroupPriceList accountGroupPriceListId, SalesInvoice salesInvoiceId, SalesInvoiceItem salesInvoiceItem) {
    /**
     * SQL Function - getProductsDetailsForSale(?,?,?,?) params. 1. Account Group Id. 2.Product Id 3.Account Group price list id. 4.Sales invoice item id.
     *
     */
    List<ProductDetailSales> productDetailSales = AppDb.getList(main.dbConnector(), ProductDetailSales.class,
            "SELECT product_detail_id, batch_no, expiry_date_actual, to_char(expiry_date_actual,'Mon-yy') expiry_date, mrp_value, "
            + "pricelist_pts, free_qty_scheme_id,invoice_discount,line_discount, (invoice_discount + line_discount) as discount,"
            + "sum(quantity_available) quantity_available,  sum(quantity_free_available) quantity_free_available,pack_size, "
            + "primary_pack_id,secondary_pack_id,tertiary_pack_id,ter_to_sec_quantity,sec_to_pri_quantity, free_scheme_name, "
            + "concat_ws('#',product_detail_id,pricelist_pts,coalesce(free_qty_scheme_id,0),invoice_discount,line_discount,coalesce(ter_to_sec_quantity,0),coalesce(sec_to_pri_quantity,0)) as product_hash "
            + "FROM getProductsDetailsForSale(?,?,?,?) "
            + "GROUP by product_detail_id,batch_no, expiry_date_actual,mrp_value,pricelist_pts,free_qty_scheme_id,invoice_discount,line_discount,pack_size,"
            + "primary_pack_id,secondary_pack_id, tertiary_pack_id, ter_to_sec_quantity, sec_to_pri_quantity, free_scheme_name",
            new Object[]{accountGroupId == null ? null : accountGroupId.getId(),
              product == null ? null : product.getId(),
              accountGroupPriceListId == null ? null : accountGroupPriceListId.getId(),
              (salesInvoiceId.getSalesInvoiceType().equals(SystemConstants.SALES_INVOICE_TYPE_PROFORMA) ? salesInvoiceItem.getSalesInvoiceItemHashCode() : null)});
    return productDetailSales;
  }

  public static boolean isProductDetailExist(Main main, Product product) {
    return AppService.exist(main, "select 1 from scm_product_detail t1 inner join scm_product_batch t2 on t1.product_batch_id = t2.id and t2.product_id = ?", new Object[]{product.getId()});
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @param agp
   * @param productList
   * @return
   */
  public static List<Product> selectProductQuantityDetails(Main main, AccountGroup accountGroup, AccountGroupPriceList agp, List<Product> productList) {
    List<Product> list = new ArrayList<>();
    ProductDetailSales productDetailSales = null;
    for (Product product : productList) {
      productDetailSales = getProductDetailSales(main, product, accountGroup, agp);
      product.setProductQuantity(productDetailSales.getQuantityAvailable());
      product.setProductFreeQuantity(productDetailSales.getQuantityFreeAvailable());
      list.add(product);
    }
    return list;
  }

  /**
   *
   * @param main
   * @param product
   * @param accountGroup
   * @param accountGroupPriceList
   * @return
   */
  public static ProductDetailSales getProductDetailSales(Main main, Product product, AccountGroup accountGroup, AccountGroupPriceList accountGroupPriceList) {
    ProductDetailSales productDetailSales = null;
    if (product != null && accountGroup != null && accountGroupPriceList != null) {
      productDetailSales = (ProductDetailSales) AppDb.single(main.dbConnector(), ProductDetailSales.class,
              "SELECT sum(quantity_available) quantity_available,  sum(quantity_free_available) quantity_free_available FROM getProductsDetailsForGstSale(?,?,?,?,?)",
              new Object[]{accountGroup.getId(), product.getId(), accountGroupPriceList.getId(), null, SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date())});
    }
    return productDetailSales;
  }

  /**
   * Method to get the list of product from stock in its purchasing order.
   *
   * @param main
   * @param accountGroupId
   * @param productId
   * @param accountGroupPriceListId
   * @return
   */
  public static final List<ProductDetailSales> selectProductDetailByPurchaseOrder(Main main, AccountGroup accountGroupId, Product productId, AccountGroupPriceList accountGroupPriceListId) {
    List<ProductDetailSales> productDetailSales = AppDb.getList(main.dbConnector(), ProductDetailSales.class,
            "select * from getProductsDetailsForSale(?,?,?,null) order by expiry_date_actual ASC, product_entry_detail_id ASC", new Object[]{accountGroupId == null ? null : accountGroupId.getId(),
              productId == null ? null : productId.getId(),
              accountGroupPriceListId == null ? null : accountGroupPriceListId.getId()});
    return productDetailSales;
  }

  public static final void updateDefaultProductStatus(Main main, Product defaultProduct) {
    main.clear();
    main.param(defaultProduct.getId());
    AppService.updateSql(main, AccountGroup.class, "update scm_product set product_status_id = 1 where id = ?", false);
    main.clear();
  }

  /**
   *
   * @param main
   * @param product
   * @return
   */
  public static final TaxCode getProductPurchaseTaxCode(Main main, Product product) {
    TaxCode taxCode = null;
    if (product.getProductCategoryId() != null) {
      taxCode = product.getProductCategoryId().getPurchaseTaxCodeId();
    } else if (product.getCommodityId() != null) {
      taxCode = product.getCommodityId().getPurchaseTaxCodeId();
    }
    return taxCode;
  }

  /**
   *
   * @param main
   * @param product
   * @return
   */
  public static final TaxCode getProductSalesTaxCode(Main main, Product product) {
    TaxCode taxCode = null;
    if (product.getProductCategoryId() != null) {
      taxCode = product.getProductCategoryId().getSalesTaxCodeId();
    } else if (product.getCommodityId() != null) {
      taxCode = product.getCommodityId().getSalesTaxCodeId();
    }
    return taxCode;
  }

  public static final ProductBatch selectSingleProductBatchByProduct(Main main, Account account, Product productId) {
    ProductBatch pd = null;
    if (productId != null) {
      pd = (ProductBatch) AppService.single(main, ProductBatch.class, "select t1.* from scm_product_batch t1 "
              + "inner join scm_product t2 on t1.product_id = t2.id and t2.id = ? "
              + "inner join scm_product_preset t3 on t3.product_id = t2.id and t3.account_id = ? "
              + "order by t1.id asc limit 1", new Object[]{productId.getId(), account.getId()});
    }
    return pd;
  }

  public static long isProductExist(Main main, Integer productId, List<Manufacture> manufactureList) {
    StringBuilder sb = new StringBuilder();
    String whereCondition = "";
    for (Manufacture manf : manufactureList) {
      if (sb.length() == 0) {
        sb.append(manf.getId());
      } else {
        sb.append(",").append(manf.getId());
      }
    }
    if (productId != null) {
      whereCondition = " product_id <> " + productId + " and ";
    }
    long count = main.em().count("select count(manufacture_id) manufacture_count from scm_manufacture_product"
            + "where " + whereCondition + " product_id in ( "
            + "select product_id from scm_manufacture_product where manufacture_id in (" + sb.toString() + ") "
            + "GROUP by product_id "
            + "having count(product_id) = " + manufactureList.size()
            + ") GROUP by product_id having count(manufacture_id) = " + manufactureList.size(), null);

    return count;
  }

  public static int updateProductPriceList(Main main, Product product, ProductCategory prevProductCategory) {

    List<ProductPricelist> defaultProductPriceList = null;
    List<ProductPricelist> customProductPriceList = null;
    //StockOfProduct stockOfProduct = null;
    double mrpLte, valuePtr, valuePts, preValuePts, ptsVariation = 0;
    ProductPreset produtPreset = null;
    int ptsUpdated = 0;
    long availableQty;
    AccountGroup accountGroup;
    AccountGroupPriceList accountGroupPriceList;

    List<ProductRate> productRateList = AppDb.getList(main.dbConnector(), ProductRate.class,
            "select t2.account_id,t1.product_detail_id,t1.value_mrp,t1.value_pts,t1.value_ptr from scm_product_entry_detail t1 "
            + "inner join scm_product_entry t2 on t1.product_entry_id = t2.id "
            + "where t1.product_detail_id in ( "
            + "select id from scm_product_detail where product_batch_id in( "
            + "select id from scm_product_batch where product_id = ?)) "
            + "group by t2.account_id,t1.product_detail_id,t1.value_mrp,t1.value_pts,t1.value_ptr", new Object[]{product.getId()});
    if (!StringUtil.isEmpty(productRateList)) {
      for (ProductRate productRate : productRateList) {

        //preValuePts = productRate.getValuePts();
        //   produtPreset = ProductPresetService.selectProductPresetByProductAndAccount(main, product.getId(), new Account(productRate.getAccountId()));
        //mrpLte = ProductUtil.getMrpLteValue(productRate.getValueMrp(), product.getProductCategoryId().getPurchaseTaxCodeId().getRatePercentage());
        //valuePtr = ProductUtil.getPtrValue(produtPreset.getPtrMarginPercentage(), mrpLte, produtPreset.getMrpltePtrRateDerivationCriterion());
        //valuePts = ProductUtil.getPtsValue(produtPreset.getPtsMarginPercentage(), valuePtr, produtPreset.getPtrPtsRateDerivationCriterion());
        //Double ptr = (productRate.getValuePtr() != null ? productRate.getValuePtr() : 0.0);
        //Double pts = (productRate.getValuePts() != null ? productRate.getValuePts() : 0.0);
        defaultProductPriceList = AppService.list(main, ProductPricelist.class, "select t1.* from scm_product_pricelist t1 "
                + "inner join scm_account_group_price_list t2 on t1.account_group_price_list_id = t2.id and t2.is_default = ? "
                + "inner join scm_product_detail t3 on t1.product_detail_id = t3.id "
                + "inner join scm_account t4 on t4.id = t3.account_id and t4.account_status_id = ? and t4.pts_derivation_criteria = ? "
                + "where t1.product_detail_id = ? ",
                new Object[]{SystemConstants.DEFAULT, AccountService.ACTIVE, AccountService.DERIVE_PTS_FROM_MRP_PTR, productRate.getProductDetailId()});

        customProductPriceList = AppService.list(main, ProductPricelist.class, "select t1.* from scm_product_pricelist t1 "
                + "inner join scm_account_group_price_list t2 on t1.account_group_price_list_id = t2.id and t2.is_default <> ? "
                + "inner join scm_product_detail t3 on t1.product_detail_id = t3.id "
                + "inner join scm_account t4 on t4.id = t3.account_id and t4.account_status_id = ? and t4.pts_derivation_criteria = ? "
                + "where t1.product_detail_id = ? ",
                new Object[]{SystemConstants.DEFAULT, AccountService.ACTIVE, AccountService.DERIVE_PTS_FROM_MRP_PTR, productRate.getProductDetailId()});

        for (ProductPricelist productPrice : defaultProductPriceList) {
          preValuePts = productPrice.getValuePtsPerProdPieceSell();
          produtPreset = productPrice.getProductDetailId().getProductPresetId();

          mrpLte = ProductUtil.getMrpLteValue(productPrice.getValueMrp(), product.getProductCategoryId().getPurchaseTaxCodeId().getRatePercentage());
          valuePtr = ProductUtil.getPtrValue(produtPreset.getPtrMarginPercentage(), mrpLte, produtPreset.getMrpltePtrRateDerivationCriterion());
          valuePts = ProductUtil.getPtsValue(produtPreset.getPtsMarginPercentage(), valuePtr, produtPreset.getPtrPtsRateDerivationCriterion());

          accountGroupPriceList = productPrice.getAccountGroupPriceListId();
          accountGroup = accountGroupPriceList.getAccountGroupId();
          availableQty = AppService.count(main, "select sum(quantity_available)::integer from getProductsDetailsForGstSale(?,?,?,null,?) "
                  + "where product_detail_id = ? and pricelist_pts = ? and pricelist_ptr = ? ",
                  new Object[]{accountGroup.getId(), product.getId(), accountGroupPriceList.getId(),
                    SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()),
                    productRate.getProductDetailId(), productPrice.getValuePtsPerProdPieceSell(), productPrice.getValuePtrPerProdPieceSell()});

          productPrice.setValuePtrPerProdPieceSell(MathUtil.roundOff(valuePtr, 2));
          productPrice.setValuePtsPerProdPieceSell(MathUtil.roundOff(valuePts, 2));
          productPrice.setValuePtr(MathUtil.roundOff(valuePtr, 2));
          productPrice.setValuePts(MathUtil.roundOff(valuePts, 2));
          productPrice.setActualSellingPriceDerived(getActualSellingPriceDerived(productPrice));
          ptsVariation = (valuePts - preValuePts) * (availableQty);
          if (ptsVariation != 0) {
            if (ptsVariation > 0) {
              PlatformService.insertSales(main, productPrice.getProductDetailId().getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, Math.abs(ptsVariation), null, null, null, PlatformService.NORMAL_FUND_STATE);
            } else {
              PlatformService.insertSales(main, productPrice.getProductDetailId().getAccountId(), SystemRuntimeConfig.RATE_DIFFERENCE, null, Math.abs(ptsVariation), null, null, PlatformService.NORMAL_FUND_STATE);
            }
          }

          ProductPricelistService.updateByPk(main, productPrice);
          ptsUpdated++;
        }

        for (ProductPricelist customProductPrice : customProductPriceList) {
          customProductPrice.setValuePtrPerProdPieceSell(null);
          customProductPrice.setValuePtsPerProdPieceSell(null);
          ProductPricelistService.updateByPk(main, customProductPrice);
        }
        ptsUpdated++;
      }
    }
    return ptsUpdated;
  }

  private static Double getActualSellingPriceDerived(ProductPricelist productPricelist) {
    Double actualSellingPrice = null;
    if (productPricelist != null && productPricelist.getValuePts() != null) {
      actualSellingPrice = productPricelist.getValuePts();

      if (productPricelist.getIsSchemeDiscountToCustomer() != null && SystemConstants.DISCOUNT_FOR_CUSTOMER == productPricelist.getIsSchemeDiscountToCustomer() && productPricelist.getSchemeDiscountValueDerived() != null) {
        actualSellingPrice -= productPricelist.getSchemeDiscountValueDerived();
      }

      if (productPricelist.getIsProductDiscountToCustomer() != null && SystemConstants.DISCOUNT_FOR_CUSTOMER == productPricelist.getIsProductDiscountToCustomer() && productPricelist.getProductDiscountValueDerived() != null) {
        actualSellingPrice -= productPricelist.getProductDiscountValueDerived();
      }
    }
    return actualSellingPrice;
  }

  public static void updateTaxCodeModifiedFlag(MainView main, Product product, Account account) {
    // updates the is_tax_code_modified flag of Product entry and Product entry detail
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(product.getId());
    AppService.updateSql(main, ProductEntry.class, "update scm_product_entry set is_tax_code_modified = ? "
            + "where product_entry_status_id = ? and id in(select product_entry_id from scm_product_entry_detail "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ?)))", false);

    // updates the is_tax_code_modified flag of Product entry detail
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(product.getId());
    main.param(product.getId());
    AppService.updateSql(main, ProductEntryDetail.class, "update scm_product_entry_detail set is_tax_code_modified = ? "
            + "where product_entry_id in( "
            + "select id from scm_product_entry "
            + "where product_entry_status_id = ? and id in(select product_entry_id from scm_product_entry_detail "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ?)))) "
            + "and product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ?))", false);

    // updates the is_tax_code_modified flag of Sales Invoice
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(product.getId());
    AppService.updateSql(main, SalesInvoice.class, "update scm_sales_invoice set is_tax_code_modified = ? "
            + "where sales_invoice_status_id = ? and id in(select sales_invoice_id from scm_sales_invoice_item "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ?)))", false);

    // updates the is_tax_code_modified flag of Sales Invoice item
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(product.getId());
    main.param(product.getId());
    AppService.updateSql(main, SalesInvoiceItem.class, "update scm_sales_invoice_item set is_tax_code_modified = ? "
            + "where sales_invoice_id in(select id from scm_sales_invoice "
            + "where sales_invoice_status_id = ? and id in(select sales_invoice_id from scm_sales_invoice_item  "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ?)))) "
            + "and product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ?))", false);

    // updates the is_tax_code_modified flag of Sales Return and Sales Return Item
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(product.getId());
    AppService.updateSql(main, SalesReturn.class, "update scm_sales_return set is_tax_code_modified = ? "
            + "where sales_return_status_id = ? and id in(select sales_return_id from scm_sales_return_item "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ?)))", false);

    // updates the is_tax_code_modified flag of Sales Return Item
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(product.getId());
    main.param(product.getId());
    AppService.updateSql(main, SalesReturnItem.class, "update scm_sales_return_item set is_tax_code_modified = ? "
            + "where sales_return_id in(select id from scm_sales_return "
            + "where sales_return_status_id = ? and id in(select sales_return_id from scm_sales_return_item  "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ?)))) "
            + "and product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id in(select id from scm_product where product_category_id = ?))) ", false);

    // updates the is_tax_code_modified flag of Purchase Return
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(product.getId());
    AppService.updateSql(main, PurchaseReturn.class, "update scm_purchase_return set is_tax_code_modified = ? "
            + "where purchase_return_status_id = ? and id in(select purchase_return_id from scm_purchase_return_item "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ? )))", false);

    // updates the is_tax_code_modified flag of Purchase Return Item
    main.clear();
    main.param(SystemConstants.TAX_CODE_MODIFIED);
    main.param(SystemConstants.DRAFT);
    main.param(product.getId());
    main.param(product.getId());
    AppService.updateSql(main, PurchaseReturnItem.class, "update scm_purchase_return_item set is_tax_code_modified = ? "
            + "where purchase_return_id in(select id from scm_purchase_return "
            + "where purchase_return_status_id = ? and id in(select purchase_return_id from scm_purchase_return_item "
            + "where product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ?)))) "
            + "and product_detail_id in(select id from scm_product_detail "
            + "where product_batch_id in (select id from scm_product_batch "
            + "where product_id = ?))", false);
    main.clear();
  }

  /**
   *
   * @param main
   * @param product
   * @return
   */
  public static boolean isProductSold(Main main, Product product) {
    return AppService.exist(main, "select 1 from scm_sales_invoice_item t1 "
            + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
            + "inner join scm_product_batch t3 on t2.product_batch_id = t3.id and t3.product_id = ?"
            + "union all "
            + "select 1 from scm_sales_return_item t1 "
            + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
            + "inner join scm_product_batch t3 on t2.product_batch_id = t3.id and t3.product_id = ?", new Object[]{product.getId(), product.getId()});
  }

  /**
   *
   * @param main
   * @param company
   * @param filter
   * @return
   */
  public static List<Product> selectProductByAccount(Main main, ServiceCommodity commodity, Company company, String filter) {
    List<Object> params = new ArrayList<>();
    StringBuilder sql = new StringBuilder("select t1.id,t1.product_name from scm_product t1 where t1.company_id = ? ");
    params.add(company.getId());

    try {
      if (commodity != null) {
        sql.append("and t1.commodity_id = ? ");
        params.add(commodity.getId());
      }

      if (!StringUtil.isEmpty(filter)) {
        filter = "%" + filter.toUpperCase() + "%";
        sql.append("and upper(t1.product_name) like ? ");
        params.add(filter);
      }
      sql.append("order by t1.product_name");
      return AppService.list(main, Product.class, sql.toString(), params.toArray());
    } finally {
      params = null;
      sql = null;
    }
  }

  public static List<ProductProfile> selectProductProfile(Main main, Integer productId) {
    return AppDb.getList(main.dbConnector(), ProductProfile.class, "select * from getProductProfile(?)", new Object[]{productId});
  }

  public static List<ProductStockSummary> getProductInventory(Main main, Product product) {
    if (product != null) {
      return AppDb.getList(main.dbConnector(), ProductStockSummary.class, "SELECT account_id,batch_no as batch,pack_size,expiry_date_actual as expiry_date,"
              + "mrp_value as mrp,SUM(COALESCE(quantity_saleable,0)) as saleable,\n"
              + "SUM(COALESCE(quantity_blocked,0)) as blocked,SUM(COALESCE(quantity_damaged,0)) as damaged,\n"
              + "SUM(COALESCE(quantity_expired,0)) as expired ,SUM(COALESCE(quantity_excess,0)) as excess,\n"
              + "SUM(COALESCE(saleable_value,0)) as saleable_value,SUM(COALESCE(nonsaleable_value,0)) as nonsaleable_value,SUM(COALESCE(excess_value,0)) as excess_value \n"
              + "FROM getInventoryofProduct(?) GROUP BY account_id,batch_no,pack_size,expiry_date_actual,mrp_value", new Object[]{product.getId()});
    }
    return null;
  }

  public static List<ProductInvoiceDetail> getLastPurchases(Main main, Product product) {
    if (product != null) {
      return AppDb.getList(main.dbConnector(), ProductInvoiceDetail.class, "SELECT PEnt.id,Acc.account_title as account_code,PEnt.invoice_no,PEnt.product_entry_date as invoice_date,\n"
              + "PEntDet.product_quantity as qty,PEntDet.product_quantity_free as qty_free,PEntDet.value_rate_per_prod_piece_der as value_rate\n"
              + "FROM scm_product_entry_detail as PEntDet,scm_product_entry as PEnt,scm_account AS Acc WHERE PEntDet.product_entry_id =PEnt.id \n"
              + "AND PEnt.product_entry_status_id>=2 AND PEnt.account_id = Acc.id AND PEntDet.product_detail_id IN(SELECT scm_product_detail.id \n"
              + "FROM scm_product_detail,scm_product_batch WHERE scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "AND scm_product_batch.product_id=?) AND PEnt.opening_stock_entry!=2 ORDER BY PEnt.product_entry_date DESC,PEnt.id DESC LIMIT 5", new Object[]{product.getId()});
    }
    return null;
  }

  public static List<ProductInvoiceDetail> getLastSales(Main main, Product product) {
    if (product != null) {
      return AppDb.getList(main.dbConnector(), ProductInvoiceDetail.class, "SELECT AccGrp.group_name as account_code,SalInv.invoice_no,SalInv.invoice_date,"
              + " SalInvItem.product_qty as qty,SalInvItem.product_qty_free as qty_free,\n"
              + "SalInvItem.value_pts,SalInvItem.scheme_discount_percentage , SalInvItem.product_discount_percentage, SalInvItem.invoice_discount_value as invoice_discount,\n"
              + "SalInvItem.value_prod_piece_sold as value_rate\n"
              + "FROM scm_sales_invoice as SalInv,scm_sales_invoice_item as SalInvItem,scm_account_group AS AccGrp WHERE \n"
              + "SalInv.id =SalInvItem.sales_invoice_id \n"
              + "AND SalInv.sales_invoice_status_id>=2 AND SalInv.account_group_id = AccGrp.id AND SalInvItem.product_detail_id IN(SELECT scm_product_detail.id \n"
              + "FROM scm_product_detail,scm_product_batch WHERE scm_product_detail.product_batch_id = scm_product_batch.id\n"
              + "AND scm_product_batch.product_id=?) ORDER BY SalInv.invoice_date DESC,SalInv.id DESC LIMIT 5", new Object[]{product.getId()});
    }
    return null;
  }

  public static boolean isBatchDeletable(Main main, ProductBatch batch) {
    boolean purchased = false;
    if (batch != null) {
      purchased = AppService.exist(main, "select '1' from scm_product_entry_detail where product_detail_id in "
              + "(select id from scm_product_detail where product_batch_id = ?)", new Object[]{batch.getId()});
    }
    return !purchased;
  }

  public static List<ProductClassification> selectProductClassificationByProduct(Main main, Product product) {
    return AppService.list(main, ProductClassification.class, "select id, title from scm_product_classification where id in (select classification_id from scm_product_classification_detail where product_id = ?)", new Object[]{product.getId()});
  }

  public static void updateProductName(Main main, Product product) {
    updateAble(main, product);
    main.clear();
    main.param(product.getProductName());
    main.param(product.getId());
    AppService.updateSql(main, Product.class, "UPDATE scm_product SET product_name=? WHERE id =?", false);
  }

  /**
   * Validate insert.
   *
   * @param main
   * @param product
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Product product) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_product where company_id = ? and brand_id = ? and product_packing_detail_id = ? and pack_size = ? and product_unit_id = ? and upper(product_name) = ?",
            new Object[]{product.getCompanyId().getId(), product.getBrandId().getId(), product.getProductPackingDetailId().getId(), product.getPackSize(), product.getProductUnitId().getId(), StringUtil.toUpperCase(product.getProductName().toString())})) {
      String fields = ValidateUtil.getFieldNames("productName") + "," + ValidateUtil.getFieldNames("packSize") + "," + ValidateUtil.getFieldNames("unit");
      throw new UserMessageException("name.exist", new String[]{fields});
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param product
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Product product) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_product where company_id = ? and brand_id = ? and product_packing_detail_id = ? and pack_size = ? and product_unit_id = ? and upper(product_name)=? and id != ?",
            new Object[]{product.getCompanyId().getId(), product.getBrandId().getId(), product.getProductPackingDetailId().getId(), product.getPackSize(), product.getProductUnitId().getId(), StringUtil.toUpperCase(product.getProductName().toString()), product.getId()})) {
      String fields = ValidateUtil.getFieldNames("productName") + "," + ValidateUtil.getFieldNames("packSize") + "," + ValidateUtil.getFieldNames("unit");
      throw new UserMessageException("name.exist", new String[]{fields});
    }
  }
}
