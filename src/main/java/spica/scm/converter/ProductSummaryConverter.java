/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import spica.scm.common.ProductSummary;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-2
 */
@FacesConverter("spica.scm.converter.ProductSummaryConverter")
public class ProductSummaryConverter implements Converter {

  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    value = getAsString(context, component, value);

    if (!StringUtil.isEmpty(value) && !value.equals("null")) {
      String result = null;
      String[] part = value.split("_");
      ProductSummary ps = new ProductSummary();
      result = part[0];
      if (!StringUtil.isEmpty(result) && !"0".equals(result)) {
        ps.setProductId(Integer.valueOf(result));
      }

      result = part[1];
      if (!StringUtil.isEmpty(result) && !"0".equals(result)) {
        ps.setProductDetailId(Integer.valueOf(result));
      }

      result = part[2];
      if (!StringUtil.isEmpty(result) && !"0".equals(result)) {
        ps.setProductPresetId(Integer.valueOf(result));
      }

      result = part[3];
      if (!"0".equals(result)) {
        ps.setProductName(result);
      }

      result = part[4];
      if (!"0".equals(result)) {
        ps.setSalesInvoiceId(Integer.valueOf(result));
      }

      result = part[5];
      if (!"0".equals(result)) {
        ps.setProductBatchId(Integer.valueOf(result));
      }

      result = part[6];
      if (!"0".equals(result)) {
        ps.setSchemeDiscountDerived(Double.parseDouble(result));
      }

      result = part[7];
      if (!"0".equals(result)) {
        ps.setProductDiscountDerived(Double.parseDouble(result));
      }
      result = part[8];
      if (!"0".equals(result)) {
        ps.setBatch(result);
      }
      ps.after();

      return ps;
    }
    return null;
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null) {
      return null;
    }
    return value.toString();
  }

}
