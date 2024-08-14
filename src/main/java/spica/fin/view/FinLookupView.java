/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.fin.view;

import java.util.ArrayList;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.common.FilterObjects;
import spica.fin.domain.AccountingDocType;
import spica.fin.domain.AccountingGroup;
import spica.fin.domain.AccountingHead;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.ChequeEntry;
import spica.scm.domain.Company;
import spica.fin.domain.TaxCode;
import spica.fin.service.TaxHeadService;
import spica.scm.domain.AccountGroup;
import spica.scm.view.ScmLookupSql;
import wawo.app.faces.AppLookup;
import wawo.entity.util.StringUtil;

/**
 *
 * @author arun
 */
public abstract class FinLookupView {

  private static final String ACCOUNTING_LEDGER_EXPENSE = "select id,title,accounting_group_id,billwise,igst_id,cgst_id,sgst_id,tds_id from fin_accounting_ledger where company_id=? and accounting_group_id in(?,?) order by upper(title) asc";
  private static final String ACCOUNTING_EXPENSE_LEDGER_ITEMS_AUTO = "select id,title,accounting_group_id,billwise,igst_id,cgst_id,sgst_id,tds_id  from fin_accounting_ledger where company_id=? and accounting_group_id in(?,?) and upper(title) like ? order by upper(title) asc";

  private static final String PARENT_ACCOUNTING_HEAD = "select id,title from fin_accounting_head where id <> ? and id not in (select id from fin_accounting_head where accounting_head_id = ?) and company_id=? order by upper(title) asc";
  private static final String PARENT_ACCOUNTING_HEAD_AUTO = "select id,title from fin_accounting_head where id <> ? and id not in (select id from fin_accounting_head where accounting_head_id = ?) and company_id=? and upper(title) like ? order by upper(title) asc";

  /**
   * Creates a new instance of FinLookupView
   */
  public FinLookupView() {

  }

  public static final List<AccountingGroup> accountingGroupAuto(String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingGroup.class, ScmLookupSql.ACCOUNTING_GROUP_AUTO, new Object[]{AccountingConstant.ACC_OPENING_BALANCE.getId(), companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountingGroup.class, ScmLookupSql.ACCOUNTING_GROUP, new Object[]{AccountingConstant.ACC_OPENING_BALANCE.getId(), companyId});
  }

//  public static final List<AccountGroup> accountGroupAuto(AccountingLedger accoutingLedger) {
//    if (accoutingLedger != null) {
//      return AppLookup.getAutoFilter(AccountGroup.class, ScmLookupSql.CUSTOMER_ACCOUNT_GROUP,
//              new Object[]{accoutingLedger.getId(), AccountingConstant.ACC_ENTITY_SALES.getId(),
//                accoutingLedger.getId(), AccountingConstant.ACC_ENTITY_SALES_RETURN.getId(),
//                accoutingLedger.getId(), AccountingConstant.ACC_ENTITY_DEBIT_CREDIT_NOTE.getId()
//              });
//    }
//    return null;
//  }
  public static final List<AccountingLedger> accountingLedgerDirecrIndirectAuto(String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingLedger.class, ACCOUNTING_EXPENSE_LEDGER_ITEMS_AUTO, new Object[]{companyId, AccountingConstant.GROUP_DIRECT_EXPENSE.getId(), AccountingConstant.GROUP_INDIRECT_EXPENSE.getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountingLedger.class, ACCOUNTING_LEDGER_EXPENSE, new Object[]{companyId, AccountingConstant.GROUP_DIRECT_EXPENSE.getId(), AccountingConstant.GROUP_INDIRECT_EXPENSE.getId()});
  }

  public static List<AccountingHead> parentAccountingHeadAuto(Integer accountingHeadId, String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingHead.class, PARENT_ACCOUNTING_HEAD_AUTO, new Object[]{accountingHeadId, accountingHeadId, companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountingHead.class, PARENT_ACCOUNTING_HEAD, new Object[]{accountingHeadId, accountingHeadId, companyId});
  }

