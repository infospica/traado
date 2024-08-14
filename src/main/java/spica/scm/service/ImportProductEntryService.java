/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.service;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.domain.Consignment;
import spica.scm.domain.Import;
import spica.scm.domain.ImportProductEntryDetail;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.util.ImportUtil;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;

/**
 *
 * @author godson
 */
public class ImportProductEntryService extends ImportUtil {

  private static SqlPage getImportProductSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_import", Import.class, main);
    sql.main("select scm_import.id,scm_import.reference_no,scm_import.created_at,scm_import.invoice_amount,"
            + "scm_import.entry_date,scm_import.created_by,scm_import.status "
            + " from scm_import scm_import "); //Main query
    sql.count("select count(scm_import.id) as total from scm_import scm_import "); //Count query
    sql.string(new String[]{"scm_import.reference_no", "scm_import.created_at", "scm_import.entry_date", "scm_import.created_by"}); //String search or sort fields
    sql.number(new String[]{"scm_import.id", "scm_import.reference_no", "scm_import.status", "scm_import.invoice_amount"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_import.entry_date", "scm_import.created_at"});  //Date search or sort fields
    return sql;
  }

  public static final List<Import> listPagedByAccount(Main main, Account account, int openingStock) {
    SqlPage sql = getImportProductSqlPaged(main);
    sql.cond("where scm_import.import_type=? and opening_stock_entry = ? and scm_import.account_id = ? ");
    sql.param(SystemConstants.IMPORT_TYPE_PRODUCT_ENTRY);
    sql.param(openingStock);
    if (account != null) {
      sql.param(account.getId());
    }
    return AppService.listPagedJpa(main, sql);
  }

  public static List<ImportProductEntryDetail> selectAllProductEntryLogList(Main main, Import importProductEntry) {
    return AppService.list(main, ImportProductEntryDetail.class, "select * from scm_product_entry_import where import_id=? order by line_no asc",
            new Object[]{importProductEntry.getId()});
  }

  public static List<Consignment> selectConsignmentList(Main main, Integer companyId) {
    String sql = "select t1.id as list_id, t1.name as title,t1.code,t3.vendor_name as supplier_name from scm_brand t1 left join scm_supplier_brand t2 on t1.id=t2.brand_id \n"
            + "left join scm_vendor t3 on t3.id=t2.vendor_id where t1.company_id=?";
    return AppDb.getList(main.dbConnector(), Consignment.class, sql, new Object[]{companyId});
  }

  public static int validateProduct(Main main, Import importEntry, List<ImportProductEntryDetail> productEntryList, Company company) {
    int errorNum = 0;
    boolean validate = true;
    for (ImportProductEntryDetail importProductEntryItem : productEntryList) {
      String error = "";
      boolean exist = false;
      Product product = getProductId(main, importProductEntryItem.getProductName(), company.getId());
      if (product != null) {
        exist = AppService.exist(main, "select '1' from scm_product where id=? and brand_id in "
                + "(select brand_id from scm_supplier_brand where vendor_id in(select vendor_id from scm_account where id=?))", new Object[]{product.getId(), importEntry.getAccountId().getId()});
        if (!exist) {
          error += (error.isEmpty() ? "" : ", ") + "Product not found";
        } else {
          exist = AppService.exist(main, "select '1' from scm_product where id=? and commodity_id in "
                  + "(select commodity_id from scm_vendor_commodity where vendor_id in(select vendor_id from scm_account where id=?))", new Object[]{product.getId(), importEntry.getAccountId().getId()});
          if (!exist) {
            error += (error.isEmpty() ? "" : ", ") + "Product not found";
          }
        }
        exist = AppService.exist(main, "select '1' from scm_product_batch where upper(TRIM(batch_no))=? and product_id =? and  value_mrp=? ",
                getParam(importProductEntryItem.getBatchNo(), product.getId(), importProductEntryItem.getValueMrp()));
        if (importProductEntryItem.getBatchNo() != null && !exist && (importProductEntryItem.getExpiryDate() != null && importProductEntryItem.getValueMrp() != null)) {
          importProductEntryItem.setProduct(product);
          ProductBatch batch = createBatch(main, null, importProductEntryItem);
          exist = true;
        } else if (importProductEntryItem.getBatchNo() == null || !exist) {
          importProductEntryItem.setBatchNo(null);
          if (importProductEntryItem.getExpiryDate() == null) {
            error += (error.isEmpty() ? "" : ", ") + "Empty Expiry Date";
            validate = false;
          }
          if (importProductEntryItem.getValueMrp() == null) {
            error += (error.isEmpty() ? "" : ", ") + "Empty MRP";
            validate = false;
          }
          error += "batch not found";
          validate = false;
        }
        if (importProductEntryItem.getQty() == null) {
          error += (error.isEmpty() ? "" : ", ") + "Empty Qty";
          validate = false;
        }
        if (importProductEntryItem.getRate() == null) {
          error += (error.isEmpty() ? "" : ", ") + "Empty Rate";
          validate = false;
        }
      } else {
        error += (error.isEmpty() ? "" : ", ") + "Product not Found";
        validate = false;
      }
      importProductEntryItem.setError(error);
      if (error.isEmpty()) {
        importProductEntryItem.setStatus(SystemConstants.ERROR_FREE);
      } else {
        errorNum++;
        importProductEntryItem.setStatus(SystemConstants.ERROR);
      }
      insertOrUpdateImportProductEntryItem(main, importProductEntryItem);
      main.em().flush();
    }
    if (!validate) {
      importEntry.setStatus(SystemConstants.IMPORT_FAILED);
    } else {
      importEntry.setStatus(SystemConstants.IMPORT_SUCCESS);
    }
    insertOrUpdateImport(main, importEntry);
    return errorNum;
  }

  public static void importProductEntry(Main main, List<ImportProductEntryDetail> importProductEntryList) {
    if (importProductEntryList != null) {
      for (ImportProductEntryDetail productEntry : importProductEntryList) {
        AppService.insert(main, productEntry);
      }
    }
  }

  public static List<ImportProductEntryDetail> selectAllProductEntryLogList(Main main, Company company, Account account, String user) {
    return AppDb.getList(main.dbConnector(), ImportProductEntryDetail.class, "select * from scm_product_entry_import where company_id=? and account_id=? and created_by=? order by line_no asc",
            new Object[]{company.getId(), account.getId(), user});
  }

  public static List<ImportProductEntryDetail> selectProductEntryLogList(Main main, Company company, Account account, String user, Integer status) {
    return AppDb.getList(main.dbConnector(), ImportProductEntryDetail.class, "select * from scm_product_entry_import where status=? and company_id=? and account_id=? "
            + "and created_by=? order by line_no asc", new Object[]{status, company.getId(), account.getId(), user});
  }

  public static void deleteProductEntryImport(Main main, Company company, Account account, String user) {
    AppService.deleteSql(main, ImportProductEntryDetail.class, "delete from scm_product_entry_import where company_id=? and account_id=? and created_by=?", new Object[]{company.getId(), account.getId(), user});
  }

  public static void insertOrUpdateImportProductEntryItem(Main main, ImportProductEntryDetail importProductEntry) {
    if (importProductEntry.getId() == null) {
      AppService.insert(main, importProductEntry);
    } else {
      AppService.update(main, importProductEntry);
    }
  }

  public static List<ImportProductEntryDetail> getProductEntryByInvoiceNo(Main main, Company company, Account account, String invoiceNo) {
    String sql = "select t1.invoice_no,t1.invoice_amount,t1.invoice_amount_discount_value as invoice_discount,t1.cash_discount_value as cash_discount,\n"
            + "t1.cash_discount_taxable as cash_discount_taxable,t5.product_name,t4.batch_no,\n"
            + "t4.expiry_date_actual as expiry_date,t4.value_mrp,t2.product_quantity as qty,t2.product_quantity_free as free_qty,\n"
            + "CASE WHEN t2.is_scheme_discount_to_customer = 1 THEN 'Customer' else 'Company' end as scheme_discount_beneficiary,\n"
            + "CASE WHEN t2.scheme_discount_replacement = 1 THEN 'Y' ELSE 'N' end as replacement,t2.discount_value as product_discount,\n"
            + "CASE WHEN t2.is_product_discount_to_customer = 1 THEN 'Customer' else 'Company' end as product_discount_beneficiary,\n"
            + "t2.value_rate as rate,t2.value_pts as pts, t2.product_quantity_excess as excess_qty,t2.product_quantity_damaged as damaged_qty,t2.product_quantity_shortage as shortage\n"
            + "from scm_product_entry t1 \n"
            + "left join scm_product_entry_detail t2 on t1.id=t2.product_entry_id left join scm_product_detail t3 on t3.id=t2.product_detail_id \n"
            + "left join scm_product_batch t4 on t4.id=t3.product_batch_id left join scm_product t5 on t5.id=t4.product_id\n"
            + "where product_entry_id in (select id from scm_product_entry where UPPER(invoice_no)=? and company_id=? and account_id=?)";
    return AppDb.getList(main.dbConnector(), ImportProductEntryDetail.class, sql, new Object[]{invoiceNo, company.getId(), account.getId()});
  }

  public static final void deleteByPk(Main main, Import entry) {
    AppService.delete(main, Import.class, entry.getId());
  }

  public static final void deleteItemByPk(Main main, ImportProductEntryDetail productEntry) {
    AppService.delete(main, ImportProductEntryDetail.class, productEntry.getId());
  }

  //  ---------Creating new Batches if batch no is null
  public static ProductBatch createBatch(Main main, ProductEntryDetail productEntryDetail, ImportProductEntryDetail importEntry) {
    ProductBatch batch = new ProductBatch();
    Random random = new Random();
    int rand = (int) random.nextInt(900) + 100;
    if (productEntryDetail == null) {
      productEntryDetail = new ProductEntryDetail(importEntry);
    }
    if (productEntryDetail.getBatchNo() == null) {
      batch.setBatchNo("NIL-" + importEntry.getValueMrp().intValue() + "-" + rand);
    } else {
      batch.setBatchNo(productEntryDetail.getBatchNo());
    }
    batch.setProductId(productEntryDetail.getProduct());
    batch.setProductPackingDetailId(productEntryDetail.getProduct().getProductPackingDetailId());
    batch.setDefaultProductPackingId(productEntryDetail.getProduct().getProductPackingDetailId() == null ? null : productEntryDetail.getProduct().getProductPackingDetailId().getPackPrimary());
    batch.setExpiryDateActual(importEntry.getExpiryDate());
    if (batch.getExpiryDateActual() != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(batch.getExpiryDateActual());
      int year = cal.get(Calendar.YEAR);
      if (year < 1000) {
        cal.add(Calendar.YEAR, 2000);
        batch.setExpiryDateActual(cal.getTime());
      }
    }

    if (productEntryDetail.getProduct().getExpirySalesDays() == null) {
      batch.setExpiryDateSales(batch.getExpiryDateActual());
    } else {
      Calendar cal = Calendar.getInstance();
      cal.setTime(batch.getExpiryDateActual());
      if (productEntryDetail.getProduct().getExpirySalesDays() != null) {
        cal.add(Calendar.DATE, -productEntryDetail.getProduct().getExpirySalesDays());
      }
      batch.setExpiryDateSales(cal.getTime());
    }
    batch.setValueMrp(importEntry.getValueMrp());
    batch.setIsSaleable(SystemConstants.BATCH_SALEABLE);
    ProductBatchService.insertOrUpdate(main, batch);
    return batch;
  }
}
