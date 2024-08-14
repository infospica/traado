/*
 * 
 * Copyright 2015-2018 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.SalesInvoiceItem;
import wawo.app.faces.Jsf;
import wawo.entity.util.StringUtil;

/**
 *
 * @author arun
 */
@FacesConverter("spica.scm.converter.ProductBatchConverter")
public class ProductBatchConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    value = getAsString(context, component, value);
    SalesInvoiceItem salesInvoiceItem = (SalesInvoiceItem) Jsf.getAttribute("selectItem");

    if (salesInvoiceItem != null) {
      if(salesInvoiceItem.getProductDetailSales() == null){
        return g(salesInvoiceItem, value);
      }      
      if (!StringUtil.isEmpty(value) && salesInvoiceItem.getProductDetailSales().toString().equals(value)) {
        salesInvoiceItem.setProductHashChanged(Boolean.FALSE);
        return salesInvoiceItem.getProductDetailSales();
      }
      return g(salesInvoiceItem, value);

    }
    return null;
  }
  
  private Object g(SalesInvoiceItem salesInvoiceItem, String value){
           salesInvoiceItem.setProductHashChanged(Boolean.TRUE);
      if (!StringUtil.isEmpty(salesInvoiceItem.getProductDetailSalesList())) {
        if (!StringUtil.isEmpty(value)) {
          for (ProductDetailSales productDetails : salesInvoiceItem.getProductDetailSalesList()) {
            if (value.equals(productDetails.getProductHash())) {

              return productDetails;
            }
          }
        }
      } else {
        return salesInvoiceItem.getProductDetailSales();
      }
      return null;
  }

  public String getAsString(FacesContext fc, UIComponent uic, Object value) {
    if (value == null) {
      return null;
    }
//    if("null".equals(value.toString())){
//       SalesInvoiceItem salesInvoiceItem = (SalesInvoiceItem) Jsf.getAttribute("selectItem");
//       return salesInvoiceItem.getProductDetailSales().toString();
//    }
    return value.toString();
  }
}
