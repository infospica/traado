/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Account;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.StockLedger;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author fify
 */
public class StockLedgerService {

  public static List<StockLedger> getStockLedgerList(Main main, String fromDate, String toDate, AccountGroup accountGroup, Account account, Product product, ProductBatch productBatch) {
    //String sql = "SELECT ROW_NUMBER () OVER (ORDER BY opening_stock,entry_date),* FROM getStockLedgerForProduct(11,71,143,'2017-11-01','2017-12-31')";
    String sql = "SELECT ROW_NUMBER () OVER (ORDER BY entry_date::timestamp::date,confirmed_date::timestamp::date,COALESCE(opening_stock,0) DESC,COALESCE(quantity_in,0) DESC),* FROM getStockLedgerForProduct(?,?,?,?,?,?)";
    if (productBatch != null && productBatch.getId() != null) {
      sql = "SELECT ROW_NUMBER () OVER (ORDER BY entry_date::timestamp::date,confirmed_date::timestamp::date,\n"
              + "		COALESCE(opening_stock,0) DESC,COALESCE(quantity_in,0) DESC),* FROM getStockLedgerForProduct(?,?,?,?,?,?) WHERE UPPER(prod_batch) = UPPER('" + productBatch.getBatchNo() + "')";
    }

    return AppDb.getList(main.dbConnector(), StockLedger.class, sql, new Object[]{UserRuntimeView.instance().getCompany().getId(), accountGroup.getId(), account == null ? null : account.getId(), product.getId(), fromDate, toDate});
  }
}
