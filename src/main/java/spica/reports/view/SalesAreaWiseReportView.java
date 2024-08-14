/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.view;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spica.reports.model.FilterParameters;
import spica.reports.model.SalesAreaWiseReport;
import spica.reports.service.SalesAreaWiseReportService;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.export.ExcelUtil;
import spica.scm.service.CompanyAddressService;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sujesh
 */
@ViewScoped
@Named(value = "salesAreaWiseReportView")
public class SalesAreaWiseReportView implements Serializable {

  private SalesAreaWiseReport salesAreaWiseReport;
  private List<SalesAreaWiseReport> salesAreaWiseReportList;
  private FilterParameters filterParameters;
  private Company company;
  private boolean salesReturn;
  private boolean damageExpiry;
  static DecimalFormat decimalFormat = new DecimalFormat("0.00");
  Double custQty = 0.0, custFreeQty = 0.0, custGdsValue = 0.0, custRate = 0.0, custGoodsDiscount = 0.0, custCreditSales = 0.0;
  Double grpQty = 0.0, grpFreeQty = 0.0, grpGdsValue = 0.0, grpRate = 0.0, grpGoodsDiscount = 0.0, grpCreditSales = 0.0;

  public void reset() {
    salesAreaWiseReportList = null;
  }

  public SalesAreaWiseReport getSalesAreaWiseReport() {
    return salesAreaWiseReport;
  }

  public List<SalesAreaWiseReport> getSalesAreaWiseReportList(MainView main) {
    try {
      if (StringUtil.isEmpty(salesAreaWiseReportList)) {
        salesAreaWiseReportList = SalesAreaWiseReportService.selectAreaWiseReportByCompanyAndGroup(main, getCompany(), filterParameters, isSalesReturn(), isDamageExpiry());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return salesAreaWiseReportList;
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
    }
    return filterParameters;
  }

  public void setFilterParameters(FilterParameters filterParameters) {
    this.filterParameters = filterParameters;
  }

  public Company getCompany() {
    company = UserRuntimeView.instance().getCompany();
    return company;
  }

  public boolean isSalesReturn() {
    return salesReturn;
  }

  public void setSalesReturn(boolean salesReturn) {
    this.salesReturn = salesReturn;
  }

  public boolean isDamageExpiry() {
    return damageExpiry;
  }

  public void setDamageExpiry(boolean damageExpiry) {
    this.damageExpiry = damageExpiry;
  }

  public void export(MainView main) {
    try {
      String name = "Sales Area Wise Report";
      createExcelExport(name, main, getSalesAreaWiseReportList(main), getCompany());
    } catch (Throwable t) {
      main.rollback(t, "error.export");
    } finally {
      main.close();
    }
  }

  private void createExcelExport(String name, MainView main, List<SalesAreaWiseReport> salesAreaWiseReports, Company company) throws IOException {

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      XSSFRow row = null;
      Cell cell;
      CellStyle noramlRight = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      CellStyle colHead = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);
      CellStyle colHeadRight = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
      CellStyle colHeadLeft = ExcelUtil.styleBold(workbook, HorizontalAlignment.LEFT);
      CellStyle groupHead = ExcelUtil.styleBold(workbook, HorizontalAlignment.LEFT);
      CellStyle regular = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      CellStyle regularRightRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.RIGHT);
      CellStyle regularRed = ExcelUtil.styleRed(workbook, HorizontalAlignment.LEFT);

      int rowId = 0, colWidth = 1500, size = 1, cellId = 0;
      row = sheet.createRow(rowId);
      String colHeader[] = {"Product Name", "Packing", "Qty", "Free Qty", "Goods Value", "Rate", "Invoice Number", "Pincode",
        "Invoice Date", "MRP", "Goods Value Discount Deducted", "Customer GST No.", "HSN/SAC Code", "Credit Sales", "Cash Sales", "Invoice number with Prefix"};

      createDocHeader(main, row, colHeader.length - 1, sheet, rowId, groupHead, company);

