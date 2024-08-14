/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spica.reports.model.DocumentSummary;
import spica.reports.model.FilterParameters;
import spica.reports.model.Gst1Report;
import spica.reports.model.Gst3bReport;
import spica.reports.model.GstB2csReport;
import spica.reports.model.GstCdnReport;
import spica.reports.model.GstHsnReport;
import static spica.scm.export.ExcelUtil.FILL_ORANGE;
import spica.scm.util.MathUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.entity.util.UniqueCheck;
import static spica.scm.export.ExcelUtil.decimalFormat;
import wawo.app.faces.MainView;

/**
 *
 * @author godson
 */
public class ExcelGstSheet {

  public static void createGst1Report(MainView main, FilterParameters filterParameters, List<Gst1Report> b2bReportList, List<GstCdnReport> cdnReportList, List<GstHsnReport> hsnList, List<GstB2csReport> b2csReportList, List<DocumentSummary> documentSummaryList, boolean exportAll, String type) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      String fileName = "";
      if (filterParameters != null && filterParameters.getAccountGroup() != null) {
        fileName = filterParameters.getAccountGroup().getGroupName() + "_";
      }
      if (exportAll) {
        fileName = "gst1_report";
        XSSFSheet b2bsheet = workbook.createSheet("b2b_report");
        createGstB2bReport(b2bsheet, b2bReportList, workbook, filterParameters);
        XSSFSheet cdnrCsheet = workbook.createSheet("cdnr_report");
        createGstCdnReport(cdnrCsheet, cdnReportList, workbook, SystemConstants.CDNR, filterParameters);
        XSSFSheet hsnsheet = workbook.createSheet("hsn_report");
        createHsnReport(hsnsheet, hsnList, workbook, filterParameters);
        XSSFSheet b2cssheet = workbook.createSheet("b2cs_report");
        createGstB2csReport(b2cssheet, b2csReportList, workbook, filterParameters);
        XSSFSheet docSummarySheet = workbook.createSheet("document_summary");
        createDocumentSummaryReport(docSummarySheet, documentSummaryList, workbook, filterParameters);

      } else {
        if (type.equals(SystemConstants.B2B)) {
          fileName = "b2b_report";
          XSSFSheet sheet = workbook.createSheet(fileName + "b2b_report");
          createGstB2bReport(sheet, b2bReportList, workbook, filterParameters);
        } else if (type.equals(SystemConstants.CDNR)) {
          fileName = "cdnr_report";
          XSSFSheet sheet = workbook.createSheet("cdnr_report");
          createGstCdnReport(sheet, cdnReportList, workbook, SystemConstants.CDNR, filterParameters);
        } else if (type.equals(SystemConstants.HSN)) {
          fileName += "hsn_report";
          XSSFSheet sheet = workbook.createSheet("hsn_report");
          createHsnReport(sheet, hsnList, workbook, filterParameters);
        } else if (type.equals(SystemConstants.B2CS)) {
          fileName = "b2cs_report";
          XSSFSheet sheet = workbook.createSheet("b2cs_report");
          createGstB2csReport(sheet, b2csReportList, workbook, filterParameters);
        } else if (type.equals(SystemConstants.DOC_SUMMARY)) {
          fileName = "document_summary";
          XSSFSheet sheet = workbook.createSheet("document_summary");
          createDocumentSummaryReport(sheet, documentSummaryList, workbook, filterParameters);
        }
      }
      ExcelUtil.write(fileName + ".xlsx", workbook);
    }

  }

  private static void createGstB2bReport(XSSFSheet sheet, List<Gst1Report> companyGstReportList, XSSFWorkbook workbook, FilterParameters filterParameters) {

    sheet.setDefaultColumnWidth(15);
    XSSFRow row;

    // Create cell style 
    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    CellStyle styleNormalCenter = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);

    int recipient = 0, invoices = 0;
    double taxablevalue = 0.00, invoicevalue = 0.00, kfCessTotal = 0.00, taxvalue = 0.0, serviceTaxValue = 0.00, serviceTaxableValue = 0.00;

    try (UniqueCheck inv = new UniqueCheck()) {
      try (UniqueCheck cust = new UniqueCheck()) {
        if (companyGstReportList != null && !companyGstReportList.isEmpty()) {
          for (Gst1Report gstb2bReport : companyGstReportList) {
            if (!inv.exist(gstb2bReport.getInvoiceNo())) {
              invoices++;
            }
            if (!cust.exist(gstb2bReport.getCustomerName())) {
              recipient++;
            }
            invoicevalue += gstb2bReport.getInvoiceAmount() != null ? gstb2bReport.getInvoiceAmount() : 0;
            taxablevalue += gstb2bReport.getTaxableValue() != null ? gstb2bReport.getTaxableValue() : 0;
            taxvalue += gstb2bReport.getTaxAmount() != null ? gstb2bReport.getTaxAmount() : 0;
            serviceTaxableValue += gstb2bReport.getServiceTaxableValue() != null ? gstb2bReport.getServiceTaxableValue() : 0;
            serviceTaxValue += gstb2bReport.getServiceTaxAmount() != null ? gstb2bReport.getServiceTaxAmount() : 0;
            if (gstb2bReport.getKfCessValue() != null) {
              kfCessTotal += gstb2bReport.getKfCessValue();
            }
          }
        }
      }
    }
    int cellId = 0;
    int rowId = 0;
    if (filterParameters != null) {
      rowId = ExcelUtil.createDateHeader(sheet, rowId, 2,
              (filterParameters.getFromDate() != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getFromDate()) : null),
              (filterParameters.getToDate()) != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getToDate()) : null);
    }
    row = sheet.createRow(rowId++);
    row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    Cell cell;
//    Total

    cell = row.createCell(0);
    cell.setCellValue("No. of Recipients");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(2);
    cell.setCellValue("No. of Invoices");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(5);
    cell.setCellValue("Total Invoice Value");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(12);
    cell.setCellValue("Total Taxable Value");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(13);
    cell.setCellValue("Total Cess");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(14);
    cell.setCellValue("Total Tax Amount");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(15);
    cell.setCellValue("Total Service Taxable Value");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(16);
    cell.setCellValue("Total Service Tax Amount");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cellId = 0;
    row = sheet.createRow(rowId++);

    cell = row.createCell(0);
    cell.setCellValue(recipient);
    cell.setCellStyle(styleNormalCenter);

    cell = row.createCell(2);
    cell.setCellValue(invoices);
    cell.setCellStyle(styleNormalCenter);

    cell = row.createCell(5);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(invoicevalue)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(12);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(taxablevalue)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(13);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(kfCessTotal)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(14);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(taxvalue)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(15);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceTaxableValue)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(16);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceTaxValue)));
    cell.setCellStyle(styleNormalRight);

