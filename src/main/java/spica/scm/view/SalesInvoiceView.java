/*
 * @(#)SalesInvoiceView.java	1.0 Fri Dec 23 10:28:08 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * SalesInvoiceView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:08 IST 2016
 */
@Named(value = "salesInvoiceView")
@ViewScoped
public class SalesInvoiceView implements Serializable {

//  private transient SalesInvoice salesInvoice;	//Domain object/selected Domain.
//  private transient LazyDataModel<SalesInvoice> salesInvoiceLazyModel; 	//For lazy loading datatable.
//  private transient SalesInvoice[] salesInvoiceSelected;	 //Selected Domain Array
//  private transient boolean salesOrderReference;
//  private transient Company company;
//  private transient Account account;
//  private transient AccountGroup accountGroup;
//  private transient SalesAccount salesAccount;
//  private transient List<SalesInvoiceItem> salesInvoiceItemList;
//  private transient HashMap<String, Integer> productHashCode;
//  private transient Integer proformaExpireDays;
//  private transient Integer salesInvoiceFilter;
//  private transient TaxCalculator taxCalculator;
//  private transient boolean salesInvoiceEditable;
//
//  private transient boolean proformaToInvoice;
//  private transient boolean cashDiscountApplicable;
//  private transient boolean cashDiscountTaxable;
//  private transient boolean interstateSales;
//  private transient boolean intrastateSales;
//  private transient boolean internationalSales;
//  private transient boolean renderPtr;
//  private transient boolean renderPts;
//  private transient boolean cancelable;
//  private transient boolean lineItemExist;
//  private transient AccountingTransactionDetailItem accountingTransactionDetailItem;
//  private transient Consignment consignment;
//  private transient List<Consignment> customerConsignmentList;
//  private transient String pageTitle;
//  private transient List<RelatedItems> relatedItemsList;
//  private boolean taxEditable;
//  private List<TaxCode> taxCodeList;
//  private transient boolean valueMrpChanged;
//  private transient String productName;
//  private transient List<ProductInvoiceDetail> productInvoiceDetailList;
//  private transient Double customerOutstandingValue;
//
//  @PostConstruct
//  public void init() {
//    accountingTransactionDetailItem = (AccountingTransactionDetailItem) Jsf.popupParentValue(AccountingTransactionDetailItem.class);
//    if (accountingTransactionDetailItem != null) {
//      getSalesInvoice().setId(accountingTransactionDetailItem.getAccountingTransactionDetailId().getAccountingTransactionId().getEntityId());
//    }
//  }
//
//  /**
//   * Default Constructor.
//   */
//  public SalesInvoiceView() {
//    super();
//  }
//
//  /**
//   * Return SalesInvoice.
//   *
//   * @return SalesInvoice.
//   */
//  public SalesInvoice getSalesInvoice() {
//    if (salesInvoice == null) {
//      salesInvoice = new SalesInvoice();
//    }
//    return salesInvoice;
//  }
//
//  public boolean isTaxEditable() {
//    return taxEditable;
//  }
//
//  public void setTaxEditable(boolean taxEditable) {
//    this.taxEditable = taxEditable;
//  }
//
//  public List<TaxCode> getTaxCodeList() {
//    return taxCodeList;
//  }
//
//  public void setTaxCodeList(List<TaxCode> taxCodeList) {
//    this.taxCodeList = taxCodeList;
//  }
//
//  public String getPageTitle() {
//    return pageTitle;
//  }
//
//  public void setPageTitle(String pageTitle) {
//    this.pageTitle = pageTitle;
//  }
//
//  public List<RelatedItems> getRelatedItemsList() {
//    return relatedItemsList;
//  }
//
//  public void setRelatedItemsList(List<RelatedItems> relatedItemsList) {
//    this.relatedItemsList = relatedItemsList;
//  }
//
//  public String getProductName() {
//    return productName;
//  }
//
//  public void setProductName(String productName) {
//    this.productName = productName;
//  }
//
//  public List<ProductInvoiceDetail> getProductInvoiceDetailList() {
//    return productInvoiceDetailList;
//  }
//
//  public void setProductInvoiceDetailList(List<ProductInvoiceDetail> productInvoiceDetailList) {
//    this.productInvoiceDetailList = productInvoiceDetailList;
//  }
//
//  /**
//   * Set SalesInvoice.
//   *
//   * @param salesInvoice.
//   */
//  public void setSalesInvoice(SalesInvoice salesInvoice) {
//    this.salesInvoice = salesInvoice;
//  }
//
//  public List<SalesInvoiceItem> getSalesInvoiceItemList() {
//    return salesInvoiceItemList;
//  }
//
//  public void setSalesInvoiceItemList(List<SalesInvoiceItem> salesInvoiceItemList) {
//    this.salesInvoiceItemList = salesInvoiceItemList;
//  }
//
//  public boolean isSalesOrderReference() {
//    return salesOrderReference;
//  }
//
//  public void setSalesOrderReference(boolean salesOrderReference) {
//    this.salesOrderReference = salesOrderReference;
//  }
//
//  public Company getCompany() {
//    if (company == null) {
//      company = UserRuntimeView.instance().getCompany();
//    }
//    return company;
//  }
//
//  public void setCompany(Company company) {
//    this.company = company;
//  }
//
//  public TaxCalculator getTaxCalculator() {
//    return taxCalculator;
//  }
//
//  public void setTaxCalculator(TaxCalculator taxCalculator) {
//    this.taxCalculator = taxCalculator;
//  }
//
//  public List<Consignment> getCustomerConsignmentList() {
//    return customerConsignmentList;
//  }
//
//  public void setCustomerConsignmentList(List<Consignment> customerConsignmentList) {
//    this.customerConsignmentList = customerConsignmentList;
//  }
//
//  public boolean isValueMrpChanged() {
//    return valueMrpChanged;
//  }
//
//  public void setValueMrpChanged(boolean valueMrpChanged) {
//    this.valueMrpChanged = valueMrpChanged;
//  }
//
//  public Double getCustomerOutstandingValue() {
//    return customerOutstandingValue;
//  }
//
//  public void setCustomerOutstandingValue(Double customerOutstandingValue) {
//    this.customerOutstandingValue = customerOutstandingValue;
//  }
//
//  public Account getAccount() {
//    if (account == null) {
//      if (UserRuntimeView.instance().getAccount() != null) {
//        setAccount(UserRuntimeView.instance().getAccount());
//      }
//    }
//    return account;
//  }
//
//  public void setAccount(Account account) {
//    this.account = account;
//  }
//
//  public AccountGroup getAccountGroup() {
//    if (UserRuntimeView.instance().getAccountGroup() != null) {
//      accountGroup = UserRuntimeView.instance().getAccountGroup();
//    }
//    return accountGroup;
//  }
//
//  public void setAccountGroup(AccountGroup accountGroup) {
//    this.accountGroup = accountGroup;
//  }
//
//  public SalesAccount getSalesAccount() {
//    return salesAccount;
//  }
//
//  public void setSalesAccount(SalesAccount salesAccount) {
//    this.salesAccount = salesAccount;
//  }
//
//  public Consignment getConsignment() {
//    return consignment;
//  }
//
//  public void setConsignment(Consignment consignment) {
//    this.consignment = consignment;
//  }
//
//  public Integer getSalesInvoiceFilter() {
//    return salesInvoiceFilter;
//  }
//
//  public void setSalesInvoiceFilter(Integer salesInvoiceFilter) {
//    this.salesInvoiceFilter = salesInvoiceFilter;
//  }
//
//  public HashMap<String, Integer> getProductHashCode() {
//    if (productHashCode == null) {
//      productHashCode = new HashMap<>();
//    }
//    return productHashCode;
//  }
//
//  public void setProductHashCode(HashMap<String, Integer> productHashCode) {
//    this.productHashCode = productHashCode;
//  }
//
//  public boolean isCashDiscountApplicable() {
//    return cashDiscountApplicable;
//  }
//
//  public void setCashDiscountApplicable(boolean cashDiscountApplicable) {
//    this.cashDiscountApplicable = cashDiscountApplicable;
//  }
//
//  public boolean isCashDiscountTaxable() {
//    return cashDiscountTaxable;
//  }
//
//  public void setCashDiscountTaxable(boolean cashDiscountTaxable) {
//    this.cashDiscountTaxable = cashDiscountTaxable;
//  }
//
//  public Integer getProformaExpireDays() {
//    return proformaExpireDays;
//  }
//
//  public void setProformaExpireDays(Integer proformaExpireDays) {
//    this.proformaExpireDays = proformaExpireDays;
//  }
//
//  public boolean isProformaToInvoice() {
//    return proformaToInvoice;
//  }
//
//  public void setProformaToInvoice(boolean proformaToInvoice) {
//    this.proformaToInvoice = proformaToInvoice;
//  }
//
//  public boolean isInterstateSales() {
//    return interstateSales;
//  }
//
//  public void setInterstateSales(boolean interstateSales) {
//    this.interstateSales = interstateSales;
//  }
//
//  public boolean isIntrastateSales() {
//    return intrastateSales;
//  }
//
//  public void setIntrastateSales(boolean intrastateSales) {
//    this.intrastateSales = intrastateSales;
//  }
//
//  public boolean isInternationalSales() {
//    return internationalSales;
//  }
//
//  public void setInternationalSales(boolean internationalSales) {
//    this.internationalSales = internationalSales;
//  }
//
//  public boolean isRenderPtr() {
//    return renderPtr;
//  }
//
//  public void setRenderPtr(boolean renderPtr) {
//    this.renderPtr = renderPtr;
//  }
//
//  public boolean isRenderPts() {
//    return renderPts;
//  }
//
//  public void setRenderPts(boolean renderPts) {
//    this.renderPts = renderPts;
//  }
//
//  public boolean isLineItemExist() {
//    return lineItemExist;
//  }
//
//  public void setLineItemExist(boolean lineItemExist) {
//    this.lineItemExist = lineItemExist;
//  }
//
//  public boolean isSalesInvoiceItemDeleted(SalesInvoiceItem salesInvoiceItem) {
//    if (salesInvoiceItem != null && salesInvoiceItem.getProformaItemStatus() != null) {
//      return ((salesInvoiceItem.getProformaItemStatus().equals(SalesInvoiceUtil.PROFORMA_ITEM_DELETED)));
//    }
//    return false;
//  }
//
//  public boolean isCancelable() {
//    return cancelable;
//  }
//
//  public void setCancelable(boolean cancelable) {
//    this.cancelable = cancelable;
//  }
//
//  /**
//   * Return LazyDataModel of SalesInvoice.
//   *
//   * @return
//   */
//  public LazyDataModel<SalesInvoice> getSalesInvoiceLazyModel() {
//    return salesInvoiceLazyModel;
//  }
//
//  /**
//   * Return SalesInvoice[].
//   *
//   * @return
//   */
//  public SalesInvoice[] getSalesInvoiceSelected() {
//    return salesInvoiceSelected;
//  }
//
//  /**
//   * Set SalesInvoice[].
//   *
//   * @param salesInvoiceSelected
//   */
//  public void setSalesInvoiceSelected(SalesInvoice[] salesInvoiceSelected) {
//    this.salesInvoiceSelected = salesInvoiceSelected;
//  }
//
//  /**
//   * Change view of
//   *
//   * @param main
//   * @param viewType
//   * @return
//   */
//  public String switchSalesInvoice(MainView main, String viewType) {
//    //this.main = main;
//    if (!StringUtil.isEmpty(viewType)) {
//      try {
//        main.setViewType(viewType);
//        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
//          if (UserRuntimeView.instance().getCompany() == null) {
//            main.error("company.required");
//            main.setViewType(ViewTypes.list);
//            return null;
//          } else if (UserRuntimeView.instance().getAccountGroup() == null) {
//            main.error("account.required");
//            main.setViewType(ViewTypes.list);
//            return null;
//          } else {
//            setSalesInvoiceNewFormView();
//            generatePageTitle();
//          }
//        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
//          selectSalesInvoiceForEdit(main);
//          setValueMrpChanged(SalesInvoiceService.isProductValueMrpChanged(main, getSalesInvoice()));
//          validateSalesInvoiceIsEditable();
//          consignment = ConsignmentCommodityService.consignmentBySalesInvoice(main, getSalesInvoice());
//          customerConsignmentList = ConsignmentService.selectConsignmentByCustomer(main, getSalesInvoice().getCustomerId());
//          setRenderPtr(SalesInvoiceService.isPtsPtrRenderable(main, getAccountGroup(), false));
//          setRenderPts(SalesInvoiceService.isPtsPtrRenderable(main, getAccountGroup(), true));
//          setCancelable(isConfirmed() || isConfirmedAndPacked());
//          generatePageTitle();
//          if (isDraft()) {
//            setCustomerOutstandingValue(selectCustomerOutstanding(main, getSalesInvoice().getCustomerId(), getSalesInvoice().getAccountGroupId()));
//            //setRelatedItemsList(SalesInvoiceService.selectRelatedItemsOfSalesInvoice(main, getSalesInvoice()));
//          }
//          lookupGstTaxCode();
//          hasLineItems();
//        } else if (ViewType.list.toString().equals(viewType)) {
//          resetSalesInvoiceView();
//          setPageTitle("Sales Invoice");
//          setRelatedItemsList(null);
//          setTaxCodeList(null);
//          setTaxEditable(false);
//          setLineItemExist(false);
//          loadSalesInvoiceList(main);
//        }
//      } catch (Throwable t) {
//        main.rollback(t);
//      } finally {
//        main.close();
//      }
//    }
//    return null;
//  }
//
//  /**
//   *
//   * @return
//   */
//  private String generatePageTitle() {
//    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceType() != null) {
//      setPageTitle(getSalesInvoice().getSalesInvoiceType().equals(SystemConstants.SALES_INVOICE_TYPE_PROFORMA) ? "Pro-Forma Invoice" : "Sales Invoice");
//    } else {
//      setPageTitle("Sales Invoice");
//    }
//    return "";
//  }
//
//  /**
//   *
//   */
//  private void resetSalesInvoiceView() {
//    setValueMrpChanged(false);
//    setAccount(null);
//    setCompany(null);
//    setAccountGroup(null);
//    setCompany(null);
//    setSalesAccount(null);
//    setProformaToInvoice(false);
//    setSalesInvoiceItemList(null);
//    getProductHashCode().clear();
//    setCashDiscountApplicable(false);
//    setCashDiscountTaxable(false);
//    salesInvoiceEditable = true;
//    setRenderPtr(false);
//    setRenderPts(false);
//    setProductName(null);
//    setProductInvoiceDetailList(null);
//  }
//
//  /**
//   *
//   */
//  private void setSalesInvoiceNewFormView() {
//    getSalesInvoice().reset();
//    getSalesInvoice().setCompanyId(getCompany());
//    getSalesInvoice().setCompanyId(getCompany());
//    getSalesInvoice().setAccountGroupId(getAccountGroup());
//    getSalesInvoice().setDecimalPrecision(getAccountGroup().getCustomerDecimalPrecision() == null ? 2 : getAccountGroup().getCustomerDecimalPrecision());
//    getSalesInvoice().setServiceAsExpense(getAccountGroup().getServiceAsExpense());
//    getSalesInvoice().setTaxProcessorId(getCompany().getCountryTaxRegimeId().getTaxProcessorId());
//    setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesInvoice().getTaxProcessorId().getProcessorClass()));
//    getSalesInvoice().setInvoiceEntryDate(new Date());
//    getSalesInvoice().setInvoiceDate(new java.util.Date());
//  }
//
//  /**
//   *
//   */
//  private void setSalesBusinessModes(MainView main) {
//    if (getSalesInvoice() != null && getSalesInvoice().getCustomerId() != null) {
//      if (getSalesInvoice().getCustomerId().getStateId() != null) {
//        setInternationalSales(SystemRuntimeConfig.isInternationalBusiness(getCompany().getCountryId().getId(), getSalesInvoice().getCustomerId().getStateId().getId()));
//        setInterstateSales(SystemRuntimeConfig.isInterstateBusiness(getCompany().getStateId().getStateCode(), getSalesInvoice().getCustomerId().getStateId().getStateCode()));
//        setIntrastateSales(SystemRuntimeConfig.isIntrastateBusiness(getCompany().getStateId().getStateCode(), getSalesInvoice().getCustomerId().getStateId().getStateCode()));
//      }
//      getSalesInvoice().getCustomerId().setTradeProfile(CustomerService.getCustomerTradeProfile(main, getSalesInvoice().getCustomerId()));
//    }
//  }
//
//  /**
//   * Selecting the Sales invoice for editing/Updating.
//   *
//   * @param main
//   */
//  private void selectSalesInvoiceForEdit(MainView main) {
//    setSalesInvoice((SalesInvoice) SalesInvoiceService.selectByPk(main, getSalesInvoice()));
//    setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesInvoice().getTaxProcessorId().getProcessorClass()));
//    setSalesBusinessModes(main);
//    getSalesInvoice().setDecimalPrecision(getAccountGroup().getCustomerDecimalPrecision() == null ? 2 : getAccountGroup().getCustomerDecimalPrecision());
//    selectSalesInvoiceItemList(main, getSalesInvoice());
//    if (getSalesInvoice().getCashDiscountApplicable() != null) {
//      setCashDiscountApplicable(true);
//      if (getSalesInvoice().getCashDiscountTaxable() != null) {
//        setCashDiscountTaxable(true);
//      }
//    } else {
//      setCashDiscountApplicable(false);
//      setCashDiscountTaxable(false);
//    }
//
//    if (getSalesInvoiceItemList() != null && getSalesInvoiceItemList().isEmpty()) {
//      actionAddNewLineItem();
//    }
//
//  }
//
//  /**
//   *
//   * @param main
//   * @param salesInvoice
//   */
//  private void selectSalesInvoiceItemList(MainView main, SalesInvoice salesInvoice) {
////    if (getSalesInvoiceItemList() == null) {
//    setSalesInvoiceItemList(SalesInvoiceItemService.selectSalesInvoiceItemBySalesInvoice(main, salesInvoice, getTaxCalculator()));
//    getTaxCalculator().processSalesInvoiceCalculation(salesInvoice, getSalesInvoiceItemList(), true);
////    }
//  }
//
//  /**
//   * Create salesInvoiceLazyModel.
//   *
//   * @param main
//   */
//  private void loadSalesInvoiceList(final MainView main) {
//    if (salesInvoiceLazyModel == null) {
//      salesInvoiceLazyModel = new LazyDataModel<SalesInvoice>() {
//        private List<SalesInvoice> list;
//
//        @Override
//        public List<SalesInvoice> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
//          try {
//            if (getAccountGroup() != null) {
//              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
//              list = SalesInvoiceService.listPaged(main, getCompany(), getAccountGroup(), getSalesInvoiceFilter(), null, null);
//              main.commit(salesInvoiceLazyModel, first, pageSize);
//            }
//          } catch (Throwable t) {
//            main.rollback(t, "error.list");
//            return null;
//          } finally {
//            main.close();
//          }
//          return list;
//        }
//
//        @Override
//        public Object getRowKey(SalesInvoice salesInvoice) {
//          return salesInvoice.getId();
//        }
//
//        @Override
//        public SalesInvoice getRowData(String rowKey) {
//          if (list != null) {
//            for (SalesInvoice obj : list) {
//              if (rowKey.equals(obj.getId().toString())) {
//                return obj;
//              }
//            }
//          }
//          return null;
//        }
//      };
//    }
//  }
//
//  /**
//   *
//   */
//  private void uploadFiles() {
//    String SUB_FOLDER = "scm_sales_invoice/";
//  }
//
//  /**
//   *
//   * @param main
//   * @param status
//   * @return
//   */
//  public String actionUpdateTaxVariation(MainView main, Integer status) {
//    try {
//      getSalesInvoiceItemList().forEach((sitem) -> {
//        sitem.setIsTaxCodeModified(SystemConstants.TAX_CODE_MODIFIED - SystemConstants.TAX_CODE_MODIFIED);
//      });
//      getSalesInvoice().setIsTaxCodeModified(SystemConstants.TAX_CODE_MODIFIED - SystemConstants.TAX_CODE_MODIFIED);
//      return saveSalesInvoice(main, status);
//    } catch (Throwable t) {
//      getSalesInvoice().setIsTaxCodeModified(SystemConstants.TAX_CODE_MODIFIED);
//      main.rollback(t);
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  /**
//   * Insert or update.
//   *
//   * @param main
//   * @param status
//   * @return the page to display.
//   */
//  public String saveSalesInvoice(MainView main, int status) {
//    if ((status == SystemConstants.CONFIRMED) && !hasSalesInvoiceItem()) {
//      main.error("error.product.missing");
//      return null;
//    }
//    if (isValueMrpChanged()) {
//      main.error("error.mrp.changed");
//      return null;
//    }
//
//    if (status != SystemConstants.CONFIRMED) {
//      getSalesInvoice().setSalesInvoiceStatusId(new SalesInvoiceStatus(status));
//    }
//    return saveOrCloneSalesInvoice(main, "save", status);
//  }
//
//  /**
//   *
//   * @param main
//   * @return
//   */
//  public String saveProFormaInvoice(MainView main) {
//    if (!hasSalesInvoiceItem()) {
//      main.error("error.product.missing");
//      return null;
//    }
//    getSalesInvoice().setSalesInvoiceType(SystemConstants.SALES_INVOICE_TYPE_PROFORMA);
//    getSalesInvoice().setProformaCreatedAt(new Date());
//    getSalesInvoice().setInvoiceNo("PF" + (getSalesInvoice().getInvoiceNo().substring(2, getSalesInvoice().getInvoiceNo().length())));
//    if (getProformaExpireDays() != null) {
//      getSalesInvoice().setProformaExpiringAt(SalesInvoiceUtil.addDaysToDate(new Date(), getProformaExpireDays()));
//    }
//    return saveOrCloneSalesInvoice(main, "save", SystemConstants.DRAFT);
//  }
//
//  /**
//   * Duplicate or clone a record.
//   *
//   * @param main
//   * @return
//   */
//  public String cloneSalesInvoice(MainView main) {
//    main.setViewType("newform");
//    return saveOrCloneSalesInvoice(main, "clone", 0);
//  }
//
//  /**
//   *
//   * @return
//   */
//  private boolean hasSalesInvoiceItem() {
//    boolean rvalue = false;
//    if (getSalesInvoiceItemList() != null) {
//      for (SalesInvoiceItem salesInvoiceItem : getSalesInvoiceItemList()) {
//        if (salesInvoiceItem.getTaxCodeId() != null && (salesInvoiceItem.getProductQty() != null || salesInvoiceItem.getProductQtyFree() != null) && salesInvoiceItem.getProdPieceSellingForced() != null && salesInvoiceItem.getValuePts() != null) {
//          rvalue = true;
//          break;
//        }
//      }
//    }
//    return rvalue;
//  }
//
//  /**
//   * Method to update Pro-Forma expiry date.
//   *
//   * @param main
//   * @return
//   */
//  public String saveProformaExpiryDays(MainView main) {
//    try {
//      getSalesInvoice().setProformaExpiringAt(SalesInvoiceUtil.addDaysToDate(getSalesInvoice().getProformaExpiringAt(), getProformaExpireDays()));
//      SalesInvoiceService.updateProformaExpiryDate(main, getSalesInvoice());
//      main.commit();
//    } catch (Throwable t) {
//      main.rollback(t, "error.save");
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  /**
//   * Save or clone.
//   *
//   * @param main
//   * @param key
//   * @param status
//   * @return
//   */
//  private String saveOrCloneSalesInvoice(MainView main, String key, int status) {
//    try {
//      uploadFiles(); //File upload
//      if (null != key) {
//        switch (key) {
//          case "save":
//
//            getSalesInvoice().setIsSalesInterstate(isInterstateSales() ? SystemConstants.INTERSTATE : SystemConstants.INTRASTATE);
//            if (getSalesInvoice().getSalesInvoiceType() == null) {
//              getSalesInvoice().setSalesInvoiceType(SystemConstants.SALES_INVOICE_TYPE_SALES_INVOICE);
//            }
//            /**
//             * Sets cash discount applicable field value. If Cash discount is applied, program will apply this value to Grand net amount. If Cash discount is taxable then program
//             * will apply cash discount on goods value.
//             */
//            getSalesInvoice().setCashDiscountTaxable(null);
//            if (isCashDiscountApplicable()) {
//              getSalesInvoice().setCashDiscountApplicable(SystemConstants.CASH_DISCOUNT_APPLICABLE);
//              if (isCashDiscountTaxable()) {
//                getSalesInvoice().setCashDiscountTaxable(SystemConstants.CASH_DISCOUNT_TAXABLE);
//              }
//            } else {
//              getSalesInvoice().setCashDiscountValue(null);
//            }
//
//            if (getSalesInvoice() != null && getSalesInvoice().getId() != null) {
//
//              /**
//               * Checking sales invoice is editable or not. Here program, verifying sales invoice last modified date. If Last modified date is greater than current sales invoice
//               * last modified date, then program will not allow update of sales invoice.
//               */
//              if (SalesInvoiceService.isSalesInvoiceEditable(main, getSalesInvoice())) {
//
//                /**
//                 * Processing sales invoice calculation.
//                 */
//                getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//
//                /**
//                 * Insert/Update Sales Invoice Item List.
//                 */
//                int isProductSold = SalesInvoiceItemService.insertOrUpdateSalesInvoiceItems(main, getSalesInvoice(), getSalesInvoiceItemList(), status, isProformaToInvoice(), getTaxCalculator());
//
//                if (isProductSold == SalesInvoiceUtil.REQUESTED_ITEMS_SOLD) {
//                  getSalesInvoice().setSalesInvoiceStatusId(new SalesInvoiceStatus(status));
//
//                  SalesInvoiceService.insertOrUpdate(main, getSalesInvoice());
//
//                  if (SystemConstants.CONFIRMED.equals(getSalesInvoice().getSalesInvoiceStatusId().getId())) {
//
//                    PlatformService.salesInvoicePlatformEntry(main, getSalesInvoice());
//
//                    LedgerExternalService.saveLedgerTaxCode(main, getSalesInvoice().getCompanyId());
//
//                    StockPreSaleService.releaseStockFromPreSale(main, getSalesInvoice());
//
//                    getTaxCalculator().saveSales(main, getSalesInvoice());
//                  }
//                } else if (isProductSold == SalesInvoiceUtil.REQUESTED_FEW_ITEMS_NOT_AVAILABLE) {
//                  main.error("error.product.not.available");
//                  selectSalesInvoiceForEdit(main);
//                  return null;
//                }
//
//              } else {
//                main.error("error.past.record.edit");
//                selectSalesInvoiceForEdit(main);
//                return null;
//              }
//              setProformaToInvoice(false);
//
//            } else {
//              getSalesInvoice().setSalesInvoiceStatusId(new SalesInvoiceStatus(status));
//              SalesInvoiceService.insertOrUpdate(main, getSalesInvoice());
//            }
//            break;
//          case "clone":
//            SalesInvoiceService.clone(main, getSalesInvoice());
//            break;
//        }
//        setSalesInvoiceItemList(null);
//        main.commit("success." + key);
//        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
//      }
//    } catch (Throwable t) {
//      main.rollback(t, "error." + key);
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  /**
//   *
//   * @param main
//   * @return
//   */
//  public String actionCancelSalesInvoice(MainView main) {
//    try {
//      long cancelled = SalesInvoiceService.cancelSalesInvoice(main, getSalesInvoice());
//      if (cancelled == 0) {
//        main.commit("success.save");
//      } else {
//        main.error("error.foreignkey.violates.constraint");
//      }
//    } catch (Throwable t) {
//      main.rollback(t);
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  /**
//   * Delete one or many SalesInvoice.
//   *
//   * @param main
//   * @return the page to display.
//   */
//  public String deleteSalesInvoice(MainView main) {
//    Integer id = null;
//    try {
//      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesInvoiceSelected)) {
//        for (SalesInvoice e : salesInvoiceSelected) {
//          id = e.getId();
//          SalesInvoiceService.deleteByPk(main, e);
//          main.em().commit();
//        }
////        SalesInvoiceService.deleteByPkArray(main, getSalesInvoiceSelected()); //many record delete from list
//        main.commit("success.delete");
//        salesInvoiceSelected = null;
//      } else {
//        SalesInvoiceService.deleteByPk(main, getSalesInvoice());  //individual record delete from list or edit form
//        main.commit("success.delete");
//        if ("editform".equals(main.getViewType())) {
//          main.setViewType(ViewTypes.newform);
//        }
//      }
//    } catch (Throwable t) {
//      AppUtil.referenceError(main, t, id);
//      main.rollback(t, "error.delete");
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  /**
//   *
//   * @param main
//   * @return
//   */
//  public String actionCancelProforma(MainView main) {
//    try {
//      SalesInvoiceService.releaseBlockedProductsFromStockPreSalse(main, getSalesInvoice());
//      SalesInvoiceService.updateSalesInvoiceStatus(main, getSalesInvoice(), SystemConstants.CANCELLED);
//      main.commit("success.proforma.cancelled");
//    } catch (Throwable t) {
//      main.rollback(t, "error.delete");
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  /**
//   *
//   * @param main
//   * @return
//   */
//  public String actionProformaToInvoice(MainView main) {
//    try {
//      setProformaToInvoice(true);
//    } catch (Throwable t) {
//      t.printStackTrace();
//    }
//    return null;
//  }
//
//  public List<ServiceCommodity> completeServiceDetail(String filter) {
//    return ScmLookupExtView.lookupServiceCommodity(getCompany(), filter);
//  }
//
//  public void serviceSelectEvent(SelectEvent event) {
//  }
//
//  /**
//   * SalesOrder autocomplete filter.
//   * <pre>
//   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//   * If your list is smaller in size and is cached you can use.
//   * <o:converter list="#{ScmLookupView.salesOrderAuto(null)}" converterId="omnifaces.ListConverter"  />
//   * Note:- ScmLookupView.salesOrderAuto(null) Should be implemented to return full values from cache if the filter is null
//   * </pre>
//   *
//   * @param filter
//   * @return
//   */
//  public List<SalesOrder> salesOrderAuto(String filter) {
//    return ScmLookupView.salesOrderAuto(filter);
//  }
//
//  /**
//   *
//   * @return
//   */
//  public List<CompanyAddress> lookUpCompanyAddress() {
//    List<CompanyAddress> companyAddressList = null;
//    companyAddressList = ScmLookupExtView.companyAddressByCompany(UserRuntimeView.instance().getCompany());
//    if (companyAddressList != null && !companyAddressList.isEmpty() && companyAddressList.size() == 1) {
//      getSalesInvoice().setCompanyAddressId(companyAddressList.get(0));
//    }
//
//    return companyAddressList;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public List<CustomerAddress> lookUpCustomerAddress() {
//    List<CustomerAddress> list = null;
//    if (getSalesInvoice().getCustomerId() != null) {
//      list = ScmLookupExtView.customerAddressByCustomer(getSalesInvoice().getCustomerId(),true);
//      if (list != null && !list.isEmpty() && list.size() == 1) {
//        getSalesInvoice().setCustomerAddressId(list.get(0));
//      }
//    }
//    return list;
//
//  }
//
//  public List<CustomerAddress> lookUpShippingAddress() {
//    return lookUpCustomerAddress();
//  }
//
//  /**
//   * SalesInvoiceStatus autocomplete filter.
//   * <pre>
//   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//   * If your list is smaller in size and is cached you can use.
//   * <o:converter list="#{ScmLookupView.salesInvoiceStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
//   * Note:- ScmLookupView.salesInvoiceStatusAuto(null) Should be implemented to return full values from cache if the filter is null
//   * </pre>
//   *
//   * @param filter
//   * @return
//   */
//  public List<SalesInvoiceStatus> salesInvoiceStatusAuto(String filter) {
//    return ScmLookupView.salesInvoiceStatusAuto(filter);
//  }
//
//  /**
//   * Customer autocomplete filter.
//   * <pre>
//   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
//   * If your list is smaller in size and is cached you can use.
//   * <o:converter list="#{ScmLookupView.customerAuto(null)}" converterId="omnifaces.ListConverter"  />
//   * Note:- ScmLookupView.customerAuto(null) Should be implemented to return full values from cache if the filter is null
//   * </pre>
//   *
//   * @param filter
//   * @return
//   */
//  public List<Customer> customerAuto(String filter) {
//    if (getSalesInvoice() != null && getSalesInvoice().getAccountGroupId() != null) {
//      return ScmLookupExtView.selectCustomerByAccountGroup(getSalesInvoice().getAccountGroupId(), filter);
//    }
//    return null;
//  }
//
//  /**
//   * Method to select products based on its account group.
//   *
//   * @param filter
//   * @return
//   */
//  public List<Product> completeProduct(String filter) {
//    List<Product> productList = null;
//    try (MainView main = (MainView) Jsf.getViewBean(AppConfig.mainView)) {
//      if (getSalesInvoice().getAccountGroupId() != null) {
//        productList = getTaxCalculator().selectProductForSales(main, getSalesInvoice(), filter);
//      }
//    } catch (Throwable t) {
//      t.printStackTrace();
//    }
//    return productList;
//  }
//
//  /**
//   * Method to select product detail based on its product.
//   *
//   * @param main
//   * @param salesInvoiceItem
//   * @return
//   */
//  public List<ProductDetailSales> selectProductDetailSales(MainView main, SalesInvoiceItem salesInvoiceItem) {
//    List<ProductDetailSales> productDetailSalesList = null;
//
//    try {
//      if (StringUtil.isEmpty(productDetailSalesList)) {
//        productDetailSalesList = salesInvoiceItem.getProductDetailSalesList();
//        if (salesInvoiceItem != null && salesInvoiceItem.getProduct() != null && salesInvoiceItem.getProductDetailSales() == null) {
//          if (getSalesInvoice().getAccountGroupId() != null && getSalesInvoice().getAccountGroupPriceListId() != null) {
//            productDetailSalesList = getTaxCalculator().selectProductDetailForSales(main, getSalesInvoice(), salesInvoiceItem);
//
////          for (ProductDetailSales pds : pdsList) {
////            if (getProductHashCode().containsKey(pds.getProductHash())) {
////              if (salesInvoiceItem.getId() != null && salesInvoiceItem.getProductHash().equals(pds.getProductHash())) {
////                productDetailSalesList.add(pds);
////              } else if (salesInvoiceItem.getProductDetailSales() != null && pds.getProductHash().equals(salesInvoiceItem.getProductDetailSales().getProductHash())) {
////                productDetailSalesList.add(pds);
////              }
////            } else {
////              productDetailSalesList.add(pds);
////            }
////          }
//            if (!productDetailSalesList.isEmpty() && salesInvoiceItem.getProductDetailSales() == null) {
//              salesInvoiceItem.setProductDetailSales(productDetailSalesList.get(0));
//              updateProductDetails(salesInvoiceItem);
//              salesInvoiceItem.setProductDetailSalesList(productDetailSalesList);
//            }
//          }
//        }
//      }
//    } catch (Throwable t) {
//      t.printStackTrace();
//    } finally {
//      main.close();
//    }
//    return productDetailSalesList;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public List<AccountGroup> accountGroupByAccountOrSalesOrder() {
//    List<AccountGroup> list = null;
//    if (getSalesInvoice().getSalesOrderId() != null && getSalesInvoice().getSalesOrderId().getId() != null) {
//      list = ScmLookupExtView.accountGroupBySalesOrder(getSalesInvoice().getSalesOrderId());
//    } else {
//      if (UserRuntimeView.instance().getAccountCurrent() != null) {
//        list = ScmLookupExtView.accountGroupByAccount(UserRuntimeView.instance().getAccountCurrent());
//      }
//    }
//    if (list != null && !list.isEmpty() && getSalesInvoice().getAccountGroupId() == null) {
//      getSalesInvoice().setAccountGroupId(list.get(0));
//      updateSalesInvoiceNumber();
//    }
//    return list;
//  }
//
//  /**
//   *
//   */
//  private void updateSalesInvoiceNumber() {
//    try (MainView main = (MainView) Jsf.getViewBean(AppConfig.mainView)) {
//      if (getSalesInvoice().getAccountGroupId() != null) {
//        getSalesInvoice().setInvoiceNo("DR-" + AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getSalesInvoice().getAccountGroupId(), PrefixTypeService.SALES_INVOICE_PREFIX_ID, true));
//      } else {
//        getSalesInvoice().setInvoiceNo(null);
//      }
//    } catch (Throwable t) {
//      t.printStackTrace();
//    }
//  }
//
//  /**
//   *
//   * @return
//   */
//  public List<AccountGroupPriceList> accountGroupPriceListByAccountGroup() {
//    List<AccountGroupPriceList> accountGroupPriceList = null;
//    if (getSalesInvoice().getAccountGroupId() != null) {
//      accountGroupPriceList = ScmLookupExtView.accountGroupPriceListByAccountGroup(getSalesInvoice().getAccountGroupId());
//      if (accountGroupPriceList != null && !accountGroupPriceList.isEmpty() && accountGroupPriceList.size() == 1 && getSalesInvoice().getAccountGroupPriceListId() == null) {
//        getSalesInvoice().setAccountGroupPriceListId(accountGroupPriceList.get(0));
//      }
//    }
//    return accountGroupPriceList;
//  }
//
//  /**
//   *
//   * @param main
//   * @param salesInvoiceType
//   * @return
//   */
//  public String newSalesInvoice(MainView main, int salesInvoiceType) {
//    try {
//      setSalesOrderReference(salesInvoiceType == 2);
//      getSalesInvoice().reset();
//      main.setViewType(ViewTypes.newform);
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  /**
//   *
//   * @param main
//   */
//  public void actionInsertOrUpdateSalesInvoiceItem(MainView main) {
//    boolean isNew = false;
//    try {
//      if (isValueMrpChanged()) {
//        main.error("error.mrp.changed");
//        return;
//      }
//
//      Integer rowIndex = Jsf.getParameterInt("rownumber");
//      SalesInvoiceItem salesInvoiceItem = getSalesInvoiceItemList().get(rowIndex);
//
//      if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getProdPieceSellingForced() != null && salesInvoiceItem.getProdPieceSellingForced() > 0) {
//        if (salesInvoiceItem.getId() == null) {
//          isNew = true;
//        }
//        salesInvoiceItem.setSalesInvoiceId(getSalesInvoice());
//
//        double invoiceDisc = salesInvoiceItem.getProductDetailSales().getInvoiceDiscountValue() == null ? 0 : salesInvoiceItem.getProductDetailSales().getInvoiceDiscountValue();
//        //double lineDisc = salesInvoiceItem.getProductDetailSales().getProductDiscountValue() == null ? 0 : salesInvoiceItem.getProductDetailSales().getProductDiscountValue();
//        double pts = salesInvoiceItem.getProductDetailSales().getPricelistPts() == null ? 0 : salesInvoiceItem.getProductDetailSales().getPricelistPts();
//        salesInvoiceItem.setInvoiceDiscountActual(MathUtil.roundOff(invoiceDisc, 2));
//        //salesInvoiceItem.setProductDiscountActual(MathUtil.roundOff(lineDisc, 2));
//        salesInvoiceItem.setValueProdPieceSelling(pts);
//        salesInvoiceItem.setSecToPriQuantityActual(salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
//        salesInvoiceItem.setTerToSecQuantityActual(salesInvoiceItem.getProductDetailSales().getTerToSecQuantity());
//        salesInvoiceItem.setProductHash(salesInvoiceItem.getProductDetailSales().getProductHash());
//
//        // Sets Free Scheme to sales invoice item.
//        if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() > 0) {
//          if (salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId() != null) {
//            salesInvoiceItem.setProductFreeQtyScheme(new ProductFreeQtyScheme(salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId()));
//          }
//        }
//
//        getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//
//        if (isProformaToInvoice()) {
//
//          if (isNew) {
//            salesInvoiceItem.setProformaItemStatus(SalesInvoiceUtil.PROFORMA_ITEM_NEW);
//          } else {
//            salesInvoiceItem = SalesInvoiceUtil.isProformaItemModified(salesInvoiceItem);
//          }
//          if (salesInvoiceItem.getProformaItemStatus() != null && salesInvoiceItem.getProformaItemStatus() > 0) {
//            getSalesInvoice().setProformaStatus(SalesInvoiceUtil.PROFORMA_MODIFIED);
//          }
//
//        } else {
//          SalesInvoiceService.insertOrUpdate(main, getSalesInvoice());
//          if (salesInvoiceItem.getTaxCodeId() != null && salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProdPieceSellingForced() != null && salesInvoiceItem.getValuePts() != null) {
//            SalesInvoiceItemService.insertOrUpdateSalesInvoiceItem(main, getSalesInvoice(), salesInvoiceItem, getTaxCalculator(), getSalesInvoiceItemList().get(getSalesInvoiceItemList().size() - 1).getSortOrder());
//            selectSalesInvoiceItemList(main, getSalesInvoice());
//          }
//        }
//
//        if (isNew) {
//          actionAddNewLineItem();
//        } else if (rowIndex == 0) {
//          actionAddNewLineItem();
//        }
//        getTaxCalculator().processSalesInvoiceCalculation(salesInvoice, salesInvoiceItemList, true);
//      }
//    } catch (Throwable t) {
//      main.rollback(t);
//    } finally {
//      main.close();
//    }
//
//  }
//
//  /**
//   * Method to delete sales invoice item.
//   *
//   * @param main
//   * @param salesInvoiceItem
//   */
//  public void actionDeleteSalesItem(MainView main, SalesInvoiceItem salesInvoiceItem) {
//
//    if (salesInvoiceItem != null && salesInvoiceItem.getId() != null && salesInvoiceItem.getId() > 0) {
//      try {
//        if (!StringUtil.isEmpty(salesInvoiceItem.getSalesInvoiceItemHashCode())) {
//          SalesInvoiceItemService.deleteBySalesInvoiceItemHashCode(main, salesInvoiceItem);
//          setValueMrpChanged(SalesInvoiceService.isProductValueMrpChanged(main, getSalesInvoice()));
//        }
//        //getProductHashCode().remove(salesInvoiceItem.getProductHash());
//        getSalesInvoiceItemList().remove(salesInvoiceItem);
//        getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//      } catch (Throwable t) {
//        main.rollback(t);
//      } finally {
//        main.close();
//      }
//    } else {
//      if (getSalesInvoiceItemList() != null && !getSalesInvoiceItemList().isEmpty()) {
//        if (salesInvoiceItem != null && salesInvoiceItem.getProductDetailSales() != null && salesInvoiceItem.getProductDetailSales().getProductHash() != null) {
//          // getProductHashCode().remove(salesInvoiceItem.getProductDetailSales().getProductHash());
//        }
//        getSalesInvoiceItemList().remove(salesInvoiceItem);
//      }
//    }
//    //if (!isProformaToInvoice()) {
//    //}
//    if (getSalesInvoiceItemList().isEmpty()) {
//      actionAddNewLineItem();
//    }
//    hasLineItems();
//  }
//
//  /**
//   *
//   * @param salesInvoiceItem
//   */
//  public void actionUndoDeleteSalesItem(SalesInvoiceItem salesInvoiceItem) {
//    if (salesInvoiceItem != null && salesInvoiceItem.getProformaItemStatus() != null) {
//      if (salesInvoiceItem.getProformaItemStatus().equals(SalesInvoiceUtil.PROFORMA_ITEM_DELETED)) {
//        salesInvoiceItem.setProformaItemStatus(null);
//      }
//    }
//  }
//
//  /**
//   * Method to add new sales invoice item to the list.
//   *
//   */
//  private void actionAddNewLineItem() {
//    if (getSalesInvoiceItemList() == null) {
//      salesInvoiceItemList = new ArrayList<>();
//    }
//    SalesInvoiceItem sit = new SalesInvoiceItem();
//    getSalesInvoiceItemList().add(0, sit);
//  }
//
//  /**
//   * Product selection event handler.
//   *
//   * @param salesInvoiceItem
//   */
//  public void updateCurrentRow(SalesInvoiceItem salesInvoiceItem) {
//    salesInvoiceItem.setProductDetailSales(null);
//    salesInvoiceItem.setProductDetailId(null);
//    salesInvoiceItem.setProdPieceSellingForced(null);
//    salesInvoiceItem.setValuePts(null);
//    salesInvoiceItem.setValuePtr(null);
//    salesInvoiceItem.setValueProdPieceSelling(null);
//    salesInvoiceItem.setProductDiscountActual(null);
//    salesInvoiceItem.setInvoiceDiscountActual(null);
//    salesInvoiceItem.setValueVat(null);
//    salesInvoiceItem.setValueSale(null);
//    salesInvoiceItem.setValueGoods(null);
//    salesInvoiceItem.setProductQty(null);
//    salesInvoiceItem.setProductQtyFree(null);
//    salesInvoiceItem.setProductDiscountValue(null);
//    salesInvoiceItem.setTertiaryQuantity(null);
//    salesInvoiceItem.setSecondaryQuantity(null);
//    hasLineItems();
//
//    //if (getSalesInvoiceItemList().size() == 1 && salesInvoiceItem.getId() == null) {
//    //getSalesInvoice().setCommodityId(salesInvoiceItem.getProduct().getCommodityId());
//    //getSalesInvoice().setProductTypeId(salesInvoiceItem.getProduct().getProductTypeId());
//    //}
//  }
//
//  /**
//   * Product Detail select event handler.
//   *
//   * @param salesInvoiceItem
//   */
//  public void updateProductDetails(SalesInvoiceItem salesInvoiceItem) {
//    //ProductDetailSales pds = (ProductDetailSales) salesInvoiceItem.getProductDetailSales();
//    //StringBuilder keys = new StringBuilder();
//    //boolean keyExist;
//    try (MainView main = (MainView) Jsf.getViewBean(AppConfig.mainView)) {
//
////      getProductHashCode().put(pds.getProductHash(), pds.getProductDetailId());
////      for (String key : getProductHashCode().keySet()) {
////        keyExist = false;
////        for (SalesInvoiceItem sii : getSalesInvoiceItemList()) {
////          if (sii.getProductDetailSales() != null && key.equals(sii.getProductDetailSales().getProductHash())) {
////            keyExist = true;
////          } else if (salesInvoiceItem.getId() != null) {
////            if (key.equals(salesInvoiceItem.getProductHash())) {
////              keyExist = true;
////            }
////          }
////        }
////        if (!keyExist) {
////          if (keys.length() == 0) {
////            keys.append(key);
////          } else {
////            keys.append(",").append(key);
////          }
////        }
////      }
////      if (keys.length() > 0) {
////        for (String key : keys.toString().split(",")) {
////          getProductHashCode().remove(key);
////        }
////      }
//      getTaxCalculator().updateSalesInvoiceItem(main, salesInvoiceItem);
//
//      setCurrentAvailableQty(salesInvoiceItem, SalesInvoiceUtil.PRODUCT_EMPTY_QTY);
//
//      setProductInvoiceDetailList(SalesInvoiceService.selectProductRateHistory(main, getSalesInvoice(), salesInvoiceItem));
//      if (!StringUtil.isEmpty(getProductInvoiceDetailList())) {
//        Jsf.addCallbackParam("productCount", getProductInvoiceDetailList().size());
//      } else {
//        Jsf.addCallbackParam("productCount", 0);
//      }
//
//    } catch (Throwable t) {
//      t.printStackTrace();
//    }
//  }
//
//  public void taxCodeSelectEvent(SalesInvoiceItem salesInvoiceItem) {
//    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//  }
//
//  /**
//   *
//   * @param salesInvoiceItem
//   * @param fieldType
//   */
//  public void setCurrentAvailableQty(SalesInvoiceItem salesInvoiceItem, int fieldType) {
//    int availableQty = 0;
//    Integer productFreeQtyAvailable;
//    Integer productQtyAvailable;
//    Integer requestedProductFreeQty;
//    Integer requestedProductQty;
//    Integer possibleProductFreeQty;
//    Integer possibleProductQty;
//    try {
//      if (salesInvoiceItem != null && salesInvoiceItem.getProductDetailSales() != null) {
//        productQtyAvailable = salesInvoiceItem.getProductDetailSales().getQuantityAvailable() == null ? 0 : salesInvoiceItem.getProductDetailSales().getQuantityAvailable();
//        productFreeQtyAvailable = salesInvoiceItem.getProductDetailSales().getQuantityFreeAvailable() == null ? 0 : salesInvoiceItem.getProductDetailSales().getQuantityFreeAvailable();
//        availableQty = (productQtyAvailable + productFreeQtyAvailable);
//        requestedProductQty = salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
//        requestedProductFreeQty = salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree();
//        switch (fieldType) {
//          case SalesInvoiceUtil.PRODUCT_PRIMARY_QTY:
//            possibleProductFreeQty = availableQty - requestedProductQty;
//            salesInvoiceItem.setCurrentAvailableFreeQty(possibleProductFreeQty);
//            break;
//          case SalesInvoiceUtil.PRODUCT_FREE_QTY:
//            possibleProductQty = availableQty - requestedProductFreeQty;
//            salesInvoiceItem.setCurrentAvailableQty(possibleProductQty);
//            break;
//          default:
//            salesInvoiceItem.setCurrentAvailableFreeQty(availableQty);
//            salesInvoiceItem.setCurrentAvailableQty(availableQty);
//            break;
//        }
//      }
//    } catch (Throwable t) {
//      t.printStackTrace();
//    }
//  }
//
//  /**
//   *
//   * @param salesInvoiceItem
//   */
//  public void updateProductFreeQtyScheme(SalesInvoiceItem salesInvoiceItem) {
//    if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() == 0) {
//      salesInvoiceItem.setProductFreeQtyScheme(null);
//    }
//    salesInvoiceItem.setSchemeDiscountValue(null);
//    salesInvoiceItem.setSchemeDiscountPercentage(null);
//    setCurrentAvailableQty(salesInvoiceItem, SalesInvoiceUtil.PRODUCT_FREE_QTY);
//    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//  }
//
//  /**
//   *
//   * @param main
//   * @param salesInvoiceItem
//   * @param qType
//   */
//  public void updateProductSalesValues(MainView main, SalesInvoiceItem salesInvoiceItem, int qType) {
//
//    try {
//      if (salesInvoiceItem != null) {
//        if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getValueProdPieceSelling() != null) {
//
//          if (salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId() != null) {
//            int freeQty = SalesInvoiceService.selectProductFreeQuantity(main, salesInvoiceItem);
//            if (freeQty > 0) {
//              salesInvoiceItem.setSuggestedFreeQty(freeQty);
//              if (qType == SalesInvoiceUtil.PRODUCT_PRIMARY_QTY || qType == SalesInvoiceUtil.PRODUCT_SECONDARY_QTY || qType == SalesInvoiceUtil.PRODUCT_TERTIARY_QTY) {
//                salesInvoiceItem.setProductQtyFree(freeQty);
//              }
//              ProductFreeQtyScheme productFreeQtyScheme = new ProductFreeQtyScheme();
//              productFreeQtyScheme.setId(salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId());
//              salesInvoiceItem.setProductFreeQtyScheme(productFreeQtyScheme);
//            }
//          }
//
//          if (qType == SalesInvoiceUtil.PRODUCT_PRIMARY_QTY) {
//            double qty = salesInvoiceItem.getProductQty();
//            if (salesInvoiceItem.getProductDetailSales().getSecondaryPackId() != null) {
//              salesInvoiceItem.setSecondaryQuantity(qty / salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
//            }
//            if (salesInvoiceItem.getProductDetailSales().getTertiaryPackId() != null) {
//              salesInvoiceItem.setTertiaryQuantity(salesInvoiceItem.getSecondaryQuantity() / salesInvoiceItem.getProductDetailSales().getTerToSecQuantity());
//            }
//          }
//
//          // Finds Possible free Quantity
//          salesInvoiceItem.setPossibleFreeQuantity((salesInvoiceItem.getProductDetailSales().getQuantityAvailable() + salesInvoiceItem.getProductDetailSales().getQuantityFreeAvailable()) - salesInvoiceItem.getProductQty());
//
//          if (salesInvoiceItem.getProductDetailSales().getFreeQtySchemeId() != null) {
//            if (salesInvoiceItem.getProductQtyFree() > salesInvoiceItem.getPossibleFreeQuantity()) {
//              salesInvoiceItem.setProductQtyFree(salesInvoiceItem.getPossibleFreeQuantity());
//            }
//          }
//
//          //Derived Scheme Discount
//          if (salesInvoiceItem.getProductDetailSales() != null && salesInvoiceItem.getProductDetailSales().getSchemeDiscountPer() != null && salesInvoiceItem.getProductDetailSales().getSchemeDiscountPer() > 0) {
//            if (salesInvoiceItem.getProductQty() != null) {
//              if (salesInvoiceItem.getSchemeDiscountPercentage() == null) {
//                salesInvoiceItem.setSchemeDiscountPercentage(salesInvoiceItem.getProductDetailSales().getSchemeDiscountPer());
//              }
//              double sDisc = salesInvoiceItem.getProdPieceSellingForced() * salesInvoiceItem.getSchemeDiscountPercentage() / 100;
//              salesInvoiceItem.setSchemeDiscountValue(sDisc * salesInvoiceItem.getProductQty());
//            } else {
//              if (salesInvoiceItem.getProdPieceSellingForced() != null) {
//                salesInvoiceItem.setSchemeDiscountValue(salesInvoiceItem.getProdPieceSellingForced() * salesInvoiceItem.getSchemeDiscountPercentage() / 100);
//              }
//            }
//          }
//
//          //Derived Product Discount
//          if (salesInvoiceItem.getProductDetailSales() != null && salesInvoiceItem.getProductDetailSales().getProductDiscountPer() != null && salesInvoiceItem.getProductDetailSales().getProductDiscountPer() > 0) {
//            if (salesInvoiceItem.getProductQty() != null) {
//              if (salesInvoiceItem.getProductDiscountPercentage() == null) {
//                salesInvoiceItem.setProductDiscountPercentage(salesInvoiceItem.getProductDetailSales().getProductDiscountPer());
//              }
//              double pDisc = salesInvoiceItem.getProdPieceSellingForced() * salesInvoiceItem.getProductDiscountPercentage() / 100;
//              salesInvoiceItem.setProductDiscountValue(pDisc * salesInvoiceItem.getProductQty());
//            } else {
//              if (salesInvoiceItem.getProdPieceSellingForced() != null) {
//                salesInvoiceItem.setProductDiscountValue(salesInvoiceItem.getProdPieceSellingForced() * salesInvoiceItem.getProductDiscountPercentage() / 100);
//              }
//
//            }
//          }
//
//          getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//        } else {
//          salesInvoiceItem.setValueSale(null);
//          getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//        }
//        setCurrentAvailableQty(salesInvoiceItem, SalesInvoiceUtil.PRODUCT_PRIMARY_QTY);
//      }
//    } catch (Throwable t) {
//      main.rollback(t);
//    } finally {
//      main.close();
//    }
//  }
//
//  /**
//   *
//   * @param main
//   * @param salesInvoiceItem
//   * @param qtyType
//   */
//  public void updateProductQuantity(MainView main, SalesInvoiceItem salesInvoiceItem, int qtyType) {
//    Double tQty;
//    Double sQty;
//    Double pQty;
//    try {
//      if (salesInvoiceItem.getProductDetailSales() != null) {
//        if (salesInvoiceItem.getProductDetailSales().getTertiaryPackId() != null && qtyType == SalesInvoiceUtil.PRODUCT_TERTIARY_QTY) {
//          tQty = salesInvoiceItem.getTertiaryQuantity();
//          if (tQty != null) {
//            salesInvoiceItem.setSecondaryQuantity(tQty * salesInvoiceItem.getProductDetailSales().getTerToSecQuantity());
//            pQty = (salesInvoiceItem.getSecondaryQuantity() * salesInvoiceItem.getProductDetailSales().getSecToPriQuantity());
//            salesInvoiceItem.setProductQty(pQty.intValue());
//          } else {
//            salesInvoiceItem.setSecondaryQuantity(null);
//            salesInvoiceItem.setProductQty(null);
//          }
//          updateProductSalesValues(main, salesInvoiceItem, qtyType);
//        } else if (salesInvoiceItem.getProductDetailSales().getSecToPriQuantity() != null && qtyType == SalesInvoiceUtil.PRODUCT_SECONDARY_QTY) {
//          sQty = salesInvoiceItem.getSecondaryQuantity();
//          if (sQty != null) {
//            if (salesInvoiceItem.getProductDetailSales().getTertiaryPackId() != null) {
//              tQty = salesInvoiceItem.getSecondaryQuantity() / salesInvoiceItem.getProductDetailSales().getTerToSecQuantity();
//              salesInvoiceItem.setTertiaryQuantity(tQty);
//            }
//            pQty = sQty * salesInvoiceItem.getProductDetailSales().getSecToPriQuantity();
//            salesInvoiceItem.setProductQty(pQty.intValue());
//          } else {
//            salesInvoiceItem.setTertiaryQuantity(null);
//            salesInvoiceItem.setProductQty(null);
//          }
//          updateProductSalesValues(main, salesInvoiceItem, qtyType);
//        }
//      }
//    } catch (Throwable t) {
//      main.rollback(t);
//    } finally {
//      main.close();
//    }
//
//  }
//
//  /**
//   *
//   */
//  public void updateSalesInvoiceAmount() {
//    if (getSalesInvoice().getInvoiceAmount() != null && getSalesInvoice().getInvoiceAmount() > 0) {
//      getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//    }
//  }
//
//  /**
//   * Customer Auto complete event handler.
//   *
//   * @param event
//   */
//  public void customerSelectedEvent(SelectEvent event) {
//    Customer cust = (Customer) event.getObject();
//    if (cust != null) {
//      try (MainView main = (MainView) Jsf.getViewBean(AppConfig.mainView)) {
//        SalesAccount saccount = SalesAccountService.selectSalesAccountByCustomerAccountGroup(main, cust, getAccountGroup());
//        if (saccount != null) {
//          getSalesInvoice().setAccountGroupPriceListId(saccount.getAccountGroupPriceListId());
//          getSalesInvoice().setSalesCreditDays(saccount.getSalesCreditDays());
//          setSalesBusinessModes(main);
//          setCustomerOutstandingValue(selectCustomerOutstanding(main, cust, getAccountGroup()));
//        }
//      }
//    } else {
//      getSalesInvoice().setAccountGroupPriceListId(null);
//      getSalesInvoice().setSalesCreditDays(null);
//      setSalesAccount(null);
//    }
//  }
//
//  private Double selectCustomerOutstanding(MainView main, Customer customer, AccountGroup accountGroup) {
//    Double outstanding = null;
//    outstanding = TradeOutstandingService.selectCustomerOutstandingValue(main, customer, accountGroup);
//    return outstanding;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isMedicineCommodity() {
//    return (getSalesInvoice() != null && getSalesInvoice().getCommodityId() != null && getSalesInvoice().getCommodityId().getId().equals(SystemConstants.MEDICINE_COMMODITY_ID));
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isDraft() {
//    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceStatusId() != null) {
//      return (getSalesInvoice().getSalesInvoiceStatusId().getId().equals(SystemConstants.DRAFT));
//    }
//    return false;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isTaxCodeModified() {
//    if (getSalesInvoice() != null && getSalesInvoice().getIsTaxCodeModified() != null) {
//      return (SystemConstants.TAX_CODE_MODIFIED.equals(getSalesInvoice().getIsTaxCodeModified()));
//    }
//    return false;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isProforma() {
//    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceType() != null) {
//      return (getSalesInvoice().getSalesInvoiceType().equals(SystemConstants.SALES_INVOICE_TYPE_PROFORMA));
//    }
//    return false;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isConfirmed() {
//    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceStatusId() != null) {
//      return (getSalesInvoice().getSalesInvoiceStatusId().getId().equals(SystemConstants.CONFIRMED));
//    }
//    return false;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isConfirmedAndPacked() {
//    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceStatusId() != null) {
//      return (getSalesInvoice().getSalesInvoiceStatusId().getId().equals(SystemConstants.CONFIRMED_AND_PACKED));
//    }
//    return false;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isCancelled() {
//    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceStatusId() != null) {
//      return (getSalesInvoice().getSalesInvoiceStatusId().getId().equals(SystemConstants.CANCELLED));
//    }
//    return false;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isExpired() {
//    if (getSalesInvoice() != null && getSalesInvoice().getSalesInvoiceStatusId() != null) {
//      return (getSalesInvoice().getSalesInvoiceStatusId().getId().equals(SystemConstants.EXPIRED));
//    }
//    return false;
//  }
//
//  /**
//   *
//   */
//  public void validateSalesInvoiceIsEditable() {
//    salesInvoiceEditable = false;
//    if (getSalesInvoice() != null && getSalesInvoice().getId() == null) {
//      salesInvoiceEditable = true;
//    } else if (isDraft()) {
//      salesInvoiceEditable = true;
//    } else if (isProformaToInvoice()) {
//      salesInvoiceEditable = true;
//    }
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isSalesInvoiceEditable() {
//    return salesInvoiceEditable;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isProformaEditable() {
//    if (isProformaToInvoice()) {
//      return false;
//    } else if (isProforma() && !isCancelled() && !isExpired()) {
//      return true;
//    }
//    return false;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public List<VendorContact> lookupVendorSalesAgentPersonProfile() {
//    List<VendorContact> list = null;
//    if (getSalesInvoice() != null && getSalesInvoice().getAccountGroupId() != null) {
//      list = (List<VendorContact>) ScmLookupExtView.selectVendorContactByAccountGroup(getSalesInvoice().getAccountGroupId());
//    }
//    return list;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public List<UserProfile> lookupSalesAgentUserProfile() {
//    List<UserProfile> list = null;
//    if (getSalesInvoice() != null && getSalesInvoice().getAccountGroupId() != null) {
//      list = (List<UserProfile>) ScmLookupExtView.selectCompanySalesAgentUserProfile(getCompany(), getSalesInvoice().getAccountGroupId());
//    }
//    return list;
//  }
//
//  public void print(MainView main, String key, String printMode, String freeType) {
//    if (printMode.equals(SystemConstants.PRINT_PORTRAIT)) {
//      getSalesInvoice().setPrintMode(SystemConstants.PRINT_PORTRAIT);
//      if (SystemConstants.PDF_ITEXT.equals(key)) {
//        if (freeType.equals(SystemConstants.TYPE_I)) {
//          getSalesInvoice().setPrintType(SystemConstants.TYPE_I);
//        } else if (freeType.equals(SystemConstants.TYPE_II)) {
//          getSalesInvoice().setPrintType(SystemConstants.TYPE_II);
//        } else if (freeType.equals(SystemConstants.SAMPLE)) {
//          getSalesInvoice().setPrintType(SystemConstants.SAMPLE);
//        }
//        Jsf.popupForm(getTaxCalculator().getSalesInvoicePrintIText(), getSalesInvoice());
//      } else if (SystemConstants.PDF_KENDO.equals(key)) {
//        Jsf.popupForm(getTaxCalculator().getSalesInvoicePortrait(), getSalesInvoice());
//      }
//    } else if (printMode.equals(SystemConstants.PRINT_LANDSCAPE)) {
//      getSalesInvoice().setPrintType("");
//      CompanySettings settings = UserRuntimeView.instance().getCompany().getCompanySettings();
//      if (settings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
//        getSalesInvoice().setPrintMode(SystemConstants.PRINT_SINGLE_LINE);
//      } else {
//        getSalesInvoice().setPrintMode(SystemConstants.PRINT_MULTIPLE_LINE);
//      }
//      if (SystemConstants.PDF_ITEXT.equals(key)) {
//        Jsf.popupForm(getTaxCalculator().getSalesInvoicePrintIText(), getSalesInvoice());
//      } else if (SystemConstants.PDF_KENDO.equals(key)) {
//        Jsf.popupForm(getTaxCalculator().getSalesInvoiceLandscapeMultipleForm(), getSalesInvoice());
//      }
//    }
//  }
//
//  /**
//   *
//   */
//  public void dialogClose() {
//    Jsf.closePopup(accountingTransactionDetailItem);
//  }
//
//  /**
//   *
//   */
//  public void actionApplyForcedRoundOffValue() {
//    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//  }
//
//  /**
//   * Applying Cash discounts
//   */
//  public void actionApplyCashDiscount() {
//    if (isCashDiscountApplicable()) {
//      getSalesInvoice().setCashDiscountApplicable(SystemRuntimeConfig.CASH_DISCOUNT_APPLICABLE);
//    } else {
//      getSalesInvoice().setCashDiscountValue(null);
//      getSalesInvoice().setCashDiscountApplicable(null);
//    }
//    if (isCashDiscountTaxable()) {
//      getSalesInvoice().setCashDiscountTaxable(SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE);
//    } else {
//      getSalesInvoice().setCashDiscountTaxable(null);
//    }
//    if (getSalesInvoiceItemList() != null) {
//      getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//    }
//  }
//
//  /**
//   *
//   * @param sitem
//   * @return
//   */
//  public boolean schemeDiscountApplied(SalesInvoiceItem sitem) {
//    return (sitem != null && sitem.getProductQtyFree() == null && sitem.getSchemeDiscountValue() != null);
//  }
//
//  /**
//   *
//   * @param sitem
//   * @return
//   */
//  public boolean schemeApplicable(SalesInvoiceItem sitem) {
//    return (sitem != null && sitem.getQuantityOrFree() != null && SystemConstants.FREE_SCHEME.equals(sitem.getQuantityOrFree()));
//  }
//
//  /**
//   * Product scheme discount value event handler.
//   *
//   * @param salesInvoiceItem
//   */
//  public void schemeDiscountValueChangeEventHandler(SalesInvoiceItem salesInvoiceItem) {
//    salesInvoiceItem.setSchemeDiscountPercentage(null);
//    salesInvoiceItem.setSchemeDiscountDerived(null);
//    salesInvoiceItem.setIsSchemeDiscountInPercentage(null);
//    if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getSchemeDiscountValue() != null) {
//      salesInvoiceItem.setIsSchemeDiscountInPercentage(SystemConstants.DISCOUNT_IN_VALUE);
//    }
//    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//
//  }
//
//  /**
//   * Product Scheme discount percentage event handler.
//   *
//   * @param salesInvoiceItem
//   */
//  public void schemeDiscountPercChangeEventHandler(SalesInvoiceItem salesInvoiceItem) {
//    salesInvoiceItem.setSchemeDiscountValue(null);
//    salesInvoiceItem.setSchemeDiscountDerived(null);
//    salesInvoiceItem.setIsSchemeDiscountInPercentage(null);
//    if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getSchemeDiscountPercentage() != null) {
//      salesInvoiceItem.setIsSchemeDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
//    }
//    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//  }
//
//  /**
//   * Product discount value change event handler
//   *
//   * @param salesInvoiceItem
//   */
//  public void productDiscountValueChangeEventHandler(SalesInvoiceItem salesInvoiceItem) {
//    salesInvoiceItem.setProductDiscountPercentage(null);
//    salesInvoiceItem.setProductDiscountDerived(null);
//    salesInvoiceItem.setIsProductDiscountInPercentage(null);
//    if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getProductDiscountValue() != null) {
//      salesInvoiceItem.setIsProductDiscountInPercentage(SystemConstants.DISCOUNT_IN_VALUE);
//    }
//    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//  }
//
//  /**
//   * Product Scheme discount percentage event handler.
//   *
//   * @param salesInvoiceItem
//   */
//  public void productDiscountPercChangeEventHandler(SalesInvoiceItem salesInvoiceItem) {
//    salesInvoiceItem.setProductDiscountValue(null);
//    salesInvoiceItem.setProductDiscountDerived(null);
//    salesInvoiceItem.setIsProductDiscountInPercentage(null);
//    if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0 && salesInvoiceItem.getProductDiscountPercentage() != null) {
//      salesInvoiceItem.setIsProductDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
//    }
//    getTaxCalculator().processSalesInvoiceCalculation(getSalesInvoice(), getSalesInvoiceItemList(), true);
//  }
//
//  /**
//   *
//   * @param main
//   */
//  public void actionResetInvoiceEntry(MainView main) {
//    try {
//      SalesInvoiceService.actionResetSalesInvoiceToDraft(main, getSalesInvoice());
//      getSalesInvoice().setSalesInvoiceStatusId(new SalesInvoiceStatus(SystemConstants.DRAFT));
//      //saveSalesInvoice(main, SystemConstants.DRAFT);
//    } catch (Throwable t) {
//      main.rollback(t, "error.select");
//    } finally {
//      main.close();
//    }
//  }
//
//  /**
//   *
//   * @param main
//   */
//  public void actionResetAllInvoiceEntry(MainView main) {
//    try {
//      SalesInvoiceService.resetAllSalesInvoiceToDraft(main, getAccountGroup());
//    } catch (Throwable t) {
//      main.rollback(t, "error.select");
//    } finally {
//      main.close();
//    }
//  }
//
//  /**
//   *
//   * @param main
//   * @return
//   */
//  public String actionUpdatePacking(MainView main) {
//    try {
//      SalesInvoiceService.updateSalesInvoicePacking(main, getSalesInvoice());
//    } catch (Throwable t) {
//      main.rollback(t);
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  /**
//   *
//   * @param main
//   * @return
//   */
//  public String addToExistingConsignment(MainView main) {
//    if (consignment != null) {
//      try {
//        ConsignmentCommodityService.insertArraySales(main, new SalesInvoice[]{getSalesInvoice()}, getConsignment());
//        main.close();
//        Jsf.popupForm(ConsignmentService.SALES_INVOICE_CONSIGNMENT_POPUP, null, consignment.getId());
//      } catch (Throwable t) {
//        main.rollback(t);
//      } finally {
//        main.close();
//      }
//    }
//    return null;
//  }
//
//  /**
//   *
//   * @param main
//   * @return
//   */
//  public String createConsignment(MainView main) {
//    Jsf.popupForm(ConsignmentService.SALES_INVOICE_CONSIGNMENT_POPUP, getSalesInvoice());
//    return null;
//  }
//
//  /**
//   *
//   * @param main
//   * @return
//   */
//  public String viewConsignment(MainView main) {
//    if (consignment != null) {
//      Jsf.popupForm(ConsignmentService.SALES_INVOICE_CONSIGNMENT_POPUP, null, consignment.getId());
//    }
//    return null;
//  }
//
//  /**
//   *
//   * @return
//   */
//  public boolean isProductTypeEditable() {
//    return !(!StringUtil.isEmpty(getSalesInvoiceItemList()) && getSalesInvoiceItemList().get(0).getProduct() != null);
//  }
//
//  /**
//   *
//   * @return
//   */
//  public List<ProductType> lookupProductType() {
//    return ScmLookupExtView.lookupProductType();
//  }
//
//  /**
//   *
//   * @return
//   */
//  private List<TaxCode> lookupGstTaxCode() {
//    if (taxCodeList == null) {
//      taxCodeList = ScmLookupExtView.lookupGstTaxCode(UserRuntimeView.instance().getCompany());
//    }
//    return taxCodeList;
//  }
//
//  private void hasLineItems() {
//    setLineItemExist(false);
//    if (!StringUtil.isEmpty(getSalesInvoiceItemList())) {
//      if (getSalesInvoiceItemList().size() == 1) {
//        if (getSalesInvoiceItemList().get(0).getProduct() != null) {
//          setLineItemExist(true);
//        }
//      } else {
//        setLineItemExist(true);
//      }
//    }
//  }
}
