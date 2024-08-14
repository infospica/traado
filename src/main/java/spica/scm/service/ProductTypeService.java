/*
 * @(#)ProductTypeService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.ServiceCommodity;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProductType;
import spica.scm.domain.Status;
import spica.scm.validate.ProductTypeIs;

/**
 * ProductTypeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class ProductTypeService {  

  /**
   * ProductType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_type", ProductType.class, main);
    sql.main("select scm_product_type.id,scm_product_type.commodity_id,scm_product_type.title,scm_product_type.sort_order,scm_product_type.created_by,scm_product_type.modified_by,scm_product_type.created_at,scm_product_type.modified_at,scm_product_type.status_id,scm_product_type.product_type_code from scm_product_type scm_product_type "); //Main query
    sql.count("select count(scm_product_type.id) as total from scm_product_type scm_product_type "); //Count query
    sql.join("left outer join scm_service_commodity scm_product_typecommodity_id on (scm_product_typecommodity_id.id = scm_product_type.commodity_id) left outer join scm_status scm_product_typestatus_id on (scm_product_typestatus_id.id = scm_product_type.status_id)"); //Join Query

    sql.string(new String[]{"scm_product_typecommodity_id.title", "scm_product_type.title", "scm_product_type.created_by", "scm_product_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_product_type.id", "scm_product_type.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_type.created_at", "scm_product_type.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductType.
   *
   * @param main
   * @return List of ProductType
   */
  public static final List<ProductType> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductTypeSqlPaged(main));
  }

//  /**
//   * Return list of ProductType based on condition
//   * @param main
//   * @return List<ProductType>
//   */
//  public static final List<ProductType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductType by key.
   *
   * @param main
   * @param productType
   * @return ProductType
   */
  public static final ProductType selectByPk(Main main, ProductType productType) {
    return (ProductType) AppService.find(main, ProductType.class, productType.getId());
  }

  /**
   * Insert ProductType.
   *
   * @param main
   * @param productType
   */
  public static final void insert(Main main, ProductType productType) {
    ProductTypeIs.insertAble(main, productType);  //Validating
    AppService.insert(main, productType);

  }

  /**
   * Update ProductType by key.
   *
   * @param main
   * @param productType
   * @return ProductType
   */
  public static final ProductType updateByPk(Main main, ProductType productType) {
    ProductTypeIs.updateAble(main, productType); //Validating
    return (ProductType) AppService.update(main, productType);
  }

  /**
   * Insert or update ProductType
   *
   * @param main
   * @param productType
   */
  public static void insertOrUpdate(Main main, ProductType productType) {
    if (productType.getId() == null) {
      insert(main, productType);
    } else {
      updateByPk(main, productType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productType
   */
  public static void clone(Main main, ProductType productType) {
    productType.setId(null); //Set to null for insert
    insert(main, productType);
  }

  /**
   * Delete ProductType.
   *
   * @param main
   * @param productType
   */
  public static final void deleteByPk(Main main, ProductType productType) {
    ProductTypeIs.deleteAble(main, productType); //Validation
    AppService.delete(main, ProductType.class, productType.getId());
  }

  /**
   * Delete Array of ProductType.
   *
   * @param main
   * @param productType
   */
  public static final void deleteByPkArray(Main main, ProductType[] productType) {
    for (ProductType e : productType) {
      deleteByPk(main, e);
    }
  }

  public static List<ProductType> productTypeAuto(Main main, Integer id) {
    return AppService.list(main, ProductType.class, "select id,title from scm_product_type where commodity_id = ? and status_id=?", new Object[]{id, 1});

  }

  /**
   *
   * @param main
   * @param commodity
   */
  public static void insertGeneralProductType(Main main, ServiceCommodity commodity) {
    ProductType productType = new ProductType();
    productType.setCommodityId(commodity);
    Status status = new Status();
    status.setId(StatusService.STATUS_ACTIVE);
    productType.setStatusId(status);
    productType.setProductTypeCode("GL");
    productType.setTitle("General");
    insert(main, productType);
  }
}
