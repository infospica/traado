/*
 * @(#)PurchaseOrderGstIndiaView.java	1.0 Mon Apr 11 14:41:12 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import spica.scm.common.ProductQuantityInfo;
import spica.scm.common.ProductSummary;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.domain.Consignment;
import spica.scm.domain.Contract;
import spica.scm.domain.Product;
import spica.scm.domain.PurchaseOrder;
import spica.scm.domain.PurchaseOrderItem;
import spica.scm.domain.PurchaseOrderStatus;
import spica.scm.domain.PurchaseReceiptPlan;
import spica.scm.domain.PurchaseReq;
import spica.scm.domain.PurchaseReqItem;
import spica.scm.domain.PurchaseShipmentPlan;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.AccountService;
import spica.scm.service.ConsignmentReferenceService;
import spica.scm.service.PrefixTypeService;
import spica.scm.service.ProductService;
import spica.scm.service.PurchaseOrderItemService;
import spica.scm.service.PurchaseOrderService;
import spica.scm.service.PurchaseReceiptPlanService;
import spica.scm.service.PurchaseReqItemService;
import spica.scm.service.PurchaseReqService;
import spica.scm.tax.TaxCalculator;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * PurchaseOrderView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Apr 11 14:41:12 IST 2016
 */
@Named(value = "purchaseOrderView")
@ViewScoped
public class PurchaseOrderView implements Serializable {

  private transient PurchaseOrder purchaseOrder;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseOrder> purchaseOrderLazyModel; 	//For lazy loading datatable.
  private transient PurchaseOrder[] purchaseOrderSelected;	 //Selected Domain Array
  private transient PurchaseReq purchaseReq;	//Domain object/selected Domain.

  private List<PurchaseOrderItem> purchaseOrderItemList;
  private List<PurchaseReqItem> purchaseReqItemList;
  private transient boolean isNew;

  private List<PurchaseReq> selectedPurchaseReq;
  private transient PurchaseReq[] purchaseReqSelectedArray;	 //Selected Domain Array
  private List<PurchaseReq> purchaseReqList = null;

  private String requisitionProducts;
  private Map<String, Object> dataTableFilters;
  private Object filterValue;
  private Date datePlan1;
  private Date datePlan2;
  private Date datePlan3;
  private Date datePlan;
  private Contract contract;
  private Account account;
  private int split1;
  private int split2;
  private int split3;
  private int activeIndex = 0; //tab active index, reset to 0 when new records
  private List<PurchaseShipmentPlan> purchaseShipmentPlanList = null;
  private boolean purchaseReqAvailable;
  private transient Account primaryAccountId;
  private transient TaxCalculator taxCalculator;
  private transient Integer currentProductId = null;

  @PostConstruct
  public void init() {
    Integer invoiceId = (Integer) Jsf.popupParentValue(Integer.class);
    if (invoiceId != null) {
      getPurchaseOrder().setId(invoiceId);
    }
  }

  /**
   * Default Constructor.
   */
  public PurchaseOrderView() {
    super();
  }

  /**
   *
   * @return
   */
  public Account getPrimaryAccountId() {
    if (getAccount() != null) {
      primaryAccountId = getAccount().getAccountId() != null ? getAccount().getAccountId() : getAccount();
    }
    return primaryAccountId;
  }

  /**
   *
   * @param primaryAccountId
   */
  public void setPrimaryAccountId(Account primaryAccountId) {
    this.primaryAccountId = primaryAccountId;
  }

  /**
   *
   * @return
   */
  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  /**
   *
   * @param taxCalculator
   */
  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public Map<String, Object> getDataTableFilters() {
    if (dataTableFilters == null) {
      dataTableFilters = new HashMap<>();
    }
    return dataTableFilters;
  }

  public void setDataTableFilters(Map<String, Object> dataTableFilters) {
    this.dataTableFilters = dataTableFilters;
  }

  public Object getFilterValue() {
    return filterValue;
  }

  public void setFilterValue(Object filterValue) {
    this.filterValue = filterValue;
  }

  public Date getDatePlan1() {
    return datePlan1;
  }

  public void setDatePlan1(Date datePlan1) {
    this.datePlan1 = datePlan1;
  }

  public Date getDatePlan2() {
    return datePlan2;
  }

  public void setDatePlan2(Date datePlan2) {
    this.datePlan2 = datePlan2;
  }

  public Date getDatePlan3() {
    return datePlan3;
  }

  public void setDatePlan3(Date datePlan3) {
    this.datePlan3 = datePlan3;
  }

  public Date getDatePlan() {
    return datePlan;
  }

