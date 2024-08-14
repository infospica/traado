/*
 * @(#)ScmLookupView.java	1.0 Thu Apr 07 11:31:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.util.ArrayList;
import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.AccountStatus;
import spica.scm.domain.AddressType;
import spica.scm.domain.Bank;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyBank;
import spica.scm.domain.Consignment;
import spica.scm.domain.ConsignmentDocType;
import spica.scm.domain.ConsignmentReceiptType;
import spica.scm.domain.ConsignmentStatus;
import spica.scm.domain.ContractStatus;
import spica.scm.domain.Country;
import spica.scm.domain.CountryTaxRegime;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.District;
import spica.scm.domain.Manufacture;
import spica.scm.domain.Product;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductClassification;
import spica.scm.domain.ProductPacking;
import spica.scm.domain.ProductType;
import spica.scm.domain.PurchaseReq;
import spica.scm.domain.PurchaseReqStatus;
import spica.scm.domain.SalesAccount;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesOrder;
import spica.scm.domain.SalesReq;
import spica.scm.domain.SalesReturnReceipt;
import spica.scm.domain.State;
import spica.scm.domain.SupplierGroup;
import spica.fin.domain.TaxCode;
import spica.fin.domain.TaxHead;
import spica.scm.domain.Territory;
import spica.scm.domain.TradeProfile;
import spica.scm.domain.Transporter;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorBank;
import spica.scm.domain.VendorContact;
import spica.scm.service.AddressTypeService;
import spica.scm.service.ConsignmentStatusService;
import spica.fin.service.TaxHeadService;
import spica.scm.domain.Brand;
import spica.scm.domain.PlatformDescription;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductPackingDetail;
import spica.scm.domain.ProductUnit;
import spica.scm.service.AccountService;
import spica.scm.service.CommodityService;
import spica.scm.service.UserProfileService;
import spica.sys.SystemConstants;

import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.AppLookup;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 * ScmLookupView bean is a JSF bean in application scope. Lookup/Reference data is configured in this class.
 *
 * @see ScmLookupView to see the lookup queries.
 */
/**
 * ScmLookupView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:25 IST 2016
 */
public abstract class ScmLookupExtView {

  private static final int TRADE_PROFILE_FIRST_LEVEL = 1;
  private static final int TRADE_PROFILE_SECOND_LEVEL = 2;
  private static final int TRADE_PROFILE_THIRD_LEVEL = 3;
  private static final int TRADE_PROFILE_FOURTH_LEVEL = 4;
  private static final int TRADE_PROFILE_FIFTH_LEVEL = 5;
  private static final int TRADE_PROFILE_SIXTH_LEVEL = 6;
  private static final int ACCOUNT_STATUS_DRAFT = 2;
  public static final int ACTIVE_ACCOUNT = 1;
//  private static final int TAX = 20;
  private static final String CUSTOMER_AUTO = "select id,customer_name from scm_customer where company_id=? and upper(customer_name) like ? order by upper(customer_name) asc";
  private static final String CUSTOMER_AUTO_NEW = "select id,customer_name from scm_customer where company_id=? order by upper(customer_name) asc";
  // private static final String BANK_ACC_TYPE = "select id ,title from scm_bank_account_type where id in(1,2)";

  public static final String TRADE_PROFILE_BY_LEVEL = "select id,title from scm_trade_profile where trade_level = ? order by upper(title) asc";
  public static final String TRADE_PROFILE_SECOND_AND_THIRD_LEVEL = "select id,title from scm_trade_profile where trade_level in (?,?) order by upper(title) asc";

  private static final String PRODUCT_AUTO = "SELECT id,product_name FROM scm_product WHERE scm_product.id IN (SELECT scm_product_batch.product_id FROM"
          + " scm_product_detail,scm_product_batch WHERE scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_detail.account_id = ?) "
          + " AND company_id = ? AND upper(product_name) LIKE ? ORDER BY UPPER(product_name) ASC";

  private static final String PRODUCT_AUTO_BY_BRAND = "SELECT id,product_name FROM scm_product WHERE scm_product.id IN (SELECT scm_product_batch.product_id FROM"
          + " scm_product_detail,scm_product_batch WHERE scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_detail.account_id = ?) "
          + " AND company_id = ? AND upper(product_name) LIKE ? AND brand_id IN(SELECT brand_id FROM scm_account_group_brands WHERE account_group_id = ?) \n"
          + " ORDER BY UPPER(product_name) ASC";

  private static final String PRODUCT_BY_ACCOUNTGROUP_AUTO = "SELECT id,product_name FROM scm_product WHERE scm_product.id IN (SELECT scm_product_batch.product_id FROM"
          + " scm_product_detail,scm_product_batch WHERE scm_product_detail.product_batch_id = scm_product_batch.id AND scm_product_detail.account_id "
          + " IN (SELECT account_id FROM scm_account_group_detail  WHERE account_group_id = ? )) "
          + " AND company_id = ? AND upper(product_name) LIKE ? AND brand_id IN(SELECT brand_id FROM scm_account_group_brands  WHERE account_group_id = ?)  ORDER BY UPPER(product_name) ASC";
  //public static final String ACCOUNT_VENDOR_AUTO = "select sv.id,sv.vendor_name from scm_vendor sv left join scm_account sa on sa.vendor_id = sv.id where sv.company_id = ? and sa.vendor_id is null and upper(sv.vendor_name) like ? order by upper(sv.vendor_name) asc";
  public static final String ACCOUNT_VENDOR = "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM ( "
          + "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM scm_vendor "
          + "WHERE id NOT IN (SELECT vendor_id FROM scm_account) AND company_id = ?  AND vendor_type = 1 "
          + "UNION "
          + "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM scm_vendor "
          + "WHERE id NOT IN (SELECT vendor_id FROM scm_account) AND company_id = ?  AND vendor_type =2 AND parent_id IN (SELECT vendor_id FROM scm_account "
          + "WHERE account_status_id = 1)"
          + ") AS TAB "
          + "order by upper(TAB.vendor_name) asc";

  public static final String ACCOUNT_VENDOR_AUTO = "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM ( "
          + "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM scm_vendor "
          + "WHERE id NOT IN (SELECT vendor_id FROM scm_account) AND company_id = ?  AND vendor_type = 1 "
          + "UNION "
          + "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM scm_vendor "
          + "WHERE id NOT IN (SELECT vendor_id FROM scm_account) AND company_id = ?  AND vendor_type =2 AND parent_id IN (SELECT vendor_id FROM scm_account "
          + "WHERE account_status_id = 1)"
          + ") AS TAB "
          + "WHERE UPPER(TAB.vendor_name) LIKE ? order by upper(TAB.vendor_name) asc";

  public static final String ACCOUNT_EDIT_VENDOR = "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM ("
          + "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM scm_vendor "
          + "WHERE id NOT IN (SELECT vendor_id FROM scm_account WHERE vendor_id <> ?) AND company_id = ?  AND vendor_type = 1 "
          + "UNION "
          + "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM scm_vendor "
          + "WHERE id NOT IN (SELECT vendor_id FROM scm_account WHERE parent_id <> ?) AND company_id = ?  AND vendor_type = 2 AND parent_id IN (SELECT vendor_id FROM scm_account WHERE account_status_id = 1) "
          + ") AS TAB "
          + "order by upper(TAB.vendor_name) asc";

  public static final String ACCOUNT_EDIT_VENDOR_AUTO = "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM ("
          + "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM scm_vendor "
          + "WHERE id NOT IN (SELECT vendor_id FROM scm_account WHERE vendor_id <> ?) AND company_id = ?  AND vendor_type = 1 "
          + "UNION "
          + "SELECT id,vendor_name,vendor_type,parent_id,cst_no,vat_no,tin_no,gst_no,supplier_group_id FROM scm_vendor "
          + "WHERE id NOT IN (SELECT vendor_id FROM scm_account WHERE parent_id <> ?) AND company_id = ?  AND vendor_type = 2 AND parent_id IN (SELECT vendor_id FROM scm_account WHERE account_status_id = 1) "
          + ") AS TAB "
          + "WHERE UPPER(TAB.vendor_name) LIKE ? order by upper(TAB.vendor_name) asc";

  public static final String TRADE_PROFILE_BY_CUSTOMER_LEVEL = "select id,title from scm_trade_profile where trade_level in(?,?,?) order by sort_order, upper(title) asc";
  public static final String ACCOUNT_STATUS_ALL = "select id,title from scm_account_status order by upper(title) asc";
  public static final String ACCOUNT_STATUS_DRAFT_ONLY = "select id,title from scm_account_status where id = ? order by upper(title) asc";
  public static final String ACCOUNT_GROUP_BY_COMPANY = "select * from scm_account_group where company_id = ? and (is_default = 1 or account_id is null) order by is_default desc, group_name asc";
  public static final String ACCOUNT_GROUP_BY_ACCOUNT = "select * from scm_account_group inner join "
          + "scm_account_group_detail on scm_account_group.id = scm_account_group_detail.account_group_id "
          + "where scm_account_group_detail.account_id = ? and  scm_account_group.company_id = ? order by scm_account_group.is_default desc";

  public static final String ACCOUNT_GROUP_BY_VENDOR = "select * from scm_account_group inner join "
          + "scm_account_group_detail on scm_account_group.id = scm_account_group_detail.account_group_id "
          + "where scm_account_group_detail.account_id in (select id from scm_account where vendor_id=?) order by scm_account_group.is_default desc";

  public static final String ACCOUNT_GROUP_BY_SALES_ACCOUNT = "select scm_account_group.* from scm_account_group scm_account_group "
          + "inner join scm_sales_account scm_sales_account on scm_account_group.id =scm_sales_account.account_group_id "
          + "where scm_sales_account.customer_id = ?";
  public static final String ACCOUNT_GROUP_PRICE_LIST_BY_ACCOUNT_GROUP = "select * from scm_account_group_price_list where account_group_id = ? order by is_default desc";
  public static final String VENDOR_COMMODITY_BY_ACCOUNT_GROUP = "select id,title,commodity_code from scm_service_commodity WHERE id IN ( "
          + "SELECT distinct commodity_id FROM scm_vendor_commodity WHERE vendor_id IN( "
          + "SELECT vendor_id FROM scm_account WHERE id  IN("
          + "SELECT account_id FROM scm_account_group_detail  WHERE account_group_id = ?)))";
  public static final String CUSTOMER_BY_COMPANY_AND_TRADE_PROFILE = "select * from scm_customer scm_customer "
          + "inner join scm_customer_trade_profile on scm_customer.id = scm_customer_trade_profile.customer_id "
          + "inner join scm_trade_profile on scm_trade_profile.id = scm_customer_trade_profile.trade_profile_id "
          + "where scm_customer.company_id = ? and scm_customer_trade_profile.trade_profile_id = ? order by upper(scm_customer.customer_name)";
  public static final String CUSTOMER_BY_COMPANY_AND_TRADE_PROFILE_AUTO = "select * from scm_customer scm_customer "
          + "inner join scm_customer_trade_profile on scm_customer.id = scm_customer_trade_profile.customer_id "
          + "inner join scm_trade_profile on scm_trade_profile.id = scm_customer_trade_profile.trade_profile_id "
          + "where scm_customer.company_id = ? and scm_customer_trade_profile.trade_profile_id = ? and upper(scm_customer.customer_name) like ? order by upper(customer_name) asc ";
  public static final String CUSTOMER_BY_ACCOUNT = "select scm_customer.* from scm_customer scm_customer "
          + "inner join scm_sales_account sales_account on scm_customer.id = sales_account.customer_id "
          + "inner join scm_account_group scm_account_group on scm_account_group.id = sales_account.account_group_id "
          + "inner join scm_account_group_detail on scm_account_group_detail.account_group_id = scm_account_group.id and scm_account_group_detail.account_id = ? "
          + "order by upper(scm_customer.customer_name) asc";
  public static final String CUSTOMER_BY_ACCOUNT_AUTO = "select scm_customer.* from scm_customer scm_customer "
          + "inner join scm_sales_account sales_account on scm_customer.id = sales_account.customer_id "
          + "inner join scm_account_group scm_account_group on scm_account_group.id = sales_account.account_group_id "
          + "inner join scm_account_group_detail  on scm_account_group_detail.account_group_id = scm_account_group.id and scm_account_group_detail.account_id = ? "
          + "where upper(scm_customer.customer_name) like ? order by upper(scm_customer.customer_name) asc";

