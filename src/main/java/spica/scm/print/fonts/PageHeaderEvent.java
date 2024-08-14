/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print.fonts;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 *
 * @author godson
 */
public class PageHeaderEvent extends PdfPageEventHelper {

  String companyName;
  String billNo;
  String customerName;
  String date;
  Font ffont = new Font(Font.FontFamily.UNDEFINED, 7, Font.ITALIC);

  public void onStartPage(PdfWriter writer, Document document) {
    PdfContentByte cb = writer.getDirectContentUnder();
    Phrase header1 = new Phrase(companyName + " / Bill To: " + customerName, ffont);
    Phrase header2 = new Phrase(billNo + " / Date: " + date, ffont);
    ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, header1, document.left(), document.top() + 10, 0);
    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, header2, document.right(), document.top() + 10, 0);

    cb.setLineWidth(0.5f);
    cb.moveTo(document.right(), document.top());
    cb.lineTo(document.left(), document.top());
    cb.closePathStroke();
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public PageHeaderEvent(String companyName, String billNo, String customerName, String date) {
    this.companyName = companyName;
    this.billNo = billNo;
    this.customerName = customerName;
    this.date = date;
  }

}
