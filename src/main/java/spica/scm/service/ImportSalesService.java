/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Import;
import spica.scm.domain.ImportSalesItem;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import static spica.scm.service.ImportProductEntryService.getProductId;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.ImportUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;

/**
 *
 * @author godson
 */
public class ImportSalesService extends ImportUtil {

  private static SqlPage getImportSalesSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_import", Import.class, main);
    sql.main("select scm_import.id,scm_import.customer_id,scm_import.reference_no,scm_import.created_at,"
            + "scm_import.entry_date,scm_import.created_by,scm_import.status "
            + " from scm_import scm_import "); //Main query
    sql.count("select count(scm_import.id) as total from scm_import scm_import "); //Count query

    sql.string(new String[]{"scm_import.reference_no", "scm_import.created_at", "scm_import.entry_date", "scm_import.created_by",
      "scm_import.customer_id.customer_name"}); //String search or sort fields
    sql.number(new String[]{"scm_import.id", "scm_import.reference_no", "scm_import.status"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_import.entry_date", "scm_import.created_at"});  //Date search or sort fields
    return sql;
  }

  public static final List<Import> listPagedByAccountGroup(Main main, AccountGroup accountGroup) {
    SqlPage sql = getImportSalesSqlPaged(main);
    sql.cond("where scm_import.import_type=? and account_group_id = ?");
    sql.param(SystemConstants.IMPORT_TYPE_SALES);
    if (accountGroup != null) {
      sql.param(accountGroup.getId());
    }
    return AppService.listPagedJpa(main, sql);
  }

  public static void insertOrUpdateImportSalesItem(Main main, ImportSalesItem importSalesItem) {
    if (importSalesItem.getId() != null) {
      AppService.update(main, importSalesItem);
    } else {
      AppService.insert(main, importSalesItem);
    }
  }

  public static int validateItem(Main main, Import importSales, List<ImportSalesItem> importSalesItemList, Company company) {
    int errorNum = 0;
    boolean validate = true;
    for (ImportSalesItem importSalesItem : importSalesItemList) {
      String error = "";
      boolean exist = false;
      String productName = null;
      if (importSales.getStatus() == null) {
        productName = removeLastHyphen(importSalesItem.getProductName());
      } else {
        productName = importSalesItem.getProductName();
      }
      Product product = getProductIdWithPacking(main, productName, company.getId());
      if (product != null) {
        importSalesItem.setProductName(product.getProductName());
        SalesInvoice salesInvoice = new SalesInvoice();
        salesInvoice.setAccountGroupId(importSales.getAccountGroupId());
        salesInvoice.setAccountGroupPriceListId(importSales.getAccountGroupPriceListId());
        salesInvoice.setInvoiceEntryDate(importSales.getEntryDate());
        List<Product> productList = getTaxCalculator(importSales).selectProductForSales(main, salesInvoice, "");
        List<String> productNameList = productWithoutWhiteSpace(productList);
        Integer totalQty = (importSalesItem.getQty() != null ? importSalesItem.getQty() : 0) + (importSalesItem.getFreeQty() != null ? importSalesItem.getFreeQty() : 0);

        // Check whether Product is saleable
        if (productNameList.contains(removeWhiteSpace(product.getProductName()))) {
          SalesInvoiceItem invoiceItem = new SalesInvoiceItem();
          invoiceItem.setProduct(product);
          List<ProductDetailSales> productDetailSalesList = getTaxCalculator(importSales).selectProductDetailForSales(main, salesInvoice, invoiceItem);
          ProductDetailSales productDetailSales = null;
          boolean batchAvailable = false;
          int totalAvailableQty = 0;

//         Check whether batch is saleable
          DateFormat dateFormat = new SimpleDateFormat("MMM-yy");
          for (ProductDetailSales pds : productDetailSalesList) {
            totalAvailableQty += (pds.getQuantityAvailable() + (pds.getQuantityFreeAvailable() != null ? pds.getQuantityFreeAvailable() : 0));
            if (importSalesItem.getBatchNo() != null && removeWhiteSpace(pds.getBatchNo()).equals(removeWhiteSpace(importSalesItem.getBatchNo()))
                    //                    && importSalesItem.getExpiryDate() != null && pds.getExpiryDate().equals(dateFormat.format(importSalesItem.getExpiryDate())) 
                    && importSalesItem.getMrp() != null && pds.getMrpValue().equals(importSalesItem.getMrp())) {
              batchAvailable = true;
              productDetailSales = pds;
            }
          }
          if (importSalesItem.getBatchNo() != null && !batchAvailable) {
            error += (error.isEmpty() ? "" : ", ") + "Batch Unavailable";
            validate = false;
          } else if (productDetailSales != null) {
            importSalesItem.setProductDetailSales(productDetailSales);

//            If box qty is  not null
            if (importSalesItem.getBoxQty() != null) {
              importSalesItem.setSecToPriQuantity(productDetailSales.getSecToPriQuantity());
              changeSectoPrimaryQty(importSalesItem);
            }

//            Check if qty is available 
            if (totalQty > (productDetailSales.getQuantityAvailable() + (productDetailSales.getQuantityFreeAvailable() != null ? productDetailSales.getQuantityFreeAvailable() : 0))) {
              error += (error.isEmpty() ? "" : ", ") + "Maximum available Qty in batch " + importSalesItem.getBatchNo() + " is " + (productDetailSales.getQuantityAvailable() + (productDetailSales.getQuantityFreeAvailable() != null ? productDetailSales.getQuantityFreeAvailable() : 0));
              validate = false;
            }
          }
          if (totalAvailableQty < totalQty) {
            error += (error.isEmpty() ? "" : ", ") + "Maximum available Qty is " + totalAvailableQty;
            validate = false;
          }
          if ((importSalesItem.getQty() == null && importSalesItem.getFreeQty() == null) || (totalQty == 0)) {
            error += (error.isEmpty() ? "" : ", ") + "Empty Qty";
            validate = false;
          }
        } else {
          error = (error.isEmpty() ? "" : ", ") + "Product not Found";
          validate = false;
        }
      } else {
        error = (error.isEmpty() ? "" : ", ") + "Product not Found";
        validate = false;
      }
      importSalesItem.setError(error);
      if (error.isEmpty()) {
        importSalesItem.setErrorStatus(SystemConstants.ERROR_FREE);
      } else {
        errorNum++;
        importSalesItem.setErrorStatus(SystemConstants.ERROR);
      }
      insertOrUpdateImportSalesItem(main, importSalesItem);
      main.em().flush();
    }
    if (!validate) {
      importSales.setStatus(SystemConstants.IMPORT_FAILED);
    } else {
      importSales.setStatus(SystemConstants.IMPORT_SUCCESS);
    }
    insertOrUpdateImport(main, importSales);
    return errorNum;
  }

  public static List<ImportSalesItem> selectAllSalesLogList(Main main, Import importSales) {
    return AppService.list(main, ImportSalesItem.class, "select * from scm_sales_invoice_item_import where import_id=? order by line_no asc",
            new Object[]{importSales.getId()});
  }

  public static List<ProductBatch> productBatchList(Main main, Import importSales, ImportSalesItem importSalesItem) {
    return AppService.list(main, ProductBatch.class, "select batch_no,product_batch_id as id,expiry_date_actual,mrp_value as value_mrp from getproductsdetailsforgstsale(?,?,?,?,?)"
            + " group by batch_no,product_batch_id,expiry_date_actual,mrp_value  ",
            new Object[]{importSales.getAccountGroupId() == null ? null : importSales.getAccountGroupId().getId(), importSalesItem.getProduct() == null ? null : importSalesItem.getProduct().getId(),
              importSales.getAccountGroupPriceListId() == null ? null : importSales.getAccountGroupPriceListId().getId(), null,
              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(importSales.getEntryDate())});
  }

  public static ProductDetailSales productDetailSalesList(Main main, Import importSales, ImportSalesItem importSalesItem) {
    if (importSalesItem.getProduct() == null || (importSalesItem.getProduct() != null && importSalesItem.getProduct().getId() == null)) {
      importSalesItem.setProduct(getProductId(main, importSalesItem.getProductName(), UserRuntimeView.instance().getCompany().getId()));
    }
    if (importSalesItem.getProductBatch() == null) {
      importSalesItem.setProductBatch(getBatchId(main, importSalesItem.getBatchNo(), importSalesItem.getProduct().getId(), importSalesItem.getMrp(), UserRuntimeView.instance().getCompany().getId()));
    }
    return (ProductDetailSales) AppDb.single(main.dbConnector(), ProductDetailSales.class, "select pricelist_pts,sum(quantity_available) quantity_available,"
            + "sum(quantity_free_available) quantity_free_available,sec_to_pri_quantity,"
            + "expiry_date_actual,mrp_value from getproductsdetailsforgstsale(?,?,?,?,?) where product_batch_id=?  group by pricelist_pts,expiry_date_actual,mrp_value,sec_to_pri_quantity ",
            new Object[]{importSales.getAccountGroupId() == null ? null : importSales.getAccountGroupId().getId(), importSalesItem.getProduct() == null ? null : importSalesItem.getProduct().getId(),
              importSales.getAccountGroupPriceListId() == null ? null : importSales.getAccountGroupPriceListId().getId(), null,
              SystemRuntimeConfig.SDF_YYYY_MM_DD.format(importSales.getEntryDate()), importSalesItem.getProductBatch().getId()});
  }

  private static TaxCalculator getTaxCalculator(Import importSales) {
    TaxCalculator taxCalculator = SystemRuntimeConfig.getTaxCalculator(importSales.getTaxProcessorId().getProcessorClass());
    return taxCalculator;
  }

  public static Integer getLastSeq(Main main, AccountGroup accountGroup) {
    if (accountGroup != null) {
      Long lastSeq = null;
      lastSeq = AppService.count(main, "select last_seq from scm_import where account_group_id = ? order by last_seq desc limit 1 ", new Object[]{accountGroup.getId()});
      if (lastSeq == null || lastSeq == 0) {
        lastSeq = 1L;
      }
      return lastSeq.intValue();
    }
    return null;
  }

  public static final void deleteByPk(Main main, Import importSales) {
    AppService.deleteSql(main, ImportSalesItem.class, "delete from scm_sales_invoice_item_import where import_id = ?", new Object[]{importSales.getId()});
    AppService.delete(main, Import.class, importSales.getId());
  }

  public static final void deleteItemByPk(Main main, ImportSalesItem importSalesItem) {
    AppService.delete(main, ImportSalesItem.class, importSalesItem.getId());
  }

  public static void changeSectoPrimaryQty(ImportSalesItem importSalesItem) {
    int sQty = 0;
    int pQty = 0;
    if (importSalesItem.getSecToPriQuantity() != null) {
      sQty = importSalesItem.getBoxQty() != null ? importSalesItem.getBoxQty() : 0;
      if (sQty != 0) {
        pQty = sQty * importSalesItem.getSecToPriQuantity();
        importSalesItem.setQty(pQty);
      }
    }
  }

  public static void changeSectoQty(ImportSalesItem importSalesItem) {
    int sQty = 0;
    int pQty = 0;
    if (importSalesItem.getSecToPriQuantity() != null) {
      sQty = importSalesItem.getBoxQty() != null ? importSalesItem.getBoxQty() : 0;
      if (sQty != 0) {
        pQty = sQty * importSalesItem.getSecToPriQuantity();
        importSalesItem.setQty(pQty);
      }
    }
  }

  private static String removeLastHyphen(String str) {
    if (str != null) {
      int index = str.lastIndexOf('-');
      if (index > -1) {
        str = str.substring(0, index) + " " + removeWhiteSpace(str.substring(index + 1, str.length() - 1)).replaceAll("X", "*");
      }
      return str;
    }
    return null;
  }

  private static List<String> productWithoutWhiteSpace(List<Product> productList) {
    List<String> productNameList = new ArrayList<>();
    if (productList != null) {
      for (Product product : productList) {
        String productName = product.getProductName();
        productName = removeWhiteSpace(productName);
        productNameList.add(productName);
      }
    }
    return productNameList;
  }

//
//  public static void addProductComparison(Main main, ProductComparison productComparison) {
//    AppService.insert(main, productComparison);
//  }
}
