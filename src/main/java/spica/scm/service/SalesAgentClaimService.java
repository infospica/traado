/*
 * @(#)SalesAgentClaimService.java	1.0 Fri May 19 11:03:21 IST 2017
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.scm.domain.SalesAgentContract;
import spica.scm.domain.SalesAgentContractComm;
import spica.scm.domain.SalesInvoice;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesAgentClaim;
import spica.scm.validate.SalesAgentClaimIs;
import spica.sys.UserRuntimeView;
import spica.scm.common.ClaimObjects;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.SalesAgentClaimDetail;
import spica.sys.SystemConstants;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;

/**
 * SalesAgentClaimService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri May 19 11:03:21 IST 2017
 */
public abstract class SalesAgentClaimService {

  public static final int DRAFT = 2;
  public static final int CONFIRMED = 1;
  public static final String DRAFT_LABEL = "Draft";
  public static final String CONFIRMED_LABEL = "Confirmed";

  /**
   * SalesAgentClaim paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesAgentClaimSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_agent_claim", SalesAgentClaim.class, main);
    sql.main("select scm_sales_agent_claim.id,scm_sales_agent_claim.sales_agent_id,scm_sales_agent_claim.invoice_amount_total,scm_sales_agent_claim.claim_amount_total,scm_sales_agent_claim.approved_amount_total,scm_sales_agent_claim.created_by,scm_sales_agent_claim.modified_by,scm_sales_agent_claim.created_at,scm_sales_agent_claim.modified_at from scm_sales_agent_claim scm_sales_agent_claim "); //Main query
    sql.count("select count(scm_sales_agent_claim.id) as total from scm_sales_agent_claim scm_sales_agent_claim "); //Count query
    sql.join("left outer join scm_company scm_company on (scm_company.id = scm_sales_agent_claim.company_id) left outer join scm_user_profile scm_user_profile on (scm_user_profile.id = scm_sales_agent_claim.sales_agent_id)"); //Join Query
    sql.orderBy("scm_sales_agent_claim.id desc");
    // sql.string(new String[]{"scm_sales_agent_claimvendor_id.vendor_name", "scm_sales_agent_claimcompany_id.company_name", "scm_sales_agent_claim.vendor_addrees", "scm_sales_agent_claim.debit_note_no", "scm_sales_agent_claimstatus_id.title", "scm_sales_agent_claim.created_by", "scm_sales_agent_claim.modified_by"}); //String search or sort fields
    // sql.number(new String[]{"scm_sales_agent_claim.id", "scm_sales_agent_claim.claim_month", "scm_sales_agent_claim.clain_year", "scm_sales_agent_claim.sales_bv", "scm_sales_agent_claim.sales_gv", "scm_sales_agent_claim.sales_return", "scm_sales_agent_claim.rate_difference", "scm_sales_agent_claim.net_sales", "scm_sales_agent_claim.commission_percent", "scm_sales_agent_claim.commission", "scm_sales_agent_claim.service_tax_percent", "scm_sales_agent_claim.service_tax", "scm_sales_agent_claim.tds_percent", "scm_sales_agent_claim.tds", "scm_sales_agent_claim.stationary", "scm_sales_agent_claim.commission_claim", "scm_sales_agent_claim.kvat", "scm_sales_agent_claim.loading_unloading", "scm_sales_agent_claim.other", "scm_sales_agent_claim.total_claim"}); //Numeric search or sort fields
    // sql.date(new String[]{"scm_sales_agent_claim.created_at", "scm_sales_agent_claim.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesAgentClaim.
   *
   * @param main
   * @return List of SalesAgentClaim
   */
  public static final List<SalesAgentClaim> listPaged(Main main, SalesAgent salesAgent, int selectedYear, int selectedMonth) {

    main.clear();
    SqlPage sql = getSalesAgentClaimSqlPaged(main);
    sql.cond("where scm_sales_agent_claim.company_id=? and date_part('year',scm_sales_agent_claim.created_at)=? and date_part('month',scm_sales_agent_claim.created_at)=?");
    main.param(UserRuntimeView.instance().getCompany().getId());
    main.param(selectedYear);
    main.param(selectedMonth);
    if (salesAgent != null) {
      sql.cond("and scm_sales_agent_claim.sales_agent_id=?");
      main.param(salesAgent.getId());
    }
    return AppService.listPagedJpa(main, sql);
  }

  /**
   * Select SalesAgentClaim by key.
   *
   * @param main
   * @param scmSalesAgentClaim
   * @return SalesAgentClaim
   */
  public static final SalesAgentClaim selectByPk(Main main, SalesAgentClaim scmSalesAgentClaim) {
    return (SalesAgentClaim) AppService.find(main, SalesAgentClaim.class, scmSalesAgentClaim.getId());
  }

