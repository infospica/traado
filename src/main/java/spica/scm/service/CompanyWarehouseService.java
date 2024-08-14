/*
 * @(#)CompanyWarehouseService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
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
import spica.scm.domain.CompanyWarehouse;
import spica.scm.validate.CompanyWarehouseIs;

/**
 * CompanyWarehouseService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CompanyWarehouseService {

  /**
   * CompanyWarehouse paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyWarehouseSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_warehouse", CompanyWarehouse.class, main);
    sql.main("select scm_company_warehouse.id,scm_company_warehouse.company_id,scm_company_warehouse.warehouse_name,scm_company_warehouse.address,scm_company_warehouse.country_id,scm_company_warehouse.state_id,scm_company_warehouse.district_id,scm_company_warehouse.pin,scm_company_warehouse.territory_id,scm_company_warehouse.phone_1,scm_company_warehouse.phone_2,scm_company_warehouse.phone_3,scm_company_warehouse.fax_1,scm_company_warehouse.fax_2,scm_company_warehouse.email,scm_company_warehouse.sort_order,scm_company_warehouse.status_id,scm_company_warehouse.created_by,scm_company_warehouse.modified_by,scm_company_warehouse.created_at,scm_company_warehouse.modified_at from scm_company_warehouse scm_company_warehouse "); //Main query
    sql.count("select count(scm_company_warehouse.id) as total from scm_company_warehouse scm_company_warehouse "); //Count query
    sql.join("left outer join scm_company scm_company_warehousecompany_id on (scm_company_warehousecompany_id.id = scm_company_warehouse.company_id) left outer join scm_country scm_company_warehousecountry_id on (scm_company_warehousecountry_id.id = scm_company_warehouse.country_id) left outer join scm_state scm_company_warehousestate_id on (scm_company_warehousestate_id.id = scm_company_warehouse.state_id) left outer join scm_district scm_company_warehousedistrict_id on (scm_company_warehousedistrict_id.id = scm_company_warehouse.district_id) left outer join scm_territory scm_company_warehouseterritory_id on (scm_company_warehouseterritory_id.id = scm_company_warehouse.territory_id) left outer join scm_status scm_company_warehousestatus_id on (scm_company_warehousestatus_id.id = scm_company_warehouse.status_id)"); //Join Query

    sql.string(new String[]{"scm_company_warehousecompany_id.company_name", "scm_company_warehouse.warehouse_name", "scm_company_warehouse.address", "scm_company_warehousecountry_id.country_name", "scm_company_warehousestate_id.state_name", "scm_company_warehousedistrict_id.district_name", "scm_company_warehouse.pin", "scm_company_warehouseterritory_id.territory_name", "scm_company_warehouse.phone_1", "scm_company_warehouse.phone_2", "scm_company_warehouse.phone_3", "scm_company_warehouse.fax_1", "scm_company_warehouse.fax_2", "scm_company_warehouse.email", "scm_company_warehousestatus_id.title", "scm_company_warehouse.created_by", "scm_company_warehouse.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company_warehouse.id", "scm_company_warehouse.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_company_warehouse.created_at", "scm_company_warehouse.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CompanyWarehouse.
   *
   * @param main
   * @return List of CompanyWarehouse
   */
  public static final List<CompanyWarehouse> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCompanyWarehouseSqlPaged(main));
  }

  /**
   * Return all warehouse of a company.
   *
   * @param main
   * @param company
   * @return
   */
  public static final List<CompanyWarehouse> warehouseListByCompany(Main main, Company company) {
    SqlPage sql = getCompanyWarehouseSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_warehouse.company_id=?");
    sql.param(company.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of CompanyWarehouse based on condition
//   * @param main
//   * @return List<CompanyWarehouse>
//   */
//  public static final List<CompanyWarehouse> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyWarehouseSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select CompanyWarehouse by key.
   *
   * @param main
   * @param companyWarehouse
   * @return CompanyWarehouse
   */
  public static final CompanyWarehouse selectByPk(Main main, CompanyWarehouse companyWarehouse) {
    return (CompanyWarehouse) AppService.find(main, CompanyWarehouse.class, companyWarehouse.getId());
  }

  /**
   * Insert CompanyWarehouse.
   *
   * @param main
   * @param companyWarehouse
   */
  public static final void insert(Main main, CompanyWarehouse companyWarehouse) {
    CompanyWarehouseIs.insertAble(main, companyWarehouse);  //Validating
    AppService.insert(main, companyWarehouse);

  }

  /**
   * Update CompanyWarehouse by key.
   *
   * @param main
   * @param companyWarehouse
   * @return CompanyWarehouse
   */
  public static final CompanyWarehouse updateByPk(Main main, CompanyWarehouse companyWarehouse) {
    CompanyWarehouseIs.updateAble(main, companyWarehouse); //Validating
    return (CompanyWarehouse) AppService.update(main, companyWarehouse);
  }

  /**
   * Insert or update CompanyWarehouse
   *
   * @param main
   * @param CompanyWarehouse
   */
  public static void insertOrUpdate(Main main, CompanyWarehouse companyWarehouse) {
    if (companyWarehouse.getId() == null) {
      insert(main, companyWarehouse);
    }
    updateByPk(main, companyWarehouse);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyWarehouse
   */
  public static void clone(Main main, CompanyWarehouse companyWarehouse) {
    companyWarehouse.setId(null); //Set to null for insert
    insert(main, companyWarehouse);
  }

  /**
   * Delete CompanyWarehouse.
   *
   * @param main
   * @param companyWarehouse
   */
  public static final void deleteByPk(Main main, CompanyWarehouse companyWarehouse) {
    CompanyWarehouseIs.deleteAble(main, companyWarehouse); //Validation
    AppService.delete(main, CompanyWarehouse.class, companyWarehouse.getId());
  }

  /**
   * Delete Array of CompanyWarehouse.
   *
   * @param main
   * @param companyWarehouse
   */
  public static final void deleteByPkArray(Main main, CompanyWarehouse[] companyWarehouse) {
    for (CompanyWarehouse e : companyWarehouse) {
      deleteByPk(main, e);
    }
  }
}
