/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.function;

import spica.fin.domain.TaxCode;

/**
 *
 * @author anoop
 */
public interface CessProcessor {

  public Double processSalesInvoiceCess(TaxCode taxCode, Double taxableAmount);
}
