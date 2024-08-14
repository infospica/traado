/*
 * @(#)ContractService.java	1.0 Wed Mar 30 12:35:28 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Account;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.Contract;
import spica.scm.domain.ContractCommiByRange;
import spica.scm.domain.ContractStatus;
import spica.scm.validate.ContractIs;
import spica.scm.util.AccountContractUtil;
import wawo.app.faces.MainView;

/**
 * ContractService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class ContractService {

  public static final int COMMISSION_VALUE_FIXED = 1;
  public static final int COMMISSION_VALUE_PERCENTAGE = 2;
  public static final int COMMISSION_BY_RANGE = 1;
  private static final int SLAB_AMOUNT = 3;
  private static final int SLAB_PERCENTAGE = 4;

  /**
   * Contract paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getContractSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_contract", Contract.class, main);
    sql.main("select scm_contract.id,scm_contract.account_id,scm_contract.contract_code,scm_contract.contract_title,scm_contract.version_no,scm_contract.valid_from,scm_contract.valid_to,scm_contract.is_factory_direct,scm_contract.deposit_amount,scm_contract.deposit_interest_rate_percent,scm_contract.deposit_interest_claim_freq_id,scm_contract.deposit_interest_claim_due_day,scm_contract.stock_holding_ratio_factor,scm_contract.stock_holding_ratio_avg_months,scm_contract.statutory_submission_freq_id,scm_contract.statutory_submisssion_due_day,scm_contract.benefit_type,scm_contract.commission_applicable_on,scm_contract.commission_value_fixed,scm_contract.commission_value_percent,scm_contract.commission_by_range,scm_contract.margin_percentage,scm_contract.vendor_reserve_percent,scm_contract.pts_margin_percent,scm_contract.ptr_margin_percent,scm_contract.cst_claimable,scm_contract.loading_expense_claimable,scm_contract.unloading_expense_claimable,scm_contract.freight_expense_claimable,scm_contract.other_expense_claimable,scm_contract.commission_claim_frequency_id,scm_contract.commission_claim_due_day,scm_contract.purchase_credit_days,scm_contract.purchase_creditdays_appli_from,scm_contract.immediate_purch_pay_disc_per,scm_contract.sales_credit_days,scm_contract.immediate_sales_pay_disc_per,scm_contract.return_exp_item_within_days,scm_contract.return_dmg_item_within_days,scm_contract.return_nonmvg_item_within_day,scm_contract.is_lr_amount_applicable,scm_contract.stock_status_report_freq_id,scm_contract.stock_status_report_due_day,scm_contract.document_copy_path,scm_contract.note,scm_contract.contract_status_id,scm_contract.created_by,scm_contract.created_at,scm_contract.modified_by,scm_contract.modified_at from scm_contract scm_contract "); //Main query
    sql.count("select count(scm_contract.id) as total from scm_contract scm_contract "); //Count query
    sql.join("left outer join scm_account scm_contractaccount_id on (scm_contractaccount_id.id = scm_contract.account_id) left outer join scm_period scm_contractdeposit_interest_claim_freq_id on (scm_contractdeposit_interest_claim_freq_id.id = scm_contract.deposit_interest_claim_freq_id) left outer join scm_period scm_contractstatutory_submission_freq_id on (scm_contractstatutory_submission_freq_id.id = scm_contract.statutory_submission_freq_id) left outer join scm_period scm_contractcommission_claim_frequency_id on (scm_contractcommission_claim_frequency_id.id = scm_contract.commission_claim_frequency_id) left outer join scm_period scm_contractstock_status_report_freq_id on (scm_contractstock_status_report_freq_id.id = scm_contract.stock_status_report_freq_id) left outer join scm_contract_status scm_contractcontract_status_id on (scm_contractcontract_status_id.id = scm_contract.contract_status_id)"); //Join Query

    sql.string(new String[]{"scm_contractaccount_id.account_code", "scm_contract.contract_code", "scm_contract.contract_title", "scm_contract.version_no", "scm_contractdeposit_interest_claim_freq_id.title", "scm_contract.stock_holding_ratio_factor", "scm_contractstatutory_submission_freq_id.title", "scm_contractcommission_claim_frequency_id.title", "scm_contractstock_status_report_freq_id.title", "scm_contract.document_copy_path", "scm_contract.note", "scm_contractcontract_status_id.title", "scm_contract.created_by", "scm_contract.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_contract.id", "scm_contract.is_factory_direct", "scm_contract.deposit_amount", "scm_contract.deposit_interest_rate_percent", "scm_contract.deposit_interest_claim_due_day", "scm_contract.stock_holding_ratio_avg_months", "scm_contract.statutory_submisssion_due_day", "scm_contract.benefit_type", "scm_contract.commission_applicable_on", "scm_contract.commission_value_fixed", "scm_contract.commission_value_percent", "scm_contract.commission_by_range", "scm_contract.margin_percentage", "scm_contract.vendor_reserve_percent", "scm_contract.pts_margin_percent", "scm_contract.ptr_margin_percent", "scm_contract.cst_claimable", "scm_contract.loading_expense_claimable", "scm_contract.unloading_expense_claimable", "scm_contract.freight_expense_claimable", "scm_contract.other_expense_claimable", "scm_contract.commission_claim_due_day", "scm_contract.purchase_credit_days", "scm_contract.immediate_purch_pay_disc_per", "scm_contract.sales_credit_days", "scm_contract.immediate_sales_pay_disc_per", "scm_contract.return_exp_item_within_days", "scm_contract.return_dmg_item_within_days", "scm_contract.return_nonmvg_item_within_day", "scm_contract.is_lr_amount_applicable", "scm_contract.stock_status_report_due_day"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_contract.valid_from", "scm_contract.valid_to", "scm_contract.purchase_creditdays_appli_from", "scm_contract.created_at", "scm_contract.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Contract.
   *
   * @param main
   * @return List of Contract
   */
  public static final List<Contract> listPaged(Main main) {
    return AppService.listPagedJpa(main, getContractSqlPaged(main));
  }

  public static final List<Contract> listPagedContractByAccount(Main main, Integer accountId, Integer activeContractId) {
    SqlPage sql = getContractSqlPaged(main);
    sql.cond("where scm_contract.account_id = ? and scm_contract.id <> ?");
    //sql.cond("order by scm_contract.version_no desc");
    sql.param(accountId);
    sql.param(activeContractId);
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of Contract based on condition
//   * @param main
//   * @return List<Contract>
//   */
//  public static final List<Contract> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getContractSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Contract by key.
   *
   * @param main
   * @param contract
   * @return Contract
   */
  public static final Contract selectByPk(Main main, Contract contract) {
    return (Contract) AppService.find(main, Contract.class, contract.getId());
  }

  /**
   * Insert Contract.
   *
   * @param main
   * @param contract
   */
  public static final void insert(Main main, Contract contract) {
    ContractIs.insertAble(main, contract);  //Validating
    AppService.insert(main, contract);
  }

  /**
   * Update Contract by key.
   *
   * @param main
   * @param contract
   * @return Contract
   */
  public static final Contract updateByPk(Main main, Contract contract) {
    ContractIs.updateAble(main, contract); //Validating
    return (Contract) AppService.update(main, contract);
  }

  /**
   * Insert or update Contract
   *
   * @param main
   * @param contract
   */
  public static void insertOrUpdate(Main main, Contract contract) {
    if (contract.getId() == null) {
      insert(main, contract);
    } else {
      if (contract.getCommissionByRange() == 1) {
        contract.setCommissionValueFixed(null);
        contract.setCommissionValuePercent(null);
      }
      updateByPk(main, contract);
      if (contract.getCommissionByRange() == 2) {
        ContractCommiByRangeService.deleteContractCommissionRangeByContractId(main, contract.getId());
      }
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param contract
   */
  public static void clone(Main main, Contract contract) {
    contract.setId(null); //Set to null for insert
    insert(main, contract);
  }

  /**
   * Delete Contract.
   *
   * @param main
   * @param contract
   */
  public static final void deleteByPk(Main main, Contract contract) {
    ContractIs.deleteAble(main, contract); //Validation
    AppService.deleteSql(main, ContractCommiByRange.class, "delete from scm_contract_comm_by_range where contract_id = ?", new Object[]{contract.getId()});
    AppService.delete(main, Contract.class, contract.getId());
  }

  /**
   * Delete Array of Contract.
   *
   * @param main
   * @param contract
   */
  public static final void deleteByPkArray(Main main, Contract[] contract) {
    for (Contract e : contract) {
      deleteByPk(main, e);
    }
  }

  public static final Contract selectContractByAccount(Main main, Integer accountId) {
    return (Contract) AppService.single(main, Contract.class, "select * from scm_contract scm_contract where scm_contract.account_id = ? order by id desc limit ?", new Object[]{accountId, 1});
  }

  public static void updateContractStatus(Main main, Integer accountId, Integer contractId) {
    main.em().execute("update scm_contract set contract_status_id = ? where account_id = ? and id <> ?", new Object[]{AccountContractUtil.CONTRACT_STATUS_DRAFT, accountId, contractId});
  }

  public static void updateConstractStatus(MainView main, Integer contractId, int contractStatusInactive) {
    main.param(contractStatusInactive);
    main.param(contractId);
    AppService.updateSql(main, Contract.class, "update scm_contract set contract_status_id = ? where id = ?", false);
  }

  public static void clonePrimaryContract(Main main, Account account) {
    Contract primaryContract = (Contract) AppService.single(main, Contract.class, "select * from scm_contract where account_id = ? and contract_status_id = ?", new Object[]{account.getAccountId().getId(), AccountContractUtil.CONTRACT_STATUS_ACTIVE});
    if (primaryContract != null) {
      Contract contract = new Contract();
      contract.setAccountId(account);
      contract.setContractTitle(primaryContract.getContractTitle());
      contract.setContractCode(primaryContract.getContractCode());
      contract.setBenefitType(primaryContract.getBenefitType());
      contract.setCommissionApplicableOn(primaryContract.getCommissionApplicableOn());
      contract.setCommissionByRange(primaryContract.getCommissionByRange());
      contract.setCommissionClaimDueDay(primaryContract.getCommissionClaimDueDay());
      contract.setCommissionClaimFrequencyId(primaryContract.getCommissionClaimFrequencyId());
      contract.setCommissionValueFixed(primaryContract.getCommissionValueFixed());
      contract.setCommissionValuePercent(primaryContract.getCommissionValuePercent());
      contract.setCommissionValueType(primaryContract.getCommissionValueType());
      contract.setCstClaimable(primaryContract.getCstClaimable());
      contract.setVersionNo(primaryContract.getVersionNo());
      contract.setVendorReservePercent(primaryContract.getVendorReservePercent());
      contract.setValidTo(primaryContract.getValidTo());
      contract.setValidFrom(primaryContract.getValidFrom());
      contract.setUnloadingExpenseClaimable(primaryContract.getUnloadingExpenseClaimable());
      contract.setStockStatusReportFreqId(primaryContract.getStockStatusReportFreqId());
      contract.setStockStatusReportDueDay(primaryContract.getStockStatusReportDueDay());
      contract.setStockHoldingRatioFactor(primaryContract.getStockHoldingRatioFactor());
      contract.setStockHoldingRatioAvgMonths(primaryContract.getStockHoldingRatioAvgMonths());
      contract.setStatutorySubmissionDueDay(primaryContract.getStatutorySubmissionDueDay());
      contract.setStatutorySubmissionFreqId(primaryContract.getStatutorySubmissionFreqId());
      contract.setSalesCreditDays(primaryContract.getSalesCreditDays());
      contract.setReturnNonmvgItemWithinDay(primaryContract.getReturnNonmvgItemWithinDay());
      contract.setReturnExpItemWithinDays(primaryContract.getReturnExpItemWithinDays());
      contract.setPurchaseCreditdaysAppliFrom(primaryContract.getPurchaseCreditdaysAppliFrom());
      contract.setPurchaseCreditDays(primaryContract.getPurchaseCreditDays());
      contract.setPtsMarginPercent(primaryContract.getPtsMarginPercent());
      contract.setPtrMarginPercent(primaryContract.getPtrMarginPercent());
      contract.setOtherExpenseClaimable(primaryContract.getOtherExpenseClaimable());
      contract.setNote(primaryContract.getNote());
      contract.setMarginPercentage(primaryContract.getMarginPercentage());
      contract.setLoadingExpenseClaimable(primaryContract.getLoadingExpenseClaimable());
      contract.setIsFactoryDirect(primaryContract.getIsFactoryDirect());
      contract.setImmediateSalesPayDiscPer(primaryContract.getImmediateSalesPayDiscPer());
      contract.setImmediatePurchPayDiscPer(primaryContract.getImmediatePurchPayDiscPer());
      contract.setFreightExpenseClaimable(primaryContract.getFreightExpenseClaimable());
      contract.setDocumentCopyPath(primaryContract.getDocumentCopyPath());
      contract.setDepositInterestRatePercent(primaryContract.getDepositInterestRatePercent());
      contract.setDepositInterestClaimFreqId(primaryContract.getDepositInterestClaimFreqId());
      contract.setDepositInterestClaimDueDay(primaryContract.getDepositInterestClaimDueDay());
      contract.setDepositAmount(primaryContract.getDepositAmount());
      ContractStatus sc = new ContractStatus();
      sc.setId(AccountContractUtil.CONTRACT_STATUS_DRAFT);
      contract.setContractStatusId(sc);
      insert(main, contract);
    }
  }
}
