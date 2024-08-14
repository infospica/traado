/*
 * @(#)ConsignmentReferenceService.java	1.0 Fri Jul 22 10:57:51 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Consignment;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ConsignmentReference;
import spica.scm.domain.PurchaseOrder;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.SalesInvoice;
import spica.scm.validate.ConsignmentReferenceIs;

/**
 * ConsignmentReferenceService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:51 IST 2016
 */
public abstract class ConsignmentReferenceService {

  /**
   * ConsignmentReference paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getConsignmentReferenceSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_consignment_reference", ConsignmentReference.class, main);
    sql.main("select scm_consignment_reference.id,scm_consignment_reference.consignment_id,scm_consignment_reference.purchase_order_id,scm_consignment_reference.purchase_return_id,scm_consignment_reference.sales_order_id,scm_consignment_reference.sales_return_id from scm_consignment_reference scm_consignment_reference "); //Main query
    sql.count("select count(scm_consignment_reference.id) as total from scm_consignment_reference scm_consignment_reference "); //Count query
    sql.join("left outer join scm_consignment scm_consignment_referenceconsignment_id on (scm_consignment_referenceconsignment_id.id = scm_consignment_reference.consignment_id) left outer join scm_purchase_order scm_consignment_referencepurchase_order_id on (scm_consignment_referencepurchase_order_id.id = scm_consignment_reference.purchase_order_id) left outer join scm_purchase_return scm_consignment_referencepurchase_return_id on (scm_consignment_referencepurchase_return_id.id = scm_consignment_reference.purchase_return_id) left outer join scm_sales_order scm_consignment_referencesales_order_id on (scm_consignment_referencesales_order_id.id = scm_consignment_reference.sales_order_id) left outer join scm_sales_return scm_consignment_referencesales_return_id on (scm_consignment_referencesales_return_id.id = scm_consignment_reference.sales_return_id)"); //Join Query

    sql.string(new String[]{"scm_consignment_referenceconsignment_id.consignment_no", "scm_consignment_referencepurchase_order_id.purchase_order_no", "scm_consignment_referencepurchase_return_id.invoice_no", "scm_consignment_referencesales_order_id.order_no"}); //String search or sort fields
    sql.number(new String[]{"scm_consignment_reference.id", "scm_consignment_referencesales_return_id.customer_id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ConsignmentReference.
   *
   * @param main
   * @return List of ConsignmentReference
   */
  public static final List<ConsignmentReference> listPaged(Main main) {
    return AppService.listPagedJpa(main, getConsignmentReferenceSqlPaged(main));
  }

//  /**
//   * Return list of ConsignmentReference based on condition
//   * @param main
//   * @return List<ConsignmentReference>
//   */
//  public static final List<ConsignmentReference> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getConsignmentReferenceSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ConsignmentReference by key.
   *
   * @param main
   * @param consignmentReference
   * @return ConsignmentReference
   */
  public static final ConsignmentReference selectByPk(Main main, ConsignmentReference consignmentReference) {
    return (ConsignmentReference) AppService.find(main, ConsignmentReference.class, consignmentReference.getId());
  }

  /**
   * Insert ConsignmentReference.
   *
   * @param main
   * @param consignmentReference
   */
  public static final void insert(Main main, ConsignmentReference consignmentReference) {
    ConsignmentReferenceIs.insertAble(main, consignmentReference);  //Validating
    AppService.insert(main, consignmentReference);

  }

  /**
   * Update ConsignmentReference by key.
   *
   * @param main
   * @param consignmentReference
   * @return ConsignmentReference
   */
  public static final ConsignmentReference updateByPk(Main main, ConsignmentReference consignmentReference) {
    ConsignmentReferenceIs.updateAble(main, consignmentReference); //Validating
    return (ConsignmentReference) AppService.update(main, consignmentReference);
  }

