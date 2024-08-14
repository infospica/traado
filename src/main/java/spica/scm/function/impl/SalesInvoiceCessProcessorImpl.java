/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.function.impl;

import spica.fin.domain.TaxCode;
import spica.scm.function.CessProcessor;

/**
 *
 * @author anoop
 */
public class SalesInvoiceCessProcessorImpl implements CessProcessor {


  /**
   *
   * @param salesInvoice
   * @param salesInvoiceItems
   * @return
   */
  @Override
  public Double processSalesInvoiceCess(TaxCode taxCode, Double taxableValue) {
    Double cessValue = 0.0;

    if (taxCode != null && taxableValue != null) {
      if (taxCode.getRatePercentage() > 0) {
        cessValue = taxCode.getRatePercentage() * taxableValue / 100;
      }
    }
    return cessValue;
  }
}
