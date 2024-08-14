/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.scm.common.InvoiceItem;
import spica.scm.common.ProductSummary;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.domain.SalesReturnItemSplit;
import spica.scm.domain.SalesReturnSplit;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
public class SalesReturnSplitService {

  public static final SalesReturnSplit selectByPk(Main main, SalesReturnSplit salesReturnSplit) {
    return (SalesReturnSplit) AppService.find(main, SalesReturnSplit.class, salesReturnSplit.getId());
  }

  public static List<SalesReturnItemSplit> selectSalesReturnItemSplitList(Main main, SalesReturnSplit salesReturnSplit) {
    main.clear();
    List<SalesReturnItemSplit> salesReturnItemSplitList = null;
    if (SystemConstants.CONFIRMED.equals(salesReturnSplit.getSalesReturnStatusId().getId())) {
      String sql = "select row_number() OVER () as id,sales_return_split_id,product_batch_id,product_detail_id,sum(product_quantity) product_quantity,sum(product_quantity_damaged) product_quantity_damaged,\n"
              + "expected_landing_rate,value_rate,value_mrp,value_pts,value_ptr,tax_code_id,SUM(product_discount_value) product_discount_value,SUM(scheme_discount_value) scheme_discount_value,\n"
              + "SUM(invoice_discount_value) invoice_discount_value,SUM(credit_settlement_amount) as credit_settlement_amount,\n"
              + "sum(coalesce(value_cgst,0)) value_cgst,sum(coalesce(value_cgst,0)) value_cgst,sum(coalesce(value_igst,0)) value_igst,sum(coalesce(value_assessable,0)) value_assessable,\n"
              + "scheme_discount_value_derived,product_discount_value_derived,invoice_discount_value_derived,sales_invoice_id,product_hash_code,ref_invoice_no,ref_invoice_date,is_tax_code_modified,scheme_discount_percentage, \n"
              + "product_discount_percentage,purchase_rate,pack_description \n"
              + "from scm_sales_return_item_split where sales_return_split_id = ?\n"
              + "group by sales_return_split_id,product_batch_id,product_detail_id,tax_code_id,expected_landing_rate,purchase_rate,value_rate,value_mrp,value_pts,value_ptr,tax_code_id, \n"
              + "scheme_discount_value_derived,product_discount_value_derived,invoice_discount_value_derived,sales_invoice_id,product_hash_code,ref_invoice_no,ref_invoice_date,is_tax_code_modified,scheme_discount_percentage,\n"
              + "product_discount_percentage,pack_description\n";
      salesReturnItemSplitList = AppService.list(main, SalesReturnItemSplit.class, sql, new Object[]{salesReturnSplit.getId()});
      updateProductSummery(main, salesReturnItemSplitList);
    } else {
      if (salesReturnSplit.getId() != null) {
        String sql = "select * from scm_sales_return_item where sales_return_split_id = ? order by id asc";
        salesReturnItemSplitList = AppService.list(main, SalesReturnItemSplit.class, sql, new Object[]{salesReturnSplit.getId()});
        updateProductSummery(main, salesReturnItemSplitList);
      }
    }
    return salesReturnItemSplitList;
  }

  private static void updateProductSummery(Main main, List<SalesReturnItemSplit> salesReturnItemList) {
    for (SalesReturnItemSplit salesReturnSplitItem : salesReturnItemList) {
      salesReturnSplitItem.setProduct(salesReturnSplitItem.getProductBatchId().getProductId());
      salesReturnSplitItem.setProductSummary(new ProductSummary(salesReturnSplitItem.getProduct().getId(), salesReturnSplitItem.getProductBatchId().getId(), salesReturnSplitItem.getProduct().getProductName(), salesReturnSplitItem.getProductBatchId().getBatchNo()));
      salesReturnSplitItem.getProductSummary().setBatch(salesReturnSplitItem.getProductBatchId().getBatchNo());
      salesReturnSplitItem.getProductSummary().after();
      salesReturnSplitItem.setProductBatchSummary(new ProductSummary(salesReturnSplitItem.getProduct().getId(), salesReturnSplitItem.getProductBatchId().getId(), salesReturnSplitItem.getProduct().getProductName(), salesReturnSplitItem.getSalesInvoiceId() == null ? null : salesReturnSplitItem.getSalesInvoiceId().getId(), null, salesReturnSplitItem.getProductBatchId().getBatchNo()));
      salesReturnSplitItem.getProductBatchSummary().setBatch(salesReturnSplitItem.getProductBatchId().getBatchNo());
      salesReturnSplitItem.getProductBatchSummary().after();
      salesReturnSplitItem.getProductBatchSummary().setProductCode(salesReturnSplitItem.getProductHashCode());
      salesReturnSplitItem.setCurrentTaxCode(salesReturnSplitItem.getCurrentTaxCode());
      if (salesReturnSplitItem.getAccountId() != null && salesReturnSplitItem.getProduct() != null) {
        salesReturnSplitItem.setProductPreset(ProductPresetService.selectProductPresetByProductAndAccount(main, salesReturnSplitItem.getProduct().getId(), salesReturnSplitItem.getAccountId()));
      }
    }
  }

