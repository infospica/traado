/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.print.Impl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import spica.constant.AccountingConstant;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.domain.VendorClaimDetail;
import spica.scm.common.InvoiceItem;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.SalesServicesInvoiceItem;
import spica.scm.export.ExcelUtil;
import spica.scm.print.CellProperty;
import spica.scm.print.ItemTable;
import spica.scm.print.PageProduct;
import spica.scm.print.PdfUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.faces.AppView;

/**
 *
 * @author sujesh
 */
public class PageProductImpl extends PdfUtil implements PageProduct {

  @Override
  public PdfPTable setSalesReturnProductTable(List<InvoiceItem> invoiceItemList, CompanySettings companySettings, String printType) {

    float[] columns = new float[]{};
    PdfPTable productTable;
    if (printType.equals(SystemConstants.PRINT_PORTRAIT)) {
      if (companySettings.getShowManufacturer() == 1) {
        columns = salesReturnPortraitColumnsWithMfg();
      } else {
        columns = salesReturnPortraitColumnsWithOutMfg();
      }
    } else {
      if (companySettings.getShowManufacturer() == 1) {

        columns = salesReturnLandscapeColumnsWithMfg();
      } else {
        columns = salesReturnLandscapeColumnsWithOutMfg();
      }
    }
    float horizontal = 0;
    if (companySettings.getPrintHorizontalLine().equals(SystemConstants.PRINT_SHOW_HORIZONTAL_LINE)) {
      horizontal = 0.5f;
    }

    productTable = new PdfPTable(columns);
    productTable.getDefaultCell().setBorderWidthBottom(0);
    productTable.setTotalWidth(pageWidth);
    Paragraph para = new Paragraph();
    productTable.addCell(new PdfPCell(new Phrase("Sl#", fontProductHead())));
    if (companySettings.getShowManufacturer() == 1) {
      para = new Paragraph();
      para.add(new Phrase("Mfg", fontProductHead()));
      para.add(Chunk.NEWLINE);
      para.add(new Phrase("Co.", fontProductHead()));
      productTable.addCell(new PdfPCell(para));
    }
    para = new Paragraph();
    para.add(new Phrase("HSN ", fontProductHead()));
    para.add(Chunk.NEWLINE);
    para.add(new Phrase("Code", fontProductHead()));
    productTable.addCell(new PdfPCell(para));
    productTable.addCell(new PdfPCell(new Phrase("Product Name", fontProductHead())));
    if (printType.equals(SystemConstants.PRINT_SINGLE_LINE) || printType.equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
      productTable.addCell(new PdfPCell(new Phrase("Ref.No.", fontProductHead())));
      productTable.addCell(new PdfPCell(new Phrase("Ref.Date", fontProductHead())));
    }
    productTable.addCell(new PdfPCell(new Phrase("Pack", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Batch No.", fontProductHead())));
    para = new Paragraph();
    para.add(new Phrase("Exp.", fontProductHead()));
    para.add(Chunk.NEWLINE);
    para.add(new Phrase("Date", fontProductHead()));
    productTable.addCell(new PdfPCell(para));
    para = new Paragraph();
    para.add(new Phrase("MRP", fontProductHead()));
    para.add(Chunk.NEWLINE);
    para.add(new Phrase("(Rs.)", fontProductHead()));
    productTable.addCell(new PdfPCell(para));
    productTable.addCell(new PdfPCell(new Phrase("Qty", fontProductHead())));
    para = new Paragraph();
    para.add(new Phrase("Rate", fontProductHead()));
    para.add(Chunk.NEWLINE);
    para.add(new Phrase("(Rs.)", fontProductHead()));
    productTable.addCell(new PdfPCell(para));
    if (printType.equals(SystemConstants.PRINT_SINGLE_LINE) || printType.equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
      para = new Paragraph();
      para.add(new Phrase("S.Dis.", fontProductHead()));
      para.add(Chunk.NEWLINE);
      para.add(new Phrase("(Rs.)", fontProductHead()));
      productTable.addCell(new PdfPCell(para));
      para = new Paragraph();
      para.add(new Phrase("Dis.%", fontProductHead()));
      productTable.addCell(new PdfPCell(para));
      para = new Paragraph();
      para.add(new Phrase("P.Dis.", fontProductHead()));
      para.add(Chunk.NEWLINE);
      para.add(new Phrase("(Rs.)", fontProductHead()));
      productTable.addCell(new PdfPCell(para));
      para = new Paragraph();
      para.add(new Phrase("Dis.%", fontProductHead()));
      productTable.addCell(new PdfPCell(para));
    } else {
      para = new Paragraph();
      para.add(new Phrase("Dis.", fontProductHead()));
      para.add(Chunk.NEWLINE);
      para.add(new Phrase("(Rs.)", fontProductHead()));
      productTable.addCell(new PdfPCell(para));
    }
    if (printType.equals(SystemConstants.PRINT_SINGLE_LINE) || printType.equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
      para = new Paragraph();
      para.add(new Phrase("PTS", fontProductHead()));
      para.add(Chunk.NEWLINE);
      para.add(new Phrase("(Rs.)", fontProductHead()));
      productTable.addCell(new PdfPCell(para));
    }
//    para = new Paragraph();
//    para.add(new Phrase("PTR", fontProductHead()));
//    para.add(Chunk.NEWLINE);
//    para.add(new Phrase("(Rs.)", fontProductHead()));
//    productTable.addCell(new PdfPCell(para));
    para = new Paragraph();
    para.add(new Phrase("Tax", fontProductHead()));
    para.add(Chunk.NEWLINE);
    para.add(new Phrase("%", fontProductHead()));
    productTable.addCell(new PdfPCell(para));
    para = new Paragraph();
    para.add(new Phrase("Goods", fontProductHead()));
    para.add(Chunk.NEWLINE);
    para.add(new Phrase("Value", fontProductHead()));
    productTable.addCell(new PdfPCell(para));
    para = new Paragraph();
    para.add(new Phrase("Total", fontProductHead()));
    para.add(Chunk.NEWLINE);
    para.add(new Phrase("(Rs.)", fontProductHead()));
    productTable.addCell(new PdfPCell(para));

    productTable.setHeaderRows(1);

    int i = 1;
    for (InvoiceItem inv : invoiceItemList) {
      PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(i), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0.5f);
      cell.setBorderWidthLeft(0);
      cell.setMinimumHeight(cellHeight);
      productTable.addCell(cell);
//      mfg
      if (companySettings.getShowManufacturer() == 1) {
        cell = new PdfPCell(new Phrase(inv.getMfgCode(), fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0.5f);
        cell.setMinimumHeight(cellHeight);
        productTable.addCell(cell);
      }
//      HSN
      cell = new PdfPCell(new Phrase(String.valueOf(inv.getHsnCode()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthLeft(0);
      cell.setBorderWidthRight(0.5f);
      cell.setMinimumHeight(cellHeight);
      productTable.addCell(cell);
//      Product Name
      cell = new PdfPCell(new Phrase(String.valueOf(inv.getProductName()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthLeft(0);
      cell.setBorderWidthRight(0.5f);
      cell.setMinimumHeight(cellHeight);
      productTable.addCell(cell);
      if (printType.equals(SystemConstants.PRINT_SINGLE_LINE) || printType.equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
        //      Reference No
        cell = new PdfPCell(new Phrase(String.valueOf(inv.getRefInvoiceNo()), fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0.5f);
        cell.setMinimumHeight(cellHeight);
        productTable.addCell(cell);
        //      Reference Date
        if (inv.getRefInvoiceDate() != null) {
          cell = new PdfPCell(new Phrase(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(inv.getRefInvoiceDate()), fontProduct()));
        }
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0.5f);
        cell.setMinimumHeight(cellHeight);
        productTable.addCell(cell);
      }
//      Pack Size
      cell = new PdfPCell(new Phrase(String.valueOf(inv.getPackSize()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthRight(0.5f);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);
//      Batch No
      cell = new PdfPCell(new Phrase(inv.getBatchNo(), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_LEFT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthRight(0.5f);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);
//      Expiry Date
      cell = new PdfPCell(new Phrase(String.valueOf(inv.getExpiryDate()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthLeft(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthRight(0.5f);
      productTable.addCell(cell);
//      MRP
      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getValueMrp(), 2), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0.5f);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);
//      Product Qty
      para = new Paragraph();
      String prodQty = String.valueOf(inv.getProductQty());
      if (!prodQty.contains(".")) {
        prodQty = String.valueOf(inv.getProductQty().intValue());
      }
      para.add(new Phrase(prodQty, fontProduct()));
      if (inv.getProductQtyFree() != null) {
        if (inv.getProductQtyFree() > 0) {
          para.add(new Phrase(" +" + inv.getProductQtyFree(), fontProduct()));
        }
      }
      cell = new PdfPCell(para);
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0.5f);
      cell.setBorderWidthLeft(0);
      cell.setMinimumHeight(cellHeight);
      productTable.addCell(cell);
//      Rate
      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getValueRate(), 2), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0.5f);
      cell.setBorderWidthLeft(0);
      cell.setMinimumHeight(cellHeight);
      productTable.addCell(cell);
//      Discount
      if (printType.equals(SystemConstants.PRINT_SINGLE_LINE) || printType.equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
        cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getSchemeDiscountValue(), 2).equals("0.00") ? "" : AppView.formatDecimal(inv.getSchemeDiscountValue(), 2), fontProduct()));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0);
        cell.setMinimumHeight(cellHeight);
        cell.setBorderWidthRight(0.5f);
        productTable.addCell(cell);
        cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getSchemeDiscountPercentage(), 2).equals("0.00") ? "" : AppView.formatDecimal(inv.getSchemeDiscountPercentage(), 2), fontProduct()));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0);
        cell.setMinimumHeight(cellHeight);
        cell.setBorderWidthRight(0.5f);
        productTable.addCell(cell);
        cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getProductDiscountValue(), 2).equals("0.00") ? "" : AppView.formatDecimal(inv.getProductDiscountValue(), 2), fontProduct()));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0);
        cell.setMinimumHeight(cellHeight);
        cell.setBorderWidthRight(0.5f);
        productTable.addCell(cell);
        cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getProductDiscountPercentage(), 2).equals("0.00") ? "" : AppView.formatDecimal(inv.getProductDiscountPercentage(), 2), fontProduct()));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0);
        cell.setMinimumHeight(cellHeight);
        cell.setBorderWidthRight(0.5f);
        productTable.addCell(cell);
      } else {
        if (inv.getSchemeDiscountValue() != null && inv.getProductDiscountValue() != null) {
          para = new Paragraph();
          para.add(new Phrase(AppView.formatDecimal(inv.getSchemeDiscountValue(), 2).equals("0.00") ? "" : AppView.formatDecimal(inv.getSchemeDiscountValue(), 2), fontProduct()));
          para.add(new Phrase((!AppView.formatDecimal(inv.getSchemeDiscountValue(), 2).equals("0.00") && !AppView.formatDecimal(inv.getProductDiscountValue(), 2).equals("0.00")) ? (Chunk.NEWLINE + "+") : "", fontProduct()));
          para.add(new Phrase(AppView.formatDecimal(inv.getProductDiscountValue(), 2).equals("0.00") ? "" : AppView.formatDecimal(inv.getProductDiscountValue(), 2), fontProduct()));
        } else {
          if (inv.getSchemeDiscountValue() != null) {
            para.add(new Phrase(AppView.formatDecimal(inv.getSchemeDiscountValue(), 2).equals("0.00") ? "" : AppView.formatDecimal(inv.getSchemeDiscountValue(), 2), fontProduct()));
          }
          if (inv.getProductDiscountValue() != null) {
            para.add(new Phrase(AppView.formatDecimal(inv.getProductDiscountValue(), 2).equals("0.00") ? "" : AppView.formatDecimal(inv.getProductDiscountValue(), 2), fontProduct()));
          }
        }
        cell = new PdfPCell(para);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0);
        cell.setMinimumHeight(cellHeight);
        cell.setBorderWidthRight(0.5f);
        productTable.addCell(cell);
      }
