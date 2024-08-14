/*
 * @(#)StockMovementView.java	1.0 Thu Mar 29 12:11:04 IST 2018 
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import spica.scm.common.StockAdjustmentDetail;
import spica.scm.common.StocktoExpiry;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntryDetail;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.StockMovement;
import spica.scm.domain.StockMovementItem;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.PrefixTypeService;
import spica.scm.service.StockMovementItemService;
import spica.scm.service.StockMovementService;
import spica.scm.service.StockPreSaleService;
import spica.scm.service.StockSaleableService;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * StockMovementView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Mar 29 12:11:04 IST 2018
 */
@Named(value = "stockMovementView")
@ViewScoped
public class StockMovementView implements Serializable {

  private transient StockMovement stockMovement;	//Domain object/selected Domain.
  private transient LazyDataModel<StockMovement> stockMovementLazyModel; 	//For lazy loading datatable.
  private transient StockMovement[] stockMovementSelected;	 //Selected Domain Array
  private transient List<StockMovementItem> stockMovementItemList;
  private transient HashMap<Integer, String> productHashCode;
  private transient Integer stockMovementType;
  private transient String pageTitle;
  private transient List<SelectItem> stockMovementTypeList;
  private transient boolean disabled;
  private transient Date expiryDate;
  private String nextInvoiceNumber;
  private transient Date entryDate;

  @PostConstruct
  public void init() {
    Integer id = (Integer) Jsf.popupParentValue(Integer.class);
    if (id != null) {
      getStockMovement().setId(id);
    }
  }

  /**
   * Default Constructor.
   */
  public StockMovementView() {
    super();
  }

