/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
