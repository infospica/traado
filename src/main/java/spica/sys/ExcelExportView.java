/*
 *  @(#)ExcelExportView            3 Jun, 2016
 * 
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 * 
 */
package spica.sys;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import spica.fin.view.AccountingMainView;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.service.CompanyAddressService;
import wawo.app.config.AppConfig;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 * Provide system level runtime features at view tier.
 *
 * @author <a href=mailto:arun.vc@infospica.com>Arun VC</a>
 */
@Named(value = "excelExportView")
@ApplicationScoped
public class ExcelExportView implements Serializable {

  boolean invoiceEntry = false;
  boolean ledgerBalance = false;
  @Inject
  private AccountingMainView accountingMainView;

  public void postProcessXLS(Object document) {
    HSSFFont font = null;
    HSSFWorkbook wb = null;
    HSSFSheet sheet = null;
    String sheetName = null;
    CellStyle style = null;
    int rows = 0;

    int colSize = 0;
    wb = (HSSFWorkbook) document;
    sheet = wb.getSheetAt(0);
    font = wb.createFont();
    style = wb.createCellStyle();
    Iterator<Row> rowIterator = sheet.iterator();
    DataExportStyle des = getDataExportStyle(sheet.getSheetName());
    sheetName = des.getName(sheet.getSheetName());
    wb.setSheetName(wb.getSheetIndex(sheet), sheetName);
//    Name name = wb.createName();
//    name.setNameName(sheetName);
    if (rowIterator.hasNext()) {
      Row row = rowIterator.next();
      Iterator<Cell> cellIterator = row.cellIterator();
      while (cellIterator.hasNext()) {
        colSize++;
        Cell cell = cellIterator.next();
        HSSFCellStyle my_style = wb.createCellStyle();
        HSSFFont my_font = wb.createFont();
        my_font.setBold(true);
        my_style.setFont(my_font);
        cell.setCellStyle(my_style);
      }
    }

    while (rowIterator.hasNext()) {
      Row row = rowIterator.next();
      Iterator<Cell> cellIterator = row.cellIterator();
      while (cellIterator.hasNext()) {
        Cell cell = cellIterator.next();
        if (!cell.getStringCellValue().isEmpty()) {
          try {
            if (des.isDouble(cell.getColumnIndex())) {
              cell.setCellValue(Double.valueOf(cell.getStringCellValue().replaceAll(",", "")));
            }
          } catch (Throwable ignored) {
          }
        }
        rows++;
      }
    }

    createPageHeader(rows - 1, colSize - 1, sheet, sheetName, font, style);
  }

  interface DataExportStyle {

    public boolean isDouble(int index);

    public String getName(String name);
  }

  public DataExportStyle getDataExportStyle(String sheetName) {

    if ("InvoiceWiseTable".equalsIgnoreCase(sheetName)) {
      return new DataExportStyle() {
        @Override
        public boolean isDouble(int index) {
          return (index > 0);
        }

        @Override
        public String getName(String name) {
          return "Invoice_Wise_Table_report";
        }

      };
    } else if ("invoiceEntrydt1".equals(sheetName)) {
      invoiceEntry = true;
    }
    return new DataExportStyle() {
      @Override
      public boolean isDouble(int index) {
        return (index > 0);
      }

      @Override
      public String getName(String name) {
        name = UserRuntimeView.instance().getMenuActive().getTitle();
        if (name.contains("Report")) {
          return name;
        } else {
          return name + " Report";
        }

      }
    };
  }

  public void createPageHeader(int rows, int col, HSSFSheet sheet, String sheetName, HSSFFont font, CellStyle style) {

    sheet.shiftRows(0, rows, 2);

    HSSFRow hr = sheet.getRow(0);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setFont(getFont(14, true, font));
    hr.setRowStyle(style);

    Cell c = hr.createCell(0);
    c.setCellStyle(style);
    c.setCellValue(UserRuntimeView.instance().getCompany().getCompanyName());
    hr.setHeight((short) 400);

    hr = sheet.getRow(1);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setFont(getFont(10, true, font));
    hr.setRowStyle(style);

    c = hr.createCell(0);
    c.setCellStyle(style);
    c.setCellValue(getDataHeader(sheetName));
    hr.setHeight((short) 1400);

    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, col));
    sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, col));

  }

  public String getDataHeader(String sheetName) {
    String header = null;
    if (!sheetName.equals("Ledger Balance Report") || accountingMainView.getExportLedger() == null) {
      Company company = UserRuntimeView.instance().getCompany();
      MainView main = Jsf.getMain();
      CompanyAddress address = null;
      try {
        address = CompanyAddressService.selectCompanyRegisteredAddress(main, company);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
      header = address.getAddress() + ", ";
      header += address.getDistrictId().getDistrictName() + " - " + address.getPin() + "\n";
    } else {
      header = accountingMainView.getExportLedger().getTitle() + "\n";;
    }
    header += sheetName + "\n";
    header += "Run Date : " + AppConfig.displayDatePattern.format(new Date());

    return header;
  }

  public HSSFFont getFont(int size, boolean bold, HSSFFont font) {

    font.setFontHeightInPoints((short) size);
    font.setFontName("Arial");
    font.setColor(IndexedColors.BLACK.getIndex());
    font.setBold(bold);
    font.setItalic(false);
    return font;
  }

  public void changeCellHeader(HSSFSheet sheet, int row, int cell, String cellValue) {
    sheet.getRow(row).getCell(cell).setCellValue(cellValue);
  }
}
