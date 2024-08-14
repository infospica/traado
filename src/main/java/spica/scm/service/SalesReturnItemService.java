/*
 * @(#)SalesReturnItemService.java	1.0 Mon Jan 29 16:45:34 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.scm.common.InvoiceItem;
import spica.scm.common.ProductSummary;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnCreditSettlement;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesReturnItem;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.scm.util.SalesInvoiceItemUtil;
import spica.scm.validate.SalesReturnItemIs;
import spica.sys.SystemConstants;
import wawo.entity.core.AppDb;

/**
 * SalesReturnItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jan 29 16:45:34 IST 2018
 */
public abstract class SalesReturnItemService {

  /**
   * SalesReturnItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesReturnItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_return_item", SalesReturnItem.class, main);
    sql.main("select scm_sales_return_item.id,scm_sales_return_item.account_id,scm_sales_return_item.sales_return_id,scm_sales_return_item.product_batch_id,scm_sales_return_item.product_detail_id,"
            + "scm_sales_return_item.tax_code_id,scm_sales_return_item.sales_invoice_id,scm_sales_return_item.sales_invoice_item_id,scm_sales_return_item.product_quantity,"
            + "scm_sales_return_item.product_quantity_damaged,"
            + "scm_sales_return_item.pack_size,"
            + "scm_sales_return_item.value_rate_sold,scm_sales_return_item.value_rate,scm_sales_return_item.value_mrp,scm_sales_return_item.value_pts,"
            + "scm_sales_return_item.expected_landing_rate,scm_sales_return_item.value_ptr,scm_sales_return_item.landing_price_per_piece_company,scm_sales_return_item.scheme_discount_value,"
            + "scm_sales_return_item.scheme_discount_value_derived,scm_sales_return_item.product_discount_value,scm_sales_return_item.product_discount_value_derived,"
            + "scm_sales_return_item.invoice_discount_value,scm_sales_return_item.invoice_discount_value_derived,scm_sales_return_item.cash_discount_value,"
            + "scm_sales_return_item.cash_discount_value_derived,scm_sales_return_item.value_goods,scm_sales_return_item.value_assessable,scm_sales_return_item.value_cgst,"
            + "scm_sales_return_item.value_sgst,scm_sales_return_item.value_igst,scm_sales_return_item.return_rate_per_piece,scm_sales_return_item.created_at,"
            + "scm_sales_return_item.modified_at,scm_sales_return_item.created_by,scm_sales_return_item.modified_by,scm_sales_return_item.product_hash_code, "
            + "scm_sales_return_item.is_tax_code_modified,scm_sales_return_item.product_detail_hash from scm_sales_return_item scm_sales_return_item "); //Main query
    sql.count("select count(scm_sales_return_item.id) as total from scm_sales_return_item scm_sales_return_item "); //Count query
    sql.join("left outer join scm_account scm_sales_return_itemaccount_id on (scm_sales_return_itemaccount_id.id = scm_sales_return_item.account_id) "
            + "left outer join scm_sales_return scm_sales_return_itemsales_return_id on (scm_sales_return_itemsales_return_id.id = scm_sales_return_item.sales_return_id) "
            + "left outer join scm_product_batch scm_product_batch_id on (scm_product_batch_id.id = scm_sales_return_item.product_batch_id) "
            + "left outer join scm_product_detail scm_sales_return_itemproduct_detail_id on (scm_sales_return_itemproduct_detail_id.id = scm_sales_return_item.product_detail_id) "
            + "left outer join scm_tax_code scm_sales_return_itemtax_code_id on (scm_sales_return_itemtax_code_id.id = scm_sales_return_item.tax_code_id) "
            + "left outer join scm_sales_invoice scm_sales_return_itemsales_invoice_id on (scm_sales_return_itemsales_invoice_id.id = scm_sales_return_item.sales_invoice_id) "
            + "left outer join scm_sales_invoice_item scm_sales_return_itemsales_invoice_item_id on (scm_sales_return_itemsales_invoice_item_id.id = scm_sales_return_item.sales_invoice_item_id) "
    ); //Join Query

    sql.string(new String[]{"scm_sales_return_itemaccount_id.account_code", "scm_sales_return_itemsales_return_id.invoice_no", "scm_sales_return_itemproduct_detail_id.batch_no", "scm_sales_return_itemtax_code_id.code", "scm_sales_return_itemsales_invoice_id.invoice_no", "scm_sales_return_item.pack_size", "scm_sales_return_item.created_by", "scm_sales_return_item.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_return_item.id", "scm_sales_return_itemsales_invoice_item_id.product_qty", "scm_sales_return_item.product_quantity", "scm_sales_return_item.product_quantity_damaged", "scm_sales_return_item.value_rate_sold", "scm_sales_return_item.value_rate", "scm_sales_return_item.value_mrp", "scm_sales_return_item.value_pts", "scm_sales_return_item.value_ptr", "scm_sales_return_item.landing_price_per_piece_company", "scm_sales_return_item.scheme_discount_value", "scm_sales_return_item.scheme_discount_value_derived", "scm_sales_return_item.product_discount_value", "scm_sales_return_item.product_discount_value_derived", "scm_sales_return_item.invoice_discount_value", "scm_sales_return_item.invoice_discount_value_derived", "scm_sales_return_item.cash_discount_value", "scm_sales_return_item.cash_discount_value_derived", "scm_sales_return_item.value_goods", "scm_sales_return_item.value_assessable", "scm_sales_return_item.value_cgst", "scm_sales_return_item.value_sgst", "scm_sales_return_item.value_igst", "scm_sales_return_item.return_rate_per_piece"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_return_item.created_at", "scm_sales_return_item.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesReturnItem.
   *
   * @param main
   * @return List of SalesReturnItem
   */
  public static final List<SalesReturnItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesReturnItemSqlPaged(main));
  }

