/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.sys;

import spica.constant.ScmConstant;

/**
 *
 * @author java-2
 */
public final class SystemConstants {

  public static final Integer DEFAULT = 1;
  public static final Integer DEFAULT_EXPIRY_SALES_DAYS = 90;

  public static final Integer YES = 1;
  public static final Integer NO = 0;

  public static final Integer SERVICE_AS_EXPENSE = 1;
  public static final Integer SERVICE_AS_NON_EXPENSE = 0;

  public static final String[] PRODUCT_TYPES = new String[]{"GL:General", "CC:Cold Chain"};
  public static final String PRODUCT_TYPE_GENERAL = "GL";
  public static final String PRODUCT_TYPE_COLD_CHAIN = "CC";

  public static final Integer STOCK_ADJUSTMENT_TYPE_SALEABLE = 1;
  public static final Integer STOCK_ADJUSTMENT_TYPE_EXCESS = 2;

  public static final Integer PRODUCT_STOCK_STATUS_DAMAGED = 1;
  public static final Integer PRODUCT_STOCK_STATUS_EXPIRED = 2;
  public static final Integer PRODUCT_QTY_TYPE_SALEABLE = 0;

  public static final Integer ONE_DIMENSION = 1;
  public static final Integer TWO_DIMENSION = 2;
  public static final Integer THREE_DIMENSION = 3;
  public static final Integer PRODUCT_PACKING_STRIP = 1;
  public static final Integer PRODUCT_PACKING_PRIMARY = 1;
  public static final Integer PRODUCT_PACKING_SECONDARY = 2;
  public static final Integer PRODUCT_PACKING_TERTIARY = 3;

  public static final Integer RATE_DERIVATION_MARK_UP = 1;
  public static final Integer RATE_DERIVATION_MARK_DOWN = 2;

  public static final Integer PRODUCT_STATUS_ACTIVE = 1;
  public static final Integer PRODUCT_PRESET_EDITABLE = 1;

  public static final int FIRST_LEVEL = 1;
  public static final int SECOND_LEVEL = 2;
  public static final int THIRD_LEVEL = 3;
  public static final int FOURTH_LEVEL = 4;

  public static final Integer PLATFORM_SUMMARY_VIEW = 1;
  public static final Integer PLATFORM_DETAILED_VIEW = 2;
  public static final Integer PLATFORM_FUND_VIEW = 3;

  public static final int INVOICE_ENTRY = 0;
  public static final int OPENING_STOCK_ENTRY = 1;
  public static final int SALES_RETURN_STOCK_ENTRY = 2;

  public static final Integer DEBIT_NOTE = 1;
  public static final Integer CREDIT_NOTE = 2;

  public static final Integer DEBIT_CREDIT_PARTY_SUPPLIER = 1;
  public static final Integer DEBIT_CREDIT_PARTY_CUSTOMER = 2;

  public static final Integer TAXABLE_DEBIT_CREDIT_NOTE = 1;
  public static final Integer NON_TAXABLE_DEBIT_CREDIT_NOTE = 0;

  /**
   * Status value
   */
  public static final Integer EXPIRED = -1;
  public static final Integer CANCELLED = 0;
  public static final Integer DRAFT = 1;
  public static final Integer CONFIRMED = 2;
  public static final Integer CONFIRMED_AND_PACKED = 3;
  public static final Integer CONFIRMED_PARTIALYY_PROCESSED = 3;
  public static final Integer CONFIRMED_AND_PLANNED = 3;
  public static final Integer CONFIRMED_AND_PART_PENDING = 3;
  public static final Integer CONFIRMED_AND_INVOICED = 4;
  public static final Integer CONFIRMED_AND_PROCESSED = 4;
  public static final Integer SPLITS = 5;

  public static final Integer SALES_RETURN_TYPE_SALEABLE_AND_DAMAGED = 0;
  public static final Integer SALES_RETURN_TYPE_SALEABLE = 1;
  public static final Integer SALES_RETURN_TYPE_DAMAGED = 2;

  public static final Integer SALES_INVOICE_TYPE_PROFORMA = 2;
  public static final Integer SALES_INVOICE_TYPE_SALES_INVOICE = 1;

