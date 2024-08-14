/*
 * @(#)ManufactureService.java	1.0 Mon Aug 21 14:03:19 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.Manufacture;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnSplit;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * ManufactureService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 21 14:03:19 IST 2017
 */
public abstract class ManufactureService {

  /**
   * Manufacture paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getManufactureSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_manufacture", Manufacture.class, main);
    sql.main("select t1.id,t1.company_id,t1.vendor_id,t1.name,t1.code,t1.address,t1.state_id,t1.district_id,t1.pin,t1.description,t1.created_by,t1.modified_by,t1.created_at,t1.modified_at from scm_manufacture t1 "); //Main query
    sql.count("select count(t1.id) as total from scm_manufacture t1 "); //Count query
    sql.join("left outer join scm_state t2 on (t2.id = t1.state_id) left outer join scm_district t3 on (t3.id = t1.district_id) "
            + "left outer join scm_company t4 on (t4.id = t1.company_id) left outer join scm_vendor t5 on (t5.id = t1.vendor_id)"); //Join Query

    sql.string(new String[]{"t1.name", "t1.code", "t1.address", "t2.state_name", "t3.district_name", "t1.description", "t1.created_by", "t1.modified_by"}); //String search or sort fields
    sql.number(new String[]{"t1.id", "t1.pin"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.created_at", "t1.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Manufacture.
   *
   * @param main
   * @return List of Manufacture
   */
  public static final List<Manufacture> listPaged(Main main) {
    return AppService.listPagedJpa(main, getManufactureSqlPaged(main));
  }

  public static final List<Manufacture> listPagedByCompany(Main main, Company company) {
    SqlPage sql = getManufactureSqlPaged(main);
    sql.cond("where t1.company_id = ?");
    main.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of Manufacture based on condition
//   * @param main
//   * @return List<Manufacture>
//   */
//  public static final List<Manufacture> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getManufactureSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Manufacture by key.
   *
   * @param main
   * @param manufacture
   * @return Manufacture
   */
  public static final Manufacture selectByPk(Main main, Manufacture manufacture) {
    return (Manufacture) AppService.find(main, Manufacture.class, manufacture.getId());
  }

  /**
   * Insert Manufacture.
   *
   * @param main
   * @param manufacture
   */
  public static final void insert(Main main, Manufacture manufacture) {
    insertAble(main, manufacture);  //Validating
    AppService.insert(main, manufacture);

  }

  /**
   * Update Manufacture by key.
   *
   * @param main
   * @param manufacture
   * @return Manufacture
   */
  public static final Manufacture updateByPk(Main main, Manufacture manufacture) {
    updateAble(main, manufacture); //Validating
    return (Manufacture) AppService.update(main, manufacture);
  }

  /**
   * Insert or update Manufacture
   *
   * @param main
   * @param manufacture
   */
  public static void insertOrUpdate(Main main, Manufacture manufacture) {
    if (manufacture.getId() == null) {
      insert(main, manufacture);
    } else {
      updateByPk(main, manufacture);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param manufacture
   */
  public static void clone(Main main, Manufacture manufacture) {
    manufacture.setId(null); //Set to null for insert
    insert(main, manufacture);
  }

  /**
   * Delete Manufacture.
   *
   * @param main
   * @param manufacture
   */
  public static final void deleteByPk(Main main, Manufacture manufacture) {
    deleteAble(main, manufacture); //Validation
    AppService.delete(main, Manufacture.class, manufacture.getId());
  }

  /**
   * Delete Array of Manufacture.
   *
   * @param main
   * @param manufacture
   */
  public static final void deleteByPkArray(Main main, Manufacture[] manufacture) {
    for (Manufacture e : manufacture) {
      deleteByPk(main, e);
    }
  }

  public static void insertOrUpdateVendorAsManufacturer(Main main, Manufacture manufacture, Vendor vendor, VendorAddress vendorAddress) {
    manufacture.setVendorId(vendor);
    manufacture.setCompanyId(vendor.getCompanyId());
    manufacture.setCountryId(vendor.getCountryId());
    manufacture.setStateId(vendorAddress.getStateId());
    manufacture.setDistrictId(vendorAddress.getDistrictId());
    manufacture.setAddress(vendorAddress.getAddress());
    manufacture.setName(vendor.getVendorName());
    insertOrUpdate(main, manufacture);
  }

  /**
   *
   * @param main
   * @param vendor
   * @return
   */
  public static Manufacture selectByVendor(Main main, Vendor vendor) {
    return (Manufacture) AppService.single(main, Manufacture.class, "select * from scm_manufacture where vendor_id = ?", new Object[]{vendor.getId()});
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param manufacture
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, Manufacture manufacture) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param manufacture
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, Manufacture manufacture) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_manufacture where company_id = ? and upper(code)=?", new Object[]{manufacture.getCompanyId().getId(), StringUtil.toUpperCase(manufacture.getCode())})) {
      throw new UserMessageException("code.exist", ValidateUtil.getFieldName("manufactureCode"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param manufacture
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, Manufacture manufacture) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_manufacture where company_id = ? and upper(code)=? and id !=?", new Object[]{manufacture.getCompanyId().getId(), StringUtil.toUpperCase(manufacture.getCode()), manufacture.getId()})) {
      throw new UserMessageException("code.exist", ValidateUtil.getFieldName("manufactureCode"));
    }
  }

  /**
   *
   * @param main
   * @param salesInvoice
   * @return
   */
  public static final List<Manufacture> selectManufactureBySalesInvoice(Main main, SalesInvoice salesInvoice) {
    List<Manufacture> manufactureList = AppService.list(main, Manufacture.class, "select * from scm_manufacture where id in (select manufacture_id from scm_product_batch where id in("
            + "select product_batch_id from scm_product_detail where id in(select product_detail_id from scm_sales_invoice_item where sales_invoice_id = ?)))",
            new Object[]{salesInvoice.getId()});
    return manufactureList;
  }

  public static final List<Manufacture> selectManufactureBySalesReturn(Main main, SalesReturn salesReturn) {
    List<Manufacture> manufactureList = AppService.list(main, Manufacture.class, "select * from scm_manufacture where id in (select manufacture_id from scm_product_batch where id in("
            + "select product_batch_id from scm_product_detail where id in(select product_detail_id from scm_sales_return_item where sales_return_id = ?)))",
            new Object[]{salesReturn.getId()});
    return manufactureList;
  }

  public static final List<Manufacture> selectManufactureBySalesReturnSplit(Main main, SalesReturnSplit salesReturnSplit) {
    List<Manufacture> manufactureList = AppService.list(main, Manufacture.class, "select * from scm_manufacture where id in (select manufacture_id from scm_product_batch where id in("
            + "select product_batch_id from scm_product_detail where id in(select product_detail_id from scm_sales_return_item_split where sales_return_split_id = ?)))",
            new Object[]{salesReturnSplit.getId()});
    return manufactureList;
  }

}
