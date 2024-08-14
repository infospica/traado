/*
 * @(#)SalesOrderItemService.java	1.0 Fri Feb 03 19:15:39 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesOrder;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesOrderItem;
import spica.scm.domain.SalesReq;
import spica.scm.domain.SalesReqItem;
import spica.scm.validate.SalesOrderItemIs;
import wawo.entity.util.StringUtil;

/**
 * SalesOrderItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 03 19:15:39 IST 2017
 */
public abstract class SalesOrderItemService {

  /**
   * SalesOrderItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesOrderItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_order_item", SalesOrderItem.class, main);
    sql.main("select scm_sales_order_item.id,scm_sales_order_item.sales_order_id,scm_sales_order_item.product_id,scm_sales_order_item.qty_required,"
            + "scm_sales_order_item.qty_free_suggested,scm_sales_order_item.value_prod_piece_selling,scm_sales_order_item.tax_code_id,"
            + "scm_sales_order_item.value_prod_piece_selling_forced,scm_sales_order_item.discount_value_fixed,scm_sales_order_item.discount_value_percentage,"
            + "scm_sales_order_item.sales_req_item_id from scm_sales_order_item scm_sales_order_item "); //Main query
    sql.count("select count(scm_sales_order_item.id) as total from scm_sales_order_item scm_sales_order_item "); //Count query
    sql.join("left outer join scm_sales_order scm_sales_order on (scm_sales_order.id = scm_sales_order_item.sales_order_id) "
            + "left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_sales_order_item.product_detail_id) "
            + "left outer join scm_product scm_product on (scm_product.id = scm_sales_order_item.product_id) "
            + "left outer join scm_tax_code scm_tax_code on (scm_tax_code.id = scm_sales_order_item.tax_code_id) "
            + "left outer join scm_sales_req_item scm_sales_req_item on (scm_sales_req_item.id = scm_sales_order_item.sales_req_item_id)"); //Join Query
    sql.string(new String[]{"scm_sales_order.sales_order_no"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_order_item.id", "scm_sales_order_item.qty_required", "scm_sales_order_item.qty_free_suggested",
      "scm_sales_order_item.value_prod_piece_selling", "scm_sales_order_item.value_prod_piece_selling_forced",
      "scm_sales_order_item.discount_value_fixed", "scm_sales_order_item.discount_value_percentage", "scm_sales_req_item.qty_required"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesOrderItem.
   *
   * @param main
   * @return List of SalesOrderItem
   */
  public static final List<SalesOrderItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesOrderItemSqlPaged(main));
  }

  /**
   *
   * @param main
   * @param salesOrder
   * @return
   */
  public static final List<SalesOrderItem> selectSalesInvoiceItemBySalesInvoice(Main main, SalesOrder salesOrder) {
    List<SalesOrderItem> list = AppService.list(main, SalesInvoiceItem.class, "select * from scm_sales_order_item where sales_order_id = ?", new Object[]{salesOrder.getId()});
    return list;
  }

