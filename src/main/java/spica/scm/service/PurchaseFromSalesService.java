/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.SalesInvoice;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;

/**
 *
 * @author godson
 */
public class PurchaseFromSalesService {

  private static SqlPage getPurchaseFromSalesSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_sales_invoice", SalesInvoice.class, main);
    sql.main("select scm_sales_invoice.id,scm_sales_invoice.invoice_no,scm_sales_invoice.invoice_date,scm_sales_invoice.customer_id,scm_sales_invoice.account_group_id,\n"
            + "scm_sales_invoice.invoice_amount_net,scm_sales_invoice.invoice_amount_igst,invoice_amt_disc_percent,scm_sales_invoice.invoice_amount,"
            + "scm_sales_invoice.is_tax_code_modified,scm_sales_invoice.sales_invoice_status_id,scm_sales_invoice.invoice_entry_date,"
            + "scm_sales_invoice.cash_discount_applicable,scm_sales_invoice.cash_discount_taxable,scm_sales_invoice.cash_discount_value,scm_sales_invoice.tcs_net_value,"
            + "scm_sales_invoice.invoice_amount_discount,scm_sales_invoice.account_group_price_list_id "
            + " from scm_sales_invoice"); //Main query  
    sql.count("select count(scm_sales_invoice.id) from scm_sales_invoice scm_sales_invoice "); //Count query
    sql.string(new String[]{"scm_sales_invoice.invoice_no"}); //String search or sort fields
    sql.number(new String[]{"scm_sales_invoice.id,scm_sales_invoice.invoice_amount_net,scm_sales_invoice.invoice_amount_gst,scm_sales_invoice.invoice_amount"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  public static final List<SalesInvoice> listPaged(Main main, ProductEntry productEntry, AccountingFinancialYear accountingFinancialYear) {
    if (productEntry.getAccountId().getVendorId().getIsCompany() != null && productEntry.getAccountId().getVendorId().getIsCompany().intValue() == 1) {
      SqlPage sql = getPurchaseFromSalesSqlPaged(main);
      main.clear();
      sql.join("join scm_customer scm_customer on  scm_customer.id=scm_sales_invoice.customer_id ");
      sql.cond("where scm_customer.is_company=1 and scm_customer.gst_no=?  "
              + "and scm_sales_invoice.invoice_entry_date::date<=?::date and scm_sales_invoice.invoice_entry_date::date>=(?::date - INTERVAL '45 days') "
              + "and sales_invoice_status_id >1 "
              + "and UPPER(scm_sales_invoice.invoice_no) not in(select UPPER(invoice_no) from scm_product_entry where account_id=? and financial_year_id = ? and product_entry_status_id=2)");
      sql.param(productEntry.getCompanyId().getGstNo());
      sql.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(productEntry.getProductEntryDate()));
      sql.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(productEntry.getProductEntryDate()));
      sql.param(productEntry.getAccountId().getId());
      sql.param(accountingFinancialYear.getId());

      return AppService.listPagedJpa(main, sql);
    } else {
      return null;
    }
  }

  public static Product productInCurrentComapny(Main main, Company company, Product product) {
    return (Product) AppService.single(main, Product.class, "select * from scm_product where company_id=? and product_name=? and product_status_id=1", new Object[]{company.getId(), product.getProductName()});
  }

  public static ProductBatch findorCreateBatch(Main main, Product product, String batchNo, Date expiryDate, Double valueMrp, Integer existingProductId) {
    ProductBatch batch = null;
    if (product != null && batchNo != null && expiryDate != null && valueMrp != null) {
      batch = (ProductBatch) AppService.single(main, ProductBatch.class, "select * from scm_product_batch where product_id = ? and batch_no=? and expiry_date_actual::date=?::date and value_mrp=? limit 1",
              new Object[]{product.getId(), batchNo, SystemRuntimeConfig.SDF_YYYY_MM_DD.format(expiryDate), valueMrp});
    }
    if (batch == null) {
      batch = new ProductBatch();
      ProductBatch oldBatch = (ProductBatch) AppService.single(main, ProductBatch.class, "select * from scm_product_batch where product_id = ? and batch_no=? and expiry_date_actual::date=?::date and value_mrp=? limit 1",
              new Object[]{existingProductId, batchNo, SystemRuntimeConfig.SDF_YYYY_MM_DD.format(expiryDate), valueMrp});
      batch.setBatchNo(oldBatch.getBatchNo());
      batch.setExpiryDateActual(oldBatch.getExpiryDateActual());
      batch.setExpiryDateSales(oldBatch.getExpiryDateSales());
      batch.setValueMrp(oldBatch.getValueMrp());
      batch.setCreatedBy("root");
      batch.setCreatedAt(new Date());
      batch.setProductId(product);
      batch.setProductPackingDetailId(oldBatch.getProductPackingDetailId());
      batch.setDefaultProductPackingId(oldBatch.getDefaultProductPackingId());
      batch.setIsSaleable(oldBatch.getIsSaleable());
      AppService.insert(main, batch);
      main.em().flush();
    }
    return batch;
  }

  public static ProductDetail getProductDetailFromProductAndAccount(Main main, ProductBatch batch, Account account) {
    return (ProductDetail) AppService.single(main, ProductDetail.class, "select * from scm_product_detail where product_batch_id=? and account_id=?",
            new Object[]{batch.getId(), account.getId()});
  }

  public static void deleteProductEntryDetailByEntry(Main main, ProductEntry productEntry) {
    AppService.deleteSql(main, ProductEntryDetail.class, "delete from scm_product_entry_detail where product_entry_id=?", new Object[]{productEntry.getId()});
  }
}
