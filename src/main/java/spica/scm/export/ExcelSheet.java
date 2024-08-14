package spica.scm.export;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spica.constant.AccountingConstant;
import spica.fin.common.FinalStatement;
import spica.reports.model.AgeWiseStockAnalysis;
import spica.reports.model.CompanyCustomerSales;
import spica.reports.model.CreditNoteReport;
import spica.reports.model.CustomerOutstanding;
import spica.reports.model.FilterParameters;
import spica.scm.common.PlatformSummary;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerImporter;
import spica.scm.domain.District;
import spica.scm.domain.Product;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.Territory;
import static spica.scm.export.ExcelUtil.FILL_ORANGE;
import static spica.scm.export.ExcelUtil.decimalFormat;
import spica.scm.util.MathUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.faces.AppView;
import wawo.app.faces.MainView;
import wawo.entity.util.DateUtil;
import wawo.entity.util.UniqueCheck;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author godson
 */
public class ExcelSheet {

  private static Double today = 0.00, thisMonthSales = 0.00, thisMonthReturn = 0.00, thisMonthTotal = 0.00, lastMonthTotal = 0.00;
  private static Double qty = 0.00, qtyFree = 0.00, salesValue = 0.00, billAmount = 0.00, billAmountWithouTcs = 0.00, tcsAmount = 0.00;
  private static Double qtyCustomer = 0.00, qtyFreeCustomer = 0.00, salesValueCustomer = 0.00, billAmountCustomer = 0.00;
  private static Double qtyTotal = 0.00, qtyFreeTotal = 0.00, salesValueTotal = 0.00, billAmountTotal = 0.00, billAmountWithouTcsTotal = 0.00, tcsAmountTotal = 0.00;
  ;
  private static Double todayCompanyTotal = 0.00, thisMonthSalesCompanyTotal = 0.00, thisMonthReturnCompanyTotal = 0.00, thisMonthCompanyTotal = 0.00, lastMonthCompanyTotal = 0.00;
  private static int groupLastRow = 0, invoiceFirstRow = 0;
  private static int groupFirstRow = 0, invoiceLastRow = 0;

  public static void createCustomerOutstandingReport(MainView main, Customer customer, List<CustomerOutstanding> customerOutstandingList, AccountGroup accountGroup, Integer type) throws IOException {
    //setWorkbook(workbook);

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet("customer_outstanding");
      sheet.setDefaultColumnWidth(15);
      String[] arr = null;
      if (type != SystemConstants.SHOW_INVOICE_WISE) {
        arr = new String[]{"Customer Name", "District", "Invoice No", "Voucher Type", "Invoice Date", "Bill Amount", "Received Amount", "Recievable Amount", "Total", "Outstanding Days", "Age Range", "Agent Name", "District", "Territory", "Phone 1", "Phone 2", "Address"};
      } else {
        arr = new String[]{"Customer Name", "District", "Invoice No", "Voucher Type", "Invoice Date", "Bill Amount", "Received Amount", "Recievable Amount", "Total", "Outstanding Days", "Age Range", "Agent Name"};
      }
      XSSFRow row;

      CellStyle style = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);
      CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      CellStyle rightAlign = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      CellStyle rightBold = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
      CellStyle rightBoldNegative = ExcelUtil.styleRedBold(workbook, HorizontalAlignment.RIGHT);
//    date
      int rowId = 0;
      String title = "Customer Outstanding as on " + SystemRuntimeConfig.SDF_DD_MM_YYYY.format(new Date());
      rowId = ExcelUtil.createDocHeader(main, sheet, rowId, title, arr.length - 1, null, accountGroup, customer);

      int cellId = 0;
      row = sheet.createRow(rowId++);
      int size = 0;
      Cell cell = row.createCell(0);
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        cell.setCellStyle(style);
        sheet.autoSizeColumn(size);
        size++;
      }

      Double billAmount = 0.0, receivedAmount = 0.0, receivableamount = 0.0;
      if (customerOutstandingList != null && !customerOutstandingList.isEmpty()) {
        ListIterator<CustomerOutstanding> list = customerOutstandingList.listIterator();
        while (list.hasNext()) {
          CustomerOutstanding cust = list.next();
          cellId = 0;
          row = sheet.createRow(rowId++);
          row.setRowStyle(styleNormal);

          cell = row.createCell(cellId++);
          cell.setCellValue(cust.getName());
          cell.setCellStyle(styleNormal);
          cell = row.createCell(cellId++);
          cell.setCellValue(cust.getDistrict());
          cell.setCellStyle(styleNormal);
          cell = row.createCell(cellId++);
          cell.setCellValue(cust.getInvoiceno());
          cell.setCellStyle(styleNormal);
          cell = row.createCell(cellId++);
          cell.setCellValue(cust.getVoucherType());
          cell.setCellStyle(styleNormal);
          cell = row.createCell(cellId++);
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(cust.getInvoicedate()));
          cell.setCellStyle(styleNormal);
          cell = row.createCell(cellId++);
          if (cust.getInvoiceamount() != null) {
            cell.setCellValue(doublePrecision(cust.getInvoiceamount()));
          }
          cell.setCellStyle(rightAlign);
          cell = row.createCell(cellId++);
          if (cust.getReceivedAmount() != null && cust.getReceivedAmount() > 0) {
            receivedAmount += cust.getReceivedAmount();
            if (cust.getReceivedAmount() != null) {
              cell.setCellValue(doublePrecision(cust.getReceivedAmount()));
            }
            cell.setCellStyle(rightAlign);
          } else {
            cell.setCellValue("");
            cell.setCellStyle(rightAlign);
          }
          cell = row.createCell(cellId++);
          if (cust.getOustandingamount() != null && cust.getOustandingamount() != 0) {
            receivableamount += cust.getOustandingamount();
            if (cust.getOustandingamount() != null) {
              cell.setCellValue(doublePrecision(cust.getOustandingamount()));
            }
            cell.setCellStyle(rightAlign);
          } else {
            cell.setCellValue("");
            cell.setCellStyle(rightAlign);
          }

          Double cumulativeSum = 0.00;
          if (customerOutstandingList.size() > list.nextIndex()) {

            if (!cust.getName().equals(customerOutstandingList.get(list.nextIndex()).getName())) {
              cumulativeSum = cust.getCumulativeAmount() == null ? 0.0 : cust.getCumulativeAmount();
            } else {
              cumulativeSum = 0.00;
            }
          } else {
            cumulativeSum = cust.getCumulativeAmount();
          }
          cumulativeSum = cumulativeSum == null ? 0.00 : cumulativeSum;
          if (cumulativeSum > 0) {
            cumulativeSum = doublePrecision(cumulativeSum);
          }
          cell = row.createCell(cellId++);
          if (cumulativeSum > 0) {
            cell.setCellValue(cumulativeSum);
            cell.setCellStyle(rightBold);
          } else if (cumulativeSum < 0) {
            cell.setCellValue(cumulativeSum);
            cell.setCellStyle(rightBoldNegative);
          } else {
            cell.setCellValue("");
          }

//        cell = row.createCell(cellId++);
//        cell.setCellValue(cust.getCumulativeAmount());
//        cell.setCellStyle(rightAlign);
          cell = row.createCell(cellId++);
          cell.setCellValue(cust.getDaysoutstanding());
          cell.setCellStyle(rightAlign);

          cell = row.createCell(cellId++);
          cell.setCellValue(cust.getDaysRange());
          cell.setCellStyle(styleNormal);

          billAmount += cust.getInvoiceamount() == null ? 0.0 : cust.getInvoiceamount();

          cell = row.createCell(cellId++);
          cell.setCellValue(cust.getAgentName());
          cell.setCellStyle(styleNormal);
          if (type != SystemConstants.SHOW_INVOICE_WISE) {
            cell = row.createCell(cellId++);
            cell.setCellValue(cust.getDistrict());
            cell.setCellStyle(styleNormal);
            cell = row.createCell(cellId++);
            cell.setCellValue(cust.getTerritoryName());
            cell.setCellStyle(styleNormal);
            cell = row.createCell(cellId++);
            cell.setCellValue(cust.getPhone1());
            cell.setCellStyle(styleNormal);
            cell = row.createCell(cellId++);
            cell.setCellValue(cust.getPhone2());
            cell.setCellStyle(styleNormal);
            cell = row.createCell(cellId++);
            cell.setCellValue(cust.getAddress());
            cell.setCellStyle(styleNormal);
          }
        }
      }

      row = sheet.createRow(rowId++);
      row.setRowStyle(styleNormal);
      cell = row.createCell(5);
      if (billAmount != null) {
        cell.setCellValue(doublePrecision(billAmount));
      }
      cell.setCellStyle(rightBold);

      cell = row.createCell(6);
      if (receivedAmount != null) {
        cell.setCellValue(doublePrecision(receivedAmount));
      }
      cell.setCellStyle(rightBold);

      cell = row.createCell(7);
      if (receivableamount != null) {
        cell.setCellValue(doublePrecision(receivableamount));
      }
      cell.setCellStyle(rightBold);
      ExcelUtil.write("customer_outstanding.xlsx", workbook);
    }
  }

  private static Double doublePrecision(Double number) {
    AppView appView = new AppView();
    String n = appView.decimal(number);
    n = n.replace(",", "");
    return Double.parseDouble(n);

  }

  public static void createAgeWiseStockReport(List<AgeWiseStockAnalysis> ageWiseStockAnalysisList) throws IOException {
    // setWorkbook(workbook);
    String name = "age_wise_stock_analysis";
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      XSSFRow row;
      Cell cell;
      // Create cell style 
      CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      CellStyle styleNormalCenter = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);

      int rowId = 0;
      int cellId = 0;
