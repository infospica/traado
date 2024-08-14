/*
 * @(#)SalesReturnView.java	1.0 Mon Jan 29 16:45:18 IST 2018
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
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.domain.TaxCode;
import spica.fin.view.FinLookupView;
import spica.scm.common.ConsignmentRate;
import spica.scm.common.ProductSummary;
import spica.scm.common.RelatedItems;
import spica.scm.common.SelectItem;
import spica.scm.domain.Account;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesReturn;
import spica.scm.service.SalesReturnService;
import spica.scm.domain.Company;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.CompanyFinancialYear;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.Consignment;
import spica.scm.domain.Customer;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductPreset;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.SalesReturnCreditSettlement;
import spica.scm.domain.SalesReturnItem;
import spica.scm.domain.SalesReturnSplit;
import spica.scm.domain.SalesReturnStatus;
import spica.scm.export.LineItemExport;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.AccountService;
import spica.scm.service.CompanySettingsService;
import spica.scm.service.ConsignmentService;
import spica.scm.service.CustomerAddressService;
import spica.scm.service.InvoiceLogService;
import spica.scm.service.PrefixTypeService;
import spica.scm.service.ProductBatchService;
import spica.scm.service.ProductDetailService;
import spica.scm.service.ProductPresetService;
import spica.scm.service.ProductService;
import spica.scm.service.SalesAgentService;
import spica.scm.service.SalesReturnItemService;
import static spica.scm.service.SalesReturnService.deleteByPk;
import spica.scm.service.SalesReturnSplitService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.AppUtil;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.scm.util.ReferenceInvoice;
import spica.scm.util.SalesInvoiceUtil;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import static spica.sys.SystemRuntimeConfig.SDF_YYYY_MM_DD;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.entity.core.UserMessageException;

/**
 * SalesReturnView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jan 29 16:45:18 IST 2018
 */
@Named(value = "salesReturnView")
@ViewScoped
public class SalesReturnView implements Serializable {

  private transient SalesReturn salesReturn;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesReturn> salesReturnLazyModel; 	//For lazy loading datatable.
  private transient SalesReturn[] salesReturnSelected;	 //Selected Domain Array
  private transient SalesReturnItem salesReturnItemId;
  private transient TaxCalculator taxCalculator;
  private transient List<SalesReturnItem> salesReturnItemList;
  private transient List<ConsignmentRate> consignmentRateList;
  private transient ConsignmentRate consignmentRate;
  private transient String lrAmount;
  private transient Double oldLrAmount;
  private transient Double lrAmountMax;
  private transient AccountingFinancialYear accountingFinancialYear;
  private transient boolean intraStateSales;
  private transient boolean interStateSales;
  private transient boolean customerEditable;
  private transient boolean lineItemExist;
  private transient HashMap<String, Integer> productHashCode;
  private transient Integer rowIndex;
  private transient Integer salesReturnType;
  private transient String salesReturnTypeFilter;
  private transient String pageTitle;
  private transient List<SelectItem> salesReturnTypeList;
  private transient List<RelatedItems> relatedItemsList;
  private transient AccountingTransactionDetailItem accountingTransactionDetailItem;

  private boolean taxEditable;
  private transient boolean valueMrpChanged;
  private List<TaxCode> taxCodeList;
  private List<SalesReturnItem> selectedSalesReturnItems;
  private String nextInvoiceNumber;
  private transient List<SalesReturn> tmpList;
  private transient int rowSize;
  private transient int start;
  private transient String sortColumn;
  private transient SortOrder sOrder;
  private transient boolean displayReference;
  private transient boolean displayReferenceInDialog;
  private Double totalSchemeDiscount;
  private Double totalInvoiceDiscount;
  private Double totalProductDiscount;
  private Double totalQty;
  private Double totalCgst;
  private Double totalSgst;
  private Double totalIgst;
  private Double qtyInSettlement;
  private Double ratePerProd;
  private List<ReferenceInvoice> creditSettlementList;
  private ReferenceInvoice selectedCreditSettlement;
  private transient Integer rowNumber;
  private transient boolean limitReturnQty;
  private List<SalesReturnSplit> salesReturnSplitList;
  private boolean tradeClosed;

  /**
   * Default Constructor.
   */
  public SalesReturnView() {
    super();
  }

  @PostConstruct
  public void init() {

    Integer invoiceId = (Integer) Jsf.popupParentValue(Integer.class);
    if (invoiceId != null) {
      getSalesReturn().setId(invoiceId);
    }
    accountingTransactionDetailItem = (AccountingTransactionDetailItem) Jsf.popupParentValue(AccountingTransactionDetailItem.class);
    if (accountingTransactionDetailItem != null) {
      getSalesReturn().setId(accountingTransactionDetailItem.getAccountingTransactionDetailId().getAccountingTransactionId().getEntityId());
    }
  }

  /**
   * Return SalesReturn.
   *
   * @return SalesReturn.
   */
  public SalesReturn getSalesReturn() {
    if (salesReturn == null) {
      salesReturn = new SalesReturn();
    }
    if (salesReturn.getCompanyId() == null) {
      salesReturn.setCompanyId(UserRuntimeView.instance().getCompany());
      salesReturn.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
      salesReturn.setAccountGroupId(UserRuntimeView.instance().getAccountGroup());
    }
    return salesReturn;
  }

