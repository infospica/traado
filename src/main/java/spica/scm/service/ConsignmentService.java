/*
 * @(#)ConsignmentService.java	1.0 Fri Jul 22 10:57:51 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import spica.scm.common.ConsignmentRate;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.Consignment;
import spica.scm.domain.ConsignmentCommodity;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.ConsignmentReference;
import spica.scm.domain.ConsignmentStatus;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.validate.ConsignmentIs;
import spica.scm.view.AccountUtil;
import spica.scm.view.ScmLookupSql;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.util.StringUtil;

/**
 * ConsignmentService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:51 IST 2016
 */
public abstract class ConsignmentService {

  public static final int BY_ROAD = 1;
  public static final int BY_AIR = 2;
  public static final int BY_RAIL = 3;
  public static final int BY_SEA = 4;
  public static final int BY_COURIER = 5;

  public static final String PURCHASE_CONSIGNMENT_URL = "/scm/consignment/purchase_consignment.xhtml";
  public static final String PURCHASE_RETURN_CONSIGNMENT_URL = "/scm/consignment/purchase_return_consignment.xhtml";
  public static final String SALES_CONSIGNMENT_URL = "/scm/consignment/sales_consignment.xhtml";
  public static final String salesReturnConsignment = "/scm/consignment/sales_return_consignment.xhtml";
  public static final String PURCHASE_RETURN_URL = "/scm/purchase_return/purchase_return.xhtml";
  public static final String PURCHASE_RETURN_CONSIGNMENT_POPUP = "/consignment/purchase_return_consignment.xhtml";
  public static final String SALES_INVOICE_CONSIGNMENT_POPUP = "/consignment/sales_consignment.xhtml";

  public static final int wb8F = 1;
  public static final int wb8FA = 2;
  public static final int wbTS = 3;
  public static final int VENDOR_PURCHASE_CONSIGNMENT = 2;