  public static final String CUSTOMER_BY_SALES_ACCOUNT = "select * from scm_customer where id in (select customer_id from scm_sales_account where id = ?)";
  public static final String CUSTOMER_BY_SALES_ACCOUNT_AUTO = "select * from scm_customer where id in (select customer_id from scm_sales_account where id = ?) and upper(customer_name) like ? order by upper(customer_name) asc";
  public static final String SALEABLE_ACCOUNT_GROUP_BY_ACCOUNT = "select scm_account_group.* from scm_account_group scm_account_group "
          + "left outer join scm_account_group_detail scm_account_group_detail "
          + "on  scm_account_group.id = scm_account_group_detail.account_group_id "
          + "where (scm_account_group.account_id = ? and scm_account_group.is_default = 1) or (scm_account_group_detail.account_id = ?)";
  public static final String CUSTOMER_BY_ACCOUNT_GROUP = "select customer.* from scm_customer customer inner join scm_sales_account sales_account on customer.id = sales_account.customer_id where sales_account.account_group_id = ? order by upper(customer.customer_name) asc";
  public static final String CUSTOMER_BY_ACCOUNT_GROUP_AUTO = "select customer.* from scm_customer customer inner join scm_sales_account sales_account on customer.id = sales_account.customer_id where sales_account.account_group_id = ? and upper(customer.customer_name) like ? order by upper(customer_name) asc";
  public static final String ACCOUNT_GROUP_FOR_SALES_ACCOUNT = "select ag.* from scm_account_group ag "
          + "left join scm_account_group_detail agd on ag.account_id = agd.account_id where (ag.account_id is null or ag.is_default = 1) ";
  public static final String ACCOUNT_GROUP_FOR_SALES_ACCOUNT_AUTO = "";

  public static final String VENDOR_COMMODITY = "select scm_service_commodity.* from scm_service_commodity scm_service_commodity inner join scm_vendor_commodity scm_vendor_commodity on scm_service_commodity.id = scm_vendor_commodity.commodity_id where scm_vendor_commodity.vendor_id = ?";
  public static final String ACCOUNT_GROUP_BY_CUSTOMER = "select ag.* from scm_account_group ag inner join scm_sales_account sa on ag.id = sa.account_group_id where sa.customer_id = ?";
  public static final String ACCOUNT_GROUP_BY_CUSTOMER_AND_ACCOUNT = "select ag.* from scm_account_group ag inner join scm_sales_account sa on ag.id = sa.account_group_id where sa.customer_id = ? and ag.id in (select account_group_id from scm_account_group_detail where account_id=?)";

  public static final String CUSTOMER_BY_COMPANY = "select id,customer_name from scm_customer where company_id = ? order by upper(customer_name) asc";
  public static final String CUSTOMER_BY_COMPANY_AUTO = "select id,customer_name from scm_customer where company_id = ? and upper(customer_name) like ? order by upper(customer_name) asc";
  public static final String CUSTOMER_BY_COMPANY_AND_TRADE_TYPE = "select scm_customer.id,scm_customer.customer_name from scm_customer scm_customer inner join scm_customer_trade_profile scm_customer_trade_profile on scm_customer_trade_profile.customer_id = scm_customer.id where scm_customer.company_id = ? and scm_customer_trade_profile.trade_profile_id > ? order by upper(scm_customer.customer_name) asc";
  public static final String CUSTOMER_BY_COMPANY_AND_TRADE_TYPE_AUTO = "select scm_customer.id,scm_customer.customer_name from scm_customer scm_customer inner join scm_customer_trade_profile scm_customer_trade_profile on scm_customer_trade_profile.customer_id = scm_customer.id where scm_customer.company_id = ? and scm_customer_trade_profile.trade_profile_id > ? and upper(scm_customer.customer_name) like ? order by upper(scm_customer.customer_name) asc";
  public static final String CONTRACT_STATUS = "select id,title from scm_contract_status where id = ? order by upper(title) asc";

  public static final String VENDOR_BANK_BY_VENDOR_AUTO = "select id,account_name from scm_vendor_bank where vendor_id = ? and upper(account_name) like ? order by upper(account_name) asc";
  public static final String VENDOR_BANK_BY_VENDOR = "select id,account_name from scm_vendor_bank where vendor_id = ? order by sort_order, upper(account_name) asc";
  public static final String VENDOR_ADDRESS_BY_VENDOR = "select id,address from scm_vendor_address where vendor_id = ? order by sort_order, upper(address) asc";
  public static final String VENDOR_ADDRESS_BY_VENDOR_AUTO = "select id,address from scm_vendor_address where vendor_id = ? and upper(address) like ? order by upper(address) asc";

  public static final String COMPANY_BANK_BY_COMPANY = "select id,account_name from scm_company_bank where company_id = ? order by sort_order, upper(account_name) asc";
  public static final String COMPANY_BANK_BY_COMPANY_AUTO = "select id,account_name from scm_company_bank where company_id = ? and upper(account_name) like ? order by upper(account_name) asc";
  public static final String COMPANY_ADDRESS_BY_COMPANY = "select id,address from scm_company_address where company_id = ? order by sort_order, upper(address) asc";
  public static final String COMPANY_ADDRESS_BY_COMPANY_AUTO = "select id,address from scm_company_address where company_id = ? and upper(address) like ? order by upper(address) asc";

  public static final String ACCOUNT_BY_COMPANY_AUTO = "select id, account_code, account_title from scm_account where company_id = ? and upper(account_title) like ? order by upper(account_title) asc";
  public static final String ACCOUNT_BY_COMPANY = "select id, account_code, account_title from scm_account where company_id = ?";

  public static final String ACCOUNT_BY_PRIMARY_VENDOR = "select scm_account.id, scm_account.account_code,scm_account.vendor_id, scm_account.account_title from scm_account "
          + "inner join scm_vendor on scm_account.vendor_id = scm_vendor.id and scm_vendor.vendor_type = 1 where scm_account.company_id = ? order by upper(scm_account.account_code) asc";
  public static final String ACCOUNT_BY_PRIMARY_VENDOR_AUTO = "select scm_account.id, scm_account.account_code,scm_account.vendor_id, scm_account.account_title from scm_account "
          + "inner join scm_vendor on scm_account.vendor_id = scm_vendor.id and scm_vendor.vendor_type = 1 where scm_account.company_id = ? and "
          + "(upper(scm_account.account_code) like ? or upper(scm_account.account_title) like ?) "
          + "order by upper(scm_account.account_code) asc";

  public static final String ACCOUNT_BY_TRADE_TYPE_AUTO = "select scm_account.id, account_code, account_title from scm_account "
          + "inner join scm_vendor on scm_account.vendor_id = scm_vendor.id and scm_vendor.vendor_type = 1 and scm_account.account_status_id = 1 "
          + "where scm_account.company_id = ? and company_trade_profile_id = ? and (upper(account_code) like ? or upper(account_title) like ?) "
          + "order by upper(account_code) asc";
  public static final String ACCOUNT_BY_TRADE_TYPE = "select scm_account.id, account_code, account_title from scm_account "
          + "inner join scm_vendor on scm_account.vendor_id = scm_vendor.id and scm_vendor.vendor_type = 1 and scm_account.account_status_id = 1 "
          + "where scm_account.company_id = ? and company_trade_profile_id = ? order by upper(account_code) asc";

  public static final String ACCOUNT_BY_ACCOUNTGROUP_AUTO = "SELECT * FROM scm_account WHERE account_status_id=?  "
          + " AND id IN(SELECT account_id FROM scm_account_group_detail WHERE account_group_id =?)   "
          + " AND UPPER(account_code) LIKE ? ORDER BY UPPER(account_code) ASC";
  public static final String ACCOUNT_BY_ACCOUNTGROUP_PROFILE_AUTO = "SELECT * FROM scm_account WHERE account_status_id=?  "
          + " AND id IN(SELECT account_id FROM scm_account_group_detail WHERE account_group_id =?) AND id IN( SELECT account_id FROM scm_user_account WHERE user_id=?) "
          + " AND UPPER(account_code) LIKE ? ORDER BY UPPER(account_code) ASC";
//  public static final String PRODUCT_CATEGORY_BY_COMPANY_COMMODITY_AUTO = "select * from scm_product_category where commodity_id = ? and upper(title) like ? and status_id = ? "
//          + "and id in(select product_category_id from scm_product_category_tax where company_id = ?) order by upper(title) asc";
//  public static final String PRODUCT_CATEGORY_BY_COMPANY_COMMODITY = "select * from scm_product_category where commodity_id = ? and status_id = ? "
//          + "and id in(select product_category_id from scm_product_category_tax where company_id = ?) order by upper(title) asc";  
  public static final String PRODUCT_TYPE_BY_COMMODITY_AUTO = "select id,title from scm_product_type where commodity_id = ? and upper(title) like ? and status_id=? order by upper(title) asc";
  public static final String PRODUCT_TYPE_BY_COMMODITY = "select * from scm_product_type where commodity_id = ? and status_id=? order by upper(title) asc";

  public static final String PRODUCT_CATEGORY_BY_COMMODITY_AUTO = "select * from scm_product_category where commodity_id = ? and upper(title) like ? and status_id=? order by upper(title) asc";
  public static final String PRODUCT_CATEGORY_BY_COMMODITY = "select * from scm_product_category where commodity_id = ? and status_id=? order by upper(title) asc";

  public static final String PRODUCT_PRESET_BY_PRODUCT = "select * from scm_product_preset where product_id = ? and account_id = ?";
  public static final String PRODUCT_ENTRY_DETAIL_BY_ID = "select * from scm_product_entry_detail where id = ?";

  public static final String PRODUCT_BATCH_BY_PRODUCT_AUTO = "select id,batch_no from scm_product_batch where product_id=? and upper(batch_no) like ? order by upper(batch_no) asc";
  public static final String PRODUCT_BATCH_BY_PRODUCT = "select id,batch_no from scm_product_batch where product_id=?  order by upper(batch_no) asc";

  public static final String PRODUCT_CLASSIFICATION_AUTO = "select id,title from scm_product_classification where company_id=? and  upper(title) like ? and status_id=? order by upper(title) asc";

  public static final String PRODUCT_CLASSIFICATION = "select id,title from scm_product_classification where company_id=? and status_id=? order by upper(title) asc";

  public static final String ADDRESS_STATE_BY_COUNTRY_AUTO = "select id,state_name,state_code from scm_state where country_id = ? and upper(state_name) like ? order by upper(state_name) asc";
  public static final String ADDRESS_STATE_BY_COUNTRY = "select id,state_name,state_code from scm_state where country_id = ? order by upper(state_name) asc";
  public static final String ADDRESS_DISTRICT_BY_STATE_AUTO = "select id,district_name from scm_district where state_id = ? and upper(district_name) like ? order by upper(district_name) asc";
  public static final String ADDRESS_DISTRICT_BY_STATE = "select id,district_name from scm_district where state_id = ? order by upper(district_name) asc";
  public static final String ADDRESS_TERRITORY_BY_DISTRICT_AUTO = "select scm_territory.id,scm_territory.territory_name from scm_territory left outer join scm_territory_district on(scm_territory_district.territory_id=scm_territory.id) where scm_territory.company_id = ? and scm_territory_district.district_id = ? and upper(scm_territory.territory_name) like ? order by upper(scm_territory.territory_name) asc";
  //public static final String ADDRESS_TERRITORY_BY_DISTRICT_ID="select t.id,t.territory_name from  scm_territory t, scm_territory_district td where (t.id=td.territory_id) and (td.district_id=?) and upper(t.territory_name) like upper(?)";

  public static final String ADDRESS_TERRITORY_BY_DISTRICT = "select scm_territory.id,scm_territory.territory_name from scm_territory left outer join scm_territory_district on(scm_territory_district.territory_id=scm_territory.id) where scm_territory.company_id = ? and scm_territory_district.district_id = ? order by upper(scm_territory.territory_name) asc";

