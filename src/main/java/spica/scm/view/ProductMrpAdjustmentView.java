/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductMrpAdjustment;
import spica.scm.domain.ProductMrpAdjustmentItem;
import spica.scm.service.AccountService;
import spica.scm.service.ProductBatchService;
import spica.scm.service.ProductMrpAdjustmentItemService;
import spica.scm.service.ProductMrpAdjustmentService;
import spica.scm.service.ProductService;
import spica.scm.util.CountryTaxRegimeUtil;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Anoop Jayachandran
 */
@Named(value = "productMrpAdjustmentView")
@ViewScoped
public class ProductMrpAdjustmentView implements Serializable {

  private transient ProductMrpAdjustment productMrpAdjustment;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductMrpAdjustment> productMrpAdjustmentLazyModel; 	//For lazy loading datatable.
  private transient ProductMrpAdjustment[] productMrpAdjustmentSelected;	 //Selected Domain Array
  private transient List<ProductMrpAdjustmentItem> productMrpAdjustmentItemList;
  private transient ServiceCommodity commodityId;
  private transient Product prevProduct;
  private transient ProductBatch prevProductBatch;
  private transient boolean productModified;
  private transient String productTaxDetail;

  /**
   * Default Constructor.
   */
  public ProductMrpAdjustmentView() {
    super();
  }

  /**
   * Return ProductMrpAdjustment.
   *
   * @return ProductMrpAdjustment.
   */
  public ProductMrpAdjustment getProductMrpAdjustment() {
    if (productMrpAdjustment == null) {
      productMrpAdjustment = new ProductMrpAdjustment();
    }
    return productMrpAdjustment;
  }

  /**
   * Set ProductMrpAdjustment.
   *
   * @param productMrpAdjustment.
   */
  public void setProductMrpAdjustment(ProductMrpAdjustment productMrpAdjustment) {
    this.productMrpAdjustment = productMrpAdjustment;
  }

  public List<ProductMrpAdjustmentItem> getProductMrpAdjustmentItemList() {
    return productMrpAdjustmentItemList;
  }

  public void setProductMrpAdjustmentItemList(List<ProductMrpAdjustmentItem> productMrpAdjustmentItemList) {
    this.productMrpAdjustmentItemList = productMrpAdjustmentItemList;
  }

//  public Account getAccountId() {
//    return UserRuntimeView.instance().getAccount();
//  }
  public Company getCompanyId() {
    return UserRuntimeView.instance().getCompany();
  }

  public boolean isProductModified() {
    return productModified;
  }

  public void setProductModified(boolean productModified) {
    this.productModified = productModified;
  }

  public Product getPrevProduct() {
    return prevProduct;
  }

  public void setPrevProduct(Product prevProduct) {
    this.prevProduct = prevProduct;
  }

  public ProductBatch getPrevProductBatch() {
    return prevProductBatch;
  }

  public void setPrevProductBatch(ProductBatch prevProductBatch) {
    this.prevProductBatch = prevProductBatch;
  }

  public boolean isDraft() {
    return (getProductMrpAdjustment() != null && SystemConstants.DRAFT.equals(getProductMrpAdjustment().getStatusId()));
  }

  public boolean isConfirmed() {
    return (getProductMrpAdjustment() != null && SystemConstants.CONFIRMED.equals(getProductMrpAdjustment().getStatusId()));
  }

  public String getProductTaxDetail() {
    return productTaxDetail;
  }

  public void setProductTaxDetail(String productTaxDetail) {
    this.productTaxDetail = productTaxDetail;
  }

  public ServiceCommodity getCommodityId() {
    return commodityId;
  }

