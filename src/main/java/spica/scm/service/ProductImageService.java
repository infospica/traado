/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Product;
import spica.scm.domain.ProductImage;
import wawo.app.Main;
import wawo.app.common.AppService;

/**
 *
 * @author sujesh
 */
public abstract class ProductImageService {

  public static List selectProductImagesByProduct(Main main, Product product, Integer companyId) {
    List<ProductImage> imageList;
    imageList = AppService.list(main, ProductImage.class, " SELECT * FROM scm_product_image WHERE product_id = ? AND company_id = ? ", new Object[]{product.getId().intValue(), companyId.intValue()});
    return imageList;
  }

  public static void deleteById(Main main, Integer id) {
    AppService.deleteSql(main, ProductImage.class, " DELETE FROM scm_product_image WHERE id = ? ", new Object[]{id});
  }

  public static void saveOrUpdate(Main main, ProductImage productImage) {
    if (productImage.getId() == null) {
      AppService.insert(main, productImage);
    } else {
      AppService.update(main, productImage);
    }
  }

}
