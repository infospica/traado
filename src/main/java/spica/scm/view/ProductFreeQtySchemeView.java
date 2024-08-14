/*
 * @(#)ProductFreeQtySchemeView.java	1.0 Fri Jan 13 15:50:50 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.ProdFreeQtySchemeRange;
import spica.scm.domain.ProductDetail;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ProductFreeQtyScheme;
import spica.scm.domain.ProductPacking;
import spica.scm.service.ProdFreeQtySchemeRangeService;
import spica.scm.service.ProductFreeQtySchemeService;
import spica.scm.util.FromRangeComparator;
import wawo.app.faces.Jsf;

/**
 * ProductFreeQtySchemeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jan 13 15:50:50 IST 2017
 */
@Named(value = "productFreeQtySchemeView")
@ViewScoped
public class ProductFreeQtySchemeView implements Serializable {

  private transient ProductFreeQtyScheme productFreeQtyScheme;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductFreeQtyScheme> productFreeQtySchemeLazyModel; 	//For lazy loading datatable.
  // private transient ProductFreeQtyScheme productFreeQtySchemeSelected;	 //Selected Domain Array
  private List<ProdFreeQtySchemeRange> prodFreeQtySchemeRangeList;
  private String productName;
  private transient ProdFreeQtySchemeRange prodFreeQtySchemeRange;	//Domain object/selected Domain.
  private String freeQuantity;
  private String productDetailId;
  private String productFreeSchemeId;
  private String productId;
  private String accountId;
  private String productEntryDetailId;
  private String schemeUnit = " (Quantity)";
  private transient String oldScheme;

  /**
   * Default Constructor.
   */
  public ProductFreeQtySchemeView() {
    super();
  }

  /**
   * Return ProductFreeQtyScheme.
   *
   * @return ProductFreeQtyScheme.
   */
  public ProductFreeQtyScheme getProductFreeQtyScheme() {
    if (productFreeQtyScheme == null) {
      productFreeQtyScheme = new ProductFreeQtyScheme();
    }
    return productFreeQtyScheme;
  }

  /**
   * Set ProductFreeQtyScheme.
   *
   * @param productFreeQtyScheme.
   */
  public void setProductFreeQtyScheme(ProductFreeQtyScheme productFreeQtyScheme) {
    this.productFreeQtyScheme = productFreeQtyScheme;
  }

  public String getProductDetailId() {
    return productDetailId;
  }

