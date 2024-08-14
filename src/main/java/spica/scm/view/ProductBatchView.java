/*
 * @(#)ProductBatchView.java	1.0 Mon May 21 17:45:01 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItemInteger;
import spica.scm.domain.Manufacture;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ProductBatch;
import spica.scm.service.ProductBatchService;
import spica.scm.domain.Product;
import spica.scm.domain.ProductPacking;
import spica.scm.domain.ProductPackingDetail;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * ProductBatchView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon May 21 17:45:01 IST 2018
 */
@Named(value = "productBatchView")
@ViewScoped
public class ProductBatchView implements Serializable {

  private transient ProductBatch productBatch;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductBatch> productBatchLazyModel; 	//For lazy loading datatable.
  private transient ProductBatch[] productBatchSelected;	 //Selected Domain Array
  private transient Product product;
  private transient boolean productBatchTraded;
  private transient boolean productBatchSalesDone;

  @PostConstruct
  public void init() {
    MainView main = Jsf.getMain();
    try {
      product = (Product) Jsf.popupParentValue(Product.class);
      getProductBatch().setId(Jsf.getParameterInt("id"));
      setProductBatchTraded(ProductBatchService.isProductBatchTraded(main, getProductBatch()));
      setProductBatchSalesDone(ProductBatchService.isProductBatchSalesDone(main, productBatch));
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   * Default Constructor.
   */
  public ProductBatchView() {
    super();
  }

  /**
   * Return ProductBatch.
   *
   * @return ProductBatch.
   */
  public ProductBatch getProductBatch() {
    if (productBatch == null) {
      productBatch = new ProductBatch();
    }
    return productBatch;
  }

  /**
   * Set ProductBatch.
   *
   * @param productBatch.
   */
  public void setProductBatch(ProductBatch productBatch) {
    this.productBatch = productBatch;
  }

  public Product getProduct() {
    return product;
  }

  public boolean isProductBatchTraded() {
    return productBatchTraded;
  }

  public void setProductBatchTraded(boolean productBatchTraded) {
    this.productBatchTraded = productBatchTraded;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductBatch(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getProductBatch().reset();
          getProductBatch().setIsSaleable(SystemConstants.BATCH_SALEABLE);
          getProductBatch().setProductPackingDetailId(product.getProductPackingDetailId());

        } else if (main.isEdit() && !main.hasError()) {
          setProductBatch((ProductBatch) ProductBatchService.selectByPk(main, getProductBatch()));
//          if (!isProductBatchTraded()) {
//              setProductBatchTraded(ProductBatchService.isProductConfirmed(main, getProductBatch()));
//          }
        } else if (main.isList()) {
          loadProductBatchList(main);
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
   * Create productBatchLazyModel.
   *
   * @param main
   */
  private void loadProductBatchList(final MainView main) {
    if (productBatchLazyModel == null) {
      productBatchLazyModel = new LazyDataModel<ProductBatch>() {
        private List<ProductBatch> list;

        @Override
        public List<ProductBatch> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProductBatchService.listPaged(main);
            main.commit(productBatchLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductBatch productBatch) {
          return productBatch.getId();
        }

        @Override
        public ProductBatch getRowData(String rowKey) {
          if (list != null) {
            for (ProductBatch obj : list) {
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
    String SUB_FOLDER = "scm_product_batch/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProductBatch(MainView main) {
    return saveOrCloneProductBatch(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProductBatch(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneProductBatch(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProductBatch(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            getProductBatch().setProductId(product);
            if (getProductBatch().getExpiryDateActual() != null) {
              Calendar cal = Calendar.getInstance();
              cal.setTime(getProductBatch().getExpiryDateActual());
              int year = cal.get(Calendar.YEAR);
              if (year < 1000) {
                cal.add(Calendar.YEAR, 2000);
                getProductBatch().setExpiryDateActual(cal.getTime());
              }
            }

            if (product.getExpirySalesDays() == null) {
              getProductBatch().setExpiryDateSales(getProductBatch().getExpiryDateActual());
            } else {
              Calendar cal = Calendar.getInstance();
              cal.setTime(getProductBatch().getExpiryDateActual());
              if (product.getExpirySalesDays() != null) {
                cal.add(Calendar.DATE, -product.getExpirySalesDays());
              }
              getProductBatch().setExpiryDateSales(cal.getTime());
            }
            getProductBatch().setBatchNo(getProductBatch().getBatchNo().toUpperCase());
            ProductBatchService.insertOrUpdate(main, getProductBatch());
            break;
          case "clone":
            ProductBatchService.clone(main, getProductBatch());
            break;
        }
        productBatchLazyModel = null;
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
   * Delete one or many ProductBatch.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProductBatch(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(productBatchSelected)) {
        ProductBatchService.deleteByPkArray(main, getProductBatchSelected()); //many record delete from list
        main.commit("success.delete");
        productBatchSelected = null;
      } else {
        ProductBatchService.deleteByPk(main, getProductBatch());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProductBatch.
   *
   * @return
   */
  public LazyDataModel<ProductBatch> getProductBatchLazyModel() {
    return productBatchLazyModel;
  }

  /**
   * Return ProductBatch[].
   *
   * @return
   */
  public ProductBatch[] getProductBatchSelected() {
    return productBatchSelected;
  }

  /**
   * Set ProductBatch[].
   *
   * @param productBatchSelected
   */
  public void setProductBatchSelected(ProductBatch[] productBatchSelected) {
    this.productBatchSelected = productBatchSelected;
  }

  /**
   *
   * @return
   */
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

  public List<ProductPackingDetail> lookupProductPackingDetail() {
    if (product != null && product.getProductUnitId() != null) {
      return ScmLookupExtView.lookupProductPackingDetail(product.getProductUnitId());
    }
    return null;
  }

  public List<SelectItemInteger> getSelectItemYesNo() {
    List<SelectItemInteger> listYesNo = new ArrayList<>();
    SelectItemInteger si1 = new SelectItemInteger();
    si1.setItemLabel("Yes");
    si1.setIntValue(SystemConstants.BATCH_SALEABLE);
    listYesNo.add(si1);
    SelectItemInteger si2 = new SelectItemInteger();
    si2.setItemLabel("No");
    si2.setIntValue(SystemConstants.BATCH_BLOCKED);
    listYesNo.add(si2);
    return listYesNo;
  }

  public void productPackingSelectEvent(SelectEvent event) {
    getProductBatch().setDefaultProductPackingId(null);
  }

  public void productBatchPopupClose() {
    Jsf.popupReturn(product, getProductBatch());
  }

  /**
   *
   * @return
   */
  public List<Manufacture> lookupManufacturer() {
    List<Manufacture> list = ScmLookupExtView.manufacturerByProductAuto(getProduct());
    if (!StringUtil.isEmpty(list) && getProductBatch().getManufactureId() == null) {
      getProductBatch().setManufactureId(list.get(0));
    }
    return list;
  }

  public void productPackingReturned() {
  }

  public void productPackingOpen() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      Jsf.popupForm(FileConstant.PRODUCT_PACKING_DETAIL, getProductBatch());
    }
  }

  public boolean isProductBatchSalesDone() {
    return productBatchSalesDone;
  }

  public void setProductBatchSalesDone(boolean productBatchSalesDone) {
    this.productBatchSalesDone = productBatchSalesDone;
  }
}
