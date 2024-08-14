/*
 * @(#)PurchaseOrderItemService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.PurchaseOrder;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.PurchaseOrderItem;
import spica.scm.domain.PurchaseReq;
import spica.scm.domain.PurchaseReqItem;
import spica.fin.domain.TaxCode;
import spica.scm.domain.ProductBatch;
import spica.scm.validate.PurchaseOrderItemIs;
import spica.sys.SystemConstants;

/**
 * PurchaseOrderItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class PurchaseOrderItemService {

  private static final int SORT_ORDER = 1;

  /**
   * PurchaseOrderItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseOrderItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_order_item", PurchaseOrderItem.class, main);
    sql.main("select scm_purchase_order_item.id,scm_purchase_order_item.purchase_order_id,scm_purchase_order_item.product_id,scm_purchase_order_item.qty_required,scm_purchase_order_item.qty_suggested,scm_purchase_order_item.last_landing_price_per_piece,scm_purchase_order_item.order_price_per_piece from scm_purchase_order_item scm_purchase_order_item "); //Main query
    sql.count("select count(scm_purchase_order_item.id) as total from scm_purchase_order_item scm_purchase_order_item "); //Count query
    sql.join("left outer join scm_purchase_order scm_purchase_order_itempurchase_order_id on (scm_purchase_order_itempurchase_order_id.id = scm_purchase_order_item.purchase_order_id) left outer join scm_product scm_purchase_order_itemproduct_id on (scm_purchase_order_itemproduct_id.id = scm_purchase_order_item.product_id) "); //Join Query

    sql.string(new String[]{"scm_purchase_order_itempurchase_order_id.purchase_order_no", "scm_purchase_order_itemproduct_id.product_name"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_order_item.id", "scm_purchase_order_item.qty_required", "scm_purchase_order_item.qty_suggested", "scm_purchase_order_item.last_landing_price_per_piece", "scm_purchase_order_item.order_price_per_piece"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseOrderItem.
   *
   * @param main
   * @return List of PurchaseOrderItem
   */
  public static final List<PurchaseOrderItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseOrderItemSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseOrderItem based on condition
