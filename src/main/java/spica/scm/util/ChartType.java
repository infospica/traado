/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.util;

/**
 *
 * @author sujesh
 */
public enum ChartType {
  SALES_INVOICE("Sales Invoice"), PURCHASE("Purchase"), SALES_SERVICE("Sales Service Invoice");

  private String type;

  ChartType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

}
