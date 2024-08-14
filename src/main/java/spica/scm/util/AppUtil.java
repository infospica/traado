/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.app.faces.MainView;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Anoop Jayachandran
 */
public class AppUtil {

  public static final String PAN_REGEX = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
  public static final Pattern PAN_PATTERN = Pattern.compile(PAN_REGEX);

  /**
   * Method to check the given string is a valid PAN or not.
   *
   * Validation criteria - 1. PAN text is 10 character length String. 2. First 5 character must be alphabetical characters i.e A to Z. 3. Characters from 6 to 9 is numerical
   * characters. 4. The 10th character must be a alphabet.
   *
   * @param value
   * @return
   */
  public static final boolean isValidPan(String value) {
    if (!StringUtil.isEmpty(value)) {
      return PAN_PATTERN.matcher(value.toUpperCase()).matches();
    }
    return false;
  }

  /**
   * Method to validate the given string is a valid GSTIN or not.
   *
   * Sample GSTIN - 32AAAFZ5554AIZJ
   *
   * Validation criteria - 1. GSTIN text is 15 character string value. 2. The 2 character must be a numerical characters. 3. The character from 3 to 12 represents the PAN text. 4.
   * The 14th character must be the character 'Z'.
   *
   * @param gstin
   * @return
   */
  public static final boolean isValidGstin(String gstin) {
    boolean valid = false;
    String value;
    if (!StringUtil.isEmpty(gstin)) {
      if (gstin.length() == 15) {
        value = gstin.substring(0, 2);
        if (MathUtil.isNumericWithRegex(value)) {
          value = gstin.substring(2, 12);
          if (isValidPan(value.toUpperCase())) {
            if (gstin.substring(13, 14).equalsIgnoreCase("z")) {
              valid = true;
            }
          }
        }
      }
    }
    return valid;
  }

  /**
   * <pre>
   * daysBetween(1-2-2017,1-4-2017); //days between
   * </pre>
   *
   * @param from
   * @param to
   * @param addToFromDate
   * @return
   */
  public static long daysBetween(Date from, Date to) {
    Calendar fCal = Calendar.getInstance();
    fCal.setTime(from);
    LocalDate fld = LocalDate.of(fCal.get(Calendar.YEAR), fCal.get(Calendar.MONTH) + 1, fCal.get(Calendar.DATE));
    fCal.setTime(to);
    LocalDate tld = LocalDate.of(fCal.get(Calendar.YEAR), fCal.get(Calendar.MONTH) + 1, fCal.get(Calendar.DATE));
    return ChronoUnit.DAYS.between(fld, tld);
  }

  public static void referenceError(MainView main, Throwable t, Integer id) {
    if (t.getMessage() != null) {
      if (t.getMessage().contains("still referenced from table \"scm_stock_saleable")) {
        main.error("used.in", new String[]{" in Sales"});
      } else if (t.getMessage().contains("still referenced from table \"scm_purchase_return_item")) {
        main.error("used.in", new String[]{" in Purchase Return"});
      } else if (t.getMessage().contains("still referenced from table \"scm_sales_invoice_item")) {
        main.error("used.in", new String[]{" in Sales"});
      }
    }
  }

  public static void referenceError(Main main, String invoiceNo, String existing) {
    if (!StringUtil.isEmpty(existing)) {
      if (existing.equals(SystemConstants.ACCOUNTING_TRANSACTION_SETTLEMENT)) {
        throw new UserMessageException("used.settlement.in", new String[]{"in " + invoiceNo});
      }
      if (invoiceNo.equals(SystemConstants.LINE_ITEM)) {
        throw new UserMessageException("used.lineItem.in", new String[]{"in " + existing});
      } else {
        throw new UserMessageException("used.in", new String[]{invoiceNo, "in " + existing});
      }
    }
  }
}
