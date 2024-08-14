/*
 * @(#)ProductPresetService.java	1.0 Tue Sep 20 15:27:15 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.Product;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductPreset;
import spica.scm.validate.ProductPresetIs;

/**
 * ProductPresetService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Sep 20 15:27:15 IST 2016
 */
public abstract class ProductPresetService {

  /**
   * ProductPreset paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductPresetSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_preset", ProductPreset.class, main);
    sql.main("select scm_product_preset.id,scm_product_preset.product_id,scm_product_preset.account_id,scm_product_preset.margin_percentage,scm_product_preset.vendor_reserve_percentage,scm_product_preset.ptr_margin_percentage,scm_product_preset.pts_margin_percentage,scm_product_preset.created_by,scm_product_preset.modified_by,scm_product_preset.created_at,scm_product_preset.modified_at,scm_product_preset.pack_size from scm_product_preset scm_product_preset "); //Main query
    sql.count("select count(scm_product_preset.id) as total from scm_product_preset scm_product_preset "); //Count query
    sql.join("left outer join scm_product scm_product_presetproduct_id on (scm_product_presetproduct_id.id = scm_product_preset.product_id) "
            + "left outer join scm_account scm_product_presetaccount_id on (scm_product_presetaccount_id.id = scm_product_preset.account_id) "); //Join Query

    sql.string(new String[]{"scm_product_presetproduct_id.product_name", "scm_product_presetaccount_id.account_code", "scm_product_preset.created_by", "scm_product_preset.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_preset.id", "scm_product_preset.margin_percentage", "scm_product_preset.vendor_reserve_percentage", "scm_product_preset.ptr_margin_percentage", "scm_product_preset.pts_margin_percentage"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_preset.created_at", "scm_product_preset.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductPreset.
   *
   * @param main
   * @return List of ProductPreset
   */
  public static final List<ProductPreset> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductPresetSqlPaged(main));
  }

//  /**
//   * Return list of ProductPreset based on condition
//   * @param main
//   * @return List<ProductPreset>
//   */
//  public static final List<ProductPreset> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductPresetSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductPreset by key.
   *
   * @param main
   * @param productPreset
   * @return ProductPreset
   */
  public static final ProductPreset selectByPk(Main main, ProductPreset productPreset) {
    return (ProductPreset) AppService.find(main, ProductPreset.class, productPreset.getId());
  }

  /**
   * Insert ProductPreset.
   *
   * @param main
   * @param productPreset
   */
  public static final void insert(Main main, ProductPreset productPreset) {
    ProductPresetIs.insertAble(main, productPreset);  //Validating
    AppService.insert(main, productPreset);

  }

  /**
   * Update ProductPreset by key.
   *
   * @param main
   * @param productPreset
   * @return ProductPreset
   */
  public static final ProductPreset updateByPk(Main main, ProductPreset productPreset) {
    ProductPresetIs.updateAble(main, productPreset); //Validating
    return (ProductPreset) AppService.update(main, productPreset);
  }

  /**
   * Insert or update ProductPreset
   *
   * @param main
   * @param productPreset
   */
  public static void insertOrUpdate(Main main, ProductPreset productPreset) {
    if (productPreset.getId() == null) {
      insert(main, productPreset);
    } else {
      updateByPk(main, productPreset);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productPreset
   */
  public static void clone(Main main, ProductPreset productPreset) {
    productPreset.setId(null); //Set to null for insert
    insert(main, productPreset);
  }

  /**
   * Delete ProductPreset.
   *
   * @param main
   * @param productPreset
   */
  public static final void deleteByPk(Main main, ProductPreset productPreset) {
    ProductPresetIs.deleteAble(main, productPreset); //Validation
    AppService.delete(main, ProductPreset.class, productPreset.getId());
  }

  /**
   * Delete Array of ProductPreset.
   *
   * @param main
   * @param productPreset
   */
  public static final void deleteByPkArray(Main main, ProductPreset[] productPreset) {
    for (ProductPreset e : productPreset) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param productId
   * @param account
   * @return
   */
  public static final ProductPreset selectProductPresetByProductAndAccount(Main main, Integer productId, Account account) {
    return (ProductPreset) AppService.single(main, ProductPreset.class, "select * from scm_product_preset where product_id = ? and account_id = ?", new Object[]{productId, account.getId()});
  }

  public static boolean presetIsDeletable(Main main, ProductPreset productPreset) {
    String sql = "select '1' from scm_product_detail where product_preset_id = ?";
    return AppService.exist(main, sql, new Object[]{productPreset.getId()});
  }

  public static List<ProductPreset> selectProductPresetByProduct(Main main, Product product) {
    List<ProductPreset> presetList = null;
    main.clear();
    if (product != null && product.getId() != null) {
      String sql = " select * from scm_product_preset where product_id = ? ";
      main.param(product.getId());
      if (product.getAcccount() != null && product.getAcccount().getId() != null) {
        sql += " AND account_id = ? ";
        main.param(product.getAcccount().getId());
      }
      presetList = AppService.list(main, ProductPreset.class, sql, main.getParamData().toArray());
    }
    return presetList;
  }
}
