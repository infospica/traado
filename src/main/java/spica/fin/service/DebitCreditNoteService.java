/*
 * @(#)DebitCreditNoteService.java	1.0 Tue Feb 27 12:40:25 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.domain.TaxCode;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.Platform;
import spica.scm.domain.Product;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesReturn;
import spica.scm.function.SalesReturnCreditNote;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.AccountGroupService;
import spica.scm.service.PrefixTypeService;
import spica.sys.SystemConstants;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.app.faces.MainView;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * DebitCreditNoteService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Feb 27 12:40:25 IST 2018
 */
public abstract class DebitCreditNoteService {

  //public static final Integer STATUS_CONFIRMED = 1;
  //public static final Integer STATUS_DRAFT = 2;
  // public static final Integer INTRASTATE = 1;
  // public static final Integer INTERSTATE = 2;
  public static final Integer SUPPLIER_DEBIT_CREDIT_NOTE = 1;
  public static final Integer CUSTOMER_DEBIT_CREDIT_NOTE = 2;
  public static final Integer SUPPLIER_SALES_BILL = 3;

  /**
   * DebitCreditNote paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getDebitCreditNoteSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("t1", DebitCreditNote.class, main);
    sql.main("select t1.id,t1.company_id,t1.account_id,t1.accounting_ledger_id,t1.invoice_no,t1.invoice_date,t1.entry_date,t1.taxable_invoice,t1.status_id,"
            + "t1.account_id,t1.account_group_id,t1.document_no,t1.confirmed_net_value,"
            + "t1.tax_processor_id,t1.invoice_type,t1.assessable_value,t1.igst_value,t1.cgst_value,t1.sgst_value,t1.net_value,t1.round_off_value,t1.created_by,"
            + "t1.modified_by,t1.created_at,t1.modified_at from fin_debit_credit_note t1 "); //Main query
    sql.count("select count(t1.id) as total from fin_debit_credit_note t1 "); //Count query
    sql.join("LEFT OUTER JOIN fin_accounting_ledger t2 ON t2.id=t1.accounting_ledger_id ");

//    sql.join("left outer join fin_accounting_ledger fin_accounting_ledger on (fin_accounting_ledger.id = t1.accounting_ledger_id) "
//            + "left outer join scm_company scm_company on (scm_company.id = t1.company_id)"
//            + "left outer join scm_account scm_account on (scm_account.id = t1.account_id)"
//            + "left outer join scm_account_group scm_account_group on (scm_account_group.id = t1.account_group_id)"); //Join Query
    sql.string(new String[]{"t1.document_no", "t2.title"}); //String search or sort fields
    sql.number(new String[]{"t1.igst_value", "t1.cgst_value", "t1.sgst_value", "t1.net_value"}); //Numeric search or sort fields
    sql.date(new String[]{"t1.invoice_date", "t1.entry_date"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of DebitCreditNote.
   *
   * @param main
   * @return List of DebitCreditNote
   */
  public static final List<DebitCreditNote> listPaged(Main main) {
    return AppService.listPagedJpa(main, getDebitCreditNoteSqlPaged(main));
  }