  /**
   * Insert or update ConsignmentReference
   *
   * @param main
   * @param ConsignmentReference
   */
  public static void insertOrUpdate(Main main, ConsignmentReference consignmentReference) {
    if (consignmentReference.getId() == null) {
      insert(main, consignmentReference);
    } else {
      updateByPk(main, consignmentReference);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param consignmentReference
   */
  public static void clone(Main main, ConsignmentReference consignmentReference) {
    consignmentReference.setId(null); //Set to null for insert
    insert(main, consignmentReference);
  }

  /**
   * Delete ConsignmentReference.
   *
   * @param main
   * @param consignmentReference
   */
  public static final void deleteByPk(Main main, ConsignmentReference consignmentReference) {
    ConsignmentReferenceIs.deleteAble(main, consignmentReference); //Validation
    AppService.delete(main, ConsignmentReference.class, consignmentReference.getId());
  }

  /**
   * Delete Array of ConsignmentReference.
   *
   * @param main
   * @param consignmentReference
   */
  public static final void deleteByPkArray(Main main, ConsignmentReference[] consignmentReference) {
    for (ConsignmentReference e : consignmentReference) {
      deleteByPk(main, e);
    }
  }

  public static void insertArray(Main main, PurchaseOrder[] purchaseOrderSelected, Consignment consignment) {
    if (purchaseOrderSelected != null) {
      ConsignmentReference ConsignmentReference;
      for (PurchaseOrder purchaseOrder : purchaseOrderSelected) {  //Reinserting
        ConsignmentReference = new ConsignmentReference();
        ConsignmentReference.setPurchaseOrderId(purchaseOrder);
        ConsignmentReference.setConsignmentId(consignment);
        insert(main, ConsignmentReference);
      }
    }
  }

  public static void insertArray1(Main main, PurchaseReturn[] purchaseReturnSelected, Consignment consignment) {
    if (purchaseReturnSelected != null) {
      ConsignmentReference ConsignmentReference;
      for (PurchaseReturn purchaseReturn : purchaseReturnSelected) {  //Reinserting
        ConsignmentReference = new ConsignmentReference();
        ConsignmentReference.setPurchaseReturnId(purchaseReturn);
        ConsignmentReference.setConsignmentId(consignment);
        insert(main, ConsignmentReference);
      }
    }
  }

  public static void deleteConsignmentReferencePO(Main main, PurchaseOrder purchaseOrder, Consignment consignment) {
    AppService.deleteSql(main, ConsignmentReference.class, "delete from scm_consignment_reference where purchase_order_id = ? and consignment_id = ?", new Object[]{purchaseOrder.getId(), consignment.getId()});
  }

  public static void deleteConsignmentReferencePR(Main main, PurchaseReturn purchaseReturn, Consignment consignment) {
    AppService.deleteSql(main, ConsignmentReference.class, "delete from scm_consignment_reference where purchase_return_id = ? and consignment_id = ?", new Object[]{purchaseReturn.getId(), consignment.getId()});
  }

  public static void insertArrayInvoice(Main main, PurchaseReturn[] purchaseReturnInvSelected, Consignment consignment) {
    if (purchaseReturnInvSelected != null) {
      ConsignmentReference ConsignmentReference;
      for (PurchaseReturn purchaseReturn : purchaseReturnInvSelected) {  //Reinserting
        ConsignmentReference = new ConsignmentReference();
        ConsignmentReference.setPurchaseReturnId(purchaseReturn);
        ConsignmentReference.setConsignmentId(consignment);
        insert(main, ConsignmentReference);
      }
    }
  }

  public static void insertArraySalesInvoice(Main main, SalesInvoice[] salesInvoiceSelected, Consignment consignment) {
    if (salesInvoiceSelected != null) {
      ConsignmentReference ConsignmentReference;
      for (SalesInvoice salesInvoice : salesInvoiceSelected) {  //Reinserting
        ConsignmentReference = new ConsignmentReference();
        ConsignmentReference.setSalesInvoiceId(salesInvoice);
        ConsignmentReference.setConsignmentId(consignment);
        insert(main, ConsignmentReference);
      }
    }
  }
}
