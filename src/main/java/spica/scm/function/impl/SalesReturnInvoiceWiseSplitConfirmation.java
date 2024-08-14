/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.function.impl;

import java.util.List;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.domain.SalesReturnSplit;
import spica.scm.function.SalesReturnConfirmation;
import spica.scm.service.PlatformService;
import spica.scm.service.SalesReturnItemService;
import spica.scm.service.SalesReturnService;
import spica.scm.service.SalesReturnSplitService;
import spica.scm.service.StockSaleableService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.MathUtil;
import spica.scm.util.SalesInvoiceUtil;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Anoop Jayachandran
 */
public class SalesReturnInvoiceWiseSplitConfirmation implements SalesReturnConfirmation {

  @Override
  public void confirmSalesReturn(Main main, TaxCalculator taxCalculator, SalesReturn salesReturn) {
    List<SalesReturnItem> invoiceSalesReturnItems = null;
    List<SalesReturnItem> openSalesReturnItems = null;
    SalesReturn salesReturnObj = null;
    SalesReturnItem salesReturnItemObj = null;
    salesReturn.setDisableRoundOff(true);
    Integer returnSplitForGstFiling = salesReturn.getReturnSplitForGstFiling();
    int invoiceCounter = 1;

    boolean isDamagedReturn = (salesReturn.getSalesReturnType().equals(SystemConstants.SALES_RETURN_TYPE_DAMAGED));

//    List<String> salesInvoiceIds = AppDb.listFirst(main.dbConnector(), "select distinct sales_invoice_id from scm_sales_return_item where sales_invoice_id IS NOT NULL AND sales_return_id = ?", new Object[]{salesReturn.getId()});
//
//    if (!StringUtil.isEmpty(salesInvoiceIds)) {
//      for (String invoiceId : salesInvoiceIds) {
//
//        salesReturnObj = getSalesReturn(salesReturn, invoiceCounter);
//
//        invoiceSalesReturnItems = AppService.list(main, SalesReturnItem.class, "select * from scm_sales_return_item where sales_invoice_id = ? and sales_return_id = ? order by id asc",
//                new Object[]{Integer.parseInt(invoiceId), salesReturn.getId()});
//        salesReturnObj.setBusinessArea(SalesInvoiceUtil.getSalesMode(salesReturnObj.getCompanyId(), salesReturnObj.getCustomerId()));
//        taxCalculator.processSalesReturnCalculation(salesReturnObj, invoiceSalesReturnItems, false);
//
//        SalesInvoice salesInvoiceForSplit = (SalesInvoice) AppService.single(main, SalesInvoice.class,
//                "select invoice_no,invoice_entry_date from scm_sales_invoice where id =?", new Object[]{Integer.parseInt(invoiceId)});
//        salesReturnObj.setReferenceNo(salesInvoiceForSplit.getInvoiceNo());
//        salesReturnObj.setReferenceInvoiceDate(salesInvoiceForSplit.getInvoiceEntryDate());
//        SalesReturnSplit salesReturnSplit = null;
//        if (returnSplitForGstFiling != null && returnSplitForGstFiling.intValue() == SystemConstants.NO) {
//          SalesReturnService.insert(main, salesReturnObj);
//        } else {
//          salesReturnSplit = new SalesReturnSplit(salesReturnObj);
//          salesReturnSplit.setReferenceNo(salesInvoiceForSplit.getInvoiceNo());
//          salesReturnSplit.setReferenceInvoiceDate(salesInvoiceForSplit.getInvoiceEntryDate());
//          salesReturnSplit.setSalesReturnId(salesReturn);
//          SalesReturnSplitService.insertReturnSplit(main, salesReturnSplit);
//          main.em().flush();
//        }
//        for (SalesReturnItem item : invoiceSalesReturnItems) {
//          salesReturnItemObj = new SalesReturnItem(item);
//          salesReturnItemObj.setSalesReturnId(salesReturnObj);
//          if (returnSplitForGstFiling != null && returnSplitForGstFiling.intValue() == SystemConstants.NO) {
//            SalesReturnItemService.insert(main, salesReturnItemObj);
//          } else {
//            SalesReturnSplitService.insertReturnItemSplit(main, salesReturnSplit, salesReturnItemObj);
//          }
//        }
//
////                if returnSplit is For Gst Filing parent is already gone to platform and stock saleable.
//        if (returnSplitForGstFiling != null && returnSplitForGstFiling.intValue() == SystemConstants.NO) {
//          PlatformService.salesReturnPlatformEntry(main, salesReturnObj, invoiceSalesReturnItems);
//        }
//        main.em().flush();
//        if (returnSplitForGstFiling != null && returnSplitForGstFiling.intValue() == SystemConstants.NO) {
//          StockSaleableService.insertSalesReturnItems(main, salesReturnObj);
//          taxCalculator.saveSalesReturn(main, salesReturnObj);
//        }
//        invoiceCounter++;
//      }
//    }
    List<String> salesInvoiceHashList = AppDb.listFirst(main.dbConnector(), "select distinct CONCAT(ref_invoice_no,'#',ref_invoice_date) from scm_sales_return_item where  sales_return_id =? \n"
            + "AND ref_invoice_no IS NOT NULL AND TRIM(ref_invoice_no) !=''", new Object[]{salesReturn.getId()});
    if (!StringUtil.isEmpty(salesInvoiceHashList)) {
      for (String invoiceHash : salesInvoiceHashList) {
        String[] invoiceHashArray = invoiceHash.split("#");
        String invoiceNo = invoiceHashArray[0];
        String invoiceDate = "";
        if (invoiceHashArray.length > 1) {
          invoiceDate = invoiceHashArray[1];
        }
        salesReturnObj = getSalesReturn(salesReturn, invoiceCounter);

        invoiceSalesReturnItems = AppService.list(main, SalesReturnItem.class, "select * from scm_sales_return_item where ref_invoice_no = ? and ref_invoice_date::date = ?::date and sales_return_id = ?  order by id asc",
                new Object[]{invoiceNo, invoiceDate, salesReturn.getId()});
        salesReturnObj.setBusinessArea(SalesInvoiceUtil.getSalesMode(salesReturnObj.getCompanyId(), salesReturnObj.getCustomerId()));
        taxCalculator.processSalesReturnCalculation(salesReturnObj, invoiceSalesReturnItems, false);

        salesReturnObj.setReferenceNo(invoiceSalesReturnItems.get(0).getRefInvoiceNo());
        salesReturnObj.setReferenceInvoiceDate(invoiceSalesReturnItems.get(0).getRefInvoiceDate());
        SalesReturnSplit salesReturnSplit = null;
        if (returnSplitForGstFiling != null && returnSplitForGstFiling.intValue() == SystemConstants.NO) {
          SalesReturnService.insert(main, salesReturnObj);
        } else {
          salesReturnSplit = new SalesReturnSplit(salesReturnObj);
          salesReturnSplit.setReferenceNo(invoiceSalesReturnItems.get(0).getRefInvoiceNo());
          salesReturnSplit.setReferenceInvoiceDate(invoiceSalesReturnItems.get(0).getRefInvoiceDate());
          salesReturnSplit.setSalesReturnId(salesReturn);
          SalesReturnSplitService.insertReturnSplit(main, salesReturnSplit);
          main.em().flush();
        }
        for (SalesReturnItem item : invoiceSalesReturnItems) {
          salesReturnItemObj = new SalesReturnItem(item);
          salesReturnItemObj.setSalesReturnId(salesReturnObj);
          if (returnSplitForGstFiling != null && returnSplitForGstFiling.intValue() == SystemConstants.NO) {
            SalesReturnItemService.insert(main, salesReturnItemObj);
          } else {
            SalesReturnSplitService.insertReturnItemSplit(main, salesReturnSplit, salesReturnItemObj);
          }
        }

//                if returnSplit is For Gst Filing parent is already gone to platform and stock saleable.
        main.em().flush();
        if (returnSplitForGstFiling != null && returnSplitForGstFiling.intValue() == SystemConstants.NO) {
          SalesReturnService.insertSalesReturnOpenProductItems(main, salesReturnObj, taxCalculator, isDamagedReturn);
          StockSaleableService.insertSalesReturnItems(main, salesReturnObj);
          taxCalculator.saveSalesReturn(main, salesReturnObj);
        }
        invoiceCounter++;
      }
    }

    if (AppService.exist(main, "select 1 from scm_sales_return_item where sales_return_id = ? and (ref_invoice_no is null OR TRIM(ref_invoice_no) = '' )", new Object[]{salesReturn.getId()})) {
      SalesReturn openSalesReturn = getSalesReturn(salesReturn, invoiceCounter);
      openSalesReturnItems = AppService.list(main, SalesReturnItem.class, "select * from scm_sales_return_item where sales_return_id = ? and sales_invoice_id is null and (ref_invoice_no is null OR TRIM(ref_invoice_no) = '' ) order by id ", new Object[]{salesReturn.getId()});

      openSalesReturn.setBusinessArea(SalesInvoiceUtil.getSalesMode(openSalesReturn.getCompanyId(), openSalesReturn.getCustomerId()));
      for (SalesReturnItem returnItem : openSalesReturnItems) {
        returnItem.setRefInvoiceNo(openSalesReturn.getReferenceNo());
        returnItem.setRefInvoiceDate(openSalesReturn.getReferenceInvoiceDate());
      }
      taxCalculator.processSalesReturnCalculation(openSalesReturn, openSalesReturnItems, false);
      SalesReturnSplit salesReturnSplit = null;
      openSalesReturn.setReferenceNo(salesReturn.getReferenceNo());
      openSalesReturn.setReferenceInvoiceDate(salesReturn.getReferenceInvoiceDate());
      if (returnSplitForGstFiling != null && returnSplitForGstFiling.intValue() == SystemConstants.NO) {
        SalesReturnService.insert(main, openSalesReturn);
      } else {
        salesReturnSplit = new SalesReturnSplit(openSalesReturn);
        salesReturnSplit.setSalesReturnId(salesReturn);
        salesReturnSplit.setReferenceNo(salesReturn.getReferenceNo());
        salesReturnSplit.setReferenceInvoiceDate(salesReturn.getReferenceInvoiceDate());
        SalesReturnSplitService.insertReturnSplit(main, salesReturnSplit);
        main.em().flush();
      }

      for (SalesReturnItem item : openSalesReturnItems) {
        salesReturnItemObj = new SalesReturnItem(item);
        salesReturnItemObj.setSalesReturnId(openSalesReturn);
        if (returnSplitForGstFiling != null && returnSplitForGstFiling.intValue() == SystemConstants.NO) {
          SalesReturnItemService.insert(main, salesReturnItemObj);
        } else {
          SalesReturnSplitService.insertReturnItemSplit(main, salesReturnSplit, salesReturnItemObj);
        }
      }

      main.em().flush();

      //                if returnSplit is For Gst Filing parent is already gone to platform and stock saleable.
      if (returnSplitForGstFiling != null && returnSplitForGstFiling.intValue() == SystemConstants.NO) {
        SalesReturnService.insertSalesReturnOpenProductItems(main, openSalesReturn, taxCalculator, isDamagedReturn);
        StockSaleableService.insertSalesReturnItems(main, openSalesReturn);
        taxCalculator.saveSalesReturn(main, openSalesReturn);
      }

    }

  }

