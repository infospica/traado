
/*
 * @(#)ProductEntryView.java	1.0 Thu Sep 08 18:33:15 IST 2016
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.ConsignmentRate;
import spica.scm.common.ProductSummary;
import spica.scm.common.SelectItem;
import spica.scm.common.SelectItemInteger;

import spica.scm.domain.ProductEntry;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.domain.TaxCode;
import spica.scm.common.ProductInvoiceDetail;
import spica.scm.common.RelatedItems;
import spica.scm.domain.CompanyFinancialYear;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.Consignment;
import spica.scm.domain.Product;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductEntryStatus;
import spica.scm.domain.ProductPacking;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.AccountGroupService;
import spica.scm.service.AccountService;
import spica.scm.service.CommodityService;
import spica.scm.service.CompanySettingsService;
import spica.scm.service.ConsignmentCommodityService;
import spica.scm.service.ConsignmentService;
import spica.scm.service.ProductDetailService;
import spica.scm.service.ProductEntryDetailService;
import spica.scm.service.ProductEntryService;
import spica.scm.service.ProductService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.AppUtil;
import spica.scm.util.ProductUtil;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * ProductEntryView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Sep 08 18:33:15 IST 2016
 */
@Named(value = "productEntryView")
@ViewScoped
public class ProductEntryView implements Serializable {

  private transient ProductEntry productEntry;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductEntry> productEntryLazyModel; 	//For lazy loading datatable.
  private transient ProductEntry[] productEntrySelected;	 //Selected Domain Array
  //private transient Product product;
  private transient TaxCalculator taxCalculator;
  private transient ProductPacking productDiscountPackUnit;
  private transient ProductPacking schemeDiscountPackUnit;
  private transient ProductEntryDetail productEntryDetailId;	//Domain object/selected Domain.
  private transient List<ProductEntryDetail> productEntryDetailList;
  private transient List<ProductEntryDetail> serviceEntryDetailList;
  private transient List<RelatedItems> relatedItemsList;
  private transient List<ConsignmentRate> consignmentRateList;
  private transient List<ProductInvoiceDetail> productInvoiceDetailList;
  private transient String productName;

  private transient ConsignmentRate consignmentRate;
  private transient String lrAmount;
  private transient Double oldLrAmount;

  private transient boolean lineDiscountApplicable;
  private transient boolean specialDiscountApplicable;
  private transient boolean cashDiscountApplicable;
  private transient boolean taxableCashDiscount;
  private transient boolean productPresetModified;
  private transient Integer lineDiscountBeneficiary;
  private transient Integer rowIndex;
  private transient Integer serviceRowIndex;
  private transient Double lrAmountMax;

  private transient Integer schemeDiscountToCustomer;
  private transient Integer schemeDiscountReplacement;
  private transient Integer productDiscountReplacement;
  private transient boolean openingStockEntry;
  private transient boolean renderPtr;
  private transient boolean renderPts;
  private transient boolean ptsDerivedFromMrpPtr;
  private transient boolean valueMrpChanged;
  private transient boolean contractCreate;

  private transient HashMap<String, Integer> productHashCode;
  private AccountingTransactionDetailItem accountingTransactionDetailItem;
  private boolean taxEditable;
  private List<TaxCode> taxCodeList;
  private List<ProductEntryDetail> selectedProductEntryDetails;
  private transient Double outstandingAmount;
  private String upcomingInvoiceNumber;
  private transient List<ProductEntry> tmpList;
  private transient int rowSize;
  private transient int start;
  private transient String sortColumn;
  private transient SortOrder sOrder;
  private Double totalSchemeDiscount;
  private Double totalInvoiceDiscount;
  private Double totalProductDiscount;
  private Double totalQty;
  private Double totalCgst;
  private Double totalSgst;
  private Double totalIgst;
  private boolean igstTransaction;
  private boolean tradeClosed;
  private boolean renderReplacement;
  private int count = 1;
  private transient boolean discountsApplicableForServices;
  private boolean disableServiceDiscount;

  @PostConstruct
  public void init() {

    Integer invoiceId = (Integer) Jsf.popupParentValue(Integer.class);
    if (invoiceId != null) {
      getProductEntry().setId(invoiceId);
    }

    accountingTransactionDetailItem = (AccountingTransactionDetailItem) Jsf.popupParentValue(AccountingTransactionDetailItem.class);
    if (accountingTransactionDetailItem != null) {
      getProductEntry().setId(accountingTransactionDetailItem.getAccountingTransactionDetailId().getAccountingTransactionId().getEntityId());
    }
  }