      Map<Integer, List<SalesAreaWiseReport>> groupMap = new HashMap<Integer, List<SalesAreaWiseReport>>();
      Map<Integer, String> groupNmMap = new HashMap<>();
      List<SalesAreaWiseReport> reportList = new ArrayList<>();
      for (SalesAreaWiseReport areaWise : salesAreaWiseReports) {
        if (!groupMap.containsKey(areaWise.getAccountGroupId())) {
          reportList = new ArrayList<>();
        }
        reportList.add(areaWise);
        groupMap.put(areaWise.getAccountGroupId(), reportList);
        groupNmMap.put(areaWise.getAccountGroupId(), areaWise.getAccountGroupName());
      }

      rowId += 4;
//      Integer groupId = null;
      int grpSize = 0, listSize = 0, grpCounter = 0, listcounter = 0;
      for (Map.Entry<Integer, List<SalesAreaWiseReport>> entry : groupMap.entrySet()) {
        grpCounter += 1;
        List<SalesAreaWiseReport> list = entry.getValue();
        grpSize = groupMap.size();
        listSize = entry.getValue().size();
        if (list != null) {
          cellId = 0;
          size = 1;
          row = sheet.createRow(rowId += 1);
          for (String obj : colHeader) {
            cell = row.createCell(cellId++);
            cell.setCellValue(obj);
            cell.setCellStyle(colHead);
//          sheet.autoSizeColumn(size);
            size++;
          }
          rowId += 1;

          String groupName = groupNmMap.get(entry.getKey());
          row = sheet.createRow(rowId);
          sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 10));
          cell = row.createCell(0);
          cell.setCellValue("Company Name: " + groupName);
          cell.setCellStyle(groupHead);
          sheet.setColumnWidth(0, colWidth * 5);

          cellId = 0;
          listcounter = 0;
          Integer districtId = null;
          Integer customerId = null;

