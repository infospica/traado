/*
 * @(#)SalesReqService.java	1.0 Tue May 10 17:16:26 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Customer;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesReq;
import spica.scm.validate.SalesReqIs;
import spica.sys.SystemConstants;

/**
 * SalesReqService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 10 17:16:26 IST 2016
 */
public abstract class SalesReqService {

  public static Integer SALES_REQ_SCHEME_REQUIRED = 1;
  public static Integer SALES_REQ_SCHEME_NOT_REQUIRED = 0;

  /**
   * SalesReq paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesReqSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_req", SalesReq.class, main);
    sql.main("select scm_sales_req.id,scm_sales_req.company_id,scm_sales_req.financial_year_id,scm_sales_req.sales_account_id,scm_sales_req.requisition_no,scm_sales_req.requisition_date,scm_sales_req.purchase_order_no,"
            + "scm_sales_req.purchase_order_date,scm_sales_req.commodity_expected_date,scm_sales_req.initiated_by,scm_sales_req.vendor_sales_agent_id,"
            + "scm_sales_req.document_copy_path,scm_sales_req.created_by,scm_sales_req.sales_req_status_id,scm_sales_req.modified_by,scm_sales_req.created_at,scm_sales_req.modified_at,scm_sales_req.approved_by,scm_sales_req.approved_at,scm_sales_req.disc_val_prod_piece_fixed,scm_sales_req.disc_val_prod_piece_percent,scm_sales_req.note from scm_sales_req scm_sales_req "); //Main query
    sql.count("select count(scm_sales_req.id) as total from scm_sales_req scm_sales_req "); //Count query
    sql.join("left outer join scm_sales_account scm_sales_reqsales_account_id on (scm_sales_reqsales_account_id.id = scm_sales_req.sales_account_id) left outer join scm_sales_agent_contract scm_sales_reqvendor_sales_agent_id on (scm_sales_reqvendor_sales_agent_id.id = scm_sales_req.vendor_sales_agent_id) left outer join scm_sales_req_status scm_sales_reqsales_req_status_id on (scm_sales_reqsales_req_status_id.id = scm_sales_req.sales_req_status_id)"); //Join Query

    sql.string(new String[]{"scm_sales_req.requisition_no", "scm_sales_req.purchase_order_no", "scm_sales_req.initiated_by", "scm_sales_req.document_copy_path", "scm_sales_req.created_by", "scm_sales_reqsales_req_status_id.title", "scm_sales_req.modified_by", "scm_sales_req.approved_by", "scm_sales_req.note"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_req.id", "scm_sales_reqsales_account_id.sales_credit_amount", "scm_sales_req.disc_val_prod_piece_fixed", "scm_sales_req.disc_val_prod_piece_percent"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_req.requisition_date", "scm_sales_req.purchase_order_date", "scm_sales_req.commodity_expected_date", "scm_sales_reqvendor_sales_agent_id.valid_from", "scm_sales_req.created_at", "scm_sales_req.modified_at", "scm_sales_req.approved_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesReq.
   *
   * @param main
   * @param accountGroupId
   * @param filter
   * @return List of SalesReq
   */
  public static final List<SalesReq> listPaged(Main main, AccountGroup accountGroupId, Integer filter) {
    SqlPage sql = getSalesReqSqlPaged(main);
    if (accountGroupId != null) {
      main.clear();
      main.param(accountGroupId.getId());
      if (filter != null) {
        main.param(filter);
        sql.cond("where scm_sales_req.sales_account_id in (select id from scm_sales_account where account_group_id = ?) and scm_sales_req.sales_req_status_id = ? ");
      } else {
        sql.cond("where scm_sales_req.sales_account_id in (select id from scm_sales_account where account_group_id = ?)");
      }
      return AppService.listPagedJpa(main, sql);
    } else {
      return AppService.listPagedJpa(main, getSalesReqSqlPaged(main));
    }
  }

//  /**
//   * Return list of SalesReq based on condition
//   * @param main
//   * @return List<SalesReq>
//   */
//  public static final List<SalesReq> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesReqSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesReq by key.
   *
   * @param main
   * @param salesReq
   * @return SalesReq
   */
  public static final SalesReq selectByPk(Main main, SalesReq salesReq) {
    return (SalesReq) AppService.find(main, SalesReq.class, salesReq.getId());
  }

  /**
   * Insert SalesReq.
   *
   * @param main
   * @param salesReq
   */
  public static final void insert(Main main, SalesReq salesReq) {
    SalesReqIs.insertAble(main, salesReq);  //Validating
    AppService.insert(main, salesReq);

  }

  /**
   * Update SalesReq by key.
   *
   * @param main
   * @param salesReq
   * @return SalesReq
   */
  public static final SalesReq updateByPk(Main main, SalesReq salesReq) {
    SalesReqIs.updateAble(main, salesReq); //Validating
    return (SalesReq) AppService.update(main, salesReq);
  }

