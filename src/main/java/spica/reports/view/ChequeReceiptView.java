/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spica.reports.model.ChequeReceipt;
import spica.reports.service.ChequeReceiptService;
import spica.scm.domain.Company;
import spica.scm.export.ExcelUtil;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sujesh
 */
@Named(value = "chequeReceiptView")
@ViewScoped
public class ChequeReceiptView implements Serializable {

  private List<ChequeReceipt> chequeReceiptList;
  private ChequeReceipt chequeReceipt;
  private Date date;
  private Company company;
  private Double totalAmount = 0.0;

  @PostConstruct
  public void init() {
    chequeReceiptList = new ArrayList<>();
  }

  public void fetchChequeReceiptList(MainView main) {
    try {
      if (StringUtil.isEmpty(chequeReceiptList)) {
        chequeReceiptList = ChequeReceiptService.seleChequeReceiptListByDate(main, getDate(), getCompany());
        for (ChequeReceipt crc : chequeReceiptList) {
          totalAmount += crc.getAmount();
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void reset() {
    chequeReceiptList = null;
    chequeReceipt = null;
    company = null;
  }

  public List<ChequeReceipt> getChequeReciptList() {
    return chequeReceiptList;
  }

  public ChequeReceipt getChequeReceipt() {
    return chequeReceipt;
  }

  public Date getDate() {
    if (date == null) {
      date = new Date();
    }
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Company getCompany() {
    if (null == company) {
      company = UserRuntimeView.instance().getCompany();
    }
    return company;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void checkPresent(MainView main) {
    try {
      createCheckPresentExl(getChequeReciptList(), getCompany());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void createCheckPresentExl(List<ChequeReceipt> chequeReceiptreport, Company company) throws IOException {

    String name = "cheque_preset";
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      XSSFRow row;
      Cell cell;

      CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      CellStyle colHead = ExcelUtil.styleHead(workbook);

      String[] arr = new String[]{"Sl.No", "Name of the Remitter", "DD/Cheque Number", "Date", "Amount", "Drawee", "Contra Date"};
      int size = 0, rowId = 0, sl = 1;
      int cellId = 0;

      row = sheet.createRow(rowId);
      cell = row.createCell(0);
      cell.setCellValue(company.getCompanyName().toUpperCase());
      cell.setCellStyle(colHead);
      sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 7));
      rowId += 1;
      row = sheet.createRow(rowId);
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        sheet.autoSizeColumn(size);
        size++;
      }
      rowId += 1;
      for (ChequeReceipt cr : chequeReceiptreport) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);

        cell = row.createCell(cellId++);
        cell.setCellValue(sl);

        cell = row.createCell(cellId++);
        cell.setCellValue(cr.getPartyName());

        cell = row.createCell(cellId++);
        cell.setCellValue(cr.getChequeNo());

        cell = row.createCell(cellId++);
        cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(cr.getCreatedAt()));

        cell = row.createCell(cellId++);
        cell.setCellStyle(styleNormalRight);
        cell.setCellValue(cr.getAmount());

        cell = row.createCell(cellId++);
        cell.setCellValue(cr.getDrawee());
        sl += 1;
      }
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }
}
