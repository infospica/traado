/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
@FacesValidator("spica.scm.validators.FaxValidator")
public class FaxValidator implements Validator {

  private static final String FAX_PATTERN = "^[+]?[0-9]{10,13}+$";

  private final Pattern pattern;
  private Matcher matcher;

  public FaxValidator() {
    pattern = Pattern.compile(FAX_PATTERN);
  }

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String strValue = (String) value;
    if (!StringUtil.isEmpty(strValue)) {
      matcher = pattern.matcher(strValue.toUpperCase());
      if (!matcher.matches()) {
        Jsf.error(component, "error.invalid.fax.no");
      }
    }
  }
}