  /**
   * Insert or update SalesReq
   *
   * @param main
   * @param salesReq
   */
  public static void insertOrUpdate(Main main, SalesReq salesReq) {
    if (salesReq.getSchemeRequired() != null && salesReq.getSchemeRequired().equals(SALES_REQ_SCHEME_NOT_REQUIRED)) {
      salesReq.setDiscValProdPieceFixed(null);
      salesReq.setDiscValProdPiecePercent(null);
    }
    if (SystemConstants.CONFIRMED.equals(salesReq.getSalesReqStatusId().getId())) {
      salesReq.setRequisitionNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesReq.getSalesAccountId().getAccountGroupId(), PrefixTypeService.SALES_REQUISITION_PREFIX_ID, false, salesReq.getFinancialYearId()));
      AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesReq.getSalesAccountId().getAccountGroupId(), PrefixTypeService.SALES_REQUISITION_PREFIX_ID, false, salesReq.getFinancialYearId());
    }
    if (salesReq.getId() == null) {
      salesReq.setRequisitionNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesReq.getSalesAccountId().getAccountGroupId(), PrefixTypeService.SALES_REQUISITION_PREFIX_ID, true, salesReq.getFinancialYearId()));
      insert(main, salesReq);
      AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesReq.getSalesAccountId().getAccountGroupId(), PrefixTypeService.SALES_REQUISITION_PREFIX_ID, true, salesReq.getFinancialYearId());
    } else {
      updateByPk(main, salesReq);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesReq
   */
  public static void clone(Main main, SalesReq salesReq) {
    salesReq.setId(null); //Set to null for insert
    insert(main, salesReq);
  }

  /**
   * Delete SalesReq.
   *
   * @param main
   * @param salesReq
   */
  public static final void deleteByPk(Main main, SalesReq salesReq) {
    SalesReqIs.deleteAble(main, salesReq); //Validation
    AppService.delete(main, SalesReq.class, salesReq.getId());
  }

  /**
   * Delete Array of SalesReq.
   *
   * @param main
   * @param salesReq
   */
  public static final void deleteByPkArray(Main main, SalesReq[] salesReq) {
    for (SalesReq e : salesReq) {
      deleteByPk(main, e);
    }
  }

  /**
   * Method to sales req by its sales accounts.
   *
   * @param main
   * @param accountGroupId
   * @param customer
   * @return
   *
   */
  public static final List<SalesReq> selectSalesReqBySalesAccount(Main main, AccountGroup accountGroupId, Customer customer) {
    List<SalesReq> list = AppService.list(main, SalesReq.class,
            "select * from scm_sales_req where id in (select tab1.sales_req_id from ( "
            + "select scm_sales_req_item.sales_req_id, sum(scm_sales_req_item.qty_required) qty_required from scm_sales_req_item scm_sales_req_item "
            + "where scm_sales_req_item.sales_req_id in ( "
            + "select id from scm_sales_req where scheme_required = " + SALES_REQ_SCHEME_NOT_REQUIRED + " and sales_req_status_id in (" + SystemConstants.CONFIRMED + "," + SystemConstants.CONFIRMED_PARTIALYY_PROCESSED + ") and sales_account_id in "
            + "(select id from scm_sales_account where customer_id = ? and  account_group_id = ?)) "
            + "group by scm_sales_req_item.sales_req_id) tab1 "
            + "left join "
            + "(select scm_sales_req_item.sales_req_id, sum(scm_sales_order_item.qty_required) qty_required from scm_sales_order_item scm_sales_order_item "
            + "inner join scm_sales_req_item scm_sales_req_item on scm_sales_order_item.sales_req_item_id = scm_sales_req_item.id "
            + "inner join scm_sales_req scm_sales_req on scm_sales_req.id = scm_sales_req_item.sales_req_id "
            + "where scm_sales_req.scheme_required = " + SALES_REQ_SCHEME_NOT_REQUIRED + " and scm_sales_req.sales_req_status_id in (" + SystemConstants.CONFIRMED + "," + SystemConstants.CONFIRMED_PARTIALYY_PROCESSED + ") and scm_sales_req.sales_account_id in "
            + "(select id from scm_sales_account where customer_id =  ? and  account_group_id = ?) "
            + "group by scm_sales_req_item.sales_req_id) tab2 "
            + "on tab1.sales_req_id = tab2.sales_req_id "
            + "group by tab1.sales_req_id "
            + "having sum(tab1.qty_required - coalesce(tab2.qty_required,0)) > 0) order by id asc",
            new Object[]{customer.getId(), accountGroupId.getId(), customer.getId(), accountGroupId.getId()});

    return list;
  }

  /**
   * Method to update the status of Sales Req Status.
   *
   * @param main
   * @param salesReq
   * @param salesReqStatusId
   */
  public static final void updateSalesReqStatus(Main main, SalesReq salesReq, Integer salesReqStatusId) {
    main.clear();
    main.param(salesReqStatusId);
    main.param(salesReq.getId());
    AppService.updateSql(main, SalesReq.class, "update scm_sales_req set sales_req_status_id = ? where id = ?", false);
    main.clear();
  }

  public static boolean isRequestedProductProcessed(Main main, SalesReq salesReq) {
    long count = AppService.count(main, "select sum((coalesce(tab1.qty_required,0) - coalesce(tab2.qty_required,0)) + (coalesce(tab1.qty_free_suggested,0) - coalesce(tab2.qty_free_suggested,0))) as qty_required "
            + "from (select * from scm_sales_req_item where sales_req_id = ?) tab1 "
            + "left outer join "
            + "(select sales_req_item_id, sum(coalesce(scm_sales_order_item.qty_required,0)) as qty_required, sum(coalesce(scm_sales_order_item.qty_free_suggested,0)) as qty_free_suggested "
            + "from scm_sales_order_item scm_sales_order_item "
            + "inner join scm_sales_order scm_sales_order on scm_sales_order.id = scm_sales_order_item.sales_order_id "
            + "where scm_sales_order_item.sales_req_item_id in (select id from scm_sales_req_item where sales_req_id = ?) "
            + "group by scm_sales_order_item.sales_req_item_id) tab2 "
            + "on tab1.id = tab2.sales_req_item_id", new Object[]{salesReq.getId(), salesReq.getId()});
    return (count <= 0);
  }
}
