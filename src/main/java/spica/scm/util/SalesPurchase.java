/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.util;

/**
 *
 * @author sujesh
 */
public class SalesPurchase {

  private Double sale;
  private Double purchase;
  private Double salesService;

  public SalesPurchase() {
  }

  public SalesPurchase(Double sale, Double purchase) {
    this.sale = sale;
    this.purchase = purchase;
  }

  public SalesPurchase(Double sale, Double purchase, Double salesService) {
    this.sale = sale;
    this.purchase = purchase;
    this.salesService = salesService;
  }

  public Double getSale() {
    return sale;
  }

  public void setSale(Double sale) {
    this.sale = sale;
  }

  public Double getPurchase() {
    return purchase;
  }

  public void setPurchase(Double purchase) {
    this.purchase = purchase;
  }

  public Double getSalesService() {
    return salesService;
  }

  public void setSalesService(Double salesService) {
    this.salesService = salesService;
  }

}
