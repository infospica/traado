/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.importer.view;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
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
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import spica.scm.domain.Brand;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.ImportProduct;
import spica.scm.domain.Manufacture;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductClassification;
import spica.scm.domain.ProductPackingDetail;
import spica.scm.domain.ProductType;
import spica.scm.domain.Vendor;
import spica.scm.export.ExcelUtil;
import spica.scm.service.ImportProductService;
import spica.scm.view.ScmLookupExtView;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "importProductView")
@ViewScoped
public class ImportProductView implements Serializable {

  private List<ImportProduct> productLogErrorList;
  private List<ImportProduct> selectedProduct;
  private Manufacture defaultManufacture;
  private int activeIndex = 0;
  private int errorLogNum = 0;

  public void actionProductImport(MainView main) {
    try {
      List<ImportProduct> importProductList = ImportProductService.selectAllProductLogList(main, UserRuntimeView.instance().getCompany(), UserRuntimeView.instance().getAppUser().getName());
      for (ImportProduct list : importProductList) {
        ImportProductService.validateProduct(main, list, null, UserRuntimeView.instance().getCompany());
      }
      setProductLogErrorList(null);
      if (getProductLogErrorList(main).isEmpty()) {
        ImportProductService.insertOrUpdate(main, UserRuntimeView.instance().getCompany());
        main.commit("success.import");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.import");
    } finally {
      main.close();
    }
  }

  public void downloadExcelFormat(MainView main) {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      List<ImportProduct> supplierList = ImportProductService.selectSupplierList(main, UserRuntimeView.instance().getCompany().getId());
      List<ImportProduct> categoryList = ImportProductService.selectProductCategoryList(main);
      List<ImportProduct> productTypeList = ImportProductService.selectProductTypeList(main);
      List<ImportProduct> commodityList = ImportProductService.selectCommodityList(main, UserRuntimeView.instance().getCompany().getId());
      List<ImportProduct> productUnitList = ImportProductService.selectProductUnitList(main);
      List<ImportProduct> classificationList = ImportProductService.selectClassificationList(main);
      List<ImportProduct> brandList = ImportProductService.selectBrandList(main, UserRuntimeView.instance().getCompany().getId());
      List<ImportProduct> packingDetailList = ImportProductService.selectPackingDetailList(main);
      List<ImportProduct> manufactureList = ImportProductService.selectManufactureList(main, UserRuntimeView.instance().getCompany().getId());

      String name = "import_product";

      XSSFSheet importsheet = workbook.createSheet("import_product");
      createProductImportFormat(importsheet, workbook);
      XSSFSheet suppliersheet = workbook.createSheet("supplier_list");
      createReferenceSupplierSheet(suppliersheet, workbook, supplierList);
      XSSFSheet commoditysheet = workbook.createSheet("commodity_list");
      createBrandCommoditySheet(commoditysheet, workbook, commodityList, false);
      XSSFSheet categorysheet = workbook.createSheet("product_category_list");
      createReferenceSheet(categorysheet, workbook, categoryList, false, true);
      XSSFSheet typeSheet = workbook.createSheet("product_type_list");
      createReferenceSheet(typeSheet, workbook, productTypeList, false, false);
      XSSFSheet classificationsheet = workbook.createSheet("product_classification_list");
      createReferenceSheet(classificationsheet, workbook, classificationList, false, true);
      XSSFSheet unitsheet = workbook.createSheet("product_unit_list");
      createReferenceSheet(unitsheet, workbook, productUnitList, false, false);
      XSSFSheet brandsheet = workbook.createSheet("brand_list");
      createBrandCommoditySheet(brandsheet, workbook, brandList, true);
      XSSFSheet packingDetailsheet = workbook.createSheet("packing_detail_list");
      createReferenceSheet(packingDetailsheet, workbook, packingDetailList, false, false);
      XSSFSheet manufacturesheet = workbook.createSheet("manufacture_list");
      createReferenceSheet(manufacturesheet, workbook, manufactureList, true, false);
//      Validation

      XSSFName xname = suppliersheet.getWorkbook().createName();
      xname.setRefersToFormula("$B$2:$B$1000");
      xname.setNameName("SUPPLIER");
//      name = workbook.createName();
//      name.setNameName("data");
//      name.setRefersToFormula('supplier_list'
//      ,"$B$1:$F$1");
      XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(importsheet);
      XSSFDataValidationConstraint dvConstraintBeneficiary = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(name);
      XSSFDataValidation validation1 = (XSSFDataValidation) dvHelper.createValidation(dvConstraintBeneficiary, new CellRangeAddressList(1, 100, 0, 0));
      validation1.setShowErrorBox(true);
      importsheet.addValidationData(validation1);
      ExcelUtil.write(name+".xlsx", workbook);
    } catch (Throwable t) {
      main.rollback(t, "error.import");
    } finally {
      main.close();
    }
  }