  public static List<SalesReturnItem> getSalesReturnItemListBySplitItem(Main main, List<SalesReturnItemSplit> returnItemSplitList) {
    List<SalesReturnItem> returnItemList = new ArrayList<>();
    if (returnItemList != null) {
      for (SalesReturnItemSplit list : returnItemSplitList) {
        returnItemList.add(new SalesReturnItem(list));
      }
    }
    return returnItemList;
  }

  public static final void insertReturnSplit(Main main, SalesReturnSplit salesReturnSplit) {
    AppService.insert(main, salesReturnSplit);
  }

  public static List<SalesReturnSplit> salesReturnSplitList(Main main, SalesReturn salesReturn) {
    List<SalesReturnSplit> returnSplitList = null;
    if (salesReturn != null && salesReturn.getId() != null) {
      returnSplitList = AppService.list(main, SalesReturnSplit.class, "select * from scm_sales_return_split where sales_return_id = ?",
              new Object[]{salesReturn.getId()});
    }
    return returnSplitList;
  }

  public static final void insertReturnItemSplit(Main main, SalesReturnSplit returnSplit, SalesReturnItem salesReturnItem) {
    SalesReturnItemSplit returnItemSplit = new SalesReturnItemSplit(salesReturnItem);
    returnItemSplit.setSalesReturnSplitId(returnSplit);
    AppService.insert(main, returnItemSplit);
  }

  public static List<SalesReturnItemSplit> getReturnItemSplitList(Main main, SalesReturnSplit returnSplit) {
    List<SalesReturnItemSplit> returnItemSplitList = null;
    if (returnSplit != null) {
      returnItemSplitList = AppService.list(main, SalesReturnItemSplit.class,
              "select * from scm_sales_return_item_split where sales_return_split_id=?", new Object[]{returnSplit.getId()});
    }
    return returnItemSplitList;
  }

  public static final void deleteReturnSplitList(Main main, SalesReturn salesReturn) {
    List<SalesReturnSplit> returnSplitList = AppService.list(main, SalesReturnSplit.class, "select * from scm_sales_return_split where sales_return_id = ?",
            new Object[]{salesReturn.getId()});
    if (returnSplitList != null) {
      for (SalesReturnSplit returnSplit : returnSplitList) {
        deleteReturnItemSplit(main, returnSplit);
        deleteReturnSplit(main, returnSplit);
      }
    }
  }

  private static final void deleteReturnSplit(Main main, SalesReturnSplit salesReturnSplit) {
    if (salesReturnSplit != null) {
      AppService.delete(main, SalesReturnSplit.class, salesReturnSplit.getId()); 
    }
  }

  private static final void deleteReturnItemSplit(Main main, SalesReturnSplit salesReturnSplit) {
    if (salesReturnSplit != null) {
      AppService.deleteSql(main, SalesReturnItemSplit.class, "delete from scm_sales_return_item_split where sales_return_split_id = ?",
              new Object[]{salesReturnSplit.getId()});
    }
  }

