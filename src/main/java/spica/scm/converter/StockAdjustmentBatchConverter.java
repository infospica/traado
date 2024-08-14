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
import spica.scm.common.StockAdjustmentDetail;
import spica.scm.domain.StockAdjustmentItem;
import wawo.app.faces.Jsf;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@FacesConverter("spica.scm.converter.StockAdjustmentBatchConverter")
public class StockAdjustmentBatchConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    value = getAsString(context, component, value);
    StockAdjustmentItem stockAdjustmentItem = (StockAdjustmentItem) Jsf.getAttribute("selectItem");

    if (stockAdjustmentItem != null) {
      if (stockAdjustmentItem.getStockAdjustmentDetail() == null) {
        return g(stockAdjustmentItem, value);
      }
      if (!StringUtil.isEmpty(value) && stockAdjustmentItem.getStockAdjustmentDetail().toString().equals(value)) {
        return stockAdjustmentItem.getStockAdjustmentDetail();
      }
      return g(stockAdjustmentItem, value);

    }
    return null;
  }

  private Object g(StockAdjustmentItem stockAdjustmentItem, String value) {
    if (!StringUtil.isEmpty(stockAdjustmentItem.getStockAdjustmentDetailList())) {
      if (!StringUtil.isEmpty(value)) {
        for (StockAdjustmentDetail stockAdjustmentDetail : stockAdjustmentItem.getStockAdjustmentDetailList()) {
          if (value.equals(stockAdjustmentDetail.toString())) {
            return stockAdjustmentDetail;
          }
        }
      }
    } else {
      return stockAdjustmentItem.getStockAdjustmentDetail();
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
