/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.SelectEvent;
import spica.scm.common.ProductPackingUnits;
import spica.scm.common.ProductSummary;
import spica.scm.domain.Account;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.Contract;
import spica.scm.domain.Manufacture;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductClassification;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductPacking;
import spica.scm.domain.ProductPackingDetail;
import spica.scm.domain.ProductPreset;
import spica.scm.domain.ProductStatus;
import spica.scm.domain.ProductType;
import spica.scm.domain.ProductUnit;
import spica.scm.domain.SalesReturnItem;
import spica.scm.service.AccountService;
import spica.scm.service.ContractService;
import spica.scm.service.ProductBatchService;
import spica.scm.service.ProductDetailService;
import spica.scm.service.ProductPresetService;
import spica.scm.service.ProductService;
import spica.scm.service.ProductUnitService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.CountryTaxRegimeUtil;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-2
 */
@Named(value = "productSalesReturnView")
@ViewScoped
public class ProductSalesReturnView implements Serializable {

  private transient Product product;	//Domain object
  private transient ProductDetail productDetail;	//Domain object
  private transient ProductPreset productPreset;	//Domain object
  private transient ProductPackingDetail productPackingDetail;
  private transient Account account;	//Domain object
  private transient Contract contract;	//Domain object    
  private transient Product[] productSelected;	 //Selected Domain Array
  private transient SalesReturnItem salesReturnItem;

  private transient String commodityTaxes = "";
  private transient String productCategoryTaxes = "";

  private transient String primaryPackTitle;
  private transient String secondaryPackTitle;
  private transient String tertiaryPackTitle;
  private transient List<ProductPacking> selectedPackingList = null;

  private transient boolean productEntryExist;
  private transient ProductBatch productBatch;
  private transient boolean productBatchTraded;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    productEntryExist = false;
    setSalesReturnItem((SalesReturnItem) Jsf.popupParentValue(SalesReturnItem.class));
    if (getSalesReturnItem() != null) {

      MainView main = Jsf.getMain();
      try {
        setProduct(ProductService.selectByPk(main, getSalesReturnItem().getProduct()));
        if (getSalesReturnItem().getProductBatchId() != null) {
          setProductBatch(ProductBatchService.selectByPk(main, getSalesReturnItem().getProductBatchId()));
          productPackingDetail = getProductBatch().getProductPackingDetailId();

        } else {
          setProductBatch(new ProductBatch(getProduct().getProductPackingDetailId()));
          setProductPreset(salesReturnItem.getProductPreset());
          setAccount(getProductPreset().getAccountId());
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }

    }
  }

  /**
   * Creates a new instance of ProductSalesReturnView
   */
  public ProductSalesReturnView() {
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

  public ProductBatch getProductBatch() {
    return productBatch;
  }

  public boolean isProductBatchTraded() {
    return productBatchTraded;
  }

  public void setProductBatchTraded(boolean productBatchTraded) {
    this.productBatchTraded = productBatchTraded;
  }

  public void setProductBatch(ProductBatch productBatch) {
    this.productBatch = productBatch;
  }

  public ProductPackingDetail getProductPackingDetail() {
    return productPackingDetail;
  }

  public void setProductPackingDetail(ProductPackingDetail productPackingDetail) {
    this.productPackingDetail = productPackingDetail;
  }

  /**
   *
   * @return
   */
  public ProductDetail getProductDetail() {
    if (productDetail == null) {
      productDetail = new ProductDetail();
    }
    return productDetail;
  }

  /**
   *
   * @param productDetail
   */
  public void setProductDetail(ProductDetail productDetail) {
    this.productDetail = productDetail;
  }

  /**
   *
   * @return
   */
  public ProductPreset getProductPreset() {
    if (productPreset == null) {
      productPreset = new ProductPreset();
    }
    return productPreset;
  }

  /**
   *
   * @param productPreset
   */
  public void setProductPreset(ProductPreset productPreset) {
    this.productPreset = productPreset;
  }

  public SalesReturnItem getSalesReturnItem() {
    return salesReturnItem;
  }

  public void setSalesReturnItem(SalesReturnItem salesReturnItem) {
    this.salesReturnItem = salesReturnItem;
  }

  /**
   *
   * @return
   */
  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }

