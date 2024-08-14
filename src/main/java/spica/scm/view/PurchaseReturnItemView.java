/*
 * @(#)PurchaseReturnItemView.java	1.0 Wed May 25 13:23:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.RelatedItems;
import spica.scm.common.ReturnItem;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.PurchaseReturnItem;
import spica.scm.service.PurchaseReturnItemService;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductType;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.domain.PurchaseReturnItemStatus;
import spica.scm.domain.StockSaleable;
import spica.scm.service.ProductDetailService;
import spica.scm.service.ProductEntryDetailService;
import spica.scm.service.ProductService;
import spica.scm.service.PurchaseReturnItemstatusService;
import spica.scm.service.PurchaseReturnStockCatService;
import spica.scm.service.StockStatusService;
import spica.scm.tax.TaxCalculator;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * PurchaseReturnItemView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed May 25 13:23:25 IST 2016
 */
@Named(value = "purchaseReturnItemView")
@ViewScoped
public class PurchaseReturnItemView implements Serializable {

  private transient PurchaseReturnItem purchaseReturnItem;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseReturnItem> purchaseReturnItemLazyModel; 	//For lazy loading datatable.
  private transient PurchaseReturnItem[] purchaseReturnItemSelected;	 //Selected Domain Array

  private transient PurchaseReturn purchaseReturn;
  private transient TaxCalculator taxCalculator;

  private transient List<ReturnItem> purchaseReturnStockItemList;
  private transient ReturnItem[] purchaseReturnStockItem;
  private transient List<ReturnItem> stockItemList;

  private transient Product productId;
  private transient ProductBatch batch;
  private transient Double quantityReturn;
  private transient Double quantityAvailable;
  private transient Integer selectedItemsNo;
  private transient boolean stockType;
  private transient boolean productTypeEditable;
  private boolean loadable;
  private List<RelatedItems> relatedItemsList;

  private Map<String, Object> dataTableFilters;

  @PostConstruct
  public void init() {
    purchaseReturn = (PurchaseReturn) Jsf.dialogParent(PurchaseReturn.class);
    if (purchaseReturn.getTaxProcessorId() != null) {
      taxCalculator = (SystemRuntimeConfig.getTaxCalculator(purchaseReturn.getTaxProcessorId().getProcessorClass()));
    }
  }

  /**
   * Default Constructor.
   */
  public PurchaseReturnItemView() {
    super();
  }

  /**
   * Return PurchaseReturnItem.
   *
   * @return PurchaseReturnItem.
   */
  public PurchaseReturnItem getPurchaseReturnItem() {
    if (purchaseReturnItem == null) {
      purchaseReturnItem = new PurchaseReturnItem();
    }
    return purchaseReturnItem;
  }

  /**
   * Set PurchaseReturnItem.
   *
   * @param purchaseReturnItem.
   */
  public void setPurchaseReturnItem(PurchaseReturnItem purchaseReturnItem) {
    this.purchaseReturnItem = purchaseReturnItem;
  }

  public PurchaseReturn getPurchaseReturn() {
      if (purchaseReturn == null) {
      purchaseReturn = new PurchaseReturn();
    }
     if (purchaseReturn.getCompanyId() == null) {
      purchaseReturn.setCompanyId(UserRuntimeView.instance().getCompany());
      purchaseReturn.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
      purchaseReturn.setAccountId(UserRuntimeView.instance().getAccount());
    }
    return purchaseReturn;
  }

  public void setPurchaseReturn(PurchaseReturn purchaseReturn) {
    this.purchaseReturn = purchaseReturn;
  }

  public List<ReturnItem> getPurchaseReturnStockItemList() {
    if (purchaseReturnStockItemList == null) {
      purchaseReturnStockItemList = new ArrayList<>();
    }
    return purchaseReturnStockItemList;
  }

  public void setPurchaseReturnStockItemList(List<ReturnItem> purchaseReturnStockItemList) {
    this.purchaseReturnStockItemList = purchaseReturnStockItemList;
  }

  public ReturnItem[] getPurchaseReturnStockItem() {
    return purchaseReturnStockItem;
  }

  public void setPurchaseReturnStockItem(ReturnItem[] purchaseReturnStockItem) {
    this.purchaseReturnStockItem = purchaseReturnStockItem;
  }

