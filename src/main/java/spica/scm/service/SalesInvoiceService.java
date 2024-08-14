/*
 * @(#)SalesInvoiceService.java	1.0 Fri Dec 23 10:28:18 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.service.LedgerExternalDataService;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Company;
import spica.scm.domain.Platform;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesInvoiceStatus;

import spica.scm.domain.SalesOrder;
import spica.scm.domain.SalesOrderItem;
import spica.scm.domain.StockPreSale;
import spica.scm.domain.StockSaleable;
import spica.fin.domain.TaxCode;
import spica.scm.common.InvoiceItem;
import spica.scm.common.ProductInvoiceDetail;
import spica.scm.common.RelatedItems;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Customer;
import spica.scm.domain.District;
import spica.scm.domain.ProductFreeQtyScheme;
import spica.scm.domain.SalesAccount;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.AppUtil;
import spica.scm.util.ReferenceInvoice;
import spica.scm.util.SalesInvoiceItemUtil;
import spica.scm.util.SalesInvoiceUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.util.StringUtil;

/**
 * SalesInvoiceService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:18 IST 2016
 */
public abstract class SalesInvoiceService {

  /**
   * SalesInvoice paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesInvoiceSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_invoice", SalesInvoice.class, main);
    sql.main("select scm_sales_invoice.id,scm_sales_invoice.invoice_entry_date,scm_sales_invoice.invoice_amount,scm_sales_invoice.note,"
            + "scm_sales_invoice.note,scm_sales_invoice.tcs_net_value,"
            //  + "scm_sales_invoice.invoice_amount_net,"
            // + "scm_sales_invoice.invoice_amt_disc_percent,scm_sales_invoice.freight_rate,scm_sales_invoice.sales_credit_days,scm_sales_invoice.note,"
            + "scm_sales_invoice.invoice_amount_igst,scm_sales_invoice.sales_invoice_status_id,scm_sales_invoice.created_by,scm_sales_invoice.modified_by,scm_sales_invoice.created_at,scm_sales_invoice.modified_at,"
            + "scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_date,scm_sales_invoice.customer_id "
            //  + "scm_sales_invoice.invoice_amount_assessable,scm_sales_invoice.round_off,scm_sales_invoice.round_off_forced,scm_sales_invoice.invoice_amount_vat,scm_sales_invoice.invoice_amount_discount,"
            // + "scm_sales_invoice.sales_order_id,"
            // + "scm_sales_invoice.account_group_id,scm_sales_invoice.account_group_price_list_id,scm_sales_invoice.is_sales_interstate,"
            //  + "scm_sales_invoice.company_sales_agent_person_profile_id,scm_sales_invoice.company_sales_agent_commission_value_forced,scm_sales_invoice.company_sales_agent_commission_percentage_forced,"
            //  + "scm_sales_invoice.company_sales_agent_commission_value_actual,scm_sales_invoice.company_sales_agent_commission_percentage_actual,"
            //  + "scm_sales_invoice.tax_code_id,scm_sales_invoice.vendor_sales_agent_person_profile_id,scm_sales_invoice.parent_id,scm_sales_invoice.is_tax_code_modified,"
            // + "scm_sales_invoice.proforma_created_at,scm_sales_invoice.proforma_expiring_at,scm_sales_invoice.company_address_id,scm_sales_invoice.customer_address_id,"
            //  + "scm_sales_invoice.commission_applicable_on,scm_sales_invoice.invoice_amount_gst,scm_sales_invoice.invoice_amount_cgst,scm_sales_invoice.tax_processor_id,"
            //   + "scm_sales_invoice.invoice_amount_sgst,scm_sales_invoice.cash_discount_applicable,scm_sales_invoice.cash_discount_taxable,scm_sales_invoice.cash_discount_value,"
            // + "scm_sales_invoice.no_of_boxes,scm_sales_invoice.weight,scm_sales_invoice.weight_unit,scm_sales_invoice.invoice_amount_goods "
            + "from scm_sales_invoice scm_sales_invoice "); //Main query
    sql.count("select count(scm_sales_invoice.id) as total from scm_sales_invoice scm_sales_invoice "); //Count query
    sql.join(" "
            + "left outer join scm_sales_invoice_status scm_sales_invoice_status on (scm_sales_invoice_status.id = scm_sales_invoice.sales_invoice_status_id) "
            + "left outer join scm_customer scm_customer on (scm_customer.id = scm_sales_invoice.customer_id) "
            + "left outer join scm_sales_order scm_sales_order on (scm_sales_order.id = scm_sales_invoice.sales_order_id) "
    //   + "left outer join scm_tax_code scm_tax_code on (scm_tax_code.id = scm_sales_invoice.tax_code_id) "
    //  + "left outer join scm_account_group scm_account_group on (scm_account_group.id = scm_sales_invoice.account_group_id) "
    //   + "left outer join scm_sales_invoice scm_sales_invoice_parent on (scm_sales_invoice_parent.id = scm_sales_invoice.parent_id) "
    //   + "left outer join scm_account_group_price_list scm_account_group_price_list on (scm_account_group_price_list.id = scm_sales_invoice.account_group_price_list_id)" //Join Query
    //   + "left outer join scm_company_address scm_company_address on(scm_company_address.id = scm_sales_invoice.company_address_id)"
    //  + "left outer join scm_customer_address scm_customer_address on(scm_customer_address.id = scm_sales_invoice.customer_address_id)"
    );

    sql.string(new String[]{"scm_sales_invoice.note", "scm_sales_invoice.invoice_no", "scm_sales_invoice_status.title", "scm_customer.customer_name", "scm_customer.gst_no"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_invoice.invoice_amount", "scm_sales_invoice.invoice_amount_net", "scm_sales_invoice.tcs_net_value"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_invoice.invoice_date", "scm_sales_invoice.invoice_entry_date"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesInvoice.
   *
   * @param main
   * @param accountGroup
   * @param statusFilter
   * @return List of SalesInvoice
   */
  public static final List<SalesInvoice> listPaged(Main main, Company company, AccountGroup accountGroup, Integer statusFilter, Customer customer, AccountingFinancialYear accountingFinancialYear) {
    SqlPage sql = getSalesInvoiceSqlPaged(main);
    String query = null;
    if (statusFilter != null) {
      main.clear();
      main.param(company.getId());
      main.param(accountGroup.getId());

      if (statusFilter == 3) {
        main.param(SystemConstants.SALES_INVOICE_TYPE_PROFORMA);
        query = "where scm_sales_invoice.company_id = ? and scm_sales_invoice.account_group_id = ? and scm_sales_invoice.sales_invoice_type = ?";
      } else {
        main.param(statusFilter);
        query = "where scm_sales_invoice.company_id = ? and scm_sales_invoice.account_group_id = ? and scm_sales_invoice.sales_invoice_status_id = ?";
      }
    } else {
      main.clear();
      main.param(company.getId());
      main.param(accountGroup.getId());
      query = "where scm_sales_invoice.company_id = ? and scm_sales_invoice.account_group_id = ? and scm_sales_invoice.sales_invoice_status_id in (" + SystemConstants.CONFIRMED + "," + SystemConstants.DRAFT + "," + SystemConstants.CONFIRMED_AND_PACKED + "," + SystemConstants.EXPIRED + "," + SystemConstants.CANCELLED + ")";
    }

    if (customer != null && customer.getId() != null) {
      query += " AND scm_sales_invoice.customer_id = ? ";
      main.param(customer.getId());
    }
    if (accountingFinancialYear != null && accountingFinancialYear.getId() != null) {
      query += " AND scm_sales_invoice.invoice_entry_date::date >= ?::date AND scm_sales_invoice.invoice_entry_date::date <= ?::date ";
      main.param(accountingFinancialYear.getStartDate());
      main.param(accountingFinancialYear.getEndDate());
    }

    sql.cond(query);
    sql.orderBy("invoice_entry_date desc,invoice_no desc");
    return AppService.listPagedJpa(main, sql);
  }

