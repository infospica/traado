/*
 * @(#)StockAdjustmentView.java	1.0 Wed Mar 21 14:37:37 IST 2018 
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
import spica.constant.AccountingConstant;
import spica.scm.common.StockAdjustmentDetail;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.scm.domain.ProductDetail;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.StockAdjustment;
import spica.scm.service.StockAdjustmentService;
import spica.scm.domain.StockAdjustmentItem;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.AccountService;
import spica.scm.service.PrefixTypeService;
import spica.scm.service.StockAdjustmentItemService;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * StockAdjustmentView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 21 14:37:37 IST 2018
 */
@Named(value = "stockAdjustmentView")
@ViewScoped
public class StockAdjustmentView implements Serializable {

  private transient StockAdjustment stockAdjustment;	//Domain object/selected Domain.
  private transient LazyDataModel<StockAdjustment> stockAdjustmentLazyModel; 	//For lazy loading datatable.
  private transient StockAdjustment[] stockAdjustmentSelected;	 //Selected Domain Array
  private transient List<StockAdjustmentItem> stockAdjustmentItemList;
  private transient List<StockAdjustmentItem> stockAdjustmentItemSelected;
  private transient HashMap<Integer, String> productHashCode;
  private transient Integer totalQuantityExcess = 0;
  private transient Integer totalQuantityShortage = 0;
  private transient Double valueExcess = 0.00;
  private transient Double valueShortage = 0.00;
  private transient boolean disabled = false;
  private String nextInvoiceNumber;
  private transient boolean showExcessAdjustment;
  private transient Date entryDate;

  @PostConstruct
  public void init() {
    Integer stockAdjId = (Integer) Jsf.popupParentValue(Integer.class);
    if (stockAdjId != null) {
      getStockAdjustment().setId(stockAdjId);
    }
  }

  /**
   * Default Constructor.
   */
  public StockAdjustmentView() {
    super();
  }