  public static final List<AccountingHead> accountingHeadAuto() {
    return AppLookup.getAutoFilter(AccountingHead.class, "select id,title from fin_accounting_head where id <> ? order by id asc", new Object[]{AccountingConstant.HEAD_OPENING_ENTRY.getId()});
  }

//Cheque entry
  private static final String CHEQUE_ENTRY_NON_RECONCILED = "select id,cheque_no,cheque_date,amount,note,account_group_id from fin_cheque_entry where ledger_id = ? and status_id = ? order by cheque_no, upper(cheque_no) asc";
  private static final String CHEQUE_ENTRY_NON_RECONCILED_BY_ACCOUNT_GROUP = "select id,cheque_no,cheque_date,amount,note,account_group_id from fin_cheque_entry where ledger_id = ? and account_group_id = ? and status_id = ? order by cheque_no, upper(cheque_no) asc";

  public static List<ChequeEntry> checkEntryNonReconciled(AccountingLedger ledger, FilterObjects filterObjects) {
    if (filterObjects != null && filterObjects.getSelectedAccountGroup() != null) {
      return AppLookup.getAutoFilter(ChequeEntry.class,
              CHEQUE_ENTRY_NON_RECONCILED_BY_ACCOUNT_GROUP, new Object[]{ledger.getId(), filterObjects.getSelectedAccountGroup().getId(), AccountingConstant.BANK_CHEQUE_RECIEVED});
    } else {
      return AppLookup.getAutoFilter(ChequeEntry.class,
              CHEQUE_ENTRY_NON_RECONCILED, new Object[]{ledger.getId(), AccountingConstant.BANK_CHEQUE_RECIEVED});
    }
  }

  //Acoounting document type
  private static final String ACCOUNTING_DOC_TYPE_VOUCHER = "select id,title from fin_accounting_doc_type where voucher_type_id = ? order by sort_order";

  public static List<AccountingDocType> accountingDocTypeByVoucher(Integer voucherTypeId) {
    return AppLookup.getAutoFilter(AccountingDocType.class,
            ACCOUNTING_DOC_TYPE_VOUCHER, new Object[]{voucherTypeId});
  }

  //Ledgers
  //=========
  private static final String LEDGER_SELECT = "select * from fin_accounting_ledger where company_id=?";
  private static final String LEDGER_BY_GROUP = LEDGER_SELECT + " and accounting_group_id in(?,?,?,?) order by upper(title) asc";
  private static final String LEDGER_BY_GROUP_AUTO = LEDGER_SELECT + " and accounting_group_id in(?,?,?,?) and upper(title) like ? order by upper(title) asc";
  private static final String LEDGER_BY_LEDGER_CODE = LEDGER_SELECT + " and accounting_group_id in(?) and ledger_code in (?,?) order by upper(title) asc";
  private static final String LEDGER_BY_LEDGER_CODE_AUTO = LEDGER_SELECT + " and accounting_group_id in(?) and ledger_code in (?,?) and upper(title) like ? order by upper(title) asc";
  private static final String ACCOUNTING_LEDGER = LEDGER_SELECT + " order by upper(title) asc";
  private static final String ACCOUNTING_LEDGER_AUTO = LEDGER_SELECT + " and upper(title) like ? order by upper(title) asc";
  private static final String LEDGER_BY_GROUP_TYPE_AUTO = LEDGER_SELECT + " and accounting_group_id in(?,?,?,?) and accounting_entity_type_id = ? and upper(title) like ? order by upper(title) asc";
  private static final String LEDGER_TYPE_BY_GROUP = LEDGER_SELECT + " and accounting_group_id in(?,?,?,?) and accounting_entity_type_id = ? order by upper(title) asc";
  private static final String LEDGER_BY_GROUP_OD_AUTO = LEDGER_SELECT + " and accounting_group_id in(?,?,?,?,?) and upper(title) like ? order by upper(title) asc";
  private static final String LEDGER_BY_GROUP_CASH_AUTO = LEDGER_SELECT + " and accounting_group_id in(?,?,?,?,?,?) and upper(title) like ? order by upper(title) asc";