//   * @param main
//   * @return List<PurchaseOrderItem>
//   */
//  public static final List<PurchaseOrderItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseOrderItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PurchaseOrderItem by key.
   *
   * @param main
   * @param purchaseOrderItem
   * @return PurchaseOrderItem
   */
  public static final PurchaseOrderItem selectByPk(Main main, PurchaseOrderItem purchaseOrderItem) {
    return (PurchaseOrderItem) AppService.find(main, PurchaseOrderItem.class, purchaseOrderItem.getId());
  }

  /**
   * Insert PurchaseOrderItem.
   *
   * @param main
   * @param purchaseOrderItem
   */
  public static final void insert(Main main, PurchaseOrderItem purchaseOrderItem) {
    PurchaseOrderItemIs.insertAble(main, purchaseOrderItem);  //Validating
    AppService.insert(main, purchaseOrderItem);

  }

  /**
   * Update PurchaseOrderItem by key.
   *
   * @param main
   * @param purchaseOrderItem
   * @return PurchaseOrderItem
   */
  public static final PurchaseOrderItem updateByPk(Main main, PurchaseOrderItem purchaseOrderItem) {
    PurchaseOrderItemIs.updateAble(main, purchaseOrderItem); //Validating
    return (PurchaseOrderItem) AppService.update(main, purchaseOrderItem);
  }

  /**
   * Insert or update PurchaseOrderItem
   *
   * @param main
   * @param purchaseOrderItem
   */
  public static void insertOrUpdate(Main main, PurchaseOrderItem purchaseOrderItem) {
    if (purchaseOrderItem.getId() == null) {
      insert(main, purchaseOrderItem);
    } else {
      updateByPk(main, purchaseOrderItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseOrderItem
   */
  public static void clone(Main main, PurchaseOrderItem purchaseOrderItem) {
    purchaseOrderItem.setId(null); //Set to null for insert
    insert(main, purchaseOrderItem);
  }

  /**
   * Delete PurchaseOrderItem.
   *
   * @param main
   * @param purchaseOrderItem
   */
  public static final void deleteByPk(Main main, PurchaseOrderItem purchaseOrderItem) {
    PurchaseOrderItemIs.deleteAble(main, purchaseOrderItem); //Validation
    AppService.delete(main, PurchaseOrderItem.class, purchaseOrderItem.getId());
  }

  /**
   * Delete Array of PurchaseOrderItem.
   *
   * @param main
   * @param purchaseOrderItem
   */
  public static final void deleteByPkArray(Main main, PurchaseOrderItem[] purchaseOrderItem) {
    for (PurchaseOrderItem e : purchaseOrderItem) {
      deleteByPk(main, e);
    }
  }

  public static List<PurchaseOrderItem> selectPurchaseOrderItemByPurchaseOrder(Main main, Integer purchaseOrderId) {
    List<PurchaseOrderItem> list = AppService.list(main, PurchaseOrderItem.class, "select * from scm_purchase_order_item where purchase_order_id = ?", new Object[]{purchaseOrderId});
    for (PurchaseOrderItem purchaseOrderItem : list) {
      purchaseOrderItem.setTaxCode(getProductTaxCode(main, purchaseOrderItem.getProductBatchId()));
    }
    return list;
  }

  /**
   *
   * @param main
   * @param productBatch
   * @return
   */
  public static TaxCode getProductTaxCode(Main main, ProductBatch productBatch) {
    TaxCode taxCode = null;
    if (productBatch != null) {
      if (productBatch.getProductId().getProductCategoryId() != null) {
        taxCode = productBatch.getProductId().getProductCategoryId().getPurchaseTaxCodeId();
      } else if (productBatch.getProductId().getCommodityId() != null) {
        taxCode = productBatch.getProductId().getCommodityId().getPurchaseTaxCodeId();
      }
    }
    return taxCode;
  }

  /**
   *
   * @param main
   * @param purchaseOrderItemList
   * @param purchaseOrder
   * @param orderStatusId
   * @param isNew
   */
  public static void insertOrUpdate(Main main, List<PurchaseOrderItem> purchaseOrderItemList, PurchaseOrder purchaseOrder, String orderStatusId, boolean isNew) {
    StringBuilder inParams = new StringBuilder();
    Object[] params = null;
    if (purchaseOrderItemList != null) {
      for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItemList) {
        purchaseOrderItem.setPurchaseOrderId(purchaseOrder);
        insertOrUpdate(main, purchaseOrderItem);
        if (isNew) {
          if (purchaseOrderItem.getPurchaseReqItemId() != null && purchaseOrderItem.getPurchaseReqItemId().getId() != null) {
            if (inParams.length() == 0) {
              inParams.append(purchaseOrderItem.getPurchaseReqItemId().getId());
            } else {
              inParams.append(",").append(purchaseOrderItem.getPurchaseReqItemId().getId());
            }
          }
        }
        purchaseOrderItem.setTaxCode(getProductTaxCode(main, purchaseOrderItem.getProductBatchId()));
      }
      main.em().flush();

      AppService.deleteSql(main, PurchaseOrderItem.class, "delete from scm_purchase_order_item where sort_order = ? and purchase_order_id = ?", new Object[]{SORT_ORDER, purchaseOrder.getId()});//deleting all preveously trashed records
      main.param(Integer.parseInt(orderStatusId));
      main.param(purchaseOrder.getId());
      AppService.updateSql(main, PurchaseOrder.class, "update scm_purchase_order set purchase_order_status_id = ? where id = ?", false);// draft or confirmed

      List<PurchaseReqItem> pReqList = main.em().list(PurchaseReqItem.class, "select purchase_requisition_id as id, sum(qty_required) as qty_required from scm_purchase_req_item where purchase_requisition_id in ("
              + "select distinct purchase_requisition_id from scm_purchase_req_item where id in (select purchase_req_item_id from scm_purchase_order_item "
              + "where purchase_order_id = ?)) group by purchase_requisition_id", new Object[]{purchaseOrder.getId()});

      for (PurchaseReqItem pri : pReqList) {
        main.clear();
        long usedCount = AppService.count(main, "select sum(qty_required) from scm_purchase_order_item where purchase_req_item_id in (select id from  scm_purchase_req_item where purchase_requisition_id = ?)", new Object[]{pri.getId()});
        if (usedCount >= pri.getQtyRequired()) {
          main.param(SystemConstants.CONFIRMED_AND_PROCESSED);
          main.param(pri.getId());
          AppService.updateSql(main, PurchaseReq.class, "update scm_purchase_req set purchase_requisition_status_id = ? where id = ?", false);
        } else {
          main.param(SystemConstants.CONFIRMED_PARTIALYY_PROCESSED);
          main.param(pri.getId());
          AppService.updateSql(main, PurchaseReq.class, "update scm_purchase_req set purchase_requisition_status_id = ? where id = ?", false);
        }
      }
    }
  }

  public static void updateTrashByPk(Main main, String orderDetailId) {
    main.param(1);
    main.param(Integer.parseInt(orderDetailId));
    AppService.updateSql(main, PurchaseOrderItem.class, "update scm_purchase_order_item set sort_order = ? where id = ?", false);
  }

  /**
   *
   * @param main
   * @param purchaseOrder
   * @param purchaseOrderItemList
   */
  public static void insertOrUpdatePurchaseOrderItem(Main main, PurchaseOrder purchaseOrder, List<PurchaseOrderItem> purchaseOrderItemList) {
    for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItemList) {
      if (purchaseOrderItem.getProductId() != null && purchaseOrderItem.getProductBatchId() != null && purchaseOrderItem.getQtyRequired() != null && purchaseOrderItem.getQtyRequired() > 0) {
        purchaseOrderItem.setPurchaseOrderId(purchaseOrder);
        insertOrUpdate(main, purchaseOrderItem);
      }
    }
  }

  /**
   *
   * @param main
   * @param purchaseOrder
   */
  public static void updatePurchaseRequisitionStatus(Main main, PurchaseOrder purchaseOrder) {
    main.em().flush();
    List<PurchaseReqItem> pReqList = main.em().list(PurchaseReqItem.class, "select purchase_requisition_id as id, sum(qty_required) as qty_required from scm_purchase_req_item where purchase_requisition_id in ("
            + "select distinct purchase_requisition_id from scm_purchase_req_item where id in (select purchase_req_item_id from scm_purchase_order_item "
            + "where purchase_order_id = ?)) group by purchase_requisition_id", new Object[]{purchaseOrder.getId()});

    for (PurchaseReqItem pri : pReqList) {
      main.clear();
      long usedCount = AppService.count(main, "select sum(qty_required) from scm_purchase_order_item where purchase_req_item_id in (select id from  scm_purchase_req_item where purchase_requisition_id = ?)", new Object[]{pri.getId()});
      if (usedCount >= pri.getQtyRequired()) {
        main.param(SystemConstants.CONFIRMED_AND_PROCESSED);
        main.param(pri.getId());
        AppService.updateSql(main, PurchaseReq.class, "update scm_purchase_req set purchase_requisition_status_id = ? where id = ?", false);
      } else {
        main.param(SystemConstants.CONFIRMED_PARTIALYY_PROCESSED);
        main.param(pri.getId());
        AppService.updateSql(main, PurchaseReq.class, "update scm_purchase_req set purchase_requisition_status_id = ? where id = ?", false);
      }
    }
  }
}
