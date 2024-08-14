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
import spica.fin.common.ChequeParty;
import wawo.entity.util.StringUtil;

/**
 *
 * @author fify
 */
@FacesConverter("spica.scm.converter.ChequePartyConverter")
public class ChequePartyConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    value = getAsString(context, component, value);

    if (!StringUtil.isEmpty(value)) {
      String result = null;
      String[] part = value.split("_");
      ChequeParty cp = new ChequeParty();
      result = part[0];
      if (!"0".equals(result)) {
        cp.setId(Integer.valueOf(result));
      }

      result = part[1];
      if (!"0".equals(result)) {
        cp.setPartyType(String.valueOf(result));
      }
      return cp;
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