  /**
   * Auto filter AccountingLedger from db based on the user input.
   *
   * @param filter - typed words by the user.
   * @param companyId
   * @return
   */
  public static final List<AccountingLedger> accountingLedgerAuto(String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingLedger.class, ACCOUNTING_LEDGER_AUTO, new Object[]{companyId, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountingLedger.class, ACCOUNTING_LEDGER, new Object[]{companyId});
  }

  public static final List<AccountingLedger> ledgerPurchaseAuto(String filter, Integer companyId) {
    return ledgerByGroup(filter, companyId, AccountingConstant.GROUP_PURCHASE_ACCOUNT.getId(), 0, 0, 0); //No second and third and 4th
  }

  public static final List<AccountingLedger> ledgerDirectAndIndirectIncomeAuto(String filter, Integer companyId) {
    return ledgerByGroup(filter, companyId, AccountingConstant.GROUP_DIRECT_INCOME.getId(), AccountingConstant.GROUP_INDIRECT_INCOME.getId(), 0, 0); //No second and third and 4th
  }

  public static final List<AccountingLedger> ledgerAssetsAndLiabilityAuto(String filter, Integer companyId) {
    return ledgerByGroup(filter, companyId, AccountingConstant.GROUP_CURRENT_ASSET.getId(), AccountingConstant.GROUP_CURRENT_LIABILITY.getId(), 0, 0); //Third and 4th group is empty here
  }

  public static final List<AccountingLedger> ledgerDebtorsAndCreditorsAuto(String filter, Integer companyId) {
    return ledgerByGroup(filter, companyId, AccountingConstant.GROUP_SUNDRY_CREDITORS.getId(), AccountingConstant.GROUP_SUNDRY_DEBTORS.getId(), 0, 0); //Third and 4th group is empty here
  }

  public static final List<AccountingLedger> ledgerPaymentAuto(String filter, Integer companyId) {
    return ledgerByGroup(filter, companyId, AccountingConstant.GROUP_BANK_ACCOUNT.getId(), AccountingConstant.GROUP_CASH_ACCOUNT.getId(), AccountingConstant.GROUP_BANK_OD.getId(), 0); //4th is empty
  }

  public static final List<AccountingLedger> ledgerExpenseDirectIndirectAndFixedAssetAuto(String filter, Integer companyId) {
    return ledgerByGroup(filter, companyId, AccountingConstant.GROUP_DIRECT_EXPENSE.getId(), AccountingConstant.GROUP_INDIRECT_EXPENSE.getId(), AccountingConstant.GROUP_FIXED_ASSET.getId(), 0); //4th is empty, TODO may be bring liability too
  }

  public static final List<AccountingLedger> ledgerExpensePaymentAgainstAuto(String filter, Integer companyId) {
    return ledgerByGroup(filter, companyId, AccountingConstant.GROUP_SUNDRY_CREDITORS.getId(), AccountingConstant.GROUP_SUNDRY_DEBTORS.getId(), AccountingConstant.GROUP_BANK_ACCOUNT.getId(), AccountingConstant.GROUP_SUNDRY_CREDITORS_EXPENSE.getId(), AccountingConstant.GROUP_BANK_OD.getId(), AccountingConstant.GROUP_CASH_ACCOUNT.getId());
  }

  private static final List<AccountingLedger> ledgerByGroup(String filter, Integer companyId, Integer groupId1, Integer groupId2, Integer groupId3, Integer groupId4, Integer groupId5, Integer groupId6) {
//    if (!StringUtil.isEmpty(filter)) {
    return AppLookup.getAutoFilter(AccountingLedger.class, LEDGER_BY_GROUP_CASH_AUTO, new Object[]{companyId, groupId1, groupId2, groupId3, groupId4, groupId5, groupId6, "%" + filter.toUpperCase() + "%"});
//    }
//    return AppLookup.getAutoFilter(AccountingLedger.class, LEDGER_BY_GROUP, new Object[]{companyId, groupId1, groupId2, groupId3, groupId4,groupId5,groupId6});
  }

