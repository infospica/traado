/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.export;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.Customer;
import spica.scm.domain.District;
import spica.scm.domain.Product;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.Territory;
import static spica.scm.print.PdfUtil.fontProduct;
import static spica.scm.print.PdfUtil.fontProductHead;
import spica.scm.service.CompanyAddressService;
import wawo.app.faces.Jsf;
import wawo.app.faces.JsfIo;
import wawo.app.faces.MainView;
import static wawo.entity.core.AppIo.getMimetypesFileTypeMap;
import wawo.entity.util.IoUtil;

/**
 *
 * @author godson
 */
public class ExcelUtil {

  public static final String FILL_BLUE = "BLUE";
  public static final String FILL_ORANGE = "ORANGE";
  public static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

  public static int createHeader(XSSFWorkbook workbook, XSSFSheet sheet, Company company, AccountGroup ag, Account account, Customer customer) {
    Cell cell;
    int rowId = 0;
    XSSFRow row = sheet.createRow(rowId);
    cell = row.createCell(0);
    if (company != null) {
      cell.setCellValue(" Comapany Name -  " + company.getCompanyName());
      cell.setCellStyle(styleBold(workbook, HorizontalAlignment.LEFT, 12));
      sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
      rowId++;
    }
    if (ag != null) {
      row = sheet.createRow(rowId);
      cell = row.createCell(0);
      cell.setCellValue(" Account Group -  " + ag.getGroupName());
      cell.setCellStyle(styleBold(workbook, HorizontalAlignment.LEFT));
      sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 5));
      rowId++;
    }
    if (account != null) {
      row = sheet.createRow(rowId);
      cell = row.createCell(0);
      cell.setCellValue(" Account  -  " + account.getAccountTitle());
      cell.setCellStyle(styleBold(workbook, HorizontalAlignment.LEFT));
      sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 5));
      rowId++;
    }
    if (customer != null) {
      row = sheet.createRow(rowId);
      cell = row.createCell(0);
      cell.setCellValue(" Customer  -  " + customer.getCustomerName());
      cell.setCellStyle(styleBold(workbook, HorizontalAlignment.LEFT));
      sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 5));
      rowId++;
    }
    return rowId;
  }

  public static int createDocHeader(MainView main, XSSFSheet sheet, int rowId, String title, int size, Company company, AccountGroup ag, Customer customer) {
    rowId = createHeader(sheet, rowId, size, title, styleHead(sheet.getWorkbook()));
    if (company != null) {
      rowId = createCompanyHeader(main, sheet, rowId, size, company);
    }
    if (customer != null) {
      rowId = createCustomerHeader(sheet, rowId, size, customer.getCustomerName());
    }
    if (ag != null) {
      rowId = createAccountGroupHeader(sheet, rowId, size, ag.getGroupName());
    }
    return rowId;
  }

  public static int createDocHeaderForAgeWiseOutstanding(MainView main, XSSFSheet sheet, int rowId, String title, int size, FilterParameters filterParameters, SalesAgent salesAgent, Territory territory, District district, List<Product> productList) {
    rowId = createHeader(sheet, rowId, size, title, styleHead(sheet.getWorkbook()));
    if (filterParameters != null) {
      if (filterParameters.getAccountGroup() != null) {
        rowId = createAccountGroupHeader(sheet, rowId, size, filterParameters.getAccountGroup().getGroupName());
      }
      if (filterParameters.getAccount() != null) {
        rowId = createAccountHeader(sheet, rowId, size, filterParameters.getAccount().getAccountTitle());
      }
      if (salesAgent != null) {
        rowId = createSalesAgentHeader(sheet, rowId, size, salesAgent.getName());
      }
      if (filterParameters.getCustomer() != null) {
        rowId = createCustomerHeader(sheet, rowId, size, filterParameters.getCustomer().getCustomerName());
      }
      if (territory != null) {
        rowId = createTerritoryHeader(sheet, rowId, size, territory.getTerritoryName());
      }
      if (district != null) {
        rowId = createDistrictHeader(sheet, rowId, size, district.getDistrictName());
      }
      if (productList != null) {
        rowId = createProductHeader(sheet, rowId, size, productList);
      }
    }
    return rowId;
  }

  private static int createCustomerHeader(XSSFSheet sheet, int rowId, int size, String customerName) {
    return createHeader(sheet, rowId, size, " Customer -  " + customerName);
  }

  public static int createAccountGroupHeader(XSSFSheet sheet, int rowId, int size, String groupName) {
    return createHeader(sheet, rowId, size, " Account Group -  " + groupName);
  }

  public static int createSalesAgentHeader(XSSFSheet sheet, int rowId, int size, String salesAgentName) {
    return createHeader(sheet, rowId, size, " Sales Agent -  " + salesAgentName);
  }

  public static int createAccountHeader(XSSFSheet sheet, int rowId, int size, String accountName) {
    return createHeader(sheet, rowId, size, " Account -  " + accountName);
  }

  public static int createTerritoryHeader(XSSFSheet sheet, int rowId, int size, String territoryName) {
    return createHeader(sheet, rowId, size, " Territory -  " + territoryName);
  }

  public static int createDistrictHeader(XSSFSheet sheet, int rowId, int size, String districtName) {
    return createHeader(sheet, rowId, size, " District -  " + districtName);
  }

  public static int createProductHeader(XSSFSheet sheet, int rowId, int size, List<Product> productList) {
    String products = "";
    if (productList != null) {
      int i = 1;
      for (Product p : productList) {
        if (i == 1) {
          products += p.getProductName();
        } else {
          products += ", " + p.getProductName();
        }
        i++;
      }
    }
    return createHeader(sheet, rowId, size, " Products -  " + products);
  }

  private static int createCompanyHeader(MainView main, XSSFSheet sheet, int rowId, int size, Company company) {
    CompanyAddress address = CompanyAddressService.selectCompanyRegisteredAddress(main, company);
    String header = company.getCompanyName();
    header += "\n" + address.getAddress();
    header += "\n" + address.getPhone1() + ", " + address.getEmail();
    return createHeader(sheet, rowId, size, header, styleHead(sheet.getWorkbook(), 12), 4);
  }

  public static int createDateHeader(XSSFSheet sheet, int rowId, int size, String fromDate, String toDate) {
    String header = (fromDate != null ? fromDate : "") + (toDate != null ? " - " + toDate : "");
    return createHeader(sheet, rowId, size, header, styleHead(sheet.getWorkbook(), 10), 1);
  }

  private static int createHeader(XSSFSheet sheet, int rowId, int mergeSize, String title) {
    return createHeader(sheet, rowId, mergeSize, title, null, 0);
  }

  private static int createHeader(XSSFSheet sheet, int rowId, int mergeSize, String title, CellStyle style) {
    return createHeader(sheet, rowId, mergeSize, title, style, 2);
  }

  private static int createHeader(XSSFSheet sheet, int rowId, int mergeSize, String title, CellStyle style, int height) {
    XSSFRow row = sheet.createRow(rowId);
    Cell cell = row.createCell(0);
    cell.setCellValue(title);
    if (style == null) {
      style = sheet.getWorkbook().createCellStyle();
      style.setFont(font(10, true, sheet.getWorkbook()));
      style.setAlignment(HorizontalAlignment.LEFT);
      style.setWrapText(true);
      style.setVerticalAlignment(VerticalAlignment.CENTER);
    }
    cell.setCellStyle(style);
    sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, mergeSize));
    if (height != 0) {
      row.setHeight((short) (row.getHeight() * height));
    }
    return rowId + 1;
  }

  ///////////////////////////// header common method end can be move to util //////////////////////////////
  /**
   * To close the document and prevent from throwing exception on clonsole.
   *
   * @param document
   * @throws IOException
   */
  /**
   * To write to output file.
   * <pre>
   * {@code
   *
   * String fileName = "/"+ companySettings.getCompanyId().getId()+"/saleinvoice/"+getSalesInvoice().getInvoiceNo() + ".pdf";
   * PdfWriter pdfWriter = PdfWriter.getInstance(document, ExcelUtil.outFile(fileName));
   * //document.close();
   * }
   * </pre>
   *
   * @param fileName
   * @return
   * @throws IOException
   */
  public static OutputStream outFile(String fileName) throws IOException {
    String path = JsfIo.fileUploadPrivateFolder + fileName;
    IoUtil.formatToUnixAndMakeMissingFolders(path);
    return new FileOutputStream(new File(path));
  }

  /**
   * Return output stream and force download.
   * <pre>
   * {@code
   * PdfWriter pdfWriter = PdfWriter.getInstance(document, ExcelUtil.outResponse("test.pdf"));
   * }
   * </pre>
   *
   * @param fileName
   * @return
   * @throws IOException
   */
  public static OutputStream outResponse(String fileName) throws IOException {
    return getResponse(fileName, "attachment", Jsf.getResponse());
  }

  /**
   * Return output stream and try to preview the file in the browser.
   * <pre>
   * {@code
   * PdfWriter pdfWriter = PdfWriter.getInstance(document, ExcelUtil.outResponseInline("test.pdf"));
   * }
   * </pre>
   *
   * @param fileName
   * @return
   * @throws IOException
   */
  public static OutputStream outResponseInline(String fileName) throws IOException {
    return getResponse(fileName, "inline", Jsf.getResponse());
  }

  public static OutputStream getResponse(String fileName, String downloadType, HttpServletResponse response) throws IOException {
    String name = IoUtil.getFileName(fileName);
    if (downloadType == null) {
      downloadType = "attachment";
    }
    response.setHeader("Content-Type", getMimetypesFileTypeMap().getContentType(name));
    response.setHeader("Content-Disposition", downloadType + "; filename=\"" + name + "\"");
    return response.getOutputStream();
  }

  public static void downloadInline(String file, HttpServletResponse response) throws IOException {
    download(file, "inline", response);
  }

  public static void downloadAttchment(String file, HttpServletResponse response) throws IOException {
    download(file, "attachment", response);
  }

  private static void download(String file, String disposition, HttpServletResponse response) throws IOException {
    int DEFAULT_BUFFER_SIZE = 4024;
    BufferedInputStream input = null;
    BufferedOutputStream output = null;
    try {
      // Open file.
      input = new BufferedInputStream(new FileInputStream(new File(file)), DEFAULT_BUFFER_SIZE);
      // Init servlet response.
      response.reset();
      response.setHeader("Content-Type", getMimetypesFileTypeMap().getContentType(file));
      // response.setHeader("Content-Length", String.valueOf(file.length()));
      response.setHeader("Content-Disposition", disposition + "; filename=\"" + IoUtil.getFileName(file) + "\"");
      output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
      // Write file contents to response.
      byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
      int length;
      while ((length = input.read(buffer)) > 0) {
        output.write(buffer, 0, length);
      }
      // Finalize task.
      output.flush();
    } finally {
      // Gently close streams.
      IoUtil.close(output);
      IoUtil.close(input);
    }
  }

  /**
   * Download excel file.
   * <pre>
   * {@code
   * ExcelUtil.write("test.xlsx", workbook);
   * }
   * </pre>
   *
   * @param fileName
   * @param workbook
   * @throws IOException
   */
  public static void write(String fileName, XSSFWorkbook workbook) throws IOException {
    workbook.write(outResponse(fileName));
    workbook.close();
    FacesContext.getCurrentInstance().responseComplete();
  }

  /**
   * Write excel to output stream.
   * <pre>
   * {@code
   * String fileName = "/"+ companySettings.getCompanyId().getId()+"/saleinvoice/"+getSalesInvoice().getInvoiceNo() + ".pdf";
   * ExcelUtil.write("test.xlsx", workbook, ExcelUtil.outFile(fileName));
   * }
   * </pre>
   *
   * @param fileName
   * @param workbook
   * @throws IOException
   */
  public static void write(String fileName, XSSFWorkbook workbook, OutputStream out) throws IOException {
    workbook.write(out);
    workbook.close();
  }

  public static XSSFCellStyle setFillColor(String color, HorizontalAlignment alignment, XSSFWorkbook workbook) {
    XSSFCellStyle styleRowBlue = workbook.createCellStyle();
    styleRowBlue.setFont(font(10, true, workbook));
    if (color.equals(FILL_BLUE)) {
      styleRowBlue.setFillForegroundColor(new XSSFColor(new Color(51, 153, 255)));
    }
    if (color.equals(FILL_ORANGE)) {
      styleRowBlue.setFillForegroundColor(new XSSFColor(new Color(255, 204, 153)));
    }
    styleRowBlue.setVerticalAlignment(VerticalAlignment.CENTER);
    styleRowBlue.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    styleRowBlue.setAlignment(alignment);

    return styleRowBlue;
  }

  public static XSSFCellStyle setFillColorwithBorder(String color, HorizontalAlignment alignment, XSSFWorkbook workbook) {
    XSSFCellStyle styleRowBlue = workbook.createCellStyle();
    styleRowBlue.setFont(font(10, true, workbook));
    if (color != null) {
      if (color.equals(FILL_BLUE)) {
        styleRowBlue.setFillForegroundColor(new XSSFColor(new Color(51, 153, 255)));
      }
      if (color.equals(FILL_ORANGE)) {
        styleRowBlue.setFillForegroundColor(new XSSFColor(new Color(255, 204, 153)));
      }
    }
    styleRowBlue.setVerticalAlignment(VerticalAlignment.CENTER);
    styleRowBlue.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    styleRowBlue.setAlignment(alignment);
    styleRowBlue.setBorderBottom(BorderStyle.THIN);
    styleRowBlue.setBorderTop(BorderStyle.THIN);
    styleRowBlue.setBorderRight(BorderStyle.THIN);
    styleRowBlue.setBorderLeft(BorderStyle.THIN);
    return styleRowBlue;
  }

  public static CellStyle styleHead(XSSFWorkbook workbook) {
    return styleHead(workbook, 15);
  }

  public static CellStyle styleHead(XSSFWorkbook workbook, int size) {
    XSSFFont font = font(size, true, workbook);
    CellStyle styleHead = workbook.createCellStyle();
    styleHead.setFont(font);
    styleHead.setWrapText(true);
    styleHead.setAlignment(HorizontalAlignment.CENTER);
    styleHead.setVerticalAlignment(VerticalAlignment.CENTER);
    return styleHead;
  }

  public static CellStyle styleBold(XSSFWorkbook workbook, HorizontalAlignment alignment) {
    return style(workbook, alignment, IndexedColors.BLACK, 10, true);
  }

  public static CellStyle styleBold(XSSFWorkbook workbook, HorizontalAlignment alignment, int size) {
    return style(workbook, alignment, IndexedColors.BLACK, size, true);
  }

  public static CellStyle style(XSSFWorkbook workbook, HorizontalAlignment alignment, int size) {
    return style(workbook, alignment, IndexedColors.BLACK, size, false);
  }

  public static CellStyle style(XSSFWorkbook workbook, HorizontalAlignment alignment) {
    return style(workbook, alignment, IndexedColors.BLACK, 10, false);
  }

  public static CellStyle style(XSSFWorkbook workbook, VerticalAlignment alignment) {
    return style(workbook, alignment, IndexedColors.BLACK, 10, false);
  }

  public static CellStyle styleRed(XSSFWorkbook workbook, HorizontalAlignment alignment) {
    return style(workbook, alignment, IndexedColors.RED, 10, false);
  }

  public static CellStyle styleRedBold(XSSFWorkbook workbook, HorizontalAlignment alignment) {
    return style(workbook, alignment, IndexedColors.RED, 10, true);
  }

  private static CellStyle style(XSSFWorkbook workbook, HorizontalAlignment alignment, IndexedColors color, int size, boolean bold) {
    workbook.createCellStyle();
    CellStyle style = workbook.createCellStyle();
    style.setFont(font(size, bold, color.getIndex(), workbook));
    style.setAlignment(alignment);
    return style;
  }

  private static CellStyle style(XSSFWorkbook workbook, VerticalAlignment alignment, IndexedColors color, int size, boolean bold) {
    workbook.createCellStyle();
    CellStyle style = workbook.createCellStyle();
    style.setFont(font(size, bold, color.getIndex(), workbook));
    style.setVerticalAlignment(alignment);
    return style;
  }

  private static XSSFFont font(int size, boolean bold, XSSFWorkbook workbook) {
    return font(size, bold, IndexedColors.BLACK.getIndex(), workbook);
  }

  private static XSSFFont font(int size, boolean bold, short color, XSSFWorkbook workbook) {
    // font = null;, XSSFWorkbook workbook
    XSSFFont font = workbook.createFont();
    font.setFontHeightInPoints((short) size);
    font.setFontName("Arial");
    //  font.setColor(IndexedColors.BLACK.getIndex());
    font.setBold(bold);
    font.setColor(color);
    font.setItalic(false);
    return font;
  }

  public static String outFilePath(String fileName) throws IOException {
    String path = JsfIo.fileUploadPrivateFolder + fileName;
    return path;
  }

  //PDF UTils
  public static void responseComplete(Document document, String filePath) throws IOException {
    document.close();
    if (filePath == null) {
      FacesContext.getCurrentInstance().responseComplete();
    }
  }

  public static Document getPortrait() {
    Rectangle pageSize = new Rectangle(595, 842);
    return new Document(pageSize, 20, 20, 20, 20);
  }

  public static Document getLandscape() {
    Rectangle pageSize = new Rectangle(842, 595);
    return new Document(pageSize, 20, 20, 20, 20);
  }

  public static PdfWriter getPdfWriter(Document document, String fileName, String filePath) throws IOException, DocumentException {
    boolean write = filePath != null;
    PdfWriter pw = PdfWriter.getInstance(document, write ? ExcelUtil.outFile(filePath) : ExcelUtil.outResponseInline(fileName));

    return pw;
  }

  public static PdfPCell addDecimal(PdfPTable table, Double value, Integer align) {
    return addString(table, value == null ? "" : String.format("%.2f", value), align);
  }

  public static PdfPCell addNumber(PdfPTable table, Integer value, Integer align) {
    return addString(table, value == null ? "" : String.valueOf(value), align);
  }

  public static PdfPCell addString(PdfPTable table, String value, Integer align) {
    PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", fontProduct));
    cell.setBorderWidthBottom(0);
    cell.setBorderWidthTop(0);
    cell.setBorderWidthRight(0);
    cell.setBorderWidthLeft(0);
    if (align != null) {
      cell.setHorizontalAlignment(align);
    }
    table.addCell(cell);
    return cell;
  }

  public static PdfPCell addHead(PdfPTable table, String value) {
    PdfPCell cell = new PdfPCell(new Phrase(value, fontProductHead));
    table.addCell(cell);
    return cell;
  }

  public static PdfPTable emptyRows(float[] columns, float width, float expectedHeight, float sumTableHeight, Rectangle pageSize) {

    PdfPTable table = new PdfPTable(columns);
    table.getDefaultCell().setBorder(0);
    table.setTotalWidth(width);
    table.setLockedWidth(true);
    float rHeight = expectedHeight - sumTableHeight;
    while (table.getTotalHeight() <= rHeight) {
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
    table.setSplitLate(true);
    return table;
  }

}
