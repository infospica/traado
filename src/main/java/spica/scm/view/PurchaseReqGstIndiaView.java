/*
 * @(#)PurchaseReqView.java	1.0 Mon Jul 24 18:05:03 IST 2017 
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
import spica.scm.common.ProductQuantityInfo;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.PurchaseReq;
import spica.scm.service.PurchaseReqService;
import spica.scm.domain.Contract;
import spica.scm.domain.PurchaseReqStatus;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.scm.domain.PurchaseReqItem;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.AccountService;
import spica.scm.service.PrefixTypeService;
import spica.scm.service.PurchaseReqItemService;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.faces.Jsf;

/**
 * PurchaseReqView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jul 24 18:05:03 IST 2017
 */
@Named(value = "purchaseReqGstIndiaView")
@ViewScoped
public class PurchaseReqGstIndiaView implements Serializable {

  private transient PurchaseReq purchaseReq;	//Domain object/selected Domain.
  private transient LazyDataModel<PurchaseReq> purchaseReqLazyModel; 	//For lazy loading datatable.
  private transient PurchaseReq[] purchaseReqSelected;	 //Selected Domain Array
  private transient List<PurchaseReqItem> purchaseReqItemList;
  private transient List<PurchaseReqStatus> purchaseReqStatusList;

  private transient Account account;
  private transient Contract contract;
  private transient Company company;
  private transient Account primaryAccountId;
  private transient Integer currentProductId = null;
  private transient Integer productNetQuantity;
  private transient HashMap<Integer, String> productMap;
  private transient Map<String, Object> purchaseReqStatusFilterMap;
  private transient Object filterValue;

  /**
   * Default Constructor.
   */
  public PurchaseReqGstIndiaView() {
    super();
  }