  public void setDatePlan(Date datePlan) {
    this.datePlan = datePlan;
  }

  public boolean isPurchaseReqAvailable() {
    return purchaseReqAvailable;
  }

  public Integer getCurrentProductId() {
    return currentProductId;
  }

  public void setCurrentProductId(Integer currentProductId) {
    this.currentProductId = currentProductId;
  }

  public void setPurchaseReqAvailable(boolean purchaseReqAvailable) {
    this.purchaseReqAvailable = purchaseReqAvailable;
  }

  /**
   * Return PurchaseOrder.
   *
   * @return PurchaseOrder.
   */
  public PurchaseOrder getPurchaseOrder() {
    if (purchaseOrder == null) {
      purchaseOrder = new PurchaseOrder();
    }
    if (purchaseOrder.getCompanyId() == null) {
      purchaseOrder.setCompanyId(UserRuntimeView.instance().getCompany());
      purchaseOrder.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    return purchaseOrder;
  }

  public Contract getContract() {
    if (contract == null) {
      contract = UserRuntimeView.instance().getContract();
    } else {
      if (getPurchaseOrder().getId() != null) {
        contract = getPurchaseOrder().getContractId();
      }
    }
    return contract;
  }

  public Account getAccount() {
    account = UserRuntimeView.instance().getAccount();
    return account;
  }

  public void setContract(Contract contract) {
    this.contract = contract;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public String getRequisitionProducts() {
    return requisitionProducts;
  }

  public void setRequisitionProducts(String requisitionProducts) {
    this.requisitionProducts = requisitionProducts;
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }

  /**
   * Set PurchaseOrder.
   *
   * @param purchaseOrder.
   */
  public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
  }

  public List<PurchaseReq> getSelectedPurchaseReq() {
    return selectedPurchaseReq;
  }

  public void setSelectedPurchaseReq(List<PurchaseReq> selectedPurchaseReq) {
    this.selectedPurchaseReq = selectedPurchaseReq;
  }

  public List<PurchaseReqItem> getPurchaseReqItemList() {
    return purchaseReqItemList;
  }

  public void setPurchaseReqItemList(List<PurchaseReqItem> purchaseReqItemList) {
    this.purchaseReqItemList = purchaseReqItemList;
  }

  public PurchaseReq[] getPurchaseReqSelectedArray() {
    return purchaseReqSelectedArray;
  }

  public void setPurchaseReqSelectedArray(PurchaseReq[] purchaseReqSelectedArray) {
    this.purchaseReqSelectedArray = purchaseReqSelectedArray;
  }

  public int getSplit1() {
    return split1;
  }

  public void setSplit1(int split1) {
    this.split1 = split1;
  }

  public int getSplit2() {
    return split2;
  }

  public void setSplit2(int split2) {
    this.split2 = split2;
  }

  public int getSplit3() {
    return split3;
  }

  public void setSplit3(int split3) {
    this.split3 = split3;
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }
  private int changeTab = 0;

  public void setPurchaseReqList(List<PurchaseReq> purchaseReqList) {
    this.purchaseReqList = purchaseReqList;
  }

  /**
   * Return LazyDataModel of PurchaseOrder.
   *
   * @return
   */
  public LazyDataModel<PurchaseOrder> getPurchaseOrderLazyModel() {
    return purchaseOrderLazyModel;
  }

  /**
   * Return PurchaseOrder[].
   *
   * @return
   */
  public PurchaseOrder[] getPurchaseOrderSelected() {
    return purchaseOrderSelected;
  }

  /**
   * Set PurchaseOrder[].
   *
   * @param purchaseOrderSelected
   */
  public void setPurchaseOrderSelected(PurchaseOrder[] purchaseOrderSelected) {
    this.purchaseOrderSelected = purchaseOrderSelected;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @param consignment1
   * @param str
   * @return
   */
  public String switchPurchaseOrder(MainView main, String viewType) {
    purchaseShipmentPlanList = null;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        if (UserRuntimeView.instance().getCompany() == null) {
          main.error("company.required");
          main.setViewType(ViewTypes.list);
          return null;
        } else if (UserRuntimeView.instance().getAccount() == null) {
          main.error("account.required");
          if (ViewType.newform.toString().equals(viewType)) {
            main.setViewType(ViewTypes.list);
          }
          return null;
        } else if (UserRuntimeView.instance().getContract() == null) {
          main.error("contract.required");
          if (ViewType.newform.toString().equals(viewType)) {
            main.setViewType(ViewTypes.list);
          }
          return null;
        }
        main.setViewType(viewType);
        if (purchaseReq != null) {
          setPurchaseOrder(PurchaseOrderService.createPO(main, purchaseReq));
          main.commit("success.save");
          resetList();
          purchaseReq = null;
          main.setViewType(ViewTypes.editform);
        } else if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          setDatePlan1(null);
          setDatePlan2(null);
          setDatePlan3(null);
          setActiveIndex(0);
          newPurchaseOrder(main);
          getPurchaseOrder().setTaxProcessorId(getCompany().getCountryTaxRegimeId().getTaxProcessorId());
          setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getPurchaseOrder().getTaxProcessorId().getProcessorClass()));
          setPurchaseOrderItemList(selectPurchaseReqItem(main));
          if (getPurchaseOrderItemList().isEmpty()) {
            addNewPurchaseOrderItem();
          }
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          resetList();
          if (changeTab != 1) {
            setActiveIndex(0);
          } else {
            Jsf.execute("$('#potabs a[href=\"#shipmentPlanDiv\"]').trigger('click');");
            changeTab = 0;
          }
          setPurchaseOrder((PurchaseOrder) PurchaseOrderService.selectByPk(main, getPurchaseOrder()));
          setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getPurchaseOrder().getTaxProcessorId().getProcessorClass()));
          setPurchaseOrderItemList(PurchaseOrderItemService.selectPurchaseOrderItemByPurchaseOrder(main, getPurchaseOrder().getId()));

          for (PurchaseOrderItem purchaseOrderItem : getPurchaseOrderItemList()) {

            purchaseOrderItem.setProductSummary(new ProductSummary(purchaseOrderItem.getProductId().getId(), purchaseOrderItem.getProductBatchId() != null ? purchaseOrderItem.getProductBatchId().getId() : null,
                    purchaseOrderItem.getProductId().getProductName()));
            getTaxCalculator().updatePurchaseOrderItem(main, getAccount(), purchaseOrderItem);
          }

          getTaxCalculator().processPurchaseOrderCalculation(getPurchaseOrder(), getPurchaseOrderItemList());

          if (isDraft() && getPurchaseOrderItemList().isEmpty()) {
            addNewPurchaseOrderItem();
          }

          List<PurchaseReceiptPlan> listDate = PurchaseReceiptPlanService.getShipmentDate(main, getPurchaseOrder());
          for (int i = 0; i < listDate.size(); i++) {
            if (i == 0) {
              setDatePlan1(listDate.get(i).getExpectedDate());
            } else if (i == 1) {
              setDatePlan2(listDate.get(i).getExpectedDate());
            } else if (i == 2) {
              setDatePlan3(listDate.get(i).getExpectedDate());
            }
          }
        } else if (ViewType.list.toString().equals(viewType)) {
          setPurchaseReqAvailable(false);
          getPurchaseReqList(main);
          setAccount(null);
          setContract(null);
          setActiveIndex(0);
          setFilterValue(null);
          loadPurchaseOrderList(main);
          setSelectedPurchaseReq(null);
          setPurchaseReqSelectedArray(null);
          main.getPageData().setSearchKeyWord(null);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void resetList() {
    purchaseOrderItemList = null;
    purchaseReqItemList = null;
    selectedPurchaseReq = null;
    purchaseReqList = null;
    setPurchaseOrderItemList(null);
    setPurchaseReqSelectedArray(null);
  }

  /**
   * Create purchaseOrderLazyModel.
   *
   * @param main
   */
  private void loadPurchaseOrderList(final MainView main) {
    if (purchaseOrderLazyModel == null) {
      purchaseOrderLazyModel = new LazyDataModel<PurchaseOrder>() {
        private List<PurchaseOrder> list;

        @Override
        public List<PurchaseOrder> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getAccount() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = PurchaseOrderService.listPagedPurchaseOrderByAccount(main, UserRuntimeView.instance().getAccountCurrent(), getDataTableFilters());
              main.commit(purchaseOrderLazyModel, first, pageSize);
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
        public Object getRowKey(PurchaseOrder purchaseOrder) {
          return purchaseOrder.getId();
        }

        @Override
        public PurchaseOrder getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseOrder obj : list) {
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
    String SUB_FOLDER = "scm_purchase_order/";
  }

  /**
   *
   * @return
   */
  private boolean isPurchaseOrderConfirmable() {
    boolean rvalue = false;
    if (getPurchaseOrderItemList() != null) {
      for (PurchaseOrderItem purchaseOrderItem : getPurchaseOrderItemList()) {
        if (purchaseOrderItem.getProductId() != null && purchaseOrderItem.getProductBatchId() != null && purchaseOrderItem.getQtyRequired() != null && purchaseOrderItem.getQtyRequired() > 0) {
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
  public String savePurchaseOrder(MainView main, Integer status) {
    PurchaseOrderStatus pos = null;
    if (SystemConstants.DRAFT.equals(status)) {
      pos = new PurchaseOrderStatus();
      pos.setId(status);
    } else if (SystemConstants.CONFIRMED.equals(status)) {
      if (!isPurchaseOrderConfirmable()) {
        main.error("error.product.missing");
        return null;
      }
      pos = new PurchaseOrderStatus();
      pos.setId(status);
    }
    getPurchaseOrder().setPurchaseOrderStatusId(pos);
    return saveOrClonePurchaseOrder(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseOrder(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseOrder(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseOrder(MainView main, String key) {
    boolean isNew = false;
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseOrderService.insertOrUpdate(main, getPurchaseOrder());
            PurchaseOrderItemService.insertOrUpdatePurchaseOrderItem(main, getPurchaseOrder(), getPurchaseOrderItemList());
            PurchaseOrderItemService.updatePurchaseRequisitionStatus(main, getPurchaseOrder());
            break;
          case "clone":
            PurchaseOrderService.clone(main, getPurchaseOrder());
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
   * Delete one or many PurchaseOrder.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseOrder(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseOrderSelected)) {
        PurchaseOrderService.deleteByPkArray(main, getPurchaseOrderSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseOrderSelected = null;
      } else {
        PurchaseOrderService.deleteByPk(main, getPurchaseOrder());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())) {
          getPurchaseOrder().reset();
          resetList();
          main.setViewType(ViewTypes.newform);
        }
        setPurchaseOrderItemList(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  public List<PurchaseReq> getPurchaseReqList(MainView main) {
    if (purchaseReqList == null) {
      purchaseReqList = new ArrayList<>();
    }
    try {
      if (getAccount() != null) {
        purchaseReqList = ScmLookupExtView.purchaseReqByPoOrderItem(getAccount().getId(), SystemConstants.CONFIRMED, SystemConstants.CONFIRMED_PARTIALYY_PROCESSED);
        if (purchaseReqList != null && !purchaseReqList.isEmpty()) {
          setPurchaseReqAvailable(true);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return purchaseReqList;
  }

  public PurchaseReq getPurchaseReq() {
    return purchaseReq;
  }

  public void setPurchaseReq(PurchaseReq purchaseReq) {
    this.purchaseReq = purchaseReq;
  }

  public List<PurchaseOrderItem> getPurchaseOrderItemList() {
    if (purchaseOrderItemList == null) {
      purchaseOrderItemList = new ArrayList<>();
    }
    return purchaseOrderItemList;
  }

  /**
   *
   */
  private void addNewPurchaseOrderItem() {
    PurchaseOrderItem reqItem = new PurchaseOrderItem();
    getPurchaseOrderItemList().add(0, reqItem);
  }

  /**
   *
   * @param main
   */
  public void actionInsertOrUpdatePurchaseOrderItem(MainView main) {
    boolean isNew = false;
    try {
      if (isDraft()) {
        Integer rowIndex = Jsf.getParameterInt("rownumber");
        PurchaseOrderItem purchaseOrderItem = getPurchaseOrderItemList().get(rowIndex);
        if (purchaseOrderItem.getProductId() != null && purchaseOrderItem.getProductBatchId() != null && purchaseOrderItem.getQtyRequired() != null && purchaseOrderItem.getOrderPricePerPiece() != null) {
          if (purchaseOrderItem.getId() == null) {
            isNew = true;
          }

          getTaxCalculator().processPurchaseOrderCalculation(getPurchaseOrder(), getPurchaseOrderItemList());

          PurchaseOrderService.insertOrUpdate(main, getPurchaseOrder());
          purchaseOrderItem.setPurchaseOrderId(getPurchaseOrder());
          PurchaseOrderItemService.insertOrUpdatePurchaseOrderItem(main, getPurchaseOrder(), getPurchaseOrderItemList());
          PurchaseOrderItemService.updatePurchaseRequisitionStatus(main, getPurchaseOrder());

          if (isNew) {
            addNewPurchaseOrderItem();
          } else if (rowIndex == 0) {
            addNewPurchaseOrderItem();
          }
        }

        for (PurchaseOrderItem poi : getPurchaseOrderItemList()) {
          if (poi.getProductId() != null) {
            getTaxCalculator().updatePurchaseOrderItem(main, getAccount(), poi);
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
   *
   * @param purchaseOrderItem
   */
  public void productSelectEvent(PurchaseOrderItem purchaseOrderItem) {
    if (purchaseOrderItem != null && purchaseOrderItem.getProductId() != null) {
      MainView main = Jsf.getMain();
      try {
        ProductQuantityInfo pqi = PurchaseReqService.selectProductQuantityInfo(main, getAccount(), purchaseOrderItem.getProductId());
        if (pqi != null) {
          purchaseOrderItem.setLastLandingPricePerPiece(pqi.getProductRate());
          purchaseOrderItem.setQtySuggested(pqi.getSuggestedQty());
        }
//        if (list != null && !list.isEmpty() && list.size() == 1) {
//          purchaseOrderItem.setProductSummary(list.get(0));
//          getTaxCalculator().updatePurchaseOrderItem(main, getAccount(), purchaseOrderItem);
//        } else {
//          purchaseOrderItem.setProductSummary(new ProductSummary(purchaseOrderItem.getProductId().getId(), null, purchaseOrderItem.getProductId().getProductName()));
//          getTaxCalculator().updatePurchaseOrderItem(main, getAccount(), purchaseOrderItem);
//        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  /**
   *
   * @param event
   */
  public void productBatchSelectEvent(SelectEvent event) {
    ProductSummary ps = (ProductSummary) event.getObject();
    MainView main = Jsf.getMain();
    try {
      PurchaseOrderItem purchaseOrderItem = (PurchaseOrderItem) Jsf.getAttribute("purchaseOrderItem");
      if (purchaseOrderItem != null) {
        purchaseOrderItem.setProductSummary(ps);
        purchaseOrderItem.setOrderPricePerPiece(null);
        getTaxCalculator().updatePurchaseOrderItem(main, getAccount(), purchaseOrderItem);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param productOrderItem
   */
  public void totalBlurEvent(PurchaseOrderItem productOrderItem) {
    if (productOrderItem != null) {
      if (productOrderItem.getQtyRequired() != null && productOrderItem.getOrderPricePerPiece() != null) {
        productOrderItem.setNetValue(productOrderItem.getQtyRequired() * productOrderItem.getOrderPricePerPiece());
      } else {
        productOrderItem.setNetValue(null);
      }
      getTaxCalculator().processPurchaseOrderCalculation(getPurchaseOrder(), getPurchaseOrderItemList());
    }
  }

  /**
   *
   * @param main
   * @param purchaseOrderItem
   */
  public void actionDeletePurchaseOrderItem(MainView main, PurchaseOrderItem purchaseOrderItem) {
    try {
      if (purchaseOrderItem != null && purchaseOrderItem.getId() == null) {
        getPurchaseOrderItemList().remove(purchaseOrderItem);
      } else if (purchaseOrderItem != null && purchaseOrderItem.getId() != null) {
        PurchaseOrderItemService.deleteByPk(main, purchaseOrderItem);
        getPurchaseOrderItemList().remove(purchaseOrderItem);
      }
      if (getPurchaseOrderItemList().isEmpty()) {
        addNewPurchaseOrderItem();
      }
      getTaxCalculator().processPurchaseOrderCalculation(getPurchaseOrder(), getPurchaseOrderItemList());
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }

  }

  public void setPurchaseOrderItemList(List<PurchaseOrderItem> purchaseOrderItemList) {
    this.purchaseOrderItemList = purchaseOrderItemList;
  }

  private List<PurchaseOrderItem> selectPurchaseReqItem(MainView main) {
    setRequisitionProducts(null);
    StringBuilder purchaseReqIds = new StringBuilder();

    List<PurchaseReqItem> purchaseReqItems = null;
    List<PurchaseOrderItem> purchaseOrdertems = null;
    if (getPurchaseReqSelectedArray() != null && getPurchaseReqSelectedArray().length > 0) {
      setSelectedPurchaseReq(Arrays.asList(getPurchaseReqSelectedArray()));
    }
    if (getSelectedPurchaseReq() != null && !getSelectedPurchaseReq().isEmpty()) {
      /**
       * Creating a comma separated string of purchase requisition id.
       */
      for (PurchaseReq pr : getSelectedPurchaseReq()) {
        if (SystemConstants.CONFIRMED.equals(pr.getPurchaseRequisitionStatusId().getId()) || SystemConstants.CONFIRMED_PARTIALYY_PROCESSED.equals(pr.getPurchaseRequisitionStatusId().getId())) {
          if (purchaseReqIds.length() == 0) {
            purchaseReqIds.append(pr.getId());
          } else {
            purchaseReqIds.append(",").append(pr.getId());
          }
        }
      }

      try {
        if (purchaseReqIds.length() > 0) {
          purchaseReqItems = PurchaseReqItemService.selectPurchaseReqItemByPurchaseReq(main, purchaseReqIds.toString());
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }

      /**
       * Create a list of purchase order item from purchase requisition item
       */
      purchaseOrdertems = new ArrayList<>();
      PurchaseOrderItem purchaseOrderItem;
      if (purchaseReqItems != null) {
        for (PurchaseReqItem pri : purchaseReqItems) {
          purchaseOrderItem = new PurchaseOrderItem();
          if (pri.getProductDetailId() == null) {
            purchaseOrderItem.setProductBatchId(ProductService.selectSingleProductBatchByProduct(main, getAccount(), pri.getProductId()));
          }
          if (purchaseOrderItem.getProductBatchId() != null) {
            purchaseOrderItem.setProductId(pri.getProductId());
            purchaseOrderItem.setPurchaseReqItemId(pri);
            purchaseOrderItem.setTaxCode(PurchaseOrderItemService.getProductTaxCode(main, purchaseOrderItem.getProductBatchId()));

            purchaseOrderItem.setQtyRequired(pri.getQtyRequired());

            ProductQuantityInfo pqi = PurchaseReqService.selectProductQuantityInfo(main, getAccount(), purchaseOrderItem.getProductId());
            if (pqi != null) {
              purchaseOrderItem.setLastLandingPricePerPiece(pqi.getProductRate());
              purchaseOrderItem.setQtySuggested(pqi.getSuggestedQty());
            }
            purchaseOrderItem.setProductSummary(new ProductSummary(pri.getProductId().getId(), pri.getProductId().getProductName()));
            purchaseOrdertems.add(purchaseOrderItem);
          }
        }
      }
      for (PurchaseOrderItem poi : purchaseOrdertems) {
        poi.setProductSummary(new ProductSummary(poi.getProductId().getId(), poi.getProductBatchId() != null ? poi.getProductBatchId().getId() : null,
                poi.getProductId().getProductName()));
        getTaxCalculator().updatePurchaseOrderItem(main, getAccount(), poi);
      }
      if (purchaseOrdertems != null && !purchaseOrdertems.isEmpty()) {
        getTaxCalculator().processPurchaseOrderCalculation(getPurchaseOrder(), purchaseOrdertems);
      }
    }
    return purchaseOrdertems;
  }

  public void switchToShipment() {
    changeTab = 1;
  }

  public boolean isIsNew() {
    return isNew;
  }

  public void setIsNew(boolean isNew) {
    this.isNew = isNew;
  }

  /**
   * Creating purchase Req
   *
   * @param event
   */
  public void createPurchaseOrder(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      Account a = (Account) event.getObject();
      UserRuntimeView.instance().setAccount(a);
      UserRuntimeView.instance().changeAccount(main, a);
      newPurchaseOrder(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public String newPurchaseOrder(MainView main) {
    try {
      if (UserRuntimeView.instance().getAccount() == null) {
        //main.error("account.required");
        main.setViewType(ViewTypes.list);
        return null;
      } else if (UserRuntimeView.instance().getContract() == null) {
        //main.error("contract.required");
        main.setViewType(ViewTypes.list);
        return null;
      } else {
        getPurchaseOrder().reset();
        getPurchaseOrder().setAccountId(getAccount());
        getPurchaseOrder().setContractId(getContract());
        if (AccountService.INDIVIDUAL_SUPPLIER.equals(getAccount().getPurchaseChannel())) {
          getPurchaseOrder().setPurchaseOrderNo("DR-" + AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getAccount(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, true, getPurchaseOrder().getFinancialYearId()));
        } else {
          getPurchaseOrder().setPurchaseOrderNo("DR-" + AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getPrimaryAccountId(), PrefixTypeService.PURCHASE_ORDER_INVOICE_PREFIX_ID, true, getPurchaseOrder().getFinancialYearId()));
        }
        getPurchaseOrder().setPurchaseOrderDate(new Date());
        PurchaseOrderStatus pos = new PurchaseOrderStatus();
        pos.setId(SystemConstants.DRAFT);
        getPurchaseOrder().setPurchaseOrderStatusId(pos);
        main.setViewType(ViewTypes.newform);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public boolean isDraft() {
    return (getPurchaseOrder().getPurchaseOrderStatusId() != null && SystemConstants.DRAFT.equals(getPurchaseOrder().getPurchaseOrderStatusId().getId()));
  }

  public boolean isConfirmed() {
    return (getPurchaseOrder().getPurchaseOrderStatusId() != null && SystemConstants.CONFIRMED.equals(getPurchaseOrder().getPurchaseOrderStatusId().getId()));
  }

  public boolean isConfirmedAndPlanned() {
    return (getPurchaseOrder().getPurchaseOrderStatusId() != null && SystemConstants.CONFIRMED_AND_PLANNED.equals(getPurchaseOrder().getPurchaseOrderStatusId().getId()));
  }

  public void consignmentPODialogClose() {
    Jsf.popupReturn(null, null);
  }

  public String insertConsignmentPO(MainView main, Consignment consignment) {
    try {
      ConsignmentReferenceService.insertArray(main, getPurchaseOrderSelected(), consignment);
      main.commit("success.select");
    } catch (Throwable t) {
      main.rollback(t, "error.list");
    } finally {
      main.close();
    }
    return null;
  }

  public void filterEventHandler(SelectEvent eve) {
    String filterKey = (String) Jsf.getAttribute("filterKey");
    if (eve.getObject() == null) {
      getDataTableFilters().remove(filterKey);
    } else {
      getDataTableFilters().put(filterKey, eve.getObject());
    }
  }

  public List<PurchaseShipmentPlan> getPurchaseShipmentPlanList(MainView main) {
    PurchaseShipmentPlan shipPlan;
    if (StringUtil.isEmpty(purchaseShipmentPlanList)) {
      try {
        if (PurchaseReceiptPlanService.getShipmentCount(main, getPurchaseOrder()) == 0) {
          purchaseShipmentPlanList = new ArrayList<>();
          if (purchaseOrderItemList != null) {
            for (PurchaseOrderItem poItemlist : purchaseOrderItemList) {
              if (poItemlist.getProductId() != null) {
                shipPlan = new PurchaseShipmentPlan();
                shipPlan.setQty1(poItemlist.getQtyRequired());
                shipPlan.setProductName(poItemlist.getProductId().getProductName());
                shipPlan.setQtyRequired(poItemlist.getQtyRequired());
                shipPlan.setPurchaseOrderId(getPurchaseOrder().getId());
                shipPlan.setId(poItemlist.getId());
                setDatePlan1(new Date());
                purchaseShipmentPlanList.add(shipPlan);
              }
            }
          }
        } else {
          purchaseShipmentPlanList = PurchaseOrderService.getPurchaseShipmentPlanList(main, getPurchaseOrder());
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return purchaseShipmentPlanList;
  }

  public void shipmentPlan(int split) {
    setSplit1(0);
    setSplit2(0);
    setSplit3(0);
    for (PurchaseShipmentPlan plan : purchaseShipmentPlanList) {
      int balQty = 0;
      int totQty = 0;
      if (split > 0) {
        plan.setQty1(null);
        plan.setQty2(null);
        plan.setQty3(null);
      }
      if (split < 2) {
        setDatePlan2(null);
      }
      if (split < 3) {
        setDatePlan3(null);
      }
      if (split >= 1 && plan.getQtyRequired() != null) {
        plan.setQty1(plan.getQtyRequired() / split);
      }
      if (split >= 2 && plan.getQtyRequired() != null) {
        plan.setQty2(plan.getQtyRequired() / split);
      }
      if (split >= 3 && plan.getQtyRequired() != null) {
        plan.setQty3(plan.getQtyRequired() / split);
      }
      if (plan.getQty3() != null) {
        totQty = plan.getQty1() + plan.getQty2() + plan.getQty3();
      } else if (plan.getQty2() != null) {
        totQty = plan.getQty1() + plan.getQty2();
      } else if (plan.getQty1() != null) {
        totQty = plan.getQty1();
      }
      if (plan.getQtyRequired() != null && totQty < plan.getQtyRequired()) {
        balQty = plan.getQtyRequired() - totQty;
      }
      if (plan.getQty3() != null) {
        plan.setQty3(plan.getQty3() + balQty);
      } else if (plan.getQty2() != null) {
        plan.setQty2(plan.getQty2() + balQty);
      }
    }
  }

  public String saveShipmentPlan(MainView main, int statusFlag) {
    try {
      boolean flag = false;
      boolean dateFlag = false;
      for (PurchaseShipmentPlan plan : purchaseShipmentPlanList) {

        int totQty = 0;
        int balQty = 0;
        if (plan.getQty3() != null) {
          totQty = plan.getQty1() + plan.getQty2() + plan.getQty3();
          flag = true;
        } else if (plan.getQty2() != null) {
          totQty = plan.getQty1() + plan.getQty2();
          flag = true;
        } else if (plan.getQty1() != null) {
          totQty = plan.getQty1();
          flag = true;
        }

        if (plan.getQtyRequired() < totQty) {
          balQty = totQty - plan.getQtyRequired();
          if (plan.getQty3() != null) {
            plan.setQty3(plan.getQty3() - balQty);
          } else if (plan.getQty2() != null) {
            plan.setQty2(plan.getQty2() - balQty);
          } else if (plan.getQty1() != null) {
            plan.setQty1(plan.getQty1() - balQty);
          }
        }

        if (plan.getQtyRequired() > totQty) {
          balQty = plan.getQtyRequired() - totQty;
          if (plan.getQty3() != null) {
            plan.setQty3(plan.getQty3() + balQty);
          } else if (plan.getQty2() != null) {
            plan.setQty2(plan.getQty2() + balQty);
          } else if (plan.getQty1() != null) {
            plan.setQty1(plan.getQty1() + balQty);
          }
        }
        if (plan.getQty1() != null && getDatePlan1() == null) {
          dateFlag = true;
        }
        if (plan.getQty2() != null && getDatePlan2() == null) {
          dateFlag = true;
        }
        if (plan.getQty3() != null && getDatePlan3() == null) {
          dateFlag = true;
        }
        if (plan.getQty1() == null && getDatePlan1() != null) {
          setDatePlan1(null);
        }
        if (plan.getQty2() == null && getDatePlan2() != null) {
          setDatePlan2(null);
        }
        if (plan.getQty3() == null && getDatePlan3() != null) {
          setDatePlan3(null);
        }
      }
      if (flag == false) {
        main.error("select.split");
        return null;
      }

      if (dateFlag == true) {
        main.error("select.date");
        return null;
      }

      Date toDate = new Date();
      int num = 0;
      if (getDatePlan1() != null) {
        num = getDatePlan1().compareTo(toDate);
        if (num < 0 || num == 0) {
          main.error("select.date1");
          return null;
        }
      }

      if (getDatePlan2() != null) {
        num = getDatePlan2().compareTo(toDate);
        if (num < 0 || num == 0) {
          main.error("select.date2");
          return null;
        }
      }

      if (getDatePlan3() != null) {
        num = getDatePlan3().compareTo(toDate);
        if (num < 0 || num == 0) {
          main.error("select.date3");
          return null;
        }
      }

      PurchaseReceiptPlanService.insertOrUpdate(main, purchaseShipmentPlanList, datePlan1, datePlan2, datePlan3);
      if (statusFlag == 2) {
        PurchaseReceiptPlanService.updatePurchaseOrderStatus(main, getPurchaseOrder());
      }
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public boolean isSplitPlan1() {
    return split1 == 1;
  }

  public boolean isSplitPlan2() {
    return split2 == 2;
  }

  public boolean isSplitPlan3() {
    return split3 == 3;
  }

  /**
   *
   * @param main
   * @param purchaseOrderItem
   * @return
   */
  public List<ProductSummary> completeProductDetail(MainView main, PurchaseOrderItem purchaseOrderItem) {
    List<ProductSummary> list = null;
    try {
      if (purchaseOrderItem != null && purchaseOrderItem.getProductId() != null) {
        list = getTaxCalculator().productSummaryForPurchaseOrder(main, getAccount(), purchaseOrderItem.getProductId().getProductName(), getPurchaseOrder().getId(), purchaseOrderItem.getId());
        if (list != null && !list.isEmpty() && purchaseOrderItem.getProductSummary() == null) {
          purchaseOrderItem.setProductSummary(list.get(0));
          getTaxCalculator().updatePurchaseOrderItem(main, getAccount(), purchaseOrderItem);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return list;
  }

//  public List<ProductSummary> completeProductDetail(String filter) {
//    List<ProductSummary> list = null;
//    List<ProductSummary> productSummeryList = new ArrayList<>();
//    MainView main = Jsf.getMain();
//    try {
//    PurchaseOrderItem purchaseOrderItem = (PurchaseOrderItem) Jsf.getAttribute("productOrderItem");
//    list = getTaxCalculator().productSummaryForPurchaseOrder(main, getAccount(), purchaseOrderItem.getProductId().getProductName(), getPurchaseOrder().getId(), purchaseOrderItem.getId());
//    } catch (Throwable t) {
//      main.rollback(t, "error.select");
//    } finally {
//      main.close();
//    }
//    return productSummeryList;
//  }
  /**
   * Method to select products based on its account group.
   *
   * @param filter
   * @return
   */
  public List<Product> completeProduct(String filter) {
    return ScmLookupExtView.selectProductForPurchaseOrder(filter, getAccount().getId(), getCompany().getId());
  }
}
