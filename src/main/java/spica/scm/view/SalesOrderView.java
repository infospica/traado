/*
 * @(#)SalesOrderView.java	1.0 Tue May 17 15:23:05 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesOrder;
import spica.scm.service.SalesOrderService;
import spica.scm.domain.Customer;
import spica.scm.domain.Product;
import spica.scm.domain.ProductPricelist;
import spica.scm.domain.SalesAccount;
import spica.scm.domain.SalesOrderItem;
import spica.scm.domain.SalesOrderStatus;
import spica.scm.domain.SalesReq;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.PrefixTypeService;
import spica.scm.service.ProductPricelistService;
import spica.scm.service.ProductService;
import spica.scm.service.SalesAccountService;
import spica.scm.service.SalesInvoiceService;
import spica.scm.service.SalesOrderItemService;
import spica.scm.service.SalesReqService;
import spica.scm.tax.TaxCalculator;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * SalesOrderView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 17 15:23:05 IST 2016
 */
@Named(value = "salesOrderView")
@ViewScoped
public class SalesOrderView implements Serializable {

  private transient SalesOrder salesOrder;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesOrder> salesOrderLazyModel; 	//For lazy loading datatable.
  private transient SalesOrder[] salesOrderSelected;	 //Selected Domain Array
  private transient SalesReq[] salesReqSelected;	 //Selected Domain Array

  private transient List<SalesOrderItem> salesOrderItemList;
  private transient List<SalesReq> salesReqList;
  private transient String taxCode;
  private transient Account account;
  private transient AccountGroup accountGroup = null;
  private transient Integer currentProductId = null;
  private transient HashMap<Integer, String> salesRequestedProduct;
  private transient boolean salesReqAvailable = false;
  private transient boolean salesRequisitionSelected = false;
  private transient boolean orderProcessed = false;
  private transient Integer salesOrderFilter;
  private transient TaxCalculator taxCalculator;

  private transient Integer productQuantityCount;

  @PostConstruct
  public void init() {
    Integer invoiceId = (Integer) Jsf.popupParentValue(Integer.class);
    if (invoiceId != null) {
      getSalesOrder().setId(invoiceId);
    }
  }

  /**
   * Default Constructor.
   */
  public SalesOrderView() {
    super();
  }

