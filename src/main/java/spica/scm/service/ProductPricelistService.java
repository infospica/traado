/*
 * @(#)ProductPricelistService.java	1.0 Wed Apr 13 15:41:17 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import spica.scm.common.PriceListDetails;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductMrpAdjustment;
import spica.scm.domain.ProductMrpAdjustmentItem;
import spica.scm.domain.ProductPriceListVo;
import spica.scm.domain.ProductPricelist;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.scm.validate.ProductPricelistIs;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.util.StringUtil;

/**
 * ProductPricelistService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:17 IST 2016
 */
public abstract class ProductPricelistService {

  /**
   * Select ProductPricelist by key.
   *
   * @param main
   * @param productPricelist
   * @return ProductPricelist
   */
  public static final ProductPricelist
          selectByPk(Main main, ProductPricelist productPricelist) {
    return (ProductPricelist) AppService.find(main, ProductPricelist.class,
            productPricelist.getId());
  }

  /**
   * Insert ProductPricelist.
   *
   * @param main
   * @param productPricelist
   */
  public static final void insert(Main main, ProductPricelist productPricelist) {
    ProductPricelistIs.insertAble(main, productPricelist);  //Validating
    productPricelist.setCreatedAt(new Date());
    productPricelist.setCreatedBy(UserRuntimeView.instance().getAppUser() != null ? UserRuntimeView.instance().getAppUser().getName() : null);
    AppService.insert(main, productPricelist);

  }

  /**
   * Update ProductPricelist by key.
   *
   * @param main
   * @param productPricelist
   * @return ProductPricelist
   */
  public static final ProductPricelist updateByPk(Main main, ProductPricelist productPricelist) {
    ProductPricelistIs.updateAble(main, productPricelist); //Validating
    productPricelist.setModifiedAt(new Date());
    productPricelist.setModifiedBy(UserRuntimeView.instance().getAppUser() != null ? UserRuntimeView.instance().getAppUser().getName() : null);
    return (ProductPricelist) AppService.update(main, productPricelist);
  }

  /**
   * Insert or update ProductPricelist
   *
   * @param main
   * @param productPricelist
   */
  public static void insertOrUpdate(Main main, ProductPricelist productPricelist) {
    if (productPricelist.getId() == null) {
      insert(main, productPricelist);
    } else {
      updateByPk(main, productPricelist);
    }
  }