  public void setProductDetailId(String productDetailId) {
    this.productDetailId = productDetailId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getFreeQuantity() {
    return freeQuantity;
  }

  public void setFreeQuantity(String freeQuantity) {
    this.freeQuantity = freeQuantity;
  }

  public String getProductFreeSchemeId() {
    return productFreeSchemeId;
  }

  public void setProductFreeSchemeId(String productFreeSchemeId) {
    this.productFreeSchemeId = productFreeSchemeId;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public List<ProdFreeQtySchemeRange> getProdFreeQtySchemeRangeList() {
    if (prodFreeQtySchemeRangeList == null) {
      prodFreeQtySchemeRangeList = new ArrayList<>();
      ProdFreeQtySchemeRange pp = new ProdFreeQtySchemeRange();
      prodFreeQtySchemeRangeList.add(pp);
    }
    return prodFreeQtySchemeRangeList;
  }

  public void setProdFreeQtySchemeRangeList(List<ProdFreeQtySchemeRange> prodFreeQtySchemeRangeList) {
    this.prodFreeQtySchemeRangeList = prodFreeQtySchemeRangeList;
  }

  public String getSchemeUnit() {
    if (StringUtil.isEmpty(schemeUnit)) {
      schemeUnit = " (Quantity)";
    }
    return schemeUnit;
  }

  public void setSchemeUnit(String schemeUnit) {
    this.schemeUnit = schemeUnit;
  }

  /**
   * Return PrdEntFrQtySchmRang.
   *
   * @return PrdEntFrQtySchmRang.
   */
  public ProdFreeQtySchemeRange getProdFreeQtySchemeRange() {
    if (prodFreeQtySchemeRange == null) {
      prodFreeQtySchemeRange = new ProdFreeQtySchemeRange();
    }
    return prodFreeQtySchemeRange;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getProductEntryDetailId() {
    return productEntryDetailId;
  }

  public void setProductEntryDetailId(String productEntryDetailId) {
    this.productEntryDetailId = productEntryDetailId;
  }

  /**
   * Set PrdEntFrQtySchmRang.
   *
   * @param prodFreeQtySchemeRange.
   */
  public void setProdFreeQtySchemeRange(ProdFreeQtySchemeRange prodFreeQtySchemeRange) {
    this.prodFreeQtySchemeRange = prodFreeQtySchemeRange;
  }

  /**
   *
   * @param event
   */
  public void updateSchemeType(SelectEvent event) {
    ProductPacking pp = (ProductPacking) event.getObject();
    if (pp != null) {
      setSchemeUnit(" (" + pp.getTitle() + ")");
    } else {
      setSchemeUnit(" (Quantity)");
    }
  }

  /**
   *
   * @param productFreeQtyScheme
   * @return
   */
  private String getSchemeUnitTitle(ProductFreeQtyScheme productFreeQtyScheme) {
    String title = " (Quantity)";
    if (productFreeQtyScheme != null) {
      title = " (" + productFreeQtyScheme.getFreeQuantitySchemeUnitId().getTitle() + ")";
    }
    return title;
  }

  /**
   * Removing range from range list.
   *
   * @param main
   * @param prodFreeQtySchemeRange
   * @return
   */
  public String deleteSchemeByRange(MainView main, ProdFreeQtySchemeRange prodFreeQtySchemeRange) {
    try {
      if (prodFreeQtySchemeRange != null) {
        getProdFreeQtySchemeRangeList().remove(prodFreeQtySchemeRange);
        if (getProdFreeQtySchemeRangeList().isEmpty()) {
          ProdFreeQtySchemeRange pp = new ProdFreeQtySchemeRange();
          getProdFreeQtySchemeRangeList().add(pp);
        } else {
          Collections.sort(getProdFreeQtySchemeRangeList(), new FromRangeComparator());
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @return
   */
  public boolean isSingleRangeList() {
    return getProdFreeQtySchemeRangeList() != null && getProdFreeQtySchemeRangeList().size() == 1;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductFreeQtyScheme(MainView main, String viewType) {
    // FacesContext fcontext = FacesContext.getCurrentInstance();
    main.getPageData().setPageSize(4);
    loadProductFreeQtySchemeList(main);
    if ((ViewTypes.newform.equals(viewType) || ViewTypes.list.equals(viewType)) && !main.hasError()) {
      prodFreeQtySchemeRangeList = null;
      getProductFreeQtyScheme().reset();
      getProductFreeQtyScheme().setIsFreeQtyToCustomer(0);
      getProductFreeQtyScheme().setReplacement(0);
      getProductFreeQtyScheme().setFreeQuantitySchemeByRange(0);
    }
    if (!main.hasError()) {
      try {
        if (ViewTypes.isEdit(viewType) && !main.hasError()) {

          if (!StringUtil.isEmpty(getProductFreeSchemeId())) {
            getProductFreeQtyScheme().setId(Integer.valueOf(getProductFreeSchemeId()));
          }

          setProductFreeQtyScheme(ProductFreeQtySchemeService.selectByPk(main, getProductFreeQtyScheme()));
          setProdFreeQtySchemeRangeList(ProdFreeQtySchemeRangeService.selectProdFreeQtySchemeRange(main, getProductFreeQtyScheme().getId()));
          oldScheme = getProductFreeQtyScheme().getSchemeCode() != null ? getProductFreeQtyScheme().getSchemeCode() : "";

//          ProductFreeQtyScheme prFreeSheme = new ProductFreeQtyScheme();
//          if (!getProductFreeSchemeId().equals("") && getProductFreeQtySchemeSelected() == null) {
//            prFreeSheme.setId(Integer.valueOf(getProductFreeSchemeId()));
//            setProductFreeQtyScheme(ProductFreeQtySchemeService.selectByPk(main, prFreeSheme));
//            setProductFreeQtySchemeSelected(getProductFreeQtyScheme());
//            setProdFreeQtySchemeRangeList(ProdFreeQtySchemeRangeService.selectProdFreeQtySchemeRange(main, prFreeSheme.getId()));
//          } else {
//            if (getProductFreeQtySchemeSelected() != null) {
//              setProductFreeQtyScheme(getProductFreeQtySchemeSelected());
//            }
//            if (getProductFreeQtyScheme() != null && getProductFreeQtyScheme().getId() != null) {
//              setProdFreeQtySchemeRangeList(ProdFreeQtySchemeRangeService.selectProdFreeQtySchemeRange(main, getProductFreeQtyScheme().getId()));
//            }
//          }
          if (isPurchasBenefitToCustomer()) {
            setSchemeUnit(getSchemeUnitTitle(getProductFreeQtyScheme()));
          }
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
   * Create productFreeQtySchemeLazyModel.
   *
   * @param main
   */
  private void loadProductFreeQtySchemeList(final MainView main) {
    if (productFreeQtySchemeLazyModel == null) {
      productFreeQtySchemeLazyModel = new LazyDataModel<ProductFreeQtyScheme>() {
        private List<ProductFreeQtyScheme> list;

        @Override
        public List<ProductFreeQtyScheme> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductFreeQtySchemeService.listPagedScheme(main, Integer.valueOf(productDetailId));
            main.commit(productFreeQtySchemeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductFreeQtyScheme productFreeQtyScheme) {
          return productFreeQtyScheme.getId();
        }

        @Override
        public ProductFreeQtyScheme getRowData(String rowKey) {
          if (list != null) {
            for (ProductFreeQtyScheme obj : list) {
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
    String SUB_FOLDER = "scm_product_free_qty_scheme/";
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductFreeQtyScheme(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductFreeQtyScheme(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductFreeQtyScheme(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductFreeQtySchemeService.insertOrUpdate(main, getProductFreeQtyScheme());
            break;
          case "clone":
            ProductFreeQtySchemeService.clone(main, getProductFreeQtyScheme());
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

  public String saveProductFreeQtyScheme(MainView main) {
    String schCode = "";
    try {
      String oldScheme = "";
      if (getProductFreeQtyScheme().getFreeQuantitySchemeByRange() != null && getProductFreeQtyScheme().getFreeQuantitySchemeByRange() == 1) {
        Collections.sort(getProdFreeQtySchemeRangeList(), new FromRangeComparator());
        if (validateSchemeRange(getProdFreeQtySchemeRangeList())) {
          main.error("error.free.quantity.range");
          return null;
        }
      }
      //oldScheme = getProductFreeQtyScheme().getSchemeCode() != null ? getProductFreeQtyScheme().getSchemeCode() : "";
      if (getProductFreeQtyScheme().getIsFreeQtyToCustomer() == 0) {
        getProductFreeQtyScheme().setFreeQuantitySchemeUnitId(null);
        getProductFreeQtyScheme().setFreeQtySchemeUnitQtyFor(null);
        getProductFreeQtyScheme().setFreeQtySchemeUnitQtyFree(null);
        getProductFreeQtyScheme().setFreeQuantitySchemeByRange(null);
        prodFreeQtySchemeRangeList = null;
      }
      if (getProductFreeQtyScheme().getFreeQuantitySchemeByRange() == null || getProductFreeQtyScheme().getFreeQuantitySchemeByRange() == 1) {
        getProductFreeQtyScheme().setFreeQtySchemeUnitQtyFor(null);
        getProductFreeQtyScheme().setFreeQtySchemeUnitQtyFree(null);
      }
      if (!StringUtil.isEmpty(getProductFreeSchemeId())) {
        if (getProductFreeQtyScheme().getFreeQuantitySchemeByRange() == null || getProductFreeQtyScheme().getFreeQuantitySchemeByRange() == 0) {
          ProdFreeQtySchemeRangeService.deleteProdFreeQtySchemeRange(main, Integer.valueOf(getProductFreeSchemeId()));
        }
      }
      ProductDetail pd = new ProductDetail();
      pd.setId(Integer.valueOf(getProductDetailId()));
      getProductFreeQtyScheme().setProductDetailId(pd);

      if (getProductFreeQtyScheme().getFreeQuantitySchemeByRange() != null && getProductFreeQtyScheme().getFreeQuantitySchemeByRange() == 0) {
        schCode = getProductDetailId().concat("|").concat(getProductFreeQtyScheme().getFreeQuantitySchemeByRange().toString()).concat("|").concat(getProductFreeQtyScheme().getFreeQuantitySchemeUnitId().getId().toString()).concat("|").concat(getProductFreeQtyScheme().getFreeQtySchemeUnitQtyFor().toString()).concat("-").concat(getProductFreeQtyScheme().getFreeQtySchemeUnitQtyFree().toString());
      } else if (getProductFreeQtyScheme().getFreeQuantitySchemeByRange() != null && getProductFreeQtyScheme().getFreeQuantitySchemeByRange() == 1) {
        schCode = getProductDetailId().concat("|").concat(getProductFreeQtyScheme().getFreeQuantitySchemeByRange().toString()).concat("|").concat(getProductFreeQtyScheme().getFreeQuantitySchemeUnitId().getId().toString());
        if (prodFreeQtySchemeRangeList != null) {
          for (ProdFreeQtySchemeRange range : prodFreeQtySchemeRangeList) {

            schCode = schCode.concat("|").concat(range.getRangeFrom().toString()).concat("-").concat(range.getRangeTo() == null ? "" : range.getRangeTo().toString()).concat("-").concat(range.getFreeQty().toString());
          }
        }
      } else {
        schCode = "Benefit".concat(getProductFreeQtyScheme().getReplacement().toString());
      }
      getProductFreeQtyScheme().setSchemeCode(schCode);
      if (!getProductFreeQtyScheme().getSchemeCode().equals(oldScheme)) {
        ProductFreeQtySchemeService.insertOrUpdate(main, productFreeQtyScheme);
      }

      ProdFreeQtySchemeRangeService.insertArray(main, prodFreeQtySchemeRangeList, productFreeQtyScheme);
      main.commit("success.save");
      main.setViewType(ViewTypes.editform);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public String newProductFreeQtyScheme(MainView main) {
    getProductFreeQtyScheme().reset();
    main.setViewType(ViewTypes.newform);
    return null;
  }

  /**
   *
   * @param main
   * @param productFreeQtyScheme
   * @return
   */
  public String deleteFreeQtyScheme(MainView main, ProductFreeQtyScheme productFreeQtyScheme) {
    try {
      ProductFreeQtySchemeService.deleteFreeQtyScheme(main, productFreeQtyScheme);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Return LazyDataModel of ProductFreeQtyScheme.
   *
   * @return
   */
  public LazyDataModel<ProductFreeQtyScheme> getProductFreeQtySchemeLazyModel() {
    return productFreeQtySchemeLazyModel;
  }

  /**
   * Return ProductFreeQtyScheme[].
   *
   * @return
   */
//  public ProductFreeQtyScheme getProductFreeQtySchemeSelected() {
//    return productFreeQtySchemeSelected;
//  }
  /**
   * Set ProductFreeQtyScheme[].
   *
   * @param productFreeQtySchemeSelected
   */
//  public void setProductFreeQtySchemeSelected(ProductFreeQtyScheme productFreeQtySchemeSelected) {
//    this.productFreeQtySchemeSelected = productFreeQtySchemeSelected;
//  }
  public void ajaxBehaviorEventHandler(AjaxBehaviorEvent event) {
    if (getProductFreeQtyScheme().getIsFreeQtyToCustomer() == null || getProductFreeQtyScheme().getIsFreeQtyToCustomer() == 1) {
      getProductFreeQtyScheme().setFreeQuantitySchemeByRange(0);
    } else {
      getProductFreeQtyScheme().setFreeQuantitySchemeByRange(0);
      getProductFreeQtyScheme().setReplacement(0);
    }
  }

  public boolean isPurchasBenefitToCustomer() {
    return getProductFreeQtyScheme().getIsFreeQtyToCustomer() != null && getProductFreeQtyScheme().getIsFreeQtyToCustomer() == 1;
  }

  public boolean renderRangeDiv() {
    return getProductFreeQtyScheme().getFreeQuantitySchemeByRange() != null && getProductFreeQtyScheme().getFreeQuantitySchemeByRange() == 1;
  }

  public void ajaxBehaviorEventHandlerScheme(AjaxBehaviorEvent event) {
  }

  /**
   *
   * @param ProdFreeQtySchemeRange
   * @return
   */
  private boolean validateSchemeRange(List<ProdFreeQtySchemeRange> prodFreeQtySchemeRangeList) {
    ProdFreeQtySchemeRange preRange = null;
    int size = 0;
    int counter = 0;
    if (prodFreeQtySchemeRangeList != null && prodFreeQtySchemeRangeList.size() == 1) {
      if (prodFreeQtySchemeRangeList.get(0).getRangeFrom() >= prodFreeQtySchemeRangeList.get(0).getRangeTo()) {
        return true;
      }
    } else if (prodFreeQtySchemeRangeList != null && prodFreeQtySchemeRangeList.size() > 1) {
      size = prodFreeQtySchemeRangeList.size();
      for (ProdFreeQtySchemeRange range : prodFreeQtySchemeRangeList) {
        counter++;
        if (range.getRangeTo() != null && range.getRangeTo() <= range.getRangeFrom()) {
          return true;
        }
        if ((range.getRangeTo() != null && preRange != null) && range.getRangeFrom() <= preRange.getRangeTo()) {
          return true;
        }
        if (size != counter) {
          if (range.getRangeTo() == null) {
            return true;
          }
        }
        preRange = range;
      }
    }
    return false;
  }

  /**
   *
   * @param main
   * @return
   */
  public String addNewRange(MainView main) {
    if (getProdFreeQtySchemeRangeList() != null) {
      Collections.sort(getProdFreeQtySchemeRangeList(), new FromRangeComparator());
      if (validateSchemeRange(getProdFreeQtySchemeRangeList())) {
        main.error("error.free.quantity.range");
        return null;
      }

      ProdFreeQtySchemeRange psr = new ProdFreeQtySchemeRange();
      if (getProdFreeQtySchemeRangeList().isEmpty()) {
        psr.setRangeFrom(0);
        getProdFreeQtySchemeRangeList().add(0, psr);
      } else {
        getProdFreeQtySchemeRangeList().add(0, psr);
      }
    }
    return null;
  }

  public void insertProductFreeQtyScheme() {
    try {
      Jsf.addCookie(oldScheme, productId, 0);
      if (getProductFreeQtyScheme() != null && getProductFreeQtyScheme().getId() != null) {
        setProductFreeSchemeId(getProductFreeQtyScheme().getId().toString());
        Jsf.addCallbackParam("schemeId", getProductFreeSchemeId());
        //Jsf.info("success.select");        
      } else {
        Jsf.addCallbackParam("schemeId", "");
      }
    } catch (Throwable t) {
      Jsf.error(t, "error.list");
    }
  }

  public void onRowSelect(SelectEvent event) {
    setProductFreeQtyScheme((ProductFreeQtyScheme) event.getObject());
  }

  public void schemePopupClose() {
    Jsf.popupClose(productFreeQtyScheme);
  }

  public List<ProductPacking> lookUpProductPackingUnits(MainView main) {
    return null;//ScmLookupExtView.lookUpProductPackingUnits(getProductEntryDetailId());
  }
}
