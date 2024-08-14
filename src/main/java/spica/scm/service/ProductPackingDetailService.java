/*
 * @(#)ProductPackingDetailService.java	1.0 Mon May 21 17:45:02 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductPackingDetail;
import spica.scm.validate.ProductPackingDetailIs;

/**
 * ProductPackingDetailService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon May 21 17:45:02 IST 2018
 */
public abstract class ProductPackingDetailService {

  /**
   * ProductPackingDetail paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductPackingDetailSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_packing_detail", ProductPackingDetail.class, main);
    sql.main("select scm_product_packing_detail.id,scm_product_packing_detail.title,"
            + "scm_product_packing_detail.pack_primary,scm_product_packing_detail.pack_secondary,"
            + "scm_product_packing_detail.pack_tertiary,scm_product_packing_detail.pack_tertiary_secondary_qty,"
            + "scm_product_packing_detail.primary_dimension_unit_qty,scm_product_packing_detail.primary_dimension_group_qty,"
            + "scm_product_packing_detail.pack_secondary_primary_qty,scm_product_packing_detail.pack_primary_dimension,scm_product_packing_detail.created_by,"
            + "scm_product_packing_detail.modified_by,scm_product_packing_detail.created_at,scm_product_packing_detail.modified_at "
            + "from scm_product_packing_detail scm_product_packing_detail "); //Main query
    sql.count("select count(scm_product_packing_detail.id) as total from scm_product_packing_detail scm_product_packing_detail "); //Count query
    sql.join("left outer join scm_product_packing scm_product_packing_detailprimary_product_packing_id on (scm_product_packing_detailprimary_product_packing_id.id = scm_product_packing_detail.pack_primary) left outer join scm_product_packing scm_product_packing_detailsecondary_product_packing_id on (scm_product_packing_detailsecondary_product_packing_id.id = scm_product_packing_detail.pack_secondary) left outer join scm_product_packing scm_product_packing_detailtertiary_product_packing_id on (scm_product_packing_detailtertiary_product_packing_id.id = scm_product_packing_detail.pack_tertiary)"); //Join Query

    sql.string(new String[]{"scm_product_packing_detail.title", "scm_product_packing_detailprimary_product_packing_id.title", "scm_product_packing_detailsecondary_product_packing_id.title", "scm_product_packing_detailtertiary_product_packing_id.title", "scm_product_packing_detail.created_by", "scm_product_packing_detail.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_packing_detail.id", "scm_product_packing_detail.pack_tertiary_secondary_qty", "scm_product_packing_detail.pack_secondary_primary_qty", "scm_product_packing_detail.pack_primary_dimension"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_packing_detail.created_at", "scm_product_packing_detail.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductPackingDetail.
   *
   * @param main
   * @return List of ProductPackingDetail
   */
  public static final List<ProductPackingDetail> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductPackingDetailSqlPaged(main));
  }

//  /**
//   * Return list of ProductPackingDetail based on condition
//   * @param main
//   * @return List<ProductPackingDetail>
//   */
//  public static final List<ProductPackingDetail> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductPackingDetailSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductPackingDetail by key.
   *
   * @param main
   * @param productPackingDetail
   * @return ProductPackingDetail
   */
  public static final ProductPackingDetail selectByPk(Main main, ProductPackingDetail productPackingDetail) {
    return (ProductPackingDetail) AppService.find(main, ProductPackingDetail.class, productPackingDetail.getId());
  }

  /**
   * Insert ProductPackingDetail.
   *
   * @param main
   * @param productPackingDetail
   */
  public static final void insert(Main main, ProductPackingDetail productPackingDetail) {
    ProductPackingDetailIs.insertAble(main, productPackingDetail);  //Validating
    AppService.insert(main, productPackingDetail);

  }

  /**
   * Update ProductPackingDetail by key.
   *
   * @param main
   * @param productPackingDetail
   * @return ProductPackingDetail
   */
  public static final ProductPackingDetail updateByPk(Main main, ProductPackingDetail productPackingDetail) {
    ProductPackingDetailIs.updateAble(main, productPackingDetail); //Validating
    return (ProductPackingDetail) AppService.update(main, productPackingDetail);
  }

  /**
   * Insert or update ProductPackingDetail
   *
   * @param main
   * @param productPackingDetail
   */
  public static void insertOrUpdate(Main main, ProductPackingDetail productPackingDetail) {
    if (productPackingDetail.getId() == null) {
      insert(main, productPackingDetail);
    } else {
      updateByPk(main, productPackingDetail);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productPackingDetail
   */
  public static void clone(Main main, ProductPackingDetail productPackingDetail) {
    productPackingDetail.setId(null); //Set to null for insert
    insert(main, productPackingDetail);
  }

  /**
   * Delete ProductPackingDetail.
   *
   * @param main
   * @param productPackingDetail
   */
  public static final void deleteByPk(Main main, ProductPackingDetail productPackingDetail) {
    ProductPackingDetailIs.deleteAble(main, productPackingDetail); //Validation
    AppService.delete(main, ProductPackingDetail.class, productPackingDetail.getId());
  }

  /**
   * Delete Array of ProductPackingDetail.
   *
   * @param main
   * @param productPackingDetail
   */
  public static final void deleteByPkArray(Main main, ProductPackingDetail[] productPackingDetail) {
    for (ProductPackingDetail e : productPackingDetail) {
      deleteByPk(main, e);
    }
  }
}
