/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.print.Impl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.util.List;
import java.util.Map;
import spica.constant.AccountingConstant;
import spica.scm.common.InvoiceGroup;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.Manufacture;
import spica.scm.domain.SalesInvoice;
import spica.scm.print.PageFooter;
import spica.scm.print.PdfUtil;
import spica.scm.util.CurencyToText;
import spica.sys.UserRuntimeView;
import wawo.app.faces.AppView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sujesh
 */
public class PageFooterImpl extends PdfUtil implements PageFooter {

  @Override
  public PdfPTable setItemQty(int totalItem, String netQuantity, Integer boxNo, Double weight, Double sDisc, Double pDisc, Double creditSettlement) {

    PdfPTable table = new PdfPTable(new float[]{16, 16, 16, 16, 17, 17});
    if (creditSettlement != null) {
      table = new PdfPTable(new float[]{15.5f, 15.5f, 15.5f, 15.5f, 16.5f, 16.5f, 17});
    }
    table.getDefaultCell().setBorder(0);
    table.setWidthPercentage(100);

    Paragraph para = new Paragraph();
    para.add(new Phrase("Total items: ", fontTax()));
    para.add(new Phrase(String.valueOf(totalItem), fontBill()));
    PdfPCell totalItemCell = new PdfPCell(para);
    totalItemCell.setPaddingBottom(5);

    para = new Paragraph();
    para.add(new Phrase("Total Qty: ", fontTax()));
    para.add(new Phrase(String.valueOf(netQuantity == null ? "" : netQuantity), fontBill()));
    PdfPCell totalQty = new PdfPCell(para);
    totalQty.setPaddingBottom(5);

    para = new Paragraph();
    para.add(new Phrase("No. of Cases: ", fontTax()));
    para.add(new Phrase(String.valueOf(boxNo == null ? " " : boxNo), fontBill()));
    PdfPCell cases = new PdfPCell(para);
    cases.setPaddingBottom(5);

    para = new Paragraph();
    para.add(new Phrase("Net Weight: ", fontTax()));
    para.add(new Phrase(String.valueOf(weight == null ? " " : weight), fontBill()));
    PdfPCell weightCell = new PdfPCell(para);
    weightCell.setPaddingBottom(5);

    para = new Paragraph();
    para.add(new Phrase("S.Disc: ", fontTax()));
    para.add(new Phrase(String.valueOf(sDisc == null ? " " : sDisc), fontBill()));
    PdfPCell sDiscCell = new PdfPCell(para);
    sDiscCell.setPaddingBottom(5);

    para = new Paragraph();
    para.add(new Phrase("P.Disc: ", fontTax()));
    para.add(new Phrase(String.valueOf(pDisc == null ? " " : pDisc), fontBill()));
    PdfPCell pDiscCell = new PdfPCell(para);
    pDiscCell.setPaddingBottom(5);

    PdfPCell creditCell = new PdfPCell();
    if (creditSettlement != null) {
      para = new Paragraph();
      para.add(new Phrase("Credit Adjustment: ", fontTax()));
      para.add(new Phrase(String.valueOf(creditSettlement), fontBill()));
      creditCell = new PdfPCell(para);
      pDiscCell.setPaddingBottom(5);
    }

    weightCell.setBorderWidthRight(1);
    table.addCell(totalItemCell).setBorder(0);
    table.addCell(totalQty).setBorder(0);
    table.addCell(cases).setBorder(0);
    table.addCell(weightCell).setBorder(0);
    table.addCell(sDiscCell).setBorder(0);
    table.addCell(pDiscCell).setBorder(0);
    if (creditSettlement != null) {
      table.addCell(creditCell).setBorder(0);
    }

    return table;
  }

  @Override
  public PdfPTable setInterStateTaxation(List<InvoiceGroup> invoiceGroupList) {
    PdfPTable table = new PdfPTable(3);
    table.getDefaultCell().setBorder(0);
    PdfPCell gstLabel = new PdfPCell(new Phrase("GST %", fontTax()));
    PdfPCell taxableLabel = new PdfPCell(new Phrase("Taxable", fontTax()));
    PdfPCell taxValueLabel = new PdfPCell(new Phrase("Tax value", fontTax()));
    gstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    taxableLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    taxValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    gstLabel.setBorderWidthLeft(0);
    taxableLabel.setBorderWidthLeft(0);
    taxValueLabel.setBorderWidthRight(0);
    gstLabel.setBorderWidthTop(0);
    taxableLabel.setBorderWidthTop(0);
    taxValueLabel.setBorderWidthTop(0);
    table.addCell(gstLabel);
    table.addCell(taxableLabel);
    table.addCell(taxValueLabel);

    // Add Tax Values
    invoiceGroupList.forEach((invgrp) -> {
      PdfPCell gstValue = new PdfPCell(new Phrase(String.valueOf(invgrp.getTaxCode().getRatePercentage()), fontTax()));
      PdfPCell taxableValue = new PdfPCell(new Phrase(AppView.formatDecimal(invgrp.getAssessableValue(), 2), fontTax()));
      PdfPCell taxValue = new PdfPCell(new Phrase(AppView.formatDecimal(invgrp.getInvoiceIgstValue(), 2), fontTax()));
      gstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      taxableValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      taxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      gstValue.setBorderWidthLeft(0);
      taxableValue.setBorderWidthLeft(0);
      gstValue.setBorderWidthTop(0);
      taxableValue.setBorderWidthTop(0);
      taxValue.setBorderWidthTop(0);
      table.addCell(gstValue);
      table.addCell(taxableValue);
      table.addCell(taxValue);
    });

    return table;
  }