  public void setCommodityId(ServiceCommodity commodityId) {
    this.commodityId = commodityId;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductMrpAdjustment(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType) && !main.hasError()) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getProductMrpAdjustment().reset();
          getProductMrpAdjustment().setCompanyId(getCompanyId());

        } else if (ViewType.editform.toString().equals(viewType)) {
          setProductMrpAdjustment((ProductMrpAdjustment) ProductMrpAdjustmentService.selectByPk(main, getProductMrpAdjustment()));

          setProductMrpAdjustmentItemList(ProductMrpAdjustmentItemService.listProductMrpAdjustmentItems(main, getProductMrpAdjustment()));

          setPrevProduct(getProductMrpAdjustment().getProductId());
        } else if (ViewType.list.toString().equals(viewType)) {
          resetProductMrpAdjustmentView();
          loadProductMrpAdjustmentList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void resetProductMrpAdjustmentView() {
    setProductModified(false);
    setPrevProduct(null);
    setProductMrpAdjustmentItemList(null);
    setProductTaxDetail(null);
    setCommodityId(null);
  }

  public void productSelectEvent(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      Product prod = (Product) event.getObject();
      getProductMrpAdjustment().setProductBatchId(null);
      getProductMrpAdjustment().setPreviousValueMrp(null);
      getProductMrpAdjustment().setValueMrp(null);
      setProductMrpAdjustmentItemList(null);
      setProductTaxDetail(null);
      setProductModified(main.isEdit() && prod != null && getPrevProduct() != null && !prod.getId().equals(getPrevProduct().getId()));
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void productBatchSelectEvent(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      ProductBatch batch = (ProductBatch) event.getObject();
      if (!isProductModified()) {
        setProductModified(main.isEdit() && batch != null && getPrevProductBatch() != null && !batch.getId().equals(getPrevProductBatch().getId()));
      }
      setProductTaxDetail(null);
      if (batch != null) {
        setProductTaxDetail(CountryTaxRegimeUtil.getProductCategoryTaxAsString(batch.getProductId().getProductCategoryId(), batch.getProductId().getCompanyId().getCountryTaxRegimeId().getRegime()));
      }
      getProductMrpAdjustment().setPreviousValueMrp(batch.getValueMrp());
      getProductMrpAdjustment().setValueMrp(null);
      setProductMrpAdjustmentItemList(null);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void commoditySelectEventHandler(SelectEvent event) {
    getProductMrpAdjustment().setProductId(null);
    getProductMrpAdjustment().setProductBatchId(null);
    getProductMrpAdjustment().setPreviousValueMrp(null);
    getProductMrpAdjustment().setValueMrp(null);
    setProductMrpAdjustmentItemList(null);
    setProductTaxDetail(null);
  }

  public void updateChangeEvent(ProductMrpAdjustmentItem productMrpAdjustmentItem) {
    getActualSellingPriceDerived(productMrpAdjustmentItem);
    ProductMrpAdjustmentItemService.calculateCreditDebitValue(productMrpAdjustmentItem);
  }

  private static void getActualSellingPriceDerived(ProductMrpAdjustmentItem productMrpAdjustmentItem) {
    Double valuePts = productMrpAdjustmentItem.getValuePts() != null ? productMrpAdjustmentItem.getValuePts() : 0.0;
    if (valuePts != null) {
      double actualSellingPrice = valuePts;
      Double schemeDiscount = productMrpAdjustmentItem.getSchemeDiscountPercentage() != null ? productMrpAdjustmentItem.getSchemeDiscountPercentage() : 0;
      if (schemeDiscount != null && schemeDiscount > 0) {
        if (productMrpAdjustmentItem.getIsSchemeDiscountToCustomer() != null && productMrpAdjustmentItem.getIsSchemeDiscountToCustomer().equals(SystemConstants.DISCOUNT_FOR_CUSTOMER)) {
          actualSellingPrice -= actualSellingPrice * productMrpAdjustmentItem.getSchemeDiscountPercentage() / 100;
        }
      }
      Double productDiscount = productMrpAdjustmentItem.getProductDiscountPercentage() != null ? productMrpAdjustmentItem.getProductDiscountPercentage() : 0.0;
      if (productDiscount != null && productDiscount > 0) {
        if (productMrpAdjustmentItem.getIsProductDiscountToCustomer() != null && productMrpAdjustmentItem.getIsProductDiscountToCustomer().equals(SystemConstants.DISCOUNT_FOR_CUSTOMER)) {
          actualSellingPrice -= actualSellingPrice * productMrpAdjustmentItem.getProductDiscountPercentage() / 100;
        }
      }
      productMrpAdjustmentItem.setActualSellingPriceDerived(actualSellingPrice);
    }
    productMrpAdjustmentItem.setExpectedLandingRate(ProductUtil.getExpectedLandingRate(productMrpAdjustmentItem.getProductEntryDetailId().getPtsSsRateDerivationCriterion(),
            productMrpAdjustmentItem.getActualSellingPriceDerived(), productMrpAdjustmentItem.getProductEntryDetailId().getMarginPercentage(), productMrpAdjustmentItem.getAccountId()));
  }

  public void loadProductBatchDetails(MainView main) {
    try {
      if (getProductMrpAdjustment() != null && getProductMrpAdjustment().getProductBatchId() != null && getProductMrpAdjustment().getValueMrp() != null) {
        if (getProductMrpAdjustment() != null && getProductMrpAdjustment().getId() != null) {
          ProductMrpAdjustmentItemService.deleteProductMrpAdjustmentItemByProductMrpAdjustment(main, getProductMrpAdjustment());
        }
        setProductMrpAdjustmentItemList(ProductMrpAdjustmentItemService.selectProductBatchStockDetails(main, getProductMrpAdjustment().getProductBatchId(), getCompanyId()));
        ProductMrpAdjustmentItemService.processProductMrpChange(getProductMrpAdjustment(), getProductMrpAdjustmentItemList());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   * Create productMrpAdjustmentLazyModel.
   *
   * @param main
   */
  private void loadProductMrpAdjustmentList(final MainView main) {
    if (productMrpAdjustmentLazyModel == null) {
      productMrpAdjustmentLazyModel = new LazyDataModel<ProductMrpAdjustment>() {
        private List<ProductMrpAdjustment> list;

        @Override
        public List<ProductMrpAdjustment> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductMrpAdjustmentService.listProductMrpAdjustmentByAccount(main, getCompanyId());
            main.commit(productMrpAdjustmentLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductMrpAdjustment productMrpAdjustment) {
          return productMrpAdjustment.getId();
        }

        @Override
        public ProductMrpAdjustment getRowData(String rowKey) {
          if (list != null) {
            for (ProductMrpAdjustment obj : list) {
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
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductMrpAdjustment(MainView main, int status) {
    if (getProductMrpAdjustment().getPreviousValueMrp() == getProductMrpAdjustment().getValueMrp()) {
      main.error("error.value.same.mrp");
      return null;
    }
    getProductMrpAdjustment().setStatusId(status);
    return saveOrCloneProductMrpAdjustment(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductMrpAdjustment(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductMrpAdjustment(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductMrpAdjustment(MainView main, String key) {
    try {
      if (null != key) {
        switch (key) {
          case "save":
            ProductMrpAdjustmentService.insertOrUpdate(main, getProductMrpAdjustment(), getProductMrpAdjustmentItemList(), isProductModified());
            break;
          case "clone":
            ProductMrpAdjustmentService.clone(main, getProductMrpAdjustment());
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
   * Delete one or many ProductMrpAdjustment.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductMrpAdjustment(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productMrpAdjustmentSelected)) {
        ProductMrpAdjustmentService.deleteByPkArray(main, getProductMrpAdjustmentSelected()); //many record delete from list
        main.commit("success.delete");
        productMrpAdjustmentSelected = null;
      } else {
        ProductMrpAdjustmentService.deleteByPk(main, getProductMrpAdjustment());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductMrpAdjustment.
   *
   * @return
   */
  public LazyDataModel<ProductMrpAdjustment> getProductMrpAdjustmentLazyModel() {
    return productMrpAdjustmentLazyModel;
  }

  /**
   * Return ProductMrpAdjustment[].
   *
   * @return
   */
  public ProductMrpAdjustment[] getProductMrpAdjustmentSelected() {
    return productMrpAdjustmentSelected;
  }

  /**
   * Set ProductMrpAdjustment[].
   *
   * @param productMrpAdjustmentSelected
   */
  public void setProductMrpAdjustmentSelected(ProductMrpAdjustment[] productMrpAdjustmentSelected) {
    this.productMrpAdjustmentSelected = productMrpAdjustmentSelected;
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
    MainView main = Jsf.getMain();
    try {
      return ProductService.selectProductByAccount(main, getCommodityId(), getCompanyId(), filter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<ProductBatch> productBatchAuto(String filter) {
    if (getProductMrpAdjustment() != null && getProductMrpAdjustment().getProductId() != null) {
      MainView main = Jsf.getMain();
      try {
        return ProductBatchService.selectProductBatchByProduct(main, getProductMrpAdjustment().getProductId(), filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   *
   * @return
   */
  public List<ServiceCommodity> lookupCommodity() {
    return ScmLookupExtView.lookupCommodityByCountryAndCompany(getCompanyId());
  }

}
