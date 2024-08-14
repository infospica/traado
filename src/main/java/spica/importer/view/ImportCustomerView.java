/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.importer.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import spica.scm.domain.Company;
import spica.scm.domain.Country;
import spica.scm.domain.Currency;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.CustomerImporter;
import spica.scm.domain.District;
import spica.scm.domain.State;
import spica.scm.domain.Territory;
import spica.scm.domain.TradeProfile;
import spica.scm.export.ExcelSheet;
import spica.scm.service.CustomerImporterService;
import spica.scm.service.ImportCustomerService;
import spica.scm.view.ScmLookupExtView;
import spica.scm.view.ScmLookupView;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "importCustomerView")
@ViewScoped
public class ImportCustomerView implements Serializable {

  private List<CustomerImporter> customerImportLogList;
  private List<CustomerImporter> selectedCustomer;
  private int activeIndex = 0;
  private int errorLogNum = 0;

  public void actionCustomerImport(MainView main) {
    try {
      List<CustomerImporter> customerImporterList = CustomerImporterService.selectAllCustomerImporter(main, getCompany());
      for (CustomerImporter list : customerImporterList) {
        ImportCustomerService.validateProduct(main, list, getCompany());
      }
      setCustomerImportLogList(null);
      if (getCustomerLogErrorList(main).isEmpty()) {
        actionInsertCustomer(main);
        main.commit("success.import");
      }
    } catch (Exception t) {
      main.rollback(t, "error.import");
    } finally {
      main.close();
    }
  }