//     List
    Object[] arr = new Object[]{"GSTIN/UIN of Recipient", "Receiver Name", "Invoice Number", "Invoice Date", "Bill Amount", "Invoice Value (Based on Tax)", "Place Of Supply", "Reverse Charge",
      "Applicable % of Tax Rate", "Invoice Type", "E-Commerce GSTIN", "Rate", "Taxable Value", "Cess Amount", "Tax Amount", "Service Taxable Value", "Service Tax Amount"};
    row = sheet.createRow(rowId++);
    row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
    int size = 0;
    cellId = 0;

    for (Object obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj.toString());
      cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      sheet.autoSizeColumn(size);
      size++;
    }
    if (companyGstReportList != null && !companyGstReportList.isEmpty()) {
      for (Gst1Report list : companyGstReportList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getGstNo());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getCustomerName());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getInvoiceNo());

        cell = row.createCell(cellId++);
        if (list.getEntryDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(list.getEntryDate()));
        }

        cell = row.createCell(cellId++);
        if (list.getBillAmount() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getBillAmount())));
        }
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        if (list.getInvoiceAmount() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getInvoiceAmount())));
        }
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getStateCode() + "-" + list.getStateName());

        cell = row.createCell(cellId++);
        cell.setCellValue("N");

        cell = row.createCell(cellId++);
        cell.setCellValue("");

        cell = row.createCell(cellId++);
        cell.setCellValue("Regular");

        cell = row.createCell(cellId++);
        cell.setCellValue("");

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getRatePercentage());
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        if (list.getTaxableValue() != null && list.getTaxableValue() > 0) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTaxableValue())));
        }
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        if (list.getKfCessValue() != null && list.getKfCessValue() > 0) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getKfCessValue())));
        }
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        if (list.getTaxAmount() != null && list.getTaxAmount() > 0) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTaxAmount())));
        }
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        if (list.getServiceTaxableValue() != null && list.getServiceTaxableValue() != 0) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getServiceTaxableValue())));
        }
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        if (list.getServiceTaxAmount() != null && list.getServiceTaxAmount() != 0) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getServiceTaxAmount())));
        }
        cell.setCellStyle(styleNormalRight);
      }
    }
  }

  private static void createGstCdnReport(XSSFSheet sheet, List<GstCdnReport> gstCdnReportList, XSSFWorkbook workbook, String type, FilterParameters filterParameters) {
    //  setWorkbook(workbook);
    sheet.setDefaultColumnWidth(15);
    XSSFRow row;
    // Create cell style 
    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    CellStyle styleNormalCenter = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);
    if (type.equals(SystemConstants.CDNR)) {
      gstCdnReportList = createGstCdnReportList(gstCdnReportList);
    }
    int recipient = 0, invoices = 0, refunds = 0;
    double taxablevalue = 0.00, refundvalue = 0.00;
    UniqueCheck inv = new UniqueCheck();
    try (UniqueCheck refund = new UniqueCheck()) {
      try (UniqueCheck cust = new UniqueCheck()) {
        if (gstCdnReportList != null && !gstCdnReportList.isEmpty()) {
          for (GstCdnReport gstCdnReport : gstCdnReportList) {
            if (gstCdnReport.getRefundNumber() != null) {
              if (!refund.exist(gstCdnReport.getRefundNumber())) {
                refunds++;
              }
            }
            if (gstCdnReport.getReceiptNo() != null) {
              if (!inv.exist(gstCdnReport.getReceiptNo())) {
                invoices++;
              }
            }
            if (!cust.exist(gstCdnReport.getName())) {
              recipient++;
            }
            if (gstCdnReport.getRefundValue() != null) {
              refundvalue += gstCdnReport.getDocumentType() != null ? (gstCdnReport.getDocumentType().equals("C") ? gstCdnReport.getRefundValue() : -gstCdnReport.getRefundValue()) : gstCdnReport.getRefundValue();
            }
            if (gstCdnReport.getTaxableValue() != null) {
              taxablevalue += gstCdnReport.getDocumentType() != null ? (gstCdnReport.getDocumentType().equals("C") ? gstCdnReport.getTaxableValue() : -gstCdnReport.getTaxableValue()) : gstCdnReport.getTaxableValue();
            }
          }
        }
      }
    }
    int cellId = 0;
    int rowId = 0;
    if (filterParameters != null) {
      rowId = ExcelUtil.createDateHeader(sheet, rowId, 2,
              (filterParameters.getFromDate() != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getFromDate()) : null),
              (filterParameters.getToDate()) != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getToDate()) : null);
    }
    row = sheet.createRow(rowId++);
// COLOR BLUE
    row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
    Cell cell;
//    Total

    cell = row.createCell(0);
    cell.setCellValue("No. of Recipients");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(2);
    cell.setCellValue("No. of Invoices");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(4);
    cell.setCellValue("No. of Notes/Vouchers");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(9);
    cell.setCellValue("Total Note/Refund Voucher Value");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(12);
    cell.setCellValue("Total Taxable Value");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(13);
    cell.setCellValue("Total Cess");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cellId = 0;
    row = sheet.createRow(rowId++);

    cell = row.createCell(0);
    cell.setCellValue(recipient);
    cell.setCellStyle(styleNormalCenter);

    cell = row.createCell(2);
    cell.setCellValue(invoices);
    cell.setCellStyle(styleNormalCenter);

    cell = row.createCell(4);
    cell.setCellValue(refunds);
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(9);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(refundvalue)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(12);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(taxablevalue)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(13);
    cell.setCellValue("0");
    cell.setCellStyle(styleNormalRight);

//     List
    Object[] arr = new Object[]{"GSTIN/UIN of Recipient", "Receiver Name", "Invoice/Advance Receipt Number", "Invoice/Advance Receipt date", "Note/Refund Voucher Number",
      "Note/Refund Voucher date", "Document Type", "Place Of Supply", "Bill Amount", "Note/Refund Voucher Value (By Tax)", "Applicable % of Tax Rate", "Rate",
      "Taxable Value", "Cess Amount", "Pre GST", "Return Type"};
    row = sheet.createRow(rowId++);
    row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
    int size = 0;
    cellId = 0;

    for (Object obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj.toString());
      cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      sheet.autoSizeColumn(size);
      size++;
    }
    if (gstCdnReportList != null && !gstCdnReportList.isEmpty()) {
      for (GstCdnReport list : gstCdnReportList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getGstNo());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getName());

        cell = row.createCell(cellId++);
        if (list.getReceiptNo() != null) {
          cell.setCellValue(list.getReceiptNo());
        }

        cell = row.createCell(cellId++);
        if (list.getRecieptDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(list.getRecieptDate()));
        }

        cell = row.createCell(cellId++);
        if (list.getRefundNumber() != null) {
          cell.setCellValue(list.getRefundNumber());
        }
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        if (list.getRefundDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(list.getRefundDate()));
        }

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getDocumentType());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getStateCode() + "-" + list.getStateName());

        cell = row.createCell(cellId++);
        if (list.getBillAmount() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getBillAmount())));
        }
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getRefundValue())));
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        cell.setCellValue("");

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getTaxRate());
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTaxableValue())));
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        cell.setCellValue("");
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        cell.setCellValue("N");
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getReturnType());
      }
    }
  }

  private static List<GstCdnReport> createGstCdnReportList(List<GstCdnReport> cdnReportList) {
    List<GstCdnReport> gstCdnExcel = new ArrayList<>();
    Double taxableValue = 0.00, refundValue = 0.00;
    int size = 1;
    GstCdnReport cdnReport = new GstCdnReport();
    UniqueCheck rate = new UniqueCheck();
    UniqueCheck invoice = new UniqueCheck();
    for (GstCdnReport list : cdnReportList) {
      if (invoice.exist(list.getRefundNumber()) && rate.exist(list.getTaxRate())) {
        taxableValue += list.getTaxableValue();
        refundValue += list.getRefundValue();
        if (size == cdnReportList.size()) {
          cdnReport.setTaxableValue(taxableValue);
          cdnReport.setRefundValue(refundValue);
          gstCdnExcel.add(cdnReport);
        }

      } else {
        rate = new UniqueCheck();
        cdnReport.setTaxableValue(taxableValue);
        cdnReport.setRefundValue(refundValue);
        if (cdnReport.getId() != null) {
          gstCdnExcel.add(cdnReport);
        }
        taxableValue = list.getTaxableValue();
        refundValue = list.getRefundValue();
        cdnReport = list;
        rate.exist(list.getTaxRate());
        if (size == cdnReportList.size()) {
          gstCdnExcel.add(list);
        }
      }
      size++;
    }
    return gstCdnExcel;
  }

  private static void createGstB2csReport(XSSFSheet sheet, List<GstB2csReport> gstB2csReportList, XSSFWorkbook workbook, FilterParameters filterParameters) {
//    setWorkbook(workbook);
    sheet.setDefaultColumnWidth(15);
    XSSFRow row;

    // Create cell style 
    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    CellStyle styleRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.RIGHT);
    //   CellStyle styleNormalCenter = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);

    double taxablevalue = 0.00;
    double cessAmount = 0.00;
    if (gstB2csReportList != null && !gstB2csReportList.isEmpty()) {
      for (GstB2csReport gstB2csReport : gstB2csReportList) {
        taxablevalue += gstB2csReport.getTaxableValue() != null ? MathUtil.roundOff(gstB2csReport.getTaxableValue(), 0) : 0;
        cessAmount += gstB2csReport.getCessAmount() != null ? gstB2csReport.getCessAmount() : 0.0;
      }
    }
    int cellId = 0;
    int rowId = 0;
    if (filterParameters != null) {
      rowId = ExcelUtil.createDateHeader(sheet, rowId, 2,
              (filterParameters.getFromDate() != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getFromDate()) : null),
              (filterParameters.getToDate()) != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getToDate()) : null);
    }
    row = sheet.createRow(rowId++);
    // COLOR BLUE
    row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
    Cell cell;
