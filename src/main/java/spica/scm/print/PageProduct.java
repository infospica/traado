/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.List;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.domain.VendorClaimDetail;
import spica.scm.common.InvoiceItem;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.SalesServicesInvoiceItem;

/**
 *
 * @author sujesh
 */
public interface PageProduct {

  public PdfPTable setSalesReturnProductTable(List<InvoiceItem> invoiceItemList, CompanySettings companySettings, String printType);

  public PdfPTable setSalesServicesInvoiceProduct(List<SalesServicesInvoiceItem> salesServicesInvoiceItemList, CompanySettings companySettings);

  public PdfPTable setDebitCreditNoteProduct(List<DebitCreditNoteItem> debitCreditNoteItemList, CompanySettings companySettings, Integer taxableInvoice, boolean intrastate, Integer supplierOrCustomer, Integer sezZone);

  public PdfPTable emptyRows(float[] columns, float width, float expectedHeight, float sumTableHeight, Rectangle pageSize);

  public PdfPTable setProductItems(ItemTable itemTable, PdfWriter writer, float pageWidth) throws Exception;

  public PdfPTable salesCommissionTable(List<VendorClaimDetail> claimList);

  public PdfPTable pageFillRows(PdfWriter writer, Document document, float[] columns, float footerHeight, String printType);

  public PdfPTable setChequeReceiptProduct(AccountingTransactionDetail partyDetail, CompanySettings companySettings);

  //public PdfPTable setJournalProduct(List<AccountingTransactionDetail> partyDetailList, CompanySettings companySettings);
}
