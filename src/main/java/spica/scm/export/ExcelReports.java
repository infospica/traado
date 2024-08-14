/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.export;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spica.reports.model.FilterParameters;
import spica.reports.model.ReceiptReport;
import spica.scm.domain.Company;
import spica.scm.domain.Territory;
import spica.sys.SystemRuntimeConfig;
import wawo.app.faces.MainView;

/**
 *
 * @author Arun
 */
public class ExcelReports {

  public static void collectionReceipt(MainView main, List<ReceiptReport> list, Territory territory, FilterParameters param, Company company) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      String fileName = territory == null ? "Collection_Receipt" : "Area_wise_collection_report";
      String title = territory == null ? "Collection Receipt" : "Report Name : Area Wise Collection report";
      title += " as on " + SystemRuntimeConfig.SDF_DD_MM_YYYY.format(new Date());

      XSSFSheet sheet = workbook.createSheet(fileName);
      sheet.setDefaultColumnWidth(15);

//    SimpleDateFormat colDate = new SimpleDateFormat("MMM dd,yy");
      XSSFRow row;

//    XSSFFont font = workbook.createFont();
//    font.setFontHeightInPoints((short) 10);
//    font.setFontName("Arial");
//    font.setColor(IndexedColors.BLACK.getIndex());
//    font.setBold(true);
//    font.setItalic(false);
      //  XSSFFont fontHead =  getFont(10, true);
      //  CellStyle styletotal = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
      // Create cell style 
      CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);

      //  CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      //   CellStyle styleNormalCenter = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);
      int cellId = 0;
      int rowId = 0;
      String[] arr = new String[]{"Date", "Ledger Name", "Net Amount", "Cheque Amount", "Cheque No", "Cheque Date", "Excess Received", "Cheque Bank", "Settled Bill Detail"};
      int size = 0;
      // Page Header
      rowId = ExcelUtil.createDocHeader(main, sheet, rowId, title, arr.length - 1, company, null, null);

      Cell cell;
      if (territory != null) {
        rowId = ExcelUtil.createTerritoryHeader(sheet, rowId, 2, territory.getTerritoryName());
      }
      if (param.getFromDate() != null && param.getToDate() != null) {
        rowId = ExcelUtil.createDateHeader(sheet, rowId, 1,
                (param.getFromDate() != null ? SystemRuntimeConfig.SDF_DD_MM_YYYY.format(param.getFromDate()) : null),
                (param.getToDate()) != null ? SystemRuntimeConfig.SDF_DD_MM_YYYY.format(param.getToDate()) : null);
      }
      row = sheet.createRow(rowId++);
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        cell.setCellStyle(ExcelUtil.styleBold(workbook, HorizontalAlignment.LEFT));
        sheet.autoSizeColumn(size);
        size++;
      }

      for (ReceiptReport receipt : list) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);

        cell = row.createCell(cellId++);
        if (receipt.getEntryDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(receipt.getEntryDate()));
        }
        cell.setCellStyle(styleNormal);

        cell = row.createCell(cellId++);
        if (receipt.getCustomer() != null) {
          cell.setCellValue(receipt.getCustomer());
        }
        cell.setCellStyle(styleNormal);

        cell = row.createCell(cellId++);
        if (receipt.getAmount() != null) {
          cell.setCellValue(receipt.getAmount());
        }
        cell.setCellStyle(styleNormal);

        cell = row.createCell(cellId++);
        if (receipt.getChequeNo() != null) {
          cell.setCellValue(receipt.getChequeNo());
        }
        cell.setCellStyle(styleNormal);

        cell = row.createCell(cellId++);
        if (receipt.getChequeDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(receipt.getChequeDate()));
        }
        cell.setCellStyle(styleNormal);

        cell = row.createCell(cellId++);
        cell.setCellValue("");
        cell.setCellStyle(styleNormal);

        cell = row.createCell(cellId++);
        if (receipt.getBankName() != null) {
          cell.setCellValue(receipt.getBankName());
        }
        cell.setCellStyle(styleNormal);

        cell = row.createCell(cellId++);
        if (receipt.getNarration() != null) {
          cell.setCellValue(receipt.getNarration());
        }
        cell.setCellStyle(styleNormal);
      }
      ExcelUtil.write(fileName + ".xlsx", workbook);
    }
  }

}
