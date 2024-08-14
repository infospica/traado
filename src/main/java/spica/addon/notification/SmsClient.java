/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.addon.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import spica.fin.domain.SmsLog;

import spica.scm.domain.CompanySettings;
import spica.sys.SystemConstants;
import spica.sys.domain.SmsProvider;
import wawo.app.Main;
import wawo.app.common.AppService;

/**
 *
 * @author sujesh
 */
public class SmsClient {

  private static String getCountyCode(CompanySettings setting) {
    return setting.getCompanyId().getCountryId().getPhoneCode();
  }

  /**
   * Retrun a valid mobile number amoung two with country code.if number is not valid return null.
   *
   * @param n1
   * @param n2
   * @param setting
   * @return
   */
  public static String getValidOne(String n1, String n2, CompanySettings setting) {

    String n = getValid(n1, (setting));
    if (n == null) {
      return getValid(n2, (setting));
    }
    return n;
  }

  /**
   * Return a valid number with country code if number is not valid return null.
   *
   * @param number
   * @param setting
   * @return
   */
  public static String getValid(String number, CompanySettings setting) {
    String n = number.replaceAll("[^0-9]", "");
    if (n.length() == 10) {
      return getCountyCode(setting) + n;
    } else if (n.length() < 10) {
      return null;
    } else if (n.length() > 12) {
      return null;
    } else {
      return n;
    }
  }

  public static String getInvoiceMessage(boolean amended, int status, String title, String party, String invoiceNo, String link) {
    if (status == SystemConstants.CONFIRMED) {
      if (amended) {
        return title + " amended for " + party + " " + link;
      } else {
        return title + " generated for " + party + " " + link;
      }
    } else if (status == SystemConstants.CANCELLED) {
      return title + " " + invoiceNo + " Canceled for " + party;
    }
    return null;
  }

  public static String sendSMS(Main main, CompanySettings companySettings, String number, String message, SmsLog log) throws IOException {
//TODO this can be cached
    SmsProvider serviceProvider = (SmsProvider) AppService.single(main, SmsProvider.class, "select * from sys_sms_provider where id = ?", new Object[]{companySettings.getSmsProviderId().getId()});
    String serviceApi = companySettings.getSmsProviderId().getServiceApi();
    serviceApi = serviceApi.replaceAll("#apikey", new String(Base64.getDecoder().decode(serviceProvider.getApiKey())));
    serviceApi = serviceApi.replaceAll("#numbers", number.replaceAll("[^0-9]", "")); //FIXME will it work with multiple numbers with comma
    serviceApi = serviceApi.replaceAll("#message", URLEncoder.encode(message, "UTF-8"));
    serviceApi = serviceApi.replaceAll("#sender", serviceProvider.getSenderId());

    String response = send(serviceApi); //send sms

    companySettings.setSmsUsed(companySettings.getSmsUsed() + 1);
    AppService.update(main, companySettings); // update setting
    if (log != null) {
      log.setCompanyId(companySettings.getCompanyId());  //loging sms
      log.setCreatedAt(new Date());
      log.setSmsProviderId(companySettings.getSmsProviderId());
      log.setResponse(response);
      log.setRecipient(number);
      log.setSenderId(serviceProvider.getSenderId());
      log.setMessage(message);
      AppService.insert(main, log);
    }
    return response;
  }

  public static SmsLog getSmsLog(Integer entityId, Integer entityType, Integer personId, Integer personType) {
    SmsLog log = new SmsLog();
    log.setEntityId(entityId);
    log.setEntityType(entityType);
    log.setPersonId(personId);
    log.setPersonType(personType);
    return log;
  }

  private static String send(String serviceApi) throws IOException {

    HttpURLConnection conn = (HttpURLConnection) new URL(serviceApi).openConnection();
    conn.setConnectTimeout(5000);
    conn.setDoOutput(true);
    conn.setRequestMethod("GET");
    conn.setRequestProperty("accept", "application/json");

    try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      final StringBuilder stringBuffer = new StringBuilder();
      String line;
      while ((line = rd.readLine()) != null) {
        stringBuffer.append(line);
      }
      rd.close();
      return stringBuffer.toString();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }

  }

  public static String getOTPMessage(Integer otp) {
    return otp + " is your OTP to activate your phone number";
  }

}
