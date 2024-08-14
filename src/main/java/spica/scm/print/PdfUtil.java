/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sujesh
 */
public class PdfUtil {

  private final String REGULAR = "/fonts/Roboto-Light.ttf";
  private final String BOLD = "/fonts/Roboto-Bold.ttf";

  float sl = 3, mfg = 4, hsn = 6, product = 18, pack = 5, batch = 8f, exp = 4.5f, qty = 4, mrp = 5, pts = 5, ptr = 5, disc = 3, rate = 5,
          discvalue = 5, ta = 7, gstp = 4, gsta = 6, total = 7, tax = 4, sgst = 5, cgst = 5, igst = 5, goodsValue = 5, status = 6,
          refNo = 9, refDate = 6;
  public float pageWidth = 555;
  public float landScapeWidth = 802;
  public String proformaLabel = " PRO FORMA GST INVOICE";
  public String invoiceLabel = " GST INVOICE ";
  public String challan = " CHALLAN ";
  public String serviceInvoiceLabel = " SALES SERVICES INVOICE ";
  public String debitNoteLabel = " CREDIT DEBIT NOTE ";
  public String sampleNote = " PHYSICIAN'S SAMPLE MEANT FOR \n FREE DISTRIBUTION TO DOCTOR \n NOT FOR SALE AND NO COMMERCIAL VALUE ";
  public final float cellHeight = 15;

  private static BaseFont baseFont;

