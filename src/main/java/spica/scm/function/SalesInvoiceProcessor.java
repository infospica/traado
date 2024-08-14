/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.function;

import java.util.List;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;

/**
 *
 * @author anoop
 */
public interface SalesInvoiceProcessor {

  public void processSalesInvoice(SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItems, boolean draftView);
}
