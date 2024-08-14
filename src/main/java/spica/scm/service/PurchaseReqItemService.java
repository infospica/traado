/*
 * @(#)PurchaseReqItemService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.PurchaseReq;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.PurchaseReqItem;
import spica.scm.validate.PurchaseReqItemIs;
import wawo.entity.util.StringUtil;

/**
 * PurchaseReqItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class PurchaseReqItemService {

  /**
   * PurchaseReqItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReqItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_req_item", PurchaseReqItem.class, main);
    sql.main("select scm_purchase_req_item.id,scm_purchase_req_item.purchase_requisition_id,scm_purchase_req_item.product_id,scm_purchase_req_item.product_detail_id,scm_purchase_req_item.qty_required,scm_purchase_req_item.product_name,scm_purchase_req_item.qty_suggested,scm_purchase_req_item.sort_order from scm_purchase_req_item scm_purchase_req_item "); //Main query
    sql.count("select count(scm_purchase_req_item.id) as total from scm_purchase_req_item scm_purchase_req_item "); //Count query
    sql.join("left outer join scm_purchase_req scm_purchase_req_itempurchase_requisition_id on (scm_purchase_req_itempurchase_requisition_id.id = scm_purchase_req_item.purchase_requisition_id) left outer join scm_product scm_purchase_req_itemproduct_id on (scm_purchase_req_itemproduct_id.id = scm_purchase_req_item.product_id) left outer join scm_product_detail scm_purchase_req_itemproduct_detail_id on (scm_purchase_req_itemproduct_detail_id.id = scm_purchase_req_item.product_detail_id)"); //Join Query

    sql.string(new String[]{"scm_purchase_req_itempurchase_requisition_id.requisition_no", "scm_purchase_req_itemproduct_id.product_name", "scm_purchase_req_itemproduct_detail_id.batch_no", "scm_purchase_req_item.product_name"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_req_item.id", "scm_purchase_req_item.qty_required", "scm_purchase_req_item.qty_suggested", "scm_purchase_req_item.sort_order"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseReqItem.
   *
   * @param main
   * @return List of PurchaseReqItem
   */
  public static final List<PurchaseReqItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseReqItemSqlPaged(main));
  }

  /**
   * Select PurchaseReqItem by key.
   *
   * @param main
   * @param purchaseReqItem
   * @return PurchaseReqItem
   */
  public static final PurchaseReqItem selectByPk(Main main, PurchaseReqItem purchaseReqItem) {
    return (PurchaseReqItem) AppService.find(main, PurchaseReqItem.class, purchaseReqItem.getId());
  }

  /**
   * Insert PurchaseReqItem.
   *
   * @param main
   * @param purchaseReqItem
   */
  public static final void insert(Main main, PurchaseReqItem purchaseReqItem) {
    PurchaseReqItemIs.insertAble(main, purchaseReqItem);  //Validating
    AppService.insert(main, purchaseReqItem);

  }

  /**
   * Update PurchaseReqItem by key.
   *
   * @param main
   * @param purchaseReqItem
   * @return PurchaseReqItem
   */
  public static final PurchaseReqItem updateByPk(Main main, PurchaseReqItem purchaseReqItem) {
    PurchaseReqItemIs.updateAble(main, purchaseReqItem); //Validating
    return (PurchaseReqItem) AppService.update(main, purchaseReqItem);
  }

  /**
   * Insert or update PurchaseReqItem
   *
   * @param main
   * @param purchaseReqItem
   */
  public static void insertOrUpdate(Main main, PurchaseReqItem purchaseReqItem) {
    //ProductDetail productDetail = (ProductDetail) AppService.single(main, ProductDetail.class, "select * from scm_product_detail where product_id = ? order by id asc limit 1", new Object[]{purchaseReqItem.getProductId().getId()});
    //purchaseReqItem.setProductDetailId(productDetail);
    if (purchaseReqItem.getId() == null) {
      insert(main, purchaseReqItem);
    } else {
      updateByPk(main, purchaseReqItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReqItem
   */
  public static void clone(Main main, PurchaseReqItem purchaseReqItem) {
    purchaseReqItem.setId(null); //Set to null for insert
    insert(main, purchaseReqItem);
  }

  /**
   * Delete PurchaseReqItem.
   *
   * @param main
   * @param purchaseReqItem
   */
  public static final void deleteByPk(Main main, PurchaseReqItem purchaseReqItem) {
    PurchaseReqItemIs.deleteAble(main, purchaseReqItem); //Validation
    AppService.delete(main, PurchaseReqItem.class, purchaseReqItem.getId());
  }

  /**
   * Delete Array of PurchaseReqItem.
   *
   * @param main
   * @param purchaseReqItem
   */
  public static final void deleteByPkArray(Main main, PurchaseReqItem[] purchaseReqItem) {
    for (PurchaseReqItem e : purchaseReqItem) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param purchaseReq
   * @return
   */
  public static List<PurchaseReqItem> selectPurchaseReqItemByPurchaseReq(Main main, PurchaseReq purchaseReq) {
    String sql = "select * from scm_purchase_req_item where purchase_requisition_id = ?";
    return AppService.list(main, PurchaseReqItem.class, sql, new Object[]{purchaseReq.getId()});
  }

  /**
   *
   * @param main
   * @param purchaseReqId
   * @return
   */
  public static List<PurchaseReqItem> selectPurchaseReqItemByPurchaseReq(Main main, String purchaseReqId) {
    if (!StringUtil.isEmpty(purchaseReqId)) {
      return AppService.list(main, PurchaseReqItem.class, "select * from scm_purchase_req_item where purchase_requisition_id in (" + purchaseReqId + ") "
              + "and id not in (select purchase_req_item_id from scm_purchase_order_item where purchase_req_item_id is not null)", null);
    }
    return null;
  }

  /**
   * Insert or update PurchaseReqItem
   *
   * @param main
   * @param purchaseReqItemList
   * @param purchaseReq
   *
   */
  public static void insertOrUpdatePurchaseReqItem(Main main, List<PurchaseReqItem> purchaseReqItemList, PurchaseReq purchaseReq) {
    if (purchaseReqItemList != null) {
      for (PurchaseReqItem purchaseReqItem : purchaseReqItemList) {
        if (purchaseReqItem.getProductId() != null && purchaseReqItem.getQtyRequired() != null && purchaseReqItem.getQtyRequired() > 0) {
          purchaseReqItem.setPurchaseRequisitionId(purchaseReq);
          insertOrUpdate(main, purchaseReqItem);
        }
      }
    }
  }

}