  /**
   * Return StockAdjustment.
   *
   * @return StockAdjustment.
   */
  public StockAdjustment getStockAdjustment() {
    if (stockAdjustment == null) {
      stockAdjustment = new StockAdjustment();
    }
    if (stockAdjustment.getCompanyId() == null) {
      stockAdjustment.setCompanyId(UserRuntimeView.instance().getCompany());
      stockAdjustment.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    return stockAdjustment;
  }

  /**
   * Set StockAdjustment.
   *
   * @param stockAdjustment.
   */
  public void setStockAdjustment(StockAdjustment stockAdjustment) {
    this.stockAdjustment = stockAdjustment;
  }

  public List<StockAdjustmentItem> getStockAdjustmentItemList() {
    MainView main = Jsf.getMain();
    try {
      if (getStockAdjustment().getId() != null && StringUtil.isEmpty(stockAdjustmentItemList)) {
        setStockAdjustmentItemList(StockAdjustmentItemService.selectStockAdjustmentItemByStockAdjustment(main, getStockAdjustment()));
      }
    } catch (Exception t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return stockAdjustmentItemList;
  }

  public void setStockAdjustmentItemList(List<StockAdjustmentItem> stockAdjustmentItemList) {
    this.stockAdjustmentItemList = stockAdjustmentItemList;
  }

  /**
   * Return LazyDataModel of StockAdjustment.
   *
   * @return
   */
  public LazyDataModel<StockAdjustment> getStockAdjustmentLazyModel() {
    return stockAdjustmentLazyModel;
  }

  /**
   * Return StockAdjustment[].
   *
   * @return
   */
  public StockAdjustment[] getStockAdjustmentSelected() {
    return stockAdjustmentSelected;
  }

  /**
   * Set StockAdjustment[].
   *
   * @param stockAdjustmentSelected
   */
  public void setStockAdjustmentSelected(StockAdjustment[] stockAdjustmentSelected) {
    this.stockAdjustmentSelected = stockAdjustmentSelected;
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

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchStockAdjustment(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getStockAdjustment().reset();
          getProductHashCode().clear();
          getStockAdjustment().setEntryDate(new Date());
          getStockAdjustment().setStockAdjustmentType(SystemConstants.STOCK_ADJUSTMENT_TYPE_SALEABLE);
          setStockAdjustmentItemList(null);
          addNewStockAdjustmentItem();
        } else if (main.isEdit() && !main.hasError()) {
          setStockAdjustment((StockAdjustment) StockAdjustmentService.selectByPk(main, getStockAdjustment()));

          setStockAdjustmentItemList(StockAdjustmentItemService.selectStockAdjustmentItemByStockAdjustment(main, getStockAdjustment()));
          if (getStockAdjustment().getStatusId().equals(SystemConstants.DRAFT)) {
            nextInvoiceNo(main);
            setStockAdjustmentItemSelected(getStockAdjustmentItemList());
            deleteStockAdjustmentItem(main, false);
            setStockAdjustmentItemList(null);
            addNewStockAdjustmentItem();
            return null;
          }
          if (StringUtil.isEmpty(getStockAdjustmentItemList())) {
            addNewStockAdjustmentItem();
          } else {
            for (StockAdjustmentItem stockAdjustmentItem : getStockAdjustmentItemList()) {
              getProductHashCode().put(stockAdjustmentItem.getProductDetailId().getId(), stockAdjustmentItem.getStockAdjustmentDetail().getBatchNo());
              stockAdjustmentItem.after();
              int excessqty = 0, shortageQty = 0;
              Double valueExcess = 0.00, valueShortage = 0.00;
              stockAdjustmentItem.setQuantityAvailable(stockAdjustmentItem.getQuantityAvailable() != null ? stockAdjustmentItem.getQuantityAvailable() : 0);
              if (stockAdjustmentItem.getQuantitySaleableActual() != null) {
                if (stockAdjustmentItem.getQuantityAvailable() < stockAdjustmentItem.getQuantitySaleableActual()) {
                  excessqty = stockAdjustmentItem.getQuantitySaleableActual() - stockAdjustmentItem.getQuantityAvailable();
                  valueExcess = excessqty * (stockAdjustmentItem.getLandRate() != null ? stockAdjustmentItem.getLandRate() : 0);
                } else if (stockAdjustmentItem.getQuantityAvailable() > stockAdjustmentItem.getQuantitySaleableActual()) {
                  shortageQty = stockAdjustmentItem.getQuantityAvailable() - stockAdjustmentItem.getQuantitySaleableActual();
                  valueShortage = shortageQty * (stockAdjustmentItem.getLandRate() != null ? stockAdjustmentItem.getLandRate() : 0);
                }
              }
              totalQuantityExcess += excessqty;
              totalQuantityShortage += shortageQty;
              this.valueExcess += valueExcess;
              this.valueShortage += valueShortage;
            }
          }
        } else if (main.isList()) {
          setShowExcessAdjustment(true);
          stockAdjustmentLazyModel = null;
          setStockAdjustmentItemList(null);
          getProductHashCode().clear();
          loadStockAdjustmentList(main);
        }
        main.commit();
        setStockAdjustmentItemSelected(null);
        setStockAdjustmentSelected(null);
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create stockAdjustmentLazyModel.
   *
   * @param main
   */
  private void loadStockAdjustmentList(final MainView main) {
    if (stockAdjustmentLazyModel == null) {
      stockAdjustmentLazyModel = new LazyDataModel<StockAdjustment>() {
        private List<StockAdjustment> list;

        @Override
        public List<StockAdjustment> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = StockAdjustmentService.listPagedByCompany(main, showExcessAdjustment, UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
            main.commit(stockAdjustmentLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(StockAdjustment stockAdjustment) {
          return stockAdjustment.getId();
        }

        @Override
        public StockAdjustment getRowData(String rowKey) {
          if (list != null) {
            for (StockAdjustment obj : list) {
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
    String SUB_FOLDER = "scm_stock_adjustment/";
  }

  private boolean isConfirmable() {
    boolean rvalue = false;
    if (!StringUtil.isEmpty(getStockAdjustmentItemList())) {
      for (StockAdjustmentItem stockAdjustmentItem : getStockAdjustmentItemList()) {
        if (stockAdjustmentItem.getProductDetailId() != null && stockAdjustmentItem.getStockAdjustmentDetail() != null
                && (stockAdjustmentItem.getQuantitySaleableActual() != null || stockAdjustmentItem.getQuantityDamagedActual() != null)) {
          rvalue = true;
          break;
        }
      }
    }
    return rvalue;
  }

  private boolean isDraftable() {
    boolean rvalue = false;
    if (!StringUtil.isEmpty(getStockAdjustmentItemList())) {
      for (StockAdjustmentItem stockAdjustmentItem : getStockAdjustmentItemList()) {
        if (stockAdjustmentItem.getProductDetailId() != null && stockAdjustmentItem.getStockAdjustmentDetail() != null
                && (stockAdjustmentItem.getQuantitySaleableActual() != null || stockAdjustmentItem.getQuantityDamagedActual() != null)) {
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
  public String saveStockAdjustment(MainView main, int status) {
    if (stockAdjustment.getAccountGroupId() == null && stockAdjustment.getAccountId() == null) {
      main.error("error.select.accountorAccountgroup");
      return null;
    }
    if (status == SystemConstants.CONFIRMED && !isConfirmable()) {
      main.error("error.actual.qty.missing");
      return null;
    }
    if (stockAdjustment.getId() != null && status == SystemConstants.DRAFT && !isDraftable()) {
      main.error("error.actual.qty.missing");
      return null;
    }
    getStockAdjustment().setStatusId(status);
    return saveOrCloneStockAdjustment(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneStockAdjustment(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneStockAdjustment(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneStockAdjustment(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            StockAdjustmentService.insertOrUpdateStockAdjustment(main, getStockAdjustment(), getStockAdjustmentItemList());
            break;
          case "clone":
            StockAdjustmentService.clone(main, getStockAdjustment());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      getStockAdjustment().setStatusId(SystemConstants.DRAFT);
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many StockAdjustment.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteStockAdjustment(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(stockAdjustmentSelected)) {
        StockAdjustmentService.deleteByPkArray(main, getStockAdjustmentSelected()); //many record delete from list
        main.commit("success.delete");
        stockAdjustmentSelected = null;
      } else {
        StockAdjustmentService.deleteByPk(main, getStockAdjustment());  //individual record delete from list or edit form
        main.commit("success.delete");
        setStockAdjustmentItemList(null);
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

  private void addNewStockAdjustmentItem() {
    StockAdjustmentItem stockAdjustmentItem = new StockAdjustmentItem();
    if (StringUtil.isEmpty(stockAdjustmentItemList)) {
      stockAdjustmentItemList = new ArrayList<>();
    }
    getStockAdjustmentItemList().add(0, stockAdjustmentItem);
  }

  /**
   *
   * @return
   */
  public boolean isDraft() {
    if (getStockAdjustment() != null && getStockAdjustment().getStatusId() != null) {
      return (getStockAdjustment().getStatusId().equals(SystemConstants.DRAFT));
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean isConfirmed() {
    if (getStockAdjustment() != null && getStockAdjustment().getStatusId() != null) {
      return (getStockAdjustment().getStatusId().equals(SystemConstants.CONFIRMED));
    }
    return false;
  }

  public boolean isReverseEntry() {
    if (getStockAdjustment() != null && getStockAdjustment().getReverseEntry() != null) {
      return (getStockAdjustment().getReverseEntry().equals(AccountingConstant.REVERSE_ENTRY));
    }
    return false;
  }

  /**
   *
   * @return
   */
  public String getStatusTitle() {
    String statusTitle = "";
    if (isDraft()) {
      statusTitle = "Draft";
    } else if (isConfirmed()) {
      statusTitle = "Confirmed";
    }
    return statusTitle;
  }

  public void adjustmentFilterSelectEvent(MainView main) {
    main.getPageData().reset();
  }

  /**
   * Method to select products based on account.
   *
   * @param filter
   * @return
   */
  public List<Product> completeProduct(String filter) {
    List<Product> productList = new ArrayList<>();
    MainView main = Jsf.getMain();
    try {
      if (stockAdjustment.getAccountGroupId() != null || stockAdjustment.getAccountId() != null) {
        productList = ScmLookupExtView.lookupProductsForStockAdjustment(filter, stockAdjustment.getAccountId(), stockAdjustment.getAccountGroupId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return productList;
  }

  /**
   *
   * @param stockAdjustmentItem
   * @param index
   */
  public void productSelectEventHandler(StockAdjustmentItem stockAdjustmentItem, int index) {
    stockAdjustmentItem.setProductId(stockAdjustmentItem.getProductId());
    stockAdjustmentItem.setStockAdjustmentDetail(null);
    stockAdjustmentItem.setPackSize(null);
    stockAdjustmentItem.setValueMrp(null);
    stockAdjustmentItem.setValuePts(null);
    stockAdjustmentItem.setLandRate(null);
    stockAdjustmentItem.setQuantityAvailable(null);
    stockAdjustmentItem.setQuantitySaleableActual(null);
    stockAdjustmentItem.setQuantityDamaged(null);
    stockAdjustmentItem.setQuantityDamagedActual(null);
    stockAdjustmentItem.after();
    setDisabled(true);
    Jsf.update("accountGroupDiv");
    Jsf.update("accountDiv");
  }

  /**
   *
   * @param stockAdjustmentItem
   * @param index
   */
  public List<StockAdjustmentDetail> stockAdjustmentBatchAuto(String filter) {
    StockAdjustmentItem stockAdjustmentItem = (StockAdjustmentItem) Jsf.getAttribute("selectItem");
    MainView main = Jsf.getMain();
    try {
      stockAdjustmentItem.setStockAdjustmentDetailList(StockAdjustmentService.updateStockAdjustmentDetail(main, getStockAdjustment(), stockAdjustmentItem, filter));
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return stockAdjustmentItem.getStockAdjustmentDetailList();
  }

  /**
   * Method to remove unused keys from product hash map.
   */
  private void removeProductHashKeys() {
    boolean keyExist = false;
    ArrayList<Integer> keys = new ArrayList<>();
    for (Integer key : getProductHashCode().keySet()) {
      keyExist = false;
      for (StockAdjustmentItem stockAdjustmentItem : getStockAdjustmentItemList()) {
        if (stockAdjustmentItem.getProductDetailId() != null && key.equals(stockAdjustmentItem.getProductDetailId().getId())) {
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

  /**
   *
   * @param main
   * @param stockAdjustmentItem
   */
  private void actionDeleteStockAdjustmentItem(MainView main, StockAdjustmentItem stockAdjustmentItem, boolean addNew) {
    if (stockAdjustmentItem != null && stockAdjustmentItem.getId() != null && stockAdjustmentItem.getId() > 0) {
      StockAdjustmentItemService.deleteByProductDetail(main, stockAdjustmentItem);
      if (addNew) {
        getStockAdjustmentItemList().remove(stockAdjustmentItem);
        getProductHashCode().remove(stockAdjustmentItem.getProductDetailId().getId());
      }
    } else {
      if (stockAdjustmentItem.getProductDetailId() != null) {
        getProductHashCode().remove(stockAdjustmentItem.getProductDetailId().getId());
      }
      getStockAdjustmentItemList().remove(stockAdjustmentItem);
    }
    if (getStockAdjustmentItemList().isEmpty() && addNew) {
      addNewStockAdjustmentItem();
    }
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionInsertOrUpdateStockAdjustmentItem(MainView main) {
    boolean isNew = false;
    try {
      Integer index = Jsf.getParameterInt("rownumber");
      StockAdjustmentItem stockAdjustmentItem = getStockAdjustmentItemList().get(index);
      getProductHashCode().put(stockAdjustmentItem.getProductDetailId().getId(), stockAdjustmentItem.getStockAdjustmentDetail().getBatchNo());
      removeProductHashKeys();
      if ((stockAdjustmentItem != null && stockAdjustmentItem.getProductDetailId() != null)
              && (stockAdjustmentItem.getQuantitySaleableActual() != null || stockAdjustmentItem.getQuantityDamagedActual() != null)) {

        if (stockAdjustmentItem.getId() == null) {
          isNew = true;
        }

        getStockAdjustment().setStatusId(SystemConstants.DRAFT);
        if (stockAdjustmentItem.getId() != null) {
          StockAdjustmentItemService.deleteByProductDetailOriginal(main, stockAdjustmentItem);
        }
        stockAdjustmentItem.setId(null);
        StockAdjustmentService.insertOrUpdate(main, getStockAdjustment());

        stockAdjustmentItem.setStockAdjustmentId(getStockAdjustment());

        StockAdjustmentItemService.insert(main, stockAdjustmentItem);
        stockAdjustmentItem.setProductDetailOriginal(stockAdjustmentItem.getProductDetailId());
        if (isNew) {
          addNewStockAdjustmentItem();
        } else if (index == 0) {
          addNewStockAdjustmentItem();
        }
      }
      main.commit();
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param stockAdjustmentItem
   */
  public void saleableQtyChangeEvent(StockAdjustmentItem stockAdjustmentItem) {
    int purchaseQty = stockAdjustmentItem.getQuantityPurchased() == null ? 0 : stockAdjustmentItem.getQuantityPurchased();
    int saleableQty = stockAdjustmentItem.getQuantitySaleableActual() == null ? 0 : stockAdjustmentItem.getQuantitySaleableActual();
    stockAdjustmentItem.setMaxDamagedActualQty(purchaseQty - saleableQty);
  }

  /**
   *
   * @param stockAdjustmentItem
   */
  public void damagedQtyChangeEvent(StockAdjustmentItem stockAdjustmentItem) {
    int purchaseQty = stockAdjustmentItem.getQuantityPurchased() == null ? 0 : stockAdjustmentItem.getQuantityPurchased();
    int actualDamagedQty = stockAdjustmentItem.getQuantityDamagedActual() == null ? 0 : stockAdjustmentItem.getQuantityDamagedActual();
    stockAdjustmentItem.setMaxSaleableActualQty(purchaseQty - actualDamagedQty);
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionResetStockAdjustment(MainView main) {
    try {
      StockAdjustmentService.resetStockAdjustmentToDraft(main, getStockAdjustment());
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  public String decimalCheck(String s) {
    String arr[] = s.split("\\.");
    if (arr[1].charAt(0) == '0') {
      s = arr[0] + ' ' + arr[1].replaceAll("[^A-Za-z]+", "");
      return s;
    } else {
      return s;
    }
  }

  public Integer getTotalQuantityExcess() {
    return totalQuantityExcess;
  }

  public void setTotalQuantityExcess(Integer totalQuantityExcess) {
    this.totalQuantityExcess = totalQuantityExcess;
  }

  public Integer getTotalQuantityShortage() {
    return totalQuantityShortage;
  }

  public void setTotalQuantityShortage(Integer totalQuantityShortage) {
    this.totalQuantityShortage = totalQuantityShortage;
  }

  public Double getValueExcess() {
    return valueExcess;
  }

  public void setValueExcess(Double valueExcess) {
    this.valueExcess = valueExcess;
  }

  public Double getValueShortage() {
    return valueShortage;
  }

  public void setValueShortage(Double valueShortage) {
    this.valueShortage = valueShortage;
  }

  public void reset() {
    setTotalQuantityExcess(0);
    setTotalQuantityShortage(0);
    setValueExcess(0.00);
    setValueShortage(0.00);
    setDisabled(false);
    setStockAdjustmentItemList(null);
  }

  public List<StockAdjustmentItem> getStockAdjustmentItemSelected() {
    return stockAdjustmentItemSelected;
  }

  public void setStockAdjustmentItemSelected(List<StockAdjustmentItem> stockAdjustmentItemSelected) {
    this.stockAdjustmentItemSelected = stockAdjustmentItemSelected;
  }

  public void deleteStockAdjustmentItem(MainView main, boolean showMessage) {
    try {
      if (stockAdjustmentItemSelected != null) {
        for (StockAdjustmentItem selected : stockAdjustmentItemSelected) {
          actionDeleteStockAdjustmentItem(main, selected, showMessage);
        }
        if (showMessage) {
          main.commit("success.delete");
        }
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
//      StringUtil.clearList(stockAdjustmentItemSelected);
    }
  }

  public Long getRowKey() {
    return new Date().getTime();
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

  public void batchSelectEventHandler(MainView main, StockAdjustmentItem stockAdjustmentItem) {
//    MainView main = Jsf.getMain();
    try {
      StockAdjustmentDetail stockAdjustmentDetail = stockAdjustmentItem.getStockAdjustmentDetail();
      if (stockAdjustmentDetail != null) {
        Account account = AccountService.selectById(main, stockAdjustmentDetail.getAccountId());
        stockAdjustmentItem.setAccountId(account);
        stockAdjustmentItem.setProductDetailId(new ProductDetail(stockAdjustmentDetail.getProductDetailId()));
        stockAdjustmentItem.setQuantityPurchased(stockAdjustmentDetail.getQuantityIn());
        stockAdjustmentItem.setQuantityAvailable(stockAdjustmentDetail.getQuantitySaleable());
        stockAdjustmentItem.setQuantityDamaged(stockAdjustmentDetail.getQuantityDamaged());
        stockAdjustmentItem.after();
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public boolean isDisabled() {
    if (stockAdjustmentItemList != null && stockAdjustmentItemList.get(0) != null && stockAdjustmentItemList.get(0).getProductDetailId() != null) {
      disabled = true;
    }
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public void updateReferenceNo(MainView main) {
    try {
      nextInvoiceNo(main);
      if (stockAdjustment.getReverseEntry() == null && stockAdjustment.getStockAdjustmentType() == SystemConstants.STOCK_ADJUSTMENT_TYPE_SALEABLE) {
        if (stockAdjustment.getAccountId() != null) {
          getStockAdjustment().setReferenceNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getStockAdjustment().getAccountId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, true, getStockAdjustment().getFinancialYearId()) + "-DR");
        } else if (stockAdjustment.getAccountGroupId() != null) {
          getStockAdjustment().setReferenceNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getStockAdjustment().getAccountGroupId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, true, getStockAdjustment().getFinancialYearId()) + "-DR");
        } else if (stockAdjustment.getAccountId() == null && stockAdjustment.getAccountGroupId() == null) {
          getStockAdjustment().setReferenceNo(null);
        }
      }
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void nextInvoiceNo(MainView main) {
    if (stockAdjustment.getAccountId() != null) {
      nextInvoiceNumber = AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getStockAdjustment().getAccountId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, false, getStockAdjustment().getFinancialYearId());
    } else if (stockAdjustment.getAccountGroupId() != null) {
      nextInvoiceNumber = AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getStockAdjustment().getAccountGroupId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, false, getStockAdjustment().getFinancialYearId());
    } else if (stockAdjustment.getAccountGroupId() == null && stockAdjustment.getAccountId() == null) {
      nextInvoiceNumber = null;
    }
  }

  public String getNextInvoiceNumber() {
    return nextInvoiceNumber;
  }

  public boolean isShowExcessAdjustment() {
    return showExcessAdjustment;
  }

  public void setShowExcessAdjustment(boolean showExcessAdjustment) {
    this.showExcessAdjustment = showExcessAdjustment;
  }

  public Date getEntryDate() {
    if (getStockAdjustment() != null && getStockAdjustment().getStatusId() != null && getStockAdjustment().getStatusId().intValue() == 2) {
      entryDate = getStockAdjustment().getEntryDate();
    } else {
      entryDate = new Date();
      getStockAdjustment().setEntryDate(entryDate);
    }
    return entryDate;
  }

  public void setEntryDate(Date entryDate) {
    this.entryDate = entryDate;
  }

}
