/*
 * @(#)ProductPricelistView.java	1.0 Wed Apr 13 15:41:14 IST 2016 
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ProductPricelist;
import spica.scm.service.ProductPricelistService;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.ProductPriceListVo;
import spica.scm.service.AccountGroupPriceListService;
import spica.sys.SystemConstants;

import wawo.app.faces.Jsf;

/**
 * ProductPricelistView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:14 IST 2016
 */
@Named(value = "productPricelistView")
@ViewScoped
public class ProductPricelistView implements Serializable {

  private transient ProductPricelist productPricelist;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductPricelist> productPricelistLazyModel; 	//For lazy loading datatable.
  private transient ProductPricelist[] productPricelistSelected;	 //Selected Domain Array 
  // private List<ProductPricelist> productPriceList;
  private int activeIndex;
  private Account account;
  private AccountGroup accoutGroup;
  private AccountGroupPriceList accountGroupPriceList;
  private ProductPriceListVo productPriceListVo;
  // private List<PriceListDetails> priceListDetailsList;
  private transient Integer appliedOn;
  private transient Integer rateCriteria;
  private transient String[] selectedPriceTypes;
  private List<ProductPriceListVo> productPriceListVoList;
  private Map<String, List> productPriceListMap;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    account = (Account) Jsf.popupParentValue(Account.class);
    accoutGroup = (AccountGroup) Jsf.popupParentValue(AccountGroup.class);
    getAccountGroupPriceList().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public ProductPricelistView() {
    super();
  }