  public static final String CONSIGNMENT_VENDOR = "select id,consignment_no from scm_consignment where vendor_id = ? and consignment_status_id=4 order by upper(consignment_no) asc";
  public static final String CONSIGNMENT_VENDOR_AUTO = "select id,consignment_no from scm_consignment where vendor_id = ? and consignment_status_id=4 and upper(consignment_no) like ? order by upper(consignment_no) asc";
  public static final String CONSIGNMENT_DELIVERED_VENDOR = "select id,consignment_no from scm_consignment where vendor_id = ? and account_id = ? and (product_entry_verified < ? or product_entry_verified is null) and consignment_status_id in (3,4) order by upper(consignment_no) asc";
  public static final String CONSIGNMENT_DELIVERED_VENDOR_AUTO = "select id,consignment_no from scm_consignment where vendor_id = ? and account_id = ? and (product_entry_verified < ? or product_entry_verified is null) and consignment_status_id in(3, 4) and upper(consignment_no) like ? order by upper(consignment_no) asc";

  public static final String DISTRICT_BY_STATE = "select id,district_name from scm_district where state_id = ? order by upper(district_name) asc";
  public static final String PARENT_COMPANY = "select id,company_name from scm_company where id <> ? and id not in (select id from scm_company where company_id = ?) order by upper(company_name) asc";
  public static final String PARENT_COMPANY_AUTO = "select id,company_name from scm_company where id <> ? and id not in (select id from scm_company where company_id = ?) and upper(company_name) like ? order by upper(company_name) asc";
  public static final String PARENT_CUSTOMER = "select id,customer_name from scm_customer where id <> ? and id not in (select id from scm_customer where customer_id = ?) and company_id=? order by upper(customer_name) asc";
  public static final String PARENT_CUSTOMER_AUTO = "select id,customer_name from scm_customer where id <> ? and id not in (select id from scm_customer where customer_id = ?) and company_id=? and upper(customer_name) like ? order by upper(customer_name) asc";
  public static final String SHIPMENT_MODE_AUTO = "select id,title from scm_shipment_mode where id in(select shipment_mode_id from scm_transporter_shipment_mode where transporter_id=?)and upper(title) like ? order by upper(title) asc ";
  public static final String ADDRESS_STATE_BY_DISTRICT_AUTO = "select scm_state.id,scm_state.state_name from scm_state left outer join scm_district scm_district on (scm_state.id = scm_district.state_id)where scm_district.id=? and upper(state_name) like ? order by upper(state_name) asc";
  public static final String ADDRESS_STATE_BY_DISTRICT = "select scm_state.id,scm_state.state_name from scm_state left outer join scm_district scm_district on (scm_state.id = scm_district.state_id)where scm_district.id=? order by upper(state_name) asc";
  public static final String VENDOR_BY_COMPANY_AUTO = "select id,vendor_name from scm_vendor where company_id = ? and upper(vendor_name) like ? order by upper(vendor_name) asc";
  public static final String VENDOR_BY_COMPANY = "select id,vendor_name from scm_vendor where company_id = ? order by upper(vendor_name) asc";
  public static final String DOC_TYPE_BY_TRANSPORT_AUTO = "select id,title from scm_consignment_doc_type where transport_mode_id = ? and upper(title) like ? order by upper(title) asc";
  public static final String DOC_TYPE_BY_TRANSPORT = "select id,title from scm_consignment_doc_type where transport_mode_id = ? order by upper(title) asc";
  public static final String RECEIPT_TYPE_BY_TRANSPORT_AUTO = "select id,title from scm_consignment_receipt_type where transport_mode_id = ? and upper(title) like ? order by upper(title) asc";
  public static final String RECEIPT_TYPE_BY_TRANSPORT = "select id,title from scm_consignment_receipt_type where transport_mode_id = ? order by upper(title) asc";
  public static final String PURCHASE_REQ_BY_PO_ORDER_ITEM = "select * from scm_purchase_req pr where pr.account_id = ? and (pr.purchase_requisition_status_id = ? OR pr.purchase_requisition_status_id = ?) and pr.id in(select pri.purchase_requisition_id from scm_purchase_req_item pri where pri.id not in(select poi.purchase_req_item_id from scm_purchase_order_item poi where poi.purchase_req_item_id is not null))";
  public static final String VENDOR_COMMODITY_BY_VENDOR_ID = "select scm_service_commodity.id, scm_service_commodity.title from scm_service_commodity scm_service_commodity inner join scm_vendor_commodity scm_vendor_commodity on scm_service_commodity.id = scm_vendor_commodity.commodity_id where scm_vendor_commodity.vendor_id = ?";
  public static final String COMMODITY_AUTO = "select scm_service_commodity.id, scm_service_commodity.title from scm_service_commodity scm_service_commodity";
  public static final String PURCHASE_REQ_STATUS = "select id,title from scm_purchase_req_status order by upper(title) asc";
  public static final String TAX_CODE_COMMODITY = "select * from scm_tax_code where country_id = ? and status_id = 1 and tax_head_id in (select id from scm_tax_head where country_tax_regime_id = ? and applied_on = ?)";
  public static final String ACTIVE_SALES_SURCHARGE = "select id,code from scm_tax_code where status_id=1 and country_id = ?";
  public static final String TRANSPORETR_BY_COMPANY_AUTO = "select id,transporter_name from scm_transporter where company_id = ? and upper(transporter_name) like ? order by upper(transporter_name) asc";
  public static final String TRANSPORTER_BY_COMPANY = "select id,transporter_name from scm_transporter where company_id = ? order by upper(transporter_name) asc";
  public static final String VENDOR_BY_COMPANY_AUTO_VAT_APPLICABLE = "select id,vendor_name from scm_vendor where company_id = ? and vat_applicable=1 and upper(vendor_name) like ? order by upper(vendor_name) asc";
  public static final String VENDOR_BY_COMPANY_VAT_APPLICABLE = "select id,vendor_name from scm_vendor where company_id = ? and vat_applicable=1 order by upper(vendor_name) asc";

  public static final String VENDOR_BY_COMPANY_ACCOUNT_AUTO = "select id,vendor_name from scm_vendor where company_id = ? and id in(select vendor_id from scm_account where id=?)and upper(vendor_name) like ? order by upper(vendor_name) asc";
  public static final String VENDOR_BY_COMPANY_ACCOUNT = "select id,vendor_name from scm_vendor where company_id = ? and id in(select vendor_id from scm_account where id=?) order by upper(vendor_name) asc";

  private static final String USERPROFILE_AUTO = "select id,name,user_code from scm_user_profile where company_id=? and designation_id=? and upper(name) like ? order by upper(name) asc";

  //public static final String COMMODITY = "select id,title from scm_service_commodity where id in (select commodity_id from scm_service_commodity_tax where company_id = ?)  order by upper(title) asc";
  //public static final String COMMODITY_AUTO1 = "select id,title from scm_service_commodity where upper(title) like ? and id in (select commodity_id from scm_service_commodity_tax where company_id = ?) order by upper(title) asc";
  //public static final String COMMODITY_BYID = "select id,title from scm_service_commodity where id=? and id in (select commodity_id from scm_service_commodity_tax where company_id = ?)";
  public static final String MANUFACTURE_BY_COMPANY_AUTO = "select id, name, code from scm_manufacture where company_id = ? and upper(code) like ? order by upper(code) asc";
  public static final String MANUFACTURE_BY_COMPANY = "select id,name, code from scm_manufacture where company_id = ? ";
  public static final String BRAND_BY_COMPANY_AUTO = "select id, name, code from scm_brand where company_id = ? and (upper(code) like ? or upper(name) like ?) order by upper(code) asc";
  public static final String BRAND_BY_COMPANY = "select id,name, code from scm_brand where company_id = ? ";
  public static final String SALESAGENT_BY_SERVICES_AUTO = "select id, title from scm_services where upper(title) like ? order by upper(title) asc";
  public static final String SALESAGENT_BY_SERVICES = "select id,title from scm_services";

  public static final String BANK_AUTO_FILTER_BY_COUNTRY = "select id,name from scm_bank where country_id = ? and upper(name) like ? order by upper(name) asc";
  public static final String BANK_FILTER_BY_COUNTRY = "select id,name from scm_bank where country_id = ? order by upper(name) asc";
  public static final String ACCOUNTGROUP_BY_SALES_AGENT_AUTO = "select id,group_name from scm_account_group where company_id = ? and upper(group_name) like ? order by upper(group_name) asc";
  public static final String ACCOUNTGROUP_BY_SALES_AGENT = "select id,group_name from scm_account_group where company_id = ? order by upper(group_name) asc";
  public static final String TERRITORY_SALES_AGENT_AUTO = "select id,territory_name from scm_territory where company_id = ? and upper(territory_name) like ? order by upper(territory_name) asc";
  public static final String TERRITORY_SALES_AGENT = "select id,territory_name from scm_territory where company_id = ? order by upper(territory_name) asc";

  public static final String CONSIGNMENT_EXPENSE_PURCHASE = "select id,consignment_no from scm_consignment where account_id = ? and consignment_type_id=1 order by upper(consignment_no) asc";
  public static final String CONSIGNMENT_EXPENSE_PURCHASE_AUTO = "select id,consignment_no from scm_consignment where account_id = ? and consignment_type_id=1 and upper(consignment_no) like ? order by upper(consignment_no) asc";
  public static final String CONSIGNMENT_EXPENSE_SALES = "select id,consignment_no from scm_consignment where account_group_id = ? and consignment_type_id=2 order by upper(consignment_no) asc";
  public static final String CONSIGNMENT_EXPENSE_SALES_AUTO = "select id,consignment_no from scm_consignment where account_group_id = ? and consignment_type_id=2 and upper(consignment_no) like ? order by upper(consignment_no) asc";

  public static final String SERVICE_COMMODITY_AUTO = "select id,title,hsn_code from scm_service_commodity where country_id = ? and commodity_or_service = 2 and upper(title) like ? order by upper(title) asc";
  public static final String SERVICE_COMMODITY = "select id,title,hsn_code from scm_service_commodity where country_id = ? and commodity_or_service = 2 order by upper(title) asc";
  public static final String SERVICES_AUTO = "select * from scm_service_commodity where commodity_or_service = ? and (company_id = ? or (country_id = ? and company_id is null)) and upper(title) like ? order by upper(title) asc";
  public static final String SERVICES = "select * from scm_service_commodity where commodity_or_service = ? and (company_id = ? or (country_id = ? and company_id is null)) order by upper(title) asc";
  public static final String SUPPLIER_GROUP = "select id,title from scm_supplier_group where company_id =?  order by upper(title) asc";
  public static final String SUPPLIER_GROUP_AUTO = "select id,title from scm_supplier_group where company_id =? and  upper(title) like ? order by upper(title) asc";

  /**
   *
   * @param filter
   * @param companyId
   * @return
   */
  public static List<Customer> customerAuto(String filter, Integer companyId) {
    Object[] params = new Object[]{companyId, "%" + filter.toUpperCase() + "%"};
    return AppLookup.getAutoFilter(ScmLookupView.customerClass(), CUSTOMER_AUTO, params);
  }

  /**
   *
   * @param filter
   * @param companyId
   * @return
   */
  public static List<Product> productAuto(String filter, Integer companyId, Integer accountId) {
    Object[] params = new Object[]{accountId, companyId, "%" + filter.toUpperCase() + "%"};
    return AppLookup.getAutoFilter(ScmLookupView.productClass(), PRODUCT_AUTO, params);
  }

  public static List<Product> productAutoByBrand(String filter, Integer companyId, Integer accountGroupId, Integer accountId) {
    Object[] params = new Object[]{accountId, companyId, "%" + filter.toUpperCase() + "%", accountGroupId};
    return AppLookup.getAutoFilter(ScmLookupView.productClass(), PRODUCT_AUTO_BY_BRAND, params);
  }

  public static List<Product> productByAccountGroupAuto(String filter, Integer companyId, Integer accountGroupId) {
    Object[] params = new Object[]{accountGroupId, companyId, "%" + filter.toUpperCase() + "%", accountGroupId};
    return AppLookup.getAutoFilter(ScmLookupView.productClass(), PRODUCT_BY_ACCOUNTGROUP_AUTO, params);
  }

  /**
   *
   * @return
   */
  public static List<TradeProfile> tradeProfileFirstLevel() {
    return AppLookup.getAutoFilter(ScmLookupView.tradeProfileClass(), TRADE_PROFILE_BY_LEVEL, new Object[]{TRADE_PROFILE_FIRST_LEVEL});
  }

  public static List<TradeProfile> tradeProfileByCustomer() {
    return AppLookup.getAutoFilter(ScmLookupView.tradeProfileClass(), TRADE_PROFILE_BY_CUSTOMER_LEVEL, new Object[]{TRADE_PROFILE_FOURTH_LEVEL, TRADE_PROFILE_FIFTH_LEVEL, TRADE_PROFILE_SIXTH_LEVEL});
  }

