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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
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
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.Contract;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.Import;
import spica.scm.domain.ImportSalesItem;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesInvoiceStatus;
import spica.scm.domain.TradeProfile;
import spica.scm.export.ExcelUtil;
import spica.scm.service.ImportSalesService;
import spica.scm.service.SalesInvoiceItemService;
import spica.scm.service.SalesInvoiceService;
import spica.scm.service.TradeProfileService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.ImportUtil;
import spica.scm.util.MathUtil;
import spica.scm.util.SalesInvoiceItemUtil;
import spica.scm.util.SalesInvoiceUtil;
import spica.scm.view.ScmLookupExtView;
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
@Named(value = "importSalesView")
@ViewScoped
public class ImportSalesView implements Serializable {

  private Import importSales;
  private SalesInvoice salesInvoice;
  private int lastSeq;
  private int errorLogNum = 0;
  private transient TaxCalculator taxCalculator;
  private LazyDataModel<Import> importSalesLazyModel;
  private List<Import> importSalesListSelected;
  private List<ImportSalesItem> importSalesItemList;
  private List<ImportSalesItem> selectedSalesItemList;
  private boolean importable = false;

  @PostConstruct
  public void init() {
    Integer importId = (Integer) Jsf.popupParentValue(Integer.class);
    if (importId != null) {
      getImportSales().setId(importId);
    }
  }