//     List
      String[] arr = new String[]{"PRODUCT NAME", "BILL NO", "BILL DATE", "QTY", "FQTY", "DAYS", "TOTAL QTY", "0 TO 30 DAYS \n (Taxable Amt)",
        "31 TO 60 DAYS \n (Taxable Amt)", "61 TO 120 DAYS \n (Taxable Amt)", "121 TO 180 DAYS \n (Taxable Amt)", "181 TO 365 DAYS \n (Taxable Amt)", "ABOVE 365 DAYS \n (Taxable Amt)",
        "TAXABLE AMT \n (TOTAL)", "GST %", "GST AMT", "NET AMT", "HSN/SAC CODE"};
      row = sheet.createRow(rowId++);
      row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      row.setHeight((short) 600);
      int size = 0;
      cellId = 0;

      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size);
        size++;
      }
      int totalQty = 0;
      Double tax30 = 0.00, tax60 = 0.00, tax120 = 0.00, tax180 = 0.00, tax365 = 0.00,
              taxAbove365 = 0.00, totalTaxable = 0.00, totalgstAmt = 0.00, totalNetAmt = 0.00;
      CellStyle hStyle = ExcelUtil.styleHead(workbook);
      if (ageWiseStockAnalysisList != null && !ageWiseStockAnalysisList.isEmpty()) {
        for (AgeWiseStockAnalysis list : ageWiseStockAnalysisList) {
          cellId = 0;
          row = sheet.createRow(rowId++);
          row.setRowStyle(styleNormal);

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getProductName());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getInvoiceNo());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getProductEntryDate());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getCurrentQty());

          cell = row.createCell(cellId++);
          cell.setCellValue(0);
          cell.setCellStyle(styleNormalRight);

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getDays());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getCurrentQty());

          cell = row.createCell(cellId++);
          if (list.getDays() >= 0 && list.getDays() <= 30) {
            cell.setCellValue(list.getTaxableAmount());
            cell.setCellStyle(styleNormalRight);
            tax30 += list.getTaxableAmount();
          } else {
            cell.setCellValue("-");
            cell.setCellStyle(styleNormalCenter);
          }
          cell = row.createCell(cellId++);
          if (list.getDays() >= 31 && list.getDays() <= 60) {
            cell.setCellValue(list.getTaxableAmount());
            cell.setCellStyle(styleNormalRight);
            tax60 += list.getTaxableAmount();
          } else {
            cell.setCellValue("-");
            cell.setCellStyle(styleNormalCenter);
          }
          cell = row.createCell(cellId++);
          if (list.getDays() >= 61 && list.getDays() <= 120) {
            cell.setCellValue(list.getTaxableAmount());
            cell.setCellStyle(styleNormalRight);
            tax120 += list.getTaxableAmount();
          } else {
            cell.setCellValue("-");
            cell.setCellStyle(styleNormalCenter);
          }
          cell = row.createCell(cellId++);
          if (list.getDays() >= 121 && list.getDays() <= 180) {
            cell.setCellValue(list.getTaxableAmount());
            cell.setCellStyle(styleNormalRight);
            tax180 += list.getTaxableAmount();
          } else {
            cell.setCellValue("-");
            cell.setCellStyle(styleNormalCenter);
          }
          cell = row.createCell(cellId++);
          if (list.getDays() >= 181 && list.getDays() <= 365) {
            cell.setCellValue(list.getTaxableAmount());
            cell.setCellStyle(styleNormalRight);
            tax365 += list.getTaxableAmount();
          } else {
            cell.setCellValue("-");
            cell.setCellStyle(styleNormalCenter);
          }
          cell = row.createCell(cellId++);
          if (list.getDays() > 365) {
            cell.setCellValue(list.getTaxableAmount());
            cell.setCellStyle(styleNormalRight);
            taxAbove365 += list.getTaxableAmount();
          } else {
            cell.setCellValue("-");
            cell.setCellStyle(styleNormalCenter);
          }
          cell = row.createCell(cellId++);
          cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTaxableAmount())));
          cell.setCellStyle(styleNormalRight);

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getRatePercentage());

          Double gstAmount = list.getTaxableAmount() * (list.getRatePercentage() / 100);
          Double netAmount = list.getTaxableAmount() + gstAmount;

          cell = row.createCell(cellId++);
          cell.setCellValue(gstAmount);
          cell.setCellStyle(styleNormalRight);

          cell = row.createCell(cellId++);
          cell.setCellValue(netAmount);
          cell.setCellStyle(styleNormalRight);

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getHsnCode());
          totalQty += list.getCurrentQty();
          totalTaxable += list.getTaxableAmount();
          totalgstAmt += gstAmount;
          totalNetAmt += netAmount;
        }
      }
      cellId = 0;
      row = sheet.createRow(rowId);
      row.setRowStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue("Total");
      cell.setCellStyle(hStyle);
      sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 2));
      cellId += 2;
      cell = row.createCell(cellId++);
      cell.setCellValue(totalQty);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue(0);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue("");
      cell = row.createCell(cellId++);
      cell.setCellValue(totalQty);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue(tax30);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue(tax60);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue(tax120);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue(tax180);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue(tax365);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue(taxAbove365);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue(totalTaxable);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue("");
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue(totalgstAmt);
      cell.setCellStyle(hStyle);
      cell = row.createCell(cellId++);
      cell.setCellValue(totalNetAmt);
      cell.setCellStyle(hStyle);
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  public static void createDailySalesAgentwiseReport(List<CompanyCustomerSales> dailywiseReport, String company) throws IOException {
    // setWorkbook(workbook);
    String name = "daily_sales_agent_wise_report";
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      XSSFRow row;
      Cell cell;
      // Create cell style 
      //CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      CellStyle styleVertical = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);

      //  CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
//    CellStyle styleNormalCenter = workbook.createCellStyle();
//    styleNormalCenter.setAlignment(HorizontalAlignment.CENTER);
//
//    CellStyle styleHead = workbook.createCellStyle();
//    styleHead.setFont(getFontBlack(15, true, workbook));
//    styleHead.setAlignment(HorizontalAlignment.CENTER);
//    styleHead.setVerticalAlignment(VerticalAlignment.CENTER);
//
//    CellStyle styleTitle = workbook.createCellStyle();
//    styleTitle.setFont(getFontBlack(10, true, workbook));
//    styleTitle.setAlignment(HorizontalAlignment.LEFT);
      SimpleDateFormat simpleformat = new SimpleDateFormat("MMM-YY");
      //   Calendar cal = Calendar.getInstance();

      int rowId = 0;
      int cellId = 0, size = 0;
      int i = 1;
      int listSize = dailywiseReport.size();
      row = sheet.createRow(rowId++);
      cell = row.createCell(cellId++);
      cell.setCellValue(company);
      cell.setCellStyle(ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

      row = sheet.createRow(rowId++);
      cell = row.createCell(0);
      cell.setCellValue("SALES AS ON");
      cell.setCellStyle(ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
      cell = row.createCell(3);
      cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(new Date()));
      cell.setCellStyle(ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      cell = row.createCell(4);
      cell.setCellValue(simpleformat.format(new Date()));
      cell.setCellStyle(ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 6));
      cell = row.createCell(7);
      cell.setCellValue(simpleformat.format(DateUtil.moveMonths(new Date(), -1)));
      cell.setCellStyle(ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

      cellId = 0;
      String[] arr = new String[]{"Account Group", "Territory", "Sales Agent", "TODAY SALES TOTAL", "THIS MONTH SALE", "THIS MONTH RETURN", "THIS MONTH TOTAL SALE", "LAST MONTH TOTAL SALE"};
      row = sheet.createRow(rowId++);

      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj.toString());
        cell.setCellStyle(ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size);
        size++;
      }
      UniqueCheck uaccnt = new UniqueCheck();
      boolean first = true;
      groupFirstRow = 0;
      groupLastRow = 0;
      for (CompanyCustomerSales list : dailywiseReport) {
        if (uaccnt.exist(list.getGroupName())) {
          rowId = addDailySalesAgentReportRows(workbook, list, sheet, rowId);

          if (listSize == i) {
            rowId = createDailySalesAgentReportFooter(workbook, sheet, first, rowId, false);
            if (groupFirstRow < groupLastRow) {
              sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
              sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
            }
            today = 0.00;
            thisMonthSales = 0.00;
            thisMonthReturn = 0.00;
            thisMonthTotal = 0.00;
            lastMonthTotal = 0.00;
          }
        } else {
          rowId = createDailySalesAgentReportFooter(workbook, sheet, first, rowId, false);
          if (groupFirstRow < groupLastRow) {
            sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
            sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
          }
          today = 0.00;
          thisMonthSales = 0.00;
          thisMonthReturn = 0.00;
          thisMonthTotal = 0.00;
          lastMonthTotal = 0.00;
          groupFirstRow = rowId;
          rowId = addDailySalesAgentReportRows(workbook, list, sheet, rowId);
          first = false;
          if (listSize == i) {
            rowId = createDailySalesAgentReportFooter(workbook, sheet, first, rowId, false);
            if (groupFirstRow < groupLastRow) {
              sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
              sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
            }
            today = 0.00;
            thisMonthSales = 0.00;
            thisMonthReturn = 0.00;
            thisMonthTotal = 0.00;
            lastMonthTotal = 0.00;
          }
        }
        i++;
      }
      today = todayCompanyTotal;
      thisMonthSales = thisMonthSalesCompanyTotal;
      thisMonthReturn = thisMonthReturnCompanyTotal;
      thisMonthTotal = thisMonthCompanyTotal;
      lastMonthTotal = lastMonthCompanyTotal;
      createDailySalesAgentReportFooter(workbook, sheet, false, rowId, true);
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  private static int addDailySalesAgentReportRows(XSSFWorkbook workbook, CompanyCustomerSales list, XSSFSheet sheet, int rowId) {
    XSSFRow row;
    Cell cell;
    int cellId = 0;
    // CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);

    // CellStyle rightAlign =ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    CellStyle styleRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.RIGHT);

    cellId = 0;
    row = sheet.createRow(rowId++);

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getGroupName());

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getTerritoryName());

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getAgentName());

    cell = row.createCell(cellId++);
    if ((list.getTodaySales() - list.getTodayReturn()) > 0) {
      cell.setCellValue(list.getTodaySales() - list.getTodayReturn());
    }
    today += (list.getTodaySales() - list.getTodayReturn());

    cell = row.createCell(cellId++);
    if (list.getThisMonthSales() != 0) {
      cell.setCellValue(list.getThisMonthSales());
    }
    thisMonthSales += list.getThisMonthSales();

    cell = row.createCell(cellId++);
    if (list.getThisMonthReturn() != 0) {
      cell.setCellValue(list.getThisMonthReturn());
      cell.setCellStyle(styleRed);
    }
    thisMonthReturn += list.getThisMonthReturn();

    cell = row.createCell(cellId++);
    if ((list.getThisMonthSales() - list.getThisMonthReturn()) != 0) {
      cell.setCellValue(list.getThisMonthSales() - list.getThisMonthReturn());
    }
    thisMonthTotal += (list.getThisMonthSales() - list.getThisMonthReturn());

    cell = row.createCell(cellId++);
    if ((list.getLastMonthSales() - list.getLastMonthReturn()) != 0) {
      cell.setCellValue(list.getLastMonthSales() - list.getLastMonthReturn());
    }
    lastMonthTotal += (list.getLastMonthSales() - list.getLastMonthReturn());
    return rowId;
  }

  private static int createDailySalesAgentReportFooter(XSSFWorkbook workbook, XSSFSheet sheet, boolean first, int rowId, boolean color) {
    XSSFRow row;
    Cell cell;
    CellStyle styleFooter = null;
    if (color) {
      styleFooter = ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.RIGHT, workbook);
    } else {
      styleFooter = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
    }
    if (!first) {
      sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 2));
      row = sheet.createRow(rowId++);
      cell = row.createCell(0);
      cell.setCellValue("TOTAL");
      cell.setCellStyle(styleFooter);

      cell = row.createCell(3);
      cell.setCellValue(today);
      todayCompanyTotal += today;
      cell.setCellStyle(styleFooter);

      cell = row.createCell(4);
      cell.setCellValue(thisMonthSales);
      thisMonthSalesCompanyTotal += thisMonthSales;
      cell.setCellStyle(styleFooter);

      cell = row.createCell(5);
      cell.setCellValue(thisMonthReturn);
      thisMonthReturnCompanyTotal += thisMonthReturn;
      cell.setCellStyle(styleFooter);

      cell = row.createCell(6);
      cell.setCellValue(thisMonthTotal);
      thisMonthCompanyTotal += thisMonthTotal;
      cell.setCellStyle(styleFooter);

      cell = row.createCell(7);
      cell.setCellValue(lastMonthTotal);
      lastMonthCompanyTotal += lastMonthTotal;
      cell.setCellStyle(styleFooter);
      groupLastRow = rowId - 2;
    }
    return rowId;
  }

  public static void createProfitAndLostReport(List<FinalStatement> expenseList, List<FinalStatement> incomeList,
          List<FinalStatement> pandlaccountExpenseList, List<FinalStatement> pandlaccountIncomeList, Company company, String period, MainView main) throws IOException {
//    setWorkbook(workbook);
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet("Trading_And_Profit_And_Loss");
      sheet.setDefaultColumnWidth(20);
      XSSFRow row;
      Cell cell;

      // Create cell style 
      //  CellStyle styleNormal =ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      //  CellStyle styleVertical = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);
      //  CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      //   CellStyle styleNormalCenter = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);
      CellStyle amount = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT, 13);
      amount.setWrapText(true);
      amount.setVerticalAlignment(VerticalAlignment.CENTER);

      CellStyle colHead = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);
      colHead.setWrapText(true);
      colHead.setVerticalAlignment(VerticalAlignment.CENTER);

      CellStyle regular = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      regular.setWrapText(true);
      regular.setVerticalAlignment(VerticalAlignment.CENTER);

      CellStyle regularAmount = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      regularAmount.setWrapText(true);
      regularAmount.setVerticalAlignment(VerticalAlignment.CENTER);

      //  CellStyle styleTitle = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      //SimpleDateFormat simpleformat = new SimpleDateFormat("MMM-YY");
      // Calendar cal = Calendar.getInstance();
      int rowId = 0, cellId = 0, size = 1;
      //row = sheet.createRow(rowId);
      String[] arr = new String[]{"", "Particulars", " ", "Amount", " ", "Particulars", " ", "Amount"};

      rowId = ExcelUtil.createDocHeader(main, sheet, rowId, " TRADING AND PROFIT AND LOSS " + period, arr.length - 1, company, null, null); //Can use account group here
      //rowId += 2;
      row = sheet.createRow(rowId++);

      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        cell.setCellStyle(colHead);
        sheet.autoSizeColumn(size);
        size++;
      }
      cellId = 0;
      int rowCount = 0;
      Double expenseAmount = 0.0, incomeAmount = 0.0, pandlExpAmoont = 0.0, pandlIncomeAmount = 0.0;
      rowCount = expenseList.size() >= incomeList.size() ? expenseList.size() : incomeList.size();
      FinalStatement f;
      for (int i = 0; i < rowCount; i++) {
        row = sheet.createRow(rowId++);
        if (i < expenseList.size()) {
          cell = row.createCell(1);
          f = expenseList.get(i);
          cell.setCellValue(f.getTitle());
          cell.setCellStyle(regular);

//        cell = row.createCell(2);
//        if (expenseList.get(i).getOpeningBalance() != null) {
//          cell.setCellValue(expenseList.get(i).getOpeningBalance());
//        }
//        cell.setCellStyle(regularAmount);
          cell = row.createCell(2);
          if (f.getTrtype() != null && (f.getTrtype().equals("dr") || f.getTrtype().equals("cr"))) {
            cell.setCellValue(MathUtil.roundOff(f.getTotalAmount(), 2));
          }
          cell.setCellStyle(regularAmount);

          cell = row.createCell(3);
          if (f.getTrtype() != null && (f.getTrtype().equals("dr"))) {
            if (f.getDebit() != null) {
              cell.setCellValue(f.getDebit());
            }
          } else {
            cell.setCellValue(MathUtil.roundOff(f.getTotalAmount(), 2));
          }
          cell.setCellStyle(regularAmount);
          expenseAmount += f.getTotalAmount();
        }
        cell = row.createCell(4);
        cell.setCellValue("");
        if (i < incomeList.size()) {
          f = incomeList.get(i);
          cell = row.createCell(5);
          cell.setCellValue(f.getTitle());
          cell.setCellStyle(regular);

//        cell = row.createCell(7);
//        if (f.getOpeningBalance() != null) {
//          cell.setCellValue(f.getOpeningBalance());
//        }
//        cell.setCellStyle(regularAmount);
          cell = row.createCell(6);
          if (f.getTrtype() != null && (f.getTrtype().equals("dr") || f.getTrtype().equals("cr"))) {
            cell.setCellValue(MathUtil.roundOff(f.getTotalAmount(), 2));
          }
          cell.setCellStyle(regularAmount);

          cell = row.createCell(7);
          if (f.getTrtype() != null && (f.getTrtype().equals("dr"))) {
            if (f.getDebit() != null) {
              cell.setCellValue(MathUtil.roundOff(f.getDebit(), 2));
            }
          } else {
            cell.setCellValue(MathUtil.roundOff(f.getTotalAmount(), 2));
          }
          cell.setCellStyle(regularAmount);
          incomeAmount += f.getTotalAmount();
        }
      }
      row = sheet.createRow(rowId++);
      cell = row.createCell(3);
      cell.setCellValue(MathUtil.roundOff(expenseAmount, 2));
      cell.setCellStyle(amount);

      cell = row.createCell(7);
      cell.setCellValue(MathUtil.roundOff(incomeAmount, 2));
      cell.setCellStyle(amount);
      int t = rowId++;
      sheet.addMergedRegion(new CellRangeAddress(t, t, 0, 7));

      cellId = 0;
      row = sheet.createRow(rowId++);
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        if (!obj.toString().equals("Opening Balance")) {
          cell.setCellValue(obj.toString());
        }
        cell.setCellStyle(colHead);