  /**
   *
   * @param filter
   * @param companyId
   * @param accountId
   * @return
   */
  public static List<Vendor> accountVendorAuto(String filter, Integer companyId, Account accountId) {
    if (accountId != null) {
      if (!StringUtil.isEmpty(filter)) {
        return AppLookup.getAutoFilter(ScmLookupView.vendorClass(), ACCOUNT_EDIT_VENDOR_AUTO, new Object[]{accountId.getVendorId().getId(), companyId, accountId.getVendorId().getId(), companyId, "%" + filter.toUpperCase() + "%"});
      } else {
        return AppLookup.getAutoFilter(ScmLookupView.vendorClass(), ACCOUNT_EDIT_VENDOR, new Object[]{accountId.getVendorId().getId(), companyId, accountId.getVendorId().getId(), companyId});
      }
    } else {
      if (!StringUtil.isEmpty(filter)) {
        return AppLookup.getAutoFilter(ScmLookupView.vendorClass(), ACCOUNT_VENDOR_AUTO, new Object[]{companyId, companyId, "%" + filter.toUpperCase() + "%"});
      } else {
        List<Vendor> list = AppLookup.getAutoFilter(ScmLookupView.vendorClass(), ACCOUNT_VENDOR, new Object[]{companyId, companyId});
        return list;
      }
    }
  }

  /**
   *
   * @param main
   * @return
   */
  public static List<AccountStatus> accountStatus(MainView main) {
    if (main.isNew()) {
      return AppLookup.getAutoFilter(AccountStatus.class, ACCOUNT_STATUS_DRAFT_ONLY, new Object[]{ACCOUNT_STATUS_DRAFT});
    } else {
      return AppLookup.getCached(main, AccountStatus.class, ACCOUNT_STATUS_ALL);
    }
  }

  /**
   *
   * @param status
   * @return
   */
  public static List<ContractStatus> contractStatus(int status) {
    return AppLookup.getAutoFilter(ContractStatus.class, CONTRACT_STATUS, new Object[]{status});
  }

  /**
   *
   * @param vendorId
   * @param filter
   * @return
   */
  public static List<VendorBank> vendorBankAuto(Integer vendorId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(VendorBank.class, VENDOR_BANK_BY_VENDOR_AUTO, new Object[]{vendorId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(VendorBank.class, VENDOR_BANK_BY_VENDOR, new Object[]{vendorId});
  }

  /**
   *
   * @param vendorId
   * @param filter
   * @return
   */
  static List<VendorAddress> vendorAddressAuto(Integer vendorId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(VendorAddress.class, VENDOR_ADDRESS_BY_VENDOR_AUTO, new Object[]{vendorId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(VendorAddress.class, VENDOR_ADDRESS_BY_VENDOR, new Object[]{vendorId});
  }

  /**
   *
   * @param vendorId
   * @return
   */
  static List<VendorBank> vendorBank(Integer vendorId) {
    return AppLookup.getAutoFilter(VendorBank.class, VENDOR_BANK_BY_VENDOR, new Object[]{vendorId});
  }

  static List<VendorAddress> vendorAddress(Integer vendorId) {
    return AppLookup.getAutoFilter(VendorAddress.class, VENDOR_ADDRESS_BY_VENDOR, new Object[]{vendorId});
  }

  /**
   *
   * @param companyId
   * @return
   */
  public static List<CompanyBank> companyBank(Integer companyId) {
    return AppLookup.getAutoFilter(CompanyBank.class, COMPANY_BANK_BY_COMPANY, new Object[]{companyId});
  }

  /**
   *
   * @param companyId
   * @return
   */
  static List<CompanyAddress> companyAddress(Integer companyId) {
    return AppLookup.getAutoFilter(CompanyAddress.class, COMPANY_ADDRESS_BY_COMPANY, new Object[]{companyId});
  }

  /**
   *
   * @param companyId
   * @param filter
   * @return
   */
  public static List<CompanyBank> companyBankAuto(Integer companyId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(CompanyBank.class, COMPANY_BANK_BY_COMPANY_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(CompanyBank.class, COMPANY_BANK_BY_COMPANY, new Object[]{companyId});
  }

  /**
   *
   * @param companyId
   * @param filter
   * @return
   */
  static List<CompanyAddress> companyAddressAuto(Integer companyId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(CompanyAddress.class, COMPANY_ADDRESS_BY_COMPANY_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(CompanyAddress.class, COMPANY_ADDRESS_BY_COMPANY, new Object[]{companyId});
  }

  /**
   *
   * @param filter
   * @param companyId
   * @return
   */
  public static final List<Account> accountByCompanyAuto(String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Account.class, ACCOUNT_BY_COMPANY_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Account.class, ACCOUNT_BY_COMPANY, new Object[]{companyId});
  }

  public static final List<Account> accountByTradeTypeAuto(String filter, Integer companyId, Integer tradeTypeId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Account.class, ACCOUNT_BY_TRADE_TYPE_AUTO, new Object[]{companyId, tradeTypeId, "%" + filter.toUpperCase() + "%", "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Account.class, ACCOUNT_BY_TRADE_TYPE, new Object[]{companyId, tradeTypeId});
  }

  public static final List<Account> accountByPrimaryVendorAuto(String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Account.class, ACCOUNT_BY_PRIMARY_VENDOR_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%", "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Account.class, ACCOUNT_BY_PRIMARY_VENDOR, new Object[]{companyId});
  }

  public static final List<Manufacture> manufactureByCompanyAuto(String filter, Company company) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Manufacture.class, MANUFACTURE_BY_COMPANY_AUTO, new Object[]{company.getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Manufacture.class, MANUFACTURE_BY_COMPANY, new Object[]{company.getId()});
  }

  public static final List<Brand> brandByCompanyAuto(String filter, Company company) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Brand.class, BRAND_BY_COMPANY_AUTO, new Object[]{company.getId(), "%" + filter.toUpperCase() + "%", "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Brand.class, BRAND_BY_COMPANY, new Object[]{company.getId()});
  }

//  public static final List<Services> salesAgentServicesByServicesAuto(String filter) {
//    if (!StringUtil.isEmpty(filter)) {
//      return AppLookup.getAutoFilter(Services.class, SALESAGENT_BY_SERVICES_AUTO, new Object[]{"%" + filter.toUpperCase() + "%"});
//    }
//    return AppLookup.getAutoFilter(Services.class, SALESAGENT_BY_SERVICES, new Object[]{});
//  }
//
//  public static final List<Services> salesAgentServicesByServicesAuto1(String filter) {
//    if (!StringUtil.isEmpty(filter)) {
//      return AppLookup.getAutoFilter(Services.class, SALESAGENT_BY_SERVICES_AUTO, new Object[]{"%" + filter.toUpperCase() + "%"});
//    }
//    return AppLookup.getAutoFilter(Services.class, SALESAGENT_BY_SERVICES, new Object[]{});
//  }
  public static final List<AccountGroup> accountGroupBySalesAgentAuto(String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountGroup.class, ACCOUNTGROUP_BY_SALES_AGENT_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountGroup.class, ACCOUNTGROUP_BY_SALES_AGENT, new Object[]{companyId});
  }

//  public static final List<AccountGroup> accountGroupBySalesAgentAuto1(String filter) {
//    if (!StringUtil.isEmpty(filter)) {
//      return AppLookup.getAutoFilter(AccountGroup.class, ACCOUNTGROUP_BY_SALES_AGENT_AUTO, new Object[]{"%" + filter.toUpperCase() + "%"});
//    }
//    return AppLookup.getAutoFilter(AccountGroup.class, ACCOUNTGROUP_BY_SALES_AGENT, new Object[]{});
//  }
  public static final List<Territory> territoryBySalesAgentAuto(String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Territory.class, TERRITORY_SALES_AGENT_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Territory.class, TERRITORY_SALES_AGENT, new Object[]{companyId});
  }

//  public static final List<Territory> territoryBySalesAgentAuto1(String filter) {
//    if (!StringUtil.isEmpty(filter)) {
//      return AppLookup.getAutoFilter(Territory.class, TERRITORY_SALES_AGENT_AUTO, new Object[]{"%" + filter.toUpperCase() + "%"});
//    }
//    return AppLookup.getAutoFilter(Territory.class, TERRITORY_SALES_AGENT, new Object[]{});
//  }
  /**
   *
   * @param commodityId
   * @param company
   * @param filter
   * @return
   */
  public static List<ProductCategory> productCategoryAuto(ServiceCommodity commodityId, Company company, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(ProductCategory.class, PRODUCT_CATEGORY_BY_COMMODITY_AUTO, new Object[]{commodityId.getId(), "%" + filter.toUpperCase() + "%", 1, company.getId()});
    }
    return AppLookup.getAutoFilter(ProductCategory.class, PRODUCT_CATEGORY_BY_COMMODITY, new Object[]{commodityId.getId(), 1, company.getId()});
  }

  /**
   *
   * @param commodityId
   * @param filter
   * @return
   */
  public static List<ProductType> productTypeAuto(Integer commodityId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(ProductType.class, PRODUCT_TYPE_BY_COMMODITY_AUTO, new Object[]{commodityId, "%" + filter.toUpperCase() + "%", 1});
    }
    return AppLookup.getAutoFilter(ProductType.class, PRODUCT_TYPE_BY_COMMODITY, new Object[]{commodityId, 1});
  }

  public static List<ProductType> lookupProductTypeByCommodity(ServiceCommodity commodity) {
    if (commodity != null) {
      return AppLookup.getAutoFilter(ProductType.class, PRODUCT_TYPE_BY_COMMODITY, new Object[]{commodity.getId(), 1});
    }
    return null;
  }

  /**
   *
   * @param commodityId
   * @param filter
   * @return
   */
  public static List<ProductClassification> productClassificationAuto(Integer companyId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(ProductClassification.class, PRODUCT_CLASSIFICATION_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%", 1});
    }
    return AppLookup.getAutoFilter(ProductClassification.class, PRODUCT_CLASSIFICATION, new Object[]{companyId, 1});
  }

