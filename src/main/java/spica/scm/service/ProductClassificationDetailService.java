/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.ManufactureProduct;
import spica.scm.domain.Product;
import spica.scm.domain.ProductClassification;
import spica.scm.domain.ProductClassificationDetail;
import wawo.app.Main;
import wawo.app.common.AppService;

/**
 *
 * @author godson
 */
public class ProductClassificationDetailService {

  public static final ProductClassificationDetail selectByPk(Main main, ProductClassificationDetail productClassificationDetail) {
    return (ProductClassificationDetail) AppService.find(main, ProductClassificationDetail.class, productClassificationDetail.getId());
  }

  public static final void insert(Main main, ProductClassificationDetail productClassificationDetail) {
    AppService.insert(main, productClassificationDetail);
  }

  public static final ProductClassificationDetail updateByPk(Main main, ProductClassificationDetail productClassificationDetail) {
    return (ProductClassificationDetail) AppService.update(main, productClassificationDetail);
  }

  public static void insertOrUpdate(Main main, ProductClassificationDetail productClassificationDetail) {
    if (productClassificationDetail.getId() == null) {
      insert(main, productClassificationDetail);
    } else {
      updateByPk(main, productClassificationDetail);
    }
  }

  public static final void deleteByPk(Main main, ProductClassificationDetail productClassificationDetail) {
    AppService.delete(main, ProductClassificationDetail.class, productClassificationDetail.getId());
  }

  public static final void deleteByPkArray(Main main, ProductClassificationDetail[] productClassificationDetail) {
    for (ProductClassificationDetail e : productClassificationDetail) {
      deleteByPk(main, e);
    }
  }

  public static void insertOrUpdate(Main main, Product product, List<ProductClassification> productClassificationList) {
    if (product.getId() != null) {
      AppService.deleteSql(main, ManufactureProduct.class, "delete from scm_product_classification_detail where product_id = ?", new Object[]{product.getId()});
      if (productClassificationList != null && !productClassificationList.isEmpty()) {
        for (ProductClassification classification : productClassificationList) {
          insert(main, new ProductClassificationDetail(product, classification));
        }
      }
    }
  }
}
