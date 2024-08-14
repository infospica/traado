/*
 *  @(#)SystemRuntimeConfig            3 Jun, 2016
 * 
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 * 
 */
package spica.sys;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import spica.scm.domain.Account;
import spica.fin.domain.AccountingLedger;
import spica.scm.domain.ConsignmentStatus;
import spica.scm.domain.ConsignmentType;
import spica.scm.domain.Currency;
import spica.scm.domain.PlatformDescription;
import spica.scm.domain.PlatformSource;
import spica.scm.domain.PlatformStatus;
import spica.scm.domain.TradeProfile;
import spica.scm.domain.TransportMode;
import spica.fin.domain.VendorClaimStatus;
import spica.scm.domain.Company;
import spica.scm.domain.PlatformSettlementType;
import spica.scm.tax.TaxCalculator;
import spica.scm.view.ScmLookupView;
import spica.sys.domain.Role;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 * Application or system constants.
 *
 * @author <a href=mailto:arun.vc@infospica.com>Arun VC</a>
 */
public final class SystemRuntimeConfig {

  /**
   * Enable or hide branch details in company
   */
  static final boolean BRANCH_AS_COMPANY = false;
  static final boolean BRANCH_AS_INFO_TAB = false;
  static final boolean COMPANY_LICENSE = true;
  static final boolean COMPANY_LICENSE_TAB = false;

  // ConsignmentType
  public static final Integer CONSIGNMENT_TYPE_PURCHASE = 1;
  public static final Integer CONSIGNMENT_TYPE_SALES = 2;
  public static final Integer CONSIGNMENT_TYPE_PURCHASE_RETURN = 3;
  public static final Integer CONSIGNMENT_TYPE_SALES_RETURN = 4;

  public static ConsignmentType PURCHASE_CONSIGNMENT_TYPE = new ConsignmentType(CONSIGNMENT_TYPE_PURCHASE);
  public static ConsignmentType SALES_CONSIGNMENT_TYPE = new ConsignmentType(CONSIGNMENT_TYPE_SALES);
  public static ConsignmentType SALES_RETURN_CONSIGNMENT_TYPE = new ConsignmentType(CONSIGNMENT_TYPE_SALES_RETURN);
  public static ConsignmentType PURCHASE_RETURN_CONSIGNMENT_TYPE = new ConsignmentType(CONSIGNMENT_TYPE_PURCHASE_RETURN);

  // Consignment Status
  public static ConsignmentStatus CONSIGNMENT_STATUS_DRAFT = new ConsignmentStatus(1);
  public static ConsignmentStatus CONSIGNMENT_STATUS_RECEIVED_AND_DELIVERED = new ConsignmentStatus(8);

  // Transport Mode
  public static TransportMode TRANSPORT_MODE_BYROAD = new TransportMode(1);
  public static TransportMode TRANSPORT_MODE_BYAIR = new TransportMode(2);
  public static TransportMode TRANSPORT_MODE_BYRAIL = new TransportMode(3);
  public static TransportMode TRANSPORT_MODE_BYSEA = new TransportMode(4);
  public static TransportMode TRANSPORT_MODE_BYCOURIER = new TransportMode(5);

  public static String CONSIGNMENT_PACKING_UNIT[] = {"Box(es)", "Carton(s)", "Case(s)", "Piece"};
  public static String CONSIGNMENT_WEIGHT_UNIT[] = {"Kilogram", "Gram",};
//FIXME godson use this constant everwhere than creating new instance
  public static final SimpleDateFormat SDF_MMM_YY = new SimpleDateFormat("MMM-yy");
  public static final SimpleDateFormat SDF_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
  public static final SimpleDateFormat SDF_DD_MM_YYYY = new SimpleDateFormat("dd-MM-yyyy");
  public static final SimpleDateFormat SDF_DD_MMM_YYYY = new SimpleDateFormat("dd-MMM-yyyy");
  public static final SimpleDateFormat SDF_DD_MM_YYYY_HH_MM = new SimpleDateFormat("dd-MM-yyyy HH:mm");