  /**
   * Select SalesInvoice by key.
   *
   * @param main
   * @param salesInvoice
   * @return SalesInvoice
   */
  public static final SalesInvoice selectByPk(Main main, SalesInvoice salesInvoice) {
    main.clear();
    SalesInvoice inv = (SalesInvoice) AppService.single(main, SalesInvoice.class, "select * from scm_sales_invoice where id = ? ", new Object[]{salesInvoice.getId()});
    return inv;
    // return (SalesInvoice) AppService.find(main, SalesInvoice.class, salesInvoice.getId());
  }

  /**
   * Insert SalesInvoice.
   *
   * @param main
   * @param salesInvoice
   */
  public static final void insert(Main main, SalesInvoice salesInvoice) {
//    SalesInvoiceIs.insertAble(main, salesInvoice);  //Validating
    AppService.insert(main, salesInvoice);

  }

  /**
   * Update SalesInvoice by key.
   *
   * @param main
   * @param salesInvoice
   * @return SalesInvoice
   */
  public static final SalesInvoice updateByPk(Main main, SalesInvoice salesInvoice) {
//    SalesInvoiceIs.updateAble(main, salesInvoice); //Validating
    return (SalesInvoice) AppService.update(main, salesInvoice);
  }

  /**
   * Insert or update SalesInvoice
   *
   * @param main
   * @param salesInvoice
   */
  public static void insertOrUpdate(Main main, SalesInvoice salesInvoice) {
    if (salesInvoice.getId() == null) {
      salesInvoice.setInvoiceNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesInvoice.getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, true, salesInvoice.getFinancialYearId()) + "-DR");
      insert(main, salesInvoice);
      AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesInvoice.getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, true, salesInvoice.getFinancialYearId());
    } else {
      if (SystemConstants.CONFIRMED.equals(salesInvoice.getSalesInvoiceStatusId().getId())) {
        SalesInvoiceUtil.setSalesInvoiceAddress(salesInvoice);
        if (salesInvoice.getConfirmedAt() == null || (salesInvoice.getSalesInvoiceType() != null && salesInvoice.getSalesInvoiceType().intValue() == 2 && salesInvoice.getInvoiceNo().startsWith("PF"))) {
          salesInvoice.setInvoiceNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesInvoice.getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, false, salesInvoice.getFinancialYearId()));
          AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesInvoice.getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, false, salesInvoice.getFinancialYearId());
        }
        salesInvoice.setConfirmedAt(new Date());
        salesInvoice.setConfirmedBy(main.getAppUser().getLogin());
      }
      updateByPk(main, salesInvoice);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesInvoice
   */
  public static void clone(Main main, SalesInvoice salesInvoice) {
    salesInvoice.setId(null); //Set to null for insert
    insert(main, salesInvoice);
  }

  /**
   * Delete SalesInvoice.
   *
   * @param main
   * @param salesInvoice
   */
  public static final void deleteByPk(Main main, SalesInvoice salesInvoice) {
    SalesInvoiceLogService.createSalesInvoiceLogDelete(main, salesInvoice);
//    SalesInvoiceIs.deleteAble(main, salesInvoice); //Validation
    AppService.deleteSql(main, StockPreSale.class, "delete from scm_stock_presale where sales_invoice_id=?", new Object[]{salesInvoice.getId()});
    deleteReference(main, salesInvoice);
    // AppService.deleteSql(main, StockPreSale.class, "delete from scm_stock_presale where sales_invoice_id=?", new Object[]{salesInvoice.getId()});
    referenceErrorMessages(main, salesInvoice.getId(), salesInvoice.getInvoiceNo(), SystemConstants.DEBIT_CREDIT_NOTE);
    AppService.deleteSql(main, SalesInvoice.class, "delete from scm_sales_invoice_item where sales_invoice_id=?", new Object[]{salesInvoice.getId()});
    AppService.delete(main, SalesInvoice.class, salesInvoice.getId());
  }

  /**
   * Delete Array of SalesInvoice.
   *
   * @param main
   * @param salesInvoice
   */
