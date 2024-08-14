/*
 * @(#)ProductCategoryView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.Status;
import spica.fin.domain.TaxCode;
import spica.scm.service.ProductCategoryService;
import spica.fin.service.TaxHeadService;
import spica.scm.util.CountryTaxRegimeUtil;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * ProductCategoryView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "productCategoryView")
@ViewScoped
public class ProductCategoryView implements Serializable {

  private transient ProductCategory productCategory;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductCategory> productCategoryLazyModel; 	//For lazy loading datatable.
  private transient ProductCategory[] productCategorySelected;	 //Selected Domain Array
  private transient Product product;
  private transient Company company;
  private transient TaxCode prevTaxCode;
  private transient ServiceCommodity commodity;
  private transient Boolean draft;

  @PostConstruct
  public void init() {
    product = (Product) Jsf.popupParentValue(Product.class);
    if (product != null) {
      setProductCategory(product.getProductCategoryId());
    }
    Integer id = Jsf.getParameterInt("id");
    //Fetch product category based on id or from product
  }

  /**
   * Default Constructor.
   */
  public ProductCategoryView() {
    super();
  }

  /**
   * Return ProductCategory.
   *
   * @return ProductCategory.
   */
  public ProductCategory getProductCategory() {
    if (productCategory == null) {
      productCategory = new ProductCategory();
    }
    return productCategory;
  }

  /**
   * Set ProductCategory.
   *
   * @param productCategory.
   */
  public void setProductCategory(ProductCategory productCategory) {
    this.productCategory = productCategory;
  }

  public TaxCode getPrevTaxCode() {
    return prevTaxCode;
  }

  public void setPrevTaxCode(TaxCode prevTaxCode) {
    this.prevTaxCode = prevTaxCode;
  }

  public Company getCompany() {
    company = UserRuntimeView.instance().getCompany();
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductCategory(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProductCategory().reset();
          setPrevTaxCode(null);
          setDraft(true);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProductCategory((ProductCategory) ProductCategoryService.selectByPk(main, getProductCategory()));
          prevTaxCode = getProductCategory().getPurchaseTaxCodeId();
          setDraft(getProductCategory().getStatusId().getId().intValue() == SystemConstants.DRAFT);
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProductCategoryList(main);
          setPrevTaxCode(null);
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
   * Create productCategoryLazyModel.
   *
   * @param main
   */
  private void loadProductCategoryList(final MainView main) {
    if (productCategoryLazyModel == null) {
      productCategoryLazyModel = new LazyDataModel<ProductCategory>() {
        private List<ProductCategory> list;

        @Override
        public List<ProductCategory> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (UserRuntimeView.instance().getCompany() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = ProductCategoryService.listPagedByCountryAndCompany(main, UserRuntimeView.instance().getCompany(), getCommodity());
              main.commit(productCategoryLazyModel, first, pageSize);
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
        public Object getRowKey(ProductCategory productCategory) {
          return productCategory.getId();
        }

        @Override
        public ProductCategory getRowData(String rowKey) {
          if (list != null) {
            for (ProductCategory obj : list) {
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
    String SUB_FOLDER = "scm_product_category/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductCategory(MainView main) {
    return saveOrCloneProductCategory(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductCategory(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProductCategory(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductCategory(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProductCategoryService.insertOrUpdate(main, getProductCategory());
            if (prevTaxCode != null && !prevTaxCode.getId().equals(getProductCategory().getPurchaseTaxCodeId().getId())) {
              ProductCategoryService.updateProductPriceList(main, getProductCategory());
            }
            break;
          case "clone":
            ProductCategoryService.clone(main, getProductCategory());
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
   * Delete one or many ProductCategory.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductCategory(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(productCategorySelected)) {
        ProductCategoryService.deleteByPkArray(main, getProductCategorySelected()); //many record delete from list
        main.commit("success.delete");
        productCategorySelected = null;
      } else {
        ProductCategoryService.deleteByPk(main, getProductCategory());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductCategory.
   *
   * @return
   */
  public LazyDataModel<ProductCategory> getProductCategoryLazyModel() {
    return productCategoryLazyModel;
  }

  /**
   * Return ProductCategory[].
   *
   * @return
   */
  public ProductCategory[] getProductCategorySelected() {
    return productCategorySelected;
  }

  /**
   * Set ProductCategory[].
   *
   * @param productCategorySelected
   */
  public void setProductCategorySelected(ProductCategory[] productCategorySelected) {
    this.productCategorySelected = productCategorySelected;
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
   * @param filter
   * @return
   */
  public List<ServiceCommodity> commodityAuto(String filter) {
    List<ServiceCommodity> list = ScmLookupExtView.lookupCommodityByCountryAndCompany(filter, UserRuntimeView.instance().getCompany());
    list.add(0, new ServiceCommodity(0, "All Commodity"));
    return list;
  }

  /**
   *
   * @return
   */
  public List<TaxCode> lookupGstTaxCode() {
    return ScmLookupExtView.lookupGstTaxCode(UserRuntimeView.instance().getCompany());
  }

  public void productCatClose() {
    Jsf.popupClose(product);
  }

  /**
   * Method to get the list of general status.
   *
   * @param filter
   * @return
   */
  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  /**
   * Return product category tax code list.
   *
   * @return
   */
  public List<TaxCode> selectTaxCode() {
    return ScmLookupExtView.selectProductCategoryTaxCode();
  }

  public List<TaxCode> taxCodeAuto() {
    return ScmLookupExtView.activetaxCodeAuto(UserRuntimeView.instance().getCompany(), TaxHeadService.PRODUCT_CATEGORY);
  }

  public boolean isVatRegime() {
    return CountryTaxRegimeUtil.isVatRegime(UserRuntimeView.instance().getCompany());
  }

  public boolean isGstRegime() {
    return CountryTaxRegimeUtil.isGstRegime(UserRuntimeView.instance().getCompany());
  }

  /**
   *
   * @param event
   */
  public void gstCommoditySelectEvent(SelectEvent event) {
    ServiceCommodity commodity = (ServiceCommodity) event.getObject();
    MainView main = Jsf.getMain();
    try {
      if (commodity != null) {
        if (StringUtil.isEmpty(getProductCategory().getHsnCode())) {
          getProductCategory().setHsnCode(commodity.getHsnSacCode());
        } else if (getProductCategory().getCommodityId() != null && !getProductCategory().getCommodityId().getId().equals(commodity.getId())) {
          getProductCategory().setHsnCode(commodity.getHsnSacCode());
        }

        if (getProductCategory().getPurchaseTaxCodeId() == null) {
          getProductCategory().setPurchaseTaxCodeId(commodity.getPurchaseTaxCodeId());
        } else if (getProductCategory().getCommodityId() != null && !getProductCategory().getCommodityId().getId().equals(commodity.getId())) {
          getProductCategory().setPurchaseTaxCodeId(commodity.getPurchaseTaxCodeId());
        }
        if (getProductCategory().getSalesTaxCodeId() == null) {
          getProductCategory().setSalesTaxCodeId(commodity.getSalesTaxCodeId());
        } else if (getProductCategory().getCommodityId() != null && !getProductCategory().getCommodityId().getId().equals(commodity.getId())) {
          getProductCategory().setSalesTaxCodeId(commodity.getSalesTaxCodeId());
        }
        getProductCategory().setCommodityId(commodity);
      } else {
        getProductCategory().setHsnCode(null);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param event
   */
  public void gstPurchaseTaxItemSelectEvent(SelectEvent event) {
    getProductCategory().setSalesTaxCodeId(null);
    TaxCode taxCode = (TaxCode) event.getObject();
    if (taxCode != null && getProductCategory().getSalesTaxCodeId() == null) {
      getProductCategory().setSalesTaxCodeId(taxCode);
    }
  }

  public List<TaxCode> lookupGstCessTaxCode() {
    return ScmLookupExtView.lookupGstCessTaxCode(UserRuntimeView.instance().getCompany());
  }

  public ServiceCommodity getCommodity() {
    return commodity;
  }

  public void setCommodity(ServiceCommodity commodity) {
    this.commodity = commodity;
  }

  public void commoditySelectEvent(SelectEvent event) {
    ServiceCommodity commodity = (ServiceCommodity) event.getObject();
    this.commodity = commodity;
  }

  public Boolean getDraft() {
    return draft;
  }

  public void setDraft(Boolean draft) {
    this.draft = draft;
  }

}