  /**
   * Set SalesReturn.
   *
   * @param salesReturn.
   */
  public void setSalesReturn(SalesReturn salesReturn) {
    this.salesReturn = salesReturn;
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

  public boolean isValueMrpChanged() {
    return valueMrpChanged;
  }

  public void setValueMrpChanged(boolean valueMrpChanged) {
    this.valueMrpChanged = valueMrpChanged;
  }

  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  public List<RelatedItems> getRelatedItemsList() {
    return relatedItemsList;
  }

  public void setRelatedItemsList(List<RelatedItems> relatedItemsList) {
    this.relatedItemsList = relatedItemsList;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public List<SalesReturnItem> getSalesReturnItemList() {
    return salesReturnItemList;
  }

  public void setSalesReturnItemList(List<SalesReturnItem> salesReturnItemList) {
    this.salesReturnItemList = salesReturnItemList;
  }

  public boolean isIntraStateSales() {
    return intraStateSales;
  }

  public void setIntraStateSales(boolean intraStateSales) {
    this.intraStateSales = intraStateSales;
  }

  public boolean isInterStateSales() {
    return interStateSales;
  }

  public void setInterStateSales(boolean interStateSales) {
    this.interStateSales = interStateSales;
  }

  public ConsignmentRate getConsignmentRate() {
    return consignmentRate;
  }

  public void setConsignmentRate(ConsignmentRate consignmentRate) {
    this.consignmentRate = consignmentRate;
  }

  public boolean isLineItemExist() {
    return lineItemExist;
  }

  public void setLineItemExist(boolean lineItemExist) {
    this.lineItemExist = lineItemExist;
  }

//  public List<Account> getAccountList() {
//    return accountList;
//  }
//
//  public void setAccountList(List<Account> accountList) {
//    this.accountList = accountList;
//  }
//  public Account getAccountId() {
//    return accountId;
//  }
//
//  public void setAccountId(Account accountId) {
//    this.accountId = accountId;
//  }
  public String getLrAmount() {
    return lrAmount;
  }

  public void setLrAmount(String lrAmount) {
    this.lrAmount = lrAmount;
  }

  public Double getOldLrAmount() {
    return oldLrAmount;
  }

  public void setOldLrAmount(Double oldLrAmount) {
    this.oldLrAmount = oldLrAmount;
  }

  public Double getLrAmountMax() {
    return lrAmountMax;
  }

  public void setLrAmountMax(Double lrAmountMax) {
    this.lrAmountMax = lrAmountMax;
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

  public SalesReturnItem getSalesReturnItemId() {
    if (salesReturnItemId == null) {
      salesReturnItemId = new SalesReturnItem();
    } else {
      if (qtyInSettlement != null && qtyInSettlement > 0) {
        ratePerProd = (salesReturnItemId.getValueAssessable() != null ? salesReturnItemId.getValueAssessable() : 0) / qtyInSettlement;
      }

    }
    return salesReturnItemId;
  }

  public void setSalesReturnItemId(SalesReturnItem salesReturnItemId) {
    this.salesReturnItemId = salesReturnItemId;
  }

  public Integer getRowIndex() {
    return rowIndex;
  }

  public void setRowIndex(Integer rowIndex) {
    this.rowIndex = rowIndex;
  }

  public Integer getSalesReturnType() {
    return salesReturnType;
  }

  public void setSalesReturnType(Integer salesReturnType) {
    this.salesReturnType = salesReturnType;
  }

  public String getSalesReturnTypeFilter() {
    return salesReturnTypeFilter;
  }

  public void setSalesReturnTypeFilter(String salesReturnTypeFilter) {
    this.salesReturnTypeFilter = salesReturnTypeFilter;
  }

  public String getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  public List<ConsignmentRate> getConsignmentRateList() {
    return consignmentRateList;
  }

  public void setConsignmentRateList(List<ConsignmentRate> consignmentRateList) {
    this.consignmentRateList = consignmentRateList;
  }

  /**
   * Return LazyDataModel of SalesReturn.
   *
   * @return
   */
  public LazyDataModel<SalesReturn> getSalesReturnLazyModel() {
    return salesReturnLazyModel;
  }

  /**
   * Return SalesReturn[].
   *
   * @return
   */
  public SalesReturn[] getSalesReturnSelected() {
    return salesReturnSelected;
  }

  /**
   * Set SalesReturn[].
   *
   * @param salesReturnSelected
   */
  public void setSalesReturnSelected(SalesReturn[] salesReturnSelected) {
    this.salesReturnSelected = salesReturnSelected;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesReturn(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else if (UserRuntimeView.instance().getAccountGroup() == null) {
            main.error("account.group.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else {
            setSalesReturnNewForm(main);
            setSalesReturnPageTitle();
            getSalesReturn().setDecimalPrecision(getSalesReturn().getAccountGroupId().getCustomerDecimalPrecision() == null ? 2 : getSalesReturn().getAccountGroupId().getCustomerDecimalPrecision());
          }
        } else if (main.isEdit() && !main.hasError()) {

          setSalesReturnPageTitle();
          setSalesReturn((SalesReturn) SalesReturnService.selectByPk(main, getSalesReturn()));
          CompanySettingsService.selectIfNull(main, getSalesReturn().getCompanyId());
          getSalesReturn().setDecimalPrecision(getSalesReturn().getAccountGroupId().getCustomerDecimalPrecision() == null ? 2 : getSalesReturn().getAccountGroupId().getCustomerDecimalPrecision());
          getSalesReturn().setBusinessArea(SalesInvoiceUtil.getSalesMode(getSalesReturn().getCompanyId(), getSalesReturn().getCustomerId()));
          setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesReturn().getTaxProcessorId().getProcessorClass()));
          setLimitReturnQty(SalesReturnService.isLimitReturnQty(main, getSalesReturn().getAccountGroupId()));
          getSalesReturn().setLimitReturnQty(limitReturnQty);
          setSalesReturnItemList(SalesReturnItemService.selectSalesReturnItemList(main, getSalesReturn()));
          getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
          updateConsignmentDetails(main, getSalesReturn().getConsignmentId());
          setValueMrpChanged(SalesReturnService.isProductValueMrpChanged(main, getSalesReturn()));
          //getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
          //updateProductHashCode(getSalesReturnItemList());
          setSalesMode(getSalesReturn().getCompanyId(), getSalesReturn().getCustomerId());
          if (getSalesReturnItemList() != null && getSalesReturnItemList().isEmpty()) {
            addNewSalesReturnItem();
          }

//          if (!isDraft()) {
//            setRelatedItemsList(SalesReturnService.selectRelatedItemsOfSalesReturn(main, getSalesReturn()));
//          }
          lookupGstTaxCode();
          hasLineItem();
          nextInvoiceNumber(main);
        } else if (main.isList()) {
          main.getPageData().setSearchKeyWord(null);
          resetSalesReturnView();
          getSalesReturn().setCompanyId(null);
          setPageTitle("Sales Return");
          setRelatedItemsList(null);
          setTaxCodeList(null);
          setTaxEditable(false);
          setValueMrpChanged(false);
          setLineItemExist(false);
          //setAccountList(null);
          loadSalesReturnList(main);
        }
        CompanySettingsService.selectIfNull(main, getSalesReturn().getCompanyId());
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
   * @param main
   * @return
   */
  public String actionNewForm(MainView main) {
    main.setViewType(ViewTypes.newform);
    return null;
  }

  private void setSalesReturnNewForm(MainView main) {
    getSalesReturn().reset();
    getProductHashCode().clear();
    CompanySettingsService.selectIfNull(main, getSalesReturn().getCompanyId());
    getSalesReturn().setSalesReturnType(getSalesReturnType() == null ? SystemConstants.SALES_RETURN_TYPE_SALEABLE_AND_DAMAGED : getSalesReturnType());
    getSalesReturn().setTaxProcessorId(getSalesReturn().getCompanyId().getCountryTaxRegimeId().getTaxProcessorId());
    setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesReturn().getTaxProcessorId().getProcessorClass()));
    getSalesReturn().setInvoiceNo("DR-" + AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getSalesReturn().getAccountGroupId(), PrefixTypeService.SALES_RETURN_PREFIX_ID, true, getSalesReturn().getFinancialYearId()));
    getSalesReturn().setInvoiceDate(new Date());
    getSalesReturn().setEntryDate(new Date());
    getSalesReturn().setSalesReturnStatusId(new SalesReturnStatus(SystemConstants.DRAFT));
    getSalesReturn().setAccountingLedgerId(accountingLedgerSalesReturnAuto("").get(0));
    getSalesReturn().setIsTaxable(SystemConstants.DEFAULT);
  }

  private void resetSalesReturnView() {
    setTaxCalculator(null);
    setSalesReturnItemList(null);
    setLrAmount(null);
    setLrAmountMax(null);
    setConsignmentRate(null);
    setInterStateSales(false);
    setIntraStateSales(false);
    getProductHashCode().clear();
    setSalesReturnType(SystemConstants.SALES_RETURN_TYPE_SALEABLE);
    setConsignmentRateList(null);
  }

  private void setSalesReturnPageTitle() {
    if (isDamagedSalesReturn()) {
      setPageTitle("Sales Return - Damaged");
    } else {
      setPageTitle("Sales Return - Saleable");
    }
  }

  public List<SelectItem> getSalesReturnTypeList() {
    if (salesReturnTypeList == null) {
      salesReturnTypeList = new ArrayList<>();
      salesReturnTypeList.add(new SelectItem("Saleable Return Entry", SystemConstants.SALES_RETURN_TYPE_SALEABLE));
      salesReturnTypeList.add(new SelectItem("Damaged Return Entry", SystemConstants.SALES_RETURN_TYPE_DAMAGED));
    }
    return salesReturnTypeList;
  }

  /**
   *
   * @param main
   * @param consignment
   */
  private void updateConsignmentDetails(MainView main, Consignment consignment) {
    if (consignment != null) {
      setOldLrAmount(getSalesReturn().getTotalExpenseAmount());
      try {
        setConsignmentRate(ConsignmentService.selectConsignmentRateByAccountGroupConsignment(main, getSalesReturn().getCustomerId(), getSalesReturn().getAccountGroupId(), getSalesReturn().getConsignmentId().getId()));
      } finally {
        main.close();
      }
      setConsignmentRecieptDetails(getConsignmentRate());
    }
  }

