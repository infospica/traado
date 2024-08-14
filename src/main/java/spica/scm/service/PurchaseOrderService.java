/*
 * @(#)PurchaseOrderService.java	1.0 Mon Apr 11 14:41:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import spica.scm.domain.Account;
import spica.scm.domain.Consignment;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.PurchaseOrder;
import spica.scm.domain.PurchaseOrderItem;
import spica.scm.domain.PurchaseOrderStatus;
import spica.scm.domain.PurchaseReq;
import spica.scm.domain.PurchaseShipmentPlan;
import spica.scm.validate.PurchaseOrderIs;
import spica.scm.view.AccountUtil;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;

/**
 * PurchaseOrderService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Apr 11 14:41:21 IST 2016
 */
public abstract class PurchaseOrderService {

  /**
   * PurchaseOrder paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseOrderSqlPaged(Main main) {
    // left outer join scm_account scm_account_id on (scm_account_id.id = scm_purchase_order.account_id)
    SqlPage sql = AppService.sqlPage("scm_purchase_order", PurchaseOrder.class, main);
    sql.main("select scm_purchase_order.id,scm_purchase_order.purchase_order_no,scm_purchase_order.purchase_order_date,scm_purchase_order.account_id,"
            + "scm_purchase_order.contract_id,scm_purchase_order.purchase_order_status_id,scm_purchase_order.created_by,scm_purchase_order.modified_by,scm_purchase_order.financial_year_id,"
            + "scm_purchase_order.created_at,scm_purchase_order.modified_at,scm_purchase_order.confirmed_by,scm_purchase_order.confirmed_at,scm_purchase_order.company_id,"
            + "scm_purchase_order.to_be_approved_by,scm_purchase_order.approved_by,scm_purchase_order.approved_at from scm_purchase_order scm_purchase_order "); //Main query
    sql.count("select count(scm_purchase_order.id) as total from scm_purchase_order scm_purchase_order "); //Count query
    sql.join("left outer join scm_contract scm_purchase_ordercontract_id on (scm_purchase_ordercontract_id.id = scm_purchase_order.contract_id) left outer join scm_purchase_order_status scm_purchase_orderpurchase_order_status_id on (scm_purchase_orderpurchase_order_status_id.id = scm_purchase_order.purchase_order_status_id)"); //Join Query

    sql.string(new String[]{"scm_purchase_order.purchase_order_no", "scm_purchase_ordercontract_id.contract_code", "scm_purchase_orderpurchase_order_status_id.title", "scm_purchase_order.created_by", "scm_purchase_order.modified_by", "scm_purchase_order.confirmed_by", "scm_purchase_order.to_be_approved_by", "scm_purchase_order.approved_by"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_order.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_purchase_order.purchase_order_date", "scm_purchase_order.created_at", "scm_purchase_order.modified_at", "scm_purchase_order.confirmed_at", "scm_purchase_order.approved_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseOrder.
   *
   * @param main
   * @return List of PurchaseOrder
   */
  public static final List<PurchaseOrder> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseOrderSqlPaged(main));
  }

  public static final List<PurchaseOrder> listPagedPurchaseOrderByAccount(Main main, Account activeAccount, Map<String, Object> filters) {
    StringBuilder whereCondition = new StringBuilder();
    SqlPage sql = getPurchaseOrderSqlPaged(main);
    whereCondition.append("where scm_purchase_order.account_id = ?");
    sql.param(activeAccount.getId());
    if (filters != null) {
      for (String key : filters.keySet()) {
        whereCondition.append(" and ").append(key).append(" = ? ");
        sql.param(filters.get(key));
      }
      sql.append(whereCondition.toString());
    }
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of PurchaseOrder based on condition
//   * @param main
//   * @return List<PurchaseOrder>
//   */
//  public static final List<PurchaseOrder> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseOrderSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PurchaseOrder by key.
   *
   * @param main
   * @param purchaseOrder
   * @return PurchaseOrder
   */
  public static final PurchaseOrder selectByPk(Main main, PurchaseOrder purchaseOrder) {
    return (PurchaseOrder) AppService.find(main, PurchaseOrder.class, purchaseOrder.getId());
  }

  /**
   * Insert PurchaseOrder.
   *
   * @param main
   * @param purchaseOrder
   */
  public static final void insert(Main main, PurchaseOrder purchaseOrder) {
    PurchaseOrderIs.insertAble(main, purchaseOrder);  //Validating
    AppService.insert(main, purchaseOrder);

  }

  /**
   * Update PurchaseOrder by key.
   *
   * @param main
   * @param purchaseOrder
   * @return PurchaseOrder
   */
  public static final PurchaseOrder updateByPk(Main main, PurchaseOrder purchaseOrder) {
    PurchaseOrderIs.updateAble(main, purchaseOrder); //Validating
    return (PurchaseOrder) AppService.update(main, purchaseOrder);
  }

  /**
   * Insert or update PurchaseOrder
   *
   * @param main
   * @param purchaseOrder
   */
  public static void insertOrUpdate(Main main, PurchaseOrder purchaseOrder) {
    purchaseOrder.setCompanyId(UserRuntimeView.instance().getCompany());
    if (SystemConstants.CONFIRMED.equals(purchaseOrder.getPurchaseOrderStatusId().getId())) {
      if (AccountUtil.isPrimaryVendorAccount(purchaseOrder.getAccountId())) {
        purchaseOrder.setPurchaseOrderNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, purchaseOrder.getAccountId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, false, purchaseOrder.getFinancialYearId()));
        AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseOrder.getAccountId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, false, purchaseOrder.getFinancialYearId());
      } else {
        if (AccountService.INDIVIDUAL_SUPPLIER.equals(purchaseOrder.getAccountId().getPurchaseChannel())) {
          purchaseOrder.setPurchaseOrderNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, purchaseOrder.getAccountId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, false, purchaseOrder.getFinancialYearId()));
          AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseOrder.getAccountId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, false, purchaseOrder.getFinancialYearId());
        } else {
          purchaseOrder.setPurchaseOrderNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, purchaseOrder.getAccountId().getAccountId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, false, purchaseOrder.getFinancialYearId()));
          AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseOrder.getAccountId().getAccountId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, false, purchaseOrder.getFinancialYearId());
        }
      }
    }

    if (purchaseOrder.getId() == null) {
      insert(main, purchaseOrder);
      if (AccountUtil.isPrimaryVendorAccount(purchaseOrder.getAccountId())) {
        AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseOrder.getAccountId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, true, purchaseOrder.getFinancialYearId());
      } else {
        if (AccountService.INDIVIDUAL_SUPPLIER.equals(purchaseOrder.getAccountId().getPurchaseChannel())) {
          AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseOrder.getAccountId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, true, purchaseOrder.getFinancialYearId());
        } else {
          AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseOrder.getAccountId().getAccountId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, true, purchaseOrder.getFinancialYearId());
        }
      }
    } else {
      updateByPk(main, purchaseOrder);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseOrder
   */
  public static void clone(Main main, PurchaseOrder purchaseOrder) {
    purchaseOrder.setId(null); //Set to null for insert    
    insert(main, purchaseOrder);
  }

  /**
   * Delete PurchaseOrder.
   *
   * @param main
   * @param purchaseOrder
   */
  public static final void deleteByPk(Main main, PurchaseOrder purchaseOrder) {
    main.param(SystemConstants.CONFIRMED_PARTIALYY_PROCESSED);
    main.param(purchaseOrder.getId());
    AppService.updateSql(main, PurchaseReq.class, "update scm_purchase_req set purchase_requisition_status_id = ? where id in ("
            + "select pri.purchase_requisition_id from scm_purchase_req_item pri "
            + "inner join scm_purchase_order_item poi on pri.id = poi.purchase_req_item_id "
            + "where poi.purchase_order_id = ? "
            + ")", false);
    AppService.deleteSql(main, PurchaseOrderItem.class, "delete from scm_purchase_order_item where purchase_order_id = ?", new Object[]{purchaseOrder.getId()});//deleting all child
    PurchaseOrderIs.deleteAble(main, purchaseOrder); //Validation
    AppService.delete(main, PurchaseOrder.class, purchaseOrder.getId());
  }

  /**
   * Delete Array of PurchaseOrder.
   *
   * @param main
   * @param purchaseOrder
   */
  public static final void deleteByPkArray(Main main, PurchaseOrder[] purchaseOrder) {
    for (PurchaseOrder e : purchaseOrder) {
      deleteByPk(main, e);
    }
  }

  /**
   * Insert or update PurchaseOrder
   *
   * @param main
   * @param purchaseReq
   */
  public static PurchaseOrder createPO(Main main, PurchaseReq purchaseReq) {
    PurchaseOrder po = new PurchaseOrder();
    po.setAccountId(purchaseReq.getAccountId());
    po.setContractId(purchaseReq.getContractId());
    po.setPurchaseOrderNo(purchaseReq.getRequisitionNo());
    po.setPurchaseOrderStatusId(new PurchaseOrderStatus(SystemConstants.DRAFT));
    po.setPurchaseOrderDate(new Date());
    po.setCompanyId(UserRuntimeView.instance().getCompany());
    if (po.getId() == null) {
      insert(main, po);
    } else {
      updateByPk(main, po);
    }
    return po;
  }

  public static List<PurchaseOrder> selectByPOConsignment(MainView main, Consignment consignment) {
    SqlPage sql = getPurchaseOrderSqlPaged(main);
    sql.join("left outer join scm_consignment_reference scm_consignment_reference on (scm_purchase_order.id = scm_consignment_reference.purchase_order_id)");
    if (consignment.getId() == null) {
      return null;
    }
    sql.cond("where scm_consignment_reference.consignment_id = ?");
    sql.param(consignment.getId());
    return AppService.listAllJpa(main, sql);
  }

//  public static final List<PurchaseOrder> selectByPOConsignment(Main main, Consignment consignment) {
//    return AppService.list(main, PurchaseOrder.class, "select scm_purchase_order.id,scm_purchase_order.purchase_order_no from scm_purchase_order scm_purchase_order ", new Object[]{});
//  }
  public static List<PurchaseOrder> listPOReferenceNotInConsignment(Main main, Consignment consignment) {
    SqlPage sql = getPurchaseOrderSqlPaged(main);
    if (consignment.getId() == null) {
      return null;
    }
    sql.cond("where scm_purchase_order.id not in(select scm_consignment_reference.purchase_order_id from scm_consignment_reference scm_consignment_reference where scm_consignment_reference.consignment_id =?)");
    sql.param(consignment.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static List<PurchaseOrder> selectPO(MainView main, Consignment consignment) {
    SqlPage sql = getPurchaseOrderSqlPaged(main);
    if (consignment.getId() == null) {
      return null;
    }
    sql.cond("where scm_purchase_order.account_id = ? and scm_purchase_order.purchase_order_status_id <> ? and scm_purchase_order.id not in(select scm_consignment_reference.purchase_order_id from scm_consignment_reference scm_consignment_reference where scm_consignment_reference.consignment_id =?)");
    sql.param(consignment.getAccountId().getId());
    sql.param(SystemConstants.DRAFT);
    sql.param(consignment.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static List<PurchaseShipmentPlan> getPurchaseShipmentPlanList(Main main, PurchaseOrder purchaseOrder) {
    String sql = "select POI.purchase_order_id, POI.id, scm_product.product_name,POI.qty_required,POIS.qty1,POIS.qty2,POIS.qty3 from scm_purchase_order_item as POI, "
            + "( "
            + "SELECT * FROM crosstab('select purchase_order_item_id, purchase_order_id, quantity_required from scm_purchase_receipt_plan where purchase_order_id = " + purchaseOrder.getId() + " ') "
            + "AS scm_purchase_receipt_plan(po_item_id INT,qty1 NUMERIC, qty2 NUMERIC, qty3 NUMERIC) "
            + ") as POIS, "
            + "(select * from scm_product ) as scm_product "
            + "WHERE POI.id = POIS.po_item_id and scm_product.id = POI.product_id";
    List<PurchaseShipmentPlan> list = AppDb.getList(main.dbConnector(), PurchaseShipmentPlan.class, sql, null);
    if (list.size() == 0) {
      list = AppDb.getList(main.dbConnector(), PurchaseShipmentPlan.class, "select id,purchase_order_id,product_name,qty_required from scm_purchase_order_item where purchase_order_id=?", new Object[]{purchaseOrder.getId()});
    }
    return list;
  }
}
