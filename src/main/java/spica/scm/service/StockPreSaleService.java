/*
 * @(#)StockSaleableService.java	1.0 Fri Feb 03 19:15:39 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.SalesInvoice;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.StockPreSale;

/**
 * StockPreSaleService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 03 19:15:39 IST 2017
 */
public abstract class StockPreSaleService {

  /**
   * StockPreSale paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStockPreSaleSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_stock_presale", StockPreSale.class, main);
    sql.main("select scm_stock_presale.id,scm_stock_presale.product_detail_id,scm_stock_presale.account_id,scm_stock_presale.quantity_in,"
            + "scm_stock_presale.stock_status_id,scm_stock_presale.stock_saleable_id,scm_stock_presale.blocked_at,scm_stock_presale.released_at,"
            + "scm_stock_presale.ref_product_entry_detail_id,scm_stock_presale.sales_invoice_id,scm_stock_presale.sales_invoice_item_id,"
            + "scm_stock_presale.free_scheme_id,scm_stock_presale.sales_order_item_id  "
            + "from scm_stock_presale scm_stock_presale "); //Main query
    sql.count("select count(scm_stock_presale.id) as total from scm_stock_presale scm_stock_presale "); //Count query
    sql.append("left outer join scm_product_detail scm_product_detail on (scm_product_detail.id = scm_stock_presale.product_detail_id) "
            + "left outer join scm_account scm_account on (scm_account.id = scm_stock_presale.account_id) "
            + "left outer join scm_stock_status scm_stock_status on (scm_stock_status.id = scm_stock_presale.stock_status_id) "
            + "left outer join scm_stock_saleable scm_stock_saleable on (scm_stock_saleable.id = scm_stock_presale.stock_saleable_id) "
            + "left outer join scm_sales_invoice scm_sales_invoice on (scm_sales_invoice.id = scm_stock_presale.sales_invoice_id) "
            + "left outer join scm_sales_invoice_item scm_sales_invoice_item on (scm_sales_invoice_item.id = scm_stock_presale.sales_invoice_item_id) "
            + "left outer join scm_sales_order_item scm_sales_order_item on (scm_sales_order_item.id = scm_stock_presale.sales_order_item_id) "
            + "left outer join scm_product_free_qty_scheme scm_product_free_qty_scheme on (scm_product_free_qty_scheme.id = scm_stock_presale.free_scheme_id) "
            + "left outer join scm_product_entry_detail scm_product_entry_detail on (scm_product_entry_detail.id = scm_stock_presale.ref_product_entry_detail_id)"); //Join Query

    sql.string(new String[]{"scm_product_detail.batch_no", "scm_account.account_code", "scm_stock_status.title"}); //String search or sort fields
    sql.number(new String[]{"scm_stock_presale.id", "scm_stock_presale.quantity_in", "scm_stock_saleable.quantity_in", "scm_product_entry_detail.product_quantity"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_stock_presale.blocked_at", "scm_stock_presale.released_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of StockPreSale.
   *
   * @param main
   * @return List of StockPreSale
   */
  public static final List<StockPreSale> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStockPreSaleSqlPaged(main));
  }

  /**
   * Select StockPreSale by key.
   *
   * @param main
   * @param stockPreSale
   * @return StockPreSale
   */
  public static final StockPreSale selectByPk(Main main, StockPreSale stockPreSale) {
    return (StockPreSale) AppService.find(main, StockPreSale.class, stockPreSale.getId());
  }

  /**
   * Insert StockPreSale.
   *
   * @param main
   * @param stockPreSale
   */
  public static final void insert(Main main, StockPreSale stockPreSale) {
    AppService.insert(main, stockPreSale);
  }

  /**
   * Update StockPreSale by key.
   *
   * @param main
   * @param stockPreSale
   * @return StockPreSale
   */
  public static final StockPreSale updateByPk(Main main, StockPreSale stockPreSale) {
    return (StockPreSale) AppService.update(main, stockPreSale);
  }

  /**
   * Insert or update StockPreSale
   *
   * @param main
   * @param stockPreSale
   */
  public static void insertOrUpdate(Main main, StockPreSale stockPreSale) {
    if (stockPreSale.getId() == null) {
      insert(main, stockPreSale);
    } else {
      updateByPk(main, stockPreSale);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param stockPreSale
   */
  public static void clone(Main main, StockPreSale stockPreSale) {
    stockPreSale.setId(null); //Set to null for insert
    insert(main, stockPreSale);
  }

  /**
   * Delete StockSaleable.
   *
   * @param main
   * @param stockPreSale
   */
  public static final void deleteByPk(Main main, StockPreSale stockPreSale) {
    AppService.delete(main, StockPreSale.class, stockPreSale.getId());
  }

  /**
   * Delete Array of StockSaleable.
   *
   * @param main
   * @param stockPreSale
   */
  public static final void deleteByPkArray(Main main, StockPreSale[] stockPreSale) {
    for (StockPreSale e : stockPreSale) {
      deleteByPk(main, e);
    }
  }

  public static final void releaseStockFromPreSale(Main main, SalesInvoice salesInvoice) {
    AppService.deleteSql(main, StockPreSale.class, "delete from scm_stock_presale where sales_invoice_id = ?", new Object[]{salesInvoice.getId()});
  }
  
  public static final void deleteByProductEntryDetail(Main main,ProductEntryDetail productEntryDetail){
    AppService.deleteSql(main, StockPreSale.class, "delete from scm_stock_presale where ref_product_entry_detail_id = ?", new Object[]{productEntryDetail.getId()});
  }
}
