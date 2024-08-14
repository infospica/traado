/*
 * @(#)SalesInvoiceView.java	1.0 Fri Dec 23 10:28:08 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.addon.notification.Notify;
import spica.addon.service.DocumentService;
import spica.constant.AccountingConstant;
import spica.fin.domain.TaxCode;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.Consignment;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.Product;
import spica.scm.domain.ProductFreeQtyScheme;
import spica.scm.domain.SalesAccount;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.SalesInvoiceStatus;
import spica.scm.domain.SalesOrder;
import spica.scm.domain.VendorContact;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.ConsignmentCommodityService;
import spica.scm.service.ConsignmentService;
import spica.fin.service.LedgerExternalService;
import spica.fin.service.TaxCodeService;
import spica.reports.model.CustomerOutstanding;
import spica.reports.model.FilterParameters;
import spica.reports.service.CustomerOutstandingService;
import spica.scm.common.InvoiceByHsn;
import spica.scm.common.ProductInvoiceDetail;
import spica.scm.common.RelatedItems;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.CompanyFinancialYear;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.ProductType;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.State;
import spica.scm.domain.TradeProfile;
import spica.scm.export.LineItemExport;
import spica.scm.service.CompanySettingsService;
import spica.scm.service.CustomerService;
import spica.scm.service.PlatformService;
import spica.scm.service.PrefixTypeService;
import spica.scm.service.SalesAgentService;
import spica.scm.service.SalesInvoiceItemService;
import spica.scm.service.SalesInvoiceLogService;
import spica.scm.service.SalesInvoiceService;
import spica.scm.service.StockPreSaleService;
import spica.scm.service.TradeProfileService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.AppUtil;
import spica.scm.util.MathUtil;
import spica.scm.util.SalesInvoiceItemUtil;
import spica.scm.util.SalesInvoiceUtil;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import static spica.sys.SystemRuntimeConfig.SDF_DD_MM_YYYY;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * SalesInvoiceView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:08 IST 2016
 */
@Named(value = "salesInvoiceGstIndiaView")
@ViewScoped
public class SalesInvoiceGstIndiaView implements Serializable {

  private transient SalesInvoice salesInvoice;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesInvoice> salesInvoiceLazyModel; 	//For lazy loading datatable.
  private transient SalesInvoice[] salesInvoiceSelected;	 //Selected Domain Array
  private transient boolean salesOrderReference;
  private transient Account account;
  private transient SalesAccount salesAccount;
  private transient List<SalesInvoiceItem> salesInvoiceItemList;
  private transient HashMap<String, Integer> productHashCode;
  private transient Integer proformaExpireDays;
  private transient Integer salesInvoiceFilter;
  private transient TaxCalculator taxCalculator;
  private transient boolean salesInvoiceEditable;

  private transient boolean proformaToInvoice;
  private transient boolean cashDiscountApplicable;
  private transient boolean cashDiscountTaxable;
  private transient boolean interstateSales;
  private transient boolean intrastateSales;
  private transient boolean internationalSales;
  private transient boolean renderPtr;
  private transient boolean renderPts;
  private transient boolean cancelable;
  private transient boolean lineItemExist;
  // private transient AccountingTransactionDetailItem accountingTransactionDetailItem;
  private transient Consignment consignment;
  private transient List<Consignment> customerConsignmentList;
  private transient String pageTitle;
  private transient List<RelatedItems> relatedItemsList;
  private boolean taxEditable;
  private List<TaxCode> taxCodeList;
  private transient boolean valueMrpChanged;
  private transient String productName;
  private transient List<ProductInvoiceDetail> productInvoiceDetailList;
  private transient Double customerOutstandingValue;
  private transient List<SalesInvoiceItem> salesInvoiceItemSelected;
  private transient List<SalesInvoice> tmpList;
  private transient int rowSize;
  private transient int start;
  private transient String sortColumn;
  private transient SortOrder sOrder;
  private String nextInvoiceNumber;
  private transient Customer filterCustomer;
  private transient Double totalSchemeDisc;
  private transient Double totalProductDisc;
  private transient Map<String, InvoiceByHsn> hsnMap;
  private Double totalSchemeDiscount;
  private Double totalInvoiceDiscount;
  private Double totalProductDiscount;
  private Double totalQty;
  private Integer nearExpiredProducts;
  private Double totalCgst;
  private Double totalSgst;
  private Double totalIgst;
  private Date initialEntryDate;
  private boolean customer;
  private boolean tradeClosed;

  @PostConstruct
  public void init() {
    Integer invoiceId = (Integer) Jsf.popupParentValue(Integer.class);
    if (invoiceId != null) {
      getSalesInvoice().setId(invoiceId);
    }
  }

  /**
   * Default Constructor.
   */
  public SalesInvoiceGstIndiaView() {
    super();
  }

  /**
   * Return SalesInvoice.
   *
   * @return SalesInvoice.
   */
  public SalesInvoice getSalesInvoice() {
    if (salesInvoice == null) {
      salesInvoice = new SalesInvoice();
    }
    if (salesInvoice.getCompanyId() == null) {
      salesInvoice.setCompanyId(UserRuntimeView.instance().getCompany());
      salesInvoice.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
      salesInvoice.setAccountGroupId(UserRuntimeView.instance().getAccountGroup());
    }
    return salesInvoice;
  }

  public boolean isTaxEditable() {
    return taxEditable;
  }

  public void setTaxEditable(boolean taxEditable) {
    this.taxEditable = taxEditable;
  }

  public List<TaxCode> getTaxCodeList() {
    return taxCodeList;
  }

  public void setTaxCodeList(List<TaxCode> taxCodeList) {
    this.taxCodeList = taxCodeList;
  }

  public String getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  public List<RelatedItems> getRelatedItemsList() {
    return relatedItemsList;
  }