//      cell.setCellStyle(setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size);
        size++;
      }

      rowCount = pandlaccountExpenseList.size() >= pandlaccountIncomeList.size() ? pandlaccountExpenseList.size() : pandlaccountIncomeList.size();
      for (int i = 0; i < rowCount; i++) {
        row = sheet.createRow(rowId++);
        if (i < pandlaccountExpenseList.size()) {
          cell = row.createCell(1);
          f = pandlaccountExpenseList.get(i);
          cell.setCellValue(f.getTitle());
          cell.setCellStyle(regular);

//        cell = row.createCell(2);
//        cell.setCellStyle(regularAmount);
          cell = row.createCell(2);
          cell.setCellStyle(regularAmount);

          cell = row.createCell(3);
          cell.setCellValue(MathUtil.roundOff(f.getTotalAmount(), 2));
          cell.setCellStyle(regularAmount);
          pandlExpAmoont += f.getTotalAmount();
        }
        cell = row.createCell(4);
        cell.setCellValue("");
        if (i < pandlaccountIncomeList.size()) {
          cell = row.createCell(5);
          f = pandlaccountIncomeList.get(i);
          cell.setCellValue(f.getTitle());
          cell.setCellStyle(regular);

//        cell = row.createCell(6);
//        cell.setCellStyle(regularAmount);
          cell = row.createCell(6);
          cell.setCellStyle(regularAmount);

          cell = row.createCell(7);
          cell.setCellValue(MathUtil.roundOff(f.getTotalAmount(), 2));
          cell.setCellStyle(regularAmount);
          pandlIncomeAmount += f.getTotalAmount();
        }
      }

      row = sheet.createRow(rowId++);
      cell = row.createCell(3);
      cell.setCellValue(MathUtil.roundOff(pandlExpAmoont, 2));
      cell.setCellStyle(amount);

      cell = row.createCell(7);
      cell.setCellValue(MathUtil.roundOff(pandlIncomeAmount, 2));
      cell.setCellStyle(amount);
      t = rowId++;
      sheet.addMergedRegion(new CellRangeAddress(t, t, 0, 7));
      ExcelUtil.write("Trading_And_Profit_And_Loss.xlsx", workbook);
    }
  }

  public static void createDailyReport(List<CompanyCustomerSales> dailyReport, FilterParameters filterParameters) throws IOException {
    //   setWorkbook(workbook);

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet("daily_sales_agent_report");
      sheet.setDefaultColumnWidth(22);
      XSSFRow row = null;
      // DecimalFormat decimalFormat = ExcelUtil.decimalFormat;
//    XSSFFont font = workbook.createFont();
//    font.setFontHeightInPoints((short) 10);
//    font.setFontName("Arial");
//    font.setColor(IndexedColors.BLACK.getIndex());
//    font.setBold(true);
//    font.setItalic(false);

      //   CellStyle styletotal = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      // Create cell style 
      //   CellStyle style = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
//    CellStyle styleNormalRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.LEFT);
//    CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
//    CellStyle styleNormalRightRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.RIGHT);
      CellStyle styleVertical = ExcelUtil.style(workbook, VerticalAlignment.CENTER);
      // CellStyle styleNormalCenter = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);

//    CellStyle colHead =  ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
////    colHead.setWrapText(true);
////    colHead.setVerticalAlignment(VerticalAlignment.CENTER);
      Cell cell;
      int rowId = 0;
      int cellId = 0;
      row = sheet.createRow(rowId++);
      cell = row.createCell(cellId++);
      String text1 = "Sales Agent : " + (filterParameters.getSalesAgent() != null ? filterParameters.getSalesAgent().getName() : "");
      cell.setCellValue(text1);
      sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
      row = sheet.createRow(rowId++);
      cellId = 0;
      cell = row.createCell(cellId++);
      String text2 = "Entry Date : " + (filterParameters.getToDate() != null ? SystemRuntimeConfig.SDF_DD_MM_YYYY.format(filterParameters.getToDate()) : "");
      cell.setCellValue(text2);
      sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
      row = sheet.createRow(rowId++);
      cellId = 0;
      String[] arr = new String[]{"Customer Name", "Invoice No", "Product Name", "Quantity", "Quantity Free", "Sales Value", "Bill Amount"};
      row = sheet.createRow(rowId++);
      //   int size = 0;
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj.toString());
        cell.setCellStyle(ExcelUtil.setFillColor(ExcelUtil.FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      }
      int listSize = dailyReport.size();
      int i = 1;
      UniqueCheck ucust = new UniqueCheck();
      UniqueCheck uinvoice = new UniqueCheck();
      boolean first = true;
      groupFirstRow = 0;
      groupLastRow = 0;
      invoiceFirstRow = 0;
      invoiceLastRow = 0;
      qtyTotal = 0.0;
      qtyFreeTotal = 0.0;
      salesValueTotal = 0.0;
      billAmountTotal = 0.0;
      for (CompanyCustomerSales list : dailyReport) {
        if (ucust.exist(list.getCustomerName())) {
          if (uinvoice.exist(list.getInvoiceNo())) {
            rowId = addDailyReportRows(workbook, list, sheet, rowId);

            if (listSize == i) {
              rowId = createDailyReportFooter(workbook, sheet, first, rowId, list.getInvoiceNo(), false, false);
              if (invoiceFirstRow < invoiceLastRow) {
                sheet.addMergedRegion(new CellRangeAddress(invoiceFirstRow, invoiceLastRow, 1, 1));
                sheet.getRow(invoiceFirstRow).getCell(1).setCellStyle(styleVertical);
              }
              rowId = createDailyReportFooter(workbook, sheet, first, rowId, null, false, true);
              if (groupFirstRow < groupLastRow) {
                sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
                sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
              }
              qty = 0.00;
              qtyFree = 0.00;
              salesValue = 0.00;
              billAmount = 0.00;
              qtyCustomer = 0.00;
              qtyFreeCustomer = 0.00;
              salesValueCustomer = 0.00;
              billAmountCustomer = 0.00;
            }
          } else {
//          If invoice does not exist
            if (i > 1) {
              rowId = createDailyReportFooter(workbook, sheet, first, rowId, dailyReport.get(i - 2).getInvoiceNo(), false, false);
              if (invoiceFirstRow < invoiceLastRow) {
                sheet.addMergedRegion(new CellRangeAddress(invoiceFirstRow, invoiceLastRow, 1, 1));
                sheet.getRow(invoiceFirstRow).getCell(1).setCellStyle(styleVertical);
              }
            }
            if (groupFirstRow < groupLastRow) {
              sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
              sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
            }
            invoiceFirstRow = rowId;
            qty = 0.00;
            qtyFree = 0.00;
            salesValue = 0.00;
            billAmount = 0.00;
            rowId = addDailyReportRows(workbook, list, sheet, rowId);
            first = false;
            if (listSize == i) {
              rowId = createDailyReportFooter(workbook, sheet, first, rowId, list.getInvoiceNo(), false, false);
              if (invoiceFirstRow < invoiceLastRow) {
                sheet.addMergedRegion(new CellRangeAddress(invoiceFirstRow, invoiceLastRow, 1, 1));
                sheet.getRow(invoiceFirstRow).getCell(1).setCellStyle(styleVertical);
              }
              rowId = createDailyReportFooter(workbook, sheet, first, rowId, null, false, true);
              if (groupFirstRow < groupLastRow) {
                sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
                sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
              }
              qty = 0.00;
              qtyFree = 0.00;
              salesValue = 0.00;
              billAmount = 0.00;
            }
          }
        } else {
//        If customer does not exist
          uinvoice.exist(list.getInvoiceNo());
          if (i > 1) {
            rowId = createDailyReportFooter(workbook, sheet, first, rowId, dailyReport.get(i - 2).getInvoiceNo(), false, false);
            if (invoiceFirstRow < invoiceLastRow) {
              sheet.addMergedRegion(new CellRangeAddress(invoiceFirstRow, invoiceLastRow, 1, 1));
              sheet.getRow(invoiceFirstRow).getCell(1).setCellStyle(styleVertical);
            }
          }
          rowId = createDailyReportFooter(workbook, sheet, first, rowId, null, false, true);
          if (groupFirstRow < groupLastRow) {
            sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
            sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
          }

          qty = 0.00;
          qtyFree = 0.00;
          salesValue = 0.00;
          billAmount = 0.00;
          qtyCustomer = 0.00;
          qtyFreeCustomer = 0.00;
          salesValueCustomer = 0.00;
          billAmountCustomer = 0.00;
          invoiceFirstRow = rowId;
          groupFirstRow = rowId;
          rowId = addDailyReportRows(workbook, list, sheet, rowId);
          first = false;
          if (listSize == i) {
            rowId = createDailyReportFooter(workbook, sheet, first, rowId, list.getInvoiceNo(), false, false);
            if (invoiceFirstRow < invoiceLastRow) {
              sheet.addMergedRegion(new CellRangeAddress(invoiceFirstRow, invoiceLastRow, 1, 1));
              sheet.getRow(invoiceFirstRow).getCell(1).setCellStyle(styleVertical);
            }
            rowId = createDailyReportFooter(workbook, sheet, first, rowId, null, false, true);
            if (groupFirstRow < groupLastRow) {
              sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
              sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
            }
            qty = 0.00;
            qtyFree = 0.00;
            salesValue = 0.00;
            billAmount = 0.00;
            qtyCustomer = 0.00;
            qtyFreeCustomer = 0.00;
            salesValueCustomer = 0.00;
            billAmountCustomer = 0.00;
          }
        }
        i++;
      }
      qty = qtyTotal;
      qtyFree = qtyFreeTotal;
      salesValue = salesValueTotal;
      billAmount = billAmountTotal;
      createDailyReportFooter(workbook, sheet, false, rowId, null, true, false);
      ExcelUtil.write("daily_sales_agent_report.xlsx", workbook);
    }
  }

  private static int addDailyReportRows(XSSFWorkbook workbook, CompanyCustomerSales list, XSSFSheet sheet, int rowId) {
    XSSFRow row;
    Cell cell;
    int cellId = 0;
    //  CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    //  CellStyle rightAlign = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    CellStyle styleRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.RIGHT);
    CellStyle styleRedLeft = ExcelUtil.styleRed(workbook, HorizontalAlignment.LEFT);

    cellId = 0;
    row = sheet.createRow(rowId++);

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getCustomerName());

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getInvoiceNo());
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRedLeft);
    }

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getProductName());
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRedLeft);
    }

    cell = row.createCell(cellId++);
    if (list.getQty() != null) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getQty())));
      qty += list.getQty();
    }
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    }

    cell = row.createCell(cellId++);
    if (list.getQtyFree() != null) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getQtyFree())));
      qtyFree += list.getQtyFree();
    }
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    }

    cell = row.createCell(cellId++);
    if (list.getSalesValue() != null) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getSalesValue())));
      salesValue += list.getSalesValue();
    }
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    }

    cell = row.createCell(cellId++);
    if (list.getNetAmount() != null) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getNetAmount())));
      billAmount += (list.getNetAmount());
    }
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    }

    return rowId;
  }

  private static int createDailyReportFooter(XSSFWorkbook workbook, XSSFSheet sheet, boolean first, int rowId, String invoiceNo, boolean color, boolean invoiceExist) {
    XSSFRow row;
    Cell cell;
    CellStyle styleFooter;
    CellStyle styleFooterLeft;
    if (color) {
      styleFooter = ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.RIGHT, workbook);
      styleFooterLeft = ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.LEFT, workbook);
    } else {
      styleFooter = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
      styleFooterLeft = ExcelUtil.styleBold(workbook, HorizontalAlignment.LEFT);
    }
    if (!first) {
      if (color) {
        sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 2));
      } else {
        if (!invoiceExist) {
          sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 1, 2));
        }
      }
      row = sheet.createRow(rowId++);
      if (!invoiceExist) {
        cell = row.createCell(color ? 0 : 1);
        cell.setCellValue(color ? "GRAND TOTAL" : (invoiceNo != null ? invoiceNo + " TOTAL" : "TOTAL"));
        cell.setCellStyle(styleFooterLeft);
      } else {
//        cell = row.createCell(1);
//        cell.setCellValue(" TOTAL");
      }

      cell = row.createCell(3);
      if (!invoiceExist) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(color ? qtyTotal : qty)));
      } else {
//        cell.setCellValue(Double.parseDouble(decimalFormat.format(qtyCustomer)));

        qtyTotal += qtyCustomer;
        qtyCustomer = 0.0;
      }
      qtyCustomer += qty;
      cell.setCellStyle(styleFooter);

      cell = row.createCell(4);
      if (!invoiceExist) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(color ? qtyFreeTotal : qtyFree)));
      } else {
//        cell.setCellValue(Double.parseDouble(decimalFormat.format(qtyFreeCustomer)));

        qtyFreeTotal += qtyFreeCustomer;
        qtyFreeCustomer = 0.0;
      }
      qtyFreeCustomer += qtyFree;
      cell.setCellStyle(styleFooter);

      cell = row.createCell(5);
      if (!invoiceExist) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(color ? salesValueTotal : salesValue)));
      } else {
//        cell.setCellValue(Double.parseDouble(decimalFormat.format(salesValueCustomer)));

        salesValueTotal += salesValueCustomer;
        salesValueCustomer = 0.0;
      }
      salesValueCustomer += salesValue;
      cell.setCellStyle(styleFooter);

      cell = row.createCell(6);
      if (!invoiceExist) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(color ? billAmountTotal : billAmount)));
      } else {
//        cell.setCellValue(Double.parseDouble(decimalFormat.format(billAmountCustomer)));

        billAmountTotal += billAmountCustomer;
        billAmountCustomer = 0.0;
      }
      billAmountCustomer += billAmount;
      cell.setCellStyle(styleFooter);
      invoiceLastRow = rowId - 2;
      if (invoiceExist) {
        groupLastRow = rowId - 2;
        rowId--;
        qty = 0.0;
        qtyFree = 0.0;
        salesValue = 0.0;
        billAmount = 0.0;
      }
    }
    return rowId;
  }

  public static void createCustomerOutstandingAgeWise(MainView main, List<CustomerOutstanding> customerOutstandingList, FilterParameters filterParameters, SalesAgent salesAgent, Territory territory, District district, List<Product> productList) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet("customer_outstanding_age_wise");
      sheet.setDefaultColumnWidth(15);
      String[] arr = null;
      arr = new String[]{"District", "Customer Name", "Agent", "Invoice No", "Invoice Date", "Outstanding Days", "0-30", "31-60", "61-90", "90-120", "Above 120", "Grand Total"};
      XSSFRow row;

      CellStyle styleTop = ExcelUtil.style(workbook, VerticalAlignment.TOP);

      int rowId = 0;
      String title = "";
      if (filterParameters != null && filterParameters.getToDate() != null) {
        title = "Customer Outstanding Age Wise as on " + SystemRuntimeConfig.SDF_DD_MM_YYYY.format(filterParameters.getToDate());
      }
      rowId = ExcelUtil.createDocHeaderForAgeWiseOutstanding(main, sheet, rowId, title, arr.length, filterParameters, salesAgent, territory, district, productList);

      int cellId = 0;
      row = sheet.createRow(rowId++);
      Cell cell = row.createCell(0);
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj.toString());
        cell.setCellStyle(ExcelUtil.setFillColor(ExcelUtil.FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      }
      int listSize = customerOutstandingList.size();
      int i = 1;
      UniqueCheck uDist = new UniqueCheck();
      UniqueCheck uCust = new UniqueCheck();
      boolean first = true;
      ExcelVariables ev = new ExcelVariables();
      ev.resetAll();
      for (CustomerOutstanding list : customerOutstandingList) {
        if (uDist.exist(list.getDistrict())) {
          if (uCust.exist(list.getName())) {
            rowId = addCustomerOutstandingAgeWiseRows(workbook, list, sheet, rowId, ev);

            if (listSize == i) {
              rowId = addCustomerOutstandingAgeWiseFooter(workbook, sheet, ev, first, rowId, list.getName(), false, false);
              ev.addDistrictTotal();
              ev.resetCustomerTotal();
              if (ev.getSecGroupFirstRow() < ev.getSecGroupLastRow()) {
                sheet.addMergedRegion(new CellRangeAddress(ev.getSecGroupFirstRow(), ev.getSecGroupLastRow(), 1, 1));
                sheet.getRow(ev.getSecGroupFirstRow()).getCell(1).setCellStyle(styleTop);
              }
              rowId = addCustomerOutstandingAgeWiseFooter(workbook, sheet, ev, first, rowId, list.getDistrict(), false, true);
              ev.addReportTotal();
              ev.resetDistrictTotal();
              if (ev.getGroupFirstRow() < ev.getGroupLastRow()) {
                sheet.addMergedRegion(new CellRangeAddress(ev.getGroupFirstRow(), ev.getGroupLastRow(), 0, 0));
                sheet.getRow(ev.getGroupFirstRow()).getCell(0).setCellStyle(styleTop);
              }
            }
          } else {
//          If Customer does not exist
            if (i > 1) {
              rowId = addCustomerOutstandingAgeWiseFooter(workbook, sheet, ev, first, rowId, customerOutstandingList.get(i - 2).getName(), false, false);
              ev.addDistrictTotal();
              ev.resetCustomerTotal();
              if (ev.getSecGroupFirstRow() < ev.getSecGroupLastRow()) {
                sheet.addMergedRegion(new CellRangeAddress(ev.getSecGroupFirstRow(), ev.getSecGroupLastRow(), 1, 1));
                sheet.getRow(ev.getSecGroupFirstRow()).getCell(1).setCellStyle(styleTop);
              }
            }
            if (ev.getGroupFirstRow() < ev.getGroupLastRow()) {
              sheet.addMergedRegion(new CellRangeAddress(ev.getGroupFirstRow(), ev.getGroupLastRow(), 0, 0));
              sheet.getRow(ev.getGroupFirstRow()).getCell(0).setCellStyle(styleTop);
            }

            ev.setSecGroupFirstRow(rowId);
            rowId = addCustomerOutstandingAgeWiseRows(workbook, list, sheet, rowId, ev);
            first = false;
            if (listSize == i) {
              rowId = addCustomerOutstandingAgeWiseFooter(workbook, sheet, ev, first, rowId, list.getName(), false, false);
              ev.addDistrictTotal();
              ev.resetCustomerTotal();
              if (ev.getSecGroupFirstRow() < ev.getSecGroupLastRow()) {
                sheet.addMergedRegion(new CellRangeAddress(ev.getSecGroupFirstRow(), ev.getSecGroupLastRow(), 1, 1));
                sheet.getRow(ev.getSecGroupFirstRow()).getCell(1).setCellStyle(styleTop);
              }
              rowId = addCustomerOutstandingAgeWiseFooter(workbook, sheet, ev, first, rowId, list.getDistrict(), false, true);
              ev.addReportTotal();
              ev.resetDistrictTotal();
              if (ev.getGroupFirstRow() < ev.getGroupLastRow()) {
                sheet.addMergedRegion(new CellRangeAddress(ev.getGroupFirstRow(), ev.getGroupLastRow(), 0, 0));
                sheet.getRow(ev.getGroupFirstRow()).getCell(0).setCellStyle(styleTop);
              }
            }
          }
        } else {
//        If District does not exist
          uCust.exist(list.getName());
          if (i > 1) {
            rowId = addCustomerOutstandingAgeWiseFooter(workbook, sheet, ev, first, rowId, customerOutstandingList.get(i - 2).getName(), false, false);
            ev.addDistrictTotal();
            ev.resetCustomerTotal();
            if (ev.getSecGroupFirstRow() < ev.getSecGroupLastRow()) {
              sheet.addMergedRegion(new CellRangeAddress(ev.getSecGroupFirstRow(), ev.getSecGroupLastRow(), 1, 1));
              sheet.getRow(ev.getSecGroupFirstRow()).getCell(1).setCellStyle(styleTop);
            }
            rowId = addCustomerOutstandingAgeWiseFooter(workbook, sheet, ev, first, rowId, customerOutstandingList.get(i - 2).getDistrict(), false, true);
            ev.addReportTotal();
            ev.resetDistrictTotal();
            if (ev.getGroupFirstRow() < ev.getGroupLastRow()) {
              sheet.addMergedRegion(new CellRangeAddress(ev.getGroupFirstRow(), ev.getGroupLastRow(), 0, 0));
              if (sheet.getRow(ev.getGroupFirstRow()) != null && sheet.getRow(ev.getGroupFirstRow()).getCell(0) != null) {
                sheet.getRow(ev.getGroupFirstRow()).getCell(0).setCellStyle(styleTop);
              }
            }
          }
          ev.setSecGroupFirstRow(rowId);
          ev.setGroupFirstRow(rowId);
          rowId = addCustomerOutstandingAgeWiseRows(workbook, list, sheet, rowId, ev);

          first = false;
          if (listSize == i) {
            rowId = addCustomerOutstandingAgeWiseFooter(workbook, sheet, ev, first, rowId, list.getName(), false, false);
            ev.addDistrictTotal();
            ev.resetCustomerTotal();
            if (ev.getSecGroupFirstRow() < ev.getSecGroupLastRow()) {
              sheet.addMergedRegion(new CellRangeAddress(ev.getSecGroupFirstRow(), ev.getSecGroupLastRow(), 1, 1));
              sheet.getRow(ev.getSecGroupFirstRow()).getCell(1).setCellStyle(styleTop);
            }
            rowId = addCustomerOutstandingAgeWiseFooter(workbook, sheet, ev, first, rowId, list.getDistrict(), false, true);
            ev.addReportTotal();
            ev.resetDistrictTotal();
            if (ev.getGroupFirstRow() < ev.getGroupLastRow()) {
              sheet.addMergedRegion(new CellRangeAddress(ev.getGroupFirstRow(), ev.getGroupLastRow(), 0, 0));
              sheet.getRow(ev.getGroupFirstRow()).getCell(0).setCellStyle(styleTop);
            }
          }

        }
        i++;
      }
      addCustomerOutstandingAgeWiseFooter(workbook, sheet, ev, false, rowId, null, true, false);
      ExcelUtil.write("customer_outstanding_age_wise.xlsx", workbook);
    }

  }

  private static int addCustomerOutstandingAgeWiseRows(XSSFWorkbook workbook, CustomerOutstanding list, XSSFSheet sheet, int rowId, ExcelVariables ev) {
    XSSFRow row;
    Cell cell;
    int cellId = 0;
    CellStyle styleRedLeft = ExcelUtil.styleRed(workbook, HorizontalAlignment.LEFT);
    CellStyle styleRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    if (list.getEntityTypeId() == AccountingConstant.ACC_ENTITY_SALES_RETURN.getId()) {
      styleRight = ExcelUtil.styleRed(workbook, HorizontalAlignment.RIGHT);
    }

    cellId = 0;
    row = sheet.createRow(rowId++);

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getDistrict());

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getName());

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getAgentName());

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getInvoiceno());
    if (list.getEntityTypeId() == AccountingConstant.ACC_ENTITY_SALES_RETURN.getId()) {
      cell.setCellStyle(styleRedLeft);
    }

    cell = row.createCell(cellId++);
    if (list.getInvoicedate() != null) {
      cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(list.getInvoicedate()));
    }

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getDaysoutstanding());
    int daysOutstanding = 0;
    if (list.getDaysoutstanding() != null) {
      daysOutstanding = Integer.parseInt(list.getDaysoutstanding());
    }

    cell = row.createCell(cellId++);
    if (daysOutstanding < 31) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getOustandingamount())));
      ev.setAmountUpto30(ev.getAmountUpto30() + (list.getOustandingamount() != null ? list.getOustandingamount() : 0.00));
    }
    cell.setCellStyle(styleRight);

    cell = row.createCell(cellId++);
    if (daysOutstanding > 30 && daysOutstanding < 61) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getOustandingamount())));
      ev.setAmountUpto60(ev.getAmountUpto60() + (list.getOustandingamount() != null ? list.getOustandingamount() : 0.00));
    }
    cell.setCellStyle(styleRight);

    cell = row.createCell(cellId++);
    if (daysOutstanding > 60 && daysOutstanding < 91) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getOustandingamount())));
      ev.setAmountUpto90(ev.getAmountUpto90() + (list.getOustandingamount() != null ? list.getOustandingamount() : 0.00));
    }
    cell.setCellStyle(styleRight);

    cell = row.createCell(cellId++);
    if (daysOutstanding > 90 && daysOutstanding < 121) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getOustandingamount())));
      ev.setAmountUpto120(ev.getAmountUpto120() + (list.getOustandingamount() != null ? list.getOustandingamount() : 0.00));
    }
    cell.setCellStyle(styleRight);

    cell = row.createCell(cellId++);
    if (daysOutstanding > 120) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getOustandingamount())));
      ev.setAmountAbove120(ev.getAmountAbove120() + (list.getOustandingamount() != null ? list.getOustandingamount() : 0.00));
    }
    cell.setCellStyle(styleRight);

    cell = row.createCell(cellId++);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getOustandingamount())));
    ev.setTotal(ev.getTotal() + (list.getOustandingamount() != null ? list.getOustandingamount() : 0.00));
    cell.setCellStyle(styleRight);

    return rowId;
  }

  private static int addCustomerOutstandingAgeWiseFooter(XSSFWorkbook workbook, XSSFSheet sheet, ExcelVariables ev, boolean first, int rowId, String name, boolean color, boolean customerExist) {
    XSSFRow row;
    Cell cell;
    CellStyle styleFooter;
    CellStyle styleFooterLeft;
    if (color) {
      styleFooter = ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.RIGHT, workbook);
      styleFooterLeft = ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.LEFT, workbook);
    } else {
      styleFooter = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
      styleFooterLeft = ExcelUtil.styleBold(workbook, HorizontalAlignment.LEFT);
    }
    if (!first) {
      if (color) {
        sheet.addMergedRegion(new CellRangeAddress(rowId + 1, rowId + 1, 0, 5));
      } else {
        if (!customerExist) {
          sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 1, 2));
        } else {
          sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 2));
        }
      }
      row = sheet.createRow(rowId++);
      if (!customerExist) {
        cell = row.createCell(color ? 0 : 1);
        cell.setCellValue(color ? "GRAND TOTAL" : (name != null ? name + " TOTAL" : "TOTAL"));
      } else {
        cell = row.createCell(0);
        cell.setCellValue(name != null ? name + " TOTAL" : "");
      }
      cell.setCellStyle(styleFooterLeft);

      cell = row.createCell(6);
      if (!customerExist) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(color ? ev.getReportAmountUpto30() : ev.getAmountUpto30())));
      } else {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getDistrictAmountUpto30())));
      }
      cell.setCellStyle(styleFooter);

      cell = row.createCell(7);
      if (!customerExist) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(color ? ev.getReportAmountUpto60() : ev.getAmountUpto60())));
      } else {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getDistrictAmountUpto60())));
      }
      cell.setCellStyle(styleFooter);

      cell = row.createCell(8);
      if (!customerExist) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(color ? ev.getReportAmountUpto90() : ev.getAmountUpto90())));
      } else {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getDistrictAmountUpto90())));
      }
      cell.setCellStyle(styleFooter);

      cell = row.createCell(9);
      if (!customerExist) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(color ? ev.getReportAmountUpto120() : ev.getAmountUpto120())));
      } else {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getDistrictAmountUpto120())));
      }
      cell.setCellStyle(styleFooter);

      cell = row.createCell(10);
      if (!customerExist) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(color ? ev.getReportAmountAbove120() : ev.getAmountAbove120())));
      } else {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getDistrictAmountAbove120())));
      }
      cell.setCellStyle(styleFooter);

      cell = row.createCell(11);
      if (!customerExist) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(color ? ev.getReportTotal() : ev.getTotal())));
      } else {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getDistrictTotal())));
      }
      cell.setCellStyle(styleFooter);

      ev.setSecGroupLastRow(rowId - 2);
      if (customerExist) {
        ev.setGroupLastRow(rowId - 2);
        ev.resetCustomerTotal();
      }
    }
    return rowId;
  }

  public static void createCustomerExport(MainView main, List<CustomerImporter> customerImporterList, District district, boolean formatOnly) throws IOException {
    //setWorkbook(workbook);
    String name = "customer_export";
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      String[] arr = new String[]{"Customer Name", "Country", "State", "District", "Territory", "City/Town", "Pan No", "Gst No", "Address", "pin", "Phone 1", "Phone 2", "Phone 3", "Fax 1", "Email", "customer_type(Retailer/Stockist/Distributor/Consumer/Individual)", "Tax Zone(Taxable/Sez/Tax free)"};
      XSSFRow row;

      CellStyle style = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);
      CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      int rowId = 0;
      if (district != null) {
        String title = "District - " + district.getDistrictName();
        rowId = ExcelUtil.createDocHeader(main, sheet, rowId, title, 3, null, null, null);
      }
      int cellId = 0;
      row = sheet.createRow(rowId++);
      int size = 0;
      Cell cell = row.createCell(0);
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        cell.setCellStyle(style);
        sheet.autoSizeColumn(size);
        size++;
      }
      if (!formatOnly && customerImporterList != null) {
        for (CustomerImporter list : customerImporterList) {
          cellId = 0;
          row = sheet.createRow(rowId++);
          row.setRowStyle(styleNormal);

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getCustomerName());
          cell = row.createCell(cellId++);
          if (list.getCountryId() != null) {
            cell.setCellValue(list.getCountryId().getCountryName());
          }
          cell = row.createCell(cellId++);
          if (list.getStateId() != null) {
            cell.setCellValue(list.getStateId().getStateName());
          }
          cell = row.createCell(cellId++);
          if (list.getDistrictId() != null) {
            cell.setCellValue(list.getDistrictId().getDistrictName());
          }
          cell = row.createCell(cellId++);
          if (list.getTerritoryId() != null) {
            cell.setCellValue(list.getTerritoryId().getTerritoryName());
          }
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getCitOrTown());
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getPanNo());
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getGstNo());
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getAddress());
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getPin());
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getPhone1());
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getPhone2());
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getPhone3());
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getFax1());
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getEmail());
          cell = row.createCell(cellId++);
          if (list.getCustomerType() != null) {
            cell.setCellValue(list.getCustomerType().getTitle());
          }
          cell = row.createCell(cellId++);
          if (list.getTaxableZone() != null) {
            if (list.getTaxableZone().intValue() == 1) {
              cell.setCellValue("Taxable");
            }
            if (list.getTaxableZone().intValue() == 2) {
              cell.setCellValue("Sez");
            }
            if (list.getTaxableZone().intValue() == 3) {
              cell.setCellValue("Tax Free");
            }
          } else {
            cell.setCellValue("Taxable");
          }
        }
      }
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  public static void createPlatform(MainView main, List<PlatformSummary> platformSummaryList, Company company, Account account, ProductEntry productEntry) throws IOException {

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet("platform");
      sheet.setDefaultColumnWidth(20);
      String[] arr = new String[]{"Platform Description", "Entry Date", "Account", "Customer Name", "Purchase Invoice", "Sales Invoice", "Sales Return Invoice",
        "Purchase Return Invoice", "Mrp Adjustment", "Product Name", "Credit Amount", "Debit Amount"};
      XSSFRow row;

      CellStyle style = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);
      CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      CellStyle rightAlign = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      CellStyle rightBold = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