  /**
   * Return StockMovement.
   *
   * @return StockMovement.
   */
  public StockMovement getStockMovement() {
    if (stockMovement == null) {
      stockMovement = new StockMovement();
    }
    if (stockMovement.getCompanyId() == null) {
      stockMovement.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    if (stockMovement.getFinancialYearId() == null) {
      stockMovement.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    return stockMovement;
  }

  /**
   * Set StockMovement.
   *
   * @param stockMovement.
   */
  public void setStockMovement(StockMovement stockMovement) {
    this.stockMovement = stockMovement;
  }

  /**
   * Return LazyDataModel of StockMovement.
   *
   * @return
   */
  public LazyDataModel<StockMovement> getStockMovementLazyModel() {
    return stockMovementLazyModel;
  }

  /**
   * Return StockMovement[].
   *
   * @return
   */
  public StockMovement[] getStockMovementSelected() {
    return stockMovementSelected;
  }

  /**
   * Set StockMovement[].
   *
   * @param stockMovementSelected
   */
  public void setStockMovementSelected(StockMovement[] stockMovementSelected) {
    this.stockMovementSelected = stockMovementSelected;
  }

  public List<StockMovementItem> getStockMovementItemList() {
    if (StringUtil.isEmpty(stockMovementItemList)) {
      stockMovementItemList = new ArrayList<>();
    }
    return stockMovementItemList;
  }

  public void setStockMovementItemList(List<StockMovementItem> stockMovementItemList) {
    this.stockMovementItemList = stockMovementItemList;
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }

  public HashMap<Integer, String> getProductHashCode() {
    if (productHashCode == null) {
      productHashCode = new HashMap<>();
    }
    return productHashCode;
  }

  public void setProductHashCode(HashMap<Integer, String> productHashCode) {
    this.productHashCode = productHashCode;
  }

  public Integer getStockMovementType() {
    return stockMovementType;
  }

  public void setStockMovementType(Integer stockMovementType) {
    this.stockMovementType = stockMovementType;
  }

  public String getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchStockMovement(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getStockMovement().reset();
          getProductHashCode().clear();
          getStockMovement().setStockMovementType(getStockMovementType());
          setPageTitle(getStockMovementPageTitle(getStockMovementType()));
          setStockMovementItemList(null);
          addNewStockMovementItem();
          setDisabled(false);
        } else if (main.isEdit() && !main.hasError()) {
          selectStockMovementForEdit(main);
        } else if (main.isList()) {
          stockMovementLazyModel = null;
          setStockMovementItemList(null);
          getProductHashCode().clear();
          setStockMovementType(null);
          setPageTitle("Stock Movement");
          loadStockMovementList(main);
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
    main.setViewType(ViewTypes.newform);
    return null;
  }

  private String getStockMovementPageTitle(Integer stockMovementType) {
    return (stockMovementType == 1 ? "Stock Movement - Saleable To Damaged" : stockMovementType == 2 ? "Stock Movement - Damaged To Saleable" : "Stock Movement - Saleable To Expired");
  }

  /**
   * Create stockMovementLazyModel.
   *
   * @param main
   */
  private void loadStockMovementList(final MainView main) {
    if (stockMovementLazyModel == null) {
      stockMovementLazyModel = new LazyDataModel<StockMovement>() {
        private List<StockMovement> list;

        @Override
        public List<StockMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = StockMovementService.listPagedByMovementType(main, getStockMovementType(), UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
            main.commit(stockMovementLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(StockMovement stockMovement) {
          return stockMovement.getId();
        }

        @Override
        public StockMovement getRowData(String rowKey) {
          if (list != null) {
            for (StockMovement obj : list) {
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
    String SUB_FOLDER = "scm_stock_movement/";
  }

  private boolean isConfirmable() {
    boolean rvalue = false;
    if (!StringUtil.isEmpty(getStockMovementItemList())) {
      for (StockMovementItem stockMovementItem : getStockMovementItemList()) {
        if (stockMovementItem.getProductDetailId() != null && (stockMovementItem.getQuantitySaleableActual() != null || stockMovementItem.getQuantityDamagedActual() != null)) {
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
   * @return the page to display.
   */
  public String saveStockMovement(MainView main, int status) {
    if (status == SystemConstants.CONFIRMED && !isConfirmable()) {
      main.error("error.actual.qty.missing");
      return null;
    }
    getStockMovement().setStatusId(status);
    return saveOrCloneStockMovement(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneStockMovement(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneStockMovement(main, "clone");
  }

  public List<SelectItem> getStockMovementTypeList() {
    if (stockMovementTypeList == null) {
      stockMovementTypeList = new ArrayList<>();
      stockMovementTypeList.add(new SelectItem("Saleable to Damaged", SystemConstants.STOCK_MOVEMENT_SALEABLE_TO_DAMAGED));
      stockMovementTypeList.add(new SelectItem("Damaged to Saleable", SystemConstants.STOCK_MOVEMENT_DAMAGED_TO_SALEABLE));
      stockMovementTypeList.add(new SelectItem("Stock Movement to Expired", SystemConstants.STOCK_MOVEMENT_SALEABLE_TO_EXPIRED));
    }
    return stockMovementTypeList;
  }

  public void setStockMovementTypeList(List<SelectItem> stockMovementTypeList) {
    this.stockMovementTypeList = stockMovementTypeList;
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneStockMovement(MainView main, String key) {
    try {
      if (!isUsedByOtherUser(main)) {
        uploadFiles(); //File upload
        if (null != key) {
          switch (key) {
            case "save":
              StockMovementService.insertOrUpdateStockMovement(main, getStockMovement(), getStockMovementItemList());
              break;
            case "clone":
              StockMovementService.clone(main, getStockMovement());
              break;
          }
          main.commit("success." + key);
          main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
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
   * Delete one or many StockMovement.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteStockMovement(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(stockMovementSelected)) {
        StockMovementService.deleteByPkArray(main, getStockMovementSelected()); //many record delete from list
        main.commit("success.delete");
        stockMovementSelected = null;
      } else {
        StockMovementService.deleteByPk(main, getStockMovement());  //individual record delete from list or edit form
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

  public String deleteStockMovementSelected(MainView main) {
    try {
      if (!StringUtil.isEmpty(getStockMovementItemList())) {
        if (getStockMovementItemList() != null) {
          for (StockMovementItem list : getStockMovementItemList()) {
            StockMovementItemService.deleteByPk(main, list);
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  public boolean isDraft() {
    if (getStockMovement() != null && getStockMovement().getStatusId() != null) {
      return (getStockMovement().getStatusId().equals(SystemConstants.DRAFT));
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean isConfirmed() {
    if (getStockMovement() != null && getStockMovement().getStatusId() != null) {
      return (getStockMovement().getStatusId().equals(SystemConstants.CONFIRMED));
    }
    return false;
  }

  private void addNewStockMovementItem() {
    StockMovementItem stockMovementItem = new StockMovementItem();
    getStockMovementItemList().add(0, stockMovementItem);
  }

  /**
   * Method to select products based on account.
   *
   * @param filter
   * @return
   */
  public List<ProductDetail> completeProduct(String filter) {
    List<ProductDetail> productList = new ArrayList<>();
    MainView main = Jsf.getMain();
    try {
      if (stockMovement.getAccountId() != null || stockMovement.getAccountGroupId() != null) {
        StockMovementItem stockMovementItem = (StockMovementItem) Jsf.getAttribute("stockMovementItem");
        List<ProductDetail> list = ScmLookupExtView.lookupProductForStockAdjustment(filter, stockMovement.getAccountId(), stockMovement.getAccountGroupId());
        for (ProductDetail ps : list) {
          if (getProductHashCode().containsKey(ps.getId())) {
            if (stockMovementItem != null && stockMovementItem.getProductDetailId() != null && stockMovementItem.getProductDetailId().getId().equals(ps.getId())) {
              productList.add(ps);
            }
          } else {
            productList.add(ps);
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return productList;
  }

  public void productSelectEventHandler(StockMovementItem stockMovementItem, int index) {
    MainView main = Jsf.getMain();
    try {
      StockAdjustmentDetail stockAdjustmentDetail = StockMovementService.selectStockAdjustmentDetailByProductDetail(main, stockMovement, stockMovementItem.getProductDetailId());
      if (stockAdjustmentDetail != null) {
        stockMovementItem.setProductId(stockMovementItem.getProductDetailId().getProductBatchId().getProductId());
        stockMovementItem.setQuantityPurchased(stockAdjustmentDetail.getQuantityIn());
        stockMovementItem.setQuantityAvailable(stockAdjustmentDetail.getQuantitySaleable());
        stockMovementItem.setQuantityDamaged(stockAdjustmentDetail.getQuantityDamaged());
        stockMovementItem.after();
      }
      getProductHashCode().put(stockMovementItem.getProductDetailId().getId(), stockMovementItem.getProductDetailId().getProductBatchId().getBatchNo());
      removeProductHashKeys();
      setDisabled(true);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   * Method to remove unused keys from product hash map.
   */
  private void removeProductHashKeys() {
    boolean keyExist = false;
    ArrayList<Integer> keys = new ArrayList<>();
    for (Integer key : getProductHashCode().keySet()) {
      keyExist = false;
      for (StockMovementItem stockMovementItem : getStockMovementItemList()) {
        if (stockMovementItem.getProductDetailId() != null && key.equals(stockMovementItem.getProductDetailId().getId())) {
          keyExist = true;
        }
      }
      if (!keyExist) {
        keys.add(key);
      }
    }

    for (Integer key : keys) {
      getProductHashCode().remove(key);
    }
  }

  public void saleableQtyChangeEvent(StockMovementItem stockMovementItem) {
  }

  public void damagedQtyChangeEvent(StockMovementItem stockMovementItem) {
  }

  public void actionDeleteStockMovementItem(MainView main, StockMovementItem stockMovementItem) {
    try {
      if (!isUsedByOtherUser(main)) {
        if (stockMovementItem != null && stockMovementItem.getId() != null && stockMovementItem.getId() > 0) {
          StockMovementItemService.deleteByPk(main, stockMovementItem);
          getStockMovementItemList().remove(stockMovementItem);
          getProductHashCode().remove(stockMovementItem.getProductDetailId().getId());
        } else {
          if (stockMovementItem.getProductDetailId() != null) {
            getProductHashCode().remove(stockMovementItem.getProductDetailId().getId());
          }
          getStockMovementItemList().remove(stockMovementItem);
        }
        if (getStockMovementItemList().isEmpty()) {
          addNewStockMovementItem();
          setDisabled(false);
        }
      }
      Jsf.update("accountGroup");
      Jsf.update("account");
      Jsf.update("cal1");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionInsertOrUpdateStockMovementItem(MainView main) {
    boolean isNew = false;
    try {
      if (!isUsedByOtherUser(main)) {
        Integer index = Jsf.getParameterInt("rownumber");
        StockMovementItem stockMovementItem = getStockMovementItemList().get(index);
        if ((stockMovementItem != null && stockMovementItem.getProductDetailId() != null)
                && (stockMovementItem.getQuantitySaleableActual() != null || stockMovementItem.getQuantityDamagedActual() != null)) {

          if (stockMovementItem.getId() == null) {
            isNew = true;
          }

          getStockMovement().setStatusId(SystemConstants.DRAFT);
          StockMovementService.insertOrUpdate(main, getStockMovement());

          stockMovementItem.setStockMovementId(getStockMovement());

          StockMovementItemService.insertOrUpdate(main, stockMovementItem);

          if (isNew) {
            addNewStockMovementItem();
          } else if (index == 0) {
            addNewStockMovementItem();
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

  public List<StocktoExpiry> getStocktoExpiryList(MainView main) {
    List<StocktoExpiry> stocktoExpiryList = new ArrayList<>();
    try {
      if (isConfirmed()) {
        List<StockMovementItem> list = StockMovementItemService.getStockMovementItemForExpiry(main, stockMovement);
        stocktoExpiryList = StockMovementService.getMovedExpiryProducts(main, list);
      } else {
        stocktoExpiryList = StockMovementService.getExpiredProductList(main, getCompany(), stockMovement.getAccountGroupId(), stockMovement.getAccountId(), expiryDate);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return stocktoExpiryList;
  }

  public void moveToexpiry(MainView main) {
    try {
      if (!isUsedByOtherUser(main)) {
        if (stockMovement.getAccountGroupId() == null && stockMovement.getAccountId() == null) {
          List<Account> accountList = UserRuntimeView.instance().accountAuto("");
          for (Account account : accountList) {                                       //If movement done on company basis
            List<StocktoExpiry> stocktoExpiryList = StockMovementService.getExpiredProductforMovement(main, getCompany(), null, account, expiryDate);
            StockMovement movement = null;
            if (stocktoExpiryList != null && stocktoExpiryList.size() > 0) {
              movement = updateStockMovement(main, stockMovement, account);
              for (StocktoExpiry expiredList : stocktoExpiryList) {
                StockMovementItem stockMovementItem = null;
                stockMovementItem = updateStockMovementItem(main, movement, expiredList);
                StockSaleableService.stockMovementSaleableToExpired(main, stockMovementItem);
              }
            }
          }
        } else {
          List<StocktoExpiry> stocktoExpiryList = StockMovementService.getExpiredProductforMovement(main, getCompany(), stockMovement.getAccountGroupId(), stockMovement.getAccountId(), expiryDate);
          stockMovement = updateStockMovement(main, stockMovement, stockMovement.getAccountId());
          for (StocktoExpiry expiredList : stocktoExpiryList) {
            StockMovementItem stockMovementItem = null;
            stockMovementItem = updateStockMovementItem(main, stockMovement, expiredList);
            StockSaleableService.stockMovementSaleableToExpired(main, stockMovementItem);
          }
        }
        main.commit("success." + "save");
        main.setViewType(ViewTypes.list);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  private StockMovement updateStockMovement(MainView main, StockMovement stockMovement, Account account) {
    StockMovement movement = new StockMovement();
    movement.setAccountId(account);
    movement.setCompanyId(getCompany());
    movement.setConfirmedAt(new java.util.Date());
    movement.setConfirmedBy(main.getAppUser().getLogin());
    movement.setCreatedBy(main.getAppUser().getLogin());
    movement.setCreatedAt(new java.util.Date());
    movement.setStatusId(SystemConstants.CONFIRMED);
    movement.setDescription(stockMovement.getDescription());
    movement.setReferenceNo(stockMovement.getReferenceNo() + (account == null ? "" : "-" + account.getAccountCode()));
    movement.setEntryDate(stockMovement.getEntryDate());
    movement.setStockMovementType(stockMovementType);
    movement.setAccountGroupId(stockMovement.getAccountGroupId());
    StockMovementService.insert(main, movement);
    main.em().flush();
    return movement;
  }

  private StockMovementItem updateStockMovementItem(MainView main, StockMovement stockMovement, StocktoExpiry expired) {
    StockPreSaleService.deleteByProductEntryDetail(main, new ProductEntryDetail(expired.getProductEntryDetailId()));
    StockMovementItem stockMovementItem = new StockMovementItem();
    if (expired.getAccountId() != null) {
      stockMovementItem.setAccountId(new Account(expired.getAccountId()));
    }
    stockMovementItem.setStockMovementId(stockMovement);
    stockMovementItem.setProductId(new Product(expired.getProductId()));
    stockMovementItem.setProductDetailId(new ProductDetail(expired.getProductDetailId()));
    stockMovementItem.getProductDetailId().setAccountId(new Account(expired.getAccountId()));
    stockMovementItem.setProductEntryDetailId(new ProductEntryDetail(expired.getProductEntryDetailId()));
    int quantity = (expired.getQuantitySaleable() != null ? expired.getQuantitySaleable() : 0) + (expired.getQuantityFreeSaleable() != null ? expired.getQuantityFreeSaleable() : 0);
    stockMovementItem.setQuantityExpired(quantity);
    stockMovementItem.setStockId(expired.getStockId());
    StockMovementItemService.insert(main, stockMovementItem);
    return stockMovementItem;
  }

  public List<Account> accountList(String filter) {
    List<Account> accountList = null;
    accountList = UserRuntimeView.instance().accountAuto(filter);
    accountList.add(0, new Account(0, "--"));
    return accountList;
  }

  public List<AccountGroup> accountGroupList(String filter) {
    List<AccountGroup> accountGroupList = null;
    accountGroupList = UserRuntimeView.instance().accountGroupAuto(filter);
    accountGroupList.add(0, new AccountGroup(0, "--"));
    return accountGroupList;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public Date getExpiryDate() {
    if (expiryDate == null) {
      expiryDate = UserRuntimeView.instance().getToday();
    }
    return expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public void updateReferenceNo(MainView main) {
    try {
      nextInvoiceNo(main);
      if (stockMovement.getAccountId() != null) {
        stockMovement.setReferenceNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getStockMovement().getAccountId(), PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID, true, getStockMovement().getFinancialYearId()) + "-DR");
      } else if (stockMovement.getAccountGroupId() != null) {
        stockMovement.setReferenceNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getStockMovement().getAccountGroupId(), PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID, true, getStockMovement().getFinancialYearId()) + "-DR");
      } else if (stockMovement.getAccountId() == null && stockMovement.getAccountGroupId() == null) {
        stockMovement.setReferenceNo(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void nextInvoiceNo(MainView main) {
    try {
      if (stockMovement.getAccountId() != null) {
        nextInvoiceNumber = AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getStockMovement().getAccountId(), PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID, false, getStockMovement().getFinancialYearId());
      } else if (stockMovement.getAccountGroupId() != null) {
        nextInvoiceNumber = AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getStockMovement().getAccountGroupId(), PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID, false, getStockMovement().getFinancialYearId());
      } else if (stockMovement.getAccountGroupId() == null && getStockMovement().getAccountId() == null) {
        nextInvoiceNumber = null;
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public String getNextInvoiceNumber() {
    return nextInvoiceNumber;
  }

  private boolean isUsedByOtherUser(MainView main) {
    if (getStockMovement().getId() == null || StockMovementService.isStockMovementEditable(main, getStockMovement())) {
      return false;
    } else {
      main.error("error.past.record.edit");
      selectStockMovementForEdit(main);
      return true;
    }
  }

  private void selectStockMovementForEdit(MainView main) {
    setStockMovement((StockMovement) StockMovementService.selectByPk(main, getStockMovement()));
    setStockMovementType(getStockMovement().getStockMovementType());
    setStockMovementItemList(StockMovementItemService.selectStockMovementItemByStockMovement(main, getStockMovement()));
    if (getStockMovement().getStatusId() != null & getStockMovement().getStatusId().intValue() == SystemConstants.DRAFT) {
      deleteStockMovementSelected(main);
      setStockMovementItemList(null);
    }
    setPageTitle(getStockMovementPageTitle(getStockMovement().getStockMovementType()));
    if (StringUtil.isEmpty(getStockMovementItemList())) {
      addNewStockMovementItem();
      setDisabled(false);
    } else {
      for (StockMovementItem stockMovementItem : getStockMovementItemList()) {
        getProductHashCode().put(stockMovementItem.getProductDetailId().getId(), stockMovementItem.getProductDetailId().getProductBatchId().getBatchNo());
        stockMovementItem.after();
      }
      setDisabled(true);
    }
  }

  public Date getEntryDate() {
    if (getStockMovement() != null && getStockMovement().getStatusId() != null && getStockMovement().getStatusId().intValue() == 2) {
      entryDate = getStockMovement().getEntryDate();
    } else {
      entryDate = new Date();
      getStockMovement().setEntryDate(entryDate);
    }
    return entryDate;
  }
}