  private SalesReturn getSalesReturn(SalesReturn salesReturn, int invoiceCounter) {
    SalesReturn sreturn = new SalesReturn(salesReturn);
    sreturn.setInvoiceNo(salesReturn.getInvoiceNo() + " - " + invoiceCounter);
    return sreturn;
  }

//  private SalesReturnItem getSalesReturnItem(SalesReturnItem salesReturnItem) {
//    SalesReturnItem item = new SalesReturnItem(salesReturnItem);
//    return item;
//  }
  private void processSalesReturnCalculation(SalesReturn salesReturn, List<SalesReturnItem> salesReturnItems) {

    double invoiceAmountNet = salesReturnItems.stream().mapToDouble(SalesReturnItem::getValueAssessable).sum();
    double valueIgst = salesReturnItems.stream().mapToDouble(SalesReturnItem::getValueIgst).sum();
    double valueCgst = salesReturnItems.stream().mapToDouble(SalesReturnItem::getValueCgst).sum();
    double valueSgst = salesReturnItems.stream().mapToDouble(SalesReturnItem::getValueSgst).sum();
    double invoiceAmount = invoiceAmountNet + valueIgst;

    double grandTotal = MathUtil.roundOff(invoiceAmountNet + valueIgst, 2);
    salesReturn.setGrandTotal(grandTotal);

    double invoiceValue = Double.valueOf(Math.round(salesReturn.getGrandTotal()));

    double invoiceRoundOff = invoiceValue - grandTotal;
    salesReturn.setInvoiceValue(invoiceValue);

    salesReturn.setInvoiceAmount(invoiceValue);
    salesReturn.setInvoiceRoundOff(MathUtil.roundOff(invoiceRoundOff, 2));

    salesReturn.setInvoiceAmount(invoiceAmount);
    salesReturn.setInvoiceAmountAssessable(invoiceAmountNet);
    salesReturn.setInvoiceAmountNet(invoiceAmountNet);
    salesReturn.setInvoiceAmountIgst(valueIgst);
    salesReturn.setInvoiceAmountCgst(valueCgst);
    salesReturn.setInvoiceAmountSgst(valueSgst);

  }

}