  /**
   * Default Constructor.
   */
  public ProductEntryView() {
    super();
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

  /**
   * Return ProductEntry.
   *
   * @return ProductEntry.
   */
  public ProductEntry getProductEntry() {
    if (productEntry == null) {
      productEntry = new ProductEntry();
    }
    if (productEntry.getCompanyId() == null) {
      productEntry.setCompanyId(UserRuntimeView.instance().getCompany());
      productEntry.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
      productEntry.setAccountId(UserRuntimeView.instance().getAccount());

    }
    return productEntry;
  }

  public boolean isOpeningStockEntry() {
    return openingStockEntry;
  }

  public void setOpeningStockEntry(boolean openingStockEntry) {
    this.openingStockEntry = openingStockEntry;
  }

  public List<RelatedItems> getRelatedItemsList() {
    return relatedItemsList;
  }

  public void setRelatedItemsList(List<RelatedItems> relatedItemsList) {
    this.relatedItemsList = relatedItemsList;
  }

  public List<ConsignmentRate> getConsignmentRateList() {
    return consignmentRateList;
  }

  public void setConsignmentRateList(List<ConsignmentRate> consignmentRateList) {
    this.consignmentRateList = consignmentRateList;
  }

  public List<ProductInvoiceDetail> getProductInvoiceDetailList() {
    return productInvoiceDetailList;
  }

  public void setProductInvoiceDetailList(List<ProductInvoiceDetail> productInvoiceDetailList) {
    this.productInvoiceDetailList = productInvoiceDetailList;
  }

  public boolean isValueMrpChanged() {
    return valueMrpChanged;
  }

  public void setValueMrpChanged(boolean valueMrpChanged) {
    this.valueMrpChanged = valueMrpChanged;
  }

  public Integer getSchemeDiscountReplacement() {
    if (schemeDiscountReplacement == null) {
      schemeDiscountReplacement = 0;
    }
    return schemeDiscountReplacement;
  }

  public void setSchemeDiscountReplacement(Integer schemeDiscountReplacement) {
    this.schemeDiscountReplacement = schemeDiscountReplacement;
  }

  public Integer getProductDiscountReplacement() {
    if (productDiscountReplacement == null) {
      productDiscountReplacement = 0;
    }
    return productDiscountReplacement;
  }

  public void setProductDiscountReplacement(Integer productDiscountReplacement) {
    this.productDiscountReplacement = productDiscountReplacement;
  }

  /**
   * Set ProductEntry.
   *
   * @param productEntry.
   */
  public void setProductEntry(ProductEntry productEntry) {
    this.productEntry = productEntry;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

//  public Product getProduct() {
//    return product;
//  }
//
//  public void setProduct(Product product) {
//    this.product = product;
//  }
  public HashMap<String, Integer> getProductHashCode() {
    if (productHashCode == null) {
      productHashCode = new HashMap<>();
    }
    return productHashCode;
  }

  public void setProductHashCode(HashMap<String, Integer> productHashCode) {
    this.productHashCode = productHashCode;
  }

  public Double getLrAmountMax() {
    return lrAmountMax;
  }

  public void setLrAmountMax(Double lrAmountMax) {
    this.lrAmountMax = lrAmountMax;
  }

  /**
   *
   * @return
   */
  public ProductEntry[] getProductEntrySelected() {
    return productEntrySelected;
  }

  /**
   *
   * @param productEntrySelected
   */
  public void setProductEntrySelected(ProductEntry[] productEntrySelected) {
    this.productEntrySelected = productEntrySelected;
  }

  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public ProductEntryDetail getProductEntryDetailId() {
    if (productEntryDetailId == null) {
      productEntryDetailId = new ProductEntryDetail();
    }
    return productEntryDetailId;
  }

  public void setProductEntryDetailId(ProductEntryDetail productEntryDetailId) {
    this.productEntryDetailId = productEntryDetailId;
  }

  /**
   * Return LazyDataModel of ProductEntry.
   *
   * @return
   */
  public LazyDataModel<ProductEntry> getProductEntryLazyModel() {
    return productEntryLazyModel;
  }

  public List<ProductEntryDetail> getProductEntryDetailList() {
    if (productEntryDetailList == null) {
      productEntryDetailList = new ArrayList<>();
    }
    return productEntryDetailList;
  }

  public void setProductEntryDetailList(List<ProductEntryDetail> productEntryDetailList) {
    this.productEntryDetailList = productEntryDetailList;
  }

  public List<ProductEntryDetail> getServiceEntryDetailList() {
    if (serviceEntryDetailList == null) {
      serviceEntryDetailList = new ArrayList<>();
    }
    return serviceEntryDetailList;
  }

  public void setServiceEntryDetailList(List<ProductEntryDetail> serviceEntryDetailList) {
    this.serviceEntryDetailList = serviceEntryDetailList;
  }

  public ConsignmentRate getConsignmentRate() {
    return consignmentRate;
  }

  public void setConsignmentRate(ConsignmentRate consignmentRate) {
    this.consignmentRate = consignmentRate;
  }

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
    return taxableCashDiscount;
  }

  public void setTaxableCashDiscount(boolean taxableCashDiscount) {
    this.taxableCashDiscount = taxableCashDiscount;
  }

  public ProductPacking getProductDiscountPackUnit() {
    return productDiscountPackUnit;
  }

  public void setProductDiscountPackUnit(ProductPacking productDiscountPackUnit) {
    this.productDiscountPackUnit = productDiscountPackUnit;
  }

  public ProductPacking getSchemeDiscountPackUnit() {
    return schemeDiscountPackUnit;
  }

  public void setSchemeDiscountPackUnit(ProductPacking schemeDiscountPackUnit) {
    this.schemeDiscountPackUnit = schemeDiscountPackUnit;
  }

  /**
   *
   * @return
   */
  public Integer getLineDiscountBeneficiary() {
    return lineDiscountBeneficiary;
  }

  /**
   *
   * @param lineDiscountBeneficiary
   */
  public void setLineDiscountBeneficiary(Integer lineDiscountBeneficiary) {
    this.lineDiscountBeneficiary = lineDiscountBeneficiary;
  }

  public Integer getSchemeDiscountToCustomer() {
    return schemeDiscountToCustomer;
  }

  public void setSchemeDiscountToCustomer(Integer schemeDiscountToCustomer) {
    this.schemeDiscountToCustomer = schemeDiscountToCustomer;
  }

  public Integer getRowIndex() {
    return rowIndex;
  }

  public void setRowIndex(Integer rowIndex) {
    this.rowIndex = rowIndex;
  }

  public Integer getServiceRowIndex() {
    return serviceRowIndex;
  }

  public void setServiceRowIndex(Integer serviceRowIndex) {
    this.serviceRowIndex = serviceRowIndex;
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

  public boolean isPtsDerivedFromMrpPtr() {
    return ptsDerivedFromMrpPtr;
  }

  public void setPtsDerivedFromMrpPtr(boolean ptsDerivedFromMrpPtr) {
    this.ptsDerivedFromMrpPtr = ptsDerivedFromMrpPtr;
  }

  public boolean isProductPresetModified() {
    return productPresetModified;
  }

  public void setProductPresetModified(boolean productPresetModified) {
    this.productPresetModified = productPresetModified;
  }

  private void resetProductEntryView() {
    setTaxCalculator(null);
    setProductEntryDetailList(null);
    setServiceEntryDetailList(null);
    setConsignmentRate(null);
    setLrAmount(null);
    setOldLrAmount(null);
    setLineDiscountApplicable(false);
    setTaxableCashDiscount(false);
    setCashDiscountApplicable(false);
    setRenderPts(false);
    setRenderPtr(false);
    setConsignmentRateList(null);
    setValueMrpChanged(false);
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @param openingStock
   * @return
   */
  public String switchProductEntry(MainView main, String viewType, int openingStock) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        contractCreate = false;
        setOutstandingAmount(ProductEntryService.getOutstandingAmount(main, getProductEntry().getAccountId()));
        if (!AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
          setRenderReplacement(false);
        } else {
          setRenderReplacement(true);
        }
        if (ViewType.newform.toString().equals(viewType)) {
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else if (UserRuntimeView.instance().getAccount() == null) {
            main.error("account.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else if (UserRuntimeView.instance().getContract() == null) {
            main.error("error.product.entry.contract", new String[]{getProductEntry().getAccountId().getAccountTitle()});
            main.setViewType(ViewTypes.list);
            contractCreate = true;
            return null;
          } else {
            getProductEntry().reset();
            resetProductEntryView();
            getProductHashCode().clear();
            setOpeningStockEntry(SystemConstants.OPENING_STOCK_ENTRY == openingStock);
            CompanySettingsService.selectIfNull(main, getProductEntry().getCompanyId());
            getProductEntry().setOpeningStockEntry(openingStock);
            getProductEntry().setProductEntryDate(new Date());
            getProductEntry().setContractId(UserRuntimeView.instance().getContract());
            getProductEntry().setProductEntryStatusId(new ProductEntryStatus(SystemConstants.DRAFT));
            getProductEntry().setTaxProcessorId(getProductEntry().getCompanyId().getCountryTaxRegimeId().getTaxProcessorId());
            setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getProductEntry().getTaxProcessorId().getProcessorClass()));
            getProductEntry().setDecimalPrecision(getProductEntry().getAccountId().getSupplierDecimalPrecision() == null ? 2 : getProductEntry().getAccountId().getSupplierDecimalPrecision());
            getProductEntry().setSezZone((getProductEntry().getAccountId().getVendorId().getTaxable() != null && getProductEntry().getAccountId().getVendorId().getTaxable().intValue() == 2) ? 1 : 0
            );
            getProductEntry().setIsPurchaseInterstate(AccountUtil.getBusinessArea(getProductEntry().getAccountId()).intValue() == 1 ? 1 : 0);
            setIgstTransaction((getProductEntry().getIsPurchaseInterstate().intValue() == 1 || getProductEntry().getSezZone().intValue() == 1) ? true : false);
            upcomingInvoiceNumber(main);
          }
        } else if (ViewType.editform.toString().equals(viewType)) {

          setProductEntry(ProductEntryService.selectByPk(main, getProductEntry()));
          CompanySettingsService.selectIfNull(main, getProductEntry().getCompanyId());
          setValueMrpChanged(ProductEntryService.isProductValueMrpChanged(main, getProductEntry()));
          setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getProductEntry().getTaxProcessorId().getProcessorClass()));
          updateConsignmentDetails(main, getProductEntry().getConsignmentId());
          setProductEntryDetailList(ProductEntryDetailService.selectProductEntryDetailByProductEntryId(main, getProductEntry(), getTaxCalculator()));
          setServiceEntryDetailList(ProductEntryDetailService.selectServiceEntryDetails(main, getProductEntry()));
          setOpeningStockEntry(SystemConstants.OPENING_STOCK_ENTRY == openingStock);

          for (ProductEntryDetail ped : getProductEntryDetailList()) {
            ped.setProductSummary(new ProductSummary(ped));
            ped.getProductSummary().after();
            getProductHashCode().put(ped.getProductSummary().getProductHash(), ped.getProductSummary().getProductId());
            ped.setProductBatchId(ped.getProductDetailId().getProductBatchId());
          }

          setProductEntryApplicableDiscounts(productEntry);
          if (StringUtil.isEmpty(getServiceEntryDetailList())) {
            addNewServiceEntryDetail();
          }
          setIgstTransaction((getProductEntry().getIsPurchaseInterstate().intValue() == 1 || getProductEntry().getSezZone().intValue() == 1) ? true : false);
          getProductEntry().setBusinessArea(igstTransaction ? 1 : 2);
          getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
          if (StringUtil.isEmpty(getProductEntryDetailList())) {
            addNewProductEntryDetail();
          } else if (isDraft() && !StringUtil.isEmpty(getProductEntryDetailList())) {
            setProductPresetModified(ProductEntryService.isProductPresetModified(main, getProductEntry()));
          }

          setRenderPtr(AccountUtil.isRenderPtr(getProductEntry().getAccountId()));
          setRenderPts(AccountUtil.isRenderPts(getProductEntry().getAccountId()));
          setPtsDerivedFromMrpPtr(getProductEntry().getAccountId().getPtsDerivationCriteria().equals(AccountService.DERIVE_PTS_FROM_MRP_PTR));

          if (!isDraft()) {
            setRelatedItemsList(ProductEntryService.selectRelatedItemsOfProductEntry(main, getProductEntry()));
          }
          lookupGstTaxCode();

          getProductEntry().setDecimalPrecision(getProductEntry().getAccountId().getSupplierDecimalPrecision() == null ? 2 : getProductEntry().getAccountId().getSupplierDecimalPrecision());
          setSelectedProductEntryDetails(null);
          upcomingInvoiceNumber(main);
        } else if (ViewType.list.toString().equals(viewType)) {
          resetProductEntryView();
          getProductHashCode().clear();
          getProductEntry().setCompanyId(null);
          setSpecialDiscountApplicable(false);
          setLrAmountMax(null);
          setProductPresetModified(false);
          setRelatedItemsList(null);
          setConsignmentRate(null);
          setProductInvoiceDetailList(null);
          setProductName(null);
          setTaxEditable(false);
          setTaxCodeList(null);
          setOpeningStockEntry(SystemConstants.OPENING_STOCK_ENTRY == openingStock);
          loadProductEntryList(main);
          main.getPageData().setSearchKeyWord(null);
        }
        CompanySettingsService.selectIfNull(main, getProductEntry().getCompanyId());
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
   * Create productEntryLazyModel.
   *
   * @param main
   */
  private void loadProductEntryList(final MainView main) {
    if (productEntryLazyModel == null) {
      productEntryLazyModel = new LazyDataModel<ProductEntry>() {
        private List<ProductEntry> list;

        @Override
        public List<ProductEntry> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getProductEntry().getAccountId() != null && getProductEntry().getAccountId().getId() != null) {
              start = first;
              rowSize = pageSize;
              sortColumn = sortField;
              sOrder = sortOrder;
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = ProductEntryService.listPagedByAccount(main, getProductEntry().getAccountId().getId(), isOpeningStockEntry(), getProductEntry().getFinancialYearId());
              tmpList = list;
              main.commit(productEntryLazyModel, first, pageSize);
            } else {
              list = null;
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
        public Object getRowKey(ProductEntry productEntry) {
          return getProductEntry().getId();
        }

        @Override
        public ProductEntry getRowData(String rowKey) {
          if (list != null) {
            for (ProductEntry obj : list) {
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
   * @return
   */
  private boolean isProductEntryConfirmable() {
    boolean rvalue = false;
    if (getProductEntryDetailList() != null) {
      for (ProductEntryDetail productEntryDetail : getProductEntryDetailList()) {
        if (productEntryDetail.getProductBatchId() != null && (productEntryDetail.getProductQuantity() != null || productEntryDetail.getProductQuantityFree() != null) && productEntryDetail.getValueRate() != null && productEntryDetail.getValuePts() != null) {
          rvalue = true;
          break;
        }
      }
    }
    return rvalue;
  }

  /**
   * Insert or update.
   *
   * @param main
   * @param status
   * @return the page to display.
   */
  public String saveProductEntry(MainView main, Integer status) {
    if ((status.equals(SystemConstants.CONFIRMED)) && !isProductEntryConfirmable()) {
      main.error("error.product.missing");
      return null;
    }
    if (isValueMrpChanged()) {
      main.error("error.mrp.changed");
      return null;
    }

    getProductEntry().setProductEntryStatusId(new ProductEntryStatus(status));
    getProductEntry().setQuantityOrFree(getProductEntry().getAccountId().getIsSchemeApplicable());

    if ((getProductEntry().getInvoiceDiscountValue() != null || getProductEntry().getInvoiceAmountDiscountPerc() != null) && getProductEntry().getIsInvoiceDiscountToCustomer() == null) {
      if (AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
        getProductEntry().setIsInvoiceDiscountToCustomer(SystemConstants.DISCOUNT_FOR_COMPANY);
      } else {
        getProductEntry().setIsInvoiceDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
      }
    }
    return saveOrCloneProductEntry(main, "save");
  }

  /**
   *
   * @param main
   * @param status
   * @return
   */
  public String actionUpdateTaxVariation(MainView main, Integer status) {
    try {
      for (ProductEntryDetail ped : getProductEntryDetailList()) {
        ped.setIsTaxCodeModified(SystemConstants.TAX_CODE_MODIFIED - SystemConstants.TAX_CODE_MODIFIED);
      }
      getProductEntry().setIsTaxCodeModified(SystemConstants.TAX_CODE_MODIFIED - SystemConstants.TAX_CODE_MODIFIED);
      return saveProductEntry(main, status);
    } catch (Throwable t) {
      getProductEntry().setIsTaxCodeModified(SystemConstants.TAX_CODE_MODIFIED);
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductEntry(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneProductEntry(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductEntry(MainView main, String key) {
    try {
      if (!isUsedByOtherUser(main)) {
        boolean nextPrv = false;
        if (null != key) {
          switch (key) {
            case "save":
              if (getProductEntry().getId() == null) {
                getProductEntry().setAccountGroupId(AccountGroupService.selectDefaultAccountGroupByAccount(main, getProductEntry().getAccountId()));
                nextPrv = true;
              }

              setProductEntryApplicableDiscounts();
              //error check
              if (!StringUtil.isEmpty(getProductEntryDetailList())) {
                for (ProductEntryDetail productEntryDetail : getProductEntryDetailList()) {
                  if (productEntryDetail.getProductQuantity() != null) {
                    if (productEntryDetail.getProductQuantity() % 1 > 0 && productEntryDetail.getProductQuantityDamaged() == null) {
                      throw new UserMessageException("error.missing.quantityDamaged",
                              new String[]{productEntryDetail.getProduct() != null ? productEntryDetail.getProduct().getProductName() : "",
                                productEntryDetail.getBatchNo() != null ? productEntryDetail.getBatchNo() : ""});
                    } else if (productEntryDetail.getProductQuantityDamaged() != null) {
                      Double qty = productEntryDetail.getProductQuantity() + (productEntryDetail.getProductQuantityFree() != null ? productEntryDetail.getProductQuantityFree().doubleValue() : 0);
                      if (productEntryDetail.getProductQuantityDamaged() > qty) {
                        throw new UserMessageException("error.value.damagedQuantity",
                                new String[]{productEntryDetail.getProduct() != null ? productEntryDetail.getProduct().getProductName() : "",
                                  productEntryDetail.getBatchNo() != null ? productEntryDetail.getBatchNo() : ""});
                      }
                      if (productEntryDetail.getProductQuantityDamaged() % 1 > 0 || productEntryDetail.getProductQuantity() % 1 > 0) {
                        if (productEntryDetail.getProductQuantityDamaged().doubleValue() != productEntryDetail.getProductQuantity().doubleValue()) {
                          throw new UserMessageException("error.value.quantity",
                                  new String[]{productEntryDetail.getProduct() != null ? productEntryDetail.getProduct().getProductName() : "",
                                    productEntryDetail.getBatchNo() != null ? productEntryDetail.getBatchNo() : ""});
                        }
                        if (productEntryDetail.getProductQuantityFree() != null) {
                          throw new UserMessageException("error.value.quantityFree",
                                  new String[]{productEntryDetail.getProduct() != null ? productEntryDetail.getProduct().getProductName() : "",
                                    productEntryDetail.getBatchNo() != null ? productEntryDetail.getBatchNo() : ""});
                        }
                      }
                    }
                  }
                }
              }
              ProductEntryService.insertOrUpdate(main, getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList(), getTaxCalculator());

              if (getProductEntry().getOpeningStockEntry() == 0) {
                updateConsignmentDetails(main, getProductEntry().getConsignmentId());
                ConsignmentCommodityService.insertOrUpdateInvoiceEntryDetails(main, getProductEntry());
              }
              break;

            case "clone":
              ProductEntryService.clone(main, getProductEntry());
              break;
          }
          main.commit("success." + key);
          main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
          if (nextPrv) {
            loadPreviousNextProductEntry(main);
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
   *
   * @param main
   * @return
   */
  public String actionResetInvoiceEntry(MainView main) {
    try {
      ProductEntryService.actionResetProductEntryToDraft(main, getProductEntry());
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
   * @return
   */
  public String actionResetAllInvoiceEntry(MainView main) {
    try {
      ProductEntryService.resetAllInvoiceEntry(main, getProductEntry().getAccountId());
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  public String actionCorrectPtrAndPtsValues(MainView main) {
    try {
      ProductEntryService.updatePtrAndPtsValues(main, getProductEntry());
      getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param consignment
   */
  private void updateConsignmentDetails(MainView main, Consignment consignment) {
    if (consignment != null) {
      setOldLrAmount(getProductEntry().getTotalExpenseAmount());
      setConsignmentRate(ConsignmentService.selectConsignmentRateByConsignment(main, getProductEntry().getAccountId(), getProductEntry().getConsignmentId().getId()));
      setConsignmentRecieptDetails(getConsignmentRate());
    }
  }

  public void actionInsertOrUpdatePurchaseEntryDetail(MainView main) {
    boolean isNew = false;
    try {
      if (isValueMrpChanged()) {
        main.error("error.mrp.changed");
        return;
      }
      if (!isUsedByOtherUser(main)) {
        if (isDraft()) {
          Integer index = Jsf.getParameterInt("rownumber");
          ProductEntryDetail productEntryDetail = getProductEntryDetailList().get(index);
          if (productEntryDetail.getProductQuantity() == null) {
            productEntryDetail.setProductQuantity(0.0);
          }
          if (productEntryDetail.getProductQuantity() != null) {
            if (productEntryDetail.getProductQuantity() % 1 > 0 && productEntryDetail.getProductQuantityDamaged() == null) {
              throw new UserMessageException("error.missing.quantityDamaged",
                      new String[]{productEntryDetail.getProduct() != null ? productEntryDetail.getProduct().getProductName() : "",
                        productEntryDetail.getBatchNo() != null ? productEntryDetail.getBatchNo() : ""});
            } else if (productEntryDetail.getProductQuantityDamaged() != null) {
              Double qty = productEntryDetail.getProductQuantity() + (productEntryDetail.getProductQuantityFree() != null ? productEntryDetail.getProductQuantityFree().doubleValue() : 0);
              if (productEntryDetail.getProductQuantityDamaged() > qty) {
                throw new UserMessageException("error.value.damagedQuantity",
                        new String[]{productEntryDetail.getProduct() != null ? productEntryDetail.getProduct().getProductName() : "",
                          productEntryDetail.getBatchNo() != null ? productEntryDetail.getBatchNo() : ""});
              }
              if (productEntryDetail.getProductQuantityDamaged() % 1 > 0 || productEntryDetail.getProductQuantity() % 1 > 0) {
                if (productEntryDetail.getProductQuantityDamaged().doubleValue() != productEntryDetail.getProductQuantity().doubleValue()) {
                  throw new UserMessageException("error.value.quantity",
                          new String[]{productEntryDetail.getProduct() != null ? productEntryDetail.getProduct().getProductName() : "",
                            productEntryDetail.getBatchNo() != null ? productEntryDetail.getBatchNo() : ""});
                }
                if (productEntryDetail.getProductQuantityFree() != null) {
                  throw new UserMessageException("error.value.quantityFree",
                          new String[]{productEntryDetail.getProduct() != null ? productEntryDetail.getProduct().getProductName() : "",
                            productEntryDetail.getBatchNo() != null ? productEntryDetail.getBatchNo() : ""});
                }
              }
            }
          }
          if (productEntryDetail.getProductBatchId() != null) {
            productEntryDetail.getProductSummary().setBatch(productEntryDetail.getBatchNo());
            productEntryDetail.setProductDetailId(ProductDetailService.insertOrUpdate(main, getProductEntry().getAccountId(), productEntryDetail.getProductBatchId(), productEntryDetail.getProductDetailId()));
          }

          if (productEntryDetail != null && productEntryDetail.getProductDetailId() != null) {
            if (productEntryDetail.getProductQuantity() != null && productEntryDetail.getValueRate() != null) {
              if (productEntryDetail.getId() == null) {
                isNew = true;
              }

              getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());

              // sets applicable discounts
              setProductEntryApplicableDiscounts();

              ProductEntryService.insertOrUpdate(main, getProductEntry());

              productEntryDetail.setProductEntryId(getProductEntry());
              ProductEntryDetailService.insertOrUpdate(main, productEntryDetail);

              if (isNew) {
                addNewProductEntryDetail();
              } else if (index == 0) {
                addNewProductEntryDetail();
              }
            }
            //main.close();
            getTaxCalculator().updateProductEntryDetail(main, getProductEntry().getAccountId(), productEntryDetail);
          }

          for (ProductEntryDetail ped : getProductEntryDetailList()) {
            if (ped.getProductSummary() != null && ped.getProductSummary().getProductId() != null) {
              getTaxCalculator().updateProductEntryDetail(main, getProductEntry().getAccountId(), ped);
            }
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  public void actionInsertOrUpdateServiceEntryDetail(MainView main) {
    boolean isNew = false;
    try {
      if (isValueMrpChanged()) {
        main.error("error.mrp.changed");
        return;
      }

      if (isDraft()) {
        Integer index = Jsf.getParameterInt("rownumber");
        ProductEntryDetail productEntryDetail = getServiceEntryDetailList().get(index);

        if (productEntryDetail != null && productEntryDetail.getServiceCommodityId() != null) {
          if (productEntryDetail.getValueRate() != null) {
            if (productEntryDetail.getId() == null) {
              isNew = true;
            }

            getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());

            ProductEntryService.insertOrUpdate(main, getProductEntry());

            productEntryDetail.setProductEntryId(getProductEntry());
            ProductEntryDetailService.insertOrUpdate(main, productEntryDetail);

            if (isNew) {
              addNewServiceEntryDetail();
            } else if (index == 0) {
              addNewServiceEntryDetail();
            }
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   * Method to update the product quantities, Damaged, Shortage, and Excess Quantity.
   *
   * @param main
   * @return
   */
  public String actionUpdateProductEntryProductQuantity(MainView main) {
    try {
      if (getProductEntryDetailId() != null && getProductEntryDetailId().getId() != null) {
        ProductEntryDetailService.updateProdutQuantity(main, getProductEntryDetailId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public String actionUpdateProductPurchaseBenefit(MainView main) {
    try {
      if (getProductEntryDetailId() != null && getProductEntryDetailId().getId() != null) {
        ProductEntryDetailService.updateProdutPurchaseBenefit(main, getProductEntryDetailId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   */
  private void setProductEntryApplicableDiscounts() {
    getProductEntry().setLineItemDiscount(isLineDiscountApplicable() ? SystemRuntimeConfig.LINE_DISCOUNT_APPLICABLE : null);
    getProductEntry().setSpecialDiscountApplicable(isSpecialDiscountApplicable() ? SystemRuntimeConfig.SPECIAL_DISCOUNT_APPLICABLE : null);
    getProductEntry().setCashDiscountApplicable(isCashDiscountApplicable() ? SystemRuntimeConfig.CASH_DISCOUNT_APPLICABLE : null);
    getProductEntry().setCashDiscountTaxable(isTaxableCashDiscount() ? SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE : null);
    getProductEntry().setDiscountsApplicableForService(isDiscountsApplicableForServices() ? SystemRuntimeConfig.DISCOUNT_APPLICABLE_FOR_SERVICES : null);
  }

  /**
   *
   * @param productEntry
   */
  private void setProductEntryApplicableDiscounts(ProductEntry productEntry) {
    setLineDiscountApplicable(getProductEntry().getLineItemDiscount() != null && getProductEntry().getLineItemDiscount().equals(SystemRuntimeConfig.LINE_DISCOUNT_APPLICABLE));
    setSpecialDiscountApplicable(getProductEntry().getSpecialDiscountApplicable() != null && getProductEntry().getSpecialDiscountApplicable().equals(SystemRuntimeConfig.SPECIAL_DISCOUNT_APPLICABLE));
    setCashDiscountApplicable(getProductEntry().getCashDiscountApplicable() != null && getProductEntry().getCashDiscountApplicable().equals(SystemRuntimeConfig.CASH_DISCOUNT_APPLICABLE));
    setTaxableCashDiscount(getProductEntry().getCashDiscountTaxable() != null && getProductEntry().getCashDiscountTaxable().equals(SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE));
    setDiscountsApplicableForServices(getProductEntry().getDiscountsApplicableForService() != null && getProductEntry().getDiscountsApplicableForService().equals(SystemRuntimeConfig.DISCOUNT_APPLICABLE_FOR_SERVICES));
  }

  /**
   * Delete one or many ProductEntry.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductEntry(MainView main) {
    Integer id = null;
    try {
      if (main.isList() && !StringUtil.isEmpty(productEntrySelected)) {
        //many record delete from list
        for (ProductEntry e : productEntrySelected) {
          id = e.getId();
          ProductEntryService.deleteByPk(main, e);
          main.em().commit();
        }
        main.commit("success.delete");
        productEntrySelected = null;
      } else {
        id = getProductEntry().getId();
        ProductEntryService.deleteByPk(main, getProductEntry());  //individual record delete from list or edit form
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

  /**
   *
   * @param main
   * @param productEntryDetail
   */
  private void actionDeleteProductEntryDetail(MainView main, ProductEntryDetail productEntryDetail) {
    if (!isUsedByOtherUser(main)) {
      if (productEntryDetail != null && productEntryDetail.getId() != null && productEntryDetail.getId() > 0) {

        ProductEntryDetailService.deleteByPk(main, productEntryDetail);
        getProductEntryDetailList().remove(productEntryDetail);
        getProductHashCode().remove(productEntryDetail.getProductSummary().getProductHash());
        getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
        setValueMrpChanged(ProductEntryService.isProductValueMrpChanged(main, getProductEntry()));

      } else {
        if (productEntryDetail.getProductSummary() != null && productEntryDetail.getProductSummary().getProductCode() != null) {
          getProductHashCode().remove(productEntryDetail.getProductSummary().getProductHash());
        }
        getProductEntryDetailList().remove(productEntryDetail);
      }
      if (getProductEntryDetailList().isEmpty()) {
        addNewProductEntryDetail();
      }
    }
  }

  public void actionDeleteServiceEntryDetail(MainView main, ProductEntryDetail productEntryDetail) {
    if (!isUsedByOtherUser(main)) {
      if (productEntryDetail != null && productEntryDetail.getId() != null && productEntryDetail.getId() > 0) {
        try {
          ProductEntryDetailService.deleteByPk(main, productEntryDetail);
          getServiceEntryDetailList().remove(productEntryDetail);

          getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
        } catch (Throwable t) {
          main.rollback(t);
        } finally {
          main.close();
        }
      } else {
        getServiceEntryDetailList().remove(productEntryDetail);
      }
      if (getServiceEntryDetailList().isEmpty()) {
        addNewServiceEntryDetail();
      }
    }
  }

  /**
   *
   */
  private void addNewProductEntryDetail() {
    ProductEntryDetail productEntryDetail = new ProductEntryDetail();
    productEntryDetail.setProductEntryId(getProductEntry());
    getProductEntryDetailList().add(0, productEntryDetail);
  }

  private void addNewServiceEntryDetail() {
    ProductEntryDetail productEntryDetail = new ProductEntryDetail();
    productEntryDetail.setProductEntryId(getProductEntry());
    productEntryDetail.setIsService(CommodityService.SERVICE);
    getServiceEntryDetailList().add(0, productEntryDetail);
  }

  /**
   *
   * @return
   */
  public boolean isConfirmed() {
    return (getProductEntry().getProductEntryStatusId() != null && getProductEntry().getProductEntryStatusId().getId().equals(SystemConstants.CONFIRMED));
  }

  public boolean isDraft() {
    return (getProductEntry().getProductEntryStatusId() != null && getProductEntry().getProductEntryStatusId().getId().equals(SystemConstants.DRAFT));
  }

  public boolean isTaxCodeModified() {
    return (getProductEntry().getIsTaxCodeModified() != null && getProductEntry().getIsTaxCodeModified().equals(SystemConstants.TAX_CODE_MODIFIED));
  }

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
   * @param rate
   */
  private void setConsignmentRecieptDetails(ConsignmentRate rate) {
    StringBuilder sb = new StringBuilder();
    if (rate != null && rate.getId() != null) {
      if (rate.getLrAmount() == 0 && rate.getTotalExpenseAmount() > 0) {
        setLrAmount("( " + String.valueOf(rate.getTotalExpenseAmount()) + ")");
        lrAmountMax = rate.getTotalExpenseAmount();
        if (rate.getNoOfInvoice() != null && rate.getNoOfInvoice() == 1 && getProductEntry().getTotalExpenseAmount() == null) {
          getProductEntry().setTotalExpenseAmount(rate.getTotalExpenseAmount());
        }
      } else if (rate.getTotalExpenseAmount().equals(rate.getLrAmount()) && rate.getTotalExpenseAmount() > 0) {
        setLrAmount("( " + String.valueOf(rate.getTotalExpenseAmount()) + ")");
      } else if (rate.getLrAmountDifference() > 0) {
        if (getProductEntry().getTotalExpenseAmount() != null && getProductEntry().getTotalExpenseAmount() > 0) {
          lrAmountMax = getProductEntry().getTotalExpenseAmount() + rate.getLrAmountDifference();
        } else {
          lrAmountMax = rate.getLrAmountDifference();
        }
        sb.append(rate.getLrAmountDifference());
        sb.append(" ( ").append(rate.getTotalExpenseAmount()).append(" )");
        setLrAmount(sb.toString());
      }
    }
  }

  /**
   *
   * @return
   */
  public List<SelectItem> lookUpDiscountBenefit() {
    List<SelectItem> list = new ArrayList<>();
    if (AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
      list.add(new SelectItem("Company", SystemConstants.DISCOUNT_FOR_COMPANY));
    }
    list.add(new SelectItem("Customer", SystemConstants.DISCOUNT_FOR_CUSTOMER));
    return list;
  }

  /**
   *
   * @param main
   * @return
   */
  public List<ConsignmentRate> lookUpConsignmentByAccount(MainView main) {
    try {
      if (StringUtil.isEmpty(getConsignmentRateList())) {
        if (getProductEntry() != null && getProductEntry().getConsignmentId() != null) {
          setConsignmentRateList((List<ConsignmentRate>) ConsignmentService.lookUpConsignmentByAccount(main, getProductEntry().getAccountId(), getProductEntry().getConsignmentId().getId()));
        } else {
          setConsignmentRateList((List<ConsignmentRate>) ConsignmentService.lookUpConsignmentByAccount(main, getProductEntry().getAccountId(), null));
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return getConsignmentRateList();
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<ProductSummary> completeProductDetail(String filter) {
    MainView main = Jsf.getMain();
    try {
      ProductEntryDetail ped = (ProductEntryDetail) Jsf.getAttribute("productEntryItem");
      return ProductEntryService.productSummaryAuto(main, getProductEntry().getAccountId(), filter,
              getProductEntry() != null ? getProductEntry().getId() : null, ped.getProductDetailId() != null ? ped.getProductDetailId().getId() : null);

    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void productSelectEvent(ProductEntryDetail productEntryDetail, Integer rowIndex) {
    MainView main = Jsf.getMain();
    try {
      if (productEntryDetail != null && productEntryDetail.getProductSummary() != null) {
        Product product = ProductService.selectByPk(main, new Product(productEntryDetail.getProductSummary().getProductId()));
        productEntryDetail.setProduct(product);
        productEntryDetail.setProductInvoiceDetailList(ProductEntryService.selectLastFivePurchaseByProductDetail(main, productEntryDetail));
        setProductInvoiceDetailList(productEntryDetail.getProductInvoiceDetailList());
        setProductName(product.getProductName());
        productEntryDetail.setHsnCode(product.getHsnCode());
        productEntryDetail.getProductSummary().setBatch(null);
        if (!StringUtil.isEmpty(getProductInvoiceDetailList())) {
          Jsf.addCallbackParam("purchaseCount", getProductInvoiceDetailList().size());
        } else {
          Jsf.addCallbackParam("purchaseCount", 0);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<ProductSummary> completeProductBatch(String filter) {
    List<ProductSummary> productSummeryList = new ArrayList<>();
    MainView main = Jsf.getMain();
    try {
      ProductEntryDetail ped = (ProductEntryDetail) Jsf.getAttribute("productEntryItem");
      List<ProductSummary> list = getTaxCalculator().productSummaryBatchAuto(main, getProductEntry().getAccountId(), filter,
              getProductEntry() != null ? getProductEntry().getId() : null, ped.getProductDetailId() != null ? ped.getProductDetailId().getId() : null,
              ped.getProduct() != null ? ped.getProduct().getId() : null);
      productSummeryList = list;

//      for (ProductSummary ps : list) {
//        if (getProductHashCode().containsKey(ps.getProductHash())) {
//          if (ped != null && !StringUtil.isEmpty(ped.getProductHash()) && ped.getProductHash().equals(ps.getProductId() + "#" + ps.getProductBatchId())) {
//            productSummeryList.add(ps);
//          }
//        } else {
//          productSummeryList.add(ps);
//        }
//      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return productSummeryList;
  }

  public List<ServiceCommodity> completeServiceDetail(String filter) {
    //ProductEntryDetail ped = (ProductEntryDetail) Jsf.getAttribute("serviceEntryItem");
    return ScmLookupExtView.lookupServiceCommodity(getProductEntry().getCompanyId(), filter);
  }

  /**
   *
   * @param event
   */
  public void consignmentRateItemSelectEvent(SelectEvent event) {
    setLrAmount("");
    ConsignmentRate rate = (ConsignmentRate) event.getObject();

    if (getProductEntry() != null && getProductEntry().getId() != null && getProductEntry().getConsignmentId() != null) {
      if (getProductEntry().getConsignmentId().getId().equals(rate.getId())) {
        getProductEntry().setTotalExpenseAmount(getOldLrAmount());
      } else {
        getProductEntry().setTotalExpenseAmount(null);
      }
    } else {
      getProductEntry().setTotalExpenseAmount(null);
    }

    if (rate != null) {
      setConsignmentRecieptDetails(rate);
      Consignment c = new Consignment();
      c.setId(rate.getId());
      getProductEntry().setConsignmentId(c);
    } else {
      getProductEntry().setConsignmentId(null);
    }

  }

  /**
   * Invoice Discount value change event handler.
   *
   * @param event
   */
  public void invoiceAmountDiscountValueBlurEvent(ValueChangeEvent event) {
    Double dValue = (Double) event.getNewValue();
    setSpecialDiscountApplicable(dValue != null && dValue > 0);
    getProductEntry().setIsInvoiceDiscountInPercentage(null);
    getProductEntry().setInvoiceAmountDiscountPerc(null);
    if (dValue != null) {
      getProductEntry().setIsInvoiceDiscountInPercentage(SystemConstants.DISCOUNT_IN_VALUE);
    }
    getProductEntry().setInvoiceAmountDiscountValue(dValue);
    getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
  }

  /**
   * Invoice discount percentage value change event handler.
   *
   * @param event
   */
  public void invoiceAmountDiscountPercBlurEvent(ValueChangeEvent event) {
    Double dValue = (Double) event.getNewValue();
    setSpecialDiscountApplicable(dValue != null && dValue > 0);
    getProductEntry().setIsInvoiceDiscountInPercentage(null);
    getProductEntry().setInvoiceAmountDiscountValue(null);
    if (dValue != null) {
      getProductEntry().setIsInvoiceDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
    }
    getProductEntry().setInvoiceAmountDiscountPerc(dValue);
    getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
  }

  /**
   *
   * @param productEntryDetail
   * @param rowIndex
   */
  public void productBatchSelectEvent(ProductEntryDetail productEntryDetail, Integer rowIndex) {
    if (productEntryDetail != null && productEntryDetail.getProductSummary() != null) {
      MainView main = Jsf.getMain();
      setRowIndex(rowIndex);
      try {
        productEntryDetail.resetProductEntryDetail();
        setProductInvoiceDetailList(null);
        getTaxCalculator().updateProductEntryDetail(main, getProductEntry().getAccountId(), productEntryDetail);
        productEntryDetail.getProductSummary().setBatch(productEntryDetail.getBatchNo());
        if (productEntryDetail.getProductSummary().getProductBatchId() == null) {
          setRowIndex(rowIndex);
          productEntryDetail.setProductBatchId(null);
          if (count == 1) {
            actionOpenProductPopup(productEntryDetail, rowIndex);
          }
        } else {
          getProductHashCode().put(productEntryDetail.getProductSummary().getProductHash(), productEntryDetail.getProductSummary().getProductId());
          productEntryDetail.after();
          removeProductHashKeys();
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  public void serviceSelectEvent(ProductEntryDetail productEntryDetail, Integer rowIndex) {
    if (productEntryDetail != null && productEntryDetail.getServiceCommodityId() != null) {
      setServiceRowIndex(rowIndex);
      MainView main = Jsf.getMain();
      try {
        productEntryDetail.resetProductEntryDetail();

        //getTaxCalculator().updateProductEntryDetail(main, getAccount(), productEntryDetail);
        productEntryDetail.setTaxCodeId(productEntryDetail.getServiceCommodityId().getSalesTaxCodeId());

//        if (!StringUtil.isEmpty(getProductInvoiceDetailList())) {
//          Jsf.addCallbackParam("purchaseCount", getProductInvoiceDetailList().size());
//        } else {
//          Jsf.addCallbackParam("purchaseCount", 0);
//        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  /**
   * Method to remove unused keys from product hash map.
   */
  private void removeProductHashKeys() {
    boolean keyExist = false;
    StringBuilder keys = new StringBuilder("");
    for (String key : getProductHashCode().keySet()) {
      keyExist = false;
      for (ProductEntryDetail productEntryDetail : getProductEntryDetailList()) {
        if (productEntryDetail.getProductSummary() != null && key.equals(productEntryDetail.getProductSummary().getProductHash())) {
          keyExist = true;
        }
      }
      if (!keyExist) {
        if (keys.length() == 0) {
          keys.append(key);
        } else {
          keys.append(",").append(key);
        }
      }
    }

    if (keys.length() > 0) {
      for (String key : keys.toString().split(",")) {
        getProductHashCode().remove(key);
      }
    }
  }

  /**
   *
   * @param productEntryDetail
   */
  public void tertiaryQtyBlurEvent(ProductEntryDetail productEntryDetail) {
    if (isDraft()) {
      if (productEntryDetail != null) {
        if (productEntryDetail.getPackTertiarySecondaryQty() != null && productEntryDetail.getProductTertairyQuantity() != null) {

          Double sQty = productEntryDetail.getProductTertairyQuantity() * productEntryDetail.getPackTertiarySecondaryQty();
          productEntryDetail.setProductSecondaryQuantity(sQty);

          Double pQty = productEntryDetail.getProductSecondaryQuantity() * productEntryDetail.getPackSecondaryPrimaryQty();
          productEntryDetail.setProductQuantity(pQty);
        }
        if (productEntryDetail.getValueRate() != null) {
          getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
        }
      }
    }
  }

  /**
   *
   * @param productEntryDetail
   */
  public void secondaryQtyBlurEvent(ProductEntryDetail productEntryDetail) {
    if (isDraft()) {
      if (productEntryDetail != null) {
        double totalQty = (productEntryDetail.getProductQuantity() != null ? productEntryDetail.getProductQuantity() : 0)
                + (productEntryDetail.getProductQuantityFree() != null ? productEntryDetail.getProductQuantityFree() : 0); // qty+free
        if (productEntryDetail.getPackSecondaryPrimaryQty() != null && productEntryDetail.getProductSecondaryQuantity() != null) {
          Double pQty = productEntryDetail.getProductSecondaryQuantity() * productEntryDetail.getPackSecondaryPrimaryQty().doubleValue();
          if (pQty != totalQty) {
            productEntryDetail.setProductQuantity(pQty);
            productEntryDetail.setProductQuantityFree(null);
          }

          if (productEntryDetail.getPackTertiary() != null && productEntryDetail.getPackTertiarySecondaryQty() != null) {
            productEntryDetail.setProductTertairyQuantity(productEntryDetail.getProductSecondaryQuantity() / productEntryDetail.getPackTertiarySecondaryQty());
          }
        }
        if (productEntryDetail.getValueRate() != null) {
          getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
        }
      }
    }
  }

  /**
   *
   * @param productEntryDetail
   */
  public void primaryQtyBlurEvent(ProductEntryDetail productEntryDetail) {
    if (isDraft()) {
      if (productEntryDetail != null) {
        double totalQty = (productEntryDetail.getProductQuantity() != null ? productEntryDetail.getProductQuantity() : 0)
                + (productEntryDetail.getProductQuantityFree() != null ? productEntryDetail.getProductQuantityFree() : 0);
        if (productEntryDetail.getProductQuantity() != null) {
          if (productEntryDetail.getPackSecondary() != null && productEntryDetail.getPackSecondaryPrimaryQty() != null) {
            double sQty = (totalQty / productEntryDetail.getPackSecondaryPrimaryQty());
            productEntryDetail.setProductSecondaryQuantity(sQty);
          }

          if (productEntryDetail.getPackTertiary() != null && productEntryDetail.getPackTertiarySecondaryQty() != null) {
            Double tQty = productEntryDetail.getProductSecondaryQuantity() / productEntryDetail.getPackTertiarySecondaryQty();
            productEntryDetail.setProductTertairyQuantity(tQty);
          }
          if (productEntryDetail.getValueRate() != null) {
            getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());

            if (AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
              if (productEntryDetail.getValueRate() > 0 && productEntryDetail.getValuePtsModified() == null && !productEntryDetail.getValueRate().equals(productEntryDetail.getValueExpected())) {
                productEntryDetail.setValuePtsModified(SystemConstants.PTS_VALUE_MODIFIED);
              }
            }
          }
        }
      }
    }
  }

  /**
   *
   */
  public void productQuantityShortageBlurEvent() {
    if (isDraft()) {
      if (getProductEntryDetailId().getProductQuantityShortage() != null) {
        getProductEntryDetailId().setProductQuantityExcess(null);
        getProductEntryDetailId().setExcessQuantityDescription(null);
        getProductEntryDetailId().setExcessQuantityNote(null);
      }
    }
  }

  /**
   *
   * @param productEntryDetail
   */
  public void productFreeQtyBlurEvent(ProductEntryDetail productEntryDetail) {
    if (isDraft()) {
      if (productEntryDetail.getProductQuantityFree() != null) {
        double totalQty = (productEntryDetail.getProductQuantity() != null ? productEntryDetail.getProductQuantity() : 0)
                + (productEntryDetail.getProductQuantityFree() != null ? productEntryDetail.getProductQuantityFree() : 0);
        if (productEntryDetail.getPackSecondary() != null && productEntryDetail.getPackSecondaryPrimaryQty() != null) {
          double sQty = (totalQty / productEntryDetail.getPackSecondaryPrimaryQty());
          productEntryDetail.setProductSecondaryQuantity(sQty);
        }

        if (productEntryDetail.getPackTertiary() != null && productEntryDetail.getPackTertiarySecondaryQty() != null) {
          Double tQty = productEntryDetail.getProductSecondaryQuantity() / productEntryDetail.getPackTertiarySecondaryQty();
          productEntryDetail.setProductTertairyQuantity(tQty);
        }
      }
      if (productEntryDetail != null) {
        getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
      }
    }
  }

  /**
   *
   * @param productEntryDetail
   */
  public void boxRateBlurEvent(ProductEntryDetail productEntryDetail) {
    if (isDraft()) {
      if (productEntryDetail != null) {
        if (productEntryDetail.getValueBoxRate() != null) {
          if (productEntryDetail.getPackSecondary() != null && productEntryDetail.getPackSecondaryPrimaryQty() != null) {
            productEntryDetail.setValueRate(productEntryDetail.getValueBoxRate() / productEntryDetail.getPackSecondaryPrimaryQty());
            getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
            ProductUtil.getPtsValueFromRate(getProductEntry(), productEntryDetail);
          }
        }
      }
    }
  }

  /**
   *
   * @param productEntryDetail
   */
  public void valueRateBlurEvent(ProductEntryDetail productEntryDetail) {
    if (isDraft()) {
      if (productEntryDetail != null) {
        if (productEntryDetail.getValueRate() != null) {
          if (!isPtsDerivedFromMrpPtr()) {
            productEntryDetail.setValuePts(null);
          }

          if (productEntryDetail.getPackSecondary() != null && productEntryDetail.getPackSecondaryPrimaryQty() != null) {
            productEntryDetail.setValueBoxRate(productEntryDetail.getValueRate() * productEntryDetail.getPackSecondaryPrimaryQty());
          }

          getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
          ProductUtil.getPtsValueFromRate(getProductEntry(), productEntryDetail);

        }
      }
    }
  }

  public void serviceRateChangeEvent(ProductEntryDetail productEntryDetail) {
    if (isDraft()) {
      if (productEntryDetail != null && productEntryDetail.getValueRate() != null) {
        getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
      }
    }
  }

  public void taxRateChangeEvent(MainView main, ProductEntryDetail productEntryDetail) {
    try {
      if (isDraft()) {
        if (productEntryDetail != null) {
          if (AccountService.DERIVE_PTS_FROM_MRP_PTR.equals(getProductEntry().getAccountId().getPtsDerivationCriteria())) {
            productEntryDetail.setValueRate(null);
            productEntryDetail.setValueBoxRate(null);
            getTaxCalculator().updateProductEntryDetail(main, getProductEntry().getAccountId(), productEntryDetail);
          }
          getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param productEntryDetail
   * @param index
   * @return
   */
  public String actionOpenProductPopup(ProductEntryDetail productEntryDetail, Integer index) {
    if (productEntryDetail != null && productEntryDetail.getProduct() != null) {
      setRowIndex(index);
      Jsf.popupForm(FileConstant.PRODUCT_POPUP, productEntryDetail, productEntryDetail.getProduct().getId());
      Jsf.update("currentRow");
    }
    return null;
  }

  /**
   * Method to open product detail dialog.
   */
  public void openProductPopup() {
    Integer index = Jsf.getParameterInt("rownumber");
    ProductEntryDetail productEntryDetail = getProductEntryDetailList().get(index);
    if (productEntryDetail != null && productEntryDetail.getProduct() != null && productEntryDetail.getProductDetailId() == null) {
      actionOpenProductPopup(productEntryDetail, index);
    }
  }

  /**
   *
   * @param main
   */
  public void productPopupReturned(MainView main) {
    try {
      if (isDraft()) {
        ProductEntryDetail productEntryDetail = (ProductEntryDetail) Jsf.popupParentValue(ProductEntryDetail.class
        );
        if (productEntryDetail != null && productEntryDetail.getProductBatchId() != null) {
          getTaxCalculator().updateProductEntryDetail(main, getProductEntry().getAccountId(), productEntryDetail);
          getProductHashCode().put(productEntryDetail.getProductSummary().getProductHash(), productEntryDetail.getProductSummary().getProductId());
          removeProductHashKeys();
          productEntryDetail.setProductSummary(new ProductSummary(productEntryDetail));
          productEntryDetail.after();
        }
        if (productEntryDetail.getProductHash() != null && !productEntryDetail.getProductHash().equals(productEntryDetail.getProductSummary().getProductHash())) {
          getProductHashCode().remove(productEntryDetail.getProductHash());
        }
        count++;
        productBatchSelectEvent(productEntryDetail, rowIndex);
        count = 1;
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @return
   */
  public String actionOpenProductFreeQtySchemePopup() {
    try {
      Integer index = Jsf.getParameterInt("rownumber");
      Integer freeQty = Jsf.getParameterInt("freeQty");
      ProductEntryDetail productEntryDetail = getProductEntryDetailList().get(index);
      productEntryDetail.setProductQuantityFree(freeQty);
      setRowIndex(index);
      if (productEntryDetail != null && productEntryDetail.getProductDetailId() != null && productEntryDetail.getProductQuantityFree() != null
              && productEntryDetail.getProductQuantityFree() > 0) {
        Jsf.popupForm(FileConstant.PRODUCT_FREE_QTY_SCHEME_POPUP, productEntryDetail, productEntryDetail.getProductFreeQtySchemeId() != null ? productEntryDetail.getProductFreeQtySchemeId().getId() : null);
      }
    } catch (Throwable t) {
      Jsf.fatal(t, "error.save");
    }
    return null;
  }

  public String actionOpenProductSchemeDiscValuePopup() {
    try {
      Integer index = Jsf.getParameterInt("rownumber");
      Double freeDisc = Jsf.getParameterLong("freeQty").doubleValue();
      ProductEntryDetail productEntryDetail = getProductEntryDetailList().get(index);
      productEntryDetail.setSchemeDiscountValue(freeDisc);
      setRowIndex(index);
      if (productEntryDetail != null && productEntryDetail.getProductDetailId() != null && productEntryDetail.getSchemeDiscountValue() != null
              && productEntryDetail.getSchemeDiscountValue() > 0) {
        Jsf.popupForm(FileConstant.PRODUCT_FREE_QTY_SCHEME_POPUP, productEntryDetail, productEntryDetail.getProductFreeQtySchemeId() != null ? productEntryDetail.getProductFreeQtySchemeId().getId() : null);
      }
    } catch (Throwable t) {
      Jsf.fatal(t, "error.save");
    }
    return null;
  }

  public String actionOpenProductSchemeDiscPercentagePopup() {
    try {
      Integer index = Jsf.getParameterInt("rownumber");
      Double freeDisc = Jsf.getParameterLong("freeQty").doubleValue();
      ProductEntryDetail productEntryDetail = getProductEntryDetailList().get(index);
      productEntryDetail.setSchemeDiscountPercentage(freeDisc);
      setRowIndex(index);
      if (productEntryDetail != null && productEntryDetail.getProductDetailId() != null && productEntryDetail.getSchemeDiscountPercentage() != null
              && productEntryDetail.getSchemeDiscountPercentage() > 0) {
        Jsf.popupForm(FileConstant.PRODUCT_FREE_QTY_SCHEME_POPUP, productEntryDetail, productEntryDetail.getProductFreeQtySchemeId() != null ? productEntryDetail.getProductFreeQtySchemeId().getId() : null);
      }
    } catch (Throwable t) {
      Jsf.fatal(t, "error.save");
    }
    return null;
  }

  /**
   *
   * @param main
   * @return
   */
  public String productFreeQtySchemePopupReturned(MainView main) {
    try {
      if (isDraft()) {
        ProductEntryDetail productEntryDetail = (ProductEntryDetail) Jsf.popupParentValue(ProductEntryDetail.class
        );
        if (productEntryDetail != null && productEntryDetail.getId() != null && productEntryDetail.getProductFreeQtySchemeId() != null) {
          ProductEntryDetailService.updateProductFreeQtyScheme(main, productEntryDetail);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Product Margin pop up
   */
  public void productMarginListPopup() {
    Jsf.popupList(FileConstant.PRODUCT_MARGIN, getProductEntry());
  }

  public boolean isTagged(ProductEntryDetail productEntryDetail) {
    boolean tagged = false;
    if (productEntryDetail.getProductQuantityDamaged() != null && productEntryDetail.getProductQuantityDamaged() > 0) {
      tagged = true;
    } else if (productEntryDetail.getProductQuantityExcess() != null && productEntryDetail.getProductQuantityExcess() > 0) {
      tagged = true;
    } else if (productEntryDetail.getProductQuantityShortage() != null && productEntryDetail.getProductQuantityShortage() > 0) {
      tagged = true;
    }
    return tagged;
  }

  /**
   *
   * @return
   */
  public List<SelectItem> purchaseDiscountBeneficiaries() {
    List<SelectItem> list = new ArrayList<>();
    list.add(new SelectItem("Company", SystemConstants.DISCOUNT_FOR_COMPANY));
    list.add(new SelectItem("Customer", SystemConstants.DISCOUNT_FOR_CUSTOMER));
    return list;
  }

  public List<SelectItemInteger> getSelectItemYesNo() {
    List<SelectItemInteger> listYesNo = new ArrayList<>();
    SelectItemInteger si1 = new SelectItemInteger();
    si1.setItemLabel("Yes");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItemInteger si2 = new SelectItemInteger();
    si2.setItemLabel("No");
    si2.setIntValue(0);
    listYesNo.add(si2);
    return listYesNo;
  }

  /**
   *
   * @param main
   */
  public void actionUpdateInvoiceDiscountBeneficiary(MainView main) {
    try {
      if (main.isEdit() && getProductEntry().getIsInvoiceDiscountToCustomer() != null) {
        ProductEntryService.updateProductEntryInvoiceDiscountBeneficiary(main, getProductEntry());
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
  public void updateInvoiceDiscountBeneficiaryType() {
    if (getProductEntry() != null && getProductEntry().getIsInvoiceDiscountToCustomer() == null) {
      if (AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
        getProductEntry().setIsInvoiceDiscountToCustomer(SystemConstants.DISCOUNT_FOR_COMPANY);
      } else {
        getProductEntry().setIsInvoiceDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
      }
    }
  }

  /**
   *
   */
  public void updateLineDiscountBeneficiaryType() {
    Integer index = Jsf.getParameterInt("rownumber");
    setRowIndex(index);
    ProductEntryDetail productEntryDetail = getProductEntryDetailList().get(index);
    if (productEntryDetail.getIsLineDiscountToCustomer() != null) {
      setLineDiscountBeneficiary(productEntryDetail.getIsLineDiscountToCustomer());
      if (SystemConstants.DISCOUNT_FOR_COMPANY == productEntryDetail.getIsLineDiscountToCustomer()) {
        setProductDiscountReplacement(productEntryDetail.getProductDiscountReplacement());
      }
    } else {
      if (AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
        setLineDiscountBeneficiary(SystemConstants.DISCOUNT_FOR_COMPANY);
      } else {
        setLineDiscountBeneficiary(SystemConstants.DISCOUNT_FOR_CUSTOMER);
      }
      setProductDiscountReplacement(null);
    }
  }

  /**
   *
   */
  public void updateSchemeDiscountBeneficiaryType() {
    Integer index = Jsf.getParameterInt("rownumber");
    setRowIndex(index);
    ProductEntryDetail productEntryDetail = getProductEntryDetailList().get(index);
    if (productEntryDetail.getIsSchemeDiscountToCustomer() != null) {
      setSchemeDiscountToCustomer(productEntryDetail.getIsSchemeDiscountToCustomer());
      setSchemeDiscountReplacement(null);
      if (SystemConstants.DISCOUNT_FOR_COMPANY == productEntryDetail.getIsSchemeDiscountToCustomer()) {
        setSchemeDiscountReplacement(productEntryDetail.getSchemeDiscountReplacement());
      }
    } else {
      if (AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
        setSchemeDiscountToCustomer(SystemConstants.DISCOUNT_FOR_COMPANY);
        setSchemeDiscountReplacement(null);
      } else {
        setSchemeDiscountToCustomer(SystemConstants.DISCOUNT_FOR_CUSTOMER);
        setSchemeDiscountReplacement(null);
      }
    }
  }

  /**
   *
   * @param main
   */
  public void actionUpdateProductPurchaseBeneficiary(MainView main) {
    try {
      ProductEntryDetail productEntryDetail = getProductEntryDetailList().get(getRowIndex());
      productEntryDetail.setIsLineDiscountToCustomer(getLineDiscountBeneficiary());
      productEntryDetail.setProductDiscountPackUnit(getProductDiscountPackUnit());
      if (getLineDiscountBeneficiary() != null && getLineDiscountBeneficiary() == SystemConstants.DISCOUNT_FOR_COMPANY) {
        if (!AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
          setProductDiscountReplacement(1);
        }
        productEntryDetail.setProductDiscountPackUnit(null);
        productEntryDetail.setProductDiscountReplacement(getProductDiscountReplacement());
      }
      if (productEntryDetail.getId() != null) {
        ProductEntryService.updateProductDiscountBeneficiary(main, productEntryDetail);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @return
   */
  public boolean isFreeSchemeApplicable() {
    return (SystemConstants.FREE_SCHEME.equals(getProductEntry().getQuantityOrFree()));
  }

  /**
   * Method to update scheme discount beneficiary of a product.
   *
   * @param main
   */
  public void actionUpdateSchemeDiscountBeneficiary(MainView main) {
    try {
      ProductEntryDetail productEntryDetail = getProductEntryDetailList().get(getRowIndex());
      productEntryDetail.setIsSchemeDiscountToCustomer(getSchemeDiscountToCustomer());
      productEntryDetail.setSchemeDiscountPackUnit(getSchemeDiscountPackUnit());
      productEntryDetail.setSchemeDiscountReplacement(null);
      if (getSchemeDiscountToCustomer() != null && getSchemeDiscountToCustomer() == SystemConstants.DISCOUNT_FOR_COMPANY) {
        if (!AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
          setSchemeDiscountReplacement(1);
        }
        productEntryDetail.setSchemeDiscountPackUnit(null);
        productEntryDetail.setSchemeDiscountReplacement(getSchemeDiscountReplacement());
      }
      if (productEntryDetail.getId() != null) {
        ProductEntryService.updateSchemeDiscountBeneficiary(main, productEntryDetail);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void productDiscountValueChangeEvent(ProductEntryDetail productEntryDetail) {
    try {
      if (productEntryDetail.getDiscountValue() != null) {
        productEntryDetail.setDiscountPercentage(null);
        productEntryDetail.setIsProductDiscountInPercentage(SystemConstants.DISCOUNT_IN_VALUE);
      } else {
        productEntryDetail.setDiscountPercentage(null);
        productEntryDetail.setIsProductDiscountInPercentage(null);
        productEntryDetail.setIsLineDiscountToCustomer(null);
      }
      getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
    } catch (Throwable t) {
      Jsf.fatal(t, "error.save");
    }
  }

  public void productDiscountPercChangeEvent(ProductEntryDetail productEntryDetail) {
    try {
      if (productEntryDetail.getDiscountPercentage() != null) {
        productEntryDetail.setDiscountValue(null);
        productEntryDetail.setIsProductDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
      } else {
        productEntryDetail.setDiscountValue(null);
        productEntryDetail.setIsProductDiscountInPercentage(null);
        productEntryDetail.setIsLineDiscountToCustomer(null);
      }
      getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
    } catch (Throwable t) {
      Jsf.fatal(t, "error.save");
    }
  }

  public void dialogClose() {
    Jsf.closePopup(accountingTransactionDetailItem);
  }

  /**
   * Applying Cash Discount
   */
  public void actionApplyCashDiscount() {
    if (isCashDiscountApplicable()) {
      getProductEntry().setCashDiscountApplicable(SystemRuntimeConfig.CASH_DISCOUNT_APPLICABLE);
    } else {
      getProductEntry().setCashDiscountApplicable(null);
      getProductEntry().setCashDiscountValue(null);
      setTaxableCashDiscount(false);
    }
    if (isTaxableCashDiscount()) {
      getProductEntry().setCashDiscountTaxable(SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE);
    } else {
      getProductEntry().setCashDiscountTaxable(null);
    }
    getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
  }

  public void discountsApplicableForServicesListener() {
    if (discountsApplicableForServices) {
      getProductEntry().setDiscountsApplicableForService(SystemRuntimeConfig.DISCOUNT_APPLICABLE_FOR_SERVICES);
    } else {
      getProductEntry().setDiscountsApplicableForService(null);
    }
    getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
  }

  /**
   *
   */
  public void invoiceAmountChangeEventHandler() {
    if (getProductEntry().getInvoiceAmount() != null) {
      getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
      getProductEntry().setInvoiceAmountVariation(getProductEntry().getInvoiceAmount() - (getProductEntry().getInvoiceValue() == null ? 0 : getProductEntry().getInvoiceValue()));
    } else {
      getProductEntry().setInvoiceAmountVariation(null);
    }
  }

  /**
   *
   */
  public void productDiscountChangeEventHandler() {
    if (!isLineDiscountApplicable()) {
      for (ProductEntryDetail ped : getProductEntryDetailList()) {
        ped.setDiscountValue(null);
        ped.setDiscountPercentage(null);
        ped.setIsLineDiscountToCustomer(null);
      }
    }
    getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
  }

  /**
   * Lookup method for Product discount packing unit.
   *
   * @param main
   * @return
   */
  public List<ProductPacking> lookUpProductPackingUnits(MainView main) {
    List<ProductPacking> packUnitList = null;
    ProductEntryDetail productEntryDetail = null;
    try {
      if (getProductEntryDetailList() != null && getRowIndex() != null) {
        productEntryDetail = getProductEntryDetailList().get(getRowIndex());
        if (productEntryDetail != null) {
          packUnitList = ScmLookupExtView.lookUpProductPackingUnits(productEntryDetail.getProductBatchId());
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return packUnitList;
  }

  public void consignmentPopupReturned(MainView main) {
    Consignment cons = (Consignment) Jsf.popupParentValue(Consignment.class);
    try {
      if (cons != null) {
        setConsignmentRateList(null);
        List<ConsignmentRate> list = lookUpConsignmentByAccount(main);
        for (ConsignmentRate c : list) {
          if (c.getId().equals(cons.getId())) {
            getProductEntry().setConsignmentId(cons);
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

  public void openProduct() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      // opens a new form if id is null else edit
      Jsf.popupForm(FileConstant.PRODUCT, new Product(), null);
    }
  }

  public void openServices() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      // opens a new form if id is null else edit
      Jsf.popupForm(FileConstant.SERVICES, new ServiceCommodity(), null);
    }
  }

  /**
   *
   * @param context
   * @param component
   * @param value
   * @throws ValidatorException
   */
  public void validateProductFreeScheme(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    ProductEntryDetail productEntryDetail = null;
    int index = Integer.parseInt(component.getClientId().split(":")[1]);
    if (isFreeSchemeApplicable() && !StringUtil.isEmpty(getProductEntryDetailList()) && index >= 0) {
      productEntryDetail = getProductEntryDetailList().get(index);
      if (productEntryDetail.getProductQuantityFree() != null && productEntryDetail.getProductQuantityFree() > 0 && productEntryDetail.getProductFreeQtySchemeId() == null) {
        Jsf.error(component, "error.invalid.rangeto");
      }
    }
  }

  public void invoiceDateSelectEvent(SelectEvent event) {
    Date invoiceDate = (Date) event.getObject();
    if (getProductEntry().getInvoiceDate() == null) {
      getProductEntry().setInvoiceDate(invoiceDate);
    }

  }

  /**
   *
   * @return
   */
  private List<TaxCode> lookupGstTaxCode() {
    if (taxCodeList == null) {
      taxCodeList = ScmLookupExtView.lookupGstTaxCode(getProductEntry().getCompanyId());
    }
    return taxCodeList;
  }

  public String inputClss(int rowIndex) {
    if (!isRenderPts()) {
      return "submit_b_" + rowIndex + "_1  text-right";
    }
    return "text-right";
  }

  public List<ProductEntryDetail> getSelectedProductEntryDetails() {
    return selectedProductEntryDetails;
  }

  public void setSelectedProductEntryDetails(List<ProductEntryDetail> selectedProductEntryDetails) {
    this.selectedProductEntryDetails = selectedProductEntryDetails;
  }

  public void deleteProductEntryDetails(MainView main) {
    int id = 0;
    try {
      if (selectedProductEntryDetails != null) {
        for (ProductEntryDetail detail : selectedProductEntryDetails) {
          actionDeleteProductEntryDetail(main, detail);
          if (detail != null && detail.getId() != null) {
            id = detail.getId();
          }
        }
      }
      main.commit("success.delete");
    } catch (Throwable t) {
      AppUtil.referenceError(main, t, id);
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public Long getRowKey() {
    return new Date().getTime();
  }

  public Double getOutstandingAmount() {
    return outstandingAmount;
  }

  public void setOutstandingAmount(Double outstandingAmount) {
    this.outstandingAmount = outstandingAmount;
  }

  public boolean isContractCreate() {
    return contractCreate;
  }

  public String getUpcomingInvoiceNumber() {
    return upcomingInvoiceNumber;
  }

  public void setUpcomingInvoiceNumber(String upcomingInvoiceNumber) {
    this.upcomingInvoiceNumber = upcomingInvoiceNumber;
  }

  public void upcomingInvoiceNumber(MainView main) {
    try {
      upcomingInvoiceNumber = AccountGroupDocPrefixService.getProductEntryInvoiceNumber(main, getProductEntry().getCompanyId(), getProductEntry().getAccountId(), getProductEntry().getFinancialYearId());
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void nextOrPreviousInvoice(MainView main, Integer next) {
    try {
      if (tmpList != null) {
        Integer id = getInvoiceIdByIndex(main, tmpList.indexOf(getProductEntry()), next);
        getProductEntry().setId(id);
        productEntry = ProductEntryService.selectByPk(main, getProductEntry());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private Integer getInvoiceIdByIndex(MainView main, Integer index, Integer next) {
    int rowCount = productEntryLazyModel.getRowCount();
    if ((index >= (rowSize - 1)) && start <= rowCount && next > 0) {
      start += rowSize;
    } else if (next < 0 && index == 0) {
      start -= rowSize;
      index = rowSize;
    }
    loadPreviousNextProductEntry(main);
    ProductEntry invoice = tmpList.get((index + next == rowSize) ? 0 : index + next);
    return invoice.getId();
  }

  public boolean isNextButton() {
    int rowCount = productEntryLazyModel == null ? 0 : productEntryLazyModel.getRowCount();
    if (tmpList != null && (tmpList.indexOf(getProductEntry()) != (tmpList.size() - 1) || ((start + rowSize) <= rowCount))) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isPreviousButton() {
    if (tmpList == null) {
      return false;
    }
    if (tmpList.indexOf(getProductEntry()) == 0 && start == 0) {
      return false;
    } else {
      return true;
    }
  }

  private void loadPreviousNextProductEntry(MainView main) {
    main.getPageData().setSearchKeyWord(null);
    AppPage.move(main.getPageData(), start, rowSize, sortColumn, sOrder.name());
    tmpList = ProductEntryService.listPagedByAccount(main, getProductEntry().getAccountId().getId(), isOpeningStockEntry(), getProductEntry().getCompanyId().getCurrentFinancialYear());
  }

  private boolean isUsedByOtherUser(MainView main) {
    if (getProductEntry().getId() != null) {
      ProductEntry pe = ProductEntryService.selectByPk(main, getProductEntry());
      if (ProductEntryService.isProductEntryEditable(pe, getProductEntry())) {
        return false;
      } else {
        main.error("error.past.record.edit");
        selectProductEntryForEdit(main);
        return true;
      }
    } else {
      return false;
    }

  }

  private void selectProductEntryForEdit(MainView main) {
    setProductEntry(ProductEntryService.selectByPk(main, getProductEntry()));
    setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getProductEntry().getTaxProcessorId().getProcessorClass()));
    updateConsignmentDetails(main, getProductEntry().getConsignmentId());
    setProductEntryDetailList(ProductEntryDetailService.selectProductEntryDetailByProductEntryId(main, getProductEntry(), getTaxCalculator()));
    setServiceEntryDetailList(ProductEntryDetailService.selectServiceEntryDetails(main, getProductEntry()));
    if (getProductEntry().getCashDiscountApplicable() != null) {
      setCashDiscountApplicable(true);
      if (getProductEntry().getCashDiscountTaxable() != null) {
        setTaxableCashDiscount(true);
      }
    } else {
      setCashDiscountApplicable(false);
      setTaxableCashDiscount(true);
    }

    if (getProductEntryDetailList() != null && getProductEntryDetailList().isEmpty()) {
      if (getProductEntryDetailList() == null) {
        productEntryDetailList = new ArrayList<>();
      }
      ProductEntryDetail dtl = new ProductEntryDetail();
      getProductEntryDetailList().add(0, dtl);
    }

    if (getServiceEntryDetailList() != null && getServiceEntryDetailList().isEmpty()) {
      if (getServiceEntryDetailList() == null) {
        serviceEntryDetailList = new ArrayList<>();
      }
      ProductEntryDetail dtl = new ProductEntryDetail();
      getServiceEntryDetailList().add(0, dtl);
    }
  }

  public void productSchemeDiscValueBlurEvent(ProductEntryDetail productEntryDetail) {
    if (productEntryDetail.getSchemeDiscountValue() != null) {
      productEntryDetail.setSchemeDiscountPercentage(null);
      productEntryDetail.setIsSchemeDiscountInPercentage(SystemConstants.DISCOUNT_IN_VALUE);
    } else {
      productEntryDetail.setIsSchemeDiscountInPercentage(null);
    }
    if (isDraft()) {
      if (productEntryDetail != null) {
        getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
      }
    }
  }

  public void productSchemeDiscPercBlurEvent(ProductEntryDetail productEntryDetail) {
    if (productEntryDetail.getSchemeDiscountPercentage() != null) {
      productEntryDetail.setSchemeDiscountValue(null);
      productEntryDetail.setIsSchemeDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
    } else {
      productEntryDetail.setIsSchemeDiscountInPercentage(null);
    }
    if (isDraft()) {
      if (productEntryDetail != null) {
        getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), getServiceEntryDetailList());
      }
    }
  }

  public Date getMinInvoiceDate() {
    Date minInvoiceDate = null;
    if (getProductEntry().getProductEntryDate() != null) {
      minInvoiceDate = getProductEntry().getProductEntryDate();
      Calendar c = Calendar.getInstance();
      c.setTime(minInvoiceDate);
      c.add(Calendar.MONTH, -4);
      minInvoiceDate = c.getTime();
    }
    return minInvoiceDate;
  }

  public void calculateTotalDiscounts() {
    totalSchemeDiscount = 0.0;
    totalInvoiceDiscount = 0.0;
    totalProductDiscount = 0.0;
    totalQty = 0.0;
    totalSgst = 0.0;
    totalCgst = 0.0;
    totalIgst = 0.0;
    if (getProductEntryDetailList() != null) {
      for (ProductEntryDetail list : getProductEntryDetailList()) {
        totalSchemeDiscount += list.getSchemeDiscountValue() != null ? list.getSchemeDiscountValue() : 0;
        totalProductDiscount += list.getDiscountValue() != null ? list.getDiscountValue() : 0;
        totalInvoiceDiscount += list.getInvoiceDiscountValue() != null ? list.getInvoiceDiscountValue() : 0;
        totalSgst += list.getValueSgst() != null ? list.getValueSgst() : 0;
        totalCgst += list.getValueCgst() != null ? list.getValueCgst() : 0;
        totalIgst += list.getValueIgst() != null ? list.getValueIgst() : 0;
        totalQty += (list.getProductQuantity() != null ? list.getProductQuantity() : 0) + (list.getProductQuantityFree() != null ? list.getProductQuantityFree() : 0);
      }
    }
  }

  public void accountInvoiceNoEdit(MainView main, ProductEntry productEntry) {
    try {
      if (getProductEntry().getAccountInvoiceNo() != null && getProductEntry().getId() != null) {
        getProductEntry().setAccountInvoiceNo(getProductEntry().getAccountInvoiceNo().trim().toUpperCase());
        if (ProductEntryService.isAccountInvoiceNoUpdateable(main, productEntry)) {
          ProductEntryService.updateAccountInvoiceNo(main, productEntry);
          main.commit("success.save");
        } else {
          throw new UserMessageException("error.exist.documentNo", new String[]{getProductEntry().getAccountInvoiceNo()});
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
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

  public Double getTotalCgst() {
    return totalCgst;
  }

  public Double getTotalSgst() {
    return totalSgst;
  }

  public Double getTotalIgst() {
    return totalIgst;
  }

  public boolean isIgstTransaction() {
    return igstTransaction;
  }

  public void setIgstTransaction(boolean igstTransaction) {
    this.igstTransaction = igstTransaction;
  }

  public void purchaseFromSalesPopupReturned(MainView main) {
    try {
      setProductEntryDetailList(getProductEntry().getProductEntryDetailList());
      getTaxCalculator().processProductEntryCalculation(getProductEntry(), getProductEntryDetailList(), null);
      for (ProductEntryDetail detail : productEntryDetailList) {
        if (detail.getDiscountValue() != null) {
          setLineDiscountApplicable(true);
          break;
        }
        setLineDiscountApplicable(false);
      }
      if (getProductEntry().getCashDiscountApplicable() != null) {
        setCashDiscountApplicable(getProductEntry().getCashDiscountApplicable() == 1 ? true : false);
      } else {
        setCashDiscountApplicable(false);
      }
      if (getProductEntry().getCashDiscountTaxable() != null) {
        setTaxableCashDiscount(getProductEntry().getCashDiscountTaxable() == 1 ? true : false);
      } else {
        setTaxableCashDiscount(false);
      }
      if (getProductEntry().getInvoiceDiscountValue() != null || getProductEntry().getInvoiceAmountDiscountPerc() != null) {
        setSpecialDiscountApplicable(true);
      } else {
        setSpecialDiscountApplicable(false);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select", null);
    } finally {
      main.close();
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
    CompanySettings companySettings = getProductEntry().getCompanyId().getCompanySettings();
    for (CompanyFinancialYear fy : getProductEntry().getCompanyId().getCompanyFinancialYearList()) {
      if (getProductEntry().getFinancialYearId().equals(fy.getFinancialYearId())) {
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
        if (productEntry != null && getProductEntry().getProductEntryStatusId() != null && getProductEntry().getProductEntryStatusId().getId().intValue() > SystemConstants.DRAFT) {
          Date entryDate = getProductEntry().getProductEntryDate();
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

  public String actionNewForm(MainView main) {
    main.setViewType(ViewTypes.newform);
    return null;
  }

  public void companyBeneficiarySelection(boolean schemeDisc) {
    if (schemeDisc) {
      if (schemeDiscountToCustomer != null && schemeDiscountToCustomer == 0 && !AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
        setSchemeDiscountReplacement(SystemConstants.DISCOUNT_REPLACEMENT);
        setRenderReplacement(false);
      }
    } else {
      if (lineDiscountBeneficiary != null && lineDiscountBeneficiary == 0 && !AccountUtil.isSuperStockiest(getProductEntry().getAccountId())) {
        setProductDiscountReplacement(SystemConstants.DISCOUNT_REPLACEMENT);
        setRenderReplacement(false);
      }
    }
  }

  public boolean isRenderReplacement() {
    return renderReplacement;
  }

  public void setRenderReplacement(boolean renderReplacement) {
    this.renderReplacement = renderReplacement;
  }

  public boolean isDiscountsApplicableForServices() {
    return discountsApplicableForServices;
  }

  public void setDiscountsApplicableForServices(boolean discountsApplicableForServices) {
    this.discountsApplicableForServices = discountsApplicableForServices;
  }

  public boolean isDisableServiceDiscount() {
    disableServiceDiscount = false;
    if (getProductEntryDetailList() != null) {
      for (ProductEntryDetail detail : getProductEntryDetailList()) {
        if (detail.getValueRate() != null) {
          disableServiceDiscount = true;
          break;
        }
      }
    }
    return disableServiceDiscount;
  }

  public void setDisableServiceDiscount(boolean disableServiceDiscount) {
    this.disableServiceDiscount = disableServiceDiscount;
  }

}