  public static List<InvoiceItem> selectSalesReturnItemForPrint(Main main, SalesReturnSplit salesReturnSplit) {
    String sql = null;

    sql = "SELECT T_SRetIte.hsn_code,T_Prod.product_name,T_ProdEnDet.pack_size,T_Man.name AS manufacture_name,T_Man.code AS mfg_code,\n"
            + "T_ProdBat.batch_no,to_char(T_ProdBat.expiry_date_actual,'Mon-yy') AS expiry_date,T_SRetIte.ref_invoice_no,T_SRetIte.ref_invoice_date,\n"
            + "ROUND(T_SRetIte.value_mrp,2) AS value_mrp,ROUND(T_SRetIte.value_ptr,2) AS value_ptr,ROUND(T_SRetIte.value_pts,2) AS value_pts,ROUND(T_SRetIte.value_rate,2) AS value_rate,\n"
            + "SUM(CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END) AS product_qty,\n"
            + "SUM((COALESCE(T_SRetIte.scheme_discount_value_derived,0)*CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END)) AS scheme_discount_value,\n"
            + "SUM((COALESCE(T_SRetIte.product_discount_value_derived,0)*CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END)) AS product_discount_value,\n"
            + "SUM((COALESCE(T_SRetIte.invoice_discount_value_derived,0)*CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END)) AS invoice_discount_value,\n"
            + "SUM((COALESCE(T_SRetIte.cash_discount_value_derived,0)*CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END)) AS cash_discount_value,\n"
            + "SUM((COALESCE(T_SRetIte.return_rate_per_piece,0)*CASE WHEN T_SRet.sales_return_type =1 THEN COALESCE(T_SRetIte.product_quantity,0) ELSE COALESCE(T_SRetIte.product_quantity_damaged,0) END)) AS taxable_amount,\n"
            + "T_SRetIte.value_goods AS value_goods,T_SRetIte.value_igst AS value_igst,\n"
            + "T_SRetIte.value_cgst AS value_cgst,T_SRetIte.value_sgst AS value_sgst, T_igst.rate_percentage AS igst_tax_rate\n"
            + ",T_cgst.rate_percentage AS cgst_tax_rate,T_sgst.rate_percentage AS sgst_tax_rate \n"
            + "FROM scm_sales_return_split AS T_SRet,scm_sales_return_item_split AS T_SRetIte\n"
            + "LEFT OUTER JOIN scm_tax_code T_igst ON T_SRetIte.tax_code_id = T_igst.id \n"
            + "LEFT OUTER JOIN scm_tax_code T_cgst ON T_cgst.parent_id = T_igst.id AND T_cgst.tax_type = ? \n"
            + "LEFT OUTER JOIN scm_tax_code T_sgst ON T_sgst.parent_id = T_igst.id AND T_sgst.tax_type = ? ,\n"
            + "scm_product_entry_detail AS T_ProdEnDet,\n"
            + "scm_product_detail AS T_ProdDet,scm_product AS T_Prod,scm_product_batch AS T_ProdBat\n"
            + "LEFT OUTER JOIN scm_manufacture AS T_Man ON T_ProdBat.manufacture_id = T_Man.id \n"
            + "WHERE \n"
            + "T_SRet.id = T_SRetIte.sales_return_split_id AND  \n"
            + "T_SRetIte.product_entry_detail_id =  T_ProdEnDet.id AND \n"
            + "T_ProdEnDet.product_detail_id = T_ProdDet.id AND\n"
            + "T_ProdDet.product_batch_id = T_ProdBat.id AND\n"
            + "T_ProdBat.product_id = T_Prod.id \n"
            + "AND T_SRetIte.sales_return_split_id = ? \n"
            + "GROUP BY \n"
            + "T_SRetIte.hsn_code,T_Prod.product_name,T_ProdEnDet.pack_size,T_Man.name,T_Man.code,\n"
            + "T_ProdBat.batch_no,T_ProdBat.expiry_date_actual,T_SRetIte.value_mrp,T_SRetIte.value_ptr,T_SRetIte.value_pts,T_SRetIte.value_rate,\n"
            + "T_SRetIte.value_goods ,T_SRetIte.value_igst ,T_SRetIte.ref_invoice_date,T_SRetIte.ref_invoice_no,\n"
            + "T_SRetIte.value_cgst ,T_SRetIte.value_sgst , T_igst.rate_percentage\n"  
            + ",T_cgst.rate_percentage ,T_sgst.rate_percentage  ";
    List<InvoiceItem> list = null;
    list = AppDb.getList(main.dbConnector(), InvoiceItem.class, sql, new Object[]{AccountingConstant.TAX_TYPE_CGST, AccountingConstant.TAX_TYPE_SGST, salesReturnSplit.getId()});
    return list;
  }
}
