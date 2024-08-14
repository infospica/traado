/*
 * @(#)VendorView.java	1.0 Fri Feb 03 19:15:30 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import wawo.app.config.ViewType;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.service.ProductEntryDetailService;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import wawo.app.faces.Jsf;

/**
 * VendorView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 03 19:15:30 IST 2017
 */
@Named(value = "productMarginView")
@ViewScoped
public class ProductMarginView implements Serializable {

  private transient ProductEntry productEntry;
  private transient List<ProductEntryDetail> productMarginValueList;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    productEntry = (ProductEntry) Jsf.popupParentValue(ProductEntry.class);
  }

  /**
   * Default Constructor.
   */
  public ProductMarginView() {
    super();
  }

  public ProductEntry getProductEntry() {
    return productEntry;
  }

  public void setProductEntry(ProductEntry productEntry) {
    this.productEntry = productEntry;
  }

  public List<ProductEntryDetail> getProductMarginValueList() {
    if (productMarginValueList == null) {
      productMarginValueList = new ArrayList<>();
    }
    return productMarginValueList;
  }

  public void setProductMarginValueList(List<ProductEntryDetail> productMarginValueList) {
    this.productMarginValueList = productMarginValueList;
  }

  public double getProductQuantity(ProductEntryDetail productEntryDetail) {
    double productQuantity = productEntryDetail.getProductQuantity();
    if (productEntryDetail.getProductFreeQtySchemeId() == null && productEntryDetail.getProductQuantityFree() != null && productEntryDetail.getProductQuantityFree() > 0) {
      productQuantity += productEntryDetail.getProductQuantityFree();
    }
    productQuantity -= productEntryDetail.getProductQuantityShortage() == null ? 0 : productEntryDetail.getProductQuantityShortage();
    return productQuantity;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProductMargin(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
        } else if (ViewType.list.toString().equals(viewType)) {
          if (getProductMarginValueList().isEmpty()) {
            if (getProductEntry() != null && getProductEntry().getId() != null) {
              setProductMarginValueList(ProductEntryDetailService.selectProductEntryDetailByProductEntryId(main, getProductEntry(), null));
            }
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
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void productMarginPopupClose() {
    Jsf.popupReturn(getProductEntry(), null);
  }

  public double getMarginValueSummery() {
    double marginValueSummery = 0;
    for (ProductEntryDetail productEntryDetail : getProductMarginValueList()) {
      if (productEntryDetail.getMarginValueDeviation() != null) {
        marginValueSummery += productEntryDetail.getMarginValueDeviation();
      }
    }
    marginValueSummery = MathUtil.roundOff(marginValueSummery, 4);
    return marginValueSummery;
  }

  public double getVendorMarginValueSummery() {
    double vendorMarginValueSummery = 0;
    for (ProductEntryDetail productEntryDetail : getProductMarginValueList()) {
      if (productEntryDetail.getVendorMarginValue() != null) {
        vendorMarginValueSummery += productEntryDetail.getVendorMarginValue();
      }
    }
    vendorMarginValueSummery = MathUtil.roundOff(vendorMarginValueSummery, 4);
    return vendorMarginValueSummery;
  }

  public double getMarginPercentage(ProductEntryDetail productEntryDetail) {
    double marginPercentage = 0;
    if (productEntryDetail != null) {
      marginPercentage = MathUtil.roundOff(ProductUtil.getSsMargin(productEntryDetail.getPtsSsRateDerivationCriterion(), productEntryDetail.getActualSellingPriceDerived(), productEntryDetail.getValueRatePerProdPieceDer()), 2);
      //marginPercentage = marginPercentage - productEntryDetail.getMarginPercentage();
    }
    return marginPercentage;
  }

  public String getProductMargingPercentage(ProductEntryDetail productEntryDetail) {
    String expectedMargingPercentage = "";
    if (productEntryDetail != null && productEntryDetail.getMarginPercentage() != null) {
      expectedMargingPercentage = productEntryDetail.getMarginPercentage().toString();
      if (productEntryDetail.getProductEntryId() != null && productEntryDetail.getProductEntryId().getVendorReservePercentage() != null) {
        expectedMargingPercentage += " + " + productEntryDetail.getProductEntryId().getVendorReservePercentage();
      }
    }
    return expectedMargingPercentage;
  }
}
