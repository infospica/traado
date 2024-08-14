/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.addon.notification;

import java.io.IOException;
import spica.addon.service.DocumentService;
import spica.constant.AccountingConstant;
import spica.fin.domain.SmsLog;
import spica.scm.domain.Company;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.DocumentStore;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.SalesInvoice;
import spica.sys.SystemConstants;
import spica.sys.domain.User;
import wawo.app.Main;
import wawo.app.config.AppConfig;
import wawo.app.faces.Jsf;
import wawo.entity.core.AppIo;
import wawo.mail.smtp.SmtpMail;

/**
 *
 * @author arun
 */
public class Notify {

  private static String getDocUrl(String uid) {
    return " " + Jsf.getHttpRequest().getScheme() + "://" + Jsf.getHttpRequest().getServerName() + "/" + SystemConstants.SMS_HOST_SUFFIX + uid;
  }

  public static void salesInvoice(Main main, SalesInvoice si, String option, boolean isCustomer, String filePath) throws IOException {
    // if file path then its new record confirm for creating a new document,
    // if file path is null then its for sending an existing sales invoice.
    String title = "Sales Invoice of ";
    String subject = title + si.getCustomerId().getCustomerName();
    String link;
    String smsContent;
    DocumentStore documentStore = DocumentService.selectByEntityId(main, si.getId(), AccountingConstant.ACC_ENTITY_SALES.getId(), si.getCompanyId().getId());
    if (filePath == null) { // will be null for custom send mail and sms from a button
      if (documentStore == null) {
        Jsf.warn("no.document.generated"); //May be in future to generate the docuemtn
        return;
      }
      link = getDocUrl(documentStore.getUniqueId());
      smsContent = subject + link;
    } else { // message for sending while print and confirm      
      documentStore = getDocumentData(documentStore, si.getId(), AccountingConstant.ACC_ENTITY_SALES.getId(), filePath, si.getSalesInvoiceStatusId().getId(), si.getCompanyId());
      DocumentService.insertOrUpdate(main, documentStore);
      link = getDocUrl(documentStore.getUniqueId());
      smsContent = SmsClient.getInvoiceMessage(si.getConfirmedBy() != null, documentStore.getStatus(), title, si.getCustomerId().getCustomerName(), si.getInvoiceNo(), link);
    }

    String mailContent = getMailContent(title, si.getCustomerId().getCustomerName(), si.getInvoiceNo(), link);
    CompanySettings setting = si.getCompanyId().getCompanySettings();
    if (setting == null) {
      throw new NullPointerException("Company setting is null");
    }
    if (!"localhost".equals(Jsf.getHttpRequest().getServerName())) { //Not sending mails for localhost TODO allow for only approved domains
      if (isCustomer) {
        notifySms(1, setting.getSmsSalesInvoice(), si.getId(), AccountingConstant.ACC_ENTITY_SALES.getId(), si.getCustomerId().getId(), AccountingConstant.ACC_ENTITY_CUSTOMER.getId(), si.getCustomerAddressId().getPhone1(), si.getCustomerAddressId().getPhone2(), smsContent, setting, option, main);
        notifyEmail(main, si.getCustomerAddressId().getEmail(), subject, mailContent, documentStore.getFilePath(), setting, option, true);
      } else if (si.getCompanySalesAgentPersonProfileId() != null) {
        notifySms(si.getCompanySalesAgentPersonProfileId().getPhoneVerified(), setting.getSmsSalesInvoice(), si.getId(), AccountingConstant.ACC_ENTITY_SALES.getId(), si.getCompanySalesAgentPersonProfileId().getId(), AccountingConstant.ACC_ENTITY_AGENT.getId(), si.getCompanySalesAgentPersonProfileId().getPhone1(), si.getCompanySalesAgentPersonProfileId().getPhone2(), smsContent, setting, option, main);
        notifyEmail(main, si.getCompanySalesAgentPersonProfileId().getEmail(), subject, mailContent, documentStore.getFilePath(), setting, option, true);
      }
    }
  }

  private static DocumentStore getDocumentData(DocumentStore documentStore, Integer entityId, Integer entityType, String filePath, Integer status, Company company) {

    int statusCode = 2;
    if (SystemConstants.CONFIRMED.intValue() == status) {
      statusCode = 2;
    } else if (SystemConstants.DRAFT.intValue() == status) {
      statusCode = 1;
    } else if (SystemConstants.CANCELLED.intValue() == status) {
      statusCode = 0;
    }

    documentStore = documentStore == null ? new DocumentStore() : documentStore;
    documentStore.setFilePath(filePath);
    documentStore.setStatus(statusCode);
    documentStore.setEntityId(entityId); //sale invoice, sales return etc
    documentStore.setEntityType(entityType);
    documentStore.setUniqueId(company.getId() + "" + entityId + "" + System.currentTimeMillis());
    documentStore.setCompanyId(company);
    return documentStore;
  }

