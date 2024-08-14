/*
 * @(#)VendorClaimService.java	1.0 Mon Jan 22 10:40:45 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.TaxCode;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.fin.domain.VendorClaim;
import spica.fin.domain.VendorClaimDetail;
import spica.fin.domain.VendorClaimHeader;
import spica.reports.model.FilterParameters;
import spica.scm.common.VendorClaimObjectDetail;
import spica.scm.domain.AccountGroupDocPrefix;
import spica.scm.domain.Company;
import spica.scm.domain.Contract;
import spica.scm.domain.ContractCommiByRange;
import spica.scm.domain.Platform;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.PrefixTypeService;
import spica.sys.SystemConstants;
import spica.sys.common.DocumentNumberGen;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;

/**
 * VendorClaimService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jan 22 10:40:45 IST 2018
 */
public abstract class VendorClaimService {

  /**
   * VendorClaim paginated query.
   *
   * @param main
   * @return SqlPage
   */
  public static final Integer DRAFT = 1;
  public static final Integer CONFIRM = 3;

  private static SqlPage getVendorClaimSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_vendor_claim", VendorClaim.class, main);
    sql.main("select scm_vendor_claim.service,scm_vendor_claim.id,scm_vendor_claim.vendor_id,scm_vendor_claim.company_id,scm_vendor_claim.claim_month,scm_vendor_claim.claim_year,scm_vendor_claim.status_id,scm_vendor_claim.created_by,scm_vendor_claim.modified_by,scm_vendor_claim.created_at,scm_vendor_claim.modified_at,scm_vendor_claim.commission_claim,scm_vendor_claim.gst_claim,scm_vendor_claim.invoice_number from scm_vendor_claim scm_vendor_claim "); //Main query
    sql.count("select count(scm_vendor_claim.id) as total from scm_vendor_claim scm_vendor_claim "); //Count query
    sql.join("left outer join scm_vendor scm_vendor_claimvendor_id on (scm_vendor_claimvendor_id.id = scm_vendor_claim.vendor_id) left outer join scm_company scm_vendor_claimcompany_id on (scm_vendor_claimcompany_id.id = scm_vendor_claim.company_id) left outer join scm_vendor_claim_status scm_vendor_claimstatus_id on (scm_vendor_claimstatus_id.id = scm_vendor_claim.status_id)"); //Join Query

