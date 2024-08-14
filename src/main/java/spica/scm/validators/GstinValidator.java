/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
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
@FacesValidator("spica.scm.validators.GstinValidator")
public class GstinValidator implements Validator {

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String strValue = (String) value;
    if (!StringUtil.isEmpty(strValue)) {
      if (!AppUtil.isValidGstin(strValue)) {
        Jsf.error(component, "error.invalid.gstin");
      }
    }
  }
}
