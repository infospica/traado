/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.function;

import spica.scm.domain.SalesReturn;
import spica.scm.tax.TaxCalculator;
import wawo.app.Main;

/**
 *
 * @author Anoop Jayachandran
 */
public interface SalesReturnConfirmation {

  public void confirmSalesReturn(Main main, TaxCalculator taxCalculator, SalesReturn salesReturn);
}
