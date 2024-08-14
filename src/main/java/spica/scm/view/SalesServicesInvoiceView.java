/*
 * @(#)SalesInvoiceServiceView.java	1.0 Wed Jan 31 15:55:49 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.fin.view.FinLookupView;
import spica.fin.domain.AccountingLedger;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesServicesInvoice;
import spica.scm.domain.SalesServicesInvoiceItem;
import spica.fin.domain.TaxCode;
import spica.scm.service.CommodityService;
import spica.scm.service.SalesServicesInvoiceItemService;
import spica.scm.service.SalesServicesInvoiceService;
import spica.scm.tax.TaxCalculator;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import static spica.sys.SystemRuntimeConfig.SDF_YYYY_MM_DD;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * SalesInvoiceServiceView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Jan 31 15:55:49 IST 2018
 */
@Named(value = "salesServicesInvoiceView")
@ViewScoped
public class SalesServicesInvoiceView implements Serializable {

  private transient SalesServicesInvoice salesServicesInvoice;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesServicesInvoice> salesServicesInvoiceLazyModel; 	//For lazy loading datatable.
  private transient SalesServicesInvoice[] salesServicesInvoiceSelected;	 //Selected Domain Array  
  private transient List<SalesServicesInvoiceItem> salesServicesInvoiceItemList;
  private transient TaxCalculator taxCalculator;
  private transient boolean localServices;
  private transient Integer salesArea;
  private transient boolean interstateServices;
  private transient boolean intrastateServices;
  private transient boolean internationalServices;
  private transient boolean taxableServicesExist;

  @PostConstruct
  public void init() {
    Integer invoiceId = (Integer) Jsf.popupParentValue(Integer.class);
    if (invoiceId != null) {
      getSalesServicesInvoice().setId(invoiceId);
    }
  }

  /**
   * Default Constructor.
   */
  public SalesServicesInvoiceView() {
    super();
  }

  /**
   * Return SalesServicesInvoice.
   *
   * @return SalesServicesInvoice.
   */
  public SalesServicesInvoice getSalesServicesInvoice() {
    if (salesServicesInvoice == null) {
      salesServicesInvoice = new SalesServicesInvoice();
    }
    if (salesServicesInvoice.getCompanyId() == null) {
      salesServicesInvoice.setCompanyId(UserRuntimeView.instance().getCompany());
      salesServicesInvoice.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    return salesServicesInvoice;
  }

  /**
   * Set SalesServicesInvoice.
   *
   * @param salesServicesInvoice.
   */
  public void setSalesServicesInvoice(SalesServicesInvoice salesServicesInvoice) {
    this.salesServicesInvoice = salesServicesInvoice;
  }

  public List<SalesServicesInvoiceItem> getSalesServicesInvoiceItemList() {
    if (salesServicesInvoiceItemList == null) {
      salesServicesInvoiceItemList = new ArrayList<>();
    }
    return salesServicesInvoiceItemList;
  }

  public void setSalesServicesInvoiceItemList(List<SalesServicesInvoiceItem> salesServicesInvoiceItemList) {
    this.salesServicesInvoiceItemList = salesServicesInvoiceItemList;
  }

  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }

  /**
   * Return LazyDataModel of SalesServicesInvoice.
   *
   * @return
   */
  public LazyDataModel<SalesServicesInvoice> getSalesServicesInvoiceLazyModel() {
    return salesServicesInvoiceLazyModel;
  }

  /**
   * Return SalesServicesInvoice[].
   *
   * @return
   */
  public SalesServicesInvoice[] getSalesServicesInvoiceSelected() {
    return salesServicesInvoiceSelected;
  }

  /**
   * Set SalesServicesInvoice[].
   *
   * @param salesServicesInvoiceSelected
   */
  public void setSalesServicesInvoiceSelected(SalesServicesInvoice[] salesServicesInvoiceSelected) {
    this.salesServicesInvoiceSelected = salesServicesInvoiceSelected;
  }

  public boolean isLocalServices() {
    return localServices;
  }

  public void setLocalServices(boolean localServices) {
    this.localServices = localServices;
  }

  public boolean isInterstateServices() {
    return interstateServices;
  }

  public void setInterstateServices(boolean interstateServices) {
    this.interstateServices = interstateServices;
  }

  public boolean isInternationalServices() {
    return internationalServices;
  }

  public void setInternationalServices(boolean internationalServices) {
    this.internationalServices = internationalServices;
  }

  public boolean isIntrastateServices() {
    return intrastateServices;
  }

  public void setIntrastateServices(boolean intrastateServices) {
    this.intrastateServices = intrastateServices;
  }