  private static final List<AccountingLedger> ledgerByGroup(String filter, Integer companyId, Integer groupId1, Integer groupId2, Integer groupId3, Integer groupId4) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingLedger.class, LEDGER_BY_GROUP_AUTO, new Object[]{companyId, groupId1, groupId2, groupId3, groupId4, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountingLedger.class, LEDGER_BY_GROUP, new Object[]{companyId, groupId1, groupId2, groupId3, groupId4});
  }

  private static final List<AccountingLedger> ledgerByGroup(String filter, Integer companyId, Integer groupId1, Integer groupId2, Integer groupId3, Integer groupId4, Integer groupId5) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingLedger.class, LEDGER_BY_GROUP_OD_AUTO, new Object[]{companyId, groupId1, groupId2, groupId3, groupId4, groupId5, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountingLedger.class, LEDGER_BY_GROUP_OD_AUTO, new Object[]{companyId, groupId1, groupId2, groupId3, groupId4, groupId5});
  }

  public static final List<AccountingLedger> ledgerSalesServiceAuto(String filter, Integer companyId) {
    return ledgerByLedgerCode(filter, companyId, AccountingConstant.GROUP_SALES_ACCOUNT.getId(), AccountingConstant.LEDGER_CODE_SALES_SERVICE, "0");
  }

  public static final List<AccountingLedger> ledgerSalesProductAuto(String filter, Integer companyId) {
    return ledgerByLedgerCode(filter, companyId, AccountingConstant.GROUP_SALES_ACCOUNT.getId(), AccountingConstant.LEDGER_CODE_SALES, "0");
  }

  public static final List<AccountingLedger> ledgerSalesReturnAuto(String filter, Integer companyId) {
    return ledgerByLedgerCode(filter, companyId, AccountingConstant.GROUP_SALES_ACCOUNT.getId(), AccountingConstant.LEDGER_CODE_SALES_RETURN, "0");
  }

  public static final List<AccountingLedger> ledgerPurchaseReturnAuto(String filter, Integer companyId) {
    return ledgerByLedgerCode(filter, companyId, AccountingConstant.GROUP_PURCHASE_ACCOUNT.getId(), AccountingConstant.LEDGER_CODE_PURCHASE_RETURN, "0");
  }

  public static final List<AccountingLedger> ledgerSalesAllAuto(String filter, Integer companyId) {
    return ledgerByLedgerCode(filter, companyId, AccountingConstant.GROUP_SALES_ACCOUNT.getId(), AccountingConstant.LEDGER_CODE_SALES, AccountingConstant.LEDGER_CODE_SALES_SERVICE);
  }

  private static final List<AccountingLedger> ledgerByLedgerCode(String filter, Integer companyId, Integer groupId1, String ledgerCode1, String ledgerCode2) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingLedger.class, LEDGER_BY_LEDGER_CODE_AUTO, new Object[]{companyId, groupId1, ledgerCode1, ledgerCode2, "%" + filter.toUpperCase() + "%"});

    }
    return AppLookup.getAutoFilter(AccountingLedger.class, LEDGER_BY_LEDGER_CODE, new Object[]{companyId, groupId1, ledgerCode1, ledgerCode2});
  }

  //TAX Code
  //=============
  private static final String TAX_CODE_SELECT = "select scm_tax_code.id, scm_tax_code.code from scm_tax_code inner join scm_tax_head on scm_tax_head.id = scm_tax_code.tax_head_id where scm_tax_head.computed_on <> ? and scm_tax_code.country_id = ? ";
  private static final String TAX_CODE_ALL_PARENT = TAX_CODE_SELECT + " and scm_tax_code.parent_id is null order by upper(scm_tax_code.code) asc";
  private static final String TAX_CODE_BY_HEAD = TAX_CODE_SELECT + " and scm_tax_code.parent_id is null and scm_tax_head.id =? order by upper(scm_tax_code.code) asc";
  private static final String TAX_CODE_BY_NON_HEAD = TAX_CODE_SELECT + " and scm_tax_code.parent_id is null and scm_tax_head.id != ? order by upper(scm_tax_code.code) asc";
  private static final String ACCOUNTING_LEDGER_SUPPLIER = "select * from fin_accounting_ledger where company_id = ? and accounting_entity_type_id = ? and entity_id in (select vendor_id from scm_account where id = ?)";
  private static final String ACCOUNTING_LEDGER_CUSTOMER = "select * from fin_accounting_ledger where company_id = ? and accounting_entity_type_id = ? and entity_id in (select customer_id from scm_sales_account where account_group_id = ?)";

  public static final List<TaxCode> taxCodeAll(Company company) { //servicesTaxCode
    return AppLookup.getAutoFilter(TaxCode.class,
            TAX_CODE_ALL_PARENT, new Object[]{TaxHeadService.COMPUTED_ON_TAX, company.getCountryId().getId()});
  }

  public static final List<TaxCode> taxCodeTdsHead(Company company) { //servicesTaxCodeTds
    return AppLookup.getAutoFilter(TaxCode.class,
            TAX_CODE_BY_HEAD, new Object[]{TaxHeadService.COMPUTED_ON_TAX, company.getCountryId().getId(), AccountingConstant.TAX_HEAD_TDS.getId()});
  }

  public static final List<TaxCode> taxCodeGstHead(Company company) {
    return AppLookup.getAutoFilter(TaxCode.class,
            TAX_CODE_BY_HEAD, new Object[]{TaxHeadService.COMPUTED_ON_TAX, company.getCountryId().getId(), AccountingConstant.TAX_HEAD_GST.getId()});
  }

  public static final List<TaxCode> taxCodeNonTdsHead(Company company) {//servicesTaxCodeSalesAgent
    return AppLookup.getAutoFilter(TaxCode.class,
            TAX_CODE_BY_NON_HEAD, new Object[]{TaxHeadService.COMPUTED_ON_TAX, company.getCountryId().getId(), AccountingConstant.TAX_HEAD_TDS.getId()});
  }

  private static final String ACCOUNTING_LEDGER_TAX_ITEMS_AUTO = LEDGER_SELECT + " and ledger_code=? and (accounting_group_id = ? or accounting_group_id in(select id from fin_accounting_group where parent_id=?)) and upper(title) like ? order by upper(title) asc";
  private static final String ACCOUNTING_TAX_ITEMS_LEDGER = LEDGER_SELECT + " and ledger_code=? and (accounting_group_id = ? or accounting_group_id in(select id from fin_accounting_group where parent_id=?)) order by upper(title) asc";

  public static final List<AccountingLedger> ledgerTaxAuto(String filter, Integer companyId, boolean isIn) {
    String ledgerCode = isIn ? AccountingConstant.LEDGER_CODE_TAX_INPUT : AccountingConstant.LEDGER_CODE_TAX_OUTPUT;
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingLedger.class, ACCOUNTING_LEDGER_TAX_ITEMS_AUTO, new Object[]{companyId, ledgerCode, AccountingConstant.ACC_GROUP_DUTIES_AND_TAX.getId(), AccountingConstant.ACC_GROUP_DUTIES_AND_TAX.getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountingLedger.class, ACCOUNTING_TAX_ITEMS_LEDGER, new Object[]{companyId, ledgerCode, AccountingConstant.ACC_GROUP_DUTIES_AND_TAX.getId(), AccountingConstant.ACC_GROUP_DUTIES_AND_TAX.getId()});
  }

  private static final String TAX_TDS_LEDGER_AUTO = LEDGER_SELECT + " and ledger_code = ? and upper(title) like ? order by upper(title) asc";
  private static final String TAX_TDS_LEDGER = LEDGER_SELECT + " and ledger_code = ? order by upper(title) asc";

  public static final List<AccountingLedger> ledgerTaxTdsAuto(String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingLedger.class, TAX_TDS_LEDGER_AUTO, new Object[]{companyId, AccountingConstant.LEDGER_CODE_TAX_TDS_LIABILITY, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountingLedger.class, TAX_TDS_LEDGER, new Object[]{companyId, AccountingConstant.LEDGER_CODE_TAX_TDS_LIABILITY});
  }

  private static final String ROUNDOFF_LEDGER_AUTO = LEDGER_SELECT + " and ledger_code = ? and upper(title) like ? order by upper(title) asc";
  private static final String ROUNDOFF_LEDGER = LEDGER_SELECT + " and ledger_code = ? order by upper(title) asc";

  public static final List<AccountingLedger> ledgerRoundOffAuto(String filter, Integer companyId) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingLedger.class, ROUNDOFF_LEDGER_AUTO, new Object[]{companyId, AccountingConstant.LEDGER_CODE_PURCHASE_ROUND_OFF, "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountingLedger.class, ROUNDOFF_LEDGER, new Object[]{companyId, AccountingConstant.LEDGER_CODE_PURCHASE_ROUND_OFF});
  }

  public static final List<AccountingLedger> ledgerByEntityAuto(String filter, Integer companyId, Integer entityFromId, String entityType) {
    String sql = null;
    ArrayList<Object> params = new ArrayList();
    if (AccountingConstant.SUPPLIER_DEBIT_CREDIT_NOTE.equals(entityType) || AccountingConstant.SUPPLIER_SALES_BILL.equals(entityType)) {
      sql = ACCOUNTING_LEDGER_SUPPLIER;
      params.add(companyId);
      params.add(AccountingConstant.ACC_ENTITY_VENDOR.getId());
      params.add(entityFromId);
    } else if (AccountingConstant.CUSTOMER_DEBIT_CREDIT_NOTE.equals(entityType)) {
      sql = ACCOUNTING_LEDGER_CUSTOMER;
      params.add(companyId);
      params.add(AccountingConstant.ACC_ENTITY_CUSTOMER.getId());
      params.add(entityFromId);
    }
    if (!StringUtil.isEmpty(filter)) {
      params.add("%" + filter.toUpperCase() + "%");
    }
    return ledgerByEntity(filter, sql, params.toArray());
  }

  private static final List<AccountingLedger> ledgerByEntity(String filter, String sql, Object[] params) {
    if (!StringUtil.isEmpty(sql)) {
      if (!StringUtil.isEmpty(filter)) {
        return AppLookup.getAutoFilter(AccountingLedger.class, sql + "and upper(title) like ? order by upper(title) asc", params);
      }
      return AppLookup.getAutoFilter(AccountingLedger.class, sql, params);
    }
    return null;
  }

  public static final List<AccountingLedger> ledgerDebtorsAuto(String filter, Integer companyId) {
    return ledgerByGroupAndType(filter, companyId, AccountingConstant.GROUP_SUNDRY_CREDITORS.getId(), AccountingConstant.GROUP_SUNDRY_DEBTORS.getId(), 0, 0); //Third and 4th group is empty here
  }

  private static final List<AccountingLedger> ledgerByGroupAndType(String filter, Integer companyId, Integer groupId1, Integer groupId2, Integer groupId3, Integer groupId4) {
    if (!StringUtil.isEmpty(filter)) {
      return AppLookup.getAutoFilter(AccountingLedger.class, LEDGER_BY_GROUP_TYPE_AUTO, new Object[]{companyId, groupId1, groupId2, groupId3, groupId4, AccountingConstant.ACC_ENTITY_CUSTOMER.getId(), "%" + filter.toUpperCase() + "%"});
    }
    return AppLookup.getAutoFilter(AccountingLedger.class, LEDGER_TYPE_BY_GROUP, new Object[]{companyId, groupId1, groupId2, groupId3, groupId4, AccountingConstant.ACC_ENTITY_CUSTOMER});
  }

}
