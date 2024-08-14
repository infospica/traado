/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.sys;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import spica.scm.domain.Company;
import wawo.app.config.AppConfig;
import wawo.app.faces.UserState;

/**
 *
 * @author arun
 */
public class SysDateView extends UserState implements Serializable {

  private Map<Integer, String> monthsMap = new HashMap<Integer, String>();
  private Map<Integer, String> monthsFiscalMap = new LinkedHashMap<Integer, String>();
  // private Map<Integer, String> monthsYearMap = new LinkedHashMap<Integer, String>();
  private int startFiscalMonth = Calendar.MARCH;
  private int startFiscalYear = Calendar.getInstance().get(Calendar.YEAR);
  private int endFiscalYear = Calendar.getInstance().get(Calendar.YEAR);

  //Fragment update on change of company
  public void loadYearComboValues(Company company) {
    loadYearComboValues(company.getFinancialYearStartDate());
  }

  private void loadYearComboValues(Date toDate) {
    startFiscalMonth = Calendar.MARCH;
    startFiscalYear = Calendar.getInstance().get(Calendar.YEAR);
    endFiscalYear = Calendar.getInstance().get(Calendar.YEAR);
    Calendar now = Calendar.getInstance();
    startFiscalMonth = 1;
    int to = getCurrentYear() - 3;
    if (toDate != null) {
      Calendar toCal = Calendar.getInstance();
      toCal.setTime(toDate);
      to = toCal.get(Calendar.YEAR);
      startFiscalMonth = toCal.get(Calendar.MONTH) + 1;
    }

    monthsMap.clear();
    monthsFiscalMap.clear();
    String[] months = new DateFormatSymbols().getMonths();
    for (int i = 1; i < months.length; i++) {
      //monthsMap.put(i, (i < 10 ? "0" : "") + (i + " - " + months[i - 1]));
      monthsMap.put(i, months[i - 1]);
    }

    for (int i = startFiscalMonth; i < months.length; i++) {
      //monthsFiscalMap.put(i, (i < 10 ? "0" : "") + (i + " - " + months[i - 1]));
      monthsFiscalMap.put(i, months[i - 1]);
    }
    if (startFiscalMonth > 1) {
      for (int i = 1; i < startFiscalMonth; i++) {
        monthsFiscalMap.put(i, (i < 10 ? "0" : "") + (i + " - " + months[i - 1]));
        monthsFiscalMap.put(i, months[i - 1]);
      }
    }
    startFiscalMonth = monthsFiscalMap.keySet().iterator().next();
    if (startFiscalMonth > getCurrentMonth()) {
      startFiscalYear--;
    } else {
      endFiscalYear++;
    }
  }

  public int getStartFiscalMonth() {
    return startFiscalMonth;
  }

  public int getStartFiscalYear() {
    return startFiscalYear;
  }

  public Date getStartFiscalDate() {
    Calendar cal = Calendar.getInstance();
    cal.set(startFiscalYear, startFiscalMonth - 1, 1);
    return cal.getTime();
  }

  public Date getEndFiscalDate() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getStartFiscalDate());
    cal.add(Calendar.YEAR, 1);
    cal.add(Calendar.DATE, -1);
    return cal.getTime();
  }

  public Map<Integer, String> getMonthsMap() {
    return monthsMap;
  }

  public Map<Integer, String> getMonthsFiscalMap() {
    return monthsFiscalMap;
  }

  public int getCurrentYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }

  public int getCurrentMonth() {
    return Calendar.getInstance().get(Calendar.MONTH) + 1;
  }

  public int getCurrenFiscalDate() {
    return Calendar.getInstance().get(Calendar.MONTH) + 1;
  }

  private Date minDate;

  public Date getMinDate() {
    if (minDate == null) {
      try {
        minDate = AppConfig.displayDatePattern.parse("01-01-2000");
      } catch (ParseException ex) {
        Logger.getLogger(UserRuntimeView.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return minDate;
  }
}