  public void downloadExcelFormat(MainView main) throws IOException {
    try {
      ExcelSheet.createCustomerExport(main, null, null, true);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<CustomerImporter> getCustomerLogErrorList(MainView main) {
    try {
      if (customerImportLogList == null) {
        customerImportLogList = ImportCustomerService.selectCustomerLogList(main, getCompany(), SystemConstants.IMPORT_FAILED);
      }
      setErrorLogNum(customerImportLogList.size());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return customerImportLogList;
  }

  public void setCustomerImportLogList(List<CustomerImporter> customerImportLogList) {
    this.customerImportLogList = customerImportLogList;
  }

  public Long getRowKey() {
    return new Date().getTime();
  }

  public List<CustomerImporter> getSelectedCustomer() {
    return selectedCustomer;
  }

  public void setSelectedCustomer(List<CustomerImporter> selectedCustomer) {
    this.selectedCustomer = selectedCustomer;
  }

  public void reset() {
    customerImportLogList = null;
    selectedCustomer = null;
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  public void handleFileUpload(FileUploadEvent event) {
    MainView main = Jsf.getMain();
    try {
      ImportCustomerService.deleteCustomerImport(main, UserRuntimeView.instance().getCompany());
      InputStream input = event.getFile().getInputstream();
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
    List<CustomerImporter> importCustomerList = new ArrayList<>();
    while (rowIterator.hasNext()) {
      rowIterator.next();
      Row row = sheet.getRow(sheetRowNum);
      if (row != null) {
        Cell cell = row.getCell(0);
        if (cell != null) {
          CustomerImporter customerImporter = new CustomerImporter();
          customerImporter.setLineNo(sheetRowNum);
          customerImporter.setCustomerName(cell == null ? null : new DataFormatter().formatCellValue(cell));
          cell = row.getCell(1);
          Country country = null;
          if (cell != null) {
            country = ImportCustomerService.findCountryId(main, new DataFormatter().formatCellValue(cell));
          }
          customerImporter.setCountryId(country);
          cell = row.getCell(2);
          State state = null;
          if (cell != null) {
            state = ImportCustomerService.findStateId(main, new DataFormatter().formatCellValue(cell), country);
          }
          customerImporter.setStateId(state);
          cell = row.getCell(3);
          District district = null;
          if (cell != null) {
            district = ImportCustomerService.findDistrictId(main, new DataFormatter().formatCellValue(cell), state);
          }
          customerImporter.setDistrictId(district);
          cell = row.getCell(4);
          Territory territory = null;
          if (cell != null) {
            territory = ImportCustomerService.findTerritoryId(main, new DataFormatter().formatCellValue(cell), country, getCompany());
          }
          customerImporter.setTerritoryId(territory);
          cell = row.getCell(5);
          if (cell != null) {
            customerImporter.setCitOrTown(new DataFormatter().formatCellValue(cell));
          }
          cell = row.getCell(6);
          if (cell != null) {
            customerImporter.setPanNo(new DataFormatter().formatCellValue(cell));
          }
          cell = row.getCell(7);
          if (cell != null) {
            customerImporter.setGstNo(new DataFormatter().formatCellValue(cell));
          }
          cell = row.getCell(8);
          if (cell != null) {
            customerImporter.setAddress(new DataFormatter().formatCellValue(cell));
          }
          cell = row.getCell(9);
          if (cell != null) {
            customerImporter.setPin(new DataFormatter().formatCellValue(cell));
          }
          cell = row.getCell(10);
          if (cell != null) {
            customerImporter.setPhone1(new DataFormatter().formatCellValue(cell));
          }
          cell = row.getCell(11);
          if (cell != null) {
            customerImporter.setPhone2(new DataFormatter().formatCellValue(cell));
          }
          cell = row.getCell(12);
          if (cell != null) {
            customerImporter.setPhone3(new DataFormatter().formatCellValue(cell));
          }
          cell = row.getCell(13);
          if (cell != null) {
            customerImporter.setFax1(new DataFormatter().formatCellValue(cell));
          }
          cell = row.getCell(14);
          if (cell != null) {
            customerImporter.setEmail(new DataFormatter().formatCellValue(cell));
          }
          cell = row.getCell(15);
          if (cell != null) {
            String tradeProfile = new DataFormatter().formatCellValue(cell);
            TradeProfile tp = null;
            if (StringUtil.toUpperCase(tradeProfile).contains("STOCKIST") || StringUtil.toUpperCase(tradeProfile).contains("DISTRIBUTOR")) {
              tp = new TradeProfile(5);
            } else if (StringUtil.toUpperCase(tradeProfile).contains("CONSUMER") || StringUtil.toUpperCase(tradeProfile).contains("INDIVIDUAL")) {
              tp = new TradeProfile(8);
            } else if (StringUtil.toUpperCase(tradeProfile).contains("RETAILER")) {
              tp = new TradeProfile(7);
            } else if (StringUtil.toUpperCase(tradeProfile).contains("SUPER STOCKIEST")) {
              tp = new TradeProfile(4);
            }
            customerImporter.setCustomerType(tp);
          }
          cell = row.getCell(16);
          if (cell != null) {
            String taxZone = new DataFormatter().formatCellValue(cell);
            if (StringUtil.toUpperCase(taxZone).contains("TAXABLE")) {
              customerImporter.setTaxableZone(1);
            } else if (StringUtil.toUpperCase(taxZone).contains("SEZ")) {
              customerImporter.setTaxableZone(2);
            } else if (StringUtil.toUpperCase(taxZone).contains("TAX FREE")) {
              customerImporter.setTaxableZone(3);
            }
          } else {
            customerImporter.setTaxableZone(1);
          }
          customerImporter.setCompanyId(UserRuntimeView.instance().getCompany());
          if (customerImporter.getCustomerName() != null && !StringUtil.isEmpty(customerImporter.getCustomerName())) {
            importCustomerList.add(customerImporter);
          }
          sheetRowNum++;
        }
      }
    }
    ImportCustomerService.importCustomer(main, importCustomerList);
    errorLogNum = 0;
    for (CustomerImporter customerImporter : importCustomerList) {
      boolean validate = ImportCustomerService.validateProduct(main, customerImporter, UserRuntimeView.instance().getCompany());
      if (!validate) {
        errorLogNum++;
      }
    }
    if (errorLogNum == 0) {
      actionInsertCustomer(main);
    }

    main.commit("success.save");
  }

  public int getErrorLogNum() {
    if (customerImportLogList != null) {
      errorLogNum = customerImportLogList.size();
    }
    return errorLogNum;
  }

  public void setErrorLogNum(int errorLogNum) {
    this.errorLogNum = errorLogNum;
  }

  public void updateLineItem(MainView main, CustomerImporter customerImporter) {
    try {
      ImportCustomerService.insertOrUpdateCustomerImport(main, customerImporter);
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void deleteCustomerLogErrorList(MainView main) {
    try {
      if (selectedCustomer != null) {
        for (CustomerImporter errorList : selectedCustomer) {
          if (errorList != null && errorList.getId() != null && errorList.getId() > 0) {
            ImportCustomerService.deleteByPk(main, errorList);
            getCustomerLogErrorList(main).remove(errorList);
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void actionInsertCustomer(MainView main) {

    TradeProfile tradeProfile = null;
    CustomerAddress customerAddress = null;
    try {
      List<CustomerImporter> list = ImportCustomerService.selectCustomerLogList(main, UserRuntimeView.instance().getCompany(), SystemConstants.IMPORT_SUCCESS);
      for (CustomerImporter customerImporter : list) {
        Customer customer = getCustomer(customerImporter, main);
        tradeProfile = customerImporter.getCustomerType();
        customerAddress = getCustomerAddress(customerImporter, customer);
        CustomerImporterService.insertOrUpdate(main, customer, tradeProfile, customerAddress);

      }
      //CustomerImporterService.deleteAllCustomerImporter(main);
    } catch (Throwable t) {
      main.rollback(t, "error.import");
    } finally {
      main.close();
    }
    main.commit("success.import");
  }

  private Customer getCustomer(CustomerImporter customerImporter, Main main) {
    Customer customer = new Customer();
    customer.setCompanyId(UserRuntimeView.instance().getCompany());
    customer.setCustomerName(customerImporter.getCustomerName());
    customer.setDescription(customerImporter.getCustomerName());
    customer.setCountryId(customerImporter.getCountryId());
    customer.setStateId(customerImporter.getStateId());
    customer.setPanNo(customerImporter.getPanNo());
    customer.setGstNo(customerImporter.getGstNo());
    customer.setSalesLedgerId(CustomerImporterService.getSalesLedger(main, UserRuntimeView.instance().getCompany()));
    customer.setTradeProfile(customerImporter.getCustomerType());
    customer.setCurrencyId(new Currency(1));
    return customer;
  }

  private CustomerAddress getCustomerAddress(CustomerImporter customerImporter, Customer customer) {
    CustomerAddress customerAddress = new CustomerAddress();
    customerAddress.setCustomerId(customer);
    customerAddress.setDistrictId(customerImporter.getDistrictId());
    customerAddress.setTerritoryId(customerImporter.getTerritoryId());
    customerAddress.setAddress(customerImporter.getAddress());
    customerAddress.setPin(customerImporter.getPin());
    customerAddress.setPhone1(customerImporter.getPhone1());
    customerAddress.setPhone2(customerImporter.getPhone2());
    customerAddress.setPhone3(customerImporter.getPhone3());
    customerAddress.setFax1(customerImporter.getFax1());
    customerAddress.setEmail(customerImporter.getEmail());
    return customerAddress;
  }

  public List<Country> countryAuto(String filter) {
    return ScmLookupView.countryAuto(filter);
  }

  public List<State> stateAuto(String filter, CustomerImporter customerImporter) {
    if (customerImporter.getCountryId() != null && customerImporter.getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(customerImporter.getCountryId().getId(), filter);
    }
    return null;
  }

  public List<District> districtAuto(String filter, CustomerImporter customerImporter) {
    if (customerImporter.getStateId() != null && customerImporter.getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(customerImporter.getStateId().getId(), filter);
    }
    return null;
  }

  public List<TradeProfile> getTradeProfileSelected() {
    return ScmLookupExtView.tradeProfileByCustomer();
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }
}