//  /**
//   * Return list of SalesReturnItem based on condition
//   * @param main
//   * @return List<SalesReturnItem>
//   */
//  public static final List<SalesReturnItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesReturnItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesReturnItem by key.
   *
   * @param main
   * @param salesReturnItem
   * @return SalesReturnItem
   */
  public static final SalesReturnItem selectByPk(Main main, SalesReturnItem salesReturnItem) {
    return (SalesReturnItem) AppService.find(main, SalesReturnItem.class, salesReturnItem.getId());
  }

  /**
   * Insert SalesReturnItem.
   *
   * @param main
   * @param salesReturnItem
   */
  public static final void insert(Main main, SalesReturnItem salesReturnItem) {
    SalesReturnItemIs.insertAble(main, salesReturnItem);  //Validating
    AppService.insert(main, salesReturnItem);

  }

  /**
   * Update SalesReturnItem by key.
   *
   * @param main
   * @param salesReturnItem
   * @return SalesReturnItem
   */
  public static final SalesReturnItem updateByPk(Main main, SalesReturnItem salesReturnItem) {
    SalesReturnItemIs.updateAble(main, salesReturnItem); //Validating
    return (SalesReturnItem) AppService.update(main, salesReturnItem);
  }

  /**
   * Insert or update SalesReturnItem
   *
   * @param main
   * @param salesReturnItem
   */
  public static void insertOrUpdate(Main main, SalesReturnItem salesReturnItem) {
    if (salesReturnItem.getId() == null) {
      insert(main, salesReturnItem);
    } else {
      updateByPk(main, salesReturnItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesReturnItem
   */
  public static void clone(Main main, SalesReturnItem salesReturnItem) {
    salesReturnItem.setId(null); //Set to null for insert
    insert(main, salesReturnItem);
  }

  /**
   * Delete SalesReturnItem.
   *
   * @param main
   * @param salesReturnItem
   */
  public static final void deleteByPk(Main main, SalesReturnItem salesReturnItem) {
    SalesReturnItemIs.deleteAble(main, salesReturnItem); //Validation
    deleteCreditSettlementBySalesReturnItem(main, salesReturnItem);
    AppService.delete(main, SalesReturnItem.class, salesReturnItem.getId());
  }

  /**
   * Delete Array of SalesReturnItem.
   *
   * @param main
   * @param salesReturnItem
   */
  public static final void deleteByPkArray(Main main, SalesReturnItem[] salesReturnItem) {
    for (SalesReturnItem e : salesReturnItem) {
      deleteByPk(main, e);
    }
  }

  public static List<SalesReturnItem> selectSalesReturnItemList(Main main, SalesReturn salesReturn) {
    main.clear();
    List<SalesReturnItem> salesReturnItemList = null;
    if (SystemConstants.CONFIRMED.equals(salesReturn.getSalesReturnStatusId().getId())) {
      String sql = "select row_number() OVER () as id,string_agg(id::varchar, '#') as sales_return_item_hash,sales_return_id,product_batch_id,product_detail_id,sum(product_quantity) product_quantity,sum(product_quantity_damaged) product_quantity_damaged,\n"
              + "expected_landing_rate,value_rate,value_mrp,value_pts,value_ptr,tax_code_id,SUM(product_discount_value) product_discount_value,SUM(scheme_discount_value) scheme_discount_value,\n"
              + "SUM(invoice_discount_value) invoice_discount_value,SUM(credit_settlement_amount) as credit_settlement_amount,\n"
              + "sum(coalesce(value_cgst,0)) value_cgst,sum(coalesce(value_cgst,0)) value_cgst,sum(coalesce(value_igst,0)) value_igst,sum(coalesce(value_assessable,0)) value_assessable,\n"
              + "scheme_discount_value_derived,product_discount_value_derived,invoice_discount_value_derived,sales_invoice_id,product_hash_code,ref_invoice_no,ref_invoice_date,is_tax_code_modified,scheme_discount_percentage, \n"
              + "product_discount_percentage,purchase_rate,pack_description,credit_exist,remarks \n"
              + "from scm_sales_return_item where sales_return_id = ?\n"
              + "group by sales_return_id,product_batch_id,product_detail_id,tax_code_id,expected_landing_rate,purchase_rate,value_rate,value_mrp,value_pts,value_ptr,tax_code_id, \n"
              + "scheme_discount_value_derived,product_discount_value_derived,invoice_discount_value_derived,sales_invoice_id,product_hash_code,ref_invoice_no,ref_invoice_date,is_tax_code_modified,scheme_discount_percentage,\n"
              + "product_discount_percentage,pack_description,credit_exist,remarks \n";
      salesReturnItemList = AppService.list(main, SalesReturnItem.class, sql, new Object[]{salesReturn.getId()});
      updateProductSummery(main, salesReturn, salesReturnItemList);
    } else {
      if (salesReturn.getId() != null) {
        String sql = "select * from scm_sales_return_item where sales_return_id = ? order by id asc";
        salesReturnItemList = AppService.list(main, SalesReturnItem.class, sql, new Object[]{salesReturn.getId()});
        updateProductSummery(main, salesReturn, salesReturnItemList);
      }
    }
    return salesReturnItemList;
  }

  private static void updateProductSummery(Main main, SalesReturn salesReturn, List<SalesReturnItem> productEntryDetailList) {
    for (SalesReturnItem salesReturnItem : productEntryDetailList) {
      salesReturnItem.setProduct(salesReturnItem.getProductBatchId().getProductId());
      salesReturnItem.setProductSummary(new ProductSummary(salesReturnItem.getProduct().getId(), salesReturnItem.getProductBatchId().getId(), salesReturnItem.getProduct().getProductName(), salesReturnItem.getProductBatchId().getBatchNo()));
      salesReturnItem.getProductSummary().setBatch(salesReturnItem.getProductBatchId().getBatchNo());
      salesReturnItem.getProductSummary().after();
      salesReturnItem.setProductBatchSummary(new ProductSummary(salesReturnItem.getProduct().getId(), salesReturnItem.getProductBatchId().getId(), salesReturnItem.getProduct().getProductName(), salesReturnItem.getSalesInvoiceId() == null ? null : salesReturnItem.getSalesInvoiceId().getId(), null, salesReturnItem.getProductBatchId().getBatchNo()));
      salesReturnItem.getProductBatchSummary().setBatch(salesReturnItem.getProductBatchId().getBatchNo());
      if (salesReturnItem.getSalesInvoiceId() != null) {
        salesReturnItem.getProductBatchSummary().setSchemeDiscountDerived(salesReturnItem.getSchemeDiscountValueDerived());
        salesReturnItem.getProductBatchSummary().setProductDiscountDerived(salesReturnItem.getProductDiscountValueDerived());
        salesReturnItem.getProductBatchSummary().setValuePtr(salesReturnItem.getValuePtr());
        salesReturnItem.getProductBatchSummary().setValuePts(salesReturnItem.getValuePts());
        salesReturnItem.getProductBatchSummary().setProdPieceSellingForced(salesReturnItem.getValueRate());
      }
      salesReturnItem.getProductBatchSummary().after();
      if (salesReturnItem.getSalesInvoiceId() == null) {
        salesReturnItem.getProductBatchSummary().setProductCode(salesReturnItem.getProductHashCode());
      }
      salesReturnItem.setCurrentTaxCode(salesReturnItem.getCurrentTaxCode());
      if (salesReturnItem.getAccountId() != null && salesReturnItem.getProduct() != null) {
        salesReturnItem.setProductPreset(ProductPresetService.selectProductPresetByProductAndAccount(main, salesReturnItem.getProduct().getId(), salesReturnItem.getAccountId()));
      }
      if (salesReturn.isLimitReturnQty()) {
        salesReturnItem.setMaxReturnableQty(salesReturnItem.getProductQuantitySold());
      }
      //salesReturnItem.getProductSummary().after();
      //salesReturnItem.after();
    }
  }

  public static void updateSalesReturnItemFromSales(Main main, SalesReturnItem salesReturnItem, SalesReturn salesReturn) {
    List<Object> params = new ArrayList<>();
    String sql = "";
    StringBuilder condition = new StringBuilder();
//    StringBuilder join = new StringBuilder();
    int count = 0;
    if (salesReturn != null && salesReturnItem.getSalesInvoiceId() != null) {

//      params.add(salesReturnItem.getSalesInvoiceId().getId());
//
//      if (salesReturnItem.getProductBatchSummary() != null && salesReturnItem.getProductBatchSummary().getProdPieceSellingForced() != null) {
//        condition.append(" AND s_inv_item.prod_piece_selling_forced = ?");
//        params.add(salesReturnItem.getProductBatchSummary().getProdPieceSellingForced());
//      }
//
//      if (salesReturnItem.getProductBatchSummary() != null && salesReturnItem.getProductBatchSummary().getMrpValue() != null) {
//        condition.append("  AND p_list.value_mrp =? ");
//        params.add(salesReturnItem.getProductBatchSummary().getMrpValue());
//      }
//
//      if (salesReturnItem.getProductDetailHash() != null) {
//        condition.append(" AND s_inv_item.product_detail_id in(");
//        for (String productDetailId : salesReturnItem.getProductDetailHash().split("#")) {
//          if (count == 0) {
//            condition.append("?");
//          } else {
//            condition.append(",?");
//          }
//          params.add(Integer.parseInt(productDetailId));
//          count++;
//        }
//        condition.append(") ");
//      }
//
//      if (salesReturnItem.getProductDiscountPercentage() != null) {
//        condition.append("and s_inv_item.product_discount_percentage = ? ");
//        params.add(salesReturnItem.getProductDiscountPercentage());
//      } else {
//        condition.append("and s_inv_item.product_discount_percentage is null ");
//      }
//
//      if (salesReturnItem.getSchemeDiscountPercentage() != null) {
//        condition.append("and s_inv_item.scheme_discount_percentage = ? ");
//        params.add(salesReturnItem.getSchemeDiscountPercentage());
//      } else {
//        condition.append("and s_inv_item.scheme_discount_percentage is null ");
//      }
//      AccountGroupPriceList agpList = (AccountGroupPriceList) AppService.single(main, AccountGroupPriceList.class, "SELECT * FROM scm_account_group_price_list WHERE id=?",
//              new Object[]{salesReturnItem.getSalesInvoiceId().getAccountGroupPriceListId().getId()});
//      if (agpList.getIsDefault().equals(1)) {
//        condition.append(" AND \n"
//                + "p_list.account_group_price_list_id IN\n"
//                + "(SELECT id FROM scm_account_group_price_list WHERE is_default=1 AND account_group_id IN(\n"
//                + "SELECT scm_account_group_detail.account_group_id  FROM scm_account_group,scm_account_group_detail WHERE \n"
//                + "scm_account_group.id  = scm_account_group_detail.account_group_id AND scm_account_group.is_default=1 \n"
//                + "AND scm_account_group_detail.account_id IN(SELECT AG_Prim.account_id FROM scm_account_group_detail as AG_Prim WHERE AG_Prim.account_group_id=?))) ");
//        params.add(salesReturn.getAccountGroupId().getId());
//      } else {
//        condition.append(" AND p_list.account_group_price_list_id = ? ");
//        params.add(salesReturnItem.getSalesInvoiceId().getAccountGroupPriceListId().getId());
//      }
//    Sales InvoiceIds are passed on at the time of batch selection
      if (salesReturnItem.getSalesInvoiceItemIds() != null) {
        for (String salesInvoiceItemId : salesReturnItem.getSalesInvoiceItemIds().split(",")) {
          if (count == 0) {
            condition.append("?");
          } else {
            condition.append(",?");
          }
          params.add(Integer.parseInt(salesInvoiceItemId));
          count++;
        }
        condition.append(") ");
      }

      sql = "SELECT ROW_NUMBER() OVER() as id,MIN(s_inv_item.product_entry_detail_id) as product_entry_detail_id, \n"
              + "SUM(COALESCE(s_inv_item.product_qty,0)) product_qty,\n"
              + "SUM(COALESCE(s_inv_item.product_qty_free,0)) as product_qty_free, \n"
              + "s_inv_item.prod_piece_selling_forced,s_inv_item.value_mrp,s_inv_item.value_pts,\n"
              + "s_inv_item.value_ptr,s_inv_item.scheme_discount_percentage,\n"
              + "s_inv_item.product_discount_percentage,s_inv_item.invoice_discount_derived,s_inv_item.quantity_or_free,"
              + "s_inv_item.cash_discount_value_derived \n"
              + "FROM scm_sales_invoice_item s_inv_item\n"
              + "WHERE\n"
              + "s_inv_item.id in( " + condition.toString()
              + " GROUP BY s_inv_item.prod_piece_selling_forced,s_inv_item.invoice_discount_derived,s_inv_item.quantity_or_free,\n"
              + "s_inv_item.value_mrp,s_inv_item.value_pts,s_inv_item.value_ptr,\n"
              + "s_inv_item.scheme_discount_percentage,s_inv_item.product_discount_percentage,s_inv_item.cash_discount_value_derived";

      SalesInvoiceItem sitem = (SalesInvoiceItem) AppService.single(main, SalesInvoiceItem.class, sql, params.toArray());

      if (sitem != null) {
        if (sitem.getProductEntryDetailId() != null) {
          salesReturnItem.setValuePtr(sitem.getValuePtr());
          salesReturnItem.setValuePts(sitem.getValuePts());
          if (salesReturnItem.getSalesInvoiceId() != null && salesReturnItem.getSalesInvoiceId().getIsMrpChanged() != null && salesReturnItem.getSalesInvoiceId().getIsMrpChanged().intValue() == 1) {
            salesReturnItem.setValueMrp(salesReturnItem.getValueMrp());
          } else {
            salesReturnItem.setValueMrp(sitem.getValueMrp());
          }
        }
        salesReturnItem.setValueRate(sitem.getProdPieceSellingForced());
        salesReturnItem.setSchemeDiscountValueDerived(sitem.getSchemeDiscountDerived());
        salesReturnItem.setProductDiscountValueDerived(sitem.getProductDiscountDerived());
        salesReturnItem.setInvoiceDiscountValueDerived(sitem.getInvoiceDiscountDerived());
        salesReturnItem.setSchemeDiscountValue(sitem.getSchemeDiscountDerived());
        salesReturnItem.setProductDiscountValue(sitem.getProductDiscountDerived());
        Double specialDisc = 0.0;
//-------------------Showing invoice discount + cash discount from sales as invoice_discount_derived (For GECO, must change later)
        if (true) {  //---------ADD required condition later (Should also add condition for the list showing batches)
          if (sitem.getInvoiceDiscountDerived() != null && sitem.getCashDiscountValueDerived() != null) {
            specialDisc = sitem.getInvoiceDiscountDerived() + sitem.getCashDiscountValueDerived();
          } else if (sitem.getInvoiceDiscountDerived() != null && sitem.getCashDiscountValueDerived() == null) {
            specialDisc = sitem.getInvoiceDiscountDerived();
          } else if (sitem.getInvoiceDiscountDerived() == null && sitem.getCashDiscountValueDerived() != null) {
            specialDisc = sitem.getCashDiscountValueDerived();
          }
        } else {
          specialDisc = sitem.getInvoiceDiscountDerived();
        }
        salesReturnItem.setInvoiceDiscountValueDerived(specialDisc);
        if (SalesInvoiceItemUtil.isFreeScheme(sitem.getQuantityOrFree())) {
          salesReturnItem.setProductQuantitySold(sitem.getProductQty().doubleValue());
        } else {
          salesReturnItem.setProductQuantitySold(sitem.getProductQty().doubleValue() + (sitem.getProductQtyFree() == null ? 0 : sitem.getProductQtyFree().doubleValue()));
        }
        salesReturnItem.setProductQuantityFreeSold(sitem.getProductQtyFree());
        if (salesReturn.isLimitReturnQty()) {   //Limit the maximum quantity that can be returned using current sales invoice;
          salesReturnItem.setMaxReturnableQty(salesReturnItem.getProductQuantitySold());
        }
        // Mark MRP change flag 
        if (!salesReturnItem.getValueMrp().equals(salesReturnItem.getProductBatchId().getValueMrp())) {
          salesReturnItem.setIsMrpChanged(SystemConstants.DEFAULT);
        }
      }
      sitem = null;

    } else if (salesReturnItem.getProductPreset() != null && salesReturnItem.getProductBatchId() != null) {
      calculateProductPtrPtsValues(main, salesReturnItem);
    } else if (salesReturnItem.getAccountId() != null && salesReturnItem.getCurrentTaxCode() != salesReturnItem.getTaxCodeId()) {
      calculateProductPtrPtsValues(main, salesReturnItem);
    }
  }

  private static void calculateProductPtrPtsValues(Main main, SalesReturnItem salesReturnItem) {
    double mrpLte = ProductUtil.getMrpLteValue(salesReturnItem.getProductBatchId().getValueMrp(), salesReturnItem.getTaxCodeId().getRatePercentage());
    if (salesReturnItem.getAccountId() == null) {
      if (salesReturnItem.getProductDetailId() != null && salesReturnItem.getProductDetailId().getAccountId() != null) {
        salesReturnItem.setAccountId(salesReturnItem.getProductDetailId().getAccountId());
      } else {
        salesReturnItem.setAccountId(salesReturnItem.getProductPreset().getAccountId());
      }
    } else if (salesReturnItem.getProductPreset() == null) {
      salesReturnItem.setProductPreset(ProductPresetService.selectProductPresetByProductAndAccount(main, salesReturnItem.getProduct().getId(), salesReturnItem.getAccountId()));
    }

    // To Do : calculate ptr and pts based on pts derivation criteria,     
    if (AccountService.DERIVE_PTS_FROM_MRP_PTR.equals(salesReturnItem.getAccountId().getPtsDerivationCriteria())) {
      salesReturnItem.setValuePtr(MathUtil.roundOff(ProductUtil.getPtrValue(salesReturnItem.getProductPreset().getPtrMarginPercentage(), mrpLte, salesReturnItem.getProductPreset().getMrpltePtrRateDerivationCriterion()), 2));
      salesReturnItem.setValuePts(MathUtil.roundOff(ProductUtil.getPtsValue(salesReturnItem.getProductPreset().getPtsMarginPercentage(), salesReturnItem.getValuePtr(), salesReturnItem.getProductPreset().getPtrPtsRateDerivationCriterion()), 2));
      salesReturnItem.setValueMrp(salesReturnItem.getProductBatchId().getValueMrp());
      salesReturnItem.setQuantityOrFree(salesReturnItem.getAccountId().getIsSchemeApplicable());
    }
  }

  /**
   *
   * @param main
   * @param salesReturnItemId
   */
  public static final void updateProdutQuantity(Main main, SalesReturnItem salesReturnItemId) {
    String updateQry = "update scm_sales_return_item set product_quantity_damaged = ? where id = ?";
    main.clear();
    main.param(salesReturnItemId.getProductQuantityDamaged());
    main.param(salesReturnItemId.getId());
    AppService.updateSql(main, SalesReturnItem.class, updateQry, false);
    main.clear();
  }

  public static void updateSalesReturnItem(Main main, List<SalesReturnItem> openingSalesReturnList, boolean isDamagedReturn) {
    double productQty = 0.0;
    for (SalesReturnItem item : openingSalesReturnList) {
      productQty = isDamagedReturn ? item.getProductQuantityDamaged() : item.getProductQuantity();
      item.setReturnRatePerPiece(findReturnRatePerPiece(item, productQty));
      insertOrUpdate(main, item);
    }
  }

  public static double findReturnRatePerPiece(SalesReturnItem salesReturnItem, Double productQty) {
    double salesReturnRate = salesReturnItem.getValueRate();
    if (salesReturnItem.getSchemeDiscountValueDerived() != null) {
      salesReturnRate -= (salesReturnItem.getSchemeDiscountValueDerived());
    }
    if (salesReturnItem.getProductDiscountValueDerived() != null) {
      salesReturnRate -= (salesReturnItem.getProductDiscountValueDerived());
    }
    if (salesReturnItem.getInvoiceDiscountValueDerived() != null) {
      salesReturnRate -= (salesReturnItem.getInvoiceDiscountValueDerived());
    }
    return salesReturnRate;
  }

  public static List<InvoiceItem> selectSalesReturnItemForPrint(Main main, SalesReturn salesReturn) {
    String sql = null;

    sql = "SELECT T_SRetIte.hsn_code,T_Prod.product_name,COALESCE(T_ProdEnDet.pack_size,'') as pack_size,T_Man.name AS manufacture_name,T_Man.code AS mfg_code,\n"
            + "T_ProdBat.batch_no,to_char(T_ProdBat.expiry_date_actual,'Mon-yy') AS expiry_date,T_SRetIte.ref_invoice_no,T_SRetIte.ref_invoice_date,\n"
            + "ROUND(T_SRetIte.value_mrp,2) AS value_mrp,ROUND(T_SRetIte.value_ptr,2) AS value_ptr,ROUND(T_SRetIte.value_pts,2) AS value_pts,ROUND(T_SRetIte.value_rate,2) AS value_rate,\n"
            + "SUM(CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END) AS product_qty,\n"
            + "SUM((COALESCE(T_SRetIte.scheme_discount_value_derived,0)*CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END)) AS scheme_discount_value,\n"
            + "COALESCE(T_SRetIte.scheme_discount_percentage,0) AS scheme_discount_percentage,COALESCE(T_SRetIte.product_discount_percentage,0) AS product_discount_percentage,\n"
            + "SUM((COALESCE(T_SRetIte.product_discount_value_derived,0)*CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END)) AS product_discount_value,\n"
            + "SUM((COALESCE(T_SRetIte.invoice_discount_value_derived,0)*CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END)) AS invoice_discount_value,\n"
            + "SUM((COALESCE(T_SRetIte.cash_discount_value_derived,0)*CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END)) AS cash_discount_value,\n"
            + "SUM((COALESCE(T_SRetIte.return_rate_per_piece,0)*CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END)) AS taxable_amount,\n"
            + "SUM(COALESCE(T_SRetIte.value_goods,0)) AS value_goods,SUM(COALESCE(T_SRetIte.value_igst,0)) AS value_igst,\n"
            + "SUM(COALESCE(T_SRetIte.value_cgst,0)) AS value_cgst,SUM(COALESCE(T_SRetIte.value_sgst,0)) AS value_sgst, T_igst.rate_percentage AS igst_tax_rate\n"
            + ",T_cgst.rate_percentage AS cgst_tax_rate,T_sgst.rate_percentage AS sgst_tax_rate \n"
            + "FROM scm_sales_return AS T_SRet,scm_sales_return_item AS T_SRetIte\n"
            + "LEFT OUTER JOIN scm_tax_code T_igst ON T_SRetIte.tax_code_id = T_igst.id \n"
            + "LEFT OUTER JOIN scm_tax_code T_cgst ON T_cgst.parent_id = T_igst.id AND T_cgst.tax_type = ?\n"
            + "LEFT OUTER JOIN scm_tax_code T_sgst ON T_sgst.parent_id = T_igst.id AND T_sgst.tax_type = ?\n"
            + "LEFT OUTER JOIN scm_product_entry_detail as T_ProdEnDet ON T_ProdEnDet.id=T_SRetIte.product_entry_detail_id,\n"
            + "scm_product AS T_Prod,scm_product_batch AS T_ProdBat\n"
            + "LEFT OUTER JOIN scm_manufacture AS T_Man ON T_ProdBat.manufacture_id = T_Man.id \n"
            + "WHERE \n"
            + "T_SRet.id = T_SRetIte.sales_return_id AND  \n"
            + "T_SRetIte.product_batch_id= T_ProdBat.id AND\n"
            + "T_ProdBat.product_id = T_Prod.id \n"
            + "AND T_SRetIte.sales_return_id =?\n"
            + "GROUP BY \n"
            + "T_SRetIte.hsn_code,T_Prod.product_name,T_ProdEnDet.pack_size,T_Man.name,T_Man.code,\n"
            + "T_ProdBat.batch_no,T_ProdBat.expiry_date_actual,T_SRetIte.value_mrp,T_SRetIte.value_ptr,T_SRetIte.value_pts,T_SRetIte.value_rate,T_igst.rate_percentage\n"
            + ",T_cgst.rate_percentage ,T_sgst.rate_percentage,T_SRetIte.ref_invoice_no,T_SRetIte.ref_invoice_date,T_SRetIte.scheme_discount_percentage,T_SRetIte.product_discount_percentage \n"
            + "\n";
    List<InvoiceItem> list = null;
    list = AppDb.getList(main.dbConnector(), InvoiceItem.class, sql, new Object[]{AccountingConstant.TAX_TYPE_CGST, AccountingConstant.TAX_TYPE_SGST, salesReturn.getId()});
    return list;
  }

  public static void insertOrUpdateCreditSettlement(Main main, SalesReturnCreditSettlement salesReturnCreditSettlement) {
    if (salesReturnCreditSettlement.getId() != null) {
      AppService.update(main, salesReturnCreditSettlement);
    } else {
      AppService.insert(main, salesReturnCreditSettlement);
    }
  }

  public static SalesReturnCreditSettlement findCreditSettlement(Main main, SalesReturnItem salesReturnItem) {
    SalesReturnCreditSettlement creditSettlement = (SalesReturnCreditSettlement) AppService.single(main, SalesReturnCreditSettlement.class,
            "select * from scm_sales_return_credit_settlement where sales_return_item_id = ?", new Object[]{salesReturnItem.getId()});
    return creditSettlement;
  }

  public static void deleteCreditSettlement(Main main, SalesReturnCreditSettlement salesReturnCreditSettlement) {
    if (salesReturnCreditSettlement.getId() != null) {
      AppService.delete(main, SalesReturnCreditSettlement.class, salesReturnCreditSettlement);
    }
  }

  public static void deleteCreditSettlementBySalesReturnItem(Main main, SalesReturnItem salesReturnItem) {
    AppService.deleteSql(main, SalesReturnCreditSettlement.class, "delete from scm_sales_return_credit_settlement where sales_return_item_id = ?",
            new Object[]{salesReturnItem.getId()});
  }

  public static void deleteCreditSettlementBySalesReturn(Main main, SalesReturn salesReturn) {
    AppService.deleteSql(main, SalesReturnCreditSettlement.class, "delete from scm_sales_return_credit_settlement where sales_return_id = ?",
            new Object[]{salesReturn.getId()});
  }
}