  public void setStockType(boolean stockType) {
    this.stockType = stockType;
  }

  public Product getProductId() {
    return productId;
  }

  public void setProductId(Product productId) {
    this.productId = productId;
  }

  public Double getQuantityReturn() {
    return quantityReturn;
  }

  public void setQuantityReturn(Double quantityReturn) {
    this.quantityReturn = quantityReturn;
  }

  public Double getQuantityAvailable() {
    return quantityAvailable;
  }

  public void setQuantityAvailable(Double quantityAvailable) {
    this.quantityAvailable = quantityAvailable;
  }

  public boolean isPurchaseReturnNonMovingAndNearExpiry() {
    if (getPurchaseReturn().getPurchaseReturnStockCat() != null) {
      if (getPurchaseReturn().getPurchaseReturnStockCat().getId() == PurchaseReturnStockCatService.STOCK_NON_MOVING_AND_NEAR_EXPIRY) {
        return true;
      }
    }
    return false;
  }

  public boolean isStockNonMovingAndNearExpiry() {
    if (getPurchaseReturn().getPurchaseReturnStockCat() != null) {
      return (getPurchaseReturn().getPurchaseReturnStockCat().getId() == PurchaseReturnStockCatService.STOCK_NON_MOVING_AND_NEAR_EXPIRY);
    } else {
      return false;
    }
  }

  public boolean isProductTypeEditable() {
    return productTypeEditable;
  }