//    date
      int rowId = 0;
      rowId = ExcelUtil.createHeader(workbook, sheet, company, null, account, null);
      int cellId = 0;
      row = sheet.createRow(rowId++);
      int size = 0;
      Cell cell = row.createCell(0);
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        size++;
      }

      Double totalCredit = 0.0, totalDebit = 0.0;
      if (platformSummaryList != null && !platformSummaryList.isEmpty()) {
        for (PlatformSummary platform : platformSummaryList) {
          cellId = 0;
          row = sheet.createRow(rowId++);
          row.setRowStyle(styleNormal);

          cell = row.createCell(cellId++);
          if (platform.getPlatformDesc() != null) {
            cell.setCellValue(platform.getPlatformDesc());
          }
          cell = row.createCell(cellId++);
          if (platform.getEntryDate() != null) {
            cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(platform.getEntryDate()));
          }
          cell = row.createCell(cellId++);
          if (platform.getAccountTitle() != null) {
            cell.setCellValue(platform.getAccountTitle());
          }
          cell = row.createCell(cellId++);
          if (platform.getCustomerName() != null) {
            cell.setCellValue(platform.getCustomerName());
          }
          cell = row.createCell(cellId++);
          if (platform.getPurchaseInvoice() != null) {
            cell.setCellValue(platform.getPurchaseInvoice());
          }
          cell = row.createCell(cellId++);
          if (platform.getSalesInvoice() != null) {
            cell.setCellValue(platform.getSalesInvoice());
          }
          cell = row.createCell(cellId++);
          if (platform.getSalesReturnInvoice() != null) {
            cell.setCellValue(platform.getSalesReturnInvoice());
          }
          cell = row.createCell(cellId++);
          if (platform.getPurchaseReturnInvoice() != null) {
            cell.setCellValue(platform.getPurchaseReturnInvoice());
          }
          cell = row.createCell(cellId++);
          if (platform.getMrpAdjInvoice() != null) {
            cell.setCellValue(platform.getMrpAdjInvoice());
          }
          cell = row.createCell(cellId++);
          if (platform.getProductName() != null) {
            cell.setCellValue(platform.getProductName());
          }
          cell = row.createCell(cellId++);
          if (platform.getCreditAmountRequired() != null) {
            cell.setCellValue(Double.parseDouble(decimalFormat.format(platform.getCreditAmountRequired())));
            cell.setCellStyle(rightAlign);
            totalCredit += platform.getCreditAmountRequired();
          }
          cell = row.createCell(cellId++);
          if (platform.getDebitAmountRequired() != null) {
            cell.setCellValue(Double.parseDouble(decimalFormat.format(platform.getDebitAmountRequired())));
            cell.setCellStyle(rightAlign);
            totalDebit += platform.getDebitAmountRequired();
          }
        }
      }
      row = sheet.createRow(rowId++);
      cell = row.createCell(0);
      cell.setCellValue("Total");
      cell.setCellStyle(style);
      sheet.addMergedRegion(new CellRangeAddress(rowId - 1, rowId - 1, 0, 9));
      cell = row.createCell(10);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(totalCredit)));
      cell.setCellStyle(rightBold);
      cell = row.createCell(11);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(totalDebit)));
      cell.setCellStyle(rightBold);
      ExcelUtil.write("platform" + ".xlsx", workbook);
    }
  }

  public static void createTradingDifferenceExcel(MainView main, List<PlatformSummary> tradingDifferenceList, List<CompanyCustomerSales> debitCreditList, Company company, Account account) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      String fileName = "";
      if (account != null) {
        fileName = account.getAccountCode() + "_";
      }
      fileName = "trading_difference";
      XSSFSheet tradingDifference = workbook.createSheet("trading_difference");
      createTradingDifferenceSheet(tradingDifference, tradingDifferenceList, workbook, company, account);
      XSSFSheet debitCredit = workbook.createSheet("debit_credit_notes");
      createDebitCreditList(debitCredit, debitCreditList, workbook, company, account);
      ExcelUtil.write(fileName + ".xlsx", workbook);
    }

  }

  private static void createTradingDifferenceSheet(XSSFSheet sheet, List<PlatformSummary> tradingDifferenceList, XSSFWorkbook workbook, Company company, Account account) {
    //setWorkbook(workbook);

    sheet.setDefaultColumnWidth(25);

    String[] arr = new String[]{"Purchase Invoice No", "Trade Event", "Invoice No", "Invoice Date", "Customer Name", "Entry Date", "Parent Invoice No", "Product Name",
      "Batch", "Tax %", "Landing Price", "Selling Price", "Sold Price", "Margin", "Margin Diff. Per Unit", "Total Qty", "Total Margin Diff."};
    XSSFRow row;

    CellStyle style = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);
    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    CellStyle rightAlign = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    CellStyle rightBold = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
    int rowId = 0;
    rowId = ExcelUtil.createHeader(workbook, sheet, company, null, account, null);
    int cellId = 0;
    row = sheet.createRow(rowId++);
    int size = 0;
    Cell cell = row.createCell(0);
    for (String obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj);
      cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      size++;
    }

    Double totalQty = 0.0, totalMarginDiff = 0.0;
    if (tradingDifferenceList != null && !tradingDifferenceList.isEmpty()) {
      for (PlatformSummary tradeDiff : tradingDifferenceList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);

        cell = row.createCell(cellId++);
        if (tradeDiff.getAccountInvoiceNo() != null) {
          cell.setCellValue(tradeDiff.getAccountInvoiceNo());
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getTradeEvent() != null) {
          cell.setCellValue(tradeDiff.getTradeEvent());
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getInvoiceNo() != null) {
          cell.setCellValue(tradeDiff.getInvoiceNo());
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getInvoiceDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(tradeDiff.getInvoiceDate()));
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getCustomerName() != null) {
          cell.setCellValue(tradeDiff.getCustomerName());
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getEntryDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(tradeDiff.getEntryDate()));
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getParentInvoiceNo() != null) {
          cell.setCellValue(tradeDiff.getParentInvoiceNo());
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getProductName() != null) {
          cell.setCellValue(tradeDiff.getProductName());
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getBatchNo() != null) {
          cell.setCellValue(tradeDiff.getBatchNo());
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getTaxRate() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(tradeDiff.getTaxRate())));
          cell.setCellStyle(rightAlign);
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getLandingPrice() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(tradeDiff.getLandingPrice())));
          cell.setCellStyle(rightAlign);
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getSellingPrice() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(tradeDiff.getSellingPrice())));
          cell.setCellStyle(rightAlign);
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getSoldPrice() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(tradeDiff.getSoldPrice())));
          cell.setCellStyle(rightAlign);
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getMargin() != null) {
          cell.setCellValue(tradeDiff.getMargin());
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getMarginDifferencePerUnit() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(tradeDiff.getMarginDifferencePerUnit())));
          cell.setCellStyle(rightAlign);
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getTotalQty() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(tradeDiff.getTotalQty())));
          cell.setCellStyle(rightAlign);
          totalQty += tradeDiff.getTotalQty();
        }
        cell = row.createCell(cellId++);
        if (tradeDiff.getTotalMarginDifference() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(tradeDiff.getTotalMarginDifference())));
          cell.setCellStyle(rightAlign);
          totalMarginDiff += tradeDiff.getTotalMarginDifference();
        }
      }
    }
    row = sheet.createRow(rowId++);
    cell = row.createCell(0);
    cell.setCellValue("Total");
    cell.setCellStyle(style);
    sheet.addMergedRegion(new CellRangeAddress(rowId - 1, rowId - 1, 0, 13));
    cell = row.createCell(15);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(totalQty)));
    cell.setCellStyle(rightBold);
    cell = row.createCell(16);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(totalMarginDiff)));
    cell.setCellStyle(rightBold);
  }

  private static void createDebitCreditList(XSSFSheet sheet, List<CompanyCustomerSales> debitCreditList, XSSFWorkbook workbook, Company company, Account account) {
    //setWorkbook(workbook);
    sheet.setDefaultColumnWidth(25);
    String[] arr = new String[]{"Type", "Document No", "Invoice Date", "Customer Name", "Entry Date", "Title", "HSN/SAC Code", "Ref Invoice No", "Ref Invoice Date",
      "Net Amount"};
    XSSFRow row;

    CellStyle style = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);
    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    CellStyle rightAlign = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    CellStyle rightBold = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
    int rowId = 0;
    rowId = ExcelUtil.createHeader(workbook, sheet, company, null, account, null);
    int cellId = 0;
    row = sheet.createRow(rowId++);
    int size = 0;
    Cell cell = row.createCell(0);
    for (String obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj);
      cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      size++;
    }

    Double netAmount = 0.0;
    if (debitCreditList != null && !debitCreditList.isEmpty()) {
      for (CompanyCustomerSales debitCredit : debitCreditList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);
        cell = row.createCell(cellId++);
        if (debitCredit.getInvoiceType() != null) {
          cell.setCellValue(debitCredit.getInvoiceType());
        }
        cell = row.createCell(cellId++);
        if (debitCredit.getDocumentNo() != null) {
          cell.setCellValue(debitCredit.getDocumentNo());
        }
        cell = row.createCell(cellId++);
        if (debitCredit.getInvoiceDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(debitCredit.getInvoiceDate()));
        }
        cell = row.createCell(cellId++);
        if (debitCredit.getCustomerName() != null) {
          cell.setCellValue(debitCredit.getCustomerName());
        }
        cell = row.createCell(cellId++);
        if (debitCredit.getEntryDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(debitCredit.getEntryDate()));
        }
        cell = row.createCell(cellId++);
        if (debitCredit.getProductName() != null) {
          cell.setCellValue(debitCredit.getProductName());
        }
        cell = row.createCell(cellId++);
        if (debitCredit.getHsnCode() != null) {
          cell.setCellValue(debitCredit.getHsnCode());
        }
        cell = row.createCell(cellId++);
        if (debitCredit.getRefInvoiceNo() != null) {
          cell.setCellValue(debitCredit.getRefInvoiceNo());
        }
        cell = row.createCell(cellId++);
        if (debitCredit.getRefInvoiceDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(debitCredit.getRefInvoiceDate()));
        }
        cell = row.createCell(cellId++);
        if (debitCredit.getNetAmount() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(debitCredit.getNetAmount())));
          cell.setCellStyle(rightAlign);
          netAmount += debitCredit.getNetAmount();
        }
      }
    }
  }

  public static void createCreditNoteReport(MainView main, List<CreditNoteReport> creditNoteSalesReportList, List<CreditNoteReport> creditNoteReturnReportList, FilterParameters filterParameters) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      String fileName = "rate_difference_cn";
      XSSFSheet salesSheet = workbook.createSheet("sales");
      createCreditNoteReport(salesSheet, creditNoteSalesReportList, workbook, filterParameters);
      XSSFSheet returnsheet = workbook.createSheet("sales_return");
      createCreditNoteReport(returnsheet, creditNoteReturnReportList, workbook, filterParameters);

      ExcelUtil.write(fileName + ".xlsx", workbook);
    }

  }

  private static void createCreditNoteReport(XSSFSheet sheet, List<CreditNoteReport> creditNoteReportList, XSSFWorkbook workbook, FilterParameters filterParameters) {

    sheet.setDefaultColumnWidth(15);
    String type = "";
    if (filterParameters != null) {
      if (filterParameters.getFilterOption() == 0) {
        type = "Landing Rate";
      } else if (filterParameters.getFilterOption() == 1) {
        type = "Purchase Rate";
      } else if (filterParameters.getFilterOption() == 2) {
        type = "PTS";
      }
    }
    String[] arr = new String[]{"SD Name", "Invoice No", "GST %", "Product Name", "Quantity", "Free Quantity", "Rate", "Gross Value", "Disc %", "Disc. Amount", "Net Value",
      "Commission %", type, "Difference", "Net Difference", "Comm. Deduction", "Net Comm. Deduction", "CN Amount", "Amount for free", "Comm. Deduction", "CN Amount(free)", "Net CN Amount", "GST on Diff", "Total CN Value"};
    XSSFRow row;

    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    CellStyle rightAlign = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    int rowId = 0;
    rowId = ExcelUtil.createHeader(workbook, sheet, null, filterParameters.getAccountGroup(), filterParameters.getAccount(), null);

    int cellId = 0;
    row = sheet.createRow(rowId++);
    int size = 0;
    Cell cell = row.createCell(0);
    for (String obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj);
      sheet.autoSizeColumn(size);
      cell.setCellStyle(ExcelUtil.setFillColor(ExcelUtil.FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      size++;
    }

    if (creditNoteReportList != null && !creditNoteReportList.isEmpty()) {
      ListIterator<CreditNoteReport> list = creditNoteReportList.listIterator();
      while (list.hasNext()) {
        CreditNoteReport report = list.next();
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);
        cell = row.createCell(cellId++);
        cell.setCellValue(report.getCustomerName());
        cell.setCellStyle(styleNormal);
        cell = row.createCell(cellId++);
        cell.setCellValue(report.getInvoiceNo());
        cell.setCellStyle(styleNormal);
        cell = row.createCell(cellId++);
        cell.setCellValue(report.getRatePercentage());
        cell.setCellStyle(styleNormal);
        cell = row.createCell(cellId++);
        cell.setCellValue(report.getProductName());
        cell.setCellStyle(styleNormal);
        cell = row.createCell(cellId++);
        cell.setCellValue(report.getQty());
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        cell.setCellValue(report.getQtyFree());
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        if (report.getRate() != null) {
          cell.setCellValue(doublePrecision(report.getRate()));
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        if (report.getGrossValue() != null) {
          cell.setCellValue(doublePrecision(report.getGrossValue()));
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        if (report.getDiscAmount() != null && report.getGrossValue() != null && report.getGrossValue() != 0) {
          cell.setCellValue(doublePrecision(report.getDiscAmount() * 100 / report.getGrossValue()));
        } else {
          cell.setCellValue(0);
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        if (report.getDiscAmount() != null) {
          cell.setCellValue(doublePrecision(report.getDiscAmount()));
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        if (report.getNetValue() != null) {
          cell.setCellValue(doublePrecision(report.getNetValue()));
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        if (report.getMarginPercentage() != null) {
          cell.setCellValue(doublePrecision(report.getMarginPercentage()));
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        if (report.getBillingSd() != null) {
          cell.setCellValue(doublePrecision(report.getBillingSd()));
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        Double diff = 0.0;
        if (report.getBillingSd() != null && report.getNetValue() != null && report.getQty() != null && report.getQty() != 0) {
          diff = report.getBillingSd() - (report.getNetValue() / report.getQty());
          cell.setCellValue(doublePrecision(diff));
        } else {
          cell.setCellValue(0);
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        cell.setCellValue(doublePrecision(diff * report.getQty()));
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        Double k = 0.0;
        if (report.getMarginPercentage() != null) {
          k = diff * report.getMarginPercentage() / 100;
          cell.setCellValue(doublePrecision(k));
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        cell.setCellValue(doublePrecision(k * report.getQty()));
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        Double l = (diff - k) * report.getQty();
        cell.setCellValue(doublePrecision(l));
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        Double m = 0.0;
        if (report.getQtyFree() != null && report.getRate() != null) {
          m = report.getQtyFree() * report.getBillingSd();
          cell.setCellValue(doublePrecision(m));
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        Double n = 0.0;
        if (report.getQtyFree() != null && report.getRate() != null && report.getMarginPercentage() != null) {
          n = report.getQtyFree() * report.getBillingSd() * report.getMarginPercentage() / 100;
          cell.setCellValue(doublePrecision(n));
        }
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        Double o = m - n;
        cell.setCellValue(doublePrecision(o));
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        Double p = l + o;
        cell.setCellValue(doublePrecision(p));
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        Double q = (p * report.getRatePercentage() / 100);
        cell.setCellValue(doublePrecision(q));
        cell.setCellStyle(rightAlign);
        cell = row.createCell(cellId++);
        cell.setCellValue(doublePrecision(p + q));
        cell.setCellStyle(rightAlign);
      }
    }
  }

  public static void createCustomerWiseTcsReport(List<CompanyCustomerSales> tcsReportList, Company company, FilterParameters filterParameters) throws IOException {
    String name = "tcs_report";
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      XSSFRow row;
      Cell cell;
      CellStyle styleVertical = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);

      int rowId = 0;
      int cellId = 0, size = 0;
      int i = 1;
      int listSize = tcsReportList.size();
      row = sheet.createRow(rowId++);
      rowId = ExcelUtil.createHeader(workbook, sheet, company, filterParameters.getAccountGroup(), null, null);
      row = sheet.createRow(rowId++);
      cell = row.createCell(0);
      cellId = 0;
      String[] arr = new String[]{"CUSTOMER NAME", "INVOICE NO", "INVOICE DATE", "GST ", "PAN", "BILL AMOUNT WITH TCS", "BILL AMOUNT WITHOUT TCS", "TCS AMOUNT"};
      row = sheet.createRow(rowId++);

      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj.toString());
        cell.setCellStyle(ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size);
        size++;
      }
      UniqueCheck uaccnt = new UniqueCheck();
      boolean first = true;
      groupFirstRow = 0;
      groupLastRow = 0;
      for (CompanyCustomerSales list : tcsReportList) {
        if (uaccnt.exist(list.getCustomerName())) {
          rowId = addTcsReportRows(workbook, list, sheet, rowId);

          if (listSize == i) {
            rowId = createTcsReportFooter(workbook, sheet, first, rowId, false, tcsReportList.get(i - 2).getCustomerName());
            if (groupFirstRow < groupLastRow) {
              sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
              sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
            }
            billAmount = 0.00;
            billAmountWithouTcs = 0.00;
            tcsAmount = 0.00;
          }
        } else {
          if (i != 1) {
            rowId = createTcsReportFooter(workbook, sheet, first, rowId, false, tcsReportList.get(i - 2).getCustomerName());
          } else {
            rowId = createTcsReportFooter(workbook, sheet, first, rowId, false, null);
          }
          if (groupFirstRow < groupLastRow) {
            sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
            sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
          }
          billAmount = 0.00;
          billAmountWithouTcs = 0.00;
          tcsAmount = 0.00;
          groupFirstRow = rowId;
          rowId = addTcsReportRows(workbook, list, sheet, rowId);
          first = false;
          if (listSize == i) {
            if (i != 1) {
              rowId = createTcsReportFooter(workbook, sheet, first, rowId, false, tcsReportList.get(i - 2).getCustomerName());
            } else {
              rowId = createTcsReportFooter(workbook, sheet, first, rowId, false, null);
            }
            if (groupFirstRow < groupLastRow) {
              sheet.addMergedRegion(new CellRangeAddress(groupFirstRow, groupLastRow, 0, 0));
              sheet.getRow(groupFirstRow).getCell(0).setCellStyle(styleVertical);
            }
            billAmount = 0.00;
            billAmountWithouTcs = 0.00;
            tcsAmount = 0.00;
          }
        }
        i++;
      }
      billAmount = billAmountTotal;
      billAmountWithouTcs = billAmountWithouTcsTotal;
      tcsAmount = tcsAmountTotal;
      createTcsReportFooter(workbook, sheet, false, rowId, true, null);
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  private static int addTcsReportRows(XSSFWorkbook workbook, CompanyCustomerSales list, XSSFSheet sheet, int rowId) {
    XSSFRow row;
    Cell cell;
    int cellId = 0;
    CellStyle styleRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.RIGHT);

    cellId = 0;
    row = sheet.createRow(rowId++);

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getCustomerName());

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getInvoiceNo());

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getEntryDate());

    cell = row.createCell(cellId++);
    if (list.getGstNo() != null) {
      cell.setCellValue(list.getGstNo());
    }

    cell = row.createCell(cellId++);
    if (list.getPanNo() != null) {
      cell.setCellValue(list.getPanNo());
    }

    cell = row.createCell(cellId++);
    if (list.getInvoiceAmount() != null && list.getInvoiceAmount() != 0) {
      cell.setCellValue(list.getInvoiceAmount());
    }
    billAmount += list.getInvoiceAmount();

    cell = row.createCell(cellId++);
    if (list.getInvoiceAmountSubtotal() != null && list.getInvoiceAmountSubtotal() != 0) {
      cell.setCellValue(list.getInvoiceAmountSubtotal());
    }
    billAmountWithouTcs += list.getInvoiceAmountSubtotal();

    cell = row.createCell(cellId++);
    if (list.getTcsNetAmount() != null && list.getTcsNetAmount() != 0) {
      cell.setCellValue(list.getTcsNetAmount());
    }
    tcsAmount += list.getTcsNetAmount();

    return rowId;
  }

  private static int createTcsReportFooter(XSSFWorkbook workbook, XSSFSheet sheet, boolean first, int rowId, boolean color, String customerName) {
    XSSFRow row;
    Cell cell;
    CellStyle styleFooter = null;
    if (color) {
      styleFooter = ExcelUtil.setFillColorwithBorder(FILL_ORANGE, HorizontalAlignment.RIGHT, workbook);
    } else {
      styleFooter = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
    }
    if (!first) {
      sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 4));
      row = sheet.createRow(rowId++);
      cell = row.createCell(0);
      if (customerName != null) {
        cell.setCellValue(customerName.toUpperCase() + " TOTAL");
      } else {
        cell.setCellValue("TOTAL");
      }
      cell.setCellStyle(styleFooter);

      cell = row.createCell(5);
      cell.setCellValue(billAmount);
      billAmountTotal += billAmount;
      cell.setCellStyle(styleFooter);

      cell = row.createCell(6);
      cell.setCellValue(billAmountWithouTcs);
      billAmountWithouTcsTotal += billAmountWithouTcs;
      cell.setCellStyle(styleFooter);

      cell = row.createCell(7);
      cell.setCellValue(tcsAmount);
      tcsAmountTotal += tcsAmount;
      cell.setCellStyle(styleFooter);
    }
    return rowId;
  }
}