  public void setRelatedItemsList(List<RelatedItems> relatedItemsList) {
    this.relatedItemsList = relatedItemsList;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public List<ProductInvoiceDetail> getProductInvoiceDetailList() {
    return productInvoiceDetailList;
  }

  public void setProductInvoiceDetailList(List<ProductInvoiceDetail> productInvoiceDetailList) {
    this.productInvoiceDetailList = productInvoiceDetailList;
  }

  /**
   * Set SalesInvoice.
   *
   * @param salesInvoice.
   */
  public void setSalesInvoice(SalesInvoice salesInvoice) {
    this.salesInvoice = salesInvoice;
  }

  public List<SalesInvoiceItem> getSalesInvoiceItemList() {
    return salesInvoiceItemList;
  }

  public void setSalesInvoiceItemList(List<SalesInvoiceItem> salesInvoiceItemList) {
    this.salesInvoiceItemList = salesInvoiceItemList;
  }

  public boolean isSalesOrderReference() {
    return salesOrderReference;
  }

  public void setSalesOrderReference(boolean salesOrderReference) {
    this.salesOrderReference = salesOrderReference;
  }

  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public List<Consignment> getCustomerConsignmentList() {
    return customerConsignmentList;
  }

  public void setCustomerConsignmentList(List<Consignment> customerConsignmentList) {
    this.customerConsignmentList = customerConsignmentList;
  }

  public boolean isValueMrpChanged() {
    return valueMrpChanged;
  }

  public void setValueMrpChanged(boolean valueMrpChanged) {
    this.valueMrpChanged = valueMrpChanged;
  }

  public Double getCustomerOutstandingValue() {
    return customerOutstandingValue;
  }

  public void setCustomerOutstandingValue(Double customerOutstandingValue) {
    this.customerOutstandingValue = customerOutstandingValue;
  }

  public Account getAccount() {
    if (account == null) {
      if (UserRuntimeView.instance().getAccount() != null) {
        setAccount(UserRuntimeView.instance().getAccount());
      }
    }
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public SalesAccount getSalesAccount() {
    return salesAccount;
  }

  public void setSalesAccount(SalesAccount salesAccount) {
    this.salesAccount = salesAccount;
  }

  public Consignment getConsignment() {
    return consignment;
  }

  public void setConsignment(Consignment consignment) {
    this.consignment = consignment;
  }

  public Integer getSalesInvoiceFilter() {
    return salesInvoiceFilter;
  }

  public void setSalesInvoiceFilter(Integer salesInvoiceFilter) {
    this.salesInvoiceFilter = salesInvoiceFilter;
  }

  public HashMap<String, Integer> getProductHashCode() {
    if (productHashCode == null) {
      productHashCode = new HashMap<>();
    }
    return productHashCode;
  }

  public void setProductHashCode(HashMap<String, Integer> productHashCode) {
    this.productHashCode = productHashCode;
  }

  public boolean isCashDiscountApplicable() {
    return cashDiscountApplicable;
  }

  public void setCashDiscountApplicable(boolean cashDiscountApplicable) {
    this.cashDiscountApplicable = cashDiscountApplicable;
  }

  public boolean isCashDiscountTaxable() {
    return cashDiscountTaxable;
  }

  public void setCashDiscountTaxable(boolean cashDiscountTaxable) {
    this.cashDiscountTaxable = cashDiscountTaxable;
  }

  public Integer getProformaExpireDays() {
    return proformaExpireDays;
  }

  public void setProformaExpireDays(Integer proformaExpireDays) {
    this.proformaExpireDays = proformaExpireDays;
  }

  public boolean isProformaToInvoice() {
    return proformaToInvoice;
  }

  public void setProformaToInvoice(boolean proformaToInvoice) {
    this.proformaToInvoice = proformaToInvoice;
  }

  public boolean isInterstateSales() {
    return interstateSales;
  }

  public void setInterstateSales(boolean interstateSales) {
    this.interstateSales = interstateSales;
  }

  public boolean isIntrastateSales() {
    return intrastateSales;
  }

  public void setIntrastateSales(boolean intrastateSales) {
    this.intrastateSales = intrastateSales;
  }

  public boolean isInternationalSales() {
    return internationalSales;
  }

  public void setInternationalSales(boolean internationalSales) {
    this.internationalSales = internationalSales;
  }

  public boolean isRenderPtr() {
    return renderPtr;
  }

  public void setRenderPtr(boolean renderPtr) {
    this.renderPtr = renderPtr;
  }

  public boolean isRenderPts() {
    return renderPts;
  }

  public void setRenderPts(boolean renderPts) {
    this.renderPts = renderPts;
  }

  public boolean isLineItemExist() {
    return lineItemExist;
  }

  public void setLineItemExist(boolean lineItemExist) {
    this.lineItemExist = lineItemExist;
  }

  public boolean isSalesInvoiceItemDeleted(SalesInvoiceItem salesInvoiceItem) {
    if (salesInvoiceItem != null && salesInvoiceItem.getProformaItemStatus() != null) {
      return ((salesInvoiceItem.getProformaItemStatus().equals(SalesInvoiceUtil.PROFORMA_ITEM_DELETED)));
    }
    return false;
  }

  public boolean isCancelable() {
    return cancelable;
  }

  public void setCancelable(boolean cancelable) {
    this.cancelable = cancelable;
  }

  /**
   * Return LazyDataModel of SalesInvoice.
   *
   * @return
   */
  public LazyDataModel<SalesInvoice> getSalesInvoiceLazyModel() {
    return salesInvoiceLazyModel;
  }

  /**
   * Return SalesInvoice[].
   *
   * @return
   */
  public SalesInvoice[] getSalesInvoiceSelected() {
    return salesInvoiceSelected;
  }

  /**
   * Set SalesInvoice[].
   *
   * @param salesInvoiceSelected
   */
  public void setSalesInvoiceSelected(SalesInvoice[] salesInvoiceSelected) {
    this.salesInvoiceSelected = salesInvoiceSelected;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesInvoice(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          StringUtil.clearList(salesInvoiceItemSelected);
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else if (UserRuntimeView.instance().getAccountGroup() == null) {
            main.error("account.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else {
            setSalesInvoiceNewFormView(main);
            generatePageTitle();
          }
          initialEntryDate = null;
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setSalesInvoiceItemList(null);
          selectSalesInvoiceForEdit(main);
          setValueMrpChanged(SalesInvoiceService.isProductValueMrpChanged(main, getSalesInvoice()));
          validateSalesInvoiceIsEditable();
          consignment = ConsignmentCommodityService.consignmentBySalesInvoice(main, getSalesInvoice());
          customerConsignmentList = ConsignmentService.selectConsignmentByCustomer(main, getSalesInvoice().getCustomerId());
          setRenderPtr(SalesInvoiceService.isPtsPtrRenderable(main, getSalesInvoice().getAccountGroupId(), false));
          setRenderPts(SalesInvoiceService.isPtsPtrRenderable(main, getSalesInvoice().getAccountGroupId(), true));
          setCancelable(isConfirmed() || isConfirmedAndPacked());
          generatePageTitle();
          if (isDraft()) {
            setCustomerOutstandingValue(selectCustomerOutstanding(main, getSalesInvoice().getCustomerId(), getSalesInvoice().getAccountGroupId()));
            //setRelatedItemsList(SalesInvoiceService.selectRelatedItemsOfSalesInvoice(main, getSalesInvoice()));
            if (getSalesInvoice().getConfirmedAt() != null) {
              initialEntryDate = getSalesInvoice().getInvoiceEntryDate();
            }
          }
          lookupGstTaxCode();
          hasLineItems();
          nextInvoiceNumber(main);
        } else if (ViewType.list.toString().equals(viewType)) {
          resetSalesInvoiceView();
          setPageTitle("Sales Invoice");
          getSalesInvoice().setCompanyId(null);
          setRelatedItemsList(null);
          setTaxCodeList(null);
          setTaxEditable(false);
          setLineItemExist(false);
          loadSalesInvoiceList(main);
          main.getPageData().setSearchKeyWord(null);
          initialEntryDate = null;
        }
        CompanySettingsService.selectIfNull(main, getSalesInvoice().getCompanyId());
        setTradeClosedValue();
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   *
   * @return
   */
  private String generatePageTitle() {
    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceType() != null) {
      setPageTitle(getSalesInvoice().getSalesInvoiceType().equals(SystemConstants.SALES_INVOICE_TYPE_PROFORMA) ? "Pro-Forma Invoice" : "Sales Invoice");
    } else {
      setPageTitle("Sales Invoice");
    }
    return "";
  }

  /**
   *
   */
  private void resetSalesInvoiceView() {
    setValueMrpChanged(false);
    setAccount(null);
    setSalesAccount(null);
    setProformaToInvoice(false);
    setSalesInvoiceItemList(null);
    getProductHashCode().clear();
    setCashDiscountApplicable(false);
    setCashDiscountTaxable(false);
    salesInvoiceEditable = true;
    setRenderPtr(false);
    setRenderPts(false);
    setProductName(null);
    setProductInvoiceDetailList(null);
    salesInvoiceLazyModel = null;
    setAccountingFinancialYear(null);
  }

  /**
   *
   */
  private void setSalesInvoiceNewFormView(MainView main) {
    getSalesInvoice().reset();
    CompanySettingsService.selectIfNull(main, getSalesInvoice().getCompanyId());
    getSalesInvoice().setDecimalPrecision(getSalesInvoice().getAccountGroupId().getCustomerDecimalPrecision() == null ? 2 : getSalesInvoice().getAccountGroupId().getCustomerDecimalPrecision());
    getSalesInvoice().setServiceAsExpense(getSalesInvoice().getAccountGroupId().getServiceAsExpense());
    getSalesInvoice().setTaxProcessorId(getSalesInvoice().getCompanyId().getCountryTaxRegimeId().getTaxProcessorId());
    setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesInvoice().getTaxProcessorId().getProcessorClass()));
    getSalesInvoice().setInvoiceEntryDate(new Date());
    getSalesInvoice().setInvoiceDate(new java.util.Date());
  }

  /**
   *
   */
  private void setSalesBusinessModes(MainView main) {
    if (getSalesInvoice() != null && getSalesInvoice().getCustomerId() != null) {
      if (getSalesInvoice().getCustomerId().getStateId() != null) {
        State customerState = getSalesInvoice().getCustomerId().getStateId();
        if (getSalesInvoice().getCustomerAddressId() != null && getSalesInvoice().getCustomerAddressId().getStateId() != null) {
          customerState = getSalesInvoice().getCustomerAddressId().getStateId();
        }
        setInternationalSales(SystemRuntimeConfig.isInternationalBusiness(getSalesInvoice().getCompanyId().getCountryId().getId(), customerState.getId()));
        setInterstateSales(SystemRuntimeConfig.isInterstateBusiness(getSalesInvoice().getCompanyId().getStateId().getStateCode(), customerState.getStateCode()));
        setIntrastateSales(SystemRuntimeConfig.isIntrastateBusiness(getSalesInvoice().getCompanyId().getStateId().getStateCode(), customerState.getStateCode()));
      }
      if (!StringUtil.isEmpty(getSalesInvoice().getCustomerId().getGstNo()) && getSalesInvoice().getSezZone() != null && getSalesInvoice().getSezZone().intValue() == 1) {
        setInterstateSales(true);
        setIntrastateSales(false);
      }
      getSalesInvoice().getCustomerId().setTradeProfile(CustomerService.getCustomerTradeProfile(main, getSalesInvoice().getCustomerId()));
    }
  }

  /**
   * Selecting the Sales invoice for editing/Updating.
   *
   * @param main
   */
  private void selectSalesInvoiceForEdit(MainView main) {
    setSalesInvoice((SalesInvoice) SalesInvoiceService.selectByPk(main, getSalesInvoice()));
    CompanySettingsService.selectIfNull(main, getSalesInvoice().getCompanyId());
    setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesInvoice().getTaxProcessorId().getProcessorClass()));
    setSalesBusinessModes(main);
    getSalesInvoice().setDecimalPrecision(getSalesInvoice().getAccountGroupId().getCustomerDecimalPrecision() == null ? 2 : getSalesInvoice().getAccountGroupId().getCustomerDecimalPrecision());
    selectSalesInvoiceItemList(main, getSalesInvoice());
    getInvoiceByHsnList(salesInvoiceItemList);
    if (StringUtil.equalsInt(getSalesInvoice().getSalesInvoiceStatusId().getId(), SystemConstants.DRAFT)) {
      if (StringUtil.equalsInt(getSalesInvoice().getServiceAsExpense(), SystemConstants.SERVICE_AS_NON_EXPENSE)) {
        getSalesInvoice().setServiceAsExpense(0);
        getSalesInvoice().setServiceId(null);
        getSalesInvoice().setServiceTaxCodeId(null);
        getSalesInvoice().setFreightRate(null);
        getSalesInvoice().setServiceValueCgst(null);
        getSalesInvoice().setServiceValueIgst(null);
        getSalesInvoice().setServiceValueSgst(null);
        getSalesInvoice().setServices2Id(null);
        getSalesInvoice().setService2TaxCodeId(null);
        getSalesInvoice().setService2Rate(null);
        getSalesInvoice().setService2Cgst(null);
        getSalesInvoice().setService2Igst(null);
        getSalesInvoice().setService2Sgst(null);
      }
    }
    if (getSalesInvoice().getCashDiscountApplicable() != null) {
      setCashDiscountApplicable(true);
      if (getSalesInvoice().getCashDiscountTaxable() != null) {
        setCashDiscountTaxable(true);
      }
    } else {
      setCashDiscountApplicable(false);
      setCashDiscountTaxable(false);
    }