  private static void createProductImportFormat(XSSFSheet sheet, XSSFWorkbook workbook) {
    sheet.setDefaultColumnWidth(15);
    CellStyle rightAlign = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
    String[] arr = new String[]{"supplier_name", "product_name", "commodity", "product_category", "product_type", "product_classification",
      "pack_size", "product_unit", "hsn_code", "brand (code)", "expiry_sales_days", "product_packing_detail", "packing_description",
      "manufacturers (code)", "ptr_margin(%)", "markup/markdown", "pts_margin(%)", "markup/markdown", "margin(%)", "markup/markdown"};
    XSSFRow row;
    int cellId = 0;
    row = sheet.createRow(0);
    Cell cell;
    int size = 0;
    for (String obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj);
      cell.setCellStyle(rightAlign);
      sheet.autoSizeColumn(size);
      size++;
    }
  }

  private static void createReferenceSheet(XSSFSheet sheet, XSSFWorkbook workbook, List<ImportProduct> itemList, boolean code, boolean commodity) {
    sheet.setDefaultColumnWidth(40);
    CellStyle rightAlign = ExcelUtil.styleBold(workbook, HorizontalAlignment.LEFT);
    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    String[] arr;
    if (code) {
      arr = new String[]{"Id", "Title", "Code"};
    } else if (commodity) {
      arr = new String[]{"Id", "Title", "Commodity"};
    } else {
      arr = new String[]{"Id", "Title"};
    }
    XSSFRow row;
    int rowId = 0;
    int cellId = 0;
    row = sheet.createRow(rowId++);
    Cell cell;
    for (String obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj);
      cell.setCellStyle(rightAlign);
    }
    if (itemList != null) {
      for (ImportProduct list : itemList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getListId());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getTitle());

        if (code) {
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getCode());
        } else if (commodity) {
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getCommodity());
        }
      }
    }
  }

  private static void createBrandCommoditySheet(XSSFSheet sheet, XSSFWorkbook workbook, List<ImportProduct> itemList, boolean brand) {
    sheet.setDefaultColumnWidth(40);
    CellStyle rightAlign = ExcelUtil.styleBold(workbook, HorizontalAlignment.LEFT);
    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    String[] arr;
    if (brand) {
      arr = new String[]{"Id", "supplier_name", "brand", "code"};
    } else {
      arr = new String[]{"Id", "supplier_name", "commodity"};
    }
    XSSFRow row;
    int rowId = 0;
    int cellId = 0;
    row = sheet.createRow(rowId++);
    Cell cell;
    for (String obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj);
      cell.setCellStyle(rightAlign);
    }
    if (itemList != null) {
      for (ImportProduct list : itemList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getListId());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getSupplierName());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getTitle());
        if (brand) {
          cell = row.createCell(cellId++);
          cell.setCellValue(list.getCode());
        }
      }
    }
  }

  private static void createReferenceSupplierSheet(XSSFSheet sheet, XSSFWorkbook workbook, List<ImportProduct> itemList) {
    sheet.setDefaultColumnWidth(40);
    CellStyle rightAlign = ExcelUtil.styleBold(workbook, HorizontalAlignment.LEFT);
    CellStyle styleNormal = ExcelUtil.style(workbook, HorizontalAlignment.LEFT);
    String[] arr = new String[]{"Id", "supplier_name", "ptr_margin_percentage", "pts_margin_percentage", "margin_percentage"};
    XSSFRow row;
    int rowId = 0;
    int cellId = 0;
    row = sheet.createRow(rowId++);
    Cell cell;
    for (String obj : arr) {
      cell = row.createCell(cellId++);
      cell.setCellValue(obj);
      cell.setCellStyle(rightAlign);
    }
    if (itemList != null) {
      for (ImportProduct list : itemList) {
        cellId = 0;
        row = sheet.createRow(rowId++);
        row.setRowStyle(styleNormal);

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getListId());

        cell = row.createCell(cellId++);
        cell.setCellValue(list.getSupplierName());

        cell = row.createCell(cellId++);
        if (list.getPtrMarginPercentage() != null) {
          cell.setCellValue(list.getPtrMarginPercentage());
        }
        cell = row.createCell(cellId++);
        if (list.getPtsMarginPercentage() != null) {
          cell.setCellValue(list.getPtsMarginPercentage());
        }
        cell = row.createCell(cellId++);
        if (list.getMarginPercentage() != null) {
          cell.setCellValue(list.getMarginPercentage());
        }
      }
    }
  }

  public List<ImportProduct> getProductLogErrorList(MainView main) {
    try {
      if (productLogErrorList == null) {
        productLogErrorList = ImportProductService.selectProductLogList(main, UserRuntimeView.instance().getCompany(), UserRuntimeView.instance().getAppUser().getName(), SystemConstants.IMPORT_FAILED);
      }
      setErrorLogNum(productLogErrorList.size());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return productLogErrorList;
  }

  public void setProductLogErrorList(List<ImportProduct> productLogErrorList) {
    this.productLogErrorList = productLogErrorList;
  }

  public Long getRowKey() {
    return new Date().getTime();
  }

  public List<ImportProduct> getSelectedProduct() {
    return selectedProduct;
  }

  public void setSelectedProduct(List<ImportProduct> selectedProduct) {
    this.selectedProduct = selectedProduct;
  }

  public void reset() {
    productLogErrorList = null;
    selectedProduct = null;
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  public Manufacture getDefaultManufacture() {
    return defaultManufacture;
  }

  public void setDefaultManufacture(Manufacture defaultManufacture) {
    this.defaultManufacture = defaultManufacture;
  }

  public void handleFileUpload(FileUploadEvent event) {
    MainView main = Jsf.getMain();
    ImportProductService.deleteProductImport(main, UserRuntimeView.instance().getCompany(), UserRuntimeView.instance().getAppUser().getName());
    try (InputStream input = event.getFile().getInputstream()) {
      uploadExcel(main, input);
      if (errorLogNum > 0) {
        setActiveIndex(1);
      }
      Jsf.update("tabFragmentId");
      Jsf.update("validateUploadDiv");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  private void uploadExcel(MainView main, InputStream inputStream) throws Exception {
    reset();
    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
    XSSFSheet sheet = workbook.getSheetAt(0);
    Iterator<Row> rowIterator = sheet.iterator();
    rowIterator.next();
    int sheetRowNum = 1;
    List<ImportProduct> importProductList = new ArrayList<>();
    while (rowIterator.hasNext()) {
      rowIterator.next();
      Row row = sheet.getRow(sheetRowNum);
      if (row != null && row.getCell(1) != null) {
        ImportProduct importProduct = new ImportProduct();
        importProduct.setLineNo(sheetRowNum);
        importProduct.setSupplierName(row.getCell(0) == null ? null : new DataFormatter().formatCellValue(row.getCell(0)));
        importProduct.setProductName(row.getCell(1) == null ? null : new DataFormatter().formatCellValue(row.getCell(1)));
        importProduct.setCommodity(row.getCell(2) == null ? null : new DataFormatter().formatCellValue(row.getCell(2)));
        importProduct.setProductCategory(row.getCell(3) == null ? null : new DataFormatter().formatCellValue(row.getCell(3)));
        importProduct.setProductType(row.getCell(4) == null ? null : new DataFormatter().formatCellValue(row.getCell(4)));
        importProduct.setProductClassification(row.getCell(5) == null ? null : new DataFormatter().formatCellValue(row.getCell(5)));
        if (row.getCell(6) != null && row.getCell(6).getCellTypeEnum() == CellType.NUMERIC) {
          importProduct.setPackSize(row.getCell(6).getNumericCellValue());
        }
        importProduct.setProductUnit(row.getCell(7) == null ? null : new DataFormatter().formatCellValue(row.getCell(7)));
        importProduct.setHsnCode(row.getCell(8) == null ? null : new DataFormatter().formatCellValue(row.getCell(8)));
        importProduct.setBrand(row.getCell(9) == null ? null : new DataFormatter().formatCellValue(row.getCell(9)));
        if (row.getCell(10) != null && row.getCell(10).getCellTypeEnum() == CellType.NUMERIC) {
          importProduct.setExpirySalesDays(Integer.valueOf(new DataFormatter().formatCellValue(row.getCell(10))));
        }
        importProduct.setProductPackingDetail(row.getCell(11) == null ? null : row.getCell(11).getStringCellValue());
        importProduct.setPackingDescription(row.getCell(12) == null ? null : row.getCell(12).getStringCellValue());
        importProduct.setManufacturer(row.getCell(13) == null ? null : new DataFormatter().formatCellValue(row.getCell(13)));
        if (row.getCell(14) != null && row.getCell(14).getCellTypeEnum() == CellType.NUMERIC) {
          importProduct.setPtrMarginPercentage(row.getCell(14).getNumericCellValue());
        }
        String preset = "";
        if (row.getCell(15) != null) {
          preset = StringUtil.trim(row.getCell(15).getStringCellValue());
          importProduct.setPtrMarkupOrMarkdown(preset.toUpperCase().equals("MARKUP") ? 1 : 2);
        }
        if (row.getCell(16) != null && row.getCell(16).getCellTypeEnum() == CellType.NUMERIC) {
          importProduct.setPtsMarginPercentage(row.getCell(16).getNumericCellValue());
        }
        if (row.getCell(17) != null) {
          preset = StringUtil.trim(row.getCell(17).getStringCellValue());
          importProduct.setPtsMarkupOrMarkdown(preset.toUpperCase().equals("MARKUP") ? 1 : 2);
        }
        if (row.getCell(18) != null && row.getCell(18).getCellTypeEnum() == CellType.NUMERIC) {
          importProduct.setMarginPercentage(row.getCell(18).getNumericCellValue());
        }
        if (row.getCell(19) != null) {
          preset = StringUtil.trim(row.getCell(19).getStringCellValue());
          importProduct.setMarginMarkupOrMarkdown(preset.toUpperCase().equals("MARKUP") ? 1 : 2);
        }
        importProduct.setCreatedBy(UserRuntimeView.instance().getAppUser().getName());
        importProduct.setCompanyId(UserRuntimeView.instance().getCompany().getId());
        importProductList.add(importProduct);
        sheetRowNum++;
      }
    }
    ImportProductService.importProduct(main, importProductList);
    errorLogNum = 0;
    for (ImportProduct importProduct : importProductList) {
      boolean validate = ImportProductService.validateProduct(main, importProduct, null, UserRuntimeView.instance().getCompany());
      if (!validate) {
        errorLogNum++;
      }
    }
    if (errorLogNum == 0) {
      actionProductImport(main);
    }
    main.commit("success.save");
  }

  public int getErrorLogNum() {
    MainView main = Jsf.getMain();
    try {
      if (getProductLogErrorList(main) != null) {
        errorLogNum = productLogErrorList.size();
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return errorLogNum;
  }

  public void setErrorLogNum(int errorLogNum) {
    this.errorLogNum = errorLogNum;
  }

  public void updateLineItem(MainView main, ImportProduct product) {
    try {
      ImportProductService.insertOrUpdateProductImport(main, product);
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public List<Vendor> supplierAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return ImportProductService.supplierAuto(main, UserRuntimeView.instance().getCompany(), filter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<ServiceCommodity> CommodityAuto(MainView main, String filter, ImportProduct product) {
    try {
      Vendor vendor = null;
      if (product.getSupplierName() != null) {
        vendor = new Vendor(ImportProductService.getSupplierId(main, UserRuntimeView.instance().getCompany(), product.getSupplierName()));
      }
      if (vendor != null && vendor.getId() != null) {
        return ImportProductService.commodityAuto(main, UserRuntimeView.instance().getCompany(), filter, vendor.getId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<ProductCategory> productCategoryByCommodityAuto(MainView main, String filter, ImportProduct product) {
    ServiceCommodity commodity = null;
    try {
      if (product.getCommodity() != null) {
        commodity = new ServiceCommodity(ImportProductService.getCommodityId(main, UserRuntimeView.instance().getCompany(), product.getCommodity()));
      }
      if (commodity != null && commodity.getId() != null) {
        return ImportProductService.productCategoryByCommodityAuto(main, commodity.getId(), filter);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<ProductType> productTypeAuto(MainView main, String filter, ImportProduct product) {
    ServiceCommodity commodity = null;
    try {
      if (product.getCommodity() != null) {
        commodity = new ServiceCommodity(ImportProductService.getCommodityId(main, UserRuntimeView.instance().getCompany(), product.getCommodity()));
      }
      if (commodity != null && commodity.getId() != null) {
        return ScmLookupExtView.lookupProductTypeByCommodity(commodity);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<ProductClassification> productClassificationAuto(MainView main, String filter, ImportProduct product) {
    ServiceCommodity commodity = null;
    try {
      if (product.getCommodity() != null) {
        commodity = new ServiceCommodity(ImportProductService.getCommodityId(main, UserRuntimeView.instance().getCompany(), product.getCommodity()));
      }
      if (commodity != null && commodity.getId() != null) {
        return ImportProductService.productClassificationAuto(main, UserRuntimeView.instance().getCompany().getId(), filter);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<Brand> brandAuto(MainView main, String filter, ImportProduct product) {
    Vendor vendor = null;
    try {
      if (product.getSupplierName() != null) {
        vendor = new Vendor(ImportProductService.getSupplierId(main, UserRuntimeView.instance().getCompany(), product.getSupplierName()));
      }
      if (vendor != null && vendor.getId() != null) {
        return ImportProductService.brandByCompanyAuto(main, filter, UserRuntimeView.instance().getCompany(), vendor.getId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<ProductPackingDetail> productPackingDetailAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return ImportProductService.productPackingDetailAuto(main, filter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<Manufacture> manufactureByCompanyAuto(String filter) {
    return ScmLookupExtView.manufactureByCompanyAuto(filter, UserRuntimeView.instance().getCompany());
  }

  public void deleteProductLogErrorList(MainView main) {
    try {
      if (selectedProduct != null) {
        for (ImportProduct errorList : selectedProduct) {
          if (errorList != null && errorList.getId() != null && errorList.getId() > 0) {
            ImportProductService.deleteByPk(main, errorList);
            getProductLogErrorList(main).remove(errorList);
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }
}
