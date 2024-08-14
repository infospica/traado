/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.service;

import java.util.Date;
import java.util.List;
import spica.scm.common.InvoiceLog;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
public class InvoiceLogService {

  public static final int SALES_INVOICE = 1;
  public static final int SALES_RETURN = 2;
  public static final int PURCHASE_ENTRY = 3;
  public static final int PURCHASE_RETURN = 4;
  public static final int DRAFT = 1;
  public static final int CONFIRMED = 2;
  public static final int CONFIRMED_PACKED = 3;
  public static final int DELETE = 4;

  public static final void insert(Main main, InvoiceLog invoiceLog) {
    AppService.insert(main, invoiceLog);
  }

  public static void insertSalesInvoiceLog(Main main, SalesInvoice salesInvoice) {

  }

  public static void insertSalesReturnLog(Main main, SalesReturn salesReturn, List<SalesReturnItem> returnItemList, Integer logType) {
    if (salesReturn != null) {
      InvoiceLog invoiceLog = new InvoiceLog();
      invoiceLog.setEntityId(salesReturn.getId());
      invoiceLog.setEntityTypeId(SALES_RETURN);
      invoiceLog.setInvoiceNo(salesReturn.getInvoiceNo());
      invoiceLog.setEntryDate(salesReturn.getEntryDate());
      invoiceLog.setInvoiceDate(salesReturn.getInvoiceDate());
      invoiceLog.setCompanyId(salesReturn.getCompanyId().getId());
      invoiceLog.setAccountGroupId(salesReturn.getAccountGroupId().getId());
      if (salesReturn.getCustomerId() != null) {
        invoiceLog.setCustomerOrSupplierId(salesReturn.getCustomerId().getId());
      }
      invoiceLog.setInvoiceStatusId(salesReturn.getSalesReturnStatusId().getId());
      invoiceLog.setInvoiceAmount(salesReturn.getInvoiceAmount());
      invoiceLog.setValueGoods(salesReturn.getInvoiceAmountGoods());
      invoiceLog.setRoundOff(salesReturn.getInvoiceRoundOff());
      invoiceLog.setValueCgst(salesReturn.getInvoiceAmountCgst());
      invoiceLog.setValueSgst(salesReturn.getInvoiceAmountSgst());
      invoiceLog.setValueIgst(salesReturn.getInvoiceAmountIgst());
      invoiceLog.setValueTaxable(salesReturn.getInvoiceAmountAssessable());
      invoiceLog.setCreatedBy(salesReturn.getCreatedBy());
      invoiceLog.setCreatedAt(salesReturn.getCreatedAt());
      if (logType != null) {
        if (logType.intValue() == DRAFT) {
          invoiceLog.setDraftedAt(new Date());
          invoiceLog.setDraftedBy(UserRuntimeView.instance().getAppUser().getName());
          invoiceLog.setDescription("DRAFTED");
        }
        if (logType.intValue() == CONFIRMED) {
          invoiceLog.setConfirmedAt(new Date());
          invoiceLog.setConfirmedBy(UserRuntimeView.instance().getAppUser().getName());
          invoiceLog.setDescription("CONFIRMED");
        }
        if (logType.intValue() == DELETE) {
          invoiceLog.setDeletedAt(new Date());
          invoiceLog.setDeletedBy(UserRuntimeView.instance().getAppUser().getName());
          invoiceLog.setDescription("DELETED");
        }
      }
      //From List
      Double totalQty = 0.0, valueTaxable = 0.0;
      String itemDetailHash = "";
      if (!StringUtil.isEmpty(returnItemList)) {
        for (SalesReturnItem list : returnItemList) {
          Double qty = list.getProductQuantity() != null ? list.getProductQuantity() : (list.getProductQuantityDamaged() != null ? list.getProductQuantityDamaged() : 0);
          String hash = createItemDetailHash(list.getProductBatchId() != null ? list.getProductBatchId().getId() : null, qty,
                  list.getSalesInvoiceId() != null ? list.getSalesInvoiceId().getId() : null);
          totalQty += qty != null ? qty : 0;
          valueTaxable = list.getValueAssessable() != null ? list.getValueAssessable() : 0;
          itemDetailHash += itemDetailHash.isEmpty() ? hash : "#" + hash;
        }
        invoiceLog.setTotalQty(totalQty);
        invoiceLog.setTotalValueTaxableItem(valueTaxable);
        invoiceLog.setItemDetailHash(itemDetailHash);
      }
      insert(main, invoiceLog);
    }
  }

  private static String createItemDetailHash(Integer productDetailId, Double qty, Integer salesInvoiceId) {
    String hash = "";
    if (productDetailId != null && qty != null) {
      hash = productDetailId + "," + qty + (salesInvoiceId != null ? "," + salesInvoiceId : "");
    }
    return hash;
  }
}
