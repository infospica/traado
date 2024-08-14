/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.importer.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.ProductSummary;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.domain.Consignment;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.ConsignmentStatus;
import spica.scm.domain.Contract;
import spica.scm.domain.Import;
import spica.scm.domain.ImportProductEntryDetail;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductEntryStatus;
import spica.scm.export.ExcelUtil;
import spica.scm.service.AccountGroupService;
import spica.scm.service.ConsignmentService;
import spica.scm.service.ImportProductEntryService;
import spica.scm.service.ProductEntryService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.ImportUtil;
import spica.scm.view.AccountUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "importProductEntryView")
@ViewScoped
public class ImportProductEntryView implements Serializable {

  private Import importProductEntry;
  private LazyDataModel<Import> importProductEntryLazyModel;
  private List<ImportProductEntryDetail> importProductEntryItemList;
  private List<ProductEntryDetail> productEntryDetailList;
  private List<ImportProductEntryDetail> selectedProductEntryItemList;
  private List<Import> importProductEntryListSelected;
  private int errorLogNum = 0;
  private transient TaxCalculator taxCalculator;
  private boolean importable = false;
  private transient boolean lineDiscountApplicable;
  private transient boolean specialDiscountApplicable;
  private transient boolean cashDiscountApplicable;
  private transient boolean taxableCashDiscount;
  private transient boolean openingStockEntry;

  @PostConstruct
  public void init() {
    Integer importId = (Integer) Jsf.popupParentValue(Integer.class);
    if (importId != null) {
      getImportProductEntry().setId(importId);
    }
  }

