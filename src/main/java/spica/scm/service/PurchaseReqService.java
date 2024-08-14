/*
 * @(#)PurchaseReqService.java	1.0 Wed Apr 13 15:41:17 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import spica.scm.common.ProductQuantityInfo;
import spica.scm.domain.Account;
import spica.scm.domain.Product;
import spica.scm.domain.PurchaseReq;
import spica.scm.domain.PurchaseReqItem;
import spica.scm.validate.PurchaseReqIs;
import spica.scm.view.AccountUtil;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;

/**
 * PurchaseReqService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:17 IST 2016
 */
public abstract class PurchaseReqService {

  /**
   * PurchaseReq paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPurchaseReqSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_req", PurchaseReq.class, main);
    sql.main("select scm_purchase_req.id,scm_purchase_req.company_id,scm_purchase_req.financial_year_id,scm_purchase_req.requisition_no,scm_purchase_req.requisition_date,scm_purchase_req.contract_id,scm_purchase_req.purchase_requisition_status_id,scm_purchase_req.created_by,scm_purchase_req.modified_by,scm_purchase_req.created_at,scm_purchase_req.modified_at,scm_purchase_req.confirmed_by,scm_purchase_req.confirmed_at from scm_purchase_req scm_purchase_req "); //Main query
    sql.count("select count(scm_purchase_req.id) as total from scm_purchase_req scm_purchase_req "); //Count query
    sql.join("left outer join scm_contract scm_purchase_reqcontract_id on (scm_purchase_reqcontract_id.id = scm_purchase_req.contract_id) left outer join scm_purchase_req_status scm_purchase_reqpurchase_requisition_status_id on (scm_purchase_reqpurchase_requisition_status_id.id = scm_purchase_req.purchase_requisition_status_id)"); //Join Query

    sql.string(new String[]{"scm_purchase_req.requisition_no", "scm_purchase_reqcontract_id.contract_code", "scm_purchase_reqpurchase_requisition_status_id.title", "scm_purchase_req.created_by", "scm_purchase_req.modified_by", "scm_purchase_req.confirmed_by"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_req.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_purchase_req.requisition_date", "scm_purchase_req.created_at", "scm_purchase_req.modified_at", "scm_purchase_req.confirmed_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PurchaseReq.
   *
   * @param main
   * @return List of PurchaseReq
   */
  public static final List<PurchaseReq> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPurchaseReqSqlPaged(main));
  }