//      PTS
      if (printType.equals(SystemConstants.PRINT_SINGLE_LINE) || printType.equals(SystemConstants.PRINT_MULTIPLE_LINE)) {
        cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getValuePts(), 2), fontProduct()));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0.5f);
        cell.setMinimumHeight(cellHeight);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);
      }
//      PTR
//      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getValuePtr(), 2), fontProduct()));
//      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//      cell.setBorderWidthBottom(horizontal);
//      cell.setBorderWidthTop(0);
//      cell.setMinimumHeight(cellHeight);
//      cell.setBorderWidthRight(0);
//      cell.setBorderWidthLeft(0);
//      productTable.addCell(cell);
//      Tax Rate
      cell = new PdfPCell(new Phrase(String.valueOf(inv.getCgstTaxRate() + inv.getSgstTaxRate()), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthRight(0.5f);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);
//      Goods Value
      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getValueGoods(), 2), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0.5f);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);
//      Taxable Amount
      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getTaxableAmount(), 2), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);
      i++;
    }
    return productTable;
  }

  @Override
  public PdfPTable setSalesServicesInvoiceProduct(List<SalesServicesInvoiceItem> salesServicesInvoiceItemList, CompanySettings companySettings) {
    float[] columns = salesServicesInvoiceColumn();
    PdfPTable productTable;

    float horizontal = 0;
    if (companySettings.getPrintHorizontalLine().equals(SystemConstants.PRINT_SHOW_HORIZONTAL_LINE)) {
      horizontal = 0.5f;
    }

    productTable = new PdfPTable(columns);

    productTable.getDefaultCell().setBorderWidthBottom(0);
    productTable.setTotalWidth(pageWidth);
    productTable.addCell(new PdfPCell(new Phrase("Sno", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Services", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("SAC Code", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Tax Rate", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("TDS Rate", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Taxable Value", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("SGST", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("CGST", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("IGST", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("TDS", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Total", fontProductHead())));

//    para = new Paragraph();
//    para.add(new Phrase("Total", fontProductHead()));
//    para.add(Chunk.NEWLINE);
//    para.add(new Phrase("(Rs.)", fontProductHead()));
//    productTable.addCell(new PdfPCell(para));
    productTable.setHeaderRows(1);

    int i = 1;
    for (SalesServicesInvoiceItem inv : salesServicesInvoiceItemList) {
      PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(i), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(String.valueOf(inv.getCommodityId().getTitle()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setBorderWidthLeft(0);
      cell.setMinimumHeight(cellHeight);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(String.valueOf(inv.getCommodityId().getHsnSacCode()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthLeft(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthRight(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(String.valueOf(inv.getCommodityId().getSalesTaxCodeId().getRatePercentage()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      if (inv.getCommodityId().getTdsTaxCodeId() != null) {
        cell = new PdfPCell(new Phrase(inv.getCommodityId().getTdsTaxCodeId().getRatePercentage() != null ? inv.getCommodityId().getTdsTaxCodeId().getRatePercentage().toString() : "", fontProduct()));
      } else {
        cell = new PdfPCell(new Phrase(""));
      }
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthRight(0);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getTaxableValue(), 2), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getSgstAmount(), 2), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getCgstAmount(), 2), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getIgstAmount(), 2), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getTdsAmount(), 2), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(AppView.formatDecimal(inv.getNetValue(), 2), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);
      i++;
    }
    return productTable;
  }

  @Override
  public PdfPTable setDebitCreditNoteProduct(List<DebitCreditNoteItem> debitCreditNoteItemList, CompanySettings companySettings, Integer taxableInvoice, boolean intrastate, Integer supplierOrCustomer, Integer sezZone) {
    float[] columns = new float[]{};
    PdfPTable productTable;
    if (intrastate && sezZone.intValue() == 0) {
      columns = debitCreditNoteIntrastateColumn();
    } else {
      columns = debitCreditNoteInterstateColumn();
    }

    float horizontal = 0;
    if (companySettings.getPrintHorizontalLine().equals(SystemConstants.PRINT_SHOW_HORIZONTAL_LINE)) {
      horizontal = 0.5f;
    }

    productTable = new PdfPTable(columns);

    productTable.getDefaultCell().setBorderWidthBottom(0);
    productTable.setTotalWidth(pageWidth);
    productTable.addCell(new PdfPCell(new Phrase("Sl#", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Title", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("HSN / SAC", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Narration", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Ref. Invoice No", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Ref. Invoice Date", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Rate", fontProductHead())));
    if (taxableInvoice == 1 && intrastate && sezZone.intValue() == 0) {
      productTable.addCell(new PdfPCell(new Phrase("SGST", fontProductHead())));
      productTable.addCell(new PdfPCell(new Phrase("CGST", fontProductHead())));
    }
    if (!intrastate || sezZone.intValue() == 1) {
      productTable.addCell(new PdfPCell(new Phrase("IGST", fontProductHead())));
    }
    productTable.addCell(new PdfPCell(new Phrase("Total", fontProductHead())));

//    para = new Paragraph();
//    para.add(new Phrase("Total", fontProductHead()));
//    para.add(Chunk.NEWLINE);
//    para.add(new Phrase("(Rs.)", fontProductHead()));
//    productTable.addCell(new PdfPCell(para));
    productTable.setHeaderRows(1);

    int i = 1;
    for (DebitCreditNoteItem debt : debitCreditNoteItemList) {
      PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(i), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthRight(0);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(String.valueOf(debt.getTitle()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(String.valueOf(debt.getHsnSacCode()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(String.valueOf(debt.getDescription() == null ? "" : debt.getDescription()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      String refInvNo = supplierOrCustomer == 1 ? (debt.getProductEntryDetailId() != null ? debt.getProductEntryDetailId().getProductEntryId().getInvoiceNo() : null) : debt.getRefInvoiceNo();
      cell = new PdfPCell(new Phrase(String.valueOf(refInvNo == null ? "" : refInvNo), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthLeft(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthRight(0);
      productTable.addCell(cell);

      AppView appview = new AppView();
      cell = new PdfPCell(new Phrase(appview.date(debt.getRefInvoiceDate()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(AppView.formatDecimal(debt.getTaxableValue(), 2), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      if (taxableInvoice == 1 && intrastate && sezZone.intValue() == 0) {
        cell = new PdfPCell(new Phrase(AppView.formatDecimal(debt.getSgstAmount(), 2), fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0);
        cell.setMinimumHeight(cellHeight);
        cell.setBorderWidthRight(0);
        productTable.addCell(cell);

        cell = new PdfPCell(new Phrase(AppView.formatDecimal(debt.getCgstAmount(), 2), fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthLeft(0);
        cell.setMinimumHeight(cellHeight);
        productTable.addCell(cell);
      }
      cell = new PdfPCell();
      if (taxableInvoice == 1 && (!intrastate || sezZone.intValue() == 1)) {
        cell = new PdfPCell(new Phrase(AppView.formatDecimal(debt.getIgstAmount(), 2), fontProduct()));
        cell.setBorderWidthBottom(horizontal);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        cell.setMinimumHeight(cellHeight);
        cell.setBorderWidthLeft(0);
        productTable.addCell(cell);
      }

      cell = new PdfPCell(new Phrase(AppView.formatDecimal(debt.getNetValue(), 2), fontProduct()));
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setMinimumHeight(cellHeight);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);
      i++;
    }
    return productTable;
  }

  public PdfPTable emptyRows(float[] columns, float width, float expectedHeight, float sumTableHeight, Rectangle pageSize) {
   return ExcelUtil.emptyRows(columns, width, expectedHeight, sumTableHeight, pageSize);
  }

  @Override
  public PdfPTable setProductItems(ItemTable itemTable, PdfWriter writer, float pageWidth) throws Exception {
    float[] columns = itemTable.getColumnWidth();
    final Set<Entry<String, Boolean>> mapValues = itemTable.getCellMap().entrySet();
    final int maplength = mapValues.size();
    final Entry<String, Boolean>[] headerCells = new Entry[maplength];
    mapValues.toArray(headerCells);

    PdfPTable productTable;
    productTable = new PdfPTable(columns);
    productTable.getDefaultCell().setBorderWidthBottom(0);
    productTable.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
    productTable.setTotalWidth(pageWidth);
    productTable.setLockedWidth(true);
    for (CellProperty cp : itemTable.getCellProperties()) {
      if (cp.isLandscape() || cp.isPortrait()) {
        if (cp.isPortrait() == itemTable.isPortrait() || (cp.isLandscape() == !itemTable.isPortrait() && cp.isLandscape())) {
          productTable.addCell(new PdfPCell(new Phrase(cp.getText(), fontProductHead())));
        }
      }
    }
    Map<String, Boolean> dummyCells = new LinkedHashMap<>();
    productTable.setHeaderRows(1);
    for (CellProperty data : itemTable.getData()) {
      if (isShowable(headerCells, dummyCells, itemTable.getCellProperties().size())) {
        PdfPCell cell = new PdfPCell(new Phrase(data.getText(), fontProduct()));
        cell.setHorizontalAlignment(data.getAlignment());
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0f);
        cell.setBorderWidthRight(0.4f);
        cell.setMinimumHeight(cellHeight);
        if (dummyCells.size() == 1) {
          cell.setBorderWidthLeft(0.4f);
        }
        productTable.addCell(cell);
        productTable.getTotalHeight();
      }
    }
    productTable.getTotalHeight();
    return productTable;
  }

  public boolean isShowable(Entry<String, Boolean>[] headerCells, Map<String, Boolean> dummyCells, int columns) {
    boolean flag;
    if (dummyCells.isEmpty()) {
      dummyCells.put(headerCells[0].getKey(), headerCells[0].getValue());
      flag = headerCells[0].getValue();
    } else {
      dummyCells.put(headerCells[dummyCells.size()].getKey(), headerCells[dummyCells.size()].getValue());
      flag = headerCells[dummyCells.size() - 1].getValue();
    }
    if (dummyCells.size() == columns) {
      dummyCells.clear();
    }
    return flag;
  }

  @Override
  public PdfPTable salesCommissionTable(List<VendorClaimDetail> claimDetailList) {
    PdfPTable productTable;
    float horizontal = 0;
    float[] columns = new float[]{14, 8, 8, 10, 10, 10, 10, 10, 10, 10};
    productTable = new PdfPTable(columns);
    productTable.getDefaultCell().setBorderWidthBottom(0);
    productTable.setTotalWidth(pageWidth);
    productTable.addCell(new PdfPCell(new Phrase("Description of Services", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("HSN", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Taxable Value", fontProductHead())));

    PdfPTable cgstTable = new PdfPTable(1);
    cgstTable.getDefaultCell().setPadding(0);
    cgstTable.addCell(new PdfPCell(new Phrase("CGST", fontProductHead())));
    PdfPTable cgst = new PdfPTable(2);
    cgst.addCell(new PdfPCell(new Phrase("Rate", fontProductHead())));
    cgst.addCell(new PdfPCell(new Phrase("Amount", fontProductHead())));
    cgst.setPaddingTop(0);
    cgstTable.addCell(cgst);
    PdfPCell cgstCell = new PdfPCell(cgstTable);
    cgstCell.setColspan(2);
    productTable.addCell(cgstCell);

    PdfPTable sgstTable = new PdfPTable(1);
    sgstTable.getDefaultCell().setPadding(0);
    sgstTable.addCell(new PdfPCell(new Phrase("SGST", fontProductHead())));
    PdfPTable sgst = new PdfPTable(2);
    sgst.addCell(new PdfPCell(new Phrase("Rate", fontProductHead())));
    sgst.addCell(new PdfPCell(new Phrase("Amount", fontProductHead())));
    sgst.setPaddingTop(0);
    sgstTable.addCell(cgst);
    PdfPCell sgstCell = new PdfPCell(sgstTable);
    sgstCell.setColspan(2);
    productTable.addCell(sgstCell);

    PdfPTable igstTable = new PdfPTable(1);
    igstTable.getDefaultCell().setPadding(0);
    igstTable.addCell(new PdfPCell(new Phrase("IGST", fontProductHead())));
    PdfPTable igst = new PdfPTable(2);
    igst.addCell(new PdfPCell(new Phrase("Rate", fontProductHead())));
    igst.addCell(new PdfPCell(new Phrase("Amount", fontProductHead())));
    igst.setPaddingTop(0);
    igstTable.addCell(igst);
    PdfPCell igstCell = new PdfPCell(igstTable);
    igstCell.setColspan(2);
    productTable.addCell(igstCell);
    productTable.addCell(new PdfPCell(new Phrase("Total", fontProductHead())));

    int i = 1;
    for (VendorClaimDetail detail : claimDetailList) {
      PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(i), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);
    }

    return productTable;
  }

  @Override
  public PdfPTable pageFillRows(PdfWriter writer, Document document, float[] columns, float footerHeight, String printType) {
    PdfPTable table = new PdfPTable(columns);
    table.setTotalWidth(pageWidth);
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorderWidthBottom(0);
    table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
    if (isBreakable(writer, footerHeight)) {
      while (writer.getVerticalPosition(false) >= table.getTotalHeight()) {
        int i = 0;
        for (float arr : columns) {
          PdfPCell cell = new PdfPCell(new Phrase(""));
          if (i == 0) {
            cell.setBorderWidthLeft(0.5F);
          } else {
            cell.setBorderWidthLeft(0);
          }
          cell.setBorderWidthTop(0);
          cell.setPadding(0);
          cell.setFixedHeight(1);
          cell.setBorderWidthBottom(0);
          table.addCell(cell);
          i++;
        }
      }

    }
    table.setSplitLate(true);
    return table;
  }

  private boolean isBreakable(PdfWriter writer, float height) {
    if (writer.getVerticalPosition(false) <= height + 30) {
      return true;
    }
    return false;
  }

  @Override
  public PdfPTable setChequeReceiptProduct(AccountingTransactionDetail partyDetail, CompanySettings companySettings) {
    float[] columns = new float[]{5, 20, 20, 20, 20};
    PdfPTable productTable;
    productTable = new PdfPTable(columns);
    float horizontal = 0;
    boolean cheque = (partyDetail.getDetailItem().get(0).getDocumentTypeId().getId().intValue() == AccountingConstant.DOC_TYPE_RECEIPT_CHEQUE.getId());
    productTable.getDefaultCell().setBorderWidthBottom(0);
    productTable.setTotalWidth(pageWidth);
    productTable.addCell(new PdfPCell(new Phrase("Sl#", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Towards By", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase(cheque ? "Cheque No." : "Document No.", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase(cheque ? "Cheque Date" : "Document Date", fontProductHead())));
    productTable.addCell(new PdfPCell(new Phrase("Amount", fontProductHead())));
    productTable.setHeaderRows(1);
    AppView appview = new AppView();
    int i = 1;
    for (AccountingTransactionDetailItem detail : partyDetail.getDetailItem()) {
      PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(i), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(String.valueOf(detail.getDocumentTypeId().getTitle()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(String.valueOf(detail.getDocumentNumber()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(String.valueOf(appview.date(detail.getDocumentDate())), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setBorderWidthLeft(0);
      productTable.addCell(cell);

      cell = new PdfPCell(new Phrase(String.valueOf(detail.getNetAmount()), fontProduct()));
      cell.setBorderWidthBottom(horizontal);
      cell.setBorderWidthTop(0);
      cell.setBorderWidthRight(0);
      cell.setBorderWidthLeft(0);
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
      productTable.addCell(cell);
    }

    return productTable;
  }

}