  public Integer getSalesArea() {
    return salesArea;
  }

  public void setSalesArea(Integer salesArea) {
    this.salesArea = salesArea;
  }

  public boolean isTaxableServicesExist() {
    return taxableServicesExist;
  }

  public void setTaxableServicesExist(boolean taxableServicesExist) {
    this.taxableServicesExist = taxableServicesExist;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesServicesInvoice(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getSalesServicesInvoice().reset();
          setSalesServicesInvoiceItemList(null);
          getSalesServicesInvoice().setCompanyId(UserRuntimeView.instance().getCompany());
          getSalesServicesInvoice().setAccountGroupId(UserRuntimeView.instance().getAccountGroup());
          getSalesServicesInvoice().setTaxableInvoice(SalesServicesInvoiceService.TAXABLE_SALES_SERVICES_INVOICE);
          getSalesServicesInvoice().setTaxProcessorId(getSalesServicesInvoice().getCompanyId().getCountryTaxRegimeId().getTaxProcessorId());
          getSalesServicesInvoice().setEntryDate(new Date());
          setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesServicesInvoice().getTaxProcessorId().getProcessorClass()));
        } else if (main.isEdit() && !main.hasError()) {
          setSalesServicesInvoice((SalesServicesInvoice) SalesServicesInvoiceService.selectByPk(main, getSalesServicesInvoice()));
          setSalesServicesInvoiceItemList(SalesServicesInvoiceItemService.selectBySalesInvoiceService(main, getSalesServicesInvoice()));
          setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesServicesInvoice().getTaxProcessorId().getProcessorClass()));
          setSalesServicesType(getSalesServicesInvoice().getAccountingLedgerId());
          if (getSalesServicesInvoice().getAccountGroupId() == null) {
            getSalesServicesInvoice().setAccountGroupId(UserRuntimeView.instance().getAccountGroup());
          }
          if (StringUtil.isEmpty(getSalesServicesInvoiceItemList())) {
            addNewSalesServicesInvoiceItem();
          } else {
            double taxSum = getSalesServicesInvoiceItemList().stream().map(SalesServicesInvoiceItem::getIgstId).map(TaxCode::getRatePercentage).collect(Collectors.summingDouble(i -> i));
            if (taxSum > 0) {
              setTaxableServicesExist(true);
            }
            getTaxCalculator().processSalesServicesInvoiceCalculation(salesServicesInvoice, salesServicesInvoiceItemList);
          }
        } else if (main.isList()) {
          setSalesServicesInvoiceItemList(null);
          setTaxCalculator(null);
          setTaxableServicesExist(false);
          loadSalesInvoiceServiceList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create salesInvoiceServiceLazyModel.
   *
   * @param main
   */
  private void loadSalesInvoiceServiceList(final MainView main) {
    if (salesServicesInvoiceLazyModel == null) {
      salesServicesInvoiceLazyModel = new LazyDataModel<SalesServicesInvoice>() {
        private List<SalesServicesInvoice> list;

        @Override
        public List<SalesServicesInvoice> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (UserRuntimeView.instance().getCompany() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = SalesServicesInvoiceService.listPagedByCompanyAndAccountGroup(main, UserRuntimeView.instance().getCompany(), UserRuntimeView.instance().getAccountGroup());
              main.commit(salesServicesInvoiceLazyModel, first, pageSize);
            }
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesServicesInvoice salesServicesInvoice) {
          return salesServicesInvoice.getId();
        }

        @Override
        public SalesServicesInvoice getRowData(String rowKey) {
          if (list != null) {
            for (SalesServicesInvoice obj : list) {
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

  private void uploadFiles() {
    String SUB_FOLDER = "scm_sales_invoice_service/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesServicesInvoice(MainView main, int status) {
    getSalesServicesInvoice().setStatusId(status);
    return saveOrCloneSalesServicesInvoice(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesServicesInvoice(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneSalesServicesInvoice(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesServicesInvoice(MainView main, String key) {
    // Account accountId = null;
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            manageEntryDate();
            SalesServicesInvoiceService.insertOrUpdate(main, getSalesServicesInvoice());
            SalesServicesInvoiceItemService.insertOrUpdate(main, getSalesServicesInvoiceItemList());
            if (SystemConstants.CONFIRMED.intValue() == getSalesServicesInvoice().getStatusId()) {
              getTaxCalculator().saveSalesServicesInvoice(main, getSalesServicesInvoice());
            }
            break;
          case "clone":
            SalesServicesInvoiceService.clone(main, getSalesServicesInvoice());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many SalesServicesInvoice.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesServicesInvoice(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(salesServicesInvoiceSelected)) {
        SalesServicesInvoiceService.deleteByPkArray(main, getSalesServicesInvoiceSelected()); //many record delete from list
        main.commit("success.delete");
        salesServicesInvoiceSelected = null;
      } else {
        SalesServicesInvoiceService.deleteByPk(main, getSalesServicesInvoice());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
          main.setViewType(ViewTypes.newform);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  public List<AccountingLedger> accountingLedgerAuto(String filter) {
//    return FinLookupView.ledgerDebtorsAndCreditorsAuto(filter, UserRuntimeView.instance().getCompany().getId());
    return FinLookupView.ledgerDebtorsAuto(filter, UserRuntimeView.instance().getCompany().getId());
  }

  public void addNewSalesServicesInvoiceItem() {
    SalesServicesInvoiceItem item = new SalesServicesInvoiceItem();
    item.setSalesServicesInvoiceId(getSalesServicesInvoice());
    getSalesServicesInvoiceItemList().add(0, item);
  }

  public List<ServiceCommodity> commodityServiceAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      if (getCompany() != null) {
        //list = ScmLookupExtView.commodityServiceAuto(filter, getCompany());
        return CommodityService.getServices(main, getCompany(), filter);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void openServicesPopup() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      // opens a new form if id is null else edit
      Jsf.popupForm(FileConstant.SERVICES, new ServiceCommodity(), null);
    }
  }

  public List<AccountingLedger> accountingLedgerSalesServicesAuto(String filter) {
    return FinLookupView.ledgerSalesServiceAuto(filter, getCompany().getId());
  }

  public boolean isDraft() {
    return getSalesServicesInvoice() != null && getSalesServicesInvoice().getStatusId() != null && SystemConstants.DRAFT.intValue() == getSalesServicesInvoice().getStatusId();
  }

  public boolean isConfirmed() {
    return getSalesServicesInvoice() != null && getSalesServicesInvoice().getStatusId() != null && SystemConstants.CONFIRMED.intValue() == getSalesServicesInvoice().getStatusId();
  }

  private void setSalesServicesType(AccountingLedger accountingLedger) {
    setInternationalServices(false);
    setInterstateServices(false);
    setIntrastateServices(false);
    setSalesArea(null);
    if (accountingLedger != null) {
      if (accountingLedger.getCountryId() != null && accountingLedger.getCompanyId() != null && accountingLedger.getCompanyId().getId().equals(getCompany().getId())) {
        if (accountingLedger.getStateId() != null) {
          if (accountingLedger.getStateId().getId().equals(getCompany().getStateId().getId())) {
            setIntrastateServices(true);
            setSalesArea(SalesServicesInvoiceService.SALES_INTRASTATE);
          } else {
            setInterstateServices(true);
            setSalesArea(SalesServicesInvoiceService.SALES_INTERSTATE);
          }
        }
      }
    }
    getSalesServicesInvoice().setSalesArea(getSalesArea());
  }

  /**
   *
   * @param main
   */
  public void insertOrUpdateSalesInvoiceServiceItem(MainView main) {
    boolean isNew = false;
    try {
      Integer index = Jsf.getParameterInt("rownumber");
      SalesServicesInvoiceItem salesServicesInvoiceItem = getSalesServicesInvoiceItemList().get(index);

      if (salesServicesInvoiceItem != null && salesServicesInvoiceItem.getCommodityId() != null && salesServicesInvoiceItem.getTaxableValue() != null) {
        if (salesServicesInvoiceItem.getId() == null) {
          isNew = true;
        }
        getTaxCalculator().processSalesServicesInvoiceCalculation(salesServicesInvoice, salesServicesInvoiceItemList);
        salesServicesInvoiceItem = salesServicesInvoiceItemList.get(index);
        SalesServicesInvoiceItemService.insertOrUpdate(main, salesServicesInvoiceItem);

        if (!isTaxableServicesExist() && salesServicesInvoiceItem.getIgstId().getRatePercentage() > 0) {
          setTaxableServicesExist(true);
        }

        if (isNew) {
          addNewSalesServicesInvoiceItem();
        } else if (index == 0) {
          addNewSalesServicesInvoiceItem();
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param main
   * @param salesServicesInvoiceItem
   */
  public void actionDeleteSalesServicesInvoiceItem(MainView main, SalesServicesInvoiceItem salesServicesInvoiceItem) {
    try {
      if (salesServicesInvoiceItem != null && salesServicesInvoiceItem.getId() != null && salesServicesInvoiceItem.getId() > 0) {
        SalesServicesInvoiceItemService.deleteByPk(main, salesServicesInvoiceItem);
        getSalesServicesInvoiceItemList().remove(salesServicesInvoiceItem);
        getTaxCalculator().processSalesServicesInvoiceCalculation(salesServicesInvoice, salesServicesInvoiceItemList);
      } else {
        getSalesServicesInvoiceItemList().remove(salesServicesInvoiceItem);
        getTaxCalculator().processSalesServicesInvoiceCalculation(salesServicesInvoice, salesServicesInvoiceItemList);
      }
      if (getSalesServicesInvoiceItemList().isEmpty()) {
        addNewSalesServicesInvoiceItem();
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public void servicesRateBlurEvent(SalesServicesInvoiceItem salesServicesInvoiceItem) {
    getTaxCalculator().processSalesServicesInvoiceCalculation(getSalesServicesInvoice(), getSalesServicesInvoiceItemList());
  }

  public void servicesSelectEvent(SalesServicesInvoiceItem salesServicesInvoiceItem) {
    if (salesServicesInvoiceItem.getCommodityId() != null) {
      salesServicesInvoiceItem.setTaxableValue(null);
      salesServicesInvoiceItem.setIgstId(salesServicesInvoiceItem.getCommodityId().getSalesTaxCodeId());
      if (getSalesServicesInvoice().getTdsTaxCodeId() != null) {
        salesServicesInvoiceItem.setTdsTaxCodeId(getSalesServicesInvoice().getTdsTaxCodeId());
      } else {
        salesServicesInvoiceItem.setTdsTaxCodeId(salesServicesInvoiceItem.getCommodityId().getTdsTaxCodeId());
      }
    } else {
      salesServicesInvoiceItem.setTaxableValue(null);
      salesServicesInvoiceItem.setCgstAmount(null);
      salesServicesInvoiceItem.setSgstAmount(null);
      salesServicesInvoiceItem.setIgstAmount(null);
      salesServicesInvoiceItem.setNetValue(null);
      salesServicesInvoiceItem.setIgstId(null);
      salesServicesInvoiceItem.setTdsTaxCodeId(null);
      salesServicesInvoiceItem.setTdsAmount(null);
    }
    getTaxCalculator().processSalesServicesInvoiceCalculation(getSalesServicesInvoice(), getSalesServicesInvoiceItemList());
  }

  public void ledgerSelectEvent(SelectEvent event) {
    AccountingLedger al = (AccountingLedger) event.getObject();
    setSalesServicesType(al);
    setSalesServicesInvoiceNo(getSalesServicesInvoice());

  }

  public void taxableInvoiceChangeEvent(ValueChangeEvent event) {
    Integer taxableInvoice = (Integer) event.getNewValue();
    getSalesServicesInvoice().setTaxableInvoice(taxableInvoice);
    setSalesServicesInvoiceNo(getSalesServicesInvoice());
  }

  private void setSalesServicesInvoiceNo(SalesServicesInvoice salesServicesInvoice) {
    MainView main = Jsf.getMain();
    try {
      getSalesServicesInvoice().setSerialNo(SalesServicesInvoiceService.selectSalesServicesInvoiceNo(main, salesServicesInvoice, true));
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<TaxCode> lookupTdsTaxCode() {
    if (getCompany() != null) {
      return ScmLookupExtView.lookupTdsTaxCode(getCompany());
    }
    return null;
  }

  public void print() {
    Jsf.popupForm(getTaxCalculator().getSalesServicesInvoicePrintIText(), getSalesServicesInvoice());
  }

  private void manageEntryDate() throws ParseException {
    if (new SimpleDateFormat("HH").format(getSalesServicesInvoice().getEntryDate()).equals("00") && getSalesServicesInvoice().getCreatedAt() == null) {
      String entryDate = SDF_YYYY_MM_DD.format(getSalesServicesInvoice().getEntryDate());
      String entryTime = new SimpleDateFormat("hh:mm:ss").format(new Date());
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      getSalesServicesInvoice().setEntryDate(formatter.parse(entryDate + " " + entryTime));
    } else if (getSalesServicesInvoice().getCreatedAt() != null) {
      String entryDate = SDF_YYYY_MM_DD.format(getSalesServicesInvoice().getEntryDate());
      String entryTime = new SimpleDateFormat("hh:mm:ss").format(getSalesServicesInvoice().getCreatedAt());
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      getSalesServicesInvoice().setEntryDate(formatter.parse(entryDate + " " + entryTime));
    }
  }

  public void actionResetToDraft(MainView main) {
    try {
      getSalesServicesInvoice().setStatusId(SystemConstants.DRAFT);
      saveOrCloneSalesServicesInvoice(main, "save");
      SalesServicesInvoiceService.resetToDraftSalesServiceItem(main, getSalesServicesInvoice());
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }
}
