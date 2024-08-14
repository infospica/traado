/*
 * @(#)ConsignmentCommodityService.java	1.0 Fri Jul 22 10:57:51 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Consignment;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ConsignmentCommodity;
import spica.scm.domain.ConsignmentReceiptDetail;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.SalesInvoice;
import spica.scm.validate.ConsignmentCommodityIs;

/**
 * ConsignmentCommodityService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:51 IST 2016
 */
public abstract class ConsignmentCommodityService {

  /**
   * ConsignmentCommodity paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getConsignmentCommoditySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_consignment_commodity", ConsignmentCommodity.class, main);
    sql.main("select scm_consignment_commodity.id,scm_consignment_commodity.consignment_id,scm_consignment_commodity.commodity_id,scm_consignment_commodity.commodity_name,"
            + "scm_consignment_commodity.commodity_description,scm_consignment_commodity.commodity_qty,scm_consignment_commodity.commodity_weight,"
            + "scm_consignment_commodity.commodity_qty_unit,scm_consignment_commodity.commodity_weight_unit,"
            + "scm_consignment_commodity.commodity_value,scm_consignment_commodity.invoice_no,scm_consignment_commodity.invoice_date,"
            + "scm_consignment_commodity.invoice_file_path,scm_consignment_commodity.debit_note_no,scm_consignment_commodity.debit_note_date,"
            + "scm_consignment_commodity.debit_note_file_path from scm_consignment_commodity scm_consignment_commodity "); //Main query
    sql.count("select count(scm_consignment_commodity.id) as total from scm_consignment_commodity scm_consignment_commodity "); //Count query
    sql.join("left outer join scm_consignment scm_consignment_commodityconsignment_id on (scm_consignment_commodityconsignment_id.id = scm_consignment_commodity.consignment_id) left outer join scm_service_commodity scm_consignment_commoditycommodity_id on (scm_consignment_commoditycommodity_id.id = scm_consignment_commodity.commodity_id)"); //Join Query

    sql.string(new String[]{"scm_consignment_commodityconsignment_id.consignment_no", "scm_consignment_commoditycommodity_id.title", "scm_consignment_commodity.commodity_name", "scm_consignment_commodity.commodity_description", "scm_consignment_commodity.invoice_no", "scm_consignment_commodity.invoice_file_path", "scm_consignment_commodity.debit_note_no", "scm_consignment_commodity.debit_note_file_path"}); //String search or sort fields
    sql.number(new String[]{"scm_consignment_commodity.id", "scm_consignment_commodity.commodity_qty", "scm_consignment_commodity.commodity_weight", "scm_consignment_commodity.commodity_value"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_consignment_commodity.invoice_date", "scm_consignment_commodity.debit_note_date"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ConsignmentCommodity.
   *
   * @param main
   * @return List of ConsignmentCommodity
   */
  public static final List<ConsignmentCommodity> listPaged(Main main) {
    return AppService.listPagedJpa(main, getConsignmentCommoditySqlPaged(main));
  }

//  /**
//   * Return list of ConsignmentCommodity based on condition
//   * @param main
//   * @return List<ConsignmentCommodity>
//   */
//  public static final List<ConsignmentCommodity> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getConsignmentCommoditySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ConsignmentCommodity by key.
   *
   * @param main
   * @param consignmentCommodity
   * @return ConsignmentCommodity
   */
  public static final ConsignmentCommodity selectByPk(Main main, ConsignmentCommodity consignmentCommodity) {
    return (ConsignmentCommodity) AppService.find(main, ConsignmentCommodity.class, consignmentCommodity.getId());
  }

  /**
   * Insert ConsignmentCommodity.
   *
   * @param main
   * @param consignmentCommodity
   */
  public static final void insert(Main main, ConsignmentCommodity consignmentCommodity) {
    ConsignmentCommodityIs.insertAble(main, consignmentCommodity);  //Validating
    AppService.insert(main, consignmentCommodity);

  }