//  /**
//   * Return list of PurchaseReq based on condition
//   * @param main
//   * @return List<PurchaseReq>
//   */
//  public static final List<PurchaseReq> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPurchaseReqSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PurchaseReq by key.
   *
   * @param main
   * @param purchaseReq
   * @return PurchaseReq
   */
  public static final PurchaseReq selectByPk(Main main, PurchaseReq purchaseReq) {
    return (PurchaseReq) AppService.find(main, PurchaseReq.class, purchaseReq.getId());
  }

  /**
   * Insert PurchaseReq.
   *
   * @param main
   * @param purchaseReq
   */
  public static final void insert(Main main, PurchaseReq purchaseReq) {
    PurchaseReqIs.insertAble(main, purchaseReq);  //Validating
    AppService.insert(main, purchaseReq);

  }

  /**
   * Update PurchaseReq by key.
   *
   * @param main
   * @param purchaseReq
   * @return PurchaseReq
   */
  public static final PurchaseReq updateByPk(Main main, PurchaseReq purchaseReq) {
    PurchaseReqIs.updateAble(main, purchaseReq); //Validating
    return (PurchaseReq) AppService.update(main, purchaseReq);
  }

  /**
   * Insert or update PurchaseReq
   *
   * @param main
   * @param purchaseReq
   */
  public static void insertOrUpdate(Main main, PurchaseReq purchaseReq) {
    if (SystemConstants.CONFIRMED.equals(purchaseReq.getPurchaseRequisitionStatusId().getId())) {
      purchaseReq.setConfirmedAt(new Date());
      purchaseReq.setConfirmedBy(main.getAppUser().getName());
      if (AccountUtil.isPrimaryVendorAccount(purchaseReq.getAccountId())) {
        purchaseReq.setRequisitionNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, purchaseReq.getAccountId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, false, purchaseReq.getFinancialYearId()));
        AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseReq.getAccountId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, false, purchaseReq.getFinancialYearId());
      } else {
        if (AccountService.INDIVIDUAL_SUPPLIER.equals(purchaseReq.getAccountId().getPurchaseChannel())) {
          purchaseReq.setRequisitionNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, purchaseReq.getAccountId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, false, purchaseReq.getFinancialYearId()));
          AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseReq.getAccountId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, false, purchaseReq.getFinancialYearId());
        } else {
          purchaseReq.setRequisitionNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, purchaseReq.getAccountId().getAccountId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, false, purchaseReq.getFinancialYearId()));
          AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseReq.getAccountId().getAccountId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, false, purchaseReq.getFinancialYearId());
        }
      }
    }
    if (purchaseReq.getId() == null) {
      insert(main, purchaseReq);
      if (AccountUtil.isPrimaryVendorAccount(purchaseReq.getAccountId())) {
        AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseReq.getAccountId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, true, purchaseReq.getFinancialYearId());
      } else {
        if (AccountService.INDIVIDUAL_SUPPLIER.equals(purchaseReq.getAccountId().getPurchaseChannel())) {
          AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseReq.getAccountId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, true, purchaseReq.getFinancialYearId());
        } else {
          AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, purchaseReq.getAccountId().getAccountId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, true, purchaseReq.getFinancialYearId());
        }
      }
    } else {
      updateByPk(main, purchaseReq);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param purchaseReq
   */
  public static void clone(Main main, PurchaseReq purchaseReq) {
    purchaseReq.setId(null); //Set to null for insert
    insert(main, purchaseReq);
  }

  /**
   * Delete PurchaseReq.
   *
   * @param main
   * @param purchaseReq
   */
  public static final void deleteByPk(Main main, PurchaseReq purchaseReq) {
    AppService.deleteSql(main, PurchaseReqItem.class, "delete from scm_purchase_req_item where purchase_requisition_id = ?", new Object[]{purchaseReq.getId()});//deleting all child
    PurchaseReqIs.deleteAble(main, purchaseReq); //Validation
    AppService.delete(main, PurchaseReq.class, purchaseReq.getId());
  }

  /**
   * Delete Array of PurchaseReq.
   *
   * @param main
   * @param purchaseReq
   */
  public static final void deleteByPkArray(Main main, PurchaseReq[] purchaseReq) {
    for (PurchaseReq e : purchaseReq) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param activeAccount
   * @param filters
   * @return
   */
  public static List<PurchaseReq> listPurchaseReqByAccount(Main main, Account activeAccount, Map<String, Object> filters) {
    //  StringBuilder whereCondition = new StringBuilder();
    SqlPage page = getPurchaseReqSqlPaged(main);
    page.cond("where scm_purchase_req.account_id = ?");
    page.param(activeAccount.getId());
    if (filters != null) {
      for (String key : filters.keySet()) {
        if (SystemConstants.CONFIRMED.equals(filters.get(key))) {
          page.cond(" and ");
          page.cond(key);
          page.cond(" in (?) ");
          page.param(filters.get(key));
//          sqlPage.param(CONFIRMED_PARTIALYY_PROCESSED);
        } else {
          page.cond(" and ");
          page.cond(key);
          page.cond(" = ? ");
          page.param(filters.get(key));
        }
      }
      // page.append(whereCondition.toString());
    }
    return AppService.listPagedJpa(main, page);
  }

  /**
   *
   * @param main
   * @param account
   * @param productId
   * @return
   */
  public static ProductQuantityInfo selectProductQuantityInfo(Main main, Account account, Product productId) {
    ProductQuantityInfo qtyInfo = (ProductQuantityInfo) AppDb.single(main.dbConnector(), ProductQuantityInfo.class, "select * from getproductsuggestedqtyforreq(?, ?)",
            new Object[]{account.getId(), productId.getId()});
    return qtyInfo;
  }
}