  /**
   * Return SalesOrder.
   *
   * @return SalesOrder.
   */
  public SalesOrder getSalesOrder() {
    if (salesOrder == null) {
      salesOrder = new SalesOrder();
    }
    if (salesOrder.getCompanyId() == null) {
      salesOrder.setCompanyId(UserRuntimeView.instance().getCompany());
      salesOrder.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    return salesOrder;
  }

  /**
   * Set SalesOrder.
   *
   * @param salesOrder.
   */
  public void setSalesOrder(SalesOrder salesOrder) {
    this.salesOrder = salesOrder;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public Account getAccount() {
    if (account == null) {
      if (UserRuntimeView.instance().getAccount() != null) {
        setAccount(UserRuntimeView.instance().getAccount());
      }
    }
    return account;
  }

  public Integer getCurrentProductId() {
    return currentProductId;
  }

  public void setCurrentProductId(Integer currentProductId) {
    this.currentProductId = currentProductId;
  }

  public HashMap<Integer, String> getSalesRequestedProduct() {
    if (salesRequestedProduct == null) {
      salesRequestedProduct = new HashMap<>();
    }
    return salesRequestedProduct;
  }

  public void setSalesRequestedProduct(HashMap<Integer, String> salesRequestedProduct) {
    this.salesRequestedProduct = salesRequestedProduct;
  }

  public AccountGroup getAccountGroup() {
    if (UserRuntimeView.instance().getAccountGroup() != null) {
      this.accountGroup = UserRuntimeView.instance().getAccountGroup();
    }
    return this.accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public List<SalesReq> getSalesReqList() {
    if (salesReqList == null) {
      salesReqList = new ArrayList<>();
    }
    return salesReqList;
  }

  public void setSalesReqList(List<SalesReq> salesReqList) {
    this.salesReqList = salesReqList;
  }

  public SalesReq[] getSalesReqSelected() {
    return salesReqSelected;
  }

  public void setSalesReqSelected(SalesReq[] salesReqSelected) {
    this.salesReqSelected = salesReqSelected;
  }

  public boolean isSalesReqAvailable() {
    return salesReqAvailable;
  }

  public void setSalesReqAvailable(boolean salesReqAvailable) {
    this.salesReqAvailable = salesReqAvailable;
  }

  public boolean isSalesRequisitionSelected() {
    return salesRequisitionSelected;
  }

  public void setSalesRequisitionSelected(boolean salesRequisitionSelected) {
    this.salesRequisitionSelected = salesRequisitionSelected;
  }

  public String getTaxCode() {
    return taxCode;
  }

  public void setTaxCode(String taxCode) {
    this.taxCode = taxCode;
  }

  public List<SalesOrderItem> getSalesOrderItemList() {
    if (salesOrderItemList == null) {
      salesOrderItemList = new ArrayList<>();
    }
    return salesOrderItemList;
  }

  public void setSalesOrderItemList(List<SalesOrderItem> salesOrderItemList) {
    this.salesOrderItemList = salesOrderItemList;
  }

  public boolean isOrderProcessed() {
    return orderProcessed;
  }

  public void setOrderProcessed(boolean orderProcessed) {
    this.orderProcessed = orderProcessed;
  }

  public Integer getSalesOrderFilter() {
    return salesOrderFilter;
  }

  public void setSalesOrderFilter(Integer salesOrderFilter) {
    this.salesOrderFilter = salesOrderFilter;
  }

  public boolean isDraft() {
    if (getSalesOrder() != null && getSalesOrder().getSalesOrderStatusId() != null) {
      return (getSalesOrder().getSalesOrderStatusId().getId().equals(SystemConstants.DRAFT));
    }
    return false;
  }

  public boolean isConfirmed() {
    if (getSalesOrder() != null && getSalesOrder().getSalesOrderStatusId() != null) {
      return (getSalesOrder().getSalesOrderStatusId().getId().equals(SystemConstants.CONFIRMED));
    }
    return false;
  }

  public Integer getProductQuantityCount() {
    return productQuantityCount;
  }

  public void setProductQuantityCount(Integer productQuantityCount) {
    this.productQuantityCount = productQuantityCount;
  }

  /**
   * Method to check the sales order status processed or not.
   *
   * @return
   */
  public boolean isInvoiced() {
    if (getSalesOrder() != null && getSalesOrder().getSalesOrderStatusId() != null) {
      return (getSalesOrder().getSalesOrderStatusId().getId().equals(SystemConstants.CONFIRMED_AND_INVOICED));
    }
    return false;
  }

  /**
   * Method to check the sales order status processed or not.
   *
   * @return
   */
  public boolean isPartPending() {
    if (getSalesOrder() != null && getSalesOrder().getSalesOrderStatusId() != null) {
      return (getSalesOrder().getSalesOrderStatusId().getId().equals(SystemConstants.CONFIRMED_AND_PART_PENDING));
    }
    return false;
  }

  public boolean isMergeable() {
    if (isConfirmed() || isPartPending()) {
      return !(isOrderProcessed());
    }
    return false;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesOrder(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else if (UserRuntimeView.instance().getAccountGroup() == null) {
            main.error("account.group.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else {
            getSalesOrder().resetSalesOrder();
            getSalesOrder().setAccountGroupId(getAccountGroup());
            setSalesOrderItemList(null);
            getSalesOrder().setSalesOrderDate(new Date());
            updateSalesOrderNumber(main);
            if (getSalesReqSelected() != null && getSalesReqSelected().length > 0) {
              setSalesOrderItemList(populateSalesOrderItem(SalesOrderService.selectSalesOrderItemBySalesReqArray(main, salesReqSelected)));
              setSalesRequisitionSelected(true);
            }
          }
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSalesReqSelected(null);
          setSalesOrder((SalesOrder) SalesOrderService.selectByPk(main, getSalesOrder()));
          setSalesOrderItemList(SalesOrderItemService.selectSalesOrderItemBySalesOrder(main, getSalesOrder()));
          setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesOrder().getCustomerId().getCompanyId().getCountryTaxRegimeId().getTaxProcessorId().getProcessorClass()));
          populateSalesOrderItem(getSalesOrderItemList());
          if (getSalesOrderItemList() != null && getSalesOrderItemList().isEmpty()) {
            getSalesOrderItemList().add(new SalesOrderItem());
          }
          if (!isDraft()) {
            setOrderProcessed(SalesOrderService.isRequestedProductProcessed(main, getSalesOrder()));
          }
        } else if (ViewType.list.toString().equals(viewType)) {
          main.getPageData().setSearchKeyWord(null);
          setAccount(null);
          setAccountGroup(null);
          setSalesOrderFilter(null);
          setSalesRequestedProduct(null);
          getSalesOrder().reset();
          setSalesReqSelected(null);
          setSalesReqList(null);
          setSalesReqAvailable(false);
          setSalesRequisitionSelected(false);
          setProductQuantityCount(null);
          setTaxCalculator(null);
          loadSalesOrderList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  public String actionNewForm(MainView main) {
    main.setViewType(ViewType.newform.toString());
    return null;
  }

  /**
   * Create salesOrderLazyModel.
   *
   * @param main
   */
  private void loadSalesOrderList(final MainView main) {
    if (salesOrderLazyModel == null) {
      salesOrderLazyModel = new LazyDataModel<SalesOrder>() {
        private List<SalesOrder> list;

        @Override
        public List<SalesOrder> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getAccountGroup() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = SalesOrderService.listPagedByAccount(main, getAccountGroup(), getSalesOrderFilter());
              main.commit(salesOrderLazyModel, first, pageSize);
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
        public Object getRowKey(SalesOrder salesOrder) {
          return salesOrder.getId();
        }

        @Override
        public SalesOrder getRowData(String rowKey) {
          if (list != null) {
            for (SalesOrder obj : list) {
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
    String SUB_FOLDER = "scm_sales_order/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @param status
   * @return the page to display.
   */
  public String saveSalesOrder(MainView main, int status) {
    return saveOrCloneSalesOrder(main, status, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesOrder(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesOrder(main, SystemConstants.DRAFT, "clone");
  }

  /**
   * Method to get the Sales Order Status object.
   *
   * @param statusId
   * @return
   */
  private SalesOrderStatus getSalesOrderStatus(int statusId) {
    SalesOrderStatus salesOrderStatus = new SalesOrderStatus();
    salesOrderStatus.setId(statusId);
    return salesOrderStatus;
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param status
   * @param key
   * @return
   */
  private String saveOrCloneSalesOrder(MainView main, int status, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            double amount = 0.0;
            for (SalesOrderItem item : getSalesOrderItemList()) {
              amount += item.getValueProdPieceSellingForced();
            }
            getSalesOrder().setTotalAmount(amount);
            getSalesOrder().setCompanyId(UserRuntimeView.instance().getCompany());
            getSalesOrder().setSalesOrderStatusId(getSalesOrderStatus(status));
            SalesOrderService.insertOrUpdate(main, getSalesOrder());
            SalesOrderItemService.insertSalesOrderItem(main, getSalesOrder(), getSalesOrderItemList());
            if (getSalesReqSelected() != null && getSalesReqSelected().length > 0) {
              //SalesOrderReferenceService.insertSalesOrderReference(main, getSalesOrder(), getSalesReqSelected());
            }
            if (SystemConstants.CONFIRMED.equals(status)) {

              SalesOrderService.updateSalesReqStatus(main, getSalesOrder());
            }
            break;
          case "clone":
            SalesOrderService.clone(main, getSalesOrder());
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
   * Delete one or many SalesOrder.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesOrder(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesOrderSelected)) {
        SalesOrderService.deleteByPkArray(main, getSalesOrderSelected()); //many record delete from list
        main.commit("success.delete");
        salesOrderSelected = null;
      } else {
        SalesOrderService.deleteByPk(main, getSalesOrder());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())) {
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

  public void actionInsertOrUpdateSalesInvoiceItem(MainView main) {
    boolean isNew = false;
    try {
      Integer rowIndex = Jsf.getParameterInt("rownumber");
      SalesOrderItem salesOrderItem = getSalesOrderItemList().get(rowIndex);

      if (salesOrderItem.getId() == null) {
        isNew = true;
      }
      salesOrderItem.setSalesOrderId(getSalesOrder());
      SalesOrderItemService.insertOrUpdateSalesOrderItem(main, salesOrderItem);
      SalesOrderService.insertOrUpdate(main, getSalesOrder());

      if (isNew) {
        addNewSalesOrderItem();
      } else if (rowIndex == 0) {
        addNewSalesOrderItem();
      }

    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  private void addNewSalesOrderItem() {
    SalesOrderItem salesOrderItem = new SalesOrderItem();
    getSalesOrderItemList().add(0, salesOrderItem);
  }

  /**
   * Method to delete sales order item.
   *
   * @param main
   * @param salesOrderItem
   * @return
   */
  public String deleteSalesOrderItem(MainView main, SalesOrderItem salesOrderItem) {
    try {
      if (salesOrderItem != null && salesOrderItem.getId() == null) {
        getSalesOrderItemList().remove(salesOrderItem);
        if (salesOrderItem.getProductId() != null) {
          getSalesRequestedProduct().remove(salesOrderItem.getProductId().getId());
        }
      } else if (salesOrderItem != null && salesOrderItem.getId() != null) {
        SalesOrderItemService.deleteByPk(main, salesOrderItem);
        getSalesOrderItemList().remove(salesOrderItem);
        getSalesRequestedProduct().remove(salesOrderItem.getProductId().getId());
      }
      if (getSalesOrderItemList().isEmpty()) {
        addNewSalesOrderItem();
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Return LazyDataModel of SalesOrder.
   *
   * @return
   */
  public LazyDataModel<SalesOrder> getSalesOrderLazyModel() {
    return salesOrderLazyModel;
  }

  /**
   * Return SalesOrder[].
   *
   * @return
   */
  public SalesOrder[] getSalesOrderSelected() {
    return salesOrderSelected;
  }

  /**
   * Set SalesOrder[].
   *
   * @param salesOrderSelected
   */
  public void setSalesOrderSelected(SalesOrder[] salesOrderSelected) {
    this.salesOrderSelected = salesOrderSelected;
  }

  /**
   * SalesOrderStatus autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.salesOrderStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.salesOrderStatusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesOrderStatus> salesOrderStatusAuto(String filter) {
    return ScmLookupView.salesOrderStatusAuto(filter);
  }

  /**
   * Method to select products based on its account group.
   *
   * @param filter
   * @return
   */
  public List<Product> completeProduct(String filter) {
    MainView main = Jsf.getMain();
    List<Product> completeProduct = new ArrayList<>();
    setCurrentProductId((Integer) Jsf.getAttribute("productId"));
    try {
      List<Product> productList = ScmLookupExtView.selectProductByAccountGroup(filter, getSalesOrder().getAccountGroupId(), getSalesOrder(), getCurrentProductId());

      for (Product product : productList) {
        if (getCurrentProductId() != null && product.getId().equals(getCurrentProductId())) {
          completeProduct.add(product);
        }
        if (!getSalesRequestedProduct().containsKey(product.getId())) {
          completeProduct.add(product);
        }
      }
      completeProduct = ProductService.selectProductQuantityDetails(main, getSalesOrder().getAccountGroupId(), getSalesOrder().getAccountGroupPriceListId(), completeProduct);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return completeProduct;
  }

  /**
   * Method to get the rate of a product from its product Price List table.
   *
   * @param salesOrderItem
   */
  public void updateProductPrice(SalesOrderItem salesOrderItem) {
    MainView main = Jsf.getMain();
    try {
      ProductPricelist productPrice = (ProductPricelist) ProductPricelistService.selectProductPriceListByAccountGroupAndProduct(main, getSalesOrder().getAccountGroupId(), salesOrderItem.getProductId());
      if (productPrice != null) {
        salesOrderItem.setValueProdPieceSelling(productPrice.getValuePtsPerProdPieceSell());
      }
      //salesOrderItem.setTaxCodeId(salesOrderItem.getProductId().getCommodityId().getTaxCodeSales());
      salesOrderItem.setQtyRequired(null);
      if (salesOrderItem.getProductId() != null) {
        if (getCurrentProductId() != null) {
          getSalesRequestedProduct().remove(getCurrentProductId());
        }
        getSalesRequestedProduct().put(salesOrderItem.getProductId().getId(), salesOrderItem.getProductId().getProductName());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public List<Customer> customerAuto(String filter) {
    if (getSalesOrder() != null && getSalesOrder().getAccountGroupId() != null) {
      return ScmLookupExtView.selectCustomerByAccountGroup(getSalesOrder().getAccountGroupId(), filter);
    }
    return null;
  }

  public List<Customer> customerByAccountGroup() {
    List<Customer> list = null;
    if (getAccountGroup() != null) {
      list = ScmLookupExtView.selectCustomerByAccountGroup(getAccountGroup());
    }
    return list;
  }

  public List<AccountGroup> accountGroupByAccount() {
    if (UserRuntimeView.instance().getAccount() != null) {
      MainView main = Jsf.getMain();
      try {
        List<AccountGroup> list = ScmLookupExtView.accountGroupByAccount(UserRuntimeView.instance().getAccount());
        if (list != null && !list.isEmpty() && getSalesOrder().getAccountGroupId() == null) {
          getSalesOrder().setAccountGroupId(list.get(0));
          getSalesOrder().setSalesOrderNo("DR-" + getNextSalesOrderNumber(main, getSalesOrder().getAccountGroupId()));
        }
        return list;
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public List<AccountGroupPriceList> accountGroupPriceListByAccountGroup() {
    List<AccountGroupPriceList> accountGroupPriceList = null;
    if (getSalesOrder().getAccountGroupId() != null) {
      accountGroupPriceList = ScmLookupExtView.accountGroupPriceListByAccountGroup(getSalesOrder().getAccountGroupId());
      if (accountGroupPriceList != null && !accountGroupPriceList.isEmpty() && accountGroupPriceList.size() == 1 && getSalesOrder().getAccountGroupPriceListId() == null) {
        getSalesOrder().setAccountGroupPriceListId(accountGroupPriceList.get(0));
      }
    }
    return accountGroupPriceList;
  }

  public void accountGroupSelectedEvent(SelectEvent event) {
    AccountGroup ag = (AccountGroup) event.getObject();
    MainView main = Jsf.getMain();
    try {
      if (ag != null) {
        getSalesOrder().setSalesOrderNo("DR-" + getNextSalesOrderNumber(main, ag));
      } else {
        getSalesOrder().setAccountGroupId(null);
        getSalesOrder().setAccountGroupPriceListId(null);
        getSalesOrder().setCustomerId(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   * Method to handle customer selection event.
   *
   * @param event
   */
  public void customerSelectedEvent(SelectEvent event) {
    setSalesReqAvailable(false);
    Customer customer = (Customer) event.getObject();
    if (customer != null) {
      MainView main = Jsf.getMain();
      try {
        setSalesReqList(SalesReqService.selectSalesReqBySalesAccount(main, getAccountGroup(), customer));
        if (getSalesReqList() != null && !getSalesReqList().isEmpty()) {
          setSalesReqAvailable(true);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  public void customerSelectEvent(SelectEvent event) {
    Customer cust = (Customer) event.getObject();
    if (cust != null && getAccountGroup() != null) {
      MainView main = Jsf.getMain();
      try {
        SalesAccount saccount = SalesAccountService.selectSalesAccountByCustomerAccountGroup(main, cust, getAccountGroup());
        if (saccount != null) {
          getSalesOrder().setAccountGroupPriceListId(saccount.getAccountGroupPriceListId());
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  private void updateSalesOrderNumber(MainView main) {
    if (getSalesOrder().getAccountGroupId() != null) {
      getSalesOrder().setSalesOrderNo("DR-" + getNextSalesOrderNumber(main, getSalesOrder().getAccountGroupId()));
    } else {
      getSalesOrder().setSalesOrderNo(null);
    }
  }

  private String getNextSalesOrderNumber(MainView main, AccountGroup accountGroup) {
    return AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, accountGroup, PrefixTypeService.SALES_ORDER_PREFIX_ID, true, getSalesOrder().getFinancialYearId());
  }

  /**
   *
   * @param salesReqItemList
   * @return
   */
  private List<SalesOrderItem> populateSalesOrderItem(List<SalesOrderItem> salesOrderItemList) {
    List<SalesOrderItem> list = null;
    if (salesOrderItemList != null) {
      list = new ArrayList<>();
      int totQty = 0;
      for (SalesOrderItem salesOrderItem : salesOrderItemList) {
        if (salesOrderItem.getQtyRequired() > 0) {
          totQty += salesOrderItem.getQtyRequired();
          // Checks the product is already added in sales order list or not
          if (!getSalesRequestedProduct().containsKey(salesOrderItem.getProductId().getId())) {
            // Adding product in product request map.
            getSalesRequestedProduct().put(salesOrderItem.getProductId().getId(), salesOrderItem.getProductId().getProductName());
          }
          list.add(salesOrderItem);
        }
      }
      setProductQuantityCount(totQty == 0 ? null : totQty);
    }
    return list;
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionMergeSalesOrderToSalesInvoice(MainView main) {
    try {
      if (UserRuntimeView.instance().isGstRegime()) {
        List<SalesOrderItem> generalSalesOrderItemList = SalesOrderItemService.selectSalesOrderItemByProductType(main, getSalesOrder(), SystemConstants.PRODUCT_TYPE_GENERAL);
        List<SalesOrderItem> coldChainSalesOrderItemList = SalesOrderItemService.selectSalesOrderItemByProductType(main, getSalesOrder(), SystemConstants.PRODUCT_TYPE_COLD_CHAIN);

        if (generalSalesOrderItemList != null && !generalSalesOrderItemList.isEmpty()) {
          SalesOrderService.mergeSalesOrderToSalesInvoice(main, getSalesOrder(), generalSalesOrderItemList, getTaxCalculator());
        }

        if (coldChainSalesOrderItemList != null && !coldChainSalesOrderItemList.isEmpty()) {
          SalesOrderService.mergeSalesOrderToSalesInvoice(main, getSalesOrder(), coldChainSalesOrderItemList, getTaxCalculator());
        }

      } else {
        SalesInvoiceService.mergeSalesOrderToSalesInvoice(main, getSalesOrder());
      }
      SalesOrderStatus salesOrderStatus = new SalesOrderStatus();
      salesOrderStatus.setId(SystemConstants.CONFIRMED_AND_PART_PENDING);
      getSalesOrder().setSalesOrderStatusId(salesOrderStatus);
      SalesOrderService.insertOrUpdate(main, getSalesOrder());
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  private void processSalesOrderItemSummery() {
    productQuantityCount = 0;
    for (SalesOrderItem item : getSalesOrderItemList()) {
      productQuantityCount += (item.getQtyRequired() == null ? 0 : item.getQtyRequired());
    }
  }

  public void qtyChangeEventHandler() {
    processSalesOrderItemSummery();
  }
}
