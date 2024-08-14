/*
 * @(#)SalesOrderReferenceService.java	1.0 Fri Feb 03 19:15:39 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.SalesOrder;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesOrderReference;
import spica.scm.domain.SalesReq;
import spica.scm.validate.SalesOrderReferenceIs;

/**
 * SalesOrderReferenceService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 03 19:15:39 IST 2017
 */
public abstract class SalesOrderReferenceService {

  /**
   * SalesOrderReference paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesOrderReferenceSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_order_reference", SalesOrderReference.class, main);
    sql.main("select scm_sales_order_reference.id,scm_sales_order_reference.sales_order_id, scm_sales_order_reference.sales_req_id from scm_sales_order_reference scm_sales_order_reference "); //Main query
    sql.count("select count(scm_sales_order_reference.id) as total from scm_sales_order_reference scm_sales_order_reference "); //Count query
    sql.join("left outer join scm_sales_order scm_sales_order on (scm_sales_order.id = scm_sales_order_reference.sales_order_id)"
            + "left outer join scm_sales_req scm_sales_req on (scm_sales_req.id = scm_sales_order_reference.sales_req_id)"); //Join Query

    sql.string(new String[]{"scm_sales_order_referencesales_order_id.order_no"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_order_reference.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesOrderReference.
   *
   * @param main
   * @return List of SalesOrderReference
   */
  public static final List<SalesOrderReference> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesOrderReferenceSqlPaged(main));
  }

//  /**
//   * Return list of SalesOrderReference based on condition
//   * @param main
//   * @return List<SalesOrderReference>
//   */
//  public static final List<SalesOrderReference> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesOrderReferenceSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesOrderReference by key.
   *
   * @param main
   * @param salesOrderReference
   * @return SalesOrderReference
   */
  public static final SalesOrderReference selectByPk(Main main, SalesOrderReference salesOrderReference) {
    return (SalesOrderReference) AppService.find(main, SalesOrderReference.class, salesOrderReference.getId());
  }

  /**
   * Insert SalesOrderReference.
   *
   * @param main
   * @param salesOrderReference
   */
  public static final void insert(Main main, SalesOrderReference salesOrderReference) {
    SalesOrderReferenceIs.insertAble(main, salesOrderReference);  //Validating
    AppService.insert(main, salesOrderReference);

  }

  /**
   * Update SalesOrderReference by key.
   *
   * @param main
   * @param salesOrderReference
   * @return SalesOrderReference
   */
  public static final SalesOrderReference updateByPk(Main main, SalesOrderReference salesOrderReference) {
    SalesOrderReferenceIs.updateAble(main, salesOrderReference); //Validating
    return (SalesOrderReference) AppService.update(main, salesOrderReference);
  }

  /**
   * Insert or update SalesOrderReference
   *
   * @param main
   * @param salesOrderReference
   */
  public static void insertOrUpdate(Main main, SalesOrderReference salesOrderReference) {
    if (salesOrderReference.getId() == null) {
      insert(main, salesOrderReference);
    } else {
      updateByPk(main, salesOrderReference);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesOrderReference
   */
  public static void clone(Main main, SalesOrderReference salesOrderReference) {
    salesOrderReference.setId(null); //Set to null for insert
    insert(main, salesOrderReference);
  }

  /**
   * Delete SalesOrderReference.
   *
   * @param main
   * @param salesOrderReference
   */
  public static final void deleteByPk(Main main, SalesOrderReference salesOrderReference) {
    SalesOrderReferenceIs.deleteAble(main, salesOrderReference); //Validation
    AppService.delete(main, SalesOrderReference.class, salesOrderReference.getId());
  }

  /**
   * Delete Array of SalesOrderReference.
   *
   * @param main
   * @param salesOrderReference
   */
  public static final void deleteByPkArray(Main main, SalesOrderReference[] salesOrderReference) {
    for (SalesOrderReference e : salesOrderReference) {
      deleteByPk(main, e);
    }
  }

  public static final void insertSalesOrderReference(Main main, SalesOrder salesOrder, SalesReq salesReq) {
    if (salesReq != null && salesOrder != null) {
      SalesOrderReference salesOrderReference = null;
      salesOrderReference = new SalesOrderReference();
      salesOrderReference.setSalesOrderId(salesOrder);
      salesOrderReference.setSalesReqId(salesReq);
      insert(main, salesOrderReference);
    }
  }

  /**
   * Inserts an array of SalesOrderReference.
   *
   * @param main
   * @param salesOrder
   * @param salesReqSelected
   */
  public static final void insertSalesOrderReference(Main main, SalesOrder salesOrder, SalesReq[] salesReqSelected) {
    AppService.deleteSql(main, SalesOrderReference.class, "delete from scm_sales_order_reference where sales_order_id = ?", new Object[]{salesOrder.getId()});
    if (salesReqSelected != null) {
      SalesOrderReference salesOrderReference = null;
      for (SalesReq salesReq : salesReqSelected) {
        salesOrderReference = new SalesOrderReference();
        salesOrderReference.setSalesOrderId(salesOrder);
        salesOrderReference.setSalesReqId(salesReq);
        insert(main, salesOrderReference);
      }
    }
  }
}