  /**
   *
   * @return
   */
  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Contract getContract() {
    return contract;
  }

  public void setContract(Contract contract) {
    this.contract = contract;
  }

  /**
   *
   * @return
   */
  public String getCommodityTaxes() {
    return commodityTaxes;
  }

  /**
   *
   * @param commodityTaxes
   */
  public void setCommodityTaxes(String commodityTaxes) {
    this.commodityTaxes = commodityTaxes;
  }

  /**
   *
   * @return
   */
  public String getProductCategoryTaxes() {
    return productCategoryTaxes;
  }

  /**
   *
   * @param productCategoryTaxes
   */
  public void setProductCategoryTaxes(String productCategoryTaxes) {
    this.productCategoryTaxes = productCategoryTaxes;
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

  /**
   *
   * @return
   */
  public String getPrimaryPackTitle() {
    return primaryPackTitle;
  }

  /**
   *
   * @param primaryPackTitle
   */
  public void setPrimaryPackTitle(String primaryPackTitle) {
    this.primaryPackTitle = primaryPackTitle;
  }

  /**
   *
   * @return
   */
  public String getSecondaryPackTitle() {
    return secondaryPackTitle;
  }

  /**
   *
   * @param secondaryPackTitle
   */
  public void setSecondaryPackTitle(String secondaryPackTitle) {
    this.secondaryPackTitle = secondaryPackTitle;
  }

  /**
   *
   * @return
   */
  public String getTertiaryPackTitle() {
    return tertiaryPackTitle;
  }

  /**
   *
   * @param tertiaryPackTitle
   */
  public void setTertiaryPackTitle(String tertiaryPackTitle) {
    this.tertiaryPackTitle = tertiaryPackTitle;
  }

  /**
   *
   * @return
   */
  public List<ProductPacking> getSelectedPackingList() {
    if (selectedPackingList == null) {
      selectedPackingList = new ArrayList<>();
    }
    return selectedPackingList;
  }

  /**
   *
   * @param selectedPackingList
   */
  public void setSelectedPackingList(List<ProductPacking> selectedPackingList) {
    this.selectedPackingList = selectedPackingList;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProduct(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getProduct().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setProduct((Product) ProductService.selectByPk(main, getProduct()));
          setContract(ContractService.selectContractByAccount(main, getAccount().getId()));
          //setProductPreset(ProductPresetService.selectProductPresetByProductAndAccount(main, getProduct().getId(), getAccount()));
          if (getProductPackingDetail() != null) {
            setProductPresetPackingTitle(getProductPackingDetail());
            //setSelectedProductPackingList(getProductPackingDetail());

            //getProductPreset().setPrimaryDimensionContentQty(getProduct().getPackSize());
            //getProductPreset().setPrimaryDimensionContentQtyUnit(getProduct().getProductUnitId().getSymbol());
          }

          //Set Commodity Tax String
          setProductCommodityTaxDetails(getProduct().getCommodityId());
          // Set Product Category Tax Details
          setProductCategoryTaxDetails(getProduct().getProductCategoryId());
        } else if (main.isList()) {
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void uploadFiles() {
    String SUB_FOLDER = "scm_product/";
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
    main.setViewType(ViewTypes.newform);
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
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductService.insertOrUpdate(main, getProduct());
            break;
          case "clone":
            ProductService.clone(main, getProduct());
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
   * Delete one or many Product.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProduct(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(productSelected)) {
        ProductService.deleteByPkArray(main, getProductSelected()); //many record delete from list
        main.commit("success.delete");
        productSelected = null;
      } else {
        ProductService.deleteByPk(main, getProduct());  //individual record delete from list or edit form
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
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductDetail(MainView main) {
    return saveOrCloneProductDetail(main, "save");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductDetail(MainView main, String key) {

    try {
      uploadFiles(); //File upload      
      if (null != key) {
        switch (key) {
          case "save":
            getProductBatch().setIsSaleable(SystemConstants.BATCH_SALEABLE);
            getProductBatch().setProductId(getProduct());

            if (getProductBatch().getExpiryDateActual() != null) {
              Calendar cal = Calendar.getInstance();
              cal.setTime(getProductBatch().getExpiryDateActual());
              int year = cal.get(Calendar.YEAR);
              if (year < 1000) {
                cal.add(Calendar.YEAR, 2000);
                getProductBatch().setExpiryDateActual(cal.getTime());
              }
            }

            if (getProduct().getExpirySalesDays() == null) {
              getProductBatch().setExpiryDateSales(getProductBatch().getExpiryDateActual());
            } else {
              Calendar cal = Calendar.getInstance();
              cal.setTime(getProductBatch().getExpiryDateActual());
              if (getProduct().getExpirySalesDays() != null) {
                cal.add(Calendar.DATE, -getProduct().getExpirySalesDays());
              }
              getProductBatch().setExpiryDateSales(cal.getTime());
            }
            getProductBatch().setBatchNo(getProductBatch().getBatchNo().toUpperCase());

            ProductBatchService.insertOrUpdate(main, getProductBatch());
            getSalesReturnItem().setProduct(getProductBatch().getProductId());
            getSalesReturnItem().setProductBatchId(getProductBatch());
            getSalesReturnItem().setAccountId(getAccount());
            if (getProductBatch() != null) {
              getSalesReturnItem().setProductBatchSummary(new ProductSummary(getProductBatch().getProductId().getId(), getProductBatch().getId(),
                      getProductBatch().getProductId().getProductName(), getProductBatch().getBatchNo()));
            }
            updateSalesReturnItem(main, getSalesReturnItem());
            Jsf.addCallbackParam("saved", true);
            productPopupClose();
            break;
          case "clone":
            ProductDetailService.clone(main, getProductDetail());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      Jsf.addCallbackParam("saved", false);
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
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
  public List<ProductCategory> lookupProductCategoryByCompany(String filter) {
    List<ProductCategory> list = null;
    if (getProduct().getCommodityId() != null && getProduct().getCommodityId().getId() != null && getCompany() != null) {
      list = ScmLookupExtView.productCategoryAuto(getProduct().getCommodityId(), getCompany(), filter);

      if (list != null && list.size() == 1 && getProduct().getProductCategoryId() == null) {
        getProduct().setProductCategoryId(list.get(0));
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
  public List<ProductType> lookupProductType(String filter) {
    List<ProductType> list = null;
    if (getProduct().getCommodityId() != null && getProduct().getCommodityId().getId() != null) {
      list = ScmLookupExtView.productTypeAuto(getProduct().getCommodityId().getId(), filter);
      if (list != null && list.size() == 1 && getProduct().getProductTypeId() == null) {
        getProduct().setProductTypeId(list.get(0));
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
      return ScmLookupExtView.productClassificationAuto(getProduct().getCommodityId().getId(), filter);
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
   * Commodity autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.commodityAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.commodityAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   *
   * @return
   */
  public List<ServiceCommodity> selectVendorCommodity() {
    List<ServiceCommodity> companyCommodityList = ScmLookupExtView.selectCommodityByVendor(getAccount().getVendorId());
    return companyCommodityList;
  }

  /**
   * ProductUnit autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productUnitAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productUnitAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductUnit> productUnitAuto(String filter) {
    return ScmLookupView.productUnitAuto(filter);
  }

  public List<ProductPacking> getPrimaryProductPackingList() {
    return ScmLookupExtView.lookupProductPacking(SystemConstants.PRODUCT_PACKING_PRIMARY);
  }

  public List<ProductPackingDetail> lookupProductPackingDetail() {
    if (product != null && product.getProductUnitId() != null) {
      return ScmLookupExtView.lookupProductPackingDetail(product.getProductUnitId());
    }
    return null;
  }

  public List<ProductPacking> productPackingAuto() {
    List<ProductPacking> list = null;
    if (getProductBatch() != null && getProductBatch().getProductPackingDetailId() != null) {
      list = new ArrayList<>();
      if (getProductBatch().getProductPackingDetailId().getPackPrimary() != null) {
        list.add(getProductBatch().getProductPackingDetailId().getPackPrimary());
      }

      if (getProductBatch().getProductPackingDetailId().getPackSecondary() != null) {
        list.add(getProductBatch().getProductPackingDetailId().getPackSecondary());
      }

      if (getProductBatch().getProductPackingDetailId().getPackTertiary() != null) {
        list.add(getProductBatch().getProductPackingDetailId().getPackTertiary());
      }

      if (!StringUtil.isEmpty(list) && list.size() == 1) {
        getProductBatch().setDefaultProductPackingId(list.get(0));
      }
    }
    return list;
  }

  public void productPackingSelectEvent(SelectEvent event) {

  }

  /**
   *
   * @param main
   * @return
   */
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

  public List<ProductPacking> getSecondaryProductPackingList() {
    return ScmLookupExtView.lookupProductPacking(SystemConstants.PRODUCT_PACKING_SECONDARY);
  }

  /**
   *
   * @param main
   * @return
   */
  public List<ProductPacking> getTertiaryProductPackingList() {
    return ScmLookupExtView.lookupProductPacking(SystemConstants.PRODUCT_PACKING_TERTIARY);
  }

  /**
   *
   * @return
   */
  public boolean isDimensionTwo() {
    return (getProductPackingDetail() != null && getProductPackingDetail().getPackPrimaryDimension() != null && getProductPackingDetail().getPackPrimaryDimension().equals(SystemConstants.TWO_DIMENSION));
  }

  /**
   *
   * @return
   */
  public boolean isDimensionThree() {
    return (getProductPackingDetail() != null && getProductPackingDetail().getPackPrimaryDimension() != null && getProductPackingDetail().getPackPrimaryDimension().equals(SystemConstants.THREE_DIMENSION));
  }

  /**
   *
   * @return
   */
  public boolean isDimensionOne() {
    return (getProductPackingDetail() != null && getProductPackingDetail().getPackPrimaryDimension() != null && getProductPackingDetail().getPackPrimaryDimension().equals(SystemConstants.ONE_DIMENSION));
  }

  public boolean hasTertiaryPacking() {
    return (getProductPackingDetail() != null && getProductPackingDetail().getPackTertiary() != null && getProductPackingDetail().getPackTertiary().getId() != null);
  }

  public boolean hasSecondaryPacking() {
    return (getProductPackingDetail() != null && getProductPackingDetail().getPackSecondary() != null && getProductPackingDetail().getPackSecondary().getId() != null);
  }

  /**
   *
   * @return
   */
  public boolean isStripPrimaryPacking() {
    return getProductPackingDetail() != null && getProductPackingDetail().getPackPrimary() != null && getProductPackingDetail().getPackPrimary().getId().equals(SystemConstants.PRODUCT_PACKING_STRIP);
  }

  public boolean isProductEntryExist() {
    return productEntryExist;
  }

  public void setProductEntryExist(boolean productEntryExist) {
    this.productEntryExist = productEntryExist;
  }

  /**
   * Method to generate the product packing details.
   *
   * @param productPackingUnits
   * @return
   */
  public String getProductPackingInfo(ProductPackingUnits productPackingUnits) {
    return ProductUtil.getProductPackingInfo(productPackingUnits);
  }

  /**
   *
   * @param productPreset
   */
  private void setProductPresetPackingTitle(ProductPackingDetail productPreset) {
    if (productPreset != null) {
      if (productPreset.getPackPrimary() != null) {
        setPrimaryPackTitle(productPreset.getPackPrimary().getTitle());
      }
      if (productPreset.getPackSecondary() != null) {
        setSecondaryPackTitle(productPreset.getPackSecondary().getTitle());
      }
      if (productPreset.getPackTertiary() != null) {
        setTertiaryPackTitle(productPreset.getPackTertiary().getTitle());
      }
    }
  }

  /**
   *
   * @param packDimension
   * @return
   */
  public String getPackDimensionTitle(Integer packDimension) {
    return ProductUtil.getPackDimensionTitle(packDimension);
  }

  public void addNewProductDetail() {
    getProductBatch().reset();
    setProductEntryExist(false);
    setProductBatchTraded(false);
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void productPopupClose() {
    Jsf.popupReturn(getSalesReturnItem(), getSalesReturnItem());
  }

  public List<Manufacture> lookupManufacturer() {
    List<Manufacture> list = ScmLookupExtView.manufacturerByProductAuto(getProduct());
    if (!StringUtil.isEmpty(list) && getProductBatch().getManufactureId() == null) {
      getProductBatch().setManufactureId(list.get(0));
    }
    return list;
  }

  private void updateSalesReturnItem(MainView main, SalesReturnItem salesReturnItem) {
    salesReturnItem.resetSalesReturnItem();
    TaxCalculator taxCalculator = SystemRuntimeConfig.getTaxCalculator(salesReturnItem.getSalesReturnId().getTaxProcessorId().getProcessorClass());
    taxCalculator.updateSalesReturnItem(main, salesReturnItem);
    salesReturnItem.setProductHashCode(salesReturnItem.getProductBatchSummary().getProductCode());
    //getProductHashCode().put(salesReturnItem.getProductSummary().getProductHash(), salesReturnItem.getProductSummary().getProductId());
    //salesReturnItem.after();
    salesReturnItem.setSalesInvoiceItemIds(salesReturnItem.getProductBatchSummary().getSalesInvoiceItemIds());
    ProductPreset productPreset = ProductPresetService.selectProductPresetByProductAndAccount(main, salesReturnItem.getProduct().getId(), salesReturnItem.getAccountId());
    if (salesReturnItem.getSalesInvoiceId() == null) {
      calculateLandingRate(salesReturnItem, productPreset);
    }
    if (salesReturnItem.getSalesInvoiceId() != null) {
      salesReturnItem.setRefInvoiceNo(salesReturnItem.getSalesInvoiceId().getInvoiceNo());
      salesReturnItem.setRefInvoiceDate(salesReturnItem.getSalesInvoiceId().getInvoiceEntryDate());
    }
    if (salesReturnItem.getProductDetailList() != null) {
      salesReturnItem.getProductDetailList().add(salesReturnItem.getProductBatchSummary());
    }
    //removeProductHashKeys();
  }

  private void calculateLandingRate(SalesReturnItem salesReturnItem, ProductPreset productPreset) {
    Double ptsValue = salesReturnItem.getValuePts();
    Account account = salesReturnItem.getAccountId();
    int decimalPrecision = account.getSupplierDecimalPrecision() == null ? 2 : account.getSupplierDecimalPrecision();
    if (account.getPtsDerivationCriteria().equals(AccountService.DERIVE_PTS_FROM_MRP_PTR)) {
      salesReturnItem.setExpectedLandingRate(MathUtil.roundOff(ProductUtil.getExpectedLandingRate(productPreset.getPtsSsRateDerivationCriterion(), ptsValue, productPreset.getMarginPercentage(), salesReturnItem.getAccountId()), decimalPrecision));
    }
  }
}