    if (getSalesInvoiceItemList() != null && getSalesInvoiceItemList().isEmpty()) {
      actionAddNewLineItem();
    }

  }

  /**
   *
   * @param main
   * @param salesInvoice
   */
  private void selectSalesInvoiceItemList(MainView main, SalesInvoice salesInvoice) {
    Double tcsValue = getSalesInvoice().getTcsNetValue();
    setSalesInvoiceItemList(SalesInvoiceItemService.selectSalesInvoiceItemBySalesInvoice(main, salesInvoice, getTaxCalculator(), true));
    if (getSalesInvoice().getSalesInvoiceStatusId().getId().intValue() == SystemConstants.DRAFT) {
      getTaxCalculator().processSalesInvoiceCalculation(salesInvoice, getSalesInvoiceItemList(), true);
    } else {
      getTaxCalculator().processSalesInvoiceCalculation(salesInvoice, getSalesInvoiceItemList(), false);
      if (tcsValue != null && tcsValue != 0) {
        if (StringUtil.equalsInt(getSalesInvoice().getCompanyId().getTcsEnabled(), 1)) {
          TaxCode taxCode = TaxCodeService.selectByPk(main, AccountingConstant.TAX_CODE_TCS);
          Double customerTotalSales = CustomerService.customerTotalSalesValue(main, salesInvoice);
          getTaxCalculator().calculateTcsonSales(salesInvoice, salesInvoiceItemList, customerTotalSales, taxCode);
        }
      }
    }
  }

  /**
   * Create salesInvoiceLazyModel.
   *
   * @param main
   */
  private void loadSalesInvoiceList(final MainView main) {
    if (salesInvoiceLazyModel == null) {
      tmpList = new ArrayList<>();
      salesInvoiceLazyModel = new LazyDataModel<SalesInvoice>() {
        List<SalesInvoice> list;

        @Override
        public List<SalesInvoice> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getSalesInvoice().getAccountGroupId() != null) {
              start = first;
              rowSize = pageSize;
              sortColumn = sortField;
              sOrder = sortOrder;
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = SalesInvoiceService.listPaged(main, getSalesInvoice().getCompanyId(), getSalesInvoice().getAccountGroupId(), getSalesInvoiceFilter(), getFilterCustomer(), getSalesInvoice().getFinancialYearId());
              tmpList = list;
              main.commit(salesInvoiceLazyModel, first, pageSize);
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
        public Object getRowKey(SalesInvoice salesInvoice) {
          return salesInvoice.getId();
        }

        @Override
        public SalesInvoice getRowData(String rowKey) {
          if (list != null) {
            for (SalesInvoice obj : list) {
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

  /**
   *
   */
  private void uploadFiles() {
    String SUB_FOLDER = "scm_sales_invoice/";
  }

  /**
   *
   * @param main
   * @param status
   * @return
   */
  public String actionUpdateTaxVariation(MainView main, Integer status) {
    try {
      getSalesInvoiceItemList().forEach((sitem) -> {
        sitem.setIsTaxCodeModified(SystemConstants.TAX_CODE_MODIFIED - SystemConstants.TAX_CODE_MODIFIED);
      });
      getSalesInvoice().setIsTaxCodeModified(SystemConstants.TAX_CODE_MODIFIED - SystemConstants.TAX_CODE_MODIFIED);
      return saveSalesInvoice(main, status);
    } catch (Throwable t) {
      getSalesInvoice().setIsTaxCodeModified(SystemConstants.TAX_CODE_MODIFIED);
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Insert or update.
   *
   * @param main
   * @param status
   * @return the page to display.
   */
  public String saveSalesInvoice(MainView main, int status) {
    if ((status == SystemConstants.CONFIRMED) && !hasSalesInvoiceItem()) {
      main.error("error.product.missing");
      return null;
    }
    if (isValueMrpChanged()) {
      main.error("error.mrp.changed");
      return null;
    }
    if (status != SystemConstants.CONFIRMED) {
      getSalesInvoice().setSalesInvoiceStatusId(new SalesInvoiceStatus(status));
    }
    if (isBatchMissing()) {
      main.error("error.batch.missing");
      return null;
    }
    return saveOrCloneSalesInvoice(main, "save", status);
  }

  /**
   *
   * @param main
   * @return
   */
  public String saveProFormaInvoice(MainView main) {
    if (!hasSalesInvoiceItem()) {
      main.error("error.product.missing");
      return null;
    }
    getSalesInvoice().setSalesInvoiceType(SystemConstants.SALES_INVOICE_TYPE_PROFORMA);
    getSalesInvoice().setProformaCreatedAt(new Date());
    getSalesInvoice().setInvoiceNo("PF" + (getSalesInvoice().getInvoiceNo().substring(2, getSalesInvoice().getInvoiceNo().length())));
    if (getProformaExpireDays() != null) {
      getSalesInvoice().setProformaExpiringAt(SalesInvoiceUtil.addDaysToDate(new Date(), getProformaExpireDays()));
    }
    return saveOrCloneSalesInvoice(main, "save", SystemConstants.DRAFT);
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesInvoice(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesInvoice(main, "clone", 0);
  }

  /**
   *
   * @return
   */
  private boolean hasSalesInvoiceItem() {
    boolean rvalue = false;
    if (getSalesInvoiceItemList() != null) {
      for (SalesInvoiceItem salesInvoiceItem : getSalesInvoiceItemList()) {
        if (salesInvoiceItem.getTaxCodeId() != null && (salesInvoiceItem.getProductQty() != null || salesInvoiceItem.getProductQtyFree() != null) && salesInvoiceItem.getProdPieceSellingForced() != null && salesInvoiceItem.getValuePts() != null) {
          rvalue = true;
          break;
        }
      }
    }
    return rvalue;
  }

  /**
   * Method to update Pro-Forma expiry date.
   *
   * @param main
   * @return
   */
  public String saveProformaExpiryDays(MainView main) {
    try {
      getSalesInvoice().setProformaExpiringAt(SalesInvoiceUtil.addDaysToDate(getSalesInvoice().getProformaExpiringAt(), getProformaExpireDays()));
      SalesInvoiceService.updateProformaExpiryDate(main, getSalesInvoice());
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @param status
   * @return
   */
  private String saveOrCloneSalesInvoice(MainView main, String key, int status) {
    int currentStatus = getSalesInvoice().getSalesInvoiceStatusId() != null ? getSalesInvoice().getSalesInvoiceStatusId().getId() : 1;
    try {
      if (key.equals("save")) {
        //Last confirmed sales' entry date should be lesser than current entry date
        Date maxEntryDate = getMaxDateforInvoice(main);
        if (getSalesInvoice().getConfirmedAt() == null || (getSalesInvoice().getSalesInvoiceType() != null && getSalesInvoice().getSalesInvoiceType().intValue() == 2 && getSalesInvoice().getInvoiceNo().startsWith("PF"))) {
          Date lastEntryDate = SalesInvoiceService.getLastConfirmedEntryDate(main, salesInvoice);
          if (getSalesInvoice().getInvoiceEntryDate().compareTo(lastEntryDate) < 0) {
            String strDate = SDF_DD_MM_YYYY.format(lastEntryDate);
            main.error("error.confirm.minInvoiceDate", new String[]{strDate});
            return null;
          }
        } else if (maxEntryDate != null && getSalesInvoice().getInvoiceEntryDate().compareTo(maxEntryDate) > 0) {
          String strDate = SDF_DD_MM_YYYY.format(maxEntryDate);
          main.error("error.confirm.maxInvoiceDate", new String[]{strDate});
          return null;
        }
      }
      uploadFiles(); //File upload 
      boolean reloadPrvNxt = false;
      if (null != key) {
        switch (key) {
          case "save":
            if (null == getSalesInvoice().getId()) {
              reloadPrvNxt = true;
            }
//            Sets Entry Date as Invoice Date
            if (getSalesInvoice().getInvoiceEntryDate().compareTo(SystemRuntimeConfig.getMaxEntryDate(getSalesInvoice().getCompanyId())) > 0 && getSalesInvoice().getConfirmedAt() == null) {
              getSalesInvoice().setInvoiceEntryDate(SystemRuntimeConfig.getMaxEntryDate(getSalesInvoice().getCompanyId()));
            }
            getSalesInvoice().setInvoiceDate(getSalesInvoice().getInvoiceEntryDate());
            boolean interState = SystemRuntimeConfig.isInterstateBusiness(getSalesInvoice().getCompanyId().getStateId().getStateCode(), getSalesInvoice().getCustomerAddressId().getStateId().getStateCode());
            getSalesInvoice().setIsSalesInterstate(interState ? SystemConstants.INTERSTATE : SystemConstants.INTRASTATE);
            if (getSalesInvoice().getSalesInvoiceType() == null) {
              getSalesInvoice().setSalesInvoiceType(SystemConstants.SALES_INVOICE_TYPE_SALES_INVOICE);
            }
            /**
             * Sets cash discount applicable field value. If Cash discount is applied, program will apply this value to Grand net amount. If Cash discount is taxable then program
             * will apply cash discount on goods value.
             */
            getSalesInvoice().setCashDiscountTaxable(null);
            if (isCashDiscountApplicable()) {
              getSalesInvoice().setCashDiscountApplicable(SystemConstants.CASH_DISCOUNT_APPLICABLE);
              if (isCashDiscountTaxable()) {
                getSalesInvoice().setCashDiscountTaxable(SystemConstants.CASH_DISCOUNT_TAXABLE);
              }
            } else {
              getSalesInvoice().setCashDiscountValue(null);
            }

            if (getSalesInvoice() != null && getSalesInvoice().getId() != null) {

              /**
               * Checking sales invoice is editable or not. Here program, verifying sales invoice last modified date. If Last modified date is greater than current sales invoice
               * last modified date, then program will not allow update of sales invoice.
               */
              if (SalesInvoiceService.isSalesInvoiceEditable(main, getSalesInvoice())) {

                if (getSalesInvoice().getCustomerId().getTradeProfile() == null) {
                  TradeProfile tp = TradeProfileService.selectTradeProfileByCustomer(main, getSalesInvoice().getCustomerId());
                  getSalesInvoice().getCustomerId().setTradeProfile(tp);
                }

                /**
                 * Processing sales invoice calculation.
                 */
                getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//                TCS Calculations
                if (SystemConstants.CONFIRMED.equals(status)) {
                  if (StringUtil.equalsInt(getSalesInvoice().getCompanyId().getTcsEnabled(), 1)) {
                    TaxCode taxCode = TaxCodeService.selectByPk(main, AccountingConstant.TAX_CODE_TCS);
                    Double customerTotalSales = CustomerService.customerTotalSalesValue(main, salesInvoice);
                    getTaxCalculator().calculateTcsonSales(salesInvoice, salesInvoiceItemList, customerTotalSales, taxCode);
                  }
                }
                /**
                 * Insert/Update Sales Invoice Item List.
                 */
                int isProductSold = SalesInvoiceItemService.insertOrUpdateSalesInvoiceItems(main, getSalesInvoice(), getSalesInvoiceItemList(), status, isProformaToInvoice(), getTaxCalculator());

                if (isProductSold == SalesInvoiceUtil.REQUESTED_ITEMS_SOLD) {
                  getSalesInvoice().setSalesInvoiceStatusId(new SalesInvoiceStatus(status));

                  SalesInvoiceService.insertOrUpdate(main, getSalesInvoice());

                  if (SystemConstants.CONFIRMED.equals(getSalesInvoice().getSalesInvoiceStatusId().getId())) {

                    PlatformService.salesInvoicePlatformEntry(main, getSalesInvoice());

                    LedgerExternalService.saveLedgerTaxCode(main, getSalesInvoice().getCompanyId());

                    StockPreSaleService.releaseStockFromPreSale(main, getSalesInvoice());

                    getTaxCalculator().saveSales(main, getSalesInvoice());

                    SalesInvoiceLogService.createSalesInvoiceLogConfirm(main, salesInvoice);
                  }
                } else if (isProductSold == SalesInvoiceUtil.REQUESTED_FEW_ITEMS_NOT_AVAILABLE) {
                  main.error("error.product.not.available");
                  selectSalesInvoiceForEdit(main);
                  return null;
                }

              } else {
                main.error("error.past.record.edit");
                selectSalesInvoiceForEdit(main);
                return null;
              }
              setProformaToInvoice(false);

            } else {
              getSalesInvoice().setSalesInvoiceStatusId(new SalesInvoiceStatus(status));
              SalesInvoiceService.insertOrUpdate(main, getSalesInvoice());
            }
            break;
          case "clone":
            SalesInvoiceService.clone(main, getSalesInvoice());
            break;
        }

        getSalesInvoice().setPrintType(SystemConstants.TYPE_I);
        makeDocumentAndNotify(main, salesInvoice);
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
        if (reloadPrvNxt) {
          loadPreviousNextInvoiceList(main);
        }
      }
    } catch (Throwable t) {
      getSalesInvoice().setSalesInvoiceStatusId(new SalesInvoiceStatus(currentStatus));
      main.rollback(t, "error." + key);
    } finally {
      main.close();
      StringUtil.clearList(salesInvoiceItemSelected);
    }
    return null;
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionCancelSalesInvoice(MainView main) {
    try {
      long cancelled = SalesInvoiceService.cancelSalesInvoice(main, getSalesInvoice());
      if (cancelled == 0) {
        main.commit("success.save");
      } else {
        main.error("error.foreignkey.violates.constraint");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many SalesInvoice.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesInvoice(MainView main) {
    Integer id = null;
    boolean deleteable = true;
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesInvoiceSelected)) {
        for (SalesInvoice e : salesInvoiceSelected) {
          if (e.getSalesInvoiceStatusId().getId().intValue() >= SystemConstants.CONFIRMED) {
            deleteable = false;
            break;
          }
        }
        if (deleteable) {
          for (SalesInvoice e : salesInvoiceSelected) {
            id = e.getId();
            SalesInvoiceService.deleteByPk(main, e);
            main.em().commit();
          }
          main.commit("success.delete");
          salesInvoiceSelected = null;
        }
      } else {
        if (getSalesInvoice().getSalesInvoiceStatusId().getId().intValue() < SystemConstants.CONFIRMED) {
          id = getSalesInvoice().getId();
          SalesInvoiceService.deleteByPk(main, getSalesInvoice());  //individual record delete from list or edit form
          main.commit("success.delete");
          if ("editform".equals(main.getViewType())) {
            main.setViewType(ViewTypes.newform);
          }
        } else {
          deleteable = false;
        }
      }
      if (!deleteable) {
        throw new UserMessageException("error.delete.confirmed");
      }
    } catch (Throwable t) {
      AppUtil.referenceError(main, t, id);
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionCancelProforma(MainView main) {
    try {
      SalesInvoiceService.releaseBlockedProductsFromStockPreSalse(main, getSalesInvoice());
      SalesInvoiceService.updateSalesInvoiceStatus(main, getSalesInvoice(), SystemConstants.CANCELLED);
      main.commit("success.proforma.cancelled");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  //FIXME main not required
  /**
   *
   * @param main
   * @return
   */
  public String actionProformaToInvoice(MainView main) {
    setProformaToInvoice(true);
    return null;
  }

  public List<ServiceCommodity> completeServiceDetail(String filter) {
    return ScmLookupExtView.lookupServiceCommodity(getSalesInvoice().getCompanyId(), filter);
  }

  public void serviceSelectEvent() {
    if (getSalesInvoiceItemList() != null) {
      getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
    }
  }

  /**
   * SalesOrder autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.salesOrderAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.salesOrderAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesOrder> salesOrderAuto(String filter) {
    return ScmLookupView.salesOrderAuto(filter);
  }

  /**
   *
   * @return
   */
  public List<CompanyAddress> lookUpCompanyAddress() {
    List<CompanyAddress> companyAddressList = null;
    companyAddressList = ScmLookupExtView.companyAddressByCompany(getSalesInvoice().getCompanyId());
    if (companyAddressList != null && !companyAddressList.isEmpty() && companyAddressList.size() == 1) {
      getSalesInvoice().setCompanyAddressId(companyAddressList.get(0));
    }

    return companyAddressList;
  }

  /**
   *
   * @return
   */
  public List<CustomerAddress> lookUpCustomerAddress(boolean showAll) {
    List<CustomerAddress> list = null;
    if (getSalesInvoice().getCustomerId() != null) {
      list = ScmLookupExtView.customerAddressByCustomer(getSalesInvoice().getCustomerId(), showAll);
      if (list != null && !list.isEmpty() && list.size() == 1) {
        CustomerAddress address = list.get(0);
        getSalesInvoice().setCustomerAddressId(address);
        if (address != null) {
          getSalesInvoice().setCustomerDistrictId(address.getDistrictId());
        }
      }
    }
    return list;

  }

  public List<CustomerAddress> lookUpShippingAddress() {
    return lookUpCustomerAddress(true);
  }

  /**
   * SalesInvoiceStatus autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.salesInvoiceStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.salesInvoiceStatusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesInvoiceStatus> salesInvoiceStatusAuto(String filter) {
    return ScmLookupView.salesInvoiceStatusAuto(filter);
  }

  /**
   * Customer autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.customerAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.customerAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Customer> customerAuto(String filter) {
    MainView main = Jsf.getMain();
    List<Customer> customerList = null;
    try {
      if (getSalesInvoice() != null && getSalesInvoice().getAccountGroupId() != null) {
        customerList = ScmLookupExtView.selectCustomerByAccountGroup(getSalesInvoice().getAccountGroupId(), filter);
        for (Customer list : customerList) {
          SalesInvoiceService.addDistrictToCustomer(main, list);
        }
      }
      return customerList;

    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Method to select products based on its account group.
   *
   * @param filter
   * @return
   */
  public List<Product> completeProduct(String filter) {
    List<Product> productList = null;
    MainView main = Jsf.getMain();
    try {
      if (getSalesInvoice().getAccountGroupId() != null) {
        productList = getTaxCalculator().selectProductForSales(main, getSalesInvoice(), filter);
      }
      return productList;
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @return
   */
  public List<AccountGroup> accountGroupByAccountOrSalesOrder() {
    MainView main = Jsf.getMain();
    List<AccountGroup> list = null;
    try {

      if (getSalesInvoice().getSalesOrderId() != null && getSalesInvoice().getSalesOrderId().getId() != null) {
        list = ScmLookupExtView.accountGroupBySalesOrder(getSalesInvoice().getSalesOrderId());
      } else {
        if (UserRuntimeView.instance().getAccountCurrent() != null) {
          list = ScmLookupExtView.accountGroupByAccount(UserRuntimeView.instance().getAccountCurrent());
        }
      }
      if (list != null && !list.isEmpty() && getSalesInvoice().getAccountGroupId() == null) {
        getSalesInvoice().setAccountGroupId(list.get(0));
        updateSalesInvoiceNumber(main);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return list;
  }

  /**
   *
   */
  private void updateSalesInvoiceNumber(MainView main) {
    if (getSalesInvoice().getAccountGroupId() != null) {
      getSalesInvoice().setInvoiceNo("DR-" + AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getSalesInvoice().getAccountGroupId(),
              PrefixTypeService.SALES_INVOICE_PREFIX_ID, true, getSalesInvoice().getFinancialYearId()));
    } else {
      getSalesInvoice().setInvoiceNo(null);
    }
  }

  /**
   *
   * @return
   */
  public List<AccountGroupPriceList> accountGroupPriceListByAccountGroup() {
    List<AccountGroupPriceList> accountGroupPriceList = null;
    if (getSalesInvoice().getAccountGroupId() != null) {
      accountGroupPriceList = ScmLookupExtView.accountGroupPriceListByAccountGroup(getSalesInvoice().getAccountGroupId());
      if (accountGroupPriceList != null && !accountGroupPriceList.isEmpty() && accountGroupPriceList.size() == 1 && getSalesInvoice().getAccountGroupPriceListId() == null) {
        getSalesInvoice().setAccountGroupPriceListId(accountGroupPriceList.get(0));
      }
    }
    return accountGroupPriceList;
  }

  /**
   *
   * @param main
   * @param salesInvoiceType
   * @return
   */
  public String newSalesInvoice(MainView main, int salesInvoiceType) {
    try {
      setSalesOrderReference(salesInvoiceType == 2);
      getSalesInvoice().reset();
      main.setViewType(ViewTypes.newform);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param main
   */
  public void actionInsertOrUpdateSalesInvoiceItem(MainView main) {
    boolean isNew = false;
    try {
      if (isValueMrpChanged()) {
        main.error("error.mrp.changed");
        return;
      }
      if (SalesInvoiceService.isSalesInvoiceEditable(main, getSalesInvoice())) {
        Integer rowIndex = Jsf.getParameterInt("rownumber");
        SalesInvoiceItem salesInvoiceItem = getSalesInvoiceItemList().get(rowIndex);

        if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getProdPieceSellingForced() != null && salesInvoiceItem.getProdPieceSellingForced() > 0) {
          if (salesInvoiceItem.getId() == null) {
            isNew = true;
          }
          salesInvoiceItem.setSalesInvoiceId(getSalesInvoice());

          double invoiceDisc = salesInvoiceItem.getProductDetailSales().getInvoiceDiscountValue() == null ? 0 : salesInvoiceItem.getProductDetailSales().getInvoiceDiscountValue();
          //double lineDisc = salesInvoiceItem.getProductDetailSales().getProductDiscountValue() == null ? 0 : salesInvoiceItem.getProductDetailSales().getProductDiscountValue();
          double pts = salesInvoiceItem.getProductDetailSales().getPricelistPts() == null ? 0 : salesInvoiceItem.getProductDetailSales().getPricelistPts();
          salesInvoiceItem.setInvoiceDiscountActual(MathUtil.roundOff(invoiceDisc, 2));
          //salesInvoiceItem.setProductDiscountActual(MathUtil.roundOff(lineDisc, 2));
          salesInvoiceItem.setValueProdPieceSelling(pts);
          salesInvoiceItem.setSecToPriQuantityActual(salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
          salesInvoiceItem.setTerToSecQuantityActual(salesInvoiceItem.getProductDetailSales().getTerToSecQuantity());
          salesInvoiceItem.setProductHash(salesInvoiceItem.getProductDetailSales().getProductHash());

          // Sets Free Scheme to sales invoice item.
          if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() > 0) {
            if (salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId() != null) {
              salesInvoiceItem.setProductFreeQtyScheme(new ProductFreeQtyScheme(salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId()));
            }
          }
          if (getSalesInvoice().getCustomerId().getTradeProfile() == null) {
            TradeProfile tp = TradeProfileService.selectTradeProfileByCustomer(main, getSalesInvoice().getCustomerId());
            getSalesInvoice().getCustomerId().setTradeProfile(tp);
          }
          getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);

          if (isProformaToInvoice()) {

            if (isNew) {
              salesInvoiceItem.setProformaItemStatus(SalesInvoiceUtil.PROFORMA_ITEM_NEW);
            } else {
              salesInvoiceItem = SalesInvoiceUtil.isProformaItemModified(salesInvoiceItem);
            }
            if (salesInvoiceItem.getProformaItemStatus() != null && salesInvoiceItem.getProformaItemStatus() > 0) {
              getSalesInvoice().setProformaStatus(SalesInvoiceUtil.PROFORMA_MODIFIED);
            }

          } else {
            SalesInvoiceService.insertOrUpdate(main, getSalesInvoice());
            if (salesInvoiceItem.getTaxCodeId() != null && salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProdPieceSellingForced() != null && salesInvoiceItem.getValuePts() != null) {
              boolean newRecord = salesInvoiceItem.getId() == null || !StringUtil.gtInt(salesInvoiceItem.getId(), 0);
              Integer sort = newRecord ? getSalesInvoiceItemList().get(getSalesInvoiceItemList().size() - 1).getSortOrder() : salesInvoiceItem.getSortOrder();
              SalesInvoiceItem item = SalesInvoiceItemService.insertOrUpdateSalesInvoiceItem(main, getSalesInvoice(), salesInvoiceItem, null, getTaxCalculator(), sort, false);
              if (newRecord) {
                getSalesInvoiceItemList().remove(salesInvoiceItem);
                getSalesInvoiceItemList().add(item);
              } else {
                getSalesInvoiceItemList().set(rowIndex, item);
              }

              //   getSalesInvoiceItemList().set(rowIndex, item);
              //  getSalesInvoiceItemList().remove(salesInvoiceItem);
              //   getSalesInvoiceItemList().add(item);
              //selectSalesInvoiceItemList(main, getSalesInvoice());
            }
          }
          getInvoiceByHsnList(salesInvoiceItemList);
          if (isNew) {
            actionAddNewLineItem();
          } else if (rowIndex == 0) {
            actionAddNewLineItem();
          }
        }
        //   getTaxCalculator().processSalesInvoiceCalculation(salesInvoice, salesInvoiceItemList);
      } else {
        main.error("error.past.record.edit");
        selectSalesInvoiceForEdit(main);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }

  }

  /**
   * Method to delete sales invoice item.
   *
   * @param main
   * @param salesInvoiceItem
   */
  private void actionDeleteSalesItem(MainView main, SalesInvoiceItem salesInvoiceItem) {

    if (salesInvoiceItem != null && salesInvoiceItem.getId() != null && salesInvoiceItem.getId() > 0) {
      if (!StringUtil.isEmpty(salesInvoiceItem.getSalesInvoiceItemHashCode())) {
        SalesInvoiceItemService.deleteBySalesInvoiceItemHashCode(main, salesInvoiceItem);
        getSalesInvoiceItemList().removeIf(t -> (t.getSalesInvoiceItemHashCode() != null && t.getSalesInvoiceItemHashCode().equals(salesInvoiceItem.getSalesInvoiceItemHashCode())));
        setValueMrpChanged(SalesInvoiceService.isProductValueMrpChanged(main, getSalesInvoice()));
      }
      //getProductHashCode().remove(salesInvoiceItem.getProductHash());
//      getSalesInvoiceItemList().remove(salesInvoiceItem);
      getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);

    } else {
      if (!StringUtil.isEmpty(getSalesInvoiceItemList())) {
        getSalesInvoiceItemList().removeIf(t -> (t.getSalesInvoiceItemHashCode() != null && t.getSalesInvoiceItemHashCode().equals(salesInvoiceItem.getSalesInvoiceItemHashCode())));

//        if (salesInvoiceItem != null && salesInvoiceItem.getProductDetailSales() != null && salesInvoiceItem.getProductDetailSales().getProductHash() != null) {
//          // getProductHashCode().remove(salesInvoiceItem.getProductDetailSales().getProductHash());
//        }
      }
    }
    getSalesInvoiceItemList().remove(salesInvoiceItem);
    //if (!isProformaToInvoice()) {
    //}
    if (getSalesInvoiceItemList().isEmpty()) {
      actionAddNewLineItem();
    }
    hasLineItems();
  }

  /**
   *
   * @param salesInvoiceItem
   */
  public void actionUndoDeleteSalesItem(SalesInvoiceItem salesInvoiceItem) {
    if (salesInvoiceItem != null && salesInvoiceItem.getProformaItemStatus() != null) {
      if (salesInvoiceItem.getProformaItemStatus().equals(SalesInvoiceUtil.PROFORMA_ITEM_DELETED)) {
        salesInvoiceItem.setProformaItemStatus(null);
      }
    }
  }

  /**
   * Method to add new sales invoice item to the list.
   *
   */
  private void actionAddNewLineItem() {
    if (getSalesInvoiceItemList() == null) {
      salesInvoiceItemList = new ArrayList<>();
    }
    SalesInvoiceItem sit = new SalesInvoiceItem();
    getSalesInvoiceItemList().add(0, sit);
  }

  /**
   * Product selection event handler.
   *
   * @param salesInvoiceItem
   */
  public void updateCurrentRow(MainView main, SalesInvoiceItem salesInvoiceItem) {
    try {
      salesInvoiceItem.setProductDetailSales(null);
      salesInvoiceItem.setProductDetailId(null);
      salesInvoiceItem.setProdPieceSellingForced(null);
      salesInvoiceItem.setValuePts(null);
      salesInvoiceItem.setValuePtr(null);
      salesInvoiceItem.setValueProdPieceSelling(null);
      salesInvoiceItem.setProductDiscountActual(null);
      salesInvoiceItem.setInvoiceDiscountActual(null);
      salesInvoiceItem.setValueVat(null);
      salesInvoiceItem.setValueSale(null);
      salesInvoiceItem.setValueGoods(null);
      salesInvoiceItem.setProductQty(null);
      salesInvoiceItem.setProductQtyFree(null);
      salesInvoiceItem.setProductDiscountValue(null);
      salesInvoiceItem.setTertiaryQuantity(null);
      salesInvoiceItem.setSecondaryQuantity(null);
      salesInvoiceItem.setProductDetailSalesList(null);
      hasLineItems();
      if (salesInvoiceItem.getSalesInvoiceId() == null) {
        salesInvoiceItem.setSalesInvoiceId(getSalesInvoice());
      }
      salesInvoiceItem.setProductDetailSalesList(SalesInvoiceService.selectProductDetailSales(main, salesInvoiceItem));
      setProductName(salesInvoiceItem.getProduct() != null ? salesInvoiceItem.getProduct().getProductName() : null);
      salesInvoiceItem.setHsnCode(salesInvoiceItem.getProduct() != null ? salesInvoiceItem.getProduct().getHsnCode() : null);
      productInvoiceDetailList = SalesInvoiceService.selectProductRateHistory(main, salesInvoiceItem.getSalesInvoiceId(), salesInvoiceItem);
      Jsf.addCallbackParam("productCount", StringUtil.isEmpty(productInvoiceDetailList) ? 0 : productInvoiceDetailList.size());
      //if (getSalesInvoiceItemList().size() == 1 && salesInvoiceItem.getId() == null) {
      //getSalesInvoice().setCommodityId(salesInvoiceItem.getProduct().getCommodityId());
      //getSalesInvoice().setProductTypeId(salesInvoiceItem.getProduct().getProductTypeId());
      //}
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   * Product Detail select event handler.
   *
   * @param salesInvoiceItem
   */
  public void updateProductDetails(MainView main, SalesInvoiceItem salesInvoiceItem) {
    try {
      // SalesInvoiceService.updateProductDetails(main, salesInvoiceItem, getTaxCalculator());
      if (salesInvoiceItem.getProductHashChanged()) {
        if (salesInvoiceItem.getProductDetailSales() != null) {
          getTaxCalculator().updateSalesInvoiceItem(main, salesInvoiceItem);
        }
        SalesInvoiceItemUtil.setCurrentAvailableQty(salesInvoiceItem, SalesInvoiceUtil.PRODUCT_EMPTY_QTY);
//      if (salesInvoiceItem.getProductDetailSales() != null) {
//        List<ProductInvoiceDetail> productInvoiceDetailList = SalesInvoiceService.selectProductRateHistory(main, salesInvoiceItem.getSalesInvoiceId(), salesInvoiceItem);
//        Jsf.addCallbackParam("productCount", StringUtil.isEmpty(productInvoiceDetailList) ? 0 : productInvoiceDetailList.size());
//      }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void taxCodeSelectEvent(SalesInvoiceItem salesInvoiceItem) {
    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
  }

  /**
   *
   * @param salesInvoiceItem
   */
  public void updateProductFreeQtyScheme(SalesInvoiceItem salesInvoiceItem) {
    if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() == 0) {
      salesInvoiceItem.setProductFreeQtyScheme(null);
    }
    double productQty = 0, freeQty = 0;
    productQty = salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
    freeQty = salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree();
    if (productQty + freeQty == 0) {
      salesInvoiceItem.setProductQty(1);
      salesInvoiceItem.setProductQtyFree(salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree());
    } else {
      salesInvoiceItem.setProductQty(salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty());
      salesInvoiceItem.setProductQtyFree(salesInvoiceItem.getProductQtyFree() == null ? null : salesInvoiceItem.getProductQtyFree());
    }

    salesInvoiceItem.setSchemeDiscountValue(null);
    salesInvoiceItem.setSchemeDiscountPercentage(null);
    SalesInvoiceItemUtil.setCurrentAvailableQty(salesInvoiceItem, SalesInvoiceUtil.PRODUCT_FREE_QTY);
    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
    if (salesInvoiceItem.getProductDetailSales() != null && salesInvoiceItem.getProductDetailSales().getSecToPriQuantity() != null) {
      double qty = salesInvoiceItem.getProductQty();
      double free = salesInvoiceItem.getProductQtyFree() != null ? salesInvoiceItem.getProductQtyFree() : 0;
      salesInvoiceItem.setSecondaryQuantity((qty + free) / salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
    }
  }

  /**
   *
   * @param main
   * @param salesInvoiceItem
   * @param qType
   */
  public void updateProductSalesValues(MainView main, SalesInvoiceItem salesInvoiceItem, int qType) {
    try {
      SalesInvoiceService.updateProductSalesValues(main, getSalesInvoice(), getSalesInvoiceItemList(), salesInvoiceItem, qType);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param main
   * @param salesInvoiceItem
   * @param qtyType
   */
  public void updateProductQuantity(MainView main, SalesInvoiceItem salesInvoiceItem, int qtyType) {
    Double tQty;
    Double sQty;
    Double pQty;
    try {
      if (salesInvoiceItem.getProductDetailSales() != null) {
        if (salesInvoiceItem.getProductDetailSales().getTertiaryPackId() != null && qtyType == SalesInvoiceUtil.PRODUCT_TERTIARY_QTY) {
          tQty = salesInvoiceItem.getTertiaryQuantity();
          if (tQty != null) {
            salesInvoiceItem.setSecondaryQuantity(tQty * salesInvoiceItem.getProductDetailSales().getTerToSecQuantity());
            pQty = (salesInvoiceItem.getSecondaryQuantity() * salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
            salesInvoiceItem.setProductQty(pQty.intValue());
          } else {
            salesInvoiceItem.setSecondaryQuantity(null);
            salesInvoiceItem.setProductQty(null);
          }
          updateProductSalesValues(main, salesInvoiceItem, qtyType);
        } else if (salesInvoiceItem.getProductDetailSales().getSecToPriQuantity() != null && qtyType == SalesInvoiceUtil.PRODUCT_SECONDARY_QTY) {
          sQty = salesInvoiceItem.getSecondaryQuantity();
          if (sQty != null) {
            if (salesInvoiceItem.getProductDetailSales().getTertiaryPackId() != null) {
              tQty = salesInvoiceItem.getSecondaryQuantity() / salesInvoiceItem.getProductDetailSales().getTerToSecQuantity();
              salesInvoiceItem.setTertiaryQuantity(tQty);
            }
            pQty = sQty * salesInvoiceItem.getProductDetailSales().getSecToPriQuantity();
            salesInvoiceItem.setProductQty(pQty.intValue());
            salesInvoiceItem.setProductQtyFree(0);
          } else {
            salesInvoiceItem.setTertiaryQuantity(null);
            salesInvoiceItem.setProductQty(null);
          }
          updateProductSalesValues(main, salesInvoiceItem, qtyType);
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
   */
  public void updateSalesInvoiceAmount() {
    if (getSalesInvoice().getInvoiceAmount() != null && getSalesInvoice().getInvoiceAmount() > 0) {
      getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
    }
  }

  /**
   * Customer Auto complete event handler.
   *
   * @param event
   */
  public void customerSelectedEvent(SelectEvent event) {
    Customer cust = (Customer) event.getObject();
    MainView main = Jsf.getMain();
    try {
      if (cust != null) {
        SalesInvoiceService.customerSelectEvent(main, getSalesInvoice(), getSalesInvoice().getCompanyId(), getSalesInvoice().getAccountGroupId(), cust);
        setCustomerOutstandingValue(selectCustomerOutstanding(main, cust, getSalesInvoice().getAccountGroupId()));
        setSalesBusinessModes(main);
        getSalesInvoice().setSezZone((cust.getTaxable() != null && !StringUtil.isEmpty(cust.getGstNo()) && cust.getTaxable().intValue() == 2) ? 1 : 0);
        if (!StringUtil.isEmpty(cust.getGstNo()) && getSalesInvoice().getCustomerId().getTaxable() != null && getSalesInvoice().getSezZone().intValue() == 1) {
          setInterstateSales(true);
          setIntrastateSales(false);
        }
        List<CustomerAddress> list = lookUpShippingAddress();
        if (list.size() == 1) {
          getSalesInvoice().setShippingAddressId(list.get(0));
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  private Double selectCustomerOutstanding(MainView main, Customer customer, AccountGroup accountGroup) {
    FilterParameters filterParameters = new FilterParameters();
    filterParameters.setCustomer(customer);
    filterParameters.setAccountGroup(accountGroup);
    List<CustomerOutstanding> customerOutstandingList = CustomerOutstandingService.getCustomerOutstandingList(main, filterParameters, 2, null, null, null);
    Double outstandingValue = 0.0;
    for (CustomerOutstanding outstanding : customerOutstandingList) {
      outstandingValue += outstanding.getOustandingamount();
    }
    return outstandingValue;
  }

  /**
   *
   * @return
   */
  public boolean isMedicineCommodity() {
    return (getSalesInvoice() != null && getSalesInvoice().getCommodityId() != null && getSalesInvoice().getCommodityId().getId().equals(SystemConstants.MEDICINE_COMMODITY_ID));
  }

  /**
   *
   * @return
   */
  public boolean isDraft() {
    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceStatusId() != null) {
      return (getSalesInvoice().getSalesInvoiceStatusId().getId().equals(SystemConstants.DRAFT));
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean isTaxCodeModified() {
    if (getSalesInvoice() != null && getSalesInvoice().getIsTaxCodeModified() != null) {
      return (SystemConstants.TAX_CODE_MODIFIED.equals(getSalesInvoice().getIsTaxCodeModified()));
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean isProforma() {
    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceType() != null) {
      return (getSalesInvoice().getSalesInvoiceType().equals(SystemConstants.SALES_INVOICE_TYPE_PROFORMA));
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean isConfirmed() {
    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceStatusId() != null) {
      return (getSalesInvoice().getSalesInvoiceStatusId().getId().equals(SystemConstants.CONFIRMED));
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean isConfirmedAndPacked() {
    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceStatusId() != null) {
      return (getSalesInvoice().getSalesInvoiceStatusId().getId().equals(SystemConstants.CONFIRMED_AND_PACKED));
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean isCancelled() {
    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceStatusId() != null) {
      return (getSalesInvoice().getSalesInvoiceStatusId().getId().equals(SystemConstants.CANCELLED));
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean isExpired() {
    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceStatusId() != null) {
      return (getSalesInvoice().getSalesInvoiceStatusId().getId().equals(SystemConstants.EXPIRED));
    }
    return false;
  }

  /**
   *
   */
  public void validateSalesInvoiceIsEditable() {
    salesInvoiceEditable = false;
    if (getSalesInvoice() != null && getSalesInvoice().getId() == null) {
      salesInvoiceEditable = true;
    } else if (isDraft()) {
      salesInvoiceEditable = true;
    } else if (isProformaToInvoice()) {
      salesInvoiceEditable = true;
    }
  }

  /**
   *
   * @return
   */
  public boolean isSalesInvoiceEditable() {
    return salesInvoiceEditable;
  }

  /**
   *
   * @return
   */
  public boolean isProformaEditable() {
    if (isProformaToInvoice()) {
      return false;
    } else if (isProforma() && !isCancelled() && !isExpired()) {
      return true;
    }
    return false;
  }

  /**
   *
   * @return
   */
  public List<VendorContact> lookupVendorSalesAgentPersonProfile() {
    List<VendorContact> list = null;
    if (getSalesInvoice() != null && getSalesInvoice().getAccountGroupId() != null) {
      list = (List<VendorContact>) ScmLookupExtView.selectVendorContactByAccountGroup(getSalesInvoice().getAccountGroupId());
    }
    return list;
  }

  /**
   *
   * @return
   */
  public List<SalesAgent> lookupSalesAgent(String filter) {
    List<SalesAgent> list = null;
    if (getSalesInvoice() != null && getSalesInvoice().getAccountGroupId() != null && getSalesInvoice().getCustomerId() != null) {
      list = (List<SalesAgent>) SalesAgentService.selectSalesAgentbyCustomer(filter, getSalesInvoice().getCompanyId(), getSalesInvoice().getAccountGroupId(), getSalesInvoice().getCustomerId());
    }
    return list;
  }

  public void print(MainView main, String key, String printMode, String freeType) {
    try {
      getSalesInvoice().setProformaStatus(isProformaEditable() ? SystemConstants.SALES_INVOICE_TYPE_PROFORMA : isDraft() ? SystemConstants.SALES_INVOICE_TYPE_PROFORMA : SystemConstants.SALES_INVOICE_TYPE_SALES_INVOICE);
      if (printMode.equals(SystemConstants.PRINT_PORTRAIT)) {
        getSalesInvoice().setPrintMode(SystemConstants.PRINT_PORTRAIT);
        if (SystemConstants.PDF_ITEXT.equals(key)) {
          if (freeType.equals(SystemConstants.TYPE_I)) {
            getSalesInvoice().setPrintType(SystemConstants.TYPE_I);
          } else if (freeType.equals(SystemConstants.TYPE_II)) {
            getSalesInvoice().setPrintType(SystemConstants.TYPE_II);
          } else if (freeType.equals(SystemConstants.SAMPLE)) {
            getSalesInvoice().setPrintType(SystemConstants.SAMPLE);
          }
          Jsf.popupForm(getTaxCalculator().getSalesInvoicePrintIText(), getSalesInvoice());
        } else if (SystemConstants.PDF_KENDO.equals(key)) {
          Jsf.popupForm(getTaxCalculator().getSalesInvoicePortrait(), getSalesInvoice());
        }
      } else if (printMode.equals(SystemConstants.PRINT_LANDSCAPE)) {
        getSalesInvoice().setPrintType("");
        CompanySettings settings = getSalesInvoice().getCompanyId().getCompanySettings();
        if (settings == null) {
          settings = CompanySettingsService.selectByCompany(main.em(), getSalesInvoice().getCompanyId());
        }
        if (settings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
          getSalesInvoice().setPrintMode(SystemConstants.PRINT_SINGLE_LINE);
        } else {
          getSalesInvoice().setPrintMode(SystemConstants.PRINT_MULTIPLE_LINE);
        }
        if (SystemConstants.PDF_ITEXT.equals(key)) {
          Jsf.popupForm(getTaxCalculator().getSalesInvoicePrintIText(), getSalesInvoice());
        } else if (SystemConstants.PDF_KENDO.equals(key)) {
          Jsf.popupForm(getTaxCalculator().getSalesInvoiceLandscapeMultipleForm(), getSalesInvoice());
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
   */
  //  public void dialogClose() {
  //    Jsf.closePopup(accountingTransactionDetailItem);
  //  }
  /**
   *
   */
  public void actionApplyForcedRoundOffValue() {
    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
  }

  /**
   * Applying Cash discounts
   */
  public void actionApplyCashDiscount() {
    MainView main = Jsf.getMain();
    try {
      if (isCashDiscountApplicable()) {
        getSalesInvoice().setCashDiscountApplicable(SystemRuntimeConfig.CASH_DISCOUNT_APPLICABLE);
      } else {
        getSalesInvoice().setCashDiscountValue(null);
        getSalesInvoice().setCashDiscountApplicable(null);
      }
      if (isCashDiscountTaxable()) {
        getSalesInvoice().setCashDiscountTaxable(SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE);
      } else {
        getSalesInvoice().setCashDiscountTaxable(null);
      }
      if (getSalesInvoiceItemList() != null) {
        getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
      }
      if (getSalesInvoiceItemList() != null && !StringUtil.isEmpty(getSalesInvoiceItemList())) {
        saveSalesInvoice(main, SystemConstants.DRAFT);
        Jsf.update("f1");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param sitem
   * @return
   */
  public boolean schemeDiscountApplied(SalesInvoiceItem sitem) {
    return (sitem != null && sitem.getProductQtyFree() == null && sitem.getSchemeDiscountValue() != null);
  }

  /**
   *
   * @param sitem
   * @return
   */
  public boolean schemeApplicable(SalesInvoiceItem sitem) {
    return (sitem != null && sitem.getQuantityOrFree() != null && SystemConstants.FREE_SCHEME.equals(sitem.getQuantityOrFree()));
  }

  /**
   * Product scheme discount value event handler.
   *
   * @param salesInvoiceItem
   */
  public void schemeDiscountValueChangeEventHandler(SalesInvoiceItem salesInvoiceItem) {
    salesInvoiceItem.setSchemeDiscountPercentage(null);
    salesInvoiceItem.setSchemeDiscountDerived(null);
    salesInvoiceItem.setIsSchemeDiscountInPercentage(null);
    if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getSchemeDiscountValue() != null) {
      salesInvoiceItem.setIsSchemeDiscountInPercentage(SystemConstants.DISCOUNT_IN_VALUE);
    }
    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);

  }

  /**
   * Product Scheme discount percentage event handler.
   *
   * @param salesInvoiceItem
   */
  public void schemeDiscountPercChangeEventHandler(SalesInvoiceItem salesInvoiceItem) {
    salesInvoiceItem.setSchemeDiscountValue(null);
    salesInvoiceItem.setSchemeDiscountDerived(null);
    salesInvoiceItem.setIsSchemeDiscountInPercentage(null);
    if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getSchemeDiscountPercentage() != null) {
      salesInvoiceItem.setIsSchemeDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
    }
    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
  }

  /**
   * Product discount value change event handler
   *
   * @param salesInvoiceItem
   */
  public void productDiscountValueChangeEventHandler(SalesInvoiceItem salesInvoiceItem) {
    salesInvoiceItem.setProductDiscountPercentage(null);
    salesInvoiceItem.setProductDiscountDerived(null);
    salesInvoiceItem.setIsProductDiscountInPercentage(null);
    if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getProductDiscountValue() != null) {
      salesInvoiceItem.setIsProductDiscountInPercentage(SystemConstants.DISCOUNT_IN_VALUE);
    }
    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
  }

  /**
   * Product Scheme discount percentage event handler.
   *
   * @param salesInvoiceItem
   */
  public void productDiscountPercChangeEventHandler(SalesInvoiceItem salesInvoiceItem) {
    salesInvoiceItem.setProductDiscountValue(null);
    salesInvoiceItem.setProductDiscountDerived(null);
    salesInvoiceItem.setIsProductDiscountInPercentage(null);
    if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getProductDiscountPercentage() != null) {
      salesInvoiceItem.setIsProductDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
    }
    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
  }

  /**
   *
   * @param main
   */
  public void actionResetInvoiceEntry(MainView main) {
    try {
      SalesInvoiceService.actionResetSalesInvoiceToDraft(main, getSalesInvoice());
      getSalesInvoice().setSalesInvoiceStatusId(new SalesInvoiceStatus(SystemConstants.DRAFT));
      SalesInvoiceLogService.createSalesInvoiceLogDraft(main, salesInvoice);
      getSalesInvoice().setPrintType(SystemConstants.TYPE_I);
      makeDocumentAndNotify(main, salesInvoice);
      //saveSalesInvoice(main, SystemConstants.DRAFT);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param main
   */
  public void actionResetAllInvoiceEntry(MainView main) {
    try {
      SalesInvoiceService.resetAllSalesInvoiceToDraft(main, getSalesInvoice().getAccountGroupId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionUpdatePacking(MainView main) {
    try {
      SalesInvoiceService.updateSalesInvoicePacking(main, getSalesInvoice());
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param main
   * @return
   */
  public String addToExistingConsignment(MainView main) {
    if (consignment != null) {
      try {
        ConsignmentCommodityService.insertArraySales(main, new SalesInvoice[]{getSalesInvoice()}, getConsignment());
        Jsf.popupForm(ConsignmentService.SALES_INVOICE_CONSIGNMENT_POPUP, null, consignment.getId());
      } catch (Throwable t) {
        main.rollback(t, "error.save");
      } finally {
        main.close();
      }
    }
    return null;
  }

  //FIXME move this to popupview, main is not requesed aa argumet
  /**
   *
   * @param main
   * @return
   */
  public String createConsignment(MainView main) {
    Jsf.popupForm(ConsignmentService.SALES_INVOICE_CONSIGNMENT_POPUP, getSalesInvoice());
    return null;
  }

  /**
   *
   * @param main
   * @return
   */
  public String viewConsignment(MainView main) {
    if (consignment != null) {
      Jsf.popupForm(ConsignmentService.SALES_INVOICE_CONSIGNMENT_POPUP, null, consignment.getId());
    }
    return null;
  }

  /**
   *
   * @return
   */
  public boolean isProductTypeEditable() {
    return !(!StringUtil.isEmpty(getSalesInvoiceItemList()) && getSalesInvoiceItemList().get(0).getProduct() != null);
  }

  /**
   *
   * @return
   */
  public List<ProductType> lookupProductType() {
    return ScmLookupExtView.lookupProductType();
  }

  /**
   *
   * @return
   */
  private List<TaxCode> lookupGstTaxCode() {
    if (taxCodeList == null) {
      taxCodeList = ScmLookupExtView.lookupGstTaxCode(getSalesInvoice().getCompanyId());
    }
    return taxCodeList;
  }

  private void hasLineItems() {
    setLineItemExist(false);
    if (!StringUtil.isEmpty(getSalesInvoiceItemList())) {
      if (getSalesInvoiceItemList().size() == 1) {
        if (getSalesInvoiceItemList().get(0).getProduct() != null) {
          setLineItemExist(true);
        }
      } else {
        setLineItemExist(true);
      }
    }
  }

  public List<SalesInvoiceItem> getSalesInvoiceItemSelected() {
    return salesInvoiceItemSelected;
  }

  public void setSalesInvoiceItemSelected(List<SalesInvoiceItem> salesInvoiceItemSelected) {
    this.salesInvoiceItemSelected = salesInvoiceItemSelected;
  }

  public void deleteSalesInvoiceItemSelected(MainView main) {
    try {
      if (SalesInvoiceService.isSalesInvoiceEditable(main, getSalesInvoice())) {
        if (salesInvoiceItemSelected != null) {
          for (SalesInvoiceItem selected : salesInvoiceItemSelected) {
            actionDeleteSalesItem(main, selected);
          }
          main.commit("success.delete");
        }
      } else {
        main.error("error.past.record.edit");
        selectSalesInvoiceForEdit(main);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
      StringUtil.clearList(salesInvoiceItemSelected);
    }
  }

  public void deleteSalesInvoiceItemSelected(MainView main, SalesInvoiceItem salesInvoiceItem) {
    try {
      if (salesInvoiceItem != null) {
        if (salesInvoiceItem.getSalesInvoiceItemHashCode() == null) {
          getSalesInvoiceItemList().remove(salesInvoiceItem);
        } else {
          actionDeleteSalesItem(main, salesInvoiceItem);
        }
        main.commit("success.delete");
      }

    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

//  public Long getRowKey() {
//    return new Date().getTime();
//  }
  //
//  public List<ProductDetailSales> selectProductDetailSales(MainView main, SalesInvoiceItem salesInvoiceItem) {
//    // MainView main = Jsf.getMain();
//    try {
//      // SalesInvoiceItem salesInvoiceItem = (SalesInvoiceItem) Jsf.getAttribute("selectItem");
//      if (salesInvoiceItem != null) {
//        if (salesInvoiceItem.getSalesInvoiceId() == null) {
//          salesInvoiceItem.setSalesInvoiceId(getSalesInvoice());
//        }
//        return SalesInvoiceService.selectProductDetailSales(main, salesInvoiceItem);
//      }
//    } catch (Throwable t) {
//      main.rollback(t, "error.select");
//    } finally {
//      main.close();
//    }
//    return null;
//  }
  public List<ProductDetailSales> salesBatchAuto(String filter) {
    MainView main = Jsf.getMain();
    List<ProductDetailSales> productDetailSalesList = null;
    try {
      SalesInvoiceItem salesInvoiceItem = (SalesInvoiceItem) Jsf.getAttribute("selectItem");
      if (salesInvoiceItem != null) {
        if (salesInvoiceItem.getSalesInvoiceId() == null) {
          salesInvoiceItem.setSalesInvoiceId(getSalesInvoice());
        }
        if (salesInvoiceItem.getProductDetailSalesList() == null) {
          productDetailSalesList = SalesInvoiceService.selectProductDetailSales(main, salesInvoiceItem);
          if (StringUtil.isEmpty(productDetailSalesList)) {
            Jsf.warn("error.batch.qty", new String[]{salesInvoiceItem.getProduct().getProductName()});
          }
          return productDetailSalesList;
        } else {
          return salesInvoiceItem.getProductDetailSalesList();
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void openCustomerPopup(Integer id) {
    Jsf.popupForm("/customer/customer.xhtml".replaceFirst("/scm", ""), id, id);
  }

  public String getNextInvoiceNumber() {
    return nextInvoiceNumber;
  }

  public void nextInvoiceNumber(MainView main) {
    try {
      if (getSalesInvoice().getAccountGroupId() == null) {
        getSalesInvoice().setAccountGroupId(getSalesInvoice().getAccountGroupId());
      }
      nextInvoiceNumber = AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getSalesInvoice().getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID,
              false, getSalesInvoice().getFinancialYearId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<Customer> customerFilterAuto(String filter) {
    List<Customer> customerList = ScmLookupExtView.selectCustomerByAccountGroup(UserRuntimeView.instance().getAccountGroup(), filter);
    customerList.add(0, new Customer(0, "All Customer"));
    return customerList;
  }

  public Customer getFilterCustomer() {
    return filterCustomer;
  }

  public void setFilterCustomer(Customer filterCustomer) {
    this.filterCustomer = filterCustomer;
  }

  public void customerFilterSelectedEvent(SelectEvent event) {
    filterCustomer = (Customer) event.getObject();
    salesInvoiceLazyModel = null;
  }

//  private List<AccountingTransactionDetailItem> payableDetailItems;
//  private AccountingTransactionDetail accountingTransactionDetail;
//  private Double adjustedAmount = 0.0;
//  private Double adjustedBalance = 0.0;
//
//  public void prepareSettlementData(MainView main) {
//    if (payableDetailItems == null) {
//      salesInvoice = SalesInvoiceService.selectByPk(main, salesInvoice);
//      if (getSalesInvoice() != null) {
//        FilterObjects filterObjects = new FilterObjects();
//        filterObjects.setSelectedAccountGroup(UserRuntimeView.instance().getAccountGroup());
//        accountingTransactionDetail = AccountingTransactionDetailService.selectTransactionDetailByEntityTypeAndEntityId(main, AccountingConstant.ACC_ENTITY_SALES.getId(), getSalesInvoice().getId());
//        accountingTransactionDetail.setDetailItemPayable(AccountingTransactionDetailItemService.listPagedByPayable(main, accountingTransactionDetail.getAccountingLedgerId().getId(), filterObjects, accountingTransactionDetail.isShowAllBills()));
//        payableDetailItems = accountingTransactionDetail.getDetailItemPayable();
//      }
//    }
//  }
//
//  public void onRowSelect(SelectEvent event) {
//    calculateBalanceAmount();
//  }
//
//  public void onRowUnselect(UnselectEvent event) {
//    calculateBalanceAmount();
//  }
//
//  public void onToggleSelect(ToggleSelectEvent event) {
//    calculateBalanceAmount();
//  }
//
//  private void calculateBalanceAmount() {
//    adjustedAmount = 0.0;
//    adjustedBalance = 0.0;
//    for (AccountingTransactionDetailItem selected : accountingTransactionDetail.getAccountingTransactionDetailItemSelected()) {
//      adjustedAmount += selected.getBalanceAmount();
//      adjustedBalance = getSalesInvoice().getInvoiceAmount() - adjustedAmount;
//      if (adjustedBalance < 0) {
//        adjustedAmount = adjustedAmount + adjustedBalance;
//        adjustedBalance = getSalesInvoice().getInvoiceAmount() - adjustedAmount;
//      }
//    }
//  }
//
//  public void saveSettlement(MainView main) {
//    List<AccountingTransactionDetailItem> payableList = new ArrayList();
//    addPayable(accountingTransactionDetail.getDetailItem(), payableList);
//    addPayableArray(accountingTransactionDetail.getAccountingTransactionDetailItemSelected(), payableList);
//
//    for (AccountingTransactionDetailItem payable : payableList) {
//      LedgerExternalDataService.settle(accountingTransactionDetail, payable, null, payable.getAdjustmentAmount());
//    }
//
//  }
//
//  private void addPayable(List<AccountingTransactionDetailItem> items, List<AccountingTransactionDetailItem> payable) {
//    for (AccountingTransactionDetailItem item : items) {
//      payable.add(item);
//    }
//  }
//
//  private void addPayableArray(AccountingTransactionDetailItem[] items, List<AccountingTransactionDetailItem> payable) {
//    for (AccountingTransactionDetailItem item : items) {
//      payable.add(item);
//    }
//  }
//
//  public List<AccountingTransactionDetailItem> getPayableDetailItems() {
//    return payableDetailItems;
//  }
//
//  public AccountingTransactionDetail getAccountingTransactionDetail() {
//    return accountingTransactionDetail;
//  }
//
//  public Double getAdjustedAmount() {
//    return adjustedAmount;
//  }
//
//  public Double getAdjustedBalance() {
//    return adjustedBalance;
//  }
  public void nextOrPreviousSalesInvoice(MainView main, Integer next) {
    try {
      if (tmpList != null) {
        Integer id = getSalesInvoiceIdByIndex(main, tmpList.indexOf(getSalesInvoice()), next);
        salesInvoice = SalesInvoiceService.selectByPk(main, new SalesInvoice(id));
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private Integer getSalesInvoiceIdByIndex(MainView main, Integer index, Integer next) {
    int rowCount = salesInvoiceLazyModel.getRowCount();
    if ((index >= (rowSize - 1)) && start <= rowCount && next > 0) {
      start += rowSize;
    } else if (next < 0 && index == 0) {
      start -= rowSize;
      index = rowSize;
    }
    loadPreviousNextInvoiceList(main);
    SalesInvoice invoice = tmpList.get((index + next == rowSize) ? 0 : index + next);
    return invoice.getId();
  }

  public boolean isNextButton() {
    int rowCount = salesInvoiceLazyModel == null ? 0 : salesInvoiceLazyModel.getRowCount();
    if (tmpList != null && (tmpList.indexOf(getSalesInvoice()) != (tmpList.size() - 1) || ((start + rowSize) <= rowCount))) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isPreviousButton() {
    if (tmpList == null) {
      return false;
    }
    if (tmpList.indexOf(getSalesInvoice()) == 0 && start == 0) {
      return false;
    } else {
      return true;
    }
  }

  public Double getTotalSchemeDisc() {
    totalSchemeDisc = 0.0;
    if (salesInvoiceItemList != null) {
      for (SalesInvoiceItem invoiceItem : salesInvoiceItemList) {
        if (invoiceItem != null) {
          totalSchemeDisc += invoiceItem.getSchemeDiscountValue() != null ? invoiceItem.getSchemeDiscountValue() : 0;
        }
      }
    }
    return totalSchemeDisc;
  }

  public Double getTotalProductDisc() {
    totalProductDisc = 0.0;
    if (salesInvoiceItemList != null) {
      for (SalesInvoiceItem invoiceItem : salesInvoiceItemList) {
        if (invoiceItem != null) {
          totalProductDisc += invoiceItem.getProductDiscountValue() != null ? invoiceItem.getProductDiscountValue() : 0;
        }
      }
    }
    return totalProductDisc;
  }

  public Date setMinDateforInvoice(MainView main) {
    try {
      Date minDate = null;
      if (lineItemExist) {
        if (initialEntryDate != null) {
          minDate = DateUtils.truncate(initialEntryDate, java.util.Calendar.DAY_OF_MONTH);
        } else {
          minDate = DateUtils.truncate(getSalesInvoice().getInvoiceEntryDate(), java.util.Calendar.DAY_OF_MONTH);
        }
      } else {
        minDate = SystemRuntimeConfig.getMinEntryDate(getSalesInvoice().getCompanyId());;
      }
      return minDate;
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return new Date();
  }

  public Date getMaxDateforInvoice(MainView main) {
    try {
      return SalesInvoiceService.getNearestEntryDate(main, salesInvoice, initialEntryDate);
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return new Date();
  }

  private void loadPreviousNextInvoiceList(MainView main) {
    main.getPageData().setSearchKeyWord(null);
    AppPage.move(main.getPageData(), start, rowSize, sortColumn, sOrder.name());
    tmpList = (SalesInvoiceService.listPaged(main, getSalesInvoice().getCompanyId(), getSalesInvoice().getAccountGroupId(), getSalesInvoiceFilter(), getFilterCustomer(), getSalesInvoice().getFinancialYearId()));
  }

  public void export(MainView main, String type) {
    try {
      String name;
      if (type.equals("IMPORT")) {
        name = getSalesInvoice().getInvoiceNo().replace("/", "_");
      } else {
        name = "Sales_Invoice_Items_";
      }
      if (type.equals("IMPORT")) {
        LineItemExport.exportSalesInvocieItemsForImport(name, main, getSalesInvoiceItemList(), getSalesInvoice());
      } else {
        LineItemExport.exportSalesInvocieItems(name, main, getSalesInvoiceItemList(), getSalesInvoice().getCompanyId(), getSalesInvoice().getCustomerId(), getSalesInvoice(), getHsnList());
      }
    } catch (Throwable t) {
      main.error(t, "error.export", main.getAppUser().isRoot() ? new String[]{t.getMessage()} : null);
    } finally {
      main.close();
    }
  }

  private void getInvoiceByHsnList(List<SalesInvoiceItem> salesInvoiceItemList) {
    hsnMap = new LinkedHashMap<>();
    for (SalesInvoiceItem invoiceItem : salesInvoiceItemList) {
      if (invoiceItem.getProduct() != null) {
        InvoiceByHsn invoiceByHsn = new InvoiceByHsn(invoiceItem);
        if (hsnMap.containsKey(invoiceByHsn.getHsnHash())) {
          InvoiceByHsn obj = hsnMap.get(invoiceByHsn.getHsnHash());
          obj.setTaxValue(obj.getTaxValue() + invoiceByHsn.getTaxValue());
          obj.setTaxableValue(obj.getTaxableValue() + invoiceByHsn.getTaxableValue());
          obj.setTotalvalue(obj.getTotalvalue() + invoiceByHsn.getTotalvalue());
          hsnMap.put(invoiceByHsn.getHsnHash(), obj);
        } else {
          hsnMap.put(invoiceByHsn.getHsnHash(), invoiceByHsn);
        }
      }
    }
    if (getSalesInvoice().getServiceId() != null) {
      InvoiceByHsn invoiceByHsn = new InvoiceByHsn(salesInvoice, true);
      hsnMap.put(invoiceByHsn.getHsnHash(), invoiceByHsn);
    }
    if (getSalesInvoice().getServices2Id() != null) {
      InvoiceByHsn invoiceByHsn = new InvoiceByHsn(salesInvoice, false);
      hsnMap.put(invoiceByHsn.getHsnHash(), invoiceByHsn);
    }
  }

  public List<Map.Entry<String, InvoiceByHsn>> getHsnList() {
    Set<Map.Entry<String, InvoiceByHsn>> invoiceByHsnList = null;
    if (hsnMap != null) {
      invoiceByHsnList = hsnMap.entrySet();
      return new ArrayList<Map.Entry<String, InvoiceByHsn>>(invoiceByHsnList);
    }
    return null;
  }

  private AccountingFinancialYear accountingFinancialYear;

//  public List<AccountingFinancialYear> getFinancialYearList() {
//    return UserRuntimeView.instance().getCompany().getFinancialYearList();
//  }
  public void setAccountingFinancialYear(AccountingFinancialYear accountingFinancialYear) {
    this.accountingFinancialYear = accountingFinancialYear;
  }

  public void calculateTotalDiscounts(MainView main) {
    try {
      getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
      totalSchemeDiscount = 0.0;
      totalInvoiceDiscount = 0.0;
      totalProductDiscount = 0.0;
      totalQty = 0.0;
      nearExpiredProducts = 0;
      totalSgst = 0.0;
      totalCgst = 0.0;
      totalIgst = 0.0;
      if (getSalesInvoiceItemList() != null) {
        for (SalesInvoiceItem list : getSalesInvoiceItemList()) {
          totalSchemeDiscount += list.getSchemeDiscountValue() != null ? list.getSchemeDiscountValue() : 0;
          totalProductDiscount += list.getProductDiscountValue() != null ? list.getProductDiscountValue() : 0;
          totalInvoiceDiscount += list.getInvoiceDiscountValue() != null ? list.getInvoiceDiscountValue() : 0;
          totalQty += (list.getProductQty() != null ? list.getProductQty() : 0) + (list.getProductQtyFree() != null ? list.getProductQtyFree() : 0);
          totalSgst += list.getValueSgst() != null ? list.getValueSgst() : 0;
          totalCgst += list.getValueCgst() != null ? list.getValueCgst() : 0;
          totalIgst += list.getValueIgst() != null ? list.getValueIgst() : 0;
          if (list.getIsNearExpiry() != null) {
            nearExpiredProducts += list.getIsNearExpiry() <= 0 ? 1 : 0;
          }
        }
      }
//                TCS Calculations
      if (StringUtil.equalsInt(getSalesInvoice().getCompanyId().getTcsEnabled(), 1)) {
        TaxCode taxCode = TaxCodeService.selectByPk(main, AccountingConstant.TAX_CODE_TCS);
        Double customerTotalSales = CustomerService.customerTotalSalesValue(main, salesInvoice);
        getTaxCalculator().calculateTcsonSales(salesInvoice, salesInvoiceItemList, customerTotalSales, taxCode);
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  public Double getTotalSchemeDiscount() {
    return totalSchemeDiscount;
  }

  public Double getTotalInvoiceDiscount() {
    return totalInvoiceDiscount;
  }

  public Double getTotalProductDiscount() {
    return totalProductDiscount;
  }

  public Double getTotalQty() {
    return totalQty;
  }

  public Integer getNearExpiredProducts() {
    return nearExpiredProducts;
  }

  public Double getTotalCgst() {
    return totalCgst;
  }

  public Double getTotalSgst() {
    return totalSgst;
  }

  public Double getTotalIgst() {
    return totalIgst;
  }

  public void showCustomerHealth() {
    if (getSalesInvoice().getCustomerId() != null && getSalesInvoice().getCustomerId().getId() != null) {
      Jsf.popupForm(FileConstant.CUSTOMER_HEALTH_POPUP, getSalesInvoice(), getSalesInvoice().getCustomerId().getId());
    }
  }

  public void customerAddresschangeEvenet() {
    if (getSalesInvoice().getCustomerAddressId() != null) {
      getSalesInvoice().setCustomerDistrictId(getSalesInvoice().getCustomerAddressId().getDistrictId());
    }
  }

  public boolean isTradeClosed() {
    return tradeClosed;
  }

  public void setTradeClosed(boolean tradeClosed) {
    this.tradeClosed = tradeClosed;
  }

  public void setTradeClosedValue() {
    tradeClosed = false;
    CompanySettings companySettings = getSalesInvoice().getCompanyId().getCompanySettings();
    for (CompanyFinancialYear fy : getSalesInvoice().getCompanyId().getCompanyFinancialYearList()) {
      if (getSalesInvoice().getFinancialYearId().equals(fy.getFinancialYearId())) {
        if (fy.getClosureStatus().equals(1)) {
          tradeClosed = true;
          return;
        }
      }
    }
    if (companySettings != null) {
      if (companySettings.getEnableCloseOfTrade() != null && companySettings.getEnableCloseOfTrade().intValue() == 1) {
        Date date = new Date();
        int dayOfClosing = companySettings.getDayOfClosing() != null ? companySettings.getDayOfClosing() : 1;
        if (salesInvoice != null && getSalesInvoice().getSalesInvoiceStatusId() != null && getSalesInvoice().getSalesInvoiceStatusId().getId().intValue() > SystemConstants.DRAFT) {
          Date entryDate = getSalesInvoice().getInvoiceEntryDate();
          Calendar cal = Calendar.getInstance();
          cal.setTime(entryDate);
          cal.set(Calendar.DAY_OF_MONTH, dayOfClosing + 1);
          cal.add(Calendar.MONTH, 1);
          if (date.after(cal.getTime())) {
            tradeClosed = true;
          }
        }
      }
    }
  }

  public boolean isSalesAgentCompulsory() {
    MainView main = Jsf.getMain();
    try {
      CompanySettings companySettings = getSalesInvoice().getCompanyId().getCompanySettings();
      if (companySettings == null) {
        companySettings = CompanySettingsService.selectByCompany(main.em(), getSalesInvoice().getCompanyId());
        getSalesInvoice().getCompanyId().setCompanySettings(companySettings);
      }
      if (companySettings.getCompulsorySalesAgent() != null && companySettings.getCompulsorySalesAgent().intValue() == SystemConstants.YES) {
        return true;
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return false;
  }

  public boolean isCustomer() {
    return customer;
  }

  public void setCustomer(boolean customer) {
    this.customer = customer;
  }

  private String chooseAnOption;

  public String getChooseAnOption() {
    chooseAnOption = StringUtil.isEmpty(chooseAnOption) ? SystemConstants.EMAIL_OPTION : chooseAnOption;
    return chooseAnOption;
  }

  public void setChooseAnOption(String chooseAnOption) {
    this.chooseAnOption = chooseAnOption;
  }

  public void sendSmsEmail(MainView main) {
    try {
      if (getSalesInvoice().getCompanyId().getCompanySettings() == null) {
        getSalesInvoice().getCompanyId().setCompanySettings(CompanySettingsService.selectByCompany(main.em(), getSalesInvoice().getCompanyId()));
      }
      Notify.salesInvoice(main, salesInvoice, getChooseAnOption(), isCustomer(), null);
    } catch (Throwable t) {
      main.error(t, "error.send", null);
    } finally {
      main.close();
    }
  }

  private void makeDocumentAndNotify(Main main, SalesInvoice salesInvoice) throws Throwable {
    try {
      if (getSalesInvoice().getSalesInvoiceStatusId().getId() != SystemConstants.DRAFT) {
        getSalesInvoice().setPrintMode(SystemConstants.PRINT_PORTRAIT);
        String filePath = DocumentService.getPathSalesInvoice(salesInvoice);
        SalesInvoicePrintView.generatePrint(main, salesInvoice, filePath);
        Notify.salesInvoice(main, salesInvoice, SystemConstants.SMS_OPTION, false, filePath); // Sending sms to sales agent
      }
    } catch (Throwable t) {
      Jsf.error("error.create.document");
      throw t;
    } finally {
      // main.close(); // dont close cause transaction should continue
    }
  }

  public boolean isBatchMissing() {
    if (salesInvoiceItemList != null) {
      for (SalesInvoiceItem list : salesInvoiceItemList) {
        if (list.getProduct() != null) {
          if (list.getProductDetailSales() == null || (list.getProductDetailSales() != null && list.getProductDetailSales().getBatchNo() == null)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean isMenuItemRendered(boolean cstmr) {
    if (cstmr) {
      if (salesInvoice.getCustomerAddressId() != null && (!StringUtil.isEmpty(getSalesInvoice().getCustomerAddressId().getEmail()) || !StringUtil.isEmpty(getSalesInvoice().getCustomerAddressId().getPhone1()))) {
        return true;
      }
    } else {
      if (salesInvoice.getCompanySalesAgentPersonProfileId() != null && (!StringUtil.isEmpty(getSalesInvoice().getCompanySalesAgentPersonProfileId().getEmail()) || !StringUtil.isEmpty(getSalesInvoice().getCompanySalesAgentPersonProfileId().getPhone1()))) {
        return true;
      }
    }
    return false;
  }

  public void setRcCustomer() {
    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();
    boolean csflag = Boolean.valueOf(params.get("customer"));
    setCustomer(csflag);
    setChooseAnOption(SystemConstants.EMAIL_OPTION);
  }

}
