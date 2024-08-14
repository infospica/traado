/*
 * @(#)SalesReturnService.java	1.0 Mon Jan 29 16:45:34 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import spica.fin.domain.DebitCreditNote;
import spica.fin.service.AccountingLedgerTransactionService;
import spica.fin.service.DebitCreditNoteService;
import spica.fin.service.LedgerExternalDataService;
import spica.scm.common.ProductSummary;
import spica.scm.common.RelatedItems;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.Customer;
import spica.scm.domain.Platform;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductPricelist;
import spica.scm.domain.SalesInvoiceItem;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnCreditSettlement;
import spica.scm.domain.SalesReturnItem;
import spica.scm.domain.SalesReturnItemSplit;
import spica.scm.domain.SalesReturnSplit;
import spica.scm.domain.SalesReturnStatus;
import spica.scm.domain.StockNonSaleable;
import spica.scm.domain.StockSaleable;
import spica.scm.domain.TradingVariationLog;
import spica.scm.domain.UserProfile;
import spica.scm.function.SalesReturnConfirmation;
import spica.scm.function.impl.SalesReturnInvoiceWiseSplitConfirmation;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.AppUtil;
import spica.scm.util.MathUtil;
import spica.scm.util.ReferenceInvoice;
import spica.scm.validate.SalesReturnIs;
import spica.scm.view.PopUpView;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.AppLookup;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;
import wawo.entity.util.UniqueCheck;

/**
 * SalesReturnService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jan 29 16:45:34 IST 2018
 */
public abstract class SalesReturnService {

