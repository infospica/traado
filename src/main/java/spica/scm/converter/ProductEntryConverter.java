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
import spica.scm.domain.ProductEntry;
import spica.scm.service.ProductEntryService;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author Godson Joseph
 */
@FacesConverter("spica.scm.converter.ProductEntryConverter")
public class ProductEntryConverter implements Converter {

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
      if (entityType.equals(ProductEntry.class)) {
        ProductEntry productEntry = ProductEntryService.selectByPk(main, new ProductEntry(Integer.parseInt(value), null));
        if (productEntry != null) {
          if (productEntry.getAccountInvoiceNo() == null && object != null) {
            productEntry = (ProductEntry) object;
          }
        }
        return productEntry;
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }

    return null;
  }
}
