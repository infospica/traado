/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.validators;

import java.util.Date;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import wawo.app.faces.Jsf;

/**
 *
 * @author java-2
 */
@FacesValidator("calendarStartDateValidator")
public class CalendarStartDateValidator implements Validator {

  @Override
  public void validate(FacesContext fc, UIComponent uic, Object value) throws ValidatorException {
    if (value == null) {
      return;
    }

    Object startDateAttr = uic.getAttributes().get("startDate");
    if (startDateAttr == null) {
      return;
    }

    Date startDate = (Date) startDateAttr;
    Date endDate = (Date) value;
    if (endDate.before(startDate)) {
      Jsf.error(uic, "error.invalid.date");
    }
  }

}