  @Override
  public PdfPTable setAmountWords(double invoiceAmount, int showManufacturer, List<Manufacture> manufactureList, String bank, String accNo, String ifsc) {
    PdfPTable table = new PdfPTable(1);
    Paragraph para = new Paragraph();
    para.add(new Phrase("Invoice Amount in words:", fontTax()));
    para.add(new Phrase(CurencyToText.india(invoiceAmount), fontBill()));
    if (invoiceAmount > 0) {
      para.add(new Phrase(" Only", fontBill()));
    }
    PdfPCell amountWords = new PdfPCell(para);
    amountWords.setHorizontalAlignment(Element.ALIGN_LEFT);
    amountWords.setVerticalAlignment(Element.ALIGN_CENTER);
    PdfPCell mfgAbbreviation = new PdfPCell();

    if (showManufacturer == 1 && manufactureList != null) {
      para = new Paragraph();
      for (Manufacture mfg : manufactureList) {
        int index = 0;
        para.add(new Phrase(mfg.getCode() + "-" + mfg.getName(), fontTax()));
        if (index > 1) {
          para.add(new Phrase(",", fontTax()));
        }
        index++;
      }
      mfgAbbreviation = new PdfPCell(para);

    }
    para = new Paragraph();
    if (bank != null) {
      para.add(new Phrase("Bank: " + bank + "; Acc.No: " + accNo + "; IFSC: " + ifsc, fontTax()));
    }
    PdfPCell bankDetails = new PdfPCell(para);
    bankDetails.setHorizontalAlignment(Element.ALIGN_LEFT);
    bankDetails.setVerticalAlignment(Element.ALIGN_BOTTOM);
    table.addCell(amountWords).setBorder(0);
    table.addCell(mfgAbbreviation).setBorder(0);
    if (bank != null) {
      table.addCell(bankDetails).setBorder(0);
    }

    return table;
  }

