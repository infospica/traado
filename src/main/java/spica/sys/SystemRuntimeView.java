/*
 *  @(#)SystemRuntimeView            3 Jun, 2016
 * 
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 * 
 */
package spica.sys;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import static spica.sys.SystemRuntimeConfig.SDF_MMM_YY;
import wawo.app.config.AppConfig;
import wawo.app.faces.Jsf;

/**
 * Provide system level runtime features at view tier.
 *
 * @author <a href=mailto:arun.vc@infospica.com>Arun VC</a>
 */
@Named(value = "sysRuntimeView")
@ApplicationScoped
public class SystemRuntimeView implements Serializable {

  @Inject
  @Push
  PushContext applicationChannel;

  public SystemRuntimeView() {
    AppConfig.maxDisplayText = 85;
  }

  public void pushDownTime5() {
    pushToApplicationChannel("Application update in 5 minutes.<br/> Save your work.");
  }

  public void pushDownTime1() {
    pushToApplicationChannel("Application update in 1 minutes.<br/> Save your work.");
  }

  public void pushToApplicationChannel(String message) {
    applicationChannel.send(message);
  }

  public static String refUrl(String page) {
    return refUrl(page, "list");
  }

  public static String refUrl(String page, String viewType) {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      return Jsf.popupUrl(page, viewType);
    } else {
      return null;
    }
  }

  public boolean isBranchAsCompany() {
    return SystemRuntimeConfig.BRANCH_AS_COMPANY;
  }

  public boolean isBranchAsInfoTab() {
    return SystemRuntimeConfig.BRANCH_AS_INFO_TAB;
  }

  public boolean isCompanyLicense() {
    return SystemRuntimeConfig.COMPANY_LICENSE;
  }

  public boolean isCompanyLicenseTab() {
    return SystemRuntimeConfig.COMPANY_LICENSE_TAB;
  }

  public String getGrid4() {
    return "col-lg-2 col-md-2 col-sm-4 control-label,col-lg-2 col-md-4 col-sm-7,col-lg-2 col-md-2 col-sm-4 control-label,col-lg-2 col-md-4 col-sm-7";
  }

  public String getGrid2Small() {
    return "col-md-2 col-sm-4 control-label,col-md-4 col-sm-7, col-md-2 col-sm-4 control-label,col-md-4 col-sm-7";
  }

  public String getGrid2Medium() {
    return "col-md-4 col-sm-4 control-label,col-md-8 col-sm-8";
  }

  public String getGrid2() {
    return "col-md-6 col-sm-6,col-md-6 col-sm-6";
  }

  public String getGrid4Uniform() {
    return "col-md-4 col-sm-4,col-md-4 col-sm-4, col-md-4 col-sm-4,col-md-4 col-sm-4, col-md-4 col-sm-4,col-md-4 col-sm-4, col-md-4 col-sm-4,col-md-4 col-sm-4,col-md-4 col-sm-4,col-md-4 col-sm-4";
  }

  /**
   * Method to convert date to string in Mon-YY format.
   *
   * @param expiryDate
   * @return
   */
  public String dateToMMYY(Date expiryDate) {
    String dateString = "";
    if (expiryDate != null) {
      dateString = SDF_MMM_YY.format(expiryDate);
    }
    return dateString;
  }

  public String statusStyle(Integer status) {
    if (status == null) {
      return "";
    }
    if (status == SystemConstants.CONFIRMED) {//Confirmed
      return "label";
    } else if (status == SystemConstants.DRAFT) {
      return "label";
    } else if (status == SystemConstants.CANCELLED) {
      return "label";
    } else if (status == SystemConstants.EXPIRED) {
      return "label";
    } else if (status == SystemConstants.CONFIRMED_PARTIALYY_PROCESSED) {
      return "label";
    } else if (status == SystemConstants.CONFIRMED_AND_PROCESSED) {
      return "label";
    } else if (status == SystemConstants.CONFIRMED_AND_PLANNED) {
      return "label";
    } else if (status == SystemConstants.CONFIRMED_AND_PART_PENDING) {
      return "label";
    } else if (status == SystemConstants.CONFIRMED_AND_PACKED) {
      return "label";
    }
    return "";
  }

  public String statusColor(Integer status) {
    return SystemConstants.statusColor(status);
  }

  public String statusColorYesNo(Integer status) {

    if (status == null) {
      return "";
    }
    if (status == 1) {//Confirmed
      return "#09a77b";
    } else if (status == 0 || status == 2 || status == 3) {
      return "#fd4921";
    }

    return "yellow";
  }

  public void dateLteToday(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    if (null != value) {
      Date date = (Date) value;
      Date from = getNow().getTime();
      if (date.after(new Date()) && !date.toString().equals(from.toString())) {
        Jsf.error(component, "date.lte.today", null);
      }
    }
  }

  public void dateGteToday(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    if (null != value) {
      Date date = (Date) value;
      Date from = getNow().getTime();
      if (date.before(new Date()) && !date.toString().equals(from.toString())) {
        Jsf.error(component, "date.gte.today", null);
      }
    }
  }

  public void dateGteDays(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    if (null != value) {
      Date date = (Date) value;
      Date from = getDate("from");
      if (date.before(from) && !date.toString().equals(from.toString())) {
        Jsf.error(component, "date.gte", new String[]{AppConfig.displayDatePattern.format(from)});
      }
    }
  }

  public void dateLteDays(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    if (null != value) {
      Date date = (Date) value;
      Date from = getDate("from");
      if (date.after(from) && !date.toString().equals(from.toString())) {
        Jsf.error(component, "date.lte", new String[]{AppConfig.displayDatePattern.format(from)});
      }
    }
  }

  /**
   * <pre>
   * {@code
   *  <p:calendar validator="#{sysRuntimeView.dateGteDays}" >
   *  <f:attribute name="from" value="-90" /> <!-- date should be greater than -90 days days -->
   *   </p:calendar>
   * </pre> }
   * </pre>
   *
   * param context
   *
   * @param component
   * @param value
   * @throws ValidatorException
   */
  public void dateBetweenDays(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    if (null != value) {
      Date date = (Date) value;
      Date from = getDate("from");
      Date to = getDate("to");
      if ((date.after(from) && !date.toString().equals(from.toString())) && (date.before(to) && date.compareTo(to) != 0)) {
        Jsf.error(component, "date.between", new String[]{AppConfig.displayDatePattern.format(from), AppConfig.displayDatePattern.format(to)});
      }
    }
  }

  private Date getDate(String param) {
    Integer less = (Integer) Jsf.getAttribute(param);
    Calendar now = getNow();
    now.add(Calendar.DAY_OF_MONTH, less); //Allow only 90 days prior from current day
    return now.getTime();
  }

  private Calendar getNow() {
    Calendar now = Calendar.getInstance();
    now.set(Calendar.HOUR, 0);
    now.set(Calendar.MINUTE, 0);
    now.set(Calendar.SECOND, 0);
    now.set(Calendar.HOUR_OF_DAY, 0);
    return now;
  }
}