  public BaseFont baseFont() {
    BaseFont base = null;
    try {
      base = BaseFont.createFont(getPath(REGULAR), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
      baseFont = base;
    } catch (DocumentException | IOException e) {
    }
    return base;
  }

  public BaseFont baseBold() {
    BaseFont baseBold = null;
    try {
      baseBold = BaseFont.createFont(getPath(BOLD), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    } catch (DocumentException | IOException e) {
    }
    return baseBold;
  }

  public Font fontBill() {
    return new Font(baseBold(), 8.5f, Font.NORMAL);
  }

  public Font fontInvoiceNo() {
    return new Font(baseBold(), 9.5f, Font.NORMAL);
  }

  public Font fontNetAmount() {
    return new Font(baseBold(), 9.5f, Font.NORMAL);
  }

  public Font fontCalculation() {
    return new Font(baseFont(), 8.5f, Font.BOLD);
  }

  public Font fontTax() {
    return new Font(baseFont(), 7.5f, Font.BOLD);
  }

  public Font fontDisclaimer() {
    return new Font(baseFont(), 5.5f, Font.NORMAL);
  }

  public Font fontTitle() {
    return new Font(baseBold(), 11, Font.BOLD);
  }

  public Font fontTitleForLong() {
    return new Font(baseBold(), 10, Font.BOLD);
  }

  public Font fontHeader() {
    return new Font(baseFont(), 8f, Font.BOLD);
  }

  public Font fontSubHeader() {
    return new Font(baseFont(), 7.5f, Font.BOLD);
  }

  public static Font fontProduct = new Font(baseFont, 7, Font.BOLD);

  public Font fontProduct() {
    return new Font(baseFont(), 7, Font.BOLD);
  }
  public static Font fontProductHead = new Font(baseFont, 7, Font.BOLD);

  public Font fontProductHead() {
    return new Font(baseFont(), 7, Font.BOLD);
  }

  public float[] portraitColumnsWithMfg() {
    return new float[]{sl, mfg, hsn + 1.5f, product, pack + 1.5f, batch, exp + 0.5f, mrp, qty, rate, disc + 2, discvalue + 2, ptr, ta + 1, gstp, gsta + 1, total + 2};
  }

  public float[] portraitColumnsWithOutMfg() {
    return new float[]{sl, hsn, product, pack, batch, exp, mrp, qty - 1, rate, disc + 1, discvalue, ptr, ta, gstp, gsta, total};
  }

  public float[] landscapeColumnsWithMfg() {
    return new float[]{sl, mfg - 1, hsn, product + 1, pack, batch - 1, exp, mrp, qty - 1, rate, disc + 1, discvalue, pts, ptr, ta, gstp, gsta, total};
  }

  public float[] landscapeColumnsWithOutMfg() {
    return new float[]{sl, hsn, product, pack, batch, exp, mrp, qty - 1, rate, disc, discvalue, pts, ptr, ta, gstp, gsta, total};
  }

  public float[] salesReturnPortraitColumnsWithMfg() {
    return new float[]{sl, mfg, hsn + 1.5f, product - 2, pack + 1.5f, batch, exp + 1, mrp, qty + 3, rate, discvalue + 2, gstp, ta + 1, total + 2};
  }

  public float[] salesReturnPortraitColumnsWithOutMfg() {
    return new float[]{sl, hsn, product - 2, pack, batch - 1, exp, mrp, qty, rate, discvalue, gstp, ta, total};
  }

  public float[] salesReturnLandscapeColumnsWithMfg() {
    return new float[]{sl, mfg - 1, hsn, product - 1, refNo, refDate, pack, batch - 1.5f, exp, mrp, qty, rate, discvalue, disc, discvalue, disc, pts, gstp, ta, total};
  }

  public float[] salesReturnLandscapeColumnsWithOutMfg() {
    return new float[]{sl, hsn, product - 1, refNo, refDate, pack, batch - 1, exp, mrp, qty, rate, discvalue, disc, discvalue, disc, pts, gstp, ta, total};
  }

  public float[] salesServicesInvoiceColumn() {
    return new float[]{3, 18, 8, 8, 8, 8, 8, 8, 8, 9, 10};
  }

  public float[] debitCreditNoteIntrastateColumn() {
    return new float[]{3, 17, 8, 15, 11, 11, 7, 7, 7, 9};
  }

  public float[] debitCreditNoteInterstateColumn() {
    return new float[]{3, 18, 9, 20, 11, 12, 9, 9, 10};
  }

  public float[] portraitColumnsWithMfgTypeI() {
    return new float[]{sl, mfg, hsn + 1.5f, product, pack + 1.5f, batch + 1, exp + 1, mrp, qty, qty, rate, disc + 2, discvalue + 2, ptr, gstp, ta + 1};
  }

  public float[] portraitColumnsWithOutMfgTypeI() {
    return new float[]{sl, hsn, product, pack, batch, exp, mrp, qty - 1, qty - 1, rate, disc + 1, discvalue, ptr, gstp, ta};
  }

  public float[] landscapeColumnsWithMfgTypeI() {
    return new float[]{sl, mfg - 1, hsn, product + 1, pack, batch - 1, exp, mrp, qty - 1, qty - 1, rate, disc + 1, discvalue, pts, ptr, gstp, ta};
  }

  public float[] landscapeColumnsWithOutMfgTypeI() {
    return new float[]{sl, hsn, product, pack, batch, exp, mrp, qty - 1, qty - 1, rate, disc, discvalue, pts, ptr, gstp, ta};
  }

  public float[] portraitColumnsWithMfgTypeII() {
    return new float[]{sl, mfg, hsn + 1.5f, product, pack + 1.5f, batch + 1, exp + 0.5f, mrp, qty, rate, discvalue + 2, disc + 2, discvalue + 2, ptr, gstp, ta + 1};
  }

  public float[] portraitColumnsWithOutMfgTypeII() {
    return new float[]{sl, hsn, product, pack, batch, exp, mrp, qty - 1, rate, discvalue, disc + 1, discvalue, ptr, gstp, ta};
  }

  public float[] landscapeColumnsWithMfgTypeII() {
    return new float[]{sl, mfg - 1, hsn, product + 1, pack, batch - 1, exp, mrp, qty - 1, rate, discvalue, disc + 1, discvalue, pts, ptr, gstp, ta};
  }

  public float[] landscapeColumnsWithOutMfgTypeII() {
    return new float[]{sl, hsn, product, pack, batch, exp, mrp, qty - 1, rate, discvalue, disc, discvalue, pts, ptr, gstp, ta};
  }

  public float[] sampleInvoiceColumn() {
    return new float[]{sl, product, hsn, pack, batch, mfg, exp, qty};
  }

  public float[] purchaseReturnPortraitColumnsWithOutMfg(boolean intrastate) {
    if (intrastate) {
      return new float[]{sl, hsn, product - 1, batch - 2, exp + 2, tax, qty, discvalue - 1, disc, rate, sgst, cgst, mrp + 1, goodsValue + 1, total};
    } else {
      return new float[]{sl, hsn, product - 1, batch - 2, exp + 2, tax, qty, discvalue - 1, disc, rate, igst, mrp + 1, goodsValue + 1, total};
    }
  }

  public float[] purchaseReturnLandScapeColumnsWithOutMfg(boolean intrastate) {
    if (intrastate) {
      return new float[]{sl, hsn, product, batch - 2, exp + 1, tax, qty, discvalue, disc + 1, rate, sgst, cgst, mrp, goodsValue, total};
    } else {
      return new float[]{sl, hsn, product, batch - 2, exp + 1, tax, qty, discvalue, disc + 1, rate, igst, mrp, goodsValue, total};
    }
  }

  public String getPath(String file) {
    String filePath = null;
    URL url = getClass().getClassLoader().getResource(file);
    try {
      Path path = Paths.get(url.toURI());
      filePath = path.toAbsolutePath().toString();
    } catch (URISyntaxException ex) {
      Logger.getLogger(PdfUtil.class.getName()).log(Level.SEVERE, null, ex);
    }
    return filePath;
  }

  public String decimalCheck(String s) {
    if (s != null) {
      String arr[] = s.split("\\.");
      if (arr[1].charAt(0) == '0') {
        return arr[0] + arr[1].replaceAll("[^A-Za-z]+", "");
      }
    }
    return s;
  }

  public String getLogo(String image) {
    image += "/logo/";
    return getPath(image);
  }

  public float[] getCellWidth(Float keys[]) {
    float[] array = new float[keys.length];
    int index = 0;
    for (Float element : keys) {
      array[index++] = element.floatValue();
    }
    return array;
  }

  public String limitStringSize(String text, int length, boolean append) {
    StringBuilder str = new StringBuilder(text);
    if (str.length() > length) {
      str.setLength(length);
      if (append) {
        str.append("...");
      }
    }
    return str.toString();
  }
}