  /**
   * SalesReturn paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesReturnSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_return", SalesReturn.class, main);
    sql.main("select scm_sales_return.id,scm_sales_return.sales_return_type,scm_sales_return.account_invoice_no,scm_sales_return.tax_processor_id,scm_sales_return.company_id,scm_sales_return.is_tax_code_modified,"
            + "scm_sales_return.account_group_id,scm_sales_return.consignment_id,scm_sales_return.customer_id,scm_sales_return.invoice_no,scm_sales_return.invoice_date,"
            + "scm_sales_return.entry_date,scm_sales_return.invoice_amount_goods,scm_sales_return.invoice_amount_assessable,scm_sales_return.invoice_amount_cgst,"
            + "scm_sales_return.invoice_amount_sgst,scm_sales_return.invoice_amount_igst,scm_sales_return.invoice_amount,scm_sales_return.invoice_amount_variation,"
            + "scm_sales_return.invoice_round_off,scm_sales_return.total_expense_amount,scm_sales_return.sales_return_status_id,scm_sales_return.note,"
            + "scm_sales_return.created_at,scm_sales_return.modified_at,scm_sales_return.created_by,scm_sales_return.modified_by,scm_sales_return.confirmed_at,"
            + "scm_sales_return.confirmed_by,scm_sales_return.is_taxable,scm_sales_return.return_split,scm_sales_return.return_split_for_gst_filing from scm_sales_return scm_sales_return "); //Main query
    sql.count("select count(scm_sales_return.id) as total from scm_sales_return scm_sales_return "); //Count query
    sql.join("left outer join scm_tax_processor scm_sales_returntax_processor_id on (scm_sales_returntax_processor_id.id = scm_sales_return.tax_processor_id) left outer join scm_company scm_sales_returncompany_id on (scm_sales_returncompany_id.id = scm_sales_return.company_id) left outer join scm_account_group scm_sales_returnaccount_group_id on (scm_sales_returnaccount_group_id.id = scm_sales_return.account_group_id) left outer join scm_consignment scm_sales_returnconsignment_id on (scm_sales_returnconsignment_id.id = scm_sales_return.consignment_id) left outer join scm_customer scm_sales_returncustomer_id on (scm_sales_returncustomer_id.id = scm_sales_return.customer_id) left outer join scm_sales_return_status scm_sales_returnsales_return_status_id on (scm_sales_returnsales_return_status_id.id = scm_sales_return.sales_return_status_id)"); //Join Query

    sql.string(new String[]{"scm_sales_return.note", "scm_sales_returntax_processor_id.processor_class", "scm_sales_return.account_invoice_no", "scm_sales_returncompany_id.company_name", "scm_sales_returnaccount_group_id.group_name", "scm_sales_returnconsignment_id.consignment_no", "scm_sales_returncustomer_id.customer_name", "scm_sales_return.invoice_no", "scm_sales_returnsales_return_status_id.title", "scm_sales_return.note", "scm_sales_return.created_by", "scm_sales_return.modified_by", "scm_sales_return.confirmed_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_return.id", "scm_sales_return.sales_return_type", "scm_sales_return.invoice_amount_goods", "scm_sales_return.invoice_amount_assessable", "scm_sales_return.invoice_amount_cgst", "scm_sales_return.invoice_amount_sgst", "scm_sales_return.invoice_amount_igst", "scm_sales_return.invoice_amount", "scm_sales_return.invoice_amount_variation", "scm_sales_return.invoice_round_off", "scm_sales_return.total_expense_amount"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_return.invoice_date", "scm_sales_return.entry_date", "scm_sales_return.created_at", "scm_sales_return.modified_at", "scm_sales_return.confirmed_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesReturn.
   *
   * @param main
   * @return List of SalesReturn
   */
  public static final List<SalesReturn> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesReturnSqlPaged(main));
  }

  public static final List<SalesReturn> listPagedByAccountGroup(Main main, Integer companyId, AccountGroup accountGroup, String filter, AccountingFinancialYear accountingFinancialYear) {
    SqlPage sql = getSalesReturnSqlPaged(main);
    if (StringUtil.isEmpty(filter)) {
      sql.cond("where scm_sales_return.company_id = ? and scm_sales_return.account_group_id = ? and scm_sales_return.sales_return_status_id <> ? "
              + " AND scm_sales_return.entry_date::date >= ?::date AND scm_sales_return.entry_date::date <= ?::date ");
      sql.param(companyId);
      sql.param(accountGroup.getId());
      sql.param(SystemConstants.SPLITS);
      sql.param(accountingFinancialYear.getStartDate());
      sql.param(accountingFinancialYear.getEndDate());
      sql.orderBy("scm_sales_return.id desc");
    } else {
      sql.cond("where scm_sales_return.company_id = ? and scm_sales_return.account_group_id = ? and sales_return_type = ? and scm_sales_return.sales_return_status_id <> ? "
              + " AND scm_sales_return.entry_date::date >= ?::date AND scm_sales_return.entry_date::date <= ?::date  ");
      sql.param(companyId);
      sql.param(accountGroup.getId());
      sql.param(Integer.parseInt(filter));
      sql.param(SystemConstants.SPLITS);
      sql.param(accountingFinancialYear.getStartDate());
      sql.param(accountingFinancialYear.getEndDate());
      sql.orderBy("scm_sales_return.id desc");
    }
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of SalesReturn based on condition
//   * @param main
//   * @return List<SalesReturn>
//   */
//  public static final List<SalesReturn> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesReturnSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesReturn by key.
   *
   * @param main
   * @param salesReturn
   * @return SalesReturn
   */
  public static final SalesReturn selectByPk(Main main, SalesReturn salesReturn) {
    return (SalesReturn) AppService.find(main, SalesReturn.class, salesReturn.getId());
  }

  /**
   * Insert SalesReturn.
   *
   * @param main
   * @param salesReturn
   */
  public static final void insert(Main main, SalesReturn salesReturn) {
    SalesReturnIs.insertAble(main, salesReturn);  //Validating
    AppService.insert(main, salesReturn);

  }

  /**
   * Update SalesReturn by key.
   *
   * @param main
   * @param salesReturn
   * @return SalesReturn
   */
  public static final SalesReturn updateByPk(Main main, SalesReturn salesReturn) {
    SalesReturnIs.updateAble(main, salesReturn); //Validating
    return (SalesReturn) AppService.update(main, salesReturn);
  }

  /**
   * Insert or update SalesReturn
   *
   * @param main
   * @param salesReturn
   */
  public static void insertOrUpdate(Main main, SalesReturn salesReturn) {
    if (salesReturn.getId() == null) {
      salesReturn.setInvoiceNo("DR-" + AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesReturn.getAccountGroupId(), PrefixTypeService.SALES_RETURN_PREFIX_ID, true, salesReturn.getFinancialYearId()));
      insert(main, salesReturn);
      AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesReturn.getAccountGroupId(), PrefixTypeService.SALES_RETURN_PREFIX_ID, true, salesReturn.getFinancialYearId());
    } else {
//      if (SystemConstants.CONFIRMED.equals(salesReturn.getSalesReturnStatusId().getId())) {
//        salesReturn.setInvoiceNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesReturn.getAccountGroupId(), PrefixTypeService.SALES_RETURN_PREFIX_ID, false));
//        AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesReturn.getAccountGroupId(), PrefixTypeService.SALES_RETURN_PREFIX_ID, false);
//      }
      updateByPk(main, salesReturn);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesReturn
   */
  public static void clone(Main main, SalesReturn salesReturn) {
    salesReturn.setId(null); //Set to null for insert
    insert(main, salesReturn);
  }

  /**
   * Delete SalesReturn.
   *
   * @param main
   * @param salesReturn
   */
  public static final void deleteByPk(Main main, SalesReturn salesReturn) {
    SalesReturnIs.deleteAble(main, salesReturn); //Validation
    AppService.deleteSql(main, SalesReturnCreditSettlement.class, "delete from scm_sales_return_credit_settlement where sales_return_id=?", new Object[]{salesReturn.getId()});
    List<DebitCreditNote> debitCreditList = main.em().list(DebitCreditNote.class, "select * from fin_debit_credit_note where id in(select debit_credit_note_id from fin_debit_credit_note_item where product_entry_detail_id in \n"
            + "(select id from scm_product_entry_detail where product_entry_id in (select id  from scm_product_entry where sales_return_id = ?) ))", new Object[]{salesReturn.getId()});
    for (DebitCreditNote dcNote : debitCreditList) {
      DebitCreditNoteService.deleteByPk(main, dcNote);
    }
    resetSalesReturnStatusToDraft(main, salesReturn);
    AppService.deleteSql(main, SalesReturn.class, "delete from scm_sales_return_item where sales_return_id=?", new Object[]{salesReturn.getId()});
    AppService.delete(main, SalesReturn.class, salesReturn.getId());
  }

  /**
   * Delete Array of SalesReturn.
   *
   * @param main
   * @param salesReturn
   */
//  public static final void deleteByPkArray(Main main, SalesReturn[] salesReturn) {
//    for (SalesReturn e : salesReturn) {
//      deleteByPk(main, e);
//      try{
//      main.em().commit();
//      } catch(Throwable t){
//        AppUtil.referenceError(main, t, e.getId());
//      }
//    }
//  }
  private static boolean isPersistable(SalesReturnItem salesReturnItem) {
    boolean rvalue = false;
    if (salesReturnItem != null) {
      if (salesReturnItem.getTaxCodeId() != null && (salesReturnItem.getProductQuantity() != null || salesReturnItem.getProductQuantityDamaged() != null)
              && salesReturnItem.getValueRate() != null) {
        rvalue = true;
        if (salesReturnItem.getSalesInvoiceId() == null && salesReturnItem.getExpectedLandingRate() != null) {
          rvalue = true;
        }
//        else {
//          rvalue = false;
//        }
      }
    }
    return rvalue;
  }

  public static void insertOrUpdateSalesReturn(MainView main, SalesReturn salesReturn, List<SalesReturnItem> salesReturnItemList, TaxCalculator taxCalculator) {

    //double difference;
    boolean isRateVerified = false;
    boolean isDamagedReturn = (salesReturn.getSalesReturnType().equals(SystemConstants.SALES_RETURN_TYPE_DAMAGED));
    //ProductPricelist productPricelist = null;

    List<Account> accountList = null;
    Double productQty = null;

    CompanySettings settings = UserRuntimeView.instance().getCompany().getCompanySettings();
    if (salesReturn.getConfirmedAt() == null) {
      if (settings != null && settings.getId() != null) {
        salesReturn.setReturnSplit(settings.getSplitSalesReturn());
        salesReturn.setReturnSplitForGstFiling(settings.getReturnSplitForGstFiling() == null ? SystemConstants.NO : settings.getReturnSplitForGstFiling());
      }
    }
    insertOrUpdate(main, salesReturn);

    if (salesReturnItemList != null) {
      for (SalesReturnItem salesReturnItem : salesReturnItemList) {
        if (isPersistable(salesReturnItem)) {
          isRateVerified = salesReturnItem.getSalesInvoiceId() != null ? true : salesReturnItem.getExpectedLandingRate() != null;
          productQty = isDamagedReturn ? salesReturnItem.getProductQuantityDamaged() : salesReturnItem.getProductQuantity() == null ? null : salesReturnItem.getProductQuantity().doubleValue();
          if (productQty != null && salesReturnItem.getValueRate() != null && isRateVerified) {
            salesReturnItem.setSalesReturnId(salesReturn);
            if (salesReturnItem.getProductDetailId() != null) {
              salesReturnItem.setAccountId(salesReturnItem.getProductDetailId().getAccountId());
            }
//          if (isDamagedReturn) {
//            salesReturnItem.setProductQuantityDamaged(salesReturnItem.getProductQuantity());
//          }
            if (salesReturnItem.getHsnCode() == null) {
              salesReturnItem.setHsnCode(salesReturnItem.getProduct().getHsnCode());
            }
            if (salesReturnItem.getSalesInvoiceId() != null) {
              salesReturnItem.setRefInvoiceNo(salesReturnItem.getSalesInvoiceId().getInvoiceNo());
              salesReturnItem.setRefInvoiceDate(salesReturnItem.getSalesInvoiceId().getInvoiceEntryDate());
            }
            SalesReturnItemService.insertOrUpdate(main, salesReturnItem);
          }
        }
      }
    }

    if (SystemConstants.CONFIRMED.equals(salesReturn.getSalesReturnStatusId().getId())) {
      InvoiceLogService.insertSalesReturnLog(main, salesReturn, salesReturnItemList, InvoiceLogService.CONFIRMED);
      main.em().flush();

      if (salesReturn.getConfirmedAt() == null) {
        salesReturn.setInvoiceNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesReturn.getAccountGroupId(), PrefixTypeService.SALES_RETURN_PREFIX_ID, false, salesReturn.getFinancialYearId()));
        AccountGroupDocPrefixService.updateSalesPrefixSequence(main, salesReturn.getAccountGroupId(), PrefixTypeService.SALES_RETURN_PREFIX_ID, false, salesReturn.getFinancialYearId());
      }

      salesReturn.setConfirmedAt(new java.util.Date());
      salesReturn.setConfirmedBy(main.getAppUser().getLogin());

      List<SalesReturnItem> listSalesReturnItem = confirmSalesReturnItem(main, salesReturn);
      SalesReturnItemService.deleteCreditSettlementBySalesReturn(main, salesReturn);
      AppService.deleteSql(main, SalesReturnItem.class, "delete from scm_sales_return_item where sales_return_id = ? and sales_invoice_id is not null ", new Object[]{salesReturn.getId()});

      //double marginValue = 0.0, vendorMarginValue = 0.0;
      SalesReturnCreditSettlement creditSettlement = null;
      List<SalesReturnCreditSettlement> creditSettlementList = new ArrayList<>();
      String salesReturnItemHash = "";
      int i = 0;
      try (UniqueCheck hash = new UniqueCheck()) {
        for (SalesReturnItem salesReturnItem : listSalesReturnItem) {
          //productPricelist = null;
          salesReturnItem.setAccountId(salesReturnItem.getProductDetailId().getAccountId());
          salesReturnItem.setProductEntryDetailId(salesReturnItem.getSalesInvoiceItemId().getProductEntryDetailId());

          if (isDamagedReturn) {
            if (salesReturnItem.getProductQuantityDamaged() == null && salesReturnItem.getProductQuantity() != null) {
              salesReturnItem.setProductQuantityDamaged(salesReturnItem.getProductQuantity().doubleValue());
            }
          }

          productQty = isDamagedReturn ? salesReturnItem.getProductQuantityDamaged() : salesReturnItem.getProductQuantity();
          salesReturnItem.setReturnRatePerPiece(SalesReturnItemService.findReturnRatePerPiece(salesReturnItem, productQty));

          SalesReturnItemService.insertOrUpdate(main, salesReturnItem);
//        For Credit Settlement-----------------------------------------------------
          if (hash.exist(salesReturnItem.getProductHashCode())) {
            if (salesReturnItem.getSalesReturnCreditSettlement() != null) {
              creditSettlement = new SalesReturnCreditSettlement(salesReturnItem.getSalesReturnCreditSettlement());
              if (creditSettlement != null) {
                creditSettlement.setId(null);
                creditSettlement.setSalesReturnItemId(salesReturnItem);
                salesReturnItemHash += StringUtil.isEmpty(salesReturnItemHash) ? salesReturnItem.getId().toString() : ("#" + salesReturnItem.getId());
                creditSettlementList.add(creditSettlement);
              }
            }
          } else {
            if (!StringUtil.isEmpty(creditSettlementList)) {
              insertCreditSettlementList(main, creditSettlementList, salesReturnItemHash);
              creditSettlementList = new ArrayList<>();
              salesReturnItemHash = "";
            }
            if (salesReturnItem.getSalesReturnCreditSettlement() != null) {
              creditSettlement = new SalesReturnCreditSettlement(salesReturnItem.getSalesReturnCreditSettlement());
              if (creditSettlement != null) {
                creditSettlement.setId(null);
                creditSettlement.setSalesReturnItemId(salesReturnItem);
                salesReturnItemHash += StringUtil.isEmpty(salesReturnItemHash) ? salesReturnItem.getId().toString() : ("#" + salesReturnItem.getId());
                creditSettlementList.add(creditSettlement);
              }
            }
          }

          if (listSalesReturnItem.size() == i + 1) {
            insertCreditSettlementList(main, creditSettlementList, salesReturnItemHash);
            creditSettlementList = new ArrayList<>();
            salesReturnItemHash = "";
          }
          i++;
          //--------------------------------End of Credit Settlement 
        }
      }
      main.em().flush();

      long splitCount = AppService.count(main, "SELECT COUNT(item_count) FROM( "
              + "SELECT DISTINCT CONCAT(ref_invoice_no,ref_invoice_date) AS item_count FROM scm_sales_return_item WHERE sales_return_id  = ? ) AS tab",
              new Object[]{salesReturn.getId()});

      if (salesReturn.getReturnSplit() != null && salesReturn.getReturnSplit().intValue() == SystemConstants.SPLITTABLE_SALES_RETURN && splitCount > 1
              && (salesReturn.getReturnSplitForGstFiling() != null && salesReturn.getReturnSplitForGstFiling().intValue() == SystemConstants.NO)) {
        SalesReturnConfirmation salesReturnConfirmation = new SalesReturnInvoiceWiseSplitConfirmation();
        salesReturnConfirmation.confirmSalesReturn(main, taxCalculator, salesReturn);
        salesReturn.setSalesReturnStatusId(new SalesReturnStatus(SystemConstants.SPLITS));
      } else {
        // Excute below methods, if sales return not splitable     
        PlatformService.salesReturnPlatformEntry(main, salesReturn, listSalesReturnItem);
        insertSalesReturnOpenProductItems(main, salesReturn, taxCalculator, isDamagedReturn);
        main.em().flush();
        StockSaleableService.insertSalesReturnItems(main, salesReturn);
        if (SystemConstants.CONFIRMED.equals(salesReturn.getSalesReturnStatusId().getId())) {
          taxCalculator.saveSalesReturn(main, salesReturn);
        }
        if (salesReturn.getReturnSplitForGstFiling() != null && salesReturn.getReturnSplitForGstFiling().intValue() == SystemConstants.YES) {
          SalesReturnConfirmation salesReturnConfirmation = new SalesReturnInvoiceWiseSplitConfirmation();
          salesReturnConfirmation.confirmSalesReturn(main, taxCalculator, salesReturn);
        }
      }

      // if sliptable - SalesReturnConfirmation a = new SalesReturnInvoiceWiseSplitConfirmation(); a.confirmSalesReturn();
//      List<SalesReturnItem> openingSalesReturnList = null;
//      if (AppService.exist(main, "select 1 from scm_sales_return_item where sales_invoice_id is null", null)) {
//        accountList = AppService.list(main, Account.class, "select * from scm_account where account_status_id = ? and id in (select account_id from scm_account_group_detail where account_group_id = ?)", new Object[]{AccountService.ACTIVE, salesReturn.getAccountGroupId().getId()});
//        for (Account account : accountList) {
//          openingSalesReturnList = AppService.list(main, SalesReturnItem.class, "select * from scm_sales_return_item where sales_invoice_id is null and account_id = ? and sales_return_id = ? ", new Object[]{account.getId(), salesReturn.getId()});
//          if (!StringUtil.isEmpty(openingSalesReturnList)) {
//            ProductEntryService.insertSalesReturnInvoiceEntry(main, account, salesReturn, openingSalesReturnList, taxCalculator);
//            SalesReturnItemService.updateSalesReturnItem(main, openingSalesReturnList, isDamagedReturn);
//          }
//        }
//      }
      // Excute below methods, if sales return not splitable      
      /**
       * insert product stock into stock table.
       */
      //---
      updateByPk(main, salesReturn);

      Jsf.popupForm(AccountingLedgerTransactionService.JOURNAL_POPUP, salesReturn);
    }

  }

  public static void insertSalesReturnOpenProductItems(Main main, SalesReturn salesReturn, TaxCalculator taxCalculator, boolean isDamagedReturn) {
    List<SalesReturnItem> openingSalesReturnList = null;
    List<Account> accountList = null;
    if (AppService.exist(main, "select 1 from scm_sales_return_item where sales_invoice_id is null", null)) {
      accountList = AppService.list(main, Account.class, "select * from scm_account where account_status_id = ? and id in (select account_id from scm_account_group_detail where account_group_id = ?)", new Object[]{AccountService.ACTIVE, salesReturn.getAccountGroupId().getId()});
      for (Account account : accountList) {
        openingSalesReturnList = AppService.list(main, SalesReturnItem.class, "select * from scm_sales_return_item where sales_invoice_id is null and account_id = ? and sales_return_id = ? ", new Object[]{account.getId(), salesReturn.getId()});
        if (!StringUtil.isEmpty(openingSalesReturnList)) {
          ProductEntryService.insertSalesReturnInvoiceEntry(main, account, salesReturn, openingSalesReturnList, taxCalculator);
          SalesReturnItemService.updateSalesReturnItem(main, openingSalesReturnList, isDamagedReturn);
        }
      }
    }
  }

  private static List<SalesReturnItem> confirmSalesReturnItem(Main main, SalesReturn salesReturn) {
    List<SalesReturnItem> salesReturnItemList = new ArrayList<>();
    SalesReturnItem salesReturnItem = null;
    List<SalesInvoiceItem> salesInvoiceItemList = null;
    String sql = "select row_number() OVER () as id,sales_return_id,product_batch_id,product_detail_id,sum(product_quantity) product_quantity,sum(product_quantity_damaged) product_quantity_damaged,"
            + "expected_landing_rate,value_rate,value_mrp,value_pts,value_ptr,tax_code_id,SUM(product_discount_value) as product_discount_value,product_discount_percentage, "
            + "SUM(scheme_discount_value) as scheme_discount_value,SUM(invoice_discount_value) as invoice_discount_value,product_detail_hash, sum(COALESCE(value_goods,0)) as value_goods, "
            + "sum(coalesce(value_cgst,0)) value_cgst,sum(coalesce(value_sgst,0)) value_sgst,sum(coalesce(value_igst,0)) value_igst,sum(coalesce(value_assessable,0)) value_assessable,"
            + "scheme_discount_value_derived,product_discount_value_derived,invoice_discount_value_derived,sales_invoice_id,product_hash_code,ref_invoice_no,ref_invoice_date,is_tax_code_modified,scheme_discount_percentage,credit_settlement_amount,"
            + "string_agg(id::varchar, '#') as sales_return_item_hash,credit_exist,remarks,hsn_code "
            + "from scm_sales_return_item where sales_return_id = ? "
            + "group by sales_return_id,product_batch_id,product_detail_id,credit_exist,tax_code_id,hsn_code,remarks,expected_landing_rate,value_rate,value_mrp,value_pts,value_ptr,tax_code_id,invoice_discount_value,product_detail_hash,credit_settlement_amount, "
            + "scheme_discount_value_derived,product_discount_value_derived,invoice_discount_value_derived,sales_invoice_id,product_hash_code,ref_invoice_no,ref_invoice_date,is_tax_code_modified,scheme_discount_percentage,product_discount_percentage ";

    List<SalesReturnItem> list = AppService.list(main, SalesReturnItem.class, sql, new Object[]{salesReturn.getId()});

    Integer productQty, soldQty;
    Double productDamagedQty;
    Double totalProductQty;
    Double creditSettlementAmount;
    int counter;
    //List<Object> params = new ArrayList<>();
    List<Integer> ids = new ArrayList<>();
    Stream<String> productDetailIdStream = null;
    //StringBuilder sql = new StringBuilder();
    String sqlSalesInvoiceItem;
    for (SalesReturnItem returnItem : list) {
      creditSettlementAmount = returnItem.getCreditSettlementAmount();
      totalProductQty = returnItem.getProductQuantity() != null ? returnItem.getProductQuantity() : returnItem.getProductQuantityDamaged();
      sqlSalesInvoiceItem = "select * from scm_sales_invoice_item item where item.sales_invoice_id = ?";
      //counter = 0;
      productQty = returnItem.getProductQuantity() == null ? 0 : returnItem.getProductQuantity();
      productDamagedQty = returnItem.getProductQuantityDamaged() == null ? 0 : returnItem.getProductQuantityDamaged();
      //For Credit Settlement
      String returnIdForSettlement = returnItem.getSalesReturnItemHash() != null ? returnItem.getSalesReturnItemHash().split("#")[0] : null;
      StringBuilder p = new StringBuilder("");

      ids.clear();
      if (returnItem.getProductDetailHash() != null) {
        productDetailIdStream = Arrays.stream(returnItem.getProductDetailHash().split("#"));

        ids = productDetailIdStream.map(Integer::parseInt).collect(Collectors.toList());
        for (Integer id : ids) {
          if (p.length() == 0) {
            p.append(id);
          } else {
            p.append(",").append(id);
          }
        }
      }
      sqlSalesInvoiceItem += " and item.product_detail_id in (" + p + ")  ";
      productDetailIdStream = null;

      //sql.setLength(0);
      //sql.append("select * from scm_sales_invoice_item where sales_invoice_id = ? and product_detail_id in(");
      //params.add(returnItem.getSalesInvoiceId().getId());
      //for (String productDetailId : returnItem.getProductDetailHash().split("#")) {
//        if (counter == 0) {
//          sql.append("?");
//        } else {
//          sql.append(",?");
//        }
//        counter++;
      //  ids.add(Integer.parseInt(productDetailId));
      //}
      //sql.append(") ");
      if (returnItem.getSalesInvoiceId() != null) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(returnItem.getSalesInvoiceId().getId());
        if (returnItem.getValueRate() != null) {
          params.add(returnItem.getValueRate());
          sqlSalesInvoiceItem += " and item.prod_piece_selling_forced=? ";
        }
        if (returnItem.getSchemeDiscountValueDerived() != null) {
          params.add(MathUtil.roundOff(returnItem.getSchemeDiscountValueDerived(), 1));
          sqlSalesInvoiceItem += " and ROUND(item.scheme_discount_derived,1)=? ";
        } else {
          sqlSalesInvoiceItem += " and item.scheme_discount_derived is null ";
        }
        if (returnItem.getProductDiscountValueDerived() != null) {
          params.add(MathUtil.roundOff(returnItem.getProductDiscountValueDerived(), 1));
          sqlSalesInvoiceItem += " and ROUND(item.product_discount_derived,1)=? ";
        } else {
          sqlSalesInvoiceItem += " and item.product_discount_derived is null ";
        }
        salesInvoiceItemList = AppService.list(main, SalesInvoiceItem.class, sqlSalesInvoiceItem, params.toArray());
        if (salesInvoiceItemList == null || StringUtil.isEmpty(salesInvoiceItemList)) {
          throw new UserMessageException("error.product.missing");
        }
        // salesInvoiceItemList = AppService.list(main, SalesInvoiceItem.class, sql.toString(), params.toArray());
        int i = 1;
        for (SalesInvoiceItem sitem : salesInvoiceItemList) {
          soldQty = sitem.getProductQty() + (sitem.getProductQtyFree() == null ? 0 : sitem.getProductQtyFree());
          if (productQty > 0 || productDamagedQty > 0) {
            salesReturnItem = new SalesReturnItem();
            salesReturnItem.setSalesReturnId(returnItem.getSalesReturnId());
            salesReturnItem.setProductDetailId(sitem.getProductDetailId());
            salesReturnItem.setProductBatchId(returnItem.getProductBatchId());
            salesReturnItem.setHsnCode(returnItem.getHsnCode());
            salesReturnItem.setAccountId(returnItem.getAccountId());
            salesReturnItem.setValueRate(returnItem.getValueRate());
            salesReturnItem.setValuePts(MathUtil.roundOff(returnItem.getValuePts(), 2));
            salesReturnItem.setValuePtr(returnItem.getValuePtr() == null ? null : MathUtil.roundOff(returnItem.getValuePtr(), 2));
            salesReturnItem.setValueMrp(returnItem.getValueMrp());
            salesReturnItem.setRemarks(returnItem.getRemarks());

            salesReturnItem.setRefInvoiceNo(returnItem.getRefInvoiceNo());
            salesReturnItem.setRefInvoiceDate(returnItem.getRefInvoiceDate());

            salesReturnItem.setProductHashCode(returnItem.getProductHashCode());
            salesReturnItem.setProductDetailHash(returnItem.getProductDetailHash());
            salesReturnItem.setCreditExist(returnItem.getCreditExist());
            salesReturnItem.setQuantityOrFree(sitem.getProductEntryDetailId().getProductEntryId().getQuantityOrFree());
            salesReturnItem.setSchemeDiscountValueDerived(sitem.getSchemeDiscountDerived());
            salesReturnItem.setProductDiscountValueDerived(sitem.getProductDiscountDerived());
            Double specialDisc = 0.0;
//-------------------Showing invoice discount + cash discount from sales as invoice_discount_derived (For GECO, must change later)
            if (true) {  //---------ADD required condition later (Should also add condition for the list showing batches)
              if (sitem.getInvoiceDiscountDerived() != null && sitem.getCashDiscountValueDerived() != null) {
                specialDisc = sitem.getInvoiceDiscountDerived() + sitem.getCashDiscountValueDerived();
              } else if (sitem.getInvoiceDiscountDerived() != null && sitem.getCashDiscountValueDerived() == null) {
                specialDisc = sitem.getInvoiceDiscountDerived();
              } else if (sitem.getInvoiceDiscountDerived() == null && sitem.getCashDiscountValueDerived() != null) {
                specialDisc = sitem.getCashDiscountValueDerived();
              }
            } else {
              specialDisc = sitem.getInvoiceDiscountDerived();
            }
            salesReturnItem.setInvoiceDiscountValueDerived(specialDisc);
            salesReturnItem.setSchemeDiscountPercentage(sitem.getSchemeDiscountPercentage());
            salesReturnItem.setProductDiscountPercentage(sitem.getProductDiscountPercentage());
            salesReturnItem.setSalesInvoiceId(returnItem.getSalesInvoiceId());
            salesReturnItem.setSalesInvoiceItemId(sitem);
            salesReturnItem.setTaxCodeId(returnItem.getTaxCodeId());
            salesReturnItem.setIsMrpChanged(returnItem.getIsMrpChanged());
            salesReturnItem.setProductQuantitySold(soldQty.doubleValue());
            if (salesReturn.isLimitReturnQty() || i != salesInvoiceItemList.size()) {
              if (productQty != 0 && productQty <= soldQty) {
                salesReturnItem.setProductQuantity(productQty);
                productQty = 0;
              } else if (productDamagedQty != 0 && productDamagedQty <= soldQty) {
                salesReturnItem.setProductQuantityDamaged(productDamagedQty);
                productDamagedQty = 0.0;
              } else {
                if (productQty != 0) {
                  salesReturnItem.setProductQuantity(soldQty);
                  productQty -= soldQty;
                } else if (productDamagedQty != 0) {
                  salesReturnItem.setProductQuantityDamaged(soldQty.doubleValue());
                  productDamagedQty -= soldQty;
                }
              }
            } else if (i == salesInvoiceItemList.size()) {
              if (productQty != 0) {
                salesReturnItem.setProductQuantity(productQty);
              } else if (productDamagedQty != 0) {
                salesReturnItem.setProductQuantityDamaged(productDamagedQty);
              }
            }
            Double taxRate = sitem.getTaxCodeId().getRatePercentage();
            Double returnQty = salesReturnItem.getProductQuantity() != null ? salesReturnItem.getProductQuantity().doubleValue() : salesReturnItem.getProductQuantityDamaged().doubleValue();
            Double taxableValue = 0.0;
            //-------------------Showing invoice discount + cash discount from sales as invoice_discount_derived (For GECO, must change later)
            if (true) {  //---------ADD required condition later (Should also add condition for the list showing batches)
              taxableValue = sitem.getValueProdPieceSold() * returnQty;
            } else {
              taxableValue = (sitem.getValueProdPieceSold() + (sitem.getCashDiscountValueDerived() != null ? sitem.getCashDiscountValueDerived() : 0)) * returnQty;
            }
            if (sitem.getSchemeDiscountDerived() != null) {
              salesReturnItem.setSchemeDiscountValue(sitem.getSchemeDiscountDerived() * returnQty);
            }
            if (sitem.getProductDiscountDerived() != null) {
              salesReturnItem.setProductDiscountValue(sitem.getProductDiscountDerived() * returnQty);
            }
            if (specialDisc != null && specialDisc != 0) {
              salesReturnItem.setInvoiceDiscountValue(specialDisc * returnQty);
            }
            if (creditSettlementAmount != null) {
              Double currentQty = salesReturnItem.getProductQuantity() != null ? salesReturnItem.getProductQuantity() : salesReturnItem.getProductQuantityDamaged();
              salesReturnItem.setCreditSettlementAmount((currentQty / totalProductQty) * creditSettlementAmount);
              taxableValue -= salesReturnItem.getCreditSettlementAmount();
            }
            salesReturnItem.setValueAssessable(taxableValue);
            if (sitem.getValueCgst() != null) {
              salesReturnItem.setValueSgst(taxableValue * taxRate / 200);
              salesReturnItem.setValueCgst(taxableValue * taxRate / 200);
            }
            salesReturnItem.setValueIgst(taxableValue * taxRate / 100);
            salesReturnItem.setValueGoods(salesReturnItem.getValueRate() * returnQty);
            if (creditSettlementAmount != null && returnIdForSettlement != null) {
              salesReturnItem.setSalesReturnCreditSettlement(SalesReturnItemService.findCreditSettlement(main, new SalesReturnItem(Integer.parseInt(returnIdForSettlement))));
            }
            salesReturnItemList.add(salesReturnItem);
          }
          i++;
        }
      }
    }
    return salesReturnItemList;
  }

  /**
   *
   * @param main
   * @param salesReturn
   */
  public static void resetSalesReturnStatusToDraft(Main main, SalesReturn salesReturn) {
    referenceErrorMessages(main, salesReturn.getId(), salesReturn.getInvoiceNo(), null);
    referenceErrorMessages(main, salesReturn.getId(), salesReturn.getInvoiceNo(), SystemConstants.SALES);
    referenceErrorMessages(main, salesReturn.getId(), salesReturn.getInvoiceNo(), SystemConstants.PURCHASE_RETURN);
    SalesReturnSplitService.deleteReturnSplitList(main, salesReturn);
    AppService.deleteSql(main, StockNonSaleable.class, "delete from scm_stock_non_saleable where sales_return_item_id in (select id from scm_sales_return_item where sales_return_id = ?) ",
            new Object[]{salesReturn.getId()});

    AppService.deleteSql(main, StockSaleable.class, "delete from scm_stock_saleable where sales_return_item_id in (select id from scm_sales_return_item where sales_return_id = ?) ", new Object[]{salesReturn.getId()});

    AppService.deleteSql(main, Platform.class, "delete from scm_platform where sales_return_item_id in(select id from scm_sales_return_item where sales_return_id = ?)", new Object[]{salesReturn.getId()});

    AppService.deleteSql(main, Platform.class, "delete from scm_platform where sales_return_id = ?", new Object[]{salesReturn.getId()});

    main.clear();
    main.param(salesReturn.getId());
    AppService.updateSql(main, SalesReturnItem.class, "update scm_sales_return_item set product_entry_detail_id = null where sales_return_id = ?", false);
    main.clear();

    AppService.deleteSql(main, ProductPricelist.class, "delete from scm_product_pricelist where product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id in (select id from scm_product_entry where sales_return_id = ?))", new Object[]{salesReturn.getId()});

    referenceErrorMessages(main, salesReturn.getId(), salesReturn.getInvoiceNo(), SystemConstants.PRODUCT_ENTRY);
    AppService.deleteSql(main, TradingVariationLog.class, "delete from scm_trading_variation_log where product_entry_detail_id in(select id from scm_product_entry_detail "
            + "where product_entry_id in (select id from scm_product_entry where sales_return_id = ?))", new Object[]{salesReturn.getId()});
    AppService.deleteSql(main, ProductEntryDetail.class, "delete from scm_product_entry_detail where product_entry_id in (select id from scm_product_entry where sales_return_id = ?)", new Object[]{salesReturn.getId()});

    AppService.deleteSql(main, ProductEntry.class, "delete from scm_product_entry where sales_return_id = ?", new Object[]{salesReturn.getId()});

    LedgerExternalDataService.deleteSalesReturn(main, salesReturn);

    long taxChangeCount = AppService.count(main, "select count(t1.id) from scm_sales_return_item t1 "
            + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
            + "inner join scm_product_batch t3 on t2.product_batch_id = t3.id "
            + "inner join scm_product t4 on t3.product_id = t4.id "
            + "inner join scm_product_category t5 on t4.product_category_id = t5.id "
            + "inner join scm_tax_code t6 on t5.purchase_tax_code_id = t6.id "
            + "inner join scm_tax_code t7 on t7.id = t1.tax_code_id "
            + "where t1.sales_return_id = ? and t7.rate_percentage != t6.rate_percentage", new Object[]{salesReturn.getId()});

    if (taxChangeCount == 0) {
      main.clear();
      main.param(SystemConstants.DRAFT);
      main.param(salesReturn.getId());
      AppService.updateSql(main, SalesReturn.class, "update scm_sales_return set sales_return_status_id = ? where id = ?", false);
      main.clear();
    } else {
      main.clear();
      main.param(SystemConstants.DRAFT);
      main.param(salesReturn.getId());
      AppService.updateSql(main, SalesReturn.class, "update scm_sales_return set sales_return_status_id = ?, is_tax_code_modified = 1 where id = ?", false);
      main.clear();

      main.param(salesReturn.getId());
      AppService.updateSql(main, SalesReturnItem.class, "update scm_sales_return_item set is_tax_code_modified = 1 "
              + "where id in(select t1.id from scm_sales_return_item t1 "
              + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
              + "inner join scm_product_batch t3 on t2.product_batch_id = t3.id "
              + "inner join scm_product t4 on t3.product_id = t4.id "
              + "inner join scm_product_category t5 on t4.product_category_id = t5.id "
              + "inner join scm_tax_code t6 on t5.purchase_tax_code_id = t6.id "
              + "inner join scm_tax_code t7 on t7.id = t1.tax_code_id "
              + "where t1.sales_return_id = ? and t7.rate_percentage != t6.rate_percentage)", false);
      main.clear();
    }

  }

  /**
   *
   * @param main
   * @param accountGroup
   */
  public static void resetAllSalesReturnToDraft(Main main, AccountGroup accountGroup) {
    List<SalesReturn> list = AppService.list(main, SalesReturn.class, "select * from scm_sales_return where account_group_id = ? and sales_return_status_id >= ?", new Object[]{accountGroup.getId(), SystemConstants.CONFIRMED});
    list.forEach((SalesReturn salesReturn) -> {
      resetSalesReturnStatusToDraft(main, salesReturn);
    });
  }

  public static List<RelatedItems> selectRelatedItemsOfSalesReturn(Main main, SalesReturn salesReturn) {
    StringBuilder sql = new StringBuilder();
    sql.append("select id, invoice_no, 'Sales Invoice' as title from scm_sales_invoice where id in(select sales_invoice_id from scm_sales_return_item where sales_return_id = ?)");
    sql.append("union ");
    sql.append("select id,invoice_no, 'Invoice Entry' as title from scm_product_entry where id in(select product_entry_id from scm_product_entry_detail "
            + "where id in(select product_entry_detail_id from scm_sales_return_item where sales_return_id = ?))");

    return AppDb.getList(main.dbConnector(), RelatedItems.class, sql.toString(), new Object[]{salesReturn.getId(), salesReturn.getId()});
  }

  /**
   *
   * @param salesReturn
   * @param filter
   * @return
   */
  public static List<ProductSummary> lookupProductBatch(Main main, SalesReturn salesReturn, String filter) {
    filter = "%" + filter + "%";
    /*return AppDb.getList(main.dbConnector(), ProductSummary.class, "select t1.id as product_id,t1.product_name,t2.batch_no as batch,"
            + "to_char(t2.expiry_date_actual, 'Mon-yy') as expiry_date,t2.value_mrp as mrp_value,t2.id as product_batch_id, "
            + "null as product_preset_id, null as account_code from scm_product t1 "
            + "inner join scm_product_batch t2 on t2.product_id = t1.id "
            + "where upper(t1.product_name) like upper(?) and t1.id in( "
            + "select product_id from scm_product_batch where id in(select product_batch_id from scm_product_detail where account_id in( "
            + "select account_id from scm_account_group_detail where account_group_id = ?))) "
            + "union all "
            + "select t1.id as product_id,t1.product_name,null as batch, null as expiry_date, null as mrp_value, null as product_batch_id, t2.id as product_preset_id,t3.account_code "
            + "from scm_product t1 "
            + "inner join scm_product_preset t2 on t2.product_id = t1.id "
            + "inner join scm_account t3 on t3.id = t2.account_id "
            + "where t1.id in(select product_id from scm_product_preset where account_id in(select account_id from scm_account_group_detail where account_group_id = ?)) "
            + "and upper(t1.product_name) like upper(?) ", new Object[]{filter, salesReturn.getAccountGroupId().getId(), salesReturn.getAccountGroupId().getId(), filter});*/
    return AppDb.getList(main.dbConnector(), ProductSummary.class, "select t1.id as product_id,t1.product_name,t2.batch_no as batch,\n"
            + "to_char(t2.expiry_date_actual, 'Mon-yy') as expiry_date,t2.value_mrp as mrp_value,t2.id as product_batch_id,\n"
            + "null as product_preset_id, null as account_code from scm_product t1 \n"
            + "inner join scm_product_batch t2 on t2.product_id = t1.id \n"
            + "where upper(t1.product_name) like upper(?) and t1.id in( \n"
            + "select product_id from scm_product_preset where account_id in( \n"
            + "select account_id from scm_account_group_detail where account_group_id = ?)) \n"
            + "AND t1.brand_id IN(SELECT brand_id FROM scm_account_group_brands WHERE account_group_id = ?)	\n"
            + "union  \n"
            + "select t1.id as product_id,t1.product_name,null as batch, null as expiry_date, null as mrp_value, \n"
            + "null as product_batch_id, t2.id as product_preset_id,t3.account_code \n"
            + "from scm_product t1 \n"
            + "inner join scm_product_preset t2 on t2.product_id = t1.id \n"
            + "inner join scm_account t3 on t3.id = t2.account_id \n"
            + "where t1.id in(select product_id from scm_product_preset where account_id \n"
            + "in(select account_id from scm_account_group_detail where account_group_id = ?))\n"
            + "    AND t1.brand_id IN(SELECT brand_id FROM scm_account_group_brands WHERE account_group_id = ?)\n"
            + "and upper(t1.product_name) like upper(?)", new Object[]{filter, salesReturn.getAccountGroupId().getId(), salesReturn.getAccountGroupId().getId(), salesReturn.getAccountGroupId().getId(), salesReturn.getAccountGroupId().getId(), filter});

  }

  public static boolean isProductValueMrpChanged(Main main, SalesReturn salesReturn) {
    return AppService.exist(main, "select 1 from scm_sales_return_item where sales_return_id = ? and is_mrp_changed = ?", new Object[]{salesReturn.getId(), SystemConstants.DEFAULT});
  }

  /**
   *
   * @param main
   * @param id
   * @return
   */
  private static void referenceErrorMessages(Main main, Integer id, String invoiceNo, String type) {
    String existing = "";
    int i = 0;
    String sql = "";
    if (type != null) {
      if (type.equals(SystemConstants.SALES)) {
        sql = "select * from scm_sales_invoice where id in"
                + "(select sales_invoice_id from scm_sales_invoice_item where id in\n"
                + "(select sales_invoice_item_id from scm_stock_saleable where stock_saleable_id in\n"
                + "(select id from scm_stock_saleable where sales_return_item_id in(select id from scm_sales_return_item where sales_return_id= ?))))";
      } else if (type.equals(SystemConstants.PRODUCT_ENTRY)) {
        sql = "select * from scm_sales_invoice where id in(select sales_invoice_id from scm_sales_invoice_item where id in\n"
                + "(select id from scm_sales_invoice_item where product_entry_detail_id in (select id from scm_product_entry_detail where product_entry_id in \n"
                + "(select id from scm_product_entry where sales_return_id = ?))))";
      } else if (type.equals(SystemConstants.PURCHASE_RETURN)) {
        sql = "select * from scm_purchase_return where id in(select purchase_return_id \n"
                + "from scm_purchase_return_item where stock_non_saleable_id in\n"
                + "(select id from scm_stock_non_saleable where sales_return_item_id in \n"
                + "(select id from scm_sales_return_item where sales_return_id = ?)))";
      }
    } else {
      sql = "select invoice_no from scm_purchase_return where id in(select purchase_return_id from scm_purchase_return_item where product_entry_detail_id in"
              + "(select id from scm_product_entry_detail where sales_return_item_id in(select id from scm_sales_return_item where sales_return_id =?)))";
    }

    List<ReferenceInvoice> exist = (List<ReferenceInvoice>) AppDb.getList(main.dbConnector(), ReferenceInvoice.class, sql, new Object[]{id});

    for (ReferenceInvoice t : exist) {
      if (i > 0) {
        existing += ",";
      }
      existing += t.getInvoiceNo();
      i++;
    }
    AppUtil.referenceError(main, invoiceNo, existing);

  }

  public static boolean isSalesReturnEditable(MainView main, SalesReturn updatedSalesReturn) {
    SalesReturn salesReturn = selectByPk(main, updatedSalesReturn);
    if (salesReturn.getModifiedAt() == null) {
      return true;
    } else if (salesReturn.getModifiedAt() != null && updatedSalesReturn.getModifiedAt() == null) {
      return false;
    } else {
      return isPastModifiedDate(salesReturn.getModifiedAt(), updatedSalesReturn.getModifiedAt());
    }
  }

  private static boolean isPastModifiedDate(Date currentDate, Date modifiedDate) {
    return !currentDate.after(modifiedDate);
  }

  public static List<String> getSalesInvoiceList(Main main, SalesReturn salesReturn) {
    if (salesReturn != null && salesReturn.getCustomerId() != null) {
      String sql = "select invoice_no from scm_sales_invoice where customer_id= ? and account_group_id = ?";
      List<String> salesInvoiceList = AppDb.listFirst(main.dbConnector(), sql, new Object[]{salesReturn.getCustomerId().getId(), salesReturn.getAccountGroupId().getId()});
      return salesInvoiceList;
    }
    return null;
  }

  public static List<ProductSummary> lookupProduct(Main main, SalesReturn salesReturn, String filter) {
    filter = "%" + filter + "%";
    /*return AppDb.getList(main.dbConnector(), ProductSummary.class, "select t1.id as product_id,t1.product_name,t2.batch_no as batch,"
            + "to_char(t2.expiry_date_actual, 'Mon-yy') as expiry_date,t2.value_mrp as mrp_value,t2.id as product_batch_id, "
            + "null as product_preset_id, null as account_code from scm_product t1 "
            + "inner join scm_product_batch t2 on t2.product_id = t1.id "
            + "where upper(t1.product_name) like upper(?) and t1.id in( "
            + "select product_id from scm_product_batch where id in(select product_batch_id from scm_product_detail where account_id in( "
            + "select account_id from scm_account_group_detail where account_group_id = ?))) "
            + "union all "
            + "select t1.id as product_id,t1.product_name,null as batch, null as expiry_date, null as mrp_value, null as product_batch_id, t2.id as product_preset_id,t3.account_code "
            + "from scm_product t1 "
            + "inner join scm_product_preset t2 on t2.product_id = t1.id "
            + "inner join scm_account t3 on t3.id = t2.account_id "
            + "where t1.id in(select product_id from scm_product_preset where account_id in(select account_id from scm_account_group_detail where account_group_id = ?)) "
            + "and upper(t1.product_name) like upper(?) ", new Object[]{filter, salesReturn.getAccountGroupId().getId(), salesReturn.getAccountGroupId().getId(), filter});*/
    return AppDb.getList(main.dbConnector(), ProductSummary.class, "select t1.id as product_id,t1.product_name\n"
            + " from scm_product t1 \n"
            + "inner join scm_product_batch t2 on t2.product_id = t1.id \n"
            + "where upper(t1.product_name) like upper(?) and t1.id in( \n"
            + "select product_id from scm_product_preset where account_id in( \n"
            + "SELECT account_id FROM scm_account_group_detail WHERE account_group_id = ? UNION \n"
            + "SELECT scm_account.id  FROM scm_account_group_detail,scm_account WHERE \n"
            + "scm_account_group_detail.account_id= scm_account.account_id AND  account_group_id = ?\n"
            + ")) \n"
            + "AND t1.brand_id IN(SELECT brand_id FROM scm_account_group_brands WHERE account_group_id = ?)	\n"
            + "group by 1,2 \n"
            + "union  \n"
            + "select t1.id as product_id,t1.product_name\n"
            + "from scm_product t1 \n"
            + "inner join scm_product_preset t2 on t2.product_id = t1.id \n"
            + "inner join scm_account t3 on t3.id = t2.account_id \n"
            + "where t1.id in(select product_id from scm_product_preset where account_id \n"
            + "in(\n"
            + "SELECT account_id FROM scm_account_group_detail WHERE account_group_id = ? UNION \n"
            + "SELECT scm_account.id  FROM scm_account_group_detail,scm_account WHERE \n"
            + "scm_account_group_detail.account_id= scm_account.account_id AND  account_group_id = ?\n"
            + "))\n"
            + "    AND t1.brand_id IN(SELECT brand_id FROM scm_account_group_brands WHERE account_group_id = ?)\n"
            + "and upper(t1.product_name) like upper(?)\n"
            + "group by 1,2 order by 2\n"
            + "", new Object[]{filter, salesReturn.getAccountGroupId().getId(), salesReturn.getAccountGroupId().getId(), salesReturn.getAccountGroupId().getId(), salesReturn.getAccountGroupId().getId(), salesReturn.getAccountGroupId().getId(), salesReturn.getAccountGroupId().getId(), filter});

  }

  public static String updateRefDateFromRefNo(Main main, SalesReturn salesReturn) {
    String date = null;
    if (salesReturn != null && salesReturn.getReferenceNo() != null) {
      main.clear();
      main.param(salesReturn.getReferenceNo());
      date = AppService.firstString(main, "select invoice_date::date from scm_sales_invoice where invoice_no = ?");
    }
    return date;
  }

  public static boolean isNegativeStock(Main main, SalesReturn salesReturn) {
    if (salesReturn.getSalesReturnType().intValue() == SystemConstants.SALES_RETURN_TYPE_SALEABLE) {
      if (AppService.exist(main, "select '1' from scm_sales_return_item where sales_return_id = ? \n"
              + "AND (select SUM(COALESCE(quantity_in,0))-SUM(COALESCE(quantity_out,0)) from \n"
              + "scm_stock_saleable where ref_product_entry_detail_id = scm_sales_return_item.product_entry_detail_id)-COALESCE(product_quantity,product_quantity_damaged)<0\n"
              + "", new Object[]{salesReturn.getId()})) {
        return true;
      }
    } else if (salesReturn.getSalesReturnType().intValue() == SystemConstants.SALES_RETURN_TYPE_DAMAGED) {
      if (AppService.exist(main, "select '1' from scm_sales_return_item where sales_return_id = ? \n"
              + "AND (select SUM(COALESCE(quantity_in,0))-SUM(COALESCE(quantity_out,0)) from \n"
              + "scm_stock_non_saleable where ref_product_entry_detail_id = scm_sales_return_item.product_entry_detail_id)-COALESCE(product_quantity,product_quantity_damaged)<0\n"
              + "", new Object[]{salesReturn.getId()})) {
        return true;
      }
    }
    return false;
  }

  public static List<ReferenceInvoice> getCreditSettlementList(Main main, SalesReturn salesReturn, SalesReturnItem salesReturnItem) {
    List<ReferenceInvoice> list = null;
    if (salesReturnItem != null && salesReturnItem.getSalesInvoiceId() != null) {
      list = AppDb.getList(main.dbConnector(), ReferenceInvoice.class, "SELECT debi_credit_id as debit_credit_id,debi_credit_item_id as debit_credit_note_item_id,"
              + "* FROM getCustomerCreditSummary(?,?,?,?,?,?) ",
              new Object[]{salesReturn.getCompanyId().getId(), salesReturn.getAccountGroupId().getId(),
                salesReturn.getCustomerId().getId(), salesReturnItem.getProduct().getId(), salesReturnItem.getId() != null ? salesReturnItem.getId() : null,
                SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesReturn.getEntryDate())});
    }
    return list;
  }

  private static void insertCreditSettlementList(Main main, List<SalesReturnCreditSettlement> returnCreditSettlementList, String salesReturnItemHash) {
    for (SalesReturnCreditSettlement creditSettlement : returnCreditSettlementList) {
      creditSettlement.setSalesReturnItemHash(salesReturnItemHash);
      SalesReturnItemService.insertOrUpdateCreditSettlement(main, creditSettlement);
    }
  }

  public static boolean isLimitReturnQty(Main main, AccountGroup accountGroup) {
    long count = 0;
    count = AppService.count(main, "select sum(t1.limit_return_by_sales) pts from scm_account t1 inner join scm_account_group_detail t2 on t1.id = t2.account_id and t2.account_group_id = ?", new Object[]{accountGroup.getId()});
    return count > 0;
  }
}
