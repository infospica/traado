/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.validators;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import spica.scm.util.AppUtil;
import wawo.app.faces.Jsf;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-2
 */
@FacesValidator("spica.scm.validators.PanCardValidator")
public class PanCardValidator implements Validator {

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String strValue = (String) value;
    if (!StringUtil.isEmpty(strValue)) {
      if (!AppUtil.isValidPan(strValue)) {
        Jsf.error(component, "error.invalid.pan.no");
      }
    }
  }
}