  @Override
  public PdfPTable setBillAmount(Map<String, String> billMap, boolean isSales) {
    PdfPTable table = new PdfPTable(new float[]{50, 50});

    if (billMap.containsKey("GVAL")) {
      PdfPCell goodsValueLabel = new PdfPCell(new Phrase("Goods Value", fontCalculation()));
      PdfPCell goodsValue = new PdfPCell(new Phrase(billMap.get("GVAL"), fontCalculation()));
      goodsValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(goodsValueLabel).setBorder(0);
      table.addCell(goodsValue).setBorder(0);
    }
    if (billMap.containsKey("INVD")) {
      PdfPCell invDiscLabel = new PdfPCell(new Phrase("Inv. Disc.", fontCalculation()));
      PdfPCell invDiscValue = new PdfPCell(new Phrase("-" + billMap.get("INVD"), fontCalculation()));
      invDiscLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
      invDiscValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      if (isSales) {
        if (!billMap.containsKey("CASHDTAX") && !billMap.containsKey("FREIGHT1") && !billMap.containsKey("FREIGHT2")) {
          invDiscValue.setPaddingBottom(5f);
          invDiscLabel.setPaddingBottom(5f);
        }
      }
      table.addCell(invDiscLabel).setBorder(0);
      table.addCell(invDiscValue).setBorder(0);
    }
    if (billMap.containsKey("CASHDTAX")) {
      PdfPCell cashDiscLabel = new PdfPCell(new Phrase("Cash Disc.", fontCalculation()));
      PdfPCell cashDiscValue = new PdfPCell(new Phrase("-" + billMap.get("CASHDTAX"), fontCalculation()));
      cashDiscLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cashDiscValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      if (isSales) {
        if (!billMap.containsKey("FREIGHT1") && !billMap.containsKey("FREIGHT2")) {
          cashDiscLabel.setPaddingBottom(5f);
          cashDiscValue.setPaddingBottom(5f);
        }
      }
      table.addCell(cashDiscLabel).setBorder(0);
      table.addCell(cashDiscValue).setBorder(0);
    }
    if (billMap.containsKey("FREIGHT1")) {
      PdfPCell goodsValueLabel = new PdfPCell(new Phrase(billMap.get("FREIGHT1LABEL"), fontCalculation()));
      PdfPCell goodsValue = new PdfPCell(new Phrase(billMap.get("FREIGHT1"), fontCalculation()));
      goodsValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      if (!billMap.containsKey("FREIGHT2")) {
        goodsValueLabel.setPaddingBottom(5f);
        goodsValue.setPaddingBottom(5f);
      }
      table.addCell(goodsValueLabel).setBorder(0);
      table.addCell(goodsValue).setBorder(0);
    }
    if (billMap.containsKey("FREIGHT2")) {
      PdfPCell goodsValueLabel = new PdfPCell(new Phrase(billMap.get("FREIGHT2LABEL"), fontCalculation()));
      PdfPCell goodsValue = new PdfPCell(new Phrase(billMap.get("FREIGHT2"), fontCalculation()));
      goodsValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      goodsValueLabel.setPaddingBottom(5f);
      goodsValue.setPaddingBottom(5f);
      table.addCell(goodsValueLabel).setBorder(0);
      table.addCell(goodsValue).setBorder(0);
    }
    if (billMap.containsKey("TAXV")) {
      PdfPCell taxValueLabel = new PdfPCell(new Phrase("Taxable Value", fontCalculation()));
      PdfPCell taxValue = new PdfPCell(new Phrase(billMap.get("TAXV"), fontCalculation()));
      taxValueLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
      taxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      taxValue.setBorderWidth(0);
      if (isSales) {
        taxValue.setBorderWidthTop(0.2f);
      }
      table.addCell(taxValueLabel).setBorder(0);
      table.addCell(taxValue);
    }
    if (billMap.containsKey("KFCESS")) {
      PdfPCell goodsValueLabel = new PdfPCell(new Phrase("K.F. Cess", fontCalculation()));
      PdfPCell goodsValue = new PdfPCell(new Phrase(billMap.get("KFCESS"), fontCalculation()));
      goodsValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(goodsValueLabel).setBorder(0);
      table.addCell(goodsValue).setBorder(0);
    }
    if (billMap.containsKey("Taxable Value")) {
      PdfPCell gstValueLabel = new PdfPCell(new Phrase("Taxable Value", fontCalculation()));
      PdfPCell gstValue = new PdfPCell(new Phrase(billMap.get("Taxable Value"), fontCalculation()));
      gstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(gstValueLabel).setBorder(0);
      table.addCell(gstValue).setBorder(0);
    }
    if (billMap.containsKey("GST")) {
      PdfPCell gstValueLabel = new PdfPCell(new Phrase("GST Value", fontCalculation()));
      PdfPCell gstValue = new PdfPCell(new Phrase(billMap.get("GST"), fontCalculation()));
      gstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(gstValueLabel).setBorder(0);
      table.addCell(gstValue).setBorder(0);
    }
    if (billMap.containsKey("TDS")) {
      PdfPCell tdsValueLabel = new PdfPCell(new Phrase("TDS Value", fontCalculation()));
      PdfPCell tdsValue = new PdfPCell(new Phrase(billMap.get("TDS"), fontCalculation()));
      tdsValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(tdsValueLabel).setBorder(0);
      table.addCell(tdsValue).setBorder(0);
    }
    if (billMap.containsKey("L-TDS")) {
      PdfPCell tdsValueLabel = new PdfPCell(new Phrase("TDS Value", fontCalculation()));
      PdfPCell tdsValue = new PdfPCell(new Phrase(StringUtil.isEmpty(billMap.get("L-TDS")) ? "" : "-" + billMap.get("L-TDS"), fontCalculation()));
      tdsValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(tdsValueLabel).setBorder(0);
      table.addCell(tdsValue).setBorder(0);
    }
    if (billMap.containsKey("TOTAL")) {
      PdfPCell totalLabel = new PdfPCell(new Phrase("Total", fontCalculation()));
      PdfPCell total = new PdfPCell(new Phrase(billMap.get("TOTAL"), fontCalculation()));
      total.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(totalLabel).setBorder(0);
      table.addCell(total).setBorder(0);
    }
    if (billMap.containsKey("CASHD")) {
      PdfPCell cashDiscLabel = new PdfPCell(new Phrase("Cash Disc.", fontCalculation()));
      PdfPCell cashDiscValue = new PdfPCell(new Phrase("-" + billMap.get("CASHD"), fontCalculation()));
      cashDiscLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cashDiscValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(cashDiscLabel).setBorder(0);
      table.addCell(cashDiscValue).setBorder(0);
    }
    if (billMap.containsKey("TCS")) {
      PdfPCell tcsValueLabel = new PdfPCell(new Phrase("TCS Value", fontCalculation()));
      PdfPCell tcsValue = new PdfPCell(new Phrase(billMap.get("TCS"), fontCalculation()));
      tcsValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(tcsValueLabel).setBorder(0);
      table.addCell(tcsValue).setBorder(0);
    }
    if (billMap.containsKey("ROUND")) {
      PdfPCell roundOffLabel = new PdfPCell(new Phrase("Round Off", fontCalculation()));
      PdfPCell roundOffValue = new PdfPCell(new Phrase(billMap.get("ROUND"), fontCalculation()));
      roundOffLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
      roundOffValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(roundOffLabel).setBorder(0);
      table.addCell(roundOffValue).setBorder(0);
    }
    if (billMap.containsKey("EMPTY")) {
      PdfPCell emptyCell = new PdfPCell(new Phrase(" ", fontCalculation()));
      PdfPCell emptyValue = new PdfPCell(new Phrase(billMap.get("EMPTY"), fontCalculation()));
      table.addCell(emptyCell).setBorder(0);
      table.addCell(emptyValue).setBorder(0);
    }
    PdfPCell netAmountLabel = new PdfPCell(new Phrase("Net Amount", fontNetAmount()));
    PdfPCell netAmountValue = new PdfPCell(new Phrase(billMap.get("NET"), fontNetAmount()));
    netAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
    table.addCell(netAmountLabel).setBorder(0);
    table.addCell(netAmountValue).setBorder(0);

    return table;
  }