  static {
    SDF_DD_MM_YYYY_HH_MM.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
  }
  //Menu id
  static final int MENU_BASIC_ID = 1;
  static final int MENU_COMPANY_ID = 6;
  static final int MENU_DASHBOARD_ID = 7;
  static final int MENU_SECURITY_ID = 55;
  static final int MENU_MASTER_ID = 57;
  static final int MENU_PURCHASE_ID = 58;
  static final int MENU_SALES_ID = 60;
  static final int MENU_PRODUCT_ID = 89;
  static final int MENU_SYSTEM_SETTING_ID = 144;
  static final int MENU_ACCOUNTING_ID = 157;
  static final int MENU_PLATFORM_ID = 170;
  static final int MENU_STOCK_ID = 209;
  static final int MENU_REPORT_ID = 10;
  static final int MENU_CHEQUE_RECEIPT_ID = 199;
  static final int MENU_STOCK_AND_SALES_ID = 210;
  static final int MENU_STOCK_LEDGER_ID = 211;
  static final int MENU_PRODUCT_CATEGORY_TAX = 28;
  static final int MENU_MRP_ADJUSTMENT = 92;
  static final int MENU_IMPORT_ID = 246;
  static final int MENU_IMPORT_SALES_ID = 249;
  static final int MENU_STOCK_ADJUSTMENT = 20;
  static final int MENU_STOCK_MOVEMENT = 19;
  static final int MENU_PRODUCT_INVENTORY = 168;
  static final int MENU_STOCK_AUDIT = 228;
  static final int MENU_PURCHASE_REGISTER = 261;
  //Role
  static final int VENDOR_ROLE_ID = 4;
  static final int CUSTOMER_ROLE_ID = 5;
  static final int TRANSPORTER_ROLE_ID = 7;
  static final int COMPANY_SALES_AGENT_ID = 8;

  // public static final int INTERNATIONAL_BUSINESS = 1;
  // public static final int INTERSTATE_BUSINESS = 2;
  // public static final int INTRASTATE_BUSINESS = 3;
  public static final Integer LINE_DISCOUNT_APPLICABLE = 1;
  public static final Integer CASH_DISCOUNT_APPLICABLE = 1;
  public static final Integer SPECIAL_DISCOUNT_APPLICABLE = 1;
  public static final Integer CASH_DISCOUNT_TAXABLE = 1;
  public static final Integer DISCOUNT_APPLICABLE_FOR_SERVICES = 1;
  public static Role VENDOR_ROLE;
  public static Role CUSTOMER_ROLE;
  public static Role TRANSPORTER_ROLE;
  public static Role COMPANY_SALES_AGENT;

  public static final double PLATFORM_UNREALIZED_MIN_VALUE = -0.01;
  public static final double PLATFORM_UNREALIZED_MAX_VALUE = 0.01;

  public static PlatformSettlementType PLATFORM_SETTLEMENT_TYPE_SELF = new PlatformSettlementType(1);
  public static PlatformSettlementType PLATFORM_SETTLEMENT_TYPE_CREDIT_NOTE = new PlatformSettlementType(2);
  public static PlatformSettlementType PLATFORM_SETTLEMENT_TYPE_DEBIT_NOTE = new PlatformSettlementType(3);
  //Platform
  public static PlatformSource SALES = new PlatformSource(2);
  public static PlatformSource SALES_RETURN = new PlatformSource(4);
  public static PlatformSource PURCHASE = new PlatformSource(1);
  public static PlatformSource PURCHASE_RETURN = new PlatformSource(3);
  //public static PlatformSource SETTLEMENT_BALANCE = new PlatformSource(5);
  public static PlatformSource DEBIT_CREDIT_NOTE = new PlatformSource(5);
  public static PlatformSource VENDOR_CLAIM = new PlatformSource(7);
  public static PlatformSource PLATFORM = new PlatformSource(8);

  public static PlatformDescription SHORTAGE = new PlatformDescription(1);
  public static PlatformDescription EXCESS = new PlatformDescription(2);
  public static PlatformDescription VENDOR_RESERVE = new PlatformDescription(3);
  public static PlatformDescription INVOICE_DIFFERENCE = new PlatformDescription(4);
  public static PlatformDescription FREE_QUANTITY = new PlatformDescription(5);
  public static PlatformDescription MARGIN_DIFFERENCE = new PlatformDescription(6);
  public static PlatformDescription DISCOUNT = new PlatformDescription(7);
  public static PlatformDescription RATE_DIFFERENCE = new PlatformDescription(8);
  public static PlatformDescription VENDOR_CLAIM_DESC = new PlatformDescription(14);
  public static PlatformDescription PLATFORM_DIFFERENCE = new PlatformDescription(9); //TODO guess this should be credit note debit note
  public static PlatformDescription PLATFORM_SELF_ADJUST_DIFFERENCE = new PlatformDescription(15);
  public static PlatformDescription PLATFORM_UNREALIZED_AMOUNT = new PlatformDescription(10);
  public static PlatformDescription MRP_ADJUSTMENT = new PlatformDescription(11);
  public static PlatformDescription REPLACEMENT = new PlatformDescription(12);
  public static PlatformDescription CUSTOMER_CREDIT_ADJUSTMENT = new PlatformDescription(16);

