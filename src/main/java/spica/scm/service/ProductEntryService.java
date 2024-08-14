/*
 * @(#)ProductEntryService.java	1.0 Thu Sep 08 18:33:22 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.DebitCreditNotePlatform;
import spica.fin.domain.TaxCode;
import spica.fin.service.LedgerExternalDataService;
import spica.fin.service.LedgerExternalService;
import spica.reports.model.CustomerOutstanding;
import spica.scm.common.InvoiceGroup;
import spica.scm.common.ProductInvoiceDetail;
import spica.scm.common.ProductSummary;
import spica.scm.common.RelatedItems;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import spica.scm.domain.ConsignmentCommodity;
import spica.scm.domain.Platform;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductEntryStatus;
import spica.scm.domain.ProductPreset;
import spica.scm.domain.ProductPricelist;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.domain.StockAdjustment;
import spica.scm.domain.StockAdjustmentItem;
import spica.scm.domain.StockNonSaleable;
import spica.scm.domain.StockSaleable;
import spica.scm.domain.StockPreSale;
import spica.scm.domain.TradingVariationLog;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.AppUtil;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.scm.util.ReferenceInvoice;
import spica.scm.validate.ProductEntryIs;
import spica.scm.view.AccountUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.util.StringUtil;

/**
 * ProductEntryService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Sep 08 18:33:22 IST 2016
 */
public abstract class ProductEntryService {

  /**
   * ProductEntry paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProductEntrySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_product_entry", ProductEntry.class, main);
    sql.main("select scm_product_entry.id,scm_product_entry.opening_stock_entry,scm_product_entry.account_id,scm_product_entry.consignment_id,scm_product_entry.contract_id,scm_product_entry.invoice_no,"
            + "scm_product_entry.invoice_date,scm_product_entry.product_entry_date,scm_product_entry.invoice_amount,scm_product_entry.invoice_amount_net,"
            + "scm_product_entry.invoice_amount_assessable,scm_product_entry.invoice_amount_vat,"
            + "scm_product_entry.invoice_amount_cst,scm_product_entry.invoice_amount_ex_duty,scm_product_entry.invoice_amount_discount_value,"
            + "scm_product_entry.invoice_amount_discount_perc,scm_product_entry.is_invoice_discount_to_customer,scm_product_entry.vendor_reserve_percentage,"
            + "scm_product_entry.total_expense_amount,scm_product_entry.credit_period,scm_product_entry.purchase_credit_days,scm_product_entry.purchase_credit_day_appli_from,"
            + "scm_product_entry.sales_credit_days,scm_product_entry.product_entry_status_id,scm_product_entry.note,scm_product_entry.invoice_amount_variation,"
            + "scm_product_entry.line_item_discount,scm_product_entry.invoice_round_off,scm_product_entry.is_tax_code_modified, "
            + "scm_product_entry.account_invoice_no,scm_product_entry.invoice_amount_cgst,scm_product_entry.invoice_amount_sgst,"
            + "scm_product_entry.invoice_amount_igst,scm_product_entry.tax_processor_id,scm_product_entry.record_type,scm_product_entry.confirmed_at,"
            + "scm_product_entry.confirmed_by,scm_product_entry.created_at,scm_product_entry.modified_at,scm_product_entry.created_by,scm_product_entry.modified_by, "
            + "scm_product_entry.special_discount_applicable,scm_product_entry.cash_discount_applicable,scm_product_entry.cash_discount_taxable,scm_product_entry.cash_discount_value,"
            + "scm_product_entry.special_discount_value,scm_product_entry.company_id,scm_product_entry.is_invoice_discount_in_percentage,scm_product_entry.financial_year_id,"
            + "scm_product_entry.reference_no "
            + "from scm_product_entry scm_product_entry "); //Main query
    sql.count("select count(scm_product_entry.id) as total from scm_product_entry scm_product_entry "); //Count query
    sql.join("left outer join scm_account scm_product_entryaccount_id on (scm_product_entryaccount_id.id = scm_product_entry.account_id) "
            + "left outer join scm_consignment scm_product_entryconsignment_id on (scm_product_entryconsignment_id.id = scm_product_entry.consignment_id) "
            + "left outer join scm_contract scm_product_entrycontract_id on (scm_product_entrycontract_id.id = scm_product_entry.contract_id) "
            + "left outer join scm_company scm_company on (scm_company.id = scm_product_entry.company_id) "
            + "left outer join scm_product_entry_status product_entry_status on (product_entry_status.id = scm_product_entry.product_entry_status_id) "); //Join Query

    sql.string(new String[]{"scm_product_entry.note", "scm_product_entryaccount_id.account_code", "scm_product_entryconsignment_id.consignment_no", "scm_product_entrycontract_id.contract_code", "scm_product_entry.invoice_no", "product_entry_status.title", "scm_product_entry.account_invoice_no", "scm_product_entry.reference_no"}); //String search or sort fields
    sql.number(new String[]{"scm_product_entry.id", "scm_product_entry.invoice_amount", "scm_product_entry.invoice_amount_net"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_product_entry.invoice_date", "scm_product_entry.product_entry_date", "scm_product_entry.confirmed_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of ProductEntry.
   *
   * @param main
   * @return List of ProductEntry
   */
  public static final List<ProductEntry> listPaged(Main main) {
    return AppService.listPagedJpa(main, getProductEntrySqlPaged(main));
  }

