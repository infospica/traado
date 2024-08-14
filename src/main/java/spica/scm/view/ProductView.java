/*
 * @(#)ProductView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.sys.domain.Gallery;
import spica.addon.view.GalleryView;
import spica.scm.common.ProductInvoiceDetail;
import spica.scm.common.ProductProfile;
import spica.scm.common.ProductStockSummary;
import spica.scm.domain.Account;
import spica.scm.domain.Brand;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.Manufacture;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductClassification;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductImage;
import spica.scm.domain.ProductPacking;
import spica.scm.domain.ProductPackingDetail;
import spica.scm.domain.ProductPreset;
import spica.scm.domain.ProductPricelist;
import spica.scm.domain.ProductStatus;
import spica.scm.domain.ProductType;
import spica.scm.domain.ProductUnit;
import spica.scm.service.ManufactureProductService;
import spica.scm.service.ManufactureService;
import spica.scm.service.ProductBatchService;
import spica.scm.service.ProductClassificationDetailService;
import spica.scm.service.ProductPresetService;
import spica.scm.service.ProductPricelistService;
import spica.scm.service.ProductService;
import spica.scm.service.ProductUnitService;
import spica.scm.tax.GstIndia;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.CountryTaxRegimeUtil;
import spica.scm.util.ProductUtil;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * ProductView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "productView")
@ViewScoped
public class ProductView implements Serializable {

  private transient Product product;	//Domain object/selected Domain.
  private transient LazyDataModel<Product> productLazyModel; 	//For lazy loading datatable.
  private transient Product[] productSelected;	 //Selected Domain Array
  private transient int activeIndex = 0; //tab active index, reset to 0 when new records
  private transient ProductPreset productPreset = null;
  private transient List<ProductPricelist> productPricelistList;
  private transient List<ProductBatch> productBatchList;

  private final int TRADE_PROFILE_SS = 4;
  private final int TRADE_PROFILE_RETAILER = 7;

  private transient String commodityTaxes = "";
  private transient String productCategoryTaxes = "";

  private transient int addProdDetail = 0;
  private transient boolean productFilter;
  private transient ProductEntryDetail productEntryDetail = null;
  private transient String pePackInformation = "";
  private transient boolean productDetailExist;
  private transient Account account;
  private transient Integer invoiceProductDetailId;
  private transient double invoiceProductMrp;
  private transient Date invoiceProductExpiryDate;
  private transient String invoiceProductBatchNo;
  private transient Manufacture defaultManufacture;
  private transient List<Manufacture> manufactureList;
  private transient List<ProductClassification> productClassificationList;
  private transient ProductCategory prevProductCategory;
  private transient boolean productSold;
  private ProductImage productImage;
  private List<Gallery> galleries;
  private transient boolean uploadGallery;
  private Product productMaster;
  private List<ProductPreset> presetLists;
  private List<ProductStockSummary> productInventory;
  private transient int saleable;
  private transient int blocked;
  private transient int damaged;
  private transient int expired;
  private transient int excess;
  private transient Double saleableValue;
  private transient Double nonSaleableValue;
  private transient Double excessValue;
  @Inject
  private GalleryView galleryView;

  @PostConstruct
  public void init() {
    getProduct().reset();
    product = (Product) Jsf.popupParentValue(Product.class);
    if (product != null && product.getId() != null && product.getProductMaster()) {
      productMaster = product;
    }
  }

  /**
   * Default Constructor.
   */
  public ProductView() {
    super();
  }

  /**
   * Return Product.
   *
   * @return Product.
   */
  public Product getProduct() {
    if (product == null) {
      product = new Product();
    }
    return product;
  }

  /**
   * Set Product.
   *
   * @param product.
   */
  public void setProduct(Product product) {
    this.product = product;
  }

  public Account getAccount() {
    if (account == null && UserRuntimeView.instance().getAccount() != null) {
      account = UserRuntimeView.instance().getAccount();
    }
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public String getProductCategoryTaxes() {
    return productCategoryTaxes;
  }

  public void setProductCategoryTaxes(String productCategoryTaxes) {
    this.productCategoryTaxes = productCategoryTaxes;
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  public List<ProductBatch> getProductBatchList() {
    return productBatchList;
  }

  public void setProductBatchList(List<ProductBatch> productBatchList) {
    this.productBatchList = productBatchList;
  }

  public ProductCategory getPrevProductCategory() {
    return prevProductCategory;
  }

  public void setPrevProductCategory(ProductCategory prevProductCategory) {
    this.prevProductCategory = prevProductCategory;
  }

  /**
   * Return LazyDataModel of Product.
   *
   * @return
   */
  public LazyDataModel<Product> getProductLazyModel() {
    return productLazyModel;
  }

  private void selectProductBatchByProduct(MainView main) {
    productBatchList = ProductBatchService.selectProductBatchByProduct(main, getProduct(), UserRuntimeView.instance().getAccount());
  }

  /**
   * Return Product[].
   *
   * @return
   */
  public Product[] getProductSelected() {
    return productSelected;
  }

  /**
   * Set Product[].
   *
   * @param productSelected
   */
  public void setProductSelected(Product[] productSelected) {
    this.productSelected = productSelected;
  }

  public Integer getInvoiceProductDetailId() {
    return invoiceProductDetailId;
  }

  public void setInvoiceProductDetailId(Integer invoiceProductDetailId) {
    this.invoiceProductDetailId = invoiceProductDetailId;
  }

  public double getInvoiceProductMrp() {
    return invoiceProductMrp;
  }

  public void setInvoiceProductMrp(double invoiceProductMrp) {
    this.invoiceProductMrp = invoiceProductMrp;
  }

  public Date getInvoiceProductExpiryDate() {
    return invoiceProductExpiryDate;
  }

  public void setInvoiceProductExpiryDate(Date invoiceProductExpiryDate) {
    this.invoiceProductExpiryDate = invoiceProductExpiryDate;
  }

  public String getInvoiceProductBatchNo() {
    return invoiceProductBatchNo;
  }

  public void setInvoiceProductBatchNo(String invoiceProductBatchNo) {
    this.invoiceProductBatchNo = invoiceProductBatchNo;
  }

  public boolean getProductFilter() {
    return productFilter;
  }

  public void setProductFilter(boolean productFilter) {
    this.productFilter = productFilter;
  }

  public ProductPreset getProductPreset() {
    if (productPreset == null) {
      productPreset = new ProductPreset();
    }
    return productPreset;
  }

  public void setProductPreset(ProductPreset productPreset) {
    this.productPreset = productPreset;
  }

  public String getCommodityTaxes() {
    return commodityTaxes;
  }

  public void setCommodityTaxes(String commodityTaxes) {
    this.commodityTaxes = commodityTaxes;
  }

  public int getAddProdDetail() {
    return addProdDetail;
  }

  public void setAddProdDetail(int addProdDetail) {
    this.addProdDetail = addProdDetail;
  }

  public ProductEntryDetail getProductEntryDetail() {
    return productEntryDetail;
  }

  public void setProductEntryDetail(ProductEntryDetail productEntryDetail) {
    this.productEntryDetail = productEntryDetail;
  }

  public String getPePackInformation() {
    return pePackInformation;
  }

  public void setPePackInformation(String pePackInformation) {
    this.pePackInformation = pePackInformation;
  }
  private boolean prodDetailEditable;

  public boolean isProdDetailEditable() {
    return prodDetailEditable;
  }

  public void setProdDetailEditable(boolean prodDetailEditable) {
    this.prodDetailEditable = prodDetailEditable;
  }

  public boolean isProductDetailExist() {
    return productDetailExist;
  }

  public void setProductDetailExist(boolean productDetailExist) {
    this.productDetailExist = productDetailExist;
  }

  public String getProductEntryDetailPackingInfo(ProductEntryDetail productEntryDetail) {
    return ProductUtil.getProductPackingInfo(productEntryDetail);
  }

  public Manufacture getDefaultManufacture() {
    return defaultManufacture;
  }

  public void setDefaultManufacture(Manufacture defaultManufacture) {
    this.defaultManufacture = defaultManufacture;
  }

  public List<Manufacture> getManufactureList() {
    return manufactureList;
  }

  public void setManufactureList(List<Manufacture> manufactureList) {
    this.manufactureList = manufactureList;
  }

  public boolean isProductSold() {
    return productSold;
  }

  public void setProductSold(boolean productSold) {
    this.productSold = productSold;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProduct(MainView main, String viewType) {
    //this.main = main;

    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          resetProductView();
          String name = null;
          if (getProduct().getIsPopup() != null && getProduct().getIsPopup().intValue() == 1) {
            name = getProduct().getProductName();
          }
          getProduct().reset();
          if (name != null) {
            getProduct().setProductName(name);
          }
          getProductPreset().reset();
          getProduct().setExpirySalesDays(SystemConstants.DEFAULT_EXPIRY_SALES_DAYS);
          getProduct().setCompanyId(UserRuntimeView.instance().getCompany());
          if (UserRuntimeView.instance().getAccount() != null && UserRuntimeView.instance().getContract() != null) {
            if (UserRuntimeView.instance().getContract().getNearExpiryDays() != null) {
              getProduct().setExpirySalesDays(UserRuntimeView.instance().getContract().getNearExpiryDays());
            }
          }
          getProduct().setIsPopup(null);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError() && !isProductCategoryTaxCodeModified()) {
          setActiveIndex(0);
          setProduct((Product) ProductService.selectByPk(main, getProduct()));
          setPrevProductCategory(getProduct().getProductCategoryId());
          selectProductBatchByProduct(main);
          productClassificationList = ProductService.selectProductClassificationByProduct(main, getProduct());
          manufactureList = ProductService.selectManufactureByProduct(main, getProduct());
          /**
           * Checks product is used in purchase entry or not
           */
          //setProductDetailExist(!StringUtil.isEmpty(productDetailList));
          /**
           * Method used to set the product tax related information.
           */
          //Set Commodity Tax String
          setProductCommodityTaxDetails(getProduct().getCommodityId());
          // Set Product Category Tax Details
          setProductCategoryTaxDetails(getProduct().getProductCategoryId());

          if (UserRuntimeView.instance().getAccount() == null) {
            getProductPreset().reset();
            return null;
          }
          if (productMaster == null) {
            setProductPreset(ProductPresetService.selectProductPresetByProductAndAccount(main, getProduct().getId(), getAccount()));
          } else {
            setAccount(productMaster.getAcccount());
            presetLists = ProductPresetService.selectProductPresetByProduct(main, productMaster);
          }
          setProductSold(ProductService.isProductSold(main, product));
          showHideGallery(main);
          setProductInventory(null);

        } else if (ViewType.list.toString().equals(viewType)) {
          main.getPageData().setSearchKeyWord(null);
          resetProductView();
          loadProductList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void resetProductView() {
    setActiveIndex(0);
    setProductFilter(ProductService.FILTER_PRODUCTS_BY_BRAND_AND_VENDOR_COMMODITY);
    setAccount(null);
    setProductDetailExist(false);
    setProductBatchList(null);
    setManufactureList(null);
    setCommodityTaxes("");
    setProductCategoryTaxes("");
    setPrevProductCategory(null);
    setProductDetailExist(false);
    setManufactureList(null);
    setProductSold(false);
    productLazyModel = null;
  }

  /**
   * Create productLazyModel.
   *
   * @param main
   */
  private void loadProductList(final MainView main) {
    if (productLazyModel == null) {
      productLazyModel = new LazyDataModel<Product>() {
        private List<Product> list;

        @Override
        public List<Product> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductService.listPaged(main, UserRuntimeView.instance().getCompany(), getAccount(), getProductFilter());
            main.commit(productLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Product product) {
          return product.getId();
        }

        @Override
        public Product getRowData(String rowKey) {
          if (list != null) {
            for (Product obj : list) {
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
    String SUB_FOLDER = "scm_product/";
  }

  public void productFilterSelectEvent(MainView main) {
    main.getPageData().reset();
  }

  public String actionSaveProduct(MainView main) {
    if (main.isEdit() && isProductCategoryTaxCodeModified()) {
      Jsf.addCallbackParam("saved", 0);
      return null;
    } else {
      return saveProduct(main);
    }
  }

  public void saveProductClose(MainView main) {
    saveProduct(main);
    Jsf.execute("closePopup()");
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProduct(MainView main) {
    return saveOrCloneProduct(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProduct(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProduct(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProduct(MainView main, String key) {
    try {
      int ptsUpdated = 0;
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            getProduct().setProductName(getProduct().getProductName().replace('_', ' '));
            ProductService.insertOrUpdate(main, getProduct());
            ProductClassificationDetailService.insertOrUpdate(main, getProduct(), getProductClassificationList());
            ManufactureProductService.insertOrUpdate(main, getProduct(), getManufactureList());
            if (isProductCategoryTaxCodeModified()) {
              ptsUpdated = ProductService.updateProductPriceList(main, getProduct(), getPrevProductCategory());
              if (ptsUpdated != 0) {
                ProductService.updateTaxCodeModifiedFlag(main, getProduct(), getAccount());
              }
            }
            break;
          case "clone":
            ProductService.clone(main, getProduct());
            ManufactureProductService.insertOrUpdate(main, getProduct(), getManufactureList());
            break;
        }
        Jsf.addCallbackParam("saved", 1);
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
  public String saveProductPreset(MainView main) {
    try {
      getProductPreset().setAccountId(getAccount());
      getProductPreset().setProductId(getProduct());
      settingMarginPercentageValues();
      ProductPresetService.insertOrUpdate(main, getProductPreset());
      main.commit("success.save");
      main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   */
  private void settingMarginPercentageValues() {
//    if (getProductPreset().getVendorReservePercentage() == null && UserRuntimeView.instance().getContract() != null) {
//      getProductPreset().setVendorReservePercentage(UserRuntimeView.instance().getContract().getVendorReservePercent());
//    }
    if (getProductPreset().getPtrMarginPercentage() == null && UserRuntimeView.instance().getContract() != null) {
      getProductPreset().setPtrMarginPercentage(UserRuntimeView.instance().getContract().getPtrMarginPercent());
    }
    if (getProductPreset().getPtsMarginPercentage() == null && UserRuntimeView.instance().getContract() != null) {
      getProductPreset().setPtsMarginPercentage(UserRuntimeView.instance().getContract().getPtsMarginPercent());
    }
  }

  public static String toddMMyy(Date day) {
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
    String date = formatter.format(day);
    return date;
  }

  /**
   * Delete one or many Product.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProduct(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productSelected)) {
        ProductService.deleteByPkArray(main, getProductSelected()); //many record delete from list
        main.commit("success.delete");
        productSelected = null;
      } else {
        ProductService.deleteByPk(main, getProduct());  //individual record delete from list or edit form
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
   * @param productBatch
   * @return
   */
  public String deleteProductBatch(MainView main, ProductBatch productBatch) {
    try {
      ProductBatchService.deleteByPk(main, productBatch);
      getProductBatchList().remove(productBatch);
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * ProductCategory autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productCategoryAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productCategoryAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductCategory> productCategoryAuto(String filter) {
    List<ProductCategory> list = null;
    if (getProduct().getCommodityId() != null && getProduct().getCommodityId().getId() != null && getCompany() != null) {
      list = ScmLookupExtView.productCategoryAuto(getProduct().getCommodityId(), getCompany(), filter);
      if (getProduct().getId() == null && list != null && list.size() == 1 && getProduct().getProductCategoryId() == null) {
        getProduct().setProductCategoryId(list.get(0));
        updateProductCategoryDetails(getProduct().getProductCategoryId());
        productTypeAuto();
      }
    }
    return list;
  }

  /**
   * ProductType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductType> productTypeAuto() {
    List<ProductType> list = null;
    if (getProduct().getCommodityId() != null && getProduct().getCommodityId().getId() != null) {
      list = ScmLookupExtView.lookupProductTypeByCommodity(getProduct().getCommodityId());
      if (list != null && getProduct().getProductTypeId() == null) {
        for (ProductType type : list) {
          if (type.getProductTypeCode().equals(SystemConstants.PRODUCT_TYPE_GENERAL)) {
            getProduct().setProductTypeId(type);
          }
        }
      }
    }
    return list;
  }

  /**
   * ProductClassification autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productClassificationAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productClassificationAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductClassification> productClassificationAuto(String filter) {
    if (getProduct().getCommodityId() != null && getProduct().getCommodityId().getId() != null) {
      return ScmLookupExtView.productClassificationAuto(getProduct().getCompanyId().getId(), filter);
    }
    return null;
  }

  /**
   * ProductStatus autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productStatusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductStatus> productStatusAuto(String filter) {
    return ScmLookupView.productStatusAuto(filter);
  }

  /**
   *
   * @param productCategory
   */
  private void setProductCategoryTaxDetails(ProductCategory productCategory) {
    if (productCategory != null) {
      setProductCategoryTaxes(CountryTaxRegimeUtil.getProductCategoryTaxAsString(productCategory, getCompany().getCountryTaxRegimeId().getRegime()));
    }
  }

  /**
   *
   * @param commodity
   */
  private void setProductCommodityTaxDetails(ServiceCommodity commodity) {
    if (commodity != null) {
      if (getProduct().getCommodityId() != null) {
        setCommodityTaxes(CountryTaxRegimeUtil.getCommodityTaxAsString(getProduct().getCommodityId(), getCompany().getCountryTaxRegimeId().getRegime()));
      }
    }
  }

  /**
   * Product Category ajax select event handler.
   *
   * Method selects the ProductCategoryTax based on it company and product category. And sets the commodity tax details by converting ProductCategoryTax to String.
   *
   *
   * @param event
   */
  public void productCategorySelectEventHandler(SelectEvent event) {
    ProductCategory productCategory = (ProductCategory) event.getObject();
    getProduct().setHsnCode(null);
    updateProductCategoryDetails(productCategory);
  }

  /**
   *
   * @param productCategory
   */
  private void updateProductCategoryDetails(ProductCategory productCategory) {
    if (productCategory != null) {
      setProductCategoryTaxDetails(productCategory);
      if (StringUtil.isEmpty(getProduct().getHsnCode())) {
        if (!StringUtil.isEmpty(productCategory.getHsnCode())) {
          getProduct().setHsnCode(productCategory.getHsnCode());
        } else if (getProduct().getCommodityId() != null) {
          getProduct().setHsnCode(getProduct().getCommodityId().getHsnSacCode());
        }
      }
    }
  }

  /**
   * Product Commodity select event handler.
   *
   * This method is an ajax select event handler. After successful execution of this method. The Product Commodity Ajax component updates Product Category, Product Type and Product
   * Classification Components. And Also Creates the String format of Product's Commodity Tax.
   *
   * @param event
   */
  public void commoditySelectEventHandler(SelectEvent event) {
    ServiceCommodity commodity = (ServiceCommodity) event.getObject();
    getProduct().setHsnCode(null);
    updateProductCommodityDetails(commodity);
    productCategoryAuto(null);
  }

  /**
   *
   * @param commodity
   */
  private void updateProductCommodityDetails(ServiceCommodity commodity) {
    getProduct().setProductCategoryId(null);
    getProduct().setProductTypeId(null);
    getProduct().setProductClassificationId(null);
    setProductCategoryTaxes("");
    setCommodityTaxes("");
    if (commodity != null) {
      setProductCommodityTaxDetails(commodity);
    }
  }

  /**
   *
   * @return
   */
  public boolean isCommodityMedicine() {
    return getProduct().getCommodityId() != null && getProduct().getCommodityId().getId() == 1;

  }

  /**
   * ProductPacking autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productPackingAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productPackingAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductPacking> productPackingAuto(String filter) {
    return ScmLookupView.productPackingAuto(filter);
  }

//  public List<ProductUnit> productPackingUnit(MainView main) {
//    if (getProduct() != null && getProduct().getProductPackingDetailId() != null) {
//      return ProductPackingUnitService.selectProductUnitByProductPacking(main, getProduct().getProductPackingDetailId());
//    }
//    return null;
//  }
  public List<ProductUnit> productPackingUnit(MainView main) {
    try {
      return ProductUnitService.productPackingUnit(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<ProductPackingDetail> lookupProductPackingDetail() {
    if (getProduct() != null && getProduct().getProductUnitId() != null) {
      return ScmLookupExtView.lookupProductPackingDetail(getProduct().getProductUnitId());
    }
    return null;
  }

  public void itemSelectEvent1(SelectEvent event) {
    getProduct().setProductUnitId((ProductUnit) event.getObject());
  }

  /**
   *
   * @param productBatch
   * @return
   */
  public String getProductDetailPriceList(MainView main, ProductBatch productBatch) {
    productPricelistList = null;
    try {
      productPricelistList = ProductPricelistService.selectProductPriceListByProductBatch(main, productBatch);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<ProductPricelist> getProductPricelistList() {
    return productPricelistList;
  }

  public void setProductPricelistList(List<ProductPricelist> productPricelistList) {
    this.productPricelistList = productPricelistList;
  }

  public void onRowEdit(RowEditEvent event) {
    MainView main = Jsf.getMain();
    try {
      ProductPricelist priceList = (ProductPricelist) event.getObject();
      ProductPricelistService.updatePriceList(main, priceList);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public boolean isTradeTypeSS() {
    if (UserRuntimeView.instance().getAccount() == null) {
      return false;
    }
    try {
      if (getAccount() != null && (getAccount().getCompanyTradeProfileId().getId() == TRADE_PROFILE_SS || getAccount().getCompanyTradeProfileId().getId() == TRADE_PROFILE_RETAILER)) {
        return true;
      }
    } catch (Throwable t) {
      Jsf.fatal(t, null);
    }
    return false;
  }

  public boolean marginPercentValue() {
    return (UserRuntimeView.instance().getContract() != null && UserRuntimeView.instance().getContract().getMarginPercentage() != null);

  }

  public boolean vendorReservePercentageValue() {
    return (UserRuntimeView.instance().getContract() != null && UserRuntimeView.instance().getContract().getVendorReservePercent() != null);

  }

  public boolean ptrMarginPercentageValue() {
    return (UserRuntimeView.instance().getContract() != null && UserRuntimeView.instance().getContract().getPtrMarginPercent() != null);

  }

  public boolean ptsMarginPercentageValue() {
    return (UserRuntimeView.instance().getContract() != null && UserRuntimeView.instance().getContract().getPtsMarginPercent() != null);
  }

  public boolean isProductEntry() {
    return false;

  }

  public boolean isProdIdExist() {
    return false;
  }

  public boolean isProdDetailIdExist() {
    return false;
  }

  public boolean isProductCategoryTaxCodeModified() {
    if (getPrevProductCategory() != null && getProduct().getProductCategoryId() != null) {
      return (!getPrevProductCategory().getPurchaseTaxCodeId().getId().equals(getProduct().getProductCategoryId().getPurchaseTaxCodeId().getId()));
    }
    return false;
  }

  public void ProductEntryProductDialogClose() {
    Jsf.returnDialog(null);
  }

  public void productCommodityOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.COMMODITY, getProduct(), getProduct().getCommodityId() == null ? null : getProduct().getCommodityId().getId());
    }
  }

  public void productCatOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      //Jsf.popupForm(FileConstant.productCat, getProduct()); // always opens a new form
      Jsf.popupForm(FileConstant.PRODUCT_CATEGORY, getProduct(), getProduct().getProductCategoryId() == null ? null : getProduct().getProductCategoryId().getId()); // opens a new form if id is null else edit
    }
  }

  public void productTypeOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      //Jsf.popupForm(FileConstant.productType, getProduct()); // always opens a new form
      Jsf.popupForm(FileConstant.PRODUCT_TYPE, getProduct(), getProduct().getProductTypeId() == null ? null : getProduct().getProductTypeId().getId()); // opens a new form if id is null else edit
    }
  }

  public void productPackingOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.PRODUCT_PACKING_DETAIL, getProduct());
    }
  }

  public void productTypeReturned() {
    // ProductType pt = (ProductType) Jsf.popupReturnValue(product, ProductType.class);
    //  getProduct().setProductTypeId(pt);
  }

  public void productPackingReturned() {

  }

  public void packInformation() {

  }

  public void addNewProductDetail() {
    setAddProdDetail(1);
    setProdDetailEditable(false);
  }

  public boolean addPrdDtl() {
    return (getAddProdDetail() != 0);
  }

  public boolean productEntryDetailIdExist() {
    return false;//(!StringUtil.isEmpty(getProductEntryDetailId()) && !getProductEntryDetailId().equals("null"));
  }

  public void itemDateSelectEvent() {
    // Date d = (Date) event.getObject();
//    if (getProductBatch().getExpiryDateActual() != null) {
//      Calendar cal = Calendar.getInstance();
//      cal.setTime(getProductDetail().getExpiryDateActual());
//      if (getProductPreset().getExpirySalesDays() != null) {
//        cal.add(Calendar.DATE, -getProductPreset().getExpirySalesDays());
//      }
//      Date dateBefore = (Date) cal.getTime();
//      getProductDetail().setExpiryDateSales(dateBefore);
//      Jsf.update("expiryDateSales");
//    }
  }

  public void blurEvent() {
  }

  public boolean isRenderProdDetailAdd() {
    return false;//(getProductEntryDetailId().equals("null") && !getProductDetailId().equals("null"));
  }

  public String getProductExpiryDate() {
    String productExpiryDate = "";
    if (getInvoiceProductExpiryDate() != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("MMM-YY");
      productExpiryDate = sdf.format(getInvoiceProductExpiryDate());
    }
    return productExpiryDate;
  }

  public List<ServiceCommodity> selectVendorCommodity() {
    List<ServiceCommodity> vendorCommodityList = null;
    if (getAccount() != null) {
      vendorCommodityList = ScmLookupExtView.selectCommodityByVendor(getAccount().getVendorId());
      if (getProduct().getId() == null && vendorCommodityList != null && vendorCommodityList.size() == 1) {
        getProduct().setCommodityId(vendorCommodityList.get(0));
        updateProductCommodityDetails(getProduct().getCommodityId());
        productCategoryAuto(null);
      }
    }
    return vendorCommodityList;
  }

  public String getPackDimensionTitle(Integer packDimension) {
    return ProductUtil.getPackDimensionTitle(packDimension);
  }

  /**
   * Method to get the list of company brand.
   *
   * @return
   */
  public List<Brand> lookupBrandByCompany() {
    return ScmLookupExtView.lookupBrandByCompany(getCompany());
  }

  public void productUnitItemSelectEvent(SelectEvent event) {
    getProduct().setProductPackingDetailId(null);
  }

  public void actionNewProductBatch() {
    Jsf.popupForm(FileConstant.PRODUCT_BATCH, getProduct()); // opens a new form if id is null else edit    
  }

  public void actionEditProductBatch(Integer id) {
    Jsf.popupForm(FileConstant.PRODUCT_BATCH, getProduct(), id);
  }

  public void productBatchPopupReturned(MainView main) {
    setProductBatchList(null);
    try {
      selectProductBatchByProduct(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public boolean isProductBatchExist() {
    return (!StringUtil.isEmpty(getProductBatchList()) && getProductBatchList().size() > 0);
  }

  public List<Manufacture> manufactureByCompanyAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      if (getManufactureList() != null && !getManufactureList().isEmpty()) {
        if (defaultManufacture == null) {
          defaultManufacture = ManufactureService.selectByPk(main, getManufactureList().get(0));
        }
      }
      return ScmLookupExtView.manufactureByCompanyAuto(filter, getCompany());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void onManufactureSelect(SelectEvent event) {
    Set<Manufacture> tempSet = new HashSet<>();
    List<Manufacture> tempList;
    if (getManufactureList().size() > 1) {
      for (Manufacture manuf : getManufactureList()) {
        tempSet.add(manuf);
      }
      tempList = new ArrayList<>(tempSet);
      setManufactureList(tempList);
    }
  }

  /**
   *
   * @param event
   */
  public void onManufactureUnSelect(UnselectEvent event) {
    if (getManufactureList().size() == 1) {
      getManufactureList().clear();
      setDefaultManufacture(null);
    }
  }

  public void onClassificationSelect(SelectEvent event) {
    Set<ProductClassification> tempSet = new HashSet<>();
    List<ProductClassification> tempList;
    if (getProductClassificationList().size() > 1) {
      for (ProductClassification pc : getProductClassificationList()) {
        tempSet.add(pc);
      }
      tempList = new ArrayList<>(tempSet);
      setProductClassificationList(tempList);
    }
  }

  /**
   *
   * @param event
   */
  public void onClassificationUnselect(UnselectEvent event) {
    if (getProductClassificationList().size() == 1) {
      getProductClassificationList().clear();
    }
  }

  public void productManufactureOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.MANUFACTURER, new Manufacture()); // opens a new form if id is null else edit
    }
  }

  public void productValidation(FacesContext context, UIComponent component, Object value) {
    List<Manufacture> list = (List<Manufacture>) value;
    String validateParam = (String) Jsf.getParameter("validateManufactures");
    if (!StringUtil.isEmpty(validateParam)) {
      if (list != null && list.size() <= 1) {
        Jsf.error(component, "error.product.exist");
      } else if (list != null && list.size() > 1) {
        MainView main = Jsf.getMain();
        try {
          long exist = ProductService.isProductExist(main, getProduct().getId(), list);
          if (exist > 0) {
            Jsf.error(component, "error.product.exist");
          }
        } catch (Throwable t) {
          main.rollback(t, "error.select");
        } finally {
          main.close();
        }
      }
    }
  }

  public void handleFileUpload(FileUploadEvent event) {
    MainView main = Jsf.getMain();
    productImage = new ProductImage();
    try (InputStream input = event.getFile().getInputstream()) {
//     getGalleryView().uploadImage(main, input, event.getFile().getFileName(), event.getFile().getSize(), 1, 1);
////      imageResize(input,event.getFile().getFileName());
//      int s = 0;
//      float percent = 0.1f;
//      long size = event.getFile().getSize();
//      long fileSize = 50 * 1000; //in KB
//      while (true) {
//        if (size > fileSize) {
//          percent = size / fileSize;
//          if (percent >= 1) {
//            s = Math.round(percent);
//            size = s * 1000;
//          }
//        } else {
//          break;
//        }
//      }
//      percent = Float.valueOf("0." + s);
//      byte[] imageBytes = imageResize(input, event.getFile().getFileName(), percent); //new byte[(int) event.getFile().getSize()];
//      input.read(imageBytes, 0, imageBytes.length);
//
//      getProductImage().setImage(Base64.encodeBase64String(imageBytes));
//      getProductImage().setImageName(event.getFile().getFileName());
//      getProductImage().setCompanyId(UserRuntimeView.instance().getCompany());
//      getProductImage().setProductId(getProduct());
//      if (main.isEdit()) {
//        ProductImageService.saveOrUpdate(main, getProductImage());
//        imageList = null;
//        main.commit("success.save");
//      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public List getProductImages(MainView main) {
    try {
      //  galleries = GalleryService.loadGalleryByEntity(main, getProduct().getId(), 1, UserRuntimeView.instance().getCompany().getId()); //ProductImageService.selectProductImagesByProduct(main, getProduct(), UserRuntimeView.instance().getCompany().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.load");
    } finally {
      main.close();
    }
    return galleries;
  }

  public ProductImage getProductImage() {
    return productImage;
  }

  public void setProductImage(ProductImage productImage) {
    this.productImage = productImage;
  }

  public void setGalleryView(GalleryView galleryView) {
    this.galleryView = galleryView;
  }

  public GalleryView getGalleryView() {
    return galleryView;
  }

  public void manageGallery() {
    TaxCalculator taxCalculator = new GstIndia();
    Jsf.popupForm(taxCalculator.getManageGallery(), getProduct());
  }

  private void showHideGallery(MainView main) {
    CompanySettings settings = UserRuntimeView.instance().getCompany().getCompanySettings();
    if (settings.getProductGallery() != null && settings.getProductGallery() == SystemConstants.SHOW_UPLOAD_PRODUCT_GALLERY) {
      uploadGallery = true;
    } else {
      uploadGallery = false;
    }
  }

  public boolean isUploadGallery() {
    return uploadGallery;
  }

  public List<ProductProfile> getProductProfiles(MainView main, Integer productId) {
    List<ProductProfile> profileList = null;
    try {
      profileList = ProductService.selectProductProfile(main, productId);
      int i = 1;
      for (ProductProfile profile : profileList) {
        profile.setId(i);
        i++;
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return profileList;
  }

  public List<ProductProfile> getProductProfiles(MainView main) {
    try {
      if (product != null) {
        return getProductProfiles(main, product.getId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public boolean presetIsDeletable(MainView main) {
    try {
      if (productPreset != null && productPreset.getId() != null) {
        return ProductPresetService.presetIsDeletable(main, productPreset);
      } else {
        return true;
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return false;
  }

  public void deletePreset(MainView main) {
    try {
      ProductPresetService.deleteByPk(main, getProductPreset());
      main.commit("success.delete");
      setProductPreset(ProductPresetService.selectProductPresetByProductAndAccount(main, getProduct().getId(), getAccount()));
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public List<ProductPreset> getPresetLists() {
    return presetLists;
  }

  public String saveProductPresetList(MainView main) {
    try {
//      getProductPreset().setAccountId(getAccount());
//      getProductPreset().setProductId(getProduct());
//      settingMarginPercentageValues();
      for (ProductPreset preset : presetLists) {
        ProductPresetService.insertOrUpdate(main, preset);
      }

      main.commit("success.save");
      main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public List<ProductStockSummary> getProductInventory(MainView main) {
    try {
      if (productInventory == null && product != null) {
        productInventory = ProductService.getProductInventory(main, product);
      }
      saleable = 0;
      blocked = 0;
      damaged = 0;
      expired = 0;
      excess = 0;
      saleableValue = 0.0;
      nonSaleableValue = 0.0;
      excessValue = 0.0;
      if (!StringUtil.isEmpty(productInventory)) {
        for (ProductStockSummary list : productInventory) {
          saleable += list.getSaleable() != null ? list.getSaleable() : 0;
          blocked += list.getBlocked() != null ? list.getBlocked() : 0;
          damaged += list.getDamaged() != null ? list.getDamaged() : 0;
          expired += list.getExpired() != null ? list.getExpired() : 0;
          excess += list.getExcess() != null ? list.getExcess() : 0;
          saleableValue += list.getSaleableValue() != null ? list.getSaleableValue() : 0;
          nonSaleableValue += list.getNonSaleableValue() != null ? list.getNonSaleableValue() : 0;
          excessValue += list.getExcessValue() != null ? list.getExcessValue() : 0;
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return productInventory;
  }

  public void setProductInventory(List<ProductStockSummary> productInventory) {
    this.productInventory = productInventory;
  }

  public List<ProductInvoiceDetail> getLastPurchaseList(MainView main) {
    try {
      if (product != null) {
        return ProductService.getLastPurchases(main, product);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<ProductInvoiceDetail> getLastSalesList(MainView main) {
    try {
      if (product != null) {
        return ProductService.getLastSales(main, product);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public boolean isBatchDeletable(MainView main, ProductBatch batch) {
    try {
      return ProductService.isBatchDeletable(main, batch);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return false;
  }

  public int getSaleable() {
    return saleable;
  }

  public void setSaleable(int saleable) {
    this.saleable = saleable;
  }

  public int getBlocked() {
    return blocked;
  }

  public void setBlocked(int blocked) {
    this.blocked = blocked;
  }

  public int getDamaged() {
    return damaged;
  }

  public void setDamaged(int damaged) {
    this.damaged = damaged;
  }

  public int getExpired() {
    return expired;
  }

  public void setExpired(int expired) {
    this.expired = expired;
  }

  public int getExcess() {
    return excess;
  }

  public void setExcess(int excess) {
    this.excess = excess;
  }

  public Double getSaleableValue() {
    return saleableValue;
  }

  public void setSaleableValue(Double saleableValue) {
    this.saleableValue = saleableValue;
  }

  public Double getNonSaleableValue() {
    return nonSaleableValue;
  }

  public void setNonSaleableValue(Double nonSaleableValue) {
    this.nonSaleableValue = nonSaleableValue;
  }

  public Double getExcessValue() {
    return excessValue;
  }

  public void setExcessValue(Double excessValue) {
    this.excessValue = excessValue;
  }

  public List<ProductClassification> getProductClassificationList() {
    return productClassificationList;
  }

  public void setProductClassificationList(List<ProductClassification> productClassificationList) {
    this.productClassificationList = productClassificationList;
  }

  public String actionSaveProductName(MainView main) {
    try {
      if (main.isEdit() && isProductCategoryTaxCodeModified()) {
        Jsf.addCallbackParam("saved", 0);       
      } else {
        ProductService.updateProductName(main, product);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

}
