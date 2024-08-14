/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.util;

import java.util.regex.Pattern;

/**
 *
 * @author Anoop Jayachandran
 */
public class MathUtil {

  private static final String NUMERIC_REGEX = "-?\\d+";
  private static final Pattern NUMERIC_PATTERN = Pattern.compile(NUMERIC_REGEX);

  /**
   * Method to round the float values.
   *
   * Eg., roundOff(22.065825436408973, 2); will return the value 22.07.
   *
   * @param doubleValue
   * @param decimalPalces
   * @return
   */
  public static final double roundOff(double doubleValue, int decimalPalces) {
    if (decimalPalces < 0) {
      throw new IllegalArgumentException();
    }
    long decimalFactor = (long) Math.pow(10, decimalPalces);
    doubleValue = doubleValue * decimalFactor;
    long value = Math.round(doubleValue);
    return (double) value / decimalFactor;
  }

  /**
   * Method to check the given String is numeric or not.
   *
   * @param value
   * @return
   */
  public static final boolean isNumericWithRegex(String value) {
    return value != null && NUMERIC_PATTERN.matcher(value).matches();
  }

  /**
   * Method to check the given String is numeric or not.
   *
   * @param value
   * @return
   */
  public static final boolean isNumeric(String value) {
    if (value == null || value.isEmpty()) {
      return false;
    }
    int i = 0;
    if (value.charAt(0) == '-') {
      if (value.length() > 1) {
        i++;
      } else {
        return false;
      }
    }
    for (; i < value.length(); i++) {
      if (!Character.isDigit(value.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
