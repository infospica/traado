/*
 * @(#)SupplierGroupService.java	1.0 Thu Dec 21 15:36:46 IST 2017 
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
import spica.scm.domain.SupplierGroup;
import spica.scm.validate.SupplierGroupIs;

/**
 * SupplierGroupService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Dec 21 15:36:46 IST 2017
 */
public abstract class SupplierGroupService {

  /**
   * SupplierGroup paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSupplierGroupSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_supplier_group", SupplierGroup.class, main);
    sql.main("select scm_supplier_group.id,scm_supplier_group.company_id,scm_supplier_group.title,scm_supplier_group.description,scm_supplier_group.status_id,scm_supplier_group.created_at,scm_supplier_group.created_by,scm_supplier_group.modified_at,scm_supplier_group.modified_by from scm_supplier_group scm_supplier_group "); //Main query
    sql.count("select count(scm_supplier_group.id) as total from scm_supplier_group scm_supplier_group "); //Count query
    sql.join("left outer join scm_status scm_supplier_groupstatus_id on (scm_supplier_groupstatus_id.id = scm_supplier_group.status_id) "
            + "left outer join scm_company scm_company on (scm_company.id = scm_supplier_group.company_id) "); //Join Query

    sql.string(new String[]{"scm_supplier_group.title", "scm_supplier_group.description", "scm_supplier_groupstatus_id.title", "scm_supplier_group.created_by", "scm_supplier_group.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_supplier_group.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_supplier_group.created_at", "scm_supplier_group.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SupplierGroup.
   *
   * @param main
   * @return List of SupplierGroup
   */
  public static final List<SupplierGroup> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSupplierGroupSqlPaged(main));
  }

  public static List<SupplierGroup> listPagedByCompany(Main main, Company company) {
    SqlPage sql = getSupplierGroupSqlPaged(main);
    sql.cond("where scm_supplier_group.company_id = ?");
    sql.param(company.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of SupplierGroup based on condition
//   * @param main
//   * @return List<SupplierGroup>
//   */
//  public static final List<SupplierGroup> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSupplierGroupSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SupplierGroup by key.
   *
   * @param main
   * @param supplierGroup
   * @return SupplierGroup
   */
  public static final SupplierGroup selectByPk(Main main, SupplierGroup supplierGroup) {
    return (SupplierGroup) AppService.find(main, SupplierGroup.class, supplierGroup.getId());
  }

  /**
   * Insert SupplierGroup.
   *
   * @param main
   * @param supplierGroup
   */
  public static final void insert(Main main, SupplierGroup supplierGroup) {
    SupplierGroupIs.insertAble(main, supplierGroup);  //Validating
    AppService.insert(main, supplierGroup);

  }

  /**
   * Update SupplierGroup by key.
   *
   * @param main
   * @param supplierGroup
   * @return SupplierGroup
   */
  public static final SupplierGroup updateByPk(Main main, SupplierGroup supplierGroup) {
    SupplierGroupIs.updateAble(main, supplierGroup); //Validating
    return (SupplierGroup) AppService.update(main, supplierGroup);
  }

  /**
   * Insert or update SupplierGroup
   *
   * @param main
   * @param supplierGroup
   */
  public static void insertOrUpdate(Main main, SupplierGroup supplierGroup) {
    if (supplierGroup.getId() == null) {
      insert(main, supplierGroup);
    } else {
      updateByPk(main, supplierGroup);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param supplierGroup
   */
  public static void clone(Main main, SupplierGroup supplierGroup) {
    supplierGroup.setId(null); //Set to null for insert
    insert(main, supplierGroup);
  }

  /**
   * Delete SupplierGroup.
   *
   * @param main
   * @param supplierGroup
   */
  public static final void deleteByPk(Main main, SupplierGroup supplierGroup) {
    SupplierGroupIs.deleteAble(main, supplierGroup); //Validation
    AppService.delete(main, SupplierGroup.class, supplierGroup.getId());
  }

  /**
   * Delete Array of SupplierGroup.
   *
   * @param main
   * @param supplierGroup
   */
  public static final void deleteByPkArray(Main main, SupplierGroup[] supplierGroup) {
    for (SupplierGroup e : supplierGroup) {
      deleteByPk(main, e);
    }
  }
}
