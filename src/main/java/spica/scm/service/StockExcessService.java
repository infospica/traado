/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
