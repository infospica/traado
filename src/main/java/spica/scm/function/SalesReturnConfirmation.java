/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
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
