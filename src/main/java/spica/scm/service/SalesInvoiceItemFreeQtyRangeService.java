/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.SalesInvoiceItemFreeQtyRange;
import spica.scm.validate.SalesInvoiceItemFreeQtyRangeIs;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;

/**
 *
 * @author java-2
 */
public abstract class SalesInvoiceItemFreeQtyRangeService {

  /**
   * SalesInvoiceItemFreeQtyRange paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesInvoiceItemFreeQtyRangeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sal_inv_item_free_qty_by_range", SalesInvoiceItemFreeQtyRange.class, main);
    sql.main("select scm_sal_inv_item_free_qty_by_range.id, scm_sal_inv_item_free_qty_by_range.range_from, "
            + "scm_sal_inv_item_free_qty_by_range.range_to, scm_sal_inv_item_free_qty_by_range.free_qty, "
            + "scm_sal_inv_item_free_qty_by_range.sales_invoice_item_id from scm_sal_inv_item_free_qty_by_range scm_sal_inv_item_free_qty_by_range "); //Main query    
    sql.count("select count(scm_sal_inv_item_free_qty_by_range.id) as total from scm_sal_inv_item_free_qty_by_range scm_sal_inv_item_free_qty_by_range "); //Count query    
    sql.join("left outer join scm_sales_invoice_item scm_sales_invoice_item on (scm_sales_invoice_item.id = scm_sal_inv_item_free_qty_by_range.sales_invoice_item_id)"); //Join Query
    sql.string(null); //String search or sort fields
    sql.number(new String[]{"scm_sal_inv_item_free_qty_by_range.free_qty", "scm_sal_inv_item_free_qty_by_range.range_from", "scm_sal_inv_item_free_qty_by_range.range_to", "scm_sal_inv_item_free_qty_by_range.sales_invoice_item_id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesInvoiceItemFreeQtyRange.
   *
   * @param main
   * @return List of SalesInvoiceItemFreeQtyRange
   */
  public static final List<SalesInvoiceItemFreeQtyRange> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesInvoiceItemFreeQtyRangeSqlPaged(main));
  }

  /**
   * Select SalesInvoiceItemFreeQtyRange by key.
   *
   * @param main
   * @param salesInvoiceItemFreeQtyRange
   * @return SalesInvoiceItemFreeQtyRange
   */
  public static final SalesInvoiceItemFreeQtyRange selectByPk(Main main, SalesInvoiceItemFreeQtyRange salesInvoiceItemFreeQtyRange) {
    return (SalesInvoiceItemFreeQtyRange) AppService.find(main, SalesInvoiceItemFreeQtyRange.class, salesInvoiceItemFreeQtyRange.getId());
  }

  /**
   * Insert SalesItemFreeQtyRange.
   *
   * @param main
   * @param salesInvoiceItemFreeQtyRange
   */
  public static final void insert(Main main, SalesInvoiceItemFreeQtyRange salesInvoiceItemFreeQtyRange) {
    SalesInvoiceItemFreeQtyRangeIs.insertAble(main, salesInvoiceItemFreeQtyRange);  //Validating
    AppService.insert(main, salesInvoiceItemFreeQtyRange);

  }

  /**
   * Update SalesInvoiceItemFreeQtyRange by key.
   *
   * @param main
   * @param salesInvoiceItemFreeQtyRange
   * @return SalesInvoiceItemFreeQtyRange
   */
  public static final SalesInvoiceItemFreeQtyRange updateByPk(Main main, SalesInvoiceItemFreeQtyRange salesInvoiceItemFreeQtyRange) {
    SalesInvoiceItemFreeQtyRangeIs.updateAble(main, salesInvoiceItemFreeQtyRange); //Validating
    return (SalesInvoiceItemFreeQtyRange) AppService.update(main, salesInvoiceItemFreeQtyRange);
  }

  /**
   * Insert or update SalesInvoiceItemFreeQtyRange
   *
   * @param main
   * @param salesInvoiceItemFreeQtyRange
   */
  public static void insertOrUpdate(Main main, SalesInvoiceItemFreeQtyRange salesInvoiceItemFreeQtyRange) {
    if (salesInvoiceItemFreeQtyRange.getId() == null) {
      insert(main, salesInvoiceItemFreeQtyRange);
    } else {
      updateByPk(main, salesInvoiceItemFreeQtyRange);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesInvoiceItemFreeQtyRange
   */
  public static void clone(Main main, SalesInvoiceItemFreeQtyRange salesInvoiceItemFreeQtyRange) {
    salesInvoiceItemFreeQtyRange.setId(null); //Set to null for insert
    insert(main, salesInvoiceItemFreeQtyRange);
  }

  /**
   * Delete SalesInvoiceItemFreeQtyRange.
   *
   * @param main
   * @param salesInvoiceItemFreeQtyRange
   */
  public static final void deleteByPk(Main main, SalesInvoiceItemFreeQtyRange salesInvoiceItemFreeQtyRange) {
    SalesInvoiceItemFreeQtyRangeIs.deleteAble(main, salesInvoiceItemFreeQtyRange); //Validation
    AppService.delete(main, SalesInvoiceItemFreeQtyRange.class, salesInvoiceItemFreeQtyRange.getId());
  }

  /**
   * Delete Array of SalesInvoiceItemFreeQtyRange.
   *
   * @param main
   * @param salesInvoiceItemFreeQtyRange
   */
  public static final void deleteByPkArray(Main main, SalesInvoiceItemFreeQtyRange[] salesInvoiceItemFreeQtyRange) {
    for (SalesInvoiceItemFreeQtyRange e : salesInvoiceItemFreeQtyRange) {
      deleteByPk(main, e);
    }
  }
}