  public static final List<ProductEntry> listPagedByAccount(Main main, Integer accountId, boolean openingStockEntry, AccountingFinancialYear accountingFinancialYear) {
    SqlPage sql = getProductEntrySqlPaged(main);
    main.clear();
    if (openingStockEntry) {
      sql.cond("where scm_product_entry.account_id = ? and scm_product_entry.opening_stock_entry = ? "
              + " AND scm_product_entry.financial_year_id = ? ");
      sql.param(accountId);
      sql.param(1);
      sql.param(accountingFinancialYear.getId());
      sql.orderBy("scm_product_entry.product_entry_date desc");
    } else {
      sql.cond("where scm_product_entry.account_id = ? and scm_product_entry.opening_stock_entry = ? "
              + " AND scm_product_entry.financial_year_id = ?");
      sql.param(accountId);
      sql.param(0);
      sql.param(accountingFinancialYear.getId());
      sql.orderBy("scm_product_entry.product_entry_date desc");
    }
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of ProductEntry based on condition
//   * @param main
//   * @return List<ProductEntry>
//   */
//  public static final List<ProductEntry> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProductEntrySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select ProductEntry by key.
   *
   * @param main
   * @param productEntry
   * @return ProductEntry
   */
  public static final ProductEntry selectByPk(Main main, ProductEntry productEntry) {
    return (ProductEntry) AppService.find(main, ProductEntry.class, productEntry.getId());
  }

  /**
   * Insert ProductEntry.
   *
   * @param main
   * @param productEntry
   */
  public static final void insert(Main main, ProductEntry productEntry) {
    ProductEntryIs.insertAble(main, productEntry);  //Validating
    AppService.insert(main, productEntry);

  }

  /**
   * Update ProductEntry by key.
   *
   * @param main
   * @param productEntry
   * @return ProductEntry
   */
  public static final ProductEntry updateByPk(Main main, ProductEntry productEntry) {
    ProductEntryIs.updateAble(main, productEntry); //Validating
    return (ProductEntry) AppService.update(main, productEntry);
  }

  /**
   * Insert or update ProductEntry
   *
   * @param main
   * @param productEntry
   * @return
   */
  public static int insertOrUpdate(Main main, ProductEntry productEntry) {
    if (productEntry.getInvoiceAmountDiscountValue() == null && productEntry.getInvoiceAmountDiscountPerc() == null) {
      productEntry.setIsInvoiceDiscountToCustomer(null);
    }

    if (productEntry.getId() == null) {
      insert(main, productEntry);
    } else {
      ProductEntry pe = selectByPk(main, productEntry);
      if (isProductEntryEditable(pe, productEntry)) {
        productEntry.setCreatedAt(pe.getCreatedAt());
        productEntry.setCreatedBy(pe.getCreatedBy());
        if (SystemConstants.CONFIRMED.equals(productEntry.getProductEntryStatusId().getId())) {
          productEntry.setConfirmedAt(new java.util.Date());
          productEntry.setConfirmedBy(main.getAppUser().getLogin());
        }
        updateByPk(main, productEntry);
      } else {
        productEntry = pe;
        return 1;
      }
    }
    return 0;
  }

  /**
   *
   * @param main
   * @param productEntry
   * @param productEntryDetail
   */
  private static void insertOrUpdateProductEntryDetail(Main main, ProductEntry productEntry, ProductEntryDetail productEntryDetail) {
    if (productEntryDetail.getProductBatchId() != null && (productEntryDetail.getProductQuantity() != null) && productEntryDetail.getValueRate() != null && productEntryDetail.getValuePts() != null) {
      productEntryDetail.setProductEntryId(productEntry);
      productEntryDetail.setValuePtsPerProdPiece(productEntryDetail.getValuePts());
      productEntryDetail.setValuePtrPerProdPiece(productEntryDetail.getValuePtr());
//      productEntryDetail.setValueRatePerProdPieceDer(ProductUtil.getProductLandingPrice(productEntryDetail));  Change of logic
      productEntryDetail.setValueRatePerProdPieceDer(ProductUtil.getValueRatePerProdPiece(productEntryDetail));
      productEntryDetail.setLandingPricePerPieceCompany(ProductUtil.getProductCompanyLandingRate(productEntryDetail));
      productEntryDetail.setActualSellingPriceDerived(ProductUtil.getActualSellingPriceDerived(productEntryDetail));
      productEntryDetail.setProductDetailId(ProductDetailService.insertOrUpdate(main, productEntry.getAccountId(), productEntryDetail.getProductBatchId(), productEntryDetail.getProductDetailId()));
      ProductEntryDetailService.insertOrUpdate(main, productEntryDetail);
    } else if (productEntryDetail.getServiceCommodityId() != null && productEntryDetail.getValueRate() != null) {
      productEntryDetail.setProductEntryId(productEntry);
      ProductEntryDetailService.insertOrUpdate(main, productEntryDetail);
    }
  }

  /**
   *
   * @param main
   * @param productEntry
   * @param productEntryDetailList
   * @param taxCalculator
   * @return
   */
  public static int insertOrUpdate(Main main, ProductEntry productEntry, List<ProductEntryDetail> productEntryDetailList, List<ProductEntryDetail> serviceEntryDetailList, TaxCalculator taxCalculator) {
    if (productEntry.getId() == null) {
      insert(main, productEntry);
      for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
        insertOrUpdateProductEntryDetail(main, productEntry, productEntryDetail);
      }
    } else {
      ProductEntry pe = selectByPk(main, productEntry);
      if (isProductEntryEditable(pe, productEntry)) {

        taxCalculator.processProductEntryCalculation(productEntry, productEntryDetailList, serviceEntryDetailList);

        for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
          insertOrUpdateProductEntryDetail(main, productEntry, productEntryDetail);
        }

        if (!StringUtil.isEmpty(serviceEntryDetailList)) {
          for (ProductEntryDetail serviceEntryDetail : serviceEntryDetailList) {
            insertOrUpdateProductEntryDetail(main, productEntry, serviceEntryDetail);
          }
        }

        if (productEntry.getProductEntryStatusId().getId().equals(SystemConstants.CONFIRMED)) {
          main.em().flush();
          productEntry.setConfirmedAt(new java.util.Date());
          productEntry.setConfirmedBy(main.getAppUser().getLogin());

          taxCalculator.productMarginCalculation(main, productEntry, productEntryDetailList);
          main.em().flush();

          LedgerExternalService.saveLedgerTaxCode(main, productEntry.getCompanyId());

          /**
           * insert product stock into stock table.
           */
          StockSaleableService.insertProductEntryItems(main, productEntry);

          /**
           * Inserting/Updating default product Price List
           *
           */
          AccountGroupPriceList accountGroupPriceList = AccountGroupPriceListService.selectDefaultAccountGroupPriceList(main, productEntry.getAccountId());
          productEntry.setAccountGroup(accountGroupPriceList.getAccountGroupId());
          ProductPricelistService.insertOrUpdateProductPriceList(main, productEntry, accountGroupPriceList);

          /**
           * Updating system generated account invoice number.
           */
          String invoiceNumber = "";
          if (StringUtil.isEmpty(productEntry.getAccountInvoiceNo()) && productEntry.getAccountId() != null) {
            if (AccountUtil.isPrimaryVendorAccount(productEntry.getAccountId())) {
              invoiceNumber = AccountGroupDocPrefixService.getProductEntryInvoiceNumber(main, productEntry.getCompanyId(), productEntry.getAccountId(), productEntry.getFinancialYearId());
              AccountGroupDocPrefixService.updateProductEntryPrefixSequence(main, productEntry.getCompanyId(), productEntry.getAccountId(), productEntry.getFinancialYearId());
            } else {
              if (AccountService.INDIVIDUAL_SUPPLIER.equals(productEntry.getAccountId().getPurchaseChannel())) {
                invoiceNumber = AccountGroupDocPrefixService.getProductEntryInvoiceNumber(main, productEntry.getCompanyId(), productEntry.getAccountId(), productEntry.getFinancialYearId());
                AccountGroupDocPrefixService.updateProductEntryPrefixSequence(main, productEntry.getCompanyId(), productEntry.getAccountId(), productEntry.getFinancialYearId());
              } else {
                invoiceNumber = AccountGroupDocPrefixService.getProductEntryInvoiceNumber(main, productEntry.getCompanyId(), productEntry.getAccountId().getAccountId(), productEntry.getFinancialYearId());
                AccountGroupDocPrefixService.updateProductEntryPrefixSequence(main, productEntry.getCompanyId(), productEntry.getAccountId().getAccountId(), productEntry.getFinancialYearId());
              }
            }
            ProductEntryService.updateProductEntryInvoiceNumber(main, productEntry, invoiceNumber);
            productEntry.setAccountInvoiceNo(invoiceNumber);
          }
          if (StringUtil.isEmpty(productEntry.getReferenceNo())) {
            productEntry.setReferenceNo(AccountGroupDocPrefixService.getProductEntryInvoiceNumber(main, productEntry.getCompanyId(), null, productEntry.getFinancialYearId()));
            AccountGroupDocPrefixService.updateProductEntryPrefixSequence(main, productEntry.getCompanyId(), null, productEntry.getFinancialYearId());
          }

          if (productEntry.getOpeningStockEntry() == 0) {
            /**
             * Product Entry Accounting value insertion.
             */
            //LOGGER.info("##### Product Entry Accounting Transaction Started.");
            if (productEntry.getTcsNetValue() != null && productEntry.getTcsNetValue() > 0) {
              if (StringUtil.gtDouble(productEntry.getTcsNetValue(), 0.0)) {

                InvoiceGroup ig = new InvoiceGroup();
                ig.setAssessableValue(productEntry.getInvoiceValue());
                ig.setTaxCode(AccountingConstant.TAX_CODE_TCS);
                Double tcs = productEntry.getTcsNetValue();
                ig.setInvoiceTcsValue(tcs);
                productEntry.getInvoiceGroup().add(ig);
              }
            }
            taxCalculator.savePurchase(main, productEntry);
            //LOGGER.info("##### Product Entry Accounting Transaction Finished.");

            /**
             * Checking invoice amount difference. If Invoice Difference found, it will hit on platform.
             */
            if (productEntry.getInvoiceAmountVariation() != null && productEntry.getInvoiceAmountVariation() != 0) {
              if (productEntry.getInvoiceAmountVariation() < 0) {
                PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.INVOICE_DIFFERENCE, null, Math.abs(productEntry.getInvoiceAmountVariation()), productEntry, null, PlatformService.NORMAL_FUND_STATE);
              } else {
                PlatformService.insertPurchase(main, productEntry.getAccountId(), SystemRuntimeConfig.INVOICE_DIFFERENCE, Math.abs(productEntry.getInvoiceAmountVariation()), null, productEntry, null, PlatformService.NORMAL_FUND_STATE);
              }
            }

            for (ProductEntryDetail productEntryDetail : productEntryDetailList) {
              insertOrUpdateProductEntryDetail(main, productEntry, productEntryDetail);
            }
          }
        }
        updateByPk(main, productEntry);
      } else {
        productEntry = pe;
        return 1;
      }

    }
    return 0;
  }

  /**
   *
   * @param productEntry
   * @param productEntryUpdated
   * @return
   */
  public static boolean isProductEntryEditable(ProductEntry productEntry, ProductEntry productEntryUpdated) {
    if (productEntryUpdated.getModifiedAt() == null) {
      return true;
    } else if (productEntryUpdated.getModifiedAt() != null && productEntry.getModifiedAt() == null) {
      return false;
    } else {
      return isPastModifiedDate(productEntry.getModifiedAt(), productEntryUpdated.getModifiedAt());
    }
  }

  private static boolean isPastModifiedDate(Date currentDate, Date modifiedDate) {
    return !currentDate.after(modifiedDate);
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param productEntry
   */
  public static void clone(Main main, ProductEntry productEntry) {
    productEntry.setId(null); //Set to null for insert
    insert(main, productEntry);
  }

  /**
   * Delete ProductEntry.
   *
   * @param main
   * @param productEntry
   */
  public static final void deleteByPk(Main main, ProductEntry productEntry) {
    ProductEntryIs.deleteAble(main, productEntry); //Validation
    actionResetProductEntryToDraft(main, productEntry);
    referenceErrorMessages(main, productEntry.getId(), productEntry.getInvoiceNo(), SystemConstants.SALES_INVOICE_ITEM);
    AppService.deleteSql(main, ProductEntryDetail.class, "delete from scm_product_entry_detail where product_entry_id = ?", new Object[]{productEntry.getId()});
    AppService.deleteSql(main, ConsignmentCommodity.class, "delete from scm_consignment_commodity where product_entry_id = ?", new Object[]{productEntry.getId()});
    AppService.delete(main, ProductEntry.class, productEntry.getId());
  }

  /**
   * Delete Array of ProductEntry.
   *
   * @param main
   * @param productEntry
   */
//  public static final void deleteByPkArray(Main main, ProductEntry[] productEntry) {
//    for (ProductEntry e : productEntry) {
//      deleteByPk(main, e);
//    }
//  }
  /**
   *
   * @param main
   * @param productEntryId
   * @param lineDiscount
   * @param roundOff
   */
  public static void updateProductEntryRoundOffAndLineDiscount(Main main, Integer productEntryId, String lineDiscount, String roundOff) {
    main.param(StringUtil.isEmpty(lineDiscount) ? null : Integer.parseInt(lineDiscount));
    main.param(StringUtil.isEmpty(roundOff) ? null : Double.parseDouble(roundOff));
    main.param(productEntryId);
    AppService.updateSql(main, ProductEntry.class, "update scm_product_entry set line_item_discount = ?, invoice_round_off = ? where id = ?", false);
    main.clear();
    if ("0".equals(lineDiscount)) {
      main.param(productEntryId);
      AppService.updateSql(main, ProductEntryDetail.class, "update scm_product_entry_detail set discount_value = null, discount_percentage = null, is_product_discount_to_customer = null, discount_pack_unit = null where product_entry_id = ?", false);
    }
    main.clear();
  }

  public static final void updateProductEntryInvoiceNumber(Main main, ProductEntry productEntry, String invoiceNumber) {
    main.clear();
    main.param(invoiceNumber);
    main.param(productEntry.getId());
    AppService.updateSql(main, ProductEntry.class, "update scm_product_entry set account_invoice_no = ? where id = ?", false);
    main.clear();
  }

  public static void updateProductEntryInvoiceDiscountBeneficiary(Main main, ProductEntry productEntry) {
    ProductEntryIs.updateAble(main, productEntry); //Validating
    main.clear();
    main.param(productEntry.getIsInvoiceDiscountToCustomer());
    main.param(productEntry.getId());
    AppService.updateSql(main, ProductEntry.class, "update scm_product_entry set is_invoice_discount_to_customer = ? where id = ?", false);
    main.clear();
  }

  public static void updateProductDiscountBeneficiary(Main main, ProductEntryDetail productEntryDetail) {
    ProductEntryIs.updateAble(main, productEntryDetail.getProductEntryId()); //Validating
    main.clear();
    main.param(productEntryDetail.getIsLineDiscountToCustomer());
    String sql = "update scm_product_entry_detail set is_product_discount_to_customer = ?";
    if (productEntryDetail.getProductDiscountPackUnit() != null) {
      sql += " ,product_discount_pack_unit = ?";
      main.param(productEntryDetail.getProductDiscountPackUnit().getId());
    }
    if (productEntryDetail.getProductDiscountReplacement() != null) {
      sql += "   ,product_discount_replacement = ?";
      main.param(productEntryDetail.getProductDiscountReplacement());
    }
    sql += "  where id = ?";
    main.param(productEntryDetail.getId());
    AppService.updateSql(main, ProductEntry.class, sql, false);
    main.clear();
  }

  public static void updateSchemeDiscountBeneficiary(Main main, ProductEntryDetail productEntryDetail) {
    ProductEntryIs.updateAble(main, productEntryDetail.getProductEntryId()); //Validating
    main.clear();
    main.param(productEntryDetail.getIsSchemeDiscountToCustomer());
    String sql = "update scm_product_entry_detail set is_scheme_discount_to_customer = ?";
    if (productEntryDetail.getSchemeDiscountPackUnit() != null) {
      sql += " ,scheme_discount_pack_unit = ?";
      main.param(productEntryDetail.getSchemeDiscountPackUnit().getId());
    }
    if (productEntryDetail.getSchemeDiscountReplacement() != null) {
      sql += "  ,scheme_discount_replacement = ?";
      main.param(productEntryDetail.getSchemeDiscountReplacement());
    }
    sql += "  where id = ?";
    main.param(productEntryDetail.getId());
    AppService.updateSql(main, ProductEntry.class, sql, false);
    main.clear();
  }

  /**
   * Method to reset confirmed product entry to draft.
   *
   * Releases product quantity from stock saleable and non-saleable. Removes all transaction from accounting traction tables. Removes Price list entries. Updates product entry
   * status to draft.
   *
   * @param main
   * @param productEntry
   */
  public static void actionResetProductEntryToDraft(Main main, ProductEntry productEntry) {
    if (AppService.exist(main, "select '1' from scm_stock_adjustment_item where product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?)",
            new Object[]{productEntry.getId()})) {
      referenceErrorMessages(main, productEntry.getId(), productEntry.getInvoiceNo(), SystemConstants.STOCK_ADJUSTMENT);
    }
    referenceErrorMessages(main, productEntry.getId(), productEntry.getInvoiceNo(), null);
    referenceErrorMessages(main, productEntry.getId(), productEntry.getInvoiceNo(), SystemConstants.PURCHASE_RETURN);
    referenceErrorMessages(main, productEntry.getId(), productEntry.getInvoiceNo(), SystemConstants.SALES);
    AppService.deleteSql(main, StockPreSale.class, "delete from scm_stock_presale where ref_product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?)", new Object[]{productEntry.getId()});
    AppService.deleteSql(main, StockSaleable.class, "delete from scm_stock_saleable where ref_product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?) and stock_adjustment_item_id is not null", new Object[]{productEntry.getId()});
    AppService.deleteSql(main, StockSaleable.class, "delete from scm_stock_saleable where ref_product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?) and stock_movement_item_id is not null", new Object[]{productEntry.getId()});
    AppService.deleteSql(main, StockSaleable.class, "delete from scm_stock_saleable where product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?)", new Object[]{productEntry.getId()});
    AppService.deleteSql(main, StockNonSaleable.class, "delete from scm_stock_non_saleable where ref_product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?)", new Object[]{productEntry.getId()});
    AppService.deleteSql(main, StockNonSaleable.class, "delete from scm_stock_non_saleable where product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?)", new Object[]{productEntry.getId()});
    AppService.deleteSql(main, StockAdjustmentItem.class, "delete from scm_stock_adjustment_item where product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?)", new Object[]{productEntry.getId()});
    AppService.deleteSql(main, StockAdjustment.class, "delete from scm_stock_adjustment where id in (select stock_adjustment_id from scm_stock_adjustment_item where product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?))", new Object[]{productEntry.getId()});
    AppService.deleteSql(main, DebitCreditNotePlatform.class, "delete from fin_debit_credit_note_platform where platform_id in (select id from scm_platform where product_entry_id = ?)", new Object[]{productEntry.getId()});
    AppService.deleteSql(main, Platform.class, "delete from scm_platform where product_entry_id = ?;", new Object[]{productEntry.getId()});

    AppService.deleteSql(main, Platform.class, "delete from scm_platform where product_entry_detail_id in (select id from scm_product_entry_detail "
            + "where product_entry_id = ?)", new Object[]{productEntry.getId()});

    AppService.deleteSql(main, ProductPricelist.class, "delete from scm_product_pricelist where product_entry_detail_id in(select id from scm_product_entry_detail "
            + "where product_entry_id = ?)", new Object[]{productEntry.getId()});

    AppService.deleteSql(main, TradingVariationLog.class, "delete from scm_trading_variation_log where product_entry_detail_id in(select id from scm_product_entry_detail "
            + "where product_entry_id = ?)", new Object[]{productEntry.getId()});

    LedgerExternalDataService.deleteProductEntry(main, productEntry);

    long taxChangeCount = AppService.count(main, "select count(t1.id) from scm_product_entry_detail t1 "
            + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
            + "inner join scm_product_batch t3 on t2.product_batch_id = t3.id "
            + "inner join scm_product t4 on t3.product_id = t4.id "
            + "inner join scm_product_category t5 on t4.product_category_id = t5.id "
            + "inner join scm_tax_code t6 on t5.purchase_tax_code_id = t6.id "
            + "inner join scm_tax_code t7 on t7.id = t1.tax_code_id "
            + "where t1.product_entry_id = ? and t7.rate_percentage != t6.rate_percentage", new Object[]{productEntry.getId()});

    if (taxChangeCount == 0) {
      main.clear();
      main.param(SystemConstants.DRAFT);
      main.param(productEntry.getId());
      AppService.updateSql(main, ProductEntry.class, "update scm_product_entry set product_entry_status_id = ? where id = ?", false);
      main.clear();
    } else {
      main.clear();
      main.param(SystemConstants.DRAFT);
      main.param(productEntry.getId());
      AppService.updateSql(main, ProductEntry.class, "update scm_product_entry set product_entry_status_id = ?, is_tax_code_modified = 1 where id = ?", false);
      main.clear();

      main.param(productEntry.getId());
      AppService.updateSql(main, ProductEntryDetail.class, "update scm_product_entry_detail set is_tax_code_modified = 1 "
              + "where id in(select t1.id from scm_product_entry_detail t1 "
              + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
              + "inner join scm_product_batch t3 on t2.product_batch_id = t3.id "
              + "inner join scm_product t4 on t3.product_id = t4.id "
              + "inner join scm_product_category t5 on t4.product_category_id = t5.id "
              + "inner join scm_tax_code t6 on t5.purchase_tax_code_id = t6.id "
              + "inner join scm_tax_code t7 on t7.id = t1.tax_code_id "
              + "where t1.product_entry_id = ? and t7.rate_percentage != t6.rate_percentage)", false);
      main.clear();
    }

  }

  /**
   *
   * @param main
   * @param account
   */
  public static void resetAllInvoiceEntry(Main main, Account account) {
    List<ProductEntry> list = AppService.list(main, ProductEntry.class, "select * from scm_product_entry where account_id = ? and product_entry_status_id = ? and sales_return_id is null order by id desc", new Object[]{account.getId(), SystemConstants.CONFIRMED});
    list.forEach((ProductEntry productEntry) -> {
      actionResetProductEntryToDraft(main, productEntry);
    });
  }

  public static boolean isProductPresetModified(Main main, ProductEntry productEntry) {
    boolean rvalue = AppService.exist(main, "select case when "
            + "sum((t1.ptr_margin_percentage - t3.ptr_margin_percentage) "
            + "+ (t1.pts_margin_percentage - t3.pts_margin_percentage) "
            + "+ (t1.mrplte_ptr_rate_derivation_criterion - t3.mrplte_ptr_rate_derivation_criterion) "
            + "+ (t1.ptr_pts_rate_derivation_criterion - t3.ptr_pts_rate_derivation_criterion)"
            + "+ (t1.pts_ss_rate_derivation_criterion - t3.pts_ss_rate_derivation_criterion)) != 0 then 1 else null end as ptr_margin_percentage "
            + "from scm_product_entry_detail t1 "
            + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
            + "inner join scm_product_preset t3 on t2.product_preset_id = t3.id "
            + "where t1.product_entry_id = ?", new Object[]{productEntry.getId()});
    return rvalue;
  }

  public static void updatePtrAndPtsValues(Main main, ProductEntry productEntry) {
    ProductPreset productPreset = null;
    TaxCode taxCode = null;
    double mrpLte = 0.0;
    double ptrValue = 0.0;
    double ptsValue = 0.0;
    List<ProductEntryDetail> items = AppService.list(main, ProductEntryDetail.class, "select id,product_detail_id from (select t1.id,t1.product_detail_id, "
            + "case when sum((t1.ptr_margin_percentage - t3.ptr_margin_percentage) "
            + "+ (t1.pts_margin_percentage - t3.pts_margin_percentage) "
            + "+ (t1.mrplte_ptr_rate_derivation_criterion - t3.mrplte_ptr_rate_derivation_criterion) "
            + "+ (t1.ptr_pts_rate_derivation_criterion - t3.ptr_pts_rate_derivation_criterion) "
            + "+ (t1.pts_ss_rate_derivation_criterion - t3.pts_ss_rate_derivation_criterion)) != 0 then 1 else null end as ptr_margin_percentage "
            + "from scm_product_entry_detail t1 "
            + "inner join scm_product_detail t2 on t1.product_detail_id = t2.id "
            + "inner join scm_product_preset t3 on t2.product_preset_id = t3.id "
            + "where t1.product_entry_id = ? group by t1.id,t1.product_detail_id) tab where tab.ptr_margin_percentage = 1;", new Object[]{productEntry.getId()});

    for (ProductEntryDetail productEntryDetail : items) {
      productPreset = productEntryDetail.getProductDetailId().getProductPresetId();
      taxCode = productPreset.getProductId().getProductCategoryId().getPurchaseTaxCodeId();

      if (productEntry.getAccountId().getPtsDerivationCriteria().equals(AccountService.DERIVE_PTS_FROM_MRP_PTR)) {
        mrpLte = ProductUtil.getMrpLteValue(productEntryDetail.getProductDetailId().getProductBatchId().getValueMrp(), taxCode.getRatePercentage());
        ptrValue = MathUtil.roundOff(ProductUtil.getPtrValue(productPreset.getPtrMarginPercentage(), mrpLte, productPreset.getMrpltePtrRateDerivationCriterion()), 2);
        ptsValue = MathUtil.roundOff(ProductUtil.getPtsValue(productPreset.getPtsMarginPercentage(), ptrValue, productPreset.getPtrPtsRateDerivationCriterion()), 2);
        productEntryDetail.setValuePtr(ptrValue);
        productEntryDetail.setValuePts(ptsValue);
      } else {
        productEntryDetail.setValuePts(null);
        ProductUtil.getPtsValueFromRate(productEntry, productEntryDetail);
      }

      main.param(productPreset.getMrpltePtrRateDerivationCriterion());
      main.param(productPreset.getPtrPtsRateDerivationCriterion());
      main.param(productPreset.getPtsSsRateDerivationCriterion());
      main.param(productPreset.getPtrMarginPercentage());
      main.param(productPreset.getPtsMarginPercentage());
      main.param(productEntryDetail.getValuePtr());
      main.param(productEntryDetail.getValuePts());
      main.param(productEntryDetail.getId());
      AppService.updateSql(main, ProductEntryDetail.class, "update scm_product_entry_detail set mrplte_ptr_rate_derivation_criterion = ?,"
              + "ptr_pts_rate_derivation_criterion = ?, pts_ss_rate_derivation_criterion = ?, ptr_margin_percentage = ?,"
              + "pts_margin_percentage = ?, value_ptr = ?, value_pts = ? where id = ?", false);
    }
  }

  public static List<RelatedItems> selectRelatedItemsOfProductEntry(Main main, ProductEntry productEntry) {
    StringBuilder sql = new StringBuilder();
    sql.append("select id,invoice_no, 'Sales Invoice' as title from scm_sales_invoice where id in(select sales_invoice_id from scm_sales_invoice_item "
            + "where product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?)) ");
    sql.append("union ");
    sql.append("select id,invoice_no, 'Purchase Return' as title from scm_purchase_return where id in(select purchase_return_id from scm_purchase_return_item "
            + "where product_entry_detail_id in (select id from scm_product_entry_detail where product_entry_id = ?)) ");
    sql.append("union ");
    sql.append("select id,invoice_no, 'Sales Return' as title from scm_sales_return where id in(select sales_return_id from scm_sales_return_item "
            + "where product_entry_detail_id in (select id from scm_product_entry_detail where product_entry_id = ?)) ");

    return AppDb.getList(main.dbConnector(), RelatedItems.class, sql.toString(), new Object[]{productEntry.getId(), productEntry.getId(), productEntry.getId()});
  }

  /**
   *
   * @param main
   * @param account
   * @param salesReturnId
   * @param openingSalesReturnList
   * @param taxCalculator
   */
  public static void insertSalesReturnInvoiceEntry(Main main, Account account, SalesReturn salesReturnId, List<SalesReturnItem> openingSalesReturnList, TaxCalculator taxCalculator) {
    ProductEntry productEntry = new ProductEntry();
    productEntry.setAccountId(account);
    productEntry.setCompanyId(account.getCompanyId());
    productEntry.setConfirmedAt(new Date());
    productEntry.setConfirmedBy(main.getAppUser().getLogin());
    productEntry.setCreatedBy(main.getAppUser().getLogin());
    productEntry.setInvoiceNo("SRT-" + SystemRuntimeConfig.SDF_YYYY_MM_DD.format(new Date()));
    productEntry.setAccountInvoiceNo(productEntry.getInvoiceNo());
    productEntry.setInvoiceDate(salesReturnId.getEntryDate());
    productEntry.setProductEntryDate(salesReturnId.getEntryDate());
    productEntry.setOpeningStockEntry(SystemConstants.SALES_RETURN_STOCK_ENTRY);
    productEntry.setProductEntryStatusId(new ProductEntryStatus(SystemConstants.CONFIRMED));
    productEntry.setQuantityOrFree(account.getIsSchemeApplicable());
    productEntry.setSalesReturnId(salesReturnId);
    productEntry.setFinancialYearId(salesReturnId.getFinancialYearId());
    insert(main, productEntry);

    ProductEntryDetailService.insertSalesReturnInvoiceEntryItem(main, productEntry, openingSalesReturnList, taxCalculator);
    main.em().flush();

    AccountGroupPriceList accountGroupPriceList = AccountGroupPriceListService.selectDefaultAccountGroupPriceList(main, productEntry.getAccountId());
    ProductPricelistService.insertOrUpdateProductPriceList(main, productEntry, accountGroupPriceList);

  }

  /**
   *
   * @param main
   * @param productEntryDetail
   * @return
   */
  public static List<ProductInvoiceDetail> selectLastFivePurchaseByProductDetail(Main main, ProductEntryDetail productEntryDetail) {
    List<ProductInvoiceDetail> list = null;
    if (productEntryDetail != null && productEntryDetail.getProduct() != null) {
      if (productEntryDetail.getId() != null) {
        list = (AppDb.getList(main.dbConnector(), ProductInvoiceDetail.class, "select invoice_no,invoice_date,value_rate,value_pts,value_ptr,t2.value_mrp,t4.batch_no,t5.account_code,"
                + "t2.landing_price_per_piece_company as land_rate,t2.product_quantity as qty,t2.product_quantity_free as qty_free\n"
                + "from scm_product_entry t1 \n"
                + "inner join scm_product_entry_detail t2 on t1.id = t2.product_entry_id \n"
                + "left join scm_product_detail t3 on t3.id = t2.product_detail_id \n"
                + "left join scm_product_batch t4 on t4.id = t3.product_batch_id \n"
                + "left join scm_account t5 on t5.id=t1.account_id \n"
                + "where t4.product_id=? and t1.product_entry_status_id=? and t1.sales_return_id is null and t1.id !=?  order by t1.id desc limit 5", new Object[]{productEntryDetail.getProduct().getId(),
                  SystemConstants.CONFIRMED, productEntryDetail.getProductEntryId().getId()}));
      } else {
        list = (AppDb.getList(main.dbConnector(), ProductInvoiceDetail.class, "select invoice_no,invoice_date,value_rate,value_pts,value_ptr,t2.value_mrp,t4.batch_no,t5.account_code,"
                + "t2.landing_price_per_piece_company as land_rate,t2.product_quantity  as qty,t2.product_quantity_free  as qty_free \n"
                + "from scm_product_entry t1 \n"
                + "inner join scm_product_entry_detail t2 on t1.id = t2.product_entry_id \n"
                + "left join scm_product_detail t3 on t3.id = t2.product_detail_id \n"
                + "left join scm_product_batch t4 on t4.id = t3.product_batch_id \n"
                + "left join scm_account t5 on t5.id=t1.account_id \n"
                + "where t4.product_id= ? and t1.product_entry_status_id=? and t1.sales_return_id is null order by t1.id desc limit 5", new Object[]{productEntryDetail.getProduct().getId(),
                  SystemConstants.CONFIRMED}));
      }
    }
    return list;
  }

  public static boolean isProductValueMrpChanged(Main main, ProductEntry productEntry) {
    return AppService.exist(main, "select 1 from scm_product_entry_detail where product_entry_id = ? and is_mrp_changed = ?", new Object[]{productEntry.getId(), SystemConstants.DEFAULT});
  }

  public static final Double getOutstandingAmount(Main main, Account account) {
    CustomerOutstanding outstanding = null;
    if (account != null) {
      outstanding = (CustomerOutstanding) AppDb.single(main.dbConnector(), CustomerOutstanding.class, "SELECT COALESCE(debit-credit,0) as oustanding_amount from \n"
              + "(select SUM(t1.debit) as debit, SUM(t1.credit) as credit \n"
              + "from fin_accounting_transaction_detail t1 where accounting_ledger_id in \n"
              + "(select id from fin_accounting_ledger where entity_id = ? and accounting_entity_type_id=?))tab", new Object[]{account.getVendorId().getId(), AccountingConstant.ACC_ENTITY_VENDOR.getId()});
      return outstanding.getOustandingamount();
    }
    return null;
  }

  private static void referenceErrorMessages(Main main, Integer id, String invoiceNo, String type) {
    String existing = "";
    int i = 0;
    String sql = "";
    if (type != null) {
      if (type.equals(SystemConstants.SALES)) {
        sql = "select invoice_no,customer_id from scm_sales_invoice where id in(select sales_invoice_id from scm_sales_invoice_item where id in\n"
                + "(select sales_invoice_item_id from scm_stock_saleable where stock_saleable_id in\n"
                + "(select id from scm_stock_saleable where ref_product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id= ?))))";
      } else if (type.equals(SystemConstants.SALES_INVOICE_ITEM)) {
        sql = "select invoice_no,customer_id from scm_sales_invoice where id in(select sales_invoice_id from scm_sales_invoice_item where product_entry_detail_id in\n"
                + "(select id from scm_product_entry_detail where product_entry_id =?))";
      } else if (type.equals(SystemConstants.STOCK_ADJUSTMENT)) {
        sql = "select reference_no as invoice_no from scm_stock_adjustment where id in"
                + "(select stock_adjustment_id from scm_stock_adjustment_item where product_entry_detail_id in(select id from scm_product_entry_detail where product_entry_id = ?))";
      } else if (type.equals(SystemConstants.PURCHASE_RETURN)) {
        sql = "select invoice_no from scm_purchase_return where id in(select purchase_return_id from scm_purchase_return_item where product_entry_detail_id in"
                + "(select id from scm_product_entry_detail where product_entry_id=?))";
      }
    } else {
      sql = "select invoice_no,customer_id from scm_sales_invoice where id in(select sales_invoice_id from scm_sales_invoice_item where product_entry_detail_id in"
              + "(select id from scm_product_entry_detail where product_entry_id =?))";
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

  public static List<ProductSummary> productSummaryAuto(Main main, Account account, String filter, Integer productEntryId, Integer productDetailId) {
    String condition = "";
    StringBuilder sql = new StringBuilder();
    List<ProductSummary> list;
    filter = "%" + filter + "%";
    List<Object> params = new ArrayList<>();
    params.add(account.getId());
    params.add(account.getVendorId().getId());
    params.add(account.getVendorId().getId());
    params.add(filter);
    params.add(account.getId());
    params.add(account.getId());
    params.add(account.getVendorId().getId());
    params.add(account.getVendorId().getId());
    params.add(productEntryId);

    if (productDetailId != null) {
      //condition = " and t7.id not in(select product_detail_id from scm_product_entry_detail where product_entry_id = ? and product_detail_id <> ?) ";
      condition = " AND scm_product_detail.id != ? ";
      params.add(productDetailId);
    }

    params.add(filter);

    sql.append("SELECT tab.product_id as product_id,tab.product_name from (SELECT prod.id as product_id,prod.product_name\n"
            + "FROM scm_product as prod \n"
            + "INNER JOIN scm_product_preset prod_preset on prod.id = prod_preset.product_id \n"
            + "and prod_preset.account_id = ? INNER JOIN scm_product_category prod_cat on prod.product_category_id = prod_cat.id \n"
            + "INNER JOIN scm_tax_code tax_code on tax_code.id = prod_cat.purchase_tax_code_id \n"
            + "WHERE prod.product_status_id=1 and prod.brand_id IN ( SELECT brand_id FROM  scm_supplier_brand WHERE vendor_id = ?) \n"
            + "AND prod.commodity_id IN(SELECT commodity_id FROM scm_vendor_commodity WHERE vendor_id = ?) \n"
            + "AND UPPER(prod.product_name) LIKE UPPER(?)\n"
            + "UNION ALL \n"
            + "SELECT prod.id as product_id,prod.product_name\n"
            + "FROM scm_product as prod \n"
            + "INNER JOIN scm_product_preset prod_preset on prod.id = prod_preset.product_id and prod_preset.account_id = ? \n"
            + "INNER JOIN scm_product_category prod_cat on prod.product_category_id = prod_cat.id \n"
            + "INNER JOIN scm_tax_code tax_code on tax_code.id = prod_cat.purchase_tax_code_id \n"
            + "INNER JOIN scm_product_batch as prod_batch  ON prod.id = prod_batch.product_id \n"
            + "LEFT JOIN scm_product_detail as prod_det ON prod_batch.id = prod_det.product_batch_id  \n"
            + "AND prod_det.account_id = ? \n"
            + "WHERE prod.product_status_id=1 and brand_id IN( SELECT brand_id FROM  scm_supplier_brand WHERE vendor_id = ? ) \n"
            + "AND prod.commodity_id IN(SELECT commodity_id FROM scm_vendor_commodity WHERE vendor_id = ?) \n"
            + "AND prod_batch.id NOT IN( SELECT scm_product_detail.product_batch_id \n"
            + "from scm_product_entry_detail,scm_product_detail \n"
            + "WHERE  scm_product_detail.id = scm_product_entry_detail.product_detail_id  \n"
            + "AND scm_product_entry_detail.product_entry_id = ? ").append(condition).append(" )  AND UPPER(prod.product_name) LIKE UPPER(?))tab GROUP BY 1,2 ORDER BY 1 ");

    list = AppDb.getList(main.dbConnector(), ProductSummary.class, sql.toString(), params.toArray());

    return list;
  }

  public static ProductEntry getProductEntryByDetailId(Main main, Integer productEntryDetailId) {
    if (productEntryDetailId != null) {
      return (ProductEntry) AppService.single(main, ProductEntry.class, "select * from scm_product_entry where id in"
              + "(select product_entry_id from scm_product_entry_detail where id = ?)", new Object[]{productEntryDetailId});
    }
    return null;
  }

  public static boolean isAccountInvoiceNoUpdateable(Main main, ProductEntry productEntry) {
    if (AppService.exist(main, "select '1' from scm_product_entry where UPPER(account_invoice_no)=? and account_id=? and financial_year_id=?",
            new Object[]{productEntry.getAccountInvoiceNo().trim(), productEntry.getAccountId().getId(), productEntry.getFinancialYearId().getId()})) {
      main.clear();
      main.param(productEntry.getId());
      String currentAccountInvoiceNo = AppService.firstString(main, "select account_invoice_no from scm_product_entry where id =?");
      productEntry.setAccountInvoiceNo(currentAccountInvoiceNo);
      return false;
    }
    return true;
  }

  public static void updateAccountInvoiceNo(Main main, ProductEntry productEntry) {
    if (productEntry.getId() != null && productEntry.getAccountInvoiceNo() != null) {
      main.clear();
      main.param(productEntry.getId());
      String currentAccountInvoiceNo = AppService.firstString(main, "select account_invoice_no from scm_product_entry where id =?");
      main.clear();
      main.param(productEntry.getAccountInvoiceNo());
      main.param(productEntry.getId());
      AppService.updateSql(main, ProductEntry.class, "UPDATE scm_product_entry SET account_invoice_no=? WHERE id =?;", false);
      main.clear();
      main.param(productEntry.getAccountInvoiceNo());
      main.param(productEntry.getId());
      main.param(productEntry.getCompanyId().getId());
      AppService.updateSql(main, AccountingTransaction.class, "UPDATE fin_accounting_transaction SET ref_document_no = ? WHERE accounting_entity_type_id=8 AND "
              + "entity_id=? AND company_id =?;", false);
      AccountingTransaction accountingTransaction = (AccountingTransaction) AppService.single(main, AccountingTransaction.class,
              "select * from fin_accounting_transaction where ref_document_no = ? and accounting_entity_type_id=8 AND entity_id=? AND company_id =?",
              new Object[]{productEntry.getAccountInvoiceNo(), productEntry.getId(), productEntry.getCompanyId().getId()});
      if (accountingTransaction != null) {
        main.clear();
        main.param(productEntry.getAccountInvoiceNo());
        main.param(accountingTransaction.getId());
        AppService.updateSql(main, AccountingTransactionDetail.class, "UPDATE fin_accounting_transaction_detail_item SET document_number = ? WHERE accounting_transaction_detail_id IN\n"
                + "(SELECT id FROM fin_accounting_transaction_detail WHERE accounting_transaction_id=?);", false);
        main.clear();
        main.param(currentAccountInvoiceNo);
        main.param(productEntry.getAccountInvoiceNo());
        main.param(accountingTransaction.getId());
        main.param(accountingTransaction.getId());
        AppService.updateSql(main, AccountingTransaction.class, "UPDATE fin_accounting_transaction SET narration = REPLACE(narration,?,?) WHERE id IN(\n"
                + "SELECT accounting_transaction_id FROM fin_accounting_transaction_detail WHERE id IN(\n"
                + "SELECT transaction_detail_id FROM fin_accounting_transaction_settlement WHERE adjusted_transaction_detail_id IN\n"
                + "(SELECT id FROM fin_accounting_transaction_detail WHERE accounting_transaction_id=?) UNION\n"
                + "SELECT adjusted_transaction_detail_id FROM fin_accounting_transaction_settlement WHERE transaction_detail_id IN\n"
                + "(SELECT id FROM fin_accounting_transaction_detail WHERE accounting_transaction_id=?)));", false);
      }
    }
  }

  public static int getPurchasePrefix(Main main, Company company, AccountingFinancialYear financialYear) {
    return AppService.count(main, "select COALESCE(purchase_prefix_index,0) from scm_product_entry where company_id=? and financial_year_id = ? order by COALESCE(purchase_prefix_index,0) desc limit 1",
            new Object[]{company.getId(), financialYear.getId()}).intValue();
  }
}
