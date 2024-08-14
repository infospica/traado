/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.export;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spica.reports.model.CustomerLedgerReport;
import static spica.scm.export.ExcelUtil.FILL_BLUE;
import static spica.scm.export.ExcelUtil.FILL_ORANGE;
import static spica.scm.export.ExcelUtil.decimalFormat;
import spica.sys.SystemRuntimeConfig;
import wawo.entity.util.UniqueCheck;

/**
 *
 * @author godson
 */
public class LedgerReportExcel {

  //FIXME dont declare here two processes can override these values, deine an object and pass to methods and get this calculated
  private static boolean accountGroup = false;
  private static Double cuCr = 0.00;
  private static Double cuDr = 0.00;
  private static Double openingBalance = 0.00;
  private static Double scr = 0.00;
  private static Double sdr = 0.00;

  public static void createCustomerLedgerReport(List<CustomerLedgerReport> customerLedgerReportList, String salesAgent) throws IOException {

    String name = "customer_ledger_report";
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      XSSFRow row;
      Cell cell;
      CellStyle styleHead = ExcelUtil.styleHead(workbook);
      UniqueCheck ucust = new UniqueCheck();
      UniqueCheck ucag = new UniqueCheck();
      int rowId = 0;
      int cellId = 0;
      int i = 1;
      int listSize = customerLedgerReportList.size();
      Integer customerRow = null;
      boolean first = true;
      if (!salesAgent.isEmpty()) {
        row = sheet.createRow(rowId);
        row.setHeight((short) 500);
        row.setRowStyle(styleHead);
        sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 8));
        cell = row.createCell(0);
        cell.setCellValue(salesAgent);
        rowId++;
      }
      for (CustomerLedgerReport rep : customerLedgerReportList) {
        if (ucust.exist(rep.getCustomerName())) {
          rowId = addAccountGroup(ucag, rep, sheet, rowId);
          rowId = addLedgerRows(rep, sheet, rowId);
          if (listSize == i) {
            rowId = createTotalFooter("", sdr, scr, sheet, first, rowId);
            cuDr += sdr;
            cuCr += scr;
            sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 8));
            rowId++;
            rowId = createTotalFooter("Ledger Total", cuDr, cuCr, sheet, first, rowId);
          }
        } else {
          accountGroup = false;
          if (customerRow != null) {
            row = sheet.getRow(customerRow);
            cell = row.createCell(6);
            cell.setCellValue(Double.parseDouble(decimalFormat.format(openingBalance)));
            cell.setCellStyle(ExcelUtil.setFillColor(FILL_BLUE, HorizontalAlignment.LEFT, workbook));
            openingBalance = 0.00;
          }

          customerRow = 0;
          ucag.close();

          rowId = createTotalFooter("", sdr, scr, sheet, first, rowId);
          cuDr += sdr;
          cuCr += scr;
          sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 8));
          rowId++;
          rowId = createTotalFooter("Ledger Total", cuDr, cuCr, sheet, first, rowId);
          if (!first) {
            rowId++;
          }
          scr = 0.00;
          sdr = 0.00;
          cuCr = 0.00;
          cuDr = 0.00;
          row = sheet.createRow(rowId);
          row.setRowStyle(ExcelUtil.setFillColor(FILL_BLUE, HorizontalAlignment.LEFT, workbook));
          row.setHeight((short) 500);
          sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 2));
          sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 4, 5));
          cell = row.createCell(0);
          cell.setCellValue(rep.getCustomerName());
          cell.setCellStyle(ExcelUtil.setFillColor(FILL_BLUE, HorizontalAlignment.LEFT, workbook));
          cell = row.createCell(4);
          cell.setCellValue("Opening Balance");
          cell.setCellStyle(ExcelUtil.setFillColor(FILL_BLUE, HorizontalAlignment.LEFT, workbook));
          customerRow = rowId;
          rowId++;
          row = sheet.createRow(rowId++);
          row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          cellId = 0;
          Object[] arr = new Object[]{"Date", "Particulars", "Vch Type", "Vch No", "Debit", "Credit", "Balance", "Avg.PDays", "Narration"};

          for (Object obj : arr) {
            cell = row.createCell(cellId++);
            cell.setCellValue(obj.toString());
            cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.LEFT, workbook));
          }
          rowId = addAccountGroup(ucag, rep, sheet, rowId);
          rowId = addLedgerRows(rep, sheet, rowId);
          first = false;
          if (listSize == i) {
            rowId = createTotalFooter("", sdr, scr, sheet, first, rowId);
            cuDr += sdr;
            cuCr += scr;
            sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 8));
            rowId++;
            rowId = createTotalFooter("Ledger Total", cuDr, cuCr, sheet, first, rowId);
          }
        }
        i++;
      }
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  private static int addAccountGroup(UniqueCheck ucag, CustomerLedgerReport rep, XSSFSheet sheet, int rowId) {

    if (rep.getAccountGroup() != null) {

      if (!ucag.exist(rep.getAccountGroup())) {
        XSSFRow row;
        Cell cell;
        CellStyle styleTitle = ExcelUtil.styleBold(sheet.getWorkbook(), HorizontalAlignment.LEFT);
        if (accountGroup) {
          if (sdr.doubleValue() != scr.doubleValue()) {
            rowId = createTotalFooter("", sdr, scr, sheet, false, rowId);
            cuDr += sdr;
            cuCr += scr;
            sdr = 0.00;
            scr = 0.00;
          }
        }
        row = sheet.createRow(rowId);
        row.setRowStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 8));
        cell = row.createCell(0);
        cell.setCellValue(rep.getAccountGroup() != null ? rep.getAccountGroup() : "");
        rowId++;

        accountGroup = true;

      }
    }
    return rowId;
  }

  private static int addLedgerRows(CustomerLedgerReport rep, XSSFSheet sheet, int rowId) {

    XSSFRow row;
    Cell cell;
    CellStyle styleNormal = ExcelUtil.style(sheet.getWorkbook(), HorizontalAlignment.LEFT);
    Double cr = rep.getCreditAmount() == null ? 0 : rep.getCreditAmount();
    Double dr = rep.getDebitAmount() == null ? 0 : rep.getDebitAmount();
    if (rep.getIsOpening() == 1) {
      openingBalance += (dr - cr);
      if (dr > cr) {
        sdr = dr - cr;
        scr = 0.00;
      } else {
        scr = cr - dr;
        sdr = 0.00;
      }
    } else {
      scr += cr;
      sdr += dr;
    }
    int cellId = 0;
    if (cr.doubleValue() != dr.doubleValue()) {
      row = sheet.createRow(rowId++);
      row.setRowStyle(styleNormal);

      cell = row.createCell(cellId++);
      cell.setCellValue(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(rep.getAccDate()));

      cell = row.createCell(cellId++);
      cell.setCellValue(rep.getTransTitle());

      cell = row.createCell(cellId++);
      cell.setCellValue(rep.getVoucherType());

      cell = row.createCell(cellId++);
      cell.setCellValue(rep.getDocumentNumber());

      cell = row.createCell(cellId++);
      if (rep.getIsOpening() == 1) {
        if (dr > cr) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(dr - cr)));
        } else {
          cell.setCellValue("");
        }
      } else {
        if (dr != 0) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(dr)));
        } else {
          cell.setCellValue("");
        }
      }
      cell = row.createCell(cellId++);
      if (rep.getIsOpening() == 1) {
        if (dr < cr) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(cr - dr)));
        } else {
          cell.setCellValue("");
        }
      } else {
        if (cr != 0) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(cr)));
        } else {
          cell.setCellValue("");
        }
      }

      cell = row.createCell(cellId++);
      if (rep.getIsOpening() == 2) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(sdr - scr)));
      } else {
        cell.setCellValue("");
      }
      long diff = new Date().getTime() - rep.getAccDate().getTime();
      cell = row.createCell(cellId++);
      if (rep.getIsOpening() == 2) {
        cell.setCellValue(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
      } else {
        cell.setCellValue("");
      }
      cell = row.createCell(cellId++);
      cell.setCellValue(rep.getNarration());
    }
    return rowId;
  }

  private static int createTotalFooter(String total, Double dr, Double cr, XSSFSheet sheet, boolean first, int rowId) {
    XSSFRow row;
    Cell cell;
    CellStyle styleTitle = ExcelUtil.styleBold(sheet.getWorkbook(), HorizontalAlignment.LEFT);
    if (!first) {
      row = sheet.createRow(rowId++);
      row.setRowStyle(styleTitle);

      cell = row.createCell(1);
      cell.setCellValue(total);
      cell.setCellStyle(styleTitle);

      cell = row.createCell(4);
      if (dr != 0) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(dr)));
        cell.setCellStyle(styleTitle);
      } else {
        cell.setCellValue("");
      }

      cell = row.createCell(5);
      if (cr != 0) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(cr)));
        cell.setCellStyle(styleTitle);
      } else {
        cell.setCellValue("");
      }

      cell = row.createCell(6);
      if (dr - cr != 0) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(dr - cr)));
        cell.setCellStyle(styleTitle);
      } else {
        cell.setCellValue("");
      }
    }
    return rowId;
  }

}
