/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.export;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spica.scm.common.InvoiceByHsn;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import static spica.scm.export.ExcelUtil.decimalFormat;
import spica.scm.service.CustomerAddressService;
import spica.scm.util.MathUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
public class LineItemExport {

  public static void exportSalesInvocieItems(String name, MainView main, List<SalesInvoiceItem> itemList, Company company, Customer customer, SalesInvoice invoice, List<Map.Entry<String, InvoiceByHsn>> hsnList) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      XSSFRow row;
      Cell cell;
      Object arr[] = {"Sl.No.", "Product", "Batch", "Exp.Date", "Tax %", "Qty", "Free Qty", "Scheme Disc.", "Scheme Disc %", "Product Disc",
        "Product Disc %", "Rate", "PTS", "PTR", "MRP", "Goods Value", "Value"};

      CellStyle colHead = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);
      CellStyle regular = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);

      SimpleDateFormat simpleformat = new SimpleDateFormat("MMM-YY");

      int rowId = 0, cellId = 0, size = 1, colWidth = 800;
      row = sheet.createRow(rowId);
      createDocHeader(main, row, sheet, rowId, colHead, company, customer, invoice.getInvoiceNo(), "SALES INVOICE");
      rowId += 2;
      row = sheet.createRow(rowId++);
      for (Object obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj.toString());
        cell.setCellStyle(colHead);
        sheet.autoSizeColumn(size);
        size++;
      }

      cellId = 0;
      int rowCount = itemList.size();
      for (int i = 0; i < rowCount; i++) {
        row = sheet.createRow(rowId++);

        cell = row.createCell(0);
        cell.setCellValue(i + 1);
        cell.setCellStyle(regular);
        sheet.setColumnWidth(0, colWidth * 2);

        cell = row.createCell(1);
        if (itemList.get(i).getProduct() != null) {
          cell.setCellValue(itemList.get(i).getProduct().getProductName());
        }
        cell.setCellStyle(regular);
        sheet.setColumnWidth(1, colWidth * 10);

        cell = row.createCell(2);
        cell.setCellValue(itemList.get(i).getBatchNo());
        cell.setCellStyle(regular);

        cell = row.createCell(3);
        if (null != itemList.get(i).getExpiryDate()) {
          cell.setCellValue(simpleformat.format(itemList.get(i).getExpiryDate()));
        }
        cell.setCellStyle(regular);

        cell = row.createCell(4);
        if (itemList.get(i).getTaxCodeId() != null) {
          cell.setCellValue(itemList.get(i).getTaxCodeId().getRatePercentage());
        }
        cell.setCellStyle(regular);

        cell = row.createCell(5);
        if (itemList.get(i).getProductQty() != null) {
          cell.setCellValue(itemList.get(i).getProductQty());
        }
        cell.setCellStyle(regular);

        cell = row.createCell(6);
        if (itemList.get(i).getProductQtyFree() != null) {
          cell.setCellValue(itemList.get(i).getProductQtyFree());
        }
        cell.setCellStyle(regular);

        cell = row.createCell(7);
        if (itemList.get(i).getSchemeDiscountValue() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(itemList.get(i).getSchemeDiscountValue())));
        }
        cell.setCellStyle(regular);

        cell = row.createCell(8);
        if (itemList.get(i).getSchemeDiscountPercentage() != null) {
          cell.setCellValue(itemList.get(i).getSchemeDiscountPercentage());
        }
        cell.setCellStyle(regular);

        cell = row.createCell(9);
        if (itemList.get(i).getProductDiscountValue() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(itemList.get(i).getProductDiscountValue())));
        }
        cell.setCellStyle(regular);

        cell = row.createCell(10);
        if (itemList.get(i).getProductDiscountPercentage() != null) {
          cell.setCellValue(itemList.get(i).getProductDiscountPercentage());
        }
        cell.setCellStyle(regular);

        cell = row.createCell(11);
        if (itemList.get(i).getProdPieceSellingForced() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(itemList.get(i).getProdPieceSellingForced())));
        }
        cell.setCellStyle(regular);

        cell = row.createCell(12);
        if (itemList.get(i).getValuePts() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(itemList.get(i).getValuePts())));
        }
        cell.setCellStyle(regular);

        cell = row.createCell(13);
        if (itemList.get(i).getValuePtr() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(itemList.get(i).getValuePtr())));
        }
        cell.setCellStyle(regular);

        cell = row.createCell(14);
        if (itemList.get(i).getValueMrp() != null) {
          cell.setCellValue(itemList.get(i).getValueMrp());
        }
        cell.setCellStyle(regular);

        cell = row.createCell(15);
        if (itemList.get(i).getValueGoods() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(itemList.get(i).getValueGoods())));
        }
        cell.setCellStyle(regular);

        cell = row.createCell(16);
        if (itemList.get(i).getValueSale() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(itemList.get(i).getValueSale())));
        }
        cell.setCellStyle(regular);

      }
      rowId++;
      row = sheet.createRow(rowId++);
      boolean interstate = false;
      if (invoice.getIsSalesInterstate().equals(SystemConstants.INTERSTATE)) {
        interstate = true;
      }
      Object hsnIntrastate[] = {"Sl.No", "HSN/SAC Code", "Taxable Value", "Tax%", "CGST%", "Value", "SGST%", "Value", "Total Tax Amount"};
      Object hsnInterstate[] = {"Sl.No", "HSN/SAC Code", "Taxable Value", "Tax%", "Total Tax Amount"};

      for (Object obj : interstate ? hsnInterstate : hsnIntrastate) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj.toString());
        cell.setCellStyle(colHead);
        sheet.autoSizeColumn(size);
        size++;
      }
      int i = 0;
      for (Map.Entry<String, InvoiceByHsn> list : hsnList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        cell = row.createCell(cellId++);
        cell.setCellValue(i);
        cell.setCellStyle(regular);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getValue().getHsnCode());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getValue().getTaxableValue());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getValue().getTaxRate());

        if (!interstate) {
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getValue().getCgstRate());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getValue().getCgstValue());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getValue().getSgstRate());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getValue().getSgstValue());
        }
        cell = row.createCell(cellId++);
        cell.setCellValue(list.getValue().getTaxValue());
        i++;
      }
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  public static void exportSalesInvocieItemsForImport(String name, MainView main, List<SalesInvoiceItem> itemList, SalesInvoice invoice) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      CellStyle colHead = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);
      CellStyle regular = ExcelUtil.style(workbook, HorizontalAlignment.CENTER);

      String[] arr = new String[]{"product_name", "batch_no", "expiry_date (mm/yy)", "box_qty", "qty", "free_qty", "product_discount_value", "product_discount_percentage", "mrp", "rate"};
      XSSFRow row;
      int rowId = 0, cellId = 0, size = 0, colWidth = 800;
      row = sheet.createRow(rowId++);
      Cell cell;
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        cell.setCellStyle(colHead);
        sheet.autoSizeColumn(size);
        size++;
      }

      cellId = 0;
      int rowCount = itemList.size();
      for (int i = 0; i < rowCount; i++) {
        row = sheet.createRow(rowId++);

        cell = row.createCell(0);
        if (itemList.get(i).getProduct() != null) {
          cell.setCellValue(itemList.get(i).getProduct().getProductName());
        }
        cell.setCellStyle(regular);
        sheet.setColumnWidth(0, colWidth * 10);

        cell = row.createCell(1);
        cell.setCellValue(itemList.get(i).getBatchNo());
        cell.setCellStyle(regular);

        cell = row.createCell(2);
        SimpleDateFormat simpleformat = new SimpleDateFormat("MMM-YY");
        if (null != itemList.get(i).getExpiryDate()) {
          cell.setCellValue(simpleformat.format(itemList.get(i).getExpiryDate()));
        }
        cell.setCellStyle(regular);

        cell = row.createCell(3);
        if (itemList.get(i).getTaxCodeId() != null) {
          cell.setCellValue("");
        }
        cell.setCellStyle(regular);

        cell = row.createCell(4);
        if (itemList.get(i).getProductQty() != null) {
          cell.setCellValue(itemList.get(i).getProductQty());
        }
        cell.setCellStyle(regular);

        cell = row.createCell(5);
        if (itemList.get(i).getProductQtyFree() != null) {
          cell.setCellValue(itemList.get(i).getProductQtyFree());
        }
        cell.setCellStyle(regular);

        cell = row.createCell(6);
        if (itemList.get(i).getProductDiscountValue() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(itemList.get(i).getProductDiscountValue())));
        }
        cell.setCellStyle(regular);

        cell = row.createCell(7);
        if (itemList.get(i).getProductDiscountPercentage() != null) {
          cell.setCellValue(itemList.get(i).getProductDiscountPercentage());
        }
        cell.setCellStyle(regular);

        cell = row.createCell(8);
        if (itemList.get(i).getValuePts() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(itemList.get(i).getValueMrp())));
        }
        cell.setCellStyle(regular);

        cell = row.createCell(9);
        if (itemList.get(i).getProdPieceSellingForced() != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(itemList.get(i).getProdPieceSellingForced())));
        }
        cell.setCellStyle(regular);

      }
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  public static void exportSalesReturnItems(String name, MainView main, SalesReturn salesReturn, List<SalesReturnItem> itemList) throws IOException {

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      XSSFRow row;
      Cell cell;
      String arr[] = {"Sl.No.", "Product", "Batch", "Exp.Date", "Pack", "Tax %", "Ref. Invoice", "Ref. Date", "Qty", "Scheme Disc.", "Scheme Disc %", "Product Disc",
        "Product Disc %", "Spl. Disc", "Rate", "Pur rate", "PTS", "PTR", "MRP", "Goods Value", "Value"};

      CellStyle colHead = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);

      int rowId = 0, cellId = 0, size = 1, colWidth = 800;
      row = sheet.createRow(rowId);
      createDocHeader(main, row, sheet, rowId, colHead, salesReturn.getCompanyId(), salesReturn.getCustomerId(), salesReturn.getInvoiceNo(), "SALES RETURN");
      rowId += 2;
      row = sheet.createRow(rowId++);
      for (Object obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj.toString());
        cell.setCellStyle(colHead);
        sheet.autoSizeColumn(size);
        size++;
      }

      int i = 1;
      for (SalesReturnItem list : itemList) {
        cellId = 0;
        row = sheet.createRow(rowId++);

        cell = row.createCell(cellId++);
        cell.setCellValue(i++);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getProduct().getProductName());

        cell = row.createCell(cellId++);
        if (list.getProductBatchId() != null && list.getProductBatchId().getBatchNo() != null) {
          cell.setCellValue(list.getProductBatchId().getBatchNo());
        }
        cell = row.createCell(cellId++);
        if (list.getProductBatchId() != null && list.getProductBatchId().getExpiryDateActual() != null) {
          cell.setCellValue(list.getProductBatchId().getExpiryDateActual());
        }
        cell = row.createCell(cellId++);
        cell.setCellValue(list.getProduct().getPackSize() + " " + list.getProduct().getProductUnitId().getSymbol());

        cell = row.createCell(cellId++);
        if (list.getTaxCodeId() != null && list.getTaxCodeId().getRatePercentage() != null) {
          cell.setCellValue(list.getTaxCodeId().getRatePercentage());
        }
        cell = row.createCell(cellId++);
        if (list.getSalesInvoiceId() != null) {
          cell.setCellValue(list.getSalesInvoiceId().getInvoiceNo());
        }

        cell = row.createCell(cellId++);
        if (list.getSalesInvoiceId() != null && list.getSalesInvoiceId().getInvoiceDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(list.getSalesInvoiceId().getInvoiceDate()));
        } else if (list.getRefInvoiceDate() != null) {
          cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(list.getRefInvoiceDate()));
        }

        cell = row.createCell(cellId++);
        if (salesReturn.getSalesReturnType().equals(SystemConstants.SALES_RETURN_TYPE_DAMAGED)) {
          cell.setCellValue(list.getProductQuantityDamaged());
        } else {
          cell.setCellValue(list.getProductQuantity());
        }

        cell = row.createCell(cellId++);
        if (list.getSchemeDiscountValue() != null) {
          cell.setCellValue(list.getSchemeDiscountValue());
        }

        cell = row.createCell(cellId++);
        if (list.getSchemeDiscountPercentage() != null) {
          cell.setCellValue(list.getSchemeDiscountPercentage());
        }

        cell = row.createCell(cellId++);
        if (list.getProductDiscountValue() != null) {
          cell.setCellValue(list.getProductDiscountValue());
        }

        cell = row.createCell(cellId++);
        if (list.getProductDiscountPercentage() != null) {
          cell.setCellValue(list.getProductDiscountPercentage());
        }

        cell = row.createCell(cellId++);
        if (list.getInvoiceDiscountValue() != null) {
          cell.setCellValue(list.getInvoiceDiscountValue());
        }

        cell = row.createCell(cellId++);
        if (list.getValueRate() != null) {
          cell.setCellValue(list.getValueRate());
        }
        cell = row.createCell(cellId++);
        if (list.getPurchaseRate() != null) {
          cell.setCellValue(list.getPurchaseRate());
        }

        cell = row.createCell(cellId++);
        if (list.getValuePts() != null) {
          cell.setCellValue(list.getValuePts());
        }
        cell = row.createCell(cellId++);
        if (list.getValuePtr() != null) {
          cell.setCellValue(list.getValuePtr());
        }
        cell = row.createCell(cellId++);
        if (list.getValueMrp() != null) {
          cell.setCellValue(list.getValueMrp());
        }
        cell = row.createCell(cellId++);
        if (list.getValueGoods() != null) {
          cell.setCellValue(list.getValueGoods());
        }
        cell = row.createCell(cellId++);
        if (list.getValueNet() != null) {
          cell.setCellValue(list.getValueNet());
        }
      }
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  public static void exportPurchaseReturnItems(String name, MainView main, PurchaseReturn purchaseReturn, List<PurchaseReturnItemReplica> itemList, List<Map.Entry<String, InvoiceByHsn>> hsnList) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      XSSFRow row;
      Cell cell;
      String arrIntrastate[] = {"Sl.No.", "Product", "Batch", "Exp.Date", "Tax %", "Return Qty", "Available Qty", "Scheme Disc.", "Scheme Disc %", "Product Disc",
        "Product Disc %", "Inv. Disc", "Inv. Disc %", "Rate", "SGST", "CGST", "MRP", "Goods Value", "Net Amount", "Status"};
      String arrInterstate[] = {"Sl.No.", "Product", "Batch", "Exp.Date", "Tax %", "Return Qty", "Available Qty", "Scheme Disc.", "Scheme Disc %", "Product Disc",
        "Product Disc %", "Inv. Disc", "Inv. Disc %", "Rate", "IGST", "MRP", "Goods Value", "Net Amount", "Status"};

      CellStyle colHead = ExcelUtil.styleBold(workbook, HorizontalAlignment.CENTER);

      int rowId = 0, cellId = 0, size = 1, colWidth = 800;
      row = sheet.createRow(rowId);
      createDocHeader(main, row, sheet, rowId, colHead, purchaseReturn.getCompanyId(), null, purchaseReturn.getInvoiceNo(), "PURCHASE RETURN ");
      rowId += 2;
      row = sheet.createRow(rowId++);

      for (String obj : purchaseReturn.getIsReturnInterstate().equals(SystemConstants.INTERSTATE_PURCHASE) ? arrInterstate : arrIntrastate) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        cell.setCellStyle(colHead);
        sheet.autoSizeColumn(size);
        size++;
      }

      int i = 1;
      for (PurchaseReturnItemReplica list : itemList) {
        cellId = 0;
        row = sheet.createRow(rowId++);

        cell = row.createCell(cellId++);
        cell.setCellValue(i++);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getProductDetail().getProductBatchId().getProductId().getProductName());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getProductDetail().getProductBatchId().getBatchNo());

        cell = row.createCell(cellId++);
        cell.setCellValue(SystemRuntimeConfig.SDF_DD_MM_YYYY.format(list.getProductDetail().getProductBatchId().getExpiryDateActual()));

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getTaxCode().getRatePercentage());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getQuantityReturned());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getActualQty());

        cell = row.createCell(cellId++);
        if (list.getSchemeDiscountValue() != null) {
          cell.setCellValue(list.getSchemeDiscountValue());
        }

        cell = row.createCell(cellId++);
        if (list.getSchemeDiscountPercentage() != null) {
          cell.setCellValue(list.getSchemeDiscountPercentage());
        }

        cell = row.createCell(cellId++);
        if (list.getProductDiscountValue() != null) {
          cell.setCellValue(list.getProductDiscountValue());
        }

        cell = row.createCell(cellId++);
        if (list.getProductDiscountPerc() != null) {
          cell.setCellValue(list.getProductDiscountPerc());
        }

        cell = row.createCell(cellId++);
        if (list.getInvoiceDiscountValue() != null) {
          cell.setCellValue(list.getInvoiceDiscountValue());
        }

        cell = row.createCell(cellId++);
        if (list.getInvoiceDiscountPercentage() != null) {
          cell.setCellValue(list.getInvoiceDiscountPercentage());
        }

        cell = row.createCell(cellId++);
        if (list.getValueRate() != null) {
          cell.setCellValue(list.getValueRate());
        }

        if (purchaseReturn.getIsReturnInterstate().equals(SystemConstants.INTERSTATE_PURCHASE)) {
          cell = row.createCell(cellId++);
          if (list.getValueIgst() != null) {
            cell.setCellValue(list.getValueIgst());
          }
        } else {
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getValueSgst());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getValueCgst());
        }
        cell = row.createCell(cellId++);
        if (list.getValueMrp() != null) {
          cell.setCellValue(list.getValueMrp());
        }

        cell = row.createCell(cellId++);
        if (list.getValueGoods() != null) {
          cell.setCellValue(list.getValueGoods());
        }

        cell = row.createCell(cellId++);
        if (list.getValueNet() != null) {
          cell.setCellValue(list.getValueNet());
        }

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getPurchaseReturnItemStatus().getTitle());
      }
      rowId++;
      row = sheet.createRow(rowId++);
      boolean interstate = false;
      if (purchaseReturn.getIsReturnInterstate().equals(SystemConstants.INTERSTATE_PURCHASE)) {
        interstate = true;
      }
      Object hsnIntrastate[] = {"Sl.No", "HSN/SAC Code", "Taxable Value", "Tax%", "CGST%", "Value", "SGST%", "Value", "Total Tax Amount"};
      Object hsnInterstate[] = {"Sl.No", "HSN/SAC Code", "Taxable Value", "Tax%", "Total Tax Amount"};
      cellId = 0;
      for (Object obj : interstate ? hsnInterstate : hsnIntrastate) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj.toString());
        cell.setCellStyle(colHead);
        size++;
      }
      i = 1;
      for (Map.Entry<String, InvoiceByHsn> list : hsnList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        cell = row.createCell(cellId++);
        cell.setCellValue(i);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getValue().getHsnCode());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getValue().getTaxableValue());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getValue().getTaxRate());

        if (!interstate) {
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getValue().getCgstRate());

          cell = row.createCell(cellId++);
          cell.setCellValue(MathUtil.roundOff(list.getValue().getCgstValue(), 2));

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getValue().getSgstRate());

          cell = row.createCell(cellId++);
          cell.setCellValue(MathUtil.roundOff(list.getValue().getSgstValue(), 2));
        }
        cell = row.createCell(cellId++);
        cell.setCellValue(MathUtil.roundOff(list.getValue().getTaxValue(), 2));
        i++;
      }
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  //FIXME can use method from excelutil
  private static void createDocHeader(MainView main, XSSFRow row, XSSFSheet sheet, int rowId, CellStyle colHead, Company company, Customer customer, String invocieNo, String type) {
    Cell cell;
    CustomerAddress address = null;
    String header = company.getCompanyName();
    if (customer != null) {
      address = CustomerAddressService.selectCustomerRegisteredAddress(main, customer);
      header = company.getCompanyName();
      header += "\n" + address.getAddress();
      header += "\n" + address.getPhone1() + ", " + address.getEmail();
    }
    row = sheet.createRow(rowId);
    cell = row.createCell(1);
    cell.setCellValue(type + " " + invocieNo);
    cell.setCellStyle(colHead);
    sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 1, 15));

    rowId = rowId + 1;
    sheet.addMergedRegion(new CellRangeAddress(rowId, rowId, 1, 15));
    row = sheet.createRow(rowId);
    cell = row.createCell(1);
    cell.setCellValue(header);
    cell.setCellStyle(colHead);
    if (address != null) {
      row.setHeight((short) (row.getHeight() * 5));
    }
  }

}