  /**
   * Update ConsignmentCommodity by key.
   *
   * @param main
   * @param consignmentCommodity
   * @return ConsignmentCommodity
   */
  public static final ConsignmentCommodity updateByPk(Main main, ConsignmentCommodity consignmentCommodity) {
    ConsignmentCommodityIs.updateAble(main, consignmentCommodity); //Validating
    return (ConsignmentCommodity) AppService.update(main, consignmentCommodity);
  }

  /**
   * Insert or update ConsignmentCommodity
   *
   * @param main
   * @param ConsignmentCommodity
   */
  public static void insertOrUpdate(Main main, ConsignmentCommodity consignmentCommodity) {
    if (consignmentCommodity.getId() == null) {
      insert(main, consignmentCommodity);
    } else {
      updateByPk(main, consignmentCommodity);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param consignmentCommodity
   */
  public static void clone(Main main, ConsignmentCommodity consignmentCommodity) {
    consignmentCommodity.setId(null); //Set to null for insert
    insert(main, consignmentCommodity);
  }

  /**
   * Delete ConsignmentCommodity.
   *
   * @param main
   * @param consignmentCommodity
   */
  public static final void deleteByPk(Main main, ConsignmentCommodity consignmentCommodity) {
    ConsignmentCommodityIs.deleteAble(main, consignmentCommodity); //Validation
    AppService.deleteSql(main, ConsignmentReceiptDetail.class, "delete from scm_consignment_receipt_detail scm_consignment_receipt_detail where scm_consignment_receipt_detail.consignment_commodity_id=?", new Object[]{consignmentCommodity.getId()});
    AppService.delete(main, ConsignmentCommodity.class, consignmentCommodity.getId());
  }

  /**
   * Delete Array of ConsignmentCommodity.
   *
   * @param main
   * @param consignmentCommodity
   */
  public static final void deleteByPkArray(Main main, ConsignmentCommodity[] consignmentCommodity) {
    for (ConsignmentCommodity e : consignmentCommodity) {
      deleteByPk(main, e);
    }
  }

  /**
   * Return all commodity of a consignment.
   *
   * @param main
   * @param consignment
   * @return
   */
  public static final List<ConsignmentCommodity> getConsignmentCommodityList(Main main, Consignment consignment) {
    SqlPage sql = getConsignmentCommoditySqlPaged(main);
    if (consignment.getId() == null) {
      return null;
    }
    sql.cond("where scm_consignment_commodity.consignment_id=?");
    sql.param(consignment.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static Long selectReceiptDetail(Main main, ConsignmentCommodity consignmentCommodity) {
    return main.em().count("select count(id) from scm_consignment_receipt_detail where scm_consignment_receipt_detail.consignment_commodity_id = ?", new Object[]{consignmentCommodity.getId()});
  }

  public static final ConsignmentReceiptDetail receiptPlanDetails(Main main, ConsignmentCommodity consignmentCommodity) {
    if (consignmentCommodity.getId() != null) {
      return (ConsignmentReceiptDetail) AppService.single(main, ConsignmentReceiptDetail.class, "select * from scm_consignment_receipt_detail where scm_consignment_receipt_detail.consignment_commodity_id=?", new Object[]{consignmentCommodity.getId()});
    } else {
      return null;
    }
  }

  public static Long selectCommodity(Main main, Consignment consignment) {
    return main.em().count("select count(id) from scm_consignment_commodity where consignment_id = ?", new Object[]{consignment.getId()});
  }

  public static void insertPurchaseReturnItems(Main main, PurchaseReturn[] purchaseReturnInvSelected, Consignment consignment) {
    if (purchaseReturnInvSelected != null) {
      ConsignmentCommodity consignmentCommodity;
      for (PurchaseReturn purchaseReturn : purchaseReturnInvSelected) {  //Reinserting
        consignmentCommodity = new ConsignmentCommodity();
        consignmentCommodity.setInvoiceNo(purchaseReturn.getInvoiceNo());
        consignmentCommodity.setInvoiceDate(purchaseReturn.getInvoiceDate());
        consignmentCommodity.setConsignmentId(consignment);
        consignmentCommodity.setCommodityWeight((double) purchaseReturn.getWeight());
        consignmentCommodity.setCommodityQty(purchaseReturn.getNoOfBoxes().doubleValue());
        consignmentCommodity.setCommodityValue(purchaseReturn.getInvoiceAmountNet());
        consignmentCommodity.setCommodityId(purchaseReturn.getCommodityId());
        consignmentCommodity.setPurchaseReturnId(purchaseReturn);
        insert(main, consignmentCommodity);
      }
    }
  }

  public static void insertArraySales(Main main, SalesInvoice[] salesInvoiceSelected, Consignment consignment) {
    if (salesInvoiceSelected != null) {
      ConsignmentCommodity consignmentCommodity;
      for (SalesInvoice salesInvoice : salesInvoiceSelected) {  //Reinserting
        consignmentCommodity = new ConsignmentCommodity();
        consignmentCommodity.setInvoiceNo(salesInvoice.getInvoiceNo());
        consignmentCommodity.setInvoiceDate(salesInvoice.getInvoiceDate());
        consignmentCommodity.setConsignmentId(consignment);
        consignmentCommodity.setCommodityWeight(salesInvoice.getWeight() == null ? null : (double) salesInvoice.getWeight());
        consignmentCommodity.setCommodityQty(salesInvoice.getNoOfBoxes() == null ? null : salesInvoice.getNoOfBoxes().doubleValue());
        consignmentCommodity.setCommodityValue(salesInvoice.getInvoiceAmountNet());
        consignmentCommodity.setCommodityId(salesInvoice.getCommodityId());
        consignmentCommodity.setSalesInvoiceId(salesInvoice);
        insert(main, consignmentCommodity);
      }
    }
  }

  public static final Consignment consignmentByReturnId(Main main, PurchaseReturn purchaseReturn) {
    if (purchaseReturn.getId() != null) {
      return (Consignment) AppService.single(main, Consignment.class, "select * from scm_consignment where id in (select consignment_id from scm_consignment_commodity where purchase_return_id=? limit ?)", new Object[]{purchaseReturn.getId(), 1});
    } else {
      return null;
    }
  }

  public static final Consignment consignmentBySalesInvoice(Main main, SalesInvoice salesInvoice) {
    if (salesInvoice.getId() != null) {
      return (Consignment) AppService.single(main, Consignment.class, "select * from scm_consignment where id in (select consignment_id from scm_consignment_commodity where sales_invoice_id = ? limit ?)", new Object[]{salesInvoice.getId(), 1});
    } else {
      return null;
    }
  }

  public static void insertOrUpdateInvoiceEntryDetails(Main main, ProductEntry productEntry) {
    ConsignmentCommodity consignmentCommodity = (ConsignmentCommodity) AppService.single(main, ConsignmentCommodity.class, "select * from scm_consignment_commodity where product_entry_id = ? and consignment_id = ?", new Object[]{productEntry.getId(), productEntry.getConsignmentId().getId()});
    if (consignmentCommodity != null && consignmentCommodity.getId() != null) {
      consignmentCommodity.setConsignmentId(productEntry.getConsignmentId());
      consignmentCommodity.setInvoiceDate(productEntry.getInvoiceDate());
      consignmentCommodity.setInvoiceNo(productEntry.getInvoiceNo());
      consignmentCommodity.setProductEntryId(productEntry);
      consignmentCommodity.setCommodityQty(productEntry.getProductSecondaryPackQuantity());
      consignmentCommodity.setCommodityQtyUnit(1);
      consignmentCommodity.setCommodityValue(productEntry.getInvoiceAmount());
      insertOrUpdate(main, consignmentCommodity);
    } else {
      consignmentCommodity = new ConsignmentCommodity();
      consignmentCommodity.setConsignmentId(productEntry.getConsignmentId());
      consignmentCommodity.setInvoiceDate(productEntry.getInvoiceDate());
      consignmentCommodity.setInvoiceNo(productEntry.getInvoiceNo());
      consignmentCommodity.setProductEntryId(productEntry);
      consignmentCommodity.setCommodityQty(productEntry.getProductSecondaryPackQuantity());
      consignmentCommodity.setCommodityQtyUnit(1);
      consignmentCommodity.setCommodityValue(productEntry.getInvoiceAmount());
      insert(main, consignmentCommodity);
    }
  }
}
