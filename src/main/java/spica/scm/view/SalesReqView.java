/*
 * @(#)SalesReqView.java	1.0 Tue May 10 17:16:17 IST 2016
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
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.AccountGroup;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesReq;
import spica.scm.service.SalesReqService;
import spica.scm.domain.Customer;
import spica.scm.domain.Product;
import spica.scm.domain.ProductPricelist;
import spica.scm.domain.SalesAccount;
import spica.scm.domain.SalesOrder;
import spica.scm.domain.SalesOrderStatus;
import spica.scm.domain.SalesReqItem;
import spica.scm.domain.SalesReqStatus;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.PrefixTypeService;
import spica.scm.service.ProductPricelistService;
import spica.scm.service.ProductService;
import spica.scm.service.SalesAccountService;
import spica.scm.service.SalesOrderItemService;
import spica.scm.service.SalesOrderService;
import spica.scm.service.SalesReqItemService;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.entity.core.UserMessageException;

/**
 * SalesReqView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 10 17:16:17 IST 2016
 */
@Named(value = "salesReqView")
@ViewScoped
public class SalesReqView implements Serializable {

  private transient SalesReq salesReq;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesReq> salesReqLazyModel; 	//For lazy loading datatable.
  private transient SalesReq[] salesReqSelected;	 //Selected Domain Array
  private transient AccountGroup accountGroup = null;
  private transient Customer customer = null;

  private transient List<SalesReqItem> salesReqItemList;
  private transient List<AccountGroup> accountGroupList = null;
  private transient List<Customer> customerList = null;
  private boolean schemeRequired = false;
  private boolean requestProcessed = false;
  private transient Integer currentProductId = null;
  private transient HashMap<Integer, String> salesRequestedProduct;
  private transient Integer salesReqFilter;

  private transient Integer salesReqItemCount;
  private transient Integer productQuantityCount;

  /**
   * Default Constructor.
   */
  public SalesReqView() {
    super();
  }

