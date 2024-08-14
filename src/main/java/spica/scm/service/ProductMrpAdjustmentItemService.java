/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.List;
import spica.fin.domain.TaxCode;
import spica.scm.common.ProductDefaultPriceList;
import spica.scm.domain.Company;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductMrpAdjustment;
import spica.scm.domain.ProductMrpAdjustmentItem;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Anoop Jayachandran
 */
public abstract class ProductMrpAdjustmentItemService {

  /**
   * ProductMrpAdjustmentItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductMrpAdjustmentItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_mrp_adjustment_item", ProductMrpAdjustmentItem.class, main);
    sql.main("select scm_product_mrp_adjustment_item.id,scm_product_mrp_adjustment_item.product_mrp_adjustment_id,scm_product_mrp_adjustment_item.product_detail_id,"
            + "scm_product_mrp_adjustment_item.product_entry_detail_id,scm_product_mrp_adjustment_item.saleable_qty,scm_product_mrp_adjustment_item.damaged_qty,"
            + "scm_product_mrp_adjustment_item.value_mrp_old,scm_product_mrp_adjustment_item.value_mrp,"
            + "scm_product_mrp_adjustment_item.value_credit,scm_product_mrp_adjustment_item.value_debit "
            + "from scm_product_mrp_adjustment_item scm_product_mrp_adjustment_item"); //Main query
    sql.count("select count(scm_product_mrp_adjustment_item.id) as total from scm_product_mrp_adjustment_item scm_product_mrp_adjustment_item"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_product_mrp_adjustment_item.title", "scm_product_mrp_adjustment_item.display_color", "scm_product_mrp_adjustment_item.created_by", "scm_product_mrp_adjustment_item.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_mrp_adjustment_item.id", "scm_product_mrp_adjustment_item.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_mrp_adjustment_item.created_at", "scm_product_mrp_adjustment_item.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductMrpAdjustmentItem.
   *
   * @param main
   * @return List of ProductMrpAdjustmentItem
   */
  public static final List<ProductMrpAdjustmentItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductMrpAdjustmentItemSqlPaged(main));
  }

  /**
   *
   * @param main
   * @param productMrpAdjustment
   * @return
   */
  public static final List<ProductMrpAdjustmentItem> listProductMrpAdjustmentItems(Main main, ProductMrpAdjustment productMrpAdjustment) {

    List<ProductMrpAdjustmentItem> list = AppService.list(main, ProductMrpAdjustmentItem.class, "select * from scm_product_mrp_adjustment_item where product_mrp_adjustment_id = ? order by id", new Object[]{productMrpAdjustment.getId()});

    //List<ProductDefaultPriceList> pricelist = selectProductDefaultPriceList(main, productMrpAdjustment.getProductBatchId(), productMrpAdjustment.getAccountId());
    // To Do : need to update latest saleable and damaged qty
    //for (ProductDefaultPriceList rate : pricelist) {
    //}
    return list;
  }

  /**
   *
   * @param main
   * @param productMrpAdjustment
   */
  public static final void deleteProductMrpAdjustmentItemByProductMrpAdjustment(Main main, ProductMrpAdjustment productMrpAdjustment) {
    AppService.deleteSql(main, ProductMrpAdjustmentItem.class, "delete from scm_product_mrp_adjustment_item where product_mrp_adjustment_id = ?", new Object[]{productMrpAdjustment.getId()});
  }