  /**
   *
   * @param salesReturnItemList
   */
  // private void updateProductHashCode(List<SalesReturnItem> salesReturnItemList) {
//    for (SalesReturnItem salesReturnItem : salesReturnItemList) {
//      if (salesReturnItem.getProductSummary() != null && !StringUtil.isEmpty(salesReturnItem.getProductSummary().getProductHash())) {
//        getProductHashCode().put(salesReturnItem.getProductSummary().getProductHash(), salesReturnItem.getProductSummary().getProductId());
//      }
//    }
  // }
  /**
   * Create salesReturnLazyModel.
   *
   * @param main
   */
  private void loadSalesReturnList(final MainView main) {
    if (salesReturnLazyModel == null) {
      salesReturnLazyModel = new LazyDataModel<SalesReturn>() {
        private List<SalesReturn> list;

        @Override
        public List<SalesReturn> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            list = null;
            if (getSalesReturn().getCompanyId() != null && getSalesReturn().getAccountGroupId() != null) {
              start = first;
              rowSize = pageSize;
              sortColumn = sortField;
              sOrder = sortOrder;
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = SalesReturnService.listPagedByAccountGroup(main, getSalesReturn().getCompanyId().getId(), getSalesReturn().getAccountGroupId(), getSalesReturnTypeFilter(), getSalesReturn().getFinancialYearId());
              tmpList = list;
              main.commit(salesReturnLazyModel, first, pageSize);
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
        public Object getRowKey(SalesReturn salesReturn) {
          return salesReturn.getId();
        }

        @Override
        public SalesReturn getRowData(String rowKey) {
          if (list != null) {
            for (SalesReturn obj : list) {
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

  private boolean hasSalesReturnItem() {
    boolean rvalue = false;
    if (getSalesReturnItemList() != null) {
      rvalue = true; //***** ATLEAST ONE LINE IS ADDED
      for (SalesReturnItem salesReturnItem : getSalesReturnItemList()) {

        if (salesReturnItem.getTaxCodeId() == null || salesReturnItem.getValueRate() == null
                || (salesReturnItem.getProductQuantity() == null && salesReturnItem.getProductQuantityDamaged() == null)) {
// CHECKING WHETHER EXISTS TaxCodeID ,ValueRate & QTY all three should be there to CONFIRM
          rvalue = false;
          break;
        }
        if (salesReturnItem.getSalesInvoiceId() == null && salesReturnItem.getExpectedLandingRate() == null) {
// CHECKING WHETHER EXISTS LandRate for records without SalesInvoice Reference >>>ProductEntryDetailId
          rvalue = false;
          break;
        }
      }
    }
    return rvalue;
  }

  private void uploadFiles() {
    String SUB_FOLDER = "scm_sales_return/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @param status
   * @return the page to display.
   */
  public String saveSalesReturn(MainView main, Integer status) {
    if ((SystemConstants.CONFIRMED.equals(status)) && !hasSalesReturnItem()) {
      main.error("error.product.missing");
      return null;
    }
    if ((SystemConstants.CONFIRMED.equals(status)) && !hasReferenceNo()) {
      main.error("error.missing.referenceNo");
      return null;
    }
    if ((SystemConstants.CONFIRMED.equals(status)) && !hasReferenceDate()) {
      main.error("error.missing.referenceDate");
      return null;
    }
    if (isValueMrpChanged()) {
      main.error("fatal.error.mrp.changed");
      return null;
    }
    getSalesReturn().setSalesReturnStatusId(new SalesReturnStatus(status));
    return saveOrCloneSalesReturn(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesReturn(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneSalesReturn(main, "clone");
  }

  /**
   *
   * @param main
   */
  public void actionResetSalesReturn(MainView main) {
    try {
      if (SalesReturnService.isNegativeStock(main, getSalesReturn())) {
        throw new UserMessageException("error.sales.return");
      }
      InvoiceLogService.insertSalesReturnLog(main, salesReturn, salesReturnItemList, InvoiceLogService.DRAFT);
      SalesReturnService.resetSalesReturnStatusToDraft(main, getSalesReturn());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void actionResetAllSalesReturn(MainView main) {
    try {
      SalesReturnService.resetAllSalesReturnToDraft(main, getSalesReturn().getAccountGroupId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesReturn(MainView main, String key) {
    try {
      if (!isUsedByOtherUser(main)) {
        uploadFiles(); //File upload
        boolean nextPrv = false;
        if (null != key) {
          switch (key) {
            case "save":
              if (getSalesReturn().getId() == null) {
                nextPrv = true;
              }
              SalesReturnService.insertOrUpdateSalesReturn(main, getSalesReturn(), getSalesReturnItemList(), getTaxCalculator());
              break;
            case "clone":
              SalesReturnService.clone(main, getSalesReturn());
              break;
          }
          main.commit("success." + key);
          if (getSalesReturn().getSalesReturnStatusId().getId().intValue() == 5 && (getSalesReturn().getReturnSplitForGstFiling() == null
                  || (getSalesReturn().getReturnSplitForGstFiling() != null && getSalesReturn().getReturnSplitForGstFiling().intValue() == 0))) {
            main.setViewType(ViewTypes.list);
          } else {
            main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
          }
          if (nextPrv) {
            loadPreviousNextSalesReturnList(main);
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many SalesReturn.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesReturn(MainView main) {
    Integer id = null;
    try {
      if (main.isList() && !StringUtil.isEmpty(salesReturnSelected)) {
        for (SalesReturn e : salesReturnSelected) {
          InvoiceLogService.insertSalesReturnLog(main, salesReturn, salesReturnItemList, InvoiceLogService.DELETE);
          id = e.getId();
          deleteByPk(main, e);
          main.em().commit();
        }
        // SalesReturnService.deleteByPkArray(main, getSalesReturnSelected()); //many record delete from list
        main.commit("success.delete");
        salesReturnSelected = null;
      } else {
        id = getSalesReturn().getId();
        InvoiceLogService.insertSalesReturnLog(main, salesReturn, salesReturnItemList, InvoiceLogService.DELETE);
        SalesReturnService.deleteByPk(main, getSalesReturn());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
          main.setViewType(ViewTypes.newform);
        }
      }
    } catch (Throwable t) {
      AppUtil.referenceError(main, t, id);
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  private void setSalesMode(Company company, Customer customer) {
    setInterStateSales(false);
    setIntraStateSales(false);
    if (customer != null) {
      int salesMode = SalesInvoiceUtil.getSalesMode(company, customer);
      setIntraStateSales(SalesInvoiceUtil.INTRA_STATE_SALES == salesMode);
      setInterStateSales(SalesInvoiceUtil.INTER_STATE_SALES == salesMode);
      if (!StringUtil.isEmpty(customer.getGstNo()) && getSalesReturn().getSezZone() != null && getSalesReturn().getSezZone().intValue() == 1) {
        setIntraStateSales(false);
        setInterStateSales(true);
      }
    }
  }

  /**
   *
   * @return
   */
  public boolean isConfirmed() {
    return (getSalesReturn().getSalesReturnStatusId() != null && getSalesReturn().getSalesReturnStatusId().getId().equals(SystemConstants.CONFIRMED));
  }

  /**
   *
   * @return
   */
  public boolean isDraft() {
    return (getSalesReturn().getSalesReturnStatusId() != null && getSalesReturn().getSalesReturnStatusId().getId().equals(SystemConstants.DRAFT));
  }

  public boolean isDamagedSalesReturn() {
    return (getSalesReturn().getSalesReturnType() != null && getSalesReturn().getSalesReturnType().equals(SystemConstants.SALES_RETURN_TYPE_DAMAGED));
  }

  public boolean isSaleableSalesReturn() {
    return (getSalesReturn().getSalesReturnType() != null && getSalesReturn().getSalesReturnType().equals(SystemConstants.SALES_RETURN_TYPE_SALEABLE));
  }

  public boolean isSaleableAndDamagedSalesReturn() {
    return (getSalesReturn().getSalesReturnType() != null && getSalesReturn().getSalesReturnType().equals(SystemConstants.SALES_RETURN_TYPE_SALEABLE_AND_DAMAGED));
  }

  public boolean isTaxable() {
    return (getSalesReturn().getIsTaxable() != null ? getSalesReturn().getIsTaxable().equals(SystemConstants.DEFAULT) : true);
  }

  public void salesReturnTypeFilterSelectEvent(MainView main) {
    main.getPageData().reset();
  }

  /**
   * Method to get the list of Customer.
   *
   * @param filter
   * @return
   */
  public List<Customer> customerAuto(String filter) {
    List<Customer> list = ScmLookupExtView.lookupCustomerBySalesInvoice(getSalesReturn().getCompanyId(), getSalesReturn().getAccountGroupId(), filter);
    return list;
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> accountingLedgerSalesReturnAuto(String filter) {
    return FinLookupView.ledgerSalesReturnAuto(filter, getSalesReturn().getCompanyId().getId());
  }

  public void customerSelectEventHandler(SelectEvent event) {
    Customer cust = (Customer) event.getObject();
    MainView main = Jsf.getMain();
    try {
      if (cust != null) {
        setSalesMode(getSalesReturn().getCompanyId(), cust);
        getSalesReturn().setCustomerAddressId(CustomerAddressService.selectCustomerRegisteredAddress(main, cust));
        getSalesReturn().setSezZone((cust.getTaxable() != null && !StringUtil.isEmpty(cust.getGstNo()) && cust.getTaxable().intValue() == 2) ? 1 : 0);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public boolean isCustomerEditable() {
    if (getSalesReturn() != null && getSalesReturn().getId() == null) {
      customerEditable = true;
    } else if (StringUtil.isEmpty(getSalesReturnItemList()) || (getSalesReturnItemList() != null && getSalesReturnItemList().get(0).getProductSummary() == null)) {
      customerEditable = true;
    } else {
      customerEditable = false;
    }
    return customerEditable;
  }

  public void setCustomerEditable(boolean customerEditable) {
    this.customerEditable = customerEditable;
  }

  public void openSalesReturnReceiptPopup() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesReturnReceiptView().replaceFirst("/scm", ""), getSalesReturn(), null);// opens a new form if id is null else edit
    }
  }

  public List<ConsignmentRate> lookUpConsignmentByCustomerAccountGroup(MainView main) {
    try {
      if (StringUtil.isEmpty(getConsignmentRateList()) && getSalesReturn().getCustomerId() != null && getSalesReturn().getAccountGroupId() != null) {
        if (getSalesReturn() != null && getSalesReturn().getConsignmentId() != null) {
          setConsignmentRateList((List<ConsignmentRate>) ConsignmentService.lookUpConsignmentByCustomerAccountGroup(main, getSalesReturn().getCustomerId(), getSalesReturn().getAccountGroupId(), getSalesReturn().getConsignmentId().getId()));
        } else {
          setConsignmentRateList((List<ConsignmentRate>) ConsignmentService.lookUpConsignmentByCustomerAccountGroup(main, getSalesReturn().getCustomerId(), getSalesReturn().getAccountGroupId(), null));
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return getConsignmentRateList();
  }

  public void consignmentRateItemSelectEvent(SelectEvent event) {
    setLrAmount("");
    ConsignmentRate rate = (ConsignmentRate) event.getObject();
    setConsignmentRate(rate);
    if (getSalesReturn() != null && getSalesReturn().getId() != null && getSalesReturn().getConsignmentId() != null) {
      if (getSalesReturn().getConsignmentId().getId().equals(rate.getId())) {
        getSalesReturn().setTotalExpenseAmount(getOldLrAmount());
      } else {
        getSalesReturn().setTotalExpenseAmount(null);
      }
    } else {
      getSalesReturn().setTotalExpenseAmount(null);
    }

    if (rate != null) {
      setConsignmentRecieptDetails(rate);
      Consignment c = new Consignment();
      c.setId(rate.getId());
      getSalesReturn().setConsignmentId(c);
    } else {
      getSalesReturn().setConsignmentId(null);
    }
  }

  private void setConsignmentRecieptDetails(ConsignmentRate rate) {
    StringBuilder sb = new StringBuilder();
    if (rate != null && rate.getId() != null) {
      if (rate.getLrAmount() == 0 && rate.getTotalExpenseAmount() > 0) {
        setLrAmount("( " + String.valueOf(rate.getTotalExpenseAmount()) + ")");
        lrAmountMax = rate.getTotalExpenseAmount();
        if (rate.getNoOfInvoice() != null && rate.getNoOfInvoice() == 1 && getSalesReturn().getTotalExpenseAmount() == null) {
          getSalesReturn().setTotalExpenseAmount(rate.getTotalExpenseAmount());
        }
      } else if (rate.getTotalExpenseAmount().equals(rate.getLrAmount()) && rate.getTotalExpenseAmount() > 0) {
        setLrAmount("( " + String.valueOf(rate.getTotalExpenseAmount()) + ")");
      } else if (rate.getLrAmountDifference() > 0) {
        if (getSalesReturn().getTotalExpenseAmount() != null && getSalesReturn().getTotalExpenseAmount() > 0) {
          lrAmountMax = getSalesReturn().getTotalExpenseAmount() + rate.getLrAmountDifference();
        } else {
          lrAmountMax = rate.getLrAmountDifference();
        }
        sb.append(rate.getLrAmountDifference());
        sb.append(" ( ").append(rate.getTotalExpenseAmount()).append(" )");
        setLrAmount(sb.toString());
      }
    }
  }

  public List<ProductSummary> completeProductDetail(SalesReturnItem salesReturnItem) {
    List<ProductSummary> productSummeryList = new ArrayList<>();
    MainView main = Jsf.getMain();
    try {
      if (salesReturnItem != null && salesReturnItem.getProduct() != null) {
        if (salesReturnItem.getProductDetailList() == null) {
          productSummeryList = getTaxCalculator().productSummaryForSalesReturnAuto(main, getSalesReturn(), salesReturnItem);
          salesReturnItem.setProductDetailList(productSummeryList);
        } else {
          productSummeryList = salesReturnItem.getProductDetailList();
        }
      }
      for (ProductSummary productSummary : productSummeryList) {
        if (productSummary.getProductBatchId() == null) {
          productSummary.setProductCode(" ");
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return productSummeryList;
  }

  public void productSelectEvent(SalesReturnItem salesReturnItem, Integer rowIndex) {
    if (salesReturnItem != null && salesReturnItem.getProductSummary() != null) {
      setRowIndex(rowIndex);
      MainView main = Jsf.getMain();
      try {
        salesReturnItem.resetSalesReturnItem();
        salesReturnItem.setProduct(ProductService.selectByPk(main, new Product(salesReturnItem.getProductSummary().getProductId())));
        salesReturnItem.setHsnCode(salesReturnItem.getProductSummary().getHsnCode());
        hasLineItem();
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  public void productBatchSelectEvent(SalesReturnItem salesReturnItem, Integer rowIndex) {
    if (salesReturnItem != null && salesReturnItem.getProductBatchSummary() != null) {
      getSalesReturn().setLimitReturnQty(limitReturnQty);
      setRowIndex(rowIndex);
      MainView main = Jsf.getMain();
      try {
        if (salesReturnItem.getProductBatchSummary().getAccountStatusId() != null && salesReturnItem.getProductBatchSummary().getAccountStatusId().intValue() == 1) {
          if (salesReturnItem.getProductBatchSummary().getProductBatchId() == null) {
            salesReturnItem.setProductPreset(ProductPresetService.selectByPk(main, new ProductPreset(salesReturnItem.getProductBatchSummary().getProductPresetId())));
            salesReturnItem.setAccountId(salesReturnItem.getProductPreset().getAccountId());
            salesReturnItem.setProductBatchId(null);
            salesReturnItem.resetSalesReturnItem();
            actionOpenProductPopup(salesReturnItem, rowIndex);
          } else {
            salesReturnItem.setProduct(ProductService.selectByPk(main, new Product(salesReturnItem.getProductBatchSummary().getProductId())));
            salesReturnItem.setProductBatchId(ProductBatchService.selectByPk(main, new ProductBatch(salesReturnItem.getProductBatchSummary().getProductBatchId())));
            //salesReturnItem.setProductPreset(ProductPresetService.selectByPk(main, new ProductPreset(salesReturnItem.getProductSummary().getProductPresetId())));
            updateSalesReturnItem(main, salesReturnItem);
          }
          salesReturnItem.setHsnCode(salesReturnItem.getProductBatchSummary().getHsnCode());
          loadCreditSettlementList(main, salesReturnItem);
          if (salesReturnItem.getId() != null) {
            clearCreditSettlement(main, salesReturnItem);
          }
          if (StringUtil.isEmpty(getCreditSettlementList())) {
            salesReturnItem.setCreditExist(0);
          } else {
            salesReturnItem.setCreditExist(1);
          }
          salesReturnItem.setCreditSettlementAmount(null);
        } else {
          main.error("error.inactive.account");
          Jsf.update("inactiveAccountMessage");
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  public void taxCodeSelectEvent(MainView main, SalesReturnItem salesReturnItem) {
    if (salesReturnItem != null && salesReturnItem.getProductBatchSummary() != null) {
      try {
        salesReturnItem.setValueRate(null);
        SalesReturnItemService.updateSalesReturnItemFromSales(main, salesReturnItem, getSalesReturn());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  public String actionOpenProductPopup(SalesReturnItem salesReturnItem, Integer index) {
    if (salesReturnItem != null && salesReturnItem.getProduct() != null) {
      setRowIndex(index);
      salesReturnItem.setSalesReturnId(salesReturn);
      Jsf.popupForm(FileConstant.PRODUCT_SALES_RETURN_POPUP, salesReturnItem, salesReturnItem.getProduct().getId());
      Jsf.update("currentRow");
    }
    return null;
  }

  /**
   *
   * @param main
   * @param salesReturnItem
   */
  private void updateSalesReturnItem(MainView main, SalesReturnItem salesReturnItem) {
    salesReturnItem.resetSalesReturnItem();
    getTaxCalculator().updateSalesReturnItem(main, salesReturnItem);
    salesReturnItem.setProductHashCode(salesReturnItem.getProductBatchSummary().getProductCode());

    //getProductHashCode().put(salesReturnItem.getProductSummary().getProductHash(), salesReturnItem.getProductSummary().getProductId());
    //salesReturnItem.after();
    salesReturnItem.setSalesInvoiceItemIds(salesReturnItem.getProductBatchSummary().getSalesInvoiceItemIds());
    SalesReturnItemService.updateSalesReturnItemFromSales(main, salesReturnItem, getSalesReturn());
    ProductPreset productPreset = ProductPresetService.selectProductPresetByProductAndAccount(main, salesReturnItem.getProduct().getId(), salesReturnItem.getAccountId());
    if (salesReturnItem.getSalesInvoiceId() == null) {
      salesReturnItem.setProductDetailId(ProductDetailService.getProductDetailByBatchForSalesReturn(main, salesReturnItem.getAccountId(),
              salesReturnItem.getProductBatchId(), productPreset));
    }
    if (salesReturnItem.getSalesInvoiceId() == null) {
      calculateLandingRate(salesReturnItem, productPreset);
    }
    if (salesReturnItem.getSalesInvoiceId() != null) {
      salesReturnItem.setRefInvoiceNo(salesReturnItem.getSalesInvoiceId().getInvoiceNo());
      salesReturnItem.setRefInvoiceDate(salesReturnItem.getSalesInvoiceId().getInvoiceEntryDate());
    }
    //removeProductHashKeys();
  }

  public void openAccountPopup() {
    Integer index = Jsf.getParameterInt("rownumber");
    SalesReturnItem salesReturnItem = getSalesReturnItemList().get(index);
    if (salesReturnItem != null && salesReturnItem.getSalesReturnId() != null) {
      PrimeFaces.current().executeScript("PF('accountDlgVar').show();");
    }
  }

//  private void removeProductHashKeys() {
//    boolean keyExist;
//    StringBuilder keys = new StringBuilder("");
//    for (String key : getProductHashCode().keySet()) {
//      keyExist = false;
//      for (SalesReturnItem salesReturnItem : getSalesReturnItemList()) {
//        if (salesReturnItem.getProductSummary() != null && key.equals(salesReturnItem.getProductSummary().getProductHash())) {
//          keyExist = true;
//        }
//      }
//      if (!keyExist) {
//        if (keys.length() == 0) {
//          keys.append(key);
//        } else {
//          keys.append(",").append(key);
//        }
//      }
//    }
//
//    if (keys.length() > 0) {
//      for (String key : keys.toString().split(",")) {
//        getProductHashCode().remove(key);
//      }
//    }
//  }
  /**
   *
   * @param product
   * @return
   */
  public String productPackSize(Product product) {
    String pazkSize = "";
    if (product != null && product.getPackSize() != null && product.getProductUnitId() != null) {
      pazkSize = product.getPackSize() + " " + product.getProductUnitId().getSymbol();
    }
    return pazkSize;
  }

  /**
   *
   * @param salesReturnItem
   */
  public void tertiaryQtyBlurEvent(SalesReturnItem salesReturnItem) {
//    if (isDraft()) {
//      if (salesReturnItem != null) {
//        if (salesReturnItem.getPackTertiarySecondaryQty() != null && salesReturnItem.getProductTertairyQuantity() != null) {
//
//          Double sQty = salesReturnItem.getProductTertairyQuantity() * salesReturnItem.getPackTertiarySecondaryQty();
//          salesReturnItem.setProductSecondaryQuantity(sQty);
//
//          Double pQty = salesReturnItem.getProductSecondaryQuantity() * salesReturnItem.getPackSecondaryPrimaryQty();
//          salesReturnItem.setProductQuantity(pQty.intValue());
//        }
//      }
//    }
  }

  /**
   *
   * @param salesReturnItem
   */
  public void secondaryQtyBlurEvent(SalesReturnItem salesReturnItem) {
//    if (isDraft()) {
//      if (salesReturnItem != null) {
//        if (salesReturnItem.getPackSecondaryPrimaryQty() != null && salesReturnItem.getProductSecondaryQuantity() != null) {
//          Double pQty = salesReturnItem.getProductSecondaryQuantity() * salesReturnItem.getPackSecondaryPrimaryQty().doubleValue();
//          salesReturnItem.setProductQuantity(pQty.intValue());
//
//          if (salesReturnItem.getPackTertiaryId() != null && salesReturnItem.getPackTertiarySecondaryQty() != null) {
//            salesReturnItem.setProductTertairyQuantity(salesReturnItem.getProductSecondaryQuantity() / salesReturnItem.getPackTertiarySecondaryQty());
//          }
//        }
//      }
//    }
  }

  public void opensalesReturnProduct() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      // opens a new form if id is null else edit
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesReturnView().replaceFirst("/scm", ""), getSalesReturn().getId(), null);
    }
  }

  /**
   *
   * @param salesReturnItem
   */
  public void primaryQtyBlurEvent(SalesReturnItem salesReturnItem) {
    Double productQty;
    if (isDraft()) {
      if (salesReturnItem != null) {
        if (salesReturnItem.getProductQuantity() != null || salesReturnItem.getProductQuantityDamaged() != null) {

          productQty = salesReturnItem.getProductQuantity() != null ? salesReturnItem.getProductQuantity().doubleValue() : salesReturnItem.getProductQuantityDamaged();
//          if (isDamagedSalesReturn()) {
//            productQty = salesReturnItem.getProductQuantityDamaged();
//          }

//          if (salesReturnItem.getPackSecondaryId() != null && salesReturnItem.getPackSecondaryPrimaryQty() != null) {
//            double sQty = (productQty / salesReturnItem.getPackSecondaryPrimaryQty().doubleValue());
//            salesReturnItem.setProductSecondaryQuantity(sQty);
//          }
//          if (salesReturnItem.getPackTertiaryId() != null && salesReturnItem.getPackTertiarySecondaryQty() != null) {
//            Double tQty = salesReturnItem.getProductSecondaryQuantity().doubleValue() / salesReturnItem.getPackTertiarySecondaryQty().doubleValue();
//            salesReturnItem.setProductTertairyQuantity(tQty);
//          }
          if (salesReturnItem.getValueRate() != null) {
            salesReturnItem.setProductDiscountValue(null);
            salesReturnItem.setSchemeDiscountValue(null);
            salesReturnItem.setInvoiceDiscountValue(null);
            getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), true);
          }
        }
        salesReturnItem.setValueTaxableBeforeSettlement(null);
        if (salesReturnItem.getMaxReturnableQty() != null) {
          if (salesReturnItem.getProductQuantity() != null && salesReturnItem.getProductQuantity() > salesReturnItem.getMaxReturnableQty()) {
            salesReturnItem.setProductQuantity(salesReturnItem.getMaxReturnableQty().intValue());
          }
          if (salesReturnItem.getProductQuantityDamaged() != null && salesReturnItem.getProductQuantityDamaged() > salesReturnItem.getMaxReturnableQty()) {
            salesReturnItem.setProductQuantityDamaged(salesReturnItem.getMaxReturnableQty());
          }
        }
      }
    }
  }

  public void schemeDiscountValueChangeEvent(SalesReturnItem salesReturnItem) {
    if (salesReturnItem.getSalesInvoiceId() == null) {
      if (salesReturnItem.getSchemeDiscountValue() != null) {
        salesReturnItem.setIsSchemeDiscountInValue(SystemConstants.YES);
      } else {
        salesReturnItem.setIsSchemeDiscountInValue(null);
        salesReturnItem.setSchemeDiscountPercentage(null);
      }
    }
    getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
    salesReturnItem.setValueTaxableBeforeSettlement(null);
  }

  public void schemeDiscountPercentageChangeEvent(SalesReturnItem salesReturnItem) {
    if (salesReturnItem.getSalesInvoiceId() == null) {
      if (salesReturnItem.getSchemeDiscountPercentage() != null) {
        salesReturnItem.setIsSchemeDiscountInValue(SystemConstants.NO);
      } else {
        salesReturnItem.setIsSchemeDiscountInValue(null);
        salesReturnItem.setSchemeDiscountValue(null);
      }
    }
    getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
    salesReturnItem.setValueTaxableBeforeSettlement(null);
  }

  public void productDiscountValueChangeEvent(SalesReturnItem salesReturnItem) {
    if (salesReturnItem.getSalesInvoiceId() == null) {
      if (salesReturnItem.getProductDiscountValue() != null) {
        salesReturnItem.setIsProductDiscountInValue(SystemConstants.YES);
      } else {
        salesReturnItem.setIsProductDiscountInValue(null);
        salesReturnItem.setProductDiscountPercentage(null);
      }
    }
    getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
    salesReturnItem.setValueTaxableBeforeSettlement(null);
  }

  public void productDiscountPercentageChangeEvent(SalesReturnItem salesReturnItem) {
    if (salesReturnItem.getSalesInvoiceId() == null) {
      if (salesReturnItem.getProductDiscountPercentage() != null) {
        salesReturnItem.setIsProductDiscountInValue(SystemConstants.NO);
      } else {
        salesReturnItem.setIsProductDiscountInValue(null);
        salesReturnItem.setProductDiscountValue(null);
      }
    }
    getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
    salesReturnItem.setValueTaxableBeforeSettlement(null);
  }

  public void valueRateBlurEvent(SalesReturnItem salesReturnItem) {
    if (isDraft()) {
      if (salesReturnItem != null) {
        if (salesReturnItem.getValueRate() != null) {
//          if (salesReturnItem.getPackSecondaryId() != null && salesReturnItem.getPackSecondaryPrimaryQty() != null) {
//            salesReturnItem.setValueBoxRate(salesReturnItem.getValueRate() * salesReturnItem.getPackSecondaryPrimaryQty());
//          }
          getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
          updatePtsValueFromRate(salesReturnItem.getAccountId(), salesReturnItem, salesReturnItem.getProductPreset());
        }
      }
      salesReturnItem.setValueTaxableBeforeSettlement(null);
    }
  }
//
//  public void landingRateBlurEvent(SalesReturnItem salesReturnItem) {
//    if (isDraft()) {
//      if (salesReturnItem != null) {
//        if (salesReturnItem.getLandingRate() != null) {
//          updatePtsValueFromRate(salesReturnItem.getAccountId(), salesReturnItem, salesReturnItem.getProductPreset());
//        }
//      }
//    }
//  }

  public void purchaseRateBlurEvent(SalesReturnItem salesReturnItem) {
    if (isDraft()) {
      if (salesReturnItem != null) {
        if (salesReturnItem.getPurchaseRate() != null) {
          updateLandingRateFromPts(salesReturnItem, salesReturnItem.getProductPreset());
        }
      }
    }
  }

  private static Double updateLandingRateFromPts(SalesReturnItem salesReturnItem, ProductPreset productPreset) {
    Double actualPtsValue = ProductUtil.getActualPtsDerived(salesReturnItem);
    Double landingRate = null;
    if (actualPtsValue != null) {
      landingRate = ProductUtil.getExpectedLandingRate(productPreset.getPtsSsRateDerivationCriterion(), actualPtsValue, productPreset.getMarginPercentage(), salesReturnItem.getAccountId());
      salesReturnItem.setExpectedLandingRate(landingRate);
    }
    return landingRate;
  }

  private static Double updatePtsValueFromRate(Account account, SalesReturnItem salesReturnItem, ProductPreset produtPreset) {
    Double ptsValue = null;
    Double actualPtsValue = null;
    Double landRate = null;
    if (AccountService.DERIVE_PTS_FROM_LANDING_RATE.equals(account.getPtsDerivationCriteria()) && salesReturnItem.getExpectedLandingRate() != null) {
//      ptsValue = ProductUtil.getExpectedPts(produtPreset.getMarginPercentage(), salesReturnItem.getLandingRate(), produtPreset.getPtsSsRateDerivationCriterion());
      ptsValue = salesReturnItem.getValueRate();
      landRate = ProductUtil.getExpectedLandingRate(produtPreset.getPtsSsRateDerivationCriterion(), ptsValue, produtPreset.getMarginPercentage(), salesReturnItem.getAccountId());
      salesReturnItem.setExpectedLandingRate(landRate);
      salesReturnItem.setValuePts(MathUtil.roundOff(ptsValue, 2));
    } else if ((AccountService.DERIVE_PTS_FROM_RATE.equals(account.getPtsDerivationCriteria()) || AccountService.DERIVE_PTS_FROM_LANDING_RATE.equals(account.getPtsDerivationCriteria()))
            && salesReturnItem.getValueRate() != null) {
//      ptsValue = ProductUtil.getExpectedPts(produtPreset.getMarginPercentage(), salesReturnItem.getValueRate(), produtPreset.getPtsSsRateDerivationCriterion());
      ptsValue = salesReturnItem.getValueRate();
      actualPtsValue = ProductUtil.getActualPtsDerived(salesReturnItem);
      landRate = ProductUtil.getExpectedLandingRate(produtPreset.getPtsSsRateDerivationCriterion(), actualPtsValue, produtPreset.getMarginPercentage(), salesReturnItem.getAccountId());
      salesReturnItem.setExpectedLandingRate(landRate);
      salesReturnItem.setValuePts(MathUtil.roundOff(ptsValue, 2));
    }
    return ptsValue;
  }

  public boolean isTagged(SalesReturnItem salesReturnItem) {
    boolean tagged = false;
    if (salesReturnItem.getCreditSettlementAmount() != null && salesReturnItem.getCreditSettlementAmount() > 0) {
      tagged = true;
    }
    return tagged;
  }

  private void actionDeleteSalesReturnItem(MainView main, SalesReturnItem salesReturnItem) {

    if (salesReturnItem != null && salesReturnItem.getId() != null && salesReturnItem.getId() > 0) {

      SalesReturnItemService.deleteByPk(main, salesReturnItem);
      getSalesReturnItemList().remove(salesReturnItem);
      setValueMrpChanged(SalesReturnService.isProductValueMrpChanged(main, getSalesReturn()));
      //getProductHashCode().remove(salesReturnItem.getProductSummary().getProductHash());

    } else {
      //if (salesReturnItem.getProductSummary() != null && salesReturnItem.getProductSummary().getProductCode() != null) {
      //getProductHashCode().remove(salesReturnItem.getProductSummary().getProductHash());
      //}
      getSalesReturnItemList().remove(salesReturnItem);
    }
    getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
    if (getSalesReturnItemList().isEmpty()) {
      addNewSalesReturnItem();
    }
    hasLineItem();
  }

  private void addNewSalesReturnItem() {
    if (getSalesReturnItemList() == null) {
      salesReturnItemList = new ArrayList<>();
    }
    SalesReturnItem salesReturnItem = new SalesReturnItem();
    getSalesReturnItemList().add(0, salesReturnItem);
  }

  public String actionInsertOrUpdateSalesReturnItem(MainView main) {
    boolean isNew = false;
    try {
      if (isValueMrpChanged()) {
        main.error("fatal.error.mrp.changed");
        return null;
      }
      if (!isUsedByOtherUser(main)) {
        if (isDraft()) {
          Integer index = Jsf.getParameterInt("rownumber");
          if (index == null) {
            index = rowNumber;
          }
          SalesReturnItem salesReturnItem = getSalesReturnItemList().get(index);
          if (salesReturnItem.getSalesReturnCreditSettlement() == null) {
            salesReturnItem.setCreditSettlementAmount(null);
          }
          boolean isRateVerified;
          if (salesReturnItem != null && salesReturnItem.getProductBatchId() != null && salesReturnItem.getValueRate() != null && (salesReturnItem.getProductQuantity() != null || salesReturnItem.getProductQuantityDamaged() != null) && salesReturnItem.getValueRate() != null) {
            isRateVerified = salesReturnItem.getSalesInvoiceId() != null ? true : salesReturnItem.getPurchaseRate() != null;
            if (isRateVerified) {
              if (salesReturnItem.getId() == null) {
                isNew = true;
              }

              if (salesReturnItem.getValuePts() == null) {
                updatePtsValueFromRate(salesReturnItem.getAccountId(), salesReturnItem, salesReturnItem.getProductPreset());
              }

//            if (isDamagedSalesReturn()) {
//              salesReturnItem.setProductQuantityDamaged(salesReturnItem.getProductQuantity());
//            }
              getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);

              SalesReturnService.insertOrUpdate(main, getSalesReturn());
              salesReturnItem.setSalesReturnId(getSalesReturn());
              SalesReturnItemService.insertOrUpdate(main, salesReturnItem);

              if (isNew) {
                addNewSalesReturnItem();
              } else if (index == 0) {
                addNewSalesReturnItem();
              }
            }
          }

          for (SalesReturnItem ped : getSalesReturnItemList()) {
            if (ped.getProductBatchSummary() != null && ped.getProductBatchSummary().getProductId() != null) {
              getTaxCalculator().updateSalesReturnItem(main, ped);
            }
          }
          if (salesReturnItem.getSalesReturnCreditSettlement() != null) {
            SalesReturnCreditSettlement creditSettlement = salesReturnItem.getSalesReturnCreditSettlement();
            creditSettlement.setSalesReturnItemId(salesReturnItem);
            if (creditSettlement.getSalesReturnItemHash() == null) {
              creditSettlement.setSalesReturnItemHash(salesReturnItem.getId().toString());
            }
            SalesReturnItemService.insertOrUpdateCreditSettlement(main, creditSettlement);
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public String actionUpdateSalesReturnQuantity(MainView main) {
    try {
      if (getSalesReturnItemId() != null && getSalesReturnItemId().getId() != null) {
        SalesReturnItemService.updateProdutQuantity(main, getSalesReturnItemId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public void consignmentPopupReturned(MainView main) {
    Consignment cons = (Consignment) Jsf.popupParentValue(Consignment.class);
    try {
      setConsignmentRateList(null);
      if (cons != null && cons.getId() != null) {
        List<ConsignmentRate> list = lookUpConsignmentByCustomerAccountGroup(main);
        for (ConsignmentRate c : list) {
          if (c.getId().equals(cons.getId())) {
            getSalesReturn().setConsignmentId(cons);
            updateConsignmentDetails(main, cons);
            setConsignmentRecieptDetails(c);
            break;
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<ProductSummary> lookupProduct(String filter) {
    List<ProductSummary> list = null;
    MainView main = Jsf.getMain();
    try {
      list = SalesReturnService.lookupProduct(main, getSalesReturn(), filter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return list;
  }

  public void productPopupReturned(MainView main) {
    main.close();
//    try {
////      if (isDraft()) {
////        //SalesReturnItem salesReturnItem = (SalesReturnItem) Jsf.popupParentValue(SalesReturnItem.class);
////      }
//    } catch (Throwable t) {
//      t.printStackTrace();
//    } finally {
//      main.close();
//    }
  }

  /**
   *
   * @return
   */
  private List<TaxCode> lookupGstTaxCode() {
    if (taxCodeList == null) {
      taxCodeList = ScmLookupExtView.lookupGstTaxCode(getSalesReturn().getCompanyId());
    }
    return taxCodeList;
  }

  public void hasLineItem() {
    setLineItemExist(false);
    if (!StringUtil.isEmpty(getSalesReturnItemList())) {
      if (getSalesReturnItemList().size() == 1) {
        if (getSalesReturnItemList().get(0).getProduct() != null) {
          setLineItemExist(true);
        }
      } else {
        setLineItemExist(true);
      }
    }
  }

  //FIXME main not required
  public void printLandscape(MainView main, String key) {
    try {
      CompanySettings settings = getSalesReturn().getCompanyId().getCompanySettings();
      if (settings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
        getSalesReturn().setPrintType(SystemConstants.PRINT_SINGLE_LINE);
      } else {
        getSalesReturn().setPrintType(SystemConstants.PRINT_MULTIPLE_LINE);
      }
      if (SystemConstants.PDF_ITEXT.equals(key)) {
        Jsf.popupForm(getTaxCalculator().getSalesReturnPrintIText(), getSalesReturn());
      } else if (SystemConstants.PDF_KENDO.equals(key)) {
        Jsf.popupForm(getTaxCalculator().getSalesReturnPrintLandScape(), getSalesReturn());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void printPortrait(MainView main, String key) {
    try {
      getSalesReturn().setPrintType(SystemConstants.PRINT_PORTRAIT);
      if (SystemConstants.PDF_ITEXT.equals(key)) {
        Jsf.popupForm(getTaxCalculator().getSalesReturnPrintIText(), getSalesReturn());
      } else if (SystemConstants.PDF_KENDO.equals(key)) {
        Jsf.popupForm(getTaxCalculator().getSalesReturnPrintPortrait(), getSalesReturn());
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  /**
   * Sales Agent Auto
   *
   * @param filter
   * @return
   */
  public List<SalesAgent> salesAgentAuto(String filter) {
    List<SalesAgent> list = null;
    if (getSalesReturn().getCustomerId() != null) {
      list = SalesAgentService.selectSalesAgentbyCustomer(filter, getSalesReturn().getCompanyId(), getSalesReturn().getAccountGroupId(), getSalesReturn().getCustomerId());
    }
    return list;
  }

  public void openSalesInvoicePopUp(Integer id) {
    Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesInvoiceView().replaceFirst("/scm", ""), id, id);
  }

  public AccountingTransactionDetailItem getAccountingTransactionDetailItem(Integer id) {
    AccountingTransactionDetailItem acc = new AccountingTransactionDetailItem();
    if (acc.getAccountingTransactionDetailId() == null) {
      acc.setAccountingTransactionDetailId(new AccountingTransactionDetail());
    }
    if (acc.getAccountingTransactionDetailId().getAccountingTransactionId() == null) {
      acc.getAccountingTransactionDetailId().setAccountingTransactionId(new AccountingTransaction());
    }
    acc.getAccountingTransactionDetailId().getAccountingTransactionId().setEntityId(id);
    return acc;
  }

  public List<SalesReturnItem> getSelectedSalesReturnItems() {
    return selectedSalesReturnItems;
  }

  public void setSelectedSalesReturnItems(List<SalesReturnItem> selectedSalesReturnItems) {
    this.selectedSalesReturnItems = selectedSalesReturnItems;
  }

  public Long getRowKey() {
    return new Date().getTime();
  }

  public void deleteSalesReturnItems(MainView main) {
    try {
      if (!isUsedByOtherUser(main)) {
        if (selectedSalesReturnItems != null) {
          for (SalesReturnItem item : selectedSalesReturnItems) {
            actionDeleteSalesReturnItem(main, item);
          }
          main.commit("success.delete");
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  private void calculateLandingRate(SalesReturnItem salesReturnItem, ProductPreset productPreset) {
    Double ptsValue = salesReturnItem.getValuePts();
    Account account = salesReturnItem.getAccountId();
    int decimalPrecision = account.getSupplierDecimalPrecision() == null ? 2 : account.getSupplierDecimalPrecision();
    if (account.getPtsDerivationCriteria().equals(AccountService.DERIVE_PTS_FROM_MRP_PTR)) {
      salesReturnItem.setExpectedLandingRate(MathUtil.roundOff(ProductUtil.getExpectedLandingRate(productPreset.getPtsSsRateDerivationCriterion(), ptsValue, productPreset.getMarginPercentage(), salesReturnItem.getAccountId()), decimalPrecision));
    }
  }

  public String getNextInvoiceNumber() {
    return nextInvoiceNumber;
  }

  public void nextInvoiceNumber(MainView main) {
    try {
      if (getSalesReturn().getAccountGroupId() == null) {
        getSalesReturn().setAccountGroupId(getSalesReturn().getAccountGroupId());
      }
      nextInvoiceNumber = AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getSalesReturn().getAccountGroupId(), PrefixTypeService.SALES_RETURN_PREFIX_ID, false, getSalesReturn().getFinancialYearId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void nextOrPreviousInvoice(MainView main, Integer next) {
    try {
      if (tmpList != null) {
        Integer id = getInvoiceIdByIndex(main, tmpList.indexOf(getSalesReturn()), next);
        getSalesReturn().setId(id);
        salesReturn = SalesReturnService.selectByPk(main, getSalesReturn());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private Integer getInvoiceIdByIndex(MainView main, Integer index, Integer next) {
    int rowCount = salesReturnLazyModel.getRowCount();
    if ((index >= (rowSize - 1)) && start <= rowCount && next > 0) {
      start += rowSize;
    } else if (next < 0 && index == 0) {
      start -= rowSize;
      index = rowSize;
    }
    loadPreviousNextSalesReturnList(main);
    SalesReturn invoice = tmpList.get((index + next == rowSize) ? 0 : index + next);
    return invoice.getId();
  }

  public boolean isNextButton() {
    int rowCount = salesReturnLazyModel == null ? 0 : salesReturnLazyModel.getRowCount();
    if (tmpList != null && (tmpList.indexOf(getSalesReturn()) != (tmpList.size() - 1) || ((start + rowSize) <= rowCount))) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isPreviousButton() {
    if (tmpList == null) {
      return false;
    }
    if (tmpList.indexOf(getSalesReturn()) == 0 && start == 0) {
      return false;
    } else {
      return true;
    }
  }

  private void loadPreviousNextSalesReturnList(MainView main) {
    main.getPageData().setSearchKeyWord(null);
    AppPage.move(main.getPageData(), start, rowSize, sortColumn, sOrder.name());
    tmpList = SalesReturnService.listPagedByAccountGroup(main, getSalesReturn().getCompanyId().getId(), getSalesReturn().getAccountGroupId(), getSalesReturnTypeFilter(), getSalesReturn().getFinancialYearId());
  }

  private boolean isUsedByOtherUser(MainView main) {
    if (getSalesReturn().getId() == null || SalesReturnService.isSalesReturnEditable(main, getSalesReturn())) {
      return false;
    } else {
      main.error("error.past.record.edit");
      selectSalesReturnForEdit(main);
      return true;
    }
  }

  private void selectSalesReturnForEdit(MainView main) {
    setSalesReturn((SalesReturn) SalesReturnService.selectByPk(main, getSalesReturn()));
    setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesReturn().getTaxProcessorId().getProcessorClass()));
    setSalesReturnItemList(SalesReturnItemService.selectSalesReturnItemList(main, getSalesReturn()));
    getSalesReturn().setBusinessArea(SalesInvoiceUtil.getSalesMode(getSalesReturn().getCompanyId(), getSalesReturn().getCustomerId()));
    getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
    updateConsignmentDetails(main, getSalesReturn().getConsignmentId());
    setValueMrpChanged(SalesReturnService.isProductValueMrpChanged(main, getSalesReturn()));
    setSalesMode(getSalesReturn().getCompanyId(), getSalesReturn().getCustomerId());
    if (getSalesReturnItemList() != null && getSalesReturnItemList().isEmpty()) {
      addNewSalesReturnItem();
    }
    nextInvoiceNumber(main);
  }

  public List<String> getSalesInvoiceList(MainView main) {
    try {
      List<String> salesInvoiceList = SalesReturnService.getSalesInvoiceList(main, salesReturn);
      return salesInvoiceList;
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void export(MainView main) {
    try {
      String name = "Sales_Invoice_items";
      LineItemExport.exportSalesReturnItems(name, main, getSalesReturn(), getSalesReturnItemList());
    } catch (Throwable t) {
      main.rollback(t, "error.export");
    } finally {
      main.close();
    }
  }

  public void updateReferenceDateFromRefNo(MainView main) {
    try {
      getSalesReturn().setReferenceNo(getSalesReturn().getReferenceNo());
      String date = SalesReturnService.updateRefDateFromRefNo(main, getSalesReturn());
      if (date != null) {
        getSalesReturn().setReferenceInvoiceDate(SDF_YYYY_MM_DD.parse(date));
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  private boolean hasReferenceNo() {
    boolean reference = true;
    if (getSalesReturn().getReferenceNo() == null) {
      if (getSalesReturnItemList() != null) {
        for (SalesReturnItem list : getSalesReturnItemList()) {
          if (list.getRefInvoiceNo() == null) {
            reference = false;
          }
        }
      }
    } else if (getSalesReturn().getReferenceNo() != null) {
      reference = true;
    }
    return reference;
  }

  private boolean hasReferenceDate() {
    boolean reference = true;
    if (getSalesReturn().getReferenceInvoiceDate() == null) {
      if (getSalesReturnItemList() != null) {
        for (SalesReturnItem list : getSalesReturnItemList()) {
          if (list.getRefInvoiceDate() == null) {
            reference = false;
          }
        }
      }
    } else if (getSalesReturn().getReferenceNo() != null) {
      reference = true;
    }
    return reference;
  }

  public void calculateTotalDiscounts() {
    totalSchemeDiscount = 0.0;
    totalInvoiceDiscount = 0.0;
    totalProductDiscount = 0.0;
    totalQty = 0.0;
    totalSgst = 0.0;
    totalCgst = 0.0;
    totalIgst = 0.0;
    if (getSalesReturnItemList() != null) {
      for (SalesReturnItem list : getSalesReturnItemList()) {
        totalSchemeDiscount += list.getSchemeDiscountValue() != null ? list.getSchemeDiscountValue() : 0;
        totalProductDiscount += list.getProductDiscountValue() != null ? list.getProductDiscountValue() : 0;
        totalInvoiceDiscount += list.getInvoiceDiscountValue() != null ? list.getInvoiceDiscountValue() : 0;
        totalQty += (list.getProductQuantity() != null ? list.getProductQuantity() : (list.getProductQuantityDamaged() == null ? 0 : list.getProductQuantityDamaged()));
        totalSgst += list.getValueSgst() != null ? list.getValueSgst() : 0;
        totalCgst += list.getValueCgst() != null ? list.getValueCgst() : 0;
        totalIgst += list.getValueIgst() != null ? list.getValueIgst() : 0;
      }
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

  public Double getTotalCgst() {
    return totalCgst;
  }

  public Double getTotalSgst() {
    return totalSgst;
  }

  public Double getTotalIgst() {
    return totalIgst;
  }

  public void loadSalesReturnSplitList(MainView main) {
    salesReturnSplitList = new ArrayList<>();
    try {
      salesReturnSplitList = SalesReturnSplitService.salesReturnSplitList(main, salesReturn);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<SalesReturnSplit> getSalesReturnSplitList() {
    return salesReturnSplitList;
  }

  public void setSalesReturnSplitList(List<SalesReturnSplit> salesReturnSplitList) {
    this.salesReturnSplitList = salesReturnSplitList;
  }

  public boolean isDisplayReference() {
    CompanySettings settings = getSalesReturn().getCompanyId().getCompanySettings();
    if (getSalesReturn().getConfirmedAt() == null) {
      if (settings.getSplitSalesReturn() == null || (settings.getSplitSalesReturn() != null
              && settings.getSplitSalesReturn().intValue() == SystemConstants.NOT_SPLITTABLE_SALES_RETURN)) {
        displayReference = true;
      } else if (settings.getSplitSalesReturn() != null
              && settings.getSplitSalesReturn().intValue() == SystemConstants.SPLITTABLE_SALES_RETURN) {
        displayReference = false;
      }
    } else if (getSalesReturn().getConfirmedAt() != null) {
      if (getSalesReturn().getReturnSplit() == null || (getSalesReturn().getReturnSplit() != null && getSalesReturn().getReturnSplit().intValue() == SystemConstants.NOT_SPLITTABLE_SALES_RETURN)) {
        displayReference = true;
      } else {
        displayReference = false;
      }
    }
    return displayReference;
  }

  public boolean isDisplayReferenceInDialog() {
    CompanySettings settings = getSalesReturn().getCompanyId().getCompanySettings();
    displayReferenceInDialog = false;
    if (getSalesReturn().getConfirmedAt() == null && (settings.getSplitSalesReturn() == null
            || (settings.getSplitSalesReturn() != null && settings.getSplitSalesReturn().intValue() == SystemConstants.NOT_SPLITTABLE_SALES_RETURN))) {
      displayReferenceInDialog = true;
    } else if (getSalesReturn().getConfirmedAt() != null) {
      if (getSalesReturn().getReturnSplit() == null || (getSalesReturn().getReturnSplit() != null && getSalesReturn().getReturnSplit().intValue() == SystemConstants.NOT_SPLITTABLE_SALES_RETURN)) {
        displayReferenceInDialog = true;
      } else {
        displayReferenceInDialog = false;
      }
    }
    if (getSalesReturnItemList() != null) {
      for (SalesReturnItem list : getSalesReturnItemList()) {
        if (list.getSalesInvoiceId() == null && StringUtil.isEmpty(list.getRefInvoiceNo())) {
          displayReferenceInDialog = true;
        }
      }
    }
    return displayReferenceInDialog;
  }

  public void setDisplayReferenceInDialog(boolean displayReferenceInDialog) {
    this.displayReferenceInDialog = displayReferenceInDialog;
  }

  public boolean refInvoiceDateRequired(SalesReturnItem salesReturnItem) {
    boolean flag = false;
    if (!StringUtil.isEmpty(salesReturnItem.getRefInvoiceNo())) {
      flag = true;
    }
    return flag;
  }

  public void loadCreditSettlementList(MainView main, SalesReturnItem salesReturnItem) {
    try {
      List<ReferenceInvoice> list = null;
      if (salesReturnItem != null) {
        list = SalesReturnService.getCreditSettlementList(main, salesReturn, salesReturnItem);
      }
      setCreditSettlementList(list);
      if (salesReturnItem.getValueTaxableBeforeSettlement() == null) {
        salesReturnItem.setValueTaxableBeforeSettlement(salesReturnItem.getValueAssessable());
      }
      if (salesReturnItem.getProductQuantity() != null || salesReturnItem.getProductQuantityDamaged() != null) {
        qtyInSettlement = salesReturnItem.getProductQuantity() == null ? salesReturnItem.getProductQuantityDamaged() : salesReturnItem.getProductQuantity();
      }
      ratePerProd = qtyInSettlement != null ? ((salesReturnItem.getValueTaxableBeforeSettlement() != null ? salesReturnItem.getValueTaxableBeforeSettlement() : 0) / qtyInSettlement) : 0;
      setSelectedCreditSettlement(null);
      String returnId = null;
      if (salesReturnItem.getSalesReturnItemHash() != null) {
        returnId = salesReturnItem.getSalesReturnItemHash().split("#")[0];
      }
      SalesReturnCreditSettlement creditSettlement = null;
      if (returnId != null) {
        creditSettlement = SalesReturnItemService.findCreditSettlement(main, new SalesReturnItem(Integer.parseInt(returnId)));
      } else if (salesReturnItem.getId() != null) {
        creditSettlement = SalesReturnItemService.findCreditSettlement(main, salesReturnItem);
      }

      if (creditSettlement != null && list != null) {
        for (ReferenceInvoice referenceInvoice : list) {
          if (referenceInvoice.getDebitCreditNoteItemId() != null
                  && referenceInvoice.getDebitCreditNoteItemId().intValue() == creditSettlement.getDebitCreditNoteItemId().getId()) {
            setSelectedCreditSettlement(referenceInvoice);
            break;
          }
        }
      }

    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public Date getMinEntryDate() {
    if (getSalesReturn().getId() == null) {
      return SystemRuntimeConfig.getMinEntryDate(getSalesReturn().getCompanyId());
    }
    return getSalesReturn().getEntryDate();
  }

  public List<ReferenceInvoice> getCreditSettlementList() {
    return creditSettlementList;
  }

  public void setCreditSettlementList(List<ReferenceInvoice> creditSettlementList) {
    this.creditSettlementList = creditSettlementList;
  }

  public void creditSettlementChangeEvent(SalesReturnItem salesReturnItem) {
    Double amount = salesReturnItem.getCreditSettlementAmount();
    salesReturnItem.setCreditSettlementAmount(null);
    getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), true);
    Double taxableValue = salesReturnItem.getValueAssessable() != null ? salesReturnItem.getValueAssessable() : 0.00;
    salesReturnItem.setCreditSettlementAmount(amount);
    taxableValue -= salesReturnItem.getCreditSettlementAmount() != null ? salesReturnItem.getCreditSettlementAmount() : 0;
    if (qtyInSettlement != null && qtyInSettlement > 0) {
      ratePerProd = (taxableValue != null ? taxableValue : 0) / qtyInSettlement;
    }
  }

  public void saveCreditSettlement(MainView main, SalesReturnItem salesReturnItem) {
    SalesReturnCreditSettlement creditSettlement = null;
    try {
      if (getSelectedCreditSettlement() == null) {
        main.error("select.an.invoice");
        return;
      }
      if (salesReturnItem.getCreditSettlementAmount() == null) {
        main.error("credit.settlement.required");
        return;
      }
      if (StringUtil.isEmpty(salesReturnItem.getRemarks())) {
        main.error("remarks.required");
        return;
      }

      if (getSelectedCreditSettlement() != null && salesReturnItem.getCreditSettlementAmount() != null && salesReturnItem.getRemarks() != null) {
        if (salesReturnItem.getId() != null) {
          creditSettlement = SalesReturnItemService.findCreditSettlement(main, salesReturnItem);
        }
        if (creditSettlement == null) {
          creditSettlement = new SalesReturnCreditSettlement();
        }
        creditSettlement.setSalesInvoiceId(salesReturnItem.getSalesInvoiceId());
        creditSettlement.setSalesReturnId(salesReturn);
        if (salesReturnItem.getId() != null) {
          creditSettlement.setSalesReturnItemId(salesReturnItem);
        }
        creditSettlement.setDebitCreditNoteItemId(new DebitCreditNoteItem(getSelectedCreditSettlement().getDebitCreditNoteItemId()));
        creditSettlement.setNote(salesReturnItem.getRemarks());
      }
      salesReturnItem.setSalesReturnCreditSettlement(creditSettlement);
      getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), true);
      Jsf.execute("PF('tagsDlg').hide();");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void clearCreditSettlement(MainView main, SalesReturnItem salesReturnItem) {
    salesReturnItem.setSalesReturnCreditSettlement(null);
    salesReturnItem.setCreditSettlementAmount(null);
    setSelectedCreditSettlement(null);
    salesReturnItem.setRemarks(null);
    setSelectedCreditSettlement(null);
    try {
      SalesReturnItemService.deleteCreditSettlementBySalesReturnItem(main, salesReturnItem);
      getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), true);
      if (salesReturnItem.getId() != null) {
        SalesReturnItemService.updateByPk(main, salesReturnItem);
      }
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public Double getQtyInSettlement() {
    return qtyInSettlement;
  }

  public Double getRatePerProd() {
    return ratePerProd;
  }

  public Integer getRowNumber() {
    return rowNumber;
  }

  public void setRowNumber(Integer rowNumber) {
    this.rowNumber = rowNumber;
  }

  public ReferenceInvoice getSelectedCreditSettlement() {
    return selectedCreditSettlement;
  }

  public void setSelectedCreditSettlement(ReferenceInvoice selectedCreditSettlement) {
    this.selectedCreditSettlement = selectedCreditSettlement;
  }

  public boolean isLimitReturnQty() {
    return limitReturnQty;
  }

  public void setLimitReturnQty(boolean limitReturnQty) {
    this.limitReturnQty = limitReturnQty;
  }

  public void salesReturnFromPurRetPopupReturned(MainView main) {
    setSalesReturnItemList(getSalesReturn().getSalesReturnItemList());
    getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
    if (!StringUtil.isEmpty(getSalesReturn().getProductList())) {
      for (Product p : getSalesReturn().getProductList()) {
        SalesReturnItem returnItem = new SalesReturnItem();
        ProductSummary productSummary = new ProductSummary();
        productSummary.setProductName(p.getProductName());
        productSummary.setHsnCode(p.getHsnCode());
        returnItem.setProduct(p);
        returnItem.setHsnCode(p.getHsnCode());
        returnItem.setProductSummary(productSummary);
        getSalesReturnItemList().add(returnItem);
      }
      main.error("error.products.info.required");
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
    CompanySettings companySettings = getSalesReturn().getCompanyId().getCompanySettings();
    for (CompanyFinancialYear fy : getSalesReturn().getCompanyId().getCompanyFinancialYearList()) {
      if (getSalesReturn().getFinancialYearId().equals(fy.getFinancialYearId())) {
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
        if (salesReturn != null && getSalesReturn().getSalesReturnStatusId() != null && getSalesReturn().getSalesReturnStatusId().getId().intValue() > SystemConstants.DRAFT) {
          Date entryDate = getSalesReturn().getEntryDate();
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
      CompanySettings companySettings = getSalesReturn().getCompanyId().getCompanySettings();
      if (companySettings == null) {
        companySettings = CompanySettingsService.selectByCompany(main.em(), getSalesReturn().getCompanyId());
        getSalesReturn().getCompanyId().setCompanySettings(companySettings);
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
}