  /**
   * Return SalesReq.
   *
   * @return SalesReq.
   */
  public SalesReq getSalesReq() {
    if (salesReq == null) {
      salesReq = new SalesReq();
    }
    if (salesReq.getCompanyId() == null) {
      salesReq.setCompanyId(UserRuntimeView.instance().getCompany());
      salesReq.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    return salesReq;
  }

  /**
   * Set SalesReq.
   *
   * @param salesReq.
   */
  public void setSalesReq(SalesReq salesReq) {
    this.salesReq = salesReq;
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

  public Integer getCurrentProductId() {
    return currentProductId;
  }

  public void setCurrentProductId(Integer currentProductId) {
    this.currentProductId = currentProductId;
  }

  public List<SalesReqItem> getSalesReqItemList() {
    if (salesReqItemList == null) {
      salesReqItemList = new ArrayList<>();
    }
    return salesReqItemList;
  }

  public void setSalesReqItemList(List<SalesReqItem> salesReqItemList) {
    this.salesReqItemList = salesReqItemList;
  }

  public List<AccountGroup> getAccountGroupList() {
    if (getCustomer() != null && getCustomer().getId() != null) {
      accountGroupList = ScmLookupExtView.accountGroupByCustomer(getCustomer());
    }
    return accountGroupList;
  }

  public void setAccountGroupList(List<AccountGroup> accountGroupList) {
    this.accountGroupList = accountGroupList;
  }

  public List<Customer> getCustomerList() {
    if (customerList == null) {
      customerList = new ArrayList<>();
    }
    return customerList;
  }

  public void setCustomerList(List<Customer> customerList) {
    this.customerList = customerList;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public AccountGroup getAccountGroup() {
    if (UserRuntimeView.instance().getAccountGroup() != null) {
      this.accountGroup = UserRuntimeView.instance().getAccountGroup();
    }
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public boolean isSchemeRequired() {
    return schemeRequired;
  }

  public void setSchemeRequired(boolean schemeRequired) {
    this.schemeRequired = schemeRequired;
  }

  public boolean isRequestProcessed() {
    return requestProcessed;
  }

  public void setRequestProcessed(boolean requestProcessed) {
    this.requestProcessed = requestProcessed;
  }

  public Integer getSalesReqFilter() {
    return salesReqFilter;
  }

  public void setSalesReqFilter(Integer salesReqFilter) {
    this.salesReqFilter = salesReqFilter;
  }

  public Integer getSalesReqItemCount() {
    return salesReqItemCount;
  }

  public void setSalesReqItemCount(Integer salesReqItemCount) {
    this.salesReqItemCount = salesReqItemCount;
  }

  public Integer getProductQuantityCount() {
    return productQuantityCount;
  }

  public void setProductQuantityCount(Integer productQuantityCount) {
    this.productQuantityCount = productQuantityCount;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesReq(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          if (UserRuntimeView.instance().getAccountGroup() == null) {
            main.error("account.group.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else {
            getSalesReq().reset();
            setSalesReqItemList(null);
            setSalesReq(actionNewSalesReq());
            setCustomer(null);
            setAccountGroup(null);
            setSchemeRequired(false);
          }
        } else if (ViewType.editform.toString().equals(viewType)) {
          setSchemeRequired(false);
          setSalesReq((SalesReq) SalesReqService.selectByPk(main, getSalesReq()));
          if (getSalesReq().getSchemeRequired() != null && getSalesReq().getSchemeRequired().equals(1)) {
            setSchemeRequired(true);
          }
          setSalesReqItemList(SalesReqItemService.selectSalesReqItemBySalesReq(main, getSalesReq()));
          processSalesRequistionSummery();
          if (SystemConstants.DRAFT.equals(getSalesReq().getSalesReqStatusId().getId())) {
            addNewSalesReqItem();
          }
          setCustomer(getSalesReq().getSalesAccountId().getCustomerId());
          setAccountGroup(getSalesReq().getSalesAccountId().getAccountGroupId());
          if (!isDraft()) {
            setRequestProcessed(SalesReqService.isRequestedProductProcessed(main, getSalesReq()));
          }
        } else if (ViewType.list.toString().equals(viewType)) {
          main.getPageData().setSearchKeyWord(null);
          setSalesReqFilter(null);
          setAccountGroup(null);
          setCustomer(null);
          setCurrentProductId(null);
          setSalesRequestedProduct(null);
          setSchemeRequired(false);
          loadSalesReqList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private SalesReq actionNewSalesReq() {
    SalesReq req = new SalesReq();
    req.setRequisitionDate(new Date());
    req.setRequisitionNo("DR-" + getSalesRequistionNumber(getAccountGroup()));
    return req;
  }

  /**
   *
   * @param event
   */
  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup ag = (AccountGroup) event.getObject();
    getSalesReq().setRequisitionNo("DR-" + getSalesRequistionNumber(ag));
    getSalesReq().setSalesAccountId(getCustomerSalesAccount(getCustomer(), ag));
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @return
   */
  private String getSalesRequistionNumber(AccountGroup accountGroup) {
    MainView main = Jsf.getMain();
    try {
      if (accountGroup != null) {
        return AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, accountGroup, PrefixTypeService.SALES_REQUISITION_PREFIX_ID, true, getSalesReq().getFinancialYearId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return "";
  }

  /**
   *
   * @param main
   * @param customer
   * @param accountGroup
   * @return
   */
  private SalesAccount getCustomerSalesAccount(Customer customer, AccountGroup accountGroup) {
    SalesAccount saccount = null;
    MainView main = Jsf.getMain();
    try {
      if (getCustomer() != null && getAccountGroup() != null) {
        saccount = SalesAccountService.selectSalesAccountByCustomerAccountGroup(main, customer, accountGroup);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return saccount;
  }

  /**
   *
   * @param event
   */
  public void customerSelectEvent(SelectEvent event) {
    Customer cust = (Customer) event.getObject();
    if (cust != null && getAccountGroup() != null) {
      MainView main = Jsf.getMain();
      try {
        SalesAccount saccount = SalesAccountService.selectSalesAccountByCustomerAccountGroup(main, cust, getAccountGroup());
        getSalesReq().setSalesAccountId(saccount);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  /**
   * Create salesReqLazyModel.
   *
   * @param main
   */
  private void loadSalesReqList(final MainView main) {
    if (salesReqLazyModel == null) {
      salesReqLazyModel = new LazyDataModel<SalesReq>() {
        private List<SalesReq> list;

        @Override
        public List<SalesReq> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getAccountGroup() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = SalesReqService.listPaged(main, getAccountGroup(), getSalesReqFilter());
              main.commit(salesReqLazyModel, first, pageSize);
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
        public Object getRowKey(SalesReq salesReq) {
          return salesReq.getId();
        }

        @Override
        public SalesReq getRowData(String rowKey) {
          if (list != null) {
            for (SalesReq obj : list) {
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
    String SUB_FOLDER = "scm_sales_req/";
  }

  /**
   *
   * @param main
   * @param status
   * @return
   */
  public String actionSaveSalesReq(MainView main, int status) {
    // boolean isNew = false;
    try {
      getSalesReq().setSalesReqStatusId(getRequisitionStatus(status));
      if (getSalesReq().getId() == null) {
        //  isNew = true;
      }
      getSalesReq().setSchemeRequired(SalesReqService.SALES_REQ_SCHEME_NOT_REQUIRED);
      if (isSchemeRequired()) {
        getSalesReq().setSchemeRequired(SalesReqService.SALES_REQ_SCHEME_REQUIRED);
      }
      SalesReqService.insertOrUpdate(main, getSalesReq());
      SalesReqItemService.insertOrUpdateSalesReqItem(main, salesReq, getSalesReqItemList());
      main.commit("success.save");
      main.setViewType(ViewTypes.editform);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param status
   * @return
   */
  private SalesReqStatus getRequisitionStatus(int status) {
    SalesReqStatus rs = new SalesReqStatus();
    if (SystemConstants.DRAFT == status) {
      rs.setId(SystemConstants.DRAFT);
    } else if (SystemConstants.CONFIRMED == status) {
      rs.setId(SystemConstants.CONFIRMED);
    }
    return rs;
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionNewSalesReqItem(MainView main) {
    SalesReqItem reqItem = new SalesReqItem();
    try {
      if (getSalesReqItemList() != null && getSalesReqItemList().size() > 0) {
        SalesReqItem item = getSalesReqItemList().get(0);
        if (item != null && item.getId() == null) {
          item.setSalesReqId(getSalesReq());
          SalesReqItemService.insert(main, item);
        }
        getSalesReqItemList().add(0, reqItem);
      } else {
        getSalesReqItemList().add(reqItem);
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
   * @param main
   */
  public void actionInsertOrUpdateSalesReqItem(MainView main) {
    boolean isNew = false;
    try {
      Integer rowIndex = Jsf.getParameterInt("rownumber");
      SalesReqItem salesReqItem = getSalesReqItemList().get(rowIndex);

      if (salesReqItem.getProductId() != null && salesReqItem.getQtyRequired() != null) {
        if (salesReqItem.getId() == null) {
          isNew = true;
        }
        getSalesReq().setSchemeRequired(SalesReqService.SALES_REQ_SCHEME_NOT_REQUIRED);
        if (isSchemeRequired()) {
          getSalesReq().setSchemeRequired(SalesReqService.SALES_REQ_SCHEME_REQUIRED);
        }

        salesReqItem.setSalesReqId(getSalesReq());
        if (getSalesReq().getSchemeRequired() != null && getSalesReq().getSchemeRequired().equals(SalesReqService.SALES_REQ_SCHEME_NOT_REQUIRED)) {
          salesReqItem.setDiscountValueFixed(null);
          salesReqItem.setDiscountValuePercentage(null);
          salesReqItem.setQtyFreeSuggested(null);
          salesReqItem.setValueProdPieceSellingForced(null);
        }

        SalesReqItemService.insertOrUpdate(main, salesReqItem);
        SalesReqService.insertOrUpdate(main, getSalesReq());

        if (isNew) {
          addNewSalesReqItem();
        } else if (rowIndex == 0) {
          addNewSalesReqItem();
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
  private void addNewSalesReqItem() {
    SalesReqItem reqItem = new SalesReqItem();
    getSalesReqItemList().add(0, reqItem);
  }

  /**
   *
   * @param main
   * @param salesReqItem
   * @return
   */
  public String actionDeleteSalesItem(MainView main, SalesReqItem salesReqItem) {
    try {
      if (salesReqItem != null && salesReqItem.getId() == null) {
        getSalesReqItemList().remove(salesReqItem);
        if (salesReqItem.getProductId() != null) {
          getSalesRequestedProduct().remove(salesReqItem.getProductId().getId());
        }
      } else if (salesReqItem != null && salesReqItem.getId() != null) {
        SalesReqItemService.deleteByPk(main, salesReqItem);
        getSalesReqItemList().remove(salesReqItem);
        getSalesRequestedProduct().remove(salesReqItem.getProductId().getId());
      }
      processSalesRequistionSummery();
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesReq(MainView main) {
    return saveOrCloneSalesReq(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesReq(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesReq(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesReq(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesReqService.insertOrUpdate(main, getSalesReq());
            break;
          case "clone":
            SalesReqService.clone(main, getSalesReq());
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
   * Delete one or many SalesReq.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesReq(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesReqSelected)) {
        SalesReqService.deleteByPkArray(main, getSalesReqSelected()); //many record delete from list
        main.commit("success.delete");
        salesReqSelected = null;
      } else {
        SalesReqService.deleteByPk(main, getSalesReq());  //individual record delete from list or edit form
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

  /**
   *
   * @param main
   * @return
   */
  public String mergeToSalesOrder(MainView main) {
    try {
      SalesOrder salesOrder = getSalesOrder();
      SalesOrderService.insert(main, salesOrder);
      int insertCounter = SalesOrderItemService.insertSalesOrderItemFromSalesReq(main, salesOrder, getSalesReq());
      if (insertCounter > 0) {
        SalesReqService.updateSalesReqStatus(main, getSalesReq(), SystemConstants.CONFIRMED_PARTIALYY_PROCESSED);
        salesOrder.setSalesOrderNo("DR-" + AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, salesOrder.getAccountGroupId(), PrefixTypeService.SALES_ORDER_PREFIX_ID, true, getSalesReq().getFinancialYearId()));
        salesOrder.setSalesOrderDate(new Date());
        SalesOrderStatus salesOrderStatus = new SalesOrderStatus();
        salesOrderStatus.setId(SystemConstants.DRAFT);
        salesOrder.setSalesOrderStatusId(salesOrderStatus);
        SalesOrderService.insertOrUpdate(main, salesOrder);
        main.commit("success.merge.salesreq");
      } else {
        throw new UserMessageException("error.past.record.edit");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Return LazyDataModel of SalesReq.
   *
   * @return
   */
  public LazyDataModel<SalesReq> getSalesReqLazyModel() {
    return salesReqLazyModel;
  }

  /**
   * Return SalesReq[].
   *
   * @return
   */
  public SalesReq[] getSalesReqSelected() {
    return salesReqSelected;
  }

  /**
   * Set SalesReq[].
   *
   * @param salesReqSelected
   */
  public void setSalesReqSelected(SalesReq[] salesReqSelected) {
    this.salesReqSelected = salesReqSelected;
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<Product> completeProduct(String filter) {
    MainView main = Jsf.getMain();
    List<Product> completeProduct = null;
    try {
      setCurrentProductId((Integer) Jsf.getAttribute("productId"));
      List<Product> productList = null;
      productList = ScmLookupExtView.selectProductByAccountGroup(filter, getAccountGroup(), getSalesReq(), getCurrentProductId());

      for (Product product : productList) {
        if (getCurrentProductId() != null && product.getId().equals(getCurrentProductId())) {
          completeProduct.add(product);
        }
        if (!getSalesRequestedProduct().containsKey(product.getId())) {
          completeProduct.add(product);
        }
      }
      completeProduct = ProductService.selectProductQuantityDetails(main, getSalesReq().getSalesAccountId().getAccountGroupId(),
              getSalesReq().getSalesAccountId().getAccountGroupPriceListId(), completeProduct);

    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return completeProduct;
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
    if (getAccountGroup() != null) {
      return ScmLookupExtView.selectCustomerByAccountGroup(getAccountGroup(), filter);
    }
    return null;
  }

  /**
   *
   * @return
   */
  public boolean disablePercentDiscount() {
    if (getSalesReq() != null) {
      return getSalesReq().getDiscValProdPieceFixed() != null && getSalesReq().getDiscValProdPieceFixed() > 0;
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean disableFixedDiscount() {
    if (getSalesReq() != null) {
      return getSalesReq().getDiscValProdPiecePercent() != null && getSalesReq().getDiscValProdPiecePercent() > 0;
    }
    return false;
  }

  /**
   * Method to get the rate of a product from its product Price List table.
   *
   * @param salesReqItem
   */
  public void updateProductPrice(SalesReqItem salesReqItem) {

    MainView main = Jsf.getMain();
    try {
      ProductPricelist productPrice = (ProductPricelist) ProductPricelistService.selectProductPriceListByAccountGroupAndProduct(main, getSalesReq().getSalesAccountId().getAccountGroupId(), salesReqItem.getProductId());
      if (productPrice != null) {
        salesReqItem.setValueProdPieceSelling(productPrice.getValuePtsPerProdPieceSell());
      }
      salesReqItem.setQtyRequired(null);
      if (salesReqItem.getProductId() != null) {
        if (getCurrentProductId() != null) {
          getSalesRequestedProduct().remove(getCurrentProductId());
        }
        getSalesRequestedProduct().put(salesReqItem.getProductId().getId(), salesReqItem.getProductId().getProductName());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }

  }

  /**
   * Method to get the list account groups based on the account.
   *
   * @return
   */
  public List<AccountGroup> lookupAccountGroupByAccount() {
    List<AccountGroup> list;
    list = ScmLookupExtView.accountGroupByAccount(UserRuntimeView.instance().getAccount());
    if (list != null && !list.isEmpty() && getAccountGroup() == null) {
      setAccountGroup(list.get(0));
      getSalesReq().setRequisitionNo("DR-" + getSalesRequistionNumber(getAccountGroup()));
    }
    return list;
  }

  /**
   * Method to check the sales order status confirmed or not.
   *
   * @return
   */
  public boolean isConfirmed() {
    if (getSalesReq() != null && getSalesReq().getSalesReqStatusId() != null) {
      return (getSalesReq().getSalesReqStatusId().getId().equals(SystemConstants.CONFIRMED));
    }
    return false;
  }

  /**
   * Method to check the sales order status draft or not.
   *
   * @return
   */
  public boolean isDraft() {
    if (getSalesReq() != null && getSalesReq().getSalesReqStatusId() != null) {
      return (getSalesReq().getSalesReqStatusId().getId().equals(SystemConstants.DRAFT));
    }
    return false;
  }

  /**
   * Method to check the sales order status processed or not.
   *
   * @return
   */
  public boolean isProcessed() {
    if (getSalesReq() != null && getSalesReq().getSalesReqStatusId() != null) {
      return (getSalesReq().getSalesReqStatusId().getId().equals(SystemConstants.CONFIRMED_AND_PROCESSED));
    }
    return false;
  }

  /**
   * Method to check the sales order status processed or not.
   *
   * @return
   */
  public boolean isPartiallyProcessed() {
    if (getSalesReq() != null && getSalesReq().getSalesReqStatusId() != null) {
      return (getSalesReq().getSalesReqStatusId().getId().equals(SystemConstants.CONFIRMED_PARTIALYY_PROCESSED));
    }
    return false;
  }

  /**
   * Method to check the sales order status processed or not.
   *
   * @return
   */
  public boolean isMergeable() {
    if (isConfirmed() || isPartiallyProcessed()) {
      return !(isRequestProcessed());
    }
    return false;
  }

  /**
   *
   * @param salesReq
   * @return
   */
  private SalesOrder getSalesOrder() {
    SalesOrder salesOrder = new SalesOrder();
    salesOrder.setAccountGroupId(salesReq.getSalesAccountId().getAccountGroupId());
    salesOrder.setCustomerId(salesReq.getSalesAccountId().getCustomerId());
    salesOrder.setAccountGroupPriceListId(salesReq.getSalesAccountId().getAccountGroupPriceListId());
    salesOrder.setDiscountValueFixed(salesReq.getDiscValProdPieceFixed());
    salesOrder.setDiscountValuePerc(salesReq.getDiscValProdPiecePercent());
    return salesOrder;
  }

  private void processSalesRequistionSummery() {
    salesReqItemCount = 0;
    productQuantityCount = 0;
    for (SalesReqItem item : getSalesReqItemList()) {
      salesReqItemCount++;
      productQuantityCount += (item.getQtyRequired() == null ? 0 : item.getQtyRequired());
    }
  }

  public void qtyChangeEventHandler() {
    processSalesRequistionSummery();
  }
}