  public void setProductTypeEditable(boolean productTypeEditable) {
    this.productTypeEditable = productTypeEditable;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPurchaseReturnItem(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        setProductId(null);
        setQuantityAvailable(null);
        setQuantityReturn(null);
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getPurchaseReturnItem().reset();
          if (getPurchaseReturn().getPurchaseReturnStockCat() != null && getPurchaseReturn().getPurchaseReturnStockCat().getId().equals(SystemConstants.DAMAGED_AND_EXPIRED)) {
//            actionLoadPurchaseRetunStockItems(main);
          } else {
//            if (PurchaseReturnItemService.isPurchaseReturnItemExist(main, getPurchaseReturn())) {
//              setProductTypeEditable(false);
//            } 
            if (getPurchaseReturn().getPurchaseReturnItemReplicaList() != null && !getPurchaseReturn().getPurchaseReturnItemReplicaList().isEmpty()) {
              setProductTypeEditable(false);
            } else {
              setProductTypeEditable(true);
            }
          }
        } else if (ViewType.editform.toString().equals(viewType)) {
          setPurchaseReturnItem((PurchaseReturnItem) PurchaseReturnItemService.selectByPk(main, getPurchaseReturnItem()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPurchaseReturnItemList(main);
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
   * Create purchaseReturnItemLazyModel.
   *
   * @param main
   */
  private void loadPurchaseReturnItemList(final MainView main) {
    if (purchaseReturnItemLazyModel == null) {
      purchaseReturnItemLazyModel = new LazyDataModel<PurchaseReturnItem>() {
        private List<PurchaseReturnItem> list;

        @Override
        public List<PurchaseReturnItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PurchaseReturnItemService.listPaged(main);
            main.commit(purchaseReturnItemLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PurchaseReturnItem purchaseReturnItem) {
          return purchaseReturnItem.getId();
        }

        @Override
        public PurchaseReturnItem getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReturnItem obj : list) {
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
    String SUB_FOLDER = "scm_purchase_return_item/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePurchaseReturnItem(MainView main) {
    return saveOrClonePurchaseReturnItem(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseReturnItem(MainView main) {
    main.setViewType("newform");
    return saveOrClonePurchaseReturnItem(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseReturnItem(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PurchaseReturnItemService.insertOrUpdate(main, getPurchaseReturnItem());
            break;
          case "clone":
            PurchaseReturnItemService.clone(main, getPurchaseReturnItem());
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
   * Delete one or many PurchaseReturnItem.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseReturnItem(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(purchaseReturnItemSelected)) {
        PurchaseReturnItemService.deleteByPkArray(main, getPurchaseReturnItemSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseReturnItemSelected = null;
      } else {
        PurchaseReturnItemService.deleteByPk(main, getPurchaseReturnItem());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PurchaseReturnItem.
   *
   * @return
   */
  public LazyDataModel<PurchaseReturnItem> getPurchaseReturnItemLazyModel() {
    return purchaseReturnItemLazyModel;
  }

  /**
   * Return PurchaseReturnItem[].
   *
   * @return
   */
  public PurchaseReturnItem[] getPurchaseReturnItemSelected() {
    return purchaseReturnItemSelected;
  }

  /**
   * Set PurchaseReturnItem[].
   *
   * @param purchaseReturnItemSelected
   */
  public void setPurchaseReturnItemSelected(PurchaseReturnItem[] purchaseReturnItemSelected) {
    this.purchaseReturnItemSelected = purchaseReturnItemSelected;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void purchaseReturnItemPopupClose() {
    Jsf.popupReturn(getPurchaseReturn(), null);
  }

  /**
   * Return Lookup/Reference List of ProductType.
   * <pre>
   * {@code
   * Recommended usage.
   *
   * When records are less.
   *
   * productType();
   *
   * When records are huge.
   *
   * <p:autoComplete value="#{parentView.parent.productType}" completeMethod="#{ScmLookupSql.productTypeAuto}" converter="wawo.LookupIntConverter" >
   * <f:attribute name="clazz" value="#{ScmLookupSql.productTypeClass()}"  />
   * <f:attribute name="byid" value="#{ScmLookupSql.productTypeById()}"  />
   * </p:autoComplete>
   *
   * }
   * </pre>
   *
   * @param main
   * @return
   * @see .ScmLookupSql.PRODUCT_TYPE
   */
  /**
   *
   * @return
   */
  public List<ProductType> productType() {
    List<ProductType> list = null;
    if (getPurchaseReturn().getAccountId() != null && getPurchaseReturn().getAccountId().getId() != null) {
      list = ScmLookupExtView.lookupProductTypeByAccount(getPurchaseReturn().getAccountId().getId());
    }
    return list;
  }

  /**
   * Product autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Product> productAuto(String filter) {
    return ScmLookupExtView.lookupProductByAccount(filter, getPurchaseReturn().getAccountId());
  }

  /**
   *
   * @param main
   */
  public void actionLoadPurchaseRetunStockItems(MainView main) {
    try {
      if (getPurchaseReturn().getPurchaseReturnStockCat() != null) {
        if (PurchaseReturnStockCatService.STOCK_DAMAGED_AND_EXPIRED == getPurchaseReturn().getPurchaseReturnStockCat().getId()) {
          List<ReturnItem> returnItemList = taxCalculator.getDamagedAndExpiredProductsForPurchaseReturn(main, getProductId(), getPurchaseReturn().getId(), getPurchaseReturn().getAccountId().getId(), null, getPurchaseReturn().getPurchaseReturnStockCat().getId(), getPurchaseReturn().getEntryDate());
          setPurchaseReturnStockItemList(updateReturnItemsReturnQty(main, getPurchaseReturn().getPurchaseReturnItemReplicaList(), returnItemList));
        } else if (PurchaseReturnStockCatService.STOCK_NON_MOVING_AND_NEAR_EXPIRY == (getPurchaseReturn().getPurchaseReturnStockCat().getId())) {
          List<ReturnItem> returnItemList = taxCalculator.getNonMovingProductsForPurchaseReturn(main, getProductId(), getPurchaseReturn().getId(), getPurchaseReturn().getAccountId().getId(), "GL", getPurchaseReturn().getPurchaseReturnStockCat().getId(), getPurchaseReturn().getEntryDate());
          setPurchaseReturnStockItemList(updateReturnItemsReturnQty(main, getPurchaseReturn().getPurchaseReturnItemReplicaList(), returnItemList));
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private List<ReturnItem> updateReturnItemsReturnQty(MainView main, List<PurchaseReturnItemReplica> purchaseReturnItemList, List<ReturnItem> returnItemList) {
    List<ReturnItem> list = new ArrayList<ReturnItem>();
    if (purchaseReturnItemList != null) {
      for (ReturnItem returnItem : returnItemList) {
        returnItem.after();
        if (returnItem.getProductDetailId() != null) {
          ProductDetail pd = ProductDetailService.selectByPk(main, new ProductDetail(returnItem.getProductDetailId()));
          returnItem.setProductName(pd.getProductBatchId().getProductId().getProductName());
        }

        for (PurchaseReturnItemReplica prItem : purchaseReturnItemList) {
          prItem.after();
          if (returnItem.getInvoiceProductKey().equals(prItem.getInvoiceProductKey())) {
            list.add(returnItem);
            returnItem.setQty(prItem.getQuantityReturned());
            break;
          }
        }
      }
      if (list != null && !list.isEmpty()) {
        setPurchaseReturnStockItem(list.toArray(new ReturnItem[list.size()]));
      }

    }
    return returnItemList;
  }

  /**
   *
   */
  public void stockTypeValueChangeListener() {
    resetPurchaseReturnStockItems();
    if (getPurchaseReturn().getPurchaseReturnStockType() != null && getPurchaseReturn().getPurchaseReturnStockType().getId() != null) {
      stockType = getPurchaseReturn().getPurchaseReturnStockType().getId() == 1;
    } else {
      stockType = false;
    }
  }

  /**
   *
   * @param event
   */
  public void listenerCommodityItemSelect(SelectEvent event) {
    resetPurchaseReturnStockItems();
  }

  /**
   *
   */
  public void productTypeSelectEventHandler(SelectEvent event) {
    ProductType pt = (ProductType) event.getObject();
    if (pt != null) {
//      getPurchaseReturn().setProductTypeId(pt);
      resetPurchaseReturnStockItems();
    }
  }

  /**
   *
   */
  private void resetPurchaseReturnStockItems() {
    setPurchaseReturnStockItemList(null);
    setPurchaseReturnStockItem(null);
  }

  private PurchaseReturnItemStatus getPurchaseReturnItemStatus(Integer stockStatusId) {
    PurchaseReturnItemStatus itemStatus = new PurchaseReturnItemStatus();
    if (stockStatusId.equals(StockStatusService.STOCK_STATUS_DAMAGED) || stockStatusId.equals(StockStatusService.STOCK_STATUS_BLOCKED_DAMAGED)) {
      itemStatus.setId(PurchaseReturnItemstatusService.DAMAGED);
      itemStatus.setTitle(PurchaseReturnItemstatusService.STATUS_DAMAGED);
    } else if (stockStatusId.equals(StockStatusService.STOCK_STATUS_EXPIRED) || stockStatusId.equals(StockStatusService.STOCK_STATUS_BLOCKED_EXPIRED)) {
      itemStatus.setId(PurchaseReturnItemstatusService.EXPIRED);
      itemStatus.setTitle(PurchaseReturnItemstatusService.STATUS_EXPIRED);
    }
    return itemStatus;
  }

  /**
   *
   * @param main
   */
  public void insertPurchaseReturnItems(MainView main) {
    List<PurchaseReturnItemReplica> purchaseReturnItemList = new ArrayList<>();
    try {
      if (getPurchaseReturnStockItem() != null) {
        if (isStockNonMovingAndNearExpiry()) {
          for (ReturnItem stockItems : getPurchaseReturnStockItem()) {
            purchaseReturnItemList.add(getSaleablePurchaseReturnItem(main, stockItems));
          }
        } else {
          for (ReturnItem stockItems : getPurchaseReturnStockItem()) {
            purchaseReturnItemList.add(getNonSaleablePurchaseReturnItem(main, stockItems));
          }
        }
      }
      if (!purchaseReturnItemList.isEmpty()) {
        if (getPurchaseReturn().getPurchaseReturnItemReplicaList() != null && !getPurchaseReturn().getPurchaseReturnItemReplicaList().isEmpty()) {
          getPurchaseReturn().setPurchaseReturnItemReplicaList(mergePurchaseReturnItemList(purchaseReturnItemList, getPurchaseReturn().getPurchaseReturnItemReplicaList()));
        } else {
          getPurchaseReturn().setPurchaseReturnItemReplicaList(purchaseReturnItemList);
        }
      }
      selectedItemsNo = purchaseReturnItemList.size();
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param selectedPurchaseReturnItemList
   * @param existingPurchaseReturnItemList
   * @return
   */
  private List<PurchaseReturnItemReplica> mergePurchaseReturnItemList(List<PurchaseReturnItemReplica> selectedPurchaseReturnItemList, List<PurchaseReturnItemReplica> existingPurchaseReturnItemList) {
    boolean productExist = false;
    for (PurchaseReturnItemReplica pr : selectedPurchaseReturnItemList) {
      pr.after();
      productExist = false;

      for (PurchaseReturnItemReplica pri : existingPurchaseReturnItemList) {
        pri.after();
        if (pr.getInvoiceProductKey().equals(pri.getInvoiceProductKey())) {
          pri.setQuantityReturned(pr.getQuantityReturned());
          productExist = true;
//          if (pr.getStockSaleableId() != null && pr.getStockSaleableId().getId() == pri.getStockSaleableId().getId()) {
//            pri.setQuantityReturned(pr.getQuantityReturned());
//            productExist = true;
//          } else if (pr.getStockNonSaleableId() != null && pr.getStockNonSaleableId().getId() == pri.getStockNonSaleableId().getId()) {
//            pri.setQuantityReturned(pr.getQuantityReturned());
//            productExist = true;
//          }
        }
      }

      if (!productExist) {
        existingPurchaseReturnItemList.add(pr);
      }
    }
    return existingPurchaseReturnItemList;
  }

  /**
   *
   * @param stockItems
   * @return
   */
  private PurchaseReturnItemReplica getSaleablePurchaseReturnItem(MainView main, ReturnItem stockItems) {
    PurchaseReturnItemReplica prItem = new PurchaseReturnItemReplica();

    StockSaleable stockSaleable = new StockSaleable();
    stockSaleable.setId(stockItems.getStockId());

    ProductDetail pd = new ProductDetail();
    pd.setId(stockItems.getProductDetailId());
    prItem.setProductDetail(ProductDetailService.selectByPk(main, pd));

    prItem.setPurchaseReturn(getPurchaseReturn());
    prItem.setQuantityReturned(stockItems.getQty());
    prItem.setActualQty(stockItems.getQuantityAvailable());
    prItem.setLandingPricePerPieceCompany(stockItems.getLandingRateCompany());
    prItem.setActualLandingPricePerPieceCompany(stockItems.getLandingRateCompany());
    prItem.setValueRate(stockItems.getValueRate());
    prItem.setActualValueRate(stockItems.getValueRate());
    prItem.setHsnCode(stockItems.getHsnCode());
    prItem.setRefSalesInvoiceId(stockItems.getRefSalesInvoiceId());

    ProductEntryDetail ped = new ProductEntryDetail();
    if (stockItems.getProductEntryDetailId() != null) {
      prItem.setProductEntryDetailId(stockItems.getProductEntryDetailId());
      ped.setId(stockItems.getProductEntryDetailId());
      prItem.setProductEntryDetail(ProductEntryDetailService.selectByPk(main, ped));
    }
    prItem.setTaxCode(ProductService.getProductPurchaseTaxCode(main, prItem.getProductDetail().getProductBatchId().getProductId()));

    prItem.setReferenceDate(stockItems.getReferenceDate());
    prItem.setReferenceNo(stockItems.getReferenceNo());
    PurchaseReturnItemStatus purchaseReturnItemStatus = new PurchaseReturnItemStatus();
    purchaseReturnItemStatus.setId(PurchaseReturnItemstatusService.NONMOVING);
    purchaseReturnItemStatus.setTitle(PurchaseReturnItemstatusService.STATUS_NONMOVING);
    prItem.setPurchaseReturnItemStatus(purchaseReturnItemStatus);

    //prItem.setCreatedBy(UserRuntimeView.instance().getLoginId());
    //prItem.setStockSaleableId(stockSaleable);
    prItem.setValueMrp(stockItems.getMrpValue());
    prItem.setProductKey(stockItems.getProductKey());
    prItem.setProductEntryId(stockItems.getProductEntryId());
    prItem.setPackSize(stockItems.getPackSize());
    return prItem;
  }

  /**
   *
   * @param main
   * @param stockItems
   * @return
   */
  private PurchaseReturnItemReplica getNonSaleablePurchaseReturnItem(MainView main, ReturnItem stockItems) {
    PurchaseReturnItemReplica prItem = new PurchaseReturnItemReplica();

//    if (stockItems.getStockId() != null) {
//      StockNonSaleable stockNonSaleable = new StockNonSaleable();
//      stockNonSaleable.setId(stockItems.getStockId());
//      stockNonSaleable.setAccountId(getPurchaseReturn().getAccountId());
//      stockNonSaleable.setCreatedBy(UserRuntimeView.instance().getLoginId());
//      prItem.setStockNonSaleableId(stockNonSaleable);
//    }
    ProductDetail pd = new ProductDetail();
    pd.setId(stockItems.getProductDetailId());
    prItem.setProductDetail(ProductDetailService.selectByPk(main, pd));

    prItem.setTaxCode(ProductService.getProductPurchaseTaxCode(main, prItem.getProductDetail().getProductBatchId().getProductId()));
    prItem.setProductEntryDetailId(stockItems.getProductEntryDetailId());
//    prItem.setProductEntryDetailId(ProductEntryDetailService.selectByPk(main, new ProductEntryDetail(stockItems.getProductEntryDetailId())));
    prItem.setPurchaseReturn(getPurchaseReturn());
    prItem.setQuantityReturned(stockItems.getQty());
    prItem.setActualQty(stockItems.getQuantityAvailable());
    prItem.setLandingPricePerPieceCompany(stockItems.getLandingRateCompany());
    prItem.setActualLandingPricePerPieceCompany(stockItems.getLandingRateCompany());
    prItem.setValueRate(stockItems.getValueRate());
    prItem.setActualValueRate(stockItems.getValueRate());
    prItem.setHsnCode(stockItems.getHsnCode());
    prItem.setProductEntryId(stockItems.getProductEntryId());
    prItem.setPurchaseReturnItemStatus(getPurchaseReturnItemStatus(stockItems.getStockStatusId()));
    prItem.setProductKey(stockItems.getProductKey());
    prItem.setValueMrp(stockItems.getMrpValue());
    prItem.setReferenceDate(stockItems.getReferenceDate());
    prItem.setReferenceNo(stockItems.getReferenceNo());
    prItem.setRefSalesInvoiceId(stockItems.getRefSalesInvoiceId());
    //prItem.setCreatedBy(UserRuntimeView.instance().getLoginId());
    return prItem;
  }

  private Object filterValue;

  public Object getFilterValue() {
    return filterValue;
  }

  public void setFilterValue(Object filterValue) {
    this.filterValue = filterValue;
  }

  public List<Product> productByAccount(MainView main) {
    try {
      return ProductService.productByAccount(main, UserRuntimeView.instance().getAccount().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
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

  public void filterEventHandler(SelectEvent eve) {
    String filterKey = (String) Jsf.getAttribute("filterKey");
    if (eve.getObject() == null) {
      getDataTableFilters().remove(filterKey);
    } else {
      getDataTableFilters().put(filterKey, eve.getObject());
    }
  }

  public List<ServiceCommodity> selectVendorCommodity() {
    List<ServiceCommodity> vendorCommodityList = ScmLookupExtView.selectVendorCommodityByVendor(getPurchaseReturn().getAccountId().getVendorId().getId());
    if (vendorCommodityList != null && !vendorCommodityList.isEmpty() && getPurchaseReturn().getCommodityId() == null) {
      getPurchaseReturn().setCommodityId(vendorCommodityList.get(0));
    }
    return vendorCommodityList;

  }

  public Double getValue(ReturnItem returnItem) {
    Double value = 0.0;

    if (returnItem.getLandingRateCompany() != null) {
      if (returnItem.getValueTax() == null) {
        returnItem.setValueTax(0.0);
      }
      if (returnItem.getValueExciseDuty() == null) {
        returnItem.setValueExciseDuty(0.0);
      }

      value = (returnItem.getLandingRateCompany() + returnItem.getValueTax() + returnItem.getValueExciseDuty()) * returnItem.getQty();
      returnItem.setValue(value);
    }
    return value;

  }

  /**
   *
   * @param returnItemList
   */
  private void setQuantitySummary(List<ReturnItem> returnItemList) {
    Double aQty = 0.0;
    Double rQty = 0.0;
    for (ReturnItem item : returnItemList) {
      aQty += item.getQuantityAvailable() == null ? 0 : item.getQuantityAvailable();
      rQty += item.getQty() == null ? 0 : item.getQty();
    }
    setQuantityAvailable(aQty);
    setQuantityReturn(rQty);
  }

  public void returnQtyChangeEvent() {
    setQuantitySummary(getPurchaseReturnStockItemList());
  }

  public void productSelectEvent(MainView main) {
    try {
      actionLoadPurchaseRetunStockItems(main);
      setBatch(null);
      setStockItemList(null);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void loadPurchaseReturnStock(MainView main) {
    try {
      setProductId(null);
      setBatch(null);
      setStockItemList(null);
      actionLoadPurchaseRetunStockItems(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public ProductBatch getBatch() {
    return batch;
  }

  public void setBatch(ProductBatch batch) {
    this.batch = batch;
  }

  public List<ProductBatch> batchAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      if (productId != null) {
        return PurchaseReturnItemService.batchByProduct(main, productId, filter);
      } else {
        return ScmLookupView.productBatchAuto(filter);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void batchSelectEvent(MainView main) {
    try {
      List<ReturnItem> stockItemBasedonBatch = new ArrayList();
      if (purchaseReturnStockItemList == null || purchaseReturnStockItemList.size() == 0) {
        actionLoadPurchaseRetunStockItems(main);
      }
      if (purchaseReturnStockItemList != null && purchaseReturnStockItemList.size() > 0) {
        for (ReturnItem list : purchaseReturnStockItemList) {
          if (list.getBatchNo().equals(batch.getBatchNo())) {
            stockItemBasedonBatch.add(list);
          }
        }
        purchaseReturnStockItemList = stockItemBasedonBatch;
      }
      setStockItemList(null);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<ReturnItem> getStockItemList() {
    if (stockItemList == null || stockItemList.isEmpty()) {
      stockItemList = new ArrayList<>();
      if (getPurchaseReturnStockItem() != null && getPurchaseReturnStockItem().length > 0) {
//        stockItemList.addAll(Arrays.asList(getPurchaseReturnStockItem()));  //adding selected Objects
        getPurchaseReturnStockItemList().removeAll(Arrays.asList(getPurchaseReturnStockItem())); //removing selected objects from total list 
        stockItemList.addAll(getPurchaseReturnStockItemList());
      } else if (getPurchaseReturnStockItemList() != null) {
        stockItemList.addAll(getPurchaseReturnStockItemList());
      }
      setQuantitySummary(stockItemList);
    }
    return stockItemList;
  }

  public void setStockItemList(List<ReturnItem> stockItemList) {
    this.stockItemList = stockItemList;
  }

  public Integer getSelectedItemsNo() {
    return selectedItemsNo;
  }

  public void setSelectedItemsNo(Integer selectedItemsNo) {
    this.selectedItemsNo = selectedItemsNo;
  }

  public boolean isLoadable() {
    if (getPurchaseReturnStockItem() != null && getPurchaseReturnStockItem().length > 0) {
      loadable = false;
    } else {
      loadable = true;
    }
    return loadable;
  }

  public void loadRelatedItemsList(MainView main, Integer productEntryDetailId) {
    setRelatedItemsList(null);
    try {
      if (productEntryDetailId != null) {
        setRelatedItemsList(PurchaseReturnItemService.getRelatedItemsList(main, productEntryDetailId,
                SystemRuntimeConfig.SDF_YYYY_MM_DD.format(getPurchaseReturn().getEntryDate())));
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  public List<RelatedItems> getRelatedItemsList() {
    return relatedItemsList;
  }

  public void setRelatedItemsList(List<RelatedItems> relatedItemsList) {
    this.relatedItemsList = relatedItemsList;
  }

  public void showPopup(Integer id, String sourceType) {
    PopUpView popUpView = new PopUpView();
    if (sourceType != null && id != null) {
      if (sourceType.equals("SR")) {
        popUpView.showSalesReturn(id);
      } else if (sourceType.equals("MOV")) {
        popUpView.showStockMovement(id);
      } else if (sourceType.equals("PE")) {
        popUpView.showPurchase(id);
      } else if (sourceType.equals("ADJ")) {
        popUpView.showStockAdjustMent(id);
      }
    }
  }
}