  private static String getMailContent(String invoice, String name, String no, String link) {
    return "<div> " + invoice + " " + name + " <b>" + no + "</b><br/><a href='" + link + "' target='_blank' > Download  </a></div>";
  }

  private static void notifySms(Integer verified, Integer allowed, Integer entityId, Integer entityType, Integer personId, Integer personType, String p1, String p2, String message, CompanySettings setting, String option, Main main) {
    if (SystemConstants.SMS_OPTION.equals(option) || SystemConstants.SMS_EMAIL.equals(option)) {
      try {
        if (setting.getSmsEnabled() == 1) {
          if (setting.getSmsUsed() < setting.getSmsAllowed()) { // Checking the sms quato for this customer
            if (allowed == null || allowed != 1) {
              Jsf.warn("sms.not.enabled");
              return;
            }
            if (verified == null || verified != 1) {
              Jsf.warn("phone.not.verified");
              return;
            }
            String number = SmsClient.getValidOne(p1, p2, setting);
            SmsLog log = SmsClient.getSmsLog(entityId, entityType, personId, personType);
            if (number != null) {
              SmsClient.sendSMS(main, setting, number, message, log);
              Jsf.info("success.send.sms");
            } else {
              Jsf.warn("sms.phone.invalid", new String[]{number});
            }
          } else {
            Jsf.warn("sms.no.balance");
          }
        }// else {
        // Jsf.warn("sms.not.enabled");
        //}
      } catch (Throwable t) {
        main.error(t, "error.send.sms", null);
      }
    }
  }

  private static void notifyEmail(Main main, String emailto, String subject, String content, String filePath, CompanySettings setting, String option, boolean html) {
    if (SystemConstants.EMAIL_OPTION.equals(option) || SystemConstants.SMS_EMAIL.equals(option)) {
      try {
        if (emailto != null) {
          //TODO if the company setting email is empty use Default mail send
          SmtpMail m = new SmtpMail(AppConfig.mailSetting);
          m.setSubject(subject);
          if (html) {
            m.addContentHtml(content);
          } else {
            m.addContentText(content);
          }
          if (filePath != null) {
            m.addAttachment(AppIo.getPrivateFolder("") + filePath);
          }
          m.setToCcBcc(emailto, null, null);
          String senderid = m.send();
          //TODO log to mail send
          Jsf.info("success.send.email");
        } else {
          Jsf.warn("invalid.email");
        }
      } catch (Throwable t) {
        main.error(t, "error.send.email", null);
      }
    }
  }

  public static void otpSalesAgent(Main main, CompanySettings companySettings, SalesAgent salesAgent) {
    if (salesAgent.getOtp() == null) {
      salesAgent.setOtp(getRandomNumber());
    }
    notifySms(1, 1, salesAgent.getId(), AccountingConstant.ACC_ENTITY_AGENT.getId(), salesAgent.getId(), AccountingConstant.ACC_ENTITY_AGENT.getId(), salesAgent.getPhone1(), null, SmsClient.getOTPMessage(salesAgent.getOtp()), companySettings, SystemConstants.SMS_OPTION, main);

  }

  // It will generate 6 digit random Number
  private static Integer getRandomNumber() {
    return Integer.valueOf(new String(wawo.entity.util.RandomGen.getNumeric(6)));
  }

  public static void registration(Main main, User user, String clearPwd) {
    if (clearPwd == null) {
      clearPwd = "";
    }
    notifyEmail(main, user.getEmail(), AppConfig.appName + " :: Registration", "Welcome " + user.getName() + "\n\nYou have been successfully registered to " + AppConfig.appName + ".\n\nPlease Login using your registered email (" + user.getEmail().toLowerCase() + ") and Password " + clearPwd + "\n\nThank you for registering with us.", null, null, SystemConstants.EMAIL_OPTION, false);
  }

  public static void resetPassword(Main main, User user, String clearPwd) {
    if (clearPwd == null) {
      clearPwd = "";
    }
    notifyEmail(main, user.getEmail(), AppConfig.appName + " :: Password Reset", "Dear " + user.getName() + "\n\nYour password has been changed.\n\nPlease Login using your registered email (" + user.getEmail().toLowerCase() + ") and New Password " + clearPwd + "\n\nThank you.", null, null, SystemConstants.EMAIL_OPTION, false);
  }

}
