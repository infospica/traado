/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.view;

import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import spica.scm.domain.Account;
import spica.scm.service.AccountService;
import spica.scm.service.AccountStatusService;
import spica.scm.service.VendorService;
import static spica.sys.SystemConstants.INTERSTATE_PURCHASE;
import static spica.sys.SystemConstants.INTRASTATE_PURCHASE;

/**
 *
 * @author java-2
 */
@Named(value = "accountUtilView")
@ApplicationScoped
public class AccountUtil implements Serializable {
//FIXME thing of better naming for this file and its usage

  public final static int TRADE_TYPE_MARKETER = 1;
  public final static int TRADE_TYPE_CSA = 2;
  public final static int TRADE_TYPE_CF = 3;
  public final static int TRADE_TYPE_SS = 4;

  private final static Integer FIRST_PURCHASE = 1;
//  private final static Integer SECOND_PURCHASE = 2;

  // private final static Integer INTERSTATE_PURCHASE = 1;
  // private final static Integer INTRASTATE_PURCHASE = 2;
  public AccountUtil() {
  }

  /**
   * Method to check trading type.
   *
   * @param account
   * @return
   */
  public static boolean isSuperStockiest(Account account) {
    return (account != null && account.getCompanyTradeProfileId() != null && account.getCompanyTradeProfileId().getId() == TRADE_TYPE_SS);
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isConsigneeSalesAgent(Account account) {
    return (account != null && account.getCompanyTradeProfileId() != null && account.getCompanyTradeProfileId().getId() == TRADE_TYPE_CSA);
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isCarryingForwardingAgent(Account account) {
    return (account != null && account.getCompanyTradeProfileId() != null && account.getCompanyTradeProfileId().getId() == TRADE_TYPE_CF);
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isInterStatePurchase(Account account) {
    if (account != null) {
      return (INTERSTATE_PURCHASE.intValue() == account.getIsPurchaseInterstate());
    }
    return false;
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isIntraStatePurchase(Account account) {
    return (account != null && INTRASTATE_PURCHASE.intValue() == (account.getIsPurchaseInterstate()));
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isFirstPurchase(Account account) {
    return (FIRST_PURCHASE.equals(account.getPurchaseLevel()));
  }

  public static Integer getBusinessArea(Account account) {
    if (isInterStatePurchase(account)) {
      return INTERSTATE_PURCHASE;
    } else if (isIntraStatePurchase(account)) {
      return INTRASTATE_PURCHASE;
    }
    return null;
  }

  /**
   *
   * @param account
   * @return
   */
  public static String getPurchaseType(Account account) {
    if (isSuperStockiest(account) && isInterStatePurchase(account)) {
      return "C-Form";
    } else if ((isCarryingForwardingAgent(account) || isConsigneeSalesAgent(account)) && isInterStatePurchase(account)) {
      return "F-Form";
    }
    return "Local Purchase";
  }

  /**
   *
   * @param account
   * @return
   */
  public static String getVendorName(Account account) {
    return account.getVendorId().getVendorName();
  }

  /**
   *
   * @param account
   * @return
   */
  public static String getVendorTin(Account account) {
    return account.getTin();
  }

  /**
   *
   * @param account
   * @return
   */
  public static String getGstin(Account account) {
    return account.getGstNo();
  }

  /**
   *
   * @param account
   * @return
   */
  public static String getTradeProfileTitle(Account account) {
    return (account != null && account.getCompanyTradeProfileId() != null) ? account.getCompanyTradeProfileId().getTitle() : "";
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isDraftAccount(Account account) {
    return (account != null && account.getAccountStatusId() != null && AccountStatusService.ACCOUNT_STATUS_DRAFT.equals(account.getAccountStatusId().getId()));
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isAccountActivable(Account account) {
    return (account != null && account.getId() != null && AccountStatusService.ACCOUNT_STATUS_DRAFT.equals(account.getAccountStatusId().getId()));
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isActiveAccount(Account account) {
    return (account != null && account.getId() != null && AccountStatusService.ACCOUNT_STATUS_ACTIVE.equals(account.getAccountStatusId().getId()));
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isPrimaryVendorAccount(Account account) {
    return (account != null && account.getVendorId() != null && VendorService.VENDOR_TYPE_PRIMARY.equals(account.getVendorId().getVendorType()));
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isScondaryVendorAccount(Account account) {
    return (account != null && account.getVendorId() != null && VendorService.VENDOR_TYPE_SECONDARY.equals(account.getVendorId().getVendorType()));
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isAccountSuspended(Account account) {
    return (account != null && account.getAccountStatusId() != null && AccountStatusService.ACCOUNT_STATUS_INACTIVE.equals(account.getAccountStatusId().getId()));
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isIndividualPurchaseChannel(Account account) {
    return (account != null && account.getPurchaseChannel() != null && AccountService.INDIVIDUAL_SUPPLIER.equals(account.getPurchaseChannel()));
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isRenderPtr(Account account) {
    return (account != null && account.getRenderPtr() != null && AccountService.RENDER_PTR.equals(account.getRenderPtr()));
  }

  /**
   *
   * @param account
   * @return
   */
  public static boolean isRenderPts(Account account) {
    return (account != null && account.getRenderPts() != null && AccountService.RENDER_PTS.equals(account.getRenderPts()));
  }
}
