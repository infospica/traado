/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.validators;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import wawo.app.faces.Jsf;

/**
 *
 * @author sujesh
 */
@FacesValidator("spica.scm.validators.PasswordValidator")
public class PasswordValidator implements Validator{
  
    
  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
       String confirmPassword = (String) value;
    UIInput passwordInput = (UIInput) component.findComponent("newPassword");
    String password = (String) passwordInput.getLocalValue();
        if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
          Jsf.error(component,"error.password.missmatch");
               
    }
  }
}
