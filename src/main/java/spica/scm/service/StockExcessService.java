/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.common.StockExcess;
import spica.scm.domain.Account;
import spica.scm.domain.Product;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author java-2
 */
public abstract class StockExcessService {

  public static List<StockExcess> selectStockExcessList(Main main, Account accountId, Product productId) {
    String sql = "SELECT * FROM getExcessStockofProduct(?,?)";
    return AppDb.getList(main.dbConnector(), StockExcess.class, sql, new Object[]{accountId == null ? null : accountId.getId(), productId == null ? null : productId.getId()});
  }
}