  public static final int PURCHASE_RETURN_VERIFIED = 4;
  public static final Integer DAMAGED_AND_EXPIRED = 1;
  public static final Integer NON_MOVING_AND_NEAR_EXPIRY = 2;

  public static final Integer DISCOUNT_IN_PERCENTAGE = 1;
  public static final Integer DISCOUNT_IN_VALUE = 2;

  public static final Integer BATCH_SALEABLE = 1;
  public static final Integer BATCH_BLOCKED = 2;

  public static final Integer CONSIGNMENT_TYPE_PURCHASE = 1;
  public static final Integer CONSIGNMENT_TYPE_SALES = 2;
  public static final Integer CONSIGNMENT_TYPE_PURCHASE_RETURN = 3;
  public static final Integer CONSIGNMENT_TYPE_SALES_RETURN = 4;

  public static final int CONSIGNMENT_STATUS_DRAFT = 1;
  public static final int CONSIGNMENT_STATUS_CONFIRMED = 2;
  public static final int CONSIGNMENT_STATUS_CENCELLED = 5;
  public static final int CONSIGNMENT_STATUS_READY_FOR_DISPATCH_AND_WAY_BILL_READY = 6;
  public static final int CONSIGNMENT_STATUS_DISPATCHED = 7;
  public static final int CONSIGNMENT_STATUS_DELIVERED = 8;
  public static final int CONSIGNMENT_STATUS_FULLY_DAMAGED_CANCELLED = 9;
  public static final int CONSIGNMENT_STATUS_FULLY_DAMAGED_DELIVERED = 10;
  public static final int CONSIGNMENT_STATUS_CANCEL_AND_INFORM_ST = 11;
  public static final int CONSIGNMENT_STATUS_CENCEL_AND_INFORMED_ST = 12;
  public static final int CONSIGNMENT_STATUS_CANCEL_LOST_AND_INFORM_ST = 13;
  public static final int CONSIGNMENT_STATUS_CANCEL_LOST_AND_INFORMED_ST = 14;
  public static final int CONSIGNMENT_STATUS_DISPACHED_AND_CHECKPOST_CLEARED = 15;
  public static final int CONSIGNMENT_STATUS_LOST = 16;
  public static final int CONSIGNMENT_STATUS_NORMAL = 17;

  public static final Integer TAX_CODE_MODIFIED = 1;

  /**
   * Product Entry Flags
   */
  public static final int DISCOUNT_FOR_COMPANY = 0;
  public static final int DISCOUNT_FOR_CUSTOMER = 1;
  public static final Integer PTS_VALUE_MODIFIED = 1;
  public static final Integer QUANTITY_SCHEME = 1;
  public static final Integer FREE_SCHEME = 2;

  public static final Integer MEDICINE_COMMODITY_ID = 1;
  public static final Integer PRODUCT_NOT_AVAILABLE = 2;
  public static final Integer REQUESTED_QTY_NOT_AVAILABLE = 1;
  public static final Integer STOCK_TYPE_FREE = 1;
  public static final Integer STOCK_TYPE_SALEABLE = 0;
  public static final Integer STOCK_SALEABLE_OUT = 0;
  public static final Integer STOCK_SALEABLE_OUT_FOR_FREE = 1;
  public static final Integer STOCK_SALEABLE_AS_FREE_IN = 2;
  public static final Integer STOCK_SALEABLE_AS_FREE_OUT = 3;
  public static final Integer VAT_APPLICABLE_ON_MRP = 1;
  public static final Integer VAT_APPLICABLE_ON_GOODS_VALUE = 2;
  public static final Integer CASH_DISCOUNT_APPLICABLE = 1;
  public static final Integer CASH_DISCOUNT_TAXABLE = 1;
  public static final Integer PRODUCT_NOT_AVAILABLE_IN_STOCK = 1;

  public static final Integer STOCK_MOVEMENT_SALEABLE_TO_DAMAGED = 1;
  public static final Integer STOCK_MOVEMENT_DAMAGED_TO_SALEABLE = 2;
  public static final Integer STOCK_MOVEMENT_SALEABLE_TO_EXPIRED = 3;