  /**
   * Consignment paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getConsignmentSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_consignment", Consignment.class, main);
    sql.main("select scm_consignment.id,scm_consignment.consignment_no,scm_consignment.account_group_id,scm_consignment.note,scm_consignment.consignment_date,scm_consignment.vendor_id,scm_consignment.company_id,scm_consignment.customer_id,scm_consignment.transport_mode_id,scm_consignment.consignor_address,scm_consignment.consignor_country,scm_consignment.consignor_state_id,scm_consignment.consignor_district,scm_consignment.consignor_territory,scm_consignment.consignor_pin,scm_consignment.consignor_phone1,scm_consignment.consignor_phone2,scm_consignment.consignor_phone3,scm_consignment.consignor_fax1,scm_consignment.consignor_fax2,scm_consignment.consignor_email,scm_consignment.consignee_address,scm_consignment.consignee_country,scm_consignment.consignee_state_id,scm_consignment.consignee_district,scm_consignment.consignee_territory,scm_consignment.consignee_pin,scm_consignment.consignee_phone1,scm_consignment.consignee_phone2,scm_consignment.consignee_phone3,scm_consignment.consignee_fax_1,scm_consignment.consignee_fax2,scm_consignment.consignee_email,scm_consignment.consignment_status_id,scm_consignment.consignment_type_id,scm_consignment.consignment_status_log,scm_consignment.created_by,scm_consignment.modified_by,scm_consignment.created_at,scm_consignment.modified_at,scm_consignment.despatched_at,scm_consignment.confirmed_at,scm_consignment.confirmed_by,scm_consignment.delivered_at,scm_consignment.consignment_status_log from scm_consignment scm_consignment "); //Main query ,scm_consignment_doc_type.title ,scm_consignmentDetail_id.entry_document_type_id,scm_consignmentDetail_id.id
    sql.count("select count(scm_consignment.id) as total from scm_consignment scm_consignment "); //Count query
    sql.join("left outer join scm_account_group scm_account_group on (scm_account_group.id = scm_consignment.account_group_id) left outer join scm_vendor scm_consignmentvendor_id on (scm_consignmentvendor_id.id = scm_consignment.vendor_id) left outer join scm_company scm_consignmentcompany_id on (scm_consignmentcompany_id.id = scm_consignment.company_id) left outer join scm_customer scm_consignmentcustomer_id on (scm_consignmentcustomer_id.id = scm_consignment.customer_id) left outer join scm_transport_mode scm_consignmenttransport_mode_id on (scm_consignmenttransport_mode_id.id = scm_consignment.transport_mode_id) left outer join scm_consignment_status scm_consignmentconsignment_status_id on (scm_consignmentconsignment_status_id.id = scm_consignment.consignment_status_id) left outer join scm_consignment_type scm_consignmentconsignment_type_id on (scm_consignmentconsignment_type_id.id = scm_consignment.consignment_type_id)"); //Join Query left outer join scm_consignment_detail scm_consignmentDetail_id on (scm_consignmentDetail_id.consignment_id = scm_consignment.id) left outer join scm_consignment_doc_type scm_consignment_doc_type on (scm_consignmentDetail_id.entry_document_type_id = scm_consignment_doc_type.id)

    sql.string(new String[]{"scm_consignment.consignment_no", "scm_consignmentvendor_id.vendor_name", "scm_consignment.note", "scm_consignmentcompany_id.company_name", "scm_consignmentcustomer_id.customer_name", "scm_consignmenttransport_mode_id.title", "scm_consignment.consignor_address", "scm_consignment.consignor_country", "scm_consignment.consignor_state_id", "scm_consignment.consignor_district", "scm_consignment.consignor_territory", "scm_consignment.consignor_pin", "scm_consignment.consignor_phone1", "scm_consignment.consignor_phone2", "scm_consignment.consignor_phone3", "scm_consignment.consignor_fax1", "scm_consignment.consignor_fax2", "scm_consignment.consignor_email", "scm_consignment.consignee_address", "scm_consignment.consignee_country", "scm_consignment.consignee_state_id", "scm_consignment.consignee_district", "scm_consignment.consignee_territory", "scm_consignment.consignee_pin", "scm_consignment.consignee_phone1", "scm_consignment.consignee_phone2", "scm_consignment.consignee_phone3", "scm_consignment.consignee_fax_1", "scm_consignment.consignee_fax2", "scm_consignment.consignee_email", "scm_consignmentconsignment_status_id.title", "scm_consignmentconsignment_type_id.title", "scm_consignment.consignment_status_log", "scm_consignment.created_by", "scm_consignment.modified_by", "scm_consignment.confirmed_by"}); //String search or sort fields
    sql.number(new String[]{"scm_consignment.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_consignment.consignment_date", "scm_consignment.created_at", "scm_consignment.modified_at", "scm_consignment.despatched_at", "scm_consignment.confirmed_at", "scm_consignment.delivered_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Consignment.
   *
   * @param main
   * @param consignmentTypeId
   * @return List of Consignment
   */
  public static final List<Consignment> listPaged(Main main, Integer consignmentTypeId) {
    SqlPage sql = getConsignmentSqlPaged(main);
    sql.cond("where scm_consignment.consignment_type_id = ?");
    sql.param(consignmentTypeId);
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<Consignment> listPagedConsignmentByCompanyAndConsignmentType(Main main, Consignment consignment) {
    SqlPage sql = getConsignmentSqlPaged(main);
    sql.param(consignment.getCompanyId().getId());
    sql.param(consignment.getConsignmentTypeId().getId());
    if (consignment.getAccountId() != null) {
      sql.cond("where scm_consignment.company_id = ? and scm_consignment.consignment_type_id = ? and account_id = ?");
      sql.param(consignment.getAccountId().getId());
    } else if (consignment.getAccountGroupId() != null) {
      sql.cond("where scm_consignment.company_id = ? and scm_consignment.consignment_type_id = ? and scm_consignment.account_group_id = ? ");
      sql.param(consignment.getAccountGroupId().getId());
    }
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<Consignment> listPagedConsignmentByCompanyAndConsignmentTypeAndVendor(Main main, Consignment consignment, int id, Account accId) {
    SqlPage sql = getConsignmentSqlPaged(main);
    sql.cond("where scm_consignment.company_id = ? and scm_consignment.consignment_type_id = ? and scm_consignment.vendor_id=? and scm_consignment.account_id=?");
    sql.param(consignment.getCompanyId().getId());
    sql.param(consignment.getConsignmentTypeId().getId());
    sql.param(id);
    sql.param(accId.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<Consignment> listPagedConsignmentByCompanyAndConsignmentTypeAndCustomer(Main main, Consignment consignment, int id, Account accId) {
    SqlPage sql = getConsignmentSqlPaged(main);
    sql.cond("where scm_consignment.company_id = ? and scm_consignment.consignment_type_id = ? and scm_consignment.customer_id=? and scm_consignment.account_id=? and consignment_status_id not in(?)");
    sql.param(consignment.getCompanyId().getId());
    sql.param(consignment.getConsignmentTypeId().getId());
    sql.param(id);
    sql.param(accId.getId());
    sql.param(1);
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<Consignment> listPagedConsignmentByCompanyAndConsignmentTypeAndCompany(Main main, Consignment consignment, int id, Account accId) {
    SqlPage sql = getConsignmentSqlPaged(main);
    sql.cond("where scm_consignment.company_id = ? and scm_consignment.consignment_type_id = ? and scm_consignment.account_id=?");
//    and scm_consignment.vendor_id is null and scm_consignment.customer_id is null
    sql.param(consignment.getCompanyId().getId());
    sql.param(consignment.getConsignmentTypeId().getId());
    sql.param(accId.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<Consignment> listPagedConsignmentByCompanyAndAccountGroup(Main main, Consignment consignment, int id, AccountGroup accGroup) {
    SqlPage sql = getConsignmentSqlPaged(main);
    sql.cond("where scm_consignment.company_id = ? and scm_consignment.consignment_type_id = ? and scm_consignment.account_group_id=?");
//    and scm_consignment.vendor_id is null and scm_consignment.customer_id is null
    sql.param(consignment.getCompanyId().getId());
    sql.param(consignment.getConsignmentTypeId().getId());
    sql.param(accGroup.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of Consignment based on condition
//   * @param main
//   * @return List<Consignment>
//   */
//  public static final List<Consignment> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getConsignmentSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Consignment by key.
   *
   * @param main
   * @param consignment
   * @return Consignment
   */
  public static final Consignment selectByPk(Main main, Consignment consignment) {
    return (Consignment) AppService.find(main, Consignment.class, consignment.getId());
  }

  /**
   * Insert Consignment.
   *
   * @param main
   * @param consignment
   */
  public static final void insert(Main main, Consignment consignment) {
    ConsignmentIs.insertAble(main, consignment);  //Validating
    AppService.insert(main, consignment);

  }

  /**
   * Update Consignment by key.
   *
   * @param main
   * @param consignment
   * @return Consignment
   */
  private static final Consignment updateByPk(Main main, Consignment consignment) {
    ConsignmentIs.updateAble(main, consignment); //Validating
    return (Consignment) AppService.update(main, consignment);
  }

  /**
   * Insert or update Consignment
   *
   * @param main
   * @param Consignment
   */
  public static void insertOrUpdate(Main main, Consignment consignment) {
    if (consignment.getId() == null) {
      insert(main, consignment);
    } else {
      updateByPk(main, consignment);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param consignment
   */
  public static void clone(Main main, Consignment consignment) {
    consignment.setId(null); //Set to null for insert
    insert(main, consignment);
  }

  /**
   * Delete Consignment.
   *
   * @param main
   * @param consignment
   */
  public static final void deleteByPk(Main main, Consignment consignment) {
    ConsignmentIs.deleteAble(main, consignment); //Validation
    AppService.deleteSql(main, ConsignmentDetail.class, "delete from scm_consignment_detail scm_consignment_detail where scm_consignment_detail.consignment_id=?", new Object[]{consignment.getId()});
    AppService.deleteSql(main, ConsignmentCommodity.class, "delete from scm_consignment_commodity scm_consignment_commodity where scm_consignment_commodity.consignment_id=?", new Object[]{consignment.getId()});
    AppService.deleteSql(main, ConsignmentReference.class, "delete from scm_consignment_reference scm_consignment_reference where scm_consignment_reference.consignment_id=?", new Object[]{consignment.getId()});
    AppService.delete(main, Consignment.class, consignment.getId());
  }

  /**
   * Delete Array of Consignment.
   *
   * @param main
   * @param consignment
   */
  public static final void deleteByPkArray(Main main, Consignment[] consignment) {
    for (Consignment e : consignment) {
      deleteByPk(main, e);
    }
  }

  public static void setFromVendorAddress(MainView main, Consignment consignment) {
    List<VendorAddress> vAddress = main.em().list(VendorAddress.class, "select * from scm_vendor_address where scm_vendor_address.vendor_id=? order by scm_vendor_address.sort_order limit ?", new Object[]{consignment.getVendorId().getId(), 1});
//    Vendor v = selectVendorById(main, consignment.getVendorId().getId());
    consignment.setConsignorCst(consignment.getAccountId().getCstRegNo());
    consignment.setConsignorTin(consignment.getAccountId().getTin());
    if (!StringUtil.isEmpty(vAddress)) {
      copyFromAddress(consignment, vAddress.get(0));
    } else {
      clearFromAddress(consignment);
    }
  }

  public static void setFromVendorAddresss(MainView main, Consignment consignment) {
    List<VendorAddress> vAddress = main.em().list(VendorAddress.class, "select * from scm_vendor_address where scm_vendor_address.vendor_id=? limit ?", new Object[]{consignment.getVendorId().getId(), 1});

    if (!StringUtil.isEmpty(vAddress)) {
      copyFromAddress(consignment, vAddress.get(0));
    } else {
      clearFromAddress(consignment);
    }
  }

  public static void setFromCompanyAddress(MainView main, Consignment consignment) {
    List<CompanyAddress> vAddress = main.em().list(CompanyAddress.class, "select * from scm_company_address where scm_company_address.company_id=? limit ?", new Object[]{consignment.getCompanyId().getId(), 1});
    Company c = selectCompanyById(main, consignment.getCompanyId().getId());
    consignment.setConsignorCst(c.getCstNo());
    consignment.setConsignorTin(c.getTinNo());
    if (!StringUtil.isEmpty(vAddress)) {
      copyFromAddress(consignment, vAddress.get(0));
    } else {
      clearFromAddress(consignment);
    }
  }

  public static void setFromCustomerAddress(MainView main, Consignment consignment) {
    List<CustomerAddress> vAddress = main.em().list(CustomerAddress.class, "select * from scm_customer_address where scm_customer_address.customer_id=? limit 1", new Object[]{consignment.getCustomerId().getId()});
    if (!StringUtil.isEmpty(vAddress)) {
      copyFromAddress(consignment, vAddress.get(0));
    } else {
      clearFromAddress(consignment);
    }
  }

  private static void copyFromAddress(Consignment consignment, spica.scm.common.Address address) {

    consignment.setConsignorAddress(address.getAddress());
    consignment.setConsignorCountry(address.getCountryId().getCountryName());
    consignment.setConsignorStateId(address.getStateId());
    if (address.getDistrictId() != null) {
      consignment.setConsignorDistrict(address.getDistrictId().getDistrictName());
    }
    //  consignment.setConsignorTerritory(address.getTerritoryId().getTerritoryName());
    consignment.setConsignorPin(address.getPin());
    consignment.setConsignorPhone1(address.getPhone1());
    consignment.setConsignorPhone2(address.getPhone2());
    consignment.setConsignorPhone3(address.getPhone3());
    consignment.setConsignorFax1(address.getFax1());
    consignment.setConsignorFax2(address.getFax2());
    consignment.setConsignorEmail(address.getEmail());
  }

  private static void clearFromAddress(Consignment consignment) {

    consignment.setConsignorAddress("");
    consignment.setConsignorCountry("");
    consignment.setConsignorStateId(null);
    consignment.setConsignorDistrict("");
    consignment.setConsignorTerritory("");
    consignment.setConsignorPin("");
    consignment.setConsignorPhone1("");
    consignment.setConsignorPhone2("");
    consignment.setConsignorPhone3("");
    consignment.setConsignorFax1("");
    consignment.setConsignorFax2("");
    consignment.setConsignorEmail("");
  }

  public static void setToCustomerAddress(MainView main, Consignment consignment) {
    CustomerAddress vAddress = (CustomerAddress) AppService.single(main, CustomerAddress.class, "select * from scm_customer_address where scm_customer_address.customer_id=? order by scm_customer_address.sort_order  limit 1", new Object[]{consignment.getCustomerId().getId()});
    Customer c = selectCustomerById(main, consignment.getCustomerId().getId());
    consignment.setConsigneeCst(c.getCstNo());
    consignment.setConsigneeTin(c.getTinNo());
    if (vAddress != null) {
      copyToAddress(consignment, vAddress);
    } else {
      clearToAddress(consignment);
    }
  }

  public static void setToCompanyAddress(MainView main, Consignment consignment) {
    CompanyAddress vAddress = (CompanyAddress) AppService.single(main, CompanyAddress.class, "select * from scm_company_address where scm_company_address.company_id=? order by scm_company_address.sort_order limit 1", new Object[]{consignment.getCompanyId().getId()});
    Company cmp = selectCompanyById(main, consignment.getCompanyId().getId());
    if (cmp != null) {
      consignment.setConsigneeCst(cmp.getCstNo());
      consignment.setConsigneeTin(cmp.getTinNo());
    }
    if (vAddress != null) {
      copyToAddress(consignment, vAddress);
    } else {
      clearToAddress(consignment);
    }
  }

  public static Company selectCompanyById(Main main, Integer id) {
    Company cmp = (Company) AppService.single(main, Company.class, "select id,cst_no,vat_no,tin_no from scm_company where id = ?", new Object[]{id});
    return cmp;
  }

  public static Customer selectCustomerById(Main main, Integer id) {
    Customer cust = (Customer) AppService.single(main, Customer.class, "select id,cst_no,vat_no,tin_no from scm_customer where id = ?", new Object[]{id});
    return cust;
  }

  public static Vendor selectVendorById(Main main, Integer id) {
    Vendor vendor = (Vendor) AppService.single(main, Vendor.class, "select id,cst_no,vat_no,tin_no from scm_vendor where id = ?", new Object[]{id});
    return vendor;
  }

  public static void setToVendorAddress(MainView main, Consignment consignment) {
    VendorAddress vAddress = (VendorAddress) AppService.single(main, VendorAddress.class, "select * from scm_vendor_address where scm_vendor_address.vendor_id=? order by scm_vendor_address.sort_order limit 1", new Object[]{consignment.getVendorId().getId()});
//    Vendor v = selectVendorById(main, consignment.getVendorId().getId());
    consignment.setConsigneeCst(consignment.getAccountId().getCstRegNo());
    consignment.setConsigneeTin(consignment.getAccountId().getTin());
    if (vAddress != null) {
      copyToAddress(consignment, vAddress);
    } else {
      clearToAddress(consignment);
    }
  }

  private static void copyToAddress(Consignment consignment, spica.scm.common.Address address) {
    consignment.setConsigneeAddress(address.getAddress());
    consignment.setConsigneeCountry(address.getCountryId().getCountryName());
    consignment.setConsigneeStateId(address.getStateId());
    if (address.getDistrictId() != null) {
      consignment.setConsigneeDistrict(address.getDistrictId().getDistrictName());
    }
    //consignment.setConsigneeTerritory(address.getTerritoryId().getTerritoryName());
    consignment.setConsigneePin(address.getPin());
    consignment.setConsigneePhone1(address.getPhone1());
    consignment.setConsigneePhone2(address.getPhone2());
    consignment.setConsigneePhone3(address.getPhone3());
    consignment.setConsigneeFax1(address.getFax1());
    consignment.setConsigneeFax2(address.getFax2());
    consignment.setConsigneeEmail(address.getEmail());
  }

  private static void clearToAddress(Consignment consignment) {
    consignment.setConsigneeAddress("");
    consignment.setConsigneeCountry("");
    consignment.setConsigneeStateId(null);
    consignment.setConsigneeDistrict("");
    consignment.setConsigneeTerritory("");
    consignment.setConsigneePin("");
    consignment.setConsigneePhone1("");
    consignment.setConsigneePhone2("");
    consignment.setConsigneePhone3("");
    consignment.setConsigneeFax1("");
    consignment.setConsigneeFax2("");
    consignment.setConsigneeEmail("");
  }

  public static void setStatus(Main main, int status, Consignment consignment) {
    //Check Consignment Status Log - duplicate data inserted
    Date date = new Date();
    List<ConsignmentStatus> csList = AppService.list(main, ConsignmentStatus.class, "select id,title from scm_consignment_status order by id asc", null);
    for (ConsignmentStatus bp : csList) {
      if (bp.getId() == status) {
        consignment.setConsignmentStatusId(bp);
        break;
      }
    }

    if (status == SystemConstants.CONSIGNMENT_STATUS_DRAFT) {
      consignment.setCreatedAt(date);
      consignment.setConsignmentStatusLog("<br/> Draft Created at " + consignment.getCreatedAt());
    } else if (status == SystemConstants.CONSIGNMENT_STATUS_CONFIRMED) {
      consignment.setConfirmedBy(UserRuntimeView.instance().getAppUser().getLogin());
      consignment.setConfirmedAt(date);
      consignment.setConsignmentStatusLog("<br/> Confirmed at " + consignment.getConfirmedAt() + "<br/>" + consignment.getConsignmentStatusLog());
    } else if (status == SystemConstants.CONSIGNMENT_STATUS_CENCELLED) {
      main.clear();
      main.param(status);
      main.param("<br/> Cancelled at " + date + "<br/>" + consignment.getConsignmentStatusLog());
      main.param(consignment.getId());
      AppService.updateSql(main, Consignment.class, "update scm_consignment set modified_by=?,modified_at=? ,consignment_status_id=? ,consignment_status_log=? where id=?", true);

    } else if (status == SystemConstants.CONSIGNMENT_STATUS_READY_FOR_DISPATCH_AND_WAY_BILL_READY) {
      main.clear();
      main.param(status);
//      main.param(date);
      main.param("<br/> Ready for Despatch And waybill ready at " + date + "<br/>" + consignment.getConsignmentStatusLog());
      main.param(consignment.getId());
      AppService.updateSql(main, Consignment.class, "update scm_consignment set modified_by=?,modified_at=? ,consignment_status_id=? , consignment_status_log=? where id=?", true);
    } else if (status == SystemConstants.CONSIGNMENT_STATUS_DISPATCHED) {
      main.clear();
      consignment.setDespatchedAt(date);
      main.param(status);
      main.param(date);
      main.param("<br/> Despatched at " + consignment.getDespatchedAt() + "<br/>" + consignment.getConsignmentStatusLog());
      main.param(consignment.getId());
      AppService.updateSql(main, Consignment.class, "update scm_consignment set modified_by=?,modified_at=? ,consignment_status_id=?,despatched_at=? , consignment_status_log=? where id=?", true);
    } else if (status == SystemConstants.CONSIGNMENT_STATUS_DELIVERED) {//|| status == fullyDamagedCancelled || status == fullyDamagedDelivered
      consignment.setDeliveredAt(date);
      main.clear();
      main.param(status);
      main.param(date);
      main.param("<br/> Received at " + consignment.getDeliveredAt() + "<br/>" + consignment.getConsignmentStatusLog());
      main.param(consignment.getId());
      AppService.updateSql(main, Consignment.class, "update scm_consignment set modified_by=?,modified_at=? ,consignment_status_id=?,delivered_at=? ,consignment_status_log=? where id=?", true);
    } else if (status == SystemConstants.CONSIGNMENT_STATUS_CANCEL_AND_INFORM_ST) {
      main.clear();
      main.param(status);
      main.param("<br/> Consignment Cancel & Inform Sales Tax " + date + "<br/>" + consignment.getConsignmentStatusLog());
      main.param(consignment.getId());
      AppService.updateSql(main, Consignment.class, "update scm_consignment set modified_by=?,modified_at=? ,consignment_status_id=?, consignment_status_log=? where id=?", true);
    } else if (status == SystemConstants.CONSIGNMENT_STATUS_CENCEL_AND_INFORMED_ST) {
      main.clear();
      main.param(status);
      main.param("<br/> Consignment Cancelled & Informed to Sales Tax " + date + "<br/>" + consignment.getConsignmentStatusLog());
      main.param(consignment.getId());
      AppService.updateSql(main, Consignment.class, "update scm_consignment set modified_by=?,modified_at=? ,consignment_status_id=?, consignment_status_log=? where id=?", true);
    } else if (status == SystemConstants.CONSIGNMENT_STATUS_DISPACHED_AND_CHECKPOST_CLEARED) {
      main.clear();
      main.param(status);
      main.param("<br/> Dispatched And Check Post Cleared at " + date + "<br/>" + consignment.getConsignmentStatusLog());
      main.param(consignment.getId());
      AppService.updateSql(main, Consignment.class, "update scm_consignment set modified_by=?,modified_at=? ,consignment_status_id=?, consignment_status_log=? where id=?", true);
    } else if (status == SystemConstants.CONSIGNMENT_STATUS_FULLY_DAMAGED_DELIVERED) {
      main.clear();
      main.param(status);
      main.param("<br/> Lost / Fully Damaged And Delevered at " + date + "<br/>" + consignment.getConsignmentStatusLog());
      main.param(consignment.getId());
      AppService.updateSql(main, Consignment.class, "update scm_consignment set modified_by=?,modified_at=? ,consignment_status_id=?, consignment_status_log=? where id=?", true);
    } else if (status == SystemConstants.CONSIGNMENT_STATUS_FULLY_DAMAGED_CANCELLED) {
      main.clear();
      main.param(status);
      main.param("<br/> Reciept Entered at " + date + "<br/>" + consignment.getConsignmentStatusLog());
      main.param(consignment.getId());
      AppService.updateSql(main, Consignment.class, "update scm_consignment set modified_by=?,modified_at=? ,consignment_status_id=?, consignment_status_log=? where id=?", true);
    }
//    else if (status == ConsignmentService.normal) {
//      main.clear();
//      main.param(status);
//      main.param("<br/> Reciept Entered At " + date + "<br/>" + consignment.getConsignmentStatusLog() + "<br/>");
//      main.param(consignment.getId());
//      AppService.updateSql(main, Consignment.class, "update scm_consignment set modified_by=?,modified_at=? ,consignment_status_id=?, consignment_status_log=? where id=?", true);
//    }
  }

  public static Long selectReceipt(Main main, Consignment consignment) {
    return main.em().count("select count(id) from scm_consignment_receipt where scm_consignment_receipt.consignment_id = ?", new Object[]{consignment.getId()});
  }

  public static final boolean validateCommodity(Main main, Consignment consignment) {
    return AppService.exist(main, "select id from scm_consignment_commodity where scm_consignment_commodity.consignment_id=?", new Object[]{consignment.getId()});

  }

  public static final boolean confirmConsignmentDoc(Main main, Consignment consignment) {
    return AppService.exist(main, "select id from scm_consignment_detail where consignment_id=?", new Object[]{consignment.getId()});

  }

  public static Consignment selectConsignmentStatusLog(Main main, Integer id) {
    return (Consignment) AppService.single(main, Consignment.class, "select * from scm_consignment where id = ?", new Object[]{id});
  }

  public static void updateProductEntryVerifiedStatus(Main main, Integer consingmentId, int status) {
    main.clear();
    main.param(status);
    main.param(consingmentId);
    AppService.updateSql(main, Consignment.class, "update scm_consignment set product_entry_verified = ? where id = ?", false);
  }

  public static Long customerConsignmentCount(Main main, Customer customer) {
    return main.em().count("select count(id) from scm_consignment where scm_consignment.customer_id = ? and consignment_type_id=? and consignment_status_id in(?)", new Object[]{customer.getId(), 2, 1});
  }

  public static List<Consignment> consignmentInfo(Main main, Company company, Account account) {
    return main.em().list(Consignment.class, "select * from scm_consignment where company_id=? and account_id = ? and consignment_type_id=? and id not in(select consignment_id from scm_consignment_receipt)", new Object[]{company.getId(), account.getId(), 1});
  }

  public static List<ConsignmentRate> lookUpConsignmentByAccount(Main main, Account account, Integer consignmentId) {
    if (consignmentId != null) {
      return AppDb.getList(main.dbConnector(), ConsignmentRate.class, ScmLookupSql.CONSIGNMENT_RATE_BY_ACCOUNT_AND_CONSIGNMENT, new Object[]{account.getId(), consignmentId});
    } else {
      return AppDb.getList(main.dbConnector(), ConsignmentRate.class, ScmLookupSql.CONSIGNMENT_RATE_BY_ACCOUNT, new Object[]{account.getId()});
    }
  }

  public static List<ConsignmentRate> lookUpConsignmentByCustomerAccountGroup(Main main, Customer customer, AccountGroup accountGroup, Integer consignmentId) {
    List<ConsignmentRate> list = null;
    if (customer != null && accountGroup != null) {
      if (consignmentId != null) {
        return AppDb.getList(main.dbConnector(), ConsignmentRate.class, ScmLookupSql.CONSIGNMENT_RATE_BY_ACCOUNTGROUP_AND_CONSIGNMENT,
                new Object[]{customer.getId(), accountGroup.getId(), consignmentId});
      } else {
        return AppDb.getList(main.dbConnector(), ConsignmentRate.class, ScmLookupSql.CONSIGNMENT_RATE_BY_ACCOUNTGROUP, new Object[]{customer.getId(), accountGroup.getId()});
      }
    }
    return list;
  }

  public static ConsignmentRate selectConsignmentRateByAccountGroupConsignment(Main main, Customer customer, AccountGroup accountGroup, Integer consignmentId) {
    ConsignmentRate rate = (ConsignmentRate) AppDb.single(main.dbConnector(), ConsignmentRate.class, ScmLookupSql.CONSIGNMENT_DETAIL_BY_ACCOUNTGROUP_CONSIGNMENT_ID, new Object[]{customer.getId(), accountGroup.getId(), consignmentId});
    return rate;
  }

  public static ConsignmentRate selectConsignmentRateByConsignment(Main main, Account account, Integer consignmentId) {
    ConsignmentRate rate = (ConsignmentRate) AppDb.single(main.dbConnector(), ConsignmentRate.class, ScmLookupSql.CONSIGNMENT_DETAIL_BY_CONSIGNMENT_ID, new Object[]{account.getId(), consignmentId});
    return rate;
  }

  public static List<Consignment> selectSalesReturnConsignment(MainView main, Company company, Customer customerId) {
    return main.em().list(Consignment.class, "select * from scm_consignment where company_id = ? and customer_id = ? and consignment_type_id = ? "
            + "and id not in(select consignment_id from scm_consignment_receipt)",
            new Object[]{company.getId(), customerId.getId(), SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN});
  }

  public static boolean isConsignmentReferenceExist(Main main, Consignment consignment) {
    return AppService.exist(main, "select 1 from scm_sales_invoice where id in (select sales_invoice_id from scm_consignment_commodity where consignment_id = ?)", new Object[]{consignment.getId()});
  }

  /**
   *
   * @param main
   * @param consignment
   */
  public static void insertOrUpdateInvoiceEntryConsignmentDetail(Main main, Consignment consignment) {
    boolean consignmentExist = false;
    ConsignmentDetail consignmentDetail = consignment.getConsignmentDetail();
    if (consignment != null && consignment.getId() != null) {
      consignmentExist = true;
    }

    if (!consignmentExist) {
      consignment.setConsignmentTypeId(SystemRuntimeConfig.PURCHASE_CONSIGNMENT_TYPE);
    }
    //ConsignmentService.insertOrUpdate(main, consignment);

    if (!consignmentExist) {
      if (AccountService.INDIVIDUAL_SUPPLIER.equals(consignment.getAccountId().getPurchaseChannel()) || AccountUtil.isPrimaryVendorAccount(consignment.getAccountId())) {
        consignment.setConsignmentNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, consignment.getAccountId(), PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX_ID, false, consignment.getFinancialYearId()));
      } else {
        consignment.setConsignmentNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, consignment.getAccountId().getAccountId(), PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX_ID, false, consignment.getFinancialYearId()));
      }
    }

    //main.em().flush();
    consignmentDetail.setConsignmentId(consignment);
    ConsignmentService.insertOrUpdate(main, consignment);

    ConsignmentDetailService.insertOrUpdate(main, consignmentDetail);

    if (!consignmentExist) {
      if (AccountService.INDIVIDUAL_SUPPLIER.equals(consignment.getAccountId().getPurchaseChannel()) || AccountUtil.isPrimaryVendorAccount(consignment.getAccountId())) {
        AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, consignment.getAccountId(), PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX_ID, false, consignment.getFinancialYearId());
      } else {
        AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, consignment.getAccountId().getAccountId(), PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX_ID, false, consignment.getFinancialYearId());
      }
    }
  }

  /**
   *
   * @param main
   * @param consignment
   */
  public static void insertOrUpdateSalesReturnConsignmentDetail(Main main, Consignment consignment) {
    boolean consignmentExist = false;
    ConsignmentDetail consignmentDetail = consignment.getConsignmentDetail();
    if (consignment != null && consignment.getId() != null) {
      consignmentExist = true;
    }

    if (!consignmentExist) {
      consignment.setConsignmentTypeId(SystemRuntimeConfig.SALES_RETURN_CONSIGNMENT_TYPE);
    }
    //  ConsignmentService.insertOrUpdate(main, consignment);

    if (!consignmentExist) {
      consignment.setConsignmentNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, consignment.getAccountGroupId(), PrefixTypeService.SALES_RETURN_CONSIGNMENT_PREFIX_ID, false, consignment.getFinancialYearId()));
    }

    consignmentDetail.setConsignmentId(consignment);
    ConsignmentService.insertOrUpdate(main, consignment);
    // main.em().flush();
    ConsignmentDetailService.insertOrUpdate(main, consignmentDetail);

    if (!consignmentExist) {
      AccountGroupDocPrefixService.updateSalesPrefixSequence(main, consignment.getAccountGroupId(), PrefixTypeService.SALES_RETURN_CONSIGNMENT_PREFIX_ID, false, consignment.getFinancialYearId());
    }
  }

  /**
   *
   * @param main
   * @param customerId
   * @return
   */
  public static List<Consignment> selectConsignmentByCustomer(Main main, Customer customerId) {
    return AppService.list(main, Consignment.class, "select * from scm_consignment where customer_id = ?", new Object[]{customerId.getId()});
  }

}
