/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.print;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import static spica.sys.SystemRuntimeConfig.SDF_DD_MM_YYYY_HH_MM;
import wawo.app.config.AppConfig;

/**
 *
 * @author godson
 */
public class PageFooterEvent extends PdfPageEventHelper {

  private PdfTemplate total;
  private Image image;
  private float pageWidth;

  public PageFooterEvent(float pageWidth) {
    this.pageWidth = pageWidth;
  }

  public void onEndPage(PdfWriter writer, Document document) {
    PdfContentByte cb = writer.getDirectContentUnder();
    cb.setLineWidth(0.3f);
    cb.moveTo(document.right(), document.bottom());
    cb.lineTo(document.left(), document.bottom());
    cb.closePathStroke();
    try {
      addFooter(writer);
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    }
  }

  public void onOpenDocument(PdfWriter writer, Document document) {
    total = writer.getDirectContent().createTemplate(30, 16);
    try {
      image = Image.getInstance(total);
      image.setRole(PdfName.ARTIFACT);
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    }
  }

  public void onCloseDocument(PdfWriter writer, Document document) {
    int totalLength = String.valueOf(writer.getPageNumber()).length();
    int totalWidth = totalLength * 5;
    ColumnText.showTextAligned(total, Element.ALIGN_RIGHT, new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)), totalWidth, 6, 0);
  }

  private void addFooter(PdfWriter writer) throws DocumentException {
    PdfPTable footer = new PdfPTable(3);

    // set defaults
    footer.setWidths(new int[]{24, 2, 1});
    footer.setTotalWidth(pageWidth - 37);
    footer.setLockedWidth(true);
    footer.getDefaultCell().setFixedHeight(40);
    footer.getDefaultCell().setBorderWidth(0);

    // add copyright
    footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
    Date date = Calendar.getInstance().getTime();

    String strDate = SDF_DD_MM_YYYY_HH_MM.format(date);
    footer.addCell(new Phrase(strDate, new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));

    // add current page count
    footer.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)));

    // add placeholder for total page count
    PdfPCell totalPageCount = new PdfPCell(image);
    totalPageCount.setBorderWidth(0);
    footer.addCell(totalPageCount);

    // write page
    PdfContentByte canvas = writer.getDirectContent();
    canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
    footer.writeSelectedRows(0, -1, 34, 20, canvas);
    canvas.endMarkedContentSequence();

  }

}
