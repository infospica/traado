/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import wawo.app.faces.Jsf;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-3
 */
@FacesValidator("spica.scm.validators.PhoneValidator")
public class PhoneValidator implements Validator {

  private static final String PHONE_PATTERN = "^[+]?[0-9]{10,13}+$";

  private final Pattern pattern;
  private Matcher matcher;

  public PhoneValidator() {
    pattern = Pattern.compile(PHONE_PATTERN);
  }

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String strValue = (String) value;
    if (!StringUtil.isEmpty(strValue)) {
      matcher = pattern.matcher(strValue.toUpperCase());
      if (!matcher.matches()) {
        Jsf.error(component, "error.invalid.phone.no");
      }
    }
  }
}