  /**
   *
   * @param countryId
   * @param filter
   * @return
   */
  public static List<State> addressStateAuto(Integer countryId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(State.class, ADDRESS_STATE_BY_COUNTRY_AUTO, new Object[]{countryId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(State.class, ADDRESS_STATE_BY_COUNTRY, new Object[]{countryId});
  }

  public static List<State> addressStateAuto1(Integer countryId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(State.class, ADDRESS_STATE_BY_DISTRICT_AUTO, new Object[]{countryId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(State.class, ADDRESS_STATE_BY_DISTRICT, new Object[]{countryId});
  }

  /**
   *
   * @param stateId
   * @param filter
   * @return
   */
  public static List<District> addressDistrictAuto(Integer stateId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(District.class, ADDRESS_DISTRICT_BY_STATE_AUTO, new Object[]{stateId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(District.class, ADDRESS_DISTRICT_BY_STATE, new Object[]{stateId});
  }

  public static List<Territory> addressTerritoryAuto(Integer companyId, Integer districtId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Territory.class, ADDRESS_TERRITORY_BY_DISTRICT_AUTO, new Object[]{companyId, districtId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Territory.class, ADDRESS_TERRITORY_BY_DISTRICT, new Object[]{companyId, districtId});
  }

  /**
   *
   * @param vendorId
   * @param filter
   * @return
   */
  public static List<Consignment> consignmentVendorAuto(Integer vendorId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Consignment.class, CONSIGNMENT_VENDOR_AUTO, new Object[]{vendorId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Consignment.class, CONSIGNMENT_VENDOR, new Object[]{vendorId});
  }

  /**
   *
   * @param accountId
   * @param filter
   * @return
   */
  public static List<Consignment> consignmentExpensePurchaseAuto(Integer accountId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Consignment.class, CONSIGNMENT_EXPENSE_PURCHASE_AUTO, new Object[]{accountId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Consignment.class, CONSIGNMENT_EXPENSE_PURCHASE, new Object[]{accountId});
  }

  /**
   *
   * @param accountId
   * @param filter
   * @return
   */
  public static List<Consignment> consignmentExpenseSalesAuto(Integer accountGroupId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Consignment.class, CONSIGNMENT_EXPENSE_SALES_AUTO, new Object[]{accountGroupId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Consignment.class, CONSIGNMENT_EXPENSE_SALES, new Object[]{accountGroupId});
  }

  public static List<Consignment> selectDeliveredConsignmentByVendorAuto(Integer vendorId, Integer accountId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Consignment.class, CONSIGNMENT_DELIVERED_VENDOR_AUTO, new Object[]{vendorId, accountId, 1, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Consignment.class, CONSIGNMENT_DELIVERED_VENDOR, new Object[]{vendorId, accountId, 1});
  }

  public static List<District> districtAuto(Integer stateId) {
    return AppLookup.getAutoFilter(District.class, DISTRICT_BY_STATE, new Object[]{stateId});
  }

  /**
   * Auto filter Parent Company from db based on the user input.
   *
   * @param filter - typed words by the user.
   * @param companyId - editing company id.
   * @return
   */
  public static List<Company> parentCompanyAuto(Integer companyId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Company.class, PARENT_COMPANY_AUTO, new Object[]{companyId, companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Company.class, PARENT_COMPANY, new Object[]{companyId, companyId});
  }

  public static List<Customer> parentCustomerAuto(Integer customerId, String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Customer.class, PARENT_CUSTOMER_AUTO, new Object[]{customerId, "%" + filter.toUpperCase() + "%", companyId});
    }
    return AppLookup.getAutoFilter(Customer.class, PARENT_CUSTOMER, new Object[]{customerId, customerId, companyId});
  }

  public static List<Customer> customerAuto(Integer companyId) {
    return AppLookup.getAutoFilter(Customer.class, CUSTOMER_AUTO_NEW, new Object[]{companyId});
  }

//  public static List<AccountingHead> AccountingHeadAuto(Integer companyId) {
//    return AppLookup.getAutoFilter(AccountingHead.class, ACCOUNTING_AUTO_NEW, new Object[]{companyId});
//  }
//  public static List<BankAccountType> bankAccountType() {
//    return AppLookup.getAutoFilter(BankAccountType.class, BANK_ACC_TYPE, null);
//  }
  public static List<Vendor> consignmentVendorByCompanyAuto(Integer companyId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Vendor.class, VENDOR_BY_COMPANY_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Vendor.class, VENDOR_BY_COMPANY, new Object[]{companyId});
  }

  public static List<Vendor> consignmentVendorByVatApplicable(Integer companyId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Vendor.class, VENDOR_BY_COMPANY_AUTO_VAT_APPLICABLE, new Object[]{companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Vendor.class, VENDOR_BY_COMPANY_VAT_APPLICABLE, new Object[]{companyId});
  }

  public static List<Customer> consignmentCustomerByCompanyAuto(Integer companyId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_COMPANY_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_COMPANY, new Object[]{companyId});
  }

  public static List<Transporter> transporterByCompanyAuto(Integer companyId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Transporter.class, TRANSPORETR_BY_COMPANY_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Transporter.class, TRANSPORTER_BY_COMPANY, new Object[]{companyId});
  }

  public static List<ConsignmentDocType> consignmentDocTypeAuto(Integer typeId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(ConsignmentDocType.class, DOC_TYPE_BY_TRANSPORT_AUTO, new Object[]{typeId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(ConsignmentDocType.class, DOC_TYPE_BY_TRANSPORT, new Object[]{typeId});
  }

  public static List<PurchaseReq> purchaseReqByPoOrderItem(Integer accoutId, Integer confirmStatus, int partialStatus) {
    return AppLookup.getAutoFilter(PurchaseReq.class, "select * from scm_purchase_req pr "
            + "where pr.account_id = ? and (pr.purchase_requisition_status_id = ? OR pr.purchase_requisition_status_id = ?) ",
            new Object[]{accoutId, confirmStatus, partialStatus});
  }

  public static List<ConsignmentReceiptType> consignmentReceiptTypeAuto(Integer typeId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(ConsignmentReceiptType.class, RECEIPT_TYPE_BY_TRANSPORT_AUTO, new Object[]{typeId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(ConsignmentReceiptType.class, RECEIPT_TYPE_BY_TRANSPORT, new Object[]{typeId});
  }

  public static List<TradeProfile> tradeProfileSecondAndThirdLevel() {
    return AppLookup.getAutoFilter(ScmLookupView.tradeProfileClass(), TRADE_PROFILE_SECOND_AND_THIRD_LEVEL, new Object[]{TRADE_PROFILE_SECOND_LEVEL, TRADE_PROFILE_THIRD_LEVEL});
  }

  public static List<ServiceCommodity> vendorCommodityByVendorId(int vendrorId) {
    return AppLookup.getAutoFilter(ServiceCommodity.class, VENDOR_COMMODITY_BY_VENDOR_ID, new Object[]{vendrorId});
  }

  public static List<ServiceCommodity> commodityAuto() {
    return AppLookup.getAutoFilter(ServiceCommodity.class, COMMODITY_AUTO, null);
  }

  public static List<PurchaseReqStatus> purchaseReqStatus() {
    return AppLookup.getAutoFilter(PurchaseReqStatus.class, PURCHASE_REQ_STATUS, null);
  }

  public static List<TaxCode> activetaxCodeAuto(Company company, Integer appiledOn) {
    return AppLookup.getAutoFilter(TaxCode.class, TAX_CODE_COMMODITY, new Object[]{company.getCountryId().getId(), company.getCountryTaxRegimeId().getId(), appiledOn});
  }

  public static List<TaxCode> activetaxCodeSalesAuto(Company companyId) {
    return AppLookup.getAutoFilter(TaxCode.class, ACTIVE_SALES_SURCHARGE, new Object[]{companyId.getCountryId().getId()});
  }

  /**
   * Auto filter AccountGroup from db based on the user input.
   *
   * @param filter - typed words by the user.
   * @return
   */
  public static List<AccountGroup> accountGroupAuto(Company company, String filter) {
    return AppLookup.getAutoFilter(AccountGroup.class, ScmLookupSql.ACCOUNT_GROUP_AUTO, new Object[]{company.getId(), "%" + filter.toUpperCase() + "%"});
  }

  public static List<AccountGroup> defaultAccountGroupByAccount(Account account) {
    //return AppLookup.getAutoFilter(AccountGroup.class, SALEABLE_ACCOUNT_GROUP_BY_ACCOUNT, new Object[]{account.getId(), account.getId()});
    return null;
  }

  public static List<AccountGroupPriceList> accountGroupPriceListByAccountGroup(AccountGroup accountGroupId) {
    return AppLookup.getAutoFilter(AccountGroupPriceList.class, ACCOUNT_GROUP_PRICE_LIST_BY_ACCOUNT_GROUP, new Object[]{accountGroupId.getId()});
  }

  public static List<ServiceCommodity> selectVendorCommodity(AccountGroup accountGroup) {
    return AppLookup.getAutoFilter(ServiceCommodity.class, VENDOR_COMMODITY_BY_ACCOUNT_GROUP, new Object[]{accountGroup.getId()});
  }

  public static List<Customer> selectCustomerBySalesAccount(SalesAccount salesAccount, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_SALES_ACCOUNT_AUTO, new Object[]{salesAccount.getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_SALES_ACCOUNT, new Object[]{salesAccount.getId()});
  }

  /**
   *
   * @param company
   * @return
   */
  public static List<AccountGroup> accountGroupByCompany(Company company) {
    if (company != null && company.getId() != null) {
      return AppLookup.getAutoFilter(AccountGroup.class, ACCOUNT_GROUP_BY_COMPANY, new Object[]{company.getId()});
    }
    return null;
  }

  /**
   *
   * @param salesOrder
   * @return
   */
  static List<AccountGroup> accountGroupBySalesOrder(SalesOrder salesOrder) {
    //AppLookup.getAutoFilter(AccountGroup.class, "select ", new Object[]{salesOrder.getId()});
    return null;
  }

  /**
   *
   * @param company
   * @param account
   * @param filter
   * @return
   */
  public static List<Customer> selectCustomerByCompanyAndAccount(Company company, Account account, String filter) {
    account.getCompanyTradeProfileId().getId();
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_COMPANY_AND_TRADE_PROFILE_AUTO, new Object[]{company.getId(), account.getCompanyTradeProfileId().getId() + 1, filter});
    }
    return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_COMPANY_AND_TRADE_PROFILE, new Object[]{company.getId(), account.getCompanyTradeProfileId().getId() + 1});
  }

  public static List<Vendor> consignmentVendorByCompanyAccountAuto(Integer companyId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Vendor.class, VENDOR_BY_COMPANY_ACCOUNT_AUTO, new Object[]{companyId, UserRuntimeView.instance().getAccount().getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Vendor.class, VENDOR_BY_COMPANY_ACCOUNT, new Object[]{companyId, UserRuntimeView.instance().getAccount().getId()});
  }

  public static List<Customer> customerByCompany(Company company, String filter) {
    List<Customer> list = null;
    if (company != null) {
      if (!StringUtil.isEmpty(filter)) {
        list = AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_COMPANY_AUTO, new Object[]{company.getId(), "%" + filter.toUpperCase() + "%"});
      } else {
        list = AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_COMPANY, new Object[]{company.getId()});
      }
    }
    return list;
  }

  public static List<AccountGroup> accountGroupByCustomerId(Integer customerId) {
    return AppLookup.getAutoFilter(AccountGroup.class, ACCOUNT_GROUP_BY_CUSTOMER, new Object[]{customerId});
  }

  public static List<AccountGroup> accountGroupByVendor(Integer vendorId) {
    return AppLookup.getAutoFilter(AccountGroup.class, ACCOUNT_GROUP_BY_VENDOR, new Object[]{vendorId});
  }

  public static List<AccountGroup> accountGroupByCustomer(Customer customer) {
    return AppLookup.getAutoFilter(AccountGroup.class, ACCOUNT_GROUP_BY_CUSTOMER, new Object[]{customer.getId()});
  }

  /**
   * Method to get the product packing details.
   *
   * @param main
   * @param productEntryDetailId
   * @param productId
   * @param accountId
   * @return
   */
  public static final List<ProductPacking> lookUpProductPackingUnits(ProductBatch productBatchId) {
    List<ProductPacking> packUnitList = null;
    if (productBatchId != null) {
      packUnitList = new ArrayList<>();
      if (productBatchId.getProductPackingDetailId().getPackTertiary() != null) {
        packUnitList.add(productBatchId.getProductPackingDetailId().getPackTertiary());
      }
      if (productBatchId.getProductPackingDetailId().getPackSecondary() != null) {
        packUnitList.add(productBatchId.getProductPackingDetailId().getPackSecondary());
      }
      if (productBatchId.getProductPackingDetailId().getPackPrimary() != null) {
        packUnitList.add(productBatchId.getProductPackingDetailId().getPackPrimary());
      }
    }
    return packUnitList;
  }

  /**
   *
   * @param filter
   * @param accountGroup
   * @param salesReq
   * @param productId
   * @return
   */
  public static final List<Product> selectProductByAccountGroup(String filter, AccountGroup accountGroup, SalesReq salesReq, Integer productId) {
    List<Object> params = new ArrayList<>();
    params.add(accountGroup.getId());
    params.add(salesReq.getId());
    String whereCondition = "WHERE account_id IN (SELECT account_id FROM scm_account_group_detail WHERE account_group_id = ?))"
            + "and id not in (select product_id from scm_sales_req_item where sales_req_id = ?) ";

    String sql = "select * from scm_product WHERE id IN (SELECT product_id FROM scm_product_preset ";

    if (productId != null) {
      whereCondition = "WHERE account_id IN (SELECT account_id FROM scm_account_group_detail WHERE account_group_id = ?)) "
              + "and id not in (select product_id from scm_sales_req_item where sales_req_id = ? and product_id <> ?) ";
      params.add(productId);
    }
    sql += whereCondition;

    if (!StringUtil.isEmpty(filter)) {
      sql = sql + " and upper(product_name) like ? order by upper(product_name) asc";
      params.add("%" + filter.toUpperCase() + "%");
    } else {
      sql = sql + " order by upper(product_name) asc";
    }
    if (!params.isEmpty()) {
      return AppLookup.getAutoFilter(Product.class, sql, params.toArray());
    }
    return null;
  }

  /**
   *
   * @param accountGroupId
   * @param filter
   * @return
   */
  public static final List<Customer> selectCustomerByAccountGroup(AccountGroup accountGroupId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_ACCOUNT_GROUP_AUTO, new Object[]{accountGroupId.getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_ACCOUNT_GROUP, new Object[]{accountGroupId.getId()});
  }