  public static final Integer INTRASTATE = ScmConstant.INTRASTATE;
  public static final Integer INTERSTATE = ScmConstant.INTERSTATE;
  public static final Integer GLOBAL = 2;

  public final static Integer INTERSTATE_PURCHASE = 1;
  public final static Integer INTRASTATE_PURCHASE = 2;

  public static final Integer COMMISSION_ON_GOODS_VALUE = 1;
  public static final Integer COMMISSION_ON_BILL_AMOUNT = 2;

  public static final Integer COMMISSION_APPLICABLE_ON_SALES = 1;
  public static final Integer COMMISSION_APPLICABLE_ON_COLLECTION = 2;

  public static final Integer COMMISSION_BY_RANGE = 1;

  public static final Integer COMMISSION_AMOUNT_AS_FIXED = 1;
  public static final Integer COMMISSION_AMOUNT_AS_PERCENTAGE = 2;

  public static final Integer PURCHASE_DEBIT_CREDIT_NOTE = 1;
  public static final Integer SALES_DEBIT_CREDIT_NOTE = 2;

  public static final SalesReturnCreditNote SALES_RETURN_CREDIT_NOTE = SalesReturnCreditNote.SINGLE_CREDIT_NOTE;

  public static final Integer REGISTERED_ADDRESS = 1;
  public static final Integer BILLING_ADDRESS = 2;
  public static final Integer SHIPPING_ADDRESS = 3;

  public static final Integer DRUG_LIC = 1;
  public static final Integer FSSAI_LIC = 2;

  public static final Integer ACTIVE_LICENSE = 1;

  public static final Integer EMAIL_SECURITY_NONE = 0;
  public static final Integer EMAIL_SECURITY_SSL_TSSL = 1;
  public static final Integer EMAIL_SECURITY_STARTTLS = 2;
  public static final Integer EMAIL_NO_AUTHENTICATION = 0;
  public static final Integer EMAIL_PASSWORD_AUTHENTICATION = 1;

  public static final String INPUT_TAX = "Input";
  public static final String OUTPUT_TAX = "Output";

  public static final String PRINT_SINGLE_LINE = "SINGLE_LINE";
  public static final String PRINT_MULTIPLE_LINE = "MULTIPLE_LINE";
  public static final String PRINT_BILL_TITLE_TOP = "TOP";
  public static final String PRINT_BILL_TITLE_MIDDLE = "MIDDLE";
  public static final String PRINT_PORTRAIT = "PORTRAIT";
  public static final String PRINT_LANDSCAPE = "LANDSCAPE";

  public static final Integer ACTIVE_CURRENT_FINANCIAL_YEAR = 1;
  public static final Integer IN_ACTIVE_FINANCIAL_YEAR = 0;
  public static final Integer SPLITTABLE_SALES_RETURN = 1;
  public static final Integer NOT_SPLITTABLE_SALES_RETURN = 0;
  public static final Integer DISCOUNT_REPLACEMENT = 1;
  public static final Integer DISCOUNT_NON_REPLACEMENT = 0;

  public static final Integer PRINT_SHOW_HORIZONTAL_LINE = 1;
  public static final Integer PRINT_HIDE_HORIZONTAL_LINE = 0;

  public static final float PRINT_LANDSCAPE_HEGHT = 595f;
  public static final float PRINT_PORTRAIT_HEIGHT = 802f;

  public enum SalesReturnCreditNote {
    SINGLE_CREDIT_NOTE, INVOICE_CREDIT_NOTE, LEDGER_ENTRY;
  }

  public static final String PDF_ITEXT = "ITEXT";
  public static final String PDF_KENDO = "KENDO";
  public static final String TYPE_I = "TYPE_I";
  public static final String TYPE_II = "TYPE_II";

  public static final String B2B = "B2B";
  public static final String B2CS = "B2CS";
  public static final String HSN = "HSN";
  public static final String DOC_SUMMARY = "DOC_SUMMARY";
  public static final String CDNR_D = "CDNR_D";
  public static final String CDNR_C = "CDNR_C";
  public static final String CDNR = "CDNR";
  public static final String PURCHASE_REPORT = "PURCHASE_REPORT";
  public static final String PURCHASE_RETURN_REPORT = "PURCHASE_RETURN_REPORT";
  public static final String SALES_REPORT = "SALES_REPORT";
  public static final String SALES_RETURN_REPORT = "SALES_RETURN_REPORT";

