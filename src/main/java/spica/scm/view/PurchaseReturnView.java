/*
 * @(#)ScmPurchaseReturnView.java	1.0 Sat Jul 22 17:33:03 IST 2017
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.fin.domain.AccountingLedger;
import spica.scm.common.ReturnItem;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.PurchaseReturn;
import spica.scm.service.PurchaseReturnService;
import spica.scm.domain.PurchaseReturnStatus;
import spica.scm.domain.PurchaseReturnStockCat;
import spica.scm.domain.Consignment;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.ConsignmentCommodityService;
import spica.scm.service.ConsignmentService;
import spica.scm.service.PrefixTypeService;
import spica.scm.service.PurchaseReturnItemService;
import spica.scm.service.PurchaseReturnStockCatService;
import spica.fin.view.FinLookupView;
import spica.scm.common.InvoiceByHsn;
import spica.scm.common.ProductInvoiceDetail;
import spica.scm.common.RelatedItems;
import spica.scm.domain.CompanyFinancialYear;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.PurchaseReturnItem;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.export.LineItemExport;
import spica.scm.service.AccountGroupService;
import spica.scm.service.CompanyService;
import spica.scm.service.CompanySettingsService;
import spica.scm.service.StockNonSaleableService;
import spica.scm.service.StockSaleableService;
import spica.scm.service.StockStatusService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.AppUtil;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.entity.core.UserMessageException;

/**
 * ScmPurchaseReturnView
 *
 * @author	Spirit 1.2
 * @version	1.0, Sat Jul 22 17:33:03 IST 2017
 */
@Named(value = "purchaseReturnView")
@ViewScoped
public class PurchaseReturnView implements Serializable {

  private transient PurchaseReturn purchaseReturn;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseReturn> purchaseReturnLazyModel; 	//For lazy loading datatable.
  private transient PurchaseReturn[] purchaseReturnSelected;	 //Selected Domain Array
  private transient PurchaseReturnStockCat purchaseReturnStockCat;
  private transient List<PurchaseReturnItemReplica> purchaseReturnItemReplicaList = null;
  private transient TaxCalculator taxCalculator;
  private transient Consignment consignment;
  private transient List<RelatedItems> relatedItemsList;
  private transient boolean valueMrpChanged;
  private transient PurchaseReturnItemReplica[] purchaseReturnItemSelected;
  private String nextInvoiceNumber;
  private transient List<PurchaseReturn> tmpList;
  private transient int rowSize;
  private transient int start;
  private transient String sortColumn;
  private transient SortOrder sOrder;
  private transient List<ProductInvoiceDetail> purchaseHistoryList;
  private transient Integer isInvoiceDiscountPercentage;
  private boolean igstTransaction;
  private boolean tradeClosed;
  private transient Map<String, InvoiceByHsn> hsnMap;
//  private transient 

  @PostConstruct
  public void init() {
    Integer invoiceId = (Integer) Jsf.popupParentValue(Integer.class);
    if (invoiceId != null) {
      getPurchaseReturn().setId(invoiceId);
    }
  }

  /**
   * Default Constructor.
   */
  public PurchaseReturnView() {
    super();
  }

  /**
   * Return purchaseReturn.
   *
   * @return PurchaseReturn.
   */
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

  /**
   * Set PurchaseReturn.
   *
   * @param purchaseReturn.
   */
  public void setPurchaseReturn(PurchaseReturn purchaseReturn) {
    this.purchaseReturn = purchaseReturn;
  }

  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  public Consignment getConsignment() {
    return consignment;
  }