  /**
   * Return PurchaseReq.
   *
   * @return PurchaseReq.
   */
  public PurchaseReq getPurchaseReq() {
    if (purchaseReq == null) {
      purchaseReq = new PurchaseReq();
    }
    if (purchaseReq.getCompanyId() == null) {
      purchaseReq.setCompanyId(UserRuntimeView.instance().getCompany());
      purchaseReq.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    return purchaseReq;
  }

  /**
   * Set PurchaseReq.
   *
   * @param purchaseReq.
   */
  public void setPurchaseReq(PurchaseReq purchaseReq) {
    this.purchaseReq = purchaseReq;
  }

  /**
   *
   * @return
   */
  public Account getAccount() {
    account = UserRuntimeView.instance().getAccount();
    return account;
  }

  /**
   *
   * @param account
   */
  public void setAccount(Account account) {
    this.account = account;
  }

  /**
   *
   * @return
   */
  public Contract getContract() {
    contract = UserRuntimeView.instance().getContract();
    return contract;
  }

  /**
   *
   * @param contract
   */
  public void setContract(Contract contract) {
    this.contract = contract;
  }

  /**
   *
   * @return
   */
  public Account getPrimaryAccountId() {
    if (primaryAccountId == null) {
      primaryAccountId = UserRuntimeView.instance().getPrimaryAccount();
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
  public Company getCompany() {
    if (company == null) {
      company = UserRuntimeView.instance().getCompany();
    }
    return company;
  }

  /**
   *
   * @return
   */
  public HashMap<Integer, String> getProductMap() {
    if (productMap == null) {
      productMap = new HashMap<>();
    }
    return productMap;
  }

  /**
   *
   * @param requestedProduct
   */
  public void setProductMap(HashMap<Integer, String> productMap) {
    this.productMap = productMap;
  }

  /**
   *
   * @return
   */
  public Integer getCurrentProductId() {
    return currentProductId;
  }

  /**
   *
   * @param currentProductId
   */
  public void setCurrentProductId(Integer currentProductId) {
    this.currentProductId = currentProductId;
  }

  /**
   *
   * @return
   */
  public List<PurchaseReqItem> getPurchaseReqItemList() {
    if (purchaseReqItemList == null) {
      purchaseReqItemList = new ArrayList<>();
    }
    return purchaseReqItemList;
  }

  /**
   *
   * @param purchaseReqItemList
   */
  public void setPurchaseReqItemList(List<PurchaseReqItem> purchaseReqItemList) {
    this.purchaseReqItemList = purchaseReqItemList;
  }

  /**
   *
   * @return
   */
  public Integer getProductNetQuantity() {
    return productNetQuantity;
  }

  /**
   *
   * @param productNetQuantity
   */
  public void setProductNetQuantity(Integer productNetQuantity) {
    this.productNetQuantity = productNetQuantity;
  }

  /**
   *
   * @return
   */
  public List<PurchaseReqStatus> getPurchaseReqStatusList() {
    if (purchaseReqStatusList == null) {
      purchaseReqStatusList = ScmLookupExtView.purchaseReqStatus();
    }
    return purchaseReqStatusList;
  }

  /**
   *
   * @param purchaseReqStatusList
   */
  public void setPurchaseReqStatusList(List<PurchaseReqStatus> purchaseReqStatusList) {
    this.purchaseReqStatusList = purchaseReqStatusList;
  }

  /**
   *
   * @return
   */
  public Map<String, Object> getPurchaseReqStatusFilterMap() {
    if (purchaseReqStatusFilterMap == null) {
      purchaseReqStatusFilterMap = new HashMap<>();
    }
    return purchaseReqStatusFilterMap;
  }

  /**
   *
   * @param purchaseReqStatusFilterMap
   */
  public void setPurchaseReqStatusFilterMap(Map<String, Object> purchaseReqStatusFilterMap) {
    this.purchaseReqStatusFilterMap = purchaseReqStatusFilterMap;
  }

  /**
   *
   * @return
   */
  public Object getFilterValue() {
    return filterValue;
  }

  /**
   *
   * @param filterValue
   */
  public void setFilterValue(Object filterValue) {
    this.filterValue = filterValue;
  }

  /**
   * Return LazyDataModel of PurchaseReq.
   *
   * @return
   */
  public LazyDataModel<PurchaseReq> getPurchaseReqLazyModel() {
    return purchaseReqLazyModel;
  }

  /**
   * Return PurchaseReq[].
   *
   * @return
   */
  public PurchaseReq[] getPurchaseReqSelected() {
    return purchaseReqSelected;
  }

  /**
   * Set PurchaseReq[].
   *
   * @param purchaseReqSelected
   */
  public void setPurchaseReqSelected(PurchaseReq[] purchaseReqSelected) {
    this.purchaseReqSelected = purchaseReqSelected;
  }

  /**
   *
   * @param main
   */
  private void createNewPurchaseReq(MainView main) {
    String prefixValue = "";
    getPurchaseReq().reset();
    setPurchaseReqItemList(null);
    if (AccountService.INDIVIDUAL_SUPPLIER.equals(getAccount().getPurchaseChannel())) {
      prefixValue = AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getAccount(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, true, getPurchaseReq().getFinancialYearId());
    } else {
      prefixValue = AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getPrimaryAccountId(), PrefixTypeService.PURCHASE_REQ_INVOICE_PREFIX_ID, true, getPurchaseReq().getFinancialYearId());
    }
    getPurchaseReq().setAccountId(getAccount());
    getPurchaseReq().setContractId(getContract());
    getPurchaseReq().setRequisitionNo("DR-" + prefixValue);
    getPurchaseReq().setRequisitionDate(new Date());
    PurchaseReqStatus prStatus = new PurchaseReqStatus();
    prStatus.setId(SystemConstants.DRAFT);
    getPurchaseReq().setPurchaseRequisitionStatusId(prStatus);
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPurchaseReq(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        if (getAccount() == null) {
          main.error("account.required");
          if (ViewType.newform.toString().equals(viewType)) {
            main.setViewType(ViewTypes.list);
          }
          return null;
        } else if (getContract() == null) {
          main.error("contract.required");
          if (ViewType.newform.toString().equals(viewType)) {
            main.setViewType(ViewTypes.list);
          }
          return null;
        }
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getPurchaseReq().reset();
          createNewPurchaseReq(main);
          getProductMap().clear();
          if (getPurchaseReqItemList().isEmpty()) {
            addNewPurchaseReqItem();
          }
        } else if (main.isEdit() && !main.hasError()) {
          setPurchaseReq((PurchaseReq) PurchaseReqService.selectByPk(main, getPurchaseReq()));
          setPurchaseReqItemList(PurchaseReqItemService.selectPurchaseReqItemByPurchaseReq(main, getPurchaseReq()));

          getPurchaseReqItemList().forEach((pr) -> {
            getProductMap().put(pr.getProductId().getId(), pr.getProductId().getProductName());
          });

          setProductNetQuantity(getProdcutTotalQuantity());

          if (SystemConstants.DRAFT.equals(getPurchaseReq().getPurchaseRequisitionStatusId().getId()) && getPurchaseReqItemList().isEmpty()) {
            addNewPurchaseReqItem();
          }
        } else if (main.isList()) {
          setAccount(null);
          setContract(null);
          setProductNetQuantity(null);
          setPurchaseReqStatusFilterMap(null);
          setFilterValue(null);
          setPurchaseReqSelected(null);
          getProductMap().clear();
          loadPurchaseReqList(main);
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

  /**
   * Create purchaseReqLazyModel.
   *
   * @param main
   */
  private void loadPurchaseReqList(final MainView main) {
    if (purchaseReqLazyModel == null) {
      purchaseReqLazyModel = new LazyDataModel<PurchaseReq>() {
        private List<PurchaseReq> list;

        @Override
        public List<PurchaseReq> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            //list = PurchaseReqService.listPaged(main);
            list = PurchaseReqService.listPurchaseReqByAccount(main, UserRuntimeView.instance().getAccount(), getPurchaseReqStatusFilterMap());
            main.commit(purchaseReqLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PurchaseReq purchaseReq) {
          return purchaseReq.getId();
        }

        @Override
        public PurchaseReq getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReq obj : list) {
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
    String SUB_FOLDER = "scm_purchase_req/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePurchaseReq(MainView main, Integer status) {
    if (status.equals(SystemConstants.DRAFT)) {
      PurchaseReqStatus prStatus = new PurchaseReqStatus();
      prStatus.setId(SystemConstants.DRAFT);
      getPurchaseReq().setPurchaseRequisitionStatusId(prStatus);
    } else if (status.equals(SystemConstants.CONFIRMED)) {
      if (!isPurchaseReqConfirmable()) {
        main.error("error.product.missing");
        return null;
      }
      PurchaseReqStatus prStatus = new PurchaseReqStatus();
      prStatus.setId(SystemConstants.CONFIRMED);
      getPurchaseReq().setPurchaseRequisitionStatusId(prStatus);
    }
    return saveOrClonePurchaseReq(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePurchaseReq(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrClonePurchaseReq(main, "clone");
  }

  /**
   *
   */
  private void addNewPurchaseReqItem() {
    PurchaseReqItem reqItem = new PurchaseReqItem();
    getPurchaseReqItemList().add(0, reqItem);
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionNewPurchaseOrder(MainView main) {
    try {
      main.setViewPath(FileConstant.PURCHASE_ORDER_VIEW, ViewTypes.newform);
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePurchaseReq(MainView main, String key) {
    //boolean isNew = false;
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            if (getPurchaseReq().getId() == null) {
              //isNew = true;
            }
            PurchaseReqService.insertOrUpdate(main, getPurchaseReq());
            PurchaseReqItemService.insertOrUpdatePurchaseReqItem(main, getPurchaseReqItemList(), getPurchaseReq());
            break;
          case "clone":
            PurchaseReqService.clone(main, getPurchaseReq());
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
   *
   * @return
   */
  private boolean isPurchaseReqConfirmable() {
    boolean rvalue = false;
    if (getPurchaseReqItemList() != null) {
      for (PurchaseReqItem purchaseReqItem : getPurchaseReqItemList()) {
        if (purchaseReqItem.getProductId() != null && purchaseReqItem.getQtyRequired() != null && purchaseReqItem.getQtyRequired() > 0) {
          rvalue = true;
          break;
        }
      }
    }
    return rvalue;
  }

  /**
   *
   * @param main
   */
  public String actionInsertOrUpdatePurchaseReqItem(MainView main) {
    boolean isNew = false;
    try {
      if (isDraft()) {
        Integer rowIndex = Jsf.getParameterInt("rownumber");
        PurchaseReqItem purchaseReqItem = getPurchaseReqItemList().get(rowIndex);

        if (purchaseReqItem.getProductId() != null && purchaseReqItem.getQtyRequired() != null && purchaseReqItem.getQtyRequired() > 0) {
          if (purchaseReqItem.getId() == null) {
            isNew = true;
          }

          PurchaseReqService.insertOrUpdate(main, getPurchaseReq());

          purchaseReqItem.setProductName(purchaseReqItem.getProductId().getProductName());
          purchaseReqItem.setPurchaseRequisitionId(getPurchaseReq());
          PurchaseReqItemService.insertOrUpdate(main, purchaseReqItem);

          setProductNetQuantity(getProdcutTotalQuantity());

          if (isNew) {
            addNewPurchaseReqItem();
          } else if (rowIndex == 0) {
            addNewPurchaseReqItem();
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

  /**
   * Delete one or many PurchaseReq.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePurchaseReq(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(purchaseReqSelected)) {
        PurchaseReqService.deleteByPkArray(main, getPurchaseReqSelected()); //many record delete from list
        main.commit("success.delete");
        purchaseReqSelected = null;
      } else {
        PurchaseReqService.deleteByPk(main, getPurchaseReq());  //individual record delete from list or edit form
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

  /**
   * Method to delete PurchaseReqItem.
   *
   * @param main
   * @param purchaseReqItem
   * @return
   */
  public String actionDeletePurchaseReqItem(MainView main, PurchaseReqItem purchaseReqItem) {
    try {
      if (purchaseReqItem != null && purchaseReqItem.getId() == null) {
        getPurchaseReqItemList().remove(purchaseReqItem);
        if (purchaseReqItem.getProductId() != null) {
          getProductMap().remove(purchaseReqItem.getProductId().getId());
        }
      } else if (purchaseReqItem != null && purchaseReqItem.getId() != null) {
        PurchaseReqItemService.deleteByPk(main, purchaseReqItem);
        getPurchaseReqItemList().remove(purchaseReqItem);
        getProductMap().remove(purchaseReqItem.getProductId().getId());
      }
      if (getPurchaseReqItemList().isEmpty()) {
        addNewPurchaseReqItem();
      }
      setProductNetQuantity(getProdcutTotalQuantity());
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Method to select products based on its account group.
   *
   * @param filter
   * @return
   */
  public List<Product> completeProduct(String filter) {
    List<Product> productList = null;
    List<Product> completeProduct = new ArrayList<>();
    setCurrentProductId((Integer) Jsf.getAttribute("productId"));
    try {
      productList = ScmLookupExtView.selectProductForPurchaseReq(filter, getAccount().getId(), getCompany().getId(), getPurchaseReq().getId(), getCurrentProductId());

      for (Product product : productList) {
        if (getCurrentProductId() != null && product.getId().equals(getCurrentProductId())) {
          completeProduct.add(product);
          getProductMap().put(product.getId(), product.getProductName());
        }
        if (!getProductMap().containsKey(product.getId())) {
          completeProduct.add(product);
        }
      }
    } catch (Throwable t) {
      Jsf.fatal(t, "error.select");
    }
    return completeProduct;
  }

  /**
   *
   * @param purchaseReqItem
   */
  public void productSelectEvent(PurchaseReqItem purchaseReqItem) {
    MainView main = Jsf.getMain();
    try {
      if (purchaseReqItem.getProductId() != null) {
        purchaseReqItem.setProductName(purchaseReqItem.getProductId().getProductName());
        getProductMap().put(purchaseReqItem.getProductId().getId(), purchaseReqItem.getProductId().getProductName());
        removeProductHashKeys();
        ProductQuantityInfo pqi = PurchaseReqService.selectProductQuantityInfo(main, getAccount(), purchaseReqItem.getProductId());
        if (pqi != null) {
          purchaseReqItem.setCurrentQty(pqi.getQuantityAvailable());
          purchaseReqItem.setQtySuggested(pqi.getSuggestedQty());
        }
      }
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
    StringBuilder keys = new StringBuilder("");
    for (Integer key : getProductMap().keySet()) {
      keyExist = false;
      for (PurchaseReqItem pri : getPurchaseReqItemList()) {
        if (pri.getProductId() != null && key.equals(pri.getProductId().getId())) {
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
        getProductMap().remove(key);
      }
    }
  }

  /**
   *
   */
  public void productQtyBlurEvent() {
    setProductNetQuantity(getProdcutTotalQuantity());
  }

  /**
   *
   * @param eve
   */
  public void purchaseReqStatusFilterEventHandler(SelectEvent eve) {
    String filterKey = (String) Jsf.getAttribute("filterKey");
    if (eve.getObject() == null) {
      getPurchaseReqStatusFilterMap().remove(filterKey);
    } else {
      getPurchaseReqStatusFilterMap().put(filterKey, eve.getObject());
    }
  }

  /**
   * Method to get the requested total product quantity.
   *
   * @return quantity
   */
  private Integer getProdcutTotalQuantity() {
    Integer qty = 0;
    qty = getPurchaseReqItemList().stream().filter(item -> item.getQtyRequired() != null).mapToInt(PurchaseReqItem::getQtyRequired).sum();
    return qty;
  }

  /**
   *
   * @return
   */
  public boolean isDraft() {
    return ((getPurchaseReq() != null) && (getPurchaseReq().getPurchaseRequisitionStatusId() != null && getPurchaseReq().getPurchaseRequisitionStatusId().getId().equals(SystemConstants.DRAFT)));
  }

  /**
   *
   * @return
   */
  public boolean isConfirm() {
    return (getPurchaseReq().getPurchaseRequisitionStatusId() != null && getPurchaseReq().getPurchaseRequisitionStatusId().getId().equals(SystemConstants.CONFIRMED));
  }

  /**
   *
   * @return
   */
  public boolean isConfirmed() {
    return (getPurchaseReq().getPurchaseRequisitionStatusId() != null && getPurchaseReq().getPurchaseRequisitionStatusId().getId() > SystemConstants.DRAFT);
  }

}