  public static final String CUSTOM_DATE = "CUSTOM_DATE";
  public static final String CURRENT_FINANCIAL_YEAR = "CURRENT_FINANCIAL_YEAR";
  public static final String PREVIOUS_FINANCIAL_YEAR = "PREVIOUS_FINANCIAL_YEAR";
  public static final String CURRENT_YEAR = "CURRENT_YEAR";
  public static final String PREVIOUS_YEAR = "PREVIOUS_YEAR";
  public static final String CURRENT_MONTH = "CURRENT_MONTH";
  public static final String LAST_MONTH = "LAST_MONTH";
  public static final String LAST_7DAYS = "LAST_7DAYS";
  public static final String YESTERDAY = "YESTERDAY";
  public static final String TODAY = "TODAY";

  public static final Integer SHOW_INVOICE_WISE = 1;
  public static final Integer HIDE_INVOICE_WISE = 0;
  public static final Integer SHOW_AGE_WISE = 3;

  public static final String SAMPLE = "SAMPLE";
  public static final Integer DEFAULT_EXPIRY_DAYS = 100;

  public static final int TRADE_PROFILE_CONSUMER = 8;

  /**
   *
   */
  public static final int CESS_ON_INTRASTATE_SALES = 1;
  public static final int CESS_ON_INTERSTATE_SALES = 2;
  public static final int CESS_ON_ALL_SALES = 3;
  public static final Integer SHOW_UPLOAD_PRODUCT_GALLERY = 1;
  public static final Integer HIDE_UPLOAD_PRODUCT_GALLERY = 0;
  public static final int TRADING_RATE_PRECISION = 3;
  public static final int ENTRY_RATE_PRECISION = 4;

  public static String statusColor(Integer status) {

    if (status == null) {
      return "";
    }
    if (status == SystemConstants.CONFIRMED) {//Confirmed
      return "#09a77b";
    } else if (status == SystemConstants.DRAFT) {
      return "#fdb81e";
    } else if (status == SystemConstants.CANCELLED) {
      return "#fd4921";
    } else if (status == SystemConstants.EXPIRED) {
      return "#777071ed";
    } else if (status == SystemConstants.CONFIRMED_AND_PROCESSED) {
      return "#337ab7";
    } else if (status == SystemConstants.CONFIRMED_PARTIALYY_PROCESSED) {
      return "#08c";
    }

    return "";
  }
  public static final int LEFT = 0;
  public static final int RIGHT = 2;

  public static final String ALL = "ALL";
  public static final String BY_GROUP = "BY GROUP";

// GALLERY TYPES
  public static final String GALLERY_PRODUCT = "PRODUCT";
  public static final int GALLERY_PRODUCT_TYPE = 1;
  public static final int GALLERY_MAX_FILES = 6;
  // PRINT FILTER
  public static final String ALL_CUSTOMER = "All Customers";
  public static final String ALL_ACCOUNT_GROUP = "All Account Group";
  public static final String ALL_DISTRICT = "All District";
  public static final String ALL_SALES_AGENT = "All Sales Agent";
  public static final String ALL_ACCOUNT = "All Account";
  public static final String ALL_TERRITORY = "All Territory";
  // CLAIM
  public static final Integer CLAIM_SALES = 1;
  public static final Integer CLAIM_PURCHASE = 2;
  public static final Integer CLAIM_SALES_RETURN = 3;
  public static final Integer CLAIM_PURCHASE_RETURN = 4;
  public static final Integer CLAIM_SALES_TAX_LIST = 5;
  public static final Integer CLAIM_PURCHASE_TAX_LIST = 6;
  public static final Integer CLAIM_SALES_RETURN_TAX_LIST = 7;
  public static final Integer CLAIM_PURCHASE_RETURN_TAX_LIST = 8;
  public static final Integer CLAIM_EXPENSE = 10;
  public static final Integer CLAIM_CREDIT_NOTE = 11;

//  Import
  public static final int IMPORT_FAILED = 0;
  public static final int IMPORT_SUCCESS = 1;
  public static final int IMPORT_TYPE_PRODUCT = 1;
  public static final int IMPORT_TYPE_PRODUCT_ENTRY = 2;
  public static final int IMPORT_TYPE_SALES = 3;