//  /**
//   * Return list of SalesOrderItem based on condition
//   * @param main
//   * @return List<SalesOrderItem>
//   */
//  public static final List<SalesOrderItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesOrderItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesOrderItem by key.
   *
   * @param main
   * @param salesOrderItem
   * @return SalesOrderItem
   */
  public static final SalesOrderItem selectByPk(Main main, SalesOrderItem salesOrderItem) {
    return (SalesOrderItem) AppService.find(main, SalesOrderItem.class, salesOrderItem.getId());
  }

  /**
   * Insert SalesOrderItem.
   *
   * @param main
   * @param salesOrderItem
   */
  public static final void insert(Main main, SalesOrderItem salesOrderItem) {
    SalesOrderItemIs.insertAble(main, salesOrderItem);  //Validating
    AppService.insert(main, salesOrderItem);

  }

  /**
   * Update SalesOrderItem by key.
   *
   * @param main
   * @param salesOrderItem
   * @return SalesOrderItem
   */
  public static final SalesOrderItem updateByPk(Main main, SalesOrderItem salesOrderItem) {
    SalesOrderItemIs.updateAble(main, salesOrderItem); //Validating
    return (SalesOrderItem) AppService.update(main, salesOrderItem);
  }

  /**
   * Insert or update SalesOrderItem
   *
   * @param main
   * @param salesOrderItem
   */
  public static void insertOrUpdate(Main main, SalesOrderItem salesOrderItem) {
    if (salesOrderItem.getId() == null) {
      insert(main, salesOrderItem);
    } else {
      updateByPk(main, salesOrderItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesOrderItem
   */
  public static void clone(Main main, SalesOrderItem salesOrderItem) {
    salesOrderItem.setId(null); //Set to null for insert
    insert(main, salesOrderItem);
  }

  /**
   * Delete SalesOrderItem.
   *
   * @param main
   * @param salesOrderItem
   */
  public static final void deleteByPk(Main main, SalesOrderItem salesOrderItem) {
    SalesOrderItemIs.deleteAble(main, salesOrderItem); //Validation
    String salesOrderItemIds[] = null;
    SalesOrderItem item = null;
    if (!StringUtil.isEmpty(salesOrderItem.getSalesOrderItemIdHashCode())) {
      salesOrderItemIds = salesOrderItem.getSalesOrderItemIdHashCode().split("#");
      if (salesOrderItemIds.length > 1) {
        for (String id : salesOrderItemIds) {
          item = new SalesOrderItem();
          item.setId(Integer.parseInt(id));
          AppService.delete(main, SalesOrderItem.class, item);
        }
      } else {
        salesOrderItem.setId(Integer.parseInt(salesOrderItem.getSalesOrderItemIdHashCode()));
        AppService.delete(main, SalesOrderItem.class, salesOrderItem.getId());
      }
    }
  }

  /**
   * Delete Array of SalesOrderItem.
   *
   * @param main
   * @param salesOrderItem
   */
  public static final void deleteByPkArray(Main main, SalesOrderItem[] salesOrderItem) {
    for (SalesOrderItem e : salesOrderItem) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param salesOrder
   * @return
   */
  public static final List<SalesOrderItem> selectSalesOrderItemBySalesOrder(Main main, SalesOrder salesOrder) {
    return AppService.list(main, SalesOrderItem.class, "select row_number() OVER () as id, string_agg(id::varchar, '#') as sales_order_item_id_hash_code, "
            + "string_agg(sales_req_item_id::varchar, '#') as sales_req_item_id_hash_code, sum(qty_required) qty_required, "
            + "product_id,value_prod_piece_selling,tax_code_id,qty_free_suggested,value_prod_piece_selling_forced,discount_value_fixed,discount_value_percentage "
            + "from scm_sales_order_item where sales_order_id = ? "
            + "group by product_id,value_prod_piece_selling,tax_code_id,qty_free_suggested,value_prod_piece_selling_forced,discount_value_fixed,discount_value_percentage", new Object[]{salesOrder.getId()});
  }

  /**
   *
   * @param main
   * @param salesOrder
   * @param salesOrderItemList
   */
  public static final void insertSalesOrderItem(Main main, SalesOrder salesOrder, List<SalesOrderItem> salesOrderItemList) {
    if (salesOrder != null && salesOrder.getId() != null) {
      AppService.deleteSql(main, SalesOrderItem.class, "delete from scm_sales_order_item where sales_order_id = ?", new Object[]{salesOrder.getId()});
    }

    String[] salesReqItemIds = null;
    Integer productQty = null;
    int counter = 0;
    if (salesOrderItemList != null && !salesOrderItemList.isEmpty()) {
      for (SalesOrderItem salesOrderItem : salesOrderItemList) {
        salesOrderItem.setSalesOrderId(salesOrder);
        counter = 0;
        if (!StringUtil.isEmpty(salesOrderItem.getSalesReqItemIdHashCode())) {
          salesReqItemIds = salesOrderItem.getSalesReqItemIdHashCode().split("#");
          if (salesReqItemIds.length > 1) {
            productQty = salesOrderItem.getQtyRequired();
            for (String salesReqItemId : salesReqItemIds) {
              counter++;
              SalesReqItem sitem = (SalesReqItem) AppService.single(main, SalesReqItem.class, "select scm_sales_req_item.id, (scm_sales_req_item.qty_required - sum(coalesce(scm_sales_order_item.qty_required,0))) as qty_required, "
                      + "(coalesce(scm_sales_req_item.qty_free_suggested,0) - sum(coalesce(scm_sales_order_item.qty_free_suggested,0))) as qty_free_suggested from scm_sales_req_item scm_sales_req_item "
                      + "left join scm_sales_order_item on scm_sales_order_item.sales_req_item_id = scm_sales_req_item.id "
                      + "where scm_sales_req_item.id = ? or scm_sales_order_item.sales_req_item_id = ? "
                      + "GROUP By scm_sales_order_item.sales_req_item_id, scm_sales_req_item.id", new Object[]{Integer.valueOf(salesReqItemId), Integer.valueOf(salesReqItemId)});
              if (productQty <= sitem.getQtyRequired() && sitem.getQtyRequired() > 0) {
                SalesOrderItem item = getSalesOrderItem(salesOrderItem, productQty);
                item.setSalesReqItemId(sitem);
                item.setSalesOrderId(salesOrder);
                insert(main, item);
                productQty = 0;
              } else if (productQty > sitem.getQtyRequired() && sitem.getQtyRequired() > 0) {
                SalesOrderItem item = getSalesOrderItem(salesOrderItem, sitem.getQtyRequired());
                item.setSalesReqItemId(sitem);
                item.setSalesOrderId(salesOrder);
                productQty -= sitem.getQtyRequired();
                if (counter == salesReqItemIds.length && productQty > 0) {
                  item.setQtyRequired(item.getQtyRequired() + productQty);
                }
                insert(main, item);
              }
            }
          } else {
            SalesReqItem sitem = new SalesReqItem();
            sitem.setId(Integer.parseInt(salesOrderItem.getSalesReqItemIdHashCode()));
            salesOrderItem.setId(null);
            salesOrderItem.setSalesReqItemId(sitem);
            insert(main, salesOrderItem);
          }
        } else {
          salesOrderItem.setId(null);
          insert(main, salesOrderItem);
        }
      }
    }
  }

  private static final SalesOrderItem getSalesOrderItem(SalesOrderItem salesOrderItem, Integer requiredQty) {
    SalesOrderItem sItem = new SalesOrderItem();
    sItem.setDiscountValueFixed(salesOrderItem.getDiscountValueFixed());
    sItem.setDiscountValuePercentage(salesOrderItem.getDiscountValuePercentage());
    sItem.setProductId(salesOrderItem.getProductId());
    sItem.setQtyRequired(requiredQty);
    sItem.setQtyFreeSuggested(salesOrderItem.getQtyFreeSuggested());
    sItem.setTaxCodeId(salesOrderItem.getTaxCodeId());
    sItem.setValueProdPieceSelling(salesOrderItem.getValueProdPieceSelling());
    sItem.setValueProdPieceSellingForced(salesOrderItem.getValueProdPieceSellingForced());

    return sItem;
  }

  /**
   *
   * @param main
   * @param salesOrder
   * @param salesReq
   * @return
   */
  public static final int insertSalesOrderItemFromSalesReq(Main main, SalesOrder salesOrder, SalesReq salesReq) {
    int insertCounter = 0;
    String sql = "select tab1.id, (coalesce(tab1.qty_required,0) - coalesce(tab2.qty_required,0)) as qty_required, "
            + "(coalesce(tab1.qty_free_suggested,0) - coalesce(tab2.qty_free_suggested,0)) as qty_free_suggested,tax_code_id, "
            + "tab1.product_id,tab1.value_prod_piece_selling,tab1.discount_value_fixed,tab1.discount_value_percentage,tab1.value_prod_piece_selling_forced "
            + "from (select * from scm_sales_req_item where sales_req_id = ?) tab1 "
            + "left outer join "
            + "(select sales_req_item_id, sum(coalesce(scm_sales_order_item.qty_required,0)) as qty_required, sum(coalesce(scm_sales_order_item.qty_free_suggested,0)) as qty_free_suggested "
            + "from scm_sales_order_item scm_sales_order_item "
            + "where sales_req_item_id in (select id from scm_sales_req_item where sales_req_id = ?) group by scm_sales_order_item.sales_req_item_id) tab2 "
            + "on tab1.id = tab2.sales_req_item_id;";

    List<SalesReqItem> list = AppService.list(main, SalesReqItem.class, sql, new Object[]{salesReq.getId(), salesReq.getId()});
    SalesOrderItem salesOrderItem = null;
    for (SalesReqItem salesReqItem : list) {
      if ((salesReqItem.getQtyRequired() != null && salesReqItem.getQtyRequired() > 0) || (salesReqItem.getQtyFreeSuggested() != null && salesReqItem.getQtyFreeSuggested() > 0)) {
        salesOrderItem = new SalesOrderItem();
        salesOrderItem.setSalesOrderId(salesOrder);
        salesOrderItem.setDiscountValueFixed(salesReqItem.getDiscountValueFixed());
        salesOrderItem.setDiscountValuePercentage(salesReqItem.getDiscountValuePercentage());
        salesOrderItem.setProductId(salesReqItem.getProductId());
        salesOrderItem.setQtyFreeSuggested(salesReqItem.getQtyFreeSuggested());
        salesOrderItem.setQtyRequired(salesReqItem.getQtyRequired());
        salesOrderItem.setSalesReqItemId(salesReqItem);
        salesOrderItem.setValueProdPieceSelling(salesReqItem.getValueProdPieceSelling());
        salesOrderItem.setValueProdPieceSellingForced(salesReqItem.getValueProdPieceSellingForced());
        salesOrderItem.setTaxCodeId(ProductService.getProductSalesTaxCode(main, salesOrderItem.getProductId()));
        insert(main, salesOrderItem);
        insertCounter++;
      }
    }
    return insertCounter;
  }

  /**
   *
   * @param main
   * @param salesOrderItem
   */
  public static void insertOrUpdateSalesOrderItem(Main main, SalesOrderItem salesOrderItem) {
    String[] salesReqItemIds = null;
    Integer productQty = null;
    int counter = 0;
    if (!StringUtil.isEmpty(salesOrderItem.getSalesReqItemIdHashCode())) {
      salesReqItemIds = salesOrderItem.getSalesReqItemIdHashCode().split("#");
      if (salesReqItemIds.length > 1) {
        productQty = salesOrderItem.getQtyRequired();
        for (String salesReqItemId : salesReqItemIds) {
          counter++;
          SalesReqItem sitem = (SalesReqItem) AppService.single(main, SalesReqItem.class, "select scm_sales_req_item.id, (scm_sales_req_item.qty_required - sum(coalesce(scm_sales_order_item.qty_required,0))) as qty_required, "
                  + "(coalesce(scm_sales_req_item.qty_free_suggested,0) - sum(coalesce(scm_sales_order_item.qty_free_suggested,0))) as qty_free_suggested from scm_sales_req_item scm_sales_req_item "
                  + "left join scm_sales_order_item on scm_sales_order_item.sales_req_item_id = scm_sales_req_item.id "
                  + "where scm_sales_req_item.id = ? or scm_sales_order_item.sales_req_item_id = ? "
                  + "GROUP By scm_sales_order_item.sales_req_item_id, scm_sales_req_item.id", new Object[]{Integer.valueOf(salesReqItemId), Integer.valueOf(salesReqItemId)});
          if (productQty <= sitem.getQtyRequired() && sitem.getQtyRequired() > 0) {
            SalesOrderItem item = getSalesOrderItem(salesOrderItem, productQty);
            item.setSalesReqItemId(sitem);
            item.setSalesOrderId(salesOrderItem.getSalesOrderId());
            insert(main, item);
            productQty = 0;
          } else if (productQty > sitem.getQtyRequired() && sitem.getQtyRequired() > 0) {
            SalesOrderItem item = getSalesOrderItem(salesOrderItem, sitem.getQtyRequired());
            item.setSalesReqItemId(sitem);
            item.setSalesOrderId(salesOrderItem.getSalesOrderId());
            productQty -= sitem.getQtyRequired();
            if (counter == salesReqItemIds.length && productQty > 0) {
              item.setQtyRequired(item.getQtyRequired() + productQty);
            }
            insert(main, item);
          }
        }
      } else {
        insert(main, salesOrderItem);
      }
    } else {
      insertOrUpdate(main, salesOrderItem);
    }
  }

  /**
   *
   * @param main
   * @param salesOrder
   * @param productType
   * @return
   */
  public static List<SalesOrderItem> selectSalesOrderItemByProductType(Main main, SalesOrder salesOrder, String productType) {
    List<SalesOrderItem> salesOrderItemList = AppService.list(main, SalesOrderItem.class, "select scm_sales_order_item.id as id,"
            + "scm_sales_order_item.sales_order_id, scm_sales_order_item.product_id,scm_sales_order_item.value_prod_piece_selling_forced,scm_sales_order_item.value_prod_piece_selling, "
            + "scm_sales_order_item.discount_value_fixed, scm_sales_order_item.tax_code_id, "
            + "(sum(scm_sales_order_item.qty_required) - (sum(coalesce(scm_sales_invoice_item.product_qty,0)))) as qty_required, "
            + "(sum(scm_sales_order_item.qty_free_suggested) - (sum(coalesce(scm_sales_invoice_item.product_qty_free,0)))) as qty_free_suggested "
            + "from scm_sales_order_item scm_sales_order_item "
            + "inner join scm_product scm_product on scm_product.id = scm_sales_order_item.product_id "
            + "inner join scm_product_type scm_product_type on scm_product_type.id = scm_product.product_type_id "
            + "inner join scm_tax_code scm_tax_code on scm_tax_code.id = scm_sales_order_item.tax_code_id "
            + "left join scm_sales_invoice_item scm_sales_invoice_item on scm_sales_invoice_item.sales_order_item_id = scm_sales_order_item.id "
            + "where scm_sales_order_item.sales_order_id = ? and scm_product_type.product_type_code = ? "
            + "group by scm_sales_order_item.id,scm_sales_order_item.product_id, scm_sales_order_item.sales_order_id,scm_sales_order_item.value_prod_piece_selling_forced,scm_sales_order_item.value_prod_piece_selling, "
            + "scm_sales_order_item.discount_value_fixed, scm_sales_order_item.tax_code_id "
            + "having (sum(scm_sales_order_item.qty_required) - sum(coalesce(scm_sales_invoice_item.product_qty,0))) > 0 "
            + "or (sum(scm_sales_order_item.qty_free_suggested) - (sum(coalesce(scm_sales_invoice_item.product_qty_free,0)))) > 0 ",
            new Object[]{salesOrder.getId(), productType});
    return salesOrderItemList;
  }
}
