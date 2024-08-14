/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.util;

import java.util.Comparator;
import spica.scm.domain.ProdFreeQtySchemeRange;

/**
 *
 * @author java-2
 */
public class FromRangeComparator implements Comparator {

  @Override
  public int compare(Object object1, Object object2) {
    ProdFreeQtySchemeRange range1 = (ProdFreeQtySchemeRange) object1;
    ProdFreeQtySchemeRange range2 = (ProdFreeQtySchemeRange) object2;

    if (range1.getRangeFrom().equals(range2.getRangeFrom())) {
      return 0;
    } else if (range1.getRangeFrom() > range2.getRangeFrom()) {
      return 1;
    } else {
      return -1;
    }
  }
}