//    Total

    cell = row.createCell(4);
    cell.setCellValue("Total Taxable Value");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(5);
    cell.setCellValue("Total Cess");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cellId = 0;
    row = sheet.createRow(rowId++);

    cell = row.createCell(4);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(taxablevalue)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(5);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(cessAmount)));
    cell.setCellStyle(styleNormalRight);

//     List
    String[] arr = new String[]{"Type", "Place Of Supply", "Applicable % of Tax Rate", "Rate", "Taxable Value", "Cess Amount", "E-Commerce GSTIN"};
    row = sheet.createRow(rowId++);
    row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
    int size = 0;
    cellId = 0;

    for (String obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj);
      cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      sheet.autoSizeColumn(size);
      size++;
    }
    if (gstB2csReportList != null && !gstB2csReportList.isEmpty()) {
      for (GstB2csReport list : gstB2csReportList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);

        cell = row.createCell(cellId++);
        if (list.getType().intValue() == 1) {
          cell.setCellValue("SALES");
        } else {
          cell.setCellValue("CDN");
        }

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getStateCode() + "-" + list.getStateName());

        cell = row.createCell(cellId++);
        cell.setCellValue("");

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getRatePercentage());
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTaxableValue() != null ? list.getTaxableValue() : 0)));
        if (list.getType().intValue() == 1) {
          cell.setCellStyle(styleNormalRight);
        } else {
          cell.setCellStyle(styleRed);
        }

        cell = row.createCell(cellId++);
        if (list.getCessAmount() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getCessAmount())));
        }
        cell.setCellStyle(styleNormalRight);

        cell = row.createCell(cellId++);
        cell.setCellValue("");

      }
    }
  }

  private static void createHsnReport(XSSFSheet sheet, List<GstHsnReport> hsnReportList, XSSFWorkbook workbook, FilterParameters filterParameters) {
    //   setWorkbook(workbook);
    sheet.setDefaultColumnWidth(15);
    XSSFRow row;
    Cell cell;
    // Create cell style 
    // CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    CellStyle styleNormalCenter = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);
    //  CellStyle styleHead =  ExcelUtil.style(workbook, HorizontalAlignment.CENTER,15);    
    //  styleHead.setVerticalAlignment(VerticalAlignment.CENTER);

    //  CellStyle styleTitle =  ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    int hsn = 0;
    double taxablevalue = 0.00, totalValue = 0.00, totalIgst = 0.00, totalCgst = 0.00, totalSgst = 0.00, totalKfCess = 0.0;
    try (UniqueCheck uc = new UniqueCheck()) {
      if (hsnReportList != null && !hsnReportList.isEmpty()) {
        for (GstHsnReport hsnReport : hsnReportList) {
          if (hsnReport.getHsnCode() != null && !uc.exist(hsnReport.getHsnCode())) {
            hsn++;
          }
          totalValue += hsnReport.getTotalValue();
          taxablevalue += hsnReport.getTotalTaxableValue();
          totalIgst += hsnReport.getTotalIgst();
          totalCgst += hsnReport.getTotalCgst();
          totalSgst += hsnReport.getTotalSgst();
          if (hsnReport.getKfCessValue() != null) {
            totalKfCess += hsnReport.getKfCessValue();
          }
        }
      }
    }

    int rowId = 0;
    if (filterParameters != null) {
      rowId = ExcelUtil.createDateHeader(sheet, rowId, 2,
              (filterParameters.getFromDate() != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getFromDate()) : null),
              (filterParameters.getToDate()) != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getToDate()) : null);
    }
    row = sheet.createRow(rowId++);
    row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