  /**
   * Return ProductPricelist.
   *
   * @return ProductPricelist.
   */
  public ProductPricelist getProductPricelist() {
    if (productPricelist == null) {
      productPricelist = new ProductPricelist();
    }
    return productPricelist;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public AccountGroup getAccoutGroup() {
    return accoutGroup;
  }

  public void setAccoutGroup(AccountGroup accoutGroup) {
    this.accoutGroup = accoutGroup;
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  public Integer getAppliedOn() {
    return appliedOn;
  }

  public void setAppliedOn(Integer appliedOn) {
    this.appliedOn = appliedOn;
  }

  public Integer getRateCriteria() {
    return rateCriteria;
  }

  public void setRateCriteria(Integer rateCriteria) {
    this.rateCriteria = rateCriteria;
  }

  /**
   *
   * @param main
   * @return
   */
//  public List<ProductPricelist> getProductPriceList(MainView main) {
//    if (productPriceList == null) {
//      try {
//        if (getAccountGroupPriceList().getId() != null) {
//          productPriceList = ProductPricelistService.selectDefaultProductPriceList(main, getAccountGroupPriceList());
//        } else {
//          productPriceList = ProductPricelistService.selectDefaultProductPriceList(main, getAccount());
//        }
//
//      } catch (Throwable t) {
//        main.rollback(t, "error.select");
//      } finally {
//        main.close();
//      }
//
//    }
//    return productPriceList;
//  }
//
//  public void setProductPriceList(List<ProductPricelist> productPriceList) {
//    this.productPriceList = productPriceList;
//  }
  public AccountGroupPriceList getAccountGroupPriceList() {
    if (accountGroupPriceList == null) {
      accountGroupPriceList = new AccountGroupPriceList();
    }
    return accountGroupPriceList;
  }

  public void setAccountGroupPriceList(AccountGroupPriceList accountGroupPriceList) {
    this.accountGroupPriceList = accountGroupPriceList;
  }

  /**
   * Set ProductPricelist.
   *
   * @param productPricelist.
   */
  public void setProductPricelist(ProductPricelist productPricelist) {
    this.productPricelist = productPricelist;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductPricelist(MainView main, String viewType) {
    //this.main = main;    
    if (!StringUtil.isEmpty(viewType)) {
      setProductPriceListVoList(null);
      setAppliedOn(1);
      setRateCriteria(1);
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getProductPricelist().reset();
//          getProductPricelist().setProductDetailId(parent);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setAccountGroupPriceList(AccountGroupPriceListService.selectByPk(main, getAccountGroupPriceList()));
          main.setViewType(viewType);
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductPricelistList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  public List<ProductPriceListVo> getProductPriceListVoList(MainView main) {
    if (StringUtil.isEmpty(productPriceListVoList)) {
      try {
        if (getAccount() != null) {
          productPriceListVoList = ProductPricelistService.selectProductPriceListByAccount(main, getAccountGroupPriceList(), getAccount());
        } else if (getAccoutGroup() != null) {
          productPriceListVoList = ProductPricelistService.selectProductPriceListByAccountGroup(main, getAccountGroupPriceList(), getAccoutGroup());
        }
        productPriceListVoList = mergeToPriceListGroup(productPriceListVoList);
//        for (ProductPriceListVo priceListVo : productPriceListVoList) {
//          priceListVo.setPriceListDetailsList(getPriceListDetailList(main, priceListVo));
//        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return productPriceListVoList;
  }

//  public List<ProductPriceListVo> getProductPriceListVoList() {
//    return productPriceListVoList;
//  }
  public void setProductPriceListVoList(List<ProductPriceListVo> productPriceListVoList) {
    this.productPriceListVoList = productPriceListVoList;
  }

  /**
   * Create productPricelistLazyModel.
   *
   * @param main
   */
  private void loadProductPricelistList(final MainView main) {
    if (productPricelistLazyModel == null) {
      productPricelistLazyModel = new LazyDataModel<ProductPricelist>() {
        private List<ProductPricelist> list;

        @Override
        public List<ProductPricelist> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            //list = ProductPricelistService.listPaged(main, parent);
            main.commit(productPricelistLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductPricelist productPricelist) {
          return productPricelist.getId();
        }

        @Override
        public ProductPricelist getRowData(String rowKey) {
          if (list != null) {
            for (ProductPricelist obj : list) {
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
    String SUB_FOLDER = "scm_product_pricelist/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductPricelist(MainView main) {
    return saveOrCloneProductPricelist(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductPricelist(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductPricelist(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductPricelist(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductPricelistService.insertOrUpdate(main, getProductPricelist());
            break;
          case "clone":
            ProductPricelistService.clone(main, getProductPricelist());
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
   * @param main
   * @return
   */
  public String saveProductPricelistVo(MainView main) {
    try {
      if (productPriceListVoList != null && !productPriceListVoList.isEmpty()) {

        ProductPricelistService.insertOrUpdatePriceListVo(main, reArrangeProductPriceList(), getAccountGroupPriceList(), null);
        main.commit("success.save");
        productPriceListVoList = null;
        //main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many ProductPricelist.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductPricelist(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productPricelistSelected)) {
        ProductPricelistService.deleteByPkArray(main, getProductPricelistSelected()); //many record delete from list
        main.commit("success.delete");
        productPricelistSelected = null;
      } else {
        ProductPricelistService.deleteByPk(main, getProductPricelist());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductPricelist.
   *
   * @return
   */
  public LazyDataModel<ProductPricelist> getProductPricelistLazyModel() {
    return productPricelistLazyModel;
  }

  /**
   * Return ProductPricelist[].
   *
   * @return
   */
  public ProductPricelist[] getProductPricelistSelected() {
    return productPricelistSelected;
  }

  /**
   * Set ProductPricelist[].
   *
   * @param productPricelistSelected
   */
  public void setProductPricelistSelected(ProductPricelist[] productPricelistSelected) {
    this.productPricelistSelected = productPricelistSelected;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void productPricelistDialogClose() {
    Jsf.returnDialog(null);
  }

  /**
   *
   * @return
   */
  public boolean isNotDefault() {
    return getAccountGroupPriceList().getIsDefault() != null && getAccountGroupPriceList().getIsDefault() != 1;
  }

  /**
   *
   */
  public void productPriceListVoClose() {
    if (getAccount() != null) {
      Jsf.popupClose(getAccount());
    } else if (getAccoutGroup() != null) {
      Jsf.popupClose(getAccoutGroup());
    }

  }

  public void addPercantageToPTS(MainView main) {

    if (productPriceListVoList != null && !productPriceListVoList.isEmpty()) {
      List<String> pTypeList = Arrays.asList(selectedPriceTypes);
      for (ProductPriceListVo list : productPriceListVoList) {
        list.setDataModified(true);
        if (list.getDefaultprice() != null) {
          if (getAccountGroupPriceList().getVariationPercentage() != null) {
//            if (getAppliedOn() == 1) {
//              if (pTypeList.contains("PTS")) {
//                if (getRateCriteria() == SystemConstants.RATE_DERIVATION_MARK_UP) {
//                  list.setValuePtsPerProdPieceSell(list.getLandingRate() * (1 + (getAccountGroupPriceList().getVariationPercentage() / 100)));
//                } else {
//                  list.setValuePtsPerProdPieceSell(list.getLandingRate() * (1 - (getAccountGroupPriceList().getVariationPercentage() / 100)));
//                }
//              }
//              if (pTypeList.contains("PTR")) {
//                if (getRateCriteria() == SystemConstants.RATE_DERIVATION_MARK_UP) {
//                  list.setValuePtrPerProdPiece(list.getValuePtsPerProdPieceSell() * (1 + ((getAccountGroupPriceList().getVariationPercentage() / 100))));
//                } else {
//                  list.setValuePtrPerProdPiece(list.getValuePtsPerProdPieceSell() * (1 - ((getAccountGroupPriceList().getVariationPercentage() / 100))));
//                }
//              }
//            } else {
            if (pTypeList.contains("PTS")) {
              if (getRateCriteria() == SystemConstants.RATE_DERIVATION_MARK_UP && list.getDefaultprice() != null) {
                list.setValuePtsPerProdPieceSell(list.getDefaultprice() * (1 + (getAccountGroupPriceList().getVariationPercentage() / 100)));
              }
              if (getRateCriteria() == SystemConstants.RATE_DERIVATION_MARK_DOWN && list.getDefaultprice() != null) {
                list.setValuePtsPerProdPieceSell(list.getDefaultprice() / (1 - (getAccountGroupPriceList().getVariationPercentage() / 100)));
              }
            }
            if (pTypeList.contains("PTR")) {
              if (getRateCriteria() == SystemConstants.RATE_DERIVATION_MARK_UP && list.getDefaultPtr() != null) {
                list.setValuePtrPerProdPiece(list.getDefaultPtr() * (1 + (getAccountGroupPriceList().getVariationPercentage() / 100)));
              }
              if (getRateCriteria() == SystemConstants.RATE_DERIVATION_MARK_DOWN && list.getDefaultPtr() != null) {
                list.setValuePtrPerProdPiece(list.getDefaultPtr() / (1 - (getAccountGroupPriceList().getVariationPercentage() / 100)));
              }
            }
//            }
          }
        }
      }
    }
  }

  private List<ProductPriceListVo> mergeToPriceListGroup(List<ProductPriceListVo> productPriceListVoList) {
    productPriceListMap = new HashMap<>();
    List<ProductPriceListVo> priceLists = new ArrayList();
    List<ProductPriceListVo> tmpList = new ArrayList<>();
    ProductPriceListVo obj = null;
    int counter = 1;
    for (ProductPriceListVo productPriceListVo : productPriceListVoList) {
      String hash = productPriceListVo.getProductName().trim() + "#" + productPriceListVo.getBatchNo().trim() + "#" + productPriceListVo.getExpiryDateActual() + "#" + productPriceListVo.getValueMrp();
      if (productPriceListVo.getIsDefault() == 1) {
        hash += "#" + (productPriceListVo.getDefaultprice() == null ? 0.0 : productPriceListVo.getDefaultprice()) + "#" + (productPriceListVo.getDefaultPtr() == null ? 0.0 : productPriceListVo.getDefaultPtr());
      } else {
        hash += "#" + (productPriceListVo.getValuePtsPerProdPieceSell() == null ? productPriceListVo.getDefaultprice() : productPriceListVo.getValuePtsPerProdPieceSell()) + "#" + (productPriceListVo.getValuePtrPerProdPiece() == null ? productPriceListVo.getDefaultPtr() : productPriceListVo.getValuePtrPerProdPiece());
      }
      productPriceListVo.setHash(hash);
      if (obj != null) {
        if (!obj.getHash().equals(productPriceListVo.getHash())) {
          tmpList.add(obj);
          productPriceListMap.put(obj.getHash(), priceLists);
          priceLists = new ArrayList();
        }
        if (counter == productPriceListVoList.size()) {
          tmpList.add(productPriceListVo);
          priceLists.add(productPriceListVo);
          productPriceListMap.put(hash, priceLists);
          priceLists = new ArrayList();
        }
      }
      priceLists.add(productPriceListVo);
      obj = productPriceListVo;
      counter++;
    }
    return tmpList;
  }

  private List<ProductPriceListVo> reArrangeProductPriceList() {
    List<ProductPriceListVo> productPriceLists = new ArrayList<>();
    for (ProductPriceListVo productPriceListVo : productPriceListVoList) {
      if (productPriceListVo.getHash() != null && productPriceListVo.isDataModified()) {
        List<ProductPriceListVo> priceList = productPriceListMap.get(productPriceListVo.getHash());
        for (ProductPriceListVo p : priceList) {
          p.setDataModified(productPriceListVo.isDataModified());
          if (productPriceListVo.getIsDefault() == 1) { //Default price list
            p.setValuePtsPerProdPieceSell(productPriceListVo.getDefaultprice());
            p.setValuePtrPerProdPiece(productPriceListVo.getDefaultPtr());
          } else { //Custom price list
            p.setValuePtsPerProdPieceSell(productPriceListVo.getValuePtsPerProdPieceSell());
            p.setValuePtrPerProdPiece(productPriceListVo.getValuePtrPerProdPiece());
          }
          productPriceLists.add(p);
        }
      }
    }
    return productPriceLists;
  }

  public String[] getSelectedPriceTypes() {
    if (StringUtil.isEmpty(selectedPriceTypes)) {
      selectedPriceTypes = new String[]{"PTS", "PTR"};
    }
    return selectedPriceTypes;
  }

  public void setSelectedPriceTypes(String[] selectedPriceTypes) {
    if (selectedPriceTypes.length == 0) {
      this.selectedPriceTypes = getSelectedPriceTypes();
    } else {
      this.selectedPriceTypes = selectedPriceTypes;
    }
  }

//  public static void productPriceListCalculation(ProductPriceListVo productPriceListVo) {
//    ProductUtil.productPriceListCalculation(productPriceListVo);
//  }
//  private List<PriceListDetails> getPriceListDetailList(MainView main, ProductPriceListVo productPriceListVo) {
//    if (productPriceListVo != null) {
//      priceListDetailsList = ProductPricelistService.getPriceListDetailList(main, productPriceListVo);
//    }
//    return priceListDetailsList;
//  }
  public void loadPriceListDetailsList(MainView main, ProductPriceListVo productPriceListVo) {
    if (productPriceListVo.getPriceListDetailsList() == null) {
      productPriceListVo.setPriceListDetailsList(new ArrayList<>());

      List<ProductPriceListVo> priceList = productPriceListMap.get(productPriceListVo.getHash());
      if (priceList != null) {
        productPriceListVo.getPriceListDetailsList().clear();
        for (ProductPriceListVo pvo : priceList) {
          productPriceListVo.getPriceListDetailsList().add(ProductPricelistService.getPriceListDetails(main, pvo));
        }
      } else {
        productPriceListVo.getPriceListDetailsList().add(ProductPricelistService.getPriceListDetails(main, productPriceListVo));
      }
    }
    this.productPriceListVo = productPriceListVo;
  }

//  public ProductPriceListVo getProductPriceListVo() {
//    MainView main = Jsf.getMain();
//    if (productPriceListVo != null) {
//      productPriceListVo.setPriceListDetailsList(getPriceListDetailList(main, productPriceListVo));
//    }
//    return productPriceListVo;
//  }
  public ProductPriceListVo getProductPriceListVo() {
    return productPriceListVo;
  }

  public void setProductPriceListVo(ProductPriceListVo productPriceListVo) {
    this.productPriceListVo = productPriceListVo;
  }

  public boolean isGain(String value) {
    if (value != null && value.contains("LOSS")) {
      return false;
    }
    return true;
  }
}
