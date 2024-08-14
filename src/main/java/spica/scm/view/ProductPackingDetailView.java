/*
 * @(#)ProductPackingDetailView.java	1.0 Mon May 21 17:45:01 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ProductPackingDetail;
import spica.scm.service.ProductPackingDetailService;
import spica.scm.domain.ProductPacking;
import spica.sys.SystemConstants;
import wawo.app.faces.Jsf;

/**
 * ProductPackingDetailView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon May 21 17:45:01 IST 2018
 */
@Named(value = "productPackingDetailView")
@ViewScoped
public class ProductPackingDetailView implements Serializable {

  private transient ProductPackingDetail productPackingDetail;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductPackingDetail> productPackingDetailLazyModel; 	//For lazy loading datatable.
  private transient ProductPackingDetail[] productPackingDetailSelected;	 //Selected Domain Array
  private transient List<ProductPacking> primaryProductPackingList;
  private transient List<ProductPacking> secondaryProductPackingList;
  private transient List<ProductPacking> tertiaryProductPackingList;

  private String primaryPackTitle;
  private String secondaryPackTitle;
  private String tertiaryPackTitle;

  String tertiaryQty = "";
  String secondaryQty = "";

  /**
   * Default Constructor.
   */
  public ProductPackingDetailView() {
    super();
  }

  /**
   * Return ProductPackingDetail.
   *
   * @return ProductPackingDetail.
   */
  public ProductPackingDetail getProductPackingDetail() {
    if (productPackingDetail == null) {
      productPackingDetail = new ProductPackingDetail();
    }
    return productPackingDetail;
  }

  /**
   * Set ProductPackingDetail.
   *
   * @param productPackingDetail.
   */
  public void setProductPackingDetail(ProductPackingDetail productPackingDetail) {
    this.productPackingDetail = productPackingDetail;
  }

  /**
   * Return LazyDataModel of ProductPackingDetail.
   *
   * @return
   */
  public LazyDataModel<ProductPackingDetail> getProductPackingDetailLazyModel() {
    return productPackingDetailLazyModel;
  }

  /**
   * Return ProductPackingDetail[].
   *
   * @return
   */
  public ProductPackingDetail[] getProductPackingDetailSelected() {
    return productPackingDetailSelected;
  }

  /**
   * Set ProductPackingDetail[].
   *
   * @param productPackingDetailSelected
   */
  public void setProductPackingDetailSelected(ProductPackingDetail[] productPackingDetailSelected) {
    this.productPackingDetailSelected = productPackingDetailSelected;
  }

  public List<ProductPacking> getPrimaryProductPackingList() {
    if (StringUtil.isEmpty(primaryProductPackingList)) {
      primaryProductPackingList = ScmLookupExtView.lookupProductPacking(SystemConstants.PRODUCT_PACKING_PRIMARY);
    }
    return primaryProductPackingList;
  }

  public void setPrimaryProductPackingList(List<ProductPacking> primaryProductPackingList) {
    this.primaryProductPackingList = primaryProductPackingList;
  }

  public List<ProductPacking> getSecondaryProductPackingList() {
    if (StringUtil.isEmpty(secondaryProductPackingList)) {
      secondaryProductPackingList = ScmLookupExtView.lookupProductPacking(SystemConstants.PRODUCT_PACKING_SECONDARY);
    }
    return secondaryProductPackingList;
  }

  public void setSecondaryProductPackingList(List<ProductPacking> secondaryProductPackingList) {
    this.secondaryProductPackingList = secondaryProductPackingList;
  }

  public List<ProductPacking> getTertiaryProductPackingList() {
    if (StringUtil.isEmpty(tertiaryProductPackingList)) {
      tertiaryProductPackingList = ScmLookupExtView.lookupProductPacking(SystemConstants.PRODUCT_PACKING_TERTIARY);
    }
    return tertiaryProductPackingList;
  }

  public void setTertiaryProductPackingList(List<ProductPacking> tertiaryProductPackingList) {
    this.tertiaryProductPackingList = tertiaryProductPackingList;
  }