//    Total

    cell = row.createCell(0);
    cell.setCellValue("No. of HSN");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(5);
    cell.setCellValue("Total Value");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(6);
    cell.setCellValue("Total Taxable Value");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(7);
    cell.setCellValue("Total Integrated Tax");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(8);
    cell.setCellValue("Total Central Tax");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(9);
    cell.setCellValue("Total State/UT Tax");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    cell = row.createCell(10);
    cell.setCellValue("Total Cess");
    cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    row = sheet.createRow(rowId++);

    cell = row.createCell(0);
    cell.setCellValue(hsn);
    cell.setCellStyle(styleNormalCenter);

    cell = row.createCell(5);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(totalValue)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(6);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(taxablevalue)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(7);
    if (totalIgst != 0) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(totalIgst)));
    }
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(8);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(totalCgst)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(9);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(totalSgst)));
    cell.setCellStyle(styleNormalRight);

    cell = row.createCell(10);
    if (totalKfCess != 0) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(totalKfCess)));
    }
    cell.setCellStyle(styleNormalRight);

    UniqueCheck uhsn = new UniqueCheck();

    int i = 1;
    int listSize = 0;
    if (hsnReportList != null) {
      listSize = hsnReportList.size();
    }
    boolean first = true;
    //     List
    int cellId = 0, size = 0;
    Object[] arr = new Object[]{"HSN", "Product Category", "Description", "UQC", "Total Quantity", "Total Value", "Taxable Value", "Integrated Tax Amount",
      "Central Tax Amount", "State/UT Tax Amount", "Cess Amount", "HSN Total Value", "HSN Taxable Value", "HSN Integrated Tax Amount", "HSN Central Tax Amount",
      "HSN State/UT Tax Amount", "HSN Cess Amount"};
    row = sheet.createRow(rowId++);
    row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));

    for (Object obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj.toString());
      cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      sheet.autoSizeColumn(size);
      size++;
    }
    ExcelVariables ev = new ExcelVariables();
    ev.resetTotal();
    if (hsnReportList != null && !hsnReportList.isEmpty()) {
      for (GstHsnReport list : hsnReportList) {
        if (list.getHsnCode() != null && uhsn.exist(list.getHsnCode())) {
          rowId = addHsnReportRows(workbook, list, sheet, rowId, ev);

          if (listSize == i) {
            rowId = createTotalFooter(workbook, sheet, first, rowId, ev);
            ev.resetTotal();
          }
        } else {
          rowId = createTotalFooter(workbook, sheet, first, rowId, ev);
          ev.resetTotal();

          rowId = addHsnReportRows(workbook, list, sheet, rowId, ev);
          first = false;
          if (listSize == i) {
            rowId = createTotalFooter(workbook, sheet, first, rowId, ev);
            ev.resetTotal();
          }
        }
        i++;
      }
    }
  }

  private static int createTotalFooter(XSSFWorkbook workbook, XSSFSheet sheet, boolean first, int rowId, ExcelVariables ev) {
    XSSFRow row;
    Cell cell;
    CellStyle styleTitle = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    if (!first) {
      int cellId = 11;
      row = sheet.getRow(rowId - 1);
      cell = row.createCell(cellId++);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getValueTot())));
      cell.setCellStyle(styleTitle);
      cell = row.createCell(cellId++);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getTaxableTot())));
      cell.setCellStyle(styleTitle);
      cell = row.createCell(cellId++);
      if (ev.getIgstTot() != 0) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getIgstTot())));
      }
      cell.setCellStyle(styleTitle);
      cell = row.createCell(cellId++);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getCgstTot())));
      cell.setCellStyle(styleTitle);
      cell = row.createCell(cellId++);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getSgstTot())));
      cell.setCellStyle(styleTitle);
      cell = row.createCell(cellId++);
      if (ev.getCessTot() != 0) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(ev.getCessTot())));
      }
      cell.setCellStyle(styleTitle);
    }
    return rowId;
  }

  private static int addHsnReportRows(XSSFWorkbook workbook, GstHsnReport list, XSSFSheet sheet, int rowId, ExcelVariables ev) {
    XSSFRow row;
    Cell cell;
    int cellId = 0;
    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    CellStyle rightAlign = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    CellStyle styleRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.RIGHT);

    row = sheet.createRow(rowId++);
    row.setRowStyle(styleNormal);

    cell = row.createCell(cellId++);
    cell.setCellValue(list.getHsnCode());

    cell = row.createCell(cellId++);
    if (list.getProductCategoryTitle() != null) {
      cell.setCellValue(list.getProductCategoryTitle());
    }

    cell = row.createCell(cellId++);
    if (list.getCommodityTitle() != null) {
      cell.setCellValue(list.getCommodityTitle());
    }

    cell = row.createCell(cellId++);
    if (list.getProductPackingTitle() != null) {
      cell.setCellValue(list.getProductPackingTitle());
    }

    cell = row.createCell(cellId++);
    if (list.getTotalQuantity() != null) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTotalQuantity())));
    }
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    }
    cell = row.createCell(cellId++);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTotalValue())));
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    } else {
      cell.setCellStyle(rightAlign);
    }

    cell = row.createCell(cellId++);
    cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTotalTaxableValue())));
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    } else {
      cell.setCellStyle(rightAlign);
    }

    cell = row.createCell(cellId++);
    if (list.getTotalIgst() != null && list.getTotalIgst() != 0) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTotalIgst())));
    }
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    } else {
      cell.setCellStyle(rightAlign);
    }

    cell = row.createCell(cellId++);
    if (list.getTotalCgst() != null && list.getTotalCgst() != 0) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTotalCgst())));
    }
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    } else {
      cell.setCellStyle(rightAlign);
    }

    cell = row.createCell(cellId++);
    if (list.getTotalSgst() != null && list.getTotalSgst() != 0) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getTotalSgst())));
    }
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    } else {
      cell.setCellStyle(rightAlign);
    }

    cell = row.createCell(cellId++);
    if (list.getKfCessValue() != null && list.getKfCessValue() != 0) {
      cell.setCellValue(Double.parseDouble(decimalFormat.format(list.getKfCessValue())));
    }
    if (list.getInvoiceOrReturn() == 2) {
      cell.setCellStyle(styleRed);
    } else {
      cell.setCellStyle(rightAlign);
    }
    ev.setValueTot(ev.getValueTot() + (list.getTotalValue() == null ? 0 : list.getTotalValue()));
    ev.setTaxableTot(ev.getTaxableTot() + (list.getTotalTaxableValue() == null ? 0 : list.getTotalTaxableValue()));
    ev.setIgstTot(ev.getIgstTot() + (list.getTotalIgst() == null ? 0 : list.getTotalIgst()));
    ev.setCgstTot(ev.getCgstTot() + (list.getTotalCgst() == null ? 0 : list.getTotalCgst()));
    ev.setSgstTot(ev.getSgstTot() + (list.getTotalSgst() == null ? 0 : list.getTotalSgst()));
    ev.setCessTot(ev.getCessTot() + (list.getKfCessValue() == null ? 0 : list.getKfCessValue()));

    return rowId;
  }

  private static void createDocumentSummaryReport(XSSFSheet sheet, List<DocumentSummary> docSummaryList, XSSFWorkbook workbook, FilterParameters filterParameters) {
    //setWorkbook(workbook);

    sheet.setDefaultColumnWidth(25);

    String[] arr = new String[]{"", "", "", "Total Number", "Total Cancelled"};
    Cell cell;
    int cellId = 0;
    int rowId = 0;
    if (filterParameters != null) {
      rowId = ExcelUtil.createDateHeader(sheet, rowId, 2,
              (filterParameters.getFromDate() != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getFromDate()) : null),
              (filterParameters.getToDate()) != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getToDate()) : null);
    }
    XSSFRow row;
    row = sheet.createRow(rowId++);
    for (String obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj);
      cell.setCellStyle(ExcelUtil.setFillColor(ExcelUtil.FILL_BLUE, HorizontalAlignment.CENTER, workbook));
    }
    int totalRow = rowId;
    int totalNo = 0, totalCancelled = 0;
    rowId = rowId + 1;
    row = sheet.createRow(rowId++);
    cellId = 0;
    arr = new String[]{"Nature of Document", "Sr. No. From", "Sr. No. To", "Total Number", "Cancelled No"};
    for (String obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj);
      cell.setCellStyle(ExcelUtil.setFillColor(ExcelUtil.FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
    }

    CellStyle rightAlign = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);

    for (DocumentSummary list : docSummaryList) {
      cellId = 0;
      row = sheet.createRow(rowId++);
      cell = row.createCell(cellId++);
      cell.setCellValue(list.getDocumentNature());
      cell = row.createCell(cellId++);
      cell.setCellValue(list.getSlFrom());
      cell = row.createCell(cellId++);
      cell.setCellValue(list.getSlTo());
      cell = row.createCell(cellId++);
      cell.setCellValue(list.getTotalNo());
      cell = row.createCell(cellId++);
      cell.setCellValue(list.getCancelledNo());
      cell.setCellStyle(rightAlign);
      totalNo += list.getTotalNo();
      totalCancelled += list.getCancelledNo();
      cell.setCellStyle(rightAlign);

    }
    row = sheet.createRow(totalRow);
    cell = row.createCell(3);
    cell.setCellValue(totalNo);
    cell.setCellStyle(rightAlign);
    cell = row.createCell(4);
    cell.setCellValue(totalCancelled);
    cell.setCellStyle(rightAlign);
  }

  public static void createGst3bReport(MainView main, FilterParameters filterParameters, Map<String, List<Gst3bReport>> gst3bMap, boolean exportAll, String type) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      String fileName = "";
      if (exportAll) {
        fileName = "gst_3b_report";
        XSSFSheet purchaseSheet = workbook.createSheet("3b_purchase_report");
        createGst3bSheet(purchaseSheet, gst3bMap.get("PURCHASE"), workbook, filterParameters);
        XSSFSheet purchaseReturnSheet = workbook.createSheet("3b_purchase_return_report");
        createGst3bSheet(purchaseReturnSheet, gst3bMap.get("PURCHASE RETURN"), workbook, filterParameters);
        XSSFSheet salesSheet = workbook.createSheet("3b_sales_report");
        createGst3bSheet(salesSheet, gst3bMap.get("SALES"), workbook, filterParameters);
        XSSFSheet salesReturnSheet = workbook.createSheet("3b_sales_return_report");
        createGst3bSheet(salesReturnSheet, gst3bMap.get("SALES RETURN"), workbook, filterParameters);
        XSSFSheet cdnrCSheet = workbook.createSheet("3b_cdnr_c_report");
        createGst3bSheet(cdnrCSheet, gst3bMap.get("CDNR C"), workbook, filterParameters);
        XSSFSheet cdnrDSheet = workbook.createSheet("3b_cdnr_d_report");
        createGst3bSheet(cdnrDSheet, gst3bMap.get("CDNR D"), workbook, filterParameters);

      } else {
        if (type.equals(SystemConstants.PURCHASE_REPORT)) {
          fileName = "3b_purchase_report";
          XSSFSheet purchaseSheet = workbook.createSheet("3b_purchase_report");
          createGst3bSheet(purchaseSheet, gst3bMap.get("PURCHASE"), workbook, filterParameters);
        } else if (type.equals(SystemConstants.PURCHASE_RETURN_REPORT)) {
          fileName = "3b_purchase_return_report";
          XSSFSheet purchaseReturnSheet = workbook.createSheet("3b_purchase_return_report");
          createGst3bSheet(purchaseReturnSheet, gst3bMap.get("PURCHASE RETURN"), workbook, filterParameters);
        } else if (type.equals(SystemConstants.SALES_REPORT)) {
          fileName += "3b_sales_report";
          XSSFSheet salesSheet = workbook.createSheet("3b_sales_report");
          createGst3bSheet(salesSheet, gst3bMap.get("SALES"), workbook, filterParameters);
        } else if (type.equals(SystemConstants.SALES_RETURN_REPORT)) {
          fileName = "3b_sales_return_report";
          XSSFSheet salesReturnSheet = workbook.createSheet("3b_sales_return_report");
          createGst3bSheet(salesReturnSheet, gst3bMap.get("SALES RETURN"), workbook, filterParameters);
        } else if (type.equals(SystemConstants.CDNR_C)) {
          fileName = "3b_cdnr_c_report";
          XSSFSheet cdnrCSheet = workbook.createSheet("3b_cdnr_c_report");
          createGst3bSheet(cdnrCSheet, gst3bMap.get("CDNR C"), workbook, filterParameters);
        } else if (type.equals(SystemConstants.CDNR_D)) {
          fileName = "3b_cdnr_d_report";
          XSSFSheet cdnrDSheet = workbook.createSheet("3b_cdnr_d_report");
          createGst3bSheet(cdnrDSheet, gst3bMap.get("CDNR D"), workbook, filterParameters);
        }
      }
      ExcelUtil.write(fileName + ".xlsx", workbook);
    }

  }

  private static void createGst3bSheet(XSSFSheet sheet, List<Gst3bReport> gst3bReportList, XSSFWorkbook workbook, FilterParameters filterParameters) {
    // setWorkbook(workbook);
    sheet.setDefaultColumnWidth(15);
    XSSFRow row;

    XSSFFont font = workbook.createFont();
    font.setFontHeightInPoints((short) 10);
    font.setFontName("Arial");
    font.setColor(IndexedColors.BLACK.getIndex());
    font.setBold(true);
    font.setItalic(false);

    CellStyle styletotal = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    CellStyle styleNormalRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
    //CellStyle styleNormalCenter = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);

    Cell cell;
    int cellId = 0;
    int rowId = 0;
    if (filterParameters != null) {
      rowId = ExcelUtil.createDateHeader(sheet, rowId, 2,
              (filterParameters.getFromDate() != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getFromDate()) : null),
              (filterParameters.getToDate()) != null ? SystemRuntimeConfig.SDF_DD_MMM_YYYY.format(filterParameters.getToDate()) : null);
    }
    String name = "NAME";
    String type = "";
    String taxSuffix = "", gst0title = "NON TAXABLE VALUE";

    Map<Integer, String> map = new HashMap<>();
    for (Gst3bReport report : gst3bReportList) {
      if (map.isEmpty() || !map.containsKey(report.getAccountGroupId())) {
        map.put(report.getAccountGroupId(), report.getGroupName());
      }
    }

    for (Map.Entry<Integer, String> m : map.entrySet()) {
      row = sheet.createRow(rowId);
      if (m.getValue() != null) {
        ExcelUtil.createAccountGroupHeader(sheet, rowId, 7, m.getValue());
      }
      rowId += 1;
      Map<String, Gst3bReport> supplierMap = new LinkedHashMap<>();
      Map<Double, Double[]> taxMap = new LinkedHashMap<>();
      Map<Double, Double[]> igstTaxMap = new LinkedHashMap<>();
      Map<Double, Double[]> serviceTaxMap = new LinkedHashMap<>();
      Map<Double, Double[]> serviceIgstTaxMap = new LinkedHashMap<>();
      List<Integer> interstate = new ArrayList<>();
      List<Double> gstrates = new ArrayList<>();
      List<Double> igstrates = new ArrayList<>();
      List<Double> kfCessList = new ArrayList<>();
      List<Double> tcsList = new ArrayList<>();
      List<Double> serviceGst = new ArrayList<>();
      List<Double> serviceIgst = new ArrayList<>();
      if (gst3bReportList != null && !gst3bReportList.isEmpty()) {
        for (Gst3bReport list : gst3bReportList) {
          if (list.getIsInterstate() == 0 || list.getIsInterstate() == 2) {
            if (list.getTaxPercentage() != null) {
              gstrates.add(list.getTaxPercentage());
            }
            if (list.getServiceTaxPercentage() != null) {
              serviceGst.add(list.getServiceTaxPercentage());
            }
          } else if (list.getIsInterstate() == 1) {
            if (list.getTaxPercentage() != null) {
              igstrates.add(list.getTaxPercentage());
            }
            if (list.getServiceTaxPercentage() != null) {
              serviceIgst.add(list.getServiceTaxPercentage());
            }
          }
          interstate.add(list.getIsInterstate());
          kfCessList.add(list.getKfCessValue());
          tcsList.add(list.getTcsValue());
          if (supplierMap.containsKey(list.getDocumentNo())) {
            if (list.getIsInterstate() == 0 || list.getIsInterstate() == 2) {
              if (list.getTaxPercentage() != null) {
                taxMap.put(list.getTaxPercentage(), new Double[]{list.getTaxableValue(), list.getTaxData()});
              }
              if (list.getServiceTaxPercentage() != null) {
                serviceTaxMap.put(list.getServiceTaxPercentage(), new Double[]{list.getServiceTaxableValue(), list.getServiceTaxData()});
              }
            } else if (list.getIsInterstate() == 1) {
              if (list.getTaxPercentage() != null) {
                igstTaxMap.put(list.getTaxPercentage(), new Double[]{list.getTaxableValue(), list.getTaxData()});
              }
              if (list.getServiceTaxPercentage() != null) {
                serviceIgstTaxMap.put(list.getServiceTaxPercentage(), new Double[]{list.getServiceTaxableValue(), list.getServiceTaxData()});
              }
            }
          } else {
            taxMap = new LinkedHashMap<>();
            serviceTaxMap = new LinkedHashMap<>();
            igstTaxMap = new LinkedHashMap<>();
            serviceIgstTaxMap = new LinkedHashMap<>();

            if (list.getIsInterstate() == 0 || list.getIsInterstate() == 2) {
              if (list.getTaxPercentage() != null) {
                taxMap.put(list.getTaxPercentage(), new Double[]{list.getTaxableValue(), list.getTaxData()});
              }
              if (list.getServiceTaxPercentage() != null) {
                serviceTaxMap.put(list.getServiceTaxPercentage(), new Double[]{list.getServiceTaxableValue(), list.getServiceTaxData()});
              }
            } else if (list.getIsInterstate() == 1) {
              if (list.getTaxPercentage() != null) {
                igstTaxMap.put(list.getTaxPercentage(), new Double[]{list.getTaxableValue(), list.getTaxData()});
              }
              if (list.getServiceTaxPercentage() != null) {
                serviceIgstTaxMap.put(list.getServiceTaxPercentage(), new Double[]{list.getServiceTaxableValue(), list.getServiceTaxData()});
              }
            }
          }
          if (!taxMap.isEmpty()) {
            list.setTaxMap(taxMap);
          }
          if (!igstTaxMap.isEmpty()) {
            list.setIgstTaxMap(igstTaxMap);
          }
          if (!serviceTaxMap.isEmpty()) {
            list.setServiceTaxMap(serviceTaxMap);
          }
          if (!serviceIgstTaxMap.isEmpty()) {
            list.setServiceIgstTaxMap(serviceIgstTaxMap);
          }
          supplierMap.put(list.getDocumentNo(), list);
          if (list.getType().equals("purchase")) {
            name = "SUPPLIER NAME";
            taxSuffix = "PUR";
          } else if (list.getType().equals("cdnr_c")) {
            name = "CUSTOMER NAME";
          } else if (list.getType().equals("cdnr_d")) {
            name = "PARTY NAME";
          } else if (list.getType().equals("sales_return")) {
            name = "CUSTOMER NAME";
            taxSuffix = "SALES";
          } else if (list.getType().equals("sales")) {
            name = "CUSTOMER NAME";
            taxSuffix = "SALES";
          }
          type = list.getType();
        }
      }
//     Creating Header
      Object[] arr = null;
      if (taxSuffix.equals("PUR")) {
        arr = new Object[]{"INV.NO", "INV DATE", "DOCUMENT_NO", "ENTRY_DATE", name, "GSTNO", "PLACE OF SUPPLY"};
      } else {
        arr = new Object[]{"INV.NO", "INV DATE", name, "GSTNO", "PLACE OF SUPPLY"};
      }
      row = sheet.createRow(rowId++);
      row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      int size = 0;
      cellId = 0;
      int cellNo0 = 0, cellNo5 = 0, cellNo12 = 0, cellNo18 = 0, cellNo28 = 0;
      int igstcellNo0 = 0, igstcellNo5 = 0, igstcellNo12 = 0, igstcellNo18 = 0, igstcellNo28 = 0;
      int serviceCellNo0 = 0, serviceCellNo5 = 0, serviceCellNo12 = 0, serviceCellNo18 = 0, serviceCellNo28 = 0;
      int serviceIgstcellNo0 = 0, serviceIgstcellNo5 = 0, serviceIgstcellNo12 = 0, serviceIgstcellNo18 = 0, serviceIgstcellNo28 = 0;

      for (Object obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj.toString());
        cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size);
        size++;
      }
      if (type.equals("sales_return")) {
        cellId = createCell(row, cellId, "RETURN TYPE", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size++);
      }
      cellId = createCell(row, cellId, "BILL AMOUNT", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      sheet.autoSizeColumn(size++);
      if (gstrates.contains(0.00) || igstrates.contains(0.00)) {
        cellNo0 = cellId;
        igstcellNo0 = cellId;
        cellId = createCell(row, cellId, gst0title, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size);
      }
      if (interstate.contains(2) || interstate.contains(0)) {
        if (gstrates.contains(5.00)) {
          cellNo5 = cellId;
          cellId = createCell(row, cellId, "GST 5% " + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "GST 5% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (gstrates.contains(12.00)) {
          cellNo12 = cellId;
          cellId = createCell(row, cellId, "GST 12% " + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "GST 12% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (gstrates.contains(18.00)) {
          cellNo18 = cellId;
          cellId = createCell(row, cellId, "GST 18% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "GST 18% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (gstrates.contains(28.00)) {
          cellNo28 = cellId;
          cellId = createCell(row, cellId, "GST 28% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "GST 28% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
      }
      if (interstate.contains(1)) {
        if (igstrates.contains(5.00)) {
          igstcellNo5 = cellId;
          cellId = createCell(row, cellId, "IGST 5% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "IGST 5% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (igstrates.contains(12.00)) {
          igstcellNo12 = cellId;
          cellId = createCell(row, cellId, "IGST 12% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "IGST 12% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (igstrates.contains(18.00)) {
          igstcellNo18 = cellId;
          cellId = createCell(row, cellId, "IGST 18% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "IGST 18% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (igstrates.contains(28.00)) {
          igstcellNo28 = cellId;
          cellId = createCell(row, cellId, "IGST 28% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "IGST 28% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
      }
      int lastCell = cellId;
//    Service Data
      if (serviceGst.contains(0.00) || serviceIgst.contains(0.00)) {
        serviceCellNo0 = cellId;
        serviceIgstcellNo0 = cellId;
        cellId = createCell(row, cellId, "SERVICE NON TAXABLE" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size++);
      }
      if (interstate.contains(2) || interstate.contains(0)) {
        if (serviceGst.contains(5.00)) {
          serviceCellNo5 = cellId;
          cellId = createCell(row, cellId, "SERVICE GST 5% " + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "SERVICE GST 5% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (serviceGst.contains(12.00)) {
          serviceCellNo12 = cellId;
          cellId = createCell(row, cellId, "SERVICE GST 12% " + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "SERVICE GST 12% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (serviceGst.contains(18.00)) {
          serviceCellNo18 = cellId;
          cellId = createCell(row, cellId, "SERVICE GST 18% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "SERVICE GST 18% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (serviceGst.contains(28.00)) {
          serviceCellNo28 = cellId;
          cellId = createCell(row, cellId, "SERVICE GST 28% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "SERVICE GST 28% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
      }
      if (interstate.contains(1)) {
        if (serviceIgst.contains(5.00)) {
          serviceIgstcellNo5 = cellId;
          cellId = createCell(row, cellId, "SERVICE IGST 5% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "SERVICE IGST 5% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (serviceIgst.contains(12.00)) {
          serviceIgstcellNo12 = cellId;
          cellId = createCell(row, cellId, "SERVICE IGST 12% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "SERVICE IGST 12% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (serviceIgst.contains(18.00)) {
          serviceIgstcellNo18 = cellId;
          cellId = createCell(row, cellId, "SERVICE IGST 18% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "SERVICE IGST 18% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
        if (serviceIgst.contains(28.00)) {
          serviceIgstcellNo28 = cellId;
          cellId = createCell(row, cellId, "SERVICE IGST 28% TAX" + taxSuffix, ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
          cellId = createCell(row, cellId, "SERVICE IGST 28% TAX", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
          sheet.autoSizeColumn(size++);
        }
      }
      int servicelastCell = cellId;
      if (interstate.contains(2) || interstate.contains(0)) {
        cellId = createCell(row, cellId, "INV CGST AMT", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size++);
        cellId = createCell(row, cellId, "INV SGST/UGST AMT", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size++);
      }
      if (interstate.contains(1)) {
        cellId = createCell(row, cellId, "INV IGST AMT", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size++);
      }
      cellId = createCell(row, cellId, "GST TAX AMT", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      sheet.autoSizeColumn(size++);
      if (type.equals("sales")) {
        cellId = createCell(row, cellId, "CESS AMT", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size++);
      }
      if (type.equals("sales") || type.equals("purchase")) {
        cellId = createCell(row, cellId, "TCS AMOUNT", ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
        sheet.autoSizeColumn(size++);
      }
      Double totalBillAmount = 0.00, totalgst0pur = 0.00, totalgst5pur = 0.00, totalgst5tax = 0.00, totalgst12pur = 0.00, totalgst12tax = 0.00, totalgst18pur = 0.00, kfCessTotal = 0.0,
              totalgst18tax = 0.00, totalgst28pur = 0.00, totalgst28tax = 0.00, totalcgst = 0.00, totalsgst = 0.00, totaligst = 0.00, totaltaxamnt = 0.00, tcsAmountTotal = 0.0;
      Double totalIgst0pur = 0.00, totalIgst5pur = 0.00, totalIgst5tax = 0.00, totalIgst12pur = 0.00, totalIgst12tax = 0.00, totalIgst18pur = 0.00,
              totalIgst18tax = 0.00, totalIgst28pur = 0.00, totalIgst28tax = 0.00;
      Double serviceGst0pur = 0.00, serviceGst5pur = 0.00, serviceGst5tax = 0.00, serviceGst12pur = 0.00, serviceGst12tax = 0.00, serviceGst18pur = 0.00,
              serviceGst18tax = 0.00, serviceGst28pur = 0.00, serviceGst28tax = 0.00, totalservicecgst = 0.00, totalservicesgst = 0.00, totalserviceigst = 0.00;
      Double serviceIgst0pur = 0.00, serviceIgst5pur = 0.00, serviceIgst5tax = 0.00, serviceIgst12pur = 0.00, serviceIgst12tax = 0.00, serviceIgst18pur = 0.00,
              serviceIgst18tax = 0.00, serviceIgst28pur = 0.00, serviceIgst28tax = 0.00;
//      Double grandBillAmnt=0.0,grandGst0pur =0.0,grandGst5pur=0.0,grandGst12pur=0.0,grandGst18pur=0.0,grandGst5pur=0.0,grand
//    List Iteration
      for (Map.Entry<String, Gst3bReport> supplier : supplierMap.entrySet()) {
        if (supplier.getValue().getAccountGroupId().intValue() == m.getKey()) {
          cellId = 0;
          int startCellId = servicelastCell;
          //    int serviceStartCellId = lastCell;
          row = sheet.createRow(rowId++);
          row.setRowStyle(styleNormal);
          Gst3bReport report = supplier.getValue();
          cell = row.createCell(cellId++);
          cell.setCellValue(report.getInvoiceNo());

          cell = row.createCell(cellId++);
          if (report.getType().equals("purchase")) {
            cell.setCellValue(report.getInvoiceDate());
          } else {
            cell.setCellValue(report.getEntryDate());
          }

          if (report.getType().equals("purchase")) {
            cell = row.createCell(cellId++);
            cell.setCellValue(report.getDocumentNo());
            cell = row.createCell(cellId++);
            cell.setCellValue(report.getEntryDate());
          }

          cell = row.createCell(cellId++);
          if (report.getType().equals("purchase")) {
            cell.setCellValue(report.getSupplierName());
          } else if (report.getType().equals("cdnr_d") || report.getType().equals("cdnr_c")) {
            cell.setCellValue(report.getReceiverName());
          } else if (report.getType().equals("sales")) {
            cell.setCellValue(report.getCustomerName());
          } else if (report.getType().equals("sales_return")) {
            cell.setCellValue(report.getCustomerName());
          }

          cell = row.createCell(cellId++);
          cell.setCellValue(report.getGstNo());
          cell.setCellStyle(styleNormalRight);

          cell = row.createCell(cellId++);
          cell.setCellValue(report.getPlaceOfSupply());
          cell.setCellStyle(styleNormal);

          if (report.getType().equals("sales_return")) {
            cell = row.createCell(cellId++);
            cell.setCellValue(report.getReturnType());
            cell.setCellStyle(styleNormal);
          }

          cell = row.createCell(cellId++);
          cell.setCellValue(Double.parseDouble(decimalFormat.format(report.getBillAmount())));
          cell.setCellStyle(styleNormalRight);
          totalBillAmount += report.getBillAmount();
          Double totalTax = 0.00;
          Double totalServiceTax = 0.00;

//      Iteration based on cgst/sgst
          if (report.getTaxMap() != null) {
            for (Map.Entry<Double, Double[]> tax : report.getTaxMap().entrySet()) {
              Double key = tax.getKey();
              Double[] taxArray = tax.getValue();
              if (interstate.contains(0) || interstate.contains(2)) {
                if (key == 0.00) {
                  cell = row.createCell(cellNo0);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  totalgst0pur += (taxArray[0] == null ? 0 : taxArray[0]);
                }
                if (key == 5.00) {
                  cell = row.createCell(cellNo5);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  totalgst5pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(cellNo5 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  totalgst5tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 12.00) {
                  cell = row.createCell(cellNo12);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  totalgst12pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(cellNo12 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  totalgst12tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 18.00) {
                  cell = row.createCell(cellNo18);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  totalgst18pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(cellNo18 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  totalgst18tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 28.00) {
                  cell = row.createCell(cellNo28);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  totalgst28pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(cellNo28 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  totalgst28tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
              }
            }
          }
//      Iteration based on Igst

          if (report.getIgstTaxMap() != null) {
            for (Map.Entry<Double, Double[]> tax : report.getIgstTaxMap().entrySet()) {
              Double key = tax.getKey();
              Double[] taxArray = tax.getValue();
              if (interstate.contains(1)) {
                if (key == 0.00) {
                  cell = row.createCell(igstcellNo0);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  totalIgst0pur += (taxArray[0] == null ? 0 : taxArray[0]);
                }
                if (key == 5.00) {
                  cell = row.createCell(igstcellNo5);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  totalIgst5pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(igstcellNo5 + 1);
                  cell.setCellValue(Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  totalIgst5tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 12.00) {
                  cell = row.createCell(igstcellNo12);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  totalIgst12pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(igstcellNo12 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  totalIgst12tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 18.00) {
                  cell = row.createCell(igstcellNo18);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  totalIgst18pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(igstcellNo18 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  totalIgst18tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 28.00) {
                  cell = row.createCell(igstcellNo28);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  totalIgst28pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(igstcellNo28 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  totalIgst28tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
              }
            }
          }
//      Service Data

//      Iteration based on cgst/sgst
          if (report.getServiceTaxMap() != null) {
            for (Map.Entry<Double, Double[]> tax : report.getServiceTaxMap().entrySet()) {
              Double key = tax.getKey();
              Double[] taxArray = tax.getValue();
              if (interstate.contains(0) || interstate.contains(2)) {
                if (key == 0.00) {
                  cell = row.createCell(serviceCellNo0);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  serviceGst0pur += (taxArray[0] == null ? 0 : taxArray[0]);
                }
                if (key == 5.00) {
                  cell = row.createCell(serviceCellNo5);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  serviceGst5pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(serviceCellNo5 + 1);
                  cell.setCellValue(Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalServiceTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  serviceGst5tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 12.00) {
                  cell = row.createCell(serviceCellNo12);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  serviceGst12pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(serviceCellNo12 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalServiceTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  serviceGst12tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 18.00) {
                  cell = row.createCell(serviceCellNo18);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  serviceGst18pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(serviceCellNo18 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalServiceTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  serviceGst18tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 28.00) {
                  cell = row.createCell(serviceCellNo28);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  serviceGst28pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(serviceCellNo28 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalServiceTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  serviceGst28tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
              }
            }
          }
//      Iteration based on Igst

          if (report.getServiceIgstTaxMap() != null) {
            for (Map.Entry<Double, Double[]> tax : report.getServiceIgstTaxMap().entrySet()) {
              Double key = tax.getKey();
              Double[] taxArray = tax.getValue();
              if (interstate.contains(1)) {
                if (key == 0.00) {
                  cell = row.createCell(serviceIgstcellNo0);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  serviceIgst0pur += (taxArray[0] == null ? 0 : taxArray[0]);
                }
                if (key == 5.00) {
                  cell = row.createCell(serviceIgstcellNo5);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  serviceIgst5pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(serviceIgstcellNo5 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalServiceTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  serviceIgst5tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 12.00) {
                  cell = row.createCell(serviceIgstcellNo12);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  serviceIgst12pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(serviceIgstcellNo12 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalServiceTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  serviceIgst12tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 18.00) {
                  cell = row.createCell(serviceIgstcellNo18);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  serviceIgst18pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(serviceIgstcellNo18 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalServiceTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  serviceIgst18tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
                if (key == 28.00) {
                  cell = row.createCell(serviceIgstcellNo28);
                  cell.setCellValue(taxArray[0] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[0])));
                  cell.setCellStyle(styleNormalRight);
                  serviceIgst28pur += (taxArray[0] == null ? 0 : taxArray[0]);

                  cell = row.createCell(serviceIgstcellNo28 + 1);
                  cell.setCellValue(taxArray[1] == null ? 0 : Double.parseDouble(decimalFormat.format(taxArray[1])));
                  cell.setCellStyle(styleNormalRight);
                  totalServiceTax += (taxArray[1] == null ? 0 : taxArray[1]);
                  serviceIgst28tax += (taxArray[1] == null ? 0 : taxArray[1]);
                }
              }
            }
          }
//      Total CGST,SGST,IGST calculation
          totalTax += totalServiceTax;
          if (interstate.contains(2) || interstate.contains(0)) {
            cell = row.createCell(startCellId++);
            if (report.getIsInterstate() == 2 || report.getIsInterstate() == 0) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(totalTax / 2)));
            }
            cell.setCellStyle(styleNormalRight);
            totalcgst += (report.getIsInterstate() == 2 || report.getIsInterstate() == 0 ? totalTax / 2 : 0);

            cell = row.createCell(startCellId++);
            if (report.getIsInterstate() == 2 || report.getIsInterstate() == 0) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(totalTax / 2)));
            }
            cell.setCellStyle(styleNormalRight);
            totalsgst += (report.getIsInterstate() == 2 || report.getIsInterstate() == 0 ? totalTax / 2 : 0);
          }
          if (interstate.contains(1)) {
            cell = row.createCell(startCellId++);
            if (report.getIsInterstate() == 1) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(totalTax)));
            }
            cell.setCellStyle(styleNormalRight);
            totaligst += (report.getIsInterstate() == 1 ? totalTax : 0);
          }
          cell = row.createCell(startCellId++);
          cell.setCellValue(Double.parseDouble(decimalFormat.format(totalTax)));
          cell.setCellStyle(styleNormalRight);
          totaltaxamnt += totalTax;
          if (report.getType().equals("sales") && !kfCessList.isEmpty()) {
            cell = row.createCell(startCellId++);
            if (report.getKfCessValue() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(report.getKfCessValue())));
              kfCessTotal += report.getKfCessValue();
            }
            cell.setCellStyle(styleNormalRight);

          }
          if ((report.getType().equals("sales") || report.getType().equals("purchase")) && !tcsList.isEmpty()) {
            cell = row.createCell(startCellId++);
            if (report.getTcsValue() != null) {
              cell.setCellValue(Double.parseDouble(decimalFormat.format(report.getTcsValue())));
              tcsAmountTotal += report.getTcsValue();
            }
            cell.setCellStyle(styleNormalRight);

          }
        }
      }
//    Creating footer row

      cellId = 0;
      row = sheet.createRow(rowId);
      row.setRowStyle(ExcelUtil.styleHead(workbook, 10));
      cell = row.createCell(cellId++);
      cell.setCellValue("Total");
      sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 4));
      if (taxSuffix.equals("PUR")) {
        cellId += 6;
      } else {
        cellId += 4;
      }
      if (type.equals("sales_return")) {
        cellId++;
      }
      cell = row.createCell(cellId++);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(totalBillAmount)));
      cell.setCellStyle(styletotal);

      if (gstrates.contains(0.00) || igstrates.contains(0.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalgst0pur + totalIgst0pur)));
        cell.setCellStyle(styletotal);
      }
      if (gstrates.contains(5.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalgst5pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalgst5tax)));
        cell.setCellStyle(styletotal);
      }
      if (gstrates.contains(12.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalgst12pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalgst12tax)));
        cell.setCellStyle(styletotal);
      }
      if (gstrates.contains(18.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalgst18pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalgst18tax)));
        cell.setCellStyle(styletotal);
      }
      if (gstrates.contains(28.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalgst28pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalgst28tax)));
        cell.setCellStyle(styletotal);
      }
      if (igstrates.contains(5.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalIgst5pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalIgst5tax)));
        cell.setCellStyle(styletotal);
      }
      if (igstrates.contains(12.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalIgst12pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalIgst12tax)));
        cell.setCellStyle(styletotal);
      }
      if (igstrates.contains(18.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalIgst18pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalIgst18tax)));
        cell.setCellStyle(styletotal);
      }
      if (igstrates.contains(28.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalIgst28pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalIgst28tax)));
        cell.setCellStyle(styletotal);
      }
//    Service Tax Total footer
      if (serviceGst.contains(0.00) || serviceIgst.contains(0.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceGst0pur + serviceIgst0pur)));
        cell.setCellStyle(styletotal);
      }
      if (serviceGst.contains(5.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceGst5pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceGst5tax)));
        cell.setCellStyle(styletotal);
      }
      if (serviceGst.contains(12.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceGst12pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceGst12tax)));
        cell.setCellStyle(styletotal);
      }
      if (serviceGst.contains(18.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceGst18pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceGst18tax)));
        cell.setCellStyle(styletotal);
      }
      if (serviceGst.contains(28.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceGst28pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceGst28tax)));
        cell.setCellStyle(styletotal);
      }
      if (serviceIgst.contains(5.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceIgst5pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceIgst5tax)));
        cell.setCellStyle(styletotal);
      }
      if (serviceIgst.contains(12.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceIgst12pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceIgst12tax)));
        cell.setCellStyle(styletotal);
      }
      if (serviceIgst.contains(18.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceIgst18pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceIgst18tax)));
        cell.setCellStyle(styletotal);
      }
      if (serviceIgst.contains(28.00)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceIgst28pur)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(serviceIgst28tax)));
        cell.setCellStyle(styletotal);
      }

      if (interstate.contains(2) || interstate.contains(0)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalcgst)));
        cell.setCellStyle(styletotal);

        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totalsgst)));
        cell.setCellStyle(styletotal);
      }
      if (interstate.contains(1)) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(totaligst)));
        cell.setCellStyle(styletotal);
      }
      cell = row.createCell(cellId++);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(totaltaxamnt)));
      cell.setCellStyle(styletotal);
      if (type.equals("sales") && !kfCessList.isEmpty()) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(kfCessTotal)));
        cell.setCellStyle(styletotal);
      }
      if ((type.equals("sales") || type.equals("purchase")) && !tcsList.isEmpty()) {
        cell = row.createCell(cellId++);
        cell.setCellValue(Double.parseDouble(decimalFormat.format(tcsAmountTotal)));
        cell.setCellStyle(styletotal);
      }
      rowId += 1;
    }
  }

  private static Integer createCell(XSSFRow row, Integer cellId, String cellValue, CellStyle cellStyle) {
    Cell cell = row.createCell(cellId++);
    if (cellValue != null) {
      cell.setCellValue(cellValue);
    }
    if (cellStyle != null) {
      cell.setCellStyle(cellStyle);
    }
    return cellId;
  }
}
