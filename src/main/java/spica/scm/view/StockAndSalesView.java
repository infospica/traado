/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import spica.constant.ReportConstant;
import spica.scm.domain.StockAndSales;
import spica.scm.service.StockAndSalesService;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Product;
import spica.scm.export.ExcelUtil;
import static spica.scm.export.ExcelUtil.FILL_ORANGE;
import static spica.scm.export.ExcelUtil.decimalFormat;

/**
 *
 * @author fify
 */
@Named(value = "stockandsalesView")
@ViewScoped
public class StockAndSalesView implements Serializable {

  private transient List<StockAndSales> stockandsalesList;
  private transient Date selectedDate;
  private transient Date fromDate;
  private transient Date today;
  private transient String lastMonth;
  private transient String secondLastMonth;
  private transient Double openingStockValue;
  private transient Double puchaseValue;
  private transient Double salesValue;
  private transient Double closingStockValue;
  private transient Double puchaseReturnValue;
  private transient Double salesReturnValue;
  private transient Double lastMonthSaleValue;
  private transient Double secondLastMonthSaleValue;
  private transient Double stockAdjustmentValue;
  private transient Double netSaleValue;
  private transient Double netProfitValue;
  private transient Double accgrpSalesValue;
  private transient Double accgrpSalesReturnValue;
  private transient Double openingEntryValue;
  private transient Double currentStock;
  private transient Double damagedStock;
  private transient Double damagedStockValue;
  private transient Double grandTotal;
  private AccountGroup accountGroup;
  private boolean icludeDamaged;
  private boolean includeSaleable = true;
  private boolean includeZeroQty;
  private Account account;
  private Product product;
  private transient String reportType;