  public static final List<Customer> selectCustomerByAccountGroup(AccountGroup accountGroupId) {
    return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_ACCOUNT_GROUP, new Object[]{accountGroupId.getId()});
  }

  /**
   *
   * @param filter
   * @param customer
   * @param salesAccount
   * @return
   */
  public static final List<AccountGroup> accountGroupForSalesAccount(String filter, Customer customer, SalesAccount salesAccount) {
    StringBuilder sql = new StringBuilder();
    List<Object> param = new ArrayList<>();
    param.add(customer.getCompanyId().getId());
    param.add(customer.getId());
    sql.append("select * from scm_account_group where company_id = ? and id in (select distinct account_group_id from scm_account_group_detail)"
            + "and id not in (select account_group_id from scm_sales_account sa where sa.customer_id = ?) ");
    if (!StringUtil.isEmpty(filter)) {
      sql.append(" and upper(group_name) like ? order by upper(group_name) asc");
      param.add("%" + filter.toUpperCase() + "%");
      return AppLookup.getAutoFilter(AccountGroup.class, sql.toString(), param.toArray());
    } else {
      sql.append(" order by upper(group_name) asc");
      return AppLookup.getAutoFilter(AccountGroup.class, sql.toString(), param.toArray());
    }
  }

  /**
   * Method to get list of vendor commodity.
   *
   * @param vendorId
   * @return
   */
  public static final List<ServiceCommodity> selectVendorCommodityByVendor(Integer vendorId) {
    List<ServiceCommodity> list = null;
    if (vendorId != null) {
      return AppLookup.getAutoFilter(ServiceCommodity.class, "select * from scm_service_commodity where id in (select commodity_id from scm_vendor_commodity where vendor_id = ?)", new Object[]{vendorId});
    }
    return list;
  }

  /**
   * Method to get the list of product type based on commodity.
   *
   * @param commodityId
   * @return
   */
  public static final List<ProductType> productTypeByCommodity(Integer commodityId) {
    if (commodityId != null) {
      return AppLookup.getAutoFilter(ProductType.class, "select * from scm_product_type where commodity_id = ?", new Object[]{commodityId});
    }
    return null;
  }

  public static final List<ProductType> lookupProductTypeByAccount(Integer accountId) {
    String sql = "select min(t1.id) as id,t1.product_type_code,t1.title from scm_product_type t1 "
            + "inner join scm_product t2 on t1.id = t2.product_type_id "
            + "inner join scm_product_preset t3 on t3.product_id = t2.id and t3.account_id = ? "
            + "group by t1.product_type_code,t1.title";
    if (accountId != null) {
      return AppLookup.getAutoFilter(ProductType.class, sql, new Object[]{accountId});
    }
    return null;

  }

  public static final List<Vendor> selectPrimaryVendors(Vendor vendor, Company company) {
    if (company != null) {
      if (vendor != null && vendor.getId() != null) {
        return AppLookup.getAutoFilter(Vendor.class, "select * from scm_vendor where vendor_type = 1 and company_id = ? and id <> ?", new Object[]{company.getId(), vendor.getId()});
      } else {
        return AppLookup.getAutoFilter(Vendor.class, "select * from scm_vendor where vendor_type = 1 and company_id = ?", new Object[]{company.getId()});
      }
    }
    return null;
  }

  public static final List<ServiceCommodity> selectCommodityByVendor(Vendor primaryVendorId) {
    return AppLookup.getAutoFilter(ServiceCommodity.class, "select * from scm_service_commodity where id in(select commodity_id from scm_vendor_commodity where vendor_id = ?)", new Object[]{primaryVendorId.getId()});
  }

  public static final List<ServiceCommodity> selectCommodity() {
    return AppLookup.getAutoFilter(ServiceCommodity.class, ScmLookupSql.COMMODITY, null);
  }