  public void setConsignment(Consignment consignment) {
    this.consignment = consignment;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public PurchaseReturnStockCat getPurchaseReturnStockCat() {
    return purchaseReturnStockCat;
  }

  public void setPurchaseReturnStockCat(PurchaseReturnStockCat purchaseReturnStockCat) {
    this.purchaseReturnStockCat = purchaseReturnStockCat;
  }

  public boolean isValueMrpChanged() {
    return valueMrpChanged;
  }

  public void setValueMrpChanged(boolean valueMrpChanged) {
    this.valueMrpChanged = valueMrpChanged;
  }

  public List<PurchaseReturnItemReplica> getPurchaseReturnItemReplicaList() {
    if (purchaseReturnItemReplicaList == null) {
      purchaseReturnItemReplicaList = new ArrayList<>();
    }
    return purchaseReturnItemReplicaList;
  }

  public void setPurchaseReturnItemReplicaList(List<PurchaseReturnItemReplica> purchaseReturnItemReplicaList) {
    this.purchaseReturnItemReplicaList = purchaseReturnItemReplicaList;
  }

  public List<RelatedItems> getRelatedItemsList() {
    return relatedItemsList;
  }

  public void setRelatedItemsList(List<RelatedItems> relatedItemsList) {
    this.relatedItemsList = relatedItemsList;
  }

  /**
   * Return LazyDataModel of PurchaseReturn.
   *
   * @return
   */
  public LazyDataModel<PurchaseReturn> getPurchaseReturnLazyModel() {
    return purchaseReturnLazyModel;
  }

  /**
   * Return PurchaseReturn[].
   *
   * @return
   */
  public PurchaseReturn[] getPurchaseReturnSelected() {
    return purchaseReturnSelected;
  }

  /**
   * Set PurchaseReturn[].
   *
   * @param purchaseReturnSelected
   */
  public void setPurchaseReturnSelected(PurchaseReturn[] purchaseReturnSelected) {
    this.purchaseReturnSelected = purchaseReturnSelected;
  }

  public PurchaseReturnItemReplica[] getPurchaseReturnItemSelected() {
    return purchaseReturnItemSelected;
  }

  public void setPurchaseReturnItemSelected(PurchaseReturnItemReplica[] purchaseReturnItemSelected) {
    this.purchaseReturnItemSelected = purchaseReturnItemSelected;
  }

  public Long getRowKey() {
    return new Date().getTime();
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionNewPurchaseReturn(MainView main) {
    try {
      if (UserRuntimeView.instance().getCompany() == null) {
        main.error("company.required");
        main.setViewType(ViewTypes.list);
        return null;
      } else if (UserRuntimeView.instance().getAccount() == null) {
        main.error("account.required");
        main.setViewType(ViewTypes.list);
        return null;
      } else if (UserRuntimeView.instance().getContract() == null) {
        main.error("contract.required");
        main.setViewType(ViewTypes.list);
        return null;
      }
      getPurchaseReturn().reset();
      setPurchaseReturnItemReplicaList(null);
      CompanySettingsService.selectIfNull(main, getPurchaseReturn().getCompanyId());
      getPurchaseReturn().setPurchaseReturnStockCat(getPurchaseReturnStockCat());
      getPurchaseReturn().setDecimalPrecision(UserRuntimeView.instance().getAccount().getSupplierDecimalPrecision() == null ? 2 : UserRuntimeView.instance().getAccount().getSupplierDecimalPrecision());
      getPurchaseReturn().setInvoiceNo("DR-" + AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getPurchaseReturn().getAccountId(), PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, true, getPurchaseReturn().getFinancialYearId()));
      getPurchaseReturn().setInvoiceDate(new Date());
      getPurchaseReturn().setEntryDate(new Date());
      getPurchaseReturn().setTaxProcessorId(getPurchaseReturn().getCompanyId().getCountryTaxRegimeId().getTaxProcessorId());
      setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getPurchaseReturn().getTaxProcessorId().getProcessorClass()));
      getPurchaseReturn().setAccountingLedgerId(accountingLedgerPurchaseReturnAuto("").get(0));
      getPurchaseReturn().setIsReturnInterstate(getPurchaseReturn().getAccountId().getIsPurchaseInterstate());
      getPurchaseReturn().setSezZone((getPurchaseReturn().getAccountId().getVendorId().getTaxable() != null && getPurchaseReturn().getAccountId().getVendorId().getTaxable().intValue() == 2) ? 1 : 0);
      setIgstTransaction((getPurchaseReturn().getIsReturnInterstate().intValue() == 1 || getPurchaseReturn().getSezZone().intValue() == 1) ? true : false);
      main.setViewType(ViewTypes.newform); // Change to ViewTypes.list to navigate to list page
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPurchaseReturn(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          if (getPurchaseReturn() != null && getPurchaseReturn().getBusinessArea() == null) {
            getPurchaseReturn().setBusinessArea(igstTransaction ? 1 : 2);
          }
        } else if (main.isEdit() && !main.hasError()) {
          //FIXME thse  6 line is it required
          if (getPurchaseReturn().getCompanyId() == null) {
            getPurchaseReturn().setCompanyId(CompanyService.getCompanyByPurchaseReturn(main, getPurchaseReturn()));
          }
          if (getPurchaseReturn().getCompanyId().getCompanySettings() == null) {
            getPurchaseReturn().getCompanyId().setCompanySettings(CompanySettingsService.selectByCompany(main.em(), getPurchaseReturn().getCompanyId()));
          }
          selectPurchaseReturnForEdit(main);
          if (!isDraft()) {
            setRelatedItemsList(PurchaseReturnService.selectRelatedItemsOfPurchaseReturn(main, getPurchaseReturn()));
          }
          nextInvoiceNumber(main);
        } else if (main.isList()) {
          setPurchaseReturnStockCat(null);
          setTaxCalculator(null);
          getPurchaseReturn().reset();
          setPurchaseReturnItemReplicaList(null);
          setRelatedItemsList(null);
          loadPurchaseReturnList(main);
          setValueMrpChanged(false);
          purchaseReturnStockCat = new PurchaseReturnStockCat();
          purchaseReturnStockCat.setId(PurchaseReturnStockCatService.STOCK_NON_MOVING_AND_NEAR_EXPIRY);
          main.getPageData().setSearchKeyWord(null);
        }
        CompanySettingsService.selectIfNull(main, getPurchaseReturn().getCompanyId());
        setTradeClosedValue();
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  public String switchToShipping(MainView main) {

    if (consignment != null) {
      Jsf.popupForm(ConsignmentService.PURCHASE_RETURN_CONSIGNMENT_POPUP, null, consignment.getId());
    } else {
      Jsf.popupForm(ConsignmentService.PURCHASE_RETURN_CONSIGNMENT_POPUP, getPurchaseReturn());
    }
    return null;
  }

  /**
   * Create purchaseReturnLazyModel.
   *
   * @param main
   */
  private void loadPurchaseReturnList(final MainView main) {
    if (purchaseReturnLazyModel == null) {
      purchaseReturnLazyModel = new LazyDataModel<PurchaseReturn>() {
        private List<PurchaseReturn> list;

        @Override
        public List<PurchaseReturn> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            if (UserRuntimeView.instance().getAccountCurrent() != null) {
              start = first;
              rowSize = pageSize;
              sortColumn = sortField;
              sOrder = sortOrder;
              list = PurchaseReturnService.listPurchaseReturnByAccount(main, UserRuntimeView.instance().getAccountCurrent(), UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
              tmpList = list;
              main.commit(purchaseReturnLazyModel, first, pageSize);
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
        public Object getRowKey(PurchaseReturn purchaseReturn) {
          return purchaseReturn.getId();
        }

        @Override
        public PurchaseReturn getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReturn obj : list) {
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
    String SUB_FOLDER = "purchase_return/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @param status
   * @return the page to display.
   */
  public String savePurchaseReturn(MainView main, int status) {
    if (isValueMrpChanged()) {
      main.error("error.mrp.changed");
      return null;
    }

    getPurchaseReturn().setPurchaseReturnStatusId(new PurchaseReturnStatus(status));
    return saveOrClonePurchaseReturn(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseReturn(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrClonePurchaseReturn(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseReturn(MainView main, String key) {
    List<ReturnItem> purchaseReturnStockItemList = null;
    List<PurchaseReturnItem> purchaseReturnItemsList = null;
    String productTypeCode = "GL";
    try {
      if (!isUsedByOtherUser(main)) {
        uploadFiles(); //File upload
        boolean nextPrv = false;
        if (null != key) {
          if (null == getPurchaseReturn().getId()) {
            nextPrv = true;
          }
          switch (key) {
            case "save":
              if (SystemConstants.CONFIRMED == getPurchaseReturn().getPurchaseReturnStatusId().getId()) {

                if (isDamagedAndExpiredReturn()) {
                  purchaseReturnStockItemList = getTaxCalculator().getDamagedAndExpiredProductsForPurchaseReturn(main, null, null, getPurchaseReturn().getAccountId().getId(), null, getPurchaseReturn().getPurchaseReturnStockCat().getId(), getPurchaseReturn().getEntryDate());
                } else {
                  purchaseReturnStockItemList = getTaxCalculator().getNonMovingProductsForPurchaseReturn(main, null, null, getPurchaseReturn().getAccountId().getId(), productTypeCode, getPurchaseReturn().getPurchaseReturnStockCat().getId(), getPurchaseReturn().getEntryDate());
                }

                if (verifyAndUpdateReturnQuantity(purchaseReturnStockItemList, getPurchaseReturnItemReplicaList())) {
                  getPurchaseReturn().setAccountGroup(AccountGroupService.selectDefaultAccountGroupByAccount(main, getPurchaseReturn().getAccountId()));
                  PurchaseReturnService.insertOrUpdate(main, getPurchaseReturn());
                  purchaseReturnItemsList = PurchaseReturnItemService.insertPurchaseReturnItemList(main, getPurchaseReturn(), getPurchaseReturnItemReplicaList());
                  if (isDamagedAndExpiredReturn()) {
                    StockNonSaleableService.insertPurchaseReturnProductDetails(main, purchaseReturnItemsList);
                  } else if (isNonMovingAndNearExpiryReturn()) {
                    StockSaleableService.insertPurchaseReturnProductDetails(main, purchaseReturnItemsList);
                  }

                  getTaxCalculator().savePurchaseReturn(main, purchaseReturn);

                } else {
                  main.error("invalid.stock.qty");
                  return null;
                }
              } else if (SystemConstants.DRAFT == getPurchaseReturn().getPurchaseReturnStatusId().getId()) {
                PurchaseReturnService.insertOrUpdate(main, getPurchaseReturn());
                PurchaseReturnItemService.insertPurchaseReturnItemList(main, getPurchaseReturn(), getPurchaseReturnItemReplicaList());
              } else {
                PurchaseReturnService.insertOrUpdate(main, getPurchaseReturn());
              }

              break;

            case "clone":
              PurchaseReturnService.clone(main, getPurchaseReturn());
              break;
          }
          main.commit("success." + key);
          main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
          if (nextPrv) {
            loadPreviousNextPurchaseReturnList(main);
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
   * Delete one or many PurchaseReturn.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseReturn(MainView main) {
    Integer id = null;
    try {
      if (main.isList() && !StringUtil.isEmpty(purchaseReturnSelected)) {
        //many record delete from list
        for (PurchaseReturn e : purchaseReturnSelected) {
          id = e.getId();
          PurchaseReturnService.deleteByPk(main, e);
          main.em().commit();
        }
        main.commit("success.delete");
        purchaseReturnSelected = null;
      } else {
        id = getPurchaseReturn().getId();
        PurchaseReturnService.deleteByPk(main, getPurchaseReturn());  //individual record delete from list or edit form
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
   * @param purchaseReturnItem
   * @return
   */
  private String deletePurchaseReturnItem(MainView main, PurchaseReturnItemReplica purchaseReturnItem) {
    PurchaseReturnItemService.deleteByPk(main, new PurchaseReturnItem(purchaseReturnItem));
    setValueMrpChanged(PurchaseReturnService.isProductValueMrpChanged(main, getPurchaseReturn()));
    getPurchaseReturnItemReplicaList().remove(purchaseReturnItem);
    getTaxCalculator().processPurchaseReturnCalculation(getPurchaseReturn(), getPurchaseReturnItemReplicaList());
    if (getPurchaseReturn().getPurchaseReturnItemReplicaList() != null) {
      getPurchaseReturn().getPurchaseReturnItemReplicaList().remove(purchaseReturnItem);
    }
    return null;
  }

  /**
   * Method to check the purchase return is in Draft status or not.
   *
   * @return
   */
  public boolean isDraft() {
    return ((getPurchaseReturn() != null && getPurchaseReturn().getId() == null) || (getPurchaseReturn() != null && getPurchaseReturn().getPurchaseReturnStatusId() != null && getPurchaseReturn().getPurchaseReturnStatusId().getId().equals(SystemConstants.DRAFT)));
  }

  /**
   * Method to check the purchase return is in Confirmed status or not.
   *
   * @return
   */
  public boolean isConfirmed() {
    return (getPurchaseReturn() != null && getPurchaseReturn().getPurchaseReturnStatusId() != null && getPurchaseReturn().getPurchaseReturnStatusId().getId().equals(SystemConstants.CONFIRMED));
  }

  /**
   * Method to check the purchase return is in Confirmed and Packed status or not.
   *
   * @return
   */
  public boolean isConfirmedAndPacked() {
    return (getPurchaseReturn().getPurchaseReturnStatusId() != null && getPurchaseReturn().getPurchaseReturnStatusId().getId() == SystemConstants.CONFIRMED_AND_PACKED);
  }

  /**
   *
   * @return
   */
  public boolean isNonMovingAndNearExpiryReturn() {
    return (getPurchaseReturn().getPurchaseReturnStockCat() != null && PurchaseReturnStockCatService.STOCK_NON_MOVING_AND_NEAR_EXPIRY == getPurchaseReturn().getPurchaseReturnStockCat().getId());
  }

  /**
   *
   * @return
   */
  public boolean isDamagedAndExpiredReturn() {
    return (getPurchaseReturn().getPurchaseReturnStockCat() != null && PurchaseReturnStockCatService.STOCK_DAMAGED_AND_EXPIRED == getPurchaseReturn().getPurchaseReturnStockCat().getId());
  }

  /**
   *
   */
  public void purchaseReturnItemListPopup(MainView main) {
    try {
      if (!isUsedByOtherUser(main)) {
        Jsf.popupForm(FileConstant.PURCHASE_RETURN_ITEM_GST_POPUP_URL, getPurchaseReturn()); // opens a new form if id is null else edit
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> accountingLedgerPurchaseReturnAuto(String filter) {
    return FinLookupView.ledgerPurchaseReturnAuto(filter, getPurchaseReturn().getCompanyId().getId());
  }

  /**
   *
   * @param main
   */
  public void purchaseReturnItemPopupReturned(MainView main) {
    try {
      setPurchaseReturnItemReplicaList(getPurchaseReturn().getPurchaseReturnItemReplicaList());
      getTaxCalculator().processPurchaseReturnCalculation(getPurchaseReturn(), getPurchaseReturnItemReplicaList());
      getInvoiceByHsnList(getPurchaseReturnItemReplicaList());
      //selectProductForPurchaseReturn(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void returnQtyChangeEvent() {
    getTaxCalculator().processPurchaseReturnCalculation(getPurchaseReturn(), getPurchaseReturnItemReplicaList());
  }

  /**
   *
   * @param main
   */
  // private void selectProductForPurchaseReturn(MainView main) {
//    List<PurchaseReturnItem> list = new ArrayList<>();
//    try {
//      if (getPurchaseReturn() != null && getPurchaseReturn().getId() != null) {
//        //list = PurchaseReturnItemService.listPurchaseReturnItemByPurchaseReturn(main, getPurchaseReturn());//getPurchaseReturn().getItemLists();
//      }
//
//      if (getPurchaseReturn() != null && getPurchaseReturn().getPurchaseReturnItemList() != null && !getPurchaseReturn().getPurchaseReturnItemList().isEmpty()) {
//        if (list == null) {
//          list = new ArrayList<>();
//        }
//        //list.addAll(getPurchaseReturn().getPurchaseReturnItemList());
//      }
//      setPurchaseReturnItemList(list);
//    } catch (Throwable t) {
//      main.rollback(t, "error.select");
//    } finally {
//      main.close();
//    }
//  }
  /**
   *
   * @param purchaseReturnStockItemList
   * @param purchaseReturnItemList
   * @return
   */
  private boolean verifyAndUpdateReturnQuantity(List<ReturnItem> purchaseReturnStockItemList, List<PurchaseReturnItemReplica> purchaseReturnItemList) {
    boolean isValid = true;
    //Integer stockId = null;
    Double avialableQty = null;
    String product = "";
    if (purchaseReturnStockItemList != null && purchaseReturnItemList != null) {
      for (PurchaseReturnItemReplica pItem : purchaseReturnItemList) {
        if (pItem != null && pItem.getProductDetailId() != null && pItem.getProductDetail().getProductBatchId() != null && pItem.getProductDetail().getProductBatchId().getProductId() != null) {
          product = pItem.getProductDetail().getProductBatchId().getProductId().getProductName() + " (" + pItem.getProductDetail().getProductBatchId().getBatchNo() + ")";
        }
        for (ReturnItem rItem : purchaseReturnStockItemList) {
//          if (pItem.getStockNonSaleableId() != null) {
//            stockId = pItem.getStockNonSaleableId().getId();
//          } else if (pItem.getStockSaleableId() != null) {
//            stockId = pItem.getStockSaleableId().getId();
//          }
          if (pItem.getProductDetail() != null) {
            if (rItem.getInvoiceProductKey().equals(pItem.getInvoiceProductKey())) {
              if (rItem.getQuantityAvailable() != null) {
                avialableQty = rItem.getQuantityAvailable();
                pItem.setStockType(0);
              } else if (rItem.getQuantityFreeAvailable() != null) {
                avialableQty = rItem.getQuantityFreeAvailable();
                pItem.setStockType(1);
              }
              pItem.setProductStockStatus(StockStatusService.STOCK_STATUS_SALEABLE);
              if (avialableQty < pItem.getQuantityReturned()) {
                pItem.setActualQty(avialableQty);
                isValid = false;
                break;
              }
            }
          }
        }
        if (avialableQty == null) {
          isValid = false;
          break;
        }
      }
    }
    if (!isValid) {
      getPurchaseReturn().setPurchaseReturnStatusId(new PurchaseReturnStatus(SystemConstants.DRAFT));
      throw new UserMessageException("error.product.qty", new String[]{product});
    }
    return isValid;
  }

  public String actionResetPurchaseReturn(MainView main) {
    try {
      PurchaseReturnService.resetPurchaseReturnStatusToDraft(main, getPurchaseReturn());
      savePurchaseReturn(main, SystemConstants.DRAFT);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public void schemeDiscountValueChangeEvent(PurchaseReturnItemReplica purchaseReturnItem) {
    try {
      if (purchaseReturnItem.getSchemeDiscountValue() != null) {
        purchaseReturnItem.setSchemeDiscountPercentage(null);
        purchaseReturnItem.setIsSchemeDiscountInPercentage(SystemConstants.DISCOUNT_IN_VALUE);
      } else {
        purchaseReturnItem.setSchemeDiscountPercentage(null);
        purchaseReturnItem.setIsSchemeDiscountInPercentage(null);
      }
      getTaxCalculator().processPurchaseReturnCalculation(getPurchaseReturn(), getPurchaseReturnItemReplicaList());

    } catch (Throwable t) {
      Jsf.fatal(t, "error.select");
    }
  }

  public void schemeDiscountPercChangeEvent(PurchaseReturnItemReplica purchaseReturnItem) {
    try {
      if (purchaseReturnItem.getSchemeDiscountPercentage() != null) {
        purchaseReturnItem.setSchemeDiscountValue(null);
        purchaseReturnItem.setIsSchemeDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
      } else {
        purchaseReturnItem.setSchemeDiscountValue(null);
        purchaseReturnItem.setIsSchemeDiscountInPercentage(null);
      }
      getTaxCalculator().processPurchaseReturnCalculation(getPurchaseReturn(), getPurchaseReturnItemReplicaList());
    } catch (Throwable t) {
      Jsf.fatal(t, "error.select");
    }
  }

  public void productDiscountValueChangeEvent(PurchaseReturnItemReplica purchaseReturnItem) {
    try {
      if (purchaseReturnItem.getProductDiscountValue() != null) {
        purchaseReturnItem.setProductDiscountPerc(null);
        purchaseReturnItem.setIsProductDiscountInPercentage(SystemConstants.DISCOUNT_IN_VALUE);
      } else {
        purchaseReturnItem.setProductDiscountPerc(null);
        purchaseReturnItem.setIsProductDiscountInPercentage(null);
      }
      getTaxCalculator().processPurchaseReturnCalculation(getPurchaseReturn(), getPurchaseReturnItemReplicaList());

    } catch (Throwable t) {
      Jsf.fatal(t, "error.select");
    }
  }

  public void productDiscountPercChangeEvent(PurchaseReturnItemReplica purchaseReturnItem) {
    try {
      if (purchaseReturnItem.getProductDiscountPerc() != null) {
        purchaseReturnItem.setProductDiscountValue(null);
        purchaseReturnItem.setIsProductDiscountInPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
      } else {
        purchaseReturnItem.setProductDiscountValue(null);
        purchaseReturnItem.setIsProductDiscountInPercentage(null);
      }
      getTaxCalculator().processPurchaseReturnCalculation(getPurchaseReturn(), getPurchaseReturnItemReplicaList());
    } catch (Throwable t) {
      Jsf.fatal(t, "error.select");
    }
  }

  public void invoiceDiscountValueChangeEvent(PurchaseReturnItemReplica purchaseReturnItem) {
    try {
      if (purchaseReturnItem.getInvoiceDiscountValue() != null) {
        purchaseReturnItem.setInvoiceDiscountPercentage(null);
        setIsInvoiceDiscountPercentage(SystemConstants.DISCOUNT_IN_VALUE);
      } else {
        purchaseReturnItem.setInvoiceDiscountPercentage(null);
        setIsInvoiceDiscountPercentage(null);
      }
      getTaxCalculator().processPurchaseReturnCalculation(getPurchaseReturn(), getPurchaseReturnItemReplicaList());

    } catch (Throwable t) {
      Jsf.fatal(t, "error.select");
    }
  }

  public void invoiceDiscountPercChangeEvent(PurchaseReturnItemReplica purchaseReturnItem) {
    try {
      if (purchaseReturnItem.getInvoiceDiscountPercentage() != null) {
        purchaseReturnItem.setInvoiceDiscountValue(null);
        setIsInvoiceDiscountPercentage(SystemConstants.DISCOUNT_IN_PERCENTAGE);
      } else {
        purchaseReturnItem.setInvoiceDiscountValue(null);
        setIsInvoiceDiscountPercentage(null);
      }
      getTaxCalculator().processPurchaseReturnCalculation(getPurchaseReturn(), getPurchaseReturnItemReplicaList());
    } catch (Throwable t) {
      Jsf.fatal(t, "error.select");
    }
  }

  //FIXME main is not required for these methods
  public void printLandscape(MainView main, String key) {
    CompanySettings settings = getPurchaseReturn().getCompanyId().getCompanySettings();
    if (settings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
      getPurchaseReturn().setPrintType(SystemConstants.PRINT_SINGLE_LINE);
    } else {
      getPurchaseReturn().setPrintType(SystemConstants.PRINT_MULTIPLE_LINE);
    }
    if (SystemConstants.PDF_ITEXT.equals(key)) {
      Jsf.popupForm(getTaxCalculator().getPurchaseReturnPrintIText(), getPurchaseReturn());
    }
  }

  public void printPortrait(MainView main, String key) {
    getPurchaseReturn().setPrintType(SystemConstants.PRINT_PORTRAIT);
    Jsf.popupForm(getTaxCalculator().getPurchaseReturnPrintIText(), getPurchaseReturn());
  }

  public void deletePurchaseReturnItems(MainView main) {
    try {
      if (!isUsedByOtherUser(main)) {
        if (purchaseReturnItemSelected != null) {
          for (PurchaseReturnItemReplica returnItem : purchaseReturnItemSelected) {
            deletePurchaseReturnItem(main, returnItem);
          }
        }
        setPurchaseReturnItemSelected(null);
        main.commit("success.delete");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public String getNextInvoiceNumber() {
    return nextInvoiceNumber;
  }

  public void nextInvoiceNumber(MainView main) {
    try {
      if (getPurchaseReturn().getAccountId() != null) {
        nextInvoiceNumber = AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getPurchaseReturn().getAccountId(), PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, false, getPurchaseReturn().getFinancialYearId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void nextOrPreviousInvoice(MainView main, Integer next) {
    try {
      if (tmpList != null) {
        Integer id = getInvoiceIdByIndex(main, tmpList.indexOf(getPurchaseReturn()), next);
        getPurchaseReturn().setId(id);
        purchaseReturn = PurchaseReturnService.selectByPk(main, getPurchaseReturn());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private Integer getInvoiceIdByIndex(MainView main, Integer index, Integer next) {
    int rowCount = purchaseReturnLazyModel.getRowCount();
    if ((index >= (rowSize - 1)) && start <= rowCount && next > 0) {
      start += rowSize;
    } else if (next < 0 && index == 0) {
      start -= rowSize;
      index = rowSize;
    }
    loadPreviousNextPurchaseReturnList(main);
    PurchaseReturn invoice = tmpList.get((index + next == rowSize) ? 0 : index + next);
    return invoice.getId();
  }

  public boolean isNextButton() {
    int rowCount = purchaseReturnLazyModel == null ? 0 : purchaseReturnLazyModel.getRowCount();
    if (tmpList != null && (tmpList.indexOf(getPurchaseReturn()) != (tmpList.size() - 1) || ((start + rowSize) <= rowCount))) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isPreviousButton() {
    if (tmpList == null) {
      return false;
    }
    if (tmpList.indexOf(getPurchaseReturn()) == 0 && start == 0) {
      return false;
    } else {
      return true;
    }
  }

  private void loadPreviousNextPurchaseReturnList(MainView main) {
    main.getPageData().setSearchKeyWord(null);
    AppPage.move(main.getPageData(), start, rowSize, sortColumn, sOrder.name());
    tmpList = PurchaseReturnService.listPurchaseReturnByAccount(main, getPurchaseReturn().getAccountId(), getPurchaseReturn().getCompanyId().getCurrentFinancialYear());
  }

  private boolean isUsedByOtherUser(MainView main) {
    if (getPurchaseReturn().getId() == null || PurchaseReturnService.isPurchaseReturnEditable(main, getPurchaseReturn())) {
      return false;
    } else {
      main.error("error.past.record.edit");
      selectPurchaseReturnForEdit(main);
      return true;
    }
  }

  private void selectPurchaseReturnForEdit(MainView main) {
    setPurchaseReturn((PurchaseReturn) PurchaseReturnService.selectByPk(main, getPurchaseReturn()));
    CompanySettingsService.selectIfNull(main, getPurchaseReturn().getCompanyId());
    getPurchaseReturn().setDecimalPrecision(getPurchaseReturn().getAccountId().getSupplierDecimalPrecision() == null ? 2 : getPurchaseReturn().getAccountId().getSupplierDecimalPrecision());
    setValueMrpChanged(PurchaseReturnService.isProductValueMrpChanged(main, getPurchaseReturn()));
    setIgstTransaction((getPurchaseReturn().getIsReturnInterstate().intValue() == 1 || getPurchaseReturn().getSezZone().intValue() == 1) ? true : false);
    getPurchaseReturn().setBusinessArea(igstTransaction ? 1 : 2);
    setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getPurchaseReturn().getTaxProcessorId().getProcessorClass()));
    setPurchaseReturnItemReplicaList(PurchaseReturnItemService.listPurchaseReturnItemByPurchaseReturn(main, getPurchaseReturn()));
    getTaxCalculator().processPurchaseReturnCalculation(getPurchaseReturn(), getPurchaseReturnItemReplicaList());
    if (getPurchaseReturnItemReplicaList() != null) {
      getPurchaseReturn().setPurchaseReturnItemReplicaList(getPurchaseReturnItemReplicaList());
    }
    getInvoiceByHsnList(getPurchaseReturnItemReplicaList());
    consignment = ConsignmentCommodityService.consignmentByReturnId(main, getPurchaseReturn());
  }

  public void loadPurchaseHistoryList(MainView main, PurchaseReturnItemReplica returnItemReplica) {
    purchaseHistoryList = null;
    try {
      ProductEntryDetail productEntryDetail = returnItemReplica.getProductEntryDetail() == null
              ? (returnItemReplica.getProductEntryDetailId() != null ? new ProductEntryDetail(returnItemReplica.getProductEntryDetailId()) : null) : returnItemReplica.getProductEntryDetail();
      purchaseHistoryList = PurchaseReturnService.purchaseHistoryList(main, getPurchaseReturn().getAccountId(), returnItemReplica.getProductDetail(), productEntryDetail);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<ProductInvoiceDetail> getPurchaseHistoryList() {
    return purchaseHistoryList;
  }

  public void setPurchaseHistoryList(List<ProductInvoiceDetail> purchaseHistoryList) {
    this.purchaseHistoryList = purchaseHistoryList;
  }

  public Integer getIsInvoiceDiscountPercentage() {
    return isInvoiceDiscountPercentage;
  }

  public void setIsInvoiceDiscountPercentage(Integer isInvoiceDiscountPercentage) {
    this.isInvoiceDiscountPercentage = isInvoiceDiscountPercentage;
  }

  public void export(MainView main) {
    try {
      String fileName = "Purchase_return_items";
      LineItemExport.exportPurchaseReturnItems(fileName, main, getPurchaseReturn(), getPurchaseReturnItemReplicaList(), getHsnList());
    } catch (Throwable t) {
      main.rollback(t, "error.export");
    } finally {
      main.close();
    }
  }

  public boolean isIgstTransaction() {
    return igstTransaction;
  }

  public void setIgstTransaction(boolean igstTransaction) {
    this.igstTransaction = igstTransaction;
  }

  public boolean isTradeClosed() {
    return tradeClosed;
  }

  public void setTradeClosed(boolean tradeClosed) {
    this.tradeClosed = tradeClosed;
  }

  public void setTradeClosedValue() {
    tradeClosed = false;
    CompanySettings companySettings = getPurchaseReturn().getCompanyId().getCompanySettings();
    for (CompanyFinancialYear fy : getPurchaseReturn().getCompanyId().getCompanyFinancialYearList()) {
      if (getPurchaseReturn().getFinancialYearId().equals(fy.getFinancialYearId())) {
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
        if (purchaseReturn != null && getPurchaseReturn().getPurchaseReturnStatusId() != null && getPurchaseReturn().getPurchaseReturnStatusId().getId().intValue() > SystemConstants.DRAFT) {
          Date entryDate = getPurchaseReturn().getEntryDate();
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

  private void getInvoiceByHsnList(List<PurchaseReturnItemReplica> purchaseReturnItems) {
    hsnMap = new LinkedHashMap<>();
    for (PurchaseReturnItemReplica invoiceItem : purchaseReturnItems) {
      if (invoiceItem.getProductEntryDetailId() != null) {
        if (invoiceItem.getHsnCode() == null && (invoiceItem.getProductDetail() != null && invoiceItem.getProductDetail().getProductBatchId() != null)) {
          invoiceItem.setHsnCode(invoiceItem.getProductDetail().getProductBatchId().getProductId().getHsnCode());
        }
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
  }

  public List<Map.Entry<String, InvoiceByHsn>> getHsnList() {
    Set<Map.Entry<String, InvoiceByHsn>> invoiceByHsnList = null;
    if (hsnMap != null) {
      invoiceByHsnList = hsnMap.entrySet();
      return new ArrayList<Map.Entry<String, InvoiceByHsn>>(invoiceByHsnList);
    }
    return null;
  }
}