          for (SalesAreaWiseReport salesAreaWiseReport : list) {
            listcounter += 1;
            if (customerId == null || customerId.intValue() != salesAreaWiseReport.getCustomerId().intValue()) {
              if (custQty > 0) {
                rowId = createTotal(false, row, cell, colHeadRight, colHeadLeft, rowId);
                custQty = 0.0;
                custFreeQty = 0.0;
                custGdsValue = 0.0;
                custRate = 0.0;
                custGoodsDiscount = 0.0;
                custCreditSales = 0.0;
              }
            }

            if (districtId == null || districtId.intValue() != salesAreaWiseReport.getDistrictId().intValue()) {
              if (grpQty > 0) {
                rowId = createTotal(true, row, cell, colHeadRight, colHeadLeft, rowId);
                grpQty = 0.0;
                grpFreeQty = 0.0;
                grpGdsValue = 0.0;
                grpRate = 0.0;
                grpGoodsDiscount = 0.0;
                grpCreditSales = 0.0;
              }

            }

            if (districtId == null || districtId.intValue() != salesAreaWiseReport.getDistrictId().intValue()) {
              row = sheet.createRow(rowId += 1);
              sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 10));
              cell = row.createCell(0);
              cell.setCellValue("Area Name : " + salesAreaWiseReport.getDistrictName());
              cell.setCellStyle(groupHead);
              sheet.setColumnWidth(0, colWidth * 5);
            }

            if (customerId == null || customerId.intValue() != salesAreaWiseReport.getCustomerId().intValue()) {

              row = sheet.createRow(rowId += 1);
              sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, 10));
              cell = row.createCell(0);
              cell.setCellValue(salesAreaWiseReport.getCustomerName());
              cell.setCellStyle(groupHead);
              sheet.setColumnWidth(0, colWidth * 5);
            }

            districtId = salesAreaWiseReport.getDistrictId();
            customerId = salesAreaWiseReport.getCustomerId();

            row = sheet.createRow(rowId += 1);
            cell = row.createCell(0);
            cell.setCellValue(salesAreaWiseReport.getProductName());
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRed : regular);
            sheet.setColumnWidth(0, colWidth * 6);

            cell = row.createCell(1);
            cell.setCellValue(salesAreaWiseReport.getPackSize() + "" + salesAreaWiseReport.getUnit());
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRed : regular);
            sheet.setColumnWidth(1, colWidth * 2);

            cell = row.createCell(2);
            cell.setCellValue(salesAreaWiseReport.getQuantity());
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRightRed : noramlRight);
            sheet.setColumnWidth(2, colWidth * 2);

            cell = row.createCell(3);
            if (salesAreaWiseReport.getFreeQuantity() != null) {
              cell.setCellValue(salesAreaWiseReport.getFreeQuantity());
            }
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRightRed : noramlRight);
            sheet.setColumnWidth(3, colWidth * 2);

            cell = row.createCell(4);
            cell.setCellValue(Double.parseDouble(decimalFormat.format(salesAreaWiseReport.getValueGoods())));
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRightRed : noramlRight);
            sheet.setColumnWidth(4, colWidth * 2);

            cell = row.createCell(5);
            cell.setCellValue(salesAreaWiseReport.getRate());
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRightRed : noramlRight);
            sheet.setColumnWidth(5, colWidth * 2);

            cell = row.createCell(6);
            cell.setCellValue(salesAreaWiseReport.getInvoiceNumber());
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRed : regular);
            sheet.setColumnWidth(6, colWidth * 2);

            cell = row.createCell(7);
            if (salesAreaWiseReport.getPincode() != null) {
              cell.setCellValue(salesAreaWiseReport.getPincode());
            }
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRed : regular);
            sheet.setColumnWidth(7, colWidth * 2);

            cell = row.createCell(8);
            cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(salesAreaWiseReport.getInvoiceDate()));
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRed : regular);
            sheet.setColumnWidth(8, colWidth * 2);

            cell = row.createCell(9);
            cell.setCellValue(Double.parseDouble(decimalFormat.format(salesAreaWiseReport.getMrp())));
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRightRed : noramlRight);
            sheet.setColumnWidth(9, colWidth * 2);

            cell = row.createCell(10);
            cell.setCellValue(Double.parseDouble(decimalFormat.format(salesAreaWiseReport.getDeductedValue())));
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRightRed : noramlRight);
            sheet.setColumnWidth(10, colWidth * 2);

            cell = row.createCell(11);
            cell.setCellValue(salesAreaWiseReport.getGstNo());
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRed : regular);
            sheet.setColumnWidth(11, colWidth * 3);

            cell = row.createCell(12);
            cell.setCellValue(salesAreaWiseReport.getHsnCode());
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRed : regular);
            sheet.setColumnWidth(12, colWidth * 2);

            cell = row.createCell(13);
            cell.setCellValue(Double.parseDouble(decimalFormat.format(salesAreaWiseReport.getCreditSales())));
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRightRed : noramlRight);
            sheet.setColumnWidth(13, colWidth * 2);

            cell = row.createCell(14);
            cell.setCellValue("");
            cell.setCellStyle(salesAreaWiseReport.getInvoiceType().equals("SR") ? regularRed : regular);
            sheet.setColumnWidth(14, colWidth * 3);

            custQty += salesAreaWiseReport.getQuantity() == null ? 0.0 : salesAreaWiseReport.getQuantity();
            custFreeQty += salesAreaWiseReport.getFreeQuantity() == null ? 0.0 : salesAreaWiseReport.getFreeQuantity();
            custGdsValue += salesAreaWiseReport.getValueGoods() == null ? 0.0 : salesAreaWiseReport.getValueGoods();
            custRate += salesAreaWiseReport.getRate() == null ? 0.0 : salesAreaWiseReport.getRate();
            custGoodsDiscount += salesAreaWiseReport.getDeductedValue() == null ? 0.0 : salesAreaWiseReport.getDeductedValue();
            custCreditSales += salesAreaWiseReport.getCreditSales() == null ? 0.0 : salesAreaWiseReport.getCreditSales();

            grpQty += salesAreaWiseReport.getQuantity() == null ? 0.0 : salesAreaWiseReport.getQuantity();
            grpFreeQty += salesAreaWiseReport.getFreeQuantity() == null ? 0.0 : salesAreaWiseReport.getFreeQuantity();
            grpGdsValue += salesAreaWiseReport.getValueGoods() == null ? 0.0 : salesAreaWiseReport.getValueGoods();
            grpRate += salesAreaWiseReport.getRate() == null ? 0.0 : salesAreaWiseReport.getRate();
            grpGoodsDiscount += salesAreaWiseReport.getDeductedValue() == null ? 0.0 : salesAreaWiseReport.getDeductedValue();
            grpCreditSales += salesAreaWiseReport.getCreditSales() == null ? 0.0 : salesAreaWiseReport.getCreditSales();

            if (grpCounter == grpSize && listcounter == listSize) {
              if (custQty > 0) {
                rowId = createTotal(false, row, cell, colHeadRight, colHeadLeft, rowId);
              }
              if (grpQty > 0) {
                rowId = createTotal(true, row, cell, colHeadRight, colHeadLeft, rowId);
              }

            }
          }
        }
      }
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  //FIXME use exelutil common method
  private static void createDocHeader(MainView main, XSSFRow row, int size, XSSFSheet sheet, int rowId, CellStyle colHead, Company company) {
    CompanyAddress address = CompanyAddressService.selectCompanyRegisteredAddress(main, company);
    Cell cell;

    rowId = rowId + 1;
    row = sheet.createRow(rowId);
    sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, size));
    cell = row.createCell(0);
    cell.setCellValue("Sales Area Wise Report");
    cell.setCellStyle(colHead);

    rowId = rowId + 1;
    row = sheet.createRow(rowId);
    cell = row.createCell(0);
    cell.setCellValue(" Company Name " + company.getCompanyName());
    cell.setCellStyle(colHead);
    sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, size));

    rowId = rowId + 1;
    sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 0, size));
    row = sheet.createRow(rowId);
    cell = row.createCell(0);
    cell.setCellValue(address.getAddress());
    cell.setCellStyle(colHead);

  }

  private int createTotal(boolean area, XSSFRow row, Cell cell, CellStyle colHeadRight, CellStyle colHeadLeft, int rowId) {
    if (area) {
      row = row.getSheet().createRow(rowId += 1);
      cell = row.createCell(0);
      cell.setCellValue("Area Total ");
      cell.setCellStyle(colHeadLeft);

      cell = row.createCell(2);
      cell.setCellValue(grpQty);
      cell.setCellStyle(colHeadRight);

      cell = row.createCell(3);
      cell.setCellValue(grpFreeQty);
      cell.setCellStyle(colHeadRight);

      cell = row.createCell(4);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(grpGdsValue)));
      cell.setCellStyle(colHeadRight);

      cell = row.createCell(5);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(grpRate)));
      cell.setCellStyle(colHeadRight);

      cell = row.createCell(10);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(grpGoodsDiscount)));
      cell.setCellStyle(colHeadRight);

      cell = row.createCell(13);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(grpCreditSales)));
      cell.setCellStyle(colHeadRight);
    } else {
      row = row.getSheet().createRow(rowId += 1);
      cell = row.createCell(0);
      cell.setCellValue("Group Total ");
      cell.setCellStyle(colHeadLeft);

      cell = row.createCell(2);
      cell.setCellValue(custQty);
      cell.setCellStyle(colHeadRight);

      cell = row.createCell(3);
      cell.setCellValue(custFreeQty);
      cell.setCellStyle(colHeadRight);

      cell = row.createCell(4);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(custGdsValue)));
      cell.setCellStyle(colHeadRight);

      cell = row.createCell(5);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(custRate)));
      cell.setCellStyle(colHeadRight);

      cell = row.createCell(10);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(custGoodsDiscount)));
      cell.setCellStyle(colHeadRight);

      cell = row.createCell(13);
      cell.setCellValue(Double.parseDouble(decimalFormat.format(custCreditSales)));
      cell.setCellStyle(colHeadRight);
    }
    return rowId;
  }

}