  public String getPrimaryPackTitle() {
    return primaryPackTitle;
  }

  public void setPrimaryPackTitle(String primaryPackTitle) {
    this.primaryPackTitle = primaryPackTitle;
  }

  public String getSecondaryPackTitle() {
    return secondaryPackTitle;
  }

  public void setSecondaryPackTitle(String secondaryPackTitle) {
    this.secondaryPackTitle = secondaryPackTitle;
  }

  public String getTertiaryPackTitle() {
    return tertiaryPackTitle;
  }

  public void setTertiaryPackTitle(String tertiaryPackTitle) {
    this.tertiaryPackTitle = tertiaryPackTitle;
  }

  public String getTertiaryQty() {
    return tertiaryQty;
  }

  public void setTertiaryQty(String tertiaryQty) {
    this.tertiaryQty = tertiaryQty;
  }

  public String getSecondaryQty() {
    return secondaryQty;
  }

  public void setSecondaryQty(String secondaryQty) {
    this.secondaryQty = secondaryQty;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductPackingDetail(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getProductPackingDetail().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setProductPackingDetail((ProductPackingDetail) ProductPackingDetailService.selectByPk(main, getProductPackingDetail()));
        } else if (main.isList()) {
          loadProductPackingDetailList(main);
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
   * Create productPackingDetailLazyModel.
   *
   * @param main
   */
  private void loadProductPackingDetailList(final MainView main) {
    if (productPackingDetailLazyModel == null) {
      productPackingDetailLazyModel = new LazyDataModel<ProductPackingDetail>() {
        private List<ProductPackingDetail> list;

        @Override
        public List<ProductPackingDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductPackingDetailService.listPaged(main);
            main.commit(productPackingDetailLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductPackingDetail productPackingDetail) {
          return productPackingDetail.getId();
        }

        @Override
        public ProductPackingDetail getRowData(String rowKey) {
          if (list != null) {
            for (ProductPackingDetail obj : list) {
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
    String SUB_FOLDER = "scm_product_packing_detail/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductPackingDetail(MainView main) {
    return saveOrCloneProductPackingDetail(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductPackingDetail(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneProductPackingDetail(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductPackingDetail(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
//            if (getProductPreset().getPackPrimary() != null || getProductPreset().getPackSecondary() != null || getProductPreset().getPackTertiary() != null) {
//              if (!productPresetValidation(main)) {
//                return null;
//              }
//            }
//            if (getProductPreset().getPrimaryDimensionGroupQty() != null && getProductPreset().getPrimaryDimensionUnitQty() == null) {
//              main.error("error.primaryDimensionUnitQty");
//              return null;
//            }
            ProductPackingDetailService.insertOrUpdate(main, getProductPackingDetail());
            break;
          case "clone":
            ProductPackingDetailService.clone(main, getProductPackingDetail());
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
   * Delete one or many ProductPackingDetail.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductPackingDetail(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(productPackingDetailSelected)) {
        ProductPackingDetailService.deleteByPkArray(main, getProductPackingDetailSelected()); //many record delete from list
        main.commit("success.delete");
        productPackingDetailSelected = null;
      } else {
        ProductPackingDetailService.deleteByPk(main, getProductPackingDetail());  //individual record delete from list or edit form
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

  public boolean isStripPrimaryPacking() {
    return getProductPackingDetail() != null && getProductPackingDetail().getPackPrimary() != null && getProductPackingDetail().getPackPrimary().getId().equals(SystemConstants.PRODUCT_PACKING_STRIP);
  }

  public boolean isTwoDimension() {
    return (getProductPackingDetail() != null && getProductPackingDetail().getPackPrimaryDimension() != null && getProductPackingDetail().getPackPrimaryDimension().equals(SystemConstants.TWO_DIMENSION));
  }

  public boolean isThreeDimension() {
    return (getProductPackingDetail() != null && getProductPackingDetail().getPackPrimaryDimension() != null && getProductPackingDetail().getPackPrimaryDimension().equals(SystemConstants.THREE_DIMENSION));
  }

  public boolean isOneDimension() {
    return (getProductPackingDetail() != null && getProductPackingDetail().getPackPrimaryDimension() != null && getProductPackingDetail().getPackPrimaryDimension().equals(SystemConstants.ONE_DIMENSION));
  }

  public boolean isSecondaryPackingRequired() {
    return (isTwoDimension() || isThreeDimension() || hasTertiaryPacking() || hasSecondaryPacking());
  }

  public boolean hasTertiaryPacking() {
    return (getProductPackingDetail() != null && getProductPackingDetail().getPackTertiary() != null && getProductPackingDetail().getPackTertiary().getId() != null);
  }

  public boolean hasSecondaryPacking() {
    return (getProductPackingDetail() != null && getProductPackingDetail().getPackSecondary() != null && getProductPackingDetail().getPackSecondary().getId() != null);
  }

  public void primaryPackSelectEvent(SelectEvent event) {
    ProductPacking productPacking = (ProductPacking) event.getObject();
    setPrimaryPackTitle(null);
    setSecondaryPackTitle(null);
    setTertiaryPackTitle(null);
    getProductPackingDetail().setPackSecondary(null);
    getProductPackingDetail().setPackTertiary(null);
    getProductPackingDetail().setPackPrimaryDimension(SystemConstants.ONE_DIMENSION);
    secondaryProductPackingList = null;
    if (productPacking != null) {
      getProductPackingDetail().setPackPrimary(productPacking);
      setPrimaryPackTitle(productPacking.getTitle());
    } else {
      getProductPackingDetail().setPackPrimary(null);
    }
    getProductPackingDetail().setTitle(getProductPackingInfo(getProductPackingDetail()));
  }

  public void secondaryPackSelectEvent(SelectEvent event) {
    ProductPacking productPacking = (ProductPacking) event.getObject();
    setSecondaryPackTitle(null);
    setTertiaryPackTitle(null);
    getProductPackingDetail().setPackTertiary(null);
    getProductPackingDetail().setPackTertiarySecondaryQty(null);
    getProductPackingDetail().setPackSecondary(null);
    getProductPackingDetail().setPackSecondaryPrimaryQty(null);
    if (productPacking != null) {
      getProductPackingDetail().setPackSecondary(productPacking);
      setSecondaryPackTitle(productPacking.getTitle());
    }
    getProductPackingDetail().setTitle(getProductPackingInfo(getProductPackingDetail()));
  }

  public void tertiaryPackSelectEvent(SelectEvent event) {
    ProductPacking productPacking = (ProductPacking) event.getObject();
    setTertiaryPackTitle("");
    if (productPacking != null) {
      getProductPackingDetail().setPackTertiary(productPacking);
      setTertiaryPackTitle(productPacking.getTitle());
    } else {
      getProductPackingDetail().setPackTertiary(null);
      getProductPackingDetail().setPackTertiarySecondaryQty(null);
    }
    updatePackingTitle();
  }

  public void packDimensionSelectEvent(SelectEvent event) {
    getProductPackingDetail().setPackTertiarySecondaryQty(null);
    getProductPackingDetail().setPackSecondaryPrimaryQty(null);
    getProductPackingDetail().setPackSecondary(null);
    getProductPackingDetail().setPackTertiary(null);
    getProductPackingDetail().setPrimaryDimensionUnitQty(null);
    getProductPackingDetail().setPrimaryDimensionGroupQty(null);
    setSecondaryPackTitle(null);
  }

  /**
   * Calculating secondary to primary quantity of product while pack dimension in 3 dimension.
   */
  public void updateSecondaryToPrimaryQty() {
    if (getProductPackingDetail().getPrimaryDimensionGroupQty() != null && getProductPackingDetail().getPrimaryDimensionUnitQty() != null) {
      getProductPackingDetail().setPackSecondaryPrimaryQty(getProductPackingDetail().getPrimaryDimensionGroupQty() * getProductPackingDetail().getPrimaryDimensionUnitQty());
    } else {
      getProductPackingDetail().setPackSecondaryPrimaryQty(null);
    }
    updatePackingTitle();
  }

  public void onChangeSecondaryPrimaryQty(ValueChangeEvent event) {
    getProductPackingDetail().setPackSecondaryPrimaryQty((Integer) event.getNewValue());
    MainView main = Jsf.getMain();
    try {
      setUnitVal(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void setUnitVal(MainView main) {
    if (getProductPackingDetail().getPackSecondaryPrimaryQty() != null && getProductPackingDetail().getPackPrimaryDimension() != null && getProductPackingDetail().getPackPrimaryDimension() == 3 && getProductPackingDetail().getPrimaryDimensionGroupQty() != null) {
      if (getProductPackingDetail().getPackSecondaryPrimaryQty() % getProductPackingDetail().getPrimaryDimensionGroupQty() == 0) {
        getProductPackingDetail().setPrimaryDimensionUnitQty(getProductPackingDetail().getPackSecondaryPrimaryQty() / getProductPackingDetail().getPrimaryDimensionGroupQty());
      } else {
        getProductPackingDetail().setPrimaryDimensionUnitQty(null);
        main.error("error.division");
      }
    }

    if (getProductPackingDetail().getPackSecondaryPrimaryQty() != null && getProductPackingDetail().getPackPrimaryDimension() != null && getProductPackingDetail().getPackPrimaryDimension() == 2) {
      getProductPackingDetail().setPrimaryDimensionUnitQty(getProductPackingDetail().getPackSecondaryPrimaryQty());
    }
  }

  private String getProductPackingInfo(ProductPackingDetail productPackingDetail) {
    StringBuilder packing = new StringBuilder();
    if (productPackingDetail != null) {
      if (productPackingDetail.getPackTertiary() != null) {
        if (productPackingDetail.getPackTertiarySecondaryQty() != null) {
          if (productPackingDetail.getPackSecondary() != null && productPackingDetail.getPackPrimary() != null && productPackingDetail.getPackSecondaryPrimaryQty() != null && productPackingDetail.getPackTertiarySecondaryQty() != null) {
            packing.append("1 ").append(productPackingDetail.getPackTertiary().getTitle()).append(" = ").append(productPackingDetail.getPackTertiarySecondaryQty()).append(" ")
                    .append(productPackingDetail.getPackSecondary().getTitle())
                    .append(" = ").append((productPackingDetail.getPackTertiarySecondaryQty() * productPackingDetail.getPackSecondaryPrimaryQty())).append(" ").append(productPackingDetail.getPackPrimary().getTitle());
          }
        }
      } else if (productPackingDetail.getPackSecondary() != null) {
        if (productPackingDetail.getPackSecondaryPrimaryQty() != null) {
          if (productPackingDetail.getPackPrimary() != null) {
            if (productPackingDetail.getPackPrimaryDimension() != null && productPackingDetail.getPackPrimaryDimension() == 3) {
              // 1 Box = 10 Strip ( 1 Box = 5 X 2 X 'n' Tab )
              packing.append("1 ").append(productPackingDetail.getPackSecondary().getTitle()).append(" = ").append(productPackingDetail.getPackSecondaryPrimaryQty()).append(" ")
                      .append(productPackingDetail.getPackPrimary().getTitle()).append(" ( 1 ").append(productPackingDetail.getPackSecondary().getTitle()).append(" = ")
                      .append(productPackingDetail.getPrimaryDimensionGroupQty()).append(" x ").append(productPackingDetail.getPrimaryDimensionUnitQty())
                      .append(" x 'n' Tab )");
            } else {
              packing.append("1 ").append(productPackingDetail.getPackSecondary().getTitle()).append(" = ").append(productPackingDetail.getPackSecondaryPrimaryQty()).append(" ")
                      .append(productPackingDetail.getPackPrimary().getTitle());
            }
          }
        }
      } else if (productPackingDetail.getPackPrimary() != null) {
        packing.append(productPackingDetail.getPackPrimary().getTitle());
      }
    }
    return packing.toString();
  }

  public void updatePackingTitle() {
    getProductPackingDetail().setTitle(getProductPackingInfo(getProductPackingDetail()));
  }

}