//  public static final void deleteByPkArray(Main main, SalesInvoice[] salesInvoice) {
//    for (SalesInvoice e : salesInvoice) {
//      deleteByPk(main, e);
//    }
//  }
  /**
   *
   * @param main
   * @param salesInvoiceItem
   * @return
   */
  public static Integer selectProductFreeQuantity(Main main, SalesInvoiceItem salesInvoiceItem) {
    /**
     * Sql Function parameters (1) - ProductFreeQtyScheme - id (2) - TertairyToSecondaryQty (3) - SecondaryToPrimaryQty (4) - PrimaryQuantity
     *
     * Will return available free quantity for given primary quantity.
     */
    main.clear();
    main.param(salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId());
    main.param(salesInvoiceItem.getProductDetailSales().getTerToSecQuantity());
    main.param(salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
    main.param(salesInvoiceItem.getProductQty());
    String freeQty = AppService.firstString(main, "SELECT * FROM getAllowedFreeQuantityAsPerScheme(?,?,?,?)");

    return freeQty == null ? 0 : Integer.parseInt(freeQty);
  }

  /**
   * Method to extends sales invoice pro-forma expiry date.
   *
   * @param main
   * @param salesInvoice
   */
  public static final void updateProformaExpiryDate(Main main, SalesInvoice salesInvoice) {
    main.clear();
    main.param(salesInvoice.getProformaExpiringAt());
    main.param(salesInvoice.getId());
    AppService.updateSql(main, SalesInvoice.class, "update scm_sales_invoice set proforma_expiring_at = ? where id = ?", false);
    main.clear();
  }

  /**
   * Method to update sales invoice status.
   *
   * @param main
   * @param salesInvoice
   * @param salesInvoiceStatusId
   */
  public static final void updateSalesInvoiceStatus(Main main, SalesInvoice salesInvoice, Integer salesInvoiceStatusId) {
    main.clear();
    main.param(salesInvoiceStatusId);
    main.param(salesInvoice.getId());
    AppService.updateSql(main, SalesInvoice.class, "update scm_sales_invoice set sales_invoice_status_id = ? where id = ?", false);
    main.clear();
  }

  public static void updateSalesInvoicePrinted(Main main, SalesInvoice salesInvoice) {
    main.clear();
    main.param(new Date());
    main.param(main.getAppUser().getLogin());
    main.param(salesInvoice.getId());
    AppService.updateSql(main, SalesInvoice.class, "update scm_sales_invoice set is_invoice_printed = (is_invoice_printed + 1), invoice_printed_at = ?, invoice_printed_by = ? where id = ?", false);
    main.clear();
  }

  /**
   *
   * @param main
   * @param salesInvoice
   * @return
   */
  public static final boolean isSalesInvoiceEditable(Main main, SalesInvoice salesInvoice) {
    if (salesInvoice.getId() != null) {
      SalesInvoice invoice = selectByPk(main, salesInvoice);
      if (invoice.getModifiedAt() == null) {
        return true;
      } else if (invoice.getModifiedAt() != null && salesInvoice.getModifiedAt() == null) {
        return false;
      } else {
        return isPastModifiedDate(invoice.getModifiedAt(), salesInvoice.getModifiedAt());
      }
    } else {
      return true;
    }
  }

  /**
   *
   * @param currentDate
   * @param modifiedDate
   * @return
   */
  private static boolean isPastModifiedDate(Date currentDate, Date modifiedDate) {
    return !currentDate.after(modifiedDate);
  }

  /**
   * Method to merge sales requisition items to sales invoice.
   *
   * @param main
   * @param salesOrder
   */
  public static void mergeSalesOrderToSalesInvoice(Main main, SalesOrder salesOrder) {
    List<SalesOrderItem> salesOrderItemGeneralList;
    List<SalesOrderItem> salesOrderItemColdChainList;

    /**
     * get the list of tax code.
     */
    List<TaxCode> taxCodeList = AppService.list(main, TaxCode.class, "select * from scm_tax_code where id in(select tax_code_id from scm_sales_order_item where sales_order_id = ?)", new Object[]{salesOrder.getId()});

    for (TaxCode taxCode : taxCodeList) {
      salesOrderItemGeneralList = AppService.list(main, SalesOrderItem.class, "select row_number() OVER () as id, string_agg(scm_sales_order_item.id::varchar, '#') as sales_order_item_id_hash_code,"
              + "scm_sales_order_item.sales_order_id, scm_sales_order_item.product_id,scm_sales_order_item.value_prod_piece_selling_forced,scm_sales_order_item.value_prod_piece_selling, "
              + "scm_sales_order_item.discount_value_fixed, scm_sales_order_item.tax_code_id, "
              + "(sum(scm_sales_order_item.qty_required) - (sum(coalesce(scm_sales_invoice_item.product_qty,0)))) as qty_required, "
              + "(sum(scm_sales_order_item.qty_free_suggested) - (sum(coalesce(scm_sales_invoice_item.product_qty_free,0)))) as qty_free_suggested "
              + "from scm_sales_order_item scm_sales_order_item "
              + "inner join scm_product scm_product on scm_product.id = scm_sales_order_item.product_id "
              + "inner join scm_product_type scm_product_type on scm_product_type.id = scm_product.product_type_id "
              + "inner join scm_tax_code scm_tax_code on scm_tax_code.id = scm_sales_order_item.tax_code_id "
              + "left join scm_sales_invoice_item scm_sales_invoice_item on scm_sales_invoice_item.sales_order_item_id = scm_sales_order_item.id "
              + "where scm_sales_order_item.tax_code_id = ? and scm_sales_order_item.sales_order_id = ? and scm_product_type.product_type_code = ? "
              + "group by scm_sales_order_item.product_id, scm_sales_order_item.sales_order_id,scm_sales_order_item.value_prod_piece_selling_forced,scm_sales_order_item.value_prod_piece_selling, "
              + "scm_sales_order_item.discount_value_fixed, scm_sales_order_item.tax_code_id "
              + "having (sum(scm_sales_order_item.qty_required) - sum(coalesce(scm_sales_invoice_item.product_qty,0))) > 0 "
              + "or (sum(scm_sales_order_item.qty_free_suggested) - (sum(coalesce(scm_sales_invoice_item.product_qty_free,0)))) > 0 ",
              new Object[]{taxCode.getId(), salesOrder.getId(), SystemConstants.PRODUCT_TYPE_GENERAL});

      salesOrderItemColdChainList = AppService.list(main, SalesOrderItem.class, "select row_number() OVER () as id, string_agg(scm_sales_order_item.id::varchar, '#') as sales_order_item_id_hash_code,"
              + "scm_sales_order_item.sales_order_id, scm_sales_order_item.product_id,scm_sales_order_item.value_prod_piece_selling_forced,scm_sales_order_item.value_prod_piece_selling, "
              + "scm_sales_order_item.discount_value_fixed, scm_sales_order_item.tax_code_id, "
              + "(sum(scm_sales_order_item.qty_required) - (sum(coalesce(scm_sales_invoice_item.product_qty,0)))) as qty_required, "
              + "(sum(scm_sales_order_item.qty_free_suggested) - (sum(coalesce(scm_sales_invoice_item.product_qty_free,0)))) as qty_free_suggested "
              + "from scm_sales_order_item scm_sales_order_item "
              + "inner join scm_product scm_product on scm_product.id = scm_sales_order_item.product_id "
              + "inner join scm_product_type scm_product_type on scm_product_type.id = scm_product.product_type_id "
              + "inner join scm_tax_code scm_tax_code on scm_tax_code.id = scm_sales_order_item.tax_code_id "
              + "left join scm_sales_invoice_item scm_sales_invoice_item on scm_sales_invoice_item.sales_order_item_id = scm_sales_order_item.id "
              + "where scm_sales_order_item.tax_code_id = ? and scm_sales_order_item.sales_order_id = ? and scm_product_type.product_type_code = ? "
              + "group by scm_sales_order_item.product_id, scm_sales_order_item.sales_order_id,scm_sales_order_item.value_prod_piece_selling_forced,scm_sales_order_item.value_prod_piece_selling, "
              + "scm_sales_order_item.discount_value_fixed, scm_sales_order_item.tax_code_id "
              + "having (sum(scm_sales_order_item.qty_required) - sum(coalesce(scm_sales_invoice_item.product_qty,0))) > 0 "
              + "or (sum(scm_sales_order_item.qty_free_suggested) - (sum(coalesce(scm_sales_invoice_item.product_qty_free,0)))) > 0 ",
              new Object[]{taxCode.getId(), salesOrder.getId(), SystemConstants.PRODUCT_TYPE_COLD_CHAIN});

      createSalesInvoice(main, salesOrderItemGeneralList, salesOrder);
      createSalesInvoice(main, salesOrderItemColdChainList, salesOrder);

    }
  }

  /**
   * Method to create sales invoice from sales order.
   *
   * @param main
   * @param salesOrderItemList
   * @param salesReq
   */
  private static void createSalesInvoice(Main main, List<SalesOrderItem> salesOrderItemList, SalesOrder salesOrder) {
    int counter = 0;
    SalesInvoice salesInvoice = null;
    Integer productQty;
    Integer productFreeQty;
    String[] salesOrderItemIds;
    Integer salesOrderItemId;
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

        salesOrderItemIds = salesOrderItem.getSalesOrderItemIdHashCode().split("#");

        if (counter == 0) {
          salesInvoice = convertSalesOrderToSalesInvoice(salesOrder, salesOrderItem);
          salesInvoice.setAccountGroupPriceListId(accountGroupPriceList);
          SalesInvoiceService.insert(main, salesInvoice);
          main.em().flush();
        }

        List<ProductDetailSales> productDetailList = SalesInvoiceItemService.getProductCurrentStockAvailability(main, salesInvoice.getAccountGroupId(), salesOrderItem.getProductId(), salesInvoice.getAccountGroupPriceListId());

        productQty = salesOrderItem.getQtyRequired() == null ? 0 : salesOrderItem.getQtyRequired();
        productFreeQty = salesOrderItem.getQtyFreeSuggested() == null ? 0 : salesOrderItem.getQtyFreeSuggested();

        if (salesOrderItemIds.length > 1) {
          for (String orderId : salesOrderItemIds) {
            salesOrderItemId = Integer.parseInt(orderId);
            SalesOrderItem orderItem = new SalesOrderItem();
            orderItem.setId(salesOrderItemId);
            convertSalesOrderItemToSalesInvoiceItem(main, productDetailList, salesInvoice, orderItem, productQty, productFreeQty);
          }
        } else {
          salesOrderItemId = Integer.parseInt(salesOrderItemIds[0]);
          SalesOrderItem orderItem = new SalesOrderItem();
          orderItem.setId(salesOrderItemId);
          convertSalesOrderItemToSalesInvoiceItem(main, productDetailList, salesInvoice, orderItem, productQty, productFreeQty);
        }
        counter++;
      }

      main.em().flush();
      String invoiceNo = AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesInvoice.getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, true, salesInvoice.getFinancialYearId());
      salesInvoice.setInvoiceNo(invoiceNo);
      salesInvoice.setInvoiceDate(new Date());
      //List<SalesInvoiceItem> salesInvoiceItemList = SalesInvoiceItemService.selectSalesInvoiceItemBySalesInvoice(main, salesInvoice, null);
      //SalesSummery salesSummery = SalesInvoiceUtil.calculateSalesInvoice(salesInvoice, salesInvoiceItemList);
      //SalesSummery salesSummery = null;
      //salesInvoice = salesSummery.getSalesInvoice();
      SalesInvoiceService.insertOrUpdate(main, salesInvoice);
      AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesInvoice.getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, true, salesInvoice.getFinancialYearId());
    }
  }

  /**
   *
   * @param main
   * @param productDetailList
   * @param salesInvoice
   * @param salesOrderItem
   * @param productQty
   * @param productFreeQty
   */
  public static final void convertSalesOrderItemToSalesInvoiceItem(Main main, List<ProductDetailSales> productDetailList, SalesInvoice salesInvoice, SalesOrderItem salesOrderItem, Integer productQty, Integer productFreeQty) {
    SalesInvoiceItem salesInvoiceItem = null;
    StockPreSale stockPreSale = null;
    StockPreSale stockPreSaleToStockOut = null;
    StockPreSale stockPreSaleConvertIn = null;
    StockPreSale stockPreSaleConvertOut = null;
    StockPreSale stockPreSaleToSaleable = null;
    Integer stockId = null;
    Integer freeStockId = null;
    String[] stockIds;

    for (ProductDetailSales productDetailSales : productDetailList) {

      stockIds = productDetailSales.getStockSaleableId().split("#");
      if (stockIds.length > 1) {
        stockId = Integer.parseInt(stockIds[0]);
        freeStockId = Integer.parseInt(stockIds[1]);
      } else {
        stockId = Integer.parseInt(stockIds[0]);
      }

      if (productQty > 0) {
        if (productDetailSales.getQuantityAvailable() != null && productDetailSales.getQuantityAvailable() > 0) {
          salesInvoiceItem = SalesInvoiceItemUtil.mergeSalesOrderItemToSalesInvoiceItem(productQty, productDetailSales);
          productQty -= salesInvoiceItem.getProductQty();
          stockPreSale = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, stockId, StockStatusService.STOCK_STATUS_SALEABLE, SystemConstants.STOCK_TYPE_SALEABLE);
        }
      }

      /**
       * if quantity not satisfied program will check for free quantity is available or not. if free quantity available program will convert the free quantity to saleable quantity.
       */
      if (productQty > 0 && productDetailSales.getQuantityFreeAvailable() != null && productDetailSales.getQuantityFreeAvailable() > 0) {
        stockPreSaleToSaleable = SalesInvoiceItemUtil.getFreeToSaleable(productQty, salesInvoice, salesInvoiceItem, productDetailSales, freeStockId, SystemConstants.STOCK_TYPE_FREE);
        stockPreSaleConvertIn = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToSaleable, SystemConstants.STOCK_SALEABLE_AS_FREE_IN, SystemConstants.STOCK_TYPE_SALEABLE);
        stockPreSaleConvertOut = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToSaleable, SystemConstants.STOCK_SALEABLE_AS_FREE_OUT, SystemConstants.STOCK_TYPE_SALEABLE);

        productQty -= stockPreSaleToSaleable.getQuantityIn();
        salesInvoiceItem.setProductQty((salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty()) + stockPreSaleToSaleable.getQuantityIn());
      }

      if (productFreeQty > 0) {
        if (productDetailSales.getQuantityFreeAvailable() != null && productDetailSales.getQuantityFreeAvailable() > 0) {
          salesInvoiceItem = SalesInvoiceItemUtil.mergeSalesOrderItemFreeQty(salesInvoiceItem, productFreeQty, productDetailSales);
          productFreeQty -= salesInvoiceItem.getProductQtyFree();
          if (stockPreSale == null) {
            stockPreSale = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, freeStockId, StockStatusService.STOCK_STATUS_SALEABLE, SystemConstants.STOCK_TYPE_SALEABLE);
          }
        }
      }

      if (productFreeQty > 0 && productDetailSales.getQuantityAvailable() != null && productDetailSales.getQuantityAvailable() > 0) {
        stockPreSaleToStockOut = SalesInvoiceItemUtil.getSaleableToFree(productFreeQty, salesInvoice, salesInvoiceItem, productDetailSales, stockId, SystemConstants.STOCK_TYPE_SALEABLE);
        stockPreSaleConvertIn = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToStockOut, SystemConstants.STOCK_SALEABLE_AS_FREE_IN, SystemConstants.STOCK_TYPE_FREE);
        stockPreSaleConvertOut = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToStockOut, SystemConstants.STOCK_SALEABLE_AS_FREE_OUT, SystemConstants.STOCK_TYPE_FREE);

        salesInvoiceItem.setProductQtyFree((salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree()) + stockPreSaleToStockOut.getQuantityOut());
        productFreeQty -= stockPreSaleToStockOut.getQuantityOut();
      }

      salesInvoiceItem.setSalesInvoiceId(salesInvoice);
      salesInvoiceItem.setSalesOrderItemId(salesOrderItem);
      SalesInvoiceItemService.insert(main, salesInvoiceItem);

      insertStockPreSale(main, stockPreSale);
      insertStockPreSale(main, stockPreSaleToSaleable);
      insertStockPreSale(main, stockPreSaleToStockOut);
      insertStockPreSale(main, stockPreSaleConvertIn);
      insertStockPreSale(main, stockPreSaleConvertOut);

      if (productQty == 0 && productFreeQty == 0) {
        break;
      }
    }
  }

  /**
   *
   * @param main
   * @param productDetailList
   * @param salesInvoice
   * @param salesOrderItem
   */
  public static final void convertSalesOrderItemToSalesInvoiceItem(Main main, List<ProductDetailSales> productDetailSalesList, SalesInvoice salesInvoice, SalesOrderItem salesOrderItem) {

    String[] salesInvoiceItemIds;
    SalesInvoiceItem salesInvoiceItem = null;
    StockPreSale stockPreSaleOut = null;
    StockPreSale stockPreSaleFreeOut = null;
    StockPreSale stockPreSaleToStockOut = null;
    StockPreSale stockPreSaleConvertIn = null;
    StockPreSale stockPreSaleConvertOut = null;
    StockPreSale stockPreSaleToSaleable = null;

    Integer stockId = null;
    Integer freeStockId = null;
    String[] stockIds;
    Integer productQty = null;
    Integer productFreeQty = null;
    Integer productFreeQuantityIn = 0;
    boolean schemeApplicable = false;
    ProductDetail productDetail = null;

//    productFreeQty = salesOrderItem.getQtyFreeSuggested() == null ? 0 : salesOrderItem.getQtyFreeSuggested();
//      if (salesInvoiceItemId.getProductQtyFree() != null && salesInvoiceItemId.getProductQtyFree() > 0) {
//        productQty += salesInvoiceItemId.getProductQtyFree();
//        productFreeQuantityIn = salesInvoiceItemId.getProductQtyFree();
//      }
//    } else {
//      productQty = salesInvoiceItemId.getProductQty() == null ? 0 : salesInvoiceItemId.getProductQty();
//      productFreeQty = salesInvoiceItemId.getProductQtyFree() == null ? 0 : salesInvoiceItemId.getProductQtyFree();
//    }
    for (ProductDetailSales productDetailSales : productDetailSalesList) {
      productDetail = ProductDetailService.selectByPk(main, new ProductDetail(productDetailSales.getProductDetailId()));
      schemeApplicable = SystemRuntimeConfig.isSchemeApplicable(productDetail.getAccountId());

      stockIds = productDetailSales.getStockSaleableId().split("#");
      if (stockIds.length > 1) {
        if (productFreeQty == null) {
          productFreeQty = salesOrderItem.getQtyFreeSuggested() == null ? 0 : salesOrderItem.getQtyFreeSuggested();
        }
        if (productQty == null) {
          productQty = (salesOrderItem.getQtyRequired() == null ? 0 : salesOrderItem.getQtyRequired());
        }
        stockId = Integer.parseInt(stockIds[0]);
        freeStockId = Integer.parseInt(stockIds[1]);
      } else {
        stockId = Integer.parseInt(stockIds[0]);
        if (!schemeApplicable) {
          if (productQty == null) {
            productFreeQuantityIn = salesOrderItem.getQtyFreeSuggested() == null ? 0 : salesOrderItem.getQtyFreeSuggested();
            productQty = (salesOrderItem.getQtyRequired() == null ? 0 : salesOrderItem.getQtyRequired()) + productFreeQuantityIn;
            productFreeQty = 0;
          }
        } else {
          if (productQty == null) {
            productQty = (salesOrderItem.getQtyRequired() == null ? 0 : salesOrderItem.getQtyRequired());
            productFreeQty = salesOrderItem.getQtyFreeSuggested() == null ? 0 : salesOrderItem.getQtyFreeSuggested();;
            productFreeQuantityIn = 0;
          }
        }
      }

      salesInvoiceItem = SalesInvoiceItemUtil.getSalesInvoiceItem(productDetailSales);

      if (productDetailSales.getQuantityAvailable() != null && productDetailSales.getQuantityAvailable() > 0 && productQty > 0) {
        if (productQty <= productDetailSales.getQuantityAvailable()) {
          salesInvoiceItem.setProductQty(productQty - productFreeQuantityIn);
          salesInvoiceItem.setProductDetailId(productDetail);

          if (productFreeQuantityIn > 0) {
            salesInvoiceItem.setProductQtyFree(productFreeQuantityIn);
          }

          stockPreSaleOut = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, stockId, StockStatusService.STOCK_STATUS_SALEABLE, SystemConstants.STOCK_TYPE_SALEABLE);
          stockPreSaleOut.setQuantityOut(salesInvoiceItem.getProductQty() + productFreeQuantityIn);

          productDetailSales.setQuantityAvailable(productDetailSales.getQuantityAvailable() - stockPreSaleOut.getQuantityOut());
          productQty = 0;
          productFreeQuantityIn = 0;
        } else if (productQty > productDetailSales.getQuantityAvailable()) {

          if (productFreeQuantityIn > 0 && productFreeQuantityIn < productDetailSales.getQuantityAvailable()) {
            salesInvoiceItem.setProductQtyFree(productFreeQuantityIn);
            productFreeQuantityIn = 0;
            productDetailSales.setQuantityAvailable(productDetailSales.getQuantityAvailable() - productFreeQuantityIn);
          }

          salesInvoiceItem.setProductQty(productDetailSales.getQuantityAvailable());
          salesInvoiceItem.setProductDetailId(productDetail);

          stockPreSaleOut = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, stockId, StockStatusService.STOCK_STATUS_SALEABLE, SystemConstants.STOCK_TYPE_SALEABLE);
          stockPreSaleOut.setQuantityOut(salesInvoiceItem.getProductQty() + (salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree()));

          productQty -= productDetailSales.getQuantityAvailable();
          productDetailSales.setQuantityAvailable(0);
        }
      }

      if (productQty > 0 && productDetailSales.getQuantityFreeAvailable() != null && productDetailSales.getQuantityFreeAvailable() > 0 && schemeApplicable) {
        stockPreSaleToSaleable = SalesInvoiceItemUtil.getFreeToSaleable(productQty, salesInvoice, salesInvoiceItem, productDetailSales, freeStockId, SystemConstants.STOCK_TYPE_FREE);
        stockPreSaleConvertIn = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToSaleable, SystemConstants.STOCK_SALEABLE_AS_FREE_IN, SystemConstants.STOCK_TYPE_SALEABLE);
        stockPreSaleConvertOut = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToSaleable, SystemConstants.STOCK_SALEABLE_AS_FREE_OUT, SystemConstants.STOCK_TYPE_SALEABLE);

        productQty -= stockPreSaleToSaleable.getQuantityIn();
        salesInvoiceItem.setProductQty((salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty()) + stockPreSaleToSaleable.getQuantityIn());
      }

      if (productFreeQty > 0) {
        if (productDetailSales.getQuantityFreeAvailable() != null && productDetailSales.getQuantityFreeAvailable() > 0 && schemeApplicable) {
          if (productFreeQty <= productDetailSales.getQuantityFreeAvailable()) {
            salesInvoiceItem.setProductQtyFree(productFreeQty);
            productDetailSales.setQuantityFreeAvailable(productDetailSales.getQuantityFreeAvailable() - productFreeQty);
            productFreeQty = 0;

            stockPreSaleFreeOut = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, freeStockId, StockStatusService.STOCK_STATUS_SALEABLE, SystemConstants.STOCK_TYPE_FREE);
            stockPreSaleFreeOut.setQuantityOut(salesInvoiceItem.getProductQtyFree());

            if (productDetailSales.getFreeQtySchemeId() != null) {
              salesInvoiceItem.setProductFreeQtyScheme(SalesInvoiceItemUtil.getProductFreeQtyScheme(productDetailSales.getFreeQtySchemeId()));
              stockPreSaleFreeOut.setFreeSchemeId(salesInvoiceItem.getProductFreeQtyScheme());
            }
          } else if (productFreeQty > productDetailSales.getQuantityFreeAvailable()) {
            salesInvoiceItem.setProductQtyFree(productDetailSales.getQuantityFreeAvailable());

            stockPreSaleFreeOut = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, freeStockId, StockStatusService.STOCK_STATUS_SALEABLE, SystemConstants.STOCK_TYPE_FREE);
            stockPreSaleFreeOut.setQuantityOut(productDetailSales.getQuantityFreeAvailable());

            productFreeQty -= productDetailSales.getQuantityFreeAvailable();
            productDetailSales.setQuantityFreeAvailable(0);

            if (productDetailSales.getFreeQtySchemeId() != null) {
              salesInvoiceItem.setProductFreeQtyScheme(SalesInvoiceItemUtil.getProductFreeQtyScheme(productDetailSales.getFreeQtySchemeId()));
              stockPreSaleFreeOut.setFreeSchemeId(salesInvoiceItem.getProductFreeQtyScheme());

            }
          }
        }
      }

      if (productFreeQty > 0 && productDetailSales.getQuantityAvailable() != null && productDetailSales.getQuantityAvailable() > 0 && schemeApplicable) {
        stockPreSaleToStockOut = SalesInvoiceItemUtil.getSaleableToFree(productFreeQty, salesInvoice, salesInvoiceItem, productDetailSales, stockId, StockStatusService.STOCK_TYPE_SALEABLE);
        stockPreSaleConvertIn = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToStockOut, SystemConstants.STOCK_SALEABLE_AS_FREE_IN, StockStatusService.STOCK_TYPE_FREE);
        stockPreSaleConvertOut = SalesInvoiceItemUtil.getStockPreSaleConvertInOut(stockPreSaleToStockOut, SystemConstants.STOCK_SALEABLE_AS_FREE_OUT, StockStatusService.STOCK_TYPE_FREE);

        salesInvoiceItem.setProductQtyFree((salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree()) + stockPreSaleToStockOut.getQuantityOut());
        productFreeQty -= stockPreSaleToStockOut.getQuantityOut();
      }

      SalesInvoiceItemService.insert(main, salesInvoiceItem);
      salesInvoiceItem.setSalesInvoiceItemHashCode(String.valueOf(salesInvoiceItem.getId()));
      insertStockPreSale(main, stockPreSaleOut);
      insertStockPreSale(main, stockPreSaleFreeOut);
      insertStockPreSale(main, stockPreSaleToSaleable);
      insertStockPreSale(main, stockPreSaleToStockOut);

      insertStockPreSale(main, stockPreSaleConvertIn);
      insertStockPreSale(main, stockPreSaleConvertOut);

      if (productQty == 0 && productFreeQty == 0) {
        break;
      }
    }

  }

  /**
   *
   * @param main
   * @param stockPreSale
   */
  private static void insertStockPreSale(Main main, StockPreSale stockPreSale) {
    if (stockPreSale != null) {
      StockPreSaleService.insert(main, stockPreSale);
    }
  }

  /**
   *
   * @param salesOrder
   * @param salesOrderItem
   * @return
   */
  private static SalesInvoice convertSalesOrderToSalesInvoice(SalesOrder salesOrder, SalesOrderItem salesOrderItem) {
    SalesInvoice salesInvoice = new SalesInvoice();
    salesInvoice.setAccountGroupId(salesOrder.getAccountGroupId());
    salesInvoice.setCustomerId(salesOrder.getCustomerId());
    salesInvoice.setCommodityId(salesOrderItem.getProductId().getProductCategoryId().getCommodityId());
    salesInvoice.setTaxCodeId(salesOrderItem.getTaxCodeId());
    //salesInvoice.setProductTypeId(salesOrderItem.getProductId().getProductTypeId());
    salesInvoice.setSalesOrderId(salesOrder);
    SalesInvoiceStatus status = new SalesInvoiceStatus();
    status.setId(SystemConstants.DRAFT);
    salesInvoice.setSalesInvoiceStatusId(status);
    return salesInvoice;
  }

  /**
   *
   * @param main
   * @param salesInvoice
   */
  public static void releaseBlockedProductsFromStockPreSalse(Main main, SalesInvoice salesInvoice) {
    AppService.deleteSql(main, StockPreSale.class, "delete from scm_stock_presale where sales_invoice_id = ?", new Object[]{salesInvoice.getId()});
  }

  private static void deleteReference(Main main, SalesInvoice salesInvoice) {
    AppService.deleteSql(main, StockSaleable.class, "delete from scm_stock_saleable where sales_invoice_item_id in(select id from scm_sales_invoice_item "
            + "where sales_invoice_id = ?)", new Object[]{salesInvoice.getId()});

    AppService.deleteSql(main, Platform.class, "delete from scm_platform where sales_invoice_item_id in(select id from scm_sales_invoice_item "
            + "where sales_invoice_id = ?)", new Object[]{salesInvoice.getId()});

    AppService.deleteSql(main, Platform.class, "delete from scm_platform where sales_invoice_id = ?", new Object[]{salesInvoice.getId()});

    LedgerExternalDataService.deleteSalesInvoice(main, salesInvoice);
  }

  /**
   *
   * @param main
   * @param salesInvoice
   */
  public static void actionResetSalesInvoiceToDraft(Main main, SalesInvoice salesInvoice) {
    referenceErrorMessages(main, salesInvoice.getId(), salesInvoice.getInvoiceNo(), null);
    referenceErrorMessages(main, salesInvoice.getId(), salesInvoice.getInvoiceNo(), SystemConstants.SALES_RETURN);
    List<StockSaleable> stockList = AppService.list(main, StockSaleable.class, "select * from scm_stock_saleable where "
            + "sales_invoice_item_id in(select id from scm_sales_invoice_item where sales_invoice_id = ?)", new Object[]{salesInvoice.getId()});

    deleteReference(main, salesInvoice);

    long taxChangeCount = AppService.count(main, "select count(t1.id) from scm_sales_invoice_item t1 "
            + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
            + "inner join scm_product_batch t3 on t2.product_batch_id = t3.id "
            + "inner join scm_product t4 on t3.product_id = t4.id "
            + "inner join scm_product_category t5 on t4.product_category_id = t5.id "
            + "inner join scm_tax_code t6 on t5.purchase_tax_code_id = t6.id "
            + "inner join scm_tax_code t7 on t7.id = t1.tax_code_id "
            + "where t1.sales_invoice_id = ? and t7.rate_percentage != t6.rate_percentage", new Object[]{salesInvoice.getId()});

    if (taxChangeCount == 0) {
      main.clear();
      main.param(SystemConstants.DRAFT);
      main.param(salesInvoice.getId());
      AppService.updateSql(main, SalesInvoice.class, "update scm_sales_invoice set sales_invoice_status_id = ? where id = ?", false);
      main.clear();
    } else {
      main.clear();
      main.param(SystemConstants.DRAFT);
      main.param(salesInvoice.getId());
      AppService.updateSql(main, SalesInvoice.class, "update scm_sales_invoice set sales_invoice_status_id = ?, is_tax_code_modified = 1 where id = ?", false);
      main.clear();

      main.param(salesInvoice.getId());
      AppService.updateSql(main, SalesInvoiceItem.class, "update scm_sales_invoice_item set is_tax_code_modified = 1 "
              + "where id in(select t1.id from scm_sales_invoice_item t1 "
              + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
              + "inner join scm_product_batch t3 on t2.product_batch_id = t3.id "
              + "inner join scm_product t4 on t3.product_id = t4.id "
              + "inner join scm_product_category t5 on t4.product_category_id = t5.id "
              + "inner join scm_tax_code t6 on t5.purchase_tax_code_id = t6.id "
              + "inner join scm_tax_code t7 on t7.id = t1.tax_code_id "
              + "where t1.sales_invoice_id = ? and t7.rate_percentage != t6.rate_percentage)", false);
      main.clear();
    }

    for (StockSaleable stockSaleable : stockList) {
      StockPreSale stockPreSale = SalesInvoiceItemUtil.getStockPreSale(salesInvoice, stockSaleable);
      StockPreSaleService.insert(main, stockPreSale);
    }
    stockList = null;
    main.clear();
  }

  public static final List<SalesInvoice> selectConfirmedAndPackedSalesInvoice(Main main, AccountGroup accountGroup) {
    SqlPage sql = getSalesInvoiceSqlPaged(main);
    main.param(accountGroup.getId());
    sql.cond("where scm_sales_invoice.account_group_id = ? and scm_sales_invoice.sales_invoice_status_id in (" + SystemConstants.CONFIRMED_AND_PACKED + ") and scm_sales_invoice.id not in(select coalesce(scm_consignment_commodity.sales_invoice_id, 0) from scm_consignment_commodity)");
    return AppService.listPagedJpa(main, sql);
  }

  public static void updateSalesInvoicePacking(Main main, SalesInvoice salesInvoice) {
    main.clear();
    main.param(salesInvoice.getNoOfBoxes());
    main.param(salesInvoice.getWeight());
    main.param(salesInvoice.getWeightUnit());
    main.param(SystemConstants.CONFIRMED_AND_PACKED);
    main.param(salesInvoice.getId());
    AppService.updateSql(main, SalesInvoice.class, "update scm_sales_invoice set no_of_boxes = ?, weight= ?, weight_unit = ?, sales_invoice_status_id = ? where id = ?", false);
    main.clear();
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @param pts
   * @return
   */
  public static boolean isPtsPtrRenderable(Main main, AccountGroup accountGroup, boolean pts) {
    long count = 0;
    if (pts) {
      count = AppService.count(main, "select sum(t1.render_pts) pts from scm_account t1 inner join scm_account_group_detail t2 on t1.id = t2.account_id and t2.account_group_id = ?", new Object[]{accountGroup.getId()});
    } else {
      count = AppService.count(main, "select sum(t1.render_ptr) ptr from scm_account t1 inner join scm_account_group_detail t2 on t1.id = t2.account_id and t2.account_group_id = ?", new Object[]{accountGroup.getId()});
    }
    return count != 0;
  }

  /**
   *
   * @param main
   * @param salesInvoice
   */
  public static long cancelSalesInvoice(Main main, SalesInvoice salesInvoice) {
    long salesInvoiceTransactionCount = 0;

    salesInvoiceTransactionCount += AppService.count(main, "select count(id) from scm_sales_return_item where sales_invoice_item_id in(select id from scm_sales_invoice_item where sales_invoice_id = ?)", new Object[]{salesInvoice.getId()});

    salesInvoiceTransactionCount += AppService.count(main, "select count(id) from fin_accounting_transaction_settlement "
            + "where transaction_detail_item_id in(select id from fin_accounting_transaction_detail_item "
            + "where transaction_detail_id in(select id from fin_accounting_transaction_detail "
            + "where accounting_transaction_id in(select id from fin_accounting_transaction "
            + "where accounting_entity_type_id = ? and entity_id = ?)))", new Object[]{AccountingConstant.ACC_ENTITY_SALES.getId(), salesInvoice.getId()});

    salesInvoiceTransactionCount += AppService.count(main, "select count(id) from fin_accounting_transaction_settlement "
            + "where adjusted_transaction_detail_item_id in(select id from fin_accounting_transaction_detail_item "
            + "where transaction_detail_id in(select id from fin_accounting_transaction_detail "
            + "where accounting_transaction_id in(select id from fin_accounting_transaction "
            + "where accounting_entity_type_id = ? and entity_id = ?)))", new Object[]{AccountingConstant.ACC_ENTITY_SALES.getId(), salesInvoice.getId()});

    salesInvoiceTransactionCount += AppService.count(main, "select count(id) from fin_accounting_transaction_settlement "
            + "where transaction_detail_id in(select id from fin_accounting_transaction_detail "
            + "where accounting_transaction_id in(select id from fin_accounting_transaction "
            + "where accounting_entity_type_id = ? and entity_id = ?))", new Object[]{AccountingConstant.ACC_ENTITY_SALES.getId(), salesInvoice.getId()});

    salesInvoiceTransactionCount += AppService.count(main, "select count(id) from fin_accounting_transaction_settlement "
            + "where adjusted_transaction_detail_id in(select id from fin_accounting_transaction_detail "
            + "where accounting_transaction_id in(select id from fin_accounting_transaction "
            + "where accounting_entity_type_id = 7 and entity_id = ?))", new Object[]{AccountingConstant.ACC_ENTITY_SALES.getId(), salesInvoice.getId()});

    if (salesInvoiceTransactionCount == 0) {
      /**
       * Reverse entry of Platform.
       */
      PlatformService.salesInvoiceReverseEntry(main, salesInvoice);
      /**
       * Reverse entry of Accounting Transaction.
       */
      LedgerExternalDataService.saveSalesCancel(main, salesInvoice, salesInvoice.getCompanyId().getCurrencyId(), salesInvoice.getCompanyId());

      /**
       * Stock adjustment.
       */
      StockAdjustmentService.adjustSalesInvoiceStock(main, salesInvoice);

      /**
       * Updating sales invoice status.
       */
      updateSalesInvoiceStatus(main, salesInvoice, SystemConstants.CANCELLED);
    }
    return salesInvoiceTransactionCount;
  }

  /**
   *
   * @param main
   * @param accountGroup
   */
  public static void resetAllSalesInvoiceToDraft(Main main, AccountGroup accountGroup) {
    List<SalesInvoice> list = AppService.list(main, SalesInvoice.class, "select * from scm_sales_invoice where account_group_id = ? and sales_invoice_status_id >= ? and sales_invoice_type = ? ", new Object[]{accountGroup.getId(), SystemConstants.CONFIRMED, SystemConstants.SALES_INVOICE_TYPE_SALES_INVOICE});
    list.forEach((SalesInvoice salesInvoice) -> {
      actionResetSalesInvoiceToDraft(main, salesInvoice);
    });
  }

  public static List<RelatedItems> selectRelatedItemsOfSalesInvoice(Main main, SalesInvoice salesInvoice) {
    StringBuilder sql = new StringBuilder();
    sql.append("select id, invoice_no, 'Invoice Entry' as title from scm_product_entry where id in(select product_entry_id from scm_product_entry_detail "
            + "where id in(select product_entry_detail_id from scm_sales_invoice_item where sales_invoice_id = ?)) ");
    sql.append("union ");
    sql.append("select id, invoice_no, 'Sales Return' as title from scm_sales_return where id in(select sales_return_id from scm_sales_return_item "
            + "where sales_invoice_item_id in(select id from scm_sales_invoice_item where sales_invoice_id  = ?))");

    return AppDb.getList(main.dbConnector(), RelatedItems.class, sql.toString(), new Object[]{salesInvoice.getId(), salesInvoice.getId()});
  }

  public static boolean isProductValueMrpChanged(Main main, SalesInvoice salesInvoice) {
    return AppService.exist(main, "select 1 from scm_sales_invoice_item where sales_invoice_id = ? and is_mrp_changed = ?", new Object[]{salesInvoice.getId(), SystemConstants.DEFAULT});
  }

  public static List<ProductInvoiceDetail> selectProductRateHistory(Main main, SalesInvoice salesInvoice, SalesInvoiceItem salesInvoiceItem) {
    List<ProductInvoiceDetail> list = null;
    if (salesInvoiceItem != null && salesInvoiceItem.getProduct() != null) {

      list = AppDb.getList(main.dbConnector(), ProductInvoiceDetail.class, "SELECT t2.invoice_no,t1.product_qty as qty,t1.product_qty_free as qty_free,"
              + "t1.scheme_discount_derived as scheme_discount,t1.scheme_discount_percentage,t1.product_discount_derived as product_discount,"
              + "t1.product_discount_percentage,t1.invoice_discount_derived as invoice_discount,"
              + "t2.invoice_date,t1.prod_piece_selling_forced as value_rate,\n"
              + "t1.value_pts,t1.value_ptr,t1.value_mrp,t4.batch_no \n"
              + "FROM scm_sales_invoice_item t1 \n"
              + "INNER JOIN scm_sales_invoice t2 ON t1.sales_invoice_id = t2.id AND t2.sales_invoice_status_id = ? \n"
              + "INNER JOIN scm_product_detail t3 ON t1.product_detail_id =t3.id \n"
              + "LEFT JOIN scm_product_batch t4 ON t4.id=t3.product_batch_id\n"
              + "LEFT JOIN scm_product t5 ON t5.id=t4.product_id \n"
              + "WHERE t2.account_group_id = ?  and t5.id=? and t2.customer_id=? "
              + "ORDER BY t2.invoice_date DESC Limit 5",
              new Object[]{SystemConstants.CONFIRMED, salesInvoice.getAccountGroupId().getId(), salesInvoiceItem.getProduct().getId(), salesInvoice.getCustomerId().getId()});
    }
    return list;
  }

  public static SalesInvoice selectInvoiceByInvoiceNumber(Main main, String invoiceNumber) {
    String sql = " SELECT * FROM scm_sales_invoice WHERE invoice_no = ? ";
    return (SalesInvoice) AppService.single(main, SalesInvoice.class, sql, new Object[]{invoiceNumber});
  }

  public static List<ProductDetailSales> selectProductDetailSales(Main main, SalesInvoiceItem salesInvoiceItem) {
    List<ProductDetailSales> productDetailSalesList = null;
    SalesInvoice salesInvoice = salesInvoiceItem.getSalesInvoiceId();
    if (salesInvoiceItem != null && salesInvoiceItem.getProduct() != null) {
      if (salesInvoice.getAccountGroupId() != null && salesInvoice.getAccountGroupPriceListId() != null) {
        TaxCalculator taxCalculator = SystemRuntimeConfig.getTaxCalculator(salesInvoice.getTaxProcessorId().getProcessorClass());
        productDetailSalesList = taxCalculator.selectProductDetailForSales(main, salesInvoice, salesInvoiceItem);
        if (!StringUtil.isEmpty(productDetailSalesList)) {
//          if (salesInvoiceItem.getProductDetailSales() == null) {
//            //  salesInvoiceItem.setProductDetailSales(productDetailSalesList.get(0));
//            //  salesInvoiceItem.setProductHashChanged(Boolean.TRUE);
//          } else {
//            productDetailSalesList.remove(salesInvoiceItem.getProductDetailSales());
//            productDetailSalesList.add(0, salesInvoiceItem.getProductDetailSales());
//          }
          salesInvoiceItem.setProductDetailSalesList(productDetailSalesList);
        } else {
          salesInvoiceItem.setProductDetailSales(null);
        }
      }
    }
    return salesInvoiceItem.getProductDetailSalesList();
  }

  private static void referenceErrorMessages(Main main, Integer id, String invoiceNo, String type) {
    String existing = "";
    int i = 0;
    String sql = "";
    if (type != null) {
      if (type.equals(SystemConstants.SALES_RETURN)) {
        sql = "select invoice_no,customer_id from scm_sales_return where id in"
                + "(select sales_return_id from scm_sales_return_item where id in\n"
                + "(select sales_return_item_id from scm_stock_saleable where stock_saleable_id in\n"
                + "(select id from scm_stock_saleable where sales_invoice_item_id in(select id from scm_sales_invoice_item where sales_invoice_id= ?))))";
      } else if (type.equals(SystemConstants.DEBIT_CREDIT_NOTE)) {
        sql = "select * from fin_debit_credit_note where id in(select debit_credit_note_id from fin_debit_credit_note_item where sales_invoice_item_id in\n"
                + "(select id from scm_sales_invoice_item where sales_invoice_id =?))";
      }
    } else {
      sql = "select invoice_no,customer_id from scm_sales_return where id in(select sales_return_id from scm_sales_return_item where sales_invoice_id = ?)";
    }
    List<ReferenceInvoice> exist = (List<ReferenceInvoice>) AppDb.getList(main.dbConnector(), ReferenceInvoice.class, sql, new Object[]{id});
    for (ReferenceInvoice t : exist) {
      if (i > 0) {
        existing += ",";
      }
      existing += t.getInvoiceNo();
      i++;
    }
    AppUtil.referenceError(main, invoiceNo, existing);

  }

  public static void customerSelectEvent(Main main, SalesInvoice salesInvoice, Company company, AccountGroup accountGroup, Customer customer) {
    if (customer != null) {
      SalesAccount saccount = SalesAccountService.selectSalesAccountByCustomerAccountGroup(main, customer, accountGroup);
      if (saccount != null) {
        salesInvoice.setAccountGroupPriceListId(saccount.getAccountGroupPriceListId());
        salesInvoice.setSalesCreditDays(saccount.getSalesCreditDays());
      }
    } else {
      salesInvoice.setAccountGroupPriceListId(null);
      salesInvoice.setSalesCreditDays(null);
    }
  }

  public static void updateProductSalesValues(Main main, SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItemList, SalesInvoiceItem salesInvoiceItem, int qType) {
    TaxCalculator taxCalculator = SystemRuntimeConfig.getTaxCalculator(salesInvoice.getTaxProcessorId().getProcessorClass());
    if (salesInvoiceItem != null) {
      SalesInvoiceItemUtil.setCurrentAvailableQty(salesInvoiceItem, SalesInvoiceUtil.PRODUCT_PRIMARY_QTY);

      if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getValueProdPieceSelling() != null) {

        if (salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId() != null) {
          int freeQty = SalesInvoiceService.selectProductFreeQuantity(main, salesInvoiceItem);
          if (freeQty > 0) {
            salesInvoiceItem.setSuggestedFreeQty(freeQty);
            if (qType == SalesInvoiceUtil.PRODUCT_PRIMARY_QTY || qType == SalesInvoiceUtil.PRODUCT_SECONDARY_QTY || qType == SalesInvoiceUtil.PRODUCT_TERTIARY_QTY) {
              salesInvoiceItem.setProductQtyFree(freeQty);
            }
            ProductFreeQtyScheme productFreeQtyScheme = new ProductFreeQtyScheme();
            productFreeQtyScheme.setId(salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId());
            salesInvoiceItem.setProductFreeQtyScheme(productFreeQtyScheme);
          }
        }

        if (qType == SalesInvoiceUtil.PRODUCT_PRIMARY_QTY) {
          double qty = salesInvoiceItem.getProductQty() != null ? salesInvoiceItem.getProductQty() : 0;
          double freeQty = salesInvoiceItem.getProductQtyFree() != null ? salesInvoiceItem.getProductQtyFree() : 0;
          if (salesInvoiceItem.getProductDetailSales().getSecondaryPackId() != null) {
            salesInvoiceItem.setSecondaryQuantity((qty + freeQty) / salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
          }
          if (salesInvoiceItem.getProductDetailSales().getTertiaryPackId() != null) {
            salesInvoiceItem.setTertiaryQuantity(salesInvoiceItem.getSecondaryQuantity() / salesInvoiceItem.getProductDetailSales().getTerToSecQuantity());
          }
        }

        // Finds Possible free Quantity
        salesInvoiceItem.setPossibleFreeQuantity((salesInvoiceItem.getProductDetailSales().getQuantityAvailable() + salesInvoiceItem.getProductDetailSales().getQuantityFreeAvailable()) - salesInvoiceItem.getProductQty());

        if (salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId() != null) {
          if (salesInvoiceItem.getProductQtyFree() > salesInvoiceItem.getPossibleFreeQuantity()) {
            salesInvoiceItem.setProductQtyFree(salesInvoiceItem.getPossibleFreeQuantity());
          }
        }

        //Derived Scheme Discount
        if (salesInvoiceItem.getProductDetailSales() != null && salesInvoiceItem.getProductDetailSales().getSchemeDiscountPer() != null && salesInvoiceItem.getProductDetailSales().getSchemeDiscountPer() > 0) {
          if (salesInvoiceItem.getProductQty() != null) {
            if (salesInvoiceItem.getSchemeDiscountPercentage() == null) {
              salesInvoiceItem.setSchemeDiscountPercentage(salesInvoiceItem.getProductDetailSales().getSchemeDiscountPer());
            }
            double sDisc = salesInvoiceItem.getProdPieceSellingForced() * salesInvoiceItem.getSchemeDiscountPercentage() / 100;
            salesInvoiceItem.setSchemeDiscountValue(sDisc * salesInvoiceItem.getProductQty());
          } else {
            if (salesInvoiceItem.getProdPieceSellingForced() != null) {
              salesInvoiceItem.setSchemeDiscountValue(salesInvoiceItem.getProdPieceSellingForced() * salesInvoiceItem.getSchemeDiscountPercentage() / 100);
            }
          }
        }

        //Derived Product Discount
        if (salesInvoiceItem.getProductDetailSales() != null && salesInvoiceItem.getProductDetailSales().getProductDiscountPer() != null && salesInvoiceItem.getProductDetailSales().getProductDiscountPer() > 0) {
          if (salesInvoiceItem.getProductQty() != null) {
            if (salesInvoiceItem.getProductDiscountPercentage() == null) {
              salesInvoiceItem.setProductDiscountPercentage(salesInvoiceItem.getProductDetailSales().getProductDiscountPer());
            }
            double pDisc = salesInvoiceItem.getProdPieceSellingForced() * salesInvoiceItem.getProductDiscountPercentage() / 100;
            salesInvoiceItem.setProductDiscountValue(pDisc * salesInvoiceItem.getProductQty());
          } else {
            if (salesInvoiceItem.getProdPieceSellingForced() != null) {
              salesInvoiceItem.setProductDiscountValue(salesInvoiceItem.getProdPieceSellingForced() * salesInvoiceItem.getProductDiscountPercentage() / 100);
            }

          }
        }
        taxCalculator.processSalesInvoiceCalculation(salesInvoice, salesInvoiceItemList, true);
      } else {
        salesInvoiceItem.setValueSale(null);
        taxCalculator.processSalesInvoiceCalculation(salesInvoice, salesInvoiceItemList, true);
      }
    }
  }

  public static void addDistrictToCustomer(Main main, Customer customer) {
    if (customer != null) {
      District district = (District) AppService.single(main, District.class, "select * from scm_district where id in"
              + "(select district_id from scm_customer_address where customer_id = ? and address_type_id =1)", new Object[]{customer.getId()});
      if (district != null) {
        customer.setCustomerAddress(district.getDistrictName());
      }
    }
  }

  public static Date getLastConfirmedEntryDate(Main main, SalesInvoice salesInvoice) {
    Date lastConfirmedEntryDate = null;
    InvoiceItem lastSales = (InvoiceItem) AppDb.single(main.dbConnector(), InvoiceItem.class,
            "select invoice_entry_date as entry_date from scm_sales_invoice where financial_year_id = ? and account_group_id =? and confirmed_at is not null \n"
            + "order by invoice_entry_date desc limit 1",
            new Object[]{salesInvoice.getFinancialYearId().getId(), salesInvoice.getAccountGroupId().getId()});
    if (lastSales != null) {
      lastConfirmedEntryDate = lastSales.getEntryDate();
    }
    if (lastConfirmedEntryDate != null) {
      return lastConfirmedEntryDate;
    } else {
      return SystemRuntimeConfig.getMinEntryDate(salesInvoice.getCompanyId());
    }
  }

  public static Date getNearestEntryDate(Main main, SalesInvoice salesInvoice, Date initialEntryDate) {
    Date nearestEntryDate = null;
    if (initialEntryDate != null) {
      InvoiceItem lastSales = (InvoiceItem) AppDb.single(main.dbConnector(), InvoiceItem.class,
              "select invoice_entry_date as entry_date from scm_sales_invoice \n"
              + "where financial_year_id = ? and account_group_id =? and confirmed_at is not null \n"
              + "and invoice_entry_date::date >=?::date and id !=? order by invoice_entry_date asc limit 1",
              new Object[]{salesInvoice.getFinancialYearId().getId(), salesInvoice.getAccountGroupId().getId(),
                SystemRuntimeConfig.SDF_YYYY_MM_DD.format(initialEntryDate), salesInvoice.getId()});
      if (lastSales != null) {
        nearestEntryDate = lastSales.getEntryDate();
      }
    }
    return nearestEntryDate;
  }

}