//  /**
//   * Return list of ProductMrpAdjustmentItem based on condition
//   * @param main
//   * @return List<ProductMrpAdjustmentItem>
//   */
//  public static final List<ProductMrpAdjustmentItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductMrpAdjustmentItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductMrpAdjustmentItem by key.
   *
   * @param main
   * @param productMrpAdjustmentItem
   * @return ProductMrpAdjustmentItem
   */
  public static final ProductMrpAdjustmentItem selectByPk(Main main, ProductMrpAdjustmentItem productMrpAdjustmentItem) {
    return (ProductMrpAdjustmentItem) AppService.find(main, ProductMrpAdjustmentItem.class, productMrpAdjustmentItem.getId());
  }

  /**
   * Insert ProductMrpAdjustmentItem.
   *
   * @param main
   * @param productMrpAdjustmentItem
   */
  public static final void insert(Main main, ProductMrpAdjustmentItem productMrpAdjustmentItem) {
    insertAble(main, productMrpAdjustmentItem);  //Validating
    AppService.insert(main, productMrpAdjustmentItem);

  }

  /**
   * Update ProductMrpAdjustmentItem by key.
   *
   * @param main
   * @param productMrpAdjustmentItem
   * @return ProductMrpAdjustmentItem
   */
  public static final ProductMrpAdjustmentItem updateByPk(Main main, ProductMrpAdjustmentItem productMrpAdjustmentItem) {
    updateAble(main, productMrpAdjustmentItem); //Validating
    return (ProductMrpAdjustmentItem) AppService.update(main, productMrpAdjustmentItem);
  }

  /**
   * Insert or update ProductMrpAdjustmentItem
   *
   * @param main
   * @param productMrpAdjustmentItem
   */
  public static void insertOrUpdate(Main main, ProductMrpAdjustmentItem productMrpAdjustmentItem) {
    if (productMrpAdjustmentItem.getId() == null) {
      insert(main, productMrpAdjustmentItem);
    } else {
      updateByPk(main, productMrpAdjustmentItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productMrpAdjustmentItem
   */
  public static void clone(Main main, ProductMrpAdjustmentItem productMrpAdjustmentItem) {
    productMrpAdjustmentItem.setId(null); //Set to null for insert
    insert(main, productMrpAdjustmentItem);
  }

  /**
   * Delete ProductMrpAdjustmentItem.
   *
   * @param main
   * @param productMrpAdjustmentItem
   */
  public static final void deleteByPk(Main main, ProductMrpAdjustmentItem productMrpAdjustmentItem) {
    deleteAble(main, productMrpAdjustmentItem); //Validation
    AppService.delete(main, ProductMrpAdjustmentItem.class, productMrpAdjustmentItem.getId());
  }

  /**
   * Delete Array of ProductMrpAdjustmentItem.
   *
   * @param main
   * @param productMrpAdjustmentItem
   */
  public static final void deleteByPkArray(Main main, ProductMrpAdjustmentItem[] productMrpAdjustmentItem) {
    for (ProductMrpAdjustmentItem e : productMrpAdjustmentItem) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param productMrpAdjustmentItem
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, ProductMrpAdjustmentItem productMrpAdjustmentItem) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param productMrpAdjustmentItem
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, ProductMrpAdjustmentItem productMrpAdjustmentItem) throws UserMessageException {
  }

  /**
   * Validate update.
   *
   * @param main
   * @param productMrpAdjustmentItem
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, ProductMrpAdjustmentItem productMrpAdjustmentItem) throws UserMessageException {
  }

  public static void insertOrUpdate(Main main, ProductMrpAdjustment productMrpAdjustment, List<ProductMrpAdjustmentItem> productMrpAdjustmentItemList, boolean productModified) {
    ProductDefaultPriceList defaultPriceList = null;
    if (productModified) {
      deleteProductMrpAdjustmentItemByProductMrpAdjustment(main, productMrpAdjustment);
    }

    if (!StringUtil.isEmpty(productMrpAdjustmentItemList)) {
      for (ProductMrpAdjustmentItem productMrpAdjustmentItem : productMrpAdjustmentItemList) {
        if (productMrpAdjustmentItem.getValuePts() != null && productMrpAdjustmentItem.getValuePts() > 0) {
          productMrpAdjustmentItem.setProductMrpAdjustmentId(productMrpAdjustment);
          productMrpAdjustmentItem.setPreviousValueMrp(productMrpAdjustment.getPreviousValueMrp());
          productMrpAdjustmentItem.setValueMrp(productMrpAdjustment.getValueMrp());

          if (SystemConstants.CONFIRMED.equals(productMrpAdjustment.getStatusId())) {
            defaultPriceList = uniqueProductDefaultPriceList(main, productMrpAdjustment, productMrpAdjustmentItem);
            productMrpAdjustmentItem.setSaleableQty(defaultPriceList.getQuantitySaleable());
            productMrpAdjustmentItem.setDamagedQty(defaultPriceList.getQuantityDamaged());
            productMrpAdjustmentItem.setQuantityExpired(defaultPriceList.getQuantityExpired());
          }

          calculateCreditDebitValue(productMrpAdjustmentItem);
          insertOrUpdate(main, productMrpAdjustmentItem);
        }
      }
    }
  }

  /**
   *
   * @param main
   * @param productBatch
   * @param company
   * @return
   */
  public static List<ProductMrpAdjustmentItem> selectProductBatchStockDetails(Main main, ProductBatch productBatch, Company company) {
    List<ProductMrpAdjustmentItem> list = new ArrayList<>();
    List<ProductDefaultPriceList> stockDetails = selectProductDefaultPriceList(main, productBatch, company);
    for (ProductDefaultPriceList productStockDetail : stockDetails) {
      ProductMrpAdjustmentItem productMrpAdjustmentItem = new ProductMrpAdjustmentItem(productStockDetail);
      productMrpAdjustmentItem.setAccountId(AccountService.selectByPk(main, productMrpAdjustmentItem.getAccountId()));
      productMrpAdjustmentItem.setProductDetailId(ProductDetailService.selectByPk(main, productMrpAdjustmentItem.getProductDetailId()));
      productMrpAdjustmentItem.setProductEntryDetailId(ProductEntryDetailService.selectByPk(main, productMrpAdjustmentItem.getProductEntryDetailId()));
      list.add(productMrpAdjustmentItem);
    }
    return list;
  }

  public static List<ProductDefaultPriceList> selectProductDefaultPriceList(Main main, ProductBatch productBatch, Company company) {
    return AppDb.getList(main.dbConnector(), ProductDefaultPriceList.class, "SELECT * FROM getStockofProductForMrpChange(?,?) where product_batch_id = ? ", new Object[]{company.getId(), productBatch.getProductId().getId(), productBatch.getId()});
  }

  public static ProductDefaultPriceList uniqueProductDefaultPriceList(Main main, ProductMrpAdjustment productMrpAdjustment, ProductMrpAdjustmentItem productMrpAdjustmentItem) {
    return (ProductDefaultPriceList) AppDb.single(main.dbConnector(), ProductDefaultPriceList.class, "SELECT * FROM getStockofProductForMrpChange(?,?) where product_entry_detail_id = ?",
            new Object[]{productMrpAdjustment.getCompanyId().getId(), productMrpAdjustment.getProductBatchId().getProductId().getId(), productMrpAdjustmentItem.getProductEntryDetailId().getId()});
  }

  /**
   *
   * @param productMrpAdjustment
   * @param productMrpAdjustmentItems
   */
  public static final void processProductMrpChange(ProductMrpAdjustment productMrpAdjustment, List<ProductMrpAdjustmentItem> productMrpAdjustmentItems) {
    TaxCode taxCode = null;
    Double mrpLte;
    Double valuePtr, schemeDiscDer, prodDiscDer;
    Double valuePts, actualSellingPrice;

    for (ProductMrpAdjustmentItem productMrpAdjustmentItem : productMrpAdjustmentItems) {
      schemeDiscDer = null;
      prodDiscDer = null;
      ProductEntryDetail productEntryDetail = null;
      productEntryDetail = productMrpAdjustmentItem.getProductEntryDetailId();
      productMrpAdjustmentItem.setValueMrp(productMrpAdjustment.getValueMrp());
      if (AccountService.DERIVE_PTS_FROM_MRP_PTR.equals(productMrpAdjustmentItem.getProductDetailId().getAccountId().getPtsDerivationCriteria())) {
        taxCode = productMrpAdjustmentItem.getProductDetailId().getProductBatchId().getProductId().getProductCategoryId().getPurchaseTaxCodeId();
        mrpLte = ProductUtil.getMrpLteValue(productMrpAdjustment.getValueMrp(), taxCode.getRatePercentage());
        valuePtr = ProductUtil.getPtrValue(productEntryDetail.getPtrMarginPercentage(), mrpLte, productEntryDetail.getMrpltePtrRateDerivationCriterion());
        valuePts = ProductUtil.getPtsValue(productEntryDetail.getPtsMarginPercentage(), valuePtr, productEntryDetail.getPtrPtsRateDerivationCriterion());
      } else {
        valuePts = productMrpAdjustmentItem.getPreviousValuePts();
        valuePtr = productMrpAdjustmentItem.getPreviousValuePtr();
      }
      actualSellingPrice = valuePts;

      productMrpAdjustmentItem.setValuePtr(valuePtr);
      productMrpAdjustmentItem.setValuePts(valuePts);
      if (productMrpAdjustmentItem.getSchemeDiscountPercentage() != null && productMrpAdjustmentItem.getIsSchemeDiscountToCustomer() == SystemConstants.DISCOUNT_FOR_CUSTOMER) {
        schemeDiscDer = (productMrpAdjustmentItem.getValueRate() * productMrpAdjustmentItem.getSchemeDiscountPercentage() / 100);
        actualSellingPrice -= schemeDiscDer;
      }

      if (productMrpAdjustmentItem.getProductDiscountPercentage() != null && productMrpAdjustmentItem.getIsProductDiscountToCustomer() == SystemConstants.DISCOUNT_FOR_CUSTOMER) {
        prodDiscDer = ((productMrpAdjustmentItem.getValueRate() - (schemeDiscDer == null ? 0 : schemeDiscDer)) * productMrpAdjustmentItem.getProductDiscountPercentage() / 100);
        actualSellingPrice -= prodDiscDer;
      }
      productMrpAdjustmentItem.setActualSellingPriceDerived(actualSellingPrice);
      productMrpAdjustmentItem.setExpectedLandingRate(ProductUtil.getExpectedLandingRate(productEntryDetail.getPtsSsRateDerivationCriterion(), actualSellingPrice,
              productEntryDetail.getMarginPercentage(), productMrpAdjustmentItem.getAccountId()));
      calculateCreditDebitValue(productMrpAdjustmentItem);
    }
  }

  public static final void calculateCreditDebitValue(ProductMrpAdjustmentItem productMrpAdjustmentItem) {
    Double difference = 0.0, value = 0.0, productQty = 0.0;

    productMrpAdjustmentItem.setValueDebit(null);
    productMrpAdjustmentItem.setValueCredit(null);

    if (productMrpAdjustmentItem.getPreviousExpectedLandingRate() != null) {

      productQty = productMrpAdjustmentItem.getSaleableQty() == null ? 0 : productMrpAdjustmentItem.getSaleableQty();
      difference = (productMrpAdjustmentItem.getExpectedLandingRate() - productMrpAdjustmentItem.getPreviousExpectedLandingRate());

      value = productQty * difference;

      if (difference > 0) {
        productMrpAdjustmentItem.setValueCredit(Math.abs(MathUtil.roundOff(value, 2)));
      } else {
        productMrpAdjustmentItem.setValueDebit(Math.abs(MathUtil.roundOff(value, 2)));
      }
    }
  }

}