  public ImportSalesView() {
    super();
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchImportSales(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          setSalesImportNewFormView(main);
        } else if (main.isEdit() && !main.hasError()) {
          setImportSales((Import) ImportSalesService.selectByPk(main, getImportSales()));
          setImportSalesItemList(ImportSalesService.selectAllSalesLogList(main, getImportSales()));

        } else if (main.isList()) {
          importSalesLazyModel = null;
          setImportSalesItemList(null);
          loadImportSalesList(main);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void loadImportSalesList(final MainView main) {
    if (importSalesLazyModel == null) {
      importSalesLazyModel = new LazyDataModel<Import>() {
        private List<Import> list;

        @Override
        public List<Import> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ImportSalesService.listPagedByAccountGroup(main, UserRuntimeView.instance().getAccountGroup());
            main.commit(importSalesLazyModel, first, pageSize);
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
  private void setSalesImportNewFormView(MainView main) {
    getImportSales().reset();
    setSalesInvoice(null);
    setTaxCalculator(null);
    getImportSales().setImportType(SystemConstants.IMPORT_TYPE_SALES);
    if (getAccountGroup() != null) {
      setLastSeq(ImportSalesService.getLastSeq(main, getAccountGroup()));
      getImportSales().setReferenceNo("IMPORT/" + getAccountGroup().getGroupCode() + "/" + getLastSeq());
      getImportSales().setCompanyId(getCompany());
      getImportSales().setAccountGroupId(getAccountGroup());
      getImportSales().setTaxProcessorId(getCompany().getCountryTaxRegimeId().getTaxProcessorId());
      setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getImportSales().getTaxProcessorId().getProcessorClass()));
      getImportSales().setCreatedBy(UserRuntimeView.instance().getAppUser().getName());
      getImportSales().setCreatedAt(new Date());
    }
  }

//  -----Validating and Importing
  public void actionSalesImport(MainView main) {
    try {
      List<ImportSalesItem> importSalesItemList = new ArrayList<>();
      if (getCompany() != null) {
        importSalesItemList = ImportSalesService.selectAllSalesLogList(main, getImportSales());
      }
      if (importSales.getStatus() == SystemConstants.IMPORT_FAILED) {
        ImportSalesService.validateItem(main, importSales, importSalesItemList, getCompany());
      }
      if (importSales.getStatus() == SystemConstants.IMPORT_SUCCESS) {
        SalesInvoice salesInvoice = saveSalesInvoice(main, importSales);
        setSalesInvoice(salesInvoice);
        saveSalesInvoiceItem(main, salesInvoice, importSalesItemList);
        main.setViewType(ViewTypes.list);
        main.commit("success.import");
      }
    } catch (Exception t) {
      main.rollback(t, "error.import");
    } finally {
      main.close();
    }
  }

//  --------Create new Sales Invoice
  private SalesInvoice saveSalesInvoice(MainView main, Import importSales) {
    SalesInvoice salesInvoice = new SalesInvoice();
    salesInvoice.setAccountGroupId(importSales.getAccountGroupId());
    salesInvoice.setAccountGroupPriceListId(importSales.getAccountGroupPriceListId());
    salesInvoice.setCompanyId(importSales.getCompanyId());
    salesInvoice.setInvoiceDate(importSales.getEntryDate());
    salesInvoice.setSalesCreditDays(importSales.getSalesCreditDays());
    salesInvoice.setSalesInvoiceStatusId(new SalesInvoiceStatus(SystemConstants.DRAFT));
    salesInvoice.setCustomerId(importSales.getCustomerId());
    CompanyAddress companyAddress = ImportSalesService.getCompanyAddress(main, getCompany());
    salesInvoice.setCompanyAddressId(companyAddress);
    salesInvoice.setNote(importSales.getNote());
    salesInvoice.setTaxProcessorId(importSales.getTaxProcessorId());
    salesInvoice.setCreatedAt(new Date());
    salesInvoice.setCreatedBy(importSales.getCreatedBy());
    salesInvoice.setInvoiceEntryDate(importSales.getEntryDate());
    boolean isInterstateSales = SystemRuntimeConfig.isInterstateBusiness(getCompany().getStateId().getStateCode(), salesInvoice.getCustomerId().getStateId().getStateCode());
    salesInvoice.setIsSalesInterstate(isInterstateSales ? SystemConstants.INTERSTATE : SystemConstants.INTRASTATE);
    salesInvoice.setSalesInvoiceType(SystemConstants.SALES_INVOICE_TYPE_SALES_INVOICE);
    CustomerAddress customerAddress = ImportSalesService.getCustomerAddress(main, salesInvoice.getCustomerId());
    salesInvoice.setCustomerAddressId(customerAddress);
    salesInvoice.setShippingAddressId(customerAddress);
    salesInvoice.setFinancialYearId(importSales.getFinancialYearId());
    salesInvoice.setServiceAsExpense(importSales.getAccountGroupId().getServiceAsExpense());
    SalesInvoiceService.insertOrUpdate(main, salesInvoice);
    return salesInvoice;
  }

//  ------Create Sales Invoice Items
  private void saveSalesInvoiceItem(MainView main, SalesInvoice salesInvoice, List<ImportSalesItem> importSalesItemList) {
    List<SalesInvoiceItem> salesInvoiceItemList = new ArrayList<>();
    if (salesInvoice != null && importSalesItemList != null) {
      for (ImportSalesItem importSalesItem : importSalesItemList) {
        ProductDetailSales detailSales = null;
        SalesInvoiceItem salesInvoiceItem = new SalesInvoiceItem();
        salesInvoiceItem.setProduct(ImportSalesService.getProductId(main, importSalesItem.getProductName(), getCompany().getId()));
        salesInvoiceItem.setSalesInvoiceId(salesInvoice);
        salesInvoiceItem.setProductDetailSalesList(SalesInvoiceService.selectProductDetailSales(main, salesInvoiceItem));
        salesInvoiceItem.setProductDetailSales(new ProductDetailSales());
        boolean batchAvailable = false;
        if (importSalesItem.getBatchNo() != null) {      // If Batch is provided
          DateFormat dateFormat = new SimpleDateFormat("MMM-yy");
          for (ProductDetailSales pd : salesInvoiceItem.getProductDetailSalesList()) {
            if (pd.getBatchNo().trim().equals(importSalesItem.getBatchNo().trim())
                    && importSalesItem.getExpiryDate() != null && pd.getExpiryDate().equals(dateFormat.format(importSalesItem.getExpiryDate()))
                    && importSalesItem.getMrp() != null && pd.getMrpValue().equals(importSalesItem.getMrp())) {
              batchAvailable = true;
              detailSales = pd;
            }
          }
          if (batchAvailable) {
            salesInvoiceItem.getProductDetailSales().setBatchNo(importSalesItem.getBatchNo());
            salesInvoiceItem.getProductDetailSales().setProductBatchId(ImportSalesService.getBatchId(main, importSalesItem.getBatchNo(),
                    salesInvoiceItem.getProduct().getId(), salesInvoiceItem.getValueMrp(), getCompany().getId()).getId());
            salesInvoiceItem.setProductDetailSales(updateProductDetailSales(detailSales));                               // update values in productDetailSales of salesInvoiceItem
            getTaxCalculator().updateSalesInvoiceItem(main, salesInvoiceItem);
            updateSalesInvoiceItemByImportItem(salesInvoiceItem, importSalesItem);
            if (salesInvoiceItem.getProdPieceSellingForced() == null) {
              salesInvoiceItem.setProdPieceSellingForced(detailSales.getPricelistPts());
            }
            salesInvoiceItemList.add(salesInvoiceItem);
          }
        } else {                                                        //If Batch is null
          createSalesInvoiceItemByBatch(main, salesInvoiceItemList, salesInvoiceItem, importSalesItem);
        }
      }
      int sortOrder = 1;
      for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItemList) {
        updateSalesInvoiceItem(main, salesInvoiceItem);
        SalesInvoiceService.updateProductSalesValues(main, salesInvoice, salesInvoiceItemList, salesInvoiceItem, SalesInvoiceUtil.PRODUCT_PRIMARY_QTY);
        salesInvoiceItem.setProductHash(salesInvoiceItem.getProductDetailSales().getProductHash());
        salesInvoiceItem.setSortOrder(sortOrder++);
      }
      SalesInvoiceItemService.insertOrUpdateSalesInvoiceItems(main, salesInvoice, salesInvoiceItemList, SystemConstants.DRAFT, true, getTaxCalculator());
      SalesInvoiceService.insertOrUpdate(main, salesInvoice);
    }
  }

//  --------setting batches for products without batch no (based on qty)
  private void createSalesInvoiceItemByBatch(MainView main, List<SalesInvoiceItem> salesInvoiceItemList, SalesInvoiceItem salesInvoiceItem, ImportSalesItem importSalesItem) {
    SalesInvoiceItem salesInvoiceDetail = null;
    Integer qtyEntered = importSalesItem.getQty() != null ? importSalesItem.getQty() : 0;
    Integer freeQtyEntered = importSalesItem.getFreeQty() != null ? importSalesItem.getFreeQty() : 0;
    Integer totalQty = qtyEntered + freeQtyEntered;
    for (ProductDetailSales pd : salesInvoiceItem.getProductDetailSalesList()) {
      salesInvoiceDetail = SalesInvoiceItemUtil.getSalesInvoiceItem(salesInvoiceItem);
      salesInvoiceDetail.setSalesInvoiceId(salesInvoiceItem.getSalesInvoiceId());
      salesInvoiceDetail.setProduct(salesInvoiceItem.getProduct());
      salesInvoiceDetail.setProductDetailSales(salesInvoiceItem.getProductDetailSales());
      Integer availableQty = pd.getQuantityAvailable() + pd.getQuantityFreeAvailable();
      if (totalQty > 0) {
        salesInvoiceDetail.setProductDetailSales(updateProductDetailSales(pd));                               // update values in productDetailSales of salesInvoiceItem
        getTaxCalculator().updateSalesInvoiceItem(main, salesInvoiceDetail);
        if (totalQty > availableQty) {
          if (qtyEntered > availableQty) {
            importSalesItem.setQty(availableQty);
            importSalesItem.setFreeQty(0);
            qtyEntered -= availableQty;
          } else {
            importSalesItem.setQty(qtyEntered);
            availableQty -= qtyEntered;
            if (freeQtyEntered > availableQty) {
              importSalesItem.setFreeQty(availableQty);
              qtyEntered = 0;
              freeQtyEntered -= availableQty;
            } else {
              importSalesItem.setFreeQty(freeQtyEntered);
              qtyEntered = 0;
              freeQtyEntered = 0;
            }
          }
        } else {
          importSalesItem.setQty(qtyEntered);
          importSalesItem.setFreeQty(freeQtyEntered);
          qtyEntered = 0;
          freeQtyEntered = 0;
        }
        updateSalesInvoiceItemByImportItem(salesInvoiceDetail, importSalesItem);
        if (salesInvoiceDetail.getProdPieceSellingForced() == null) {
          salesInvoiceDetail.setProdPieceSellingForced(pd.getPricelistPts());
        }
        salesInvoiceItemList.add(salesInvoiceDetail);
        totalQty = qtyEntered + freeQtyEntered;
      }
    }
  }

  private void updateSalesInvoiceItem(MainView main, SalesInvoiceItem salesInvoiceItem) {
    if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getProdPieceSellingForced() != null && salesInvoiceItem.getProdPieceSellingForced() > 0) {
      salesInvoiceItem.setSalesInvoiceId(getSalesInvoice());

      double invoiceDisc = salesInvoiceItem.getProductDetailSales().getInvoiceDiscountValue() == null ? 0 : salesInvoiceItem.getProductDetailSales().getInvoiceDiscountValue();
      double pts = salesInvoiceItem.getProductDetailSales().getPricelistPts() == null ? 0 : salesInvoiceItem.getProductDetailSales().getPricelistPts();
      salesInvoiceItem.setInvoiceDiscountActual(MathUtil.roundOff(invoiceDisc, 2));
      salesInvoiceItem.setValueProdPieceSelling(pts);
      salesInvoiceItem.setSecToPriQuantityActual(salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
      salesInvoiceItem.setTerToSecQuantityActual(salesInvoiceItem.getProductDetailSales().getTerToSecQuantity());
      salesInvoiceItem.setProductHash(salesInvoiceItem.getProductDetailSales().getProductHash());

      if (getSalesInvoice().getCustomerId().getTradeProfile() == null) {
        TradeProfile tp = TradeProfileService.selectTradeProfileByCustomer(main, getSalesInvoice().getCustomerId());
        getSalesInvoice().getCustomerId().setTradeProfile(tp);
      }
    }
  }

  private void updateSalesInvoiceItemByImportItem(SalesInvoiceItem salesInvoiceItem, ImportSalesItem importSalesItem) {
    salesInvoiceItem.setProductQty(importSalesItem.getQty());
    salesInvoiceItem.setProductQtyFree(importSalesItem.getFreeQty());
    salesInvoiceItem.setProdPieceSellingForced(importSalesItem.getRate());
    salesInvoiceItem.setProductDiscountValue(importSalesItem.getProductDiscountValue());
    salesInvoiceItem.setProductDiscountPercentage(importSalesItem.getProductDiscountPercentage());
  }

  private ProductDetailSales updateProductDetailSales(ProductDetailSales detailSales) {
    ProductDetailSales pds = new ProductDetailSales();
    pds.setBatchNo(detailSales.getBatchNo());
    pds.setProductBatchId(detailSales.getProductBatchId());
    pds.setExpiryDate(detailSales.getExpiryDate());
    pds.setPackSize(detailSales.getPackSize());
    pds.setQuantityAvailable(detailSales.getQuantityAvailable());
    pds.setQuantityFreeAvailable(detailSales.getQuantityFreeAvailable());
    pds.setSchemeDiscountPer(detailSales.getSchemeDiscountPer());
    pds.setProductDiscountPer(detailSales.getProductDiscountPer());
    pds.setPricelistPts(detailSales.getPricelistPts());
    pds.setPricelistPtr(detailSales.getPricelistPtr());
    pds.setMrpValue(detailSales.getMrpValue());
    pds.setProductHash(detailSales.getProductHash());

    return pds;
  }

// --------Saving and importing sales invoice from Excel
  public void handleFileUpload(FileUploadEvent event) {
    MainView main = Jsf.getMain();
    try (InputStream input = event.getFile().getInputstream()) {
      importSales.setLastSeq(lastSeq + 1);
      ImportUtil.insertOrUpdateImport(main, importSales);
      main.em().flush();
      uploadExcel(main, input, importSales);
      if (importSales.getStatus() == SystemConstants.IMPORT_FAILED) {
        main.error("error.import");
        setImportSalesItemList(ImportSalesService.selectAllSalesLogList(main, importSales));
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.setViewType(ViewTypes.editform);
      Jsf.update("f1");
      main.close();
    }
  }

  private void uploadExcel(MainView main, InputStream inputStream, Import importSales) throws IOException, ParseException {
    try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
      XSSFSheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rowIterator = sheet.iterator();
      rowIterator.next();
      int sheetRowNum = 1;
      List<ImportSalesItem> importSalesItemList = new ArrayList<>();
      while (rowIterator.hasNext()) {
        Cell cell = null;
        rowIterator.next();
        Row row = sheet.getRow(sheetRowNum);
        if (row != null && row.getCell(0) != null) {
          String productName = new DataFormatter().formatCellValue(row.getCell(0));
          if (!productName.isEmpty()) {
            ImportSalesItem importItem = new ImportSalesItem();
            importItem.setLineNo(sheetRowNum++);
            importItem.setProductName(row.getCell(0) == null ? null : new DataFormatter().formatCellValue(row.getCell(0)));
            importItem.setBatchNo(row.getCell(1) == null ? null : new DataFormatter().formatCellValue(row.getCell(1)));
            Date expiryDate = null;
            cell = row.getCell(2);
            if (cell != null && cell.toString().matches("((0[1-9])|(1[0-2]))/\\d{2}")) {
              String date = "01/" + cell;
              expiryDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
            }
            if (expiryDate != null) {
              importItem.setExpiryDate(expiryDate);
            }
            cell = row.getCell(3);
            if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setBoxQty(Integer.valueOf(new DataFormatter().formatCellValue(cell)));
            }
            cell = row.getCell(4);
            if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setQty((int) cell.getNumericCellValue());
            }
            cell = row.getCell(5);
            if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setFreeQty(Integer.valueOf(new DataFormatter().formatCellValue(cell)));
            }
            cell = row.getCell(6);
            if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setProductDiscountValue(cell.getNumericCellValue());
            }
            cell = row.getCell(7);
            if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setProductDiscountPercentage(cell.getNumericCellValue());
            }
            cell = row.getCell(8);
            if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setMrp(cell.getNumericCellValue());
            }
            cell = row.getCell(9);
            if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
              importItem.setRate(cell.getNumericCellValue());
            }
            importItem.setImportId(importSales);
            importSalesItemList.add(importItem);
          }
        }
      }

      errorLogNum = 0;
      ImportSalesService.validateItem(main, importSales, importSalesItemList, getCompany());
      if (importSales.getStatus() == SystemConstants.IMPORT_SUCCESS) {
        actionSalesImport(main);
      }
    }
  }

  public List<Customer> customerAuto(String filter) {
    if (getAccountGroup() != null) {
      return ScmLookupExtView.selectCustomerByAccountGroup(getAccountGroup(), filter);
    }
    return null;
  }

  public List<AccountGroupPriceList> accountGroupPriceListByAccountGroup() {
    List<AccountGroupPriceList> accountGroupPriceList = null;
    if (getAccountGroup() != null) {
      accountGroupPriceList = ScmLookupExtView.accountGroupPriceListByAccountGroup(getAccountGroup());
      if (accountGroupPriceList != null && !accountGroupPriceList.isEmpty() && accountGroupPriceList.size() == 1 && getImportSales().getAccountGroupPriceListId() == null) {
        getImportSales().setAccountGroupPriceListId(accountGroupPriceList.get(0));
      }
    }
    return accountGroupPriceList;
  }

  public void customerSelectedEvent(SelectEvent event) {
    Customer cust = (Customer) event.getObject();
    MainView main = Jsf.getMain();
    try {
      if (cust != null) {
        SalesInvoice salesInvoice = new SalesInvoice();
        SalesInvoiceService.customerSelectEvent(main, salesInvoice, getCompany(), getAccountGroup(), cust);
        getImportSales().setSalesCreditDays(salesInvoice.getSalesCreditDays());
        getImportSales().setAccountGroupPriceListId(salesInvoice.getAccountGroupPriceListId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void entryDateSelectedEvent(SelectEvent event) {
    Date entryDate = (Date) event.getObject();
    if (entryDate != null) {
      getImportSales().setEntryDate(entryDate);
    }
  }

  public void priceListSelectedEvent(SelectEvent event) {
    AccountGroupPriceList priceList = (AccountGroupPriceList) event.getObject();
    if (priceList != null) {
      getImportSales().setAccountGroupPriceListId(priceList);
    }
  }

  public boolean isImportable() {
    if (importSales.getCustomerId() != null && importSales.getEntryDate() != null && importSales.getAccountGroupPriceListId() != null) {
      importable = true;
    } else {
      importable = false;
    }
    return importable;
  }

  //  -------Error Handling Section
  public void updateLineItem(MainView main, ImportSalesItem importSalesItem) {
    try {
      ImportSalesService.insertOrUpdateImportSalesItem(main, importSalesItem);
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public List<Product> completeProduct(String filter) {
    List<Product> productList = null;
    MainView main = Jsf.getMain();
    try {
      if (getSalesInvoice().getAccountGroupId() != null) {
        productList = getTaxCalculator().selectProductForSales(main, getSalesInvoice(), filter);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return productList;
  }

  public List<ProductBatch> completeBatch(String filter, ImportSalesItem importSalesItem) {
    List<ProductBatch> productBatchList = null;
    MainView main = Jsf.getMain();
    try {
      importSalesItem.setProduct(ImportUtil.getProductId(main, importSalesItem.getProductName(), getCompany().getId()));
      if (getSalesInvoice().getAccountGroupId() != null) {
        productBatchList = ImportSalesService.productBatchList(main, importSales, importSalesItem);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return productBatchList;
  }

  public void batchSelectEvent(MainView main) {
    try {
      ImportSalesItem importSalesItem = (ImportSalesItem) Jsf.getAttribute("selectItem");
      importSalesItem.setProductDetailSales(ImportSalesService.productDetailSalesList(main, importSales, importSalesItem));
      importSalesItem.setQtyAvailable(importSalesItem.getProductDetailSales().getQuantityAvailable());
      importSalesItem.setExpiryDate(importSalesItem.getProductDetailSales().getExpiryDateActual());
      importSalesItem.setMrp(importSalesItem.getProductDetailSales().getMrpValue());
      importSalesItem.setSecToPriQuantity(importSalesItem.getProductDetailSales().getSecToPriQuantity());
      if (importSalesItem.getRate() == null) {
        importSalesItem.setRate(importSalesItem.getProductDetailSales().getPricelistPts());
      }
      updateLineItem(main, importSalesItem);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

//  Delete Import
  public void deleteImportSalesList(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(importSalesListSelected)) {
        for (Import importSales : importSalesListSelected) {
          ImportSalesService.deleteByPk(main, importSales);
        }
      } else {
        ImportSalesService.deleteByPk(main, getImportSales());
        main.setViewType(ViewTypes.list);
      }
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public void deleteImportSalesItem(MainView main) {
    try {
      for (ImportSalesItem importSalesItem : selectedSalesItemList) {
        ImportSalesService.deleteItemByPk(main, importSalesItem);
      }
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public void updateBoxQty(MainView main, ImportSalesItem importSalesItem) {
    try {
      ImportSalesService.changeSectoPrimaryQty(importSalesItem);
      importSalesItem.setFreeQty(null);
      updateLineItem(main, importSalesItem);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

//  -------Create Excel Format for Sales
  public void downloadExcelFormat(MainView main) {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      String name = "import_sales";
      XSSFSheet importsheet = workbook.createSheet(name);
      createSalesImportFormat(importsheet, workbook);
      XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(importsheet);
      List<Product> productList = new ArrayList<>();
      if (getAccountGroup() != null) {
        productList = completeProduct("");
        String[] product = new String[productList.size()];
        if (productList != null) {
          int i = 0;
          for (Product p : productList) {
            product[i] = p.getProductName();
            i++;
          }
        }
        if (productList.size() > 1) {
          XSSFDataValidationConstraint dvConstraintBeneficiary = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(product);
          XSSFDataValidation validation1 = (XSSFDataValidation) dvHelper.createValidation(dvConstraintBeneficiary, new CellRangeAddressList(1, 100, 0, 0));
          validation1.setShowErrorBox(true);
          importsheet.addValidationData(validation1);
        }
      }
      ExcelUtil.write(name + ".xlsx", workbook);
    } catch (Throwable t) {
      main.rollback(t, "error.download");
    } finally {
      main.close();
    }
  }

  private void createSalesImportFormat(XSSFSheet sheet, XSSFWorkbook workbook) {
    sheet.setDefaultColumnWidth(15);
    CellStyle rightAlignBold = ExcelUtil.styleBold(workbook, HorizontalAlignment.RIGHT);
    String[] arr = new String[]{"product_name", "batch_no", "expiry_date (mm/yy)", "box_qty", "qty", "free_qty", "product_discount_value", "product_discount_percentage", "mrp", "rate"};
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
  }

//  ------Getters and Setters
  public Long getRowKey() {
    return new Date().getTime();
  }

  public int getErrorLogNum() {
    errorLogNum = 0;
    if (getImportSalesItemList() != null) {
      for (ImportSalesItem item : importSalesItemList) {
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

  public AccountGroup getAccountGroup() {
    return UserRuntimeView.instance().getAccountGroup();
  }

  public Contract getContract() {
    return UserRuntimeView.instance().getContract();
  }

  public List<ImportSalesItem> getImportSalesItemList() {
    return importSalesItemList;
  }

  public void setImportSalesItemList(List<ImportSalesItem> importSalesItemList) {
    this.importSalesItemList = importSalesItemList;
  }

  public List<ImportSalesItem> getSelectedSalesItemList() {
    return selectedSalesItemList;
  }

  public void setSelectedSalesItemList(List<ImportSalesItem> selectedSalesItemList) {
    this.selectedSalesItemList = selectedSalesItemList;
  }

  public TaxCalculator getTaxCalculator() {
    if (getImportSales() != null && getImportSales().getTaxProcessorId() != null) {
      taxCalculator = SystemRuntimeConfig.getTaxCalculator(getImportSales().getTaxProcessorId().getProcessorClass());
    }
    return taxCalculator;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public Import getImportSales() {
    if (importSales == null) {
      importSales = new Import();
    }
    return importSales;
  }

  public void setImportSales(Import importSales) {
    this.importSales = importSales;
  }

  public SalesInvoice getSalesInvoice() {
    if (salesInvoice == null) {
      salesInvoice = new SalesInvoice();
      salesInvoice.setAccountGroupId(getAccountGroup());
    }
    return salesInvoice;
  }

  public void setSalesInvoice(SalesInvoice salesInvoice) {
    this.salesInvoice = salesInvoice;
  }

  public int getLastSeq() {
    return lastSeq;
  }

  public void setLastSeq(int lastSeq) {
    this.lastSeq = lastSeq;
  }

  public LazyDataModel<Import> getImportSalesLazyModel() {
    return importSalesLazyModel;
  }

  public void setImportSalesLazyModel(LazyDataModel<Import> importSalesLazyModel) {
    this.importSalesLazyModel = importSalesLazyModel;
  }

  public List<Import> getImportSalesListSelected() {
    return importSalesListSelected;
  }

  public void setImportSalesListSelected(List<Import> importSalesListSelected) {
    this.importSalesListSelected = importSalesListSelected;
  }

//  public void fromRmdyToIce(FileUploadEvent event) throws IOException {
//    InputStream input = event.getFile().getInputstream();
//    MainView main = Jsf.getMain();
//    try (XSSFWorkbook workbook = new XSSFWorkbook(input)) {
//      XSSFSheet sheet = workbook.getSheetAt(0);
//      Iterator<Row> rowIterator = sheet.iterator();
//      rowIterator.next();
//      int sheetRowNum = 6;
//      String productName = "";
//      String batchName = "";
//      List<ProductComparison> productComparisonList = new ArrayList<>();
//      while (rowIterator.hasNext()) {
//        Cell cell = null;
//        rowIterator.next();
//        Row row = sheet.getRow(sheetRowNum);
//        if (row != null) {
//          if (row != null && row.getCell(0) != null) {
//            productName = row.getCell(0).getStringCellValue();
//          } else if (productName.toUpperCase().contains("TOTAL")) {
//            productName = "";
//          }
//          cell = row.getCell(2);
//          if (row != null && cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
//            batchName = (row != null && cell != null) ? new DataFormatter().formatCellValue(cell) : "";
//          } else {
//            batchName = (row != null && cell != null) ? cell.getStringCellValue() : "";
//          }
//          if (!productName.toUpperCase().contains("TOTAL") && !batchName.toUpperCase().contains("TOTAL")) {
//            ProductComparison productComparison = new ProductComparison();
//            productComparison.setRmdyProductName(productName);
//            cell = row.getCell(2);
//            if (row != null && cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) { 
//              productComparison.setRmdyBatch(cell == null ? null : new DataFormatter().formatCellValue(cell));
//            } else {
//              productComparison.setRmdyBatch(cell == null ? null : cell.getStringCellValue());
//            }
//            productComparison.setProductName(productName);
//            cell = row.getCell(6);
//            if (row != null && cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
//              productComparison.setRmdyBatch(cell == null ? null : new DataFormatter().formatCellValue(cell));
//            } else {
//              productComparison.setRmdyBatch(cell == null ? null : cell.getStringCellValue());
//            }
//            productComparisonList.add(productComparison);
//          }
//        }
//      }
//      for (ProductComparison productComparison : productComparisonList) {
//        ImportSalesService.addProductComparison(main, productComparison);
//      }
//    } catch (Exception t) {
//      main.error("error.save");
//    }
//  }
}