//  public static final List<Commodity> selectCommodityByCompany(Company company) {
//    return AppLookup.getAutoFilter(Commodity.class, "select * from scm_service_commodity where id in(select commodity_id from scm_service_commodity_tax where company_id = ?)", new Object[]{company.getId()});
//  }
  public static final List<ServiceCommodity> lookupCommodityByCountryAndCompany(Company company) {
    return AppLookup.getAutoFilter(ServiceCommodity.class, "select * from scm_service_commodity where company_id = ? and commodity_or_service = 1 or (country_id = ? and company_id is null)", new Object[]{company.getId(), company.getCountryId().getId()});
  }

  public static final List<ServiceCommodity> lookupCommodityByCountryAndCompany(String filter, Company company) {
    if (!StringUtil.isEmpty(filter)) {
      filter = "%" + filter + "%";
      return AppLookup.getAutoFilter(ServiceCommodity.class, "select * from scm_service_commodity where commodity_or_service = 1 and (company_id = ? or (country_id = ? and company_id is null)) and upper(title) like upper(?)", new Object[]{company.getId(), company.getCountryId().getId(), filter});
    } else {
      return AppLookup.getAutoFilter(ServiceCommodity.class, "select * from scm_service_commodity where commodity_or_service = 1 and (company_id = ? or (country_id = ? and company_id is null))", new Object[]{company.getId(), company.getCountryId().getId()});
    }
  }

  public static final List<AccountGroup> accountGroupByCustomerAccount(Integer customerId, Account account) {
    return AppLookup.getAutoFilter(AccountGroup.class, ACCOUNT_GROUP_BY_CUSTOMER_AND_ACCOUNT, new Object[]{customerId, account.getId()});
  }

  public static final List<AccountGroup> accountGroupByAccount(Account account) {
    return AppLookup.getAutoFilter(AccountGroup.class, ACCOUNT_GROUP_BY_ACCOUNT, new Object[]{account.getId(), account.getCompanyId().getId()});
  }

  public static final List<VendorContact> selectVendorContactByAccountGroup(AccountGroup accountGroupId) {
    return AppLookup.getAutoFilter(VendorContact.class, "select * from scm_vendor_contact where vendor_id in (select vendor_id from scm_account where id in (select account_id from scm_account_group_detail where account_group_id = ?))", new Object[]{accountGroupId.getId()});
  }

  public static final List<UserProfile> selectCompanySalesAgentUserProfile(Company company, AccountGroup accountGroupId) {
    return AppLookup.getAutoFilter(UserProfile.class, "select * from scm_user_profile scm_user_profile "
            + "where scm_user_profile.id not in(select scm_sales_agent_contract.user_profile_id from scm_sales_agent_contract scm_sales_agent_contract where scm_sales_agent_contract.company_id = ?) "
            + "and scm_user_profile.company_id = ? and scm_user_profile.vendor_id is null and scm_user_profile.customer_id is null and scm_user_profile.transporter_id is null and designation_id = ? "
            + "and id in(select sales_agent_id from scm_sales_agent_account_group where account_group_id = ?)",
            new Object[]{company.getId(), company.getId(), UserProfileService.DESIGNATION_SALES_AGENT, accountGroupId.getId()});
  }

  public static final List<CompanyAddress> companyAddressByCompany(Company company) {
    List<CompanyAddress> list = null;
    if (company != null) {
      list = AppLookup.getAutoFilter(CompanyAddress.class, "select id,address,district_id,pin,state_id,address_type_id from scm_company_address where company_id = ? and status_id = 1", new Object[]{company.getId()});
    }

    return list;
  }

  public static final List<CustomerAddress> customerAddressByCustomer(Customer customer, boolean showAll) {
    List<CustomerAddress> list = null;
    if (customer != null) {
      if (showAll) {
        list = AppLookup.getAutoFilter(CustomerAddress.class, "select id,address,district_id,pin,state_id,address_type_id,email,phone_1 from scm_customer_address where customer_id = ? and status_id = 1", new Object[]{customer.getId()});
      } else {
        list = AppLookup.getAutoFilter(CustomerAddress.class, "select id,address,district_id,pin,state_id,address_type_id,email,phone_1 from scm_customer_address where customer_id = ? and status_id = 1 and address_type_id=1", new Object[]{customer.getId()});
      }
    }

    return list;
  }

  public static List<UserProfile> userProfileAuto(String filter, Integer companyId) {
    Object[] params = new Object[]{companyId, UserProfileService.DESIGNATION_SALES_AGENT, "%" + filter.toUpperCase() + "%"};
    return AppLookup.getAutoFilter(ScmLookupView.userProfileClass(), USERPROFILE_AUTO, params);
  }

  public static final List<Product> selectProductByAccountGroup(String filter, AccountGroup accountGroup, SalesOrder salesOrder, Integer productId) {
    List<Object> params = new ArrayList<>();
    params.add(accountGroup.getId());
    params.add(salesOrder.getId());

    String sql = "select * from scm_product WHERE id IN (select scm_product_batch.product_id from scm_product_detail,scm_product_batch where scm_product_detail.product_batch_id = scm_product_batch.id  ";

    String whereCondition = " and  scm_product_detail.account_id IN (SELECT account_id FROM scm_account_group_detail WHERE account_group_id = ?)) "
            + "and id not in (select product_id from scm_sales_order_item where sales_order_id = ?) ";

    if (productId != null) {
      whereCondition = " and scm_product_detail.account_id IN (SELECT account_id FROM scm_account_group_detail WHERE account_group_id = ?)) "
              + "and id not in (select product_id from scm_sales_order_item where sales_order_id = ? and product_id <> ?) ";
      params.add(productId);
    }
    sql = sql + whereCondition;

    if (!StringUtil.isEmpty(filter)) {
      sql = sql + " and upper(product_name) like ? order by upper(product_name) asc";
      params.add("%" + filter.toUpperCase() + "%");
    } else {
      sql = sql + " order by upper(product_name) asc";
    }
    if (!params.isEmpty()) {
      return AppLookup.getAutoFilter(Product.class, sql, params.toArray());
    }
    return null;
  }

  /**
   * Method to select list of customer based on the account they belongs.
   *
   * @param account
   * @param filter
   * @return
   */
  public static final List<Customer> selectCustomerByAccount(Account account, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_ACCOUNT_AUTO, new Object[]{account.getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Customer.class, CUSTOMER_BY_ACCOUNT, new Object[]{account.getId()});
  }

  public static final List<AccountGroup> selectAccountGroupByCustomer(Customer customer) {
    return AppLookup.getAutoFilter(AccountGroup.class, "select * from scm_account_group where id in (select account_group_id from scm_sales_account where customer_id = ?)", new Object[]{customer.getId()});
  }

  public static final List<TaxCode> selectProductCommodityTaxCode() {
    return AppLookup.getAutoFilter(TaxCode.class, "select * from scm_tax_code where tax_head_id in (select id from scm_tax_head where applied_on = ?)", new Object[]{TaxHeadService.PRODUCT_COMMODITY});
  }

  public static final List<TaxCode> selectProductCategoryTaxCode() {
    return AppLookup.getAutoFilter(TaxCode.class, "select * from scm_tax_code where tax_head_id in (select id from scm_tax_head where applied_on = ?)", new Object[]{TaxHeadService.PRODUCT_CATEGORY});
  }

  public static final List<CountryTaxRegime> selectCountryTaxRegimeByCountry(Country countryId) {
    return AppLookup.getAutoFilter(CountryTaxRegime.class, "select * from scm_country_tax_regime where country_id = ?", new Object[]{countryId.getId()});
  }

  public static final List<TaxCode> taxCodeByCountryTaxRegime(Company company) {
    return AppLookup.getAutoFilter(TaxCode.class, "select * from scm_tax_code where country_id = ? and tax_head_id in"
            + "(select id from scm_tax_head where country_tax_regime_id = ? and applied_on = ?)", new Object[]{company.getCountryId().getId(), company.getCountryTaxRegimeId().getId(), TaxHeadService.PRODUCT_COMMODITY});
  }

  public static final List<TaxCode> lookupGstTaxCode(Company company) {
    return AppLookup.getAutoFilter(TaxCode.class, "select * from scm_tax_code where country_id = ? and parent_id is null and tax_head_id = ? order by rate_percentage asc",
            new Object[]{company.getCountryId().getId(), TaxHeadService.TAX_HEAD_GST});
  }

  public static final List<TaxCode> lookupTdsTaxCode(Company company) {
    return AppLookup.getAutoFilter(TaxCode.class, "select * from scm_tax_code where country_id = ? and parent_id is null and tax_head_id = ?",
            new Object[]{company.getCountryId().getId(), TaxHeadService.TAX_HEAD_TDS});
  }

  public static final List<TaxCode> lookupGstCessTaxCode(Company company) {
    return AppLookup.getAutoFilter(TaxCode.class, "select * from scm_tax_code where country_id = ? and parent_id is null and tax_head_id in"
            + "(select id from scm_tax_head where country_tax_regime_id = ? and applied_on = ? and computed_on = ?)",
            new Object[]{company.getCountryId().getId(), company.getCountryTaxRegimeId().getId(), TaxHeadService.HSN_AND_SAC, TaxHeadService.COMPUTED_ON_TAX});
  }

  public static final List<ServiceCommodity> commodityServiceAuto(String filter, Company company) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(ServiceCommodity.class, SERVICE_COMMODITY_AUTO, new Object[]{company.getCountryId().getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(ServiceCommodity.class, SERVICE_COMMODITY, new Object[]{company.getCountryId().getId()});
  }

  /**
   *
   * @param filter
   * @param accountId
   * @param companyId
   * @param purchaseReqId
   * @param productId
   * @return
   */
  public static final List<Product> selectProductForPurchaseReq(String filter, Integer accountId, Integer companyId, Integer purchaseReqId, Integer productId) {
    StringBuilder condition = new StringBuilder();
    StringBuilder searchQuery = new StringBuilder();
    List<Object> params = new ArrayList<>();
    filter = "%" + filter + "%";
    params.add(accountId);
    params.add(companyId);
    params.add(SystemConstants.PRODUCT_STATUS_ACTIVE);

    if (purchaseReqId != null) {
      if (productId != null) {
        condition.append(" and scm_product.id not in (select product_id from scm_purchase_req_item where purchase_requisition_id = ? and product_id <> ?) ");
        params.add(purchaseReqId);
        params.add(productId);
      } else {
        condition.append(" and scm_product.id not in (select product_id from scm_purchase_req_item where purchase_requisition_id = ?) ");
        params.add(purchaseReqId);
      }
      if (!StringUtil.isEmpty(filter)) {
        condition.append(" and UPPER(scm_product.product_name) LIKE UPPER(?) ");
        params.add(filter.toUpperCase());
      }
    }

    searchQuery.append("SELECT * FROM scm_product scm_product "
            + "INNER JOIN scm_product_preset ON  scm_product.id = scm_product_preset.product_id  AND scm_product_preset.account_id = ? "
            + "WHERE scm_product.company_id = ? and scm_product.product_status_id = ? ").append(condition).append(" ORDER BY scm_product.product_name ASC");
    return AppLookup.getAutoFilter(Product.class, searchQuery.toString(), params.toArray());
  }

  /**
   *
   * @param countryTaxRegimeId
   * @return
   */
  public static final List<TaxHead> selectTaxHeadByRegime(CountryTaxRegime countryTaxRegimeId) {
    return AppLookup.getAutoFilter(TaxHead.class, "select * from scm_tax_head where country_tax_regime_id = ?", new Object[]{countryTaxRegimeId.getId()});
  }

  public static final List<Product> selectProductForPurchaseOrder(String filter, Integer accountId, Integer companyId) {
    StringBuilder searchQuery = new StringBuilder();
    List<Object> params = new ArrayList<>();
    filter = "%" + filter + "%";
    params.add(accountId);
    params.add(SystemConstants.PRODUCT_STATUS_ACTIVE);
    params.add(companyId);
    params.add(filter.toUpperCase());

    searchQuery.append("select t1.id,t1.product_name from scm_product t1 "
            + "inner join scm_product_preset t2 on t1.id = t2.product_id and t2.account_id = ? "
            + "where t1.product_status_id = ? and t1.company_id = ? and upper(t1.product_name) like upper(?)").append(" ORDER BY t1.product_name ASC");
    return AppLookup.getAutoFilter(Product.class, searchQuery.toString(), params.toArray());
  }

  public static final List<AddressType> selectCompanyAddressType(String filter, CompanyAddress companyAddress) {
    filter = "%" + filter + "%";
    if (companyAddress != null && companyAddress.getAddressTypeId() != null && companyAddress.getAddressTypeId().getId().equals(AddressTypeService.REGISTERED_ADDRESS)) {
      return AppLookup.getAutoFilter(AddressType.class, "select id,title from scm_address_type where id = ? and upper(title) like ? order by upper(title) asc", new Object[]{AddressTypeService.REGISTERED_ADDRESS, filter});
    } else {
      return AppLookup.getAutoFilter(AddressType.class, "select id,title from scm_address_type where id <> ? and  upper(title) LIKE upper(?) order by upper(title) asc", new Object[]{AddressTypeService.REGISTERED_ADDRESS, filter});
    }
  }

  public static final List<AddressType> selectVendorAddressType(String filter, VendorAddress vendorAddress) {
    filter = "%" + filter + "%";
    if (vendorAddress != null && vendorAddress.getAddressTypeId() != null && vendorAddress.getAddressTypeId().getId().equals(AddressTypeService.REGISTERED_ADDRESS)) {
      return AppLookup.getAutoFilter(AddressType.class, "select id,title from scm_address_type where id = ? and upper(title) like ? order by upper(title) asc", new Object[]{AddressTypeService.REGISTERED_ADDRESS, filter});
    } else {
      return AppLookup.getAutoFilter(AddressType.class, "select id,title from scm_address_type where id <> ? and  upper(title) LIKE upper(?) order by upper(title) asc", new Object[]{AddressTypeService.REGISTERED_ADDRESS, filter});
    }
  }

  public static final List<AddressType> selectCustomerAddressType(String filter, CustomerAddress customerAddress) {
    filter = "%" + filter + "%";
    if (customerAddress != null && customerAddress.getAddressTypeId() != null && customerAddress.getAddressTypeId().getId().equals(AddressTypeService.REGISTERED_ADDRESS)) {
      return AppLookup.getAutoFilter(AddressType.class, "select id,title from scm_address_type where id = ? and upper(title) like ? order by upper(title) asc", new Object[]{AddressTypeService.REGISTERED_ADDRESS, filter});
    } else {
      return AppLookup.getAutoFilter(AddressType.class, "select id,title from scm_address_type where id <> ? and  upper(title) LIKE upper(?) order by upper(title) asc", new Object[]{AddressTypeService.REGISTERED_ADDRESS, filter});
    }
  }

  public static List<State> lookupStateByCountry(Country country, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      filter = "%" + filter.toUpperCase() + "%";
      return AppLookup.getAutoFilter(State.class, "select * from scm_state where country_id = ? and upper(state_name) like ? order by upper(state_name) asc", new Object[]{country.getId(), filter});
    } else {
      return AppLookup.getAutoFilter(State.class, "select * from scm_state where country_id = ? order by upper(state_name) asc", new Object[]{country.getId()});
    }
  }

  public static List<Manufacture> manufacturerByProductAuto(Product product) {
    if (product != null) {
      return AppLookup.getAutoFilter(Manufacture.class, "select * from scm_manufacture where id in (select manufacture_id from scm_manufacture_product where product_id = ?)", new Object[]{product.getId()});
    }
    return null;
  }

  public static final List<Product> selectProductsOfAccountGroup(String filter, AccountGroup ag, SalesReq sr, Integer productId) {
    String cond = "";
    String filterCond = "";
    List<Object> params = new ArrayList<>();
    params.add(ag.getId());
    params.add(ag.getId());
    params.add(sr.getId());
    if (productId != null) {
      cond = "and product_id <> ? ";
      params.add(productId);
    }

    if (!StringUtil.isEmpty(filter)) {
      filterCond = " and upper(p.product_name) like ? order by upper(p.product_name) asc";
      params.add(filter);
    } else {
      filterCond = " order by upper(p.product_name) asc";
    }
    String sql = "select distinct p.* from scm_product p "
            + "inner join scm_product_preset pp on pp.product_id = p.id and pp.account_id in(SELECT account_id FROM scm_account_group_detail WHERE account_group_id = ?) "
            + "left join scm_product_detail pd on pd.product_id = p.id and pd.account_id in(SELECT account_id FROM scm_account_group_detail WHERE account_group_id = ?) "
            + "where p.id not in (select product_id from scm_sales_req_item where sales_req_id = ? " + cond + ") " + filterCond;

    return null;
  }

  public static final List<SalesReturnReceipt> lookupSalesReturnReceiptByCustomer(Customer customerId) {
    List<SalesReturnReceipt> list = null;
    if (customerId != null) {
      list = AppLookup.getAutoFilter(SalesReturnReceipt.class, "select * from scm_sales_return_receipt where customer_id = ?", new Object[]{customerId.getId()});
    }
    return list;
  }

  public static final List<Consignment> lookupSalesReturnConsignmentByCustomer(Customer customerId) {
    List<Consignment> list = null;
    if (customerId != null) {
      list = AppLookup.getAutoFilter(Consignment.class, "select t1.* from scm_consignment t1 "
              + "inner join scm_consignment_receipt t2 on t1.id = t2.consignment_id "
              + "inner join scm_consignment_detail t3 on t2.consignment_id = t1.id "
              + "where t1.consignment_type_id =  ? and customer_id = ? "
              + "group by t1.id", new Object[]{SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN, customerId.getId()});
    }
    return list;
  }

  public static final List<SalesInvoice> lookupSalesInvoiceByCustomer(Customer customerId) {
    List<SalesInvoice> list = null;
    if (customerId != null) {
      list = AppLookup.getAutoFilter(SalesInvoice.class, "select * from scm_sales_invoice where customer_id = ? and sales_invoice_status_id = ?", new Object[]{customerId.getId(), SystemConstants.CONFIRMED});
    }
    return list;
  }

  public static final List<Customer> selectCustomerByCompany(Company company, String filter) {
    List<Customer> list = null;
    if (company != null) {
      if (!StringUtil.isEmpty(filter)) {
        filter = "%" + filter + "%";
        list = AppLookup.getAutoFilter(Customer.class, "select id,customer_name from scm_customer where company_id = ? and upper(customer_name) like upper(?) order by upper(customer_name)", new Object[]{company.getId(), filter});
      } else {
        list = AppLookup.getAutoFilter(Customer.class, "select id,customer_name from scm_customer where company_id = ? order by upper(customer_name)", new Object[]{company.getId()});
      }
    }
    return list;
  }

  public static final List<Customer> lookupCustomerBySalesInvoice(Company company, AccountGroup accountGroup, String filter) {
    List<Customer> list = null;
    if (company != null) {
      if (!StringUtil.isEmpty(filter)) {
        filter = "%" + filter + "%";
        list = AppLookup.getAutoFilter(Customer.class, "select * from scm_customer where company_id = ? and upper(customer_name) like upper(?) and id in(select customer_id from scm_sales_account where account_group_id = ? ) order by customer_name",
                new Object[]{company.getId(), filter, accountGroup.getId()});
      } else {
        list = AppLookup.getAutoFilter(Customer.class, "select * from scm_customer where company_id = ? and id in(select customer_id from scm_sales_account where account_group_id = ? ) order by customer_name",
                new Object[]{company.getId(), accountGroup.getId()});
      }
    }
    return list;
  }

  public static final List<AccountGroup> lookupAccountGroupByCustomer(Customer customerId) {
    List<AccountGroup> list = null;
    if (customerId != null) {
      list = AppLookup.getAutoFilter(AccountGroup.class, "select * from scm_account_group where id in(select account_group_id from scm_sales_account where customer_id = ?)", new Object[]{customerId.getId()});
    }
    return list;
  }

  /**
   *
   * @param countryId
   * @param filter
   * @return
   */
  public static List<Bank> bankAutoFilterByCompanyCountry(Integer countryId, String filter) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Bank.class, BANK_AUTO_FILTER_BY_COUNTRY, new Object[]{countryId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(Bank.class, BANK_FILTER_BY_COUNTRY, new Object[]{countryId});
  }

  /**
   *
   * @param company
   * @return
   */
  public static final List<SupplierGroup> lookupSupplierGroup(Company company) {
    List<SupplierGroup> list = null;
    if (company != null) {
      list = AppLookup.getAutoFilter(SupplierGroup.class, "select * from scm_supplier_group where company_id = ?", new Object[]{company.getId()});
    }
    return list;
  }

  public static List<Product> lookupProductByAccount(String filter, Account accountId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(Product.class, "select * from scm_product "
              + "where upper(product_name) like upper(?) and id in(select product_id from scm_product_batch "
              + "where id in(select product_batch_id from scm_product_detail where account_id = ?))",
              new Object[]{"%" + filter.toUpperCase() + "%", accountId.getId()});
    }
    return AppLookup.getAutoFilter(Product.class, "select * from scm_product "
            + "where id in(select product_id from scm_product_batch "
            + "where id in(select product_batch_id from scm_product_detail where account_id = ?)) ",
            new Object[]{accountId.getId()});
  }

  public static List<ProductDetail> lookupProductForStockAdjustment(String filter, Account accountId, AccountGroup accountGroupId) {
    List<ProductDetail> list = null;
    ArrayList<Object> params = new ArrayList<>();
    String sql = "select pd.* from scm_product_detail pd ";
    String join = "left join scm_product_entry_detail ped on ped.product_detail_id=pd.id inner join scm_product_batch pb on pd.product_batch_id = pb.id  ";
    String cond = "";
    if (accountId != null) {
      join += "and pd.account_id = ? ";
      params.add(accountId.getId());
    } else if (accountGroupId != null) {
      join += "and pd.account_id in (SELECT account_id FROM scm_account_group_detail WHERE account_group_id =?) ";
      params.add(accountGroupId.getId());
    }
    join += "inner join scm_product p on pb.product_id = p.id and p.product_status_id = ? "
            + "where pd.id in(select product_detail_id from scm_product_entry_detail "
            + "where product_entry_id in(select id from scm_product_entry ";
    params.add(SystemConstants.PRODUCT_STATUS_ACTIVE);
    if (accountId != null) {
      cond += "where account_id = ? ";
      params.add(accountId.getId());
    } else if (accountGroupId != null) {
      cond += "where account_id in(SELECT account_id FROM scm_account_group_detail WHERE account_group_id =?) ";
      params.add(accountGroupId.getId());
    }
    cond += "and product_entry_status_id > ?)) ";
    params.add(SystemConstants.DRAFT);
    sql += join + cond;
    if (!StringUtil.isEmpty(filter)) {
      params.add("%" + filter.toUpperCase() + "%");
      sql += "and upper(p.product_name) like upper(?) group by pd.id,p.id order by p.product_name asc";
    } else {
      sql += " group by p.id,pd.id order by p.product_name ";
    }
    list = AppLookup.getAutoFilter(ProductDetail.class, sql, params.toArray());

    return list;
  }

  /**
   *
   * @return
   */
  public static List<ConsignmentStatus> lookupConsignmentReceiptStatus() {
    return AppLookup.getAutoFilter(ConsignmentStatus.class, "select * from scm_consignment_status where id in(?,?)", new Object[]{ConsignmentStatusService.RECEIVED_DELIVERED, ConsignmentStatusService.LOST_FULLY_DAMAGED_DELIVERED});
  }

  /**
   *
   * @param filter
   * @param company
   * @return
   */
  public static List<Bank> lookupBankByCountry(String filter, Company company) {
    List<Bank> list = null;
    if (company != null && company.getCountryId() != null) {
      if (!StringUtil.isEmpty(filter)) {
        list = AppLookup.getAutoFilter(Bank.class, "select * from scm_bank where country_id = ? and upper(name) like ? order by upper(name) asc", new Object[]{company.getCountryId().getId(), "%" + filter.toUpperCase() + "%"});
      } else {
        list = AppLookup.getAutoFilter(Bank.class, "select * from scm_bank where country_id = ? order by upper(name) asc", new Object[]{company.getCountryId().getId()});
      }
    }
    return list;
  }

  public static List<Account> lookupAccountByAccountGroup(AccountGroup accountGroupId) {
    return AppLookup.getAutoFilter(Account.class, "select * from scm_account where account_status_id = ? and id in (select account_id from scm_account_group_detail where account_group_id = ?)", new Object[]{AccountService.ACTIVE, accountGroupId.getId()});
  }

  public static List<ProductType> lookupProductType() {
    return AppLookup.getAutoFilter(ProductType.class, "(select id,product_type_code,title from scm_product_type where product_type_code = ?  limit 1) "
            + "union "
            + "(select id,product_type_code,title from scm_product_type where product_type_code = ? limit 1)", new Object[]{SystemConstants.PRODUCT_TYPE_GENERAL, SystemConstants.PRODUCT_TYPE_COLD_CHAIN});
  }

  public static List<ProductCategory> lookupProductCategory() {
    return AppLookup.getAutoFilter(ProductCategory.class, "select * from scm_product_category where purchase_tax_code_id is not null", null);
  }

  public static List<ProductCategory> lookupProductCategoryAuto(String filter) {
    if (!StringUtil.isEmpty(filter)) {
      filter = "%" + filter + "%";
      return AppLookup.getAutoFilter(ProductCategory.class, "select * from scm_product_category where purchase_tax_code_id is not null and title like ?", new Object[]{filter});
    } else {
      return AppLookup.getAutoFilter(ProductCategory.class, "select * from scm_product_category where purchase_tax_code_id is not null", null);
    }
  }

  public static final List<TaxCode> lookupPurchaseTaxCode(Company company, TaxCode taxCodeId) {
    if (taxCodeId != null) {
      return AppLookup.getAutoFilter(TaxCode.class, "select * from scm_tax_code where country_id = ? and parent_id is null and tax_head_id = ? "
              + "and id <> ? ",
              new Object[]{company.getCountryId().getId(), TaxHeadService.TAX_HEAD_GST, taxCodeId.getId()});
    } else {
      return AppLookup.getAutoFilter(TaxCode.class, "select * from scm_tax_code where country_id = ? and parent_id is null and tax_head_id = ? ",
              new Object[]{company.getCountryId().getId(), TaxHeadService.TAX_HEAD_GST});
    }
  }

  /**
   *
   * @param company
   * @return
   */
  public static List<Manufacture> lookupManufacture(Company company) {
    List<Manufacture> list = null;
    if (company != null) {
      list = AppLookup.getAutoFilter(Manufacture.class, "select * from scm_manufacture where company_id = ? ", new Object[]{company.getId()});
    }
    return list;
  }

  /**
   *
   * @param company
   * @return
   */
  public static List<Brand> lookupBrandByCompany(Company company) {
    List<Brand> list = null;
    if (company != null) {
      list = AppLookup.getAutoFilter(Brand.class, "select * from scm_brand where company_id = ? ", new Object[]{company.getId()});
    }
    return list;
  }

  /**
   *
   * @param productPackingType
   * @return
   */
  public static List<ProductPacking> lookupProductPacking(Integer productPackingType) {
    return AppLookup.getAutoFilter(ProductPacking.class, "select * from scm_product_packing where pack_type = ?", new Object[]{productPackingType});
  }

  /**
   *
   * @param productUnitId
   * @return
   */
  public static List<ProductPackingDetail> lookupProductPackingDetail(ProductUnit productUnitId) {
    String sql = "SELECT * FROM scm_product_packing_detail where pack_primary in(select product_packing_id from scm_product_packing_unit where product_unit_id = ?) \n"
            + "ORDER BY pack_primary,pack_secondary,pack_tertiary,pack_tertiary_secondary_qty,pack_secondary_primary_qty,pack_primary_dimension";
    return AppLookup.getAutoFilter(ProductPackingDetail.class, sql, new Object[]{productUnitId.getId()});
  }

  public static List<PlatformDescription> lookupPlatformDescription() {
    return AppLookup.getAutoFilter(PlatformDescription.class, "select id,title from scm_platform_description order by upper(title) asc", null);
  }

  public static List<Account> accountByAccountGroupProfileAll(MainView main, AccountGroup accountGroup, String filter) {
    List<Account> list = ScmLookupExtView.accountByAccountGroupProfile(main, accountGroup, filter);
    if (list != null || list.size() > 0) {
      list.add(0, new Account(0, SystemConstants.ALL_ACCOUNT));
    }
    return list;
  }

  public static List<Account> accountByAccountGroupProfile(MainView main, AccountGroup accountGroup, String filter) {
    if (accountGroup != null) {
      if (!StringUtil.isEmpty(filter)) {
        if (!UserRuntimeView.instance().getAppUser().isRoot() && !AppService.exist(main, "select id from scm_user_account where user_id=? and account_id is null", new Object[]{UserRuntimeView.instance().getAppUser().getId()})) {
          return AppLookup.getAutoFilter(Account.class, ACCOUNT_BY_ACCOUNTGROUP_PROFILE_AUTO, new Object[]{ACTIVE_ACCOUNT, accountGroup.getId(), UserRuntimeView.instance().getAppUser().getId(), "%" + filter.toUpperCase() + "%"});
        } else {
          return AppLookup.getAutoFilter(Account.class, ACCOUNT_BY_ACCOUNTGROUP_AUTO, new Object[]{ACTIVE_ACCOUNT, accountGroup.getId(), "%" + filter.toUpperCase() + "%"});
        }
      } else {
        if (!UserRuntimeView.instance().getAppUser().isRoot() && !AppService.exist(main, "select id from scm_user_account where user_id=? and account_id is null", new Object[]{UserRuntimeView.instance().getAppUser().getId()})) {
          return AppLookup.getAutoFilter(Account.class, ACCOUNT_BY_ACCOUNTGROUP_PROFILE_AUTO, new Object[]{ACTIVE_ACCOUNT, accountGroup.getId(), UserRuntimeView.instance().getAppUser().getId(), "%"});
        } else {
          return AppLookup.getAutoFilter(Account.class, ACCOUNT_BY_ACCOUNTGROUP_AUTO, new Object[]{ACTIVE_ACCOUNT, accountGroup.getId(), "%"});
        }
      }
    }
    return null;
  }

  public static List<ProductBatch> productBatchAuto(String filter, Product product) {
    if (product != null) {
      if (!StringUtil.isEmpty(filter)) {
        return AppLookup.getAutoFilter(ProductBatch.class, PRODUCT_BATCH_BY_PRODUCT_AUTO, new Object[]{product.getId(), "%" + filter.toUpperCase() + "%"});
      } else {
        return AppLookup.getAutoFilter(ProductBatch.class, PRODUCT_BATCH_BY_PRODUCT, new Object[]{product.getId()});
      }
    }
    return null;
  }

  public static final List<ServiceCommodity> lookupServiceCommodity(Company company, String filter) {

    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(ServiceCommodity.class, SERVICES_AUTO, new Object[]{CommodityService.SERVICE, company.getId(), company.getCountryId().getId(), "%" + filter.toUpperCase() + "%"});
    } else {
      return AppLookup.getAutoFilter(ServiceCommodity.class, SERVICES, new Object[]{CommodityService.SERVICE, company.getId(), company.getCountryId().getId()});
    }
  }

  public static List<SupplierGroup> supplierGroupAutoByComapny(Company company, String filter) {

    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(SupplierGroup.class, SUPPLIER_GROUP_AUTO, new Object[]{company.getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(SupplierGroup.class, SUPPLIER_GROUP, new Object[]{company.getId()});

  }

  public static final List<TaxCode> lookupIntraStateGstTaxCode(Company company) {
    return AppLookup.getAutoFilter(TaxCode.class, "select * from scm_tax_code where country_id = ? and parent_id is not null and tax_head_id = ? order by rate_percentage asc",
            new Object[]{company.getCountryId().getId(), TaxHeadService.TAX_HEAD_GST});
  }

  public static List<Product> lookupProductsForStockAdjustment(String filter, Account accountId, AccountGroup accountGroupId) {
    List<Product> list = null;
    ArrayList<Object> params = new ArrayList<>();
    String sql = "select * from scm_product \n";
    String cond = "where id in (select product_id from scm_product_batch where id in\n"
            + "(select product_batch_id from scm_product_detail ";
    if (accountId != null) {
      cond += "where account_id = ? ))";
      params.add(accountId.getId());
    } else if (accountGroupId != null) {
      cond += "where account_id in (SELECT account_id FROM scm_account_group_detail WHERE account_group_id =?))) ";
      params.add(accountGroupId.getId());
    }
    cond += "and product_status_id =? ";
    params.add(SystemConstants.PRODUCT_STATUS_ACTIVE);
    sql += cond;
    if (!StringUtil.isEmpty(filter)) {
      params.add("%" + filter.toUpperCase() + "%");
      sql += "and upper(product_name) like upper(?) order by product_name asc";
    } else {
      sql += " order by product_name asc";
    }
    list = AppLookup.getAutoFilter(Product.class, sql, params.toArray());

    return list;
  }
}
