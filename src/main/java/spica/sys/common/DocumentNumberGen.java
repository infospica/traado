/*
 *  @(#)DocumentNumberGen            6 Jun, 2017
 * 
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 * 
 */
package spica.sys.common;

import java.util.Calendar;
import java.util.Date;
import spica.scm.common.DocumentNumber;
import wawo.entity.util.StringUtil;

/**
 *
 * @author <a href=mailto:arun.vc@infospica.com>Arun VC</a>
 */
public class DocumentNumberGen {

  /**
   *
   * @param prefix
   * @param finStartDate
   * @return
   */
  public static String getPrefixLiteral(DocumentNumber prefix, Date finStartDate) {
    StringBuilder sb = new StringBuilder();
    sb.append(prefix.getDocPrefix());

    // Appending year sequence
    if (prefix.getYearSequence() == 1) {
      sb.append(getFormattedYear(prefix.getYearPadding())).append("/");
    } else if (prefix.getYearSequence() == 2) {
      sb.append(getFinancialYearFormat(prefix, finStartDate)).append("/");
    }
    sb.append(leftPadLiteral(prefix.getDocNumberCounter().toString(), prefix.getPadding(), '0'));
    return sb.toString();
  }

  /**
   *
   * @param padding
   * @return
   */
  private static String getFinancialYearFormat(DocumentNumber prefix, Date finStartDate) {
    int startYear = 0;
    int endYear = 0;
    Integer padding = prefix.getYearPadding();
    if (padding == null) {
      padding = 1;
    }
//    if (Calendar.getInstance().get(Calendar.MONTH) >= startMonth) {
    startYear = wawo.entity.util.DateUtil.getYear(finStartDate);
    endYear = startYear + 1;
//    } else if (Calendar.getInstance().get(Calendar.MONTH) < startMonth) {
//      endYear = Calendar.getInstance().get(Calendar.YEAR);
//      startYear = endYear - 1;
//    }
    StringBuilder sb = new StringBuilder();
    if (padding == 2) {
      sb.append(toTwo(startYear)); //1 19
      if (prefix.getStartYearOnly() == 0) {// 0 Append start and end fin year date  1920
        sb.append(toTwo(endYear));
      }
    } else {
      sb.append(startYear); //2019
      if (prefix.getStartYearOnly() == 0) { //0 = Appends only the start fin year date eg 20192020,
        sb.append(endYear);
      }
    }
    return sb.toString();
  }

  private static String toTwo(int startYear) {
    return String.valueOf(startYear).substring(2);
  }

  /**
   *
   * @param format
   * @return
   */
  private static String getFormattedYear(Integer padding) {
    if (padding == null) {
      padding = 1;
    }
    if (padding == 2) {
      return toTwo(Calendar.getInstance().get(Calendar.YEAR));
    }
    return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
  }

  /**
   * Method to append a character to the left side of a string.
   *
   * @param label
   * @param padding
   * @param paddingLiteral
   * @return
   */
  private static String leftPadLiteral(String label, int padding, char paddingLiteral) {
    if (!StringUtil.isEmpty(label)) {
      int labelLength = label.length();
      while (labelLength < padding) {
        label = paddingLiteral + label;
        labelLength++;
      }
    }
    return label;
  }
}