  /**
   * Insert SalesAgentClaim.
   *
   * @param main
   * @param scmSalesAgentClaim
   */
  public static final void insert(Main main, SalesAgentClaim scmSalesAgentClaim) {
    SalesAgentClaimIs.insertAble(main, scmSalesAgentClaim);  //Validating
    AppService.insert(main, scmSalesAgentClaim);

  }

  /**
   * Update SalesAgentClaim by key.
   *
   * @param main
   * @param scmSalesAgentClaim
   * @return SalesAgentClaim
   */
  public static final SalesAgentClaim updateByPk(Main main, SalesAgentClaim scmSalesAgentClaim) {
    SalesAgentClaimIs.updateAble(main, scmSalesAgentClaim); //Validating
    return (SalesAgentClaim) AppService.update(main, scmSalesAgentClaim);
  }

  /**
   * Insert or update SalesAgentClaim
   *
   * @param main
   * @param scmSalesAgentClaim
   */
  public static void insertOrUpdate(Main main, SalesAgentClaim scmSalesAgentClaim) {
    if (scmSalesAgentClaim.getId() == null) {
      insert(main, scmSalesAgentClaim);
    } else {
      updateByPk(main, scmSalesAgentClaim);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param scmSalesAgentClaim
   */
  public static void clone(Main main, SalesAgentClaim scmSalesAgentClaim) {
    scmSalesAgentClaim.setId(null); //Set to null for insert
    insert(main, scmSalesAgentClaim);
  }

  /**
   * Delete SalesAgentClaim.
   *
   * @param main
   * @param scmSalesAgentClaim
   */
  public static final void deleteByPk(Main main, SalesAgentClaim scmSalesAgentClaim) {
    SalesAgentClaimIs.deleteAble(main, scmSalesAgentClaim); //Validation
    AppService.delete(main, SalesAgentClaim.class, scmSalesAgentClaim.getId());
  }

  /**
   * Delete Array of SalesAgentClaim.
   *
   * @param main
   * @param scmSalesAgentClaim
   */
  public static final void deleteByPkArray(Main main, SalesAgentClaim[] scmSalesAgentClaim) {
    for (SalesAgentClaim e : scmSalesAgentClaim) {
      deleteByPk(main, e);
    }
  }

//  public static SalesAgentClaim selectClaimByVendor(Main main, Account account, SalesAgentClaim vc) {
//
//    Object[] salesInvoiceItem = (Object[]) AppService.single(main, null, "SELECT sum(t1.value_sale::float),sum(t1.value_igst::float) FROM scm_sales_invoice_item t1, scm_sales_invoice t2,scm_product_detail t3 \n"
//            + "where date_part('month',t2.invoice_date)=? \n"
//            + "and date_part('year',t2.invoice_date)=? and t2.id=t1.sales_invoice_id\n"
//            + "and t3.id=t1.product_detail_id and t3.account_id=?", new Object[]{vc.getClaimMonth(), vc.getClainYear(), account.getId()});
//    //AccountGroupDetail agd = new AccountGroupDetail();
//    // agd.setAccountGroupId(defaultAccountGroup);
//    //  agd.setAccountId(account);
//    //  insert(main, agd);
//    // SalesAgentClaim vc = new SalesAgentClaim();
//
//    vc.setSalesBv((Double) salesInvoiceItem[0]);
//    vc.setKvat((Double) salesInvoiceItem[1]);
//    return vc;
//
//  }
  public static List<SalesAgentClaimDetail> getCommision(Main main, Integer userProfileId, int monthNumber) {
    List<SalesAgentClaimDetail> claimObjectsList = new ArrayList<>();
    SalesAgentContract contract = (SalesAgentContract) AppService.single(main, SalesAgentContract.class, "select * from scm_sales_agent_contract where user_profile_id=?", new Object[]{userProfileId});
    if (contract == null) {
      throw new UserMessageException("missing.agent.contract");
    }
    String sql = "";
    if (contract.getCommissionApplicableOn().equals(SystemConstants.COMMISSION_APPLICABLE_ON_SALES) && contract.getCommissionOn().equals(SystemConstants.COMMISSION_ON_GOODS_VALUE)) {
//      sql = "select t1.goods_value::float invoice_amount,t2.invoice_no invoice_no,t1.sales_invoice_id sales_invoice_id from scm_sales_invoice_value_account_wise t1, scm_sales_invoice t2\n"
//              + "where t2.id=t1.sales_invoice_id and t2.company_sales_agent_person_profile_id=? and t1.parent_id is null and date_part('month',t1.processed_date)<?";
      sql = "select t1.value_before_tax as invoice_amount,t1.document_no as invoice_no,t1.entity_id as sales_invoice_id "
              + "from fin_trade_outstanding t1 where t1.entity_id in(select id from scm_sales_invoice where company_sales_agent_person_profile_id = ?) "
              + "and t1.entity_type_id = ? and t1.parent_id is null and date_part('month',t1.entry_date) = ?";

    } else if (contract.getCommissionApplicableOn().equals(SystemConstants.COMMISSION_APPLICABLE_ON_SALES) && contract.getCommissionOn().equals(SystemConstants.COMMISSION_ON_BILL_AMOUNT)) {
//      sql = "select t1.invoice_amount::float invoice_amount,t2.invoice_no invoice_no,t1.sales_invoice_id sales_invoice_id from scm_sales_invoice_value_account_wise t1, scm_sales_invoice t2 "
//              + "where t2.id=t1.sales_invoice_id and t2.company_sales_agent_person_profile_id=? and t1.parent_id is null and date_part('month',t1.processed_date)<?";      
      sql = "select t1.value_after_tax as invoice_amount,t1.document_no as invoice_no,t1.entity_id as sales_invoice_id "
              + "from fin_trade_outstanding t1 where t1.entity_id in(select id from scm_sales_invoice where company_sales_agent_person_profile_id = ?) "
              + "and t1.entity_type_id = ? and t1.parent_id is null and date_part('month',t1.entry_date) = ?";

    } else if (contract.getCommissionApplicableOn().equals(SystemConstants.COMMISSION_APPLICABLE_ON_COLLECTION) && contract.getCommissionOn().equals(SystemConstants.COMMISSION_ON_GOODS_VALUE)) {
//      sql = "select sum(t1.goods_value::float) invoice_amount,t2.invoice_no invoice_no,t1.sales_invoice_id sales_invoice_id from scm_sales_invoice_value_account_wise t1, scm_sales_invoice t2\n"
//              + "where t2.id=t1.sales_invoice_id and t2.company_sales_agent_person_profile_id=? and t1.parent_id is not null and date_part('month',t1.processed_date)<? group by t2.invoice_no,t1.sales_invoice_id";
      sql = "select sum(t1.value_before_tax) as invoice_amount,t1.document_no as invoice_no,t1.entity_id as sales_invoice_id "
              + "from fin_trade_outstanding t1 where t1.entity_id in(select id from scm_sales_invoice where company_sales_agent_person_profile_id = ?) "
              + "and t1.entity_type_id = ? and t1.parent_id is null and date_part('month',t1.entry_date) = ? group by t1.document_no,t1.entity_id";

    } else if (contract.getCommissionApplicableOn().equals(SystemConstants.COMMISSION_APPLICABLE_ON_COLLECTION) && contract.getCommissionOn().equals(SystemConstants.COMMISSION_ON_GOODS_VALUE)) {
//      sql = "select sum(t1.invoice_amount::float) invoice_amount,t2.invoice_no invoice_no,t1.sales_invoice_id sales_invoice_id from scm_sales_invoice_value_account_wise t1, scm_sales_invoice t2\n"
//              + "where t2.id=t1.sales_invoice_id and t2.company_sales_agent_person_profile_id=? and t1.parent_id is not null and date_part('month',t1.processed_date)<? group by t2.invoice_no,t1.sales_invoice_id";
      sql = "select sum(t1.value_after_tax) as invoice_amount,t1.document_no as invoice_no,t1.entity_id as sales_invoice_id "
              + "from fin_trade_outstanding t1 where t1.entity_id in(select id from scm_sales_invoice where company_sales_agent_person_profile_id = ?) "
              + "and t1.entity_type_id = ? and t1.parent_id is null and date_part('month',t1.entry_date) = ? group by t1.document_no,t1.entity_id";

    }
    List<ClaimObjects> commissionObject = (List<ClaimObjects>) AppDb.getList(main.dbConnector(), ClaimObjects.class, sql, new Object[]{userProfileId, AccountingConstant.ACC_ENTITY_SALES.getId(), monthNumber});

    for (ClaimObjects claimList : commissionObject) {

      Double commission = 0.0;
      if (!contract.getCommissionByRange().equals(SystemConstants.COMMISSION_BY_RANGE)) { // not range
        commission = (contract.getCommissionType().equals(SystemConstants.COMMISSION_AMOUNT_AS_FIXED)) ? contract.getCommissionValueFixed() : claimList.getInvoiceAmount() * contract.getCommissionValuePercentage() / 100;
      } else { // range
        List<SalesAgentContractComm> salesAgentContractCommList = AppService.list(main, SalesAgentContractComm.class, "select * from scm_sales_agent_contract_comm where sales_agent_contract_id=?", new Object[]{contract.getId()});
        for (SalesAgentContractComm list : salesAgentContractCommList) {
          if (claimList.getInvoiceAmount() >= list.getRangeFrom() && claimList.getInvoiceAmount() <= list.getRangeTo()) {
            commission = (contract.getCommissionType().equals(SystemConstants.COMMISSION_AMOUNT_AS_FIXED)) ? list.getValueFixed() : claimList.getInvoiceAmount() * list.getValuePercentage() / 100;
            break;
          }
        }
      }
      claimObjectsList.add(new SalesAgentClaimDetail(new SalesInvoice(claimList.getSalesInvoiceId(), claimList.getInvoiceNo()), claimList.getInvoiceAmount(), commission, commission));
    }
    return claimObjectsList;
  }
}