  public static void insertOrUpdatePriceListVo(Main main, List<ProductPriceListVo> productPricelistVo, AccountGroupPriceList accountGroupPriceList, Map<Integer, List> productPriceListMap) {
    if (productPricelistVo != null) {
      for (ProductPriceListVo priceList : productPricelistVo) {
        main.getParamData().clear();
        if (priceList.getId() != null && priceList.getValuePtsPerProdPieceSell() != null) {
          if (priceList.isDataModified()) {
            Account account = AccountService.selectByPk(main, new Account(priceList.getAccountId()));
            ProductUtil.productPriceListCalculation(priceList, account);
            main.param(priceList.getValuePtsPerProdPieceSell());
            main.param(priceList.getValuePtrPerProdPiece());
            main.param(priceList.getActualSellingPriceDerived());
            main.param(priceList.getExpectedLandingRate());
            main.param(priceList.getMarginValueDeviationDer());
            main.param(priceList.getMarginPercentageDeviation());
            main.param(priceList.getId());
            AppService.updateSql(main, ProductPricelist.class, "update scm_product_pricelist set value_pts_per_prod_piece=? , value_ptr_per_prod_piece = ?,"
                    + "actual_selling_price_derived=?,expected_landing_rate=?,margin_value_deviation_der=?,margin_percentage_deviation=? where id=? ", false);
          }
//      the value pts in table contains the originial pts in the time of purchase. It is not affected by update

        } else if (priceList.getId() == null && priceList.getValuePtsPerProdPieceSell() != null) {
          Account account = AccountService.selectByPk(main, new Account(priceList.getAccountId()));
          ProductUtil.productPriceListCalculation(priceList, account);
          ProductPricelist entity = new ProductPricelist();
          entity.setAccountGroupPriceListId(new AccountGroupPriceList(accountGroupPriceList.getId()));
          entity.setValuePtsPerProdPieceSell(priceList.getValuePtsPerProdPieceSell());
          entity.setValuePtrPerProdPieceSell(priceList.getValuePtrPerProdPiece());
          entity.setProductDetailId(new ProductDetail(priceList.getProductDetailId()));
          entity.setProductEntryDetailId(new ProductEntryDetail(priceList.getProductEntryDetailId()));
          entity.setValueMrp(priceList.getValueMrp());
          entity.setValuePtr(priceList.getValuePtr());
          entity.setValuePts(priceList.getValuePts());
//          entity.setSchemeDiscountPercentage(priceList.getSchemeDiscountPercentage());
//          entity.setSchemeDiscountValueDerived(priceList.getSchemeDiscountValueDerived());
//          entity.setIsSchemeDiscountToCustomer(priceList.getIsSchemeDiscountToCustomer());
//          entity.setSchemeDiscountReplacement(priceList.getSchemeDiscountReplacement());
//          entity.setProductDiscountPercentage(priceList.getProductDiscountPercentage());
//          entity.setProductDiscountValueDerived(priceList.getProductDiscountValueDerived());
//          entity.setIsProductDiscountToCustomer(priceList.getIsProductDiscountToCustomer());
          entity.setLandingRate(priceList.getLandingRate());
          entity.setMarginPercentageDeviation(priceList.getMarginPercentageDeviation());
          entity.setMarginValueDeviation(entity.getMarginValueDeviation());
          entity.setVendorMarginValue(priceList.getVendorMarginValue());
          entity.setMarginValueDeviationDer(priceList.getMarginValueDeviationDer());
          entity.setVendorMarginValueDer(priceList.getVendorMarginValueDer());
          entity.setExpectedLandingRate(priceList.getExpectedLandingRate());
          entity.setActualSellingPriceDerived(priceList.getActualSellingPriceDerived());
          entity.setIsMrpChange(priceList.getIsMrpChange());
          AppService.insert(main, entity);

        } else if ((!SystemConstants.DEFAULT.equals(accountGroupPriceList.getIsDefault()) && priceList.getId() != null)
                && (priceList.getValuePtsPerProdPieceSell() == null || priceList.getValuePtsPerProdPieceSell() == 0)) {
          AppService.deleteSql(main, ProductPricelist.class,
                  "delete from scm_product_pricelist where id = ?", new Object[]{priceList.getId()});

        }
      }
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productPricelist
   */
  public static void clone(Main main, ProductPricelist productPricelist) {
    productPricelist.setId(null); //Set to null for insert
    insert(main, productPricelist);
  }

  /**
   * Delete ProductPricelist.
   *
   * @param main
   * @param productPricelist
   */
  public static final void deleteByPk(Main main, ProductPricelist productPricelist) {
    ProductPricelistIs.deleteAble(main, productPricelist); //Validation
    AppService.delete(main, ProductPricelist.class, productPricelist.getId());
  }

  /**
   * Delete Array of ProductPricelist.
   *
   * @param main
   * @param productPricelist
   */
  public static final void deleteByPkArray(Main main, ProductPricelist[] productPricelist) {
    for (ProductPricelist e : productPricelist) {
      deleteByPk(main, e);
    }
  }

  public static List<ProductPricelist> selectDefaultProductPriceList(Main main, AccountGroupPriceList accountGroupPriceList) {
    List<ProductPricelist> list = AppService.list(main, ProductPricelist.class,
            "select * from scm_product_pricelist where account_group_price_list_id = ?", new Object[]{accountGroupPriceList.getId()});
    return list;
  }

  public static List<ProductPricelist> selectDefaultProductPriceList(Main main, Account account) {
    List<ProductPricelist> list = AppService.list(main, ProductPricelist.class,
            "select * from scm_product_pricelist "
            + "where account_group_price_list_id in ( "
            + "select agpl.id from scm_account_group_price_list agpl "
            + "inner join scm_account_group_detail agd on agpl.account_group_id = agd.account_group_id "
            + "where agd.account_id = ? "
            + ")", new Object[]{account.getId()});
    return list;
  }

  public static List<ProductPricelist> selectProductPriceListByProductBatch(Main main, ProductBatch productBatch) {
    if (productBatch == null) {
      return null;
    } else {
      return AppService.list(main, ProductPricelist.class,
              "select t1.id,t1.account_group_price_list_id,t1.product_detail_id,t1.value_pts_per_prod_piece "
              + "from scm_product_pricelist t1 inner join scm_product_detail t2 on t1.product_detail_id = t2.id and t2.product_batch_id = ? "
              + "group by t1.id ,t1.value_pts_per_prod_piece", new Object[]{productBatch.getId()});
    }
  }

  public static void updatePriceList(Main main, ProductPricelist priceList) {
    if (priceList.getId() != null && priceList.getValuePtsPerProdPieceSell() != null) {
      main.param(priceList.getValuePtsPerProdPieceSell());
      main.param(priceList.getId());
      AppService.updateSql(main, ProductPricelist.class, "update scm_product_pricelist set value_pts_per_prod_piece=? where id=? ", false);
    }
  }

  public static void insertOrUpdateProductPriceList(Main main, ProductEntry productEntry, AccountGroupPriceList accountGroupPriceList) {
    List<ProductEntryDetail> list = AppService.list(main, ProductEntryDetail.class,
            "select * from scm_product_entry_detail where scm_product_entry_detail.product_entry_id = ? and is_service is null", new Object[]{productEntry.getId()});
    if (list != null) {
      ProductPricelist productPricelist;
      for (ProductEntryDetail ped : list) {
        AppService.deleteSql(main, ProductPricelist.class, "delete from scm_product_pricelist where product_entry_detail_id is null and product_detail_id = ?", new Object[]{ped.getProductDetailId().getId()});
        productPricelist = new ProductPricelist();
        productPricelist.setAccountGroupPriceListId(accountGroupPriceList);
        productPricelist.setProductDetailId(ped.getProductDetailId());
        productPricelist.setProductEntryDetailId(ped);
        productPricelist.setValuePtsPerProdPieceSell(ped.getValuePtsPerProdPiece());
        productPricelist.setValuePtrPerProdPieceSell(ped.getValuePtrPerProdPiece());
        productPricelist.setValueMrp(ped.getValueMrp());
        productPricelist.setValuePtr(ped.getValuePtr());
        productPricelist.setValuePts(ped.getValuePts());
        productPricelist.setSchemeDiscountValueDerived(ped.getSchemeDiscountValueDerived());
        productPricelist.setIsSchemeDiscountToCustomer(ped.getIsSchemeDiscountToCustomer());
        productPricelist.setProductDiscountValueDerived(ped.getProductDiscountValueDerived());
        productPricelist.setIsProductDiscountToCustomer(ped.getIsLineDiscountToCustomer());
        productPricelist.setMarginValueDeviation(ped.getMarginValueDeviation());
        productPricelist.setVendorMarginValue(ped.getVendorMarginValue());
        productPricelist.setLandingRate(ped.getValueRatePerProdPieceDer());
        productPricelist.setExpectedLandingRate(ped.getExpectedLandingRate());
        productPricelist.setActualSellingPriceDerived(ped.getActualSellingPriceDerived());
        productPricelist.setMarginPercentageDeviation(ped.getMarginPercentageDeviation());
        productPricelist.setMarginValueDeviationDer(ped.getMarginValueDeviationDer());
        productPricelist.setVendorMarginValueDer(ped.getVendorMarginValueDer());
        productPricelist.setProductDiscountPercentage(ped.getDiscountPercentage());
        productPricelist.setSchemeDiscountPercentage(ped.getSchemeDiscountPercentage());
        productPricelist.setSchemeDiscountReplacement(ped.getSchemeDiscountReplacement());
        productPricelist.setProductDiscountReplacement(ped.getProductDiscountReplacement());
        insert(main, productPricelist);
      }
    }
  }

  public static ProductPricelist selectProductPriceListByAccountGroupAndProduct(Main main, AccountGroup accountGroup, Product product) {
    return (ProductPricelist) AppService.single(main, ProductPricelist.class, "select scm_product_pricelist.* from scm_product_pricelist "
            + "inner join scm_account_group_price_list on scm_product_pricelist.account_group_price_list_id = scm_account_group_price_list.id "
            + "inner join scm_account_group on scm_account_group.id = scm_account_group_price_list.account_group_id "
            + "inner join scm_product_detail on scm_product_detail.id = scm_product_pricelist.product_detail_id "
            + "inner join scm_product_preset on scm_product_preset.id = scm_product_detail.product_preset_id "
            + "where scm_account_group_price_list.account_group_id = ? and scm_account_group.is_default = 1 "
            + "and scm_account_group_price_list.is_default = 1 and scm_product_preset.product_id = ? order by scm_product_pricelist.id asc limit 1", new Object[]{accountGroup.getId(), product.getId()});
  }

  /**
   *
   * @param main
   * @param accountGroupPriceList
   * @param account
   * @return
   */
  public static List<ProductPriceListVo> selectProductPriceListByAccount(MainView main, AccountGroupPriceList accountGroupPriceList, Account account) {
    String sql = "";
    if (accountGroupPriceList.getIsDefault() != null && SystemConstants.DEFAULT == accountGroupPriceList.getIsDefault()) {
      sql = "SELECT p1.id,scm_product.product_name,scm_product_batch.batch_no,scm_product_batch.expiry_date_actual::date,scm_product_batch.value_mrp,\n"
              + "p1.value_pts_per_prod_piece as current_pts, p2.scheme_discount_percentage,p2.is_scheme_discount_to_customer,p2.pts_ss_rate_derivation_criterion,\n"
              + "p2.discount_percentage as product_discount_percentage,p2.is_product_discount_to_customer,p2.margin_percentage,p2.margin_value_deviation_der,\n"
              + "p2.value_rate_per_prod_piece_der,scm_product_detail.account_id,\n"
              + " p1.value_ptr_per_prod_piece, p1.value_pts_per_prod_piece,p1.value_ptr_per_prod_piece as default_ptr,\n"
              + "p1.value_pts_per_prod_piece as defaultprice,scm_account_group_price_list.is_default ,p2.expected_landing_rate, \n"
              + "p1.account_group_price_list_id,p1.product_entry_detail_id from scm_product_pricelist p1  \n"
              + "join scm_product_entry_detail p2 on(p2.id=p1.product_entry_detail_id) \n"
              + "join scm_product_detail on(p2.product_detail_id = scm_product_detail.id) \n"
              + "join scm_product_batch on(scm_product_detail.product_batch_id = scm_product_batch.id and scm_product_batch.expiry_date_actual> CURRENT_DATE ) \n"
              + "join scm_product on(scm_product.id = scm_product_batch.product_id) \n"
              + "join scm_account_group_price_list on(scm_account_group_price_list.id=p1.account_group_price_list_id) \n"
              + "WHERE p1.account_group_price_list_id = ?\n"
              + " order by scm_product.product_name,scm_product_batch.batch_no,scm_product_batch.expiry_date_actual, scm_product_batch.value_mrp, defaultprice,default_ptr ASC";
      return AppDb.getList(main.dbConnector(), ProductPriceListVo.class, sql, new Object[]{accountGroupPriceList.getId()});
    } else {
      sql = "SELECT ProdPLDef.id as default_id,ProdPL.id,Prod.product_name,ProdBat.batch_no,ProdBat.expiry_date_actual::date,ProdBat.value_mrp,\n"
              + "ProdPL.value_pts_per_prod_piece,ProdPL.value_pts_per_prod_piece as current_pts,ProdPL.value_ptr_per_prod_piece,\n"
              + "ProdPLDef.value_pts_per_prod_piece as default_price,ProdPLDef.product_entry_detail_id,ProdPLDef.product_detail_id,\n"
              + "ProdPLDef.value_ptr_per_prod_piece  as default_ptr,ProdPL.value_pts_per_prod_piece as valuePtsPerProdPieceSell, ProdPLDef.value_ptr,ProdPLDef.value_pts,\n"
              + "ProdPLDef.margin_value_deviation,ProdPLDef.vendor_margin_value,ProdPLDef.landing_rate,ProdPLDef.is_mrp_change,ProdPLDef.vendor_margin_value_der,\n"
              + "ProdEnDet.value_rate_per_prod_piece_der,ProdEnDet.actual_selling_price_derived,ProdEnDet.margin_percentage, 0 as is_default, \n"
              + "CASE WHEN ProdPL.value_pts_per_prod_piece IS NULL THEN ProdPLDef.value_pts_per_prod_piece ELSE ProdPL.value_pts_per_prod_piece END as pl_pts,\n"
              + "CASE WHEN ProdPL.value_ptr_per_prod_piece IS NULL THEN ProdPLDef.value_ptr_per_prod_piece ELSE ProdPL.value_ptr_per_prod_piece END as pl_ptr,\n"
              + "ProdDet.account_id as accountId \n"
              + "FROM scm_product_detail ProdDet, scm_product_entry_detail ProdEnDet, scm_product_batch ProdBat, scm_product Prod , scm_product_pricelist  ProdPLDef\n"
              + "LEFT OUTER JOIN scm_product_pricelist ProdPL ON ProdPLDef.product_entry_detail_id = ProdPL.product_entry_detail_id \n"
              + "AND ProdPLDef.product_entry_detail_id = ProdPL.product_entry_detail_id \n"
              + "AND ProdPL.account_group_price_list_id = ?\n"
              + "WHERE ProdEnDet.id = ProdPLDef.product_entry_detail_id AND ProdEnDet.product_detail_id = ProdDet.id AND ProdDet.product_batch_id = ProdBat.id\n"
              + "AND Prod.id = ProdBat.product_id AND ProdBat.expiry_date_actual> CURRENT_DATE AND \n"
              + "ProdDet.account_id IN(SELECT id FROM scm_account WHERE account_id =? UNION SELECT id FROM scm_account WHERE id =? ) and\n"
              + "ProdPLDef.account_group_price_list_id IN(\n"
              + "SELECT scm_account_group_price_list.id \n"
              + "FROM scm_account_group_price_list WHERE account_group_id IN(\n"
              + "select id from scm_account_group where is_default = ? and id in(select account_group_id from scm_account_group_detail where account_id = ?)) and scm_account_group_price_list.is_default=? )  \n"
              + "order by Prod.product_name,ProdBat.batch_no,ProdBat.expiry_date_actual,ProdBat.value_mrp,pl_pts,pl_ptr ASC";
      if (account.getAccountId() == null) {
        return AppDb.getList(main.dbConnector(), ProductPriceListVo.class, sql, new Object[]{accountGroupPriceList.getId(), account.getId(), account.getId(),
          SystemConstants.DEFAULT, account.getId(), SystemConstants.DEFAULT});
      } else {
        return AppDb.getList(main.dbConnector(), ProductPriceListVo.class, sql, new Object[]{accountGroupPriceList.getId(), account.getAccountId().getId(), account.getAccountId().getId(),
          SystemConstants.DEFAULT, account.getAccountId().getId(), SystemConstants.DEFAULT});
      }
    }
  }

  public static List<ProductPriceListVo> selectProductPriceListByAccountGroup(Main main, AccountGroupPriceList accountGroupPriceList, AccountGroup accountGroup) {
    String sql = "";
    if (accountGroupPriceList.getIsDefault() != null && SystemConstants.DEFAULT == accountGroupPriceList.getIsDefault()) {
      sql = "SELECT ProdPLDef.id,ProdPLDef.account_group_price_list_id,Prod.product_name,ProdBat.batch_no,ProdBat.expiry_date_actual::date,ProdBat.value_mrp,ProdPLDef.value_pts_per_prod_piece,\n"
              + "ProdPLDef.value_pts_per_prod_piece as current_pts,ProdPLDef.value_ptr_per_prod_piece, \n"
              + "ProdPLDef.value_pts_per_prod_piece as default_price,ProdPLDef.product_entry_detail_id,ProdPLDef.product_detail_id, \n"
              + "ProdPLDef.value_pts_per_prod_piece as valuePtsPerProdPieceSell,ProdDet.account_id,\n"
              + " COALESCE(ProdPLDef.value_ptr_per_prod_piece,ProdEnDet.value_ptr) as default_ptr,ProdEnDet.expected_landing_rate as landing_rate,\n"
              + "ProdEnDet.expected_landing_rate, ProdEnDet.margin_value_deviation_der,ProdEnDet.margin_percentage,ProdEnDet.value_rate_per_prod_piece_der,\n"
              + "ProdEnDet.is_scheme_discount_to_customer,ProdEnDet.is_product_discount_to_customer,ProdEnDet.scheme_discount_percentage,\n"
              + "ProdEnDet.discount_percentage as product_discount_percentage,ProdEnDet.pts_ss_rate_derivation_criterion,1 as is_default \n"
              + "FROM scm_product_pricelist  ProdPLDef \n"
              + "JOIN scm_product_entry_detail ProdEnDet on(ProdEnDet.id = ProdPLDef.product_entry_detail_id) \n"
              + "JOIN scm_product_detail ProdDet on(ProdEnDet.product_detail_id = ProdDet.id) \n"
              + "JOIN scm_product_batch ProdBat on(ProdDet.product_batch_id = ProdBat.id and ProdBat.expiry_date_actual > CURRENT_DATE ) \n"
              + "JOIN scm_product Prod on(Prod.id = ProdBat.product_id) \n"
              + "WHERE ProdPLDef.account_group_price_list_id \n"
              + "IN(SELECT scm_account_group_price_list.id FROM scm_account_group_price_list \n"
              + "WHERE account_group_id IN(select id from scm_account_group where is_default = ?\n"
              + "and id in(select account_group_id from scm_account_group_detail \n"
              + "where account_id in(select account_id from scm_account_group_detail where account_group_id = ?))))  \n"
              + "order by Prod.product_name,ProdBat.batch_no,ProdPLDef.product_detail_id,landing_rate,valuePtsPerProdPieceSell ASC";
      return AppDb.getList(main.dbConnector(), ProductPriceListVo.class, sql, new Object[]{SystemConstants.DEFAULT, accountGroup.getId()});
    } else {
      sql = "SELECT ProdPLDef.id as default_id,ProdPL.id,Prod.product_name,ProdBat.batch_no,ProdBat.expiry_date_actual::date,ProdBat.value_mrp,\n"
              + "ProdPL.value_pts_per_prod_piece,ProdPL.value_pts_per_prod_piece as current_pts,ProdPL.value_ptr_per_prod_piece,\n"
              + "ProdPLDef.value_pts_per_prod_piece as default_price,ProdPLDef.product_entry_detail_id,ProdPLDef.product_detail_id,\n"
              + "ProdPLDef.value_ptr_per_prod_piece  as default_ptr,ProdPL.value_pts_per_prod_piece as valuePtsPerProdPieceSell, ProdPLDef.value_ptr,ProdPLDef.value_pts,\n"
              + "ProdPLDef.margin_value_deviation,ProdPLDef.vendor_margin_value,ProdPLDef.landing_rate,ProdPLDef.is_mrp_change,ProdPLDef.vendor_margin_value_der,\n"
              + "ProdEnDet.value_rate_per_prod_piece_der,ProdEnDet.actual_selling_price_derived,ProdEnDet.margin_percentage, 0 as is_default,ProdDet.account_id,  \n"
              + "CASE WHEN ProdPL.value_pts_per_prod_piece IS NULL THEN ProdPLDef.value_pts_per_prod_piece ELSE ProdPL.value_pts_per_prod_piece END as pl_pts,\n"
              + "CASE WHEN ProdPL.value_ptr_per_prod_piece IS NULL THEN ProdPLDef.value_ptr_per_prod_piece ELSE ProdPL.value_ptr_per_prod_piece END as pl_ptr\n"
              + "FROM scm_product_pricelist  ProdPLDef \n"
              + "JOIN scm_product_entry_detail ProdEnDet on(ProdEnDet.id = ProdPLDef.product_entry_detail_id) \n"
              + "JOIN scm_product_detail ProdDet on(ProdEnDet.product_detail_id = ProdDet.id) \n"
              + "JOIN scm_product_batch ProdBat on(ProdDet.product_batch_id = ProdBat.id) \n"
              + "JOIN scm_product Prod on(Prod.id = ProdBat.product_id) \n"
              + "LEFT OUTER JOIN scm_product_pricelist ProdPL ON ProdPLDef.product_detail_id = ProdPL.product_detail_id \n"
              + "AND ProdPLDef.product_entry_detail_id = ProdPL.product_entry_detail_id AND ProdPL.account_group_price_list_id = ?\n"
              + "WHERE ProdPLDef.account_group_price_list_id \n"
              + "IN(SELECT scm_account_group_price_list.id FROM scm_account_group_price_list \n"
              + "WHERE account_group_id IN(select id from scm_account_group where is_default = ? \n"
              + "and id in(select account_group_id from scm_account_group_detail \n"
              + "where account_id in(select account_id from scm_account_group_detail where account_group_id = ?)))) \n"
              + "order by Prod.product_name,ProdBat.batch_no,ProdBat.expiry_date_actual,ProdBat.value_mrp,pl_pts,pl_ptr ASC";
    }

    return AppDb.getList(main.dbConnector(), ProductPriceListVo.class, sql, new Object[]{accountGroupPriceList.getId(), SystemConstants.DEFAULT, accountGroup.getId()});
  }

  public static void updatePriceList(Main main, List<ProductMrpAdjustmentItem> productMrpAdjustmentItemList) {
    double actualSellingPrice = 0.0;
    for (ProductMrpAdjustmentItem productMrpAdjustmentItem : productMrpAdjustmentItemList) {
      ProductPricelist productPricelist = (ProductPricelist) AppService.single(main, ProductPricelist.class, "select t1.* from scm_product_pricelist t1 "
              + "inner join scm_account_group_price_list t2 on t1.account_group_price_list_id = t2.id "
              + "where t1.product_entry_detail_id = ? and t2.is_default = ?;",
              new Object[]{productMrpAdjustmentItem.getProductEntryDetailId().getId(), SystemConstants.DEFAULT});

      productPricelist.setValuePtsPerProdPieceSell(productMrpAdjustmentItem.getValuePts());
      productPricelist.setValuePtrPerProdPieceSell(productMrpAdjustmentItem.getValuePtr());
      productPricelist.setValueMrp(productMrpAdjustmentItem.getValueMrp());
      productPricelist.setValuePtr(productMrpAdjustmentItem.getValuePtr());
      productPricelist.setValuePts(productMrpAdjustmentItem.getValuePts());
      productPricelist.setSchemeDiscountPercentage(productMrpAdjustmentItem.getSchemeDiscountPercentage());
      productPricelist.setProductDiscountPercentage(productMrpAdjustmentItem.getProductDiscountPercentage());
      productPricelist.setProductDiscountValueDerived(null);
      productPricelist.setSchemeDiscountValueDerived(null);
      productPricelist.setLandingRate(productMrpAdjustmentItem.getLandingRate());
      productPricelist.setActualSellingPriceDerived(productMrpAdjustmentItem.getActualSellingPriceDerived());
      productPricelist.setExpectedLandingRate(productMrpAdjustmentItem.getExpectedLandingRate());
      productPricelist.setIsMrpChange(SystemConstants.DEFAULT);
      productPricelist.setMarginValueDeviationDer((productMrpAdjustmentItem.getExpectedLandingRate() != null ? productMrpAdjustmentItem.getExpectedLandingRate() : 0)
              - (productMrpAdjustmentItem.getLandingRate() != null ? productMrpAdjustmentItem.getLandingRate() : 0));
      ProductPricelistService.updateByPk(main, productPricelist);
      AppService.deleteSql(main, ProductPricelist.class, "delete from scm_product_pricelist t1  "
              + "using scm_account_group_price_list t2 where t1.account_group_price_list_id = t2.id \n"
              + " and t1.product_entry_detail_id = ? and t2.is_default = ?;",
              new Object[]{productMrpAdjustmentItem.getProductEntryDetailId().getId(), 0});
    }
  }

  public static void updateCustomPriceList(Main main, ProductMrpAdjustment productMrpAdjustment) {
    List<ProductPricelist> customProductPriceList = AppService.list(main, ProductPricelist.class, "select t1.* from scm_product_pricelist t1 "
            + "inner join scm_account_group_price_list t2 on t1.account_group_price_list_id = t2.id and t2.is_default <> ? "
            + "inner join scm_product_detail t3 on t1.product_detail_id = t3.id "
            + "where t1.product_detail_id in(select id from scm_product_detail where product_batch_id = ?) ",
            new Object[]{SystemConstants.DEFAULT, productMrpAdjustment.getProductBatchId().getId()});

    if (!StringUtil.isEmpty(customProductPriceList)) {
      for (ProductPricelist customProductPrice : customProductPriceList) {
        customProductPrice.setValuePtrPerProdPieceSell(null);
        customProductPrice.setValuePtsPerProdPieceSell(null);
        ProductPricelistService.updateByPk(main, customProductPrice);
      }
    }

  }

  public static PriceListDetails getPriceListDetails(Main main, ProductPriceListVo productPriceListVo) {
    if (productPriceListVo != null) {
      return (PriceListDetails) AppDb.single(main.dbConnector(), PriceListDetails.class, "SELECT * FROM getProductPriceListSummary(?,?) ",
              new Object[]{productPriceListVo.getId() == null ? productPriceListVo.getDefaultId() : productPriceListVo.getId(),
                productPriceListVo.getProductEntryDetailId()});
    }
    return null;
  }
}
