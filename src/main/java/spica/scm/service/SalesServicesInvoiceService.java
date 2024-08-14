/*
 * @(#)SalesInvoiceServiceService.java	1.0 Wed Jan 31 15:55:51 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingTransaction;
import spica.fin.service.AccountingTransactionService;
import spica.fin.service.TradeOutstandingService;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.SalesServicesInvoice;
import spica.scm.domain.SalesServicesInvoiceItem;
import spica.scm.validate.SalesServicesInvoiceIs;
import spica.sys.SystemConstants;

/**
 * SalesInvoiceServiceService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Jan 31 15:55:51 IST 2018
 */
public abstract class SalesServicesInvoiceService {

  public static final Integer STATUS_DRAFT = 2;
  public static final Integer STATUS_CONFIRMED = 1;
  public static final Integer SALES_INTRASTATE = 1;
  public static final Integer SALES_INTERSTATE = 2;
  public static final Integer SALES_INTERNATIONAL = 3;
  public static final Integer TAXABLE_SALES_SERVICES_INVOICE = 1;
  public static final Integer NON_TAXABLE_SALES_SERVICES_INVOICE = 0;

  /**
   * SalesServicesInvoice paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getSalesServicesInvoiceSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_services_invoice", SalesServicesInvoice.class, main);
    sql.main("select scm_sales_services_invoice.id,scm_sales_services_invoice.company_id,scm_sales_services_invoice.financial_year_id,scm_sales_services_invoice.accounting_ledger_id,scm_sales_services_invoice.entry_date,scm_sales_services_invoice.serial_no,scm_sales_services_invoice.status_id,scm_sales_services_invoice.created_by,scm_sales_services_invoice.modified_by,scm_sales_services_invoice.created_at,scm_sales_services_invoice.modified_at,scm_sales_services_invoice.account_group_id from scm_sales_services_invoice scm_sales_services_invoice "); //Main query
    sql.count("select count(scm_sales_services_invoice.id) as total from scm_sales_services_invoice scm_sales_services_invoice "); //Count query
    sql.join("left outer join scm_company scm_company on (scm_company.id = scm_sales_services_invoice.company_id) "
            + "left outer join fin_accounting_ledger fin_accounting_ledger on (fin_accounting_ledger.id = scm_sales_services_invoice.accounting_ledger_id) "); //Join Query

    sql.string(new String[]{"scm_company.company_name", "fin_accounting_ledger.title", "scm_sales_services_invoice.serial_no", "scm_sales_services_invoice.created_by", "scm_sales_services_invoice.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_services_invoice.id", "scm_sales_services_invoice.status_id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_sales_services_invoice.entry_date", "scm_sales_services_invoice.created_at", "scm_sales_services_invoice.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of SalesServicesInvoice.
   *
   * @param main
   * @return List of SalesServicesInvoice
   */
  public static final List<SalesServicesInvoice> listPaged(Main main) {
    return AppService.listPagedJpa(main, getSalesServicesInvoiceSqlPaged(main));
  }