  public static final Integer COMMISSION_AMOUNT_AS_VALUE = 1;

  //  Export
  public static final int EXPORT = 1;
  public static final int EXPORT_ALL = 2;

  public static final String SALES = "SALES";
  public static final String SALES_INVOICE_ITEM = "SALES_INVOICE_ITEM";
  public static final String SALES_RETURN = "SALES_RETURN";
  public static final String PURCHASE = "PURCHASE";
  public static final String PRODUCT_ENTRY = "PRODUCT ENTRY";
  public static final String PURCHASE_RETURN = "PURCHASE_RETURN";
  public static final String DEBIT_CREDIT_NOTE = "DEBIT_CREDIT_NOTE";
  public static final String ACCOUNTING_TRANSACTION = "ACCOUNTING_TRANSACTION";
  public static final String ACCOUNTING_TRANSACTION_SETTLEMENT = "ACCOUNTING_TRANSACTION_SETTLEMENT";
  public static final String STOCK_ADJUSTMENT = "STOCK_ADJUSTMENT";

  public static final Integer NEXT = 2;
  public static final Integer PREVIOUS = 1;

  public static final String CUSTOMER_REPORT = "Customer Report";

  public static final Integer SUMMARY = 1;
  public static final Integer DETAIL = 2;

  public static final String GST_URL = "https://services.gst.gov.in/services/searchtp";
  public static final String EWAY_BILL = "https://ewaybillgst.gov.in/login.aspx";

  public static final String PRINT_COMMISSION_CLAIM = "PRINT COMMISSION CLAIM";
  public static final String PRINT_COMMISSION_EXPENSE = "PRINT COMMISSION EXPENSE";
  public static final String PRINT_CHEQUE_RECEIPT = "RECEIPT VOUCHER";
  public static final String PRINT_VOUCHER = " VOUCHER";

  public static final Integer E_WAY_BILL_AMOUNT = 50000;

  public static final Integer PRINT_SHOW_HEADER_ON_ALL_PAGES = 1;

  public static final Integer ERROR = 1;
  public static final Integer ERROR_FREE = 0;

  public static final String LINE_ITEM = "LINE ITEM";
  public static final String CHEQUE_TODAY = "CHEQUE TODAY";
  public static final String CHEQUES_OVERDUE = "CHEQUES OVERDUE";
  public static final String UNDATED_CHEQUE = "UNDATE CHEQUE";
  public static final String CHEQUE_RECEIPT = "CHEQUE RECEIPT";
  public static final String MARGIN_DIFFERENCE = "MARGIN DIFFERENCE";
  public static final String RATE_DIFFERENCE = "RATE DIFFERENCE";
  public static final String INVOICE_DIFFERENCE = "INVOICE DIFFERENCE";
  public static final String SHORTAGE_QUANTITY = "SHORTAGE QUANTITY";
  public static final String CUSTOMER_OUTSTANDING = "CUSTOMER OUTSTANDING";
  public static final String MRP_ADJUSTMENT = "MRP ADJUSTMENT";
  public static final String REPLACEMENT = "REPLACEMENT";
  
  //public static final String SMS_API_KEY="UKeFXe8SXqI-nnX0m2SqOsbacW5S54Dpu2WmLI2kPH";
  //public static final String SMS_SENDER_URL="https://api.textlocal.in/send/?";
  //public static final String SMS_HOST_URL="https://geco.infospica.com/";
  public static final String SMS_HOST_SUFFIX = "download?uid=";
  // public static final String SMS_SENDER_ID="TXTLCL";

  public static final String SMS_OPTION = "SMS";
  public static final String EMAIL_OPTION = "EMAIL";
  public static final String SMS_EMAIL = "SMS_EMAIL";

  public static final Double TCS_APPLICABLE_AMOUNT = 5000000.0;
}
