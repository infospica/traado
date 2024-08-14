/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import spica.scm.common.InvoiceItem;
import spica.scm.common.ProductSummary;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.domain.SalesReturn;
import spica.scm.util.MathUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;

/**
 *
 * @author godson
 */
public class SalesReturnFromPurReturnService {

  private static SqlPage getSalesReturnFromPurReturnSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_purchase_return", PurchaseReturn.class, main);
    sql.main("select scm_purchase_return.id,scm_purchase_return.invoice_no,scm_purchase_return.invoice_date,scm_purchase_return.entry_date,\n"
            + "scm_purchase_return.purchase_return_stock_cat,scm_purchase_return.purchase_return_stock_type,scm_purchase_return.invoice_amount,\n"
            + "scm_purchase_return.remarks from scm_purchase_return  "); //Main query  
    sql.count("select count(scm_purchase_return.id) from scm_purchase_return scm_purchase_return"); //Count query
    sql.string(new String[]{"scm_purchase_return.invoice_no"}); //String search or sort fields
    sql.number(new String[]{"scm_purchase_return.id,scm_purchase_return.invoice_amount"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  public static final List<PurchaseReturn> listPaged(Main main, SalesReturn salesReturn, AccountingFinancialYear accountingFinancialYear) {
    if (salesReturn.getCustomerId().getIsCompany() != null && salesReturn.getCustomerId().getIsCompany().intValue() == 1) {
      SqlPage sql = getSalesReturnFromPurReturnSqlPaged(main);
      main.clear();
      sql.join("join scm_account scm_account on scm_account.id = scm_purchase_return.account_id ");
      sql.join("join scm_vendor scm_vendor on scm_vendor.id = scm_account.vendor_id ");
      sql.cond("where scm_vendor.is_company = 1  and scm_vendor.gst_no=? \n"
              + "and scm_purchase_return.entry_date::date<= (?::date) \n"
              + "and scm_purchase_return.entry_date::date>= (?::date - INTERVAL '45 days') and scm_purchase_return.purchase_return_status_id>1\n"
              + "and scm_purchase_return.purchase_return_stock_cat=? \n"
              + "and UPPER(scm_purchase_return.invoice_no) not in \n"
              + "(select UPPER(debit_note_no) from scm_sales_return where customer_id=? and financial_year_id = ? and sales_return_status_id=2)");
      sql.param(salesReturn.getCompanyId().getGstNo());
      sql.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesReturn.getEntryDate()));
      sql.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(salesReturn.getEntryDate()));
      if (salesReturn.getSalesReturnType() != null && salesReturn.getSalesReturnType().intValue() == SystemConstants.SALES_RETURN_TYPE_SALEABLE) {
        sql.param(SystemConstants.NON_MOVING_AND_NEAR_EXPIRY);
      } else {
        sql.param(SystemConstants.DAMAGED_AND_EXPIRED);
      }
      sql.param(salesReturn.getCustomerId().getId());
      sql.param(accountingFinancialYear.getId());

      return AppService.listPagedJpa(main, sql);
    } else {
      return null;
    }
  }

  public static Product productInCurrentComapny(Main main, Company company, Product product) {
    return (Product) AppService.single(main, Product.class, "select * from scm_product where company_id=? and product_name=?", new Object[]{company.getId(), product.getProductName()});
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

  public static void deleteSalesReturnItemByReturnId(Main main, SalesReturn salesReturn) {
    AppService.deleteSql(main, ProductEntryDetail.class, "delete from scm_sales_return_item where sales_return_id=?", new Object[]{salesReturn.getId()});
  }

  public static String getProductNameFromDetail(Main main, PurchaseReturnItemReplica itemReplica) {
    String sql = "select prod.product_name from scm_product_detail detail,scm_product_batch batch,scm_product prod \n"
            + "where detail.id = ? and detail.product_batch_id=batch.id and batch.product_id=prod.id";
    main.clear();
    main.param(itemReplica.getProductDetail() != null ? itemReplica.getProductDetail().getId() : null);
    return AppService.firstString(main, sql);
  }

  public static Product getProductFromDetail(Main main, Company company, PurchaseReturnItemReplica itemReplica) {
    String sql = "select * from scm_product where company_id=? and UPPER(product_name)=(select UPPER(prod.product_name) from scm_product_detail detail,scm_product_batch batch,scm_product prod \n"
            + "where detail.id = ? and detail.product_batch_id=batch.id and batch.product_id=prod.id)";
    return (Product) AppService.single(main, Product.class, sql, new Object[]{company.getId(),
      itemReplica.getProductDetail() != null ? itemReplica.getProductDetail().getId() : null});
  }

  public static ProductSummary getProductSummaryForReturn(Main main, Company company, AccountGroup ag, PurchaseReturnItemReplica itemReplica) {
    Product product = getProductFromDetail(main, company, itemReplica);
    if (product != null) {
      InvoiceItem invoiceItem = (InvoiceItem) AppDb.single(main.dbConnector(), InvoiceItem.class,
              "select batch.batch_no,batch.expiry_date_actual,value_mrp from scm_product_detail detail,scm_product_batch batch\n"
              + "where detail.product_batch_id=batch.id   and detail.id=? ", new Object[]{itemReplica.getProductDetail().getId()});

      List<ProductBatch> batchList = AppService.list(main, ProductBatch.class, "select * from scm_product_batch where product_id=? "
              + " and batch_no=? and expiry_date_actual::date=?::date and value_mrp=?",
              new Object[]{product.getId(), invoiceItem.getBatchNo(), SystemRuntimeConfig.SDF_YYYY_MM_DD.format(invoiceItem.getExpiryDateActual()), invoiceItem.getValueMrp()});
      //List bcz there can be multiple batches with same bacth_no,expiry date and mrp but different manufacture id
      StringBuilder b = new StringBuilder("");
      for (ProductBatch batch : batchList) {
        if (b.length() == 0) {
          b.append(batch.getId());
        } else {
          b.append(",").append(batch.getId());
        }
      }
      String sql = "SELECT STRING_AGG(t1.id::text, ',') as sales_invoice_item_ids,\n"
              + "t5.id as product_id,t5.product_name as product_name,t7.pack_size as pack_size,t4.batch_no as batch,to_char(t4.expiry_date_actual, 'Mon-yy') as expiry_date,\n"
              + "t4.id as product_batch_id, t2.id as sales_invoice_id,t4.value_mrp as mrp_value, t1.tax_code_id as gst_tax_code_id, t6.rate_percentage as gst_percentage,\n"
              + "t2.invoice_no as invoice_no,t2.invoice_date as invoice_date, t1.prod_piece_selling_forced as prod_piece_selling_forced,t1.scheme_discount_derived as scheme_discount_derived\n"
              + ",t1.product_discount_derived as product_discount_derived,t1.invoice_discount_derived as invoice_discount_derived,\n"
              + "sum(coalesce(t1.product_qty,0) + coalesce(t1.product_qty_free,0)) product_qty, string_agg(t3.id::varchar, '#') product_detail_hash,\n"
              + "null as product_preset_id,null as account_code,t1.product_discount_percentage as product_discount_percentage,t1.scheme_discount_percentage ,t1.value_ptr,t1.value_pts,\n"
              + "SUM(COALESCE(t8.returned_qty,0)) as returned_qty,t1.hsn_code,\n"
              + "1 as account_status_id\n"
              + "from scm_sales_invoice_item t1\n"
              + "inner join scm_sales_invoice t2 on t1.sales_invoice_id = t2.id and t2.id=?\n"
              + "inner join scm_product_detail t3 on t3.id = t1.product_detail_id\n"
              + "inner join scm_product_batch t4 on t4.id = t3.product_batch_id \n"
              + "inner join scm_product t5 on t5.id = t4.product_id and t5.id = ?   \n"
              + "inner join scm_tax_code t6 on t6.id = t1.tax_code_id inner join scm_product_entry_detail t7 on t7.id=t1.product_entry_detail_id\n"
              + "left join (SELECT sales_invoice_item_id,SUM(COALESCE(product_quantity,0)+COALESCE(product_quantity_damaged,0)) as returned_qty FROM  scm_sales_return_item,\n"
              + "scm_sales_return  WHERE scm_sales_return_item.sales_return_id =scm_sales_return.id AND sales_invoice_item_id IS NOT NULL AND \n"
              + "scm_sales_return.account_group_id = ? AND scm_sales_return.sales_return_status_id=2 GROUP BY sales_invoice_item_id) as t8  ON t8.sales_invoice_item_id=t1.id \n"
              + "where t1.product_detail_id in (select id from scm_product_detail where  product_batch_id in(" + b + "))\n"
              + " and t1.prod_piece_selling_forced=?";
      ArrayList<Object> params = new ArrayList<>();
      params.add(itemReplica.getRefSalesInvoiceId());
      params.add(product.getId());
      params.add(ag.getId());
      params.add(itemReplica.getValueRate());
      if (itemReplica.getSchemeDiscountValue() != null) {
        sql += " and ROUND(t1.scheme_discount_derived,2)=? ";
        params.add(MathUtil.roundOff(itemReplica.getSchemeDiscountValue() / itemReplica.getQuantityReturned(), 2));
      } else {
        sql += " and t1.scheme_discount_derived is null ";
      }
      if (itemReplica.getProductDiscountValueDerived() != null) {
        sql += " and ROUND(t1.product_discount_derived,3) =?  ";
        params.add(MathUtil.roundOff(itemReplica.getProductDiscountValueDerived(), 3));
      } else {
        sql += " and t1.product_discount_derived is null  ";
      }
      if (itemReplica.getInvoiceDiscountValue() != null) {
        sql += " and ROUND(t1.invoice_discount_derived,2)=? ";
        params.add(MathUtil.roundOff(itemReplica.getInvoiceDiscountValue() / itemReplica.getQuantityReturned(), 2));
      } else {
        sql += " and t1.invoice_discount_derived is null ";
      }
      sql += " group by t5.id,t1.hsn_code,t5.product_name,t7.pack_size,t4.batch_no,t4.expiry_date_actual,\n"
              + "t4.id,t2.id,t4.value_mrp,t1.tax_code_id,t6.rate_percentage, t2.invoice_no,t2.invoice_date,t1.prod_piece_selling_forced,\n"
              + "t1.scheme_discount_derived,t1.product_discount_derived,t1.invoice_discount_derived,t1.scheme_discount_percentage,t1.product_discount_percentage,t1.value_ptr,t1.value_pts";
      return (ProductSummary) AppDb.single(main.dbConnector(), ProductSummary.class, sql, params.toArray());
    }
    return null;
  }

  public static Long getRefSalesInvoiceId(Main main, Company company, PurchaseReturnItemReplica itemReplica) {
    return AppService.count(main, "select id from scm_sales_invoice where invoice_no=? and invoice_date=? and company_id=? ",
            new Object[]{itemReplica.getReferenceNo(), itemReplica.getReferenceDate(), company.getId()});
  }
}