  public static final List<SalesServicesInvoice> listPagedByCompany(Main main, Company companyId) {
    SqlPage sql = getSalesServicesInvoiceSqlPaged(main);
    sql.cond("where scm_sales_services_invoice.company_id = ?");
    sql.param(companyId.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static final List<SalesServicesInvoice> listPagedByCompanyAndAccountGroup(Main main, Company companyId, AccountGroup accountGroupId) {
    SqlPage sql = getSalesServicesInvoiceSqlPaged(main);
    sql.cond("where scm_sales_services_invoice.company_id = ? AND scm_sales_services_invoice.account_group_id=?");
    sql.param(companyId.getId());
    sql.param(accountGroupId.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of SalesInvoiceService based on condition
//   * @param main
//   * @return List<SalesInvoiceService>
//   */
//  public static final List<SalesInvoiceService> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSalesInvoiceServiceSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select SalesServicesInvoice by key.
   *
   * @param main
   * @param salesServicesInvoice
   * @return SalesServicesInvoice
   */
  public static final SalesServicesInvoice selectByPk(Main main, SalesServicesInvoice salesServicesInvoice) {
    return (SalesServicesInvoice) AppService.find(main, SalesServicesInvoice.class, salesServicesInvoice.getId());
  }

  /**
   * Insert SalesServicesInvoice.
   *
   * @param main
   * @param salesServicesInvoice
   */
  public static final void insert(Main main, SalesServicesInvoice salesServicesInvoice) {
    SalesServicesInvoiceIs.insertAble(main, salesServicesInvoice);  //Validating
    AppService.insert(main, salesServicesInvoice);

  }

  /**
   * Update SalesServicesInvoice by key.
   *
   * @param main
   * @param salesServicesInvoice
   * @return SalesServicesInvoice
   */
  public static final SalesServicesInvoice updateByPk(Main main, SalesServicesInvoice salesServicesInvoice) {
    SalesServicesInvoiceIs.updateAble(main, salesServicesInvoice); //Validating
    return (SalesServicesInvoice) AppService.update(main, salesServicesInvoice);
  }

  /**
   * Insert or update SalesInvoiceService
   *
   * @param main
   * @param salesServicesInvoice
   */
  public static void insertOrUpdate(Main main, SalesServicesInvoice salesServicesInvoice) {
    if (salesServicesInvoice.getId() == null) {
      salesServicesInvoice.setSerialNo(SalesServicesInvoiceService.selectSalesServicesInvoiceNo(main, salesServicesInvoice, true));
      insert(main, salesServicesInvoice);
      updateSalesServicesAccountGroupDocPrefix(main, salesServicesInvoice, true);
    } else {
      if (SystemConstants.CONFIRMED.intValue() == salesServicesInvoice.getStatusId()) {
        salesServicesInvoice.setSerialNo(SalesServicesInvoiceService.selectSalesServicesInvoiceNo(main, salesServicesInvoice, false));
        updateSalesServicesAccountGroupDocPrefix(main, salesServicesInvoice, false);
      }
      updateByPk(main, salesServicesInvoice);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param salesServicesInvoice
   */
  public static void clone(Main main, SalesServicesInvoice salesServicesInvoice) {
    salesServicesInvoice.setId(null); //Set to null for insert
    insert(main, salesServicesInvoice);
  }

  /**
   * Delete SalesInvoiceService.
   *
   * @param main
   * @param salesServicesInvoice
   */
  public static final void deleteByPk(Main main, SalesServicesInvoice salesServicesInvoice) {
    SalesServicesInvoiceIs.deleteAble(main, salesServicesInvoice); //Validation
    deleteSalesServiceInvoiceItemBySalesService(main, salesServicesInvoice.getId());
    AppService.delete(main, SalesServicesInvoice.class, salesServicesInvoice.getId());
  }

  /**
   * Delete Array of SalesInvoiceService.
   *
   * @param main
   * @param salesServicesInvoice
   */
  public static final void deleteByPkArray(Main main, SalesServicesInvoice[] salesServicesInvoice) {
    for (SalesServicesInvoice e : salesServicesInvoice) {
      deleteByPk(main, e);
    }
  }

  public static final String selectSalesServicesInvoiceNo(Main main, SalesServicesInvoice salesServicesInvoice, boolean draft) {
    String prefix = null;
    String draftPrefix = draft ? "DR-" : "";
    if (salesServicesInvoice.getAccountingLedgerId() != null) {
      if (TAXABLE_SALES_SERVICES_INVOICE.intValue() == salesServicesInvoice.getTaxableInvoice()) {
        prefix = draftPrefix + AccountGroupDocPrefixService.getNextSalesServicesPefixSequence(main, salesServicesInvoice.getAccountingLedgerId(), PrefixTypeService.SALES_SERVICES_TAXABLE_PREFIX_ID, salesServicesInvoice.getFinancialYearId());
      } else {
        prefix = draftPrefix + AccountGroupDocPrefixService.getNextSalesServicesPefixSequence(main, salesServicesInvoice.getAccountingLedgerId(), PrefixTypeService.SALES_SERVICES_NON_TAXABLE_PREFIX_ID, salesServicesInvoice.getFinancialYearId());
      }
    }
    return prefix;
  }

  private static final void updateSalesServicesAccountGroupDocPrefix(Main main, SalesServicesInvoice salesServicesInvoice, boolean draft) {
    if (salesServicesInvoice.getAccountingLedgerId() != null) {
      if (TAXABLE_SALES_SERVICES_INVOICE.equals(salesServicesInvoice.getTaxableInvoice())) {
        AccountGroupDocPrefixService.updateSalesServicesPrefixSequence(main, salesServicesInvoice.getAccountingLedgerId(), PrefixTypeService.SALES_SERVICES_TAXABLE_PREFIX_ID, draft, salesServicesInvoice.getFinancialYearId());
      } else {
        AccountGroupDocPrefixService.updateSalesServicesPrefixSequence(main, salesServicesInvoice.getAccountingLedgerId(), PrefixTypeService.SALES_SERVICES_NON_TAXABLE_PREFIX_ID, draft, salesServicesInvoice.getFinancialYearId());
      }
    }
  }

  public static void deleteSalesServiceInvoiceItemBySalesService(Main main, Integer id) {
    AccountingTransaction transaction = (AccountingTransaction) AppService.single(main, AccountingTransaction.class, "select * from fin_accounting_transaction where accounting_entity_type_id = ? and entity_id = ?", new Object[]{AccountingConstant.ACC_ENTITY_SALES_SERVICES_INVOICE.getId(), id});
    if (transaction != null) {
      AccountingTransactionService.deleteByPk(main, transaction);
    }
    TradeOutstandingService.deleteByEntityIdAndEntityType(main, id, AccountingConstant.ACC_ENTITY_SALES_SERVICES_INVOICE.getId());
    // TradeOutstandingTaxService.deleteByEntityAndEntityType(main, id, AccountingConstant.ACC_ENTITY_SALES_SERVICES_INVOICE.getId());
    AppService.deleteSql(main, SalesServicesInvoiceItem.class, " delete from scm_sales_services_invoice_item where sales_services_invoice_id = ? ", new Object[]{id});
  }

  public static void resetToDraftSalesServiceItem(Main main, SalesServicesInvoice salesServicesInvoice) {
    AccountingTransaction transaction = (AccountingTransaction) AppService.single(main, AccountingTransaction.class, "select * from fin_accounting_transaction where accounting_entity_type_id = ? and entity_id = ?", new Object[]{AccountingConstant.ACC_ENTITY_SALES_SERVICES_INVOICE.getId(), salesServicesInvoice.getId()});
    if (transaction != null) {
      AccountingTransactionService.deleteByPk(main, transaction);
    }
  }

}