  @Override
  public PdfPTable setDeclaration(CompanySettings companySettings, String companyName, String districtName) {
    PdfPTable table = new PdfPTable(new float[]{60, 40});
    Paragraph dp = new Paragraph();
    if (companySettings != null && companySettings.getDisclaimer()) {
      for (String dis : getFooter(companySettings, districtName)) {
        dp.add(new Chunk(dis, fontDisclaimer()));
        dp.add(Chunk.NEWLINE);
      }
    }
    dp.setLeading(5, 0);
    PdfPCell declarationCell = new PdfPCell(dp);
    declarationCell.setLeading(8, 0);

    Paragraph name = new Paragraph();
    name.add(new Phrase("For ", fontTax()));
    name.add(new Phrase(companyName, fontBill()));
    PdfPCell signatureCell = new PdfPCell(name);
    name.setIndentationLeft(5);
    signatureCell.setFixedHeight(50);

    declarationCell.setPadding(3);
    signatureCell.setPadding(3);

    table.addCell(declarationCell);
    table.addCell(signatureCell);

    return table;
  }

  @Override
  public PdfPTable setWebMail(String email, String web) {
    PdfPTable table = new PdfPTable(1);
    Paragraph mailandWeb = new Paragraph();
    mailandWeb.add(new Phrase("Email :", fontTax()));
    mailandWeb.add(new Phrase(email != null ? email : "", fontTax()));
    mailandWeb.add(new Phrase("  Web :", fontTax()));
    mailandWeb.add(new Phrase(web != null ? web : "", fontTax()));
    PdfPCell cell = new PdfPCell(mailandWeb);
    cell.setBackgroundColor(BaseColor.WHITE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setBorderWidthLeft(0.5f);
    cell.setBorderWidthRight(0.5f);
    table.addCell(cell).setBorderWidthTop(0);
    return table;
  }

  @Override
  public PdfPTable setIntraStateTaxation(List<InvoiceGroup> invoiceGroupList) {
    PdfPTable table = new PdfPTable(7);
    table.getDefaultCell().setBorder(0);

    PdfPCell gstLabel = new PdfPCell(new Phrase("GST %", fontTax()));
    PdfPCell taxableLabel = new PdfPCell(new Phrase("Taxable", fontTax()));
    PdfPCell sgstLabel = new PdfPCell(new Phrase("SGST", fontTax()));
    PdfPCell staxLabel = new PdfPCell(new Phrase("Tax", fontTax()));
    PdfPCell cgstLabel = new PdfPCell(new Phrase("CGST", fontTax()));
    PdfPCell ctaxLabel = new PdfPCell(new Phrase("Tax", fontTax()));
    PdfPCell taxValueLabel = new PdfPCell(new Phrase("Tax value", fontTax()));
//    PdfPCell kfLabel = new PdfPCell(new Phrase("KFCess", fontTax()));

    gstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    taxableLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    sgstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    staxLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    cgstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    ctaxLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    taxValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
//    kfLabel.setHorizontalAlignment(Element.ALIGN_CENTER);

    gstLabel.setBorderWidthLeft(0);
    taxableLabel.setBorderWidthLeft(0);
    sgstLabel.setBorderWidthLeft(0);
    staxLabel.setBorderWidthLeft(0);
    cgstLabel.setBorderWidthLeft(0);
    ctaxLabel.setBorderWidthLeft(0);
    taxValueLabel.setBorderWidthLeft(0);
    taxValueLabel.setBorderWidthRight(0);
//    kfLabel.setBorderWidthRight(0);

    gstLabel.setBorderWidthTop(0);
    taxableLabel.setBorderWidthTop(0);
    sgstLabel.setBorderWidthTop(0);
    staxLabel.setBorderWidthTop(0);
    cgstLabel.setBorderWidthTop(0);
    ctaxLabel.setBorderWidthTop(0);
    taxValueLabel.setBorderWidthTop(0);
//    kfLabel.setBorderWidthTop(0);

    table.addCell(gstLabel);
    table.addCell(taxableLabel);
    table.addCell(sgstLabel);
    table.addCell(staxLabel);
    table.addCell(cgstLabel);
    table.addCell(ctaxLabel);
    table.addCell(taxValueLabel);
//    table.addCell(kfLabel);

    // Add Tax Values
    invoiceGroupList.forEach((invgrp) -> {
      PdfPCell gstValue = new PdfPCell(new Phrase(String.valueOf(invgrp.getTaxCode().getRatePercentage()), fontTax()));
      PdfPCell taxableValue = new PdfPCell(new Phrase(AppView.formatDecimal(invgrp.getAssessableValue(), 2), fontTax()));
      PdfPCell sgstValue = new PdfPCell(new Phrase(AppView.formatDecimal(UserRuntimeView.instance().getGstTaxCode(invgrp.getTaxCode(), AccountingConstant.TAX_TYPE_CGST).getRatePercentage(), 2), fontTax()));
      PdfPCell staxValue = new PdfPCell(new Phrase(AppView.formatDecimal(invgrp.getInvoiceCgstValue(), 2), fontTax()));
      PdfPCell cgstValue = new PdfPCell(new Phrase(AppView.formatDecimal(UserRuntimeView.instance().getGstTaxCode(invgrp.getTaxCode(), AccountingConstant.TAX_TYPE_SGST).getRatePercentage(), 2), fontTax()));
      PdfPCell ctaxValue = new PdfPCell(new Phrase(AppView.formatDecimal(invgrp.getInvoiceSgstValue(), 2), fontTax()));
      PdfPCell taxValue = new PdfPCell(new Phrase(AppView.formatDecimal(invgrp.getInvoiceIgstValue(), 2), fontTax()));
//      PdfPCell kfValue = new PdfPCell(new Phrase(AppView.formatDecimal(invgrp.get, 2), fontTax()));

      gstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      taxableValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      sgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      staxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      ctaxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      taxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

      table.addCell(gstValue);
      table.addCell(taxableValue);
      table.addCell(sgstValue);
      table.addCell(staxValue);
      table.addCell(cgstValue);
      table.addCell(ctaxValue);
      table.addCell(taxValue);
    });
    return table;
  }

  public String[] getFooter(CompanySettings companySettings, String districtName) {
    String disclaimer = "", dis[] = null;
    if (companySettings != null && companySettings.getPrintInvoiceFooter() != null) {
      disclaimer = (companySettings.getPrintInvoiceFooter().replaceAll("#company#", companySettings.getCompanyId().getCompanyName())).replaceAll("#district#", districtName);
    }
    dis = disclaimer.split("<br/>");
    disclaimer = "";
    for (String arr : dis) {
      disclaimer += arr;
    }
    dis = disclaimer.split("<br />");
    return dis;
  }

  @Override
  public PdfPTable setSalesServicesInvoiceTaxation(List<InvoiceGroup> invoiceGroupList, boolean isIntraStateServices) {

    PdfPTable table = new PdfPTable(10);
    table.getDefaultCell().setBorder(0);

    PdfPCell gstpLabel = new PdfPCell(new Phrase("GST %", fontTax()));
    gstpLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    gstpLabel.setBorderWidthLeft(0);
    gstpLabel.setBorderWidthTop(0);
    table.addCell(gstpLabel);

    PdfPCell itemCountLabel = new PdfPCell(new Phrase("Item Count", fontTax()));
    itemCountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    itemCountLabel.setBorderWidthLeft(0);
    itemCountLabel.setBorderWidthTop(0);
    table.addCell(itemCountLabel);

    PdfPCell taxableLabel = new PdfPCell(new Phrase("Taxable", fontTax()));
    taxableLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    taxableLabel.setBorderWidthLeft(0);
    taxableLabel.setBorderWidthTop(0);
    table.addCell(taxableLabel);

    PdfPCell cgstpLabel = new PdfPCell(new Phrase("CGST %", fontTax()));
    cgstpLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    cgstpLabel.setBorderWidthLeft(0);
    cgstpLabel.setBorderWidthTop(0);
    table.addCell(cgstpLabel);

    PdfPCell cgstValueLabel = new PdfPCell(new Phrase("Value", fontTax()));
    cgstValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    cgstValueLabel.setBorderWidthLeft(0);
    cgstValueLabel.setBorderWidthTop(0);
    table.addCell(cgstValueLabel);

    PdfPCell sgstpLabel = new PdfPCell(new Phrase("SGST %", fontTax()));
    sgstpLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    sgstpLabel.setBorderWidthLeft(0);
    sgstpLabel.setBorderWidthTop(0);
    table.addCell(sgstpLabel);

    PdfPCell sgstValueLabel = new PdfPCell(new Phrase("Value", fontTax()));
    sgstValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    sgstValueLabel.setBorderWidthLeft(0);
    sgstValueLabel.setBorderWidthTop(0);
    table.addCell(sgstValueLabel);

    PdfPCell gstValueLabel = new PdfPCell(new Phrase("GST Value", fontTax()));
    gstValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    gstValueLabel.setBorderWidthLeft(0);
    gstValueLabel.setBorderWidthTop(0);
    table.addCell(gstValueLabel);

    PdfPCell tdsValueLabel = new PdfPCell(new Phrase("TDS Value", fontTax()));
    tdsValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    tdsValueLabel.setBorderWidthLeft(0);
    tdsValueLabel.setBorderWidthTop(0);
    table.addCell(tdsValueLabel);

    PdfPCell netValueLabel = new PdfPCell(new Phrase("Net value", fontTax()));
    netValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    netValueLabel.setBorderWidthRight(0);
    netValueLabel.setBorderWidthTop(0);
    table.addCell(netValueLabel);

    // Add Tax Values
    for (InvoiceGroup invgrp : invoiceGroupList) {
      PdfPCell gstp = new PdfPCell(new Phrase(String.valueOf(invgrp.getTaxCode().getRatePercentage() == null ? 0.00 : invgrp.getTaxCode().getRatePercentage()), fontTax()));
      gstp.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(gstp);

      PdfPCell itemCount = new PdfPCell(new Phrase(String.valueOf(invgrp.getProductQuantity() == null ? 0 : invgrp.getProductQuantity()), fontTax()));
      itemCount.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(itemCount);

      PdfPCell taxableValue = new PdfPCell(new Phrase(invgrp.getAssessableValue() == null ? "0.00" : AppView.formatDecimal(invgrp.getAssessableValue(), 2), fontTax()));
      taxableValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(taxableValue);

      PdfPCell cgstp = new PdfPCell(new Phrase(invgrp.getTaxCode().getRatePercentage() == null ? "" : isIntraStateServices ? AppView.formatDecimal(invgrp.getTaxCode().getRatePercentage() / 2, 2) : "", fontTax()));
      cgstp.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(cgstp);

      PdfPCell cgstValue = new PdfPCell(new Phrase(invgrp.getInvoiceCgstValue() == null ? "" : isIntraStateServices ? AppView.formatDecimal(invgrp.getInvoiceCgstValue(), 2) : "", fontTax()));
      cgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(cgstValue);

      PdfPCell sgstp = new PdfPCell(new Phrase(invgrp.getTaxCode().getRatePercentage() == null ? "" : isIntraStateServices ? AppView.formatDecimal(invgrp.getTaxCode().getRatePercentage() / 2, 2) : "", fontTax()));
      sgstp.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(sgstp);

      PdfPCell sgstValue = new PdfPCell(new Phrase(invgrp.getInvoiceSgstValue() == null ? "" : isIntraStateServices ? AppView.formatDecimal(invgrp.getInvoiceSgstValue(), 2) : "", fontTax()));
      sgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(sgstValue);

      PdfPCell gstValue = new PdfPCell(new Phrase(invgrp.getInvoiceIgstValue() == null ? "" : AppView.formatDecimal(invgrp.getInvoiceIgstValue(), 2), fontTax()));
      gstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(gstValue);

      PdfPCell tds = new PdfPCell(new Phrase(invgrp.getInvoiceTdsValue() == null ? "" : AppView.formatDecimal(invgrp.getInvoiceTdsValue(), 2), fontTax()));
      tds.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(tds);

      PdfPCell netValue = new PdfPCell(new Phrase(AppView.formatDecimal(invgrp.getInvoiceNetAmount(), 2), fontTax()));
      netValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(netValue);

    }
    return table;
  }

  @Override
  public PdfPTable setTotalItem(int totalItem) {
    PdfPTable table = new PdfPTable(1);
    table.getDefaultCell().setBorder(0);
    table.setWidthPercentage(100);

    Paragraph para = new Paragraph();
    para.add(new Phrase("Total items: ", fontTax()));
    para.add(new Phrase(String.valueOf(totalItem), fontBill()));
    PdfPCell totalItemCell = new PdfPCell(para);
    totalItemCell.setPaddingBottom(5);

    return table;
  }

  @Override
  public PdfPTable setDebitCreidtNoteTaxation(List<InvoiceGroup> invoiceGroupList, Integer taxableInvoice, boolean intraState, Integer sezZone) {
    int column = 0;
    if (taxableInvoice == 1 && intraState && sezZone.intValue() == 0) {
      column = 9;
    } else {
      column = 5;
    }
    PdfPTable table = new PdfPTable(column);
    table.getDefaultCell().setBorder(0);

    PdfPCell gstpLabel = new PdfPCell(new Phrase("GST %", fontTax()));
    gstpLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    gstpLabel.setBorderWidthLeft(0);
    gstpLabel.setBorderWidthTop(0);
    table.addCell(gstpLabel);

    PdfPCell itemCountLabel = new PdfPCell(new Phrase("Item Count", fontTax()));
    itemCountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    itemCountLabel.setBorderWidthLeft(0);
    itemCountLabel.setBorderWidthTop(0);
    table.addCell(itemCountLabel);

    PdfPCell taxableLabel = new PdfPCell(new Phrase("Taxable", fontTax()));
    taxableLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    taxableLabel.setBorderWidthLeft(0);
    taxableLabel.setBorderWidthTop(0);
    table.addCell(taxableLabel);
    if (taxableInvoice == 1 && intraState && sezZone.intValue() == 0) {
      PdfPCell cgstpLabel = new PdfPCell(new Phrase("CGST %", fontTax()));
      cgstpLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
      cgstpLabel.setBorderWidthLeft(0);
      cgstpLabel.setBorderWidthTop(0);
      table.addCell(cgstpLabel);

      PdfPCell cgstValueLabel = new PdfPCell(new Phrase("Value", fontTax()));
      cgstValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
      cgstValueLabel.setBorderWidthLeft(0);
      cgstValueLabel.setBorderWidthTop(0);
      table.addCell(cgstValueLabel);

      PdfPCell sgstpLabel = new PdfPCell(new Phrase("SGST %", fontTax()));
      sgstpLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
      sgstpLabel.setBorderWidthLeft(0);
      sgstpLabel.setBorderWidthTop(0);
      table.addCell(sgstpLabel);

      PdfPCell sgstValueLabel = new PdfPCell(new Phrase("Value", fontTax()));
      sgstValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
      sgstValueLabel.setBorderWidthLeft(0);
      sgstValueLabel.setBorderWidthTop(0);
      table.addCell(sgstValueLabel);
    }
    PdfPCell gstValueLabel = new PdfPCell();
    if (taxableInvoice == 1) {
      gstValueLabel = new PdfPCell(new Phrase("GST Value", fontTax()));
    }
    gstValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    gstValueLabel.setBorderWidthLeft(0);
    gstValueLabel.setBorderWidthTop(0);
    table.addCell(gstValueLabel);
    PdfPCell netValueLabel = new PdfPCell(new Phrase("Net value", fontTax()));
    netValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    netValueLabel.setBorderWidthRight(0);
    netValueLabel.setBorderWidthTop(0);
    table.addCell(netValueLabel);

    // Add Tax Values
    for (InvoiceGroup invgrp : invoiceGroupList) {
      PdfPCell gstp = new PdfPCell(new Phrase(String.valueOf(invgrp.getTaxCode().getRatePercentage() == null ? 0.00 : invgrp.getTaxCode().getRatePercentage()), fontTax()));
      gstp.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(gstp);

      PdfPCell itemCount = new PdfPCell(new Phrase(String.valueOf(invgrp.getProductQuantity() == null ? 0 : invgrp.getProductQuantity()), fontTax()));
      itemCount.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(itemCount);

      PdfPCell taxableValue = new PdfPCell(new Phrase(invgrp.getAssessableValue() == null ? "0.00" : AppView.formatDecimal(invgrp.getAssessableValue(), 2), fontTax()));
      taxableValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(taxableValue);
      if (taxableInvoice == 1 && intraState && sezZone.intValue() == 0) {
        PdfPCell cgstp = new PdfPCell(new Phrase(invgrp.getTaxCode().getRatePercentage() == null ? "" : AppView.formatDecimal(UserRuntimeView.instance().getGstTaxCode(invgrp.getTaxCode(), AccountingConstant.TAX_TYPE_CGST).getRatePercentage(), 2), fontTax()));
        cgstp.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cgstp);

        PdfPCell cgstValue = new PdfPCell(new Phrase(invgrp.getInvoiceCgstValue() == null ? "" : AppView.formatDecimal(invgrp.getInvoiceCgstValue(), 2), fontTax()));
        cgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cgstValue);

        PdfPCell sgstp = new PdfPCell(new Phrase(invgrp.getTaxCode().getRatePercentage() == null ? "" : AppView.formatDecimal(UserRuntimeView.instance().getGstTaxCode(invgrp.getTaxCode(), AccountingConstant.TAX_TYPE_SGST).getRatePercentage(), 2), fontTax()));
        sgstp.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(sgstp);

        PdfPCell sgstValue = new PdfPCell(new Phrase(invgrp.getInvoiceSgstValue() == null ? "" : AppView.formatDecimal(invgrp.getInvoiceSgstValue(), 2), fontTax()));
        sgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(sgstValue);
      }
      PdfPCell gstValue = new PdfPCell();
      if (taxableInvoice == 1) {
        gstValue = new PdfPCell(new Phrase(invgrp.getInvoiceIgstValue() == null ? "" : AppView.formatDecimal(invgrp.getInvoiceIgstValue(), 2), fontTax()));
      }
      gstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(gstValue);

      PdfPCell netValue = new PdfPCell(new Phrase(AppView.formatDecimal(invgrp.getInvoiceNetAmount(), 2), fontTax()));
      netValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(netValue);

    }
    return table;
  }

  @Override
  public PdfPTable setNote(String note) {
    PdfPTable table = new PdfPTable(1);
    Paragraph para = new Paragraph();
    para.add(new Phrase("Note: ", fontTax()));
    para.add(new Phrase(note != null ? note : "", fontTax()));
    PdfPCell remarksCell = new PdfPCell(para);
    remarksCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    remarksCell.setFixedHeight(20);
    table.addCell(remarksCell).setBorder(0);

    return table;
  }

  @Override
  public PdfPTable setInterStateServiceTaxation(SalesInvoice salesInvoice) {
    PdfPTable table = new PdfPTable(3);
    table.getDefaultCell().setBorder(0);
    PdfPCell gstLabel = new PdfPCell(new Phrase("Serv. GST %", fontTax()));
    PdfPCell taxableLabel = new PdfPCell(new Phrase("Serv. Taxable", fontTax()));
    PdfPCell taxValueLabel = new PdfPCell(new Phrase("Tax value", fontTax()));
    gstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    taxableLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    taxValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    gstLabel.setBorderWidthLeft(0);
    taxableLabel.setBorderWidthLeft(0);
    taxValueLabel.setBorderWidthRight(0);
    gstLabel.setBorderWidthTop(0.5f);
    taxableLabel.setBorderWidthTop(0.5f);
    taxValueLabel.setBorderWidthTop(0.5f);
    table.addCell(gstLabel);
    table.addCell(taxableLabel);
    table.addCell(taxValueLabel);

    // Add Tax Values
    if (salesInvoice.getFreightRate() != null) {
      PdfPCell gstValue = new PdfPCell(new Phrase(String.valueOf(salesInvoice.getServiceTaxCodeId().getRatePercentage()), fontTax()));
      PdfPCell taxableValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getFreightRate(), 2), fontTax()));
      PdfPCell tax = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getServiceValueIgst(), 2), fontTax()));
      gstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      taxableValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      tax.setHorizontalAlignment(Element.ALIGN_RIGHT);
      gstValue.setBorderWidthLeft(0);
      taxableValue.setBorderWidthLeft(0);
      gstValue.setBorderWidthTop(0);
      taxableValue.setBorderWidthTop(0);
      tax.setBorderWidthTop(0);
      table.addCell(gstValue);
      table.addCell(taxableValue);
      table.addCell(tax);
    }
    if (salesInvoice.getService2Rate() != null) {
      PdfPCell gstValue = new PdfPCell(new Phrase(String.valueOf(salesInvoice.getService2TaxCodeId().getRatePercentage()), fontTax()));
      PdfPCell taxableValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getService2Rate(), 2), fontTax()));
      PdfPCell tax = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getService2Igst(), 2), fontTax()));
      gstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      taxableValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      tax.setHorizontalAlignment(Element.ALIGN_RIGHT);
      gstValue.setBorderWidthLeft(0);
      taxableValue.setBorderWidthLeft(0);
      gstValue.setBorderWidthTop(0);
      taxableValue.setBorderWidthTop(0);
      tax.setBorderWidthTop(0);
      table.addCell(gstValue);
      table.addCell(taxableValue);
      table.addCell(tax);
    }
    return table;
  }

  @Override
  public PdfPTable setIntraStateServiceTaxation(SalesInvoice salesInvoice) {
    PdfPTable table = new PdfPTable(7);
    table.getDefaultCell().setBorder(0);

    PdfPCell gstLabel = new PdfPCell(new Phrase("Serv. GST %", fontTax()));
    PdfPCell taxableLabel = new PdfPCell(new Phrase("Serv. Taxable", fontTax()));
    PdfPCell sgstLabel = new PdfPCell(new Phrase("SGST", fontTax()));
    PdfPCell staxLabel = new PdfPCell(new Phrase("Tax", fontTax()));
    PdfPCell cgstLabel = new PdfPCell(new Phrase("CGST", fontTax()));
    PdfPCell ctaxLabel = new PdfPCell(new Phrase("Tax", fontTax()));
    PdfPCell taxValueLabel = new PdfPCell(new Phrase("Tax value", fontTax()));
//    PdfPCell kfLabel = new PdfPCell(new Phrase("KFCess", fontTax()));

    gstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    taxableLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    sgstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    staxLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    cgstLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    ctaxLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
    taxValueLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
//    kfLabel.setHorizontalAlignment(Element.ALIGN_CENTER);

    gstLabel.setBorderWidthLeft(0);
    taxableLabel.setBorderWidthLeft(0);
    sgstLabel.setBorderWidthLeft(0);
    staxLabel.setBorderWidthLeft(0);
    cgstLabel.setBorderWidthLeft(0);
    ctaxLabel.setBorderWidthLeft(0);
    taxValueLabel.setBorderWidthLeft(0);
    taxValueLabel.setBorderWidthRight(0);
//    kfLabel.setBorderWidthRight(0);

    gstLabel.setBorderWidthTop(0.5f);
    taxableLabel.setBorderWidthTop(0.5f);
    sgstLabel.setBorderWidthTop(0.5f);
    staxLabel.setBorderWidthTop(0.5f);
    cgstLabel.setBorderWidthTop(0.5f);
    ctaxLabel.setBorderWidthTop(0.5f);
    taxValueLabel.setBorderWidthTop(0.5f);
//    kfLabel.setBorderWidthTop(0);

    table.addCell(gstLabel);
    table.addCell(taxableLabel);
    table.addCell(sgstLabel);
    table.addCell(staxLabel);
    table.addCell(cgstLabel);
    table.addCell(ctaxLabel);
    table.addCell(taxValueLabel);
//    table.addCell(kfLabel);

    // Add Tax Values
    if (salesInvoice.getFreightRate() != null) {
      PdfPCell gstValue = new PdfPCell(new Phrase(String.valueOf(salesInvoice.getServiceTaxCodeId().getRatePercentage()), fontTax()));
      PdfPCell taxableValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getFreightRate(), 2), fontTax()));
      PdfPCell sgstValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getServiceTaxCodeId().getRatePercentage() / 2, 2), fontTax()));
      PdfPCell staxValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getServiceValueSgst(), 2), fontTax()));
      PdfPCell cgstValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getServiceTaxCodeId().getRatePercentage() / 2, 2), fontTax()));
      PdfPCell ctaxValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getServiceValueCgst(), 2), fontTax()));
      PdfPCell tax = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getServiceValueIgst(), 2), fontTax()));
      gstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      taxableValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      sgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      staxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      ctaxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      tax.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(gstValue);
      table.addCell(taxableValue);
      table.addCell(sgstValue);
      table.addCell(staxValue);
      table.addCell(cgstValue);
      table.addCell(ctaxValue);
      table.addCell(tax);
    }
    if (salesInvoice.getService2Rate() != null) {
      PdfPCell gstValue = new PdfPCell(new Phrase(String.valueOf(salesInvoice.getService2TaxCodeId().getRatePercentage()), fontTax()));
      PdfPCell taxableValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getService2Rate(), 2), fontTax()));
      PdfPCell sgstValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getService2TaxCodeId().getRatePercentage() / 2, 2), fontTax()));
      PdfPCell staxValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getService2Sgst(), 2), fontTax()));
      PdfPCell cgstValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getService2TaxCodeId().getRatePercentage() / 2, 2), fontTax()));
      PdfPCell ctaxValue = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getService2Cgst(), 2), fontTax()));
      PdfPCell tax = new PdfPCell(new Phrase(AppView.formatDecimal(salesInvoice.getService2Igst(), 2), fontTax()));
      gstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      taxableValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      sgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      staxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cgstValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      ctaxValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
      tax.setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(gstValue);
      table.addCell(taxableValue);
      table.addCell(sgstValue);
      table.addCell(staxValue);
      table.addCell(cgstValue);
      table.addCell(ctaxValue);
      table.addCell(tax);
    }
    return table;
  }
}