  public List<StockAndSales> getStockAndSalesList(MainView main) {
    if (StringUtil.isEmpty(stockandsalesList)) {
      try {
        if (selectedDate == null) {
          main.error("select.date");
          return null;
        }
        if (fromDate == null) {
          getFromDate();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        cal.add(Calendar.MONTH, -1);
        lastMonth = new SimpleDateFormat("MMM").format(cal.getTime());
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(fromDate);
        cal1.add(Calendar.MONTH, -2);
        secondLastMonth = new SimpleDateFormat("MMM").format(cal1.getTime());
        openingStockValue = 0.0;
        puchaseValue = 0.0;
        salesValue = 0.0;
        closingStockValue = 0.0;
        puchaseReturnValue = 0.0;
        salesReturnValue = 0.0;
        lastMonthSaleValue = 0.0;
        secondLastMonthSaleValue = 0.0;
        stockAdjustmentValue = 0.0;
        accgrpSalesValue = 0.0;
        accgrpSalesReturnValue = 0.0;
        openingEntryValue = 0.0;
        damagedStock = 0.0;
        currentStock = 0.0;
        damagedStockValue = 0.0;
        if (accountGroup != null) {

          stockandsalesList = StockAndSalesService.getStockAndSalesList(main, accountGroup, account, fromDate, selectedDate, reportType, includeZeroQty, icludeDamaged, includeSaleable);
          for (StockAndSales sales : stockandsalesList) {
            openingStockValue += sales.getOpStockValue() == null ? 0 : sales.getOpStockValue();
            puchaseValue += sales.getPurchaseValue() == null ? 0 : sales.getPurchaseValue();
            salesValue += sales.getSalesValue() == null ? 0 : sales.getSalesValue();
            closingStockValue += sales.getTotValue() == null ? 0 : sales.getTotValue();
            puchaseReturnValue += sales.getPurchaseReturnValue() == null ? 0 : sales.getPurchaseReturnValue();
            salesReturnValue += sales.getSalesReturnValue() == null ? 0 : sales.getSalesReturnValue();
            lastMonthSaleValue += sales.getLastMonthSaleValue() == null ? 0 : sales.getLastMonthSaleValue();
            secondLastMonthSaleValue += sales.getSecondlastMonthSaleValue() == null ? 0 : sales.getSecondlastMonthSaleValue();
            stockAdjustmentValue += sales.getStockAdjustValue() == null ? 0 : sales.getStockAdjustValue();
            accgrpSalesValue += sales.getAccgrpSalesValue() == null ? 0 : sales.getAccgrpSalesValue();
            accgrpSalesReturnValue += sales.getAccgrpSalesReturnValue() == null ? 0 : sales.getAccgrpSalesReturnValue();
            openingEntryValue += sales.getOpenEntryValue() == null ? 0 : sales.getOpenEntryValue();
            damagedStock += sales.getCurrentDamQty() == null ? 0 : sales.getCurrentDamQty();
            damagedStockValue += sales.getTotDamValue() == null ? 0 : sales.getTotDamValue();
            currentStock += sales.getCurrentQty() == null ? 0 : sales.getCurrentQty();
          }
        }
        grandTotal = closingStockValue + damagedStockValue;
        netSaleValue = salesValue - salesReturnValue;
        netProfitValue = closingStockValue + salesValue + puchaseReturnValue - openingStockValue - puchaseValue - salesReturnValue - stockAdjustmentValue;
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return stockandsalesList;
  }

  public List<Account> accountAuto(String filter) {
    if (accountGroup != null && accountGroup.getId() != null) {
      MainView main = Jsf.getMain();
      try {
        return ScmLookupExtView.accountByAccountGroupProfile(main, accountGroup, filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    if (accountGroup != null) {
      if (accountGroup.getId() != null) {
        setAccountGroup(accountGroup);
        setAccount(null);
      }
    }
  }

  public void accountChangeEvent(AjaxBehaviorEvent event) {
    account = null;
    stockandsalesList = null;
  }

  public void accountSelectEvent(SelectEvent event) {
    account = (Account) event.getObject();
  }

  public void submitForm() {
    stockandsalesList = null;
  }

  public void resetStockAndSalesView() {
    setStockandsalesList(null);
    setFromDate(null);
    setSelectedDate(null);
    setReportType(null);
    setAccountGroup(null);
    setAccount(null);
  }

  public void setStockAndSalesList(List<StockAndSales> stockandsalesList) {
    this.stockandsalesList = stockandsalesList;
  }

  public List<StockAndSales> getStockandsalesList() {
    return stockandsalesList;
  }

  public void setStockandsalesList(List<StockAndSales> stockandsalesList) {
    this.stockandsalesList = stockandsalesList;
  }

  public Date getSelectedDate() {
    if (selectedDate == null) {
      selectedDate = new Date();
    }
    return selectedDate;
  }

  public void setSelectedDate(Date selectedDate) {
    this.selectedDate = selectedDate;
  }

  public Date getFromDate() {
    if (fromDate == null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.set(cal.get(cal.YEAR), cal.get(cal.MONTH), 1);
      fromDate = cal.getTime();

    }
    return fromDate;
  }

  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  public String getLastMonth() {
    return lastMonth;
  }

  public void setLastMonth(String lastMonth) {
    this.lastMonth = lastMonth;
  }

  public String getSecondLastMonth() {
    return secondLastMonth;
  }

  public void setSecondLastMonth(String secondLastMonth) {
    this.secondLastMonth = secondLastMonth;
  }

  public Double getOpeningStockValue() {
    return openingStockValue;
  }

  public void setOpeningStockValue(Double openingStockValue) {
    this.openingStockValue = openingStockValue;
  }

  public Double getPuchaseValue() {
    return puchaseValue;
  }

  public void setPuchaseValue(Double puchaseValue) {
    this.puchaseValue = puchaseValue;
  }

  public Double getSalesValue() {
    return salesValue;
  }

  public void setSalesValue(Double salesValue) {
    this.salesValue = salesValue;
  }

  public Double getClosingStockValue() {
    return closingStockValue;
  }

  public void setClosingStockValue(Double closingStockValue) {
    this.closingStockValue = closingStockValue;
  }

  public Double getPuchaseReturnValue() {
    return puchaseReturnValue;
  }

  public void setPuchaseReturnValue(Double puchaseReturnValue) {
    this.puchaseReturnValue = puchaseReturnValue;
  }

  public Double getSalesReturnValue() {
    return salesReturnValue;
  }

  public void setSalesReturnValue(Double salesReturnValue) {
    this.salesReturnValue = salesReturnValue;
  }

  public Double getLastMonthSaleValue() {
    return lastMonthSaleValue;
  }

  public void setLastMonthSaleValue(Double lastMonthSaleValue) {
    this.lastMonthSaleValue = lastMonthSaleValue;
  }

  public Double getSecondLastMonthSaleValue() {
    return secondLastMonthSaleValue;
  }

  public void setSecondLastMonthSaleValue(Double secondLastMonthSaleValue) {
    this.secondLastMonthSaleValue = secondLastMonthSaleValue;
  }

  public Double getNetSaleValue() {
    return netSaleValue;
  }

  public void setNetSaleValue(Double netSaleValue) {
    this.netSaleValue = netSaleValue;
  }

  public Date getToday() {
    if (today == null) { //Set current date
      today = new Date();
    }
    return today;
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public String getReportType() {
    return reportType;
  }

  public void setReportType(String reportType) {
    this.reportType = reportType;
  }

  public Double getStockAdjustmentValue() {
    return stockAdjustmentValue;
  }

  public void setStockAdjustmentValue(Double stockAdjustmentValue) {
    this.stockAdjustmentValue = stockAdjustmentValue;
  }

  public Double getNetProfitValue() {
    return netProfitValue;
  }

  public void setNetProfitValue(Double netProfitValue) {
    this.netProfitValue = netProfitValue;
  }

  public Double getAccgrpSalesValue() {
    return accgrpSalesValue;
  }

  public void setAccgrpSalesValue(Double accgrpSalesValue) {
    this.accgrpSalesValue = accgrpSalesValue;
  }

  public Double getAccgrpSalesReturnValue() {
    return accgrpSalesReturnValue;
  }

  public void setAccgrpSalesReturnValue(Double accgrpSalesReturnValue) {
    this.accgrpSalesReturnValue = accgrpSalesReturnValue;
  }

  public Double getOpeningEntryValue() {
    return openingEntryValue;
  }

  public void setOpeningEntryValue(Double openingEntryValue) {
    this.openingEntryValue = openingEntryValue;
  }

  public Double getDamagedStock() {
    return damagedStock;
  }

  public Double getDamagedStockValue() {
    return damagedStockValue;
  }

  public Double getGrandTotal() {
    return grandTotal;
  }

  public Double getCurrentStock() {
    return currentStock;
  }

  public boolean isIcludeDamaged() {
    return icludeDamaged;
  }

  public void setIcludeDamaged(boolean icludeDamaged) {
    this.icludeDamaged = icludeDamaged;
  }

  public void export(MainView main) {
    try {
      createStockandSalesExport(getStockAndSalesList(main), reportType);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void createStockandSalesExport(List<StockAndSales> stockAndSalesList, String reportType) throws IOException {
    //    Create cell style
    String name = "stock_and_sales_report";
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
      CellStyle styletotal = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
      XSSFRow row;
      Cell cell;
      int cellId = 0;
      int rowId = 0;

      row = sheet.createRow(rowId++);
      row.setRowStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.CENTER, workbook));
      // int size = 0;
      cellId = 0;

      cell = row.createCell(cellId++);
      cell.setCellValue("Product Name");
      cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.LEFT, workbook));
      reportType = reportType != null ? reportType : "";
      if (reportType.equals(ReportConstant.DETAIL_VIEW)) {
        Object[] arr = new Object[]{"Packing", "Expiry Date", "Batch", "MRP"};
        for (Object obj : arr) {
          cell = row.createCell(cellId++);
          cell.setCellValue(obj.toString());
          cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.LEFT, workbook));
        }
      }
      Object[] arr = new Object[]{"Opening Stock", "Opening Entry Stock", "Purchase", "Purchase Return", secondLastMonth, lastMonth, "Sales",
        "Sales Return", "Adjustment"};
      for (Object obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj.toString());
        cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.LEFT, workbook));
      }
      if (reportType.equals(ReportConstant.AUDIT_VIEW)) {
        cell = row.createCell(cellId++);
        cell.setCellValue("Sales Rate");
        cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.LEFT, workbook));
      }
      cell = row.createCell(cellId++);
      cell.setCellValue("Current Stock");
      cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.LEFT, workbook));
      cell = row.createCell(cellId++);
      cell.setCellValue("Ave. Rate");
      cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.LEFT, workbook));
      cell = row.createCell(cellId++);
      cell.setCellValue("Stock Value");
      cell.setCellStyle(ExcelUtil.setFillColor(FILL_ORANGE, HorizontalAlignment.LEFT, workbook));

      for (StockAndSales list : stockAndSalesList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getProductName());

        if (reportType.equals(ReportConstant.DETAIL_VIEW)) {
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getProdPacking());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getExpiryDate());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getProdBatch());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getProdMrpValue());
        }
        cell = row.createCell(cellId++);
        if (list.getQtyAvailable() != null) {
          cell.setCellValue(list.getQtyAvailable());
        }
        cell = row.createCell(cellId++);
        if (list.getOpenEntryQty() != null) {
          cell.setCellValue(list.getOpenEntryQty());
        }

        cell = row.createCell(cellId++);
        if (list.getPurchaseQty() != null) {
          cell.setCellValue(list.getPurchaseQty());
        }

        cell = row.createCell(cellId++);
        if (list.getPurchaseReturnQty() != null) {
          cell.setCellValue(list.getPurchaseReturnQty());
        }

        cell = row.createCell(cellId++);
        if (list.getSecondlastMonthSaleQty() != null) {
          cell.setCellValue(list.getSecondlastMonthSaleQty());
        }

        cell = row.createCell(cellId++);
        if (list.getLastMonthSaleQty() != null) {
          cell.setCellValue(list.getLastMonthSaleQty());
        }

        cell = row.createCell(cellId++);
        if (list.getSalesQty() != null) {
          cell.setCellValue(list.getSalesQty());
        }

        cell = row.createCell(cellId++);
        if (list.getSalesReturnQty() != null) {
          cell.setCellValue(list.getSalesReturnQty());
        }

        cell = row.createCell(cellId++);
        if (list.getStockAdjustQty() != null) {
          cell.setCellValue(list.getStockAdjustQty());
        }

        if (reportType.equals(ReportConstant.AUDIT_VIEW)) {
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getSaleRate());
        }

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getCurrentQty());

        cell = row.createCell(cellId++);
        if (list.getAveRate() != null) {
          cell.setCellValue(list.getAveRate());
        }
        cell = row.createCell(cellId++);
        cell.setCellValue(list.getTotValue());

      }

      rowId++;
      row = sheet.createRow(rowId++);
      row.setRowStyle(styletotal);

      cellId = 2;
      cell = row.createCell(cellId++);
      cell.setCellValue("Opg.Stk Val:");

      cell = row.createCell(cellId++);
      if (openingStockValue != null) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(openingStockValue)));
      }

      cell = row.createCell(cellId++);
      cell.setCellValue("Puchase Val:");

      cell = row.createCell(cellId++);
      if (puchaseValue != null) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(puchaseValue)));
      }

      cell = row.createCell(cellId++);
      cell.setCellValue("Sales Val:");

      cell = row.createCell(cellId++);
      if (salesValue != null) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(salesValue)));
      }

      cellId = 0;
      row = sheet.createRow(rowId++);
      row.setRowStyle(styletotal);

      cell = row.createCell(cellId++);
      cell.setCellValue("Net Stk. Adjust.'s:");

      cell = row.createCell(cellId++);
      if (stockAdjustmentValue != null) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(stockAdjustmentValue)));
      }

      cell = row.createCell(cellId++);
      cell.setCellValue("Closing.Stk Val:");

      cell = row.createCell(cellId++);
      if (closingStockValue != null) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(closingStockValue)));
      }

      if (!reportType.equals(ReportConstant.AUDIT_VIEW)) {
        cell = row.createCell(cellId++);
        cell.setCellValue("Puchase Ret Val:");

        cell = row.createCell(cellId++);
        if (puchaseReturnValue != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(puchaseReturnValue)));
        }

        cell = row.createCell(cellId++);
        cell.setCellValue("Sales Ret Val:");

        cell = row.createCell(cellId++);
        if (salesReturnValue != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(salesReturnValue)));
        }

        cell = row.createCell(cellId++);
        cell.setCellValue("Opening Entry Val:");

        cell = row.createCell(cellId++);
        if (openingEntryValue != null) {
          cell.setCellValue(Double.parseDouble(decimalFormat.format(openingEntryValue)));
        }
      }

      cellId = 0;
      row = sheet.createRow(rowId++);
      row.setRowStyle(styletotal);

      cell = row.createCell(cellId++);
      cell.setCellValue(lastMonth);

      cell = row.createCell(cellId++);
      if (lastMonthSaleValue != null) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(lastMonthSaleValue)));
      }

      cell = row.createCell(cellId++);
      cell.setCellValue(secondLastMonth);

      cell = row.createCell(cellId++);
      if (secondLastMonthSaleValue != null) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(secondLastMonthSaleValue)));
      }

      cell = row.createCell(cellId++);
      cell.setCellValue("Net Sale:");

      cell = row.createCell(cellId++);
      if (netSaleValue != null) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(netSaleValue)));
      }

      cell = row.createCell(cellId++);
      cell.setCellValue("Net Profit:");

      cell = row.createCell(cellId++);
      if (netProfitValue != null) {
        cell.setCellValue(Double.parseDouble(decimalFormat.format(netProfitValue)));
      }
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }

  public boolean isIncludeZeroQty() {
    return includeZeroQty;
  }

  public void setIncludeZeroQty(boolean includeZeroQty) {
    this.includeZeroQty = includeZeroQty;
  }

  public boolean isIncludeSaleable() {
    return includeSaleable;
  }

  public void setIncludeSaleable(boolean includeSaleable) {
    this.includeSaleable = includeSaleable;
  }

}
