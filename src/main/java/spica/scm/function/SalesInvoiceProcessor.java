/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
