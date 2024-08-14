/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.util;

import java.io.Serializable;
import java.util.List;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;

/**
 *
 * @author java-2
 */
public class SalesSummery implements Serializable {

  private SalesInvoice salesInvoice;
  private List<SalesInvoiceItem> salesInvoiceItem;

  public SalesSummery() {
  }

  public SalesSummery(SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItem) {
    this.salesInvoice = salesInvoice;
    this.salesInvoiceItem = salesInvoiceItem;
  }

  public SalesInvoice getSalesInvoice() {
    return salesInvoice;
  }

  public void setSalesInvoice(SalesInvoice salesInvoice) {
    this.salesInvoice = salesInvoice;
  }

  public List<SalesInvoiceItem> getSalesInvoiceItem() {
    return salesInvoiceItem;
  }

  public void setSalesInvoiceItem(List<SalesInvoiceItem> salesInvoiceItem) {
    this.salesInvoiceItem = salesInvoiceItem;
  }
}
