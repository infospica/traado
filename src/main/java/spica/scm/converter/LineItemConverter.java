/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import spica.scm.domain.Brand;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Manufacture;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductClassification;
import spica.scm.domain.ProductPackingDetail;
import spica.scm.domain.ProductType;
import spica.scm.domain.ProductUnit;
import spica.scm.domain.Vendor;
import spica.scm.util.ImportUtil;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author godson
 */
@FacesConverter("spica.scm.converter.LineItemConverter")
public class LineItemConverter implements Converter {

  private static Object object;

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    value = getAsString(context, component, value);
    Class<?> entityType = component.getValueExpression("value").getType(context.getELContext());
    object = (Object) Jsf.getAttribute("param");
    if (value != null && entityType != null) {
      return convertionHelper(value, entityType, object);
    } else {
      return null;
    }
  }

  @Override
  public String getAsString(FacesContext fc, UIComponent uic, Object value) {
    if (value == null) {
      return null;
    }
    return value.toString();
  }

  private Object convertionHelper(String value, Class entityType, Object object) {
    MainView main = Jsf.getMain();
    try {
         if (entityType.equals(Vendor.class)) {
      Vendor vendor = ImportUtil.getVendorbyId(main, value);
      if (vendor.getVendorName() == null && object != null) {
        vendor = (Vendor) object;
      }
      return vendor;
    }
    if (entityType.equals(ServiceCommodity.class)) {
      ServiceCommodity commodity = ImportUtil.getCommoditybyId(main, value);
      if (commodity.getTitle() == null && object != null) {
        commodity = (ServiceCommodity) object;
      }
      return commodity;
    } else if (entityType.equals(Brand.class)) {
      Brand brand = ImportUtil.getBrandbyId(main, value);
      if (brand.getCode() == null && object != null) {
        brand = (Brand) object;
      }
      return brand;
    } else if (entityType.equals(ProductCategory.class)) {
      ProductCategory productCategory = ImportUtil.getProductCategorybyId(main, value);
      if (productCategory.getTitle() == null && object != null) {
        productCategory = (ProductCategory) object;
      }
      return productCategory;
    } else if (entityType.equals(ProductClassification.class)) {
      ProductClassification classification = ImportUtil.getProductClassificationbyId(main, value);
      if (classification.getTitle() == null && object != null) {
        classification = (ProductClassification) object;
      }
      return classification;
    } else if (entityType.equals(ProductUnit.class)) {
      ProductUnit productUnit = ImportUtil.getProductUnitbyId(main, value);
      if (productUnit.getTitle() == null && object != null) {
        productUnit = (ProductUnit) object;
      }
      return productUnit;
    } else if (entityType.equals(ProductPackingDetail.class)) {
      ProductPackingDetail packingDetail = ImportUtil.getProductPackingDetailbyId(main, value);
      if (packingDetail.getTitle() == null && object != null) {
        packingDetail = (ProductPackingDetail) object;
      }
      return packingDetail;
    } else if (entityType.equals(ProductType.class)) {
      ProductType productType = ImportUtil.getProductTypebyId(main, value);
      if (productType.getTitle() == null && object != null) {
        productType = (ProductType) object;
      }
      return productType;
    } else if (entityType.equals(Manufacture.class)) {
      Manufacture manufacture = ImportUtil.getmanufacturebyId(main, value);
      if (manufacture.getCode() == null && object != null) {
        manufacture = (Manufacture) object;
      }
      return manufacture;
    } else if (entityType.equals(Product.class)) {
      Product product = ImportUtil.getProductbyId(main, value);
      if (product.getProductName() == null && object != null) {
        product = (Product) object;
      }
      return product;
    } else if (entityType.equals(ProductBatch.class)) {
      ProductBatch batch = ImportUtil.getProductBatchbyId(main, value);
      if (batch.getBatchNo() == null && object != null) {
        batch.setBatchNo(object.toString());
      }
      return batch;
    }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
 
    return null;
  }
}
