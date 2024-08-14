/*
 * @(#)SalesOrderService.java	1.0 Tue May 17 15:23:09 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Consignment;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesInvoiceStatus;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesOrder;
import spica.scm.domain.SalesOrderItem;
import spica.scm.domain.SalesReq;
import spica.scm.tax.TaxCalculator;
import spica.scm.validate.SalesOrderIs;
import spica.sys.SystemConstants;

/**
 * SalesOrderService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 17 15:23:09 IST 2016
 */
public abstract class SalesOrderService {

  //public static final Integer SALES_ORDER_STATUS_DRAFT = 2;
  //public static final Integer SALES_ORDER_STATUS_CONFIRMED = 1;
  //public static final Integer SALES_ORDER_STATUS_INVOICED = 3;
  //public static final Integer SALES_ORDER_STATUS_PART_PENDING = 4;
  /**
   * SalesOrder paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesOrderSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_order", SalesOrder.class, main);
    sql.main("select scm_sales_order.id,scm_sales_order.company_id,scm_sales_order.financial_year_id, scm_sales_order.sales_order_no, scm_sales_order.sales_order_date,scm_sales_order.account_group_price_list_id,"
            + "scm_sales_order.customer_id, scm_sales_order.account_group_id,scm_sales_order.discount_value_fixed,scm_sales_order.discount_value_perc,"
            + "scm_sales_order.sales_order_status_id,scm_sales_order.created_by,scm_sales_order.modified_by,scm_sales_order.created_at,scm_sales_order.modified_at,"
            + "scm_sales_order.approved_at,scm_sales_order.approved_by "
            + "from scm_sales_order scm_sales_order "); //Main query
    sql.count("select count(scm_sales_order.id) from scm_sales_order scm_sales_order "); //Count query
    sql.join("left outer join scm_sales_order_status scm_sales_order_status on (scm_sales_order_status.id = scm_sales_order.sales_order_status_id) "
            + "left outer join scm_customer scm_customer on (scm_customer.id = scm_sales_order.customer_id) "
            + "left outer join scm_account_group scm_account_group on (scm_account_group.id = scm_sales_order.account_group_id)"); //Join Query

    sql.string(new String[]{"scm_sales_order.sales_order_no", "scm_sales_order.created_by", "scm_sales_order.modified_by", "scm_sales_order.approved_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_order.id", "scm_sales_order.account_group_price_list_id", "scm_sales_order.discount_value_fixed", "scm_sales_order.discount_value_percentage", "scm_sales_order.sales_order_status_id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_order.sales_order_date", "scm_sales_order.created_at", "scm_sales_order.modified_at", "scm_sales_order.approved_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesOrder.
   *
   * @param main
   * @return List of SalesOrder
   */
  public static final List<SalesOrder> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesOrderSqlPaged(main));
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @param filter
   * @return
   */
  public static List<SalesOrder> listPagedByAccount(Main main, AccountGroup accountGroup, Integer filter) {
    SqlPage sql = getSalesOrderSqlPaged(main);
    main.clear();
    main.param(accountGroup.getId());
    if (filter != null) {
      main.param(filter);
      sql.cond("where scm_sales_order.account_group_id = ? and scm_sales_order.sales_order_status_id = ? ");
    } else {
      sql.cond("where scm_sales_order.account_group_id = ?");
    }

    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of SalesOrder based on condition
//   * @param main
//   * @return List<SalesOrder>
//   */
//  public static final List<SalesOrder> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesOrderSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesOrder by key.
   *
   * @param main
   * @param salesOrder
   * @return SalesOrder
   */
  public static final SalesOrder selectByPk(Main main, SalesOrder salesOrder) {
    return (SalesOrder) AppService.find(main, SalesOrder.class, salesOrder.getId());
  }

  /**
   * Insert SalesOrder.
   *
   * @param main
   * @param salesOrder
   */
  public static final void insert(Main main, SalesOrder salesOrder) {
    SalesOrderIs.insertAble(main, salesOrder);  //Validating
    AppService.insert(main, salesOrder);

  }

  /**
   * Update SalesOrder by key.
   *
   * @param main
   * @param salesOrder
   * @return SalesOrder
   */
  public static final SalesOrder updateByPk(Main main, SalesOrder salesOrder) {
    SalesOrderIs.updateAble(main, salesOrder); //Validating
    return (SalesOrder) AppService.update(main, salesOrder);
  }

  /**
   * Insert or update SalesOrder
   *
   * @param main
   * @param salesOrder
   */
  public static void insertOrUpdate(Main main, SalesOrder salesOrder) {
    if (salesOrder.getId() == null) {
      salesOrder.setSalesOrderNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesOrder.getAccountGroupId(), PrefixTypeService.SALES_ORDER_PREFIX_ID, true, salesOrder.getFinancialYearId()));
      insert(main, salesOrder);
      AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesOrder.getAccountGroupId(), PrefixTypeService.SALES_ORDER_PREFIX_ID, true, salesOrder.getFinancialYearId());
    } else {
      if (SystemConstants.CONFIRMED.equals(salesOrder.getSalesOrderStatusId().getId())) {
        salesOrder.setSalesOrderNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesOrder.getAccountGroupId(), PrefixTypeService.SALES_ORDER_PREFIX_ID, false, salesOrder.getFinancialYearId()));
        AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesOrder.getAccountGroupId(), PrefixTypeService.SALES_ORDER_PREFIX_ID, false, salesOrder.getFinancialYearId());
      }
      updateByPk(main, salesOrder);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesOrder
   */
  public static void clone(Main main, SalesOrder salesOrder) {
    salesOrder.setId(null); //Set to null for insert
    insert(main, salesOrder);
  }

  /**
   * Delete SalesOrder.
   *
   * @param main
   * @param salesOrder
   */
  public static final void deleteByPk(Main main, SalesOrder salesOrder) {
    SalesOrderIs.deleteAble(main, salesOrder); //Validation
    AppService.delete(main, SalesOrder.class, salesOrder.getId());
  }

  /**
   * Delete Array of SalesOrder.
   *
   * @param main
   * @param salesOrder
   */
  public static final void deleteByPkArray(Main main, SalesOrder[] salesOrder) {
    for (SalesOrder e : salesOrder) {
      deleteByPk(main, e);
    }
  }

  public static List<SalesOrder> selectBySOConsignment(Main main, Consignment consignment) {
    SqlPage sql = getSalesOrderSqlPaged(main);
    sql.join("left outer join scm_consignment_reference scm_consignment_reference on (scm_sales_order.id = scm_consignment_reference.sales_order_id)");
    if (consignment.getId() == null) {
      return null;
    }
    sql.cond("where scm_consignment_reference.consignment_id = ?");
    sql.param(consignment.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static final void updateSalesReqStatus(Main main, SalesOrder salesOrder) {
    String sql = "select distinct scm_sales_req.* from scm_sales_req_item scm_sales_req_item "
            + "inner join scm_sales_order_item scm_sales_order_item on scm_sales_order_item.sales_req_item_id = scm_sales_req_item.id "
            + "inner join scm_sales_order scm_sales_order on scm_sales_order.id = scm_sales_order_item.sales_order_id and scm_sales_order.id = ? "
            + "inner join scm_sales_req scm_sales_req on scm_sales_req.id = scm_sales_req_item.sales_req_id";
    //sql = "select * from scm_sales_req where id in (select sales_req_id from scm_sales_order_reference where sales_order_id = ?)";

    List<SalesReq> salesReqList = AppService.list(main, SalesReq.class, sql, new Object[]{salesOrder.getId()});
    main.em().flush();
    if (salesReqList != null) {
      for (SalesReq salesReq : salesReqList) {
        long count = AppService.count(main, "select sum((coalesce(tab1.qty_required,0) - coalesce(tab2.qty_required,0)) + (coalesce(tab1.qty_free_suggested,0) - coalesce(tab2.qty_free_suggested,0))) as qty_required "
                + "from (select * from scm_sales_req_item where sales_req_id = ?) tab1 "
                + "left outer join "
                + "(select sales_req_item_id, sum(coalesce(scm_sales_order_item.qty_required,0)) as qty_required, sum(coalesce(scm_sales_order_item.qty_free_suggested,0)) as qty_free_suggested "
                + "from scm_sales_order_item scm_sales_order_item "
                + "inner join scm_sales_order scm_sales_order on scm_sales_order.id = scm_sales_order_item.sales_order_id "
                + "where scm_sales_order.sales_order_status_id = 1 and scm_sales_order_item.sales_req_item_id in (select id from scm_sales_req_item where sales_req_id = ?) "
                + "group by scm_sales_order_item.sales_req_item_id) tab2 "
                + "on tab1.id = tab2.sales_req_item_id", new Object[]{salesReq.getId(), salesReq.getId()});

        if (count <= 0) {
          SalesReqService.updateSalesReqStatus(main, salesReq, SystemConstants.CONFIRMED_AND_PROCESSED);
        }
      }
    }
  }

  /**
   *
   * @param main
   * @param salesReqSelected
   * @return
   */
  public static final List<SalesOrderItem> selectSalesOrderItemBySalesReqArray(Main main, SalesReq[] salesReqSelected) {
    List<SalesOrderItem> salesReqList = null;
    StringBuilder salesReqIds = new StringBuilder("");

    for (SalesReq salesReq : salesReqSelected) {
      if (salesReqIds.length() == 0) {
        salesReqIds.append(salesReq.getId());
      } else {
        salesReqIds.append(",").append(salesReq.getId());
      }
    }

    if (salesReqIds.length() > 0) {
      //list = main.em().listJpql(SalesReqItem.class, "select s from SalesReqItem s where s.salesReqId in ?1 order by s.salesReqId ASC", new Object[]{Arrays.asList(salesReqSelected)});      
      salesReqList = AppService.list(main, SalesOrderItem.class, "select tab1.id,tab1.sales_req_item_id_hash_code, tab1.tax_code_id, tab1.product_id,tab1.value_prod_piece_selling, ((coalesce(tab1.qty_required,0)) - (coalesce(tab2.qty_required,0))) as qty_required from ( "
              + "select row_number() OVER () as id, string_agg(id::varchar, '#') as sales_req_item_id_hash_code, sum(qty_required) qty_required, "
              + "product_id,value_prod_piece_selling,tax_code_id "
              + "from scm_sales_req_item "
              + "where sales_req_id in (select * from unnest(array[" + salesReqIds + "])) group by product_id,value_prod_piece_selling,tax_code_id) tab1 "
              + "left outer join "
              + "(select scm_sales_order_item.product_id, sum(scm_sales_order_item.qty_required) qty_required "
              + "from scm_sales_order_item scm_sales_order_item "
              + "where scm_sales_order_item.sales_req_item_id in ("
              + "select id from scm_sales_req_item where sales_req_id in (select id from scm_sales_req where id in (select * from unnest(array[" + salesReqIds + "]))))"
              + "group by scm_sales_order_item.product_id) tab2 "
              + "on tab1.product_id = tab2.product_id", null);

    }
    return salesReqList;
  }

  public static boolean isRequestedProductProcessed(Main main, SalesOrder salesOrder) {
    long count = AppService.count(main, "select sum((coalesce(tab1.qty_required,0) - coalesce(tab2.product_qty,0)) + (coalesce(tab1.qty_free_suggested,0) - coalesce(tab2.product_qty_free,0))) as qty_required "
            + "from (select * from scm_sales_order_item where sales_order_id = ?) tab1 "
            + "left outer join "
            + "(select sales_order_item_id, sum(coalesce(scm_sales_invoice_item.product_qty,0)) as product_qty, sum(coalesce(scm_sales_invoice_item.product_qty_free,0)) as product_qty_free "
            + "from scm_sales_invoice_item scm_sales_invoice_item "
            + "inner join scm_sales_invoice scm_sales_invoice on scm_sales_invoice.id = scm_sales_invoice_item.sales_invoice_id "
            + "where scm_sales_invoice_item.sales_order_item_id in (select id from scm_sales_order_item where sales_order_id = ?)"
            + "group by scm_sales_invoice_item.sales_order_item_id) tab2 "
            + "on tab1.id = tab2.sales_order_item_id", new Object[]{salesOrder.getId(), salesOrder.getId()});
    return (count <= 0);
  }

  public static void mergeSalesOrderToSalesInvoice(Main main, SalesOrder salesOrder, List<SalesOrderItem> salesOrderItemList, TaxCalculator taxCalculator) {

    int counter = 0;
    SalesInvoice salesInvoice = null;
    Integer productQty;
    Integer productFreeQty;
    AccountGroupPriceList accountGroupPriceList = null;
    if (salesOrderItemList != null && salesOrderItemList.size() > 0) {

      if (salesOrder.getAccountGroupId() != null) {
        /**
         * Note : Here account group price list may change, can consider it from sales account.
         */
        accountGroupPriceList = (AccountGroupPriceList) AppService.single(main, AccountGroupPriceList.class,
                "select * from scm_account_group_price_list where account_group_id = ? and is_default = 1", new Object[]{salesOrder.getAccountGroupId().getId()});
      }

      for (SalesOrderItem salesOrderItem : salesOrderItemList) {
        if (counter == 0) {
          salesInvoice = convertSalesOrderToSalesInvoice(salesOrder, salesOrderItem);
          salesInvoice.setAccountGroupPriceListId(accountGroupPriceList);
          SalesInvoiceService.insert(main, salesInvoice);
          main.em().flush();
          counter++;
        }

        List<ProductDetailSales> productDetailList = SalesInvoiceItemService.getProductCurrentStockAvailability(main, salesInvoice.getAccountGroupId(), salesOrderItem.getProductId(), salesInvoice.getAccountGroupPriceListId());

        productQty = salesOrderItem.getQtyRequired() == null ? 0 : salesOrderItem.getQtyRequired();
        productFreeQty = salesOrderItem.getQtyFreeSuggested() == null ? 0 : salesOrderItem.getQtyFreeSuggested();
        //SalesInvoiceService.convertSalesOrderItemToSalesInvoiceItem(main, productDetailList, salesInvoice, salesOrderItem, productQty, productFreeQty);
        SalesInvoiceService.convertSalesOrderItemToSalesInvoiceItem(main, productDetailList, salesInvoice, salesOrderItem);

      }

      main.em().flush();
      String invoiceNo = AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesInvoice.getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, true, salesOrder.getFinancialYearId());
      salesInvoice.setInvoiceNo(invoiceNo);
      salesInvoice.setInvoiceDate(new Date());
      List<SalesInvoiceItem> salesInvoiceItemList = SalesInvoiceItemService.selectSalesInvoiceItemBySalesInvoice(main, salesInvoice, taxCalculator, false);
      taxCalculator.processSalesInvoiceCalculation(salesInvoice, salesInvoiceItemList, true);
      SalesInvoiceService.insertOrUpdate(main, salesInvoice);
      AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesInvoice.getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, true, salesOrder.getFinancialYearId());
    }
  }

  private static SalesInvoice convertSalesOrderToSalesInvoice(SalesOrder salesOrder, SalesOrderItem salesOrderItem) {
    SalesInvoice salesInvoice = new SalesInvoice();
    salesInvoice.setAccountGroupId(salesOrder.getAccountGroupId());
    salesInvoice.setCustomerId(salesOrder.getCustomerId());
    //salesInvoice.setProductTypeId(salesOrderItem.getProductId().getProductTypeId());
    salesInvoice.setSalesOrderId(salesOrder);
    SalesInvoiceStatus status = new SalesInvoiceStatus();
    status.setId(SystemConstants.DRAFT);
    salesInvoice.setSalesInvoiceStatusId(status);
    salesInvoice.setTaxProcessorId(salesOrder.getCustomerId().getCompanyId().getCountryTaxRegimeId().getTaxProcessorId());
    return salesInvoice;
  }
}
