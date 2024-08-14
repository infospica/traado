/*
 * @(#)SalesServicesInvoiceItemService.java	1.0 Wed Jan 31 15:55:51 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.SalesServicesInvoice;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesServicesInvoiceItem;
import spica.scm.validate.SalesServicesInvoiceItemIs;
import wawo.entity.util.StringUtil;

/**
 * SalesInvoiceServiceItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Jan 31 15:55:51 IST 2018
 */
public abstract class SalesServicesInvoiceItemService {

  /**
   * SalesInvoiceServiceItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesServicesInvoiceItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_services_invoice_item", SalesServicesInvoiceItem.class, main);
    sql.main("select scm_sales_services_invoice_item.id,scm_sales_services_invoice_item.sales_services_invoice_id,scm_sales_services_invoice_item.commodity_id,scm_sales_services_invoice_item.taxable_value,scm_sales_services_invoice_item.igst_id,scm_sales_services_invoice_item.sgst_id,scm_sales_services_invoice_item.cgst_id,scm_sales_services_invoice_item.igst_amount,scm_sales_services_invoice_item.sgst_amount,scm_sales_services_invoice_item.cgst_amount from scm_sales_services_invoice_item scm_sales_services_invoice_item "); //Main query
    sql.count("select count(scm_sales_services_invoice_item.id) as total from scm_sales_services_invoice_item scm_sales_services_invoice_item "); //Count query
    sql.join("left outer join scm_sales_services_invoice scm_sales_services_invoice on (scm_sales_services_invoice.id = scm_sales_services_invoice_item.sales_invoice_service_id) "
            + "left outer join scm_service_commodity scm_service_commodity on (scm_service_commodity.id = scm_sales_services_invoice_item.commodity_id) "
            + "left outer join scm_tax_code igst_id on (igst_id.id = scm_sales_services_invoice_item.igst_id) "
            + "left outer join scm_tax_code sgst_id on (sgst_id.id = scm_sales_services_invoice_item.sgst_id) "
            + "left outer join scm_tax_code cgst_id on (cgst_id.id = scm_sales_services_invoice_item.cgst_id)"); //Join Query

    sql.string(new String[]{"scm_service_commodity.title", "igst_id.code", "sgst_id.code", "cgst_id.code"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_services_invoice_item.id", "scm_sales_services_invoice_item.taxable_value", "scm_sales_services_invoice_item.igst_amount",
      "scm_sales_services_invoice_item.sgst_amount", "scm_sales_services_invoice_item.cgst_amount"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesServicesInvoiceItem.
   *
   * @param main
   * @return List of SalesServicesInvoiceItem
   */
  public static final List<SalesServicesInvoiceItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesServicesInvoiceItemSqlPaged(main));
  }

//  /**
//   * Return list of SalesServicesInvoiceItem based on condition
//   * @param main
//   * @return List<SalesServicesInvoiceItem>
//   */
//  public static final List<SalesServicesInvoiceItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesServicesInvoiceItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesServicesInvoiceItem by key.
   *
   * @param main
   * @param salesInvoiceServiceItem
   * @return SalesServicesInvoiceItem
   */
  public static final SalesServicesInvoiceItem selectByPk(Main main, SalesServicesInvoiceItem salesInvoiceServiceItem) {
    return (SalesServicesInvoiceItem) AppService.find(main, SalesServicesInvoiceItem.class, salesInvoiceServiceItem.getId());
  }

  /**
   * Insert SalesServicesInvoiceItem.
   *
   * @param main
   * @param salesInvoiceServiceItem
   */
  public static final void insert(Main main, SalesServicesInvoiceItem salesInvoiceServiceItem) {
    SalesServicesInvoiceItemIs.insertAble(main, salesInvoiceServiceItem);  //Validating
    AppService.insert(main, salesInvoiceServiceItem);

  }

  /**
   * Update SalesServicesInvoiceItem by key.
   *
   * @param main
   * @param salesInvoiceServiceItem
   * @return SalesServicesInvoiceItem
   */
  public static final SalesServicesInvoiceItem updateByPk(Main main, SalesServicesInvoiceItem salesInvoiceServiceItem) {
    SalesServicesInvoiceItemIs.updateAble(main, salesInvoiceServiceItem); //Validating
    return (SalesServicesInvoiceItem) AppService.update(main, salesInvoiceServiceItem);
  }

  /**
   * Insert or update SalesServicesInvoiceItem
   *
   * @param main
   * @param salesInvoiceServiceItem
   */
  public static void insertOrUpdate(Main main, SalesServicesInvoiceItem salesInvoiceServiceItem) {
    if (salesInvoiceServiceItem.getId() == null) {
      insert(main, salesInvoiceServiceItem);
    } else {
      updateByPk(main, salesInvoiceServiceItem);
    }
  }

  public static void insertOrUpdate(Main main, List<SalesServicesInvoiceItem> salesInvoiceServiceItemList) {
    if (!StringUtil.isEmpty(salesInvoiceServiceItemList)) {
      for (SalesServicesInvoiceItem salesServicesInvoiceItem : salesInvoiceServiceItemList) {
        if (salesServicesInvoiceItem != null && salesServicesInvoiceItem.getCommodityId() != null && salesServicesInvoiceItem.getTaxableValue() != null) {
          insertOrUpdate(main, salesServicesInvoiceItem);
        }
      }
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesInvoiceServiceItem
   */
  public static void clone(Main main, SalesServicesInvoiceItem salesInvoiceServiceItem) {
    salesInvoiceServiceItem.setId(null); //Set to null for insert
    insert(main, salesInvoiceServiceItem);
  }

  /**
   * Delete SalesServicesInvoiceItem.
   *
   * @param main
   * @param salesInvoiceServiceItem
   */
  public static final void deleteByPk(Main main, SalesServicesInvoiceItem salesInvoiceServiceItem) {
    SalesServicesInvoiceItemIs.deleteAble(main, salesInvoiceServiceItem); //Validation
    AppService.delete(main, SalesServicesInvoiceItem.class, salesInvoiceServiceItem.getId());
  }

  /**
   * Delete Array of SalesServicesInvoiceItem.
   *
   * @param main
   * @param salesInvoiceServiceItem
   */
  public static final void deleteByPkArray(Main main, SalesServicesInvoiceItem[] salesInvoiceServiceItem) {
    for (SalesServicesInvoiceItem e : salesInvoiceServiceItem) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param salesServicesInvoice
   * @return
   */
  public static List<SalesServicesInvoiceItem> selectBySalesInvoiceService(Main main, SalesServicesInvoice salesServicesInvoice) {
    return AppService.list(main, SalesServicesInvoiceItem.class, "select * from scm_sales_services_invoice_item where sales_services_invoice_id = ?", new Object[]{salesServicesInvoice.getId()});
  }
}