    sql.string(new String[]{"scm_vendor_claimvendor_id.vendor_name", "scm_vendor_claimcompany_id.company_name", "scm_vendor_claimstatus_id.title", "scm_vendor_claim.created_by", "scm_vendor_claim.modified_by", "scm_vendor_claim.invoice_number"}); //String search or sort fields
    sql.number(new String[]{"scm_vendor_claim.id", "scm_vendor_claim.claim_month", "scm_vendor_claim.claim_year,scm_vendor_claim.commission_claim,scm_vendor_claim.gst_claim"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_vendor_claim.created_at", "scm_vendor_claim.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of VendorClaim.
   *
   * @param main
   * @return List of VendorClaim
   */
  public static final List<VendorClaim> listPaged(Main main, Integer vendorId, Integer companyId) {
    SqlPage sql = getVendorClaimSqlPaged(main);
    main.clear();
    sql.cond(" where scm_vendor_claim.company_id = ? AND scm_vendor_claim.vendor_id = ? ");
    main.param(companyId);
    main.param(vendorId);
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of VendorClaim based on condition
//   * @param main
//   * @return List<VendorClaim>
//   */
//  public static final List<VendorClaim> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getVendorClaimSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  public static final List<VendorClaim> selectProcessedClaim(Main main, VendorClaim vc) {
    main.clear();
    SqlPage sql = getVendorClaimSqlPaged(main);
    sql.cond("where scm_vendor_claim.vendor_id=? and scm_vendor_claim.company_id=?");
    main.param(vc.getAccountId().getVendorId().getId());
    main.param(vc.getCompanyId().getId());
    // main.param(SystemRuntimeConfig.CLAIM_PROCESSED.getId());
    return AppService.listAllJpa(main, sql);
  }

  /**
   * Select VendorClaim by key.
   *
   * @param main
   * @param vendorClaim
   * @return VendorClaim
   */
  public static final VendorClaim selectByPk(Main main, VendorClaim vendorClaim) {
    return (VendorClaim) AppService.find(main, VendorClaim.class, vendorClaim.getId());
  }

  public static final List<VendorClaimDetail> selecteByClaim(Main main, VendorClaim vendorClaim) {

    return AppService.list(main, VendorClaimDetail.class, "select * from scm_vendor_claim_detail where vendor_claim_id=? order by id", new Object[]{vendorClaim.getId()});

  }

  /**
   * Insert VendorClaim.
   *
   * @param main
   * @param vendorClaim
   */
  public static final void insert(Main main, VendorClaim vendorClaim) {
    insertAble(main, vendorClaim);  //Validating
//    AccountingTransaction accountingTransaction = new AccountingTransaction();
//    accountingTransaction.setVoucherTypeId(SystemRuntimeConfig.VOUCHER_TYPE_DEBIT_NOTE);
//TODO    vendorClaim.setDebitNoteNo(AccountingPrefixService.getPrefixOnSave(main, accountingTransaction));
    AppService.insert(main, vendorClaim);

  }

  /**
   * Update VendorClaim by key.
   *
   * @param main
   * @param vendorClaim
   * @return VendorClaim
   */
  public static final VendorClaim updateByPk(Main main, VendorClaim vendorClaim) {
    updateAble(main, vendorClaim); //Validating
    updateInvoiceNumber(main, vendorClaim, vendorClaim.getStatusId().intValue() == SystemConstants.DRAFT);
    updateDebitCreditNoteAccountGroupDocPrefix(main, vendorClaim, vendorClaim.getStatusId().intValue() == SystemConstants.DRAFT);
    return (VendorClaim) AppService.update(main, vendorClaim);
  }

  /**
   * Insert or update VendorClaim
   *
   * @param main
   * @param vendorClaim
   */
  public static void insertOrUpdate(Main main, VendorClaim vendorClaim, List<VendorClaimDetail> claimDetailList) {
    if (vendorClaim.getId() == null) {
      insert(main, vendorClaim);
    } else {
      updateByPk(main, vendorClaim);
    }
    insertOrUpdateClaimDetails(main, vendorClaim, claimDetailList);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param vendorClaim
   */
  public static void clone(Main main, VendorClaim vendorClaim) {
    vendorClaim.setId(null); //Set to null for insert
    insert(main, vendorClaim);
  }

  /**
   * Delete VendorClaim.
   *
   * @param main
   * @param vendorClaim
   */
  public static final void deleteByPk(Main main, VendorClaim vendorClaim) {
    deleteAble(main, vendorClaim); //Validation
    AppService.delete(main, VendorClaim.class, vendorClaim.getId());
  }

  /**
   * Delete Array of VendorClaim.
   *
   * @param main
   * @param vendorClaim
   */
  public static final void deleteByPkArray(Main main, VendorClaim[] vendorClaim) {
    for (VendorClaim e : vendorClaim) {
      AppService.deleteSql(main, VendorClaimDetail.class, "DELETE FROM scm_vendor_claim_detail WHERE vendor_claim_header_id in ( SELECT id FROM scm_vendor_claim_header WHERE vendor_claim_id = ? )", new Object[]{e.getId()});
      AppService.deleteSql(main, VendorClaimHeader.class, "DELETE FROM scm_vendor_claim_header WHERE vendor_claim_id = ? ", new Object[]{e.getId()});
      AppService.deleteSql(main, Platform.class, "DELETE FROM scm_platform WHERE vendor_claim_id = ? ", new Object[]{e.getId()});
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param scmVendorClaim
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, VendorClaim scmVendorClaim) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param scmVendorClaim
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, VendorClaim scmVendorClaim) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_vendor_claim where upper(vendor_addrees)=?", new Object[]{StringUtil.toUpperCase(scmVendorClaim.getVendorAddrees())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param scmVendorClaim
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, VendorClaim scmVendorClaim) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_vendor_claim where upper(vendor_addrees)=? and id !=?", new Object[]{StringUtil.toUpperCase(scmVendorClaim.getVendorAddrees()), scmVendorClaim.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  public static Double selectSalesAmount(Main main, Integer accountingId, Integer companyId, FilterParameters filterParameter) {
    String sql = "select sum(t1.total_amount) from fin_accounting_transaction t1 \n"
            + "where t1.ledger_debit_id = ? AND t1.voucher_type_id = ? AND t1.company_id = ? \n"
            + "AND t1.entry_date::date >= ? ::date AND  t1.entry_date::date <= ?::date  AND t1.entity_id is null";
    Double amount = AppService.countDouble(main, sql, new Object[]{accountingId, AccountingConstant.VOUCHER_TYPE_SALES.getId(), companyId, filterParameter.getFromDate(), filterParameter.getToDate()});
    return amount;

  }

  public static ContractCommiByRange selectVendorCommission(Main main, Integer accountId, Double amount) {
    String sql = "select t1.* from scm_contract_comm_by_range t1 left join scm_contract t2 on t2.id = t1.contract_id where t2.account_id =? "
            + " AND t1.range_from <= ? AND t1.range_to >= ? ";

    return (ContractCommiByRange) AppService.single(main, ContractCommiByRange.class,
            sql, new Object[]{accountId, amount, amount});

  }

  public static Contract selectVendorContract(Main main, Integer accountId) {
    String sql = "select * from scm_contract where account_id = ? AND contract_status_id = 1";
    return (Contract) AppService.single(main, Contract.class, sql, new Object[]{accountId});
  }

  public static List selectVendorClaimExpense(Main main, Integer companyId, Integer ledgerId, FilterParameters filterParameter) {
    String sql = "select sum(t1.total_amount) as amount ,t1.ledger_debit_id as id,t2.title,t1.voucher_type_id from fin_accounting_transaction t1 \n"
            + "left join fin_accounting_ledger t2 on t2.id = t1.ledger_debit_id\n"
            + "where t1.ledger_credit_id = ? AND t1.voucher_type_id = ? AND t1.company_id = ? AND t1.entry_date::date >=?::date AND  t1.entry_date::date <=?::date \n"
            + " AND t1.entity_id is null \n"
            + "GROUP BY t1.ledger_debit_id,t2.title,t1.voucher_type_id";

    return AppDb.getList(main.dbConnector(), VendorClaimObjectDetail.class,
            sql, new Object[]{ledgerId, AccountingConstant.VOUCHER_TYPE_EXPENSE.getId(), companyId, filterParameter.getFromDate(), filterParameter.getToDate()});
  }

  public static void insertOrUpdateClaimDetails(Main main, VendorClaim vendorClaim, List<VendorClaimDetail> claimDetailList) {
    VendorClaimHeader claimHeader = null;
    VendorClaimHeader header = null;
    for (VendorClaimDetail detail : claimDetailList) {
      claimHeader = detail.getVendorClaimHeaderId();
      claimHeader.setVendorClaimId(vendorClaim);
      if (claimHeader != null && detail.getChildRow() == 0) {
        header = new VendorClaimHeader();
        insertOrUpdateClaimHeader(main, claimHeader);
        header = claimHeader;
      }
      detail.setVendorClaimHeaderId(header);
      AppService.deleteSql(main, VendorClaimDetail.class, "delete from scm_vendor_claim_detail where vendor_claim_header_id = ? ", new Object[]{header.getId()});
      detail.setId(null);
      insertOrUpdateClaimDetail(main, detail);
    }

  }

  public static List<VendorClaimDetail> selectVendorClaimDetailByClaim(Main main, VendorClaim claim) {
    String sql = "select t1.* from scm_vendor_claim_detail t1,scm_vendor_claim_header t2 \n"
            + "where  t1.vendor_claim_header_id = t2.id AND t2.vendor_claim_id = ? order by t1.id asc";
    return AppService.list(main, VendorClaimDetail.class, sql, new Object[]{claim.getId()});
  }

  public static List<AccountingLedger> selectInputTax(Main main, Company company) {
    String sql = "select * from fin_accounting_ledger where accounting_group_id in(?,?,?) and company_id = ? and accounting_entity_type_id = ? and ledger_code = ? order by title asc";
    main.clear();
    main.param(AccountingConstant.ACC_HEAD_TAX_IGST.getId());
    main.param(AccountingConstant.ACC_HEAD_TAX_CGST.getId());
    main.param(AccountingConstant.ACC_HEAD_TAX_SGST.getId());
    main.param(company.getId());
    main.param(AccountingConstant.ACC_ENTITY_TAX.getId());
    main.param(AccountingConstant.LEDGER_CODE_TAX_INPUT);
    return AppService.list(main, AccountingLedger.class, sql, main.getParamData().toArray());

  }

  public static List<AccountingLedger> selectOutputTax(Main main, Company company) {
    String sql = "select * from fin_accounting_ledger where accounting_group_id in(?,?,?) and company_id = ? and accounting_entity_type_id = ? and ledger_code = ? order by title asc";
    main.clear();
    main.param(AccountingConstant.ACC_HEAD_TAX_IGST.getId());
    main.param(AccountingConstant.ACC_HEAD_TAX_CGST.getId());
    main.param(AccountingConstant.ACC_HEAD_TAX_SGST.getId());
    main.param(company.getId());
    main.param(AccountingConstant.ACC_ENTITY_TAX.getId());
    main.param(AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
    return AppService.list(main, AccountingLedger.class, sql, main.getParamData().toArray());

  }

  public static List<TaxCode> selectTaxByCompany(Main main, Integer companyId) {
    String sql = "select id,code,rate_percentage,parent_id from scm_tax_code where status_id = 1 and tax_type is not null";
    List<TaxCode> taxList = AppService.list(main, TaxCode.class, sql, new Object[]{companyId});
    return taxList;
  }

  public static final void insertOrUpdateClaimHeader(Main main, VendorClaimHeader claimHeader) {
    if (claimHeader != null) {
      if (claimHeader.getId() == null) {
        AppService.insert(main, claimHeader);
      } else {
        AppService.update(main, claimHeader);
      }
    }
  }

  public static final void insertOrUpdateClaimDetail(Main main, VendorClaimDetail claimDetail) {
    if (claimDetail != null) {
      if (claimDetail.getId() == null) {
        AppService.insert(main, claimDetail);
      } else {
        AppService.update(main, claimDetail);
      }
    }
  }

  public static final List<AccountingLedger> getExpenseList(Main main, Integer companyId) {
    String sql = "select * from fin_accounting_ledger where accounting_group_id = ? and company_id = ?";
    return AppService.list(main, AccountingLedger.class, sql, new Object[]{AccountingConstant.GROUP_INDIRECT_EXPENSE.getId(), companyId});
  }

  public static List<TaxCode> getIGSTTaxCode(Main main) {
    String sql = "select * from scm_tax_code where tax_head_id = ? and parent_id is null order by code asc";
    main.clear();
    main.param(AccountingConstant.TAX_HEAD_GST.getId());
    return AppService.list(main, TaxCode.class, sql, main.getParamData().toArray());
  }

  public static List<TaxCode> getTdsTaxCode(Main main, Integer companyId) {
    String sql = "select * from scm_tax_code where tax_head_id = ? and company_id=?";
    main.clear();
    main.param(AccountingConstant.TAX_HEAD_TDS.getId());
    main.param(companyId);
    return AppService.list(main, TaxCode.class, sql, main.getParamData().toArray());
  }

  public static List<TaxCode> getCGSTTaxCode(Main main) {
    String sql = "select * from scm_tax_code where tax_head_id = ? and tax_type = ? ";
    main.clear();
    main.param(AccountingConstant.TAX_HEAD_GST.getId());
    main.param(AccountingConstant.TAX_TYPE_CGST);
    return AppService.list(main, TaxCode.class, sql, main.getParamData().toArray());
  }

  public static List<TaxCode> getSGSTTaxCode(Main main) {
    String sql = "select * from scm_tax_code where tax_head_id = ? and tax_type = ? ";
    main.clear();
    main.param(AccountingConstant.TAX_HEAD_GST.getId());
    main.param(AccountingConstant.TAX_TYPE_SGST);
    return AppService.list(main, TaxCode.class, sql, main.getParamData().toArray());
  }

  public static List<TaxCode> getTaxCodeByParent(Main main, Integer taxCodeId) {
    String sql = "select * from scm_tax_code where parent_id = ? ";
    return AppService.list(main, TaxCode.class, sql, new Object[]{taxCodeId});
  }

  public static void resetToDraft(Main main, VendorClaim vendorClaim) {
    List<AccountingTransaction> transactionList = AccountingTransactionService.selectByVendorClaimList(main, vendorClaim);
    if (transactionList != null) {
      for (AccountingTransaction transaction : transactionList) {
        AccountingTransactionService.deleteByPk(main, transaction);
        TradeOutstandingService.deleteByEntityIdAndEntityType(main, vendorClaim.getId(), AccountingConstant.ACC_ENTITY_VENDOR_CLAIM.getId());
      }
    }
    vendorClaim.setStatusId(SystemConstants.DRAFT);
    updateByPk(main, vendorClaim);

  }

  public static void updateInvoiceNumber(Main main, VendorClaim vendorClaim, boolean draft) {
    String prefix = "";
    String draftPrefix = draft ? "DR-" : "";
    AccountGroupDocPrefix accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
            "select * from scm_account_group_doc_prefix where account_id = ? and prefix_type_id = ?", new Object[]{vendorClaim.getAccountId().getId(), PrefixTypeService.VALUE_DEBIT_NOTE_NON_TAXABLE_PREFIX_ID});

    if (accountGroupDocPrefix != null) {
      if (draft) {
        updateDebitCreditNoteAccountGroupDocPrefix(main, vendorClaim, draft);
      }
      prefix = draftPrefix + DocumentNumberGen.getPrefixLiteral(accountGroupDocPrefix, vendorClaim.getCompanyId().getCurrentFinancialYear().getStartDate());
    }
    vendorClaim.setInvoiceNumber(prefix);
  }

  public static void updateDebitCreditNoteAccountGroupDocPrefix(Main main, VendorClaim vendorClaim, boolean draftPrefix) {
    if (vendorClaim != null) {
      AccountGroupDocPrefix accountGroupDocPrefix = (AccountGroupDocPrefix) AppService.single(main, AccountGroupDocPrefix.class,
              "select * from scm_account_group_doc_prefix where account_id = ? and prefix_type_id = ?", new Object[]{vendorClaim.getAccountId().getId(), PrefixTypeService.VALUE_DEBIT_NOTE_NON_TAXABLE_PREFIX_ID});
      if (accountGroupDocPrefix != null) {
        if (draftPrefix) {
          accountGroupDocPrefix.setDocNumberCounter(accountGroupDocPrefix.getDraftDocNumberCounter());
          accountGroupDocPrefix.setDraftDocNumberCounter(getNextSequence(accountGroupDocPrefix, draftPrefix));
        } else {
          accountGroupDocPrefix.setDocNumberCounter(getNextSequence(accountGroupDocPrefix, draftPrefix));
        }
        AccountGroupDocPrefixService.insertOrUpdate(main, accountGroupDocPrefix);
      }
    }
  }

  private static Integer getNextSequence(AccountGroupDocPrefix accountGroupDocPrefix, boolean draft) {
    double maxValue;
    Integer nextSequence = 0;
    if (accountGroupDocPrefix != null) {
      maxValue = Math.pow(10, accountGroupDocPrefix.getPadding());
      if ((accountGroupDocPrefix.getDocNumberCounter() + 1) >= maxValue) {
        //nextSequence = 1;
        nextSequence = (accountGroupDocPrefix.getDocNumberCounter() + 1);
      } else {
        nextSequence = (accountGroupDocPrefix.getDocNumberCounter() + 1);
      }
    }
    return nextSequence;
  }

  public static AccountingLedger getAccountingLedgerByTaxCodeAndLedgerCode(Main main, Integer companyId, Integer parentId, Integer taxCodeId, String ledgerCode) {
    String sql = "select * from fin_accounting_ledger where entity_id = (select id from scm_tax_code where parent_id=? and id not in(?)) "
            + "and company_id=? and ledger_code=? ";
    return (AccountingLedger) AppService.single(main, AccountingLedger.class, sql, new Object[]{parentId, taxCodeId, companyId, ledgerCode});

  }
}