  /**
   *
   * @param main
   * @param company
   * @return
   */
  public static final List<DebitCreditNote> listPagedByCompany(Main main, Company company) {
    SqlPage sql = getDebitCreditNoteSqlPaged(main);
    sql.cond("where t1.company_id = ? and t1.financial_year_id=? ");
    sql.param(company.getId());
    sql.param(company.getCurrentFinancialYear().getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<DebitCreditNote> listPagedByAccount(Main main, Company company, Account account, Integer invoiceType) {
    SqlPage sql = getDebitCreditNoteSqlPaged(main);
    sql.cond("where t1.company_id = ? and t1.account_id = ? and t1.invoice_type = ? and t1.account_group_id IS NULL and t1.financial_year_id=? ");
    sql.param(company.getId());
    sql.param(account.getId());
    sql.param(invoiceType);
    sql.param(company.getCurrentFinancialYear().getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<DebitCreditNote> listPagedByAccountGroup(Main main, Company company, AccountGroup accountGroup, Integer invoiceType) {
    SqlPage sql = getDebitCreditNoteSqlPaged(main);
    sql.cond("where t1.company_id = ? and t1.account_group_id = ? and t1.invoice_type = ? and t1.financial_year_id=? ");
    sql.param(company.getId());
    sql.param(accountGroup.getId());
    sql.param(invoiceType);
    sql.param(company.getCurrentFinancialYear().getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<DebitCreditNote> listPagedByParty(Main main, DebitCreditNote drcr) {
    SqlPage sql = getDebitCreditNoteSqlPaged(main);
    String s = " where t1.company_id = ? and t1.invoice_type = ? and t1.debit_credit_party = ? and t1.financial_year_id =? ";
    sql.param(drcr.getCompanyId().getId());
    sql.param(drcr.getInvoiceType());
    sql.param(drcr.getDebitCreditParty());
    sql.param(drcr.getCompanyId().getCurrentFinancialYear().getId());
    if (drcr.getDebitCreditParty() == Integer.parseInt(AccountingConstant.SUPPLIER_DEBIT_CREDIT_NOTE)) {
      s += " AND t1.account_id = ? ";
      sql.param(drcr.getAccountId().getId());
    }
    if (drcr.getDebitCreditParty() == Integer.parseInt(AccountingConstant.CUSTOMER_DEBIT_CREDIT_NOTE)) {
      s += " AND t1.account_group_id = ? ";
      sql.param(drcr.getAccountGroupId().getId());
    }
    sql.cond(s);
    sql.orderBy(" t1.entry_date desc ");

    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of DebitCreditNote based on condition
//   * @param main
//   * @return List<DebitCreditNote>
//   */
//  public static final List<DebitCreditNote> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getDebitCreditNoteSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select DebitCreditNote by key.
   *
   * @param main
   * @param debitCreditNote
   * @return DebitCreditNote
   */
  public static final DebitCreditNote selectByPk(Main main, DebitCreditNote debitCreditNote) {
    return (DebitCreditNote) AppService.find(main, DebitCreditNote.class, debitCreditNote.getId());
  }

  /**
   * Insert DebitCreditNote.
   *
   * @param main
   * @param debitCreditNote
   */
  public static final void insert(Main main, DebitCreditNote debitCreditNote) {
    insertAble(main, debitCreditNote);  //Validating
    AppService.insert(main, debitCreditNote);

  }

  /**
   * Update DebitCreditNote by key.
   *
   * @param main
   * @param debitCreditNote
   * @return DebitCreditNote
   */
  public static final DebitCreditNote updateByPk(Main main, DebitCreditNote debitCreditNote) {
    updateAble(main, debitCreditNote); //Validating
    return (DebitCreditNote) AppService.update(main, debitCreditNote);
  }

  /**
   * Insert or update DebitCreditNote
   *
   * @param main
   * @param debitCreditNote
   */
  public static void insertOrUpdate(Main main, DebitCreditNote debitCreditNote) {
    if (SystemConstants.CONFIRMED.equals(debitCreditNote.getStatusId())) {
      if (debitCreditNote.getConfirmedAt() == null) {
        selectDebitCreditNoteInvoiceNo(main, debitCreditNote, false);
        updateDebitCreditNoteAccountGroupDocPrefix(main, debitCreditNote, false);
      }
      debitCreditNote.setConfirmedAt(new Date());
      if (main.getAppUser() != null) {
        debitCreditNote.setConfirmedBy(main.getAppUser().getLogin());
      }
    }

    if (debitCreditNote.getId() == null) {
      insert(main, debitCreditNote);
      updateDebitCreditNoteAccountGroupDocPrefix(main, debitCreditNote, true);
    } else {
      updateByPk(main, debitCreditNote);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param debitCreditNote
   */
  public static void clone(Main main, DebitCreditNote debitCreditNote, List<DebitCreditNoteItem> debitCreditNoteItemList) {
    debitCreditNote.setId(null); //Set to null for insert    
    insert(main, debitCreditNote);
    for (DebitCreditNoteItem item : debitCreditNoteItemList) {
      item.setId(null);
      item.setDebitCreditNoteId(debitCreditNote);
    }
    insertOrUpdate(main, debitCreditNote, debitCreditNoteItemList);
  }

  /**
   * Delete DebitCreditNote.
   *
   * @param main
   * @param debitCreditNote
   */
  public static final void deleteByPk(Main main, DebitCreditNote debitCreditNote) {
    deleteAble(main, debitCreditNote); //Validation
    resetToDraft(main, debitCreditNote);
    AppService.deleteSql(main, Platform.class, "delete from scm_platform where debit_credit_note_id=?", new Object[]{debitCreditNote.getId()});
    AppService.deleteSql(main, DebitCreditNoteItem.class, "delete from fin_debit_credit_note_item where debit_credit_note_id=?", new Object[]{debitCreditNote.getId()});
    AppService.delete(main, DebitCreditNote.class, debitCreditNote.getId());
  }

  /**
   * Delete Array of DebitCreditNote.
   *
   * @param main
   * @param debitCreditNote
   */
  public static final void deleteByPkArray(Main main, DebitCreditNote[] debitCreditNote) {
    for (DebitCreditNote e : debitCreditNote) {
      deleteByPk(main, e);
      main.em().commit();
    }
  }

  /**
   *
   * @param main
   * @param debitCreditNote
   * @param draft
   * @return
   */
  public static final void selectDebitCreditNoteInvoiceNo(Main main, DebitCreditNote debitCreditNote, boolean draft) {
    String prefix = "";
    String draftPrefix = draft ? "DR-" : "";
    if (debitCreditNote != null) {

      if (debitCreditNote.getInvoiceType().equals(SystemConstants.DEBIT_NOTE)) {
        if (SystemConstants.TAXABLE_DEBIT_CREDIT_NOTE.equals(debitCreditNote.getTaxableInvoice())) {
          if (debitCreditNote.getDebitCreditParty() == DebitCreditNoteService.SUPPLIER_SALES_BILL) {
            prefix = draftPrefix + AccountGroupDocPrefixService.getNextDebitCreditNotePefixSequence(main, debitCreditNote, PrefixTypeService.VALUE_DEBIT_NOTE_TAXABLE_PREFIX_ID, draft, debitCreditNote.getFinancialYearId());
          } else {
            prefix = draftPrefix + AccountGroupDocPrefixService.getNextDebitCreditNotePefixSequence(main, debitCreditNote, PrefixTypeService.VALUE_DEBIT_NOTE_TAXABLE_PREFIX_ID, draft, debitCreditNote.getFinancialYearId());
          }

        } else {
          prefix = draftPrefix + AccountGroupDocPrefixService.getNextDebitCreditNotePefixSequence(main, debitCreditNote, PrefixTypeService.VALUE_DEBIT_NOTE_NON_TAXABLE_PREFIX_ID, draft, debitCreditNote.getFinancialYearId());
        }
      } else {
        if (SystemConstants.TAXABLE_DEBIT_CREDIT_NOTE.equals(debitCreditNote.getTaxableInvoice())) {
          prefix = draftPrefix + AccountGroupDocPrefixService.getNextDebitCreditNotePefixSequence(main, debitCreditNote, PrefixTypeService.VALUE_CREDIT_NOTE_TAXABLE_PREFIX_ID, draft, debitCreditNote.getFinancialYearId());
        } else {
          prefix = draftPrefix + AccountGroupDocPrefixService.getNextDebitCreditNotePefixSequence(main, debitCreditNote, PrefixTypeService.VALUE_CREDIT_NOTE_NON_TAXABLE_PREFIX_ID, draft, debitCreditNote.getFinancialYearId());
        }
      }

    }
    debitCreditNote.setDocumentNo(prefix);
  }

  /**
   *
   * @param main
   * @param debitCreditNote
   * @param draft
   */
  public static void updateDebitCreditNoteAccountGroupDocPrefix(Main main, DebitCreditNote debitCreditNote, boolean draft) {
    if (debitCreditNote != null) {
      if (debitCreditNote.getInvoiceType().equals(SystemConstants.DEBIT_NOTE)) {
        if (SystemConstants.TAXABLE_DEBIT_CREDIT_NOTE.equals(debitCreditNote.getTaxableInvoice())) {
          AccountGroupDocPrefixService.updateDebitCreditNotePrefixSequence(main, debitCreditNote, PrefixTypeService.VALUE_DEBIT_NOTE_TAXABLE_PREFIX_ID, draft, debitCreditNote.getFinancialYearId());
        } else {
          AccountGroupDocPrefixService.updateDebitCreditNotePrefixSequence(main, debitCreditNote, PrefixTypeService.VALUE_DEBIT_NOTE_NON_TAXABLE_PREFIX_ID, draft, debitCreditNote.getFinancialYearId());
        }
      } else {
        if (SystemConstants.TAXABLE_DEBIT_CREDIT_NOTE.equals(debitCreditNote.getTaxableInvoice())) {
          AccountGroupDocPrefixService.updateDebitCreditNotePrefixSequence(main, debitCreditNote, PrefixTypeService.VALUE_CREDIT_NOTE_TAXABLE_PREFIX_ID, draft, debitCreditNote.getFinancialYearId());
        } else {
          AccountGroupDocPrefixService.updateDebitCreditNotePrefixSequence(main, debitCreditNote, PrefixTypeService.VALUE_CREDIT_NOTE_NON_TAXABLE_PREFIX_ID, draft, debitCreditNote.getFinancialYearId());
        }

      }
    }
  }

  /**
   *
   * @param main
   * @param debitCreditNote
   * @param debitCreditNoteItemList
   */
  public static void insertOrUpdate(Main main, DebitCreditNote debitCreditNote, List<DebitCreditNoteItem> debitCreditNoteItemList) {
    insertOrUpdate(main, debitCreditNote);
    if (!StringUtil.isEmpty(debitCreditNoteItemList)) {
      for (DebitCreditNoteItem debitCreditNoteItem : debitCreditNoteItemList) {
        if (!StringUtil.isEmpty(debitCreditNoteItem.getTitle()) && debitCreditNoteItem.getTaxableValue() != null) {
          DebitCreditNoteItemService.insertOrUpdate(main, debitCreditNoteItem);
        }
      }
    }
  }

  public static final void insertSalesReturnCreditNote(Main main, SalesReturn saleReturn, SalesReturnCreditNote salesReturnCreditNote) {
    salesReturnCreditNote.processCreditNote(main, saleReturn);
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param debitCreditNote
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, DebitCreditNote debitCreditNote) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param debitCreditNote
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, DebitCreditNote debitCreditNote) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_debit_credit_note where upper(company_id)=?", new Object[]{StringUtil.toUpperCase(debitCreditNote.getCompanyId())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param debitCreditNote
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, DebitCreditNote debitCreditNote) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_debit_credit_note where upper(company_id)=? and id !=?", new Object[]{StringUtil.toUpperCase(debitCreditNote.getCompanyId()), debitCreditNote.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  public static List<Product> getProductListByLedgerAndAccountGroup(MainView main, Integer customerId, AccountGroup accountGroup, String filter) {

//    String sql = "select t5.id,t5.product_name,t5.hsn_code from scm_sales_invoice t1,scm_sales_invoice_item t2,scm_product_detail t3,scm_product_preset t4,scm_product t5\n"
//            + "WHERE t1.customer_id = ? \n"
//            + "AND t1.id=t2.sales_invoice_id\n"
//            + "AND t2.account_id = ? \n"
//            + "AND t2.product_detail_id = t3.id\n"
//            + "AND t3.product_preset_id = t4.id\n"
//            + "AND t4.product_id = t5.id\n"
//            + "AND UPPER(t5.product_name) LIKE ? \n"
//            + "GROUP BY t5.id,t5.product_name,t5.hsn_code";
    String sql = "select t5.id,t5.product_name,t5.hsn_code \n"
            + "from scm_sales_invoice t1,scm_sales_invoice_item t2,scm_product_detail t3,scm_product_preset t4,scm_product t5\n"
            + "WHERE t1.customer_id = ? AND t1.account_group_id = ? \n"
            + "AND t1.id=t2.sales_invoice_id\n"
            + "AND t2.product_detail_id = t3.id\n"
            + "AND t3.product_preset_id = t4.id\n"
            + "AND t4.product_id = t5.id\n"
            + "AND UPPER(t5.product_name) LIKE ?\n"
            + "GROUP BY t5.id,t5.product_name,t5.hsn_code";

    return AppService.list(main, Product.class, sql, new Object[]{customerId, accountGroup.getId(), "%" + filter.toUpperCase() + "%"});

  }

  public static List<Product> getProductPurchaseByLedgerAndAccount(MainView main, DebitCreditNote drcr, String filter) {
//  PRODUCT BY SALES
//    String sql = "select t5.id,t5.product_name,t5.hsn_code \n"
//            + "from  scm_sales_invoice t1, scm_sales_invoice_item t2,scm_product_detail t3, scm_product_preset t4 , scm_product t5\n"
//            + "WHERE t1.company_id = ? \n"
//            + "AND t1.sales_invoice_status_id = 2\n"
//            + "AND t2.sales_invoice_id = t1.id\n"
//            + "AND t2.product_detail_id = t3.id \n"
//            + "AND t3.account_id = ? AND t3.product_preset_id = t4.id \n"
//            + "AND t4.product_id = t5.id\n"
//            + "GROUP BY t5.id,t5.product_name,t5.hsn_code order by t5.product_name ASC \n" ;
    String sql = "SELECT t1.id,t1.product_name,t1.hsn_code FROM scm_product t1, scm_product_detail t2,scm_product_preset t3 \n"
            + "WHERE t2.account_id = ? \n"
            + "AND t2.product_preset_id = t3.id \n"
            + "AND t3.product_id = t1.id AND t1.company_id = ? \n"
            + "AND UPPER(t1.product_name) LIKE ? GROUP BY t1.id,t1.product_name,t1.hsn_code ORDER BY  t1.product_name ASC ";

    return AppService.list(main, Product.class, sql, new Object[]{drcr.getAccountId().getId(), drcr.getCompanyId().getId(), "%" + filter.toUpperCase() + "%"});

  }

  public static List<SalesInvoice> listSalesInvoiceByAccount(MainView main, Account account) {
    String sql = "select t1.id,t1.invoice_no,t1.invoice_date FROM scm_sales_invoice t1, scm_sales_invoice_item t2\n"
            + "WHERE t2.sales_invoice_id=t1.id AND t2.account_id =? \n"
            + "GROUP BY t1.id,t1.invoice_no,t1.invoice_date ORDER BY t1.invoice_date DESC ";
    return AppService.list(main, SalesInvoice.class, sql, new Object[]{account.getId()});
  }

  public static List<ServiceCommodity> getServices(MainView main, Company company, String filter) {
    String sql = "select * from scm_service_commodity where \n"
            + "(company_id = ? or company_id is null)\n"
            + "AND commodity_or_service = 2 AND UPPER(title) like ? ";
    return AppService.list(main, ServiceCommodity.class, sql, new Object[]{company.getId(), "%" + filter.toUpperCase() + "%"});
  }

  public static List<ProductEntryDetail> getPurchaseProductInvoice(MainView main, Product product, Account account) {
    String sql = "select t1.invoice_date,t1.account_id,t2.id,t2.product_entry_id,t2.batch_no,t2.value_mrp,t2.expiry_date,t2.tax_code_id,t2.product_quantity \n"
            + "from scm_product_entry t1, scm_product_entry_detail t2, scm_product_detail t3 , scm_product_preset t4 , scm_tax_code t5\n"
            + "WHERE t1.id = t2.product_entry_id\n"
            + "AND t2.product_detail_id = t3.id  \n"
            + "AND t3.account_id = ? AND t3.product_preset_id = t4.id\n"
            + "AND t4.product_id = ? \n"
            //            + "AND t1.sales_return_id is null \n"
            + "AND t2.tax_code_id = t5.id\n"
            + "GROUP BY t1.invoice_date,t1.account_id,t2.id,t2.product_entry_id,t2.batch_no,t2.value_mrp,t2.expiry_date,t2.tax_code_id,t2.product_quantity\n"
            + "ORDER BY t1.invoice_date DESC";

    return AppService.list(main, ProductEntryDetail.class, sql, new Object[]{account.getId(), product.getId()});
  }

  public static List<SalesInvoiceItem> selectInvoiceByProductAndAccountGroupAndCustomer(MainView main, Product product, AccountGroup accountGroupId, Integer customerId) {
    String sql = "select t1.invoice_date,t2.id,t2.sales_invoice_id,t2.product_qty,t2.tax_code_id,t2.value_mrp,t2.expiry_date,t2.batch_no\n"
            + "from scm_sales_invoice t1,scm_sales_invoice_item t2,scm_product_detail t3,scm_product_preset t4\n"
            + "WHERE t1.id = t2.sales_invoice_id\n"
            + "AND t2.product_detail_id = t3.id\n"
            + "AND t3.product_preset_id = t4.id\n"
            + "AND t4.product_id = ? \n"
            + "AND t1.account_group_id = ? AND t1.customer_id= ? \n"
            + "GROUP BY t1.account_group_id,t1.invoice_date,t2.id,t2.sales_invoice_id,t2.product_qty,t2.tax_code_id,t2.value_mrp,t2.expiry_date,t2.batch_no\n"
            + "ORDER BY t1.invoice_date DESC ";
    return AppService.list(main, SalesInvoiceItem.class, sql, new Object[]{product.getId(), accountGroupId.getId(), customerId});
  }

  public static TaxCode selectTaxCodeByCommodity(MainView main, ServiceCommodity commodity) {
    String sql = "select t2.id,t2.code,t2.rate_percentage from scm_service_commodity t1, scm_tax_code t2\n"
            + "WHERE t1.id = ?\n"
            + "AND t1.sales_tax_code_id = t2.id GROUP BY t2.id,t2.code,t2.rate_percentage";
    return (TaxCode) AppService.single(main, TaxCode.class, sql, new Object[]{commodity.getId()});

  }

  public static TaxCode selectTaxCodeByCategory(MainView main, ProductCategory productCategory) {
    String sql = "select t2.id,t2.code,t2.rate_percentage from scm_product_category t1, scm_tax_code t2\n"
            + "WHERE t1.id = ?\n"
            + "AND t1.sales_tax_code_id = t2.id GROUP BY t2.id,t2.code,t2.rate_percentage";
    return (TaxCode) AppService.single(main, TaxCode.class, sql, new Object[]{productCategory.getId()});

  }

  public static List<SalesInvoice> selectInvoiceByServiceAndAccount(MainView main, ServiceCommodity commodity, Account account) {
    String sql = "select t1.id,t1.invoice_no,t1.invoice_date from scm_sales_invoice t1,scm_sales_invoice_item t2,scm_product_detail t3,scm_product_preset t4,scm_product t5\n"
            + "WHERE t1.id = t2.sales_invoice_id\n"
            + "AND t2.product_detail_id = t3.id\n"
            + "AND t3.product_preset_id = t4.id\n"
            + "AND t4.account_id = ? \n"
            + "AND t4.product_id = t5.id \n"
            + "AND t5.commodity_id = ? \n"
            + "GROUP BY t1.id,t1.invoice_no,t1.invoice_date\n"
            + "ORDER BY t1.invoice_date DESC";
    return AppService.list(main, SalesInvoice.class, sql, new Object[]{account.getId(), commodity.getId()});
  }

  public static Platform selectPlatFormInSettlementByDebitCredit(MainView main, DebitCreditNote debitCreditNote) {

    String sql = " select t1.id,t1.debit_credit_note_id\n"
            + "from scm_platform t1,scm_platform_settlement_item t2\n"
            + "where t1.debit_credit_note_id = ? \n"
            + "AND t2.platform_id = t1.id";
    return (Platform) AppService.single(main, Platform.class, sql, new Object[]{debitCreditNote.getId()});

  }

  public static void resetToDraft(Main main, DebitCreditNote debitCreditNote) {
    AccountingTransaction transaction = AccountingTransactionService.selectByDebitCreditNote(main, debitCreditNote);
    if (transaction != null) {
      AccountingTransactionService.deleteByPk(main, transaction);
      TradeOutstandingService.deleteByEntityIdAndEntityType(main, debitCreditNote.getId(), AccountingConstant.ACC_ENTITY_DEBIT_CREDIT_NOTE.getId());
    }
    debitCreditNote.setStatusId(SystemConstants.DRAFT);
    updateByPk(main, debitCreditNote);

  }

  public static DebitCreditNote selectByAccountingTransactionDetailItem(MainView main, Integer id) {
    String sql = "SELECT t4.* \n"
            + "FROM fin_accounting_transaction_detail_item t1,fin_accounting_transaction_detail t2,fin_accounting_transaction t3 , fin_debit_credit_note t4 \n"
            + "WHERE t1.id = ? \n"
            + "AND t1.accounting_transaction_detail_id = t2.id \n"
            + "AND t2.accounting_transaction_id = t3.id \n"
            + "AND t3.accounting_entity_type_id = ? \n"
            + "AND t3.entity_id = t4.id ";
    return (DebitCreditNote) AppService.single(main, DebitCreditNote.class, sql, new Object[]{id, AccountingConstant.ACC_ENTITY_DEBIT_CREDIT_NOTE.getId()});
  }

  public static List<SalesInvoice> selectInvoiceByCustomer(Main main, Integer customerId, AccountGroup accountGroup, String filter) {
    String sql = "select t1.invoice_no,t1.id,t1.invoice_date,t1.invoice_amount_net,t1.invoice_entry_date \n"
            + " from scm_sales_invoice t1 \n"
            + " WHERE t1.customer_id = ? AND t1.account_group_id = ? \n"
            + " AND UPPER(t1.invoice_no) LIKE ? \n"
            + " GROUP BY t1.invoice_no,t1.id,t1.invoice_date,t1.invoice_amount_net,t1.invoice_entry_date ORDER BY t1.invoice_date DESC \n";

    return AppService.list(main, SalesInvoice.class, sql, new Object[]{customerId, accountGroup.getId(), "%" + filter.toUpperCase() + "%"});
  }

  public static List<Product> selectProductBySalesInvoice(MainView main, SalesInvoice selectedInvoice) {
    String sql = "select t5.id,t5.product_name,t5.hsn_code,t5.product_category_id \n"
            + " from scm_sales_invoice t1,scm_sales_invoice_item t2,scm_product_detail t3,scm_product_preset t4,scm_product t5 \n"
            + "WHERE t1.id = ? \n"
            + "AND t1.id = t2.sales_invoice_id \n"
            + "AND t2.product_detail_id = t3.id \n"
            + "AND t3.product_preset_id = t4.id \n"
            + "AND t4.product_id = t5.id \n"
            + "GROUP BY t5.id,t5.product_name,t5.hsn_code,t5.product_category_id ORDER BY t5.product_name ASC";
    return AppService.list(main, Product.class, sql, new Object[]{selectedInvoice.getId()});
  }

  public static List<ProductEntry> selectInvoiceBySupplier(MainView main, Account account, String filter) {
    AccountGroup accountGroup = AccountGroupService.selectDefaultAccountGroupByAccount(main, account);
    String sql = "select t5.id,t5.invoice_date ,t5.account_invoice_no,t5.invoice_amount\n"
            + "from scm_sales_invoice t1 , scm_sales_invoice_item t2 , scm_product_detail t3, scm_product_entry_detail t4 , scm_product_entry t5\n"
            + "WHERE t1.account_group_id = ? \n"
            + "AND t1.id = t2.sales_invoice_id\n"
            + "AND t2.product_detail_id = t3.id\n"
            + "AND t3.id = t4.product_detail_id\n"
            + "AND t4.product_entry_id = t5.id\n"
            + "AND t5.account_invoice_no IS NOT NULL\n"
            + " AND UPPER(t5.account_invoice_no) like ? \n"
            + "group by t5.id,t5.invoice_date ,t5.account_invoice_no,t5.invoice_amount order by t5.invoice_date desc ";

    return AppService.list(main, ProductEntry.class, sql, new Object[]{accountGroup.getId(), "%" + filter.toUpperCase() + "%"});

  }

  public static List<Product> selectProductBySupplierInvoice(Main main, ProductEntry productEntry) {
    String sql = "SELECT t5.id,t5.product_name,t5.hsn_code,t5.product_category_id \n"
            + "FROM scm_product_entry t1 , scm_product_entry_detail t2, scm_product_detail t3 , scm_product_preset t4 , scm_product t5\n"
            + "WHERE t1.id = ? \n"
            + "AND t1.id = t2.product_entry_id\n"
            + "AND t2.product_detail_id = t3.id\n"
            + "AND t3.product_preset_id = t4.id\n"
            + "AND t4.product_id = t5.id\n"
            + "GROUP BY t5.id,t5.product_name,t5.hsn_code,t5.product_category_id  ORDER BY t5.product_name ASC";
    return AppService.list(main, Product.class, sql, new Object[]{productEntry.getId()});
  }

}
