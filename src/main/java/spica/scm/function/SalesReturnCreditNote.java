/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.function;

import spica.scm.domain.SalesReturn;
import wawo.app.Main;

/**
 *
 * @author Anoop Jayachandran
 */
public interface SalesReturnCreditNote {

  public void processCreditNote(Main main, SalesReturn salesReturn);
}