  public ImportProductEntryView() {
    super();
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchImportProductEntry(MainView main, String viewType, Integer openingStock) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        openingStock = (openingStock == null ? 0 : openingStock);
        setOpeningStockEntry(SystemConstants.OPENING_STOCK_ENTRY == openingStock);
        if (main.isNew() && !main.hasError()) {
          setProductEntryImportNewFormView(openingStock);
        } else if (main.isEdit() && !main.hasError()) {
          setImportProductEntry((Import) ImportProductEntryService.selectByPk(main, getImportProductEntry()));
          setImportProductEntryItemList(ImportProductEntryService.selectAllProductEntryLogList(main, getImportProductEntry()));
          setTaxCalculator(taxCalculator);
        } else if (main.isList()) {
          importProductEntryLazyModel = null;
          setImportProductEntryItemList(null);
          loadImportProductEntryList(main, openingStock);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void loadImportProductEntryList(final MainView main, int openingStock) {
    if (importProductEntryLazyModel == null) {
      importProductEntryLazyModel = new LazyDataModel<Import>() {
        private List<Import> list;

        @Override
        public List<Import> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ImportProductEntryService.listPagedByAccount(main, getAccount(), openingStock);
            main.commit(importProductEntryLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Import stockAdjustment) {
          return stockAdjustment.getId();
        }

        @Override
        public Import getRowData(String rowKey) {
          if (list != null) {
            for (Import obj : list) {
              if (rowKey.equals(obj.getId().toString())) {
                return obj;
              }
            }
          }
          return null;
        }
      };
    }
  }

//  --------Setting values for new Import
  private void setProductEntryImportNewFormView(int openingStock) {
    getImportProductEntry().reset();
    getImportProductEntry().setImportType(SystemConstants.IMPORT_TYPE_PRODUCT_ENTRY);
    getImportProductEntry().setOpeningStockEntry(openingStock);
    getImportProductEntry().setCompanyId(getCompany());
    getImportProductEntry().setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    if (getAccount() != null) {
      getImportProductEntry().setAccountId(getAccount());
      getImportProductEntry().setTaxProcessorId(getCompany().getCountryTaxRegimeId().getTaxProcessorId());
      getImportProductEntry().setCreatedBy(UserRuntimeView.instance().getAppUser().getName());
    }
    getImportProductEntry().setCreatedAt(new Date());
  }

//  -----Validating and Importing  
  public void actionProductEntryImport1(MainView main) {
    try {
      importPe(main);
      main.commit("success.import");
    } catch (Throwable t) {
      main.rollback(t, "error.import");
    } finally {
      main.close();
    }
  }

  private void importPe(MainView main) {
    List<ImportProductEntryDetail> importProductEntryItemList = new ArrayList<>();
    if (getCompany() != null && getAccount() != null) {
      importProductEntryItemList = ImportProductEntryService.selectAllProductEntryLogList(main, getImportProductEntry());
    }
    if (importProductEntry.getStatus() == SystemConstants.IMPORT_FAILED) {
      ImportProductEntryService.validateProduct(main, importProductEntry, importProductEntryItemList, getCompany());
    }
    if (importProductEntry.getStatus() == SystemConstants.IMPORT_SUCCESS) {
      ProductEntry productEntry = saveProductEntry(main, importProductEntry);
      saveProductEntryDetail(main, importProductEntryItemList, productEntry);
    }
  }

//  --------Create new Purchase Entry  
  private ProductEntry saveProductEntry(MainView main, Import importProductEntry) {
    List<ProductEntryDetail> serviceEntryDetailList = new ArrayList<>();
    productEntryDetailList = new ArrayList<>();
    ProductEntry productEntry = new ProductEntry();
    productEntry.setInvoiceNo(importProductEntry.getReferenceNo());
    productEntry.setInvoiceDate(importProductEntry.getEntryDate());
    productEntry.setInvoiceAmount(importProductEntry.getInvoiceAmount());
    productEntry.setProductEntryStatusId(new ProductEntryStatus(SystemConstants.DRAFT));
    productEntry.setProductEntryDate(new Date());
    productEntry.setCompanyId(getCompany());
    productEntry.setFinancialYearId(getCompany().getCurrentFinancialYear());
    productEntry.setOpeningStockEntry(importProductEntry.getOpeningStockEntry());
    productEntry.setConsignmentId(createConsignmentReceiptInfo(main));
    productEntry.setProductEntryStatusId(new ProductEntryStatus(SystemConstants.DRAFT));
    setSpecialDiscountApplicable(importProductEntry.getInvoiceDiscount() != null && importProductEntry.getInvoiceDiscount() > 0);
    if (importProductEntry.getInvoiceDiscount() != null) {
      productEntry.setIsInvoiceDiscountInPercentage(SystemConstants.DISCOUNT_IN_VALUE);
      setSpecialDiscountApplicable(true);
    }
    productEntry.setInvoiceAmountDiscountValue(importProductEntry.getInvoiceDiscount());
    productEntry.setTaxProcessorId(getCompany().getCountryTaxRegimeId().getTaxProcessorId());
    setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(productEntry.getTaxProcessorId().getProcessorClass()));
    getTaxCalculator().processProductEntryCalculation(productEntry, productEntryDetailList, serviceEntryDetailList);
    productEntry.setInvoiceDiscountValue(importProductEntry.getInvoiceDiscount());
    productEntry.setDecimalPrecision(getAccount().getSupplierDecimalPrecision() == null ? 2 : getAccount().getSupplierDecimalPrecision());
    productEntry.setQuantityOrFree(getAccount().getIsSchemeApplicable());
    if ((productEntry.getInvoiceDiscountValue() != null || productEntry.getInvoiceAmountDiscountPerc() != null) && productEntry.getIsInvoiceDiscountToCustomer() == null) {
      if (AccountUtil.isSuperStockiest(getAccount())) {
        productEntry.setIsInvoiceDiscountToCustomer(SystemConstants.DISCOUNT_FOR_COMPANY);
      } else {
        productEntry.setIsInvoiceDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
      }
    }
    if (productEntry.getId() == null) {
      productEntry.setAccountId(getAccount());
      productEntry.setContractId(getContract());
      productEntry.setIsPurchaseInterstate(AccountUtil.getBusinessArea(productEntry.getAccountId()).intValue() == 1 ? 1 : 0);
      productEntry.setAccountGroupId(AccountGroupService.selectDefaultAccountGroupByAccount(main, getAccount()));
    }

    ProductEntryService.insertOrUpdate(main, productEntry);
    main.em().flush();
    return productEntry;
  }

//  ------Create Product Entry Details  
  private void saveProductEntryDetail(MainView main, List<ImportProductEntryDetail> importEntryDetailList, ProductEntry productEntry) {
    List<ProductEntryDetail> serviceEntryDetailList = new ArrayList<>();
    for (ImportProductEntryDetail list : importEntryDetailList) {
      ProductEntryDetail entryDetail = new ProductEntryDetail();
      entryDetail.setProductEntryId(productEntry);
      productEntryDetailList.add(entryDetail);
      setProductEntryDetail(main, entryDetail, list);
    }
    if (importProductEntry.getCashDiscount() != null) {
      productEntry.setCashDiscountValue(importProductEntry.getCashDiscount());
      setCashDiscountApplicable(true);
      if (importProductEntry.getCashDiscountTaxable() != null) {
        setTaxableCashDiscount(importProductEntry.getCashDiscountTaxable() == SystemConstants.CASH_DISCOUNT_TAXABLE ? true : false);
      }
    }
    // sets applicable discounts
    setProductEntryApplicableDiscounts(productEntry);
    ProductEntryService.insertOrUpdate(main, productEntry, productEntryDetailList, serviceEntryDetailList, getTaxCalculator());
  }

  private void setProductEntryDetail(MainView main, ProductEntryDetail productEntryDetail, ImportProductEntryDetail importEntry) {
    ProductSummary productSummary = new ProductSummary();
    productEntryDetail.resetProductEntryDetail();
    productEntryDetail.setProduct(ImportProductEntryService.getProductId(main, importEntry.getProductName().trim(), getCompany().getId()));
    productEntryDetail.setProductBatchId(ImportProductEntryService.getBatchId(main, importEntry.getBatchNo().trim(), productEntryDetail.getProduct().getId(), importEntry.getValueMrp(), getCompany().getId()));
    productSummary.setProductName(productEntryDetail.getProduct().getProductName());
    productSummary.setProductId(productEntryDetail.getProduct().getId());
    productSummary.setBatch(importEntry.getBatchNo());
    if (productSummary.getBatch() == null || productSummary.getBatch().isEmpty()) {
      ProductBatch batch = ImportProductEntryService.createBatch(main, productEntryDetail, importEntry);
      productEntryDetail.setProductBatchId(batch);
      productEntryDetail.setBatchNo(batch.getBatchNo());
      productSummary.setBatch(batch.getBatchNo());
      productSummary.setProductBatchId(batch.getId());
    } else {
      productEntryDetail.setBatchNo(productEntryDetail.getProductBatchId().getBatchNo());
      productSummary.setBatch(productEntryDetail.getProductBatchId().getBatchNo());
      productSummary.setProductBatchId(productEntryDetail.getProductBatchId().getId());
    }
    productEntryDetail.setProductSummary(productSummary);
    productEntryDetail.setProductQuantity(importEntry.getQty());
    if (importEntry.getFreeQty() != null && importEntry.getFreeQty() > 0) {
      productEntryDetail.setProductQuantityFree(importEntry.getFreeQty());
      if (StringUtil.trim(importEntry.getSchemeDiscountBeneficiary()).toUpperCase().equals("CUSTOMER")) {
        productEntryDetail.setIsSchemeDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
      } else {
        productEntryDetail.setIsSchemeDiscountToCustomer(SystemConstants.DISCOUNT_FOR_COMPANY);
        if (importEntry.getReplacement() != null) {
          if (importEntry.getReplacement().equals("Y")) {
            productEntryDetail.setSchemeDiscountReplacement(SystemConstants.DISCOUNT_REPLACEMENT);
          } else {
            productEntryDetail.setSchemeDiscountReplacement(SystemConstants.DISCOUNT_NON_REPLACEMENT);
          }
        }
      }
    }
    if (importEntry.getProductDiscount() != null && importEntry.getProductDiscount() > 0) {
      productEntryDetail.setDiscountValue(importEntry.getProductDiscount());
      if (StringUtil.trim(importEntry.getProductDiscountBeneficiary()).toUpperCase().equals("CUSTOMER")) {
        productEntryDetail.setIsLineDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
      } else {
        productEntryDetail.setIsLineDiscountToCustomer(SystemConstants.DISCOUNT_FOR_COMPANY);
      }
      setLineDiscountApplicable(true);
    }
    productEntryDetail.setValueRate(importEntry.getRate());
    productEntryDetail.setValuePts(importEntry.getPts());
    if (importEntry.getDamagedQty() != null) {
      productEntryDetail.setProductQuantityDamaged(importEntry.getDamagedQty());
    }
    productEntryDetail.setProductQuantityExcess(importEntry.getExcessQty());
    productEntryDetail.setProductQuantityShortage(importEntry.getShortage());
    getTaxCalculator().updateProductEntryDetail(main, getAccount(), productEntryDetail);
    if (importEntry.getPts() != null) {
      productEntryDetail.setValuePts(importEntry.getPts());
    }
    if (productEntryDetail.getProductSummary().getProductBatchId() == null) {
      productEntryDetail.setProductBatchId(null);
    } else {
//      getProductHashCode().put(productEntryDetail.getProductSummary().getProductHash(), productEntryDetail.getProductSummary().getProductId());
      productEntryDetail.setProductInvoiceDetailList(ProductEntryService.selectLastFivePurchaseByProductDetail(main, productEntryDetail));
    }
  }

  private void setProductEntryApplicableDiscounts(ProductEntry productEntry) {
    productEntry.setLineItemDiscount(isLineDiscountApplicable() ? SystemRuntimeConfig.LINE_DISCOUNT_APPLICABLE : null);
    productEntry.setSpecialDiscountApplicable(isSpecialDiscountApplicable() ? SystemRuntimeConfig.SPECIAL_DISCOUNT_APPLICABLE : null);
    productEntry.setCashDiscountApplicable(isCashDiscountApplicable() ? SystemRuntimeConfig.CASH_DISCOUNT_APPLICABLE : null);
    productEntry.setCashDiscountTaxable(isTaxableCashDiscount() ? SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE : null);
  }

  private Consignment createConsignmentReceiptInfo(MainView main) {
    Consignment consignment = new Consignment();
    consignment.setCompanyId(UserRuntimeView.instance().getCompany());
    consignment.setVendorId(UserRuntimeView.instance().getAccount().getVendorId());
    consignment.setAccountId(UserRuntimeView.instance().getAccount());
    ConsignmentService.setFromVendorAddress(main, consignment);
    ConsignmentService.setToCompanyAddress(main, consignment);
    consignment.setConsignmentDetail(new ConsignmentDetail());
    consignment.getConsignmentDetail().setNoOfInvoice(1);
    consignment.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    consignment.getConsignmentDetail().setReceiptNo("Imported");
    consignment.getConsignmentDetail().setReceiptNote("Imported");
    consignment.setConsignmentStatusId(new ConsignmentStatus(SystemConstants.CONSIGNMENT_STATUS_DELIVERED));
    consignment.setConsignmentDate(new Date());
    ConsignmentService.insertOrUpdateInvoiceEntryConsignmentDetail(main, consignment);
    main.em().flush();
    return consignment;
  }

  public void downloadExcelFormat(MainView main) {
    try {
      createProductImportFormat();
    } catch (Throwable t) {
      main.error(t, "error.download", new String[]{""});
    } finally {
      main.close();
    }
  }

  private void createProductImportFormat() throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      String name = "import_product_entry";
      XSSFSheet sheet = workbook.createSheet(name);
      sheet.setDefaultColumnWidth(15);
      CellStyle rightAlignBold = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
      CellStyle rightAlign = ExcelUtil.style(workbook, HorizontalAlignment.RIGHT);
      List<ImportProductEntryDetail> entryList = new ArrayList<>();
      String[] arr = new String[]{"product_name", "batch_no", "expiry_date", "value_mrp", "qty", "free_qty", "scheme_discount_beneficiary", "replacement(Y/N)", "product_discount_value", "product_discount_beneficiary", "rate", "pts", "excess_qty", "damaged_qty", "shortage"};
      XSSFRow row;
      int cellId = 0;
      row = sheet.createRow(0);
      Cell cell;
      int size = 0;
      for (String obj : arr) {
        cell = row.createCell(cellId++);
        cell.setCellValue(obj);
        cell.setCellStyle(rightAlignBold);
        sheet.autoSizeColumn(size);
        size++;
      }
      if (entryList != null && !entryList.isEmpty()) {
        int i = 1;
        for (ImportProductEntryDetail list : entryList) {
          row = sheet.createRow(i++);
          cellId = 0;
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getProductName() == null ? "" : list.getProductName());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getBatchNo() == null ? "" : list.getBatchNo());

          cell = row.createCell(cellId++);
          if (list.getExpiryDate() != null) {
            String strDate = SystemRuntimeConfig.SDF_DD_MM_YYYY.format(list.getExpiryDate() == null ? "" : list.getExpiryDate());
            cell.setCellValue(strDate);
          }

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getValueMrp() == null ? 0 : list.getValueMrp());
          cell.setCellStyle(rightAlign);

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getQty() == null ? 0 : list.getQty());
          cell.setCellStyle(rightAlign);

          cell = row.createCell(cellId++);
          if (list.getFreeQty() != null) {
            cell.setCellValue(list.getFreeQty());
          }
          cell.setCellStyle(rightAlign);

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getSchemeDiscountBeneficiary() == null ? "" : list.getSchemeDiscountBeneficiary());

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getReplacement() == null ? "" : list.getReplacement());

          cell = row.createCell(cellId++);
          if (list.getProductDiscount() != null) {
            cell.setCellValue(list.getProductDiscount());
          }
          cell.setCellStyle(rightAlign);

          cell = row.createCell(cellId++);
          cell.setCellValue(list.getProductDiscountBeneficiary() == null ? "" : list.getProductDiscountBeneficiary());

          cell = row.createCell(cellId++);
          if (list.getRate() != null) {
            cell.setCellValue(list.getRate());
          }
          cell.setCellStyle(rightAlign);

          cell = row.createCell(cellId++);
          if (list.getPts() != null) {
            cell.setCellValue(list.getPts());
          }
          cell.setCellStyle(rightAlign);

          cell = row.createCell(cellId++);
          if (list.getExcessQty() != null) {
            cell.setCellValue(list.getExcessQty());
          }
          cell.setCellStyle(rightAlign);

          cell = row.createCell(cellId++);
          if (list.getDamagedQty() != null) {
            cell.setCellValue(list.getDamagedQty());
          }
          cell.setCellStyle(rightAlign);

          cell = row.createCell(cellId++);
          if (list.getShortage() != null) {
            cell.setCellValue(list.getShortage());
          }
          cell.setCellStyle(rightAlign);
        }
      }
      XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
      XSSFDataValidationConstraint dvConstraintBeneficiary = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(new String[]{"Company", "Customer"});
      XSSFDataValidationConstraint dvConstraintReplaccement = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(new String[]{"Y", "N"});
      XSSFDataValidation validation1 = (XSSFDataValidation) dvHelper.createValidation(dvConstraintBeneficiary, new CellRangeAddressList(1, 100, 6, 6));
      XSSFDataValidation validation2 = (XSSFDataValidation) dvHelper.createValidation(dvConstraintBeneficiary, new CellRangeAddressList(1, 100, 9, 9));
      XSSFDataValidation validation3 = (XSSFDataValidation) dvHelper.createValidation(dvConstraintReplaccement, new CellRangeAddressList(1, 100, 7, 7));
      validation1.setShowErrorBox(true);
      validation2.setShowErrorBox(true);
      validation3.setShowErrorBox(true);
      sheet.addValidationData(validation1);
      sheet.addValidationData(validation2);
      sheet.addValidationData(validation3);
      ExcelUtil.write(name + ".xlsx", workbook);
    }
  }
  // ------------Saving and importing purchase entry from Excel

  public void handleFileUpload(FileUploadEvent event) {
    MainView main = Jsf.getMain();
    try (InputStream input = event.getFile().getInputstream()) {
      ImportUtil.insertOrUpdateImport(main, importProductEntry);
      main.em().flush();
      int errorLogNum = uploadExcel(main, input, importProductEntry);
      if (errorLogNum == 0) {
        importPe(main);
        main.commit("success.import");
      }
      if (importProductEntry.getStatus() == SystemConstants.IMPORT_FAILED) {
        main.error("error.import");
        setImportProductEntryItemList(ImportProductEntryService.selectAllProductEntryLogList(main, importProductEntry));
      }
    } catch (Throwable t) {
      main.rollback(t, "error.import");
    } finally {
      main.setViewType(ViewTypes.editform);
      main.close();
      Jsf.update("f1");
    }
  }

  private int uploadExcel(MainView main, InputStream inputStream, Import importProductEntry) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
      XSSFSheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rowIterator = sheet.iterator();
      rowIterator.next();
      int sheetRowNum = 1;
      List<ImportProductEntryDetail> importProductEntryList = new ArrayList<>();
      while (rowIterator.hasNext()) {
        rowIterator.next();
        Row row = sheet.getRow(sheetRowNum);
        if (row != null && row.getCell(0) != null) {
          String productName = new DataFormatter().formatCellValue(row.getCell(0));
          if (!productName.isEmpty()) {
            ImportProductEntryDetail importItem = new ImportProductEntryDetail();
            importItem.setLineNo(sheetRowNum);
            importItem.setProductName(row.getCell(0) == null ? null : new DataFormatter().formatCellValue(row.getCell(0)));
            importItem.setBatchNo(row.getCell(1) == null ? null : new DataFormatter().formatCellValue(row.getCell(1)));
            if (importItem.getBatchNo() != null) {
              importItem.setBatchNo(importItem.getBatchNo().trim());
            }
            if (row.getCell(2) != null && row.getCell(2).getCellTypeEnum() == CellType.NUMERIC && HSSFDateUtil.isCellDateFormatted(row.getCell(2))) {
              importItem.setExpiryDate(row.getCell(2).getDateCellValue());
            }
            if (row.getCell(3) != null && row.getCell(3).getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setValueMrp(row.getCell(3).getNumericCellValue());
            }
            if (row.getCell(4) != null && row.getCell(4).getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setQty(row.getCell(4).getNumericCellValue());
            }
            if (row.getCell(5) != null && row.getCell(5).getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setFreeQty((int) row.getCell(5).getNumericCellValue());
            }
            importItem.setSchemeDiscountBeneficiary(row.getCell(6) == null ? null : new DataFormatter().formatCellValue(row.getCell(6)));
            importItem.setReplacement(row.getCell(7) == null ? null : new DataFormatter().formatCellValue(row.getCell(7)));
            if (row.getCell(8) != null && row.getCell(8).getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setProductDiscount(row.getCell(8).getNumericCellValue());
            }
            importItem.setProductDiscountBeneficiary(row.getCell(9) == null ? null : new DataFormatter().formatCellValue(row.getCell(9)));
            if (row.getCell(10) != null && row.getCell(10).getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setRate(row.getCell(10).getNumericCellValue());
            }
            if (row.getCell(11) != null && row.getCell(11).getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setPts(row.getCell(11).getNumericCellValue());
            }
            if (row.getCell(12) != null && row.getCell(12).getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setExcessQty((int) row.getCell(12).getNumericCellValue());
            }
            if (row.getCell(13) != null && row.getCell(13).getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setDamagedQty(row.getCell(13).getNumericCellValue());
            }
            if (row.getCell(14) != null && row.getCell(14).getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setShortage((int) row.getCell(14).getNumericCellValue());
            }
            importItem.setImportId(importProductEntry);
            importProductEntryList.add(importItem);
            sheetRowNum++;
          }
        }
      }
      ImportProductEntryService.importProductEntry(main, importProductEntryList);
      return ImportProductEntryService.validateProduct(main, importProductEntry, importProductEntryList, getCompany());
    }
  }

