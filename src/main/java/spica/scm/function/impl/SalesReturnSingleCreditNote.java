/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.function.impl;

import java.util.List;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.service.DebitCreditNoteService;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.function.SalesReturnCreditNote;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Anoop Jayachandran
 */
public class SalesReturnSingleCreditNote implements SalesReturnCreditNote {

  @Override
  public void processCreditNote(Main main, SalesReturn salesReturn) {
    DebitCreditNoteItem debitCreditNoteItem = null;

    List<SalesReturnItem> salesReturnItemList = AppService.list(main, SalesReturnItem.class, "select * from scm_sales_return_item where sales_return_id = ?", new Object[]{salesReturn.getId()});

    if (!StringUtil.isEmpty(salesReturnItemList)) {

      DebitCreditNote debitCreditNote = null;//new DebitCreditNote(salesReturn, SystemConstants.CREDIT_NOTE, SystemConstants.TAXABLE_DEBIT_CREDIT_NOTE, SystemConstants.DRAFT);
      AccountingLedger accountingLedger = (AccountingLedger) AppService.single(main, AccountingLedger.class,
              "select * from fin_accounting_ledger where company_id = ? and accounting_entity_type_id = ? and entity_id = ?", new Object[]{salesReturn.getCompanyId().getId(), AccountingConstant.ACC_ENTITY_CUSTOMER.getId(), salesReturn.getCustomerId().getId()});
      DebitCreditNoteService.selectDebitCreditNoteInvoiceNo(main, debitCreditNote, true);
      debitCreditNote.setAccountingLedgerId(accountingLedger);

      AppService.insert(main, debitCreditNote);

      for (SalesReturnItem salesReturnItem : salesReturnItemList) {
        debitCreditNoteItem = new DebitCreditNoteItem();
        debitCreditNoteItem.setDebitCreditNoteId(debitCreditNote);
        debitCreditNoteItem.setTitle(salesReturnItem.getProductBatchId().getProductId().getProductName());
        debitCreditNoteItem.setDescription("Sales Return Credit Note");
        debitCreditNoteItem.setRefInvoiceDate(salesReturn.getEntryDate());
        debitCreditNoteItem.setRefInvoiceNo(salesReturn.getInvoiceNo());
        debitCreditNoteItem.setTaxCodeId(salesReturnItem.getTaxCodeId());
        debitCreditNoteItem.setHsnSacCode(salesReturnItem.getProductBatchId().getProductId().getHsnCode());
        debitCreditNoteItem.setIgstAmount(salesReturnItem.getValueIgst());
        debitCreditNoteItem.setCgstAmount(salesReturnItem.getValueCgst());
        debitCreditNoteItem.setSgstAmount(salesReturnItem.getValueSgst());
        debitCreditNoteItem.setTaxableValue(salesReturnItem.getValueAssessable());
        debitCreditNoteItem.setNetValue(salesReturnItem.getValueNet());
        AppService.insert(main, debitCreditNoteItem);
      }
    }
  }
}
