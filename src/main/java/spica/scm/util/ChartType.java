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