  public static PlatformStatus PLATFORM_STATUS_NEW = new PlatformStatus(1);
  public static PlatformStatus PLATFORM_STATUS_PROCESSED = new PlatformStatus(2);
  public static PlatformStatus PLATFORM_STATUS_PROCESSING = new PlatformStatus(3);

  //Claim
  public static VendorClaimStatus CLAIM_DRAFT = new VendorClaimStatus(1);
  public static VendorClaimStatus CLAIM_PROCESSED = new VendorClaimStatus(2);

  public static TradeProfile CSA = new TradeProfile(2);
  public static TradeProfile CANDF = new TradeProfile(3);
  public static Currency DEFAULT_CURRENCY;

  //Ledger
  public static AccountingLedger OPENING_BALANCE = new AccountingLedger(85); //Opening balance ledger

  // voucher type
  // Toggle switch for MegaMenu
  public static final int TOGGLE_MEGA_MENU_ALL = 1;
  public static final int TOGGLE_MEGA_MENU_FAVORITE = 2;
  //public static final int STATUS_PENDING = 1;
  //public static Integer SALES_RETURN_STATUS_DRAFT = 2;
  //public static Integer SALES_RETURN_STATUS_CONFIRMED = 1;  

  public static void initRole(MainView main) {
    if (VENDOR_ROLE == null) {
      VENDOR_ROLE = roleSelect(main, VENDOR_ROLE_ID);
    }
    if (CUSTOMER_ROLE == null) {
      CUSTOMER_ROLE = roleSelect(main, CUSTOMER_ROLE_ID);
    }
    if (TRANSPORTER_ROLE == null) {
      TRANSPORTER_ROLE = roleSelect(main, TRANSPORTER_ROLE_ID);
    }
    if (COMPANY_SALES_AGENT == null) {
      COMPANY_SALES_AGENT = roleSelect(main, COMPANY_SALES_AGENT_ID);
    }
  }

  private static Role roleSelect(MainView main, int id) {
    List<Role> roleList = ScmLookupView.role(main);
    for (Role bp : roleList) {
      if (bp.getId() == id) {
        return bp;
      }
    }
    return null;
  }

  public static void initCurrency(MainView main) {
    List<Currency> cList = ScmLookupView.currency(main);
    for (Currency bp : cList) {
      DEFAULT_CURRENCY = bp;
      return;
    }
  }

  private static final Cache<String, TaxCalculator> taxCache = Caffeine.newBuilder()
          .expireAfterAccess(60, TimeUnit.MINUTES)
          .expireAfterWrite(10, TimeUnit.DAYS)
          .maximumSize(10_000)
          .build();

  public static TaxCalculator getTaxCalculator(Company company) {
    return getTaxCalculator(company.getCountryTaxRegimeId().getTaxProcessorId().getProcessorClass());
  }

  public static TaxCalculator getTaxCalculator(String taxProcessorName) {
    try {
      TaxCalculator calc = taxCache.getIfPresent("_SCM_CALC_CACHE");
      if (calc == null) {
        calc = (TaxCalculator) Class.forName(taxProcessorName).newInstance();
        taxCache.put("_SCM_CALC_CACHE", calc);
      }
      return calc;

    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static final boolean isInternationalBusiness(int sourceCountryCode, int desitnationCountryCode) {
    return (desitnationCountryCode != sourceCountryCode);
  }

  public static final boolean isInterstateBusiness(String sourceStateCode, String desitnationStateCode) {
    return (!StringUtil.isEmpty(sourceStateCode) && !StringUtil.isEmpty(desitnationStateCode) && !desitnationStateCode.equals(sourceStateCode));
  }

  public static final boolean isIntrastateBusiness(String sourceStateCode, String desitnationStateCode) {
    return (!StringUtil.isEmpty(sourceStateCode) && !StringUtil.isEmpty(desitnationStateCode) && desitnationStateCode.equals(sourceStateCode));
  }

  public static boolean isSchemeApplicable(Account account) {
    return (account != null && account.getIsSchemeApplicable() != null && account.getIsSchemeApplicable().equals(SystemConstants.FREE_SCHEME));
  }

  public static Date getMinEntryDate(Company c) {
    return c.getCurrentFinancialYear().getStartDate();
  }

  public static Date getMaxEntryDate(Company c) {
    Date today = new Date();
    if (today.before(c.getCurrentFinancialYear().getEndDate())) {
      return today;
    }
    return c.getCurrentFinancialYear().getEndDate();
  }
}