//  ------------Error Handling Section
  public void updateLineItem(MainView main, ImportProductEntryDetail entry) {
    try {
      ImportProductEntryService.insertOrUpdateImportProductEntryItem(main, entry);
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public List<ProductSummary> completeProductDetail(String filter) {
    ImportProductEntryDetail iped = (ImportProductEntryDetail) Jsf.getAttribute("productEntryItem");
    MainView main = Jsf.getMain();
    try {
      iped.setProductSummaryList(getTaxCalculator().productSummaryAuto(main, getAccount(), filter, 0, null));
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return iped.getProductSummaryList();
  }

  public void productSelectEvent(MainView main, ImportProductEntryDetail iped) {
    try {
      if (iped.getProductSummary() != null) {
        ProductBatch batch = ImportUtil.getProductBatchbyId(main, iped.getProductSummary().getProductBatchId() != null ? iped.getProductSummary().getProductBatchId().toString() : null);
        iped.setBatchNo(batch.getBatchNo());
        if (batch != null) {
          DateFormat dateFormat = new SimpleDateFormat("MM-yy");
          if (batch.getExpiryDateActual() != null) {
            String expiryDate = dateFormat.format(batch.getExpiryDateActual());
            iped.setDate(expiryDate);
          }
          iped.setValueMrp(batch.getValueMrp());
        }
        iped.setProductName(iped.getProductSummary().getProductName());
        updateLineItem(main, iped);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

//--------------- Delete Import
  public void deleteImportProductEntryList(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(importProductEntryListSelected)) {
        for (Import importProductEntry : importProductEntryListSelected) {
          ImportProductEntryService.deleteByPk(main, importProductEntry);
        }
      } else {
        ImportProductEntryService.deleteByPk(main, getImportProductEntry());
        main.setViewType(ViewTypes.list);
      }
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public void deleteProductEntryDetail(MainView main) {
    try {
      for (ImportProductEntryDetail importProductEntryItem : selectedProductEntryItemList) {
        ImportProductEntryService.deleteItemByPk(main, importProductEntryItem);
      }
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public void reset() {
    setLineDiscountApplicable(false);
    setSpecialDiscountApplicable(false);
    setProductEntryDetailList(null);
    setSelectedProductEntryItemList(null);
    setImportProductEntryItemList(null);
    setCashDiscountApplicable(false);
    setTaxCalculator(null);
  }

  public boolean isImportable() {
    if (importProductEntry.getReferenceNo() != null && importProductEntry.getEntryDate() != null && importProductEntry.getInvoiceAmount() != null) {
      importable = true;
    } else {
      importable = false;
    }
    return importable;
  }

//  -------------Getters and setters
  public Long getRowKey() {
    return new Date().getTime();
  }

  public int getErrorLogNum() {
    errorLogNum = 0;
    if (getImportProductEntryItemList() != null) {
      for (ImportProductEntryDetail item : importProductEntryItemList) {
        if (item.getError() != null && !item.getError().isEmpty()) {
          errorLogNum++;
        }
      }
    }
    return errorLogNum;
  }

  public void setErrorLogNum(int errorLogNum) {
    this.errorLogNum = errorLogNum;
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }

  public Account getAccount() {
    return UserRuntimeView.instance().getAccount();
  }

  public Contract getContract() {
    return UserRuntimeView.instance().getContract();
  }

  public List<ImportProductEntryDetail> getImportProductEntryItemList() {
    return importProductEntryItemList;
  }

  public void setImportProductEntryItemList(List<ImportProductEntryDetail> importProductEntryItemList) {
    this.importProductEntryItemList = importProductEntryItemList;
  }

  public boolean isLineDiscountApplicable() {
    return lineDiscountApplicable;
  }

  public void setLineDiscountApplicable(boolean lineDiscountApplicable) {
    this.lineDiscountApplicable = lineDiscountApplicable;
  }

  public boolean isSpecialDiscountApplicable() {
    return specialDiscountApplicable;
  }

  public void setSpecialDiscountApplicable(boolean specialDiscountApplicable) {
    this.specialDiscountApplicable = specialDiscountApplicable;
  }

  public boolean isCashDiscountApplicable() {
    return cashDiscountApplicable;
  }

  public void setCashDiscountApplicable(boolean cashDiscountApplicable) {
    this.cashDiscountApplicable = cashDiscountApplicable;
  }

  public boolean isTaxableCashDiscount() {
    taxableCashDiscount = false;
    if (importProductEntry != null && importProductEntry.getCashDiscountTaxable() != null) {
      taxableCashDiscount = importProductEntry.getCashDiscountTaxable() == SystemConstants.CASH_DISCOUNT_TAXABLE ? true : false;
    }
    return taxableCashDiscount;
  }

  public void setTaxableCashDiscount(boolean taxableCashDiscount) {
    if (importProductEntry != null) {
      importProductEntry.setCashDiscountTaxable(taxableCashDiscount ? SystemConstants.CASH_DISCOUNT_TAXABLE : 0);
    }
    this.taxableCashDiscount = taxableCashDiscount;
  }

  public TaxCalculator getTaxCalculator() {
    if (getImportProductEntry() != null && getImportProductEntry().getTaxProcessorId() != null) {
      taxCalculator = SystemRuntimeConfig.getTaxCalculator(getImportProductEntry().getTaxProcessorId().getProcessorClass());
    }
    return taxCalculator;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public List<ProductEntryDetail> getProductEntryDetailList() {
    return productEntryDetailList;
  }

  public void setProductEntryDetailList(List<ProductEntryDetail> productEntryDetailList) {
    this.productEntryDetailList = productEntryDetailList;
  }

  public List<ImportProductEntryDetail> getSelectedProductEntryItemList() {
    return selectedProductEntryItemList;
  }

  public void setSelectedProductEntryItemList(List<ImportProductEntryDetail> selectedProductEntryItemList) {
    this.selectedProductEntryItemList = selectedProductEntryItemList;
  }

  public Import getImportProductEntry() {
    if (importProductEntry == null) {
      importProductEntry = new Import();
    }
    return importProductEntry;
  }

  public void setImportProductEntry(Import importProductEntry) {
    this.importProductEntry = importProductEntry;
  }

  public LazyDataModel<Import> getImportProductEntryLazyModel() {
    return importProductEntryLazyModel;
  }

  public void setImportProductEntryLazyModel(LazyDataModel<Import> importProductEntryLazyModel) {
    this.importProductEntryLazyModel = importProductEntryLazyModel;
  }

  public List<Import> getImportProductEntryListSelected() {
    return importProductEntryListSelected;
  }

  public void setImportProductEntryListSelected(List<Import> importProductEntryListSelected) {
    this.importProductEntryListSelected = importProductEntryListSelected;
  }

  public boolean isOpeningStockEntry() {
    return openingStockEntry;
  }

  public void setOpeningStockEntry(boolean openingStockEntry) {
    this.openingStockEntry = openingStockEntry;
  }
}
